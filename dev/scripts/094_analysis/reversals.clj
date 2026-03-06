(require '[clojure.edn :as edn])

(def dists (edn/read-string (slurp "data/experiments/094/distributions.edn")))
(def pd (:phrase-count-dist dists))

(defn prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else (not (some #(zero? (mod n %)) (range 3 (inc (int (Math/sqrt n))) 2)))))

;; Get the odd sequence: n=1,3,5,7,...,49
(let [odd-seq (for [n (range 1 100 2)
                    :let [w (get pd n 0)]
                    :when (pos? w)]
                {:n n :words w :prime? (prime? n)})
      pairs (partition 2 1 odd-seq)
      reversals (filter (fn [[a b]] (> (:words b) (:words a))) pairs)]
  
  (println "=== ODD SEQUENCE: REVERSALS (where count INCREASES) ===")
  (println)
  (doseq [[a b] reversals]
    (println (format "  %2d→%2d: %d→%d (+%.1f%%)  %s→%s"
                     (:n a) (:n b) (:words a) (:words b)
                     (* 100.0 (/ (- (double (:words b)) (:words a)) (:words a)))
                     (if (:prime? a) "P" "C") (if (:prime? b) "P" "C"))))
  
  (println)
  (println (str "Total reversals: " (count reversals)))
  (println (str "  Target is prime: " (count (filter #(:prime? (second %)) reversals))))
  (println (str "  Target is composite: " (count (remove #(:prime? (second %)) reversals))))
  
  ;; Also: compare smoothed envelope
  ;; For each odd n, compute ratio to geometric mean of neighbors
  (println)
  (println "=== RATIO TO NEIGHBOR MEAN (>1 = above trend) ===")
  (let [triples (partition 3 1 odd-seq)]
    (doseq [[a b c] triples]
      (let [geo-mean (Math/sqrt (* (double (:words a)) (:words c)))
            ratio (/ (:words b) geo-mean)]
        (when (> ratio 1.05)
          (println (format "  n=%2d: %d vs neighbors(%d,%d) geo-mean=%.0f ratio=%.3f  %s"
                           (:n b) (:words b) (:words a) (:words c)
                           geo-mean ratio
                           (if (:prime? b) "PRIME" "composite"))))))))
