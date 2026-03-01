(ns experiments.066-center-grid
  "The Holy of Holies — the 13×67 grid at the center of the 4D space.

   304,850 = 7 × 50 × 13 × 67.  Every letter has address (a, b, c, d).

   At the center of the two outer axes (a=3, b=25), there are exactly
   871 = 13 × 67 letters — one 'love × understanding' block.

   The Tabernacle's Holy of Holies is a perfect cube: a space where
   all dimensions are equal, maximal symmetry. This is the analogue
   in the letter stream: extract the center block and read it as a grid.

   Rows are d-fibers (67 consecutive letters, understanding lines).
   Columns are c-fibers (13 letters, love/unity lines).
   Diagonals cross both axes simultaneously.

   FINDINGS:

   1. THE CENTER LETTER — a=3, b=25, c=6, d=33 = position 152,860.
      Letter: ו (vav, value 6). Verse: Leviticus 8:35.
      Vav is the letter of connection. Six is the number of creation days.
      The center of the 4D space is a connector.

   2. VERSE SPAN — The 871-letter Holy of Holies spans Leviticus 8:29–9:7.
      15 verses. This is the CONSECRATION OF THE PRIESTHOOD:
        - Lev 8:29-36: Moses completes the ordination (wave offering, anointing)
        - Lev 9:1-7: The EIGHTH DAY — Aaron begins his own ministry
      The center of the space contains the transition from Moses to Aaron,
      from preparation to service. The handoff.

   3. THE CENTER ROW (c=6, the center d-fiber):
      'And day and night, SEVEN DAYS, you shall GUARD the CHARGE of YHWH,
       that you do not die, for so I have been commanded.'
      Contains שמר (guard) TWICE — at offsets 19 and 27.
      Gematria: 6,070 — the highest of all 13 rows.

   4. COLUMN WORDS — The 13-letter love-axis columns contain:
      - אל (God) — 8 columns contain it
      - את (aleph-tav) — 5 columns
      - לב (heart) — 4 columns
      - דם (blood) — 2 columns
      - יד (hand) — 2 columns
      - שם (name) — 1 column
      - בן (son) — 1 column
      - אב (father) — 1 column
      Reading vertically through the love axis yields: God, aleph-tav,
      heart, blood, hand, name, son, father. The vocabulary of covenant.

   5. DIAGONAL LIGHT — Two main diagonals contain אור (light):
      - Diagonal 17: נעואורםואכנמא — 'light' at offset 3
      - Diagonal 23: מחדאאורולשידך — 'light' at offset 4
      Light appears on the diagonals of the Holy of Holies.
      The Urim (אורים, 'lights') sits nearby in Lev 8:8.

   6. ANTI-DIAGONAL 31 = 871 — The 31st anti-diagonal reads:
      העהשוואנתהיהח — gematria sum = 871 = 13 × 67.
      This diagonal's gematria equals the number of letters in the grid.
      The grid contains its own measure on one of its diagonals.
      871 divides by BOTH inner dimensions: ÷13 = 67, ÷67 = 13.

   7. DIVISIBILITY BY 67 — Two main diagonals and two anti-diagonals
      have gematria divisible by 67 (understanding). Two columns as well.
      Expected by chance: ~1.5%. Observed in diagonals: ~3.6%.
      Understanding (67) threads through the diagonal fabric.

   8. DIVISIBILITY BY 13 IN MAIN DIAGONALS — Five of 55:
      diag 2 (962=13×74), diag 4 (1222=13×94), diag 5 (962=13×74),
      diag 51 (611=13×47). 9.1% vs 7.7% expected.
      1222 = 13 × 94 — and 1222 is the number of self-mirroring את
      under the a-fold (from experiment 061). The grid echoes that count.

   9. COLUMN d=24 IS SPECIAL — gematria 469 = 7 × 67.
      The ONLY column divisible by BOTH 7 and 67. Column 24 is
      the jubilee midpoint (b has 50 values, 24 is near center).
      Seven × understanding on a single love-line.

  10. COLUMN d=47 — gematria 1,474 = 67 × 22.
      22 = the number of Hebrew letters. Understanding × alphabet.

  11. SYMMETRY — c-mirror pairs (love fold within the grid) show:
      Div-by-13 deficit: only 3.5% vs 7.7% expected.
      The love axis within the Holy of Holies does NOT divide by love
      at the pair level. The asymmetry suggests directionality —
      the love axis flows, it doesn't mirror.

  12. ROW c=0 DIVIDES BY 7 — gematria 3,514 = 7 × 502.
      The first row of the Holy of Holies is divisible by completeness."
  (:require [selah.space.coords :as coords]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Grid extraction ──────────────────────────────────────

(defn extract-grid
  "Extract the 13×67 grid at a=3, b=25. Returns a 2D vector of
   {:letter :gv :pos :c :d :verse} maps. grid[c][d]."
  [s]
  (vec
   (for [c (range 13)]
     (vec
      (for [d (range 67)]
        (let [pos (coords/coord->idx 3 25 c d)]
          {:letter (coords/letter-at s pos)
           :gv     (coords/gv-at s pos)
           :pos    pos
           :c      c
           :d      d
           :verse  (coords/verse-at s pos)}))))))

(defn grid-letters
  "Extract the raw letter string for a grid row or arbitrary cell sequence."
  [cells]
  (apply str (map :letter cells)))

(defn grid-gv-sum
  "Sum gematria values for a sequence of cells."
  [cells]
  (reduce + (map :gv cells)))

;; ── Reading directions ───────────────────────────────────

(defn row [grid c]
  (nth grid c))

(defn col [grid d]
  (mapv #(nth % d) grid))

(defn main-diagonals
  "Diagonals at slope (1,1): c increases, d increases.
   Starting from (0, d0) for d0=0..66, then (c0, 0) for c0=1..12.
   Each diagonal has min(13-c0, 67-d0) cells."
  [grid]
  (concat
   ;; Start from top edge: c=0, d=0..66
   (for [d0 (range 67)]
     (let [len (min 13 (- 67 d0))]
       (vec (for [i (range len)]
              (get-in grid [(+ 0 i) (+ d0 i)])))))
   ;; Start from left edge: c=1..12, d=0
   (for [c0 (range 1 13)]
     (let [len (min (- 13 c0) 67)]
       (vec (for [i (range len)]
              (get-in grid [(+ c0 i) (+ 0 i)])))))))

(defn anti-diagonals
  "Diagonals at slope (1,-1): c increases, d decreases.
   Starting from (0, d0) for d0=0..66, then (c0, 66) for c0=1..12."
  [grid]
  (concat
   ;; Start from top edge: c=0, d=0..66
   (for [d0 (range 67)]
     (let [len (min 13 (inc d0))]
       (vec (for [i (range len)]
              (get-in grid [(+ 0 i) (- d0 i)])))))
   ;; Start from right edge: c=1..12, d=66
   (for [c0 (range 1 13)]
     (let [len (min (- 13 c0) 67)]
       (vec (for [i (range len)]
              (get-in grid [(+ c0 i) (- 66 i)])))))))

;; ── Word search ──────────────────────────────────────────

(def target-words
  {"שמר"  "guard"
   "אמת"  "truth"
   "תורה" "Torah"
   "יהוה" "YHWH"
   "אהבה" "love"
   "בינה" "understanding"
   "אחד"  "one"
   "שבע"  "seven"
   "אור"  "light"
   "חיים" "life"
   "לב"   "heart"
   "אל"   "God"
   "את"   "aleph-tav"
   "יום"  "day"
   "שלם"  "peace/whole"
   "קדש"  "holy"
   "כהן"  "priest"
   "דם"   "blood"
   "יד"   "hand"
   "עין"  "eye"
   "פה"   "mouth"
   "שם"   "name"
   "בן"   "son"
   "אב"   "father"})

(defn search-in-line
  "Search for all target words in a letter sequence.
   Returns seq of {:word :meaning :offset}."
  [letters]
  (let [s (apply str (map :letter letters))]
    (for [[word meaning] target-words
          :let [idx (.indexOf s word)]
          :when (>= idx 0)]
      {:word word :meaning meaning :offset idx})))

(defn search-all-subsequences
  "Search for target words in ALL contiguous subsequences of a line.
   Returns seq of {:word :meaning :offset}."
  [letters]
  (let [s (apply str (map :letter letters))
        results (atom [])]
    (doseq [[word meaning] target-words]
      (loop [start 0]
        (let [idx (.indexOf s word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :meaning meaning :offset idx})
            (recur (inc idx))))))
    @results))

;; ── Verse helpers ────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

;; ── Main ─────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 066: THE CENTER GRID — THE HOLY OF HOLIES")
  (println "  871 letters at a=3, b=25: the 13×67 love×understanding block")
  (println "================================================================")
  (println)

  (let [s    (coords/space)
        grid (extract-grid s)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 0: THE CENTER LETTER
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 0: THE CENTER OF THE CENTER")
    (println "  a=3, b=25, c=6, d=33 — THE center letter of the 4D space")
    (println "================================================================")
    (println)

    (let [center-pos (coords/coord->idx 3 25 6 33)
          center-cell (get-in grid [6 33])
          v (:verse center-cell)]
      (println (format "  Position: %,d of %,d" center-pos coords/total-letters))
      (println (format "  Letter: %s" (:letter center-cell)))
      (println (format "  Gematria value: %d" (:gv center-cell)))
      (println (format "  Verse: %s" (verse-label v)))
      ;; Show context — the full row (d-fiber) through the center
      (let [center-row (row grid 6)]
        (println (format "  Center row (c=6): %s" (grid-letters center-row)))
        (println (format "  Center row gematria: %,d" (grid-gv-sum center-row)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: WHAT TEXT IS THIS?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: VERSE SPAN")
    (println "  What biblical text do these 871 letters cover?")
    (println "================================================================")
    (println)

    (let [first-cell (get-in grid [0 0])
          last-cell  (get-in grid [12 66])
          first-v (:verse first-cell)
          last-v  (:verse last-cell)]
      (println (format "  First letter: position %,d — %s" (:pos first-cell) (verse-label first-v)))
      (println (format "  Last letter:  position %,d — %s" (:pos last-cell) (verse-label last-v)))
      (println)

      ;; List all verses that appear in this block
      (let [all-verses (distinct (map (fn [cell] (verse-label (:verse cell)))
                                      (apply concat grid)))]
        (println (format "  Spans %d verses:" (count all-verses)))
        (doseq [v all-verses]
          (println (format "    %s" v)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 2: THE GRID — visual print
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 2: THE 13×67 GRID")
    (println "  Rows = d-fibers (understanding), Columns = c-fibers (love)")
    (println "================================================================")
    (println)

    (doseq [c (range 13)]
      (let [r (row grid c)]
        (println (format "  c=%2d │ %s │ gv=%,d" c (grid-letters r) (grid-gv-sum r)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 3: ROW ANALYSIS (d-fibers, 67 letters each)
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 3: ROW ANALYSIS — 13 rows of 67 letters")
    (println "================================================================")
    (println)

    (let [row-gvs (vec (for [c (range 13)]
                         (grid-gv-sum (row grid c))))
          total (reduce + row-gvs)]
      (doseq [c (range 13)]
        (let [gv (nth row-gvs c)
              div7  (zero? (mod gv 7))
              div13 (zero? (mod gv 13))
              div67 (zero? (mod gv 67))
              divs (str (when div7 " ÷7") (when div13 " ÷13") (when div67 " ÷67"))]
          (println (format "  c=%2d  gv=%,6d%s" c gv divs))))
      (println)
      (println (format "  Total grid gematria: %,d" total))
      (let [div7  (zero? (mod total 7))
            div13 (zero? (mod total 13))
            div67 (zero? (mod total 67))]
        (when div7  (println (format "    ÷7  = %,d" (/ total 7))))
        (when div13 (println (format "    ÷13 = %,d" (/ total 13))))
        (when div67 (println (format "    ÷67 = %,d" (/ total 67))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 4: COLUMN ANALYSIS (c-fibers, 13 letters each)
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 4: COLUMN ANALYSIS — 67 columns of 13 letters (love lines)")
    (println "================================================================")
    (println)

    (let [col-data (vec (for [d (range 67)]
                          (let [c (col grid d)
                                letters (grid-letters c)
                                gv (grid-gv-sum c)]
                            {:d d :letters letters :gv gv})))
          div7-count  (count (filter #(zero? (mod (:gv %) 7)) col-data))
          div13-count (count (filter #(zero? (mod (:gv %) 13)) col-data))
          div67-count (count (filter #(zero? (mod (:gv %) 67)) col-data))]

      ;; Print all 67 columns
      (doseq [{:keys [d letters gv]} col-data]
        (let [div7  (zero? (mod gv 7))
              div13 (zero? (mod gv 13))
              div67 (zero? (mod gv 67))
              divs (str (when div7 " ÷7") (when div13 " ÷13") (when div67 " ÷67"))]
          (println (format "  d=%2d  %s  gv=%,5d%s" d letters gv divs))))
      (println)
      (println (format "  Columns divisible by 7:  %d of 67 (%.1f%%, expected %.1f%%)"
                       div7-count (* 100.0 (/ div7-count 67.0)) (/ 100.0 7)))
      (println (format "  Columns divisible by 13: %d of 67 (%.1f%%, expected %.1f%%)"
                       div13-count (* 100.0 (/ div13-count 67.0)) (/ 100.0 13)))
      (println (format "  Columns divisible by 67: %d of 67 (%.1f%%, expected %.1f%%)"
                       div67-count (* 100.0 (/ div67-count 67.0)) (/ 100.0 67))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 5: WORD SEARCH IN COLUMNS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 5: WORD SEARCH — target words in columns (love lines)")
    (println "================================================================")
    (println)

    (let [col-hits (for [d (range 67)
                         :let [c (col grid d)
                               hits (search-all-subsequences c)]
                         :when (seq hits)]
                     {:d d :letters (grid-letters c) :hits hits})]
      (if (seq col-hits)
        (doseq [{:keys [d letters hits]} col-hits]
          (println (format "  d=%2d  %s" d letters))
          (doseq [{:keys [word meaning offset]} hits]
            (println (format "        found: %s (%s) at offset %d" word meaning offset))))
        (println "  No target words found in columns."))
      (println)

      ;; Also search in rows
      (println "── Target words in rows (understanding lines) ──")
      (println)
      (let [row-hits (for [c (range 13)
                           :let [r (row grid c)
                                 hits (search-all-subsequences r)]
                           :when (seq hits)]
                       {:c c :letters (grid-letters r) :hits hits})]
        (doseq [{:keys [c hits]} row-hits]
          (println (format "  c=%2d  found: %s"
                           c (str/join ", " (map #(format "%s(%s)@%d" (:word %) (:meaning %) (:offset %)) hits)))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 6: DIAGONAL ANALYSIS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 6: DIAGONALS")
    (println "  Main diagonals (slope 1,1) and anti-diagonals (slope 1,-1)")
    (println "================================================================")
    (println)

    ;; Main diagonals — only those of length >= 4 (can contain real words)
    (println "── Main diagonals (length >= 4, with target words) ──")
    (println)
    (let [mains (main-diagonals grid)
          hits (for [[idx diag] (map-indexed vector mains)
                     :when (>= (count diag) 4)
                     :let [found (search-all-subsequences diag)]
                     :when (seq found)]
                 {:idx idx :letters (grid-letters diag) :len (count diag) :hits found})]
      (if (seq hits)
        (doseq [{:keys [idx letters len hits]} hits]
          (println (format "  diag %2d (len=%2d): %s" idx len letters))
          (doseq [{:keys [word meaning offset]} hits]
            (println (format "        found: %s (%s) at offset %d" word meaning offset))))
        (println "  No target words found in main diagonals."))
      ;; Also report full-length diagonals (length 13)
      (println)
      (println "── Full-length main diagonals (length 13) ──")
      (let [full (filter #(= 13 (count %)) mains)]
        (println (format "  %d diagonals of length 13" (count full)))
        (doseq [[i diag] (map-indexed vector full)]
          (let [letters (grid-letters diag)
                gv (grid-gv-sum diag)
                div7  (zero? (mod gv 7))
                div13 (zero? (mod gv 13))
                div67 (zero? (mod gv 67))
                divs (str (when div7 " ÷7") (when div13 " ÷13") (when div67 " ÷67"))]
            (println (format "  diag %2d: %s  gv=%,5d%s" i letters gv divs))))))
    (println)

    ;; Anti-diagonals
    (println "── Anti-diagonals (length >= 4, with target words) ──")
    (println)
    (let [antis (anti-diagonals grid)
          hits (for [[idx diag] (map-indexed vector antis)
                     :when (>= (count diag) 4)
                     :let [found (search-all-subsequences diag)]
                     :when (seq found)]
                 {:idx idx :letters (grid-letters diag) :len (count diag) :hits found})]
      (if (seq hits)
        (doseq [{:keys [idx letters len hits]} hits]
          (println (format "  diag %2d (len=%2d): %s" idx len letters))
          (doseq [{:keys [word meaning offset]} hits]
            (println (format "        found: %s (%s) at offset %d" word meaning offset))))
        (println "  No target words found in anti-diagonals."))
      ;; Full-length anti-diagonals
      (println)
      (println "── Full-length anti-diagonals (length 13) ──")
      (let [full (filter #(= 13 (count %)) antis)]
        (println (format "  %d diagonals of length 13" (count full)))
        (doseq [[i diag] (map-indexed vector full)]
          (let [letters (grid-letters diag)
                gv (grid-gv-sum diag)
                div7  (zero? (mod gv 7))
                div13 (zero? (mod gv 13))
                div67 (zero? (mod gv 67))
                divs (str (when div7 " ÷7") (when div13 " ÷13") (when div67 " ÷67"))]
            (println (format "  diag %2d: %s  gv=%,5d%s" i letters gv divs))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 7: GRID-LEVEL STATISTICS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 7: GRID STATISTICS")
    (println "================================================================")
    (println)

    (let [all-cells (apply concat grid)
          total-gv (reduce + (map :gv all-cells))
          letter-freq (frequencies (map :letter all-cells))
          sorted-freq (sort-by val > letter-freq)]
      (println (format "  Total letters: %d (= 13 × 67 = 871)" (count all-cells)))
      (println (format "  Total gematria: %,d" total-gv))
      (let [div7  (zero? (mod total-gv 7))
            div13 (zero? (mod total-gv 13))
            div67 (zero? (mod total-gv 67))]
        (when div7  (println (format "    ÷7  = %,d" (/ total-gv 7))))
        (when div13 (println (format "    ÷13 = %,d" (/ total-gv 13))))
        (when div67 (println (format "    ÷67 = %,d" (/ total-gv 67))))
        (println (format "    mod 7  = %d" (mod total-gv 7)))
        (println (format "    mod 13 = %d" (mod total-gv 13)))
        (println (format "    mod 67 = %d" (mod total-gv 67))))
      (println)

      (println "  Letter frequency in the center grid:")
      (doseq [[letter cnt] sorted-freq]
        (println (format "    %s  %3d  (%.1f%%)" letter cnt (* 100.0 (/ cnt 871.0))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 8: SYMMETRY — does the grid have mirror properties?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 8: SYMMETRY — mirror pairs within the grid")
    (println "================================================================")
    (println)

    ;; c-mirror: (c, d) <-> (12-c, d) — within the grid
    (println "── c-mirror: (c, d) <-> (12-c, d) ──")
    (let [pairs (for [c (range 6)
                      d (range 67)]
                  (let [cell1 (get-in grid [c d])
                        cell2 (get-in grid [(- 12 c) d])]
                    {:c1 c :c2 (- 12 c) :d d
                     :l1 (:letter cell1) :l2 (:letter cell2)
                     :gv-sum (+ (:gv cell1) (:gv cell2))}))
          same-letter (count (filter #(= (:l1 %) (:l2 %)) pairs))
          gv-div7  (count (filter #(zero? (mod (:gv-sum %) 7)) pairs))
          gv-div13 (count (filter #(zero? (mod (:gv-sum %) 13)) pairs))]
      (println (format "  %d mirror pairs (6 × 67, excluding center row c=6)" (count pairs)))
      (println (format "  Same letter: %d (%.1f%%)" same-letter (* 100.0 (/ same-letter (double (count pairs))))))
      (println (format "  Pair GV ÷7:  %d (%.1f%%, expected %.1f%%)"
                       gv-div7 (* 100.0 (/ gv-div7 (double (count pairs)))) (/ 100.0 7)))
      (println (format "  Pair GV ÷13: %d (%.1f%%, expected %.1f%%)"
                       gv-div13 (* 100.0 (/ gv-div13 (double (count pairs)))) (/ 100.0 13))))
    (println)

    ;; d-mirror: (c, d) <-> (c, 66-d) — within the grid
    (println "── d-mirror: (c, d) <-> (c, 66-d) ──")
    (let [pairs (for [c (range 13)
                      d (range 33)]
                  (let [cell1 (get-in grid [c d])
                        cell2 (get-in grid [c (- 66 d)])]
                    {:c c :d1 d :d2 (- 66 d)
                     :l1 (:letter cell1) :l2 (:letter cell2)
                     :gv-sum (+ (:gv cell1) (:gv cell2))}))
          same-letter (count (filter #(= (:l1 %) (:l2 %)) pairs))
          gv-div7  (count (filter #(zero? (mod (:gv-sum %) 7)) pairs))
          gv-div13 (count (filter #(zero? (mod (:gv-sum %) 13)) pairs))]
      (println (format "  %d mirror pairs (13 × 33, excluding center col d=33)" (count pairs)))
      (println (format "  Same letter: %d (%.1f%%)" same-letter (* 100.0 (/ same-letter (double (count pairs))))))
      (println (format "  Pair GV ÷7:  %d (%.1f%%, expected %.1f%%)"
                       gv-div7 (* 100.0 (/ gv-div7 (double (count pairs)))) (/ 100.0 7)))
      (println (format "  Pair GV ÷13: %d (%.1f%%, expected %.1f%%)"
                       gv-div13 (* 100.0 (/ gv-div13 (double (count pairs)))) (/ 100.0 13))))
    (println)

    ;; Full point mirror: (c, d) <-> (12-c, 66-d)
    (println "── Point mirror: (c, d) <-> (12-c, 66-d) ──")
    (let [pairs (for [c (range 13)
                      d (range 67)
                      :when (or (< c 6)
                                (and (= c 6) (< d 33)))]
                  (let [cell1 (get-in grid [c d])
                        cell2 (get-in grid [(- 12 c) (- 66 d)])]
                    {:c1 c :d1 d :c2 (- 12 c) :d2 (- 66 d)
                     :l1 (:letter cell1) :l2 (:letter cell2)
                     :gv-sum (+ (:gv cell1) (:gv cell2))}))
          same-letter (count (filter #(= (:l1 %) (:l2 %)) pairs))
          gv-div7  (count (filter #(zero? (mod (:gv-sum %) 7)) pairs))
          gv-div13 (count (filter #(zero? (mod (:gv-sum %) 13)) pairs))]
      (println (format "  %d antipodal pairs" (count pairs)))
      (println (format "  Same letter: %d (%.1f%%)" same-letter (* 100.0 (/ same-letter (double (count pairs))))))
      (println (format "  Pair GV ÷7:  %d (%.1f%%, expected %.1f%%)"
                       gv-div7 (* 100.0 (/ gv-div7 (double (count pairs)))) (/ 100.0 7)))
      (println (format "  Pair GV ÷13: %d (%.1f%%, expected %.1f%%)"
                       gv-div13 (* 100.0 (/ gv-div13 (double (count pairs)))) (/ 100.0 13))))
    (println)

    (println "Done.")))
