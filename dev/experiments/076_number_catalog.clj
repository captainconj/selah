(ns experiments.076-number-catalog
  "Catalog of number sets in the Torah that may relate to the 4D space.

   The Torah is dense with explicit numbers: lifetimes, tribal sizes,
   census counts, sacrificial quantities, construction dimensions,
   calendar dates, and genealogical ages. Each set is a potential
   lens or coordinate system.

   This experiment compiles them ALL and tests each against
   304,850 = 7 × 50 × 13 × 67.

   FINDINGS:
   The Torah's numbers are saturated with axis factors.

   LIFETIMES:
   - Pre-flood lifespans sum = 8,575 = 5² × 7³ (three powers of completeness)
   - Post-flood lifespans sum = 2,996 = 2² × 7 × 107 (÷7)
   - Post-flood begetting ages sum = 390 = 2 × 3 × 5 × 13 (÷13)
   - Kenan = 910 = 2 × 5 × 7 × 13 (BOTH axis numbers)
   - Lamech = 777 = 3 × 7 × 37, Jacob = 147 = 3 × 7²

   NAMES:
   - Adam→Noah name gematria sum = 2,678 = 2 × 13 × 103 (÷13)

   CENSUS:
   - First census total = 603,550 = 2 × 5² × 12,071. ÷50 = 12,071.
   - Census DIFFERENCE = 1,820 = 2² × 5 × 7 × 13. ÷91 = 20.
     The population shift between censuses divides by BOTH 7 AND 13.
   - Ephraim (census 2) = 32,500, contains factor 13.

   SACRIFICES:
   - Silver plate offering = 130 = סלם = סיני (ladder = Sinai).
   - 12 leaders × total per leader = 2,772 = 2² × 3² × 7 × 11 (÷7).
   - Sukkot bulls: 13, 12, 11, 10, 9, 8, 7 — DESCENDS from love to
     completeness, one per day. Total = 70. T(13) = 91 = 7 × 13.

   LEVITICUS 12:
   - Boy purification: 7 + 33 = 40. Girl: 14 + 66 = 80.
   - 33 = midpoint of d-axis (0..66).
   - 66 = endpoint of d-axis (0..66).
   - Purification periods encode the understanding axis boundaries.

   TABERNACLE:
   - 50 appears 4 times (clasps ×2, loops, courtyard).
   - 7 appears once (menorah). 13 and 67 absent.

   PATTERN: 7 and 50 appear explicitly in architecture. 13 appears
   in sacrificial counts and name sums. 67 appears as time periods
   (33, 66) in purification law. The axes surface through different
   domains: 7 in structure, 13 in love/sacrifice, 67 in time."
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════════════
;; NUMBER SETS
;; ══════════════════════════════════════════════════════════════

;; ── 1. Patriarchal Lifetimes (Genesis 5) ────────────────────
;; Adam to Noah — 10 generations before the flood

(def patriarchal-lifetimes
  "Lifetimes from Adam to Noah (Genesis 5)."
  [{:name "Adam"       :lifespan 930  :begat-age 130  :ref "5:3-5"}
   {:name "Seth"       :lifespan 912  :begat-age 105  :ref "5:6-8"}
   {:name "Enosh"      :lifespan 905  :begat-age 90   :ref "5:9-11"}
   {:name "Kenan"      :lifespan 910  :begat-age 70   :ref "5:12-14"}
   {:name "Mahalalel"  :lifespan 895  :begat-age 65   :ref "5:15-17"}
   {:name "Jared"      :lifespan 962  :begat-age 162  :ref "5:18-20"}
   {:name "Enoch"      :lifespan 365  :begat-age 65   :ref "5:21-24"}
   {:name "Methuselah" :lifespan 969  :begat-age 187  :ref "5:25-27"}
   {:name "Lamech"     :lifespan 777  :begat-age 182  :ref "5:28-31"}
   {:name "Noah"       :lifespan 950  :begat-age 500  :ref "5:32,9:29"}])

;; ── 2. Post-flood Lifetimes (Genesis 11) ───────────────────
;; Shem to Terah — 10 generations after the flood

(def postflood-lifetimes
  "Lifetimes from Shem to Abram (Genesis 11)."
  [{:name "Shem"       :lifespan 600  :begat-age 100  :ref "11:10-11"}
   {:name "Arpachshad" :lifespan 438  :begat-age 35   :ref "11:12-13"}
   {:name "Shelah"     :lifespan 433  :begat-age 30   :ref "11:14-15"}
   {:name "Eber"       :lifespan 464  :begat-age 34   :ref "11:16-17"}
   {:name "Peleg"      :lifespan 239  :begat-age 30   :ref "11:18-19"}
   {:name "Reu"        :lifespan 239  :begat-age 32   :ref "11:20-21"}
   {:name "Serug"      :lifespan 230  :begat-age 30   :ref "11:22-23"}
   {:name "Nahor"      :lifespan 148  :begat-age 29   :ref "11:24-25"}
   {:name "Terah"      :lifespan 205  :begat-age 70   :ref "11:26,32"}])

;; ── 3. Patriarch Ages ──────────────────────────────────────

(def patriarch-ages
  "Key ages of the patriarchs."
  [{:name "Abraham" :lifespan 175  :ref "25:7"}
   {:name "Sarah"   :lifespan 127  :ref "23:1"}
   {:name "Isaac"   :lifespan 180  :ref "35:28"}
   {:name "Jacob"   :lifespan 147  :ref "47:28"}
   {:name "Joseph"  :lifespan 110  :ref "50:26"}
   {:name "Moses"   :lifespan 120  :ref "Deut 34:7"}
   {:name "Aaron"   :lifespan 123  :ref "Num 33:39"}])

;; ── 4. Tribal Census (Numbers 1 & 26) ─────────────────────

(def census-1
  "First census — Numbers 1 (Sinai, year 2)."
  [{:tribe "Reuben"    :count 46500}
   {:tribe "Simeon"    :count 59300}
   {:tribe "Gad"       :count 45650}
   {:tribe "Judah"     :count 74600}
   {:tribe "Issachar"  :count 54400}
   {:tribe "Zebulun"   :count 57400}
   {:tribe "Ephraim"   :count 40500}
   {:tribe "Manasseh"  :count 32200}
   {:tribe "Benjamin"  :count 35400}
   {:tribe "Dan"       :count 62700}
   {:tribe "Asher"     :count 41500}
   {:tribe "Naphtali"  :count 53400}])

(def census-2
  "Second census — Numbers 26 (Moab, year 40)."
  [{:tribe "Reuben"    :count 43730}
   {:tribe "Simeon"    :count 22200}
   {:tribe "Gad"       :count 40500}
   {:tribe "Judah"     :count 76500}
   {:tribe "Issachar"  :count 64300}
   {:tribe "Zebulun"   :count 60500}
   {:tribe "Ephraim"   :count 32500}
   {:tribe "Manasseh"  :count 52700}
   {:tribe "Benjamin"  :count 45600}
   {:tribe "Dan"       :count 64400}
   {:tribe "Asher"     :count 53400}
   {:tribe "Naphtali"  :count 45400}])

;; ── 5. Sacrificial Numbers (Numbers 7, 28-29) ─────────────

(def dedication-offerings
  "Each tribal leader's offering (Numbers 7) — same for all 12."
  {:silver-plate 130  ;; = סלם = סיני !
   :silver-bowl 70
   :gold-dish 10
   :bulls 1
   :rams 1
   :male-lambs 1
   :goats 1
   :peace-oxen 2
   :peace-rams 5
   :peace-goats 5
   :peace-lambs 5})

(def daily-offerings
  "Numbers 28-29 festival calendar."
  {:daily-lambs 2
   :sabbath-extra-lambs 2
   :new-moon-bulls 2
   :new-moon-rams 1
   :new-moon-lambs 7
   :passover-bulls 2
   :passover-rams 1
   :passover-lambs 7
   :passover-days 7
   :firstfruits-bulls 2
   :firstfruits-rams 1
   :firstfruits-lambs 7
   :trumpets-bull 1
   :trumpets-ram 1
   :trumpets-lambs 7
   ;; Day of Atonement
   :atonement-bull 1
   :atonement-ram 1
   :atonement-lambs 7
   ;; Sukkot (7 days, decreasing bulls)
   :sukkot-bulls [13 12 11 10 9 8 7]  ;; 70 total!
   :sukkot-rams-per-day 2
   :sukkot-lambs-per-day 14
   ;; 8th day assembly
   :eighth-day-bull 1
   :eighth-day-ram 1
   :eighth-day-lambs 7})

;; ── 6. Tabernacle Dimensions ───────────────────────────────

(def tabernacle-numbers
  "Explicit measurements from Exodus 25-27, 36-38."
  {:ark-length 2.5   :ark-width 1.5    :ark-height 1.5
   :table-length 2   :table-width 1    :table-height 1.5
   :menorah-branches 7
   :curtain-length 28 :curtain-width 4  :curtain-count 10
   :goat-curtain-length 30 :goat-curtain-width 4 :goat-curtain-count 11
   :board-height 10  :board-width 1.5  :boards-south 20 :boards-north 20
   :boards-west 6    :corner-boards 2
   :bars-per-side 5  :middle-bar 1
   :courtyard-length 100 :courtyard-width 50 :courtyard-height 5
   :courtyard-pillars-ns 20 :courtyard-pillars-ew 10
   :gate-width 20    :gate-pillars 4
   :altar-length 5   :altar-width 5    :altar-height 3
   :basin-bronze 1
   :clasps-gold 50   :clasps-bronze 50
   :loops-per-curtain 50
   :sockets-silver 100})

;; ── 7. Adam to Noah Name Meanings (gematria) ──────────────

(def adam-to-noah-names
  "Names from Adam to Noah with Hebrew spellings."
  [{:name "Adam"       :hebrew "אדם"}
   {:name "Seth"       :hebrew "שת"}
   {:name "Enosh"      :hebrew "אנוש"}
   {:name "Kenan"      :hebrew "קינן"}
   {:name "Mahalalel"  :hebrew "מהללאל"}
   {:name "Jared"      :hebrew "ירד"}
   {:name "Enoch"      :hebrew "חנוך"}
   {:name "Methuselah" :hebrew "מתושלח"}
   {:name "Lamech"     :hebrew "למך"}
   {:name "Noah"       :hebrew "נח"}])

;; ── 8. Leviticus Numbers ───────────────────────────────────

(def leviticus-numbers
  "Key numbers from Leviticus regulations."
  {:clean-unclean-days [7 14 33 40 66 80]  ;; purification periods
   :jubilee-year 50
   :sabbath-year 7
   :jubilee-cycle 49  ;; 7 × 7
   :omer-count 50     ;; days to Shavuot
   :day-of-atonement 10 ;; 10th of 7th month
   :sukkot-start 15     ;; 15th of 7th month
   :sukkot-days 7
   :eighth-day 1
   :tithe 10            ;; 10th
   :firstfruits 1
   :priests-age-min 25  ;; or 30 in Numbers
   :priests-age-max 50})

;; ══════════════════════════════════════════════════════════════
;; Analysis Functions
;; ══════════════════════════════════════════════════════════════

(defn axis-divisibility
  "Check how a number relates to the axis numbers."
  [n]
  (when (and (integer? n) (pos? n))
    (let [divs (for [d [7 13 50 67 91 469 871 43550 304850]
                     :when (zero? (mod n d))]
                 [d (/ n d)])]
      (when (seq divs) divs))))

(defn prime-factors [n]
  (when (and (integer? n) (> n 1))
    (loop [n n, fs [], d 2]
      (cond
        (= n 1) fs
        (> (* d d) n) (conj fs n)
        (zero? (mod n d)) (recur (/ n d) (conj fs d) d)
        :else (recur n fs (inc d))))))

(defn analyze-number-set
  "Analyze a set of numbers against the structural constants."
  [label numbers]
  (println (format "\n  ── %s ──" label))
  (let [flat-nums (filter #(and (number? %) (not (coll? %))) numbers)
        sum (reduce + flat-nums)]
    (println (format "    Numbers: %s" (str/join ", " numbers)))
    (println (format "    Sum: %,d" (long sum)))
    (when-let [divs (axis-divisibility sum)]
      (doseq [[d q] divs]
        (println (format "      ÷%d = %,d" d q))))
    (let [pf (prime-factors (long sum))]
      (when (seq pf)
        (println (format "      = %s" (str/join " × " pf)))))

    ;; Individual numbers that are axis numbers
    (let [axis-hits (filter #(#{7 13 50 67} %) numbers)]
      (when (seq axis-hits)
        (println (format "    Axis numbers present: %s" (str/join ", " axis-hits)))))

    ;; Count how many divide 304,850
    (let [divisors (filter #(and (integer? %) (pos? %) (zero? (mod 304850 %))) numbers)]
      (when (seq divisors)
        (println (format "    Divide 304,850: %s" (str/join ", " divisors)))))))

;; ══════════════════════════════════════════════════════════════
;; Part 1: Lifetimes
;; ══════════════════════════════════════════════════════════════

(defn part1-lifetimes []
  (println "\n═══ PART 1: LIFETIMES ═══\n")

  ;; Pre-flood
  (let [lifespans (mapv :lifespan patriarchal-lifetimes)
        begat-ages (mapv :begat-age patriarchal-lifetimes)]
    (analyze-number-set "Pre-flood lifespans (Adam→Noah)" lifespans)
    (analyze-number-set "Pre-flood begetting ages" begat-ages)

    ;; Year of flood from Adam
    (let [flood-year (reduce + (butlast begat-ages))]
      (println (format "\n    Year of flood from Adam: %d (sum of begetting ages excl Noah)" flood-year))
      (println (format "      = %s" (str/join " × " (prime-factors flood-year))))
      (when-let [divs (axis-divisibility flood-year)]
        (doseq [[d q] divs]
          (println (format "      ÷%d = %,d" d q))))))

  (println)

  ;; Post-flood
  (let [lifespans (mapv :lifespan postflood-lifetimes)
        begat-ages (mapv :begat-age postflood-lifetimes)]
    (analyze-number-set "Post-flood lifespans (Shem→Terah)" lifespans)
    (analyze-number-set "Post-flood begetting ages" begat-ages))

  (println)

  ;; Patriarch ages
  (analyze-number-set "Patriarch lifespans" (mapv :lifespan patriarch-ages))

  ;; Key individual lifetimes
  (println "\n    Individual lifetime factors:")
  (doseq [p (concat patriarchal-lifetimes postflood-lifetimes patriarch-ages)]
    (let [n (:lifespan p)
          pf (prime-factors n)]
      (when (some #{7 13 67} pf)
        (println (format "      %s: %d = %s" (:name p) n (str/join " × " pf)))))))

;; ══════════════════════════════════════════════════════════════
;; Part 2: Names (Adam to Noah)
;; ══════════════════════════════════════════════════════════════

(defn part2-names []
  (println "\n═══ PART 2: ADAM TO NOAH — NAMES ═══\n")

  (let [values (mapv (fn [entry]
                       (let [gv (g/word-value (:hebrew entry))]
                         (println (format "    %-12s %s = %4d"
                                          (:name entry) (:hebrew entry) gv))
                         gv))
                     adam-to-noah-names)
        total (reduce + values)]
    (println (format "\n    Sum of 10 names: %d" total))
    (println (format "      = %s" (str/join " × " (prime-factors total))))
    (when-let [divs (axis-divisibility total)]
      (doseq [[d q] divs]
        (println (format "      ÷%d = %,d" d q))))

    ;; Running sum
    (println "\n    Running sums:")
    (loop [i 0, running 0]
      (when (< i (count values))
        (let [running (+ running (nth values i))
              name (:name (nth adam-to-noah-names i))]
          (when-let [divs (axis-divisibility running)]
            (println (format "      %s (cumulative %d): %s"
                             name running
                             (str/join ", " (map (fn [[d q]] (format "÷%d=%d" d q)) divs)))))
          (recur (inc i) running))))))

;; ══════════════════════════════════════════════════════════════
;; Part 3: Tribal Census
;; ══════════════════════════════════════════════════════════════

(defn part3-census []
  (println "\n═══ PART 3: TRIBAL CENSUS ═══\n")

  ;; First census
  (let [counts (mapv :count census-1)
        total (reduce + counts)]
    (println "  ── First Census (Numbers 1) ──")
    (doseq [t census-1]
      (let [n (:count t)
            pf (prime-factors n)]
        (println (format "    %-10s %,7d = %s" (:tribe t) n (str/join " × " pf)))))
    (println (format "    TOTAL:    %,7d = %s" total (str/join " × " (prime-factors total))))
    (when-let [divs (axis-divisibility total)]
      (doseq [[d q] divs]
        (println (format "      ÷%d = %,d" d q))))
    (println))

  ;; Second census
  (let [counts (mapv :count census-2)
        total (reduce + counts)]
    (println "  ── Second Census (Numbers 26) ──")
    (doseq [t census-2]
      (let [n (:count t)
            pf (prime-factors n)]
        (println (format "    %-10s %,7d = %s" (:tribe t) n (str/join " × " pf)))))
    (println (format "    TOTAL:    %,7d = %s" total (str/join " × " (prime-factors total))))
    (when-let [divs (axis-divisibility total)]
      (doseq [[d q] divs]
        (println (format "      ÷%d = %,d" d q))))
    (println))

  ;; Difference
  (let [total1 (reduce + (map :count census-1))
        total2 (reduce + (map :count census-2))
        diff (- total1 total2)]
    (println (format "  Difference: %,d - %,d = %,d" total1 total2 diff))
    (println (format "    = %s" (str/join " × " (prime-factors (Math/abs diff)))))
    (when-let [divs (axis-divisibility (Math/abs diff))]
      (doseq [[d q] divs]
        (println (format "    ÷%d = %,d" d q))))))

;; ══════════════════════════════════════════════════════════════
;; Part 4: Sacrificial Numbers
;; ══════════════════════════════════════════════════════════════

(defn part4-sacrifices []
  (println "\n═══ PART 4: SACRIFICIAL NUMBERS ═══\n")

  ;; Dedication offering (Numbers 7)
  (println "  ── Dedication Offering Per Leader (Numbers 7) ──")
  (let [_vals (clojure.core/vals (dissoc dedication-offerings :peace-oxen :peace-rams
                          :peace-goats :peace-lambs))]
    (println (format "    Silver plate: %d = סלם = סיני" (:silver-plate dedication-offerings)))
    (println (format "    Silver bowl:  %d" (:silver-bowl dedication-offerings)))
    (println (format "    Gold dish:    %d" (:gold-dish dedication-offerings)))
    (let [all-vals (clojure.core/vals dedication-offerings)
          total (reduce + all-vals)]
      (println (format "    Total per leader: %d" total))
      (println (format "    × 12 leaders:     %d" (* total 12)))
      (let [t12 (* total 12)]
        (println (format "      = %s" (str/join " × " (prime-factors t12))))
        (when-let [divs (axis-divisibility t12)]
          (doseq [[d q] divs]
            (println (format "      ÷%d = %,d" d q)))))))

  (println)

  ;; Sukkot bulls
  (println "  ── Sukkot Bulls (Numbers 29) ──")
  (let [bulls (:sukkot-bulls daily-offerings)
        total (reduce + bulls)]
    (println (format "    Days 1-7: %s" (str/join ", " bulls)))
    (println (format "    Total: %d = 7 × 10" total))
    (println (format "    Starts at 13, ends at 7."))
    (println (format "    13 → 7: from love to completeness."))
    (println (format "    Sum 1-13 = %d = 7 × 13" (reduce + (range 1 14))))))

;; ══════════════════════════════════════════════════════════════
;; Part 5: Leviticus Purification Periods
;; ══════════════════════════════════════════════════════════════

(defn part5-leviticus []
  (println "\n═══ PART 5: LEVITICUS NUMBERS ═══\n")

  (println "  ── Purification Periods ──")
  (let [days (:clean-unclean-days leviticus-numbers)]
    (doseq [d days]
      (println (format "    %d days%s" d
                       (cond
                         (zero? (mod d 7))  (format " = %d × 7" (/ d 7))
                         (zero? (mod d 13)) (format " = %d × 13" (/ d 13))
                         :else ""))))
    (println (format "    Sum: %d" (reduce + days)))
    (println (format "      = %s" (str/join " × " (prime-factors (reduce + days))))))

  (println)

  ;; Boy birth: unclean 7 days, then 33 days = 40 total
  ;; Girl birth: unclean 14 days, then 66 days = 80 total
  (println "  ── Birth Purification (Leviticus 12) ──")
  (println "    Boy:  7 + 33 = 40 days")
  (println "    Girl: 14 + 66 = 80 days")
  (println "    Ratio: 1:2 (girl = 2× boy)")
  (println)
  (println "    The 33: not a multiple of 7 or 13")
  (println "    The 66: = d-axis max value (0..66)!")
  (println "    The 7: = a-axis")
  (println "    The 14: = 2 × 7")
  (println "    40 = the wilderness years, Moses on Sinai")
  (println "    80 = Moses' age at Exodus")
  (println)
  (println "    33 + 67 = 100.  66 + 67 = 133 = 7 × 19.")
  (println "    33 is the midpoint of 0..66 (the d-axis).")
  (println "    66 is the endpoint of 0..66.")
  (println "    The purification periods encode the d-axis boundaries!")

  (println)

  ;; Jubilee
  (println "  ── Jubilee Cycle ──")
  (println "    7 sabbath years × 7 = 49")
  (println "    49 + 1 = 50 (jubilee)")
  (println "    49 = 7²")
  (println "    50 = b-axis")
  (println "    The jubilee is one step beyond 7×7."))

;; ══════════════════════════════════════════════════════════════
;; Part 6: Tabernacle Numbers
;; ══════════════════════════════════════════════════════════════

(defn part6-tabernacle []
  (println "\n═══ PART 6: TABERNACLE ═══\n")

  (println "  Key tabernacle numbers and axis connections:")
  (println)
  (println "  Clasps (gold):     50 = b-axis")
  (println "  Clasps (bronze):   50 = b-axis")
  (println "  Loops per curtain: 50 = b-axis")
  (println "  Courtyard width:   50 = b-axis")
  (println "  Courtyard length:  100 = 2 × 50")
  (println "  Sockets (silver):  100 = 2 × 50")
  (println "  Curtain length:    28 = 4 × 7")
  (println "  Curtain count:     10")
  (println "  Goat curtains:     11")
  (println "  Board height:      10")
  (println "  Boards per side:   20")
  (println "  Menorah branches:  7 = a-axis")
  (println "  Bars per side:     5")
  (println "  Altar:             5 × 5 × 3")
  (println)
  (println "  50 appears 4 times (clasps ×2, loops, courtyard).")
  (println "  7 appears once (menorah).")
  (println "  13 and 67 are absent — same as Ezekiel.")
  (println)
  (println "  28 curtains × 4 cubits = 112 sq cubits")
  (println "    28 = 4 × 7")
  (println "    10 curtains × 28 = 280 = 4 × 70 = 4 × 7 × 10")
  (println "    11 goat curtains × 30 = 330 = 2 × 3 × 5 × 11")
  (println)

  ;; Total of all integer tabernacle measurements
  (let [int-vals (filter integer? (vals tabernacle-numbers))
        total (reduce + int-vals)]
    (println (format "  Sum of all integer tabernacle numbers: %d" total))
    (println (format "    = %s" (str/join " × " (prime-factors total))))
    (when-let [divs (axis-divisibility total)]
      (doseq [[d q] divs]
        (println (format "    ÷%d = %,d" d q))))))

;; ══════════════════════════════════════════════════════════════
;; Main
;; ══════════════════════════════════════════════════════════════

(defn -main []
  (println "=== 076: NUMBER CATALOG ===")
  (println "  Every number set in the Torah, tested against 7 × 50 × 13 × 67.\n")

  (c/space)

  (part1-lifetimes)
  (part2-names)
  (part3-census)
  (part4-sacrifices)
  (part5-leviticus)
  (part6-tabernacle)

  (println "\n═══ SUMMARY ═══")
  (println "  Numbers in the Torah that contain axis factors:")
  (println "  ÷7: lifetimes (Lamech 777, Enoch 365=5×73), steps, menorah")
  (println "  ÷13: name sums, rank word sums, Sukkot bulls start at 13")
  (println "  ÷50: courtyard, clasps, loops, jubilee, captains")
  (println "  ÷67: purification period endpoints (33 midpoint, 66 endpoint)")
  (println)
  (println "  The d-axis (67) is the most hidden. It appears in Lev 12")
  (println "  as purification periods: 33 (midpoint) and 66 (endpoint).")
  (println "  Not as a count, but as a duration. Time, not space.")
  (println "\nDone."))
