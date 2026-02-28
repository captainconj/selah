(ns experiments.011-eleven-letters
  "The 11-letter difference between Exodus and Numbers.
   Find all 11-letter words in Exodus with their context.
   Run: clojure -M:dev -m experiments.011-eleven-letters"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(defn extract-words-from-verse
  "Extract Hebrew words from a verse string, returning normalized letter-only forms."
  [verse-str]
  (let [stripped (norm/strip-html verse-str)]
    (->> (clojure.string/split stripped #"[\s\u05BE]+")
         (mapv (fn [w] (apply str (filter norm/hebrew-letter? w))))
         (filterv #(pos? (count %))))))

(defn find-n-letter-words
  "Find all words of exactly n letters in a book. Returns vec of maps."
  [book-name n]
  (let [chapters (get sefaria/book-chapters book-name)
        results  (atom [])]
    (doseq [ch (range 1 (inc chapters))]
      (let [verses (sefaria/fetch-chapter book-name ch)]
        (doseq [[v-idx verse-text] (map-indexed vector verses)]
          (let [words (extract-words-from-verse verse-text)]
            (doseq [[w-idx word] (map-indexed vector words)]
              (when (= (count word) n)
                (swap! results conj
                       {:book    book-name
                        :chapter ch
                        :verse   (inc v-idx)
                        :word-pos (inc w-idx)
                        :word    word
                        :gematria (g/word-value word)})))))))
    @results))

(defn -main []
  (println "=== The 11-Letter Difference ===\n")

  ;; Verify the counts
  (let [exod-letters (sefaria/book-letters "Exodus")
        num-letters  (sefaria/book-letters "Numbers")]
    (println (format "  Exodus:  %,d letters" (count exod-letters)))
    (println (format "  Numbers: %,d letters" (count num-letters)))
    (println (format "  Difference: %d letters\n" (- (count exod-letters) (count num-letters)))))

  ;; Find all 11-letter words in Exodus
  (println "=== 11-Letter Words in Exodus ===\n")
  (let [words-11 (find-n-letter-words "Exodus" 11)]
    (println (format "  Found: %d words of exactly 11 letters\n" (count words-11)))
    (println (format "  %-8s  %-14s  %8s  %s"
                     "Location" "Word" "Gematria" ""))
    (println (apply str (repeat 50 "-")))
    (doseq [{:keys [chapter verse word gematria]} words-11]
      (println (format "  Ex %2d:%2d  %-14s  %8d" chapter verse word gematria)))

    ;; Unique words
    (let [unique (distinct (map :word words-11))]
      (println (format "\n  Unique 11-letter words: %d" (count unique)))
      (println)
      (doseq [w unique]
        (let [occurrences (filter #(= (:word %) w) words-11)]
          (println (format "  %s (gematria %d) — %d occurrences:"
                           w (g/word-value w) (count occurrences)))
          (doseq [{:keys [chapter verse]} occurrences]
            (println (format "    Exodus %d:%d" chapter verse)))))))

  ;; Also check Numbers for comparison
  (println "\n=== 11-Letter Words in Numbers ===\n")
  (let [words-11 (find-n-letter-words "Numbers" 11)]
    (println (format "  Found: %d words of exactly 11 letters\n" (count words-11)))
    (let [unique (distinct (map :word words-11))]
      (println (format "  Unique 11-letter words: %d" (count unique)))
      (println)
      (doseq [w unique]
        (let [occurrences (filter #(= (:word %) w) words-11)]
          (println (format "  %s (gematria %d) — %d occurrences:"
                           w (g/word-value w) (count occurrences)))
          (doseq [{:keys [chapter verse]} occurrences]
            (println (format "    Numbers %d:%d" chapter verse)))))))

  ;; All 5 books — how many 11-letter words in each?
  (println "\n=== 11-Letter Word Counts Per Book ===\n")
  (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
    (let [words (find-n-letter-words book 11)]
      (println (format "  %-12s: %3d words (%d unique)"
                       book (count words)
                       (count (distinct (map :word words)))))))

  (println "\nDone."))
