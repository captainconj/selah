(ns experiments.fiber.143w-axis-vocabularies
  "Experiment 143w: Axis vocabularies.

   Jubilee carries: righteousness, grace, garden, Isaac, Joseph, wings, strength.
   Love carries: silver, breach (Perez), wrath, flood.
   Completeness carries: vine, 'at the end.'"
  (:require [selah.search :as s]
            [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.string :as str]))

;; (s/build!)

(defn axis-exclusive-words
  "Find words that appear on axis skip1 but not on skip2."
  [skip1 skip2]
  (let [{:keys [letters letter-idx n]} (s/index)
        words (vec (filter #(= 3 (count %)) (d/torah-words)))
        find (fn [w s] (or (seq (s/search-at-skip letters letter-idx w s n))
                           (seq (s/search-at-skip letters letter-idx w (- s) n))))]
    (filter #(and (find % skip1) (not (find % skip2))) words)))

(defn run-all []
  (let [axes {871 "jubilee" 67 "love" 43550 "completeness"}]
    (doseq [[s1 l1] axes
            [s2 l2] axes
            :when (not= s1 s2)]
      (let [exclusive (axis-exclusive-words s1 s2)]
        (when (seq exclusive)
          (println (format "\n%s-only (not on %s): %d words" l1 l2 (count exclusive)))
          (doseq [w (sort exclusive)]
            (println (format "  %s  GV=%d" w (g/word-value w)))))))))

(comment
  (s/build!)
  (run-all))
