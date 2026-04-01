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

      ;; Altar of Burnt Offering instruction: Ex 27:1-8
      instr-1 (extract "Exodus" 27 1)
      instr-2 (extract "Exodus" 27 2)
      instr-3 (extract "Exodus" 27 3)
      instr-4 (extract "Exodus" 27 4)
      instr-5 (extract "Exodus" 27 5)
      instr-6 (extract "Exodus" 27 6)
      instr-7 (extract "Exodus" 27 7)
      instr-8 (extract "Exodus" 27 8)

      ;; Altar built: Ex 38:1-7
      built-1 (extract "Exodus" 38 1)
      built-2 (extract "Exodus" 38 2)
      built-3 (extract "Exodus" 38 3)
      built-4 (extract "Exodus" 38 4)
      built-5 (extract "Exodus" 38 5)
      built-6 (extract "Exodus" 38 6)
      built-7 (extract "Exodus" 38 7)]

  ;; Print all verse details
  (println "=== ALTAR INSTRUCTION: Ex 27:1-8 ===")
  (doseq [v [instr-1 instr-2 instr-3 instr-4 instr-5 instr-6 instr-7 instr-8]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  (println)
  (println "=== ALTAR BUILT: Ex 38:1-7 ===")
  (doseq [v [built-1 built-2 built-3 built-4 built-5 built-6 built-7]]
    (println (format "  %s  coord=%s  gv=%d  letters=%d"
                     (:ref v) (:coord v) (:gv v) (:letter-count v)))
    (println (format "    %s" (:letters v))))

  ;; Word-by-word oracle — key altar words
  (println)
  (println "=== WORD-BY-WORD ORACLE ===")
  (doseq [w ["מזבח" "עלה" "קרן" "קרנת" "נחשת"
             "סיר" "יעה" "מזרק" "מזלג" "מחתה"
             "מכבר" "רשת" "טבעת" "בד" "עץ" "שטים"
             "חלל" "לחת" "ארבע" "רבוע" "חמש" "שלש"]]
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
          (let [words (get by-head reader)]
            (doseq [rw (take 2 words)]
              (println (format "    %-6s: %-8s count=%-3d  %s"
                               (name reader) (:word rw) (:reading-count rw)
                               (or (dict/translate (:word rw)) "")))))))))

  ;; The altar dimensions
  (println)
  (println "=== DIMENSIONS ===")
  (println "5 cubits long × 5 cubits wide × 3 cubits high")
  (println "  5 × 5 × 3 = 75")
  (println "  5 + 5 + 3 = 13 = love = אהבה = אחד")
  (println "  As letters: ה(5) + ה(5) + ג(3) = GV 13")
  (println "  Dims as letters spell: ההג")
  (println "  10,10,6 half-cubits → GV 26 = YHWH (from 095)")

  ;; Four horns
  (println)
  (println "=== THE FOUR HORNS ===")
  (let [by-head (o/forward-by-head "קרן")]
    (println "קרן (horn, GV=" (g/word-value "קרן") ")")
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-6s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; מזבח (altar) per-head detail
  (println)
  (println "=== מזבח PER-HEAD ===")
  (let [by-head (o/forward-by-head "מזבח")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; Altar implements
  (println)
  (println "=== IMPLEMENTS ===")
  (doseq [w ["סיר" "יעה" "מזרק" "מזלג" "מחתה"]]
    (let [by-head (o/forward-by-head w)
          bstep (try (basin/step w) (catch Exception _ nil))
          class (cond (nil? bstep) "invisible"
                      (:fixed? bstep) "FIXED"
                      :else (str "→ " (:next bstep)))]
      (println (format "\n  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [top (first (get by-head reader))]
          (when top
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word top) (:reading-count top)
                             (or (dict/translate (:word top)) ""))))))))

  ;; d-axis walks
  (println)
  (println "=== D-AXIS WALK at (2,34,7,*) — Altar instruction layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 34 7 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  (println)
  (println "=== D-AXIS WALK at (3,6,4,*) — Altar built layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 6 4 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Gematria totals
  (println)
  (println "=== GEMATRIA TOTALS ===")
  (let [instr-total (reduce + (map :gv [instr-1 instr-2 instr-3 instr-4 instr-5 instr-6 instr-7 instr-8]))
        built-total (reduce + (map :gv [built-1 built-2 built-3 built-4 built-5 built-6 built-7]))]
    (println "Instruction (27:1-8) total:" instr-total)
    (println "Built (38:1-7) total:" built-total)
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

  ;; עלה (burnt offering) per-head
  (println)
  (println "=== עלה PER-HEAD ===")
  (let [by-head (o/forward-by-head "עלה")]
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (get by-head reader)]
        (println (format "    %-8s count=%-3d gv=%-4d %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  ;; Key cross-references
  (println)
  (println "=== CROSS-REFERENCES ===")
  (println "מזבח GV=57. 57 = 3 × 19")
  (println "עלה GV=105. 105 = 3 × 5 × 7")
  (println "קרן GV=350. 350 = 2 × 5² × 7")
  (println "מזבח + עלה = 162 = 2 × 81 = 2 × 3⁴")
  (println "4 × קרן = 1400 = 2³ × 5² × 7")
  (println)
  (println "נחשת (bronze) GV=758 = 2 × 379")
  (println "שטים (acacia) GV=359")
  (let [bstep (try (basin/step "שטים") (catch Exception _ nil))]
    (println "שטים basin:" (cond (nil? bstep) "invisible"
                                  (:fixed? bstep) "FIXED"
                                  :else (str "→ " (:next bstep))))))
