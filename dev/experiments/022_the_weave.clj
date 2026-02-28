(ns experiments.022-the-weave
  "The seven threads woven through the Torah.
   Every 7th letter forms a sub-sequence.
   Does each thread carry different information?
   Do they encode something when read together?
   The Menorah hypothesis: 7 branches, one light.
   Run: clojure -M:dev -m experiments.022-the-weave"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn bin-average [v n-bins]
  (let [src-n (count v)
        bin-sz (/ (double src-n) n-bins)]
    (mapv (fn [i]
            (let [start (int (* i bin-sz))
                  end   (min src-n (int (* (inc i) bin-sz)))
                  chunk (subvec v start end)]
              (if (empty? chunk) 0.0
                  (/ (reduce + (map double chunk)) (count chunk)))))
          (range n-bins))))

(defn -main []
  (println "=== THE WEAVE ===")
  (println "  Seven threads. One Torah. What do they carry?\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)]

    (println (format "  %,d letters.\n" n))

    ;; Build the 7 threads
    (let [threads (mapv (fn [offset]
                          (let [indices (vec (range offset n 7))
                                thread-letters (mapv #(nth all-letters %) indices)
                                thread-gems (mapv #(nth gem-vals %) indices)]
                            {:offset offset
                             :indices indices
                             :letters thread-letters
                             :gems thread-gems
                             :n (count thread-letters)}))
                        (range 7))]

      ;; ── 1. The Menorah: 7 sums ───────────────────────────────
      (println "── 1. The Menorah: Seven Branch Gematria ──")
      (println "  Each thread's total gematria.\n")

      (let [sums (mapv #(reduce + (:gems %)) threads)
            total (reduce + sums)
            mean-sum (/ (double total) 7)
            center-sum (nth sums 3)]
        (doseq [i (range 7)]
          (let [s (nth sums i)
                bar (apply str (repeat (int (/ (* 40.0 s) (apply max sums))) "█"))]
            (println (format "  Thread %d: %,12d  %s%s"
                             i s bar
                             (if (= i 3) "  ← CENTER" "")))))
        (println (format "\n  Total: %,d" total))
        (println (format "  Mean: %,.0f" mean-sum))
        (println (format "  Center thread: %,d (%.4f of mean)"
                         center-sum (/ (double center-sum) mean-sum)))

        ;; Mirror symmetry of thread sums
        (println "\n  Thread mirrors:")
        (doseq [i (range 3)]
          (let [j (- 6 i)
                sa (nth sums i) sb (nth sums j)
                diff (Math/abs (- sa sb))
                sum-pair (+ sa sb)]
            (println (format "    Thread %d + Thread %d: %,d + %,d = %,d  |diff| = %,d (%.3f%%)"
                             i j sa sb sum-pair diff
                             (* 100 (/ (double diff) (/ (+ (double sa) sb) 2.0)))))))
        (println (format "    Thread 3 (center): %,d" center-sum))

        ;; Do the sums form a palindromic pattern?
        (let [first3 (mapv double (subvec sums 0 3))
              last3  (mapv double (vec (reverse (subvec sums 4 7))))
              cos-pal (cosine-sim first3 last3)]
          (println (format "\n  Palindrome of thread sums (0-2 vs rev 4-6): cos = %.6f" cos-pal)))

        ;; Numerological check
        (println (format "\n  Total mod 7: %d" (mod total 7)))
        (println (format "  Total mod 49: %d" (mod total 49)))
        (println (format "  Total mod 343 (7³): %d" (mod total 343)))
        (println (format "  Total / 7: %,d" (quot total 7)))
        (println (format "  Total / 49: %,d remainder %d" (quot total 49) (mod total 49))))

      ;; ── 2. What's unique in each thread? ──────────────────────
      (println "\n── 2. Thread Signatures ──")
      (println "  Letter frequency DEVIATIONS from the Torah mean.\n")

      (let [torah-freqs (frequencies all-letters)
            torah-profs (mapv (fn [c] (/ (double (get torah-freqs c 0)) n)) alphabet)]

        ;; For each thread, which letters are over/under-represented?
        (doseq [t threads]
          (let [{:keys [offset letters n]} t
                t-freqs (frequencies letters)
                t-profs (mapv (fn [c] (/ (double (get t-freqs c 0)) n)) alphabet)
                deviations (mapv (fn [i]
                                   (let [expected (nth torah-profs i)
                                         actual (nth t-profs i)
                                         dev (if (pos? expected)
                                               (* 100 (/ (- actual expected) expected))
                                               0.0)]
                                     {:letter (nth alphabet i) :dev dev
                                      :value (g/letter-value (nth alphabet i))}))
                                 (range 22))
                most-over  (first (sort-by (comp - :dev) deviations))
                most-under (first (sort-by :dev deviations))]
            (println (format "  Thread %d: most over-represented: %s (%+.2f%%, value %d)  most under: %s (%+.2f%%, value %d)"
                             offset
                             (:letter most-over) (:dev most-over) (:value most-over)
                             (:letter most-under) (:dev most-under) (:value most-under))))))

      ;; ── 3. Seven windows sliding ─────────────────────────────
      (println "\n── 3. The Seven Windows ──")
      (println "  Does the seven-fold structure appear at every position?")
      (println "  Take every position mod 7. Compare running statistics.\n")

      ;; For each position mod 7 class, compute running gematria
      (let [mod7-gems (mapv (fn [offset]
                               (let [indices (range offset n 7)]
                                 (mapv #(nth gem-vals %) indices)))
                             (range 7))
            ;; Cumulative sum for each class
            mod7-cums (mapv (fn [gems] (vec (reductions + gems))) mod7-gems)
            ;; Sample at 100 points
            min-len (apply min (map count mod7-gems))
            step (quot min-len 100)
            ;; Compare running curves
            samples (mapv (fn [cums]
                            (mapv #(double (nth cums (* % step))) (range 100)))
                          mod7-cums)]

        ;; How similar are the 7 running curves?
        (println "  Pairwise cosine of running gematria curves (100 samples):")
        (let [all-cos (for [i (range 7) j (range (inc i) 7)]
                        (cosine-sim (nth samples i) (nth samples j)))
              mean-cos (/ (reduce + all-cos) (count all-cos))
              min-cos (apply min all-cos)
              max-cos (apply max all-cos)]
          (println (format "    Mean: %.6f  Min: %.6f  Max: %.6f" mean-cos min-cos max-cos)))

        ;; Do the curves diverge or stay parallel?
        (println "\n  Divergence at each quintile:")
        (doseq [q [20 40 60 80 99]]
          (let [vals-at-q (mapv #(nth % q) samples)
                mn (apply min vals-at-q)
                mx (apply max vals-at-q)
                spread (* 100 (/ (- mx mn) (/ (+ mx mn) 2.0)))]
            (println (format "    At %d%%: spread = %.3f%%" q spread)))))

      ;; ── 4. The 441 investigation ─────────────────────────────
      (println "\n── 4. The Number 441 ──")
      (println "  Center letters of the 7 seals sum to 441 = 21² = (7×3)².")
      (println "  Where else does 441 appear?\n")

      (let [seg-size (quot n 7)
            center-letters (mapv (fn [i]
                                    (let [start (* i seg-size)
                                          end (if (= i 6) n (* (inc i) seg-size))
                                          mid (+ start (quot (- end start) 2))]
                                      (nth all-letters mid)))
                                  (range 7))
            center-gems (mapv #(long (g/letter-value %)) center-letters)]

        (println (format "  Center letters: %s" (apply str center-letters)))
        (println (format "  Center values:  %s" (str center-gems)))
        (println (format "  Sum: %d = %d² = (7×3)²" (reduce + center-gems)
                         (long (Math/sqrt (reduce + center-gems)))))

        ;; Search for 441 in running windows
        (println "\n  Windows of 7 consecutive letters that sum to 441:")
        (let [hits (atom 0)]
          (doseq [i (range (- n 6))]
            (let [window (subvec gem-vals i (+ i 7))
                  s (reduce + window)]
              (when (= s 441)
                (swap! hits inc)
                (when (<= @hits 20)
                  (let [letters (subvec all-letters i (+ i 7))]
                    (println (format "    Position %,6d: %s = %s" i
                                     (apply str letters) (str (vec window)))))))))
          (println (format "  Total 7-letter windows summing to 441: %,d" @hits))

          ;; Expected count if random
          ;; Mean gem value ≈ 69, so mean of 7 ≈ 483, SD ≈ sqrt(7) * SD_single
          ;; This is a rough estimate
          (let [mean-gem (/ (double (reduce + gem-vals)) n)
                var-gem (/ (reduce + (map #(Math/pow (- (double %) mean-gem) 2) gem-vals)) n)
                sd-gem (Math/sqrt var-gem)
                ;; For 7 consecutive (with autocorrelation), mean sum ≈ 7*mean, SD ≈ sqrt(7)*SD
                expected-mean (* 7 mean-gem)
                expected-sd (* (Math/sqrt 7) sd-gem)]
            (println (format "  Expected sum of 7 letters: mean=%.0f, sd=%.0f" expected-mean expected-sd))
            (println (format "  441 is %.1f standard deviations from mean" (/ (- 441 expected-mean) expected-sd))))))

      ;; ── 5. The creation days mapping ─────────────────────────
      (println "\n── 5. Do the 7 Seals Map to the 7 Days? ──")
      (println "  Genesis 1 contains 7 days of creation.")
      (println "  Compare each day's letter profile to the corresponding seal.\n")

      ;; Extract the 7 days from Genesis 1
      (let [gen1 (sefaria/fetch-chapter "Genesis" 1)
            ;; Days roughly map to verses:
            ;; Day 1: v1-5, Day 2: v6-8, Day 3: v9-13,
            ;; Day 4: v14-19, Day 5: v20-23, Day 6: v24-31, Day 7: Gen 2:1-3
            day-verse-ranges [[1 5] [6 8] [9 13] [14 19] [20 23] [24 31]]
            gen2 (sefaria/fetch-chapter "Genesis" 2)
            seg-size (quot n 7)]

        (println (format "  %4s  %8s  %12s  %8s  %s"
                         "Day" "Letters" "Gematria" "Mean gem" "cos(Seal)"))
        (println (apply str (repeat 55 "─")))

        (doseq [d (range 6)]
          (let [[v-start v-end] (nth day-verse-ranges d)
                verses (subvec (vec gen1) (dec v-start) v-end)
                day-text (apply str (map norm/strip-html verses))
                day-letters (norm/letter-stream day-text)
                day-gems (mapv #(long (g/letter-value %)) day-letters)
                day-prof (letter-profile day-letters)
                ;; Compare to corresponding seal
                seal-start (* d seg-size)
                seal-end (if (= d 6) n (* (inc d) seg-size))
                seal-letters (subvec all-letters seal-start seal-end)
                seal-prof (letter-profile seal-letters)
                cos-val (if (and day-prof seal-prof) (cosine-sim day-prof seal-prof) 0.0)]
            (println (format "  %4d  %,8d  %,12d  %8.2f  %.4f"
                             (inc d) (count day-letters) (reduce + day-gems)
                             (/ (double (reduce + day-gems)) (count day-letters))
                             cos-val))))

        ;; Day 7 from Gen 2:1-3
        (let [day7-verses (subvec (vec gen2) 0 3)
              day7-text (apply str (map norm/strip-html day7-verses))
              day7-letters (norm/letter-stream day7-text)
              day7-gems (mapv #(long (g/letter-value %)) day7-letters)
              day7-prof (letter-profile day7-letters)
              seal7-letters (subvec all-letters (* 6 seg-size))
              seal7-prof (letter-profile seal7-letters)
              cos-val (if (and day7-prof seal7-prof) (cosine-sim day7-prof seal7-prof) 0.0)]
          (println (format "  %4d  %,8d  %,12d  %8.2f  %.4f"
                           7 (count day7-letters) (reduce + day7-gems)
                           (/ (double (reduce + day7-gems)) (count day7-letters))
                           cos-val)))

        ;; Compare day profiles to each other — chiastic?
        (println "\n  Cross-comparison: each day's profile vs every seal:")
        (print "        ")
        (doseq [s (range 7)] (print (format " Seal %d " s)))
        (println)
        (println (apply str (repeat 65 "─")))

        (let [day-profiles
              (conj
               (mapv (fn [d]
                       (let [[v-start v-end] (nth day-verse-ranges d)
                             verses (subvec (vec gen1) (dec v-start) v-end)
                             day-text (apply str (map norm/strip-html verses))
                             day-letters (norm/letter-stream day-text)]
                         (letter-profile day-letters)))
                     (range 6))
               ;; Day 7
               (let [day7-verses (subvec (vec gen2) 0 3)
                     day7-text (apply str (map norm/strip-html day7-verses))
                     day7-letters (norm/letter-stream day7-text)]
                 (letter-profile day7-letters)))

              seal-profiles
              (mapv (fn [i]
                      (let [start (* i seg-size)
                            end (if (= i 6) n (* (inc i) seg-size))]
                        (letter-profile (subvec all-letters start end))))
                    (range 7))]

          (doseq [d (range 7)]
            (print (format "  Day %d " (inc d)))
            (doseq [s (range 7)]
              (let [cos (if (and (nth day-profiles d) (nth seal-profiles s))
                          (cosine-sim (nth day-profiles d) (nth seal-profiles s))
                          0.0)]
                (print (format "  %.4f" cos))))
            (println))))

      ;; ── 6. Thread-internal structure ──────────────────────────
      (println "\n── 6. Thread Internal Structure ──")
      (println "  Does each thread reproduce the palindromic structure?")
      (println "  Split each thread into 5 segments.\n")

      (doseq [t threads]
        (let [{:keys [offset gems n]} t
              seg5-size (quot n 5)
              segs (mapv (fn [i]
                           (let [start (* i seg5-size)
                                 end (if (= i 4) n (* (inc i) seg5-size))]
                             (reduce + (subvec gems start end))))
                         (range 5))
              [s1 s2 s3 s4 s5] (mapv double segs)
              ;; Torah-level patterns
              twin-diff (Math/abs (- s2 s4))
              ae-ratio (/ s1 s5)
              split-ratio (/ (+ s1 s2 s3) (+ s4 s5))]
          (println (format "  Thread %d: segs=%s" offset (str segs)))
          (println (format "    Seg1/Seg5 = %.4f  (√2 = 1.4142, err = %.4f)"
                           ae-ratio (Math/abs (- ae-ratio (Math/sqrt 2)))))
          (println (format "    |Seg2-Seg4| = %,.0f" twin-diff))
          (println (format "    (1+2+3)/(4+5) = %.4f  (π/2 = 1.5708, err = %.4f)"
                           split-ratio (Math/abs (- split-ratio (/ Math/PI 2)))))
          (println)))

      ;; ── 7. The interleave pattern ─────────────────────────────
      (println "── 7. The Interleave ──")
      (println "  Read across all 7 threads position by position.")
      (println "  At position k, take thread[0][k], thread[1][k], ..., thread[6][k].")
      (println "  These 7 letters form a 'chord'. What patterns emerge?\n")

      (let [min-len (apply min (map :n threads))
            ;; Sample chords at regular intervals
            n-samples 1000
            step (quot min-len n-samples)
            chords (mapv (fn [k]
                           (let [pos (* k step)]
                             (mapv (fn [t] (nth (:letters t) pos)) threads)))
                         (range n-samples))
            ;; How many chords have repeated letters?
            repeat-count (count (filter (fn [chord]
                                          (< (count (set chord)) 7))
                                        chords))
            ;; How many chords contain all unique letters?
            unique-count (count (filter (fn [chord]
                                          (= (count (set chord)) 7))
                                        chords))
            ;; Chord gematria sums
            chord-sums (mapv (fn [chord]
                                (reduce + (map #(long (g/letter-value %)) chord)))
                              chords)
            mean-chord (/ (double (reduce + chord-sums)) n-samples)
            ;; How many chord sums are divisible by 7?
            div7-count (count (filter #(zero? (mod % 7)) chord-sums))
            ;; Expected: 1/7 ≈ 14.3%
            expected-div7 (/ n-samples 7.0)]
        (println (format "  Sampled %,d chords (every %,d positions):" n-samples step))
        (println (format "    Chords with repeated letters: %,d (%.1f%%)"
                         repeat-count (* 100 (/ (double repeat-count) n-samples))))
        (println (format "    Chords with 7 unique letters: %,d (%.1f%%)"
                         unique-count (* 100 (/ (double unique-count) n-samples))))
        (println (format "    Mean chord sum: %.1f" mean-chord))
        (println (format "    Chord sums divisible by 7: %,d (%.1f%%, expected %.1f%%)"
                         div7-count
                         (* 100 (/ (double div7-count) n-samples))
                         (* 100 (/ 1.0 7))))

        ;; Show some example chords
        (println "\n  First 10 chords:")
        (doseq [k (range 10)]
          (let [chord (nth chords k)
                s (reduce + (map #(long (g/letter-value %)) chord))]
            (println (format "    %s  sum=%d  mod7=%d"
                             (apply str chord) s (mod s 7)))))

        ;; Is the sequence of chord sums palindromic?
        (let [first-half (subvec chord-sums 0 500)
              second-half-rev (vec (reverse (subvec chord-sums 500)))
              cos-pal (cosine-sim (mapv double first-half)
                                  (mapv double second-half-rev))]
          (println (format "\n  Chord sum sequence palindrome: cos = %.4f" cos-pal))))

      ;; ── 8. The 49 grid ──────────────────────────────────────
      (println "\n── 8. The 49 Grid (7×7) ──")
      (println "  Arrange the Torah into a 7×7 grid of 49 segments.")
      (println "  Each cell is ~6,250 letters.\n")

      (let [cell-size (quot n 49)
            grid (mapv (fn [i]
                         (let [start (* i cell-size)
                               end (if (= i 48) n (* (inc i) cell-size))
                               chunk (subvec gem-vals start end)
                               s (reduce + chunk)]
                           {:cell i :row (quot i 7) :col (mod i 7)
                            :start start :end end
                            :sum s :mean (/ (double s) (count chunk))
                            :letters (- end start)}))
                       (range 49))]

        ;; Display as 7×7 grid of gematria sums
        (println "  Gematria sums (÷1000):")
        (print "        ")
        (doseq [c (range 7)] (print (format "  col %d " c)))
        (println)
        (println (apply str (repeat 65 "─")))
        (doseq [r (range 7)]
          (print (format "  row %d" r))
          (doseq [c (range 7)]
            (let [cell (nth grid (+ (* r 7) c))]
              (print (format "  %6.0f" (/ (double (:sum cell)) 1000)))))
          (println))

        ;; Row sums and column sums
        (println "\n  Row sums:")
        (let [row-sums (mapv (fn [r]
                                (reduce + (map :sum (filter #(= (:row %) r) grid))))
                              (range 7))]
          (doseq [r (range 7)]
            (println (format "    Row %d: %,12d" r (nth row-sums r))))
          ;; Row palindrome
          (let [first3 (mapv double (subvec row-sums 0 3))
                last3 (mapv double (vec (reverse (subvec row-sums 4 7))))
                cos-pal (cosine-sim first3 last3)]
            (println (format "    Row palindrome (0-2 vs rev 4-6): cos = %.6f" cos-pal))))

        (println "\n  Column sums:")
        (let [col-sums (mapv (fn [c]
                                (reduce + (map :sum (filter #(= (:col %) c) grid))))
                              (range 7))]
          (doseq [c (range 7)]
            (println (format "    Col %d: %,12d" c (nth col-sums c))))
          ;; Column palindrome
          (let [first3 (mapv double (subvec col-sums 0 3))
                last3 (mapv double (vec (reverse (subvec col-sums 4 7))))
                cos-pal (cosine-sim first3 last3)]
            (println (format "    Column palindrome (0-2 vs rev 4-6): cos = %.6f" cos-pal))))

        ;; Diagonal sums
        (let [main-diag (mapv (fn [i] (:sum (nth grid (+ (* i 7) i)))) (range 7))
              anti-diag (mapv (fn [i] (:sum (nth grid (+ (* i 7) (- 6 i))))) (range 7))
              main-sum (reduce + main-diag)
              anti-sum (reduce + anti-diag)]
          (println (format "\n  Main diagonal sum: %,d" main-sum))
          (println (format "  Anti-diagonal sum: %,d" anti-sum))
          (println (format "  |Difference|: %,d (%.3f%%)"
                           (Math/abs (- main-sum anti-sum))
                           (* 100 (/ (double (Math/abs (- main-sum anti-sum)))
                                     (/ (+ (double main-sum) anti-sum) 2.0)))))
          ;; Center cell
          (let [center (nth grid 24)]
            (println (format "  Center cell (3,3): sum=%,d, mean=%.2f"
                             (:sum center) (:mean center)))))))

  (println "\nDone. The weave is revealed.")))
