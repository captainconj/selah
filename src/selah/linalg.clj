(ns selah.linalg
  "Thin Neanderthal wrapper — the linear algebra verbs we need.

   Handles the column-major conversion layer so callers can think
   in row-major (seq of seqs, vec of double-arrays) and get native
   BLAS/LAPACK via MKL underneath."
  (:require [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.linalg :as la]))

;; ══════════════════════════════════════════════════════════════
;; Construction
;; ══════════════════════════════════════════════════════════════

(defn matrix
  "Build a Neanderthal dge from a seq of row vectors.
   Each row is a seq of numbers. Result is column-major internally
   but indexed as you'd expect: (nc/entry m row col)."
  [rows]
  (let [m (count rows)
        n (count (first rows))]
    (nn/dge m n (mapcat identity rows) {:layout :row})))

(defn from-double-arrays
  "Convert a vec of double-arrays (row-major) to a Neanderthal dge.
   n = number of columns."
  [arrs n]
  (let [m (count arrs)
        result (nn/dge m n)]
    (dotimes [i m]
      (let [^doubles row (arrs i)]
        (dotimes [j n]
          (nc/entry! result i j (aget row j)))))
    result))

(defn vec->dv
  "Convert a Clojure seq to a Neanderthal double vector."
  [xs]
  (nn/dv (vec xs)))

(defn dge
  "Create an m×n zero matrix."
  [m n]
  (nn/dge m n))

;; ══════════════════════════════════════════════════════════════
;; Extraction
;; ══════════════════════════════════════════════════════════════

(defn to-vecs
  "Extract all rows of a matrix as Clojure vectors."
  [m]
  (mapv (fn [i] (vec (seq (nc/row m i))))
        (range (nc/mrows m))))

(defn to-double-arrays
  "Convert matrix back to vec of double-arrays (for interop with existing code)."
  [m]
  (let [rows (nc/mrows m)
        cols (nc/ncols m)]
    (mapv (fn [i]
            (let [arr (double-array cols)]
              (dotimes [j cols]
                (aset arr j (nc/entry m i j)))
              arr))
          (range rows))))

(defn row-vec
  "Extract row i as a Clojure vector."
  [m i]
  (vec (seq (nc/row m i))))

(defn col-vec
  "Extract column j as a Clojure vector."
  [m j]
  (vec (seq (nc/col m j))))

;; ══════════════════════════════════════════════════════════════
;; Matrix Operations
;; ══════════════════════════════════════════════════════════════

(defn mat-mul
  "Multiply two matrices. Returns a new matrix."
  [a b]
  (nc/mm a b))

(defn mat-pow
  "Raise a square matrix to power p by repeated squaring.
   Uses BLAS GEMM at each step."
  [m p]
  (cond
    (= p 1) (nc/copy m)
    (even? p) (let [half (mat-pow m (/ p 2))]
                (nc/mm half half))
    :else (nc/mm m (mat-pow m (dec p)))))

(defn row-normalize!
  "Normalize each row to sum to 1 (in place). Makes a stochastic matrix.
   Rows that sum to 0 are left as zeros."
  [m]
  (dotimes [i (nc/mrows m)]
    (let [r (nc/row m i)
          s (nc/asum r)]
      (when (pos? s)
        (nc/scal! (/ 1.0 s) r))))
  m)

;; ══════════════════════════════════════════════════════════════
;; Decompositions
;; ══════════════════════════════════════════════════════════════

(defn- ensure-col-major
  "Copy matrix to column-major layout if needed."
  [m]
  (let [n (nc/mrows m)
        c (nc/ncols m)
        result (nn/dge n c)]
    (dotimes [i n]
      (dotimes [j c]
        (nc/entry! result i j (nc/entry m i j))))
    result))

(defn eigendecomp
  "Eigendecomposition of a square matrix.
   Returns {:values vec-of-doubles :vectors matrix-of-right-eigenvectors}.
   Values are real parts (imaginary returned separately if complex)."
  [m]
  (let [n (nc/mrows m)
        mc (ensure-col-major m)
        w  (nn/dge n 2)
        vr (nn/dge n n)]
    (la/ev! mc w nil vr)
    {:values (vec (seq (nc/col w 0)))
     :imag   (vec (seq (nc/col w 1)))
     :vectors vr}))

(defn svd
  "Singular value decomposition.
   Returns {:u matrix :sigma vec-of-singular-values :vt matrix}."
  [m]
  (let [result (la/svd m true true)]
    {:u (:u result)
     :sigma (vec (seq (nc/dia (:sigma result))))
     :vt (:vt result)}))

(defn stochastic-eigendecomp
  "Eigendecomposition optimized for stochastic matrices.
   The stationary distribution is the left eigenvector for λ=1.
   Returns {:stationary vec :eigenvalues vec :vectors matrix}."
  [m]
  (let [n (nc/mrows m)
        ;; Left eigenvectors = right eigenvectors of M^T
        mc (ensure-col-major m)
        mt (nn/dge n n)
        _ (dotimes [i n]
            (dotimes [j n]
              (nc/entry! mt i j (nc/entry mc j i))))
        w  (nn/dge n 2)
        vl (nn/dge n n)]
    (la/ev! mt w nil vl)
    (let [evals (vec (seq (nc/col w 0)))
          ;; Find the eigenvector for λ closest to 1
          idx (apply max-key #(- 1.0 (Math/abs (- 1.0 (evals %)))) (range n))
          evec (vec (seq (nc/col vl idx)))
          ;; Normalize to probability distribution
          total (reduce + (map #(Math/abs %) evec))
          stationary (mapv #(/ (Math/abs %) total) evec)]
      {:stationary stationary
       :eigenvalues evals
       :vectors vl
       :dominant-idx idx})))

;; ══════════════════════════════════════════════════════════════
;; Convenience
;; ══════════════════════════════════════════════════════════════

(defn entry
  "Get entry at (row, col)."
  [m row col]
  (nc/entry m row col))

(defn entry!
  "Set entry at (row, col)."
  [m row col val]
  (nc/entry! m row col val))

(defn mrows [m] (nc/mrows m))
(defn ncols [m] (nc/ncols m))

(defn copy [m] (nc/copy m))

(defn scal!
  "Scale all entries by alpha, in place."
  [alpha m]
  (nc/scal! alpha m))

(defn axpy!
  "y = alpha*x + y, in place."
  [alpha x y]
  (nc/axpy! alpha x y))
