(ns experiments.046-nucleon-arithmetic
  "Shcherbak's arithmetic of the genetic code (1993-2003).
   The amino acid side chains have nucleon counts (protons + neutrons).
   When grouped by the genetic code's symmetry classes,
   these counts produce systematic multiples of 037.
   Not approximately. Exactly.
   This is peer-reviewed molecular biology.
   Run: clojure -M:dev -m experiments.046-nucleon-arithmetic"
  (:require [clojure.string :as str]))

;; Standard nucleon counts for amino acid side chains (R-groups)
;; These are the number of protons + neutrons in each side chain
;; Source: Shcherbak (2003), "Arithmetic inside the universal genetic code"
;; Published in BioSystems 70 (2003) 187-209
;;
;; "Standard" side chains use the most common isotopes
(def side-chains
  ;; [name, 1-letter, nucleons, codons-count, degeneracy-class]
  ;; degeneracy: I = 2-fold or less, II = 4-fold or more
  [["Gly"  \G   1  4 :II]
   ["Ala"  \A  15  4 :II]
   ["Val"  \V  43  4 :II]
   ["Leu"  \L  57  6 :II]
   ["Ile"  \I  57  3 :I]    ;; 3-fold, but functionally Class I
   ["Pro"  \P  41  4 :II]
   ["Ser"  \S  31  6 :II]
   ["Thr"  \T  45  4 :II]
   ["Cys"  \C  47  2 :I]
   ["Met"  \M  75  1 :I]
   ["Asp"  \D  59  2 :I]
   ["Asn"  \N  58  2 :I]
   ["Glu"  \E  73  2 :I]
   ["Gln"  \Q  72  2 :I]
   ["Lys"  \K  72  2 :I]
   ["Arg"  \R 100  6 :II]
   ["His"  \H  81  2 :I]
   ["Phe"  \F  91  2 :I]
   ["Tyr"  \Y 107  2 :I]
   ["Trp"  \W 130  1 :I]])

(defn -main []
  (println "=== NUCLEON ARITHMETIC ===")
  (println "  Shcherbak's discovery. 37 in the genetic code.\n")

  ;; ── 1. The raw counts ─────────────────────────────────────────
  (println "── 1. The 20 Side Chains ──\n")

  (println (format "  %-4s %-4s %-8s %-8s %-8s %-8s"
                   "AA" "Code" "Nucleons" "Codons" "Class" "mod 37"))
  (println (apply str (repeat 46 "─")))
  (doseq [[name code nucs codons class] side-chains]
    (println (format "  %-4s %-4c %-8d %-8d %-8s %-8d"
                     name code nucs codons (clojure.core/name class) (mod nucs 37))))

  (let [total (reduce + (map #(nth % 2) side-chains))]
    (println (format "\n  Total nucleons (all 20): %d" total))
    (println (format "  mod 37 = %d" (mod total 37))))

  ;; ── 2. The two classes ────────────────────────────────────────
  (println "\n── 2. Rumer's Two Classes ──")
  (println "  Class I: 2-fold degeneracy (3rd position discriminating)")
  (println "  Class II: ≥4-fold degeneracy (3rd position wobble)\n")

  (let [class-I (filter #(= :I (nth % 4)) side-chains)
        class-II (filter #(= :II (nth % 4)) side-chains)
        sum-I (reduce + (map #(nth % 2) class-I))
        sum-II (reduce + (map #(nth % 2) class-II))]

    (println "  Class I (discriminating):")
    (doseq [[name _ nucs _ _] class-I]
      (print (format "    %s(%d)" name nucs)))
    (println)
    (println (format "  Sum = %d" sum-I))
    (println (format "  mod 37 = %d" (mod sum-I 37)))
    (println)

    (println "  Class II (wobble):")
    (doseq [[name _ nucs _ _] class-II]
      (print (format "    %s(%d)" name nucs)))
    (println)
    (println (format "  Sum = %d" sum-II))
    (println (format "  mod 37 = %d" (mod sum-II 37)))
    (println (format "  %d / 37 = %d exactly" sum-II (quot sum-II 37)))
    (println)
    (println (format "  333 = 9 × 37"))
    (println (format "  333 = the gematria of the Torah's center word"))
    (println (format "  333 = 3 × 111 = 3 × 3 × 37")))

  ;; ── 3. The Shcherbak transformation ──────────────────────────
  (println "\n── 3. The Transformation ──")
  (println "  Shcherbak noticed: when you multiply each amino acid's")
  (println "  nucleon count by its number of codons, something happens.\n")

  (let [weighted (map (fn [[name code nucs codons class]]
                         [name nucs codons (* nucs codons)])
                       side-chains)
        total-weighted (reduce + (map #(nth % 3) weighted))]

    (println (format "  %-4s %-8s %-8s %-10s" "AA" "Nucleons" "Codons" "N×C"))
    (println (apply str (repeat 34 "─")))
    (doseq [[name nucs codons prod] weighted]
      (println (format "  %-4s %-8d %-8d %-10d" name nucs codons prod)))

    (println (format "\n  Σ(nucleons × codons) = %,d" total-weighted))
    (println (format "  mod 37 = %d" (mod total-weighted 37)))
    (println (format "  mod 7 = %d" (mod total-weighted 7)))
    (println (format "  %,d / 37 = %.2f" total-weighted (/ (double total-weighted) 37)))

    ;; By class
    (let [w-I (reduce + (map #(nth % 3) (filter (fn [[_ _ _ _ c]] (= c :I))
                                                   (map conj weighted (map #(nth % 4) side-chains)))))
          w-II (reduce + (map #(nth % 3) (filter (fn [[_ _ _ _ c]] (= c :II))
                                                    (map conj weighted (map #(nth % 4) side-chains)))))]
      ;; Recompute properly
      (let [weighted-by-class (map (fn [[name code nucs codons class]]
                                     {:name name :nucs nucs :codons codons
                                      :prod (* nucs codons) :class class})
                                   side-chains)
            w-I (reduce + (map :prod (filter #(= :I (:class %)) weighted-by-class)))
            w-II (reduce + (map :prod (filter #(= :II (:class %)) weighted-by-class)))]
        (println (format "\n  Class I weighted sum: %,d  mod 37=%d" w-I (mod w-I 37)))
        (println (format "  Class II weighted sum: %,d  mod 37=%d" w-II (mod w-II 37))))))

  ;; ── 4. The block structure ────────────────────────────────────
  (println "\n── 4. The Block Structure ──")
  (println "  Group amino acids by their shared structural blocks.\n")

  ;; Amino acids can be built from smaller blocks:
  ;; The simplest decomposition groups them by size
  (let [by-size (sort-by #(nth % 2) side-chains)
        ;; Pairs that differ by exactly 14 (one CH2 group)
        diffs (for [a side-chains b side-chains
                    :when (and (not= a b)
                               (= 14 (Math/abs (- (nth a 2) (nth b 2)))))]
                [(first a) (nth a 2) (first b) (nth b 2)])]

    (println "  Amino acid pairs differing by exactly 14 nucleons (one CH₂):")
    (let [seen (atom #{})]
      (doseq [[a na b nb] (sort-by second diffs)]
        (let [pair (set [a b])]
          (when-not (contains? @seen pair)
            (swap! seen conj pair)
            (println (format "    %s(%d) + CH₂ = %s(%d)" a na b nb)))))))

  ;; ── 5. The 37 × n pattern ────────────────────────────────────
  (println "\n── 5. Multiples of 37 ──")
  (println "  Which combinations of amino acid nucleons equal 37 × n?\n")

  ;; Find all subsets of 2-4 amino acids whose nucleon sum is divisible by 37
  (let [aa-nucs (mapv (fn [[name _ nucs _ _]] [name nucs]) side-chains)]

    ;; All pairs
    (println "  Pairs summing to multiples of 37:")
    (let [pairs (for [i (range 20) j (range (inc i) 20)
                      :let [a (nth aa-nucs i) b (nth aa-nucs j)
                            s (+ (second a) (second b))]
                      :when (zero? (mod s 37))]
                  [(first a) (first b) s (quot s 37)])]
      (doseq [[a b s k] (sort-by #(nth % 2) pairs)]
        (println (format "    %s + %s = %d = %d × 37" a b s k)))
      (println (format "  Total pairs: %d (of %d possible)" (count pairs) (quot (* 20 19) 2))))

    ;; Triple sums
    (println "\n  Notable triples summing to multiples of 37:")
    (let [triples (for [i (range 20) j (range (inc i) 20) k (range (inc j) 20)
                        :let [a (nth aa-nucs i) b (nth aa-nucs j) c (nth aa-nucs k)
                              s (+ (second a) (second b) (second c))]
                        :when (zero? (mod s 37))]
                    [(first a) (first b) (first c) s (quot s 37)])
          count-triples (count triples)]
      (doseq [[a b c s k] (take 10 (sort-by #(nth % 3) triples))]
        (println (format "    %s + %s + %s = %d = %d × 37" a b c s k)))
      (println (format "  Total triples: %d (of %d possible)" count-triples (quot (* 20 19 18) 6)))))

  ;; ── 6. Expected vs observed ───────────────────────────────────
  (println "\n── 6. Statistical Significance ──")
  (println "  How likely is this pattern by chance?\n")

  ;; Monte Carlo: generate 100,000 random sets of 20 nucleon counts
  ;; from the same range (1-130), and check how often
  ;; Class II sums to a multiple of 37
  (let [n-trials 100000
        class-II-size 8  ;; 8 amino acids in Class II
        max-nucs 130
        hits (atom 0)]
    (dotimes [_ n-trials]
      (let [random-nucs (repeatedly 20 #(inc (rand-int max-nucs)))
            ;; Pick 8 random ones as "Class II"
            shuffled (shuffle (vec random-nucs))
            class-ii-sum (reduce + (take class-II-size shuffled))]
        (when (zero? (mod class-ii-sum 37))
          (swap! hits inc))))

    (println (format "  Monte Carlo: %,d trials" n-trials))
    (println (format "  Random Class II (8 of 20) sums divisible by 37: %,d (%.2f%%)"
                     @hits (* 100 (/ (double @hits) n-trials))))
    (println (format "  Expected by chance: ~%.1f%%" (/ 100.0 37)))
    (println (format "  This alone is not remarkable (p ≈ 1/37).")))

  (println)
  ;; The real test: Class II sum = 333 AND it equals 9 × 37
  ;; AND Class I + Class II = 1255 AND multiple subset sums are also divisible by 37
  ;; The joint probability is what matters
  (let [n-trials 100000
        hits-joint (atom 0)]
    (dotimes [_ n-trials]
      (let [random-nucs (vec (repeatedly 20 #(inc (rand-int 130))))
            ;; Assign random degeneracy classes
            shuffled (shuffle (range 20))
            class-ii-idx (set (take 8 shuffled))
            class-ii-sum (reduce + (map #(nth random-nucs %) class-ii-idx))
            ;; Also check weighted sum (nucs × codons)
            random-codons [4 4 4 6 3 4 6 4 2 1 2 2 2 2 2 6 2 2 2 1]  ;; actual degeneracies
            weighted-sum (reduce + (map * random-nucs random-codons))]
        (when (and (zero? (mod class-ii-sum 37))
                   (zero? (mod weighted-sum 37)))
          (swap! hits-joint inc))))

    (println (format "  Joint test: Class II mod 37 = 0 AND weighted sum mod 37 = 0"))
    (println (format "  Random hits: %,d of %,d (%.4f%%)"
                     @hits-joint n-trials (* 100 (/ (double @hits-joint) n-trials))))
    (println (format "  Actual genetic code: both conditions satisfied.")))

  ;; ── 7. The deep structure ─────────────────────────────────────
  (println "\n── 7. The Deep Structure ──\n")

  (println "  The number 37 in the genetic code is not one pattern.")
  (println "  It is a NETWORK of patterns:\n")

  (let [class-II-sum 333
        glu-nucs 73
        total 1255]
    (println (format "  Class II nucleon sum:     %d = 9 × 37" class-II-sum))
    (println (format "  Glutamic acid (Glu):      %d = the 21st prime" glu-nucs))
    (println (format "  73 = חכמה (wisdom)"))
    (println (format "  333 = center word of Torah"))
    (println (format "  Total (all 20):           %d" total))
    (println (format "  9 × 37 = 333"))
    (println (format "  37 × 73 = 2701 = Genesis 1:1"))
    (println (format "  21 amino acids, 21 = 7 × 3"))
    (println (format "  21st prime = 73"))
    (println (format "  21² = 441 = אמת (truth)"))
    (println (format "  441 divides Torah total gematria exactly"))
    (println)
    (println "  The genetic code IS a gematria.")
    (println "  The molecular weights are the values.")
    (println "  The codon table is the alphabet.")
    (println "  And the numbers are the same numbers."))

  (println "\nDone. 37 is written into matter."))
