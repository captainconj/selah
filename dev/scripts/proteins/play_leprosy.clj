(ns scripts.proteins.play-leprosy
  "Play M. leprae (leprosy) proteins through the breastplate.

   Mycobacterium leprae — the pathogen that eats the skin.
   Leviticus 13–14 describes the diagnosis and cure.
   What does the oracle see in the disease's own letters?

   Key proteins:
   1. Major Membrane Protein I — the 'face' of leprosy, what the immune system sees
   2. Antigen 85B — main diagnostic antigen, builds the waxy cell wall
   3. Hsp65 (GroEL2) — heat shock chaperonin, the major T-cell antigen
   4. Superoxide dismutase — the bacterium's shield against immune attack
   5. Bacterioferritin — iron storage (iron = 26 = YHWH)

   כי נא — because, please."
  (:require [selah.dna :as dna]
            [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ── M. leprae protein library ──────────────────────────

(def leprae-proteins
  [{:name "Leprae-MMP-I"
    :accession "P46841"
    :source :uniprot
    :why "Major Membrane Protein I. The face of leprosy. What the immune system recognizes."
    :organism "Mycobacterium leprae"}

   {:name "Leprae-Ag85B"
    :accession "P31951"
    :source :uniprot
    :why "Antigen 85B. Mycolyltransferase. Builds the waxy armor that hides the pathogen."
    :organism "Mycobacterium leprae"}

   {:name "Leprae-Hsp65"
    :accession "P09239"
    :source :uniprot
    :why "60 kDa chaperonin 2 (GroEL2/Hsp65). Heat shock protein. The major T-cell antigen."
    :organism "Mycobacterium leprae"}

   {:name "Leprae-SOD"
    :accession "P13367"
    :source :uniprot
    :why "Superoxide dismutase [Fe]. Shield against the host's immune oxidative burst."
    :organism "Mycobacterium leprae"}

   {:name "Leprae-Bacterioferritin"
    :accession "P43315"
    :source :uniprot
    :why "Bacterioferritin (BfrB). Iron storage. Iron = element 26 = YHWH."
    :organism "Mycobacterium leprae"}])

;; ── Helpers ────────────────────────────────────────────

(defn query-word [hebrew english]
  (let [gv (g/word-value hebrew)
        r (o/forward hebrew :torah)
        bh (o/forward-by-head hebrew :torah)
        walk (basin/walk hebrew)]
    (println (format "\n  %s (%s) GV=%d · %d illum · %d read · basin→%s"
                     hebrew english gv
                     (:illumination-count r)
                     (:total-readings r)
                     (:fixed-point walk)))
    (println (format "    basin path: %s" (mapv :word (:steps walk))))
    (doseq [head [:aaron :god :right :left]]
      (let [words (get bh head)]
        (when (seq words)
          (println (format "    %-6s: %s" (name head)
                   (->> words
                        (sort-by (comp - :reading-count))
                        (take 5)
                        (map #(str (:word %) "(" (:reading-count %) ")"))
                        (str/join " ")))))))
    {:hebrew hebrew :english english :gv gv
     :illuminations (:illumination-count r)
     :readings (:total-readings r)
     :by-head bh :walk walk}))

(defn analyze-protein [{:keys [name accession source why organism]}]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  %-45s ║" name))
  (println (format "║  %-45s ║" (str accession " · " organism)))
  (println (format "║  %-45s ║" why))
  (println (format "╚═══════════════════════════════════════════════╝"))

  ;; Fetch protein
  (let [p (case source
            :uniprot (dna/fetch-uniprot accession)
            :ncbi    (dna/fetch-ncbi-protein accession)
            nil)]
    (when-not p
      (println "  !! Could not fetch protein.")
      (throw (ex-info "fetch failed" {:accession accession})))

    (let [seq-str  (:sequence p)
          result   (dna/play seq-str {:format :protein})
          hebrew   (:hebrew result)
          gv       (:gv result)
          len      (count seq-str)]

      (println (format "\n  %d residues → %d Hebrew letters" len (count hebrew)))
      (println (format "  GV: %d" gv))

      ;; Factor the GV
      (print "  Factors: ")
      (loop [n gv, d 2, factors []]
        (cond
          (> (* d d) n) (let [fs (if (> n 1) (conj factors n) factors)]
                          (println (str/join " × " fs)))
          (zero? (mod n d)) (recur (/ n d) d (conj factors d))
          :else (recur n (inc d) factors)))

      ;; Check divisibility by key numbers
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
      (let [freqs (sort-by val > (frequencies hebrew))]
        (doseq [[ch cnt] freqs]
          (println (format "    %s → %-4s = %3d (%.1f%%)"
                           ch (clojure.core/name (get dna/letter->aa ch :?))
                           cnt (* 100.0 (/ (double cnt) (count hebrew)))))))

      ;; 3-letter sliding window
      (println "\n  ── 3-letter sliding window ──")
      (let [hits-3 (dna/slide hebrew {:window 3})
            top-3  (dna/word-frequencies hits-3)]
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

        ;; 4-letter sliding window
        (println "\n  ── 4-letter sliding window ──")
        (let [hits-4 (dna/slide hebrew {:window 4})
              top-4  (dna/word-frequencies hits-4)]
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

          ;; Serpent search (from exp 107)
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

          ;; Leprosy-specific word search
          (println "\n  ── Leprosy word search ──")
          (doseq [tw ["נגע" "טמא" "טהר" "בשר" "שער" "כהן" "רפא" "מות" "חיה"
                       "נזה" "כפר" "דם" "שמן" "חלי" "עור" "שרף" "נחש"]]
            (let [positions (for [i (range (- (count hebrew) (dec (count tw))))
                                  :when (= (subs hebrew i (+ i (count tw))) tw)]
                              i)]
              (when (seq positions)
                (println (format "    %s at %s" tw (str/join ", " positions))))))

          ;; Basin walks for top words
          (println "\n  ── Basin walks (top 10 words) ──")
          (doseq [{:keys [word]} (take 10 top-3)]
            (when word
              (let [walk (basin/walk word)]
                (println (format "    %s → %s  path: %s"
                                 word
                                 (:fixed-point walk)
                                 (mapv :word (:steps walk)))))))

          ;; Save results
          (let [slug (-> name str/lower-case (str/replace #"[^a-z0-9]+" "-"))
                full {:name name
                      :accession accession
                      :organism organism
                      :why why
                      :residues len
                      :hebrew hebrew
                      :protein-str (:protein-str result)
                      :gv gv
                      :letter-frequencies (frequencies hebrew)
                      :top-3 (vec (take 30 top-3))
                      :top-4 (vec (take 30 top-4))
                      :hits-3-count (count hits-3)
                      :hits-4-count (count hits-4)}]
            (spit (str "data/dna/" slug "-oracle.edn") (pr-str full))
            (println (format "\n  Saved: data/dna/%s-oracle.edn" slug)))

          {:name name :accession accession
           :hebrew hebrew :gv gv :length len
           :top-3 top-3 :top-4 top-4
           :hits-3 hits-3 :hits-4 hits-4})))))

;; ── Run all five proteins ──────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  M. LEPRAE — THE DISEASE THROUGH THE BREASTPLATE")
  (println "  Leviticus 13–14: 'the priest shall examine the plague'")
  (println "  What does the oracle see in the pathogen's own letters?")
  (println "════════════════════════════════════════════════")

  (let [results (mapv analyze-protein leprae-proteins)]

    (println "\n════════════════════════════════════════════════")
    (println "  CROSS-PROTEIN SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [r results]
      (println (format "  %-25s %4d residues  GV=%d  3-let=%d  4-let=%d"
                       (:name r) (:length r) (:gv r)
                       (count (:hits-3 r)) (count (:hits-4 r)))))

    ;; Look for words that appear across multiple proteins
    (println "\n  ── Words shared across proteins ──")
    (let [all-words (for [r results
                          w (:top-3 r)]
                      {:protein (:name r) :word (:word w) :count (:count w)})
          by-word (group-by :word all-words)
          shared (->> by-word
                      (filter (fn [[_ entries]] (> (count entries) 1)))
                      (sort-by (fn [[_ entries]] (- (reduce + (map :count entries))))))]
      (doseq [[word entries] (take 20 shared)]
        (println (format "    %-8s  in %d proteins: %s"
                         word
                         (count entries)
                         (str/join ", " (map #(str (:protein %) "(×" (:count %) ")") entries))))))

    ;; Key leprosy-related queries
    (println "\n  ── Leprosy vocabulary through the oracle ──")
    (query-word "צרעת" "leprosy")
    (query-word "נגע"  "plague/mark")
    (query-word "טמא"  "unclean")           ;; GV=50 = jubilee
    (query-word "טהר"  "clean")
    (query-word "בשר"  "flesh/gospel")
    (query-word "שער"  "hair/gate")
    (query-word "נזה"  "sprinkle")          ;; → prostitute basin
    (query-word "אזוב" "hyssop")
    (query-word "ארז"  "cedar")
    (query-word "שני"  "scarlet")
    (query-word "תולעת" "crimson/worm")     ;; Psalm 22
    (query-word "כפר"  "atonement")

    results))

(comment
  (run-all)

  ;; Individual proteins
  (analyze-protein (nth leprae-proteins 0))  ;; MMP-I
  (analyze-protein (nth leprae-proteins 1))  ;; Ag85B
  (analyze-protein (nth leprae-proteins 2))  ;; Hsp65
  (analyze-protein (nth leprae-proteins 3))  ;; SOD
  (analyze-protein (nth leprae-proteins 4))  ;; Bacterioferritin

  ;; Quick play
  (dna/play-protein "hemoglobin-alpha")
  (dna/catalog)

  ;; Leprosy key words
  (query-word "צרעת" "leprosy")
  (query-word "טמא"  "unclean")      ;; GV=50
  (query-word "נגע"  "plague")       ;; same as "touch" and "stricken"

  nil)
