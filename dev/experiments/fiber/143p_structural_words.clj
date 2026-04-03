(ns experiments.fiber.143p-structural-words
  "Experiment 143p: Structural words. Choose→altar. Guard→breath. Holy→Melchizedek."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (doseq [[w eng] [["בחר" "choose"] ["שמר" "guard"] ["מצבה" "pillar"] ["את" "aleph-tav"]
                    ["כפר" "atone"] ["קדש" "holy"] ["ברך" "bless"] ["צוה" "command"] ["עבד" "serve"]]]
    (let [hits (s/find-word [7 50 13 67] w {:max-results 500})
          ns (f/non-surface hits)
          hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
          rich (first (sort-by :torah-word-count > ns))]
      (println (format "\n%s (%s GV=%d): %d ns, top: %s" w eng (g/word-value w) (count ns)
                       (clojure.string/join ", " (map (fn [[k v]] (str k "(" v ")")) (take 5 (sort-by val > hosts))))))
      (when (and rich (>= (:torah-word-count rich) 2)) (f/print-fiber rich)))))
(comment (s/build!) (run-all))
