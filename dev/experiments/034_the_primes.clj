(ns experiments.034-the-primes
  "The Torah through the lens of prime numbers.
   Prime-indexed letters. Twin primes. Mersenne primes.
   The intersection of number theory and the letter stream.
   Run: clojure -M:dev -m experiments.034-the-primes"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else (loop [i 3]
            (cond
              (> (* i i) n) true
              (zero? (mod n i)) false
              :else (recur (+ i 2))))))

(defn primes-up-to [limit]
  ;; Simple sieve of Eratosthenes
  (let [sieve (boolean-array (inc limit) true)]
    (aset sieve 0 false)
    (aset sieve 1 false)
    (loop [i 2]
      (when (<= (* i i) limit)
        (when (aget sieve i)
          (loop [j (* i i)]
            (when (<= j limit)
              (aset sieve j false)
              (recur (+ j i)))))
        (recur (inc i))))
    (vec (filter #(aget sieve %) (range (inc limit))))))

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
  (println "=== THE PRIMES ===")
  (println "  Number theory meets the letter stream.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        torah-prof (letter-profile all-letters)]

    (println (format "  %,d letters.\n" n))
    (println "  Generating primes...")
    (let [primes (primes-up-to n)
          n-primes (count primes)]
      (println (format "  %,d primes up to %,d\n" n-primes n))

      ;; ── 1. Prime-indexed letters ────────────────────────────
      (println "── 1. Prime-Indexed Letters ──")
      (println "  The sub-Torah of letters at prime positions.\n")

      (let [prime-letters (mapv #(nth all-letters %) primes)
            prime-gems (mapv #(long (g/letter-value %)) prime-letters)
            prime-prof (letter-profile prime-letters)
            prime-sum (reduce + prime-gems)]
        (println (format "  %,d prime-indexed letters" n-primes))
        (println (format "  cos(Torah): %.6f" (cosine-sim prime-prof torah-prof)))
        (println (format "  Σ gematria: %,d" prime-sum))
        (println (format "  mod 7 = %d, mod 37 = %d, mod 73 = %d"
                         (mod prime-sum 7) (mod prime-sum 37) (mod prime-sum 73)))
        (println (format "  mod 441 = %d" (mod prime-sum 441)))

        ;; Palindrome
        (let [half (quot n-primes 2)
              pa (letter-profile (subvec prime-letters 0 half))
              pb (letter-profile (vec (reverse (subvec prime-letters half))))
              pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
          (println (format "  Internal palindrome: %.4f" pal))))

      ;; ── 2. Composite-indexed letters ────────────────────────
      (println "\n── 2. Composite-Indexed Letters ──")
      (println "  The complement: letters at non-prime positions.\n")

      (let [prime-set (set primes)
            comp-letters (vec (keep-indexed (fn [i ch] (when-not (prime-set i) ch)) all-letters))
            comp-gems (mapv #(long (g/letter-value %)) comp-letters)
            comp-prof (letter-profile comp-letters)
            comp-sum (reduce + comp-gems)]
        (println (format "  %,d composite-indexed letters" (count comp-letters)))
        (println (format "  cos(Torah): %.6f" (cosine-sim comp-prof torah-prof)))
        (println (format "  Σ gematria: %,d" comp-sum))
        (println (format "  mod 7 = %d, mod 37 = %d" (mod comp-sum 7) (mod comp-sum 37))))

      ;; ── 3. Twin primes ──────────────────────────────────────
      (println "\n── 3. Twin Primes ──")
      (println "  Primes p where p+2 is also prime.\n")

      (let [twins (filterv (fn [p] (and (< (+ p 2) n) (prime? (+ p 2)))) primes)
            twin-letters (mapv #(nth all-letters %) twins)
            twin-gems (mapv #(long (g/letter-value %)) twin-letters)
            twin-sum (reduce + twin-gems)]
        (println (format "  %,d twin primes up to %,d" (count twins) n))
        (println (format "  Σ gematria: %,d" twin-sum))
        (println (format "  mod 7 = %d, mod 37 = %d" (mod twin-sum 7) (mod twin-sum 37)))

        ;; Letter pairs at twin primes
        (println "\n  First 20 twin prime letter pairs:")
        (doseq [p (take 20 twins)]
          (let [ch1 (nth all-letters p)
                ch2 (nth all-letters (+ p 2))]
            (println (format "    p=%5d:  %c(%d) — %c(%d)  pair_sum=%d"
                             p ch1 (long (g/letter-value ch1))
                             ch2 (long (g/letter-value ch2))
                             (+ (long (g/letter-value ch1)) (long (g/letter-value ch2))))))))

      ;; ── 4. Gematria at special primes ───────────────────────
      (println "\n── 4. Letters at Special Primes ──\n")

      (let [mersenne-exps [2 3 5 7 13 17 19 31]
            mersenne-primes (mapv (fn [p] (dec (long (Math/pow 2 p)))) mersenne-exps)]
        (println "  Mersenne primes (2^p - 1):")
        (doseq [[exp mp] (map vector mersenne-exps mersenne-primes)]
          (when (< mp n)
            (let [ch (nth all-letters mp)
                  gv (long (g/letter-value ch))]
              (println (format "    M(%2d) = %,10d  letter: %c  gem=%d" exp mp ch gv)))))

        ;; Sum at Mersenne positions
        (let [valid-mersenne (filterv #(< % n) mersenne-primes)
              m-gems (mapv #(long (g/letter-value (nth all-letters %))) valid-mersenne)
              m-sum (reduce + m-gems)]
          (println (format "\n  Mersenne position gematria sum: %d" m-sum))
          (println (format "  mod 7 = %d" (mod m-sum 7)))))

      ;; Fibonacci primes
      (println "\n  Fibonacci primes:")
      (let [fibs (loop [a 1 b 1 result []]
                   (if (> a n)
                     result
                     (recur b (+ a b) (if (prime? a) (conj result a) result))))]
        (doseq [f fibs]
          (when (< f n)
            (let [ch (nth all-letters f)
                  gv (long (g/letter-value ch))]
              (println (format "    Fib prime %,10d  letter: %c  gem=%d" f ch gv))))))

      ;; ── 5. Prime-counting function π(n) and the Torah ──────
      (println "\n── 5. Prime Counting ──")
      (println "  How does π(n) relate to the Torah's structure?\n")

      (let [n-primes-at (fn [x] (count (take-while #(<= % x) primes)))]
        (doseq [[label pos] [["At center" (quot n 2)]
                              ["At 1/7" (quot n 7)]
                              ["At 1/5" (quot n 5)]
                              ["At Gen/Exod boundary" 78064]
                              ["Total" n]]]
          (let [pi-n (n-primes-at pos)]
            (println (format "  π(%,d) = %,d  ratio=%.6f%s"
                             pos pi-n (/ (double pi-n) pos)
                             (if (= label "Total") (format "  ≈ 1/ln(n) = %.6f" (/ 1.0 (Math/log n))) ""))))))

      ;; ── 6. Gap primes — primes at Torah structural gaps ────
      (println "\n── 6. Primes at Structural Positions ──\n")

      (let [positions [[37 "37th letter"]
                        [73 "73rd letter"]
                        [441 "441st letter"]
                        [2701 "2701st letter"]
                        [5846 "verse count"]
                        [153134 "center"]
                        [78064 "Gen length"]
                        [63857 "Exod length"]
                        [44980 "Lev length"]]]
        (doseq [[pos label] positions]
          (println (format "  %12s (%,7d): %s prime, nearest prime = %,d (distance %d)"
                           label pos
                           (if (prime? pos) "IS" "not")
                           (first (filter prime? (map #(+ pos %) (range 1000))))
                           (- (long (first (filter prime? (map #(+ pos %) (range 1000))))) pos)))))

      ;; ── 7. Prime gematria words ────────────────────────────
      (println "\n── 7. Prime Factorization of Key Numbers ──\n")

      (let [factorize (fn [num]
                        (loop [n num d 2 factors []]
                          (cond
                            (< n 2) factors
                            (zero? (mod n d)) (recur (quot n d) d (conj factors d))
                            (> (* d d) n) (conj factors n)
                            :else (recur n (inc d) factors))))]
        (doseq [num [306269 21113757 2701 441 1841 5846 78064 63857 44980 63846 55222]]
          (let [factors (factorize num)]
            (println (format "  %,12d = %s" num
                             (str/join " × " (map str factors)))))))

      ;; ── 8. Every prime-th letter palindrome ────────────────
      (println "\n── 8. Sub-Torahs at Prime Strides ──\n")

      (println (format "  %8s  %6s  %8s  %8s" "Stride" "Ltrs" "cos" "palindrome"))
      (println (apply str (repeat 40 "─")))

      (doseq [p [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97]]
        (let [sub (mapv #(nth all-letters %) (range 0 n p))
              sub-prof (letter-profile sub)
              cos (if sub-prof (cosine-sim sub-prof torah-prof) 0.0)
              half (quot (count sub) 2)
              pa (letter-profile (subvec sub 0 half))
              pb (letter-profile (vec (reverse (subvec sub half))))
              pal (if (and pa pb) (cosine-sim pa pb) 0.0)]
          (println (format "  %8d  %,6d  %8.6f  %8.4f%s"
                           p (count sub) cos pal
                           (cond
                             (= p 7) "  ← seven"
                             (= p 37) "  ← 37 (12th prime)"
                             (= p 73) "  ← 73 (21st prime)"
                             :else "")))))))

  (println "\nDone. The primes have spoken."))
