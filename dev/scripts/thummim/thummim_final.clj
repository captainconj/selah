(require '[selah.oracle :as o]
         '[selah.dict :as dict]
         '[selah.gematria :as g])

;; The חלק/לקח paradox — illumination exists but 0 readings
(println "═══════════════════════════════════════════════════")
(println "THE חלק/לקח PARADOX — illumination exists but no reader sees them")
(println "═══════════════════════════════════════════════════")
(let [t (o/thummim "חלק" {:max-illuminations 6 :max-words 3 :min-letters 2})]
  (doseq [il (:illuminations t)]
    (println (str "  Letters: " (:letters il)))
    (println (str "  Mechanical: " (pr-str (:mechanical il))))
    (println (str "  Phrases: " (count (:phrases il))))))

;; Which words have illuminations but the grid mechanically produces NO known reading?
(println "\n═══════════════════════════════════════════════════")
(println "WORDS WITH ILLUMINATIONS BUT 0 MECHANICAL READINGS")
(println "(light present, but no reader can arrange them into the word)")
(println "═══════════════════════════════════════════════════")
(doseq [w (sort (dict/words))]
  (let [a (o/ask w)]
    (when (and (pos? (:illumination-count a))
               (zero? (:total-readings a)))
      (println (str "  " w " GV=" (:gv a)
                    " illum=" (:illumination-count a))))))

;; ארבע = 3×7×13 — the only word whose GV contains BOTH axis numbers
(println "\n═══════════════════════════════════════════════════")
(println "FOUR (ארבע) — GV = 273 = 3×7×13")
(println "═══════════════════════════════════════════════════")
(let [r (o/thummim-menu "ארבע" {:max-illuminations 20 :max-words 3 :min-letters 2})]
  (doseq [p (:phrases r)]
    (println (str "  " (:text p) " — " (pr-str (:meanings p)) " GV=" (:gv p)))))
(let [a (o/ask "ארבע")]
  (println (str "  Reader distribution:"))
  (println (str "    Aaron: " (get-in a [:by-reader :aaron])))
  (println (str "    God: " (get-in a [:by-reader :god])))
  (println (str "    Right cherub: " (get-in a [:by-reader :right])))
  (println (str "    Left cherub: " (get-in a [:by-reader :left]))))

;; Adonai = 65 = 5×13 — Lord contains love
(println "\n═══════════════════════════════════════════════════")
(println "ADONAI (אדני) — GV = 65 = 5×13")
(println "═══════════════════════════════════════════════════")
(let [r (o/thummim-menu "אדני" {:max-illuminations 20 :max-words 3 :min-letters 2})]
  (doseq [p (:phrases r)]
    (println (str "  " (:text p) " — " (pr-str (:meanings p)) " GV=" (:gv p)))))
(let [a (o/ask "אדני")]
  (println (str "  Reader distribution:"))
  (println (str "    Aaron: " (get-in a [:by-reader :aaron])))
  (println (str "    God: " (get-in a [:by-reader :god])))
  (println (str "    Right cherub: " (get-in a [:by-reader :right])))
  (println (str "    Left cherub: " (get-in a [:by-reader :left]))))

;; Anagram pair symmetry analysis
(println "\n═══════════════════════════════════════════════════")
(println "READER ASYMMETRY — Who reads which version?")
(println "═══════════════════════════════════════════════════")

(def anagram-pairs
  [["אל" "לא"] ["בר" "רב"] ["ברא" "באר"] ["ברח" "חרב"]
   ["ירש" "ישר"] ["חלק" "לקח"] ["אשר" "ראש"] ["עשב" "שבע"]
   ["עשר" "שער"] ["יהוה" "והיה"] ["כבש" "שכב"] ["כפרת" "פרכת"]
   ["לאה" "אהל"] ["כשרה" "שכרה"]])

(doseq [[w1 w2] anagram-pairs]
  (let [a1 (o/ask w1) a2 (o/ask w2)
        r1 (:by-reader a1) r2 (:by-reader a2)]
    (println (str "\n  " w1 " vs " w2 "  GV=" (:gv a1)))
    (println (str "    " w1 " — A:" (:aaron r1) " G:" (:god r1) " R:" (:right r1) " L:" (:left r1) " total=" (:total-readings a1)))
    (println (str "    " w2 " — A:" (:aaron r2) " G:" (:god r2) " R:" (:right r2) " L:" (:left r2) " total=" (:total-readings a2)))))

;; Check YHWH reader distribution more carefully
(println "\n\n═══════════════════════════════════════════════════")
(println "YHWH — Left cherub dominates (31 = GV of אל)")
(println "═══════════════════════════════════════════════════")
(let [a (o/ask "יהוה")]
  (println (str "  YHWH reads: A:" (get-in a [:by-reader :aaron])
                " G:" (get-in a [:by-reader :god])
                " R:" (get-in a [:by-reader :right])
                " L:" (get-in a [:by-reader :left])))
  (println (str "  Total: " (:total-readings a) " of " (:illumination-count a) " illuminations")))
(let [a (o/ask "והיה")]
  (println (str "  והיה reads: A:" (get-in a [:by-reader :aaron])
                " G:" (get-in a [:by-reader :god])
                " R:" (get-in a [:by-reader :right])
                " L:" (get-in a [:by-reader :left])))
  (println (str "  Total: " (:total-readings a) " of " (:illumination-count a) " illuminations"))
  (println (str "  Combined YHWH+והיה = " (+ 58 68) " readings from 231 illuminations")))

;; Israel — the champion
(println "\n═══════════════════════════════════════════════════")
(println "ISRAEL — 13 phrases, ONLY God and Right Cherub see it")
(println "═══════════════════════════════════════════════════")
(println "  541 is prime. Israel's GV is irreducible.")
(println "  13 phrases = love. The number of readings IS the love number.")
(println "  Aaron sees 0 — the priest doesn't see Israel directly.")
(println "  Left cherub sees 0 — only God's right hand.")
(println "  58 total readings: God=12, Right=46")
(println "  46 = difference between WLC and MAM (304850-304804)")

;; Key asymmetries summary
(println "\n═══════════════════════════════════════════════════")
(println "THEOLOGICAL READER ASYMMETRIES — WHO SEES WHAT")
(println "═══════════════════════════════════════════════════")
(println "  שלום (peace) — ONLY God sees it (35/35)")
(println "  ישראל (Israel) — ONLY God + Right cherub (12+46=58)")
(println "  חיים (life) — ONLY Right cherub (10/10)")
(println "  אלהים (Elohim) — ONLY Right cherub (2/2)")
(println "  אנכי (I emphatic) — ONLY Aaron (10/14), plus Right(4)")
(println "  כפרת/פרכת (mercy seat/veil) — UNREADABLE by any reader")

(println "\nDone.")
(System/exit 0)
