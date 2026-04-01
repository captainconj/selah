(ns experiments.097-crossbeam-protein
  "The cross-beam read as codons: 67 = 22×3 + 1.
   Mirror-pair symmetry, the 506 bridge, permutation tests."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [clojure.edn :as edn]))

;; ── The 22 codons ──────────────────────────────────────────

(defn horizontal-beam-letters []
  (let [s (c/space)]
    (mapv #(c/letter-at s %) (for [d (range 67)] (c/coord->idx 3 25 6 d)))))

(defn triplets
  "Split a letter sequence into triplets + remainder."
  [letters]
  (let [n (count letters)
        full (* 3 (quot n 3))
        trips (mapv vec (partition 3 (take full letters)))
        rem (vec (drop full letters))]
    {:triplets trips :remainder rem}))

(defn triplet-gv [trip]
  (reduce + (map g/letter-value trip)))

(defn codon-table
  "Build the 22-codon table from the horizontal beam."
  []
  (let [letters (horizontal-beam-letters)
        {:keys [triplets remainder]} (triplets letters)]
    {:codons (mapv (fn [i trip]
                     {:index (inc i)
                      :letters (apply str trip)
                      :gv (triplet-gv trip)})
                   (range) triplets)
     :remainder {:letters (apply str remainder)
                 :gv (reduce + (map g/letter-value remainder))}
     :total-letters (count letters)
     :total-codons (count triplets)}))

;; ── Mirror pairs ───────────────────────────────────────────

(defn mirror-pairs
  "Mirror-pair sums: gv[i] + gv[n-1-i] for i in 0..n/2-1."
  [gvs]
  (let [n (count gvs)]
    (mapv (fn [i]
            {:pair [(inc i) (- n i)]
             :mercy (nth gvs i)
             :truth (nth gvs (- n 1 i))
             :sum (+ (nth gvs i) (nth gvs (- n 1 i)))})
          (range (quot n 2)))))

(defn max-consecutive-run
  "Longest run of consecutive equal sums in mirror pairs."
  [pairs]
  (let [sums (mapv :sum pairs)]
    (loop [i 1, current 1, best 1]
      (if (>= i (count sums))
        best
        (if (= (nth sums i) (nth sums (dec i)))
          (recur (inc i) (inc current) (max best (inc current)))
          (recur (inc i) 1 best))))))

(defn has-k-consecutive?
  "True if k consecutive mirror pairs share the same sum."
  [pairs k]
  (let [sums (mapv :sum pairs)]
    (some (fn [i]
            (apply = (subvec sums i (+ i k))))
          (range (- (count sums) (dec k))))))

(defn find-bridges
  "Find all runs of 3+ consecutive equal-sum mirror pairs."
  [pairs]
  (let [sums (mapv :sum pairs)]
    (loop [i 0, bridges []]
      (if (>= i (- (count sums) 2))
        bridges
        (if (= (nth sums i) (nth sums (inc i)) (nth sums (+ i 2)))
          (let [sum-val (nth sums i)
                ;; extend to find full run length
                end (loop [j (+ i 3)]
                      (if (and (< j (count sums)) (= (nth sums j) sum-val))
                        (recur (inc j))
                        j))]
            (recur end (conj bridges {:start (inc i) :end end :sum sum-val :length (- end i)})))
          (recur (inc i) bridges))))))

;; ── Permutation test ───────────────────────────────────────

(defn permutation-test
  "Shuffle the 22 GV values n-trials times.
   Test: how often do 3+ consecutive mirror pairs share a sum?"
  [gvs n-trials]
  (let [results (pmap (fn [_seed]
                        (let [shuffled (shuffle gvs)
                              pairs (mirror-pairs shuffled)
                              max-run (max-consecutive-run pairs)
                              bridges (find-bridges pairs)]
                          {:max-run max-run
                           :has-3 (>= max-run 3)
                           :bridges bridges}))
                      (range n-trials))
        count-3plus (count (filter :has-3 results))
        max-runs (mapv :max-run results)
        run-freq (frequencies max-runs)]
    {:n-trials n-trials
     :count-3plus count-3plus
     :p-value-3plus (double (/ count-3plus n-trials))
     :run-distribution (into (sorted-map) run-freq)
     :avg-max-run (double (/ (reduce + max-runs) n-trials))
     :bridges-found (filter #(seq (:bridges %)) results)}))

(defn permutation-test-specific
  "Test specifically for 3 consecutive pairs summing to target-sum,
   optionally flanked by flank-sum."
  [gvs n-trials target-sum flank-sum]
  (let [results (pmap (fn [_]
                        (let [shuffled (shuffle gvs)
                              pairs (mirror-pairs shuffled)
                              sums (mapv :sum pairs)
                              n (count sums)
                              ;; check for 3 consecutive = target
                              has-target (some (fn [i]
                                                 (and (= (nth sums i) target-sum)
                                                      (= (nth sums (inc i)) target-sum)
                                                      (= (nth sums (+ i 2)) target-sum)))
                                               (range (- n 2)))
                              ;; check flanking
                              has-flanked (some (fn [i]
                                                  (and (= (nth sums i) target-sum)
                                                       (= (nth sums (inc i)) target-sum)
                                                       (= (nth sums (+ i 2)) target-sum)
                                                       (or (and (> i 0) (= (nth sums (dec i)) flank-sum))
                                                           (and (< (+ i 3) n) (= (nth sums (+ i 3)) flank-sum)))))
                                                (range (- n 2)))]
                          {:has-target (boolean has-target)
                           :has-flanked (boolean has-flanked)}))
                      (range n-trials))
        count-target (count (filter :has-target results))
        count-flanked (count (filter :has-flanked results))]
    {:n-trials n-trials
     :target-sum target-sum
     :flank-sum flank-sum
     :count-target count-target
     :p-target (double (/ count-target n-trials))
     :count-flanked count-flanked
     :p-flanked (double (/ count-flanked n-trials))}))

;; ── Factor analysis ────────────────────────────────────────

(defn factorize [n]
  (loop [n n, d 2, factors []]
    (cond
      (> (* d d) n) (if (> n 1) (conj factors n) factors)
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

(defn pair-analysis
  "Analyze each mirror pair for notable factorizations."
  [pairs]
  (mapv (fn [{:keys [pair sum] :as p}]
          (let [factors (factorize sum)]
            (assoc p
                   :factors factors
                   :div-7 (zero? (mod sum 7))
                   :div-13 (zero? (mod sum 13))
                   :div-26 (zero? (mod sum 26))
                   :is-square (let [r (int (Math/sqrt sum))] (= sum (* r r)))
                   :is-triangular (let [;; T(n) = n(n+1)/2, so n = (-1+sqrt(1+8*sum))/2
                                        disc (+ 1 (* 8 sum))
                                        sq (int (Math/sqrt disc))]
                                    (and (= disc (* sq sq))
                                         (zero? (mod (dec sq) 2)))))))
        pairs))

;; ── Run ────────────────────────────────────────────────────

(comment
  ;; Initialize
  (c/space)

  ;; Build codon table
  (def ct (codon-table))
  (:total-codons ct) ;; => 22
  (:remainder ct)    ;; => {:letters "ת", :gv 400}

  ;; GV values
  (def gvs (mapv :gv (:codons ct)))
  ;; => [22 86 70 307 480 90 346 640 441 540 415 17 431 446 36 160 416 26 371 255 58 17]

  ;; Mirror pairs
  (def pairs (mirror-pairs gvs))
  (doseq [p pairs]
    (println (format "Pair %2d <-> %2d: %3d + %3d = %4d"
                     (first (:pair p)) (second (:pair p))
                     (:mercy p) (:truth p) (:sum p))))

  ;; The bridge
  (find-bridges pairs)
  ;; => [{:start 5, :end 8, :sum 506, :length 3}]

  ;; Pair analysis
  (def pa (pair-analysis pairs))
  (doseq [p pa]
    (println (format "Pair %2d <-> %2d: sum=%4d factors=%s%s%s"
                     (first (:pair p)) (second (:pair p))
                     (:sum p) (:factors p)
                     (if (:div-13 p) " ÷13" "")
                     (if (:is-square p) " SQUARE" ""))))

  ;; === PERMUTATION TEST 1: any 3+ consecutive ===
  (def perm1 (time (permutation-test gvs 100000)))
  ;; p-value, run distribution, etc.
  (:p-value-3plus perm1)
  (:run-distribution perm1)
  (:avg-max-run perm1)

  ;; === PERMUTATION TEST 2: specifically 506, flanked by 676 ===
  (def perm2 (time (permutation-test-specific gvs 100000 506 676)))
  ;; p-target: how often 3 consecutive = 506
  ;; p-flanked: how often 506-bridge flanked by 676=26²

  ;; === SAVE ===
  (spit "data/experiments/097/codon-table.edn" (pr-str ct))
  (spit "data/experiments/097/mirror-pairs.edn" (pr-str pa))
  (spit "data/experiments/097/permutation-general.edn"
        (pr-str (dissoc perm1 :bridges-found)))
  (spit "data/experiments/097/permutation-506.edn" (pr-str perm2))
  (spit "data/experiments/097/summary.edn"
        (pr-str {:codon-count 22
                 :remainder "ת"
                 :remainder-gv 400
                 :total-gv (reduce + gvs)
                 :bridge {:pairs [[5 18] [6 17] [7 16]]
                          :sum 506
                          :factorization "22 × 23"
                          :meaning "Hebrew letters × human chromosome pairs"}
                 :flank {:pair [8 15]
                         :sum 676
                         :factorization "26²"
                         :meaning "YHWH squared"}
                 :love-frame {:pair-1 {:pair [1 22] :sum 39 :factorization "3 × 13"}
                              :pair-3 {:pair [3 20] :sum 325 :factorization "25 × 13"}}
                 :permutation-p-3plus (:p-value-3plus perm1)
                 :permutation-p-506 (:p-target perm2)
                 :permutation-p-506-flanked-676 (:p-flanked perm2)
                 :permutation-n-trials 100000}))

  nil)
