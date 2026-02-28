(ns experiments.027-spectrum
  "Spectral analysis of the Torah's gematria stream.
   The FFT should reveal the hidden frequencies.
   If the Torah breathes in sevens, we'll see the peak.
   Run: clojure -M:dev -m experiments.027-spectrum"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

;; Simple radix-2 DIT FFT (power-of-2 input required)
(defn fft
  "Compute FFT of a double array. Returns array of [real imag] pairs.
   Input length must be a power of 2."
  [^doubles x]
  (let [n (alength x)]
    (if (= n 1)
      [[( aget x 0) 0.0]]
      (let [half (quot n 2)
            even-arr (double-array half)
            odd-arr (double-array half)
            _ (dotimes [i half]
                (aset even-arr i (aget x (* 2 i)))
                (aset odd-arr i (aget x (+ 1 (* 2 i)))))
            even-fft (fft even-arr)
            odd-fft (fft odd-arr)
            result (object-array n)]
        (dotimes [k half]
          (let [angle (/ (* -2.0 Math/PI k) n)
                wr (Math/cos angle)
                wi (Math/sin angle)
                [or_ oi] (nth odd-fft k)
                tr (- (* wr or_) (* wi oi))
                ti (+ (* wr oi) (* wi or_))
                [er ei] (nth even-fft k)]
            (aset result k [(+ er tr) (+ ei ti)])
            (aset result (+ k half) [(- er tr) (- ei ti)])))
        (vec result)))))

(defn magnitude [[r i]]
  (Math/sqrt (+ (* r r) (* i i))))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn next-power-of-2 [n]
  (loop [p 1]
    (if (>= p n) p (recur (* 2 p)))))

(defn -main []
  (println "=== SPECTRUM ===")
  (println "  Spectral analysis of the gematria stream.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        mean-gem (/ (double (reduce + gem-vals)) n)]

    (println (format "  %,d letters. Mean gematria: %.2f\n" n mean-gem))

    ;; ── 1. Binned FFT ──────────────────────────────────────
    ;; Bin the gematria values to a power-of-2 length for FFT
    (println "── 1. Binned Gematria Spectrum ──")
    (println "  Bin to 8192 (2¹³) points, then FFT.\n")

    (let [n-bins 8192  ;; 2^13
          bin-size (/ (double n) n-bins)
          binned (double-array n-bins)]
      ;; Average gematria in each bin, subtract mean
      (dotimes [i n-bins]
        (let [start (int (* i bin-size))
              end   (min n (int (* (inc i) bin-size)))
              chunk (subvec gem-vals start end)
              avg (if (empty? chunk) 0.0
                      (- (/ (reduce + (map double chunk)) (count chunk)) mean-gem))]
          (aset binned i avg)))

      (println "  Computing FFT...")
      (let [spectrum (fft binned)
            mags (mapv magnitude spectrum)
            ;; Only first half is unique (Nyquist)
            half-n (quot n-bins 2)
            freq-mags (mapv (fn [k] {:freq k
                                      :period (if (pos? k) (/ (double n-bins) k) ##Inf)
                                      :mag (nth mags k)})
                            (range 1 half-n))]

        ;; Top 20 peaks
        (println "\n  Top 20 frequency peaks:")
        (println (format "  %6s  %10s  %12s  %8s  %s"
                         "Freq" "Period" "Magnitude" "bins" "Nearest harmonic"))
        (println (apply str (repeat 60 "─")))

        (let [sorted-peaks (take 20 (sort-by (comp - :mag) freq-mags))]
          (doseq [{:keys [freq period mag]} sorted-peaks]
            (let [;; Convert period from bins to letters
                  letters-per-period (* period bin-size)
                  ;; Check for harmonics of key numbers
                  nearest (cond
                            (< (Math/abs (- (/ (double n-bins) freq) 7)) 1) "~7"
                            (< (Math/abs (- (/ (double n-bins) freq) 37)) 2) "~37"
                            (< (Math/abs (- (/ (double n-bins) freq) 49)) 3) "~49"
                            (< (Math/abs (- (/ (double n-bins) freq) 73)) 3) "~73"
                            (< (Math/abs (- (/ (double n-bins) freq) 5)) 1) "~5 (books!)"
                            :else "")]
              (println (format "  %6d  %10.1f  %12.1f  %,8.0f  %s"
                               freq period mag letters-per-period nearest)))))

        ;; ── 2. Specific frequencies ─────────────────────────
        (println "\n── 2. Energy at Key Frequencies ──")
        (println "  What's the spectral power at specific periods?\n")

        (let [key-periods [{:name "~5 books" :bins (/ n-bins 5.0)}
                           {:name "~7 seals" :bins (/ n-bins 7.0)}
                           {:name "~37" :bins (/ n-bins 37.0)}
                           {:name "~49" :bins (/ n-bins 49.0)}
                           {:name "~73" :bins (/ n-bins 73.0)}
                           {:name "~187 ch" :bins (/ n-bins 187.0)}
                           {:name "~343" :bins (/ n-bins 343.0)}
                           {:name "~441" :bins (/ n-bins 441.0)}]]
          (println (format "  %12s  %8s  %12s  %12s"
                           "Period" "Freq bin" "Magnitude" "Power"))
          (println (apply str (repeat 52 "─")))
          (doseq [{:keys [name bins]} key-periods]
            (let [k (int (Math/round bins))
                  ;; Average over nearby bins for smoother estimate
                  nearby (range (max 1 (- k 1)) (min half-n (+ k 2)))
                  avg-mag (/ (reduce + (map #(nth mags %) nearby)) (count nearby))
                  power (* avg-mag avg-mag)]
              (println (format "  %12s  %8d  %12.1f  %12.1f"
                               name k avg-mag power)))))

        ;; ── 3. Power spectrum bands ─────────────────────────
        (println "\n── 3. Power by Frequency Band ──")
        (println "  How is the spectral energy distributed?\n")

        (let [bands [{:name "DC (period > 4096)" :lo 1 :hi 2}
                     {:name "Very low (1024-4096)" :lo 2 :hi 8}
                     {:name "Low (256-1024)" :lo 8 :hi 32}
                     {:name "Mid-low (64-256)" :lo 32 :hi 128}
                     {:name "Mid (16-64)" :lo 128 :hi 512}
                     {:name "Mid-high (4-16)" :lo 512 :hi 2048}
                     {:name "High (2-4)" :lo 2048 :hi half-n}]
              total-power (reduce + (map #(* % %) (subvec mags 1 half-n)))]
          (println (format "  %30s  %12s  %8s" "Band" "Power" "% total"))
          (println (apply str (repeat 55 "─")))
          (doseq [{:keys [name lo hi]} bands]
            (let [band-power (reduce + (map #(* (nth mags %) (nth mags %))
                                            (range lo (min hi half-n))))]
              (println (format "  %30s  %12.0f  %7.2f%%"
                               name band-power
                               (* 100 (/ band-power total-power)))))))

        ;; ── 4. Is the spectrum palindromic? ─────────────────
        (println "\n── 4. Spectral Palindrome ──")
        (println "  Is the power spectrum itself palindromic?")
        (println "  Compare low-frequency shape to high-frequency shape.\n")

        (let [;; Take the magnitude spectrum and test palindrome
              useful-mags (subvec mags 1 (min 2048 half-n))
              n-useful (count useful-mags)
              half-useful (quot n-useful 2)
              first-mags (mapv double (subvec useful-mags 0 half-useful))
              last-mags-rev (mapv double (vec (reverse (subvec useful-mags half-useful))))]
          (println (format "  Spectrum palindrome (first %d vs rev last %d): cos = %.4f"
                           half-useful half-useful
                           (cosine-sim first-mags last-mags-rev))))

        ;; ── 5. Shuffled control ─────────────────────────────
        (println "\n── 5. Shuffled Control ──")
        (println "  Compare spectrum to shuffled Torah.\n")

        (let [shuffled (double-array n-bins)
              shuffled-gems (vec (shuffle gem-vals))
              _ (dotimes [i n-bins]
                  (let [start (int (* i bin-size))
                        end   (min n (int (* (inc i) bin-size)))
                        chunk (subvec shuffled-gems start end)
                        avg (if (empty? chunk) 0.0
                                (- (/ (reduce + (map double chunk)) (count chunk)) mean-gem))]
                    (aset shuffled i avg)))
              shuf-spectrum (fft shuffled)
              shuf-mags (mapv magnitude shuf-spectrum)]

          ;; Compare at key frequencies
          (println (format "  %12s  %12s  %12s  %8s"
                           "Period" "Torah mag" "Shuffled mag" "Ratio"))
          (println (apply str (repeat 50 "─")))
          (doseq [[name freq] [["~5 books" (int (/ n-bins 5.0))]
                                ["~7 seals" (int (/ n-bins 7.0))]
                                ["~37" (int (/ n-bins 37.0))]
                                ["~49" (int (/ n-bins 49.0))]]]
            (let [torah-mag (nth mags freq)
                  shuf-mag (nth shuf-mags freq)
                  ratio (if (pos? shuf-mag) (/ torah-mag shuf-mag) ##Inf)]
              (println (format "  %12s  %12.1f  %12.1f  %8.2fx"
                               name torah-mag shuf-mag ratio))))

          ;; Overall spectral flatness
          (let [torah-peak (apply max (subvec mags 1 half-n))
                torah-mean-mag (/ (reduce + (subvec mags 1 half-n)) (dec half-n))
                shuf-peak (apply max (subvec shuf-mags 1 half-n))
                shuf-mean-mag (/ (reduce + (subvec shuf-mags 1 half-n)) (dec half-n))]
            (println (format "\n  Torah: peak/mean = %.2f (concentrated energy)"
                             (/ torah-peak torah-mean-mag)))
            (println (format "  Shuffled: peak/mean = %.2f (flat)"
                             (/ shuf-peak shuf-mean-mag)))))))

  (println "\nDone. The frequencies are revealed.")))
