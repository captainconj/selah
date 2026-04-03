(ns experiments.fiber.143c-grace-fibers
  "Experiment 143c: Grace fibers — the ghost traces the son.

   חסד (GV=72) cannot be read on the breastplate. But it has fibers.
   9 of 79 non-surface fibers land in Joseph through shared samech.

   Key fibers:
   - Grace → breastplate (GV=363=anointed) → Joseph → stew
   - Grace → I have forgiven → you knew
   - Grace → family → Joseph → your hand (jubilee walk)
   - Grace → bread → Joseph → born
   - Grace → dawn → Joseph → field"
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]))

;; (s/build!)

(defn grace-through-joseph []
  (let [hits (s/find-word [7 50 13 67] "חסד")
        ns (f/non-surface hits)]
    (println (format "%d non-surface grace fibers\n" (count ns)))
    (let [joseph (filter #(some (fn [tw] (and tw (= (:word tw) "יוסף"))) (:torah-words %)) ns)]
      (println (format "%d pass through Joseph:\n" (count joseph)))
      (doseq [h joseph] (f/print-fiber h) (println)))))

(defn grace-forgiveness []
  (let [hits (s/find-word [7 50 13 67] "חסד")
        ns (f/non-surface hits)]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "סלחתי")))
                                          (:torah-words %)) ns)))))

(defn grace-cross-decomposition []
  (println "Grace→Joseph across all 111 decompositions:\n")
  (let [decomps (s/all-decompositions)]
    (doseq [dims decomps]
      (let [hits (s/find-word dims "חסד" {:max-results 200})
            ns (f/non-surface hits)
            hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
            joseph (get hosts "יוסף" 0)]
        (when (> joseph 0)
          (println (format "  %s (%dD): %d Joseph hosts" (str dims) (count dims) joseph)))))))

(defn run-all []
  (grace-through-joseph)
  (println "\n═══ Grace → Forgiveness ═══")
  (grace-forgiveness))

(comment
  (s/build!)
  (run-all)
  (grace-cross-decomposition))
