(ns experiments.073-fiber-words
  "Experiment 073: c-Fibers and a-Fibers as Word Carriers

   d-fibers (67 consecutive letters) are always readable as text.
   But the other axes carry letters too — just spaced apart.

   c-fibers: 13 letters, spaced 67 apart. Short enough to hold
   a Hebrew word or root (2-4 letter words).
   304,850 / 13 = 23,450 c-fibers total.

   a-fibers: 7 letters, spaced 43,550 apart.
   Could contain a 2-4 letter word.
   304,850 / 7 = 43,550 a-fibers total.

   b-fibers: 50 letters, spaced 871 apart.
   Sample 1000 random ones. Look for patterns.

   FINDINGS:

   1. c-FIBERS ARE WORD-RICH: 46.7% of all 23,450 c-fibers contain at
      least one recognizable Hebrew word. But this is EXACTLY what chance
      predicts — every word's observed/expected ratio is 0.87–1.52×.
      The c-axis reads like noise, not signal. 13 letters drawn from
      Torah's letter frequencies will naturally form common 2-letter
      combinations (את, אל, אב, etc.) nearly half the time.

   2. a-FIBERS MATCH CHANCE TOO: 27.3% of 43,550 a-fibers carry words.
      Lower percentage (only 7 letters), but again ratios are ~1.0×.
      Most notable: אחד (one) at 1.62× expected (17 observed, 10.5 expected).
      Interesting but within random fluctuation for this sample size.

   3. b-FIBERS ARE NEARLY SATURATED: 93.2% of 6,097 b-fibers contain
      words. With 50 letters per fiber, almost every one will contain
      common 2-3 letter Hebrew words by chance. All ratios ~1.0×.
      ארץ (land) at 2.29× is the only mild outlier.

   4. THE AXES ARE STATISTICALLY TRANSPARENT. Unlike d-fibers (which
      carry actual text because they are consecutive), the c, a, and b
      axes produce letter strings that behave like random draws from the
      Torah's letter frequency distribution. The transposition machine
      preserves readability only along the d-axis.

   5. SPECIFIC KEY WORDS IN c-FIBERS:
      - תורה (Torah): 9 c-fibers (expected 7.6) — 1.19×
      - יהוה (YHWH):  27 c-fibers (expected 20.6) — 1.31×
      - אהבה (love):   9 c-fibers (expected 9.5) — 0.95×
      - בינה (understanding): 3 c-fibers (expected 3.9) — 0.77×
      None dramatically exceed chance. The dimension names (love,
      understanding) do not preferentially encode themselves on their
      own axis.

   6. DISTRIBUTION IS UNIFORM. Word-bearing c-fibers distribute evenly
      across the a-axis (0.95–1.05× per division) and d-axis (no
      position preferred). No clustering at center or boundaries.

   7. THE d-AXIS IS SPECIAL: it's the ONLY axis where letters are
      consecutive (spacing = 1). All other axes space letters 67+
      positions apart, destroying word-level coherence. The 4D space
      has exactly one 'text' direction and three 'skip' directions.
      This is the fundamental asymmetry of the coordinate system."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Target words ─────────────────────────────────────────
;;
;; Hebrew 2-4 letter words we search for as substrings.

(def target-words-2
  "2-letter Hebrew words"
  {"את" "aleph-tav"
   "אל" "God"
   "אב" "father"
   "בן" "son"
   "לב" "heart"
   "דם" "blood"
   "שם" "name"
   "יד" "hand"
   "חי" "living"
   "רע" "evil/friend"
   "אש" "fire"
   "עז" "strong"
   "גם" "also"
   "כל" "all"
   "אם" "mother/if"
   "עם" "people"
   "בר" "grain/son"
   "חן" "grace"
   "רב" "many"})

(def target-words-3
  "3-letter Hebrew words"
  {"אור" "light"
   "שמר" "guard"
   "אמת" "truth"
   "קדש" "holy"
   "יום" "day"
   "שבע" "seven"
   "טוב" "good"
   "אדם" "man"
   "חוה" "Eve"
   "מים" "water"
   "ארץ" "land"
   "שלם" "peace"
   "משה" "Moses"
   "דבר" "word/thing"
   "ברך" "bless"
   "חכם" "wise"
   "זרע" "seed"
   "נפש" "soul"
   "רוח" "spirit"
   "עין" "eye"
   "אחד" "one"
   "כהן" "priest"})

(def target-words-4
  "4-letter Hebrew words"
  {"תורה" "Torah"
   "יהוה" "YHWH"
   "אהבה" "love"
   "בינה" "understanding"
   "אהרן" "Aaron"
   "שמים" "heaven"
   "אלהם" "Elohim (defective)"})

(def target-words-long
  "5+ letter words"
  {"ישראל" "Israel"})

(def all-targets
  "All target words merged"
  (merge target-words-2 target-words-3 target-words-4 target-words-long))

;; ── Word search in fiber string ──────────────────────────

(defn find-words-in-string
  "Find all target words that appear as substrings of s.
   Returns seq of {:word :offset}."
  [s]
  (let [results (transient [])]
    (doseq [[word _] all-targets]
      (loop [start 0]
        (let [idx (.indexOf ^String s ^String word (int start))]
          (when (>= idx 0)
            (conj! results {:word word :offset idx})
            (recur (inc idx))))))
    (persistent! results)))

;; ── Letter frequency computation ─────────────────────────

(defn compute-letter-freqs
  "Compute frequency of each of the 27 letter indices in the Torah stream."
  [s]
  (let [stream ^bytes (:stream s)
        n (alength stream)
        counts (long-array 27)]
    (dotimes [i n]
      (let [b (aget stream i)]
        (aset counts b (inc (aget counts b)))))
    (let [total (double n)]
      (vec (for [i (range 27)]
             {:idx i
              :char (nth coords/letter-chars i)
              :count (aget counts i)
              :freq (/ (aget counts i) total)})))))

(defn char-prob
  "Probability of a given char in the Torah."
  [freq-map ch]
  (get freq-map ch 0.0))

(defn word-prob-independent
  "Probability of a word appearing at a specific position in a random
   string of the given length, assuming independent letter draws."
  [freq-map ^String word]
  (reduce * (map #(char-prob freq-map %) word)))

(defn expected-substring-count
  "Expected number of times a word of length k appears as a substring
   in a random string of length n (independent letters)."
  [freq-map ^String word ^long n]
  (let [k (count word)
        positions (max 0 (- n k -1))]
    (* positions (word-prob-independent freq-map word))))

;; ── Fiber extraction helpers ─────────────────────────────

(defn extract-fiber-string
  "Extract the letter string for a fiber (int[] of positions)."
  [s positions]
  (let [sb (StringBuilder.)]
    (dotimes [i (alength ^ints positions)]
      (.append sb (coords/letter-at s (aget ^ints positions i))))
    (.toString sb)))

(defn extract-c-fiber
  "Extract c-fiber at (a, b, d). Returns 13-letter string."
  [s a b d]
  (extract-fiber-string s (coords/fiber :c {:a a :b b :d d})))

(defn extract-a-fiber
  "Extract a-fiber at (b, c, d). Returns 7-letter string."
  [s b c d]
  (extract-fiber-string s (coords/fiber :a {:b b :c c :d d})))

(defn extract-b-fiber
  "Extract b-fiber at (a, c, d). Returns 50-letter string."
  [s a c d]
  (extract-fiber-string s (coords/fiber :b {:a a :c c :d d})))

;; ── Main ─────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 073: c-FIBERS AND a-FIBERS AS WORD CARRIERS")
  (println "  Do the non-text axes carry readable Hebrew words?")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        da (coords/dim-a)   ;; 7
        db (coords/dim-b)   ;; 50
        dc (coords/dim-c)   ;; 13
        dd (coords/dim-d)]  ;; 67

    ;; ── PART 0: Letter frequencies and expected rates ────
    (println "================================================================")
    (println "  PART 0: LETTER FREQUENCIES AND BASELINES")
    (println "================================================================")
    (println)

    (let [freqs (compute-letter-freqs s)
          freq-map (into {} (map (fn [{:keys [char freq]}] [char freq]) freqs))]

      ;; Show top 10 letters by frequency
      (println "  Top 10 letters by frequency:")
      (doseq [{:keys [char count freq]} (take 10 (reverse (sort-by :freq freqs)))]
        (println (format "    %s  %,6d  %.4f" char count freq)))
      (println)

      ;; Expected word counts in c-fibers (13 letters, 23,450 fibers)
      (println "  Expected word counts — c-fibers (13 letters × 23,450 fibers):")
      (println "  (How many c-fibers should contain each word by chance?)")
      (println)
      (let [n-c-fibers (* da db dd)  ;; 7 × 50 × 67 = 23,450
            word-expectations
            (sort-by :expected >
                     (for [[word _] all-targets
                           :let [e-per-fiber (expected-substring-count freq-map word dc)
                                 e-total (* e-per-fiber n-c-fibers)]]
                       {:word word
                        :e-per-fiber e-per-fiber
                        :expected e-total}))]
        (doseq [{:keys [word expected]} (take 20 word-expectations)]
          (println (format "    %-6s  expected: %8.1f c-fibers" word expected)))
        (println)

        ;; Expected for a-fibers (7 letters, 43,550 fibers)
        (println "  Expected word counts — a-fibers (7 letters × 43,550 fibers):")
        (println)
        (let [n-a-fibers (* db dc dd)  ;; 50 × 13 × 67 = 43,550
              a-word-expectations
              (sort-by :expected >
                       (for [[word _] all-targets
                             :let [e-per-fiber (expected-substring-count freq-map word da)
                                   e-total (* e-per-fiber n-a-fibers)]]
                         {:word word
                          :e-per-fiber e-per-fiber
                          :expected e-total}))]
          (doseq [{:keys [word expected]} (take 20 a-word-expectations)]
            (println (format "    %-6s  expected: %8.1f a-fibers" word expected))))
        (println)

        ;; ════════════════════════════════════════════════════
        ;; PART 1: SCAN ALL 23,450 c-FIBERS
        ;; ════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 1: ALL 23,450 c-FIBERS (13 letters, skip 67)")
        (println "  Scanning for Hebrew words...")
        (println "================================================================")
        (println)

        (let [c-results (atom {:total 0
                               :with-words 0
                               :word-counts {}  ;; word -> count
                               :best []})       ;; top hits
              all-hits (atom (transient []))]

          ;; Scan every c-fiber
          (dotimes [a da]
            (dotimes [b db]
              (dotimes [d dd]
                (let [fstr (extract-c-fiber s a b d)
                      words (find-words-in-string fstr)]
                  (swap! c-results update :total inc)
                  (when (seq words)
                    (swap! c-results update :with-words inc)
                    (doseq [{:keys [word]} words]
                      (swap! c-results update-in [:word-counts word]
                             (fnil inc 0)))
                    ;; Store hits for later analysis
                    (swap! all-hits conj!
                           {:a a :b b :d d :fiber fstr
                            :words words
                            :n-words (count words)}))))))

          (let [c-hits (persistent! @all-hits)
                {:keys [total with-words word-counts]} @c-results]

            (println (format "  Total c-fibers scanned: %,d" total))
            (println (format "  c-fibers with ≥1 word:  %,d  (%.1f%%)"
                             with-words (* 100.0 (/ with-words (double total)))))
            (println)

            ;; Word frequency table: observed vs expected
            (println "  Word frequencies — observed vs expected:")
            (println (format "  %-6s %-16s %8s %8s %8s"
                             "Word" "Meaning" "Observed" "Expected" "Ratio"))
            (println "  " (apply str (repeat 56 "-")))

            (let [n-c-fibers total
                  rows (sort-by :observed >
                                (for [[word _] all-targets
                                      :let [obs (get word-counts word 0)
                                            e-per-fiber (expected-substring-count freq-map word dc)
                                            expected (* e-per-fiber n-c-fibers)]]
                                  {:word word
                                   :observed obs :expected expected
                                   :ratio (if (pos? expected) (/ obs expected) 0.0)}))]
              (doseq [{:keys [word observed expected ratio]} rows
                      :when (or (pos? observed) (> expected 1.0))]
                (println (format "  %-6s %8d %8.1f %8.2f×"
                                 word observed expected ratio))))
            (println)

            ;; Top 30 most word-rich c-fibers
            (println "  ── TOP 30 MOST WORD-RICH c-FIBERS ──")
            (println)
            (let [top30 (take 30 (reverse (sort-by :n-words c-hits)))]
              (doseq [{:keys [a b d fiber words n-words]} top30]
                (let [pos (coords/coord->idx a b 0 d)
                      v (coords/verse-at s pos)]
                  (println (format "    (%d,%2d,*,%2d)  %d words  near %s %d:%d"
                                   a b d n-words (:book v) (:ch v) (:vs v)))
                  (println (format "      fiber: %s" fiber))
                  (println (format "      words: %s"
                                   (str/join ", "
                                             (map #(format "%s@%d"
                                                           (:word %) (:offset %))
                                                  words))))
                  (println))))

            ;; Distribution: which coordinates cluster word-bearing c-fibers?
            (println "  ── WORD-BEARING c-FIBER DISTRIBUTION ──")
            (println)

            ;; By a-axis
            (println "  By a-axis (7 divisions):")
            (let [a-dist (frequencies (map :a c-hits))
                  expected-per-a (/ (count c-hits) 7.0)]
              (doseq [a (range da)]
                (let [cnt (get a-dist a 0)]
                  (println (format "    a=%d: %,5d  (%.2f×)" a cnt
                                   (/ cnt expected-per-a))))))
            (println)

            ;; By d-axis (top/bottom 10)
            (println "  By d-axis (understanding position) — top 10:")
            (let [d-dist (frequencies (map :d c-hits))
                  expected-per-d (/ (count c-hits) 67.0)
                  sorted-d (sort-by val > d-dist)]
              (doseq [[d cnt] (take 10 sorted-d)]
                (println (format "    d=%2d: %,5d  (%.2f×)" d cnt
                                 (/ cnt expected-per-d)))))
            (println)))

        ;; ════════════════════════════════════════════════════
        ;; PART 2: SCAN ALL 43,550 a-FIBERS
        ;; ════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 2: ALL 43,550 a-FIBERS (7 letters, skip 43,550)")
        (println "  Scanning for Hebrew words...")
        (println "================================================================")
        (println)

        (let [a-results (atom {:total 0
                               :with-words 0
                               :word-counts {}})
              a-all-hits (atom (transient []))]

          (dotimes [b db]
            (dotimes [c dc]
              (dotimes [d dd]
                (let [fstr (extract-a-fiber s b c d)
                      words (find-words-in-string fstr)]
                  (swap! a-results update :total inc)
                  (when (seq words)
                    (swap! a-results update :with-words inc)
                    (doseq [{:keys [word]} words]
                      (swap! a-results update-in [:word-counts word]
                             (fnil inc 0)))
                    (swap! a-all-hits conj!
                           {:b b :c c :d d :fiber fstr
                            :words words
                            :n-words (count words)}))))))

          (let [a-hits (persistent! @a-all-hits)
                {:keys [total with-words word-counts]} @a-results]

            (println (format "  Total a-fibers scanned: %,d" total))
            (println (format "  a-fibers with ≥1 word:  %,d  (%.1f%%)"
                             with-words (* 100.0 (/ with-words (double total)))))
            (println)

            ;; Word frequency table
            (println "  Word frequencies — observed vs expected:")
            (println (format "  %-6s %-16s %8s %8s %8s"
                             "Word" "Meaning" "Observed" "Expected" "Ratio"))
            (println "  " (apply str (repeat 56 "-")))

            (let [n-a-fibers total
                  rows (sort-by :observed >
                                (for [[word _] all-targets
                                      :let [obs (get word-counts word 0)
                                            e-per-fiber (expected-substring-count freq-map word da)
                                            expected (* e-per-fiber n-a-fibers)]]
                                  {:word word
                                   :observed obs :expected expected
                                   :ratio (if (pos? expected) (/ obs expected) 0.0)}))]
              (doseq [{:keys [word observed expected ratio]} rows
                      :when (or (pos? observed) (> expected 0.5))]
                (println (format "  %-6s %8d %8.1f %8.2f×"
                                 word observed expected ratio))))
            (println)

            ;; Top 20 a-fibers
            (println "  ── TOP 20 MOST WORD-RICH a-FIBERS ──")
            (println)
            (let [top20 (take 20 (reverse (sort-by :n-words a-hits)))]
              (doseq [{:keys [b c d fiber words n-words]} top20]
                (let [pos (coords/coord->idx 0 b c d)
                      v (coords/verse-at s pos)]
                  (println (format "    (*,%2d,%2d,%2d)  %d words  near %s %d:%d"
                                   b c d n-words (:book v) (:ch v) (:vs v)))
                  (println (format "      fiber: %s" fiber))
                  (println (format "      words: %s"
                                   (str/join ", "
                                             (map #(format "%s@%d"
                                                           (:word %) (:offset %))
                                                  words))))
                  (println))))

            ;; Distribution by c-axis (love)
            (println "  By c-axis (13 love positions):")
            (let [c-dist (frequencies (map :c a-hits))
                  expected-per-c (/ (count a-hits) 13.0)]
              (doseq [c (range dc)]
                (let [cnt (get c-dist c 0)]
                  (println (format "    c=%2d: %,5d  (%.2f×)" c cnt
                                   (/ cnt expected-per-c))))))
            (println)))

        ;; ════════════════════════════════════════════════════
        ;; PART 3: SAMPLE 1,000 b-FIBERS
        ;; ════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 3: SAMPLE 1,000 b-FIBERS (50 letters, skip 871)")
        (println "================================================================")
        (println)

        (let [;; Total b-fibers = 7 × 13 × 67 = 6,097
              ;; Small enough to scan them all
              total-b-fibers (* da dc dd)
              b-results (atom {:total 0
                               :with-words 0
                               :word-counts {}})
              b-all-hits (atom (transient []))]

          (println (format "  Total b-fibers: %,d (scanning all of them)" total-b-fibers))
          (println)

          (dotimes [a da]
            (dotimes [c dc]
              (dotimes [d dd]
                (let [fstr (extract-b-fiber s a c d)
                      words (find-words-in-string fstr)]
                  (swap! b-results update :total inc)
                  (when (seq words)
                    (swap! b-results update :with-words inc)
                    (doseq [{:keys [word]} words]
                      (swap! b-results update-in [:word-counts word]
                             (fnil inc 0)))
                    (swap! b-all-hits conj!
                           {:a a :c c :d d :fiber fstr
                            :words words
                            :n-words (count words)}))))))

          (let [b-hits (persistent! @b-all-hits)
                {:keys [total with-words word-counts]} @b-results]

            (println (format "  b-fibers with ≥1 word:  %,d  (%.1f%%)"
                             with-words (* 100.0 (/ with-words (double total)))))
            (println)

            ;; Word frequency table
            (println "  Word frequencies — observed vs expected:")
            (println (format "  %-6s %-16s %8s %8s %8s"
                             "Word" "Meaning" "Observed" "Expected" "Ratio"))
            (println "  " (apply str (repeat 56 "-")))

            (let [n-b-fibers total
                  rows (sort-by :observed >
                                (for [[word _] all-targets
                                      :let [obs (get word-counts word 0)
                                            e-per-fiber (expected-substring-count freq-map word db)
                                            expected (* e-per-fiber n-b-fibers)]]
                                  {:word word
                                   :observed obs :expected expected
                                   :ratio (if (pos? expected) (/ obs expected) 0.0)}))]
              (doseq [{:keys [word observed expected ratio]} rows
                      :when (or (pos? observed) (> expected 0.5))]
                (println (format "  %-6s %8d %8.1f %8.2f×"
                                 word observed expected ratio))))
            (println)

            ;; Any b-fiber that spells a really interesting word?
            (println "  ── NOTABLE b-FIBER FINDS ──")
            (println)
            (let [notable-words #{"תורה" "יהוה" "אהבה" "בינה" "ישראל" "משה" "אהרן"
                                  "אמת" "קדש" "שלם" "שמר"}
                  notable (filter (fn [{:keys [words]}]
                                    (some #(notable-words (:word %)) words))
                                  b-hits)]
              (println (format "  b-fibers containing key words: %d" (count notable)))
              (doseq [{:keys [a c d fiber words]} (take 20 notable)]
                (let [pos (coords/coord->idx a 0 c d)
                      v (coords/verse-at s pos)
                      key-words (filter #(notable-words (:word %)) words)]
                  (println (format "    (%d,*,%2d,%2d)  near %s %d:%d"
                                   a c d (:book v) (:ch v) (:vs v)))
                  (println (format "      words: %s"
                                   (str/join ", " (map #(format "%s@%d"
                                                                (:word %) (:offset %))
                                                       key-words))))))
              (println))))

        ;; ════════════════════════════════════════════════════
        ;; PART 4: SPECIAL COORDINATE SEARCHES
        ;; ════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 4: SPECIAL COORDINATE SEARCHES")
        (println "  c-fibers at the geometric center and key positions")
        (println "================================================================")
        (println)

        ;; Center c-fiber: (3, 25, *, 33)
        (println "  ── CENTER c-fiber: a=3, b=25, d=33 ──")
        (let [fstr (extract-c-fiber s 3 25 33)
              words (find-words-in-string fstr)]
          (println (format "    fiber: %s" fstr))
          (if (seq words)
            (println (format "    words: %s"
                             (str/join ", " (map #(format "%s@%d"
                                                          (:word %) (:offset %))
                                                  words))))
            (println "    (no target words)")))
        (println)

        ;; Center a-fiber: (*, 25, 6, 33)
        (println "  ── CENTER a-fiber: b=25, c=6, d=33 ──")
        (let [fstr (extract-a-fiber s 25 6 33)
              words (find-words-in-string fstr)]
          (println (format "    fiber: %s" fstr))
          (if (seq words)
            (println (format "    words: %s"
                             (str/join ", " (map #(format "%s@%d"
                                                          (:word %) (:offset %))
                                                  words))))
            (println "    (no target words)")))
        (println)

        ;; Origin fibers
        (println "  ── ORIGIN c-fiber: a=0, b=0, d=0 ──")
        (let [fstr (extract-c-fiber s 0 0 0)
              words (find-words-in-string fstr)]
          (println (format "    fiber: %s" fstr))
          (if (seq words)
            (println (format "    words: %s"
                             (str/join ", " (map #(format "%s@%d"
                                                          (:word %) (:offset %))
                                                  words))))
            (println "    (no target words)")))
        (println)

        ;; c-fibers that spell תורה, יהוה, אהבה, or בינה
        (println "  ── SEARCH: c-fibers spelling key 4-letter words ──")
        (let [key-words {"תורה" "Torah" "יהוה" "YHWH" "אהבה" "love" "בינה" "understanding"}]
          (doseq [[word _] key-words]
            (let [hits (atom 0)]
              (dotimes [a da]
                (dotimes [b db]
                  (dotimes [d dd]
                    (let [fstr (extract-c-fiber s a b d)]
                      (when (>= (.indexOf ^String fstr ^String word) 0)
                        (swap! hits inc)
                        (when (<= @hits 5)
                          (let [pos (coords/coord->idx a b 0 d)
                                v (coords/verse-at s pos)]
                            (println (format "    %s at (%d,%2d,*,%2d)  near %s %d:%d"
                                             word a b d
                                             (:book v) (:ch v) (:vs v)))
                            (println (format "      fiber: %s" (extract-c-fiber s a b d))))))))))
              (println (format "    Total c-fibers with %s: %d" word @hits))
              (println))))

        ;; ════════════════════════════════════════════════════
        ;; PART 5: SUMMARY AND COMPARISON
        ;; ════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 5: SUMMARY — AXIS COMPARISON")
        (println "================================================================")
        (println)

        (println "  Axis    | Length | Count   | Spacing  | Character")
        (println "  --------|--------|---------|----------|--------------------")
        (println "  d-fiber |   67   |  4,550  |    1     | consecutive text")
        (println "  c-fiber |   13   | 23,450  |   67     | word-length probes")
        (println "  b-fiber |   50   |  6,097  |  871     | medium-range skip")
        (println "  a-fiber |    7   | 43,550  | 43,550   | whole-Torah skip")
        (println)))

    (println)
    (println "Done.")))
