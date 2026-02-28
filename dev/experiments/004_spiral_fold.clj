(ns experiments.004-spiral-fold
  "Fibonacci spiral fold on the Torah cylinder.
   Given circumference C, the spiral traces positions mod C.
   What circumferences produce interesting patterns?
   Run: clojure -M:dev -m experiments.004-spiral-fold"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.locate :as locate]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn load-torah []
  (let [torah (vec (mapcat sefaria/book-letters book-names))]
    (println (format "Torah: %,d letters" (count torah)))
    torah))

(defn fibonacci-seq []
  (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))

;; ── Spiral fold on cylinder ─────────────────────────────────

(defn spiral-fold
  "From center, step out in Fibonacci increments.
   On a cylinder of circumference C, each position wraps.
   Returns the column (mod C) of each spiral position."
  [center n circumference num-steps]
  (let [fibs (take num-steps (fibonacci-seq))]
    (concat [{:step 0 :pos center :col (mod center circumference)}]
            (mapcat (fn [f]
                      (let [fwd (+ center f)
                            bwd (- center f)]
                        (concat
                         (when (< fwd n)
                           [{:step f :pos fwd :col (mod fwd circumference)}])
                         (when (>= bwd 0)
                           [{:step (- f) :pos bwd :col (mod bwd circumference)}]))))
                    fibs))))

(defn analyze-circumference
  "For a given circumference, analyze the spiral fold columns."
  [torah center circumference num-steps]
  (let [n     (count torah)
        steps (spiral-fold center n circumference num-steps)
        cols  (mapv :col steps)
        ;; How many distinct columns are visited?
        distinct-cols (count (distinct cols))
        ;; What letters appear at these positions?
        letters (mapv #(nth torah (:pos %)) steps)
        ;; Column distribution
        col-freqs (frequencies cols)]
    {:circumference   circumference
     :steps           (count steps)
     :distinct-cols   distinct-cols
     :coverage        (/ (double distinct-cols) circumference)
     :letters         letters
     :col-frequencies col-freqs}))

;; ── Scribal dimensions ───────────────────────────────────────

(defn test-circumferences [torah]
  (println "\n=== Spiral Fold — Testing Circumferences ===")
  (let [center (quot (count torah) 2)
        ;; Traditional scribal dimensions from the Talmud:
        ;; A standard Torah scroll column is ~42 lines, ~30 letters per line
        ;; but there's variation. Key numbers to test:
        candidates [7 12 22 26 27 30 42 49 50 60 72 100
                    ;; 42 lines × 30 chars = 1260 letters per column
                    ;; 248 columns in a Torah scroll (traditional)
                    ;; 304,805 letters / 248 columns ≈ 1229 letters per column
                    248
                    ;; n / num_columns for different column counts
                    (quot (count torah) 245)
                    (quot (count torah) 248)
                    (quot (count torah) 256)]]
    (println (format "  Center: %,d" center))
    (println (format "\n  %-8s  %6s  %6s  %8s" "Circ" "Steps" "Cols" "Coverage"))
    (println (apply str (repeat 36 "-")))
    (doseq [c candidates]
      (let [result (analyze-circumference torah center c 30)]
        (println (format "  %-8d  %6d  %6d  %8.3f"
                         c (:steps result) (:distinct-cols result) (:coverage result)))))))

;; ── Deep analysis at key circumferences ──────────────────────

(defn deep-spiral [torah circumference label]
  (println (format "\n=== Spiral at Circumference %d (%s) ===" circumference label))
  (let [center (quot (count torah) 2)
        n      (count torah)
        steps  (spiral-fold center n circumference 25)]
    (println (format "  %-6s  %8s  %5s  %s  %s"
                     "Step" "Position" "Col" "Letter" "Value"))
    (println (apply str (repeat 40 "-")))
    (doseq [{:keys [step pos col]} steps]
      (let [ch (nth torah pos)
            v  (g/letter-value ch)]
        (println (format "  %+6d  %,8d  %5d  %s     %d"
                         step pos col ch v))))
    ;; The collected letters
    (let [letters (mapv #(nth torah (:pos %)) steps)]
      (println (format "\n  Letters: %s" (apply str letters)))
      (println (format "  Gematria: %,d" (reduce + (map g/letter-value letters)))))))

;; ── Column analysis — what words form vertically? ────────────

(defn column-at [torah circumference col-idx num-rows]
  "Extract a vertical column from the Torah wrapped at given circumference."
  (let [n (count torah)]
    (for [row (range num-rows)
          :let [pos (+ col-idx (* row circumference))]
          :when (< pos n)]
      {:row row :pos pos :letter (nth torah pos) :value (g/letter-value (nth torah pos))})))

(defn scan-vertical-words [torah circumference]
  (println (format "\n=== Vertical Column Scan at Width %d ===" circumference))
  (let [n        (count torah)
        num-rows (quot n circumference)
        ;; Check a few columns near the center
        center-col (mod (quot n 2) circumference)]
    (println (format "  Grid: %d columns × %d rows" circumference num-rows))
    (println (format "  Center column: %d" center-col))

    ;; Show center column and neighbors
    (doseq [col [(dec center-col) center-col (inc center-col)]]
      (let [entries (column-at torah circumference col 20)]
        (println (format "\n  Column %d (first 20 rows):" col))
        (println (format "  Letters: %s" (apply str (map :letter entries))))
        (println (format "  Values:  %s" (mapv :value entries)))))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (let [torah (load-torah)]
    (test-circumferences torah)
    (deep-spiral torah 42 "lines per column")
    (deep-spiral torah 50 "Tabernacle dimension")
    (deep-spiral torah 22 "Hebrew alphabet size")
    (scan-vertical-words torah 50))
  (println "\nDone."))
