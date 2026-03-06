(require '[selah.dna :as dna] :reload)
(require '[selah.oracle :as o])
(require '[selah.gematria :as g])
(require '[selah.dict :as dict])
(require '[clojure.java.io :as io])

(def p53 (dna/get-protein "p53"))
(def result (dna/play (:sequence p53) {:format :protein}))
(def hebrew (:hebrew result))

;; Window size 3 — trigrams through the oracle
(def hits (atom []))
(let [n (count hebrew)]
  (doseq [i (range 0 (- n 2))]
    (let [w (subs hebrew i (+ i 3))
          fwd (o/forward (seq w) :torah)
          known (:known-words fwd)]
      (when (seq known)
        (swap! hits conj {:position i
                          :letters w
                          :gv (g/word-value w)
                          :top-5 (vec (take 5 (map (fn [k]
                                                      {:word (:word k)
                                                       :meaning (:meaning k)
                                                       :reading-count (:reading-count k)})
                                                    known)))})))))

(def top-words
  (->> @hits
       (map (fn [h] (first (:top-5 h))))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
               :count (count entries)}))
       (sort-by :count >)
       vec))

;; Write EDN
(io/make-parents "data/dna/p53-oracle.edn")
(spit "data/dna/p53-oracle.edn"
      (pr-str {:name "p53"
               :accession "P04637"
               :description "Cellular tumor antigen p53 — Guardian of the Genome"
               :residues (count (:sequence p53))
               :hebrew hebrew
               :gv (:gv result)
               :letter-frequencies (into (sorted-map-by (fn [a b] (compare [(get (frequencies hebrew) b) b]
                                                                            [(get (frequencies hebrew) a) a])))
                                         (frequencies hebrew))
               :window-size 3
               :windows-with-readings (count @hits)
               :total-windows (- (count hebrew) 2)
               :top-words (vec (take 50 top-words))
               :all-windows (vec @hits)}))

;; Write human-readable report
(spit "data/dna/p53-report.txt"
      (with-out-str
        (println "=== p53 — GUARDIAN OF THE GENOME ===")
        (println "Cellular tumor antigen p53 (TP53)")
        (println (str (count (:sequence p53)) " residues → " (count hebrew) " Hebrew letters"))
        (println)
        (println (str "Hebrew: " hebrew))
        (println (str "GV: " (:gv result)))
        (println)
        (println "Letter frequencies:")
        (doseq [[ch cnt] (sort-by val > (frequencies hebrew))]
          (println (format "  %s → %-4s = %3d (%.1f%%)"
                           ch (name (get dna/letter->aa ch :?))
                           cnt (* 100.0 (/ (double cnt) (count hebrew))))))
        (println)
        (println (str "=== ORACLE READINGS (window=3) ==="))
        (println (str "Windows with readings: " (count @hits) "/" (- (count hebrew) 2)))
        (println)
        (doseq [{:keys [position letters gv top-5]} @hits]
          (let [top (first top-5)]
            (println (format "  [%3d] %s (gv=%d) → %s %s (×%d)"
                             position letters gv
                             (:word top) (or (:meaning top) "")
                             (:reading-count top)))))
        (println)
        (println "=== MOST FREQUENT ORACLE WORDS (top word per window) ===")
        (doseq [{:keys [word meaning count]} (take 40 top-words)]
          (println (format "  %-8s %-30s ×%d" word (or meaning "") count)))))

(println (str "Wrote data/dna/p53-oracle.edn (" (count @hits) " windows)"))
(println (str "Wrote data/dna/p53-report.txt"))
(println)

;; Print highlights
(println "=== p53 HIGHLIGHTS ===")
(println (str (count (:sequence p53)) " residues → " (count hebrew) " Hebrew letters"))
(println (str "GV: " (:gv result)))
(println)
(println "Top 30 oracle words:")
(doseq [{:keys [word meaning count]} (take 30 top-words)]
  (println (format "  %-8s %-30s ×%d" word (or meaning "") count)))
