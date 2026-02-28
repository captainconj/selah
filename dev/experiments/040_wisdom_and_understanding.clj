(ns experiments.040-wisdom-and-understanding
  "חכמה (wisdom) = 73. בינה (understanding) = 67.
   73 is the structural prime of Genesis 1:1.
   67 is the 19th prime. 19 = חוה (Eve).
   What is the relationship between wisdom and understanding
   in the architecture?
   Run: clojure -M:dev -m experiments.040-wisdom-and-understanding"
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
  (println "=== WISDOM AND UNDERSTANDING ===")
  (println "  חכמה = 73. בינה = 67. The two pillars.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "  %,d letters.\n" n))

    ;; ── 1. The two pillars ──────────────────────────────────────
    (println "── 1. The Two Pillars ──\n")

    (let [pairs [["חכמה" "wisdom" 73]
                 ["בינה" "understanding" 67]
                 ["דעת" "knowledge" 474]
                 ["תבונה" "discernment" 463]
                 ["שכל" "intellect" 350]
                 ["חכם" "wise (adj)" 68]  ;; same as חיים!
                 ["תורה" "Torah" 611]
                 ["אמת" "truth" 441]]]  ;; 441 = 21²!
      (println "  The wisdom family:")
      (doseq [[heb eng gem] pairs]
        (println (format "    %6s (%s): %d  mod 7=%d  mod 37=%d"
                         heb eng gem (mod gem 7) (mod gem 37)))))

    (println)
    (println "  Notable:")
    (println "    חכמה (wisdom) = 73 — the 21st prime, Star(4)")
    (println "    חכם (wise) = 68 = חיים (life)!")
    (println "    אמת (truth) = 441 = 21² = (7×3)²")
    (println "    73 + 67 = 140 = 4 × 5 × 7")
    (println "    73 × 67 = 4891")
    (println (format "    4891 mod 7 = %d, mod 37 = %d" (mod 4891 7) (mod 4891 37)))
    (println (format "    73 - 67 = 6 (the days of creation)"))

    ;; ── 2. Every 73rd and 67th letter ───────────────────────────
    (println "\n── 2. Skip 73 and Skip 67 ──\n")

    (doseq [[skip label] [[73 "wisdom (73)"] [67 "understanding (67)"]
                           [140 "73+67"] [37 "mirror-prime (37)"]]]
      (let [sub (mapv #(nth all-letters %) (range 0 n skip))
            sub-gems (mapv #(long (g/letter-value %)) sub)
            sub-sum (reduce + sub-gems)]
        (println (format "  Skip %d (%s): %,d letters, Σ=%,d, mod 7=%d, mod 37=%d, mod 73=%d"
                         skip label (count sub) sub-sum
                         (mod sub-sum 7) (mod sub-sum 37) (mod sub-sum 73)))))

    ;; ── 3. Words with gematria 73 and 67 ────────────────────────
    (println "\n── 3. Words of Wisdom and Understanding ──")
    (println "  Counting words with gematria 73 and 67.\n")

    (let [word-counts (atom {73 0, 67 0, 441 0, 68 0, 37 0, 26 0})
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
                          (doseq [target (keys @word-counts)]
                            (when (= gem target)
                              (swap! word-counts update target inc))))))))))]
      (doseq [[gem label] [[73 "חכמה (wisdom)"]
                            [67 "בינה (understanding)"]
                            [441 "אמת (truth)"]
                            [68 "חיים/חכם (life/wise)"]
                            [37 "הבל (Abel) / palindrome prime"]
                            [26 "יהוה (the Name)"]]]
        (println (format "    gem=%d (%s): %,d words" gem label (get @word-counts gem))))

      ;; Ratio of wisdom to understanding words
      (let [w73 (get @word-counts 73)
            w67 (get @word-counts 67)]
        (println (format "\n    Wisdom/Understanding ratio: %.3f" (/ (double w73) (max 1 w67))))
        (println (format "    73 words + 67 words = %d" (+ w73 w67)))))

    ;; ── 4. The 73-fold structure ────────────────────────────────
    (println "\n── 4. The 73-Fold Structure ──")
    (println "  73 segments of the Torah.\n")

    (let [seg73 (quot n 73)
          s73 (mapv (fn [i]
                      (let [start (* i seg73)
                            end (if (= i 72) n (* (inc i) seg73))]
                        (double (reduce + (subvec gem-vals start end)))))
                    (range 73))
          ;; Palindrome: 0-35 vs reversed 37-72
          half 36
          first-half (subvec s73 0 half)
          last-half (vec (reverse (subvec s73 37 73)))
          cos73 (cosine-sim first-half last-half)]
      (println (format "  73-fold palindrome (0-35 vs rev 37-72): cos = %.6f" cos73))
      (println (format "  Center segment (37th): %,.0f" (nth s73 36)))

      ;; 73 threads
      (let [t73 (mapv (fn [off]
                        (double (reduce + (map #(long (nth gem-vals %)) (range off n 73)))))
                      (range 73))
            t-first (subvec t73 0 half)
            t-last (vec (reverse (subvec t73 37 73)))
            cos-t73 (cosine-sim t-first t-last)]
        (println (format "  73-thread palindrome: cos = %.6f" cos-t73))))

    ;; ── 5. The 67-fold structure ────────────────────────────────
    (println "\n── 5. The 67-Fold Structure ──")
    (println "  67 segments of the Torah.\n")

    (let [seg67 (quot n 67)
          s67 (mapv (fn [i]
                      (let [start (* i seg67)
                            end (if (= i 66) n (* (inc i) seg67))]
                        (double (reduce + (subvec gem-vals start end)))))
                    (range 67))
          half 33
          first-half (subvec s67 0 half)
          last-half (vec (reverse (subvec s67 34 67)))
          cos67 (cosine-sim first-half last-half)]
      (println (format "  67-fold palindrome (0-32 vs rev 34-66): cos = %.6f" cos67))
      (println (format "  Center segment (34th): %,.0f" (nth s67 33)))

      ;; 67 threads
      (let [t67 (mapv (fn [off]
                        (double (reduce + (map #(long (nth gem-vals %)) (range off n 67)))))
                      (range 67))
            t-first (subvec t67 0 half)
            t-last (vec (reverse (subvec t67 34 67)))
            cos-t67 (cosine-sim t-first t-last)]
        (println (format "  67-thread palindrome: cos = %.6f" cos-t67))))

    ;; ── 6. Proverbs 3:19 ───────────────────────────────────────
    (println "\n── 6. The Proverbs Connection ──")
    (println "  'The Lord by wisdom founded the earth;")
    (println "   by understanding he established the heavens.' (Prov 3:19)\n")

    ;; In the Torah: where does wisdom (73) meet understanding (67)?
    ;; Find adjacent words where one has gem=73 and next has gem=67
    (println "  Searching for adjacent 73+67 word pairs in Torah...\n")
    (let [pairs-found (atom [])
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [[vi v] (map-indexed vector verses)]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"\s+")
                          heb-words (filterv #(re-find #"[\u05D0-\u05EA]" %) raw-words)
                          word-gems (mapv (fn [w]
                                           (let [letters (norm/letter-stream w)]
                                             (reduce + (map #(long (g/letter-value %)) letters))))
                                         heb-words)]
                      (doseq [i (range (dec (count word-gems)))]
                        (when (or (and (= (nth word-gems i) 73)
                                      (= (nth word-gems (inc i)) 67))
                                  (and (= (nth word-gems i) 67)
                                      (= (nth word-gems (inc i)) 73)))
                          (swap! pairs-found conj
                                 {:book book :ch ch :verse (inc vi)
                                  :words [(nth heb-words i) (nth heb-words (inc i))]
                                  :gems [(nth word-gems i) (nth word-gems (inc i))]}))))))))]
      (println (format "  Adjacent pairs (73,67) or (67,73): %d" (count @pairs-found)))
      (doseq [p (take 10 @pairs-found)]
        (println (format "    %s %d:%d — %s(%d) %s(%d)"
                         (:book p) (:ch p) (:verse p)
                         (first (:words p)) (first (:gems p))
                         (second (:words p)) (second (:gems p))))))

    ;; ── 7. The star numbers ────────────────────────────────────
    (println "\n── 7. The Star Numbers ──")
    (println "  37 = Star(3). 73 = Star(4). Star numbers are hexagonal.")
    (println "  Star(n) = 6n(n-1) + 1\n")

    (let [star (fn [k] (+ (* 6 k (dec k)) 1))
          stars (mapv star (range 1 10))]
      (println (format "  Star numbers: %s" (str/join ", " stars)))
      (println (format "  Star(1)=%d, Star(2)=%d, Star(3)=%d, Star(4)=%d"
                       (star 1) (star 2) (star 3) (star 4)))

      ;; How do star numbers relate to the Torah?
      (doseq [k (range 1 8)]
        (let [s (star k)]
          (println (format "  Star(%d)=%d: total mod Star(%d) = %d, n mod Star(%d) = %d"
                           k s k (mod total-gem s) k (mod n s)))))

      ;; Star(3) × Star(4) = T(Star(4))
      (println (format "\n  Star(3) × Star(4) = %d × %d = %d = T(%d)"
                       (star 3) (star 4) (* (star 3) (star 4)) (star 4)))

      ;; T(n) = n(n+1)/2
      (println (format "  T(73) = 73 × 74 / 2 = %d = 2701" (quot (* 73 74) 2)))
      (println (format "  T(37) = 37 × 38 / 2 = %d" (quot (* 37 38) 2)))
      (println (format "  T(7) = 7 × 8 / 2 = %d = 28 (letters in Gen 1:1)" (quot (* 7 8) 2))))

    ;; ── 8. The trinity: 37, 73, 441 ────────────────────────────
    (println "\n── 8. The Trinity: 37, 73, 441 ──")
    (println "  37 mirrors 73. Their bridge is 21. 21² = 441.\n")

    (println "  Network of relationships:")
    (println "    37 = 12th prime")
    (println "    73 = 21st prime = חכמה (wisdom)")
    (println "    21 = 7 × 3 (index of 73 in the primes)")
    (println "    441 = 21² = (7×3)² = אמת (truth)")
    (println (format "    37 + 73 = %d" (+ 37 73)))
    (println (format "    37 + 73 = 110 mod 7 = %d" (mod 110 7)))
    (println (format "    441 - 37 = %d = %d × %d" (- 441 37) (quot (- 441 37) 2) 2))
    (println (format "    441 - 73 = %d = %d × %d" (- 441 73) (quot (- 441 73) 2) 2))
    (println (format "    441 / 37 = %.4f" (/ 441.0 37)))
    (println (format "    441 / 73 = %.4f" (/ 441.0 73)))
    (println (format "    2701 / 441 = %.4f" (/ 2701.0 441)))
    (println (format "    Total gematria / 441 = %d remainder %d" (quot total-gem 441) (mod total-gem 441)))

    ;; ── 9. Where the pillars stand ─────────────────────────────
    (println "\n── 9. Where the Pillars Stand ──")
    (println "  Position 73 and position 37 in the letter stream.\n")

    (let [p73 (nth all-letters 73)
          p37 (nth all-letters 37)
          g73 (long (g/letter-value p73))
          g37 (long (g/letter-value p37))]
      (println (format "  Letter at position 37: %c (gem=%d)" p37 g37))
      (println (format "  Letter at position 73: %c (gem=%d)" p73 g73))
      (println (format "  Sum: %d, mod 7=%d" (+ g37 g73) (mod (+ g37 g73) 7))))

    ;; What about position 441?
    (let [p441 (nth all-letters 441)
          g441 (long (g/letter-value p441))]
      (println (format "  Letter at position 441: %c (gem=%d)" p441 g441))
      (println (format "  mod 7=%d" (mod g441 7))))

    ;; The word containing position 73
    (println "\n  The 73rd letter sits in the word:")
    (let [gen1-raw (sefaria/fetch-chapter "Genesis" 1)
          all-words (atom [])
          pos (atom 0)
          _ (doseq [v gen1-raw]
              (let [stripped (norm/strip-html v)
                    raw-words (str/split stripped #"\s+")
                    heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                (doseq [w heb-words]
                  (let [letters (vec (norm/letter-stream w))
                        start @pos
                        end (+ start (count letters))]
                    (swap! all-words conj {:word w :start start :end end :letters letters})
                    (reset! pos end)))))]
      (doseq [w @all-words]
        (when (and (<= (:start w) 73) (< 73 (:end w)))
          (let [gem (reduce + (map #(long (g/letter-value %)) (:letters w)))]
            (println (format "    %s (positions %d-%d, gem=%d)"
                             (:word w) (:start w) (dec (:end w)) gem)))))))

  (println "\nDone. Wisdom and understanding are the two pillars."))
