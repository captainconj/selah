(ns experiments.008-autocorrelation
  "H14: Autocorrelation of the gematria stream at symbolic lags.
   Does the Torah correlate with itself at lags 7, 26, 50?
   Run: clojure -M:dev -m experiments.008-autocorrelation"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn autocorrelation
  "Compute normalized autocorrelation at lag k for a double array.
   Returns a value between -1 and 1."
  [^doubles arr n lag]
  (let [mean (/ (areduce arr i ret 0.0 (+ ret (aget arr i))) n)
        ;; Variance
        var  (/ (areduce arr i ret 0.0
                         (let [d (- (aget arr i) mean)]
                           (+ ret (* d d))))
                n)]
    (if (zero? var)
      0.0
      (let [;; Cross-correlation at lag
            sum (loop [i 0 acc 0.0]
                  (if (>= i (- n lag))
                    acc
                    (recur (inc i)
                           (+ acc (* (- (aget arr i) mean)
                                     (- (aget arr (+ i lag)) mean))))))]
        (/ sum (* n var))))))

(defn -main []
  (println "Loading Torah...")
  (let [torah  (vec (mapcat sefaria/book-letters book-names))
        n      (count torah)
        _      (println (format "  %,d letters" n))
        gvals  (g/stream-values torah)
        ;; Convert to double array for speed
        arr    (double-array (map double gvals))]

    ;; ── Autocorrelation at specific symbolic lags ────────────
    (println "\n=== Autocorrelation at Symbolic Lags ===")
    (println (format "  %6s  %10s  %s" "Lag" "r(lag)" "Significance"))
    (println (apply str (repeat 40 "-")))

    (let [symbolic-lags [1 2 3 4 5 6 7 8 9 10 11 12 13
                         22 26 42 49 50 51
                         72 100
                         ;; Book boundaries
                         248
                         ;; Popular ELS skips
                         ;; Fibonacci
                         21 34 55 89 144 233 377]
          lag-values (mapv (fn [lag]
                            (let [r (autocorrelation arr n lag)]
                              {:lag lag :r r}))
                          (sort symbolic-lags))]
      (doseq [{:keys [lag r]} lag-values]
        (let [note (cond
                     (= lag 7)   "← 7 (days, completion)"
                     (= lag 12)  "← 12 (tribes, partition)"
                     (= lag 22)  "← 22 (Hebrew letters)"
                     (= lag 26)  "← 26 (YHWH gematria)"
                     (= lag 42)  "← 42 (lines per column)"
                     (= lag 49)  "← 49 (7×7)"
                     (= lag 50)  "← 50 (Tabernacle, ELS skip)"
                     (= lag 72)  "← 72 (names of God)"
                     (= lag 100) "← 100 (Tabernacle length)"
                     (= lag 248) "← 248 (scroll columns)"
                     :else       "")]
          (println (format "  %6d  %10.6f  %s" lag r note)))))

    ;; ── Scan for peaks ───────────────────────────────────────
    (println "\n=== Autocorrelation Scan (lags 1-500) — Top 20 peaks ===")
    (let [lags (range 1 501)
          values (mapv (fn [lag] {:lag lag :r (autocorrelation arr n lag)}) lags)
          sorted (reverse (sort-by :r values))]
      (println (format "  %6s  %10s" "Lag" "r(lag)"))
      (println (apply str (repeat 20 "-")))
      (doseq [{:keys [lag r]} (take 20 sorted)]
        (println (format "  %6d  %10.6f" lag r))))

    ;; ── Check: is lag-1 autocorrelation just linguistic? ─────
    ;; Compare Torah to shuffled control
    (println "\n=== Control: Shuffled Torah ===")
    (let [shuffled (double-array (shuffle (vec gvals)))
          r-shuf  (autocorrelation shuffled n 1)
          r-torah (autocorrelation arr n 1)]
      (println (format "  Torah r(1):    %.6f" r-torah))
      (println (format "  Shuffled r(1): %.6f" r-shuf))
      (println (format "  Ratio: %.2f" (/ r-torah (Math/abs r-shuf)))))

    ;; ── Per-book autocorrelation at lag 7 ────────────────────
    (println "\n=== Per-Book Autocorrelation at Lag 7 ===")
    (doseq [book book-names]
      (let [letters (sefaria/book-letters book)
            vals    (g/stream-values letters)
            a       (double-array (map double vals))
            bn      (count letters)
            r7      (autocorrelation a bn 7)]
        (println (format "  %-12s: r(7) = %.6f" book r7))))

    ;; ── Autocorrelation difference spectrum ──────────────────
    ;; Compare r(lag) for lag vs lag±1 to find isolated peaks
    (println "\n=== Isolated Peaks (r(lag) > r(lag-1) AND r(lag) > r(lag+1)) ===")
    (let [lags (range 1 301)
          values (vec (map (fn [lag] (autocorrelation arr n lag)) lags))
          peaks (for [i (range 1 (dec (count values)))
                      :let [curr (nth values i)
                            prev (nth values (dec i))
                            next (nth values (inc i))]
                      :when (and (> curr prev) (> curr next))]
                  {:lag (inc i) :r curr :prominence (- curr (max prev next))})]
      (println (format "  %6s  %10s  %10s" "Lag" "r(lag)" "Prominence"))
      (println (apply str (repeat 30 "-")))
      (doseq [{:keys [lag r prominence]} (take 15 (reverse (sort-by :prominence peaks)))]
        (println (format "  %6d  %10.6f  %10.6f" lag r prominence)))))

  (println "\nDone."))
