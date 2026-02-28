(ns experiments.033-the-word
  "The word level.
   We've studied letters. Now study words.
   79,847 words in the Torah (by Sefaria count).
   Word lengths, gematria distributions, palindromes.
   Run: clojure -M:dev -m experiments.033-the-word"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn -main []
  (println "=== THE WORD ===")
  (println "  Structure at the word level.\n")

  (println "Loading all words...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        all-words (atom [])
        _ (doseq [book books]
            (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
              (let [verses (sefaria/fetch-chapter book ch)]
                (doseq [v verses]
                  (let [stripped (norm/strip-html v)
                        raw-words (str/split stripped #"\s+")
                        heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                    (doseq [w heb-words]
                      (let [letters (norm/letter-stream w)]
                        (when (seq letters)
                          (swap! all-words conj
                                 {:text (apply str letters)
                                  :len (count letters)
                                  :gem (reduce + (map #(long (g/letter-value %)) letters))
                                  :book book})))))))))
        words (vec @all-words)
        n (count words)
        gems (mapv :gem words)
        total-gem (reduce + gems)]

    (println (format "  %,d words. Total gematria: %,d\n" n total-gem))

    ;; ── 1. Word length distribution ──────────────────────────
    (println "── 1. Word Length Distribution ──\n")

    (let [len-freqs (frequencies (map :len words))
          max-len (apply max (keys len-freqs))]
      (doseq [l (range 1 (inc (min 15 max-len)))]
        (let [ct (get len-freqs l 0)
              pct (* 100 (/ (double ct) n))
              bar (apply str (repeat (int (* pct 3)) "█"))]
          (println (format "  len=%2d  %,6d  %5.2f%%  %s" l ct pct bar))))
      (let [long-ct (reduce + (map second (filter #(> (first %) 15) len-freqs)))]
        (println (format "  len>15  %,6d  %5.2f%%" long-ct (* 100 (/ (double long-ct) n)))))

      (let [mean-len (/ (double (reduce + (map :len words))) n)]
        (println (format "\n  Mean word length: %.2f letters" mean-len))))

    ;; ── 2. Word gematria distribution ────────────────────────
    (println "\n── 2. Word Gematria Distribution ──\n")

    (let [mean-gem (/ (double total-gem) n)
          gem-sorted (sort gems)
          median-gem (nth gem-sorted (quot n 2))]
      (println (format "  Mean word gematria: %.1f" mean-gem))
      (println (format "  Median word gematria: %d" median-gem))
      (println (format "  Min: %d  Max: %,d" (first gem-sorted) (last gem-sorted)))

      ;; Key gematria values
      (println "\n  Words with key gematria values:")
      (doseq [target [7 26 37 73 111 130 222 333 441 2701]]
        (let [matching (filter #(= (:gem %) target) words)
              ct (count matching)
              examples (take 5 (distinct (map :text matching)))]
          (when (pos? ct)
            (println (format "    gem=%4d: %,4d words  examples: %s"
                             target ct (str/join ", " examples)))))))

    ;; ── 3. Word palindrome ───────────────────────────────────
    (println "\n── 3. Word-Level Palindrome ──")
    (println "  Is the word sequence palindromic?\n")

    (let [half (quot n 2)
          first-gems (mapv double (subvec gems 0 half))
          last-gems-rev (mapv double (vec (reverse (subvec gems half))))]
      (println (format "  Word gematria palindrome (first %,d vs rev last %,d): cos = %.6f"
                       half half (cosine-sim first-gems last-gems-rev))))

    ;; Chunked word palindrome (more robust)
    (let [n-chunks 100
          chunk-size (quot n n-chunks)
          chunk-sums (mapv (fn [i]
                             (let [start (* i chunk-size)
                                   end (min n (* (inc i) chunk-size))]
                               (double (reduce + (subvec gems start end)))))
                           (range n-chunks))
          half (quot n-chunks 2)
          first-cs (subvec chunk-sums 0 half)
          last-cs (vec (reverse (subvec chunk-sums half)))]
      (println (format "  Chunked (100 bins): cos = %.6f" (cosine-sim first-cs last-cs))))

    ;; 7-fold word division
    (let [seg-size (quot n 7)
          seg-sums (mapv (fn [i]
                           (let [start (* i seg-size)
                                 end (if (= i 6) n (* (inc i) seg-size))]
                             (double (reduce + (subvec gems start end)))))
                         (range 7))
          first3 (subvec seg-sums 0 3)
          last3-rev (vec (reverse (subvec seg-sums 4 7)))]
      (println (format "  7-fold word palindrome (1-3 vs rev 5-7): cos = %.6f"
                       (cosine-sim first3 last3-rev)))
      (println (format "  Center segment gematria: %,.0f" (nth seg-sums 3))))

    ;; ── 4. Unique words ──────────────────────────────────────
    (println "\n── 4. Vocabulary ──\n")

    (let [word-texts (map :text words)
          unique-words (distinct word-texts)
          vocab-size (count unique-words)
          word-freqs (frequencies word-texts)
          top-words (take 20 (sort-by (comp - second) word-freqs))]

      (println (format "  Vocabulary size: %,d unique words" vocab-size))
      (println (format "  Type/token ratio: %.4f" (/ (double vocab-size) n)))

      ;; Most common words
      (println "\n  Top 20 most common words:")
      (doseq [[w ct] top-words]
        (let [gem (reduce + (map #(long (g/letter-value %)) w))]
          (println (format "    %8s  gem=%4d  count=%,5d  (%.2f%%)"
                           w gem ct (* 100 (/ (double ct) n))))))

      ;; Hapax legomena (words appearing exactly once)
      (let [hapax (count (filter #(= (second %) 1) word-freqs))]
        (println (format "\n  Hapax legomena (words appearing once): %,d (%.1f%% of vocabulary)"
                         hapax (* 100 (/ (double hapax) vocab-size))))))

    ;; ── 5. Word gematria mod 7 ──────────────────────────────
    (println "\n── 5. Word Gematria mod 7 ──\n")

    (let [mod7-freqs (frequencies (map #(mod (:gem %) 7) words))]
      (doseq [r (range 7)]
        (let [ct (get mod7-freqs r 0)
              pct (* 100 (/ (double ct) n))]
          (println (format "  mod 7 = %d:  %,6d  (%5.2f%%)%s"
                           r ct pct
                           (if (= r 0) (format "  ← divisible by 7 (expected %.2f%%)" (/ 100.0 7)) ""))))))

    ;; ── 6. Book-level word stats ─────────────────────────────
    (println "\n── 6. Words by Book ──\n")

    (doseq [book books]
      (let [book-words (filterv #(= (:book %) book) words)
            bk-n (count book-words)
            bk-gems (mapv :gem book-words)
            bk-total (reduce + bk-gems)
            bk-mean-len (/ (reduce + (map :len book-words)) (double bk-n))
            bk-mean-gem (/ (double bk-total) bk-n)]
        (println (format "  %12s: %,6d words  mean_len=%.2f  mean_gem=%.1f  Σ=%,d"
                         book bk-n bk-mean-len bk-mean-gem bk-total))))

    ;; ── 7. Words that are palindromes (textual) ──────────────
    (println "\n── 7. Palindrome Words ──")
    (println "  Words that read the same forward and backward.\n")

    (let [palindromes (filterv #(let [t (:text %)]
                                   (and (> (count t) 2)
                                        (= t (apply str (reverse t)))))
                               words)
          pal-texts (distinct (map :text palindromes))]
      (println (format "  Palindrome word occurrences: %,d" (count palindromes)))
      (println (format "  Unique palindrome words: %d" (count pal-texts)))
      (when (seq pal-texts)
        (println "  Examples:")
        (doseq [t (take 20 pal-texts)]
          (let [gem (reduce + (map #(long (g/letter-value %)) t))
                ct (count (filter #(= (:text %) t) words))]
            (println (format "    %8s  gem=%4d  count=%,d" t gem ct))))))

    ;; ── 8. First and last words ──────────────────────────────
    (println "\n── 8. First and Last Words ──\n")

    (let [first7 (subvec words 0 7)
          last7 (subvec words (- n 7) n)]
      (println "  First 7 words:")
      (doseq [w first7]
        (println (format "    %s  gem=%d" (:text w) (:gem w))))
      (let [f7-gem (reduce + (map :gem first7))]
        (println (format "  Sum: %d" f7-gem))
        (println (format "  (2701 = T(73) = 37 × 73)\n")))

      (println "  Last 7 words:")
      (doseq [w last7]
        (println (format "    %s  gem=%d" (:text w) (:gem w))))
      (let [l7-gem (reduce + (map :gem last7))]
        (println (format "  Sum: %d" l7-gem))
        (println (format "  Ratio (first/last): %.4f" (/ (double (reduce + (map :gem first7))) l7-gem))))))

  (println "\nDone. The words are counted."))
