(ns experiments.137-hoh-across-spaces
  "Experiment 137: The Holy of Holies Across All Spaces.

   The Holy of Holies is a 10×10×10 cube. In the canonical 4D space
   [7,50,13,67], it sits at a=3 and spans b=20..29, c=1..10, d=28..37.
   The Ark lives inside. YHWH is written on the spine. The Mercy Seat
   asks 'what is my name?'

   Question: In which other decompositions of 304,850 can this cube
   exist? What survives? What changes?

   A decomposition can host the cube if at least 3 axes have size ≥ 10.
   For each qualifying space, we try every valid placement of the cube
   (choosing which 3 axes to span, and which coordinates to pin),
   centered at the space center. Then we check:

   1. What text is inside the 1,000-letter cube?
   2. Is Lev 8:35 (guard the charge) inside?
   3. Does YHWH appear on the Ark spine?
   4. Does the Mercy Seat ask its question?
   5. What does the oracle read between the cherubim?

   Qualifying spaces: 24 in 3D, 17 in 4D, 4 in 5D = 45 total."
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.oracle :as o]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── Configuration ─────────────────────────────────────────

(def cube-side 10)  ;; Holy of Holies is 10×10×10 cubits

;; Ark is 2.5 × 1.5 × 1.5 cubits = 5 × 3 × 3 in half-cubit units
;; But in our coordinate mapping: centered within the cube,
;; occupying the middle portion of each cube axis.
;; In the canonical: b=24..26 (3 of 10), c=5..7 (3 of 10), d=31..35 (5 of 10)
;; Relative to cube start: [4..6, 4..6, 3..7] — centered, Ark dims [3,3,5]

(def ark-offsets
  "Ark position offsets within the 10×10×10 cube.
   Ark is 5×3×3 (length × width × height in the cube).
   Centered: offset 4..6 on first two cube-axes, 3..7 on third."
  {:length [4 5 6]        ;; 3 positions centered in 10
   :width  [4 5 6]        ;; 3 positions centered in 10
   :height [3 4 5 6 7]})  ;; 5 positions centered in 10

(def mercy-seat-offsets
  "Mercy Seat: top face of Ark.
   Same length/height as Ark, width = top only (offset 6)."
  {:length [4 5 6]
   :width  [6]            ;; top face only
   :height [3 4 5 6 7]})

(def cherub-offsets
  "Cherubim sit on the mercy seat at the ends.
   Right: height offset 3, Left: height offset 7."
  {:truth  {:length [4 5 6] :width [6] :height [3]}
   :mercy   {:length [4 5 6] :width [6] :height [7]}
   :between {:length [4 5 6] :width [6] :height [4 5 6]}})

;; ── Space qualification ───────────────────────────────────

(defn axes-ge-10
  "Indices of axes with value >= 10."
  [dims]
  (keep-indexed (fn [i d] (when (>= d 10) i)) dims))

(defn qualifying?
  "Can this decomposition host a 10×10×10 cube?"
  [dims]
  (>= (count (axes-ge-10 dims)) 3))

(defn cube-axis-assignments
  "All ways to choose 3 axes (from those >= 10) for the cube.
   Returns seqs of [cube-axis-indices pinning-axis-indices]."
  [dims]
  (let [ge10 (vec (axes-ge-10 dims))
        k (count ge10)]
    (for [i (range k)
          j (range (inc i) k)
          m (range (inc j) k)]
      (let [cube-axes [(ge10 i) (ge10 j) (ge10 m)]
            pin-axes (vec (remove (set cube-axes) (range (count dims))))]
        {:cube-axes cube-axes
         :pin-axes  pin-axes}))))

;; ── Cube placement ────────────────────────────────────────

(defn place-cube
  "Place a 10×10×10 cube centered at the space center.
   cube-axes: which 3 axis indices the cube spans.
   pin-axes: remaining axes, pinned at their center value.

   Returns {:cube-ranges  {axis-idx -> [start end)}
            :pin-coords   {axis-idx -> value}
            :center-coord full center coordinate}."
  [dims cube-axes pin-axes]
  (let [center (u/center-coord dims)
        cube-ranges (into {}
                     (for [ax cube-axes]
                       (let [c (center ax)
                             half (quot cube-side 2)
                             start (- c half)
                             end (+ start cube-side)]
                         ;; Clamp to axis bounds
                         (if (and (>= start 0) (<= end (dims ax)))
                           [ax [start end]]
                           ;; If cube doesn't fit centered, shift it
                           (let [start' (max 0 (min start (- (dims ax) cube-side)))
                                 end'   (+ start' cube-side)]
                             (if (<= end' (dims ax))
                               [ax [start' end']]
                               nil))))))  ;; nil = doesn't fit
        pin-coords (into {} (for [ax pin-axes]
                              [ax (center ax)]))]
    (when (and (= (count cube-ranges) 3)
               (every? some? (vals cube-ranges)))
      {:cube-ranges  cube-ranges
       :pin-coords   pin-coords
       :center-coord center})))

(defn cube-positions
  "Generate all positions inside the cube."
  [dims placement]
  (let [{:keys [cube-ranges pin-coords]} placement
        k (count dims)
        sorted-cube-axes (sort (keys cube-ranges))
        [ax0 ax1 ax2] sorted-cube-axes
        [s0 e0] (cube-ranges ax0)
        [s1 e1] (cube-ranges ax1)
        [s2 e2] (cube-ranges ax2)]
    (for [v0 (range s0 e0)
          v1 (range s1 e1)
          v2 (range s2 e2)]
      (let [coord (vec (for [i (range k)]
                         (cond
                           (= i ax0) v0
                           (= i ax1) v1
                           (= i ax2) v2
                           :else (pin-coords i))))]
        (u/coord->idx dims coord)))))

(defn furniture-positions
  "Generate positions for a piece of furniture within the cube.
   offset-spec: {:length [...] :width [...] :height [...]}
   cube-axes: the 3 axis indices [length-axis width-axis height-axis]"
  [dims placement cube-axes offset-spec]
  (let [{:keys [cube-ranges pin-coords]} placement
        k (count dims)
        sorted-cube-axes (vec (sort cube-axes))
        ;; Map furniture dimensions to sorted cube axes
        ;; length → first cube axis, width → second, height → third
        [ax0 ax1 ax2] sorted-cube-axes
        base0 (first (cube-ranges ax0))
        base1 (first (cube-ranges ax1))
        base2 (first (cube-ranges ax2))]
    (for [lo (:length offset-spec)
          wo (:width offset-spec)
          ho (:height offset-spec)]
      (let [coord (vec (for [i (range k)]
                         (cond
                           (= i ax0) (+ base0 lo)
                           (= i ax1) (+ base1 wo)
                           (= i ax2) (+ base2 ho)
                           :else (pin-coords i))))]
        (u/coord->idx dims coord)))))

;; ── Analysis ──────────────────────────────────────────────

(defn region-info
  "Extract letters, GV, and verses for a set of positions."
  [s positions]
  (let [pos-vec (vec positions)
        letters (mapv #(c/letter-at s %) pos-vec)
        gvs (mapv #(c/gv-at s %) pos-vec)
        verses (into (sorted-set)
                     (map #(let [v (c/verse-at s %)]
                             (str (:book v) " " (:ch v) ":" (:vs v)))
                          pos-vec))]
    {:letters letters
     :text    (apply str letters)
     :count   (count letters)
     :gv      (reduce + gvs)
     :verses  verses}))

(defn contains-verse?
  "Does any position in the set fall in the given verse?"
  [s positions book ch vs]
  (some (fn [p]
          (let [v (c/verse-at s p)]
            (and (= (:book v) book)
                 (= (:ch v) ch)
                 (= (:vs v) vs))))
        positions))

(defn find-yhwh-in-region
  "Search for consecutive YHWH (יהוה) in the region's letters."
  [s positions]
  (let [letters (mapv #(c/letter-at s %) (vec positions))
        text (apply str letters)
        yhwh "יהוה"]
    (loop [idx 0 found []]
      (let [next-idx (.indexOf ^String text yhwh (int idx))]
        (if (neg? next-idx)
          found
          (recur (inc next-idx) (conj found next-idx)))))))

(defn oracle-on-region
  "Run oracle on a region's letters."
  [s positions vocab]
  (let [letters (mapv #(c/letter-at s %) (vec positions))
        ;; Normalize final forms
        normalized (map (fn [ch]
                          (get {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ} ch ch))
                        letters)
        text (apply str normalized)]
    (o/parse-letters text {:vocab vocab})))

;; ── Per-space analysis ────────────────────────────────────

(defn analyze-placement
  "Analyze a single cube placement in a given space."
  [s dims assignment]
  (let [{:keys [cube-axes pin-axes]} assignment
        placement (place-cube dims cube-axes pin-axes)]
    (when placement
      (let [hoh-pos (cube-positions dims placement)
            ark-pos (furniture-positions dims placement (sort cube-axes) ark-offsets)
            mercy-pos (furniture-positions dims placement (sort cube-axes) mercy-seat-offsets)
            right-pos (furniture-positions dims placement (sort cube-axes)
                                          (get-in cherub-offsets [:truth]))
            left-pos (furniture-positions dims placement (sort cube-axes)
                                         (get-in cherub-offsets [:mercy]))
            between-pos (furniture-positions dims placement (sort cube-axes)
                                            (get-in cherub-offsets [:between]))

            hoh-info (region-info s hoh-pos)
            ark-info (region-info s ark-pos)
            mercy-info (region-info s mercy-pos)
            right-info (region-info s right-pos)
            left-info (region-info s left-pos)
            between-info (region-info s between-pos)

            ;; Key tests
            has-lev835 (contains-verse? s hoh-pos "Leviticus" 8 35)
            yhwh-in-ark (find-yhwh-in-region s ark-pos)
            between-oracle (oracle-on-region s between-pos :dict)

            ;; Center of the cube in this space
            cube-center-pos (u/coord->idx dims (:center-coord placement))
            cube-center-verse (u/verse-str s cube-center-pos)]

        {:dims          dims
         :cube-axes     (sort cube-axes)
         :pin-axes      pin-axes
         :pin-values    (mapv #((:center-coord placement) %) pin-axes)
         :placement     placement
         :cube-center   cube-center-verse

         ;; Sizes
         :hoh-count     (:count hoh-info)
         :ark-count     (:count ark-info)
         :mercy-count   (:count mercy-info)

         ;; Key tests
         :has-lev835    has-lev835
         :yhwh-in-ark  (count yhwh-in-ark)
         :yhwh-positions yhwh-in-ark

         ;; GV
         :ark-gv        (:gv ark-info)
         :mercy-gv      (:gv mercy-info)

         ;; Oracle between cherubim. Keep saved artifacts Hebrew-first.
         :between-text   (:text between-info)
         :between-oracle (mapv (fn [r]
                                 {:text (:text r)})
                               between-oracle)

         ;; Cherubim / mercy seat
         :truth-text      (:text right-info)
         :mercy-text      (:text left-info)
         :mercy-seat-text (:text mercy-info)

         ;; Verse span
         :hoh-verses    (:verses hoh-info)
         :ark-verses    (:verses ark-info)}))))

(defn analyze-space
  "Analyze all valid cube placements in a given decomposition."
  [s dims]
  (let [assignments (cube-axis-assignments dims)]
    (->> assignments
         (map #(analyze-placement s dims %))
         (remove nil?)
         vec)))

;; ── Survey ────────────────────────────────────────────────

(defn all-qualifying-spaces
  "All decompositions across 3D-5D that can host a 10×10×10 cube."
  []
  (let [spaces (atom [])]
    (doseq [k [3 4 5]]
      (doseq [dims (u/decompositions-k u/N k)]
        (when (qualifying? dims)
          (swap! spaces conj {:dims dims :k k}))))
    @spaces))

(defn run-survey
  "Run the full Holy of Holies survey across all qualifying spaces."
  []
  (let [s (c/space)
        spaces (all-qualifying-spaces)
        _ (println (str "=== Experiment 137: Holy of Holies Across "
                        (count spaces) " Spaces ===\n"))
        results (atom [])
        output (StringBuilder.)]

    (.append output (str "=== Experiment 137: Holy of Holies Across All Spaces ===\n"))
    (.append output (str (count spaces) " qualifying decompositions\n\n"))

    (doseq [{:keys [dims k]} spaces]
      (println (str "  Analyzing " (u/format-dims dims) " (" k "D)..."))
      (let [placements (analyze-space s dims)]
        (doseq [p placements]
          (swap! results conj p)

          (.append output (str "── " (u/format-dims dims) " (" k "D) ──\n"))
          (.append output (str "  Cube axes: " (pr-str (:cube-axes p))
                               " | Pin: " (pr-str (zipmap (:pin-axes p) (:pin-values p))) "\n"))
          (.append output (str "  Cube center: " (:cube-center p) "\n"))
          (.append output (str "  HoH letters: " (:hoh-count p)
                               " | Ark: " (:ark-count p)
                               " | Mercy Seat: " (:mercy-count p) "\n"))
          (.append output (str "  Lev 8:35 inside? " (:has-lev835 p) "\n"))
          (.append output (str "  YHWH in Ark: " (:yhwh-in-ark p) " occurrences\n"))
          (.append output (str "  Ark GV: " (:ark-gv p)
                               (when (zero? (mod (:ark-gv p) 137))
                                 (str " = " (quot (:ark-gv p) 137) " × 137"))
                               "\n"))
          (.append output (str "  Mercy Seat GV: " (:mercy-gv p) "\n"))
          (.append output (str "  Right cherub: " (:truth-text p) "\n"))
          (.append output (str "  Left cherub: " (:mercy-text p) "\n"))
          (.append output (str "  Between cherubim: " (:between-text p) "\n"))
          (when (seq (:between-oracle p))
            (.append output (str "  Oracle readings: "
                                 (str/join ", " (map :text (:between-oracle p)))
                                 "\n")))
          (.append output "\n"))))

    ;; Summary
    (let [all @results
          with-lev835 (filter :has-lev835 all)
          with-yhwh (filter #(pos? (:yhwh-in-ark %)) all)
          with-137 (filter #(zero? (mod (:ark-gv %) 137)) all)]

      (.append output "\n═══ SUMMARY ═══\n")
      (.append output (str "Total placements analyzed: " (count all) "\n"))
      (.append output (str "Contain Lev 8:35: " (count with-lev835) "\n"))
      (.append output (str "YHWH in Ark: " (count with-yhwh) "\n"))
      (.append output (str "Ark GV divisible by 137: " (count with-137) "\n"))

      (when (seq with-lev835)
        (.append output "\n── Spaces with Lev 8:35 ──\n")
        (doseq [p with-lev835]
          (.append output (str "  " (u/format-dims (:dims p))
                               " axes=" (pr-str (:cube-axes p))
                               " | YHWH=" (:yhwh-in-ark p)
                               " | Ark GV=" (:ark-gv p) "\n"))))

      (when (seq with-yhwh)
        (.append output "\n── Spaces with YHWH in Ark ──\n")
        (doseq [p with-yhwh]
          (.append output (str "  " (u/format-dims (:dims p))
                               " axes=" (pr-str (:cube-axes p))
                               " | count=" (:yhwh-in-ark p)
                               " | center=" (:cube-center p) "\n")))))

    (u/save-results! "137-hoh-across-spaces" @results (str output))
    (println (str "\nDone. " (count @results) " placements analyzed."))
    (println (str "Results: data/experiments/137-hoh-across-spaces-output.txt"))
    @results))

;; ── REPL entry ────────────────────────────────────────────

(comment

  ;; Load the space
  (c/space)

  ;; Check qualifying spaces
  (def spaces (all-qualifying-spaces))
  (count spaces) ;; should be 45

  ;; Quick test: canonical space
  (let [s (c/space)]
    (analyze-space s [7 50 13 67]))

  ;; Also test sorted canonical
  (let [s (c/space)]
    (analyze-space s [7 13 50 67]))

  ;; Run the full survey
  (def results (run-survey))

  ;; Inspect a specific result
  (first (filter #(= (:dims %) [7 13 50 67]) results))

  nil)
