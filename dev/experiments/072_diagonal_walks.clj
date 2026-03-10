(ns experiments.072-diagonal-walks
  "Multi-axis walks — diagonal movement through the 4D space.

   We've walked along single axes (fibers):
     d-axis: skip 1    (consecutive text, always readable)
     c-axis: skip 67   (13 letters — short, chance-level words per 073)
     b-axis: skip 871  (50 letters — chance-level per 073)
     a-axis: skip 43,550 (7 letters — where 068 found Torah, love, truth)

   Diagonals increment multiple coordinates simultaneously:
     (Δa, Δb, Δc, Δd) → skip = Δa×43550 + Δb×871 + Δc×67 + Δd

   Key diagonal slopes:
     (1,0,1,0) → skip 43,617   a+c: completeness+love, 7 steps
     (1,0,0,1) → skip 43,551   a+d: completeness+understanding, 7 steps
     (0,1,0,1) → skip 872      b+d: jubilee+understanding, 50 steps
     (0,0,1,1) → skip 68       c+d: love+understanding, 13 steps
     (1,0,-1,0)→ skip 43,483   a-c: completeness minus love, 7 steps
     (0,1,1,0) → skip 938      b+c: jubilee+love, 13 steps

   Question: do any diagonals produce words at rates above chance?
   And if so, at which starting coordinates?

   FINDINGS:

   NULL RESULT — diagonals do not exceed single-axis word rates.
   The d-axis (consecutive text) remains the only readable direction.

   1. SHORT DIAGONALS (7 steps) — Exhaustive scan of all 7-step
      diagonals. Word rates: 14.1–14.9%, uniformly across all
      slopes. This is LOWER than the a-fiber baseline (~27% from
      073). Diagonals are worse than single-axis walks.

   2. MEDIUM DIAGONALS (13 steps) — c+d, b+c show 25–27% word
      rate. All matches are short common words (את, אל, אב, לב, שם).
      No diagonal produces elevated rates for semantic words.

   3. LONG DIAGONALS (50 steps) — b+d at 56%, b+d⁻ at 67%.
      Expected for 50-letter strings sampling random Torah letters.

   4. SIGNPOST DIAGONALS — 8 jubilee-bridging את. Sparse results:
      1-2 words per diagonal, mostly 2-letter commons. Two textures:
      Gen 46:6 (Jacob to Egypt) → truth on a+d⁻.
      Exod 29:11 (sacrifice) → light on a+d⁻.
      Both within chance for 5-7 letter strings.

   5. THE GRAND DIAGONAL — From (0,0,0,0), the main diagonal of
      the entire space: בויאההו. GV = 35 = 5 × 7. Divides by
      completeness. From (0,0,0,66): GV = 952 = 7 × 136, contains
      לב (heart). Two grand diagonals ÷7.

   INTERPRETATION: Combined with 070 (fold ≠ reading path) and
   073 (transparent non-text axes), this completes the picture:
   the d-axis IS the text, the a-axis carries positioned words
   (068), and everything else — folds, diagonals, other axes —
   samples the text transparently. The structure is in the
   coordinate system itself, not in novel reading paths through it."
  (:require [selah.space.coords :as coords]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Diagonal walk machinery ─────────────────────────────────

(def strides
  "Axis strides: a=43550, b=871, c=67, d=1"
  {:a 43550 :b 871 :c 67 :d 1})

(defn diagonal-skip
  "Compute the linear skip for a diagonal slope vector.
   slope is a map of axis → direction (+1, -1, or 0)."
  [slope]
  (reduce + (for [[axis dir] slope]
              (* dir (get strides axis)))))

(defn diagonal-walk
  "Walk diagonally from a starting position.
   slope: map of axis → direction (+1, -1, 0).
   Returns vector of positions (may be shorter if walk leaves bounds)."
  [start slope]
  (let [skip (diagonal-skip slope)
        ;; Max steps limited by smallest axis in the slope
        max-by-axis (for [[axis dir] slope
                          :when (not (zero? dir))
                          :let [coord-val (let [c (coords/idx->coord start)]
                                           (case axis
                                             :a (aget c 0) :b (aget c 1)
                                             :c (aget c 2) :d (aget c 3)))
                                dim (case axis
                                      :a (coords/dim-a) :b (coords/dim-b)
                                      :c (coords/dim-c) :d (coords/dim-d))]]
                      (if (pos? dir)
                        (- dim coord-val)   ;; steps to reach max
                        (inc coord-val)))   ;; steps to reach 0
        max-steps (apply min max-by-axis)
        n (min max-steps 304850)] ;; safety cap
    (loop [i 0, pos start, result (transient [])]
      (if (or (>= i n) (< pos 0) (>= pos 304850))
        (persistent! result)
        (recur (inc i) (+ pos skip) (conj! result pos))))))

(defn positions->letters
  [s positions]
  (apply str (map #(coords/letter-at s %) positions)))

(defn positions->gv
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
   "זרע"  "seed"     "ברך" "bless"   "עץ"   "tree"
   "מים"  "water"    "שמים" "heaven" "ארץ"  "land"})

(defn search-all-words [text]
  (let [results (atom [])]
    (doseq [[word _] target-words]
      (loop [start 0]
        (let [idx (.indexOf ^String text ^String word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :offset idx})
            (recur (inc idx))))))
    @results))

;; ── Named diagonals ──────────────────────────────────────────

(def named-diagonals
  "Key diagonal slopes to test, with semantic labels."
  [;; Two-axis diagonals (positive direction)
   {:name "a+c"  :label "completeness+love"         :slope {:a 1 :c 1}  :max-len 7}
   {:name "a+d"  :label "completeness+understanding" :slope {:a 1 :d 1}  :max-len 7}
   {:name "a+b"  :label "completeness+jubilee"       :slope {:a 1 :b 1}  :max-len 7}
   {:name "b+d"  :label "jubilee+understanding"      :slope {:b 1 :d 1}  :max-len 50}
   {:name "c+d"  :label "love+understanding"         :slope {:c 1 :d 1}  :max-len 13}
   {:name "b+c"  :label "jubilee+love"               :slope {:b 1 :c 1}  :max-len 13}
   ;; Two-axis diagonals (mixed direction)
   {:name "a+c⁻" :label "completeness−love"          :slope {:a 1 :c -1} :max-len 7}
   {:name "a+d⁻" :label "completeness−understanding" :slope {:a 1 :d -1} :max-len 7}
   {:name "b+d⁻" :label "jubilee−understanding"      :slope {:b 1 :d -1} :max-len 50}
   {:name "c+d⁻" :label "love−understanding"         :slope {:c 1 :d -1} :max-len 13}
   ;; Three-axis diagonals
   {:name "a+c+d"  :label "completeness+love+understanding" :slope {:a 1 :c 1 :d 1}  :max-len 7}
   {:name "a+b+d"  :label "completeness+jubilee+understanding" :slope {:a 1 :b 1 :d 1} :max-len 7}
   {:name "b+c+d"  :label "jubilee+love+understanding" :slope {:b 1 :c 1 :d 1}  :max-len 13}
   ;; Four-axis diagonal (the grand diagonal)
   {:name "a+b+c+d" :label "grand diagonal" :slope {:a 1 :b 1 :c 1 :d 1} :max-len 7}])

;; ── Part 1: Survey all diagonals from the center ────────────

(defn part1-center-diagonals [s]
  (println "\n═══ PART 1: DIAGONALS FROM THE CENTER ═══\n")
  (let [center (coords/coord->idx 3 25 6 33)]
    (println (format "  Center: position %d = (3, 25, 6, 33) = Lev 8:35\n" center))

    (println (format "  %-12s %-35s %5s %4s %5s  Words"
                     "Diagonal" "Label" "Skip" "Len" "GV"))
    (println (apply str (repeat 90 "─")))

    (doseq [diag named-diagonals]
      (let [skip (diagonal-skip (:slope diag))
            positions (diagonal-walk center (:slope diag))
            n (count positions)]
        (when (>= n 2)
          (let [text (positions->letters s positions)
                gv   (positions->gv s positions)
                words (search-all-words text)
                divs (cond-> ""
                       (zero? (mod gv 7)) (str "÷7 ")
                       (zero? (mod gv 13)) (str "÷13 ")
                       (zero? (mod gv 67)) (str "÷67"))]
            (println (format "  %-12s %-35s %5d %4d %5d  %-6s %s"
                             (:name diag) (:label diag)
                             skip n gv divs
                             (if (seq words)
                               (str/join ", " (map :word words))
                               "—")))))))
    (println)))

;; ── Part 2: Exhaustive scan of short diagonals ──────────────

(defn part2-short-diagonal-scan [s]
  (println "\n═══ PART 2: SHORT DIAGONALS (7 STEPS) — EXHAUSTIVE SCAN ═══\n")
  (println "  Testing all 43,550 starting positions for each 7-step diagonal.")
  (println "  Same approach as 073 (transparent axes) but for diagonals.\n")

  ;; For 7-step diagonals, we can start from any of the 43,550 (b,c,d) combinations
  ;; at a=0 (so we have room for 7 steps along a)
  (let [seven-step-diags (filter #(= 7 (:max-len %)) named-diagonals)
        results (atom {})]

    (doseq [diag seven-step-diags]
      (let [slope (:slope diag)
            skip (diagonal-skip slope)
            ;; Start from a=0 (and appropriate values for other axes)
            ;; For positive slopes, start at min values
            ;; For negative slopes, start at max values for those axes
            word-counts (atom 0)
            total-walks (atom 0)
            words-found (atom {})
            best-walk (atom nil)
            best-score (atom 0)]

        ;; Sample: for b=0..49, c=0..12, d=0..66 at a=0
        ;; But only valid starts (where the walk stays in bounds)
        (doseq [b (range (coords/dim-b))
                c (range (coords/dim-c))
                d (range (coords/dim-d))]
          ;; Determine start a based on slope direction
          (let [start-a (if (get slope :a) 0 0) ;; a always starts at 0 for these
                ;; Adjust c,d start for negative slopes
                start-c (if (= -1 (get slope :c 0)) 12 c)
                start-d (if (= -1 (get slope :d 0)) 66 d)
                ;; Check if walk stays in bounds for non-a axes
                c-end (+ start-c (* 6 (get slope :c 0)))
                d-end (+ start-d (* 6 (get slope :d 0)))
                valid? (and (>= c-end 0) (< c-end 13)
                            (>= d-end 0) (< d-end 67))]
            (when valid?
              (let [start (coords/coord->idx start-a b start-c start-d)
                    positions (diagonal-walk start slope)
                    n (count positions)]
                (when (= n 7)
                  (swap! total-walks inc)
                  (let [text (positions->letters s positions)
                        words (search-all-words text)]
                    (when (seq words)
                      (swap! word-counts inc)
                      (doseq [w words]
                        (swap! words-found update (:word w) (fnil inc 0)))
                      (let [score (count words)]
                        (when (> score @best-score)
                          (reset! best-score score)
                          (reset! best-walk
                                  {:positions positions
                                   :text text
                                   :words words
                                   :gv (positions->gv s positions)
                                   :start-coord [start-a b start-c start-d]}))))))))))

        (swap! results assoc (:name diag)
               {:total @total-walks
                :with-words @word-counts
                :rate (if (pos? @total-walks)
                        (/ (double @word-counts) @total-walks)
                        0.0)
                :word-freq @words-found
                :best @best-walk})))

    ;; Print results
    (println (format "  %-12s %7s %7s %7s  Top words" "Diagonal" "Total" "Words" "Rate"))
    (println (apply str (repeat 80 "─")))
    (doseq [diag seven-step-diags
            :let [r (get @results (:name diag))]]
      (let [top-words (->> (:word-freq r)
                           (sort-by val >)
                           (take 5)
                           (map (fn [[w c]] (format "%s(%d)" w c)))
                           (str/join " "))]
        (println (format "  %-12s %7d %7d %7.4f  %s"
                         (:name diag) (:total r) (:with-words r)
                         (:rate r) top-words))))

    ;; Compare to a-fiber baseline from 073
    (println "\n  Baseline (a-fibers from 073): 43,550 walks, ~27% with words\n")

    ;; Show best walks
    (println "  ── BEST WALKS ──")
    (doseq [diag seven-step-diags
            :let [r (get @results (:name diag))
                  best (:best r)]
            :when best]
      (let [v (coords/verse-at s (first (:positions best)))]
        (println (format "  %s: %s → \"%s\" [%s] GV=%d from %s %s:%s"
                         (:name diag)
                         (str/join "," (map :word (:words best)))
                         (:text best)
                         (str/join " " (map #(vec (coords/idx->coord %))
                                            (:positions best)))
                         (:gv best)
                         (:book v) (:ch v) (:vs v)))))
    (println)
    @results))

;; ── Part 3: Medium diagonals (13 steps) ─────────────────────

(defn part3-medium-diagonal-scan [s]
  (println "\n═══ PART 3: MEDIUM DIAGONALS (13 STEPS) — SAMPLED ═══\n")
  (println "  Testing c+d and b+c diagonals (13 steps each).")
  (println "  Sampling 5,000 random starts per diagonal.\n")

  (let [thirteen-step-diags (filter #(= 13 (:max-len %)) named-diagonals)
        rng (java.util.Random. 42)]

    (doseq [diag thirteen-step-diags]
      (let [slope (:slope diag)
            skip (diagonal-skip slope)
            word-counts (atom 0)
            total-walks (atom 0)
            words-found (atom {})
            best-walk (atom nil)
            best-score (atom 0)
            n-samples 5000]

        (dotimes [_ n-samples]
          ;; Generate a valid random start
          (let [a (.nextInt rng (int (coords/dim-a)))
                b (.nextInt rng (int (coords/dim-b)))
                c (.nextInt rng (int (coords/dim-c)))
                d (.nextInt rng (int (coords/dim-d)))
                ;; Adjust for slope direction — start at 0 for positive, max for negative
                start-c (cond
                          (= 1 (get slope :c 0)) 0
                          (= -1 (get slope :c 0)) 12
                          :else c)
                start-d (cond
                          (= 1 (get slope :d 0)) 0
                          (= -1 (get slope :d 0)) 66
                          :else d)
                start-b (cond
                          (= 1 (get slope :b 0)) (min b 37) ;; room for 13 steps
                          (= -1 (get slope :b 0)) (max b 12)
                          :else b)
                start (coords/coord->idx a start-b start-c start-d)
                positions (diagonal-walk start slope)
                n (count positions)]
            (when (>= n 13)
              (swap! total-walks inc)
              (let [text (positions->letters s positions)
                    words (search-all-words text)]
                (when (seq words)
                  (swap! word-counts inc)
                  (doseq [w words]
                    (swap! words-found update (:word w) (fnil inc 0)))
                  (let [score (count words)]
                    (when (> score @best-score)
                      (reset! best-score score)
                      (reset! best-walk
                              {:text text :words words
                               :gv (positions->gv s positions)
                               :start (vec (coords/idx->coord start))}))))))))

        (println (format "  %-12s: %d valid of %d tried, %d with words (%.4f)"
                         (:name diag)
                         @total-walks n-samples @word-counts
                         (if (pos? @total-walks)
                           (/ (double @word-counts) @total-walks)
                           0.0)))
        (when-let [best @best-walk]
          (println (format "    Best: %s from %s, GV=%d"
                           (str/join ", " (map :word (:words best)))
                           (:start best) (:gv best))))
        (when (seq @words-found)
          (let [top (->> @words-found (sort-by val >) (take 5))]
            (println (format "    Top words: %s"
                             (str/join " " (map (fn [[w c]] (format "%s(%d)" w c)) top))))))))
    (println)))

;; ── Part 4: Long diagonals (50 steps) ───────────────────────

(defn part4-long-diagonal-scan [s]
  (println "\n═══ PART 4: LONG DIAGONALS (50 STEPS) — SAMPLED ═══\n")
  (println "  Testing b+d diagonal (50 steps, skip 872).")
  (println "  Sampling 5,000 random starts.\n")

  (let [slope {:b 1 :d 1}
        skip (diagonal-skip slope)
        word-counts (atom 0)
        total-walks (atom 0)
        words-found (atom {})
        best-walk (atom nil)
        best-score (atom 0)
        rng (java.util.Random. 42)
        n-samples 5000]

    (dotimes [_ n-samples]
      (let [a (.nextInt rng (int (coords/dim-a)))
            c (.nextInt rng (int (coords/dim-c)))
            d (.nextInt rng (int (coords/dim-d)))
            start (coords/coord->idx a 0 c d)
            positions (diagonal-walk start slope)
            n (count positions)]
        (when (>= n 10)  ;; at least 10 steps
          (swap! total-walks inc)
          (let [text (positions->letters s positions)
                words (search-all-words text)]
            (when (seq words)
              (swap! word-counts inc)
              (doseq [w words]
                (swap! words-found update (:word w) (fnil inc 0)))
              (let [score (count words)]
                (when (> score @best-score)
                  (reset! best-score score)
                  (reset! best-walk
                          {:text text :words words :n n
                           :gv (positions->gv s positions)
                           :start (vec (coords/idx->coord start))}))))))))

    (println (format "  b+d (skip %d): %d valid, %d with words (%.4f)"
                     skip @total-walks @word-counts
                     (if (pos? @total-walks)
                       (/ (double @word-counts) @total-walks)
                       0.0)))
    (when-let [best @best-walk]
      (println (format "  Best (%d steps): %s from %s, GV=%d"
                       (:n best)
                       (str/join ", " (map :word (:words best)))
                       (:start best) (:gv best))))
    (when (seq @words-found)
      (let [top (->> @words-found (sort-by val >) (take 8))]
        (println (format "  Top words: %s"
                         (str/join " " (map (fn [[w c]] (format "%s(%d)" w c)) top))))))

    ;; Also try b+d⁻ (reverse d)
    (println)
    (let [slope2 {:b 1 :d -1}
          skip2 (diagonal-skip slope2)
          word-counts2 (atom 0)
          total-walks2 (atom 0)
          words-found2 (atom {})]
      (dotimes [_ n-samples]
        (let [a (.nextInt rng (int (coords/dim-a)))
              c (.nextInt rng (int (coords/dim-c)))
              start (coords/coord->idx a 0 c 66)
              positions (diagonal-walk start slope2)
              n (count positions)]
          (when (>= n 10)
            (swap! total-walks2 inc)
            (let [text (positions->letters s positions)
                  words (search-all-words text)]
              (when (seq words)
                (swap! word-counts2 inc)
                (doseq [w words]
                  (swap! words-found2 update (:word w) (fnil inc 0))))))))

      (println (format "  b+d⁻ (skip %d): %d valid, %d with words (%.4f)"
                       skip2 @total-walks2 @word-counts2
                       (if (pos? @total-walks2)
                         (/ (double @word-counts2) @total-walks2)
                         0.0)))
      (when (seq @words-found2)
        (let [top (->> @words-found2 (sort-by val >) (take 8))]
          (println (format "  Top words: %s"
                           (str/join " " (map (fn [[w c]] (format "%s(%d)" w c)) top)))))))
    (println)))

;; ── Part 5: Diagonals from the 8 jubilee-bridging את ────────

(defn part5-signpost-diagonals [s]
  (println "\n═══ PART 5: DIAGONALS FROM THE 8 JUBILEE-BRIDGING את ═══\n")
  (println "  The 8 את that bridge b-boundaries mark narrative hinges.")
  (println "  Walking diagonally from each — do the diagonals read?\n")

  ;; From experiment 059/068: the 8 jubilee-bridging את positions
  ;; They sit at c=12, d=66 (aleph) → c=0, d=0 (tav)
  ;; We need to find them. Each aleph is at the end of a jubilee block.
  ;; Use the known verse references to locate them.
  (let [target-verses [{:ref "Genesis 17:19" :note "Covenant with Isaac"}
                       {:ref "Genesis 37:31" :note "Joseph's coat"}
                       {:ref "Genesis 46:6"  :note "Jacob to Egypt"}
                       {:ref "Exodus 3:11"   :note "Burning bush"}
                       {:ref "Exodus 12:17"  :note "Passover"}
                       {:ref "Exodus 29:11"  :note "Sacrifice"}
                       {:ref "Numbers 32:36" :note "Cities"}
                       {:ref "Deuteronomy 3:12" :note "Possession"}]
        ;; For each signpost, try all diagonals from a nearby position
        short-diags (filter #(<= (:max-len %) 7) named-diagonals)]

    ;; Use the center position as reference for now — the signpost coordinates
    ;; were analyzed in 059 and are at c=12, d=66 (aleph of each את)
    ;; Let's find אתs near b-boundaries by scanning
    (println "  Scanning for את pairs where aleph is at d=66, c=12...")
    (let [at-positions (atom [])]
      (doseq [a (range (coords/dim-a))
              b (range (coords/dim-b))
              :let [;; Position of potential aleph at end of jubilee
                    aleph-pos (coords/coord->idx a b 12 66)
                    ;; Position of potential tav at start of next jubilee
                    tav-pos (coords/coord->idx a (inc b) 0 0)]
              :when (< tav-pos 304850)
              :when (and (= \א (coords/letter-at s aleph-pos))
                         (= \ת (coords/letter-at s tav-pos)))]
        (let [v (coords/verse-at s aleph-pos)]
          (swap! at-positions conj
                 {:aleph-pos aleph-pos :tav-pos tav-pos
                  :coord [a b 12 66]
                  :verse (str (:book v) " " (:ch v) ":" (:vs v))})))

      (println (format "  Found %d jubilee-bridging את.\n" (count @at-positions)))

      ;; For each, try diagonals
      (doseq [at @at-positions]
        (let [interesting (atom [])]
          (doseq [diag short-diags]
            (let [;; Walk from the aleph position
                  positions (diagonal-walk (:aleph-pos at) (:slope diag))
                  n (count positions)]
              (when (>= n 3)
                (let [text (positions->letters s positions)
                      words (search-all-words text)
                      gv (positions->gv s positions)]
                  (when (seq words)
                    (swap! interesting conj
                           {:diag (:name diag) :text text
                            :words (map :word words)
                            :gv gv :n n}))))))

          (when (seq @interesting)
            (println (format "  %s (coord %s):" (:verse at) (:coord at)))
            (doseq [r (sort-by (comp count :words) > @interesting)]
              (println (format "    %-12s: %s [GV=%d, %d steps]"
                               (:diag r)
                               (str/join ", " (:words r))
                               (:gv r) (:n r)))))))
      (println))))

;; ── Part 6: The grand diagonal through (0,0,0,0) ───────────

(defn part6-grand-diagonal [s]
  (println "\n═══ PART 6: THE GRAND DIAGONAL ═══\n")
  (println "  Walking (1,1,1,1) from (0,0,0,0) — the main diagonal of the space.")
  (println "  Skip = 43550 + 871 + 67 + 1 = 44,489\n")

  (let [start 0
        slope {:a 1 :b 1 :c 1 :d 1}
        positions (diagonal-walk start slope)
        n (count positions)
        text (positions->letters s positions)
        gv (positions->gv s positions)]
    (println (format "  %d steps (limited by a-axis = 7)" n))
    (println (format "  Letters: %s" text))
    (println (format "  GV: %d" gv))
    (println (format "  ÷7=%s  ÷13=%s  ÷67=%s"
                     (zero? (mod gv 7)) (zero? (mod gv 13)) (zero? (mod gv 67))))

    ;; What verses does each step land in?
    (println "\n  Step-by-step:")
    (doseq [[i pos] (map-indexed vector positions)]
      (let [coord (vec (coords/idx->coord pos))
            letter (coords/letter-at s pos)
            v (coords/verse-at s pos)]
        (println (format "    %d: pos=%d coord=%s letter=%s → %s %s:%s"
                         i pos coord letter
                         (:book v) (:ch v) (:vs v)))))

    ;; Try from other corners
    (println "\n  Other corner diagonals:")
    (doseq [[label start-coord] [["(0,0,0,66)"  [0 0 0 66]]
                                  ["(0,0,12,0)"  [0 0 12 0]]
                                  ["(0,0,12,66)" [0 0 12 66]]
                                  ["(0,49,0,0)"  [0 49 0 0]]
                                  ["(6,0,0,0)"   [6 0 0 0]]]]
      (let [start-pos (apply coords/coord->idx start-coord)
            ;; Choose slope direction to stay in bounds
            slope-map (zipmap [:a :b :c :d]
                              (map (fn [v mx] (if (> v (/ mx 2)) -1 1))
                                   start-coord [6 49 12 66]))
            positions (diagonal-walk start-pos slope-map)
            n (count positions)
            text (positions->letters s positions)
            gv (positions->gv s positions)
            words (search-all-words text)]
        (println (format "    %s → %s slope=%s GV=%d ÷7=%s %s"
                         label text
                         (pr-str (vals slope-map))
                         gv (zero? (mod gv 7))
                         (if (seq words)
                           (str "→ " (str/join ", " (map :word words)))
                           "")))))
    (println)))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "=== 072: DIAGONAL WALKS — MULTI-AXIS MOVEMENT ===")
  (println "  Walking diagonally through the 4D space.")
  (println "  Skip = Δa×43550 + Δb×871 + Δc×67 + Δd\n")

  (let [s (coords/space)]
    (part1-center-diagonals s)
    (part2-short-diagonal-scan s)
    (part3-medium-diagonal-scan s)
    (part4-long-diagonal-scan s)
    (part5-signpost-diagonals s)
    (part6-grand-diagonal s)

    (println "\n═══ SUMMARY ═══\n")
    (println "  Single-axis fibers: only d-axis (skip 1) is readable.")
    (println "  Non-text axes: transparent (chance-level words, per 073).")
    (println "  Diagonals: do they exceed the single-axis baseline?")
    (println "  If yes → new reading paths through the space.")
    (println "  If no  → the d-axis IS the text, everything else samples it.")
    (println "\nDone.")))
