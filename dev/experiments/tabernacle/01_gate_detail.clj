(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin])

(sc/build!)

(let [s (sc/space)]

  ;; d-axis walk at the gate instruction layer (2,35,4,*)
  (println "=== D-AXIS WALK at (2,35,4,*) — Gate instruction layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 2 35 4 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; d-axis walk at the gate built layer (3,7,3,*)
  (println)
  (println "=== D-AXIS WALK at (3,7,3,*) — Gate built layer ===")
  (doseq [d (range 67)]
    (let [idx (sc/coord->idx 3 7 3 d)
          letter (sc/letter-at s idx)]
      (print letter)))
  (println)

  ;; Per-head analysis of key gate words
  (println)
  (println "=== PER-HEAD: Key gate words ===")
  (doseq [w ["שער" "אמה" "שני" "רחב" "חמש" "מסך" "קומה" "עשרים"]]
    (let [by-head (o/forward-by-head w)]
      (println (format "\n  %s (gv=%d, %s):" w (g/word-value w) (or (dict/translate w) "?")))
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)
              top (first words)]
          (when top
            (println (format "    %-6s: %-6s count=%-3d  %s"
                             (name reader) (:word top) (:reading-count top)
                             (or (dict/translate (:word top)) ""))))))))

  ;; Basin analysis of gate words
  (println)
  (println "=== BASIN CLASSIFICATION ===")
  (doseq [w ["שער" "אמה" "שני" "שש" "רקם" "ארגמן" "משזר" "מעשה"
             "רחב" "חמש" "קומה" "עשרים" "אמות" "לעמת" "מסך" "חצר"]]
    (let [result (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? result) "invisible"
                  (:fixed? result) "FIXED"
                  :else (str "→ " (:next result)))]
      (println (format "  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) class
                       (or (dict/translate w) "")))))

  ;; Cross-reference: gate GV=6866
  (println)
  (println "=== GEMATRIA CROSS-REFERENCES ===")
  (println "Gate instruction GV = 6866")
  (println "  6866 = 2 × 3433")
  (println "  6866 = 2 × 7 × 491 ?" (= (* 2 7 491) 6866))
  (println "  6866 = 2 × 11 × 313 ?" (= (* 2 11 313) 6866))
  (println "  6866 / 26 =" (/ 6866.0 26))
  (println "  6866 / 13 =" (/ 6866.0 13))
  (println)
  (println "Gate built GV = 8439")
  (println "  8439 = 3 × 2813")
  (println "  8439 / 26 =" (/ 8439.0 26))
  (println "  8439 / 13 =" (/ 8439.0 13))
  (println)
  (println "Sum = " (+ 6866 8439))
  (println "  15305 / 26 =" (/ 15305.0 26))
  (println "  15305 / 13 =" (/ 15305.0 13))

  ;; Factorizations
  (println)
  (let [factorize (fn [n]
                    (loop [n n d 2 factors []]
                      (cond
                        (< n 2) factors
                        (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                        :else (recur n (inc d) factors))))]
    (println "6866 =" (factorize 6866))
    (println "8439 =" (factorize 8439))
    (println "15305 =" (factorize 15305)))

  ;; What words does the oracle see in "gate" letters?
  (println)
  (println "=== ORACLE ON INDIVIDUAL GATE WORDS — 3-letter subwords ===")
  (doseq [w ["מסך" "שער" "חצר" "תכלת" "ארגמן" "תולעת" "משזר" "מעשה" "רקם"]]
    (let [fwd (o/forward w)
          known (:known-words fwd)]
      (println (format "\n  %s (%s, gv=%d):" w (or (dict/translate w) "?") (g/word-value w)))
      (println (format "    illuminations: %d, total readings: %d, known: %d"
                       (:illumination-count fwd) (:total-readings fwd) (count known)))
      (doseq [kw (take 8 known)]
        (println (format "    %-6s count=%-3d gv=%-4d  %s"
                         (:word kw) (:reading-count kw) (:gv kw)
                         (or (dict/translate (:word kw)) ""))))))

  (println)
  (println "=== KEY TRANSFORMS ===")
  (println "עשרים (twenty, 620) ↔ שערים (gates, 620) — same letters")
  (println "שני (two/scarlet, 360) ↔ נשי (women, 360) — same letters")
  (println "רחב (wide, 210) ↔ בחר (choose, 210) — same letters")
  (println "חמש (five, 348) ↔ משח (anoint, 348) — same letters")
  (println "קומה (height, 151) ↔ מקוה (hope/mikveh, 151) — same letters")
  (println "לעמת (corresponding, 540) = שמר (guard, 540) — same GV!")
  (println "אמות (cubits, 447) = I die (447) — same word")
  (println "אמה (cubit, 46) = Levi (46) — same GV"))
