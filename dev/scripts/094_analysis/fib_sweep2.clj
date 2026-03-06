(require '[clojure.edn :as edn]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(def fib-list (vec (take 35 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))))
(def fibs (set fib-list))

;; Load the slim sweep (no phrases, just counts)
(println "Loading thummim-sweep.edn...")
(def results (edn/read-string (slurp "data/experiments/094/thummim-sweep.edn")))
(println (str "Loaded " (count results) " words"))
(println (str "Sample: " (select-keys (first results) [:word :gv :phrase-count :illumination-count :len])))

;; Words with Fibonacci phrase counts
(println)
(println "=== LARGEST FIBONACCI PHRASE COUNTS ===")
(let [all-counts (sort > (distinct (map :phrase-count results)))
      fib-counts (sort > (filter fibs all-counts))]
  (println (str "Fibonacci phrase counts found: " (vec (sort fib-counts))))
  (println)
  ;; Show the biggest ones with their words
  (doseq [f (take 10 fib-counts)]
    (let [ws (filter #(= f (:phrase-count %)) results)
          fidx (.indexOf fib-list (int f))]
      (println (format "\n  F(%d) = %d: %d words" (inc fidx) f (count ws)))
      (doseq [w (take 5 ws)]
        (println (format "    %s  GV=%d  len=%d  illum=%d  %s"
                         (:word w) (:gv w) (:len w)
                         (:illumination-count w)
                         (or (dict/translate (:word w)) "")))))))

;; Fibonacci illumination counts
(println)
(println "=== LARGEST FIBONACCI ILLUMINATION COUNTS ===")
(let [all-illum (sort > (distinct (map :illumination-count (filter :illuminable? results))))
      fib-illum (sort > (filter fibs all-illum))]
  (println (str "Fibonacci illumination counts found: " (vec (take 20 (sort fib-illum)))))
  (doseq [f (take 5 fib-illum)]
    (let [ws (filter #(= f (:illumination-count %)) results)
          fidx (.indexOf fib-list (int f))]
      (println (format "\n  F(%d) = %d: %d words" (inc fidx) f (count ws)))
      (doseq [w (take 5 ws)]
        (println (format "    %s  GV=%d  phrases=%d  %s"
                         (:word w) (:gv w) (:phrase-count w)
                         (or (dict/translate (:word w)) "")))))))

;; Double Fibonacci: both phrase-count AND illumination-count are Fibonacci
(println)
(println "=== DOUBLE FIBONACCI: phrase-count AND illumination-count ===")
(let [doubles (filter #(and (fibs (:phrase-count %))
                            (fibs (:illumination-count %))
                            (:illuminable? %)
                            (> (:phrase-count %) 1))
                      results)]
  (println (str "Count: " (count doubles)))
  (doseq [w (sort-by (comp - :phrase-count) (take 30 doubles))]
    (let [m (dict/translate (:word w))]
      (println (format "  %s  phrases=%d  illum=%d  GV=%d  len=%d  %s"
                       (:word w) (:phrase-count w) (:illumination-count w)
                       (:gv w) (:len w) (or m ""))))))

;; Triple Fibonacci: phrase-count AND illumination-count AND GV
(println)
(println "=== TRIPLE FIBONACCI: phrase-count AND illumination-count AND GV ===")
(let [triples (filter #(and (fibs (:phrase-count %))
                            (fibs (:illumination-count %))
                            (fibs (:gv %))
                            (:illuminable? %)
                            (> (:phrase-count %) 1))
                      results)]
  (println (str "Count: " (count triples)))
  (doseq [w triples]
    (println (format "  %s  phrases=%d  illum=%d  GV=%d  %s"
                     (:word w) (:phrase-count w) (:illumination-count w)
                     (:gv w) (or (dict/translate (:word w)) "")))))

;; What's the biggest Fibonacci that appears as a phrase count?
(println)
(println "=== THE BIGGEST FIBONACCI ===")
(let [max-fib-pc (apply max (filter fibs (map :phrase-count results)))
      max-fib-il (apply max (filter fibs (map #(or (:illumination-count %) 0) results)))]
  (println (format "  Largest Fibonacci phrase count: %d (F%d)"
                   max-fib-pc (inc (.indexOf fib-list (int max-fib-pc)))))
  (println (format "  Largest Fibonacci illumination count: %d (F%d)"
                   max-fib-il (inc (.indexOf fib-list (int max-fib-il)))))
  ;; Who has the max fib phrase count?
  (println)
  (println "  Words with largest Fibonacci phrase count:")
  (doseq [w (filter #(= max-fib-pc (:phrase-count %)) results)]
    (println (format "    %s  GV=%d  illum=%d  %s"
                     (:word w) (:gv w) (:illumination-count w)
                     (or (dict/translate (:word w)) "")))))
