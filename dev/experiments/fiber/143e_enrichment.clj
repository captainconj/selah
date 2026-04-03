(ns experiments.fiber.143e-enrichment
  "Experiment 143e: Host enrichment analysis.

   Which Torah words host fibers MORE than their letter coverage predicts?
   The common words (את, אשר, יהוה) are background.
   The real magnets: pillar (GV=137, 8.1x), wisdom (21.5x), menorah (10.8x),
   guard (6.1x). על is the most AVOIDED (0.19x) — except by burnt offering (8.5x)."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn enrichment-analysis
  "Compute enrichment for all hosts across given search words."
  [dims search-words]
  (let [idx (s/index)
        n (double (:n idx))
        cov (reduce (fn [m w] (update m (:word w) (fnil + 0) (- (:end w) (:start w))))
                    {} (:words idx))
        hosts (atom {}) total (atom 0)]
    (doseq [w search-words]
      (doseq [h (f/non-surface (s/find-word dims w {:max-results 500}))]
        (doseq [tw (:torah-words h)]
          (when tw (swap! total inc) (swap! hosts update (:word tw) (fnil inc 0))))))
    (let [t (double @total)]
      (->> @hosts
           (map (fn [[word obs]]
                  (let [c (double (get cov word 1))
                        exp (* (/ c n) t)]
                    {:word word :observed obs :expected exp
                     :ratio (/ (double obs) exp) :gv (g/word-value word)})))
           (filter #(>= (:observed %) 3))
           (sort-by :ratio >)))))

(defn print-enrichment [results & {:keys [limit] :or {limit 30}}]
  (println "\nOver-represented (>2x):")
  (doseq [{:keys [word observed expected ratio gv]}
           (take limit (filter #(> (:ratio %) 2.0) results))]
    (println (format "  %-14s obs=%-3d exp=%.1f ratio=%.1fx GV=%d"
                     word observed expected ratio gv)))
  (println "\nUnder-represented (common words, <0.8x):")
  (doseq [{:keys [word observed expected ratio gv]}
           (take 10 (filter #(and (< (:ratio %) 0.8) (> (:expected %) 20))
                            (sort-by :ratio results)))]
    (println (format "  %-14s obs=%-3d exp=%.1f ratio=%.2fx GV=%d"
                     word observed expected ratio gv))))

(defn run-all []
  (let [results (enrichment-analysis [7 50 13 67]
                                     ["תורה" "כבש" "שלום" "אמת" "חיים" "ברית"])]
    (print-enrichment results)))

(comment
  (s/build!)
  (run-all))
