(ns experiments.fiber.143i-tabernacle
  "Experiment 143i: Tabernacle fibers. Each building material traces a story."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (doseq [[w eng] [["יתד" "peg"] ["כרב" "cherub"] ["אדן" "socket"] ["קרש" "board"]
                    ["אהל" "tent"] ["עמד" "pillar"] ["ארן" "ark"]]]
    (let [hits (s/find-word [7 50 13 67] w {:max-results 500})
          ns (f/non-surface hits)
          hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))]
      (println (format "\n%s (%s GV=%d): %d ns" w eng (g/word-value w) (count ns)))
      (doseq [[word cnt] (take 5 (sort-by val > hosts))]
        (println (format "  %s(%d)" word cnt))))))
(comment (s/build!) (run-all))
