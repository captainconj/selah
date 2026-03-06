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

      ;; Veil instruction: Ex 26:31-35
      instr-31 (extract "Exodus" 26 31)
      instr-32 (extract "Exodus" 26 32)
      instr-33 (extract "Exodus" 26 33)
      instr-34 (extract "Exodus" 26 34)
      instr-35 (extract "Exodus" 26 35)

      ;; Veil built: Ex 36:35-36
      built-35 (extract "Exodus" 36 35)
      built-36 (extract "Exodus" 36 36)]

  ;; Print verse details
  (println "=== VEIL INSTRUCTION: Ex 26:31-35 ===")
  (doseq [v [instr-31 instr-32 instr-33 instr-34 instr-35]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== VEIL BUILT: Ex 36:35-36 ===")
  (doseq [v [built-35 built-36]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key veil words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["פרכת" "כרוב" "כרבים" "תכלת" "ארגמן" "שני" "שש" "משזר"
             "חשב" "עמוד" "זהב" "כסף" "אדן" "ארן" "עדת"
             "שלחן" "מנרה" "קדש" "קדשים" "בדל"
             "נתן" "טבעת" "וו" "ציפה" "כפרת"
             "פנים" "אחר" "בית" "תוך"]]
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

  ;; פרכת (veil) per-head detail
  (println)
  (println "=== פרכת PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פרכת")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כרוב (cherub) per-head — woven into the veil
  (println)
  (println "=== כרוב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כרוב")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; בדל (separate/divide) per-head
  (println)
  (println "=== בדל PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "בדל")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; נתן (give/place) per-head
  (println)
  (println "=== נתן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "נתן")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; אדן (base/socket) per-head
  (println)
  (println "=== אדן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אדן")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; חשב (think/design) per-head — the word that flips behind the veil
  (println)
  (println "=== חשב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "חשב")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; קדש and קדשים per-head
  (println)
  (println "=== קדש PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קדש")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== קדשים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קדשים")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כסף (silver) per-head
  (println)
  (println "=== כסף PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כסף")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; The veil's function words
  (println)
  (println "=== אחר (behind/after) PER-HEAD ===")
  (let [by-head (o/forward-by-head "אחר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== בית (house) PER-HEAD ===")
  (let [by-head (o/forward-by-head "בית")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-31) "— Veil instruction layer ===")
  (let [[a b c _] (:coord instr-31)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord built-35) "— Veil built layer ===")
  (let [[a b c _] (:coord built-35)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-31 instr-32 instr-33 instr-34 instr-35]))
        built-total (reduce + (map :gv [built-35 built-36]))]
    (println "Instruction (26:31-35) total:" instr-total)
    (println "Built (36:35-36) total:" built-total)
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

  ;; Cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "פרכת (veil) GV =" (g/word-value "פרכת"))
  (println "כפרת (mercy seat) GV =" (g/word-value "כפרת"))
  (println "כפתר (knob) GV =" (g/word-value "כפתר"))
  (println "כפר (atone) GV =" (g/word-value "כפר"))
  (println "בדל (separate) GV =" (g/word-value "בדל"))
  (println "נתן (give) GV =" (g/word-value "נתן"))
  (println "אדן (base) GV =" (g/word-value "אדן"))
  (println "כסף (silver) GV =" (g/word-value "כסף"))
  (println "חשב (think) GV =" (g/word-value "חשב"))
  (println "קדש (holy) GV =" (g/word-value "קדש"))
  (println "קדשים GV =" (g/word-value "קדשים"))
  (println "פנים (face) GV =" (g/word-value "פנים"))
  (println "פרכת + כפרת =" (+ (g/word-value "פרכת") (g/word-value "כפרת")))
  (println "פרכת - כפר =" (- (g/word-value "פרכת") (g/word-value "כפר")))
  (println "קדש + קדשים =" (+ (g/word-value "קדש") (g/word-value "קדשים"))))
