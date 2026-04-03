(ns experiments.fiber.143ai-rarest-words
  "Experiment 143ai: Rarest words. Wrath in 6 dirs (love-only)."
  (:require [selah.search :as s] [selah.gematria :as g] [selah.dict :as d]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters letter-idx n]} (s/index)
        skips (distinct (map #(Math/abs (long (s/direction->skip (:strides (s/make-view [7 50 13 67])) %))) (s/direction-vectors 4)))
        words (vec (filter #(= 3 (count %)) (d/torah-words)))]
    (let [results (for [w words]
                    [w (count (filter #(or (seq (s/search-at-skip letters letter-idx w % n))
                                           (seq (s/search-at-skip letters letter-idx w (- %) n))) skips))])]
      (println "Rarest words (< 30 directions):\n")
      (doseq [[w dirs] (sort-by second (filter #(< (second %) 30) results))]
        (println (format "  %s GV=%d %d dirs" w (g/word-value w) dirs))))))
(comment (s/build!) (run-all))
