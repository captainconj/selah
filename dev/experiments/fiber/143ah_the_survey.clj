(ns experiments.fiber.143ah-the-survey
  "Experiment 143ah: The survey. All 3-letter words × 40 directions."
  (:require [selah.search :as s] [selah.gematria :as g] [selah.dict :as d]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters letter-idx n]} (s/index)
        view (s/make-view [7 50 13 67])
        skips (distinct (map #(Math/abs (long (s/direction->skip (:strides view) %))) (s/direction-vectors 4)))
        words (vec (filter #(= 3 (count %)) (d/torah-words)))
        results (atom [])]
    (doseq [w words]
      (let [total (reduce + (for [s skips] (+ (count (or (s/search-at-skip letters letter-idx w s n) []))
                                               (count (or (s/search-at-skip letters letter-idx w (- s) n) [])))))]
        (when (pos? total) (swap! results conj {:word w :hits total :gv (g/word-value w)}))))
    (println "Top 20 by total hits:\n")
    (doseq [{:keys [word hits gv]} (take 20 (sort-by :hits > @results))]
      (println (format "  %s GV=%d %d hits" word gv hits)))))
(comment (s/build!) (run-all))
