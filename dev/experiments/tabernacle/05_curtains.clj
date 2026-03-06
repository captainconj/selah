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

      ;; Curtains instruction: Ex 26:1-14 (inner curtains + goat hair + ram/badger coverings)
      ;; Key verses: 26:1 (inner curtains), 26:2 (dimensions), 26:7 (goat hair), 26:14 (ram/badger)
      instr-1  (extract "Exodus" 26 1)
      instr-2  (extract "Exodus" 26 2)
      instr-3  (extract "Exodus" 26 3)
      instr-6  (extract "Exodus" 26 6)
      instr-7  (extract "Exodus" 26 7)
      instr-14 (extract "Exodus" 26 14)

      ;; Curtains built: Ex 36:8-19
      built-8  (extract "Exodus" 36 8)
      built-9  (extract "Exodus" 36 9)
      built-13 (extract "Exodus" 36 13)
      built-14 (extract "Exodus" 36 14)
      built-19 (extract "Exodus" 36 19)]

  ;; Print verse details
  (println "=== CURTAINS INSTRUCTION ===")
  (doseq [v [instr-1 instr-2 instr-3 instr-6 instr-7 instr-14]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== CURTAINS BUILT ===")
  (doseq [v [built-8 built-9 built-13 built-14 built-19]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key curtain words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["משכן" "יריעה" "יריעת" "כרוב" "כרבים"
             "תכלת" "ארגמן" "שני" "שש" "משזר"
             "חבר" "קרס" "זהב" "אהל" "עז"
             "איל" "תחש" "עור" "אדם"
             "לאה" "חשב" "חכם" "לב"
             "עשר" "חמש" "ארבע" "שלשים" "אמה" "אחד"
             "קרסי" "לולאת"]]
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

  ;; כרוב (cherub) per-head
  (println)
  (println "=== כרוב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כרוב")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; חבר (join/couple) per-head
  (println)
  (println "=== חבר PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "חבר")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; חשב (skillful/think) per-head
  (println)
  (println "=== חשב PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "חשב")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; Layer materials oracle
  (println)
  (println "=== THE FOUR LAYERS ===")
  (doseq [w ["שש" "עז" "איל" "תחש"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :right :left]]
        (let [top (first (get by-head reader))]
          (when top
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word top) (:reading-count top)
                             (or (dict/translate (:word top)) ""))))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at (2,32,3,*) — Curtains instruction layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 32 3 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  (println)
  (println "=== D-AXIS WALK at (3,2,11,*) — Curtains built layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 2 11 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (println "Ex 26:1 GV =" (:gv instr-1))
  (println "Ex 36:8 GV =" (:gv built-8))
  (let [factorize (fn [n]
                    (loop [n n d 2 factors []]
                      (cond
                        (< n 2) factors
                        (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                        :else (recur n (inc d) factors))))]
    (println "26:1 factors:" (factorize (:gv instr-1)))
    (println "36:8 factors:" (factorize (:gv built-8))))

  ;; Dimensions
  (println)
  (println "=== DIMENSIONS ===")
  (println "Inner curtains: 28 × 4 cubits each, 10 curtains")
  (println "  28 = 7 × 4. 4 = letters in YHWH")
  (println "  10 curtains = 10 commandments")
  (println "  28 × 4 × 10 = 1,120 sq cubits total")
  (println "  1120 = 2⁵ × 5 × 7")
  (println "  28 + 4 = 32 = לב (heart)")
  (println "  GV of לב =" (g/word-value "לב"))

  ;; Cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "משכן (tabernacle) GV =" (g/word-value "משכן"))
  (println "כרוב (cherub) GV =" (g/word-value "כרוב"))
  (println "חבר (join) GV =" (g/word-value "חבר"))
  (println "חשב (think) GV =" (g/word-value "חשב"))
  (println "אחד (one) GV =" (g/word-value "אחד"))
  (println "קרס (clasp) GV =" (g/word-value "קרס"))
  (println "עשר (ten) GV =" (g/word-value "עשר"))
  (println "50 gold clasps: 50 = jubilee axis")
  (println "50 bronze clasps for goat-hair"))
