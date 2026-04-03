(ns experiments.fiber.143f-basin-crossings
  "Experiment 143f: Basin crossings in the space.

   The lamb (כבש) and lie-down (שכב) cross at 73 Torah word positions.
   The crossings are structural: feasts, tabernacle, glory, honor command, heart."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn find-crossings
  "Find Torah word positions where fibers of word1 and word2 both land."
  [dims word1 word2]
  (let [collect (fn [word]
                  (let [hosts (atom {})]
                    (doseq [h (f/non-surface (s/find-word dims word {:max-results 500}))]
                      (doseq [tw (:torah-words h)]
                        (when tw (swap! hosts update [(:start tw) (:word tw)] (fnil inc 0)))))
                    (set (keys @hosts))))]
    (clojure.set/intersection (collect word1) (collect word2))))

(defn print-crossings [crossings]
  (let [idx (s/index)
        verse-at (:verse-at idx)]
    (doseq [[pos word] (sort-by first crossings)]
      (let [v (verse-at pos)]
        (println (format "  %-12s GV=%-4d  %s %d:%d"
                         word (g/word-value word) (:book v) (:ch v) (:vs v)))))))

(defn run-all []
  (println "═══ LAMB ↔ LIE-DOWN CROSSINGS ═══\n")
  (let [crossings (find-crossings [7 50 13 67] "כבש" "שכב")]
    (println (format "  %d crossing points:\n" (count crossings)))
    (print-crossings crossings)))

(comment
  (s/build!)
  (run-all))
