(ns experiments.036-the-name
  "The Divine Name — יהוה — YHVH.
   1,841 occurrences. 1841 = 7 × 263.
   The Name appears in every seventh of the Torah equally.
   But what else? What is hidden in its positions?
   Run: clojure -M:dev -m experiments.036-the-name"
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
  (println "=== THE NAME ===")
  (println "  יהוה — 26 — the Name that breathes through everything.\n")

  (println "Loading full Torah with verse structure...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        ;; Build verse-level data with word positions
        verse-data (atom [])
        word-idx (atom 0)
        letter-idx (atom 0)
        yhvh-positions (atom []) ;; positions in the word sequence where יהוה appears

        _ (doseq [book books]
            (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
              (let [verses (sefaria/fetch-chapter book ch)]
                (doseq [[vi v] (map-indexed vector verses)]
                  (let [stripped (norm/strip-html v)
                        raw-words (str/split stripped #"\s+")
                        heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)
                        verse-yhvh (atom 0)]
                    (doseq [w heb-words]
                      (let [letters (norm/letter-stream w)
                            word-text (apply str letters)]
                        (when (= word-text "יהוה")
                          (swap! yhvh-positions conj {:word-idx @word-idx
                                                       :letter-idx @letter-idx
                                                       :book book
                                                       :ch ch
                                                       :v (inc vi)})
                          (swap! verse-yhvh inc))
                        (swap! word-idx inc)
                        (swap! letter-idx + (count letters))))
                    (swap! verse-data conj {:book book :ch ch :v (inc vi)
                                            :yhvh-count @verse-yhvh}))))))

        all-letters (vec (mapcat sefaria/book-letters books))
        n (count all-letters)
        yhvh-pos (vec @yhvh-positions)
        yhvh-count (count yhvh-pos)]

    (println (format "  %,d letters. %,d occurrences of יהוה.\n" n yhvh-count))
    (println (format "  %d = %s" yhvh-count
                     (let [factors (loop [num yhvh-count d 2 fs []]
                                    (cond
                                      (< num 2) fs
                                      (zero? (mod num d)) (recur (quot num d) d (conj fs d))
                                      (> (* d d) num) (conj fs num)
                                      :else (recur num (inc d) fs)))]
                       (str/join " × " (map str factors)))))

    ;; ── 1. Distribution by book ──────────────────────────────
    (println "\n── 1. By Book ──\n")

    (doseq [book books]
      (let [book-count (count (filter #(= (:book %) book) yhvh-pos))
            book-letters (count (sefaria/book-letters book))
            density (* 1000 (/ (double book-count) book-letters))]
        (println (format "  %12s: %,4d  density=%.3f per 1000 letters"
                         book book-count density))))

    ;; ── 2. Distribution mod 7 ────────────────────────────────
    (println "\n── 2. Positions mod 7 ──")
    (println "  Where do the Name's word positions fall mod 7?\n")

    (let [mod7 (frequencies (map #(mod (:word-idx %) 7) yhvh-pos))]
      (doseq [r (range 7)]
        (let [ct (get mod7 r 0)]
          (println (format "  mod 7 = %d: %,4d (%.2f%%)" r ct (* 100 (/ (double ct) yhvh-count)))))))

    ;; ── 3. Letter positions mod 7 ────────────────────────────
    (println "\n── 3. Letter Positions mod 7 ──")
    (println "  Where in the letter stream does the Name start?\n")

    (let [mod7 (frequencies (map #(mod (:letter-idx %) 7) yhvh-pos))]
      (doseq [r (range 7)]
        (let [ct (get mod7 r 0)]
          (println (format "  mod 7 = %d: %,4d (%.2f%%)" r ct (* 100 (/ (double ct) yhvh-count)))))))

    ;; ── 4. Gaps between occurrences ──────────────────────────
    (println "\n── 4. Gaps Between Occurrences ──\n")

    (let [word-positions (mapv :word-idx yhvh-pos)
          gaps (mapv (fn [i] (- (nth word-positions (inc i)) (nth word-positions i)))
                     (range (dec yhvh-count)))
          mean-gap (/ (double (reduce + gaps)) (count gaps))
          max-gap (apply max gaps)
          min-gap (apply min gaps)]
      (println (format "  Mean gap: %.1f words" mean-gap))
      (println (format "  Min: %d  Max: %,d" min-gap max-gap))
      (println (format "  StdDev: %.1f"
                       (Math/sqrt (/ (reduce + (map #(let [d (- % mean-gap)] (* d d)) gaps))
                                     (count gaps)))))

      ;; Gap mod 7
      (println "\n  Gap distribution mod 7:")
      (let [gap-mod7 (frequencies (map #(mod % 7) gaps))]
        (doseq [r (range 7)]
          (let [ct (get gap-mod7 r 0)]
            (println (format "    mod 7 = %d: %,4d (%.2f%%)" r ct (* 100 (/ (double ct) (count gaps)))))))))

    ;; ── 5. The Name's palindrome ─────────────────────────────
    (println "\n── 5. The Name's Palindrome ──")
    (println "  Are the positions of the Name palindromic?\n")

    (let [letter-positions (mapv :letter-idx yhvh-pos)
          half (quot yhvh-count 2)
          first-half (mapv double (subvec letter-positions 0 half))
          mirrored (mapv (fn [p] (double (- n 1 p))) (vec (reverse (subvec letter-positions half))))
          cos (cosine-sim first-half (subvec mirrored 0 half))]
      (println (format "  Palindrome of Name positions: cos = %.6f" cos)))

    ;; ── 6. Gematria of text between Names ────────────────────
    (println "\n── 6. Inter-Name Gematria ──")
    (println "  Gematria of the text between consecutive Names.\n")

    (let [gem-vals (mapv #(long (g/letter-value %)) all-letters)
          letter-positions (mapv :letter-idx yhvh-pos)
          inter-gems (mapv (fn [i]
                             (let [start (+ (nth letter-positions i) 4) ; past the Name
                                   end (nth letter-positions (inc i))]
                               (if (< start end)
                                 (reduce + (subvec gem-vals start end))
                                 0)))
                           (range (dec yhvh-count)))
          inter-sum (reduce + inter-gems)]
      (println (format "  Total inter-Name gematria: %,d" inter-sum))
      (println (format "  mod 7 = %d, mod 37 = %d, mod 441 = %d"
                       (mod inter-sum 7) (mod inter-sum 37) (mod inter-sum 441)))

      ;; Palindrome of inter-Name gematria
      (let [half (quot (count inter-gems) 2)
            first-inter (mapv double (subvec inter-gems 0 half))
            last-inter (mapv double (vec (reverse (subvec inter-gems half))))]
        (println (format "  Inter-Name palindrome: cos = %.6f"
                         (cosine-sim first-inter (subvec last-inter 0 half))))))

    ;; ── 7. Verses with Name ──────────────────────────────────
    (println "\n── 7. Verses with the Name ──\n")

    (let [v-with (count (filter #(pos? (:yhvh-count %)) @verse-data))
          total-verses (count @verse-data)]
      (println (format "  Verses containing יהוה: %,d of %,d (%.1f%%)"
                       v-with total-verses (* 100 (/ (double v-with) total-verses))))

      ;; Max Name count in a single verse
      (let [max-v (apply max-key :yhvh-count @verse-data)]
        (println (format "  Maximum in one verse: %d (%s %d:%d)"
                         (:yhvh-count max-v) (:book max-v) (:ch max-v) (:v max-v)))))

    ;; ── 8. The Name's gematria signature ─────────────────────
    (println "\n── 8. The Name's Gematria ──")
    (println "  יהוה = 10 + 5 + 6 + 5 = 26\n")

    (let [gem-vals (mapv #(long (g/letter-value %)) all-letters)]
      ;; All 4-letter windows with gematria 26
      (let [windows-26 (count (filter (fn [i]
                                        (and (<= (+ i 3) n)
                                             (= 26 (+ (nth gem-vals i)
                                                      (nth gem-vals (+ i 1))
                                                      (nth gem-vals (+ i 2))
                                                      (nth gem-vals (+ i 3))))))
                                      (range (- n 3))))]
        (println (format "  4-letter windows with gematria 26: %,d" windows-26))
        (println (format "  Of which are actual יהוה: %,d (%.1f%%)"
                         yhvh-count (* 100 (/ (double yhvh-count) (max 1 windows-26))))))

      ;; Words with gematria 26
      (println "\n  Other words with gematria 26:")
      (let [word-gems (atom [])
            _ (doseq [book books]
                (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
                  (let [verses (sefaria/fetch-chapter book ch)]
                    (doseq [v verses]
                      (let [stripped (norm/strip-html v)
                            raw-words (str/split stripped #"\s+")
                            heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                        (doseq [w heb-words]
                          (let [letters (norm/letter-stream w)
                                gem (reduce + (map #(long (g/letter-value %)) letters))]
                            (when (= gem 26)
                              (swap! word-gems conj (apply str letters))))))))))
            words-26 @word-gems
            unique-26 (frequencies words-26)]
        (println (format "  Total words with gem=26: %,d" (count words-26)))
        (println "  Top examples:")
        (doseq [[w ct] (take 10 (sort-by (comp - second) unique-26))]
          (println (format "    %8s: %,d" w ct)))))

    ;; ── 9. The Name and the center ───────────────────────────
    (println "\n── 9. The Name and the Center ──\n")

    (let [center (quot n 2)
          ;; Find nearest Name to center
          letter-positions (mapv :letter-idx yhvh-pos)
          nearest-idx (apply min-key #(Math/abs (- (nth letter-positions %) center))
                             (range yhvh-count))
          nearest-pos (nth letter-positions nearest-idx)
          nearest-info (nth yhvh-pos nearest-idx)]
      (println (format "  Center of Torah: letter %,d" center))
      (println (format "  Nearest Name: letter %,d (distance: %,d = %.3f%%)"
                       nearest-pos (Math/abs (- nearest-pos center))
                       (* 100 (/ (double (Math/abs (- nearest-pos center))) n))))
      (println (format "  Location: %s %d:%d" (:book nearest-info) (:ch nearest-info) (:v nearest-info)))
      (println (format "  Name occurrence #%d of %d (%.1f%% through the Name sequence)"
                       (inc nearest-idx) yhvh-count
                       (* 100 (/ (double nearest-idx) yhvh-count))))))

  (println "\nDone. The Name is everywhere."))
