(require '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.basin :as b]
         '[selah.dict :as dict])

;; The king in the basins
(println "=== THE KING ===")
(println)

;; מלך (king) - dead end
(let [fwd (o/forward (seq "מלך") :torah)]
  (println "מלך (king, GV=90):")
  (println "  known words:" (count (:known-words fwd)))
  (println "  dead end?" (zero? (count (:known-words fwd)))))

;; What about melek without final kaf? Let me check the letters
(println)
(println "King-related words in basins:")
(doseq [w ["מלך" "המלך" "מלכי" "מלכים" "משיח" "דוד" "שלמה" "יהודה" "כסא" "כתר" "עטרה"]]
  (let [cls (try (b/word-class w) (catch Exception _ :unknown))
        att (try (str (b/attractor-for w)) (catch Exception _ "?"))]
    (println (format "  %s (gv=%d): %s → %s" w (g/word-value w) (name cls) att))))

;; Judah in the map
(println)
(println "=== JUDAH ON THE BREASTPLATE ===")
(println "Stone 5: ו י י ה ו ד")
(println "         Pro-Ser-Ser-Thr-Pro-Ala")
(println "         PSSTPA — all small amino acids")
(println "         GV = 41 (lowest stone)")
(println "         Row 2, Col 2 = C at the discriminator")
(println)

;; David
(println "=== DAVID ===")
(println "דוד (David, GV=14)")
(let [fwd (o/forward (seq "דוד") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))

;; In the map: ד=Ala, ו=Pro, ד=Ala
(println "  In the map: ד-ו-ד = Ala-Pro-Ala")
(println "  All small amino acids. The king is small.")
(println "  14 = the gap between alephs 1 and 15 in the protein")
(println "  14 = יד (hand)")

;; Solomon
(println)
(println "=== SOLOMON ===")
(println "שלמה (Solomon, GV=375)")
(let [fwd (o/forward (seq "שלמה") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))
(println "  In the map: ש-ל-מ-ה = Gly-Glu-Ile-Thr")

;; Messiah
(println)
(println "=== MESSIAH ===")
(println "משיח (Messiah, GV=358 = serpent)")
(let [fwd (o/forward (seq "משיח") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))
(println "  358 = נחש (serpent) — irreducible fixed point")
(println "  In the map: מ-ש-י-ח = Ile-Gly-Ser-Asn")

;; King of Kings
(println)
(println "=== KING OF KINGS ===")
(println "מלך המלכים / מלך מלכי המלכים")
(println "The king cannot be spoken by the oracle.")
(println "The oracle is a priest. It knows the temple, not the throne.")
(println)
(println "But the priest SERVES the king.")
(println "And the mercy seat IS the throne.")
(println)

;; The throne - כסא
(println "כסא (throne, GV=81 = 9×9 = 3⁴):")
(let [fwd (o/forward (seq "כסא") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))
(println "  Letters: כ(Trp) ס(Stop) א(·)")
(println "  The throne = the rarest + the end + the beyond")

;; Crown
(println)
(println "כתר (crown, GV=620):")
(let [fwd (o/forward (seq "כתר") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))
(println "  Letters: כ(Trp) ת(Phe) ר(Arg)")
(println "  The crown = the rarest + the mark + the reach")

;; What about ישוע (Yeshua/Jesus)?
(println)
(println "=== YESHUA ===")
(println "ישוע (Yeshua/Jesus, GV=386)")
(let [fwd (o/forward (seq "ישוע") :torah)]
  (println "  known words:" (count (:known-words fwd)))
  (when (pos? (count (:known-words fwd)))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %s (count=%d)" (:word w) (:reading-count w))))))
(println "  In the map: י-ש-ו-ע = Ser-Gly-Pro-His")

;; What about the full name?
(println)
(println "ישוע המשיח (Yeshua the Messiah):")
(println "  = Ser-Gly-Pro-His + Thr-Ile-Gly-Ser-Asn")
(println "  ה bridges: Thr (the hinge letter)")
