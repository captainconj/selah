(ns experiments.108-ezekiels-chariot
  "Experiment 108: Ezekiel's Chariot (Ezekiel 1).

   The original throne vision. The most architecturally detailed.
   Every structural element maps to something we've already measured.

   Four faces = four readers. Wheels (אופן GV=137) = axis sum.
   Eyes everywhere = the ox's blood. Fire between the creatures.
   The man on the throne is nearly invisible (אדם = 9 illuminations).

   We walk 6 stations, asking the breastplate at each one.
   Then we slide the prophetic text through the oracle like protein —
   3-letter and 4-letter windows across Ezekiel's own letters."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────

(defn query-word
  "Run a word through the oracle. Print results. Return data."
  [hebrew english]
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

(defn fetch-verse-letters
  "Fetch verses from Sefaria, normalize to pure Hebrew letters, return string."
  [book chapter v-start v-end]
  (let [verses (sefaria/fetch-chapter book chapter)
        selected (subvec (vec verses) (dec v-start) v-end)
        raw (apply str (map norm/strip-html selected))]
    (apply str (norm/letter-stream raw))))

(defn slide-text
  "Slide n-letter windows across Hebrew text, return oracle hits.
   Like dna/slide but for any Hebrew text (not protein)."
  [hebrew window]
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

(defn word-frequencies
  "From slide results, count how often each word appears as top oracle reading."
  [hits]
  (->> hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
               :count (count entries)}))
       (sort-by (comp - :count))
       vec))

(defn walk-station
  "Walk one station of a vision. Print header, query words, slide text."
  [station-num station-name book chapter v-start v-end key-words]
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

    ;; Key words through oracle
    (println "\n  ── Key Words ──")
    (let [word-results (mapv (fn [[h e]] (query-word h e)) key-words)]

      ;; Slide 3-letter windows
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

        ;; Slide 4-letter windows (cherubim's view)
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

;; ── The Six Stations ─────────────────────────────────────

(defn station-1-opening []
  (walk-station 1 "The Opening" "Ezekiel" 1 1 3
    [["שמים"  "heavens"]
     ["פתח"   "open"]
     ["יד"    "hand"]
     ["כהן"   "priest"]
     ["מראה"  "vision"]
     ["כבר"   "Chebar/already"]
     ["יחזקאל" "Ezekiel"]]))

(defn station-2-storm []
  (walk-station 2 "The Storm" "Ezekiel" 1 4 4
    [["סערה"  "whirlwind/storm"]
     ["צפון"  "north"]
     ["ענן"   "cloud"]
     ["אש"    "fire"]
     ["חשמל"  "amber/electrum"]
     ["רוח"   "wind/spirit"]
     ["גדול"  "great"]
     ["נגה"   "brightness"]]))

(defn station-3-living-creatures []
  (walk-station 3 "The Four Living Creatures" "Ezekiel" 1 5 14
    [["חיה"   "living creature"]
     ["פנים"  "face"]
     ["כנף"   "wing"]
     ["רגל"   "foot"]
     ["אדם"   "man"]
     ["אריה"  "lion"]
     ["שור"   "ox"]
     ["נשר"   "eagle"]
     ["ארבע"  "four"]
     ["אש"    "fire"]
     ["גחלי"  "coals"]
     ["לפיד"  "torch"]
     ["ברק"   "lightning"]
     ["ישר"   "straight"]
     ["נחשת"  "bronze"]
     ["בזק"   "flash"]]))

(defn station-4-wheels []
  (walk-station 4 "The Wheels" "Ezekiel" 1 15 21
    [["אופן"  "wheel"]
     ["עין"   "eye"]
     ["רוח"   "spirit"]
     ["תרשיש" "Tarshish/beryl"]
     ["חיה"   "living creature"]
     ["ארץ"   "earth"]
     ["ירא"   "fear/awe"]
     ["גבה"   "height/lofty"]
     ["נשא"   "lift up"]]))

(defn station-5-firmament []
  (walk-station 5 "The Firmament" "Ezekiel" 1 22 25
    [["רקיע"  "firmament/expanse"]
     ["קרח"   "ice/crystal"]
     ["כנף"   "wing"]
     ["קול"   "voice"]
     ["שדי"   "Almighty"]
     ["המון"  "multitude"]
     ["מחנה"  "camp"]
     ["רפה"   "let down/relax"]
     ["עמד"   "stand"]]))

(defn station-6-throne []
  (walk-station 6 "The Throne" "Ezekiel" 1 26 28
    [["כסא"   "throne"]
     ["ספיר"  "sapphire"]
     ["אדם"   "man"]
     ["אש"    "fire"]
     ["קשת"   "rainbow/bow"]
     ["כבוד"  "glory"]
     ["יהוה"  "YHWH"]
     ["נגה"   "brightness"]
     ["מראה"  "appearance"]
     ["דמות"  "likeness"]
     ["מתנים" "loins"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 108: EZEKIEL'S CHARIOT")
  (println "  Walking Ezekiel 1 through the breastplate")
  (println "  6 stations · key words · sliding windows")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-opening)
        s2 (station-2-storm)
        s3 (station-3-living-creatures)
        s4 (station-4-wheels)
        s5 (station-5-firmament)
        s6 (station-6-throne)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5 s6]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Whole chapter analysis
    (println "\n  ── Full Chapter Slide ──")
    (let [all-letters (fetch-verse-letters "Ezekiel" 1 1 28)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full Ezekiel 1: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 20 words (3-letter) across full chapter:")
      (doseq [{:keys [word meaning count]} (take 20 top-3)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))
      (println "\n  Top 20 words (4-letter) across full chapter:")
      (doseq [{:keys [word meaning count]} (take 20 top-4)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count))))

    [s1 s2 s3 s4 s5 s6]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-opening)
  (station-2-storm)
  (station-3-living-creatures)
  (station-4-wheels)
  (station-5-firmament)
  (station-6-throne)

  ;; Quick test: fetch Ezekiel 1
  (sefaria/fetch-chapter "Ezekiel" 1)

  ;; Get verse letters for a station
  (fetch-verse-letters "Ezekiel" 1 1 3)

  ;; Slide test
  (slide-text (fetch-verse-letters "Ezekiel" 1 4 4) 3)

  ;; Cross-reference: amber/electrum — is it ghost zone?
  (query-word "חשמל" "amber/electrum")

  ;; The wheel IS the axis sum
  (query-word "אופן" "wheel")

  ;; The man on the throne — nearly invisible
  (query-word "אדם" "man")

  ;; Full chapter letters
  (count (fetch-verse-letters "Ezekiel" 1 1 28))
  )
