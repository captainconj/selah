(ns experiments.106-has-woman-been-bit
  "Experiment 106: Has Woman Been Bit by the Serpent?

   Genesis 3:15 — 'I will put enmity between thee and the woman,
   and between thy seed and her seed.'

   We ask the oracle: what does it see in Eve, woman, womb, mother?
   We check the proteins: is the serpent in her biology?
   We compare: serpent (358) vs messiah (358) — same number, what ratio?

   RESULTS:
   - Eve (חוה) is a FIXED POINT. God does NOT see her. GV=19.
   - Woman (אשה) = the fire (האש). Aaron sees fire. God sees woman. 216 readings = 6³.
   - Womb (רחם) basin → חרם (ban/destruction). God does NOT see the womb.
   - Mother (אם) GV=41 (13th prime), fixed point, all four see her equally (2 each).
   - Serpent (נחש) GV=358: only cherubim see it. 12 illuminations.
   - Messiah (משיח) GV=358: God and Left see it. 132 illuminations. 11× brighter.
   - Pregnant (הרה) basin → ההר (the mountain). All four see it.
   - Enmity (איבה) GV=18=life, basin → אביה (my father is YHWH). 594 illum.
   - Seed (זרע) basin → עזר (helper). The seed becomes the help of Gen 2:18.
   - Bruise (שוף) basin → NULL. No fixed point. The only singularity. The cross.
   - Head (ראש) basin → אשר (blessed). The crushed head becomes blessing.
   - In the proteins: Calmodulin carries both Eve AND THE serpent.
     BRCA1 (the woman's guardian) carries the serpent ×2.
     Estrogen receptor: serpent anagram at 91, Shaddai ×6, child ×4.
     hCG (pregnancy hormone): NO serpent. Says 'be fruitful' ×3. Clean.
   - The bite is molecular. The name is irreducible. The cure is 11× brighter."
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

;; ── Part 1: The Words ────────────────────────────────────

(def woman-words
  [{:hebrew "חוה"  :english "Eve (Chavah)"}
   {:hebrew "אשה"  :english "woman/wife"}
   {:hebrew "אם"   :english "mother"}
   {:hebrew "רחם"  :english "womb/mercy"}
   {:hebrew "הרה"  :english "pregnant/conceive"}
   {:hebrew "חיה"  :english "living (Eve's other name)"}
   {:hebrew "בת"   :english "daughter"}
   {:hebrew "שרה"  :english "Sarah"}
   {:hebrew "חנה"  :english "Hannah"}])

(def serpent-words
  [{:hebrew "נחש"  :english "serpent"}
   {:hebrew "משיח" :english "messiah (GV twin)"}
   {:hebrew "ארר"  :english "curse"}
   {:hebrew "זרע"  :english "seed"}
   {:hebrew "איבה" :english "enmity"}
   {:hebrew "עקב"  :english "heel/Jacob"}
   {:hebrew "שוף"  :english "bruise/crush"}])

(defn run-words []
  (println "╔═══════════════════════════════════════════════╗")
  (println "║  PART 1: THE WORDS — Woman and Serpent       ║")
  (println "╚═══════════════════════════════════════════════╝")

  (println "\n── The Woman ──")
  (let [w-results (mapv #(query-word (:hebrew %) (:english %)) woman-words)]

    (println "\n── The Serpent ──")
    (let [s-results (mapv #(query-word (:hebrew %) (:english %)) serpent-words)]

      (println "\n── The Ratio ──")
      (let [serpent (first (filter #(= "נחש" (:hebrew %)) s-results))
            messiah (first (filter #(= "משיח" (:hebrew %)) s-results))]
        (println (format "  Serpent: %d illuminations, %d readings"
                         (:illuminations serpent) (:readings serpent)))
        (println (format "  Messiah: %d illuminations, %d readings"
                         (:illuminations messiah) (:readings messiah)))
        (println (format "  Ratio:   %dx brighter"
                         (/ (:illuminations messiah) (:illuminations serpent)))))

      {:woman w-results :serpent s-results})))

;; ── Part 2: The Proteins ─────────────────────────────────

(def woman-proteins
  [{:name "Calmodulin"
    :accession "P0DP23"
    :why "Calcium messenger. Contains Eve AND THE serpent."}
   {:name "BRCA1"
    :accession "P38398"
    :why "Breast cancer guardian. When she fails, cancer follows. Serpent ×2."}
   {:name "Estrogen receptor alpha"
    :accession "P03372"
    :why "The receptor that makes female development happen."}
   {:name "hCG beta"
    :accession "P0DN86"
    :why "Human chorionic gonadotropin. The pregnancy signal."}])

(defn search-serpent
  "Search a Hebrew protein string for נחש or anagrams."
  [hebrew name]
  (let [target #{\נ \ח \ש}
        windows (for [i (range (- (count hebrew) 2))]
                  {:pos i
                   :letters (subs hebrew i (+ i 3))
                   :chars (set (seq (subs hebrew i (+ i 3))))})]
    (let [exact (filter #(= (:letters %) "נחש") windows)
          anagram (filter #(and (= (:chars %) target)
                                (not= (:letters %) "נחש")) windows)]
      (when (or (seq exact) (seq anagram))
        (println (format "  %s: serpent found!" name))
        (doseq [w exact]
          (println (format "    [%3d] %s — exact נחש" (:pos w) (:letters w))))
        (doseq [w anagram]
          (println (format "    [%3d] %s — anagram" (:pos w) (:letters w)))))
      (when (and (empty? exact) (empty? anagram))
        (println (format "  %s: no serpent" name)))
      {:exact (count exact) :anagram (count anagram)})))

(defn search-eve
  "Search a Hebrew protein string for חוה."
  [hebrew name]
  (let [windows (for [i (range (- (count hebrew) 2))]
                  {:pos i :letters (subs hebrew i (+ i 3))})]
    (let [hits (filter #(= (:letters %) "חוה") windows)]
      (when (seq hits)
        (println (format "  %s: Eve (חוה) found at %s"
                         name (str/join ", " (map :pos hits)))))
      (when (empty? hits)
        (println (format "  %s: no Eve" name)))
      (count hits))))

(defn run-protein [{:keys [name accession why]}]
  (println (format "\n── %s (%s) ──" name accession))
  (println (format "  %s" why))
  (let [{:keys [hebrew length]} (let [r (dna/fetch-uniprot accession)]
                                  {:hebrew (dna/protein-str->hebrew (:sequence r))
                                   :length (count (:sequence r))})
        gv (g/word-value hebrew)
        hits (dna/slide hebrew {:window 3 :vocab :torah})
        top-words (dna/word-frequencies hits)]
    (println (format "  %d residues. GV=%d. %d readings."
                     length gv (count hits)))

    (println "\n  Serpent search:")
    (let [serpent-result (search-serpent hebrew name)]

      (println "\n  Eve search:")
      (let [eve-count (search-eve hebrew name)]

        (println "\n  Top words:")
        (doseq [{:keys [word meaning count]} (take 10 top-words)]
          (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))

        {:name name :hebrew hebrew :gv gv
         :readings (count hits) :top-words top-words
         :serpent serpent-result :eve-count eve-count}))))

(defn run-proteins []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  PART 2: THE PROTEINS — The Bite             ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv run-protein woman-proteins))

;; ── Part 3: Genesis 3:15 ────────────────────────────────

(defn run-genesis-315 []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  PART 3: GENESIS 3:15 — The Enmity           ║")
  (println "╚═══════════════════════════════════════════════╝")
  (println "\n  'I will put enmity between thee and the woman,")
  (println "   and between thy seed and her seed;")
  (println "   he shall bruise thy head,")
  (println "   and thou shalt bruise his heel.'")

  (println "\n── Key terms ──")
  (query-word "איבה" "enmity")
  (query-word "זרע" "seed")
  (query-word "עקב" "heel/Jacob")
  (query-word "שוף" "bruise/crush")
  (query-word "ראש" "head"))

;; ── Run all ──────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 106: HAS WOMAN BEEN BIT?")
  (println "  Genesis 3:15. The enmity. The seed. The bite.")
  (println "════════════════════════════════════════════════")
  (let [words (run-words)
        proteins (run-proteins)
        genesis (run-genesis-315)]
    {:words words :proteins proteins :genesis genesis}))

(comment
  (run-all)
  (run-words)
  (run-proteins)
  (run-genesis-315)

  ;; Individual queries
  (query-word "חוה" "Eve")
  (query-word "אשה" "woman")
  (query-word "נחש" "serpent")
  (query-word "משיח" "messiah")

  ;; Check a specific protein
  (run-protein {:name "BRCA1" :accession "P38398" :why "The woman's guardian"})
  )
