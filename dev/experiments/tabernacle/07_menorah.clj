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

      ;; Menorah instruction: Ex 25:31-40
      instr-31 (extract "Exodus" 25 31)
      instr-32 (extract "Exodus" 25 32)
      instr-33 (extract "Exodus" 25 33)
      instr-34 (extract "Exodus" 25 34)
      instr-35 (extract "Exodus" 25 35)
      instr-36 (extract "Exodus" 25 36)
      instr-37 (extract "Exodus" 25 37)
      instr-38 (extract "Exodus" 25 38)
      instr-39 (extract "Exodus" 25 39)
      instr-40 (extract "Exodus" 25 40)

      ;; Menorah built: Ex 37:17-24
      built-17 (extract "Exodus" 37 17)
      built-18 (extract "Exodus" 37 18)
      built-19 (extract "Exodus" 37 19)
      built-20 (extract "Exodus" 37 20)
      built-21 (extract "Exodus" 37 21)
      built-22 (extract "Exodus" 37 22)
      built-23 (extract "Exodus" 37 23)
      built-24 (extract "Exodus" 37 24)]

  ;; Print verse details
  (println "=== MENORAH INSTRUCTION: Ex 25:31-40 ===")
  (doseq [v [instr-31 instr-32 instr-33 instr-34 instr-35 instr-36 instr-37 instr-38 instr-39 instr-40]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== MENORAH BUILT: Ex 37:17-24 ===")
  (doseq [v [built-17 built-18 built-19 built-20 built-21 built-22 built-23 built-24]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key menorah words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["מנרה" "מנרת" "נר" "נרת" "זהב" "טהור" "מקשה"
             "ירך" "קנה" "גביע" "כפתר" "פרח" "שקד" "משקד"
             "שלשה" "ארבעה" "ששה" "שבעה"
             "אור" "נכח" "פנים" "ככר" "כלי"]]
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

  ;; מנרה (menorah) per-head detail
  (println)
  (println "=== מנרה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מנרה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; קנה (branch/reed) per-head
  (println)
  (println "=== קנה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "קנה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; גביע (cup/calyx) per-head
  (println)
  (println "=== גביע PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "גביע")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; כפתר (knob/capital) per-head
  (println)
  (println "=== כפתר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כפתר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; פרח (flower) per-head
  (println)
  (println "=== פרח PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פרח")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; שקד (almond) per-head
  (println)
  (println "=== שקד PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שקד")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; אור (light) per-head
  (println)
  (println "=== אור PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "אור")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; מקשה (hammered work) per-head
  (println)
  (println "=== מקשה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מקשה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; שבעה (seven) per-head
  (println)
  (println "=== שבעה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שבעה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at" (:coord instr-31) "— Menorah instruction layer ===")
  (let [[a b c _] (:coord instr-31)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  (println)
  (println "=== D-AXIS WALK at" (:coord built-17) "— Menorah built layer ===")
  (let [[a b c _] (:coord built-17)]
    (doseq [d (range 67)]
      (let [idx (sc/coord->idx a b c d)
            letter (sc/letter-at s idx)]
        (print letter))))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-31 instr-32 instr-33 instr-34 instr-35 instr-36 instr-37 instr-38 instr-39 instr-40]))
        built-total (reduce + (map :gv [built-17 built-18 built-19 built-20 built-21 built-22 built-23 built-24]))]
    (println "Instruction (25:31-40) total:" instr-total)
    (println "Built (37:17-24) total:" built-total)
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
  (println "One talent of pure gold (ככר זהב טהור)")
  (println "  ככר (talent) GV =" (g/word-value "ככר"))
  (println "  ככר + זהב + טהור =" (+ (g/word-value "ככר") (g/word-value "זהב") (g/word-value "טהור")))
  (println "  Seven lamps. Six branches + central shaft.")
  (println "  6 + 1 = 7. Three branches each side.")
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "מנרה GV =" (g/word-value "מנרה"))
  (println "נר (lamp) GV =" (g/word-value "נר"))
  (println "אור (light) GV =" (g/word-value "אור"))
  (println "קנה (branch) GV =" (g/word-value "קנה"))
  (println "גביע (cup) GV =" (g/word-value "גביע"))
  (println "כפתר (knob) GV =" (g/word-value "כפתר"))
  (println "פרח (flower) GV =" (g/word-value "פרח"))
  (println "שקד (almond) GV =" (g/word-value "שקד"))
  (println "מקשה (hammered) GV =" (g/word-value "מקשה"))
  (println "שבעה (seven) GV =" (g/word-value "שבעה"))
  (println "ככר (talent) GV =" (g/word-value "ככר"))
  (println "שמן (oil) GV =" (g/word-value "שמן"))
  (println "זית (olive) GV =" (g/word-value "זית"))
  (println "מנרה + נר =" (+ (g/word-value "מנרה") (g/word-value "נר")))
  (println "אור + נר =" (+ (g/word-value "אור") (g/word-value "נר")))
  (println "שבעה + נר =" (+ (g/word-value "שבעה") (g/word-value "נר"))))
