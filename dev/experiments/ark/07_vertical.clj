;; Ark Walk 07 — Vertical Alignment
;; Cross-layer analysis: what stacks vertically at key d-positions
;; The Ark read as a 6×67 grid (c=7-12, d=0-66)

(require '[selah.space.coords :as sc]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [c d] (sc/letter-at s (sc/coord->idx 0 8 c d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; Vertical column at d
      vert-letters (fn [d] (mapv #(at % d) (range 7 13)))
      vert-gv (fn [d] (reduce + (map #(g/letter-value (at % d)) (range 7 13))))

      ;; Layer names
      names {7 "Found" 8 "Spec" 9 "Dir" 10 "Cov" 11 "Board" 12 "Tax"}]

  ;; ═══ FULL VERTICAL SCAN ═══
  (println "=== VERTICAL SCAN: ALL 67 COLUMNS ===")
  (println "  d   c7    c8    c9    c10   c11   c12   sum")
  (println "  --- ----- ----- ----- ----- ----- ----- ----")
  (doseq [d (range 67)]
    (let [letters (vert-letters d)
          sum (vert-gv d)]
      (println (format "  %-3d %-5s %-5s %-5s %-5s %-5s %-5s %d"
                       d
                       (str (nth letters 0) "(" (g/letter-value (nth letters 0)) ")")
                       (str (nth letters 1) "(" (g/letter-value (nth letters 1)) ")")
                       (str (nth letters 2) "(" (g/letter-value (nth letters 2)) ")")
                       (str (nth letters 3) "(" (g/letter-value (nth letters 3)) ")")
                       (str (nth letters 4) "(" (g/letter-value (nth letters 4)) ")")
                       (str (nth letters 5) "(" (g/letter-value (nth letters 5)) ")")
                       sum))))
  (println)

  ;; ═══ COLUMN SUMS ═══
  (println "=== NOTABLE COLUMN SUMS ===")
  (doseq [d (range 67)]
    (let [sum (vert-gv d)]
      (when (or (zero? (mod sum 7))
                (zero? (mod sum 13))
                (zero? (mod sum 26)))
        (let [letters (apply str (vert-letters d))]
          (println (format "  d=%-2d  sum=%-4d  letters=%s  factors=%s"
                           d sum letters (factorize sum)))))))
  (println)

  ;; ═══ IDENTICAL POSITIONS ═══
  (println "=== IDENTICAL LETTERS ACROSS LAYERS ===")
  (doseq [d (range 67)]
    (let [letters (vert-letters d)
          unique (distinct letters)]
      (when (< (count unique) 4)
        (println (format "  d=%-2d  letters=%s  unique=%d"
                         d (str/join " " (map str letters)) (count unique))))))
  (println)

  ;; ═══ KEY POSITIONS ═══
  (println "=== KEY VERTICAL POSITIONS ===")
  (doseq [[label d-pos] [["First" 0] ["Door" 14] ["Rooms" 28]
                          ["Garden" 53] ["Pairs" 59] ["Bridge" 66]]]
    (println (format "\n  %s (d=%d):" label d-pos))
    (doseq [c (range 12 6 -1)]
      (println (format "    c=%-2d %-6s: %s (gv=%d)"
                       c (names c) (at c d-pos) (g/letter-value (at c d-pos)))))
    (println (format "    Sum: %d" (vert-gv d-pos))))
  (println)

  ;; ═══ THREE FLOORS VS THREE DECKS ═══
  (println "=== THREE FLOORS (c=7,8,9) vs THREE DECKS (c=10,11,12) ===")
  (let [floor-total (reduce + (for [c [7 8 9] d (range 67)]
                                (g/letter-value (at c d))))
        deck-total (reduce + (for [c [10 11 12] d (range 67)]
                               (g/letter-value (at c d))))]
    (println (format "  Three floors GV = %d  factors = %s"
                     floor-total (factorize floor-total)))
    (println (format "  Three decks GV  = %d  factors = %s"
                     deck-total (factorize deck-total)))
    (println (format "  Total (all 6)   = %d  factors = %s"
                     (+ floor-total deck-total) (factorize (+ floor-total deck-total)))))
  (println)

  ;; ═══ BRIDGE WORDS: LAYER-TO-LAYER ═══
  (println "=== BRIDGE WORDS (d=66 column) ===")
  (let [col (apply str (for [c (range 7 13)] (at c 66)))]
    (println (format "  Full bridge column: %s  GV=%d" col (g/word-value col)))
    (println "  Below flood (c=7,8,9):")
    (let [below (apply str (for [c [7 8 9]] (at c 66)))]
      (println (format "    %s  GV=%d  %s" below (g/word-value below) (or (dict/translate below) ""))))
    (println "  Above flood (c=10,11,12):")
    (let [above (apply str (for [c [10 11 12]] (at c 66)))]
      (println (format "    %s  GV=%d  %s" above (g/word-value above) (or (dict/translate above) ""))))))
