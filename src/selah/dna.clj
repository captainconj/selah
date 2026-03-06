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
            [clojure.string :as str]
            [clj-http.lite.client :as http]
            [clojure.java.io :as io]))

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

;; ── Full Experiment: slide + analyze + save ──────────────────

(defn slide
  "Slide a window across Hebrew letters, ask the oracle at each position.
   Returns vec of {:position :letters :gv :top-5} for windows with readings."
  ([hebrew] (slide hebrew {}))
  ([hebrew {:keys [window vocab] :or {window 3, vocab :torah}}]
   (let [n (count hebrew)]
     (vec (for [i (range 0 (- n (dec window)))
                :let [w (subs hebrew i (+ i window))
                      fwd (o/forward (seq w) vocab)
                      known (:known-words fwd)]
                :when (seq known)]
            {:position i
             :letters w
             :gv (g/word-value w)
             :top-5 (vec (take 5 (map (fn [k]
                                         {:word (:word k)
                                          :meaning (:meaning k)
                                          :reading-count (:reading-count k)})
                                       known)))})))))

(defn word-frequencies
  "From slide results, count how often each word appears as the top oracle word."
  [hits]
  (->> hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
               :count (count entries)
               :positions (mapv :position entries)}))
       (sort-by :count >)
       vec))

(defn experiment
  "Full experiment: fetch a protein by name, play it through the breastplate,
   slide windows, analyze word frequencies, save results to data/dna/.

   Returns the full result map. Also prints a summary.

   Options:
     :window — window size (default 3)
     :vocab  — oracle vocabulary (default :torah)
     :save?  — save to disk (default true)"
  ([name-pattern] (experiment name-pattern {}))
  ([name-pattern {:keys [window vocab save?]
                  :or {window 3, vocab :torah, save? true}}]
   (when-let [p (get-protein name-pattern)]
     (let [result (play (:sequence p) {:format :protein})
           hebrew (:hebrew result)
           hits (slide hebrew {:window window :vocab vocab})
           top-words (word-frequencies hits)
           slug (-> (:name p) str/lower-case (str/replace #"[^a-z0-9]+" "-"))

           full {:name (:name p)
                 :accession (:accession p)
                 :organism (:organism p)
                 :description (:description p)
                 :why (:why p)
                 :residues (count (:sequence p))
                 :hebrew hebrew
                 :protein-str (:protein-str result)
                 :gv (:gv result)
                 :letter-frequencies (frequencies hebrew)
                 :window-size window
                 :windows-with-readings (count hits)
                 :total-windows (- (count hebrew) (dec window))
                 :top-words (vec (take 50 top-words))
                 :all-windows hits}]

       ;; Print summary
       (println (str "=== " (:name p) " ==="))
       (println (str (:description p)))
       (println (str (count (:sequence p)) " residues → " (count hebrew) " Hebrew letters"))
       (println (str "GV: " (:gv result)))
       (println (str "Windows with readings: " (count hits) "/" (- (count hebrew) (dec window))))
       (println)

       ;; Letter frequencies
       (println "Letter frequencies:")
       (doseq [[ch cnt] (sort-by val > (frequencies hebrew))]
         (println (format "  %s → %-4s = %3d (%.1f%%)"
                          ch (name (get letter->aa ch :?))
                          cnt (* 100.0 (/ (double cnt) (count hebrew))))))
       (println)

       ;; All windows
       (println (str "Oracle readings (window=" window "):"))
       (doseq [{:keys [position letters gv top-5]} hits]
         (let [top (first top-5)]
           (println (format "  [%3d] %s (gv=%d) → %s %s (×%d)"
                            position letters gv
                            (:word top) (or (:meaning top) "")
                            (:reading-count top)))))
       (println)

       ;; Top words
       (println "Most frequent oracle words:")
       (doseq [{:keys [word meaning count positions]} (take 30 top-words)]
         (println (format "  %-8s %-25s ×%d  at %s"
                          word (or meaning "") count (pr-str positions))))
       (println)

       ;; Save
       (when save?
         (let [edn-path (str "data/dna/" slug "-oracle.edn")
               txt-path (str "data/dna/" slug "-report.txt")]
           (io/make-parents edn-path)
           (spit edn-path (pr-str full))
           ;; Also save the printed report
           (spit txt-path
                 (with-out-str
                   (println (str "=== " (:name p) " ==="))
                   (println (:description p))
                   (println (str (count (:sequence p)) " residues → " (count hebrew) " Hebrew letters"))
                   (println (str "Hebrew: " hebrew))
                   (println (str "GV: " (:gv result)))
                   (println)
                   (println "Letter frequencies:")
                   (doseq [[ch cnt] (sort-by val > (frequencies hebrew))]
                     (println (format "  %s → %-4s = %3d (%.1f%%)"
                                      ch (name (get letter->aa ch :?))
                                      cnt (* 100.0 (/ (double cnt) (count hebrew))))))
                   (println)
                   (println (str "Oracle readings (window=" window "):"))
                   (doseq [{:keys [position letters gv top-5]} hits]
                     (let [top (first top-5)]
                       (println (format "  [%3d] %s (gv=%d) → %s %s (×%d)"
                                        position letters gv
                                        (:word top) (or (:meaning top) "")
                                        (:reading-count top)))))
                   (println)
                   (println "Most frequent oracle words:")
                   (doseq [{:keys [word meaning count positions]} (take 40 top-words)]
                     (println (format "  %-8s %-25s ×%d  at %s"
                                      word (or meaning "") count (pr-str positions))))))
           (println (str "Saved: " edn-path))
           (println (str "Saved: " txt-path))))

       full))))

;; ── Sequence Collection ─────────────────────────────────────
;;
;; Fetch protein sequences from UniProt, cache to disk.
;; The library of life, ready to play through the breastplate.

(def sequences-dir "data/sequences")

(defn- ensure-dir! [dir]
  (.mkdirs (io/file dir)))

(defn- parse-fasta
  "Parse a FASTA string → {:id :description :sequence}."
  [fasta-str]
  (let [lines (str/split-lines (str/trim fasta-str))
        header (first lines)
        [_ id desc] (re-matches #">(\S+)\s*(.*)" (or header ""))
        seq-str (->> (rest lines)
                     (remove #(str/starts-with? % ">"))
                     (map str/trim)
                     (apply str))]
    {:id (or id "unknown")
     :description (or desc "")
     :sequence seq-str}))

(defn fetch-uniprot
  "Fetch a protein sequence from UniProt by accession. Caches to disk.
   Returns {:id :description :sequence :accession :cached?}."
  [accession]
  (let [dir (str sequences-dir "/uniprot")
        path (str dir "/" accession ".fasta")]
    (ensure-dir! dir)
    (if (.exists (io/file path))
      (assoc (parse-fasta (slurp path))
             :accession accession :cached? true :path path)
      (let [url (str "https://rest.uniprot.org/uniprotkb/" accession ".fasta")
            resp (http/get url {:throw-exceptions false})]
        (if (= 200 (:status resp))
          (do
            (spit path (:body resp))
            (println (str "[dna] Fetched " accession " from UniProt"))
            (assoc (parse-fasta (:body resp))
                   :accession accession :cached? false :path path))
          (do
            (println (str "[dna] Failed to fetch " accession ": " (:status resp)))
            nil))))))

(defn fetch-ncbi-protein
  "Fetch a protein sequence from NCBI by accession. Caches to disk."
  [accession]
  (let [dir (str sequences-dir "/ncbi")
        path (str dir "/" accession ".fasta")]
    (ensure-dir! dir)
    (if (.exists (io/file path))
      (assoc (parse-fasta (slurp path))
             :accession accession :cached? true :path path)
      (let [url (str "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
                      "?db=protein&id=" accession "&rettype=fasta&retmode=text")
            resp (http/get url {:throw-exceptions false})]
        (if (and (= 200 (:status resp))
                 (str/starts-with? (str/trim (:body resp)) ">"))
          (do
            (spit path (:body resp))
            (println (str "[dna] Fetched " accession " from NCBI"))
            (assoc (parse-fasta (:body resp))
                   :accession accession :cached? false :path path))
          (do
            (println (str "[dna] Failed to fetch " accession ": " (:status resp)))
            nil))))))

(defn load-fasta
  "Load a local FASTA file → {:id :description :sequence}."
  [path]
  (when (.exists (io/file path))
    (assoc (parse-fasta (slurp path)) :path path)))

;; ── The Library ─────────────────────────────────────────────
;;
;; Curated proteins, each chosen for a reason.

(def library
  "The proteins we want to play through the breastplate.
   Each entry: {:name :accession :source :why :length}"
  [;; ── The Guardians ──
   {:name "p53"
    :accession "P04637"
    :source :uniprot
    :why "Guardian of the genome. Named for 53. 53 = garden sum = Torah portions."
    :organism "Human"}

   {:name "BRCA1"
    :accession "P38398"
    :source :uniprot
    :why "Breast cancer guardian. DNA repair. The other guardian."
    :organism "Human"}

   ;; ── The Blood ──
   {:name "Hemoglobin-alpha"
    :accession "P69905"
    :source :uniprot
    :why "Carries oxygen. The blood. 141 residues."
    :organism "Human"}

   {:name "Hemoglobin-beta"
    :accession "P68871"
    :source :uniprot
    :why "Carries oxygen. Sickle cell = one letter change. 147 residues."
    :organism "Human"}

   {:name "Myoglobin"
    :accession "P02144"
    :source :uniprot
    :why "Oxygen in muscle. The first protein structure ever solved (1958)."
    :organism "Human"}

   ;; ── The Beginning ──
   {:name "Insulin"
    :accession "P01308"
    :source :uniprot
    :why "First protein sequenced (Sanger, 1951). Life and death in the blood."
    :organism "Human"}

   ;; ── The Structure ──
   {:name "Collagen-I-alpha1"
    :accession "P02452"
    :source :uniprot
    :why "Most abundant protein in the body. The scaffold. Gly-X-Y repeats."
    :organism "Human"}

   {:name "Laminin-gamma1"
    :accession "P11047"
    :source :uniprot
    :why "Cross-shaped protein. Holds cells together. The basement membrane."
    :organism "Human"}

   ;; ── The Scroll ──
   {:name "Histone-H3"
    :accession "P68431"
    :source :uniprot
    :why "Wraps DNA. The scroll around which the text is wound. 136 residues."
    :organism "Human"}

   {:name "Histone-H4"
    :accession "P62805"
    :source :uniprot
    :why "Most conserved protein in eukaryotes. The eternal scroll."
    :organism "Human"}

   ;; ── The Mark ──
   {:name "Ubiquitin"
    :accession "P0CG48"
    :source :uniprot
    :why "76 residues. Marks proteins for destruction. Judgment."
    :organism "Human"}

   ;; ── The Light ──
   {:name "Rhodopsin"
    :accession "P08100"
    :source :uniprot
    :why "Vision. Light → signal. The eye that sees."
    :organism "Human"}

   {:name "Cytochrome-c"
    :accession "P99999"
    :source :uniprot
    :why "Electron transport. The spark of life. Most conserved across species."
    :organism "Human"}

   ;; ── The Engine ──
   {:name "ATP-synthase-beta"
    :accession "P06576"
    :source :uniprot
    :why "The rotary engine. Makes ATP. The currency of life."
    :organism "Human"}

   ;; ── The Word ──
   {:name "RNA-polymerase-II"
    :accession "P24928"
    :source :uniprot
    :why "Reads DNA, writes RNA. The scribe. The largest subunit."
    :organism "Human"}

   {:name "Ribosomal-protein-S3"
    :accession "P23396"
    :source :uniprot
    :why "Part of the ribosome — the machine that reads the code."
    :organism "Human"}

   ;; ── The Ancient ──
   {:name "Ferredoxin"
    :accession "P10109"
    :source :uniprot
    :why "Iron-sulfur protein. Among the oldest proteins. Iron = 26 = YHWH."
    :organism "Human"}

   ;; ── The Signal ──
   {:name "Calmodulin"
    :accession "P0DP23"
    :source :uniprot
    :why "Calcium messenger. 149 residues. The signal that moves."
    :organism "Human"}

   ;; ── The Immune ──
   {:name "Immunoglobulin-G1"
    :accession "P01857"
    :source :uniprot
    :why "Antibody heavy chain. The guard. Recognition and defense."
    :organism "Human"}

   ;; ── The Forbidden ──
   {:name "Serpent-toxin-alpha"
    :accession "P60615"
    :source :uniprot
    :why "Alpha-cobratoxin. The serpent's weapon. 71 residues."
    :organism "Naja kaouthia"}

   ;; ── The Man ──
   {:name "FOXP2"
    :accession "O15409"
    :source :uniprot
    :why "The language protein. Two amino acids separate man from ape. The word made flesh."
    :organism "Human"}])

(defn fetch-library!
  "Fetch all proteins in the library. Caches to disk. Returns results."
  []
  (println (str "[dna] Fetching " (count library) " proteins..."))
  (let [results (doall
                  (for [{:keys [name accession source]} library]
                    (let [r (case source
                              :uniprot (fetch-uniprot accession)
                              :ncbi    (fetch-ncbi-protein accession)
                              nil)]
                      (when r
                        (println (str "  ✓ " name " (" accession "): "
                                      (count (:sequence r)) " residues")))
                      (assoc r :name name))))]
    (println (str "[dna] Done. " (count (filter :sequence results)) "/"
                  (count library) " fetched."))
    (vec results)))

(defn get-protein
  "Get a protein from the library by name (case-insensitive partial match).
   Fetches from cache or network."
  [name-pattern]
  (let [pat (str/lower-case name-pattern)
        entry (first (filter #(str/includes? (str/lower-case (:name %)) pat) library))]
    (when entry
      (let [r (case (:source entry)
                :uniprot (fetch-uniprot (:accession entry))
                :ncbi    (fetch-ncbi-protein (:accession entry))
                nil)]
        (when r
          (merge entry r))))))

(defn play-protein
  "Fetch a protein by name and play it through the breastplate."
  [name-pattern]
  (when-let [p (get-protein name-pattern)]
    (let [result (play (:sequence p) {:format :protein})]
      (assoc result
             :name (:name p)
             :accession (:accession p)
             :why (:why p)
             :organism (:organism p)))))

(defn catalog
  "Print the library catalog."
  []
  (println "=== THE LIBRARY ===")
  (println (str (count library) " proteins\n"))
  (doseq [{:keys [name accession organism why]} library]
    (println (format "  %-25s %-10s %-20s %s" name accession organism why)))
  (println))
