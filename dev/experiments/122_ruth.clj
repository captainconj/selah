(ns experiments.122-ruth
  "Experiment 122: Ruth — The Book of Ruth.

   She came (באה) → love (אהב). The basin IS the book.
   The great-grandmother of David. The Moabitess at the threshing floor.
   Boaz = 'in him is strength' (בעז). The kinsman-redeemer (גאל).

   Short enough to sluice whole — 4 chapters, ~85 verses.

   What we already know:
   - Gate (שער): God reads wicked (רשע) inside it — Ruth 4 happens at the gate
   - Kindness (חסד) = ghost zone, GV=72 — Ruth IS chesed incarnate
   - Seed (זרע) → helper (עזר) basin — Ruth IS the helper
   - Return (שוב) — 'Call me Mara, for the LORD has dealt bitterly'
   - Wing (כנף) — 'spread your wing over your maidservant'
   - Redeem (גאל) — the kinsman-redeemer, the go'el

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-121) ──────────────────

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
                                         :meaning (:meaning k)
                                         :reading-count (:reading-count k)})
                                      known)))}))))

(defn word-frequencies [hits]
  (->> hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
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
            (println (format "    [%3d] %s → %s (%s)"
                             position letters
                             (:word top) (or (:meaning top) "?")))))
        (println "\n  Top 3-letter words:")
        (doseq [{:keys [word meaning count]} (take 15 top-3)]
          (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))

        (println "\n  ── 4-letter sliding window (cherubim's view) ──")
        (let [hits-4 (slide-text letters 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count letters) 3)))
          (doseq [{:keys [position letters top-5]} hits-4]
            (let [top (first top-5)]
              (println (format "    [%3d] %s → %s (%s)"
                               position letters
                               (:word top) (or (:meaning top) "?")))))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word meaning count]} (take 15 top-4)]
            (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))

          {:station station-num :name station-name
           :letter-count (count letters) :gv gv
           :words word-results
           :hits-3 hits-3 :hits-4 hits-4
           :top-3 top-3 :top-4 top-4})))))

;; ══════════════════════════════════════════════════════
;; RUTH VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn ruth-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  RUTH VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The principals
     ["רות"    "Ruth"]              ;; the Moabitess
     ["נעמי"   "Naomi"]             ;; "pleasant" → "bitter"
     ["בעז"    "Boaz"]              ;; "in him is strength"
     ["עבד"    "Obed"]              ;; "servant" — David's grandfather
     ["דוד"    "David"]             ;; the end of the line

     ;; The acts
     ["שוב"    "return"]            ;; Naomi returns, Ruth clings
     ["דבק"    "cling"]             ;; "Ruth clung to her"
     ["לקט"    "glean"]             ;; the gleaning
     ["גאל"    "redeem"]            ;; the kinsman-redeemer
     ["קנה"    "buy/acquire"]       ;; Boaz acquires Ruth

     ;; The places
     ["בית"    "house"]             ;; Bethlehem = house of bread
     ["לחם"    "bread"]             ;; bread/Bethlehem
     ["מואב"   "Moab"]              ;; the foreign land
     ["שדה"    "field"]             ;; the field of gleaning
     ["גרן"    "threshing floor"]   ;; the night encounter
     ["שער"    "gate"]              ;; God reads wicked inside. But justice happens here.

     ;; The relationships
     ["חמות"   "mother-in-law"]
     ["כלה"    "daughter-in-law/bride"]  ;; same word as "finish"!
     ["אשה"    "wife/woman"]
     ["חסד"    "kindness/lovingkindness"]  ;; ghost zone, GV=72!
     ["אהב"    "love"]              ;; GV=8
     ["טוב"    "good"]              ;; GV=17

     ;; The night
     ["לילה"   "night"]
     ["רגל"    "foot/feet"]         ;; she uncovers his feet
     ["גלה"    "uncover"]
     ["כנף"    "wing/corner"]       ;; "spread your wing over your maidservant"
     ["שכב"    "lie down"]          ;; Aaron's reading of the lamb

     ;; The blessing
     ["ברך"    "bless"]
     ["זרע"    "seed"]              ;; → helper basin
     ["ילד"    "bear/give birth"]
     ["עד"     "witness"]
     ["פרץ"    "Perez/breach"]      ;; ancestor of David
     ["תמר"    "Tamar"]             ;; another unlikely woman in the line

     ;; The rest
     ["מנוחה"  "rest"]              ;; "shall I not seek rest for you?"
     ["מצא"    "find"]              ;; "may you find favor"
     ["חן"     "favor/grace"]]))    ;; fixed point in the oracle

;; ══════════════════════════════════════════════════════
;; THE FIVE STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Departure ──────────────────────────
;; Ruth 1:1-18
;; Famine. Elimelech takes family to Moab. He dies. Sons marry Moabite women.
;; Sons die. Naomi returns. Orpah goes back. Ruth clings:
;; "Where you go I will go, and where you lodge I will lodge.
;;  Your people shall be my people, and your God my God.
;;  Where you die I will die, and there I will be buried."

(defn station-1-departure []
  (walk-station 1 "The Departure" "Ruth" 1 1 18
    [["רעב"    "famine"]
     ["הלך"    "go/walk"]
     ["מואב"   "Moab"]
     ["מות"    "die/death"]
     ["אשה"    "wife/woman"]
     ["בן"     "son"]
     ["שוב"    "return"]
     ["חמות"   "mother-in-law"]
     ["כלה"    "daughter-in-law"]
     ["נשק"    "kiss"]
     ["בכה"    "weep"]
     ["דבק"    "cling"]
     ["עם"     "people"]
     ["אלהים"  "God"]
     ["לון"    "lodge"]
     ["מות"    "death"]
     ["קבר"    "bury"]
     ["חסד"    "kindness"]
     ["מנוחה"  "rest"]
     ["יד"     "hand"]
     ["מר"     "bitter"]]))

;; ── Station 2: The Gleaning ──────────────────────────
;; Ruth 2:1-23
;; Ruth goes to glean in the field of Boaz. Boaz notices her.
;; "Whose young woman is this?" He shows favor.
;; "May you be fully rewarded by the LORD, the God of Israel,
;;  under whose wings you have come to take refuge."

(defn station-2-gleaning []
  (walk-station 2 "The Gleaning" "Ruth" 2 1 23
    [["שדה"    "field"]
     ["בעז"    "Boaz"]
     ["לקט"    "glean"]
     ["שבלת"   "ear of grain"]
     ["קציר"   "harvest"]
     ["חן"     "favor/grace"]
     ["נכר"    "foreign/recognize"]
     ["נפל"    "fall"]
     ["פנים"   "face"]
     ["כנף"    "wing"]
     ["חסה"    "refuge"]
     ["שכר"    "reward"]
     ["נערה"   "young woman"]
     ["אכל"    "eat"]
     ["שתה"    "drink"]
     ["מים"    "water"]
     ["שעורה"  "barley"]
     ["דבק"    "cling"]]))

;; ── Station 3: The Threshing Floor ──────────────────
;; Ruth 3:1-18
;; Naomi's plan. Ruth goes to the threshing floor at night.
;; She uncovers his feet and lies down.
;; Boaz wakes: "Who are you?" "I am Ruth your maidservant.
;;  Spread your wing over your maidservant, for you are a redeemer."
;; "You have shown more kindness at the end than at the beginning."

(defn station-3-threshing-floor []
  (walk-station 3 "The Threshing Floor" "Ruth" 3 1 18
    [["מנוחה"  "rest"]
     ["רחץ"    "wash"]
     ["סוך"    "anoint"]
     ["שמלה"   "garment"]
     ["גרן"    "threshing floor"]
     ["אכל"    "eat"]
     ["שתה"    "drink"]
     ["שכב"    "lie down"]
     ["רגל"    "foot"]
     ["גלה"    "uncover"]
     ["לילה"   "night"]
     ["חרד"    "tremble"]
     ["אשה"    "woman"]
     ["אמה"    "maidservant"]
     ["כנף"    "wing/corner"]
     ["פרש"    "spread"]
     ["גאל"    "redeem"]
     ["חסד"    "kindness"]
     ["טוב"    "good"]
     ["בקר"    "morning"]
     ["שעורה"  "barley"]]))

;; ── Station 4: The Gate ──────────────────────────
;; Ruth 4:1-12
;; Boaz goes to the gate. Sits. Calls the nearer kinsman.
;; The nearer kinsman cannot redeem — "lest I ruin my own inheritance."
;; He draws off his sandal. Boaz acquires Ruth.
;; The elders witness: "May the LORD make this woman like Rachel and Leah."
;; "May your house be like the house of Perez whom Tamar bore to Judah."

(defn station-4-gate []
  (walk-station 4 "The Gate" "Ruth" 4 1 12
    [["שער"    "gate"]
     ["ישב"    "sit"]
     ["גאל"    "redeem"]
     ["קנה"    "buy/acquire"]
     ["נעל"    "sandal"]
     ["שלף"    "draw off"]
     ["עד"     "witness"]
     ["זקן"    "elder"]
     ["רחל"    "Rachel"]
     ["לאה"    "Leah"]
     ["ישראל"  "Israel"]
     ["בית"    "house"]
     ["פרץ"    "Perez"]
     ["תמר"    "Tamar"]
     ["יהודה"  "Judah"]
     ["חיל"    "worth/valor"]
     ["שם"     "name"]
     ["נחלה"   "inheritance"]]))

;; ── Station 5: The Son ──────────────────────────
;; Ruth 4:13-22
;; Boaz takes Ruth. She conceives. Bears a son.
;; The women say to Naomi: "Blessed be the LORD, who has not left you
;;  without a redeemer. Your daughter-in-law who loves you,
;;  who is better to you than seven sons, has borne him."
;; Naomi takes the child, becomes his nurse.
;; "A son is born to Naomi!" They name him OBED.
;; He is the father of Jesse, the father of David.

(defn station-5-the-son []
  (walk-station 5 "The Son" "Ruth" 4 13 22
    [["לקח"    "take"]
     ["אשה"    "wife"]
     ["בוא"    "come/go in"]
     ["הרון"   "conceive"]
     ["ילד"    "bear"]
     ["בן"     "son"]
     ["ברך"    "bless"]
     ["גאל"    "redeemer"]
     ["שם"     "name"]
     ["ישראל"  "Israel"]
     ["כלה"    "daughter-in-law"]
     ["אהב"    "love"]
     ["טוב"    "good"]
     ["שבע"    "seven"]
     ["אמן"    "nurse"]
     ["עבד"    "Obed/servant"]
     ["ישי"    "Jesse"]
     ["דוד"    "David"]
     ["פרץ"    "Perez"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 122: RUTH")
  (println "  The Moabitess. The kinsman-redeemer. The line of David.")
  (println "  באה → אהב. She came = love.")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (ruth-vocabulary)
        s1 (station-1-departure)
        s2 (station-2-gleaning)
        s3 (station-3-threshing-floor)
        s4 (station-4-gate)
        s5 (station-5-the-son)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the entire book
    (println "\n  ── Combined Ruth Slide (entire book) ──")
    (let [all-letters (str (fetch-verse-letters "Ruth" 1 1 22)
                           (fetch-verse-letters "Ruth" 2 1 23)
                           (fetch-verse-letters "Ruth" 3 1 18)
                           (fetch-verse-letters "Ruth" 4 1 22))
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Ruth: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across all of Ruth:")
      (doseq [{:keys [word meaning count]} (take 25 top-3)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))
      (println "\n  Top 25 words (4-letter) across all of Ruth:")
      (doseq [{:keys [word meaning count]} (take 25 top-4)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count))))

    [vocab s1 s2 s3 s4 s5]))

(comment
  (run-all)

  ;; Run individual stations
  (ruth-vocabulary)
  (station-1-departure)
  (station-2-gleaning)
  (station-3-threshing-floor)
  (station-4-gate)
  (station-5-the-son)

  ;; She came = love
  (query-word "באה" "she came")
  (query-word "אהב" "love")

  ;; Kindness = ghost zone
  (query-word "חסד" "kindness")      ;; GV=72, ghost zone

  ;; The redeemer
  (query-word "גאל" "redeem")

  ;; The wing
  (query-word "כנף" "wing")

  ;; Sandal — same word as shoe at the bush!
  (query-word "נעל" "sandal/shoe")

  ;; Boaz
  (query-word "בעז" "Boaz")

  nil)
