(ns experiments.080-word-census
  "Experiment 080: Word Census

   Exhaustive inventory of every word in the Torah:
   frequency, gematria, preimage count on a-fibers.

   Then: automated pattern detection across the entire vocabulary.
   Fibonacci, triangular, cross-references, divisibility — all of it.

   This is the foundation. Everything after this builds on these tables."
  (:require [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [selah.gematria :as gem]
            [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Word extraction ──────────────────────────────────────────

(defn torah-words
  "Extract all normalized words from OSHB with frequency.
   Returns vec of {:word :freq :gv :len}, sorted by freq descending."
  []
  (let [all-words (mapcat oshb/book-words
                          ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
        normalized (map (fn [w] (apply str (norm/letter-stream (:text w)))) all-words)
        freqs (frequencies normalized)]
    (->> freqs
         (map (fn [[w f]]
                {:word w :freq f :gv (gem/word-value w) :len (count w)}))
         (sort-by :freq >)
         vec)))

;; ── Number theory ────────────────────────────────────────────

(def fibs (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))
(def fib-set (set (take 25 fibs)))
(defn fib? [n] (contains? fib-set n))
(defn fib-index [n]
  (first (keep-indexed (fn [i f] (when (= f n) (inc i))) (take 25 fibs))))

(defn triangular [n] (/ (* n (inc n)) 2))
(defn triangular? [n]
  (let [disc (+ 1 (* 8 n))]
    (when (pos? disc)
      (let [root (long (Math/sqrt disc))]
        (and (= disc (* root root))
             (odd? root))))))
(defn triangular-root [n]
  (when (triangular? n)
    (/ (dec (long (Math/sqrt (inc (* 8 n))))) 2)))

(defn perfect-square? [n]
  (when (pos? n)
    (let [r (long (Math/sqrt n))]
      (= n (* r r)))))

(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %)) (range 2 (inc (long (Math/sqrt n)))))))

(defn factorize [n]
  (loop [n n d 2 factors []]
    (cond
      (< n 2) factors
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

;; ── PART 1: The Tables ──────────────────────────────────────

(defn part-1-tables
  "Build and display the core word tables."
  []
  (println "================================================================")
  (println "  PART 1: WORD TABLES")
  (println "================================================================")
  (println)

  (let [t0 (System/currentTimeMillis)
        words (torah-words)
        t1 (System/currentTimeMillis)]
    (println (format "  %,d total tokens → %,d unique words in %.1fs"
                     (reduce + (map :freq words)) (count words) (/ (- t1 t0) 1000.0)))
    (println)

    ;; Summary by length
    (println "  ── BY LENGTH ──")
    (let [by-len (group-by :len words)]
      (doseq [n (sort (keys by-len))]
        (let [ws (get by-len n)
              total-freq (reduce + (map :freq ws))]
          (println (format "  %2d-letter: %,5d words, %,6d tokens" n (count ws) total-freq)))))
    (println)

    ;; Top 50 by frequency
    (println "  ── TOP 100 BY FREQUENCY ──")
    (println)
    (println "  Rank  Word      Freq    GV    Len")
    (println "  ────  ────────  ──────  ────  ───")
    (doseq [[i {:keys [word freq gv len]}] (map-indexed vector (take 100 words))]
      (println (format "  %3d   %-8s  %,5d   %4d  %d"
                       (inc i) word freq gv len)))
    (println)

    ;; Top 50 by gematria
    (println "  ── TOP 50 BY GEMATRIA (highest GV) ──")
    (println)
    (println "  Rank  Word        GV      Freq  Len")
    (println "  ────  ──────────  ──────  ────  ───")
    (doseq [[i {:keys [word gv freq len]}]
            (map-indexed vector (take 50 (sort-by (comp - :gv) words)))]
      (println (format "  %3d   %-10s  %,5d   %3d   %d"
                       (inc i) word gv freq len)))
    (println)

    words))

;; ── PART 2: Preimage ────────────────────────────────────────

(defn part-2-preimage
  "Compute preimage counts for all words that fit in a-fibers."
  [words]
  (println "================================================================")
  (println "  PART 2: PREIMAGE COUNTS")
  (println "================================================================")
  (println)

  ;; Only words of 2-7 letters (a-fiber = 7 positions)
  (let [eligible (filter #(<= 2 (:len %) 7) words)
        word-strs (mapv :word eligible)
        _ (println (format "  Scanning %,d words (2-7 letters) across 43,550 a-fibers..."
                           (count word-strs)))
        t0 (System/currentTimeMillis)
        counts (coords/preimage-indexed word-strs)
        t1 (System/currentTimeMillis)]
    (println (format "  Done in %.1fs" (/ (- t1 t0) 1000.0)))
    (println)

    ;; Merge preimage counts into word records
    (let [enriched (->> eligible
                        (map (fn [w]
                               (assoc w :preimage (get counts (:word w) 0))))
                        (sort-by :preimage >)
                        vec)
          with-hits (filter #(pos? (:preimage %)) enriched)
          no-hits (filter #(zero? (:preimage %)) enriched)]

      (println (format "  Words with preimage hits: %,d" (count with-hits)))
      (println (format "  Words with zero hits: %,d" (count no-hits)))
      (println (format "  Total preimage hits: %,d" (reduce + (map :preimage enriched))))
      (println)

      ;; Top 100 by preimage count (3+ letters only)
      (let [meaningful (filter #(>= (:len %) 3) with-hits)]
        (println "  ── TOP 100 BY PREIMAGE COUNT (3+ letters) ──")
        (println)
        (println "  Rank  Word      Pre   Freq    GV    Len  Factors")
        (println "  ────  ────────  ────  ──────  ────  ───  ────────")
        (doseq [[i {:keys [word preimage freq gv len]}]
                (map-indexed vector (take 100 meaningful))]
          (let [factors (when (> preimage 1)
                          (let [fs (factorize preimage)]
                            (when (> (count fs) 1)
                              (str/join "×" fs))))]
            (println (format "  %3d   %-8s  %4d  %,5d   %4d  %d    %s"
                             (inc i) word preimage freq gv len (or factors "")))))
        (println))

      ;; Distribution of preimage counts
      (println "  ── PREIMAGE COUNT DISTRIBUTION (3+ letter words) ──")
      (println)
      (let [meaningful (filter #(>= (:len %) 3) with-hits)
            by-count (group-by :preimage meaningful)
            sorted-counts (sort (keys by-count))]
        (println "  Count  Words  Example")
        (println "  ─────  ─────  ───────")
        (doseq [c sorted-counts]
          (let [ws (get by-count c)
                examples (str/join " " (map :word (take 3 ws)))]
            (when (<= (count ws) 20)  ;; Only show counts that aren't too crowded
              (println (format "  %5d  %5d  %s" c (count ws) examples))))))
      (println)

      enriched)))

;; ── PART 3: Pattern Detection ───────────────────────────────

(defn part-3-patterns
  "Exhaustive automated pattern detection."
  [enriched]
  (println "================================================================")
  (println "  PART 3: AUTOMATED PATTERN DETECTION")
  (println "================================================================")
  (println)

  (let [with-hits (filter #(and (pos? (:preimage %)) (>= (:len %) 3)) enriched)
        by-preimage (group-by :preimage with-hits)
        all-counts (sort (keys by-preimage))
        gv-index (group-by :gv with-hits)]

    ;; ── Fibonacci counts ──
    (println "  ── FIBONACCI PREIMAGE COUNTS ──")
    (println)
    (let [fib-counts (filter fib? all-counts)]
      (doseq [f fib-counts]
        (let [ws (get by-preimage f)
              gv-sum (reduce + (map :gv ws))
              tri (triangular-root gv-sum)]
          (println (format "  F(%d) = %d: %d word(s), GV sum = %,d%s"
                           (fib-index f) f (count ws) gv-sum
                           (if tri (format " = T(%d)" tri) "")))
          (doseq [{:keys [word gv freq]} (sort-by :gv ws)]
            (println (format "       %s GV=%d freq=%d%s"
                             word gv freq
                             (cond (fib? gv) (format " [GV=F(%d)]" (fib-index gv))
                                   (triangular? gv) (format " [GV=T(%d)]" (triangular-root gv))
                                   :else "")))))))
    (println)

    ;; ── Triangular counts ──
    (println "  ── TRIANGULAR PREIMAGE COUNTS ──")
    (println)
    (let [tri-counts (filter triangular? all-counts)]
      (doseq [t tri-counts]
        (let [ws (get by-preimage t)
              root (triangular-root t)]
          (when (<= (count ws) 10)
            (println (format "  T(%d) = %d: %s"
                             root t
                             (str/join ", " (map #(str (:word %) "(" (:gv %) ")") ws))))))))
    (println)

    ;; ── Perfect square counts ──
    (println "  ── PERFECT SQUARE PREIMAGE COUNTS ──")
    (println)
    (let [sq-counts (filter perfect-square? all-counts)]
      (doseq [s sq-counts]
        (let [ws (get by-preimage s)
              root (long (Math/sqrt s))]
          (when (<= (count ws) 10)
            (println (format "  %d² = %d: %s"
                             root s
                             (str/join ", " (map #(str (:word %) "(" (:gv %) ")") ws))))))))
    (println)

    ;; ── Axis-divisible counts ──
    (println "  ── AXIS-DIVISIBLE PREIMAGE COUNTS ──")
    (println)
    (doseq [[d label] [[7 "completeness"] [13 "love"] [50 "jubilee"] [67 "understanding"]]]
      (let [div-counts (filter #(and (> % 1) (zero? (mod % d))) all-counts)]
        (println (format "  ÷%d (%s): %d counts" d label (count div-counts)))
        (doseq [c (take 10 div-counts)]
          (let [ws (get by-preimage c)]
            (when (<= (count ws) 5)
              (println (format "    %d (=%d×%d): %s"
                               c (/ c d) d
                               (str/join ", " (map #(str (:word %) "(" (:gv %) ")") ws)))))))))
    (println)

    ;; ── Cross-reference web: preimage(A) = GV(B) ──
    (println "  ── CROSS-REFERENCE WEB: preimage(A) = GV(B) ──")
    (println)
    (let [xrefs (for [{:keys [word preimage gv]} with-hits
                      :when (pos? preimage)
                      :let [targets (get gv-index preimage)]
                      target targets
                      :when (not= word (:word target))]
                  {:from word :from-gv gv :count preimage
                   :to (:word target) :to-gv (:gv target)})
          by-count (group-by :count (sort-by :count xrefs))]
      (println (format "  Total cross-references: %,d" (count xrefs)))
      (println (format "  Distinct count values: %d" (count by-count)))
      (println)
      ;; Show first 30 by count value
      (doseq [[cnt refs] (take 30 (sort-by key by-count))]
        (let [from-words (distinct (map :from refs))
              to-words (distinct (map :to refs))]
          (when (and (<= (count from-words) 5) (<= (count to-words) 5))
            (println (format "  preimage=%d: %s → GV of %s"
                             cnt
                             (str/join "," (take 3 from-words))
                             (str/join "," (take 3 to-words))))))))
    (println)

    ;; ── Double-special words ──
    (println "  ── DOUBLE-FIBONACCI WORDS (preimage AND GV both Fibonacci) ──")
    (println)
    (let [doubles (filter (fn [{:keys [preimage gv]}]
                            (and (fib? preimage) (fib? gv)))
                          with-hits)]
      (doseq [{:keys [word preimage gv freq]} (sort-by :preimage doubles)]
        (println (format "  %s: preimage=%d=F(%d), GV=%d=F(%d), freq=%d"
                         word preimage (fib-index preimage) gv (fib-index gv) freq))))
    (println)

    ;; ── Words whose GV = another word's preimage count ──
    (println "  ── WORDS WHERE GV IS AN AXIS NUMBER ──")
    (println)
    (doseq [[target label] [[7 "completeness"] [13 "love"] [26 "YHWH"]
                             [50 "jubilee"] [67 "understanding"]
                             [91 "7×13"] [130 "Sinai"]]]
      (let [ws (get gv-index target)]
        (when (seq ws)
          (println (format "  GV=%d (%s): %s"
                           target label
                           (str/join ", " (map #(format "%s(pre=%d)" (:word %) (:preimage %))
                                               (sort-by (comp - :preimage) ws))))))))
    (println)

    {:by-preimage by-preimage :gv-index gv-index}))

;; ── PART 4: Staircase sums ─────────────────────────────────

(defn part-4-staircase-sums
  "GV sums at each preimage level. Look for patterns."
  [enriched]
  (println "================================================================")
  (println "  PART 4: STAIRCASE — GV SUMS BY PREIMAGE LEVEL")
  (println "================================================================")
  (println)

  (let [with-hits (filter #(and (pos? (:preimage %)) (>= (:len %) 3)) enriched)
        by-preimage (sort-by key (group-by :preimage with-hits))]
    (println "  Level  Words  GV Sum      Notable")
    (println "  ─────  ─────  ──────────  ───────")
    (doseq [[level ws] by-preimage]
      (let [gv-sum (reduce + (map :gv ws))
            n-words (count ws)
            notes (str
                    (when (fib? level) (format "F(%d) " (fib-index level)))
                    (when (triangular? level) (format "T(%d) " (triangular-root level)))
                    (when-let [tr (triangular-root gv-sum)] (format "sum=T(%d) " tr))
                    (when (fib? gv-sum) (format "sum=F(%d) " (fib-index gv-sum)))
                    (when (zero? (mod gv-sum 7)) (format "÷7=%d " (/ gv-sum 7)))
                    (when (zero? (mod gv-sum 13)) (format "÷13=%d " (/ gv-sum 13)))
                    (when (zero? (mod gv-sum 26)) (format "÷26=%d " (/ gv-sum 26)))
                    (when (zero? (mod gv-sum 67)) (format "÷67=%d " (/ gv-sum 67))))]
        (when (<= n-words 20)  ;; Only show manageable levels
          (println (format "  %5d  %5d  %,10d  %s" level n-words gv-sum notes)))))))

;; ── Main ────────────────────────────────────────────────────

(defn -main []
  (println)
  (println "╔══════════════════════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 080: WORD CENSUS                               ║")
  (println "║                                                            ║")
  (println "║  Every word. Frequency. Gematria. Preimage.                ║")
  (println "║  Then: find ALL the patterns.                              ║")
  (println "╚══════════════════════════════════════════════════════════════╝")
  (println)

  (let [words (part-1-tables)
        enriched (part-2-preimage words)]
    (part-3-patterns enriched)
    (part-4-staircase-sums enriched)

    (println)
    (println "  The census is taken.")
    (println "  The data speaks for itself.")
    (println)
    (println "Done.")))
