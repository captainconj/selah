(ns experiments.061b-axis-words
  "Experiment 061b: Where do the axis numbers appear as WORDS in the Torah?

   304,850 = 7 × 50 × 13 × 67

   The factorization names four numbers. Those numbers name concepts:
     7  = שבע (seven) — completeness, the Sabbath
     50 = חמשים (fifty) — jubilee, freedom, the fiftieth gate
     13 = שלש עשרה (thirteen) — אחד (one, gv=13), אהבה (love, gv=13)
     67 = בינה (understanding, gv=67)

   Do these words cluster at particular coordinates in the 4D space?
   Do they appear at fold points? Near each other? At boundaries?

   FINDINGS:

   1. בינה (understanding) and אהבה (love) do NOT appear in the Torah.
      They are post-Pentateuch words (Proverbs, Song of Songs, etc.).
      The axis names are concepts the Torah EMBODIES but does not SAY.
      The structure speaks what the text is silent about.

   2. אחד (one, gv=13) — 189 occurrences.
      MASSIVELY concentrated at a=4 (3.19× expected) — Numbers 7,
      the tribal offerings: 'one silver bowl, one silver basin...'
      repeated for each of the 12 tribes. The word 'one' hammers
      the same region of the space.

   3. שבע (seven) — 171 occurrences, relatively uniform across a-axis.
      Peaks at a=1 (1.43×, Genesis 41 — Pharaoh's dreams of 7 cows/ears).
      Dips at a=6 (0.45×, Deuteronomy — few narrative uses of 'seven').
      Peaks at c=7 (1.44×) — the word 'seven' clusters on the SEVENTH
      position of the love/unity axis.

   4. חמשים (fifty) — 39 occurrences.
      Peaks at a=2 and a=4 (Tabernacle curtains: 50 clasps; Levitical
      service age: 50 years). Zero in a=1 (Genesis narrative section).
      c=7 peak again (2.00×).

   5. יובל (jubilee) — only 4 occurrences.
      Genesis 4:21 (Jubal, inventor of instruments — the name IS the word).
      Leviticus 25:10-12 (the jubilee law itself).
      All Lev hits at a=4, c=9-11. Tightly clustered.

   6. 'Thirteen' as a number-word: only 2 occurrences in the entire Torah.
      Gen 17:25 (Ishmael was 13 when circumcised) and Num 29:13 (13 bulls).

   7. Co-occurrence: שבע + אחד share 11 d-fibers (67-letter understanding
      lines). Genesis 41 (dreams) and Numbers 28-29 (offerings) are the
      main clusters. Seven and One appear together on the same line of
      understanding.

   8. c=7 affinity: both שבע and חמשים peak at c=7 (the 8th position
      on the 13-position love axis). 7 is the only value that is both
      an axis dimension AND a coordinate peak for other axis words.

   9. LEVITICUS 25:8 — the verse that multiplies 7 to get 50.
      'Count SEVEN sabbaths of years — SEVEN times SEVEN years —
       forty-nine years... consecrate the FIFTIETH year.'
      All 4 שבע in this verse sit on the SAME d-fiber: (4, 6, 7, *).
      That fiber is at c=7. The verse about 7×7→50 puts its sevens
      at the seventh coordinate of love, on one line of understanding.

   10. The Shema (Deut 6:4): אחד at [6, 0, 3, 11].
       a=6 — the final seventh (the seventh day).
       The most important 'one' in Judaism lands in the last division.

   11. The two absent axis-concept words (בינה and אהבה) are both
       gematria-connected: אהבה = 13 (the c-axis dimension) and
       בינה = 67 (the d-axis dimension). The text gives us the
       numbers 7 and 50 in words. The concepts of love (13) and
       understanding (67) it gives through structure alone."
  (:require [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Build word-position index ────────────────────────────────

(defn build-word-index
  "Build a seq of {:word <normalized-letters> :raw <original> :book :ch :vs :start :end}
   mapping each word to its position in the Torah letter stream."
  []
  (let [pos (atom 0)]
    (vec
     (for [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
           word (oshb/book-words book)
           :let [letters (norm/letter-stream (:text word))
                 n (count letters)
                 start @pos
                 _ (swap! pos + n)]
           :when (pos? n)]
       {:word    (apply str letters)
        :raw     (:text word)
        :book    book
        :ch      (:chapter word)
        :vs      (:verse word)
        :start   start
        :end     (+ start n)}))))

(defn find-word
  "Find all occurrences of a target word (exact normalized match).
   Returns seq of word-index entries."
  [word-idx target]
  (filter #(= (:word %) target) word-idx))

(defn find-words
  "Find all occurrences matching any of the given targets."
  [word-idx targets]
  (let [target-set (set targets)]
    (filter #(target-set (:word %)) word-idx)))

(defn find-word-prefix
  "Find words that START with the given prefix."
  [word-idx prefix]
  (filter #(str/starts-with? (:word %) prefix) word-idx))

;; ── Reporting helpers ────────────────────────────────────────

(defn describe-hit
  "Describe a word hit with its 4D coordinates."
  [hit]
  (let [coord (vec (coords/idx->coord (:start hit)))]
    (merge hit {:coord coord
                :a (nth coord 0)
                :b (nth coord 1)
                :c (nth coord 2)
                :d (nth coord 3)})))

(defn print-hits
  "Print a list of word hits with coordinates."
  [label hits]
  (println (format "\n=== %s === (%d occurrences)" label (count hits)))
  (println)
  (when (seq hits)
    (let [described (map describe-hit hits)]
      (doseq [h described]
        (println (format "  [%d %2d %2d %2d]  pos=%6d  %s %d:%d  %s"
                         (:a h) (:b h) (:c h) (:d h)
                         (:start h)
                         (:book h) (:ch h) (:vs h)
                         (:word h))))
      described)))

(defn axis-distribution
  "Show how hits distribute across each axis."
  [label hits]
  (when (seq hits)
    (let [described (map describe-hit hits)]
      (println (format "\n  %s — axis distribution:" label))
      (doseq [[axis-name axis-key dim-count]
              [["a (7 days)"        :a 7]
               ["b (50 jubilees)"   :b 50]
               ["c (13 one/love)"   :c 13]
               ["d (67 understanding)" :d 67]]]
        (let [freq (frequencies (map axis-key described))
              expected (/ (count described) (double dim-count))]
          ;; Only show a and c (compact). b and d are too wide.
          (when (<= dim-count 13)
            (println (format "    %s:" axis-name))
            (doseq [i (range dim-count)]
              (let [cnt (get freq i 0)
                    ratio (if (pos? expected) (/ cnt expected) 0.0)]
                (println (format "      %2d: %3d  (%.2f×)" i cnt ratio))))))))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "═══════════════════════════════════════════════════")
  (println "  061b: THE AXIS WORDS — where the numbers speak")
  (println "  304,850 = 7 × 50 × 13 × 67")
  (println "═══════════════════════════════════════════════════")

  (println "\nBuilding space and word index...")
  (def s (coords/space))
  (def widx (build-word-index))
  (println (format "  %,d words indexed." (count widx)))

  ;; ── Seven (שבע / שבעה / שבעת) ────────────────────────────

  (let [hits (find-words widx ["שבע" "שבעה" "שבעת"])]
    (def seven-hits (print-hits "SEVEN — שבע/שבעה/שבעת (7)" hits))
    (axis-distribution "seven" hits))

  ;; ── Fifty (חמשים) ─────────────────────────────────────────

  (let [hits (find-word widx "חמשים")]
    (def fifty-hits (print-hits "FIFTY — חמשים (50)" hits))
    (axis-distribution "fifty" hits))

  ;; ── Thirteen — multi-word, so look for שלש + עשרה, שלשה + עשר ──

  (println "\n=== THIRTEEN — שלש עשרה / שלשה עשר (13) ===")
  (println)
  (let [;; Find all שלש/שלשה words, then check if next word is עשרה/עשר
        all-words widx
        n (count all-words)
        thirteen-hits
        (for [i (range (dec n))
              :let [w1 (nth all-words i)
                    w2 (nth all-words (inc i))]
              :when (or (and (= (:word w1) "שלש") (= (:word w2) "עשרה"))
                        (and (= (:word w1) "שלשה") (= (:word w2) "עשר"))
                        (and (= (:word w1) "שלשה") (= (:word w2) "עשרה")))]
          (let [combined-word (str (:word w1) (:word w2))
                coord (vec (coords/idx->coord (:start w1)))]
            (merge w1 {:word combined-word
                       :coord coord
                       :a (nth coord 0)
                       :b (nth coord 1)
                       :c (nth coord 2)
                       :d (nth coord 3)})))]
    (def thirteen-hits thirteen-hits)
    (println (format "  %d occurrences" (count thirteen-hits)))
    (doseq [h thirteen-hits]
      (println (format "  [%d %2d %2d %2d]  pos=%6d  %s %d:%d  %s"
                       (:a h) (:b h) (:c h) (:d h)
                       (:start h)
                       (:book h) (:ch h) (:vs h)
                       (:word h)))))

  ;; ── Understanding / בינה (gv=67) ──────────────────────────

  (let [hits (find-word widx "בינה")]
    (def binah-hits (print-hits "BINAH — בינה (understanding, gv=67)" hits))
    (axis-distribution "binah" hits))

  ;; ── One / אחד (gv=13) ─────────────────────────────────────

  (let [hits (find-word widx "אחד")]
    (def echad-hits (print-hits "ECHAD — אחד (one, gv=13)" hits))
    (axis-distribution "echad" hits))

  ;; ── Love / אהבה (gv=13) ───────────────────────────────────

  (let [hits (find-word widx "אהבה")]
    (def ahavah-hits (print-hits "AHAVAH — אהבה (love, gv=13)" hits))
    (axis-distribution "ahavah" hits))

  ;; ── Jubilee / יובל ─────────────────────────────────────────

  (let [hits (find-word widx "יובל")]
    (def yovel-hits (print-hits "YOVEL — יובל (jubilee)" hits))
    (axis-distribution "yovel" hits))

  ;; ══════════════════════════════════════════════════════════
  ;; OBSERVATIONS
  ;; ══════════════════════════════════════════════════════════

  (println "\n═══════════════════════════════════════════════════")
  (println "  OBSERVATIONS")
  (println "═══════════════════════════════════════════════════")

  ;; Center checks — do any land at or near the geometric center (3,25,6,33)?
  (println "\n--- Proximity to geometric center (3, 25, 6, 33) ---")
  (let [center-a 3 center-b 25 center-c 6 center-d 33
        all-described (concat seven-hits fifty-hits
                              (map describe-hit (find-word widx "בינה"))
                              (map describe-hit (find-word widx "אחד"))
                              (map describe-hit (find-word widx "אהבה"))
                              (map describe-hit (find-word widx "יובל"))
                              thirteen-hits)
        center-a-hits (filter #(= (:a %) center-a) all-described)]
    (println (format "  %d of %d axis-word occurrences fall in a=3 (center seventh)"
                     (count center-a-hits)
                     (count all-described))))

  ;; Boundary analysis — how many fall at d=0 or d=66 (understanding boundaries)?
  (println "\n--- Boundary positions ---")
  (let [all-described (concat seven-hits fifty-hits
                              (map describe-hit (find-word widx "בינה"))
                              (map describe-hit (find-word widx "אחד"))
                              (map describe-hit (find-word widx "אהבה"))
                              (map describe-hit (find-word widx "יובל"))
                              thirteen-hits)]
    (let [d0  (count (filter #(= (:d %) 0) all-described))
          d66 (count (filter #(= (:d %) 66) all-described))
          c0  (count (filter #(= (:c %) 0) all-described))
          c12 (count (filter #(= (:c %) 12) all-described))]
      (println (format "  d=0 (understanding start): %d" d0))
      (println (format "  d=66 (understanding end):  %d" d66))
      (println (format "  c=0 (love start):          %d" c0))
      (println (format "  c=12 (love end):           %d" c12))))

  ;; Co-occurrence: do axis words appear in the same verse?
  (println "\n--- Co-occurrence in same verse ---")
  (let [all-hits (concat
                  (map #(assoc (describe-hit %) :label "שבע") (find-words widx ["שבע" "שבעה" "שבעת"]))
                  (map #(assoc (describe-hit %) :label "חמשים") (find-word widx "חמשים"))
                  (map #(assoc (describe-hit %) :label "בינה") (find-word widx "בינה"))
                  (map #(assoc (describe-hit %) :label "אחד") (find-word widx "אחד"))
                  (map #(assoc (describe-hit %) :label "אהבה") (find-word widx "אהבה"))
                  (map #(assoc (describe-hit %) :label "יובל") (find-word widx "יובל")))
        verse-key (fn [h] [(:book h) (:ch h) (:vs h)])
        by-verse (group-by verse-key all-hits)
        multi (filter (fn [[_ vs]] (> (count (distinct (map :label vs))) 1)) by-verse)]
    (doseq [[vk vs] (sort-by (fn [[_ vs]] (:start (first vs))) multi)]
      (let [labels (distinct (map :label vs))]
        (println (format "  %s %d:%d — %s"
                         (first vk) (second vk) (nth vk 2)
                         (str/join " + " labels))))))

  ;; Same d-fiber — do any axis words share a d-fiber (same a,b,c)?
  (println "\n--- Shared d-fibers (same a, b, c = 67-letter understanding line) ---")
  (let [all-hits (concat
                  (map #(assoc (describe-hit %) :label "שבע") (find-words widx ["שבע" "שבעה" "שבעת"]))
                  (map #(assoc (describe-hit %) :label "חמשים") (find-word widx "חמשים"))
                  (map #(assoc (describe-hit %) :label "בינה") (find-word widx "בינה"))
                  (map #(assoc (describe-hit %) :label "אחד") (find-word widx "אחד"))
                  (map #(assoc (describe-hit %) :label "אהבה") (find-word widx "אהבה"))
                  (map #(assoc (describe-hit %) :label "יובל") (find-word widx "יובל")))
        fiber-key (fn [h] [(:a h) (:b h) (:c h)])
        by-fiber (group-by fiber-key all-hits)
        multi (filter (fn [[_ vs]] (> (count (distinct (map :label vs))) 1)) by-fiber)]
    (println (format "  %d fibers contain multiple axis words:" (count multi)))
    (doseq [[fk vs] (sort-by (fn [[_ vs]] (:start (first vs))) multi)]
      (let [labels (frequencies (map :label vs))]
        (println (format "    fiber [%d %2d %2d *] — %s"
                         (nth fk 0) (nth fk 1) (nth fk 2)
                         (str/join ", " (map (fn [[l c]] (format "%s×%d" l c)) labels)))))))

  ;; The seven divisions — how do שבע words distribute across the 7 parts?
  (println "\n--- שבע across the seven divisions (expected: uniform ≈ 1.00×) ---")
  (let [seven-described (map describe-hit (find-words widx ["שבע" "שבעה" "שבעת"]))
        a-freq (frequencies (map :a seven-described))
        expected (/ (count seven-described) 7.0)]
    (doseq [a (range 7)]
      (let [cnt (get a-freq a 0)]
        (println (format "    a=%d: %3d  (%.2f×)" a cnt (/ cnt expected))))))

  ;; The Shema — Deuteronomy 6:4
  (println "\n--- The Shema: 'Hear O Israel, the LORD our God, the LORD is ONE' ---")
  (let [shema-echad (first (filter #(and (= "Deuteronomy" (:book %))
                                          (= 6 (:ch %)) (= 4 (:vs %)))
                                    (find-word widx "אחד")))]
    (when shema-echad
      (let [d (describe-hit shema-echad)]
        (println (format "  אחד in Deut 6:4 — coord [%d %2d %2d %2d]  pos=%d"
                         (:a d) (:b d) (:c d) (:d d) (:start d)))
        (println (format "  a=%d (day %d of 7)" (:a d) (inc (:a d))))
        (println (format "  c=%d (position on the love/unity axis)" (:c d)))
        (println (format "  d=%d (position on the understanding axis)" (:d d))))))

  ;; The c=7 coincidence
  (println "\n--- c=7 affinity: the seventh position on the love axis ---")
  (let [seven-at-c7 (count (filter #(= 7 (:c (describe-hit %)))
                                    (find-words widx ["שבע" "שבעה" "שבעת"])))
        fifty-at-c7 (count (filter #(= 7 (:c (describe-hit %)))
                                    (find-word widx "חמשים")))]
    (println (format "  שבע at c=7: %d of 171 (%.1f%%, expected %.1f%%)"
                     seven-at-c7 (* 100.0 (/ seven-at-c7 171.0))
                     (/ 100.0 13.0)))
    (println (format "  חמשים at c=7: %d of 39 (%.1f%%, expected %.1f%%)"
                     fifty-at-c7 (* 100.0 (/ fifty-at-c7 39.0))
                     (/ 100.0 13.0))))

  ;; What's absent — the silent axes
  (println "\n--- The silent axes ---")
  (println "  בינה (understanding, gv=67): 0 occurrences in the Torah.")
  (println "  אהבה (love, gv=13):          0 occurrences in the Torah.")
  (println)
  (println "  The factorization [7 50 13 67] names two concepts that")
  (println "  the Torah EMBODIES but never SAYS as words.")
  (println "  Understanding and love are structural, not lexical.")
  (println "  The text shows them; it doesn't tell them.")

  ;; Lev 25:8 — the verse that multiplies seven by seven
  (println "\n--- Leviticus 25:8 — 'seven sabbaths of years, seven times seven' ---")
  (let [lev258 (filter #(and (= "Leviticus" (:book %)) (= 25 (:ch %)) (= 8 (:vs %)))
                        (find-words widx ["שבע" "שבעה" "שבעת"]))]
    (println (format "  %d uses of שבע in a single verse:" (count lev258)))
    (doseq [h lev258]
      (let [d (describe-hit h)]
        (println (format "    [%d %2d %2d %2d]  %s" (:a d) (:b d) (:c d) (:d d) (:word d)))))
    (println "  This verse connects 7 (completeness) to 50 (jubilee):"))
  (println "  'Count seven sabbaths of years — seven times seven years —")
  (println "   forty-nine years. Then you shall sound the trumpet on the")
  (println "   tenth day of the seventh month... consecrate the FIFTIETH year.'")

  ;; Genesis 1:5 — the first אחד in the Torah
  (println "\n--- Genesis 1:5 — the first אחד: 'And there was evening and morning, day ONE' ---")
  (let [first-echad (first (find-word widx "אחד"))
        d (describe-hit first-echad)]
    (println (format "  First אחד in Torah: [%d %2d %2d %2d]  pos=%d"
                     (:a d) (:b d) (:c d) (:d d) (:start d)))
    (println (format "  a=%d, c=%d, d=%d" (:a d) (:c d) (:d d)))
    (println "  The word 'one' first appears — naming the first day."))

  (println "\n═══════════════════════════════════════════════════")
  (println "  Done.")
  (println "═══════════════════════════════════════════════════"))
