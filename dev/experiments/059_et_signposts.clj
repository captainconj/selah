;; Experiment 059: The את Signposts — Fibers Through the Jubilee Bridges
;;
;; Experiment 058 found 8 את that bridge jubilee (b-axis) boundaries.
;; The aleph sits at c=12, d=66 — the last position on both the
;; love and understanding axes — and the tav begins c=0, d=0 of
;; the next b-block.
;;
;; These 8 את are narrative hinges:
;;   Covenant → betrayal → exile → calling → redemption →
;;   sacrifice → settlement → possession.
;;
;; This experiment follows every fiber through each signpost.
;; Four fibers per position, two positions per את (aleph + tav),
;; = 64 fibers total. What do they connect?
;;
;; FINDINGS:
;;
;; 1. COORDINATE STRUCTURE: Every aleph sits at c=12, d=66 (END of love,
;;    END of understanding). Every tav sits at c=0, d=0 (BEGINNING of both).
;;    First letter of the alphabet at the last coordinate.
;;    Last letter at the first. End to beginning across the jubilee.
;;
;; 2. GEMATRIA TOTALS across all 8 signposts:
;;    - a-axis (completeness) fibers total = 10,725 = 13 × 825
;;      The completeness axis divides by love.
;;    - d-axis (understanding) fibers total = 80,857 = 7 × 11,551
;;      The understanding axis divides by completeness.
;;    The axes speak each other's language.
;;
;; 3. שמר (GUARD) appears at את#5 — Exodus 12:17 (the Passover):
;;    ושמרתם את המצות — "You shall GUARD the matzot."
;;    Both the aleph's and tav's d-fibers contain שמר.
;;    Same root as the center (Lev 8:35) and the sword (Gen 3:24).
;;    Three guard posts: center, sword, Passover.
;;
;; 4. WORDS IN THE D-FIBERS:
;;    - את#1: ברית (covenant) — and it IS the covenant with Isaac
;;    - את#3: ישראל — and it IS Jacob/Israel descending to Egypt
;;    - את#4: ישראל, משה — and it IS Moses called at the bush
;;    - את#6: יהוה — and it IS the sacrifice before the LORD
;;    - את#7: d-fiber gematria 6,365 = 67 × 95 (÷ understanding)
;;    - את#8: אמת (truth) — "this land we possessed"
;;
;; 5. DISTRIBUTION: a-values are [0, 1, 1, 1, 2, 2, 5, 5].
;;    Days 3, 4, 6 have NO jubilee-bridging את.
;;    None are in a=3 (center seventh / Leviticus).
;;    The center guards; the signposts bridge. Complementary.
;;
;; 6. 172 fiber intersections between signposts. The fibers from
;;    different את cross each other throughout the space.
;;    Signposts sharing the same a-value have fully overlapping b-fibers.

(require '[selah.space.coords :as c]
         '[clojure.string :as str]
         '[clojure.set :as set])

(def s (c/space))
(def vrefs (:verse-ref s))

;;; ============================================================
;;; UTILITIES
;;; ============================================================

(defn verse-text
  "Get the letter-only text of a verse reference."
  [vref]
  (apply str (map #(c/letter-at s %) (range (:start vref) (:end vref)))))

(defn verse-label [vref]
  (str (:book vref) " " (:ch vref) ":" (:vs vref)))

(defn fiber-info
  "Extract a fiber and return a map of its data."
  [free-axis fixed]
  (let [positions (c/fiber free-axis fixed)
        n (alength positions)
        letters (apply str (map #(c/letter-at s (aget positions %)) (range n)))
        first-pos (aget positions 0)
        last-pos (aget positions (dec n))
        v-first (c/verse-at s first-pos)
        v-last (c/verse-at s last-pos)
        gv-sum (reduce + (map #(c/gv-at s (aget positions %)) (range n)))]
    {:axis      free-axis
     :length    n
     :positions positions
     :letters   letters
     :gv-sum    gv-sum
     :start-pos first-pos
     :end-pos   last-pos
     :start-verse v-first
     :end-verse   v-last}))

(defn print-fiber
  "Print a fiber with its metadata."
  [label free-axis fixed]
  (let [{:keys [length letters gv-sum start-verse end-verse]} (fiber-info free-axis fixed)
        axis-names {:a "completeness" :b "jubilee" :c "love/unity" :d "understanding"}]
    (println (format "  %s-fiber (%d letters — %s):" (name free-axis) length (get axis-names free-axis)))
    (println (format "    text: %s" letters))
    (println (format "    gematria sum: %,d" gv-sum))
    (println (format "    mod 7=%d  mod 13=%d  mod 67=%d" (mod gv-sum 7) (mod gv-sum 13) (mod gv-sum 67)))
    (println (format "    spans: %s → %s" (verse-label start-verse) (verse-label end-verse)))))

(defn coord-map
  "Convert a position to a named coordinate map."
  [pos]
  (let [c (c/idx->coord pos)]
    {:a (aget c 0) :b (aget c 1) :c (aget c 2) :d (aget c 3)}))

(defn fixed-for
  "Given a coordinate map and a free axis, return the fixed axes."
  [coord-m free-axis]
  (dissoc coord-m free-axis))

;;; ============================================================
;;; FIND THE 8 JUBILEE-BRIDGING את
;;; ============================================================

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

(def jubilee-bridging
  (filter (fn [pos]
            (let [c1 (c/idx->coord pos)
                  c2 (c/idx->coord (inc pos))]
              (not= (aget c1 1) (aget c2 1))))
          et-positions))

(println "╔══════════════════════════════════════════════════════════════╗")
(println "║  EXPERIMENT 059: THE את SIGNPOSTS — FIBERS THROUGH HINGES  ║")
(println "╚══════════════════════════════════════════════════════════════╝")
(println)
(println (format "Total את in Torah: %,d" (count et-positions)))
(println (format "Jubilee-bridging את: %d" (count jubilee-bridging)))
(println)

;;; ============================================================
;;; FOR EACH את: PRINT CONTEXT + ALL 8 FIBERS (4 per letter)
;;; ============================================================

(def all-fiber-data (atom []))  ;; collect for intersection analysis

(doseq [[idx pos] (map-indexed vector jubilee-bridging)]
  (let [aleph-pos pos
        tav-pos (inc pos)
        d-aleph (c/describe aleph-pos)
        d-tav (c/describe tav-pos)
        aleph-coord (coord-map aleph-pos)
        tav-coord (coord-map tav-pos)
        v-aleph (c/verse-at s aleph-pos)
        v-tav (c/verse-at s tav-pos)]

    (println (format "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"))
    (println (format "  את #%d" (inc idx)))
    (println (format "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"))
    (println)
    (println (format "  א position %,d  coord %s  %s"
                     aleph-pos (vec (c/idx->coord aleph-pos)) (:verse d-aleph)))
    (println (format "  ת position %,d  coord %s  %s"
                     tav-pos (vec (c/idx->coord tav-pos)) (:verse d-tav)))
    (println)

    ;; Context: the verse(s) containing this את
    (println "  ── Verse context ──")
    (let [v-text (verse-text v-aleph)]
      (println (format "  %s: %s" (verse-label v-aleph) v-text)))
    (when (not= (:start v-aleph) (:start v-tav))
      (let [v-text (verse-text v-tav)]
        (println (format "  %s: %s" (verse-label v-tav) v-text))))
    (println)

    ;; Fibers through the ALEPH
    (println "  ── Fibers through א ──")
    (doseq [axis [:a :b :c :d]]
      (let [fixed (fixed-for aleph-coord axis)
            fi (fiber-info axis fixed)]
        (swap! all-fiber-data conj {:et-idx (inc idx) :letter \א :pos aleph-pos
                                     :axis axis :fiber fi})
        (print-fiber (str "א-" (name axis)) axis fixed))
      (println))

    ;; Fibers through the TAV
    (println "  ── Fibers through ת ──")
    (doseq [axis [:a :b :c :d]]
      (let [fixed (fixed-for tav-coord axis)
            fi (fiber-info axis fixed)]
        (swap! all-fiber-data conj {:et-idx (inc idx) :letter \ת :pos tav-pos
                                     :axis axis :fiber fi})
        (print-fiber (str "ת-" (name axis)) axis fixed))
      (println))

    (println)))

;;; ============================================================
;;; INTERSECTION ANALYSIS: Do fibers from different את share positions?
;;; ============================================================

(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  INTERSECTION ANALYSIS")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)

;; Build position sets for each fiber, keyed by [et-idx letter axis]
(let [fiber-sets (map (fn [{:keys [et-idx letter axis fiber]}]
                        {:key   [et-idx letter axis]
                         :label (format "את#%d %s %s-fiber" et-idx letter (name axis))
                         :pset  (set (map #(aget ^ints (:positions fiber) %)
                                          (range (:length fiber))))})
                      @all-fiber-data)
      ;; Check all pairs from DIFFERENT את
      pairs (for [a fiber-sets
                  b fiber-sets
                  :when (< (first (:key a)) (first (:key b)))]
              [a b])
      intersections (filter (fn [[a b]]
                              (seq (set/intersection (:pset a) (:pset b))))
                            pairs)]
  (if (empty? intersections)
    (println "  No fibers from different את share positions.")
    (do
      (println (format "  Found %d fiber intersections:" (count intersections)))
      (println)
      (doseq [[a b] intersections]
        (let [shared (sort (set/intersection (:pset a) (:pset b)))]
          (println (format "  %s  ∩  %s" (:label a) (:label b)))
          (println (format "    %d shared positions" (count shared)))
          (doseq [pos (take 5 shared)]
            (let [d (c/describe pos)]
              (println (format "      pos %,d  %s  %s  %s" pos (:letter d) (:coord d) (:verse d)))))
          (when (> (count shared) 5)
            (println (format "      ... and %d more" (- (count shared) 5))))
          (println))))))

;;; ============================================================
;;; WORD SEARCH IN FIBERS
;;; ============================================================

(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  WORD SEARCH ACROSS FIBERS")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)

(let [target-words {"שמר" "guard/keep"
                    "את"  "aleph-tav"
                    "תורה" "Torah"
                    "אחד" "one"
                    "אהבה" "love"
                    "חיים" "life"
                    "ברית" "covenant"
                    "שלום" "peace"
                    "אמת" "truth"
                    "אור" "light"
                    "יהוה" "YHWH"
                    "ישראל" "Israel"
                    "משה" "Moses"}]
  (doseq [[word meaning] (sort-by key target-words)]
    (let [hits (filter (fn [{:keys [fiber]}]
                         (>= (.indexOf ^String (:letters fiber) word) 0))
                       @all-fiber-data)]
      (when (seq hits)
        (println (format "  %s (%s) — found in %d fibers:" word meaning (count hits)))
        (doseq [{:keys [et-idx letter axis fiber]} hits]
          (let [idx (.indexOf ^String (:letters fiber) word)]
            (println (format "    את#%d %s %s-fiber at offset %d" et-idx letter (name axis) idx))))
        (println)))))

;;; ============================================================
;;; GEMATRIA PATTERNS
;;; ============================================================

(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  GEMATRIA PATTERNS")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)

;; Group by axis and look for numerical patterns
(doseq [axis [:a :b :c :d]]
  (let [fibers-on-axis (filter #(= axis (:axis %)) @all-fiber-data)
        sums (map #(get-in % [:fiber :gv-sum]) fibers-on-axis)]
    (println (format "  %s-axis fiber sums:" (name axis)))
    (doseq [{:keys [et-idx letter fiber]} fibers-on-axis]
      (let [gv (:gv-sum fiber)]
        (print (format "    את#%d %s: %,6d" et-idx letter gv))
        (when (zero? (mod gv 7))   (print "  (÷7)"))
        (when (zero? (mod gv 13))  (print "  (÷13)"))
        (when (zero? (mod gv 37))  (print "  (÷37)"))
        (when (zero? (mod gv 67))  (print "  (÷67)"))
        (when (zero? (mod gv 73))  (print "  (÷73)"))
        (when (zero? (mod gv 401)) (print "  (÷401=את)"))
        (println)))
    (let [total (reduce + sums)]
      (println (format "    TOTAL: %,d" total))
      (when (zero? (mod total 7)) (println "    ÷7 ✓"))
      (when (zero? (mod total 13)) (println "    ÷13 ✓"))
      (when (zero? (mod total 401)) (println "    ÷401 (=את) ✓")))
    (println)))

;;; ============================================================
;;; COORDINATE PATTERNS
;;; ============================================================

(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  COORDINATE STRUCTURE OF THE 8 את")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)

(println "  Each aleph is at c=12, d=66 (end of love, end of understanding)")
(println "  Each tav is at c=0, d=0 (beginning of love, beginning of understanding)")
(println)
(println "  #  א-coord          ת-coord          a  b→b' Verse")
(println "  ─  ────────────────  ────────────────  ─  ──── ─────")
(doseq [[idx pos] (map-indexed vector jubilee-bridging)]
  (let [c1 (c/idx->coord pos)
        c2 (c/idx->coord (inc pos))
        v (c/verse-at s pos)]
    (println (format "  %d  [%d,%2d,%2d,%2d]      [%d,%2d,%2d,%2d]      %d  %2d→%2d %s"
                     (inc idx)
                     (aget c1 0) (aget c1 1) (aget c1 2) (aget c1 3)
                     (aget c2 0) (aget c2 1) (aget c2 2) (aget c2 3)
                     (aget c1 0)
                     (aget c1 1) (aget c2 1)
                     (verse-label v)))))

(println)
(println "  a-values of the 8 את:")
(let [a-vals (mapv #(aget (c/idx->coord %) 0) jubilee-bridging)]
  (println (format "    %s" a-vals))
  (println (format "    distribution: %s" (frequencies a-vals))))

(println)
(println "  b-values (jubilee boundaries crossed):")
(let [b-crossings (mapv (fn [pos]
                          [(aget (c/idx->coord pos) 1)
                           (aget (c/idx->coord (inc pos)) 1)])
                        jubilee-bridging)]
  (doseq [[idx [b1 b2]] (map-indexed vector b-crossings)]
    (println (format "    את#%d: b=%2d → b=%2d  (jubilee %d → %d)" (inc idx) b1 b2 b1 b2))))

;;; ============================================================
;;; ALEPH-TAV PAIR: SHARED VS UNIQUE FIBERS
;;; ============================================================

(println)
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  ALEPH-TAV PAIR ANALYSIS")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)
(println "  For each את, the aleph and tav are adjacent but in different")
(println "  b-blocks. Their fibers reach into different regions of the space.")
(println)

(doseq [[idx pos] (map-indexed vector jubilee-bridging)]
  (let [aleph-c (coord-map pos)
        tav-c (coord-map (inc pos))
        ;; a-fiber is the only axis where both letters might share
        ;; the same fixed coordinates (b, c, d differ)
        ]
    (println (format "  את#%d:" (inc idx)))
    (println (format "    א at %s  ת at %s" aleph-c tav-c))

    ;; The a-fiber: same free axis, different fixed values
    ;; These sweep through different parts of the Torah
    (let [a-aleph (fiber-info :a (fixed-for aleph-c :a))
          a-tav   (fiber-info :a (fixed-for tav-c :a))]
      (println (format "    a-fibers: א spans %s→%s | ת spans %s→%s"
                       (verse-label (:start-verse a-aleph))
                       (verse-label (:end-verse a-aleph))
                       (verse-label (:start-verse a-tav))
                       (verse-label (:end-verse a-tav)))))

    ;; The d-fiber gematria pair
    (let [d-aleph (fiber-info :d (fixed-for aleph-c :d))
          d-tav   (fiber-info :d (fixed-for tav-c :d))]
      (println (format "    d-fiber gematria: א=%,d  ת=%,d  sum=%,d"
                       (:gv-sum d-aleph) (:gv-sum d-tav)
                       (+ (:gv-sum d-aleph) (:gv-sum d-tav))))
      (let [pair-sum (+ (:gv-sum d-aleph) (:gv-sum d-tav))]
        (when (zero? (mod pair-sum 7))
          (println (format "      ÷7 ✓  (%d × 7)" (/ pair-sum 7))))
        (when (zero? (mod pair-sum 401))
          (println (format "      ÷401 (=את) ✓")))))
    (println)))

;;; ============================================================
;;; CONNECTION TO THE CENTER
;;; ============================================================

(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println "  CONNECTION TO THE CENTER (3, 25, 6, 33)")
(println "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
(println)

(let [center-pos (c/coord->idx 3 25 6 33)
      center-verse (c/verse-at s center-pos)]
  (println (format "  Center of space: position %,d — %s" center-pos (verse-label center-verse)))
  (println)

  ;; Do any את fibers pass through or near the center?
  (doseq [{:keys [et-idx letter axis fiber]} @all-fiber-data]
    (let [positions (:positions fiber)
          n (:length fiber)]
      (doseq [i (range n)]
        (let [pos (aget positions i)]
          (when (= pos center-pos)
            (println (format "  *** את#%d %s %s-fiber passes through THE CENTER ***"
                             et-idx letter (name axis)))))))))

;; Check if any את share a-value 3 (center seventh)
(println)
(println "  את in the center seventh (a=3):")
(doseq [[idx pos] (map-indexed vector jubilee-bridging)]
  (let [c1 (c/idx->coord pos)]
    (when (= 3 (aget c1 0))
      (let [d (c/describe pos)]
        (println (format "    את#%d at %s — %s" (inc idx) (:coord d) (:verse d)))))))

(println)
(println "Done.")
