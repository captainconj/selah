(ns experiments.fiber.143d-meeting-points
  "Experiment 143d: Meeting points. Where 3+ search words converge on same Torah word."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn run-all []
  (let [dims [7 50 13 67]
        words ["תורה" "כבש" "שלום" "אמת" "חיים" "חסד" "ברית"]
        hosts (atom {})]
    (doseq [w words]
      (doseq [h (f/non-surface (s/find-word dims w {:max-results 500}))]
        (doseq [tw (:torah-words h)]
          (when tw (swap! hosts update (:start tw) (fnil conj #{}) w)))))
    (let [meetings (filter #(>= (count (val %)) 3) @hosts)]
      (println (format "%d positions where 3+ words meet\n" (count meetings)))
      (doseq [[pos ws] (sort-by (comp count val) > meetings)]
        (let [tw ((:word-at (s/index)) pos) v ((:verse-at (s/index)) pos)]
          (println (format "  %-12s %s %d:%d — %d words: %s" (:word tw) (:book v) (:ch v) (:vs v) (count ws) (sort ws))))))))
(comment (s/build!) (run-all))
