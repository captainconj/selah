(in-ns 'experiments.101-the-map)

;; Which letter is unmapped?
(println "All letters:" (count (keys letter-cells)))
(println "All targets (20 AA + Stop):" (+ (count amino-acids) 1))
(println "Assignment count:" (count assignment1))
(println)

(let [all-hebrew (set (keys letter-cells))
      assigned (set (keys assignment1))
      missing (clojure.set/difference all-hebrew assigned)]
  (println "UNMAPPED LETTER:" (pr-str missing))
  (doseq [ch missing]
    (println (format "  %s (gv=%d, freq=%d)" ch (g/letter-value ch) (merged-freq ch)))
    (println (format "  cells: %s" (pr-str (sort (get letter-cells ch)))))
    (println (format "  rows: %s" (pr-str (sort (set (map first (get letter-cells ch)))))))))

;; What was aleph's best match before greedy stole it?
(let [aleph-matches (get matches \א)]
  (println "\nAleph's top matches (before greedy):")
  (doseq [m (take 5 aleph-matches)]
    (println (format "  %s: sim=%.3f (%d codons)" (name (:aa m)) (:similarity m) (:aa-codons m)))))

;; The silent letter
(println)
(println "ALEPH IS UNMAPPED.")
(println "  GV = 1. The first letter. Silent — has no sound of its own.")
(println "  22 letters, 21 coded entities (20 AAs + Stop).")
(println "  One letter is beyond the code.")
(println "  Aleph begins: Elohim, Echad (one), Ani (I), Emet (truth).")
(println "  It is not coded. It IS.")

;; What about aleph-tav?
(println)
(println "ALEPH-TAV (את):")
(println "  Aleph = unmapped (beyond the code)")
(println "  Tav = Phe (phenylalanine, aromatic ring, 2 codons)")
(println "  The sign: the silent + the mark. The reader + the read.")
(println "  את illuminates columns 1 and 3 only — skips the discriminator.")
(println "  The boundary marker is not content. It frames the code.")
