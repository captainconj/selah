(ns experiments.113-the-cross
  "Experiment 113: The Cross.

   The passion narrative walked through the breastplate oracle.
   The cross in the 4D space — understanding × love — slid through the oracle.

   Two parts:
   I.  Walk the Hebrew vocabulary of the passion through the oracle.
       Six stations follow the narrative arc.
   II. Slide the actual Torah letters on the cross beams through the oracle.
       The cross at (3,25,6,33): 67 letters horizontal (understanding),
       13 letters vertical (love), 7 letters spine (completeness).

   We already know from experiments 108-112:
   - The cross center = Lev 8:35 = ו (nail) = 'guard the charge'
   - 13 nails (vavs) on the cross = love = one
   - Crown → covenant (כתר → כרת)
   - Grave → morning (קבר → בקר)
   - Lamb lies down unanimously
   - Veil = atonement cover = menorah knob, all GV=700
   - Pierced (חלל) = 3 illuminations = throne

   The cross is not imposed on the text. It is the text.

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [selah.space.coords :as c]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-112) ──────────────────

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

;; ── Vocabulary Station Walker ─────────────────────────

(defn walk-vocabulary [station-num station-name key-words]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "╚═══════════════════════════════════════════════╝"))
  (println "\n  ── Key Words ──")
  (let [word-results (mapv (fn [[h e]] (query-word h e)) key-words)]
    {:station station-num :name station-name
     :words word-results}))

;; ── Text + Vocabulary Station Walker ──────────────────

(defn walk-text-station [station-num station-name text-label hebrew-letters key-words]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (let [gv (g/word-value hebrew-letters)]
    (println (format "\n  %s: %d letters. GV=%d." text-label (count hebrew-letters) gv))
    (println (format "  Letters: %s" (if (> (count hebrew-letters) 120)
                                       (str (subs hebrew-letters 0 120) "...")
                                       hebrew-letters)))

    (println "\n  ── Key Words ──")
    (let [word-results (mapv (fn [[h e]] (query-word h e)) key-words)]

      (println "\n  ── 3-letter sliding window ──")
      (let [hits-3 (slide-text hebrew-letters 3)
            top-3 (word-frequencies hits-3)]
        (println (format "  %d/%d windows produced readings."
                         (count hits-3) (- (count hebrew-letters) 2)))
        (doseq [{:keys [position letters top-5]} hits-3]
          (let [top (first top-5)]
            (println (format "    [%3d] %s → %s"
                             position letters
                             (:word top)))))
        (println "\n  Top 3-letter words:")
        (doseq [{:keys [word count]} (take 15 top-3)]
          (println (format "    %-8s ×%d" word count)))

        (println "\n  ── 4-letter sliding window (cherubim's view) ──")
        (let [hits-4 (slide-text hebrew-letters 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count hebrew-letters) 3)))
          (doseq [{:keys [position letters top-5]} hits-4]
            (let [top (first top-5)]
              (println (format "    [%3d] %s → %s"
                               position letters
                               (:word top)))))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word count]} (take 15 top-4)]
            (println (format "    %-8s ×%d" word count)))

          {:station station-num :name station-name
           :letter-count (count hebrew-letters) :gv gv
           :words word-results
           :hits-3 hits-3 :hits-4 hits-4
           :top-3 top-3 :top-4 top-4})))))

;; ══════════════════════════════════════════════════════
;; PART I: THE VOCABULARY OF THE PASSION
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Garden ─────────────────────────────
;; Gethsemane. The lamb chooses.
;; "Not my will but yours." (Luke 22:42)
;; "Could you not watch one hour?" (Mark 14:37)
;; שמר = guard/watch — the SAME WORD from the cross center, Lev 8:35.

(defn station-1-garden []
  (walk-vocabulary 1 "The Garden — The Lamb Chooses"
    [["גן"     "garden"]
     ["שמן"    "oil/olive"]
     ["כוס"    "cup"]
     ["רצון"   "will/desire"]
     ["שמר"    "watch/guard"]
     ["דם"     "blood"]
     ["בגד"    "betray/garment"]
     ["נשק"    "kiss/weapon"]
     ["חרב"    "sword"]
     ["אזן"    "ear"]
     ["ישן"    "sleep"]]))

;; ── Station 2: The Trial ──────────────────────────────
;; The king condemned. Crown of thorns.
;; "What is truth?" (John 18:38) — אמת is irreducible in the oracle.
;; כתר (crown) → כרת (cut covenant). Already established.
;; בגד = garment AND betray. Same word. Judas's act and the soldiers' lots.

(defn station-2-trial []
  (walk-vocabulary 2 "The Trial — The King Condemned"
    [["שפט"    "judge"]
     ["כתר"    "crown"]
     ["קוץ"    "thorn"]
     ["שוט"    "scourge/whip"]
     ["ירק"    "spit/green"]
     ["לעג"    "mock/scorn"]
     ["אסר"    "bind"]
     ["רחץ"    "wash"]
     ["נקי"    "innocent"]
     ["קנה"    "reed/acquire"]
     ["ארגמן"  "purple"]]))

;; ── Station 3: Golgotha ───────────────────────────────
;; The place of the skull. The tree. The nails.
;; חמץ = vinegar AND leaven. The sour wine IS the leaven.
;; צלע = side AND rib. The soldier pierced his side —
;;   Eve was taken from Adam's צלע. The church born from the wound.
;; מרה = gall/bitter AND the bitter waters of Exodus 15.

(defn station-3-golgotha []
  (walk-vocabulary 3 "Golgotha — The Place of the Skull"
    [["עץ"     "tree/wood"]
     ["מסמר"   "nail"]
     ["גלגלת"  "skull/Golgotha"]
     ["חמץ"    "vinegar/leaven"]
     ["חשך"    "darkness"]
     ["צמא"    "thirst"]
     ["חנית"   "spear"]
     ["צלע"    "side/rib"]
     ["עצם"    "bone"]
     ["מרה"    "gall/bitter"]
     ["גורל"   "lot/destiny"]
     ["לבוש"   "clothing"]]))

;; ── Station 4: The Cry ────────────────────────────────
;; The words from the cross.
;; "My God, my God, why have you forsaken me?" — Psalm 22:1
;; "It is finished" — כלה (consumed) or תם (complete/perfect)
;; "Father, into your hands I commit my spirit" — רוח, יד, אב
;; אבן (stone) = א+בן = Father+Son. The stone IS the relationship.
;; Also slide the letters of Psalm 22:1-2 through the oracle.

(defn station-4-cry []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  Station 4: The Cry — The Words from the Cross  ║")
  (println "╚═══════════════════════════════════════════════╝")

  (println "\n  ── Key Words ──")
  (let [word-results (mapv (fn [[h e]] (query-word h e))
                       [["אלי"    "my God"]
                        ["למה"    "why"]
                        ["עזב"    "forsake"]
                        ["כלה"    "finish/consume"]
                        ["תם"     "complete/perfect"]
                        ["רוח"    "spirit/breath"]
                        ["סלח"    "forgive"]
                        ["גנב"    "thief"]
                        ["עדן"    "Eden/paradise"]
                        ["אב"     "father"]
                        ["בן"     "son"]
                        ["יד"     "hand"]
                        ["אבן"    "stone"]
                        ["פקד"    "entrust/appoint"]])

        ;; Slide Psalm 22:1-2 through the oracle
        _ (println "\n  ── Psalm 22:1-2 — 'My God, my God, why have you forsaken me?' ──")
        ps22-letters (fetch-verse-letters "Psalms" 22 1 2)
        ps22-gv (g/word-value ps22-letters)]

    (println (format "\n  Psalm 22:1-2: %d letters. GV=%d." (count ps22-letters) ps22-gv))
    (println (format "  Letters: %s" ps22-letters))

    (println "\n  ── 3-letter sliding window on Psalm 22:1-2 ──")
    (let [hits-3 (slide-text ps22-letters 3)
          top-3 (word-frequencies hits-3)]
      (println (format "  %d/%d windows produced readings."
                       (count hits-3) (- (count ps22-letters) 2)))
      (doseq [{:keys [position letters top-5]} hits-3]
        (let [top (first top-5)]
          (println (format "    [%3d] %s → %s"
                           position letters
                           (:word top)))))
      (println "\n  Top 3-letter words:")
      (doseq [{:keys [word count]} (take 15 top-3)]
        (println (format "    %-8s ×%d" word count)))

      (println "\n  ── 4-letter sliding window on Psalm 22:1-2 ──")
      (let [hits-4 (slide-text ps22-letters 4)
            top-4 (word-frequencies hits-4)]
        (println (format "  %d/%d windows produced readings."
                         (count hits-4) (- (count ps22-letters) 3)))
        (doseq [{:keys [position letters top-5]} hits-4]
          (let [top (first top-5)]
            (println (format "    [%3d] %s → %s"
                             position letters
                             (:word top)))))
        (println "\n  Top 4-letter words:")
        (doseq [{:keys [word count]} (take 15 top-4)]
          (println (format "    %-8s ×%d" word count)))

        {:station 4 :name "The Cry"
         :words word-results
         :ps22-letters ps22-letters :ps22-gv ps22-gv
         :ps22-hits-3 hits-3 :ps22-hits-4 hits-4
         :ps22-top-3 top-3 :ps22-top-4 top-4}))))

;; ── Station 5: The Tearing ────────────────────────────
;; The veil rends. The earth shakes. The tombs open.
;; פרכת (veil) GV=700 = כפרת (atonement cover) = כפתר (menorah knob).
;; קרע (tear) — the right cherub reads the firmament (רקיע) as יקרע (it will tear).
;; קבר (grave) basin → בקר (morning). Already found in experiment 112.

(defn station-5-tearing []
  (walk-vocabulary 5 "The Tearing — The Veil Rends"
    [["פרכת"   "veil"]
     ["קרע"    "tear/rend"]
     ["רעש"    "earthquake"]
     ["סלע"    "rock"]
     ["קבר"    "tomb/grave"]
     ["פתח"    "open/door"]
     ["אבן"    "stone"]
     ["בשם"    "spice/fragrance"]
     ["חותם"   "seal"]
     ["שלש"    "three"]
     ["יום"    "day"]
     ["סדין"   "linen"]]))

;; ── Station 6: The Rising ─────────────────────────────
;; The stone rolls away. The morning comes.
;; Father+Son = אבן (stone). The stone IS the relationship.
;; Door → birth (דלת → תלד). To enter the tomb is to be born out of it.
;; Grave → morning (קבר → בקר). Every burial walks to dawn.

(defn station-6-rising []
  (walk-vocabulary 6 "The Rising — Morning"
    [["קום"    "rise/stand"]
     ["בקר"    "morning"]
     ["חי"     "alive/living"]
     ["בכור"   "firstborn"]
     ["מלאך"   "angel/messenger"]
     ["גלל"    "roll"]
     ["דלת"    "door"]
     ["שלום"   "peace"]
     ["ראשית"  "beginning/first"]
     ["חדש"    "new"]
     ["ירא"    "fear/awe"]]))

;; ══════════════════════════════════════════════════════
;; PART II: THE CROSS IN THE SPACE
;; ══════════════════════════════════════════════════════

(defn beam-letters
  "Extract letters from a beam in the 4D space."
  [positions]
  (let [s (c/space)]
    (apply str (mapv #(c/letter-at s %) positions))))

(defn walk-beam [beam-name positions]
  (let [letters (beam-letters positions)
        gv (g/word-value letters)]
    (println (format "\n  ── %s: %d letters, GV=%d ──" beam-name (count letters) gv))
    (println (format "  Letters: %s" letters))

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

      (when (>= (count (beam-letters positions)) 4)
        (println "\n  ── 4-letter sliding window (cherubim's view) ──")
        (let [hits-4 (slide-text (beam-letters positions) 4)
              top-4 (word-frequencies hits-4)]
          (println (format "  %d/%d windows produced readings."
                           (count hits-4) (- (count (beam-letters positions)) 3)))
          (doseq [{:keys [position letters top-5]} hits-4]
            (let [top (first top-5)]
              (println (format "    [%3d] %s → %s"
                               position letters
                               (:word top)))))
          (println "\n  Top 4-letter words:")
          (doseq [{:keys [word count]} (take 15 top-4)]
            (println (format "    %-8s ×%d" word count)))
          {:hits-4 hits-4 :top-4 top-4}))

      {:beam beam-name :letter-count (count letters) :gv gv
       :hits-3 hits-3 :top-3 top-3})))

(defn station-7-cross-in-space []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  Station 7: The Cross in the 4D Space           ║")
  (println "║  Center: (3,25,6,33) = Lev 8:35 = ו (nail)     ║")
  (println "║  Horizontal: 67 letters (understanding)         ║")
  (println "║  Vertical: 13 letters (love)                    ║")
  (println "║  Spine: 7 letters (completeness)                ║")
  (println "╚═══════════════════════════════════════════════╝")

  ;; Ensure space is loaded
  (c/space)

  (let [h-pos (for [d (range 67)] (c/coord->idx 3 25 6 d))
        v-pos (for [cc (range 13)] (c/coord->idx 3 25 cc 33))
        s-pos (for [a (range 7)] (c/coord->idx a 25 6 33))
        m-pos (for [d (range 67)] (c/coord->idx 3 25 7 d))]

    ;; Print beam details
    (println "\n  ── Beam Letters ──")
    (let [s (c/space)]
      (println "\n  Horizontal (d=0..66, understanding):")
      (doseq [d (range 67)]
        (let [p (c/coord->idx 3 25 6 d)
              desc (c/describe p)]
          (print (format "%s" (:letter desc)))))
      (println)

      (println "\n  Vertical (c=0..12, love):")
      (doseq [cc (range 13)]
        (let [p (c/coord->idx 3 25 cc 33)
              desc (c/describe p)]
          (println (format "    c=%2d  %s (GV=%d)  %s"
                           cc (:letter desc) (:gematria desc) (:verse desc)))))

      (println "\n  Spine (a=0..6, completeness):")
      (doseq [a (range 7)]
        (let [p (c/coord->idx a 25 6 33)
              desc (c/describe p)]
          (println (format "    a=%d  %s (GV=%d)  %s"
                           a (:letter desc) (:gematria desc) (:verse desc)))))

      ;; Nail count
      (println "\n  ── Nails (ו) on the cross ──")
      (let [nails (->> (distinct (concat h-pos v-pos))
                       (filter #(= \ו (c/letter-at s %))))]
        (println (format "  %d nails. 13 = love = one." (count nails)))))

    ;; Slide each beam
    (let [h-result (walk-beam "Horizontal Beam (67, understanding)" h-pos)
          v-result (walk-beam "Vertical Beam (13, love)" v-pos)
          s-result (walk-beam "Spine (7, completeness)" s-pos)]

      (println "\n  ── Mercy Seat Line (c=7, one layer above the cross) ──")
      (let [m-result (walk-beam "Mercy Seat Line (67, grace overhead)" m-pos)]

        {:horizontal h-result
         :vertical v-result
         :spine s-result
         :mercy-seat m-result}))))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 113: THE CROSS")
  (println "  Walking the passion through the breastplate")
  (println "  6 vocabulary stations + the cross in the space")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-garden)
        s2 (station-2-trial)
        s3 (station-3-golgotha)
        s4 (station-4-cry)
        s5 (station-5-tearing)
        s6 (station-6-rising)
        s7 (station-7-cross-in-space)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (println "\n  ── Vocabulary Stations ──")
    (doseq [s [s1 s2 s3 s5 s6]]
      (println (format "  Station %d (%s): %d words queried"
                       (:station s) (:name s) (count (:words s)))))
    (println (format "  Station 4 (The Cry): %d words + Psalm 22:1-2 (%d letters, GV=%d)"
                     (count (:words s4))
                     (count (:ps22-letters s4))
                     (:ps22-gv s4)))

    (println "\n  ── Cross Beams ──")
    (doseq [[k label] [[:horizontal "Horizontal (67)"]
                        [:vertical "Vertical (13)"]
                        [:spine "Spine (7)"]
                        [:mercy-seat "Mercy Seat (67)"]]]
      (let [b (get s7 k)]
        (println (format "  %s: %d letters, GV=%d, %d 3-letter readings"
                         label (:letter-count b) (:gv b) (count (:hits-3 b))))))

    [s1 s2 s3 s4 s5 s6 s7]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-garden)
  (station-2-trial)
  (station-3-golgotha)
  (station-4-cry)
  (station-5-tearing)
  (station-6-rising)
  (station-7-cross-in-space)

  ;; Quick checks
  (sefaria/fetch-chapter "Psalms" 22)

  ;; The double-meaning words
  (query-word "בגד" "betray/garment")    ;; betrayal = garment
  (query-word "חמץ" "vinegar/leaven")    ;; sour wine = leaven
  (query-word "נשק" "kiss/weapon")       ;; kiss = weapon
  (query-word "צלע" "side/rib")          ;; pierced side = Eve's rib
  (query-word "מרה" "gall/bitter")       ;; gall = Marah
  (query-word "אבן" "stone")            ;; father+son

  ;; From experiment 112 (verify cross-references)
  (query-word "כבש" "lamb")
  (query-word "קבר" "grave")             ;; basin→morning
  (query-word "חלל" "pierced")           ;; 3 illuminations = throne

  ;; The cross center
  (query-word "שמר" "guard/watch")       ;; Lev 8:35 = cross center
  (query-word "פרכת" "veil")             ;; GV=700

  ;; Key basin walks
  (basin/walk "כתר")                     ;; crown → covenant
  (basin/walk "דלת")                     ;; door → birth
  (basin/walk "קבר")                     ;; grave → morning

  ;; The stone = father + son
  (g/word-value "אבן")                   ;; 53 = GV of garden (גן=53)
  (g/word-value "אב")                    ;; 3
  (g/word-value "בן")                    ;; 52 — but wait, actually 50+2=52

  ;; Load the 4D space for beam analysis
  (c/space)
  nil)
