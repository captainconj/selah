(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.oracle :as oracle]
         '[clojure.string :as str])

;; The 13 readings of שבעים (seventy)
(println "=== שבעים (seventy, GV=422) — 13 phrase readings ===")
(println (str "  GV: " (g/word-value "שבעים")))
(let [phrases (oracle/parse-letters "שבעים")]
  (doseq [[i p] (map-indexed vector phrases)]
    (let [text (str/join " " p)
          meanings (map dict/translate p)
          gv (reduce + (map g/word-value p))]
      (println (format "  %2d. %s  →  [%s]  GV=%d"
                       (inc i) text
                       (str/join " + " (map #(or % "?") meanings))
                       gv)))))

;; And שבע (seven) — the agent said 2 but let me verify
(println)
(println "=== שבע (seven/swear, GV=372) ===")
(let [phrases (oracle/parse-letters "שבע")]
  (println (format "  %d readings:" (count phrases)))
  (doseq [p phrases]
    (let [text (str/join " " p)
          meanings (map dict/translate p)]
      (println (format "    %s → [%s]" text
                       (str/join " + " (map #(or % "?") meanings)))))))

;; So: 7 has 2 readings. 70 has 13 readings. 
;; What about 7x7=49 (jubilee count)?
(println)
(println "=== What words = 49 in gematria? ===")
(let [torah (dict/torah-words)
      gv49 (filter #(= 49 (g/word-value %)) torah)]
  (println (str "  " (count gv49) " words"))
  (doseq [w (sort (take 10 gv49))]
    (println (format "    %s  %s" w (or (dict/translate w) "")))))

;; What about the 25 words at GV=490? The key ones through oracle:
(println)
(println "=== מתים (the dead, GV=490, anagram of תמים) ===")
(let [phrases (oracle/parse-letters "מתים")]
  (println (format "  %d readings" (count phrases)))
  (doseq [p phrases]
    (let [text (str/join " " p)
          meanings (map dict/translate p)]
      (println (format "    %s → [%s]" text
                       (str/join " + " (map #(or % "?") meanings)))))))

(println)
(println "=== סלת (fine flour/grain offering, GV=490) ===")
(let [phrases (oracle/parse-letters "סלת")]
  (println (format "  %d readings" (count phrases)))
  (doseq [p phrases]
    (let [text (str/join " " p)
          meanings (map dict/translate p)]
      (println (format "    %s → [%s]" text
                       (str/join " + " (map #(or % "?") meanings)))))))

(println)
(println "=== מררים (bitter herbs, GV=490) ===")
(let [phrases (oracle/parse-letters "מררים")]
  (println (format "  %d readings" (count phrases)))
  (doseq [p (take 15 phrases)]
    (let [text (str/join " " p)
          meanings (map dict/translate p)
          has-m (some identity meanings)]
      (when has-m
        (println (format "    %s → [%s]" text
                         (str/join " + " (map #(or % "?") meanings))))))))

(println)
(println "=== נפשכם (your souls, GV=490) ===")
(let [phrases (oracle/parse-letters "נפשכם")]
  (println (format "  %d readings" (count phrases)))
  (doseq [p (take 15 phrases)]
    (let [text (str/join " " p)
          meanings (map dict/translate p)
          has-m (some identity meanings)]
      (when has-m
        (println (format "    %s → [%s]" text
                         (str/join " + " (map #(or % "?") meanings))))))))

;; Forced reading check for the GV=490 words
(println)
(println "=== FORCED READINGS (phrase-count=1) among GV=490 words ===")
(let [gv490-words ["תמים" "נפשכם" "מתים" "סלת" "מררים" "שקץ" "ערירי"
                   "והמשפטים" "טמאתם" "מטמאת" "כלתם" "תמלך"]]
  (doseq [w gv490-words]
    (let [phrases (oracle/parse-letters w)
          ilsets (oracle/illumination-sets w)]
      (println (format "  %s: %d phrases, %d illuminations  %s"
                       w (count phrases) (count ilsets)
                       (or (dict/translate w) ""))))))
