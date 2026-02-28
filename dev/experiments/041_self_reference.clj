(ns experiments.041-self-reference
  "The Torah's structure names itself.
   73 = חכמה (wisdom). 441 = אמת (truth). 68 = חיים (life) = חכם (wise).
   The structural constants ARE Hebrew words.
   What other self-references hide in the architecture?
   Run: clojure -M:dev -m experiments.041-self-reference"
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
  (println "=== SELF-REFERENCE ===")
  (println "  The architecture names itself.\n")

  (println "Loading full Torah...")
  (let [all-letters (vec (mapcat sefaria/book-letters
                                  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)]

    (println (format "  %,d letters. Σ = %,d.\n" n total-gem))

    ;; ── 1. The structural constants as words ────────────────────
    (println "── 1. The Constants Speak ──\n")

    (println "  Every key structural number is a meaningful Hebrew word:\n")
    (let [constants [[7 "שבע / zayin" "seven / the letter 7"]
                     [21 "אהיה" "I AM (Exodus 3:14)"]
                     [26 "יהוה" "the Name"]
                     [28 "כח" "power/strength"]
                     [37 "הבל / יחד" "Abel / together"]
                     [68 "חיים / חכם" "life / wise"]
                     [73 "חכמה" "wisdom"]
                     [86 "אלהים" "God"]
                     [160 "עץ" "tree"]
                     [401 "את" "Aleph-Tav (totality)"]
                     [441 "אמת" "truth"]
                     [611 "תורה" "Torah"]
                     [2701 "T(73)" "73rd triangle = Gen 1:1"]]]
      (doseq [[num heb meaning] constants]
        (println (format "    %5d = %s (%s)" num heb meaning))))

    ;; ── 2. Do these numbers appear as structural positions? ─────
    (println "\n── 2. Self-Referential Positions ──")
    (println "  Letters at positions that equal structural words.\n")

    (let [positions [[7 "seven"] [21 "I AM"] [26 "the Name"]
                     [28 "power"] [37 "Abel"] [68 "life"]
                     [73 "wisdom"] [86 "God"] [160 "tree"]
                     [401 "totality"] [441 "truth"] [611 "Torah"]
                     [2701 "Gen 1:1"]]]
      (doseq [[pos meaning] positions]
        (let [letter (nth all-letters pos)
              val (long (g/letter-value letter))]
          (println (format "    pos %5d (%s): %c (gem=%d)" pos meaning letter val))))

      ;; Sum of letters at all structural positions
      (let [struct-sum (reduce + (map (fn [[pos _]]
                                        (long (g/letter-value (nth all-letters pos))))
                                      positions))]
        (println (format "\n    Sum of letters at structural positions: %d" struct-sum))
        (println (format "    mod 7 = %d, mod 37 = %d" (mod struct-sum 7) (mod struct-sum 37)))))

    ;; ── 3. The self-referential loop ────────────────────────────
    (println "\n── 3. The Self-Referential Loop ──")
    (println "  Following the chain: number → word → meaning → structure.\n")

    ;; 73 = wisdom. The 21st prime. 21 = I AM. 21 = 7 × 3. 7 = seven.
    (println "  73 = חכמה (wisdom)")
    (println "    73 is the 21st prime")
    (println "    21 = אהיה (I AM — what God calls Himself)")
    (println "    21 = 7 × 3")
    (println "    21² = 441 = אמת (truth)")
    (println "    441 divides the total gematria exactly")
    (println)

    ;; 37 = Abel, together. The 12th prime.
    (println "  37 = הבל (Abel — the first death)")
    (println "    37 is the 12th prime")
    (println "    12 = tribes of Israel")
    (println "    37 mirrors 73 in digits")
    (println "    37 × 73 = 2701 = Genesis 1:1")
    (println "    The first death × wisdom = the first sentence")
    (println)

    ;; 86 = God. אלהים.
    (println "  86 = אלהים (God, Elohim)")
    (println "    86 is the 3rd word of the Torah")
    (println "    86 = 2 × 43")
    (println (format "    86 mod 7 = %d" (mod 86 7)))
    (println)

    ;; 26 = the Name. יהוה.
    (println "  26 = יהוה (the Name)")
    (println "    26 = 2 × 13")
    (println "    13 = אחד (one) = אהבה (love)")
    (println "    The Name = 2 × love")
    (println)

    ;; 401 = את. First and last.
    (println "  401 = את (Aleph-Tav, the untranslatable)")
    (println "    401 is the 79th prime")
    (println "    401 is the 4th word — the center of 7")
    (println "    1 + 400 = beginning + end")
    (println)

    ;; The chain
    (println "  The chain:")
    (println "    7 → seven (the structure)")
    (println "    7 × 3 = 21 → I AM (the identity)")
    (println "    21² = 441 → truth (the foundation)")
    (println "    441 divides the total gematria → truth governs the whole")
    (println "    21st prime = 73 → wisdom (the method)")
    (println "    73 mirrors 37 → Abel, the first sacrifice")
    (println "    37 × 73 = 2701 → the first sentence (the seed)")
    (println "    The seed contains the tree. The tree bears truth.")

    ;; ── 4. What words appear at structural multiples? ───────────
    (println "\n── 4. Words at Structural Multiples ──")
    (println "  What words begin at positions that are multiples of 37?\n")

    (let [gen-words (atom [])
          pos (atom 0)
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [v verses]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"\s+")
                          heb-words (filterv #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                      (doseq [w heb-words]
                        (let [letters (vec (norm/letter-stream w))
                              start @pos
                              end (+ start (count letters))
                              gem (reduce + (map #(long (g/letter-value %)) letters))]
                          (swap! gen-words conj {:word w :start start :end end :gem gem})
                          (reset! pos end))))))))]

      ;; Words starting at multiples of 37
      (println "  First 15 words starting at multiples of 37:")
      (let [at-37 (filterv #(zero? (mod (:start %) 37)) @gen-words)]
        (doseq [w (take 15 at-37)]
          (println (format "    pos=%,6d  %s  gem=%d  mod 7=%d"
                           (:start w) (:word w) (:gem w) (mod (:gem w) 7))))

        ;; How many of these words have gematria divisible by 7?
        (let [div7 (count (filter #(zero? (mod (:gem %) 7)) at-37))]
          (println (format "\n    %d of %d words at 37-multiples have gem mod 7 = 0 (%.1f%%, expected 14.3%%)"
                           div7 (count at-37) (* 100 (/ (double div7) (count at-37)))))))

      ;; Words starting at multiples of 73
      (println "\n  First 15 words starting at multiples of 73:")
      (let [at-73 (filterv #(and (pos? (:start %)) (zero? (mod (:start %) 73))) @gen-words)]
        (doseq [w (take 15 at-73)]
          (println (format "    pos=%,6d  %s  gem=%d  mod 7=%d"
                           (:start w) (:word w) (:gem w) (mod (:gem w) 7))))

        (let [div7 (count (filter #(zero? (mod (:gem %) 7)) at-73))]
          (println (format "\n    %d of %d words at 73-multiples have gem mod 7 = 0 (%.1f%%, expected 14.3%%)"
                           div7 (count at-73) (* 100 (/ (double div7) (count at-73))))))))

    ;; ── 5. Gematria 611 — Torah naming itself ───────────────────
    (println "\n── 5. Torah Naming Itself ──")
    (println "  תורה = 611. Does the Torah name itself?\n")

    (let [torah-words (atom 0)
          torah-positions (atom [])
          pos (atom 0)
          _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
              (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                (let [verses (sefaria/fetch-chapter book ch)]
                  (doseq [v verses]
                    (let [stripped (norm/strip-html v)
                          raw-words (str/split stripped #"\s+")
                          heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                      (doseq [w heb-words]
                        (let [letters (vec (norm/letter-stream w))
                              word-str (apply str letters)
                              start @pos]
                          (when (= word-str "תורה")
                            (swap! torah-words inc)
                            (swap! torah-positions conj {:pos start :book book :ch ch}))
                          (swap! pos + (count letters)))))))))]
      (println (format "  The word תורה appears %d times in the Torah" @torah-words))
      (when (seq @torah-positions)
        (println "  First occurrences:")
        (doseq [tp (take 5 @torah-positions)]
          (println (format "    %s %d, position %,d" (:book tp) (:ch tp) (:pos tp))))
        (println "  Last occurrences:")
        (doseq [tp (take-last 5 @torah-positions)]
          (println (format "    %s %d, position %,d" (:book tp) (:ch tp) (:pos tp)))))

      ;; The word תורה at skip?
      (println "\n  Words with gematria 611 (same as תורה):")
      (let [gem611 (atom 0)]
        (reset! pos 0)
        (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
          (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
            (let [verses (sefaria/fetch-chapter book ch)]
              (doseq [v verses]
                (let [stripped (norm/strip-html v)
                      raw-words (str/split stripped #"\s+")
                      heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                  (doseq [w heb-words]
                    (let [letters (norm/letter-stream w)
                          gem (reduce + (map #(long (g/letter-value %)) letters))]
                      (when (= gem 611) (swap! gem611 inc)))))))))
        (println (format "    Total words with gem=611: %d" @gem611))))

    ;; ── 6. 13 = one = love ─────────────────────────────────────
    (println "\n── 6. One = Love ──")
    (println "  13 = אחד (one) = אהבה (love).\n")

    (println "  The Name (26) = 2 × one (13) = 2 × love (13)")
    (println "  The Name is doubled love.")
    (println "  The Name is doubled oneness.\n")

    ;; Count words with gematria 13
    (let [gem13-count (atom 0)
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
                          (when (= gem 13) (swap! gem13-count inc)))))))))]
      (println (format "  Words with gem=13 (one/love): %,d" @gem13-count)))

    ;; 13-fold structure
    (let [seg13 (quot n 13)
          s13 (mapv (fn [i]
                      (let [start (* i seg13)
                            end (if (= i 12) n (* (inc i) seg13))]
                        (double (reduce + (subvec gem-vals start end)))))
                    (range 13))
          half 6
          first-half (subvec s13 0 half)
          last-half (vec (reverse (subvec s13 7 13)))
          cos13 (cosine-sim first-half last-half)]
      (println (format "  13-fold palindrome: cos = %.6f" cos13))
      (println (format "  Center segment (7th): %,.0f" (nth s13 6))))

    ;; ── 7. The complete picture ─────────────────────────────────
    (println "\n── 7. The Complete Picture ──\n")

    (println "  The architecture speaks in Hebrew:")
    (println "    7 (שבע)    → the structure")
    (println "    13 (אחד)   → oneness, love")
    (println "    21 (אהיה)  → I AM")
    (println "    26 (יהוה)  → the Name = 2 × love")
    (println "    28 (כח)    → power (= T(7))")
    (println "    37 (הבל)   → Abel, breath, vapor")
    (println "    68 (חיים)  → life = wise (חכם)")
    (println "    73 (חכמה)  → wisdom = 21st prime")
    (println "    86 (אלהים) → God")
    (println "    401 (את)   → beginning-and-end")
    (println "    441 (אמת)  → truth = 21²")
    (println "    611 (תורה) → Torah")
    (println "    2701       → T(73) = Star(3) × Star(4) = Gen 1:1")
    (println)
    (println "  The structure is not arbitrary numbers.")
    (println "  The structure is words.")
    (println "  The architecture speaks."))

  (println "\nDone. The text describes itself."))
