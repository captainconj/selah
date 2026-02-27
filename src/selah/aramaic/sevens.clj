(ns selah.aramaic.sevens
  "Heptadic structure analysis for Aramaic/Syriac text."
  (:require [clojure.string :as str]
            [selah.aramaic.normalize :as an]
            [selah.aramaic.parse :as ap]))

;; ── Feature extraction ─────────────────────────────────────────────

(defn word-count [words] (count words))

(defn letter-count [words]
  (reduce + (map #(count (an/normalize (:text %))) words)))

(defn matres-count
  "Count of matres lectionis (ܐ ܘ ܝ — quasi-vowels)."
  [words]
  (reduce + (map #(an/count-matres (:text %)) words)))

(defn pure-consonant-count
  "Count of pure consonants (non-matres)."
  [words]
  (reduce + (map #(an/count-pure-consonants (:text %)) words)))

(defn total-gematria [words]
  (reduce + (map #(an/gematria (:text %)) words)))

(defn unique-words [words]
  (count (distinct (map #(an/normalize (:text %)) words))))

(defn unique-word-letters [words]
  (let [vocab (distinct (map #(an/normalize (:text %)) words))]
    (reduce + (map count vocab))))

(defn unique-word-gematria [words]
  (let [vocab (distinct (map :text words))]
    (reduce + (map an/gematria vocab))))

(defn words-beginning-with-alaph
  "Words starting with ܐ (Alaph)."
  [words]
  (count (filter #(= \ܐ (first (an/normalize (:text %)))) words)))

(defn verse-count [words]
  (count (distinct (map (juxt :chapter :verse) words))))

;; ── Analysis ──────────────────────────────────────────────────────

(defn analyze
  "Run heptadic feature extractions on Aramaic word sequence."
  [words]
  (let [features [["Words"                  (word-count words)]
                  ["Letters"                (letter-count words)]
                  ["Matres lectionis"       (matres-count words)]
                  ["Pure consonants"        (pure-consonant-count words)]
                  ["Gematria (total)"       (total-gematria words)]
                  ["Unique word forms"      (unique-words words)]
                  ["Unique word letters"    (unique-word-letters words)]
                  ["Unique word gematria"   (unique-word-gematria words)]
                  ["Begin with Alaph"       (words-beginning-with-alaph words)]
                  ["Verses"                 (verse-count words)]]]
    (mapv (fn [[feature value]]
            {:feature feature
             :value   value
             :mod7    (mod value 7)
             :div7?   (zero? (mod value 7))})
          features)))

(defn print-analysis [analysis label]
  (println)
  (println (format "=== Heptadic Analysis (Peshitta): %s ===" label))
  (println (format "%-28s %8s %6s %s" "Feature" "Value" "mod 7" "÷7?"))
  (println (apply str (repeat 52 "-")))
  (let [div-count (count (filter :div7? analysis))]
    (doseq [{:keys [feature value mod7 div7?]} analysis]
      (println (format "%-28s %8d %6d   %s" feature value mod7
                       (if div7? "✓" ""))))
    (println (apply str (repeat 52 "-")))
    (println (format "Divisible by 7: %d / %d features" div-count (count analysis)))))

;; ── Shuffled controls ─────────────────────────────────────────────

(defn randomize-text
  "Shuffle letters within each word, preserving word boundaries."
  [words]
  (mapv (fn [w]
          (let [letters (an/normalize (:text w))
                shuffled (apply str (shuffle (vec letters)))]
            (assoc w :text shuffled)))
        words))

(defn shuffled-controls
  "Run n shuffled trials, count how many produce >= k sevens."
  [words n]
  (let [actual  (count (filter :div7? (analyze words)))
        trials  (repeatedly n #(count (filter :div7? (analyze (randomize-text words)))))
        mean-s  (/ (double (reduce + trials)) n)
        gte     (count (filter #(>= % actual) trials))]
    {:actual        actual
     :mean-shuffled mean-s
     :max-shuffled  (apply max trials)
     :p-gte         (/ gte (double n))
     :trials        n}))

(comment
  (def matt (ap/load-matthew))

  ;; Genealogy
  (let [words (ap/verses matt 1 1 1 17)]
    (print-analysis (analyze words) "Matt 1:1-17"))

  ;; Just verse 1
  (let [words (ap/verses matt 1 1 1 1)]
    (print-analysis (analyze words) "Matt 1:1"))

  ;; Controls
  (let [words (vec (ap/verses matt 1 1 1 1))]
    (println (shuffled-controls words 1000)))
  )
