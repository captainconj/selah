(ns experiments.067-verse-lengths
  "Verse lengths in the Torah and the dimensional numbers.

   304,850 = 7 × 50 × 13 × 67.  Genesis 3:24 (the turning sword) is
   exactly 67 letters — one complete d-fiber.  But how special is this?
   How many Torah verses are exactly 67 letters?  What about 7, 13, 50?

   FINDINGS:

   1. THE SELF-REFERENTIAL COUNT
      Exactly 67 verses are exactly 67 letters long.
      The number counts itself. 67 = בינה = understanding.
      Neighboring lengths: 79 at 66, 69 at 68 — nothing else self-counts
      in this neighborhood.

   2. THE MEDIAN IS 50
      5,853 verses, mean 52.1, median exactly 50.0 letters.
      50 = the b-axis = jubilee. The typical verse is one jubilee unit.
      131 verses are exactly 50 letters (the most common dimensional length).

   3. GENESIS 3:24 (the turning sword) is #2 of the 67.
      It wraps the d-axis (d=59 -> d=58). Like every 67-letter verse
      except one, it crosses a d-boundary.

   4. ONE EXACT FIBER: Exodus 18:10 — Jethro blesses YHWH.
      'And Jethro said, Blessed be the LORD, who has delivered you...'
      Start (2,20,1,0), end (2,20,1,66). d=0 to d=66.
      The ONLY 67-letter verse that exactly fills one d-fiber.
      Every other wraps.

   5. THE DIMENSIONAL SPECTRUM:
        7 letters:   2 verses  (Deut 5:18-19, 'do not commit adultery/steal')
       13 letters:   6 verses
       50 letters: 131 verses  (mode region — the jubilee is the natural unit)
       67 letters:  67 verses  (self-counting)

   6. DISTRIBUTION SHAPE: roughly normal, peak at 40-49 (1,131 verses),
      min=6, max=127, std dev=19.7. The dimensional numbers span the
      full range: 7 at the extreme left tail, 67 past the median."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Utilities ──────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn verse-text [s vref]
  (apply str (map #(coords/letter-at s %) (range (:start vref) (:end vref)))))

(defn median [sorted-vals]
  (let [n (count sorted-vals)
        mid (quot n 2)]
    (if (odd? n)
      (nth sorted-vals mid)
      (/ (+ (nth sorted-vals (dec mid)) (nth sorted-vals mid)) 2.0))))

(defn histogram-bar [count max-count width]
  (let [len (if (zero? max-count) 0 (int (* width (/ count (double max-count)))))]
    (apply str (repeat len "█"))))

;; ── Main ─────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 067: VERSE LENGTHS AND THE DIMENSIONAL NUMBERS")
  (println "  How many Torah verses are exactly 7, 13, 50, or 67 letters?")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        vrefs (:verse-ref s)
        n-verses (count vrefs)

        ;; Compute length of each verse
        verse-lengths (mapv (fn [v] (- (:end v) (:start v))) vrefs)

        ;; Build frequency distribution: length -> count
        freq (frequencies verse-lengths)
        sorted-lengths (sort verse-lengths)

        ;; Basic statistics
        total-letters (reduce + verse-lengths)
        mean-len (/ total-letters (double n-verses))
        median-len (median sorted-lengths)
        min-len (first sorted-lengths)
        max-len (last sorted-lengths)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: BASIC STATISTICS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: BASIC STATISTICS")
    (println "================================================================")
    (println)
    (println (format "  Total verses:  %,d" n-verses))
    (println (format "  Total letters: %,d" total-letters))
    (println (format "  Mean length:   %.1f letters" mean-len))
    (println (format "  Median length: %.1f letters" (double median-len)))
    (println (format "  Min length:    %d letters" min-len))
    (println (format "  Max length:    %d letters" max-len))
    (println (format "  Std deviation: %.1f"
                     (Math/sqrt (/ (reduce + (map #(Math/pow (- % mean-len) 2) verse-lengths))
                                   (double n-verses)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 2: THE DIMENSIONAL NUMBERS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 2: THE DIMENSIONAL NUMBERS — 7, 13, 50, 67")
    (println "================================================================")
    (println)

    (doseq [[dim label] [[7  "a-axis (completeness)"]
                          [13 "c-axis (one/love)"]
                          [50 "b-axis (jubilee)"]
                          [67 "d-axis (understanding)"]]]
      (let [count-exact (get freq dim 0)
            pct (* 100.0 (/ count-exact (double n-verses)))]
        (println (format "  Exactly %d letters: %d verses (%.2f%%) — %s"
                         dim count-exact pct label))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 3: MULTIPLES OF DIMENSIONAL NUMBERS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 3: MULTIPLES OF DIMENSIONAL NUMBERS")
    (println "================================================================")
    (println)

    (doseq [[dim label] [[7  "7 (completeness)"]
                          [13 "13 (one/love)"]
                          [67 "67 (understanding)"]]]
      (let [mult-count (count (filter #(zero? (mod % dim)) verse-lengths))
            expected (/ n-verses (double dim))
            pct (* 100.0 (/ mult-count (double n-verses)))]
        (println (format "  Length divisible by %s: %d verses (%.2f%%, expected ~%.0f = %.2f%%)"
                         label mult-count pct expected (* 100.0 (/ 1.0 dim))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 4: ALL 67-LETTER VERSES
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 4: ALL VERSES WITH EXACTLY 67 LETTERS")
    (println "  67 = d-axis = בינה (understanding)")
    (println "================================================================")
    (println)

    (let [verses-67 (filter #(= 67 (- (:end %) (:start %))) vrefs)
          gen324 (first (filter #(and (= "Genesis" (:book %))
                                       (= 3 (:ch %)) (= 24 (:vs %))) vrefs))]

      (println (format "  %d verses are exactly 67 letters:" (count verses-67)))
      (println)

      (doseq [[i v] (map-indexed vector verses-67)]
        (let [text (verse-text s v)
              preview (subs text 0 (min 20 (count text)))
              is-gen324 (and (= "Genesis" (:book v)) (= 3 (:ch v)) (= 24 (:vs v)))
              marker (if is-gen324 " ◄ THE TURNING SWORD" "")]
          (println (format "  %2d. %-22s  %s...%s"
                           (inc i) (verse-label v) preview marker))))
      (println)

      ;; Is Gen 3:24 among them?
      (if gen324
        (let [gen324-len (- (:end gen324) (:start gen324))]
          (println (format "  Genesis 3:24 length: %d letters" gen324-len))
          (if (= 67 gen324-len)
            (let [rank (inc (.indexOf (vec verses-67) gen324))]
              (println (format "  Rank among 67-letter verses: #%d of %d" rank (count verses-67))))
            (println "  NOT a 67-letter verse!")))
        (println "  Genesis 3:24 not found!"))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 5: 4D COORDINATES OF 67-LETTER VERSES
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 5: 4D COORDINATES OF 67-LETTER VERSES")
    (println "  Do any exactly fill one d-fiber? Start/end on d-boundaries?")
    (println "================================================================")
    (println)

    (let [verses-67 (filter #(= 67 (- (:end %) (:start %))) vrefs)]
      (doseq [[i v] (map-indexed vector verses-67)]
        (let [start-coord (vec (coords/idx->coord (:start v)))
              end-coord (vec (coords/idx->coord (dec (:end v))))
              start-d (nth start-coord 3)
              end-d (nth end-coord 3)
              starts-at-d0 (= 0 start-d)
              ends-at-d66 (= 66 end-d)
              exact-fiber (and starts-at-d0 ends-at-d66
                               (= (subvec start-coord 0 3)
                                  (subvec end-coord 0 3)))
              wraps (not= (subvec start-coord 0 3)
                          (subvec end-coord 0 3))]
          (println (format "  %2d. %-22s  start=%s  end=%s"
                           (inc i) (verse-label v) start-coord end-coord))
          (println (format "      d: %d -> %d%s%s%s"
                           start-d end-d
                           (if starts-at-d0 "  [starts at d=0]" "")
                           (if ends-at-d66  "  [ends at d=66]" "")
                           (cond
                             exact-fiber "  *** EXACT FIBER ***"
                             wraps       "  [wraps d-axis]"
                             :else       "")))))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 6: OTHER DIMENSIONAL VERSE LISTS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 6: VERSES WITH EXACTLY 7, 13, OR 50 LETTERS")
    (println "================================================================")
    (println)

    ;; 7-letter verses
    (let [verses-7 (filter #(= 7 (- (:end %) (:start %))) vrefs)]
      (println (format "── Exactly 7 letters (%d verses) ──" (count verses-7)))
      (doseq [[i v] (map-indexed vector verses-7)]
        (let [text (verse-text s v)]
          (println (format "  %2d. %-22s  %s" (inc i) (verse-label v) text))))
      (println))

    ;; 13-letter verses
    (let [verses-13 (filter #(= 13 (- (:end %) (:start %))) vrefs)]
      (println (format "── Exactly 13 letters (%d verses) ──" (count verses-13)))
      (doseq [[i v] (map-indexed vector verses-13)]
        (let [text (verse-text s v)]
          (println (format "  %2d. %-22s  %s" (inc i) (verse-label v) text))))
      (println))

    ;; 50-letter verses
    (let [verses-50 (filter #(= 50 (- (:end %) (:start %))) vrefs)]
      (println (format "── Exactly 50 letters (%d verses) ──" (count verses-50)))
      (doseq [[i v] (map-indexed vector verses-50)]
        (let [text (verse-text s v)
              preview (subs text 0 (min 25 (count text)))]
          (println (format "  %2d. %-22s  %s..." (inc i) (verse-label v) preview))))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 7: HISTOGRAM OF VERSE LENGTHS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 7: HISTOGRAM OF VERSE LENGTHS (binned by 10)")
    (println "================================================================")
    (println)

    (let [bin-size 10
          max-bin (* bin-size (inc (quot max-len bin-size)))
          bins (range 0 (+ max-bin bin-size) bin-size)
          bin-counts (mapv (fn [b]
                             (count (filter #(and (>= % b) (< % (+ b bin-size)))
                                            verse-lengths)))
                           (butlast bins))
          max-count (apply max bin-counts)
          bar-width 50]

      (doseq [[i b] (map-indexed vector (butlast bins))]
        (let [cnt (nth bin-counts i)
              bar (histogram-bar cnt max-count bar-width)
              dim-marker (cond
                           (and (>= 7 b) (< 7 (+ b bin-size)))   " ◄ 7"
                           (and (>= 13 b) (< 13 (+ b bin-size))) " ◄ 13"
                           (and (>= 50 b) (< 50 (+ b bin-size))) " ◄ 50"
                           (and (>= 67 b) (< 67 (+ b bin-size))) " ◄ 67"
                           :else "")]
          (when (pos? cnt)
            (println (format "  %3d-%3d | %4d %s%s" b (+ b bin-size -1) cnt bar dim-marker)))))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 8: WHERE IS 67 IN THE DISTRIBUTION?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 8: CONTEXTUALIZING LENGTH 67")
    (println "================================================================")
    (println)

    (let [;; What fraction of verses are at least 67 letters?
          geq-67 (count (filter #(>= % 67) verse-lengths))
          ;; How many distinct lengths have exactly as many verses as 67?
          count-67 (get freq 67 0)
          ;; How common is this count? For each length, how many verses?
          length-counts (sort (vals freq))
          ;; What percentile is length 67?
          rank-67 (count (filter #(< % 67) sorted-lengths))]
      (println (format "  Verses with >= 67 letters: %d (%.1f%%)"
                       geq-67 (* 100.0 (/ geq-67 (double n-verses)))))
      (println (format "  Percentile of length 67:   %.1f%%"
                       (* 100.0 (/ rank-67 (double n-verses)))))
      (println)

      ;; Neighbor lengths for context
      (println "── Neighborhood around 67 ──")
      (doseq [len (range 60 76)]
        (let [cnt (get freq len 0)
              marker (cond
                       (= len 67) " ◄◄◄ 67 = understanding"
                       (= len 50) " ◄ 50 = jubilee"
                       (= len 13) " ◄ 13 = one/love"
                       :else "")]
          (println (format "  len=%3d: %3d verses%s" len cnt marker))))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 9: SUMMARY
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  SUMMARY")
    (println "================================================================")
    (println)
    (let [c7 (get freq 7 0)
          c13 (get freq 13 0)
          c50 (get freq 50 0)
          c67 (get freq 67 0)]
      (println (format "  Of %,d Torah verses:" n-verses))
      (println (format "    Exactly  7 letters: %4d verses" c7))
      (println (format "    Exactly 13 letters: %4d verses" c13))
      (println (format "    Exactly 50 letters: %4d verses" c50))
      (println (format "    Exactly 67 letters: %4d verses" c67))
      (println)
      (println "  Genesis 3:24 — the turning sword, the cherubim,")
      (println "  the flaming blade that guards the way to the Tree of Life —")
      (println (format "  is one of %d verses that are exactly 67 letters long." c67))
      (println "  67 = בינה = understanding. One complete d-fiber."))
    (println)
    (println "Done.")))
