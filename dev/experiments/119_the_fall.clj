(ns experiments.119-the-fall
  "Experiment 119: The Fall — Genesis 3.

   The origin of the wound.
   Serpent (נחש) = Messiah (משיח) = GV 358.
   Enmity (איבה) → father (אביה) confirmed in basin GV=18=life.
   Coming (בוא) → necromancer (אוב) confirmed in basin GV=9.

   Experiment 106 queried individual words but never sluiced the passage.
   Now walk the source. The tree, the serpent, the eyes, the sword.

   What we already know:
   - Tree (עץ) = 0/0 dark
   - Way (דרך) = 0/0 dark
   - Garment (בגד) = stamps 72
   - Sword (חרב) → Choose basin (size 10, GV=216)
   - Cherub (כרוב) → bless basin
   - Seed (זרע) → helper (עזר) basin
   - Eat (אכל) → food = altar basin

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-118) ──────────────────

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
;; FALL VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn fall-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  FALL VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The principals
     ["נחש"    "serpent"]            ;; GV=358 = Messiah (משיח)!
     ["משיח"   "Messiah"]           ;; GV=358 = serpent. Same number.
     ["אדם"    "Adam/man"]
     ["חוה"    "Eve"]               ;; "mother of all living"
     ["אשה"    "woman"]

     ;; The tree
     ["עץ"     "tree"]              ;; 0/0 dark
     ["דעת"    "knowledge"]
     ["טוב"    "good"]              ;; GV=17 = telling basin
     ["רע"     "evil"]

     ;; The act
     ["אכל"    "eat"]               ;; → food = altar basin
     ["פקח"    "open (eyes)"]
     ["ערום"   "naked/cunning"]     ;; same word for serpent's cunning AND their nakedness
     ["חמד"    "desire"]
     ["שכל"    "insight/prudence"]

     ;; The curse
     ["ארר"    "curse"]
     ["איבה"   "enmity"]            ;; → father (אביה) basin, GV=18=life
     ["זרע"    "seed"]              ;; → helper (עזר) basin
     ["שוף"    "bruise/crush"]      ;; the bruising of the head/heel
     ["עקב"    "heel"]              ;; Jacob's name! The heel.
     ["ראש"    "head"]              ;; → blessed (אשר) basin
     ["עצב"    "pain/toil"]
     ["אדמה"   "ground/soil"]
     ["עפר"    "dust"]              ;; → Pharaoh in ladder
     ["מות"    "death"]
     ["זעה"    "sweat"]

     ;; The covering
     ["עור"    "skin"]
     ["כתנת"   "garment/tunic"]
     ["בגד"    "garment"]           ;; stamps 72
     ["כרוב"   "cherub"]            ;; → bless basin
     ["חרב"    "sword"]             ;; → Choose basin
     ["להט"    "flaming"]
     ["דרך"    "way"]               ;; 0/0 dark
     ["חיים"   "life"]              ;; tree of life
     ["שלח"    "send"]
     ["גן"     "garden"]]))         ;; GV=53 = stone (אבן)!

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Temptation ──────────────────────────
;; Genesis 3:1-7
;; The serpent was more cunning than any beast. "Did God say...?"
;; The woman sees: good for food, pleasant to eyes, desired to make wise.
;; She eats. Gives to her husband. He eats. Eyes opened. They know nakedness.

(defn station-1-temptation []
  (walk-station 1 "The Temptation" "Genesis" 3 1 7
    [["נחש"    "serpent"]
     ["ערום"   "cunning/naked"]
     ["חיה"    "living creature"]
     ["אכל"    "eat"]
     ["מות"    "die/death"]
     ["אלהים"  "God"]
     ["עין"    "eye"]
     ["פקח"    "open"]
     ["טוב"    "good"]
     ["רע"     "evil"]
     ["ידע"    "know"]
     ["חמד"    "desire"]
     ["שכל"    "wisdom/insight"]
     ["תאוה"   "lust/desire"]
     ["פרי"    "fruit"]
     ["תאנה"   "fig"]
     ["חגר"    "gird"]]))

;; ── Station 2: The Hiding ──────────────────────────
;; Genesis 3:8-13
;; They heard the voice of the LORD walking in the garden in the cool of the day.
;; They hid. "Where are you?" "I was afraid, I was naked, I hid."
;; "Who told you?" "The woman gave me." "The serpent deceived me."

(defn station-2-hiding []
  (walk-station 2 "The Hiding" "Genesis" 3 8 13
    [["קול"    "voice"]
     ["הלך"    "walk"]
     ["גן"     "garden"]
     ["רוח"    "cool/spirit"]
     ["יום"    "day"]
     ["חבא"    "hide"]
     ["איה"    "where"]
     ["ירא"    "afraid"]
     ["ערום"   "naked"]
     ["נגד"    "tell"]
     ["אשה"    "woman"]
     ["נתן"    "give"]
     ["נשא"    "deceive/lift"]]))

;; ── Station 3: The Curse ──────────────────────────
;; Genesis 3:14-19
;; Serpent cursed. Enmity between serpent and woman, seed and seed.
;; He shall bruise your head, you shall bruise his heel.
;; Woman: pain in childbearing. Man: cursed ground, sweat, dust.

(defn station-3-curse []
  (walk-station 3 "The Curse" "Genesis" 3 14 19
    [["ארר"    "curse"]
     ["בהמה"   "beast"]
     ["שדה"    "field"]
     ["גחון"   "belly"]
     ["עפר"    "dust"]
     ["אכל"    "eat"]
     ["איבה"   "enmity"]
     ["זרע"    "seed"]
     ["שוף"    "bruise/crush"]
     ["ראש"    "head"]
     ["עקב"    "heel"]
     ["עצב"    "pain/toil"]
     ["הרון"   "conception"]
     ["ילד"    "bear/give birth"]
     ["תשוקה"  "desire"]
     ["משל"    "rule"]
     ["אדמה"   "ground"]
     ["קוץ"    "thorn"]
     ["דרדר"   "thistle"]
     ["לחם"    "bread"]
     ["זעה"    "sweat"]
     ["שוב"    "return"]
     ["מות"    "death"]]))

;; ── Station 4: The Covering ──────────────────────────
;; Genesis 3:20-24
;; Adam names Eve (חוה) — "mother of all living."
;; God makes garments of skin (עור) and clothes them.
;; "Man has become like one of us, knowing good and evil."
;; He sends them out. He places cherubim and a flaming sword
;; that turns every way, to guard the way to the tree of life.

(defn station-4-covering []
  (walk-station 4 "The Covering" "Genesis" 3 20 24
    [["חוה"    "Eve"]
     ["אם"     "mother"]
     ["חי"     "living"]
     ["כתנת"   "tunic"]
     ["עור"    "skin"]
     ["לבש"    "clothe"]
     ["ידע"    "know"]
     ["טוב"    "good"]
     ["רע"     "evil"]
     ["שלח"    "send"]
     ["עבד"    "work/serve"]
     ["אדמה"   "ground"]
     ["גרש"    "drive out"]
     ["כרוב"   "cherub"]
     ["להט"    "flaming"]
     ["חרב"    "sword"]
     ["הפך"    "turn"]
     ["שמר"    "guard"]
     ["דרך"    "way"]
     ["עץ"     "tree"]
     ["חיים"   "life"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 119: THE FALL")
  (println "  Genesis 3 — The origin of the wound")
  (println "  נחש (serpent) = משיח (Messiah) = GV 358")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (fall-vocabulary)
        s1 (station-1-temptation)
        s2 (station-2-hiding)
        s3 (station-3-curse)
        s4 (station-4-covering)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the entire chapter
    (println "\n  ── Combined Fall Slide (entire Genesis 3) ──")
    (let [all-letters (fetch-verse-letters "Genesis" 3 1 24)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Genesis 3: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across the Fall:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across the Fall:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [vocab s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (fall-vocabulary)
  (station-1-temptation)
  (station-2-hiding)
  (station-3-curse)
  (station-4-covering)

  ;; The serpent = the Messiah
  (query-word "נחש" "serpent")       ;; GV=358
  (query-word "משיח" "Messiah")     ;; GV=358

  ;; Enmity → father
  (query-word "איבה" "enmity")      ;; GV=18 = life (חי)

  ;; The bruising
  (query-word "שוף" "bruise/crush")

  ;; The seed promise
  (query-word "זרע" "seed")         ;; → helper (עזר) basin

  ;; The sword that guards
  (query-word "חרב" "sword")        ;; → Choose basin

  ;; The cherubim at the gate
  (query-word "כרוב" "cherub")      ;; → bless basin

  nil)
