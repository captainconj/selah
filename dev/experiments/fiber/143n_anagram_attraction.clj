(ns experiments.fiber.143n-anagram-attraction
  "Experiment 143n: Anagram pairs attract in the geometry.

   serpent→breastplate: 27.2x. head→blessed: 17x.
   creeping→guard: 13x. flesh↔breaking: mutual.
   lie-down→lamb: 8.3x."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]))

;; (s/build!)

(defn anagram-enrichment
  "Test enrichment of search-word → anagram-host."
  [dims sw hw]
  (let [idx (s/index)
        n (double (:n idx))
        cov (double (reduce + (map #(- (:end %) (:start %))
                                    (filter #(= (:word %) hw) (:words idx)))))
        hits (s/find-word dims sw {:max-results 500})
        ns (f/non-surface hits)
        total (double (* (count (seq sw)) (count ns)))
        hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
        obs (double (get hosts hw 0))
        exp (* (/ cov n) total)]
    {:search sw :host hw :observed (long obs) :expected exp
     :ratio (if (pos? exp) (/ obs exp) 0.0)}))

(defn run-all []
  (let [dims [7 50 13 67]
        pairs [["נחש" "חשן" "serpent→breastplate"]
               ["ראש" "אשר" "head→blessed"]
               ["רמש" "שמר" "creeping→guard"]
               ["בשר" "שבר" "flesh→break"]
               ["שבר" "בשר" "break→flesh"]
               ["שכב" "כבש" "lie-down→lamb"]]]
    (println "═══ ANAGRAM ATTRACTION ═══\n")
    (doseq [[sw hw label] pairs]
      (let [{:keys [observed expected ratio]} (anagram-enrichment dims sw hw)]
        (println (format "  %s: obs=%d exp=%.1f ratio=%.1fx"
                         label observed expected ratio))))))

(comment
  (s/build!)
  (run-all))
