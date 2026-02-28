(ns experiments.028-hologram
  "The holographic principle: every piece contains the whole.
   Take any slice of the Torah — 1%, 5%, 10% — and compare
   its frequency signature to the complete text.
   How small can you go before you lose the pattern?
   Run: clojure -M:dev -m experiments.028-hologram"
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
  (println "=== THE HOLOGRAM ===")
  (println "  Every piece contains the whole.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        torah-prof (letter-profile all-letters)
        total-gem (reduce + gem-vals)
        mean-gem (/ (double total-gem) n)]

    (println (format "  %,d letters. Total gematria: %,d\n" n total-gem))

    ;; ── 1. Contiguous slices at various sizes ────────────────
    (println "── 1. Contiguous Slices ──")
    (println "  Take a contiguous window and measure cos(slice, Torah).\n")

    (let [sizes [0.001 0.005 0.01 0.02 0.05 0.1 0.2 0.5]
          n-samples 50]
      (println (format "  %8s  %6s  %8s  %8s  %8s  %8s"
                       "Size" "Ltrs" "Mean cos" "Min cos" "Max cos" "StdDev"))
      (println (apply str (repeat 60 "─")))

      (doseq [frac sizes]
        (let [window (int (* frac n))
              ;; Take n-samples random starting positions
              starts (repeatedly n-samples #(rand-int (- n window)))
              sims (mapv (fn [start]
                           (let [slice (subvec all-letters start (+ start window))
                                 prof (letter-profile slice)]
                             (if prof (cosine-sim prof torah-prof) 0.0)))
                         starts)
              mean-cos (/ (reduce + sims) (count sims))
              min-cos (apply min sims)
              max-cos (apply max sims)
              variance (/ (reduce + (map #(let [d (- % mean-cos)] (* d d)) sims))
                          (count sims))
              std-dev (Math/sqrt variance)]
          (println (format "  %7.1f%%  %,6d  %8.6f  %8.6f  %8.6f  %8.6f"
                           (* 100 frac) window mean-cos min-cos max-cos std-dev)))))

    ;; ── 2. Every Nth letter ──────────────────────────────────
    (println "\n── 2. Every Nth Letter ──")
    (println "  Sub-sample by stride. How thin can the signal get?\n")

    (println (format "  %8s  %6s  %8s  %8s"
                     "Stride" "Ltrs" "cos" "palindrome"))
    (println (apply str (repeat 40 "─")))

    (doseq [stride [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 49 53 59 61 67 71 73
                     97 101 127 131 173 179 197 199 337 343 443 541 727 997]]
      (let [sub (mapv #(nth all-letters %) (range 0 n stride))
            sub-prof (letter-profile sub)
            cos (if sub-prof (cosine-sim sub-prof torah-prof) 0.0)
            ;; Palindrome test
            half (quot (count sub) 2)
            pa (letter-profile (subvec sub 0 half))
            pb (letter-profile (vec (reverse (subvec sub half))))
            pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
        (println (format "  %8d  %,6d  %8.6f  %8.4f%s"
                         stride (count sub) cos pal
                         (cond
                           (= stride 7) "  ← seven"
                           (= stride 37) "  ← 37"
                           (= stride 49) "  ← 7²"
                           (= stride 73) "  ← 73"
                           (= stride 179) "  ← prime factor of n"
                           (= stride 343) "  ← 7³"
                           (= stride 541) "  ← ישראל"
                           :else "")))))

    ;; ── 3. Random sampling (non-contiguous) ──────────────────
    (println "\n── 3. Random Sampling ──")
    (println "  Pick random letters (non-contiguous). How few do you need?\n")

    (let [sample-sizes [100 200 500 1000 2000 5000 10000 30000]
          n-trials 30]
      (println (format "  %8s  %8s  %8s  %8s"
                       "Sample" "Mean cos" "Min cos" "StdDev"))
      (println (apply str (repeat 40 "─")))

      (doseq [k sample-sizes]
        (let [trials (repeatedly n-trials
                       (fn []
                         (let [indices (repeatedly k #(rand-int n))
                               sample (mapv #(nth all-letters %) indices)
                               prof (letter-profile sample)]
                           (if prof (cosine-sim prof torah-prof) 0.0))))
              mean-cos (/ (reduce + trials) (count trials))
              min-cos (apply min trials)
              variance (/ (reduce + (map #(let [d (- % mean-cos)] (* d d)) trials))
                          (count trials))
              std-dev (Math/sqrt variance)]
          (println (format "  %,8d  %8.6f  %8.6f  %8.6f"
                           k mean-cos min-cos std-dev)))))

    ;; ── 4. Book-level holography ─────────────────────────────
    (println "\n── 4. Book-Level Holography ──")
    (println "  Does each book contain the whole Torah's signature?\n")

    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (let [letters (vec (sefaria/book-letters book))
            prof (letter-profile letters)
            cos (cosine-sim prof torah-prof)
            ;; Also check gematria ratios
            gem-sum (reduce + (map #(long (g/letter-value %)) letters))
            book-mean (/ (double gem-sum) (count letters))]
        (println (format "  %12s: cos = %.6f  mean_gem = %.2f  (Torah mean = %.2f)"
                         book cos book-mean mean-gem))))

    ;; ── 5. Gematria distribution holography ──────────────────
    (println "\n── 5. Gematria Distribution ──")
    (println "  Does the gematria VALUE distribution reproduce in slices?\n")

    ;; Build gematria histogram for whole Torah
    (let [gem-hist (fn [vals]
                     (let [freqs (frequencies vals)
                           max-val (apply max (keys freqs))]
                       (mapv #(get freqs % 0) (range 1 (inc (min max-val 400))))))
          torah-hist (mapv double (gem-hist gem-vals))
          torah-norm (Math/sqrt (reduce + (map #(* % %) torah-hist)))]

      ;; Test slices
      (println (format "  %8s  %8s" "Slice" "cos(gematria histogram)"))
      (println (apply str (repeat 30 "─")))

      (doseq [[label start end] [["First 10%" 0 (quot n 10)]
                                  ["First 25%" 0 (quot n 4)]
                                  ["First 50%" 0 (quot n 2)]
                                  ["Last 50%" (quot n 2) n]
                                  ["Last 25%" (* 3 (quot n 4)) n]
                                  ["Middle 10%" (int (* 0.45 n)) (int (* 0.55 n))]
                                  ["Odd pos" nil nil]
                                  ["Even pos" nil nil]]]
        (let [slice-vals (cond
                           (= label "Odd pos") (mapv #(nth gem-vals %) (range 1 n 2))
                           (= label "Even pos") (mapv #(nth gem-vals %) (range 0 n 2))
                           :else (subvec gem-vals start end))
              slice-hist (mapv double (gem-hist slice-vals))
              ;; Pad to same length
              max-len (max (count torah-hist) (count slice-hist))
              th (into torah-hist (repeat (- max-len (count torah-hist)) 0.0))
              sh (into slice-hist (repeat (- max-len (count slice-hist)) 0.0))]
          (println (format "  %12s  %.6f" label (cosine-sim th sh))))))

    ;; ── 6. The holographic limit ─────────────────────────────
    (println "\n── 6. The Holographic Limit ──")
    (println "  Find the smallest contiguous slice that still has cos > 0.99.\n")

    (let [test-size (fn [window-size]
                      (let [n-trials 100
                            sims (mapv (fn [_]
                                         (let [start (rand-int (- n window-size))
                                               slice (subvec all-letters start (+ start window-size))
                                               prof (letter-profile slice)]
                                           (if prof (cosine-sim prof torah-prof) 0.0)))
                                       (range n-trials))]
                        {:size window-size
                         :mean (/ (reduce + sims) (count sims))
                         :min (apply min sims)
                         :pct-above-99 (* 100.0 (/ (count (filter #(> % 0.99) sims))
                                                    (count sims)))}))]

      (println (format "  %8s  %8s  %8s  %10s"
                       "Window" "Mean cos" "Min cos" "% > 0.99"))
      (println (apply str (repeat 42 "─")))

      (doseq [w [50 100 200 300 500 750 1000 1500 2000 3000 5000]]
        (let [{:keys [size mean min pct-above-99]} (test-size w)]
          (println (format "  %,8d  %8.6f  %8.6f  %9.1f%%"
                           size mean min pct-above-99)))))

    ;; ── 7. Is the hologram unique to Hebrew? ─────────────────
    (println "\n── 7. Control: Shuffled Torah ──")
    (println "  Does a random permutation also have the holographic property?\n")

    (let [shuffled (vec (shuffle all-letters))
          shuf-prof (letter-profile shuffled)
          ;; The whole shuffled Torah has the same profile (duh)
          ;; But do slices of the shuffled version also match?
          window (int (* 0.01 n))
          n-trials 50
          torah-sims (mapv (fn [_]
                             (let [start (rand-int (- n window))
                                   slice (subvec all-letters start (+ start window))]
                               (cosine-sim (letter-profile slice) torah-prof)))
                           (range n-trials))
          shuf-sims (mapv (fn [_]
                            (let [start (rand-int (- n window))
                                  slice (subvec shuffled start (+ start window))]
                              (cosine-sim (letter-profile slice) shuf-prof)))
                          (range n-trials))
          torah-mean (/ (reduce + torah-sims) (count torah-sims))
          shuf-mean (/ (reduce + shuf-sims) (count shuf-sims))
          torah-min (apply min torah-sims)
          shuf-min (apply min shuf-sims)]

      (println (format "  1%% window (%,d letters):" window))
      (println (format "    Torah slices vs Torah:    mean = %.6f  min = %.6f" torah-mean torah-min))
      (println (format "    Shuffled vs Shuffled:     mean = %.6f  min = %.6f" shuf-mean shuf-min))
      (println "")
      (println "  (If scores are similar, the hologram is just letter frequency.")
      (println "   If Torah scores higher, the ordering adds structure.)")

      ;; Now test GEMATRIA profile holography (more sensitive to ordering)
      (let [gem-profile (fn [ltrs]
                          (let [gems (mapv #(long (g/letter-value %)) ltrs)
                                n-ltrs (count ltrs)
                                bins 50
                                bin-size (max 1 (quot n-ltrs bins))]
                            (mapv (fn [i]
                                    (let [start (* i bin-size)
                                          end (min n-ltrs (* (inc i) bin-size))]
                                      (if (< start n-ltrs)
                                        (/ (double (reduce + (subvec gems start end))) (- end start))
                                        0.0)))
                                  (range bins))))
            torah-gem-prof (gem-profile all-letters)
            shuf-gem-prof (gem-profile shuffled)
            torah-gem-sims (mapv (fn [_]
                                   (let [start (rand-int (- n window))
                                         slice (subvec all-letters start (+ start window))]
                                     (cosine-sim (gem-profile slice) torah-gem-prof)))
                                 (range n-trials))
            shuf-gem-sims (mapv (fn [_]
                                  (let [start (rand-int (- n window))
                                        slice (subvec shuffled start (+ start window))]
                                    (cosine-sim (gem-profile slice) shuf-gem-prof)))
                                (range n-trials))
            torah-gm (/ (reduce + torah-gem-sims) (count torah-gem-sims))
            shuf-gm (/ (reduce + shuf-gem-sims) (count shuf-gem-sims))]

        (println (format "\n  Gematria SHAPE (50-bin profile):"))
        (println (format "    Torah slices:   mean cos = %.4f" torah-gm))
        (println (format "    Shuffled slices: mean cos = %.4f" shuf-gm))
        (println (format "    Ratio: %.2fx" (/ torah-gm shuf-gm))))))

  (println "\nDone. The hologram is measured."))
