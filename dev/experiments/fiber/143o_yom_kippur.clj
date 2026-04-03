(ns experiments.fiber.143o-yom-kippur
  "Experiment 143o: Lamb and peace meet at Yom Kippur.

   Parallel fibers at the same skip converge at Leviticus 16.
   Lamb passes through לכפר (to atone). Peace starts at Exodus 3:14
   ('I AM sent me')."
  (:require [selah.search :as s]
            [selah.fiber :as f]))

;; (s/build!)

(defn find-shared-verse-fibers
  "Find fibers of word1 and word2 at the same skip that share a verse."
  [dims word1 word2]
  (let [h1 (group-by :skip (f/non-surface (s/find-word dims word1 {:max-results 500})))
        h2 (group-by :skip (f/non-surface (s/find-word dims word2 {:max-results 500})))
        shared-skips (clojure.set/intersection (set (keys h1)) (set (keys h2)))]
    (for [skip shared-skips
          a (get h1 skip) b (get h2 skip)
          :let [av (set (remove nil? (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs a))))
                bv (set (remove nil? (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs b))))
                shared (clojure.set/intersection av bv)]
          :when (seq shared)]
      {:skip skip :w1-fiber a :w2-fiber b :shared-verses shared})))

(defn run-all []
  (println "═══ LAMB AND PEACE PARALLEL FIBERS ═══\n")
  (let [results (find-shared-verse-fibers [7 50 13 67] "כבש" "שלום")]
    (println (format "  %d fiber pairs share a verse\n" (count results)))
    (doseq [{:keys [skip w1-fiber w2-fiber shared-verses]} results]
      (println (format "  Skip %d — shared verse: %s" skip (first shared-verses)))
      (println "  LAMB:")
      (f/print-fiber w1-fiber)
      (println "  PEACE:")
      (f/print-fiber w2-fiber)
      (println))))

(comment
  (s/build!)
  (run-all))
