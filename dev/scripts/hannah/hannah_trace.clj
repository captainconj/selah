;; Hannah/Eli breastplate trace
;; Run with: clojure -M:dev -e '(load-file "/tmp/hannah_trace.clj")'

(require '[selah.oracle :as o]
         '[selah.gematria :as g])

(println "=== LETTER POSITIONS ON THE GRID ===")
(println)
(doseq [ch [\ש \כ \ר \ה]]
  (println (str ch " appears at:"))
  (doseq [[stone pos] (get o/letter-index ch)]
    (let [letters (get o/stone-letters stone)
          tribe (get o/stone-tribe stone)
          row (get o/stone-row stone)
          col (get o/stone-col stone)]
      (println (str "  Stone " stone " (" tribe "), row=" row " col=" col ", pos=" pos " — letter " (nth letters pos) " in " (apply str letters)))))
  (println))

(println "=== ILLUMINATION SETS for letters {ש,כ,ר,ה} ===")
(println "(These are the same for שכרה and כשרה — same multiset of letters)")
(println)
(let [ilsets (o/illumination-sets "שכרה")]
  (println (str "Total illumination sets: " (count ilsets)))
  (println)
  
  ;; For each illumination set, what does each reader see?
  (println "=== READINGS PER ILLUMINATION SET ===")
  (println)
  (let [shkrh-count (atom 0)
        kshrh-count (atom 0)
        both-count (atom 0)]
    (doseq [[i pset] (map-indexed vector ilsets)]
      (let [rdgs (o/readings pset)
            has-shkrh (some (fn [[r w]] (= w "שכרה")) rdgs)
            has-kshrh (some (fn [[r w]] (= w "כשרה")) rdgs)]
        (when (or has-shkrh has-kshrh)
          (println (str "Illumination #" (inc i) ":"))
          (println (str "  Positions: " (pr-str (sort-by first pset))))
          (doseq [[stone pos] (sort-by first pset)]
            (println (str "    Stone " stone " pos " pos " = " (o/letter-at [stone pos])
                        " (" (get o/stone-tribe stone) ", row=" (get o/stone-row stone) " col=" (get o/stone-col stone) ")")))
          (doseq [[reader word] (sort-by first rdgs)]
            (let [marker (cond (= word "שכרה") " ← DRUNK"
                              (= word "כשרה") " ← LIKE SARAH"
                              :else "")]
              (println (str "  " (name reader) " reads: " word marker))))
          (when (and has-shkrh has-kshrh)
            (swap! both-count inc)
            (println "  *** SAME LIGHT, DIFFERENT READINGS ***"))
          (println))))
    
    (println)
    (println (str "Illumination sets where BOTH words appear from different readers: " @both-count)))
  
  ;; Now use preimage for exact counts
  (println)
  (println "=== PREIMAGE: EXACT READING COUNTS ===")
  (println)
  (let [pre-shkrh (o/preimage "שכרה")
        pre-kshrh (o/preimage "כשרה")]
    (println (str "שכרה (drunk) — " (count pre-shkrh) " total readings:"))
    (doseq [[reader cnt] (sort-by first (frequencies (map :reader pre-shkrh)))]
      (println (str "  " (name reader) ": " cnt)))
    (println)
    (println (str "כשרה (like Sarah) — " (count pre-kshrh) " total readings:"))
    (doseq [[reader cnt] (sort-by first (frequencies (map :reader pre-kshrh)))]
      (println (str "  " (name reader) ": " cnt)))
    
    ;; Now find overlapping position-sets
    (println)
    (println "=== SHARED ILLUMINATIONS (same light, different word) ===")
    (let [shkrh-psets (set (map :positions pre-shkrh))
          kshrh-psets (set (map :positions pre-kshrh))
          shared (clojure.set/intersection shkrh-psets kshrh-psets)]
      (println (str "Position-sets that produce שכרה for some reader: " (count shkrh-psets)))
      (println (str "Position-sets that produce כשרה for some reader: " (count kshrh-psets)))
      (println (str "Position-sets shared (produce BOTH from different readers): " (count shared)))
      (println)
      (doseq [pset shared]
        (println (str "Shared illumination: " (pr-str (sort-by first pset))))
        (let [rdgs (o/readings pset)]
          (doseq [[reader word] (sort-by first rdgs)]
            (let [marker (cond (= word "שכרה") " ← DRUNK"
                              (= word "כשרה") " ← LIKE SARAH"
                              :else "")]
              (println (str "  " (name reader) " reads: " word marker)))))
        (println))))
  
  ;; Gematria
  (println "=== GEMATRIA ===")
  (println (str "שכרה = " (g/word-value "שכרה")))
  (println (str "כשרה = " (g/word-value "כשרה")))
  (println (str "Both = 525 = " (let [v 525] (str "3 × 5² × 7"))))
  (println (str "525 ÷ 7 = " (/ 525 7)))
  
  ;; Forward query for all readings
  (println)
  (println "=== FORWARD QUERY (all readings from these letters) ===")
  (let [fwd (o/forward "שכרה")]
    (println (str "Total illumination sets: " (:illumination-count fwd)))
    (println (str "Total readings (4 readers × illuminations): " (:total-readings fwd)))
    (println)
    (println "Known words produced:")
    (doseq [w (:known-words fwd)]
      (println (str "  " (:word w) " (" (:meaning w) ") — " (:reading-count w) " readings by " (:readers w))))
    (println)
    (println "Unknown words produced (top 10 by rarity):")
    (doseq [w (take 10 (:unknown-words fwd))]
      (println (str "  " (:word w) " — " (:reading-count w) " readings"))))))
