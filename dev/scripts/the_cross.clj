(ns scripts.the-cross
  "The cross at the center of the 4D Torah space.
   Understanding (67) × Love (13), nail (ו) at the intersection."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]))

;; ── The cross ────────────────────────────────────────────
;;
;; Two beams through center (3,25,6,33) = Lev 8:35:
;;
;;   Horizontal: d-axis (understanding, 67 letters)
;;     a=3, b=25, c=6, d=0..66
;;     All Lev 8:35–36
;;
;;   Vertical: c-axis (love, 13 letters)
;;     a=3, b=25, d=33, c=0..12
;;     Lev 8:29 through 9:7
;;
;;   Spine: a-axis (completeness, 7 letters)
;;     b=25, c=6, d=33, a=0..6
;;     Genesis through Deuteronomy

;; ── Position builders ────────────────────────────────────

(defn horizontal-beam
  "d-axis: 67 positions, understanding."
  []
  (for [d (range 67)] (c/coord->idx 3 25 6 d)))

(defn vertical-beam
  "c-axis: 13 positions, love."
  []
  (for [cc (range 13)] (c/coord->idx 3 25 cc 33)))

(defn spine
  "a-axis: 7 positions, completeness."
  []
  (for [a (range 7)] (c/coord->idx a 25 6 33)))

(defn full-cross
  "All 79 unique positions on the cross (d + c, center counted once)."
  []
  (distinct (concat (horizontal-beam) (vertical-beam))))

(defn mercy-seat-line
  "c=7, b=25: the line one layer above the horizontal beam."
  []
  (for [d (range 67)] (c/coord->idx 3 25 7 d)))

;; ── Analysis ─────────────────────────────────────────────

(defn beam-info [positions]
  (let [s (c/space)
        letters (mapv #(c/letter-at s %) positions)
        gvs (mapv #(c/gv-at s %) positions)]
    {:text (apply str letters)
     :letters letters
     :gv (reduce + gvs)
     :count (count letters)}))

(defn nail-count
  "Count vavs (ו) on the cross."
  []
  (let [s (c/space)]
    (->> (full-cross)
         (filter #(= \ו (c/letter-at s %)))
         count)))

(defn arm-gvs
  "GV of left arm, nail, right arm on the horizontal beam."
  []
  (let [s (c/space)
        left  (reduce + (for [d (range 33)]    (c/gv-at s (c/coord->idx 3 25 6 d))))
        nail  (c/gv-at s (c/coord->idx 3 25 6 33))
        right (reduce + (for [d (range 34 67)] (c/gv-at s (c/coord->idx 3 25 6 d))))]
    {:mercy left :nail nail :truth right
     :total (+ left nail right)}))

(defn endpoints
  "The four endpoints of the cross."
  []
  (let [s (c/space)]
    {:mercy   {:letter (c/letter-at s (c/coord->idx 3 25 6 0))
              :verse  (:verse (c/describe (c/coord->idx 3 25 6 0)))}
     :truth  {:letter (c/letter-at s (c/coord->idx 3 25 6 66))
              :verse  (:verse (c/describe (c/coord->idx 3 25 6 66)))}
     :bottom {:letter (c/letter-at s (c/coord->idx 3 25 0 33))
              :verse  (:verse (c/describe (c/coord->idx 3 25 0 33)))}
     :top    {:letter (c/letter-at s (c/coord->idx 3 25 12 33))
              :verse  (:verse (c/describe (c/coord->idx 3 25 12 33)))}}))

(defn mercy-seat-words
  "Find YHWH, ביד, משה on the mercy seat centerline."
  []
  (let [s (c/space)
        line (mercy-seat-line)
        letters (mapv #(c/letter-at s %) line)
        text (apply str letters)
        find-at (fn [word]
                  (let [idx (.indexOf text word)]
                    (when (>= idx 0)
                      {:word word :d-start idx :d-end (+ idx (count word) -1)})))]
    {:text text
     :yhwh (find-at "יהוה")
     :hand (find-at "ביד")
     :moses (find-at "משה")
     :fire {:letter (nth letters 33) :d 33}
     :hand-to-fire-distance (when-let [h (find-at "ביד")]
                              (- 33 (+ (:d-start h) 1)))}))

;; ── Run ──────────────────────────────────────────────────

(comment
  ;; Initialize
  (c/space)
  nil

  ;; === HORIZONTAL BEAM (understanding, 67) ===
  (beam-info (horizontal-beam))
  ;; Left end: ו,י = nail, hand
  ;; Center: ו = nail (Lev 8:35)
  ;; Right end: א,ת = aleph-tav
  ;; GV=6070. Text = Lev 8:35-36.

  ;; Arms
  (arm-gvs)
  ;; {:mercy 3437 :nail 6 :truth 2627 :total 6070}
  ;; 33 + 1 + 33 symmetric

  ;; === VERTICAL BEAM (love, 13) ===
  (beam-info (vertical-beam))
  ;; GV=622 = 2 × 311 = 2 × GV(איש/man)
  ;; Bottom: ל (teach). Top: א (silent/first).
  ;; Top 3 descending: א,ה,י = beginning of אהיה (I AM)

  ;; Per-letter vertical
  (doseq [cc (range 13)]
    (let [p (c/coord->idx 3 25 cc 33)
          d (c/describe p)]
      (println (str "c=" cc " " (:letter d) " (" (:gematria d) ") " (:verse d)))))

  ;; === SPINE (completeness, 7) ===
  (beam-info (spine))
  ;; GV=73 = GV(חכמה) = wisdom
  ;; בחאואנה — one letter per book

  (doseq [a (range 7)]
    (let [d (c/describe (c/coord->idx a 25 6 33))]
      (println (str "a=" a " " (:letter d) " (" (:gematria d) ") " (:verse d)))))

  ;; === NAILS ===
  (nail-count)
  ;; => 13 = love = one

  ;; Nail positions on horizontal beam
  (let [s (c/space)]
    (doseq [d (range 67)]
      (when (= \ו (c/letter-at s (c/coord->idx 3 25 6 d)))
        (println (str "d=" d)))))
  ;; d=0,2,5,18,33,35,40,42,48,52,60,64 (12 on horizontal)
  ;; plus c=2 on vertical = 13 total

  ;; === ENDPOINTS ===
  (endpoints)
  ;; Left: ו (nail) — Lev 8:35
  ;; Right: ת (tav/mark) — Lev 8:36
  ;; Bottom: ל (teach) — Lev 8:29
  ;; Top: א (silent/first) — Lev 9:7 (approach the altar)

  ;; === CROSS RATIO ===
  ;; Left corner: וי = 6+10 = 16
  ;; Vertical beam = 13 letters
  ;; 16/13 = the cross ratio from experiment 076

  ;; === MERCY SEAT LINE (one layer up) ===
  (mercy-seat-words)
  ;; d=14..17: יהוה
  ;; d=18..20: ביד (by the hand of)
  ;; d=21..23: משה
  ;; d=33: ש (shin/fire)
  ;; hand-to-fire = 14 = GV(יד)

  ;; Full mercy seat centerline
  (let [s (c/space)]
    (doseq [d (range 67)]
      (let [desc (c/describe (c/coord->idx 3 25 7 d))]
        (print (:letter desc))))
    (println))

  ;; === FULL CROSS ===
  ;; 79 positions (prime)
  ;; GV = 6686 = 2 × 3343 (prime)
  (let [{h :gv} (beam-info (horizontal-beam))
        {v :gv} (beam-info (vertical-beam))
        center (c/gv-at (c/space) (c/coord->idx 3 25 6 33))]
    {:cross-gv (- (+ h v) center)
     :positions (count (full-cross))})

  nil)
