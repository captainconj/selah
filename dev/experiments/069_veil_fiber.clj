(ns experiments.069-veil-fiber
  "The Veil Fiber — Exodus 34:29-35 in the 4D space.

   Moses descends from Sinai with the tablets, his face shining.
   The people are afraid. He puts a VEIL (מסוה) over his face.
   He removes the veil when he speaks to God, replaces it when
   he speaks to the people.

   This passage sits at the a-fold crease — the boundary between
   the 3rd and 4th sevenths (a=2 → a=3). The center of the space
   (Lev 8:35) says 'guard the charge... seven days.'

   If the structure is a machine, and the center is its heart,
   then the veil is the interface — the membrane you pass through
   to reach the center. What do the fibers through the veil say?

   FINDINGS:

   1. THE CROSSING — Exodus 34:29-35 is 387 letters (pos 130,601–130,987).
      It crosses the a=2→a=3 boundary at position 130,650.
      49 letters in a=2 (outside), 338 letters in a=3 (the center seventh).
      Moses descends the mountain; the text descends into the center.

   2. SEVEN FIBERS — The passage spans EXACTLY 7 d-fibers (understanding lines):
      1 fiber in a=2 (the threshold) + 6 fibers in a=3 (the center seventh).
      The veil passage occupies seven fibers of understanding.

   3. THE MIDPOINT — Position 130,794, coordinate (3, 0, 2, 10).
      Letter: ש (shin, value 300, fire). a=3: CENTER of the seven divisions.
      b=0: the ORIGIN of the jubilee axis. The midpoint sits at the a-center
      but at the very start of the jubilee cycle — the threshold of the center.

   4. THE d-FIBER (67 letters through midpoint) — Exodus 34:32–33.
      'All the children of Israel drew near... he commanded them all that
       YHWH spoke to him at Mount Sinai... Moses finished SPEAKING to them...
       and he PUT upon his FACE [the veil].'
      Contains: יהוה (YHWH), דבר (speak) ×2, משה (Moses), את (aleph-tav) ×3.
      The fiber ends with ויתן על פניו — 'and he put upon his face.'
      The veil goes ON at the end of this fiber.

   5. קרן (SHINING) = 350 = ספיר (SAPPHIRE).
      Three occurrences of קרן in the passage, all with gematria 350.
      304,850 = 350 × 871 = shining × (love × understanding).
      The sapphire tablets. The shining face. Same number.
      The total letter count of the Torah factors through the shining.

   6. מסוה (VEIL) = 111 = אלף (aleph spelled out).
      Three occurrences of the word 'veil' in the passage:
        1st: coord [3,0,3,0..3] — starts at d=0, the BOUNDARY of understanding
        2nd: coord [3,0,3,33..36] — starts at d=33, the CENTER of understanding
        3rd: coord [3,0,4,48..51] — past center
      The 2nd veil starts at d=33, the midpoint of the understanding axis —
      exactly like the ח of the sword (החרב) in experiment 056.
      The veil's gematria (111) equals aleph spelled out: the silent letter,
      the invisible membrane. The veil IS the aleph.

   7. THE a-FIBER (7 letters through midpoint):
      a=0: ה in Genesis 1:4 — 'God saw the LIGHT that it was GOOD,
           and God SEPARATED between the light and the darkness.'
           The first separation. The first veil.
      a=3: ש (shin/fire) in Exodus 34:32 — Moses' shining face.
      a=6: א (aleph) in Deuteronomy 6:3 — 'Hear O Israel, GUARD to do.'
           The verse right before the Shema. Contains both שמע and שמר.
      The a-fiber runs from the first separation of light (Gen 1:4)
      through the shining face, to the doorstep of the Shema.
      Gematria: 727 (prime).

   8. SUM OF CENTER + VEIL d-FIBERS = 10,972 = 13 × 844.
      Divisible by 13 (love/unity). The center and the veil are
      bound together through the love axis.
      Difference = 1,168 = 73 × 16. 73 = חכמה (wisdom).
      The gap between center and veil is measured in wisdom."
  (:require [selah.space.coords :as coords]
            [clojure.string :as str]))

;; ── Target words ──────────────────────────────────────────

(def target-words
  {"תורה" "Torah"
   "יהוה" "YHWH"
   "אהבה" "love"
   "בינה" "understanding"
   "שמר"  "guard"
   "אמת"  "truth"
   "אור"  "light"
   "קדש"  "holy"
   "פנה"  "face/turn"
   "כבד"  "glory/heavy"
   "לוח"  "tablet"
   "ברית" "covenant"
   "דבר"  "word/speak"
   "משה"  "Moses"
   "אהרן" "Aaron"
   "ענן"  "cloud"
   "כסה"  "cover"
   "את"   "aleph-tav"
   "אל"   "God"
   "לב"   "heart"
   "שם"   "name"
   "חיים" "life"
   "אחד"  "one"
   "שבע"  "seven"
   "עין"  "eye"
   "קרן"  "shining/horn"
   "מסוה" "veil"
   "עד"   "witness"
   "צוה"  "command"})

(defn search-words
  "Search for all target words in a letter string.
   Returns seq of {:word :offset}."
  [s]
  (let [results (atom [])]
    (doseq [[word _] target-words]
      (loop [start 0]
        (let [idx (.indexOf ^String s ^String word (int start))]
          (when (>= idx 0)
            (swap! results conj {:word word :offset idx})
            (recur (inc idx))))))
    @results))

;; ── Helpers ───────────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn fiber-letters
  "Extract letters from a fiber (int array of positions)."
  [s positions]
  (apply str (map #(coords/letter-at s %) (seq positions))))

(defn fiber-gematria
  "Sum gematria values along a fiber."
  [s positions]
  (reduce + (map #(coords/gv-at s %) (seq positions))))

(defn fiber-report
  "Print a detailed report for a fiber."
  [s positions fiber-name axis-label]
  (let [n (alength ^ints positions)
        letters (fiber-letters s positions)
        gv-sum (fiber-gematria s positions)
        words-found (search-words letters)]
    (println (format "  %s: %d letters" fiber-name n))
    (println (format "  Letters: %s" letters))
    (println (format "  Gematria sum: %,d" gv-sum))
    (let [div7  (zero? (mod gv-sum 7))
          div13 (zero? (mod gv-sum 13))
          div67 (zero? (mod gv-sum 67))
          div50 (zero? (mod gv-sum 50))]
      (when div7  (println (format "    ÷7  = %,d" (/ gv-sum 7))))
      (when div13 (println (format "    ÷13 = %,d" (/ gv-sum 13))))
      (when div50 (println (format "    ÷50 = %,d" (/ gv-sum 50))))
      (when div67 (println (format "    ÷67 = %,d" (/ gv-sum 67)))))
    (println)

    ;; Letter-by-letter with coordinates and verses
    (println (format "  Letter-by-letter (%s):" axis-label))
    (doseq [i (range n)]
      (let [pos (aget ^ints positions i)
            coord (vec (coords/idx->coord pos))
            letter (coords/letter-at s pos)
            gv (coords/gv-at s pos)
            v (coords/verse-at s pos)]
        (println (format "    %s=%2d  %s  gv=%3d  pos=%,7d  %s"
                         axis-label i letter gv pos (verse-label v)))))
    (println)

    ;; Words found
    (if (seq words-found)
      (do
        (println "  Words found:")
        (doseq [{:keys [word offset]} (sort-by :offset words-found)]
          (println (format "    '%s' at offset %d" word offset))))
      (println "  No target words found."))
    (println)))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 069: THE VEIL FIBER")
  (println "  Exodus 34:29-35 — Moses veils his shining face")
  (println "  At the a-fold crease: boundary of the 3rd and 4th sevenths")
  (println "================================================================")
  (println)

  (let [s (coords/space)
        vrefs (:verse-ref s)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: LOCATE EXODUS 34:29-35
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: LOCATING EXODUS 34:29-35")
    (println "================================================================")
    (println)

    (let [target-verses (filter #(and (= "Exodus" (:book %))
                                       (= 34 (:ch %))
                                       (<= 29 (:vs %))
                                       (<= (:vs %) 35))
                                vrefs)
          target-verses (sort-by :vs target-verses)]

      (when (empty? target-verses)
        (println "  ERROR: Could not find Exodus 34:29-35!")
        (System/exit 1))

      (let [passage-start (:start (first target-verses))
            passage-end   (:end (last target-verses))
            passage-len   (- passage-end passage-start)]

        (println (format "  Found %d verses:" (count target-verses)))
        (doseq [v target-verses]
          (let [len (- (:end v) (:start v))
                start-coord (vec (coords/idx->coord (:start v)))
                end-coord (vec (coords/idx->coord (dec (:end v))))]
            (println (format "    %s  pos %,d–%,d  (%d letters)  coord %s → %s"
                             (verse-label v)
                             (:start v) (dec (:end v)) len
                             start-coord end-coord))))
        (println)
        (println (format "  Total passage: %,d letters (pos %,d to %,d)"
                         passage-len passage-start (dec passage-end)))

        ;; Show start/end coordinates
        (let [start-coord (vec (coords/idx->coord passage-start))
              end-coord   (vec (coords/idx->coord (dec passage-end)))]
          (println (format "  Start coord: %s" start-coord))
          (println (format "  End coord:   %s" end-coord)))

        ;; Print the passage text
        (let [passage-text (apply str (map #(coords/letter-at s %) (range passage-start passage-end)))]
          (println)
          (println (format "  Full text (%d letters):" passage-len))
          (println (format "  %s" passage-text))

          ;; Words found in passage
          (let [words (search-words passage-text)]
            (println)
            (println "  Words found in passage:")
            (doseq [{:keys [word meaning offset]} (sort-by :offset words)]
              (println (format "    '%s' (%s) at offset %d" word meaning offset)))))
        (println)

        ;; Check a-boundary crossing
        (println "── A-boundary check: does the passage cross a=2 → a=3? ──")
        (let [a-boundary (* 3 (coords/stride-a))]  ;; start of a=3
          (println (format "  a=3 starts at position %,d (= 3 × %,d)" a-boundary (coords/stride-a)))
          (if (and (< passage-start a-boundary) (>= (dec passage-end) a-boundary))
            (do
              (println (format "  YES — passage spans the boundary!"))
              (println (format "    Before boundary (a=2): %,d letters" (- a-boundary passage-start)))
              (println (format "    After boundary (a=3):  %,d letters" (- passage-end a-boundary))))
            (let [start-a (first (vec (coords/idx->coord passage-start)))
                  end-a   (first (vec (coords/idx->coord (dec passage-end))))]
              (println (format "  Passage is in a=%d to a=%d" start-a end-a))
              (when (= start-a 2)
                (println "  Entirely in the 3rd seventh (a=2) — just before the center")))))
        (println)

        ;; ════════════════════════════════════════════════════════
        ;; PART 2: THE MIDPOINT
        ;; ════════════════════════════════════════════════════════

        (println "================================================================")
        (println "  PART 2: THE MIDPOINT OF THE VEIL PASSAGE")
        (println "================================================================")
        (println)

        (let [midpoint (+ passage-start (quot passage-len 2))
              mid-coord (vec (coords/idx->coord midpoint))
              mid-desc (coords/describe midpoint)
              [ma mb mc md] mid-coord]

          (println (format "  Midpoint position: %,d" midpoint))
          (println (format "  Coordinate: %s  →  (a=%d, b=%d, c=%d, d=%d)" mid-coord ma mb mc md))
          (println (format "  Letter: %s" (:letter mid-desc)))
          (println (format "  Gematria: %d" (:gematria mid-desc)))
          (println (format "  Verse: %s" (:verse mid-desc)))
          (println)

          ;; Check axis midpoints
          (println "── Axis midpoint checks ──")
          (println (format "  a=%d  (center of 0..6 is 3)  %s"
                           ma (if (= ma 3) "*** CENTER of a-axis! ***" "")))
          (println (format "  b=%d  (center of 0..49 is 24/25)  %s"
                           mb (cond (= mb 24) "*** NEAR center of b-axis ***"
                                    (= mb 25) "*** CENTER of b-axis! ***"
                                    :else "")))
          (println (format "  c=%d  (center of 0..12 is 6)  %s"
                           mc (if (= mc 6) "*** CENTER of c-axis! ***" "")))
          (println (format "  d=%d  (center of 0..66 is 33)  %s"
                           md (if (= md 33) "*** CENTER of d-axis! ***" "")))
          (println)

          ;; Context: 5 letters each side of midpoint
          (println "── Context around midpoint (11 letters) ──")
          (doseq [i (range (- midpoint 5) (+ midpoint 6))]
            (let [d (coords/describe i)
                  marker (if (= i midpoint) " <<<" "")]
              (println (format "    pos=%,d  %s  gv=%3d  %s%s"
                               i (:letter d) (:gematria d) (:verse d) marker))))
          (println)

          ;; ════════════════════════════════════════════════════════
          ;; PART 3: THE d-FIBER (67 letters, understanding)
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 3: THE d-FIBER — 67 letters of understanding")
          (println (format "  Through midpoint at a=%d, b=%d, c=%d" ma mb mc))
          (println "================================================================")
          (println)

          (let [d-positions (coords/fiber :d {:a ma :b mb :c mc})]
            (fiber-report s d-positions "d-fiber (understanding)" "d"))

          ;; ════════════════════════════════════════════════════════
          ;; PART 4: THE c-FIBER (13 letters, love/unity)
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 4: THE c-FIBER — 13 letters of love/unity")
          (println (format "  Through midpoint at a=%d, b=%d, d=%d" ma mb md))
          (println (format "  Spacing: every 67 positions"))
          (println "================================================================")
          (println)

          (let [c-positions (coords/fiber :c {:a ma :b mb :d md})]
            (fiber-report s c-positions "c-fiber (love)" "c"))

          ;; ════════════════════════════════════════════════════════
          ;; PART 5: THE b-FIBER (50 letters, jubilee)
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 5: THE b-FIBER — 50 letters of jubilee")
          (println (format "  Through midpoint at a=%d, c=%d, d=%d" ma mc md))
          (println (format "  Spacing: every 871 positions"))
          (println "================================================================")
          (println)

          (let [b-positions (coords/fiber :b {:a ma :c mc :d md})]
            (fiber-report s b-positions "b-fiber (jubilee)" "b"))

          ;; ════════════════════════════════════════════════════════
          ;; PART 6: THE a-FIBER (7 letters, completeness)
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 6: THE a-FIBER — 7 letters of completeness")
          (println (format "  Through midpoint at b=%d, c=%d, d=%d" mb mc md))
          (println (format "  Spacing: every 43,550 positions"))
          (println "================================================================")
          (println)

          (let [a-positions (coords/fiber :a {:b mb :c mc :d md})]
            (fiber-report s a-positions "a-fiber (completeness)" "a"))

          ;; ════════════════════════════════════════════════════════
          ;; PART 7: COMPARISON WITH THE CENTER
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 7: COMPARISON — VEIL vs CENTER")
          (println "  Center fiber = (3, 25, 6, *) = Lev 8:35 'guard... 7 days'")
          (println (format "  Veil fiber   = (%d, %d, %d, *)" ma mb mc))
          (println "================================================================")
          (println)

          (let [center-d (coords/fiber :d {:a 3 :b 25 :c 6})
                veil-d   (coords/fiber :d {:a ma :b mb :c mc})
                center-letters (fiber-letters s center-d)
                veil-letters   (fiber-letters s veil-d)
                center-gv (fiber-gematria s center-d)
                veil-gv   (fiber-gematria s veil-d)]
            (println (format "  Center d-fiber: %s" center-letters))
            (println (format "  Center gematria: %,d" center-gv))
            (println)
            (println (format "  Veil d-fiber:   %s" veil-letters))
            (println (format "  Veil gematria:   %,d" veil-gv))
            (println)
            (println (format "  Difference: %,d" (Math/abs (long (- center-gv veil-gv)))))
            (println (format "  Sum: %,d" (+ center-gv veil-gv)))
            (let [diff (Math/abs (long (- center-gv veil-gv)))
                  sum  (+ center-gv veil-gv)]
              (doseq [[n label] [[7 "7"] [13 "13"] [37 "37"] [67 "67"] [73 "73"]]]
                (when (zero? (mod diff n))
                  (println (format "    Difference ÷%s = %,d" label (/ diff n))))
                (when (zero? (mod sum n))
                  (println (format "    Sum ÷%s = %,d" label (/ sum n))))))
            (println)

            ;; Do the fibers share any letters?
            (let [center-set (set (map #(aget ^ints center-d %) (range 67)))
                  veil-set   (set (map #(aget ^ints veil-d %) (range 67)))
                  shared (clojure.set/intersection center-set veil-set)]
              (println (format "  Shared positions: %d" (count shared)))
              (when (seq shared)
                (doseq [pos (sort shared)]
                  (let [d (coords/describe pos)]
                    (println (format "    pos=%,d  %s  %s" pos (:letter d) (:verse d))))))))
          (println)

          ;; ════════════════════════════════════════════════════════
          ;; PART 8: THE VEIL WORD (מסוה) — WHERE IS IT?
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 8: THE WORD מסוה (VEIL) IN THE PASSAGE")
          (println "================================================================")
          (println)

          (let [passage-text (apply str (map #(coords/letter-at s %) (range passage-start passage-end)))]
            (loop [start-idx 0
                   occurrence 1]
              (let [idx (.indexOf ^String passage-text "מסוה" (int start-idx))]
                (when (>= idx 0)
                  (println (format "  Occurrence %d: offset %d in passage" occurrence idx))
                  (let [abs-start (+ passage-start idx)]
                    (doseq [i (range 4)]
                      (let [pos (+ abs-start i)
                            d (coords/describe pos)]
                        (println (format "    %s  coord=%s  gv=%3d  %s"
                                         (:letter d) (:coord d) (:gematria d) (:verse d)))))
                    (println (format "    Gematria of מסוה: %d"
                                     (reduce + (map #(coords/gv-at s (+ abs-start %)) (range 4)))))
                    (println))
                  (recur (inc idx) (inc occurrence))))))
          (println)

          ;; ════════════════════════════════════════════════════════
          ;; PART 9: SHINING (קרן) — THE FACE THAT SHINES
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 9: קרן (SHINING/HORN) IN THE PASSAGE")
          (println "================================================================")
          (println)

          (let [passage-text (apply str (map #(coords/letter-at s %) (range passage-start passage-end)))]
            (loop [start-idx 0
                   occurrence 1]
              (let [idx (.indexOf ^String passage-text "קרן" (int start-idx))]
                (when (>= idx 0)
                  (println (format "  Occurrence %d: offset %d in passage" occurrence idx))
                  (let [abs-start (+ passage-start idx)]
                    (doseq [i (range 3)]
                      (let [pos (+ abs-start i)
                            d (coords/describe pos)]
                        (println (format "    %s  coord=%s  gv=%3d  %s"
                                         (:letter d) (:coord d) (:gematria d) (:verse d)))))
                    (println (format "    Gematria of קרן: %d"
                                     (reduce + (map #(coords/gv-at s (+ abs-start %)) (range 3)))))
                    (println))
                  (recur (inc idx) (inc occurrence))))))
          (println)

          ;; ════════════════════════════════════════════════════════
          ;; PART 10: THE PASSAGE LETTER-BY-LETTER WITH COORDINATES
          ;; ════════════════════════════════════════════════════════

          (println "================================================================")
          (println "  PART 10: PASSAGE COORDINATE MAP")
          (println "  Every letter of Exodus 34:29-35 with its 4D address")
          (println "================================================================")
          (println)

          ;; Show a-values distribution
          (let [a-vals (map (fn [pos]
                              (first (vec (coords/idx->coord pos))))
                            (range passage-start passage-end))
                a-freq (frequencies a-vals)]
            (println "  Distribution across a-axis (the seven divisions):")
            (doseq [a (sort (keys a-freq))]
              (println (format "    a=%d: %d letters (%.1f%%)"
                               a (get a-freq a)
                               (* 100.0 (/ (get a-freq a) (double passage-len))))))
            (println))

          ;; Show d-values distribution
          (let [d-vals (map (fn [pos]
                              (last (vec (coords/idx->coord pos))))
                            (range passage-start passage-end))
                d-wraps (count (filter #(< (second %) (first %))
                                        (partition 2 1 d-vals)))]
            (println (format "  d-axis wraps (d decreases): %d" d-wraps))
            (println "  (Each wrap = passage crosses a d-fiber boundary)"))
          (println)

          ;; Summary
          (println "================================================================")
          (println "  SUMMARY")
          (println "================================================================")
          (println)
          (println (format "  Exodus 34:29-35: %d letters, pos %,d–%,d"
                           passage-len passage-start (dec passage-end)))
          (println (format "  Midpoint: pos %,d, coord (%d, %d, %d, %d)"
                           midpoint ma mb mc md))
          (println (format "  Center of 4D space: (3, 25, 6, 33) = pos %,d"
                           (coords/coord->idx 3 25 6 33)))
          (println (format "  Distance from veil midpoint to center: %,d letters"
                           (Math/abs (long (- midpoint (coords/coord->idx 3 25 6 33))))))
          (println))

        (println "Done.")))))
