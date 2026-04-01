(ns experiments.111-daniels-thrones
  "Experiment 111: Daniel's Thrones (Daniel 7).

   Four beasts, thrones set up, Ancient of Days, one like a son of man.
   Daniel 7 is in Aramaic, not Hebrew. Many words share roots but
   differ in form. We test both Aramaic originals AND Hebrew cognates.

   The four beasts as corrupted versions of the four faces:
   - Lion with eagle's wings = our two throne creatures combined
   - Bear raised on one side = asymmetry
   - Leopard with four wings = four readers
   - Terrible beast with iron teeth = iron(26)=YHWH

   Ancient of Days — white = לבן = Laban?
   Son of man on clouds — nearly invisible man (9 illuminations)
   arriving on what God does not see (clouds).

   5 stations. Aramaic text fetched from Sefaria, slid through the
   breastplate alongside Hebrew cognates."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────

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

(defn walk-station [station-num station-name book chapter v-start v-end
                    aramaic-words hebrew-cognates]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "║  %s %d:%d-%d %s ║"
                   book chapter v-start v-end
                   (apply str (repeat (- 35 (count (str book " " chapter ":" v-start "-" v-end))) " "))))
  (println (format "╚═══════════════════════════════════════════════╝"))

  ;; Fetch and slide the Aramaic text
  (let [letters (fetch-verse-letters book chapter v-start v-end)
        gv (g/word-value letters)]
    (println (format "\n  %d letters. GV=%d." (count letters) gv))
    (println (format "  Letters: %s" (if (> (count letters) 120)
                                       (str (subs letters 0 120) "...")
                                       letters)))

    ;; Aramaic words through oracle
    (println "\n  ── Aramaic Words ──")
    (let [ar-results (mapv (fn [[h e]] (query-word h e)) aramaic-words)]

      ;; Hebrew cognates for comparison
      (println "\n  ── Hebrew Cognates ──")
      (let [heb-results (mapv (fn [[h e]] (query-word h e)) hebrew-cognates)]

        ;; Slide 3-letter windows
        (println "\n  ── 3-letter sliding window (Aramaic text) ──")
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

          ;; Slide 4-letter windows
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
             :aramaic-words ar-results :hebrew-cognates heb-results
             :hits-3 hits-3 :hits-4 hits-4
             :top-3 top-3 :top-4 top-4}))))))

;; ── The Five Stations ────────────────────────────────────

(defn station-1-beasts []
  (walk-station 1 "The Four Beasts" "Daniel" 7 1 8
    ;; Aramaic words
    [["חיוה"  "beast (Aram.)"]
     ["אריה"  "lion"]
     ["נשר"   "eagle"]
     ["דב"    "bear"]
     ["נמר"   "leopard"]
     ["גף"    "wing (Aram.)"]
     ["שן"    "tooth"]
     ["פרזל"  "iron (Aram.)"]
     ["קרן"   "horn"]
     ["עין"   "eye"]
     ["רב"    "great"]
     ["ים"    "sea"]
     ["רוח"   "wind"]
     ["ארבע"  "four"]]
    ;; Hebrew cognates
    [["חיה"   "living creature (Heb.)"]
     ["כנף"   "wing (Heb.)"]
     ["ברזל"  "iron (Heb.)"]
     ["לב"    "heart (Heb.)"]
     ["אנש"   "man (Aram. root)"]
     ["אדם"   "man (Heb.)"]]))

(defn station-2-thrones []
  (walk-station 2 "The Thrones" "Daniel" 7 9 10
    ;; Aramaic words
    [["כרסי"  "throne (Aram.)"]
     ["עתיק"  "ancient"]
     ["יומין" "days (Aram.)"]
     ["לבן"   "white"]
     ["חור"   "white/free"]
     ["עמר"   "wool"]
     ["גלגל"  "wheel (Aram.)"]
     ["נור"   "fire (Aram.)"]
     ["דלק"   "burn"]
     ["נהר"   "river"]
     ["אלף"   "thousand"]
     ["רבו"   "ten thousand"]
     ["דין"   "judgment"]]
    ;; Hebrew cognates
    [["כסא"   "throne (Heb.)"]
     ["אופן"  "wheel (Heb.)"]
     ["אש"    "fire (Heb.)"]
     ["שער"   "hair"]
     ["צמר"   "wool (Heb.)"]
     ["ספר"   "book"]]))

(defn station-3-judgment []
  (walk-station 3 "The Judgment" "Daniel" 7 11 12
    ;; Aramaic words
    [["קטל"   "slay (Aram.)"]
     ["אבד"   "destroy"]
     ["יקד"   "burn (Aram.)"]
     ["שלטן"  "dominion (Aram.)"]
     ["גשם"   "body (Aram.)"]
     ["חיה"   "beast"]
     ["זמן"   "time (Aram.)"]]
    ;; Hebrew cognates
    [["הרג"   "kill (Heb.)"]
     ["שרף"   "burn (Heb.)"]
     ["גוף"   "body (Heb.)"]
     ["משל"   "rule (Heb.)"]]))

(defn station-4-son-of-man []
  (walk-station 4 "The Son of Man" "Daniel" 7 13 14
    ;; Aramaic words
    [["בר"    "son (Aram.)"]
     ["אנש"   "man (Aram.)"]
     ["ענן"   "cloud"]
     ["שמין"  "heaven (Aram.)"]
     ["עתיק"  "ancient"]
     ["יומין" "days"]
     ["שלטן"  "dominion"]
     ["מלכו"  "kingdom (Aram.)"]
     ["עלם"   "eternity"]
     ["יקר"   "glory/honor (Aram.)"]
     ["עמם"   "people (Aram.)"]
     ["לשן"   "language (Aram.)"]
     ["פלח"   "serve (Aram.)"]]
    ;; Hebrew cognates
    [["בן"    "son (Heb.)"]
     ["אדם"   "man (Heb.)"]
     ["שמים"  "heaven (Heb.)"]
     ["מלכות" "kingdom (Heb.)"]
     ["עולם"  "eternity (Heb.)"]
     ["כבוד"  "glory (Heb.)"]
     ["עבד"   "serve (Heb.)"]]))

(defn station-5-interpretation []
  (walk-station 5 "The Interpretation" "Daniel" 7 15 28
    ;; Aramaic words
    [["קדיש"  "holy one (Aram.)"]
     ["עליון" "Most High"]
     ["מלכו"  "kingdom"]
     ["עלם"   "eternity"]
     ["דין"   "judgment"]
     ["חסן"   "strength (Aram.)"]
     ["עדן"   "time (Aram.)"]
     ["שאר"   "rest/remnant"]
     ["ארע"   "earth (Aram.)"]
     ["שמים"  "heaven"]]
    ;; Hebrew cognates
    [["קדוש"  "holy (Heb.)"]
     ["אלהים" "God (Heb.)"]
     ["משפט"  "judgment (Heb.)"]
     ["שאר"   "remnant (Heb.)"]
     ["ארץ"   "earth (Heb.)"]
     ["מלך"   "king (Heb.)"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 111: DANIEL'S THRONES")
  (println "  Walking Daniel 7 through the breastplate")
  (println "  5 stations · Aramaic + Hebrew cognates · sliding windows")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-beasts)
        s2 (station-2-thrones)
        s3 (station-3-judgment)
        s4 (station-4-son-of-man)
        s5 (station-5-interpretation)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Full chapter slide
    (println "\n  ── Full Chapter Slide ──")
    (let [all-letters (fetch-verse-letters "Daniel" 7 1 28)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full Daniel 7: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 20 words (3-letter) across full chapter:")
      (doseq [{:keys [word count]} (take 20 top-3)]
        (println (format "    %-8s ×%d" word count)))
      (println "\n  Top 20 words (4-letter) across full chapter:")
      (doseq [{:keys [word count]} (take 20 top-4)]
        (println (format "    %-8s ×%d" word count))))

    ;; Key cross-references
    (println "\n  ── Key Cross-References ──")
    (println "  - Iron (ברזל) = element 26 = YHWH. The terrible beast has iron teeth.")
    (println "  - Wheel (אופן) GV=137 = axis sum. Daniel says גלגל (Aram. wheel).")
    (println "  - Man (אדם) = 9 illuminations. Son of man arrives nearly invisible.")
    (println "  - Cloud (ענן): God does not see clouds? Son of man comes ON clouds.")
    (println "  - Lion with eagle wings = lion + eagle = two throne creatures fused.")
    (println "  - לשן (tongue/language): Noah's Ark box coordinates → שנל → this.")

    [s1 s2 s3 s4 s5]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-beasts)
  (station-2-thrones)
  (station-3-judgment)
  (station-4-son-of-man)
  (station-5-interpretation)

  ;; Quick test: fetch Daniel 7
  (sefaria/fetch-chapter "Daniel" 7)

  ;; Key Aramaic words
  (query-word "עתיק" "ancient")
  (query-word "בר" "son")
  (query-word "אנש" "man")
  (query-word "גלגל" "wheel")
  (query-word "נור" "fire")
  (query-word "שלטן" "dominion")
  (query-word "קדיש" "holy one")

  ;; The lion-eagle fusion
  (let [lion-gv (g/word-value "אריה")
        eagle-gv (g/word-value "נשר")]
    {:lion lion-gv :eagle eagle-gv :sum (+ lion-gv eagle-gv)})

  ;; Ancient of Days + white
  (query-word "לבן" "white/Laban")
  )
