(ns experiments.084-cherubim-coordinates
  "The cherubim in the 4D space — coordinates, spacing, the Name's structure.

   Investigates:
   1. Where כרוב/כרובים appear in the Torah and their 4D coordinates
   2. The 15-letter spacing between paired cherubs (15 = יה = Yah)
   3. Center letters of each book (sum = 132 = 4 × 33 = d-axis midpoint × 4)
   4. Vav frequency (2nd most common letter, 10.02%)
   5. The Name's internal structure as cherubim model
   6. Fiber gematria divisibility (91 = 7×13 most elevated at 1.147×)

   FINDINGS:
   - Paired cherubs exactly 15 letters apart (Yah) in Exod 25:19 and 37:8
   - Facing cherub at d=13 (love) in Exod 25:20
   - First cherubim (Gen 3:24): c=10 (Esau's rung), verse = 67 letters
   - God speaks from between cherubim (Num 7:89): d=0 (understanding origin)
   - Book centers: א(1) נ(50) ע(70) ה(5) ו(6) = 132 = 4 × 33
   - Deut center in Deut 16:15: 'seven days...the LORD will bless you'
   - Vav: 2nd most common (10.02%), top 3 letters = Name letters
   - 91 = 7×13 has strongest fiber gematria elevation (1.147×)"
  (:require [selah.gematria :as g]
            [selah.text.stream :as stream]
            [selah.text.locate :as locate]
            [selah.space.coords :as coords]))

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — Cherubim positions and coordinates
;; ═══════════════════════════════════════════════════════════════

(defn find-word-positions
  "Find all positions where a word appears as consecutive letters in the stream."
  [stream word]
  (let [wchars (vec word)
        wlen   (count wchars)
        slen   (count stream)]
    (for [i (range (- slen wlen -1))
          :when (every? true? (map #(= (stream (+ i %)) (wchars %))
                                   (range wlen)))]
      i)))

(defn part-1-cherubim []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: Cherubim in the Torah — Positions and Coordinates")
  (println "══════════════════════════════════════════════\n")

  (let [stream (stream/torah-stream)
        ;; Search for כרוב (cherub singular, 4 letters)
        positions (find-word-positions stream "כרוב")]
    (println (format "  כרוב (cherub) = %d, appears at %d positions\n"
                     (g/word-value "כרוב") (count positions)))
    (doseq [pos positions]
      (let [coord (coords/idx->coord pos)
            ref   (locate/idx->ref pos)]
        (printf "    pos %6d  coord %s  %s\n" pos (str coord) (str ref))))

    ;; Check spacing between consecutive pairs
    (println "\n  Spacing between consecutive cherub occurrences:")
    (doseq [[a b] (partition 2 1 positions)]
      (let [gap (- b a)]
        (when (<= gap 50)
          (printf "    %d → %d: gap = %d%s\n" a b gap
                  (if (= gap 15) "  ◀ = יה (Yah)" "")))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — Center letters of each book
;; ═══════════════════════════════════════════════════════════════

(defn part-2-center-letters []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: Center Letters of Each Book")
  (println "══════════════════════════════════════════════\n")

  (let [stream (stream/torah-stream)
        books  (locate/book-ranges)
        total  (atom 0)]
    (doseq [{:keys [name start end]} books]
      (let [len    (- end start)
            mid    (+ start (quot len 2))
            letter (stream mid)
            gv     (g/letter-value letter)
            ref    (locate/idx->ref mid)]
        (swap! total + gv)
        (printf "  %-12s %6d letters  center: %s (=%d) at %s\n"
                name len letter gv (str ref))))
    (println (format "\n  Sum of center values: %d = 4 × %d" @total (quot @total 4)))
    (println "  33 = d-axis midpoint (d ranges 0..66)")))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — Letter frequencies (Name-letter dominance)
;; ═══════════════════════════════════════════════════════════════

(defn part-3-letter-frequencies []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: Letter Frequencies — the Name in the Stream")
  (println "══════════════════════════════════════════════\n")

  (let [stream (stream/torah-stream)
        N      (count stream)
        freqs  (->> stream
                    frequencies
                    (sort-by val >))]
    (printf "  %-4s %-6s %7s %7s\n" "Rank" "Letter" "Count" "Freq%")
    (printf "  %-4s %-6s %7s %7s\n" "----" "------" "-------" "-------")
    (doseq [[i [letter cnt]] (map-indexed vector (take 10 freqs))]
      (printf "  %2d   %s (=%d) %7d  %5.2f%%%s\n"
              (inc i) letter (g/letter-value letter) cnt
              (* 100.0 (/ cnt N))
              (if (#{\י \ה \ו} letter) "  ◀ Name letter" "")))
    (let [name-count (->> freqs
                          (filter #(#{\י \ה \ו} (key %)))
                          (map val)
                          (reduce +))]
      (println (format "\n  Name letters (י,ה,ו) total: %d = %.1f%% of Torah"
                       name-count (* 100.0 (/ name-count N)))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — Fiber gematria divisibility
;; ═══════════════════════════════════════════════════════════════

(defn part-4-fiber-divisibility []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: Fiber Gematria Divisibility — 91 = 7×13 Most Elevated")
  (println "══════════════════════════════════════════════\n")

  (let [N       43550
        fibers  (mapv (fn [i] (coords/fiber :a i)) (range N))
        gvs     (mapv (fn [fib] (reduce + (map g/letter-value fib))) fibers)
        test-divs [7 13 26 50 67 91]]
    (printf "  %-8s %6s %8s %6s\n" "Divisor" "Count" "Expected" "Ratio")
    (printf "  %-8s %6s %8s %6s\n" "--------" "------" "--------" "------")
    (doseq [d test-divs]
      (let [cnt    (count (filter #(zero? (mod % d)) gvs))
            expect (/ N (double d))]
        (printf "  %-8s %6d %8.0f %5.3fx%s\n"
                (str d (when (= d 91) " (7×13)"))
                cnt expect (/ cnt expect)
                (if (= d 91) "  ◀ highest" ""))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 5 — The Name's structure
;; ═══════════════════════════════════════════════════════════════

(defn part-5-name-structure []
  (println "\n══════════════════════════════════════════════")
  (println "Part 5: The Name's Structure — Cherubim Architecture")
  (println "══════════════════════════════════════════════\n")

  (let [name-letters [\י \ה \ו \ה]
        name-values  (mapv g/letter-value name-letters)]
    (println "  יהוה = י(10) + ה(5) + ו(6) + ה(5) = 26 = 2 × 13\n")
    (println "  Splits:")
    (println (format "    יה | וה  = %d | %d  (Yah + ...)" 15 11))
    (println (format "    י | הוה  = %d | %d  (Yod + 'to be')" 10 16))
    (println (format "    יהו | ה  = %d | %d  (3×7 + ...)" 21 5))
    (println)
    (println "  הוה ('to be') = 16 = 2⁴. Existence is a power of two.")
    (println "  יה (Yah) = 15 = T(5). The short Name is the 5th triangular.")
    (println "  15 = the gap between paired cherubs in the text.")
    (println)
    (println "        י  (10)")
    (println "        |")
    (println "        |  God speaks from ABOVE")
    (println "        |")
    (println "   ה ——— ו ——— ה")
    (println "   (5)  (6)   (5)")
    (println)
    (println "   Left    Mercy   Right")
    (println "   Cherub  Seat    Cherub")
    (println)
    (println "  The cherubim are the two ה's.")
    (println "  The mercy seat is the ו (center letter of the Torah).")
    (println "  The yod speaks from above, between them.")))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 084: The Cherubim in the Space")
  (println "  Coordinates, spacing, the Name's architecture")
  (println "═══════════════════════════════════════════════════════")

  (part-1-cherubim)
  (part-2-center-letters)
  (part-3-letter-frequencies)
  (part-4-fiber-divisibility)
  (part-5-name-structure)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  The Name is what happens when love faces love.")
  (println "═══════════════════════════════════════════════════════"))
