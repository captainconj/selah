(require '[clojure.edn :as edn]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

;; Fibonacci set up to large range
(def fib-list
  (vec (take 40 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))))
(def fibs (set fib-list))

(println "Fibonacci up to:" (last fib-list))

;; Load the full phrase results — 11,552 words with all their phrases
(println "Loading phrase results...")
(def results (edn/read-string (slurp "data/experiments/094/phrase-results.edn")))
(println (str "Loaded " (count results) " words"))

;; For each word: check if phrase-count is Fibonacci
(println)
(println "=== WORDS WITH FIBONACCI PHRASE COUNTS ===")
(let [fib-words (filter #(fibs (:phrase-count %)) results)
      by-count (group-by :phrase-count fib-words)]
  (println (str "Total words with Fibonacci phrase count: " (count fib-words)
               " of " (count results) " (" 
               (format "%.1f%%" (* 100.0 (/ (count fib-words) (count results)))) ")"))
  (println)
  ;; Show each Fibonacci level
  (doseq [f (sort (keys by-count))]
    (let [ws (get by-count f)
          fidx (.indexOf fib-list (int f))
          with-meaning (filter #(dict/translate (:word %)) ws)]
      (println (format "  F(%d) = %d: %d words" (inc fidx) f (count ws)))
      (when (seq with-meaning)
        (doseq [w (take 10 (sort-by :word with-meaning))]
          (println (format "    %s (%s) GV=%d"
                           (:word w) (dict/translate (:word w)) (:gv w))))
        (when (> (count with-meaning) 10)
          (println (format "    ...and %d more with meanings" (- (count with-meaning) 10))))))))

;; Now the big question: what are the LARGEST Fibonacci phrase counts?
(println)
(println "=== LARGEST FIBONACCI PHRASE COUNTS ===")
(let [all-counts (set (map :phrase-count results))
      fib-counts (sort > (filter fibs all-counts))]
  (println (str "Fibonacci phrase counts that exist: " (vec (take 20 (sort fib-counts)))))
  (println)
  (println "Largest Fibonacci phrase counts with words:")
  (doseq [f (take 5 fib-counts)]
    (let [ws (filter #(= f (:phrase-count %)) results)
          fidx (.indexOf fib-list (int f))]
      (println (format "  F(%d) = %d: %d words" (inc fidx) f (count ws)))
      (doseq [w ws]
        (let [m (dict/translate (:word w))]
          (println (format "    %s  GV=%d  len=%d  %s"
                           (:word w) (:gv w) (:len w) (or m ""))))))))

;; Also: check GV of phrases — do any unique phrases have Fibonacci GV?
;; Actually, check word GV that are Fibonacci
(println)
(println "=== WORDS WITH FIBONACCI GV ===")
(let [fib-gv-words (filter #(fibs (:gv %)) results)]
  (println (str "Words with Fibonacci GV: " (count fib-gv-words)))
  (doseq [f (sort (set (map :gv fib-gv-words)))]
    (let [ws (filter #(= f (:gv %)) fib-gv-words)
          with-m (filter #(dict/translate (:word %)) ws)]
      (when (seq with-m)
        (println (format "  GV=%d (F): %s"
                         f (str/join ", " (map #(str (:word %) "(" (dict/translate (:word %)) ")")
                                               with-m))))))))

;; The intersection: Fibonacci GV AND Fibonacci phrase count
(println)  
(println "=== DOUBLE FIBONACCI: GV and phrase count both Fibonacci ===")
(let [doubles (filter #(and (fibs (:gv %)) (fibs (:phrase-count %))) results)]
  (println (str "Count: " (count doubles)))
  (doseq [w (sort-by :gv doubles)]
    (let [m (dict/translate (:word w))
          fi-gv (.indexOf fib-list (int (:gv w)))
          fi-pc (.indexOf fib-list (int (:phrase-count w)))]
      (println (format "  %s  GV=%d(F%d)  phrases=%d(F%d)  %s"
                       (:word w) (:gv w) (inc fi-gv)
                       (:phrase-count w) (inc fi-pc)
                       (or m ""))))))
