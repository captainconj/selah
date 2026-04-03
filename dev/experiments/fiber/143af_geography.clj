(ns experiments.fiber.143af-geography
  "Experiment 143af: Geography. Hot spots, book density, fiber traffic."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.dict :as d]))
;; (s/build!)
(defn hot-spots []
  (let [{:keys [letters n]} (s/index)
        torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))]
    (println "Hot spots (300-letter windows):\n")
    (let [results (for [start (range 0 (- n 300) 10000)]
                    (let [text (subs (apply str letters) start (+ start 300))
                          seen (atom #{})]
                      (doseq [wl (range 3 6) s (range (- 300 wl))]
                        (when (torah-set (subs text s (+ s wl))) (swap! seen conj (subs text s (+ s wl)))))
                      {:start start :words (count @seen) :book (:book ((:verse-at (s/index)) start))}))]
      (doseq [r (take 10 (sort-by :words > results))]
        (println (format "  %d: %d words (%s)" (:start r) (:words r) (:book r)))))))
(defn run-all [] (hot-spots))
(comment (s/build!) (run-all))
