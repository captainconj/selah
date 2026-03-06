;; Ark Walk 00 — The Column
;; Noah's Ark in 4D space: a=0, b=8, c=7-12 (six layers of 67 letters)
;; The Ark's address: Gen 6:15 dimensions 300×50×30 → letters ש,נ,ל → לשן (tongue/language)
;; From Experiment 095: z=7.88 self-reference score.

(require '[selah.space.coords :as sc]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      ;; The Ark column: a=0, b=8, six layers
      ;; c=7 Foundation, c=8 Specification, c=9 Directory/Door
      ;; c=10 Covenant, c=11 Boarding, c=12 Taxonomy
      layer-names {7 "Foundation" 8 "Specification" 9 "Directory"
                   10 "Covenant" 11 "Boarding" 12 "Taxonomy"}

      ;; Helper: read a full layer as string
      layer-letters (fn [c]
                      (apply str (for [d (range 67)]
                                   (sc/letter-at s (sc/coord->idx 0 8 c d)))))

      ;; Helper: read a single letter at position
      at (fn [c d] (sc/letter-at s (sc/coord->idx 0 8 c d)))

      ;; Helper: GV of a letter
      lgv (fn [c d] (g/letter-value (at c d)))

      ;; Helper: extract word starting at d for len letters on layer c
      word-at (fn [c d len]
                (apply str (for [i (range d (+ d len))]
                             (at c i))))

      ;; Helper: vertical column at d across layers
      vert (fn [d] (mapv #(at % d) [7 8 9 10 11 12]))

      ;; Helper: vertical GV sum
      vert-gv (fn [d] (reduce + (map #(lgv % d) [7 8 9 10 11 12])))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))]

  ;; ═══ SIX LAYERS ═══
  (println "=== THE ARK COLUMN: a=0, b=8, c=7-12 ===")
  (println)
  (doseq [c (range 7 13)]
    (let [letters (layer-letters c)
          gv (g/word-value letters)]
      (println (format "  c=%d  %-15s  GV=%-6d  factors=%s"
                       c (layer-names c) gv (factorize gv)))
      (println (format "    %s" letters))
      (println)))

  ;; ═══ THREE FLOORS (below flood) total ═══
  (println "=== THREE FLOORS (c=7,8,9) TOTAL ===")
  (let [total (reduce + (for [c [7 8 9]]
                           (g/word-value (layer-letters c))))]
    (println (format "  Total GV = %d  factors = %s" total (factorize total))))
  (println)

  ;; ═══ DOOR COLUMN (d=14) ═══
  (println "=== DOOR COLUMN (d=14) ===")
  (doseq [c (range 12 6 -1)]
    (let [letter (at c 14)
          gv (g/letter-value letter)]
      (println (format "  c=%-2d %-15s  %s  gv=%d"
                       c (layer-names c) letter gv))))
  (let [door-sum (reduce + (map #(lgv % 14) (range 7 13)))]
    (println (format "  Door column sum = %d" door-sum)))
  (println)

  ;; Door column letters: c=7,8,9 and c=10,11,12
  (let [below (apply str (map #(at % 14) [7 8 9]))
        above (apply str (map #(at % 14) [10 11 12]))]
    (println (format "  Below flood (c=7,8,9): %s  gv=%d  %s"
                     below (g/word-value below) (or (dict/translate below) "?")))
    (println (format "  Above flood (c=10,11,12): %s  gv=%d  %s"
                     above (g/word-value above) (or (dict/translate above) "?"))))
  (println)

  ;; ═══ BRIDGE COLUMN (d=66) ═══
  (println "=== BRIDGE COLUMN (d=66) ===")
  (doseq [c (range 12 6 -1)]
    (let [letter (at c 66)
          gv (g/letter-value letter)]
      (println (format "  c=%-2d %-15s  %s  gv=%d"
                       c (layer-names c) letter gv))))
  (let [bridge-sum (reduce + (map #(lgv % 66) (range 7 13)))]
    (println (format "  Bridge column sum = %d" bridge-sum)))
  ;; Top two letters
  (let [top2 (str (at 12 66) (at 11 66))]
    (println (format "  Top two (c=12,11): %s  gv=%d  %s"
                     top2 (g/word-value top2) (or (dict/translate top2) "?"))))
  (println)

  ;; ═══ FIRST COLUMN (d=0) ═══
  (println "=== FIRST COLUMN (d=0) ===")
  (doseq [c (range 12 6 -1)]
    (let [letter (at c 0)
          gv (g/letter-value letter)]
      (println (format "  c=%-2d %-15s  %s  gv=%d"
                       c (layer-names c) letter gv))))
  ;; Three floor letters rearranged
  (let [floor3 (apply str (map #(at % 0) [7 8 9]))]
    (println (format "  Three floors (c=7,8,9): %s  gv=%d"
                     floor3 (g/word-value floor3)))
    ;; Try anagrams
    (doseq [perm (distinct (map #(apply str %) [[(\נ \ש \מ) (\ש \מ \נ) (\מ \נ \ש) (\נ \מ \ש) (\ש \נ \מ) (\מ \ש \נ)]]))]
      (when-let [t (dict/translate perm)]
        (println (format "    anagram: %s = %s" perm t)))))
  (println)

  ;; ═══ VERTICAL SCANS AT KEY POSITIONS ═══
  (println "=== VERTICAL SCANS ===")
  (doseq [d [0 14 28 42 53 59 66]]
    (let [letters (vert d)
          col-str (apply str letters)]
      (println (format "  d=%-2d: %s  (c=7→12)  sum=%d"
                       d (str/join " " (map str letters)) (vert-gv d)))))
  (println)

  ;; ═══ WORD EXTRACTION PER LAYER ═══
  (println "=== LAYER WORD BOUNDARIES ===")
  (println "(words extracted by walking each layer)")
  (doseq [c (range 7 13)]
    (println (format "\n  c=%d  %s:" c (layer-names c)))
    (let [letters (layer-letters c)
          ;; Find verse boundaries that fall on this layer
          vrefs (:verse-ref s)
          layer-start (sc/coord->idx 0 8 c 0)
          layer-end (sc/coord->idx 0 8 c 66)]
      ;; Walk and print words with their d-positions
      (doseq [d (range 67)]
        (let [idx (sc/coord->idx 0 8 c d)
              v (sc/verse-at s idx)
              letter (sc/letter-at s idx)]
          (print letter)))
      (println))))
