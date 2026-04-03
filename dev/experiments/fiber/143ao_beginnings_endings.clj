(ns experiments.fiber.143ao-beginnings-endings
  "Experiment 143ao: Beginnings and endings. Every diagonal starts at the house."
  (:require [selah.search :as s] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters n]} (s/index) view (s/make-view [7 50 13 67]) strides (:strides view)]
    (println "Corner pairs (first+last letter of each diagonal):\n")
    (doseq [d (s/direction-vectors 4)]
      (let [skip (Math/abs (long (s/direction->skip strides d)))
            steps (quot n skip) last-pos (* skip (dec steps))]
        (when (> steps 1)
          (let [pair (str (nth letters 0) (nth letters last-pos))
                gv (+ (g/letter-value (nth letters 0)) (g/letter-value (nth letters last-pos)))]
            (println (format "  %s skip=%-6d %s GV=%d" d skip pair gv))))))))
(comment (s/build!) (run-all))
