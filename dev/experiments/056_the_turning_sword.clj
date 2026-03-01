;; Experiment 056: The Turning Sword
;;
;; Genesis 3:24 — "He placed at the east of the garden of Eden the
;; cherubim, and the flame of the sword that turns every which way,
;; to guard the way to the Tree of Life."
;;
;; ואת להט החרב המתהפכת לשמר את דרך עץ החיים
;;
;; RESULT: Genesis 3:24 is exactly 67 letters long.
;;
;;   67 = the d-dimension = בינה (understanding)
;;   One complete fiber of understanding.
;;
;;   The verse starts at d=59 and ends at d=58.
;;   It wraps around the understanding axis.
;;   The sword turns.
;;
;;   The c-axis (love) fiber from the sword traces Genesis 3-4:
;;   the expulsion, the curse, the naming of Eve, the banishment,
;;   the sword itself, and then Cain. The sword's love-fiber
;;   walks through the entire Fall narrative.
;;
;;   The ח of החרב (the sword) sits at coordinate (0, 4, 10, 33) —
;;   d=33, the midpoint of the understanding axis.
;;   The sword is at the center of understanding.

(require '[selah.space.coords :as c]
         '[clojure.string :as str])

(def s (c/space))

;;; ---- Find Genesis 3:24 ----

(def vrefs (:verse-ref s))
(def gen324 (first (filter #(and (= "Genesis" (:book %))
                                  (= 3 (:ch %)) (= 24 (:vs %))) vrefs)))

(println "=== GENESIS 3:24 — THE TURNING SWORD ===")
(println)
(println "Start:" (:start gen324) "  End:" (:end gen324))
(println "Length:" (- (:end gen324) (:start gen324)) "letters")
(println)
(println "67 = dim-d = בינה (understanding)")
(println "One complete fiber of understanding.")
(println)

;;; ---- The verse text ----

(let [letters (apply str (map #(c/letter-at s %) (range (:start gen324) (:end gen324))))]
  (println "Text:" letters))

;;; ---- Coordinates: the wrap ----

(println)
(let [start-coord (vec (c/idx->coord (:start gen324)))
      end-coord (vec (c/idx->coord (dec (:end gen324))))]
  (println "Start coord:" start-coord "  (d=" (nth start-coord 3) ")")
  (println "End coord:  " end-coord "  (d=" (nth end-coord 3) ")")
  (println)
  (println "Starts at d=59, ends at d=58.")
  (println "The verse wraps around the understanding axis.")
  (println "The sword turns."))

;;; ---- All 67 letters with coordinates ----

(println)
(println "=== LETTER-BY-LETTER ===")
(println)
(doseq [i (range (:start gen324) (:end gen324))]
  (let [d (c/describe i)]
    (print (format "%s(%d) " (:letter d) (nth (:coord d) 3)))))
(println)

;;; ---- The sword (החרב) ----

(println)
(println "=== THE SWORD (החרב) ===")
(println)
(let [verse-str (apply str (map #(c/letter-at s %) (range (:start gen324) (:end gen324))))
      idx (.indexOf verse-str "החרב")
      sword-start (+ (:start gen324) idx)]
  (println "החרב starts at offset" idx "in verse, position" sword-start)
  (println)
  (doseq [i (range 4)]
    (let [pos (+ sword-start i)
          d (c/describe pos)]
      (println (format "  %s  coord=%s  gv=%3d" (:letter d) (:coord d) (:gematria d)))))

  (println)
  (let [het-pos (+ sword-start 1)]  ;; ח
    (println "The ח of the sword is at:" (vec (c/idx->coord het-pos)))
    (println "d=33 — the midpoint of the understanding axis (0..66).")
    (println "The sword is at the center of understanding.")

    ;;; ---- Fibers from the sword ----

    (println)
    (println "=== FIBERS FROM THE SWORD ===")
    (println "From ח at (0, 4, 10, 33):")
    (println)

    ;; a-axis: 7 days
    (println "── a-axis: the 7 days ──")
    (let [positions (c/fiber :a {:b 4 :c 10 :d 33})]
      (doseq [i (range 7)]
        (let [pos (aget positions i)
              d (c/describe pos)]
          (println (format "  a=%d  %s  gv=%3d  %s" i (:letter d) (:gematria d) (:verse d))))))

    (println)

    ;; c-axis: 13 loves — traces the Fall
    (println "── c-axis: the 13 loves (traces the Fall) ──")
    (let [positions (c/fiber :c {:a 0 :b 4 :d 33})]
      (doseq [i (range 13)]
        (let [pos (aget positions i)
              d (c/describe pos)]
          (println (format "  c=%2d  %s  gv=%3d  %s" i (:letter d) (:gematria d) (:verse d))))))

    (println)

    ;; b-axis: 50 jubilees from the sword
    (println "── b-axis: the 50 jubilees ──")
    (let [positions (c/fiber :b {:a 0 :c 10 :d 33})]
      (println "  Letters:" (apply str (map #(c/letter-at s (aget positions %)) (range 50))))
      (println)
      (doseq [i [0 24 25 49]]
        (let [pos (aget positions i)
              d (c/describe pos)]
          (println (format "  b=%2d  %s  gv=%3d  %s" i (:letter d) (:gematria d) (:verse d))))))))

;;; ---- The turning (המתהפכת) ----

(println)
(println "=== המתהפכת — THE TURNING ===")
(println)
(let [verse-str (apply str (map #(c/letter-at s %) (range (:start gen324) (:end gen324))))
      idx (.indexOf verse-str "המתהפכת")
      abs-start (+ (:start gen324) idx)]
  (println "המתהפכת starts at offset" idx "position" abs-start)
  (println "Gematria:" (reduce + (map #(c/gv-at s (+ abs-start %)) (range 7))))
  (println)
  (doseq [i (range 7)]
    (let [pos (+ abs-start i)
          d (c/describe pos)]
      (println (format "  %s  coord=%s  gv=%3d" (:letter d) (:coord d) (:gematria d))))))

(println)
(println "Done.")
