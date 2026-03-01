;; Experiment 055: The Center of the 4D Space
;;
;; 304,850 = 7 × 50 × 13 × 67
;; The geometric center of the space is (3, 25, 6, 33).
;;
;; RESULT: The center lands in Leviticus 8:35.
;;
;;   The d-fiber through the center (67 letters at a=3, b=25, c=6)
;;   reads: "And day and night, SEVEN DAYS, you shall keep the charge
;;   of the LORD, that you do not die, for so I have been commanded."
;;
;;   The text at the center of the 7×50×13×67 structure is about
;;   keeping watch for SEVEN DAYS.
;;
;;   The midpoint (3, 25, 0, 0) = Leviticus 8:29 — same chapter.
;;   Leviticus 8 is the consecration of Aaron, where the Urim and
;;   Thummim is placed on his chest (Lev 8:8).
;;
;;   The אורים (Urim / "Lights") sits at coordinates (3, 23, 7, 19..23):
;;     a=3 — the center of the seven days
;;     c=7 — the seventh position on the love/unity axis
;;
;;   The Lights at the center of the center, on the seventh.

(require '[selah.space.coords :as c])

(def s (c/space))

;;; ---- The geometric center ----

(println "=== THE CENTER POINT (3, 25, 6, 33) ===")
(println)
(let [center-pos (c/coord->idx 3 25 6 33)]
  (println "Position:" center-pos "of" c/total-letters)
  (let [d (c/describe center-pos)]
    (println "Letter:" (:letter d))
    (println "Verse:" (:verse d))
    (println "Gematria:" (:gematria d))))

;;; ---- The midpoint ----

(println)
(println "=== MIDPOINT (3, 25, 0, 0) — letter 152,425 ===")
(println)
(let [d (c/describe 152425)]
  (println "Letter:" (:letter d))
  (println "Verse:" (:verse d)))

;;; ---- The center d-fiber: 67 letters of understanding ----

(println)
(println "=== CENTER d-FIBER: 67 LETTERS OF UNDERSTANDING ===")
(println "a=3, b=25, c=6 — varying d from 0 to 66")
(println)
(let [positions (c/fiber :d {:a 3 :b 25 :c 6})]
  (println "Letters:" (apply str (map #(c/letter-at s (aget positions %)) (range 67))))
  (println)
  (let [verses (distinct (map #(:verse (c/describe (aget positions %))) (range 67)))]
    (println "Verses:" (count verses))
    (doseq [v verses]
      (println " " v)))
  (println)
  (println "\"And day and night, SEVEN DAYS, you shall keep")
  (println " the charge of the LORD, that you do not die,")
  (println " for so I have been commanded.\"")
  (println " — Leviticus 8:35")
  (println)
  (let [total (reduce + (map #(c/gv-at s (aget positions %)) (range 67)))]
    (println "Gematria sum of center fiber:" total)))

;;; ---- The c-fiber: 13 loves through the center ----

(println)
(println "=== CENTER c-FIBER: 13 ONES/LOVES ===")
(println "a=3, b=25, d=33 — varying c from 0 to 12")
(println)
(let [positions (c/fiber :c {:a 3 :b 25 :d 33})]
  (println "Letters:" (apply str (map #(c/letter-at s (aget positions %)) (range 13))))
  (println)
  (doseq [i (range 13)]
    (let [pos (aget positions i)
          d (c/describe pos)]
      (println (format "  c=%2d  %s  gv=%3d  %s" i (:letter d) (:gematria d) (:verse d))))))

;;; ---- The a-fiber: 7 days through the center ----

(println)
(println "=== CENTER a-FIBER: 7 DAYS ===")
(println "b=25, c=6, d=33 — varying a from 0 to 6")
(println)
(let [positions (c/fiber :a {:b 25 :c 6 :d 33})]
  (println "Letters:" (apply str (map #(c/letter-at s (aget positions %)) (range 7))))
  (println)
  (doseq [i (range 7)]
    (let [pos (aget positions i)
          d (c/describe pos)]
      (println (format "  Day %d: %s  gv=%3d  %s" i (:letter d) (:gematria d) (:verse d))))))

;;; ---- Leviticus 8:8 — The Urim and Thummim ----

(println)
(println "=== LEVITICUS 8:8 — THE URIM AND THUMMIM ===")
(println)
(let [vrefs (:verse-ref s)
      lev88 (first (filter #(and (= "Leviticus" (:book %))
                                  (= 8 (:ch %)) (= 8 (:vs %))) vrefs))]
  (println "Verse starts at position:" (:start lev88))
  (println "Length:" (- (:end lev88) (:start lev88)) "letters")
  (println)

  ;; Find אורים
  (let [verse-str (apply str (map #(c/letter-at s %) (range (:start lev88) (:end lev88))))
        idx (.indexOf verse-str "אורים")]
    (when (>= idx 0)
      (let [abs-start (+ (:start lev88) idx)]
        (println "אורים (Urim / Lights) at offset" idx "in verse:")
        (doseq [i (range 5)]
          (let [pos (+ abs-start i)
                d (c/describe pos)]
            (println (format "  %s  coord=%s  gv=%3d" (:letter d) (:coord d) (:gematria d)))))
        (println)
        (println "a=3 — the center seventh")
        (println "c=7 — the seventh position on the love/unity axis")
        (println "The Lights at the center of the center, on the seventh.")))))

(println)
(println "Done.")
