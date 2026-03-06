(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.oracle :as oracle]
         '[clojure.string :as str])

;; What Torah words have GV=490?
(println "=== TORAH WORDS WITH GV=490 ===")
(let [torah (dict/torah-words)
      gv490 (filter #(= 490 (g/word-value %)) torah)]
  (doseq [w (sort gv490)]
    (let [m (dict/translate w)]
      (println (format "  %s  GV=%d  %s" w (g/word-value w) (or m "")))))
  (println (str "  Total: " (count gv490))))

;; What Torah words have GV=70?
(println)
(println "=== TORAH WORDS WITH GV=70 ===")
(let [torah (dict/torah-words)
      gv70 (filter #(= 70 (g/word-value %)) torah)]
  (doseq [w (sort gv70)]
    (let [m (dict/translate w)]
      (println (format "  %s  %s" w (or m "")))))
  (println (str "  Total: " (count gv70))))

;; Feed "seventy sevens" to the oracle: שבעים (seventy)
(println)
(println "=== שבעים (seventy) through oracle ===")
(let [phrases (oracle/parse-letters "שבעים")]
  (println (format "  %d phrase readings" (count phrases)))
  (doseq [p (take 20 phrases)]
    (let [words p
          text (str/join " " words)
          meanings (map dict/translate words)
          has-m (some identity meanings)]
      (when has-m
        (println (format "    %s → %s" text
                         (str/join " + " (map #(or % "?") meanings))))))))

;; The forced sum: atone+ransom+altar+blood through the oracle as combined letters
(println)
(println "=== כפרפדהמזבחדם (atone+ransom+altar+blood = GV 490) ===")
(let [combined "כפרפדהמזבחדם"
      _ (println (str "  Letters: " combined " (" (count combined) " letters)"))
      _ (println (str "  GV: " (g/word-value combined)))
      phrases (oracle/parse-letters combined {:max-words 4 :min-letters 2})]
  (println (format "  %d phrase readings" (count phrases)))
  (println "  With known meanings:")
  (let [with-meanings (filter (fn [p] 
                                (let [ms (map dict/translate p)]
                                  (>= (count (filter identity ms)) 2)))
                              phrases)]
    (doseq [p (take 30 with-meanings)]
      (let [text (str/join " " p)
            meanings (map dict/translate p)]
        (println (format "    %s → %s" text
                         (str/join " + " (map #(or % "?") meanings))))))))

;; Also check: does the oracle see תמים in the forced sum?
(println)
(println "=== Does atone+ransom+altar+blood contain תמים? ===")
(let [combined "כפרפדהמזבחדם"
      phrases (oracle/parse-letters combined {:max-words 4 :min-letters 2})
      has-tamim (some (fn [p] (some #(= "תמים" %) p)) phrases)]
  (println (str "  Contains תמים in any reading? " (boolean has-tamim))))

;; What about just כפרדם (atone + blood)?
(println)
(println "=== כפרדם (atone + blood, GV=344) ===")
(let [phrases (oracle/parse-letters "כפרדם" {:max-words 4 :min-letters 2})]
  (println (format "  %d phrase readings" (count phrases)))
  (doseq [p phrases]
    (let [text (str/join " " p)
          meanings (map dict/translate p)]
      (println (format "    %s → %s" text
                       (str/join " + " (map #(or % "?") meanings)))))))
