(ns experiments.fiber.143y-verbs-of-god
  "Experiment 143y: The verbs of God.

   Choose→lawgiver (Shiloh, Genesis 49:10). Love→curse (Genesis 3:17).
   See→forbidden fruit. Give→surrender of idols. Remember→Shechem.
   Visit→atonement+anger+lord. Forgive→Noah."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn verb-richest-fiber
  "Find and print the richest non-surface fiber for a word."
  [dims word]
  (let [hits (s/find-word dims word {:max-results 300})
        ns (f/non-surface hits)
        rich (first (sort-by :torah-word-count > ns))]
    (when rich (f/print-fiber rich))))

(defn run-all []
  (let [dims [7 50 13 67]
        verbs [["ברא" "create"] ["דבר" "speak"] ["ברך" "bless"]
               ["צוה" "command"] ["זכר" "remember"] ["ראה" "see"]
               ["שמע" "hear"] ["ידע" "know"] ["בחר" "choose"]
               ["אהב" "love"] ["שמר" "guard"] ["גאל" "redeem"]
               ["סלח" "forgive"] ["רפא" "heal"] ["נחם" "comfort"]
               ["פקד" "visit"] ["נתן" "give"] ["שלח" "send"]]]
    (doseq [[w eng] verbs]
      (println (format "\n═══ %s (%s) ═══" w eng))
      (verb-richest-fiber dims w))))

(comment
  (s/build!)
  (run-all))
