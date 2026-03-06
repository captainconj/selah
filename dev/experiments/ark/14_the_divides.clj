;; Ark Walk 14 — The Divides
;; Patriarchal pairs: loved/hated, chosen/passed over
;; Snake (נחש=358) = Messiah (משיח=358)
;; Anti-diagonal: enemy (19) ↔ loved (14=David)

(require '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(let [factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))]

  ;; ═══ PATRIARCHAL PAIRS ═══
  (println "=== PATRIARCHAL PAIRS ===")
  (println)
  (doseq [[a b label] [["שרה" "הגר" "Sarah / Hagar"]
                        ["יצחק" "ישמעאל" "Isaac / Ishmael"]
                        ["יעקב" "עשו" "Jacob / Esau"]
                        ["לאה" "רחל" "Leah / Rachel"]
                        ["יוסף" "בנימין" "Joseph / Benjamin"]
                        ["משה" "אהרן" "Moses / Aaron"]
                        ["דוד" "שאול" "David / Saul"]
                        ["רות" "בעז" "Ruth / Boaz"]]]
    (let [gv-a (g/word-value a)
          gv-b (g/word-value b)
          step-a (try (basin/step a) (catch Exception _ nil))
          step-b (try (basin/step b) (catch Exception _ nil))
          cls-a (cond (nil? step-a) "invisible"
                      (:fixed? step-a) "FIXED"
                      :else (str "→" (:next step-a)))
          cls-b (cond (nil? step-b) "invisible"
                      (:fixed? step-b) "FIXED"
                      :else (str "→" (:next step-b)))]
      (println (format "  %s" label))
      (println (format "    %-8s gv=%-4d  %s" a gv-a cls-a))
      (println (format "    %-8s gv=%-4d  %s" b gv-b cls-b))
      (println)))

  ;; ═══ SNAKE = MESSIAH ═══
  (println "=== SNAKE = MESSIAH ===")
  (println (format "  נחש (serpent) GV = %d" (g/word-value "נחש")))
  (println (format "  משיח (messiah) GV = %d" (g/word-value "משיח")))
  (println (format "  Equal? %s" (= (g/word-value "נחש") (g/word-value "משיח"))))
  (println)
  (println "  Basin dynamics:")
  (doseq [w ["נחש" "משיח"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          cls (cond (nil? bstep) "invisible"
                    (:fixed? bstep) "FIXED"
                    :else (str "→ " (:next bstep)))]
      (println (format "    %-8s %s" w cls))))
  (println)

  ;; ═══ PRIMORDIAL PAIRS ═══
  (println "=== PRIMORDIAL PAIRS ===")
  (doseq [[a b label] [["אדם" "חוה" "Adam / Eve"]
                        ["קין" "הבל" "Cain / Abel"]
                        ["אלהים" "אדם" "God / Adam"]]]
    (println (format "  %s: %s(%d) / %s(%d)  sum=%d"
                     label a (g/word-value a) b (g/word-value b)
                     (+ (g/word-value a) (g/word-value b)))))
  (println (format "  Adam(45) + Eve(19) = 64 codons"))
  (println)

  ;; ═══ DIAGONAL NAMES ═══
  (println "=== BREASTPLATE DIAGONAL NAMES ===")
  (println "  Main diagonal (stones 1→5→8→12):")
  (println (format "    Forward: אהרן (Aaron) = %d = 2⁸" (g/word-value "אהרן")))
  (println (format "    Backward: ויהי (and it was) = %d" (g/word-value "ויהי")))
  (println)
  (println "  Anti-diagonal (stones 3→5→7→10):")
  (println (format "    Forward: אויב (enemy) = %d" (g/word-value "אויב")))
  (println (format "    Backward: ואהב (and he loved) = %d = David" (g/word-value "ואהב")))
  (println)

  ;; ═══ PER-HEAD ON DIVIDES ═══
  (println "=== PER-HEAD ON DIVIDE WORDS ===")
  (doseq [w ["שרה" "הגר" "יצחק" "ישמעאל" "דוד" "שאול" "נחש" "משיח"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (when (seq words)
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word (first words)) (:reading-count (first words))
                             (or (dict/translate (:word (first words))) "")))))))))
