(ns experiments.135-decompositions
  "Experiment 135: Decompositions — all the spaces inside 304,850.

   304,850 = 2 × 5² × 7 × 13 × 67

   48 divisors. k-fold decompositions for k=2..7.
   We chose 7×50×13×67. What lives in the others?

   This experiment:
   1. Verifies the complete divisor list by trial division
   2. Enumerates k-fold decompositions for k=2,3,4,5,6,7
   3. Sets up the framework for exploring alternate coordinate spaces"
  (:require [selah.gematria :as g]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def N 304850)

;; ── Divisors ────────────────────────────────────────────────

(defn divisors
  "All divisors of n, by trial division up to sqrt(n)."
  [n]
  (let [limit (long (Math/sqrt n))]
    (->> (range 1 (inc limit))
         (filter #(zero? (rem n %)))
         (mapcat (fn [d] (if (= d (/ n d)) [d] [d (/ n d)])))
         sort
         vec)))

(def divs (divisors N))
;; Should be 48 divisors

;; ── 4-fold decompositions ───────────────────────────────────

(defn decompositions-4
  "All ordered 4-tuples (a,b,c,d) where a×b×c×d = n and a≤b≤c≤d.
   These are the distinct 4D coordinate spaces over n letters."
  [n]
  (let [divs (divisors n)
        results (atom [])]
    (doseq [a divs
            :when (<= a (long (Math/pow n 0.25)))]
      (let [n-a (/ n a)]
        (doseq [b divs
                :when (and (<= a b)
                           (zero? (rem n-a b))
                           (<= b (long (Math/pow n-a (/ 1.0 3)))))]
          (let [n-ab (/ n-a b)]
            (doseq [c divs
                    :when (and (<= b c)
                               (zero? (rem n-ab c))
                               (<= c (/ n-ab c)))]
              (let [d (/ n-ab c)]
                (when (and (<= c d) (integer? d))
                  (swap! results conj [a b c d]))))))))
    (sort @results)))

(def decomps (decompositions-4 N))

;; ── Generalized k-fold decompositions ─────────────────────

(defn decompositions-k
  "All ordered k-tuples (a₁ ≤ a₂ ≤ ... ≤ aₖ) where product = n.
   k=1 → [[n]], k=2 → divisor pairs, etc."
  [n k]
  (cond
    (= k 1) [[n]]
    (= k 2) (let [divs (divisors n)]
              (->> divs
                   (filter #(<= % (long (Math/sqrt n))))
                   (filter #(zero? (rem n %)))
                   (mapv (fn [d] [d (/ n d)]))))
    :else
    (let [divs (divisors n)
          ;; a₁ must be ≤ n^(1/k)
          upper (long (Math/pow n (/ 1.0 k)))
          results (atom [])]
      (doseq [a divs
              :when (<= a upper)]
        (let [rest-n (/ n a)
              ;; recurse for (k-1)-tuples of rest-n, with min = a
              sub-decomps (decompositions-k rest-n (dec k))]
          (doseq [sub sub-decomps
                  :when (<= a (first sub))]
            (swap! results conj (vec (cons a sub))))))
      (sort @results))))

;; ── Properties ──────────────────────────────────────────────

(def axis-names
  "Known axis meanings."
  {7  "completeness"
   13 "love (אהבה=אחד=13)"
   50 "jubilee"
   67 "understanding (בינה=67)"
   26 "YHWH"
   10 "Yod"
   5  "He"
   6  "Vav"
   2  "witness/division"
   14 "David/hand (דוד=יד=14)"
   25 "center of jubilee"
   35 "7×5"
   70 "7×10"
   91 "angel (מלאך=91)"})

(defn decomp-properties
  "Annotate a decomposition with axis meanings and properties."
  [[a b c d]]
  {:axes [a b c d]
   :meanings (mapv #(get axis-names % nil) [a b c d])
   :product (* a b c d)
   :axis-sum (+ a b c d)
   :has-7? (some #{7} [a b c d])
   :has-13? (some #{13} [a b c d])
   :has-67? (some #{67} [a b c d])
   :has-50? (some #{50} [a b c d])
   :named-count (count (filter some? (map #(get axis-names %) [a b c d])))})

;; ── Run ─────────────────────────────────────────────────────

(defn run-all []
  (let [divs-result (divisors N)

        expected-divs [1 2 5 7 10 13 14 25 26 35 50 65
                       67 70 91 130 134 175 182 325 335 350 455 469
                       650 670 871 910 938 1675 1742 2275 2345 3350
                       4355 4550 4690 6097 8710 11725 12194 21775
                       23450 30485 43550 60970 152425 304850]

        match? (= divs-result expected-divs)

        decomps-result (decompositions-4 N)
        annotated (mapv decomp-properties decomps-result)

        ;; The canonical space
        canonical (first (filter #(= [7 50 13 67] (:axes %)) annotated))

        ;; Spaces with theological axes
        has-7-13-67 (filter #(and (:has-7? %) (:has-13? %) (:has-67? %)) annotated)

        output (with-out-str
                 (println "=== Experiment 135: Decompositions ===")
                 (println)
                 (println (str "N = " N " = 2 × 5² × 7 × 13 × 67"))
                 (println)
                 (println (str "Divisors: " (count divs-result)))
                 (println (str "Match expected list: " match?))
                 (when-not match?
                   (println "COMPUTED:" divs-result)
                   (println "EXPECTED:" expected-divs))
                 (println)
                 (println (str "Divisors up to √" N " (√" N " ≈ " (format "%.1f" (Math/sqrt N)) "):"))
                 (let [sqrt-n (Math/sqrt N)
                       small (filter #(<= % sqrt-n) divs-result)]
                   (println (str "  " (count small) " divisors: " (vec small))))
                 (println)
                 (println (str "4-fold decompositions: " (count decomps-result)))
                 (println)
                 (println "=== ALL DECOMPOSITIONS ===")
                 (println)
                 (doseq [[i d] (map-indexed vector annotated)]
                   (let [{:keys [axes meanings axis-sum named-count]} d
                         label (if (= [7 50 13 67] axes) " ← CANONICAL" "")]
                     (printf "%3d. %s  sum=%-4d  named=%d  %s%s\n"
                             (inc i)
                             (pr-str axes)
                             axis-sum
                             named-count
                             (clojure.string/join ", "
                               (remove nil? meanings))
                             label)))
                 (println)
                 (println "=== SPACES WITH {7, 13, 67} ===")
                 (doseq [d has-7-13-67]
                   (printf "  %s  sum=%-4d\n" (pr-str (:axes d)) (:axis-sum d)))
                 (println)
                 (println "=== AXIS SUM DISTRIBUTION ===")
                 (let [sums (sort (map :axis-sum annotated))]
                   (doseq [[s ds] (sort-by first (group-by :axis-sum annotated))]
                     (printf "  sum=%-6d  %d decomposition(s)  %s\n"
                             s (count ds)
                             (if (= s 137) "← 1/α" "")))))]

    ;; Write artifacts
    (io/make-parents "data/experiments/135-decompositions.edn")
    (spit "data/experiments/135-decompositions.edn"
          (pr-str {:experiment 135
                   :N N
                   :prime-factorization "2 × 5² × 7 × 13 × 67"
                   :divisors divs-result
                   :divisor-count (count divs-result)
                   :match-expected? match?
                   :decompositions (vec decomps-result)
                   :decomposition-count (count decomps-result)
                   :annotated annotated}))
    (spit "data/experiments/135-decompositions-output.txt" output)
    (print output)
    {:divisors divs-result
     :match? match?
     :decompositions decomps-result
     :count (count decomps-result)}))

;; ── All dimensions ─────────────────────────────────────────

(defn run-all-dimensions []
  (let [all-k (into (sorted-map)
                    (for [k (range 2 8)]
                      [k (decompositions-k N k)]))]
    (doseq [[k decomps] all-k]
      (printf "\n=== %dD decompositions: %d ===\n" k (count decomps))
      (doseq [[i d] (map-indexed vector decomps)]
        (printf "%3d. %s  sum=%d\n"
                (inc i)
                (str/join " × " d)
                (apply + d))))
    (let [summary (into (sorted-map)
                        (for [[k ds] all-k]
                          [k {:count (count ds)
                              :non-trivial (count (filter #(every? pos? (map dec %)) ds))
                              :decompositions (vec ds)}]))]
      (io/make-parents "data/experiments/135-all-decompositions.edn")
      (spit "data/experiments/135-all-decompositions.edn"
            (pr-str {:experiment "135-extended"
                     :N N
                     :prime-factorization "2 × 5² × 7 × 13 × 67"
                     :by-dimension summary}))
      summary)))

;; ── REPL ────────────────────────────────────────────────────

(comment
  (divisors N)
  (decompositions-4 N)
  (decompositions-k N 2)
  (decompositions-k N 3)
  (decompositions-k N 5)
  (decompositions-k N 6)
  (decompositions-k N 7)
  (run-all)
  (run-all-dimensions)
  )
