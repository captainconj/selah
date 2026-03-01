(ns experiments.062-jubilee-spine
  "The Jubilee Spine — 50 fixed points of the acd-fold.

   304,850 = 7 × 50 × 13 × 67.  Every letter has coordinate (a, b, c, d).

   Three of the four mirror folds share an exact center:
     a-fold: a = 3  (center of 7)
     c-fold: c = 6  (center of 13)
     d-fold: d = 33 (center of 67)

   Positions with (a=3, c=6, d=33) are self-mirroring under ALL THREE folds
   simultaneously. Only b varies — giving exactly 50 positions, one per jubilee.

   These are the fixed points of the compound acd-fold.
   They are the spine of the folded space. The crease of three creases.

   FINDINGS:

   1. LOCATION: The spine runs from Exodus 35:2 (b=0) to Leviticus 21:7 (b=49).
      13 letters in Exodus (35-40), 37 letters in Leviticus (1-21).
      Each of the 50 spine letters falls in a DIFFERENT verse — perfect dispersal.

   2. GEMATRIA SUM: 3,254 — no clean divisibility by 7, 13, 50, or 67.
      The structure is topological, not arithmetic. The spine's power is in
      WHERE it is, not what it sums to.

   3. THE SABBATH FIBER: b=0 (first spine point) sits in Exodus 35:2.
      Its d-fiber reads: 'you shall do work, and on the SEVENTH DAY it shall
      be holy to you, a SABBATH of rest to YHWH.' The spine begins at Sabbath.

   4. THE CONSECRATION FIBER: b=24 (crease partner of center) sits in
      Leviticus 8:23 — Aaron's consecration. Blood on the right ear, thumb,
      and big toe. The d-fiber shows the laying of hands on the ram.

   5. THE CENTER FIBER: b=25 (geometric center) = Leviticus 8:35.
      'Day and night, SEVEN DAYS, keep the charge of YHWH.'
      The center of the spine is about guarding for seven days.

   6. THE HOLINESS FIBER: b=49 (last spine point) sits in Leviticus 21:7.
      Its d-fiber reads about priestly holiness: 'they are HOLY (קדש) to
      their God... a woman defiled they shall not take... for he is HOLY (קדש).'
      The spine ends at holiness.

   7. WORDS IN THE SPINE:
      - אל (El/God) appears TWICE: at b=2..3 and b=34..35
      - שב (return) at b=7..8
      - בא (come) at b=8..9 — 'return' and 'come' are consecutive
      - יד (hand) at b=16..17
      - חיה (living) at b=42..44

   8. LETTER FREQUENCIES: א (7 times) leads — the silent letter, seven of them.
      ה (6) and ו (6) — the breath letters. ש (5) — fire/spirit.
      These four account for 24 of 50 letters (48%).

   9. RUNNING SUM: hits div-by-7 at b=25 (center!) — running sum 1,862 = 7 x 266.
      Also div-by-67 at b=39 (running sum 2,881 = 67 x 43).
      At b=43: running sum 2,912 = 7 x 13 x 32 — divides by BOTH 7 and 13.

   10. CONSTANT SPACING: All 50 points are exactly 871 = 13 x 67 positions apart.
       The spine is a perfectly regular sampling of the letter stream at the
       stride of love x understanding."
  (:require [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [clojure.string :as str]))

;; ── Utilities ──────────────────────────────────────────

(defn verse-label [v]
  (str (:book v) " " (:ch v) ":" (:vs v)))

(defn verse-text [s vref]
  (apply str (map #(coords/letter-at s %) (range (:start vref) (:end vref)))))

;; ── Hebrew word/root scanner ───────────────────────────

(def known-roots
  "Common Hebrew roots/words to look for in the spine sequence."
  {"אל"    "El (God)"
   "אב"    "av (father)"
   "בן"    "ben (son)"
   "שם"    "shem (name)"
   "יד"    "yad (hand)"
   "לב"    "lev (heart)"
   "אח"    "ach (brother)"
   "חי"    "chai (life)"
   "טב"    "tov (good)"
   "רע"    "ra (evil)"
   "אם"    "em (mother)"
   "דם"    "dam (blood)"
   "יום"   "yom (day)"
   "אור"   "or (light)"
   "שבע"   "sheva (seven)"
   "כל"    "kol (all)"
   "עם"    "am (people)"
   "ארץ"   "eretz (land)"
   "מים"   "mayim (water)"
   "שמש"   "shemesh (sun)"
   "כהן"   "kohen (priest)"
   "קדש"   "qodesh (holy)"
   "אמת"   "emet (truth)"
   "חסד"   "chesed (lovingkindness)"
   "שלם"   "shalem (peace/whole)"
   "שלום"  "shalom (peace)"
   "תורה"  "torah (instruction)"
   "יהוה"  "YHVH"
   "אלהים" "Elohim"
   "אדם"   "adam (man)"
   "חיה"   "chayah (living)"
   "מות"   "mavet/mut (death)"
   "אש"    "esh (fire)"
   "מלך"   "melekh (king)"
   "עבד"   "eved (servant)"
   "ברך"   "barakh (bless)"
   "כבד"   "kavod (glory/heavy)"
   "נפש"   "nefesh (soul)"
   "רוח"   "ruach (spirit)"
   "דבר"   "davar (word/thing)"
   "בית"   "bayit (house)"
   "ידע"   "yada (know)"
   "עין"   "ayin (eye)"
   "פה"    "peh (mouth)"
   "אהב"   "ahav (love)"
   "שמר"   "shamar (guard)"
   "זרע"   "zera (seed)"
   "עץ"    "etz (tree)"
   "גן"    "gan (garden)"
   "ספר"   "sefer (book/scroll)"
   "בר"    "bar (son/pure)"
   "נר"    "ner (lamp)"
   "אחד"   "echad (one)"
   "יבל"   "yuval (jubilee stream)"
   "חמשים" "chamishim (fifty)"
   "את"    "et (aleph-tav)"
   "שב"    "shav (return)"
   "עד"    "ed (witness)"
   "נח"    "noach (rest)"
   "מן"    "min (from/manna)"
   "בא"    "ba (come)"
   "רב"    "rav (great/many)"
   "לך"    "lekh (go)"
   "צו"    "tsav (command)"})

(defn scan-words
  "Find all known Hebrew words/roots as consecutive subsequences
   in a letter sequence. Returns [{:word :meaning :start :end}]."
  [letters]
  (let [s (apply str letters)
        n (count s)]
    (->> (for [len (range 2 6)
               start (range (- n (dec len)))]
           (let [sub (subs s start (+ start len))]
             (when-let [meaning (get known-roots sub)]
               {:word sub :meaning meaning :start start :end (+ start len)})))
         (remove nil?)
         (sort-by :start)
         vec)))

;; ── Main ───────────────────────────────────────────────

(defn -main []
  (println "================================================================")
  (println "  EXPERIMENT 062: THE JUBILEE SPINE")
  (println "  50 fixed points of the acd-fold")
  (println "  (a=3, b=0..49, c=6, d=33)")
  (println "================================================================")
  (println)

  (let [s (coords/space)]

    ;; ════════════════════════════════════════════════════════
    ;; PART 1: THE 50 LETTERS
    ;; ════════════════════════════════════════════════════════

    (println "================================================================")
    (println "  PART 1: THE 50 SPINE LETTERS")
    (println "================================================================")
    (println)

    (let [spine-data
          (vec (for [b (range 50)]
                 (let [pos (coords/coord->idx 3 b 6 33)
                       letter (coords/letter-at s pos)
                       gv (coords/gv-at s pos)
                       v (coords/verse-at s pos)]
                   {:b b :pos pos :letter letter :gv gv :verse v})))

          letters (mapv :letter spine-data)
          spine-str (apply str letters)
          total-gv (reduce + (map :gv spine-data))]

      ;; Print the table
      (println (format "  %3s  %7s  %s  %4s  %s"
                       "b" "pos" "letter" "gv" "verse"))
      (println (apply str (repeat 60 "-")))
      (doseq [{:keys [b pos letter gv verse]} spine-data]
        (println (format "  %3d  %,7d  %s      %3d  %s"
                         b pos letter gv (verse-label verse))))
      (println)

      ;; The 50-letter sequence
      (println "── The 50-letter sequence ──")
      (println (format "  %s" spine-str))
      (println)

      ;; Total gematria
      (println "── Total gematria ──")
      (println (format "  Sum = %,d" total-gv))
      (println)

      ;; Divisibility
      (println "── Divisibility ──")
      (doseq [[d label] [[7 "7 (completeness)"]
                          [13 "13 (one/love)"]
                          [50 "50 (jubilee)"]
                          [67 "67 (understanding)"]
                          [37 "37 (prime of creation)"]
                          [73 "73 (wisdom)"]]]
        (if (zero? (mod total-gv d))
          (println (format "  %,d ÷ %s = %,d  ✓" total-gv label (/ total-gv d)))
          (println (format "  %,d mod %s = %d" total-gv label (mod total-gv d)))))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 2: RECOGNIZABLE WORDS / ROOTS
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 2: HEBREW WORDS / ROOTS IN THE SPINE")
      (println "================================================================")
      (println)

      (let [found (scan-words letters)]
        (if (seq found)
          (do
            (println (format "  Found %d words/roots:" (count found)))
            (println)
            (doseq [{:keys [word meaning start end]} found]
              (println (format "    b=%d..%d  %s  %s" start (dec end) word meaning))))
          (println "  No known words/roots found."))
        (println))

      ;; Also show all consecutive pairs/triples for manual inspection
      (println "── All consecutive pairs (for manual inspection) ──")
      (doseq [i (range 49)]
        (let [pair (subs spine-str i (+ i 2))]
          (print (format "  %s" pair))
          (when (= 0 (mod (inc i) 10)) (println))))
      (println)
      (println)

      (println "── All consecutive triples ──")
      (doseq [i (range 48)]
        (let [triple (subs spine-str i (+ i 3))]
          (print (format "  %s" triple))
          (when (= 0 (mod (inc i) 10)) (println))))
      (println)
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 3: VERSE DISTRIBUTION
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 3: VERSE DISTRIBUTION")
      (println "  Where do these 50 letters fall?")
      (println "================================================================")
      (println)

      (let [by-book (group-by #(:book (:verse %)) spine-data)
            book-order ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]

        ;; All should be in Leviticus (a=3 = center seventh)
        (println "── By book ──")
        (doseq [book book-order]
          (let [entries (get by-book book)]
            (when entries
              (println (format "  %-13s  %d letters" book (count entries))))))
        (println)

        ;; By chapter
        (println "── By chapter ──")
        (let [by-chapter (group-by #(str (:book (:verse %)) " " (:ch (:verse %))) spine-data)]
          (doseq [[ch-key entries] (sort-by key by-chapter)]
            (println (format "  %-20s  %d letters" ch-key (count entries)))))
        (println)

        ;; By unique verse
        (println "── Unique verses ──")
        (let [unique-verses (distinct (map #(verse-label (:verse %)) spine-data))]
          (println (format "  %d unique verses among 50 letters:" (count unique-verses)))
          (doseq [v unique-verses]
            (let [entries (filter #(= v (verse-label (:verse %))) spine-data)]
              (println (format "    %s  (%d letters, b=%s)"
                               v
                               (count entries)
                               (str/join "," (map :b entries)))))))
        (println))

      ;; ════════════════════════════════════════════════════════
      ;; PART 4: GEMATRIA STRUCTURE
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 4: GEMATRIA STRUCTURE")
      (println "================================================================")
      (println)

      ;; Running sum along the spine
      (println "── Running gematria sum along the spine ──")
      (let [running (reductions + (map :gv spine-data))]
        (doseq [[b rsum] (map-indexed vector running)]
          (let [entry (nth spine-data b)]
            (when (or (zero? b) (= b 24) (= b 25) (= b 49)
                      (zero? (mod rsum 7))
                      (zero? (mod rsum 13))
                      (zero? (mod rsum 67)))
              (let [divs (cond-> ""
                           (zero? (mod rsum 7))  (str " /7")
                           (zero? (mod rsum 13)) (str " /13")
                           (zero? (mod rsum 67)) (str " /67"))]
                (println (format "  b=%2d  %s  running=%,5d%s  %s"
                                 b (:letter entry) rsum divs
                                 (verse-label (:verse entry)))))))))
      (println)

      ;; First half / second half
      (let [first-half  (reduce + (map :gv (take 25 spine-data)))
            second-half (reduce + (map :gv (drop 25 spine-data)))]
        (println "── Half sums (b=0..24 vs b=25..49) ──")
        (println (format "  First half  (b=0..24):  %,d" first-half))
        (println (format "  Second half (b=25..49): %,d" second-half))
        (println (format "  Difference:             %,d" (Math/abs (- first-half second-half))))
        (doseq [[label val] [["First half" first-half]
                              ["Second half" second-half]
                              ["Difference" (Math/abs (- first-half second-half))]]]
          (let [divs (cond-> ""
                       (zero? (mod val 7))  (str " /7")
                       (zero? (mod val 13)) (str " /13")
                       (zero? (mod val 50)) (str " /50")
                       (zero? (mod val 67)) (str " /67"))]
            (when (seq divs)
              (println (format "    %s = %,d%s" label val divs))))))
      (println)

      ;; Groups of 7 (if 50 allows — 7*7=49, one remainder)
      (println "── Groups of 7 ──")
      (let [groups (partition-all 7 (map :gv spine-data))]
        (doseq [[i grp] (map-indexed vector groups)]
          (let [gsum (reduce + grp)
                divs (cond-> ""
                       (zero? (mod gsum 7))  (str " /7")
                       (zero? (mod gsum 13)) (str " /13")
                       (zero? (mod gsum 67)) (str " /67"))]
            (println (format "  Group %d (b=%d..%d): sum=%,d%s"
                             i (* i 7) (min 49 (dec (* (inc i) 7))) gsum divs)))))
      (println)

      ;; Letter frequency in the spine
      (println "── Letter frequency ──")
      (let [freq (frequencies letters)
            sorted (sort-by val > freq)]
        (doseq [[letter cnt] sorted]
          (println (format "  %s  %d  (gv=%d, total gv contribution=%d)"
                           letter cnt
                           (nth coords/letter-gv (get coords/char->idx letter))
                           (* cnt (nth coords/letter-gv (get coords/char->idx letter)))))))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 5: D-FIBERS THROUGH INTERESTING FIXED POINTS
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 5: D-FIBERS THROUGH SELECTED SPINE POINTS")
      (println "  67 letters of understanding, for b = 0, 24, 25, 49")
      (println "================================================================")
      (println)

      (doseq [b [0 24 25 49]]
        (println (format "── d-fiber at (a=3, b=%d, c=6) — 67 letters ──" b))
        (let [positions (coords/fiber :d {:a 3 :b b :c 6})
              fiber-letters (mapv #(coords/letter-at s (aget positions %)) (range 67))
              fiber-str (apply str fiber-letters)
              fiber-gv (reduce + (map #(coords/gv-at s (aget positions %)) (range 67)))
              spine-entry (nth spine-data b)
              unique-verses (distinct (map #(verse-label (coords/verse-at s (aget positions %))) (range 67)))]

          (println (format "  Spine letter at d=33: %s (gv=%d)  %s"
                           (:letter spine-entry) (:gv spine-entry)
                           (verse-label (:verse spine-entry))))
          (println (format "  67 letters: %s" fiber-str))
          (println (format "  Fiber gematria sum: %,d" fiber-gv))

          (let [divs (cond-> ""
                       (zero? (mod fiber-gv 7))  (str " /7")
                       (zero? (mod fiber-gv 13)) (str " /13")
                       (zero? (mod fiber-gv 50)) (str " /50")
                       (zero? (mod fiber-gv 67)) (str " /67"))]
            (when (seq divs)
              (println (format "  Divisibility:%s" divs))))

          (println (format "  Verses spanned: %s" (str/join ", " unique-verses)))

          ;; Mark d=33 position
          (println (format "  d=33 position in fiber: %s...%s[%s]%s...%s"
                           (subs fiber-str 0 (min 10 33))
                           (if (> 33 10) "..." "")
                           (str (nth fiber-letters 33))
                           (if (< 34 57) "..." "")
                           (subs fiber-str (max 34 57))))
          (println)))

      ;; Also show the center d-fiber (b=25, the known one from exp 055)
      (println "── d-fiber at (a=3, b=25, c=6) — the known center fiber ──")
      (let [positions (coords/fiber :d {:a 3 :b 25 :c 6})
            fiber-str (apply str (map #(coords/letter-at s (aget positions %)) (range 67)))]
        (println (format "  %s" fiber-str))
        (println "  (Lev 8:35 — 'seven days keep the charge of the LORD')"))
      (println)

      ;; ════════════════════════════════════════════════════════
      ;; PART 6: THE SPINE AS A WHOLE
      ;; ════════════════════════════════════════════════════════

      (println "================================================================")
      (println "  PART 6: THE SPINE AS A WHOLE")
      (println "  50 letters: one per jubilee, the crease of three creases")
      (println "================================================================")
      (println)

      ;; The linear distance between consecutive spine letters
      (println "── Linear spacing between spine letters ──")
      (let [positions (mapv :pos spine-data)
            gaps (mapv #(- (nth positions (inc %)) (nth positions %))
                       (range 49))]
        (println (format "  Constant gap: %,d (= stride-b = 871 = 13 × 67)" (first gaps)))
        (println (format "  All gaps equal? %s" (if (apply = gaps) "YES" "NO")))
        (println (format "  Total span: %,d letters (from pos %,d to pos %,d)"
                         (- (last positions) (first positions))
                         (first positions) (last positions))))
      (println)

      ;; Summary
      (println "── Summary ──")
      (println (format "  50 letters at (3, b, 6, 33) for b=0..49"))
      (println (format "  Sequence: %s" spine-str))
      (println (format "  Total gematria: %,d" total-gv))
      (println)
      (println "  These 50 positions are the ONLY points in 304,850 that are")
      (println "  simultaneously fixed under the a-fold, c-fold, and d-fold.")
      (println "  They are evenly spaced 871 positions apart (= 13 × 67).")
      (println "  One letter per jubilee. The spine of the folded Torah."))

    (println)
    (println "Done.")))
