(in-ns 'experiments.101-the-map)

(def letter->aa-code
  (merge
    (into {} (for [[l aa] assignment1]
               [l (case aa
                    :Ala "A" :Arg "R" :Asn "N" :Asp "D" :Cys "C"
                    :Gln "Q" :Glu "E" :Gly "G" :His "H" :Ile "I"
                    :Leu "L" :Lys "K" :Met "M" :Phe "F" :Pro "P"
                    :Ser "S" :Thr "T" :Trp "W" :Tyr "Y" :Val "V"
                    :Stop "*")]))
    {\א "·"}))  ;; aleph = beyond

;; Read each stone
(println "THE BREASTPLATE, TRANSLATED:")
(println)
(doseq [[snum letters row col] o/stone-data]
  (let [tribe (case snum
                1 "Abraham" 2 "Isaac/Jacob" 3 "Reuben"
                4 "Simeon" 5 "Judah" 6 "Dan"
                7 "Gad" 8 "Issachar" 9 "Zebulun"
                10 "Joseph" 11 "Benjamin" 12 "Yeshurun")
        hebrew (seq letters)
        aas (map #(get letter->aa-code (base-letter %) "?") hebrew)]
    (println (format "  [%2d] %-12s  %s  →  %s"
                     snum tribe
                     (apply str (interpose " " (map str hebrew)))
                     (apply str aas)))))

;; Read as continuous sequence
(println)
(println "CONTINUOUS READING (stones 1→12):")
(let [full-seq (mapcat (fn [[_ letters _ _]]
                         (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                       o/stone-data)]
  (println " " (apply str full-seq)))

;; Read by row (each row = one base)
(println)
(println "BY ROW (each row = one base):")
(doseq [r [1 2 3 4]]
  (let [row-stones (filter (fn [[_ _ sr _]] (= sr r)) o/stone-data)
        row-seq (mapcat (fn [[_ letters _ _]]
                          (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                        row-stones)
        base (best-mapping r)]
    (println (format "  Row %d (%s): %s" r base (apply str row-seq)))))

;; What are the alephs doing?
(println)
(println "ALEPH POSITIONS (· = beyond the code):")
(let [positions (keep-indexed
                  (fn [i [_ letters _ _]]
                    (let [aleph-idxs (keep-indexed
                                       (fn [j ch] (when (= \א (base-letter ch)) [i j]))
                                       (seq letters))]
                      (when (seq aleph-idxs)
                        [i (nth o/stone-data i) aleph-idxs])))
                  o/stone-data)]
  ;; actually let me just find all aleph positions in the full 72-letter sequence
  (let [all-letters (mapcat (fn [[sn letters _ _]]
                              (map-indexed (fn [i ch] {:stone sn :pos i :letter (base-letter ch)})
                                           (seq letters)))
                            o/stone-data)
        aleph-pos (keep-indexed (fn [i m] (when (= \א (:letter m))
                                            (format "  position %d: stone %d (%s)"
                                                    (inc i) (:stone m)
                                                    (case (:stone m) 1 "Abraham" 3 "Reuben" 7 "Gad" "?"))))
                                all-letters)]
    (doseq [p aleph-pos] (println p))))

;; The open reading frame
(println)
(println "OPEN READING FRAME:")
(let [full-seq (mapcat (fn [[_ letters _ _]]
                         (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                       o/stone-data)
      ;; Find first Met and first Stop after it
      seq-vec (vec full-seq)
      first-met (.indexOf seq-vec "M")
      first-stop (.indexOf (vec (drop (inc first-met) seq-vec)) "*")]
  (println (format "  First M at position %d (stone 1, bet)" (inc first-met)))
  (when (>= first-stop 0)
    (let [orf (subvec seq-vec first-met (+ first-met first-stop 2))]
      (println (format "  First * at position %d after M" (+ first-met first-stop 2)))
      (println (format "  ORF length: %d amino acids" (- (count orf) 2)))
      (println (format "  ORF: %s" (apply str orf))))))

;; What does the ORF contain?
(println)
(println "AMINO ACID COMPOSITION (full 72 letters):")
(let [full-seq (mapcat (fn [[_ letters _ _]]
                         (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                       o/stone-data)
      freqs (frequencies full-seq)]
  (doseq [[aa cnt] (sort-by (comp - val) freqs)]
    (println (format "  %s: %d" aa cnt))))
