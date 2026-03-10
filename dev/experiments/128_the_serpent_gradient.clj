(ns experiments.128-the-serpent-gradient
  "Experiment 128: The Serpent Gradient.

   Experiment 127 found a gradient: virus→TB→leprosy→hemoglobin→p53.
   The serpent decreases as we move from parasite to self.
   The cure increases.

   Does it extend? Eight organisms across the full spectrum:

   1. Prion (PrP) — pure pathology. The misfolded protein IS the disease.
   2. Malaria (CSP) — eukaryotic parasite. The deadliest.
   3. Toxoplasma (SAG1) — mind-altering parasite.
   4. Candida (Als3) — fungus. Commensal that turns pathogenic.
   5. Lactobacillus (SlpA) — probiotic. Beneficial gut bacterium.
   6. Tardigrade (Dsup) — damage suppressor. Survives everything.
   7. Human insulin — the healing protein.
   8. Human oxytocin — the love hormone.

   Looking for: serpent (נחש), lamb (כבש), key numbers,
   basin walks, the gradient."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dna :as dna]
            [clojure.string :as str]))

;; ── Protein runner (streamlined from 127) ──────────────

(defn run-protein
  "Run a protein through the breastplate. Returns data map."
  [{:keys [name accession why]}]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  %-45s ║" name))
  (println (format "║  %-45s ║" accession))
  (println (format "║  %-45s ║" (if (> (count why) 45)
                                   (str (subs why 0 42) "...")
                                   why)))
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
          (do
            (println (format "    %d hits:" (count serpent-hits)))
            (doseq [{:keys [pos letters exact?]} serpent-hits]
              (println (format "    [%3d] %s %s" pos letters
                               (if exact? "— EXACT SERPENT" "— anagram")))))
          (println "    No serpent found.")))

      ;; Lamb search
      (println "\n  ── Lamb search ──")
      (let [lamb-target #{\כ \ב \ש}
            lamb-hits (for [i (range (- (count hebrew) 2))
                            :let [w (subs hebrew i (+ i 3))]
                            :when (= (set w) lamb-target)]
                        {:pos i :letters w :exact? (= w "כבש")})]
        (if (seq lamb-hits)
          (do
            (println (format "    %d hits:" (count lamb-hits)))
            (doseq [{:keys [pos letters exact?]} lamb-hits]
              (println (format "    [%3d] %s %s" pos letters
                               (if exact? "— EXACT LAMB" "— anagram (שכב=lie down)")))))
          (println "    No lamb found.")))

      ;; Key word search
      (println "\n  ── Key word search ──")
      (doseq [tw ["נגע" "טמא" "טהר" "בשר" "שער" "כהן" "רפא" "מות" "חיה"
                   "נזה" "כפר" "דם" "שמן" "חלי" "עור" "שרף" "נחש" "תחש"
                   "קדש" "הוה" "כבש" "כבד" "חטא" "אדם" "דבר" "חוה" "אהב"
                   "אור" "חיי" "שלם" "רחם" "גאל" "צלם"]]
        (let [positions (for [i (range (- (count hebrew) (dec (count tw))))
                              :when (= (subs hebrew i (+ i (count tw))) tw)]
                          i)]
          (when (seq positions)
            (println (format "    %s (%d×) at %s" tw (count positions)
                             (str/join ", " (take 10 positions)))))))

      ;; Top words
      (println "\n  Top 3-letter words:")
      (doseq [{:keys [word meaning count]} (take 15 top-3)]
        (println (format "    %-8s %-25s ×%d" word (or meaning "") count)))
      (println "\n  Top 4-letter words:")
      (doseq [{:keys [word meaning count]} (take 15 top-4)]
        (println (format "    %-8s %-25s ×%d" word (or meaning "") count)))

      ;; Basin walks for top words
      (println "\n  ── Basin walks (top 10 words) ──")
      (doseq [{:keys [word meaning]} (take 10 top-3)]
        (when word
          (let [walk (basin/walk word)]
            (println (format "    %s (%s) → %s"
                             word (or meaning "")
                             (or (:fixed-point walk) "null"))))))

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
;; THE EIGHT ORGANISMS
;; ══════════════════════════════════════════════════════

(def gradient-proteins
  [;; ── Pathogenic end ──
   {:name "Prion-PrP"
    :accession "P04156"
    :why "Human prion protein. The misfolded protein IS the disease. Pure pathology as information."}

   {:name "Malaria-CSP"
    :accession "P19597"
    :why "P. falciparum circumsporozoite protein. Vaccine target. The coat the parasite wears."}

   {:name "Toxoplasma-SAG1"
    :accession "P13664"
    :why "T. gondii surface antigen 1. The mind-altering parasite. Rewires fear into attraction."}

   {:name "Candida-Als3"
    :accession "O74623"
    :why "C. albicans agglutinin-like protein 3. Commensal→pathogen switch. The opportunist."}

   ;; ── Beneficial end ──
   {:name "Lactobacillus-SlpA"
    :accession "P35829"
    :why "L. acidophilus S-layer protein A. Probiotic armor. The gut's handshake."}

   {:name "Tardigrade-Dsup"
    :accession "P0DOW4"
    :why "R. varieornatus damage suppressor. Shields DNA from radiation. Survives everything."}

   {:name "Human-Insulin"
    :accession "P01308"
    :why "Human insulin precursor. 110 residues. The healing protein. Regulates every cell."}

   {:name "Human-Oxytocin"
    :accession "P01178"
    :why "Oxytocin-neurophysin 1 precursor. 121 residues. Bonding, trust, birth. The love hormone."}])

;; ══════════════════════════════════════════════════════
;; SUMMARY
;; ══════════════════════════════════════════════════════

(defn summarize [results]
  (println "\n\n════════════════════════════════════════════════════════")
  (println "  THE SERPENT GRADIENT — CROSS-ORGANISM SUMMARY")
  (println "════════════════════════════════════════════════════════")

  (println "\n  ── Size & GV ──")
  (doseq [r results]
    (println (format "  %-25s %4d residues  GV=%,d"
                     (:name r) (:length r) (:gv r))))

  ;; Serpent census
  (println "\n  ── Serpent Census ──")
  (doseq [r results]
    (let [hebrew (:hebrew r)
          serpent-target #{\נ \ח \ש}
          hits (for [i (range (- (count hebrew) 2))
                     :let [w (subs hebrew i (+ i 3))]
                     :when (= (set w) serpent-target)]
                 {:exact? (= w "נחש")})]
      (println (format "  %-25s serpent: %d exact, %d anagram, %d total"
                       (:name r)
                       (count (filter :exact? hits))
                       (count (remove :exact? hits))
                       (count hits)))))

  ;; Lamb census
  (println "\n  ── Lamb Census ──")
  (doseq [r results]
    (let [hebrew (:hebrew r)
          lamb-target #{\כ \ב \ש}
          hits (for [i (range (- (count hebrew) 2))
                     :let [w (subs hebrew i (+ i 3))]
                     :when (= (set w) lamb-target)]
                 {:exact? (= w "כבש")})]
      (println (format "  %-25s lamb: %d exact, %d anagram (שכב), %d total"
                       (:name r)
                       (count (filter :exact? hits))
                       (count (remove :exact? hits))
                       (count hits)))))

  ;; Key word presence across all
  (println "\n  ── Key Words Across Organisms ──")
  (let [search-words ["חוה" "אדם" "רפא" "חיה" "מות" "קדש" "אהב" "אור" "רחם" "גאל" "צלם"]]
    (doseq [tw search-words]
      (print (format "  %-6s" tw))
      (doseq [r results]
        (let [hebrew (:hebrew r)
              cnt (count (for [i (range (- (count hebrew) (dec (count tw))))
                               :when (= (subs hebrew i (+ i (count tw))) tw)]
                           i))]
          (print (format " %3d" cnt))))
      (println)))
  (println (str "  " (str/join "" (repeat 8 "  ──────"))))
  (println (str "  name   " (str/join " " (map #(format "%3s" (subs (:name %) 0 3)) results))))

  (println "\n  ── Top 3-letter Word #1 per Organism ──")
  (doseq [r results]
    (let [top (first (:top-3 r))]
      (println (format "  %-25s %s (%s) ×%d"
                       (:name r) (:word top) (or (:meaning top) "?") (:count top)))))

  results)

;; ══════════════════════════════════════════════════════
;; RUN
;; ══════════════════════════════════════════════════════

(defn run-all []
  (println "════════════════════════════════════════════════════════")
  (println "  EXPERIMENT 128: THE SERPENT GRADIENT")
  (println "  Prion → Malaria → Toxoplasma → Candida →")
  (println "  Lactobacillus → Tardigrade → Insulin → Oxytocin")
  (println "  Does the gradient extend?")
  (println "════════════════════════════════════════════════════════")

  (let [results (mapv run-protein gradient-proteins)]
    (summarize results)))

(comment
  (run-all)

  ;; Quick single check
  (run-protein (nth gradient-proteins 0)) ;; prion
  (run-protein (nth gradient-proteins 5)) ;; tardigrade

  nil)
