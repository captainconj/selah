(ns experiments.012-twin-symmetry
  "Symmetries between Exodus and Numbers — the B/B' pair.
   Does Exodus forward mirror Numbers backward?
   Run: clojure -M:dev -m experiments.012-twin-symmetry"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn chapter-profile
  "Compute letter frequency profile and stats for a chapter."
  [book ch]
  (let [verses  (sefaria/fetch-chapter book ch)
        raw     (apply str (map norm/strip-html verses))
        letters (norm/letter-stream raw)
        n       (count letters)
        freqs   (frequencies letters)
        profile (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet)]
    {:book book :chapter ch :letters n
     :gematria (g/total letters)
     :mean     (/ (double (g/total letters)) n)
     :profile  profile}))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (/ dot (* ma mb))))

(defn -main []
  (println "=== Symmetries Between Exodus and Numbers ===\n")

  (println "Loading chapter profiles...")
  (let [exod  (mapv #(chapter-profile "Exodus" %) (range 1 41))
        num   (mapv #(chapter-profile "Numbers" %) (range 1 37))
        ;; Reversed Numbers
        num-r (vec (reverse num))]

    ;; ── 1. Forward-Forward pairing ──────────────────────────
    (println "\n=== 1. Forward-Forward: Exodus i ↔ Numbers i ===")
    (println (format "  (First 36 chapters)\n"))
    (println (format "  %3s  %6s %6s  %6s %6s  %8s %8s  %6s"
                     "Ch" "Ex.L" "Nu.L" "Ex.μ" "Nu.μ" "Ex.Σ" "Nu.Σ" "cos"))
    (println (apply str (repeat 68 "-")))
    (let [fwd-sims (atom [])]
      (doseq [i (range 36)]
        (let [e (nth exod i)
              n (nth num i)
              cos (cosine-sim (:profile e) (:profile n))]
          (swap! fwd-sims conj cos)
          (println (format "  %3d  %,6d %,6d  %6.1f %6.1f  %,8d %,8d  %.4f"
                           (inc i) (:letters e) (:letters n)
                           (:mean e) (:mean n)
                           (:gematria e) (:gematria n) cos))))
      (println (format "\n  Mean forward cosine similarity: %.4f"
                       (/ (reduce + @fwd-sims) (count @fwd-sims)))))

    ;; ── 2. Chiastic pairing: Exodus i ↔ Numbers (37-i) ─────
    (println "\n=== 2. Chiastic: Exodus i ↔ Numbers (37-i) ===")
    (println (format "  (Exodus forward, Numbers reversed)\n"))
    (println (format "  %6s  %6s %6s  %6s %6s  %8s %8s  %6s"
                     "Pair" "Ex.L" "Nu.L" "Ex.μ" "Nu.μ" "Ex.Σ" "Nu.Σ" "cos"))
    (println (apply str (repeat 68 "-")))
    (let [chi-sims (atom [])]
      (doseq [i (range 36)]
        (let [e   (nth exod i)
              n   (nth num-r i)
              cos (cosine-sim (:profile e) (:profile n))]
          (swap! chi-sims conj cos)
          (println (format "  %2d↔%2d  %,6d %,6d  %6.1f %6.1f  %,8d %,8d  %.4f"
                           (inc i) (- 36 i)
                           (:letters e) (:letters n)
                           (:mean e) (:mean n)
                           (:gematria e) (:gematria n) cos))))
      (println (format "\n  Mean chiastic cosine similarity: %.4f"
                       (/ (reduce + @chi-sims) (count @chi-sims)))))

    ;; ── 3. Letter count symmetry ────────────────────────────
    (println "\n=== 3. Letter Count Symmetries ===")

    ;; Cumulative letter counts — do they mirror?
    (let [ex-cum (reductions + (map :letters exod))
          nu-cum (reductions + (map :letters num))
          nu-cum-r (reductions + (map :letters num-r))
          ;; Normalize to [0,1]
          ex-total (last ex-cum)
          nu-total (last nu-cum)]
      (println "\n  Normalized cumulative letter counts:")
      (println (format "  %3s  %8s  %8s  %8s" "Ch" "Ex cum%" "Nu cum%" "Nu rev%"))
      (println (apply str (repeat 36 "-")))
      (doseq [i (range (min 36 (count ex-cum)))]
        (println (format "  %3d  %8.4f  %8.4f  %8.4f"
                         (inc i)
                         (/ (double (nth ex-cum i)) ex-total)
                         (/ (double (nth nu-cum i)) nu-total)
                         (/ (double (nth nu-cum-r i)) nu-total)))))

    ;; ── 4. Gematria mean per chapter — mirror? ──────────────
    (println "\n=== 4. Gematria Mean Profile ===")
    (println "  Does Exodus's mean-per-chapter mirror Numbers reversed?\n")
    (let [ex-means (mapv :mean exod)
          nu-means (mapv :mean num)
          nu-means-r (vec (reverse nu-means))
          ;; Correlate forward and reversed
          ;; Use first 36 of Exodus
          ex36 (subvec ex-means 0 36)
          fwd-corr  (cosine-sim ex36 nu-means)
          rev-corr  (cosine-sim ex36 nu-means-r)]
      (println (format "  Exodus 1-36 vs Numbers 1-36 (forward):  cosine = %.4f" fwd-corr))
      (println (format "  Exodus 1-36 vs Numbers 36-1 (reversed): cosine = %.4f" rev-corr))
      (println (format "  %s correlation is stronger"
                       (if (> rev-corr fwd-corr) "REVERSED (chiastic)" "Forward"))))

    ;; ── 5. Chapter-length symmetry ──────────────────────────
    (println "\n=== 5. Chapter Length Profile ===")
    (let [ex-lens (mapv :letters exod)
          nu-lens (mapv :letters num)
          nu-lens-r (vec (reverse nu-lens))
          ex36 (subvec ex-lens 0 36)
          fwd-corr  (cosine-sim (mapv double ex36) (mapv double nu-lens))
          rev-corr  (cosine-sim (mapv double ex36) (mapv double nu-lens-r))]
      (println (format "  Exodus 1-36 vs Numbers 1-36 (forward):  cosine = %.4f" fwd-corr))
      (println (format "  Exodus 1-36 vs Numbers 36-1 (reversed): cosine = %.4f" rev-corr))
      (println (format "  %s correlation is stronger"
                       (if (> rev-corr fwd-corr) "REVERSED (chiastic)" "Forward"))))

    ;; ── 6. Self-symmetry within each book ───────────────────
    (println "\n=== 6. Internal Symmetry ===")
    (println "  Does each book mirror itself? (first half vs reversed second half)\n")
    (doseq [[label chapters] [["Exodus" exod] ["Numbers" num]]]
      (let [n    (count chapters)
            half (quot n 2)
            first-half  (subvec chapters 0 half)
            second-half (subvec chapters half (* 2 half))
            second-rev  (vec (reverse second-half))
            ;; Compare letter counts
            a (mapv (comp double :letters) first-half)
            b (mapv (comp double :letters) second-rev)
            cos-len (cosine-sim a b)
            ;; Compare gematria means
            a2 (mapv :mean first-half)
            b2 (mapv :mean second-rev)
            cos-mean (cosine-sim a2 b2)]
        (println (format "  %s (n=%d, half=%d):" label n half))
        (println (format "    Chapter lengths:  cos(first, rev-second) = %.4f" cos-len))
        (println (format "    Gematria means:   cos(first, rev-second) = %.4f" cos-mean))))

    ;; ── 7. Cumulative gematria crossing ─────────────────────
    (println "\n=== 7. Cumulative Gematria — Do They Cross? ===")
    (let [ex-cum-g (vec (reductions + (map :gematria exod)))
          nu-cum-g (vec (reductions + (map :gematria num)))
          ;; Scale Numbers to Exodus's rate to see crossing
          ex-rate (/ (double (last ex-cum-g)) (count exod))
          nu-rate (/ (double (last nu-cum-g)) (count num))]
      (println (format "  Exodus gematria rate:  %,.0f per chapter" ex-rate))
      (println (format "  Numbers gematria rate: %,.0f per chapter" nu-rate))
      (println "\n  Cumulative gematria (normalized to % of book total):")
      (println (format "  %3s  %8s  %8s  %8s" "Ch" "Ex cum%" "Nu cum%" "Diff"))
      (println (apply str (repeat 36 "-")))
      (doseq [i (range (min 36 (count ex-cum-g)))]
        (let [ex-pct (/ (double (nth ex-cum-g i)) (last ex-cum-g))
              nu-pct (/ (double (nth nu-cum-g i)) (last nu-cum-g))]
          (when (zero? (mod (inc i) 4))
            (println (format "  %3d  %8.4f  %8.4f  %+8.4f"
                             (inc i) ex-pct nu-pct (- ex-pct nu-pct))))))))

  (println "\nDone."))
