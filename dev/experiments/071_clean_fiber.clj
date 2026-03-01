(ns experiments.071-clean-fiber
  "Experiment 071: Exodus 18:10 — The Clean Fiber

   Of the 67 verses that are exactly 67 letters long, only ONE fills
   a complete d-fiber without wrapping — that is, it starts at d=0
   and ends at d=66. Every other 67-letter verse wraps around the
   d-axis boundary. That verse is Exodus 18:10:

     ויאמר יתרו ברוך יהוה אשר הציל אתכם מיד מצרים ומיד פרעה
     אשר הציל את העם מתחת יד מצרים

     'And Jethro said, Blessed be the LORD, who has delivered you
      from the hand of Egypt and from the hand of Pharaoh, who has
      delivered the people from under the hand of Egypt.'

   Its coordinates: (2, 20, 1, 0) to (2, 20, 1, 66).
   a=2 (Exodus), b=20 (Naphtali's range), c=1 (second love position).

   Jethro is an outsider — a Midianite priest — who recognizes YHWH.
   His blessing is the only verse that fills a fiber cleanly.

   FINDINGS:

   1. VERIFIED: Exodus 18:10 is exactly 67 letters, position 104,587–104,653.
      Coordinates: start (2, 20, 1, 0), end (2, 20, 1, 66).
      d=0 to d=66, same (a,b,c) throughout. The only clean d-fiber.

   2. WHY THIS VERSE — THE COORDINATES
      - a=2: Exodus — the book of deliverance. Fitting for a deliverance blessing.
      - b=20: the 21st jubilee position (21 = 3 x 7). Falls in Naphtali's range (b=20-23).
      - c=1: the second love position. Bet (ב) — the house, creation, duality.
        The first companion after the origin.

   3. THE a-FIBER THROUGH THE ORIGIN: 91 = 7 x 13
      Seven letters at (*, 20, 1, 0): gematria = 91 = 7 x 13.
      All four dimensions present in a single product.
      The fiber touches: Gen 15:3 (Abraham's heir), Gen 41:20 (Pharaoh's dream),
      Exod 18:10 (Jethro), Lev 6:6, Num 3:32, Num 28:2, Deut 17:15 (the king).

   4. THE FOLDS
      - a-fold mirror (a=4): Numbers 3:32-34 — Merari's Levites, 'guardians of
        the holy things' (שמרת הקדש). Gematria = 6,278 = 73 x 86. Contains
        שמר (guard) and קדש (holy). The clean fiber's mirror is about guarding
        the sacred — same שמר root as the center fiber and the turning sword.
      - c-fold mirror (c=11): Exodus 18:21-22 — Jethro's ADVICE to Moses!
        'You shall provide from all the people able men... rulers of thousands,
        rulers of hundreds, rulers of fifties, rulers of tens... and they shall
        judge the people at all times.' Contains אמת (truth).
        The clean fiber IS the blessing; its c-mirror IS the counsel.
        Same chapter. Same outsider. Blessing and wisdom, mirrored.
      - d-fold (reversed): no significant words (just אל twice).

   5. THE DIFFERENCE BETWEEN CLEAN AND CENTER: 603 = 9 x 67
      Clean fiber gematria: 5,467 = 7 x 781.
      Center fiber gematria: 6,070.
      Difference: 603 = 9 x 67. Nine understandings separate them.
      The dimensional number 67 governs their relationship.

   6. THE SLAB (b=20, c=1): gematria 34,440 = 7 x 4,920
      The entire slab divides by 7.
      Of the 7 fibers, only a=2 (Exodus 18:10) touches exactly ONE verse.
      Every other fiber spans 2-3 verses. The clean fiber is uniquely contained.

   7. ONLY ONE VERSE
      Part 8 shows it plainly: a=2's fiber touches exactly 1 verse.
      a=0 touches 3 (Gen 15:3-5), a=1 touches 2, a=3 touches 3, etc.
      Exodus 18:10 IS the fiber. No other verse in this slab has that property.

   8. JETHRO THE OUTSIDER
      Jethro (יתרו) gematria = 616 = 7 x 88. Divisible by 7.
      He is a Midianite priest who recognizes YHWH — the outsider who sees.
      Three 'hands' (יד) in the verse: Egypt's hand, Pharaoh's hand, Egypt's hand.
      Deliverance from three grips.
      The verse that fills the understanding fiber perfectly is spoken by the
      one who understands from outside the covenant."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Target words ──────────────────────────────────────────

(def target-words
  {"תורה" "Torah"
   "יהוה" "YHWH"
   "אהבה" "love"
   "בינה" "understanding"
   "שמר"  "guard"
   "אמת"  "truth"
   "אור"  "light"
   "קדש"  "holy"
   "ברך"  "bless"
   "ברוך" "blessed"
   "הציל" "delivered"
   "מצרם" "Egypt(defective)"
   "מצרים" "Egypt"
   "פרעה" "Pharaoh"
   "יתרו" "Jethro"
   "משה"  "Moses"
   "את"   "aleph-tav"
   "אל"   "God"
   "יד"   "hand"
   "עם"   "people"
   "דבר"  "word/speak"
   "שם"   "name"
   "חיים" "life"
   "אחד"  "one"
   "שבע"  "seven"
   "כהן"  "priest"
   "חן"   "grace"})

(defn search-words
  "Search for all target words in a letter string.
   Returns seq of {:word :meaning :offset}."
  [s]
  (let [results (atom [])]
    (doseq [[word meaning] target-words]
      (loop [start 0]
        (let [idx (.indexOf ^String s ^String word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :meaning meaning :offset idx})
            (recur (inc idx))))))
    (sort-by :offset @results)))

;; ── Helpers ───────────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn fiber-letters
  "Extract letters from a fiber (int array of positions)."
  [s positions]
  (apply str (map #(coords/letter-at s %) (seq positions))))

(defn fiber-gematria
  "Sum gematria values along a fiber."
  [s positions]
  (reduce + (map #(coords/gv-at s %) (seq positions))))

(defn fiber-report
  "Print a detailed report for a fiber."
  [s positions fiber-name axis-label]
  (let [n (alength ^ints positions)
        letters (fiber-letters s positions)
        gv-sum (fiber-gematria s positions)
        words-found (search-words letters)]
    (println (format "  %s: %d letters" fiber-name n))
    (println (format "  Letters: %s" letters))
    (println (format "  Gematria sum: %,d" gv-sum))
    (let [div7  (zero? (mod gv-sum 7))
          div13 (zero? (mod gv-sum 13))
          div67 (zero? (mod gv-sum 67))
          div50 (zero? (mod gv-sum 50))
          div37 (zero? (mod gv-sum 37))
          div73 (zero? (mod gv-sum 73))]
      (when div7  (println (format "    ÷7  = %,d" (/ gv-sum 7))))
      (when div13 (println (format "    ÷13 = %,d" (/ gv-sum 13))))
      (when div37 (println (format "    ÷37 = %,d" (/ gv-sum 37))))
      (when div50 (println (format "    ÷50 = %,d" (/ gv-sum 50))))
      (when div67 (println (format "    ÷67 = %,d" (/ gv-sum 67))))
      (when div73 (println (format "    ÷73 = %,d" (/ gv-sum 73)))))
    (println)

    ;; Letter-by-letter with coordinates and verses
    (println (format "  Letter-by-letter (%s):" axis-label))
    (doseq [i (range n)]
      (let [pos (aget ^ints positions i)
            coord (vec (coords/idx->coord pos))
            letter (coords/letter-at s pos)
            gv (coords/gv-at s pos)
            v (coords/verse-at s pos)]
        (println (format "    %s=%2d  %s  gv=%3d  pos=%,7d  %s"
                         axis-label i letter gv pos (verse-label v)))))
    (println)

    ;; Words found
    (if (seq words-found)
      (do
        (println "  Words found:")
        (doseq [{:keys [word meaning offset]} words-found]
          (println (format "    '%s' (%s) at offset %d" word meaning offset))))
      (println "  No target words found."))
    (println)))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 071: THE CLEAN FIBER")
  (println "  Exodus 18:10 — Jethro's blessing fills one d-fiber exactly")
  (println "  The only 67-letter verse at d=0..66 without wrapping")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        vrefs (:verse-ref s)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: VERIFY — Find Exodus 18:10 and confirm coordinates
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: VERIFICATION")
    (println "  Find Exodus 18:10, confirm 67 letters, d=0 to d=66")
    (println "================================================================")
    (println)

    (let [ex1810 (first (filter #(and (= "Exodus" (:book %))
                                       (= 18 (:ch %))
                                       (= 10 (:vs %)))
                                vrefs))
          _ (when (nil? ex1810)
              (println "  ERROR: Could not find Exodus 18:10!")
              (System/exit 1))

          start (:start ex1810)
          end   (:end ex1810)
          len   (- end start)
          text  (apply str (map #(coords/letter-at s %) (range start end)))

          start-coord (vec (coords/idx->coord start))
          end-coord   (vec (coords/idx->coord (dec end)))
          [sa sb sc sd] start-coord
          [ea eb ec ed] end-coord]

      (println (format "  Verse: %s" (verse-label ex1810)))
      (println (format "  Position: %,d to %,d" start (dec end)))
      (println (format "  Length: %d letters" len))
      (println (format "  Text: %s" text))
      (println)
      (println (format "  Start coord: [a=%d, b=%d, c=%d, d=%d] = %s" sa sb sc sd start-coord))
      (println (format "  End coord:   [a=%d, b=%d, c=%d, d=%d] = %s" ea eb ec ed end-coord))
      (println)

      (if (= len 67)
        (println "  CONFIRMED: exactly 67 letters")
        (println (format "  WARNING: expected 67 letters, got %d" len)))

      (if (and (= sd 0) (= ed 66)
               (= (subvec start-coord 0 3) (subvec end-coord 0 3)))
        (println "  CONFIRMED: fills d-fiber cleanly — d=0 to d=66, same (a,b,c)")
        (println "  WARNING: does NOT fill a clean d-fiber!"))

      (println)

      ;; Words in the verse
      (let [words (search-words text)]
        (println "  Words found in verse text:")
        (doseq [{:keys [word meaning offset]} words]
          (println (format "    '%s' (%s) at offset %d" word meaning offset))))
      (println)

      ;; Letter-by-letter
      (println "  ── Letter-by-letter with d-coordinate ──")
      (doseq [i (range start end)]
        (let [d (coords/describe i)
              offset (- i start)]
          (println (format "    d=%2d  %s  gv=%3d  pos=%,7d"
                           (nth (:coord d) 3) (:letter d) (:gematria d) i))))
      (println)

      ;; Verse gematria
      (let [verse-gv (reduce + (map #(coords/gv-at s %) (range start end)))]
        (println (format "  Verse gematria: %,d" verse-gv))
        (doseq [[n label] [[7 "7 (completeness)"] [13 "13 (love)"]
                            [37 "37"] [50 "50 (jubilee)"] [67 "67 (understanding)"]
                            [73 "73"] [2701 "2701 (37×73)"]]]
          (when (zero? (mod verse-gv n))
            (println (format "    ÷%s = %,d" label (/ verse-gv n)))))
        (println))

      ;; ════════════════════════════════════════════════════════
      ;; PART 2: WHY THIS VERSE? — What's special about (2, 20, 1)?
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 2: WHY THIS VERSE?")
      (println (format "  Coordinates (a=%d, b=%d, c=%d) — what makes this point special?" sa sb sc))
      (println "================================================================")
      (println)

      (println (format "  a = %d — the third seventh (0-indexed)" sa))
      (println "    a=0: Genesis, a=1: late Genesis/early Exodus")
      (println "    a=2: Exodus — the book of deliverance")
      (println)

      (println (format "  b = %d — the 21st jubilee position (0-indexed)" sb))
      (println (format "    21 = 3 × 7"))
      (println "    If b ranges are mapped to tribes (50 positions / 12 tribes):")
      (let [tribe-ranges [["Reuben"    0  3]   ;; ~4 positions each
                           ["Simeon"    4  7]
                           ["Levi"      8 11]
                           ["Judah"    12 15]
                           ["Dan"      16 19]
                           ["Naphtali" 20 23]
                           ["Gad"      24 27]
                           ["Asher"    28 31]
                           ["Issachar" 32 35]
                           ["Zebulun"  36 39]
                           ["Joseph"   40 43]
                           ["Benjamin" 44 47]]]
        (doseq [[tribe lo hi] tribe-ranges]
          (let [marker (if (and (<= lo sb) (<= sb hi)) " <<<" "")]
            (println (format "      b=%2d-%2d  %s%s" lo hi tribe marker)))))
      (println "    (Remaining b=48,49 are overflow)")
      (println)

      (println (format "  c = %d — the second love position" sc))
      (println "    c=0: aleph (א), the beginning, God, unity")
      (println "    c=1: bet (ב), the house, creation, duality")
      (println "    In the 13 love/unity positions, c=1 is the first companion")
      (println)

      ;; What position in the Torah stream?
      (let [pct (* 100.0 (/ start (double coords/total-letters)))]
        (println (format "  Stream position: %,d of %,d (%.2f%% through Torah)"
                         start coords/total-letters pct)))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 3: THE OTHER FIBERS THROUGH THIS POINT
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 3: FIBERS THROUGH THE CLEAN FIBER'S ORIGIN")
      (println (format "  Through point (a=%d, b=%d, c=%d, d=0)" sa sb sc))
      (println "================================================================")
      (println)

      ;; c-fiber: fix a=2, b=20, d=0, vary c (13 letters)
      (println "── c-fiber: 13 letters of love/unity ──")
      (println (format "   Fix a=%d, b=%d, d=0, vary c=0..12" sa sb))
      (println (format "   Spacing: 67 positions apart"))
      (println)
      (let [c-positions (coords/fiber :c {:a sa :b sb :d 0})]
        (fiber-report s c-positions "c-fiber through origin" "c"))

      ;; a-fiber: fix b=20, c=1, d=0, vary a (7 letters)
      (println "── a-fiber: 7 letters of completeness ──")
      (println (format "   Fix b=%d, c=%d, d=0, vary a=0..6" sb sc))
      (println (format "   Spacing: 43,550 positions apart"))
      (println)
      (let [a-positions (coords/fiber :a {:b sb :c sc :d 0})]
        (fiber-report s a-positions "a-fiber through origin" "a"))

      ;; b-fiber: fix a=2, c=1, d=0, vary b (50 letters)
      (println "── b-fiber: 50 letters of jubilee ──")
      (println (format "   Fix a=%d, c=%d, d=0, vary b=0..49" sa sc))
      (println (format "   Spacing: 871 positions apart"))
      (println)
      (let [b-positions (coords/fiber :b {:a sa :c sc :d 0})]
        (fiber-report s b-positions "b-fiber through origin" "b"))

      ;; ════════════════════════════════════════════════════════
      ;; PART 4: THE FOLDS — Mirror fibers
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 4: THE FOLDS — Mirror fibers")
      (println "  a-fold: a <-> 6-a    (2 -> 4)")
      (println "  c-fold: c <-> 12-c   (1 -> 11)")
      (println "  d-fold: d <-> 66-d   (read backwards)")
      (println "================================================================")
      (println)

      ;; a-fold: mirror at a=4
      (let [mirror-a (- 6 sa)]
        (println (format "── a-fold mirror: (a=%d, b=%d, c=%d, d=*) ──" mirror-a sb sc))
        (println (format "   a=%d maps to a=%d under the a-fold (a <-> 6-a)" sa mirror-a))
        (println)
        (let [mirror-positions (coords/fiber :d {:a mirror-a :b sb :c sc})]
          (fiber-report s mirror-positions
                        (format "a-mirror d-fiber at (%d,%d,%d,*)" mirror-a sb sc)
                        "d")))

      ;; c-fold: mirror at c=11
      (let [mirror-c (- 12 sc)]
        (println (format "── c-fold mirror: (a=%d, b=%d, c=%d, d=*) ──" sa sb mirror-c))
        (println (format "   c=%d maps to c=%d under the c-fold (c <-> 12-c)" sc mirror-c))
        (println)
        (let [mirror-positions (coords/fiber :d {:a sa :b sb :c mirror-c})]
          (fiber-report s mirror-positions
                        (format "c-mirror d-fiber at (%d,%d,%d,*)" sa sb mirror-c)
                        "d")))

      ;; d-fold: read the clean fiber backwards
      (println "── d-fold: clean fiber read backwards (d=66 to d=0) ──")
      (println)
      (let [d-positions (coords/fiber :d {:a sa :b sb :c sc})
            reversed-positions (int-array (reverse (seq d-positions)))
            letters-fwd (fiber-letters s d-positions)
            letters-rev (fiber-letters s reversed-positions)
            words-rev (search-words letters-rev)]
        (println (format "  Forward:  %s" letters-fwd))
        (println (format "  Reversed: %s" letters-rev))
        (println)
        (if (seq words-rev)
          (do
            (println "  Words found in reversed fiber:")
            (doseq [{:keys [word meaning offset]} words-rev]
              (println (format "    '%s' (%s) at offset %d" word meaning offset))))
          (println "  No target words in reversed fiber."))
        (println))

      ;; ════════════════════════════════════════════════════════
      ;; PART 5: THE SLAB AT (b=20, c=1)
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 5: THE SLAB AT (b=20, c=1)")
      (println "  7 a-values × 67 d-values = 469 letters")
      (println "  The clean fiber is one row (a=2) of this slab")
      (println "================================================================")
      (println)

      (let [slab-positions (coords/slab {:b sb :c sc})
            slab-n (alength ^ints slab-positions)
            slab-gv (reduce + (map #(coords/gv-at s (aget ^ints slab-positions %))
                                    (range slab-n)))]
        (println (format "  Slab size: %d positions" slab-n))
        (println (format "  Slab gematria: %,d" slab-gv))

        (doseq [[n label] [[7 "7"] [13 "13"] [37 "37"] [50 "50"]
                            [67 "67"] [73 "73"] [469 "469 (7×67)"]]]
          (when (zero? (mod slab-gv n))
            (println (format "    ÷%s = %,d" label (/ slab-gv n)))))
        (println)

        ;; The 7 d-fibers in this slab (one per a-value)
        (println "  ── The 7 d-fibers of this slab ──")
        (println)
        (doseq [a (range 7)]
          (let [positions (coords/fiber :d {:a a :b sb :c sc})
                letters (fiber-letters s positions)
                gv-sum (fiber-gematria s positions)
                first-pos (aget ^ints positions 0)
                v (coords/verse-at s first-pos)
                clean-marker (if (= a sa) "  *** CLEAN FIBER ***" "")]
            (println (format "    a=%d  gv=%,5d  %s  near %s%s"
                             a gv-sum (subs letters 0 (min 30 (count letters)))
                             (verse-label v) clean-marker))
            ;; Divisibility
            (let [divs (for [[n label] [[7 "÷7"] [13 "÷13"] [67 "÷67"]]
                             :when (zero? (mod gv-sum n))]
                         label)]
              (when (seq divs)
                (println (format "          %s" (str/join " " divs))))))))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 6: JETHRO'S BLESSING — Narrative context
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 6: JETHRO'S BLESSING — The outsider who sees")
      (println "================================================================")
      (println)

      ;; Show surrounding verses (Exodus 18:8-12) for context
      (println "  ── Narrative context: Exodus 18:8-12 ──")
      (let [context-verses (filter #(and (= "Exodus" (:book %))
                                          (= 18 (:ch %))
                                          (<= 8 (:vs %))
                                          (<= (:vs %) 12))
                                   vrefs)]
        (doseq [v (sort-by :vs context-verses)]
          (let [vlen (- (:end v) (:start v))
                text (apply str (map #(coords/letter-at s %) (range (:start v) (:end v))))
                preview (subs text 0 (min 40 (count text)))
                is-1810 (= 10 (:vs v))
                marker (if is-1810 "  *** THE CLEAN FIBER ***" "")]
            (println (format "    %s (%d letters): %s...%s"
                             (verse-label v) vlen preview marker)))))
      (println)

      ;; Jethro's name in gematria
      (let [yitro "יתרו"
            yitro-gv (reduce + (map #(get {\י 10 \ת 400 \ר 200 \ו 6} % 0) yitro))]
        (println (format "  Jethro (יתרו) gematria: %d" yitro-gv))
        (doseq [[n label] [[7 "7"] [13 "13"] [67 "67"]]]
          (when (zero? (mod yitro-gv n))
            (println (format "    ÷%s = %d" label (/ yitro-gv n))))))
      (println)

      ;; ברוך יהוה — the core blessing
      (let [baruch-yhwh "ברוךיהוה"
            baruch-gv (reduce + (map #(coords/gv-at s %)
                                      (range start end)))
            _ nil  ;; already computed above
            baruch-word "ברוך"
            baruch-wv (reduce + (map #(get {\ב 2 \ר 200 \ו 6 \ך 20} % 0) baruch-word))
            yhwh-word "יהוה"
            yhwh-wv (reduce + (map #(get {\י 10 \ה 5 \ו 6} % 0) yhwh-word))]
        (println (format "  ברוך (blessed) gematria: %d" baruch-wv))
        (println (format "  יהוה (YHWH) gematria: %d" yhwh-wv))
        (println (format "  ברוך + יהוה = %d" (+ baruch-wv yhwh-wv)))
        (doseq [[n label] [[7 "7"] [13 "13"] [37 "37"] [67 "67"]]]
          (when (zero? (mod (+ baruch-wv yhwh-wv) n))
            (println (format "    ÷%s = %d" label (/ (+ baruch-wv yhwh-wv) n))))))
      (println)

      ;; How many times does "hand" (יד) appear?
      (let [hand-count (loop [start-idx 0 cnt 0]
                         (let [idx (.indexOf ^String text "יד" (int start-idx))]
                           (if (>= idx 0)
                             (recur (inc idx) (inc cnt))
                             cnt)))]
        (println (format "  'יד' (hand) occurs %d times in this verse" hand-count))
        (println "  'from the hand of Egypt, from the hand of Pharaoh... under the hand of Egypt'")
        (println "  Three hands — deliverance from grip"))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 7: COMPARISON — Clean fiber vs Center fiber
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 7: CLEAN FIBER vs CENTER FIBER")
      (println "  Clean fiber:  (2, 20, 1, *)  — Jethro's blessing (deliverance)")
      (println "  Center fiber: (3, 25, 6, *)  — Lev 8:35 (guard the charge)")
      (println "================================================================")
      (println)

      (let [clean-positions  (coords/fiber :d {:a sa :b sb :c sc})
            center-positions (coords/fiber :d {:a 3 :b 25 :c 6})
            clean-letters  (fiber-letters s clean-positions)
            center-letters (fiber-letters s center-positions)
            clean-gv  (fiber-gematria s clean-positions)
            center-gv (fiber-gematria s center-positions)]
        (println (format "  Clean fiber:  %s" clean-letters))
        (println (format "  Clean gv:     %,d" clean-gv))
        (println)
        (println (format "  Center fiber: %s" center-letters))
        (println (format "  Center gv:    %,d" center-gv))
        (println)
        (let [diff (Math/abs (long (- clean-gv center-gv)))
              sum  (+ clean-gv center-gv)]
          (println (format "  Difference: %,d" diff))
          (println (format "  Sum:        %,d" sum))
          (doseq [[n label] [[7 "7"] [13 "13"] [37 "37"] [67 "67"] [73 "73"]]]
            (when (zero? (mod diff n))
              (println (format "    Difference ÷%s = %,d" label (/ diff n))))
            (when (zero? (mod sum n))
              (println (format "    Sum ÷%s = %,d" label (/ sum n)))))))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 8: THE FULL SLAB — All 7 fibers side by side
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 8: VERSES TOUCHED BY EACH FIBER IN THE SLAB")
      (println "  Slab (b=20, c=1): 7 fibers, one per seventh of Torah")
      (println "================================================================")
      (println)

      (doseq [a (range 7)]
        (let [positions (coords/fiber :d {:a a :b sb :c sc})
              letters (fiber-letters s positions)
              gv-sum (fiber-gematria s positions)
              ;; Collect unique verses this fiber passes through
              verse-ids (distinct (map #(coords/verse-at s (aget ^ints positions %))
                                       (range 67)))
              clean-marker (if (= a sa) " *** CLEAN ***" "")]
          (println (format "  ── a=%d%s ──" a clean-marker))
          (println (format "     Letters: %s" letters))
          (println (format "     Gematria: %,d" gv-sum))
          (println (format "     Verses touched (%d):" (count verse-ids)))
          (doseq [v verse-ids]
            (println (format "       %s" (verse-label v))))
          (println)))

      ;; ════════════════════════════════════════════════════════
      ;; PART 9: SUMMARY
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  SUMMARY")
      (println "================================================================")
      (println)
      (println "  Exodus 18:10 — Jethro's blessing — is the ONLY verse in the Torah")
      (println "  that is exactly 67 letters AND fills a complete d-fiber cleanly")
      (println "  (d=0 to d=66, no wrapping).")
      (println)
      (println (format "  Coordinates: (a=%d, b=%d, c=%d, d=0..66)" sa sb sc))
      (println (format "    a=%d: Exodus — the book of deliverance" sa))
      (println (format "    b=%d: the 21st jubilee (21 = 3×7)" sb))
      (println (format "    c=%d: the second love position (bet, the house)" sc))
      (println)
      (println "  Jethro is a Midianite priest — an outsider.")
      (println "  He recognizes YHWH after hearing of the deliverance from Egypt.")
      (println "  His blessing: 'Blessed be YHWH, who has delivered you")
      (println "  from the hand of Egypt and from the hand of Pharaoh.'")
      (println)
      (println "  The outsider's recognition fills the fiber cleanly.")
      (println "  No wrapping. No spillover. Perfect alignment.")
      (println "  67 = בינה = understanding.")
      (println "  The one who understands fills the understanding fiber.")
      (println)

      (println "Done."))))
