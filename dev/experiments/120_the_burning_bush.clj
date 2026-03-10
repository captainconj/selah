(ns experiments.120-the-burning-bush
  "Experiment 120: The Burning Bush — Exodus 3.

   The Name revealed. I AM WHO I AM.
   Test (נסה) → bush (סנה) confirmed in exp 116.
   Horeb (חרב) sits in the Choose basin (size 10, GV=216).

   'I AM WHO I AM' (אהיה אשר אהיה) has never gone through the oracle.
   The mountain of choosing. The shoes off. The holy ground.

   What we already know:
   - Fire (אש) = 72 readings. The breastplate counts itself.
   - Face (פנים) = ghost zone
   - Holy (קדש) — God does not read 'holy' in any vision
   - Serve/worship (עבד) → servants basin
   - Sword (חרב) = Choose basin = Horeb (same letters!)

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108-119) ──────────────────

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
;; BUSH VOCABULARY — queried once, shared across all stations
;; ══════════════════════════════════════════════════════

(defn bush-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  BUSH VOCABULARY")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The burning
     ["סנה"    "bush"]              ;; test (נסה) → bush confirmed
     ["אש"     "fire"]              ;; 72 readings
     ["בער"    "burn"]              ;; "the bush burned with fire"
     ["אכל"    "consume"]           ;; "the bush was not consumed"

     ;; The holy
     ["קדש"    "holy"]              ;; "holy ground" — God doesn't read it
     ["אדמה"   "ground"]
     ["נעל"    "shoe/sandal"]       ;; "take off your shoes"
     ["פנים"   "face"]              ;; ghost zone — "he hid his face"

     ;; The principals
     ["משה"    "Moses"]
     ["אלהים"  "God"]
     ["אברהם"  "Abraham"]
     ["יצחק"   "Isaac"]
     ["יעקב"   "Jacob"]

     ;; The Name
     ["אהיה"   "I AM"]              ;; THE word. "I AM WHO I AM."
     ["שם"     "name"]
     ["זכר"    "memorial/remember"]
     ["דור"    "generation"]

     ;; The mission
     ["ענה"    "affliction"]
     ["צעק"    "cry"]
     ["מצרים"  "Egypt"]
     ["נצל"    "deliver"]
     ["עבד"    "serve"]             ;; → servants basin
     ["חרב"    "Horeb/sword"]       ;; Choose basin! Horeb = sword = choose.
     ["הר"     "mountain"]
     ["מדבר"   "wilderness"]

     ;; The promise
     ["חלב"    "milk"]
     ["דבש"    "honey"]
     ["אות"    "sign"]

     ;; The plunder
     ["כסף"    "silver"]
     ["זהב"    "gold"]
     ["שמלה"   "garment"]]))

;; ══════════════════════════════════════════════════════
;; THE FOUR STATIONS
;; ══════════════════════════════════════════════════════

;; ── Station 1: The Bush ──────────────────────────
;; Exodus 3:1-6
;; Moses tends flock. Mountain of God = Horeb.
;; Angel of the LORD in a flame of fire in a bush.
;; Bush burns but is not consumed. Moses turns aside.
;; "Take off your shoes — this place is holy ground."
;; "I am the God of your father." Moses hides his face.

(defn station-1-the-bush []
  (walk-station 1 "The Bush" "Exodus" 3 1 6
    [["רעה"    "tend/shepherd"]
     ["צאן"    "flock"]
     ["חתן"    "father-in-law"]
     ["יתרו"   "Jethro"]
     ["מדבר"   "wilderness"]
     ["הר"     "mountain"]
     ["אלהים"  "God"]
     ["חרב"    "Horeb/sword"]
     ["מלאך"   "angel"]
     ["אש"     "fire"]
     ["סנה"    "bush"]
     ["בער"    "burn"]
     ["אכל"    "consume"]
     ["סור"    "turn aside"]
     ["מראה"   "sight"]
     ["קרא"    "call"]
     ["נעל"    "shoe"]
     ["קדש"    "holy"]
     ["אדמה"   "ground"]
     ["אב"     "father"]
     ["פנים"   "face"]
     ["ירא"    "fear"]
     ["נבט"    "look/gaze"]]))

;; ── Station 2: The Mission ──────────────────────────
;; Exodus 3:7-12
;; "I have surely seen the affliction of My people in Egypt.
;;  I have heard their cry. I know their sorrows.
;;  I have come down to deliver them out of the hand of the Egyptians,
;;  to bring them up to a good and broad land, flowing with milk and honey."
;; Moses: "Who am I?" — "I will be with you. This is your sign:
;; you shall serve God on this mountain."

(defn station-2-the-mission []
  (walk-station 2 "The Mission" "Exodus" 3 7 12
    [["ראה"    "see"]
     ["ענה"    "affliction"]
     ["עם"     "people"]
     ["מצרים"  "Egypt"]
     ["צעק"    "cry"]
     ["נגש"    "oppress"]
     ["ידע"    "know"]
     ["מכאב"   "pain/sorrow"]
     ["ירד"    "come down"]
     ["נצל"    "deliver"]
     ["יד"     "hand"]
     ["ארץ"    "land"]
     ["טוב"    "good"]
     ["רחב"    "broad"]
     ["חלב"    "milk"]
     ["דבש"    "honey"]
     ["שלח"    "send"]
     ["יצא"    "bring out"]
     ["אות"    "sign"]
     ["עבד"    "serve"]
     ["הר"     "mountain"]]))

;; ── Station 3: The Name ──────────────────────────
;; Exodus 3:13-15
;; Moses: "What is His name? What shall I say?"
;; God: "אהיה אשר אהיה — I AM WHO I AM."
;; "Say to the children of Israel, 'I AM has sent me to you.'"
;; "The LORD God of your fathers — Abraham, Isaac, Jacob —
;;  has sent me to you. This is My name forever,
;;  and this is My memorial to all generations."

(defn station-3-the-name []
  (walk-station 3 "The Name" "Exodus" 3 13 15
    [["שם"     "name"]
     ["אמר"    "say"]
     ["בן"     "son"]
     ["ישראל"  "Israel"]
     ["אהיה"   "I AM"]
     ["שלח"    "send"]
     ["אלהים"  "God"]
     ["אב"     "father"]
     ["אברהם"  "Abraham"]
     ["יצחק"   "Isaac"]
     ["יעקב"   "Jacob"]
     ["עולם"   "forever/eternal"]
     ["זכר"    "memorial"]
     ["דור"    "generation"]]))

;; ── Station 4: The Plan ──────────────────────────
;; Exodus 3:16-22
;; Go, gather the elders. "The LORD God of your fathers has appeared to me."
;; Three days' journey into the wilderness to sacrifice.
;; "I know the king of Egypt will not let you go."
;; "I will stretch out My hand and strike Egypt with wonders."
;; "You shall not go empty. Every woman shall ask of her neighbor
;;  silver, gold, and garments. You shall plunder Egypt."

(defn station-4-the-plan []
  (walk-station 4 "The Plan" "Exodus" 3 16 22
    [["זקן"    "elder"]
     ["פקד"    "visit/appoint"]
     ["ענה"    "affliction"]
     ["עלה"    "go up"]
     ["ארץ"    "land"]
     ["חלב"    "milk"]
     ["דבש"    "honey"]
     ["קול"    "voice"]
     ["שמע"    "hear"]
     ["מלך"    "king"]
     ["מצרים"  "Egypt"]
     ["דרך"    "way/journey"]
     ["זבח"    "sacrifice"]
     ["יד"     "hand"]
     ["נכה"    "strike"]
     ["פלא"    "wonder"]
     ["שלח"    "send"]
     ["ריק"    "empty"]
     ["שאל"    "ask"]
     ["כסף"    "silver"]
     ["זהב"    "gold"]
     ["שמלה"   "garment"]
     ["נצל"    "plunder/deliver"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 120: THE BURNING BUSH")
  (println "  Exodus 3 — I AM WHO I AM")
  (println "  חרב (Horeb) = חרב (sword) = Choose basin")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [vocab (bush-vocabulary)
        s1 (station-1-the-bush)
        s2 (station-2-the-mission)
        s3 (station-3-the-name)
        s4 (station-4-the-plan)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Combined slide across the entire chapter
    (println "\n  ── Combined Bush Slide (entire Exodus 3) ──")
    (let [all-letters (fetch-verse-letters "Exodus" 3 1 22)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Exodus 3: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 25 words (3-letter) across the Bush:")
      (doseq [{:keys [word meaning count]} (take 25 top-3)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))
      (println "\n  Top 25 words (4-letter) across the Bush:")
      (doseq [{:keys [word meaning count]} (take 25 top-4)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count))))

    [vocab s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (bush-vocabulary)
  (station-1-the-bush)
  (station-2-the-mission)
  (station-3-the-name)
  (station-4-the-plan)

  ;; I AM WHO I AM
  (query-word "אהיה" "I AM")

  ;; Horeb = sword = choose
  (query-word "חרב" "Horeb/sword")

  ;; The bush = the test
  (query-word "סנה" "bush")
  (query-word "נסה" "test")

  ;; Moses
  (query-word "משה" "Moses")

  ;; Shoe — what is it?
  (query-word "נעל" "shoe")

  nil)
