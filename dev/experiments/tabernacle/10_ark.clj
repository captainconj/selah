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

      ;; Ark instruction: Ex 25:10-22
      instr-10 (extract "Exodus" 25 10)
      instr-11 (extract "Exodus" 25 11)
      instr-12 (extract "Exodus" 25 12)
      instr-13 (extract "Exodus" 25 13)
      instr-14 (extract "Exodus" 25 14)
      instr-15 (extract "Exodus" 25 15)
      instr-16 (extract "Exodus" 25 16)
      instr-17 (extract "Exodus" 25 17)
      instr-18 (extract "Exodus" 25 18)
      instr-19 (extract "Exodus" 25 19)
      instr-20 (extract "Exodus" 25 20)
      instr-21 (extract "Exodus" 25 21)
      instr-22 (extract "Exodus" 25 22)

      ;; Ark built: Ex 37:1-9
      built-1 (extract "Exodus" 37 1)
      built-2 (extract "Exodus" 37 2)
      built-3 (extract "Exodus" 37 3)
      built-4 (extract "Exodus" 37 4)
      built-5 (extract "Exodus" 37 5)
      built-6 (extract "Exodus" 37 6)
      built-7 (extract "Exodus" 37 7)
      built-8 (extract "Exodus" 37 8)
      built-9 (extract "Exodus" 37 9)]

  ;; Print verse details
  (println "=== ARK INSTRUCTION: Ex 25:10-22 ===")
  (doseq [v [instr-10 instr-11 instr-12 instr-13 instr-14 instr-15 instr-16
             instr-17 instr-18 instr-19 instr-20 instr-21 instr-22]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== ARK BUILT: Ex 37:1-9 ===")
  (doseq [v [built-1 built-2 built-3 built-4 built-5 built-6 built-7 built-8 built-9]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key Ark words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["ארון" "ארן" "עדת" "עדות"
             "זהב" "טהור" "זר"
             "טבעת" "פעם" "רגל"
             "בד" "שטים" "נשא"
             "כפרת" "כרוב" "כרבים" "כנף"
             "סכך" "פנים" "איש" "אח" "אחיו"
             "נועד" "דבר" "צוה" "מצוה"
             "לוח" "אבן" "עדות" "תורה"
             "מן" "אהרן" "משה"
             "שמה" "תוך" "בית"
             "קדש" "קדשים"]]
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

  ;; ארון (ark) per-head detail
  (println)
  (println "=== ארון PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ארון")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כפרת (mercy seat) per-head
  (println)
  (println "=== כפרת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כפרת")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כרוב (cherub) per-head
  (println)
  (println "=== כרוב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כרוב")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כנף (wing) per-head
  (println)
  (println "=== כנף PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כנף")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; סכך (cover/overshadow) per-head
  (println)
  (println "=== סכך PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "סכך")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; פנים (face) per-head — THE word
  (println)
  (println "=== פנים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פנים")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; איש (man) and אח (brother) — "faces one toward another"
  (println)
  (println "=== איש PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "איש")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== אחיו PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אחיו")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; דבר (speak) and נועד (meet/appoint) per-head
  (println)
  (println "=== דבר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "דבר")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== נועד PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "נועד")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; צוה (command) per-head
  (println)
  (println "=== צוה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "צוה")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; לוח (tablet) per-head
  (println)
  (println "=== לוח PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "לוח")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; נשא (carry) per-head
  (println)
  (println "=== נשא PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "נשא")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; משה (Moses) and אהרן (Aaron) per-head
  (println)
  (println "=== משה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "משה")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== אהרן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אהרן")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-10) "— Ark instruction layer ===")
  (let [[a b c _] (:coord instr-10)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord built-1) "— Ark built layer ===")
  (let [[a b c _] (:coord built-1)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Also walk the mercy seat instruction verse
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-17) "— Mercy seat instruction ===")
  (let [[a b c _] (:coord instr-17)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; The speaking verse (25:22)
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-22) "— 'I will speak with you' ===")
  (let [[a b c _] (:coord instr-22)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-10 instr-11 instr-12 instr-13 instr-14 instr-15 instr-16
                                         instr-17 instr-18 instr-19 instr-20 instr-21 instr-22]))
        built-total (reduce + (map :gv [built-1 built-2 built-3 built-4 built-5 built-6 built-7 built-8 built-9]))]
    (println "Instruction (25:10-22) total:" instr-total)
    (println "Built (37:1-9) total:" built-total)
    (println "Sum:" (+ instr-total built-total))
    (let [factorize (fn [n]
                      (loop [n n d 2 factors []]
                        (cond
                          (< n 2) factors
                          (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                          :else (recur n (inc d) factors))))]
      (println "Instr factors:" (factorize instr-total))
      (println "Built factors:" (factorize built-total))
      (println "Sum factors:" (factorize (+ instr-total built-total)))))

  ;; Dimensions and cross-references
  (println)
  (println "=== DIMENSIONS ===")
  (println "Ark: 2.5 cubits long × 1.5 cubits wide × 1.5 cubits high")
  (println "  As half-cubits: 5, 3, 3")
  (println "  As letters: ה(5) + ג(3) + ג(3) = הגג")
  (println "  GV: 5 + 3 + 3 = 11")
  (println "  2.5 + 1.5 + 1.5 = 5.5")
  (println)
  (println "Mercy seat: 2.5 cubits long × 1.5 cubits wide")
  (println "  As half-cubits: 5, 3")
  (println "  As letters: ה(5) + ג(3) = הג / חג")
  (println "  GV: 5 + 3 = 8")
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "ארון (ark) GV =" (g/word-value "ארון"))
  (println "ארן (ark short) GV =" (g/word-value "ארן"))
  (println "כפרת (mercy seat) GV =" (g/word-value "כפרת"))
  (println "כרוב (cherub) GV =" (g/word-value "כרוב"))
  (println "כנף (wing) GV =" (g/word-value "כנף"))
  (println "עדות (testimony) GV =" (g/word-value "עדות"))
  (println "עדת GV =" (g/word-value "עדת"))
  (println "לוח (tablet) GV =" (g/word-value "לוח"))
  (println "דבר (speak/word) GV =" (g/word-value "דבר"))
  (println "פנים (face) GV =" (g/word-value "פנים"))
  (println "איש (man) GV =" (g/word-value "איש"))
  (println "אחיו (his brother) GV =" (g/word-value "אחיו"))
  (println "נועד (meet) GV =" (g/word-value "נועד"))
  (println "צוה (command) GV =" (g/word-value "צוה"))
  (println "משה (Moses) GV =" (g/word-value "משה"))
  (println "אהרן (Aaron) GV =" (g/word-value "אהרן"))
  (println "נשא (carry) GV =" (g/word-value "נשא"))
  (println "סכך (cover) GV =" (g/word-value "סכך"))
  (println "ארון + כפרת =" (+ (g/word-value "ארון") (g/word-value "כפרת")))
  (println "כרוב + כרוב =" (* 2 (g/word-value "כרוב")))
  (println "ארון + עדות =" (+ (g/word-value "ארון") (g/word-value "עדות")))
  (println "פנים + איש + אחיו =" (+ (g/word-value "פנים") (g/word-value "איש") (g/word-value "אחיו"))))
