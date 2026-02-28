(ns experiments.017-the-fold
  "The deepest test: fold the Torah at its center.
   Compare the two halves — letter by letter, word by word.
   Does the fractal reproduce at sub-book scales?
   Run: clojure -M:dev -m experiments.017-the-fold"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn pearson-r
  "Pearson correlation between two double arrays."
  [^doubles xs ^doubles ys]
  (let [n (alength xs)
        sum-x (areduce xs i ret 0.0 (+ ret (aget xs i)))
        sum-y (areduce ys i ret 0.0 (+ ret (aget ys i)))
        mean-x (/ sum-x n)
        mean-y (/ sum-y n)
        cov (loop [i 0 acc 0.0]
              (if (= i n) acc
                  (recur (inc i) (+ acc (* (- (aget xs i) mean-x)
                                           (- (aget ys i) mean-y))))))
        var-x (loop [i 0 acc 0.0]
                (if (= i n) acc
                    (recur (inc i) (+ acc (Math/pow (- (aget xs i) mean-x) 2)))))
        var-y (loop [i 0 acc 0.0]
                (if (= i n) acc
                    (recur (inc i) (+ acc (Math/pow (- (aget ys i) mean-y) 2)))))]
    (if (or (zero? var-x) (zero? var-y)) 0.0
        (/ cov (Math/sqrt (* var-x var-y))))))

(defn -main []
  (println "=== The Fold ===")
  (println "  Folding the Torah at its center. What mirrors?\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        center (quot n 2)
        ;; First half: [0, center-1]
        ;; Second half reversed: [n-1, center+1]
        half-size center  ;; 153,134
        first-half (subvec all-letters 0 half-size)
        second-half-rev (vec (reverse (subvec all-letters (- n half-size))))]

    (println (format "  %,d letters. Center at %,d. Half size: %,d.\n" n center half-size))

    ;; ── 1. Letter-by-letter match ──────────────────────────
    (println "── 1. Letter-by-Letter Fold ──")
    (println "  How often does letter[i] = letter[N-1-i]?\n")

    (let [matches (loop [i 0 m 0]
                    (if (= i half-size)
                      m
                      (recur (inc i)
                             (if (= (nth first-half i) (nth second-half-rev i))
                               (inc m) m))))
          match-rate (/ (double matches) half-size)
          ;; Expected by chance: Σ p_i²
          freqs (frequencies all-letters)
          expected (reduce + (map (fn [[_ cnt]] (Math/pow (/ (double cnt) n) 2)) freqs))]
      (println (format "  Exact matches:   %,d / %,d = %.4f (%.2f%%)"
                       matches half-size match-rate (* 100 match-rate)))
      (println (format "  Expected (Σp²):  %.4f (%.2f%%)" expected (* 100 expected)))
      (println (format "  Ratio:           %.3f" (/ match-rate expected)))
      (println (format "  %s" (if (> match-rate expected)
                                "ABOVE CHANCE — palindromic signal at letter level"
                                "At or below chance — palindrome is structural, not letter-level"))))

    ;; ── 2. Per-letter mirror affinity ──────────────────────
    (println "\n── 2. Mirror Affinity by Letter ──")
    (println "  When letter X appears at position i, what appears at position N-1-i?\n")

    (let [mirror-matrix (atom {})]
      (doseq [i (range half-size)]
        (let [a (nth first-half i)
              b (nth second-half-rev i)]
          (swap! mirror-matrix update-in [a b] (fnil inc 0))))

      ;; For each letter, what's its most common mirror partner?
      (println (format "  %3s  %10s  %8s  %s" "Ltr" "Value" "Self%" "Top mirror partner"))
      (println (apply str (repeat 55 "─")))
      (doseq [c alphabet]
        (let [row (get @mirror-matrix c {})
              total (reduce + (vals row))
              self-count (get row c 0)
              self-pct (if (pos? total) (* 100 (/ (double self-count) total)) 0.0)
              top (first (sort-by (comp - val) row))]
          (when (pos? total)
            (println (format "  %s    %3d       %5.1f%%  %s (%d times, %.1f%%)"
                             c (g/letter-value c) self-pct
                             (key top) (val top)
                             (* 100 (/ (double (val top)) total))))))))

    ;; ── 3. Gematria correlation at mirrored positions ──────
    (println "\n── 3. Gematria Correlation ──")
    (println "  Pearson r between gematria values at position i and N-1-i.\n")

    (let [gem-first  (double-array (map #(double (g/letter-value %)) first-half))
          gem-second (double-array (map #(double (g/letter-value %)) second-half-rev))
          r (pearson-r gem-first gem-second)]
      (println (format "  Pearson r = %.6f" r))
      (println (format "  %s" (cond
                                (> r 0.05) "Significant positive correlation"
                                (< r -0.05) "Significant negative correlation"
                                :else "Near zero — no letter-level gematria mirror"))))

    ;; ── 4. Running palindrome score by distance from center ──
    (println "\n── 4. Palindrome Score by Distance from Center ──")
    (println "  How does the cosine similarity change as we move outward?\n")

    (println (format "  %12s  %8s  %10s  %10s" "Window" "Letters" "Profile cos" "Length cos"))
    (println (apply str (repeat 48 "─")))
    (doseq [radius [100 500 1000 2000 5000 10000 25000 50000 75000 100000 150000]]
      (when (<= radius half-size)
        (let [a (subvec first-half (- half-size radius))
              b (subvec second-half-rev (- half-size radius))
              prof-a (letter-profile a)
              prof-b (letter-profile b)
              cos-prof (if (and prof-a prof-b) (cosine-sim prof-a prof-b) 0.0)
              ;; Also: chunk into 50 pieces and compare length profiles
              chunk-size (max 1 (quot radius 50))
              lens-a (mapv (fn [i] (count (subvec a (* i chunk-size)
                                                  (min (count a) (* (inc i) chunk-size)))))
                           (range 50))
              lens-b (mapv (fn [i] (count (subvec b (* i chunk-size)
                                                  (min (count b) (* (inc i) chunk-size)))))
                           (range 50))
              cos-len (cosine-sim (mapv double lens-a) (mapv double lens-b))]
          (println (format "  %12s  %,8d  %10.6f  %10.6f"
                           (format "±%,d" radius) (* 2 radius) cos-prof cos-len)))))

    ;; ── 5. Does the fractal ratio reproduce at sub-scales? ──
    (println "\n── 5. Fractal Self-Reproduction ──")
    (println "  Does the √2 and π/2 pattern appear within individual books?\n")

    (let [book-letters-map
          (into {} (map (fn [b]
                          [b (vec (mapcat
                                   (fn [ch]
                                     (let [verses (sefaria/fetch-chapter b ch)
                                           raw (apply str (map norm/strip-html verses))]
                                       (norm/letter-stream raw)))
                                   (range 1 (inc (get sefaria/book-chapters b)))))])
                        ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))]

      (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
        (let [letters (get book-letters-map book)
              bn (count letters)
              ;; Split each book into 5 equal segments
              seg-size (quot bn 5)
              segs (mapv (fn [i]
                            (let [start (* i seg-size)
                                  end (if (= i 4) bn (* (inc i) seg-size))]
                              (- end start)))
                         (range 5))
              [s1 s2 s3 s4 s5] (mapv double segs)
              ;; Test ratios
              ae-ratio (/ s1 s5)
              bb-diff (Math/abs (- s2 s4))
              bc-ratio (/ s2 s3)
              split-ratio (/ (+ s1 s2 s3) (+ s4 s5))]
          (println (format "  %s (%,d letters, segments: %s):"
                           book bn (pr-str segs)))
          (println (format "    Seg1/Seg5 = %.4f  (√2 = 1.4142, err = %.4f)"
                           ae-ratio (Math/abs (- ae-ratio (Math/sqrt 2)))))
          (println (format "    Seg2/Seg4 = %.4f  |diff| = %,.0f"
                           (/ s2 s4) bb-diff))
          (println (format "    (1+2+3)/(4+5) = %.4f  (π/2 = 1.5708, err = %.4f)"
                           split-ratio (Math/abs (- split-ratio (/ Math/PI 2)))))
          (println))))

    ;; ── 6. The golden cut within each book ─────────────────
    (println "── 6. Golden Sections Within Books ──")
    (println "  Where does φ (0.618) cut each book?\n")

    (let [phi (/ (- (Math/sqrt 5) 1) 2)]  ;; 0.6180339...
      (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
        (let [letters (sefaria/book-letters book)
              bn (count letters)
              golden-pos (int (* phi bn))
              golden-letter (nth letters golden-pos)
              ;; Where does this fall in terms of chapters?
              chapters (get sefaria/book-chapters book)
              cum-counts (loop [ch 1 acc []]
                           (if (> ch chapters)
                             acc
                             (let [verses (sefaria/fetch-chapter book ch)
                                   raw (apply str (map norm/strip-html verses))
                                   lcount (count (norm/letter-stream raw))]
                               (recur (inc ch) (conj acc lcount)))))
              cum (vec (reductions + cum-counts))
              ch-idx (count (take-while #(<= % golden-pos) cum))]
          (println (format "  %s: φ = position %,d of %,d → Chapter %d, letter %c (value %d)"
                           book golden-pos bn (min (inc ch-idx) chapters)
                           golden-letter (g/letter-value golden-letter))))))

    ;; ── 7. Does each half reproduce the book-level ratios? ──
    (println "\n── 7. Recursive Ratio Test ──")
    (println "  Split the Torah in half. Does each half's 'book' structure")
    (println "  reproduce the same ratios?\n")

    ;; The first half contains: all of Genesis + Exodus + part of Leviticus
    ;; The second half: rest of Leviticus + Numbers + Deuteronomy
    ;; Within each half, split into 5 equal segments and test

    (doseq [[label chunk] [["First half" first-half]
                            ["Second half" (subvec all-letters center)]]]
      (let [cn (count chunk)
            seg-size (quot cn 5)
            segs (mapv (fn [i]
                          (let [start (* i seg-size)
                                end (if (= i 4) cn (* (inc i) seg-size))]
                            (- end start)))
                       (range 5))
            [s1 s2 s3 s4 s5] (mapv double segs)]
        (println (format "  %s (%,d letters):" label cn))
        (println (format "    Segments: %s" (pr-str segs)))
        (println (format "    Seg1/Seg5 = %.4f  |err from √2| = %.4f"
                         (/ s1 s5) (Math/abs (- (/ s1 s5) (Math/sqrt 2)))))
        (println (format "    |Seg2-Seg4| = %,.0f" (Math/abs (- s2 s4))))
        (println (format "    (1+2+3)/(4+5) = %.4f  |err from π/2| = %.4f"
                         (/ (+ s1 s2 s3) (+ s4 s5))
                         (Math/abs (- (/ (+ s1 s2 s3) (+ s4 s5)) (/ Math/PI 2)))))
        (println)))

    ;; ── 8. Transition matrix entropy ───────────────────────
    (println "── 8. Mirror Transition Entropy ──")
    (println "  When I know letter[i], how much does that tell me about letter[N-1-i]?\n")

    (let [;; Build transition matrix: P(mirror | letter)
          matrix (atom {})
          _ (doseq [i (range half-size)]
              (let [a (nth first-half i)
                    b (nth second-half-rev i)]
                (swap! matrix update a (fnil #(update % b (fnil inc 0)) {}))))
          ;; For each letter, compute entropy of its mirror distribution
          ;; Compare to entropy of overall letter distribution
          overall-freqs (frequencies all-letters)
          overall-total (double n)
          overall-entropy (- (reduce + (map (fn [[_ cnt]]
                                              (let [p (/ (double cnt) overall-total)]
                                                (* p (Math/log p))))
                                            overall-freqs)))]
      (println (format "  Overall letter entropy: %.4f bits" (/ overall-entropy (Math/log 2))))
      (println)
      (println (format "  %3s  %8s  %14s  %14s  %s"
                       "Ltr" "Count" "Mirror entropy" "Reduction" "Notes"))
      (println (apply str (repeat 65 "─")))

      (let [total-reduction (atom 0.0)
            total-weight (atom 0.0)]
        (doseq [c alphabet]
          (let [row (get @matrix c {})
                total (double (reduce + (vals row)))
                mirror-entropy (if (pos? total)
                                 (- (reduce + (map (fn [[_ cnt]]
                                                     (let [p (/ (double cnt) total)]
                                                       (* p (Math/log p))))
                                                   row)))
                                 0.0)
                mirror-bits (/ mirror-entropy (Math/log 2))
                overall-bits (/ overall-entropy (Math/log 2))
                reduction (- overall-bits mirror-bits)
                letter-freq (/ (double (get overall-freqs c 0)) overall-total)]
            (swap! total-reduction + (* letter-freq reduction))
            (swap! total-weight + letter-freq)
            (println (format "  %s    %,8d  %10.4f bits  %+10.4f bits  %s"
                             c (long total) mirror-bits reduction
                             (if (> reduction 0.01) "← knowing this letter reduces mirror uncertainty" "")))))

        (println (format "\n  Weighted mean entropy reduction: %.4f bits" @total-reduction))
        (println (format "  %s" (if (> @total-reduction 0.01)
                                  "The fold carries information — letters predict their mirrors."
                                  "Minimal information — the palindrome is structural, not letter-level."))))))

  (println "\nDone."))
