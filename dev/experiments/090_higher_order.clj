(ns experiments.090-higher-order
  "Higher-order Torah space — tensor, 4D DFT, M·A.

   Three decompositions of the same object:
   A. HOSVD — data-driven: what are the axes really doing?
   B. 4D DFT — all 304,850 Fourier coefficients: what standing waves exist?
   C. M·A — second-order oracle: where do reading and meaning agree?

   The Torah is T[a,b,c,d] on Z₇ × Z₅₀ × Z₁₃ × Z₆₇ = 304,850 positions.
   We've done 1D marginal DFTs (087), stochastic oracle M (088),
   and Hebbian affinity A (089). Now: the full picture."
  (:require [selah.space.coords :as coords]
            [selah.tensor :as tensor]
            [selah.spectral :as spectral]
            [selah.linalg :as la]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [experiments.088-stochastic-oracle :as exp088]
            [experiments.089-hebbian-oracle :as exp089]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.core :as nc]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════════════
;; Part 1: Build the Tensor
;; ══════════════════════════════════════════════════════════════

(defn build-tensor []
  (println "\n── Part 1: Building the Torah tensor ──")
  (let [s (coords/space)
        T (tensor/from-space s)]
    (println (str "  Shape: " (:shape T)))
    (println (str "  Total elements: " (alength ^doubles (:data T))))
    (println (str "  Frobenius norm: " (format "%.2f" (tensor/frobenius-norm T))))
    T))

;; ══════════════════════════════════════════════════════════════
;; Part 2: HOSVD — Tucker Decomposition
;; ══════════════════════════════════════════════════════════════

(defn analyze-singular-values
  "Report singular value spectrum per mode."
  [{:keys [singular-values]}]
  (println "\n── HOSVD: Singular Values per Mode ──")
  (let [axis-names ["a (completeness, 7)" "b (jubilee, 50)"
                     "c (love, 13)" "d (understanding, 67)"]]
    (doseq [[k svs] (map-indexed vector singular-values)]
      (let [total-sq (reduce + (map #(* % %) svs))
            cumulative (reductions + (map #(* % %) svs))]
        (println (str "\n  Mode " k " — " (axis-names k) ":"))
        (println (str "    Rank: " (count svs)))
        (println (str "    σ₀=" (format "%.2f" (first svs))
                       "  σ₁=" (format "%.2f" (second svs))
                       "  σ_last=" (format "%.2f" (last svs))))
        (println "    Energy concentration (cumulative % of Frobenius²):")
        (doseq [i (range (min 10 (count svs)))]
          (let [pct (* 100.0 (/ (nth cumulative i) total-sq))]
            (println (format "      top-%d: %6.2f%%" (inc i) pct))))))))

(defn analyze-core-sparsity
  "How sparse is the core tensor? Sparse = axes decouple."
  [{:keys [core]}]
  (println "\n── HOSVD: Core Tensor Sparsity ──")
  (let [^doubles data (:data core)
        n (alength data)
        frob-sq (loop [i 0 s 0.0]
                  (if (= i n) s
                      (recur (inc i) (+ s (* (aget data i) (aget data i))))))
        ;; Sort by magnitude
        sorted (sort > (map #(Math/abs %) (vec data)))
        cumulative (reductions + (map #(* % %) sorted))
        ;; How many entries capture 90%, 99%, 99.9%?
        n90 (inc (count (take-while #(< (/ % frob-sq) 0.90) cumulative)))
        n99 (inc (count (take-while #(< (/ % frob-sq) 0.99) cumulative)))
        n999 (inc (count (take-while #(< (/ % frob-sq) 0.999) cumulative)))]
    (println (str "  Core shape: " (:shape core)))
    (println (str "  Total entries: " n))
    (println (str "  Frobenius²: " (format "%.2f" frob-sq)))
    (println (str "  Entries for 90% energy:  " n90 " (" (format "%.2f%%" (* 100.0 (/ (double n90) n))) " of total)"))
    (println (str "  Entries for 99% energy:  " n99 " (" (format "%.2f%%" (* 100.0 (/ (double n99) n))) " of total)"))
    (println (str "  Entries for 99.9% energy: " n999 " (" (format "%.2f%%" (* 100.0 (/ (double n999) n))) " of total)"))
    {:n90 n90 :n99 n99 :n999 n999 :total n :frob-sq frob-sq}))

(defn analyze-factor-fourier
  "Compare HOSVD factors with Fourier basis — are they the same?"
  [{:keys [factors singular-values]}]
  (println "\n── HOSVD vs Fourier: Factor Alignment ──")
  (let [dims (coords/dims)
        axis-names ["a" "b" "c" "d"]]
    (doseq [k (range 4)]
      (let [u-k (factors k)
            dim-k (nth dims k)
            alignment (tensor/factor-fourier-alignment u-k dim-k)
            ;; For each HOSVD column, find best-matching Fourier vector
            n-cols (nc/ncols u-k)]
        (println (str "\n  Mode " k " (" (axis-names k) ", dim=" dim-k "):"))
        (doseq [i (range (min 5 n-cols))]
          (let [best-j (apply max-key
                              #(nc/entry alignment i %)
                              (range dim-k))
                best-sim (nc/entry alignment i best-j)]
            (println (format "    U_%d col %d ↔ Fourier k=%d: |cos|=%.4f %s"
                             k i best-j best-sim
                             (if (> best-sim 0.95) "≈ HARMONIC"
                                 (if (> best-sim 0.8) "~ near-harmonic"
                                     "  NON-HARMONIC"))))))))))

(defn run-hosvd [T]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 2: HOSVD — Tucker Decomposition")
  (println "═══════════════════════════════════════════════════")
  (let [decomp (tensor/hosvd T)]
    (analyze-singular-values decomp)
    (analyze-core-sparsity decomp)
    (analyze-factor-fourier decomp)
    decomp))

;; ══════════════════════════════════════════════════════════════
;; Part 3: Full 4D DFT
;; ══════════════════════════════════════════════════════════════

(defn run-dft [T]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 3: Full 4D DFT — All 304,850 Modes")
  (println "═══════════════════════════════════════════════════")

  (println "\nComputing 4D DFT...")
  (let [t0 (System/currentTimeMillis)
        F-hat (tensor/dft-4d T)
        dt (- (System/currentTimeMillis) t0)
        _ (println (str "  Done in " dt "ms."))
        P (tensor/power-spectrum-4d F-hat)
        ^doubles pdata (:data P)
        total-power (let [n (alength pdata)]
                      (loop [i 0 s 0.0]
                        (if (= i n) s
                            (recur (inc i) (+ s (aget pdata i))))))]

    ;; Top 50 modes by power
    (println "\n── Top 50 Modes by Power ──")
    (let [top50 (tensor/top-modes P 50)
          dims (coords/dims)]
      (println (format "  %-4s  %-20s  %14s  %8s  %s"
                       "#" "[k₀,k₁,k₂,k₃]" "Power" "% Total" "Cross?"))
      (doseq [[i {:keys [coord power]}] (map-indexed vector top50)]
        (let [pct (* 100.0 (/ power total-power))
              cross? (> (count (filter pos? coord)) 1)]
          (println (format "  %-4d  %-20s  %14.2f  %7.3f%%  %s"
                           (inc i) (str coord) power pct
                           (if cross? "CROSS-MODE" "marginal")))))
      ;; Count cross-mode vs marginal in top 50
      (let [cross-count (count (filter (fn [{:keys [coord]}]
                                         (> (count (filter pos? coord)) 1))
                                       top50))]
        (println (str "\n  Cross-mode in top 50: " cross-count "/50"
                       " → " (if (< cross-count 10)
                               "mostly separable (axes independent)"
                               "significant coupling")))))

    ;; DC power
    (let [dc (aget pdata 0)]
      (println (str "\n  DC power (k=0,0,0,0): " (format "%.2f" dc)
                     " = " (format "%.4f%%" (* 100.0 (/ dc total-power))) " of total")))

    ;; Marginal recovery check — sum power over 3 axes, compare with 1D DFT from 087
    (println "\n── Marginal Recovery Check ──")
    (println "  Summing 4D power over 3 axes to recover 1D marginal spectra...")
    (let [dims (coords/dims)
          s (coords/space)]
      (doseq [ax (range 4)]
        (let [dim-k (nth dims ax)
              ;; Sum power over all indices except axis ax
              marginal-power (double-array dim-k)
              n (alength pdata)]
          (dotimes [flat-i n]
            (let [coord (tensor/coord-from-flat flat-i dims)
                  k-ax (aget ^longs coord ax)]
              (aset marginal-power k-ax
                    (+ (aget marginal-power k-ax) (aget pdata flat-i)))))
          ;; Compare with 1D DFT
          (let [axis-key ([:a :b :c :d] ax)
                marginal-1d (spectral/dft (spectral/marginal-mean s axis-key))
                power-1d (double-array dim-k)]
            ;; 1D power needs scaling: the 1D marginal is mean per hyperplane,
            ;; while the 4D DFT power summed over other axes involves full sums.
            ;; They should match up to a known scale factor.
            (dotimes [i dim-k]
              (aset power-1d i (:power (marginal-1d i))))
            (println (format "  Axis %d: 4D-marginal[0]=%.2f, 1D-DFT[0]=%.2f, ratio=%.4f"
                             ax (aget marginal-power 0) (aget power-1d 0)
                             (if (pos? (aget power-1d 0))
                               (/ (aget marginal-power 0) (aget power-1d 0))
                               ##NaN)))))))

    ;; Separability test
    (println "\n── Separability Test ──")
    (println "  Comparing 4D power to product of 1D marginal powers...")
    (let [dims (coords/dims)
          n (alength pdata)
          ;; Compute marginal power per axis
          marginals (mapv (fn [ax]
                            (let [dim-k (nth dims ax)
                                  mp (double-array dim-k)]
                              (dotimes [flat-i n]
                                (let [coord (tensor/coord-from-flat flat-i dims)
                                      k (aget ^longs coord ax)]
                                  (aset mp k (+ (aget mp k) (aget pdata flat-i)))))
                              ;; Normalize to probability-like
                              (let [total (reduce + (vec mp))]
                                (dotimes [i dim-k]
                                  (aset mp i (/ (aget mp i) total))))
                              mp))
                          (range 4))
          ;; Compute separable approximation: P_sep[k0,k1,k2,k3] = P0[k0]·P1[k1]·P2[k2]·P3[k3] × N
          ;; Then compute relative deviation
          deviations (atom [])
          n-above-2x (atom 0)
          n-above-5x (atom 0)]
      (dotimes [flat-i n]
        (let [coord (tensor/coord-from-flat flat-i dims)
              actual (aget pdata flat-i)
              expected (* (aget ^doubles (marginals 0) (aget ^longs coord 0))
                          (aget ^doubles (marginals 1) (aget ^longs coord 1))
                          (aget ^doubles (marginals 2) (aget ^longs coord 2))
                          (aget ^doubles (marginals 3) (aget ^longs coord 3))
                          total-power)]
          (when (pos? expected)
            (let [ratio (/ actual expected)]
              (swap! deviations conj ratio)
              (when (> ratio 2.0) (swap! n-above-2x inc))
              (when (> ratio 5.0) (swap! n-above-5x inc))))))
      (let [devs (sort @deviations)
            nd (count devs)]
        (println (format "  Deviation ratio (actual/separable): median=%.4f, min=%.4f, max=%.4f"
                         (nth devs (/ nd 2))
                         (first devs)
                         (last devs)))
        (println (format "  Modes >2× expected: %d (%.2f%%)" @n-above-2x
                         (* 100.0 (/ (double @n-above-2x) nd))))
        (println (format "  Modes >5× expected: %d (%.2f%%)" @n-above-5x
                         (* 100.0 (/ (double @n-above-5x) nd))))
        (println (str "  → " (cond
                               (< @n-above-2x (/ nd 20)) "Highly separable — axes are independent"
                               (< @n-above-2x (/ nd 5)) "Mildly coupled — some cross-axis structure"
                               :else "Strongly coupled — genuine 4D entanglement")))))

    ;; Round-trip verification
    (println "\n── 4D DFT Round-Trip Verification ──")
    (let [rec (tensor/idft-4d F-hat)
          ^doubles orig (:data T)
          ^doubles back (:data rec)
          n (alength orig)
          max-err (loop [i 0 mx 0.0]
                    (if (= i n)
                      mx
                      (recur (inc i) (max mx (Math/abs (- (aget orig i) (aget back i)))))))]
      (println (str "  Max error: " (format "%.2e" max-err)
                     (if (< max-err 1e-6) " ✓ (machine precision)" " ✗ ERROR"))))

    {:F-hat F-hat :P P :total-power total-power}))

;; ══════════════════════════════════════════════════════════════
;; Part 4: HOSVD vs DFT Comparison
;; ══════════════════════════════════════════════════════════════

(defn compare-hosvd-dft
  "Compare HOSVD and DFT decompositions."
  [decomp _dft-result]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 4: HOSVD vs DFT — Two Lenses on One Object")
  (println "═══════════════════════════════════════════════════")

  ;; Already computed in analyze-factor-fourier, but let's give a summary
  (let [factors (:factors decomp)
        dims (coords/dims)
        axis-names ["a (completeness)" "b (jubilee)" "c (love)" "d (understanding)"]]
    (println "\n  Summary: how harmonic is each axis?")
    (doseq [k (range 4)]
      (let [u-k (factors k)
            dim-k (nth dims k)
            alignment (tensor/factor-fourier-alignment u-k dim-k)
            ;; Average best alignment per HOSVD column
            n-cols (nc/ncols u-k)
            avg-best (/ (reduce + (map (fn [i]
                                         (apply max (map #(nc/entry alignment i %)
                                                         (range dim-k))))
                                       (range n-cols)))
                        (double n-cols))]
        (println (format "  Mode %d %-25s: avg best |cos|=%.4f  %s"
                         k (axis-names k) avg-best
                         (cond
                           (> avg-best 0.95) "→ purely harmonic"
                           (> avg-best 0.80) "→ mostly harmonic"
                           (> avg-best 0.60) "→ partially harmonic"
                           :else "→ non-harmonic (localized features)")))))))

;; ══════════════════════════════════════════════════════════════
;; Part 5: Second-Order Oracle — M·A
;; ══════════════════════════════════════════════════════════════

(defn build-restricted-features
  "Build feature vectors for a specific word list (the shared vocabulary)."
  [words]
  (println (str "  Building features for " (count words) " shared words..."))
  (let [progress (atom 0)
        n (count words)]
    (->> words
         (pmap (fn [w]
                 (let [p (swap! progress inc)]
                   (when (zero? (mod p 100))
                     (println (str "    " p "/" n "..."))))
                 (let [a (selah.oracle/ask w)]
                   (when (:readable? a)
                     (exp089/word-features w a)))))
         (filterv some?))))

(defn build-restricted-affinity
  "Build affinity matrix for a feature set."
  [features]
  (exp089/build-affinity-matrix features {}))

(defn align-matrices
  "Align M and A to a shared vocabulary. Both must use the same word ordering.
   Returns {:M dge, :A dge, :words vec, :n int}."
  [tm features]
  (println "\n  Aligning M and A to shared vocabulary...")
  (let [m-words (set (:words tm))
        a-words (set (map :word features))
        shared (sort (clojure.set/intersection m-words a-words))
        n (count shared)
        _ (println (str "    Shared vocabulary: " n " words"))
        word-idx (into {} (map-indexed (fn [i w] [w i]) shared))
        ;; Extract M restricted to shared words
        M (nn/dge n n)
        _ (doseq [i (range n)]
            (let [wi (shared i)
                  mi (get (:word-idx tm) wi)]
              (when mi
                (doseq [j (range n)]
                  (let [wj (shared j)
                        mj (get (:word-idx tm) wj)]
                    (when mj
                      (nc/entry! M i j (la/entry (:matrix tm) mi mj))))))))
        ;; Build feature index for A
        feat-idx (into {} (map (fn [f] [(:word f) f]) features))
        feat-ordered (mapv #(get feat-idx %) shared)
        ;; Only keep words that have features
        valid (filterv some? feat-ordered)]
    (if (< (count valid) n)
      (do (println (str "    Warning: " (- n (count valid)) " words missing features, using " (count valid)))
          ;; Rebuild with only valid words
          (let [valid-words (mapv :word valid)
                n2 (count valid-words)
                word-idx2 (into {} (map-indexed (fn [i w] [w i]) valid-words))
                M2 (nn/dge n2 n2)]
            (doseq [i (range n2)]
              (let [wi (valid-words i)
                    mi (get (:word-idx tm) wi)]
                (when mi
                  (doseq [j (range n2)]
                    (let [wj (valid-words j)
                          mj (get (:word-idx tm) wj)]
                      (when mj
                        (nc/entry! M2 i j (la/entry (:matrix tm) mi mj))))))))
            (la/row-normalize! M2)
            (let [A2 (build-restricted-affinity valid)]
              {:M M2 :A A2 :words valid-words :n n2 :features valid})))
      (do (la/row-normalize! M)
          (let [A (build-restricted-affinity (vec feat-ordered))]
            {:M M :A A :words (vec shared) :n n :features (vec feat-ordered)})))))

(defn run-second-order
  "Part 5: M·A — where oracle reading meets semantic structure."
  []
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 5: Second-Order Oracle — M·A")
  (println "═══════════════════════════════════════════════════")

  ;; Build M from 088
  (println "\n  Step 1: Building M (stochastic oracle)...")
  (let [vocab (exp088/discover-vocabulary)
        tm (exp088/build-transition-matrix vocab)]

    ;; Build features for shared vocabulary
    (println "\n  Step 2: Building features for shared vocabulary...")
    (let [features (build-restricted-features (:words tm))
          {:keys [M A words n] :as aligned} (align-matrices tm features)]

      (println (str "\n  Working with " n " words."))

      ;; Products
      (println "\n── Computing M·A, A·M, commutator ──")
      (let [MA (nc/mm M A)
            AM (nc/mm A M)
            ;; Commutator [M,A] = MA - AM
            comm (let [c (nc/copy MA)]
                   (nc/axpy! -1.0 AM c)
                   c)
            ;; Frobenius norms
            frob-M (Math/sqrt (nc/dot (nc/view-vctr M) (nc/view-vctr M)))
            frob-A (Math/sqrt (nc/dot (nc/view-vctr A) (nc/view-vctr A)))
            frob-MA (Math/sqrt (nc/dot (nc/view-vctr MA) (nc/view-vctr MA)))
            frob-AM (Math/sqrt (nc/dot (nc/view-vctr AM) (nc/view-vctr AM)))
            frob-comm (Math/sqrt (nc/dot (nc/view-vctr comm) (nc/view-vctr comm)))
            ;; Normalized commutator
            comm-norm (/ frob-comm (* frob-M frob-A))]

        (println (format "  ||M||_F = %.4f" frob-M))
        (println (format "  ||A||_F = %.4f" frob-A))
        (println (format "  ||M·A||_F = %.4f" frob-MA))
        (println (format "  ||A·M||_F = %.4f" frob-AM))
        (println (format "  ||[M,A]||_F = %.4f" frob-comm))
        (println (format "  Normalized commutator: %.6f" comm-norm))
        (println (str "  → " (cond
                               (< comm-norm 0.01) "M and A nearly commute! Reading ≈ meaning."
                               (< comm-norm 0.1) "Mild non-commutativity. Reading and meaning partially agree."
                               (< comm-norm 0.5) "Moderate disagreement between reading and meaning."
                               :else "Strong disagreement. Oracle and semantics see different structure.")))

        ;; Row-normalize M·A → stochastic
        (println "\n── Eigendecomposition of M·A ──")
        (let [MA-stoch (la/row-normalize! (nc/copy MA))
              ma-eigen (la/eigendecomp MA-stoch)
              ;; How many eigenvalues are real (imag < 1e-6)?
              n-real (count (filter #(< (Math/abs %) 1e-6) (:imag ma-eigen)))
              ;; Top eigenvalues by magnitude
              evals (:values ma-eigen)
              eimag (:imag ma-eigen)
              sorted-idx (vec (sort-by #(- (Math/abs (evals %))) (range n)))]
          (println (str "  Eigenvalues: " n " total, " n-real " real (imag < 1e-6)"))
          (println "\n  Top 20 eigenvalues of row-normalized M·A:")
          (println (format "  %-4s %10s %10s %10s  %s" "#" "Real" "Imag" "|λ|" "Status"))
          (doseq [rank (range (min 20 n))]
            (let [i (sorted-idx rank)
                  re (evals i)
                  im (eimag i)
                  mag (Math/sqrt (+ (* re re) (* im im)))]
              (println (format "  %-4d %10.6f %10.6f %10.6f  %s"
                               (inc rank) re im mag
                               (cond
                                 (and (> re 0.99) (< (Math/abs im) 1e-6)) "← stationary"
                                 (< (Math/abs im) 1e-6) ""
                                 :else "(complex)")))))

          ;; Stationary distribution of M·A
          (println "\n── Stationary Distribution of M·A ──")
          (let [stat (la/stochastic-eigendecomp MA-stoch)
                pi (:stationary stat)
                top-words (->> (map-indexed (fn [i w] {:word w :weight (pi i)
                                                       :meaning (dict/translate w)
                                                       :gv (g/word-value w)})
                                            words)
                               (sort-by :weight >)
                               (take 30))]
            (println "\n  Top 30 words by M·A stationary weight:")
            (println (format "  %-4s %-8s %10s %6s  %s" "#" "Word" "Weight" "GV" "Meaning"))
            (doseq [[i {:keys [word weight gv meaning]}] (map-indexed vector top-words)]
              (println (format "  %-4d %-8s %10.6f %6d  %s"
                               (inc i) word weight gv (or meaning "")))))

          ;; Commutator eigenvectors — words where reading and meaning agree/disagree
          (println "\n── Commutator Spectrum ──")
          (let [comm-eigen (la/eigendecomp comm)
                c-evals (:values comm-eigen)
                c-eimag (:imag comm-eigen)
                c-sorted (vec (sort-by #(Math/abs (c-evals %)) (range n)))]
            ;; Near-zero eigenvalues = agreement (M and A act the same)
            (println "\n  Words where reading and meaning AGREE (near-zero commutator eigenvalues):")
            (doseq [rank (range (min 15 n))]
              (let [i (c-sorted rank)
                    evec (la/col-vec (:vectors comm-eigen) i)
                    ;; Find the word that dominates this eigenvector
                    dominant-idx (apply max-key #(Math/abs (evec %)) (range n))
                    w (words dominant-idx)]
                (println (format "    λ=%.6f  dominant: %-8s (%s)"
                                 (c-evals i) w (or (dict/translate w) "?")))))

            ;; Large eigenvalues = disagreement
            (let [c-sorted-desc (vec (sort-by #(- (Math/abs (c-evals %))) (range n)))]
              (println "\n  Words where reading and meaning DISAGREE (large commutator eigenvalues):")
              (doseq [rank (range (min 15 n))]
                (let [i (c-sorted-desc rank)
                      evec (la/col-vec (:vectors comm-eigen) i)
                      dominant-idx (apply max-key #(Math/abs (evec %)) (range n))
                      w (words dominant-idx)]
                  (println (format "    |λ|=%.4f  dominant: %-8s (%s)"
                                   (Math/abs (c-evals i)) w (or (dict/translate w) "?"))))))))

        {:M M :A A :MA MA :AM AM :comm comm :words words :n n}))))

;; ══════════════════════════════════════════════════════════════
;; Part 6: Synthesis
;; ══════════════════════════════════════════════════════════════

(defn synthesize
  "What do the three decompositions tell us together?"
  [decomp dft-result ma-result]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 6: Synthesis")
  (println "═══════════════════════════════════════════════════")

  ;; 1. HOSVD effective rank vs axis meaning
  (println "\n── Axis Nature (from HOSVD) ──")
  (let [svs (:singular-values decomp)
        axis-names ["a (completeness)" "b (jubilee)" "c (love)" "d (understanding)"]]
    (doseq [k (range 4)]
      (let [sv (svs k)
            total-sq (reduce + (map #(* % %) sv))
            top1-pct (* 100.0 (/ (* (first sv) (first sv)) total-sq))
            ;; Effective rank = number for 95% energy
            cumulative (reductions + (map #(* % %) sv))
            eff-rank (inc (count (take-while #(< (/ % total-sq) 0.95) cumulative)))]
        (println (format "  Mode %d %-25s: eff-rank=%d, σ₀²=%.1f%%"
                         k (axis-names k) eff-rank top1-pct)))))

  ;; 2. Cross-mode coupling from 4D DFT
  (println "\n── Cross-Mode Coupling (from 4D DFT) ──")
  (let [^doubles pdata (:data (:P dft-result))
        n (alength pdata)
        total (:total-power dft-result)
        dims (coords/dims)
        ;; Count power in cross-modes (2+ nonzero indices)
        cross-power (loop [i 0 cp 0.0]
                      (if (= i n)
                        cp
                        (let [coord (tensor/coord-from-flat i dims)
                              nonzero (loop [k 0 cnt 0]
                                        (if (= k 4) cnt
                                            (recur (inc k)
                                                   (if (pos? (aget ^longs coord k))
                                                     (inc cnt) cnt))))]
                          (recur (inc i)
                                 (if (> nonzero 1)
                                   (+ cp (aget pdata i))
                                   cp)))))]
    (println (format "  Cross-mode power: %.2f / %.2f = %.4f%%"
                     cross-power total (* 100.0 (/ cross-power total))))
    (println (str "  → " (if (< (/ cross-power total) 0.01)
                           "The axes are nearly independent — the 4D structure is a product of 1D structures."
                           "Genuine 4D coupling exists — the axes interact."))))

  ;; 3. Oracle agreement from M·A
  (when ma-result
    (println "\n── Oracle Agreement (from M·A) ──")
    (let [{:keys [comm M A n]} ma-result
          frob-comm (Math/sqrt (nc/dot (nc/view-vctr comm) (nc/view-vctr comm)))
          frob-M (Math/sqrt (nc/dot (nc/view-vctr M) (nc/view-vctr M)))
          frob-A (Math/sqrt (nc/dot (nc/view-vctr A) (nc/view-vctr A)))
          ratio (/ frob-comm (* frob-M frob-A))]
      (println (format "  Commutator ratio: %.6f" ratio))
      (println (str "  " n " words in the shared vocabulary."))))

  (println "\n═══════════════════════════════════════════════════")
  (println "  DONE.")
  (println "═══════════════════════════════════════════════════"))

;; ══════════════════════════════════════════════════════════════
;; Part 7: Permutation Test — Is This Special?
;; ══════════════════════════════════════════════════════════════

(defn shuffle-tensor
  "Randomly permute a tensor's data (preserving letter frequencies).
   Returns a new tensor with shuffled values."
  [{:keys [^doubles data shape]}]
  (let [n (alength data)
        shuffled (java.util.Arrays/copyOf data n)
        rng (java.util.Random. 42)]  ;; fixed seed for reproducibility
    ;; Fisher-Yates shuffle
    (loop [i (dec n)]
      (when (> i 0)
        (let [j (.nextInt rng (inc i))
              tmp (aget shuffled i)]
          (aset shuffled i (aget shuffled j))
          (aset shuffled j tmp)
          (recur (dec i)))))
    {:data shuffled :shape shape}))

(defn quick-hosvd-stats
  "Extract key HOSVD metrics without printing."
  [{:keys [singular-values core]}]
  (let [axis-names ["a" "b" "c" "d"]]
    {:sv-spectra
     (mapv (fn [k]
             (let [sv (singular-values k)
                   total-sq (reduce + (map #(* % %) sv))
                   cumulative (reductions + (map #(* % %) sv))
                   eff-rank (inc (count (take-while #(< (/ % total-sq) 0.95) cumulative)))
                   top1-pct (* 100.0 (/ (* (first sv) (first sv)) total-sq))]
               {:axis (axis-names k)
                :eff-rank eff-rank
                :dim (count sv)
                :top1-pct top1-pct
                :sigma-0 (first sv)
                :sigma-last (last sv)}))
           (range 4))
     :core-sparsity
     (let [^doubles cdata (:data core)
           cn (alength cdata)
           frob-sq (loop [i 0 s 0.0]
                     (if (= i cn) s
                         (recur (inc i) (+ s (let [v (aget cdata i)] (* v v))))))
           sorted (sort > (map #(Math/abs %) (vec cdata)))
           cumulative (reductions + (map #(* % %) sorted))
           n90 (inc (count (take-while #(< (/ % frob-sq) 0.90) cumulative)))
           n99 (inc (count (take-while #(< (/ % frob-sq) 0.99) cumulative)))]
       {:n90-pct (* 100.0 (/ (double n90) cn))
        :n99-pct (* 100.0 (/ (double n99) cn))})}))

(defn quick-dft-stats
  "Extract key DFT metrics without printing."
  [P dims]
  (let [^doubles pdata (:data P)
        n (alength pdata)
        total-power (loop [i 0 s 0.0]
                      (if (= i n) s
                          (recur (inc i) (+ s (aget pdata i)))))
        dc-power (aget pdata 0)
        cross-power (loop [i 0 cp 0.0]
                      (if (= i n) cp
                          (let [coord (tensor/coord-from-flat i dims)
                                nonzero (loop [k 0 cnt 0]
                                          (if (= k 4) cnt
                                              (recur (inc k)
                                                     (if (pos? (aget ^longs coord k))
                                                       (inc cnt) cnt))))]
                            (recur (inc i)
                                   (if (> nonzero 1)
                                     (+ cp (aget pdata i))
                                     cp)))))]
    {:total-power total-power
     :dc-pct (* 100.0 (/ dc-power total-power))
     :cross-pct (* 100.0 (/ cross-power total-power))}))

(defn run-permutation-test
  "The critical control: does a shuffled Torah show the same structure?"
  [T]
  (println "\n═══════════════════════════════════════════════════")
  (println "  PART 7: Permutation Test — Is This Special?")
  (println "═══════════════════════════════════════════════════")

  ;; Torah stats (already computed, but extract for comparison)
  (println "\n── Torah (original) ──")
  (let [t0 (System/currentTimeMillis)
        torah-decomp (tensor/hosvd T)
        torah-hosvd (quick-hosvd-stats torah-decomp)
        _ (println (str "  HOSVD: " (- (System/currentTimeMillis) t0) "ms"))

        t1 (System/currentTimeMillis)
        torah-fhat (tensor/dft-4d T)
        torah-P (tensor/power-spectrum-4d torah-fhat)
        torah-dft (quick-dft-stats torah-P (coords/dims))
        _ (println (str "  DFT: " (- (System/currentTimeMillis) t1) "ms"))

        ;; Shuffled version
        _ (println "\n── Shuffled Torah (same letters, random order) ──")
        T-shuf (shuffle-tensor T)

        t2 (System/currentTimeMillis)
        shuf-decomp (tensor/hosvd T-shuf)
        shuf-hosvd (quick-hosvd-stats shuf-decomp)
        _ (println (str "  HOSVD: " (- (System/currentTimeMillis) t2) "ms"))

        t3 (System/currentTimeMillis)
        shuf-fhat (tensor/dft-4d T-shuf)
        shuf-P (tensor/power-spectrum-4d shuf-fhat)
        shuf-dft (quick-dft-stats shuf-P (coords/dims))
        _ (println (str "  DFT: " (- (System/currentTimeMillis) t3) "ms"))]

    ;; Comparison
    (println "\n═══════════════════════════════════════════════════")
    (println "  COMPARISON: Torah vs Shuffled")
    (println "═══════════════════════════════════════════════════")

    ;; HOSVD
    (println "\n── Effective Rank (95% energy threshold) ──")
    (println (format "  %-25s  %6s  %6s  %s" "Axis" "Torah" "Shuf" "Verdict"))
    (doseq [k (range 4)]
      (let [t-sv (nth (:sv-spectra torah-hosvd) k)
            s-sv (nth (:sv-spectra shuf-hosvd) k)]
        (println (format "  %-25s  %4d/%d  %4d/%d  %s"
                         (str (:axis t-sv) " (dim=" (:dim t-sv) ")")
                         (:eff-rank t-sv) (:dim t-sv)
                         (:eff-rank s-sv) (:dim s-sv)
                         (cond
                           (< (:eff-rank t-sv) (:eff-rank s-sv)) "Torah MORE structured"
                           (> (:eff-rank t-sv) (:eff-rank s-sv)) "Torah LESS structured"
                           :else "Same")))))

    (println "\n── σ₀² share (dominant mode) ──")
    (println (format "  %-25s  %8s  %8s  %s" "Axis" "Torah" "Shuf" "Verdict"))
    (doseq [k (range 4)]
      (let [t-sv (nth (:sv-spectra torah-hosvd) k)
            s-sv (nth (:sv-spectra shuf-hosvd) k)]
        (println (format "  %-25s  %7.2f%%  %7.2f%%  %s"
                         (:axis t-sv)
                         (:top1-pct t-sv) (:top1-pct s-sv)
                         (let [diff (- (:top1-pct t-sv) (:top1-pct s-sv))]
                           (cond
                             (> diff 2) (format "+%.1f%% Torah has MORE dominant mode" diff)
                             (< diff -2) (format "%.1f%% Torah has LESS dominant mode" diff)
                             :else "~Same"))))))

    ;; Core sparsity
    (println "\n── Core Tensor Sparsity ──")
    (let [tc (:core-sparsity torah-hosvd)
          sc (:core-sparsity shuf-hosvd)]
      (println (format "  Entries for 90%% energy:  Torah=%.1f%%  Shuf=%.1f%%" (:n90-pct tc) (:n90-pct sc)))
      (println (format "  Entries for 99%% energy:  Torah=%.1f%%  Shuf=%.1f%%" (:n99-pct tc) (:n99-pct sc))))

    ;; DFT
    (println "\n── 4D DFT Power Distribution ──")
    (println (format "  DC power:         Torah=%6.2f%%   Shuf=%6.2f%%" (:dc-pct torah-dft) (:dc-pct shuf-dft)))
    (println (format "  Cross-mode power: Torah=%6.2f%%   Shuf=%6.2f%%" (:cross-pct torah-dft) (:cross-pct shuf-dft)))

    (let [torah-cross (:cross-pct torah-dft)
          shuf-cross (:cross-pct shuf-dft)
          diff (- torah-cross shuf-cross)]
      (println (str "\n  Δ cross-mode: " (format "%+.2f%%" diff)))
      (println (str "  → " (cond
                             (> (Math/abs diff) 5)
                             (if (pos? diff)
                               "Torah has SIGNIFICANTLY MORE cross-mode coupling than chance."
                               "Torah has SIGNIFICANTLY LESS cross-mode coupling than chance.")
                             (> (Math/abs diff) 1)
                             (if (pos? diff)
                               "Torah has moderately more cross-mode coupling."
                               "Torah has moderately less cross-mode coupling.")
                             :else
                             "No significant difference. The coupling is inherent in letter statistics."))))

    ;; Factor-Fourier alignment comparison
    (println "\n── Factor-Fourier Alignment ──")
    (let [dims (coords/dims)]
      (println (format "  %-25s  %8s  %8s" "Axis" "Torah" "Shuf"))
      (doseq [k (range 4)]
        (let [t-alignment (tensor/factor-fourier-alignment ((:factors torah-decomp) k) (nth dims k))
              s-alignment (tensor/factor-fourier-alignment ((:factors shuf-decomp) k) (nth dims k))
              t-avg (/ (reduce + (map (fn [i]
                                        (apply max (map #(nc/entry t-alignment i %)
                                                        (range (nth dims k)))))
                                      (range (nc/ncols ((:factors torah-decomp) k)))))
                       (double (nc/ncols ((:factors torah-decomp) k))))
              s-avg (/ (reduce + (map (fn [i]
                                        (apply max (map #(nc/entry s-alignment i %)
                                                        (range (nth dims k)))))
                                      (range (nc/ncols ((:factors shuf-decomp) k)))))
                       (double (nc/ncols ((:factors shuf-decomp) k))))]
          (println (format "  %-25s  %8.4f  %8.4f  %s"
                           (str (["a" "b" "c" "d"] k) " (dim=" (nth dims k) ")")
                           t-avg s-avg
                           (if (> (- t-avg s-avg) 0.05)
                             "Torah MORE harmonic"
                             (if (< (- t-avg s-avg) -0.05)
                               "Torah LESS harmonic"
                               "~Same")))))))

    (println "\n═══════════════════════════════════════════════════")
    (println "  Permutation test complete.")
    (println "═══════════════════════════════════════════════════")

    {:torah {:hosvd torah-hosvd :dft torah-dft}
     :shuffled {:hosvd shuf-hosvd :dft shuf-dft}}))

;; ══════════════════════════════════════════════════════════════
;; Run
;; ══════════════════════════════════════════════════════════════

(defn run
  "Run the full experiment. Parts 1-4 are fast (tensor ops).
   Part 5 (M·A) is slow (oracle vocabulary discovery)."
  ([]
   (run {:skip-ma false}))
  ([{:keys [skip-ma] :or {skip-ma false}}]
   (println "\n═══════════════════════════════════════════════════")
   (println "  EXPERIMENT 090 — HIGHER-ORDER TORAH SPACE")
   (println "═══════════════════════════════════════════════════")

   ;; Part 1: Build tensor
   (let [T (build-tensor)

         ;; Part 2: HOSVD
         decomp (run-hosvd T)

         ;; Part 3: 4D DFT
         dft-result (run-dft T)

         ;; Part 4: HOSVD vs DFT
         _ (compare-hosvd-dft decomp dft-result)

         ;; Part 5: M·A (optional — slow)
         ma-result (when-not skip-ma
                     (run-second-order))]

     ;; Part 6: Synthesis
     (synthesize decomp dft-result ma-result)

     {:tensor T :hosvd decomp :dft dft-result :ma ma-result})))

(comment
  ;; Quick: just tensor decompositions (no oracle, ~10-30s)
  (def results (run {:skip-ma true}))

  ;; Full: everything including M·A (~5-10 min, oracle is slow)
  (def results (run))

  ;; Piece by piece:
  (def T (build-tensor))
  (def decomp (run-hosvd T))
  (def dft-result (run-dft T))
  (compare-hosvd-dft decomp dft-result)
  (def ma-result (run-second-order))
  (synthesize decomp dft-result ma-result)

  ;; Mode unfold round-trip test
  (let [T (build-tensor)]
    (every? (fn [k]
              (let [unf (tensor/mode-unfold T k)
                    ref (tensor/mode-refold unf k (:shape T))]
                (java.util.Arrays/equals ^doubles (:data T) ^doubles (:data ref))))
            (range 4)))

  ;; 4D DFT round-trip
  (let [T (build-tensor)
        ct (tensor/dft-4d T)
        rec (tensor/idft-4d ct)
        ^doubles orig (:data T)
        ^doubles back (:data rec)
        n (alength orig)
        max-err (loop [i 0 mx 0.0]
                  (if (= i n)
                    mx
                    (recur (inc i) (max mx (Math/abs (- (aget orig i) (aget back i)))))))]
    (println (str "Max DFT round-trip error: " max-err)))

  :end)
