(ns experiments.fiber.143m-the-land
  "Experiment 143m: The promised land vocabulary."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (doseq [[w eng] [["נחלה" "inheritance"] ["גבול" "border"] ["חלב" "milk"] ["ירש" "possess"]]]
    (let [hits (s/find-word [7 50 13 67] w {:max-results 500})
          ns (f/non-surface hits)
          rich (first (sort-by :torah-word-count > ns))]
      (println (format "\n%s (%s):" w eng))
      (when rich (f/print-fiber rich)))))
(comment (s/build!) (run-all))
