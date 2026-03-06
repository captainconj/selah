(require '[clojure.edn :as edn])
(def dists (edn/read-string (slurp "data/experiments/094/distributions.edn")))
(println "Keys:" (keys dists))
(println)
(let [pd (:phrase-count-distribution dists)]
  (println "Type:" (type pd))
  (println "Count:" (count pd))
  (println "First 5:" (take 5 (sort-by key pd)))
  (println "Key types:" (set (map (comp type key) (take 5 pd)))))
