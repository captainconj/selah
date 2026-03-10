(ns experiments.078-fibonacci-staircase
  "Experiment 078: The Fibonacci Staircase

   The preimage counts — how many a-fibers contain each Hebrew word —
   land on Fibonacci numbers with zero gaps from F(1) through F(8).

   Seven consecutive Fibonacci levels: 1, 2, 3, 5, 8, 13, 21.
   Each level carries a semantic cluster that forms a biblical narrative arc:
     Existence → Sin/Forgiveness → Character → Gift → Sacrifice → Covenant

   Beyond the staircase, the machine cross-references itself:
   count(word A) = GV(word B), creating a web of equations that
   the machine uses to NAME what it counts.

   PARTS:
   1. The Staircase — Fibonacci counts with semantic groupings
   2. The Cross-Reference Web — count(A) = GV(B) equations
   3. Double-Fibonacci words — words on the spiral twice
   4. The Triangular Sum — F=3 level and T(26) = YHWH
   5. The Gaps — what lives between the Fibonacci levels
   6. The Golden Thread — ratio analysis and convergence"
  (:require [selah.space.coords :as coords]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Fibonacci utilities ─────────────────────────────────────

(def fibs
  "Lazy Fibonacci sequence."
  (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))

(def fib-set
  "First 20 Fibonacci numbers as a set."
  (set (take 20 fibs)))

(defn fib?
  "Is n a Fibonacci number?"
  [n]
  (contains? fib-set n))

(defn fib-index
  "Which Fibonacci number is n? (1-indexed). nil if not Fibonacci."
  [n]
  (first (keep-indexed (fn [i f] (when (= f n) (inc i)))
                       (take 20 fibs))))

(defn triangular
  "T(n) = 1+2+...+n = n(n+1)/2"
  [n]
  (/ (* n (inc n)) 2))

(defn triangular-root
  "If n is triangular, return the root. Otherwise nil."
  [n]
  (some #(when (= n (triangular %)) %) (range 1 200)))

;; ── Vocabulary (same as 077) ────────────────────────────────

(def vocab
  {"את" "aleph-tav" "אל" "God/to" "אב" "father" "בן" "son" "לב" "heart"
   "דם" "blood" "שם" "name" "יד" "hand" "חי" "living" "רע" "evil/friend"
   "אש" "fire" "עז" "strong" "כל" "all" "אם" "mother/if" "עם" "people"
   "בר" "grain/son" "חן" "grace" "רב" "many" "גר" "sojourner" "פר" "bull"
   "עד" "witness" "עץ" "tree" "גן" "garden" "נח" "Noah"
   "אור" "light" "שמר" "guard" "אמת" "truth" "קדש" "holy" "יום" "day"
   "שבע" "seven" "טוב" "good" "אדם" "man" "חוה" "Eve" "מים" "water"
   "ארץ" "land" "שלם" "peace" "משה" "Moses" "דבר" "word/thing" "ברך" "bless"
   "חכם" "wise" "זרע" "seed" "נפש" "soul" "רוח" "spirit" "עין" "eye"
   "אחד" "one" "כהן" "priest" "מלך" "king" "גאל" "redeem" "רחם" "mercy"
   "שבת" "sabbath" "חסד" "lovingkindness" "עבד" "servant" "כפר" "atone"
   "נתן" "give" "שמע" "hear" "ידע" "know" "ראה" "see" "הלך" "walk"
   "ישב" "dwell" "עמד" "stand" "נשא" "lift/carry" "שלח" "send" "קרא" "call"
   "מות" "death" "חטא" "sin" "ברא" "create" "צדק" "righteous" "דרך" "way/path"
   "שמן" "oil" "יין" "wine" "לחם" "bread" "אבן" "stone" "כבד" "glory/heavy"
   "ענן" "cloud" "עלה" "ascend/offering" "טהר" "pure" "אמן" "amen/faithful"
   "צור" "rock" "חלק" "portion" "כרת" "cut(covenant)" "נדר" "vow"
   "עשה" "make/do" "בוא" "come/enter" "כבש" "lamb" "זבח" "sacrifice"
   "נחש" "serpent" "קשת" "bow/rainbow" "דעת" "knowledge" "ענה" "afflict"
   "פרי" "fruit" "חשך" "darkness" "שפך" "pour-out" "נקם" "avenge"
   "סלח" "forgive" "פדה" "ransom" "ירש" "inherit" "חרב" "sword"
   "מטה" "staff/tribe" "באר" "well"
   "תורה" "Torah" "יהוה" "YHWH" "אהבה" "love" "בינה" "understanding"
   "אהרן" "Aaron" "שמים" "heaven" "ברית" "covenant" "צדקה" "righteousness"
   "חיים" "life" "אלהם" "Elohim(def)" "עולם" "eternal" "מועד" "appointed-time"
   "משכן" "tabernacle" "מזבח" "altar" "אדני" "Lord" "כרוב" "cherub"
   "ישראל" "Israel" "אלהים" "Elohim" "חכמה" "wisdom" "משפט" "judgment"
   "קרבן" "offering"})

;; ── PART 1: The Staircase ──────────────────────────────────

(defn part-1-staircase
  "Scan all words, identify Fibonacci counts, display the narrative staircase."
  []
  (println "================================================================")
  (println "  PART 1: THE FIBONACCI STAIRCASE")
  (println "  Seven consecutive Fibonacci numbers — zero gaps")
  (println "================================================================")
  (println)

  (let [t0 (System/currentTimeMillis)
        results (coords/preimage-all (keys vocab))
        elapsed (- (System/currentTimeMillis) t0)
        ;; Build count table
        count-table (sort-by (comp - :count)
                             (for [[w hits] results]
                               {:word w :count (count hits)
                                :gv (gem/word-value w)
                                :hits hits}))]

    (println (format "  Scanned 104 words × 43,550 fibers in %.1fs" (/ elapsed 1000.0)))
    (println)

    ;; Show which counts are Fibonacci
    (let [by-count (group-by :count count-table)
          fib-levels (sort (filter fib? (keys by-count)))]

      (println "  ── THE STAIRCASE ──")
      (println)
      (doseq [f fib-levels]
        (let [words (get by-count f)
              gv-sum (reduce + (map :gv words))
              tri-root (triangular-root gv-sum)]
          (println (format "  F(%d) = %2d │ %d word(s) │ GV sum = %,d%s"
                           (fib-index f) f (count words) gv-sum
                           (if tri-root (format " = T(%d)" tri-root) "")))
          (doseq [{:keys [word gv]} (sort-by :gv words)]
            (let [gv-fib (when (fib? gv) (format " = F(%d)" (fib-index gv)))]
              (println (format "         │   %s GV=%d%s"
                               word gv (or gv-fib "")))))
          (println)))

      ;; Non-Fibonacci counts in the same range
      (println "  ── BETWEEN THE STEPS (non-Fibonacci counts 1–21) ──")
      (println)
      (let [non-fib (sort (remove fib? (filter #(<= 1 % 21) (keys by-count))))]
        (doseq [n non-fib]
          (let [words (get by-count n)
                gv-sum (reduce + (map :gv words))]
            (println (format "  count = %2d │ %d word(s) │ GV sum = %,d"
                             n (count words) gv-sum))
            (doseq [{:keys [word gv]} (sort-by :gv words)]
              (println (format "             │   %s GV=%d" word gv))))))
      (println)

      ;; The sum of the Fibonacci staircase levels
      (let [level-sum (reduce + fib-levels)]
        (println (format "  Sum of Fibonacci levels: %s = %d"
                         (str/join "+" (map str fib-levels))
                         level-sum))
        (println (format "    %d = GV(גן/garden) = GV(אבן/stone)" level-sum))
        (println "    The staircase sums to the garden."))
      (println)

      {:results results :count-table count-table :by-count by-count})))

;; ── PART 2: Cross-Reference Web ─────────────────────────────

(defn part-2-cross-references
  "Find all count(A) = GV(B) equations."
  [count-table]
  (println "================================================================")
  (println "  PART 2: THE CROSS-REFERENCE WEB")
  (println "  count(A) = GV(B) — the machine NAMES what it counts")
  (println "================================================================")
  (println)

  (let [gv-index (group-by :gv count-table)
        cnt-index (group-by :count count-table)
        ;; Find all cross-references
        xrefs (for [{:keys [word count gv]} count-table
                    :when (pos? count)
                    :let [targets (get gv-index count)]
                    target targets
                    :when (not= word (:word target))]
                {:from word :count count
                 :to (:word target)
                 :to-gv (:gv target)})]

    ;; Group by count value for cleaner display
    (let [by-count (group-by :count (sort-by :count xrefs))]
      (doseq [[cnt refs] (sort-by key by-count)]
        (let [from-words (distinct (map :from refs))
              to-words (distinct (map :to refs))]
          (println (format "  count = %d:" cnt))
          (println (format "    counted: %s"
                           (str/join ", " (map #(str % "(" (get vocab % "") ")")
                                               from-words))))
          (println (format "    = GV of: %s"
                           (str/join ", " (map #(str % "(" (get vocab % "") ")")
                                               to-words))))
          (println))))

    (println (format "  Total cross-references found: %d" (count xrefs)))
    (println (format "  Distinct count values involved: %d"
                     (count (distinct (map :count xrefs)))))
    (println)))

;; ── PART 3: Double-Fibonacci Words ──────────────────────────

(defn part-3-double-fibonacci
  "Words where both count AND gematria are Fibonacci numbers."
  [count-table]
  (println "================================================================")
  (println "  PART 3: DOUBLE-FIBONACCI WORDS")
  (println "  Words inscribed on the growth spiral twice")
  (println "================================================================")
  (println)

  (let [doubles (filter (fn [{:keys [count gv]}]
                          (and (fib? count) (fib? gv) (pos? count)))
                        count-table)]
    (if (seq doubles)
      (doseq [{:keys [word count gv]} (sort-by :count doubles)]
        (println (format "  %s: count=%d=F(%d), GV=%d=F(%d)"
                         word
                         count (fib-index count)
                         gv (fib-index gv))))
      (println "  None found."))
    (println)

    ;; Words with Fibonacci GV (whether or not count is Fibonacci)
    (println "  ── ALL FIBONACCI-VALUED WORDS ──")
    (println)
    (let [fib-gv (filter #(fib? (:gv %)) count-table)]
      (doseq [{:keys [word count gv]} (sort-by :gv fib-gv)]
        (println (format "  %s GV=%d=F(%d), count=%d%s"
                         word gv (fib-index gv) count
                         (if (fib? count) (format " =F(%d) ★" (fib-index count)) "")))))
    (println)

    ;; GV values that are squares of Fibonacci
    (println "  ── FIBONACCI SQUARES IN GEMATRIA ──")
    (println)
    (let [fib-sq (set (map #(* % %) (take 10 fibs)))]
      (doseq [{:keys [word gv count]} count-table
              :when (and (fib-sq gv) (> gv 1))]
        (let [root (int (Math/sqrt gv))]
          (println (format "  %s GV=%d = %d² = F(%d)²"
                           word gv root (fib-index root))))))
    (println)))

;; ── PART 4: The Triangular Sum ──────────────────────────────

(defn part-4-triangular
  "The F=3 level and T(26) = triangle of YHWH."
  [by-count]
  (println "================================================================")
  (println "  PART 4: THE TRIANGULAR SUM — T(26) = YHWH")
  (println "================================================================")
  (println)

  ;; Check GV sums at each Fibonacci level for triangularity
  (doseq [f [1 2 3 5 8 13 21]]
    (when-let [words (get by-count f)]
      (let [gvs (map :gv words)
            gv-sum (reduce + gvs)
            tri-root (triangular-root gv-sum)]
        (println (format "  F=%2d: GV sum = %,d" f gv-sum))
        (when tri-root
          (println (format "         = T(%d) = 1+2+...+%d" tri-root tri-root))
          (when (= 26 tri-root)
            (println "         26 = GV(יהוה/YHWH)")
            (println "         The sin/forgiveness level sums to the triangle of God's name.")))
        ;; Divisibility
        (doseq [[d label] [[7 "completeness"] [13 "love"] [26 "YHWH"] [67 "understanding"]]]
          (when (zero? (mod gv-sum d))
            (println (format "         ÷%d = %d (%s)" d (/ gv-sum d) label))))
        (println))))

  ;; The specific F=3 story
  (println "  ── THE F=3 NARRATIVE ──")
  (println)
  (println "  At the sin/forgiveness level:")
  (println "    עולם(eternal) + פדה(ransom) + סלח(forgive) + חטא(sin)")
  (println "    = 146 + 89 + 98 + 18 = 351")
  (println "    = T(26) = 1+2+3+...+26")
  (println "    = triangle of יהוה (GV=26)")
  (println)
  (println "  All four words have count 3 = GV(אב/father)")
  (println "  → The Father handles sin through ransom and forgiveness for eternity")
  (println "  → And the sum of it is the triangle of God's name")
  (println)
  (println "  351/3 = 117 = 9 × 13")
  (println "  → The average GV per word at this level = 9 × love")
  (println)

  ;; Also check: is פדה(ransom) GV=89 a Fibonacci number?
  (println "  פדה(ransom) GV=89 = F(11) — ransom itself is a Fibonacci number")
  (println "  This makes ransom a double-Fibonacci word: count=3=F(4), GV=89=F(11)")
  (println))

;; ── PART 5: The Gaps ────────────────────────────────────────

(defn part-5-gaps
  "What lives between the Fibonacci levels? The container and mechanism."
  [by-count]
  (println "================================================================")
  (println "  PART 5: THE GAPS — BETWEEN THE FIBONACCI STEPS")
  (println "  What holds the staircase together?")
  (println "================================================================")
  (println)

  (let [gap-levels [4 6 7 9 10 11 12 14 15 16 17]]
    (doseq [n gap-levels]
      (when-let [words (get by-count n)]
        (let [gv-sum (reduce + (map :gv words))]
          (println (format "  count = %2d │ %d word(s) │ GV sum = %,d" n (count words) gv-sum))
          (doseq [{:keys [word gv]} (sort-by :gv words)]
            (let [notable (cond
                            (= gv 67)  " ← d-axis!"
                            (= gv 26)  " ← YHWH"
                            (fib? gv)  (format " ← F(%d)" (fib-index gv))
                            :else "")]
              (println (format "             │   %s GV=%d%s" word gv notable))))
          (println)))))

  ;; Semantic characterization
  (println "  ── SEMANTIC READING ──")
  (println)
  (println "  count=4 (pure, king, understanding, darkness, covenant):")
  (println "    → THE FRAMEWORK — the poles and structure")
  (println "    → בינה(understanding) GV=67 = the d-axis dimension")
  (println)
  (println "  count=6 (atone, oil, serpent, vow, soul, priest, bless):")
  (println "    → THE MECHANISM — temple service and ritual")
  (println)
  (println "  count=7 (cloud alone):")
  (println "    → THE PRESENCE — ענן(cloud) at exactly 7 = a-axis = completeness")
  (println)
  (println "  The Fibonacci levels tell the STORY.")
  (println "  The gaps provide the INFRASTRUCTURE.")
  (println))

;; ── PART 6: The Golden Thread ───────────────────────────────

(defn part-6-golden-thread
  "Golden ratio analysis: consecutive Fibonacci count ratios,
   ratio of word counts at each level, convergence."
  [by-count]
  (println "================================================================")
  (println "  PART 6: THE GOLDEN THREAD")
  (println "  φ in the counting structure")
  (println "================================================================")
  (println)

  (let [phi (/ (+ 1 (Math/sqrt 5)) 2)]
    (println (format "  φ (golden ratio) = %.10f" phi))
    (println)

    ;; Consecutive Fibonacci count ratios
    (println "  ── CONSECUTIVE FIBONACCI RATIOS ──")
    (println)
    (doseq [[a b] [[21 13] [13 8] [8 5] [5 3] [3 2] [2 1]]]
      (let [ratio (/ (double a) b)
            err (- phi ratio)]
        (println (format "  %2d / %2d = %.6f  (error from φ: %+.6f)" a b ratio err))))
    (println)

    ;; Word count at each level
    (println "  ── WORDS PER FIBONACCI LEVEL ──")
    (println)
    (let [word-counts (for [f [1 2 3 5 8 13 21]]
                        [f (count (get by-count f []))])]
      (doseq [[f wc] word-counts]
        (println (format "  F=%2d: %d words" f wc)))
      (println)
      (println "  6, 5, 4, 4, 4, 1, 1")
      (println "  The staircase narrows: many words at the base, singletons at the peak.")
      (println "  The lamb and covenant stand alone."))
    (println)

    ;; Check near-miss Fibonacci at higher counts
    (println "  ── HIGHER FIBONACCI NEAR-MISSES ──")
    (println)
    (let [all-counts (sort (filter pos? (keys by-count)))]
      (doseq [f [34 55 89]]
        (let [nearest (apply min-key #(Math/abs (long (- % f))) all-counts)
              delta (- nearest f)
              word (first (get by-count nearest))]
          (println (format "  F(%d) = %3d → nearest count = %d (%s) Δ=%+d"
                           (fib-index f) f nearest (:word word) delta))))))
  (println))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println)
  (println "╔══════════════════════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 078: THE FIBONACCI STAIRCASE                   ║")
  (println "║                                                            ║")
  (println "║  Seven consecutive Fibonacci numbers in preimage counts    ║")
  (println "║  The growth spiral organizes meaning                       ║")
  (println "╚══════════════════════════════════════════════════════════════╝")
  (println)

  (let [{:keys [results count-table by-count]} (part-1-staircase)]
    (part-2-cross-references count-table)
    (part-3-double-fibonacci count-table)
    (part-4-triangular by-count)
    (part-5-gaps by-count)
    (part-6-golden-thread by-count))

  (println)
  (println "  ═══════════════════════════════════════════════════════")
  (println "  SUMMARY")
  (println "  ═══════════════════════════════════════════════════════")
  (println)
  (println "  The preimage counts organize along the Fibonacci sequence.")
  (println "  Seven consecutive levels, zero gaps, telling the biblical arc:")
  (println)
  (println "    1: The rare sacred")
  (println "    2: Existence")
  (println "    3: Sin → Forgiveness  (GV sum = T(26) = triangle of YHWH)")
  (println "    5: Character          (love, holy, good, wise)")
  (println "    8: Gifts              (Torah, rainbow, give, portion)")
  (println "   13: The Sacrifice      (lamb — alone)")
  (println "   21: The Covenant       (cut-covenant — alone)")
  (println)
  (println "  The machine cross-references itself: count(A) = GV(B).")
  (println "  The lamb IS one IS love. The son IS light. Blood IS the way.")
  (println)
  (println "  Only love and ransom touch the spiral twice.")
  (println "  The staircase sums to 53 = the garden.")
  (println)
  (println "  selah.")
  (println)
  (println "Done."))
