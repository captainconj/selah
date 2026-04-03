(ns experiments.fiber.143al-the-fall-diagonal
  "Experiment 143al: The fall diagonal [0,+1,-1,0]. Command→ate→garments→wandering."
  (:require [selah.search :as s]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters]} (s/index) idx (s/index)]
    (println "Freedom↑ Love↓ diagonal (skip=804):\n")
    (doseq [i (range 7)]
      (let [pos (* 804 i) tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
        (println (format "  %d: %s in %-14s %s %d:%d" i (nth letters pos) (when tw (:word tw)) (:book v) (:ch v) (:vs v)))))))
(comment (s/build!) (run-all))
