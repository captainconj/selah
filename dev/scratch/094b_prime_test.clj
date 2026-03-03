(require '[clojure.edn :as edn])

(def dists (edn/read-string (slurp "data/experiments/094/distributions.edn")))
(def phrase-dist (:phrase-count-dist dists))

(println "Sample keys:" (take 10 (sort (keys phrase-dist))))
(println "Total bins:" (count phrase-dist))
(println "Total words:" (reduce + (vals phrase-dist)))

(defn prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else (not (some #(zero? (mod n %))
                     (range 3 (inc (int (Math/sqrt n))) 2)))))

(let [max-n (apply max (keys phrase-dist))
      _ (println "Max phrase count:" max-n)
      
      ;; For each n with data, classify
      entries (for [[n words] phrase-dist]
               {:n n :words words :prime? (prime? n) :odd? (odd? n)})
      
      ;; Total words at prime vs composite phrase counts 
      total-prime (reduce + (map :words (filter :prime? entries)))
      total-composite (reduce + (map :words (remove :prime? entries)))
      total-all (+ total-prime total-composite)
      
      ;; Among odd only
      odd-entries (filter :odd? entries)
      odd-prime (filter :prime? odd-entries)
      odd-composite (remove :prime? odd-entries)
      total-odd-prime (reduce + (map :words odd-prime))
      total-odd-composite (reduce + (map :words odd-composite))]
  
  (println)
  (println "=== PRIME vs COMPOSITE PHRASE COUNTS ===")
  (println (str "Words at PRIME phrase count:     " total-prime " (" (format "%.1f%%" (* 100.0 (/ total-prime total-all))) ")"))
  (println (str "Words at COMPOSITE phrase count: " total-composite " (" (format "%.1f%%" (* 100.0 (/ total-composite total-all))) ")"))
  (println (str "Prime/Composite ratio: " (format "%.2f" (double (/ total-prime (max 1 total-composite))))))
  (println)
  
  (println "=== THE KEY TEST: Among ODD counts, prime vs composite ===")
  (println (str "  Odd-prime words:     " total-odd-prime " across " (count odd-prime) " bins, mean=" (format "%.1f" (/ (double total-odd-prime) (count odd-prime))) "/bin"))
  (println (str "  Odd-composite words: " total-odd-composite " across " (count odd-composite) " bins, mean=" (format "%.1f" (/ (double total-odd-composite) (count odd-composite))) "/bin"))
  (println (str "  Odd prime/composite ratio: " (format "%.2f" (double (/ total-odd-prime (max 1 total-odd-composite))))))
  (println)
  
  ;; Detailed comparison: for matched ranges
  (println "=== ODD COUNTS BY DECADE: PRIME vs COMPOSITE MEAN PER BIN ===")
  (doseq [[lo hi] [[1 10] [11 20] [21 30] [31 50] [51 100] [101 200] [201 500] [501 1000]]]
    (let [in-range (filter #(<= lo (:n %) hi) entries)
          odd-in (filter :odd? in-range)
          op (filter :prime? odd-in)
          oc (remove :prime? odd-in)
          sp (reduce + (map :words op))
          sc (reduce + (map :words oc))
          np (count op)
          nc (count oc)]
      (when (or (pos? np) (pos? nc))
        (println (format "  n=%d-%d: P-bins=%d sum=%d mean=%.1f | C-bins=%d sum=%d mean=%.1f | ratio=%.2f"
                         lo hi np sp (if (pos? np) (/ (double sp) np) 0.0)
                         nc sc (if (pos? nc) (/ (double sc) nc) 0.0)
                         (if (pos? sc) (/ (double sp) (max 1.0 (* sc (/ (double np) (max 1 nc))))) 0.0))))))
  
  (println)
  (println "=== DETAILED n=1..50 ===")
  (doseq [n (range 1 51)]
    (let [w (get phrase-dist n 0)]
      (when (pos? w)
        (println (format "  %3d: %5d  %s %s"
                         n w
                         (if (prime? n) "PRIME" "     ")
                         (if (odd? n) "ODD" "even")))))))
