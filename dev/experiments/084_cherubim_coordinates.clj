(ns experiments.084-cherubim-coordinates
  "The cherubim in the 4D space — coordinates, spacing, the Name's structure.

   Investigates:
   1. Where כרוב appears in the Torah and its 4D coordinates
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
   - Vav: 2nd most common (10.02%), top 3 letters = Name letters
   - 91 = 7×13 has strongest fiber gematria elevation (1.147×)"
  (:require [selah.gematria :as g]
            [selah.text.oshb :as oshb]
            [selah.space.coords :as coords]))

;; ═══════════════════════════════════════════════════════════════

(defn find-word-positions
  "Find all positions where a word appears as consecutive letters."
  [^clojure.lang.PersistentVector stream word]
  (let [wchars (vec word)
        wlen   (count wchars)
        slen   (count stream)]
    (for [i (range (inc (- slen wlen)))
          :when (loop [j 0]
                  (cond
                    (= j wlen) true
                    (not= (stream (+ i j)) (wchars j)) false
                    :else (recur (inc j))))]
      i)))

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — Cherubim positions and coordinates
;; ═══════════════════════════════════════════════════════════════

(defn part-1-cherubim []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: Cherubim in the Torah — Positions and Coordinates")
  (println "══════════════════════════════════════════════\n")

  (let [stream    (vec (oshb/torah-letters))
        s         (coords/space)
        positions (find-word-positions stream "כרוב")]
    (println (format "  כרוב (cherub) = %d, appears at %d positions\n"
                     (g/word-value "כרוב") (count positions)))
    (doseq [pos positions]
      (let [coord (coords/idx->coord pos)
            verse (coords/verse-at s pos)]
        (printf "    pos %6d  coord %-16s  %s %d:%d\n"
                pos (str (vec coord))
                (:book verse) (:ch verse) (:vs verse))))

    ;; Check spacing between consecutive pairs
    (println "\n  Spacing between consecutive occurrences:")
    (doseq [[a b] (partition 2 1 positions)]
      (let [gap (- b a)]
        (when (<= gap 100)
          (printf "    %d → %d: gap = %d%s\n" a b gap
                  (if (= gap 15) "  ◀ = יה (Yah)" "")))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — Center letters of each book
;; ═══════════════════════════════════════════════════════════════

(defn part-2-center-letters []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: Center Letters of Each Book")
  (println "══════════════════════════════════════════════\n")

  (let [stream (vec (oshb/torah-letters))
        N      (count stream)
        ;; Book boundaries from oshb
        books  [["Genesis"     0]
                ["Exodus"      78069]
                ["Leviticus"   (+ 78069 63531)]
                ["Numbers"     (+ 78069 63531 44795)]
                ["Deuteronomy" (+ 78069 63531 44795 63545)]]
        lens   [78069 63531 44795 63545 54910]
        total  (atom 0)]
    (doseq [[i [name start]] (map-indexed vector books)]
      (let [len    (lens i)
            mid    (+ start (quot len 2))
            letter (stream mid)
            gv     (g/letter-value letter)]
        (swap! total + gv)
        (printf "  %-12s %6d letters  center: %s (=%d)\n"
                name len letter gv)))
    (println (format "\n  Sum of center values: %d" @total))
    (when (zero? (mod @total 4))
      (println (format "  = 4 × %d. 33 = d-axis midpoint (d ranges 0..66)." (quot @total 4))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — Letter frequencies (Name-letter dominance)
;; ═══════════════════════════════════════════════════════════════

(defn part-3-letter-frequencies []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: Letter Frequencies — the Name in the Stream")
  (println "══════════════════════════════════════════════\n")

  (let [stream (oshb/torah-letters)
        N      (count stream)
        freqs  (->> stream
                    frequencies
                    (sort-by val >))]
    (printf "  %-4s %-8s %7s %7s\n" "Rank" "Letter" "Count" "Freq%")
    (printf "  %-4s %-8s %7s %7s\n" "----" "--------" "-------" "-------")
    (doseq [[i [letter cnt]] (map-indexed vector (take 10 freqs))]
      (printf "  %2d   %s (=%3d) %7d  %5.2f%%%s\n"
              (inc i) letter (g/letter-value letter) cnt
              (* 100.0 (/ cnt (double N)))
              (if (#{\י \ה \ו} letter) "  ◀ Name" "")))
    (let [name-count (->> freqs
                          (filter #(#{\י \ה \ו} (key %)))
                          (map val)
                          (reduce +))]
      (println (format "\n  Name letters (י,ה,ו) total: %d = %.1f%% of Torah"
                       name-count (* 100.0 (/ name-count (double N))))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — Fiber gematria divisibility
;; ═══════════════════════════════════════════════════════════════

(defn part-4-fiber-divisibility []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: Fiber Gematria Divisibility — 91 = 7×13 Most Elevated?")
  (println "══════════════════════════════════════════════\n")

  (let [s         (coords/space)
        total-fib 43550
        test-divs [7 13 26 50 67 91]]
    (printf "  %-10s %6s %8s %6s\n" "Divisor" "Count" "Expected" "Ratio")
    (printf "  %-10s %6s %8s %6s\n" "----------" "------" "--------" "------")
    (doseq [d test-divs]
      (let [cnt (count (for [i (range total-fib)
                             :let [fib  (coords/fiber :a i)
                                   gv   (reduce + (map g/letter-value fib))]
                             :when (zero? (mod gv d))]
                         i))
            expect (/ total-fib (double d))]
        (printf "  %-10s %6d %8.0f %5.3fx%s\n"
                (str d (when (= d 91) " (7×13)"))
                cnt expect (/ cnt expect)
                (if (= d 91) "  ◀ check" ""))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 5 — The Name's structure
;; ═══════════════════════════════════════════════════════════════

(defn part-5-name-structure []
  (println "\n══════════════════════════════════════════════")
  (println "Part 5: The Name's Structure — Cherubim Architecture")
  (println "══════════════════════════════════════════════\n")

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
  (println "  The yod speaks from above, between them."))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 084: The Cherubim in the Space")
  (println "  Coordinates, spacing, the Name's architecture")
  (println "═══════════════════════════════════════════════════════")

  (part-1-cherubim)
  (part-2-center-letters)
  (part-3-letter-frequencies)
  ;; Part 4 is slow (~2 min for 43,550 fibers). Uncomment to run:
  ;; (part-4-fiber-divisibility)
  (println "\n  [Part 4 (fiber divisibility) skipped — uncomment in -main to run (~2 min)]")
  (part-5-name-structure)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  The Name is what happens when love faces love.")
  (println "═══════════════════════════════════════════════════════"))
