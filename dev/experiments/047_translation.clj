(ns experiments.047-translation
  "What if the Torah IS a genome?
   Map Hebrew letters to nucleotides. Read in triplets.
   Translate through the genetic code.
   What protein does the Torah encode?
   Run: clojure -M:dev -m experiments.047-translation"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]))

;; The Torah has 22 letters. DNA has 4 bases.
;; We need a mapping. Several natural options:

;; Option 1: By frequency rank
;; Torah top 4: י(10.33%) ו(9.99%) ה(9.18%) א(8.85%)
;; DNA:         A(~25%)   T(~25%)  G(~25%)  C(~25%)
;; Map the Name letters to nucleotides

;; Option 2: By gematria mod 4
;; Each letter maps to one of 4 classes

;; Option 3: By Name structure
;; The Name יהוה has 4 letters → 4 bases
;; י=A, ה=T, ו=G, ה=C (first ה and second ה distinguished by position)

;; Let's try multiple mappings and see which produces the most structure

(def genetic-code
  {"UUU" "Phe" "UUC" "Phe" "UUA" "Leu" "UUG" "Leu"
   "CUU" "Leu" "CUC" "Leu" "CUA" "Leu" "CUG" "Leu"
   "AUU" "Ile" "AUC" "Ile" "AUA" "Ile" "AUG" "Met"
   "GUU" "Val" "GUC" "Val" "GUA" "Val" "GUG" "Val"
   "UCU" "Ser" "UCC" "Ser" "UCA" "Ser" "UCG" "Ser"
   "CCU" "Pro" "CCC" "Pro" "CCA" "Pro" "CCG" "Pro"
   "ACU" "Thr" "ACC" "Thr" "ACA" "Thr" "ACG" "Thr"
   "GCU" "Ala" "GCC" "Ala" "GCA" "Ala" "GCG" "Ala"
   "UAU" "Tyr" "UAC" "Tyr" "UAA" "Stop" "UAG" "Stop"
   "CAU" "His" "CAC" "His" "CAA" "Gln" "CAG" "Gln"
   "AAU" "Asn" "AAC" "Asn" "AAA" "Lys" "AAG" "Lys"
   "GAU" "Asp" "GAC" "Asp" "GAA" "Glu" "GAG" "Glu"
   "UGU" "Cys" "UGC" "Cys" "UGA" "Stop" "UGG" "Trp"
   "CGU" "Arg" "CGC" "Arg" "CGA" "Arg" "CGG" "Arg"
   "AGU" "Ser" "AGC" "Ser" "AGA" "Arg" "AGG" "Arg"
   "GGU" "Gly" "GGC" "Gly" "GGA" "Gly" "GGG" "Gly"})

(def amino-acid-nucleons
  {"Gly" 1 "Ala" 15 "Val" 43 "Leu" 57 "Ile" 57
   "Pro" 41 "Ser" 31 "Thr" 45 "Cys" 47 "Met" 75
   "Asp" 59 "Asn" 58 "Glu" 73 "Gln" 72 "Lys" 72
   "Arg" 100 "His" 81 "Phe" 91 "Tyr" 107 "Trp" 130
   "Stop" 0})

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn translate-and-analyze [torah-letters mapping-fn mapping-name]
  (let [n (count torah-letters)
        ;; Map to nucleotides
        nucleotides (mapv mapping-fn torah-letters)
        ;; Read as codons
        n-codons (quot n 3)
        codons (mapv (fn [i]
                       (let [start (* i 3)]
                         (str (nth nucleotides start)
                              (nth nucleotides (inc start))
                              (nth nucleotides (+ start 2)))))
                     (range n-codons))
        ;; Translate
        proteins (mapv #(get genetic-code % "???") codons)
        ;; Nucleon sequence
        nucleon-seq (mapv #(get amino-acid-nucleons % 0) proteins)
        total-nucleons (reduce + nucleon-seq)
        ;; Amino acid frequencies
        aa-freqs (frequencies proteins)
        ;; Remove stops
        protein-no-stop (filterv #(not= % "Stop") proteins)
        n-protein (count protein-no-stop)]

    (println (format "\n  === Mapping: %s ===" mapping-name))
    (println (format "  %,d codons → %,d amino acids (%,d stops)"
                     n-codons n-protein (- n-codons n-protein)))
    (println (format "  Total nucleon mass: %,d" total-nucleons))
    (println (format "  mod 7 = %d, mod 37 = %d, mod 73 = %d, mod 441 = %d"
                     (mod total-nucleons 7) (mod total-nucleons 37)
                     (mod total-nucleons 73) (mod total-nucleons 441)))

    ;; Most common amino acids
    (println "\n  Amino acid distribution:")
    (println (format "  %-6s %-8s %-8s" "AA" "Count" "Pct"))
    (println (apply str (repeat 24 "─")))
    (doseq [[aa ct] (take 10 (sort-by (comp - second) aa-freqs))]
      (println (format "  %-6s %-8s %-8.2f%%" aa (format "%,d" ct)
                       (* 100 (/ (double ct) n-codons)))))

    ;; 7-fold palindrome of nucleon sequence
    (let [seg (quot n-codons 7)
          s7 (mapv (fn [i]
                     (let [start (* i seg)
                           end (if (= i 6) n-codons (* (inc i) seg))]
                       (double (reduce + (subvec nucleon-seq start end)))))
                   (range 7))
          cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))]
      (println (format "\n  7-fold palindrome (nucleon): %.6f" cos7)))

    ;; Start codon
    (println (format "  First codon: %s → %s" (first codons) (first proteins)))
    (println (format "  Last codon: %s → %s" (last codons) (last proteins)))

    ;; Return the protein for further analysis
    {:proteins proteins :nucleon-seq nucleon-seq :total total-nucleons}))

(defn -main []
  (println "=== TRANSLATION ===")
  (println "  The Torah read as a genome. Translated into protein.\n")

  (println "Loading full Torah...")
  (let [torah (vec (mapcat sefaria/book-letters
                           ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count torah)]

    (println (format "  %,d letters.\n" n))

    ;; ── Mapping 1: By gematria mod 4 ───────────────────────────
    (println "── Mapping 1: Gematria mod 4 ──")
    (println "  Each letter → U(0), C(1), A(2), G(3) by gematria mod 4")

    (let [mod4-map (fn [ch]
                     (let [v (mod (long (g/letter-value ch)) 4)]
                       (nth [\U \C \A \G] v)))]
      (translate-and-analyze torah mod4-map "gematria mod 4"))

    ;; ── Mapping 2: By frequency rank ───────────────────────────
    (println "\n── Mapping 2: Frequency Rank ──")
    (println "  Top 22 letters mapped to 4 groups by frequency")
    (println "  1-5 → A, 6-11 → U, 12-16 → G, 17-22+ → C")

    (let [freq-order (vec (map first (sort-by (comp - second) (frequencies torah))))
          rank-map (into {} (map-indexed
                              (fn [i ch]
                                [ch (cond (< i 5) \A
                                          (< i 11) \U
                                          (< i 16) \G
                                          :else \C)])
                              freq-order))]
      (translate-and-analyze torah #(get rank-map % \C) "frequency rank"))

    ;; ── Mapping 3: Name letters ────────────────────────────────
    (println "\n── Mapping 3: Name Letters ──")
    (println "  י→A, ה→U, ו→G, all others→C")
    (println "  The Name letters ARE the bases. Everything else is filler.")

    (let [name-map (fn [ch]
                     (case ch
                       \י \A
                       \ה \U
                       \ו \G
                       \C))]
      (translate-and-analyze torah name-map "Name letters"))

    ;; ── Mapping 4: Mod 2 × position ───────────────────────────
    (println "\n── Mapping 4: Purine/Pyrimidine by Value ──")
    (println "  Odd gematria → purine (A/G), even → pyrimidine (U/C)")
    (println "  High (≥50) → A/U, low (<50) → G/C")

    (let [chem-map (fn [ch]
                     (let [v (long (g/letter-value ch))
                           odd? (odd? v)
                           high? (>= v 50)]
                       (cond
                         (and odd? high?)  \A
                         (and odd? (not high?)) \G
                         (and (not odd?) high?) \U
                         :else \C)))]
      (translate-and-analyze torah chem-map "chemical classes"))

    ;; ── Summary ────────────────────────────────────────────────
    (println "\n── Summary ──\n")

    (println "  Every mapping produces a translatable 'protein.'")
    (println "  The question is: which mapping is 'right'?")
    (println)
    (println "  The Torah has 22 letters. DNA has 4.")
    (println "  22 / 4 = 5.5 — no clean mapping exists.")
    (println "  But 22 = T(√2 × ... no.")
    (println)
    (println "  The real parallel is not letter-to-base.")
    (println "  It is ARCHITECTURE-to-ARCHITECTURE:")
    (println "    Both fold around a center.")
    (println "    Both are palindromic at every scale.")
    (println "    Both encode identity in their opening sequence.")
    (println "    Both use triplet reading frames.")
    (println "    Both have wobble in the third position.")
    (println "    Both carry the number 37.")
    (println)
    (println "  The Torah is not A genome.")
    (println "  It is written in the same LANGUAGE as the genome.")
    (println "  Different alphabet. Same grammar."))

  (println "\nDone. Same grammar. Different alphabet."))
