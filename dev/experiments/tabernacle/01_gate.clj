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

      extract-words (fn [book ch vs]
                      (let [v (find-verse book ch vs)
                            start (:start v)
                            end (:end v)
                            letters (apply str (for [i (range start end)]
                                                (sc/letter-at s i)))
                            coord (vec (sc/idx->coord start))]
                        {:ref (str book " " ch ":" vs)
                         :coord coord
                         :start start
                         :end end
                         :letters letters
                         :letter-count (- end start)}))

      ;; Gate instruction: Ex 27:16
      gate-instr (extract-words "Exodus" 27 16)
      ;; Gate built: Ex 38:18
      gate-built (extract-words "Exodus" 38 18)

      ;; Also get surrounding verses for context
      gate-instr-15 (extract-words "Exodus" 27 15)
      gate-instr-17 (extract-words "Exodus" 27 17)
      gate-built-17 (extract-words "Exodus" 38 17)
      gate-built-19 (extract-words "Exodus" 38 19)

      ;; Run oracle on each gate verse's words
      oracle-verse (fn [v]
                     (let [letters (:letters v)
                           fwd (o/forward letters)
                           by-head (o/forward-by-head letters)
                           ;; Also get individual word oracle readings
                           ;; Parse the verse's Hebrew words from Torah text
                           ]
                       (assoc v
                              :oracle fwd
                              :by-head by-head)))

      ;; Get all words from the Torah that fall in this verse range
      torah-words-in (fn [start end]
                       (let [tw (dict/torah-words)]
                         (filter (fn [w]
                                   (let [s (:start w)]
                                     (and s (>= s start) (< s end))))
                                 tw)))]

  ;; Print gate instruction details
  (println "=== GATE INSTRUCTION: Ex 27:16 ===")
  (println "Coord:" (:coord gate-instr))
  (println "Letters:" (:letters gate-instr))
  (println "Letter count:" (:letter-count gate-instr))
  (println "GV:" (g/word-value (:letters gate-instr)))
  (println)

  ;; Oracle the gate instruction verse
  (let [fwd (o/forward (:letters gate-instr))
        by-head (o/forward-by-head (:letters gate-instr))]
    (println "--- Combined oracle (top 15) ---")
    (doseq [w (take 15 (:known-words fwd))]
      (println (format "  %-6s  count=%-3d  gv=%-4d  %s"
                       (:word w) (:reading-count w) (:gv w)
                       (or (dict/translate (:word w)) ""))))
    (println)
    (println "--- Per-head top 5 ---")
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (take 5 (get by-head reader))]
        (println (format "    %-6s  count=%-3d  gv=%-4d  %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== GATE BUILT: Ex 38:18 ===")
  (println "Coord:" (:coord gate-built))
  (println "Letters:" (:letters gate-built))
  (println "Letter count:" (:letter-count gate-built))
  (println "GV:" (g/word-value (:letters gate-built)))
  (println)

  ;; Oracle the gate built verse
  (let [fwd (o/forward (:letters gate-built))
        by-head (o/forward-by-head (:letters gate-built))]
    (println "--- Combined oracle (top 15) ---")
    (doseq [w (take 15 (:known-words fwd))]
      (println (format "  %-6s  count=%-3d  gv=%-4d  %s"
                       (:word w) (:reading-count w) (:gv w)
                       (or (dict/translate (:word w)) ""))))
    (println)
    (println "--- Per-head top 5 ---")
    (doseq [reader [:aaron :god :truth :mercy]]
      (println (str "  " (name reader) ":"))
      (doseq [w (take 5 (get by-head reader))]
        (println (format "    %-6s  count=%-3d  gv=%-4d  %s"
                         (:word w) (:reading-count w) (:gv w)
                         (or (dict/translate (:word w)) ""))))))

  (println)
  (println "=== CONTEXT: Ex 27:15-17 ===")
  (doseq [v [gate-instr-15 gate-instr gate-instr-17]]
    (println (format "  %s  %s  coord=%s  gv=%d"
                     (:ref v) (:letters v) (:coord v)
                     (g/word-value (:letters v)))))

  (println)
  (println "=== CONTEXT: Ex 38:17-19 ===")
  (doseq [v [gate-built-17 gate-built gate-built-19]]
    (println (format "  %s  %s  coord=%s  gv=%d"
                     (:ref v) (:letters v) (:coord v)
                     (g/word-value (:letters v)))))

  ;; Word-by-word oracle for instruction verse
  (println)
  (println "=== WORD-BY-WORD: Ex 27:16 ===")
  (let [letters (:letters gate-instr)
        ;; Split into Torah words by looking at the raw text
        ;; For now just oracle individual words from the verse
        words (str/split letters #"(?<=את|של|מסך|שער|חצר|עשרים|אמה|תכלת|ארגמן|תולעת|שני|שש|משזר|מעשה|רקם)")
        ;; Better approach: get each word individually
        ]
    ;; Let's oracle each word we know from this verse
    (doseq [w ["מסך" "שער" "חצר" "עשרים" "אמה" "תכלת" "ארגמן" "תולעת" "שני" "שש" "משזר" "מעשה" "רקם"]]
      (let [fwd (o/forward w)
            known (:known-words fwd)
            top (first known)
            basin-result (when top
                           (try (basin/step w) (catch Exception _ nil)))]
        (println (format "  %-8s gv=%-4d  top-reading: %-8s (count=%s)  basin: %s  %s"
                         w (g/word-value w)
                         (or (:word top) "—")
                         (or (:reading-count top) "—")
                         (or basin-result "—")
                         (or (dict/translate w) ""))))))

  ;; Word-by-word oracle for built verse
  (println)
  (println "=== WORD-BY-WORD: Ex 38:18 ===")
  (doseq [w ["מסך" "שער" "חצר" "מעשה" "רקם" "תכלת" "ארגמן" "תולעת" "שני" "שש" "משזר" "עשרים" "אמה" "קומה" "רחב" "חמש" "אמות" "לעמת" "קלעי"]]
    (let [fwd (o/forward w)
          known (:known-words fwd)
          top (first known)
          basin-result (try (basin/step w) (catch Exception _ nil))]
      (println (format "  %-8s gv=%-4d  top-reading: %-8s (count=%s)  basin: %s  %s"
                       w (g/word-value w)
                       (or (:word top) "—")
                       (or (:reading-count top) "—")
                       (or basin-result "—")
                       (or (dict/translate w) "")))))

  ;; d-axis walk at the gate instruction layer
  (println)
  (println "=== D-AXIS WALK at (2,35,4,*) — Gate instruction layer ===")
  (let [base-idx (sc/coord->idx [2 35 4 0])]
    (doseq [d (range 67)]
      (let [idx (+ base-idx d)
            letter (sc/letter-at s idx)
            gv (g/letter-value letter)]
        (print (format "%s" letter))
        (when (= 0 (mod (inc d) 10)) (println)))))
  (println)

  ;; d-axis walk at the gate built layer
  (println)
  (println "=== D-AXIS WALK at (3,7,3,*) — Gate built layer ===")
  (let [base-idx (sc/coord->idx [3 7 3 0])]
    (doseq [d (range 67)]
      (let [idx (+ base-idx d)
            letter (sc/letter-at s idx)]
        (print (format "%s" letter))
        (when (= 0 (mod (inc d) 10)) (println)))))
  (println)
  (flush)

  ;; Also get the individual word parses from the actual Torah segmentation
  (println)
  (println "=== VERSE TEXT SEGMENTATION ===")
  (println "Gate instruction (Ex 27:16):")
  (println "  ולשער החצר מסך עשרים אמה תכלת וארגמן ותולעת שני ושש משזר מעשה רקם")
  (println "Gate built (Ex 38:18):")
  (println "  ומסך שער החצר מעשה רקם תכלת וארגמן ותולעת שני ושש משזר ועשרים אמה ארך וקומה ברחב חמש אמות לעמת קלעי החצר")

  (println)
  (println "=== GEMATRIA TOTALS ===")
  (println "Gate instruction verse GV:" (g/word-value (:letters gate-instr)))
  (println "Gate built verse GV:" (g/word-value (:letters gate-built)))
  (println "Sum:" (+ (g/word-value (:letters gate-instr))
                     (g/word-value (:letters gate-built)))))
