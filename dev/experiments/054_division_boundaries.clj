;; Experiment 054: Where Do the Divisions Fall?
;;
;; When we divide 304,850 by 7, 50, 13, 67 — do the boundaries
;; fall on verse boundaries? Word boundaries? Or within words?
;;
;; Build a word-level index from OSHB and check every division point.
;;
;; RESULT: The ÷7 division cooperates with the text.
;;
;;   ÷7:    4 of 6 boundaries (67%) land on word boundaries
;;   ÷50:  16 of 49 (33%)
;;   ÷13:   2 of 12 (17%)
;;   ÷67:  15 of 66 (23%)
;;   ÷350: 92 of 349 (26%)
;;   ÷4550: 1,226 of 4,549 (27%)
;;
;;   Expected by chance: ~25% (avg word length ~3.8 letters)
;;
;;   Only ÷7 breaks the baseline. The finer divisions converge to random.
;;   The text knows about its own sevens.
;;
;;   The 4 word-aligned boundaries:
;;     3→4  Exodus 34:29     → לא       "Moses did not know his face shone"
;;     4→5  Leviticus 21:17  → מזרעך    "of your seed"
;;     5→6  Numbers 17:11    → העדה     "the congregation"
;;     6→7  Deuteronomy 6:1  → לעשות    "to do" (3 verses before the Shema)

(require '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[clojure.string :as str])

;;; ---- Build the word-boundary index ----

(defn build-word-index
  "Build a word-level index for the entire Torah.
   Returns a vector of {:book :chapter :verse :word-num :text :letters :start :end}
   where :start/:end are positions in the Torah-wide letter stream."
  []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        pos (atom 0)]
    (vec
     (mapcat
      (fn [book]
        (let [words (oshb/book-words book)
              by-cv (group-by (juxt :chapter :verse) words)]
          (mapcat
           (fn [[[ch v] ws]]
             (map-indexed
              (fn [i w]
                (let [letters (norm/letter-stream (:text w))
                      n (count letters)
                      start @pos]
                  (swap! pos + n)
                  {:book book :chapter ch :verse v
                   :word-num (inc i)
                   :text (:text w)
                   :letters (apply str letters)
                   :start start
                   :end (+ start n)}))
              ws))
           (sort-by first by-cv))))
      books))))

(def word-index (build-word-index))
(def total-letters (:end (last word-index)))

(println "Total words:" (count word-index))
(println "Total letters:" total-letters)
(println)

;;; ---- Check a division point ----

(defn check-boundary
  "Given a letter position, report what's there:
   - Is it a verse boundary? (start of some verse)
   - Is it a word boundary? (start of some word)
   - Or is it inside a word?"
  [pos word-index]
  (let [;; Binary search would be better, but linear is fine for a few hundred checks
        word (first (filter #(and (>= pos (:start %)) (< pos (:end %))) word-index))]
    (cond
      (nil? word) {:pos pos :type :beyond-end}

      (= pos (:start word))
      (if (= 1 (:word-num word))
        {:pos pos :type :verse-boundary
         :ref (str (:book word) " " (:chapter word) ":" (:verse word))
         :word (:letters word)}
        {:pos pos :type :word-boundary
         :ref (str (:book word) " " (:chapter word) ":" (:verse word))
         :word-num (:word-num word)
         :word (:letters word)})

      :else
      {:pos pos :type :within-word
       :ref (str (:book word) " " (:chapter word) ":" (:verse word))
       :word-num (:word-num word)
       :word (:letters word)
       :offset (- pos (:start word))
       :word-len (- (:end word) (:start word))})))

;;; ---- Check all division levels ----

(println "=== DIVISION BY 7 (43,550 letters per part) ===")
(println)
(let [part-size (/ total-letters 7)]
  (doseq [i (range 1 7)]
    (let [pos (* i part-size)
          info (check-boundary pos word-index)]
      (println (format "  Boundary %d→%d at letter %6d: %-15s — %s"
                       i (inc i) pos (name (:type info))
                       (pr-str (dissoc info :pos :type)))))))

(println)
(println "=== DIVISION BY 50 (6,097 letters per part) ===")
(println "(showing first 10, last 3, and any verse/word boundaries)")
(println)
(let [part-size (/ total-letters 50)  ;; 304850/50 = 6097
      boundaries (for [i (range 1 50)]
                   (let [pos (* i part-size)
                         info (check-boundary pos word-index)]
                     (assoc info :i i)))]
  ;; Count by type
  (let [by-type (frequencies (map :type boundaries))]
    (println "  Type counts:" by-type))
  (println)
  ;; Show notable ones
  (doseq [b boundaries]
    (when (or (<= (:i b) 5) (>= (:i b) 47)
              (#{:verse-boundary :word-boundary} (:type b)))
      (println (format "  Boundary %2d at letter %6d: %-15s — %s"
                       (:i b) (:pos b) (name (:type b))
                       (str (:ref b)
                            (when (= :within-word (:type b))
                              (format " word=%s offset=%d/%d"
                                      (:word b) (:offset b) (:word-len b)))))))))

(println)
(println "=== DIVISION BY 13 (23,450 letters per part) ===")
(println)
(let [part-size (/ total-letters 13)  ;; 304850/13 = 23450
      boundaries (for [i (range 1 13)]
                   (let [pos (* i part-size)
                         info (check-boundary pos word-index)]
                     (assoc info :i i)))]
  (let [by-type (frequencies (map :type boundaries))]
    (println "  Type counts:" by-type))
  (println)
  (doseq [b boundaries]
    (println (format "  Boundary %2d at letter %6d: %-15s — %s"
                     (:i b) (:pos b) (name (:type b))
                     (str (:ref b)
                          (when (= :within-word (:type b))
                            (format " word=%s offset=%d/%d"
                                    (:word b) (:offset b) (:word-len b)))
                          (when (= :word-boundary (:type b))
                            (format " word #%d=%s" (:word-num b) (:word b))))))))

(println)
(println "=== DIVISION BY 67 (4,550 letters per part) ===")
(println "(showing first 5, last 3, and any verse/word boundaries)")
(println)
(let [part-size (/ total-letters 67)  ;; 304850/67 = 4550
      boundaries (for [i (range 1 67)]
                   (let [pos (* i part-size)
                         info (check-boundary pos word-index)]
                     (assoc info :i i)))]
  (let [by-type (frequencies (map :type boundaries))]
    (println "  Type counts:" by-type))
  (println)
  (doseq [b boundaries]
    (when (or (<= (:i b) 5) (>= (:i b) 64)
              (#{:verse-boundary :word-boundary} (:type b)))
      (println (format "  Boundary %2d at letter %6d: %-15s — %s"
                       (:i b) (:pos b) (name (:type b))
                       (str (:ref b)
                            (when (= :within-word (:type b))
                              (format " word=%s offset=%d/%d"
                                      (:word b) (:offset b) (:word-len b)))
                            (when (= :word-boundary (:type b))
                              (format " word #%d=%s" (:word-num b) (:word b)))))))))

;;; ---- The nested divisions (43,550 → 871 → 67 → 1) ----

(println)
(println "=== NESTED DIVISION: 871-letter parts (÷7 then ÷50) ===")
(println "(304,850 ÷ 350 = 871 parts — showing first 10 and counts)")
(println)
(let [part-size 871  ;; 43550/50 = 871
      n-parts (/ total-letters part-size)
      boundaries (for [i (range 1 n-parts)]
                   (let [pos (* i part-size)
                         info (check-boundary pos word-index)]
                     (assoc info :i i)))]
  (let [by-type (frequencies (map :type boundaries))]
    (println "  Type counts:" by-type)
    (println (format "  Total boundaries: %d" (count boundaries)))
    (when-let [vb (seq (filter #(= :verse-boundary (:type %)) boundaries))]
      (println (format "  Verse boundaries at: %s" (str/join ", " (map :i vb)))))
    (when-let [wb (seq (filter #(= :word-boundary (:type %)) boundaries))]
      (println (format "  Word boundaries: %d of %d" (count wb) (count boundaries)))))
  (println)
  (println "  First 10:")
  (doseq [b (take 10 boundaries)]
    (println (format "    Part %3d at letter %6d: %-15s — %s"
                     (:i b) (:pos b) (name (:type b))
                     (str (:ref b)
                          (when (= :within-word (:type b))
                            (format " word=%s offset=%d/%d"
                                    (:word b) (:offset b) (:word-len b))))))))

(println)
(println "=== NESTED DIVISION: 67-letter parts (÷7 then ÷50 then ÷13) ===")
(println "(304,850 ÷ 4,550 = 67-letter parts — showing first 10 and counts)")
(println)
(let [part-size 67
      n-parts (/ total-letters part-size)
      boundaries (for [i (range 1 n-parts)]
                   (let [pos (* i part-size)
                         info (check-boundary pos word-index)]
                     (assoc info :i i)))]
  (let [by-type (frequencies (map :type boundaries))]
    (println "  Type counts:" by-type)
    (println (format "  Total boundaries: %d" (count boundaries)))
    (when-let [wb (seq (filter #(= :word-boundary (:type %)) boundaries))]
      (println (format "  Word boundaries: %d of %d (%.1f%%)"
                       (count wb) (count boundaries)
                       (* 100.0 (/ (count wb) (count boundaries))))))
    (when-let [vb (seq (filter #(= :verse-boundary (:type %)) boundaries))]
      (println (format "  Verse boundaries: %d of %d (%.1f%%)"
                       (count vb) (count boundaries)
                       (* 100.0 (/ (count vb) (count boundaries))))))))

(println)
(println "Done.")
