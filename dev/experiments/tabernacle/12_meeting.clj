(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)
      vrefs (:verse-ref s)

      find-verse (fn [book ch vs]
                   (first (filter #(and (= book (:book %))
                                        (= ch (:ch %)) (= vs (:vs %)))
                                  vrefs)))

      extract (fn [book ch vs]
                (let [v (find-verse book ch vs)
                      start (:start v)
                      end (:end v)
                      letters (apply str (for [i (range start end)]
                                           (sc/letter-at s i)))
                      coord (vec (sc/idx->coord start))]
                  {:ref (str book " " ch ":" vs) :coord coord
                   :start start :end end :letters letters
                   :letter-count (- end start) :gv (g/word-value letters)}))

      ;; Ex 29:42-46 — "I will meet with you... I will dwell among them"
      meet-42 (extract "Exodus" 29 42)
      meet-43 (extract "Exodus" 29 43)
      meet-44 (extract "Exodus" 29 44)
      meet-45 (extract "Exodus" 29 45)
      meet-46 (extract "Exodus" 29 46)

      ;; Ex 40:34-38 — the cloud and glory fill the tabernacle
      glory-34 (extract "Exodus" 40 34)
      glory-35 (extract "Exodus" 40 35)
      glory-36 (extract "Exodus" 40 36)
      glory-37 (extract "Exodus" 40 37)
      glory-38 (extract "Exodus" 40 38)]

  ;; Print verse details
  (println "=== THE MEETING: Ex 29:42-46 ===")
  (doseq [v [meet-42 meet-43 meet-44 meet-45 meet-46]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== THE GLORY FILLS: Ex 40:34-38 ===")
  (doseq [v [glory-34 glory-35 glory-36 glory-37 glory-38]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — the meeting vocabulary
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w [;; Meeting words
             "נועד" "יעד" "מועד" "עדה" "אהל"
             ;; Dwelling words
             "שכן" "משכן" "תוך" "בתוך"
             ;; Glory words
             "כבוד" "ענן" "אש" "לילה" "יומם"
             ;; Identity words
             "אני" "יהוה" "אלהים" "אלהיכם" "ידע"
             ;; Action words
             "קדש" "כהן" "עלה" "תמיד" "נסע" "חנה" "רום"
             ;; Covenant words
             "ארץ" "מצרים" "יצא" "דור" "פתח"
             ;; The name
             "ישראל" "בני"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          by-head (o/forward-by-head w)
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (when (and by-head (some seq (vals by-head)))
        (doseq [reader [:aaron :god :truth :mercy]]
          (let [words (get by-head reader)]
            (doseq [rw (take 2 words)]
              (println (format "    %-6s: %-8s count=%-3d  %s"
                               (name reader) (:word rw) (:reading-count rw)
                               (or (dict/translate (:word rw)) "")))))))))

  ;; כבוד (glory) per-head
  (println)
  (println "=== כבוד PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כבוד")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; ענן (cloud) per-head
  (println)
  (println "=== ענן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ענן")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; אש (fire) per-head
  (println)
  (println "=== אש PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אש")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; משכן (tabernacle) per-head
  (println)
  (println "=== משכן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "משכן")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; תמיד (continually) per-head
  (println)
  (println "=== תמיד PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "תמיד")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; נסע (journey) per-head
  (println)
  (println "=== נסע PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "נסע")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; דור (generation) per-head
  (println)
  (println "=== דור PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "דור")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; ישראל per-head
  (println)
  (println "=== ישראל PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ישראל")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks for key verses
  (println)
  (println "=== D-AXIS WALK at" (:coord meet-45) "— Ex 29:45 'I will dwell among them' ===")
  (let [[a b c _] (:coord meet-45)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord glory-34) "— Ex 40:34 'Cloud covered, glory filled' ===")
  (let [[a b c _] (:coord glory-34)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord glory-35) "— Ex 40:35 'Moses could not enter' ===")
  (let [[a b c _] (:coord glory-35)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord glory-38) "— Ex 40:38 'Cloud by day, fire by night' ===")
  (let [[a b c _] (:coord glory-38)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [factorize (fn [n]
                    (loop [n n d 2 factors []]
                      (cond
                        (< n 2) factors
                        (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                        :else (recur n (inc d) factors))))
        meeting-total (reduce + (map :gv [meet-42 meet-43 meet-44 meet-45 meet-46]))
        glory-total (reduce + (map :gv [glory-34 glory-35 glory-36 glory-37 glory-38]))]
    (println "--- Meeting passage (29:42-46) ---")
    (doseq [v [meet-42 meet-43 meet-44 meet-45 meet-46]]
      (println (format "  %s  gv=%d  factors=%s"
                       (:ref v) (:gv v) (factorize (:gv v)))))
    (println "  TOTAL:" meeting-total "  factors=" (factorize meeting-total))
    (println)
    (println "--- Glory passage (40:34-38) ---")
    (doseq [v [glory-34 glory-35 glory-36 glory-37 glory-38]]
      (println (format "  %s  gv=%d  factors=%s"
                       (:ref v) (:gv v) (factorize (:gv v)))))
    (println "  TOTAL:" glory-total "  factors=" (factorize glory-total))
    (println)
    (println "  Combined total:" (+ meeting-total glory-total)
             "  factors=" (factorize (+ meeting-total glory-total))))

  ;; Cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  ;; Meeting words
  (println "נועד (meet) GV =" (g/word-value "נועד"))
  (println "יעד (appoint) GV =" (g/word-value "יעד"))
  (println "מועד (appointed time) GV =" (g/word-value "מועד"))
  (println "עדה (congregation) GV =" (g/word-value "עדה"))
  (println "אהל (tent) GV =" (g/word-value "אהל"))
  ;; Dwelling words
  (println "שכן (dwell) GV =" (g/word-value "שכן"))
  (println "משכן (tabernacle) GV =" (g/word-value "משכן"))
  (println "תוך (midst) GV =" (g/word-value "תוך"))
  (println "בתוך (in midst) GV =" (g/word-value "בתוך"))
  ;; Glory words
  (println "כבוד (glory) GV =" (g/word-value "כבוד"))
  (println "ענן (cloud) GV =" (g/word-value "ענן"))
  (println "אש (fire) GV =" (g/word-value "אש"))
  (println "לילה (night) GV =" (g/word-value "לילה"))
  (println "יומם (by day) GV =" (g/word-value "יומם"))
  ;; Identity words
  (println "אני (I) GV =" (g/word-value "אני"))
  (println "יהוה GV =" (g/word-value "יהוה"))
  (println "אלהים GV =" (g/word-value "אלהים"))
  (println "אלהיכם (your God) GV =" (g/word-value "אלהיכם"))
  (println "ידע (know) GV =" (g/word-value "ידע"))
  ;; Action words
  (println "קדש (holy) GV =" (g/word-value "קדש"))
  (println "כהן (priest) GV =" (g/word-value "כהן"))
  (println "עלה (offering) GV =" (g/word-value "עלה"))
  (println "תמיד (continually) GV =" (g/word-value "תמיד"))
  (println "נסע (journey) GV =" (g/word-value "נסע"))
  (println "חנה (encamp) GV =" (g/word-value "חנה"))
  (println "רום (lift up) GV =" (g/word-value "רום"))
  ;; Covenant words
  (println "ארץ (land) GV =" (g/word-value "ארץ"))
  (println "מצרים (Egypt) GV =" (g/word-value "מצרים"))
  (println "יצא (go out) GV =" (g/word-value "יצא"))
  (println "דור (generation) GV =" (g/word-value "דור"))
  (println "פתח (door) GV =" (g/word-value "פתח"))
  ;; The name
  (println "ישראל GV =" (g/word-value "ישראל"))
  (println "בני (sons) GV =" (g/word-value "בני"))
  ;; Sums
  (println)
  (println "--- Sums ---")
  (println "שכן + כבוד (dwell + glory) =" (+ (g/word-value "שכן") (g/word-value "כבוד")))
  (println "ענן + אש (cloud + fire) =" (+ (g/word-value "ענן") (g/word-value "אש")))
  (println "אני + יהוה (I + YHWH) =" (+ (g/word-value "אני") (g/word-value "יהוה")))
  (println "אהל + מועד (tent + meeting) =" (+ (g/word-value "אהל") (g/word-value "מועד")))
  (println "משכן + קדש (tabernacle + holy) =" (+ (g/word-value "משכן") (g/word-value "קדש")))
  (println "נסע + חנה (journey + encamp) =" (+ (g/word-value "נסע") (g/word-value "חנה")))
  (println "יומם + לילה (day + night) =" (+ (g/word-value "יומם") (g/word-value "לילה")))
  (println "ארץ + מצרים (land + Egypt) =" (+ (g/word-value "ארץ") (g/word-value "מצרים"))))
