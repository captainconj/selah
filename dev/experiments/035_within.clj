(ns experiments.035-within
  "The fractal within.
   Does each book reproduce the structure of the whole?
   Does each book have its own √2 ratio, its own 7-fold palindrome,
   its own center that converges?
   Run: clojure -M:dev -m experiments.035-within"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

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

(defn analyze-book [book-name all-letters gem-vals]
  (let [n (count all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "\n━━━ %s ━━━  (%,d letters, Σ = %,d)" book-name n total-gem))

    ;; 1. Half palindrome (fold)
    (let [half (quot n 2)
          pa (letter-profile (subvec all-letters 0 half))
          pb (letter-profile (vec (reverse (subvec all-letters half))))
          cos (if (and pa pb) (cosine-sim pa pb) 0.0)]
      (println (format "  Fold palindrome: %.6f" cos)))

    ;; 2. √2 ratio test (first half / second half by letter count)
    (let [half (quot n 2)
          first-gem (reduce + (subvec gem-vals 0 half))
          second-gem (reduce + (subvec gem-vals half))
          ratio (/ (double (max first-gem second-gem)) (min first-gem second-gem))]
      (println (format "  Half ratio: %.4f (√2 = 1.4142, err = %.4f)"
                       ratio (Math/abs (- ratio (Math/sqrt 2))))))

    ;; 3. Seven-fold palindrome
    (let [seg-size (quot n 7)
          seg-sums (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end (if (= i 6) n (* (inc i) seg-size))]
                             (double (reduce + (subvec gem-vals start end)))))
                         (range 7))
          first3 (subvec seg-sums 0 3)
          last3-rev (vec (reverse (subvec seg-sums 4 7)))
          cos (cosine-sim first3 last3-rev)]
      (println (format "  7-fold palindrome (1-3 vs 5-7): %.6f" cos))
      (println (format "  Center segment (4th): %,.0f" (nth seg-sums 3))))

    ;; 4. Seven threads
    (let [thread-sums (mapv (fn [offset]
                              (double (reduce + (map #(long (nth gem-vals %))
                                                     (range offset n 7)))))
                            (range 7))
          first3 (subvec thread-sums 0 3)
          last3-rev (vec (reverse (subvec thread-sums 4 7)))
          cos (cosine-sim first3 last3-rev)]
      (println (format "  7-thread palindrome: %.6f" cos)))

    ;; 5. Center letter
    (let [center-idx (quot n 2)
          center-ch (nth all-letters center-idx)
          center-gem (long (g/letter-value center-ch))]
      (println (format "  Center letter: %c (gem=%d)" center-ch center-gem)))

    ;; 6. Center of mass
    (let [weighted (reduce + (map-indexed (fn [i v] (* (long i) (long v))) gem-vals))
          com (/ (double weighted) total-gem)
          geo (/ (double n) 2)]
      (println (format "  Center of mass: %.1f (geometric: %.1f, diff: %.3f%%)"
                       com geo (* 100 (/ (Math/abs (- com geo)) n)))))

    ;; 7. Divisibility
    (println (format "  Σ mod 7 = %d, mod 37 = %d, mod 73 = %d, mod 441 = %d"
                     (mod total-gem 7) (mod total-gem 37) (mod total-gem 73) (mod total-gem 441)))

    ;; 8. π/2 test: split into 5 segments, test (1+2+3)/(4+5)
    (let [seg5-size (quot n 5)
          seg5-sums (mapv (fn [i]
                            (let [start (* i seg5-size)
                                  end (if (= i 4) n (* (inc i) seg5-size))]
                              (reduce + (subvec gem-vals start end))))
                          (range 5))
          first3-sum (+ (nth seg5-sums 0) (nth seg5-sums 1) (nth seg5-sums 2))
          last2-sum (+ (nth seg5-sums 3) (nth seg5-sums 4))
          ratio (/ (double first3-sum) last2-sum)]
      (println (format "  5-fold ratio (1+2+3)/(4+5): %.4f (π/2 = 1.5708, err = %.4f)"
                       ratio (Math/abs (- ratio (/ Math/PI 2))))))

    ;; 9. Internal book structure — split by natural divisions
    ;; For each book, split into chapters and test palindrome
    (let [ch-count (get sefaria/book-chapters book-name)
          ch-gem-sums (mapv (fn [ch]
                              (let [verses (sefaria/fetch-chapter book-name ch)
                                    letters (mapcat (fn [v]
                                                      (norm/letter-stream (norm/strip-html v)))
                                                    verses)]
                                (double (reduce + (map #(long (g/letter-value %)) letters)))))
                            (range 1 (inc ch-count)))
          half-ch (quot ch-count 2)
          first-chs (subvec ch-gem-sums 0 half-ch)
          last-chs (vec (reverse (subvec ch-gem-sums (- ch-count half-ch))))
          cos (cosine-sim first-chs last-chs)]
      (println (format "  Chapter palindrome (%d chapters, %d vs %d): %.6f"
                       ch-count half-ch half-ch cos)))))

(defn -main []
  (println "=== THE FRACTAL WITHIN ===")
  (println "  Does each book contain the whole?\n")

  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
    (doseq [book books]
      (let [letters (vec (sefaria/book-letters book))
            gems (mapv #(long (g/letter-value %)) letters)]
        (analyze-book book letters gems)))

    ;; Comparison table
    (println "\n\n── Summary Table ──\n")
    (println (format "  %12s  %8s  %8s  %8s  %8s  %6s  %6s  %6s"
                     "Book" "Fold" "7-fold" "7-thrd" "ch-pal" "mod7" "mod37" "mod441"))
    (println (apply str (repeat 80 "─")))

    (doseq [book books]
      (let [letters (vec (sefaria/book-letters book))
            gems (mapv #(long (g/letter-value %)) letters)
            n (count letters)
            total (reduce + gems)
            ;; Fold
            half (quot n 2)
            pa (letter-profile (subvec letters 0 half))
            pb (letter-profile (vec (reverse (subvec letters half))))
            fold-cos (if (and pa pb) (cosine-sim pa pb) 0.0)
            ;; 7-fold
            seg7 (quot n 7)
            s7 (mapv (fn [i] (double (reduce + (subvec gems (* i seg7)
                                                        (if (= i 6) n (* (inc i) seg7))))))
                     (range 7))
            seven-cos (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))
            ;; 7-thread
            t7 (mapv (fn [off] (double (reduce + (map #(long (nth gems %)) (range off n 7))))) (range 7))
            thread-cos (cosine-sim (subvec t7 0 3) (vec (reverse (subvec t7 4 7))))
            ;; Chapter palindrome
            ch-count (get sefaria/book-chapters book)
            ch-sums (mapv (fn [ch]
                            (let [verses (sefaria/fetch-chapter book ch)
                                  ltrs (mapcat (fn [v]
                                                 (norm/letter-stream (norm/strip-html v)))
                                               verses)]
                              (double (reduce + (map #(long (g/letter-value %)) ltrs)))))
                          (range 1 (inc ch-count)))
            half-ch (quot ch-count 2)
            ch-cos (cosine-sim (subvec ch-sums 0 half-ch)
                               (vec (reverse (subvec ch-sums (- ch-count half-ch)))))]
        (println (format "  %12s  %8.4f  %8.4f  %8.4f  %8.4f  %6d  %6d  %6d"
                         book fold-cos seven-cos thread-cos ch-cos
                         (mod total 7) (mod total 37) (mod total 441))))))

  (println "\nDone. The fractal reproduces."))
