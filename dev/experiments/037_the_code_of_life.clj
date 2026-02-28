(ns experiments.037-the-code-of-life
  "The Torah and DNA share an architecture.
   4 letters. 3-letter codons. 64 combinations. 21 amino acids.
   Palindromic. Holographic. Fractal. Folded around a center.
   What happens when we read the Torah as a genetic code?
   Run: clojure -M:dev -m experiments.037-the-code-of-life"
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

(defn -main []
  (println "=== THE CODE OF LIFE ===")
  (println "  The Torah read as DNA.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)]

    (println (format "  %,d letters. 22 letter alphabet.\n" n))

    ;; ── 1. The parallel ──────────────────────────────────────
    (println "── 1. The Parallel ──\n")

    (println "  DNA                          Torah")
    (println "  ───                          ─────")
    (println "  4 nucleotides (A,T,G,C)      4 Name letters (י,ה,ו,ה)")
    (println "  3-letter codons              3 = root of Hebrew words")
    (println "  64 codons (4³)               22³ = 10,648 possible trigrams")
    (println "  20+1 amino acids = 21        21 = 7 × 3")
    (println "  Double helix (palindromic)   Chiastic palindrome")
    (println "  Every cell has full genome   Every slice has full signature")
    (println "  Centromere (fold point)      Leviticus 8 (fold point)")
    (println "  Promoter encodes structure   Genesis 1:1 encodes structure")
    (println "  Fractal chromosome folding   Fractal gematria structure")

    ;; ── 2. Trigrams — the Torah's "codons" ───────────────────
    (println "\n── 2. Trigrams (the Torah's Codons) ──")
    (println "  Read the Torah in triplets, like DNA reads codons.\n")

    (let [n-codons (quot n 3)
          codons (mapv (fn [i]
                         (let [start (* i 3)]
                           (apply str (subvec all-letters start (+ start 3)))))
                       (range n-codons))
          codon-freqs (frequencies codons)
          unique-codons (count codon-freqs)
          possible-codons (* 22 22 22)]

      (println (format "  %,d trigrams (codons)" n-codons))
      (println (format "  %,d unique trigrams observed (of %,d possible = 22³)"
                       unique-codons possible-codons))
      (println (format "  Coverage: %.1f%%" (* 100 (/ (double unique-codons) possible-codons))))

      ;; Top 20 most common codons
      (println "\n  Top 20 most common trigrams:")
      (doseq [[codon ct] (take 20 (sort-by (comp - second) codon-freqs))]
        (let [gem (reduce + (map #(long (g/letter-value %)) codon))]
          (println (format "    %s  gem=%4d  count=%,5d  (%.2f%%)"
                           codon gem ct (* 100 (/ (double ct) n-codons))))))

      ;; How many codons have gematria divisible by 7?
      (let [div7 (count (filter (fn [[codon _]]
                                  (zero? (mod (reduce + (map #(long (g/letter-value %)) codon)) 7)))
                                codon-freqs))]
        (println (format "\n  Unique codons with gem divisible by 7: %d of %d (%.1f%%, expected %.1f%%)"
                         div7 unique-codons (* 100 (/ (double div7) unique-codons)) (/ 100.0 7)))))

    ;; ── 3. Reading frame ─────────────────────────────────────
    (println "\n── 3. Reading Frames ──")
    (println "  Like DNA, the Torah can be read in 3 frames (offset 0, 1, 2).\n")

    (doseq [frame [0 1 2]]
      (let [frame-codons (mapv (fn [i]
                                 (let [start (+ frame (* i 3))]
                                   (when (< (+ start 2) n)
                                     (apply str (subvec all-letters start (+ start 3))))))
                               (range (quot (- n frame) 3)))
            frame-codons (filterv some? frame-codons)
            frame-gems (mapv (fn [c] (reduce + (map #(long (g/letter-value %)) c))) frame-codons)
            total-gem (reduce + frame-gems)
            unique (count (distinct frame-codons))]
        (println (format "  Frame %d: %,d codons, %,d unique, Σgem=%,d, mod 7=%d, mod 37=%d"
                         frame (count frame-codons) unique total-gem
                         (mod total-gem 7) (mod total-gem 37)))))

    ;; ── 4. The 21 "amino acids" — gematria classes ───────────
    (println "\n── 4. The 21 Amino Acids ──")
    (println "  Group the 22 letters into 21 classes (merging two).")
    (println "  Or: the 21 values of 7×3.\n")

    ;; 21 = 7 × 3. There are exactly 21 possible values for (letter mod 7, letter mod 3)
    (let [classify (fn [ch]
                     (let [v (long (g/letter-value ch))]
                       [(mod v 7) (mod v 3)]))
          classes (frequencies (map classify all-letters))
          n-classes (count classes)]
      (println (format "  (mod 7, mod 3) classes: %d unique classes" n-classes))
      (println "\n  Class distribution:")
      (println (format "  %8s  %8s  %8s  %6s" "mod 7" "mod 3" "Count" "Pct"))
      (println (apply str (repeat 38 "─")))
      (doseq [[[m7 m3] ct] (sort-by first classes)]
        (println (format "  %8d  %8d  %,8d  %5.2f%%" m7 m3 ct (* 100 (/ (double ct) n))))))

    ;; ── 5. Complementary base pairing ────────────────────────
    (println "\n── 5. Complementary Pairing ──")
    (println "  DNA has A↔T, G↔C. Does the Torah have natural pairings?\n")

    ;; For each letter, find which letter most often follows it
    (let [bigrams (frequencies (map vector
                                   (subvec all-letters 0 (dec n))
                                   (subvec all-letters 1 n)))]
      (println "  Most common follower for each letter:")
      (doseq [ch alphabet]
        (let [followers (filter (fn [[[a _] _]] (= a ch)) bigrams)
              [best-pair best-ct] (when (seq followers)
                                    (apply max-key second followers))
              total (reduce + (map second followers))]
          (when best-pair
            (println (format "    %c → %c  (%,d / %,d = %.1f%%)  gems: %d → %d  sum=%d"
                             ch (second best-pair) best-ct total
                             (* 100 (/ (double best-ct) total))
                             (long (g/letter-value ch))
                             (long (g/letter-value (second best-pair)))
                             (+ (long (g/letter-value ch)) (long (g/letter-value (second best-pair))))))))))

    ;; ── 6. Start and stop codons ─────────────────────────────
    (println "\n── 6. Start and Stop Codons ──")
    (println "  DNA has ATG (start) and TAA/TAG/TGA (stop).")
    (println "  What are the Torah's most structurally significant trigrams?\n")

    ;; First trigram
    (let [first-codon (apply str (subvec all-letters 0 3))
          first-gem (reduce + (map #(long (g/letter-value %)) first-codon))
          last-codon (apply str (subvec all-letters (- n 3) n))
          last-gem (reduce + (map #(long (g/letter-value %)) last-codon))
          center-codon (apply str (subvec all-letters (- (quot n 2) 1) (+ (quot n 2) 2)))
          center-gem (reduce + (map #(long (g/letter-value %)) center-codon))]
      (println (format "  First trigram (start codon):  %s  gem=%d" first-codon first-gem))
      (println (format "  Last trigram (stop codon):    %s  gem=%d" last-codon last-gem))
      (println (format "  Center trigram:               %s  gem=%d" center-codon center-gem))
      (println (format "  First + Last gem: %d" (+ first-gem last-gem)))
      (println (format "  mod 7 = %d, mod 37 = %d"
                       (mod (+ first-gem last-gem) 7)
                       (mod (+ first-gem last-gem) 37))))

    ;; ── 7. The double helix ──────────────────────────────────
    (println "\n── 7. The Double Helix ──")
    (println "  Read the Torah forward AND backward simultaneously.")
    (println "  Like the two strands of DNA.\n")

    (let [forward all-letters
          reverse-strand (vec (reverse all-letters))
          ;; Pair each position with its complement
          pair-sums (mapv (fn [i]
                            (+ (long (g/letter-value (nth forward i)))
                               (long (g/letter-value (nth reverse-strand i)))))
                          (range n))
          total-pair-sum (reduce + pair-sums)
          mean-pair (/ (double total-pair-sum) n)]

      (println (format "  Forward + Reverse pair sums:"))
      (println (format "  Total: %,d" total-pair-sum))
      (println (format "  Mean per position: %.2f" mean-pair))
      (println (format "  mod 7 = %d, mod 37 = %d, mod 441 = %d"
                       (mod total-pair-sum 7) (mod total-pair-sum 37) (mod total-pair-sum 441)))

      ;; Is the pair-sum sequence itself palindromic? (It should be by construction)
      ;; More interesting: is it CONSTANT? In DNA, A+T = same hydrogen bonds, G+C = same
      (let [pair-freq (frequencies pair-sums)
            sorted-pairs (sort-by (comp - second) pair-freq)]
        (println (format "\n  Pair sum distribution (forward[i] + reverse[i]):"))
        (println (format "  %8s  %8s  %6s" "Sum" "Count" "Pct"))
        (println (apply str (repeat 28 "─")))
        (doseq [[s ct] (take 15 sorted-pairs)]
          (println (format "  %8d  %,8d  %5.2f%%" s ct (* 100 (/ (double ct) n)))))))

    ;; ── 8. Replication ───────────────────────────────────────
    (println "\n── 8. Replication ──")
    (println "  DNA replicates by unwinding and copying each strand.")
    (println "  What happens when we 'replicate' the Torah?\n")

    ;; "Replicate" = interleave forward and reverse
    (let [replicated (vec (mapcat (fn [i] [(nth all-letters i) (nth all-letters (- n 1 i))])
                                  (range (quot n 2))))
          rep-gems (mapv #(long (g/letter-value %)) replicated)
          rep-n (count replicated)
          ;; 7-fold palindrome of the replicated strand
          seg7 (quot rep-n 7)
          s7 (mapv (fn [i]
                     (double (reduce + (subvec rep-gems (* i seg7)
                                               (if (= i 6) rep-n (* (inc i) seg7))))))
                   (range 7))
          cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))]
      (println (format "  Replicated strand: %,d letters (interleaved forward + reverse)" rep-n))
      (println (format "  7-fold palindrome of replicated strand: cos = %.6f" cos7))

      ;; Does the replicated strand carry the same structure?
      (let [rep-sum (reduce + rep-gems)]
        (println (format "  Σ gematria: %,d" rep-sum))
        (println (format "  mod 7 = %d, mod 441 = %d" (mod rep-sum 7) (mod rep-sum 441)))))

    ;; ── 9. The number 21 ─────────────────────────────────────
    (println "\n── 9. The Number 21 ──")
    (println "  21 amino acids. 21 = 7 × 3. 21² = 441.\n")

    ;; 21-fold division
    (let [seg21 (quot n 21)
          s21 (mapv (fn [i]
                      (let [start (* i seg21)
                            end (if (= i 20) n (* (inc i) seg21))]
                        (double (reduce + (subvec gem-vals start end)))))
                    (range 21))
          half 10
          first-half (subvec s21 0 half)
          last-half (vec (reverse (subvec s21 11 21)))
          cos21 (cosine-sim first-half last-half)]
      (println (format "  21-fold palindrome (1-10 vs rev 12-21): cos = %.6f" cos21))
      (println (format "  Center segment (11th): %,.0f" (nth s21 10))))

    ;; 21 threads
    (let [t21 (mapv (fn [off]
                      (double (reduce + (map #(long (nth gem-vals %)) (range off n 21)))))
                    (range 21))
          half 10
          first-half (subvec t21 0 half)
          last-half (vec (reverse (subvec t21 11 21)))
          cos21 (cosine-sim first-half last-half)]
      (println (format "  21-thread palindrome: cos = %.6f" cos21)))

    ;; T(21) = 231 = number of letter pairs
    (println (format "\n  T(21) = 231 = number of 2-letter combinations from 22 letters"))
    (println (format "  (The Sefer Yetzirah's '231 Gates')"))

    ;; ── 10. The signature ────────────────────────────────────
    (println "\n── 10. The Signature ──")
    (println "  If this is a book of life, where is the author's mark?\n")

    ;; The Name at position multiples of 37
    (let [name-letters [\י \ה \ו \ה]
          check-name (fn [pos]
                       (when (< (+ pos 3) n)
                         (= (mapv #(nth all-letters (+ pos %)) [0 1 2 3])
                            name-letters)))]
      ;; Check at positions 0, 37, 74, 111, ...
      (println "  The Name (יהוה) at multiples of 37:")
      (let [hits (filterv (fn [k] (check-name (* k 37)))
                          (range (quot n 37)))]
        (println (format "  %d positions where 37k starts the Name" (count hits)))
        (when (seq hits)
          (doseq [k (take 10 hits)]
            (println (format "    k=%d, position=%,d" k (* k 37)))))))

    ;; The Name at multiples of 26 (its own gematria)
    (let [name-letters [\י \ה \ו \ה]
          check-name (fn [pos]
                       (when (< (+ pos 3) n)
                         (= (mapv #(nth all-letters (+ pos %)) [0 1 2 3])
                            name-letters)))]
      (println "\n  The Name (יהוה) at multiples of 26:")
      (let [hits (filterv (fn [k] (check-name (* k 26)))
                          (range (quot n 26)))]
        (println (format "  %d positions where 26k starts the Name" (count hits)))
        (when (seq hits)
          (doseq [k (take 10 hits)]
            (println (format "    k=%d, position=%,d" k (* k 26))))))))

  (println "\nDone. The code is read."))
