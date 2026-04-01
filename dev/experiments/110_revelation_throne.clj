(ns experiments.110-revelation-throne
  "Experiment 110: John's Throne Room (Revelation 4-5).

   Extends experiment 103. The Revelation merger — where cherubim,
   seraphim, and ophanim collapse into one scene.

   No Hebrew text to fetch — Revelation is Greek. We walk the vision
   using Hebrew equivalents of the Greek terms, station by station.
   Also compute isopsephy on key Greek originals.

   5 stations: Door, 24 Elders, Four Creatures, Scroll, Lamb.

   Known from 103:
   - Crown (כתר) basin → כרת (covenant). Crown cast down = covenant.
   - Scroll (ספר) = FIXED POINT. Like YHWH for God.
   - Eagle (נשר) = ghost zone, 60 illuminations, zero readings.
   - Lamb (כבש): God/mercy → כבש, Aaron/justice → שכב (p=0.032).
   - Song (שיר) → שרי (Sarah). Song becomes the princess.
   - Gold (זהב) GV=14 → exactly 72 readings = breastplate count."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
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

(defn isopsephy
  "Greek isopsephy — sum of letter values in a Greek word.
   Standard values: α=1, β=2, ..., ι=10, κ=20, ..., ρ=100, σ=200, ..., ω=800."
  [greek-word]
  (let [vals {\α 1 \β 2 \γ 3 \δ 4 \ε 5 \ζ 7 \η 8 \θ 9
              \ι 10 \κ 20 \λ 30 \μ 40 \ν 50 \ξ 60 \ο 70 \π 80
              \ρ 100 \σ 200 \ς 200 \τ 300 \υ 400 \φ 500 \χ 600 \ψ 700 \ω 800}]
    (reduce + 0 (map #(get vals % 0) greek-word))))

(defn walk-station [station-num station-name description key-words & [greek-words]]
  (println (format "\n╔═══════════════════════════════════════════════╗"))
  (println (format "║  Station %d: %-35s ║" station-num station-name))
  (println (format "║  %-45s ║" description))
  (println (format "╚═══════════════════════════════════════════════╝"))

  (println "\n  ── Key Hebrew Words ──")
  (let [word-results (mapv (fn [[h e]] (query-word h e)) key-words)]

    ;; Greek isopsephy if provided
    (when (seq greek-words)
      (println "\n  ── Greek Isopsephy ──")
      (doseq [[greek english] greek-words]
        (println (format "    %s (%s) = %d" greek english (isopsephy greek)))))

    {:station station-num :name station-name :words word-results}))

;; ── The Five Stations ────────────────────────────────────

(defn station-1-door []
  (walk-station 1 "The Door" "Rev 4:1-3 — Door in heaven, throne, rainbow"
    [["דלת"   "door"]
     ["שמים"  "heaven"]
     ["שופר"  "trumpet"]
     ["קול"   "voice"]
     ["כסא"   "throne"]
     ["קשת"   "rainbow/bow"]
     ["ישפה"  "jasper"]
     ["אדם"   "sardius/man"]
     ["ספיר"  "sapphire"]
     ["ברקת"  "emerald"]
     ["עלה"   "go up"]]
    [["θυρα"  "door/thura"]
     ["θρονος" "throne/thronos"]
     ["ιασπις" "jasper/iaspis"]
     ["σαρδιον" "sardius/sardion"]
     ["ιρις"   "rainbow/iris"]]))

(defn station-2-elders []
  (walk-station 2 "The 24 Elders" "Rev 4:4-5 — White garments, crowns, lamps"
    [["זקן"   "elder"]
     ["לבן"   "white"]
     ["עטרה"  "crown"]
     ["כתר"   "crown/wreath"]
     ["זהב"   "gold"]
     ["ברק"   "lightning"]
     ["קול"   "voice/thunder"]
     ["רעם"   "thunder"]
     ["נר"    "lamp"]
     ["שבע"   "seven"]
     ["רוח"   "spirit"]
     ["אלהים" "God"]]
    [["πρεσβυτερος" "elder/presbyteros"]
     ["στεφανος"    "crown/stephanos"]
     ["λαμπας"      "lamp/lampas"]]))

(defn station-3-creatures []
  (walk-station 3 "The Four Living Creatures" "Rev 4:6-8 — Sea of glass, four faces, eyes, Holy"
    [["ים"     "sea"]
     ["זכוכית" "glass"]
     ["עין"    "eye"]
     ["אריה"   "lion"]
     ["שור"    "ox/calf"]
     ["אדם"    "man"]
     ["נשר"    "eagle"]
     ["כנף"    "wing"]
     ["שש"     "six"]
     ["קדוש"   "holy"]
     ["שדי"    "Almighty"]
     ["בוא"    "come"]
     ["היה"    "was"]
     ["הוה"    "is"]]
    [["ζωον"      "living creature/zōon"]
     ["υαλινη"    "glass/hualinē"]
     ["οφθαλμος"  "eye/ophthalmos"]
     ["αγιος"     "holy/hagios"]]))

(defn station-4-scroll []
  (walk-station 4 "The Scroll" "Rev 5:1-5 — Seven seals, Lion of Judah"
    [["ספר"    "scroll/book"]
     ["חותם"   "seal"]
     ["שבע"    "seven"]
     ["ימין"   "right hand"]
     ["בכה"    "weep"]
     ["אריה"   "lion"]
     ["יהודה"  "Judah"]
     ["שרש"    "root"]
     ["דוד"    "David"]
     ["נצח"    "overcome/eternal"]
     ["פתח"    "open"]
     ["ראוי"   "worthy"]]
    [["βιβλιον"  "scroll/biblion"]
     ["σφραγις"  "seal/sphragis"]
     ["λεων"     "lion/leōn"]]))

(defn station-5-lamb []
  (walk-station 5 "The Lamb" "Rev 5:6-14 — Slain lamb, seven horns, new song"
    [["כבש"    "lamb"]
     ["שה"     "lamb/sheep"]
     ["טבח"    "slain/slaughter"]
     ["שחט"    "slaughter"]
     ["קרן"    "horn"]
     ["עין"    "eye"]
     ["שבע"    "seven"]
     ["רוח"    "spirit"]
     ["שלח"    "send"]
     ["שיר"    "song"]
     ["חדש"    "new"]
     ["גאל"    "redeem"]
     ["דם"     "blood"]
     ["כהן"    "priest"]
     ["מלך"    "king"]
     ["ברך"    "bless"]
     ["כבוד"   "glory"]
     ["עז"     "strength"]
     ["הוד"    "majesty"]
     ["אמן"    "amen"]]
    [["αρνιον"    "lamb/arnion"]
     ["εσφαγμενον" "slain/esphagmenon"]
     ["κερας"     "horn/keras"]
     ["ωδη"       "song/ōdē"]
     ["αμην"      "amen/amēn"]]))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 110: JOHN'S THRONE ROOM")
  (println "  Walking Revelation 4-5 through the breastplate")
  (println "  5 stations · Hebrew equivalents · Greek isopsephy")
  (println "════════════════════════════════════════════════")

  (let [s1 (station-1-door)
        s2 (station-2-elders)
        s3 (station-3-creatures)
        s4 (station-4-scroll)
        s5 (station-5-lamb)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2 s3 s4 s5]]
      (println (format "  Station %d: %s" (:station s) (:name s))))

    ;; Cross-reference highlights
    (println "\n  ── Key Cross-References (from 103-107) ──")
    (println "  - Crown (כתר) basin → כרת (covenant). Crowns cast = covenant given.")
    (println "  - Scroll (ספר) = FIXED POINT. The book that reads itself.")
    (println "  - Eagle (נשר) = ghost zone. Dan's standard unspeakable.")
    (println "  - Lamb split: God/mercy → כבש, Aaron/justice → שכב (p=0.032)")
    (println "  - Song (שיר) → שרי (Sarah). New song = the princess.")
    (println "  - Gold (זהב) GV=14 → 72 readings = breastplate letter count.")

    [s1 s2 s3 s4 s5]))

(comment
  (run-all)

  ;; Run individual stations
  (station-1-door)
  (station-2-elders)
  (station-3-creatures)
  (station-4-scroll)
  (station-5-lamb)

  ;; Key cross-references
  (query-word "כבש" "lamb")          ;; The lamb split
  (query-word "ספר" "scroll")        ;; Fixed point
  (query-word "כתר" "crown")         ;; → covenant
  (query-word "שיר" "song")          ;; → Sarah
  (query-word "אמן" "amen")         ;; What does it do?
  (query-word "גאל" "redeem")        ;; The kinsman redeemer

  ;; Isopsephy tests
  (isopsephy "ιησους")   ;; Jesus
  (isopsephy "χριστος")  ;; Christ
  (isopsephy "αρνιον")   ;; Lamb
  (isopsephy "αμην")     ;; Amen
  )
