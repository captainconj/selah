(ns experiments.061c-compound-folds
  "Compound folds — what happens when you mirror multiple axes simultaneously?

   Single folds mirror one axis:
     a-fold: a → 6-a
     b-fold: b → 49-b
     c-fold: c → 12-c
     d-fold: d → 66-d

   Compound folds mirror multiple axes at once:
     ab-fold: (a,b,c,d) → (6-a, 49-b, c, d)
     abcd-fold: (a,b,c,d) → (6-a, 49-b, 12-c, 66-d) = position 304,849 - i

   The full fold (abcd) maps position i to (304,849 - i) — reading backwards.
   This is the simplest compound fold. The question: do the intermediate
   compound folds reveal structure too?

   For each compound fold:
   1. Where does position 0 (first letter) map to? What verse?
   2. Where does position 304,849 (last letter) map to?
   3. How many את pairs mirror to את under this fold?
   4. Does the first תורה milemarker (position 5) mirror to anything interesting?

   FINDINGS
   ========

   1. THE a-FOLD DOMINATES את PRESERVATION.
      1,326 את → את under a-fold (11.1x expected). No other fold comes close.
      The seven-day axis is the natural symmetry axis for the aleph-tav.
      c-fold (4.6x) and d-fold (2.2x) also exceed chance. The b-axis (jubilee)
      shows no special preservation (~1x expected).

   2. FIXED POINTS LIVE IN {a,c,d} — NEVER b.
      Axis b (dim 50) is even, so no midpoint exists. The acd-fold has exactly
      50 fixed points — one per jubilee cycle. These 50 positions sit at
      a=3, c=6, d=33 with b varying: the center of three axes, free on the fourth.

   3. COMPLEMENTARY FOLDS ARE PERFECT DUALS.
      Every disjoint pair (X + Y = abcd) exhibits duality:
      X(first) = Y(last) and X(last) = Y(first). Always.
      Seven pairs: a+bcd, ab+cd, ac+bd, ad+bc, abc+d, abd+c, acd+b.

   4. THE ABCD FOLD (FULL REVERSAL).
      בראשית (pos 0-5) mirrors to the last word of Torah (pos 304,844-849).
      Positions 3-4 (ש and י) map to THEMSELVES — the shin-yod pair is
      palindromic across the entire Torah. Center (152,424/152,425) swaps
      within Leviticus 8:29 (ה ↔ ח). Overall same-letter rate: 6.37%,
      matching the 6.34% expected from letter frequencies.

   5. DEUTERONOMY 6 IS THE FOLD ATTRACTOR.
      The a-fold sends position 0 to Deuteronomy 6:1 (the Shema).
      The ad-fold sends it to Deuteronomy 6:2. Both ac-fold and acd-fold
      send it to Deuteronomy 6:18 ('do what is right and good'). Deuteronomy 6
      is a gravitational center for folds that include the a-axis."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Fold machinery ────────────────────────────────────────

(def max-coords
  "Maximum coordinate values: [6 49 12 66]"
  [6 49 12 66])

(def axis-labels [:a :b :c :d])

(defn fold-position
  "Mirror position i across the given set of axes.
   axes is a set of axis indices (0=a, 1=b, 2=c, 3=d)."
  [^long i axes]
  (let [coord (coords/idx->coord i)
        a (if (axes 0) (- 6  (aget coord 0)) (aget coord 0))
        b (if (axes 1) (- 49 (aget coord 1)) (aget coord 1))
        c (if (axes 2) (- 12 (aget coord 2)) (aget coord 2))
        d (if (axes 3) (- 66 (aget coord 3)) (aget coord 3))]
    (coords/coord->idx a b c d)))

(defn fold-name
  "Human-readable name for a fold specified by axis set."
  [axes]
  (apply str (map #(name (axis-labels %)) (sort axes))))

;; Enumerate all fold combinations manually (no extra deps needed)
(def single-folds
  [{:axes #{0} :name "a"}
   {:axes #{1} :name "b"}
   {:axes #{2} :name "c"}
   {:axes #{3} :name "d"}])

(def double-folds
  [{:axes #{0 1} :name "ab"}
   {:axes #{0 2} :name "ac"}
   {:axes #{0 3} :name "ad"}
   {:axes #{1 2} :name "bc"}
   {:axes #{1 3} :name "bd"}
   {:axes #{2 3} :name "cd"}])

(def triple-folds
  [{:axes #{0 1 2} :name "abc"}
   {:axes #{0 1 3} :name "abd"}
   {:axes #{0 2 3} :name "acd"}
   {:axes #{1 2 3} :name "bcd"}])

(def quad-fold
  [{:axes #{0 1 2 3} :name "abcd"}])

(def all-folds
  (concat single-folds double-folds triple-folds quad-fold))

;; ── את scanner ────────────────────────────────────────────

(defn find-et-positions
  "Find all positions where א is followed by ת (consecutive).
   Returns vector of positions (the index of the א)."
  [s]
  (let [n (dec (:n s))
        aleph-idx (coords/char->idx \א)   ;; 0
        tav-idx   (coords/char->idx \ת)    ;; 21
        stream    ^bytes (:stream s)]
    (loop [i 0, acc (transient [])]
      (if (>= i n)
        (persistent! acc)
        (recur (inc i)
               (if (and (== (aget stream i) aleph-idx)
                        (== (aget stream (inc i)) tav-idx))
                 (conj! acc i)
                 acc))))))

(defn et-mirror-count
  "Count how many את pairs mirror to את under a given fold.
   An את at position i (א at i, ת at i+1) mirrors if:
   the fold of i has א and the fold of (i+1) has ת — in either order."
  [s et-positions axes]
  (let [stream ^bytes (:stream s)
        aleph-idx (byte 0)
        tav-idx   (byte 21)
        n (:n s)]
    (reduce
     (fn [acc i]
       (let [mi  (fold-position i axes)
             mi1 (fold-position (inc i) axes)]
         ;; Check: mirror has את (same order) at consecutive positions
         ;; The folded positions may not be consecutive, so check both individually
         (if (and (>= mi 0) (< mi n) (>= mi1 0) (< mi1 n)
                  (== (aget stream mi) aleph-idx)
                  (== (aget stream mi1) tav-idx))
           (inc acc)
           acc)))
     0
     et-positions)))

(defn et-mirror-count-reversed
  "Count how many את pairs mirror to תא (reversed) under a given fold."
  [s et-positions axes]
  (let [stream ^bytes (:stream s)
        aleph-idx (byte 0)
        tav-idx   (byte 21)
        n (:n s)]
    (reduce
     (fn [acc i]
       (let [mi  (fold-position i axes)
             mi1 (fold-position (inc i) axes)]
         (if (and (>= mi 0) (< mi n) (>= mi1 0) (< mi1 n)
                  (== (aget stream mi) tav-idx)
                  (== (aget stream mi1) aleph-idx))
           (inc acc)
           acc)))
     0
     et-positions)))

(defn et-at-mirror-site
  "Count how many fold targets themselves contain an את.
   That is: at position fold(i), is there an את starting there?"
  [s et-positions axes et-set]
  (let [n (:n s)]
    (reduce
     (fn [acc i]
       (let [mi (fold-position i axes)]
         (if (and (>= mi 0) (< mi n) (et-set mi))
           (inc acc)
           acc)))
     0
     et-positions)))

;; ── Verse description ─────────────────────────────────────

(defn verse-str [s i]
  (let [v (coords/verse-at s i)]
    (str (:book v) " " (:ch v) ":" (:vs v))))

(defn coord-str [i]
  (let [c (coords/idx->coord i)]
    (format "[%d %d %d %d]" (aget c 0) (aget c 1) (aget c 2) (aget c 3))))

;; ── Torah milemarker check ─────────────────────────────────

(defn letters-at-positions
  "Get a string of letters at arbitrary positions."
  [s positions]
  (apply str (map #(coords/letter-at s %) positions)))

(defn check-torah-at-mirror
  "Check what the first תורה milemarker (starting at position 5, skip 50)
   maps to under a given fold. Return the mirrored letters."
  [s axes]
  (let [torah-positions [5 55 105 155]  ;; ת at 5, ו at 55, ר at 105, ה at 155
        mirrored (mapv #(fold-position % axes) torah-positions)
        n (:n s)
        valid? (every? #(and (>= % 0) (< % n)) mirrored)]
    (when valid?
      {:original-positions torah-positions
       :original-letters   (letters-at-positions s torah-positions)
       :mirror-positions   mirrored
       :mirror-letters     (letters-at-positions s mirrored)
       :mirror-verse       (verse-str s (first mirrored))
       :mirror-coord       (coord-str (first mirrored))})))

;; ── Analysis per fold ─────────────────────────────────────

(defn analyze-fold
  "Full analysis of one fold type."
  [s et-positions et-set fold-spec]
  (let [axes (:axes fold-spec)
        name (:name fold-spec)
        ;; Position 0 → fold
        pos0-mirror (fold-position 0 axes)
        ;; Last position → fold
        last-pos    (dec coords/total-letters)
        posN-mirror (fold-position last-pos axes)
        ;; את analysis
        et-same     (et-at-mirror-site s et-positions axes et-set)
        ;; Torah milemarker
        torah-info  (check-torah-at-mirror s axes)
        ;; Is this fold an involution? (fold of fold = identity)
        ;; All our folds are involutions by construction (mirror of mirror = original)
        ;; Verify for position 0
        roundtrip   (fold-position pos0-mirror axes)]
    {:name           name
     :axes           axes
     :num-axes       (count axes)
     :pos0-mirror    pos0-mirror
     :pos0-verse     (verse-str s pos0-mirror)
     :pos0-coord     (coord-str pos0-mirror)
     :pos0-letter    (coords/letter-at s pos0-mirror)
     :posN-mirror    posN-mirror
     :posN-verse     (verse-str s posN-mirror)
     :posN-coord     (coord-str posN-mirror)
     :posN-letter    (coords/letter-at s posN-mirror)
     :et-mirrors     et-same
     :torah-mirror   torah-info
     :roundtrip-ok   (== roundtrip 0)}))

;; ── Fixed-point analysis ──────────────────────────────────

(defn count-fixed-points
  "How many positions are fixed under this fold?"
  [axes]
  ;; A position is fixed when, for each folded axis, the coordinate
  ;; is exactly at the midpoint. For even-length axes (dim-1 is odd),
  ;; there are no fixed points on that axis. For odd-length axes
  ;; (dim-1 is even), the midpoint = (dim-1)/2.
  ;; a: dim=7, max=6, midpoint=3 — exists
  ;; b: dim=50, max=49, midpoint=24.5 — no fixed point
  ;; c: dim=13, max=12, midpoint=6 — exists
  ;; d: dim=67, max=66, midpoint=33 — exists
  (let [dims [7 50 13 67]
        maxes [6 49 12 66]]
    (if (some #(and (axes %) (odd? (nth maxes %))) (range 4))
      ;; If any folded axis has even dimension (odd max), no fixed points
      0
      ;; Otherwise, product of free-axis dimensions
      (reduce * (map-indexed
                 (fn [i dim]
                   (if (axes i) 1 dim))
                 dims)))))

;; ── Letter frequency for expected values ─────────────────

(defn letter-frequencies
  "Count frequency of each byte index in the stream."
  [s]
  (let [stream ^bytes (:stream s)
        n (:n s)
        counts (long-array 27)]
    (dotimes [i n]
      (let [b (aget stream i)]
        (aset counts b (inc (aget counts b)))))
    counts))

;; ── Complementary fold check ─────────────────────────────

(defn complementary?
  "Two folds are complementary if their union = #{0 1 2 3}."
  [axes1 axes2]
  (= (clojure.set/union axes1 axes2) #{0 1 2 3}))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println "=== 061c: COMPOUND FOLDS ===")
  (println "  What happens when you mirror multiple axes simultaneously?\n")
  (println "  Space: 304,850 = 7 x 50 x 13 x 67")
  (println "  Position = a*43550 + b*871 + c*67 + d\n")

  (let [s           (coords/space)
        et-positions (find-et-positions s)
        et-set       (set et-positions)
        n-et         (count et-positions)
        freqs        (letter-frequencies s)
        n            (long (:n s))]

    ;; Expected: each fold is a bijection, so for each את at position i,
    ;; the mirror position is one specific position. The probability that
    ;; a random position is an את = 6032/304850.
    ;; Expected matches = 6032 * (6032/304850) ≈ 119.3
    (let [expected-simple (* n-et (/ (double n-et) n))]
      (println (format "  Total את in Torah: %d" n-et))
      (println (format "  P(random position is an את) = %d/%d = %.4f" n-et n (/ (double n-et) n)))
      (println (format "  Expected את→את by chance: %.1f  (each fold is a bijection)\n"
                       expected-simple)))

    ;; Verify the abcd fold = reverse
    (println "── Sanity check: abcd-fold(0) should be 304,849 ──")
    (let [abcd-0 (fold-position 0 #{0 1 2 3})]
      (println (format "  abcd-fold(0) = %d  (expected 304,849)  %s\n"
                       abcd-0 (if (== abcd-0 304849) "OK" "MISMATCH"))))

    ;; Fixed point analysis
    (println "── FIXED POINTS ──")
    (println "  A position is fixed when each folded axis sits at its midpoint.")
    (println "  Axis a (dim 7):  midpoint 3   — exists")
    (println "  Axis b (dim 50): midpoint 24.5 — NO fixed point (even dimension)")
    (println "  Axis c (dim 13): midpoint 6   — exists")
    (println "  Axis d (dim 67): midpoint 33  — exists")
    (println)
    (println "  Any fold involving b has ZERO fixed points.")
    (println "  Folds restricted to {a,c,d} have fixed points:\n")
    (doseq [f all-folds]
      (let [fp (count-fixed-points (:axes f))]
        (when (pos? fp)
          (println (format "    %4s-fold: %,6d fixed points" (:name f) fp)))))
    (println)

    ;; Analyze each fold
    (let [results (mapv #(analyze-fold s et-positions et-set %) all-folds)]

      ;; Summary table
      (println "── SUMMARY TABLE ──\n")
      (println (format "  %-6s  %-17s  %-26s  %-26s  %5s  %5s"
                       "Fold" "Pos0 → Coord" "Pos0 → Verse" "PosN → Verse"
                       "את→את" "Fixed"))
      (println (apply str (repeat 100 "─")))

      (doseq [r results]
        (println (format "  %-6s  %-17s  %-26s  %-26s  %5d  %5d"
                         (:name r)
                         (:pos0-coord r)
                         (:pos0-verse r)
                         (:posN-verse r)
                         (:et-mirrors r)
                         (count-fixed-points (:axes r)))))

      ;; את preservation ranking with expected comparison
      (println "\n── את PRESERVATION ──\n")
      (let [expected (double (* n-et (/ (double n-et) n)))]
        (println (format "  Expected by chance: %.1f  (6032^2 / 304850)" expected))
        (println)
        (let [ranked (sort-by :et-mirrors > results)]
          (doseq [r ranked]
            (let [ratio (/ (double (:et-mirrors r)) expected)]
              (println (format "  %6s-fold: %4d את → את  (%.2f%%)  %.1fx expected%s"
                               (:name r) (:et-mirrors r)
                               (* 100.0 (/ (:et-mirrors r) n-et))
                               ratio
                               (cond
                                 (> ratio 5.0) "  ***"
                                 (> ratio 2.0) "  **"
                                 (> ratio 1.5) "  *"
                                 :else "")))))))

      ;; ABCD fold: full reversal
      (println "\n── ABCD FOLD (FULL REVERSAL) ──\n")
      (println "  Position i → 304,849 - i  (reading the Torah backwards)\n")

      (let [abcd #{0 1 2 3}]
        ;; First word
        (println "  First word בראשית mirrors to last word of Torah:")
        (doseq [i (range 6)]
          (let [mi (fold-position i abcd)
                l1 (coords/letter-at s i)
                l2 (coords/letter-at s mi)
                same? (= l1 l2)]
            (println (format "    pos %d  %s  ↔  %s  pos %,d%s"
                             i l1 l2 mi
                             (if same? "  ← SAME LETTER" "")))))

        ;; Center swaps with itself
        (println "\n  Geometric center (pos 152,424 / 152,425):")
        (doseq [i [152424 152425]]
          (let [mi (fold-position i abcd)]
            (println (format "    pos %,d (%s %s) → pos %,d (%s %s)"
                             i (coords/letter-at s i) (verse-str s i)
                             mi (coords/letter-at s mi) (verse-str s mi)))))

        ;; Count same-letter pairs under full reversal
        (println "\n  Letter identity under full reversal:")
        (let [half (quot n 2)
              same-count (loop [i 0 cnt 0]
                           (if (>= i half)
                             cnt
                             (let [mi (- (dec n) i)]
                               (recur (inc i)
                                      (if (= (coords/letter-at s i)
                                             (coords/letter-at s mi))
                                        (inc cnt)
                                        cnt)))))
              expected-same (reduce + (map (fn [idx]
                                            (let [f (aget freqs idx)]
                                              (/ (* (double f) f) n)))
                                          (range 27)))]
          (println (format "    %,d / %,d pairs share the same letter (%.2f%%)"
                           same-count half (* 100.0 (/ (double same-count) half))))
          (println (format "    Expected by letter frequency: %.0f (%.2f%%)"
                           (* expected-same (/ (double half) n))
                           (* 100.0 (/ expected-same n))))))

      ;; Complementary fold pairs: disjoint folds whose union = {0,1,2,3}
      (println "\n── COMPLEMENTARY FOLDS (DISJOINT PAIRS) ──\n")
      (println "  Two folds with disjoint axes that compose to the full reversal:\n")
      (let [result-map (into {} (map (juxt :name identity) results))]
        (doseq [f1 all-folds
                f2 all-folds
                :when (and (empty? (clojure.set/intersection (:axes f1) (:axes f2)))
                           (= (clojure.set/union (:axes f1) (:axes f2)) #{0 1 2 3})
                           ;; Avoid duplicates: order by name
                           (neg? (compare (:name f1) (:name f2))))]
          (let [r1 (result-map (:name f1))
                r2 (result-map (:name f2))]
            (println (format "    %s + %s = abcd" (:name f1) (:name f2)))
            (println (format "      %s-fold: pos0 → %-26s  posN → %s"
                             (:name f1) (:pos0-verse r1) (:posN-verse r1)))
            (println (format "      %s-fold: pos0 → %-26s  posN → %s"
                             (:name f2) (:pos0-verse r2) (:posN-verse r2)))
            ;; Check: does fold1(pos0) = fold2(posN) and vice versa?
            (when (= (:pos0-mirror r1) (:posN-mirror r2))
              (println "      ** Dual: fold1(first) = fold2(last) **"))
            (println))))

      ;; Torah milemarker fate
      (println "── TORAH MILEMARKER FATE ──\n")
      (println "  First תורה at ELS-50 starts at position 5 (Genesis 1:1).")
      (println "  Under each fold, the four letters [5, 55, 105, 155] map to:\n")
      (doseq [r results]
        (when-let [ti (:torah-mirror r)]
          (let [letters (:mirror-letters ti)
                tag (cond
                      (= letters "תורה") "  *** TORAH PRESERVED ***"
                      (= letters "הרות") "  *** TORAH REVERSED ***"
                      :else "")]
            (println (format "    %6s-fold → %s  at %s%s"
                             (:name r) letters (:mirror-verse ti) tag)))))

      ;; Verse observations
      (println "\n── NOTABLE VERSES ──\n")
      (println "  Key destinations where folds send position 0 (ב of בראשית):\n")
      (let [verse-groups (group-by :pos0-verse results)]
        (doseq [[verse rs] (sort-by (comp - count val) verse-groups)]
          (when (> (count rs) 1)
            (println (format "    %s  ←  %s"
                             verse
                             (str/join ", " (map #(str (:name %) "-fold") rs)))))))

      ;; Fold pair gematria
      (println "\n── FOLD PAIR GEMATRIA (pos 0 + its mirror) ──\n")
      (doseq [r results]
        (let [pos0-gv (coords/gv-at s 0)
              mirror-gv (coords/gv-at s (:pos0-mirror r))
              pair-sum (+ pos0-gv mirror-gv)
              divs (cond-> []
                     (zero? (mod pair-sum 7))  (conj 7)
                     (zero? (mod pair-sum 13)) (conj 13)
                     (zero? (mod pair-sum 37)) (conj 37)
                     (zero? (mod pair-sum 67)) (conj 67)
                     (zero? (mod pair-sum 73)) (conj 73))]
          (println (format "    %6s-fold: ב(%d) + %s(%d) = %d%s"
                           (:name r)
                           pos0-gv
                           (coords/letter-at s (:pos0-mirror r))
                           mirror-gv
                           pair-sum
                           (if (seq divs)
                             (str "  ÷ " (str/join ", " divs))
                             ""))))))

    (println "\nDone.")))
