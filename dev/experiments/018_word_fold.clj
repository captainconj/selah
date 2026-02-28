(ns experiments.018-word-fold
  "Word-level palindrome test.
   Where is the resolution limit of the fractal?
   Test palindrome at every scale: letters → words → verses → chapters → books.
   Run: clojure -M:dev -m experiments.018-word-fold"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(defn extract-words
  "Extract all Hebrew words from all books in order.
   Returns vec of {:word :letters :gematria :book :chapter :verse}"
  []
  (let [results (atom [])]
    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
        (let [verses (sefaria/fetch-chapter book ch)]
          (doseq [[v-idx verse-text] (map-indexed vector verses)]
            (let [stripped (norm/strip-html verse-text)
                  raw-words (str/split stripped #"[\s\u05BE]+")
                  words (->> raw-words
                             (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                             (filter #(pos? (count %))))]
              (doseq [w words]
                (swap! results conj
                       {:word w
                        :letters (count w)
                        :gematria (g/word-value w)
                        :book book
                        :chapter ch
                        :verse (inc v-idx)})))))))
    @results))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn palindrome-score
  "Cosine similarity of first half with reversed second half."
  [v]
  (let [n (count v)
        half (quot n 2)
        a (mapv double (subvec v 0 half))
        b (mapv double (vec (reverse (subvec v half (+ half half)))))]
    (cosine-sim a b)))

(defn bin-average
  "Average a vector into n bins."
  [v n-bins]
  (let [src-n (count v)
        bin-sz (/ (double src-n) n-bins)]
    (mapv (fn [i]
            (let [start (int (* i bin-sz))
                  end   (min src-n (int (* (inc i) bin-sz)))
                  chunk (subvec v start end)]
              (if (empty? chunk) 0.0
                  (/ (reduce + (map double chunk)) (count chunk)))))
          (range n-bins))))

(defn -main []
  (println "=== Word-Level Palindrome ===")
  (println "  Finding the resolution limit of the fractal.\n")

  (println "Extracting all words from the Torah...")
  (let [all-words (extract-words)
        n-words (count all-words)]
    (println (format "  %,d words extracted.\n" n-words))

    (let [word-lengths  (mapv :letters all-words)
          word-gems     (mapv :gematria all-words)
          center-word   (quot n-words 2)]

      (println (format "  Center word (#%,d): \"%s\" (length %d, gematria %d)"
                       center-word
                       (:word (nth all-words center-word))
                       (:letters (nth all-words center-word))
                       (:gematria (nth all-words center-word))))
      (println (format "  Center word location: %s %d:%d\n"
                       (:book (nth all-words center-word))
                       (:chapter (nth all-words center-word))
                       (:verse (nth all-words center-word))))

      ;; ── 1. Raw word-level palindrome ─────────────────────
      (println "── 1. Raw Word-Level Palindrome ──")
      (println "  First half vs reversed second half.\n")

      (let [len-pal (palindrome-score word-lengths)
            gem-pal (palindrome-score word-gems)]
        (println (format "  Word lengths:   palindrome cos = %.4f" len-pal))
        (println (format "  Word gematria:  palindrome cos = %.4f" gem-pal)))

      ;; ── 2. Word-by-word fold match ───────────────────────
      (println "\n── 2. Word-by-Word Fold ──")
      (println "  How often does word[i] have the same LENGTH as word[N-1-i]?\n")

      (let [half center-word
            len-matches (loop [i 0 m 0]
                          (if (= i half) m
                              (recur (inc i)
                                     (if (= (nth word-lengths i)
                                            (nth word-lengths (- n-words 1 i)))
                                       (inc m) m))))
            len-match-rate (/ (double len-matches) half)
            ;; Expected: Σ (freq of length l)²
            len-freqs (frequencies word-lengths)
            expected-len (reduce + (map (fn [[_ cnt]]
                                          (Math/pow (/ (double cnt) n-words) 2))
                                        len-freqs))]
        (println (format "  Length matches: %,d / %,d = %.4f (%.2f%%)"
                         len-matches half len-match-rate (* 100 len-match-rate)))
        (println (format "  Expected:      %.4f (%.2f%%)" expected-len (* 100 expected-len)))
        (println (format "  Ratio:         %.3f" (/ len-match-rate expected-len))))

      ;; Exact word matches
      (let [half center-word
            word-matches (loop [i 0 m 0]
                           (if (= i half) m
                               (recur (inc i)
                                      (if (= (:word (nth all-words i))
                                             (:word (nth all-words (- n-words 1 i))))
                                        (inc m) m))))
            word-match-rate (/ (double word-matches) half)]
        (println (format "\n  Exact word matches: %,d / %,d = %.4f (%.2f%%)"
                         word-matches half word-match-rate (* 100 word-match-rate))))

      ;; ── 3. Resolution curve ──────────────────────────────
      (println "\n── 3. Resolution Curve ──")
      (println "  Palindrome score at increasing bin sizes.")
      (println "  This reveals WHERE the fractal's self-similarity lives.\n")

      (println (format "  %8s  %10s  %10s  %s"
                       "Bins" "Length pal" "Gem pal" "Scale"))
      (println (apply str (repeat 52 "─")))

      (doseq [[n-bins scale-name]
              [[n-words "individual words"]
               [(quot n-words 2) "~2 words/bin"]
               [(quot n-words 5) "~5 words/bin"]
               [(quot n-words 10) "~10 words/bin"]
               [(quot n-words 20) "~20 words/bin"]
               [(quot n-words 50) "~50 words/bin"]
               [(quot n-words 100) "~100 words/bin"]
               [1000 "~79 words/bin"]
               [500 "~158 words/bin"]
               [200 "~395 words/bin"]
               [100 "~790 words/bin (≈verse)"]
               [50 "~1580 words/bin"]
               [20 "~3950 words/bin"]
               [10 "~7900 words/bin (≈chapter)"]
               [5 "5 bins (≈book)"]]]
        (when (and (pos? n-bins) (>= (quot (count word-lengths) 2) n-bins))
          (let [binned-lens (bin-average word-lengths n-bins)
                binned-gems (bin-average word-gems n-bins)
                len-pal (palindrome-score binned-lens)
                gem-pal (palindrome-score binned-gems)]
            (println (format "  %8d  %10.4f  %10.4f  %s"
                             n-bins len-pal gem-pal scale-name)))))

      ;; ── 4. Word distribution per quintile ────────────────
      (println "\n── 4. Word Statistics by Quintile ──")
      (println "  Split Torah into 5 equal word-segments.\n")

      (let [seg-size (quot n-words 5)
            quintiles (mapv (fn [i]
                              (let [start (* i seg-size)
                                    end   (if (= i 4) n-words (* (inc i) seg-size))
                                    chunk (subvec (vec all-words) start end)
                                    lens  (mapv :letters chunk)
                                    gems  (mapv :gematria chunk)]
                                {:seg (inc i)
                                 :words (count chunk)
                                 :mean-length (/ (reduce + (map double lens)) (count lens))
                                 :mean-gem    (/ (reduce + (map double gems)) (count gems))
                                 :total-gem   (reduce + gems)}))
                            (range 5))]
        (println (format "  %4s  %8s  %10s  %10s  %12s"
                         "Seg" "Words" "Mean len" "Mean gem" "Total gem"))
        (println (apply str (repeat 52 "─")))
        (doseq [{:keys [seg words mean-length mean-gem total-gem]} quintiles]
          (println (format "  %4d  %,8d  %10.3f  %10.2f  %,12d" seg words mean-length mean-gem total-gem)))

        ;; Palindrome of mean-length and mean-gem
        (let [ml (mapv :mean-length quintiles)
              mg (mapv :mean-gem quintiles)]
          (println (format "\n  Mean-length palindrome (q1-2 vs rev q4-5): cos = %.4f"
                           (cosine-sim (mapv double (subvec ml 0 2))
                                       (mapv double (vec (reverse (subvec ml 3 5)))))))
          (println (format "  Mean-gem palindrome (q1-2 vs rev q4-5):    cos = %.4f"
                           (cosine-sim (mapv double (subvec mg 0 2))
                                       (mapv double (vec (reverse (subvec mg 3 5)))))))))

      ;; ── 5. Chiastic word-length profile ──────────────────
      (println "\n── 5. Forward vs Chiastic Word Alignment ──")
      (println "  For each book pair, compare word-level profiles.\n")

      (let [book-word-ranges
            (loop [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
                   pos 0
                   result {}]
              (if (empty? books) result
                  (let [book (first books)
                        book-words (count (filter #(= (:book %) book) all-words))]
                    (recur (rest books)
                           (+ pos book-words)
                           (assoc result book {:start pos :end (+ pos book-words)
                                              :count book-words})))))]

        (doseq [[book-a book-b label] [["Genesis" "Deuteronomy" "A/A'"]
                                        ["Exodus" "Numbers" "B/B'"]]]
          (let [range-a (get book-word-ranges book-a)
                range-b (get book-word-ranges book-b)
                lens-a (subvec word-lengths (:start range-a) (:end range-a))
                lens-b (subvec word-lengths (:start range-b) (:end range-b))
                ;; Resample to 100 bins
                bins-a (bin-average lens-a 100)
                bins-b (bin-average lens-b 100)
                fwd-cos (cosine-sim bins-a bins-b)
                rev-cos (cosine-sim bins-a (vec (reverse bins-b)))]
            (println (format "  %s (%s: %,d words, %s: %,d words):"
                             label book-a (:count range-a) book-b (:count range-b)))
            (println (format "    Forward:  cos = %.4f" fwd-cos))
            (println (format "    Chiastic: cos = %.4f  %s"
                             rev-cos (if (> rev-cos fwd-cos) "← CHIASTIC WINS" "")))
            (println))))

      ;; ── 6. Most common mirror word pairs ─────────────────
      (println "── 6. Most Common Mirror Word Pairs ──")
      (println "  When word[i] = X, what is word[N-1-i]?\n")

      (let [half center-word
            pair-counts (atom {})]
        (doseq [i (range half)]
          (let [a (:word (nth all-words i))
                b (:word (nth all-words (- n-words 1 i)))]
            (when (= a b)
              (swap! pair-counts update a (fnil inc 0)))))

        (let [sorted-pairs (take 20 (sort-by (comp - val) @pair-counts))]
          (println (format "  %16s  %6s  %8s" "Word" "Count" "Gematria"))
          (println (apply str (repeat 36 "─")))
          (doseq [[word cnt] sorted-pairs]
            (println (format "  %16s  %6d  %8d" word cnt (g/word-value word))))))

      ;; ── 7. Word length distribution: palindromic? ────────
      (println "\n── 7. Word Length Distribution Mirror ──")
      (println "  Do the first half and second half have the same word-length distribution?\n")

      (let [half center-word
            first-lens (subvec word-lengths 0 half)
            second-lens (subvec word-lengths (- n-words half))
            freq-first (frequencies first-lens)
            freq-second (frequencies second-lens)
            all-lens (sort (distinct (concat (keys freq-first) (keys freq-second))))]
        (println (format "  %4s  %10s  %10s  %8s" "Len" "First half" "Second half" "Diff %"))
        (println (apply str (repeat 40 "─")))
        (doseq [l all-lens]
          (let [f (get freq-first l 0)
                s (get freq-second l 0)
                pct-diff (if (pos? (+ f s))
                           (* 100 (/ (double (Math/abs (- f s))) (/ (+ f s) 2.0)))
                           0.0)]
            (println (format "  %4d  %,10d  %,10d  %+7.2f%%" l f s pct-diff)))))))

  (println "\nDone."))
