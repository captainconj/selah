(ns experiments.fiber.143h-the-peg
  "Experiment 143h: The peg (יתד GV=414=love command). Fall→building."
  (:require [selah.search :as s] [selah.fiber :as f]))
;; (s/build!)
(defn run-all []
  (let [hits (s/find-word [7 50 13 67] "יתד") ns (f/non-surface hits)]
    (println (format "%d non-surface hits\n" (count ns)))
    (println "Richest fibers at skip=66:")
    (doseq [h (take 10 (sort-by :torah-word-count > (filter #(= (:skip %) 66) ns)))]
      (f/print-fiber h) (println))))
(comment (s/build!) (run-all))
