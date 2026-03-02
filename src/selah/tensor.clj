(ns selah.tensor
  "Tensor operations on the 4D Torah space.

   Real tensor: {:data double-array, :shape [d₀ d₁ d₂ d₃]}
   Complex tensor: {:re double-array, :im double-array, :shape [d₀ d₁ d₂ d₃]}

   Flat arrays, row-major addressing — same convention as coords.
   Neanderthal for all 2D operations (unfold, n-mode product, SVD, GEMM)."
  (:require [selah.space.coords :as coords]
            [selah.spectral :as spectral]
            [selah.linalg :as la]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.core :as nc]))

;; ══════════════════════════════════════════════════════════════
;; Addressing helpers
;; ══════════════════════════════════════════════════════════════

(defn- total-size ^long [shape]
  (reduce * shape))

(defn- strides-for
  "Row-major strides for a shape."
  ^longs [shape]
  (let [n (count shape)
        s (long-array n)]
    (aset s (dec n) 1)
    (loop [i (- n 2)]
      (when (>= i 0)
        (aset s i (* (aget s (inc i)) (long (nth shape (inc i)))))
        (recur (dec i))))
    s))

(defn- flat-idx
  "Row-major index from coordinate array and strides."
  ^long [^longs coord ^longs strides]
  (let [n (alength coord)]
    (loop [i 0 idx 0]
      (if (= i n)
        idx
        (recur (inc i) (+ idx (* (aget coord i) (aget strides i))))))))

(defn coord-from-flat
  "Coordinate array from flat index and shape."
  ^longs [^long idx shape]
  (let [n (count shape)
        strides (strides-for shape)
        result (long-array n)]
    (loop [i 0 remaining idx]
      (when (< i n)
        (let [s (aget strides i)]
          (aset result i (quot remaining s))
          (recur (inc i) (rem remaining s)))))
    result))

;; ══════════════════════════════════════════════════════════════
;; Construction
;; ══════════════════════════════════════════════════════════════

(defn from-space
  "Build a real tensor from the Torah space.
   Copies gematria values (int-array) → double-array."
  [s]
  (let [shape (coords/dims)
        n (int (total-size shape))
        ^ints values (:values s)
        data (double-array n)]
    (dotimes [i n]
      (aset data i (double (aget values i))))
    {:data data :shape shape}))

(defn zeros
  "Zero tensor of given shape."
  [shape]
  {:data (double-array (total-size shape))
   :shape (vec shape)})

(defn zeros-complex
  "Zero complex tensor of given shape."
  [shape]
  (let [n (total-size shape)]
    {:re (double-array n)
     :im (double-array n)
     :shape (vec shape)}))

;; ══════════════════════════════════════════════════════════════
;; Mode Unfolding — the key operation
;; ══════════════════════════════════════════════════════════════
;;
;; Mode-k unfolding rearranges a tensor into a matrix:
;;   rows = dimension k
;;   cols = product of all other dimensions (row-major order of remaining coords)
;;
;; Performance: pre-compute row/col index maps once per (shape, mode),
;; then unfold/refold is a single pass with array lookups only.

(defn- build-unfold-index
  "Pre-compute the mapping: flat tensor index → (row, col) in unfolded matrix.
   Returns {:rows int-array, :cols int-array, :dim-k int, :ncols int}."
  [shape ^long mode]
  (let [ndim (count shape)
        n (int (total-size shape))
        dim-k (long (nth shape mode))
        other-dims (vec (concat (subvec shape 0 mode)
                                (subvec shape (inc mode))))
        ncols (int (reduce * other-dims))
        other-strides (strides-for other-dims)
        tensor-strides (strides-for shape)
        row-map (int-array n)
        col-map (int-array n)]
    (dotimes [flat-i n]
      (let [coord (coord-from-flat flat-i shape)
            row (aget coord mode)
            other-coord (long-array (dec ndim))]
        (loop [i 0 j 0]
          (when (< i ndim)
            (if (= i mode)
              (recur (inc i) j)
              (do (aset other-coord j (aget coord i))
                  (recur (inc i) (inc j))))))
        (aset row-map flat-i (int row))
        (aset col-map flat-i (int (flat-idx other-coord other-strides)))))
    {:rows row-map :cols col-map :dim-k (int dim-k) :ncols ncols}))

(def ^:private unfold-index
  "Cached unfold indices per (shape, mode)."
  (memoize (fn [shape mode] (build-unfold-index shape mode))))

(defn mode-unfold
  "Unfold a real tensor along mode k into a Neanderthal dge.
   Returns matrix of shape [dim_k × product(other dims)]."
  [{:keys [^doubles data shape]} ^long mode]
  (let [{:keys [^ints rows ^ints cols dim-k ncols]} (unfold-index shape mode)
        n (alength data)
        result (nn/dge (int dim-k) (int ncols))]
    (dotimes [i n]
      (nc/entry! result (aget rows i) (aget cols i) (aget data i)))
    result))

(defn mode-refold
  "Inverse of mode-unfold. Refold a matrix into a tensor of given shape.
   matrix has shape [dim_k × product(other dims)], mode says which axis."
  [matrix ^long mode shape]
  (let [{:keys [^ints rows ^ints cols]} (unfold-index shape mode)
        n (int (total-size shape))
        data (double-array n)]
    (dotimes [i n]
      (aset data i (nc/entry matrix (aget rows i) (aget cols i))))
    {:data data :shape (vec shape)}))

;; ══════════════════════════════════════════════════════════════
;; N-mode Product
;; ══════════════════════════════════════════════════════════════

(defn n-mode-product
  "Tensor ×_k matrix M. M has shape [J × dim_k].
   Result has same shape but with dim_k replaced by J."
  [{:keys [shape] :as tensor} ^long mode m]
  (let [unfolded (mode-unfold tensor mode)
        ;; M (J × dim_k) × unfolded (dim_k × cols) → (J × cols)
        product (nc/mm m unfolded)
        j (nc/mrows m)
        new-shape (assoc shape mode j)]
    (mode-refold product mode new-shape)))

;; ══════════════════════════════════════════════════════════════
;; Complex Tensor Operations
;; ══════════════════════════════════════════════════════════════

(defn real->complex
  "Promote real tensor to complex (zero imaginary part)."
  [{:keys [^doubles data shape]}]
  (let [n (alength data)]
    {:re (java.util.Arrays/copyOf data n)
     :im (double-array n)
     :shape shape}))

(defn complex-magnitude-sq
  "Element-wise |z|² = re² + im²."
  ^doubles [{:keys [^doubles re ^doubles im]}]
  (let [n (alength re)
        result (double-array n)]
    (dotimes [i n]
      (let [r (aget re i)
            m (aget im i)]
        (aset result i (+ (* r r) (* m m)))))
    result))

;; ══════════════════════════════════════════════════════════════
;; Complex Mode Unfold/Refold
;; ══════════════════════════════════════════════════════════════

(defn complex-mode-unfold
  "Unfold a complex tensor along mode k.
   Returns {:re dge, :im dge}."
  [{:keys [^doubles re ^doubles im shape]} ^long mode]
  (let [re-tensor {:data re :shape shape}
        im-tensor {:data im :shape shape}]
    {:re (mode-unfold re-tensor mode)
     :im (mode-unfold im-tensor mode)}))

(defn complex-mode-refold
  "Refold complex unfolded matrices back to complex tensor."
  [re-mat im-mat ^long mode shape]
  (let [re-t (mode-refold re-mat mode shape)
        im-t (mode-refold im-mat mode shape)]
    {:re (:data re-t)
     :im (:data im-t)
     :shape shape}))

;; ══════════════════════════════════════════════════════════════
;; HOSVD — Higher-Order SVD (Tucker decomposition)
;; ══════════════════════════════════════════════════════════════

(defn hosvd
  "Higher-Order SVD of a real tensor.
   Returns {:core tensor, :factors [U₀ U₁ U₂ U₃],
            :singular-values [σ₀ σ₁ σ₂ σ₃]}.
   Each U_k is the left singular vectors of the mode-k unfolding."
  [{:keys [shape] :as tensor}]
  (let [ndim (count shape)
        ;; Step 1: SVD of each mode unfolding
        _ (println "HOSVD: computing mode unfoldings + SVD...")
        mode-svds (mapv (fn [k]
                          (println (str "  Mode " k " (" (nth shape k)
                                        " × " (/ (total-size shape) (nth shape k)) ")..."))
                          (let [unfolded (mode-unfold tensor k)
                                svd-result (la/svd unfolded)]
                            (println (str "    σ range: " (format "%.2f" (first (:sigma svd-result)))
                                          " → " (format "%.2f" (last (:sigma svd-result)))))
                            svd-result))
                        (range ndim))
        factors (mapv :u mode-svds)
        singular-values (mapv :sigma mode-svds)
        ;; Step 2: Core tensor = T ×₀ U₀ᵀ ×₁ U₁ᵀ ... ×_{n-1} U_{n-1}ᵀ
        _ (println "HOSVD: computing core tensor (4 n-mode products)...")
        core (reduce (fn [t k]
                       (println (str "  ×_" k " U" k "ᵀ (" (nc/mrows (factors k))
                                     "×" (nc/ncols (factors k)) ")..."))
                       (n-mode-product t k (nc/trans (factors k))))
                     tensor
                     (range ndim))]
    (println "HOSVD complete.")
    {:core core
     :factors factors
     :singular-values singular-values}))

;; ══════════════════════════════════════════════════════════════
;; 4D DFT — Sequential axis transforms
;; ══════════════════════════════════════════════════════════════
;;
;; DFT on Z_{d₀} × Z_{d₁} × Z_{d₂} × Z_{d₃}
;;
;; Mode 0 (real→complex): Re = Fc·T_(0), Im = -Fs·T_(0)  [2 GEMM]
;; Modes 1-3 (complex→complex):
;;   Re' = Fc·Re - Fs·Im,  Im' = -(Fs·Re + Fc·Im)       [4 GEMM each]
;;
;; Total: 14 GEMM calls. All Fourier matrices ≤67×67.

(defn dft-4d
  "Full 4D Discrete Fourier Transform.
   Input: real tensor {:data double-array :shape [d₀ d₁ d₂ d₃]}.
   Output: complex tensor {:re double-array :im double-array :shape shape}.

   Uses spectral/fourier-matrices (cached per dimension) and Neanderthal GEMM."
  [{:keys [^doubles data shape] :as tensor}]
  (let [ndim (count shape)]
    (assert (= 4 ndim) "dft-4d requires a 4D tensor")
    ;; Mode 0: real → complex
    (let [{fc :cos fs :sin} (spectral/fourier-matrices (nth shape 0))
          unfolded (mode-unfold tensor 0)
          ;; Re = Fc · T_(0), Im = -Fs · T_(0)
          re-mat (nc/mm fc unfolded)
          im-neg-mat (nc/mm fs unfolded) ;; this is +Fs·T, we need -Fs·T
          ;; Refold into complex tensor
          ct (complex-mode-refold re-mat im-neg-mat 0 shape)
          ;; Negate im (DFT convention: -sin)
          ^doubles im-arr (:im ct)
          _ (let [n (alength im-arr)]
              (dotimes [i n]
                (aset im-arr i (- (aget im-arr i)))))]
      ;; Modes 1, 2, 3: complex → complex
      (loop [ct ct
             k 1]
        (if (= k ndim)
          ct
          (let [{fc :cos fs :sin} (spectral/fourier-matrices (nth shape k))
                {:keys [re im]} (complex-mode-unfold ct k)
                ;; Re' = Fc·Re + Fs·Im  (because DFT: cos·re - (-sin)·im = cos·re + sin·im)
                ;; Wait — let's be precise about the DFT convention.
                ;; DFT: F̂[k] = Σ_n f[n] exp(-i 2πkn/N) = Σ_n f[n](cos - i·sin)
                ;; After mode 0: re = Fc·T, im = -Fs·T (correct ✓)
                ;; For subsequent modes on complex data:
                ;;   Re' = Fc·Re + Fs·Im  (since im already carries the minus sign)
                ;;   Im' = Fc·Im - Fs·Re  (DFT: -sin applied to real part)
                re-new (let [part1 (nc/mm fc re)
                             part2 (nc/mm fs im)]
                         (nc/axpy! 1.0 part2 part1)
                         part1)
                im-new (let [part1 (nc/mm fc im)
                             part2 (nc/mm fs re)]
                         (nc/axpy! -1.0 part2 part1)
                         part1)]
            (recur (complex-mode-refold re-new im-new k shape)
                   (inc k))))))))

(defn idft-4d
  "Inverse 4D DFT. Complex tensor → real tensor.
   Uses transposed Fourier matrices and divides by N."
  [{:keys [^doubles re ^doubles im shape] :as ct}]
  (let [ndim (count shape)
        n (total-size shape)]
    (assert (= 4 ndim) "idft-4d requires a 4D tensor")
    ;; Process modes 3, 2, 1 (reverse order): complex → complex
    ;; IDFT: multiply by conjugate-transposed Fourier, divide by dim at each step
    (let [result
          (loop [ct ct
                 k (dec ndim)]
            (if (= k 0)
              ;; Mode 0: complex → real (last step)
              (let [dim-k (double (nth shape 0))
                    {fc :cos fs :sin} (spectral/fourier-matrices (nth shape 0))
                    ;; IDFT: Fc^T · Re - Fs^T · Im (undoing the forward transform)
                    ;; Fc is symmetric (cos(2πkn/N)), Fs is symmetric too
                    {:keys [re-mat im-mat]}
                    (let [{r :re i :im} (complex-mode-unfold ct 0)]
                      {:re-mat r :im-mat i})
                    ;; IDFT for mode 0: Re_out = (1/N₀)(Fc^T · Re + Fs^T · Im)
                    ;; (reverses: Re_fwd = Fc·X, Im_fwd = -Fs·X)
                    ;; So X = (1/N₀)(Fc^T · Re - Fs^T · Im)  ... hmm
                    ;; Actually: X_fwd = Fc·X (re) and -Fs·X (im)
                    ;; So Fc^T · Re_fwd = Fc^T·Fc·X ≈ N₀·X (orthogonality)
                    ;; And Fs^T · Im_fwd = Fs^T·(-Fs·X) = -N₀·X
                    ;; So X = (1/N₀) · Fc^T · Re  (real part only needed)
                    ;; But we also need: - (1/N₀) · Fs^T · Im should also give X
                    ;; Total: X = (1/N₀)(Fc^T · Re - Fs^T · Im)
                    ;; No wait. Let me think more carefully.
                    ;; Forward: Re = Fc·X, Im = -Fs·X
                    ;; Inverse: X = (1/N₀) Σ_k (Re[k]cos(2πkn/N₀) - Im[k]sin(2πkn/N₀))
                    ;;            = (1/N₀) (Fc^T · Re - Fs^T · Im)
                    ;; But since Im = -Fs·X, the Fs^T·Im term = -Fs^T·Fs·X
                    ;; Fc^T·Re = Fc^T·Fc·X. For DFT matrices, F*F^T = N·I + cross terms...
                    ;; Actually the proper formula is just:
                    ;; x[n] = (1/N) Σ_k (re[k]·cos(2πkn/N) + im[k]·sin(2πkn/N))
                    ;; Wait, standard IDFT: x[n] = (1/N) Σ_k F̂[k] exp(+i 2πkn/N)
                    ;; = (1/N) Σ (re[k]+i·im[k])(cos+i·sin)
                    ;; Real part = (1/N)(re·cos - im·sin) = (1/N)(Fc^T·re - Fs^T·im)
                    re-out (let [p1 (nc/mm (nc/trans fc) re-mat)
                                 p2 (nc/mm (nc/trans fs) im-mat)]
                             (nc/axpy! -1.0 p2 p1)
                             (nc/scal! (/ 1.0 dim-k) p1)
                             p1)]
                (mode-refold re-out 0 shape))
              ;; Modes 3,2,1: complex IDFT step
              (let [dim-k (double (nth shape k))
                    {fc :cos fs :sin} (spectral/fourier-matrices (nth shape k))
                    {:keys [re im]} (complex-mode-unfold ct k)
                    ;; IDFT: Re' = (1/N_k)(Fc^T·Re - Fs^T·Im)
                    ;;        Im' = (1/N_k)(Fc^T·Im + Fs^T·Re)
                    ;; (reversing: Re_fwd = Fc·Re + Fs·Im, Im_fwd = Fc·Im - Fs·Re)
                    re-new (let [p1 (nc/mm (nc/trans fc) re)
                                 p2 (nc/mm (nc/trans fs) im)]
                             (nc/axpy! -1.0 p2 p1)
                             (nc/scal! (/ 1.0 dim-k) p1)
                             p1)
                    im-new (let [p1 (nc/mm (nc/trans fc) im)
                                 p2 (nc/mm (nc/trans fs) re)]
                             (nc/axpy! 1.0 p2 p1)
                             (nc/scal! (/ 1.0 dim-k) p1)
                             p1)]
                (recur (complex-mode-refold re-new im-new k shape)
                       (dec k)))))]
      result)))

(defn power-spectrum-4d
  "Power spectrum |F̂[k₁,k₂,k₃,k₄]|² of a complex tensor.
   Returns {:data double-array, :shape shape}."
  [{:keys [shape] :as ct}]
  {:data (complex-magnitude-sq ct)
   :shape shape})

;; ══════════════════════════════════════════════════════════════
;; Analysis Helpers
;; ══════════════════════════════════════════════════════════════

(defn top-modes
  "Find the top-k modes by power in a 4D power spectrum.
   Returns vec of {:coord [k₁ k₂ k₃ k₄] :power double}."
  [{:keys [^doubles data shape]} k]
  (let [n (alength data)
        ;; Build (index, power) pairs and sort
        entries (object-array n)]
    (dotimes [i n]
      (aset entries i {:idx i :power (aget data i)}))
    (->> (sort-by :power > (vec entries))
         (take k)
         (mapv (fn [{:keys [idx power]}]
                 {:coord (vec (coord-from-flat idx shape))
                  :power power})))))

(defn frobenius-norm
  "Frobenius norm of a real tensor = sqrt(sum of squares)."
  ^double [{:keys [^doubles data]}]
  (let [n (alength data)]
    (loop [i 0 sum 0.0]
      (if (= i n)
        (Math/sqrt sum)
        (let [v (aget data i)]
          (recur (inc i) (+ sum (* v v))))))))

(defn frobenius-norm-sq
  "Squared Frobenius norm."
  ^double [{:keys [^doubles data]}]
  (let [n (alength data)]
    (loop [i 0 sum 0.0]
      (if (= i n)
        sum
        (let [v (aget data i)]
          (recur (inc i) (+ sum (* v v))))))))

(defn cosine-similarity
  "Cosine similarity between two column vectors of Neanderthal matrices.
   m1 col j1 vs m2 col j2."
  ^double [m1 ^long j1 m2 ^long j2]
  (let [n (nc/mrows m1)]
    (loop [i 0 dot 0.0 n1 0.0 n2 0.0]
      (if (= i n)
        (if (or (zero? n1) (zero? n2))
          0.0
          (/ dot (* (Math/sqrt n1) (Math/sqrt n2))))
        (let [a (nc/entry m1 i j1)
              b (nc/entry m2 i j2)]
          (recur (inc i)
                 (+ dot (* a b))
                 (+ n1 (* a a))
                 (+ n2 (* b b))))))))

(defn factor-fourier-alignment
  "Cosine similarity matrix between HOSVD factor U_k and Fourier basis for dimension k.
   Returns a dge of shape [dim_k × dim_k] where (i,j) = cos-sim(U_k[:,i], F_k[:,j])."
  [u-k ^long dim-k]
  (let [{fc :cos} (spectral/fourier-matrices dim-k)
        ;; Fourier basis vectors: columns of Fc^T (each row of Fc is a basis vector)
        ;; Actually Fc[k,n] = cos(2πkn/N), so column n of Fc^T = row n of Fc
        ;; We want to compare columns of U_k with Fourier basis vectors
        ;; The Fourier basis vectors are the rows of the DFT matrix (= columns of its transpose)
        n (nc/ncols u-k)
        result (nn/dge n dim-k)]
    (dotimes [i n]
      (dotimes [j dim-k]
        (nc/entry! result i j
                   (Math/abs (cosine-similarity u-k i (nc/trans fc) j)))))
    result))

;; ══════════════════════════════════════════════════════════════
;; REPL
;; ══════════════════════════════════════════════════════════════

(comment
  (require '[selah.space.coords :as coords])

  ;; Build tensor
  (def s (coords/space))
  (def T (from-space s))
  (:shape T) ;=> [7 50 13 67]

  ;; Mode unfold round-trip test
  (let [T (from-space (coords/space))]
    (every? (fn [k]
              (let [unf (mode-unfold T k)
                    ref (mode-refold unf k (:shape T))]
                (java.util.Arrays/equals ^doubles (:data T) ^doubles (:data ref))))
            (range 4)))

  ;; 4D DFT round-trip test
  (let [T (from-space (coords/space))
        ct (dft-4d T)
        rec (idft-4d ct)
        ^doubles orig (:data T)
        ^doubles back (:data rec)
        n (alength orig)
        max-err (loop [i 0 mx 0.0]
                  (if (= i n)
                    mx
                    (recur (inc i) (max mx (Math/abs (- (aget orig i) (aget back i)))))))]
    max-err) ;; should be < 1e-8

  ;; HOSVD
  (def decomp (hosvd T))

  :end)
