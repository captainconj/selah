(ns experiments.032-the-unfold
  "Map the Torah into higher dimensions.
   The 1D letter stream is one projection.
   What happens when we wrap it on a torus?
   On a helix of pitch 7? Of pitch 37?
   Run: clojure -M:dev -m experiments.032-the-unfold"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn -main []
  (println "=== THE UNFOLD ===")
  (println "  Higher-dimensional projections of the letter stream.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. The helix: wrap on pitch p ────────────────────────
    (println "── 1. Helical Wrapping ──")
    (println "  Wrap the Torah on a helix of pitch p.")
    (println "  Each 'row' is p consecutive letters.")
    (println "  Compare row sums for palindromic structure.\n")

    (doseq [pitch [5 7 11 12 22 26 37 42 49 73 343 441]]
      (let [n-rows (quot n pitch)
            row-sums (mapv (fn [r]
                             (let [start (* r pitch)
                                   end (min n (* (inc r) pitch))]
                               (reduce + (subvec gem-vals start end))))
                           (range n-rows))
            half (quot n-rows 2)
            first-half (mapv double (subvec row-sums 0 half))
            second-half-rev (mapv double (vec (reverse (subvec row-sums (- n-rows half)))))
            cos (cosine-sim first-half second-half-rev)
            ;; Column sums
            col-sums (mapv (fn [c]
                             (reduce + (for [r (range n-rows)]
                                         (let [idx (+ (* r pitch) c)]
                                           (if (< idx n) (long (nth gem-vals idx)) 0)))))
                           (range pitch))
            ;; Column palindrome (only for pitches with even number of columns)
            col-half (quot pitch 2)
            col-first (mapv double (subvec col-sums 0 col-half))
            col-last (mapv double (vec (reverse (subvec col-sums (- pitch col-half)))))
            col-cos (cosine-sim col-first col-last)]
        (println (format "  pitch=%3d  rows=%,6d  row-palindrome=%.6f  col-palindrome=%.6f%s"
                         pitch n-rows cos col-cos
                         (cond
                           (= pitch 7) "  ← seven"
                           (= pitch 37) "  ← 37"
                           (= pitch 49) "  ← 7²"
                           (= pitch 73) "  ← 73"
                           (= pitch 22) "  ← alphabet"
                           (= pitch 26) "  ← YHVH"
                           (= pitch 441) "  ← 21²"
                           (= pitch 343) "  ← 7³"
                           :else "")))))

    ;; ── 2. The torus: two pitches simultaneously ─────────────
    (println "\n── 2. Toroidal Wrapping ──")
    (println "  Wrap on a torus with two periods (p, q).")
    (println "  The text lives on a 2D surface.\n")

    (doseq [[p q] [[5 7] [7 11] [7 37] [7 73] [22 7] [37 73]]]
      (let [;; Build p×q cells, each containing n/(p*q) letters
            cell-size (quot n (* p q))
            ;; Build the p×q grid
            grid (mapv (fn [r]
                         (mapv (fn [c]
                                 (let [cell-idx (+ (* r q) c)
                                       start (* cell-idx cell-size)
                                       end (min n (* (inc cell-idx) cell-size))]
                                   (if (< start n)
                                     (reduce + (subvec gem-vals start (min end n)))
                                     0)))
                               (range q)))
                       (range p))
            ;; Row sums
            row-sums (mapv #(reduce + %) grid)
            ;; Col sums
            col-sums (mapv (fn [c] (reduce + (map #(nth % c) grid))) (range q))
            ;; Row palindrome
            r-half (quot p 2)
            row-pal (if (> r-half 0)
                      (cosine-sim (mapv double (subvec row-sums 0 r-half))
                                  (mapv double (vec (reverse (subvec row-sums (- p r-half))))))
                      0.0)
            ;; Col palindrome
            c-half (quot q 2)
            col-pal (if (> c-half 0)
                      (cosine-sim (mapv double (subvec col-sums 0 c-half))
                                  (mapv double (vec (reverse (subvec col-sums (- q c-half))))))
                      0.0)]
        (println (format "  Torus(%d, %d)  cell=%,d  row-pal=%.4f  col-pal=%.4f"
                         p q cell-size row-pal col-pal))))

    ;; ── 3. Diagonal structure ────────────────────────────────
    (println "\n── 3. Diagonal Reads ──")
    (println "  On a grid of width w, read the diagonals.\n")

    (doseq [w [7 37 73]]
      (let [n-rows (quot n w)
            ;; Main diagonal: position (r, r mod w) → index r*w + (r mod w)
            diag-indices (keep (fn [r]
                                 (let [c (mod r w)
                                       idx (+ (* r w) c)]
                                   (when (< idx n) idx)))
                               (range n-rows))
            diag-gems (mapv #(long (nth gem-vals %)) diag-indices)
            diag-sum (reduce + diag-gems)
            ;; Anti-diagonal: position (r, (w-1-r) mod w)
            anti-indices (keep (fn [r]
                                  (let [c (mod (- w 1 r) w)
                                        idx (+ (* r w) c)]
                                    (when (< idx n) idx)))
                                (range n-rows))
            anti-gems (mapv #(long (nth gem-vals %)) anti-indices)
            anti-sum (reduce + anti-gems)]
        (println (format "  Width %d:" w))
        (println (format "    Main diagonal:  Σ = %,d  mod 7 = %d  mod 37 = %d"
                         diag-sum (mod diag-sum 7) (mod diag-sum 37)))
        (println (format "    Anti-diagonal:  Σ = %,d  mod 7 = %d  mod 37 = %d"
                         anti-sum (mod anti-sum 7) (mod anti-sum 37)))
        (println (format "    |Main - Anti|:  %,d (%.4f%%)"
                         (Math/abs (- diag-sum anti-sum))
                         (* 100 (/ (double (Math/abs (- diag-sum anti-sum)))
                                   (/ (+ (double diag-sum) anti-sum) 2.0)))))))

    ;; ── 4. Spiral wrapping ───────────────────────────────────
    (println "\n── 4. Spiral Wrapping ──")
    (println "  Map the Torah onto a spiral. Compare rings.\n")

    ;; Use a simple spiral: ring k has 6k elements (hex spiral)
    ;; Ring 0: 1 element. Ring 1: 6 elements. Ring 2: 12 elements...
    ;; Total in k rings: 1 + 6(1 + 2 + ... + k) = 1 + 3k(k+1)
    (let [ring-sizes (fn [max-k]
                       (mapv (fn [k] (if (zero? k) 1 (* 6 k))) (range (inc max-k))))
          ;; Find how many rings fit
          max-rings (loop [k 0 total 0]
                      (let [ring-n (if (zero? k) 1 (* 6 k))
                            new-total (+ total ring-n)]
                        (if (> new-total n) k (recur (inc k) new-total))))
          rings (ring-sizes (dec max-rings))
          _ (println (format "  Hex spiral: %d rings, %,d letters used of %,d"
                             (count rings) (reduce + rings) n))]

      ;; Gematria sum per ring
      (println "\n  Ring gematria sums (first 20 and last 5):")
      (let [ring-sums (loop [k 0 pos 0 sums []]
                         (if (>= k (count rings))
                           sums
                           (let [sz (nth rings k)
                                 end (min n (+ pos sz))
                                 s (reduce + (subvec gem-vals pos end))]
                             (recur (inc k) end (conj sums s)))))]

        (doseq [i (range (min 20 (count ring-sums)))]
          (println (format "    Ring %3d  size=%,5d  Σ=%,8d  mean=%.1f"
                           i (nth rings i) (nth ring-sums i)
                           (/ (double (nth ring-sums i)) (nth rings i)))))
        (when (> (count ring-sums) 25)
          (println "    ...")
          (doseq [i (range (- (count ring-sums) 5) (count ring-sums))]
            (println (format "    Ring %3d  size=%,5d  Σ=%,8d  mean=%.1f"
                             i (nth rings i) (nth ring-sums i)
                             (/ (double (nth ring-sums i)) (nth rings i))))))

        ;; Palindrome of rings
        (let [half (quot (count ring-sums) 2)
              first-half (mapv double (subvec ring-sums 0 half))
              second-half-rev (mapv double (vec (reverse (subvec ring-sums (- (count ring-sums) half)))))]
          (println (format "\n  Ring palindrome (%d rings): cos = %.6f"
                           (count ring-sums) (cosine-sim first-half second-half-rev))))))

    ;; ── 5. Phase space reconstruction ────────────────────────
    (println "\n── 5. Phase Space ──")
    (println "  Embed the gematria stream in delay coordinates.")
    (println "  (gem[i], gem[i+d]) — is the attractor structured?\n")

    (doseq [d [1 7 37 73]]
      (let [pairs (mapv (fn [i] [(long (nth gem-vals i)) (long (nth gem-vals (+ i d)))])
                        (range (- n d)))
            ;; Correlation
            xs (mapv first pairs)
            ys (mapv second pairs)
            mean-x (/ (double (reduce + xs)) (count xs))
            mean-y (/ (double (reduce + ys)) (count ys))
            cov (/ (reduce + (map (fn [x y] (* (- x mean-x) (- y mean-y))) xs ys)) (count xs))
            var-x (/ (reduce + (map (fn [x] (* (- x mean-x) (- x mean-x))) xs)) (count xs))
            var-y (/ (reduce + (map (fn [y] (* (- y mean-y) (- y mean-y))) ys)) (count ys))
            corr (if (and (pos? var-x) (pos? var-y))
                   (/ cov (Math/sqrt (* var-x var-y)))
                   0.0)
            ;; Quadrant analysis
            q1 (count (filter (fn [[x y]] (and (> x mean-x) (> y mean-y))) pairs))
            q2 (count (filter (fn [[x y]] (and (< x mean-x) (> y mean-y))) pairs))
            q3 (count (filter (fn [[x y]] (and (< x mean-x) (< y mean-y))) pairs))
            q4 (count (filter (fn [[x y]] (and (> x mean-x) (< y mean-y))) pairs))
            total (count pairs)]
        (println (format "  d=%3d  corr=%.6f  Q1=%5.2f%%  Q2=%5.2f%%  Q3=%5.2f%%  Q4=%5.2f%%%s"
                         d corr
                         (* 100 (/ (double q1) total))
                         (* 100 (/ (double q2) total))
                         (* 100 (/ (double q3) total))
                         (* 100 (/ (double q4) total))
                         (cond
                           (= d 7) "  ← seven"
                           (= d 37) "  ← 37"
                           (= d 73) "  ← 73"
                           :else "")))))

    ;; ── 6. Multi-dimensional symmetry ────────────────────────
    (println "\n── 6. Multi-Scale Symmetry Test ──")
    (println "  For each dimension d, build a d-dimensional cube.")
    (println "  Test: is the cube symmetric under reflection?\n")

    (doseq [d [3 5 7]]
      (let [side-len (int (Math/pow n (/ 1.0 d)))
            used (* (long (Math/pow side-len d)))
            ;; Build the d-dimensional sums along each axis
            axis-sums (mapv (fn [axis]
                              ;; Sum along axis: group by (index / stride) mod side-len
                              (let [stride (long (Math/pow side-len axis))]
                                (mapv (fn [k]
                                        ;; Sum all elements where (index / stride) mod side_len == k
                                        (reduce + (keep (fn [i]
                                                          (when (and (< i n)
                                                                     (= k (mod (quot i stride) side-len)))
                                                            (long (nth gem-vals i))))
                                                        (range (min used n)))))
                                      (range side-len))))
                            (range d))]
        (println (format "  %dD cube, side=%d, used=%,d letters:" d side-len used))
        (doseq [axis (range d)]
          (let [sums (nth axis-sums axis)
                half (quot (count sums) 2)
                fh (mapv double (subvec sums 0 half))
                sh (mapv double (vec (reverse (subvec sums (- (count sums) half)))))]
            (println (format "    Axis %d palindrome: cos = %.6f" axis
                             (cosine-sim fh sh))))))))

  (println "\nDone. The dimensions are opened."))
