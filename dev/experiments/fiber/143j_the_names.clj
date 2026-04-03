(ns experiments.fiber.143j-the-names
  "Experiment 143j: The divine names in the geometry.

   I AM (אנכי): love(14) → serpent(358=messiah) → cherubim
   I WILL BE (אהיה): God → bless her → born(50=jubilee) → God
   I WILL BE lands in YHWH 113 times (3x enriched)."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]))

;; (s/build!)

(defn i-am-serpent-cherubim []
  (let [hits (s/find-word [7 50 13 67] "אנכי")]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "נחש")))
                                          (:torah-words %))
                                  (f/non-surface hits))))))

(defn i-am-doubled []
  (let [hits (s/find-word [7 50 13 67] "אנכי")]
    (f/print-fiber (first (filter #(and (= (:skip %) -44488)
                                        (some (fn [tw] (and tw (= (:word tw) "אני")))
                                              (:torah-words %)))
                                  (f/non-surface hits))))))

(defn i-will-be-isaac []
  (let [hits (s/find-word [7 50 13 67] "אהיה")]
    (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "וברכתיה")))
                                          (:torah-words %))
                                  (f/non-surface hits))))))

(defn i-will-be-yhwh-count []
  (let [hits (s/find-word [7 50 13 67] "אהיה")
        ns (f/non-surface hits)
        yhwh (count (filter #(some (fn [tw] (and tw (= (:word tw) "יהוה")))
                                    (:torah-words %)) ns))]
    (println (format "אהיה fibers landing in יהוה: %d of %d (%.1f%%)"
                     yhwh (count ns) (* 100.0 (/ yhwh (count ns)))))))

(defn run-all []
  (println "═══ I AM → love → serpent → cherubim ═══")
  (i-am-serpent-cherubim)
  (println "\n═══ I AM → I → I AM → struck → garments ═══")
  (i-am-doubled)
  (println "\n═══ I WILL BE → God → bless her → born → God ═══")
  (i-will-be-isaac)
  (println)
  (i-will-be-yhwh-count))

(comment
  (s/build!)
  (run-all))
