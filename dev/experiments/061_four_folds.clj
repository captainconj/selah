(ns experiments.061-four-folds
  "The four folds of the 4D Torah space.

   304,850 = 7 x 50 x 13 x 67.  Every letter has coordinate (a, b, c, d).

   Four mirror symmetries:
     a-fold: (a,b,c,d) <-> (6-a,b,c,d)   across 7 days
     b-fold: (a,b,c,d) <-> (a,49-b,c,d)  across 50 jubilees
     c-fold: (a,b,c,d) <-> (a,b,12-c,d)  across 13 loves
     d-fold: (a,b,c,d) <-> (a,b,c,66-d)  across 67 understandings

   FINDINGS:

   1. THREE FOLDS SHARE A SINGLE CREASE CENTER: Leviticus 8:35
      a-fold (a=3), c-fold (c=6), d-fold (d=33) all converge on position 152,860.
      Only b-fold differs (even dimension, no exact center — crease pair at b=24/25).
      b=24 center: Leviticus 8:23 (consecration — blood on Aaron's ear, hand, foot).

   2. THE FULL ANTIPODE: position 0 (Gen 1:1) maps to position 304,849 (Deut 34:12).
      First letter of Torah -> last letter of Torah. The space is a closed book.
      Leviticus' antipode maps back into itself (Lev 14:36).

   3. ALEPH-TAV MIRROR PAIRS — the a-fold is special:
      a-fold: 1,222 self-mirrors (div by 13!) + 52 outer pairs (div by 13!)
      b-fold: 0 self-mirrors + 53 outer pairs
      c-fold: 461 self-mirrors + 46 outer pairs
      d-fold: 100 self-mirrors + 82 outer pairs
      The a-fold's 1,222 = 13 x 94 self-mirrors and 52 = 13 x 4 outer pairs
      both divide by love (13). No other fold shows this.

   4. MILEMARKER HIGHLIGHTS:
      - Gen 1:1 torah@+50 a-mirrors to Deut 6:1 (the Shema) — pair GV 651 = 7 x 93
      - Three center YHWH@+7 in Leviticus a-mirror to themselves — pair GV 52 = 13 x 4
      - Exod 39:8 torah@+50 a-mirrors to itself (the breastplate) — pair GV 1222 = 13 x 94
      - Deut 5:16 reversed-torah@+50 a-mirrors to Exod 6:26 — pair GV 777 = 7 x 111
      - Deut 8:9 reversed-torah@+50 a-mirrors to Gen 3:10 (hiding in Eden)

   5. VERSE PAIR GEMATRIA — rates match statistical expectation:
      All four folds produce div-by-7 rates ~14%, div-by-13 ~8%, div-by-67 ~1.5%.
      No axis shows significant deviation. The structure is in the topology, not the
      verse-level arithmetic.

   6. CREASE POPULATIONS:
      a=3:  43,550 letters (1/7)
      c=6:  23,450 letters (1/13)
      d=33:  4,550 letters (1/67)
      The crease of creases (a=3, c=6, d=33) = 50 letters (one per jubilee)."
  (:require [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [selah.els.engine :as els]
            [clojure.string :as str]))

;; ── Fold primitives ──────────────────────────────────────

(defn a-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (- 6 (aget c 0)) (aget c 1) (aget c 2) (aget c 3))))

(defn b-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (- 49 (aget c 1)) (aget c 2) (aget c 3))))

(defn c-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (aget c 1) (- 12 (aget c 2)) (aget c 3))))

(defn d-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (aget c 1) (aget c 2) (- 66 (aget c 3)))))

(def fold-fns {:a a-mirror :b b-mirror :c c-mirror :d d-mirror})

;; ── Utilities ────────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn verse-text [s vref]
  (apply str (map #(coords/letter-at s %) (range (:start vref) (:end vref)))))

(defn book-offset []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        lengths (map #(count (oshb/book-letters %)) books)]
    (zipmap books (reductions + 0 (butlast lengths)))))

(defn context-at [s i radius]
  (let [start (max 0 (- i radius))
        end   (min (:n s) (+ i radius 1))]
    (apply str (map #(coords/letter-at s %) (range start end)))))

(defn find-et-positions
  "Find all consecutive aleph-tav in the stream."
  [s]
  (let [stream ^bytes (:stream s)
        n      (alength stream)
        aleph  (byte (get coords/char->idx \א))
        tav    (byte (get coords/char->idx \ת))]
    (loop [i 0 acc (transient [])]
      (if (>= i (dec n))
        (persistent! acc)
        (if (and (= (aget stream i) aleph)
                 (= (aget stream (inc i)) tav))
          (recur (inc i) (conj! acc i))
          (recur (inc i) acc))))))

(defn find-milemarkers
  "Find all classic ELS milemarkers."
  [s]
  (let [offsets (book-offset)
        results (atom [])]
    ;; Genesis: torah at +50
    (let [letters (vec (oshb/book-letters "Genesis"))]
      (doseq [hit (els/search letters "תורה" 50)]
        (swap! results conj {:book "Genesis" :word "תורה" :skip 50
                             :global-start (+ (offsets "Genesis") (:start hit))})))
    ;; Exodus: torah at +50
    (let [letters (vec (oshb/book-letters "Exodus"))]
      (doseq [hit (els/search letters "תורה" 50)]
        (swap! results conj {:book "Exodus" :word "תורה" :skip 50
                             :global-start (+ (offsets "Exodus") (:start hit))})))
    ;; Leviticus: YHWH at +7 and -7
    (let [letters (vec (oshb/book-letters "Leviticus"))]
      (doseq [hit (els/search letters "יהוה" 7)]
        (swap! results conj {:book "Leviticus" :word "יהוה" :skip 7
                             :global-start (+ (offsets "Leviticus") (:start hit))}))
      (doseq [hit (els/search letters "יהוה" -7)]
        (swap! results conj {:book "Leviticus" :word "יהוה" :skip -7
                             :global-start (+ (offsets "Leviticus") (:start hit))})))
    ;; Numbers: reversed torah at +50
    (let [letters (vec (oshb/book-letters "Numbers"))]
      (doseq [hit (els/search letters "הרות" 50)]
        (swap! results conj {:book "Numbers" :word "הרות" :skip 50
                             :global-start (+ (offsets "Numbers") (:start hit))})))
    ;; Deuteronomy: reversed torah at +50
    (let [letters (vec (oshb/book-letters "Deuteronomy"))]
      (doseq [hit (els/search letters "הרות" 50)]
        (swap! results conj {:book "Deuteronomy" :word "הרות" :skip 50
                             :global-start (+ (offsets "Deuteronomy") (:start hit))})))
    @results))

;; ── Main ─────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 061: THE FOUR FOLDS")
  (println "  a-fold (7 days), b-fold (50 jubilees),")
  (println "  c-fold (13 loves), d-fold (67 understandings)")
  (println "================================================================")
  (println)

  (let [s (coords/space)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: THE CREASE — what's at the center of each fold?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: THE CREASE")
    (println "================================================================")
    (println)

    ;; a-fold: a=3 is the exact center (odd dimension 7)
    (println "── a-fold crease: a=3 (center seventh = Leviticus) ──")
    (let [center-pos (coords/coord->idx 3 25 6 33)
          v (coords/verse-at s center-pos)]
      (println (format "  Geometric center (3,25,6,33) = position %,d" center-pos))
      (println (format "  Verse: %s" (verse-label v)))
      (println "  [Already known: Lev 8:35, 'seven days keep the charge of the LORD']"))
    (println)

    ;; b-fold: b has 50 values (0..49), no exact center. Crease pair: b=24, b=25.
    (println "── b-fold crease: b=24 and b=25 (the jubilee pair) ──")
    (println "  50 is even — no single center. The two innermost jubilee layers.")
    (let [;; The crease line between b=24 and b=25 passes through the geometric center.
          ;; Look at what's at the midpoint of the b=24 hyperplane
          pos-24 (coords/coord->idx 3 24 6 33)
          pos-25 (coords/coord->idx 3 25 6 33)
          v-24 (coords/verse-at s pos-24)
          v-25 (coords/verse-at s pos-25)]
      (println (format "  At (3,24,6,33): position %,d — %s — letter %s"
                       pos-24 (verse-label v-24) (coords/letter-at s pos-24)))
      (println (format "  At (3,25,6,33): position %,d — %s — letter %s"
                       pos-25 (verse-label v-25) (coords/letter-at s pos-25)))
      ;; What's the linear midpoint of the b-axis?
      (println)
      (println "  The b-fold maps b=0 <-> b=49, b=1 <-> b=48, ..., b=24 <-> b=25.")
      (println "  Positions at b=24 and b=25 are adjacent crease partners."))
    (println)

    ;; c-fold: c has 13 values (0..12), center is c=6 (exact)
    (println "── c-fold crease: c=6 (center of love/unity axis) ──")
    (let [center-pos (coords/coord->idx 3 25 6 33)
          v (coords/verse-at s center-pos)]
      (println (format "  c=6 is the exact center of 13. Self-mirrors under c-fold."))
      (println (format "  At (3,25,6,33): %s" (verse-label v)))
      ;; Show the c-fiber through the center to see c=6's context
      (let [positions (coords/fiber :c {:a 3 :b 25 :d 33})]
        (println "  c-fiber through (3,25,*,33):")
        (doseq [i (range 13)]
          (let [pos (aget positions i)
                d (coords/describe pos)]
            (println (format "    c=%2d  %s  gv=%3d  %s%s"
                             i (:letter d) (:gematria d) (:verse d)
                             (if (= i 6) "  <-- CREASE" "")))))))
    (println)

    ;; d-fold: d has 67 values (0..66), center is d=33 (exact)
    (println "── d-fold crease: d=33 (center of understanding axis) ──")
    (let [center-pos (coords/coord->idx 3 25 6 33)]
      (println "  d=33 is the exact center of 67. Self-mirrors under d-fold.")
      (println "  This is where the sword's chet sits (experiment 056).")
      ;; Show what's at d=33 across a few fibers
      (let [positions (coords/fiber :d {:a 3 :b 25 :c 6})]
        (println (format "  Center d-fiber letters: %s"
                         (apply str (map #(coords/letter-at s (aget positions %)) (range 67)))))
        (println (format "  Letter at d=33: %s (gv=%d)"
                         (coords/letter-at s (aget positions 33))
                         (coords/gv-at s (aget positions 33))))))
    (println)

    ;; Crease text: what verse is at the geometric center for each fold?
    (println "── Crease text summary ──")
    (println)
    (let [;; For each fold, show the verse at the geometric center of the crease
          ;; a: (3, 25, 6, 33)
          ;; b: midpoint between (3,24,6,33) and (3,25,6,33)
          ;; c: (3, 25, 6, 33)
          ;; d: (3, 25, 6, 33)
          ;; They all converge on the same position except b!
          b-crease-24 (coords/coord->idx 3 24 6 33)
          center      (coords/coord->idx 3 25 6 33)
          v-b24  (coords/verse-at s b-crease-24)
          v-ctr  (coords/verse-at s center)]
      (println "  a-fold crease center: Lev 8:35 — seven days, guard the charge")
      (println (format "  b-fold crease center: %s (b=24) / %s (b=25)"
                       (verse-label v-b24) (verse-label v-ctr)))
      (println "  c-fold crease center: Lev 8:35 — same as a-fold")
      (println "  d-fold crease center: Lev 8:35 — same as a-fold")
      (println)
      (println "  Three of four folds share the same crease center.")
      (println (format "  b-fold's crease partner (b=24): %s" (verse-label v-b24)))
      ;; Show the b=24 crease verse text
      (let [vref (nth (:verse-ref s) (aget ^ints (:verses s) b-crease-24))]
        (println (format "    text: %s" (verse-text s vref)))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 2: את MIRROR COUNT
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 2: ALEPH-TAV MIRRORS")
    (println "  6,032 consecutive את — how many mirror to another את?")
    (println "================================================================")
    (println)

    (let [et-pos (find-et-positions s)
          et-set (set et-pos)]  ;; set of aleph positions

      (println (format "Total את: %,d" (count et-pos)))
      (println)

      (doseq [[axis-key axis-name mirror-fn crease-desc]
              [[:a "a-fold (7 days)"           a-mirror "a=3"]
               [:b "b-fold (50 jubilees)"      b-mirror "b=24,25"]
               [:c "c-fold (13 loves)"         c-mirror "c=6"]
               [:d "d-fold (67 understandings)" d-mirror "d=33"]]]
        (let [;; For each את at position p (the aleph), mirror p.
              ;; If mirror(p) is also an aleph of an את, we have a mirror pair.
              ;; Self-mirror: mirror(p) = p (only on crease)
              mirrors (map (fn [p]
                             (let [mp (mirror-fn p)]
                               {:pos p :mirror mp
                                :self? (= p mp)
                                :pair? (contains? et-set mp)}))
                           et-pos)
              self-mirrors  (filter :self? mirrors)
              outer-pairs   (filter #(and (:pair? %) (not (:self? %))) mirrors)
              ;; outer-pairs counts each pair twice (p->q and q->p)
              unique-pairs  (/ (count outer-pairs) 2)
              total-mirrors (+ (count self-mirrors) unique-pairs)]
          (println (format "── %s (crease: %s) ──" axis-name crease-desc))
          (println (format "  Self-mirrors (את on crease): %d" (count self-mirrors)))
          (println (format "  Outer mirror pairs:          %d" unique-pairs))
          (println (format "  Total mirrored את:           %d (of %,d)"
                           (+ (count self-mirrors) (* 2 unique-pairs)) (count et-pos)))
          ;; Divisibility
          (let [n (count self-mirrors)]
            (when (pos? n)
              (print (format "  Self-mirror count %d:" n))
              (when (zero? (mod n 7))  (print " div by 7"))
              (when (zero? (mod n 13)) (print " div by 13"))
              (when (zero? (mod n 67)) (print " div by 67"))
              (println)))
          (when (pos? unique-pairs)
            (print (format "  Pair count %d:" unique-pairs))
            (when (zero? (mod unique-pairs 7))  (print " div by 7"))
            (when (zero? (mod unique-pairs 13)) (print " div by 13"))
            (when (zero? (mod unique-pairs 67)) (print " div by 67"))
            (println))
          (println))))

    ;; ════════════════════════════════════════════════════════
    ;; PART 3: MILEMARKER MIRRORS UNDER ALL FOUR FOLDS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 3: MILEMARKER MIRRORS UNDER ALL FOUR FOLDS")
    (println "  Each milemarker's start position, mirrored under each fold.")
    (println "================================================================")
    (println)

    (let [markers (find-milemarkers s)]
      (println (format "Found %d milemarkers.\n" (count markers)))

      ;; Summary table
      (println (format "  %-6s %-5s %-22s | %-22s  %-22s  %-22s  %-22s"
                       "Word" "Skip" "Verse"
                       "a-mirror" "b-mirror" "c-mirror" "d-mirror"))
      (println (apply str (repeat 130 "-")))

      (doseq [m (sort-by :global-start markers)]
        (let [i (:global-start m)
              v (coords/verse-at s i)
              mirrors (into {}
                            (map (fn [[k f]]
                                   (let [mi (f i)
                                         mv (coords/verse-at s mi)]
                                     [k (verse-label mv)]))
                                 fold-fns))]
          (println (format "  %-6s %+4d  %-22s | %-22s  %-22s  %-22s  %-22s"
                           (:word m) (:skip m)
                           (verse-label v)
                           (:a mirrors) (:b mirrors) (:c mirrors) (:d mirrors)))))

      ;; Detailed: pair gematria under each fold
      (println)
      (println "── Pair gematria (original ELS letters + mirror letters) ──")
      (println)
      (println (format "  %-6s %-5s %-18s  |  %8s  %8s  %8s  %8s"
                       "Word" "Skip" "Verse" "a-pair" "b-pair" "c-pair" "d-pair"))
      (println (apply str (repeat 100 "-")))

      (doseq [m (sort-by :global-start markers)]
        (let [i    (:global-start m)
              skip (:skip m)
              v    (coords/verse-at s i)
              ;; ELS positions of this milemarker
              els-positions (map #(+ i (* % skip)) (range (count (:word m))))
              ;; Only use valid positions
              valid? (every? #(and (>= % 0) (< % (:n s))) els-positions)
              orig-gv (when valid? (reduce + (map #(coords/gv-at s %) els-positions)))]
          (when valid?
            (let [pair-gvs
                  (into {}
                        (map (fn [[k f]]
                               (let [mirror-positions (map f els-positions)
                                     mv-valid? (every? #(and (>= % 0) (< % (:n s))) mirror-positions)
                                     mirror-gv (when mv-valid? (reduce + (map #(coords/gv-at s %) mirror-positions)))]
                                 [k (when (and orig-gv mirror-gv) (+ orig-gv mirror-gv))]))
                             fold-fns))
                  fmt-gv (fn [gv]
                           (if gv
                             (let [divs (cond-> ""
                                          (zero? (mod gv 7))  (str "/7")
                                          (zero? (mod gv 13)) (str "/13")
                                          (zero? (mod gv 67)) (str "/67"))]
                               (format "%d%s" gv divs))
                             "n/a"))]
              (println (format "  %-6s %+4d  %-18s  |  %8s  %8s  %8s  %8s"
                               (:word m) (:skip m)
                               (verse-label v)
                               (fmt-gv (:a pair-gvs))
                               (fmt-gv (:b pair-gvs))
                               (fmt-gv (:c pair-gvs))
                               (fmt-gv (:d pair-gvs)))))))))

    ;; ════════════════════════════════════════════════════════
    ;; PART 4: NOTABLE GEMATRIA PATTERNS IN MIRROR PAIRS
    ;; ════════════════════════════════════════════════════════

    (println)
    (println "================================================================")
    (println "  PART 4: NOTABLE GEMATRIA PATTERNS IN MIRROR PAIRS")
    (println "  Sample mirror pairs where pair gematria divides by 7, 13, or 67")
    (println "================================================================")
    (println)

    ;; For each fold, sample positions and check pair gematria
    (doseq [[axis-key axis-name mirror-fn]
            [[:a "a-fold (7 days)"            a-mirror]
             [:b "b-fold (50 jubilees)"       b-mirror]
             [:c "c-fold (13 loves)"          c-mirror]
             [:d "d-fold (67 understandings)" d-mirror]]]
      (println (format "── %s ──" axis-name))

      ;; Check verse-level: sum gematria of each verse, compare with mirror verse
      ;; Sample the first 100 verse refs
      (let [vrefs     (:verse-ref s)
            n-verses  (count vrefs)
            ;; For each verse, find its mirror (of its start position), and compare gematria
            div-7  (atom 0)
            div-13 (atom 0)
            div-67 (atom 0)
            sample (atom [])]
        (doseq [vid (range n-verses)]
          (let [vref (nth vrefs vid)
                ;; Verse gematria
                vgv (reduce + (map #(coords/gv-at s %) (range (:start vref) (:end vref))))
                ;; Mirror of the verse's midpoint
                mid (quot (+ (:start vref) (:end vref)) 2)
                mirror-mid (mirror-fn mid)
                ;; Find which verse the mirror lands in
                mirror-v (coords/verse-at s mirror-mid)
                mirror-vref (first (filter #(and (= (:book %) (:book mirror-v))
                                                  (= (:ch %) (:ch mirror-v))
                                                  (= (:vs %) (:vs mirror-v))) vrefs))
                mirror-gv (when mirror-vref
                            (reduce + (map #(coords/gv-at s %) (range (:start mirror-vref) (:end mirror-vref)))))
                pair (when mirror-gv (+ vgv mirror-gv))]
            (when pair
              (when (zero? (mod pair 7))  (swap! div-7 inc))
              (when (zero? (mod pair 13)) (swap! div-13 inc))
              (when (zero? (mod pair 67)) (swap! div-67 inc))
              ;; Collect notable ones (first 5 that div by 67)
              (when (and (zero? (mod pair 67)) (< (count @sample) 5))
                (swap! sample conj
                       {:verse (verse-label vref)
                        :vgv vgv
                        :mirror-verse (verse-label mirror-vref)
                        :mirror-gv mirror-gv
                        :pair pair})))))

        (println (format "  Verse pairs: %,d total" n-verses))
        (println (format "    Pair GV div by 7:  %,d (%.1f%%)" @div-7  (* 100.0 (/ @div-7 (double n-verses)))))
        (println (format "    Pair GV div by 13: %,d (%.1f%%)" @div-13 (* 100.0 (/ @div-13 (double n-verses)))))
        (println (format "    Pair GV div by 67: %,d (%.1f%%)" @div-67 (* 100.0 (/ @div-67 (double n-verses)))))
        (println (format "    Expected by chance: ~%.1f%%  ~%.1f%%  ~%.1f%%"
                         (/ 100.0 7) (/ 100.0 13) (/ 100.0 67)))

        (when (seq @sample)
          (println)
          (println "  Notable pairs (div by 67):")
          (doseq [{:keys [verse vgv mirror-verse mirror-gv pair]} @sample]
            (println (format "    %s (gv=%,d)  <->  %s (gv=%,d)  pair=%,d = 67 x %d"
                             verse vgv mirror-verse mirror-gv pair (/ pair 67)))))
        (println)))

    ;; ════════════════════════════════════════════════════════
    ;; PART 5: FOLD INTERACTIONS — do folds compose?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 5: FOLD COMPOSITION")
    (println "  What happens when you apply two folds in sequence?")
    (println "================================================================")
    (println)

    ;; The combined fold (a then b) on the Torah's first position
    (let [pos0 0
          ;; Apply all single folds
          _ (println "  Position 0 (first letter of Torah):")
          _ (println (format "    coord: %s  letter: %s  verse: %s"
                             (vec (coords/idx->coord pos0))
                             (coords/letter-at s pos0)
                             (verse-label (coords/verse-at s pos0))))
          _ (println)]
      ;; Single folds
      (println "  Single folds of position 0:")
      (doseq [[k f name] [[:a a-mirror "a-fold"]
                           [:b b-mirror "b-fold"]
                           [:c c-mirror "c-fold"]
                           [:d d-mirror "d-fold"]]]
        (let [m (f pos0)
              v (coords/verse-at s m)]
          (println (format "    %s -> %,d  coord %s  letter %s  %s"
                           name m (vec (coords/idx->coord m)) (coords/letter-at s m)
                           (verse-label v)))))
      (println)

      ;; Double folds (should return to original for same axis)
      (println "  Double fold verification (should return to self):")
      (doseq [[k f name] [[:a a-mirror "a-fold"]
                           [:b b-mirror "b-fold"]
                           [:c c-mirror "c-fold"]
                           [:d d-mirror "d-fold"]]]
        (println (format "    %s(%s(0)) = %d %s" name name (f (f pos0))
                         (if (= 0 (f (f pos0))) "OK" "FAIL"))))
      (println)

      ;; Cross-fold: what's at the full antipodal point (all four folds)?
      (println "  Full antipode: all four folds applied:")
      (let [antipode (-> pos0 a-mirror b-mirror c-mirror d-mirror)
            v (coords/verse-at s antipode)
            coord (vec (coords/idx->coord antipode))]
        (println (format "    Position 0 (0,0,0,0) -> antipode %,d  coord %s" antipode coord))
        (println (format "    Letter: %s  Verse: %s" (coords/letter-at s antipode) (verse-label v)))
        (let [vref (nth (:verse-ref s) (aget ^ints (:verses s) antipode))]
          (println (format "    Verse text: %s" (verse-text s vref)))))
      (println)

      ;; The antipode of the first verse of each book
      (println "  Antipode of each book's first letter:")
      (let [offsets (book-offset)]
        (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
          (let [start (get offsets book)
                anti  (-> start a-mirror b-mirror c-mirror d-mirror)
                v     (coords/verse-at s anti)]
            (println (format "    %-13s pos %,6d -> antipode %,6d  %s"
                             book start anti (verse-label v)))))))
    (println)

    ;; ════════════════════════════════════════════════════════
    ;; PART 6: CREASE POPULATION — what sits on each crease?
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 6: CREASE POPULATION")
    (println "  How many letters sit on each crease (self-mirroring positions)?")
    (println "================================================================")
    (println)

    ;; a=3: 304850/7 = 43,550 letters
    (println (format "  a-fold crease (a=3):  %,d letters (1/7 of Torah)" (/ coords/total-letters 7)))
    ;; b-fold: no exact center (even dimension), 0 self-mirroring positions
    (println (format "  b-fold crease:        0 letters (even dimension, no center)"))
    ;; c=6: all positions with c=6
    (let [c6-count (/ coords/total-letters 13)]
      (println (format "  c-fold crease (c=6):  %,d letters (1/13 of Torah)" c6-count)))
    ;; d=33: all positions with d=33
    (let [d33-count (/ coords/total-letters 67)]
      (println (format "  d-fold crease (d=33): %,d letters (1/67 of Torah)" d33-count)))
    (println)

    ;; What books/chapters dominate the c=6 crease?
    (println "── c-fold crease (c=6): what text lives here? ──")
    (let [c6-positions (coords/hyperplane :c 6)
          by-book (group-by (fn [i] (:book (coords/verse-at s (aget c6-positions i))))
                            (range (alength c6-positions)))]
      (doseq [book coords/book-names]
        (println (format "  %-13s  %,d letters" book (count (get by-book book []))))))
    (println)

    ;; What books/chapters dominate the d=33 crease?
    (println "── d-fold crease (d=33): what text lives here? ──")
    (let [d33-positions (coords/hyperplane :d 33)
          by-book (group-by (fn [i] (:book (coords/verse-at s (aget d33-positions i))))
                            (range (alength d33-positions)))]
      (doseq [book coords/book-names]
        (println (format "  %-13s  %,d letters" book (count (get by-book book []))))))
    (println)

    ;; d=33 and c=6 intersection: the crease of creases
    (println "── crease of creases: a=3, c=6, d=33 ──")
    (let [positions (coords/slab {:a 3 :c 6 :d 33})
          n (alength positions)]
      (println (format "  %d positions (one per jubilee)" n))
      (println "  Letters:" (apply str (map #(coords/letter-at s (aget positions %)) (range n))))
      (let [total-gv (reduce + (map #(coords/gv-at s (aget positions %)) (range n)))]
        (println (format "  Total gematria: %,d" total-gv))
        (when (zero? (mod total-gv 7))  (println (format "    div by 7: %d x 7" (/ total-gv 7))))
        (when (zero? (mod total-gv 13)) (println (format "    div by 13: %d x 13" (/ total-gv 13))))
        (when (zero? (mod total-gv 67)) (println (format "    div by 67: %d x 67" (/ total-gv 67))))))
    (println)

    (println "Done.")))
