(ns experiments.140-grid-variants
  "Experiment 140: Grid Variant Test.

   The parameterized oracle. Takes any 72-letter grid arrangement
   and runs the full battery: Ramban pairs, lamb split, reader
   exclusives, ghost zone, mercy concentration.

   Settles the architecture question: which arrangement produces
   the strongest courtroom?

   Variants:
   A. Birth order continuous (current)
   B. Birth order, one tribe per stone
   C. Sotah 36a mother-grouped
   D. Grid orientation flip (stone 1 = top-right)

   Note: Encampment order (Ephraim+Manasseh replacing Joseph+Levi)
   produces 74 letters, not 72. Excluded unless we find a tradition
   that resolves the 2-letter surplus."
  (:require [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.set :as set]))

;; ══════════════════════════════════════════════════════
;; THE PARAMETERIZED ORACLE
;; ══════════════════════════════════════════════════════

;; A grid is: [[stone-num letters row col] ...]
;; Same shape as selah.oracle/stone-data.

(defn build-grid
  "From stone-data, build the lookup structures the oracle needs."
  [stone-data]
  (let [stone-letters (into {} (map (fn [[s l _ _]] [s (vec l)]) stone-data))
        stone-row     (into {} (map (fn [[s _ r _]] [s r]) stone-data))
        stone-col     (into {} (map (fn [[s _ _ c]] [s c]) stone-data))
        letter-index  (reduce (fn [m [s letters _ _]]
                                (reduce-kv (fn [m2 i ch]
                                             (update m2 ch (fnil conj []) [s i]))
                                           m (vec letters)))
                              {} stone-data)
        ;; Final-form unioning
        final->base {\ך \כ, \ם \מ, \ן \נ, \ף \פ, \ץ \צ}
        resolve     (fn [ch]
                      (or (get letter-index ch)
                          (when-let [base (final->base ch)]
                            (get letter-index base))))
        letter-at   (fn [[s i]] ((stone-letters s) i))
        normalize   (fn [s] (apply str (map #(get final->base % %) s)))]
    ;; Precompute normalized Torah word index for fast dictionary lookup
    (let [torah-set    (dict/torah-words)
          norm-index   (reduce (fn [m w]
                                 (update m (apply str (map #(get final->base % %) w))
                                         (fnil conj #{}) w))
                               {} torah-set)
          norm-known?  (fn [s] (contains? norm-index
                                          (apply str (map #(get final->base % %) s))))
          norm-word    (fn [s]
                         (let [ns (apply str (map #(get final->base % %) s))
                               matches (get norm-index ns)]
                           (cond
                             (nil? matches) s
                             (contains? matches s) s
                             :else (first matches))))]
      {:stone-data    stone-data
       :stone-letters stone-letters
       :stone-row     stone-row
       :stone-col     stone-col
       :letter-index  letter-index
       :resolve       resolve
       :letter-at     letter-at
       :normalize     normalize
       :final->base   final->base
       :torah-set     torah-set
       :norm-index    norm-index
       :norm-known?   norm-known?
       :norm-word     norm-word})))

;; ── Reader traversals ────────────────────────────────

(defn read-key
  "Sort key for a position under a given reader."
  [grid reader [s i]]
  (let [r ((:stone-row grid) s)
        c ((:stone-col grid) s)]
    (case reader
      :aaron [r (- c) i]
      :god   [(- r) c i]
      :truth [(- c) r i]
      :mercy  [c (- r) i])))

(defn read-positions
  "Read a set of positions in the order a given reader would see them."
  [grid reader positions]
  (->> positions
       (sort-by #(read-key grid reader %))
       (map (:letter-at grid))
       (apply str)))

;; ── Illumination ─────────────────────────────────────

(defn illumination-sets
  "All distinct position-sets for the given word on this grid."
  [grid word]
  (let [chars  (vec word)
        n      (count chars)
        cands  (mapv (:resolve grid) chars)
        seen   (atom #{})
        result (atom [])]
    (when (every? seq cands)
      (letfn [(go [i chosen used]
                (if (= i n)
                  (let [pset (set chosen)]
                    (when-not (@seen pset)
                      (swap! seen conj pset)
                      (swap! result conj pset)))
                  (doseq [pos (cands i)]
                    (when-not (used pos)
                      (go (inc i) (conj chosen pos) (conj used pos))))))]
        (go 0 [] #{})))
    @result))

;; ── Fast dominant reading (for basin walks) ──────────

(defn dominant-reading
  "The most frequent Torah word produced by this word on this grid.
   Returns the dominant word or nil if dead end. Fast path for basins."
  [grid word]
  (let [ilsets (illumination-sets grid word)]
    (when (seq ilsets)
      (let [norm (:normalize grid)
            norm-known? (:norm-known? grid)
            norm-word (:norm-word grid)
            readings (for [pset ilsets
                          reader [:aaron :god :truth :mercy]]
                       (let [raw (read-positions grid reader pset)]
                         (when (norm-known? raw)
                           (norm-word raw))))
            valid (remove nil? readings)]
        (when (seq valid)
          (key (apply max-key val (frequencies valid))))))))

;; ── Ask (reverse query) ──────────────────────────────

(defn ask
  "Run a word through this grid. Returns illuminations and per-reader counts."
  [grid word]
  (let [norm      (:normalize grid)
        ilsets    (illumination-sets grid word)
        nw        (norm word)
        hits      (for [pset ilsets
                        reader [:aaron :god :truth :mercy]
                        :when (= nw (norm (read-positions grid reader pset)))]
                    reader)
        by-reader (frequencies hits)]
    {:word word
     :gv (g/word-value word)
     :illuminations (count ilsets)
     :total-readings (count hits)
     :aaron (get by-reader :aaron 0)
     :god   (get by-reader :god 0)
     :truth (get by-reader :truth 0)
     :mercy  (get by-reader :mercy 0)}))

;; ══════════════════════════════════════════════════════
;; THE GRID VARIANTS
;; ══════════════════════════════════════════════════════

;; ── Helper: build stone-data from a continuous string ──

(defn string->stones
  "Convert a 72-char string into stone-data (4 rows × 3 cols, 6 per stone)."
  [s]
  (assert (= 72 (count s)) (str "Expected 72 chars, got " (count s)))
  (vec (map-indexed
         (fn [i letters]
           [(inc i)
            (apply str letters)
            (inc (quot i 3))   ;; row: 1-4
            (inc (mod i 3))])  ;; col: 1-3
         (partition 6 s))))

;; ── Variant A: Birth Order Continuous (CURRENT) ──────

(def variant-a
  "Birth order, continuous flow, patriarchs prefix."
  (string->stones
    (str "אברהם"    ;; Abraham (5)
         "יצחק"     ;; Isaac (4)
         "יעקב"     ;; Jacob (4)
         "ראובן"    ;; Reuben (5) — actually 6 with full spelling
         "שמעון"    ;; Simeon (5)
         "לוי"      ;; Levi (3)
         "יהודה"    ;; Judah (5)
         "דן"       ;; Dan (2)
         "נפתלי"    ;; Naphtali (5)
         "גד"       ;; Gad (2)
         "אשר"      ;; Asher (3)
         "יששכר"    ;; Issachar (5)
         "זבולן"    ;; Zebulun (5)
         "יוסף"     ;; Joseph (4)
         "בנימין"   ;; Benjamin (6)
         "שבטי"     ;; שבטי (4)
         "ישרון"))) ;; ישרון (5)
;; Total: 5+4+4+5+5+3+5+2+5+2+3+5+5+4+6+4+5 = 72 ✓... need to verify

;; Actually, let me use the EXACT current stone-data to be safe.
(def variant-a-exact
  "The exact current oracle grid."
  [[1  "אברהםי"  1 1]   [2  "צחקיעק"  1 2]   [3  "בראובן"  1 3]
   [4  "שמעוןל"  2 1]   [5  "וייהוד"  2 2]   [6  "הדןנפת"  2 3]
   [7  "ליגדאש"  3 1]   [8  "ריששכר"  3 2]   [9  "זבולןי"  3 3]
   [10 "וסףבני"  4 1]   [11 "מיןשבט"  4 2]   [12 "יישרון"  4 3]])

;; ── Variant B: Birth Order, One Tribe Per Stone ──────
;;
;; Each stone carries one tribal name. Patriarch letters fill
;; short names to reach 6 per stone. שבטי ישרון fills the end.
;;
;; Distribution: patriarch letters go BEFORE each tribal name
;; on its stone, filling the gap.
;;
;; Reuben(6)+Simeon(5)+Levi(3)+Judah(5)+Dan(2)+Naphtali(5)
;; +Gad(2)+Asher(3)+Issachar(5)+Zebulun(5)+Joseph(4)+Benjamin(6) = 51
;; Wait — ראובן is 5 not 6. Let me recount.
;; ראובן(5) שמעון(5) לוי(3) יהודה(5) דן(2) נפתלי(5)
;; גד(2) אשר(3) יששכר(5) זבולן(5) יוסף(4) בנימין(6) = 50
;; Patriarchs: אברהם(5) יצחק(4) יעקב(4) = 13
;; שבטי ישרון = 9
;; 50 + 13 + 9 = 72 ✓
;;
;; Filler needed per stone (6 - tribal name length):
;; Reuben: 1, Simeon: 1, Levi: 3, Judah: 1, Dan: 4, Naphtali: 1
;; Gad: 4, Asher: 3, Issachar: 1, Zebulun: 1, Joseph: 2, Benjamin: 0
;; Total filler: 1+1+3+1+4+1+4+3+1+1+2+0 = 22 = 13+9 ✓
;;
;; Distribute patriarchs first (13 letters), then שבטי ישרון (9):
;; Stone 1 (Reuben, needs 1):    א + ראובן
;; Stone 2 (Simeon, needs 1):    ב + שמעון
;; Stone 3 (Levi, needs 3):      רהם + לוי
;; Stone 4 (Judah, needs 1):     י + יהודה
;; Stone 5 (Dan, needs 4):       צחקי + דן  (wait, that's weird — Isaac's letters on Dan's stone?)
;; Actually, let me try: fill sequentially from patriarch string.
;;
;; Patriarch string: אברהםיצחקיעקב (13 chars)
;; Consume 1 for Reuben's stone: א | remaining: ברהםיצחקיעקב (12)
;; Consume 1 for Simeon's stone: ב | remaining: רהםיצחקיעקב (11)
;; Consume 3 for Levi's stone: רהם | remaining: יצחקיעקב (8)
;; Consume 1 for Judah's stone: י | remaining: צחקיעקב (7)
;; Consume 4 for Dan's stone: צחקי | remaining: עקב (3)
;; Consume 1 for Naphtali's stone: ע | remaining: קב (2)
;; Consume 2 for Gad's stone (needs 4): קב | remaining: 0, need 2 more from שבטי ישרון
;; שבטי ישרון string: שבטיישרון (9 chars)
;; Consume 2 for Gad's stone: שב | remaining: טיישרון (7)
;; Consume 3 for Asher's stone: טיי | remaining: שרון (4)
;; Consume 1 for Issachar's stone: ש | remaining: רון (3)
;; Consume 1 for Zebulun's stone: ר | remaining: ון (2)
;; Consume 2 for Joseph's stone: ון | remaining: 0
;; Benjamin needs 0. Done.

(def variant-b
  "Birth order, one tribe per stone. Patriarch + שבטי ישרון as sequential filler."
  [[1  "אראובן"  1 1]    ;; א (from Abraham) + ראובן
   [2  "בשמעון"  1 2]    ;; ב (from Abraham) + שמעון
   [3  "רהםלוי"  1 3]    ;; רהם (from Abraham) + לוי
   [4  "ייהודה"  2 1]    ;; י (from Isaac) + יהודה
   [5  "צחקידן"  2 2]    ;; צחקי (from Isaac/Jacob) + דן
   [6  "ענפתלי"  2 3]    ;; ע (from Jacob) + נפתלי
   [7  "קבשבגד"  3 1]    ;; קב (from Jacob) + שב (from שבטי) + גד
   [8  "טייאשר"  3 2]    ;; טיי (from ישרון part) + אשר
   [9  "שיששכר"  3 3]    ;; ש (from ישרון) + יששכר
   [10 "רזבולן"  4 1]    ;; ר (from ישרון) + זבולן
   [11 "וניוסף"  4 2]    ;; ון (from ישרון) + יוסף
   [12 "בנימין"  4 3]])  ;; בנימין (no filler needed)

;; ── Variant C: Sotah 36a Mother-Grouped ─────────────
;;
;; Grouped by mother, as discussed in Sotah 36a:
;; Leah's sons: Reuben, Simeon, Levi, Judah, Issachar, Zebulun
;; Bilhah's sons: Dan, Naphtali
;; Zilpah's sons: Gad, Asher
;; Rachel's sons: Joseph, Benjamin
;;
;; Same filler strategy, same patriarch + שבטי ישרון.
;; Continuous string in mother order:
;; אברהםיצחקיעקב + ראובןשמעוןלוייהודהיששכרזבולןדןנפתליגדאשריוסףבנימין + שבטיישרון

(def variant-c-string
  (str "אברהם"    ;; Abraham
       "יצחק"     ;; Isaac
       "יעקב"     ;; Jacob
       ;; Leah's sons
       "ראובן"    ;; Reuben
       "שמעון"    ;; Simeon
       "לוי"      ;; Levi
       "יהודה"    ;; Judah
       "יששכר"    ;; Issachar (moved earlier)
       "זבולן"    ;; Zebulun (moved earlier)
       ;; Bilhah's sons
       "דן"       ;; Dan
       "נפתלי"    ;; Naphtali
       ;; Zilpah's sons
       "גד"       ;; Gad
       "אשר"      ;; Asher
       ;; Rachel's sons
       "יוסף"     ;; Joseph
       "בנימין"   ;; Benjamin
       ;; Closing
       "שבטי"
       "ישרון"))

(def variant-c
  "Sotah 36a mother-grouped, continuous flow."
  (string->stones variant-c-string))

;; ── Variant D: Grid Orientation Flip ─────────────────
;;
;; Same letters as Variant A, but stone 1 is top-RIGHT
;; (reading right-to-left, as Hebrew). Columns are mirrored.
;; Stone 1 gets col=3, stone 2 gets col=2, stone 3 gets col=1.

(def variant-d
  "Birth order continuous, grid mirrored (stone 1 = top-right)."
  [[1  "אברהםי"  1 3]   [2  "צחקיעק"  1 2]   [3  "בראובן"  1 1]
   [4  "שמעוןל"  2 3]   [5  "וייהוד"  2 2]   [6  "הדןנפת"  2 1]
   [7  "ליגדאש"  3 3]   [8  "ריששכר"  3 2]   [9  "זבולןי"  3 1]
   [10 "וסףבני"  4 3]   [11 "מיןשבט"  4 2]   [12 "יישרון"  4 1]])

;; ══════════════════════════════════════════════════════
;; THE BATTERY
;; ══════════════════════════════════════════════════════

(def battery-words
  "Core words for the variant comparison."
  ["שלום" "חיים" "אמת" "כבש" "שכב" "אהבה" "אהב"
   "יהוה" "והיה" "מים" "רוח" "אור" "חטא" "דם"
   "מלך" "דרך" "ארץ" "עץ" "חשך" "ברך" "מלאך"
   "שתה" "נתן" "באר" "ברא" "ישע" "אשה" "איש"
   "שעיר" "גורל" "כפר" "שלח" "נשא" "חסד" "פנים"
   "כפרת" "פרכת" "צדק" "משפט" "מצרים"
   "תורה" "ברית" "משה" "קדש" "כהן" "את"])

(defn run-battery
  "Run the full battery on a grid. Returns a map of results."
  [label grid-data]
  (let [grid (build-grid grid-data)
        ;; Verify 72 letters
        total-letters (reduce + (map #(count (second %)) grid-data))
        _ (println (format "\n═══ %s (%d letters) ═══" label total-letters))

        ;; Run battery words
        results (mapv #(ask grid %) battery-words)

        ;; Summary stats
        total-by-reader (reduce (fn [acc r]
                                  (-> acc
                                      (update :aaron + (:aaron r))
                                      (update :god + (:god r))
                                      (update :truth + (:truth r))
                                      (update :mercy + (:mercy r))))
                                {:aaron 0 :god 0 :truth 0 :mercy 0}
                                results)

        ;; Exclusives
        find-exclusive (fn [reader-key]
                         (filterv #(and (pos? (get % reader-key))
                                        (zero? (get % (if (= reader-key :aaron) :god :aaron)))
                                        (zero? (get % (if (= reader-key :truth) :mercy :truth)))
                                        (or (= reader-key :aaron)
                                            (zero? (:aaron %)))
                                        (or (= reader-key :god)
                                            (zero? (:god %)))
                                        (or (= reader-key :truth)
                                            (zero? (:truth %)))
                                        (or (= reader-key :mercy)
                                            (zero? (:mercy %))))
                                  results))

        god-excl   (find-exclusive :god)
        right-excl (find-exclusive :truth)
        left-excl  (find-exclusive :mercy)
        aaron-excl (find-exclusive :aaron)

        ;; Ghost zone (illuminated but 0 readings)
        ghosts (filterv #(and (pos? (:illuminations %))
                              (zero? (:total-readings %)))
                        results)

        ;; Dead zone (0 illuminations)
        dead (filterv #(zero? (:illuminations %)) results)

        ;; Lamb split
        lamb (first (filter #(= "כבש" (:word %)) results))
        lie  (first (filter #(= "שכב" (:word %)) results))

        ;; YHWH
        yhwh  (first (filter #(= "יהוה" (:word %)) results))
        vhyh  (first (filter #(= "והיה" (:word %)) results))]

    ;; Print results
    (println (format "  Reader totals: A=%-4d G=%-4d R=%-4d L=%-4d"
                     (:aaron total-by-reader) (:god total-by-reader)
                     (:truth total-by-reader) (:mercy total-by-reader)))

    (println "\n  ── Key Words ──")
    (doseq [r (sort-by :word results)]
      (when (pos? (:total-readings r))
        (println (format "  %-8s GV=%-4d  A=%-3d G=%-3d R=%-3d L=%-3d  total=%d"
                         (:word r) (:gv r)
                         (:aaron r) (:god r) (:truth r) (:mercy r)
                         (:total-readings r)))))

    (println "\n  ── Exclusives ──")
    (println (format "  God-only:   %s" (mapv :word god-excl)))
    (println (format "  Right-only: %s" (mapv :word right-excl)))
    (println (format "  Left-only:  %s" (mapv :word left-excl)))
    (println (format "  Aaron-only: %s" (mapv :word aaron-excl)))

    (println "\n  ── Ghost Zone (illuminated, 0 readings) ──")
    (doseq [r ghosts]
      (println (format "    %-8s illum=%d" (:word r) (:illuminations r))))

    (println "\n  ── Dead Zone (0 illuminations) ──")
    (doseq [r dead]
      (println (format "    %-8s" (:word r))))

    (println "\n  ── The Lamb Split ──")
    (when (and lamb lie)
      (println (format "    lamb(כבש):     A=%-3d G=%-3d R=%-3d L=%-3d"
                       (:aaron lamb) (:god lamb) (:truth lamb) (:mercy lamb)))
      (println (format "    lie-down(שכב): A=%-3d G=%-3d R=%-3d L=%-3d"
                       (:aaron lie) (:god lie) (:truth lie) (:mercy lie)))
      (let [lamb-pair-a (+ (:god lamb) (:truth lamb))
            lamb-pair-b (+ (:aaron lamb) (:mercy lamb))
            lie-pair-a  (+ (:god lie) (:truth lie))
            lie-pair-b  (+ (:aaron lie) (:mercy lie))]
        (println (format "    Lamb seen by God+Right: %d  by Aaron+Left: %d"
                         lamb-pair-a lamb-pair-b))
        (println (format "    Split strength: %d vs %d"
                         (Math/abs (- lamb-pair-a lamb-pair-b))
                         (Math/abs (- lie-pair-a lie-pair-b))))))

    (println "\n  ── The Name ──")
    (when yhwh
      (println (format "    YHWH:     A=%-3d G=%-3d R=%-3d L=%-3d" (:aaron yhwh) (:god yhwh) (:truth yhwh) (:mercy yhwh))))
    (when vhyh
      (println (format "    והיה:     A=%-3d G=%-3d R=%-3d L=%-3d" (:aaron vhyh) (:god vhyh) (:truth vhyh) (:mercy vhyh))))

    ;; Return data
    {:label label
     :total-letters total-letters
     :totals total-by-reader
     :results results
     :god-exclusive god-excl
     :truth-exclusive right-excl
     :mercy-exclusive left-excl
     :aaron-exclusive aaron-excl
     :ghosts ghosts
     :dead dead
     :lamb lamb
     :lie-down lie}))

;; ══════════════════════════════════════════════════════
;; RUN ALL VARIANTS
;; ══════════════════════════════════════════════════════

(defn run-all []
  (println "╔═══════════════════════════════════════════════════╗")
  (println "║  Experiment 140: Grid Variant Comparison          ║")
  (println "║  Which arrangement produces the strongest court?  ║")
  (println "╚═══════════════════════════════════════════════════╝")

  (let [results [(run-battery "A: Birth Order Continuous (current)" variant-a-exact)
                 (run-battery "B: Birth Order, Tribe Per Stone" variant-b)
                 (run-battery "C: Sotah 36a Mother-Grouped" variant-c)
                 (run-battery "D: Grid Mirrored (R→L)" variant-d)]]

    (println "\n\n═══════════════════════════════════════════════")
    (println "  COMPARISON SUMMARY")
    (println "═══════════════════════════════════════════════\n")

    (println (format "%-35s %-8s %-8s %-8s %-8s  %-15s %-15s %-10s"
                     "Variant" "Aaron" "God" "Right" "Left"
                     "Peace" "Life" "Lamb split"))
    (println (apply str (repeat 120 "─")))

    (doseq [{:keys [label totals god-exclusive right-exclusive left-exclusive lamb]} results]
      (let [peace-loc (cond
                        (some #(= "שלום" (:word %)) god-exclusive) "God"
                        (some #(= "שלום" (:word %)) right-exclusive) "Right"
                        (some #(= "שלום" (:word %)) left-exclusive) "Left"
                        :else "shared")
            life-loc (cond
                       (some #(= "חיים" (:word %)) god-exclusive) "God"
                       (some #(= "חיים" (:word %)) right-exclusive) "Right"
                       (some #(= "חיים" (:word %)) left-exclusive) "Left"
                       :else "shared")
            lamb-str (when lamb
                       (format "G%d+R%d / A%d+L%d"
                               (:god lamb) (:truth lamb)
                               (:aaron lamb) (:mercy lamb)))]
        (println (format "%-35s %-8d %-8d %-8d %-8d  %-15s %-15s %s"
                         label
                         (:aaron totals) (:god totals) (:truth totals) (:mercy totals)
                         peace-loc life-loc (or lamb-str "—")))))

    results))

;; ── Run ───────────────────────────────────────────────
;; (run-all)
