(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.oracle :as oracle]
         '[clojure.string :as str])

;; Check the return structure
(println "=== Structure check ===")
(let [r (oracle/parse-letters "שבע")]
  (println "Type:" (type r))
  (println "First:" (first r))
  (println "Type of first:" (type (first r))))

(println)

;; The 13 readings of שבעים (seventy) — using thummim-menu for full data
(println "=== שבעים (seventy, GV=422) ===")
(let [result (oracle/thummim-menu "שבעים")
      phrases (:phrases result)]
  (println (str "  Illuminations: " (:illumination-count result)))
  (println (str "  Phrase readings: " (count phrases)))
  (doseq [[i p] (map-indexed vector phrases)]
    (println (format "  %2d. %s  →  [%s]  GV=%d"
                     (inc i) (:text p)
                     (str/join " + " (map #(or % "?") (:meanings p)))
                     (:gv p)))))

(println)
(println "=== שבע (seven/swear, GV=372) ===")
(let [result (oracle/thummim-menu "שבע")]
  (println (str "  " (count (:phrases result)) " readings:"))
  (doseq [p (:phrases result)]
    (println (format "    %s → [%s]" (:text p)
                     (str/join " + " (map #(or % "?") (:meanings p)))))))

(println)
(println "=== תמים (Thummim, GV=490) ===")
(let [result (oracle/thummim-menu "תמים")]
  (println (str "  " (count (:phrases result)) " readings:"))
  (doseq [p (:phrases result)]
    (println (format "    %s → [%s]  GV=%d" (:text p)
                     (str/join " + " (map #(or % "?") (:meanings p)))
                     (:gv p)))))

(println)
(println "=== מתים (the dead, GV=490, anagram of תמים) ===")
(let [result (oracle/thummim-menu "מתים")]
  (println (str "  " (count (:phrases result)) " readings:"))
  (doseq [p (:phrases result)]
    (println (format "    %s → [%s]" (:text p)
                     (str/join " + " (map #(or % "?") (:meanings p)))))))

(println)
(println "=== נפשכם (your souls, GV=490) ===")
(let [result (oracle/thummim-menu "נפשכם")]
  (println (str "  Illuminations: " (:illumination-count result)))
  (println (str "  " (count (:phrases result)) " readings:"))
  (doseq [p (take 20 (:phrases result))]
    (let [ms (:meanings p)
          has-m (some identity ms)]
      (when has-m
        (println (format "    %s → [%s]" (:text p)
                         (str/join " + " (map #(or % "?") ms))))))))

(println)
(println "=== סלת (fine flour, GV=490) ===")
(let [result (oracle/thummim-menu "סלת")]
  (println (str "  " (count (:phrases result)) " readings:"))
  (doseq [p (:phrases result)]
    (println (format "    %s → [%s]" (:text p)
                     (str/join " + " (map #(or % "?") (:meanings p)))))))

(println)
(println "=== GV=490 forced check ===")
(doseq [w ["תמים" "נפשכם" "מתים" "סלת" "מררים" "שקץ"
           "והמשפטים" "טמאתם" "כלתם" "תמלך"]]
  (let [result (oracle/thummim-menu w)]
    (println (format "  %s: %d phrases, %d illuminations  %s"
                     w (count (:phrases result))
                     (:illumination-count result)
                     (or (dict/translate w) "")))))
