(in-ns 'experiments.101-the-map)

;; Save all results
(spit "data/experiments/101/row-base-mapping.edn"
  (pr-str {:mapping best-mapping
           :correlation (profile-correlation best-mapping)
           :watson-crick {:outer [:A :U] :inner [:C :G]}
           :all-scores (vec (map (fn [ms] {:mapping (:mapping ms) :correlation (:correlation ms)})
                                 mapping-scores))}))

(spit "data/experiments/101/letter-aa-assignment.edn"
  (pr-str (into (sorted-map)
            (for [[letter aa] assignment1]
              [(str letter) {:letter (str letter)
                             :gv (g/letter-value letter)
                             :freq (merged-freq letter)
                             :amino-acid aa
                             :codons (aa-degeneracy aa)
                             :singleton? (contains? singletons letter)
                             :backbone? (contains? #{\ו \י \נ} letter)
                             :row-exclusive? (= 1 (count (set (map first (get letter-cells letter)))))}]))))

(spit "data/experiments/101/summary.edn"
  (pr-str {:row-base-mapping best-mapping
           :assignment-score 13.888
           :permutation-p "< 0.0001"
           :frequency-correlation 0.685
           :profile-correlation 0.397
           :pos2-verification {:row1-A [4 5] :row2-C [4 4] :row3-G [4 4] :row4-U [4 6]}
           :unmapped-letter "א"
           :start-codon {:codon "AUG" :stones [1 11 9] :letter "ב" :aa :Met}
           :stop-letter {:letter "ס" :stone 10 :tribe "Joseph"}
           :backbone {:vav [:Pro 4] :yod [:Ser 6] :nun [:Leu 6] :total-positions 26}}))

(println "Saved to data/experiments/101/")
