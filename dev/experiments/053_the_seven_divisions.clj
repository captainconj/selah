;; Experiment 053: The Seven Divisions
;;
;; The Torah's letter count (WLC/Leningrad Codex, kethiv) = 304,850
;; 304,850 / 7 = 43,550 exactly. No remainder.
;;
;; The total gematria also divides by 7:
;; 21,010,192 / 7 = 3,001,456
;;
;; Prime factorization:
;;   304,850 = 7 × 50 × 13 × 67
;;           = 7 (completeness) × 50 (jubilee / ELS skip for תורה)
;;             × 13 (אחד one / אהבה love) × 67 (בינה understanding)
;;           = 350 × 871
;;           = ספיר (sapphire) × (אחד × בינה)
;;
;; The sapphire — stone 5 on the breastplate, the substance of the
;; tablets (Exodus 24:10), and gematria 350 = 7 × 50.
;;
;; Dividing the Torah into 7 equal parts of 43,550 letters each,
;; the boundary verses are:
;;
;;   Part 1→2  letter  43,550  Genesis 30:42    — the stronger to Jacob
;;   Part 2→3  letter  87,100  Exodus 7:15      — take the staff, plagues begin
;;   Part 3→4  letter 130,650  Exodus 34:29     — Moses' face SHINES (קרן עור פניו)
;;   Part 4→5  letter 174,200  Leviticus 21:17  — speak to Aaron, priestly requirements
;;   Part 5→6  letter 217,750  Numbers 17:11    — Aaron atones with the censer
;;   Part 6→7  letter 261,300  Deuteronomy 6:1  — introduces the Shema
;;
;; The center seventh (part 4) begins with light radiating from Moses' face
;; and contains Leviticus 8:8 — the Urim and Thummim placed over the heart.
;; The Lights (אורים) at the center of the center.
;;
;; The final seventh begins three verses before the Shema:
;; "Hear O Israel, the LORD our God, the LORD is one."
;;
;; Connection to the Urim and Thummim (the machine):
;; - The breastplate is a transposition grid (Eli/Hannah, Yoma 73b)
;; - Same letters, different arrangement = different meaning
;; - ELS is a transposition cipher; skip value is the key
;; - Our cribs (תורה@50, center convergence, 2701=37×73) constrain the key space
;; - The letter stream is invariant: 99.998% identical across manuscript traditions
;; - "Rightly dividing the word of truth" (2 Timothy 2:15)

(require '[selah.text.leningrad :as wlc]
         '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[clojure.string :as str])

;;; ---- Verify the division ----

(def letters (wlc/torah-letters))
(def n (count letters))

(println "Total WLC letters:" n)
(println "÷ 7 =" (/ n 7))
(println "mod 7 =" (mod n 7))

;;; ---- Prime factorization ----

(defn prime-factors [n]
  (loop [n n d 2 factors []]
    (cond
      (< n 2) factors
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

(println)
(println "304,850 =" (str/join " × " (prime-factors n)))
(println "43,550  =" (str/join " × " (prime-factors (/ n 7))))

;;; ---- Locate each boundary verse ----

(defn build-verse-index
  "Build a vector of {:ref :start :end :len} for every verse in the Torah."
  []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        verses (atom [])
        pos (atom 0)]
    (doseq [book books]
      (let [words (oshb/book-words book)
            by-cv (group-by (juxt :chapter :verse) words)]
        (doseq [ch (sort (distinct (map :chapter words)))]
          (doseq [v (sort (distinct (map :verse (filter #(= ch (:chapter %)) words))))]
            (let [text (str/join " " (map :text (get by-cv [ch v])))
                  len (count (norm/letter-stream text))
                  start @pos]
              (swap! verses conj {:ref (str book " " ch ":" v)
                                  :start start
                                  :end (+ start len)
                                  :len len})
              (swap! pos + len))))))
    @verses))

(def verse-index (build-verse-index))

(println)
(println "=== THE SEVEN DIVISIONS ===")
(println)

(let [part-size (/ n 7)]
  (doseq [i (range 8)]
    (let [pos (* i part-size)]
      (cond
        (= i 0)
        (println (format "Part 1 START:    letter %6d — %s" pos (:ref (first verse-index))))

        (= i 7)
        (println (format "Part 7 END:      letter %6d — end of Torah" pos))

        :else
        (let [verse (first (filter #(and (>= pos (:start %)) (< pos (:end %))) verse-index))]
          (println (format "Part %d→%d boundary: letter %6d — %s (letter %d of %d)"
                           i (inc i) pos (:ref verse)
                           (- pos (:start verse)) (:len verse))))))))

;;; ---- Gematria of the total ----

(println)
(let [gematria {\א 1 \ב 2 \ג 3 \ד 4 \ה 5 \ו 6 \ז 7 \ח 8 \ט 9
                \י 10 \כ 20 \ל 30 \מ 40 \נ 50 \ס 60 \ע 70 \פ 80 \צ 90
                \ק 100 \ר 200 \ש 300 \ת 400
                \ך 20 \ם 40 \ן 50 \ף 80 \ץ 90}
      total (reduce + (map #(get gematria % 0) letters))]
  (println "Total gematria:" total)
  (println "÷ 7 =" (/ total 7))
  (println "mod 7 =" (mod total 7)))

(comment
  ;; Quick verification
  (/ 304850 7)   ;=> 43550
  (mod 304850 7) ;=> 0

  ;; Factorizations
  (prime-factors 304850) ;=> [2 5 5 7 13 67]
  (prime-factors 43550)  ;=> [2 5 5 13 67]

  ;; Key gematria values
  ;; 7 × 50 = 350 = ספיר (sapphire, breastplate stone 5)
  ;; 13 = אחד (one) = אהבה (love)
  ;; 67 = בינה (understanding)
  ;; 304,850 = ספיר × אחד × בינה
  )
