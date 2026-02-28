(ns experiments.001-gematria-stats
  "First gematria experiment — per-book statistics, moments, centers, spectrum.
   Run: clojure -M:dev -m experiments.001-gematria-stats"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.locate :as locate]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
(def book-sizes [78364 63857 44980 63846 55222])

(defn locate-torah-pos [torah pos]
  (loop [p pos i 0]
    (let [sz (nth book-sizes i)]
      (if (< p sz)
        (let [ch-map (locate/chapter-map (nth book-names i))
              loc    (locate/locate (nth book-names i) ch-map p)]
          {:book (:book loc) :chapter (:chapter loc)
           :letter (nth torah (int pos))
           :value (g/letter-value (nth torah (int pos)))})
        (recur (- p sz) (inc i))))))

(defn moments [letters]
  (let [vals (g/stream-values letters)
        n    (count vals)
        mean (/ (double (reduce + vals)) n)
        m2   (/ (reduce + (map #(Math/pow (- % mean) 2) vals)) (double n))
        m3   (/ (reduce + (map #(Math/pow (- % mean) 3) vals)) (double n))
        m4   (/ (reduce + (map #(Math/pow (- % mean) 4) vals)) (double n))]
    {:mean     mean
     :variance m2
     :std-dev  (Math/sqrt m2)
     :skewness (/ m3 (Math/pow m2 1.5))
     :kurtosis (/ m4 (Math/pow m2 2.0))
     :excess-kurtosis (- (/ m4 (Math/pow m2 2.0)) 3.0)}))

(defn -main []
  ;; Load Torah
  (println "Loading Torah...")
  (def torah (vec (mapcat sefaria/book-letters book-names)))
  (def n (count torah))
  (def gvals (g/stream-values torah))
  (def phi 1.6180339887)

  ;; ── Per-book statistics ──────────────────────────────────
  (println "\n=== Per-Book Statistics ===")
  (doseq [book book-names]
    (g/print-stats book (sefaria/book-letters book)))
  (g/print-stats "TOTAL TORAH" torah)

  ;; ── Moments ──────────────────────────────────────────────
  (println "\n=== Moments ===")
  (let [m (moments torah)]
    (println (format "  Skewness:        %.4f" (:skewness m)))
    (println (format "  Excess kurtosis: %.4f" (:excess-kurtosis m))))

  (println "\n=== Per-Book Moments ===")
  (println (format "  %-12s  %6s  %7s  %7s" "Book" "Mean" "Skew" "ExKurt"))
  (println (apply str (repeat 42 "-")))
  (doseq [book book-names]
    (let [m (moments (sefaria/book-letters book))]
      (println (format "  %-12s  %6.2f  %7.4f  %7.4f"
                       book (:mean m) (:skewness m) (:excess-kurtosis m)))))

  ;; ── Centers ──────────────────────────────────────────────
  (println "\n=== Centers ===")
  (let [sum      (long (reduce + gvals))
        geo-ctr  (quot n 2)
        phi-pos  (long (/ n phi))
        ;; Energy midpoint
        energy-mid (loop [acc 0 i 0]
                     (let [s (+ acc (nth gvals i))]
                       (if (>= s (quot sum 2)) i (recur s (inc i)))))
        ;; Center of mass
        com (long (/ (double (reduce + (map-indexed (fn [i v] (* (long i) (long v))) gvals)))
                     (double sum)))]
    (println (format "  Geometric (n/2):   %,7d → %s" geo-ctr (locate-torah-pos torah geo-ctr)))
    (println (format "  Energy (Σ/2):      %,7d → %s" energy-mid (locate-torah-pos torah energy-mid)))
    (println (format "  Center of mass:    %,7d → %s" com (locate-torah-pos torah com)))
    (println (format "  Golden (n/φ):      %,7d → %s" phi-pos (locate-torah-pos torah phi-pos))))

  ;; ── Spectrum ─────────────────────────────────────────────
  (println "\n=== Eigenspectrum ===")
  (g/print-spectrum torah)

  ;; ── Key word values ──────────────────────────────────────
  (println "\n=== Key Word Gematria ===")
  (doseq [[word label] [["יהוה" "YHWH"] ["תורה" "Torah"] ["אלהים" "Elohim"]
                         ["הטבע" "Nature"] ["משיח" "Messiah"] ["דוד" "David"]
                         ["ישוע" "Yeshua"] ["משה" "Moses"] ["את" "Aleph-Tav"]
                         ["שלום" "Shalom"] ["אמת" "Truth"]]]
    (println (format "  %s (%s) = %d" word label (g/word-value word))))

  ;; ── Fibonacci positions ──────────────────────────────────
  (println "\n=== Fibonacci Positions ===")
  (let [fibs (take-while #(< % n)
               (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))]
    (doseq [f fibs]
      (println (format "  fib %-7d → %s" f (locate-torah-pos torah f))))
    (println (format "\n  Fibonacci letters: %s" (apply str (map #(nth torah %) fibs))))
    (println (format "  Gematria sum: %d" (reduce + (map #(g/letter-value (nth torah %)) fibs)))))

  ;; ── Genesis 1:1 ──────────────────────────────────────────
  (println "\n=== Genesis 1:1 ===")
  (doseq [[word label] [["בראשית" "bereshit"] ["ברא" "bara"] ["אלהים" "elohim"]
                         ["את" "et"] ["השמים" "hashamayim"] ["ואת" "ve'et"] ["הארץ" "ha'aretz"]]]
    (println (format "  %s (%s) = %d" word label (g/word-value word))))
  (println (format "  Total = %d = %d × %d" 2701 37 73))

  (println "\nDone."))
