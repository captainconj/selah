(ns experiments.109-isaiahs-temple
  "Experiment 109: Isaiah's Temple (Isaiah 6).

   Short, focused. 13 verses. The seraphim, the burning coal,
   the commissioning.

   We know from experiment 103:
   - God does NOT read 'holy' (קדוש). Only priest and justice.
   - שרף (seraph) basin → רשף (flame/pestilence). God returns nothing.
   - The coal from the altar should connect to the tabernacle altar station.
   - הנני should be deeply visible (Abraham, Moses, Isaiah all use it).

   4 stations. Walk the actual vision text and see if the oracle
   confirms what we already found in individual words."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers (same pattern as 108) ────────────────────────

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

;; ── The Four Stations ────────────────────────────────────

(defn station-1-throne []
  (walk-station 1 "The Throne" "Isaiah" 6 1 2
    [["כסא"   "throne"]
     ["רם"    "high/exalted"]
     ["נשא"   "lifted up"]
     ["שרף"   "seraph"]
     ["כנף"   "wing"]
     ["שש"    "six"]
     ["פנים"  "face"]
     ["רגל"   "foot"]
     ["עוף"   "fly"]
     ["שול"   "train/hem"]
     ["היכל"  "temple"]
     ["מלא"   "fill"]
     ["אדני"  "Lord"]]))

(defn station-2-holy []
  (walk-station 2 "Holy Holy Holy" "Isaiah" 6 3 4
    [["קדוש"  "holy"]
     ["צבאות" "hosts/armies"]
     ["כבוד"  "glory"]
     ["מלא"   "fill"]
     ["ארץ"   "earth"]
     ["קרא"   "call"]
     ["אמר"   "say"]
     ["נוע"   "shake"]
     ["עשן"   "smoke"]
     ["מזוזה" "doorpost"]
     ["אמה"   "cubit/foundation"]
     ["סף"    "threshold"]]))

(defn station-3-coal []
  (walk-station 3 "The Coal" "Isaiah" 6 5 7
    [["אוי"   "woe"]
     ["טמא"   "unclean"]
     ["שפה"   "lip"]
     ["עין"   "eye"]
     ["מלך"   "king"]
     ["גחלת"  "coal"]
     ["מזבח"  "altar"]
     ["עון"   "guilt/iniquity"]
     ["סור"   "depart/remove"]
     ["חטאת"  "sin"]
     ["כפר"   "atone"]
     ["נגע"   "touch"]
     ["מלקח"  "tongs"]]))

(defn station-4-sending []
  (walk-station 4 "The Sending" "Isaiah" 6 8 13
    [["שלח"   "send"]
     ["הנני"  "here am I"]
     ["שמע"   "hear"]
     ["ראה"   "see"]
     ["בין"   "understand"]
     ["לב"    "heart"]
     ["שמן"   "fat/oil"]
     ["כבד"   "heavy/glory"]
     ["עד"    "until/witness"]
     ["שאר"   "remnant"]
     ["קדש"   "holy"]
     ["גזע"   "stump"]
     ["זרע"   "seed"]
     ["מצבה"  "pillar/stump"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 109: ISAIAH'S TEMPLE")
  (println "  Walking Isaiah 6 through the breastplate")
  (println "  4 stations · key words · sliding windows")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-throne)
        s2 (station-2-holy)
        s3 (station-3-coal)
        s4 (station-4-sending)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    ;; Full chapter
    (println "\n  ── Full Chapter Slide ──")
    (let [all-letters (fetch-verse-letters "Isaiah" 6 1 13)
          all-3 (slide-text all-letters 3)
          all-4 (slide-text all-letters 4)
          top-3 (word-frequencies all-3)
          top-4 (word-frequencies all-4)]
      (println (format "  Full Isaiah 6: %d letters." (count all-letters)))
      (println (format "  3-letter: %d readings. 4-letter: %d readings."
                       (count all-3) (count all-4)))
      (println "\n  Top 20 words (3-letter) across full chapter:")
      (doseq [{:keys [word meaning count]} (take 20 top-3)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count)))
      (println "\n  Top 20 words (4-letter) across full chapter:")
      (doseq [{:keys [word meaning count]} (take 20 top-4)]
        (println (format "    %-8s %-20s ×%d" word (or meaning "") count))))

    [s1 s2 s3 s4]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-throne)
  (station-2-holy)
  (station-3-coal)
  (station-4-sending)

  ;; Quick test
  (sefaria/fetch-chapter "Isaiah" 6)

  ;; The seraph — God returns nothing
  (query-word "שרף" "seraph")

  ;; Holy — God does NOT read it
  (query-word "קדוש" "holy")

  ;; Here am I — deeply visible?
  (query-word "הנני" "here am I")

  ;; The coal from the altar
  (query-word "גחלת" "coal")
  (query-word "מזבח" "altar")
  )
