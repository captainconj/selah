(ns experiments.121-jacob-wrestles
  "Experiment 121: Jacob Wrestles — Genesis 32:23-33.

   The naming of Israel. The hip that limps.
   Sinew (גיד) → telling (יגד) at GV=17=good.
   Follows directly from Jacob's Ladder (exp 117).

   What we already know:
   - Israel (ישראל) = 11,880 readings (165×72) in exp 117
   - Jacob (יעקב) = fixed point, God reads ×50 (jubilee)
   - Face (פנים) = ghost zone. But here: Peniel (פנואל) = 'face of God'
   - God (אלהים) → to-them (אליהם) basin
   - Bless (ברך) — Jacob demands: 'I will not let you go unless you bless me'
   - Sun (שמש) — 'the sun rose upon him as he passed Peniel'

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-120) ──────────────────

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
;; WRESTLING VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn wrestling-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  WRESTLING VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The wrestling
     ["אבק"    "wrestle/dust"]      ;; root of "wrestle" — same root as "dust"?
     ["איש"    "man"]               ;; "a man wrestled with him"
     ["לילה"   "night"]             ;; "until the breaking of the day"
     ["יכל"    "prevail"]           ;; "he could not prevail"
     ["שחר"    "dawn"]              ;; "let me go, for the day breaks"

     ;; The wounding
     ["ירך"    "hip/thigh"]         ;; "he touched the hollow of his hip"
     ["כף"     "hollow/sole"]       ;; the socket of the hip
     ["נגע"    "touch/strike"]      ;; same word as "plague/stricken"
     ["יקע"    "dislocate"]         ;; the hip was put out of joint

     ;; The naming
     ["שם"     "name"]              ;; "what is your name?"
     ["יעקב"   "Jacob"]             ;; the old name
     ["ישראל"  "Israel"]            ;; the new name — "striven with God and men"
     ["שרה"    "strive/prevail"]    ;; the root of Israel
     ["אלהים"  "God"]               ;; "you have striven with God"
     ["אנש"    "man/mortal"]        ;; "and with men"

     ;; The face
     ["פנים"   "face"]              ;; ghost zone — but Peniel = "face of God"
     ["פנואל"  "Peniel"]            ;; "I have seen God face to face"
     ["נפש"    "soul"]              ;; "my soul is preserved"

     ;; The blessing
     ["ברך"    "bless"]             ;; "I will not let you go unless you bless me"

     ;; The limp
     ["צלע"    "limp/rib"]          ;; same word as Eve's rib?
     ["גיד"    "sinew"]             ;; → telling (יגד) at GV=17=good!
     ["נשה"    "sinew (sciatic)"]   ;; gid ha-nasheh
     ["שמש"    "sun"]               ;; "the sun rose upon him"
     ["עבר"    "cross/pass"]]))     ;; "he passed over Peniel"

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Crossing ──────────────────────────
;; Genesis 32:23-25 (MT: 32:24-26)
;; Jacob takes his two wives, two female servants, eleven sons.
;; Crosses the ford of Jabbok. Sends them across the stream.
;; Jacob is LEFT ALONE. A man wrestles with him until dawn.

(defn station-1-the-crossing []
  (walk-station 1 "The Crossing" "Genesis" 32 23 25
    [["לקח"    "take"]
     ["אשה"    "wife"]
     ["שפחה"   "maidservant"]
     ["בן"     "son"]
     ["עבר"    "cross"]
     ["יבק"    "Jabbok"]
     ["נחל"    "stream"]
     ["מעבר"   "ford"]
     ["שלח"    "send"]
     ["בדד"    "alone"]
     ["איש"    "man"]
     ["אבק"    "wrestle"]
     ["עלה"    "dawn/ascend"]
     ["שחר"    "dawn"]]))

;; ── Station 2: The Wounding ──────────────────────────
;; Genesis 32:26-27 (MT: 32:27-28)
;; He sees he cannot prevail. Touches the hollow of Jacob's hip.
;; Jacob's hip is put out of joint as he wrestles.
;; "Let me go, for the day breaks."
;; "I will not let you go unless you bless me."

(defn station-2-the-wounding []
  (walk-station 2 "The Wounding" "Genesis" 32 26 27
    [["ראה"    "see"]
     ["יכל"    "prevail"]
     ["נגע"    "touch/strike"]
     ["כף"     "hollow/sole"]
     ["ירך"    "hip"]
     ["יקע"    "dislocate"]
     ["שלח"    "let go"]
     ["עלה"    "dawn"]
     ["שחר"    "dawn"]
     ["ברך"    "bless"]]))

;; ── Station 3: The Naming ──────────────────────────
;; Genesis 32:28-30 (MT: 32:29-31)
;; "What is your name?" "Jacob."
;; "Your name shall no longer be Jacob, but ISRAEL —
;;  for you have striven with God and with men, and have prevailed."
;; Jacob asks: "Tell me your name." "Why do you ask my name?"
;; He blessed him there.
;; Jacob calls the place PENIEL — "I have seen God face to face,
;; and my soul is preserved."

(defn station-3-the-naming []
  (walk-station 3 "The Naming" "Genesis" 32 28 30
    [["שם"     "name"]
     ["יעקב"   "Jacob"]
     ["ישראל"  "Israel"]
     ["שרה"    "strive"]
     ["אלהים"  "God"]
     ["אנש"    "man/mortal"]
     ["יכל"    "prevail"]
     ["שאל"    "ask"]
     ["נגד"    "tell"]
     ["ברך"    "bless"]
     ["קרא"    "call"]
     ["מקום"   "place"]
     ["פנואל"  "Peniel"]
     ["פנים"   "face"]
     ["ראה"    "see"]
     ["נפש"    "soul"]
     ["נצל"    "deliver/preserve"]]))

;; ── Station 4: The Limp ──────────────────────────
;; Genesis 32:31-33 (MT: 32:32-33)
;; "The sun rose upon him as he passed over Peniel,
;;  and he limped on his hip.
;;  Therefore the children of Israel do not eat the sinew of the hip
;;  which is on the hollow of the thigh, to this day,
;;  because he touched the hollow of Jacob's hip
;;  in the sinew of the hip."

(defn station-4-the-limp []
  (walk-station 4 "The Limp" "Genesis" 32 31 33
    [["שמש"    "sun"]
     ["זרח"    "rise"]
     ["עבר"    "pass over"]
     ["פנואל"  "Peniel"]
     ["צלע"    "limp/rib"]
     ["ירך"    "hip"]
     ["בן"     "son"]
     ["ישראל"  "Israel"]
     ["אכל"    "eat"]
     ["גיד"    "sinew"]
     ["נשה"    "sciatic"]
     ["כף"     "hollow"]
     ["נגע"    "touch"]
     ["יעקב"   "Jacob"]
     ["יום"    "day"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 121: JACOB WRESTLES")
  (println "  Genesis 32:23-33 — The naming of Israel")
  (println "  גיד (sinew) = יגד (telling) = GV 17 = good")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (wrestling-vocabulary)
        s1 (station-1-the-crossing)
        s2 (station-2-the-wounding)
        s3 (station-3-the-naming)
        s4 (station-4-the-limp)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the wrestling scene
    (println "\n  ── Combined Wrestling Slide (Gen 32:23-33) ──")
    (let [all-letters (fetch-verse-letters "Genesis" 32 23 33)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Wrestling scene: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across the wrestling:")
      (doseq [{:keys [word count]} (take 25 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 25 words (4-letter) across the wrestling:")
      (doseq [{:keys [word count]} (take 25 top-4)]
        (println (format "    %-8s ×%d" word count))))

    [vocab s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (wrestling-vocabulary)
  (station-1-the-crossing)
  (station-2-the-wounding)
  (station-3-the-naming)
  (station-4-the-limp)

  ;; Sinew = telling = good
  (query-word "גיד" "sinew")        ;; GV=17 = good
  (query-word "טוב" "good")         ;; GV=17

  ;; Limp = rib?
  (query-word "צלע" "limp/rib")

  ;; The face of God
  (query-word "פנואל" "Peniel")
  (query-word "פנים" "face")        ;; ghost zone

  ;; Jacob → Israel
  (query-word "יעקב" "Jacob")
  (query-word "ישראל" "Israel")

  nil)
