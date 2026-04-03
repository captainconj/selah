(ns experiments.135b-decomposition-properties
  "Experiment 135b: Number-theoretic properties of 304,850.

   Findings from the spy reports on decomposition spaces:
   1. phi(304,850) = 95,040 = 12!/7! = |M_12| (Mathieu group on 12 points)
   2. 13^2 + 67^2 = 2 x 17 x 137 (silent axis diagonal encodes axis sum)
   3. Graph diameter = 133 = 137 - 4 = 7 x 19
   4. 304,850 is the ONLY number in +/-10 with a non-trivial 4-tuple sum of 137 (all factors > 1)
   5. mu(304,850) = 0 (breath is the only repeated prime)
   6. Gematria of composite axes"
  (:require [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def N 304850)
(def primes [2 5 7 13 67])
(def canonical [7 50 13 67])

;; ── Euler's totient ───────────────────────────────────────

(defn euler-totient [n prime-factors]
  (long (reduce (fn [acc p] (-> acc (/ p) (* (dec p)))) n prime-factors)))

(def phi (euler-totient N primes))
;; => 95040

(def factorial-12-over-7 (reduce * (range 8 13)))
;; => 95040 = 8 x 9 x 10 x 11 x 12

;; |M_12| = 95,040 — the Mathieu group on 12 points
;; 12 stones on the breastplate.

;; ── Silent axis diagonal ─────────────────────────────────

(def silent-diagonal-squared (+ (* 13 13) (* 67 67)))
;; => 4658 = 2 x 17 x 137

;; The face diagonal of the love-understanding plane
;; contains 137 (the axis sum) in its factorization.

;; ── Graph diameter ───────────────────────────────────────

(def graph-diameter (apply + (map dec canonical)))
;; => 133 = 137 - 4 = 7 x 19

;; Corner-to-corner Manhattan distance in the canonical grid
;; equals the fine structure constant minus the dimensionality.

;; ── Mobius function ──────────────────────────────────────

;; mu(304,850) = 0 because 5^2 | 304,850
;; The breath (He, ה) is the only repeated prime.
;; Everything else appears exactly once.

;; ── Nearby number uniqueness ─────────────────────────────

(defn divisors [n]
  (let [limit (long (Math/sqrt n))]
    (->> (range 1 (inc limit))
         (filter #(zero? (rem n %)))
         (mapcat (fn [d] (if (= d (/ n d)) [d] [d (/ n d)])))
         sort vec)))

(defn four-tuples-with-sum
  "Find all ordered 4-tuples [a b c d] where a*b*c*d = n, a+b+c+d = target,
   a <= b <= c <= d, and all factors >= 2 (no trivial factor of 1).
   Used to test uniqueness of 304,850 among nearby integers."
  [n target]
  (let [divs (filterv #(> % 1) (divisors n))
        results (atom [])]
    (doseq [a divs
            :when (<= a (long (Math/pow n 0.25)))]
      (let [na (/ n a)]
        (doseq [b divs
                :when (and (<= a b) (zero? (rem na b))
                           (<= b (long (Math/pow na (/ 1.0 3)))))]
          (let [nab (/ na b)]
            (doseq [c divs
                    :when (and (<= b c) (zero? (rem nab c)) (<= c (/ nab c)))]
              (let [dd (/ nab c)]
                (when (and (<= c dd) (integer? dd) (= target (+ a b c dd)))
                  (swap! results conj [a b c dd]))))))))
    @results))

;; ── Gematria of composite axes ───────────────────────────

(def composite-axes
  "Axis values that appear in decompositions as products of theological primes."
  {871  {:factors [13 67] :meaning "love x understanding"}
   469  {:factors [7 67]  :meaning "completeness x understanding"}
   335  {:factors [5 67]  :meaning "He x understanding"}
   938  {:factors [2 7 67] :meaning "witness x completeness x understanding"}
   670  {:factors [2 5 67] :meaning "witness x He x understanding"}
   1675 {:factors [5 5 67] :meaning "He x He x understanding"}
   455  {:factors [5 7 13] :meaning "He x completeness x love"}
   650  {:factors [2 5 5 13] :meaning "witness x He x He x love"}
   134  {:factors [2 67]  :meaning "witness x understanding"}
   182  {:factors [2 7 13] :meaning "witness x completeness x love"}
   175  {:factors [5 5 7] :meaning "He x He x completeness"}
   130  {:factors [2 5 13] :meaning "witness x He x love"}})

(defn gv-words
  "Find Torah words with a given gematria value."
  [target]
  (filter #(= target (g/word-value %)) (d/torah-words)))

;; ── Run ──────────────────────────────────────────────────

(defn run-all []
  (let [output (with-out-str
                 (println "=== Experiment 135b: Decomposition Properties ===")
                 (println)

                 (println "── Euler's totient ──")
                 (println (str "phi(304,850) = " phi))
                 (println (str "12!/7!       = " factorial-12-over-7))
                 (println (str "|M_12|       = 95040"))
                 (println (str "Match: " (= phi 95040 (long factorial-12-over-7))))
                 (println "  -> Mathieu group M_12 acts on 12 points.")
                 (println "  -> Breastplate has 12 stones.")
                 (println)

                 (println "── Silent axis diagonal ──")
                 (println (str "13^2 + 67^2 = " silent-diagonal-squared))
                 (println (str "2 x 17 x 137 = " (* 2 17 137)))
                 (println (str "Match: " (= silent-diagonal-squared (* 2 17 137))))
                 (println "  -> The silent axes encode 137 in their diagonal.")
                 (println)

                 (println "── Graph diameter ──")
                 (println (str "(7-1)+(50-1)+(13-1)+(67-1) = " graph-diameter))
                 (println (str "137 - 4 = " (- 137 4)))
                 (println (str "7 x 19 = " (* 7 19)))
                 (println "  -> Fine structure constant minus dimensionality.")
                 (println)

                 (println "── Mobius function ──")
                 (println "mu(304,850) = 0  (5^2 divides N)")
                 (println "  -> The breath is the only repeated prime.")
                 (println)

                 (println "── Nearby number uniqueness ──")
                 (println "Numbers in [304,840..304,860] with 4-tuple sum = 137:")
                 (doseq [n (range 304840 304861)]
                   (let [hits (four-tuples-with-sum n 137)]
                     (when (seq hits)
                       (println (str "  " n ": " (pr-str hits))))))
                 (println)

                 (println "── Gematria of composite axes ──")
                 (doseq [[gv info] (sort composite-axes)]
                   (let [words (gv-words gv)]
                     (printf "  GV=%d (%s): %d Torah words"
                             gv (:meaning info) (count words))
                     (when (seq words)
                       (let [sample (take 5 words)]
                         (printf "  [%s%s]"
                                 (str/join ", " sample)
                                 (if (> (count words) 5) "..." ""))))
                     (println))))]

    (io/make-parents "data/experiments/135b-decomposition-properties.edn")
    (spit "data/experiments/135b-decomposition-properties.edn"
          (pr-str {:experiment "135b"
                   :phi phi
                   :factorial-12-over-7 factorial-12-over-7
                   :mathieu-m12 95040
                   :silent-diagonal-squared silent-diagonal-squared
                   :silent-diagonal-factors [2 17 137]
                   :graph-diameter graph-diameter
                   :mobius 0
                   :composite-axes (into {}
                     (for [[gv info] composite-axes]
                       [gv (assoc info :torah-words (vec (gv-words gv)))]))}))
    (spit "data/experiments/135b-decomposition-properties-output.txt" output)
    (print output)
    {:phi phi
     :diagonal silent-diagonal-squared
     :diameter graph-diameter}))

;; ── REPL ─────────────────────────────────────────────────

(comment
  (euler-totient N primes)
  (= phi 95040 (long factorial-12-over-7))
  (= silent-diagonal-squared (* 2 17 137))
  (four-tuples-with-sum 304850 137)
  (gv-words 871)
  (gv-words 469)
  (gv-words 335)
  (run-all)
  )
