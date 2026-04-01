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

      ;; Courtyard instruction: Ex 27:9-19 (full description)
      ;; Key verses: 27:9 (south side), 27:11 (north), 27:12 (west), 27:13 (east), 27:18 (dimensions)
      court-9  (extract "Exodus" 27 9)
      court-10 (extract "Exodus" 27 10)
      court-11 (extract "Exodus" 27 11)
      court-12 (extract "Exodus" 27 12)
      court-13 (extract "Exodus" 27 13)
      court-14 (extract "Exodus" 27 14)
      court-18 (extract "Exodus" 27 18)
      court-19 (extract "Exodus" 27 19)

      ;; Courtyard built: Ex 38:9-20
      built-9  (extract "Exodus" 38 9)
      built-10 (extract "Exodus" 38 10)
      built-11 (extract "Exodus" 38 11)
      built-12 (extract "Exodus" 38 12)
      built-13 (extract "Exodus" 38 13)
      built-17 (extract "Exodus" 38 17)
      built-20 (extract "Exodus" 38 20)]

  ;; Print verse details
  (println "=== COURTYARD INSTRUCTION VERSES ===")
  (doseq [v [court-9 court-10 court-11 court-12 court-13 court-14 court-18 court-19]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== COURTYARD BUILT VERSES ===")
  (doseq [v [built-9 built-10 built-11 built-12 built-13 built-17 built-20]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle for key courtyard words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["חצר" "משכן" "קלעים" "קלע" "עמוד" "אדן" "נחשת" "כסף"
             "נגב" "תימנה" "צפון" "ים" "מזרח" "קדמה"
             "מאה" "חמשים" "ארך" "רחב" "קומה" "חמש"]]
    (let [fwd (o/forward w)
          known (:known-words fwd)
          top (first known)
          bstep (try (basin/step w) (catch Exception _ nil))
          by-head (o/forward-by-head w)
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (when (and by-head (some seq (vals by-head)))
        (doseq [reader [:aaron :god :truth :mercy]]
          (let [top-r (first (get by-head reader))]
            (when top-r
              (println (format "    %-6s: %-8s count=%-3d  %s"
                               (name reader) (:word top-r) (:reading-count top-r)
                               (or (dict/translate (:word top-r)) "")))))))))

  ;; d-axis walk at instruction layer (2,34,12,*)
  (println)
  (println "=== D-AXIS WALK at (2,34,12,*) — Courtyard instruction c=12 layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 34 12 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; d-axis walk at built layer (3,6,10,*)
  (println)
  (println "=== D-AXIS WALK at (3,6,10,*) — Courtyard built c=10 layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 6 10 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Gematria totals for the full courtyard description
  (println)
  (println "=== GEMATRIA ===")
  (let [instr-gv (reduce + (map :gv [court-9 court-10 court-11 court-12 court-13 court-14 court-18 court-19]))
        built-gv (reduce + (map :gv [built-9 built-10 built-11 built-12 built-13 built-17 built-20]))]
    (println "Courtyard instruction (27:9-14,18-19) total GV:" instr-gv)
    (println "Courtyard built (38:9-13,17,20) total GV:" built-gv)
    (println "Sum:" (+ instr-gv built-gv)))

  ;; Key verse: 27:18 has the dimensions
  (println)
  (println "=== DIMENSIONS VERSE: Ex 27:18 ===")
  (println "  " (:letters court-18))
  (println "  GV:" (:gv court-18) " coord:" (:coord court-18))
  (println "  100 cubits long × 50 wide × 5 high")
  (println "  100 = מאה, 50 = jubilee axis, 5 = ה")
  (println "  100 × 50 × 5 = 25,000")
  (println "  100 + 50 + 5 = 155")
  (println "  As letters: ק (100) + נ (50) + ה (5) = קנה (acquire/reed)")
  (println "  GV of קנה =" (g/word-value "קנה"))

  ;; Factorize helper
  (let [factorize (fn [n]
                    (loop [n n d 2 factors []]
                      (cond
                        (< n 2) factors
                        (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                        :else (recur n (inc d) factors))))]
    (println)
    (println "=== FACTORIZATIONS ===")
    (doseq [v [court-9 court-18 built-9]]
      (println (format "  %s GV=%d = %s" (:ref v) (:gv v) (factorize (:gv v))))))

  ;; The four directions oracle
  (println)
  (println "=== THE FOUR DIRECTIONS ===")
  (doseq [w ["נגב" "צפון" "ים" "מזרח" "קדמה" "תימנה"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [top-r (first (get by-head reader))]
          (when top-r
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word top-r) (:reading-count top-r)
                             (or (dict/translate (:word top-r)) ""))))))))

  ;; Silver and bronze
  (println)
  (println "=== METALS ===")
  (doseq [w ["כסף" "נחשת" "זהב"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [top-r (first (get by-head reader))]
          (when top-r
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word top-r) (:reading-count top-r)
                             (or (dict/translate (:word top-r)) "")))))))))
