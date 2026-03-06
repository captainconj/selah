(require '[selah.oracle :as o]
         '[selah.gematria :as g])

;; Understand the documented "21" vs our "41" discrepancy
;; The doc (urim-and-thummim.md line 738) says:
;; "שכרה is readable from 21 illumination patterns: 0 by Aaron, 6 by the right cherub, 15 by the left."
;; But we found 41 total readings: 20 by God, 6 right, 15 left = 41
;; The doc OMITTED God's 20 readings! It was counting only cherubim.
;; Wait, 0+6+15=21. The doc counted non-God, non-Aaron readers only.
;; Let me re-read the doc... 

;; Actually the doc says "0 by Aaron, 6 by the right cherub, 15 by the left"
;; That's 0+6+15 = 21. Missing: God's 20.
;; Let me check if God was added later or if this was a three-reader model

;; The "ask" function counts total readings across all 4 readers
(let [a (o/ask "שכרה")]
  (println "שכרה ask result:")
  (println "  total-readings:" (:total-readings a))
  (println "  by-reader:" (:by-reader a))
  (println "  illumination-count:" (:illumination-count a)))

(println)

(let [a (o/ask "כשרה")]
  (println "כשרה ask result:")
  (println "  total-readings:" (:total-readings a))
  (println "  by-reader:" (:by-reader a))
  (println "  illumination-count:" (:illumination-count a)))

;; Now trace the SINGLE shared illumination in detail
(println)
(println "=== THE CRITICAL ILLUMINATION — SAME LIGHT, DIFFERENT TRUTH ===")
(println)
(println "Illumination #61: stones Abraham(1), Issachar(8), Benjamin(11)")
(println "  ה on Abraham (pos 3) — row 1, col 1")
(println "  ר on Abraham (pos 2) — row 1, col 1")
(println "  כ on Issachar (pos 4) — row 3, col 2")
(println "  ש on Benjamin (pos 3) — row 4, col 2")
(println)
(println "God reads (bottom-up, L-to-R from Aaron's view):")
(println "  row 4 col 2: ש → row 3 col 2: כ → row 1 col 1: ר → row 1 col 1: ה")
(println "  = שכרה (DRUNK)")
(println)
(println "Right cherub reads (R-to-L columns, top-to-bottom):")
(println "  col 2 first: row 3 = כ, row 4 = ש → col 1: row 1 = ר, row 1 = ה")
(println "  = כשרה (LIKE SARAH)")
(println)
(println "Aaron reads: רהכש (gibberish)")
(println "Left reads: רהשכ (gibberish)")
(println)

;; Now verify the stochastic weights
(println "=== HANNAH WEIGHTING (inverse frequency) ===")
(println)
(println "For שכרה (41 total readings):")
(println "  Raw self-weight = 1/41 per self-reading")  
(println "  כשרה competes: 3 readings total")
(println)

;; The transition weights:
;; From שכרה: 
;;   self-readings of שכרה = 41, raw-weight = 1/41 each → but Hannah inverts
;;   Wait — the 088 model used the DICTIONARY oracle (210 words), not the Torah oracle
;;   Let me check what the actual transition computation looks like

;; In 088, the transition matrix is built from the FORWARD query on each word
;; Each known-word output gets weight = 1/reading-count (inverse frequency)
;; Then normalized to sum to 1

;; For the letter-set {ש,כ,ר,ה}:
;;   שכרה has 41 readings → raw weight per reading = 1/41
;;   כשרה has 3 readings → raw weight per reading = 1/3
;; Total raw weight = 41*(1/41) + 3*(1/3) = 1 + 1 = 2
;; Normalized: שכרה = 1/2 = 0.5, כשרה = 1/2 = 0.5
;; Wait, that doesn't match the 088 data which shows 0.087 and 0.913

;; The 088 data was computed when the code had different counts (21 and 2?)
;; Let me recalculate with current counts

(println "Current counts: שכרה=41, כשרה=3")
(println "Hannah weight (inverse reading count): שכרה→1/41, כשרה→1/3")
(println "Normalizing: total = 1/41 + 1/3 = 3/(41*3) + 41/(41*3) = 44/123")
(println (str "שכרה weight = (1/41)/(44/123) = 123/(41*44) = " (double (/ 123 (* 41 44)))))
(println (str "כשרה weight = (1/3)/(44/123) = 123/(3*44) = " (double (/ 123 (* 3 44)))))
(println)
(println "So from ANY starting word that produces {ש,כ,ר,ה}:")
(println (str "  P(כשרה) = " (format "%.4f" (double (/ 123 (* 3 44)))) " = " (format "%.1f%%" (* 100.0 (/ 123.0 (* 3 44))))))
(println (str "  P(שכרה) = " (format "%.4f" (double (/ 123 (* 41 44)))) " = " (format "%.1f%%" (* 100.0 (/ 123.0 (* 41 44))))))
(println)

;; But wait — 088 had different counts. Let me check what the transition.edn says
;; The raw-weight for שכרה→שכרה was 0.0476 ≈ 1/21
;; The raw-weight for כשרה→כשרה was 0.5 ≈ 1/2
;; So at the time of 088, the counts WERE 21 and 2 (not 41 and 3)
;; The code must have changed since then!

(println "=== DISCREPANCY ANALYSIS ===")
(println "088 data: raw-weight 0.0476 ≈ 1/21 for שכרה, 0.5 ≈ 1/2 for כשרה")
(println "Current code: 41 readings for שכרה, 3 for כשרה")
(println "")
(println "The documented 21/2 was from the original three-reader model (Aaron + 2 cherubim).")
(println "The current four-reader model (Aaron + God + 2 cherubim) adds God as a 4th reader.")
(println "God produces שכרה 20 times and כשרה 1 time.")
(println "So: original 3-reader: 0+6+15=21 / 0+1+1=2")
(println "    current 4-reader:  0+20+6+15=41 / 0+1+1+1=3")
