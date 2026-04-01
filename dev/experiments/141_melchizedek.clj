(ns experiments.141-melchizedek
  "Experiment 141: Melchizedek — the priest with no genealogy.

   Genesis 14:18-20. King of Salem. Priest of God Most High.
   Brings bread and wine. Blesses Abraham. Receives the tithe.
   No father, no mother, no beginning, no end.

   Psalm 110:4 — You are a priest forever after the order of Melchizedek.
   Hebrews 7 — Made like the Son of God, he remains a priest forever.

   Run: clojure -M:dev -e \"(require '[experiments.141-melchizedek :as exp]) (exp/run)\""
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as b]
            [selah.dict :as dict]
            [clojure.string :as str]))

(defn show [label heb]
  (let [r (o/ask heb)
        br (:by-reader r)]
    (println (format "  %-20s %-8s GV=%-4d  A=%-3d G=%-3d T=%-3d M=%-3d  total=%d"
                     label heb (g/word-value heb)
                     (:aaron br) (:god br) (:truth br) (:mercy br)
                     (:total-readings r)))))

(defn basin [heb]
  (let [r (b/step-all-heads heb)
        fmt (fn [head] (if-let [info (get r head)]
                         (format "%s(%d)" (:next info) (:weight info))
                         "dead"))]
    (println (format "    basin: A=%s G=%s T=%s M=%s"
                     (fmt :aaron) (fmt :god) (fmt :truth) (fmt :mercy)))))

(defn thummim [label letters n]
  (println (format "\n── Thummim: %s (%s, GV=%d) ──" label letters (g/word-value letters)))
  (let [ps (o/parse-letters letters)]
    (println (format "  %d phrases" (count ps)))
    (doseq [p (take n ps)]
      (println (format "  %s — %s" (:text p) (str/join ", " (:meanings p)))))))

(defn run []
  (println "═══════════════════════════════════════════════════")
  (println "  EXPERIMENT 141 — MELCHIZEDEK")
  (println "  The priest with no genealogy.")
  (println "═══════════════════════════════════════════════════\n")

  ;; ── The Name ──
  (println "── THE NAME ──\n")
  (println "  מלכי-צדק = Melchi-zedek = My King of Righteousness")
  (println (format "  מלכי (my king) = %d" (g/word-value "מלכי")))
  (println (format "  צדק (righteousness) = %d" (g/word-value "צדק")))
  (println (format "  מלכיצדק (combined) = %d" (g/word-value "מלכיצדק")))
  (println (format "  מלך (king) = %d" (g/word-value "מלך")))
  (println (format "  שלם (Salem/complete) = %d" (g/word-value "שלם")))
  (println (format "  שלום (peace) = %d" (g/word-value "שלום")))
  (println)
  (println (format "  294 = 2 × 3 × 7 × 7. Completeness squared."))
  (println (format "  294 = %d" (* 2 3 7 7)))

  ;; ── Every word in Genesis 14:18-20 ──
  (println "\n\n── GENESIS 14:18-20 — THE MEETING ──\n")
  (println "  v18: And Melchizedek king of Salem brought forth bread and wine;")
  (println "       and he was priest of God Most High.")
  (println "  v19: And he blessed him and said: Blessed be Abram of God Most High,")
  (println "       possessor of heaven and earth.")
  (println "  v20: And blessed be God Most High, who has delivered your enemies")
  (println "       into your hand. And he gave him a tithe of all.\n")

  (show "Melchizedek" "מלכיצדק")
  (show "my king" "מלכי")
  (show "righteousness" "צדק")
  (show "king" "מלך")
  (show "THE king" "המלך")
  (show "Salem" "שלם")
  (show "peace" "שלום")
  (show "brought forth" "הוציא")
  (show "bread" "לחם")
  (show "wine" "יין")
  (show "priest" "כהן")
  (show "God (El)" "אל")
  (show "Most High" "עליון")
  (show "blessed" "ברוך")
  (show "bless" "ברך")
  (show "Abram" "אברם")
  (show "heaven" "שמים")
  (show "earth/land" "ארץ")
  (show "enemies" "צריך")
  (show "hand" "יד")
  (show "tithe" "מעשר")
  (show "all" "כל")

  ;; ── Psalm 110 words ──
  (println "\n\n── PSALM 110:1,4 — THE OATH ──\n")
  (println "  v1: The LORD said to my Lord: Sit at my right hand")
  (println "      until I make your enemies a footstool for your feet.")
  (println "  v4: The LORD has sworn and will not change his mind:")
  (println "      You are a priest forever after the order of Melchizedek.\n")

  (show "sit" "שב")
  (show "right hand" "ימין")
  (show "enemies" "איביך")
  (show "footstool" "הדם")
  (show "sworn" "נשבע")
  (show "priest" "כהן")
  (show "forever" "עולם")
  (show "order/manner" "דברת")
  (show "YHWH" "יהוה")

  ;; ── Hebrews 7 concepts ──
  (println "\n\n── HEBREWS 7 — THE COMPARISON ──\n")
  (println "  Without father, without mother, without genealogy,")
  (println "  having neither beginning of days nor end of life,")
  (println "  but made like the Son of God, he remains a priest forever.\n")

  (show "father" "אב")
  (show "mother" "אם")
  (show "son" "בן")
  (show "son of God" "בנאל")
  (show "beginning" "ראשית")
  (show "end" "קץ")
  (show "life" "חיים")
  (show "death" "מות")
  (show "forever" "עולם")
  (show "covenant" "ברית")
  (show "oath" "שבועה")

  ;; ── Thummim parsings ──
  (thummim "Melchizedek" "מלכיצדק" 15)
  (thummim "king of righteousness" "מלךצדק" 15)
  (thummim "king of Salem" "מלךשלם" 15)
  (thummim "king of peace" "מלךשלום" 15)
  (thummim "priest of God Most High" "כהןאלעליון" 20)
  (thummim "bread and wine" "לחםויין" 20)

  ;; ── Basin dynamics ──
  (println "\n\n── BASIN WALKS ──\n")
  (doseq [[label w] [["my king" "מלכי"]
                      ["righteousness" "צדק"]
                      ["Salem" "שלם"]
                      ["priest" "כהן"]
                      ["bread" "לחם"]
                      ["wine" "יין"]
                      ["bless" "ברך"]
                      ["tithe" "מעשר"]
                      ["Most High" "עליון"]
                      ["forever" "עולם"]]]
    (println (format "  %s (%s):" label w))
    (basin w))

  ;; ── Comparison: Melchizedek vs Aaron ──
  (println "\n\n── COMPARISON: TWO PRIESTHOODS ──\n")
  (println (format "  %-16s %-8s  %-8s" "" "Melchizedek" "Aaron"))
  (println (format "  %-16s %-8s  %-8s" "name GV" "294" "256"))
  (println (format "  %-16s %-8s  %-8s" "" "2×3×7²" "2⁸"))
  (println (format "  %-16s %-8s  %-8s" "brings" "bread+wine" "blood+incense"))
  (println (format "  %-16s %-8s  %-8s" "genealogy" "none" "Levi→Aaron"))
  (println (format "  %-16s %-8s  %-8s" "duration" "forever" "until destroyed"))
  (println (format "  %-16s %-8s  %-8s" "Psalm 110:4" "his order" "—"))
  (println (format "  %-16s %-8s  %-8s" "Hebrews 7" "greater" "lesser"))
  (println)
  (println (format "  אהרן (Aaron) GV=%d = 2⁸ = 256" (g/word-value "אהרן")))
  (println (format "  מלכיצדק GV=%d = 2×3×7² = 294" (g/word-value "מלכיצדק")))
  (println (format "  Difference: %d = %d" (- 294 256) (- 294 256)))
  (println (format "  38 = ? %s" (if (= 38 (g/word-value "לח")) "לח (moist/fresh)" "")))

  ;; ── The visibility question ──
  (println "\n\n── THE VISIBILITY QUESTION ──\n")
  (let [noah-r (o/ask "נח")
        melch-r (o/ask "מלכיצדק")
        abram-r (o/ask "אברם")
        abraham-r (o/ask "אברהם")]
    (println "  Who does the oracle see?")
    (println (format "  Noah (נח):         %d readings" (:total-readings noah-r)))
    (println (format "  Abram (אברם):      %d readings" (:total-readings abram-r)))
    (println (format "  Abraham (אברהם):   %d readings" (:total-readings abraham-r)))
    (println (format "  Melchizedek:       %d readings" (:total-readings melch-r)))
    (println)
    (println "  Noah: visible. Abraham: visible. Melchizedek: ?"))

  (println "\n═══════════════════════════════════════════════════")
  (println "  Done. The priest with no genealogy.")
  (println "═══════════════════════════════════════════════════"))

;; (run)
