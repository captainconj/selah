(ns experiments.074-ezekiel-dimensions
  "Ezekiel's Temple dimensions — nine chapters of measurements for a
   temple never built (Ezekiel 40-48).

   If the Torah is a 4D space (304,850 = 7 × 50 × 13 × 67), then the
   architectural blueprints in scripture may describe coordinate systems.
   The Tabernacle already encodes 50 (courtyard width, clasps, loops).
   Does Ezekiel's vision encode additional structure?

   Approach:
   1. Compile every measurement from Ezekiel 40-48
   2. Test products, sums, and factorizations against structural numbers
   3. Check which measurement sets produce valid lens decompositions
   4. Compare to Tabernacle and Solomon's Temple

   FINDINGS:
   Three of four axis numbers appear directly in Ezekiel's temple:
   - 7 = outer gate steps (40:22, 40:26)
   - 13 = gate passage length (40:11)
   - 50 = gate total length (6 gates), building width, open space, outer wall
   - 67 is ABSENT — the understanding axis has no architectural expression

   Products reach 4,550 but not 304,850:
   - 7 × 13 = 91 (two-factor)
   - 7 × 50 = 350 = קרן = ספיר (three ways: 5×70, 7×50, 14×25)
   - 7 × 13 × 50 = 4,550 = half-stride (three ways)
   - No four-factor Ezekiel product = 304,850 or 43,550
   - 67 is prime and absent — the bottleneck

   Zero pure-Ezekiel 4D factorizations of 304,850 (of 41 possible).
   But 39 of 41 have ≥2 Ezekiel factors — the temple provides most of the lens.

   25 × 13 = 325 divides 304,850, 43,550, and 4,550. Gate width × passage.
   304,850 / 325 = 938 = 2 × 7 × 67.

   All large numbers (25000, 10000, 5000, 4500, 1000, 500, 250) ÷ 50.
   The jubilee axis (50) scales the sacred district. Powers of 2, 3, 5
   dominate construction — 7 and 13 appear only in ritual/symbolic counts.

   Interpretation: Ezekiel gives you the control surface (7, 13, 50) but
   not the understanding axis (67). The architecture provides three
   coordinates — the fourth must come from elsewhere (בינה = understanding
   is not built, it is received). The 50 gates of understanding in
   Kabbalistic tradition: 50 × 67 = 3,350 — but only 49 are accessible."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════════════
;; Ezekiel 40-48: Compiled Measurements
;; ══════════════════════════════════════════════════════════════
;;
;; The measuring reed = 6 long cubits (each cubit = standard cubit + handbreadth)
;; All measurements in cubits unless noted as "reeds"

(def ezekiel-measurements
  "Every explicit measurement from Ezekiel 40-48, structured."
  [;; ── Chapter 40: The Outer Court ──────────────────────────
   ;; The wall around the temple
   {:ref "40:5"  :structure "outer wall"       :dim "thickness" :value 6  :unit "reeds" :cubits 6}
   {:ref "40:5"  :structure "outer wall"       :dim "height"    :value 6  :unit "reeds" :cubits 6}

   ;; East gate
   {:ref "40:6"  :structure "east gate threshold" :dim "depth"  :value 6  :unit "reeds" :cubits 6}
   {:ref "40:7"  :structure "east gate chamber"   :dim "length" :value 6  :unit "reeds" :cubits 6}
   {:ref "40:7"  :structure "east gate chamber"   :dim "width"  :value 6  :unit "reeds" :cubits 6}
   {:ref "40:7"  :structure "east gate between chambers" :dim "width" :value 5 :unit "cubits" :cubits 5}
   {:ref "40:9"  :structure "east gate inner threshold"  :dim "depth" :value 8 :unit "cubits" :cubits 8}
   {:ref "40:9"  :structure "east gate jambs"    :dim "width"   :value 2  :unit "cubits" :cubits 2}
   {:ref "40:11" :structure "east gate opening"  :dim "width"   :value 10 :unit "cubits" :cubits 10}
   {:ref "40:11" :structure "east gate passage"  :dim "length"  :value 13 :unit "cubits" :cubits 13}
   {:ref "40:12" :structure "east gate barrier"  :dim "height"  :value 1  :unit "cubits" :cubits 1}
   {:ref "40:13" :structure "east gate total"    :dim "width"   :value 25 :unit "cubits" :cubits 25}
   {:ref "40:14" :structure "east gate posts"    :dim "height"  :value 60 :unit "cubits" :cubits 60}
   {:ref "40:15" :structure "east gate total"    :dim "length"  :value 50 :unit "cubits" :cubits 50}

   ;; Outer court
   {:ref "40:17" :structure "outer court chambers" :dim "count"  :value 30 :unit "rooms"  :cubits 30}
   {:ref "40:19" :structure "outer court"         :dim "width"  :value 100 :unit "cubits" :cubits 100}

   ;; North gate (same pattern as east)
   {:ref "40:20-22" :structure "north gate"      :dim "length" :value 50 :unit "cubits" :cubits 50}
   {:ref "40:20-22" :structure "north gate"      :dim "width"  :value 25 :unit "cubits" :cubits 25}

   ;; South gate (same pattern)
   {:ref "40:24-26" :structure "south gate"      :dim "length" :value 50 :unit "cubits" :cubits 50}
   {:ref "40:24-26" :structure "south gate"      :dim "width"  :value 25 :unit "cubits" :cubits 25}

   ;; Steps
   {:ref "40:22" :structure "outer gate steps"   :dim "count"  :value 7  :unit "steps" :cubits 7}
   {:ref "40:26" :structure "south gate steps"   :dim "count"  :value 7  :unit "steps" :cubits 7}

   ;; ── Chapter 40 continued: Inner Court ────────────────────
   {:ref "40:27" :structure "inner court"        :dim "distance" :value 100 :unit "cubits" :cubits 100}
   {:ref "40:29" :structure "inner south gate"   :dim "length" :value 50 :unit "cubits" :cubits 50}
   {:ref "40:29" :structure "inner south gate"   :dim "width"  :value 25 :unit "cubits" :cubits 25}
   {:ref "40:30" :structure "inner gate porticos" :dim "length" :value 25 :unit "cubits" :cubits 25}
   {:ref "40:30" :structure "inner gate porticos" :dim "width"  :value 5  :unit "cubits" :cubits 5}
   {:ref "40:31" :structure "inner gate steps"   :dim "count"  :value 8  :unit "steps" :cubits 8}

   ;; Inner east gate
   {:ref "40:32-34" :structure "inner east gate" :dim "length" :value 50 :unit "cubits" :cubits 50}
   {:ref "40:32-34" :structure "inner east gate" :dim "width"  :value 25 :unit "cubits" :cubits 25}
   {:ref "40:34" :structure "inner east steps"   :dim "count"  :value 8  :unit "steps" :cubits 8}

   ;; Inner north gate
   {:ref "40:35-37" :structure "inner north gate" :dim "length" :value 50 :unit "cubits" :cubits 50}
   {:ref "40:35-37" :structure "inner north gate" :dim "width"  :value 25 :unit "cubits" :cubits 25}
   {:ref "40:37" :structure "inner north steps"  :dim "count"  :value 8  :unit "steps" :cubits 8}

   ;; Tables for sacrifice
   {:ref "40:41" :structure "sacrifice tables"   :dim "count"  :value 8  :unit "tables" :cubits 8}
   {:ref "40:42" :structure "hewn stone tables"  :dim "count"  :value 4  :unit "tables" :cubits 4}
   {:ref "40:42" :structure "hewn stone table"   :dim "length" :value 1.5 :unit "cubits" :cubits 1.5}
   {:ref "40:42" :structure "hewn stone table"   :dim "width"  :value 1.5 :unit "cubits" :cubits 1.5}
   {:ref "40:42" :structure "hewn stone table"   :dim "height" :value 1  :unit "cubits" :cubits 1}

   ;; Priests' chambers
   {:ref "40:44" :structure "singers' chamber"   :dim "facing" :value 1  :unit "south" :cubits 1}
   {:ref "40:49" :structure "vestibule"          :dim "length" :value 20 :unit "cubits" :cubits 20}
   {:ref "40:49" :structure "vestibule"          :dim "width"  :value 12 :unit "cubits" :cubits 12} ;; some read 11
   {:ref "40:49" :structure "vestibule steps"    :dim "count"  :value 10 :unit "steps" :cubits 10}
   {:ref "40:49" :structure "vestibule pillars"  :dim "count"  :value 2  :unit "pillars" :cubits 2}

   ;; ── Chapter 41: The Temple Building ──────────────────────
   {:ref "41:1"  :structure "temple jambs"       :dim "width"  :value 6  :unit "cubits" :cubits 6}
   {:ref "41:2"  :structure "temple entrance"    :dim "width"  :value 10 :unit "cubits" :cubits 10}
   {:ref "41:2"  :structure "temple entrance walls" :dim "width" :value 5 :unit "cubits" :cubits 5}
   {:ref "41:2"  :structure "nave (holy place)"  :dim "length" :value 40 :unit "cubits" :cubits 40}
   {:ref "41:2"  :structure "nave (holy place)"  :dim "width"  :value 20 :unit "cubits" :cubits 20}
   {:ref "41:3"  :structure "inner room entrance" :dim "width" :value 6  :unit "cubits" :cubits 6}
   {:ref "41:3"  :structure "inner entrance jambs" :dim "width" :value 2 :unit "cubits" :cubits 2}
   {:ref "41:4"  :structure "most holy place"    :dim "length" :value 20 :unit "cubits" :cubits 20}
   {:ref "41:4"  :structure "most holy place"    :dim "width"  :value 20 :unit "cubits" :cubits 20}

   ;; Side chambers
   {:ref "41:5"  :structure "temple outer wall"  :dim "thickness" :value 6 :unit "cubits" :cubits 6}
   {:ref "41:5"  :structure "side chamber"       :dim "width"  :value 4  :unit "cubits" :cubits 4}
   {:ref "41:6"  :structure "side chambers"      :dim "stories" :value 3 :unit "floors" :cubits 3}
   {:ref "41:6"  :structure "side chambers per floor" :dim "count" :value 30 :unit "rooms" :cubits 30}
   {:ref "41:8"  :structure "side chamber foundation" :dim "height" :value 6 :unit "cubits" :cubits 6}
   {:ref "41:9"  :structure "side chamber outer wall" :dim "thickness" :value 5 :unit "cubits" :cubits 5}
   {:ref "41:10" :structure "open space"         :dim "width"  :value 20 :unit "cubits" :cubits 20}
   {:ref "41:11" :structure "side chamber doors" :dim "count"  :value 2  :unit "doors"  :cubits 2}
   {:ref "41:12" :structure "west building"      :dim "width"  :value 70 :unit "cubits" :cubits 70}
   {:ref "41:12" :structure "west building"      :dim "length" :value 90 :unit "cubits" :cubits 90}
   {:ref "41:12" :structure "west building wall" :dim "thickness" :value 5 :unit "cubits" :cubits 5}

   ;; Total measurements
   {:ref "41:13" :structure "temple total"       :dim "length" :value 100 :unit "cubits" :cubits 100}
   {:ref "41:14" :structure "temple east face"   :dim "width"  :value 100 :unit "cubits" :cubits 100}
   {:ref "41:15" :structure "west building + galleries" :dim "length" :value 100 :unit "cubits" :cubits 100}

   ;; Interior woodwork
   {:ref "41:22" :structure "wooden altar"       :dim "height" :value 3  :unit "cubits" :cubits 3}
   {:ref "41:22" :structure "wooden altar"       :dim "length" :value 2  :unit "cubits" :cubits 2}
   {:ref "41:22" :structure "wooden altar"       :dim "width"  :value 2  :unit "cubits" :cubits 2}
   {:ref "41:23" :structure "temple doors"       :dim "count"  :value 2  :unit "doors"  :cubits 2}
   {:ref "41:24" :structure "door leaves"        :dim "count"  :value 2  :unit "per door" :cubits 2}

   ;; ── Chapter 42: Priests' Chambers ────────────────────────
   {:ref "42:2"  :structure "north building"     :dim "length" :value 100 :unit "cubits" :cubits 100}
   {:ref "42:2"  :structure "north building"     :dim "width"  :value 50  :unit "cubits" :cubits 50}
   {:ref "42:3"  :structure "gallery levels"     :dim "count"  :value 3   :unit "floors" :cubits 3}
   {:ref "42:4"  :structure "inner walkway"      :dim "width"  :value 10  :unit "cubits" :cubits 10}
   {:ref "42:4"  :structure "inner walkway"      :dim "length" :value 100 :unit "cubits" :cubits 100} ;; variant: 1 cubit
   {:ref "42:7"  :structure "outer wall"         :dim "length" :value 50  :unit "cubits" :cubits 50}
   {:ref "42:8"  :structure "outer chambers"     :dim "length" :value 50  :unit "cubits" :cubits 50}

   ;; ── Chapter 42: The Outer Precinct ───────────────────────
   {:ref "42:15-20" :structure "temple precinct" :dim "length" :value 500 :unit "reeds" :cubits 500}
   {:ref "42:15-20" :structure "temple precinct" :dim "width"  :value 500 :unit "reeds" :cubits 500}

   ;; ── Chapter 43: The Altar ────────────────────────────────
   {:ref "43:13" :structure "altar base"         :dim "height" :value 1  :unit "cubits" :cubits 1}
   {:ref "43:13" :structure "altar base"         :dim "width"  :value 1  :unit "cubits" :cubits 1}
   {:ref "43:14" :structure "altar lower ledge"  :dim "height" :value 2  :unit "cubits" :cubits 2}
   {:ref "43:14" :structure "altar lower ledge"  :dim "inset"  :value 1  :unit "cubits" :cubits 1}
   {:ref "43:14" :structure "altar upper ledge"  :dim "height" :value 4  :unit "cubits" :cubits 4}
   {:ref "43:14" :structure "altar upper ledge"  :dim "inset"  :value 1  :unit "cubits" :cubits 1}
   {:ref "43:15" :structure "altar hearth"       :dim "height" :value 4  :unit "cubits" :cubits 4}
   {:ref "43:16" :structure "altar hearth"       :dim "length" :value 12 :unit "cubits" :cubits 12}
   {:ref "43:16" :structure "altar hearth"       :dim "width"  :value 12 :unit "cubits" :cubits 12}
   {:ref "43:17" :structure "altar ledge"        :dim "length" :value 14 :unit "cubits" :cubits 14}
   {:ref "43:17" :structure "altar ledge"        :dim "width"  :value 14 :unit "cubits" :cubits 14}
   {:ref "43:17" :structure "altar border"       :dim "width"  :value 0.5 :unit "cubits" :cubits 0.5}
   {:ref "43:17" :structure "altar steps"        :dim "facing" :value 1  :unit "east" :cubits 1}

   ;; ── Chapter 45: The Sacred District ──────────────────────
   {:ref "45:1"  :structure "sacred district"    :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "45:1"  :structure "sacred district"    :dim "width"  :value 10000 :unit "cubits" :cubits 10000}
   {:ref "45:2"  :structure "temple square"      :dim "side"   :value 500   :unit "cubits" :cubits 500}
   {:ref "45:2"  :structure "temple open space"  :dim "width"  :value 50    :unit "cubits" :cubits 50}
   {:ref "45:3"  :structure "sanctuary area"     :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "45:3"  :structure "sanctuary area"     :dim "width"  :value 10000 :unit "cubits" :cubits 10000}
   {:ref "45:5"  :structure "Levites' portion"   :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "45:5"  :structure "Levites' portion"   :dim "width"  :value 10000 :unit "cubits" :cubits 10000}
   {:ref "45:6"  :structure "city property"      :dim "width"  :value 5000  :unit "cubits" :cubits 5000}
   {:ref "45:6"  :structure "city property"      :dim "length" :value 25000 :unit "cubits" :cubits 25000}

   ;; ── Chapter 46: The Prince's Gate ────────────────────────
   ;; (mostly regulations, few new dimensions)

   ;; ── Chapter 47: The River ────────────────────────────────
   {:ref "47:3"  :structure "river measurement"  :dim "distance" :value 1000 :unit "cubits" :cubits 1000}
   ;; measured 4 times: ankle, knee, waist, swimming

   ;; ── Chapter 48: Land Division ────────────────────────────
   {:ref "48:8"  :structure "sacred portion"     :dim "width"  :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:9"  :structure "offering to LORD"   :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:9"  :structure "offering to LORD"   :dim "width"  :value 10000 :unit "cubits" :cubits 10000}
   {:ref "48:10" :structure "priests' portion"   :dim "north"  :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:10" :structure "priests' portion"   :dim "west"   :value 10000 :unit "cubits" :cubits 10000}
   {:ref "48:10" :structure "priests' portion"   :dim "east"   :value 10000 :unit "cubits" :cubits 10000}
   {:ref "48:10" :structure "priests' portion"   :dim "south"  :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:13" :structure "Levites' portion"   :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:13" :structure "Levites' portion"   :dim "width"  :value 10000 :unit "cubits" :cubits 10000}
   {:ref "48:15" :structure "common land"        :dim "width"  :value 5000  :unit "cubits" :cubits 5000}
   {:ref "48:15" :structure "common land"        :dim "length" :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:16" :structure "city"               :dim "side"   :value 4500  :unit "cubits" :cubits 4500}
   {:ref "48:17" :structure "city pasture"       :dim "side"   :value 250   :unit "cubits" :cubits 250}
   {:ref "48:20" :structure "total offering"     :dim "side"   :value 25000 :unit "cubits" :cubits 25000}
   {:ref "48:30" :structure "city gates"         :dim "count"  :value 12    :unit "gates"  :cubits 12}
   {:ref "48:31" :structure "gates per side"     :dim "count"  :value 3     :unit "gates"  :cubits 3}
   {:ref "48:35" :structure "city perimeter"     :dim "total"  :value 18000 :unit "cubits" :cubits 18000}])

;; ── Extract unique numbers ───────────────────────────────────

(def ezekiel-numbers
  "All unique integer measurements from Ezekiel's temple."
  (->> ezekiel-measurements
       (map :cubits)
       (filter integer?)
       distinct
       sort
       vec))

;; ── Structural numbers to test against ──────────────────────

(def structural-numbers
  "Numbers from the 4D Torah space."
  {304850  "total letters"
   43550   "stride-a (÷7)"
   871     "stride-b (÷50) = 13×67"
   67      "dim-d (understanding)"
   13      "dim-c (love)"
   50      "dim-b (jubilee)"
   7       "dim-a (completeness)"
   469     "slab size (7×67)"
   4550    "half-stride-b (÷2)"
   1222    "twice-Torah / center diagonal"
   611     "gematria of תורה"
   350     "קרן = ספיר = 7×50"
   2701    "Gen 1:1 = 37×73"
   91      "7×13"})

;; ── Part 1: Which structural numbers appear directly? ────────

(defn part1-direct-matches []
  (println "\n═══ PART 1: DIRECT MATCHES ═══\n")
  (println "  Ezekiel numbers that ARE structural numbers:\n")

  (let [ez-set (set ezekiel-numbers)]
    (doseq [[n label] (sort-by key structural-numbers)]
      (when (ez-set n)
        (let [refs (->> ezekiel-measurements
                        (filter #(= n (int (or (:cubits %) 0))))
                        (map #(str (:ref %) " " (:structure %))))]
          (println (format "  %6d = %-30s  ← %s" n label (str/join "; " refs))))))

    (println "\n  Ezekiel numbers NOT in structural set:")
    (doseq [n ezekiel-numbers
            :when (not (get structural-numbers n))]
      (println (format "  %6d" n)))))

;; ── Part 2: Products of Ezekiel numbers ─────────────────────

(defn part2-products []
  (println "\n═══ PART 2: PRODUCTS OF EZEKIEL NUMBERS ═══\n")
  (println "  Which products of 2-4 Ezekiel numbers equal structural numbers?\n")

  ;; Two-factor products
  (println "  ── Two-factor products ──")
  (let [hits (atom [])]
    (doseq [i (range (count ezekiel-numbers))
            j (range i (count ezekiel-numbers))]
      (let [a (nth ezekiel-numbers i)
            b (nth ezekiel-numbers j)
            p (* a b)]
        (when-let [label (get structural-numbers p)]
          (swap! hits conj {:a a :b b :product p :label label}))))
    (doseq [h (sort-by :product @hits)]
      (println (format "    %d × %d = %d = %s"
                       (:a h) (:b h) (:product h) (:label h))))
    (when (empty? @hits)
      (println "    (none)"))
    (println))

  ;; Three-factor products
  (println "  ── Three-factor products ──")
  (let [hits (atom [])]
    (doseq [i (range (count ezekiel-numbers))
            j (range i (count ezekiel-numbers))
            k (range j (count ezekiel-numbers))]
      (let [a (nth ezekiel-numbers i)
            b (nth ezekiel-numbers j)
            c (nth ezekiel-numbers k)
            p (* a b c)]
        (when-let [label (get structural-numbers p)]
          (swap! hits conj {:factors [a b c] :product p :label label}))))
    (doseq [h (sort-by :product @hits)]
      (println (format "    %s = %d = %s"
                       (str/join " × " (:factors h))
                       (:product h) (:label h))))
    (when (empty? @hits)
      (println "    (none)"))
    (println))

  ;; Four-factor products
  (println "  ── Four-factor products ──")
  (let [hits (atom [])
        n (count ezekiel-numbers)]
    (doseq [i (range n)
            j (range i n)
            k (range j n)
            l (range k n)]
      (let [a (nth ezekiel-numbers i)
            b (nth ezekiel-numbers j)
            c (nth ezekiel-numbers k)
            d (nth ezekiel-numbers l)
            p (* a b c d)]
        (when-let [label (get structural-numbers p)]
          (swap! hits conj {:factors [a b c d] :product p :label label}))))
    (doseq [h (sort-by :product (take 20 @hits))]
      (println (format "    %s = %d = %s"
                       (str/join " × " (:factors h))
                       (:product h) (:label h))))
    (when (> (count @hits) 20)
      (println (format "    ... (%d total)" (count @hits))))
    (when (empty? @hits)
      (println "    (none)"))
    (println)))

;; ── Part 3: Divisibility of structural numbers ──────────────

(defn part3-divisibility []
  (println "\n═══ PART 3: DIVISIBILITY ═══\n")
  (println "  Which Ezekiel numbers divide structural numbers?\n")

  (doseq [[n label] (sort-by key > structural-numbers)]
    (let [divisors (filter #(and (pos? %) (zero? (mod n %))) ezekiel-numbers)]
      (when (seq divisors)
        (println (format "  %d (%s) is divisible by:"
                         (int n) label))
        (doseq [d divisors]
          (println (format "    ÷ %d = %d" (int d) (int (/ n d)))))
        (println)))))

;; ── Part 4: Ezekiel as a lens ───────────────────────────────

(defn valid-4d-factorizations
  "Find all ways to write n as a product of exactly 4 factors ≥ 2."
  [n]
  (let [results (atom [])]
    (doseq [a (range 2 (inc (int (Math/sqrt n))))
            :when (zero? (mod n a))]
      (let [rest1 (/ n a)]
        (doseq [b (range a (inc (int (Math/sqrt rest1))))
                :when (zero? (mod rest1 b))]
          (let [rest2 (/ rest1 b)]
            (doseq [c (range b (inc (int (Math/sqrt rest2))))
                    :when (zero? (mod rest2 c))]
              (let [d (/ rest2 c)]
                (when (>= d c)
                  (swap! results conj [a b c d]))))))))
    @results))

(defn part4-ezekiel-lens []
  (println "\n═══ PART 4: EZEKIEL NUMBERS AS LENS ═══\n")
  (println "  Can Ezekiel measurements form a 4D factorization of 304,850?\n")

  ;; Check which Ezekiel numbers divide 304,850
  (let [divisors (filter #(and (pos? %) (zero? (mod 304850 %))) ezekiel-numbers)]
    (println (format "  Ezekiel numbers that divide 304,850: %s\n"
                     (str/join ", " divisors)))

    ;; Find 4D factorizations using only Ezekiel numbers
    (let [all-4d (valid-4d-factorizations 304850)
          ez-set (set ezekiel-numbers)
          ez-4d  (filter (fn [factors] (every? ez-set factors)) all-4d)]
      (println (format "  All 4D factorizations of 304,850: %d" (count all-4d)))
      (println (format "  Using only Ezekiel numbers: %d" (count ez-4d)))
      (when (seq ez-4d)
        (println "\n  Ezekiel-only factorizations:")
        (doseq [f ez-4d]
          (println (format "    %s" (str/join " × " f)))))

      ;; Check factorizations where at least 2 factors are Ezekiel numbers
      (let [ez-partial (filter (fn [factors]
                                 (>= (count (filter ez-set factors)) 2))
                               all-4d)]
        (println (format "\n  With ≥2 Ezekiel factors: %d" (count ez-partial)))
        (when (seq ez-partial)
          (doseq [f (take 20 ez-partial)]
            (let [marks (map (fn [x] (if (ez-set x) (str x "*") (str x))) f)]
              (println (format "    %s = 304,850" (str/join " × " marks))))))))))

;; ── Part 5: Key products ────────────────────────────────────

(defn part5-key-products []
  (println "\n═══ PART 5: KEY PRODUCTS ═══\n")
  (println "  Testing specific dimension combinations.\n")

  ;; The known architectural products
  (let [combos [;; Outer precinct
                {:label "precinct 500 × 500"   :values [500 500]}
                {:label "inner court 100 × 100" :values [100 100]}
                {:label "nave 40 × 20"          :values [40 20]}
                {:label "most holy 20 × 20"     :values [20 20]}
                {:label "altar 12 × 12"         :values [12 12]}
                {:label "gate 50 × 25"          :values [50 25]}
                {:label "building 100 × 50"     :values [100 50]}
                ;; 3D combinations
                {:label "altar 12 × 12 × 4"     :values [12 12 4]}
                {:label "nave 40 × 20 × ?"      :values [40 20]}
                {:label "holy 20 × 20 × 20"     :values [20 20 20]}
                ;; Sacred district
                {:label "district 25000 × 10000" :values [25000 10000]}
                {:label "perimeter 18000"        :values [18000]}
                {:label "city 4500 × 4500"       :values [4500 4500]}
                ;; Reed measurement
                {:label "reed = 6 cubits"        :values [6]}
                ;; Step counts
                {:label "outer steps 7, inner steps 8" :values [7 8]}
                ;; Interesting pairs
                {:label "50 × 25 × 7"            :values [50 25 7]}
                {:label "100 × 50 × 7"           :values [100 50 7]}
                {:label "500 × 100 × 50"         :values [500 100 50]}
                {:label "25 × 13"                :values [25 13]}]]

    (doseq [{:keys [label values]} combos]
      (let [product (reduce * values)]
        (println (format "  %-30s = %,d" label product))
        ;; Check divisibility of structural numbers
        (doseq [[n nlabel] structural-numbers
                :when (zero? (mod n product))]
          (println (format "    → %,d ÷ %,d = %,d (%s)"
                           n product (/ n product) nlabel)))
        ;; Check if product divides 304,850
        (when (zero? (mod 304850 product))
          (println (format "    → 304,850 / %,d = %,d" product (/ 304850 product))))
        ;; Factorize the product
        (when (<= product 1000000)
          (let [factors (loop [n product, fs [], d 2]
                          (cond
                            (= n 1) fs
                            (zero? (mod n d)) (recur (/ n d) (conj fs d) d)
                            :else (recur n fs (inc d))))]
            (when (> (count factors) 1)
              (println (format "    = %s" (str/join " × " factors))))))))))

;; ── Part 6: The 25,000 investigation ────────────────────────

(defn part6-large-numbers []
  (println "\n═══ PART 6: THE LARGE NUMBERS ═══\n")
  (println "  Ezekiel's sacred district uses 25,000 and 10,000 — much larger")
  (println "  than the temple itself. What are these numbers?\n")

  (let [numbers {25000 "sacred district length/width"
                 10000 "priest/Levite portion width"
                 5000  "common land width"
                 4500  "city side"
                 18000 "city perimeter"
                 500   "temple precinct side"
                 250   "city pasture"
                 1000  "river measurement"}]
    (doseq [[n label] (sort-by key > numbers)]
      (println (format "  %,6d = %-30s" n label))
      ;; Prime factorization
      (let [factors (loop [n n, fs [], d 2]
                      (cond
                        (= n 1) fs
                        (zero? (mod n d)) (recur (/ n d) (conj fs d) d)
                        :else (recur n fs (inc d))))]
        (println (format "         = %s" (str/join " × " factors))))
      ;; Relationship to 304,850
      (when (zero? (mod 304850 n))
        (println (format "         304,850 ÷ %,d = %,d" n (/ 304850 n))))
      (when (zero? (mod n 7))
        (println (format "         ÷7 = %,d" (/ n 7))))
      (when (zero? (mod n 13))
        (println (format "         ÷13 = %,d" (/ n 13))))
      (when (zero? (mod n 67))
        (println (format "         ÷67 = %,d" (/ n 67))))
      (when (zero? (mod n 50))
        (println (format "         ÷50 = %,d" (/ n 50))))
      (println)))

  ;; Key ratio
  (println "  Key ratios:")
  (println (format "    25000 / 10000 = %s" (/ 25000 10000.0)))
  (println (format "    25000 / 5000  = %d" (/ 25000 5000)))
  (println (format "    25000 / 500   = %d" (/ 25000 500)))
  (println (format "    304850 / 25000 = %.2f" (/ 304850.0 25000)))
  (println (format "    304850 / 10000 = %.2f" (/ 304850.0 10000)))
  (println (format "    25000 × 12 = %,d (12 tribes, 12 gates)" (* 25000 12)))
  (println (format "    25000 = 25 × 1000 = 5² × 10³" ))
  (println (format "    10000 = 10⁴"))
  (println))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "=== 074: EZEKIEL'S TEMPLE DIMENSIONS ===")
  (println "  Nine chapters of measurements for a temple never built.")
  (println "  Testing whether the dimensions encode coordinate systems.\n")

  (println (format "  %d measurements compiled from Ezekiel 40-48." (count ezekiel-measurements)))
  (println (format "  %d unique integer values: %s\n"
                   (count ezekiel-numbers)
                   (str/join ", " ezekiel-numbers)))

  (coords/space) ;; ensure space is loaded

  (part1-direct-matches)
  (part2-products)
  (part3-divisibility)
  (part4-ezekiel-lens)
  (part5-key-products)
  (part6-large-numbers)

  (println "\n═══ SUMMARY ═══\n")
  (println "  The question: does Ezekiel's temple describe a with-dims lens?")
  (println "  Or do its measurements relate to 304,850 = 7 × 50 × 13 × 67?")
  (println "\nDone."))
