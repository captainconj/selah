(ns experiments.115-the-cornerstone
  "Experiment 115: The Cornerstone.

   The stone the builders rejected became the head of the corner.
   This image runs through the entire Bible like a vein of gold.

   Four passages. Four angles on one stone:

   1. Psalm 118:22-29  — The rejected stone. David's psalm. The gate of YHWH.
   2. Isaiah 28:14-22  — The tested stone. The precious cornerstone. The sure foundation.
   3. Daniel 2:31-45   — The stone cut without hands. Shatters the statue. Becomes a mountain.
   4. Zechariah 4:1-10  — The capstone. 'Grace, grace to it!'

   The words to observe:
   - אבן (stone) = א + בן = father + son. GV=53 = garden (גן).
   - פנה (corner) shares root with פנים (face) = ghost zone (22 illum, 0 readings).
   - ראש פנה (head of the corner) — the cornerstone's title.
   - חן (grace) = what the builders shout. חסד = ghost zone (GV=72).
   - מאס (reject) — what the builders do. What does the breastplate do?

   From experiments 112-114, we already know:
   - Stone (אבן) is a fixed point — all four readers agree.
   - Stone = garden = 53 = Fibonacci staircase sum.
   - King = invisible (0/0). Face = ghost zone (22/0).
   - Grace (חסד) GV=72 = the breastplate cannot name itself.

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-114) ──────────────────

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
;; CORNERSTONE VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn cornerstone-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  CORNERSTONE VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [["אבן"    "stone"]           ;; GV=53. א+בן = father+son. = garden.
     ["פנה"    "corner"]          ;; shares root with face (פנים, ghost zone)
     ["ראש"    "head"]            ;; head of the corner
     ["בנה"    "build"]           ;; the builders
     ["מאס"    "reject"]          ;; the builders rejected
     ["יסד"    "found/establish"] ;; lay a foundation
     ["מוסד"   "foundation"]      ;; the sure foundation
     ["צור"    "rock"]            ;; the Rock of Israel
     ["סלע"    "rock/cliff"]      ;; Sela — another rock word
     ["פלא"    "wonder/marvelous"];; "this is the LORD's doing; it is marvelous"
     ["חן"     "grace"]           ;; "Grace, grace to it!"
     ["חסד"    "lovingkindness"]  ;; ghost zone. GV=72. The breastplate cannot name itself.
     ["בחן"    "test/examine"]    ;; the tested stone
     ["יקר"    "precious"]        ;; the precious cornerstone
     ["מלך"    "king"]            ;; invisible (0/0) in visions
     ["פנים"   "face"]            ;; ghost zone (22/0). Corner shares this root.
     ["גן"     "garden"]          ;; GV=53 = stone. Garden = stone.
     ["צלם"    "image/idol"]      ;; Nebuchadnezzar's statue
     ["הר"     "mountain"]        ;; the stone becomes a mountain
     ["שער"    "gate"]]))         ;; "This is the gate of the LORD"

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Rejected Stone ──────────────────────
;; Psalm 118:22-29
;; "The stone the builders rejected has become the head of the corner.
;;  This is the LORD's doing; it is marvelous in our eyes.
;;  This is the day the LORD has made — let us rejoice and be glad in it.
;;  O LORD, save! O LORD, prosper!
;;  Blessed is he who comes in the name of the LORD."
;;
;; The psalm Jesus quoted. The psalm the crowd sang on Palm Sunday.
;; The psalm Peter quoted at Pentecost. The psalm Paul quoted in Romans.

(defn station-1-rejected-stone []
  (walk-station 1 "The Rejected Stone" "Psalms" 118 22 29
    [["אבן"    "stone"]
     ["בנה"    "build"]
     ["מאס"    "reject"]
     ["ראש"    "head"]
     ["פנה"    "corner"]
     ["פלא"    "wonder"]
     ["יום"    "day"]
     ["שמח"    "rejoice"]
     ["גיל"    "be glad"]
     ["אנא"    "please/beseech"]
     ["ישועה"  "salvation"]
     ["הצלח"   "prosper"]
     ["ברוך"   "blessed"]
     ["שם"     "name"]
     ["אור"    "light"]
     ["חג"     "festival"]
     ["עבת"    "cord/branch"]
     ["קרן"    "horn"]
     ["מזבח"   "altar"]
     ["טוב"    "good"]]))

;; ── Station 2: The Sure Foundation ──────────────────────
;; Isaiah 28:14-22
;; "Behold, I lay in Zion a stone,
;;  a tested stone, a precious cornerstone, a sure foundation.
;;  He who believes will not be in haste.
;;  And I will make justice the line, and righteousness the plummet."
;;
;; The foundation stone. The tested stone. The refuge from the flood.

(defn station-2-sure-foundation []
  (walk-station 2 "The Sure Foundation" "Isaiah" 28 14 22
    [["אבן"    "stone"]
     ["בחן"    "test"]
     ["יקר"    "precious"]
     ["פנה"    "corner"]
     ["מוסד"   "foundation"]
     ["אמן"    "believe/amen"]
     ["חוש"    "hurry/haste"]
     ["משפט"   "justice"]
     ["צדקה"   "righteousness"]
     ["קו"     "line/measuring"]
     ["משקלת"  "plummet/plumb-line"]
     ["ברד"    "hail"]
     ["מחסה"   "refuge"]
     ["כזב"    "lie/falsehood"]
     ["שקר"    "lie/deception"]
     ["שטף"    "flood/overflow"]
     ["שוט"    "scourge/whip"]
     ["כרת"    "cut (covenant)"]
     ["מות"    "death"]
     ["שאול"   "Sheol/grave"]]))

;; ── Station 3: The Stone Without Hands ──────────────────
;; Daniel 2:31-45
;; "You watched while a stone was cut out without hands,
;;  which struck the image on its feet of iron and clay,
;;  and broke them in pieces...
;;  And the stone that struck the image became a great mountain
;;  and filled the whole earth."
;;
;; Daniel 2 is in Aramaic (like Daniel 7).
;; The breastplate reads Aramaic letters the same as Hebrew letters —
;; same alphabet, same stones light up. But the reading may differ.

(defn station-3-stone-without-hands []
  (walk-station 3 "The Stone Without Hands" "Daniel" 2 31 45
    [["אבן"    "stone"]
     ["צלם"    "image/idol"]
     ["זהב"    "gold"]
     ["כסף"    "silver"]
     ["נחש"    "bronze/serpent"]
     ["ברזל"   "iron"]
     ["חסף"    "clay (Aramaic)"]
     ["רגל"    "foot"]
     ["טור"    "mountain (Aramaic)"]
     ["הר"     "mountain (Hebrew)"]
     ["מלא"    "fill"]
     ["תבר"    "break (Aramaic)"]
     ["שבר"    "break (Hebrew)"]
     ["מלך"    "king"]
     ["מלכו"   "kingdom (Aramaic)"]
     ["קום"    "rise/establish"]
     ["יד"     "hand"]
     ["ארע"    "earth (Aramaic)"]
     ["ארץ"    "earth (Hebrew)"]]))

;; ── Station 4: The Capstone ─────────────────────────────
;; Zechariah 4:1-10
;; "Who are you, O great mountain? Before Zerubbabel you shall become a plain!
;;  And he shall bring forth the capstone with shouts of
;;  'Grace, grace to it!'"
;;
;; "Not by might, nor by power, but by my Spirit, says the LORD."
;; The seven eyes. The seven lamps. The two olive trees.
;; Grace — חן — is what the builders shout.
;; But חסד (lovingkindness, GV=72) is ghost zone.

(defn station-4-capstone []
  (walk-station 4 "The Capstone" "Zechariah" 4 1 10
    [["אבן"    "stone"]
     ["חן"     "grace"]
     ["חסד"    "lovingkindness"]
     ["רוח"    "spirit"]
     ["כח"     "power/strength"]
     ["חיל"    "might/army"]
     ["הר"     "mountain"]
     ["מישר"   "level/plain"]
     ["עין"    "eye"]
     ["שבעה"   "seven"]
     ["מנורה"  "menorah/lampstand"]
     ["זית"    "olive"]
     ["שמן"    "oil"]
     ["בדיל"   "tin/plummet"]
     ["תשואה"  "shout/acclamation"]
     ["זרבבל"  "Zerubbabel"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 115: THE CORNERSTONE")
  (println "  The stone the builders rejected — four passages")
  (println "  אבן = א + בן = father + son. GV=53 = garden.")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (cornerstone-vocabulary)
        s1 (station-1-rejected-stone)
        s2 (station-2-sure-foundation)
        s3 (station-3-stone-without-hands)
        s4 (station-4-capstone)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across all passages
    (println "\n  ── Combined Cornerstone Slide ──")
    (let [all-letters (str (fetch-verse-letters "Psalms" 118 22 29)
                           (fetch-verse-letters "Isaiah" 28 14 22)
                           (fetch-verse-letters "Daniel" 2 31 45)
                           (fetch-verse-letters "Zechariah" 4 1 10))
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Combined: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across all cornerstone passages:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across all cornerstone passages:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [vocab s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (cornerstone-vocabulary)
  (station-1-rejected-stone)
  (station-2-sure-foundation)
  (station-3-stone-without-hands)
  (station-4-capstone)

  ;; Quick checks
  (sefaria/fetch-chapter "Psalms" 118)
  (sefaria/fetch-chapter "Isaiah" 28)
  (sefaria/fetch-chapter "Daniel" 2)
  (sefaria/fetch-chapter "Zechariah" 4)

  ;; The stone IS father+son
  (query-word "אבן" "stone")
  (query-word "אב" "father")
  (query-word "בן" "son")

  ;; Corner = face root
  (query-word "פנה" "corner")
  (query-word "פנים" "face")

  ;; The grace shout
  (query-word "חן" "grace")
  (query-word "חסד" "lovingkindness")

  ;; Rock words
  (query-word "צור" "rock")
  (query-word "סלע" "rock/cliff")

  ;; The mountain that fills
  (query-word "הר" "mountain")
  (query-word "מלא" "fill")

  ;; The rejected and the precious
  (query-word "מאס" "reject")
  (query-word "יקר" "precious")

  nil)
