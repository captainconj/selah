(require '[selah.oracle :as oracle]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %)) (range 2 (inc (long (Math/sqrt n)))))))

(defn combine-letters
  "Concatenate the Hebrew letters of multiple words into one string."
  [words]
  (apply str words))

(defn report-combined
  "Parse combined letters of input words and print findings.
   Highlights specific key phrases if provided."
  [label input-words {:keys [highlights max-words min-letters vocab]
                      :or {max-words 4 min-letters 2 vocab :dict}}]
  (let [combined (combine-letters input-words)
        phrases  (oracle/parse-letters combined {:max-words max-words
                                                 :min-letters min-letters
                                                 :vocab vocab})
        total-gv (g/word-value combined)
        prime?   (prime? total-gv)]
    (println)
    (println (str "═══════════════════════════════════════════════════"))
    (println (str "  " label))
    (println (str "═══════════════════════════════════════════════════"))
    (println)
    (println (str "  Input words:     " (str/join " + " input-words)))
    (println (str "  Input meanings:  " (str/join " + " (map dict/translate input-words))))
    (println (str "  Input GVs:       " (str/join " + " (map #(str (g/word-value %)) input-words))))
    (println (str "  Combined letters: " combined))
    (println (str "  Combined GV:     " total-gv
                  (when prime? " *** PRIME ***")))
    (println (str "  Total parsings:  " (count phrases)))
    (println)

    ;; Print all parsings
    (println "  All phrase readings:")
    (doseq [{:keys [text phrase meanings gv]} phrases]
      (let [highlight? (and highlights (some #(= text %) highlights))
            prefix     (if highlight? "  >>> " "      ")]
        (println (format "%s%-30s  %s  GV=%d"
                         prefix text
                         (str/join ", " meanings)
                         gv))))

    ;; Verify highlighted phrases
    (when highlights
      (println)
      (println "  Key findings:")
      (doseq [h highlights]
        (let [match (first (filter #(= h (:text %)) phrases))]
          (if match
            (println (format "    [FOUND] %s = %s  GV=%d"
                             h (str/join ", " (:meanings match)) (:gv match)))
            (println (format "    [MISSING] %s — not found in parsings!" h))))))

    (println)
    phrases))

;; ════════════════════════════════════════════════════════════════
;; 1. בן אדם = אבן דם — Son of Man = Stone of Blood
;; ════════════════════════════════════════════════════════════════

(let [phrases (report-combined
                "בן אדם — Son of Man"
                ["בן" "אדם"]
                {:highlights ["אבן דם"]
                 :vocab :dict})
      gv (g/word-value "בנאדם")]
  (println (format "  Verification: GV(בן אדם) = %d, prime? %s" gv (prime? gv)))
  (assert (= gv 97) "GV should be 97")
  (assert (prime? gv) "97 should be prime")
  (println "  PASS: GV=97 is prime.")
  (println))

;; ════════════════════════════════════════════════════════════════
;; 2. כבש + דם → כבד שם — Lamb's Blood = Glory of the Name
;; ════════════════════════════════════════════════════════════════

(let [phrases (report-combined
                "כבש + דם — Lamb's Blood"
                ["כבש" "דם"]
                {:highlights ["כבד שם"]
                 :vocab :dict})
      gv-kavod (g/word-value "כבד")]
  (println (format "  Verification: GV(כבד/glory) = %d" gv-kavod))
  (assert (= gv-kavod 26) "GV(כבד) should be 26 = YHWH")
  (println (format "  GV(כבד) = 26 = GV(יהוה) = %d  MATCH" (g/word-value "יהוה")))
  (println "  PASS: The glory IS the Name.")
  (println))

;; ════════════════════════════════════════════════════════════════
;; 3. דרך + אמת + חיים → את דרך חי מים — Way/Truth/Life
;; ════════════════════════════════════════════════════════════════

(let [phrases (report-combined
                "דרך + אמת + חיים — Way, Truth, Life"
                ["דרך" "אמת" "חיים"]
                {:highlights ["את דרך חי מים"]
                 :vocab :dict})
      gv (g/word-value "דרךאמתחיים")]
  (println (format "  Verification: GV(דרך+אמת+חיים) = %d, prime? %s" gv (prime? gv)))
  (assert (= gv 733) "GV should be 733")
  (assert (prime? gv) "733 should be prime")
  (println "  PASS: GV=733 is prime.")
  (println))

;; ════════════════════════════════════════════════════════════════
;; 4. אב + בן + רוח → בוא חן רב — Father/Son/Spirit = Great Grace Comes
;; ════════════════════════════════════════════════════════════════

(let [phrases (report-combined
                "אב + בן + רוח — Father, Son, Spirit"
                ["אב" "בן" "רוח"]
                {:highlights ["בוא חן רב" "ברא חן בו" "באר חן בו"]
                 :vocab :dict})
      gv (g/word-value "אבבנרוח")]
  (println (format "  Verification: GV(אב+בן+רוח) = %d, prime? %s" gv (prime? gv)))
  (assert (= gv 269) "GV should be 269")
  (assert (prime? gv) "269 should be prime")
  (println (format "  Total parsings: %d (expected 42 = 6×7)" (count phrases)))
  (println "  PASS: GV=269 is prime.")
  (println))

;; ════════════════════════════════════════════════════════════════
;; Summary
;; ════════════════════════════════════════════════════════════════

(println "═══════════════════════════════════════════════════")
(println "  SUMMARY — Combined Word Findings")
(println "═══════════════════════════════════════════════════")
(println)
(println "  ┌──────────────────────┬────────────────────────────────┬─────┬────────┐")
(println "  │ Input                │ Key Reading                    │  GV │ Prime? │")
(println "  ├──────────────────────┼────────────────────────────────┼─────┼────────┤")
(doseq [[input reading gv]
        [["בן + אדם"         "אבן דם (stone of blood)"          97]
         ["כבש + דם"         "כבד שם (glory of Name)"          366]
         ["דרך + אמת + חיים" "את דרך חי מים (aleph-tav way)"   733]
         ["אב + בן + רוח"   "בוא חן רב (great grace comes)"   269]]]
  (println (format "  │ %-20s │ %-30s │ %3d │ %-6s │"
                   input reading gv (if (prime? gv) "YES" "no"))))
(println "  └──────────────────────┴────────────────────────────────┴─────┴────────┘")
(println)
(println "  Additional verification:")
(println (format "    כבד (glory) GV = %d = יהוה GV = %d" (g/word-value "כבד") (g/word-value "יהוה")))
(println)
(println "  All assertions passed.")
