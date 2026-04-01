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

      ;; Table instruction: Ex 25:23-30
      instr-23 (extract "Exodus" 25 23)
      instr-24 (extract "Exodus" 25 24)
      instr-25 (extract "Exodus" 25 25)
      instr-26 (extract "Exodus" 25 26)
      instr-27 (extract "Exodus" 25 27)
      instr-28 (extract "Exodus" 25 28)
      instr-29 (extract "Exodus" 25 29)
      instr-30 (extract "Exodus" 25 30)

      ;; Table built: Ex 37:10-16
      built-10 (extract "Exodus" 37 10)
      built-11 (extract "Exodus" 37 11)
      built-12 (extract "Exodus" 37 12)
      built-13 (extract "Exodus" 37 13)
      built-14 (extract "Exodus" 37 14)
      built-15 (extract "Exodus" 37 15)
      built-16 (extract "Exodus" 37 16)]

  ;; Print verse details
  (println "=== TABLE INSTRUCTION: Ex 25:23-30 ===")
  (doseq [v [instr-23 instr-24 instr-25 instr-26 instr-27 instr-28 instr-29 instr-30]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== TABLE BUILT: Ex 37:10-16 ===")
  (doseq [v [built-10 built-11 built-12 built-13 built-14 built-15 built-16]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key table words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["שלחן" "זהב" "טהור" "זר" "מסגרת" "טפח"
             "בד" "שטים" "קערה" "כף" "קשוה" "מנקית"
             "נסך" "לחם" "פנים" "תמיד"]]
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

  ;; שלחן per-head detail
  (println)
  (println "=== שלחן PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "שלחן")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; לחם (bread) per-head
  (println)
  (println "=== לחם PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "לחם")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; פנים (face/presence) per-head
  (println)
  (println "=== פנים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "פנים")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; טהור (pure) per-head
  (println)
  (println "=== טהור PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "טהור")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; The vessels
  (println)
  (println "=== TABLE VESSELS ===")
  (doseq [w ["קערה" "כף" "קשוה" "מנקית"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (doseq [rw (take 2 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) ""))))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at (2,31,4,*) — Table instruction layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 31 4 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  (println)
  (println "=== D-AXIS WALK at (3,5,2,*) — Table built layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 5 2 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-23 instr-24 instr-25 instr-26 instr-27 instr-28 instr-29 instr-30]))
        built-total (reduce + (map :gv [built-10 built-11 built-12 built-13 built-14 built-15 built-16]))]
    (println "Instruction (25:23-30) total:" instr-total)
    (println "Built (37:10-16) total:" built-total)
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
  (println "2 cubits long × 1 cubit wide × 1.5 cubits high")
  (println "  As half-cubits: 4, 2, 3")
  (println "  As letters: ד(4) + ב(2) + ג(3) = בגד (garment)")
  (println "  GV: 4 + 2 + 3 = 9")
  (println "  2 + 1 + 1.5 = 4.5")
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "שלחן GV =" (g/word-value "שלחן"))
  (println "לחם GV =" (g/word-value "לחם"))
  (println "פנים GV =" (g/word-value "פנים"))
  (println "לחם + פנים =" (+ (g/word-value "לחם") (g/word-value "פנים")))
  (println "שלחן + לחם =" (+ (g/word-value "שלחן") (g/word-value "לחם")))
  (println "טהור GV =" (g/word-value "טהור"))
  (println "זר GV =" (g/word-value "זר"))
  (println "נסך GV =" (g/word-value "נסך"))
  (println "תמיד GV =" (g/word-value "תמיד")))
