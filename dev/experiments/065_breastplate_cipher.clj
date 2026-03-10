(ns experiments.065-breastplate-cipher
  "Experiment 065: The Breastplate Cipher

   The Urim and Thummim breastplate has 72 letters inscribed on 12 stones
   (4 rows x 3 columns x 6 letters per stone). The Talmud says the oracle
   worked by letters illuminating on the stones, and the priest had to
   arrange them correctly -- a transposition cipher.

   We have described the breastplate as a reading device. Here we USE it.

   The 72-letter inscription (per tradition):
     Patriarchs: אברהם יצחק יעקב  (13 letters)
     12 Tribes:  ראובן שמעון לוי יהודה דן נפתלי גד אשר יששכר זבולן יוסף בנימין  (50 letters)
     Closing:    שבטי ישרון  (9 letters)

   72 = 13 + 50 + 9
   The patriarchs give 13 letters (= dim-c = one/love).
   The tribes give 50 letters (= dim-b = jubilee).
   This is not a coincidence we invented -- it IS the tradition.

   FINDINGS:

   1. THE DIMENSION CORRESPONDENCE:
      The breastplate's 72 letters decompose as 13 + 50 + 9.
      13 patriarchal letters = dim-c (one/love axis).
      50 tribal letters = dim-b (jubilee axis).
      9 closing letters ('tribes of Yeshurun') = metadata/label.
      The breastplate physically encodes two of the four dimensions.

   2. BREASTPLATE AS SELECTOR:
      When a patriarchal letter illuminates -> selects a c-hyperplane (1 of 13).
      When a tribal letter illuminates -> selects a b-hyperplane (1 of 50).
      Together they select a (c,b) slab of exactly 7 x 67 = 469 letters
      (completeness x understanding). The remaining two dimensions.

   3. GEMATRIA:
      Total inscription gematria = 4,701.
      Does not divide by 7, 13, 50, 67, 37, or 73.
      BUT: Stone Row 0 (Avraham-Yitzchak-Ya'akov + first tribal stones)
      sums to 897, which divides by 13. The patriarchal row encodes its own axis.

   4. PERIOD-72 AUTOCORRELATION:
      Letter-match rate at period 72 = 0.0674 (ratio 1.064 to random baseline).
      NOT significantly different from neighbors 71 (1.074) and 73 (1.062).
      Period 7 is the strongest match (ratio 1.123) by far.
      72 does not create a periodic signal in the Torah letter stream.
      This confirms: the breastplate is a SELECTOR, not a clock.

   5. PERIOD-72 GEMATRIA THREADS:
      72 threads (every 72nd letter from each starting offset).
      Divisibility counts match random expectation almost exactly:
        div-7: 10 observed vs 10.3 expected
        div-13: 4 vs 5.5, div-37: 2 vs 1.9, div-67: 1 vs 1.1, div-73: 2 vs 1.0
      Offset 45 divides by 67 (understanding). Offset 16 divides by 7 and 73.
      No dramatic signal -- consistent with the breastplate not being a period.

   6. 72 AND THE 4D SPACE:
      72 = 2^3 x 3^2. Torah = 2 x 5^2 x 7 x 13 x 67. gcd = 2.
      72 does NOT divide any stride (43550, 871, 67, 1).
      stride-b mod 72 = 7 (the only interesting residue).
      72 is almost completely coprime to the Torah's factorization.
      The relationship is STRUCTURAL, not arithmetic:
      72 contains 13 and 50 as sub-counts, which are dimensions.

   7. READING PATHS -- WORD YIELDS:
      Row-major (baseline/linear): 41 words found (mostly tribal/patriarchal names)
      Column-major: only 6 words -- reading down the columns destroys names
      Diagonals: 4-7 words
      Spiral: 19 words (preserves top row = names intact)
      Stone columns: 33 words (rearranges but preserves stone integrity)
      Boustrophedon: 22 words
      The linear reading is clearly the intended reading of the inscription.

   8. LETTER FREQUENCIES:
      Yod (י) massively overrepresented: 15.3% on breastplate vs 10.4% in Torah.
      Tav (ת) massively underrepresented: 1.4% vs 5.9% (ratio 0.24).
      The breastplate is NOT a frequency sample of the Torah -- it is a
      structured selector with its own compositional logic."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── The 72 Letters ────────────────────────────────────────

;; The patriarchs (13 letters)
(def patriarch-letters "אברהמיצחקיעקב")

;; The 12 tribes in breastplate order (50 letters)
(def tribe-letters "ראובןשמעוןלוייהודהדןנפתליגדאשריששכרזבולןיוסףבנימין")

;; The closing (9 letters)
(def closing-letters "שבטיישרון")

;; Full 72-letter inscription
(def inscription-72 (str patriarch-letters tribe-letters closing-letters))

;; The 12 stones, 6 letters each (traditional arrangement)
(def stones
  ["אברהמי"    ;; Stone 1:  Avraham + Yod (start of Yitzchak)
   "צחקיעק"    ;; Stone 2:  ...tzchak + Ya'akov start
   "בראובן"    ;; Stone 3:  ...kov + Reuven
   "שמעוןל"    ;; Stone 4:  Shimon + Lamed (Levi start)
   "וייהוד"    ;; Stone 5:  ...vi + Yehuda start
   "הדןנפת"    ;; Stone 6:  ...ah + Dan + Naftali start
   "ליגדאש"    ;; Stone 7:  ...li + Gad + Asher start
   "ריששכר"    ;; Stone 8:  ...er + Yissachar
   "זבולןי"    ;; Stone 9:  Zevulun + Yod (Yosef start)
   "וסףבני"    ;; Stone 10: ...osef + Binyamin start
   "מיןשבט"    ;; Stone 11: ...min + Shivtei start
   "יישרון"    ;; Stone 12: ...Yeshurun
   ])

;; ── Grid Arrangements ────────────────────────────────────

(defn make-4x18-grid
  "Arrange 72 letters into 4 rows of 18 columns (breastplate is 4 rows x 3 cols of stones,
   each stone has 6 letters, so 4 rows x 18 letters per row)."
  [letters]
  ;; The 12 stones are laid out in a 4x3 grid:
  ;;   Stone 1  Stone 2  Stone 3
  ;;   Stone 4  Stone 5  Stone 6
  ;;   Stone 7  Stone 8  Stone 9
  ;;   Stone 10 Stone 11 Stone 12
  ;; Each stone has 6 letters, so each row is 3 stones x 6 = 18 letters.
  ;; But the inscription is LINEAR across stones, so we need to
  ;; distribute by the physical stone layout.
  (let [stone-grid [[0 1 2]
                     [3 4 5]
                     [6 7 8]
                     [9 10 11]]]
    (mapv (fn [row-stones]
            (apply str (map #(nth stones %) row-stones)))
          stone-grid)))

(defn make-12x6-grid
  "12 stones, 6 letters each."
  []
  (mapv vec (map seq stones)))

;; ── Reading Paths ─────────────────────────────────────────

(defn read-row-major
  "Read the 4x18 grid left-to-right, top-to-bottom."
  [grid]
  (apply str grid))

(defn read-column-major
  "Read the 4x18 grid top-to-bottom, left-to-right."
  [grid]
  (let [nrows (count grid)
        ncols (count (first grid))]
    (apply str
           (for [c (range ncols)
                 r (range nrows)]
             (nth (nth grid r) c)))))

(defn read-diagonal-down-right
  "Read diagonals going down-right from the 4x18 grid."
  [grid]
  (let [nrows (count grid)
        ncols (count (first grid))
        ;; Start from each position in first row and first column
        starts (concat (for [c (range ncols)] [0 c])
                       (for [r (range 1 nrows)] [r 0]))]
    (apply str
           (for [[sr sc] starts
                 i (range (min nrows ncols))
                 :let [r (+ sr i) c (+ sc i)]
                 :when (and (< r nrows) (< c ncols))]
             (nth (nth grid r) c)))))

(defn read-diagonal-down-left
  "Read diagonals going down-left (anti-diagonals) from the 4x18 grid."
  [grid]
  (let [nrows (count grid)
        ncols (count (first grid))
        starts (concat (for [c (range ncols)] [0 c])
                       (for [r (range 1 nrows)] [r (dec ncols)]))]
    (apply str
           (for [[sr sc] starts
                 i (range (min nrows ncols))
                 :let [r (+ sr i) c (- sc i)]
                 :when (and (< r nrows) (>= c 0) (< c ncols))]
             (nth (nth grid r) c)))))

(defn read-spiral
  "Read the 4x18 grid in a clockwise spiral from outside-in."
  [grid]
  (let [grid (mapv vec (map seq grid))]
    (loop [top 0 bottom (dec (count grid))
           left 0 right (dec (count (first grid)))
           acc []]
      (if (or (> top bottom) (> left right))
        (apply str acc)
        (let [;; Top row left to right
              top-row (for [c (range left (inc right))] (nth (nth grid top) c))
              ;; Right column top+1 to bottom
              right-col (for [r (range (inc top) (inc bottom))] (nth (nth grid r) right))
              ;; Bottom row right-1 to left (if distinct from top)
              bottom-row (when (> bottom top)
                           (for [c (range (dec right) (dec left) -1)] (nth (nth grid bottom) c)))
              ;; Left column bottom-1 to top+1 (if distinct from right)
              left-col (when (> right left)
                         (for [r (range (dec bottom) top -1)] (nth (nth grid r) left)))]
          (recur (inc top) (dec bottom) (inc left) (dec right)
                 (into acc (concat top-row right-col bottom-row left-col))))))))

(defn read-stone-columns
  "Read the 4x3 stone arrangement by columns (down each stone column).
   Column 0: stones 1,4,7,10. Column 1: stones 2,5,8,11. Column 2: stones 3,6,9,12."
  []
  (let [col-order [[0 3 6 9]    ;; Left column of stones
                    [1 4 7 10]   ;; Middle column
                    [2 5 8 11]]] ;; Right column
    (apply str
           (for [col col-order
                 stone-idx col]
             (nth stones stone-idx)))))

(defn read-boustrophedon
  "Alternating direction: odd rows read right-to-left (as if plowing a field)."
  [grid]
  (apply str
         (map-indexed (fn [i row]
                        (if (even? i) row (str/reverse row)))
                      grid)))

;; ── Hebrew Word Scanner ───────────────────────────────────

(def known-words
  {"אב"    "av (father)"
   "אם"    "em (mother)"
   "בן"    "ben (son)"
   "בת"    "bat (daughter)"
   "אל"    "El (God)"
   "שם"    "shem (name)"
   "יד"    "yad (hand)"
   "לב"    "lev (heart)"
   "חי"    "chai (life)"
   "אח"    "ach (brother)"
   "יום"   "yom (day)"
   "אור"   "or (light)"
   "כל"    "kol (all)"
   "עם"    "am (people)"
   "דם"    "dam (blood)"
   "ארץ"   "eretz (land)"
   "מים"   "mayim (water)"
   "כהן"   "kohen (priest)"
   "קדש"   "qodesh (holy)"
   "אמת"   "emet (truth)"
   "תורה"  "torah"
   "יהוה"  "YHVH"
   "אלהים" "Elohim"
   "אדם"   "adam (man)"
   "אש"    "esh (fire)"
   "מלך"   "melekh (king)"
   "נפש"   "nefesh (soul)"
   "רוח"   "ruach (spirit)"
   "דבר"   "davar (word)"
   "בית"   "bayit (house)"
   "עין"   "ayin (eye)"
   "פה"    "peh (mouth)"
   "אהב"   "ahav (love)"
   "שמר"   "shamar (guard)"
   "זרע"   "zera (seed)"
   "גן"    "gan (garden)"
   "ספר"   "sefer (book)"
   "את"    "et (aleph-tav)"
   "שב"    "shav (return)"
   "נח"    "noach (rest)"
   "בא"    "ba (come)"
   "רב"    "rav (great)"
   "לך"    "lekh (go)"
   "צו"    "tsav (command)"
   "יש"    "yesh (there is)"
   "גד"    "gad (fortune)"
   "דן"    "dan (judge)"
   "אשר"   "asher (blessed/who)"
   "שבט"   "shevet (tribe)"
   "ישר"   "yashar (upright)"
   "שרון"  "sharon (plain)"
   "ברא"   "bara (created)"
   "ראה"   "ra'ah (see)"
   "שמע"   "shama (hear)"
   "עון"   "avon (iniquity)"
   "שני"   "sheni (second)"
   "בני"   "bnei (sons of)"
   "מין"   "min (kind/species)"
   "נפת"   "nofet (honeycomb)"
   "ליג"   "lig (?)"
   "כר"    "kar (pasture)"
   "שכר"   "sakhar (reward)"
   "יוסף"  "Yosef"
   "בנימין" "Binyamin"
   "יהודה" "Yehudah"
   "ראובן" "Reuven"
   "שמעון" "Shimon"
   "לוי"   "Levi"
   "יששכר" "Yissachar"
   "זבולן" "Zevulun"
   "נפתלי" "Naftali"
   "אברהם" "Avraham"
   "יצחק"  "Yitzchak"
   "יעקב"  "Ya'akov"
   "חק"    "choq (statute)"
   "חקי"   "chukei (statutes of)"
   "עקב"   "ekev (heel/because)"
   "שון"   "shon (?)"
   "ישרון" "Yeshurun"
   "שבטי"  "shivtei (tribes of)"
   "מי"    "mi (who)"
   "בר"    "bar (son/pure)"
   "נר"    "ner (lamp)"
   "אחד"   "echad (one)"})

(defn scan-words
  "Find known Hebrew words in a letter sequence."
  [s]
  (let [n (count s)]
    (->> (for [len (range 2 8)
               start (range (- n (dec len)))
               :let [sub (subs s start (+ start len))]
               :when (get known-words sub)]
           {:word sub :start start :end (+ start len)})
         (sort-by (juxt :start (comp - :end)))
         vec)))

;; ── Gematria ──────────────────────────────────────────────

(def gematria
  {\א 1 \ב 2 \ג 3 \ד 4 \ה 5 \ו 6 \ז 7 \ח 8 \ט 9
   \י 10 \כ 20 \ל 30 \מ 40 \נ 50 \ס 60 \ע 70 \פ 80 \צ 90
   \ק 100 \ר 200 \ש 300 \ת 400
   \ך 20 \ם 40 \ן 50 \ף 80 \ץ 90})

(defn gv [s]
  (reduce + (map #(get gematria % 0) s)))

;; ── Autocorrelation Utilities ─────────────────────────────

(defn letter-match-rate
  "Fraction of positions where stream[i] == stream[i+period] for i in 0..n-period-1."
  [^bytes stream ^long n ^long period]
  (let [limit (- n period)]
    (loop [i 0 matches 0]
      (if (>= i limit)
        (/ (double matches) limit)
        (recur (inc i)
               (if (= (aget stream i) (aget stream (+ i period)))
                 (inc matches)
                 matches))))))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 065: THE BREASTPLATE CIPHER")
  (println "  72 letters on 12 stones — using the machine")
  (println "================================================================")
  (println)

  ;; Verify our inscription
  (println (format "Inscription length: %d letters" (count inscription-72)))
  (println (format "  Patriarchs: %d letters  %s" (count patriarch-letters) patriarch-letters))
  (println (format "  Tribes:     %d letters  %s" (count tribe-letters) tribe-letters))
  (println (format "  Closing:    %d letters  %s" (count closing-letters) closing-letters))
  (assert (= 72 (count inscription-72)) "Inscription must be 72 letters")
  (assert (= 13 (count patriarch-letters)) "Patriarchs must be 13 letters")
  (assert (= 50 (count tribe-letters)) "Tribes must be 50 letters")
  (assert (= 9 (count closing-letters)) "Closing must be 9 letters")
  (println)
  (println (format "72 = 13 + 50 + 9"))
  (println (format "13 patriarchal letters = dim-c (one/love)"))
  (println (format "50 tribal letters = dim-b (jubilee)"))
  (println)

  ;; ════════════════════════════════════════════════════════
  ;; PART 1: GRID ARRANGEMENTS AND READING PATHS
  ;; ════════════════════════════════════════════════════════

  (println "================================================================")
  (println "  PART 1: THE STONE GRIDS")
  (println "================================================================")
  (println)

  ;; Show the 4x3 stone layout
  (println "── The 12 Stones (4 rows x 3 columns) ──")
  (println)
  (let [stone-grid [[0 1 2] [3 4 5] [6 7 8] [9 10 11]]]
    (doseq [row stone-grid]
      (println (format "  %s   %s   %s"
                       (nth stones (nth row 0))
                       (nth stones (nth row 1))
                       (nth stones (nth row 2))))))
  (println)

  ;; 4x18 grid
  (let [grid (make-4x18-grid inscription-72)]
    (println "── 4x18 Grid (4 rows of 18 letters) ──")
    (println)
    (doseq [[i row] (map-indexed vector grid)]
      (println (format "  Row %d: %s" i row)))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 2: READING PATHS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 2: READING PATHS")
    (println "================================================================")
    (println)

    (let [readings [["Row-major (linear)"           (read-row-major grid)]
                    ["Column-major (top to bottom)" (read-column-major grid)]
                    ["Diagonal (down-right)"        (read-diagonal-down-right grid)]
                    ["Diagonal (down-left)"         (read-diagonal-down-left grid)]
                    ["Spiral (clockwise outside-in)" (read-spiral grid)]
                    ["Stone columns (down)"          (read-stone-columns)]
                    ["Boustrophedon (serpentine)"    (read-boustrophedon grid)]]]

      (doseq [[label reading] readings]
        (println (format "── %s ──" label))
        (println (format "  %s" reading))
        (println (format "  Length: %d  Gematria: %,d" (count reading) (gv reading)))
        (let [words (scan-words reading)]
          (when (seq words)
            (println (format "  Words found (%d):" (count words)))
            (doseq [{:keys [word start]} words]
              (println (format "    pos %2d: %s" start word)))))
        (println))))

  ;; ════════════════════════════════════════════════════════
  ;; PART 3: GEMATRIA OF THE INSCRIPTION
  ;; ════════════════════════════════════════════════════════

  (println "================================================================")
  (println "  PART 3: GEMATRIA ANALYSIS")
  (println "================================================================")
  (println)

  (let [total (gv inscription-72)]
    (println (format "Total gematria of 72 letters: %,d" total))
    (println)
    (println "── Divisibility ──")
    (doseq [[d label] [[7 "7 (completeness)"]
                        [13 "13 (one/love)"]
                        [50 "50 (jubilee)"]
                        [67 "67 (understanding)"]
                        [37 "37 (prime of creation)"]
                        [73 "73 (wisdom)"]
                        [72 "72 (inscription)"]
                        [12 "12 (tribes)"]
                        [18 "18 (chai)"]
                        [36 "36 (double-chai)"]]]
      (if (zero? (mod total d))
        (println (format "  %,d / %s = %,d" total label (/ total d)))
        (println (format "  %,d mod %s = %d" total label (mod total d)))))
    (println)

    ;; Per-section gematria
    (println "── Per-section gematria ──")
    (println (format "  Patriarchs (13 letters): %,d" (gv patriarch-letters)))
    (println (format "  Tribes (50 letters):     %,d" (gv tribe-letters)))
    (println (format "  Closing (9 letters):     %,d" (gv closing-letters)))
    (println)

    ;; Per-stone gematria
    (println "── Per-stone gematria ──")
    (doseq [[i stone] (map-indexed vector stones)]
      (println (format "  Stone %2d: %s  gv=%,d" (inc i) stone (gv stone))))
    (println)

    ;; Stone row sums
    (println "── Stone row sums ──")
    (let [rows [[0 1 2] [3 4 5] [6 7 8] [9 10 11]]]
      (doseq [[i row-idxs] (map-indexed vector rows)]
        (let [row-gv (reduce + (map #(gv (nth stones %)) row-idxs))]
          (println (format "  Row %d (stones %s): %,d%s"
                           i (str/join "," (map inc row-idxs))
                           row-gv
                           (cond-> ""
                             (zero? (mod row-gv 7))  (str " /7")
                             (zero? (mod row-gv 13)) (str " /13")
                             (zero? (mod row-gv 67)) (str " /67")))))))
    (println)

    ;; Stone column sums
    (println "── Stone column sums ──")
    (let [cols [[0 3 6 9] [1 4 7 10] [2 5 8 11]]]
      (doseq [[i col-idxs] (map-indexed vector cols)]
        (let [col-gv (reduce + (map #(gv (nth stones %)) col-idxs))]
          (println (format "  Col %d (stones %s): %,d%s"
                           i (str/join "," (map inc col-idxs))
                           col-gv
                           (cond-> ""
                             (zero? (mod col-gv 7))  (str " /7")
                             (zero? (mod col-gv 13)) (str " /13")
                             (zero? (mod col-gv 67)) (str " /67"))))))))
  (println)

  ;; ════════════════════════════════════════════════════════
  ;; PART 4: PERIOD-72 AUTOCORRELATION
  ;; ════════════════════════════════════════════════════════

  (println "================================================================")
  (println "  PART 4: PERIOD-72 AUTOCORRELATION")
  (println "  Do positions i and i+72 share the same letter?")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        stream ^bytes (:stream s)
        n (int (:n s))]

    ;; Letter match rate at various periods
    (println "── Letter-match rate (fraction of positions where stream[i] == stream[i+period]) ──")
    (println)
    (let [periods [1 7 12 13 50 67 70 71 72 73 74 100 144 350 871 4235]
          base-rate (let [freq (int-array 27)]
                      (dotimes [i n] (aset freq (aget stream i) (+ (aget freq (aget stream i)) 1)))
                      (reduce + (for [f (seq freq)] (* (/ (double f) n) (/ (double f) n)))))]
      (println (format "  Expected random match rate: %.6f (based on letter frequencies)" base-rate))
      (println)
      (println (format "  %6s  %10s  %8s  %s" "Period" "Match rate" "Ratio" "Note"))
      (println (apply str (repeat 60 "-")))
      (doseq [p periods]
        (when (< p n)
          (let [rate (letter-match-rate stream n p)
                ratio (/ rate base-rate)
                note (cond
                       (= p 7)   "7 (completeness)"
                       (= p 12)  "12 (tribes)"
                       (= p 13)  "13 (one/love)"
                       (= p 50)  "50 (jubilee)"
                       (= p 67)  "67 (understanding)"
                       (= p 71)  "71 (control-)"
                       (= p 72)  "72 (breastplate)"
                       (= p 73)  "73 (control+)"
                       (= p 74)  "74 (control++)"
                       (= p 100) "100"
                       (= p 144) "144 (12x12)"
                       (= p 350) "350 (sapphire)"
                       (= p 871) "871 (13x67)"
                       (= p 4235) "~304850/72"
                       :else     "")]
            (println (format "  %6d  %10.6f  %8.4f  %s" p rate ratio note))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 5: PERIOD-72 GEMATRIA
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 5: PERIOD-72 GEMATRIA THREADS")
    (println "  Sum every 72nd letter starting from each offset 0..71")
    (println "================================================================")
    (println)

    (let [values ^ints (:values s)
          threads (vec
                    (for [offset (range 72)]
                      (loop [i offset sum 0 cnt 0]
                        (if (>= i n)
                          {:offset offset :sum sum :count cnt}
                          (recur (+ i 72)
                                 (+ sum (aget values i))
                                 (inc cnt))))))]

      ;; Show all 72 threads
      (println (format "  %3s  %12s  %6s  %s" "Off" "Sum" "Count" "Divisibility"))
      (println (apply str (repeat 50 "-")))
      (doseq [{:keys [offset sum count]} threads]
        (let [divs (cond-> ""
                     (zero? (mod sum 7))  (str " /7")
                     (zero? (mod sum 13)) (str " /13")
                     (zero? (mod sum 37)) (str " /37")
                     (zero? (mod sum 67)) (str " /67")
                     (zero? (mod sum 73)) (str " /73"))]
          (println (format "  %3d  %,12d  %6d%s" offset sum count divs))))
      (println)

      ;; Statistics
      (let [sums (mapv :sum threads)
            min-sum (apply min sums)
            max-sum (apply max sums)
            mean-sum (/ (reduce + sums) 72.0)
            div7 (count (filter #(zero? (mod (:sum %) 7)) threads))
            div13 (count (filter #(zero? (mod (:sum %) 13)) threads))
            div37 (count (filter #(zero? (mod (:sum %) 37)) threads))
            div67 (count (filter #(zero? (mod (:sum %) 67)) threads))
            div73 (count (filter #(zero? (mod (:sum %) 73)) threads))]
        (println "── Thread statistics ──")
        (println (format "  Min: %,d  Max: %,d  Range: %,d" min-sum max-sum (- max-sum min-sum)))
        (println (format "  Mean: %,.1f" mean-sum))
        (println (format "  Divisible by 7:  %d / 72  (expected ~%.1f)" div7 (/ 72.0 7)))
        (println (format "  Divisible by 13: %d / 72  (expected ~%.1f)" div13 (/ 72.0 13)))
        (println (format "  Divisible by 37: %d / 72  (expected ~%.1f)" div37 (/ 72.0 37)))
        (println (format "  Divisible by 67: %d / 72  (expected ~%.1f)" div67 (/ 72.0 67)))
        (println (format "  Divisible by 73: %d / 72  (expected ~%.1f)" div73 (/ 72.0 73))))
      (println))

    ;; ════════════════════════════════════════════════════════
    ;; PART 6: BREASTPLATE AS COORDINATE MAP
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 6: BREASTPLATE AS COORDINATE MAP")
    (println "  13 patriarchal letters -> c-axis (0..12)")
    (println "  50 tribal letters -> b-axis (0..49)")
    (println "================================================================")
    (println)

    ;; Map each patriarchal letter to a c-value
    (println "── Patriarchs -> c-axis mapping ──")
    (println (format "  %2s  %s  %3s  %s" "c" "Letter" "gv" "From"))
    (doseq [i (range 13)]
      (let [ch (nth patriarch-letters i)
            source (cond
                     (< i 5)  "Avraham"
                     (< i 9)  "Yitzchak"
                     :else    "Ya'akov")]
        (println (format "  %2d  %s      %3d  %s" i ch (get gematria ch 0) source))))
    (println)

    ;; Map each tribal letter to a b-value
    (println "── Tribes -> b-axis mapping (first 20 and last 10) ──")
    (println (format "  %2s  %s  %3s  %s" "b" "Letter" "gv" "From"))
    (let [tribe-name-map
          (let [names ["Reuven" "Shimon" "Levi" "Yehudah" "Dan" "Naftali"
                       "Gad" "Asher" "Yissachar" "Zevulun" "Yosef" "Binyamin"]
                name-letters ["ראובן" "שמעון" "לוי" "יהודה" "דן" "נפתלי"
                               "גד" "אשר" "יששכר" "זבולן" "יוסף" "בנימין"]]
            (loop [i 0 pos 0 mapping (transient [])]
              (if (>= i (count names))
                (persistent! mapping)
                (let [tribe-str (nth name-letters i)
                      tribe-name (nth names i)
                      len (count tribe-str)]
                  (recur (inc i) (+ pos len)
                         (reduce conj! mapping
                                 (for [j (range len)]
                                   {:b (+ pos j)
                                    :letter (nth tribe-str j)
                                    :tribe tribe-name})))))))]
      (doseq [entry (take 20 tribe-name-map)]
        (println (format "  %2d  %s      %3d  %s" (:b entry) (:letter entry)
                         (get gematria (:letter entry) 0) (:tribe entry))))
      (println "  ...")
      (doseq [entry (drop 40 tribe-name-map)]
        (println (format "  %2d  %s      %3d  %s" (:b entry) (:letter entry)
                         (get gematria (:letter entry) 0) (:tribe entry)))))
    (println)

    ;; Show the letter on the breastplate -> what it selects in the 4D space
    (println "── Breastplate letter -> 4D coordinate selection ──")
    (println "  When a patriarchal letter illuminates, it selects a c-hyperplane (13 slices)")
    (println "  When a tribal letter illuminates, it selects a b-hyperplane (50 slices)")
    (println "  Together, they select a (c,b) slab = 7 x 67 = 469 letters")
    (println (format "  469 = 7 x 67 (completeness x understanding)"))
    (println)

    ;; What (c,b) slabs look like
    (println "── Example: c=0 (א from Avraham), b=0 (ר from Reuven) ──")
    (let [positions (coords/slab {:b 0 :c 0})
          n-pos (alength positions)]
      (println (format "  %d positions in the slab" n-pos))
      (let [letters-str (apply str (map #(coords/letter-at s (aget positions %)) (range (min 67 n-pos))))]
        (println (format "  First 67 letters (one d-fiber per a): %s" letters-str))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 7: STRUCTURAL RELATIONSHIPS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 7: 72 AND THE 4D SPACE")
    (println "  72 = 8 x 9 = 2^3 x 3^2")
    (println "  304,850 = 2 x 5^2 x 7 x 13 x 67")
    (println "================================================================")
    (println)

    ;; Check divisibility relationships
    (println "── Does 72 divide any stride? ──")
    (let [sa (coords/stride-a)
          sb (coords/stride-b)
          sc (coords/stride-c)]
      (println (format "  stride-a = %,d  / 72 = %.4f  mod 72 = %d" sa (/ sa 72.0) (mod sa 72)))
      (println (format "  stride-b = %,d  / 72 = %.4f  mod 72 = %d" sb (/ sb 72.0) (mod sb 72)))
      (println (format "  stride-c = %,d  / 72 = %.4f  mod 72 = %d" sc (/ sc 72.0) (mod sc 72)))
      (println (format "  stride-d = 1"))
      (println)

      ;; GCD relationships
      (println "── GCD relationships ──")
      (let [gcd (fn [a b] (if (zero? b) a (recur b (mod a b))))]
        (doseq [[label val] [["304,850" 304850]
                              ["43,550 (stride-a)" sa]
                              ["871 (stride-b)" sb]
                              ["67 (stride-c)" sc]]]
          (println (format "  gcd(%s, 72) = %d" label (gcd val 72)))))
      (println)

      ;; 72 as a factor of total
      (println "── 72 and 304,850 ──")
      (println (format "  304,850 / 72 = %,.4f" (/ 304850.0 72)))
      (println (format "  304,850 mod 72 = %d" (mod 304850 72)))
      (println (format "  72 = 13 + 50 + 9"))
      (println (format "  72 = 8 x 9"))
      (println (format "  72 shares no prime factors with 304,850 (72 = 2^3 x 3^2)"))
      (println (format "  304,850 = 2 x 5^2 x 7 x 13 x 67"))
      (println (format "  gcd(72, 304850) = 2"))
      (println)

      ;; But 72 letters contain 13 + 50 letters that map to axes
      (println "── The deeper relationship ──")
      (println "  72 does NOT divide the total or any stride.")
      (println "  72 is coprime to 7, 13, 67, and 25.")
      (println "  But 72 CONTAINS 13 and 50 as its internal structure:")
      (println "    13 patriarchal letters = the c-axis dimension")
      (println "    50 tribal letters      = the b-axis dimension")
      (println "    9 closing letters      = remainder")
      (println "  The breastplate encodes the DIMENSIONS, not the period.")
      (println "  It is a selector, not a clock."))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 8: THE 9 CLOSING LETTERS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 8: THE CLOSING — שבטי ישרון")
    (println "  9 letters that don't map to b or c")
    (println "================================================================")
    (println)

    (println (format "  שבטי ישרון = 'tribes of Yeshurun'"))
    (println (format "  Gematria: %,d" (gv closing-letters)))
    (println (format "  9 = neither 7 nor 13 nor 50 nor 67"))
    (println (format "  But 72 - 9 = 63 = 7 x 9 = 7 x 3^2"))
    (println (format "  And 72 - 13 - 50 = 9"))
    (println (format "  72 = b-axis + c-axis + 9"))
    (println)
    (println "  The closing phrase is the LABEL — it tells you what the")
    (println "  breastplate is: 'the tribes of the upright one.'")
    (println "  It is metadata, not data. The operative letters are 13 + 50 = 63.")
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 9: LETTER FREQUENCY COMPARISON
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 9: BREASTPLATE vs TORAH LETTER FREQUENCIES")
    (println "================================================================")
    (println)

    (let [;; Torah letter frequencies
          torah-freq (let [freq (long-array 27)]
                       (dotimes [i (int (:n s))]
                         (let [b (aget ^bytes (:stream s) i)]
                           (aset freq b (inc (aget freq b)))))
                       freq)
          ;; Breastplate letter frequencies
          bp-chars (seq inscription-72)
          bp-freq (frequencies bp-chars)
          torah-total (double (:n s))
          bp-total 72.0]
      (println (format "  %s  %6s  %8s  %6s  %8s  %8s"
                       "Letter" "BP cnt" "BP %" "Torah cnt" "Torah %" "Ratio"))
      (println (apply str (repeat 65 "-")))
      (doseq [i (range 22)]  ;; Skip finals for cleaner comparison
        (let [ch (nth coords/letter-chars i)
              bp-cnt (get bp-freq ch 0)
              torah-cnt (aget torah-freq i)
              bp-pct (* 100 (/ bp-cnt bp-total))
              torah-pct (* 100 (/ torah-cnt torah-total))
              ratio (if (zero? torah-pct) 0 (/ bp-pct torah-pct))]
          (when (or (pos? bp-cnt) (pos? torah-cnt))
            (println (format "  %s      %4d    %5.1f%%  %,8d    %5.1f%%    %5.2f"
                             ch bp-cnt bp-pct torah-cnt torah-pct ratio))))))
    (println))

  (println "================================================================")
  (println "  Done.")
  (println "================================================================"))
