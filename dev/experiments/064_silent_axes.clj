(ns experiments.064-silent-axes
  "Do the silent axis words speak through ELS?

   304,850 = 7 × 50 × 13 × 67

   The classic pattern: תורה at skip 50 (b-axis), יהוה at skip 7 (a-axis).
   Two axes have voices. Two are silent.

   אהבה (love, gv=13) and בינה (understanding, gv=67) never appear as
   WORDS anywhere in the Torah (confirmed in experiment 061b). They are
   the two axes whose concepts the text embodies but never speaks.

   The question: do they speak through ELS — the transposition cipher?
   If the machine encodes תורה at skip 50 and יהוה at skip 7, does it
   also encode אהבה at skip 13 and בינה at skip 67?

   Axis-word ELS would mean every axis of the 4D space has a voice:
     a-axis (7):  יהוה at skip 7   — the Name
     b-axis (50): תורה at skip 50  — the Instruction
     c-axis (13): אהבה at skip 13  — Love
     d-axis (67): בינה at skip 67  — Understanding

   FINDINGS
   ========
   1. THE FOUR VOICES — axis word at axis skip:
        a-axis: יהוה at skip 7   — 35 hits (expected 26.8, ratio 1.31)  ELEVATED
        b-axis: תורה at skip 50  — 21 hits (expected 9.8,  ratio 2.14)  STRONGLY ELEVATED
        c-axis: אהבה at skip 13  — 11 hits (expected 12.3, ratio 0.90)  at expectation
        d-axis: בינה at skip 67  —  7 hits (expected 5.0,  ratio 1.39)  slightly elevated

      The two classic voices (YHWH, Torah) exceed expectation significantly.
      The two silent voices (love, understanding) are present but not elevated
      above chance. The silent axes remain silent in ELS density too.

   2. THE CONSECUTIVE SURPRISE — אהבה at skip 1:
      15 hits (expected 12.3, ratio 1.22). Love DOES appear as consecutive
      letters across word boundaries. Most are in Deuteronomy as 'לאהבה'
      ('to love') — the הבה ending of one morpheme + the א beginning of
      the next creates the pattern. The word the Torah 'cannot say' it
      actually spells constantly in the act of commanding love.

      בינה at skip 1: only 1 hit (Genesis 37:7, 'תסבינה' = 'surrounded').
      Understanding remains rare even consecutively.

   3. THE REVERSE CONSECUTIVE — אהבה at skip -1:
      11 hits. Almost all in Deuteronomy. The same 'לאהבה' pattern read
      backwards. Love speaks forward AND backward.

   4. CROSS-AXIS: יהוה at skip 67 = 34 hits (expected 26.8, ratio 1.27).
      The Name appears more than expected on the understanding axis.
      At skip 13 (love), the Name hits 25 (expected 26.8, ratio 0.93) — normal.
      The Name resonates with understanding more than with love in ELS.

   5. COORDINATE CLUSTERING:
      - love@13: peaks at a=1,3,6 (1.91x). Three self-mirroring hits in a=3
        (center seventh, Leviticus). c=6 peak at 3.55x.
      - understanding@67: 4 of 7 hits land at c=10 (7.43x expected).
        Extraordinary concentration on a single love-axis coordinate.
      - one@13: peaks at a=0 (Genesis, 2.06x).

   6. SELF-MIRRORS: Three אהבה@13 hits in a=3 (Leviticus) mirror to themselves.
      Love at skip 13 in the center seventh is invariant under the a-fold.

   7. NOTABLE MIRROR PAIRS:
      - אהבה@13 at Deut 26:2 (coord [6 31 12 28]) mirrors to Gen 22:14
        — 'The LORD will provide' on Mount Moriah, the Binding of Isaac.
      - אהבה@13 at Deut 13:4 (coord [6 13 3 3]) — the verse ABOUT loving God
        — mirrors to Gen 9:28, after the flood.
      - בינה@67 at Gen 7:6 mirrors to Deut 11:16 — 'lest your heart be deceived'.
      - בינה@67 at Num 21:5 mirrors to Gen 34:7 (same b,c coordinates).

   8. THE HONEST RESULT: The silent axes do not produce the dramatic statistical
      elevation that the classic axes do. Torah@50 at 2.14x and YHWH@7 at 1.31x
      are clearly above noise. Love@13 at 0.90x and Understanding@67 at 1.39x
      are within normal fluctuation for their sample sizes.

      The silence is itself meaningful: the structure embodies love and
      understanding without encoding them as ELS signals. The machine
      speaks Torah and the Name; it IS love and understanding.
  "
  (:require [selah.els.engine :as els]
            [selah.els.stats :as stats]
            [selah.space.coords :as coords]
            [selah.gematria :as gem]))

;; ── Helpers ──────────────────────────────────────────────

(defn build-torah-vec
  "Build the full Torah as a vector of chars for ELS search."
  [s]
  (mapv #(coords/letter-at s %) (range (:n s))))

(defn context-at
  "Get text context around position i."
  [s i radius]
  (let [start (max 0 (- i radius))
        end   (min (:n s) (+ i radius 1))]
    (apply str (map #(coords/letter-at s %) (range start end)))))

(defn describe-hit
  "Full description of an ELS hit in the Torah stream."
  [s hit]
  (let [i     (:start hit)
        coord (vec (coords/idx->coord i))
        v     (coords/verse-at s i)
        skip  (:skip hit)
        k     (count (:word hit))
        ;; Positions of all letters in this ELS
        positions (map #(+ i (* % skip)) (range k))
        ;; Ending position
        end-pos (last positions)
        end-v   (when (and (>= end-pos 0) (< end-pos (:n s)))
                  (coords/verse-at s end-pos))
        end-coord (when (and (>= end-pos 0) (< end-pos (:n s)))
                    (vec (coords/idx->coord end-pos)))]
    {:word       (:word hit)
     :skip       skip
     :start      i
     :coord      coord
     :a          (nth coord 0)
     :b          (nth coord 1)
     :c          (nth coord 2)
     :d          (nth coord 3)
     :verse      (str (:book v) " " (:ch v) ":" (:vs v))
     :end-pos    end-pos
     :end-coord  end-coord
     :end-verse  (when end-v (str (:book end-v) " " (:ch end-v) ":" (:vs end-v)))
     :context    (context-at s i 25)}))

(defn a-mirror
  "Mirror position i across the a-axis: (a,b,c,d) -> (6-a,b,c,d)."
  [i]
  (let [c (coords/idx->coord i)
        a (aget c 0)]
    (coords/coord->idx (- 6 a) (aget c 1) (aget c 2) (aget c 3))))

;; ── Search engine ────────────────────────────────────────

(defn search-word-at-skip
  "Search the full Torah for a word at a specific skip.
   Returns vector of described hits."
  [s torah-vec word skip]
  (let [raw-hits (els/search torah-vec word skip)]
    (mapv #(describe-hit s %) raw-hits)))

(defn print-hits
  "Print hits for a word/skip combination."
  [hits & {:keys [max-show] :or {max-show 15}}]
  (let [n (count hits)]
    (println (format "    %d hit%s" n (if (= n 1) "" "s")))
    (when (pos? n)
      (doseq [h (take max-show hits)]
        (println (format "      pos=%6d  %s  coord=%s  %s"
                         (:start h) (:verse h) (:coord h)
                         (subs (:context h) 0 (min 30 (count (:context h)))))))
      (when (> n max-show)
        (println (format "      ... and %d more" (- n max-show)))))))

(defn expected-count
  "Expected number of hits for a word at a given skip in the full Torah,
   based on letter frequencies."
  [torah-vec word skip]
  (stats/expected-hits torah-vec word skip))

;; ── The four voices ──────────────────────────────────────

(def search-plan
  "What to search for and at which skips."
  [;; The silent c-axis: love
   {:word "אהבה" :label "AHAVAH (love, gv=13)"
    :skips [13 -13 7 50 67 -67 26 39]}

   ;; The silent d-axis: understanding
   {:word "בינה" :label "BINAH (understanding, gv=67)"
    :skips [67 -67 7 13 50 -50 134]}

   ;; One (gv=13, same as love)
   {:word "אחד" :label "ECHAD (one, gv=13)"
    :skips [13 -13 7 50 67]}

   ;; Does Torah speak on the silent axes?
   {:word "תורה" :label "TORAH"
    :skips [13 67]}

   ;; Does the Name speak on the silent axes?
   {:word "יהוה" :label "YHWH"
    :skips [13 67]}])

;; ── Main ─────────────────────────────────────────────────

(defn -main []
  (println "═══════════════════════════════════════════════════════════")
  (println "  064: THE SILENT AXES — do love and understanding speak")
  (println "       through equidistant letter spacing?")
  (println)
  (println "  304,850 = 7 × 50 × 13 × 67")
  (println "  a-axis (7):  יהוה at skip 7   — the Name       ✓")
  (println "  b-axis (50): תורה at skip 50  — the Instruction ✓")
  (println "  c-axis (13): אהבה at skip 13  — Love            ?")
  (println "  d-axis (67): בינה at skip 67  — Understanding   ?")
  (println "═══════════════════════════════════════════════════════════\n")

  (println "Building space and letter vector...")
  (let [s         (coords/space)
        torah-vec (build-torah-vec s)
        n         (count torah-vec)
        _         (println (format "  %,d letters loaded.\n" n))

        ;; Accumulate all results for summary
        all-results (atom {})]

    ;; ══════════════════════════════════════════════════════════
    ;; PART 1: Systematic search
    ;; ══════════════════════════════════════════════════════════

    (println "══════════════════════════════════════════════════════")
    (println "  PART 1: SYSTEMATIC ELS SEARCH")
    (println "══════════════════════════════════════════════════════\n")

    (doseq [{:keys [word label skips]} search-plan]
      (println (format "── %s ──" label))
      (doseq [skip skips]
        (let [hits (search-word-at-skip s torah-vec word skip)
              exp  (expected-count torah-vec word skip)]
          (swap! all-results assoc [word skip]
                 {:hits hits :count (count hits) :expected exp})
          (println (format "  skip %+4d:  found=%d  expected=%.1f  ratio=%.2f"
                           skip (count hits) exp
                           (if (pos? exp) (/ (count hits) exp) 0.0)))
          (print-hits hits :max-show 5)))
      (println))

    ;; ══════════════════════════════════════════════════════════
    ;; PART 2: Consecutive letters (skip 1) — love across word boundaries
    ;; ══════════════════════════════════════════════════════════

    (println "══════════════════════════════════════════════════════")
    (println "  PART 2: CONSECUTIVE LETTERS (skip 1)")
    (println "  Do the silent words appear across word boundaries?")
    (println "══════════════════════════════════════════════════════\n")

    (doseq [word ["אהבה" "בינה"]]
      (let [hits (search-word-at-skip s torah-vec word 1)
            exp  (expected-count torah-vec word 1)]
        (swap! all-results assoc [word 1]
               {:hits hits :count (count hits) :expected exp})
        (println (format "  %s at skip 1 (consecutive): found=%d  expected=%.1f  ratio=%.2f"
                         word (count hits) exp
                         (if (pos? exp) (/ (count hits) exp) 0.0)))
        (print-hits hits :max-show 10))
      (println))

    ;; Also check skip -1 (reverse consecutive)
    (doseq [word ["אהבה" "בינה"]]
      (let [hits (search-word-at-skip s torah-vec word -1)]
        (swap! all-results assoc [word -1]
               {:hits hits :count (count hits) :expected 0})
        (println (format "  %s at skip -1 (reverse consecutive): found=%d"
                         word (count hits)))
        (print-hits hits :max-show 10))
      (println))

    ;; ══════════════════════════════════════════════════════════
    ;; PART 3: The four voices — axis word at axis skip
    ;; ══════════════════════════════════════════════════════════

    (println "══════════════════════════════════════════════════════")
    (println "  PART 3: THE FOUR VOICES — axis word at axis skip")
    (println "══════════════════════════════════════════════════════\n")

    (let [voices [["יהוה" 7  "a-axis: the Name at skip 7"]
                  ["תורה" 50 "b-axis: Torah at skip 50"]
                  ["אהבה" 13 "c-axis: love at skip 13"]
                  ["בינה" 67 "d-axis: understanding at skip 67"]]]
      (doseq [[word skip label] voices]
        (let [r (get @all-results [word skip]
                     ;; May need to run if not yet in cache (for torah/yhwh at 50/7)
                     (let [hits (search-word-at-skip s torah-vec word skip)
                           exp  (expected-count torah-vec word skip)]
                       (swap! all-results assoc [word skip]
                              {:hits hits :count (count hits) :expected exp})
                       {:hits hits :count (count hits) :expected exp}))]
          (println (format "  %-45s  hits=%d  expected=%.1f  ratio=%.2f"
                           label (:count r) (:expected r)
                           (if (pos? (:expected r))
                             (/ (:count r) (:expected r))
                             0.0))))))

    ;; ══════════════════════════════════════════════════════════
    ;; PART 4: Interesting hits — detailed analysis
    ;; ══════════════════════════════════════════════════════════

    (println "\n══════════════════════════════════════════════════════")
    (println "  PART 4: DETAILED ANALYSIS OF NOTABLE HITS")
    (println "══════════════════════════════════════════════════════\n")

    ;; Analyze אהבה at skip 13 hits in detail
    (let [love-13 (get @all-results ["אהבה" 13])]
      (when (pos? (:count love-13))
        (println "── אהבה (love) at skip 13 — the c-axis voice ──\n")
        (doseq [h (:hits love-13)]
          (let [mirror-pos (a-mirror (:start h))
                mirror-v   (coords/verse-at s mirror-pos)
                mirror-coord (vec (coords/idx->coord mirror-pos))]
            (println (format "  Position %d → %s" (:start h) (:verse h)))
            (println (format "    Coord: %s" (:coord h)))
            (println (format "    Context: ...%s..." (:context h)))
            (println (format "    End: pos=%d → %s  coord=%s"
                             (:end-pos h) (:end-verse h) (:end-coord h)))
            (println (format "    a-mirror: pos=%d → %s %d:%d  coord=%s"
                             mirror-pos (:book mirror-v) (:ch mirror-v) (:vs mirror-v)
                             mirror-coord))
            (println)))))

    ;; Analyze בינה at skip 67 hits in detail
    (let [binah-67 (get @all-results ["בינה" 67])]
      (when (pos? (:count binah-67))
        (println "── בינה (understanding) at skip 67 — the d-axis voice ──\n")
        (doseq [h (:hits binah-67)]
          (let [mirror-pos (a-mirror (:start h))
                mirror-v   (coords/verse-at s mirror-pos)
                mirror-coord (vec (coords/idx->coord mirror-pos))]
            (println (format "  Position %d → %s" (:start h) (:verse h)))
            (println (format "    Coord: %s" (:coord h)))
            (println (format "    Context: ...%s..." (:context h)))
            (println (format "    End: pos=%d → %s  coord=%s"
                             (:end-pos h) (:end-verse h) (:end-coord h)))
            (println (format "    a-mirror: pos=%d → %s %d:%d  coord=%s"
                             mirror-pos (:book mirror-v) (:ch mirror-v) (:vs mirror-v)
                             mirror-coord))
            (println)))))

    ;; ══════════════════════════════════════════════════════════
    ;; PART 5: Cross-axis presence — do axis words appear on OTHER axes?
    ;; ══════════════════════════════════════════════════════════

    (println "══════════════════════════════════════════════════════")
    (println "  PART 5: CROSS-AXIS PRESENCE")
    (println "  Does each axis word appear at OTHER axis skips?")
    (println "══════════════════════════════════════════════════════\n")

    (let [cross-checks [["אהבה" 7   "love at skip 7 (completeness axis)"]
                        ["אהבה" 50  "love at skip 50 (jubilee axis)"]
                        ["אהבה" 67  "love at skip 67 (understanding axis)"]
                        ["בינה" 7   "understanding at skip 7 (completeness axis)"]
                        ["בינה" 13  "understanding at skip 13 (love axis)"]
                        ["בינה" 50  "understanding at skip 50 (jubilee axis)"]
                        ["יהוה" 13  "the Name at skip 13 (love axis)"]
                        ["יהוה" 67  "the Name at skip 67 (understanding axis)"]
                        ["תורה" 13  "Torah at skip 13 (love axis)"]
                        ["תורה" 67  "Torah at skip 67 (understanding axis)"]]]
      (println (format "  %-50s  %6s  %8s  %6s" "Search" "Hits" "Expected" "Ratio"))
      (println (apply str (repeat 80 "─")))
      (doseq [[word skip label] cross-checks]
        (let [r (get @all-results [word skip])]
          (when r
            (println (format "  %-50s  %6d  %8.1f  %6.2f"
                             label (:count r) (:expected r)
                             (if (pos? (:expected r))
                               (/ (:count r) (:expected r))
                               0.0)))))))

    ;; ══════════════════════════════════════════════════════════
    ;; PART 6: Coordinate analysis of axis-word ELS hits
    ;; ══════════════════════════════════════════════════════════

    (println "\n══════════════════════════════════════════════════════")
    (println "  PART 6: WHERE DO THE AXIS-WORD ELS HITS LAND?")
    (println "  Coordinate distributions of the primary voice hits")
    (println "══════════════════════════════════════════════════════\n")

    (doseq [[word skip label] [["אהבה" 13 "love@13"]
                                ["בינה" 67 "understanding@67"]
                                ["אחד"  13 "one@13"]]]
      (let [r (get @all-results [word skip])]
        (when (and r (pos? (:count r)))
          (let [hits (:hits r)]
            (println (format "  %s — %d hits, a-axis distribution:" label (count hits)))
            (let [a-freq (frequencies (map :a hits))
                  expected (/ (count hits) 7.0)]
              (doseq [a (range 7)]
                (let [cnt (get a-freq a 0)]
                  (println (format "    a=%d: %3d  (%.2f×)" a cnt
                                   (if (pos? expected) (/ cnt expected) 0.0))))))
            (println (format "  %s — c-axis distribution:" label))
            (let [c-freq (frequencies (map :c hits))
                  expected (/ (count hits) 13.0)]
              (doseq [c (range 13)]
                (let [cnt (get c-freq c 0)]
                  (println (format "    c=%2d: %3d  (%.2f×)" c cnt
                                   (if (pos? expected) (/ cnt expected) 0.0))))))
            (println)))))

    ;; ══════════════════════════════════════════════════════════
    ;; SUMMARY
    ;; ══════════════════════════════════════════════════════════

    (println "══════════════════════════════════════════════════════")
    (println "  SUMMARY TABLE — all axis-word × axis-skip searches")
    (println "══════════════════════════════════════════════════════\n")

    (println (format "  %-8s  %4s  %6s  %8s  %6s  %s"
                     "Word" "Skip" "Hits" "Expected" "Ratio" "Note"))
    (println (apply str (repeat 70 "─")))

    (doseq [[word skip] (sort-by (fn [[w s]] [(str w) s]) (keys @all-results))]
      (let [{:keys [count expected]} (get @all-results [word skip])
            exp-d (double expected)
            ratio (if (pos? exp-d) (/ count exp-d) 0.0)
            abs-skip (if (neg? skip) (- skip) skip)
            note (cond
                   (and (= word "אהבה") (= skip 13))  "<-- c-axis voice"
                   (and (= word "בינה") (= skip 67))  "<-- d-axis voice"
                   (and (= word "יהוה") (= abs-skip 7))  "<-- a-axis voice (classic)"
                   (and (= word "תורה") (= abs-skip 50)) "<-- b-axis voice (classic)"
                   (> ratio 2.0)                        "<-- elevated"
                   :else                                "")]
        (println (format "  %-8s  %+4d  %6d  %8.1f  %6.2f  %s"
                         word (int skip) (int count) exp-d ratio note))))

    (println "\nDone.")))
