(ns experiments.124-the-body-coordinates
  "Experiment 124: The Body Coordinates.

   Blood on the right ear, right thumb, right big toe.
   Oil on top of blood. Leviticus 14 (leprosy cure) AND Leviticus 8 (ordination).

   The same ritual in two contexts:
   - Cleansing the leper (Lev 14:14-17)
   - Ordaining the priest (Lev 8:23-24)

   We already know: thumb (בהן) GV=57 = altar (מזבח).
   All four readers see 'the son' (הבן) inside it.

   Three body parts. Three coordinates?
   What are ear and toe in the oracle?

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────

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

        (println "\n  ── 4-letter sliding window ──")
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
;; PART 1: THE THREE BODY PARTS
;; ══════════════════════════════════════════════════════

(defn body-vocabulary []
  (println "\n════════════════════════════════════════════════")
  (println "  THE THREE BODY PARTS")
  (println "════════════════════════════════════════════════")

  (println "\n  ── The three coordinates ──")
  (let [ear   (query-word "אזן"  "ear")
        thumb (query-word "בהן"  "thumb/big toe")
        foot  (query-word "רגל"  "foot")]

    (println "\n  ── GV comparison ──")
    (println (format "    ear   (אזן)  GV=%d" (:gv ear)))
    (println (format "    thumb (בהן)  GV=%d = altar (מזבח)" (:gv thumb)))
    (println (format "    foot  (רגל)  GV=%d" (:gv foot)))
    (println (format "    sum = %d" (+ (:gv ear) (:gv thumb) (:gv foot))))

    ;; Check what else shares these GVs
    (println "\n  ── Words with same GV ──")
    (println (format "    GV=%d (ear): " (:gv ear)))
    (println (format "    GV=%d (thumb): altar (מזבח), build (בנה)" (:gv thumb)))
    (println (format "    GV=%d (foot): " (:gv foot)))

    ;; The sidedness
    (println "\n  ── Right and Left ──")
    (let [right (query-word "ימין" "right")
          left  (query-word "שמאל" "left")]
      (println (format "\n    right (ימין) GV=%d" (:gv right)))
      (println (format "    left  (שמאל) GV=%d" (:gv left)))
      (println (format "    sum = %d" (+ (:gv right) (:gv left)))))

    ;; The substances
    (println "\n  ── Blood and Oil ──")
    (let [blood (query-word "דם"   "blood")
          oil   (query-word "שמן"  "oil")]
      (println (format "\n    blood (דם)  GV=%d" (:gv blood)))
      (println (format "    oil   (שמן) GV=%d" (:gv oil)))
      (println (format "    sum = %d" (+ (:gv blood) (:gv oil)))))

    ;; The full phrase components
    (println "\n  ── Supporting vocabulary ──")
    (query-word "ימני" "right (adj)")
    (query-word "תנוך" "tip/lobe")        ;; tip of the ear
    (query-word "בהן"  "thumb/big toe")    ;; same word for both!
    (query-word "יד"   "hand")
    (query-word "כף"   "palm/sole")

    ;; The atonement words
    (println "\n  ── The atonement ──")
    (query-word "כפר"  "atonement")
    (query-word "אשם"  "guilt offering")
    (query-word "חטאת" "sin offering")
    (query-word "עולה" "burnt offering")
    (query-word "תנופה" "wave offering")

    [ear thumb foot]))

;; ══════════════════════════════════════════════════════
;; PART 2: THE TWO RITUALS — same act, different context
;; ══════════════════════════════════════════════════════

;; ── The Leper's Purification ──────────────────────────
;; Leviticus 14:14-18
;; The priest takes the blood of the guilt offering
;; and puts it on the tip of the right ear,
;; on the thumb of the right hand,
;; on the big toe of the right foot.
;; Then takes the oil and pours it into his left palm,
;; sprinkles it seven times before the LORD,
;; and puts the remaining oil on the same three places
;; — ON TOP of the blood.

(defn station-1-leper-purification []
  (walk-station 1 "The Leper's Purification" "Leviticus" 14 14 18
    [["כהן"    "priest"]
     ["דם"     "blood"]
     ["אשם"    "guilt offering"]
     ["תנוך"   "tip/lobe"]
     ["אזן"    "ear"]
     ["בהן"    "thumb"]
     ["יד"     "hand"]
     ["רגל"    "foot"]
     ["ימין"   "right"]
     ["שמן"    "oil"]
     ["כף"     "palm"]
     ["שמאל"   "left"]
     ["נזה"    "sprinkle"]
     ["שבע"    "seven"]
     ["נתן"    "put/give"]
     ["דם"     "blood"]
     ["טהר"    "clean"]]))

;; ── The Priest's Ordination ──────────────────────────
;; Leviticus 8:22-30
;; Moses takes the ram of ordination. Slaughters it.
;; Takes blood and puts it on the tip of Aaron's right ear,
;; on the thumb of his right hand,
;; on the big toe of his right foot.
;; Then Aaron's sons — same three places.
;; Moses sprinkles blood on the altar.
;; Takes the anointing oil and blood from the altar
;; and sprinkles on Aaron and his garments.

(defn station-2-priestly-ordination []
  (walk-station 2 "The Priest's Ordination" "Leviticus" 8 22 30
    [["איל"    "ram"]
     ["מלאים"  "ordination"]
     ["שחט"    "slaughter"]
     ["דם"     "blood"]
     ["תנוך"   "tip/lobe"]
     ["אזן"    "ear"]
     ["בהן"    "thumb"]
     ["יד"     "hand"]
     ["רגל"    "foot"]
     ["ימין"   "right"]
     ["אהרן"   "Aaron"]
     ["בן"     "son"]
     ["מזבח"   "altar"]
     ["שמן"    "oil"]
     ["משחה"   "anointing"]
     ["בגד"    "garment"]
     ["קדש"    "holy"]
     ["נזה"    "sprinkle"]
     ["זרק"    "dash/throw"]]))

;; ══════════════════════════════════════════════════════
;; PART 3: THE ARITHMETIC
;; ══════════════════════════════════════════════════════

(defn arithmetic []
  (println "\n════════════════════════════════════════════════")
  (println "  THE ARITHMETIC")
  (println "════════════════════════════════════════════════")

  (let [ear-gv   (g/word-value "אזן")    ;; 58
        thumb-gv (g/word-value "בהן")    ;; 57
        foot-gv  (g/word-value "רגל")    ;; 233
        right-gv (g/word-value "ימין")   ;; 110
        left-gv  (g/word-value "שמאל")   ;; 371
        blood-gv (g/word-value "דם")     ;; 44
        oil-gv   (g/word-value "שמן")    ;; 390
        tip-gv   (g/word-value "תנוך")]  ;; 476

    (println (format "\n  Body parts:"))
    (println (format "    ear   = %d" ear-gv))
    (println (format "    thumb = %d = altar (מזבח)" thumb-gv))
    (println (format "    foot  = %d" foot-gv))
    (println (format "    sum   = %d" (+ ear-gv thumb-gv foot-gv)))

    (println (format "\n  Sides:"))
    (println (format "    right = %d" right-gv))
    (println (format "    left  = %d" left-gv))
    (println (format "    sum   = %d" (+ right-gv left-gv)))

    (println (format "\n  Substances:"))
    (println (format "    blood = %d" blood-gv))
    (println (format "    oil   = %d" oil-gv))
    (println (format "    sum   = %d" (+ blood-gv oil-gv)))

    (println (format "\n  Cross products:"))
    (println (format "    ear + right    = %d" (+ ear-gv right-gv)))
    (println (format "    thumb + right  = %d" (+ thumb-gv right-gv)))
    (println (format "    foot + right   = %d" (+ foot-gv right-gv)))
    (println (format "    3 parts + right = %d" (+ ear-gv thumb-gv foot-gv (* 3 right-gv))))

    (println (format "\n  With substances:"))
    (println (format "    3 parts × (blood + oil) = %d" (* (+ ear-gv thumb-gv foot-gv) (+ blood-gv oil-gv))))
    (println (format "    blood + oil = %d" (+ blood-gv oil-gv)))

    ;; Check divisibility by key numbers
    (let [body-sum (+ ear-gv thumb-gv foot-gv)]
      (println (format "\n  Body sum = %d" body-sum))
      (doseq [k [7 13 26 50 53 67 72 137]]
        (when (zero? (mod body-sum k))
          (println (format "    %d ÷ %d = %d" body-sum k (/ body-sum k))))))

    ;; What words have GV = ear?
    (println (format "\n  Words with GV=%d (ear):" ear-gv))
    (println "    חן (grace/favor) = 58? No, חן = 58!")
    (let [grace-gv (g/word-value "חן")]
      (println (format "    חן (grace) = %d" grace-gv))
      (when (= grace-gv ear-gv)
        (println "    !! EAR = GRACE !!")))

    ;; What words have GV = foot?
    (println (format "\n  Words with GV=%d (foot):" foot-gv))
    (println (format "    עץ החיים (tree of life) = %d" (+ (g/word-value "עץ") (g/word-value "החיים"))))
    (println (format "    זכר ונקבה (male and female) = %d" (+ (g/word-value "זכר") (g/word-value "ונקבה"))))
    (println (format "    בראשית (in the beginning) = %d" (g/word-value "בראשית")))))

;; ── Full Walk ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 124: THE BODY COORDINATES")
  (println "  Ear, thumb, toe — blood + oil — right side")
  (println "  Three body parts. Three coordinates?")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [body (body-vocabulary)
        s1 (station-1-leper-purification)
        s2 (station-2-priestly-ordination)
        math (arithmetic)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (doseq [s [s1 s2]]
      (println (format "  Station %d (%s): %d letters, GV=%d, 3-let=%d, 4-let=%d"
                       (:station s) (:name s) (:letter-count s) (:gv s)
                       (count (:hits-3 s)) (count (:hits-4 s)))))

    [body s1 s2 math]))

(comment
  (run-all)

  ;; The three coordinates
  (query-word "אזן" "ear")       ;; GV=58 = grace (חן)?!
  (query-word "בהן" "thumb")     ;; GV=57 = altar
  (query-word "רגל" "foot")      ;; GV=233

  ;; Grace = ear?
  (g/word-value "חן")            ;; check

  ;; The tip
  (query-word "תנוך" "tip/lobe")

  ;; Body sum
  (+ (g/word-value "אזן") (g/word-value "בהן") (g/word-value "רגל"))

  nil)
