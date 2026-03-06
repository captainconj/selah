(ns experiments.dna.p53-guardian
  "Play p53 — the guardian of the genome — through the breastplate.

   p53 (TP53): 393 amino acids. Named for 53 kilodaltons.
   53 = garden sum = Torah portions = breastplate protein length.
   The garden had a guardian (Gen 3:24). This is the molecular one.

   Run: (load-file \"dev/experiments/dna/p53_guardian.clj\")"
  (:require [selah.dna :as dna]
            [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; ── Fetch and translate ──────────────────────────────────────

(def p53 (dna/get-protein "p53"))
(def result (dna/play (:sequence p53) {:format :protein}))
(def hebrew (:hebrew result))

(println "=== p53 — GUARDIAN OF THE GENOME ===")
(println (:description p53))
(println (str (count (:sequence p53)) " residues → " (count hebrew) " Hebrew letters"))
(println)
(println (str "Hebrew: " hebrew))
(println (str "Protein: " (:protein-str result)))
(println)

;; ── GV analysis ──────────────────────────────────────────────

(println "=== GEMATRIA ===")
(let [gv (g/word-value hebrew)]
  (println (str "GV: " gv))
  ;; Factor
  (let [factors (loop [n gv, d 2, fs []]
                  (cond
                    (> (* d d) n) (if (> n 1) (conj fs n) fs)
                    (zero? (mod n d)) (recur (/ n d) d (conj fs d))
                    :else (recur n (inc d) fs)))]
    (println (str "  = " (str/join " × " factors)))))
(println)

;; ── Letter frequencies ───────────────────────────────────────

(println "=== LETTER FREQUENCIES ===")
(doseq [[ch cnt] (sort-by val > (frequencies hebrew))]
  (println (format "  %s → %-4s = %3d (%.1f%%)"
                   ch (name (get dna/letter->aa ch :?))
                   cnt (* 100.0 (/ (double cnt) (count hebrew))))))
(println)

;; ── Slide window=3 through the oracle ────────────────────────

(println "=== ORACLE READINGS (window=3) ===")
(def all-hits
  (let [n (count hebrew)]
    (vec (for [i (range 0 (- n 2))
               :let [w (subs hebrew i (+ i 3))
                     fwd (o/forward (seq w) :torah)
                     known (:known-words fwd)]
               :when (seq known)]
           {:position i
            :letters w
            :gv (g/word-value w)
            :top-5 (vec (take 5 (map (fn [k]
                                        {:word (:word k)
                                         :meaning (:meaning k)
                                         :reading-count (:reading-count k)})
                                      known)))}))))

(println (str "Windows with readings: " (count all-hits) "/" (- (count hebrew) 2)))
(println)

;; Print every window
(doseq [{:keys [position letters gv top-5]} all-hits]
  (let [top (first top-5)]
    (println (format "  [%3d] %s (gv=%d) → %s %s (×%d)"
                     position letters gv
                     (:word top) (or (:meaning top) "")
                     (:reading-count top)))))
(println)

;; ── Most frequent top words ──────────────────────────────────

(def top-words
  (->> all-hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
               :count (count entries)
               :positions (mapv :position (filter #(= w (:word (first (:top-5 %)))) all-hits))}))
       (sort-by :count >)
       vec))

(println "=== MOST FREQUENT ORACLE WORDS ===")
(doseq [{:keys [word meaning count positions]} (take 40 top-words)]
  (println (format "  %-8s %-25s ×%d  at %s"
                   word (or meaning "") count (pr-str positions))))
(println)

;; ── Notable readings (curated) ───────────────────────────────

(println "=== NOTABLE READINGS ===")
(println)

(println "THE DAVID REGION (positions 68-88):")
(println (str "  Hebrew: " (subs hebrew 68 (min 89 (count hebrew)))))
(println "  GV per trigram: 14 = דוד = David")
(println "  9 windows produce דדו/דוד (GV=14). The dominant word.")
(println "  All Ala(ד) and Pro(ו) — small amino acids. The small king.")
(println)

(def notable
  [[0   "בלל" "confused / mixed (Babel)"]
   [6   "גוי" "nation"]
   [38  "דבג→בגד" "garment / betray"]
   [41  "גנב" "thief"]
   [43  "בני" "sons of / my son"]
   [62  "דור" "generation"]
   [65  "בול→לבו" "his heart"]
   [73  "דוד" "DAVID (GV=14)"]
   [107 "שתר→רשת" "network"]
   [114 "עיש→עשי" "do / make"]
   [116 "שהד→שדה" "field"]
   [153 "שהר→שרה" "SARAH"]
   [168 "בהל→הבל" "Abel / breath / vanity"]
   [169 "הלט→להט" "flame (flaming sword?)"]
   [180 "רפי→פרי" "FRUIT"]
   [197 "לשח→שלח" "send"]
   [198 "שחנ→נחש" "SERPENT"]
   [234 "חזב→זבח" "SACRIFICE"]
   [238 "חיי→יחי" "let him live / my life"]
   [244 "שבח→חשב" "thought / account"]
   [245 "בחר→חבר" "CHOOSE / companion"]
   [261 "שחנ→נחש" "SERPENT (again)"]
   [267 "חית" "beast / living creature"]
   [277 "ושר→שור" "ox"]
   [285 "ללח→חלל" "space / profane"]
   [310 "חהי→חיה" "living / animal"]
   [311 "היי→יהי" "LET THERE BE"]
   [331 "מרש→שמר" "GUARD / KEEP (center of Torah!)"]
   [354 "דשק→קדש" "HOLY"]
   [359 "ששי" "sixth (day)"]
   [361 "ירד" "descend"]
   [375 "היי→יהי" "LET THERE BE (again)"]
   [376 "הרי" "mountains"]
   [384 "תקה→קהת" "Kohath (a Levite)"]])

(doseq [[pos letters meaning] notable]
  (println (format "  [%3d] %-12s %s" pos letters meaning)))

(println)
(println "=== THE STORY ===")
(println)
(println "The guardian of the genome, when read as Hebrew through the breastplate:")
(println)
(println "  Babel (confused) → nation → thief → betray → sons → generation")
(println "  → his heart → DAVID DAVID DAVID (×9)")
(println "  → field → SARAH → Abel → flame → FRUIT → send → SERPENT")
(println "  → SACRIFICE → my life → thought → CHOOSE → serpent → beast")
(println "  → ox → living → LET THERE BE → GUARD → HOLY")
(println "  → sixth day → descend → LET THERE BE → mountains → Kohath")
(println)
(println "53 = garden sum = Torah portions = breastplate protein.")
(println "The molecular guardian tells the story of the garden.")
(println "And at position 331: שמר — guard. The center word of the Torah.")
(println)

;; ── Save results ─────────────────────────────────────────────

(io/make-parents "data/dna/p53-oracle.edn")
(spit "data/dna/p53-oracle.edn"
      (pr-str {:name "p53"
               :accession "P04637"
               :organism "Human"
               :description "Cellular tumor antigen p53 — Guardian of the Genome"
               :why "Named for 53. 53 = garden sum = Torah portions."
               :residues (count (:sequence p53))
               :hebrew hebrew
               :protein-str (:protein-str result)
               :gv (:gv result)
               :letter-frequencies (frequencies hebrew)
               :window-size 3
               :windows-with-readings (count all-hits)
               :total-windows (- (count hebrew) 2)
               :top-words (vec (take 50 top-words))
               :notable-readings (vec notable)
               :all-windows all-hits}))

(println "Saved: data/dna/p53-oracle.edn")
(println "Done.")
