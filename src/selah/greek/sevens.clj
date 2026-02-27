(ns selah.greek.sevens
  "Heptadic structure analysis — testing Panin's claims about
   features divisible by 7 in Matthew 1."
  (:require [clojure.string :as str]
            [selah.greek.normalize :as gn]
            [selah.greek.parse :as gp]))

;; ── Feature extraction ─────────────────────────────────────────────

(defn word-count
  "Number of words in the passage."
  [words]
  (count words))

(defn letter-count
  "Total Greek letters in the passage."
  [words]
  (reduce + (map #(count (gn/normalize (:text %))) words)))

(defn vowel-count
  "Total vowels in the passage."
  [words]
  (reduce + (map #(gn/count-vowels (:text %)) words)))

(defn consonant-count
  "Total consonants in the passage."
  [words]
  (reduce + (map #(gn/count-consonants (:text %)) words)))

(defn total-isopsephy
  "Sum of isopsephy values of all words."
  [words]
  (reduce + (map #(gn/isopsephy (:text %)) words)))

(defn unique-words
  "Number of distinct word forms (normalized, lowercased)."
  [words]
  (count (distinct (map #(gn/normalize (:text %)) words))))

(defn unique-word-letters
  "Total letters in the vocabulary (each unique word counted once)."
  [words]
  (let [vocab (distinct (map #(gn/normalize (:text %)) words))]
    (reduce + (map count vocab))))

(defn unique-word-isopsephy
  "Sum of isopsephy values of unique word forms."
  [words]
  (let [vocab (distinct (map :text words))]
    (reduce + (map gn/isopsephy vocab))))

;; ── Word-type features (require morphological data) ────────────────

(defn words-by-pos
  "Filter words by part-of-speech prefix."
  [words pos-prefix]
  (filter #(and (:pos %)
                (str/starts-with? (:pos %) pos-prefix))
          words))

(defn noun-count
  "Number of nouns (including proper names)."
  [words]
  (count (words-by-pos words "N")))

(defn verb-count [words] (count (words-by-pos words "V")))

;; Build a reference set of proper names from Tischendorf (which has PRI tags).
;; Match other variants by normalized word form.
(def ^:private known-names
  "Normalized forms of all PRI-tagged names in Tischendorf Matthew."
  (delay
    (let [tisch (gp/load-matthew :tischendorf)]
      (->> tisch
           (filter #(and (:morph %) (str/includes? (:morph %) "PRI")))
           (map #(gn/normalize (:text %)))
           set))))

(defn- proper-name?
  "A word is a proper name if:
   1. Its morph contains PRI (Robinson system), or
   2. Its normalized form matches a known name from Tischendorf's PRI tags."
  [word-map]
  (or (and (:morph word-map) (str/includes? (:morph word-map) "PRI"))
      (contains? @known-names (gn/normalize (:text word-map)))))

(defn proper-name-count
  "Number of proper names."
  [words]
  (count (filter proper-name? words)))

;; ── Begins-with / ends-with features ───────────────────────────────

(defn words-beginning-with-vowel
  "Count of words whose first letter is a vowel."
  [words]
  (count (filter (fn [w]
                   (let [letters (gn/normalize (:text w))]
                     (and (seq letters) (gn/vowel? (first letters)))))
                 words)))

(defn words-beginning-with-consonant
  "Count of words whose first letter is a consonant."
  [words]
  (count (filter (fn [w]
                   (let [letters (gn/normalize (:text w))]
                     (and (seq letters) (gn/consonant? (first letters)))))
                 words)))

;; ── Passage structure ──────────────────────────────────────────────

(defn verse-count
  "Number of distinct verses in the passage."
  [words]
  (count (distinct (map (juxt :chapter :verse) words))))

;; ── The full analysis ──────────────────────────────────────────────

(defn analyze
  "Run all heptadic feature extractions on a word sequence.
   Returns a map of {:feature name :value N :mod7 (mod N 7) :div7? bool}."
  [words]
  (let [has-morph? (some :pos words)
        features   (cond-> [["Words"                    (word-count words)]
                            ["Letters"                  (letter-count words)]
                            ["Vowels"                   (vowel-count words)]
                            ["Consonants"               (consonant-count words)]
                            ["Isopsephy (total)"        (total-isopsephy words)]
                            ["Unique word forms"        (unique-words words)]
                            ["Unique word letters"      (unique-word-letters words)]
                            ["Unique word isopsephy"    (unique-word-isopsephy words)]
                            ["Begin with vowel"         (words-beginning-with-vowel words)]
                            ["Begin with consonant"     (words-beginning-with-consonant words)]
                            ["Verses"                   (verse-count words)]]
                     has-morph?
                     (into [["Nouns"                    (noun-count words)]
                            ["Verbs"                    (verb-count words)]
                            ["Proper names"             (proper-name-count words)]]))]
    (mapv (fn [[feature value]]
            {:feature feature
             :value   value
             :mod7    (mod value 7)
             :div7?   (zero? (mod value 7))})
          features)))

(defn print-analysis
  "Print a formatted heptadic analysis."
  [analysis variant-name]
  (println)
  (println (format "=== Heptadic Analysis: %s ===" variant-name))
  (println (format "%-28s %8s %6s %s" "Feature" "Value" "mod 7" "÷7?"))
  (println (apply str (repeat 52 "-")))
  (let [div-count (count (filter :div7? analysis))]
    (doseq [{:keys [feature value mod7 div7?]} analysis]
      (println (format "%-28s %8d %6d   %s" feature value mod7
                       (if div7? "✓" ""))))
    (println (apply str (repeat 52 "-")))
    (println (format "Divisible by 7: %d / %d features" div-count (count analysis)))))

(defn compare-variants
  "Run heptadic analysis on Matt 1:1-17 across all variants with morphology."
  []
  (let [variants [:sblgnt :tischendorf :westcott-hort :textus-receptus :kjtr-morphology]]
    (doseq [v variants]
      (let [words    (gp/verses (gp/load-matthew v) 1 1 1 17)
            analysis (analyze words)]
        (print-analysis analysis (name v))))
    ;; Also do text-only variants
    (doseq [v [:byzantine :kjtr :scrivener]]
      (let [words    (gp/verses (gp/load-matthew v) 1 1 1 17)
            analysis (analyze words)]
        (print-analysis analysis (str (name v) " (text only)"))))))

;; ── Statistical controls ───────────────────────────────────────────

(defn shuffle-words
  "Shuffle the word order while preserving each word's internal structure.
   This changes word-count-derived features but preserves letter counts."
  [words]
  (vec (shuffle words)))

(defn randomize-text
  "Generate a random Greek text with same letter frequencies as original.
   Preserves word boundaries but shuffles letters within each word."
  [words]
  (mapv (fn [w]
          (let [letters (gn/normalize (:text w))
                shuffled (apply str (shuffle (vec letters)))]
            (assoc w :text shuffled)))
        words))

(defn expected-sevens
  "For N features, each with P(div7) = 1/7, expected count = N/7.
   Returns {:expected :actual :p-value} using binomial approximation."
  [analysis]
  (let [n       (count analysis)
        k       (count (filter :div7? analysis))
        p       (/ 1.0 7)
        expected (* n p)
        ;; Binomial P(X >= k) approximation using normal CDF
        ;; z = (k - np) / sqrt(np(1-p))
        sigma   (Math/sqrt (* n p (- 1 p)))
        z       (if (pos? sigma) (/ (- k expected 0.5) sigma) 0.0)
        ;; P(X >= k) ≈ 1 - Φ(z) using simple approximation
        p-value (if (<= z 0) 1.0
                    (/ 1.0 (+ 1.0 (* 0.3275911 z))))]
    {:features n
     :divisible-by-7 k
     :expected expected
     :z-score z
     :notable? (>= k (* 2 expected))}))

(defn shuffled-controls
  "Run n trials: shuffle the words, re-analyze, count sevens.
   Returns {:actual K :mean-shuffled avg :p-gte fraction}."
  [words n]
  (let [actual   (count (filter :div7? (analyze words)))
        trials   (repeatedly n #(count (filter :div7? (analyze (randomize-text words)))))
        mean-shuf (/ (double (reduce + trials)) n)
        gte      (count (filter #(>= % actual) trials))]
    {:actual       actual
     :mean-shuffled mean-shuf
     :max-shuffled  (apply max trials)
     :p-gte        (/ gte (double n))
     :trials       n}))

(comment
  ;; Run the full comparison
  (compare-variants)

  ;; Single variant quick check
  (let [words (gp/verses (gp/load-matthew :sblgnt) 1 1 1 17)]
    (print-analysis (analyze words) "SBLGNT"))

  ;; Shuffled controls for Matt 1:1
  (let [words (gp/verses (gp/load-matthew :tischendorf) 1 1 1 1)]
    (println (shuffled-controls words 1000)))
  )
