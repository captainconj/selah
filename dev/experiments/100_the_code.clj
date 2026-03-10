(ns experiments.e100-the-code
  "Experiment 100: The Code
   Testing the hypothesis: breastplate = codon table.
   22 letters = 22 amino acids. 4×3 grid = codon dimensions."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── The Grid ──────────────────────────────────────────────────

(def grid
  "The 12 stones with their letters, organized by row and column."
  (into {} (map (fn [[s letters r c]]
                  [s {:stone s :letters (vec letters) :row r :col c
                      :tribe (o/stone-tribe s)}])
                o/stone-data)))

;; ── Letter Census ─────────────────────────────────────────────

(def all-letters
  "All 22 Hebrew letters as they appear on the breastplate."
  (let [alphabet "אבגדהוזחטיכלמנסעפצקרשת"]
    (vec alphabet)))

(def letter-freq
  "How many times each letter appears on the grid."
  (let [all-chars (mapcat :letters (vals grid))]
    (frequencies all-chars)))

(def letter-stones
  "Which stones carry each letter."
  o/letter-index)

(def singletons
  "Letters that appear exactly once on the grid."
  (vec (sort-by g/letter-value
    (for [[ch cnt] letter-freq :when (= 1 cnt)] ch))))

;; ── The Genetic Code ──────────────────────────────────────────

(def codon-table
  "Standard genetic code: codon -> amino acid."
  {"UUU" :Phe "UUC" :Phe "UUA" :Leu "UUG" :Leu
   "CUU" :Leu "CUC" :Leu "CUA" :Leu "CUG" :Leu
   "AUU" :Ile "AUC" :Ile "AUA" :Ile "AUG" :Met
   "GUU" :Val "GUC" :Val "GUA" :Val "GUG" :Val
   "UCU" :Ser "UCC" :Ser "UCA" :Ser "UCG" :Ser
   "CCU" :Pro "CCC" :Pro "CCA" :Pro "CCG" :Pro
   "ACU" :Thr "ACC" :Thr "ACA" :Thr "ACG" :Thr
   "GCU" :Ala "GCC" :Ala "GCA" :Ala "GCG" :Ala
   "UAU" :Tyr "UAC" :Tyr "UAA" :Stop "UAG" :Stop
   "CAU" :His "CAC" :His "CAA" :Gln "CAG" :Gln
   "AAU" :Asn "AAC" :Asn "AAA" :Lys "AAG" :Lys
   "GAU" :Asp "GAC" :Asp "GAA" :Glu "GAG" :Glu
   "UGU" :Cys "UGC" :Cys "UGA" :Stop "UGG" :Trp
   "CGU" :Arg "CGC" :Arg "CGA" :Arg "CGG" :Arg
   "AGU" :Ser "AGC" :Ser "AGA" :Arg "AGG" :Arg
   "GGU" :Gly "GGC" :Gly "GGA" :Gly "GGG" :Gly})

(def aa-codon-count
  "Number of codons encoding each amino acid."
  (let [grouped (group-by val codon-table)]
    (into (sorted-map)
      (map (fn [[aa codons]] [aa (count codons)]) grouped))))

(def aa-properties
  "Key properties of the 20+2 amino acids."
  {:Ala {:codons 4 :mw  89 :class :nonpolar  :abbrev "A" :name "Alanine"}
   :Arg {:codons 6 :mw 174 :class :positive  :abbrev "R" :name "Arginine"}
   :Asn {:codons 2 :mw 132 :class :polar     :abbrev "N" :name "Asparagine"}
   :Asp {:codons 2 :mw 133 :class :negative  :abbrev "D" :name "Aspartate"}
   :Cys {:codons 2 :mw 121 :class :polar     :abbrev "C" :name "Cysteine"}
   :Gln {:codons 2 :mw 146 :class :polar     :abbrev "Q" :name "Glutamine"}
   :Glu {:codons 2 :mw 147 :class :negative  :abbrev "E" :name "Glutamate"}
   :Gly {:codons 4 :mw  75 :class :nonpolar  :abbrev "G" :name "Glycine"}
   :His {:codons 2 :mw 155 :class :positive  :abbrev "H" :name "Histidine"}
   :Ile {:codons 3 :mw 131 :class :nonpolar  :abbrev "I" :name "Isoleucine"}
   :Leu {:codons 6 :mw 131 :class :nonpolar  :abbrev "L" :name "Leucine"}
   :Lys {:codons 2 :mw 146 :class :positive  :abbrev "K" :name "Lysine"}
   :Met {:codons 1 :mw 149 :class :nonpolar  :abbrev "M" :name "Methionine"}
   :Phe {:codons 2 :mw 165 :class :nonpolar  :abbrev "F" :name "Phenylalanine"}
   :Pro {:codons 4 :mw 115 :class :nonpolar  :abbrev "P" :name "Proline"}
   :Ser {:codons 6 :mw 105 :class :polar     :abbrev "S" :name "Serine"}
   :Thr {:codons 4 :mw 119 :class :polar     :abbrev "T" :name "Threonine"}
   :Trp {:codons 1 :mw 204 :class :nonpolar  :abbrev "W" :name "Tryptophan"}
   :Tyr {:codons 2 :mw 181 :class :polar     :abbrev "Y" :name "Tyrosine"}
   :Val {:codons 4 :mw 117 :class :nonpolar  :abbrev "V" :name "Valine"}
   :Sec {:codons 1 :mw 168 :class :polar     :abbrev "U" :name "Selenocysteine"}
   :Pyl {:codons 1 :mw 255 :class :nonpolar  :abbrev "O" :name "Pyrrolysine"}})

;; ── Test 1: Letter Fingerprints ───────────────────────────────
;; For each of the 22 letters, run the oracle.
;; How many illumination sets? How many known words? Which words?

(defn letter-fingerprint
  "Oracle fingerprint of a single Hebrew letter."
  [ch]
  (let [positions (get o/letter-index ch [])
        n-positions (count positions)
        stones (set (map first positions))
        ;; Forward: what words does the oracle produce from this letter's positions?
        ;; We need to use the forward function on the letter as a string
        f (o/forward (str ch) :torah)]
    {:letter (str ch)
     :gv (g/letter-value ch)
     :positions n-positions
     :stones stones
     :n-stones (count stones)
     :illuminations (:illumination-count f)
     :total-readings (:total-readings f)
     :known-words (count (:known-words f))
     :top-words (vec (take 5 (map (fn [w] {:word (:word w) :count (:reading-count w)})
                                   (:known-words f))))}))

(defn all-fingerprints []
  (mapv letter-fingerprint all-letters))

;; ── Test 2: Row/Column Structure ──────────────────────────────
;; Which letters live on which rows and columns?
;; If rows = bases, each row should have a distinct character.

(defn row-profile [row-num]
  (let [stones (filter #(= row-num (:row (grid %))) (keys grid))
        letters (mapcat #(:letters (grid %)) stones)
        unique (set letters)]
    {:row row-num
     :stones (vec stones)
     :tribes (mapv #(:tribe (grid %)) stones)
     :letters (vec letters)
     :unique-letters (vec (sort-by g/letter-value unique))
     :n-unique (count unique)
     :singletons (vec (filter #(= 1 (letter-freq %)) unique))
     :letter-gv-sum (reduce + (map g/letter-value letters))}))

(defn col-profile [col-num]
  (let [stones (filter #(= col-num (:col (grid %))) (keys grid))
        letters (mapcat #(:letters (grid %)) stones)
        unique (set letters)]
    {:col col-num
     :stones (vec stones)
     :tribes (mapv #(:tribe (grid %)) stones)
     :letters (vec letters)
     :unique-letters (vec (sort-by g/letter-value unique))
     :n-unique (count unique)
     :singletons (vec (filter #(= 1 (letter-freq %)) unique))
     :letter-gv-sum (reduce + (map g/letter-value letters))}))

;; ── Test 3: Frequency Correlation ─────────────────────────────
;; Do letters with more grid positions correspond to amino acids
;; with more codons?

(def aa-by-codon-count
  "Amino acids grouped by how many codons they have."
  (group-by :codons (vals aa-properties)))

(def letters-by-freq
  "Letters grouped by how many times they appear on the grid."
  (group-by letter-freq (keys letter-freq)))

;; ── Test 4: Stone Triplet Codons ──────────────────────────────
;; If rows=bases and cols=codon-positions, a codon is 3 stones.
;; What do the letters spell when you combine stones from each column?

(defn stone-triplet
  "Given a base assignment (which row for each codon position),
   return the three stones and their combined letters."
  [row1 row2 row3]
  (let [s1 (first (filter #(and (= row1 (:row (grid %)))
                                (= 1 (:col (grid %))))
                          (keys grid)))
        s2 (first (filter #(and (= row2 (:row (grid %)))
                                (= 2 (:col (grid %))))
                          (keys grid)))
        s3 (first (filter #(and (= row3 (:row (grid %)))
                                (= 3 (:col (grid %))))
                          (keys grid)))]
    {:stones [s1 s2 s3]
     :tribes [(when s1 (:tribe (grid s1)))
              (when s2 (:tribe (grid s2)))
              (when s3 (:tribe (grid s3)))]
     :letters [(when s1 (:letters (grid s1)))
               (when s2 (:letters (grid s2)))
               (when s3 (:letters (grid s3)))]
     :all-letters (vec (concat (when s1 (:letters (grid s1)))
                               (when s2 (:letters (grid s2)))
                               (when s3 (:letters (grid s3)))))}))

(defn all-64-codons
  "Generate all 64 possible row-triplet combinations (4^3).
   Each represents a potential codon if rows = bases."
  []
  (for [r1 [1 2 3 4]
        r2 [1 2 3 4]
        r3 [1 2 3 4]]
    (assoc (stone-triplet r1 r2 r3)
           :codon-rows [r1 r2 r3])))

;; ── Test 5: Oracle on Codon Triplets ──────────────────────────
;; For each of the 64 row-triplets, combine the 18 letters from
;; 3 stones and ask the oracle what it sees.

(defn codon-oracle
  "Run the oracle on the combined letters of a stone triplet."
  [{:keys [all-letters codon-rows] :as triplet}]
  (let [letters-str (apply str all-letters)
        f (o/forward letters-str :torah)]
    (assoc triplet
           :illuminations (:illumination-count f)
           :total-readings (:total-readings f)
           :known-words (count (:known-words f))
           :top-5 (vec (take 5 (map (fn [w]
                                       {:word (:word w)
                                        :count (:reading-count w)
                                        :readers (:readers w)})
                                     (:known-words f)))))))

;; ── Test 6: Merged Form Analysis ─────────────────────────────

(defn base-letter
  "Map final forms to their medial equivalents."
  [ch]
  (case ch \ם \מ \ן \נ \ף \פ \ך \כ \ץ \צ ch))

(def merged-freq
  "Canonical letter frequencies (medial+final merged)."
  (frequencies (map base-letter (mapcat (fn [[s l _ _]] (vec l)) o/stone-data))))

(def canonical-singletons
  (vec (sort-by g/letter-value (for [[ch cnt] merged-freq :when (= 1 cnt)] ch))))

(def non-singleton-letters
  (vec (sort-by g/letter-value (for [[ch cnt] merged-freq :when (> cnt 1)] ch))))

;; ── Test 7: Universal Letters ────────────────────────────────

(defn row-non-singletons [r]
  (let [stones (filter (fn [[s _ row _]] (= r row)) o/stone-data)
        chars (set (map base-letter (mapcat (fn [[_ l _ _]] (vec l)) stones)))]
    (set (filter #(> (merged-freq %) 1) chars))))

(def universal-letters
  "Letters present in all 4 rows."
  (apply clojure.set/intersection (map row-non-singletons [1 2 3 4])))

;; ── Test 8: Illumination Patterns ────────────────────────────

(defn word-pattern [word]
  (let [ilsets (o/illumination-sets word)
        stones (when (seq ilsets) (set (mapcat #(map first %) ilsets)))]
    {:word word :gv (g/word-value word)
     :stones (when stones (vec (sort stones)))
     :rows (when stones (vec (sort (set (map o/stone-row stones)))))
     :cols (when stones (vec (sort (set (map o/stone-col stones)))))
     :illuminations (count ilsets)}))

(def output-dir "data/experiments/100")

(def key-pattern-words
  ["חי" "כבש" "שכב" "את" "כפר" "גפר" "עץ" "יהוה" "אלהים" "למינהו" "נח" "דם"])

(def sample-codon-rows
  [[1 1 1] [2 2 2] [3 3 3] [4 4 4]
   [1 2 3] [4 3 2] [1 3 1] [2 4 2]])

(defn- ensure-output-dir! []
  (.mkdirs (io/file output-dir)))

(defn- save-edn! [filename data]
  (spit (str output-dir "/" filename) (pr-str data)))

(defn structure-summary []
  {:the-split {:coding-positions (reduce + (map merged-freq non-singleton-letters))
               :singleton-positions (count canonical-singletons)
               :non-singleton-letters (count non-singleton-letters)
               :singleton-letters (count canonical-singletons)
               :total-unique-letters (count merged-freq)}
   :the-backbone {:universal-letters (mapv str (sort-by g/letter-value universal-letters))
                  :universal-gv (mapv g/letter-value (sort-by g/letter-value universal-letters))
                  :universal-gv-sum (reduce + (map g/letter-value universal-letters))
                  :universal-positions (reduce + (map merged-freq universal-letters))
                  :per-row (into {}
                                 (for [r [1 2 3 4]]
                                   [r (count (row-non-singletons r))]))}
   :the-pairing {:row-1-plus-4 {:coding (+ (reduce + (map merged-freq (row-non-singletons 1)))
                                           (reduce + (map merged-freq (row-non-singletons 4))))
                                :specific-1 (count (clojure.set/difference (row-non-singletons 1) (row-non-singletons 4)))
                                :specific-4 (count (clojure.set/difference (row-non-singletons 4) (row-non-singletons 1)))}
                 :row-2-plus-3 {:coding (+ (reduce + (map merged-freq (row-non-singletons 2)))
                                           (reduce + (map merged-freq (row-non-singletons 3))))
                                :specific-2 (count (clojure.set/difference (row-non-singletons 2) (row-non-singletons 3)))
                                :specific-3 (count (clojure.set/difference (row-non-singletons 3) (row-non-singletons 2)))}}
   :column-2-center {:singletons (count (filter #(= 1 (:col (grid (first (get o/letter-index %))))) canonical-singletons))
                     :out-of (count canonical-singletons)}
   :aleph-tav {:columns (:cols (word-pattern "את"))
               :skips-col-2 (= [1 3] (:cols (word-pattern "את")))}
   :row-3-guard {:singletons (count (:singletons (row-profile 3)))
                 :one-per-column (= 3 (count (:singletons (row-profile 3))))}
   :row-exclusive {:qof-only-row-1
                   (= #{1}
                      (set (map (comp :row grid first) (get o/letter-index \ק))))}
   :singleton-words {:chi-life "חי=18"
                     :ki-because "כי=30"
                     :zo-this "זו=13"}})

(defn findings-summary []
  {:finding "72 = 64 + 8"
   :detail "14 non-singleton letters fill 64 positions. 8 singleton letters fill 8. 64 codons."
   :column-2-info "4 of 8 singletons in center column — highest information at 2nd codon position"
   :row-3-info "Only row with singleton in every column"
   :alphabet-match "22 letters = 22 amino acids"})

(defn grid-summary []
  {:rows (mapv row-profile [1 2 3 4])
   :cols (mapv col-profile [1 2 3])
   :letter-freq (into (sorted-map-by #(compare (g/letter-value %1) (g/letter-value %2))) letter-freq)
   :merged-freq (into (sorted-map-by #(compare (g/letter-value %1) (g/letter-value %2))) merged-freq)
   :singletons (mapv str canonical-singletons)
   :non-singletons (mapv str non-singleton-letters)
   :universal-letters (mapv str (sort-by g/letter-value universal-letters))
   :letters-by-freq (into (sorted-map)
                          (for [[freq letters] letters-by-freq]
                            [freq (mapv str (sort-by g/letter-value letters))]))
   :aa-by-codon-count (into (sorted-map)
                            (for [[cnt aas] aa-by-codon-count]
                              [cnt (mapv :name aas)]))})

(defn illumination-patterns []
  {:patterns (mapv word-pattern key-pattern-words)
   :same-stones? {:כבש=שכב (= (:stones (word-pattern "כבש"))
                             (:stones (word-pattern "שכב")))}
   :sample-codon-rows sample-codon-rows})

(defn summary []
  (merge (findings-summary)
         {:coding-positions (reduce + (map merged-freq non-singleton-letters))
          :singleton-positions (count canonical-singletons)
          :backbone-letters (mapv str (sort-by g/letter-value universal-letters))
          :backbone-positions (reduce + (map merged-freq universal-letters))
          :backbone-name-value (reduce + (map g/letter-value universal-letters))
          :aleph-tav-columns (:cols (word-pattern "את"))
          :lamb-lying-same-stones (= (:stones (word-pattern "כבש"))
                                     (:stones (word-pattern "שכב")))}))

(defn report-text []
  (let [fps (all-fingerprints)
        rows (map row-profile [1 2 3 4])
        cols (map col-profile [1 2 3])
        patterns (map word-pattern key-pattern-words)]
    (with-out-str
      (println "=== Experiment 100: The Code ===")
      (println)
      (println (format "coding positions: %d" (reduce + (map merged-freq non-singleton-letters))))
      (println (format "singleton positions: %d" (count canonical-singletons)))
      (println (format "universal letters: %s" (str/join " " (map str (sort-by g/letter-value universal-letters)))))
      (println (format "backbone positions: %d" (reduce + (map merged-freq universal-letters))))
      (println)
      (println "Letter fingerprints:")
      (doseq [fp (sort-by (juxt (comp - :positions) :gv) fps)]
        (println (format "  %s gv=%3d pos=%2d illum=%d read=%d known=%d"
                         (:letter fp) (:gv fp) (:positions fp)
                         (:illuminations fp) (:total-readings fp) (:known-words fp))))
      (println)
      (println "Rows:")
      (doseq [r rows]
        (println (format "  row %d: unique=%d singletons=%s gv=%d"
                         (:row r) (:n-unique r) (apply str (:singletons r)) (:letter-gv-sum r))))
      (println)
      (println "Columns:")
      (doseq [c cols]
        (println (format "  col %d: unique=%d singletons=%s gv=%d"
                         (:col c) (:n-unique c) (apply str (:singletons c)) (:letter-gv-sum c))))
      (println)
      (println "Patterns:")
      (doseq [p patterns]
        (println (format "  %s → stones=%s cols=%s illum=%d"
                         (:word p) (pr-str (:stones p)) (pr-str (:cols p)) (:illuminations p)))))))

(defn run-experiment! []
  (ensure-output-dir!)
  (save-edn! "letters.edn" (all-fingerprints))
  (save-edn! "grid.edn" (grid-summary))
  (save-edn! "structure.edn" (structure-summary))
  (save-edn! "findings.edn" (findings-summary))
  (save-edn! "illumination-patterns.edn" (illumination-patterns))
  (save-edn! "summary.edn" (summary))
  (spit (str output-dir "/output.txt") (report-text))
  {:output-dir output-dir
   :summary (summary)})

;; ── Run ───────────────────────────────────────────────────────

(comment
  ;; KEY FINDING: 72 = 64 + 8
  (reduce + (map merged-freq non-singleton-letters))  ;; => 64
  (count canonical-singletons)                         ;; => 8

  ;; BACKBONE: ו, י, נ in all 4 rows = 26 positions = YHWH
  universal-letters
  (reduce + (map merged-freq universal-letters))       ;; => 26

  ;; PAIRING: Row 1+4 = 32, Row 2+3 = 32 (both halves equal)

  ;; את skips column 2 — boundary marker avoids content column
  (word-pattern "את")  ;; cols [1 3]

  ;; כבש = שכב same stones, different reading
  (= (:stones (word-pattern "כבש")) (:stones (word-pattern "שכב")))

  ;; --- Part 1: Letter Fingerprints ---
  (def fps (all-fingerprints))

  ;; Display sorted by grid frequency
  (doseq [fp (sort-by (comp - :positions) fps)]
    (println (format "%s (GV=%3d) pos=%2d stones=%d illum=%d readings=%d known=%d  %s"
                     (:letter fp) (:gv fp) (:positions fp)
                     (:n-stones fp) (:illuminations fp)
                     (:total-readings fp) (:known-words fp)
                     (str/join ", " (map #(str (:word %) "(" (:count %) ")")
                                         (:top-words fp))))))

  ;; --- Part 2: Row Profiles ---
  (doseq [r (map row-profile [1 2 3 4])]
    (println (format "\nRow %d: %s" (:row r) (str/join ", " (:tribes r))))
    (println (format "  Letters: %s" (apply str (:letters r))))
    (println (format "  Unique: %d  Singletons: %s  GV sum: %d"
                     (:n-unique r)
                     (apply str (:singletons r))
                     (:letter-gv-sum r))))

  ;; --- Part 3: Column Profiles ---
  (doseq [c (map col-profile [1 2 3])]
    (println (format "\nCol %d: %s" (:col c) (str/join ", " (:tribes c))))
    (println (format "  Letters: %s" (apply str (:letters c))))
    (println (format "  Unique: %d  Singletons: %s  GV sum: %d"
                     (:n-unique c)
                     (apply str (:singletons c))
                     (:letter-gv-sum c))))

  ;; --- Part 4: Distribution comparison ---
  (println "\n=== LETTER FREQUENCY DISTRIBUTION ===")
  (doseq [[freq letters] (sort letters-by-freq)]
    (println (format "  Freq %2d: %d letters — %s"
                     freq (count letters) (apply str (sort-by g/letter-value letters)))))

  (println "\n=== AMINO ACID CODON COUNT DISTRIBUTION ===")
  (doseq [[cnt aas] (sort aa-by-codon-count)]
    (println (format "  %d codons: %d amino acids — %s"
                     cnt (count aas) (str/join ", " (map :name aas)))))

  ;; --- Part 5: Singleton deep-dive ---
  (println "\n=== SINGLETONS ===")
  (doseq [ch singletons]
    (let [fp (letter-fingerprint ch)
          pos (first (get o/letter-index ch))
          stone (first pos)
          idx (second pos)]
      (println (format "%s (GV=%3d) stone=%d(%s) idx=%d row=%d col=%d"
                       (str ch) (:gv fp) stone (:tribe (grid stone))
                       idx (:row (grid stone)) (:col (grid stone))))))

  ;; --- Part 6: Row triplet sample ---
  ;; Take a few codons and see what the oracle reads
  (println "\n=== SAMPLE CODONS (row-triplets) ===")
  (let [samples [[1 1 1] [2 2 2] [3 3 3] [4 4 4]  ;; diagonal
                 [1 2 3] [4 3 2] [1 3 1] [2 4 2]]] ;; mixed
    (doseq [rows samples]
      (let [trip (stone-triplet (first rows) (second rows) (nth rows 2))
            letters-str (apply str (:all-letters trip))
            f (o/forward letters-str :torah)
            top3 (take 3 (:known-words f))]
        (println (format "\nCodon %s → stones %s (%s)"
                         rows (:stones trip) (str/join "+" (:tribes trip))))
        (println (format "  Letters: %s  Illuminations: %d  Known: %d"
                         letters-str (:illumination-count f) (count (:known-words f))))
        (doseq [w top3]
          (println (format "    %s (%d readings, GV=%d)"
                           (:word w) (:reading-count w) (:gv w)))))))

  nil)
