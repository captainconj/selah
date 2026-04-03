(ns experiments.fiber.143ae-theological-skips
  "Experiment 143ae: Theological skip readings. YHWH finds Babylon at creation."
  (:require [selah.search :as s] [selah.gematria :as g] [selah.dict :as d]))
;; (s/build!)
(defn read-skip [skip n-letters]
  (let [{:keys [letters]} (s/index)
        text (apply str (mapv #(nth letters (* skip %)) (range n-letters)))
        torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))
        found (atom []) seen (atom #{})]
    (doseq [wl (range 3 6) s (range (max 0 (- (count text) wl)))]
      (let [sub (subs text s (+ s wl))]
        (when (and (torah-set sub) (not (@seen sub)))
          (swap! seen conj sub)
          (let [v ((:verse-at (s/index)) (* skip s))]
            (swap! found conj {:word sub :pos s :gv (g/word-value sub)
                                :book (:book v) :ch (:ch v) :vs (:vs v)})))))
    (sort-by :pos @found)))
(defn run-all []
  (doseq [[skip label] [[7 "completeness"] [26 "YHWH"] [72 "breastplate"] [137 "axis-sum"]]]
    (println (format "\n%s (skip=%d):" label skip))
    (doseq [{:keys [word pos gv book ch vs]} (read-skip skip 100)]
      (println (format "  %d %s GV=%d %s %d:%d" pos word gv book ch vs)))))
(comment (s/build!) (run-all))
