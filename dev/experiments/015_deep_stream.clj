(ns experiments.015-deep-stream
  "Deep structure of the letter stream.
   1. Harmonic analysis — does lag-7 have overtones?
   2. Sliding window palindrome — does the letter distribution mirror itself?
   3. Running gematria curve — what shape is the energy function?
   Run: clojure -M:dev -m experiments.015-deep-stream"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn letter-profile
  "22-dimensional frequency vector for a sequence of characters."
  [letters]
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

(defn autocorrelation-at-lag
  "Compute autocorrelation of a double array at a specific lag."
  [^doubles arr lag]
  (let [n (alength arr)
        m (- n lag)
        ;; Compute mean
        sum (areduce arr i ret 0.0 (+ ret (aget arr i)))
        mean (/ sum n)
        ;; Compute variance
        var-sum (areduce arr i ret 0.0
                         (let [d (- (aget arr i) mean)]
                           (+ ret (* d d))))
        ;; Compute covariance at lag
        cov (loop [i 0 acc 0.0]
              (if (>= i m)
                acc
                (recur (inc i)
                       (+ acc (* (- (aget arr i) mean)
                                 (- (aget arr (+ i lag)) mean))))))]
    (/ cov var-sum)))

(defn -main []
  (println "=== Deep Structure of the Letter Stream ===\n")

  (println "Loading full Torah letter stream...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                 ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(double (g/letter-value %)) all-letters)
        gem-arr  (double-array gem-vals)]
    (println (format "  %,d letters loaded.\n" n))

    ;; ── 1. Harmonic analysis of lag-7 ──────────────────────
    (println "── 1. Harmonic Analysis of Lag-7 ──")
    (println "  Does the seven-fold rhythm have overtones?\n")

    (println (format "  %5s  %10s  %10s  %s" "Lag" "r" "r/r₇" ""))
    (println (apply str (repeat 50 "─")))

    (let [r7 (autocorrelation-at-lag gem-arr 7)]
      ;; Check multiples of 7
      (doseq [k [1 2 3 4 5 6 7 8 9 10 11 12 13 14
                 21 28 35 42 49 56 63 70 77
                 91 98 147 196 245 294 343]]
        (let [r (autocorrelation-at-lag gem-arr k)]
          (println (format "  %5d  %10.6f  %10.4f  %s"
                           k r (/ r r7)
                           (cond
                             (zero? (mod k 49)) " ← 7²"
                             (zero? (mod k 7))  " ← 7×"
                             :else "")))))

      (println (format "\n  r₇ = %.6f (reference)" r7))

      ;; Check whether lag-7 multiples are systematically higher
      (println "\n  Mean autocorrelation by class:")
      (let [multiples-of-7 (mapv #(autocorrelation-at-lag gem-arr %)
                                  (range 7 350 7))
            non-multiples  (mapv #(autocorrelation-at-lag gem-arr %)
                                  (filter #(not (zero? (mod % 7)))
                                          (range 1 350)))
            mean-7  (/ (reduce + multiples-of-7) (count multiples-of-7))
            mean-non (/ (reduce + non-multiples) (count non-multiples))]
        (println (format "    Multiples of 7 (lags 7,14,...,343):  mean r = %.6f  (n=%d)"
                         mean-7 (count multiples-of-7)))
        (println (format "    Non-multiples (lags 1-349 mod 7≠0): mean r = %.6f  (n=%d)"
                         mean-non (count non-multiples)))
        (println (format "    Ratio: %.2fx" (/ mean-7 mean-non)))))

    ;; ── 2. Sliding window palindrome ───────────────────────
    (println "\n── 2. Sliding Window Palindrome ──")
    (println "  Compare letter distribution at position X to position (N-X).")
    (println "  Using windows of various sizes.\n")

    (doseq [window-size [500 1000 2000 5000 10000]]
      (println (format "  Window size: %,d letters" window-size))
      (let [n-samples 50
            step (int (/ (- n (* 2 window-size)) n-samples))
            pairs (for [i (range n-samples)]
                    (let [pos-a (* i step)
                          pos-b (- n window-size (* i step))
                          chunk-a (subvec all-letters pos-a (+ pos-a window-size))
                          chunk-b (subvec all-letters pos-b (+ pos-b window-size))
                          ;; For palindrome: compare A's profile to REVERSED B's profile
                          prof-a (letter-profile chunk-a)
                          prof-b (letter-profile (vec (reverse chunk-b)))]
                      (when (and prof-a prof-b)
                        (cosine-sim prof-a prof-b))))
            valid-pairs (filter some? pairs)
            mean-sim (/ (reduce + valid-pairs) (count valid-pairs))
            ;; Control: compare A to B without reversing
            ctrl-pairs (for [i (range n-samples)]
                         (let [pos-a (* i step)
                               pos-b (- n window-size (* i step))
                               chunk-a (subvec all-letters pos-a (+ pos-a window-size))
                               chunk-b (subvec all-letters pos-b (+ pos-b window-size))
                               prof-a (letter-profile chunk-a)
                               prof-b (letter-profile chunk-b)]
                           (when (and prof-a prof-b)
                             (cosine-sim prof-a prof-b))))
            valid-ctrl (filter some? ctrl-pairs)
            mean-ctrl (/ (reduce + valid-ctrl) (count valid-ctrl))]
        (println (format "    Mirror (X ↔ rev N-X):  mean cosine = %.6f" mean-sim))
        (println (format "    Direct (X ↔ N-X):      mean cosine = %.6f" mean-ctrl))
        (println (format "    Difference:            %+.6f" (- mean-sim mean-ctrl)))
        (println)))

    ;; ── 3. Running gematria curve ──────────────────────────
    (println "── 3. Running Gematria — Shape of the Energy Function ──")
    (println "  Cumulative gematria at regular intervals.\n")

    (let [cum (vec (reductions + gem-vals))
          total (last cum)
          ;; Sample at 100 points
          step (int (/ n 100))
          samples (mapv #(nth cum (* % step)) (range 100))
          ;; Linear expectation: cum should be roughly linear
          ;; Deviation from linearity reveals structure
          deviations (mapv (fn [i]
                             (let [expected (* total (/ (double (inc i)) 100))
                                   actual   (double (nth samples i))]
                               (- actual expected)))
                           (range 100))]
      (println (format "  Total gematria: %,.0f" total))
      (println (format "  Expected per %,d letters: %,.0f\n" step (/ total 100.0)))

      (println "  Deviation from linear cumulative (positive = ahead of pace):")
      (println (format "  %5s  %12s  %12s  %12s" "Pos%" "Cumulative" "Expected" "Deviation"))
      (println (apply str (repeat 50 "─")))
      (doseq [i (range 0 100 5)]
        (let [expected (* total (/ (double (inc i)) 100))
              actual   (double (nth samples i))]
          (println (format "  %4d%%  %,12.0f  %,12.0f  %+,12.0f"
                           (inc i) actual expected (- actual expected)))))

      ;; Is the deviation curve itself palindromic?
      (let [dev-first (subvec deviations 0 50)
            dev-second (vec (reverse (subvec deviations 50 100)))
            dev-cos (cosine-sim (mapv double dev-first) (mapv double dev-second))]
        (println (format "\n  Deviation curve palindrome: cos(first half, rev second half) = %.4f" dev-cos))))

    ;; ── 4. Gematria by septile ─────────────────────────────
    (println "\n── 4. Seven-Fold Division ──")
    (println "  Split the Torah into 7 equal segments. What are their gematria sums?\n")

    (let [seg-size (quot n 7)
          segments (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end   (if (= i 6) n (* (inc i) seg-size))
                                 chunk (subvec all-letters start end)
                                 gem-sum (reduce + (map #(long (g/letter-value %)) chunk))]
                             {:seg (inc i) :start start :end end
                              :letters (- end start) :gematria gem-sum}))
                         (range 7))
          gem-vals-7 (mapv :gematria segments)
          mean-gem (/ (reduce + gem-vals-7) 7.0)
          cv (/ (Math/sqrt (/ (reduce + (map #(Math/pow (- % mean-gem) 2) gem-vals-7)) 7.0))
                mean-gem)]
      (println (format "  %4s  %10s  %12s  %10s" "Seg" "Letters" "Gematria" "% of mean"))
      (println (apply str (repeat 42 "─")))
      (doseq [{:keys [seg letters gematria]} segments]
        (println (format "  %4d  %,10d  %,12d  %9.2f%%" seg letters gematria
                         (* 100 (/ (double gematria) mean-gem)))))
      (println (format "\n  Mean: %,.0f  CV: %.4f (%.2f%%)" mean-gem cv (* 100 cv)))

      ;; Palindrome of the 7 segments
      (let [first3 (subvec gem-vals-7 0 3)
            last3  (vec (reverse (subvec gem-vals-7 4 7)))
            cos7 (cosine-sim (mapv double first3) (mapv double last3))]
        (println (format "  Palindrome (segments 1-3 vs rev 5-7): cos = %.4f" cos7))
        (println (format "  Center segment (4): %,d" (nth gem-vals-7 3)))))

    ;; ── 5. The center region in detail ─────────────────────
    (println "\n── 5. Center Region — Microscope ──")
    (println "  What happens in the immediate neighborhood of position 153,134?\n")

    (let [center (quot n 2)
          ;; Letter distribution in expanding rings around center
          rings [10 50 100 500 1000 5000 10000]]
      (println (format "  Center position: %,d" center))
      (println (format "  Center letter: %c (value %d)\n"
                       (nth all-letters center)
                       (g/letter-value (nth all-letters center))))

      (println "  Expanding rings around center:")
      (println (format "  %8s  %6s  %10s  %10s  %6s" "±radius" "letters" "gematria" "mean" "center?"))
      (println (apply str (repeat 52 "─")))
      (doseq [r rings]
        (let [start (max 0 (- center r))
              end   (min n (+ center r))
              chunk (subvec all-letters start end)
              g-sum (reduce + (map #(long (g/letter-value %)) chunk))
              g-mean (/ (double g-sum) (count chunk))
              ;; Is this ring palindromic?
              half (quot (count chunk) 2)
              first-h (subvec chunk 0 half)
              second-h (vec (reverse (subvec chunk (- (count chunk) half))))
              prof-a (letter-profile first-h)
              prof-b (letter-profile second-h)
              pal (if (and prof-a prof-b) (cosine-sim prof-a prof-b) 0.0)]
          (println (format "  %8s  %,6d  %,10d  %10.2f  %.4f"
                           (format "±%,d" r) (count chunk) g-sum g-mean pal))))

      ;; The exact center: 21 letters (10 on each side + center)
      (println "\n  The 21 letters at the center (±10):")
      (let [center-letters (subvec all-letters (- center 10) (+ center 11))]
        (println (format "    %s" (apply str center-letters)))
        (println (format "    %s" (apply str (map #(format "%3d" (g/letter-value %)) center-letters))))
        (let [center-gem (reduce + (map #(long (g/letter-value %)) center-letters))]
          (println (format "    Sum: %d" center-gem))
          (println (format "    Sum mod 7: %d" (mod center-gem 7)))
          (println (format "    Sum mod 22: %d" (mod center-gem 22))))))

    ;; ── 6. Self-similarity at different scales ─────────────
    (println "\n── 6. Self-Similarity Across Scales ──")
    (println "  Letter frequency profiles at different scales.")
    (println "  Does each scale look like the whole?\n")

    (let [torah-profile (letter-profile all-letters)]
      (println (format "  %12s  %8s  %s" "Scale" "cos(Torah)" "Description"))
      (println (apply str (repeat 50 "─")))

      ;; Compare profiles at different scales
      (doseq [[desc chunk]
              [["First half"  (subvec all-letters 0 (quot n 2))]
               ["Second half" (subvec all-letters (quot n 2))]
               ["First third" (subvec all-letters 0 (quot n 3))]
               ["Middle third" (subvec all-letters (quot n 3) (* 2 (quot n 3)))]
               ["Last third"  (subvec all-letters (* 2 (quot n 3)))]
               ["First 10%"   (subvec all-letters 0 (quot n 10))]
               ["Last 10%"    (subvec all-letters (* 9 (quot n 10)))]
               ["Center 10%"  (subvec all-letters (* 45 (quot n 100)) (* 55 (quot n 100)))]
               ["First 1%"    (subvec all-letters 0 (quot n 100))]
               ["Last 1%"     (subvec all-letters (* 99 (quot n 100)))]
               ["Center 1%"   (subvec all-letters (* 495 (quot n 1000)) (* 505 (quot n 1000)))]]]
        (let [prof (letter-profile chunk)
              cos  (cosine-sim prof torah-profile)]
          (println (format "  %12s  %8.6f  (%,d letters)" desc cos (count chunk)))))))

  (println "\nDone."))
