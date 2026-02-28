(ns experiments.023-harmonics
  "The harmonic series: 7, 49, 343.
   If the Torah breathes in sevens, what are the overtones?
   What happens at every 7th letter? Every 49th? Every 343rd?
   Run: clojure -M:dev -m experiments.023-harmonics"
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

(defn autocorrelation-at-lag [^doubles arr lag]
  (let [n (alength arr)
        m (- n lag)
        sum (areduce arr i ret 0.0 (+ ret (aget arr i)))
        mean (/ sum n)
        var-sum (areduce arr i ret 0.0
                         (let [d (- (aget arr i) mean)]
                           (+ ret (* d d))))
        cov (loop [i 0 acc 0.0]
              (if (>= i m)
                acc
                (recur (inc i)
                       (+ acc (* (- (aget arr i) mean)
                                 (- (aget arr (+ i lag)) mean))))))]
    (/ cov var-sum)))

(defn -main []
  (println "=== HARMONICS ===")
  (println "  The overtone series of seven.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        gem-arr (double-array (map double gem-vals))]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. The Harmonic Autocorrelation Spectrum ────────────
    (println "── 1. Autocorrelation at Powers of 7 ──")
    (println "  r(7), r(49), r(343), r(2401)...\n")

    (let [powers [7 49 343 2401 16807]
          r-at (mapv #(vector % (autocorrelation-at-lag gem-arr %)) powers)]
      (println (format "  %8s  %12s  %s" "Lag" "r(lag)" "Relative to r(7)"))
      (println (apply str (repeat 40 "─")))
      (let [r7 (second (first r-at))]
        (doseq [[lag r] r-at]
          (println (format "  %8d  %12.6f  %.4fx"
                           lag r (/ r r7))))))

    ;; Compare to nearby lags
    (println "\n  Neighborhood of each harmonic:")
    (doseq [center [7 49 343]]
      (println (format "\n  Around lag %d:" center))
      (doseq [lag (range (- center 3) (+ center 4))]
        (when (pos? lag)
          (let [r (autocorrelation-at-lag gem-arr lag)]
            (println (format "    r(%d) = %+.6f %s"
                             lag r
                             (if (= lag center) " ← HARMONIC" "")))))))

    ;; ── 2. Sub-sequences at each harmonic ───────────────────
    (println "\n── 2. Sub-sequences at Each Harmonic ──")
    (println "  Every Kth letter, for K = 7, 49, 343.")
    (println "  What do they look like?\n")

    (doseq [k [7 49 343]]
      (let [sub-letters (mapv #(nth all-letters %) (range 0 n k))
            sub-gems (mapv #(long (g/letter-value %)) sub-letters)
            sub-n (count sub-letters)
            prof (letter-profile sub-letters)
            torah-prof (letter-profile all-letters)
            cos-torah (cosine-sim prof torah-prof)
            gem-sum (reduce + sub-gems)
            ;; Is this subsequence palindromic?
            half (quot sub-n 2)
            first-h (letter-profile (subvec sub-letters 0 half))
            second-h-rev (letter-profile (vec (reverse (subvec sub-letters half))))
            pal-cos (if (and first-h second-h-rev) (cosine-sim first-h second-h-rev) 0.0)
            ;; Top 3 most frequent letters
            freqs (sort-by (comp - val) (frequencies sub-letters))
            top3 (take 3 freqs)]
        (println (format "  Every %dth letter (n=%,d):" k sub-n))
        (println (format "    cos(Torah): %.6f" cos-torah))
        (println (format "    Σ gematria: %,d  mod 7: %d  mod 49: %d"
                         gem-sum (mod gem-sum 7) (mod gem-sum 49)))
        (println (format "    Internal palindrome: %.4f" pal-cos))
        (println (format "    Top 3 letters: %s"
                         (str/join ", " (map (fn [[c cnt]]
                                               (format "%s (%d, %.1f%%)" c cnt
                                                       (* 100 (/ (double cnt) sub-n))))
                                             top3))))
        (println)))

    ;; ── 3. The Seven Phases at Higher Harmonics ─────────────
    (println "── 3. Seven Phases at Higher Harmonics ──")
    (println "  For K=49: there are 49 phase offsets.")
    (println "  Group them by (offset mod 7). Do the 7 groups differ?\n")

    (let [k 49
          ;; 49 sub-sequences, group by offset mod 7
          groups (mapv (fn [g]
                         (let [offsets (filter #(= (mod % 7) g) (range 49))
                               ;; Combine all letters from these offsets
                               combined (vec (mapcat (fn [off]
                                                       (mapv #(nth all-letters %)
                                                             (range off n k)))
                                                     offsets))
                               gem-sum (reduce + (map #(long (g/letter-value %)) combined))]
                           {:group g :offsets offsets :n (count combined)
                            :gem-sum gem-sum
                            :profile (letter-profile combined)}))
                       (range 7))]

      (println (format "  %6s  %10s  %12s  %8s  %10s"
                       "Group" "Letters" "Gematria" "Mean" "cos(Torah)"))
      (println (apply str (repeat 55 "─")))

      (doseq [g groups]
        (println (format "  %6d  %,10d  %,12d  %8.2f  %10.6f"
                         (:group g) (:n g) (:gem-sum g)
                         (/ (double (:gem-sum g)) (:n g))
                         (cosine-sim (:profile g) (letter-profile all-letters)))))

      ;; Mirror symmetry
      (println "\n  Group mirror sums:")
      (let [sums (mapv :gem-sum groups)]
        (doseq [i (range 3)]
          (let [j (- 6 i)
                diff (Math/abs (- (long (nth sums i)) (long (nth sums j))))]
            (println (format "    Group %d + Group %d: %,d + %,d  |diff| = %,d (%.3f%%)"
                             i j (nth sums i) (nth sums j) diff
                             (* 100 (/ (double diff)
                                       (/ (+ (double (nth sums i)) (nth sums j)) 2.0)))))))
        (println (format "    Group 3 (center): %,d" (nth sums 3)))))

    ;; ── 4. Fractal dimension of the seven-fold structure ────
    (println "\n── 4. Scale Invariance ──")
    (println "  Test the 7-fold palindrome at different scales.")
    (println "  Divide Torah into 7k segments, test palindrome.\n")

    (println (format "  %8s  %8s  %10s  %s" "Segments" "Per seg" "Palindrome" "Notes"))
    (println (apply str (repeat 50 "─")))

    (doseq [mult [1 2 3 4 5 7 10 14 49]]
      (let [n-segs (* 7 mult)
            seg-size (quot n n-segs)]
        (when (and (pos? seg-size) (>= (quot n 2) n-segs))
          (let [seg-sums (mapv (fn [i]
                                 (let [start (* i seg-size)
                                       end (min n (* (inc i) seg-size))]
                                   (double (reduce + (subvec gem-vals start end)))))
                               (range n-segs))
                ;; Group into 7 super-segments of mult segments each
                super-sums (mapv (fn [g]
                                   (reduce + (subvec seg-sums (* g mult)
                                                     (min n-segs (* (inc g) mult)))))
                                 (range 7))
                first3 (mapv double (subvec super-sums 0 3))
                last3 (mapv double (vec (reverse (subvec super-sums 4 7))))
                cos-pal (cosine-sim first3 last3)]
            (println (format "  %8d  %,8d  %10.6f  7 × %d = %d segments"
                             n-segs seg-size cos-pal mult n-segs))))))

    ;; ── 5. Every 7th verse ──────────────────────────────────
    (println "\n── 5. Every 7th Verse ──")
    (println "  Extract every 7th verse from the Torah.")
    (println "  Does this sub-Torah carry the same structure?\n")

    (let [all-verses (atom [])
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [[idx v] (map-indexed vector verses)]
                    (swap! all-verses conj
                           {:book book :chapter ch :verse (inc idx)
                            :text v})))))
          verses @all-verses
          n-verses (count verses)]

      (println (format "  Total verses: %,d" n-verses))

      ;; Every 7th verse
      (let [sub-verses (mapv #(nth verses %) (range 0 n-verses 7))
            sub-letters (vec (mapcat (fn [v]
                                      (norm/letter-stream (norm/strip-html (:text v))))
                                    sub-verses))
            sub-gems (mapv #(long (g/letter-value %)) sub-letters)
            sub-n (count sub-letters)]

        (println (format "  Every 7th verse: %,d verses, %,d letters" (count sub-verses) sub-n))

        ;; 5-part split
        (let [seg5 (quot sub-n 5)
              segs (mapv (fn [i]
                           (let [start (* i seg5)
                                 end (if (= i 4) sub-n (* (inc i) seg5))]
                             (reduce + (subvec sub-gems start end))))
                         (range 5))
              [s1 s2 s3 s4 s5] (mapv double segs)
              ae-ratio (/ s1 s5)
              twin-diff (Math/abs (- s2 s4))
              split-ratio (/ (+ s1 s2 s3) (+ s4 s5))]
          (println (format "    5-part sums: %s" (str segs)))
          (println (format "    Seg1/Seg5 = %.4f  (√2 = 1.4142, err = %.4f)"
                           ae-ratio (Math/abs (- ae-ratio (Math/sqrt 2)))))
          (println (format "    |Seg2-Seg4| = %,.0f" twin-diff))
          (println (format "    (1+2+3)/(4+5) = %.4f  (π/2 = 1.5708, err = %.4f)"
                           split-ratio (Math/abs (- split-ratio (/ Math/PI 2)))))))

      ;; Every 49th verse
      (let [sub49 (mapv #(nth verses %) (range 0 n-verses 49))
            sub49-letters (vec (mapcat (fn [v]
                                        (norm/letter-stream (norm/strip-html (:text v))))
                                      sub49))
            sub49-gems (mapv #(long (g/letter-value %)) sub49-letters)]
        (println (format "\n  Every 49th verse: %,d verses, %,d letters"
                         (count sub49) (count sub49-letters)))
        (let [gem-sum (reduce + sub49-gems)]
          (println (format "    Σ gematria: %,d  mod 7: %d  mod 49: %d"
                           gem-sum (mod gem-sum 7) (mod gem-sum 49))))))

    ;; ── 6. The 7-letter words ───────────────────────────────
    (println "\n── 6. Words of Length 7 ──")
    (println "  How many 7-letter words are there? What do they encode?\n")

    (let [all-words (atom [])
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [v verses]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"[\s\u05BE]+")
                          words (->> raw-words
                                     (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                                     (filter #(pos? (count %))))]
                      (doseq [w words]
                        (swap! all-words conj w)))))))
          words @all-words
          n-words (count words)
          seven-letter (filterv #(= 7 (count %)) words)
          n-seven (count seven-letter)]

      (println (format "  Total words: %,d" n-words))
      (println (format "  7-letter words: %,d (%.2f%%)" n-seven
                       (* 100 (/ (double n-seven) n-words))))

      ;; Gematria sum of all 7-letter words
      (let [gem-sum (reduce + (map g/word-value seven-letter))]
        (println (format "  Σ gematria of 7-letter words: %,d" gem-sum))
        (println (format "  mod 7: %d  mod 49: %d" (mod gem-sum 7) (mod gem-sum 49))))

      ;; Most common 7-letter words
      (let [freq-7 (sort-by (comp - val) (frequencies seven-letter))]
        (println "\n  Top 10 most common 7-letter words:")
        (doseq [[w cnt] (take 10 freq-7)]
          (println (format "    %s  (%d) = %d  ×%d"
                           w (count w) (g/word-value w) cnt))))

      ;; Position of 7-letter words: first half vs second half
      (let [positions (keep-indexed (fn [i w] (when (= 7 (count w)) i)) words)
            half (quot n-words 2)
            first-half (count (filter #(< % half) positions))
            second-half (count (filter #(>= % half) positions))]
        (println (format "\n  Distribution: first half = %,d, second half = %,d"
                         first-half second-half))
        (println (format "  Ratio: %.4f" (/ (double first-half) second-half)))))

    ;; ── 7. The 7 Names of God ───────────────────────────────
    (println "\n── 7. The Divine Name and Sevens ──")
    (println "  The Tetragrammaton (יהוה) = 26 = 2 + 6 = 8.")
    (println "  But 26 mod 7 = 5. What about in the letter stream?\n")

    ;; Find every occurrence of the 4-letter sequence יהוה
    (let [target [\י \ה \ו \ה]
          n-target (count target)
          occurrences (atom [])
          _ (doseq [i (range (- n (dec n-target)))]
              (when (= (subvec all-letters i (+ i n-target)) target)
                (swap! occurrences conj i)))
          occ @occurrences
          n-occ (count occ)]

      (println (format "  Occurrences of יהוה in letter stream: %,d" n-occ))
      (println (format "  %,d mod 7: %d" n-occ (mod n-occ 7)))

      ;; Positions mod 7
      (let [mod7-dist (frequencies (map #(mod % 7) occ))]
        (println "\n  Position mod 7 distribution:")
        (doseq [i (range 7)]
          (let [cnt (get mod7-dist i 0)]
            (println (format "    mod 7 = %d: %,d (%.1f%%)"
                             i cnt (* 100 (/ (double cnt) n-occ)))))))

      ;; Spacing between occurrences
      (when (> n-occ 1)
        (let [gaps (mapv - (rest occ) (butlast occ))
              mean-gap (/ (double (reduce + gaps)) (count gaps))
              mod7-gaps (frequencies (map #(mod % 7) gaps))]
          (println (format "\n  Mean spacing: %.1f letters" mean-gap))
          (println "  Gap mod 7 distribution:")
          (doseq [i (range 7)]
            (let [cnt (get mod7-gaps i 0)]
              (println (format "    gap mod 7 = %d: %,d (%.1f%%)"
                               i cnt (* 100 (/ (double cnt) (count gaps)))))))))))

  (println "\nDone. The harmonics resonate."))
