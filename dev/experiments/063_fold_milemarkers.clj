(ns experiments.063-fold-milemarkers
  "Milemarker mirrors under three remaining folds: b, c, d.

   Experiment 060 found extraordinary mirrors under the a-fold:
     Gen 1:1 torah@+50 → Shema (Deut 6:1)
     Breastplate self-mirrors
     Name ↔ reversed Name at skip 1

   The a-fold was tested. Now: do the b-fold (jubilee), c-fold (love),
   and d-fold (understanding) reveal their own thematic mirrors?

   Each fold is a mirror symmetry of the 4D space:
     b-fold: (a, b, c, d) ↔ (a, 49-b, c, d)  — across 50 jubilees
     c-fold: (a, b, c, d) ↔ (a, b, 12-c, d)   — across 13 loves
     d-fold: (a, b, c, d) ↔ (a, b, c, 66-d)   — across 67 understandings

   FINDINGS
   ========
   B-FOLD (50 jubilees)
   - No self-mirrors (even dimension, no center).
   - Pair GV div by 7: 7/26 (27%) — nearly double the ~14% expected by chance.
   - Pair GV div by 13: 4/26 (15%) — double the ~8% expected.
   - Lev 21:13 YHWH@+7 mirrors to Exod 35:11 (building the tabernacle).
     Pair GV = 35 = 5 × 7. The priestly marriage law folds to the tabernacle blueprint.
   - Lev 27:18 YHWH@+7 mirrors to Num 10:10 — pair GV = 50 (the jubilee number itself).
     The jubilee fold produces the jubilee number at the mirror of the Name.
     And at the mirror position: הוהי at skip -1 (the Name reversed in consecutive text).
   - Lev 14:46 YHWH@+7 mirrors to Exod 40:9 — pair GV = 441 = 21².

   C-FOLD (13 loves)
   - One self-mirror: Gen 16:8 torah@+50 sits at c=6 (the love-axis center).
     Since it self-mirrors, torah@+50 is confirmed at the mirror — the ELS IS its own mirror.
   - Pair GV div by 7: 6/26 (23%) — elevated above chance.
   - Deut 11:29 reversed-torah@+50 c-mirrors to Deut 12:1 ('these are the statutes').
     Mirror word = התור — Torah's first three letters, forward. Pair GV = 1222 = 13 × 94.
     1222 is the SAME pair GV as the a-fold breastplate self-mirror (Exod 39:8).
     The number 1222 = twice-Torah (611+611) appears under two different folds.
   - Lev 26:44 YHWH@+7 c-mirrors to Lev 26:33 — pair GV = 234 = 13 × 18.
     Love axis. Divides by 13 (love). The exile verses mirror each other.
   - c-fold mirrors stay within the same book — the love axis is local.
   - Exod 39:8 (breastplate) c-mirrors to Exod 39:1 (the holy garments).

   D-FOLD (67 understandings)
   - No self-mirrors (none of the 26 milemarkers sit at d=33).
   - d-fold mirrors are extremely local — same verse or adjacent verse.
     This is expected: the d-axis spans only 67 letters.
   - Pair GV div by 7: 5/26 (19%), div by 13: 3/26 (12%) — mildly elevated.
   - Deut 11:29 reversed-torah@+50 d-mirrors within same verse.
     Pair GV = 651 = 7 × 93 — the same number as Gen 1:1's a-fold mirror to the Shema.
   - Exod 39:8 (breastplate) d-mirrors to Exod 39:7 — pair GV = 741 = 13 × 57.
     The breastplate divides by love (13) under both a-fold AND d-fold.
   - Lev 21:13 YHWH@+7 d-mirrors to Lev 21:14 — pair GV = 559 = 13 × 43.

   CROSS-FOLD PATTERNS
   - No mirror positions coincide across folds (each fold maps to a different region).
   - The b-fold is the most numerologically active (27% div-by-7 rate).
   - Recurring signature numbers across folds:
     * 651 = 7×93 appears in a-fold (Gen 1:1 → Shema) and d-fold (Deut 11:29).
     * 1222 = 13×94 appears in a-fold (breastplate self) and c-fold (Deut 11:29 → Deut 12:1).
     * The breastplate (Exod 39:8) divides by 13 under a-fold, c-fold, and d-fold.
   - The b-fold uniquely produces pair GV = 50 (the jubilee) at the Name's mirror,
     and the Name reversed (הוהי) in consecutive text at that mirror position."
  (:require [selah.els.engine :as els]
            [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Fold primitives ──────────────────────────────────────

(defn b-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (- 49 (aget c 1)) (aget c 2) (aget c 3))))

(defn c-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (aget c 1) (- 12 (aget c 2)) (aget c 3))))

(defn d-mirror ^long [^long i]
  (let [c (coords/idx->coord i)]
    (coords/coord->idx (aget c 0) (aget c 1) (aget c 2) (- 66 (aget c 3)))))

(def fold-fns
  {:b {:name "b-fold (50 jubilees)"    :mirror-fn b-mirror :axis :b :max 49 :center nil}
   :c {:name "c-fold (13 loves)"       :mirror-fn c-mirror :axis :c :max 12 :center 6}
   :d {:name "d-fold (67 understandings)" :mirror-fn d-mirror :axis :d :max 66 :center 33}})

;; ── Utilities ────────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn book-offset []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        lengths (map #(count (oshb/book-letters %)) books)]
    (zipmap books (reductions + 0 (butlast lengths)))))

(defn context-at [s i radius]
  (let [start (max 0 (- i radius))
        end   (min (:n s) (+ i radius 1))]
    (apply str (map #(coords/letter-at s %) (range start end)))))

(defn els-letters-at
  "Extract k letters starting at position i with given skip in the full stream."
  [s i skip k]
  (let [positions (map #(+ i (* % skip)) (range k))
        valid? (every? #(and (>= % 0) (< % (:n s))) positions)]
    (when valid?
      (apply str (map #(coords/letter-at s %) positions)))))

(defn describe-position
  "Full description of a position."
  [s i]
  (let [coord (coords/idx->coord i)
        v     (coords/verse-at s i)]
    {:position i
     :coord    (vec coord)
     :letter   (str (coords/letter-at s i))
     :gv       (coords/gv-at s i)
     :verse    (verse-label v)
     :context  (context-at s i 30)}))

;; ── Milemarker discovery ─────────────────────────────────

(defn find-milemarkers
  "Find all classic ELS milemarkers in the WLC Torah stream."
  [s]
  (let [offsets (book-offset)
        results (atom [])]
    ;; Genesis: תורה at skip +50
    (let [letters (vec (oshb/book-letters "Genesis"))]
      (doseq [hit (els/search letters "תורה" 50)]
        (swap! results conj {:book "Genesis" :word "תורה" :skip 50
                             :global-start (+ (offsets "Genesis") (:start hit))})))
    ;; Exodus: תורה at skip +50
    (let [letters (vec (oshb/book-letters "Exodus"))]
      (doseq [hit (els/search letters "תורה" 50)]
        (swap! results conj {:book "Exodus" :word "תורה" :skip 50
                             :global-start (+ (offsets "Exodus") (:start hit))})))
    ;; Leviticus: יהוה at skip +7 and -7
    (let [letters (vec (oshb/book-letters "Leviticus"))]
      (doseq [hit (els/search letters "יהוה" 7)]
        (swap! results conj {:book "Leviticus" :word "יהוה" :skip 7
                             :global-start (+ (offsets "Leviticus") (:start hit))}))
      (doseq [hit (els/search letters "יהוה" -7)]
        (swap! results conj {:book "Leviticus" :word "יהוה" :skip -7
                             :global-start (+ (offsets "Leviticus") (:start hit))})))
    ;; Numbers: הרות at skip +50
    (let [letters (vec (oshb/book-letters "Numbers"))]
      (doseq [hit (els/search letters "הרות" 50)]
        (swap! results conj {:book "Numbers" :word "הרות" :skip 50
                             :global-start (+ (offsets "Numbers") (:start hit))})))
    ;; Deuteronomy: הרות at skip +50
    (let [letters (vec (oshb/book-letters "Deuteronomy"))]
      (doseq [hit (els/search letters "הרות" 50)]
        (swap! results conj {:book "Deuteronomy" :word "הרות" :skip 50
                             :global-start (+ (offsets "Deuteronomy") (:start hit))})))
    @results))

;; ── Mirror analysis ──────────────────────────────────────

(defn analyze-mirror
  "For a milemarker, analyze what's at its mirror under a given fold."
  [s marker mirror-fn fold-key]
  (let [i      (:global-start marker)
        skip   (:skip marker)
        word   (:word marker)
        ;; Original ELS positions
        els-positions (mapv #(+ i (* % skip)) (range (count word)))
        valid-orig? (every? #(and (>= % 0) (< % (:n s))) els-positions)
        ;; Mirror each ELS position
        mirror-positions (mapv mirror-fn els-positions)
        valid-mirror? (every? #(and (>= % 0) (< % (:n s))) mirror-positions)
        ;; Mirror of the start position
        mirror-start (mirror-fn i)
        mirror-coord (vec (coords/idx->coord mirror-start))
        orig-coord   (vec (coords/idx->coord i))]
    (when (and valid-orig? valid-mirror?)
      (let [mirror-letters (mapv #(coords/letter-at s %) mirror-positions)
            mirror-word    (apply str mirror-letters)
            mirror-verse   (verse-label (coords/verse-at s mirror-start))
            orig-verse     (verse-label (coords/verse-at s i))
            ;; Gematria
            orig-gv   (reduce + (map #(coords/gv-at s %) els-positions))
            mirror-gv (reduce + (map #(coords/gv-at s %) mirror-positions))
            pair-gv   (+ orig-gv mirror-gv)
            ;; Self-mirror?
            self? (= i mirror-start)]
        {:book         (:book marker)
         :word         word
         :skip         skip
         :global-start i
         :orig-coord   orig-coord
         :orig-verse   orig-verse
         :mirror-start mirror-start
         :mirror-coord mirror-coord
         :mirror-verse mirror-verse
         :mirror-word  mirror-word
         :mirror-context (context-at s mirror-start 30)
         :orig-gv      orig-gv
         :mirror-gv    mirror-gv
         :pair-gv      pair-gv
         :pair-div-7   (zero? (mod pair-gv 7))
         :pair-div-13  (zero? (mod pair-gv 13))
         :pair-div-67  (zero? (mod pair-gv 67))
         :self-mirror? self?}))))

(defn check-els-at-mirror
  "Check if any interesting ELS words appear at the mirror position."
  [s mirror-pos]
  (let [targets ["תורה" "הרות" "יהוה" "הוהי"]
        skips   [1 -1 7 -7 13 -13 50 -50 67 -67]]
    (for [target targets
          skip   skips
          :let [word (els-letters-at s mirror-pos skip (count target))]
          :when (= word target)]
      {:word target :skip skip :at mirror-pos})))

;; ── Printing ─────────────────────────────────────────────

(defn print-fold-analysis
  "Print detailed analysis for one fold across all milemarkers."
  [s markers fold-key]
  (let [{:keys [name mirror-fn center]} (get fold-fns fold-key)]
    (println (str "\n" (apply str (repeat 70 "=")) "\n"))
    (println (format "  %s" (str/upper-case name)))
    (when center
      (println (format "  Crease center: %s=%d (self-mirrors)" (clojure.core/name fold-key) center)))
    (when (nil? center)
      (println "  Even dimension — no exact center (crease pair)"))
    (println (apply str (repeat 70 "=")))
    (println)

    (let [analyses (keep #(analyze-mirror s % mirror-fn fold-key)
                         (sort-by :global-start markers))
          self-mirrors  (filter :self-mirror? analyses)
          cross-mirrors (remove :self-mirror? analyses)
          div7-count  (count (filter :pair-div-7 analyses))
          div13-count (count (filter :pair-div-13 analyses))
          div67-count (count (filter :pair-div-67 analyses))]

      ;; ── Detailed per-milemarker ──
      (doseq [a analyses]
        (println (format "  %s %s@%+d  pos %,d  coord %s"
                         (:book a) (:word a) (:skip a) (:global-start a) (:orig-coord a)))
        (println (format "    Verse: %s" (:orig-verse a)))
        (if (:self-mirror? a)
          (do
            (println (format "    *** SELF-MIRROR ***  (on crease)"))
            (println (format "    Pair GV: %d + %d = %d (doubled)" (:orig-gv a) (:mirror-gv a) (:pair-gv a))))
          (do
            (println (format "    Mirror → pos %,d  coord %s" (:mirror-start a) (:mirror-coord a)))
            (println (format "    Mirror verse: %s" (:mirror-verse a)))
            (println (format "    Mirror word (same skip positions mirrored): %s" (:mirror-word a)))
            (println (format "    Mirror context: ...%s..." (:mirror-context a)))))
        (let [divs (cond-> ""
                     (:pair-div-7 a)  (str " div7")
                     (:pair-div-13 a) (str " div13")
                     (:pair-div-67 a) (str " div67"))]
          (println (format "    Pair GV: %d%s" (:pair-gv a) divs)))

        ;; Check ELS at mirror
        (let [els-hits (check-els-at-mirror s (:mirror-start a))]
          (when (seq els-hits)
            (println (format "    *** ELS FOUND AT MIRROR: %s ***"
                             (str/join ", " (map #(format "%s@%+d" (:word %) (:skip %)) els-hits))))))
        (println))

      ;; ── Summary table ──
      (println (apply str (repeat 70 "-")))
      (println (format "  SUMMARY: %s" name))
      (println (apply str (repeat 70 "-")))
      (println)
      (println (format "  %-14s %-6s %5s  %-22s  ↔  %-22s  %6s  %-10s"
                       "Book" "Word" "Skip" "Verse" "Mirror Verse" "Pair" "Div"))
      (println (apply str (repeat 110 "-")))

      (doseq [a analyses]
        (let [divs (cond-> ""
                     (:pair-div-7 a)  (str "7 ")
                     (:pair-div-13 a) (str "13 ")
                     (:pair-div-67 a) (str "67"))]
          (println (format "  %-14s %-6s %+5d  %-22s  ↔  %-22s  %6d  %-10s%s"
                           (:book a) (:word a) (:skip a)
                           (:orig-verse a)
                           (if (:self-mirror? a) "** SELF **" (:mirror-verse a))
                           (:pair-gv a)
                           divs
                           (if (:self-mirror? a) " [crease]" "")))))
      (println)

      ;; ── Statistics ──
      (println (format "  Self-mirrors:      %d of %d" (count self-mirrors) (count analyses)))
      (println (format "  Pair GV div by 7:  %d of %d (%.1f%%, expect ~14.3%%)"
                       div7-count (count analyses) (* 100.0 (/ div7-count (max 1 (count analyses))))))
      (println (format "  Pair GV div by 13: %d of %d (%.1f%%, expect ~7.7%%)"
                       div13-count (count analyses) (* 100.0 (/ div13-count (max 1 (count analyses))))))
      (println (format "  Pair GV div by 67: %d of %d (%.1f%%, expect ~1.5%%)"
                       div67-count (count analyses) (* 100.0 (/ div67-count (max 1 (count analyses))))))
      (println)

      ;; ── ELS hits summary ──
      (let [all-els (mapcat (fn [a] (map #(assoc % :source-verse (:orig-verse a)
                                                    :source-word (:word a))
                                         (check-els-at-mirror s (:mirror-start a))))
                            analyses)]
        (when (seq all-els)
          (println "  *** ELS WORDS FOUND AT MIRROR POSITIONS ***")
          (doseq [hit all-els]
            (println (format "    %s@%+d at mirror of %s %s (pos %d)"
                             (:word hit) (:skip hit) (:source-word hit) (:source-verse hit) (:at hit))))
          (println)))

      ;; ── Thematic analysis ──
      (println (format "  Thematic clustering:"))
      (let [mirror-books (frequencies (map #(:book (coords/verse-at s (:mirror-start %))) cross-mirrors))]
        (println (format "    Mirror positions land in: %s"
                         (str/join ", " (map (fn [[b c]] (format "%s(%d)" b c)) (sort-by val > mirror-books))))))

      ;; Return analyses for cross-fold comparison
      analyses)))

;; ── Cross-fold comparison ────────────────────────────────

(defn print-cross-fold-comparison
  "Compare all three folds side by side."
  [s markers b-results c-results d-results]
  (println "\n" (apply str (repeat 70 "=")) "\n")
  (println "  CROSS-FOLD COMPARISON")
  (println (apply str (repeat 70 "=")))
  (println)

  ;; Find milemarkers where the same mirror position appears under multiple folds
  (println "── Do any mirror positions coincide across folds? ──")
  (let [b-mirrors (set (map :mirror-start (remove :self-mirror? b-results)))
        c-mirrors (set (map :mirror-start (remove :self-mirror? c-results)))
        d-mirrors (set (map :mirror-start (remove :self-mirror? d-results)))
        bc (clojure.set/intersection b-mirrors c-mirrors)
        bd (clojure.set/intersection b-mirrors d-mirrors)
        cd (clojure.set/intersection c-mirrors d-mirrors)
        bcd (clojure.set/intersection b-mirrors c-mirrors d-mirrors)]
    (println (format "  b ∩ c: %d shared mirror positions" (count bc)))
    (println (format "  b ∩ d: %d shared mirror positions" (count bd)))
    (println (format "  c ∩ d: %d shared mirror positions" (count cd)))
    (println (format "  b ∩ c ∩ d: %d shared mirror positions" (count bcd)))
    (when (seq bc) (println (format "    b∩c positions: %s" (str/join ", " (sort bc)))))
    (when (seq bd) (println (format "    b∩d positions: %s" (str/join ", " (sort bd)))))
    (when (seq cd) (println (format "    c∩d positions: %s" (str/join ", " (sort cd)))))
    (when (seq bcd) (println (format "    b∩c∩d positions: %s" (str/join ", " (sort bcd))))))
  (println)

  ;; Compare self-mirror counts
  (println "── Self-mirror counts ──")
  (println (format "  b-fold: %d self-mirrors (even dim, expect 0)"
                   (count (filter :self-mirror? b-results))))
  (println (format "  c-fold: %d self-mirrors (c=6 is center)"
                   (count (filter :self-mirror? c-results))))
  (println (format "  d-fold: %d self-mirrors (d=33 is center)"
                   (count (filter :self-mirror? d-results))))
  (println)

  ;; ELS hit comparison
  (println "── ELS hits at mirror positions ──")
  (doseq [[label results] [["b-fold" b-results] ["c-fold" c-results] ["d-fold" d-results]]]
    (let [hits (mapcat (fn [a] (check-els-at-mirror s (:mirror-start a))) results)]
      (println (format "  %s: %d ELS hits" label (count hits)))))
  (println)

  ;; Pair GV divisibility comparison
  (println "── Pair GV divisibility rates ──")
  (println (format "  %-10s  ÷7        ÷13       ÷67" "Fold"))
  (println (apply str (repeat 50 "-")))
  (doseq [[label results] [["b-fold" b-results] ["c-fold" c-results] ["d-fold" d-results]]]
    (let [n (count results)
          d7 (count (filter :pair-div-7 results))
          d13 (count (filter :pair-div-13 results))
          d67 (count (filter :pair-div-67 results))]
      (println (format "  %-10s  %d/%-2d %4.0f%%  %d/%-2d %4.0f%%  %d/%-2d %4.0f%%"
                       label
                       d7 n (* 100.0 (/ d7 (max 1 n)))
                       d13 n (* 100.0 (/ d13 (max 1 n)))
                       d67 n (* 100.0 (/ d67 (max 1 n)))))))
  (println (format "  %-10s  %-9s %-9s %-9s" "Expected" "~14.3%" "~7.7%" "~1.5%"))
  (println)

  ;; Notable pairs across all folds
  (println "── Most notable mirror pairs ──")
  (println)
  (doseq [[label results fold-desc] [["b-fold" b-results "jubilee"]
                                      ["c-fold" c-results "love/unity"]
                                      ["d-fold" d-results "understanding"]]]
    ;; Find pairs where pair-gv divides by 7 and 13 (or 67)
    (let [notable (filter #(or (and (:pair-div-7 %) (:pair-div-13 %))
                               (:pair-div-67 %)
                               (:self-mirror? %))
                          results)]
      (when (seq notable)
        (println (format "  %s (%s):" label fold-desc))
        (doseq [a notable]
          (let [divs (cond-> ""
                       (:pair-div-7 a)  (str "7*")
                       (:pair-div-13 a) (str "13*")
                       (:pair-div-67 a) (str "67*"))]
            (println (format "    %s %s@%+d  %s ↔ %s  pair=%d (%s)%s"
                             (:book a) (:word a) (:skip a)
                             (:orig-verse a)
                             (if (:self-mirror? a) "SELF" (:mirror-verse a))
                             (:pair-gv a) divs
                             (if (:self-mirror? a) " [crease]" "")))))
        (println)))))

;; ── Main ─────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 063: FOLD MILEMARKERS")
  (println "  b-fold (50 jubilees), c-fold (13 loves), d-fold (67 understandings)")
  (println "  What sits at each milemarker's mirror under each fold?")
  (println "================================================================")
  (println)

  (let [s       (coords/space)
        markers (find-milemarkers s)]

    (println (format "Found %d milemarkers.\n" (count markers)))

    ;; Show milemarker inventory
    (println "── Milemarker inventory ──")
    (doseq [[book book-markers] (sort-by key (group-by :book markers))]
      (println (format "  %-13s  %d markers: %s"
                       book (count book-markers)
                       (str/join ", " (map #(format "%s@%+d(pos %d)" (:word %) (:skip %) (:global-start %))
                                           (sort-by :global-start book-markers))))))
    (println)

    ;; Analyze each fold
    (let [b-results (print-fold-analysis s markers :b)
          c-results (print-fold-analysis s markers :c)
          d-results (print-fold-analysis s markers :d)]

      ;; Cross-fold comparison
      (print-cross-fold-comparison s markers b-results c-results d-results))

    (println "\nDone.")))
