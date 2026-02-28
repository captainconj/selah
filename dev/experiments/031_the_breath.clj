(ns experiments.031-the-breath
  "ה — He — the breath letter.
   It sits at the geometric center. It sits at the center of mass.
   It is the letter added to Abram's name to make Abraham.
   It is the final letter of the Divine Name.
   How does ה breathe through the Torah?
   Run: clojure -M:dev -m experiments.031-the-breath"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]))

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
  (println "=== THE BREATH ===")
  (println "  ה — He — the letter at the center.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. The frequency of ה ────────────────────────────────
    (println "── 1. He in the Torah ──\n")

    (let [he-count (count (filter #(= % \ה) all-letters))
          he-frac (/ (double he-count) n)]
      (println (format "  ה appears %,d times (%.3f%% of all letters)" he-count (* 100 he-frac)))
      (println (format "  Expected if uniform: %.3f%%" (/ 100.0 22)))
      (println (format "  ה is %.2fx more common than average" (/ he-frac (/ 1.0 22))))

      ;; Rank among all letters
      (let [freqs (frequencies all-letters)
            ranked (sort-by (comp - second) freqs)]
        (println "\n  Letter frequency ranking:")
        (doseq [[ch cnt] (take 22 ranked)]
          (let [pct (* 100 (/ (double cnt) n))]
            (println (format "    %c  %,6d  %5.2f%%%s"
                             ch cnt pct
                             (cond
                               (= ch \ה) "  ← center letter"
                               (= ch \י) "  ← (yod, value 10)"
                               (= ch \ו) "  ← (vav, value 6)"
                               :else "")))))))

    ;; ── 2. He positions — are they structured? ───────────────
    (println "\n── 2. He Positions ──")
    (println "  Where does ה appear? Is the spacing structured?\n")

    (let [he-positions (vec (keep-indexed (fn [i ch] (when (= ch \ה) i)) all-letters))
          he-n (count he-positions)
          ;; Gaps between consecutive ה
          gaps (mapv (fn [i] (- (nth he-positions (inc i)) (nth he-positions i)))
                     (range (dec he-n)))
          mean-gap (/ (double (reduce + gaps)) (count gaps))
          max-gap (apply max gaps)
          min-gap (apply min gaps)]

      (println (format "  %,d occurrences of ה" he-n))
      (println (format "  Mean gap: %.2f letters" mean-gap))
      (println (format "  Min gap: %d, Max gap: %d" min-gap max-gap))

      ;; Gap distribution mod 7
      (println "\n  Gap distribution mod 7:")
      (let [gap-mod7 (frequencies (map #(mod % 7) gaps))]
        (doseq [r (range 7)]
          (let [cnt (get gap-mod7 r 0)
                pct (* 100 (/ (double cnt) (count gaps)))]
            (println (format "    mod 7 = %d:  %,5d  (%.1f%%)" r cnt pct)))))

      ;; Are ה positions palindromic?
      (println "\n  Palindrome test on ה positions:")
      (let [half (quot he-n 2)
            first-half (mapv double (subvec he-positions 0 half))
            ;; Normalize: mirror the second half's positions
            second-half (subvec he-positions half)
            mirrored (mapv (fn [p] (double (- n 1 p))) (vec (reverse second-half)))
            cos (cosine-sim first-half (subvec mirrored 0 half))]
        (println (format "  cos(first-half positions, mirrored second-half): %.6f" cos))))

    ;; ── 3. He density across the Torah ───────────────────────
    (println "\n── 3. He Density ──")
    (println "  How does ה density vary across the Torah?\n")

    (let [n-bins 70
          bin-size (quot n n-bins)]
      (println (format "  %4s  %6s  %6s  %s" "Bin" "Count" "Density" "Histogram"))
      (println (apply str (repeat 50 "─")))

      (let [densities (mapv (fn [i]
                              (let [start (* i bin-size)
                                    end (min n (* (inc i) bin-size))
                                    seg (subvec all-letters start end)
                                    he-ct (count (filter #(= % \ה) seg))]
                                {:bin (inc i) :count he-ct
                                 :density (/ (double he-ct) (- end start))}))
                            (range n-bins))
            max-density (apply max (map :density densities))
            min-density (apply min (map :density densities))]

        (doseq [{:keys [bin count density]} densities]
          (let [bar-len (int (* 30 (/ density max-density)))
                bar (apply str (repeat bar-len "█"))]
            (println (format "  %4d  %,6d  %6.4f  %s" bin count density bar))))

        (println (format "\n  Max density: %.4f  Min density: %.4f  Range: %.4f"
                         max-density min-density (- max-density min-density)))
        (println (format "  Coefficient of variation: %.4f"
                         (let [mean-d (/ (reduce + (map :density densities)) n-bins)
                               var-d (/ (reduce + (map #(let [d (- (:density %) mean-d)] (* d d)) densities)) n-bins)]
                           (/ (Math/sqrt var-d) mean-d))))))

    ;; ── 4. The Divine Name and ה ─────────────────────────────
    (println "\n── 4. ה in the Divine Name ──")
    (println "  יהוה — YHVH — He appears twice in the Name.\n")

    ;; Count all 4-letter sequences that are יהוה
    (let [yhvh-positions (vec (keep (fn [i]
                                      (when (and (<= (+ i 3) n)
                                                 (= (nth all-letters i) \י)
                                                 (= (nth all-letters (+ i 1)) \ה)
                                                 (= (nth all-letters (+ i 2)) \ו)
                                                 (= (nth all-letters (+ i 3)) \ה))
                                        i))
                                    (range (- n 3))))
          yhvh-n (count yhvh-positions)]

      ;; Note: this counts in the letter stream, not in words
      (println (format "  יהוה as consecutive letters: %,d occurrences" yhvh-n))

      ;; How many ה are part of the Divine Name?
      ;; Each occurrence uses 2 ה's
      (let [total-he (count (filter #(= % \ה) all-letters))
            yhvh-he (* 2 yhvh-n)
            pct (* 100 (/ (double yhvh-he) total-he))]
        (println (format "  ה letters in Divine Name: %,d of %,d (%.1f%%)" yhvh-he total-he pct))))

    ;; ── 5. The five He's — the five books ────────────────────
    (println "\n── 5. He in Each Book ──")
    (println "  ה has value 5. There are 5 books. Coincidence?\n")

    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (let [letters (vec (sefaria/book-letters book))
            bk-n (count letters)
            he-ct (count (filter #(= % \ה) letters))
            pct (* 100 (/ (double he-ct) bk-n))]
        (println (format "  %12s: %,5d ה out of %,6d letters (%.3f%%)"
                         book he-ct bk-n pct))))

    ;; ── 6. Letters of the Divine Name ────────────────────────
    (println "\n── 6. YHVH Letters ──")
    (println "  The four letters of the Name: י ה ו ה\n")

    (let [name-letters #{\י \ה \ו}
          name-count (count (filter name-letters all-letters))
          pct (* 100 (/ (double name-count) n))]
      (println (format "  Letters {י, ה, ו} appear %,d times (%.2f%% of Torah)" name-count pct))
      (println (format "  Expected if uniform: %.2f%%" (* 100 (/ 3.0 22))))

      ;; These three letters by frequency
      (doseq [ch [\י \ה \ו]]
        (let [ct (count (filter #(= % ch) all-letters))]
          (println (format "    %c: %,d (%.2f%%)" ch ct (* 100 (/ (double ct) n))))))

      ;; Together they constitute:
      (println (format "\n  Together: %,d / %,d = %.2f%% of all letters"
                       name-count n pct))
      (println (format "  Nearly 1 in %.1f letters is a Name letter" (/ (double n) name-count))))

    ;; ── 7. He at structural positions ────────────────────────
    (println "\n── 7. He at Key Positions ──")
    (println "  What letter sits at each structural landmark?\n")

    (let [positions [["Geometric center" (quot n 2)]
                     ["1/7 mark" (quot n 7)]
                     ["2/7 mark" (quot (* 2 n) 7)]
                     ["3/7 mark" (quot (* 3 n) 7)]
                     ["4/7 mark" (quot (* 4 n) 7)]
                     ["5/7 mark" (quot (* 5 n) 7)]
                     ["6/7 mark" (quot (* 6 n) 7)]
                     ["1/5 mark" (quot n 5)]
                     ["2/5 mark" (quot (* 2 n) 5)]
                     ["3/5 mark" (quot (* 3 n) 5)]
                     ["4/5 mark" (quot (* 4 n) 5)]
                     ["φ point" (int (* n (/ (- (Math/sqrt 5) 1) 2)))]
                     ["1/e point" (int (/ (double n) Math/E))]
                     ["π/4 point" (int (* n (/ Math/PI 4)))]
                     ["1/√2 point" (int (/ (double n) (Math/sqrt 2)))]
                     ["37th position" 36]
                     ["73rd position" 72]
                     ["441st position" 440]
                     ["2701st position" 2700]]]
      (doseq [[label pos] positions]
        (when (< pos n)
          (let [ch (nth all-letters pos)
                gv (long (g/letter-value ch))]
            (println (format "  %18s (pos %,7d): %c  gem=%d%s"
                             label pos ch gv
                             (if (= ch \ה) "  ← ה!" "")))))))

    ;; ── 8. He-free zones ─────────────────────────────────────
    (println "\n── 8. Longest He-free Stretches ──")
    (println "  Where does the breath pause?\n")

    (let [he-pos (vec (keep-indexed (fn [i ch] (when (= ch \ה) i)) all-letters))
          gaps (mapv (fn [i] {:start (nth he-pos i)
                               :end (nth he-pos (inc i))
                               :length (- (nth he-pos (inc i)) (nth he-pos i))})
                     (range (dec (count he-pos))))
          top-gaps (take 10 (sort-by (comp - :length) gaps))
          ;; Build book map
          book-lengths (mapv (fn [b] (count (sefaria/book-letters b)))
                             ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
          cum (vec (reductions + 0 book-lengths))
          book-names ["Gen" "Exod" "Lev" "Num" "Deut"]
          which-book (fn [idx]
                       (loop [b 0]
                         (if (or (>= b 5) (< idx (nth cum (inc b))))
                           (nth book-names b)
                           (recur (inc b)))))]

      (println (format "  %4s  %8s  %8s  %6s  %s"
                       "Rank" "Start" "End" "Length" "Book"))
      (println (apply str (repeat 45 "─")))
      (doseq [[i {:keys [start end length]}] (map-indexed vector top-gaps)]
        (println (format "  %4d  %,8d  %,8d  %,6d  %s"
                         (inc i) start end length (which-book start))))))

  (println "\nDone. The breath is mapped."))
