(ns experiments.fiber.143r-the-women
  "Experiment 143r: The women.

   Eve→YHWH(83, 5x). Leah GV=36=tent. Hagar GV=208=Isaac.
   Miriam finds the sea. Rebekah finds the incense."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn woman-profile
  "Search for a woman's name and report hosts and richest fiber."
  [dims word]
  (let [hits (s/find-word dims word {:max-results 500})
        ns (f/non-surface hits)
        hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))]
    {:word word :ns (count ns) :hosts hosts
     :richest (first (sort-by :torah-word-count > ns))}))

(defn run-all []
  (let [dims [7 50 13 67]
        women [["חוה" "Eve"] ["שרה" "Sarah"] ["רבקה" "Rebekah"]
               ["רחל" "Rachel"] ["לאה" "Leah"] ["מרים" "Miriam"]
               ["הגר" "Hagar"] ["תמר" "Tamar"]]]
    (doseq [[w eng] women]
      (let [{:keys [ns hosts richest]} (woman-profile dims w)
            top-3 (take 3 (sort-by val > hosts))]
        (println (format "\n═══ %s (%s GV=%d): %d ns ═══" w eng (g/word-value w) ns))
        (println (format "  Top: %s" (str/join ", " (map (fn [[k v]] (str k "(" v ")")) top-3))))
        (when (and richest (>= (:torah-word-count richest) 2))
          (f/print-fiber richest))))))

(comment
  (s/build!)
  (run-all))
