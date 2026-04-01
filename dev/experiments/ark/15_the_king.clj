;; Ark Walk 15 — The King
;; The oracle cannot say מלך (king). It cannot say כסא (throne) or ברך (bless).
;; The oracle is a priest, not a king. But the mercy seat IS the throne.
;; משיח (358) → ימשח (he will anoint). ישוע (386) → וישע (and he saved, 229 readings!)

(require '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(let [factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))]

  ;; ═══ DEAD ENDS: WHAT THE ORACLE CANNOT SAY ═══
  (println "=== DEAD ENDS: WHAT THE ORACLE CANNOT SAY ===")
  (doseq [w ["מלך" "כסא" "ברך" "משפט"]]
    (let [fwd (try (o/forward (seq w) :torah) (catch Exception _ nil))
          known (when fwd (count (:known-words fwd)))
          illum (when fwd (count (or (:illumination-sets fwd) [])))]
      (println (format "  %-8s gv=%-4d  illuminations=%-3s  known=%-2s  %s"
                       w (g/word-value w)
                       (or illum "?") (or known "?")
                       (or (dict/translate w) "?")))))
  (println)

  ;; ═══ THE THRONE ═══
  (println "=== THE THRONE ===")
  (println (format "  כסא (throne) GV = %d = 9² = 3⁴" (g/word-value "כסא")))
  (println "  Letters: כ(Trp=rarest) ס(Stop=end) א(beyond code)")
  (println "  The throne = the rarest + the end + the beyond")
  (println)

  ;; ═══ THE CROWN ═══
  (println "=== THE CROWN ===")
  (println (format "  כתר (crown) GV = %d" (g/word-value "כתר")))
  (let [fwd (o/forward (seq "כתר") :torah)]
    (println (format "  Known words: %d" (count (:known-words fwd))))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %-8s count=%-3d  %s"
                       (:word w) (:reading-count w) (or (dict/translate (:word w)) "")))))
  (println "  כתר (crown) → כרת (cut/covenant) — the crown IS the covenant cut")
  (println)

  ;; ═══ DAVID ═══
  (println "=== DAVID ===")
  (println (format "  דוד GV = %d = יד (hand)" (g/word-value "דוד")))
  (let [fwd (o/forward (seq "דוד") :torah)]
    (println (format "  Known words: %d" (count (:known-words fwd))))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %-8s count=%-3d  %s"
                       (:word w) (:reading-count w) (or (dict/translate (:word w)) "")))))
  (println)

  ;; ═══ SOLOMON ═══
  (println "=== SOLOMON ===")
  (println (format "  שלמה GV = %d" (g/word-value "שלמה")))
  (let [fwd (o/forward (seq "שלמה") :torah)]
    (println (format "  Known words: %d" (count (:known-words fwd))))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %-8s count=%-3d  %s"
                       (:word w) (:reading-count w) (or (dict/translate (:word w)) "")))))
  (println "  שלמה → שמלה (garment) — Solomon becomes clothing")
  (println)

  ;; ═══ MESSIAH ═══
  (println "=== MESSIAH ===")
  (println (format "  משיח GV = %d = נחש (serpent)" (g/word-value "משיח")))
  (let [fwd (o/forward (seq "משיח") :torah)]
    (println (format "  Known words: %d" (count (:known-words fwd))))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %-8s count=%-3d  %s"
                       (:word w) (:reading-count w) (or (dict/translate (:word w)) "")))))
  (println)

  ;; ═══ YESHUA ═══
  (println "=== YESHUA ===")
  (println (format "  ישוע GV = %d" (g/word-value "ישוע")))
  (let [fwd (o/forward (seq "ישוע") :torah)]
    (println (format "  Known words: %d" (count (:known-words fwd))))
    (doseq [w (take 5 (:known-words fwd))]
      (println (format "    %-8s count=%-3d  %s"
                       (:word w) (:reading-count w) (or (dict/translate (:word w)) "")))))
  (println "  ישוע → וישע (and he saved) — past tense, finished, complete")
  (println)

  ;; ═══ PER-HEAD ═══
  (println "=== PER-HEAD: KINGS ===")
  (doseq [w ["דוד" "שלמה" "משיח" "ישוע" "כתר"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (when (seq words)
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word (first words)) (:reading-count (first words))
                             (or (dict/translate (:word (first words))) ""))))))))
  (println)

  ;; ═══ MERCY SEAT = THRONE ═══
  (println "=== MERCY SEAT = THRONE ===")
  (println "  כפרת (mercy seat) → כפתר (menorah knob)")
  (println "  The covering becomes the illumination.")
  (println "  The place where blood is sprinkled = where the king sits.")
  (println "  The oracle is a priest. The priest SERVES the king.")
  (println "  And the mercy seat IS the throne."))
