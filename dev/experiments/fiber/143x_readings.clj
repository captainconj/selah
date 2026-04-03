(ns experiments.fiber.143x-readings
  "Experiment 143x: Readings. Manasseh→Joseph. Israel sentence. Levi→Genesis 1:1."
  (:require [selah.search :as s] [selah.fiber :as f]))
;; (s/build!)
(defn run-all []
  (println "Manasseh → Joseph:")
  (let [hits (s/find-word [7 50 13 67] "מנשה")]
    (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))
  (println "\nLevi → Genesis 1:")
  (let [hits (s/find-word [7 50 13 67] "לוי")]
    (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))
  (println "\nIsrael sentence:")
  (let [hits (s/find-word [7 50 13 67] "ישראל")]
    (f/print-fiber (first (filter #(= (:skip %) -870) (f/non-surface hits))))))
(comment (s/build!) (run-all))
