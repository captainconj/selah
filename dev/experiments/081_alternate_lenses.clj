(ns experiments.081-alternate-lenses
  "Experiment 081: Alternate Lenses — Every 4D Factorization of 304,850

   304,850 = 2 × 5² × 7 × 13 × 67

   The canonical lens [7 50 13 67] is one of many four-factor decompositions.
   Each decomposition is a different way to fold the same letter stream into 4D.

   FINDINGS
   ========
   1. UNIQUENESS: 123 total decompositions, 41 non-trivial (all dims > 1).
      Exactly ONE contains all three of {7, 13, 67}: the canonical [7 50 13 67].
      The 50 is forced by arithmetic.

   2. ALEPH-TAV BOUNDARIES: Of 41 non-trivial lenses, 21 have clean axis-0
      (את never crosses). Every lens with 7 as axis-0 is clean. Lenses with
      5 as axis-0 leak (1 crossing). The את knows where the 7 boundary is.

   3. FOLD DEPENDENCE: The fold result depends ONLY on dim-0. Internal
      arrangement of remaining factors is irrelevant.
        dim-0=7: 4 self-mirrors, 11.5% ÷7, 26.9% ÷13 (3.5× chance)
        dim-0=5: 7 self-mirrors, 15.4% ÷7, 26.9% ÷13
        dim-0=2: 0 self-mirrors, 3.8% ÷7, 7.7% ÷13 (chance)

   4. CENTERS: Most non-trivial centers land in Leviticus 8-11.
      The canonical → Lev 8:35 ('seven days').
      [10×13×35×67] → Lev 16:29 (Yom Kippur).

   5. CONCLUSION: Alternate lenses fold correctly but say nothing.
      The canonical lens is unique because every axis means something."
  (:require [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [selah.gematria :as gem]
            [selah.els.engine :as els]
            [clojure.string :as str]
))

;; ── Factorization enumeration ────────────────────────────

(def N 304850)

(defn divisors
  "All divisors of n, sorted."
  [n]
  (sort (for [i (range 1 (inc (int (Math/sqrt n))))
              :when (zero? (mod n i))
              d [i (/ n i)]]
          d)))

(defn four-factor-decompositions
  "All unordered 4-tuples [a b c d] with a×b×c×d = n, a ≤ b ≤ c ≤ d."
  [n]
  (let [divs (divisors n)]
    (for [a divs
          :when (<= a (int (Math/pow n 0.25)))
          :let [n2 (/ n a)]
          b (divisors n2)
          :when (<= a b)
          :when (<= b (int (Math/pow n2 (/ 1.0 3))))
          :let [n3 (/ n2 b)]
          c (divisors n3)
          :when (<= b c)
          :let [d (/ n3 c)]
          :when (<= c d)
          :when (pos? d)]
      [a b c d])))

;; ── Aleph-Tav boundary analysis ─────────────────────────

(def aleph-idx (get coords/char->idx \א))
(def tav-idx (get coords/char->idx \ת))

(defn find-et-positions
  "Find all consecutive את positions in the stream."
  [s]
  (let [stream ^bytes (:stream s)
        n (alength stream)]
    (loop [i 0 acc (transient [])]
      (if (>= i (dec n))
        (persistent! acc)
        (if (and (= (aget stream i) aleph-idx)
                 (= (aget stream (inc i)) tav-idx))
          (recur (inc i) (conj! acc i))
          (recur (inc i) acc))))))

(defn et-boundary-stats
  "For a given lens [a b c d], count את that straddle each axis boundary.
   An את straddles axis-k if the aleph and tav have different k-coordinates."
  [et-positions dims]
  (coords/with-dims dims
    (let [strides (coords/strides)
          labels ["a" "b" "c" "d"]
          counts (long-array 4)]
      (doseq [pos et-positions]
        (let [c1 (coords/idx->coord pos)
              c2 (coords/idx->coord (inc pos))]
          (dotimes [k 4]
            (when (not= (aget c1 k) (aget c2 k))
              (aset counts k (inc (aget counts k)))))))
      (zipmap labels (vec counts)))))

;; ── Center verse ────────────────────────────────────────

(defn center-info
  "What verse sits at the true 4D center under this lens?
   The center is the midpoint of each axis: (a/2, b/2, c/2, d/2)."
  [s dims]
  (coords/with-dims dims
    (let [center-coord (mapv #(quot % 2) dims)
          pos (apply coords/coord->idx center-coord)
          v (coords/verse-at s pos)]
      {:position pos
       :coord (vec center-coord)
       :verse (str (:book v) " " (:ch v) ":" (:vs v))})))

;; ── Fold mirror analysis ────────────────────────────────

(defn make-mirror-fn
  "Create a mirror function for axis k under the current dims.
   Mirrors: coord[k] → (max-k - coord[k])."
  [k]
  (fn ^long [^long i]
    (let [c (coords/idx->coord i)
          max-k (dec (nth @coords/*dims* k))]
      (case (int k)
        0 (coords/coord->idx (- max-k (aget c 0)) (aget c 1) (aget c 2) (aget c 3))
        1 (coords/coord->idx (aget c 0) (- max-k (aget c 1)) (aget c 2) (aget c 3))
        2 (coords/coord->idx (aget c 0) (aget c 1) (- max-k (aget c 2)) (aget c 3))
        3 (coords/coord->idx (aget c 0) (aget c 1) (aget c 2) (- max-k (aget c 3)))))))

(defn book-offset []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        lengths (map #(count (oshb/book-letters %)) books)]
    (zipmap books (reductions + 0 (butlast lengths)))))

(defn find-milemarkers
  "Find Torah@50 and YHWH@7 milemarkers. Returns [{:book :word :skip :global-start}]."
  [s]
  (let [offsets (book-offset)
        results (atom [])]
    ;; Genesis: תורה at skip +50
    (doseq [hit (els/search (vec (oshb/book-letters "Genesis")) "תורה" 50)]
      (swap! results conj {:book "Genesis" :word "תורה" :skip 50
                           :global-start (+ (offsets "Genesis") (:start hit))}))
    ;; Exodus: תורה at skip +50
    (doseq [hit (els/search (vec (oshb/book-letters "Exodus")) "תורה" 50)]
      (swap! results conj {:book "Exodus" :word "תורה" :skip 50
                           :global-start (+ (offsets "Exodus") (:start hit))}))
    ;; Leviticus: יהוה at skip +7 and -7
    (doseq [hit (els/search (vec (oshb/book-letters "Leviticus")) "יהוה" 7)]
      (swap! results conj {:book "Leviticus" :word "יהוה" :skip 7
                           :global-start (+ (offsets "Leviticus") (:start hit))}))
    (doseq [hit (els/search (vec (oshb/book-letters "Leviticus")) "יהוה" -7)]
      (swap! results conj {:book "Leviticus" :word "יהוה" :skip -7
                           :global-start (+ (offsets "Leviticus") (:start hit))}))
    ;; Numbers: הרות at skip +50
    (doseq [hit (els/search (vec (oshb/book-letters "Numbers")) "הרות" 50)]
      (swap! results conj {:book "Numbers" :word "הרות" :skip 50
                           :global-start (+ (offsets "Numbers") (:start hit))}))
    ;; Deuteronomy: הרות at skip +50
    (doseq [hit (els/search (vec (oshb/book-letters "Deuteronomy")) "הרות" 50)]
      (swap! results conj {:book "Deuteronomy" :word "הרות" :skip 50
                           :global-start (+ (offsets "Deuteronomy") (:start hit))}))
    @results))

(defn fold-mirror-test
  "For a given lens, fold along axis k, check milemarker mirrors.
   Returns {:self-mirrors :div7-rate :div13-rate :notable}."
  [s markers dims k]
  (coords/with-dims dims
    (let [mirror-fn (make-mirror-fn k)
          dim-k (nth dims k)
          analyses
          (keep
           (fn [m]
             (let [i (:global-start m)
                   skip (:skip m)
                   word (:word m)
                   els-pos (mapv #(+ i (* % skip)) (range (count word)))
                   valid? (every? #(and (>= % 0) (< % (:n s))) els-pos)]
               (when valid?
                 (let [mirror-pos (mapv mirror-fn els-pos)
                       mirror-valid? (every? #(and (>= % 0) (< % (:n s))) mirror-pos)]
                   (when mirror-valid?
                     (let [orig-gv (reduce + (map #(coords/gv-at s %) els-pos))
                           mir-gv (reduce + (map #(coords/gv-at s %) mirror-pos))
                           pair-gv (+ orig-gv mir-gv)
                           mir-start (mirror-fn i)
                           self? (= i mir-start)]
                       {:word word :skip skip :book (:book m)
                        :pair-gv pair-gv
                        :self? self?
                        :div7 (zero? (mod pair-gv 7))
                        :div13 (zero? (mod pair-gv 13))}))))))
           markers)
          n (count analyses)]
      (when (pos? n)
        {:axis k
         :dim-k dim-k
         :count n
         :self-mirrors (count (filter :self? analyses))
         :div7-count (count (filter :div7 analyses))
         :div7-rate (double (/ (count (filter :div7 analyses)) n))
         :div13-count (count (filter :div13 analyses))
         :div13-rate (double (/ (count (filter :div13 analyses)) n))
         :notable (filter #(or (:self? %) (and (:div7 %) (:div13 %))) analyses)}))))

;; ── Parts ───────────────────────────────────────────────

(defn part-1-enumerate []
  (println "================================================================")
  (println "  PART 1: ENUMERATE ALL FOUR-FACTOR DECOMPOSITIONS")
  (println "================================================================")
  (println)
  (let [decomps (four-factor-decompositions N)]
    (println (format "  304,850 = 2 × 5² × 7 × 13 × 67"))
    (println (format "  Total four-factor decompositions: %d" (count decomps)))
    (println)
    (println (format "  %-5s  %-20s  %-8s  %s" "#" "Factors" "Max dim" "Contains 7,13,67?"))
    (println (apply str (repeat 65 "-")))
    (doseq [[i d] (map-indexed vector decomps)]
      (let [has7 (some #{7} d)
            has13 (some #{13} d)
            has67 (some #{67} d)
            all3 (and has7 has13 has67)
            tag (cond all3 "*** YES ***"
                      (and has7 has13) "7,13"
                      (and has7 has67) "7,67"
                      (and has13 has67) "13,67"
                      has7 "7"
                      has13 "13"
                      has67 "67"
                      :else "—")]
        (println (format "  %3d.  [%-18s]  %6d  %s"
                         (inc i)
                         (str/join " × " d)
                         (apply max d)
                         tag))))
    (println)
    (let [with-all3 (filter (fn [d] (and (some #{7} d) (some #{13} d) (some #{67} d))) decomps)]
      (println (format "  Decompositions containing all three of {7, 13, 67}: %d" (count with-all3)))
      (doseq [d with-all3]
        (println (format "    %s" (str/join " × " d)))))
    (println)
    decomps))

(defn part-2-centers [s decomps]
  (println "================================================================")
  (println "  PART 2: CENTER VERSE UNDER EACH LENS")
  (println "================================================================")
  (println)
  (println "  The 4D center = midpoint of each axis: (a/2, b/2, c/2, d/2).")
  (println "  Canonical: (3,25,6,33) → Lev 8:35 'seven days'.")
  (println)
  ;; Only show non-trivial lenses (no dimension = 1)
  (let [nontrivial (filter #(every? (fn [d] (> d 1)) %) decomps)]
    (println (format "  Non-trivial lenses (all dims > 1): %d of %d" (count nontrivial) (count decomps)))
    (println)
    (doseq [dims nontrivial]
      (let [info (center-info s dims)
            canonical? (= (set dims) #{7 50 13 67})]
        (println (format "  %s[%-18s]  center %-20s  pos %,7d  → %s"
                         (if canonical? "▶ " "  ")
                         (str/join " × " dims)
                         (str (:coord info))
                         (:position info)
                         (:verse info))))))
  (println))

(defn part-3-aleph-tav [s et-positions decomps]
  (println "================================================================")
  (println "  PART 3: ALEPH-TAV BOUNDARY BEHAVIOR")
  (println "================================================================")
  (println)
  (println "  Canonical: את stitches d and b boundaries, never crosses a.")
  (println "  How does את behave under each lens?")
  (println)
  (println (format "  %-22s  %6s  %6s  %6s  %6s  %s"
                   "Lens" "ax-0" "ax-1" "ax-2" "ax-3" "Pattern"))
  (println (apply str (repeat 80 "-")))
  (let [results
        (mapv
         (fn [dims]
           (let [stats (et-boundary-stats et-positions dims)
                 vals [(get stats "a") (get stats "b") (get stats "c") (get stats "d")]
                 zero-axes (count (filter zero? vals))
                 ;; Pattern: which axes does את cross?
                 pattern (str/join ""
                                  (map-indexed
                                   (fn [i v] (if (zero? v) "·" (str i)))
                                   vals))]
             (println (format "  [%-18s]  %6d  %6d  %6d  %6d  %s"
                              (str/join " × " dims)
                              (nth vals 0) (nth vals 1) (nth vals 2) (nth vals 3)
                              pattern))
             {:dims dims :stats stats :vals vals
              :zero-count zero-axes :pattern pattern}))
         decomps)]
    (println)
    ;; Focus on non-trivial lenses
    (let [nontrivial (filter #(every? (fn [d] (> d 1)) (:dims %)) results)
          respects-a0 (filter #(zero? (first (:vals %))) nontrivial)]
      (println (format "  Non-trivial lenses (all dims > 1): %d" (count nontrivial)))
      (println (format "  Of those, את NEVER crosses axis-0: %d" (count respects-a0)))
      (println)
      (println "  Non-trivial lenses detail:")
      (println (format "  %-22s  %6s  %6s  %6s  %6s  %s"
                       "Lens" "ax-0" "ax-1" "ax-2" "ax-3" "Note"))
      (println (apply str (repeat 72 "-")))
      (doseq [r nontrivial]
        (let [canonical? (= (vec (:dims r)) [7 50 13 67])
              vals (:vals r)]
          (println (format "  %s[%-18s]  %6d  %6d  %6d  %6d  %s"
                           (if canonical? "▶" " ")
                           (str/join " × " (:dims r))
                           (nth vals 0) (nth vals 1) (nth vals 2) (nth vals 3)
                           (if (zero? (first vals)) "✓ axis-0 clean" ""))))))
    (println)
    results))

(defn part-4-fold-milemarkers [s markers decomps]
  (println "================================================================")
  (println "  PART 4: FOLD MILEMARKER MIRRORS UNDER EACH LENS")
  (println "================================================================")
  (println)
  (println "  For each non-trivial lens, fold along axis-0.")
  (println "  Lenses with axis-0=1 are trivial (identity fold) — skipped.")
  (println "  Canonical [7×50×13×67]: a-fold Gen1:1→Shema, pair GVs ÷7 at 27%.")
  (println)
  ;; Only lenses where axis-0 > 1 (non-trivial fold)
  (let [nontrivial (filter #(> (first %) 1) decomps)]
    (println (format "  Non-trivial folds (axis-0 > 1): %d of %d"
                     (count nontrivial) (count decomps)))
    (println)
    (println (format "  %-22s  %4s  %5s  %7s  %7s  %s"
                     "Lens" "N" "Self" "÷7" "÷13" "Notable"))
    (println (apply str (repeat 80 "-")))
    (let [results
          (keep
           (fn [dims]
             (let [r (fold-mirror-test s markers dims 0)]
               (when r
                 (let [canonical? (= (vec dims) [7 50 13 67])]
                   (println (format "  %s[%-18s]  %4d  %5d  %5d/%d  %5d/%d  %s"
                                    (if canonical? "▶" " ")
                                    (str/join " × " dims)
                                    (:count r) (:self-mirrors r)
                                    (:div7-count r) (:count r)
                                    (:div13-count r) (:count r)
                                    (if (seq (:notable r))
                                      (str/join ", " (map #(str (:word %) "@" (:skip %)) (:notable r)))
                                      "—"))))
                 (assoc r :dims dims))))
           nontrivial)]
      (println)
      ;; Rank by combined signal
      (println "  Top 10 by ÷7 rate (excluding trivial):")
      (doseq [r (take 10 (reverse (sort-by :div7-rate results)))]
        (let [canonical? (= (vec (:dims r)) [7 50 13 67])]
          (println (format "    %s[%-18s]  ÷7 = %4.1f%%  ÷13 = %4.1f%%  self = %d"
                           (if canonical? "▶" " ")
                           (str/join " × " (:dims r))
                           (* 100 (:div7-rate r))
                           (* 100 (:div13-rate r))
                           (:self-mirrors r)))))
      (println)
      results)))

(defn part-5-summary [et-results fold-results]
  (println "================================================================")
  (println "  PART 5: SUMMARY — IS THE CANONICAL LENS UNIQUE?")
  (println "================================================================")
  (println)
  ;; Headline finding
  (println "  FINDING 1: UNIQUENESS")
  (println "  Of 123 four-factor decompositions of 304,850,")
  (println "  exactly ONE contains all three of {7, 13, 67}:")
  (println "    [7 × 50 × 13 × 67]")
  (println "  The 50 (jubilee) is forced. There is no other choice.")
  (println)

  ;; את analysis
  (let [nontrivial-et (filter #(every? (fn [d] (> d 1)) (:dims %)) et-results)
        ;; Among non-trivial lenses, which have 0 crossings on axis-0?
        et-zero (filter #(zero? (first (:vals %))) nontrivial-et)
        ;; How special is the canonical?
        canonical-et (first (filter #(= (vec (:dims %)) [7 50 13 67]) et-results))]
    (println "  FINDING 2: ALEPH-TAV BOUNDARIES")
    (println (format "  Non-trivial lenses where את never crosses axis-0: %d of %d"
                     (count et-zero) (count nontrivial-et)))
    (when canonical-et
      (println (format "  Canonical: axis-0(7)=%d  axis-1(50)=%d  axis-2(13)=%d  axis-3(67)=%d"
                       (nth (:vals canonical-et) 0)
                       (nth (:vals canonical-et) 1)
                       (nth (:vals canonical-et) 2)
                       (nth (:vals canonical-et) 3)))
      (println "  את stitches d(67) and b(50) boundaries but NEVER crosses a(7).")))
  (println)

  ;; Fold analysis
  (let [canonical (first (filter #(= (vec (:dims %)) [7 50 13 67]) fold-results))
        ;; Only compare non-trivial folds
        scored (map (fn [r] (assoc r :score (+ (:div7-rate r) (:div13-rate r))))
                    fold-results)
        top (first (reverse (sort-by :score scored)))]
    (println "  FINDING 3: FOLD MIRRORS")
    (when canonical
      (println (format "  Canonical [7×50×13×67] a-fold:"))
      (println (format "    ÷7 rate:  %.1f%%  (expect ~14.3%%)" (* 100 (:div7-rate canonical))))
      (println (format "    ÷13 rate: %.1f%%  (expect ~7.7%%)" (* 100 (:div13-rate canonical))))
      (println (format "    Self-mirrors: %d" (:self-mirrors canonical))))
    (println)
    (when top
      (println (format "  Best non-trivial: [%s]" (str/join " × " (:dims top))))
      (println (format "    ÷7 rate:  %.1f%%  ÷13 rate: %.1f%%  self: %d"
                       (* 100 (:div7-rate top))
                       (* 100 (:div13-rate top))
                       (:self-mirrors top)))))
  (println))

;; ── Main ────────────────────────────────────────────────

(defn -main []
  (println "╔══════════════════════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 081: ALTERNATE LENSES                          ║")
  (println "║  Every four-factor decomposition of 304,850                ║")
  (println "╚══════════════════════════════════════════════════════════════╝")
  (println)
  (let [s (coords/space)
        decomps (part-1-enumerate)
        _ (part-2-centers s decomps)
        et-positions (find-et-positions s)
        _ (println (format "  Found %,d את in stream.\n" (count et-positions)))
        et-results (part-3-aleph-tav s et-positions decomps)
        markers (find-milemarkers s)
        _ (println (format "  Found %d ELS milemarkers.\n" (count markers)))
        fold-results (part-4-fold-milemarkers s markers decomps)]
    (part-5-summary et-results fold-results))
  (println "Done.")
  (println "selah."))
