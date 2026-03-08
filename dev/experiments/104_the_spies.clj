(ns experiments.104-the-spies
  "Experiment 104: The Spies Go Out.

   Send twelve investigations into the angelic/throne territory.
   Named angels, the little scroll, camp standards, seven seals,
   the dragon, the conflict. Look for clues, cribs, understanding.

   'Only Joshuas and Calebs.' — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────

(defn fmt-head [by-head head n]
  (let [words (get by-head head)]
    (if (seq words)
      (->> words
           (sort-by (comp - :reading-count))
           (take n)
           (map #(str (:word %) "(" (:reading-count %) ")"))
           (str/join " "))
      "—")))

(defn query-word
  "Run a word through the oracle. Print results. Return data."
  ([hebrew] (query-word hebrew nil))
  ([hebrew english]
   (let [label (or english hebrew)
         gv (g/word-value hebrew)
         result (o/forward hebrew {:vocab :torah})
         by-head (o/forward-by-head hebrew {:vocab :torah})
         walk (basin/walk hebrew)]
     (println (format "\n%s (%s) GV=%d · %d illum · %d read · basin→%s"
                      hebrew label gv
                      (:illumination-count result)
                      (:total-readings result)
                      (:fixed-point walk)))
     (doseq [head [:aaron :god :right :left]]
       (let [s (fmt-head by-head head 5)]
         (when (not= s "—")
           (println (format "  %-6s: %s" (name head) s)))))
     {:hebrew hebrew :english english :gv gv
      :illuminations (:illumination-count result)
      :readings (:total-readings result)
      :result result :by-head by-head :walk walk})))

;; ── SPY 1: Named Angels ─────────────────────────────────

(def named-angels
  [{:hebrew "מיכאל" :english "Michael (who is like God?)"}
   {:hebrew "גבריאל" :english "Gabriel (strength of God)"}
   {:hebrew "שטן"   :english "Satan (adversary)"}
   {:hebrew "מלאך"  :english "angel (messenger)"}
   {:hebrew "שליח"  :english "sent one (apostle)"}])

(defn spy-named-angels []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 1: NAMED ANGELS                         ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) named-angels))

;; ── SPY 2: The Little Scroll (Rev 10) ───────────────────

(def little-scroll-words
  [{:hebrew "מגלה"  :english "scroll/reveal"}
   {:hebrew "דבש"   :english "honey (sweet)"}
   {:hebrew "מר"    :english "bitter"}
   {:hebrew "ענן"   :english "cloud"}
   {:hebrew "קשת"   :english "rainbow/bow"}
   {:hebrew "שמש"   :english "sun"}
   {:hebrew "אש"    :english "fire"}
   {:hebrew "עמוד"  :english "pillar"}
   {:hebrew "ים"    :english "sea"}
   {:hebrew "ארץ"   :english "earth/land"}
   {:hebrew "רעם"   :english "thunder"}
   {:hebrew "נבא"   :english "prophesy"}
   {:hebrew "אכל"   :english "eat"}])

(defn spy-little-scroll []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 2: THE LITTLE SCROLL (Rev 10)           ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) little-scroll-words))

;; ── SPY 3: Camp Standards → Reader Mapping ───────────────

(def camp-words
  [{:hebrew "יהודה"  :english "Judah (lion/east)"}
   {:hebrew "אפרים"  :english "Ephraim (ox/west)"}
   {:hebrew "ראובן"  :english "Reuben (man/south)"}
   {:hebrew "דן"     :english "Dan (eagle/north)"}
   {:hebrew "מחנה"   :english "camp"}
   {:hebrew "דגל"    :english "standard/banner"}
   {:hebrew "מזרח"   :english "east"}
   {:hebrew "מערב"   :english "west"}
   {:hebrew "נגב"    :english "south (Negev)"}
   {:hebrew "צפון"   :english "north"}])

(defn spy-camp-mapping []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 3: CAMP STANDARDS → READER MAPPING      ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) camp-words))

;; ── SPY 4: Three Sevens (seals, trumpets, bowls) ────────

(def three-sevens
  [{:hebrew "חותם"   :english "seal"}
   {:hebrew "שופר"   :english "shofar/trumpet"}
   {:hebrew "חצצרה"  :english "silver trumpet"}
   {:hebrew "מזרק"   :english "bowl (sprinkling)"}
   {:hebrew "כוס"    :english "cup"}
   {:hebrew "שבע"    :english "seven/oath"}
   {:hebrew "פתח"    :english "open"}])

(defn spy-three-sevens []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 4: THREE SEVENS (seals/trumpets/bowls)   ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) three-sevens))

;; ── SPY 5: The Dragon and the Conflict ──────────────────

(def conflict-words
  [{:hebrew "תנין"   :english "dragon/sea-serpent"}
   {:hebrew "נחש"    :english "serpent"}
   {:hebrew "לויתן"  :english "Leviathan"}
   {:hebrew "שר"     :english "prince/captain"}
   {:hebrew "מלחמה"  :english "war/battle"}
   {:hebrew "נפל"    :english "fall/Nephilim"}
   {:hebrew "גרש"    :english "cast out/drive out"}
   {:hebrew "תהום"   :english "abyss/deep"}
   {:hebrew "שלשלת" :english "chain"}])

(defn spy-conflict []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 5: THE DRAGON AND THE CONFLICT           ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) conflict-words))

;; ── SPY 6: Throne Room Deeper ────────────────────────────

(def throne-deeper
  [{:hebrew "זכוכית" :english "glass (sea of glass)"}
   {:hebrew "קרח"    :english "ice/crystal"}
   {:hebrew "ספיר"   :english "sapphire"}
   {:hebrew "רקיע"   :english "firmament/expanse"}
   {:hebrew "כבוד"   :english "glory"}
   {:hebrew "אמן"    :english "amen"}
   {:hebrew "הללויה" :english "hallelujah"}
   {:hebrew "משיח"   :english "messiah/anointed"}
   {:hebrew "גאל"    :english "redeem/redeemer"}
   {:hebrew "פדה"    :english "ransom"}])

(defn spy-throne-deeper []
  (println "\n╔═══════════════════════════════════════════════╗")
  (println "║  SPY 6: THRONE ROOM DEEPER                   ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv #(query-word (:hebrew %) (:english %)) throne-deeper))

;; ── Run all spies ────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 104: THE SPIES GO OUT")
  (println "  'The land is ripe. Only Joshuas and Calebs.'")
  (println "════════════════════════════════════════════════")
  (let [r1 (spy-named-angels)
        r2 (spy-little-scroll)
        r3 (spy-camp-mapping)
        r4 (spy-three-sevens)
        r5 (spy-conflict)
        r6 (spy-throne-deeper)]
    {:named-angels r1
     :little-scroll r2
     :camp-mapping r3
     :three-sevens r4
     :conflict r5
     :throne-deeper r6}))

(comment
  (run-all)
  (spy-named-angels)
  (spy-little-scroll)
  (spy-camp-mapping)
  (spy-three-sevens)
  (spy-conflict)
  (spy-throne-deeper)
  )
