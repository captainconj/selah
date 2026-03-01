(ns selah.space.project
  "Projection and color mapping for the 4D Torah space.

   Takes int[] position arrays from coords, produces float[] arrays
   for export. Everything is lookup tables and packed arrays."
  (:require [selah.space.coords :as c]))

;; ── Projection ─────────────────────────────────────────────
;;
;; Maps positions to spatial coordinates by selecting which
;; coordinate axes become x, y, z. Normalized to [0..1].

(def ^:private axis-max
  {:a (dec c/dim-a)    ;; 6
   :b (dec c/dim-b)    ;; 49
   :c (dec c/dim-c)    ;; 12
   :d (dec c/dim-d)})  ;; 66

(defn- coord-component
  "Extract a single coordinate component from a position."
  ^double [^long pos axis]
  (let [coord (c/idx->coord pos)]
    (case axis
      :a (double (aget coord 0))
      :b (double (aget coord 1))
      :c (double (aget coord 2))
      :d (double (aget coord 3)))))

(defn- normalize ^double [^double v ^double mx]
  (if (zero? mx) 0.0 (/ v mx)))

(defn project-3d
  "Project positions to 3D. axes = [ax ay az].
   Returns float[] of [x y z x y z ...] normalized to [0..1]."
  ^floats [^ints positions axes]
  (let [[ax ay az] axes
        mx (double (get axis-max ax))
        my (double (get axis-max ay))
        mz (double (get axis-max az))
        n (alength positions)
        out (float-array (* 3 n))]
    (dotimes [i n]
      (let [pos (aget positions i)
            coord (c/idx->coord pos)
            ;; Extract the requested axes
            vx (case ax :a (aget coord 0) :b (aget coord 1)
                     :c (aget coord 2) :d (aget coord 3))
            vy (case ay :a (aget coord 0) :b (aget coord 1)
                     :c (aget coord 2) :d (aget coord 3))
            vz (case az :a (aget coord 0) :b (aget coord 1)
                     :c (aget coord 2) :d (aget coord 3))
            j (* 3 i)]
        (aset out j       (float (normalize (double vx) mx)))
        (aset out (+ j 1) (float (normalize (double vy) my)))
        (aset out (+ j 2) (float (normalize (double vz) mz)))))
    out))

(defn project-2d
  "Project positions to 2D. axes = [ax ay].
   Returns float[] of [x y x y ...] normalized to [0..1]."
  ^floats [^ints positions axes]
  (let [[ax ay] axes
        mx (double (get axis-max ax))
        my (double (get axis-max ay))
        n (alength positions)
        out (float-array (* 2 n))]
    (dotimes [i n]
      (let [pos (aget positions i)
            coord (c/idx->coord pos)
            vx (case ax :a (aget coord 0) :b (aget coord 1)
                     :c (aget coord 2) :d (aget coord 3))
            vy (case ay :a (aget coord 0) :b (aget coord 1)
                     :c (aget coord 2) :d (aget coord 3))
            j (* 2 i)]
        (aset out j       (float (normalize (double vx) mx)))
        (aset out (+ j 1) (float (normalize (double vy) my)))))
    out))

;; ── Temporal ───────────────────────────────────────────────
;;
;; Split positions into frames by one coordinate axis.

(defn frames
  "Split positions into frames along a temporal axis.
   spatial = [ax ay az] for 3D (or [ax ay] for 2D).
   temporal = axis keyword (:a :b :c :d).
   Returns vec of {:t n :positions int[]}."
  [^ints positions spatial temporal]
  (let [dim (get {:a c/dim-a :b c/dim-b :c c/dim-c :d c/dim-d} temporal)
        ;; Group positions by their temporal coordinate
        buckets (make-array (Class/forName "[I") dim)]
    ;; First pass: count per bucket
    (let [counts (int-array dim)]
      (dotimes [i (alength positions)]
        (let [pos (aget positions i)
              coord (c/idx->coord pos)
              t (case temporal
                  :a (aget coord 0) :b (aget coord 1)
                  :c (aget coord 2) :d (aget coord 3))]
          (aset counts (int t) (inc (aget counts (int t))))))
      ;; Allocate buckets
      (dotimes [t dim]
        (aset buckets t (int-array (aget counts t))))
      ;; Second pass: fill
      (let [offsets (int-array dim)]
        (dotimes [i (alength positions)]
          (let [pos (aget positions i)
                coord (c/idx->coord pos)
                t (int (case temporal
                         :a (aget coord 0) :b (aget coord 1)
                         :c (aget coord 2) :d (aget coord 3)))
                off (aget offsets t)]
            (aset ^ints (aget buckets t) off pos)
            (aset offsets t (inc off))))))
    ;; Return as vec of maps
    (mapv (fn [t]
            {:t t :positions (aget buckets t)})
          (range dim))))

;; ── Color palettes ─────────────────────────────────────────
;;
;; Precomputed lookup tables. Each palette is a flat float[]
;; of [r g b r g b ...] with one entry per symbol/category.

(defn- hue->rgb
  "HSV to RGB with full saturation and value. h in [0..1]."
  [^double h]
  (let [h6 (* h 6.0)
        i (int (Math/floor h6))
        f (- h6 i)
        q (- 1.0 f)
        t f]
    (case (mod i 6)
      0 [1.0 t   0.0]
      1 [q   1.0 0.0]
      2 [0.0 1.0 t]
      3 [0.0 q   1.0]
      4 [t   0.0 1.0]
      5 [1.0 0.0 q])))

(def ^:private palette-27
  "27 distinct hues for the 27 Hebrew symbols."
  (let [colors (mapv #(hue->rgb (/ (double %) 27.0)) (range 27))]
    (float-array (mapcat identity colors))))

(def ^:private palette-book
  "5 colors for the 5 books."
  (float-array [0.8 0.2 0.2    ;; Genesis — red
                0.2 0.7 0.2    ;; Exodus — green
                0.9 0.9 0.2    ;; Leviticus — gold
                0.2 0.4 0.9    ;; Numbers — blue
                0.7 0.2 0.8])) ;; Deuteronomy — purple

(def ^:private palette-day
  "7 colors for the 7 days/divisions."
  (float-array [1.0 0.0 0.0    ;; day 0 — red
                1.0 0.5 0.0    ;; day 1 — orange
                1.0 1.0 0.0    ;; day 2 — yellow
                0.0 1.0 0.0    ;; day 3 — green
                0.0 0.5 1.0    ;; day 4 — blue
                0.3 0.0 1.0    ;; day 5 — indigo
                0.6 0.0 0.8])) ;; day 6 — violet

(def ^:private gv-colors
  "27 colors mapping gematria value to a warm→cool spectrum.
   Indexed by the 27-symbol byte index."
  (let [;; Map each symbol's gematria value to a hue
        ;; Low values (1-9) = warm, high values (100-400) = cool
        max-gv 400.0
        colors (mapv (fn [gv]
                       (let [t (/ (Math/log (inc (double gv)))
                                  (Math/log (inc max-gv)))]
                         (hue->rgb (* 0.7 t)))) ;; 0.0=red to 0.7=violet
                     c/letter-gv)]
    (float-array (mapcat identity colors))))

;; ── Color functions ────────────────────────────────────────

(defn color-by-letter
  "Color each position by its letter (27 hues).
   Returns float[] of [r g b r g b ...]."
  ^floats [s ^ints positions]
  (let [n (alength positions)
        stream ^bytes (:stream s)
        out (float-array (* 3 n))]
    (dotimes [i n]
      (let [sym (aget stream (aget positions i))
            j (* 3 i)
            p (* 3 sym)]
        (aset out j       (aget palette-27 p))
        (aset out (+ j 1) (aget palette-27 (+ p 1)))
        (aset out (+ j 2) (aget palette-27 (+ p 2)))))
    out))

(defn color-by-gematria
  "Color each position by its gematria value (log scale, warm→cool).
   Returns float[] of [r g b r g b ...]."
  ^floats [s ^ints positions]
  (let [n (alength positions)
        stream ^bytes (:stream s)
        out (float-array (* 3 n))]
    (dotimes [i n]
      (let [sym (aget stream (aget positions i))
            j (* 3 i)
            p (* 3 sym)]
        (aset out j       (aget gv-colors p))
        (aset out (+ j 1) (aget gv-colors (+ p 1)))
        (aset out (+ j 2) (aget gv-colors (+ p 2)))))
    out))

(defn color-by-book
  "Color each position by which book it belongs to (5 colors).
   Returns float[] of [r g b r g b ...]."
  ^floats [s ^ints positions]
  (let [n (alength positions)
        verses ^ints (:verses s)
        vrefs (:verse-ref s)
        book-idx {"Genesis" 0 "Exodus" 1 "Leviticus" 2
                  "Numbers" 3 "Deuteronomy" 4}
        out (float-array (* 3 n))]
    (dotimes [i n]
      (let [vid (aget verses (aget positions i))
            bi (get book-idx (:book (nth vrefs vid)))
            j (* 3 i)
            p (* 3 bi)]
        (aset out j       (aget palette-book p))
        (aset out (+ j 1) (aget palette-book (+ p 1)))
        (aset out (+ j 2) (aget palette-book (+ p 2)))))
    out))

(defn color-by-day
  "Color each position by its a-coordinate (7 rainbow colors).
   Returns float[] of [r g b r g b ...]."
  ^floats [^ints positions]
  (let [n (alength positions)
        out (float-array (* 3 n))]
    (dotimes [i n]
      (let [coord (c/idx->coord (aget positions i))
            a (aget coord 0)
            j (* 3 i)
            p (* 3 (int a))]
        (aset out j       (aget palette-day p))
        (aset out (+ j 1) (aget palette-day (+ p 1)))
        (aset out (+ j 2) (aget palette-day (+ p 2)))))
    out))

;; ── REPL ───────────────────────────────────────────────────

(comment
  (def s (c/space))

  ;; Project center seventh into 3D (b, c, d)
  (def center (c/hyperplane :a 3))
  (def pts (project-3d center [:b :c :d]))
  (alength pts) ;; 43550 × 3 = 130650

  ;; Color by letter
  (def cols (color-by-letter s center))
  (alength cols) ;; 43550 × 3 = 130650

  ;; Split into 7 temporal frames
  (def all (int-array (range c/total-letters)))
  (def fs (frames all [:b :c :d] :a))
  (count fs) ;=> 7
  (:t (first fs)) ;=> 0
  (alength (:positions (first fs))) ;=> 43550
  )
