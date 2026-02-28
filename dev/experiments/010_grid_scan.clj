(ns experiments.010-grid-scan
  "H11/H12: Grid wrapping at width 50 and other dimensions.
   Scan vertical columns and diagonals for Hebrew words.
   Run: clojure -M:dev -m experiments.010-grid-scan"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [selah.els.engine :as els]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn extract-column
  "Extract a vertical column from text wrapped at given width.
   Returns a vector of characters."
  [torah width col num-rows]
  (let [n (count torah)]
    (vec (for [row (range num-rows)
               :let [pos (+ col (* row width))]
               :when (< pos n)]
           (nth torah pos)))))

(defn extract-diagonal
  "Extract a diagonal from text wrapped at given width.
   direction: :down-right (+1 col per row) or :down-left (-1 col per row)."
  [torah width start-col direction num-rows]
  (let [n   (count torah)
        step (if (= direction :down-right) 1 -1)]
    (vec (for [row (range num-rows)
               :let [col (mod (+ start-col (* row step)) width)
                     pos (+ col (* row width))]
               :when (< pos n)]
           (nth torah pos)))))

;; ── Word matching ────────────────────────────────────────────

(def search-words
  "Key Hebrew words to scan for in vertical columns."
  [["תורה" "Torah"]
   ["יהוה" "YHWH"]
   ["אלהים" "Elohim"]
   ["משה" "Moses"]
   ["ישראל" "Israel"]
   ["שלום" "Shalom"]
   ["אמת" "Truth"]
   ["חיים" "Life"]
   ["קדש" "Holy"]
   ["ברית" "Covenant"]
   ["אהבה" "Love"]
   ["דוד" "David"]
   ["משיח" "Messiah"]
   ["את" "Aleph-Tav"]])

(defn find-word-in-seq
  "Find all occurrences of a word (as char vector) in a sequence of chars.
   Returns start indices."
  [chars word]
  (let [wchars (vec (norm/letter-stream word))
        wlen   (count wchars)
        slen   (count chars)]
    (for [i (range (- slen wlen -1))
          :when (= (subvec (vec chars) i (+ i wlen)) wchars)]
      i)))

(defn scan-columns-for-words
  "Scan all vertical columns at given width for target words."
  [torah width words]
  (let [num-rows (quot (count torah) width)]
    (for [[hebrew label] words
          col (range width)
          :let [column (extract-column torah width col num-rows)
                hits   (find-word-in-seq column hebrew)]
          :when (seq hits)]
      {:word hebrew :label label :col col :width width
       :hits (count hits) :positions hits})))

(defn scan-diagonals-for-words
  "Scan all diagonals at given width for target words."
  [torah width words]
  (let [num-rows (quot (count torah) width)]
    (for [[hebrew label] words
          col (range width)
          dir [:down-right :down-left]
          :let [diag (extract-diagonal torah width col dir num-rows)
                hits (find-word-in-seq diag hebrew)]
          :when (seq hits)]
      {:word hebrew :label label :col col :direction dir :width width
       :hits (count hits)})))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "Loading Torah...")
  (let [torah (vec (mapcat sefaria/book-letters book-names))
        n     (count torah)]
    (println (format "  %,d letters\n" n))

    ;; Test multiple widths
    (doseq [width [7 12 22 26 42 49 50 72]]
      (println (format "=== Grid Width %d ===" width))
      (println (format "  %d columns × %d rows\n" width (quot n width)))

      ;; Vertical scan
      (let [v-results (scan-columns-for-words torah width search-words)]
        (if (seq v-results)
          (do
            (println (format "  Vertical hits:"))
            (doseq [{:keys [word label col hits]} v-results]
              (println (format "    %s (%s) in column %d: %d hits"
                               word label col hits))))
          (println "  No vertical word hits.")))

      ;; Diagonal scan
      (let [d-results (scan-diagonals-for-words torah width search-words)]
        (if (seq d-results)
          (do
            (println (format "  Diagonal hits:"))
            (doseq [{:keys [word label col direction hits]} d-results]
              (println (format "    %s (%s) col %d %s: %d hits"
                               word label col (name direction) hits))))
          (println "  No diagonal word hits.")))

      (println))

    ;; ── Compare: word density at symbolic vs control widths ──
    (println "=== Word Density Comparison ===")
    (println (format "  %-6s  %6s  %8s  %s" "Width" "V-hits" "D-hits" "Type"))
    (println (apply str (repeat 40 "-")))
    (doseq [[width label] [[7 "symbolic (days)"]
                            [8 "control"]
                            [12 "symbolic (tribes)"]
                            [13 "control"]
                            [22 "symbolic (letters)"]
                            [23 "control"]
                            [42 "symbolic (6×7)"]
                            [43 "control"]
                            [49 "symbolic (7²)"]
                            [50 "symbolic (Tabernacle)"]
                            [51 "control"]]]
      (let [v-count (reduce + (map :hits (scan-columns-for-words torah width search-words)))
            d-count (reduce + (map :hits (scan-diagonals-for-words torah width search-words)))]
        (println (format "  %-6d  %6d  %8d  %s" width v-count d-count label)))))

  (println "\nDone."))
