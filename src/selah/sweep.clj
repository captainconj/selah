(ns selah.sweep
  "Thummim sweep analysis — Fibonacci structure in phrase counts.

   Loads the 094 sweep data lazily. Computes Fibonacci analysis on first call.
   All results are cached."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; ── Fibonacci ────────────────────────────────────────────────

(def ^:private fib-seq
  "First 35 Fibonacci numbers (up to 9,227,465)."
  (vec (take 35 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))))

(def ^:private fib-set (set fib-seq))

(defn fib?
  "Is n a Fibonacci number?"
  [n]
  (contains? fib-set n))

(defn fib-index
  "Which Fibonacci is n? Returns F(k) index (1-based), or nil."
  [n]
  (let [i (.indexOf fib-seq (int n))]
    (when (>= i 0) (inc i))))

;; ── Sweep data ───────────────────────────────────────────────

(def ^:private sweep-data
  "Lazy-loaded sweep results from 094."
  (delay
    (let [f (io/file "data/experiments/094/thummim-sweep.edn")]
      (when (.exists f)
        (:results (edn/read-string (slurp f)))))))

(defn sweep-results
  "All 12,826 word results from the Thummim sweep. Lazy."
  []
  @sweep-data)

;; ── Fibonacci analysis ───────────────────────────────────────

(def ^:private analysis-cache
  (delay
    (when-let [results (sweep-results)]
      (let [illuminable (filter :illuminable? results)

            ;; Fibonacci phrase counts
            fib-phrase (filter #(fib? (:phrase-count %)) illuminable)
            by-phrase-count (sort-by first >
                                     (group-by :phrase-count fib-phrase))
            max-fib-phrase (when (seq fib-phrase)
                             (apply max (map :phrase-count fib-phrase)))

            ;; Fibonacci illumination counts
            fib-illum (filter #(fib? (:illumination-count %)) illuminable)
            max-fib-illum (when (seq fib-illum)
                            (apply max (map :illumination-count fib-illum)))

            ;; Double: phrase + illumination both Fibonacci
            doubles (filter #(and (fib? (:phrase-count %))
                                  (fib? (:illumination-count %))
                                  (pos? (:phrase-count %)))
                            illuminable)

            ;; Triple: phrase + illumination + GV all Fibonacci
            triples (filter #(and (fib? (:phrase-count %))
                                  (fib? (:illumination-count %))
                                  (fib? (:gv %)))
                            illuminable)

            ;; Staircase: which F(n) values appear as phrase counts
            present-fibs (sort (distinct (map :phrase-count fib-phrase)))
            staircase (mapv (fn [f]
                              (let [ws (filter #(= f (:phrase-count %)) fib-phrase)]
                                {:fib f
                                 :fib-index (fib-index f)
                                 :word-count (count ws)
                                 :words (mapv #(select-keys % [:word :gv :meaning
                                                               :illumination-count
                                                               :phrase-count :len])
                                              (sort-by :word ws))}))
                            present-fibs)

            ;; Gaps: Fibonacci numbers NOT appearing as phrase counts
            max-f (or max-fib-phrase 1)
            all-fibs-to-max (filter #(<= % max-f) fib-seq)
            gaps (remove (set present-fibs) all-fibs-to-max)]

        {:total-words (count results)
         :illuminable (count illuminable)
         :fib-phrase-words (count fib-phrase)
         :fib-illum-words (count fib-illum)
         :double-fib-words (count doubles)
         :triple-fib-words (count triples)
         :max-fib-phrase {:value max-fib-phrase
                          :fib-index (when max-fib-phrase (fib-index max-fib-phrase))
                          :words (when max-fib-phrase
                                   (mapv #(select-keys % [:word :gv :meaning :len
                                                          :illumination-count])
                                         (filter #(= max-fib-phrase (:phrase-count %))
                                                 illuminable)))}
         :max-fib-illum {:value max-fib-illum
                         :fib-index (when max-fib-illum (fib-index max-fib-illum))}
         :staircase staircase
         :gaps (mapv (fn [f] {:fib f :fib-index (fib-index f)}) gaps)
         :doubles (mapv #(select-keys % [:word :gv :meaning :phrase-count
                                         :illumination-count :len])
                        (sort-by (comp - :phrase-count) doubles))
         :triples (mapv #(select-keys % [:word :gv :meaning :phrase-count
                                         :illumination-count :len])
                        triples)}))))

(defn fibonacci-analysis
  "Complete Fibonacci analysis of the Thummim sweep. Cached."
  []
  @analysis-cache)
