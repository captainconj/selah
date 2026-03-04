(ns scripts.holy-of-holies
  "Spatial mapping of the Holy of Holies and its furniture
   in the 4D Torah coordinate space."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.oracle :as o]))

;; ── Constants ────────────────────────────────────────────
;;
;; Center of space: (3,25,6,33) = Lev 8:35
;;
;; Holy of Holies: 10×10×10 cube centered there
;;   a=3, b=20..29, c=1..10, d=28..37
;;
;; Ark: 5×3×3 (half-cubits: 2.5×1.5×1.5)
;;   b=24..26, c=5..7, d=31..35
;;
;; Mercy Seat: top face of Ark
;;   b=24..26, c=7, d=31..35
;;
;; Right Cherub: b=24..26, c=7, d=31
;; Left Cherub:  b=24..26, c=7, d=35
;; Between:      b=24..26, c=7, d=32..34
;; Above:        b=24..26, c=8..10, d=31..35
;; In Front:     b=20..23, c=5..7, d=31..35

(defn pos [a b cc d] (c/coord->idx a b cc d))

;; ── Region builders ──────────────────────────────────────

(defn hoh-positions []
  (for [b (range 20 30) cc (range 1 11) d (range 28 38)]
    (pos 3 b cc d)))

(defn ark-positions []
  (for [b (range 24 27) cc (range 5 8) d (range 31 36)]
    (pos 3 b cc d)))

(defn mercy-seat-positions []
  (for [b (range 24 27) d (range 31 36)]
    (pos 3 b 7 d)))

(defn right-cherub-positions []
  (for [b (range 24 27)]
    (pos 3 b 7 31)))

(defn left-cherub-positions []
  (for [b (range 24 27)]
    (pos 3 b 7 35)))

(defn between-cherubim-positions []
  (for [b (range 24 27) d (range 32 35)]
    (pos 3 b 7 d)))

(defn above-ark-positions []
  (for [b (range 24 27) cc (range 8 11) d (range 31 36)]
    (pos 3 b cc d)))

(defn in-front-positions []
  (for [b (range 20 24) cc (range 5 8) d (range 31 36)]
    (pos 3 b cc d)))

;; ── Cross axes through center ────────────────────────────

(defn a-axis-positions []
  (for [a (range 7)] (pos a 25 6 33)))

(defn b-axis-positions []
  (for [b (range 50)] (pos 3 b 6 33)))

(defn c-axis-positions []
  (for [cc (range 13)] (pos 3 25 cc 33)))

(defn d-axis-positions []
  (for [d (range 67)] (pos 3 25 6 d)))

;; ── Analysis helpers ─────────────────────────────────────

(defn region-info
  "Compute letters, GV, and verses for a set of positions."
  [positions]
  (let [s (c/space)
        letters (mapv #(c/letter-at s %) positions)
        gvs (mapv #(c/gv-at s %) positions)
        verses (into (sorted-set)
                     (map #(let [v (c/verse-at s %)]
                             (str (:book v) " " (:ch v) ":" (:vs v)))
                          positions))]
    {:letters letters
     :text (apply str letters)
     :count (count letters)
     :gv (reduce + gvs)
     :verses verses}))

(defn describe-region [label positions]
  (let [{:keys [text count gv verses]} (region-info positions)]
    (println (str "=== " label " ==="))
    (println (str "  " count " letters | GV=" gv))
    (println (str "  " text))
    (println "  Verses:")
    (doseq [v verses]
      (println (str "    " v)))
    (println)))

(defn oracle-region
  "Run the oracle on a region's letters."
  [positions & {:keys [vocab] :or {vocab :dict}}]
  (let [s (c/space)
        letters (map #(c/letter-at s %) positions)
        ;; Normalize final forms
        text (apply str (map {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ}
                             letters
                             letters))]
    (o/parse-letters text {:vocab vocab})))

;; ── Run ──────────────────────────────────────────────────

(comment
  ;; Initialize space
  (c/space)
  nil

  ;; All regions
  (describe-region "HOLY OF HOLIES (10×10×10)" (hoh-positions))
  (describe-region "THE ARK (5×3×3)" (ark-positions))
  (describe-region "MERCY SEAT" (mercy-seat-positions))
  (describe-region "RIGHT CHERUB" (right-cherub-positions))
  (describe-region "LEFT CHERUB" (left-cherub-positions))
  (describe-region "BETWEEN THE CHERUBIM" (between-cherubim-positions))
  (describe-region "ABOVE THE ARK" (above-ark-positions))
  (describe-region "IN FRONT OF THE ARK" (in-front-positions))

  ;; Cross axes
  (describe-region "A-AXIS (completeness, 7)" (a-axis-positions))
  (describe-region "B-AXIS (jubilee, 50)" (b-axis-positions))
  (describe-region "C-AXIS (love, 13)" (c-axis-positions))
  (describe-region "D-AXIS (understanding, 67)" (d-axis-positions))

  ;; Oracle readings
  ;; Between the cherubim → dict: איש מות משה (man, death, Moses)
  (let [results (oracle-region (between-cherubim-positions) :vocab :dict)]
    (doseq [p results]
      (println (:text p) "→" (clojure.string/join " / " (:meanings p)))))

  ;; Right cherub: ימי (my days)
  (let [results (oracle-region (right-cherub-positions) :vocab :torah)]
    (doseq [p results]
      (println (:text p) "→" (clojure.string/join " / " (:meanings p)))))

  ;; Left cherub: קיר (wall)
  (let [results (oracle-region (left-cherub-positions) :vocab :torah)]
    (doseq [p results]
      (println (:text p) "→" (clojure.string/join " / " (:meanings p)))))

  ;; Lev 8:35 in the Ark: d=31..35 at b=25, c=6
  ;; Letters: י ה ו ה ו = YHWH + vav
  (doseq [d (range 31 36)]
    (let [desc (c/describe (c/coord->idx 3 25 6 d))]
      (println (str "d=" d " → " (:letter desc) " — " (:verse desc)))))

  ;; Lev 9:1 on the Mercy Seat: מהשמי = "what is my name?"
  (doseq [d (range 31 36)]
    (let [desc (c/describe (c/coord->idx 3 25 7 d))]
      (println (str "d=" d " → " (:letter desc) " — " (:verse desc)))))

  ;; Detailed per-position
  (doseq [b (range 24 27) cc (range 5 8) d (range 31 36)]
    (let [p (c/coord->idx 3 b cc d)
          desc (c/describe p)]
      (println (str "(" b "," cc "," d ") " (:letter desc) " — " (:verse desc)))))

  ;; Cross beam analysis
  ;; d-axis: left arm (d=0..32), nail (d=33), right arm (d=34..66)
  (let [s (c/space)
        left-gv  (reduce + (for [d (range 33)]  (c/gv-at s (c/coord->idx 3 25 6 d))))
        right-gv (reduce + (for [d (range 34 67)] (c/gv-at s (c/coord->idx 3 25 6 d))))
        nail-gv  (c/gv-at s (c/coord->idx 3 25 6 33))]
    {:left left-gv :nail nail-gv :right right-gv
     :total (+ left-gv nail-gv right-gv)})

  ;; === THE CROSS ===
  ;;
  ;; Horizontal beam (d-axis, understanding, 67 letters)
  ;;   Left end: ו,י = nail, hand (d=0,1)
  ;;   Center: ו = nail (d=33) — Lev 8:35
  ;;   Right end: א,ת = aleph-tav (d=65,66)
  ;;   33 + 1 + 33 symmetric arms
  ;;   Left GV=3437 | Nail=6 | Right GV=2627 | Total=6070
  ;;
  ;; Vertical beam (c-axis, love, 13 letters)
  ;;   Bottom: ל (teach, c=0) — Lev 8:29
  ;;   Center: ו (nail, c=6) — Lev 8:35
  ;;   Top: א (aleph/silent, c=12) — Lev 9:7 (approach the altar)
  ;;   Top 3: א,ה,י = beginning of אהיה (I AM)
  ;;   GV=622 = 2×311 = 2×GV(איש/man)
  ;;
  ;; 13 nails (ו) on the cross = one per unit of love
  ;; 79 total positions (prime)
  ;; Cross GV = 6686 = 2 × 3343 (prime)
  ;;
  ;; A-axis spine (7 letters): GV=73 = GV(חכמה) = wisdom
  ;;
  ;; Left corner: וי = 6+10 = 16. Vertical = 13.  16/13 = cross ratio.
  ;;
  ;; Mercy Seat line (c=7, b=25):
  ;;   d=14..17: יהוה (the Name)
  ;;   d=18..20: ביד (by the hand of)
  ;;   d=21..23: משה (Moses)
  ;;   d=33: ש (shin/fire)
  ;;   Distance from hand to fire = 14 = GV(יד)

  ;; Cross endpoints
  (let [s (c/space)]
    {:left   (c/letter-at s (c/coord->idx 3 25 6 0))   ;; ו (nail)
     :right  (c/letter-at s (c/coord->idx 3 25 6 66))   ;; ת (mark)
     :bottom (c/letter-at s (c/coord->idx 3 25 0 33))   ;; ל (teach)
     :top    (c/letter-at s (c/coord->idx 3 25 12 33))}) ;; א (silent)

  ;; Nail count on the cross
  (let [s (c/space)]
    (->> (concat
           (for [d (range 67)] (c/coord->idx 3 25 6 d))
           (for [cc (range 13) :when (not= cc 6)] (c/coord->idx 3 25 cc 33)))
         (filter #(= \ו (c/letter-at s %)))
         count))
  ;; => 13

  ;; Mercy Seat centerline (c=7, b=25) — YHWH → hand → Moses → fire
  (let [s (c/space)]
    (doseq [d (range 67)]
      (let [desc (c/describe (c/coord->idx 3 25 7 d))]
        (print (:letter desc))))
    (println))

  ;; Hand-to-fire distance
  ;; ביד at d=18..20, ש at d=33
  ;; center of ביד ≈ d=19, distance to d=33 = 14 = GV(יד)

  nil)
