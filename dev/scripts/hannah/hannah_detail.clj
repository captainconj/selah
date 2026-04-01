(require '[selah.oracle :as o]
         '[selah.gematria :as g])

;; Detail every single שכרה reading
(println "=== ALL שכרה (drunk) READINGS — 41 total ===")
(println)
(let [hits (o/preimage "שכרה")]
  (doseq [[i h] (map-indexed vector (sort-by (juxt :reader #(pr-str (:positions %))) hits))]
    (println (str (inc i) ". " (name (:reader h)) " — stones " (:stones h) " — positions " (pr-str (sort-by first (:positions h)))))))

(println)
(println "=== ALL כשרה (like Sarah) READINGS — 3 total ===")
(println)
(let [hits (o/preimage "כשרה")]
  (doseq [[i h] (map-indexed vector (sort-by (juxt :reader #(pr-str (:positions %))) hits))]
    (println (str (inc i) ". " (name (:reader h)) " — stones " (:stones h) " — positions " (pr-str (sort-by first (:positions h))))))
  
  ;; Detail each כשרה reading
  (println)
  (println "=== כשרה DETAIL ===")
  (doseq [h hits]
    (println)
    (println (str "Reader: " (name (:reader h))))
    (println (str "Positions: " (pr-str (sort-by first (:positions h)))))
    (doseq [[stone pos] (sort-by first (:positions h))]
      (println (str "  Stone " stone " (" (get o/stone-tribe stone) ") pos " pos " = " (o/letter-at [stone pos])
                  " — row=" (get o/stone-row stone) " col=" (get o/stone-col stone))))))

;; Cross-check: the doc says "21 readings" for שכרה and "2 readings" for כשרה
;; vs our "41 readings" and "3 readings"
;; The doc might count by unique illumination patterns, not reader×pattern
(println)
(println "=== RECONCILIATION WITH DOCUMENTED COUNTS ===")
(let [shkrh-hits (o/preimage "שכרה")
      kshrh-hits (o/preimage "כשרה")
      shkrh-psets (set (map :positions shkrh-hits))
      kshrh-psets (set (map :positions kshrh-hits))]
  (println (str "שכרה: " (count shkrh-hits) " reader-readings from " (count shkrh-psets) " distinct illumination patterns"))
  (println (str "כשרה: " (count kshrh-hits) " reader-readings from " (count kshrh-psets) " distinct illumination patterns"))
  
  ;; Count distinct (reader, illumination) for non-Aaron readers
  (let [non-aaron-shkrh (filter #(not= :aaron (:reader %)) shkrh-hits)
        non-aaron-kshrh (filter #(not= :aaron (:reader %)) kshrh-hits)]
    (println (str "שכרה non-Aaron: " (count non-aaron-shkrh) " readings"))
    (println (str "כשרה non-Aaron: " (count non-aaron-kshrh) " readings")))
  
  ;; Break down שכרה by reader more carefully
  (println)
  (println "שכרה readings by reader and distinct illumination set:")
  (doseq [reader [:aaron :god :truth :mercy]]
    (let [reader-hits (filter #(= reader (:reader %)) shkrh-hits)
          reader-psets (set (map :positions reader-hits))]
      (println (str "  " (name reader) ": " (count reader-hits) " readings from " (count reader-psets) " illumination patterns"))))
  
  (println)
  (println "כשרה readings by reader and distinct illumination set:")
  (doseq [reader [:aaron :god :truth :mercy]]
    (let [reader-hits (filter #(= reader (:reader %)) kshrh-hits)
          reader-psets (set (map :positions reader-hits))]
      (println (str "  " (name reader) ": " (count reader-hits) " readings from " (count reader-psets) " illumination patterns")))))

;; The 088 orbiting data
(println)
(println "=== EXPERIMENT 088: STOCHASTIC ORBIT ===")
(println "From orbiting.edn in 088:")
(println "כשרה self-weight 0.91, שכרה partner-weight 0.09")
(println "שכרה self-weight 0.50, כשרה partner-weight 0.50")
(println "This means: starting from 'like Sarah', the oracle returns to 'like Sarah' 91% of the time.")
(println "Starting from 'drunk', it's a coin flip — 50/50 between drunk and like Sarah.")
(println "The asymmetry: the CORRECT reading is a strong attractor. The WRONG reading is unstable.")
