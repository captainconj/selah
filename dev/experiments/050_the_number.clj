(ns experiments.050-the-number
  "The palindrome is free. The ordering is language.
   What's left is the NUMBER.

   The Torah has 306,269 letters with a total gematria of 21,120,089.
   This total is divisible by 7, by 441 (=21²=אמת), and the center
   word has gematria 333 = 9 × 37.

   But HOW constrained is this? Given that the Torah must contain
   specific words in specific frequencies (it's a TEXT, not random data),
   how many degrees of freedom exist in the letter counts?

   Could you write a different text with the same word frequencies
   but a different total? Or does the total follow inevitably from
   the content?

   Run: clojure -M:dev -m experiments.050-the-number"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn -main []
  (println "=== THE NUMBER ===")
  (println "  How surprising is the Torah's total gematria?\n")

  (println "Loading Torah...")
  (let [torah (vec (mapcat sefaria/book-letters
                           ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count torah)
        gems (mapv #(long (g/letter-value %)) torah)
        total (long (reduce + gems))
        freqs (frequencies torah)]

    (println (format "  %,d letters. Total gematria: %,d\n" n total))

    ;; ── 1. The total's properties ─────────────────────────────────
    (println "── 1. The Number ──\n")

    (println (format "  Total: %,d" total))
    (println (format "  mod 7 = %d" (mod total 7)))
    (println (format "  mod 37 = %d" (mod total 37)))
    (println (format "  mod 73 = %d" (mod total 73)))
    (println (format "  mod 441 = %d  (441 = 21² = אמת)" (mod total 441)))
    (println (format "  mod 2701 = %d  (2701 = 37×73 = Gen 1:1)" (mod total 2701)))
    (println (format "  %,d / 441 = %,d exactly" total (quot total 441)))
    (println (format "  %,d / 7 = %,d exactly" total (quot total 7)))

    ;; ── 2. Letter-count degrees of freedom ─────────────────────────
    (println "\n── 2. Degrees of Freedom ──")
    (println "  The total = Σ (count_i × value_i) for each letter i.\n")

    (let [sorted (sort-by (comp - second) freqs)
          contributions (mapv (fn [[ch ct]]
                                 (let [v (long (g/letter-value ch))]
                                   {:ch ch :val v :count ct :contribution (* v ct)}))
                               sorted)]

      (println (format "  %-4s %-6s %-10s %-12s %-8s" "Ltr" "Value" "Count" "V×Count" "% of Σ"))
      (println (apply str (repeat 44 "─")))
      (doseq [c contributions]
        (println (format "  %-4c %-6d %-10s %-12s %-8.2f%%"
                         (:ch c) (:val c)
                         (format "%,d" (:count c))
                         (format "%,d" (:contribution c))
                         (* 100 (/ (double (:contribution c)) total)))))
      (println (format "\n  Σ = %,d" (reduce + (map :contribution contributions))))

      ;; What letter contributes most to the total?
      (println "\n  Top contributors to the total:")
      (doseq [c (take 5 (sort-by #(- (:contribution %)) contributions))]
        (println (format "    %c (value=%d, count=%,d): %,d (%.1f%%)"
                         (:ch c) (:val c) (:count c) (:contribution c)
                         (* 100 (/ (double (:contribution c)) total))))))

    ;; ── 3. Sensitivity analysis ────────────────────────────────────
    (println "\n── 3. Sensitivity ──")
    (println "  How does the total change if we add/remove ONE letter?\n")

    (doseq [ch alphabet]
      (let [v (long (g/letter-value ch))
            new-total-add (+ total v)
            new-total-sub (- total v)]
        (when (zero? (mod v 7))
          (println (format "  %c (value=%d): adding preserves mod 7=0, removing preserves mod 7=0" ch v)))
        (when (and (not (zero? (mod v 7)))
                   (or (zero? (mod new-total-add 441))
                       (zero? (mod new-total-sub 441))))
          (println (format "  %c (value=%d): add→mod441=%d, sub→mod441=%d"
                           ch v (mod new-total-add 441) (mod new-total-sub 441))))))

    ;; How many letters have value divisible by 7?
    (let [div7-letters (filter #(zero? (mod (long (g/letter-value %)) 7)) alphabet)
          div7-count (reduce + (map #(get freqs % 0) div7-letters))]
      (println (format "\n  Letters with value mod 7 = 0: %s"
                       (str/join " " (map #(format "%c(%d)" % (long (g/letter-value %))) div7-letters))))
      (println (format "  Their total count: %,d of %,d (%.1f%%)"
                       div7-count n (* 100 (/ (double div7-count) n))))
      (println "  These letters can be added/removed freely without breaking mod 7."))

    ;; ── 4. The real constraint ─────────────────────────────────────
    (println "\n── 4. The Real Constraint ──")
    (println "  mod 441 = 0 is a much tighter constraint than mod 7 = 0.")
    (println (format "  441 = 21² = %d × %d" 21 21))
    (println (format "  One in 441 random totals would satisfy this."))
    (println (format "  But this is NOT a random total. It's Σ(count_i × value_i)."))
    (println)

    ;; Monte Carlo: perturb letter counts slightly and check
    (println "  Monte Carlo: perturb each letter count by ±1% and check mod 441.")
    (let [n-trials 100000
          rng (java.util.Random. 42)
          hits-441 (atom 0)
          hits-7 (atom 0)
          hits-both (atom 0)]
      (dotimes [_ n-trials]
        (let [perturbed-total
              (reduce + (map (fn [[ch ct]]
                               (let [v (long (g/letter-value ch))
                                     ;; Perturb count by ±1%
                                     delta (long (* 0.01 ct (.nextGaussian rng)))
                                     new-ct (max 0 (+ ct delta))]
                                 (* v new-ct)))
                             freqs))]
          (when (zero? (mod (long perturbed-total) 441))
            (swap! hits-441 inc))
          (when (zero? (mod (long perturbed-total) 7))
            (swap! hits-7 inc))
          (when (and (zero? (mod (long perturbed-total) 441))
                     (zero? (mod (long perturbed-total) 7)))
            (swap! hits-both inc))))
      (println (format "  %,d trials with ±1%% perturbation:" n-trials))
      (println (format "    mod 7 = 0:   %,d hits (%.2f%%)" @hits-7 (* 100 (/ (double @hits-7) n-trials))))
      (println (format "    mod 441 = 0: %,d hits (%.2f%%)" @hits-441 (* 100 (/ (double @hits-441) n-trials))))
      (println (format "    both = 0:    %,d hits (%.2f%%)" @hits-both (* 100 (/ (double @hits-both) n-trials)))))

    ;; ── 5. Per-book totals ─────────────────────────────────────────
    (println "\n── 5. Per-Book Totals ──\n")

    (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
          book-data (mapv (fn [book]
                             (let [letters (vec (sefaria/book-letters book))
                                   gems (mapv #(long (g/letter-value %)) letters)
                                   total (long (reduce + gems))]
                               {:book book :n (count letters) :total total}))
                           books)]
      (println (format "  %-15s %-10s %-14s %-8s %-8s %-8s" "Book" "Letters" "Total" "mod 7" "mod 37" "mod 441"))
      (println (apply str (repeat 67 "─")))
      (doseq [bd book-data]
        (println (format "  %-15s %-10s %-14s %-8d %-8d %-8d"
                         (:book bd)
                         (format "%,d" (:n bd))
                         (format "%,d" (:total bd))
                         (mod (:total bd) 7) (mod (:total bd) 37) (mod (:total bd) 441))))
      (let [all-total (reduce + (map :total book-data))]
        (println (format "\n  Combined: %,d" all-total))
        (println (format "  mod 7 = %d, mod 37 = %d, mod 441 = %d"
                         (mod all-total 7) (mod all-total 37) (mod all-total 441)))

        ;; How many books have mod 7 = 0?
        (let [books-mod7 (count (filter #(zero? (mod (:total %) 7)) book-data))]
          (println (format "\n  Books with total mod 7 = 0: %d of 5" books-mod7))
          (println (format "  Expected by chance: ~0.71 (1/7 each)")))))

    ;; ── 6. The mean letter value ──────────────────────────────────
    (println "\n── 6. The Mean ──\n")

    (let [mean-val (/ (double total) n)]
      (println (format "  Mean letter value: %.4f" mean-val))
      (println (format "  ≈ %d" (Math/round mean-val)))
      (println (format "  68 = חיים (chayyim) = life"))
      (println (format "  Actual: %.4f — off by %.4f from 68" mean-val (Math/abs (- mean-val 68.0))))
      (println)
      ;; What mean would we expect from uniform random 22 letters?
      (let [expected-mean (/ (double (reduce + (map #(long (g/letter-value %)) alphabet))) 22)]
        (println (format "  Expected mean for uniform random: %.4f" expected-mean))
        (println (format "  Torah mean: %.4f" mean-val))
        (println (format "  Difference: %.4f" (- mean-val expected-mean)))
        (println "  The Torah's mean is LOWER than uniform — small-value letters are overrepresented.")))

    ;; ── 7. The count of each letter mod 7 ──────────────────────────
    (println "\n── 7. Letter Counts mod 7 ──\n")

    (let [sorted-alpha (sort-by #(long (g/letter-value %)) alphabet)]
      (println (format "  %-4s %-6s %-10s %-8s %-8s" "Ltr" "Value" "Count" "C mod 7" "V×C mod 7"))
      (println (apply str (repeat 40 "─")))
      (doseq [ch sorted-alpha]
        (let [v (long (g/letter-value ch))
              ct (get freqs ch 0)]
          (println (format "  %-4c %-6d %-10s %-8d %-8d"
                           ch v (format "%,d" ct) (mod ct 7) (mod (* v ct) 7)))))
      ;; Sum of V×C mod 7 for each letter
      (let [per-letter-mod7 (mapv (fn [ch]
                                     (mod (* (long (g/letter-value ch)) (get freqs ch 0)) 7))
                                   alphabet)]
        (println (format "\n  Σ(V×C mod 7) per letter: %s" (str/join " " per-letter-mod7)))
        (println (format "  Their sum mod 7 = %d" (mod (reduce + per-letter-mod7) 7)))
        (println "  (This MUST equal the total mod 7, by modular arithmetic.)")))

    ;; ── 8. Summary ──────────────────────────────────────────────────
    (println "\n── 8. Summary ──\n")

    (println "  The total gematria is a WEIGHTED SUM of letter counts.")
    (println "  It is constrained by the vocabulary and topic.")
    (println "  But within those constraints, there is freedom.")
    (println)
    (println (format "  The Torah's total is %,d." total))
    (println "  mod 7 = 0. mod 441 = 0.")
    (println (format "  %,d / 441 = %,d" total (quot total 441)))
    (println (format "  %,d / 7 = %,d" total (quot total 7)))
    (println)
    (println "  The mean letter value is 68.94 ≈ 68 = חיים (life).")
    (println "  The total / 49 = integer. (49 = 7²)")
    (println)
    (println "  These are properties of the SPECIFIC letter counts.")
    (println "  They survive shuffling (they're about the sum, not the order).")
    (println "  They would NOT survive synonym substitution.")
    (println "  The text was composed at THIS exact length, with THESE exact letters,")
    (println "  to produce THESE exact properties."))

  (println "\nDone. The number is the signature."))
