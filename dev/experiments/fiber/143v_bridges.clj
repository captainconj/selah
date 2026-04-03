(ns experiments.fiber.143v-bridges
  "Experiment 143v: Bridges. Hub words, self-intersection, direction coverage."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g] [selah.dict :as d]))
;; (s/build!)
(defn self-intersection [dims word]
  (let [hits (s/find-word dims word {:max-results 500}) ns (f/non-surface hits)
        self (count (filter #(some (fn [tw] (and tw (= (:word tw) word))) (:torah-words %)) ns))]
    {:word word :self self :total (count ns) :rate (if (pos? (count ns)) (double (/ self (count ns))) 0.0)}))
(defn run-all []
  (println "Self-intersection rates:")
  (doseq [w ["יהוה" "אנכי" "אחד" "קדש" "כבש" "ברך" "ברית"]]
    (let [{:keys [self total rate]} (self-intersection [7 50 13 67] w)]
      (println (format "  %s: %d/%d = %.1f%%" w self total (* 100 rate))))))
(comment (s/build!) (run-all))
