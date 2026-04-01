(ns experiments.137b-passing-through
  "Experiment 137b: Passing Through the Door.

   The Ark of the Covenant sits in the 5D space [5,7,10,13,67]
   at He=2, completeness=3, spanning [10,13,67].

   What happens when we walk along the two pinning axes —
   through the five breaths and seven days —
   while holding the Ark's position fixed in the cube?

   What text occupies the Ark's coordinates at each slab?"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.oracle :as o]
            [clojure.string :as str]))

(def dims [5 7 10 13 67])

;; Cube spans axes 2,3,4 = [10,13,67]
;; Pinned at axis 0 (He) = 2, axis 1 (completeness) = 3
;; Cube ranges: axis2=[0,10), axis3=[0,13), axis4=[0,67)
;; Wait — let me recalculate from the center.

;; Center of dims: [2, 3, 5, 6, 33]
;; Cube: 10 units centered at each cube axis center
;;   axis2: center=5, range [0,10)  (5-5=0, 0+10=10) — fills entire axis!
;;   axis3: center=6, range [1,11)  (6-5=1, 1+10=11)
;;   axis4: center=33, range [28,38) (33-5=28, 28+10=38)

(def cube-center [2 3 5 6 33])

;; The Ark within the cube (offsets from cube start):
;; length [4,5,6], width [4,5,6], height [3,4,5,6,7]
;; Cube starts: axis2=0, axis3=1, axis4=28
;; So Ark absolute coords:
;;   axis2: 0+4..0+6 = [4,5,6]
;;   axis3: 1+4..1+6 = [5,6,7]
;;   axis4: 28+3..28+7 = [31,32,33,34,35]

(defn make-coord [he comp a2 a3 a4]
  [he comp a2 a3 a4])

(defn pos-at [coord]
  (u/coord->idx dims coord))

;; ── Walk through breath (He axis, 0..4) ────────────────

(defn ark-at-breath
  "The Ark's letters at a given He value, completeness=3."
  [s he]
  (let [positions (for [a2 [4 5 6]
                        a3 [5 6 7]
                        a4 [31 32 33 34 35]]
                    (pos-at (make-coord he 3 a2 a3 a4)))
        letters (mapv #(c/letter-at s %) positions)]
    {:he he
     :positions positions
     :letters letters
     :text (apply str letters)
     :gv (reduce + (mapv #(c/gv-at s %) positions))
     :count (count letters)}))

(defn spine-at-breath
  "The Ark's central spine (where YHWH lives) at a given He.
   axis2=5 (center), axis3=6 (center), axis4=31..35"
  [s he]
  (let [positions (for [d [31 32 33 34 35]]
                    (pos-at (make-coord he 3 5 6 d)))
        letters (mapv #(c/letter-at s %) positions)]
    {:he he
     :letters letters
     :text (apply str letters)
     :verses (mapv #(u/verse-str s %) positions)}))

(defn mercy-seat-at-breath
  "Mercy Seat row at a given He.
   axis2=[4,5,6], axis3=7 (top), axis4=[31..35]"
  [s he]
  (let [positions (for [a2 [4 5 6] a4 [31 32 33 34 35]]
                    (pos-at (make-coord he 3 a2 7 a4)))
        letters (mapv #(c/letter-at s %) positions)]
    {:he he
     :letters letters
     :text (apply str letters)
     :gv (reduce + (mapv #(c/gv-at s %) positions))}))

(defn between-at-breath
  "Between the cherubim at a given He.
   axis2=[4,5,6], axis3=7, axis4=[32,33,34]"
  [s he]
  (let [positions (for [a2 [4 5 6] a4 [32 33 34]]
                    (pos-at (make-coord he 3 a2 7 a4)))
        letters (mapv #(c/letter-at s %) positions)
        normalized (map (fn [ch]
                          (get {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ} ch ch))
                        letters)
        text (apply str normalized)
        readings (o/parse-letters text {:vocab :dict})]
    {:he he
     :text (apply str letters)
     :oracle (mapv :text readings)}))

;; ── Walk through completeness (axis 1, 0..6) ──────────

(defn ark-at-day
  "The Ark's letters at a given completeness value, He=2."
  [s day]
  (let [positions (for [a2 [4 5 6]
                        a3 [5 6 7]
                        a4 [31 32 33 34 35]]
                    (pos-at (make-coord 2 day a2 a3 a4)))
        letters (mapv #(c/letter-at s %) positions)]
    {:day day
     :positions positions
     :letters letters
     :text (apply str letters)
     :gv (reduce + (mapv #(c/gv-at s %) positions))
     :count (count letters)}))

(defn spine-at-day
  "The Ark's central spine at a given day.
   He=2, axis2=5, axis3=6, axis4=31..35"
  [s day]
  (let [positions (for [d [31 32 33 34 35]]
                    (pos-at (make-coord 2 day 5 6 d)))
        letters (mapv #(c/letter-at s %) positions)]
    {:day day
     :letters letters
     :text (apply str letters)
     :verses (mapv #(u/verse-str s %) positions)}))

(defn between-at-day
  "Between the cherubim at a given day."
  [s day]
  (let [positions (for [a2 [4 5 6] a4 [32 33 34]]
                    (pos-at (make-coord 2 day a2 7 a4)))
        letters (mapv #(c/letter-at s %) positions)
        normalized (map (fn [ch]
                          (get {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ} ch ch))
                        letters)
        text (apply str normalized)
        readings (o/parse-letters text {:vocab :dict})]
    {:day day
     :text (apply str letters)
     :oracle (mapv :text readings)}))

;; ── Full grid: every He × every day ────────────────────

(defn spine-at
  "The 5-letter spine at any (he, day)."
  [s he day]
  (let [positions (for [d [31 32 33 34 35]]
                    (pos-at (make-coord he day 5 6 d)))
        letters (mapv #(c/letter-at s %) positions)]
    {:he he :day day
     :text (apply str letters)
     :verses (mapv #(u/verse-str s %) positions)}))

(defn between-at
  "Between cherubim at any (he, day)."
  [s he day]
  (let [positions (for [a2 [4 5 6] a4 [32 33 34]]
                    (pos-at (make-coord he day a2 7 a4)))
        letters (mapv #(c/letter-at s %) positions)
        normalized (map (fn [ch]
                          (get {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ} ch ch))
                        letters)
        text (apply str normalized)
        readings (o/parse-letters text {:vocab :dict})]
    {:he he :day day
     :text (apply str letters)
     :oracle (mapv :text readings)}))

;; ── Run ────────────────────────────────────────────────

(defn run-breath-walk
  "Walk through the 5 breath slabs."
  []
  (let [s (c/space)]
    (println "═══ Walking Through Breath (He axis, 0..4) ═══")
    (println "Completeness fixed at 3 (center day)\n")

    (doseq [he (range 5)]
      (let [ark (ark-at-breath s he)
            spine (spine-at-breath s he)
            mercy (mercy-seat-at-breath s he)
            between (between-at-breath s he)]
        (println (str "── He = " he " ──"))
        (println (str "  Ark GV: " (:gv ark)
                      (when (zero? (mod (:gv ark) 137))
                        (str " = " (quot (:gv ark) 137) " × 137"))))
        (println (str "  Spine: " (:text spine)
                      "  ← " (str/join " | " (:verses spine))))
        (println (str "  Mercy Seat GV: " (:gv mercy)))
        (println (str "  Between cherubim: " (:text between)))
        (when (seq (:oracle between))
          (println (str "  Oracle: " (str/join ", " (take 6 (:oracle between)))
                        (when (> (count (:oracle between)) 6) "..."))))
        (println)))))

(defn run-day-walk
  "Walk through the 7 completeness slabs."
  []
  (let [s (c/space)]
    (println "═══ Walking Through Completeness (day axis, 0..6) ═══")
    (println "He (breath) fixed at 2 (center breath)\n")

    (doseq [day (range 7)]
      (let [ark (ark-at-day s day)
            spine (spine-at-day s day)
            between (between-at-day s day)]
        (println (str "── Day " day " ──"))
        (println (str "  Ark GV: " (:gv ark)
                      (when (zero? (mod (:gv ark) 137))
                        (str " = " (quot (:gv ark) 137) " × 137"))))
        (println (str "  Spine: " (:text spine)
                      "  ← " (str/join " | " (:verses spine))))
        (println (str "  Between cherubim: " (:text between)))
        (when (seq (:oracle between))
          (println (str "  Oracle: " (str/join ", " (take 6 (:oracle between)))
                        (when (> (count (:oracle between)) 6) "..."))))
        (println)))))

(defn run-full-grid
  "The 5×7 grid of spines and oracles."
  []
  (let [s (c/space)]
    (println "═══ The 5×7 Grid: Every Breath × Every Day ═══")
    (println "Spine (5 letters at Ark center) and oracle between cherubim\n")

    (println "── Spines ──")
    (doseq [he (range 5)]
      (doseq [day (range 7)]
        (let [sp (spine-at s he day)]
          (print (format "  [%d,%d] %-8s" he day (:text sp)))))
      (println))

    (println "\n── Oracle readings (between cherubim) ──")
    (doseq [he (range 5)]
      (doseq [day (range 7)]
        (let [bt (between-at s he day)
              first-reading (first (:oracle bt))]
          (print (format "  [%d,%d] %-20s" he day (or first-reading "—")))))
      (println))

    (println "\n── Ark GV grid ──")
    (print "       ")
    (doseq [day (range 7)] (print (format "day%-6d" day)))
    (println)
    (doseq [he (range 5)]
      (print (format "  He=%d  " he))
      (let [s' s]
        (doseq [day (range 7)]
          (let [ark (if (= [he day] [2 3])
                      (ark-at-breath s' he)  ;; canonical position
                      ;; generic: compute ark at this (he,day)
                      (let [positions (for [a2 [4 5 6]
                                            a3 [5 6 7]
                                            a4 [31 32 33 34 35]]
                                        (pos-at (make-coord he day a2 a3 a4)))
                            gv (reduce + (mapv #(c/gv-at s' %) positions))]
                        {:gv gv}))]
            (print (format "%-9d" (:gv ark))))))
      (println))))

(defn run-all []
  (run-breath-walk)
  (println "\n\n")
  (run-day-walk)
  (println "\n\n")
  (run-full-grid))

;; ── REPL ───────────────────────────────────────────────

(comment
  (c/space)
  (run-breath-walk)
  (run-day-walk)
  (run-full-grid)
  (run-all)
  nil)
