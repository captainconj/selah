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
         :palette   :letter
         :dirty?    true
         :running?  false
         :window    nil}))

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
        pal (name (:palette s))
        fa (free-axes)
        fx (fixed-axes)
        free-strs (map (fn [k] (str (name k) " (0-" (dec (get axis-dims k)) ")")) fa)
        fixed-strs (map (fn [[k v]] (str (name k) "=" v)) (sort-by key fx))]
    (concat
     [(str mode "  " pal)
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

(defn toggle-ortho
  "Switch between perspective and orthographic projection."
  []
  (let [going-ortho? (not (get-in @*state* [:camera :ortho?]))]
    (swap! *state* update-in [:camera :ortho?] not)
    (when going-ortho?
      (reset-ortho-camera!))))

(defn set-ortho [on?]
  (swap! *state* assoc-in [:camera :ortho?] (boolean on?))
  (when on? (reset-ortho-camera!)))

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

;; ── Position computation ───────────────────────────────────

(defonce ^:private all-positions
  (delay (let [a (int-array c/total-letters)]
           (dotimes [i c/total-letters] (aset a i i))
           a)))

(defn visible-positions
  "Compute the int[] of positions visible under current slice.
   If no axis is fixed, returns all 304,850 (cached)."
  []
  (let [slice (:slice @*state*)
        fixed (into {} (filter (fn [[_ v]] (some? v)) slice))]
    (if (empty? fixed)
      @all-positions
      (c/slab fixed))))

(defn position-data
  "Compute positions float[] and colors float[] for current state.
   Returns {:positions float[] :colors float[] :count int :letters float[]}."
  []
  (let [s (c/space)
        pos (visible-positions)
        n (alength pos)
        {:keys [x y z]} (:axes @*state*)
        pts (proj/project-3d pos [x y z])
        palette (:palette @*state*)
        cols (case palette
               :letter   (proj/color-by-letter s pos)
               :gematria (proj/color-by-gematria s pos)
               :book     (proj/color-by-book s pos)
               :day      (proj/color-by-day pos))
        ;; Letter indices for texture atlas lookup
        stream ^bytes (:stream s)
        letter-idx (float-array n)]
    (dotimes [i n]
      (aset letter-idx i (float (aget stream (aget pos i)))))
    {:positions pts
     :colors    cols
     :letters   letter-idx
     :count     n
     :indices   pos}))

(defn mark-clean []
  (swap! *state* assoc :dirty? false))

(defn dirty? [] (:dirty? @*state*))
