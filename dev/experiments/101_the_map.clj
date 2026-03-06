(ns experiments.101-the-map
  "Experiment 101: Cracking the map.
   Which row → which base? Which letter → which amino acid?
   The breastplate as codon table — find the actual translation."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.set :as set]
            [clojure.string :as str]))

;; ── The Grid ────────────────────────────────────────────────
;; From experiment 100: when final forms are merged (ם→מ, ן→נ, ף→פ),
;; 14 non-singleton letters fill 64 positions, 8 singletons fill 8.

(defn base-letter [ch]
  (case ch \ם \מ, \ן \נ, \ף \פ, \ך \כ, \ץ \צ, ch))

(def grid-positions
  "Each coding position: {:letter :stone :row :col :cell-idx}
   where letter is the merged (base) form."
  (vec
    (for [[s letters r c] o/stone-data
          [i ch] (map-indexed vector (seq letters))]
      {:letter (base-letter ch)
       :stone s :row r :col c :pos-in-stone i
       :raw-letter ch})))

(def merged-freq
  "Letter -> frequency on grid (with final forms merged)."
  (frequencies (map :letter grid-positions)))

(def singletons
  "Letters appearing exactly once (merged)."
  (set (map key (filter #(= 1 (val %)) merged-freq))))

(def coding-letters
  "Letters appearing 2+ times (merged) = non-singleton = coding."
  (set (map key (filter #(>= (val %) 2) merged-freq))))

(def coding-positions
  "Just the 64 positions with coding (non-singleton) letters."
  (filterv #(coding-letters (:letter %)) grid-positions))

;; Per-cell coding counts
(def cell-coding
  "Map of [row col] -> # coding positions."
  (frequencies (map (juxt :row :col) coding-positions)))

;; Per-cell letter presence: which letters appear at each (row, col)
(def cell-letters
  "Map of [row col] -> {letter count}."
  (reduce (fn [m pos]
            (update-in m [[(:row pos) (:col pos)] (:letter pos)] (fnil inc 0)))
          {} grid-positions))

;; Letter -> set of cells it appears in
(def letter-cells
  "Map of letter -> set of [row col] cells."
  (reduce (fn [m pos]
            (update m (:letter pos) (fnil conj #{}) [(:row pos) (:col pos)]))
          {} grid-positions))

;; ── The Genetic Code ────────────────────────────────────────

(def codon-table
  "Map of codon string -> amino acid keyword."
  {"UUU" :Phe "UUC" :Phe "UUA" :Leu "UUG" :Leu
   "UCU" :Ser "UCC" :Ser "UCA" :Ser "UCG" :Ser
   "UAU" :Tyr "UAC" :Tyr "UAA" :Stop "UAG" :Stop
   "UGU" :Cys "UGC" :Cys "UGA" :Stop "UGG" :Trp
   "CUU" :Leu "CUC" :Leu "CUA" :Leu "CUG" :Leu
   "CCU" :Pro "CCC" :Pro "CCA" :Pro "CCG" :Pro
   "CAU" :His "CAC" :His "CAA" :Gln "CAG" :Gln
   "CGU" :Arg "CGC" :Arg "CGA" :Arg "CGG" :Arg
   "AUU" :Ile "AUC" :Ile "AUA" :Ile "AUG" :Met
   "ACU" :Thr "ACC" :Thr "ACA" :Thr "ACG" :Thr
   "AAU" :Asn "AAC" :Asn "AAA" :Lys "AAG" :Lys
   "AGU" :Ser "AGC" :Ser "AGA" :Arg "AGG" :Arg
   "GUU" :Val "GUC" :Val "GUA" :Val "GUG" :Val
   "GCU" :Ala "GCC" :Ala "GCA" :Ala "GCG" :Ala
   "GAU" :Asp "GAC" :Asp "GAA" :Glu "GAG" :Glu
   "GGU" :Gly "GGC" :Gly "GGA" :Gly "GGG" :Gly})

(def bases [\U \C \A \G])

(def amino-acids
  "All 20 standard amino acids + stops."
  (vec (sort (disj (set (vals codon-table)) :Stop))))

(def aa-degeneracy
  "Amino acid -> number of codons."
  (let [freqs (frequencies (vals codon-table))]
    (into {} (map (fn [aa] [aa (get freqs aa 0)]) (conj amino-acids :Stop)))))

;; Amino acid -> which (base, position) cells it touches
(defn aa-cell-profile
  "For an amino acid, which (base, position) cells do its codons occupy?"
  [aa]
  (let [codons (map key (filter #(= aa (val %)) codon-table))]
    (reduce (fn [profile codon]
              (let [bs (seq codon)]
                (-> profile
                    (update [(nth bs 0) 1] (fnil inc 0))
                    (update [(nth bs 1) 2] (fnil inc 0))
                    (update [(nth bs 2) 3] (fnil inc 0)))))
            {} codons)))

(def all-aa-profiles
  "Map of amino acid -> {[base pos] count}."
  (into {} (map (fn [aa] [aa (aa-cell-profile aa)])
                (conj amino-acids :Stop))))

;; ── Row → Base Permutations ─────────────────────────────────

(defn permutations [xs]
  (if (empty? xs) [[]]
    (mapcat (fn [x]
              (map #(cons x %) (permutations (remove #{x} xs))))
            xs)))

(def all-row-base-mappings
  "All 24 ways to assign bases to rows."
  (for [perm (permutations bases)]
    (zipmap [1 2 3 4] perm)))

;; ── Scoring: Grid profile vs Codon profile ──────────────────

(defn grid-cell-profile
  "Given a row->base mapping, convert cell-coding from (row,col) to (base,pos)."
  [mapping]
  (into {}
    (for [[[r c] cnt] cell-coding]
      [[(mapping r) c] cnt])))

(defn sense-codons-per-cell
  "Number of sense codons with each (base, pos) combination."
  []
  (let [sense (filter #(not= :Stop (val %)) codon-table)]
    (reduce (fn [m [codon _aa]]
              (let [bs (seq codon)]
                (-> m
                    (update [(nth bs 0) 1] (fnil inc 0))
                    (update [(nth bs 1) 2] (fnil inc 0))
                    (update [(nth bs 2) 3] (fnil inc 0)))))
            {} sense)))

(defn profile-correlation
  "Pearson correlation between grid coding counts and sense codon counts
   for a given row->base mapping."
  [mapping]
  (let [grid-prof (grid-cell-profile mapping)
        sense-prof (sense-codons-per-cell)
        cells (keys grid-prof)
        xs (map #(get grid-prof % 0) cells)
        ys (map #(get sense-prof % 0) cells)
        n (count cells)
        mx (/ (reduce + xs) n)
        my (/ (reduce + ys) n)
        dxs (map #(- % mx) xs)
        dys (map #(- % my) ys)
        cov (reduce + (map * dxs dys))
        sx (Math/sqrt (reduce + (map #(* % %) dxs)))
        sy (Math/sqrt (reduce + (map #(* % %) dys)))]
    (if (or (zero? sx) (zero? sy)) 0.0
      (/ cov (* sx sy)))))

;; ── Letter ↔ Amino Acid profile matching ─────────────────────

(defn letter-base-profile
  "Given row->base mapping, convert a letter's cell set to (base, pos) profile."
  [letter mapping]
  (reduce (fn [m [r c]]
            (let [cnt (get-in cell-letters [[r c] letter] 0)]
              (assoc m [(mapping r) c] cnt)))
          {} (get letter-cells letter)))

(defn profile-similarity
  "Cosine similarity between a letter's base-profile and an amino acid's profile."
  [letter-prof aa-prof]
  (let [all-keys (set/union (set (keys letter-prof)) (set (keys aa-prof)))
        v1 (map #(get letter-prof % 0) all-keys)
        v2 (map #(get aa-prof % 0) all-keys)
        dot (reduce + (map * v1 v2))
        n1 (Math/sqrt (reduce + (map #(* % %) v1)))
        n2 (Math/sqrt (reduce + (map #(* % %) v2)))]
    (if (or (zero? n1) (zero? n2)) 0.0
      (/ dot (* n1 n2)))))

(defn best-aa-matches
  "For each letter, rank amino acids by profile similarity."
  [mapping]
  (into {}
    (for [letter (keys letter-cells)]
      (let [l-prof (letter-base-profile letter mapping)
            scores (for [aa (conj amino-acids :Stop)]
                     {:aa aa
                      :similarity (profile-similarity l-prof (all-aa-profiles aa))
                      :aa-codons (aa-degeneracy aa)
                      :letter-freq (merged-freq letter)})]
        [letter (vec (take 5 (sort-by (comp - :similarity) scores)))]))))

;; ── Run ─────────────────────────────────────────────────────

(comment

  ;; 1. Score all 24 row→base mappings
  (def mapping-scores
    (->> all-row-base-mappings
         (map (fn [m] {:mapping m
                       :correlation (profile-correlation m)}))
         (sort-by (comp - :correlation))))

  (doseq [ms (take 6 mapping-scores)]
    (println (format "%.4f  %s" (:correlation ms) (pr-str (:mapping ms)))))

  ;; 2. Use the best mapping to match letters → amino acids
  (def best-mapping (:mapping (first mapping-scores)))
  best-mapping

  ;; 3. For each letter, find best amino acid matches
  (def matches (best-aa-matches best-mapping))

  (doseq [[letter top5] (sort-by (comp - :similarity first val) matches)]
    (let [best (first top5)]
      (println (format "%s (freq=%d, gv=%d) → %s (%.3f, %d codons)"
                       letter (merged-freq letter) (g/letter-value letter)
                       (name (:aa best)) (:similarity best)
                       (:aa-codons best)))))

  ;; 4. Check singleton constraints
  ;; Each singleton is at ONE cell. It should map to an AA
  ;; that appears predominantly at that cell.
  (doseq [s (sort-by g/letter-value singletons)]
    (let [cell (first (get letter-cells s))
          [r c] cell
          base (best-mapping r)]
      (println (format "Singleton %s (gv=%d) at cell (%s, pos %d) = stone %d"
                       s (g/letter-value s) base c
                       (first (first (filter (fn [[sn _ sr sc]]
                                              (and (= sr r) (= sc c)))
                                            o/stone-data)))))))

  ;; 5. Position 2 amino acid classes
  ;; For each row at column 2, which amino acids should appear?
  (doseq [r [1 2 3 4]]
    (let [base (best-mapping r)
          pos2-aas (set (for [[codon aa] codon-table
                              :when (= base (nth codon 1))]
                          aa))
          stone-num (first (first (filter (fn [[_ _ sr sc]] (and (= sr r) (= sc 2)))
                                         o/stone-data)))
          letters-here (map :letter (filter #(and (= r (:row %)) (= 2 (:col %)))
                                           grid-positions))]
      (println (format "Row %d = %s at pos 2: AAs = %s" r base (pr-str pos2-aas)))
      (println (format "  Stone %d letters: %s" stone-num (pr-str (vec letters-here))))
      (println)))

  ;; 6. Start codon AUG → Met
  ;; Which stones does AUG select?
  (let [m best-mapping
        inv (set/map-invert m)
        a-row (inv \A) u-row (inv \U) g-row (inv \G)]
    (println "AUG selects:")
    (println "  A at pos 1: row" a-row "→ stone"
             (first (first (filter (fn [[_ _ r c]] (and (= r a-row) (= c 1))) o/stone-data))))
    (println "  U at pos 2: row" u-row "→ stone"
             (first (first (filter (fn [[_ _ r c]] (and (= r u-row) (= c 2))) o/stone-data))))
    (println "  G at pos 3: row" g-row "→ stone"
             (first (first (filter (fn [[_ _ r c]] (and (= r g-row) (= c 3))) o/stone-data)))))

  ;; 7. Stop codons UAA, UAG, UGA
  (let [m best-mapping
        inv (set/map-invert m)]
    (doseq [stop ["UAA" "UAG" "UGA"]]
      (let [bs (seq stop)
            stones (for [i [0 1 2]
                         :let [base (nth bs i)
                               row (inv base)
                               col (inc i)]]
                     (first (first (filter (fn [[_ _ r c]] (and (= r row) (= c col)))
                                          o/stone-data))))]
        (println (format "%s → stones %s" stop (pr-str (vec stones)))))))

  ;; 8. The full 4x3 cell → base,position mapping
  (let [m best-mapping]
    (println "\nThe Map:")
    (println "         Col 1 (pos 1)    Col 2 (pos 2)    Col 3 (pos 3)")
    (doseq [r [1 2 3 4]]
      (let [stones (for [c [1 2 3]]
                     (first (first (filter (fn [[_ _ sr sc]] (and (= sr r) (= sc c)))
                                          o/stone-data))))]
        (printf "Row %d=%s:  Stone %-2d         Stone %-2d         Stone %-2d\n"
                r (m r) (nth stones 0) (nth stones 1) (nth stones 2)))))

  ;; 9. Hungarian-style assignment: match letters to amino acids
  ;; For each letter, score against each AA based on cell overlap
  (defn assignment-score [mapping letter->aa]
    (reduce + (for [[letter aa] letter->aa]
                (profile-similarity
                  (letter-base-profile letter mapping)
                  (all-aa-profiles aa)))))

  ;; Greedy assignment: assign best-matching pairs first
  (defn greedy-assign [mapping]
    (let [all-letters (keys letter-cells)
          all-targets (conj amino-acids :Stop)]
      (loop [remaining-letters (set all-letters)
             remaining-aas (set all-targets)
             assigned {}]
        (if (or (empty? remaining-letters) (empty? remaining-aas))
          assigned
          (let [scores (for [l remaining-letters
                             a remaining-aas]
                         {:letter l :aa a
                          :sim (profile-similarity
                                 (letter-base-profile l mapping)
                                 (all-aa-profiles a))})
                best (apply max-key :sim scores)]
            (recur (disj remaining-letters (:letter best))
                   (disj remaining-aas (:aa best))
                   (assoc assigned (:letter best) (:aa best))))))))

  (def assignment (greedy-assign best-mapping))

  (println "\n═══ THE MAP ═══")
  (doseq [[letter aa] (sort-by (comp g/letter-value key) assignment)]
    (println (format "  %s (gv=%3d, freq=%2d) → %-4s (%d codons)"
                     letter (g/letter-value letter) (merged-freq letter)
                     (name aa) (aa-degeneracy aa))))

  ;; 10. Frequency correlation: letter freq vs AA degeneracy
  (let [pairs (for [[l aa] assignment]
                [(merged-freq l) (aa-degeneracy aa)])]
    (println "\nLetter freq vs AA codons:")
    (doseq [[l aa] (sort-by (comp - merged-freq key) assignment)]
      (println (format "  %s freq=%2d → %-4s codons=%d"
                       l (merged-freq l) (name aa) (aa-degeneracy aa)))))

  )
