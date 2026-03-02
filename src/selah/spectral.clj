(ns selah.spectral
  "Spectral analysis of the Torah 4D space.

   The Torah is a function f: Z₇ × Z₅₀ × Z₁₃ × Z₆₇ → ℤ.
   On a product of cyclic groups, the natural eigenbasis is the
   Discrete Fourier Transform. Each mode is a standing wave.

   This namespace composes two operations:
   - Spatial selection (breastplate): pick a slab, fiber, or hyperplane
   - Spectral selection (DFT): decompose into harmonics

   Urim = which positions (spatial filter).
   Thummim = which frequencies (spectral filter).
   Together: the doubly-focused lens.

   DFT implemented via Neanderthal matrix multiply with pre-computed
   Fourier matrices. BLAS GEMM makes batch operations fast."
  (:require [selah.space.coords :as coords]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.core :as nc]))

;; ══════════════════════════════════════════════════════════════
;; Fourier Matrices — pre-computed per dimension
;; ══════════════════════════════════════════════════════════════

(defn- build-fourier-matrices
  "Build the cos and sin Fourier matrices for dimension N.
   F_cos[k,n] = cos(2π·k·n/N)
   F_sin[k,n] = sin(2π·k·n/N)
   Returns {:cos dge :sin dge}."
  [N]
  (let [fc (nn/dge N N)
        fs (nn/dge N N)
        two-pi-over-n (/ (* 2.0 Math/PI) (double N))]
    (dotimes [k N]
      (dotimes [n N]
        (let [angle (* two-pi-over-n k n)]
          (nc/entry! fc k n (Math/cos angle))
          (nc/entry! fs k n (Math/sin angle)))))
    {:cos fc :sin fs}))

(def fourier-matrices
  "Cached Fourier matrices for each axis dimension."
  (memoize build-fourier-matrices))

;; ══════════════════════════════════════════════════════════════
;; DFT — via matrix multiply
;; ══════════════════════════════════════════════════════════════

(defn dft
  "1D Discrete Fourier Transform of a real signal via matrix multiply.
   Returns vec of {:k :re :im :magnitude :power :phase :phase-deg}."
  [^doubles signal]
  (let [N (alength signal)
        {:keys [cos sin]} (fourier-matrices N)
        x (nn/dv (vec (map #(aget signal %) (range N))))
        re-vec (nc/mv cos x)
        im-vec (nc/mv sin x)]
    (mapv
     (fn [k]
       (let [re (nc/entry re-vec k)
             im (- (nc/entry im-vec k))  ;; DFT convention: -sin
             power (+ (* re re) (* im im))]
         {:k k :re re :im im
          :magnitude (Math/sqrt power) :power power
          :phase (Math/atan2 im re)
          :phase-deg (* (Math/atan2 im re) (/ 180.0 Math/PI))}))
     (range N))))

(defn idft
  "Inverse DFT. Reconstruct a signal from frequency coefficients.
   coeffs is a vec of {:re :im} (or full dft output maps).
   Returns double-array of length N."
  [coeffs]
  (let [N (count coeffs)
        {:keys [cos sin]} (fourier-matrices N)
        ;; Build re and im vectors from coefficients
        re-in (nn/dv (mapv :re coeffs))
        im-in (nn/dv (mapv :im coeffs))
        ;; IDFT: x[n] = (1/N) * sum_k (re[k]*cos(angle) - im[k]*sin(angle))
        ;; = (1/N) * (F_cos^T · re - F_sin^T · im)
        ;; Note: F_cos is symmetric so F_cos^T = F_cos
        re-part (nc/mv (nc/trans cos) re-in)
        im-part (nc/mv (nc/trans sin) im-in)
        result (double-array N)
        inv-n (/ 1.0 N)]
    (dotimes [n N]
      (aset result n (* inv-n (- (nc/entry re-part n)
                                 (nc/entry im-part n)))))
    result))

(defn dft-batch
  "Batch 1D DFT of M signals, each of length N.
   signals: vec of double-arrays, all length N.
   Returns vec of M DFT results (each a vec of coefficient maps)."
  [signals]
  (let [N (alength ^doubles (first signals))
        M (count signals)
        {:keys [cos sin]} (fourier-matrices N)
        ;; Pack signals as columns of an N×M matrix
        x-mat (nn/dge N M)
        _ (dotimes [j M]
            (let [^doubles sig (signals j)]
              (dotimes [i N]
                (nc/entry! x-mat i j (aget sig i)))))
        ;; Batch multiply: F · X gives all DFTs at once
        re-mat (nc/mm cos x-mat)
        im-mat (nc/mm sin x-mat)]
    (mapv
     (fn [j]
       (mapv
        (fn [k]
          (let [re (nc/entry re-mat k j)
                im (- (nc/entry im-mat k j))
                power (+ (* re re) (* im im))]
            {:k k :re re :im im
             :magnitude (Math/sqrt power) :power power
             :phase (Math/atan2 im re)
             :phase-deg (* (Math/atan2 im re) (/ 180.0 Math/PI))}))
        (range N)))
     (range M))))

(defn dft-2d
  "2D DFT over a row-major double-array.
   Input: {:data double-array :rows int :cols int}.
   Returns vec of {:k1 :k2 :re :im :magnitude :power :phase :phase-deg}.

   Uses the factorization:
   Re[k1,k2] = [Fc_r · X · Fc_c^T] - [Fs_r · X · Fs_c^T]
   Im[k1,k2] = -[Fs_r · X · Fc_c^T] - [Fc_r · X · Fs_c^T]"
  [{:keys [^doubles data rows cols]}]
  (let [{fc-r :cos fs-r :sin} (fourier-matrices rows)
        {fc-c :cos fs-c :sin} (fourier-matrices cols)
        ;; Pack data into a Neanderthal matrix (rows × cols)
        x (nn/dge rows cols)
        _ (dotimes [i rows]
            (dotimes [j cols]
              (nc/entry! x i j (aget data (+ (* i cols) j)))))
        ;; Four matrix products for the 2D DFT
        fc-c-t (nc/trans fc-c)
        fs-c-t (nc/trans fs-c)
        cc (nc/mm fc-r (nc/mm x fc-c-t))   ;; Fc_r · X · Fc_c^T
        ss (nc/mm fs-r (nc/mm x fs-c-t))   ;; Fs_r · X · Fs_c^T
        sc (nc/mm fs-r (nc/mm x fc-c-t))   ;; Fs_r · X · Fc_c^T
        cs (nc/mm fc-r (nc/mm x fs-c-t))   ;; Fc_r · X · Fs_c^T
        results (transient [])]
    (dotimes [k1 rows]
      (dotimes [k2 cols]
        (let [re (- (nc/entry cc k1 k2) (nc/entry ss k1 k2))
              im (- (- (nc/entry sc k1 k2)) (nc/entry cs k1 k2))
              power (+ (* re re) (* im im))]
          (conj! results
                 {:k1 k1 :k2 k2 :re re :im im
                  :magnitude (Math/sqrt power)
                  :power power
                  :phase (Math/atan2 im re)
                  :phase-deg (* (Math/atan2 im re) (/ 180.0 Math/PI))}))))
    (persistent! results)))

(defn idft-2d
  "Inverse 2D DFT. Reconstruct a matrix from frequency coefficients.
   coeffs: vec of {:k1 :k2 :re :im}.
   Returns {:data double-array :rows rows :cols cols}."
  [coeffs rows cols]
  (let [N (* rows cols)
        result (double-array N)
        inv-n (/ 1.0 N)]
    (dotimes [n1 rows]
      (dotimes [n2 cols]
        (let [sum (reduce
                   (fn [s {:keys [k1 k2 re im]}]
                     (let [angle (+ (/ (* 2.0 Math/PI k1 n1) rows)
                                    (/ (* 2.0 Math/PI k2 n2) cols))]
                       (+ s (- (* re (Math/cos angle))
                               (* im (Math/sin angle))))))
                   0.0 coeffs)]
          (aset result (+ (* n1 cols) n2) (* inv-n sum)))))
    {:data result :rows rows :cols cols}))

;; ══════════════════════════════════════════════════════════════
;; Spatial Selection — The Urim
;; ══════════════════════════════════════════════════════════════

(defn marginal
  "Sum of gematria values for each value of the given axis.
   Returns double-array of length dim(axis)."
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
  "Mean gematria value per hyperplane on the given axis."
  [s axis]
  (let [m (marginal s axis)
        n (alength m)
        per-plane (/ coords/total-letters n)]
    (dotimes [i n]
      (aset m i (/ (aget m i) (double per-plane))))
    m))

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
    {:data result :rows dim1 :cols dim2}))

(defn fiber-signal
  "Extract the gematria values along a single fiber.
   free-axis varies; fixed is a map of the other 3 axis values.
   Returns double-array of length dim(free-axis)."
  [s free-axis fixed]
  (let [positions (coords/fiber free-axis fixed)
        n (alength positions)
        result (double-array n)
        ^ints values (:values s)]
    (dotimes [i n]
      (aset result i (double (aget values (aget positions i)))))
    result))

(defn slab-signal
  "Extract the gematria values from a 2D slab.
   fixed: map of axis → value for axes to fix.
   Returns {:data double-array :rows dim1 :cols dim2 :axes [free1 free2]}."
  [s fixed]
  (let [positions (coords/slab fixed)
        n (alength positions)
        free-axes (vec (remove (set (keys fixed)) [:a :b :c :d]))
        _ (assert (= 2 (count free-axes)) "slab needs exactly 2 free axes")
        dim1 (case (first free-axes) :a (coords/dim-a) :b (coords/dim-b)
                                     :c (coords/dim-c) :d (coords/dim-d))
        dim2 (case (second free-axes) :a (coords/dim-a) :b (coords/dim-b)
                                      :c (coords/dim-c) :d (coords/dim-d))
        result (double-array n)
        ^ints values (:values s)]
    (dotimes [i n]
      (aset result i (double (aget values (aget positions i)))))
    {:data result :rows dim1 :cols dim2
     :axes free-axes :fixed fixed}))

;; ══════════════════════════════════════════════════════════════
;; Spectral Selection — The Thummim
;; ══════════════════════════════════════════════════════════════

(defn spectrum
  "DFT spectrum of a 1D signal. Convenience wrapper over dft."
  [^doubles signal]
  (dft signal))

(defn spectrum-2d
  "DFT spectrum of a 2D signal. Convenience wrapper over dft-2d."
  [slab-data]
  (dft-2d slab-data))

(defn power-spectrum
  "Just the power values from a DFT, as double-array."
  [coeffs]
  (let [n (count coeffs)
        result (double-array n)]
    (dotimes [i n]
      (aset result i (:power (coeffs i))))
    result))

(defn dc-component
  "The k=0 (DC) component — the mean level."
  [coeffs]
  (first coeffs))

(defn non-dc
  "All non-DC components (k > 0)."
  [coeffs]
  (subvec coeffs 1))

(defn dominant-mode
  "The non-DC mode with highest power."
  [coeffs]
  (apply max-key :power (non-dc coeffs)))

(defn spectral-character
  "Summary statistics of a 1D spectrum.
   Returns {:dc-power :non-dc-power :dc-fraction :dominant-k
            :dominant-power-fraction :n-modes}."
  [coeffs]
  (let [dc-power (:power (dc-component coeffs))
        non-dc-modes (non-dc coeffs)
        non-dc-power (reduce + (map :power non-dc-modes))
        total-power (+ dc-power non-dc-power)
        dominant (dominant-mode coeffs)]
    {:dc-power dc-power
     :non-dc-power non-dc-power
     :dc-fraction (/ dc-power total-power)
     :dominant-k (:k dominant)
     :dominant-power (:power dominant)
     :dominant-power-fraction (if (pos? non-dc-power)
                                (/ (:power dominant) non-dc-power)
                                0.0)
     :dominant-phase-deg (:phase-deg dominant)
     :n-modes (count coeffs)}))

;; ══════════════════════════════════════════════════════════════
;; The Doubly-Focused Lens — Urim × Thummim
;; ══════════════════════════════════════════════════════════════

(defn fiber-spectrum
  "DFT of a single fiber. Spatial selection + spectral decomposition.
   Returns full DFT coefficients for one 1D line through the space."
  [s free-axis fixed]
  (dft (fiber-signal s free-axis fixed)))

(defn slab-spectrum
  "2D DFT of a slab. The doubly-focused lens.
   Returns full 2D DFT coefficients for a 2D slice of the space."
  [s fixed]
  (dft-2d (slab-signal s fixed)))

(defn hyperplane-power
  "Average power spectrum across all fibers in a hyperplane.
   Fixes one axis at value, runs DFT on the orthogonal free-axis for every
   fiber in the hyperplane, accumulates power.
   Uses batch DFT via Neanderthal matrix multiply for speed.
   Returns double-array of length dim(free-axis) — average power per frequency."
  [s fixed-axis fixed-value free-axis]
  (let [free-dim (case free-axis :a (coords/dim-a) :b (coords/dim-b)
                                 :c (coords/dim-c) :d (coords/dim-d))
        all-axes [:a :b :c :d]
        varying (remove #{fixed-axis free-axis} all-axes)
        dims (mapv #(case % :a (coords/dim-a) :b (coords/dim-b)
                            :c (coords/dim-c) :d (coords/dim-d))
                   varying)
        ;; Collect all fiber signals
        signals (vec
                 (for [v1 (range (dims 0))
                       v2 (range (dims 1))]
                   (let [fixed (assoc (zipmap varying [v1 v2])
                                      fixed-axis fixed-value)]
                     (fiber-signal s free-axis fixed))))
        ;; Batch DFT via matrix multiply
        all-dfts (dft-batch signals)
        n-fibers (count signals)
        power-sum (double-array free-dim)]
    ;; Accumulate power
    (doseq [dft-result all-dfts]
      (dotimes [k free-dim]
        (aset power-sum k (+ (aget power-sum k) (:power (dft-result k))))))
    ;; Average
    (let [nf (double n-fibers)]
      (dotimes [k free-dim]
        (aset power-sum k (/ (aget power-sum k) nf))))
    power-sum))

;; ══════════════════════════════════════════════════════════════
;; Filtering — Reconstruction from Selected Modes
;; ══════════════════════════════════════════════════════════════

(defn filter-modes
  "Zero out modes not in keep-set. Returns modified coefficients.
   keep-set: set of k values (1D) to keep. k=0 (DC) always kept unless excluded."
  [coeffs keep-set]
  (mapv (fn [{:keys [k] :as coeff}]
          (if (contains? keep-set k)
            coeff
            (assoc coeff :re 0.0 :im 0.0 :magnitude 0.0 :power 0.0)))
        coeffs))

(defn low-pass
  "Keep only modes k <= cutoff (plus DC at k=0)."
  [coeffs cutoff]
  (filter-modes coeffs (set (range 0 (inc cutoff)))))

(defn band-pass
  "Keep only modes in [k-low, k-high]."
  [coeffs k-low k-high]
  (filter-modes coeffs (set (range k-low (inc k-high)))))

(defn high-pass
  "Keep only modes k >= cutoff."
  [coeffs cutoff]
  (filter-modes coeffs (set (range cutoff (count coeffs)))))

(defn reconstruct
  "Reconstruct a filtered signal from modified DFT coefficients."
  [coeffs]
  (idft coeffs))

(defn residual
  "The signal that remains after removing selected modes.
   Subtracts the reconstruction of keep-set from the original signal."
  [^doubles original coeffs remove-set]
  (let [removed (filter-modes coeffs remove-set)
        reconstructed (idft removed)
        n (alength original)
        result (double-array n)]
    (dotimes [i n]
      (aset result i (- (aget original i) (aget reconstructed i))))
    result))

;; ══════════════════════════════════════════════════════════════
;; Analysis helpers
;; ══════════════════════════════════════════════════════════════

(defn axis-spectra
  "Compute all four 1D marginal spectra. Returns map of axis → coefficients."
  [s]
  (into {} (for [axis [:a :b :c :d]]
             [axis (dft (marginal-mean s axis))])))

(defn compare-spectra
  "Compare spectral characters of all four axes.
   Returns vec of {:axis :character} maps."
  [s]
  (mapv (fn [axis]
          {:axis axis
           :character (spectral-character (dft (marginal-mean s axis)))})
        [:a :b :c :d]))

(defn slab-fingerprint
  "Spectral fingerprint of a 2D slab: the 2D DFT power distribution.
   Returns {:spectrum (vec of maps), :character (summary stats),
            :top-modes (top k non-DC modes by power)}."
  [s fixed & {:keys [top-k] :or {top-k 10}}]
  (let [sig (slab-signal s fixed)
        spec (dft-2d sig)
        dc (first (filter #(and (zero? (:k1 %)) (zero? (:k2 %))) spec))
        non-dc (remove #(and (zero? (:k1 %)) (zero? (:k2 %))) spec)
        non-dc-power (reduce + (map :power non-dc))
        total-power (reduce + (map :power spec))
        top (vec (take top-k (sort-by :power > non-dc)))]
    {:spectrum spec
     :dc dc
     :dc-fraction (/ (:power dc) total-power)
     :non-dc-power non-dc-power
     :top-modes top
     :axes (:axes sig)
     :fixed fixed
     :shape [(:rows sig) (:cols sig)]}))
