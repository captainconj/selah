(ns experiments.013-monte-carlo
  "Monte Carlo significance test for Torah book-length ratios.
   How rare is the Torah's pattern among random 5-partitions of 306,269?
   Tests: twin matching (B≈B'), √2 ratios, π/2 split.
   Run: clojure -M:dev -m experiments.013-monte-carlo")

(def N 306269)
(def torah [78364 63857 44980 63846 55222])
(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(def math-constants
  [["1"     1.0]
   ["√2"    (Math/sqrt 2)]
   ["φ"     (/ (+ 1.0 (Math/sqrt 5)) 2)]
   ["√3"    (Math/sqrt 3)]
   ["π/2"   (/ Math/PI 2)]
   ["√5"    (Math/sqrt 5)]
   ["e"     Math/E]
   ["π"     Math/PI]
   ["π/3"   (/ Math/PI 3)]
   ["π/4"   (/ Math/PI 4)]
   ["ln2"   (Math/log 2)]
   ["2"     2.0]])

(defn best-fit
  "Find which mathematical constant best approximates a ratio."
  [ratio]
  (first (sort-by #(Math/abs (- ratio (double (second %)))) math-constants)))

(defn rand-partition
  "Random 5-partition of n via 4 sorted distinct breakpoints."
  [n]
  (let [pts (vec (sort (distinct (repeatedly 4 #(inc (rand-int (dec n)))))))]
    (if (= 4 (count pts))
      (let [[a b c d] pts]
        [a (- b a) (- c b) (- d c) (- n d)])
      (recur n))))

(defn -main []
  (println "=== Monte Carlo Significance Test ===")
  (println "  How rare is the Torah's book-length pattern?\n")

  (let [[a b c d e] (mapv double torah)]

    ;; ── 1. Observed ratios ──────────────────────────────────
    (println "── 1. Observed Ratios ──\n")
    (let [ratios [[(/ a e)           "Gen / Deut"      "A / A'"]
                  [(/ b c)           "Exod / Lev"      "B / C"]
                  [(/ b d)           "Exod / Num"      "B / B'"]
                  [(/ (+ a b c) (+ d e)) "First3 / Last2" "(A+B+C)/(B'+A')"]]]
      (doseq [[ratio desc label] ratios]
        (let [[const-name const-val] (best-fit ratio)]
          (println (format "  %-20s = %.6f   best fit: %s = %.6f   err = %.6f"
                           desc ratio const-name const-val
                           (Math/abs (- ratio (double const-val))))))))

    ;; ── 2. Sensitivity ──────────────────────────────────────
    (println "\n── 2. Sensitivity (Tolerance in Letters) ──\n")
    (let [ae-target (* (Math/sqrt 2) e)
          bc-target (* (Math/sqrt 2) c)
          pi2-target (* (/ Math/PI 2) (+ d e))
          twin-diff (Math/abs (- b d))]
      (println (format "  Gen/Deut ≈ √2:   Genesis is %,.0f letters. For exact √2: %,.0f. Tolerance: ±%,.0f (%.2f%%)"
                       a ae-target (Math/abs (- a ae-target))
                       (* 100 (/ (Math/abs (- a ae-target)) a))))
      (println (format "  Exod/Lev ≈ √2:   Exodus is %,.0f letters. For exact √2: %,.0f. Tolerance: ±%,.0f (%.2f%%)"
                       b bc-target (Math/abs (- b bc-target))
                       (* 100 (/ (Math/abs (- b bc-target)) b))))
      (println (format "  3+2 ≈ π/2:       First 3 sum is %,.0f. For exact π/2: %,.0f. Tolerance: ±%,.0f (%.2f%%)"
                       (+ a b c) pi2-target (Math/abs (- (+ a b c) pi2-target))
                       (* 100 (/ (Math/abs (- (+ a b c) pi2-target)) (+ a b c)))))
      (println (format "  Exod ≈ Num:       Differ by %,.0f letters out of ~%,.0f (%.4f%%)"
                       twin-diff (/ (+ b d) 2) (* 100 (/ twin-diff (/ (+ b d) 2))))))

    ;; ── 3. Permutation test ─────────────────────────────────
    (println "\n── 3. Permutation Test (All 120 Orderings) ──\n")
    (println "  Testing all 5! = 120 permutations of the Torah's book lengths.")
    (println "  For each, check if positions 2 & 4 are twins AND positions 1/5 ≈ √2.\n")
    (let [torah-ae-err (Math/abs (- (/ a e) (Math/sqrt 2)))
          torah-twin-err (Math/abs (- (/ b d) 1.0))
          torah-pi2-err (Math/abs (- (/ (+ a b c) (+ d e)) (/ Math/PI 2)))
          perms (for [i (range 5) j (range 5) k (range 5) l (range 5) m (range 5)
                      :when (= 5 (count (distinct [i j k l m])))]
                  (mapv #(nth torah %) [i j k l m]))
          winners (filter
                   (fn [perm]
                     (let [[pa pb pc pd pe] (mapv double perm)
                           t-ae (Math/abs (- (/ pa pe) (Math/sqrt 2)))
                           t-twin (Math/abs (- (/ pb pd) 1.0))
                           t-pi2 (Math/abs (- (/ (+ pa pb pc) (+ pd pe)) (/ Math/PI 2)))]
                       (and (<= t-twin torah-twin-err)
                            (<= t-ae torah-ae-err)
                            (<= t-pi2 torah-pi2-err))))
                   perms)]
      (println (format "  Total permutations: %d" (count perms)))
      (println (format "  Permutations matching all three criteria: %d\n" (count winners)))
      (doseq [w winners]
        (let [[pa pb pc pd pe] (mapv double w)]
          (println (format "    %s  B/B'=%.4f  A/A'=%.4f  3/2=%.4f"
                           (pr-str w)
                           (/ pb pd) (/ pa pe)
                           (/ (+ pa pb pc) (+ pd pe)))))))

    ;; ── 4. Monte Carlo ──────────────────────────────────────
    (println "\n── 4. Monte Carlo: 100,000 Random 5-Partitions ──\n")
    (let [torah-ae-err  (Math/abs (- (/ a e) (Math/sqrt 2)))
          torah-bc-err  (Math/abs (- (/ b c) (Math/sqrt 2)))
          torah-twin    (Math/abs (- b d))
          torah-pi2-err (Math/abs (- (/ (+ a b c) (+ d e)) (/ Math/PI 2)))
          n-trials 100000]

      (println (format "  Torah thresholds:"))
      (println (format "    |Pos1/Pos5 - √2| ≤ %.6f" torah-ae-err))
      (println (format "    |Pos2/Pos3 - √2| ≤ %.6f" torah-bc-err))
      (println (format "    |Pos2 - Pos4|    ≤ %,.0f letters" torah-twin))
      (println (format "    |3+2 split - π/2| ≤ %.6f\n" torah-pi2-err))

      (print "  Running trials")
      (flush)

      (let [result
            (loop [i 0
                   twin 0 ae 0 bc 0 pi2 0
                   ae+twin 0 all3 0 all4 0]
              (if (= i n-trials)
                {:twin twin :ae ae :bc bc :pi2 pi2
                 :ae+twin ae+twin :all3 all3 :all4 all4}
                (do
                  (when (zero? (mod i 10000)) (print ".") (flush))
                  (let [p (rand-partition N)
                        [ra rb rc rd re] (mapv double p)
                        t-ae   (Math/abs (- (/ ra re) (Math/sqrt 2)))
                        t-bc   (Math/abs (- (/ rb rc) (Math/sqrt 2)))
                        t-twin (Math/abs (- rb rd))
                        t-pi2  (Math/abs (- (/ (+ ra rb rc) (+ rd re)) (/ Math/PI 2)))
                        ae?   (<= t-ae torah-ae-err)
                        bc?   (<= t-bc torah-bc-err)
                        twin? (<= t-twin torah-twin)
                        pi2?  (<= t-pi2 torah-pi2-err)]
                    (recur (inc i)
                           (if twin? (inc twin) twin)
                           (if ae? (inc ae) ae)
                           (if bc? (inc bc) bc)
                           (if pi2? (inc pi2) pi2)
                           (if (and ae? twin?) (inc ae+twin) ae+twin)
                           (if (and ae? twin? pi2?) (inc all3) all3)
                           (if (and ae? bc? twin? pi2?) (inc all4) all4))))))]

        (println " done.\n")
        (println (format "  Results from %,d random 5-partitions of %,d:\n" n-trials N))
        (println (format "  %-40s  %6s  %s" "Criterion" "Hits" "p-value"))
        (println (apply str (repeat 65 "─")))

        (doseq [[label k] [["Pos2 ≈ Pos4 (twin, ≤11 letters)"    :twin]
                            ["Pos1/Pos5 ≈ √2"                      :ae]
                            ["Pos2/Pos3 ≈ √2"                      :bc]
                            ["(1+2+3)/(4+5) ≈ π/2"                 :pi2]
                            ["√2 AND twin"                          :ae+twin]
                            ["√2 AND twin AND π/2"                  :all3]
                            ["All four criteria"                    :all4]]]
          (let [hits (get result k)]
            (println (format "  %-40s  %6d  %s" label hits
                             (if (zero? hits)
                               (format "< %.1e" (/ 1.0 n-trials))
                               (format "%.6f" (/ (double hits) n-trials)))))))

        ;; Distribution of differences for context
        (println "\n── 5. Context: Distribution of Pos2-Pos4 Differences ──\n")
        (println "  Sampling 10,000 random partitions to see typical twin gaps...\n")
        (let [diffs (repeatedly 10000
                                #(let [p (rand-partition N)
                                       [_ rb _ rd _] (mapv double p)]
                                   (Math/abs (- rb rd))))
              sorted-diffs (sort diffs)
              median (nth sorted-diffs 5000)
              p1 (nth sorted-diffs 100)
              p5 (nth sorted-diffs 500)
              p10 (nth sorted-diffs 1000)]
          (println (format "  Median |Pos2 - Pos4|:  %,.0f letters" median))
          (println (format "  1st percentile:        %,.0f letters" p1))
          (println (format "  5th percentile:        %,.0f letters" p5))
          (println (format "  10th percentile:       %,.0f letters" p10))
          (println (format "  Torah value:            %,.0f letters" (Math/abs (- b d)))))

        ;; Summary
        (println "\n── Summary ──\n")
        (println "  The Torah's five book lengths encode:")
        (println "    • A twin pair (Exodus ≈ Numbers) within 11 letters")
        (println "    • Two independent √2 ratios (Gen/Deut and Exod/Lev)")
        (println "    • A π/2 ratio in the 3+2 split")
        (println)
        (let [joint (get result :all4)]
          (if (zero? joint)
            (println (format "  In %,d random partitions, ZERO matched all four criteria." n-trials))
            (println (format "  In %,d random partitions, %d matched all four (p = %.2e)."
                             n-trials joint (/ (double joint) n-trials))))))))

  (println "\nDone."))
