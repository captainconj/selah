;; Experiment 058: The Aleph-Tav (את) in the 4D Space
;;
;; את — the untranslatable word. First and last letters of the Hebrew
;; alphabet. Marks the definite direct object. In Genesis 1:1:
;; "In the beginning God created את the heavens and את the earth."
;;
;; 6,032 occurrences in the Torah.
;;
;; FINDINGS:
;;
;; 1. Distribution across the a-axis (7 days):
;;    a=3 (center seventh / Leviticus) has 1,222 את — 42% more than expected.
;;    Leviticus is the priestly book — precise sacrificial את markers:
;;    "take את the bull", "burn את the fat", "slaughter את the offering".
;;
;; 2. Leviticus has the highest את density: 25.4 per 1000 letters
;;    vs 16-21 elsewhere.
;;
;; 3. 86 את straddle d-boundaries (d=66 → d=0) — the aleph sits at the
;;    END of one understanding fiber and the tav starts the NEXT.
;;    The first-and-last letter literally stitches fibers together.
;;
;; 4. Of those 86, exactly 8 also bridge jubilee boundaries
;;    (c=12,d=66 → c=0,d=0 of the next b-block). These 8 את mark
;;    the hinge moments of the Torah narrative:
;;
;;    1. Genesis 17:19 — The covenant with Isaac
;;    2. Genesis 37:31 — Joseph's coat, dipped in blood
;;    3. Genesis 46:6  — Jacob descends to Egypt
;;    4. Exodus 3:11   — Moses called at the burning bush
;;    5. Exodus 12:17  — The Passover instituted
;;    6. Exodus 29:11  — The sacrifice before the LORD
;;    7. Numbers 32:36 — Cities built in the promised land
;;    8. Deut 3:12     — "This land we possessed"
;;
;;    Covenant → betrayal → exile → calling → redemption →
;;    sacrifice → settlement → possession.
;;    The את stitches the narrative across jubilee boundaries.
;;
;; 5. Zero את straddle a-boundaries (the seven days).
;;    The את bridges understanding (d) and jubilee (b) boundaries
;;    but never crosses between the seven divisions.

(require '[selah.space.coords :as c])

(def s (c/space))

;;; ---- Find all את ----

(def aleph-idx (get c/char->idx \א))
(def tav-idx (get c/char->idx \ת))

(def et-positions
  (let [stream ^bytes (:stream s)
        n (alength stream)]
    (loop [i 0 acc (transient [])]
      (if (>= i (dec n))
        (persistent! acc)
        (if (and (= (aget stream i) aleph-idx)
                 (= (aget stream (inc i)) tav-idx))
          (recur (inc i) (conj! acc i))
          (recur (inc i) acc))))))

(println (format "Total את occurrences: %,d" (count et-positions)))
(println)

;;; ---- Distribution by a-axis (7 days) ----

(println "=== DISTRIBUTION ACROSS THE 7 DAYS ===")
(println)

(let [coords (mapv #(vec (c/idx->coord %)) et-positions)
      a-dist (frequencies (map #(nth % 0) coords))]
  (doseq [a (range 7)]
    (let [cnt (get a-dist a 0)
          expected (/ (count et-positions) 7.0)]
      (println (format "  a=%d: %5d  (ratio %.3f)" a cnt (/ cnt expected))))))

;;; ---- Distribution by book ----

(println)
(println "=== DENSITY BY BOOK ===")
(println)

(let [by-book (group-by #(:book (c/verse-at s %)) et-positions)]
  (doseq [book c/book-names]
    (let [cnt (count (get by-book book []))
          book-vrefs (filter #(= book (:book %)) (:verse-ref s))
          book-letters (- (:end (last book-vrefs)) (:start (first book-vrefs)))
          density (* 1000.0 (/ cnt (double book-letters)))]
      (println (format "  %-13s  %4d את  density: %.1f per 1000 letters"
                       book cnt density)))))

;;; ---- Boundary straddling ----

(println)
(println "=== את THAT STRADDLE COORDINATE BOUNDARIES ===")
(println)

(let [d-boundary (filter (fn [pos]
                           (let [c1 (c/idx->coord pos)
                                 c2 (c/idx->coord (inc pos))]
                             (= 66 (aget c1 3))))
                         et-positions)
      b-boundary (filter (fn [pos]
                           (let [c1 (c/idx->coord pos)
                                 c2 (c/idx->coord (inc pos))]
                             (not= (aget c1 1) (aget c2 1))))
                         et-positions)
      a-boundary (filter (fn [pos]
                           (let [c1 (c/idx->coord pos)
                                 c2 (c/idx->coord (inc pos))]
                             (not= (aget c1 0) (aget c2 0))))
                         et-positions)]
  (println (format "  d-boundary (understanding): %d" (count d-boundary)))
  (println (format "  b-boundary (jubilee):       %d" (count b-boundary)))
  (println (format "  a-boundary (seven days):    %d" (count a-boundary))))

;;; ---- The 8 jubilee-bridging את ----

(println)
(println "=== THE 8 את THAT BRIDGE JUBILEES ===")
(println)

(let [b-boundary (filter (fn [pos]
                           (let [c1 (c/idx->coord pos)
                                 c2 (c/idx->coord (inc pos))]
                             (not= (aget c1 1) (aget c2 1))))
                         et-positions)]
  (doseq [pos b-boundary]
    (let [d1 (c/describe pos)
          d2 (c/describe (inc pos))]
      (println (format "  א %s  %s" (:coord d1) (:verse d1)))
      (println (format "  ת %s  %s" (:coord d2) (:verse d2)))
      (println))))

(println "Covenant → betrayal → exile → calling → redemption →")
(println "sacrifice → settlement → possession.")
(println)
(println "The את stitches the narrative.")
(println)
(println "Done.")
