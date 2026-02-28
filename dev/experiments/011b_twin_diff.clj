(ns experiments.011b-twin-diff
  "Where do the 11 extra letters in Exodus live?
   Chapter-by-chapter and verse-by-verse comparison of the twins.
   Run: clojure -M:dev -m experiments.011b-twin-diff"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(defn chapter-letter-counts
  "Get letter count per chapter for a book."
  [book-name]
  (let [chapters (get sefaria/book-chapters book-name)]
    (mapv (fn [ch]
            (let [verses (sefaria/fetch-chapter book-name ch)
                  raw    (apply str (map norm/strip-html verses))
                  letters (norm/letter-stream raw)]
              {:book book-name :chapter ch
               :letters (count letters)
               :gematria (g/total letters)}))
          (range 1 (inc chapters)))))

(defn -main []
  (println "=== The 11-Letter Difference: Exodus vs Numbers ===\n")

  (let [exod-chapters (chapter-letter-counts "Exodus")
        num-chapters  (chapter-letter-counts "Numbers")]

    ;; Chapter count comparison
    (println (format "  Exodus:  %d chapters" (count exod-chapters)))
    (println (format "  Numbers: %d chapters\n" (count num-chapters)))

    ;; Side by side — chapter letter counts
    (println "=== Chapter-by-Chapter Letter Counts ===\n")
    (println (format "  %3s  %8s  %8s  %6s  %8s  %8s  %6s"
                     "Ch" "Ex Ltrs" "Ex Gem" "" "Nu Ltrs" "Nu Gem" ""))
    (println (apply str (repeat 60 "-")))

    ;; Pair chapters (Exodus has 40, Numbers has 36)
    (let [max-ch (max (count exod-chapters) (count num-chapters))]
      (doseq [i (range max-ch)]
        (let [ex (when (< i (count exod-chapters)) (nth exod-chapters i))
              nu (when (< i (count num-chapters)) (nth num-chapters i))]
          (println (format "  %3d  %8s  %8s  %6s  %8s  %8s"
                           (inc i)
                           (if ex (format "%,d" (:letters ex)) "—")
                           (if ex (format "%,d" (:gematria ex)) "—")
                           ""
                           (if nu (format "%,d" (:letters nu)) "—")
                           (if nu (format "%,d" (:gematria nu)) "—"))))))

    ;; Total check
    (let [ex-total (reduce + (map :letters exod-chapters))
          nu-total (reduce + (map :letters num-chapters))]
      (println (format "\n  Exodus total:  %,d letters" ex-total))
      (println (format "  Numbers total: %,d letters" nu-total))
      (println (format "  Difference:    %d letters" (- ex-total nu-total))))

    ;; Exodus has 40 chapters, Numbers has 36. That's 4 extra chapters.
    ;; What if we align them differently? Or look at total letters in the
    ;; extra 4 chapters?
    (println "\n=== Exodus's Extra 4 Chapters (37-40) ===")
    (let [extra (subvec exod-chapters 36 40)
          extra-total (reduce + (map :letters extra))]
      (doseq [{:keys [chapter letters gematria]} extra]
        (println (format "  Exodus %d: %,d letters, gematria %,d"
                         chapter letters gematria)))
      (println (format "  Total in extra chapters: %,d letters" extra-total)))

    ;; First 36 chapters comparison
    (println "\n=== First 36 Chapters Side-by-Side ===")
    (let [ex36 (subvec exod-chapters 0 36)
          nu36 num-chapters
          ex36-total (reduce + (map :letters ex36))
          nu36-total (reduce + (map :letters nu36))
          diff (- ex36-total nu36-total)]
      (println (format "  Exodus 1-36:  %,d letters" ex36-total))
      (println (format "  Numbers 1-36: %,d letters" nu36-total))
      (println (format "  Difference:   %,d letters (%s has more)"
                       (Math/abs diff) (if (pos? diff) "Exodus" "Numbers"))))

    ;; Word length distributions
    (println "\n=== Word Length Distribution ===")
    (doseq [book ["Exodus" "Numbers"]]
      (let [chapters (get sefaria/book-chapters book)
            all-words (atom [])]
        (doseq [ch (range 1 (inc chapters))]
          (let [verses (sefaria/fetch-chapter book ch)]
            (doseq [verse verses]
              (let [stripped (norm/strip-html verse)
                    words (->> (clojure.string/split stripped #"[\s\u05BE]+")
                               (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                               (filter #(pos? (count %))))]
                (swap! all-words into words)))))
        (let [lengths (map count @all-words)
              by-len  (frequencies lengths)
              total   (count lengths)]
          (println (format "\n  %s — %,d words:" book total))
          (println (format "    %4s  %6s  %6s" "Len" "Count" "Freq"))
          (doseq [l (sort (keys by-len))]
            (println (format "    %4d  %6d  %5.2f%%" l (get by-len l)
                             (* 100.0 (/ (double (get by-len l)) total)))))
          (println (format "    Mean word length: %.2f"
                           (/ (double (reduce + lengths)) total))))))

    ;; Gematria comparison
    (println "\n=== Gematria Comparison ===")
    (let [ex-gem (reduce + (map :gematria exod-chapters))
          nu-gem (reduce + (map :gematria num-chapters))]
      (println (format "  Exodus gematria:  %,d" ex-gem))
      (println (format "  Numbers gematria: %,d" nu-gem))
      (println (format "  Difference:       %,d" (- ex-gem nu-gem)))
      (println (format "  Diff mod 7: %d" (mod (- ex-gem nu-gem) 7)))
      (println (format "  Diff mod 11: %d" (mod (- ex-gem nu-gem) 11)))))

  (println "\nDone."))
