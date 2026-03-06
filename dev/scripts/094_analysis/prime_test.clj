(require '[clojure.edn :as edn]
         '[clojure.java.io :as io])

;; Load distributions
(def dists (edn/read-string (slurp "data/experiments/094/distributions.edn")))
(def phrase-dist (:phrase-count-distribution dists))

;; Prime test
(defn prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else (not (some #(zero? (mod n %))
                     (range 3 (inc (int (Math/sqrt n))) 2)))))

;; Get counts for phrase-count values 1 through 100
(let [max-n 100
      counts (for [n (range 1 (inc max-n))]
               {:n n
                :words (get phrase-dist n 0)
                :prime? (prime? n)
                :odd? (odd? n)})
      
      ;; Split by prime/composite AND odd/even
      odd-prime (filter #(and (:odd? %) (:prime? %)) counts)
      odd-composite (filter #(and (:odd? %) (not (:prime? %))) counts)
      even-prime (filter #(and (not (:odd? %)) (:prime? %)) counts)  ;; just n=2
      even-composite (filter #(and (not (:odd? %)) (not (:prime? %))) counts)
      
      ;; Total words in each category
      sum-words #(reduce + (map :words %))
      
      total-prime (sum-words (filter :prime? counts))
      total-composite (sum-words (remove :prime? counts))
      total-odd-prime (sum-words odd-prime)
      total-odd-composite (sum-words odd-composite)
      total-even-prime (sum-words even-prime)  ;; just count=2
      total-even-composite (sum-words even-composite)]
  
  (println "=== PRIME vs COMPOSITE PHRASE COUNTS (n=1..100) ===")
  (println)
  (println (str "Total words with PRIME phrase count:     " total-prime))
  (println (str "Total words with COMPOSITE phrase count: " total-composite))
  (println (str "Ratio: " (format "%.2f" (double (/ total-prime (max 1 total-composite))))))
  (println)
  (println (str "Among ODD counts:"))
  (println (str "  Prime:     " total-odd-prime " (across " (count odd-prime) " bins)"))
  (println (str "  Composite: " total-odd-composite " (across " (count odd-composite) " bins)"))
  (println (str "  Ratio: " (format "%.2f" (double (/ total-odd-prime (max 1 total-odd-composite))))))
  (println (str "  Mean/bin prime: " (format "%.1f" (double (/ total-odd-prime (count odd-prime))))))
  (println (str "  Mean/bin composite: " (format "%.1f" (double (/ total-odd-composite (count odd-composite))))))
  (println)
  (println (str "Among EVEN counts:"))
  (println (str "  Prime (just n=2): " total-even-prime))
  (println (str "  Composite: " total-even-composite " (across " (count even-composite) " bins)"))
  (println)
  
  ;; THE KEY TEST: for each odd n, compare prime vs composite
  ;; Group by decade to smooth
  (println "=== ODD COUNTS: PRIME vs COMPOSITE BY RANGE ===")
  (doseq [[lo hi] [[1 10] [11 20] [21 30] [31 40] [41 50] [51 100]]]
    (let [in-range (filter #(<= lo (:n %) hi) counts)
          odd-in (filter :odd? in-range)
          op (filter :prime? odd-in)
          oc (remove :prime? odd-in)]
      (println (format "  n=%d-%d: prime-bins=%d sum=%d mean=%.1f | composite-bins=%d sum=%d mean=%.1f"
                       lo hi
                       (count op) (sum-words op) (double (/ (sum-words op) (max 1 (count op))))
                       (count oc) (sum-words oc) (double (/ (sum-words oc) (max 1 (count oc))))))))
  
  (println)
  (println "=== DETAILED: n=1..30 ===")
  (doseq [{:keys [n words prime? odd?]} (take 30 counts)]
    (println (format "  %2d: %5d  %s %s"
                     n words
                     (if prime? "PRIME" "     ")
                     (if odd? "ODD" "even")))))
