(ns experiments.137d-yhwh-loves
  "Experiment 137d: YHWH Loves — exploring the two spaces where the oracle
   between the cherubim reads אהב יהוה (YHWH loves).

   Two spaces produce identical Ark regions with YHWH in the Ark, GV=3067,
   and the oracle reading 'YHWH loves':

     1. 5 × 14 × 65 × 67  cube axes (1,2,3), pinned at axis 0 (He=2)
     2. 7 × 10 × 65 × 67  cube axes (1,2,3), pinned at axis 0 (completeness=3)

   Questions:
     - What text fills the Ark, Mercy Seat, Holy of Holies?
     - What verses do the spine letters come from?
     - Does the Mercy Seat still ask 'what is my name?'
     - What verse does the cube center fall on?
     - WHY are the Ark regions identical? Are the linear positions the same?"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.oracle :as o]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── The two spaces ──────────────────────────────────────

(def spaces
  [{:dims [5 14 65 67]  :cube-axes [1 2 3] :pin-axes [0] :label "5×14×65×67 (He=2)"}
   {:dims [7 10 65 67]  :cube-axes [1 2 3] :pin-axes [0] :label "7×10×65×67 (completeness=3)"}])

;; ── Furniture offsets (from 137) ─────────────────────────

(def cube-side 10)

(def ark-offsets
  {:length [4 5 6]
   :width  [4 5 6]
   :height [3 4 5 6 7]})

(def mercy-seat-offsets
  {:length [4 5 6]
   :width  [6]
   :height [3 4 5 6 7]})

(def cherub-offsets
  {:truth   {:length [4 5 6] :width [6] :height [3]}
   :mercy    {:length [4 5 6] :width [6] :height [7]}
   :between {:length [4 5 6] :width [6] :height [4 5 6]}})

;; ── Cube placement (from 137) ───────────────────────────

(defn place-cube [dims cube-axes pin-axes]
  (let [center (u/center-coord dims)
        cube-ranges (into {}
                     (for [ax cube-axes]
                       (let [c (center ax)
                             half (quot cube-side 2)
                             start (- c half)
                             end (+ start cube-side)]
                         (if (and (>= start 0) (<= end (dims ax)))
                           [ax [start end]]
                           (let [start' (max 0 (min start (- (dims ax) cube-side)))
                                 end'   (+ start' cube-side)]
                             (if (<= end' (dims ax))
                               [ax [start' end']]
                               nil))))))
        pin-coords (into {} (for [ax pin-axes] [ax (center ax)]))]
    (when (and (= (count cube-ranges) 3)
               (every? some? (vals cube-ranges)))
      {:cube-ranges  cube-ranges
       :pin-coords   pin-coords
       :center-coord center})))

(defn cube-positions [dims placement]
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

(defn furniture-positions [dims placement cube-axes offset-spec]
  (let [{:keys [cube-ranges pin-coords]} placement
        k (count dims)
        sorted-cube-axes (vec (sort cube-axes))
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

;; ── Region analysis ─────────────────────────────────────

(defn region-detail
  "Full detail for a set of positions: letters, GV, per-letter verse info."
  [s positions]
  (let [pos-vec (vec positions)]
    (mapv (fn [p]
            (let [v (c/verse-at s p)]
              {:pos    p
               :letter (c/letter-at s p)
               :gv     (c/gv-at s p)
               :verse  (str (:book v) " " (:ch v) ":" (:vs v))}))
          pos-vec)))

(defn region-summary [s positions]
  (let [detail (region-detail s positions)
        letters (mapv :letter detail)
        text (apply str letters)
        gv (reduce + (map :gv detail))
        verses (into (sorted-set) (map :verse detail))]
    {:text text :count (count letters) :gv gv :verses verses :detail detail}))

(defn find-yhwh [text]
  (loop [idx 0 found []]
    (let [next-idx (.indexOf ^String text "יהוה" (int idx))]
      (if (neg? next-idx)
        found
        (recur (inc next-idx) (conj found next-idx))))))

(defn oracle-on-text
  "Run oracle parse-letters on a string."
  [text vocab]
  (let [normalized (apply str (map (fn [ch]
                                     (get {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ} ch ch))
                                   text))]
    (o/parse-letters normalized {:vocab vocab})))

;; ── Spine: the longest axis through the Ark center ──────

(defn ark-spine
  "Extract the spine through the center of the Ark along each cube axis.
   The spine is a line through the Ark center, varying one cube axis
   while fixing the other two at the Ark center values.
   Returns all three spines (one per cube axis)."
  [dims placement cube-axes]
  (let [{:keys [cube-ranges pin-coords]} placement
        k (count dims)
        sorted (vec (sort cube-axes))
        ;; Ark center in cube: midpoints of ark offsets
        ;; length: 4,5,6 → center=5, width: 4,5,6 → center=5, height: 3,4,5,6,7 → center=5
        ark-center-offsets [5 5 5]
        bases (mapv #(first (cube-ranges %)) sorted)]
    (for [spine-idx (range 3)]
      (let [spine-ax (sorted spine-idx)
            [s e] (cube-ranges spine-ax)
            fixed-offsets (vec (for [i (range 3) :when (not= i spine-idx)]
                                [(sorted i) (+ (bases i) (ark-center-offsets i))]))]
        {:axis spine-ax
         :range [s e]
         :positions
         (vec (for [v (range s e)]
                (let [coord (vec (for [i (range k)]
                                   (cond
                                     (= i spine-ax) v
                                     :else (or (some (fn [[ax val]] (when (= ax i) val)) fixed-offsets)
                                               (pin-coords i)))))]
                  (u/coord->idx dims coord))))}))))

;; ── Main analysis ───────────────────────────────────────

(defn analyze-space
  "Full analysis of one space."
  [s {:keys [dims cube-axes pin-axes label]}]
  (let [placement (place-cube dims cube-axes pin-axes)
        sorted-axes (sort cube-axes)

        ;; Regions
        hoh-pos   (vec (cube-positions dims placement))
        ark-pos   (vec (furniture-positions dims placement sorted-axes ark-offsets))
        mercy-pos (vec (furniture-positions dims placement sorted-axes mercy-seat-offsets))
        right-pos (vec (furniture-positions dims placement sorted-axes (:truth cherub-offsets)))
        left-pos  (vec (furniture-positions dims placement sorted-axes (:mercy cherub-offsets)))
        between-pos (vec (furniture-positions dims placement sorted-axes (:between cherub-offsets)))

        ;; Summaries
        hoh   (region-summary s hoh-pos)
        ark   (region-summary s ark-pos)
        mercy (region-summary s mercy-pos)
        between (region-summary s between-pos)

        ;; Spine
        spines (ark-spine dims placement cube-axes)

        ;; Oracle on between-cherubim and mercy seat
        between-oracle (oracle-on-text (:text between) :dict)
        mercy-oracle   (oracle-on-text (:text mercy) :dict)

        ;; Cube center
        cube-center-pos (u/coord->idx dims (:center-coord placement))
        cube-center-verse (u/verse-str s cube-center-pos)]

    {:label          label
     :dims           dims
     :cube-axes      sorted-axes
     :pin-axes       pin-axes
     :pin-values     (mapv #((:center-coord placement) %) pin-axes)
     :placement      placement
     :cube-center    cube-center-verse
     :cube-center-pos cube-center-pos

     :hoh   hoh
     :ark   ark
     :mercy mercy
     :between between

     :truth-text  (apply str (mapv #(c/letter-at s %) right-pos))
     :mercy-text   (apply str (mapv #(c/letter-at s %) left-pos))

     :between-oracle between-oracle
     :mercy-oracle   mercy-oracle

     :spines spines

     :yhwh-in-ark (find-yhwh (:text ark))

     ;; Raw positions for identity check
     :ark-positions   (vec (sort ark-pos))
     :mercy-positions (vec (sort mercy-pos))
     :hoh-positions   (vec (sort hoh-pos))}))

;; ── Output formatting ──────────────────────────────────

(defn format-spine [s spine]
  (let [sb (StringBuilder.)]
    (.append sb (str "    Axis " (:axis spine) " range " (:range spine) ":\n"))
    (doseq [p (:positions spine)]
      (let [letter (c/letter-at s p)
            v (c/verse-at s p)
            vstr (str (:book v) " " (:ch v) ":" (:vs v))]
        (.append sb (str "      pos=" p " " letter " (" (c/gv-at s p) ") — " vstr "\n"))))
    (str sb)))

(defn format-analysis [s result]
  (let [sb (StringBuilder.)
        {:keys [label dims cube-axes pin-axes pin-values
                cube-center cube-center-pos
                hoh ark mercy between
                right-text left-text
                between-oracle mercy-oracle
                spines yhwh-in-ark
                ark-positions mercy-positions hoh-positions]} result]

    (.append sb (str "\n═══ " label " ═══\n"))
    (.append sb (str "  Dims: " (u/format-dims dims) "\n"))
    (.append sb (str "  Cube axes: " (pr-str cube-axes)
                     " | Pin: axis " (pr-str pin-axes) " = " (pr-str pin-values) "\n"))
    (.append sb (str "  Cube center: " cube-center " (pos " cube-center-pos ")\n"))

    (.append sb (str "\n── Holy of Holies (1000 letters) ──\n"))
    (.append sb (str "  Letter count: " (:count hoh) "\n"))
    (.append sb (str "  GV: " (:gv hoh) "\n"))
    (.append sb (str "  Verses: " (str/join ", " (:verses hoh)) "\n"))
    (.append sb (str "  Text: " (:text hoh) "\n"))

    (.append sb (str "\n── Ark (45 letters) ──\n"))
    (.append sb (str "  Text: " (:text ark) "\n"))
    (.append sb (str "  GV: " (:gv ark) "\n"))
    (.append sb (str "  YHWH positions: " (if (seq yhwh-in-ark) (pr-str yhwh-in-ark) "none") "\n"))
    (.append sb (str "  Verses: " (str/join ", " (:verses ark)) "\n"))
    (.append sb (str "  Per-letter detail:\n"))
    (doseq [{:keys [pos letter gv verse]} (:detail ark)]
      (.append sb (str "    " letter " (" gv ") pos=" pos " — " verse "\n")))

    (.append sb (str "\n── Mercy Seat (15 letters) ──\n"))
    (.append sb (str "  Text: " (:text mercy) "\n"))
    (.append sb (str "  GV: " (:gv mercy) "\n"))
    (.append sb (str "  Verses: " (str/join ", " (:verses mercy)) "\n"))
    (.append sb (str "  Per-letter detail:\n"))
    (doseq [{:keys [pos letter gv verse]} (:detail mercy)]
      (.append sb (str "    " letter " (" gv ") pos=" pos " — " verse "\n")))

    (.append sb (str "\n  Mercy Seat oracle (:dict):\n"))
    (if (seq mercy-oracle)
      (doseq [r mercy-oracle]
        (.append sb (str "    " (:text r) "\n")))
      (.append sb "    (no readings)\n"))

    (.append sb (str "\n── Between Cherubim (9 letters) ──\n"))
    (.append sb (str "  Text: " (:text between) "\n"))
    (.append sb (str "  Right cherub: " right-text "\n"))
    (.append sb (str "  Left cherub: " left-text "\n"))
    (.append sb (str "  Oracle readings (:dict):\n"))
    (doseq [r between-oracle]
      (.append sb (str "    " (:text r) "\n")))

    (.append sb (str "\n── Spines (through Ark center) ──\n"))
    (doseq [spine spines]
      (.append sb (format-spine s spine)))

    (.append sb (str "\n── Raw Ark positions (linear) ──\n"))
    (.append sb (str "  " (pr-str ark-positions) "\n"))

    (str sb)))

;; ── Identity check ──────────────────────────────────────

(defn check-identity
  "Check if two spaces produce identical linear positions."
  [r1 r2]
  (let [sb (StringBuilder.)]
    (.append sb "\n═══ IDENTITY CHECK: Why are the Ark regions identical? ═══\n")

    (let [ark1 (:ark-positions r1)
          ark2 (:ark-positions r2)
          same-ark? (= ark1 ark2)]
      (.append sb (str "\n  Ark positions identical? " same-ark? "\n"))
      (when (not same-ark?)
        (.append sb (str "  Space 1 Ark: " (pr-str ark1) "\n"))
        (.append sb (str "  Space 2 Ark: " (pr-str ark2) "\n"))
        (let [only1 (remove (set ark2) ark1)
              only2 (remove (set ark1) ark2)]
          (.append sb (str "  Only in space 1: " (pr-str (vec only1)) "\n"))
          (.append sb (str "  Only in space 2: " (pr-str (vec only2)) "\n")))))

    (let [mercy1 (:mercy-positions r1)
          mercy2 (:mercy-positions r2)
          same-mercy? (= mercy1 mercy2)]
      (.append sb (str "  Mercy positions identical? " same-mercy? "\n")))

    (let [hoh1 (:hoh-positions r1)
          hoh2 (:hoh-positions r2)
          same-hoh? (= hoh1 hoh2)]
      (.append sb (str "  HoH positions identical? " same-hoh? "\n")))

    ;; Show how the coordinate arithmetic maps
    (.append sb (str "\n── Coordinate mapping ──\n"))
    (let [dims1 (:dims r1)
          dims2 (:dims r2)
          pl1 (:placement r1)
          pl2 (:placement r2)]
      (.append sb (str "  Space 1 " (u/format-dims dims1) ":\n"))
      (.append sb (str "    Center: " (pr-str (:center-coord pl1)) "\n"))
      (.append sb (str "    Cube ranges: " (pr-str (:cube-ranges pl1)) "\n"))
      (.append sb (str "    Pin coords: " (pr-str (:pin-coords pl1)) "\n"))
      (.append sb (str "    Strides: " (pr-str (u/compute-strides dims1)) "\n"))

      (.append sb (str "  Space 2 " (u/format-dims dims2) ":\n"))
      (.append sb (str "    Center: " (pr-str (:center-coord pl2)) "\n"))
      (.append sb (str "    Cube ranges: " (pr-str (:cube-ranges pl2)) "\n"))
      (.append sb (str "    Pin coords: " (pr-str (:pin-coords pl2)) "\n"))
      (.append sb (str "    Strides: " (pr-str (u/compute-strides dims2)) "\n")))

    ;; Show first few ark positions with coords in each space
    (.append sb (str "\n── First 10 Ark positions in both spaces ──\n"))
    (let [ark1 (take 10 (:ark-positions r1))
          ark2 (take 10 (:ark-positions r2))]
      (.append sb "  Space 1:\n")
      (doseq [p ark1]
        (.append sb (str "    pos=" p " → coord=" (pr-str (u/idx->coord (:dims r1) p)) "\n")))
      (.append sb "  Space 2:\n")
      (doseq [p ark2]
        (.append sb (str "    pos=" p " → coord=" (pr-str (u/idx->coord (:dims r2) p)) "\n"))))

    (str sb)))

;; ── Run ─────────────────────────────────────────────────

(defn run []
  (println "Loading Torah space...")
  (let [s (c/space)
        _ (println "Analyzing two spaces where YHWH loves...\n")

        results (mapv #(analyze-space s %) spaces)

        output (StringBuilder.)]

    (.append output "=== Experiment 137d: YHWH Loves ===\n")
    (.append output "Two spaces where the oracle between the cherubim reads אהב יהוה\n")

    (doseq [r results]
      (.append output (format-analysis s r)))

    (.append output (check-identity (first results) (second results)))

    ;; Final summary
    (.append output "\n═══ SUMMARY ═══\n")
    (doseq [r results]
      (.append output (str "\n" (:label r) ":\n"))
      (.append output (str "  Cube center: " (:cube-center r) "\n"))
      (.append output (str "  Ark GV: " (:gv (:ark r)) "\n"))
      (.append output (str "  Between cherubim: " (:text (:between r)) "\n"))
      (.append output (str "  YHWH at: " (pr-str (:yhwh-in-ark r)) "\n"))
      (.append output (str "  Oracle readings: "
                           (str/join " / " (map :text (:between-oracle r)))
                           "\n")))

    (let [out-str (str output)]
      (u/save-results! "137d-yhwh-loves" (mapv #(dissoc % :placement) results) out-str)
      (println out-str)
      (println "\nResults saved to data/experiments/137d-yhwh-loves-output.txt")
      results)))

;; ── REPL entry ──────────────────────────────────────────

(comment

  (def results (run))

  nil)
