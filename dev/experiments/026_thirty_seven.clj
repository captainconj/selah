(ns experiments.026-thirty-seven
  "37 — the prime that won't let go.
   2701 = 73 × 37. 333 = 9 × 37. 111 = 3 × 37.
   73 = 21st prime. 37 = 12th prime.
   37 + 73 = 110. 37 × 73 = 2701.
   Where is 37 in the structure?
   Run: clojure -M:dev -m experiments.026-thirty-seven"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

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

(defn -main []
  (println "=== THIRTY-SEVEN ===")
  (println "  The prime hidden in everything.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "  %,d letters. Total gematria: %,d\n" n total-gem))

    ;; ── 1. 37 in the Torah's numbers ────────────────────────
    (println "── 1. The 37 Web ──")
    (println "  How 37 connects to every key number.\n")

    (println "  Multiples of 37 in the Torah's structure:")
    (println (format "    2701 = 73 × 37 = T(73)   — Genesis 1:1"))
    (println (format "    333  = 9 × 37            — center word gematria"))
    (println (format "    111  = 3 × 37            — אלף (Aleph spelled out)"))
    (println (format "    37 itself = the 12th prime"))
    (println (format "    73 = 37 reversed digits, the 21st prime"))
    (println (format "    37 + 73 = 110             — יוסף (Joseph) = 156, הוא (he) = 12"))
    (println (format "    37 × 3 = 111, × 6 = 222, × 9 = 333, × 12 = 444, ..."))
    (println (format "    Total gem = %,d" total-gem))
    (println (format "    Total gem mod 37 = %d" (mod total-gem 37)))
    (println (format "    Total gem / 37 = %,.2f" (/ (double total-gem) 37)))

    ;; ── 2. Every 37th letter ────────────────────────────────
    (println "\n── 2. Every 37th Letter ──\n")

    (let [sub37 (mapv #(nth all-letters %) (range 0 n 37))
          sub37-gems (mapv #(long (g/letter-value %)) sub37)
          sub37-sum (reduce + sub37-gems)
          sub37-prof (letter-profile sub37)
          torah-prof (letter-profile all-letters)]
      (println (format "  Every 37th letter: %,d letters" (count sub37)))
      (println (format "  cos(Torah): %.6f" (cosine-sim sub37-prof torah-prof)))
      (println (format "  Σ gematria: %,d" sub37-sum))
      (println (format "  Σ mod 37: %d" (mod sub37-sum 37)))
      (println (format "  Σ mod 7: %d" (mod sub37-sum 7)))
      (println (format "  Σ mod 73: %d" (mod sub37-sum 73)))

      ;; Internal palindrome
      (let [half (quot (count sub37) 2)
            pa (letter-profile (subvec sub37 0 half))
            pb (letter-profile (vec (reverse (subvec sub37 half))))
            pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
        (println (format "  Internal palindrome: %.4f" pal))))

    ;; ── 3. Every 73rd letter ────────────────────────────────
    (println "\n── 3. Every 73rd Letter ──\n")

    (let [sub73 (mapv #(nth all-letters %) (range 0 n 73))
          sub73-gems (mapv #(long (g/letter-value %)) sub73)
          sub73-sum (reduce + sub73-gems)
          sub73-prof (letter-profile sub73)
          torah-prof (letter-profile all-letters)]
      (println (format "  Every 73rd letter: %,d letters" (count sub73)))
      (println (format "  cos(Torah): %.6f" (cosine-sim sub73-prof torah-prof)))
      (println (format "  Σ gematria: %,d" sub73-sum))
      (println (format "  Σ mod 37: %d" (mod sub73-sum 37)))
      (println (format "  Σ mod 73: %d" (mod sub73-sum 73)))

      ;; Internal palindrome
      (let [half (quot (count sub73) 2)
            pa (letter-profile (subvec sub73 0 half))
            pb (letter-profile (vec (reverse (subvec sub73 half))))
            pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
        (println (format "  Internal palindrome: %.4f" pal))))

    ;; ── 4. Divide into 37 segments ──────────────────────────
    (println "\n── 4. The 37-fold Division ──")
    (println "  Split the Torah into 37 equal segments.\n")

    (let [seg-size (quot n 37)
          segments (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end (if (= i 36) n (* (inc i) seg-size))
                                 chunk (subvec gem-vals start end)
                                 s (reduce + chunk)]
                             {:seg (inc i) :sum s}))
                         (range 37))
          sums (mapv :sum segments)
          mean-sum (/ (double (reduce + sums)) 37)]

      ;; Show the 37 sums
      (println (format "  Segment size: %,d letters" seg-size))
      (println (format "  Mean: %,.0f\n" mean-sum))

      ;; Is this palindromic?
      (let [half 18
            first-half (mapv double (subvec sums 0 half))
            second-half-rev (mapv double (vec (reverse (subvec sums 19 37))))]
        (println (format "  Palindrome (1-18 vs rev 20-37): cos = %.6f"
                         (cosine-sim first-half second-half-rev)))
        (println (format "  Center segment (19): %,d" (nth sums 18))))

      ;; Sum of first half vs second half
      (let [first-sum (reduce + (subvec sums 0 18))
            center (nth sums 18)
            second-sum (reduce + (subvec sums 19 37))]
        (println (format "\n  First 18 segments: %,d" first-sum))
        (println (format "  Center segment:    %,d" center))
        (println (format "  Last 18 segments:  %,d" second-sum))
        (println (format "  |First - Last|:    %,d (%.3f%%)"
                         (Math/abs (- first-sum second-sum))
                         (* 100 (/ (double (Math/abs (- first-sum second-sum)))
                                   (/ (+ (double first-sum) second-sum) 2.0)))))))

    ;; ── 5. Divide into 73 segments ──────────────────────────
    (println "\n── 5. The 73-fold Division ──")
    (println "  Split the Torah into 73 equal segments.\n")

    (let [seg-size (quot n 73)
          segments (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end (if (= i 72) n (* (inc i) seg-size))
                                 chunk (subvec gem-vals start end)
                                 s (reduce + chunk)]
                             {:seg (inc i) :sum s}))
                         (range 73))
          sums (mapv :sum segments)]

      (println (format "  Segment size: %,d letters" seg-size))

      ;; Palindrome
      (let [half 36
            first-half (mapv double (subvec sums 0 half))
            second-half-rev (mapv double (vec (reverse (subvec sums 37 73))))]
        (println (format "  Palindrome (1-36 vs rev 38-73): cos = %.6f"
                         (cosine-sim first-half second-half-rev)))
        (println (format "  Center segment (37): %,d" (nth sums 36)))))

    ;; ── 6. The repunit connection ───────────────────────────
    (println "\n── 6. Repunit Connection ──")
    (println "  37 × 3 = 111 (repunit). 37 × n gives: 37, 74, 111, 148, 185, 222, ...")
    (println "  How many verse sums are multiples of 37?\n")

    (let [verse-gems (atom [])
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [v verses]
                    (let [stripped (norm/strip-html v)
                          letters (norm/letter-stream stripped)
                          gem-sum (reduce + (map #(long (g/letter-value %)) letters))]
                      (swap! verse-gems conj gem-sum))))))
          v-gems @verse-gems
          n-verses (count v-gems)
          div37 (count (filter #(zero? (mod % 37)) v-gems))
          div73 (count (filter #(zero? (mod % 73)) v-gems))]

      (println (format "  Verses divisible by 37: %,d / %,d = %.2f%% (expected %.2f%%)"
                       div37 n-verses
                       (* 100 (/ (double div37) n-verses))
                       (* 100 (/ 1.0 37))))
      (println (format "  Verses divisible by 73: %,d / %,d = %.2f%% (expected %.2f%%)"
                       div73 n-verses
                       (* 100 (/ (double div73) n-verses))
                       (* 100 (/ 1.0 73)))))

    ;; ── 7. Star numbers ─────────────────────────────────────
    (println "\n── 7. Star of David Numbers ──")
    (println "  Star(n) = 6n(n-1) + 1 = hexagonal star")
    (println "  Star(1)=1, Star(2)=13, Star(3)=37, Star(4)=73, Star(5)=121...\n")

    (let [star (fn [k] (+ 1 (* 6 k (dec k))))]
      (println "  The star sequence:")
      (doseq [k (range 1 11)]
        (println (format "    Star(%d) = %d%s"
                         k (star k)
                         (cond
                           (= k 3) "  ← 37"
                           (= k 4) "  ← 73 (37 reversed)"
                           (= k 7) (format "  (mod 7 = %d)" (mod (star k) 7))
                           :else ""))))

      (println (format "\n  37 = Star(3)"))
      (println (format "  73 = Star(4)"))
      (println (format "  37 × 73 = 2701 = T(73) = Gen 1:1"))
      (println (format "  Star(3) × Star(4) = T(Star(4))"))
      (println (format "  This is a known property: Star(n) × Star(n+1) = T(Star(n+1))"))
      (println (format "  Genesis 1:1 sits at the intersection of star and triangle numbers.")))

    ;; ── 8. Book-level divisibility by 37 ────────────────────
    (println "\n── 8. Book Gematria and 37 ──\n")

    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (let [letters (sefaria/book-letters book)
            gem-sum (reduce + (map #(long (g/letter-value %)) letters))]
        (println (format "  %12s: %,10d  mod 37 = %2d  mod 73 = %2d  ÷37 = %,.2f"
                         book gem-sum (mod gem-sum 37) (mod gem-sum 73) (/ (double gem-sum) 37)))))

    (println (format "\n  Total: %,d  mod 37 = %d  ÷37 = %,.2f"
                     total-gem (mod total-gem 37) (/ (double total-gem) 37)))

    ;; ── 9. The 37/73 lattice ────────────────────────────────
    (println "\n── 9. The 37/73 Lattice ──")
    (println "  Map the Torah onto a 37×73 grid (2701 cells).")
    (println "  Each cell is ~113 letters — roughly half a verse.\n")

    (let [cell-size (quot n 2701)
          ;; Calculate gematria for the first, center, and last cell
          first-cell-gem (reduce + (subvec gem-vals 0 cell-size))
          center-cell-start (* 1350 cell-size)
          center-cell-gem (reduce + (subvec gem-vals center-cell-start (+ center-cell-start cell-size)))
          last-cell-start (* 2700 cell-size)
          last-cell-gem (reduce + (subvec gem-vals last-cell-start (min n (+ last-cell-start cell-size))))]

      (println (format "  Grid: 37 rows × 73 columns = 2,701 cells"))
      (println (format "  Cell size: %,d letters" cell-size))
      (println (format "  First cell gematria:  %,d" first-cell-gem))
      (println (format "  Center cell gematria: %,d" center-cell-gem))
      (println (format "  Last cell gematria:   %,d" last-cell-gem))

      ;; Row sums (37 rows)
      (let [row-sums (mapv (fn [r]
                              (let [row-start (* r 73 cell-size)
                                    row-end (min n (* (inc r) 73 cell-size))]
                                (if (< row-start n)
                                  (reduce + (subvec gem-vals row-start (min row-end n)))
                                  0)))
                            (range 37))
            half 18
            first18 (mapv double (subvec row-sums 0 half))
            last18 (mapv double (vec (reverse (subvec row-sums 19 37))))]
        (println (format "\n  37 row sums palindrome: cos = %.6f"
                         (cosine-sim first18 last18)))
        (println (format "  Center row (19): %,d" (nth row-sums 18))))

      ;; Column sums (73 columns)
      (let [col-sums (mapv (fn [c]
                              (reduce + (for [r (range 37)]
                                          (let [start (+ (* r 73 cell-size) (* c cell-size))
                                                end (+ start cell-size)]
                                            (if (< start n)
                                              (reduce + (subvec gem-vals start (min end n)))
                                              0)))))
                            (range 73))
            half 36
            first36 (mapv double (subvec col-sums 0 half))
            last36 (mapv double (vec (reverse (subvec col-sums 37 73))))]
        (println (format "  73 column sums palindrome: cos = %.6f"
                         (cosine-sim first36 last36)))
        (println (format "  Center column (37): %,d" (nth col-sums 36))))))

  (println "\nDone. 37 is everywhere."))
