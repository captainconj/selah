(ns selah.space.export
  "Point cloud export for the Torah 4D space.

   Takes float[] arrays from project, writes files:
   - JSON (Three.js / web)
   - Binary float32 (WebGL BufferGeometry)
   - ASCII PLY (MeshLab / Blender)"
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:import [java.io DataOutputStream BufferedOutputStream FileOutputStream]
           [java.nio ByteBuffer ByteOrder]))

;; ── JSON export ────────────────────────────────────────────

(defn write-json!
  "Write point cloud as JSON array of [x,y,z,r,g,b] tuples.
   pos-floats: float[] of [x y z ...] (3 per vertex).
   color-floats: float[] of [r g b ...] (3 per vertex)."
  [path ^floats pos-floats ^floats color-floats]
  (let [n (/ (alength pos-floats) 3)
        points (mapv (fn [i]
                       (let [p (* 3 i)]
                         [(aget pos-floats p)
                          (aget pos-floats (+ p 1))
                          (aget pos-floats (+ p 2))
                          (aget color-floats p)
                          (aget color-floats (+ p 1))
                          (aget color-floats (+ p 2))]))
                     (range n))]
    (io/make-parents path)
    (spit path (json/write-str points))
    {:path path :vertices n :format :json}))

;; ── Binary export ──────────────────────────────────────────

(defn write-binary!
  "Write point cloud as raw float32 with header.
   Layout: [uint32 count] [uint32 floats-per-vertex] [float32 × count × fpv]
   pos-floats: float[] of [x y z ...] (3 per vertex).
   color-floats: float[] of [r g b ...] (3 per vertex)."
  [path ^floats pos-floats ^floats color-floats]
  (let [n (/ (alength pos-floats) 3)
        fpv 6  ;; x y z r g b
        buf (ByteBuffer/allocate (+ 8 (* n fpv 4)))
        _ (.order buf ByteOrder/LITTLE_ENDIAN)
        _ (.putInt buf (int n))
        _ (.putInt buf (int fpv))]
    (dotimes [i n]
      (let [p (* 3 i)]
        (.putFloat buf (aget pos-floats p))
        (.putFloat buf (aget pos-floats (+ p 1)))
        (.putFloat buf (aget pos-floats (+ p 2)))
        (.putFloat buf (aget color-floats p))
        (.putFloat buf (aget color-floats (+ p 1)))
        (.putFloat buf (aget color-floats (+ p 2)))))
    (io/make-parents path)
    (with-open [out (DataOutputStream.
                     (BufferedOutputStream.
                      (FileOutputStream. (io/file path))))]
      (.write out (.array buf)))
    {:path path :vertices n :bytes (.capacity buf) :format :binary}))

;; ── PLY export ─────────────────────────────────────────────

(defn write-ply!
  "Write point cloud as ASCII PLY (MeshLab / Blender compatible).
   pos-floats: float[] of [x y z ...] (3 per vertex).
   color-floats: float[] of [r g b ...] (3 per vertex, 0-1 range)."
  [path ^floats pos-floats ^floats color-floats]
  (let [n (/ (alength pos-floats) 3)]
    (io/make-parents path)
    (with-open [w (io/writer path)]
      ;; PLY header
      (.write w "ply\n")
      (.write w "format ascii 1.0\n")
      (.write w (str "element vertex " n "\n"))
      (.write w "property float x\n")
      (.write w "property float y\n")
      (.write w "property float z\n")
      (.write w "property uchar red\n")
      (.write w "property uchar green\n")
      (.write w "property uchar blue\n")
      (.write w "end_header\n")
      ;; Vertex data
      (dotimes [i n]
        (let [p (* 3 i)
              r (int (* 255 (aget color-floats p)))
              g (int (* 255 (aget color-floats (+ p 1))))
              b (int (* 255 (aget color-floats (+ p 2))))]
          (.write w (format "%.6f %.6f %.6f %d %d %d\n"
                            (aget pos-floats p)
                            (aget pos-floats (+ p 1))
                            (aget pos-floats (+ p 2))
                            r g b)))))
    {:path path :vertices n :format :ply}))

;; ── Framed export ──────────────────────────────────────────

(defn write-frames!
  "Write temporal frames as numbered files.
   frames: vec of {:t n :positions int[]} from project/frames.
   writer-fn: one of write-json!, write-binary!, write-ply!.
   project-fn: fn [int[] axes] → float[].
   color-fn: fn [space int[]] → float[].
   axes: spatial axes for projection."
  [dir frames writer-fn project-fn color-fn space axes & {:keys [ext] :or {ext "ply"}}]
  (let []
    (io/make-parents (str dir "/x"))
    (mapv (fn [{:keys [t positions]}]
            (let [pts (project-fn positions axes)
                  cols (color-fn space positions)
                  path (format "%s/frame_%03d.%s" dir t ext)]
              (writer-fn path pts cols)))
          frames)))

;; ── REPL ───────────────────────────────────────────────────

(comment
  (require '[selah.space.coords :as c]
           '[selah.space.project :as p])

  (def s (c/space))

  ;; Export center seventh as PLY
  (def center (c/hyperplane :a 3))
  (def pts (p/project-3d center [:b :c :d]))
  (def cols (p/color-by-letter s center))
  (write-ply! "data/export/center-seventh.ply" pts cols)

  ;; Export full Torah as binary
  (def all (int-array (range c/total-letters)))
  (def pts-all (p/project-3d all [:b :c :d]))
  (def cols-all (p/color-by-book s all))
  (write-binary! "data/export/torah-by-book.bin" pts-all cols-all)

  ;; Export 7 temporal frames as PLY
  (def fs (p/frames all [:b :c :d] :a))
  (write-frames! "data/export/frames" fs write-ply!
                 #(p/project-3d %1 %2) #(p/color-by-letter %1 %2)
                 s [:b :c :d])
  )
