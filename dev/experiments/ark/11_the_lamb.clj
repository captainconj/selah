;; Ark Walk 11 — The Lamb
;; The lamb split at the Ark. YHWH as reading mechanism.
;; Right cherub (yod=10) = the hand = guardian = proofreader
;; Three codon positions map to three taxonomy branches

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)]

  ;; ═══ THE LAMB SPLIT ═══
  (println "=== THE LAMB SPLIT ===")
  (let [by-head (o/forward-by-head "כבש")]
    (doseq [reader [:aaron :god :right :left]]
      (println (format "  %-6s:" (name reader)))
      (doseq [w (take 3 (get by-head reader))]
        (println (format "    %-8s count=%-3d  %s"
                         (:word w) (:reading-count w) (or (dict/translate (:word w)) ""))))))
  (println)
  (println "  כבש (lamb) GV = 322")
  (println "  שכב (lie down) GV = 322")
  (println "  Same letters. God + Right → lamb. Aaron + Left → lie down.")
  (println "  Basin: UNANIMOUS — all four heads agree both exist (p=0.032)")
  (println)

  ;; ═══ THE FOUR READERS AS YHWH ═══
  (println "=== THE READING PROTOCOL: YHWH ===")
  (println "  Letter  Value  Reader        Role            Direction")
  (println "  ------  -----  ------        ----            ---------")
  (println "  י       10     Right cherub  Hand (select)   columns R→L, top→bottom")
  (println "  ה       5      God           Observe (check) rows L→R, bottom→top")
  (println "  ו       6      Aaron         Nail (connect)  rows R→L, top→bottom")
  (println "  ה       5      Left cherub   Observe (verify) columns L→R, bottom→top")
  (println "  Total: 10+5+6+5 = 26 = YHWH")
  (println)

  ;; ═══ RIGHT CHERUB: THE HAND ═══
  (println "=== RIGHT CHERUB: THE HAND (י=10) ===")
  (println "  Solos: holds LIFE as fixed point")
  (println "  Reads rooms (קנים) as 'we establish' (נקים)")
  (println "  Reads wood/tree as fixed")
  (println "  The proofreader. The guard at the third branch.")
  (println)

  ;; ═══ PER-HEAD ON ARK WORDS ═══
  (println "=== PER-HEAD ON ARK KEY WORDS ===")
  (doseq [w ["כבש" "חיים" "רוח" "אמת" "שלום" "אשר" "רמש" "קנים"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (when (seq words)
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader)
                             (:word (first words))
                             (:reading-count (first words))
                             (or (dict/translate (:word (first words))) ""))))))))
  (println)

  ;; ═══ THREE CODON POSITIONS ═══
  (println "=== THREE CODON POSITIONS (c=12 TREE) ===")
  (println "  Branch  Word     Meaning     Position  Guard behavior")
  (println "  ------  ----     -------     --------  --------------")
  (println "  1st     עוף      bird/fly    1st       strict (determines class)")
  (println "  2nd     הבהמה    beast/walk  2nd       refines assignment")
  (println "  3rd     רמש      creeping    3rd       most permissive (wobble)")
  (println)
  (println "  רמש = שמר (guard). The wobble position IS the guard position.")
  (println)

  ;; ═══ DESTRUCTION = ANOINTING ═══
  (println "=== FOUNDATION SACRIFICE ROOTS ===")
  (println (format "  משחיתם (destroying them) → root משח (anoint) GV=%d" (g/word-value "משח")))
  (println (format "  כפר (covering/atonement) GV=%d — fixed point" (g/word-value "כפר")))
  (println (format "  וכפרת → וכפתר (menorah knob) — sacrifice becomes craftsmanship"))
  (println (format "  First letters of three floors: %s %s %s = שמן (oil/anointing)"
                   (sc/letter-at s (sc/coord->idx 0 8 9 0))
                   (sc/letter-at s (sc/coord->idx 0 8 8 0))
                   (sc/letter-at s (sc/coord->idx 0 8 7 0))))
  (println)

  ;; ═══ BASIN DYNAMICS FOR LAMB ═══
  (println "=== BASIN DYNAMICS ===")
  (doseq [w ["כבש" "שכב" "משח" "כפר" "רמש" "שמר" "עשה"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?"))))))
