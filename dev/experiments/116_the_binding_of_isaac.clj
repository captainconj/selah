(ns experiments.116-the-binding-of-isaac
  "Experiment 116: The Binding of Isaac.

   Genesis 22. The Aqedah. The first love.

   'Take your son, your only son, whom you love — Isaac —
    and go to the land of Moriah, and offer him there as a burnt offering.'

   This is the first time 'love' (אהב) appears in the Bible.
   The 13-axis. The silent axis. And here it speaks.

   We already know:
   - עקד (binding) was found in Isaiah 6 at positions 313-316:
     seed→barren→Binding→holy. The Aqedah produces holiness.
   - עקד appears 5,270× in the human genome (44th most frequent).
   - Stone (אבן) = father + son (א+בן) = 53 = garden.
   - Ram (איל) dominates Psalm 22 (×17).
   - Fire (אש) produces exactly 72 readings.
   - Lamb splits: God/Right see כבש, Aaron/Left see שכב.

   Now walk the source. The mountain, the wood, the ram, the angel's voice.

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-115) ──────────────────

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
;; AQEDAH VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn aqedah-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  AQEDAH VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The principals
     ["אברהם"  "Abraham"]          ;; the father who obeys
     ["יצחק"   "Isaac"]            ;; laughter. the son.
     ["אלהים"  "God"]              ;; the one who tests — basin→אליהם (to them)
     ["מלאך"   "angel/messenger"]  ;; who calls from heaven

     ;; The act
     ["עקד"    "bind"]             ;; THE word. Found in Isaiah 6, in genomes.
     ["נסה"    "test/try"]         ;; "God tested Abraham"
     ["שחט"    "slaughter"]        ;; basin→ ?
     ["שלח"    "send/stretch"]     ;; "stretch out your hand"

     ;; The first love
     ["אהב"    "love"]             ;; First occurrence in Torah. GV=8. The 13-axis speaks.
     ["יחיד"   "only/unique"]      ;; "your only son"
     ["בכור"   "firstborn"]        ;; Isaac as firstborn of promise

     ;; The offering
     ["עולה"   "burnt offering"]   ;; what Isaac was to be
     ["כבש"    "lamb"]             ;; the lamb that splits
     ["שה"     "lamb/sheep"]       ;; "God will provide the lamb"
     ["איל"    "ram"]              ;; the substitute caught in the thicket

     ;; The instruments
     ["מאכלת"  "knife"]            ;; the specific word — only here and Judges 19
     ["אש"     "fire"]             ;; = 72 readings. The breastplate counts itself.
     ["עץ"     "wood/tree"]        ;; the wood of the offering

     ;; The place
     ["מוריה"  "Moriah"]           ;; the mountain. Root: ירה (teach) or ראה (see).
     ["הר"     "mountain"]         ;; "on the mountain the LORD will provide"
     ["מזבח"   "altar"]            ;; GV=57 = build (בנה). The altar IS the building.

     ;; The seeing
     ["ירא"    "fear/see"]         ;; "now I know you fear God" / the LORD sees
     ["ראה"    "see/provide"]      ;; "God will see to the lamb"

     ;; The blessing
     ["ברך"    "bless"]            ;; the oath-blessing
     ["שבע"    "swear/seven"]      ;; "by myself I have sworn"
     ["זרע"    "seed"]             ;; "your seed shall possess"
     ["כוכב"   "star"]             ;; "as the stars of heaven"
     ["ירש"    "possess/inherit"]  ;; "your seed shall possess the gate"
     ["שער"    "gate"]]))          ;; "the gate of his enemies" (fixed point)

;; ══════════════════════════════════════════════════════
;; THE FIVE STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Command ─────────────────────────────
;; Genesis 22:1-2
;; "And it came to pass after these things that God tested Abraham,
;;  and said to him, 'Abraham!' And he said, 'Here I am.'
;;  And He said, 'Take now your son, your only son, whom you love —
;;  Isaac — and go to the land of Moriah, and offer him there
;;  as a burnt offering on one of the mountains which I shall tell you.'"
;;
;; The first "love" in the Bible. אהב appears for the first time.
;; The 13-axis, the love-axis, the silent axis — here it speaks.

(defn station-1-the-command []
  (walk-station 1 "The Command" "Genesis" 22 1 2
    [["נסה"    "test"]
     ["הנני"   "here I am"]
     ["בן"     "son"]
     ["יחיד"   "only/unique"]
     ["אהב"    "love"]
     ["יצחק"   "Isaac"]
     ["מוריה"  "Moriah"]
     ["עולה"   "burnt offering"]
     ["אמר"    "say"]]))

;; ── Station 2: The Journey ─────────────────────────────
;; Genesis 22:3-8
;; Abraham rises early. Saddles the donkey. Splits the wood.
;; Two young men walk with them. On the third day, Abraham sees the place.
;; He tells the servants: "We will worship and WE WILL RETURN."
;; Isaac carries the wood. Abraham carries the fire and the knife.
;; "Where is the lamb?" "God will provide the lamb, my son."
;;
;; The conversation between father and son. The wood on the son's back.
;; The fire in the father's hand. The knife.

(defn station-2-the-journey []
  (walk-station 2 "The Journey" "Genesis" 22 3 8
    [["חמור"   "donkey"]
     ["בקע"    "split/cleave"]
     ["עץ"     "wood"]
     ["נער"    "young man"]
     ["שלישי"  "third"]
     ["יום"    "day"]
     ["מקום"   "place"]
     ["שחה"    "worship/bow"]
     ["שוב"    "return"]
     ["אש"     "fire"]
     ["מאכלת"  "knife"]
     ["שה"     "lamb/sheep"]
     ["ראה"    "see/provide"]
     ["ילד"    "child/boy"]
     ["הלך"    "walk"]
     ["יחדו"   "together"]]))

;; ── Station 3: The Binding ─────────────────────────────
;; Genesis 22:9-14
;; Abraham builds the altar. Arranges the wood. BINDS Isaac.
;; Lays him on the altar, on the wood. Takes the knife to slaughter.
;; The angel of the LORD calls from heaven: "Abraham, Abraham!"
;; "Do not stretch out your hand against the boy."
;; Abraham lifts his eyes — a RAM caught in the thicket by its horns.
;; Offers the ram INSTEAD of his son.
;; Names the place: YHWH-Yireh — "the LORD will see/provide."
;;
;; The center of the chapter. The knife raised. The voice.
;; The ram in the thicket. The naming.

(defn station-3-the-binding []
  (walk-station 3 "The Binding" "Genesis" 22 9 14
    [["מזבח"   "altar"]
     ["עקד"    "bind"]
     ["עולה"   "burnt offering"]
     ["מאכלת"  "knife"]
     ["שחט"    "slaughter"]
     ["מלאך"   "angel"]
     ["שמים"   "heaven"]
     ["שלח"    "send/stretch"]
     ["יד"     "hand"]
     ["נער"    "boy/young man"]
     ["עין"    "eye"]
     ["איל"    "ram"]
     ["סבך"    "thicket"]
     ["קרן"    "horn"]
     ["תחת"    "instead/under"]
     ["ירא"    "fear/see"]
     ["ראה"    "see/provide"]
     ["קרא"    "call/name"]]))

;; ── Station 4: The Blessing ────────────────────────────
;; Genesis 22:15-19
;; The angel calls a SECOND time from heaven.
;; "By myself I have sworn, says the LORD,
;;  because you have done this thing
;;  and have not withheld your son, your only son,
;;  I will surely bless you, and I will surely multiply your seed
;;  as the stars of heaven and as the sand on the seashore.
;;  And your seed shall possess the gate of his enemies.
;;  In your seed all the nations of the earth shall be blessed."
;;
;; The oath. The multiplication. The gate. The nations.

(defn station-4-the-blessing []
  (walk-station 4 "The Blessing" "Genesis" 22 15 19
    [["שבע"    "swear/seven"]
     ["נאם"    "declare/oracle"]
     ["ברך"    "bless"]
     ["רבה"    "multiply"]
     ["זרע"    "seed"]
     ["כוכב"   "star"]
     ["שמים"   "heaven"]
     ["חול"    "sand"]
     ["שפה"    "shore/lip"]
     ["ירש"    "possess/inherit"]
     ["שער"    "gate"]
     ["אויב"   "enemy"]
     ["גוי"    "nation"]
     ["חשך"    "withhold"]
     ["בן"     "son"]
     ["יחיד"   "only"]]))

;; ── Station 5: The Genealogy ───────────────────────────
;; Genesis 22:20-24
;; After these things — the genealogy of Nahor (Abraham's brother).
;; Twelve sons. And among them: Bethuel, the father of Rebekah.
;; The next generation. The bride is already being prepared.
;; While Isaac lies bound on the altar, his future wife is being born.

(defn station-5-the-genealogy []
  (walk-station 5 "The Genealogy" "Genesis" 22 20 24
    [["ילד"    "bear/give birth"]
     ["מלכה"   "Milcah/queen"]
     ["בתואל"  "Bethuel"]
     ["רבקה"   "Rebekah"]
     ["אח"     "brother"]
     ["פלגש"   "concubine"]
     ["בן"     "son"]
     ["בת"     "daughter"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 116: THE BINDING OF ISAAC")
  (println "  Genesis 22 — The Aqedah")
  (println "  The first love. The mountain. The ram.")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (aqedah-vocabulary)
        s1 (station-1-the-command)
        s2 (station-2-the-journey)
        s3 (station-3-the-binding)
        s4 (station-4-the-blessing)
        s5 (station-5-the-genealogy)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the entire chapter
    (println "\n  ── Combined Aqedah Slide (entire Genesis 22) ──")
    (let [all-letters (fetch-verse-letters "Genesis" 22 1 24)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Genesis 22: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across all of Genesis 22:")
      (doseq [{:keys [word meaning count]} (take 25 top-3)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))
      (println "\n  Top 25 words (4-letter) across all of Genesis 22:")
      (doseq [{:keys [word meaning count]} (take 25 top-4)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count))))

    [vocab s1 s2 s3 s4 s5]))

(comment
  (run-all)

  ;; Run individual stations
  (aqedah-vocabulary)
  (station-1-the-command)
  (station-2-the-journey)
  (station-3-the-binding)
  (station-4-the-blessing)
  (station-5-the-genealogy)

  ;; The first love
  (query-word "אהב" "love")         ;; GV=8. First in Torah at Gen 22:2.
  (query-word "אהבה" "love(noun)")  ;; GV=13. The 13-axis.

  ;; Father + son = stone
  (query-word "אב" "father")
  (query-word "בן" "son")
  (query-word "אבן" "stone")        ;; GV=53 = garden

  ;; The lamb question
  (query-word "שה" "lamb/sheep")
  (query-word "כבש" "lamb")
  (query-word "איל" "ram")

  ;; The instruments
  (query-word "מאכלת" "knife")
  (query-word "אש" "fire")          ;; 72 readings
  (query-word "עץ" "wood")

  ;; The binding
  (query-word "עקד" "bind")

  ;; Moriah
  (query-word "מוריה" "Moriah")

  ;; The seeing
  (query-word "ירא" "fear/see")
  (query-word "ראה" "see/provide")

  nil)
