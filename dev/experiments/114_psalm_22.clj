(ns experiments.114-psalm-22
  "Experiment 114: Psalm 22 — The Crucifixion Psalm.

   Written by David ~1000 BC. A thousand years before Rome invented crucifixion.

   'My God, my God, why have you forsaken me?'
   'They pierced my hands and feet.'
   'They divide my garments among them and cast lots for my clothing.'

   Experiment 113 slid verses 1-2 through the oracle. Now we walk the full psalm.
   32 verses. Five stations. The song at the foot of the cross.

   From experiments 112-113, we already know:
   - Blood = forgive = 3 illuminations = throne signature
   - Forsake (עזב) GV=79 = positions on the cross. God cannot read it.
   - Grave → morning. Crown → covenant. Finish → everything.
   - The priest reads HOLE where God reads SPIRIT.
   - Clothing → return. Lot → sojourner. Door → birth.

   Two witnesses: Isaiah saw the servant 700 years before.
   David sang the song 1000 years before.

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-113) ──────────────────

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
;; THE FIVE STATIONS OF PSALM 22
;; ══════════════════════════════════════════════════════
;;
;; Sefaria uses Hebrew versification: v1 = superscription.
;; "My God, my God" = v2 in Hebrew = v1 in English.
;;
;; Station 1: vv. 1-6  (superscription through "our fathers trusted")
;; Station 2: vv. 7-12 (worm, mockery, womb, mother)
;; Station 3: vv. 13-19 (bulls, lion, water, bones, dogs, hands/feet)
;; Station 4: vv. 20-23 (garments, plea, sword, deliver)
;; Station 5: vv. 24-32 (praise, assembly, nations, born)

;; ── Station 1: The Hind of the Dawn ──────────────────
;; "On the hind of the dawn" — the superscription.
;; "My God, my God, why have you forsaken me?"
;; "You are holy, enthroned upon the praises of Israel."
;; "Our fathers trusted in you."

(defn station-1-cry []
  (walk-station 1 "The Hind of the Dawn" "Psalms" 22 1 6
    [["אילת"   "hind/doe"]
     ["שחר"    "dawn"]
     ["מזמור"  "psalm"]
     ["שאגה"   "roaring"]
     ["ישועה"  "salvation"]
     ["קדוש"   "holy"]
     ["תהלה"   "praise"]
     ["בטח"    "trust"]
     ["פלט"    "deliver/escape"]
     ["דומיה"  "silence"]]))

;; ── Station 2: The Worm and the Womb ─────────────────
;; "I am a worm and not a man."
;; The scarlet worm (תולעת שני): the female dies giving birth,
;; staining the wood crimson. Used to dye priestly garments.
;; "You drew me from the womb."
;; רחם = womb AND mercy. Same root.

(defn station-2-worm []
  (walk-station 2 "The Worm and the Womb" "Psalms" 22 7 12
    [["תולעת"  "worm"]
     ["חרפה"   "reproach/shame"]
     ["שפה"    "lip"]
     ["גלל"    "roll/commit"]
     ["חפץ"    "delight"]
     ["בטן"    "belly/womb"]
     ["שד"     "breast"]
     ["רחם"    "womb/mercy"]
     ["אם"     "mother"]
     ["צרה"    "trouble/distress"]
     ["עזר"    "help"]]))

;; ── Station 3: The Beasts and the Pouring ────────────
;; "Many bulls surround me."
;; "They open their mouths — a lion tearing and roaring."
;; "Poured out like water, all my bones out of joint."
;; "My heart is like wax, melted within me."
;; "Dogs surround me — like a lion, my hands and my feet."
;;
;; v17 is the contested verse:
;;   MT: כָּאֲרִי יָדַי וְרַגְלָי — "like a lion my hands and my feet"
;;   LXX/DSS: "they pierced my hands and feet"
;;   We query BOTH readings through the oracle.

(defn station-3-beasts []
  (walk-station 3 "The Beasts and the Pouring" "Psalms" 22 13 19
    [["פר"     "bull"]
     ["אביר"   "mighty one"]
     ["בשן"    "Bashan"]
     ["אריה"   "lion"]
     ["טרף"    "tear/prey"]
     ["שפך"    "pour out"]
     ["דונג"   "wax"]
     ["כח"     "strength/power"]
     ["חרס"    "potsherd/clay"]
     ["לשון"   "tongue"]
     ["כלב"    "dog"]
     ["כארי"   "like-a-lion (MT)"]
     ["כרה"    "dig/pierce (LXX)"]
     ["רגל"    "foot"]]))

;; ── Station 4: The Garments and the Plea ─────────────
;; "They divide my garments among them,
;;  and for my clothing they cast lots."
;; "Deliver my soul from the sword."
;; "From the horns of the wild oxen — you have answered me!"

(defn station-4-garments []
  (walk-station 4 "The Garments and the Plea" "Psalms" 22 20 23
    [["חלק"    "divide/portion"]
     ["נפל"    "fall/cast"]
     ["נצל"    "deliver/rescue"]
     ["נפש"    "soul"]
     ["יחידה"  "only one/unique"]
     ["קרן"    "horn"]
     ["ראם"    "wild ox"]
     ["ענה"    "answer/afflict"]]))

;; ── Station 5: The Praise and the Nations ────────────
;; "I will declare your name to my brothers."
;; "All the seed of Jacob — glorify him!"
;; "The meek shall eat and be satisfied."
;; "All the ends of the earth shall remember and turn to the LORD."
;; "A people yet to be born — that he has done it."
;;
;; כבד (glorify/heavy) GV=26 = YHWH.
;; The command to glorify carries the Name's weight.

(defn station-5-praise []
  (walk-station 5 "The Praise and the Nations" "Psalms" 22 24 32
    [["אח"     "brother"]
     ["קהל"    "assembly"]
     ["הלל"    "praise"]
     ["כבד"    "glorify/heavy"]
     ["ענו"    "meek/humble"]
     ["שבע"    "satisfy/seven"]
     ["זכר"    "remember"]
     ["שוב"    "return/repent"]
     ["אפס"    "end/nothing"]
     ["מלוכה"  "kingdom/reign"]
     ["נולד"   "born"]
     ["צדקה"   "righteousness"]
     ["עשה"    "do/make"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 114: PSALM 22")
  (println "  The Crucifixion Psalm — David's song, 1000 BC")
  (println "  32 verses · 5 stations · key words · sliding windows")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-cry)
        s2 (station-2-worm)
        s3 (station-3-beasts)
        s4 (station-4-garments)
        s5 (station-5-praise)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Full psalm slide
    (println "\n  ── Full Psalm 22 Slide ──")
    (let [all-letters (fetch-verse-letters "Psalms" 22 1 32)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full psalm: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across full psalm:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across full psalm:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [s1 s2 s3 s4 s5]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-cry)
  (station-2-worm)
  (station-3-beasts)
  (station-4-garments)
  (station-5-praise)

  ;; Quick checks
  (sefaria/fetch-chapter "Psalms" 22)

  ;; The contested verse
  (query-word "כארי" "like-a-lion (MT)")
  (query-word "כרה" "dig/pierce (LXX)")

  ;; The worm — the scarlet worm dies giving birth, staining crimson
  (query-word "תולעת" "worm")

  ;; Womb = mercy. Same root.
  (query-word "רחם" "womb/mercy")

  ;; Glorify (כבד) GV=26 = YHWH
  (query-word "כבד" "glorify/heavy")

  ;; The hind of the dawn
  (query-word "אילת" "hind/doe")
  (query-word "שחר" "dawn")

  ;; The psalm's last word: עשה (he has done it)
  (query-word "עשה" "do/make")

  ;; Born — the last station
  (query-word "נולד" "born")

  ;; Trust → what the oracle already established
  (query-word "בטח" "trust")

  nil)
