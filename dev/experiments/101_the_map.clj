(ns experiments.101-the-map
  "Experiment 101: Cracking the map.
   Which row → which base? Which letter → which amino acid?
   The breastplate as codon table — find the actual translation."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.java.io :as io]))

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

(def output-dir "data/experiments/101")

(def aa-class
  {:Ala :small :Arg :charged :Asn :polar :Asp :charged
   :Cys :special :Gln :polar :Glu :charged :Gly :small
   :His :charged :Ile :hydrophobic :Leu :hydrophobic :Lys :charged
   :Met :special :Phe :hydrophobic :Pro :small :Ser :small
   :Thr :small :Trp :special :Tyr :polar :Val :hydrophobic
   :Stop :stop})

(defn- ensure-output-dir! []
  (.mkdirs (io/file output-dir)))

(defn- save-edn! [filename data]
  (spit (str output-dir "/" filename) (pr-str data)))

(defn- profile-correlation* [pairs]
  (let [xs (map first pairs)
        ys (map second pairs)
        n (count pairs)
        mx (/ (reduce + xs) n)
        my (/ (reduce + ys) n)
        dxs (map #(- % mx) xs)
        dys (map #(- % my) ys)
        cov (reduce + (map * dxs dys))
        sx (Math/sqrt (reduce + (map #(* % %) dxs)))
        sy (Math/sqrt (reduce + (map #(* % %) dys)))]
    (if (or (zero? sx) (zero? sy))
      0.0
      (/ cov (* sx sy)))))

(defn mapping-scores []
  (->> all-row-base-mappings
       (map (fn [m] {:mapping m
                     :correlation (profile-correlation m)}))
       (sort-by (comp - :correlation))))

(defn watson-crick-mapping? [mapping]
  (= #{#{\A \U} #{\C \G}}
     #{#{(mapping 1) (mapping 4)}
       #{(mapping 2) (mapping 3)}}))

(defn best-mapping []
  (:mapping (first (filter (comp watson-crick-mapping? :mapping) (mapping-scores)))))

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
                       {:letter l
                        :aa a
                        :sim (profile-similarity
                              (letter-base-profile l mapping)
                              (all-aa-profiles a))})
              best (apply max-key :sim scores)]
          (recur (disj remaining-letters (:letter best))
                 (disj remaining-aas (:aa best))
                 (assoc assigned (:letter best) (:aa best))))))))

(defn assignment-score [mapping assignment]
  (reduce + (for [[letter aa] assignment]
              (profile-similarity
               (letter-base-profile letter mapping)
               (all-aa-profiles aa)))))

(defn assignment->aa-code [assignment]
  (merge
   (into {} (for [[l aa] assignment]
              [l (case aa
                   :Ala "A" :Arg "R" :Asn "N" :Asp "D" :Cys "C"
                   :Gln "Q" :Glu "E" :Gly "G" :His "H" :Ile "I"
                   :Leu "L" :Lys "K" :Met "M" :Phe "F" :Pro "P"
                   :Ser "S" :Thr "T" :Trp "W" :Tyr "Y" :Val "V"
                   :Stop "*")]))
   {\א "·"}))

(defn- shuffle-with-rng [xs rng]
  (let [al (java.util.ArrayList. xs)]
    (java.util.Collections/shuffle al rng)
    (vec al)))

(defn permutation-test
  ([mapping assignment] (permutation-test mapping assignment 10000 101))
  ([mapping assignment trials seed]
   (let [letters (vec (sort-by g/letter-value (keys assignment)))
         aas (vec (map assignment letters))
         observed (assignment-score mapping assignment)
         rng (java.util.Random. (long seed))
         exceedances (reduce
                      (fn [n _]
                        (let [trial-assignment (zipmap letters (shuffle-with-rng aas rng))
                              score (assignment-score mapping trial-assignment)]
                          (if (>= score observed) (inc n) n)))
                      0
                      (range trials))]
     {:trials trials
      :seed seed
      :observed observed
      :exceedances exceedances
      :p-value (/ (double (inc exceedances)) (double (inc trials)))})))

(defn position2-verification [mapping assignment]
  (into {}
        (for [r [1 2 3 4]]
          (let [base (mapping r)
                expected-aas (set (for [[codon aa] codon-table
                                        :when (= base (nth codon 1))]
                                    aa))
                letters-here (distinct (map :letter (filter #(and (= r (:row %)) (= 2 (:col %)))
                                                           grid-positions)))
                assigned-aas (set (keep assignment letters-here))
                hits (set/intersection assigned-aas expected-aas)]
            [(keyword (str "row" r "-" base))
             {:row r
              :base base
              :hits (count hits)
              :assigned (count assigned-aas)
              :hit-aas (mapv name (sort hits))
              :miss-aas (mapv name (sort (set/difference assigned-aas expected-aas)))}]))))

(defn mapping-artifact [scores chosen-mapping]
  {:mapping chosen-mapping
   :correlation (profile-correlation chosen-mapping)
   :watson-crick {:outer [:A :U] :inner [:C :G]}
   :all-scores (vec (map (fn [ms] {:mapping (:mapping ms)
                                   :correlation (:correlation ms)})
                         scores))})

(defn assignment-artifact [assignment]
  (into (sorted-map)
        (for [[letter aa] assignment]
          [(str letter) {:letter (str letter)
                         :gv (g/letter-value letter)
                         :freq (merged-freq letter)
                         :amino-acid aa
                         :codons (aa-degeneracy aa)
                         :singleton? (contains? singletons letter)
                         :backbone? (contains? #{\ו \י \נ} letter)
                         :row-exclusive? (= 1 (count (set (map first (get letter-cells letter)))))}])))

(defn translated-breastplate [mapping assignment]
  (let [letter->aa-code (assignment->aa-code assignment)]
    {:stones
     (mapv (fn [[snum letters row col]]
             {:stone snum
              :row row
              :col col
              :letters (vec (map str (seq letters)))
              :translation (apply str (map #(get letter->aa-code (base-letter %) "?") (seq letters)))})
           o/stone-data)
     :by-row
     (mapv (fn [r]
             (let [row-stones (filter (fn [[_ _ sr _]] (= sr r)) o/stone-data)
                   row-seq (mapcat (fn [[_ letters _ _]]
                                     (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                                   row-stones)]
               {:row r
                :base (mapping r)
                :translation (apply str row-seq)}))
           [1 2 3 4])
     :full-sequence
     (apply str (mapcat (fn [[_ letters _ _]]
                          (map #(get letter->aa-code (base-letter %) "?") (seq letters)))
                        o/stone-data))}))

(defn summary []
  (let [scores (mapping-scores)
        chosen-mapping (best-mapping)
        matches (best-aa-matches chosen-mapping)
        assignment (greedy-assign chosen-mapping)
        ptest (permutation-test chosen-mapping assignment)
        freq-corr (profile-correlation*
                   (for [[l aa] assignment]
                     [(merged-freq l) (aa-degeneracy aa)]))
        inv (set/map-invert chosen-mapping)]
    {:row-base-mapping chosen-mapping
     :assignment-score (assignment-score chosen-mapping assignment)
     :permutation-p (:p-value ptest)
     :permutation-exceedances (:exceedances ptest)
     :permutation-trials (:trials ptest)
     :frequency-correlation freq-corr
     :profile-correlation (profile-correlation chosen-mapping)
     :pos2-verification (into {}
                              (for [[k {:keys [hits assigned]}] (position2-verification chosen-mapping assignment)]
                                [k [hits assigned]]))
     :unmapped-letter (str (first (set/difference (set (keys letter-cells))
                                                  (set (keys assignment)))))
     :start-codon {:codon "AUG"
                   :stones (vec (for [i [0 1 2]
                                      :let [base (nth [\A \U \G] i)
                                            row (inv base)
                                            col (inc i)]]
                                  (first (first (filter (fn [[_ _ r c]] (and (= r row) (= c col)))
                                                        o/stone-data)))))
                   :letter "ב"
                   :aa :Met}
     :stop-letter {:letter "ס" :stone 10 :tribe "Joseph"}
     :backbone {:vav [:Pro 4] :yod [:Ser 6] :nun [:Leu 6] :total-positions 26}
     :aleph-top-matches (vec (take 5 (get matches \א)))}))

(defn report-text []
  (let [scores (mapping-scores)
        chosen-mapping (best-mapping)
        matches (best-aa-matches chosen-mapping)
        assignment (greedy-assign chosen-mapping)
        letter->aa-code (assignment->aa-code assignment)
        ptest (permutation-test chosen-mapping assignment)
        translated (translated-breastplate chosen-mapping assignment)
        full-seq (vec (:full-sequence translated))
        first-met (.indexOf full-seq "M")
        stop-offset (.indexOf (vec (drop (inc first-met) full-seq)) "*")
        first-stop (when (>= stop-offset 0) (+ first-met stop-offset 1))]
    (with-out-str
      (println "=== Experiment 101: The Map ===")
      (println)
      (println "Top row/base mappings:")
      (doseq [ms (take 6 scores)]
        (println (format "  %.4f  %s" (:correlation ms) (pr-str (:mapping ms)))))
      (println)
      (println (format "Chosen mapping: %s" (pr-str chosen-mapping)))
      (println (format "Profile correlation: %.4f" (profile-correlation chosen-mapping)))
      (println (format "Assignment score: %.3f" (assignment-score chosen-mapping assignment)))
      (println (format "Permutation test: p=%.6f (%d/%d)"
                       (:p-value ptest) (:exceedances ptest) (:trials ptest)))
      (println)
      (println "Assignments:")
      (doseq [[letter aa] (sort-by (comp g/letter-value key) assignment)]
        (println (format "  %s (gv=%3d, freq=%2d) -> %-4s (%d codons)"
                         letter (g/letter-value letter) (merged-freq letter)
                         (name aa) (aa-degeneracy aa))))
      (println)
      (println "Aleph:")
      (println (format "  unmapped: %s"
                       (pr-str (set/difference (set (keys letter-cells))
                                               (set (keys assignment))))))
      (doseq [m (take 5 (get matches \א))]
        (println (format "  %s sim=%.3f codons=%d"
                         (name (:aa m)) (:similarity m) (:aa-codons m))))
      (println)
      (println "Position-2 verification:")
      (doseq [[k {:keys [hits assigned hit-aas miss-aas]}] (position2-verification chosen-mapping assignment)]
        (println (format "  %s: %d/%d hits=%s misses=%s"
                         (name k) hits assigned (pr-str hit-aas) (pr-str miss-aas))))
      (println)
      (println "Translated breastplate:")
      (doseq [{:keys [stone letters translation]} (:stones translated)]
        (println (format "  [%2d] %s -> %s" stone (apply str (interpose " " letters)) translation)))
      (println)
      (when (and (>= first-met 0) (number? first-stop))
        (println (format "ORF start: %d" (inc first-met)))
        (println (format "ORF stop: %d" (inc first-stop))))
      (println (format "Full sequence: %s" (:full-sequence translated)))
      (println))))

(defn run-experiment! []
  (ensure-output-dir!)
  (let [scores (mapping-scores)
        chosen-mapping (best-mapping)
        assignment (greedy-assign chosen-mapping)]
    (save-edn! "row-base-mapping.edn" (mapping-artifact scores chosen-mapping))
    (save-edn! "letter-aa-assignment.edn" (assignment-artifact assignment))
    (save-edn! "summary.edn" (summary))
    (save-edn! "translated-breastplate.edn" (translated-breastplate chosen-mapping assignment))
    (spit (str output-dir "/output.txt") (report-text))
    {:output-dir output-dir
     :summary (summary)}))

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
