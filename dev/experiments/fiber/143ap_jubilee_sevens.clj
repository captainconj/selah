(ns experiments.fiber.143ap-jubilee-sevens
  "Experiment 143ap: The jubilee in each seventh. Sold→altar→redeemed."
  (:require [selah.search :as s] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters n]} (s/index) idx (s/index)]
    (println "Jubilee center (b=25) in each seventh:\n")
    (doseq [a (range 7)]
      (let [pos (+ (* 43550 a) (* 871 25))
            tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
        (println (format "  a=%d: %s in %-14s %s %d:%d" a (nth letters pos) (when tw (:word tw)) (:book v) (:ch v) (:vs v)))))))
(comment (s/build!) (run-all))
