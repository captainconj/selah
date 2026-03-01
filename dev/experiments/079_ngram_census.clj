(ns experiments.079-ngram-census
  "Experiment 079: N-gram Census

   Systematic frequency analysis of the Torah letter stream.
   Unigrams, bigrams, trigrams — sorted by frequency and by gematria.

   This is the raw census: what IS there, before we ask what it means."
  (:require [selah.text.oshb :as oshb]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────────

(defn ngram-value
  "Gematria value of an n-gram (vec of chars)."
  [ng]
  (reduce + (map gem/letter-value ng)))

(defn ngram-str
  "Convert n-gram (vec of chars) to string."
  [ng]
  (apply str ng))

(defn ngram-freqs
  "Count all n-grams of size n in the letter stream.
   Returns map of {[char...] count}."
  [letters n]
  (frequencies (partition n 1 letters)))

(defn format-pct
  "Format as percentage with 2 decimal places."
  [count total]
  (format "%.4f%%" (* 100.0 (/ count total))))

(defn rank-table
  "Build a sorted table from ngram frequency map.
   Returns seq of {:rank :ngram :str :count :pct :gv}"
  [freq-map total-ngrams]
  (->> freq-map
       (map (fn [[ng cnt]]
              {:ngram ng
               :str (ngram-str ng)
               :count cnt
               :pct (/ (double cnt) total-ngrams)
               :gv (ngram-value ng)}))
       (sort-by :count >)
       (map-indexed (fn [i m] (assoc m :rank (inc i))))))

;; ── PART 1: Unigrams ────────────────────────────────────────

(defn part-1-unigrams
  [letters]
  (println "================================================================")
  (println "  PART 1: UNIGRAMS — single letter frequencies")
  (println "================================================================")
  (println)

  (let [total (count letters)
        freqs (frequencies letters)
        table (->> freqs
                   (map (fn [[ch cnt]]
                          {:char ch :str (str ch) :count cnt
                           :pct (/ (double cnt) total)
                           :gv (gem/letter-value ch)}))
                   (sort-by :count >)
                   (map-indexed (fn [i m] (assoc m :rank (inc i)))))]

    (println (format "  Total letters: %,d" total))
    (println (format "  Distinct letters: %d" (count freqs)))
    (println)

    ;; By frequency
    (println "  ── BY FREQUENCY ──")
    (println)
    (println "  Rank  Letter  Count      %       GV")
    (println "  ────  ──────  ─────────  ──────  ───")
    (doseq [{:keys [rank str count pct gv]} table]
      (println (format "  %2d    %s       %,8d   %5.2f%%   %3d"
                       rank str count (* 100.0 pct) gv)))
    (println)

    ;; By gematria
    (println "  ── BY GEMATRIA VALUE ──")
    (println)
    (println "  GV   Letter  Count      %       Rank")
    (println "  ───  ──────  ─────────  ──────  ────")
    (doseq [{:keys [rank str count pct gv]} (sort-by :gv table)]
      (println (format "  %3d  %s       %,8d   %5.2f%%   %2d"
                       gv str count (* 100.0 pct) rank)))
    (println)

    ;; Quick stats
    (let [top-7 (take 7 table)
          top-7-sum (reduce + (map :count top-7))
          top-7-pct (* 100.0 (/ (double top-7-sum) total))]
      (println (format "  Top 7 letters account for %,d / %,d = %.1f%%"
                       top-7-sum total top-7-pct))
      (println (format "  Top 7: %s" (str/join " " (map :str top-7)))))
    (println)

    ;; GV-weighted frequency
    (let [gv-total (reduce + (map #(* (:count %) (:gv %)) table))]
      (println (format "  Total gematria sum: %,d" gv-total))
      (println (format "  Mean GV per letter: %.2f" (/ (double gv-total) total))))
    (println)

    {:freqs freqs :table table}))

;; ── PART 2: Bigrams ─────────────────────────────────────────

(defn part-2-bigrams
  [letters]
  (println "================================================================")
  (println "  PART 2: BIGRAMS — consecutive letter pairs")
  (println "================================================================")
  (println)

  (let [freqs (ngram-freqs letters 2)
        total (reduce + (vals freqs))
        table (rank-table freqs total)
        distinct-count (count freqs)]

    (println (format "  Total bigrams: %,d" total))
    (println (format "  Distinct bigrams: %d (of %d possible)" distinct-count (* 27 27)))
    (println)

    ;; Top 50 by frequency
    (println "  ── TOP 50 BY FREQUENCY ──")
    (println)
    (println "  Rank  Bigram  Count     %       GV")
    (println "  ────  ──────  ────────  ──────  ───")
    (doseq [{:keys [rank str count pct gv]} (take 50 table)]
      (println (format "  %3d   %s      %,7d   %5.3f%%  %3d"
                       rank str count (* 100.0 pct) gv)))
    (println)

    ;; Top 50 by gematria (highest GV bigrams that actually appear)
    (println "  ── TOP 50 BY GEMATRIA (highest GV) ──")
    (println)
    (println "  Rank  Bigram  GV    Count     %")
    (println "  ────  ──────  ───   ────────  ──────")
    (let [by-gv (sort-by :gv > table)]
      (doseq [[i {:keys [str gv count pct]}] (map-indexed vector (take 50 by-gv))]
        (println (format "  %3d   %s      %3d   %,7d   %5.3f%%"
                         (inc i) str gv count (* 100.0 pct)))))
    (println)

    ;; Bottom 50 by gematria (lowest GV bigrams)
    (println "  ── BOTTOM 50 BY GEMATRIA (lowest GV) ──")
    (println)
    (println "  Rank  Bigram  GV   Count     %")
    (println "  ────  ──────  ───  ────────  ──────")
    (let [by-gv (sort-by :gv table)]
      (doseq [[i {:keys [str gv count pct]}] (map-indexed vector (take 50 by-gv))]
        (println (format "  %3d   %s      %3d  %,7d   %5.3f%%"
                         (inc i) str gv count (* 100.0 pct)))))
    (println)

    ;; Bigrams whose GV matches significant numbers
    (println "  ── SIGNIFICANT GV BIGRAMS ──")
    (println)
    (let [by-gv-map (group-by :gv table)]
      (doseq [[target label] (sort-by first
                               [[7 "completeness"] [13 "love/one"] [26 "YHWH"]
                                [50 "jubilee"] [67 "understanding"] [53 "garden"]
                                [91 "7×13"] [130 "ladder/Sinai"]
                                [350 "sapphire=7×50"] [469 "slab=7×67"]
                                [611 "Torah"] [620 "covenant(כרת)"]])]
        (when-let [bgs (seq (get by-gv-map target))]
          (println (format "  GV=%3d (%s): %s"
                           target label
                           (str/join ", "
                                     (map #(format "%s(%,d)" (:str %) (:count %))
                                          (sort-by :count > bgs))))))))
    (println)

    {:freqs freqs :table table}))

;; ── PART 3: Trigrams ─────────────────────────────────────────

(defn part-3-trigrams
  [letters]
  (println "================================================================")
  (println "  PART 3: TRIGRAMS — consecutive letter triples")
  (println "================================================================")
  (println)

  (let [freqs (ngram-freqs letters 3)
        total (reduce + (vals freqs))
        table (rank-table freqs total)
        distinct-count (count freqs)]

    (println (format "  Total trigrams: %,d" total))
    (println (format "  Distinct trigrams: %,d (of %,d possible)" distinct-count (* 27 27 27)))
    (println)

    ;; Top 50 by frequency
    (println "  ── TOP 50 BY FREQUENCY ──")
    (println)
    (println "  Rank  Trigram  Count    %       GV")
    (println "  ────  ───────  ───────  ──────  ────")
    (doseq [{:keys [rank str count pct gv]} (take 50 table)]
      (println (format "  %3d   %s       %,6d   %5.3f%%  %3d"
                       rank str count (* 100.0 pct) gv)))
    (println)

    ;; Top 50 by gematria
    (println "  ── TOP 50 BY GEMATRIA (highest GV) ──")
    (println)
    (println "  Rank  Trigram  GV    Count    %")
    (println "  ────  ───────  ────  ───────  ──────")
    (let [by-gv (sort-by :gv > table)]
      (doseq [[i {:keys [str gv count pct]}] (map-indexed vector (take 50 by-gv))]
        (println (format "  %3d   %s       %4d  %,6d   %5.3f%%"
                         (inc i) str gv count (* 100.0 pct)))))
    (println)

    ;; Trigrams that ARE Hebrew words we know
    (println "  ── TRIGRAMS THAT ARE KNOWN WORDS ──")
    (println)
    (let [known-words {"תורה" "Torah" "יהוה" "YHWH" "אהבה" "love" "בינה" "understanding"
                       "אור" "light" "שמר" "guard" "אמת" "truth" "קדש" "holy"
                       "יום" "day" "טוב" "good" "אדם" "man" "מים" "water"
                       "שלם" "peace" "דבר" "word" "זרע" "seed" "רוח" "spirit"
                       "כהן" "priest" "מלך" "king" "גאל" "redeem" "שבת" "sabbath"
                       "כפר" "atone" "נתן" "give" "שמע" "hear" "ידע" "know"
                       "ראה" "see" "הלך" "walk" "ישב" "dwell" "עמד" "stand"
                       "מות" "death" "חטא" "sin" "ברא" "create" "צדק" "righteous"
                       "דרך" "way" "שמן" "oil" "יין" "wine" "לחם" "bread"
                       "אבן" "stone" "ענן" "cloud" "צור" "rock" "כבש" "lamb"
                       "נחש" "serpent" "דעת" "knowledge" "פרי" "fruit" "חרב" "sword"
                       "עשה" "make" "בוא" "come" "כרת" "cut" "סלח" "forgive"
                       "פדה" "ransom" "ירש" "inherit" "באר" "well" "שפך" "pour"
                       "נקם" "avenge" "עלה" "ascend" "טהר" "pure" "נדר" "vow"
                       "שלח" "send" "קרא" "call" "נשא" "lift" "כבד" "glory"
                       "אמן" "faithful" "ברך" "bless" "חלק" "portion"}
          ;; 3-letter words only
          three-letter (filter #(= 3 (count (key %))) known-words)
          ngram-map (into {} (map (fn [{:keys [ngram] :as m}] [(vec ngram) m]) table))]
      (println "  Word     Meaning      Freq     %        GV   Freq Rank")
      (println "  ───────  ───────────  ───────  ───────  ───  ─────────")
      (doseq [[word meaning] (sort-by (fn [[w _]] (- (:count (get ngram-map (vec w) {:count 0})))) three-letter)]
        (when-let [entry (get ngram-map (vec word))]
          (println (format "  %s     %-12s %,6d   %5.3f%%   %3d  %,d"
                           word meaning (:count entry) (* 100.0 (:pct entry))
                           (:gv entry) (:rank entry))))))
    (println)

    ;; Trigrams whose GV matches significant numbers
    (println "  ── SIGNIFICANT GV TRIGRAMS ──")
    (println)
    (let [by-gv-map (group-by :gv table)]
      (doseq [[target label] (sort-by first
                               [[7 "completeness"] [13 "love/one"] [26 "YHWH"]
                                [50 "jubilee"] [67 "understanding"] [53 "garden"]
                                [91 "7×13"] [130 "ladder/Sinai"]
                                [322 "lamb(כבש)"] [351 "T(26)"]
                                [441 "truth=21²"] [611 "Torah"]
                                [620 "cut-covenant(כרת)"]])]
        (when-let [bgs (seq (get by-gv-map target))]
          (let [total-freq (reduce + (map :count bgs))]
            (println (format "  GV=%3d (%s): %d distinct, %,d total"
                             target label (count bgs) total-freq))
            (doseq [bg (take 5 (sort-by :count > bgs))]
              (println (format "            %s (%,d)" (:str bg) (:count bg))))))))
    (println)

    {:freqs freqs :table table}))

;; ── PART 4: Cross-n-gram patterns ───────────────────────────

(defn part-4-patterns
  [uni-table bi-table tri-table letters]
  (println "================================================================")
  (println "  PART 4: CROSS-N-GRAM PATTERNS")
  (println "================================================================")
  (println)

  (let [total (count letters)
        ;; Letter frequency as probability
        p (into {} (map (fn [{:keys [char count]}]
                          [char (/ (double count) total)])
                        uni-table))
        ;; Expected bigram count if letters were independent
        bi-expected (fn [ng]
                      (let [[a b] ng]
                        (* (p a 0) (p b 0) (dec total))))
        ;; Observed/expected ratio for top bigrams
        bi-map (into {} (map (fn [{:keys [ngram count]}] [ngram count]) bi-table))]

    ;; Most over-represented bigrams (observed >> expected)
    (println "  ── MOST OVER-REPRESENTED BIGRAMS (observed/expected) ──")
    (println)
    (println "  Bigram  Observed  Expected  O/E Ratio  GV")
    (println "  ──────  ────────  ────────  ─────────  ───")
    (let [ratios (->> bi-table
                      (map (fn [{:keys [ngram count gv] :as m}]
                             (let [exp (bi-expected ngram)
                                   ratio (if (pos? exp) (/ count exp) 0)]
                               (assoc m :expected exp :ratio ratio))))
                      (filter #(> (:count %) 50))  ;; minimum count threshold
                      (sort-by :ratio >))]
      (doseq [{:keys [str count expected ratio gv]} (take 30 ratios)]
        (println (format "  %s      %,7d   %,7.0f   %6.2f×    %3d"
                         str count expected ratio gv))))
    (println)

    ;; Most under-represented bigrams
    (println "  ── MOST UNDER-REPRESENTED BIGRAMS (observed/expected) ──")
    (println)
    (println "  Bigram  Observed  Expected  O/E Ratio  GV")
    (println "  ──────  ────────  ────────  ─────────  ───")
    (let [ratios (->> bi-table
                      (map (fn [{:keys [ngram count gv] :as m}]
                             (let [exp (bi-expected ngram)
                                   ratio (if (pos? exp) (/ count exp) 0)]
                               (assoc m :expected exp :ratio ratio))))
                      (filter #(> (:expected %) 50))  ;; only where we'd expect to see them
                      (sort-by :ratio))]
      (doseq [{:keys [str count expected ratio gv]} (take 30 ratios)]
        (println (format "  %s      %,7d   %,7.0f   %6.2f×    %3d"
                         str count expected ratio gv))))
    (println)))

;; ── Main ────────────────────────────────────────────────────

(defn -main []
  (println)
  (println "╔══════════════════════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 079: N-GRAM CENSUS                             ║")
  (println "║                                                            ║")
  (println "║  Unigrams, bigrams, trigrams — by frequency and gematria   ║")
  (println "║  The raw census of what is there                           ║")
  (println "╚══════════════════════════════════════════════════════════════╝")
  (println)

  (let [t0 (System/currentTimeMillis)
        letters (oshb/torah-letters)
        t1 (System/currentTimeMillis)]
    (println (format "  Loaded %,d letters in %.1fs" (count letters) (/ (- t1 t0) 1000.0)))
    (println)

    (let [{uni-table :table} (part-1-unigrams letters)
          {bi-table :table} (part-2-bigrams letters)
          {tri-table :table} (part-3-trigrams letters)]

      (part-4-patterns uni-table bi-table tri-table letters)

      (println)
      (println "  ═══════════════════════════════════════════════════════")
      (println "  SUMMARY")
      (println "  ═══════════════════════════════════════════════════════")
      (println)
      (println (format "  %,d letters → %d unigrams, %,d distinct bigrams, %,d distinct trigrams"
                       (count letters) (count (distinct letters))
                       (count (distinct (partition 2 1 letters)))
                       (count (distinct (partition 3 1 letters)))))
      (println)
      (println "  The census is taken. The raw material is known.")
      (println)
      (println "Done."))))
