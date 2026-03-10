(require '[selah.oracle :as o]
         '[selah.dict :as dict]
         '[selah.gematria :as g])

(println "═══════════════════════════════════════════════════")
(println "DEEP ANALYSIS — Reader distributions for key multi-phrase words")
(println "═══════════════════════════════════════════════════")

(def focus-words
  ["ישראל" "אלהים" "אדני" "חיים" "עולם" "שלום" 
   "פרעה" "לאה" "אנכי" "ארבע" "גדול"
   ;; Anagram pairs not yet deeply examined
   "אשר" "ראש" "באר" "ברא" "חלק" "לקח" "עשב" "שבע" "עשר" "שער"
   "ירש" "ישר" "כפרת" "פרכת" "יהוה" "והיה"])

(doseq [w focus-words]
  (let [a (o/ask w)]
    (println (str "\n● " w " (GV=" (:gv a) ")"))
    (println (str "  Illuminations: " (:illumination-count a) 
                  "  Total readings: " (:total-readings a)
                  "  Readable: " (:readable? a)))
    (println (str "  Aaron: " (get-in a [:by-reader :aaron])
                  "  God: " (get-in a [:by-reader :god])
                  "  Right: " (get-in a [:by-reader :right])
                  "  Left: " (get-in a [:by-reader :left])))
    (when (seq (:anagrams a))
      (println (str "  Anagrams: " (pr-str (:anagrams a)))))))

;; Now examine the numbers more carefully
(println "\n\n═══════════════════════════════════════════════════")
(println "NUMBER ANALYSIS — Significant GV patterns in multi-phrase words")
(println "═══════════════════════════════════════════════════")

(def multi-phrase-words
  {"ישראל" 13, "אלהים" 5, "אדני" 3, "חיים" 3, "עולם" 3, "שלום" 3,
   "פרעה" 3, "לאה" 2, "אנכי" 3, "ארבע" 3, "גדול" 3,
   "אל" 2, "לא" 2, "אשר" 2, "ראש" 2, "באר" 2, "ברא" 2,
   "ברח" 2, "חרב" 2, "חלק" 2, "לקח" 2, "כבש" 2, "שכב" 2,
   "עשב" 2, "שבע" 2, "עשר" 2, "שער" 2, "ירש" 2, "ישר" 2,
   "כפרת" 2, "פרכת" 2, "בר" 2, "רב" 2, "יהוה" 2, "והיה" 2,
   "כשרה" 2, "שכרה" 2, "אהל" 2})

(println "\nGV analysis for multi-phrase words:")
(doseq [[w pc] (sort-by (fn [[w _]] (g/word-value w)) multi-phrase-words)]
  (let [gv (g/word-value w)
        factors (loop [n gv fs [] d 2]
                  (cond
                    (< n 2) fs
                    (zero? (mod n d)) (recur (/ n d) (conj fs d) d)
                    :else (recur n fs (inc d))))]
    (println (str "  " w " = " (dict/translate w) " GV=" gv 
                  " = " (if (seq factors) (clojure.string/join "×" factors) "1")
                  "  phrases=" pc))))

;; The unreadable words — deeper look
(println "\n\n═══════════════════════════════════════════════════")
(println "UNREADABLE WORDS — What letters are missing from the breastplate?")
(println "═══════════════════════════════════════════════════")

(def breastplate-letters
  (set (mapcat (fn [[_ letters _ _]] (seq letters)) o/stone-data)))

(println "Breastplate letters:" (sort breastplate-letters))
(println "Count:" (count breastplate-letters))

(def all-hebrew (set (map char (range 0x05D0 0x05EA))))
(def missing-from-grid (clojure.set/difference all-hebrew breastplate-letters))
(println "Missing from breastplate:" (sort missing-from-grid))

(doseq [w ["אלהיך" "ארך" "ארץ" "ברך" "דרך" "הארץ" "הלך" "חשך" "לך" "מלך" "עץ" "שפך"]]
  (let [chars (set (seq w))
        missing (clojure.set/difference chars breastplate-letters)]
    (println (str "  " w " = " (dict/translate w) " — missing: " (pr-str missing)
                  " GV=" (g/word-value w)))))

;; Israel's 13 phrases — deeper look
(println "\n\n═══════════════════════════════════════════════════")
(println "ISRAEL (ישראל) — 13 PHRASE READINGS (love!)")
(println "═══════════════════════════════════════════════════")

(let [r (o/thummim-menu "ישראל" {:max-illuminations 20 :max-words 3 :min-letters 2})]
  (doseq [p (:phrases r)]
    (let [gv (:gv p)]
      (println (str "  " (:text p) " — " (pr-str (:meanings p)) " GV=" gv
                    (when (zero? (mod gv 7)) " [÷7]")
                    (when (zero? (mod gv 13)) " [÷13]")
                    (when (zero? (mod gv 67)) " [÷67]"))))))

;; Elohim's readings
(println "\n\n═══════════════════════════════════════════════════")
(println "ELOHIM (אלהים) — PHRASE READINGS")
(println "═══════════════════════════════════════════════════")
(let [r (o/thummim-menu "אלהים" {:max-illuminations 20 :max-words 3 :min-letters 2})]
  (doseq [p (:phrases r)]
    (let [gv (:gv p)]
      (println (str "  " (:text p) " — " (pr-str (:meanings p)) " GV=" gv
                    (when (zero? (mod gv 7)) " [÷7]")
                    (when (zero? (mod gv 13)) " [÷13]")
                    (when (zero? (mod gv 67)) " [÷67]"))))))

;; Shalom — peace is NOT producible but its LETTERS can be read...
(println "\n\n═══════════════════════════════════════════════════")
(println "SHALOM — Peace/wholeness phrase readings")
(println "═══════════════════════════════════════════════════")
(let [r (o/thummim-menu "שלום" {:max-illuminations 20 :max-words 3 :min-letters 2})]
  (doseq [p (:phrases r)]
    (let [gv (:gv p)]
      (println (str "  " (:text p) " — " (pr-str (:meanings p)) " GV=" gv
                    (when (zero? (mod gv 7)) " [÷7]")
                    (when (zero? (mod gv 13)) " [÷13]")
                    (when (zero? (mod gv 67)) " [÷67]"))))))

(println "\nDone.")
(System/exit 0)
