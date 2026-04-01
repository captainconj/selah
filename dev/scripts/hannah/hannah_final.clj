(require '[selah.oracle :as o]
         '[selah.gematria :as g])

;; === COMBINATORIAL ANALYSIS ===
;; WHY is שכרה so much easier than כשרה?

;; Key insight: כ appears ONLY on stone 8 (Issachar), position 4
;; Stone 8 is at row=3, col=2 (the center column)

;; For the readers:
;; - God reads bottom-up, L-to-R. כ at row 3 means it's read AFTER rows 4 and BEFORE rows 1,2
;; - Right cherub reads R-to-L columns, top-bottom. כ at col 2 is read FIRST (before col 1)
;; - Left cherub reads L-to-R columns, bottom-top. כ at col 2 is read SECOND (after col 1)
;; - Aaron reads R-to-L rows, top-bottom. כ at row 3 col 2 is read LATE

;; For שכרה: ש must come BEFORE כ in reading order
;; For כשרה: כ must come BEFORE ש in reading order

;; The question: how many letter placements allow ש before כ vs כ before ש?

;; ש has 6 positions on the grid (stones 4,7,8,8,11,12)
;; But two of those are on stone 8 itself (positions 2 and 3)

;; When ש is on stone 8 (same stone as כ):
;;   ש at pos 2, כ at pos 4: within same stone, ש comes first (lower idx)
;;   ש at pos 3, כ at pos 4: within same stone, ש comes first
;;   → All readers see ש before כ on same stone → favors שכרה

;; When ש is on a different stone:
;;   - Stone 4 (row 2, col 1) vs כ on stone 8 (row 3, col 2)
;;   - Stone 7 (row 3, col 1) vs כ on stone 8 (row 3, col 2)
;;   - Stone 11 (row 4, col 2) vs כ on stone 8 (row 3, col 2)
;;   - Stone 12 (row 4, col 3) vs כ on stone 8 (row 3, col 2)

(println "=== ש POSITIONS AND THEIR RELATIONSHIP TO כ (stone 8, row 3, col 2) ===")
(println)
(let [kaf-stone 8 kaf-row 3 kaf-col 2]
  (doseq [[stone pos] (get o/letter-index \ש)]
    (let [r (o/stone-row stone) c (o/stone-col stone)]
      (println (str "ש at stone " stone " (row=" r " col=" c ") — " (get o/stone-tribe stone)))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [shin-key (o/read-key reader [stone pos])
              kaf-key  (o/read-key reader [kaf-stone 4])]
          (println (str "  " (name reader) ": ש-key=" shin-key " כ-key=" kaf-key 
                       " → " (if (neg? (compare (vec shin-key) (vec kaf-key))) "ש before כ (שכרה-favorable)" "כ before ש (כשרה-favorable)"))))))))

(println)
(println "=== COUNTING THE ASYMMETRY ===")
(println)

;; For each ש position × each reader, is the configuration שכרה-favorable or כשרה-favorable?
(let [kaf-stone 8 kaf-pos 4
      results (for [[stone pos] (get o/letter-index \ש)
                    reader [:aaron :god :truth :mercy]]
                (let [shin-key (o/read-key reader [stone pos])
                      kaf-key  (o/read-key reader [kaf-stone kaf-pos])
                      order (compare (vec shin-key) (vec kaf-key))]
                  {:shin-stone stone :reader reader 
                   :order (cond (neg? order) :shin-first
                               (pos? order) :kaf-first
                               :else :same)}))
      by-order (frequencies (map :order results))]
  (println "Across all 6 ש-positions × 4 readers = 24 configurations:")
  (println (str "  ש before כ (שכרה-favorable): " (:shin-first by-order 0)))
  (println (str "  כ before ש (כשרה-favorable): " (:kaf-first by-order 0)))
  (println (str "  Same position (impossible): " (:same by-order 0)))
  (println)
  (println "The asymmetry is " (format "%.1f" (* 100.0 (/ (:shin-first by-order 0) 
                                                          (+ (:shin-first by-order 0) (:kaf-first by-order 0)))))
           "% in favor of שכרה."))

(println)
(println "=== WHY כ IS THE BOTTLENECK ===")
(println)
(println "כ appears exactly ONCE on the entire 72-letter grid: stone 8, position 4.")
(println "Every word containing כ must use this single position.")
(println "The position of כ relative to all other letters is FIXED.")
(println "This creates a hard geometric constraint: most reading orders")
(println "will encounter other letters BEFORE כ, making שכ... (ש then כ) much")
(println "more common than כש... (כ then ש).")
(println)
(println "Stone 8 = Issachar = 'there is reward' (יש שכר)")
(println "The word שכר (reward/wages) is literally inscribed on this stone.")
(println "The drunk reading (שכרה) follows the natural direction of שכר.")
(println "The Sarah reading (כשרה) REVERSES the natural order — כ must come first.")
(println)

;; Final: the 3 כשרה readings - what makes them special?
(println "=== THE THREE PATHS TO TRUTH ===")
(println)
(let [hits (o/preimage "כשרה")]
  (doseq [h hits]
    (let [positions (:positions h)
          stones (set (map first positions))]
      (println (str (name (:reader h)) " reads כשרה from stones: " (sort stones)))
      (doseq [[s i] (sort-by first positions)]
        (println (str "  " (o/letter-at [s i]) " on stone " s " (" (get o/stone-tribe s) 
                     "), row=" (o/stone-row s) " col=" (o/stone-col s))))
      (println))))

(println "=== GEMATRIA IMPLICATIONS ===")
(println (str "525 = 3 × 5² × 7"))
(println (str "525 ÷ 7 = 75"))
(println (str "525 ÷ 3 = 175 = Abraham's lifespan (Gen 25:7)"))
(println (str "525 ÷ 25 = 21 = the number of wrong readings (original 3-reader count)"))
(println (str "21 = אהיה (I AM/I will be, Exodus 3:14) = 3 × 7"))
