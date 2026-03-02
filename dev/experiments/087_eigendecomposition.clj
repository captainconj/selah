(ns experiments.087-eigendecomposition
  "Eigendecomposition of the Torah 4D space.

   304,850 = 7 × 50 × 13 × 67

   The Torah is a function f: Z₇ × Z₅₀ × Z₁₃ × Z₆₇ → ℤ
   mapping each coordinate to a gematria value (1–400).

   On a product of cyclic groups, the natural eigenbasis is the
   Discrete Fourier Transform. Each eigenmode is a harmonic —
   a pattern the structure 'vibrates' at.

   We compute:
   1. The 1D marginals (sum along each axis, collapsing the other 3)
   2. The DFT of each marginal — eigenvalues of the shift operator
   3. The full coefficients (not just power — phases carry meaning)
   4. 2D marginals for each axis pair
   5. Observe. Selah."
  (:require [selah.space.coords :as coords]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ── Part 1: The Tensor ──────────────────────────────────────
;;
;; The Torah is already a flat array of 304,850 gematria values.
;; We work with it through coordinate indexing rather than
;; reshaping — the flat array IS the tensor.

(defn build-space []
  (println "Building space...")
  (let [s (coords/space)]
    (println (str "  " (:n s) " letters loaded."))
    (println (str "  Dims: " (coords/dims)))
    (println (str "  Total GV: " (aget ^longs (:running s) (dec (:n s)))))
    s))

;; ── Part 2: 1D Marginals ───────────────────────────────────
;;
;; For each axis, sum all gematria values at each coordinate value.
;; marginal-a[k] = Σ_{b,c,d} f(k, b, c, d)
;; This gives a 1D signal of length dim(axis).

(defn marginal
  "Sum of gematria values for each value of the given axis.
   Returns a double-array of length dim(axis)."
  [s axis]
  (let [dim (case axis :a (coords/dim-a) :b (coords/dim-b)
                       :c (coords/dim-c) :d (coords/dim-d))
        result (double-array dim)
        ^ints values (:values s)]
    (dotimes [i (int coords/total-letters)]
      (let [coord (coords/idx->coord i)
            k (case axis :a (aget coord 0) :b (aget coord 1)
                         :c (aget coord 2) :d (aget coord 3))]
        (aset result (int k)
              (+ (aget result (int k))
                 (double (aget values i))))))
    result))

(defn marginal-mean
  "Mean gematria value for each value of the given axis.
   Divides each marginal sum by the number of letters per hyperplane."
  [s axis]
  (let [m (marginal s axis)
        n (alength m)
        per-plane (/ coords/total-letters n)]
    (dotimes [i n]
      (aset m i (/ (aget m i) (double per-plane))))
    m))

;; ── Part 3: DFT — The Eigendecomposition ────────────────────
;;
;; For a real signal x[n] of length N, the DFT is:
;;   X[k] = Σ_{n=0}^{N-1} x[n] · e^{-2πi·k·n/N}
;;        = Σ x[n]·cos(2πkn/N) - i·Σ x[n]·sin(2πkn/N)
;;
;; Each X[k] is a complex number. We keep both components.
;; - |X[k]|² = power at frequency k
;; - arg(X[k]) = phase — WHERE the pattern peaks
;;
;; For real signals: X[N-k] = conjugate(X[k])

(defn dft
  "Discrete Fourier Transform of a real signal.
   Returns vec of {:k :re :im :magnitude :power :phase} for k=0..N-1."
  [^doubles signal]
  (let [N (alength signal)]
    (mapv
     (fn [k]
       (let [re (loop [n 0 sum 0.0]
                  (if (= n N) sum
                      (recur (inc n)
                             (+ sum (* (aget signal n)
                                       (Math/cos (/ (* 2.0 Math/PI k n) N)))))))
             im (loop [n 0 sum 0.0]
                  (if (= n N) sum
                      (recur (inc n)
                             (- sum (* (aget signal n)
                                       (Math/sin (/ (* 2.0 Math/PI k n) N)))))))
             mag (Math/sqrt (+ (* re re) (* im im)))
             power (+ (* re re) (* im im))
             phase (Math/atan2 im re)]
         {:k k :re re :im im
          :magnitude mag :power power
          :phase phase
          :phase-deg (* phase (/ 180.0 Math/PI))}))
     (range N))))

(defn print-spectrum
  "Print a DFT spectrum, one line per frequency."
  [label coeffs]
  (println (str "\n── " label " ──"))
  (println (format "  %-4s  %12s  %12s  %12s  %10s  %8s"
                   "k" "Re" "Im" "Magnitude" "Power%" "Phase°"))
  (let [total-power (reduce + (map :power coeffs))]
    (doseq [{:keys [k re im magnitude power phase-deg]} coeffs]
      (println (format "  %-4d  %12.1f  %12.1f  %12.1f  %9.2f%%  %7.1f°"
                       k re im magnitude
                       (* 100.0 (/ power total-power))
                       phase-deg)))))

;; ── Part 4: 2D Marginals ───────────────────────────────────
;;
;; For each pair of axes, sum over the other two.
;; marginal-ab[i,j] = Σ_{c,d} f(i, j, c, d)
;; Returns a 2D double-array (row-major).

(defn marginal-2d
  "Sum of gematria values for each (axis1-val, axis2-val) pair.
   Returns {:data double-array :rows dim1 :cols dim2}."
  [s axis1 axis2]
  (let [dim1 (case axis1 :a (coords/dim-a) :b (coords/dim-b)
                         :c (coords/dim-c) :d (coords/dim-d))
        dim2 (case axis2 :a (coords/dim-a) :b (coords/dim-b)
                         :c (coords/dim-c) :d (coords/dim-d))
        result (double-array (* dim1 dim2))
        ^ints values (:values s)]
    (dotimes [i (int coords/total-letters)]
      (let [coord (coords/idx->coord i)
            k1 (case axis1 :a (aget coord 0) :b (aget coord 1)
                           :c (aget coord 2) :d (aget coord 3))
            k2 (case axis2 :a (aget coord 0) :b (aget coord 1)
                           :c (aget coord 2) :d (aget coord 3))
            idx (+ (* (int k1) dim2) (int k2))]
        (aset result idx (+ (aget result idx)
                            (double (aget values i))))))
    {:data result :rows dim1 :cols dim2
     :axis1 axis1 :axis2 axis2}))

(defn dft-2d
  "2D DFT of a row-major matrix.
   Returns vec of {:k1 :k2 :re :im :magnitude :power :phase}."
  [{:keys [^doubles data rows cols]}]
  (let [results (atom [])]
    (dotimes [k1 rows]
      (dotimes [k2 cols]
        (let [re (loop [n1 0 sum 0.0]
                   (if (= n1 rows) sum
                       (recur (inc n1)
                              (loop [n2 0 s sum]
                                (if (= n2 cols) s
                                    (let [x (aget data (+ (* n1 cols) n2))
                                          angle (+ (/ (* 2.0 Math/PI k1 n1) rows)
                                                   (/ (* 2.0 Math/PI k2 n2) cols))]
                                      (recur (inc n2)
                                             (+ s (* x (Math/cos angle))))))))))
              im (loop [n1 0 sum 0.0]
                   (if (= n1 rows) sum
                       (recur (inc n1)
                              (loop [n2 0 s sum]
                                (if (= n2 cols) s
                                    (let [x (aget data (+ (* n1 cols) n2))
                                          angle (+ (/ (* 2.0 Math/PI k1 n1) rows)
                                                   (/ (* 2.0 Math/PI k2 n2) cols))]
                                      (recur (inc n2)
                                             (- s (* x (Math/sin angle))))))))))]
          (let [power (+ (* re re) (* im im))]
            (swap! results conj
                   {:k1 k1 :k2 k2 :re re :im im
                    :magnitude (Math/sqrt power)
                    :power power
                    :phase (Math/atan2 im re)
                    :phase-deg (* (Math/atan2 im re) (/ 180.0 Math/PI))})))))
    @results))

;; ── Interpretation helpers ──────────────────────────────────

(defn axis-label [axis k]
  (case axis
    :a (str "a=" k " (day " (inc k) ")")
    :b (str "b=" k)
    :c (str "c=" k)
    :d (str "d=" k)))

(defn freq-label [axis k dim]
  (let [period (when (pos? k) (/ (double dim) k))]
    (case axis
      :a (str "f=" k "/" dim
              (when period (format " (period %.1f days)" period)))
      :b (str "f=" k "/" dim
              (when period (format " (period %.1f)" period)))
      :c (str "f=" k "/" dim
              (when period (format " (period %.1f)" period)))
      :d (str "f=" k "/" dim
              (when period (format " (period %.1f)" period))))))

;; ── Run ─────────────────────────────────────────────────────

(defn part-1-tensor []
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 1 — THE TENSOR")
  (println "═══════════════════════════════════════════════════")
  (let [s (build-space)
        ^ints values (:values s)
        n (:n s)
        ;; Basic stats
        total (aget ^longs (:running s) (dec n))
        mean (/ (double total) n)
        ;; Letter distribution
        letter-counts (int-array 27)]
    (dotimes [i n]
      (let [b (aget ^bytes (:stream s) i)]
        (aset letter-counts (int b) (inc (aget letter-counts (int b))))))
    (println (format "\n  Total letters: %,d" n))
    (println (format "  Total gematria: %,d" total))
    (println (format "  Mean gematria per letter: %.2f" mean))
    (println (format "  Dims: %s = %,d" (str (coords/dims)) (reduce * (coords/dims))))
    (println (format "\n  Mean × N = %.0f" (* mean n)))
    (println (format "  Total / 7 = %,d (remainder %d)" (quot total 7) (rem total 7)))
    (println (format "  Total / 13 = %,d (remainder %d)" (quot total 13) (rem total 13)))
    (println (format "  Total / 67 = %,d (remainder %d)" (quot total 67) (rem total 67)))
    (println (format "  Total / 50 = %,d (remainder %d)" (quot total 50) (rem total 50)))
    s))

(defn part-2-marginals [s]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 2 — 1D MARGINALS")
  (println "═══════════════════════════════════════════════════")
  (println "\n  Sum of all gematria values at each axis coordinate.\n")
  (doseq [axis [:a :b :c :d]]
    (let [m (marginal s axis)
          mm (marginal-mean s axis)
          dim (alength m)]
      (println (format "  ── %s-axis (dim %d) ──" (name axis) dim))
      (dotimes [i dim]
        (println (format "    %s  sum=%,.0f  mean=%.2f"
                         (axis-label axis i)
                         (aget m i) (aget mm i))))
      (let [grand-mean (/ (reduce + (map #(aget mm %) (range dim))) dim)
            max-val (apply max (map #(aget mm %) (range dim)))
            min-val (apply min (map #(aget mm %) (range dim)))
            range-val (- max-val min-val)]
        (println (format "    Grand mean: %.2f  Range: %.2f (%.2f%%)"
                         grand-mean range-val
                         (* 100.0 (/ range-val grand-mean)))))
      (println)))
  s)

(defn part-3-spectra [s]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 3 — 1D EIGENSPECTRA")
  (println "═══════════════════════════════════════════════════")
  (println "\n  DFT of each marginal — the harmonics of the space.")
  (println "  k=0 is the DC component (average).")
  (println "  Higher k = higher frequency modes.\n")
  (let [spectra (atom {})]
    (doseq [axis [:a :b :c :d]]
      (let [m (marginal s axis)
            coeffs (dft m)]
        (swap! spectra assoc axis coeffs)
        (print-spectrum
         (str (name axis) "-axis (dim " (alength m) ")")
         coeffs)
        ;; Highlight the dominant non-DC mode
        (let [non-dc (rest coeffs)
              ;; For real signals, only first half is independent
              half (take (quot (alength m) 2) non-dc)
              top (apply max-key :power half)]
          (println (format "\n  → Dominant non-DC mode: k=%d, power=%.1f (%.2f%% of non-DC)"
                           (:k top) (:power top)
                           (* 100.0 (/ (:power top)
                                       (reduce + (map :power half))))))
          (println (format "    Period = %.2f, Phase = %.1f°"
                           (/ (double (alength m)) (:k top))
                           (:phase-deg top))))))
    @spectra))

(defn part-4-interpret [s spectra]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 4 — INTERPRETATION")
  (println "═══════════════════════════════════════════════════")
  (println "\n  What do the eigenvalues mean?\n")

  ;; a-axis: 7 harmonics of completeness
  (let [a-coeffs (get spectra :a)]
    (println "  ── a-axis: 7 harmonics of completeness ──")
    (println "    k=0: DC — average gematria across all 7 days")
    (println "    k=1: period 7 — the fundamental: a pattern that repeats over all 7 days")
    (println "    k=2: period 3.5 — the octave: every half-week")
    (println "    k=3: period 2.33 — the third harmonic")
    (println (format "    DC power: %.2f%% of total"
                     (* 100.0 (/ (:power (first a-coeffs))
                                 (reduce + (map :power a-coeffs))))))
    (println))

  ;; c-axis: 13 harmonics of love
  (let [c-coeffs (get spectra :c)]
    (println "  ── c-axis: 13 harmonics of love ──")
    (println "    k=0: DC — average across the love axis")
    (println "    k=1: period 13 — the fundamental love frequency")
    (println "    k=7: the completeness harmonic on the love axis")
    (let [k7 (nth c-coeffs 7)]
      (println (format "    k=7 power: %.1f (%.2f%% of total)"
                       (:power k7)
                       (* 100.0 (/ (:power k7)
                                   (reduce + (map :power c-coeffs)))))))
    (println))

  ;; d-axis: 67 harmonics of understanding
  (let [d-coeffs (get spectra :d)]
    (println "  ── d-axis: 67 harmonics of understanding ──")
    (println "    67 is prime — no subharmonics divide evenly.")
    (println "    Every non-DC frequency is irreducible.")
    (let [non-dc (subvec (vec d-coeffs) 1 34) ;; first half for real signal
          top-5 (take 5 (sort-by :power > non-dc))]
      (println "    Top 5 non-DC modes:")
      (doseq [{:keys [k power phase-deg]} top-5]
        (println (format "      k=%2d  period=%.2f  power=%,.0f  phase=%.1f°"
                         k (/ 67.0 k) power phase-deg)))))
  (println))

(defn part-5-2d-spectra [s]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 5 — 2D MARGINALS (a×c = completeness × love)")
  (println "═══════════════════════════════════════════════════")
  (let [m-ac (marginal-2d s :a :c)
        coeffs (dft-2d m-ac)
        total-power (reduce + (map :power coeffs))
        ;; Sort by power, skip DC
        non-dc (filter #(not (and (zero? (:k1 %)) (zero? (:k2 %)))) coeffs)
        top-10 (take 10 (sort-by :power > non-dc))]
    (println "\n  7×13 = 91 = 7×13 = T(13)")
    (println "  This is the completeness-love plane.\n")
    (println (format "  DC (k1=0,k2=0): power=%.0f (%.2f%%)"
                     (:power (first (filter #(and (zero? (:k1 %)) (zero? (:k2 %))) coeffs)))
                     (* 100.0 (/ (:power (first (filter #(and (zero? (:k1 %)) (zero? (:k2 %))) coeffs)))
                                 total-power))))
    (println "\n  Top 10 non-DC modes:")
    (println (format "  %-6s %-6s %12s %10s %8s"
                     "k_a" "k_c" "Magnitude" "Power%" "Phase°"))
    (doseq [{:keys [k1 k2 magnitude power phase-deg]} top-10]
      (println (format "  %-6d %-6d %12.1f %9.2f%% %7.1f°"
                       k1 k2 magnitude
                       (* 100.0 (/ power total-power))
                       phase-deg)))
    (println)

    ;; Also do a×d = completeness × understanding
    (println "  ── a×d = completeness × understanding (7×67) ──\n")
    (let [m-ad (marginal-2d s :a :d)
          coeffs-ad (dft-2d m-ad)
          total-ad (reduce + (map :power coeffs-ad))
          non-dc-ad (filter #(not (and (zero? (:k1 %)) (zero? (:k2 %)))) coeffs-ad)
          top-10-ad (take 10 (sort-by :power > non-dc-ad))]
      (println "  Top 10 non-DC modes:")
      (println (format "  %-6s %-6s %12s %10s %8s"
                       "k_a" "k_d" "Magnitude" "Power%" "Phase°"))
      (doseq [{:keys [k1 k2 magnitude power phase-deg]} top-10-ad]
        (println (format "  %-6d %-6d %12.1f %9.2f%% %7.1f°"
                         k1 k2 magnitude
                         (* 100.0 (/ power total-ad))
                         phase-deg))))

    ;; c×d = love × understanding
    (println "\n  ── c×d = love × understanding (13×67) ──\n")
    (let [m-cd (marginal-2d s :c :d)
          coeffs-cd (dft-2d m-cd)
          total-cd (reduce + (map :power coeffs-cd))
          non-dc-cd (filter #(not (and (zero? (:k1 %)) (zero? (:k2 %)))) coeffs-cd)
          top-10-cd (take 10 (sort-by :power > non-dc-cd))]
      (println "  Top 10 non-DC modes:")
      (println (format "  %-6s %-6s %12s %10s %8s"
                       "k_c" "k_d" "Magnitude" "Power%" "Phase°"))
      (doseq [{:keys [k1 k2 magnitude power phase-deg]} top-10-cd]
        (println (format "  %-6d %-6d %12.1f %9.2f%% %7.1f°"
                         k1 k2 magnitude
                         (* 100.0 (/ power total-cd))
                         phase-deg)))))
  (println))

(defn part-6-observe []
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 6 — OBSERVE. SELAH.")
  (println "═══════════════════════════════════════════════════")
  (println "
  The eigendecomposition IS the space.
  Every letter of Torah is a superposition of these harmonics.

  The DC mode (k=0) is the average — the constant background hum.
  Everything else is structure: variation from the mean that the
  text imposes on the space.

  The a-axis has 7 modes. Seven harmonics of completeness.
  The c-axis has 13 modes. Thirteen harmonics of love.
  The d-axis has 67 modes. Sixty-seven harmonics of understanding.
  The b-axis has 50 modes. Fifty harmonics of jubilee.

  Together: 7 × 50 × 13 × 67 = 304,850 eigenmodes.
  Each one is a 4D pattern — a standing wave in the Torah.

  The dominant modes are what the structure WANTS to say.
  The silent modes are what it chooses not to say.
  The phases are WHERE it says it.

  selah.
  "))

(defn run []
  (let [s (part-1-tensor)
        _ (part-2-marginals s)
        spectra (part-3-spectra s)
        _ (part-4-interpret s spectra)
        _ (part-5-2d-spectra s)
        _ (part-6-observe)]
    :done))

(comment
  ;; Run the full experiment
  (run)

  ;; Or piece by piece:
  (def s (build-space))
  (def m-a (marginal s :a))
  (def m-c (marginal s :c))
  (def m-d (marginal s :d))
  (def m-b (marginal s :b))

  ;; DFT
  (def spec-a (dft m-a))
  (def spec-c (dft m-c))
  (def spec-d (dft m-d))

  ;; 2D
  (def m-ac (marginal-2d s :a :c))
  (def spec-ac (dft-2d m-ac))

  ;; Top modes
  (->> spec-ac
       (filter #(not (and (zero? (:k1 %)) (zero? (:k2 %)))))
       (sort-by :power >)
       (take 10))

  :end)
