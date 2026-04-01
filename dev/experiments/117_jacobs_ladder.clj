(ns experiments.117-jacobs-ladder
  "Experiment 117: Jacob's Ladder.

   Genesis 28. The staircase. The gate of heaven.

   Jacob flees Esau. Lies down at a PLACE. Takes a STONE for a pillow.
   Dreams a LADDER set up on earth, its top reaching to heaven.
   Angels ascending and descending on it. The LORD above it speaks:
   'I am the LORD God of Abraham and Isaac. The land on which you lie,
    to you I will give it and to your seed.'
   Jacob wakes: 'This is none other than the HOUSE OF GOD,
                  and this is the GATE OF HEAVEN.'
   He takes the STONE, sets it as a PILLAR, pours OIL on it.
   Names the place BETHEL — house of God.

   What we already know:
   - Stone (אבן) = father + son (א+בן) = 53 = garden. Fixed point.
   - Gate (שער): God reads wicked (רשע) inside it.
   - Oil (שמן): stamps 72 at the cross.
   - Door (דלת) → birth (תלד). To enter is to be born.
   - Ladder (סלם) GV=130 = Sinai (סיני) = eye (עין). The ladder IS the mountain.
   - Jacob (יעקב) — the heel-grabber. Israel (ישראל) — the God-wrestler.
   - Angel (מלאך) = 0/0 (invisible in Gen 22). Is it still invisible here?

   The big picture plan says:
   'The staircase, the gate of heaven, the stone, the oil.
    This is none other than the house of God, and this is the gate of heaven.
    The door. The birth.'

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-116) ──────────────────

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
;; LADDER VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn ladder-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  LADDER VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The ladder and the staircase
     ["סלם"    "ladder"]            ;; GV=130 = Sinai (סיני) = eye (עין). THE word.
     ["מלאך"   "angel/messenger"]   ;; 0/0 in Gen 22. Still invisible?
     ["עלה"    "go up/ascend"]      ;; angels ascending
     ["ירד"    "go down/descend"]   ;; angels descending
     ["שמים"   "heaven"]            ;; top reaching to heaven

     ;; The stone and the pillar
     ["אבן"    "stone"]             ;; GV=53. Father+son. Garden. Pillow→pillar.
     ["מצבה"   "pillar"]            ;; GV=137! The stump is 137. The pillar is 137.
     ["שמן"    "oil"]               ;; poured on the stone. Anointing.
     ["משח"    "anoint"]            ;; rejoice→anoint (from cornerstone)

     ;; The place
     ["מקום"   "place"]             ;; "he came to a PLACE" — nameless, then named
     ["בית"    "house"]             ;; house of God = Bethel
     ["אל"     "God"]               ;; Beth-EL
     ["שער"    "gate"]              ;; gate of heaven. God reads wicked (רשע) in it.
     ["דלת"    "door"]              ;; door→birth (תלד)

     ;; The promise
     ["זרע"    "seed"]              ;; your seed shall be as the dust of the earth
     ["ארץ"    "earth/land"]        ;; the land I will give to you. 0/0 in visions.
     ["עפר"    "dust"]              ;; "as the dust of the earth"
     ["פרץ"    "spread/break forth"];; "you shall spread abroad"

     ;; The principals
     ["יעקב"   "Jacob"]             ;; the heel-grabber. Basin→?
     ["עשו"    "Esau"]              ;; the brother. The red. The hairy.
     ["ישראל"  "Israel"]            ;; the name he will receive

     ;; The seeing
     ["חלם"    "dream"]             ;; "he dreamed, and behold, a ladder"
     ["ירא"    "fear"]              ;; "How awesome is this place!"
     ["נדר"    "vow"]               ;; Jacob vows
     ["מעשר"   "tithe"]             ;; "I will surely give a tenth"

     ;; The promise words
     ["שמר"    "keep/guard"]        ;; "I will keep you wherever you go"
     ["שוב"    "return"]            ;; "I will bring you back to this land"
     ["עזב"    "forsake"]]))        ;; "I will not forsake you" — GV=79, invisible at cross

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Flight ──────────────────────────────
;; Genesis 28:1-5
;; Isaac blesses Jacob and sends him away.
;; "Go to Paddan-aram, to the house of Bethuel your mother's father,
;;  and take a wife from there, from the daughters of Laban."
;; The blessing of Abraham upon Jacob.
;; The departure. The exile begins.

(defn station-1-the-flight []
  (walk-station 1 "The Flight" "Genesis" 28 1 5
    [["ברך"    "bless"]
     ["צוה"    "command"]
     ["אשה"    "woman/wife"]
     ["לבן"    "Laban/white"]
     ["אם"     "mother"]
     ["אב"     "father"]
     ["קהל"    "assembly"]
     ["גוי"    "nation"]
     ["ירש"    "possess/inherit"]]))

;; ── Station 2: The Dream ──────────────────────────────
;; Genesis 28:10-15
;; (Skipping vv.6-9 — Esau's marriages, a sidebar.)
;; Jacob leaves Beersheba and goes toward Haran.
;; He comes to a PLACE. The sun has set. He takes a STONE
;; and puts it under his head. He lies down.
;; He DREAMS — and behold! A LADDER set up on earth,
;; its top reaching to HEAVEN. ANGELS of God ascending and descending.
;; The LORD stands above it: "I am the LORD God of Abraham and Isaac.
;; The land on which you lie, to you I will give it and to your seed.
;; Your seed shall be as the DUST of the earth.
;; You shall spread abroad to west, east, north, south.
;; In you and in your seed all the families of the earth shall be blessed.
;; I am with you. I will keep you. I will bring you back.
;; I will not forsake you."

(defn station-2-the-dream []
  (walk-station 2 "The Dream" "Genesis" 28 10 15
    [["סלם"    "ladder"]
     ["אבן"    "stone"]
     ["מקום"   "place"]
     ["חלם"    "dream"]
     ["מלאך"   "angel"]
     ["שמים"   "heaven"]
     ["ארץ"    "earth/land"]
     ["עפר"    "dust"]
     ["ירא"    "fear"]
     ["זרע"    "seed"]
     ["משפחה"  "family"]
     ["שמר"    "keep/guard"]
     ["שוב"    "return"]
     ["עזב"    "forsake"]
     ["ימה"    "westward"]
     ["קדמה"   "eastward"]
     ["צפנה"   "northward"]
     ["נגבה"   "southward"]]))

;; ── Station 3: The Awakening ──────────────────────────
;; Genesis 28:16-19
;; Jacob wakes and says:
;; "SURELY the LORD is in this place, and I did not know it."
;; He is AFRAID: "How AWESOME is this place!
;; This is none other than the HOUSE OF GOD,
;; and this is the GATE OF HEAVEN."
;; He rises early. Takes the STONE. Sets it as a PILLAR.
;; Pours OIL on top of it.
;; He calls the name of the place BETHEL —
;; but the name of the city was LUZ at first.

(defn station-3-the-awakening []
  (walk-station 3 "The Awakening" "Genesis" 28 16 19
    [["אבן"    "stone"]
     ["מצבה"   "pillar"]
     ["שמן"    "oil"]
     ["בית"    "house"]
     ["אל"     "God"]
     ["שער"    "gate"]
     ["שמים"   "heaven"]
     ["נורא"   "awesome"]
     ["ירא"    "fear"]
     ["ידע"    "know"]
     ["לוז"    "Luz (almond)"]
     ["ראש"    "head/top"]
     ["שכם"    "morning/shoulder"]
     ["קום"    "arise"]
     ["קרא"    "call/name"]]))

;; ── Station 4: The Vow ────────────────────────────────
;; Genesis 28:20-22
;; Jacob vows a vow:
;; "If God will be WITH me, and will KEEP me in this WAY that I go,
;;  and will give me BREAD to eat and CLOTHING to wear,
;;  so that I return to my father's HOUSE in PEACE,
;;  then the LORD shall be my God.
;;  And this STONE which I have set as a PILLAR
;;  shall be GOD'S HOUSE,
;;  and of all that you give me I will surely give a TENTH to you."

(defn station-4-the-vow []
  (walk-station 4 "The Vow" "Genesis" 28 20 22
    [["נדר"    "vow"]
     ["שמר"    "keep/guard"]
     ["דרך"    "way/road"]
     ["לחם"    "bread"]
     ["בגד"    "clothing/garment"]
     ["לבש"    "wear/clothe"]
     ["שלום"   "peace"]
     ["שוב"    "return"]
     ["אב"     "father"]
     ["בית"    "house"]
     ["אבן"    "stone"]
     ["מצבה"   "pillar"]
     ["מעשר"   "tithe"]
     ["עשר"    "ten/tithe"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 117: JACOB'S LADDER")
  (println "  Genesis 28 — The staircase, the gate of heaven")
  (println "  סלם GV=130 = סיני = עין. The ladder IS the mountain.")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (ladder-vocabulary)
        s1 (station-1-the-flight)
        s2 (station-2-the-dream)
        s3 (station-3-the-awakening)
        s4 (station-4-the-vow)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the entire chapter
    (println "\n  ── Combined Ladder Slide (entire Genesis 28) ──")
    (let [all-letters (fetch-verse-letters "Genesis" 28 1 22)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Genesis 28: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across all of Genesis 28:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across all of Genesis 28:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [vocab s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (ladder-vocabulary)
  (station-1-the-flight)
  (station-2-the-dream)
  (station-3-the-awakening)
  (station-4-the-vow)

  ;; The ladder = Sinai = eye
  (query-word "סלם" "ladder")       ;; GV=130
  (query-word "סיני" "Sinai")       ;; GV=130
  (query-word "עין" "eye")          ;; GV=130

  ;; The pillar = the stump = 137
  (query-word "מצבה" "pillar")      ;; GV=137!
  (query-word "אופן" "wheel")      ;; GV=137

  ;; Stone = father+son = garden
  (query-word "אבן" "stone")       ;; GV=53
  (query-word "גן" "garden")       ;; GV=53

  ;; Gate and door
  (query-word "שער" "gate")
  (query-word "דלת" "door")

  ;; Jacob → ?
  (query-word "יעקב" "Jacob")
  (query-word "עשו" "Esau")
  (query-word "ישראל" "Israel")

  nil)
