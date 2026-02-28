(ns experiments.021-seven-seals
  "Open the seven seals.
   The Torah divides into 7 equal segments with palindrome cos 0.9995.
   What's INSIDE each seal? What do they contain? How do they mirror?
   Map the content, the rhythm, the hidden connections.
   Run: clojure -M:dev -m experiments.021-seven-seals"
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

(defn entropy [freqs total]
  (- (reduce + (map (fn [[_ cnt]]
                       (let [p (/ (double cnt) total)]
                         (* p (Math/log p))))
                     freqs))))

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

(defn bin-average [v n-bins]
  (let [src-n (count v)
        bin-sz (/ (double src-n) n-bins)]
    (mapv (fn [i]
            (let [start (int (* i bin-sz))
                  end   (min src-n (int (* (inc i) bin-sz)))
                  chunk (subvec v start end)]
              (if (empty? chunk) 0.0
                  (/ (reduce + (map double chunk)) (count chunk)))))
          (range n-bins))))

(defn where-in-torah
  "Given a letter position, return which book/chapter/verse it falls in."
  [pos book-boundaries]
  (loop [bs book-boundaries]
    (when (seq bs)
      (let [{:keys [book start end chapters]} (first bs)]
        (if (and (>= pos start) (< pos end))
          (let [offset (- pos start)]
            ;; Find chapter
            (loop [chs chapters cum 0]
              (when (seq chs)
                (let [{:keys [ch letters verses]} (first chs)]
                  (if (< offset (+ cum letters))
                    {:book book :chapter ch
                     :letter-in-chapter (- offset cum)}
                    (recur (rest chs) (+ cum letters)))))))
          (recur (rest bs)))))))

(defn build-book-map
  "Build detailed book/chapter/verse map with letter boundaries."
  []
  (let [result (atom [])
        pos (atom 0)]
    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (let [book-start @pos
            chapters (atom [])]
        (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
          (let [verses (sefaria/fetch-chapter book ch)
                ch-start @pos
                ch-letters (atom 0)]
            (doseq [v verses]
              (let [stripped (norm/strip-html v)
                    letters (norm/letter-stream stripped)
                    n (count letters)]
                (swap! ch-letters + n)
                (swap! pos + n)))
            (swap! chapters conj {:ch ch :letters @ch-letters :start ch-start})))
        (swap! result conj {:book book :start book-start :end @pos
                            :letters (- @pos book-start) :chapters @chapters})))
    @result))

(defn -main []
  (println "=== THE SEVEN SEALS ===")
  (println "  The Torah divided into 7 equal segments.")
  (println "  Opening each one.\n")

  (println "Loading full Torah and building map...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        seg-size (quot n 7)
        book-map (build-book-map)]

    (println (format "  %,d letters. Segment size: %,d letters.\n" n seg-size))

    ;; Build the 7 segments
    (let [segments (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end   (if (= i 6) n (* (inc i) seg-size))
                                 chunk (subvec all-letters start end)
                                 gems  (subvec gem-vals start end)]
                             {:seal (inc i) :start start :end end
                              :letters chunk :gems gems
                              :n (count chunk)}))
                         (range 7))]

      ;; ── SEAL 1: Identity ─────────────────────────────────────
      (println "╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 1: IDENTITY                     ║")
      (println "║   What IS each segment?                         ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      (println (format "  %4s  %10s  %10s  %12s  %8s  %10s  %s"
                       "Seal" "Start" "End" "Gematria" "Mean" "Σ mod 7" "Begins in"))
      (println (apply str (repeat 80 "─")))

      (doseq [seg segments]
        (let [{:keys [seal start end gems]} seg
              gem-sum (reduce + gems)
              gem-mean (/ (double gem-sum) (count gems))
              loc (where-in-torah start book-map)]
          (println (format "  %4d  %,10d  %,10d  %,12d  %8.2f  %10d  %s %d"
                           seal start end gem-sum gem-mean (mod gem-sum 7)
                           (:book loc) (:chapter loc)))))

      ;; ── SEAL 2: Frequency Signatures ─────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 2: FREQUENCY SIGNATURES         ║")
      (println "║   Each seal's letter distribution fingerprint    ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      (let [profiles (mapv #(letter-profile (:letters %)) segments)
            torah-profile (letter-profile all-letters)]

        ;; Show how each segment's profile compares to the whole
        (println (format "  %4s  %10s  %10s  %10s  %10s  %10s"
                         "Seal" "cos(Torah)" "Entropy" "Top letter" "Top freq%" "Unique"))
        (println (apply str (repeat 65 "─")))

        (doseq [i (range 7)]
          (let [seg (nth segments i)
                prof (nth profiles i)
                cos-torah (cosine-sim prof torah-profile)
                freqs (frequencies (:letters seg))
                n-letters (:n seg)
                ent (/ (entropy freqs n-letters) (Math/log 2))
                top-letter (key (apply max-key val freqs))
                top-freq (* 100 (/ (double (val (apply max-key val freqs))) n-letters))
                unique-letters (count freqs)]
            (println (format "  %4d  %10.6f  %10.4f  %10s  %9.2f%%  %10d"
                             (inc i) cos-torah ent (str top-letter) top-freq unique-letters))))

        ;; Mirror similarity matrix: does seal i look like seal 8-i?
        (println "\n  Mirror similarity matrix (cos between seal i and seal j):")
        (println (format "  %6s" "")
                 (apply str (map #(format "  %8d" (inc %)) (range 7))))
        (println (apply str (repeat 72 "─")))

        (doseq [i (range 7)]
          (print (format "  %4d  " (inc i)))
          (doseq [j (range 7)]
            (let [cos (cosine-sim (nth profiles i) (nth profiles j))]
              (print (format "  %8.4f" cos))))
          (println)))

      ;; ── SEAL 3: The Rhythm ───────────────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 3: THE RHYTHM                   ║")
      (println "║   Autocorrelation structure within each seal     ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      (println (format "  %4s  %10s  %10s  %10s  %10s  %12s"
                       "Seal" "r(1)" "r(7)" "r(22)" "r(7)/r(1)" "7-dominance"))
      (println (apply str (repeat 65 "─")))

      (doseq [seg segments]
        (let [{:keys [seal gems]} seg
              gem-arr (double-array (map double gems))
              r1  (autocorrelation-at-lag gem-arr 1)
              r7  (autocorrelation-at-lag gem-arr 7)
              r22 (autocorrelation-at-lag gem-arr 22)
              ;; Check all lags 1-49, rank lag 7
              all-lags (mapv #(vector % (autocorrelation-at-lag gem-arr %)) (range 1 50))
              sorted (sort-by (comp - second) all-lags)
              rank-7 (inc (count (take-while #(not= 7 (first %)) sorted)))]
          (println (format "  %4d  %10.6f  %10.6f  %10.6f  %10.3f  rank %d of 49"
                           seal r1 r7 r22 (/ r7 r1) rank-7))))

      ;; ── SEAL 4: Internal Palindrome ──────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 4: INTERNAL PALINDROME           ║")
      (println "║   Is each seal itself palindromic?               ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      (println (format "  %4s  %12s  %12s  %12s  %12s"
                       "Seal" "Profile cos" "Gem curve" "Letter dist" "Self-sim"))
      (println (apply str (repeat 58 "─")))

      (doseq [seg segments]
        (let [{:keys [seal letters gems n]} seg
              half (quot n 2)
              ;; Profile palindrome
              first-half (subvec letters 0 half)
              second-half-rev (vec (reverse (subvec letters half)))
              prof-a (letter-profile first-half)
              prof-b (letter-profile (subvec second-half-rev 0 (min (count second-half-rev) half)))
              prof-cos (if (and prof-a prof-b) (cosine-sim prof-a prof-b) 0.0)
              ;; Gematria curve palindrome (binned to 50)
              binned (bin-average gems 50)
              first-25 (subvec binned 0 25)
              last-25-rev (vec (reverse (subvec binned 25)))
              gem-cos (cosine-sim (mapv double first-25) (mapv double last-25-rev))
              ;; Letter frequency distribution palindrome
              ;; Compare first/second half distribution of top 5 letters
              freq-first (frequencies first-half)
              freq-second (frequencies (subvec letters half))
              total-first (double half)
              total-second (double (- n half))
              dist-a (mapv #(/ (double (get freq-first % 0)) total-first) alphabet)
              dist-b (mapv #(/ (double (get freq-second % 0)) total-second) alphabet)
              dist-cos (cosine-sim dist-a dist-b)
              ;; Self-similarity: 10% slices
              full-prof (letter-profile letters)
              slice-size (quot n 10)
              slice-sims (for [i (range 10)]
                           (let [s (subvec letters (* i slice-size)
                                            (min n (* (inc i) slice-size)))
                                 sp (letter-profile s)]
                             (when (and full-prof sp) (cosine-sim full-prof sp))))
              mean-self-sim (/ (reduce + (filter some? slice-sims))
                              (count (filter some? slice-sims)))]
          (println (format "  %4d  %12.6f  %12.6f  %12.6f  %12.6f"
                           seal prof-cos gem-cos dist-cos mean-self-sim))))

      ;; ── SEAL 5: The Mirror Pairs ─────────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 5: THE MIRROR PAIRS              ║")
      (println "║   1↔7, 2↔6, 3↔5. Center = 4. What matches?     ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      (doseq [[i j] [[0 6] [1 5] [2 4]]]
        (let [seg-a (nth segments i)
              seg-b (nth segments j)
              prof-a (letter-profile (:letters seg-a))
              prof-b (letter-profile (:letters seg-b))
              ;; Forward: compare directly
              fwd-cos (cosine-sim prof-a prof-b)
              ;; Chiastic: compare A's profile to REVERSED sequence of B
              prof-b-rev (letter-profile (vec (reverse (:letters seg-b))))
              chi-cos (cosine-sim prof-a prof-b-rev)
              ;; Gematria curve: bin each to 100, compare
              bins-a (bin-average (:gems seg-a) 100)
              bins-b (bin-average (:gems seg-b) 100)
              bins-b-rev (vec (reverse bins-b))
              gem-fwd (cosine-sim (mapv double bins-a) (mapv double bins-b))
              gem-chi (cosine-sim (mapv double bins-a) (mapv double bins-b-rev))
              ;; Word-length proxy: using letter values binned
              ;; Gematria sum comparison
              sum-a (reduce + (:gems seg-a))
              sum-b (reduce + (:gems seg-b))
              diff (Math/abs (- sum-a sum-b))
              diff-pct (* 100 (/ (double diff) (/ (+ sum-a sum-b) 2.0)))
              ;; Where do they start?
              loc-a (where-in-torah (:start seg-a) book-map)
              loc-b (where-in-torah (:start seg-b) book-map)]

          (println (format "  ── Seal %d ↔ Seal %d ──" (inc i) (inc j)))
          (println (format "    Seal %d: %s %d → %,d letters, Σgem = %,d"
                           (inc i) (:book loc-a) (:chapter loc-a) (:n seg-a) sum-a))
          (println (format "    Seal %d: %s %d → %,d letters, Σgem = %,d"
                           (inc j) (:book loc-b) (:chapter loc-b) (:n seg-b) sum-b))
          (println (format "    Gematria difference: %,d (%.2f%%)" diff diff-pct))
          (println (format "    Profile:   forward cos = %.6f   chiastic cos = %.6f  %s"
                           fwd-cos chi-cos
                           (if (> chi-cos fwd-cos) "← CHIASTIC" "")))
          (println (format "    Gem curve: forward cos = %.6f   chiastic cos = %.6f  %s"
                           gem-fwd gem-chi
                           (if (> gem-chi gem-fwd) "← CHIASTIC" "")))
          (println)))

      ;; The center seal
      (let [center-seg (nth segments 3)
            loc (where-in-torah (:start center-seg) book-map)
            center-letter (nth (:letters center-seg) (quot (:n center-seg) 2))
            gems (:gems center-seg)
            gem-sum (reduce + gems)
            prof (letter-profile (:letters center-seg))
            torah-prof (letter-profile all-letters)]
        (println "  ── SEAL 4: THE CENTER ──")
        (println (format "    Location: %s %d to ..." (:book loc) (:chapter loc)))
        (println (format "    Letters: %,d" (:n center-seg)))
        (println (format "    Gematria sum: %,d" gem-sum))
        (println (format "    Σ mod 7: %d   Σ mod 22: %d   Σ mod 26: %d"
                         (mod gem-sum 7) (mod gem-sum 22) (mod gem-sum 26)))
        (println (format "    Center letter of center seal: %c (value %d)"
                         center-letter (g/letter-value center-letter)))
        (println (format "    cos(Torah): %.6f" (cosine-sim prof torah-prof)))
        ;; Is the center seal more palindromic than average?
        (let [half (quot (:n center-seg) 2)
              pa (letter-profile (subvec (:letters center-seg) 0 half))
              pb (letter-profile (vec (reverse (subvec (:letters center-seg) half))))
              pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
          (println (format "    Internal palindrome: %.6f" pal))))

      ;; ── SEAL 6: The Lag-7 Web ────────────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 6: THE LAG-7 WEB                ║")
      (println "║   Every 7th letter forms a sub-sequence.         ║")
      (println "║   7 interleaved threads through the Torah.       ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      ;; Extract the 7 threads: offset 0,1,2,3,4,5,6
      (let [threads (mapv (fn [offset]
                            (let [indices (range offset n 7)
                                  thread-letters (mapv #(nth all-letters %) indices)
                                  thread-gems (mapv #(nth gem-vals %) indices)]
                              {:offset offset
                               :letters thread-letters
                               :gems thread-gems
                               :n (count thread-letters)}))
                          (range 7))]

        (println (format "  %6s  %10s  %12s  %8s  %10s  %10s"
                         "Thread" "Letters" "Gematria" "Mean" "cos(Torah)" "Entropy"))
        (println (apply str (repeat 68 "─")))

        (doseq [t threads]
          (let [{:keys [offset letters gems n]} t
                prof (letter-profile letters)
                torah-prof (letter-profile all-letters)
                cos-torah (if (and prof torah-prof) (cosine-sim prof torah-prof) 0.0)
                freqs (frequencies letters)
                ent (/ (entropy freqs n) (Math/log 2))
                gem-sum (reduce + gems)
                gem-mean (/ (double gem-sum) n)]
            (println (format "  %6d  %,10d  %,12d  %8.2f  %10.6f  %10.4f"
                             offset n gem-sum gem-mean cos-torah ent))))

        ;; Are any threads more special than others?
        (println "\n  Thread-to-thread similarity matrix:")
        (let [profs (mapv #(letter-profile (:letters %)) threads)]
          (print (format "  %6s" ""))
          (doseq [j (range 7)] (print (format "  %8d" j)))
          (println)
          (println (apply str (repeat 68 "─")))
          (doseq [i (range 7)]
            (print (format "  %6d" i))
            (doseq [j (range 7)]
              (print (format "  %8.4f" (cosine-sim (nth profs i) (nth profs j)))))
            (println)))

        ;; Cross-thread palindrome: thread 0 vs rev thread 6, etc.
        (println "\n  Cross-thread mirror (thread i vs reversed thread 6-i):")
        (doseq [i (range 3)]
          (let [j (- 6 i)
                ta (nth threads i)
                tb (nth threads j)
                ;; Bin each to 200 and compare
                bins-a (bin-average (:gems ta) 200)
                bins-b (bin-average (:gems tb) 200)
                bins-b-rev (vec (reverse bins-b))
                fwd-cos (cosine-sim (mapv double bins-a) (mapv double bins-b))
                chi-cos (cosine-sim (mapv double bins-a) (mapv double bins-b-rev))]
            (println (format "    Thread %d ↔ Thread %d:  forward=%.4f  chiastic=%.4f  %s"
                             i j fwd-cos chi-cos
                             (if (> chi-cos fwd-cos) "← CHIASTIC" "")))))
        (println (format "    Thread 3 (center): %,d letters, Σgem = %,d, Σ mod 7 = %d"
                         (:n (nth threads 3))
                         (reduce + (:gems (nth threads 3)))
                         (mod (reduce + (:gems (nth threads 3))) 7))))

      ;; ── SEAL 7: The Hidden Name ──────────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SEAL 7: THE HIDDEN NAME               ║")
      (println "║   What word forms at every 7th seal boundary?    ║")
      (println "║   What letters sit at the borders?               ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      ;; Letters at each boundary
      (println "  Letters at seal boundaries (±3 letters):")
      (doseq [i (range 8)]
        (let [pos (if (= i 7) (dec n) (* i seg-size))
              safe-start (max 0 (- pos 3))
              safe-end (min n (+ pos 4))
              window (subvec all-letters safe-start safe-end)
              window-gems (mapv #(long (g/letter-value %)) window)
              loc (where-in-torah (min pos (dec n)) book-map)]
          (println (format "    Boundary %d (pos %,d = %s %d): ...%s... gems=%s sum=%d"
                           i pos
                           (if loc (:book loc) "?")
                           (if loc (:chapter loc) 0)
                           (apply str window)
                           (str window-gems)
                           (reduce + window-gems)))))

      ;; The 7 boundary letters
      (println "\n  The 7 boundary letters (first letter of each seal):")
      (let [boundary-letters (mapv (fn [i] (nth all-letters (* i seg-size))) (range 7))
            boundary-word (apply str boundary-letters)
            boundary-gems (mapv #(long (g/letter-value %)) boundary-letters)
            boundary-sum (reduce + boundary-gems)]
        (println (format "    Letters: %s" boundary-word))
        (println (format "    Values:  %s" (str boundary-gems)))
        (println (format "    Sum:     %d" boundary-sum))
        (println (format "    Sum mod 7: %d   mod 22: %d" (mod boundary-sum 7) (mod boundary-sum 22))))

      ;; The center letter of each seal
      (println "\n  Center letter of each seal:")
      (let [center-letters (mapv (fn [seg]
                                    (nth (:letters seg) (quot (:n seg) 2)))
                                  segments)
            center-word (apply str center-letters)
            center-gems (mapv #(long (g/letter-value %)) center-letters)
            center-sum (reduce + center-gems)]
        (println (format "    Letters: %s" center-word))
        (println (format "    Values:  %s" (str center-gems)))
        (println (format "    Sum:     %d" center-sum))
        (println (format "    Sum mod 7: %d   mod 22: %d" (mod center-sum 7) (mod center-sum 22))))

      ;; Last letter of each seal
      (println "\n  Last letter of each seal:")
      (let [last-letters (mapv (fn [seg]
                                  (last (:letters seg)))
                                segments)
            last-word (apply str last-letters)
            last-gems (mapv #(long (g/letter-value %)) last-letters)
            last-sum (reduce + last-gems)]
        (println (format "    Letters: %s" last-word))
        (println (format "    Values:  %s" (str last-gems)))
        (println (format "    Sum:     %d" last-sum))
        (println (format "    Sum mod 7: %d   mod 22: %d" (mod last-sum 7) (mod last-sum 22))))

      ;; ── SYNTHESIS ────────────────────────────────────────────
      (println "\n╔══════════════════════════════════════════════════╗")
      (println "║            SYNTHESIS                             ║")
      (println "╚══════════════════════════════════════════════════╝\n")

      ;; The 7 gematria sums — do they encode something?
      (let [sums (mapv (fn [seg] (reduce + (:gems seg))) segments)
            total (reduce + sums)]
        (println (format "  Gematria sums: %s" (str sums)))
        (println (format "  Total: %,d" total))
        (println (format "  Total mod 7: %d" (mod total 7)))
        (println (format "  Total mod 49: %d" (mod total 49)))
        (println (format "  Total / 7 = %,d remainder %d" (quot total 7) (mod total 7)))

        ;; Ratios between adjacent seals
        (println "\n  Ratios between adjacent seals:")
        (doseq [i (range 6)]
          (let [ratio (/ (double (nth sums i)) (nth sums (inc i)))]
            (println (format "    Seal %d / Seal %d = %.6f  %s"
                             (inc i) (+ i 2) ratio
                             (cond
                               (< (Math/abs (- ratio 1.0)) 0.01) "≈ 1 (equal)"
                               (< (Math/abs (- ratio (/ (Math/sqrt 5) 2))) 0.1) (format "≈ √5/2 (err %.4f)" (Math/abs (- ratio (/ (Math/sqrt 5) 2))))
                               (< (Math/abs (- ratio (Math/sqrt 2))) 0.1) (format "≈ √2 (err %.4f)" (Math/abs (- ratio (Math/sqrt 2))))
                               :else "")))))

        ;; Mirror sum symmetry
        (println "\n  Mirror sum symmetry:")
        (doseq [i (range 3)]
          (let [j (- 6 i)
                diff (Math/abs (- (long (nth sums i)) (long (nth sums j))))
                pct (* 100 (/ (double diff) (/ (+ (double (nth sums i)) (nth sums j)) 2.0)))]
            (println (format "    Seal %d + Seal %d = %,d   |diff| = %,d (%.3f%%)"
                             (inc i) (inc j) (+ (long (nth sums i)) (long (nth sums j)))
                             diff pct))))
        (println (format "    Seal 4 (center) = %,d" (nth sums 3)))
        (let [outer-sum (+ (long (nth sums 0)) (long (nth sums 6)))
              inner-sum (+ (long (nth sums 1)) (long (nth sums 5)))
              mid-sum   (+ (long (nth sums 2)) (long (nth sums 4)))
              center    (nth sums 3)]
          (println (format "    Outer pairs sum: %,d" outer-sum))
          (println (format "    Inner pairs sum: %,d" inner-sum))
          (println (format "    Mid pairs sum:   %,d" mid-sum))
          (println (format "    Center:          %,d" center))
          (println (format "    Ratio outer/center: %.4f" (/ (double outer-sum) center)))
          (println (format "    Ratio inner/center: %.4f" (/ (double inner-sum) center)))
          (println (format "    Ratio mid/center:   %.4f" (/ (double mid-sum) center)))))))

  (println "\nDone. The seals are open."))
