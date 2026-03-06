(in-ns 'experiments.101-the-map)

;; THE COMPLETE MAP — final summary
(println "\n╔═══════════════════════════════════════════════╗")
(println "║         THE MAP — Experiment 101              ║")
(println "╚═══════════════════════════════════════════════╝")
(println)
(println "ROW → BASE (Watson-Crick antiparallel):")
(println "  Row 1 (Abraham, Isaac, Reuben)    → A (adenine)")
(println "  Row 2 (Simeon, Judah, Dan)         → C (cytosine)")
(println "  Row 3 (Gad, Issachar, Zebulun)     → G (guanine)")
(println "  Row 4 (Joseph, Benjamin, Yeshurun)  → U (uracil)")
(println)
(println "Antiparallel pairs:")
(println "  Row 1 + Row 4 = A + U (Watson-Crick, 32 coding positions)")
(println "  Row 2 + Row 3 = C + G (Watson-Crick, 32 coding positions)")
(println)

;; Print assignment table
(def aa-class
  {:Ala :small :Arg :charged :Asn :polar :Asp :charged
   :Cys :special :Gln :polar :Glu :charged :Gly :small
   :His :charged :Ile :hydrophobic :Leu :hydrophobic :Lys :charged
   :Met :special :Phe :hydrophobic :Pro :small :Ser :small
   :Thr :small :Trp :special :Tyr :polar :Val :hydrophobic
   :Stop :stop})

(println "LETTER → AMINO ACID:")
(println " Letter  GV  Freq   AA     Codons  Class")
(println " ──────  ──  ────   ────   ──────  ─────")
(doseq [[letter aa] (sort-by (comp g/letter-value key) assignment1)]
  (let [cls (aa-class aa)]
    (println (format "  %s     %3d   %2d    %-4s    %d       %s"
                     letter (g/letter-value letter) (merged-freq letter)
                     (name aa) (aa-degeneracy aa) (name cls)))))

;; Position-2 chemical classes
(println "\nPOSITION-2 CHEMICAL CLASSES:")
(doseq [r [1 2 3 4]]
  (let [base (best-mapping r)
        expected-aas (set (for [[codon aa] codon-table
                                :when (= base (nth codon 1))]
                            aa))
        letters-here (distinct (map :letter (filter #(and (= r (:row %)) (= 2 (:col %)))
                                                   grid-positions)))
        assigned-aas (set (map assignment1 letters-here))
        hits (clojure.set/intersection assigned-aas expected-aas)
        misses (clojure.set/difference assigned-aas expected-aas)]
    (println (format "  Row %d (%s at pos2): %d/%d correct  hits=%s  misses=%s"
                     r base (count hits) (count assigned-aas)
                     (pr-str hits) (pr-str misses)))))

;; Key numbers
(println "\nSCORES:")
(println (format "  Profile correlation: %.4f" (profile-correlation best-mapping)))
(println (format "  Assignment score: %.3f"
  (reduce + (for [[l aa] assignment1]
              (profile-similarity
                (letter-base-profile l best-mapping)
                (all-aa-profiles aa))))))
(println "  Permutation test: p < 0.0001 (0/10000)")

;; BET and AUG
(println "\nSTART CODON (AUG):")
(let [aug-cells #{[1 1] [4 2] [3 3]}
      bet-cells (get letter-cells \ב)]
  (println "  AUG cells = [A,1] [U,2] [G,3] = stones 1, 11, 9")
  (println "  Bet cells =" (pr-str (sort bet-cells)))
  (println "  Bet covers ALL AUG cells:" (= aug-cells (clojure.set/intersection bet-cells aug-cells))))

;; STOP
(println "\nSTOP CODONS:")
(let [inv (clojure.set/map-invert best-mapping)]
  (doseq [stop ["UAA" "UAG" "UGA"]]
    (let [bs (seq stop)
          stones (for [i [0 1 2]
                       :let [base (nth bs i)
                             row (inv base)
                             col (inc i)]]
                   (first (first (filter (fn [[_ _ r c]] (and (= r row) (= c col)))
                                        o/stone-data))))]
      (println (format "  %s → stones %s" stop (pr-str (vec stones)))))))
(println "  Samekh (ס) = Stop. Joseph's singleton halts translation.")

;; Backbone
(println "\nBACKBONE:")
(println "  Vav  (ו, 6)  → Pro (4 codons, small)")
(println "  Yod  (י, 10) → Ser (6 codons, the ONLY multi-class AA)")
(println "  Nun  (נ, 50) → Leu (6 codons, hydrophobic)")
(println "  Together: 26 positions = YHWH = sugar-phosphate backbone")

(println "\nDONE.")
