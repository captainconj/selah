(ns experiments.029-information
  "Information theory of the Torah's letter stream.
   Shannon entropy. Mutual information at key distances.
   Conditional entropy. The deep ordering question:
   how much does position i tell you about position i+7?
   Run: clojure -M:dev -m experiments.029-information"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn shannon-entropy
  "Shannon entropy in bits."
  [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (- (reduce + (map (fn [[_ c]]
                          (let [p (/ (double c) n)]
                            (if (pos? p) (* p (/ (Math/log p) (Math/log 2))) 0.0)))
                        freqs))))))

(defn joint-entropy
  "Joint entropy H(X,Y) of paired symbols."
  [pairs]
  (let [n (count pairs)
        freqs (frequencies pairs)]
    (when (pos? n)
      (- (reduce + (map (fn [[_ c]]
                          (let [p (/ (double c) n)]
                            (if (pos? p) (* p (/ (Math/log p) (Math/log 2))) 0.0)))
                        freqs))))))

(defn mutual-information
  "I(X;Y) = H(X) + H(Y) - H(X,Y)"
  [xs ys]
  (let [pairs (mapv vector xs ys)
        hx (shannon-entropy xs)
        hy (shannon-entropy ys)
        hxy (joint-entropy pairs)]
    (when (and hx hy hxy)
      (- (+ hx hy) hxy))))

(defn conditional-entropy
  "H(Y|X) = H(X,Y) - H(X)"
  [xs ys]
  (let [pairs (mapv vector xs ys)
        hx (shannon-entropy xs)
        hxy (joint-entropy pairs)]
    (when (and hx hxy)
      (- hxy hx))))

(defn -main []
  (println "=== INFORMATION THEORY ===")
  (println "  How much does position know about position?\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. Basic entropy ─────────────────────────────────────
    (println "── 1. Shannon Entropy ──")
    (println "  Maximum for 22 symbols = log₂(22) = 4.459 bits\n")

    (let [h (shannon-entropy all-letters)]
      (println (format "  Torah entropy: %.4f bits" h))
      (println (format "  Maximum:       %.4f bits" (/ (Math/log 22) (Math/log 2))))
      (println (format "  Efficiency:    %.2f%%" (* 100 (/ h (/ (Math/log 22) (Math/log 2)))))))

    ;; Per-book entropy
    (println "\n  Per-book entropy:")
    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (let [letters (vec (sefaria/book-letters book))
            h (shannon-entropy letters)]
        (println (format "    %12s: %.4f bits" book h))))

    ;; Shuffled control
    (let [h-shuffled (shannon-entropy (shuffle all-letters))]
      (println (format "\n  Shuffled Torah: %.4f bits (should be identical — entropy is order-invariant)" h-shuffled)))

    ;; ── 2. Bigram entropy ────────────────────────────────────
    (println "\n── 2. Bigram Entropy ──")
    (println "  How much does letter i tell you about letter i+1?\n")

    (let [xs (subvec all-letters 0 (dec n))
          ys (subvec all-letters 1 n)
          h1 (shannon-entropy all-letters)
          h-cond (conditional-entropy xs ys)
          mi (mutual-information xs ys)]
      (println (format "  H(letter):     %.4f bits" h1))
      (println (format "  H(next|curr):  %.4f bits" h-cond))
      (println (format "  I(curr;next):  %.4f bits" mi))
      (println (format "  Redundancy:    %.2f%% (how much the current letter predicts the next)"
                       (* 100 (/ mi h1)))))

    ;; ── 3. Mutual information at key distances ───────────────
    (println "\n── 3. Mutual Information by Distance ──")
    (println "  I(X_i ; X_{i+d}) for key distances d.\n")

    (println (format "  %8s  %10s  %10s  %10s"
                     "Distance" "MI (bits)" "H(Y|X)" "MI/H ratio"))
    (println (apply str (repeat 45 "─")))

    (let [h1 (shannon-entropy all-letters)]
      (doseq [d [1 2 3 4 5 6 7 8 9 10 11 12 13
                  14 21 28 29 37 41 49 59 73 97
                  127 179 337 343 441 541 997]]
        (let [xs (subvec all-letters 0 (- n d))
              ys (subvec all-letters d n)
              mi (mutual-information xs ys)
              h-cond (conditional-entropy xs ys)]
          (when mi
            (println (format "  %8d  %10.6f  %10.4f  %10.4f%%%s"
                             d mi h-cond (* 100 (/ mi h1))
                             (cond
                               (= d 7) "  ← seven"
                               (= d 37) "  ← 37"
                               (= d 49) "  ← 7²"
                               (= d 73) "  ← 73"
                               (= d 179) "  ← prime factor"
                               (= d 343) "  ← 7³"
                               (= d 441) "  ← 21²"
                               (= d 541) "  ← ישראל"
                               :else "")))))))

    ;; ── 4. Gematria-value mutual information ─────────────────
    (println "\n── 4. Gematria MI ──")
    (println "  Mutual information between gematria VALUES at distance d.\n")

    ;; Bin gematria values into classes for tractable MI
    (let [gem-classes (mapv (fn [v]
                              (cond
                                (<= v 4) :tiny    ; aleph-dalet
                                (<= v 10) :small  ; he-yod
                                (<= v 50) :mid    ; kaf-nun
                                (<= v 100) :large ; samekh-qof
                                :else :huge))     ; resh-tav
                            gem-vals)
          h-gem (shannon-entropy gem-classes)]

      (println (format "  Gematria class entropy: %.4f bits (5 classes, max = %.4f)\n"
                       h-gem (/ (Math/log 5) (Math/log 2))))

      (println (format "  %8s  %10s  %10s" "Distance" "MI (bits)" "MI/H ratio"))
      (println (apply str (repeat 35 "─")))

      (doseq [d [1 2 3 5 7 11 13 37 49 73 343]]
        (let [xs (subvec gem-classes 0 (- n d))
              ys (subvec gem-classes d n)
              mi (mutual-information xs ys)]
          (when mi
            (println (format "  %8d  %10.6f  %10.4f%%%s"
                             d mi (* 100 (/ mi h-gem))
                             (cond
                               (= d 7) "  ← seven"
                               (= d 37) "  ← 37"
                               (= d 49) "  ← 7²"
                               (= d 73) "  ← 73"
                               (= d 343) "  ← 7³"
                               :else "")))))))

    ;; ── 5. Entropy by position within structure ──────────────
    (println "\n── 5. Positional Entropy ──")
    (println "  Is entropy uniform across the Torah, or does it vary?\n")

    (let [n-segments 14  ; 2×7
          seg-size (quot n n-segments)]
      (println (format "  %8s  %8s  %8s  %10s"
                       "Segment" "Letters" "Entropy" "Book"))
      (println (apply str (repeat 42 "─")))

      (let [book-lengths (mapv (fn [b] (count (sefaria/book-letters b)))
                               ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
            book-names ["Gen" "Exod" "Lev" "Num" "Deut"]
            cumulative (reductions + 0 book-lengths)]
        (dotimes [i n-segments]
          (let [start (* i seg-size)
                end (if (= i (dec n-segments)) n (* (inc i) seg-size))
                seg (subvec all-letters start end)
                h (shannon-entropy seg)
                ;; Which book is the midpoint in?
                mid (quot (+ start end) 2)
                book-idx (count (filter #(<= % mid) (butlast cumulative)))]
            (println (format "  %8d  %,8d  %8.4f  %10s"
                             (inc i) (count seg) h
                             (nth book-names (min (dec (count book-names)) (dec book-idx)))))))))

    ;; ── 6. Compression ratio ─────────────────────────────────
    (println "\n── 6. Compression ──")
    (println "  How compressible is the Torah vs shuffled?")
    (println "  Using run-length encoding as a proxy.\n")

    (let [rle-count (fn [letters]
                      ;; Count RLE runs
                      (loop [i 1 runs 1 prev (first letters)]
                        (if (>= i (count letters))
                          runs
                          (let [curr (nth letters i)]
                            (recur (inc i)
                                   (if (= curr prev) runs (inc runs))
                                   curr)))))
          torah-runs (rle-count all-letters)
          shuffled-runs (rle-count (vec (shuffle all-letters)))]
      (println (format "  Torah RLE runs:    %,d (ratio: %.4f)"
                       torah-runs (/ (double torah-runs) n)))
      (println (format "  Shuffled RLE runs: %,d (ratio: %.4f)"
                       shuffled-runs (/ (double shuffled-runs) n)))
      (println (format "  Torah has %.2f%% %s runs"
                       (* 100 (Math/abs (/ (- (double torah-runs) shuffled-runs) shuffled-runs)))
                       (if (< torah-runs shuffled-runs) "fewer" "more"))))

    ;; ── 7. Conditional on mod-7 position ─────────────────────
    (println "\n── 7. Entropy by Mod-7 Position ──")
    (println "  Does the letter distribution depend on position mod 7?\n")

    (doseq [phase (range 7)]
      (let [phase-letters (mapv #(nth all-letters %)
                                (range phase n 7))
            h (shannon-entropy phase-letters)]
        (println (format "  Phase %d: H = %.4f bits  (%,d letters)" phase h (count phase-letters)))))

    (let [;; Total MI between position-mod-7 and letter identity
          phases (mapv #(mod % 7) (range n))
          mi (mutual-information phases all-letters)]
      (println (format "\n  I(phase; letter) = %.6f bits" mi))
      (println "  (If zero, the letter distribution is independent of mod-7 position.)")))

  (println "\nDone. The information is measured."))
