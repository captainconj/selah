(ns experiments.075-hierarchy-and-ladder
  "Biblical hierarchies and ladders in the 4D coordinate space.

   The captains of thousands/hundreds/fifties/tens (Exodus 18:21,
   Deuteronomy 1:15) describe a 4-level nested hierarchy — the same
   depth as the Torah's 4D factorization. Both contain 50.

   Jacob's Ladder (Genesis 28:12) has gematria סלם = 130 = 10 × 13,
   connecting the captains' base unit (tens) to the love axis.
   The ladder verse traverses c=12→c=0 through a jubilee boundary.
   The full episode (28:10-22) sweeps 12 of 13 love-axis values.
   The missing rung (c=10) belongs to Esau (28:9).

   FINDINGS:
   Five convergences in the 4D coordinates:

   1. JETHRO'S FOLD: Exod 18:10 (clean fiber, c=1) fold-mirrors to
      Exod 18:21 (captains, c=10-11). Same a=2, b=20 slab.
      Blessing ↔ wisdom through the love axis fold (c ↔ 12-c).

   2. LADDER SWEEPS LOVE: Gen 28:10-22 covers c-values 0-9,11,12 —
      12 of 13 love-axis positions. The missing c=10 belongs to
      Gen 28:9 (Esau). With Esau, the pair sweeps ALL 13.
      The ladder verse (28:12) crosses c=12→c=0 through b=44→45,
      a jubilee boundary. End of love → beginning of love.

   3. SULLAM = SINAI: סלם = סיני = 130 = 10 × 13. The ladder and
      the mountain share the same gematria. Both connect heaven
      to earth. The number = captains-of-tens × love-axis.

   4. FIBERS THROUGH THE LADDER: The c-fiber through סלם has
      GV = 741 = 13 × 57 (divides by love). The b-fiber has
      GV = 2,709 = 7 × 387 (divides by completeness). Each
      non-text fiber divides by the axis it's not on.

   5. ESAU COMPLETES UNDERSTANDING: With Esau (28:9-22),
      GV = 50,786 = 67 × 758. Without Esau, no axis divisibility.
      Include the brother and the total divides by understanding.
      Esau fills the c=10 gap AND completes the 67 connection.

   The sum of the four rank words (עשרת + חמשים + מאות + אלפים)
   = 1,976 = 13 × 152. The hierarchy divides by love.

   Both captains verses (Exod 18:21 and Deut 1:15) sit at c=10-11.
   The captains fill the rung the ladder leaves empty.

   304,850 / (50 × 67) = 91 = 7 × 13. Total letters divided by
   50-gates-of-understanding = completeness × love.

   Slab b=45 (where the ladder LANDS, post-jubilee) has
   GV = 53,375 = 7 × 7,625. Divides by completeness.
   Slab b=44 (pre-jubilee, where it STARTS) does not.

   Ezekiel's steps (7 outer, 8 inner, 10 vestibule) sum to 25
   (= gate width). 10 × 13 = 130 = סלם = סיני.

   The Torah tree: 304,850 = 7 × 50 × 13 × 67 (branching factors).
   The captains tree: 1000 = 10 × 2 × 5 × 10 (branching ratios).
   304,850 admits 10 and 50 but rejects 100 and 1000 — the Torah
   has only one factor of 2 (100 needs two)."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════════════
;; Helpers
;; ══════════════════════════════════════════════════════════════

(defn find-verse-positions
  "Return vector of stream positions for a given verse."
  [s book ch vs]
  (let [verses (:verse-ref s)
        vid (first (keep-indexed (fn [i v]
                                   (when (and (= (:book v) book)
                                              (= (:ch v) ch)
                                              (= (:vs v) vs))
                                     i))
                                 verses))]
    (when vid
      (let [positions (keep-indexed (fn [i v] (when (= v vid) i))
                                    (seq (:verses s)))]
        {:vid vid
         :positions (vec positions)
         :start (first positions)
         :end (last positions)
         :count (count positions)
         :text (apply str (map #(c/letter-at s %) positions))
         :gv (reduce + (map #(c/gv-at s %) positions))
         :start-coord (vec (c/idx->coord (first positions)))
         :end-coord (vec (c/idx->coord (last positions)))}))))

(defn c-values-of
  "Return sorted set of c-axis values touched by positions."
  [positions]
  (->> positions
       (map #(let [coord (vec (c/idx->coord %))] (coord 2)))
       distinct
       sort
       vec))

(defn verse-range-positions
  "Return all positions for a range of verses in a chapter."
  [s book ch vs-start vs-end]
  (let [verses (:verse-ref s)
        vids (set (keep-indexed (fn [i v]
                                  (when (and (= (:book v) book)
                                             (= (:ch v) ch)
                                             (>= (:vs v) vs-start)
                                             (<= (:vs v) vs-end))
                                    i))
                                verses))]
    (vec (keep-indexed (fn [i v] (when (vids v) i))
                       (seq (:verses s))))))

;; ══════════════════════════════════════════════════════════════
;; Part 1: The Captains Hierarchy
;; ══════════════════════════════════════════════════════════════

(defn part1-captains []
  (println "\n═══ PART 1: THE CAPTAINS HIERARCHY ═══\n")

  (let [s (c/space)]
    ;; The two captains verses
    (println "  ── The Instruction (Jethro to Moses) ──")
    (let [v (find-verse-positions s "Exodus" 18 21)]
      (println (format "  Exod 18:21  coord %s → %s" (:start-coord v) (:end-coord v)))
      (println (format "  Letters: %d  GV: %d" (:count v) (:gv v)))
      (println (format "  c-values: %s" (c-values-of (:positions v))))
      (println))

    (println "  ── The Appointment (Moses carries it out) ──")
    (let [v (find-verse-positions s "Deuteronomy" 1 15)]
      (println (format "  Deut 1:15   coord %s → %s" (:start-coord v) (:end-coord v)))
      (println (format "  Letters: %d  GV: %d" (:count v) (:gv v)))
      (println (format "  c-values: %s" (c-values-of (:positions v))))
      (println))

    (println "  Both captains verses sit at c=10-11.")
    (println)

    ;; The clean fiber for comparison
    (println "  ── The Clean Fiber (Jethro's blessing) ──")
    (let [v (find-verse-positions s "Exodus" 18 10)]
      (println (format "  Exod 18:10  coord %s → %s" (:start-coord v) (:end-coord v)))
      (println (format "  Letters: %d  GV: %d" (:count v) (:gv v)))
      (println (format "  c-values: %s" (c-values-of (:positions v))))
      (println))

    (println "  Clean fiber at c=1. c-fold: c=1 ↔ c=11.")
    (println "  Captains at c=10-11. The fold-mirror connects them.")
    (println)

    ;; The hierarchy as a tree
    (println "  ── Two Trees ──")
    (println)
    (println "  Torah tree (4 levels):     Captains tree (4 levels):")
    (println "  304,850                    1,000")
    (println "    ├─ ×7 = 43,550           ├─ ×10 = 100")
    (println "    │   ├─ ×50 = 871         │   ├─ ×2 = 50")
    (println "    │   │   ├─ ×13 = 67      │   │   ├─ ×5 = 10")
    (println "    │   │   │   └─ ×67 = 1   │   │   │   └─ ×10 = 1")
    (println)
    (println "  Both contain 50. Both are 4-level hierarchies.")
    (println "  304,850 = 2×5²×7×13×67 (one factor of 2)")
    (println "  100 = 2²×5² (needs two factors of 2)")
    (println "  → Torah admits 10 and 50 but rejects 100 and 1000.")
    (println)

    ;; Gematria of the rank words
    (println "  ── Rank Word Gematria ──")
    (let [ranks {"עשרת" "tens" "חמשים" "fifties" "מאות" "hundreds" "אלפים" "thousands"}
          values (map (fn [[w label]] [(g/word-value w) label w]) ranks)
          total (reduce + (map first values))]
      (doseq [[gv label word] (sort-by first values)]
        (println (format "    %s (%s) = %d" word label gv)))
      (println (format "    Sum = %d" total))
      (doseq [d [7 13 50 67]]
        (when (zero? (mod total d))
          (println (format "    ÷%d = %d" d (/ total d)))))
      (println)
      (println (format "    שרי (captain) = %d" (g/word-value "שרי"))))))

;; ══════════════════════════════════════════════════════════════
;; Part 2: Jacob's Ladder
;; ══════════════════════════════════════════════════════════════

(defn part2-ladder []
  (println "\n═══ PART 2: JACOB'S LADDER ═══\n")

  (let [s (c/space)]
    ;; Gematria
    (println (format "  סלם (sullam/ladder) = %d = 10 × 13" (g/word-value "סלם")))
    (println (format "  סיני (Sinai)        = %d = 10 × 13" (g/word-value "סיני")))
    (println "  Same gematria. Both connect heaven to earth.")
    (println "  130 = captains-of-tens × love-axis.")
    (println)

    ;; The ladder verse
    (println "  ── Genesis 28:12 — The Ladder Verse ──")
    (let [v (find-verse-positions s "Genesis" 28 12)]
      (println (format "  Start: %s  End: %s" (:start-coord v) (:end-coord v)))
      (println (format "  Letters: %d  GV: %d" (:count v) (:gv v)))
      (println (format "  Text: %s" (:text v)))
      (println)
      (println "  c=12 (end of love) → c=0 (beginning of love)")
      (println "  b=44 → b=45 (jubilee boundary crossing)")
      (println "  The ladder wraps the love axis through a jubilee gate."))
    (println)

    ;; The full episode verse by verse
    (println "  ── Episode Sweep (Gen 28:10-22) ──")
    (let [all-c (atom #{})]
      (doseq [vs (range 10 23)]
        (let [v (find-verse-positions s "Genesis" 28 vs)
              cv (c-values-of (:positions v))
              gv-note (if (zero? (mod (:gv v) 7))
                        (format " ÷7=%d" (/ (:gv v) 7)) "")]
          (swap! all-c into cv)
          (println (format "    28:%-2d  c=%-5s  b=%-5s  %2d letters  GV=%-5d%s"
                           vs (str/join "," cv)
                           (str/join "," (sort (distinct (map #(let [co (vec (c/idx->coord %))] (co 1))
                                                              (:positions v)))))
                           (:count v) (:gv v) gv-note))))
      (println)
      (println (format "  c-values covered: %s (%d of 13)"
                       (str/join "," (sort @all-c)) (count @all-c)))
      (println (format "  Missing: c=%s"
                       (str/join "," (remove @all-c (range 13))))))

    (println)

    ;; The missing rung
    (println "  ── The Missing Rung ──")
    (let [v (find-verse-positions s "Genesis" 28 9)]
      (println (format "  Gen 28:9  c-values: %s" (c-values-of (:positions v))))
      (println (format "  Text: %s" (:text v)))
      (println)
      (println "  28:9 = Esau marries Ishmael's daughter. Sits at c=10-11.")
      (println "  The missing rung belongs to Esau — the one who sold the birthright.")
      (println "  Include 28:9 and the pair (Esau + Jacob) sweeps ALL 13 c-values."))

    (println)

    ;; Total episode
    (let [positions (verse-range-positions s "Genesis" 28 10 22)
          gv (reduce + (map #(c/gv-at s %) positions))]
      (println (format "  Episode total: %d letters, GV = %d" (count positions) gv))
      (doseq [d [7 13 50 67]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d)))))

      ;; With Esau
      (let [esau-pos (verse-range-positions s "Genesis" 28 9 22)
            gv2 (reduce + (map #(c/gv-at s %) esau-pos))]
        (println (format "  With Esau (28:9-22): %d letters, GV = %d" (count esau-pos) gv2))
        (doseq [d [7 13 50 67]]
          (when (zero? (mod gv2 d))
            (println (format "    ÷%d = %d" d (/ gv2 d)))))))))

;; ══════════════════════════════════════════════════════════════
;; Part 3: Fibers Through the Ladder
;; ══════════════════════════════════════════════════════════════

(defn part3-ladder-fibers []
  (println "\n═══ PART 3: FIBERS THROUGH THE LADDER ═══\n")

  (let [s (c/space)
        ;; Position of סלם in the text (first letter of sullam)
        ;; Gen 28:12 text: ויחלםוהנהסלםמצבארצהוראשומגיעהשמימהוהנהמלאכיאלהיםעליםוירדיםבו
        ;; The ס of סלם is at position ~10 in the verse
        v (find-verse-positions s "Genesis" 28 12)
        text (:text v)
        ;; Find סלם in the text
        sullam-offset (str/index-of text "סלם")
        sullam-pos (+ (:start v) sullam-offset)
        sullam-coord (vec (c/idx->coord sullam-pos))]

    (println (format "  סלם starts at position %d, coord %s" sullam-pos sullam-coord))
    (println)

    ;; d-fiber through sullam
    (println "  ── d-fiber (67 letters of understanding) ──")
    (let [fiber (c/fiber :d {:a (sullam-coord 0) :b (sullam-coord 1) :c (sullam-coord 2)})
          text (apply str (map #(c/letter-at s %) (seq fiber)))
          gv (reduce + (map #(c/gv-at s %) (seq fiber)))
          v-start (c/verse-at s (aget fiber 0))
          v-end (c/verse-at s (aget fiber 66))]
      (println (format "    From: %s %d:%d" (:book v-start) (:ch v-start) (:vs v-start)))
      (println (format "    To:   %s %d:%d" (:book v-end) (:ch v-end) (:vs v-end)))
      (println (format "    GV: %d" gv))
      (doseq [d [7 13 67]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d)))))
      (println (format "    Text: %s" text)))

    (println)

    ;; a-fiber through sullam (7 letters across the days)
    (println "  ── a-fiber (7 letters of completeness) ──")
    (let [fiber (c/fiber :a {:b (sullam-coord 1) :c (sullam-coord 2) :d (sullam-coord 3)})
          text (apply str (map #(c/letter-at s %) (seq fiber)))
          gv (reduce + (map #(c/gv-at s %) (seq fiber)))]
      (println (format "    Text: %s  GV: %d" text gv))
      (doseq [d [7 13 67]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d)))))
      (println "    Verses:")
      (doseq [i (range 7)]
        (let [p (aget fiber i)
              v (c/verse-at s p)
              ch (c/letter-at s p)]
          (println (format "      a=%d: %s (%s %d:%d)" i ch (:book v) (:ch v) (:vs v))))))

    (println)

    ;; c-fiber through sullam (13 letters of love)
    (println "  ── c-fiber (13 letters of love) ──")
    (let [fiber (c/fiber :c {:a (sullam-coord 0) :b (sullam-coord 1) :d (sullam-coord 3)})
          text (apply str (map #(c/letter-at s %) (seq fiber)))
          gv (reduce + (map #(c/gv-at s %) (seq fiber)))]
      (println (format "    Text: %s  GV: %d" text gv))
      (doseq [d [7 13 67]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d))))))

    (println)

    ;; b-fiber through sullam (50 letters of jubilee)
    (println "  ── b-fiber (50 letters of jubilee) ──")
    (let [fiber (c/fiber :b {:a (sullam-coord 0) :c (sullam-coord 2) :d (sullam-coord 3)})
          text (apply str (map #(c/letter-at s %) (seq fiber)))
          gv (reduce + (map #(c/gv-at s %) (seq fiber)))]
      (println (format "    Text: %s" text))
      (println (format "    GV: %d" gv))
      (doseq [d [7 13 50 67]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d))))))))

;; ══════════════════════════════════════════════════════════════
;; Part 4: Ezekiel's Steps
;; ══════════════════════════════════════════════════════════════

(defn part4-steps []
  (println "\n═══ PART 4: EZEKIEL'S STEPS ═══\n")

  (println "  The temple ascends by steps: 7 → 8 → 10")
  (println "    Outer gates:  7 steps (40:22, 40:26)")
  (println "    Inner gates:  8 steps (40:31, 40:34, 40:37)")
  (println "    Vestibule:   10 steps (40:49)")
  (println)
  (println "  Sum:     7 + 8 + 10 = 25 (gate width)")
  (println "  Product: 7 × 8 × 10 = 560 = 7 × 80")
  (println)
  (println "  7 = completeness (axis a)")
  (println "  8 = new beginning (brit milah)")
  (println "  10 = base of governance (captains of tens)")
  (println)

  ;; Connection to structural numbers
  (println "  Step connections:")
  (println (format "    7 × 50 = 350 = קרן = ספיר"))
  (println (format "    8 × 50 = 400 = ת (tav, completion)"))
  (println (format "    10 × 50 = 500 = ך (final kaf) = temple precinct"))
  (println (format "    10 × 13 = 130 = סלם = סיני (ladder = Sinai)"))
  (println)

  ;; Three steps as a mini-tree
  (println "  Three levels of approach:")
  (println "    Outer court → 7 steps → inner court")
  (println "    Inner court → 8 steps → vestibule")
  (println "    Vestibule   → 10 steps → the holy")
  (println)
  (println "  Like the Torah tree: each level requires ascending.")
  (println "  7 steps of completeness, then 8 of new beginning,")
  (println "  then 10 of governance. Each step changes something."))

;; ══════════════════════════════════════════════════════════════
;; Part 5: The 50 Convergence
;; ══════════════════════════════════════════════════════════════

(defn part5-convergence []
  (println "\n═══ PART 5: THE 50 CONVERGENCE ═══\n")

  (println "  The number 50 appears in EVERY hierarchical structure:")
  (println)
  (println "  1. Torah space:    304,850 = 7 × [50] × 13 × 67")
  (println "  2. Captains:       1000 / 100 / [50] / 10")
  (println "  3. Ezekiel gates:  gate length = [50] cubits (×6)")
  (println "  4. Tabernacle:     courtyard = 100 × [50] cubits")
  (println "  5. Noah's Ark:     300 × [50] × 30 cubits")
  (println "  6. Torah ELS:      תורה at skip [50]")
  (println "  7. Jubilee:        [50]th year = release")
  (println "  8. Pentecost:      [50] days after Passover")
  (println "  9. Kabbalistic:    [50] gates of understanding")
  (println)
  (println "  50 is the shared coordinate across every sacred hierarchy.")
  (println "  It's the jubilee — the reset, the release, the return.")
  (println "  In a base-10 world, 50 is the sacred interruption.")
  (println)

  ;; 50 gates of understanding
  (println "  50 gates of understanding: 50 × 67 = 3,350")
  (println "  But tradition says only 49 are accessible.")
  (println "  49 × 67 = 3,283. The 50th gate = binah itself.")
  (println (format "  3,350 + 871 (jubilee stride) = %d" (+ 3350 871)))
  (println (format "  304,850 / 3,350 = %.1f" (/ 304850.0 3350)))
  (println (format "  304,850 / 50 = 6,097 (prime)" )))

;; ══════════════════════════════════════════════════════════════
;; Part 6: The Slab at the Ladder
;; ══════════════════════════════════════════════════════════════

(defn part6-ladder-slab []
  (println "\n═══ PART 6: THE SLAB AT THE LADDER ═══\n")

  (let [s (c/space)
        ;; The ladder is at b=44, c=12 (start) / b=45, c=0 (end)
        ;; Check both slabs
        ]

    ;; Slab b=44 (where the ladder starts)
    (println "  ── Slab b=44 (pre-jubilee, where ladder starts) ──")
    (let [slab-pos (c/slab {:a 0 :b 44})
          gv (reduce + (map #(c/gv-at s %) (seq slab-pos)))]
      (println (format "    %d letters, GV = %d" (alength slab-pos) gv))
      (doseq [d [7 13 67 91 469]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d))))))

    (println)

    ;; Slab b=45 (where the ladder lands)
    (println "  ── Slab b=45 (post-jubilee, where ladder lands) ──")
    (let [slab-pos (c/slab {:a 0 :b 45})
          gv (reduce + (map #(c/gv-at s %) (seq slab-pos)))]
      (println (format "    %d letters, GV = %d" (alength slab-pos) gv))
      (doseq [d [7 13 67 91 469]]
        (when (zero? (mod gv d))
          (println (format "    ÷%d = %d" d (/ gv d))))))

    (println)

    ;; What's the difference?
    (let [gv44 (reduce + (map #(c/gv-at s %) (seq (c/slab {:a 0 :b 44}))))
          gv45 (reduce + (map #(c/gv-at s %) (seq (c/slab {:a 0 :b 45}))))
          diff (Math/abs (- gv44 gv45))]
      (println (format "  Difference: |%d - %d| = %d" gv44 gv45 diff))
      (doseq [d [7 13 67]]
        (when (zero? (mod diff d))
          (println (format "    ÷%d = %d" d (/ diff d))))))))

;; ══════════════════════════════════════════════════════════════
;; Main
;; ══════════════════════════════════════════════════════════════

(defn -main []
  (println "=== 075: HIERARCHY AND LADDER ===")
  (println "  Captains of 1000/100/50/10 and Jacob's Ladder")
  (println "  in the 4D Torah space.\n")

  (c/space)  ;; load

  (part1-captains)
  (part2-ladder)
  (part3-ladder-fibers)
  (part4-steps)
  (part5-convergence)
  (part6-ladder-slab)

  (println "\nDone."))
