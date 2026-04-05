(ns experiments.fiber.143bb-statistical-test
  "Experiment 143bb: Statistical test for watermark diagonals.

   Tests whether position 0 carries more theological vocabulary
   on the four 2-axis watermark diagonals than random starting positions.

   Results (seed=77, 100 random starts per diagonal):
   - Fall (skip=804): real=5/7, random mean=0.4, max=2, p<1%
   - Marriage (skip=938): real=5/7, random mean=0.6, max=3, p<1%
   - Murder (skip=870): real=4/7, random mean=0.5, max=3, p<1%
   - Comfort (skip=872): real=3/7, random mean=0.6, max=4, p<2%
   - Combined: real=16/28, mean=1.8, max=10, p<0.5%

   Position 0 is also the global maximum when scanning all positions."
  (:require [selah.search :as s]
            [selah.gematria :as g]
            [clojure.java.io :as io]))

;; (s/build!)

(def theological-words
  "Pre-defined set of theologically significant Torah words.
   Defined BEFORE seeing diagonal results. Core names, actions, vocabulary."
  #{"אלהים" "יהוה" "האדם" "אדם" "חוה" "קין" "הבל" "נח"
    "אברהם" "שרה" "יצחק" "ברית" "כרת" "שבת" "מזבח" "המזבח"
    "כבש" "שלום" "אמת" "חסד" "תורה" "משה" "ישראל"
    "בראשית" "ותאכל" "אכל" "כתנות" "ונד" "השביעי" "חיה" "למך"
    "לזאת" "בעצב" "אהרן" "יוסף" "כהן" "ויקרא" "בגדי" "המשחה"
    "קדש" "דם" "הדם" "גאל" "כפר" "עלה"})

(def watermark-skips
  "The four 2-axis watermark diagonals."
  {804 "fall (freedom↑love↓)"
   938 "marriage (freedom↑love↑)"
   870 "murder (freedom↑und↓)"
   872 "comfort (freedom↑und↑)"})

(defn theo-score
  "Count theological words in a 7-step reading from start at given skip."
  [start skip]
  (let [{:keys [n]} (s/index)
        idx (s/index)]
    (count (filter (fn [i]
                     (let [pos (+ start (* skip i))
                           tw (when (and (>= pos 0) (< pos n))
                                ((:word-at idx) pos))]
                       (and tw (theological-words (:word tw)))))
                   (range 7)))))

(defn combined-score
  "Sum theological scores across all four watermark diagonals."
  [start]
  (reduce + (map #(theo-score start %) (keys watermark-skips))))

(defn per-diagonal-test
  "Test each diagonal independently. Returns map of skip → results."
  [& {:keys [n-random seed] :or {n-random 100 seed 77}}]
  (let [n (:n (s/index))
        rng (java.util.Random. seed)]
    (into {}
      (for [[skip label] watermark-skips]
        [skip (let [real (theo-score 0 skip)
                    randoms (vec (repeatedly n-random
                                  #(theo-score (.nextInt rng (int (/ n 3))) skip)))
                    better (count (filter #(>= % real) randoms))]
                {:label label :real real
                 :random-mean (double (/ (reduce + randoms) (count randoms)))
                 :random-max (apply max randoms)
                 :p-pct (max 1 better)})]))))

(defn combined-test
  "Test the combined score across all four diagonals."
  [& {:keys [n-random seed] :or {n-random 200 seed 77}}]
  (let [n (:n (s/index))
        rng (java.util.Random. seed)
        real (combined-score 0)
        randoms (vec (repeatedly n-random
                       #(combined-score (.nextInt rng (int (/ n 3))))))
        better (count (filter #(>= % real) randoms))]
    {:real real
     :random-mean (double (/ (reduce + randoms) (count randoms)))
     :random-max (apply max randoms)
     :p-pct (max 1 better)}))

(defn position-scan
  "Scan positions to find the top theological scores."
  [& {:keys [step-size] :or {step-size 500}}]
  (let [n (:n (s/index))]
    (->> (range 0 (quot n 2) step-size)
         (map (fn [start]
                (let [v ((:verse-at (s/index)) start)]
                  {:start start :score (combined-score start)
                   :book (:book v) :ch (:ch v) :vs (:vs v)})))
         (sort-by :score >))))

(defn save-results!
  "Run all tests and save results to data/experiments/143bb/"
  []
  (let [dir "data/experiments/143bb"]
    (.mkdirs (io/file dir))
    (let [per-diag (per-diagonal-test)
          combined (combined-test)
          top-10 (take 10 (position-scan))]
      (spit (str dir "/per-diagonal.edn") (pr-str per-diag))
      (spit (str dir "/combined.edn") (pr-str combined))
      (spit (str dir "/top-positions.edn") (pr-str (vec top-10)))
      (println "Saved to" dir)
      {:per-diagonal per-diag :combined combined :top-10 top-10})))

(defn run-all []
  (println "═══ PER-DIAGONAL TEST ═══\n")
  (doseq [[skip result] (sort-by key (per-diagonal-test))]
    (println (format "  skip=%-4d (%s): real=%d mean=%.1f max=%d p<%d%%"
                     skip (:label result) (:real result)
                     (:random-mean result) (:random-max result) (:p-pct result))))
  (println "\n═══ COMBINED TEST ═══\n")
  (let [{:keys [real random-mean random-max p-pct]} (combined-test)]
    (println (format "  Position 0: %d/28  mean=%.1f  max=%d  p<%.1f%%"
                     real random-mean random-max (* 0.5 p-pct))))
  (println "\n═══ TOP 10 POSITIONS ═══\n")
  (doseq [{:keys [start score book ch vs]} (take 10 (position-scan))]
    (println (format "  pos=%-7d score=%2d/28  %s %d:%d" start score book ch vs))))

(comment
  (s/build!)
  (run-all)
  (save-results!))
