(ns experiments.fiber.143b-messages
  "Experiment 143b: Message fibers in 4D and 6D.

   Key fibers:
   - Truth: garden → 'I heard' → flaming sword
   - Covenant: pillar → bulls → obedience → cut (Exodus 24:4-8)
   - Covenant 6D: midst → Yom Kippur goat → doubled Name → your fathers
   - Peace 6D: 'I AM sent me' → tent → Yom Kippur → judgment
   - Beginning: in you → locust → earth → Moses → nations → aleph-tav (restoration)
   - Elohim 6D: Elitzur → Israel → Aaron → 'to me' → from people"
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn truth-garden-fiber []
  (let [hits (s/find-word [7 50 13 67] "אמת")]
    (f/print-fiber (first (filter #(and (= (:skip %) 805)
                                        (some (fn [tw] (and tw (= (:word tw) "האדם")))
                                              (:torah-words %))) hits)))))

(defn covenant-ceremony-fiber []
  (let [hits (s/find-word [7 50 13 67] "ברית")]
    (f/print-fiber (first (filter #(and (= (:skip %) 68)
                                        (some (fn [tw] (and tw (= (:word tw) "מצבה")))
                                              (:torah-words %))) hits)))))

(defn covenant-doubled-name-6d []
  (let [hits (s/find-word [2 5 5 7 13 67] "ברית" {:max-results 2000})]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "שעיר")))
                                          (:torah-words %))
                                  (f/non-surface hits))))))

(defn peace-sent-me-6d []
  (let [hits (s/find-word [2 5 5 7 13 67] "שלום" {:max-results 2000})]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "ילחם")))
                                          (:torah-words %))
                                  (f/non-surface hits))))))

(defn beginning-restoration []
  (let [hits (s/find-word [7 50 13 67] "בראשית")]
    (f/print-fiber (first (filter #(= (:skip %) 937) hits)))))

(defn elohim-to-me-6d []
  (let [hits (s/find-word [2 5 5 7 13 67] "אלהים" {:max-results 2000})]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "אליצור")))
                                          (:torah-words %))
                                  (f/non-surface hits))))))

(defn run-all []
  (println "\n═══ Truth: garden → heard → sword ═══")
  (truth-garden-fiber)
  (println "\n═══ Covenant: pillar → bulls → obedience → cut ═══")
  (covenant-ceremony-fiber)
  (println "\n═══ Covenant 6D: midst → goat → doubled Name → fathers ═══")
  (covenant-doubled-name-6d)
  (println "\n═══ Peace 6D: sent me → tent → Yom Kippur ═══")
  (peace-sent-me-6d)
  (println "\n═══ Beginning: curses → restoration ═══")
  (beginning-restoration)
  (println "\n═══ Elohim 6D: Elitzur → Israel → Aaron → to me ═══")
  (elohim-to-me-6d))

(comment
  (s/build!)
  (run-all))
