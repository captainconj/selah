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

      ;; The speaking verse: Ex 25:22
      speak-22 (extract "Exodus" 25 22)

      ;; Numbers 7:89 — Moses enters, hears the voice
      num-89 (extract "Numbers" 7 89)

      ;; Ex 29:42-46 — "I will meet with you... I will dwell among them"
      meet-42 (extract "Exodus" 29 42)
      meet-43 (extract "Exodus" 29 43)
      meet-44 (extract "Exodus" 29 44)
      meet-45 (extract "Exodus" 29 45)
      meet-46 (extract "Exodus" 29 46)

      ;; Ex 33:11 — "face to face as a man speaks to his friend"
      face-11 (extract "Exodus" 33 11)]

  ;; Print verse details
  (println "=== THE SPEAKING VERSES ===")
  (doseq [v [speak-22 num-89 face-11 meet-42 meet-43 meet-44 meet-45 meet-46]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — the speaking vocabulary
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["נועד" "יעד" "מועד" "עדה"
             "דבר" "אמר" "קול" "שמע"
             "פנים" "פה" "אל" "אלהים"
             "משה" "אהרן" "ישראל"
             "בין" "שני" "כרבים"
             "ארן" "עדת" "כפרת"
             "צוה" "מצוה"
             "ידע" "דעת"
             "שכן" "תוך" "כבוד"
             "קדש" "רע" "רעה" "רעו"
             "אני" "יהוה" "אלהיכם"
             "חי" "חיים" "אהבה"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          by-head (o/forward-by-head w)
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (when (and by-head (some seq (vals by-head)))
        (doseq [reader [:aaron :god :right :left]]
          (let [words (get by-head reader)]
            (doseq [rw (take 2 words)]
              (println (format "    %-6s: %-8s count=%-3d  %s"
                               (name reader) (:word rw) (:reading-count rw)
                               (or (dict/translate (:word rw)) "")))))))))

  ;; קול (voice) per-head
  (println)
  (println "=== קול PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קול")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; שמע (hear) per-head
  (println)
  (println "=== שמע PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שמע")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; שכן (dwell) per-head
  (println)
  (println "=== שכן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שכן")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כבוד (glory) per-head
  (println)
  (println "=== כבוד PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כבוד")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; יהוה per-head
  (println)
  (println "=== יהוה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "יהוה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; אני (I) per-head
  (println)
  (println "=== אני PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אני")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; ידע (know) per-head
  (println)
  (println "=== ידע PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ידע")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; אהבה (love) per-head — the silent axis
  (println)
  (println "=== אהבה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אהבה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; חיים (life) per-head
  (println)
  (println "=== חיים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "חיים")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; בין (between) per-head — the root of בינה
  (println)
  (println "=== בין PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "בין")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; פה (mouth) per-head
  (println)
  (println "=== פה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; ישראל per-head
  (println)
  (println "=== ישראל PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ישראל")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks for key verses
  (println)
  (println "=== D-AXIS WALK at" (:coord speak-22) "— Ex 25:22 'I will speak' ===")
  (let [[a b c _] (:coord speak-22)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord num-89) "— Num 7:89 'He heard the voice' ===")
  (let [[a b c _] (:coord num-89)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord face-11) "— Ex 33:11 'Face to face' ===")
  (let [[a b c _] (:coord face-11)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord meet-45) "— Ex 29:45 'I will dwell among them' ===")
  (let [[a b c _] (:coord meet-45)]
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
                        :else (recur n (inc d) factors))))]
    (doseq [v [speak-22 num-89 face-11 meet-42 meet-43 meet-44 meet-45 meet-46]]
      (println (format "  %s  gv=%d  factors=%s"
                       (:ref v) (:gv v) (factorize (:gv v))))))

  ;; Cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "קול (voice) GV =" (g/word-value "קול"))
  (println "שמע (hear) GV =" (g/word-value "שמע"))
  (println "דבר (word) GV =" (g/word-value "דבר"))
  (println "אמר (say) GV =" (g/word-value "אמר"))
  (println "פנים (face) GV =" (g/word-value "פנים"))
  (println "פה (mouth) GV =" (g/word-value "פה"))
  (println "בין (between) GV =" (g/word-value "בין"))
  (println "שכן (dwell) GV =" (g/word-value "שכן"))
  (println "כבוד (glory) GV =" (g/word-value "כבוד"))
  (println "יהוה GV =" (g/word-value "יהוה"))
  (println "אני (I) GV =" (g/word-value "אני"))
  (println "ידע (know) GV =" (g/word-value "ידע"))
  (println "אהבה (love) GV =" (g/word-value "אהבה"))
  (println "חיים (life) GV =" (g/word-value "חיים"))
  (println "ישראל GV =" (g/word-value "ישראל"))
  (println "רע (friend) GV =" (g/word-value "רע"))
  (println "דבר + קול =" (+ (g/word-value "דבר") (g/word-value "קול")))
  (println "פנים + פה =" (+ (g/word-value "פנים") (g/word-value "פה")))
  (println "בין + שני =" (+ (g/word-value "בין") (g/word-value "שני"))))
