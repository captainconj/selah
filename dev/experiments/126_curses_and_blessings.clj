(ns experiments.126-curses-and-blessings
  "Experiment 126: Curses Walk to Blessings.

   In Genesis 3 (exp 119): flaming→dew, skin→friend, sweat→strength,
   pain→profit. Every curse resolves through basin walks.

   Is this universal or local to the Fall?

   Collect every curse/judgment word. Walk each one. Where does it land?
   Collect every blessing word. Walk each one. Where does it land?

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [clojure.string :as str]))

;; ── Helper ──────────────────────────────────────────

(defn walk-word [hebrew english]
  (let [gv   (g/word-value hebrew)
        r    (o/forward hebrew :torah)
        walk (basin/walk hebrew)
        fp   (:fixed-point walk)
        path (:steps walk)]
    (println (format "  %-8s %-25s GV=%3d  illum=%3d  read=%4d  → %-8s  path=%s"
                     hebrew english gv
                     (:illumination-count r) (:total-readings r)
                     (or fp "null")
                     (mapv :word path)))
    {:hebrew hebrew :english english :gv gv
     :illuminations (:illumination-count r)
     :readings (:total-readings r)
     :fixed-point fp
     :path (mapv :word path)
     :depth (count path)}))

;; ══════════════════════════════════════════════════════
;; PART 1: CURSE / JUDGMENT / DESTRUCTION WORDS
;; ══════════════════════════════════════════════════════

(def curse-words
  [;; Primary curse roots
   ["ארר"    "curse (ארר)"]
   ["ארור"   "cursed"]
   ["מארה"   "curse (noun)"]
   ["קלל"    "curse/lighten"]
   ["קללה"   "curse (noun, קלל)"]

   ;; Destruction & devotion
   ["חרם"    "devoted destruction"]
   ["שחת"    "destroy/pit"]
   ["כרת"    "cut off"]
   ["אבד"    "perish/destroy"]
   ["שמד"    "annihilate"]
   ["הרג"    "kill"]
   ["מות"    "death"]
   ["מוות"   "death (full)"]
   ["שאול"   "sheol/grave"]
   ["קבר"    "grave"]

   ;; Judgment
   ["שפט"    "judge"]
   ["דין"    "judgment/law"]
   ["נקם"    "vengeance"]
   ["עונש"   "punishment"]
   ["אשם"    "guilt"]
   ["חטא"    "sin"]
   ["עון"    "iniquity"]
   ["פשע"    "transgression"]
   ["רשע"    "wicked"]

   ;; Plague & striking
   ["נגף"    "plague/strike"]
   ["נגע"    "plague/touch"]
   ["דבר"    "pestilence/word"]
   ["מגפה"   "epidemic"]
   ["מכה"    "wound/blow"]

   ;; Impurity & profanity
   ["טמא"    "unclean"]
   ["נדה"    "impurity/banishment"]
   ["חלל"    "profane/pierced"]
   ["נאץ"    "spurn/blaspheme"]
   ["גדף"    "revile"]
   ["תועבה"  "abomination"]

   ;; Genesis 3 curse words
   ["נחש"    "serpent"]
   ["ערום"   "cunning/naked"]
   ["זעה"    "sweat"]
   ["עצב"    "pain/toil"]
   ["קוץ"    "thorn"]
   ["דרדר"   "thistle"]
   ["עפר"    "dust"]

   ;; Exile & scattering
   ["גלה"    "exile/reveal"]
   ["נדד"    "wander"]
   ["פזר"    "scatter"]
   ["נוד"    "wandering"]])

;; ══════════════════════════════════════════════════════
;; PART 2: BLESSING / GOODNESS / LIFE WORDS
;; ══════════════════════════════════════════════════════

(def blessing-words
  [;; Blessing root
   ["ברך"    "bless"]
   ["ברכה"   "blessing"]
   ["ברוך"   "blessed"]

   ;; Goodness
   ["טוב"    "good"]
   ["טובה"   "goodness"]
   ["ישר"    "upright"]
   ["צדק"    "righteous"]
   ["צדקה"   "righteousness"]
   ["תם"     "complete/innocent"]
   ["תמים"   "blameless"]

   ;; Love & kindness
   ["אהב"    "love"]
   ["אהבה"   "love (noun)"]
   ["חסד"    "lovingkindness"]
   ["חן"     "grace/favor"]
   ["רחם"    "mercy/womb"]
   ["רחמים"  "compassion"]

   ;; Life
   ["חיה"    "live"]
   ["חיים"   "life"]
   ["נפש"    "soul/life"]
   ["רוח"    "spirit/wind"]
   ["נשמה"   "breath"]

   ;; Peace & rest
   ["שלום"   "peace"]
   ["מנוחה"  "rest"]
   ["שקט"    "quiet"]
   ["בטח"    "trust/security"]
   ["שמח"    "joy/rejoice"]
   ["ששון"   "gladness"]

   ;; Salvation & redemption
   ["ישע"    "save/deliver"]
   ["ישועה"  "salvation"]
   ["גאל"    "redeem"]
   ["פדה"    "ransom"]
   ["נצל"    "deliver/rescue"]
   ["כפר"    "atone/cover"]

   ;; Light & glory
   ["אור"    "light"]
   ["כבוד"   "glory"]
   ["הדר"    "splendor"]
   ["תפארת"  "beauty/glory"]

   ;; Provision
   ["לחם"    "bread"]
   ["מים"    "water"]
   ["שמן"    "oil"]
   ["יין"    "wine"]
   ["רפא"    "heal"]

   ;; Covenant
   ["ברית"   "covenant"]
   ["שבועה"  "oath"]
   ["אמן"    "amen/faithful"]
   ["אמת"    "truth"]
   ["חכמה"   "wisdom"]
   ["בינה"   "understanding"]])

;; ══════════════════════════════════════════════════════
;; PART 3: ANALYSIS
;; ══════════════════════════════════════════════════════

(defn analyze-walks [label words]
  (println (format "\n════════════════════════════════════════════════"))
  (println (format "  %s" label))
  (println (format "════════════════════════════════════════════════"))
  (mapv (fn [[h e]] (walk-word h e)) words))

(defn classify [results]
  (let [invisible (filter #(zero? (:illuminations %)) results)
        visible   (filter #(pos? (:illuminations %)) results)
        fixed     (filter #(= (:hebrew %) (:fixed-point %)) results)
        to-null   (filter #(nil? (:fixed-point %)) results)
        transient (filter #(and (:fixed-point %)
                                (not= (:hebrew %) (:fixed-point %))) results)]
    {:invisible (count invisible)
     :visible   (count visible)
     :fixed     (count fixed)
     :to-null   (count to-null)
     :transient (count transient)
     :total     (count results)
     :items     results}))

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 126: CURSES WALK TO BLESSINGS?")
  (println "  Every curse word. Every blessing word. Where do they go?")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [curses   (analyze-walks "CURSE / JUDGMENT / DESTRUCTION WORDS" curse-words)
        blessings (analyze-walks "BLESSING / GOODNESS / LIFE WORDS" blessing-words)
        cc (classify curses)
        bc (classify blessings)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (println "\n  ── Curse Basin Destinations ──")
    (println (format "    Total: %d" (:total cc)))
    (println (format "    Invisible (0/0): %d" (:invisible cc)))
    (println (format "    Fixed points: %d" (:fixed cc)))
    (println (format "    Walk to null: %d" (:to-null cc)))
    (println (format "    Walk to other word: %d" (:transient cc)))

    (println "\n  ── Blessing Basin Destinations ──")
    (println (format "    Total: %d" (:total bc)))
    (println (format "    Invisible (0/0): %d" (:invisible bc)))
    (println (format "    Fixed points: %d" (:fixed bc)))
    (println (format "    Walk to null: %d" (:to-null bc)))
    (println (format "    Walk to other word: %d" (:transient bc)))

    (println "\n  ── Curse destinations (non-null, non-self) ──")
    (doseq [c (filter #(and (:fixed-point %)
                             (not= (:hebrew %) (:fixed-point %)))
                       curses)]
      (println (format "    %s (%s) → %s  via %s"
                       (:hebrew c) (:english c) (:fixed-point c)
                       (str/join "→" (:path c)))))

    (println "\n  ── Blessing destinations (non-null, non-self) ──")
    (doseq [b (filter #(and (:fixed-point %)
                             (not= (:hebrew %) (:fixed-point %)))
                       blessings)]
      (println (format "    %s (%s) → %s  via %s"
                       (:hebrew b) (:english b) (:fixed-point b)
                       (str/join "→" (:path b)))))

    (println "\n  ── Where do curses land? ──")
    (let [curse-dests (frequencies (map :fixed-point curses))]
      (doseq [[dest cnt] (sort-by val > curse-dests)]
        (println (format "    %-8s ×%d" (or dest "null") cnt))))

    (println "\n  ── Where do blessings land? ──")
    (let [bless-dests (frequencies (map :fixed-point blessings))]
      (doseq [[dest cnt] (sort-by val > bless-dests)]
        (println (format "    %-8s ×%d" (or dest "null") cnt))))

    ;; Cross-check: do any curses land on blessing words?
    (println "\n  ── Do curses resolve to blessing words? ──")
    (let [blessing-set (set (map first blessing-words))]
      (doseq [c curses
              :when (and (:fixed-point c)
                         (blessing-set (:fixed-point c)))]
        (println (format "    %s (%s) → %s  ← BLESSING!"
                         (:hebrew c) (:english c) (:fixed-point c)))))

    ;; Cross-check: do any blessings land on curse words?
    (println "\n  ── Do blessings resolve to curse words? ──")
    (let [curse-set (set (map first curse-words))]
      (doseq [b blessings
              :when (and (:fixed-point b)
                         (curse-set (:fixed-point b)))]
        (println (format "    %s (%s) → %s  ← CURSE!"
                         (:hebrew b) (:english b) (:fixed-point b)))))

    {:curses cc :blessings bc}))

(comment
  (run-all)

  ;; Quick checks
  (walk-word "ארר" "curse")
  (walk-word "ברך" "bless")
  (walk-word "שחת" "destroy")
  (walk-word "טוב" "good")

  nil)
