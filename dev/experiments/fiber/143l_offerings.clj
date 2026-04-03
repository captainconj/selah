(ns experiments.fiber.143l-offerings
  "Experiment 143l: The offerings. Each has its own axis affinity."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn axis-affinity [word]
  (let [hits (s/find-word [7 50 13 67] word {:max-results 500}) ns (f/non-surface hits)]
    (println (format "\n%s: %d ns" word (count ns)))
    (doseq [ax [0 1 2 3]]
      (let [const (count (filter #(contains? (set (:constant-axes %)) ax) ns))
            vary (count (filter #(contains? (set (:varying-axes %)) ax) ns))]
        (println (format "  axis %d: const=%d vary=%d" ax const vary))))))
(defn run-all []
  (doseq [w ["עלה" "כבש" "אשם" "זבח" "סלח" "גאל"]] (axis-affinity w)))
(comment (s/build!) (run-all))
