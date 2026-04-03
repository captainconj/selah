(ns experiments.fiber.143q-messiah-prophet-king
  "Experiment 143q: The three offices.

   Messiah finds Yom Kippur and the lamb lying down.
   King finds Melchizedek and Amalek.
   Prophet finds the Passover house."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]))

;; (s/build!)

(defn office-profile [dims word]
  (let [hits (s/find-word dims word {:max-results 200})
        ns (f/non-surface hits)
        hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))]
    {:word word :ns (count ns) :hosts hosts
     :richest (first (sort-by :torah-word-count > ns))}))

(defn run-all []
  (let [dims [7 50 13 67]]
    (doseq [[w eng] [["משיח" "messiah"] ["מלך" "king"] ["נביא" "prophet"] ["צלם" "image"]]]
      (let [{:keys [ns hosts richest]} (office-profile dims w)
            top-5 (take 5 (sort-by val > hosts))]
        (println (format "\n═══ %s (%s GV=%d): %d ns ═══" w eng (g/word-value w) ns))
        (println (format "  Top: %s" (clojure.string/join ", " (map (fn [[k v]] (str k "(" v ")")) top-5))))
        (when richest (f/print-fiber richest))))))

(comment
  (s/build!)
  (run-all))
