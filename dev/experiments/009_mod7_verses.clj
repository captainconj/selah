(ns experiments.009-mod7-verses
  "Running sum mod 7 at verse boundaries.
   Where does the cumulative gematria equal 0 mod 7?
   Run: clojure -M:dev -m experiments.009-mod7-verses"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn verse-gematria-map
  "Build a map of every verse in the Torah with its gematria and running sum.
   Returns vec of {:book :chapter :verse :letters :gematria :running-sum :mod7}."
  []
  (let [results (atom [])
        running (atom 0)]
    (doseq [book book-names]
      (let [chapters (get sefaria/book-chapters book)]
        (doseq [ch (range 1 (inc chapters))]
          (let [verses (sefaria/fetch-chapter book ch)]
            (doseq [[v-idx verse-text] (map-indexed vector verses)]
              (let [raw     (norm/strip-html verse-text)
                    letters (norm/letter-stream raw)
                    gval    (g/total letters)
                    new-sum (+ @running gval)]
                (swap! running (constantly new-sum))
                (swap! results conj
                       {:book      book
                        :chapter   ch
                        :verse     (inc v-idx)
                        :letters   (count letters)
                        :gematria  gval
                        :running   new-sum
                        :mod7      (mod new-sum 7)})))))))
    @results))

(defn -main []
  (println "=== Running Sum Mod 7 at Verse Boundaries ===\n")
  (println "Building verse map...")
  (let [verses (verse-gematria-map)
        total  (count verses)]
    (println (format "  %,d verses total\n" total))

    ;; Verses where running sum mod 7 = 0
    (let [zero-crossings (filter #(zero? (:mod7 %)) verses)
          n-zeros        (count zero-crossings)]
      (println (format "  Verses where Σ mod 7 = 0: %,d out of %,d (%.1f%%)"
                       n-zeros total (* 100.0 (/ (double n-zeros) total))))
      (println (format "  Expected (uniform): %.0f (14.3%%)" (/ (double total) 7)))

      ;; First 20 zero-crossings
      (println "\n  First 20 zero-crossings:")
      (println (format "  %-12s  %3s  %3s  %10s  %8s"
                       "Book" "Ch" "V" "Σ" "Verse Σ"))
      (println (apply str (repeat 42 "-")))
      (doseq [v (take 20 zero-crossings)]
        (println (format "  %-12s  %3d  %3d  %,10d  %,8d"
                         (:book v) (:chapter v) (:verse v)
                         (:running v) (:gematria v))))

      ;; Distribution of mod 7 residues across all verses
      (println "\n=== Mod 7 Residue Distribution ===")
      (let [by-mod (frequencies (map :mod7 verses))]
        (doseq [m (range 7)]
          (let [cnt (get by-mod m 0)]
            (println (format "  mod 7 = %d: %,4d verses (%.1f%%)"
                             m cnt (* 100.0 (/ (double cnt) total))))))))

    ;; Verse gematria — which verses have gematria divisible by 7?
    (println "\n=== Verse Gematria Divisible by 7 ===")
    (let [div7 (filter #(zero? (mod (:gematria %) 7)) verses)
          n-div7 (count div7)]
      (println (format "  Verses with gematria ≡ 0 (mod 7): %,d out of %,d (%.1f%%)"
                       n-div7 total (* 100.0 (/ (double n-div7) total))))
      (println (format "  Expected (random): %.0f (14.3%%)" (/ (double total) 7)))

      (println "\n  First 15 such verses:")
      (doseq [v (take 15 div7)]
        (println (format "  %-12s %2d:%2d  gematria = %,d"
                         (:book v) (:chapter v) (:verse v) (:gematria v)))))

    ;; Genesis 1:1 gematria = 2701 = 37 × 73. Check mod 7:
    (let [gen1:1 (first verses)]
      (println (format "\n  Genesis 1:1: gematria = %,d, mod 7 = %d"
                       (:gematria gen1:1) (mod (:gematria gen1:1) 7))))

    ;; Chapter-end running sums mod 7
    (println "\n=== Chapter-End Running Sums Mod 7 ===")
    (println "  (Which chapters end with cumulative gematria ≡ 0 mod 7?)\n")
    (let [chapter-ends (->> verses
                            (partition-by (juxt :book :chapter))
                            (map last))
          zero-chapters (filter #(zero? (:mod7 %)) chapter-ends)]
      (println (format "  Chapters ending at Σ ≡ 0 (mod 7): %d out of %d"
                       (count zero-chapters) (count chapter-ends)))
      (println)
      (doseq [v zero-chapters]
        (println (format "    %-12s chapter %2d  (Σ = %,d)"
                         (:book v) (:chapter v) (:running v)))))

    ;; Verse with the highest individual gematria
    (println "\n=== Extreme Verses ===")
    (let [sorted-desc (reverse (sort-by :gematria verses))]
      (println "  Highest gematria per verse:")
      (doseq [v (take 10 sorted-desc)]
        (println (format "    %-12s %2d:%2d  gematria = %,d (%d letters)"
                         (:book v) (:chapter v) (:verse v)
                         (:gematria v) (:letters v)))))

    (let [sorted-asc (sort-by :gematria verses)]
      (println "\n  Lowest gematria per verse:")
      (doseq [v (take 10 sorted-asc)]
        (println (format "    %-12s %2d:%2d  gematria = %,d (%d letters)"
                         (:book v) (:chapter v) (:verse v)
                         (:gematria v) (:letters v))))))

  (println "\nDone."))
