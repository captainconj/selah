(ns experiments.123-the-cure-for-leprosy
  "Experiment 123: The Cure for Leprosy — Leviticus 13–14.

   The most elaborate diagnostic and purification system in Torah.
   Two birds — one slaughtered, one released alive.
   Cedar wood, scarlet yarn, hyssop.
   Blood on right ear, right thumb, right big toe.
   Oil on top of blood.

   This is the death-and-resurrection ritual embedded in Levitical law.

   What we already know:
   - Flesh (בשר) = gospel/news. Break (שבר) → flesh (בשר) at 720 readings.
   - Slaughter (שחט) → null. The slaughter arrives empty.
   - Seven (שבע) = 72 illuminations. Stamps the breastplate.
   - Oil (שמן) = stamps 72.
   - Sprinkle (נזה) → prostitute (זנה) basin. The purifier takes on the stain.
   - Hair (שער) = gate! Same word.
   - Clean (טהר) and Unclean (טמא, GV=50=jubilee) — key pair.
   - Hyssop (אזוב) — at the cross, at the Passover, at the red heifer.
   - Right (ימין) — the right side, the cherub side.

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.dna :as dna]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-122) ──────────────────

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

(defn fetch-verse-letters [book chapter v-start v-end]
  (let [verses (sefaria/fetch-chapter book chapter)
        selected (subvec (vec verses) (dec v-start) v-end)
        raw (apply str (map norm/strip-html selected))]
    (apply str (norm/letter-stream raw))))

(defn slide-text [hebrew window]
  (let [n (count hebrew)]
    (vec (for [i (range 0 (- n (dec window)))
               :let [w (subs hebrew i (+ i window))
                     fwd (o/forward (seq w) :torah)
                     known (:known-words fwd)]
               :when (seq known)]
           {:position i
            :letters w
            :gv (g/word-value w)
            :top-5 (vec (take 5 (map (fn [k]
                                        {:word (:word k)
                                         :reading-count (:reading-count k)})
                                      known)))}))))

(defn word-frequencies [hits]
  (->> hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :count (count entries)}))
       (sort-by (comp - :count))
       vec))

(defn walk-station [station-num station-name book chapter v-start v-end key-words]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "║  %s %d:%d-%d %s ║"
                   book chapter v-start v-end
                   (apply str (repeat (- 35 (count (str book " " chapter ":" v-start "-" v-end))) " "))))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [letters (fetch-verse-letters book chapter v-start v-end)
        gv (g/word-value letters)]
    (println (format "\n  %d letters. GV=%d." (count letters) gv))
    (println (format "  Letters: %s" (if (> (count letters) 120)
                                       (str (subs letters 0 120) "...")
                                       letters)))

    (println "\n  ── Key Words ──")
    (let [word-results (mapv (fn [[h e]] (query-word h e)) key-words)]

      (println "\n  ── 3-letter sliding window ──")
      (let [hits-3 (slide-text letters 3)
            top-3 (word-frequencies hits-3)]
        (println (format "  %d/%d windows produced readings."
                         (count hits-3) (- (count letters) 2)))
        (doseq [{:keys [position letters top-5]} hits-3]
          (let [top (first top-5)]
            (println (format "    [%3d] %s → %s"
                             position letters
                             (:word top)))))
        (println "\n  Top 3-letter words:")
        (doseq [{:keys [word count]} (take 15 top-3)]
          (println (format "    %-8s ×%d" word count)))

        (println "\n  ── 4-letter sliding window (cherubim's view) ──")
        (let [hits-4 (slide-text letters 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count letters) 3)))
          (doseq [{:keys [position letters top-5]} hits-4]
            (let [top (first top-5)]
              (println (format "    [%3d] %s → %s"
                               position letters
                               (:word top)))))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word count]} (take 15 top-4)]
            (println (format "    %-8s ×%d" word count)))

          {:station station-num :name station-name
           :letter-count (count letters) :gv gv
           :words word-results
           :hits-3 hits-3 :hits-4 hits-4
           :top-3 top-3 :top-4 top-4})))))

;; ══════════════════════════════════════════════════════
;; LEPROSY VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn leprosy-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  LEPROSY VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The disease
     ["צרעת"   "leprosy"]
     ["נגע"    "plague/mark"]        ;; same word as "stricken" and "touch"!
     ["עור"    "skin"]
     ["בשר"    "flesh"]              ;; = gospel/news
     ["שער"    "hair"]               ;; = gate!
     ["לבן"    "white"]              ;; = Laban
     ["אדם"    "red/man"]

     ;; The examination
     ["כהן"    "priest"]
     ["ראה"    "see/examine"]
     ["טהר"    "clean"]
     ["טמא"    "unclean"]            ;; GV=50 = jubilee!
     ["סגר"    "shut up/quarantine"]
     ["שבע"    "seven"]              ;; 72 illuminations
     ["יום"    "day"]
     ["עמק"    "deep"]               ;; "deeper than the skin"
     ["פשה"    "spread"]

     ;; The cure instruments
     ["ארז"    "cedar"]
     ["שני"    "scarlet"]
     ["תולעת"  "crimson/worm"]       ;; Psalm 22: "I am a worm"
     ["אזוב"   "hyssop"]             ;; at the cross, at the Passover
     ["צפר"    "bird"]
     ["שחט"    "slaughter"]          ;; → null! The slaughter arrives empty.
     ["חי"     "living"]             ;; the living bird released
     ["מים"    "water"]              ;; running water

     ;; The application
     ["דם"     "blood"]
     ["שמן"    "oil"]                ;; stamps 72
     ["אזן"    "ear"]                ;; right ear
     ["בהן"    "thumb/big toe"]
     ["ימין"   "right"]              ;; the right side
     ["נזה"    "sprinkle"]           ;; → prostitute (זנה) basin!

     ;; The offerings
     ["כבש"    "lamb"]               ;; the lamb that splits
     ["אשם"    "guilt offering"]
     ["חטאת"   "sin offering"]
     ["עולה"   "burnt offering"]
     ["תנופה"  "wave offering"]
     ["כפר"    "atonement"]

     ;; The cleansing
     ["רחץ"    "wash"]
     ["גלח"    "shave"]
     ["כבס"    "launder"]]))

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Diagnosis ──────────────────────────
;; Leviticus 13:1-17
;; Swelling, scab, bright spot. White hair. Raw flesh.
;; The priest examines. Pronounces clean or unclean.
;; Seven days quarantine. Examine again.

(defn station-1-diagnosis []
  (walk-station 1 "The Diagnosis" "Leviticus" 13 1 17
    [["צרעת"   "leprosy"]
     ["נגע"    "plague/mark"]
     ["עור"    "skin"]
     ["בשר"    "flesh"]
     ["שאת"    "swelling"]
     ["ספחת"   "scab"]
     ["בהרת"   "bright spot"]
     ["שער"    "hair"]
     ["לבן"    "white"]
     ["הפך"    "turn/change"]
     ["מראה"   "appearance"]
     ["עמק"    "deep"]
     ["כהן"    "priest"]
     ["ראה"    "see"]
     ["טמא"    "unclean"]
     ["טהר"    "clean"]
     ["סגר"    "quarantine"]
     ["שבע"    "seven"]
     ["יום"    "day"]
     ["פשה"    "spread"]
     ["חי"     "living/raw"]
     ["כהה"    "dim/faded"]]))

;; ── Station 2: The Spreading ──────────────────────────
;; Leviticus 13:18-46
;; Boil, burn, head/beard, chronic leprosy.
;; "Deeper than the skin" — pronounce unclean.
;; Torn clothes. Head uncovered. Cry "Unclean, unclean!"
;; Dwell alone outside the camp.

(defn station-2-spreading []
  (walk-station 2 "The Spreading" "Leviticus" 13 18 46
    [["שחין"   "boil"]
     ["מכוה"   "burn"]
     ["ראש"    "head"]
     ["זקן"    "beard"]
     ["נתק"    "scall"]
     ["צהב"    "yellow"]
     ["שער"    "hair"]
     ["שחר"    "black"]
     ["צרעת"   "leprosy"]
     ["נשא"    "carry"]
     ["בגד"    "garment"]           ;; stamps 72
     ["פרם"    "tear"]
     ["פרע"    "uncover"]
     ["שפם"    "lip/mustache"]
     ["עטה"    "cover"]
     ["טמא"    "unclean"]
     ["קרא"    "cry"]
     ["בדד"    "alone"]
     ["מחנה"   "camp"]
     ["ישב"    "dwell"]
     ["חוץ"    "outside"]]))

;; ── Station 3: The Two Birds ──────────────────────────
;; Leviticus 14:1-7
;; The priest goes outside the camp.
;; Two clean living birds, cedar wood, scarlet yarn, hyssop.
;; One bird is slaughtered over running water in an earthen vessel.
;; The living bird is dipped in the blood with the cedar, scarlet, hyssop.
;; Sprinkle seven times on the one being cleansed.
;; Pronounce clean. Release the living bird into the open field.

(defn station-3-two-birds []
  (walk-station 3 "The Two Birds" "Leviticus" 14 1 7
    [["כהן"    "priest"]
     ["יצא"    "go out"]
     ["מחנה"   "camp"]
     ["ראה"    "see/examine"]
     ["צפר"    "bird"]
     ["חי"     "living"]
     ["טהר"    "clean"]
     ["עץ"     "wood"]              ;; = tree, 0/0 dark
     ["ארז"    "cedar"]
     ["שני"    "scarlet"]
     ["תולעת"  "crimson/worm"]
     ["אזוב"   "hyssop"]
     ["שחט"    "slaughter"]
     ["כלי"    "vessel"]
     ["חרש"    "earthen"]
     ["מים"    "water"]
     ["חי"     "living/running"]
     ["דם"     "blood"]
     ["טבל"    "dip"]
     ["נזה"    "sprinkle"]
     ["שבע"    "seven"]
     ["שלח"    "release/send"]
     ["שדה"    "field"]
     ["פנים"   "face/surface"]]))

;; ── Station 4: The Seven Days + Offerings ─────────────
;; Leviticus 14:8-20
;; Shave all hair. Wash. Wait seven days. Shave again.
;; Day 8: two male lambs, one ewe lamb, fine flour, oil.
;; Guilt offering: wave the lamb, slaughter, blood on right ear,
;; right thumb, right big toe. Oil on top of blood.
;; Then sin offering and burnt offering. Atonement.

(defn station-4-offerings []
  (walk-station 4 "The Seven Days + Offerings" "Leviticus" 14 8 20
    [["כבס"    "launder"]
     ["גלח"    "shave"]
     ["שער"    "hair"]
     ["רחץ"    "wash"]
     ["מים"    "water"]
     ["טהר"    "clean"]
     ["שמיני"  "eighth"]
     ["כבש"    "lamb"]              ;; the lamb that splits
     ["כבשה"   "ewe lamb"]
     ["סלת"    "fine flour"]
     ["שמן"    "oil"]               ;; stamps 72
     ["אשם"    "guilt offering"]
     ["תנופה"  "wave offering"]
     ["שחט"    "slaughter"]
     ["דם"     "blood"]
     ["אזן"    "ear"]
     ["בהן"    "thumb"]
     ["רגל"    "foot"]
     ["ימין"   "right"]
     ["יצק"    "pour"]
     ["כף"     "palm"]
     ["נזה"    "sprinkle"]
     ["חטאת"   "sin offering"]
     ["עולה"   "burnt offering"]
     ["כפר"    "atonement"]]))

;; ══════════════════════════════════════════════════════
;; STATION 5: THE DNA — M. leprae proteins through the breastplate
;; ══════════════════════════════════════════════════════
;;
;; Mycobacterium leprae — the pathogen that eats the skin.
;; What does the oracle see in the disease's own letters?
;;
;; Like exp 107 (Bronze Serpent) played viruses and vaccines,
;; here we play the leprosy bacterium's proteins.
;;
;; Five proteins chosen:
;; 1. Major Membrane Protein I — the 'face' of leprosy
;; 2. Antigen 85B — builds the waxy armor
;; 3. Hsp65 (GroEL2) — the major T-cell antigen
;; 4. Superoxide dismutase — shield against immune attack
;; 5. Bacterioferritin — iron storage (iron = 26 = YHWH)

(def leprae-proteins
  [{:name "Leprae-MMP-I"
    :accession "P46841"
    :why "Major Membrane Protein I. The face of leprosy. What the immune system recognizes."}
   {:name "Leprae-Ag85B"
    :accession "P31951"
    :why "Antigen 85B. Mycolyltransferase. Builds the waxy armor that hides the pathogen."}
   {:name "Leprae-Hsp65"
    :accession "P09239"
    :why "60 kDa chaperonin 2 (GroEL2/Hsp65). Heat shock protein. The major T-cell antigen."}
   {:name "Leprae-SOD"
    :accession "P13367"
    :why "Superoxide dismutase [Fe]. Shield against the host's immune oxidative burst."}
   {:name "Leprae-Bacterioferritin"
    :accession "P43315"
    :why "Bacterioferritin (BfrB). Iron storage. Iron = element 26 = YHWH."}])

(defn run-protein
  "Run a protein through the breastplate at 3-letter and 4-letter windows."
  [{:keys [name accession why]}]
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

      ;; Leprosy-specific word search
      (println "\n  ── Leprosy word search ──")
      (doseq [tw ["נגע" "טמא" "טהר" "בשר" "שער" "כהן" "רפא" "מות" "חיה"
                   "נזה" "כפר" "דם" "שמן" "חלי" "עור" "שרף" "נחש" "תחש"
                   "קדש" "הוה" "עקד"]]
        (let [positions (for [i (range (- (count hebrew) (dec (count tw))))
                              :when (= (subs hebrew i (+ i (count tw))) tw)]
                          i)]
          (when (seq positions)
            (println (format "    %s at %s" tw (str/join ", " positions))))))

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

(defn station-5-dna []
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station 5: The DNA                           ║"))
  (println (format "║  M. leprae proteins through the breastplate    ║"))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [results (mapv run-protein leprae-proteins)]

    ;; Cross-protein summary
    (println "\n  ── Cross-Protein Summary ──")
    (doseq [r results]
      (println (format "  %-25s %4d residues  GV=%d  3-let=%d  4-let=%d"
                       (:name r) (:length r) (:gv r)
                       (count (:hits-3 r)) (count (:hits-4 r)))))

    ;; Words shared across proteins
    (println "\n  ── Words shared across proteins ──")
    (let [all-words (for [r results
                          w (:top-3 r)]
                      {:protein (:name r) :word (:word w)
                       :count (:count w)})
          by-word (group-by :word all-words)
          shared (->> by-word
                      (filter (fn [[_ entries]] (> (count entries) 1)))
                      (sort-by (fn [[_ entries]] (- (reduce + (map :count entries))))))]
      (doseq [[word entries] (take 20 shared)]
        (println (format "    %-8s  in %d proteins: %s"
                         word
                         (count entries)
                         (str/join ", " (map #(str (:protein %) "(×" (:count %) ")")
                                             entries))))))

    {:station 5 :name "The DNA"
     :proteins results}))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 123: THE CURE FOR LEPROSY")
  (println "  Leviticus 13–14 — Diagnosis, quarantine, purification")
  (println "  Two birds. Cedar, scarlet, hyssop. Blood + oil.")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (leprosy-vocabulary)
        s1 (station-1-diagnosis)
        s2 (station-2-spreading)
        s3 (station-3-two-birds)
        s4 (station-4-offerings)]

    (println "\n════════════════════════════════════════════════")
    (println "  TEXT SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the purification ritual
    (println "\n  ── Combined Leprosy Slide (Lev 13:1-17 + 14:1-20) ──")
    (let [diag-letters (fetch-verse-letters "Leviticus" 13 1 17)
          spread-letters (fetch-verse-letters "Leviticus" 13 18 46)
          birds-letters (fetch-verse-letters "Leviticus" 14 1 7)
          offer-letters (fetch-verse-letters "Leviticus" 14 8 20)
          all-letters (str diag-letters spread-letters birds-letters offer-letters)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Leprosy ritual: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across the leprosy ritual:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across the leprosy ritual:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    ;; Station 5: The DNA
    (let [s5 (station-5-dna)]

      (println "\n════════════════════════════════════════════════")
      (println "  COMPLETE SUMMARY")
      (println "════════════════════════════════════════════════")
      (println "  Stations 1-4: Leviticus text through the oracle")
      (println "  Station 5: M. leprae proteins through the oracle")
      (println "  The Torah describes the disease. The DNA IS the disease.")
      (println "  What do they share?")

      [vocab s1 s2 s3 s4 s5])))

(comment
  (run-all)

  ;; Run individual stations
  (leprosy-vocabulary)
  (station-1-diagnosis)
  (station-2-spreading)
  (station-3-two-birds)
  (station-4-offerings)

  ;; Station 5: DNA
  (station-5-dna)

  ;; Individual proteins
  (run-protein (nth leprae-proteins 0))  ;; MMP-I — the face
  (run-protein (nth leprae-proteins 1))  ;; Ag85B — the waxy armor
  (run-protein (nth leprae-proteins 2))  ;; Hsp65 — heat shock
  (run-protein (nth leprae-proteins 3))  ;; SOD — the shield
  (run-protein (nth leprae-proteins 4))  ;; Bacterioferritin — iron

  ;; The key pairs
  (query-word "טהר" "clean")
  (query-word "טמא" "unclean")       ;; GV=50 = jubilee!

  ;; Hair = gate
  (query-word "שער" "hair/gate")

  ;; Flesh = gospel
  (query-word "בשר" "flesh/gospel")

  ;; Sprinkle → prostitute
  (query-word "נזה" "sprinkle")

  ;; The worm of Psalm 22
  (query-word "תולעת" "crimson/worm")

  ;; Hyssop
  (query-word "אזוב" "hyssop")

  ;; The lamb
  (query-word "כבש" "lamb")

  ;; Atonement
  (query-word "כפר" "atonement")

  ;; Compare with exp 107 (bronze serpent)
  ;; COVID spike: תחש (covering) ×7, שטנה (Satan) ×3, נחשת (bronze) ×2
  ;; HIV gp160: EXACT serpent at 260, נמרד (Nimrod) ×3, שילה (Shiloh) ×3
  ;; What does leprosy say?

  nil)
