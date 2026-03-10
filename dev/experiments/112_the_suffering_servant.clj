(ns experiments.112-the-suffering-servant
  "Experiment 112: The Suffering Servant (Isaiah 53).

   The most contested passage in the Hebrew Bible.
   12 verses. Four stations. The lamb in the prophet's own words.

   We know from experiments 108-111:
   - Lamb (כבש): God reads 'lamb,' Aaron reads 'lie down' (שכב)
   - In Revelation (110): ALL FOUR readers agree — 'lie down.' The split heals.
   - Slaughter (טבח) → trust (בטח). Violence resolves to safety.
   - Door (דלת) → birth (תלד). To enter is to be born.

   Now we walk the Suffering Servant himself through the oracle.
   What does the breastplate see in the man of sorrows?

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-111) ──────────────────

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

;; ── The Four Stations ────────────────────────────────────
;;
;; Isaiah 52:13 - 53:12 is the complete "fourth servant song."
;; But 52:13-15 is the prologue spoken by God.
;; 53:1-12 is the report — spoken by the witnesses.
;;
;; We walk 53:1-12 as the body.
;; We also query 52:13-15 as station 0 (the herald).

(defn station-0-herald []
  (walk-station 0 "The Herald (God Speaks)" "Isaiah" 52 13 15
    [["עבד"    "servant"]
     ["רם"     "high/exalted"]
     ["נשא"    "lifted up"]
     ["גבה"    "exalted (very)"]
     ["שמם"    "appalled/desolate"]
     ["משחת"   "marred/disfigured"]
     ["נזה"    "sprinkle"]
     ["מלך"    "king"]
     ["גוי"    "nation"]]))

(defn station-1-report []
  (walk-station 1 "The Report" "Isaiah" 53 1 3
    [["שמועה"  "report/rumor"]
     ["זרוע"   "arm"]
     ["יונק"   "shoot/sapling"]
     ["שרש"    "root"]
     ["תאר"    "form/beauty"]
     ["הדר"    "splendor"]
     ["מראה"   "appearance"]
     ["חמד"    "desire"]
     ["בזה"    "despised"]
     ["חדל"    "rejected/ceased"]
     ["איש"    "man"]
     ["מכאב"   "pain/sorrow"]
     ["חלי"    "sickness/grief"]
     ["פנים"   "face"]]))

(defn station-2-bearing []
  (walk-station 2 "The Bearing" "Isaiah" 53 4 6
    [["חלי"    "sickness/grief"]
     ["מכאב"   "pain"]
     ["נשא"    "bear/carry"]
     ["סבל"    "carry/burden"]
     ["נגע"    "stricken/plague"]
     ["נכה"    "smitten"]
     ["ענה"    "afflicted"]
     ["חלל"    "pierced/profaned"]
     ["פשע"    "transgression"]
     ["דכא"    "crushed"]
     ["עון"    "iniquity"]
     ["מוסר"   "discipline/chastisement"]
     ["שלום"   "peace"]
     ["חבורה"  "stripe/wound"]
     ["רפא"    "heal"]
     ["צאן"    "flock/sheep"]
     ["תעה"    "go astray"]
     ["דרך"    "way"]
     ["פגע"    "lay upon/intercede"]]))

(defn station-3-silence []
  (walk-station 3 "The Silence" "Isaiah" 53 7 9
    [["נגש"    "oppressed"]
     ["ענה"    "afflicted"]
     ["פה"     "mouth"]
     ["כבש"    "lamb"]
     ["טבח"    "slaughter"]
     ["רחל"    "ewe/Rachel"]
     ["גזז"    "shearer"]
     ["אלם"    "silent/mute"]
     ["עצר"    "restrained/cut off"]
     ["משפט"   "judgment/justice"]
     ["דור"    "generation"]
     ["ארץ"    "earth/land"]
     ["חיים"   "life/living"]
     ["פשע"    "transgression"]
     ["עם"     "people"]
     ["נגע"    "plague/stricken"]
     ["קבר"    "grave"]
     ["מות"    "death"]
     ["רשע"    "wicked"]
     ["עשיר"   "rich"]
     ["חמס"    "violence"]
     ["מרמה"   "deceit"]]))

(defn station-4-offering []
  (walk-station 4 "The Offering" "Isaiah" 53 10 12
    [["חפץ"    "delight/will"]
     ["דכא"    "crush"]
     ["חלה"    "make sick/grieve"]
     ["אשם"    "guilt offering"]
     ["זרע"    "seed/offspring"]
     ["נפש"    "soul"]
     ["ימים"   "days"]
     ["חפץ"    "purpose/delight"]
     ["צלח"    "prosper/succeed"]
     ["צדיק"   "righteous"]
     ["עבד"    "servant"]
     ["צדק"    "righteousness"]
     ["רבים"   "the many"]
     ["עון"    "iniquity"]
     ["סבל"    "carry"]
     ["חלק"    "portion/divide"]
     ["עצום"   "mighty"]
     ["שלל"    "spoil/plunder"]
     ["ערה"    "pour out"]
     ["מות"    "death"]
     ["פשע"    "transgression"]
     ["פגע"    "intercede"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 112: THE SUFFERING SERVANT")
  (println "  Walking Isaiah 52:13 - 53:12 through the breastplate")
  (println "  5 stations · key words · sliding windows")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [s0 (station-0-herald)
        s1 (station-1-report)
        s2 (station-2-bearing)
        s3 (station-3-silence)
        s4 (station-4-offering)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s0 s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Full servant song (52:13 - 53:12)
    (println "\n  ── Full Servant Song Slide ──")
    (let [herald-letters (fetch-verse-letters "Isaiah" 52 13 15)
          body-letters (fetch-verse-letters "Isaiah" 53 1 12)
          all-letters (str herald-letters body-letters)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full servant song: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across full song:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across full song:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [s0 s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (station-0-herald)
  (station-1-report)
  (station-2-bearing)
  (station-3-silence)
  (station-4-offering)

  ;; Quick checks
  (sefaria/fetch-chapter "Isaiah" 52)
  (sefaria/fetch-chapter "Isaiah" 53)

  ;; The lamb — God reads lamb, Aaron reads lie down
  (query-word "כבש" "lamb")

  ;; Slaughter → trust
  (query-word "טבח" "slaughter")

  ;; The guilt offering
  (query-word "אשם" "guilt offering")

  ;; The pierced one
  (query-word "חלל" "pierced")

  ;; Crushed
  (query-word "דכא" "crushed")

  ;; The servant
  (query-word "עבד" "servant")

  ;; Righteous
  (query-word "צדיק" "righteous")

  ;; Intercede
  (query-word "פגע" "intercede")

  ;; Peace
  (query-word "שלום" "peace")

  ;; Heal
  (query-word "רפא" "heal")

  ;; The many
  (query-word "רבים" "the many")

  ;; Pour out
  (query-word "ערה" "pour out")

  ;; Soul
  (query-word "נפש" "soul")

  ;; Seed/offspring
  (query-word "זרע" "seed")
  )
