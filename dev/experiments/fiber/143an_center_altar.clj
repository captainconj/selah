(ns experiments.fiber.143an-center-altar
  "Experiment 143an: The center is the altar. Position 152425 = המזבח."
  (:require [selah.search :as s] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (let [idx (s/index) {:keys [letters]} idx pos 152425
        tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
    (println (format "Center position %d: %s in %s %s %d:%d GV=%d"
                     pos (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v) (g/word-value (:word tw))))
    (println "\nSurrounding:")
    (doseq [p (range 152420 152431)]
      (let [t ((:word-at idx) p)] (println (format "  %d: %s in %s" p (nth letters p) (when t (:word t))))))))
(comment (s/build!) (run-all))
