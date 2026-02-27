(ns selah.scan
  "Broad ELS scanning — search meaningful Hebrew words across symbolic skips."
  (:require [selah.els :as els]
            [selah.text.sefaria :as sefaria]
            [selah.stats :as stats]))

;; ── Symbolic skips ──────────────────────────────────────────────────

(def symbolic-skips
  "Skip values with theological significance in the text."
  {7  "Creation/Sabbath — days of creation, the rhythm of rest"
   12 "Tribes — the twelve sons of Israel"
   26 "Gematria of יהוה — י(10) + ה(5) + ו(6) + ה(5)"
   40 "Testing — days of flood, days on Sinai, years in wilderness"
   50 "Jubilee/Shavuot — 7×7+1, the giving of Torah"})

;; ── Word lexicon ────────────────────────────────────────────────────

(def word-lexicon
  "Meaningful Hebrew words to search for."
  [["תורה"   "Torah"     "The law/instruction"]
   ["יהוה"   "YHWH"      "The divine name, self-revealed at the burning bush"]
   ["אלהים"  "Elohim"    "God (plural of majesty)"]
   ["משה"    "Moses"     "The lawgiver, drawn from water"]
   ["אהרן"   "Aaron"     "The high priest"]
   ["ישראל"  "Israel"    "He who wrestles with God"]
   ["שבת"    "Sabbath"   "Rest, cessation — the seventh day"]
   ["ברית"   "Covenant"  "The binding agreement"]
   ["משיח"   "Messiah"   "The anointed one"]
   ["אדם"    "Adam"      "Man, humanity, from the earth"]
   ["דוד"    "David"     "Beloved — the king"]
   ["שלום"   "Peace"     "Wholeness, completeness"]
   ["אמת"    "Truth"     "Faithfulness, reliability"]
   ["חסד"    "Mercy"     "Lovingkindness, covenant loyalty"]
   ["קדש"    "Holy"      "Set apart, sacred"]
   ["גאל"    "Redeem"    "To buy back, to liberate"]
   ["ישוע"   "Yeshua"    "Salvation / Jesus"]
   ["חוה"    "Eve"       "Life-giver, mother of all living"]
   ["אברהם"  "Abraham"   "Father of many nations"]
   ["יצחק"   "Isaac"     "He laughs — the son of promise"]
   ["יעקב"   "Jacob"     "Supplanter, becomes Israel"]])

;; ── Scanning ────────────────────────────────────────────────────────

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn load-books
  "Load all five books as letter streams. Returns a map."
  []
  (into {} (map (fn [name] [name (sefaria/book-letters name)]) book-names)))

(defn scan-word-at-skip
  "Search for a word at a given skip (both forward and reverse) across all books.
   Returns a map of book -> {:forward [...] :reverse [...] :total n}."
  [books word skip]
  (into {}
    (map (fn [name]
           (let [stream  (get books name)
                 fwd     (els/search stream word skip)
                 rev     (els/search stream word (- skip))
                 fwd-v   (vec fwd)
                 rev-v   (vec rev)]
             [name {:forward fwd-v
                    :reverse rev-v
                    :total   (+ (count fwd-v) (count rev-v))}]))
         book-names)))

(defn scan-all
  "Run the full scan: all words × all symbolic skips × all books.
   Returns a seq of {:word :english :skip :skip-meaning :results}."
  [books]
  (for [skip   (sort (keys symbolic-skips))
        [word english desc] word-lexicon
        :let [results (scan-word-at-skip books word skip)
              total   (reduce + (map :total (vals results)))]
        :when (pos? total)]
    {:word         word
     :english      english
     :description  desc
     :skip         skip
     :skip-meaning (get symbolic-skips skip)
     :results      results
     :total        total}))

(defn first-hit-position
  "Find where the first hit lands in each book. Returns map of book -> start position."
  [scan-entry]
  (into {}
    (map (fn [[book {:keys [forward reverse]}]]
           (let [all-starts (concat (map :start forward) (map :start reverse))
                 earliest   (when (seq all-starts) (apply min all-starts))]
             [book earliest]))
         (:results scan-entry))))

(defn print-summary
  "Print a formatted summary of scan results."
  [scan-results]
  (println (format "%-10s %-8s %5s  %5s  %5s  %5s  %5s  %5s"
                   "" "" "Gen" "Exod" "Lev" "Num" "Deut" "Total"))
  (println (apply str (repeat 65 "-")))
  (doseq [skip (sort (keys symbolic-skips))]
    (let [entries (filter #(= skip (:skip %)) scan-results)]
      (when (seq entries)
        (println)
        (println (format "--- Skip %d: %s ---" skip (get symbolic-skips skip)))
        (doseq [{:keys [word english results total]} entries]
          (let [counts (mapv #(:total (get results %)) book-names)]
            (println (format "  %-8s %-8s %5d  %5d  %5d  %5d  %5d  %5d"
                             word english
                             (nth counts 0) (nth counts 1) (nth counts 2)
                             (nth counts 3) (nth counts 4) total))))))))

(defn notable-patterns
  "Filter scan results for patterns that seem structurally interesting:
   - Unusually high counts (> 2× expected)
   - Positional clustering (first hit near start/end)
   - Cross-book consistency"
  [books scan-results]
  (for [{:keys [word skip results total] :as entry} scan-results
        :let [expected-total (reduce + (map (fn [name]
                                              (stats/expected-hits
                                                (get books name) word skip))
                                            book-names))
              ratio (when (pos? expected-total) (/ total expected-total))
              positions (first-hit-position entry)
              books-with-hits (count (filter some? (vals positions)))]
        :when (or (and ratio (> ratio 2.0))
                  (= books-with-hits 5)
                  (some (fn [[_ pos]] (and pos (< pos 20))) positions))]
    (assoc entry
           :expected-total expected-total
           :ratio ratio
           :first-positions positions
           :books-with-hits books-with-hits)))

(comment
  ;; Full scan
  (def books (load-books))
  (def results (scan-all books))
  (print-summary results)

  ;; Find notable patterns
  (def notable (notable-patterns books results))
  (doseq [{:keys [word english skip ratio books-with-hits first-positions]} notable]
    (println (format "%s (%s) skip=%d  ratio=%.1f  books=%d  first-positions=%s"
                     word english skip (or ratio 0.0) books-with-hits first-positions)))
  )
