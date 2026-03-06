(in-ns 'experiments.101-the-map)

;; The structure of the reading
(println "THE READING FRAME:")
(println)
(println "Position 1:     · (aleph on Abraham) — before the beginning")
(println "Positions 2-54: THE PROTEIN — 53 amino acids")
(println "Position 55:    * (stop on Joseph) — the end")
(println "Positions 56-72: after the stop — untranslated")
(println)

;; The ORF
(println "THE OPEN READING FRAME (53 residues):")
(let [full-seq (vec (mapcat (fn [[_ letters _ _]]
                              (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                            o/stone-data))
      orf-start 1  ;; 0-indexed, position 2 = index 1
      orf-end 54   ;; position 55 = index 54 (the *)
      orf (subvec full-seq orf-start orf-end)]
  (println " " (apply str orf))
  (println)

  ;; Count without alephs
  (let [real-aas (remove #{"·" "*"} orf)]
    (println (format "  Amino acids: %d (+ 2 aleph gaps + 1 stop = 53+2 positions)" (count real-aas))))

  ;; 3' UTR — after the stop
  (let [utr (subvec full-seq 55)]
    (println)
    (println (format "AFTER THE STOP (3' UTR, %d residues):" (count utr)))
    (println " " (apply str utr))
    (println "  = Cys-Met-Leu-Ser | Ile-Ser-Leu-Gly-Met-Val | Ser-Ser-Gly-Arg-Pro-Leu")
    (println "  = rest of Joseph  | Benjamin                 | Yeshurun")
    (println "  Rachel's sons + the closing. Untranslated. Regulatory.")))

;; 53
(println)
(println "53:")
(println "  • 53 amino acids in the ORF")
(println "  • 53 weekly Torah portions (parashot)")
(println "  • 53 = the garden sum (experiment 078)")
(println "  • 53 = F(9), the 9th Fibonacci number")
(println "  • 53-EDO tunes the garden (spy report: music)")
(println "  • 53 is prime")

;; The aleph positions
(println)
(println "THE THREE ALEPHS (· = gaps in the protein):")
(println "  Position 1 (stone 1, Abraham) — BEFORE the start. The silence before the word.")
(println "  Position 15 (stone 3, Reuben) — the firstborn who lost his place.")
(println "  Position 41 (stone 7, Gad) — the guard. 41 = Judah's stone GV.")
(println)
(println "  1, 15, 41:")
(println "  15 = YH (יה) = the Name abbreviated")
(println "  41 = am (אם) = mother, also Judah's stone GV")
(println "  Differences: 14, 26. David, YHWH.")

;; What the protein looks like by stone
(println)
(println "THE PROTEIN BY STONE:")
(doseq [[snum letters row col] o/stone-data]
  (let [tribe (case snum
                1 "Abraham" 2 "Isaac/Jacob" 3 "Reuben"
                4 "Simeon" 5 "Judah" 6 "Dan"
                7 "Gad" 8 "Issachar" 9 "Zebulun"
                10 "Joseph" 11 "Benjamin" 12 "Yeshurun")
        hebrew (seq letters)
        aas (map #(get letter->aa-code (base-letter %) "?") hebrew)
        aa-names (map (fn [ch aa]
                        (let [l (base-letter ch)]
                          (if (= l \א) "·"
                            (name (assignment1 l)))))
                      hebrew hebrew)]
    (println (format "  [%2d] %-12s  %s  =  %s"
                     snum tribe
                     (apply str aas)
                     (clojure.string/join "-" aa-names)))))
