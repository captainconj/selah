(ns experiments.131-the-wall
  "Experiment 131: Nehemiah 3 — The Wall.

   Nehemiah rebuilds Jerusalem's wall after the exile.
   Chapter 3 lists every builder, every family, every gate.
   38 verses. ~40 builder groups. A circuit from Sheep Gate back to Sheep Gate.

   The wall is the breastplate.

   The dominant sliding-window word across the entire construction section
   (vv 1-32, 1,951 letters) is חזה — which means both 'vision' AND 'breastplate.'
   It appears ×35. The 4-letter form יחזה ('he will see') appears ×34.
   The form וחזה ('and breastplate') carries GV=26 = YHWH and appears ×19.

   The repair verb החזיק shares its root letters with the breastplate word.
   The builders are repairing the vision. Section by section. Family by family.

   Ten gates. The circuit starts and ends at the Sheep Gate.
   Sheep (צאן) = 72 readings = the breastplate stamps itself on its own entry.

   What the oracle found:
   - Wall (חומה) → wipe away (ומחה). The wall walks to erasure.
   - Gate (שער) = fixed point. God reads wicked (רשע ×16). Same as cornerstone.
   - Horses (סוסים) = 0/0. The Horse Gate is invisible. Power is dark.
   - District (פלך) = 0/0. Jurisdiction is invisible.
   - Build (בנה) GV=57 = altar = fish = fixed point. All four readers agree.
   - Door (דלת) → she will give birth (תלד). Always.
   - Tower (מגדל) = 72 readings. Breastplate stamps the tower.
   - Grave (קבר) → morning (בקר). God reads lightning (ברק ×20).
   - Furnace (תנור) → remainder (נותר). What survives the fire.
   - Serve (עבד) → on behalf of (בעד). Service IS intercession (same as Isa 53).
   - Bar/bolt (בריח) → my chosen (בחרי). God reads 'he will choose' (יבחר).
   - Strengthen (חזק) = 2 illuminations. The labor is nearly invisible.
   - Priest (כהן) = 72 readings. Fixed point.
   - The son (הבן) GV=57 → thumb (בהן). The priestly thumb anointed in Lev 8.
   - Eliashib = 17,820 illuminations / 71,280 readings = 990 × 72.
   - Baruch (blessed) = 0/0. The blessed builder is invisible.
   - Jeshua (salvation) = 924 illuminations. Blazing.
   - Daughters (בנות) = fixed point. Only God and mercy read them."
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

(defn walk-station [station-num station-name v-start v-end key-words]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "║  Nehemiah 3:%d-%d %s ║"
                   v-start v-end
                   (apply str (repeat (- 35 (count (str "Nehemiah 3:" v-start "-" v-end))) " "))))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [letters (fetch-verse-letters "Nehemiah" 3 v-start v-end)
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
        (println "\n  Top 3-letter words:")
        (doseq [{:keys [word count]} (take 15 top-3)]
          (println (format "    %-8s ×%d" word count)))

        (println "\n  ── 4-letter sliding window ──")
        (let [hits-4 (slide-text letters 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count letters) 3)))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word count]} (take 15 top-4)]
            (println (format "    %-8s ×%d" word count)))

          {:station station-num :name station-name
           :letter-count (count letters) :gv gv
           :words word-results
           :hits-3 hits-3 :hits-4 hits-4
           :top-3 top-3 :top-4 top-4})))))

;; ══════════════════════════════════════════════════════
;; THE TEN STATIONS OF THE WALL
;; ══════════════════════════════════════════════════════
;;
;; Nehemiah 3 lists builders gate by gate, circling Jerusalem
;; from the Sheep Gate clockwise back to the Sheep Gate.
;;
;; Station 1: Sheep Gate (vv 1-2)     — the priests begin
;; Station 2: Fish Gate (vv 3-5)      — the Tekoite nobles refuse
;; Station 3: Old Gate (vv 6-12)      — Shallum's daughters build
;; Station 4: Valley Gate (vv 13)     — 1000 cubits to the Dung Gate
;; Station 5: Dung Gate (vv 14)       — Malchijah ben Rechab
;; Station 6: Fountain Gate (vv 15-25)— Ezer ben Yeshua, Baruch burns
;; Station 7: Water Gate (vv 26-27)   — Nethinim in Ophel
;; Station 8: Horse Gate (vv 28-31)   — priests, each facing his house
;; Station 9: Corner → Sheep (v 32)   — goldsmiths and merchants close the circuit
;; Station 10: Opposition + Prayer (vv 33-38) — Sanballat mocks. Nehemiah prays.

;; ── Station 1: The Sheep Gate ──────────────────
;; Eliashib the high priest and the priests build.
;; They sanctify it. They build to the Tower of the Hundred
;; and the Tower of Hananel. Men of Jericho. Zaccur ben Imri.

(defn station-1-sheep-gate []
  (walk-station 1 "The Sheep Gate" 1 2
    [["צאן"     "sheep/flock"]
     ["כהן"     "priest"]
     ["גדול"    "great"]
     ["קדש"     "sanctify"]
     ["מגדל"    "tower"]
     ["מאה"     "hundred"]
     ["חנן"     "Hananel/gracious"]
     ["ירחו"    "Jericho"]
     ["זכר"     "Zaccur/remember"]]))

;; ── Station 2: The Fish Gate ────────────────────
;; Sons of Hassenaah build the Fish Gate.
;; Meremoth ben Uriah ben Hakkoz repairs next.
;; Meshullam ben Berechiah ben Meshezabel repairs.
;; Zadok ben Baana repairs.
;; The Tekoites repair — but their nobles refuse to bow their necks.

(defn station-2-fish-gate []
  (walk-station 2 "The Fish Gate" 3 5
    [["דגים"    "fish"]
     ["אור"     "Uriah/light"]
     ["ברכה"    "Berechiah/blessing"]
     ["צדוק"    "Zadok/righteous"]
     ["תקוע"    "Tekoa"]
     ["אדיר"    "noble/mighty"]
     ["צואר"    "neck"]
     ["עבד"     "serve/work"]]))

;; ── Station 3: The Old Gate ─────────────────────
;; Joiada and Meshullam repair the Old Gate.
;; Melatiah the Gibeonite, Jadon the Meronothite.
;; Uzziel the goldsmith, Hananiah the perfumer.
;; Rephaiah ben Hur — ruler of half Jerusalem.
;; Jedaiah repairs opposite his house.
;; Malchijah and Hasshub — Tower of the Furnaces.
;; Shallum and his daughters repair.

(defn station-3-old-gate []
  (walk-station 3 "The Old Gate" 6 12
    [["ישנה"    "old/Jeshanah"]
     ["גבעון"   "Gibeon/hill"]
     ["צרף"     "goldsmith/refiner"]
     ["רקח"     "perfumer"]
     ["רפא"     "Rephaiah/heal"]
     ["תנור"    "furnace/oven"]
     ["שלום"    "Shallum/peace"]
     ["בנות"    "daughters"]
     ["פלך"     "district/spindle"]]))

;; ── Station 4: The Valley Gate ──────────────────
;; Hanun and the inhabitants of Zanoah repair.
;; They build it, set up its doors, bolts, and bars —
;; and 1000 cubits of wall to the Dung Gate.

(defn station-4-valley-gate []
  (walk-station 4 "The Valley Gate" 13 13
    [["גיא"     "valley"]
     ["חנון"    "Hanun/gracious"]
     ["זנח"     "Zanoah/rejected"]
     ["דלת"     "door"]
     ["מנעל"    "bolt/lock"]
     ["בריח"    "bar"]
     ["אלף"     "thousand"]
     ["חומה"    "wall"]]))

;; ── Station 5: The Dung Gate ────────────────────
;; Malchijah ben Rechab, ruler of Beth-hakkerem.
;; He builds it, sets up doors, bolts, and bars.

(defn station-5-dung-gate []
  (walk-station 5 "The Dung Gate" 14 14
    [["אשפה"    "refuse/ash-heap"]
     ["מלך"     "king (root of Malchijah)"]
     ["רכב"     "Rechab/chariot"]
     ["כרם"     "vineyard"]
     ["דלת"     "door"]
     ["מנעל"    "bolt"]
     ["בריח"    "bar"]]))

;; ── Station 6: The Fountain Gate ────────────────
;; Shallun ben Col-hozeh — ruler of Mizpah.
;; He builds and roofs it: doors, bolts, bars.
;; Wall of the Pool of Siloam, by the king's garden,
;; to the stairs going down from the City of David.
;; Nehemiah ben Azbuk. The Levites. Bavvai ben Henadad.
;; Ezer ben YESHUA — ruler of Mizpah — repairs opposite the armory.
;; Baruch ben Zabbai "burned with zeal" — a second section.
;; Meremoth repairs a second section. Priests of the plain.
;; Benjamin and Hasshub opposite their house.
;; Azariah. Binnui. Palal ben Uzai — opposite the corner
;; and the tower projecting from the upper palace.

(defn station-6-fountain-gate []
  (walk-station 6 "The Fountain Gate" 15 25
    [["עין"     "fountain/eye"]
     ["שלח"     "Siloam/sent"]
     ["גן"      "garden"]
     ["מלך"     "king"]
     ["מעלה"    "stairs/ascent"]
     ["דוד"     "David"]
     ["נחמיה"   "Nehemiah"]
     ["לוי"     "Levite"]
     ["ישוע"    "Yeshua/salvation"]
     ["נשק"     "armory/weapons"]
     ["חרה"     "burned/zealous"]
     ["ברוך"    "Baruch/blessed"]
     ["בנימן"   "Benjamin"]
     ["קבר"     "grave/tomb"]]))

;; ── Station 7: The Water Gate / Ophel ───────────
;; The Nethinim (temple servants) live in the Ophel.
;; They repair to a point opposite the Water Gate on the east
;; and the projecting tower.
;; The Tekoites repair a second section.

(defn station-7-water-gate []
  (walk-station 7 "The Water Gate / Ophel" 26 27
    [["נתין"    "Nethinim/given"]
     ["עפל"     "Ophel/hill"]
     ["מים"     "water"]
     ["מזרח"    "east/sunrise"]
     ["תקוע"    "Tekoa"]]))

;; ── Station 8: The Horse Gate ───────────────────
;; Above the Horse Gate: the priests repair,
;; each one opposite his own house.
;; Zadok ben Immer. Shemaiah the keeper of the East Gate.
;; Hananiah ben Shelemiah. Hanun ben Zalaph.
;; Malchijah the goldsmith — to the house of the Nethinim
;; and the merchants, opposite the Muster Gate.

(defn station-8-horse-gate []
  (walk-station 8 "The Horse Gate" 28 31
    [["סוס"     "horse"]
     ["כהן"     "priest"]
     ["בית"     "house"]
     ["צדוק"    "Zadok/righteous"]
     ["שמר"     "keeper/guard"]
     ["חנן"     "Hananiah/gracious"]
     ["צרף"     "goldsmith/refiner"]
     ["נתין"    "Nethinim/given"]
     ["רכל"     "merchant/trader"]
     ["מפקד"    "muster/appointed"]]))

;; ── Station 9: The Corner → Sheep Gate ──────────
;; Between the upper room of the corner and the Sheep Gate,
;; the goldsmiths and the merchants repair.
;; The circuit closes.

(defn station-9-corner []
  (walk-station 9 "Corner → Sheep Gate" 32 32
    [["פנה"     "corner"]
     ["צאן"     "sheep"]
     ["צרף"     "goldsmith"]
     ["רכל"     "merchant"]
     ["עליה"    "upper room"]]))

;; ── Station 10: The Opposition and the Prayer ───
;; Sanballat hears and is furious.
;; "What are these feeble Jews doing? Will they revive the stones?"
;; Tobiah: "Even a fox could break their wall."
;; Nehemiah prays: "Hear, O God, for we are despised."
;; "We built the wall... for the people had a mind to work."

(defn station-10-opposition []
  (walk-station 10 "Opposition and Prayer" 33 38
    [["חרה"     "anger/burn"]
     ["יהוד"    "Judah/Jew"]
     ["אבן"     "stone"]
     ["שועל"    "fox"]
     ["פרץ"     "break through"]
     ["חומה"    "wall"]
     ["בוזה"    "despised"]
     ["חרפה"    "reproach"]
     ["לב"      "heart"]
     ["עשה"     "do/make"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 131: NEHEMIAH 3 — THE WALL")
  (println "  Every builder. Every gate. The circuit.")
  (println "  38 verses · 10 stations · 2320 letters")
  (println "  The wall is the breastplate.")
  (println "════════════════════════════════════════════════")

  (let [stations [(station-1-sheep-gate)
                  (station-2-fish-gate)
                  (station-3-old-gate)
                  (station-4-valley-gate)
                  (station-5-dung-gate)
                  (station-6-fountain-gate)
                  (station-7-water-gate)
                  (station-8-horse-gate)
                  (station-9-corner)
                  (station-10-opposition)]]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s stations]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Full construction slide (vv 1-32)
    (println "\n  ── Full Construction (vv 1-32) ──")
    (let [all-letters (fetch-verse-letters "Nehemiah" 3 1 32)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Construction section: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) — the construction:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (let [gv (g/word-value word)]
          (println (format "    %-8s GV=%-4d ×%d" word gv count))))
      (println "\n  Top 25 words (4-letter) — the construction:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (let [gv (g/word-value word)]
          (println (format "    %-8s GV=%-4d ×%d" word gv count)))))

    ;; Full chapter slide (vv 1-38)
    (println "\n  ── Full Chapter (vv 1-38) ──")
    (let [all-letters (fetch-verse-letters "Nehemiah" 3 1 38)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full chapter: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) — full chapter:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (let [gv (g/word-value word)]
          (println (format "    %-8s GV=%-4d ×%d" word gv count))))
      (println "\n  Top 25 words (4-letter) — full chapter:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (let [gv (g/word-value word)]
          (println (format "    %-8s GV=%-4d ×%d" word gv count)))))

    stations))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-sheep-gate)
  (station-2-fish-gate)
  (station-3-old-gate)
  (station-4-valley-gate)
  (station-5-dung-gate)
  (station-6-fountain-gate)
  (station-7-water-gate)
  (station-8-horse-gate)
  (station-9-corner)
  (station-10-opposition)

  ;; ── The gates ──
  (query-word "שער" "gate")
  (query-word "צאן" "sheep")
  (query-word "דגים" "fish")
  (query-word "ישנה" "old")
  (query-word "גיא" "valley")
  (query-word "אשפת" "refuse/dung")
  (query-word "עין" "fountain/eye")
  (query-word "מים" "water")
  (query-word "סוסים" "horses")
  (query-word "פנה" "corner")

  ;; ── The wall vocabulary ──
  (query-word "חומה" "wall")
  (query-word "חזק" "strengthen/repair")
  (query-word "בנה" "build")
  (query-word "דלת" "door")
  (query-word "מנעל" "bolt/lock")
  (query-word "בריח" "bar")
  (query-word "מגדל" "tower")
  (query-word "חזה" "vision/breastplate")

  ;; ── The dominant finding ──
  ;; חזה = vision AND breastplate. GV=20. 3 illum = throne. Fixed point.
  ;; Appears ×35 in the construction section — most frequent word.
  ;; וחזה GV=26 = YHWH. Appears ×19.
  ;; The wall IS the breastplate.
  (query-word "חזה" "vision/breastplate")
  (query-word "וחזה" "and-vision/and-breastplate")
  (query-word "יחזה" "he-will-see")

  ;; ── Ezer ben Yeshua (v19) ──
  ;; Help, son of Salvation. Repairs opposite the armory.
  (query-word "עזר" "help")
  (query-word "ישוע" "Yeshua/salvation")

  ;; ── Eliashib (v1) ──
  ;; 71,280 readings = 990 × 72. The high priest IS the breastplate.
  (query-word "אלישיב" "Eliashib/God-restores")

  ;; ── The invisible ──
  ;; Baruch (blessed) = 0/0
  ;; Horses = 0/0
  ;; District (פלך) = 0/0
  ;; Strengthen (חזק) = 2 illuminations
  (query-word "ברוך" "blessed")
  (query-word "סוסים" "horses")
  (query-word "פלך" "district")
  (query-word "חזק" "strengthen")

  nil)
