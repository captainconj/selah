(ns experiments.fiber.143k-the-void
  "Experiment 143k: The void was love.

   בהו (GV=13=love=one). 404 non-surface fibers.
   132 land in YHWH. Finds love commands, the garden, Joseph, the longing.
   ואהבת (love command, GV=414) = יתד (peg, GV=414)."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn void-garden-fiber []
  (let [hits (s/find-word [7 50 13 67] "בהו")
        ns (f/non-surface hits)]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "בגן")))
                                          (:torah-words %)) ns)))))

(defn void-love-command []
  (let [hits (s/find-word [7 50 13 67] "בהו")
        ns (f/non-surface hits)]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "ואהבת")))
                                          (:torah-words %)) ns)))))

(defn void-joseph-fiber []
  (let [hits (s/find-word [7 50 13 67] "בהו")
        ns (f/non-surface hits)]
    (doseq [h (filter #(some (fn [tw] (and tw (str/starts-with? (or (:word tw) "") "אהב")))
                              (:torah-words %)) ns)]
      (f/print-fiber h)
      (println))))

(defn void-yhwh-count []
  (let [hits (s/find-word [7 50 13 67] "בהו")
        ns (f/non-surface hits)
        yhwh (count (filter #(some (fn [tw] (and tw (= (:word tw) "יהוה")))
                                    (:torah-words %)) ns))]
    (println (format "Void→YHWH: %d of %d fibers (%.1f%%)"
                     yhwh (count ns) (* 100.0 (/ yhwh (count ns)))))))

(defn run-all []
  (println "═══ Void → Garden → YHWH → Exile ═══")
  (void-garden-fiber)
  (println "\n═══ Void → Love Command ═══")
  (void-love-command)
  (println "\n═══ Void → Love Words ═══")
  (void-joseph-fiber)
  (void-yhwh-count))

(comment
  (s/build!)
  (run-all))
