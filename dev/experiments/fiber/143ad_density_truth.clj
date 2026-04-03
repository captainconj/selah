(ns experiments.fiber.143ad-density-truth
  "Experiment 143ad: Density truth. Fair measurement across all skips."
  (:require [selah.search :as s] [selah.gematria :as g] [selah.dict :as d]))
;; (s/build!)
(defn density-at-skip [skip]
  (let [{:keys [letters n]} (s/index)
        torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))
        flen (min 300 (quot n skip))
        text (apply str (mapv #(nth letters (* skip %)) (range flen)))
        seen (atom #{})]
    (doseq [wl (range 3 6) s (range (max 0 (- (count text) wl)))]
      (when (torah-set (subs text s (+ s wl))) (swap! seen conj (subs text s (+ s wl)))))
    {:skip skip :letters flen :words (count @seen) :density (double (/ (count @seen) flen))}))
(defn run-all []
  (println "Density at theological skips (300 letters, 3-5 letter words):\n")
  (doseq [skip [1 7 13 26 50 67 72 137 805 871]]
    (let [{:keys [words density]} (density-at-skip skip)]
      (println (format "  skip=%-4d  %3d words  density=%.3f" skip words density)))))
(comment (s/build!) (run-all))
