(ns experiments.006-aleph-tav
  "H7: את (aleph-tav) count and distribution in the Torah.
   Searches for the word את in the verse-level Hebrew text.
   Run: clojure -M:dev -m experiments.006-aleph-tav"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn extract-words
  "Extract Hebrew words from a verse string (strip HTML, nikkud, split on spaces/maqaf)."
  [verse-str]
  (let [stripped (norm/strip-html verse-str)]
    (->> (clojure.string/split stripped #"[\s\u05BE]+")  ;; space or maqaf (Hebrew hyphen)
         (mapv (fn [w] (apply str (filter norm/hebrew-letter? w))))
         (filterv #(pos? (count %))))))

(defn find-et-occurrences
  "Find all את occurrences in a book, tracking position in letter stream."
  [book-name]
  (let [chapters (get sefaria/book-chapters book-name)
        results  (atom [])
        letter-offset (atom 0)]
    (doseq [ch (range 1 (inc chapters))]
      (let [verses (sefaria/fetch-chapter book-name ch)]
        (doseq [[v-idx verse] (map-indexed vector verses)]
          (let [words (extract-words verse)]
            (doseq [[w-idx word] (map-indexed vector words)]
              ;; Track letter position in stream
              (when (= word "את")
                (swap! results conj
                       {:book    book-name
                        :chapter ch
                        :verse   (inc v-idx)
                        :word    (inc w-idx)
                        :stream-pos @letter-offset}))
              ;; Advance letter offset
              (swap! letter-offset + (count (norm/letter-stream word))))))))
    @results))

(defn find-et-global
  "Find all את and ואת in the entire Torah with global stream positions."
  []
  (let [results  (atom [])
        global-offset (atom 0)]
    (doseq [book book-names]
      (let [chapters (get sefaria/book-chapters book)]
        (doseq [ch (range 1 (inc chapters))]
          (let [verses (sefaria/fetch-chapter book ch)]
            (doseq [[v-idx verse] (map-indexed vector verses)]
              (let [words (extract-words verse)]
                (doseq [[w-idx word] (map-indexed vector words)]
                  (let [form (cond
                               (= word "את")  :et
                               (= word "ואת") :ve-et
                               :else nil)]
                    (when form
                      (swap! results conj
                             {:book       book
                              :chapter    ch
                              :verse      (inc v-idx)
                              :word       (inc w-idx)
                              :form       form
                              :stream-pos @global-offset})))
                  (swap! global-offset + (count (norm/letter-stream word))))))))))
    @results))

(defn -main []
  (println "=== H7: את (Aleph-Tav) Count and Distribution ===\n")

  (println "Counting את and ואת occurrences...")
  (let [all-results (find-et-global)
        et-only  (filter #(= (:form %) :et) all-results)
        vet-only (filter #(= (:form %) :ve-et) all-results)]

    (println (format "  את alone: %,d" (count et-only)))
    (println (format "  ואת:      %,d" (count vet-only)))
    (println (format "  Combined: %,d" (count all-results)))

    ;; Per-book breakdown
    (println "\n=== Per-Book Breakdown ===")
    (println (format "  %-12s  %5s  %5s  %5s" "Book" "את" "ואת" "Total"))
    (println (apply str (repeat 34 "-")))
    (doseq [book book-names]
      (let [book-hits (filter #(= (:book %) book) all-results)
            et-n  (count (filter #(= (:form %) :et) book-hits))
            vet-n (count (filter #(= (:form %) :ve-et) book-hits))]
        (println (format "  %-12s  %5d  %5d  %5d" book et-n vet-n (+ et-n vet-n)))))

    ;; Number properties
    (let [total (count all-results)
          et-total (count et-only)]
      (println (format "\n  את count: %d" et-total))
      (println (format "    mod 7  = %d" (mod et-total 7)))
      (println (format "    mod 12 = %d" (mod et-total 12)))
      (println (format "  Combined count: %d" total))
      (println (format "    mod 7  = %d" (mod total 7)))
      (println (format "    mod 12 = %d" (mod total 12)))
      (println (format "\n  Gematria: את = %d, ואת = %d" (g/word-value "את") (g/word-value "ואת"))))

    ;; Per-chapter distribution — top 20
    (println "\n=== Chapters With Most את+ואת ===")
    (let [by-chapter (->> all-results
                          (group-by (juxt :book :chapter))
                          (map (fn [[[book ch] hits]]
                                 {:book book :chapter ch :count (count hits)}))
                          (sort-by :count)
                          reverse)]
      (println (format "  %-12s  %3s  %5s" "Book" "Ch" "Count"))
      (println (apply str (repeat 24 "-")))
      (doseq [{:keys [book chapter count]} (take 20 by-chapter)]
        (println (format "  %-12s  %3d  %5d" book chapter count))))

    ;; Genesis 1:1
    (println "\n=== Genesis 1:1 — את markers ===")
    (let [gen1-hits (filter #(and (= (:book %) "Genesis")
                                   (= (:chapter %) 1)
                                   (= (:verse %) 1))
                             all-results)]
      (println (format "  Occurrences in Genesis 1:1: %d" (count gen1-hits)))
      (doseq [hit gen1-hits]
        (println (format "    %s at word %d, stream pos %,d"
                         (name (:form hit)) (:word hit) (:stream-pos hit)))))

    ;; Spacing between consecutive את (global positions)
    (println "\n=== Spacing Between Consecutive את+ואת ===")
    (let [positions (mapv :stream-pos all-results)
          spacings  (mapv (fn [i] (- (nth positions (inc i)) (nth positions i)))
                          (range (dec (count positions))))
          pos-spacings (filterv pos? spacings)  ;; safety
          s-mean    (/ (double (reduce + pos-spacings)) (count pos-spacings))
          s-median  (nth (sort pos-spacings) (quot (count pos-spacings) 2))
          s-min     (apply min pos-spacings)
          s-max     (apply max pos-spacings)]
      (println (format "  Mean spacing:   %.1f letters" s-mean))
      (println (format "  Median spacing: %d letters" s-median))
      (println (format "  Min spacing:    %d letters" s-min))
      (println (format "  Max spacing:    %d letters" s-max))
      (println (format "  306,269 / %d = %.1f letters per marker" (count all-results)
                       (/ 306269.0 (count all-results))))

      ;; Histogram
      (println "\n  Spacing histogram (bucket size = 10):")
      (let [buckets (->> pos-spacings
                         (group-by #(quot % 10))
                         (sort-by key))]
        (doseq [[bucket hits] (take 20 buckets)]
          (println (format "    %4d-%4d: %4d %s"
                           (* bucket 10) (+ (* bucket 10) 9)
                           (count hits)
                           (apply str (repeat (min 50 (count hits)) "█"))))))))

  (println "\nDone."))
