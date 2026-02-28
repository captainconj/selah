(ns experiments.003-center-spiral
  "Fibonacci spiral from the Torah's center.
   The center is Leviticus 8:29. Spiral outward in Fibonacci steps.
   Run: clojure -M:dev -m experiments.003-center-spiral"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.locate :as locate]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn load-torah []
  (println "Loading Torah...")
  (let [books (mapv (fn [b]
                      (let [letters (sefaria/book-letters b)]
                        {:name b :letters letters :count (count letters)}))
                    book-names)
        torah (vec (mapcat :letters books))]
    (println (format "  %,d letters" (count torah)))
    {:torah torah :books books}))

(defn locate-torah-pos [books pos]
  (when (and (>= pos 0) (< pos (reduce + (map :count books))))
    (loop [p pos i 0]
      (let [{:keys [name letters count]} (nth books i)]
        (if (< p count)
          (let [ch-map (locate/chapter-map name)
                loc    (locate/locate name ch-map p)]
            (assoc loc :letter (nth letters p)
                       :value (g/letter-value (nth letters p))))
          (recur (- p count) (inc i)))))))

(defn fibonacci-seq
  "Generate Fibonacci numbers starting from [1, 1]."
  []
  (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))

(defn center-spiral-positions
  "From center position, generate spiral: center, center+1, center-1, center+2, center-3, center+5, center-8...
   Alternating direction, Fibonacci steps."
  [center max-n]
  (let [fibs (fibonacci-seq)]
    (->> fibs
         (mapcat (fn [f] [(+ center f) (- center f)]))
         (filter #(and (>= % 0) (< % max-n)))
         (cons center)
         vec)))

(defn spiral-from-center [books torah]
  (println "\n=== Fibonacci Spiral From Torah Center ===")
  (let [n      (count torah)
        center (quot n 2)
        fibs   (take 30 (fibonacci-seq))
        _      (println (format "  Center: position %,d" center))
        _      (println (format "  Letter at center: %s (value %d)"
                                (nth torah center)
                                (g/letter-value (nth torah center))))]

    ;; Spiral out from center
    (println (format "\n  %-6s  %8s  %-12s %3s  %s" "Fib#" "Position" "Location" "Ch" "Letter"))
    (println (apply str (repeat 52 "-")))

    ;; Center itself
    (let [loc (locate-torah-pos books center)]
      (println (format "  %-6s  %,8d  %-12s %3d  %s (%d)"
                       "center" center (:book loc) (:chapter loc)
                       (:letter loc) (:value loc))))

    ;; Forward and backward Fibonacci steps
    (let [spiral-letters (atom [(nth torah center)])]
      (doseq [[i f] (map-indexed vector fibs)]
        (let [pos-fwd (+ center f)
              pos-bwd (- center f)]
          ;; Forward
          (when (< pos-fwd n)
            (let [loc (locate-torah-pos books pos-fwd)]
              (swap! spiral-letters conj (nth torah pos-fwd))
              (println (format "  +%-5d  %,8d  %-12s %3d  %s (%d)"
                               f pos-fwd (:book loc) (:chapter loc)
                               (:letter loc) (:value loc)))))
          ;; Backward
          (when (>= pos-bwd 0)
            (let [loc (locate-torah-pos books pos-bwd)]
              (swap! spiral-letters conj (nth torah pos-bwd))
              (println (format "  -%-5d  %,8d  %-12s %3d  %s (%d)"
                               f pos-bwd (:book loc) (:chapter loc)
                               (:letter loc) (:value loc)))))))

      ;; Collected letters
      (println (format "\n  Spiral letters: %s" (apply str @spiral-letters)))
      (println (format "  Spiral gematria sum: %,d"
                       (reduce + (map g/letter-value @spiral-letters)))))))

;; ── 12-fold partition ────────────────────────────────────────

(defn twelve-fold-partition [torah]
  (println "\n=== 12-fold Partition of the Torah ===")
  (let [n      (count torah)
        seg    (quot n 12)
        _      (println (format "  Total letters: %,d" n))
        _      (println (format "  Segment size (n/12): %,d" seg))]

    (println (format "\n  %-4s  %8s  %8s  %10s  %8s  %8s"
                     "Seg" "Start" "End" "Gematria" "Mean" "Letter@start"))
    (println (apply str (repeat 58 "-")))

    (let [segments (for [i (range 12)]
                     (let [start (* i seg)
                           end   (min (+ start seg) n)
                           chunk (subvec torah start end)
                           total (g/total chunk)
                           mean  (/ (double total) (count chunk))]
                       {:segment    (inc i)
                        :start      start
                        :end        end
                        :total      total
                        :mean       mean
                        :start-char (nth torah start)}))]
      (doseq [{:keys [segment start end total mean start-char]} segments]
        (println (format "  %3d   %,8d  %,8d  %,10d  %8.2f  %s"
                         segment start end total mean start-char)))

      ;; Check: are segment energies roughly equal?
      (let [energies (mapv :total segments)
            e-mean   (/ (double (reduce + energies)) 12)
            e-std    (Math/sqrt (/ (reduce + (map #(Math/pow (- % e-mean) 2) energies)) 12.0))]
        (println (format "\n  Segment energy mean: %,.0f" e-mean))
        (println (format "  Segment energy std:  %,.0f  (CV = %.3f)" e-std (/ e-std e-mean))))

      ;; Complementary pairs (1+12, 2+11, 3+10, etc.)
      (println "\n  Complementary pairs (segment i + segment 13-i):")
      (doseq [i (range 6)]
        (let [a (nth segments i)
              b (nth segments (- 11 i))]
          (println (format "    Seg %2d + Seg %2d: %,10d + %,10d = %,10d"
                           (:segment a) (:segment b)
                           (:total a) (:total b)
                           (+ (:total a) (:total b)))))))))

;; ── Division by 2, 3, 4, 6 ──────────────────────────────────

(defn divisor-partitions [torah]
  (println "\n=== Torah Partitioned by Divisors of 12 ===")
  (let [n (count torah)]
    (doseq [d [2 3 4 6 7 12]]
      (let [seg (quot n d)
            sums (for [i (range d)]
                   (let [start (* i seg)
                         end   (min (+ start seg) n)]
                     (g/total (subvec torah start end))))]
        (println (format "\n  Divided by %d (segment = %,d letters):" d seg))
        (doseq [[i s] (map-indexed vector sums)]
          (println (format "    Part %d: %,10d" (inc i) s)))
        (let [s-mean (/ (double (reduce + sums)) d)
              s-std  (Math/sqrt (/ (reduce + (map #(Math/pow (- % s-mean) 2) sums)) (double d)))]
          (println (format "    CV = %.4f" (/ s-std s-mean))))))))

;; ── Urim & Thummim: 4×3 matrix of the Torah ─────────────────

(defn urim-matrix [torah]
  (println "\n=== Urim & Thummim: 4×3 Matrix ===")
  (println "  12 segments arranged as 4 rows × 3 columns")
  (let [n   (count torah)
        seg (quot n 12)]
    ;; Compute gematria for each of 12 segments
    (let [energies (vec (for [i (range 12)]
                          (let [start (* i seg)
                                end   (min (+ start seg) n)]
                            (g/total (subvec torah start end)))))
          ;; Arrange as 4×3 matrix (row-major)
          matrix (partition 3 energies)]
      (println (format "\n  Segment size: %,d letters\n" seg))
      (println (format "  %12s  %12s  %12s  │ %12s" "Col 1" "Col 2" "Col 3" "Row Sum"))
      (println (apply str (repeat 58 "-")))
      (doseq [[row-idx row] (map-indexed vector matrix)]
        (let [row-sum (reduce + row)]
          (println (format "  %,12d  %,12d  %,12d  │ %,12d"
                           (nth row 0) (nth row 1) (nth row 2) row-sum))))
      (println (apply str (repeat 58 "-")))
      ;; Column sums
      (let [col-sums (for [c (range 3)]
                       (reduce + (map #(nth % c) matrix)))]
        (println (format "  %,12d  %,12d  %,12d  │ %,12d"
                         (nth col-sums 0) (nth col-sums 1) (nth col-sums 2)
                         (reduce + col-sums))))

      ;; Diagonal sums
      (println "\n  Diagonals:")
      (let [flat energies
            ;; Main diagonal of 4×3: (0,0) (1,1) (2,2) — only 3 entries
            main-diag [(nth flat 0) (nth flat 4) (nth flat 8)]
            ;; Anti-diagonal: (0,2) (1,1) (2,0)
            anti-diag [(nth flat 2) (nth flat 4) (nth flat 6)]]
        (println (format "    Main: %,d + %,d + %,d = %,d"
                         (nth main-diag 0) (nth main-diag 1) (nth main-diag 2)
                         (reduce + main-diag)))
        (println (format "    Anti: %,d + %,d + %,d = %,d"
                         (nth anti-diag 0) (nth anti-diag 1) (nth anti-diag 2)
                         (reduce + anti-diag))))

      ;; Check mod 7
      (println "\n  Segment gematria mod 7:")
      (doseq [[i e] (map-indexed vector energies)]
        (println (format "    Seg %2d: %,10d  mod 7 = %d" (inc i) e (mod e 7)))))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (let [{:keys [torah books]} (load-torah)]
    (spiral-from-center books torah)
    (twelve-fold-partition torah)
    (divisor-partitions torah)
    (urim-matrix torah))
  (println "\nDone."))
