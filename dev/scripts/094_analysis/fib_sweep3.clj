(require '[clojure.edn :as edn]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(def fib-list (vec (take 35 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))))
(def fibs (set fib-list))

(println "Loading...")
(def data (edn/read-string (slurp "data/experiments/094/thummim-sweep.edn")))
(def results (:results data))
(println (str "Loaded " (count results) " words"))

;; Biggest Fibonacci phrase count
(let [pcs (filter fibs (map :phrase-count results))
      max-fib (when (seq pcs) (apply max pcs))]
  (println)
  (println (format "Largest Fibonacci phrase count: %d (F%d)"
                   max-fib (inc (.indexOf fib-list (int max-fib)))))
  (doseq [w (filter #(= max-fib (:phrase-count %)) results)]
    (println (format "  %s  GV=%d  len=%d  illum=%d  %s"
                     (:word w) (:gv w) (:len w) (:illumination-count w)
                     (or (:meaning w) "")))))

;; All Fibonacci phrase counts with counts of words
(println)
(println "=== FIBONACCI PHRASE COUNTS (descending) ===")
(let [by-pc (group-by :phrase-count results)
      fib-pcs (sort > (filter fibs (keys by-pc)))]
  (doseq [f fib-pcs]
    (let [ws (get by-pc f)
          fidx (inc (.indexOf fib-list (int f)))
          with-m (filter :meaning ws)]
      (println (format "  F(%d)=%d: %d words" fidx f (count ws)))
      (when (and (seq with-m) (<= (count ws) 20))
        (doseq [w (sort-by :word with-m)]
          (println (format "    %s (%s) GV=%d illum=%d"
                           (:word w) (:meaning w) (:gv w) (:illumination-count w))))))))

;; Biggest Fibonacci illumination count
(println)
(let [ics (filter fibs (keep :illumination-count results))
      max-fib-il (when (seq ics) (apply max ics))]
  (println (format "Largest Fibonacci illumination count: %d (F%d)"
                   max-fib-il (inc (.indexOf fib-list (int max-fib-il)))))
  (doseq [w (filter #(= max-fib-il (:illumination-count %)) results)]
    (println (format "  %s  GV=%d  phrases=%d  len=%d  %s"
                     (:word w) (:gv w) (:phrase-count w) (:len w)
                     (or (:meaning w) "")))))

;; Double Fibonacci
(println)
(println "=== DOUBLE FIBONACCI: phrase + illumination both Fibonacci ===")
(let [doubles (filter #(and (fibs (:phrase-count %))
                            (fibs (or (:illumination-count %) 0))
                            (:illuminable? %)
                            (> (:phrase-count %) 2))
                      results)]
  (println (str "Count: " (count doubles)))
  (doseq [w (sort-by (comp - :phrase-count) doubles)]
    (println (format "  %s  phrases=%d(F%d)  illum=%d(F%d)  GV=%d  %s"
                     (:word w) (:phrase-count w)
                     (inc (.indexOf fib-list (int (:phrase-count w))))
                     (:illumination-count w)
                     (inc (.indexOf fib-list (int (:illumination-count w))))
                     (:gv w) (or (:meaning w) "")))))

;; Triple: phrase + illum + GV
(println)
(println "=== TRIPLE FIBONACCI ===")
(let [triples (filter #(and (fibs (:phrase-count %))
                            (fibs (or (:illumination-count %) 0))
                            (fibs (:gv %))
                            (:illuminable? %))
                      results)]
  (println (str "Count: " (count triples)))
  (doseq [w triples]
    (println (format "  %s  phrases=%d  illum=%d  GV=%d  %s"
                     (:word w) (:phrase-count w) (:illumination-count w)
                     (:gv w) (or (:meaning w) "")))))
