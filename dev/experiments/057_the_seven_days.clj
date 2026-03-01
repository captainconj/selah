;; Experiment 057: The Seven Days in the 4D Space
;;
;; The creation narrative (Genesis 1:1 – 2:3) is 1,815 letters.
;; It starts at the origin (0, 0, 0, 0) and extends to (0, 2, 1, 5).
;;
;; FINDINGS:
;;
;; 1. The word יום (day) appears exactly 13 times in the creation narrative.
;;    13 = dim-c = אחד (one) = אהבה (love).
;;
;; 2. The creation narrative sweeps through ALL 13 values of the c-axis.
;;    Seven days of creation across thirteen positions of love/unity.
;;
;; 3. Each d-fiber (67 letters) from a creation day marker naturally
;;    frames that day's content, including the evening/morning formula
;;    (ויהי ערב ויהי בקר) in 5 of 6 days.
;;
;; 4. The d-fiber at the CENTER of the 4D space (a=3, b=25, c=6)
;;    reads: "And day and night, SEVEN DAYS, you shall keep the charge
;;    of the LORD" (Leviticus 8:35).
;;    The structure says 7. The center says 7.
;;    The center fiber is about guarding (שמר).
;;
;; 5. The turning sword (Gen 3:24) is exactly 67 letters — one fiber
;;    of understanding. It wraps around the d-axis.
;;    Its purpose: לשמר את דרך עץ החיים — to GUARD the way.
;;    Same root שמר as the center.
;;
;; 6. The structure is self-referential: the text at the center of
;;    the 7×50×13×67 space is about watching for seven days.

(require '[selah.space.coords :as c]
         '[clojure.string :as str])

(def s (c/space))
(def vrefs (:verse-ref s))

;;; ---- Creation narrative extent ----

(println "=== THE CREATION NARRATIVE IN 4D ===")
(println)

(let [gen11 (first (filter #(and (= "Genesis" (:book %)) (= 1 (:ch %)) (= 1 (:vs %))) vrefs))
      gen23 (first (filter #(and (= "Genesis" (:book %)) (= 2 (:ch %)) (= 3 (:vs %))) vrefs))
      start (:start gen11)
      end (:end gen23)
      n (- end start)]
  (println (format "Gen 1:1 – 2:3: %d letters" n))
  (println (format "Start: %s  End: %s" (vec (c/idx->coord start)) (vec (c/idx->coord (dec end)))))
  (println)

  ;; Count יום
  (let [text (apply str (map #(c/letter-at s %) (range start end)))
        yom-count (loop [from 0 cnt 0]
                    (let [idx (.indexOf text "יום" from)]
                      (if (neg? idx) cnt (recur (inc idx) (inc cnt)))))]
    (println (format "Occurrences of יום (day): %d" yom-count))
    (println "13 = dim-c = אחד (one) = אהבה (love)"))

  ;; c-axis coverage
  (println)
  (let [coords (map #(vec (c/idx->coord %)) (range start end))
        c-vals (sort (distinct (map #(nth % 2) coords)))]
    (println (format "c-axis values: %s (%d of 13)" (str/join "," c-vals) (count c-vals)))
    (println "Sweeps all 13 values of the love/unity axis.")))

;;; ---- Each day's shape ----

(println)
(println "=== EACH CREATION DAY ===")
(println)

(let [day-ranges [[1 [1 1] [1 5]]
                  [2 [1 6] [1 8]]
                  [3 [1 9] [1 13]]
                  [4 [1 14] [1 19]]
                  [5 [1 20] [1 23]]
                  [6 [1 24] [1 31]]
                  [7 [2 1] [2 3]]]]
  (doseq [[day [ch1 vs1] [ch2 vs2]] day-ranges]
    (let [v-start (first (filter #(and (= "Genesis" (:book %))
                                        (= ch1 (:ch %)) (= vs1 (:vs %))) vrefs))
          v-end (first (filter #(and (= "Genesis" (:book %))
                                      (= ch2 (:ch %)) (= vs2 (:vs %))) vrefs))
          coords (map #(vec (c/idx->coord %)) (range (:start v-start) (:end v-end)))
          c-vals (sort (distinct (map #(nth % 2) coords)))
          n-letters (- (:end v-end) (:start v-start))]
      (println (format "  Day %d: %3d letters, c=%s" day n-letters (str/join "," c-vals))))))

;;; ---- d-fibers from each day marker ----

(println)
(println "=== d-FIBERS: 67 LETTERS OF UNDERSTANDING PER DAY ===")
(println)

(let [formula "ויהיערבויהיבקר"
      day-markers [[1 1 5] [2 1 8] [3 1 13] [4 1 19] [5 1 23] [6 1 31] [7 2 3]]]
  (doseq [[day ch vs] day-markers]
    (let [vref (first (filter #(and (= "Genesis" (:book %))
                                     (= ch (:ch %)) (= vs (:vs %))) vrefs))
          start (:start vref)
          coord (vec (c/idx->coord start))
          positions (c/fiber :d {:a (nth coord 0) :b (nth coord 1) :c (nth coord 2)})
          letters (apply str (map #(c/letter-at s (aget positions %)) (range 67)))
          idx (.indexOf letters formula)]
      (println (format "Day %d — Gen %d:%d — coord %s" day ch vs coord))
      (println (format "  %s" letters))
      (when (>= idx 0)
        (println (format "  evening/morning formula at d-offset %d ✓" idx)))
      (println))))

;;; ---- The center echoes creation ----

(println "=== THE CENTER ECHOES CREATION ===")
(println)
(println "Center d-fiber (a=3, b=25, c=6):")
(let [positions (c/fiber :d {:a 3 :b 25 :c 6})]
  (println (apply str (map #(c/letter-at s (aget positions %)) (range 67))))
  (println)
  (println "\"And day and night, SEVEN DAYS, you shall keep")
  (println " the charge of the LORD\" — Leviticus 8:35"))
(println)
(println "The sword (Gen 3:24, 67 letters): לשמר את דרך עץ החיים")
(println "The center (Lev 8:35, 67 letters): ושמרתם את משמרת יהוה")
(println "Same root: שמר — to guard, to keep watch.")

(println)
(println "Done.")
