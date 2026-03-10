(ns experiments.127-pathogen-prescriptions
  "Experiment 127: The Pathogen Carries the Prescription.

   M. leprae proteins don't contain the serpent (unlike viruses).
   They contain Nazareth, the lamb, the altar, the forehead.
   The disease carries its cure.

   Question: Is this about mycobacteria specifically
   or about the map itself?

   1. M. tuberculosis key proteins
   2. Sickle cell hemoglobin (one amino acid change: Glu→Val = ל→ט)
   3. Normal hemoglobin-beta (already done exp 105) vs sickle cell
   4. p53 (tumor suppressor — guardian of the genome)
   5. p53 R175H (most common cancer mutation)

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dna :as dna]
            [clojure.string :as str]))

;; ── Protein runner (same as 123) ──────────────────────

(defn run-protein
  "Run a protein through the breastplate at 3-letter and 4-letter windows."
  [{:keys [name accession why]} & [{:keys [search-words] :or {search-words []}}]]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  %-45s ║" name))
  (println (format "║  %-45s ║" accession))
  (println (format "║  %-45s ║" (if (> (count why) 45) (str (subs why 0 42) "...") why)))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [p (dna/fetch-uniprot accession)]
    (when-not p
      (println "  !! Could not fetch protein.")
      (throw (ex-info "fetch failed" {:accession accession})))

    (let [seq-str  (:sequence p)
          hebrew   (dna/protein-str->hebrew seq-str)
          gv       (g/word-value hebrew)
          hits-3   (dna/slide hebrew {:window 3 :vocab :torah})
          hits-4   (dna/slide hebrew {:window 4 :vocab :torah})
          top-3    (dna/word-frequencies hits-3)
          top-4    (dna/word-frequencies hits-4)]

      (println (format "\n  %d residues → %d Hebrew letters. GV=%d."
                       (count seq-str) (count hebrew) gv))

      ;; Factor the GV
      (print "  Factors: ")
      (loop [n gv, d 2, factors []]
        (cond
          (> (* d d) n) (let [fs (if (> n 1) (conj factors n) factors)]
                          (println (str/join " × " fs)))
          (zero? (mod n d)) (recur (/ n d) d (conj factors d))
          :else (recur n (inc d) factors)))

      ;; Key divisibility
      (doseq [k [7 13 26 50 53 67 72 137]]
        (when (zero? (mod gv k))
          (println (format "  GV ÷ %d = %d  ← %s" k (/ gv k)
                           (case k
                             7   "completeness"
                             13  "love/unity"
                             26  "YHWH"
                             50  "jubilee"
                             53  "garden"
                             67  "understanding"
                             72  "breastplate"
                             137 "axis sum")))))

      ;; Hebrew string
      (println (format "\n  Hebrew: %s" (if (> (count hebrew) 120)
                                          (str (subs hebrew 0 120) "...")
                                          hebrew)))

      ;; Letter frequencies
      (println "\n  Letter frequencies:")
      (doseq [[ch cnt] (sort-by val > (frequencies hebrew))]
        (println (format "    %s → %-4s = %3d (%.1f%%)"
                         ch (clojure.core/name (get dna/letter->aa ch :?))
                         cnt (* 100.0 (/ (double cnt) (count hebrew))))))

      ;; Serpent search
      (println "\n  ── Serpent search ──")
      (let [serpent-target #{\נ \ח \ש}
            serpent-hits (for [i (range (- (count hebrew) 2))
                              :let [w (subs hebrew i (+ i 3))]
                              :when (= (set w) serpent-target)]
                          {:pos i :letters w :exact? (= w "נחש")})]
        (if (seq serpent-hits)
          (doseq [{:keys [pos letters exact?]} serpent-hits]
            (println (format "    [%3d] %s %s" pos letters
                             (if exact? "— EXACT SERPENT" "— anagram"))))
          (println "    No serpent found.")))

      ;; Disease-specific word search
      (println "\n  ── Key word search ──")
      (let [all-search (concat ["נגע" "טמא" "טהר" "בשר" "שער" "כהן" "רפא" "מות" "חיה"
                                 "נזה" "כפר" "דם" "שמן" "חלי" "עור" "שרף" "נחש" "תחש"
                                 "קדש" "הוה" "עקד" "כבש" "אדם" "חטא" "כבד"]
                                search-words)]
        (doseq [tw all-search]
          (let [positions (for [i (range (- (count hebrew) (dec (count tw))))
                                :when (= (subs hebrew i (+ i (count tw))) tw)]
                            i)]
            (when (seq positions)
              (println (format "    %s at %s" tw (str/join ", " positions)))))))

      ;; 3-letter readings
      (println (format "\n  ── 3-letter sliding window ──"))
      (println (format "  %d/%d windows produced readings."
                       (count hits-3) (- (count hebrew) 2)))
      (doseq [{:keys [position letters top-5]} hits-3]
        (let [top (first top-5)]
          (println (format "    [%3d] %s → %s"
                           position letters
                           (:word top)))))
      (println "\n  Top 3-letter words:")
      (doseq [{:keys [word count]} (take 20 top-3)]
        (println (format "    %-8s ×%d" word count)))

      ;; 4-letter readings
      (println (format "\n  ── 4-letter sliding window ──"))
      (println (format "  %d/%d windows produced readings."
                       (count hits-4) (- (count hebrew) 3)))
      (doseq [{:keys [position letters top-5]} hits-4]
        (let [top (first top-5)]
          (println (format "    [%3d] %s → %s"
                           position letters
                           (:word top)))))
      (println "\n  Top 4-letter words:")
      (doseq [{:keys [word count]} (take 20 top-4)]
        (println (format "    %-8s ×%d" word count)))

      ;; Basin walks for top words
      (println "\n  ── Basin walks (top 10 words) ──")
      (doseq [{:keys [word]} (take 10 top-3)]
        (when word
          (let [walk (basin/walk word)]
            (println (format "    %s → %s  path: %s"
                             word
                             (:fixed-point walk)
                             (mapv :word (:steps walk)))))))

      ;; Save
      (let [slug (-> name str/lower-case (str/replace #"[^a-z0-9]+" "-"))]
        (spit (str "data/dna/" slug "-oracle.edn")
              (pr-str {:name name :accession accession :why why
                       :residues (count seq-str) :hebrew hebrew :gv gv
                       :letter-frequencies (frequencies hebrew)
                       :top-3 (vec (take 30 top-3))
                       :top-4 (vec (take 30 top-4))}))
        (println (format "\n  Saved: data/dna/%s-oracle.edn" slug)))

      {:name name :accession accession
       :hebrew hebrew :gv gv :length (count seq-str)
       :top-3 top-3 :top-4 top-4
       :hits-3 hits-3 :hits-4 hits-4})))

;; ══════════════════════════════════════════════════════
;; PART 1: M. TUBERCULOSIS
;; ══════════════════════════════════════════════════════

(def tb-proteins
  [{:name "TB-Ag85B"
    :accession "P9WQP1"
    :why "Antigen 85B. Key vaccine target. Mycolic acid synthesis. Compare with leprae Ag85B."}
   {:name "TB-ESAT-6"
    :accession "P9WNK7"
    :why "ESAT-6. Early secreted antigen. Virulence factor. Disrupts host membranes."}
   {:name "TB-CFP-10"
    :accession "P9WNK5"
    :why "Culture filtrate protein 10 kDa. Forms complex with ESAT-6. TB diagnostic marker."}
   {:name "TB-Hsp65"
    :accession "P9WMG9"
    :why "60 kDa chaperonin 2 (GroEL2). Major T-cell antigen. Compare with leprae Hsp65."}
   {:name "TB-KatG"
    :accession "P9WIE5"
    :why "Catalase-peroxidase. Activates isoniazid. Drug resistance when mutated."}])

;; ══════════════════════════════════════════════════════
;; PART 2: HEMOGLOBIN — NORMAL vs SICKLE CELL
;; ══════════════════════════════════════════════════════

(def hemoglobin-proteins
  [{:name "HBB-Normal"
    :accession "P68871"
    :why "Hemoglobin beta chain (normal). 147 residues. Position 6: Glu (ל)."}
   {:name "HBB-Sickle"
    :accession "P68871"
    :why "Hemoglobin beta chain — SICKLE MUTANT. Position 6: Glu→Val (ל→ט). Manual mutation."}])

;; ══════════════════════════════════════════════════════
;; PART 3: p53 — GUARDIAN OF THE GENOME
;; ══════════════════════════════════════════════════════

(def p53-proteins
  [{:name "p53-Normal"
    :accession "P04637"
    :why "Tumor protein p53. Guardian of the genome. 393 residues."}
   {:name "p53-R175H"
    :accession "P04637"
    :why "p53 R175H — most common cancer mutation. Arg→His at 175. Manual mutation."}])

;; ══════════════════════════════════════════════════════
;; RUNNERS
;; ══════════════════════════════════════════════════════

(defn run-protein-with-mutation
  "Run a protein, optionally applying a single amino acid mutation."
  [{:keys [name accession why]} & [{:keys [mutation]}]]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  %-45s ║" name))
  (println (format "║  %-45s ║" accession))
  (println (format "║  %-45s ║" (if (> (count why) 45) (str (subs why 0 42) "...") why)))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [p (dna/fetch-uniprot accession)]
    (when-not p
      (println "  !! Could not fetch protein.")
      (throw (ex-info "fetch failed" {:accession accession})))

    (let [seq-str  (if mutation
                     (let [{:keys [pos from to]} mutation
                           original (:sequence p)
                           actual-aa (nth original pos)]
                       (println (format "  MUTATION: position %d, %s→%s (actual: %s)"
                                        pos from to actual-aa))
                       (str (subs original 0 pos) to (subs original (inc pos))))
                     (:sequence p))
          hebrew   (dna/protein-str->hebrew seq-str)
          gv       (g/word-value hebrew)
          hits-3   (dna/slide hebrew {:window 3 :vocab :torah})
          hits-4   (dna/slide hebrew {:window 4 :vocab :torah})
          top-3    (dna/word-frequencies hits-3)
          top-4    (dna/word-frequencies hits-4)]

      (println (format "\n  %d residues → %d Hebrew letters. GV=%d."
                       (count seq-str) (count hebrew) gv))

      ;; Factor the GV
      (print "  Factors: ")
      (loop [n gv, d 2, factors []]
        (cond
          (> (* d d) n) (let [fs (if (> n 1) (conj factors n) factors)]
                          (println (str/join " × " fs)))
          (zero? (mod n d)) (recur (/ n d) d (conj factors d))
          :else (recur n (inc d) factors)))

      ;; Key divisibility
      (doseq [k [7 13 26 50 53 67 72 137]]
        (when (zero? (mod gv k))
          (println (format "  GV ÷ %d = %d  ← %s" k (/ gv k)
                           (case k
                             7   "completeness"
                             13  "love/unity"
                             26  "YHWH"
                             50  "jubilee"
                             53  "garden"
                             67  "understanding"
                             72  "breastplate"
                             137 "axis sum")))))

      ;; Hebrew string (first 120 chars)
      (println (format "\n  Hebrew: %s" (if (> (count hebrew) 120)
                                          (str (subs hebrew 0 120) "...")
                                          hebrew)))

      ;; Serpent search
      (println "\n  ── Serpent search ──")
      (let [serpent-target #{\נ \ח \ש}
            serpent-hits (for [i (range (- (count hebrew) 2))
                              :let [w (subs hebrew i (+ i 3))]
                              :when (= (set w) serpent-target)]
                          {:pos i :letters w :exact? (= w "נחש")})]
        (if (seq serpent-hits)
          (doseq [{:keys [pos letters exact?]} serpent-hits]
            (println (format "    [%3d] %s %s" pos letters
                             (if exact? "— EXACT SERPENT" "— anagram"))))
          (println "    No serpent found.")))

      ;; Key word search
      (println "\n  ── Key word search ──")
      (doseq [tw ["נגע" "טמא" "טהר" "בשר" "שער" "כהן" "רפא" "מות" "חיה"
                   "נזה" "כפר" "דם" "שמן" "חלי" "עור" "שרף" "נחש" "תחש"
                   "קדש" "הוה" "כבש" "כבד" "חטא" "אדם" "דבר"]]
        (let [positions (for [i (range (- (count hebrew) (dec (count tw))))
                              :when (= (subs hebrew i (+ i (count tw))) tw)]
                          i)]
          (when (seq positions)
            (println (format "    %s at %s" tw (str/join ", " positions))))))

      ;; Top words
      (println "\n  Top 3-letter words:")
      (doseq [{:keys [word count]} (take 15 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 4-letter words:")
      (doseq [{:keys [word count]} (take 15 top-4)]
        (println (format "    %-8s ×%d" word count)))

      ;; Basin walks for top words
      (println "\n  ── Basin walks (top 10 words) ──")
      (doseq [{:keys [word]} (take 10 top-3)]
        (when word
          (let [walk (basin/walk word)]
            (println (format "    %s → %s  path: %s"
                             word
                             (:fixed-point walk)
                             (mapv :word (:steps walk)))))))

      ;; Save
      (let [slug (-> name str/lower-case (str/replace #"[^a-z0-9]+" "-"))]
        (spit (str "data/dna/" slug "-oracle.edn")
              (pr-str {:name name :accession accession :why why
                       :residues (count seq-str) :hebrew hebrew :gv gv
                       :letter-frequencies (frequencies hebrew)
                       :top-3 (vec (take 30 top-3))
                       :top-4 (vec (take 30 top-4))}))
        (println (format "\n  Saved: data/dna/%s-oracle.edn" slug)))

      {:name name :accession accession
       :hebrew hebrew :gv gv :length (count seq-str)
       :top-3 top-3 :top-4 top-4
       :hits-3 hits-3 :hits-4 hits-4})))

;; ══════════════════════════════════════════════════════
;; FULL RUN
;; ══════════════════════════════════════════════════════

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 127: THE PATHOGEN CARRIES THE PRESCRIPTION")
  (println "  TB, sickle cell, p53 — through the breastplate")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  ;; Part 1: TB
  (println "\n\n══ PART 1: M. TUBERCULOSIS ══")
  (let [tb-results (mapv run-protein tb-proteins)]

    ;; Part 2: Hemoglobin normal vs sickle
    (println "\n\n══ PART 2: HEMOGLOBIN — NORMAL vs SICKLE CELL ══")
    (let [hbb-normal (run-protein-with-mutation (first hemoglobin-proteins))
          ;; Sickle cell: position 5 (0-indexed, after M removed → position 6 in mature),
          ;; Glu(E)→Val(V)
          hbb-sickle (run-protein-with-mutation (second hemoglobin-proteins)
                                                {:mutation {:pos 5 :from "E" :to "V"}})]

      ;; Compare normal vs sickle
      (println "\n  ── Normal vs Sickle Comparison ──")
      (println (format "    Normal GV=%d, Sickle GV=%d, diff=%d"
                       (:gv hbb-normal) (:gv hbb-sickle)
                       (- (:gv hbb-sickle) (:gv hbb-normal))))
      (println (format "    The single mutation: Glu(ל)→Val(ט)  GV change: %d→%d (diff=%d)"
                       (g/letter-value \ל) (g/letter-value \ט)
                       (- (g/letter-value \ט) (g/letter-value \ל))))

      ;; Part 3: p53 normal vs R175H
      (println "\n\n══ PART 3: p53 — GUARDIAN OF THE GENOME ══")
      (let [p53-normal (run-protein-with-mutation (first p53-proteins))
            ;; R175H: position 174 (0-indexed), Arg(R)→His(H)
            p53-mutant (run-protein-with-mutation (second p53-proteins)
                                                  {:mutation {:pos 174 :from "R" :to "H"}})]

        ;; Compare normal vs mutant
        (println "\n  ── Normal vs R175H Comparison ──")
        (println (format "    Normal GV=%d, Mutant GV=%d, diff=%d"
                         (:gv p53-normal) (:gv p53-mutant)
                         (- (:gv p53-mutant) (:gv p53-normal))))

        ;; Cross-organism summary
        (println "\n\n════════════════════════════════════════════════")
        (println "  CROSS-ORGANISM SUMMARY")
        (println "════════════════════════════════════════════════")

        (doseq [r (concat tb-results [hbb-normal hbb-sickle p53-normal p53-mutant])]
          (println (format "  %-25s %4d residues  GV=%d  3-let=%d  4-let=%d"
                           (:name r) (:length r) (:gv r)
                           (count (:hits-3 r)) (count (:hits-4 r)))))

        {:tb tb-results
         :hbb-normal hbb-normal
         :hbb-sickle hbb-sickle
         :p53-normal p53-normal
         :p53-mutant p53-mutant}))))

(comment
  (run-all)

  ;; Quick checks
  (run-protein (first tb-proteins))
  (run-protein-with-mutation (first hemoglobin-proteins))

  ;; What does one amino acid change cost?
  ;; Glu(E) = ל (lamed, GV=30)
  ;; Val(V) = ט (tet, GV=9)
  ;; Change: 30 → 9 = -21 = ש reversed?
  (g/letter-value \ל) ;; 30
  (g/letter-value \ט) ;; 9

  nil)
