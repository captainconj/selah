(ns experiments.fiber.143z-parallel-lines
  "Experiment 143z: Parallel lines.

   Heart+aleph-tav: 211 shared verses on parallel fibers (5x any other pair).
   40 word pairs converge at Yom Kippur.
   AT→LB at position 137 on the body diagonal."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; (s/build!)

(defn parallel-meetings
  "Count shared verses between parallel fibers (same skip) of two words."
  [dims w1 w2]
  (let [h1 (group-by :skip (f/non-surface (s/find-word dims w1 {:max-results 500})))
        h2 (group-by :skip (f/non-surface (s/find-word dims w2 {:max-results 500})))
        shared-skips (clojure.set/intersection (set (keys h1)) (set (keys h2)))
        meetings (atom 0)
        verses (atom [])]
    (doseq [skip shared-skips]
      (doseq [a (get h1 skip) b (get h2 skip)]
        (let [av (set (remove nil? (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs a))))
              bv (set (remove nil? (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs b))))
              shared (clojure.set/intersection av bv)]
          (swap! meetings + (count shared))
          (swap! verses into shared))))
    {:w1 w1 :w2 w2 :meetings @meetings :verses (frequencies @verses)}))

(defn top-pairs
  "Rank all pairs by parallel-fiber verse sharing."
  [dims words]
  (let [results (atom [])]
    (doseq [i (range (count words))]
      (doseq [j (range (inc i) (count words))]
        (let [r (parallel-meetings dims (nth words i) (nth words j))]
          (when (pos? (:meetings r))
            (swap! results conj r)))))
    (sort-by :meetings > @results)))

(defn yom-kippur-pairs
  "Which word pairs share parallel fibers at Leviticus 16?"
  [dims words]
  (let [pairs (atom #{})]
    (doseq [i (range (count words))]
      (doseq [j (range (inc i) (count words))]
        (let [w1 (nth words i) w2 (nth words j)
              h1 (group-by :skip (f/non-surface (s/find-word dims w1 {:max-results 200})))
              h2 (group-by :skip (f/non-surface (s/find-word dims w2 {:max-results 200})))
              shared-skips (clojure.set/intersection (set (keys h1)) (set (keys h2)))]
          (doseq [skip shared-skips]
            (doseq [a (get h1 skip) b (get h2 skip)]
              (let [a16 (some #(and % (= (:book %) "Leviticus") (= (:ch %) 16)) (:verse-refs a))
                    b16 (some #(and % (= (:book %) "Leviticus") (= (:ch %) 16)) (:verse-refs b))]
                (when (and a16 b16)
                  (swap! pairs conj (str w1 "+" w2)))))))))
    @pairs))

(defn run-all []
  (let [dims [7 50 13 67]
        words ["תורה" "כבש" "שלום" "אמת" "חיים" "ברית" "חסד"
               "יהוה" "אנכי" "בהו" "לב" "את" "שמר" "קדש"]]
    (println "═══ TOP PARALLEL PAIRS ═══\n")
    (doseq [{:keys [w1 w2 meetings]} (take 10 (top-pairs dims words))]
      (println (format "  %s + %s : %d shared verses" w1 w2 meetings)))
    (println "\n═══ YOM KIPPUR CONVERGENCE ═══\n")
    (let [yk-words (conj words "עלה" "כפר" "דם" "גאל" "סלח")
          pairs (yom-kippur-pairs dims yk-words)]
      (println (format "  %d pairs meet at Leviticus 16" (count pairs))))))

(comment
  (s/build!)
  (run-all)

  ;; Heart + AT detail
  (let [r (parallel-meetings [7 50 13 67] "לב" "את")]
    (println (:meetings r) "meetings")
    (doseq [[[book ch vs] cnt] (take 10 (sort-by val > (:verses r)))]
      (println book ch vs "—" cnt "times"))))
