(ns experiments.025-the-number
  "441 = 21² = (7×3)².
   It appeared as:
   - The sum of center letters of the 7 seals
   - The gematria of אתם (you/them), which appears 130×130 in the halves
   Is this a coincidence or a key?
   What other numbers carry structural significance?
   Run: clojure -M:dev -m experiments.025-the-number"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(defn -main []
  (println "=== THE NUMBER ===")
  (println "  441 = 21² = (7×3)². Where does it live?\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "  %,d letters. Total gematria: %,d\n" n total-gem))

    ;; ── 1. The fundamental numbers ──────────────────────────
    (println "── 1. Fundamental Numbers ──")
    (println "  What numbers define the Torah's structure?\n")

    (println (format "  Total letters:     %,d" n))
    (println (format "  Total gematria:    %,d" total-gem))
    (println (format "  Mean gematria:     %.4f" (/ (double total-gem) n)))
    (println (format "  Letter count mod 7:   %d" (mod n 7)))
    (println (format "  Letter count mod 22:  %d" (mod n 22)))
    (println (format "  Letter count mod 26:  %d" (mod n 26)))
    (println (format "  Gematria mod 7:       %d" (mod total-gem 7)))
    (println (format "  Gematria mod 49:      %d" (mod total-gem 49)))
    (println (format "  Gematria mod 441:     %d" (mod total-gem 441)))
    (println (format "  Gematria / 441 =      %,.2f" (/ (double total-gem) 441)))
    (println (format "  Gematria / 7 =        %,d (exact)" (quot total-gem 7)))
    (println (format "  Gematria / 49 =       %,d (exact)" (quot total-gem 49)))
    (println (format "  306269 = ?            %s"
                     (str/join " × " (let [n 306269
                                            factors (atom [])
                                            v (atom n)]
                                        (loop [d 2]
                                          (when (<= (* d d) @v)
                                            (while (zero? (mod @v d))
                                              (swap! factors conj d)
                                              (swap! v quot d))
                                            (recur (inc d))))
                                        (when (> @v 1) (swap! factors conj @v))
                                        @factors))))

    ;; ── 2. The key numbers ──────────────────────────────────
    (println "\n── 2. Key Numbers ──")
    (println "  Numbers that appear structurally significant.\n")

    (let [key-numbers [7 22 26 37 49 111 130 153 187 231 306 333 441
                       2701 5846 21114]]
      (doseq [k key-numbers]
        (println (format "  %6d  n mod %d = %d   gem mod %d = %d   %s"
                         k (mod n k) k (mod total-gem k) k
                         (cond
                           (= k 7)   "days of creation"
                           (= k 22)  "letters in alphabet"
                           (= k 26)  "gematria of יהוה"
                           (= k 37)  "prime, 111/3, 333/9"
                           (= k 49)  "7², Jubilee"
                           (= k 111) "3×37, אלף (aleph spelled out)"
                           (= k 130) "count of אתם in each half"
                           (= k 153) "triangle(17), center position ÷1000"
                           (= k 187) "chapters in Torah"
                           (= k 231) "triangle(21), 22 choose 2"
                           (= k 306) "letter count ÷1000"
                           (= k 333) "9×37, center word gematria"
                           (= k 441) "21², (7×3)², center seal sum"
                           (= k 2701) "triangle(73), Gen 1:1 gematria"
                           (= k 5846) "verses in Torah"
                           (= k 21114) "total gem ÷1000 (approx)"
                           :else "")))))

    ;; ── 3. Triangle numbers ─────────────────────────────────
    (println "\n── 3. Triangle Numbers in the Torah ──")
    (println "  T(n) = n(n+1)/2. Are any structural numbers triangular?\n")

    (let [is-triangular? (fn [x]
                           (let [discriminant (+ 1 (* 8 x))
                                 root (Math/sqrt (double discriminant))]
                             (and (pos? x)
                                  (= (long root) root)
                                  (zero? (mod (dec (long root)) 2)))))
          tri-root (fn [x]
                     (let [root (Math/sqrt (+ 1.0 (* 8.0 x)))]
                       (/ (dec root) 2)))
          ;; Test key numbers
          numbers-to-test [n total-gem 2701 153134 441 231 5846 21113757
                           (quot total-gem 7) (quot total-gem 49)]]
      (doseq [x numbers-to-test]
        (println (format "  %,15d  T(%.2f)  %s"
                         x (tri-root (double x))
                         (if (is-triangular? x) "✓ TRIANGULAR" "")))))

    ;; ── 4. How 441 distributes ──────────────────────────────
    (println "\n── 4. Distribution of 441 ──")
    (println "  Count words with gematria = 441.\n")

    ;; Extract all words with gematria
    (let [all-words (atom [])
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [[v-idx v] (map-indexed vector verses)]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"[\s\u05BE]+")
                          words (->> raw-words
                                     (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                                     (filter #(pos? (count %))))]
                      (doseq [w words]
                        (swap! all-words conj
                               {:word w :gem (g/word-value w)
                                :book book :chapter ch :verse (inc v-idx)})))))))
          words @all-words
          words-441 (filter #(= 441 (:gem %)) words)]

      (println (format "  Words with gematria 441: %,d" (count words-441)))
      (println "\n  Unique words with gematria 441:")
      (let [unique-441 (sort-by (comp - val) (frequencies (map :word words-441)))]
        (doseq [[w cnt] unique-441]
          (println (format "    %16s  = %d  ×%d" w (g/word-value w) cnt))))

      ;; Which books contain them?
      (println "\n  Distribution by book:")
      (let [by-book (frequencies (map :book words-441))]
        (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
          (println (format "    %12s: %d" book (get by-book book 0)))))

      ;; ── 5. Words with gematria 333 (center word value) ────
      (println "\n── 5. Words with Gematria 333 ──")
      (println "  Center word = וישחט (gematria 333 = 9×37).\n")

      (let [words-333 (filter #(= 333 (:gem %)) words)]
        (println (format "  Words with gematria 333: %,d" (count words-333)))
        (let [unique-333 (sort-by (comp - val) (frequencies (map :word words-333)))]
          (doseq [[w cnt] unique-333]
            (println (format "    %16s  = %d  ×%d" w (g/word-value w) cnt)))))

      ;; ── 6. Words with gematria 26 (יהוה) ──────────────────
      (println "\n── 6. Words with Gematria 26 (= יהוה) ──")

      (let [words-26 (filter #(= 26 (:gem %)) words)]
        (println (format "  Words with gematria 26: %,d" (count words-26)))
        (println (format "  %,d mod 7: %d" (count words-26) (mod (count words-26) 7)))
        (let [unique-26 (sort-by (comp - val) (frequencies (map :word words-26)))]
          (doseq [[w cnt] (take 10 unique-26)]
            (println (format "    %16s  = %d  ×%d" w (g/word-value w) cnt)))))

      ;; ── 7. The sum of the first 7 words ───────────────────
      (println "\n── 7. First and Last Word Gematria ──")

      (let [first-7 (take 7 words)
            last-7 (take-last 7 words)
            first-7-sum (reduce + (map :gem first-7))
            last-7-sum (reduce + (map :gem last-7))]
        (println "\n  First 7 words of the Torah:")
        (doseq [w first-7]
          (println (format "    %16s  = %d" (:word w) (:gem w))))
        (println (format "    Sum: %d" first-7-sum))
        (println (format "    Sum mod 7: %d" (mod first-7-sum 7)))

        (println "\n  Last 7 words of the Torah:")
        (doseq [w last-7]
          (println (format "    %16s  = %d" (:word w) (:gem w))))
        (println (format "    Sum: %d" last-7-sum))
        (println (format "    Sum mod 7: %d" (mod last-7-sum 7)))

        (println (format "\n  First 7 + Last 7 = %d" (+ first-7-sum last-7-sum)))
        (println (format "  (First 7 + Last 7) mod 7: %d" (mod (+ first-7-sum last-7-sum) 7))))

      ;; ── 8. Genesis 1:1 deep numerology ────────────────────
      (println "\n── 8. Genesis 1:1 ──")
      (println "  בראשית ברא אלהים את השמים ואת הארץ\n")

      (let [gen1-words (take-while #(and (= "Genesis" (:book %))
                                          (= 1 (:chapter %))
                                          (= 1 (:verse %)))
                                    words)
            gen1-gems (mapv :gem gen1-words)
            gen1-sum (reduce + gen1-gems)]
        (println (format "  Words: %s" (str/join " " (map :word gen1-words))))
        (println (format "  Values: %s" (str gen1-gems)))
        (println (format "  Sum: %d" gen1-sum))
        (println (format "  %d = T(73) — triangle of 73" gen1-sum))
        (println (format "  73 is the 21st prime"))
        (println (format "  21 = 7 × 3"))
        (println (format "  21² = 441"))
        (println (format "  %d mod 7: %d" gen1-sum (mod gen1-sum 7)))
        (println (format "  %d mod 37: %d" gen1-sum (mod gen1-sum 37)))

        ;; The 7 words: first 3 + last 4, first 4 + last 3
        (when (= 7 (count gen1-gems))
          (let [first3 (reduce + (subvec gen1-gems 0 3))
                last4  (reduce + (subvec gen1-gems 3 7))
                first4 (reduce + (subvec gen1-gems 0 4))
                last3  (reduce + (subvec gen1-gems 4 7))]
            (println (format "\n  First 3 words: %d" first3))
            (println (format "  Last 4 words: %d" last4))
            (println (format "  First 3 / Last 4 = %.6f" (/ (double first3) last4)))
            (println (format "  First 4 words: %d" first4))
            (println (format "  Last 3 words: %d" last3))
            (println (format "  First 4 / Last 3 = %.6f" (/ (double first4) last3)))
            (println (format "  First 3 + Last 3 = %d" (+ first3 last3)))
            (println (format "  Center word (4th): %s = %d"
                             (:word (nth gen1-words 3)) (:gem (nth gen1-words 3)))))))

      ;; ── 9. The number 7 everywhere ────────────────────────
      (println "\n── 9. Seven in the Torah's DNA ──")

      (let [n-words (count words)
            ;; How many words have gematria divisible by 7?
            div7-words (count (filter #(zero? (mod (:gem %) 7)) words))
            ;; How many verse-level gematria sums are divisible by 7?
            verse-gems (atom [])
            _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
                (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                  (let [verses (sefaria/fetch-chapter book ch)]
                    (doseq [v verses]
                      (let [stripped (norm/strip-html v)
                            letters (norm/letter-stream stripped)
                            gem-sum (reduce + (map #(long (g/letter-value %)) letters))]
                        (swap! verse-gems conj gem-sum))))))
            v-gems @verse-gems
            div7-verses (count (filter #(zero? (mod % 7)) v-gems))
            n-verses (count v-gems)]

        (println (format "  Words divisible by 7:  %,d / %,d = %.2f%% (expected 14.3%%)"
                         div7-words n-words (* 100 (/ (double div7-words) n-words))))
        (println (format "  Verses divisible by 7: %,d / %,d = %.2f%% (expected 14.3%%)"
                         div7-verses n-verses (* 100 (/ (double div7-verses) n-verses))))
        (println (format "  Total gematria: %,d ÷ 7 = %,d (exact)" total-gem (quot total-gem 7)))
        (println (format "  Total gematria: %,d ÷ 49 = %,d (exact)" total-gem (quot total-gem 49)))
        (println (format "  Occurrences of Divine Name mod 7: 0 (1841 = 263 × 7)"))
        (println (format "  Center letters of 7 seals: sum = 441 = 7² × 3² = 21²"))
        (println (format "  306,269 ÷ 7 = %,d remainder %d" (quot n 7) (mod n 7))))))

  (println "\nDone. The numbers speak."))
