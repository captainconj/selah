(ns experiments.fiber.143s-cross-decomposition
  "Experiment 143s: Cross-decomposition universality.

   Void→YHWH: 111/111 (100%). Grace→Joseph: 101/111 (91%).
   Head→blessed: 97/111 (87%). Findings are properties of the text."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]))

;; (s/build!)

(defn universality-test
  "Test how many decompositions show search-word → host-word connection."
  [search-word host-word]
  (let [decomps (s/all-decompositions)
        results (atom [])]
    (doseq [dims decomps]
      (let [hits (s/find-word dims search-word {:max-results 300})
            ns (f/non-surface hits)
            hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
            count (get hosts host-word 0)]
        (when (pos? count)
          (swap! results conj {:dims dims :count count :ns (count ns)}))))
    {:search search-word :host host-word
     :present (count @results) :total (count decomps)
     :pct (* 100.0 (/ (count @results) (count decomps)))
     :top (take 5 (sort-by :count > @results))}))

(defn run-all []
  (doseq [[sw hw label] [["בהו" "יהוה" "void→YHWH"]
                           ["חסד" "יוסף" "grace→Joseph"]
                           ["ראש" "אשר" "head→blessed"]]]
    (println (format "\n═══ %s ═══" label))
    (let [{:keys [present total pct top]} (universality-test sw hw)]
      (println (format "  %d/%d decompositions (%.0f%%)" present total pct))
      (println "  Top spaces:")
      (doseq [{:keys [dims count ns]} top]
        (println (format "    %s: %d hosts from %d fibers" (str dims) count ns))))))

(comment
  (s/build!)
  (run-all))
