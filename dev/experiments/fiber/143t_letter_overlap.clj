(ns experiments.fiber.143t-letter-overlap
  "Experiment 143t: Letter overlap — which letter drives each host connection."
  (:require [selah.search :as s] [selah.fiber :as f] [selah.gematria :as g]))
;; (s/build!)
(defn which-letter-hosts [dims search-word host-word]
  (let [hits (s/find-word dims search-word) ns (f/non-surface hits)
        idx (s/index) letters (:letters idx) counts (atom {})]
    (doseq [h ns]
      (doseq [[i tw pos] (map vector (range) (:torah-words h) (:positions h))]
        (when (and tw (= (:word tw) host-word))
          (swap! counts update i (fnil inc 0)))))
    @counts))
(defn run-all []
  (let [dims [7 50 13 67]]
    (doseq [[sw hw label] [["בהו" "יהוה" "void→YHWH"] ["אמת" "את" "truth→AT"]
                             ["חסד" "יוסף" "grace→Joseph"]]]
      (println (format "\n%s:" label))
      (let [counts (which-letter-hosts dims sw hw)]
        (doseq [[i cnt] (sort counts)]
          (println (format "  letter %d (%s): %d times" i (nth (seq sw) i) cnt)))))))
(comment (s/build!) (run-all))
