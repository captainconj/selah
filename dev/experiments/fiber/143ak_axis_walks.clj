(ns experiments.fiber.143ak-axis-walks
  "Experiment 143ak: Walking all four axes. Love in creation. Jubilee in Genesis."
  (:require [selah.search :as s] [selah.gematria :as g]))
;; (s/build!)
(defn walk-axis [skip label dim]
  (println (format "\n%s axis (%d stations):" label dim))
  (let [{:keys [letters]} (s/index) idx (s/index)]
    (doseq [i (range dim)]
      (let [pos (* skip i) tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
        (println (format "  %2d: %s in %-12s %s %d:%d" i (nth letters pos) (when tw (:word tw)) (:book v) (:ch v) (:vs v)))))))
(defn run-all []
  (walk-axis 67 "love" 13) (walk-axis 871 "jubilee" 50)
  (walk-axis 43550 "completeness" 7) (walk-axis 1 "understanding" 67))
(comment (s/build!) (run-all))
