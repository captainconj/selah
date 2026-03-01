(ns experiments.070-fold-aligned-reading
  "Fold-aligned reading — the Thummim hypothesis.

   Instead of reading linearly (skip 1) or at fixed skip (ELS), read by
   alternating between a position and its fold mirror:

     pos₀, mirror(pos₀), pos₁, mirror(pos₁), pos₂, mirror(pos₂), ...

   This is reading *through* the veil — one letter from this side, one
   from the other. If the fold is a mirror, this reading path looks at
   both sides simultaneously.

   Test: for each milemarker position and for each fold (4 single,
   6 double, 4 triple, 1 quad = 15 folds), construct the interleaved
   sequence and search for Hebrew words.

   Also test: from the center position, from the 8 jubilee-bridging את
   positions, and from the clean fiber start (Exodus 18:10).

   FINDINGS:

   NULL RESULT — fold-aligned reading does NOT exceed chance.
   The fold is a symmetry, not a reading path. The Thummim hypothesis
   (fold = reading) is falsified.

   1. NULL MODEL — Mean 3.5 words per 134-letter interleaved reading
      (1000 random positions × 15 folds). Max: 12. Most words found
      are short commons: את, אל, אב, בן, לב, דם, שם (2-letter words
      with high frequency in Hebrew).

   2. MILEMARKER READINGS — Top score: 11 words (Lev יהוה@7, bd-fold,
      step 7). Within the null model tail (12 max in 15,000). Out of
      1,080 milemarker readings, finding one at 11 is expected by chance.

   3. CENTER READINGS — Best: 3 words (God, father, father). BELOW
      the null mean. The center does not read better through the fold.
      However: the original side (non-interleaved) of the best center
      reading contains את, יהוה, משה, אהרן — the center's own vocabulary.
      The fold doesn't ADD meaning; the consecutive text already has it.

   4. CLEAN FIBER — Best: 4 words (את, את, משה, father). Near null mean.

   5. VEIL READING — The d-fold through Exodus 34:29 produces
      אור (light) — the root of Urim. The a-fold produces לב (heart).
      Thematically correct but quantitatively at chance (3-4 words).
      The a-fold and d-fold at the veil's second position both give
      GV = 8,960 = 7 × 1,280. Divisibility by 7 at the fold crease.

   6. CENTER SEVENTH — Lev 19:12 under a-fold interleaving has 9 words
      but 7 are את. The aleph-tav dominates Leviticus (known from 058).
      Not a fold effect — an את density effect.

   INTERPRETATION: The fold CONFIRMS readings (testing truth against
   its mirror), but does not GENERATE them. The reading path must come
   from elsewhere — perhaps the d-axis (consecutive text) or the
   a-fibers (completeness axis) where 068 found Torah, love, truth.
   The folds are verification, not discovery."
  (:require [selah.space.coords :as coords]
            [selah.els.engine :as els]
            [selah.text.oshb :as oshb]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Fold machinery (from 061c) ──────────────────────────────

(def max-coords [6 49 12 66])

(defn fold-position
  "Mirror position i across the given set of axes.
   axes is a set of axis indices (0=a, 1=b, 2=c, 3=d)."
  [^long i axes]
  (let [coord (coords/idx->coord i)
        a (if (axes 0) (- 6  (aget coord 0)) (aget coord 0))
        b (if (axes 1) (- 49 (aget coord 1)) (aget coord 1))
        c (if (axes 2) (- 12 (aget coord 2)) (aget coord 2))
        d (if (axes 3) (- 66 (aget coord 3)) (aget coord 3))]
    (coords/coord->idx a b c d)))

(def axis-labels [:a :b :c :d])

(defn fold-name [axes]
  (apply str (map #(name (axis-labels %)) (sort axes))))

;; All 15 non-empty subsets of {0,1,2,3}
(def all-folds
  (for [n (range 1 16)
        :let [axes (set (for [bit (range 4) :when (bit-test n bit)] bit))]
        :when (seq axes)]
    axes))

;; ── Interleaved reading ──────────────────────────────────────

(defn interleaved-sequence
  "Given a start position, a reading direction (step), and a fold,
   produce an interleaved sequence of 2n letters:
   [pos, mirror(pos), pos+step, mirror(pos+step), ...]
   Returns vector of positions."
  [start step n-steps fold-axes]
  (let [max-pos 304850]
    (loop [i 0
           pos start
           result (transient [])]
      (if (>= i n-steps)
        (persistent! result)
        (let [mirror (fold-position pos fold-axes)]
          (if (and (>= pos 0) (< pos max-pos)
                   (>= mirror 0) (< mirror max-pos))
            (recur (inc i)
                   (+ pos step)
                   (-> result
                       (conj! pos)
                       (conj! mirror)))
            (persistent! result)))))))

(defn positions->letters
  "Convert a sequence of positions to a letter string."
  [s positions]
  (apply str (map #(coords/letter-at s %) positions)))

(defn positions->gv
  "Sum gematria of positions."
  [s positions]
  (reduce + (map #(coords/gv-at s %) positions)))

;; ── Word search ──────────────────────────────────────────────

(def target-words
  {"תורה" "Torah"    "יהוה" "YHWH"    "אהבה" "love"
   "בינה" "understanding" "שמר" "guard" "אמת" "truth"
   "אחד"  "one"      "שבע" "seven"   "אור"  "light"
   "חיים" "life"     "לב"  "heart"   "אל"   "God"
   "את"   "aleph-tav" "קדש" "holy"   "ברית" "covenant"
   "שלם"  "peace"    "כהן" "priest"  "דם"   "blood"
   "שם"   "name"     "בן"  "son"     "אב"   "father"
   "רוח"  "spirit"   "נפש" "soul"    "משה"  "Moses"
   "אהרן" "Aaron"    "ישראל" "Israel"
   "זרע"  "seed"     "ברך" "bless"   "עץ"   "tree"})

(defn search-words [text]
  (for [[word meaning] target-words
        :let [idx (.indexOf ^String text ^String word)]
        :when (>= idx 0)]
    {:word word :meaning meaning :offset idx}))

(defn search-all-words [text]
  (let [results (atom [])]
    (doseq [[word meaning] target-words]
      (loop [start 0]
        (let [idx (.indexOf ^String text ^String word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :meaning meaning :offset idx})
            (recur (inc idx))))))
    @results))

;; ── Milemarker positions ─────────────────────────────────────

(defn book-offset []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        lengths (map #(count (oshb/book-letters %)) books)]
    (zipmap books (reductions + 0 (butlast lengths)))))

(defn find-milemarkers
  "Find classic ELS milemarkers. Returns vector of {:book :word :skip :global-start}"
  [s]
  (let [offsets (book-offset)
        results (atom [])]
    ;; Genesis: תורה at +50
    (let [hits (els/search (vec (oshb/book-letters "Genesis")) "תורה" 50)]
      (doseq [hit hits]
        (swap! results conj {:book "Genesis" :word "תורה" :skip 50
                             :global-start (+ (offsets "Genesis") (:start hit))})))
    ;; Exodus: תורה at +50
    (let [hits (els/search (vec (oshb/book-letters "Exodus")) "תורה" 50)]
      (doseq [hit hits]
        (swap! results conj {:book "Exodus" :word "תורה" :skip 50
                             :global-start (+ (offsets "Exodus") (:start hit))})))
    ;; Leviticus: יהוה at +7
    (let [hits (els/search (vec (oshb/book-letters "Leviticus")) "יהוה" 7)]
      (doseq [hit hits]
        (swap! results conj {:book "Leviticus" :word "יהוה" :skip 7
                             :global-start (+ (offsets "Leviticus") (:start hit))})))
    ;; Numbers: הרות at +50
    (let [hits (els/search (vec (oshb/book-letters "Numbers")) "הרות" 50)]
      (doseq [hit hits]
        (swap! results conj {:book "Numbers" :word "הרות" :skip 50
                             :global-start (+ (offsets "Numbers") (:start hit))})))
    ;; Deuteronomy: הרות at +50
    (let [hits (els/search (vec (oshb/book-letters "Deuteronomy")) "הרות" 50)]
      (doseq [hit hits]
        (swap! results conj {:book "Deuteronomy" :word "הרות" :skip 50
                             :global-start (+ (offsets "Deuteronomy") (:start hit))})))
    @results))

;; ── Key positions ────────────────────────────────────────────

(def center-position
  "The geometric center: (3, 25, 6, 33)"
  (coords/coord->idx 3 25 6 33))

(def clean-fiber-start
  "Exodus 18:10 — only verse filling a complete d-fiber: (2, 20, 1, 0)"
  (coords/coord->idx 2 20 1 0))

(def sword-position
  "Genesis 3:24 — the turning sword, exactly 67 letters"
  ;; Need to find the actual start. The ח of החרב is at d=33.
  ;; The verse starts at d=59, wraps around. Use the midpoint.
  (coords/coord->idx 0 1 0 33)) ;; approximate — will determine from stream

;; ── Analysis ─────────────────────────────────────────────────

(defn analyze-interleaved
  "For a given start position and fold, try reading interleaved.
   Try different step sizes (1 = consecutive, 67 = c-skip, etc.)
   Returns analysis map."
  [s start fold-axes step n-steps]
  (let [positions (interleaved-sequence start step n-steps fold-axes)
        text      (positions->letters s positions)
        words     (search-all-words text)
        gv        (positions->gv s positions)
        ;; Also look at the odd and even subsequences separately
        orig-text   (positions->letters s (take-nth 2 positions))
        mirror-text (positions->letters s (take-nth 2 (rest positions)))
        orig-words  (search-all-words orig-text)
        mirror-words (search-all-words mirror-text)]
    {:start       start
     :fold        (fold-name fold-axes)
     :step        step
     :n-steps     n-steps
     :text        text
     :text-length (count text)
     :gv          gv
     :words       words
     :word-count  (count words)
     :orig-words  orig-words
     :mirror-words mirror-words
     ;; Divisibility
     :div-7       (zero? (mod gv 7))
     :div-13      (zero? (mod gv 13))
     :div-67      (zero? (mod gv 67))}))

(defn score-reading
  "Score a reading by word count + divisibility bonuses."
  [analysis]
  (+ (* 10 (:word-count analysis))
     (if (:div-7 analysis) 5 0)
     (if (:div-13 analysis) 5 0)
     (if (:div-67 analysis) 10 0)))

;; ── Part 1: Milemarker fold-aligned readings ────────────────

(defn part1-milemarker-readings [s]
  (println "\n═══ PART 1: MILEMARKER FOLD-ALIGNED READINGS ═══\n")
  (let [markers (find-milemarkers s)]
    (println (format "  %d milemarkers × 15 folds × 3 step sizes = %d readings\n"
                     (count markers)
                     (* (count markers) 15 3)))

    ;; For each milemarker, try all folds, steps 1 and the marker's own skip
    (let [all-results (atom [])
          steps [1 7 50]]  ;; consecutive, Name-skip, Torah-skip
      (doseq [m markers]
        (doseq [fold all-folds]
          (doseq [step steps]
            (let [analysis (analyze-interleaved s (:global-start m) fold step 67)]
              (when (> (score-reading analysis) 0)
                (swap! all-results conj
                       (assoc analysis
                              :marker-book (:book m)
                              :marker-word (:word m)
                              :marker-skip (:skip m))))))))

      ;; Top results
      (let [top (take 20 (reverse (sort-by score-reading @all-results)))]
        (println "  TOP 20 READINGS BY SCORE:")
        (println (format "  %-12s %-6s %4s  %-8s %4s  %5s  %-6s  Words"
                         "Book" "ELS" "Skip" "Fold" "Step" "GV" "÷"))
        (println (apply str (repeat 80 "─")))
        (doseq [r top]
          (let [divs (cond-> ""
                       (:div-7 r) (str "7 ")
                       (:div-13 r) (str "13 ")
                       (:div-67 r) (str "67"))]
            (println (format "  %-12s %-6s %4d  %-8s %4d  %5d  %-6s  %s"
                             (:marker-book r) (:marker-word r)
                             (:marker-skip r) (:fold r) (:step r)
                             (:gv r) divs
                             (str/join ", " (map :meaning (:words r)))))))
        (println)
        top))))

;; ── Part 2: Center fold-aligned readings ─────────────────────

(defn part2-center-readings [s]
  (println "\n═══ PART 2: CENTER FOLD-ALIGNED READINGS ═══\n")
  (println (format "  Center position: %d = %s" center-position
                   (pr-str (vec (coords/idx->coord center-position)))))
  (println (format "  Center verse: %s\n"
                   (let [v (coords/verse-at s center-position)]
                     (str (:book v) " " (:ch v) ":" (:vs v)))))

  ;; Try all 15 folds, steps 1 and 67 (d-axis step)
  (let [results (atom [])]
    (doseq [fold all-folds
            step [1 67 871 43550]]
      (let [n-steps (case step
                      1 67       ;; 134 interleaved letters
                      67 13      ;; 26 interleaved letters
                      871 7      ;; 14 interleaved letters
                      43550 3)]  ;; 6 interleaved letters (short)
        (let [analysis (analyze-interleaved s center-position fold step n-steps)]
          (swap! results conj analysis))))

    ;; Sort by score
    (let [top (take 15 (reverse (sort-by score-reading @results)))]
      (println "  TOP 15 CENTER READINGS:")
      (println (format "  %-8s %6s %5s  %5s  %-6s  Words"
                       "Fold" "Step" "Len" "GV" "÷"))
      (println (apply str (repeat 70 "─")))
      (doseq [r top]
        (let [divs (cond-> ""
                     (:div-7 r) (str "7 ")
                     (:div-13 r) (str "13 ")
                     (:div-67 r) (str "67"))]
          (println (format "  %-8s %6d %5d  %5d  %-6s  %s"
                           (:fold r) (:step r) (:text-length r)
                           (:gv r) divs
                           (str/join ", " (map :meaning (:words r)))))))
      (println)

      ;; Deep read of best result
      (when-let [best (first top)]
        (println "  ── BEST CENTER READING ──")
        (println (format "  Fold: %s  Step: %d" (:fold best) (:step best)))
        (println (format "  GV: %d (÷7=%s ÷13=%s ÷67=%s)"
                         (:gv best) (:div-7 best) (:div-13 best) (:div-67 best)))
        (println (format "  Interleaved text (%d chars): %s"
                         (count (:text best)) (:text best)))
        (println (format "  Words found: %s"
                         (str/join ", "
                                   (map #(str (:word %) " (" (:meaning %) ")")
                                        (:words best)))))
        (println (format "  Original-side words: %s"
                         (str/join ", " (map :meaning (:orig-words best)))))
        (println (format "  Mirror-side words: %s"
                         (str/join ", " (map :meaning (:mirror-words best))))))
      (println)
      top)))

;; ── Part 3: Clean fiber fold-aligned readings ────────────────

(defn part3-clean-fiber-readings [s]
  (println "\n═══ PART 3: CLEAN FIBER (EXODUS 18:10) FOLD-ALIGNED READINGS ═══\n")
  (println (format "  Clean fiber start: %d = (2, 20, 1, 0)" clean-fiber-start))
  (println (format "  Clean fiber verse: %s\n"
                   (let [v (coords/verse-at s clean-fiber-start)]
                     (str (:book v) " " (:ch v) ":" (:vs v)))))

  ;; The clean fiber is 67 letters at step 1, d varies 0..66
  ;; Try interleaving with each fold
  (let [results (atom [])]
    (doseq [fold all-folds]
      ;; Step 1 along the d-axis (consecutive letters)
      (let [analysis (analyze-interleaved s clean-fiber-start fold 1 67)]
        (swap! results conj analysis)))

    (let [sorted (reverse (sort-by score-reading @results))]
      (println "  ALL 15 FOLDS AT STEP=1:")
      (println (format "  %-8s %5s  %-6s  Words"
                       "Fold" "GV" "÷"))
      (println (apply str (repeat 60 "─")))
      (doseq [r sorted]
        (let [divs (cond-> ""
                     (:div-7 r) (str "7 ")
                     (:div-13 r) (str "13 ")
                     (:div-67 r) (str "67"))]
          (println (format "  %-8s %5d  %-6s  %s"
                           (:fold r) (:gv r) divs
                           (str/join ", " (map :meaning (:words r)))))))
      (println)

      ;; Show top result detail
      (when-let [best (first sorted)]
        (println "  ── BEST CLEAN FIBER READING ──")
        (println (format "  Fold: %s" (:fold best)))
        (println (format "  GV: %d" (:gv best)))
        (println (format "  Interleaved (%d chars): %s"
                         (count (:text best))
                         (subs (:text best) 0 (min 100 (count (:text best))))))
        (when (> (count (:text best)) 100)
          (println "  ..."))
        (println (format "  Words: %s"
                         (str/join ", "
                                   (map #(str (:word %) " (" (:meaning %) ")")
                                        (:words best))))))
      (println)
      sorted)))

;; ── Part 4: Null model — random starts ──────────────────────

(defn part4-null-model [s]
  (println "\n═══ PART 4: NULL MODEL — RANDOM POSITIONS ═══\n")
  (println "  Testing 1000 random positions × 15 folds × step=1 × 67 steps")
  (println "  to establish expected word count per reading.\n")

  (let [rng (java.util.Random. 42)
        n-samples 1000
        all-counts (atom [])]
    (doseq [_ (range n-samples)]
      (let [start (.nextInt rng 304850)]
        (doseq [fold all-folds]
          (let [analysis (analyze-interleaved s start fold 1 67)]
            (swap! all-counts conj (:word-count analysis))))))

    (let [counts @all-counts
          total (count counts)
          mean  (/ (double (reduce + counts)) total)
          max-c (apply max counts)
          ;; Distribution
          dist (frequencies counts)]
      (println (format "  %d total readings." total))
      (println (format "  Mean words per reading: %.3f" mean))
      (println (format "  Max words in any reading: %d" max-c))
      (println (format "  Distribution: %s"
                       (str/join ", "
                                 (for [n (range (inc max-c))
                                       :when (pos? (get dist n 0))]
                                   (format "%d→%d" n (get dist n 0))))))
      (println)
      {:mean mean :max max-c :dist dist})))

;; ── Part 5: Exhaustive scan — all positions on the a=3 hyperplane ──

(defn part5-center-seventh-scan [s]
  (println "\n═══ PART 5: a=3 HYPERPLANE — a-FOLD INTERLEAVED ═══\n")
  (println "  Scanning positions in the center seventh (a=3)")
  (println "  with a-fold interleaving at step=1, 67 steps.")
  (println "  The a-fold is the strongest fold (11× את preservation).\n")

  ;; Sample every 871st position in a=3 (one per jubilee step)
  (let [a3-start (* 3 43550)
        sample-positions (for [b (range 50)]
                           (+ a3-start (* b 871)))
        fold-axes #{0}  ;; a-fold only
        results (atom [])]
    (doseq [pos sample-positions]
      (let [analysis (analyze-interleaved s pos fold-axes 1 67)]
        (when (> (score-reading analysis) 0)
          (let [v (coords/verse-at s pos)]
            (swap! results conj
                   (assoc analysis
                          :verse (str (:book v) " " (:ch v) ":" (:vs v))))))))

    (let [top (take 10 (reverse (sort-by score-reading @results)))]
      (println (format "  %d of 50 positions produced words.\n" (count @results)))
      (println "  TOP 10:")
      (println (format "  %-25s %5s  %-6s  Words" "Verse" "GV" "÷"))
      (println (apply str (repeat 70 "─")))
      (doseq [r top]
        (let [divs (cond-> ""
                     (:div-7 r) (str "7 ")
                     (:div-13 r) (str "13 ")
                     (:div-67 r) (str "67"))]
          (println (format "  %-25s %5d  %-6s  %s"
                           (:verse r) (:gv r) divs
                           (str/join ", " (map :meaning (:words r)))))))
      (println)
      top)))

;; ── Part 6: The veil reading — through the fold crease ──────

(defn part6-veil-reading [s]
  (println "\n═══ PART 6: THE VEIL READING ═══\n")
  (println "  The veil (Exodus 34:29-35) sits at the a-fold crease.")
  (println "  Reading through it: alternate between a=2 and a=4 sides,")
  (println "  following the d-axis through the veil passage.\n")

  ;; The veil midpoint is approximately at the a=2→a=3 boundary
  ;; Let's use the boundary position and read with a-fold interleaving
  (let [boundary (* 3 43550) ;; first position of a=3
        ;; Also try from just before the boundary (last of a=2)
        pre-boundary (dec boundary)
        folds [#{0} #{2} #{3} #{0 2} #{0 3} #{2 3} #{0 2 3}]]

    (doseq [start [pre-boundary boundary]]
      (println (format "  ── Starting at position %d ──" start))
      (let [v (coords/verse-at s start)]
        (println (format "     Verse: %s %s:%s"
                         (:book v) (:ch v) (:vs v))))
      (doseq [fold folds]
        (let [analysis (analyze-interleaved s start fold 1 67)
              words (:words analysis)]
          (when (seq words)
            (let [divs (cond-> ""
                         (:div-7 analysis) (str "÷7 ")
                         (:div-13 analysis) (str "÷13 ")
                         (:div-67 analysis) (str "÷67"))]
              (println (format "     %s-fold: GV=%d %s  words: %s"
                               (:fold analysis)
                               (:gv analysis)
                               divs
                               (str/join ", "
                                         (map #(str (:word %) "(" (:meaning %) ")")
                                              words))))))))
      (println))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "=== 070: FOLD-ALIGNED READING — THE THUMMIM HYPOTHESIS ===")
  (println "  Reading through the veil: alternating position/mirror.")
  (println "  Testing whether the fold IS the reading path.\n")

  (let [s (coords/space)]
    ;; Part 4 first — establish null model
    (let [null (part4-null-model s)]

      ;; Part 1 — milemarkers
      (part1-milemarker-readings s)

      ;; Part 2 — center
      (part2-center-readings s)

      ;; Part 3 — clean fiber
      (part3-clean-fiber-readings s)

      ;; Part 5 — center seventh scan
      (part5-center-seventh-scan s)

      ;; Part 6 — veil reading
      (part6-veil-reading s)

      ;; Final summary
      (println "\n═══ SUMMARY ═══\n")
      (println (format "  Null model: mean %.3f words per reading (max %d)"
                       (:mean null) (:max null)))
      (println "  If fold-aligned readings consistently exceed this,")
      (println "  the fold is not just a symmetry — it's a reading path.")
      (println "  If they match chance, the Thummim is not the fold.\n")

      (println "Done."))))
