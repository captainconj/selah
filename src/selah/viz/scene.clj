(ns selah.viz.scene
  "Scene state and pure transforms for the Torah 4D space visualizer.

   No rendering code — just the state atom and functions that
   manipulate it. This is the cljc candidate."
  (:require [selah.space.coords :as c]
            [selah.space.project :as proj]))

;; ── Axis dimensions ────────────────────────────────────────

(def axis-dims {:a 7 :b 50 :c 13 :d 67})
(def axis-labels {:a "a (7 · days)" :b "b (50 · jubilee)"
                  :c "c (13 · love)" :d "d (67 · understanding)"})

;; ── State ──────────────────────────────────────────────────
;;
;; Ortho-first: start with a 2D slice (c × d) at a=3, b=0.
;; In ortho mode the camera auto-fits. No flight controls.

(defonce ^:dynamic *state*
  (atom {:camera    {:eye    [0.5 0.5 1.0]
                     :target [0.5 0.5 0.0]
                     :up     [0.0 1.0 0.0]
                     :ortho? true
                     :fov    60.0
                     :pan    [0.0 0.0]    ;; ortho pan offset
                     :zoom   1.3}         ;; ortho zoom
         :axes      {:x :d :y :c :z :b}
         :slice     {:a 3 :b 0 :c nil :d nil}
         :highlight #{}
         :boxes     []
         :display   :grid          ;; :grid (uniform) or :read (fill width)
         :palette   :letter
         :dirty?    true
         :running?  false
         :window    nil
         :pick-request nil   ;; [screen-x screen-y] from mouse click
         :selection  nil}))

(defn state  [] @*state*)
(defn state-atom [] *state*)

;; ── Derived info ───────────────────────────────────────────

(defn free-axes
  "Which axes are currently free (not sliced)."
  []
  (vec (keep (fn [[k v]] (when (nil? v) k))
             (:slice @*state*))))

(defn fixed-axes
  "Which axes are fixed, with their values."
  []
  (into {} (filter (fn [[_ v]] (some? v))
                   (:slice @*state*))))

(defn info-lines
  "State as separate lines for side panel overlay."
  []
  (let [s @*state*
        mode (if (get-in s [:camera :ortho?]) "Ortho" "3D")
        disp (name (get s :display :grid))
        pal (name (:palette s))
        fa (free-axes)
        fx (fixed-axes)
        free-strs (map (fn [k] (str (name k) " (0-" (dec (get axis-dims k)) ")")) fa)
        fixed-strs (map (fn [[k v]] (str (name k) "=" v)) (sort-by key fx))]
    (concat
     [(str mode "  " disp "  " pal)
      ""
      "Free:"]
     (map (fn [s] (str "  " s)) free-strs)
     [""
      "Fixed:"]
     (map (fn [s] (str "  " s)) fixed-strs))))

(defn info-string
  "Single-line state for console."
  []
  (clojure.string/join " | " (remove empty? (info-lines))))

;; ── Camera ─────────────────────────────────────────────────

(defn look-at
  "Set camera eye and target directly."
  [eye target]
  (swap! *state* update :camera assoc
         :eye (vec eye) :target (vec target)))

(defn reset-ortho-camera!
  "Reset ortho camera to auto-fit the current slice."
  []
  (swap! *state* update :camera assoc
         :eye [0.5 0.5 1.0] :target [0.5 0.5 0.0]
         :up [0.0 1.0 0.0] :pan [0.0 0.0] :zoom 1.3))

(defn- coord-4d->3d
  "Project a 4D coordinate [a b c d] to normalized 3D [x y z]
   using the current axis mapping."
  [[a b c d]]
  (let [{:keys [x y z]} (:axes @*state*)
        maxes {:a 6.0 :b 49.0 :c 12.0 :d 66.0}
        pick (fn [axis] (case axis :a a :b b :c c :d d))]
    [(/ (double (pick x)) (get maxes x))
     (/ (double (pick y)) (get maxes y))
     (/ (double (pick z)) (get maxes z))]))

(defn fly-to
  "Set camera target to the 3D projection of a 4D coordinate."
  [[a b c d]]
  (let [[tx ty tz] (coord-4d->3d [a b c d])]
    (swap! *state* update :camera assoc
           :target [tx ty tz]
           :fly-start (System/nanoTime)
           :fly-from  (get-in @*state* [:camera :eye])
           :fly-to    [(+ tx 0.0) (+ ty 0.0) (+ tz 0.5)])))

;; ── Axes ───────────────────────────────────────────────────

(defn set-axes
  "Map 4D axes to screen x, y, z."
  [x y z]
  (swap! *state* assoc :axes {:x x :y y :z z} :dirty? true))

;; ── Slicing ────────────────────────────────────────────────

(defn fix-axis
  "Fix an axis to a value — slices the visible cloud."
  [axis val]
  (let [dim (get axis-dims axis)]
    (swap! *state* #(-> %
                        (assoc-in [:slice axis] (mod (int val) dim))
                        (assoc :dirty? true)))))

(defn free-axis
  "Release an axis — show all values."
  [axis]
  (swap! *state* #(-> % (assoc-in [:slice axis] nil) (assoc :dirty? true))))

(defn step-axis
  "Step a fixed axis by delta. Only works on currently fixed axes."
  [axis delta]
  (let [current (get-in @*state* [:slice axis])]
    (when current
      (let [dim (get axis-dims axis)]
        (fix-axis axis (mod (+ current delta) dim))))))

;; ── Ortho/3D toggle ──────────────────────────────────────

(defn toggle-ortho
  "Switch between perspective and orthographic projection.
   Ortho: 2 free axes (2D slice). 3D: 3 free axes (one fixed)."
  []
  (let [going-ortho? (not (get-in @*state* [:camera :ortho?]))]
    (swap! *state* update-in [:camera :ortho?] not)
    (if going-ortho?
      ;; Going to ortho: fix a second axis for 2D view
      (let [fa (vec (keep (fn [[k v]] (when (nil? v) k))
                          (:slice @*state*)))]
        (when (> (count fa) 2)
          (let [axis (first (sort fa))
                mid (quot (get axis-dims axis) 2)]
            (fix-axis axis mid)))
        (reset-ortho-camera!))
      ;; Going to 3D: free one axis for 3 dimensions
      (let [fx (into {} (filter (fn [[_ v]] (some? v)) (:slice @*state*)))]
        (when (> (count fx) 1)
          (let [axis (last (sort (keys fx)))]
            (free-axis axis)))
        (let [fa (vec (sort (keep (fn [[k v]] (when (nil? v) k))
                                  (:slice @*state*))))]
          (when (>= (count fa) 3)
            (set-axes (nth fa 2) (nth fa 1) (nth fa 0))))
        (look-at [0.5 0.5 2.5] [0.5 0.5 0.0])))))

(defn set-ortho [on?]
  (swap! *state* assoc-in [:camera :ortho?] (boolean on?))
  (when on? (reset-ortho-camera!)))

(defn cycle-slice-view!
  "Cycle through common 2D views:
   c×d (a=3,b=0) → b×d (a=3,c=6) → b×c (a=3,d=33) → a×d (b=25,c=6) → ..."
  []
  (let [views [{:free [:c :d] :fixed {:a 3 :b 0}   :axes {:x :d :y :c :z :b}}
               {:free [:b :d] :fixed {:a 3 :c 6}   :axes {:x :d :y :b :z :c}}
               {:free [:b :c] :fixed {:a 3 :d 33}  :axes {:x :c :y :b :z :d}}
               {:free [:a :d] :fixed {:b 25 :c 6}  :axes {:x :d :y :a :z :c}}
               {:free [:a :c] :fixed {:b 25 :d 33} :axes {:x :c :y :a :z :d}}
               {:free [:a :b] :fixed {:c 6 :d 33}  :axes {:x :b :y :a :z :d}}]
        current-free (set (free-axes))
        idx (or (first (keep-indexed
                        (fn [i v] (when (= (set (:free v)) current-free) i))
                        views))
                -1)
        next-view (nth views (mod (inc idx) (count views)))]
    (swap! *state* #(-> %
                        (assoc :slice (merge {:a nil :b nil :c nil :d nil}
                                             (:fixed next-view)))
                        (assoc :axes (:axes next-view))
                        (assoc :dirty? true)))
    (reset-ortho-camera!)
    (let [fa (free-axes)]
      (println (str "[viz] View: " (name (first fa)) " × " (name (second fa)))))))

;; ── Palette ────────────────────────────────────────────────

(defn set-palette [p]
  (swap! *state* assoc :palette p :dirty? true))

(defn toggle-display
  "Toggle between :grid (uniform spacing) and :read (fill width) modes."
  []
  (swap! *state* update :display #(if (= % :grid) :read :grid)))

;; ── Highlighting ───────────────────────────────────────────

(defn highlight-positions
  "Add position indices to the highlight set."
  [positions]
  (swap! *state* update :highlight into positions))

(defn highlight-word
  "Find all a-fiber positions containing a Hebrew word, highlight them."
  [word]
  (let [hits (c/preimage word)]
    (when (seq hits)
      (let [positions (for [{:keys [b c d]} hits
                            a (range (c/dim-a))]
                        (c/coord->idx a b c d))]
        (highlight-positions positions)))
    (count hits)))

(defn highlight-letter
  "Highlight all positions containing a specific Hebrew letter.
   Returns the number of positions highlighted."
  [letter]
  (let [s (c/space)
        stream ^bytes (:stream s)
        idx (get c/char->idx letter)]
    (when idx
      (let [byte-idx (byte idx)
            positions (java.util.ArrayList.)]
        (dotimes [i c/total-letters]
          (when (== (aget stream i) byte-idx)
            (.add positions (int i))))
        (swap! *state* update :highlight into positions)
        (.size positions)))))

(defn clear-highlights []
  (swap! *state* assoc :highlight #{}))

;; ── Boxes ──────────────────────────────────────────────────

(defn show-box
  "Add a wireframe box overlay."
  [label min-coord max-coord color]
  (swap! *state* update :boxes conj
         {:label label :min (vec min-coord) :max (vec max-coord)
          :color (vec color)}))

(defn clear-boxes []
  (swap! *state* assoc :boxes []))

;; ── Precomputed data ──────────────────────────────────────
;;
;; All coordinates, palettes, and letter indices are computed once
;; at startup. Any state change (axis toggle, slice step, palette
;; cycle) becomes pure array indexing — no division, no allocation.

(defonce ^:private precomputed (atom nil))
(defonce ^:private slab-cache (atom {}))

(defn set-precomputed!
  "Store precomputed data from the loader thread."
  [data]
  (reset! precomputed data)
  (reset! slab-cache {}))
(def ^:const ^:private max-slab-cache 64)

(defn precompute!
  "Precompute all coordinates and color palettes for all 304,850 positions.
   Called once at startup. ~27 MB total."
  []
  (let [s (c/space)
        n (int c/total-letters)
        ;; All coordinates as flat int array [a b c d a b c d ...]
        coords (int-array (* n 4))
        _ (dotimes [i n]
            (let [c4 (c/idx->coord i)
                  j (* i 4)]
              (aset coords j       (int (aget c4 0)))
              (aset coords (+ j 1) (int (aget c4 1)))
              (aset coords (+ j 2) (int (aget c4 2)))
              (aset coords (+ j 3) (int (aget c4 3)))))
        ;; All palette colors (each position, all 4 palettes)
        all-pos (int-array n)
        _ (dotimes [i n] (aset all-pos i i))
        pal-letter   (proj/color-by-letter s all-pos)
        pal-gematria (proj/color-by-gematria s all-pos)
        pal-book     (proj/color-by-book s all-pos)
        pal-day      (proj/color-by-day all-pos)
        ;; Letter indices for texture atlas
        stream ^bytes (:stream s)
        let-idx (float-array n)
        _ (dotimes [i n]
            (aset let-idx i (float (aget stream i))))]
    (reset! precomputed
            {:coords   coords
             :palettes {:letter   pal-letter
                        :gematria pal-gematria
                        :book     pal-book
                        :day      pal-day}
             :letters  let-idx})
    (reset! slab-cache {})
    (println (str "[viz] Precomputed " n " positions"))))

;; ── Position computation ───────────────────────────────────

(defonce ^:private all-positions
  (delay (let [a (int-array c/total-letters)]
           (dotimes [i c/total-letters] (aset a i i))
           a)))

(defn visible-positions
  "Compute the int[] of positions visible under current slice.
   If no axis is fixed, returns all 304,850 (cached).
   Slabs are cached (up to 64 entries) for instant revisits."
  []
  (let [slice (:slice @*state*)
        fixed (into {} (filter (fn [[_ v]] (some? v)) slice))]
    (if (empty? fixed)
      @all-positions
      (or (get @slab-cache fixed)
          (let [s (c/slab fixed)]
            (swap! slab-cache
                   (fn [cache]
                     (let [c (if (>= (count cache) max-slab-cache)
                               {} cache)]
                       (assoc c fixed s))))
            s)))))

(defn position-data
  "Compute positions float[] and colors float[] for current state.
   Uses precomputed arrays when available — pure array indexing, no division.
   Returns {:positions float[] :colors float[] :count int :letters float[]}."
  []
  (let [pre @precomputed
        pos (visible-positions)
        n (alength pos)]
    (if pre
      ;; Fast path: precomputed arrays
      (let [{:keys [x y z]} (:axes @*state*)
            coords ^ints (:coords pre)
            mx (double (case x :a 6.0 :b 49.0 :c 12.0 :d 66.0))
            my (double (case y :a 6.0 :b 49.0 :c 12.0 :d 66.0))
            mz (double (case z :a 6.0 :b 49.0 :c 12.0 :d 66.0))
            ax-idx (int (case x :a 0 :b 1 :c 2 :d 3))
            ay-idx (int (case y :a 0 :b 1 :c 2 :d 3))
            az-idx (int (case z :a 0 :b 1 :c 2 :d 3))
            pts (float-array (* 3 n))
            _ (dotimes [i n]
                (let [p (aget pos i)
                      base (* p 4)
                      j (* 3 i)]
                  (aset pts j       (float (/ (double (aget coords (+ base ax-idx))) mx)))
                  (aset pts (+ j 1) (float (/ (double (aget coords (+ base ay-idx))) my)))
                  (aset pts (+ j 2) (float (/ (double (aget coords (+ base az-idx))) mz)))))
            ;; Colors from cached palette
            palette (:palette @*state*)
            src-colors ^floats (get-in pre [:palettes palette])
            cols (float-array (* 3 n))
            _ (dotimes [i n]
                (let [p (aget pos i)
                      src-base (* p 3)
                      dst-base (* i 3)]
                  (aset cols dst-base       (aget src-colors src-base))
                  (aset cols (+ dst-base 1) (aget src-colors (+ src-base 1)))
                  (aset cols (+ dst-base 2) (aget src-colors (+ src-base 2)))))
            ;; Letters from cache
            src-letters ^floats (:letters pre)
            letter-idx (float-array n)
            _ (dotimes [i n]
                (aset letter-idx i (aget src-letters (aget pos i))))]
        {:positions pts
         :colors    cols
         :letters   letter-idx
         :count     n
         :indices   pos})
      ;; Fallback: compute from scratch (before precompute! runs)
      (let [s (c/space)
            {:keys [x y z]} (:axes @*state*)
            pts (proj/project-3d pos [x y z])
            palette (:palette @*state*)
            cols (case palette
                   :letter   (proj/color-by-letter s pos)
                   :gematria (proj/color-by-gematria s pos)
                   :book     (proj/color-by-book s pos)
                   :day      (proj/color-by-day pos))
            stream ^bytes (:stream s)
            letter-idx (float-array n)]
        (dotimes [i n]
          (aset letter-idx i (float (aget stream (aget pos i)))))
        {:positions pts
         :colors    cols
         :letters   letter-idx
         :count     n
         :indices   pos}))))

(defn request-pick!
  "Request a pick at screen coordinates. Processed by the render loop."
  [screen-x screen-y]
  (swap! *state* assoc :pick-request [(double screen-x) (double screen-y)]))

(defn select!
  "Set the selected position index and highlight it."
  [pos-idx]
  (swap! *state* assoc :selection pos-idx)
  (when pos-idx
    (swap! *state* update :highlight conj pos-idx)
    (swap! *state* assoc :dirty? true)
    (let [info (c/describe pos-idx)]
      (println (str "[viz] Selected: " (:letter info)
                    " " (:verse info)
                    " coord=" (:coord info)
                    " gv=" (:gematria info))))))

(defn clear-pick-request! []
  (swap! *state* assoc :pick-request nil))

(defn mark-clean []
  (swap! *state* assoc :dirty? false))

(defn dirty? [] (:dirty? @*state*))
