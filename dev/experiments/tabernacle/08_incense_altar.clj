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

      ;; Incense altar instruction: Ex 30:1-10
      instr-1  (extract "Exodus" 30 1)
      instr-2  (extract "Exodus" 30 2)
      instr-3  (extract "Exodus" 30 3)
      instr-4  (extract "Exodus" 30 4)
      instr-5  (extract "Exodus" 30 5)
      instr-6  (extract "Exodus" 30 6)
      instr-7  (extract "Exodus" 30 7)
      instr-8  (extract "Exodus" 30 8)
      instr-9  (extract "Exodus" 30 9)
      instr-10 (extract "Exodus" 30 10)

      ;; Incense altar built: Ex 37:25-29
      built-25 (extract "Exodus" 37 25)
      built-26 (extract "Exodus" 37 26)
      built-27 (extract "Exodus" 37 27)
      built-28 (extract "Exodus" 37 28)
      built-29 (extract "Exodus" 37 29)]

  ;; Print verse details
  (println "=== INCENSE ALTAR INSTRUCTION: Ex 30:1-10 ===")
  (doseq [v [instr-1 instr-2 instr-3 instr-4 instr-5 instr-6 instr-7 instr-8 instr-9 instr-10]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== INCENSE ALTAR BUILT: Ex 37:25-29 ===")
  (doseq [v [built-25 built-26 built-27 built-28 built-29]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key incense altar words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["מזבח" "קטרת" "קטר" "סמים" "סם"
             "זהב" "טהור" "זר" "טבעת" "בד" "שטים"
             "אמה" "רבוע" "אמתים" "קרן" "קרנת"
             "עדת" "ארן" "פרכת" "כפר" "כפרת"
             "בקר" "ערב" "נר" "תמיד"
             "סלח" "נסך" "עלה" "מנחה" "חטאת"
             "שמן" "משחה" "קדש" "לבנה" "נטף" "שחלת" "חלבנה"]]
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

  ;; מזבח (altar) per-head detail
  (println)
  (println "=== מזבח PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מזבח")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; קטרת (incense) per-head
  (println)
  (println "=== קטרת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קטרת")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; קטר (burn incense) per-head
  (println)
  (println "=== קטר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קטר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; סמים (spices) per-head
  (println)
  (println "=== סמים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "סמים")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כפר (atone) per-head
  (println)
  (println "=== כפר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כפר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; פרכת (veil) per-head
  (println)
  (println "=== פרכת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פרכת")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; עדת (testimony) per-head
  (println)
  (println "=== עדת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "עדת")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; חטאת (sin offering) per-head
  (println)
  (println "=== חטאת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "חטאת")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; The four incense spices
  (println)
  (println "=== THE FOUR SPICES ===")
  (doseq [w ["נטף" "שחלת" "חלבנה" "לבנה"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (doseq [rw (take 2 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) ""))))))))

  ;; בקר (morning) and ערב (evening) per-head
  (println)
  (println "=== בקר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "בקר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== ערב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "ערב")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; anointing oil words
  (println)
  (println "=== שמן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שמן")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== משחה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "משחה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-1) "— Incense altar instruction layer ===")
  (let [[a b c _] (:coord instr-1)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord built-25) "— Incense altar built layer ===")
  (let [[a b c _] (:coord built-25)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-1 instr-2 instr-3 instr-4 instr-5 instr-6 instr-7 instr-8 instr-9 instr-10]))
        built-total (reduce + (map :gv [built-25 built-26 built-27 built-28 built-29]))]
    (println "Instruction (30:1-10) total:" instr-total)
    (println "Built (37:25-29) total:" built-total)
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
  (println "1 cubit long × 1 cubit wide × 2 cubits high")
  (println "  As half-cubits: 2, 2, 4")
  (println "  As letters: ב(2) + ב(2) + ד(4) = בבד")
  (println "  GV: 2 + 2 + 4 = 8")
  (println "  1 + 1 + 2 = 4")
  (println "  Square base: 1 × 1. Height: 2. Tallest furniture in the holy place.")
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "מזבח (altar) GV =" (g/word-value "מזבח"))
  (println "קטרת (incense) GV =" (g/word-value "קטרת"))
  (println "סמים (spices) GV =" (g/word-value "סמים"))
  (println "כפר (atone) GV =" (g/word-value "כפר"))
  (println "פרכת (veil) GV =" (g/word-value "פרכת"))
  (println "חטאת (sin offering) GV =" (g/word-value "חטאת"))
  (println "בקר (morning) GV =" (g/word-value "בקר"))
  (println "ערב (evening) GV =" (g/word-value "ערב"))
  (println "שמן (oil) GV =" (g/word-value "שמן"))
  (println "משחה (anointing) GV =" (g/word-value "משחה"))
  (println "קדש (holy) GV =" (g/word-value "קדש"))
  (println "נטף (stacte) GV =" (g/word-value "נטף"))
  (println "שחלת (onycha) GV =" (g/word-value "שחלת"))
  (println "חלבנה (galbanum) GV =" (g/word-value "חלבנה"))
  (println "לבנה (frankincense) GV =" (g/word-value "לבנה"))
  (println "קטרת + סמים =" (+ (g/word-value "קטרת") (g/word-value "סמים")))
  (println "מזבח + קטרת =" (+ (g/word-value "מזבח") (g/word-value "קטרת"))))
