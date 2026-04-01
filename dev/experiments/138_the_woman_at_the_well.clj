(ns experiments.138-the-woman-at-the-well
  "Experiment 138: The Woman at the Well — John 4 through the breastplate.
   The chapter about living water, run through the oracle that already
   whispered 'living water' from inside way+truth+life.

   Key findings:
   - Well (באר) = create (ברא) — Ramban pair #5. GV=203, prime.
     Basin: well→create for God/Right/Left. Aaron alone holds 'well' still.
   - Water (מים) GV=90 — fixed point for God (weight 10). Dead end for Aaron.
     God reads water. The priest cannot.
   - Life (חיים) GV=68 — fixed point ONLY for Right cherub (the Lamb).
     Life is exclusively at God's right hand. Same as experiment 097.
   - Spring of water (מעין מים) GV=260 = 10×26 = Yod×YHWH. The spring IS
     the hand of the Name.
   - Spirit (רוח) GV=214 — fixed for God and Left. Aaron/Right see חור (hole/white).
     Spirit and truth: אמת רוח appears in the 86 parsings.
   - Truth (אמת) GV=441 — fixed for God and Left (the prosecution).
   - Thirst (צמא) GV=131, prime — same as מי אנכי (who am I, emphatic).
     Parses to מצא (find). Thirst becomes finding.
   - I am he (אני הוא) GV=73, prime. 1,386 illuminations.
     Parses include והיא נא (and she, please) — the woman IS the plea.
   - Drink (שתה) GV=705 — God-exclusive fixed point. Only God reads 'drink.'
     Same word as God's solo eigenword from experiment 091.
   - Give (נתן) GV=500 — unanimous fixed point across all 4 readers.
     The giving does not change regardless of who reads it.
   - Woman (אשה) GV=306 — Left cherub dominant (20 readings).
     Basin: God/Left hold woman. Aaron/Right see fire (האש).
     The woman becomes fire for the priest.
   - Woman at the well (אשה+באר) GV=509 — parsings include:
     אהב ראש (love, head), אהב אשר (love, that-which),
     אבא שרה (father, Sarah). The woman at the well = love + Sarah.
   - Save (ישע) GV=380 — Aaron sees nothing (reads עשי).
     God sees 60 readings. Salvation is God-dominant.
   - Seed (זרע) GV=277 — God/Right see seed. Aaron/Left see עזר (help/Ezer).
     Seed becomes help. The sower becomes the helper.
   - Harvest (קציר) GV=400 — ghost zone. 110 illuminations, zero readings.
     The harvest glows and cannot be spoken. Like Egypt (also 110 illuminations).
   - Field (שדה) GV=309 — God-dominant (24 readings). Aaron: dead end.
   - White (לבן) GV=82 — Left cherub dominant (30 readings). Laban.
   - Samaria (שמרון) GV=596 — contains שמר (guard). 2,520 illuminations
     but Left cherub dominates (196 of 217 readings). Basin: total dead end.
     Samaria is maximally illuminated and leads nowhere."
  (:require [selah.oracle :as o]
            [selah.basin :as b]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ── Utilities ─────────────────────────────────────────

(defn run-oracle
  "Run o/ask and o/thummim on a word, print results."
  [label heb]
  (println (str "\n══ " label " — " heb " ══"))
  (println "GV:" (g/word-value heb))
  (let [r (o/ask heb)]
    (println "Illuminations:" (:illumination-count r))
    (println "Total readings:" (:total-readings r))
    (println "By reader:" (:by-reader r))
    (when-let [anags (:anagrams r)]
      (println "Anagrams:" (mapv :word anags)))
    (when (:readable? r)
      (println "Readable: yes"))))

(defn run-thummim
  "Run Level 2 Thummim (phrase parsing) on combined letters."
  [label letters]
  (println (str "\n── Level 2 Thummim: " label " (" letters ") ──"))
  (println "GV:" (g/word-value letters))
  (let [phrases (o/parse-letters letters)]
    (println "Phrases:" (count phrases))
    (doseq [p (take 30 phrases)]
      (println " " (:text p) "—" (str/join ", " (:meanings p))))))

(defn run-basin
  "Run one-step basin walk on a word, per head."
  [label heb]
  (println (str "\n── Basin: " label " (" heb ") ──"))
  (let [r (b/step-all-heads heb)]
    (doseq [[head info] (sort-by key r)]
      (if info
        (println (format "  %-6s → %s (weight %d)" (name head) (:next info) (:weight info)))
        (println (format "  %-6s → dead end" (name head)))))))

;; ── John 4: The Key Words ────────────────────────────

(defn run-all []
  (println "╔══════════════════════════════════════════════╗")
  (println "║  Experiment 138: The Woman at the Well       ║")
  (println "║  John 4 through the breastplate              ║")
  (println "╚══════════════════════════════════════════════╝")

  ;; ── Section 1: The Setting ──────────────────────────
  (println "\n━━━ THE SETTING ━━━")

  (run-oracle "well" "באר")
  ;; באר/ברא = well/create — Ramban pair #5. GV=203, prime.
  ;; Digging the well IS creating. Same letters, same value.

  (run-oracle "water" "מים")
  (run-oracle "woman" "אשה")
  (run-oracle "Samaria" "שמרון")

  ;; ── Section 2: The Conversation ─────────────────────
  (println "\n━━━ THE CONVERSATION ━━━")

  ;; v7: "Give me to drink"
  (run-oracle "give" "נתן")
  (run-oracle "drink" "שתה")

  ;; v10: "If you knew the gift of God"
  (run-oracle "gift" "מתנה")
  (run-oracle "know" "ידע")

  ;; v10: "living water"
  (run-oracle "life/living" "חי")
  (run-oracle "life (pl)" "חיים")

  ;; ── Section 3: The Spring ───────────────────────────
  (println "\n━━━ THE SPRING ━━━")

  ;; v14: "a spring of water welling up to eternal life"
  (run-oracle "spring/eye" "עין")
  (run-oracle "spring (noun)" "מעין")
  (run-oracle "eternal" "עולם")

  ;; ── Section 4: Spirit and Truth ─────────────────────
  (println "\n━━━ SPIRIT AND TRUTH ━━━")

  ;; v23-24: "worship the Father in spirit and truth"
  (run-oracle "spirit" "רוח")
  (run-oracle "truth" "אמת")
  (run-oracle "father" "אב")
  (run-oracle "worship/bow" "חוה")

  ;; ── Section 5: The Declaration ──────────────────────
  (println "\n━━━ THE DECLARATION ━━━")

  ;; v26: "I who speak to you am he" — אני הוא
  (run-oracle "I" "אני")
  (run-oracle "he/that" "הוא")

  ;; v29: "Come, see a man who told me all that I ever did"
  (run-oracle "come" "בוא")
  (run-oracle "see" "ראה")
  (run-oracle "man" "איש")

  ;; v42: "savior of the world"
  (run-oracle "save/deliver" "ישע")

  ;; ── Section 6: The Harvest ──────────────────────────
  (println "\n━━━ THE HARVEST ━━━")

  ;; v35-38: "the fields are white for harvest"
  (run-oracle "harvest" "קציר")
  (run-oracle "field" "שדה")
  (run-oracle "seed/sow" "זרע")
  (run-oracle "white" "לבן")

  ;; ── Section 7: Combined Words — Level 2 Thummim ────
  (println "\n\n━━━ LEVEL 2 THUMMIM — COMBINED WORDS ━━━")

  ;; Living water
  (run-thummim "living water" "מיםחיים")
  (run-thummim "living water (חי מים)" "חימים")

  ;; Gift of God
  (run-thummim "gift of God" "מתנהאלהים")

  ;; Spirit and truth
  (run-thummim "spirit and truth" "רוחאמת")

  ;; I am he
  (run-thummim "I am he" "אניהוא")

  ;; Spring of water
  (run-thummim "spring of water" "מעיןמים")

  ;; Well of living water
  (run-thummim "well of living water" "בארמיםחיים")

  ;; The well and the woman
  (run-thummim "woman at the well" "אשהבאר")

  ;; ── Section 8: Basin Walks ──────────────────────────
  (println "\n\n━━━ BASIN WALKS ━━━")

  (run-basin "well" "באר")
  (run-basin "water" "מים")
  (run-basin "life" "חיים")
  (run-basin "spirit" "רוח")
  (run-basin "truth" "אמת")
  (run-basin "thirst" "צמא")
  (run-basin "gift" "מתנה")
  (run-basin "drink" "שתה")
  (run-basin "spring" "מעין")
  (run-basin "harvest" "קציר")
  (run-basin "seed" "זרע")
  (run-basin "worship" "חוה")

  (println "\n\nDone."))

;; ── Run ───────────────────────────────────────────────
;; (run-all)

;; Quick GV checks for John 4 concepts
(comment
  (g/word-value "באר")     ;; well = 203 (prime, = create)
  (g/word-value "מעיןמים") ;; spring of water = 260 = 10 × 26 = Yod × YHWH
  (g/word-value "אניהוא")  ;; I am he = 73
  (g/word-value "צמא")     ;; thirst = 131 (prime, = מי אנכי)
  (g/word-value "מתנה")    ;; gift = 495 = 5 × 99 = 5 × (9×11)
  (g/word-value "רוחאמת")  ;; spirit + truth = 655 = 5 × 131
  (g/word-value "שמרון")   ;; Samaria = 596
  )
