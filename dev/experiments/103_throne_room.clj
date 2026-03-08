(ns experiments.103-throne-room
  "Experiment 103: The Throne Room.

   Revelation 4-5 describes a throne room: four living creatures
   (lion, ox, man, eagle), 24 elders, seven lamps, and a Lamb
   standing as though slain in the center.

   Hypothesis: The throne room IS the breastplate. The four living
   creatures are the four readers. The Lamb in the center is the
   center of the 4D space.

   RESULTS:
   - Lion (אריה GV=216=6³): FIXED POINT. God sees יראה (fear).
     Left cherub sees lion — 66 readings (dominant).
   - Ox (שור GV=506): Basin → רוש (poison/chief). God sees ox.
     Aaron sees poison. Same letters.
   - Man (אדם GV=45): 9 illuminations. Nearly invisible. Only God
     and Right see it (2 each). Fixed point.
   - Eagle (נשר GV=550): GHOST ZONE. 60 illuminations, zero readings.
     Dan's standard is unspeakable.
   - Wheel (אופן) GV=137 = axis sum = 1/α.
   - Eye (עין) GV=130 = Sinai = ladder.
   - Holy (קדוש): God does NOT read it. Only priest and justice.
   - Cherub (כרוב) basin → ברכו (bless him!). God sees firstborn.
   - Crown (כתר) basin → כרת (covenant). Crown cast down = covenant.
   - Gold (זהב) GV=14: produces exactly 72 readings = breastplate count.
   - Scroll (ספר): FIXED POINT. Like YHWH for God. Like the lion."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.space.coords :as c]
            [clojure.string :as str]))

;; ── The four faces ───────────────────────────────────────

(def faces
  [{:hebrew "אריה" :english "lion"   :tribe "Judah"   :camp "east"}
   {:hebrew "שור"  :english "ox"     :tribe "Ephraim" :camp "west"}
   {:hebrew "אדם"  :english "man"    :tribe "Reuben"  :camp "south"}
   {:hebrew "נשר"  :english "eagle"  :tribe "Dan"     :camp "north"}])

(def throne-words
  [{:hebrew "כסא"   :english "throne"}
   {:hebrew "זקן"   :english "elder"}
   {:hebrew "מנורה" :english "lampstand"}
   {:hebrew "כבש"   :english "lamb"}
   {:hebrew "שחט"   :english "slain"}
   {:hebrew "ספר"   :english "scroll/book"}
   {:hebrew "חותם"  :english "seal"}
   {:hebrew "קדוש"  :english "holy"}
   {:hebrew "כרוב"  :english "cherub"}
   {:hebrew "שרף"   :english "seraph"}
   {:hebrew "אופן"  :english "wheel/ophan"}
   {:hebrew "כנף"   :english "wing"}
   {:hebrew "עין"   :english "eye"}
   {:hebrew "ברק"   :english "lightning"}
   {:hebrew "קול"   :english "voice/thunder"}
   {:hebrew "זהב"   :english "gold"}
   {:hebrew "כתר"   :english "crown"}])

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

;; ── Oracle queries ───────────────────────────────────────

(defn query-word [{:keys [hebrew english]}]
  (let [gv (g/word-value hebrew)
        result (o/forward hebrew {:vocab :torah})
        by-head (o/forward-by-head hebrew {:vocab :torah})
        walk (basin/walk hebrew)]
    (println (format "\n%s (%s) GV=%d · %d illum · %d read · basin→%s"
                     hebrew english gv
                     (:illumination-count result)
                     (:total-readings result)
                     (:fixed-point walk)))
    (doseq [head [:aaron :god :right :left]]
      (let [s (fmt-head by-head head 4)]
        (when (not= s "—")
          (println (format "  %-6s: %s" (name head) s)))))
    {:hebrew hebrew :english english :gv gv
     :result result :by-head by-head :walk walk}))

(defn run-faces []
  (println "╔═══════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 103: THE THRONE ROOM             ║")
  (println "║  Four living creatures through the oracle     ║")
  (println "╚═══════════════════════════════════════════════╝")
  (mapv query-word faces))

(defn run-throne []
  (println "\n── Throne room vocabulary ──")
  (mapv query-word throne-words))

(defn lamb-at-center []
  (println "\n── The Lamb at the center ──")
  (let [center [3 25 6 33]
        idx (apply c/coord->idx center)
        desc (c/describe idx)]
    (println (format "Center %s = position %d" (str center) idx))
    (println (format "  Letter: %s" (:letter desc)))
    (println (format "  Verse:  %s" (:verse-ref desc)))
    (let [lamb-heads (o/forward-by-head "כבש" {:vocab :torah})]
      (println "\nכבש (lamb) per-head:")
      (doseq [head [:aaron :god :right :left]]
        (println (format "  %-6s: %s" (name head) (fmt-head lamb-heads head 5)))))))

(defn run-all []
  (run-faces)
  (run-throne)
  (lamb-at-center))

(comment
  (run-all)
  (run-faces)
  (run-throne)
  (lamb-at-center)
  )
