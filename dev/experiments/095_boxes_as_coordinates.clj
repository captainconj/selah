(ns experiments.095-boxes-as-coordinates
  "Experiment 095 — Boxes as Coordinates.

   The guard word שמר (GV=540 = 4 × פנה(corner,135) = 12 × 45(Ark volume)).
   תבה means both 'ark/box' AND 'word' — the box is a word.

   Question: Are the biblical boxes embeddings in the 7×50×13×67 Torah space?

   Five phases:
     1. Dimensions as Coordinates — map box measurements to 4D addresses
     2. Boxes as Sub-Regions — center each box, extract letter statistics
     3. Nesting & Containment — verify Ark ⊂ Holy of Holies ⊂ Tabernacle
     4. Self-Reference Test — permutation null model for construction verses
     5. Cross-Numerics — dimensions as letters, recursive boxes, Psalm 118:22"
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.set :as set]))

;; ── Helpers ─────────────────────────────────────────────────

(defn- save-edn
  "Save data to an EDN file."
  [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(def data-dir "data/experiments/095/")

(defn- num->hebrew-letter
  "Map a number to the Hebrew letter with that gematria value.
   Only works for standard values: 1-9, 10-90(by 10), 100-400(by 100)."
  [n]
  (get {1 \א 2 \ב 3 \ג 4 \ד 5 \ה 6 \ו 7 \ז 8 \ח 9 \ט
        10 \י 20 \כ 30 \ל 40 \מ 50 \נ 60 \ס 70 \ע 80 \פ 90 \צ
        100 \ק 200 \ר 300 \ש 400 \ת}
       n))

(defn- describe-pos
  "Describe a position with verse context. Returns map."
  [pos]
  (let [s (c/space)
        coord (vec (c/idx->coord pos))
        v (c/verse-at s pos)]
    {:position pos
     :coord    coord
     :letter   (str (c/letter-at s pos))
     :gematria (c/gv-at s pos)
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :book     (:book v)
     :chapter  (:ch v)
     :vs       (:vs v)}))

;; ── The Boxes ───────────────────────────────────────────────

(def boxes
  "Biblical boxes with dimensions in half-cubits where applicable."
  [{:name "Ark of the Covenant"
    :dims [5 3 3]
    :cubits "2.5 × 1.5 × 1.5"
    :source "Exodus 25:10"
    :note "half-cubits"}
   {:name "Mercy Seat"
    :dims [5 3]
    :cubits "2.5 × 1.5"
    :source "Exodus 25:17"
    :note "half-cubits, 2D surface"}
   {:name "Table of Showbread"
    :dims [4 2 3]
    :cubits "2 × 1 × 1.5"
    :source "Exodus 25:23"
    :note "half-cubits"}
   {:name "Altar of Incense"
    :dims [2 2 4]
    :cubits "1 × 1 × 2"
    :source "Exodus 30:1-2"
    :note "half-cubits"}
   {:name "Altar of Burnt Offering"
    :dims [10 10 6]
    :cubits "5 × 5 × 3"
    :source "Exodus 27:1"
    :note "half-cubits"}
   {:name "Holy of Holies"
    :dims [10 10 10]
    :cubits "10 × 10 × 10"
    :source "1 Kings 6:20"
    :note "cubits"}
   {:name "Tabernacle"
    :dims [30 10 10]
    :cubits "30 × 10 × 10"
    :source "Exodus 26"
    :note "cubits"}
   {:name "Noah's Ark"
    :dims [300 50 30]
    :cubits "300 × 50 × 30"
    :source "Genesis 6:15"
    :note "cubits"}])

;; Construction/tabernacle keywords for self-reference detection
(def construction-terms
  "Hebrew roots related to building, boxes, dimensions, guarding."
  #{"עשה" "בנה" "שמר" "ארן" "ארון" "תבה" "שלחן" "מזבח" "כפרת"
    "קדש" "משכן" "אהל" "אמה" "טפח" "זרת" "עמד" "קרש" "בריח"
    "עמוד" "יריעה" "פרכת" "כרב" "כרוב" "שלם" "מלאכה" "צוה"})

(def construction-books
  "Books/chapters where construction instructions appear."
  #{["Exodus" 25] ["Exodus" 26] ["Exodus" 27] ["Exodus" 28]
    ["Exodus" 29] ["Exodus" 30] ["Exodus" 35] ["Exodus" 36]
    ["Exodus" 37] ["Exodus" 38] ["Exodus" 39] ["Exodus" 40]
    ["Numbers" 3] ["Numbers" 4] ["Numbers" 7] ["Numbers" 18]})

(defn- construction-verse?
  "Is this verse in a construction/tabernacle chapter?"
  [{:keys [book chapter]}]
  (contains? construction-books [book chapter]))

;; ══════════════════════════════════════════════════════════════
;; Phase 1: Dimensions as Coordinates
;; ══════════════════════════════════════════════════════════════

(defn- fits-axis?
  "Does value v fit in the 4D axis ranges? a≤6, b≤49, c≤12, d≤66."
  [axis v]
  (case axis
    :a (<= 0 v 6)
    :b (<= 0 v 49)
    :c (<= 0 v 12)
    :d (<= 0 v 66)))

(defn- all-coord-assignments
  "Generate all distinct ways to assign dims (a vec of 3 numbers)
   to 4 coordinate slots (a,b,c,d). The unassigned slot gets fill-val.
   Returns seq of [a b c d] vectors."
  [dims fill-val]
  (let [axes [:a :b :c :d]
        ;; All distinct permutations of dims
        dim-perms (distinct (for [i (range 3)
                                  j (range 3)
                                  k (range 3)
                                  :when (and (not= i j) (not= j k) (not= i k))]
                              [(nth dims i) (nth dims j) (nth dims k)]))]
    (for [free-axis (range 4)     ;; which axis is free
          perm dim-perms
          :let [assigned (range 4)
                coord (vec (for [slot (range 4)]
                             (if (= slot free-axis)
                               fill-val
                               (nth perm (count (filter #(< % slot)
                                                        (remove #{free-axis}
                                                                (range slot))))))))]
          ;; Simpler: build the coord directly
          :let [filled (loop [s 0 p 0 result []]
                         (if (= s 4)
                           result
                           (if (= s free-axis)
                             (recur (inc s) p (conj result fill-val))
                             (recur (inc s) (inc p) (conj result (nth perm p))))))]
          ;; Check all values fit their axis
          :when (every? true? (map-indexed
                               (fn [i v] (fits-axis? (nth axes i) v))
                               filled))]
      {:coord filled
       :free-axis (nth axes free-axis)
       :perm perm})))

(defn phase-1-dimensions-as-coordinates
  "Map each box's physical dimensions to 4D coordinates.
   For 3D boxes: assign 3 dims to 3 axes, try fill=0, center, and max for free axis.
   For 2D boxes: assign 2 dims to 2 axes, try fill combos."
  []
  (println "\n══ Phase 1: Dimensions as Coordinates ══")
  (let [center [3 25 6 33]
        axis-maxes {:a 6 :b 49 :c 12 :d 66}
        axes [:a :b :c :d]
        results
        (vec
         (for [{:keys [name dims source] :as box} boxes
               :let [ndims (count dims)]
               :when (<= ndims 3)]
           (let [;; For 3D boxes: direct assignment
                 assignments
                 (if (= ndims 3)
                   ;; 3D: assign to 3 of 4 axes, free axis gets fill values
                   (for [free-idx (range 4)
                         perm (distinct
                               (for [i (range 3) j (range 3) k (range 3)
                                     :when (and (not= i j) (not= j k) (not= i k))]
                                 [(nth dims i) (nth dims j) (nth dims k)]))
                         fill-type [:zero :center :max]
                         :let [free-axis (nth axes free-idx)
                               fill-val (case fill-type
                                          :zero 0
                                          :center (nth center free-idx)
                                          :max (get axis-maxes free-axis))
                               coord (loop [s 0 p 0 result []]
                                       (if (= s 4)
                                         result
                                         (if (= s free-idx)
                                           (recur (inc s) p (conj result fill-val))
                                           (recur (inc s) (inc p) (conj result (nth perm p))))))
                               valid? (every? true?
                                              (map-indexed
                                               (fn [i v] (fits-axis? (nth axes i) v))
                                               coord))]
                         :when valid?]
                     {:coord coord :free-axis free-axis :fill fill-type})
                   ;; 2D: assign to 2 of 4 axes, 2 free axes get fill values
                   (for [ax1 (range 4)
                         ax2 (range (inc ax1) 4)
                         :let [free-axes (remove #{ax1 ax2} (range 4))]
                         perm (distinct [dims (vec (reverse dims))])
                         fill-type [:zero :center]
                         :let [coord (vec (for [s (range 4)]
                                            (cond
                                              (= s ax1) (first perm)
                                              (= s ax2) (second perm)
                                              :else (if (= fill-type :zero)
                                                      0
                                                      (nth center s)))))
                               valid? (every? true?
                                              (map-indexed
                                               (fn [i v] (fits-axis? (nth axes i) v))
                                               coord))]
                         :when valid?]
                     {:coord coord :free-axes (mapv #(nth axes %) free-axes)
                      :fill fill-type}))
                 ;; Deduplicate by coord
                 unique (vals (group-by :coord assignments))
                 coords (mapv first unique)
                 ;; Look up each coordinate
                 lookups
                 (vec (for [{:keys [coord] :as assignment} coords
                            :let [[a b c d] coord
                                  pos (c/coord->idx a b c d)
                                  desc (describe-pos pos)
                                  constr? (construction-verse? desc)]]
                        (merge assignment desc
                               {:construction? constr?})))]
             (println (str "\n" name " (" source ", dims=" (pr-str dims) "):"))
             (println (str "  " (count lookups) " valid coordinate assignments"))
             (let [constr-hits (filter :construction? lookups)]
               (when (seq constr-hits)
                 (println (str "  ** " (count constr-hits) " hit construction chapters **"))
                 (doseq [h constr-hits]
                   (println (str "    " (:coord h) " → " (:verse h)
                                 " (letter=" (:letter h) ")")))))
             (doseq [l (take 10 lookups)]
               (println (str "  " (:coord l) " → " (:verse l)
                             " [" (:letter l) ", gv=" (:gematria l) "]"
                             (when (:construction? l) " ★"))))
             (when (> (count lookups) 10)
               (println (str "  ... and " (- (count lookups) 10) " more")))
             {:box name :source source :dims dims
              :total-assignments (count lookups)
              :construction-hits (count (filter :construction? lookups))
              :lookups lookups})))]
    (println (str "\n── Summary ──"))
    (doseq [r results]
      (println (str "  " (:box r) ": " (:total-assignments r) " coords, "
                    (:construction-hits r) " construction hits")))
    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 2: Boxes as Sub-Regions
;; ══════════════════════════════════════════════════════════════

(defn- clamp-range
  "Center a range of size `span` at `center` within [0, max-val].
   Returns [lo hi] inclusive."
  [center span max-val]
  (let [half (quot span 2)
        lo (max 0 (- center half))
        hi (min max-val (+ lo (dec span)))]
    ;; adjust lo if hi got clamped
    [(max 0 (- hi (dec span))) hi]))

(defn- region-positions
  "Generate all positions within a box region.
   spec is {:a [lo hi] :b [lo hi] :c [lo hi] :d [lo hi]}
   where each is an inclusive range."
  [spec]
  (let [[a-lo a-hi] (:a spec)
        [b-lo b-hi] (:b spec)
        [c-lo c-hi] (:c spec)
        [d-lo d-hi] (:d spec)]
    (for [a (range a-lo (inc a-hi))
          b (range b-lo (inc b-hi))
          c (range c-lo (inc c-hi))
          d (range d-lo (inc d-hi))]
      (c/coord->idx a b c d))))

(defn- region-stats
  "Compute statistics for a set of positions."
  [positions]
  (let [s (c/space)
        n (count positions)
        letters (mapv #(c/letter-at s %) positions)
        gvs (mapv #(c/gv-at s %) positions)
        freqs (frequencies letters)
        total-gv (reduce + gvs)
        verses (distinct (map #(c/verse-at s %) positions))
        verse-strs (mapv #(str (:book %) " " (:ch %) ":" (:vs %)) verses)]
    {:letter-count n
     :total-gv total-gv
     :mean-gv (when (pos? n) (double (/ total-gv n)))
     :letter-freqs (into (sorted-map) freqs)
     :verse-count (count verses)
     :verses (vec (take 20 verse-strs))
     :construction-verses (count (filter construction-verse?
                                         (map (fn [v] {:book (:book v)
                                                       :chapter (:ch v)})
                                              verses)))}))

(def box-regions
  "Map each box to a sub-region centered at (3,25,6,33)."
  (let [center [3 25 6 33]]
    [{:name "Ark of the Covenant"
      :spec {:a (clamp-range 3 5 6)    ;; 5 along a (1..5)
             :b [0 49]                  ;; free axis
             :c (clamp-range 6 3 12)    ;; 3 along c (5..7)
             :d (clamp-range 33 3 66)}  ;; 3 along d (32..34)
      :expected-size "5×50×3×3 = 2,250"}
     {:name "Holy of Holies"
      :spec {:a [0 6]                   ;; free
             :b (clamp-range 25 10 49)  ;; 10 along b (21..30)
             :c [0 12]                  ;; free
             :d (clamp-range 33 10 66)} ;; 10 along d (29..38)
      :expected-size "7×10×13×10 = 9,100"}
     {:name "Tabernacle"
      :spec {:a [0 6]                   ;; free
             :b (clamp-range 25 30 49)  ;; 30 along b (11..40)
             :c [0 12]                  ;; free
             :d (clamp-range 33 10 66)} ;; 10 along d (29..38)
      :expected-size "7×30×13×10 = 27,300"}
     {:name "Table of Showbread"
      :spec {:a (clamp-range 3 4 6)    ;; 4 along a (1..4)
             :b (clamp-range 25 2 49)  ;; 2 along b (25..26)
             :c (clamp-range 6 3 12)   ;; 3 along c (5..7)
             :d [0 66]}                ;; free axis
      :expected-size "4×2×3×67 = 1,608"}
     {:name "Altar of Incense"
      :spec {:a (clamp-range 3 2 6)    ;; 2 along a (3..4)
             :b (clamp-range 25 2 49)  ;; 2 along b (25..26)
             :c [0 12]                 ;; free
             :d (clamp-range 33 4 66)} ;; 4 along d (32..35)
      :expected-size "2×2×13×4 = 208"}]))

(defn phase-2-sub-regions
  "Center each box at (3,25,6,33), extract letter statistics."
  []
  (println "\n══ Phase 2: Boxes as Sub-Regions ══")
  (vec
   (for [{:keys [name spec expected-size]} box-regions]
     (let [positions (vec (region-positions spec))
           stats (region-stats positions)]
       (println (str "\n" name " (expected " expected-size "):"))
       (println (str "  Actual size: " (:letter-count stats) " letters"))
       (println (str "  Total GV: " (:total-gv stats)
                     ", Mean GV: " (format "%.1f" (:mean-gv stats))))
       (println (str "  Verses spanned: " (:verse-count stats)
                     " (" (:construction-verses stats) " construction)"))
       (println (str "  Top letters: "
                     (str/join ", " (take 5 (sort-by (comp - val)
                                                     (:letter-freqs stats))))))
       (println (str "  First verses: "
                     (str/join ", " (take 5 (:verses stats)))))
       (assoc stats :name name :spec spec)))))

;; ══════════════════════════════════════════════════════════════
;; Phase 3: Nesting & Containment
;; ══════════════════════════════════════════════════════════════

(defn- range-contains?
  "Does range [lo1 hi1] fully contain [lo2 hi2]?"
  [[lo1 hi1] [lo2 hi2]]
  (and (<= lo1 lo2) (>= hi1 hi2)))

(defn- region-contains?
  "Does outer region fully contain inner region?"
  [outer inner]
  (every? (fn [axis]
            (range-contains? (get outer axis) (get inner axis)))
          [:a :b :c :d]))

(defn- position-in-region?
  "Is a position inside a region spec?"
  [pos spec]
  (let [coord (vec (c/idx->coord pos))]
    (and (<= (first (:a spec)) (nth coord 0) (second (:a spec)))
         (<= (first (:b spec)) (nth coord 1) (second (:b spec)))
         (<= (first (:c spec)) (nth coord 2) (second (:c spec)))
         (<= (first (:d spec)) (nth coord 3) (second (:d spec))))))

(defn phase-3-nesting
  "Verify containment hierarchy and test key verses."
  []
  (println "\n══ Phase 3: Nesting & Containment ══")
  (let [regions (into {} (map (juxt :name :spec) box-regions))
        ark-spec (get regions "Ark of the Covenant")
        hoh-spec (get regions "Holy of Holies")
        tab-spec (get regions "Tabernacle")

        ;; Containment tests
        ark-in-hoh (region-contains? hoh-spec ark-spec)
        hoh-in-tab (region-contains? tab-spec hoh-spec)
        ark-in-tab (region-contains? tab-spec ark-spec)

        ;; Key verse positions
        center-pos (c/coord->idx 3 25 6 33)  ;; Lev 8:35
        ;; Find Lev 8:8 (breastplate installation) via verse index
        s (c/space)
        lev88-first (some (fn [{:keys [book ch vs start]}]
                            (when (and (= book "Leviticus") (= ch 8) (= vs 8))
                              start))
                          (:verse-ref s))

        center-in-ark (position-in-region? center-pos ark-spec)
        center-in-hoh (position-in-region? center-pos hoh-spec)
        center-in-tab (position-in-region? center-pos tab-spec)

        lev88-in-ark (when lev88-first (position-in-region? lev88-first ark-spec))
        lev88-in-hoh (when lev88-first (position-in-region? lev88-first hoh-spec))
        lev88-in-tab (when lev88-first (position-in-region? lev88-first tab-spec))

        ;; Set difference: in Ark but not in HoH
        ark-positions (set (region-positions ark-spec))
        hoh-positions (set (region-positions hoh-spec))
        ark-only (set/difference ark-positions hoh-positions)
        both (set/intersection ark-positions hoh-positions)]

    (println "\n── Containment Hierarchy ──")
    (println (str "  Ark ⊂ Holy of Holies? " ark-in-hoh))
    (println (str "  Holy of Holies ⊂ Tabernacle? " hoh-in-tab))
    (println (str "  Ark ⊂ Tabernacle? " ark-in-tab))

    (println "\n── Key Verses ──")
    (println (str "  Center (3,25,6,33) = Lev 8:35 — guard the charge"))
    (println (str "    In Ark? " center-in-ark))
    (println (str "    In Holy of Holies? " center-in-hoh))
    (println (str "    In Tabernacle? " center-in-tab))
    (when lev88-first
      (let [coord (vec (c/idx->coord lev88-first))]
        (println (str "  Lev 8:8 (breastplate) at position " lev88-first
                       " = " coord))
        (println (str "    In Ark? " lev88-in-ark))
        (println (str "    In Holy of Holies? " lev88-in-hoh))
        (println (str "    In Tabernacle? " lev88-in-tab))))

    (println "\n── Ark-Only Letters ──")
    (println (str "  Ark positions: " (count ark-positions)))
    (println (str "  Holy of Holies positions: " (count hoh-positions)))
    (println (str "  Intersection: " (count both)))
    (println (str "  Ark only (not in HoH): " (count ark-only)))
    (when (pos? (count ark-only))
      (let [ark-only-stats (region-stats (vec (take 500 ark-only)))]
        (println (str "  Ark-only letters sample: "
                       (str/join "" (take 20 (map #(c/letter-at (c/space) %)
                                                   (sort (take 20 ark-only)))))))))

    {:containment {:ark-in-hoh ark-in-hoh
                   :hoh-in-tab hoh-in-tab
                   :ark-in-tab ark-in-tab}
     :center {:in-ark center-in-ark :in-hoh center-in-hoh :in-tab center-in-tab}
     :lev88 {:position lev88-first
             :coord (when lev88-first (vec (c/idx->coord lev88-first)))
             :in-ark lev88-in-ark :in-hoh lev88-in-hoh :in-tab lev88-in-tab}
     :set-sizes {:ark (count ark-positions)
                 :hoh (count hoh-positions)
                 :intersection (count both)
                 :ark-only (count ark-only)}}))

;; ══════════════════════════════════════════════════════════════
;; Phase 4: Self-Reference Test
;; ══════════════════════════════════════════════════════════════

(defn- random-3tuple
  "Generate a random 3-tuple within axis ranges."
  []
  [(rand-int 7) (rand-int 50) (rand-int 13)])

(defn phase-4-self-reference
  "Count construction-chapter hits for box coordinates vs random coordinates."
  [phase1-results]
  (println "\n══ Phase 4: Self-Reference Test ══")
  (let [;; Collect all real box coordinates
        real-coords (vec (for [box phase1-results
                               lookup (:lookups box)]
                           (:coord lookup)))
        real-n (count real-coords)
        real-hits (count (filter (fn [lookup]
                                  (construction-verse? lookup))
                                (for [box phase1-results
                                      lookup (:lookups box)]
                                  lookup)))
        real-rate (when (pos? real-n) (double (/ real-hits real-n)))

        ;; Null model: 200 trials, each with same number of random coordinates
        n-trials 200
        null-rates
        (vec
         (for [_ (range n-trials)]
           (let [random-coords (repeatedly real-n
                                           (fn []
                                             (let [[x y z] (random-3tuple)]
                                               [x y z (rand-int 67)])))
                 hits (count
                       (filter (fn [[a b c d]]
                                 (let [pos (c/coord->idx a b c d)
                                       desc (describe-pos pos)]
                                   (construction-verse? desc)))
                               random-coords))]
             (double (/ hits real-n)))))
        null-mean (/ (reduce + null-rates) (count null-rates))
        null-sorted (sort null-rates)
        null-median (nth null-sorted (quot n-trials 2))
        real-percentile (/ (count (filter #(<= % real-rate) null-rates))
                           (double n-trials))
        ;; Standard deviation
        null-var (/ (reduce + (map #(Math/pow (- % null-mean) 2) null-rates))
                    (count null-rates))
        null-sd (Math/sqrt null-var)
        z-score (when (pos? null-sd) (/ (- real-rate null-mean) null-sd))]

    (println (str "  Real coordinates: " real-n))
    (println (str "  Real construction hits: " real-hits
                  " (rate=" (format "%.3f" real-rate) ")"))
    (println (str "  Null model (" n-trials " trials):"))
    (println (str "    Mean rate: " (format "%.3f" null-mean)))
    (println (str "    Median rate: " (format "%.3f" null-median)))
    (println (str "    SD: " (format "%.4f" null-sd)))
    (println (str "  Real percentile: " (format "%.1f" (* 100 real-percentile)) "%"))
    (when z-score
      (println (str "  Z-score: " (format "%.2f" z-score))))
    (println (str "  Conclusion: "
                  (cond
                    (> real-percentile 0.95) "ELEVATED — real boxes hit construction chapters more than chance"
                    (< real-percentile 0.05) "DEPRESSED — real boxes avoid construction chapters"
                    :else "NORMAL — real box rate is within null distribution")))

    {:real-n real-n
     :real-hits real-hits
     :real-rate real-rate
     :null-mean null-mean
     :null-median null-median
     :null-sd null-sd
     :z-score z-score
     :percentile real-percentile}))

;; ══════════════════════════════════════════════════════════════
;; Phase 5: Cross-Numerics
;; ══════════════════════════════════════════════════════════════

(defn- dims-as-letters
  "Convert box dimensions to Hebrew letters via gematria value mapping."
  [dims]
  (let [letters (keep num->hebrew-letter dims)]
    (when (= (count letters) (count dims))
      {:letters (vec letters)
       :word (apply str letters)
       :gv (reduce + (map g/letter-value letters))})))

(defn- dims-as-letters-all-orders
  "All distinct orderings of dims as Hebrew words."
  [dims]
  (let [perms (distinct (for [i (range (count dims))
                              j (range (count dims))
                              k (range (count dims))
                              :when (and (not= i j) (not= j k) (not= i k)
                                         (= (count dims) 3))]
                          [(nth dims i) (nth dims j) (nth dims k)]))
        perms (if (= (count dims) 2)
                (distinct [dims (vec (reverse dims))])
                perms)]
    (keep (fn [p]
            (when-let [result (dims-as-letters p)]
              (let [word (:word result)
                    meaning (dict/translate word)]
                (assoc result :permutation p
                              :meaning meaning))))
          perms)))

(defn phase-5-cross-numerics
  "Box dimensions as letters, cornerstone tracing, recursive boxes."
  [phase1-results]
  (println "\n══ Phase 5: Cross-Numerics ══")

  ;; Part A: Dimensions as letters
  (println "\n── A. Box Dimensions as Letters ──")
  (let [dim-letters
        (vec
         (for [{:keys [name dims]} boxes
               :let [all-orders (dims-as-letters-all-orders dims)]
               :when (seq all-orders)]
           (do
             (println (str "\n  " name " " (pr-str dims) ":"))
             (doseq [{:keys [permutation word meaning gv]} all-orders]
               (println (str "    " (pr-str permutation) " → " word
                             " (GV=" gv ")"
                             (when meaning (str " = " meaning)))))
             {:box name :dims dims :letter-forms all-orders})))]

    ;; Part B: שמר = 4 × פנה
    (println "\n── B. שמר = 4 × פנה (Guard = 4 Corners) ──")
    (let [shmr-gv (g/word-value "שמר")
          pnh-gv (g/word-value "פנה")
          cornerstone-gv (+ (g/word-value "אבן") (g/word-value "פנה"))]
      (println (str "  שמר GV=" shmr-gv " = 4 × " pnh-gv " (פנה/corner)"))
      (println (str "  אבן פנה (cornerstone) GV=" cornerstone-gv))
      ;; Try cornerstone GV as a position
      (when (< cornerstone-gv c/total-letters)
        (let [desc (describe-pos cornerstone-gv)]
          (println (str "  Position " cornerstone-gv " → " (:verse desc)
                        " [" (:letter desc) "]")))))

    ;; Part C: Psalm 118:22 coordinates
    ;; We can't find Psalms in the Torah, but we can check the gematria
    (println "\n── C. Stone the Builders Rejected ──")
    (let [;; אבן (stone) GV=53, פנה (corner) GV=135
          stone-gv (g/word-value "אבן")
          corner-gv (g/word-value "פנה")]
      (println (str "  אבן (stone) = " stone-gv " = גן (garden)"))
      (println (str "  פנה (corner) = " corner-gv))
      (println (str "  אבן+פנה (cornerstone) = " (+ stone-gv corner-gv)))
      ;; Look up stone GV as a coordinate
      ;; 53 as (a,b,c,d): try idx->coord
      (let [desc53 (describe-pos stone-gv)]
        (println (str "  Position 53 → " (:verse desc53)
                      " " (:coord desc53) " [" (:letter desc53) "]"))))

    ;; Part D: Recursive boxes — Table found at Ark coordinate
    (println "\n── D. Recursive Boxes ──")
    (println "  Ark (5,3,3) as coords → several assignments")
    ;; Table dimensions (4,2,3) as coordinates
    (let [table-lookups (for [fill [0 33 66]
                              perm [[4 2 3] [4 3 2] [2 4 3] [2 3 4] [3 4 2] [3 2 4]]
                              free-idx (range 4)
                              :let [axes [:a :b :c :d]
                                    coord (loop [s 0 p 0 result []]
                                            (if (= s 4)
                                              result
                                              (if (= s free-idx)
                                                (recur (inc s) p (conj result fill))
                                                (recur (inc s) (inc p) (conj result (nth perm p))))))
                                    valid? (every? true?
                                                   (map-indexed
                                                    (fn [i v] (fits-axis? (nth axes i) v))
                                                    coord))]
                              :when valid?
                              :let [[a b c d] coord
                                    pos (c/coord->idx a b c d)
                                    desc (describe-pos pos)]]
                          (assoc desc :coord coord))
          table-constr (filter construction-verse? table-lookups)]
      (println (str "  Table (4,2,3) → " (count (distinct (map :coord table-lookups)))
                    " unique coordinates"))
      (println (str "  Construction hits: " (count table-constr)))
      (doseq [h (take 5 table-constr)]
        (println (str "    " (:coord h) " → " (:verse h)
                      " [" (:letter h) "] ★"))))

    ;; Part E: Noah's Ark dimensions as letters
    (println "\n── E. Noah's Ark (300,50,30) as Letters ──")
    (let [noah-dims [300 50 30]
          all-orders (dims-as-letters-all-orders noah-dims)]
      (doseq [{:keys [permutation word meaning gv]} all-orders]
        (println (str "  " (pr-str permutation) " → " word
                      " (GV=" gv ")"
                      (when meaning (str " = " meaning)))))
      ;; Check specific combination: ש,נ,ל → לשן (language)
      (println (str "  שנל → " (dict/translate "שנל")))
      (println (str "  נשל → " (dict/translate "נשל")))
      (println (str "  לשן → " (dict/translate "לשן")))
      (println (str "  לנש → " (dict/translate "לנש")))
      (println (str "  שלן → " (dict/translate "שלן")))
      (println (str "  נלש → " (dict/translate "נלש"))))

    {:dim-letters dim-letters}))

;; ══════════════════════════════════════════════════════════════
;; Run All
;; ══════════════════════════════════════════════════════════════

(defn run-all
  "Run all five phases, save results."
  []
  (let [t0 (System/currentTimeMillis)]
    (println "═══════════════════════════════════════════════")
    (println " Experiment 095: Boxes as Coordinates")
    (println "═══════════════════════════════════════════════")

    ;; Ensure space is loaded
    (println "\nLoading Torah space...")
    (c/space)
    (println "Space loaded.")

    ;; Phase 1
    (let [p1 (phase-1-dimensions-as-coordinates)
          _ (save-edn (str data-dir "phase1-coordinates.edn") p1)

          ;; Phase 2
          p2 (phase-2-sub-regions)
          _ (save-edn (str data-dir "phase2-regions.edn") p2)

          ;; Phase 3
          p3 (phase-3-nesting)
          _ (save-edn (str data-dir "phase3-nesting.edn") p3)

          ;; Phase 4
          p4 (phase-4-self-reference p1)
          _ (save-edn (str data-dir "phase4-selfref.edn") p4)

          ;; Phase 5
          p5 (phase-5-cross-numerics p1)
          _ (save-edn (str data-dir "phase5-numerics.edn") p5)

          elapsed (quot (- (System/currentTimeMillis) t0) 1000)]

      (println "\n═══════════════════════════════════════════════")
      (println (str " Complete in " elapsed "s"))
      (println "═══════════════════════════════════════════════")

      ;; Summary
      (println "\n── Key Findings ──")
      (doseq [box p1]
        (when (pos? (:construction-hits box))
          (println (str "  ★ " (:box box) ": " (:construction-hits box)
                        "/" (:total-assignments box) " coords hit construction chapters"))))
      (println (str "\n  Self-reference: real rate="
                    (format "%.3f" (:real-rate p4))
                    ", null=" (format "%.3f" (:null-mean p4))
                    ", z=" (when (:z-score p4) (format "%.2f" (:z-score p4)))
                    ", percentile=" (format "%.1f" (* 100 (:percentile p4))) "%"))
      (println (str "\n  Containment:"))
      (println (str "    Ark ⊂ HoH: " (get-in p3 [:containment :ark-in-hoh])))
      (println (str "    HoH ⊂ Tab: " (get-in p3 [:containment :hoh-in-tab])))
      (println (str "    Center in Ark: " (get-in p3 [:center :in-ark])))

      {:phase1 p1 :phase2 p2 :phase3 p3 :phase4 p4 :phase5 p5
       :elapsed-seconds elapsed})))

;; ── REPL ───────────────────────────────────────────────────

(comment
  ;; Run everything
  (def results (run-all))

  ;; Quick test: Ark (5,3,3,0) → should be Numbers 18:32
  (describe-pos (c/coord->idx 5 3 3 0))

  ;; Center → Lev 8:35
  (describe-pos (c/coord->idx 3 25 6 33))

  ;; Noah's Ark dims as letters
  (dims-as-letters [300 50 30])  ;; ש,נ,ל

  ;; שמר = 540 = 4 × 135 (פנה)
  (g/word-value "שמר")   ;; 540
  (g/word-value "פנה")   ;; 135
  (* 4 135)              ;; 540

  ;; Phase 1 only
  (def p1 (phase-1-dimensions-as-coordinates))

  ;; Phase 2 only
  (def p2 (phase-2-sub-regions))
  )
