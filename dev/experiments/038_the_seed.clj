(ns experiments.038-the-seed
  "Genesis 1:1 is the seed.
   2701 = 37 × 73 = T(73).
   If the first verse encodes the whole, then the whole
   should be derivable from the first verse.
   What happens when we use Gen 1:1 as a key to read the Torah?
   Run: clojure -M:dev -m experiments.038-the-seed"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]))

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
  (println "=== THE SEED ===")
  (println "  Genesis 1:1 as the key to the whole.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)

        ;; Genesis 1:1 — the seed
        gen11-letters (vec (take 28 all-letters))  ;; 28 letters in Gen 1:1
        gen11-gems (mapv #(long (g/letter-value %)) gen11-letters)
        gen11-sum (reduce + gen11-gems)]

    (println (format "  %,d letters. Total gematria: %,d\n" n total-gem))

    ;; ── 1. The seed itself ───────────────────────────────────
    (println "── 1. The Seed ──\n")

    (println (format "  Genesis 1:1: %s" (apply str gen11-letters)))
    (println (format "  %d letters. Gematria: %d = 37 × 73 = T(73)" (count gen11-letters) gen11-sum))
    (println (format "  28 = T(7) — the 7th triangle number"))
    (println (format "  28 = 4 × 7 — the number of letters in the seed"))

    ;; Individual word gematria
    (let [words [[0 6] [6 9] [9 14] [14 16] [16 21] [21 24] [24 28]]
          word-names ["בראשית" "ברא" "אלהים" "את" "השמים" "ואת" "הארץ"]]
      (println "\n  Word gematria:")
      (doseq [[i [start end]] (map-indexed vector words)]
        (let [w (subvec gen11-letters start end)
              wg (reduce + (map #(long (g/letter-value %)) w))]
          (println (format "    %d. %s  gem=%d" (inc i) (nth word-names i) wg)))))

    ;; ── 2. Read the Torah at skip 2701 ───────────────────────
    (println "\n── 2. Every 2701st Letter ──")
    (println "  Use the seed's gematria as the skip.\n")

    (let [sub (mapv #(nth all-letters %) (range 0 n 2701))
          sub-gems (mapv #(long (g/letter-value %)) sub)
          sub-sum (reduce + sub-gems)
          sub-prof (letter-profile sub)
          torah-prof (letter-profile all-letters)]
      (println (format "  %d letters at skip 2701" (count sub)))
      (println (format "  Letters: %s..." (apply str (take 20 sub))))
      (println (format "  Σ gematria: %,d" sub-sum))
      (println (format "  mod 7 = %d, mod 37 = %d, mod 73 = %d"
                       (mod sub-sum 7) (mod sub-sum 37) (mod sub-sum 73)))
      (println (format "  cos(Torah): %.6f" (cosine-sim sub-prof torah-prof)))

      ;; Palindrome
      (let [half (quot (count sub) 2)
            pa (letter-profile (subvec sub 0 half))
            pb (letter-profile (vec (reverse (subvec sub half))))]
        (println (format "  palindrome: %.4f" (if (and pa pb) (cosine-sim pa pb) 0.0)))))

    ;; ── 3. The 28-letter window ──────────────────────────────
    (println "\n── 3. Every 28th Window ──")
    (println "  Slide a 28-letter window (seed length) across the Torah.\n")

    (let [window-size 28
          n-windows (- n window-size)
          ;; Find windows with gematria = 2701
          hits-2701 (filterv (fn [i]
                               (= 2701 (reduce + (subvec gem-vals i (+ i window-size)))))
                             (range n-windows))]
      (println (format "  Windows of 28 letters with gematria 2701: %d" (count hits-2701)))
      (when (seq hits-2701)
        (println "  Positions:")
        (doseq [pos (take 10 hits-2701)]
          (let [window (subvec all-letters pos (+ pos window-size))]
            (println (format "    pos=%,6d  %s" pos (apply str window)))))))

    ;; Find windows with gematria = 441
    (let [window-size 28
          hits-441 (count (filter (fn [i]
                                    (= 441 (reduce + (subvec gem-vals i (+ i window-size)))))
                                  (range (- n window-size))))]
      (println (format "\n  Windows of 28 letters with gematria 441: %d" hits-441)))

    ;; ── 4. The seed's ratio in the Torah ─────────────────────
    (println "\n── 4. Seed Ratios ──")
    (println "  Does the Torah / Seed ratio reveal anything?\n")

    (let [ratio (/ (double total-gem) gen11-sum)]
      (println (format "  Torah / Gen 1:1 = %,d / %d = %.4f" total-gem gen11-sum ratio))
      (println (format "  = %,.2f × 2701" (/ (double total-gem) 2701)))
      (println (format "  Total / 37 = %,.2f" (/ (double total-gem) 37)))
      (println (format "  Total / 73 = %,.2f" (/ (double total-gem) 73)))
      (println (format "  Total / 2701 = %,.4f" (/ (double total-gem) 2701)))
      (println (format "  Total mod 2701 = %d" (mod total-gem 2701))))

    ;; ── 5. The 7 words as 7 keys ─────────────────────────────
    (println "\n── 5. Seven Words, Seven Keys ──")
    (println "  Use each word's gematria as a skip.\n")

    (let [word-gems [913 203 86 401 395 407 296]]
      (doseq [[i wg] (map-indexed vector word-gems)]
        (let [sub (mapv #(nth all-letters %) (range 0 n wg))
              sub-sum (reduce + (map #(long (g/letter-value %)) sub))
              sub-n (count sub)]
          (println (format "  Word %d (gem=%d): %,d letters at skip %d, Σ=%,d, mod 7=%d"
                           (inc i) wg sub-n wg sub-sum (mod sub-sum 7))))))

    ;; ── 6. The seed as a frequency key ───────────────────────
    (println "\n── 6. Seed as Frequency Key ──")
    (println "  Does the letter distribution of Gen 1:1 predict the Torah's?\n")

    (let [seed-prof (letter-profile gen11-letters)
          torah-prof (letter-profile all-letters)
          cos (cosine-sim seed-prof torah-prof)]
      (println (format "  cos(Gen 1:1, Torah): %.6f" cos))
      (println "  (How well 28 letters predict 306,269)")

      ;; Compare to random 28-letter slices
      (let [n-trials 10000
            random-cos (mapv (fn [_]
                               (let [start (rand-int (- n 28))
                                     slice (subvec all-letters start (+ start 28))
                                     sp (letter-profile slice)]
                                 (if sp (cosine-sim sp torah-prof) 0.0)))
                             (range n-trials))
            mean-cos (/ (reduce + random-cos) n-trials)
            better-count (count (filter #(> % cos) random-cos))]
        (println (format "  Mean cos for random 28-letter slice: %.6f" mean-cos))
        (println (format "  Gen 1:1 vs random: %d of %,d random slices score higher (%.1f%%)"
                         better-count n-trials (* 100 (/ (double better-count) n-trials))))))

    ;; ── 7. T(73) and the triangle ───────────────────────────
    (println "\n── 7. The Triangle ──")
    (println "  2701 = T(73). Map the Torah onto a triangle of side 73.\n")

    ;; Triangle number T(k) = k(k+1)/2
    ;; Row r (0-indexed) has r+1 elements. Total rows = 73.
    (let [triangle-size 2701
          cell-size (quot n triangle-size)
          ;; Row sums
          row-sums (loop [row 0 pos 0 sums []]
                     (if (>= row 73)
                       sums
                       (let [row-len (inc row)
                             row-letters (* row-len cell-size)
                             end (min n (+ pos row-letters))
                             s (if (< pos n) (reduce + (subvec gem-vals pos (min end n))) 0)]
                         (recur (inc row) end (conj sums (double s))))))]

      (println (format "  73 rows, cell size=%,d letters" cell-size))

      ;; Is the triangle palindromic? (rows 0-35 vs reversed rows 37-72)
      (let [half 36
            first-half (subvec row-sums 0 half)
            last-half (vec (reverse (subvec row-sums 37 73)))]
        (println (format "  Triangle row palindrome (0-35 vs rev 37-72): cos = %.6f"
                         (cosine-sim first-half last-half)))
        (println (format "  Center row (36): %,.0f" (nth row-sums 36)))))

    ;; ── 8. The 37 × 73 rectangle ────────────────────────────
    (println "\n── 8. The 37 × 73 Rectangle (revisited) ──")
    (println "  This is the Genesis 1:1 grid. 2701 cells.\n")

    (let [cell-size (quot n 2701)
          ;; Build 37 row sums and 73 column sums
          row-sums (mapv (fn [r]
                           (let [start (* r 73 cell-size)
                                 end (min n (* (inc r) 73 cell-size))]
                             (if (< start n)
                               (double (reduce + (subvec gem-vals start (min end n))))
                               0.0)))
                         (range 37))
          col-sums (mapv (fn [c]
                           (double (reduce + (for [r (range 37)]
                                               (let [start (+ (* r 73 cell-size) (* c cell-size))
                                                     end (+ start cell-size)]
                                                 (if (< start n)
                                                   (reduce + (subvec gem-vals start (min end n)))
                                                   0))))))
                         (range 73))
          ;; Row palindrome
          r-half 18
          row-pal (cosine-sim (subvec row-sums 0 r-half)
                              (vec (reverse (subvec row-sums 19 37))))
          ;; Col palindrome
          c-half 36
          col-pal (cosine-sim (subvec col-sums 0 c-half)
                              (vec (reverse (subvec col-sums 37 73))))
          ;; Diagonal sum
          diag-sum (reduce + (map (fn [r]
                                    (let [c (mod r 73)
                                          start (+ (* r 73 cell-size) (* c cell-size))
                                          end (+ start cell-size)]
                                      (if (< start n)
                                        (reduce + (subvec gem-vals start (min end n)))
                                        0)))
                                  (range 37)))]

      (println (format "  Cell size: %,d letters" cell-size))
      (println (format "  37 row palindrome: cos = %.6f" row-pal))
      (println (format "  73 col palindrome: cos = %.6f" col-pal))
      (println (format "  Main diagonal Σ: %,.0f" (double diag-sum)))
      (println (format "  Center cell (18,36): row %d, col %d" 18 36))

      ;; The center cell
      (let [center-start (+ (* 18 73 cell-size) (* 36 cell-size))
            center-end (+ center-start cell-size)
            center-gem (reduce + (subvec gem-vals center-start (min center-end n)))]
        (println (format "  Center cell gematria: %,d" center-gem))
        (println (format "  mod 7 = %d, mod 37 = %d" (mod center-gem 7) (mod center-gem 37)))))

    ;; ── 9. The seed unfolds ──────────────────────────────────
    (println "\n── 9. The Seed Unfolds ──")
    (println "  How many of the Torah's structural constants are in the first verse?\n")

    (println "  From 28 letters:")
    (println (format "    28 = T(7) — 7th triangle"))
    (println (format "    2701 = T(73) — 73rd triangle"))
    (println (format "    2701 = 37 × 73 — product of mirror primes"))
    (println (format "    37 = Star(3), 73 = Star(4)"))
    (println (format "    First 4 / Last 3 = 1.460 ≈ √2"))
    (println (format "    Center word את = Aleph-Tav = totality"))
    (println (format "    7 words (the seven)"))
    (println (format "    73 = 21st prime, 21 = 7 × 3, 21² = 441"))
    (println (format "    Total Torah / 2701 = %.4f" (/ (double total-gem) 2701)))
    (println (format "    The seed contains: 7, 21, 28, 37, 73, √2, 441, 2701"))
    (println (format "    All of these govern the whole.")))

  (println "\nDone. The seed contains the tree."))
