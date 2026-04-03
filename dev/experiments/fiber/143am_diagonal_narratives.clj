(ns experiments.fiber.143am-diagonal-narratives
  "Experiment 143am: All diagonal narratives. Each direction tells a different story."
  (:require [selah.search :as s] [clojure.string :as str]))
;; (s/build!)
(defn run-all []
  (let [{:keys [letters n]} (s/index) idx (s/index)
        view (s/make-view [7 50 13 67]) strides (:strides view)
        diags (filter #(> (count (filter (complement zero?) %)) 1) (s/direction-vectors 4))]
    (doseq [d (sort-by #(s/direction->skip strides %) diags)]
      (let [skip (s/direction->skip strides d) abs-skip (Math/abs (long skip))]
        (when (and (pos? abs-skip) (<= abs-skip 50000))
          (let [steps (min 7 (quot n abs-skip))
                words (remove nil? (map (fn [i] (when-let [tw ((:word-at idx) (* abs-skip i))] (:word tw))) (range steps)))]
            (println (format "  %s skip=%-6d: %s" d skip (str/join " → " words)))))))))
(comment (s/build!) (run-all))
