;; Ark Walk 08 — Gopher Wood (Oracle Analysis)
;; Run every Ark word through the oracle. Classify: fixed, transform, ghost, invisible.
;; Gopher wood is ghost zone. Verbs speak. Nouns hide.
;; עצי גפר (wood of gopher, GV=453) = להחית (to keep alive, GV=453)

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      ;; All unique words on the three Ark floors
      ark-words
      ;; Foundation (c=7)
      ["משחיתם" "את" "הארץ" "עשה" "לך" "תבת" "עצי" "גפר"
       "קנים" "תעשה" "התבה" "וכפרת" "אתה" "מבית" "ומחוץ" "בכפר"
       ;; Specification (c=8)
       "שלש" "מאות" "אמה" "ארך" "חמשים" "רחבה" "ושלשים" "קומתה"
       "צהר" "לתבה" "ואל"
       ;; Directory (c=9)
       "תכלנה" "מלמעלה" "ופתח" "בצדה" "תשים" "תחתים" "שנים"
       "ואני" "הנני" "מביא" "המבול"
       ;; Covenant (c=10)
       "מים" "לשחת" "כל" "בשר" "אשר" "רוח" "חיים" "השמים"
       "בארץ" "יגוע" "והקמתי" "בריתי"
       ;; Boarding (c=11)
       "ובאת" "ובניך" "ואשתך" "ונשי" "בניך" "אתך" "ומכל" "החי"
       "תביא" "להחית"
       ;; Taxonomy (c=12)
       "חית" "זכר" "ונקבה" "יהיו" "עוף" "למינהו" "הבהמה" "למינה"
       "רמש" "האדמה" "מכל"]

      ;; Classify each word
      classify (fn [w]
                 (try
                   (let [fwd (o/forward (seq w) :torah)
                         known (:known-words fwd)
                         readings (:readings fwd)
                         illum-count (count (:illumination-sets fwd))]
                     (cond
                       (zero? illum-count) :invisible
                       (empty? known) :ghost
                       (= w (:word (first known))) :fixed
                       :else :transform))
                   (catch Exception _ :error)))]

  ;; ═══ FULL CLASSIFICATION ═══
  (println "=== ARK WORD CLASSIFICATION ===")
  (println)
  (let [classified (map (fn [w]
                          (let [cls (classify w)
                                gv (g/word-value w)
                                top (when (#{:fixed :transform} cls)
                                      (try (:word (first (:known-words (o/forward (seq w) :torah))))
                                           (catch Exception _ nil)))]
                            {:word w :gv gv :class cls :top top}))
                        (distinct ark-words))
        by-class (group-by :class classified)]

    ;; Fixed points
    (println "--- FIXED POINTS (know their name) ---")
    (doseq [entry (sort-by :gv (get by-class :fixed))]
      (println (format "  %-10s gv=%-4d  %s"
                       (:word entry) (:gv entry) (or (dict/translate (:word entry)) ""))))
    (println)

    ;; Transforms
    (println "--- TRANSFORMS (become another word) ---")
    (doseq [entry (sort-by :gv (get by-class :transform))]
      (println (format "  %-10s gv=%-4d → %-10s  %s → %s"
                       (:word entry) (:gv entry) (:top entry)
                       (or (dict/translate (:word entry)) "")
                       (or (dict/translate (str (:top entry))) ""))))
    (println)

    ;; Ghost zone
    (println "--- GHOST ZONE (glow but cannot be named) ---")
    (doseq [entry (sort-by :gv (get by-class :ghost))]
      (println (format "  %-10s gv=%-4d  %s"
                       (:word entry) (:gv entry) (or (dict/translate (:word entry)) ""))))
    (println)

    ;; Invisible
    (println "--- INVISIBLE (zero illumination) ---")
    (doseq [entry (sort-by :gv (get by-class :invisible))]
      (println (format "  %-10s gv=%-4d  %s"
                       (:word entry) (:gv entry) (or (dict/translate (:word entry)) ""))))
    (println)

    ;; Summary
    (println "--- SUMMARY ---")
    (doseq [cls [:fixed :transform :ghost :invisible :error]]
      (let [n (count (get by-class cls))]
        (when (pos? n)
          (println (format "  %-10s: %d words" (name cls) n))))))
  (println)

  ;; ═══ GEMATRIA BRIDGE ═══
  (println "=== GEMATRIA BRIDGE ===")
  (println (format "  עצי גפר (wood of gopher) GV = %d" (+ (g/word-value "עצי") (g/word-value "גפר"))))
  (println (format "  להחית (to keep alive) GV = %d" (g/word-value "להחית")))
  (println "  → The material IS the purpose")
  (println)

  ;; ═══ FINAL-FORM BLINDNESS ═══
  (println "=== FINAL-FORM BLINDNESS ===")
  (println "  Every invisible word contains a final-form letter:")
  (doseq [w ["הארץ" "לך" "תבת" "ומחוץ" "בארץ"]]
    (println (format "    %s" w)))
  (println "  → The breastplate speaks in medial forms, never at the end"))
