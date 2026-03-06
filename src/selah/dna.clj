(ns selah.dna
  "Play DNA through the breastplate.

   The map (experiment 101): 21 Hebrew letters ↔ 20 amino acids + Stop.
   Aleph is unmapped — beyond the code.

   Feed a gene. Read the Hebrew. Ask the oracle what it sees.

   Direction:
     DNA → codons → amino acids → Hebrew letters → oracle

   The reverse of how the map was found.
   The map reads Hebrew as protein. This namespace reads protein as Hebrew."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ── The Map ─────────────────────────────────────────────────
;;
;; Experiment 101: row→base {1→A, 2→C, 3→G, 4→U}
;; Watson-Crick: Row 1+4 = A+U, Row 2+3 = C+G
;; Greedy profile matching, p < 0.0001
;; Aleph: worst similarity, unmapped. The silent letter.

(def letter->aa
  "Hebrew letter → amino acid (keyword). From experiment 101."
  {\ב :Met, \ג :Asp, \ד :Ala, \ה :Thr, \ו :Pro,
   \ז :Tyr, \ח :Asn, \ט :Val, \י :Ser, \כ :Trp,
   \ל :Glu, \מ :Ile, \נ :Leu, \ס :Stop, \ע :His,
   \פ :Cys, \צ :Gln, \ק :Lys, \ר :Arg, \ש :Gly,
   \ת :Phe})

(def aa->letter
  "Amino acid (keyword) → Hebrew letter. The reverse map."
  (into {} (map (fn [[l a]] [a l]) letter->aa)))

(def aa-codes
  "Three-letter → one-letter amino acid codes."
  {"Ala" \A, "Arg" \R, "Asn" \N, "Asp" \D, "Cys" \C,
   "Gln" \Q, "Glu" \E, "Gly" \G, "His" \H, "Ile" \I,
   "Leu" \L, "Lys" \K, "Met" \M, "Phe" \F, "Pro" \P,
   "Ser" \S, "Thr" \T, "Trp" \W, "Tyr" \Y, "Val" \V})

(def one-letter->aa
  "Single-letter AA code → keyword."
  {\A :Ala, \R :Arg, \N :Asn, \D :Asp, \C :Cys,
   \Q :Gln, \E :Glu, \G :Gly, \H :His, \I :Ile,
   \L :Leu, \K :Lys, \M :Met, \F :Phe, \P :Pro,
   \S :Ser, \T :Thr, \W :Trp, \Y :Tyr, \V :Val})

(def three-letter->aa
  "Three-letter AA code → keyword."
  {"Ala" :Ala, "Arg" :Arg, "Asn" :Asn, "Asp" :Asp, "Cys" :Cys,
   "Gln" :Gln, "Glu" :Glu, "Gly" :Gly, "His" :His, "Ile" :Ile,
   "Leu" :Leu, "Lys" :Lys, "Met" :Met, "Phe" :Phe, "Pro" :Pro,
   "Ser" :Ser, "Thr" :Thr, "Trp" :Trp, "Tyr" :Tyr, "Val" :Val})

;; ── The Genetic Code ────────────────────────────────────────
;;
;; Standard (universal) genetic code.
;; 64 codons → 20 amino acids + Stop.

(def codon-table
  "RNA codon → amino acid keyword. Standard genetic code."
  {"UUU" :Phe, "UUC" :Phe, "UUA" :Leu, "UUG" :Leu,
   "UCU" :Ser, "UCC" :Ser, "UCA" :Ser, "UCG" :Ser,
   "UAU" :Tyr, "UAC" :Tyr, "UAA" :Stop, "UAG" :Stop,
   "UGU" :Cys, "UGC" :Cys, "UGA" :Stop, "UGG" :Trp,
   "CUU" :Leu, "CUC" :Leu, "CUA" :Leu, "CUG" :Leu,
   "CCU" :Pro, "CCC" :Pro, "CCA" :Pro, "CCG" :Pro,
   "CAU" :His, "CAC" :His, "CAA" :Gln, "CAG" :Gln,
   "CGU" :Arg, "CGC" :Arg, "CGA" :Arg, "CGG" :Arg,
   "AUU" :Ile, "AUC" :Ile, "AUA" :Ile, "AUG" :Met,
   "ACU" :Thr, "ACC" :Thr, "ACA" :Thr, "ACG" :Thr,
   "AAU" :Asn, "AAC" :Asn, "AAA" :Lys, "AAG" :Lys,
   "AGU" :Ser, "AGC" :Ser, "AGA" :Arg, "AGG" :Arg,
   "GUU" :Val, "GUC" :Val, "GUA" :Val, "GUG" :Val,
   "GCU" :Ala, "GCC" :Ala, "GCA" :Ala, "GCG" :Ala,
   "GAU" :Asp, "GAC" :Asp, "GAA" :Glu, "GAG" :Glu,
   "GGU" :Gly, "GGC" :Gly, "GGA" :Gly, "GGG" :Gly})

;; ── Translation: DNA → Hebrew ───────────────────────────────

(defn dna->rna
  "DNA string → RNA string. T→U, lowercase accepted."
  [dna]
  (-> (str/upper-case dna)
      (str/replace #"T" "U")))

(defn split-codons
  "Split an RNA or DNA string into triplets. Trailing bases ignored."
  [s]
  (mapv #(apply str %) (partition 3 s)))

(defn codon->aa
  "Single codon (RNA) → amino acid keyword, or nil."
  [codon]
  (get codon-table (str/upper-case codon)))

(defn translate-rna
  "RNA string → vec of amino acid keywords. Starts at first AUG, stops at Stop.
   If no AUG found, translates from position 0."
  [rna]
  (let [codons (split-codons (str/upper-case rna))
        aas (mapv codon->aa codons)
        ;; Find first Met (start codon)
        start (or (first (keep-indexed (fn [i aa] (when (= aa :Met) i)) aas)) 0)]
    (loop [i start, result []]
      (if (>= i (count aas))
        result
        (let [aa (nth aas i)]
          (if (= aa :Stop)
            (conj result :Stop)
            (recur (inc i) (conj result aa))))))))

(defn translate-dna
  "DNA string → vec of amino acid keywords."
  [dna]
  (translate-rna (dna->rna dna)))

;; ── Amino acids → Hebrew ────────────────────────────────────

(defn aa->hebrew
  "Amino acid keyword → Hebrew character, or · for unknown."
  [aa]
  (get aa->letter aa \·))

(defn protein->hebrew
  "Sequence of amino acid keywords → Hebrew string."
  [aas]
  (apply str (map aa->hebrew aas)))

(defn protein-str->hebrew
  "Single-letter protein string (e.g. \"MVLSPAD...\") → Hebrew string."
  [s]
  (let [aas (map one-letter->aa (seq s))]
    (apply str (map aa->hebrew aas))))

;; ── The Full Pipeline: DNA → Hebrew → Oracle ────────────────

(defn play
  "Play a DNA sequence through the breastplate.
   Returns the Hebrew letters the breastplate sees.

   Input: DNA string (ATCG) or RNA (AUCG) or protein (single-letter AAs).
   Options:
     :format  — :dna (default), :rna, :protein
     :raw?    — if true, translate all codons without seeking AUG start

   Returns:
     {:dna      — input (if DNA/RNA)
      :protein  — amino acid sequence
      :hebrew   — Hebrew letter string
      :gv       — gematria value
      :length   — number of residues}"
  ([sequence] (play sequence {}))
  ([sequence {:keys [format raw?] :or {format :dna}}]
   (let [aas (case format
               :protein (mapv one-letter->aa (seq (str/upper-case sequence)))
               :rna     (if raw?
                          (mapv codon->aa (split-codons (str/upper-case sequence)))
                          (translate-rna sequence))
               ;; default: DNA
               (if raw?
                 (mapv codon->aa (split-codons (dna->rna sequence)))
                 (translate-dna sequence)))
         hebrew (protein->hebrew aas)]
     {:input    sequence
      :format   format
      :protein  aas
      :protein-str (apply str (map #(get (clojure.set/map-invert one-letter->aa) % \?) aas))
      :hebrew   hebrew
      :gv       (g/word-value hebrew)
      :length   (count aas)})))

(defn observe
  "Play a sequence through the breastplate and ask the oracle what it sees.
   Runs the Hebrew through forward (illumination → readings).

   Returns play result + oracle readings for each window."
  ([sequence] (observe sequence {}))
  ([sequence {:keys [format window vocab]
              :or {format :dna, window 3, vocab :torah}}]
   (let [result (play sequence {:format format})
         hebrew (:hebrew result)
         n (count hebrew)
         ;; Slide a window across the Hebrew, ask the oracle at each position
         windows (for [i (range 0 (- n (dec window)))]
                   (let [w (subs hebrew i (+ i window))
                         fwd (o/forward (seq w) vocab)]
                     {:position i
                      :letters  w
                      :gv       (g/word-value w)
                      :known-words (take 5 (:known-words fwd))
                      :top-word (first (:known-words fwd))}))]
     (assoc result
            :window-size window
            :windows (vec windows)
            :vocab vocab))))

(defn report
  "Play + observe + print a human-readable report."
  ([sequence] (report sequence {}))
  ([sequence opts]
   (let [r (observe sequence opts)
         fmt (or (:format opts) :dna)]
     (println (str "=== DNA → BREASTPLATE ==="))
     (println (str "Input:   " (count (:input r)) " " (name fmt) " characters"))
     (println (str "Protein: " (:protein-str r) " (" (:length r) " residues)"))
     (println (str "Hebrew:  " (:hebrew r)))
     (println (str "GV:      " (:gv r)))
     (println)
     (println (str "=== ORACLE READINGS (window=" (:window-size r) ") ==="))
     (doseq [{:keys [position letters gv top-word]} (:windows r)]
       (when top-word
         (println (format "  [%3d] %s (gv=%d) → %s (%s, count=%d)"
                          position letters gv
                          (:word top-word)
                          (or (:meaning top-word) "?")
                          (:reading-count top-word)))))
     (println)
     r)))
