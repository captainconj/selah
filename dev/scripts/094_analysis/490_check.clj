(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.oracle :as oracle])

;; Verify the forced sum: atone + ransom + altar + blood = 490?
(let [words ["כפר" "פדה" "מזבח" "דם"]
      gvs (map g/word-value words)
      meanings (map dict/translate words)]
  (println "=== THE FORCED SUM ===")
  (doseq [[w gv m] (map vector words gvs meanings)]
    (println (format "  %s (%s) = %d" w m gv)))
  (println (format "  SUM = %d" (reduce + gvs)))
  (println (format "  = 490? %s" (= 490 (reduce + gvs)))))

(println)

;; What about נפשכם = 490?
(println "=== נפשכם (your souls) ===")
(println (format "  GV = %d" (g/word-value "נפשכם")))

;; Thummim phrase readings
(println)
(println "=== תמים through the oracle ===")
(let [phrases (oracle/parse-letters "תמים")]
  (println (format "  %d phrase readings:" (count phrases)))
  (doseq [p phrases]
    (println (format "    %s" (clojure.string/join " " p)))))

;; What about the combined word כפר+דם (atone+blood)?
(println)
(println "=== כפר+דם (atone + blood) combined ===")
(let [combined "כפרדם"
      phrases (oracle/parse-letters combined)]
  (println (format "  GV = %d" (g/word-value combined)))
  (println (format "  %d phrase readings:" (count phrases)))
  (doseq [p (take 20 phrases)]
    (let [text (clojure.string/join " " p)
          meanings (map dict/translate p)
          has-meaning (some identity meanings)]
      (when has-meaning
        (println (format "    %s  →  %s" text
                         (clojure.string/join " + " (map #(or % "?") meanings))))))))

;; What about כפר+פדה (atone + ransom)?
(println)
(println "=== כפר+פדה (atone + ransom) combined ===")
(let [combined "כפרפדה"
      phrases (oracle/parse-letters combined)]
  (println (format "  GV = %d" (g/word-value combined)))
  (println (format "  %d phrase readings:" (count phrases)))
  (doseq [p (take 30 phrases)]
    (let [text (clojure.string/join " " p)
          meanings (map dict/translate p)
          has-meaning (some identity meanings)]
      (when has-meaning
        (println (format "    %s  →  %s" text
                         (clojure.string/join " + " (map #(or % "?") meanings))))))))

;; Seventy sevens: שבעים שבעים
(println)
(println "=== שבע (seven) through oracle ===")
(let [phrases (oracle/parse-letters "שבע")]
  (println (format "  %d phrase readings:" (count phrases)))
  (doseq [p phrases]
    (let [text (clojure.string/join " " p)
          meanings (map dict/translate p)]
      (println (format "    %s  →  %s" text
                       (clojure.string/join " + " (map #(or % "?") meanings)))))))
