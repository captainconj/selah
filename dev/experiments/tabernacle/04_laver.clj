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

      ;; Laver instruction: Ex 30:17-21
      instr-17 (extract "Exodus" 30 17)
      instr-18 (extract "Exodus" 30 18)
      instr-19 (extract "Exodus" 30 19)
      instr-20 (extract "Exodus" 30 20)
      instr-21 (extract "Exodus" 30 21)

      ;; Laver built: Ex 38:8 (just ONE verse!)
      built-8 (extract "Exodus" 38 8)]

  ;; Print all verse details
  (println "=== LAVER INSTRUCTION: Ex 30:17-21 ===")
  (doseq [v [instr-17 instr-18 instr-19 instr-20 instr-21]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== LAVER BUILT: Ex 38:8 ===")
  (println (format "  %s  coord=%s  gv=%d  letters=%d"
                   (:ref built-8) (:coord built-8) (:gv built-8) (:letter-count built-8)))
  (println (format "    %s" (:letters built-8)))

  ;; Word-by-word oracle — key laver words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["כיור" "כן" "נחשת" "רחץ" "מים" "יד" "רגל"
             "מות" "חק" "עולם" "זרע" "דור"
             "מראה" "צבא" "אשה" "פתח" "אהל" "מועד"]]
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

  ;; כיור per-head detail
  (println)
  (println "=== כיור PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "כיור")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; רחץ per-head detail
  (println)
  (println "=== רחץ PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "רחץ")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; מים per-head detail
  (println)
  (println "=== מים PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מים")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; מראה per-head (mirrors of the women)
  (println)
  (println "=== מראה PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מראה")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; מות per-head (death penalty for not washing)
  (println)
  (println "=== מות PER-HEAD (all) ===")
  (let [by-head (o/forward-by-head "מות")]
    (doseq [reader [:aaron :god :right :left]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at (2,42,3,*) — Laver instruction layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 42 3 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  (println)
  (println "=== D-AXIS WALK at (3,6,9,*) — Laver built layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 6 9 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Gematria
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-total (reduce + (map :gv [instr-17 instr-18 instr-19 instr-20 instr-21]))
        built-total (:gv built-8)]
    (println "Instruction (30:17-21) total:" instr-total)
    (println "Built (38:8) total:" built-total)
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

  ;; Key cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "כיור GV =" (g/word-value "כיור"))
  (println "כן GV =" (g/word-value "כן"))
  (println "כיור + כן =" (+ (g/word-value "כיור") (g/word-value "כן")))
  (println "רחץ GV =" (g/word-value "רחץ"))
  (println "מים GV =" (g/word-value "מים"))
  (println "רחץ + מים =" (+ (g/word-value "רחץ") (g/word-value "מים")))
  (println "מות GV =" (g/word-value "מות"))
  (println "מראה GV =" (g/word-value "מראה"))
  (println "יד + רגל =" (+ (g/word-value "יד") (g/word-value "רגל")))
  (println "יד GV =" (g/word-value "יד") "= 14 = gold")
  (println "רגל GV =" (g/word-value "רגל"))
  (println "עולם GV =" (g/word-value "עולם"))

  ;; The women's mirrors verse
  (println)
  (println "=== THE MIRRORS VERSE: Ex 38:8 ===")
  (println "ויעש את הכיור נחשת ואת כנו נחשת במראת הצבאת אשר צבאו פתח אהל מועד")
  (println "\"He made the laver of bronze and its base of bronze,")
  (println " from the mirrors of the women who served at the entrance of the tent of meeting.\"")
  (println)
  (println "The laver — the only piece made from mirrors.")
  (println "The women gave their mirrors. The washing vessel is made of seeing."))
