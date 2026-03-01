(ns experiments.068-breastplate-readings
  "The first actual use of the machine.

   The breastplate encodes two axes:
   - 13 patriarchal letters (אברהם יצחק יעקב) → c-axis (love/unity)
   - 50 tribal letters → b-axis (jubilee)

   Selecting one patriarchal letter (c) and one tribal letter (b)
   gives a 7 × 67 = 469 letter slab — completeness × understanding.

   For each of the 12 tribes, we select the slab and read it:
   - What text spans each slab?
   - Read the 7 a-fibers (7 letters each, spaced 43,550 apart). Words?
   - Read the 67 d-fibers (consecutive text). What do they say?
   - Gematria of each slab. Divisibility patterns.
   - Which (b, c) selections produce 'stronger' readings?

   FINDINGS:

   1. THE CENTER SLAB READS TORAH — At b=25, c=6 (the Holy of Holies
      coordinates), a-fiber d=64 reads אתתורהכ — seven letters at skip
      43,550 that spell את (aleph-tav) AND תורה (Torah). The center of
      the space contains Torah written along the completeness axis.
      The same slab's a=3 d-fiber IS Lev 8:35 — the center verse.
      Moses (משה) appears in TWO a-fibers (d=3 and d=34).

   2. LOVE APPEARS — Slab b=43, c=11 has gematria 34,237 = 469 × 73.
      469 = 7 × 67 (completeness × understanding). Factor 73 is half
      of the Genesis 1:1 signature (2701 = 37 × 73). Its a-fiber d=45
      reads ואהבהיה — containing אהבה (LOVE). The word that never appears
      in the Torah as a noun shows up in the 4D space on the completeness
      axis. The silent axis speaks through the reading device.

   3. TRUTH AT THE COVENANT — Slab b=22, c=5 divides by 91 = 7 × 13
      (completeness × love). Its a-fiber d=15 reads יםאמתהה — containing
      אמת (truth), the root of Thummim. Its d=0 fiber is Genesis 17:9-10,
      the Abrahamic COVENANT: ברית appears twice, שמר (guard) twice,
      זרע (seed) once. A-fiber d=8 contains רוח (spirit).
      The machine reads covenant, truth, and spirit at ÷(7×13).

   4. DAN DIVIDES BY SEVEN — Of the 12 tribal readings at c=6, only
      Dan (b=18) produces a slab divisible by 7. Gematria = 29,932.
      Dan = judgment (דן = 'he judged'). The judging tribe at the
      completeness number.

   5. THE FULL SCAN — 650 (b,c) pairs:
      - ÷7: 80/650 (12.3%, expected 14.3%)
      - ÷13: 56/650 (8.6%, expected 7.7%)
      - ÷67: 8/650 (1.2%, expected 1.5%)
      - ÷(7×13): 3 pairs — b=22/c=5, b=25/c=10, b=41/c=0
      - ÷(7×67): 2 pairs — b=9/c=11, b=43/c=11 — BOTH at c=11 (Jacob's ק)
      Divisibility rates are near chance globally. The structure is not in
      the statistics — it is in WHICH slabs divide and WHAT they contain.

   6. BENJAMIN SCORES HIGHEST — Among the 12 stones at c=6, Benjamin
      (b=44) scores 54, with 13/67 a-fibers containing target words.
      Benjamin is the youngest son, Rachel's child, the tribe of the
      Temple Mount.

   7. c=6 IS THE DESERT — The love-axis center (c=6) shows the LOWEST
      divisibility: only 3/50 slabs ÷7, only 1/50 ÷13. The center of
      love is sparse in divisibility. Love is not a number — it is a verb.
      (Recall: אהבה at skip 1 appears 15 times as the verb 'to love',
      never as the noun.)"
  (:require [selah.space.coords :as coords]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Breastplate mapping ─────────────────────────────────────
;;
;; The 72-letter inscription flows across 12 stones:
;;   Patriarchs (13 letters): אברהם יצחק יעקב → c=0..12
;;   Tribes     (50 letters): ראובן שמעון ... בנימין → b=0..49
;;   Closing    (9 letters):  שבטי ישרון

(def patriarchal-letters
  "The 13 patriarchal letters in inscription order.
   Each maps to a c-axis value (0..12)."
  [\א \ב \ר \ה \ם   ;; Abraham = c 0-4
   \י \צ \ח \ק       ;; Isaac   = c 5-8
   \י \ע \ק \ב])     ;; Jacob   = c 9-12

(def tribal-names
  "The 12 tribes in birth order, as on the breastplate stones."
  [{:tribe "Reuben"   :hebrew "ראובן"   :letters [\ר \א \ו \ב \ן]}
   {:tribe "Simeon"   :hebrew "שמעון"   :letters [\ש \מ \ע \ו \ן]}
   {:tribe "Levi"     :hebrew "לוי"     :letters [\ל \ו \י]}
   {:tribe "Judah"    :hebrew "יהודה"   :letters [\י \ה \ו \ד \ה]}
   {:tribe "Dan"      :hebrew "דן"      :letters [\ד \ן]}
   {:tribe "Naphtali" :hebrew "נפתלי"   :letters [\נ \פ \ת \ל \י]}
   {:tribe "Gad"      :hebrew "גד"      :letters [\ג \ד]}
   {:tribe "Asher"    :hebrew "אשר"     :letters [\א \ש \ר]}
   {:tribe "Issachar" :hebrew "יששכר"   :letters [\י \ש \ש \כ \ר]}
   {:tribe "Zebulun"  :hebrew "זבולן"   :letters [\ז \ב \ו \ל \ן]}
   {:tribe "Joseph"   :hebrew "יוסף"    :letters [\י \ו \ס \ף]}
   {:tribe "Benjamin" :hebrew "בנימין"  :letters [\ב \נ \י \מ \י \ן]}])

(def tribal-letters
  "The 50 tribal letters in inscription order.
   Each maps to a b-axis value (0..49)."
  (vec (mapcat :letters tribal-names)))

;; Verify
(assert (= 13 (count patriarchal-letters)))
(assert (= 50 (count tribal-letters)))

;; Each tribe's b-range
(defn tribe-b-ranges
  "Returns a vector of {:tribe :b-start :b-end} for each of 12 tribes."
  []
  (loop [tribes tribal-names
         b 0
         result []]
    (if (empty? tribes)
      result
      (let [{:keys [tribe letters]} (first tribes)
            n (count letters)]
        (recur (rest tribes)
               (+ b n)
               (conj result {:tribe tribe
                             :b-start b
                             :b-end (+ b n -1)
                             :b-count n}))))))

;; The 12 stones in the 4×3 grid, mapped to patriarchal rows
(def stone-grid
  "4 rows × 3 columns. Each entry: {:stone :tribe :row :col :patriarch}
   Row 1 → Abraham (c 0-4)
   Row 2 → Isaac (c 5-8)
   Row 3 → Jacob (c 9-12)
   Row 4 → (extending)"
  (let [tribes ["Reuben" "Simeon" "Levi"
                "Judah" "Dan" "Naphtali"
                "Gad" "Asher" "Issachar"
                "Zebulun" "Joseph" "Benjamin"]
        patriarchs ["Abraham" "Isaac" "Jacob" nil]
        ;; Representative c-values for each patriarch row:
        ;; Abraham: c=0..4, center c=2
        ;; Isaac:   c=5..8, center c=6 or 7
        ;; Jacob:   c=9..12, center c=10 or 11
        c-centers [2 6 10 6]] ;; row 4 uses center c=6 (whole space center)
    (vec (for [row (range 4)
               col (range 3)]
           (let [idx (+ (* row 3) col)]
             {:stone (inc idx)
              :tribe (nth tribes idx)
              :row row
              :col col
              :patriarch (nth patriarchs row)
              :c-center (nth c-centers row)})))))

;; ── Hebrew word fragments ───────────────────────────────────
;;
;; Hebrew words to search for in fibers

(def target-words
  {"תורה" "Torah"    "יהוה" "YHWH"    "אהבה" "love"
   "בינה" "understanding" "שמר" "guard" "אמת" "truth"
   "אחד"  "one"      "שבע" "seven"   "אור"  "light"
   "חיים" "life"     "לב"  "heart"   "אל"   "God"
   "את"   "aleph-tav" "קדש" "holy"   "ברית" "covenant"
   "שלם"  "peace"    "כהן" "priest"  "דם"   "blood"
   "שם"   "name"     "בן"  "son"     "אב"   "father"
   "ארץ"  "land"     "שמים" "heaven" "מים"  "water"
   "רוח"  "spirit"   "נפש" "soul"    "עץ"   "tree"
   "זרע"  "seed"     "ברך" "bless"   "משה"  "Moses"
   "אהרן" "Aaron"    "ישראל" "Israel"})

(defn search-words
  "Search for target words in a letter string."
  [s]
  (for [[word meaning] target-words
        :let [idx (.indexOf ^String s ^String word)]
        :when (>= idx 0)]
    {:word word :meaning meaning :offset idx}))

(defn search-all-words
  "Search for ALL occurrences of target words in a letter string."
  [s]
  (let [results (atom [])]
    (doseq [[word meaning] target-words]
      (loop [start 0]
        (let [idx (.indexOf ^String s ^String word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :meaning meaning :offset idx})
            (recur (inc idx))))))
    @results))

;; ── Slab extraction ─────────────────────────────────────────

(defn extract-slab
  "Extract the 7×67 = 469 letter slab at (b, c).
   Returns {:b :c :positions :letters :gematria :a-fibers :d-fibers}"
  [s b c]
  (let [positions (coords/slab {:b b :c c})
        n (alength positions)
        letters (apply str (map #(coords/letter-at s %) positions))
        gv-total (reduce + (map #(coords/gv-at s %) positions))
        ;; a-fibers: 7 letters spaced 43,550 apart (fix b, c, d; vary a)
        a-fibers (vec (for [d (range 67)]
                        (let [fiber-pos (coords/fiber :a {:b b :c c :d d})
                              fiber-str (apply str (map #(coords/letter-at s %) fiber-pos))
                              fiber-gv (reduce + (map #(coords/gv-at s %) fiber-pos))]
                          {:d d :letters fiber-str :gv fiber-gv})))
        ;; d-fibers: 67 consecutive letters (fix a, b, c; vary d)
        d-fibers (vec (for [a (range 7)]
                        (let [fiber-pos (coords/fiber :d {:a a :b b :c c})
                              fiber-str (apply str (map #(coords/letter-at s %) fiber-pos))
                              fiber-gv (reduce + (map #(coords/gv-at s %) fiber-pos))
                              start-v (coords/verse-at s (aget fiber-pos 0))
                              end-v (coords/verse-at s (aget fiber-pos 66))]
                          {:a a :letters fiber-str :gv fiber-gv
                           :start-verse (str (:book start-v) " " (:ch start-v) ":" (:vs start-v))
                           :end-verse (str (:book end-v) " " (:ch end-v) ":" (:vs end-v))})))]
    {:b b :c c
     :letters letters
     :gematria gv-total
     :a-fibers a-fibers
     :d-fibers d-fibers}))

(defn slab-divisibility
  "Check divisibility of slab gematria by axis numbers."
  [gv]
  (let [div7  (zero? (mod gv 7))
        div13 (zero? (mod gv 13))
        div67 (zero? (mod gv 67))
        div50 (zero? (mod gv 50))]
    (cond-> []
      div7  (conj "÷7")
      div13 (conj "÷13")
      div50 (conj "÷50")
      div67 (conj "÷67"))))

(defn score-slab
  "Score a slab for 'strength' of reading.
   Higher = more interesting."
  [slab]
  (let [gv (:gematria slab)
        div-score (+ (if (zero? (mod gv 7)) 3 0)
                     (if (zero? (mod gv 13)) 3 0)
                     (if (zero? (mod gv 67)) 3 0)
                     (if (zero? (mod gv 50)) 1 0))
        ;; Count a-fibers that contain target words
        a-word-count (count (filter #(seq (search-words (:letters %)))
                                    (:a-fibers slab)))
        ;; Count d-fibers that contain target words
        d-word-count (count (filter #(seq (search-words (:letters %)))
                                    (:d-fibers slab)))
        ;; Count a-fibers with gematria divisible by key numbers
        a-div-count (count (filter #(or (zero? (mod (:gv %) 7))
                                        (zero? (mod (:gv %) 13))
                                        (zero? (mod (:gv %) 67)))
                                   (:a-fibers slab)))
        ;; Check if 1,222 appears anywhere
        has-1222 (or (= gv 1222)
                     (some #(= (:gv %) 1222) (:a-fibers slab))
                     (some #(= (:gv %) 1222) (:d-fibers slab)))]
    (+ div-score
       (* 2 a-word-count)
       d-word-count
       a-div-count
       (if has-1222 5 0))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 068: BREASTPLATE READINGS — SELECT AND READ")
  (println "  The first actual use of the machine.")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        ranges (tribe-b-ranges)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 0: THE BREASTPLATE MAP
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 0: THE BREASTPLATE COORDINATE MAP")
    (println "================================================================")
    (println)

    (println "  Patriarchal letters (c-axis, love/unity):")
    (doseq [c (range 13)]
      (let [ch (nth patriarchal-letters c)
            patriarch (cond
                        (<= c 4) "Abraham"
                        (<= c 8) "Isaac"
                        :else    "Jacob")]
        (println (format "    c=%2d  %s  (%s)" c ch patriarch))))
    (println)

    (println "  Tribal letters (b-axis, jubilee):")
    (doseq [{:keys [tribe b-start b-end b-count]} ranges]
      (let [letters (subvec tribal-letters b-start (inc b-end))]
        (println (format "    b=%2d..%2d  %s  %s (%d letters)"
                         b-start b-end (apply str letters) tribe b-count))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: THE 12 STONE READINGS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: THE 12 STONE READINGS")
    (println "  For each tribe: b = first tribal letter, c = 6 (center of love)")
    (println "  Each slab = 7 × 67 = 469 letters")
    (println "================================================================")
    (println)

    (let [c-val 6  ;; center of c-axis
          stone-readings
          (vec (for [{:keys [tribe b-start]} ranges]
                 (let [slab (extract-slab s b-start c-val)
                       score (score-slab slab)]
                   (assoc slab :tribe tribe :score score))))]

      (doseq [{:keys [tribe b c gematria d-fibers a-fibers score]} stone-readings]
        (println (format "  ── Stone: %s (b=%d, c=%d) ──" tribe b c))
        (println (format "     Slab gematria: %,d  %s  score=%d"
                         gematria
                         (str/join " " (slab-divisibility gematria))
                         score))

        ;; d-fibers: what text do they span?
        (println "     d-fibers (67 consecutive letters, text):")
        (doseq [{:keys [a letters gv start-verse end-verse]} d-fibers]
          (let [divs (slab-divisibility gv)
                words (search-words letters)]
            (println (format "       a=%d  %s → %s  gv=%,d %s"
                             a start-verse end-verse gv (str/join " " divs)))
            (when (seq words)
              (println (format "             words: %s"
                               (str/join ", " (map #(format "%s(%s)" (:word %) (:meaning %)) words)))))))

        ;; a-fibers with words
        (let [a-with-words (filter #(seq (search-words (:letters %)))
                                    a-fibers)]
          (when (seq a-with-words)
            (println "     a-fibers with words (7 letters, skip 43,550):")
            (doseq [{:keys [d letters gv]} a-with-words]
              (let [words (search-words letters)]
                (println (format "       d=%2d  %s  gv=%,d  words: %s"
                                 d letters gv
                                 (str/join ", " (map #(format "%s(%s)" (:word %) (:meaning %)) words))))))))
        (println))

      ;; Rank by score
      (println "  ── RANKING BY SCORE ──")
      (println)
      (doseq [{:keys [tribe b gematria score]} (reverse (sort-by :score stone-readings))]
        (println (format "    score=%2d  %s (b=%d)  gv=%,d %s"
                         score tribe b gematria
                         (str/join " " (slab-divisibility gematria))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 2: ALL 13 c-VALUES FOR THE TOP TRIBE
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 2: SWEEP c-AXIS FOR EACH TRIBE")
    (println "  For each tribe's first b, try all 13 c-values.")
    (println "  Looking for the (b, c) pair with strongest signal.")
    (println "================================================================")
    (println)

    (let [all-readings
          (vec (for [{:keys [tribe b-start]} ranges
                     c (range 13)]
                 (let [slab (extract-slab s b-start c)
                       score (score-slab slab)]
                   {:tribe tribe :b b-start :c c
                    :gematria (:gematria slab)
                    :score score
                    :divs (slab-divisibility (:gematria slab))})))
          ;; Top 20 by score
          top (take 20 (reverse (sort-by :score all-readings)))]

      ;; Summary table: tribe × c
      (println "  Slab gematria divisibility matrix (tribe × c):")
      (println (format "  %12s" "")
               (str/join " " (map #(format "c=%2d" %) (range 13))))
      (doseq [{:keys [tribe b-start]} ranges]
        (let [row (filter #(= (:b %) b-start) all-readings)]
          (print (format "  %12s" tribe))
          (doseq [{:keys [divs score]} (sort-by :c row)]
            (print (format " %4s"
                           (cond
                             (some #{"÷7"} divs) "  *7"
                             (some #{"÷13"} divs) " *13"
                             (some #{"÷67"} divs) " *67"
                             :else "   ."))))
          (println)))
      (println)

      ;; Top 20
      (println "  ── TOP 20 (b, c) READINGS BY SCORE ──")
      (println)
      (doseq [{:keys [tribe b c gematria score divs]} top]
        (println (format "    score=%2d  %s b=%2d c=%2d  gv=%,6d  %s"
                         score tribe b c gematria (str/join " " divs)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 3: FULL SCAN — ALL 650 (b, c) PAIRS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 3: FULL 650-PAIR GEMATRIA SCAN")
    (println "  Slab gematria for all 50 × 13 = 650 (b, c) pairs.")
    (println "  Checking divisibility by 7, 13, 67.")
    (println "================================================================")
    (println)

    (let [results (vec (for [b (range 50)
                             c (range 13)]
                         (let [positions (coords/slab {:b b :c c})
                               gv (reduce + (map #(coords/gv-at s %) positions))]
                           {:b b :c c :gv gv})))
          div7  (filter #(zero? (mod (:gv %) 7)) results)
          div13 (filter #(zero? (mod (:gv %) 13)) results)
          div67 (filter #(zero? (mod (:gv %) 67)) results)
          div7-13 (filter #(and (zero? (mod (:gv %) 7))
                                (zero? (mod (:gv %) 13))) results)
          div7-67 (filter #(and (zero? (mod (:gv %) 7))
                                (zero? (mod (:gv %) 67))) results)
          div13-67 (filter #(and (zero? (mod (:gv %) 13))
                                 (zero? (mod (:gv %) 67))) results)
          div-all (filter #(and (zero? (mod (:gv %) 7))
                                (zero? (mod (:gv %) 13))
                                (zero? (mod (:gv %) 67))) results)]

      (println (format "  Total (b, c) pairs: %d" (count results)))
      (println (format "  Divisible by 7:     %d (%.1f%%, expected %.1f%%)"
                       (count div7) (* 100.0 (/ (count div7) 650.0)) (/ 100.0 7)))
      (println (format "  Divisible by 13:    %d (%.1f%%, expected %.1f%%)"
                       (count div13) (* 100.0 (/ (count div13) 650.0)) (/ 100.0 13)))
      (println (format "  Divisible by 67:    %d (%.1f%%, expected %.1f%%)"
                       (count div67) (* 100.0 (/ (count div67) 650.0)) (/ 100.0 67)))
      (println (format "  Divisible by 7×13:  %d (%.1f%%, expected %.1f%%)"
                       (count div7-13) (* 100.0 (/ (count div7-13) 650.0)) (/ 100.0 91)))
      (println (format "  Divisible by 7×67:  %d (%.1f%%, expected %.1f%%)"
                       (count div7-67) (* 100.0 (/ (count div7-67) 650.0)) (/ 100.0 469)))
      (println (format "  Divisible by 13×67: %d (%.1f%%, expected %.1f%%)"
                       (count div13-67) (* 100.0 (/ (count div13-67) 650.0)) (/ 100.0 871)))
      (println (format "  Divisible by ALL:   %d (expected ~%.1f)" (count div-all) (/ 650.0 (* 7 13 67))))
      (println)

      ;; Show the multiply-divisible ones
      (when (seq div7-13)
        (println "  ÷7 AND ÷13 (÷91):")
        (doseq [{:keys [b c gv]} div7-13]
          (let [v (coords/verse-at s (coords/coord->idx 0 b c 0))]
            (println (format "    b=%2d c=%2d  gv=%,6d = 91 × %d  near %s %d:%d"
                             b c gv (/ gv 91) (:book v) (:ch v) (:vs v)))))
        (println))

      (when (seq div7-67)
        (println "  ÷7 AND ÷67 (÷469):")
        (doseq [{:keys [b c gv]} div7-67]
          (let [v (coords/verse-at s (coords/coord->idx 0 b c 0))]
            (println (format "    b=%2d c=%2d  gv=%,6d = 469 × %d  near %s %d:%d"
                             b c gv (/ gv 469) (:book v) (:ch v) (:vs v)))))
        (println))

      (when (seq div13-67)
        (println "  ÷13 AND ÷67 (÷871):")
        (doseq [{:keys [b c gv]} div13-67]
          (let [v (coords/verse-at s (coords/coord->idx 0 b c 0))]
            (println (format "    b=%2d c=%2d  gv=%,6d = 871 × %d  near %s %d:%d"
                             b c gv (/ gv 871) (:book v) (:ch v) (:vs v)))))
        (println))

      (when (seq div-all)
        (println "  ÷7 AND ÷13 AND ÷67 (÷6,097):")
        (doseq [{:keys [b c gv]} div-all]
          (let [v (coords/verse-at s (coords/coord->idx 0 b c 0))]
            (println (format "    b=%2d c=%2d  gv=%,6d  near %s %d:%d"
                             b c gv (:book v) (:ch v) (:vs v)))))
        (println))

      ;; Gematria value = 1,222 (our recurring signature)?
      (let [hits-1222 (filter #(= 1222 (:gv %)) results)]
        (when (seq hits-1222)
          (println "  SLAB GEMATRIA = 1,222 (twice-Torah):")
          (doseq [{:keys [b c]} hits-1222]
            (println (format "    b=%2d c=%2d" b c)))
          (println)))

      ;; Distribution by c-value (does one patriarch's reading favor divisibility?)
      (println "  Divisibility by c-value (patriarch grouping):")
      (doseq [c (range 13)]
        (let [col (filter #(= (:c %) c) results)
              n7 (count (filter #(zero? (mod (:gv %) 7)) col))
              n13 (count (filter #(zero? (mod (:gv %) 13)) col))]
          (println (format "    c=%2d (%s)  ÷7: %2d/50 (%.0f%%)  ÷13: %2d/50 (%.0f%%)"
                           c (cond (<= c 4) "Abr" (<= c 8) "Isc" :else "Jcb")
                           n7 (* 100.0 (/ n7 50.0))
                           n13 (* 100.0 (/ n13 50.0)))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 4: DEEP READ — THE BEST SLABS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 4: DEEP READ — TOP 5 MOST DIVISIBLE SLABS")
    (println "  Full a-fiber and d-fiber analysis for the strongest readings.")
    (println "================================================================")
    (println)

    (let [;; Find slabs divisible by most axis numbers
          all-scored (for [b (range 50) c (range 13)]
                       (let [positions (coords/slab {:b b :c c})
                             gv (reduce + (map #(coords/gv-at s %) positions))
                             ndivs (+ (if (zero? (mod gv 7)) 1 0)
                                      (if (zero? (mod gv 13)) 1 0)
                                      (if (zero? (mod gv 67)) 1 0))]
                         {:b b :c c :gv gv :ndivs ndivs}))
          top5 (take 5 (reverse (sort-by (juxt :ndivs :gv) all-scored)))]

      (doseq [{:keys [b c gv]} top5]
        (let [slab (extract-slab s b c)
              divs (slab-divisibility gv)]
          (println (format "  ══ SLAB b=%d, c=%d  gv=%,d  %s ══"
                           b c gv (str/join " " divs)))

          ;; d-fibers
          (println "  d-fibers (7 readings of 67 consecutive letters):")
          (doseq [{:keys [a letters gv start-verse end-verse]} (:d-fibers slab)]
            (let [words (search-all-words letters)
                  divs (slab-divisibility gv)]
              (println (format "    a=%d  %s → %s" a start-verse end-verse))
              (println (format "         %s" letters))
              (println (format "         gv=%,d %s" gv (str/join " " divs)))
              (when (seq words)
                (println (format "         words: %s"
                                 (str/join ", " (map #(format "%s(%s)@%d" (:word %) (:meaning %) (:offset %)) words)))))))

          ;; a-fibers with words
          (let [a-hits (filter #(seq (search-words (:letters %))) (:a-fibers slab))]
            (when (seq a-hits)
              (println)
              (println "  a-fibers with words (7 letters at skip 43,550):")
              (doseq [{:keys [d letters gv]} a-hits]
                (println (format "    d=%2d  %s  gv=%,d  %s"
                                 d letters gv
                                 (str/join ", " (map #(format "%s(%s)" (:word %) (:meaning %)) (search-words letters))))))))
          (println))))

    ;; ════════════════════════════════════════════════════════
    ;; PART 5: THE CENTER SLAB — b=25, c=6
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 5: THE CENTER SLAB — b=25, c=6 (Holy of Holies)")
    (println "  This is the slab at the exact center of both free axes.")
    (println "================================================================")
    (println)

    (let [slab (extract-slab s 25 6)
          gv (:gematria slab)]
      (println (format "  Gematria: %,d  %s" gv (str/join " " (slab-divisibility gv))))
      (println (format "  mod 7 = %d, mod 13 = %d, mod 67 = %d"
                       (mod gv 7) (mod gv 13) (mod gv 67)))
      (println)

      ;; All 7 d-fibers
      (println "  d-fibers (the 7 readings at the center):")
      (doseq [{:keys [a letters gv start-verse end-verse]} (:d-fibers slab)]
        (let [words (search-all-words letters)
              divs (slab-divisibility gv)]
          (println (format "    a=%d  %s" a start-verse))
          (println (format "         %s" letters))
          (println (format "         gv=%,d %s" gv (str/join " " divs)))
          (when (seq words)
            (println (format "         words: %s"
                             (str/join ", " (map #(format "%s(%s)" (:word %) (:meaning %)) words)))))))
      (println)

      ;; a-fibers with words
      (let [a-hits (filter #(seq (search-words (:letters %))) (:a-fibers slab))]
        (println (format "  a-fibers with target words: %d of 67" (count a-hits)))
        (doseq [{:keys [d letters gv]} a-hits]
          (println (format "    d=%2d  %s  gv=%,d  %s"
                           d letters gv
                           (str/join ", " (map #(format "%s(%s)" (:word %) (:meaning %)) (search-words letters))))))))
    (println)

    (println "Done.")))
