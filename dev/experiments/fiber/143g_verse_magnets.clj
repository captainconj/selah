(ns experiments.fiber.143g-verse-magnets
  "Experiment 143g: Verse magnets. Which verses host the most search words."
  (:require [selah.search :as s] [selah.fiber :as f]))
;; (s/build!)
(defn run-all []
  (let [dims [7 50 13 67]
        words ["תורה" "כבש" "שלום" "אמת" "חיים" "חסד" "ברית"]
        verse-hits (atom {})]
    (doseq [w words]
      (doseq [h (f/non-surface (s/find-word dims w {:max-results 500}))]
        (doseq [v (:verse-refs h)]
          (when v (let [k (str (:book v) " " (:ch v) ":" (:vs v))]
                    (swap! verse-hits update k (fnil (fn [[c ws]] [(inc c) (conj ws w)]) [0 #{}])))))))
    (doseq [[ref [cnt ws]] (take 20 (sort-by (fn [[_ [c ws]]] [(- (count ws)) (- c)]) @verse-hits))]
      (println ref "—" (count ws) "words:" (sort ws)))))
(comment (s/build!) (run-all))
