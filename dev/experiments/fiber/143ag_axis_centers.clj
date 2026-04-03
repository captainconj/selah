(ns experiments.fiber.143ag-axis-centers
  "Experiment 143ag: Axis centers. Completeness→your God. Jubilee→50 righteous."
  (:require [selah.search :as s] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (let [idx (s/index) {:keys [letters]} idx]
    (doseq [[skip label center] [[43550 "completeness" 3] [871 "jubilee" 25]
                                   [67 "love" 6] [1 "understanding" 33]]]
      (let [pos (* skip center) v ((:verse-at idx) pos) tw ((:word-at idx) pos)]
        (println (format "%s center (=%d): %s in %s %s %d:%d"
                         label center (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v)))))))
(comment (s/build!) (run-all))
