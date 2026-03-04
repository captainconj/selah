(ns experiments.099-festival-calendar-path
  "Experiment 099 — Festival Calendar as Coordinate Path.

   The Torah prescribes a liturgical calendar with precise numbers:
   Passover day 14, Unleavened 7 days, Omer count 50 days,
   Trumpets day 1 month 7, Atonement day 10 month 7,
   Sukkot 7 days with bulls 13→7.

   These numbers are axis values. Question: does the festival calendar,
   read as a coordinate sequence, trace a path through the Torah
   that passes through the passages prescribing the festivals?

   Also: the Sukkot bull descent 13,12,11,10,9,8,7 as a c-axis walk."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── Helpers ─────────────────────────────────────────────────

(defn- save-edn [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(def data-dir "data/experiments/099/")

(defn- describe-pos [pos]
  (let [s (c/space)
        coord (vec (c/idx->coord pos))
        v (c/verse-at s pos)]
    {:position pos
     :coord    coord
     :letter   (str (c/letter-at s pos))
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :book     (:book v)
     :chapter  (:ch v)
     :vs       (:vs v)}))

;; ── Festival Calendar ───────────────────────────────────────

(def festival-chapters
  "Chapters where festivals are prescribed."
  #{["Leviticus" 23] ["Numbers" 28] ["Numbers" 29]
    ["Deuteronomy" 16] ["Exodus" 12] ["Exodus" 23]})

(defn- festival-verse? [desc]
  (contains? festival-chapters [(:book desc) (:chapter desc)]))

(def festivals
  "Festival calendar with associated numbers and Lev 23 verse references."
  [{:name "Passover"
    :numbers [14 1]  ;; day 14, month 1
    :ref "Lev 23:5"
    :note "14th of 1st month"}
   {:name "Unleavened Bread"
    :numbers [7 15 1]  ;; 7 days, starting 15th, month 1
    :ref "Lev 23:6"
    :note "7 days from 15th of 1st month"}
   {:name "Firstfruits"
    :numbers [1]  ;; the day after Sabbath
    :ref "Lev 23:10"
    :note "day after Sabbath in Unleavened week"}
   {:name "Shavuot (Pentecost)"
    :numbers [50 7]  ;; 50 days, 7 weeks
    :ref "Lev 23:15-16"
    :note "50 days = 7 weeks + 1"}
   {:name "Trumpets (Rosh Hashanah)"
    :numbers [1 7]  ;; day 1, month 7
    :ref "Lev 23:24"
    :note "1st day of 7th month"}
   {:name "Day of Atonement"
    :numbers [10 7]  ;; day 10, month 7
    :ref "Lev 23:27"
    :note "10th of 7th month. 490=7²×10=Thummim"}
   {:name "Sukkot"
    :numbers [15 7 7]  ;; day 15, month 7, 7 days
    :ref "Lev 23:34"
    :note "15th of 7th month, 7 days"}
   {:name "Shemini Atzeret"
    :numbers [8]  ;; the 8th day
    :ref "Lev 23:36"
    :note "8th day assembly"}])

;; ══════════════════════════════════════════════════════════════
;; Phase 1: Festival Numbers as Coordinates
;; ══════════════════════════════════════════════════════════════

(defn phase-1-festival-coordinates
  "Each festival has associated numbers. Map them to coordinates
   and check if they hit festival-prescribing chapters."
  []
  (println "\n══ Phase 1: Festival Numbers as Coordinates ══")
  (let [results
        (vec
         (for [f festivals
               :let [nums (:numbers f)]]
           (do
             (println (str "\n  " (:name f) " — numbers: " (pr-str nums)))
             ;; Try numbers as different axis assignments
             (let [lookups
                   (vec
                    (for [;; For each number, try it on each axis
                          ;; Use center values for unassigned axes
                          n nums
                          axis [:a :b :c :d]
                          :let [[a b c d] (case axis
                                            :a [(min n 6) 25 6 33]
                                            :b [3 (min n 49) 6 33]
                                            :c [3 25 (min n 12) 33]
                                            :d [3 25 6 (min n 66)])]
                          :when (case axis
                                  :a (<= n 6)
                                  :b (<= n 49)
                                  :c (<= n 12)
                                  :d (<= n 66))]
                      (let [pos (c/coord->idx a b c d)
                            desc (describe-pos pos)
                            hit? (festival-verse? desc)]
                        (println (str "    " n " on " (name axis)
                                      " → (" a "," b "," c "," d ")"
                                      " → " (:verse desc)
                                      (when hit? " ★ FESTIVAL")))
                        (assoc desc :number n :axis axis
                               :coord [a b c d] :festival-hit? hit?))))]
               {:festival (:name f) :numbers nums
                :total-lookups (count lookups)
                :festival-hits (count (filter :festival-hit? lookups))
                :lookups lookups}))))]

    ;; Summary
    (let [total (reduce + (map :total-lookups results))
          hits (reduce + (map :festival-hits results))]
      (println (str "\n── Summary ──"))
      (println (str "  Total lookups: " total))
      (println (str "  Festival chapter hits: " hits
                    " (" (format "%.1f" (* 100.0 (/ hits (max 1 total)))) "%)")))
    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 2: The Sukkot Descent — c-axis walk
;; ══════════════════════════════════════════════════════════════

(defn phase-2-sukkot-descent
  "Sukkot bulls: 13,12,11,10,9,8,7.
   These are c-axis values (love axis, range 0-12).
   Walk c=12 down to c=6 at various fixed (a,b,d) values."
  []
  (println "\n══ Phase 2: Sukkot Bull Descent as c-Axis Walk ══")
  (let [bulls [13 12 11 10 9 8 7]
        c-vals (mapv dec bulls)  ;; 0-indexed: 12,11,10,9,8,7,6

        ;; Walk at center (a=3, b=25, d=33)
        center-walk
        (vec
         (for [[day c-val bull] (map vector (range 1 8) c-vals bulls)]
           (let [pos (c/coord->idx 3 25 c-val 33)
                 desc (describe-pos pos)]
             (println (str "  Day " day ": " bull " bulls, c=" c-val
                           " → " (:verse desc) " [" (:letter desc) "]"))
             {:day day :bulls bull :c-val c-val :verse (:verse desc)})))

        ;; Walk at a=0 (Genesis), b=25, d=33
        genesis-walk
        (do
          (println (str "\n  — Same walk at a=0 (Genesis) —"))
          (vec
           (for [[day c-val bull] (map vector (range 1 8) c-vals bulls)]
             (let [pos (c/coord->idx 0 25 c-val 33)
                   desc (describe-pos pos)]
               (println (str "  Day " day ": c=" c-val
                             " → " (:verse desc)))
               {:day day :c-val c-val :verse (:verse desc)}))))

        ;; Sum = 70. What's at position 70?
        _ (println (str "\n  Bull sum = 70"))
        _ (let [desc70 (describe-pos 70)]
            (println (str "  Position 70: " (:verse desc70) " " (:coord desc70))))

        ;; 70 as coordinate
        _ (let [desc (describe-pos (c/coord->idx 3 25 6 (mod 70 67)))]
            (println (str "  70 mod 67 = " (mod 70 67) " → d=" (mod 70 67)
                          " → " (:verse desc))))]

    {:center-walk center-walk :genesis-walk genesis-walk}))

;; ══════════════════════════════════════════════════════════════
;; Phase 3: The Calendar Sequence as a Path
;; ══════════════════════════════════════════════════════════════

(defn phase-3-calendar-path
  "Walk the annual liturgical calendar as a coordinate sequence.
   Each month/day pair becomes coordinates."
  []
  (println "\n══ Phase 3: Calendar as Coordinate Path ══")
  (let [;; Major calendar points: [month day description]
        calendar [[1 14 "Passover lamb slain"]
                  [1 15 "Unleavened Bread begins"]
                  [1 21 "Unleavened Bread ends"]
                  [3 6  "Shavuot (Pentecost)"]  ;; ~50 days after Passover
                  [7 1  "Trumpets"]
                  [7 10 "Day of Atonement"]
                  [7 15 "Sukkot begins"]
                  [7 21 "Sukkot ends"]
                  [7 22 "Shemini Atzeret"]]

        ;; Map month→b, day→d (both fit axis ranges)
        results
        (vec
         (for [[month day desc] calendar
               :let [;; month as a (mod 7), day as d, keep c=6 (center)
                     a (mod month 7)
                     d (min day 66)
                     pos (c/coord->idx a 25 6 d)
                     vdesc (describe-pos pos)
                     ;; Also: month as c (mod 13), day as d
                     c (mod month 13)
                     pos2 (c/coord->idx 3 25 c d)
                     vdesc2 (describe-pos pos2)]]
           (do
             (println (str "\n  " desc " (month " month ", day " day ")"))
             (println (str "    month→a: (" a ",25,6," d ") → " (:verse vdesc)
                           (when (festival-verse? vdesc) " ★")))
             (println (str "    month→c: (3,25," c "," d ") → " (:verse vdesc2)
                           (when (festival-verse? vdesc2) " ★")))
             {:event desc :month month :day day
              :via-a {:coord [a 25 6 d] :verse (:verse vdesc)
                      :hit? (festival-verse? vdesc)}
              :via-c {:coord [3 25 c d] :verse (:verse vdesc2)
                      :hit? (festival-verse? vdesc2)}})))]
    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 4: Where Do the Festival Verses Actually Sit?
;; ══════════════════════════════════════════════════════════════

(defn phase-4-festival-verse-coordinates
  "Find the actual 4D coordinates of each festival prescription verse.
   Do their coordinates contain the numbers they prescribe?"
  []
  (println "\n══ Phase 4: Festival Verse Coordinates ══")
  (let [s (c/space)
        ;; Find specific verses
        target-verses [["Leviticus" 23 5]   ;; Passover
                       ["Leviticus" 23 6]   ;; Unleavened
                       ["Leviticus" 23 15]  ;; Omer count
                       ["Leviticus" 23 16]  ;; Pentecost
                       ["Leviticus" 23 24]  ;; Trumpets
                       ["Leviticus" 23 27]  ;; Atonement
                       ["Leviticus" 23 34]  ;; Sukkot
                       ["Leviticus" 23 36]] ;; Shemini Atzeret
        results
        (vec
         (for [[book ch vs] target-verses
               :let [vref (some (fn [v]
                                  (when (and (= (:book v) book)
                                             (= (:ch v) ch)
                                             (= (:vs v) vs))
                                    v))
                                (:verse-ref s))]
               :when vref]
           (let [start (:start vref)
                 coord (vec (c/idx->coord start))
                 [a b c-val d] coord]
             (println (str "  " book " " ch ":" vs
                           " starts at position " start
                           " = (" a "," b "," c-val "," d ")"))
             {:verse (str book " " ch ":" vs)
              :position start :coord coord
              :a a :b b :c c-val :d d})))]

    ;; Check: do any coordinate values match prescribed numbers?
    (println (str "\n── Coordinate-Number Matches ──"))
    (let [prescriptions {5 14, 6 7, 15 50, 16 50, 24 1, 27 10, 34 15, 36 8}]
      (doseq [r results
              :let [vs-num (Integer/parseInt (last (str/split (:verse r) #":")))
                    prescribed (get prescriptions vs-num)]]
        (when prescribed
          (let [matches (filterv (fn [[axis val]]
                                   (= val prescribed))
                                 [[:a (:a r)] [:b (:b r)]
                                  [:c (:c r)] [:d (:d r)]])]
            (when (seq matches)
              (println (str "  ★ " (:verse r) " prescribes " prescribed
                            ", coordinate " (first (first matches))
                            "=" (second (first matches)))))))))
    results))

;; ══════════════════════════════════════════════════════════════
;; Run All
;; ══════════════════════════════════════════════════════════════

(defn run-all []
  (let [t0 (System/currentTimeMillis)]
    (println "═══════════════════════════════════════════════")
    (println " Experiment 099: Festival Calendar Path")
    (println "═══════════════════════════════════════════════")
    (c/space)

    (let [p1 (phase-1-festival-coordinates)
          _ (save-edn (str data-dir "phase1-festival-coords.edn") p1)
          p2 (phase-2-sukkot-descent)
          _ (save-edn (str data-dir "phase2-sukkot.edn") p2)
          p3 (phase-3-calendar-path)
          _ (save-edn (str data-dir "phase3-calendar.edn") p3)
          p4 (phase-4-festival-verse-coordinates)
          _ (save-edn (str data-dir "phase4-verse-coords.edn") p4)
          elapsed (quot (- (System/currentTimeMillis) t0) 1000)]

      (println (str "\n═══ Complete in " elapsed "s ═══"))
      {:phase1 p1 :phase2 p2 :phase3 p3 :phase4 p4})))

(comment
  (def results (run-all))
  )
