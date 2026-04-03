(ns experiments.fiber.143u-direction-density
  "Experiment 143u: Direction density, self-intersection, axis reading.

   Torah is saturated: 303/313 words on all axes.
   Heart is omnidirectional (1.2x). YHWH concentrated 55x on surface.
   YHWH self-intersects 40%. Completeness axis reads בתוך (in the midst)."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.string :as str]))

;; (s/build!)

(defn axis-concentration
  "Count hits per pure axis for a word."
  [word]
  (let [{:keys [letters letter-idx n]} (s/index)
        axes {0 43550 1 871 2 67 3 1}]
    (into {} (for [[ax skip] axes]
               [ax (+ (count (or (s/search-at-skip letters letter-idx word skip n) []))
                      (count (or (s/search-at-skip letters letter-idx word (- skip) n) [])))]))))

(defn self-intersection-rate
  "What fraction of non-surface fibers self-intersect?"
  [dims word]
  (let [hits (s/find-word dims word {:max-results 500})
        ns (f/non-surface hits)
        self (count (filter #(some (fn [tw] (and tw (= (:word tw) word))) (:torah-words %)) ns))]
    {:word word :self self :total (count ns)
     :rate (if (pos? (count ns)) (double (/ self (count ns))) 0.0)}))

(defn read-completeness-axis
  "Read the 7 letters of the completeness axis."
  []
  (let [{:keys [letters n]} (s/index)
        idx (s/index)]
    (println "The 7 letters of the completeness axis:\n")
    (doseq [i (range 7)]
      (let [pos (* 43550 i)
            letter (nth letters pos)
            tw ((:word-at idx) pos)
            v ((:verse-at idx) pos)]
        (println (format "  a=%d pos=%-7d %s in %-10s %s %d:%d"
                         i pos letter (if tw (:word tw) "?") (:book v) (:ch v) (:vs v)))))
    (let [seven (apply str (mapv #(nth letters (* 43550 %)) (range 7)))]
      (println (format "\n  Spells: %s  GV=%d" seven (g/word-value seven)))
      (println "  First four letters: בתוך = in the midst"))))

(defn run-all []
  (println "═══ AXIS CONCENTRATION ═══\n")
  (doseq [w ["לב" "יהוה" "בהו" "את" "אנכי"]]
    (let [c (axis-concentration w)]
      (println (format "  %-6s a=%-5d b=%-5d c=%-5d d=%-5d" w (get c 0) (get c 1) (get c 2) (get c 3)))))
  (println "\n═══ SELF-INTERSECTION ═══\n")
  (doseq [w ["יהוה" "אנכי" "אחד" "קדש" "כבש" "ברך" "ברית"]]
    (let [{:keys [self total rate]} (self-intersection-rate [7 50 13 67] w)]
      (println (format "  %-6s %d/%d = %.1f%%" w self total (* 100 rate)))))
  (println "\n═══ COMPLETENESS AXIS ═══\n")
  (read-completeness-axis))

(comment
  (s/build!)
  (run-all))
