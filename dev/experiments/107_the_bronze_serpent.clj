(ns experiments.107-the-bronze-serpent
  "Experiment 107: The Bronze Serpent — Viruses and Vaccines.

   Numbers 21:8-9 — 'Make a fiery serpent and set it on a pole.
   Everyone who is bitten, when he looks at it, shall live.'

   The vaccine IS the bronze serpent. Take a piece of the thing
   that kills, lift it up, let the body see it, and live.

   RESULTS — Words:
   - דבר (word = pestilence) GV=206. Basin → ברד (HAIL). The word IS the plague.
   - מגפה (epidemic) basin → NULL. Like the bruise (שוף). No fixed point.
   - רפא (heal) basin → NULL. Like the bruise. Healing IS the event.
   - בלם (restrain) GV=72 = breastplate count! 72 readings! Basin → לבם (their heart).
   - טמא (unclean) GV=50 = JUBILEE axis. Fixed point.
   - שרף (seraph/fiery serpent) basin → רשף (flame/pestilence). God does NOT see it.
   - נחשת (bronze) fixed point. Only mercy sees it (1 reading).
   - חיל (strength) = 132 readings = messiah's count.

   RESULTS — Proteins:
   - SARS-CoV-2 spike: תחש (tachash/covering) ×7, חיה/חית (beast) ×12, מות (death) at 895.
   - Influenza: serpent ×2, Shaddai at 156, 'dead' ×3.
   - HIV gp160: EXACT serpent at 260, staff (מטה) ×4, beast ×6, 'Mary' ×5.
   - Vaccinia D8 (THE FIRST VACCINE, from the ox): NO SERPENT. Clean.
   - IgG1 antibody (what your body makes): EXACT SERPENT at position 196.

   The vaccine is clean. The antibody contains the serpent.
   Numbers 21. Look at the serpent and live."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dna :as dna]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────

(defn query-word
  "Run a word through the oracle. Print results. Return data."
  [hebrew english]
  (let [gv (g/word-value hebrew)
        r (o/forward hebrew {:vocab :torah})
        bh (o/forward-by-head hebrew {:vocab :torah})
        walk (basin/walk hebrew)]
    (println (format "\n%s (%s) GV=%d · %d illum · %d read · basin→%s"
                     hebrew english gv
                     (:illumination-count r)
                     (:total-readings r)
                     (:fixed-point walk)))
    (println (format "  basin path: %s" (mapv :word (:path walk))))
    (doseq [head [:aaron :god :right :left]]
      (let [words (get bh head)]
        (when (seq words)
          (println (format "  %-6s: %s" (name head)
                   (->> words
                        (sort-by (comp - :reading-count))
                        (take 5)
                        (map #(str (:word %) "(" (:reading-count %) ")"))
                        (str/join " ")))))))
    {:hebrew hebrew :english english :gv gv
     :illuminations (:illumination-count r)
     :readings (:total-readings r)
     :by-head bh :walk walk}))

(defn run-protein
  "Run a protein through the breastplate. Search for serpent, key words."
  [{:keys [name accession why]}]
  (println (format "\n══ %s (%s) ══" name accession))
  (println (format "  %s" why))
  (let [r (dna/fetch-uniprot accession)
        hebrew (dna/protein-str->hebrew (:sequence r))
        gv (g/word-value hebrew)
        hits (dna/slide hebrew {:window 3 :vocab :torah})
        top (dna/word-frequencies hits)
        target #{\נ \ח \ש}]
    (println (format "  %d residues. GV=%d. %d readings."
                     (count (:sequence r)) gv (count hits)))

    ;; Serpent search
    (println "\n  Serpent search:")
    (let [serpent-hits (for [i (range (- (count hebrew) 2))
                            :let [w (subs hebrew i (+ i 3))]
                            :when (= (set w) target)]
                        {:pos i :letters w :exact? (= w "נחש")})]
      (if (seq serpent-hits)
        (doseq [{:keys [pos letters exact?]} serpent-hits]
          (println (format "    [%3d] %s %s" pos letters
                           (if exact? "— EXACT" "— anagram"))))
        (println "    No serpent."))

      ;; Key words
      (println "\n  Key word positions:")
      (doseq [tw ["שדי" "קדש" "הוה" "עקד" "זרק" "מות" "נגע" "חיה" "מטה"
                   "רפא" "נחש" "שרף" "תלד" "שני" "דבר" "נגף" "תחש"]]
        (let [positions (for [i (range (- (count hebrew) 2))
                              :when (= (subs hebrew i (+ i 3)) tw)]
                          i)]
          (when (seq positions)
            (println (format "    %s at %s" tw (str/join ", " positions))))))

      (println "\n  Top 15 words:")
      (doseq [{:keys [word meaning count]} (take 15 top)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))

      {:name name :accession accession :hebrew hebrew :gv gv
       :readings (count hits) :top-words top
       :serpent-count (count serpent-hits)
       :serpent-exact (count (filter :exact? serpent-hits))})))

;; ── Part 1: Disease and Healing Words ────────────────────

(def disease-words
  [{:hebrew "נגף"   :english "plague/strike"}
   {:hebrew "מגפה"  :english "epidemic/plague"}
   {:hebrew "דבר"   :english "pestilence/word"}
   {:hebrew "נגע"   :english "touch/affliction"}
   {:hebrew "חלה"   :english "sick/ill"}
   {:hebrew "מות"   :english "death"}
   {:hebrew "טמא"   :english "unclean"}
   {:hebrew "ארס"   :english "venom/poison"}])

(def healing-words
  [{:hebrew "רפא"   :english "heal/physician"}
   {:hebrew "טהר"   :english "clean/purify"}
   {:hebrew "חסן"   :english "strong/store up"}
   {:hebrew "זרק"   :english "sprinkle/inject"}
   {:hebrew "סם"    :english "drug/medicine"}
   {:hebrew "בלם"   :english "restrain/bridle"}
   {:hebrew "מגן"   :english "shield/protect"}
   {:hebrew "חיל"   :english "strength/birth-pang"}])

(def bronze-serpent-words
  [{:hebrew "נס"    :english "pole/banner/miracle"}
   {:hebrew "נחשת"  :english "bronze/copper"}
   {:hebrew "שרף"   :english "seraph/fiery serpent"}])

(defn run-words []
  (println "╔═══════════════════════════════════════════════╗")
  (println "║  PART 1: DISEASE AND HEALING WORDS           ║")
  (println "╚═══════════════════════════════════════════════╝")

  (println "\n── Disease ──")
  (let [d (mapv #(query-word (:hebrew %) (:english %)) disease-words)]
    (println "\n── Healing ──")
    (let [h (mapv #(query-word (:hebrew %) (:english %)) healing-words)]
      (println "\n── The Bronze Serpent (Numbers 21) ──")
      (let [b (mapv #(query-word (:hebrew %) (:english %)) bronze-serpent-words)]
        {:disease d :healing h :bronze-serpent b}))))

;; ── Part 2: Viral Proteins ───────────────────────────────

(def virus-proteins
  [{:name "SARS-CoV-2 Spike"
    :accession "P0DTC2"
    :why "The COVID-19 spike. 1273 residues. The thing the vaccine teaches your body to see."}
   {:name "Influenza H1 (1918)"
    :accession "Q9WFX3"
    :why "Hemagglutinin from the 1918 pandemic. The deadliest flu in history."}
   {:name "HIV-1 gp160"
    :accession "P04578"
    :why "HIV envelope glycoprotein. The virus that hides from the immune system."}])

(def vaccine-proteins
  [{:name "Vaccinia D8"
    :accession "P21057"
    :why "Cowpox surface protein. THE FIRST VACCINE. From the ox. Vacca = cow."}
   {:name "IgG1 Fc (antibody)"
    :accession "P01857"
    :why "Human antibody. What the immune system MAKES in response. The bronze serpent."}])

(defn run-viruses []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  PART 2: THE VIRUSES                         ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv run-protein virus-proteins))

(defn run-vaccines []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  PART 3: THE VACCINES AND ANTIBODIES          ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv run-protein vaccine-proteins))

;; ── Run all ──────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 107: THE BRONZE SERPENT")
  (println "  'Make a fiery serpent and set it upon a pole;")
  (println "   everyone who is bitten, when he looks, shall live.'")
  (println "════════════════════════════════════════════════")
  (let [words (run-words)
        viruses (run-viruses)
        vaccines (run-vaccines)]

    (println "\n╔═══════════════════════════════════════════════╗")
    (println "║  SUMMARY                                      ║")
    (println "╚═══════════════════════════════════════════════╝")
    (println "\nViruses:")
    (doseq [v viruses]
      (println (format "  %s: %d readings, serpent=%d (exact=%d)"
                       (:name v) (:readings v)
                       (:serpent-count v) (:serpent-exact v))))
    (println "\nVaccines/Antibodies:")
    (doseq [v vaccines]
      (println (format "  %s: %d readings, serpent=%d (exact=%d)"
                       (:name v) (:readings v)
                       (:serpent-count v) (:serpent-exact v))))

    {:words words :viruses viruses :vaccines vaccines}))

(comment
  (run-all)
  (run-words)
  (run-viruses)
  (run-vaccines)

  ;; Individual
  (run-protein {:name "COVID spike" :accession "P0DTC2" :why "spike"})
  (run-protein {:name "Vaccinia" :accession "P21057" :why "first vaccine"})
  (run-protein {:name "Antibody" :accession "P01857" :why "immune response"})
  )
