(ns experiments.039-the-tree
  "The Tree of Life and the Tree of Knowledge.
   Two trees in the garden. Two books.
   The Torah is both — structure (life) and content (knowledge).
   What is the relationship between them?
   Run: clojure -M:dev -m experiments.039-the-tree"
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

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn -main []
  (println "=== THE TREE ===")
  (println "  Two trees. One garden. One architecture.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. The two trees ─────────────────────────────────────
    (println "── 1. The Two Trees ──\n")

    ;; עץ החיים — Tree of Life
    (let [tree-life "עץחיים"
          tl-gems (map #(long (g/letter-value %)) tree-life)
          tl-sum (reduce + tl-gems)]
      (println (format "  עץ החיים (Tree of Life): gem = %d" tl-sum))
      (println (format "    עץ = %d, החיים = %d" (+ 70 90) (+ 5 8 10 10 40))))

    ;; עץ הדעת טוב ורע — Tree of Knowledge of Good and Evil
    (let [tree-know "עץהדעתטובורע"
          tk-gems (map #(long (g/letter-value %)) tree-know)
          tk-sum (reduce + tk-gems)]
      (println (format "  עץ הדעת טוב ורע (Tree of Knowledge): gem = %d" tk-sum))
      (println (format "    עץ = %d, הדעת = %d, טוב = %d, ורע = %d"
                       (+ 70 90) (+ 5 4 70 400) (+ 9 6 2) (+ 6 200 70))))

    ;; ── 2. Where are the trees? ──────────────────────────────
    (println "\n── 2. Finding the Trees ──")
    (println "  Where do these phrases appear in the letter stream?\n")

    ;; Search for עץ (tree) in the stream
    (let [tree-word [\ע \צ]
          hits (filterv (fn [i]
                          (and (< (inc i) n)
                               (= (nth all-letters i) \ע)
                               (= (nth all-letters (inc i)) \צ)))
                        (range (dec n)))]
      (println (format "  עץ (tree) appears %d times in the letter stream" (count hits)))

      ;; How many are followed by ח (start of חיים, life)?
      (let [life-hits (count (filter (fn [i]
                                       (and (< (+ i 2) n)
                                            (= (nth all-letters (+ i 2)) \ח)))
                                     hits))]
        (println (format "  עצח (tree + life start): %d times" life-hits))))

    ;; ── 3. Gematria of key phrases ───────────────────────────
    (println "\n── 3. Key Gematria Values ──\n")

    (let [phrases [["חיים" "life" [8 10 10 40]]
                   ["מות" "death" [40 6 400]]
                   ["טוב" "good" [9 6 2]]
                   ["רע" "evil" [200 70]]
                   ["דעת" "knowledge" [4 70 400]]
                   ["חכמה" "wisdom" [8 20 40 5]]
                   ["בינה" "understanding" [2 10 50 5]]
                   ["עץ" "tree" [70 90]]
                   ["גן" "garden" [3 50]]
                   ["עדן" "Eden" [70 4 50]]
                   ["נחש" "serpent" [50 8 300]]
                   ["אדם" "Adam" [1 4 40]]
                   ["חוה" "Eve" [8 6 5]]]]
      (doseq [[heb eng vals] phrases]
        (let [s (reduce + vals)]
          (println (format "  %6s (%s): %4d  mod 7=%d  mod 37=%d"
                           heb eng s (mod s 7) (mod s 37))))))

    ;; ── 4. Life and death in the structure ───────────────────
    (println "\n── 4. Life and Death ──")
    (println "  חיים (life) = 68. מות (death) = 446.\n")

    ;; Words with gematria 68 (life)
    (let [life-words (atom 0)
          death-words (atom 0)
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [v verses]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"\s+")
                          heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                      (doseq [w heb-words]
                        (let [letters (norm/letter-stream w)
                              gem (reduce + (map #(long (g/letter-value %)) letters))]
                          (when (= gem 68) (swap! life-words inc))
                          (when (= gem 446) (swap! death-words inc)))))))))]
      (println (format "  Words with gem=68 (חיים): %,d" @life-words))
      (println (format "  Words with gem=446 (מות): %,d" @death-words))
      (println (format "  Life/Death ratio: %.3f" (/ (double @life-words) (max 1 @death-words))))
      (println (format "  68 + 446 = %d" (+ 68 446)))
      (println (format "  68 + 446 = 514 = 2 × 257"))
      (println (format "  68 × 446 = %,d" (* 68 446))))

    ;; ── 5. The garden — Genesis 2-3 ──────────────────────────
    (println "\n── 5. The Garden (Genesis 2-3) ──")
    (println "  The section where the trees appear.\n")

    (let [gen2 (sefaria/fetch-chapter "Genesis" 2)
          gen3 (sefaria/fetch-chapter "Genesis" 3)
          garden-text (concat gen2 gen3)
          garden-letters (vec (mapcat (fn [v] (norm/letter-stream (norm/strip-html v))) garden-text))
          garden-gems (mapv #(long (g/letter-value %)) garden-letters)
          garden-n (count garden-letters)
          garden-sum (reduce + garden-gems)]

      (println (format "  Genesis 2-3: %,d letters, Σ = %,d" garden-n garden-sum))
      (println (format "  mod 7 = %d, mod 37 = %d, mod 73 = %d"
                       (mod garden-sum 7) (mod garden-sum 37) (mod garden-sum 73)))

      ;; Does the garden have the palindrome?
      (let [half (quot garden-n 2)
            pa (letter-profile (subvec garden-letters 0 half))
            pb (letter-profile (vec (reverse (subvec garden-letters half))))
            cos (if (and pa pb) (cosine-sim pa pb) 0.0)]
        (println (format "  Garden palindrome: %.4f" cos)))

      ;; 7-fold test
      (let [seg (quot garden-n 7)
            s7 (mapv (fn [i]
                       (let [start (* i seg)
                             end (if (= i 6) garden-n (* (inc i) seg))]
                         (double (reduce + (subvec garden-gems start end)))))
                     (range 7))
            cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))]
        (println (format "  Garden 7-fold palindrome: %.4f" cos7))))

    ;; ── 6. DNA vocabulary ────────────────────────────────────
    (println "\n── 6. The DNA Vocabulary ──")
    (println "  Words that mean life, body, blood, bone, flesh, seed.\n")

    (let [dna-words [["דם" "blood" [4 40]]
                     ["בשר" "flesh" [2 300 200]]
                     ["עצם" "bone" [70 90 40]]
                     ["זרע" "seed" [7 200 70]]
                     ["נפש" "soul/self" [50 80 300]]
                     ["רוח" "spirit/breath" [200 6 8]]
                     ["לב" "heart" [30 2]]
                     ["יד" "hand" [10 4]]
                     ["עין" "eye" [70 10 50]]
                     ["פה" "mouth" [80 5]]
                     ["ראש" "head" [200 1 300]]
                     ["רגל" "foot" [200 3 30]]
                     ["בטן" "womb" [2 9 50]]]]
      (let [body-sum (reduce + (map (fn [[_ _ vals]] (reduce + vals)) dna-words))]
        (doseq [[heb eng vals] dna-words]
          (let [s (reduce + vals)]
            (println (format "  %6s (%s): %4d  mod 7=%d" heb eng s (mod s 7)))))
        (println (format "\n  Sum of all body words: %,d" body-sum))
        (println (format "  mod 7 = %d, mod 37 = %d" (mod body-sum 7) (mod body-sum 37)))))

    ;; ── 7. The mark, the name, the number ────────────────────
    (println "\n── 7. Mark, Name, Number ──")
    (println "  Three ways of identifying: sign, name, value.\n")

    ;; אות (sign/mark)
    ;; שם (name)
    ;; מספר (number)
    (let [mark-gem (+ 1 6 400)   ;; אות
          name-gem (+ 300 40)    ;; שם
          number-gem (+ 40 60 80 200)]  ;; מספר
      (println (format "  אות (sign/mark): %d" mark-gem))
      (println (format "  שם (name): %d" name-gem))
      (println (format "  מספר (number): %d" number-gem))
      (println (format "  Sum: %d" (+ mark-gem name-gem number-gem)))
      (println (format "  mod 7 = %d" (mod (+ mark-gem name-gem number-gem) 7)))

      ;; Count occurrences of each
      (let [count-word (fn [target]
                         (let [ct (atom 0)]
                           (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
                             (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                               (let [verses (sefaria/fetch-chapter book ch)]
                                 (doseq [v verses]
                                   (let [stripped (norm/strip-html v)
                                         raw-words (str/split stripped #"\s+")]
                                     (doseq [w raw-words]
                                       (let [letters (apply str (norm/letter-stream w))]
                                         (when (= letters target)
                                           (swap! ct inc)))))))))
                           @ct))]
        (println (format "\n  Occurrences:"))
        (println (format "    אות (sign): %d" (count-word "אות")))
        (println (format "    שם (name): %d" (count-word "שם")))
        (println (format "    מספר (number): %d" (count-word "מספר")))))

    ;; ── 8. The two halves ────────────────────────────────────
    (println "\n── 8. The Two Halves ──")
    (println "  Life and Knowledge. Structure and Content.\n")

    ;; Torah split in half: first half = "narrative" (creation → law)
    ;; Second half = "law" → blessing
    (let [half (quot n 2)
          first-sum (reduce + (subvec gem-vals 0 half))
          second-sum (reduce + (subvec gem-vals half n))]
      (println (format "  First half gematria:  %,d" first-sum))
      (println (format "  Second half gematria: %,d" second-sum))
      (println (format "  Ratio: %.6f" (/ (double first-sum) second-sum)))
      (println (format "  Difference: %,d (%.4f%%)"
                       (Math/abs (- first-sum second-sum))
                       (* 100 (/ (double (Math/abs (- first-sum second-sum)))
                                 (/ (+ (double first-sum) second-sum) 2)))))
      (println (format "  |First - Second| mod 7 = %d" (mod (Math/abs (- first-sum second-sum)) 7))))

    ;; ── 9. Aleph and Tav ─────────────────────────────────────
    (println "\n── 9. Aleph and Tav ──")
    (println "  The first and last. Alpha and Omega. Beginning and End.\n")

    (let [aleph-count (count (filter #(= % \א) all-letters))
          tav-count (count (filter #(= % \ת) all-letters))
          aleph-tav-sum (+ aleph-count tav-count)
          at-pairs (count (filter (fn [i]
                                    (and (< (inc i) n)
                                         (= (nth all-letters i) \א)
                                         (= (nth all-letters (inc i)) \ת)))
                                  (range (dec n))))]
      (println (format "  א (Aleph, 1): %,d occurrences" aleph-count))
      (println (format "  ת (Tav, 400): %,d occurrences" tav-count))
      (println (format "  Together: %,d (%.2f%% of Torah)" aleph-tav-sum
                       (* 100 (/ (double aleph-tav-sum) n))))
      (println (format "  Ratio א/ת: %.4f" (/ (double aleph-count) tav-count)))
      (println (format "  את consecutive pairs: %,d" at-pairs))
      (println (format "  את gem = %d = the untranslatable word" (+ 1 400)))
      (println (format "  את is the center word of Genesis 1:1"))
      (println (format "  The beginning and the end sit at the center."))))

  (println "\nDone. The tree bears fruit."))
