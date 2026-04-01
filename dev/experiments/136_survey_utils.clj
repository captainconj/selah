(ns experiments.136-survey-utils
  "Experiment 136: shared utilities for the decomposition survey.

   Generic coordinate arithmetic for arbitrary k-dimensional spaces.
   Every 136_Nd file requires this."
  (:require [selah.space.coords :as c]
            [selah.els.engine :as els]
            [selah.gematria :as g]
            [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def N 304850)
(def half-N (quot N 2))  ;; 152,425 — the fold point

;; ── Generic coordinate arithmetic ───────────────────────────

(defn compute-strides
  "Strides for a dims vector. stride[i] = product of dims[(i+1)..]."
  [dims]
  (vec (for [i (range (count dims))]
         (reduce * (subvec dims (inc i))))))

(defn idx->coord
  "Position → coordinate vector for arbitrary dims."
  [dims ^long pos]
  (let [strides (compute-strides dims)
        k (count dims)]
    (loop [i 0, rem pos, acc (transient [])]
      (if (= i k)
        (persistent! acc)
        (let [s (nth strides i 1)
              v (quot rem s)
              r (mod rem s)]
          (recur (inc i) r (conj! acc v)))))))

(defn coord->idx
  "Coordinate vector → position for arbitrary dims."
  [dims coord]
  (let [strides (compute-strides dims)]
    (reduce + (map * coord strides))))

(defn center-coord
  "Geometric center coordinate: d/2 (integer division) for each axis.
   For 0-indexed axis of size d, positions are 0..d-1.
   Center = d÷2, which for even d gives the exact midpoint,
   and for odd d gives floor(d/2) = (d-1)/2."
  [dims]
  (mapv #(quot % 2) dims))

(defn center-pos
  "Linear position of the geometric center."
  [dims]
  (coord->idx dims (center-coord dims)))

;; ── Space / text access ─────────────────────────────────────

(defn ensure-space! []
  (c/space))

(defn letter-at [s ^long i]
  (c/letter-at s i))

(defn verse-at [s ^long i]
  (c/verse-at s i))

(defn verse-str [s ^long i]
  (let [v (verse-at s i)]
    (str (:book v) " " (:ch v) ":" (:vs v))))

;; ── Walk smoothness ─────────────────────────────────────────

(defn manhattan-distance
  "Manhattan distance between two coordinate vectors."
  [c1 c2]
  (reduce + (map #(Math/abs (long (- %1 %2))) c1 c2)))

(defn walk-smoothness
  "Compute walk statistics for a dims vector.
   Returns {:mean :max :median :p90 :p99 :step1-count}
   where step1-count is how many consecutive letters are Manhattan neighbors."
  [dims ^long sample-size]
  (let [step (max 1 (quot N sample-size))
        pairs (for [i (range 0 (dec N) step)]
                (manhattan-distance
                 (idx->coord dims i)
                 (idx->coord dims (inc i))))
        dists (vec (sort pairs))
        n (count dists)]
    (when (pos? n)
      {:mean       (double (/ (reduce + dists) n))
       :max        (last dists)
       :median     (nth dists (quot n 2))
       :p90        (nth dists (long (* 0.9 n)))
       :p99        (nth dists (min (dec n) (long (* 0.99 n))))
       :step1-pct  (double (/ (count (filter #(= 1 %) dists)) n))
       :samples    n})))

;; ── Fold analysis ───────────────────────────────────────────

(defn fold-pair
  "The fold partner of position p: (N-1) - p."
  ^long [^long p]
  (- (dec N) p))

(defn fold-coord
  "Coordinate of fold point in a given dims."
  [dims]
  (idx->coord dims half-N))

(defn fold-partner-coord
  "Coordinate of the fold partner of position p."
  [dims ^long p]
  (idx->coord dims (fold-pair p)))

(defn axes-shared
  "Which axis indices have equal values between two coords?"
  [c1 c2]
  (vec (keep-indexed (fn [i [a b]] (when (= a b) i))
                     (map vector c1 c2))))

;; ── Aleph-tav positions ─────────────────────────────────────

(defn find-aleph-tavs
  "Find all positions where aleph-tav (את) appears as consecutive letters.
   Returns vec of starting positions."
  [s]
  (let [stream ^bytes (:stream s)
        n (dec (alength stream))
        aleph-idx (byte 0)   ;; א = index 0
        tav-idx   (byte 21)  ;; ת = index 21
        results (transient [])]
    (dotimes [i n]
      (when (and (= aleph-idx (aget stream i))
                 (= tav-idx   (aget stream (inc i))))
        (conj! results i)))
    (persistent! results)))

(defn aleph-tav-fold-analysis
  "For each aleph-tav, check if its fold partner shares coordinates.
   Returns summary: how many fold pairs share each axis."
  [dims s]
  (let [ats (find-aleph-tavs s)
        k (count dims)
        axis-shares (long-array k)]
    (doseq [at-pos ats]
      (let [c1 (idx->coord dims at-pos)
            c2 (idx->coord dims (fold-pair at-pos))
            shared (axes-shared c1 c2)]
        (doseq [ax shared]
          (aset axis-shares ax (inc (aget axis-shares ax))))))
    {:total-ats   (count ats)
     :axis-shares (vec axis-shares)
     :axis-pcts   (mapv #(when (pos? (count ats))
                           (double (/ % (count ats))))
                        (vec axis-shares))}))

;; ── ELS at stride values ────────────────────────────────────

(defn torah-letters-vec
  "Cached Torah letter vector for ELS searches."
  []
  (vec (oshb/torah-letters)))

(defn els-at-stride
  "Count occurrences of a word at a given stride (both directions).
   Returns {:forward :reverse :total}."
  [letters-vec word ^long stride]
  (let [fwd (count (els/search letters-vec word stride))
        rev (count (els/search letters-vec word (- stride)))]
    {:forward fwd :reverse rev :total (+ fwd rev)}))

(defn stride-els-survey
  "For a set of dims, find Torah (תורה) and YHWH (יהוה) at each stride value.
   Returns map of {stride -> {:torah {...} :yhwh {...}}}."
  [dims letters-vec]
  (let [strides (compute-strides dims)
        all-skips (distinct (concat dims strides))]
    (into (sorted-map)
          (for [skip all-skips
                :when (and (> skip 1) (< skip 100000))]
            [skip {:torah (els-at-stride letters-vec "תורה" skip)
                   :yhwh  (els-at-stride letters-vec "יהוה" skip)}]))))

;; ── Output helpers ──────────────────────────────────────────

(defn format-dims [dims]
  (str/join " × " dims))

(defn save-results!
  "Write results to data/experiments/ as both .edn and -output.txt."
  [base-name data output-str]
  (let [edn-path (str "data/experiments/" base-name ".edn")
        txt-path (str "data/experiments/" base-name "-output.txt")]
    (io/make-parents edn-path)
    (spit edn-path (pr-str data))
    (spit txt-path output-str)))

;; ── Decomposition enumeration (from 135) ────────────────────

(defn divisors [n]
  (let [limit (long (Math/sqrt n))]
    (->> (range 1 (inc limit))
         (filter #(zero? (rem n %)))
         (mapcat (fn [d] (if (= d (/ n d)) [d] [d (/ n d)])))
         sort vec)))

(defn decompositions-k
  "All ordered k-tuples (a₁ ≤ ... ≤ aₖ) where product = n, all > 1."
  [n k]
  (cond
    (= k 1) (if (> n 1) [[n]] [])
    (= k 2) (let [divs (divisors n)]
              (->> divs
                   (filter #(and (> % 1)
                                 (<= % (long (Math/sqrt n)))
                                 (> (/ n %) 1)))
                   (mapv (fn [d] [d (/ n d)]))))
    :else
    (let [divs (divisors n)
          upper (long (Math/pow n (/ 1.0 k)))
          results (atom [])]
      (doseq [a divs
              :when (and (> a 1) (<= a upper))]
        (let [rest-n (/ n a)
              sub-decomps (decompositions-k rest-n (dec k))]
          (doseq [sub sub-decomps
                  :when (<= a (first sub))]
            (swap! results conj (vec (cons a sub))))))
      (sort @results))))
