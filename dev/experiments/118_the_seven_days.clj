(ns experiments.118-the-seven-days
  "Experiment 118: The Seven Days — Genesis 1:1–2:3.

   The foundation of everything. 7 days = the a-axis of the 4D space.
   Experiment 057 studied creation's sweep across c-values but never
   sluiced the actual text through the oracle.

   304,850 = 2 × 5² × 7 × 13 × 67.
   The 7-axis IS the week that built the world.

   What we already know:
   - Good (טוב, GV=17) = the telling basin
   - Food = altar (GV=57 both ways)
   - Earth (ארץ) = 0/0 dark in every vision
   - Image (צלם) = 3 illuminations = throne signature
   - Morning (בקר) = grave→morning basin
   - Spirit/wind (רוח) — priest reads HOLE (חור), God reads spirit
   - Face (פנים) = ghost zone
   - Seed (זרע) → helper (עזר) basin

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-117) ──────────────────

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
    (doseq [head [:aaron :god :truth :mercy]]
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
          (println (format "    %s ×%d" word count)))

        (println "\n  ── 4-letter sliding window (cherubim's view) ──")
        (let [hits-4 (slide-text letters 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count letters) 3)))
          (doseq [{:keys [position letters top-5]} hits-4]
            (let [top (first top-5)]
              (println (format "    [%3d] %s → %s (%s)"
                               position letters
                               (:word top)))))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word count]} (take 15 top-4)]
            (println (format "    %s ×%d" word count)))

          {:station station-num :name station-name
           :letter-count (count letters) :gv gv
           :words word-results
           :hits-3 hits-3 :hits-4 hits-4
           :top-3 top-3 :top-4 top-4})))))

;; ══════════════════════════════════════════════════════
;; CREATION VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn creation-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  CREATION VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The acts
     ["ברא"    "create"]             ;; the first verb in Torah
     ["אמר"    "say"]               ;; "and God said" — the speech acts
     ["עשה"    "make/do"]           ;; making vs creating
     ["יצר"    "form"]              ;; not used in Gen 1, but appears in Gen 2
     ["בדל"    "separate/divide"]   ;; the fundamental operation — separation

     ;; The judgments
     ["טוב"    "good"]              ;; GV=17 = telling basin. "And God saw that it was good."
     ["ברך"    "bless"]             ;; Day 5 and Day 7

     ;; The spaces
     ["שמים"   "heaven"]            ;; created on Day 1
     ["ארץ"    "earth"]             ;; 0/0 dark in every vision
     ["מים"    "water"]             ;; separated Day 2
     ["רקיע"   "firmament"]         ;; the expanse
     ["יבשה"   "dry land"]          ;; Day 3

     ;; The lights
     ["אור"    "light"]             ;; Day 1 light — before luminaries
     ["חשך"    "darkness"]          ;; separated from light
     ["מאור"   "luminary"]          ;; sun and moon, Day 4
     ["שמש"    "sun"]               ;; the greater light
     ["ירח"    "moon"]              ;; the lesser light
     ["כוכב"   "star"]              ;; the stars also

     ;; The living
     ["חי"     "living"]            ;; living creatures
     ["נפש"    "soul"]              ;; "living soul"
     ["זרע"    "seed"]              ;; seed→helper basin
     ["עשב"    "herb/grass"]        ;; vegetation
     ["פרי"    "fruit"]             ;; fruit trees

     ;; The image
     ["צלם"    "image"]             ;; GV=160. 3 illum = throne! "in our image"
     ["דמות"   "likeness"]          ;; "after our likeness"
     ["אדם"    "man/Adam"]          ;; created Day 6
     ["זכר"    "male"]              ;; "male and female"
     ["נקבה"   "female"]            ;; "He created them"
     ["רדה"    "dominion"]          ;; "have dominion over"

     ;; The rhythm
     ["ערב"    "evening"]           ;; "and there was evening"
     ["בקר"    "morning"]           ;; grave→morning basin!
     ["יום"    "day"]               ;; numbered days
     ["שבת"    "sabbath/rest"]      ;; Day 7
     ["קדש"    "holy"]              ;; God sanctified the seventh day
     ["רוח"    "spirit/wind"]       ;; "spirit of God moved upon the face of the waters"
     ["פנים"   "face"]]))           ;; ghost zone. "face of the deep."

;; ══════════════════════════════════════════════════════
;; THE SEVEN STATIONS — one per day
;; ══════════════════════════════════════════════════════

;; ── Day 1: Light ──────────────────────────────
;; Genesis 1:1-5
;; "In the beginning God created the heaven and the earth.
;;  And the earth was without form and void, and darkness was upon
;;  the face of the deep. And the spirit of God moved upon the face
;;  of the waters. And God said, Let there be light: and there was light.
;;  And God saw the light, that it was good: and God divided the light
;;  from the darkness. And God called the light Day, and the darkness
;;  He called Night. And the evening and the morning were the first day."

(defn station-1-light []
  (walk-station 1 "Day 1: Light" "Genesis" 1 1 5
    [["ראשית"  "beginning"]
     ["ברא"    "create"]
     ["שמים"   "heaven"]
     ["ארץ"    "earth"]
     ["תהו"    "formless/void"]
     ["בהו"    "void/emptiness"]
     ["חשך"    "darkness"]
     ["תהום"   "deep"]
     ["רוח"    "spirit/wind"]
     ["פנים"   "face"]
     ["מים"    "water"]
     ["אור"    "light"]
     ["טוב"    "good"]
     ["בדל"    "separate"]
     ["יום"    "day"]
     ["לילה"   "night"]
     ["ערב"    "evening"]
     ["בקר"    "morning"]]))

;; ── Day 2: Waters ──────────────────────────────
;; Genesis 1:6-8
;; "And God said, Let there be a firmament in the midst of the waters,
;;  and let it divide the waters from the waters."

(defn station-2-waters []
  (walk-station 2 "Day 2: Waters" "Genesis" 1 6 8
    [["רקיע"   "firmament"]
     ["מים"    "water"]
     ["בדל"    "separate"]
     ["תוך"    "midst"]
     ["שמים"   "heaven"]
     ["קרא"    "call/name"]]))

;; ── Day 3: Land + Vegetation ──────────────────────
;; Genesis 1:9-13
;; Dry land appears. Earth brings forth grass, herb yielding seed,
;; fruit tree yielding fruit after its kind.

(defn station-3-land []
  (walk-station 3 "Day 3: Land + Vegetation" "Genesis" 1 9 13
    [["יבשה"   "dry land"]
     ["ארץ"    "earth"]
     ["ים"     "sea"]
     ["מקוה"   "gathering"]
     ["דשא"    "grass"]
     ["עשב"    "herb"]
     ["זרע"    "seed"]
     ["עץ"     "tree"]
     ["פרי"    "fruit"]
     ["מין"    "kind"]
     ["טוב"    "good"]]))

;; ── Day 4: Luminaries ──────────────────────────
;; Genesis 1:14-19
;; Sun, moon, stars. Signs, seasons, days, years.
;; The greater light to rule the day, the lesser to rule the night.

(defn station-4-luminaries []
  (walk-station 4 "Day 4: Luminaries" "Genesis" 1 14 19
    [["מאור"   "luminary"]
     ["רקיע"   "firmament"]
     ["אות"    "sign"]
     ["מועד"   "season/appointed time"]
     ["שמש"    "sun"]
     ["ירח"    "moon"]
     ["כוכב"   "star"]
     ["ממשלה"  "dominion/rule"]
     ["גדול"   "great"]
     ["קטן"    "small"]
     ["בדל"    "separate"]
     ["אור"    "light"]
     ["חשך"    "darkness"]
     ["טוב"    "good"]]))

;; ── Day 5: Sea Creatures + Birds ──────────────────
;; Genesis 1:20-23
;; Let the waters swarm with living creatures,
;; and let birds fly above the earth in the firmament.
;; Great sea creatures. God blessed them: be fruitful, multiply.

(defn station-5-creatures []
  (walk-station 5 "Day 5: Sea Creatures + Birds" "Genesis" 1 20 23
    [["שרץ"    "swarm"]
     ["נפש"    "soul"]
     ["חי"     "living"]
     ["עוף"    "bird"]
     ["תנין"   "sea creature"]
     ["רמש"    "creep"]
     ["מים"    "water"]
     ["מין"    "kind"]
     ["ברך"    "bless"]
     ["פרה"    "be fruitful"]
     ["רבה"    "multiply"]
     ["מלא"    "fill"]
     ["טוב"    "good"]]))

;; ── Day 6: Animals + Man ──────────────────────
;; Genesis 1:24-31
;; Living creatures after their kind. Then:
;; "Let us make man in our image, after our likeness."
;; Male and female. Dominion. Every green herb for food.
;; "And God saw everything that He had made, and behold, it was VERY good."

(defn station-6-man []
  (walk-station 6 "Day 6: Animals + Man" "Genesis" 1 24 31
    [["בהמה"   "beast/cattle"]
     ["רמש"    "creep"]
     ["חיה"    "living creature"]
     ["אדם"    "man/Adam"]
     ["צלם"    "image"]
     ["דמות"   "likeness"]
     ["זכר"    "male"]
     ["נקבה"   "female"]
     ["רדה"    "dominion"]
     ["כבש"    "subdue"]            ;; same word as lamb! What does the oracle see?
     ["דג"     "fish"]
     ["עשב"    "herb"]
     ["אכל"    "eat/food"]
     ["מאד"    "very"]
     ["טוב"    "good"]]))

;; ── Day 7: Rest ──────────────────────────────
;; Genesis 2:1-3
;; "And the heavens and the earth were finished, and all their host.
;;  And on the seventh day God finished His work which He had made;
;;  and He rested on the seventh day from all His work.
;;  And God blessed the seventh day and hallowed it,
;;  because in it He rested from all His work
;;  which God had created to make."

(defn station-7-rest []
  (walk-station 7 "Day 7: Rest" "Genesis" 2 1 3
    [["כלה"    "finish"]
     ["צבא"    "host/army"]
     ["שביעי"  "seventh"]
     ["מלאכה"  "work"]
     ["שבת"    "rest/sabbath"]
     ["ברך"    "bless"]
     ["קדש"    "holy/sanctify"]
     ["ברא"    "create"]
     ["עשה"    "make"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 118: THE SEVEN DAYS")
  (println "  Genesis 1:1 – 2:3 — The week that built the world")
  (println "  7 days = the a-axis of the 4D space")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (creation-vocabulary)
        s1 (station-1-light)
        s2 (station-2-waters)
        s3 (station-3-land)
        s4 (station-4-luminaries)
        s5 (station-5-creatures)
        s6 (station-6-man)
        s7 (station-7-rest)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5 s6 s7]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across all seven days
    (println "\n  ── Combined Creation Slide (Gen 1:1 – 2:3) ──")
    (let [ch1-letters (fetch-verse-letters "Genesis" 1 1 31)
          ch2-letters (fetch-verse-letters "Genesis" 2 1 3)
          all-letters (str ch1-letters ch2-letters)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Creation week: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across creation:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across creation:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %s ×%d" word count))))

    [vocab s1 s2 s3 s4 s5 s6 s7]))

(comment
  (run-all)

  ;; Run individual stations
  (creation-vocabulary)
  (station-1-light)
  (station-2-waters)
  (station-3-land)
  (station-4-luminaries)
  (station-5-creatures)
  (station-6-man)
  (station-7-rest)

  ;; Key queries
  (query-word "טוב" "good")           ;; GV=17, the telling basin
  (query-word "אור" "light")
  (query-word "צלם" "image")          ;; 3 illum = throne
  (query-word "ברא" "create")
  (query-word "בדל" "separate")
  (query-word "שבת" "sabbath")
  (query-word "רוח" "spirit")
  (query-word "כבש" "subdue/lamb")    ;; same word! dominion = lamb?
  (query-word "בקר" "morning")        ;; grave→morning basin

  nil)
