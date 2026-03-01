(ns experiments.060-milemarker-mirrors
  "What sits at the a-fold mirror of each classic ELS milemarker?

   The classic pattern:
     Genesis:     תורה at skip +50  (Torah, forward)
     Exodus:      תורה at skip +50  (Torah, forward)
     Leviticus:   יהוה at skip +7   (the Name)
     Numbers:     הרות at skip +50  (Torah, reversed)
     Deuteronomy: הרות at skip +50  (Torah, reversed)

   The outer four books point inward. The center spells the Name.
   Now: fold across the seven days. What's at each mirror?

   FINDINGS
   ========
   26 milemarkers in the WLC stream (3 Gen, 8 Exod, 10 Lev, 2 Num, 4 Deut).

   1. Genesis 1:1 תורה@+50 mirrors to Deuteronomy 6:1 — the Shema.
      The first ELS milemarker folds to the most important declaration in Judaism.
      Pair GV = 651 = 7 × 93. Divides by completeness.

   2. Exodus 39:8 תורה@+50 sits in a=3 (center seventh) — mirrors to ITSELF.
      The verse describes making the breastplate (חשן) that holds the Urim and Thummim.
      Pair GV = 1222 = 2 × 611 = twice-Torah. Divides by 13 (love).
      The machine that reads the letters mirrors to itself.

   3. Three center יהוה@+7 (Lev 14:46, 16:9, 21:13) all self-mirror.
      Pair GV = 52 = 4 × 13. 52 = the number of outer aleph-tav mirror pairs (059).

   4. Leviticus 26:44 יהוה@+7 mirrors to Exodus 13:13.
      At the mirror position, הוהי (the Name REVERSED) appears in consecutive text (skip 1).
      The Name at skip 7 mirrors to a place where the consecutive letters spell it backwards.

   5. Deuteronomy 5:16 הרות@+50 (Ten Commandments: honor your father and mother)
      mirrors to Exodus 6:26. Pair GV = 777 = 7 × 111.

   6. Deuteronomy 8:9 הרות@+50 mirrors to Genesis 3:10 —
      'I heard your voice in the garden and I was afraid.'
      The reversed Torah in Deuteronomy mirrors to the hiding in Eden.

   7. The chiastic structure (תורה→תורה→יהוה→הרות→הרות) already points inward.
      The a-fold confirms it: outer books mirror to each other, center mirrors to itself,
      and thematic pairings hold across the fold."
  (:require [selah.els.engine :as els]
            [selah.space.coords :as coords]
            [selah.text.oshb :as oshb]
            [selah.gematria :as gem]))

(defn book-offset
  "Returns the starting index of each book in the full Torah stream."
  []
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        lengths (map #(count (oshb/book-letters %)) books)]
    (zipmap books (reductions + 0 (butlast lengths)))))

(defn a-mirror
  "Mirror position i across the a-axis: (a,b,c,d) -> (6-a,b,c,d)."
  [i]
  (let [c (coords/idx->coord i)
        a (aget c 0)]
    (coords/coord->idx (- 6 a) (aget c 1) (aget c 2) (aget c 3))))

(defn context-at
  "Get text context around position i in the Torah stream."
  [s i radius]
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
     :letter   (coords/letter-at s i)
     :gv       (coords/gv-at s i)
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :context  (context-at s i 30)}))

(defn find-milemarkers
  "Find all classic ELS milemarkers in the WLC Torah stream."
  [s]
  (let [offsets (book-offset)
        torah   (vec (map #(coords/letter-at s %) (range (:n s))))
        results (atom [])]

    ;; Genesis: תורה at skip +50
    (let [gen-letters (vec (oshb/book-letters "Genesis"))
          hits (els/search gen-letters "תורה" 50)]
      (doseq [hit hits]
        (swap! results conj
               {:book "Genesis" :word "תורה" :skip 50
                :local-start (:start hit)
                :global-start (+ (offsets "Genesis") (:start hit))})))

    ;; Exodus: תורה at skip +50
    (let [exod-letters (vec (oshb/book-letters "Exodus"))
          hits (els/search exod-letters "תורה" 50)]
      (doseq [hit hits]
        (swap! results conj
               {:book "Exodus" :word "תורה" :skip 50
                :local-start (:start hit)
                :global-start (+ (offsets "Exodus") (:start hit))})))

    ;; Leviticus: יהוה at skip +7 and -7
    (let [lev-letters (vec (oshb/book-letters "Leviticus"))
          hits-fwd (els/search lev-letters "יהוה" 7)
          hits-rev (els/search lev-letters "יהוה" -7)]
      (doseq [hit hits-fwd]
        (swap! results conj
               {:book "Leviticus" :word "יהוה" :skip 7
                :local-start (:start hit)
                :global-start (+ (offsets "Leviticus") (:start hit))}))
      (doseq [hit hits-rev]
        (swap! results conj
               {:book "Leviticus" :word "יהוה" :skip -7
                :local-start (:start hit)
                :global-start (+ (offsets "Leviticus") (:start hit))})))

    ;; Numbers: הרות at skip +50 (Torah reversed)
    (let [num-letters (vec (oshb/book-letters "Numbers"))
          hits (els/search num-letters "הרות" 50)]
      (doseq [hit hits]
        (swap! results conj
               {:book "Numbers" :word "הרות" :skip 50
                :local-start (:start hit)
                :global-start (+ (offsets "Numbers") (:start hit))})))

    ;; Deuteronomy: הרות at skip +50
    (let [deut-letters (vec (oshb/book-letters "Deuteronomy"))
          hits (els/search deut-letters "הרות" 50)]
      (doseq [hit hits]
        (swap! results conj
               {:book "Deuteronomy" :word "הרות" :skip 50
                :local-start (:start hit)
                :global-start (+ (offsets "Deuteronomy") (:start hit))})))

    @results))

(defn analyze-mirror
  "For a milemarker, analyze what's at its a-fold mirror."
  [s marker]
  (let [i      (:global-start marker)
        mirror (a-mirror i)
        ;; The ELS covers 4 letters at skip spacing
        ;; Get all positions in the original ELS
        skip   (:skip marker)
        els-positions (map #(+ i (* % skip)) (range 4))
        ;; Mirror each position
        mirror-positions (map a-mirror els-positions)
        ;; What letters are at the mirror positions?
        mirror-letters (map #(coords/letter-at s %) mirror-positions)
        mirror-word (apply str mirror-letters)
        ;; Context at the mirror start
        mirror-desc (describe-position s mirror)
        ;; What's the gematria of the original ELS word vs mirror word?
        orig-gv (reduce + (map #(coords/gv-at s %) els-positions))
        mirror-gv (reduce + (map #(coords/gv-at s %) mirror-positions))]
    (merge marker
           {:orig-coord    (vec (coords/idx->coord i))
            :orig-verse    (:verse (describe-position s i))
            :mirror-start  mirror
            :mirror-coord  (:coord mirror-desc)
            :mirror-verse  (:verse mirror-desc)
            :mirror-letter (:letter mirror-desc)
            :mirror-word   mirror-word
            :mirror-context (:context mirror-desc)
            :orig-gv       orig-gv
            :mirror-gv     mirror-gv
            :pair-gv       (+ orig-gv mirror-gv)
            :pair-div-7    (zero? (mod (+ orig-gv mirror-gv) 7))
            :pair-div-13   (zero? (mod (+ orig-gv mirror-gv) 13))})))

(defn check-els-at-mirror
  "Check if any interesting ELS words appear at the mirror position."
  [s mirror-pos]
  (let [targets ["תורה" "הרות" "יהוה" "הוהי" "אלהים" "שמר" "אמת" "אהבה" "אחד"]
        stream  (vec (map #(coords/letter-at s %) (range (:n s))))]
    (for [target targets
          skip   [7 50 -7 -50 1 -1 13 -13 67 -67]
          :let [word (els-letters-at s mirror-pos skip (count target))]
          :when (= word target)]
      {:word target :skip skip :at mirror-pos})))

(defn -main []
  (println "=== 060: MILEMARKER MIRRORS ===")
  (println "  What sits at the a-fold mirror of each classic ELS milemarker?\n")

  (let [s       (coords/space)
        markers (find-milemarkers s)]

    (println (format "Found %d milemarkers total.\n" (count markers)))

    ;; Group by book
    (doseq [[book book-markers] (group-by :book markers)]
      (println (format "── %s ──" book))
      (doseq [m (sort-by :local-start book-markers)]
        (let [analysis (analyze-mirror s m)]
          (println (format "\n  %s at skip %+d, position %d"
                           (:word m) (:skip m) (:global-start m)))
          (println (format "    Coord: %s  Verse: %s"
                           (:orig-coord analysis) (:orig-verse analysis)))
          (println (format "    ↕ a-fold mirror ↕"))
          (println (format "    Mirror position: %d" (:mirror-start analysis)))
          (println (format "    Mirror coord: %s  Verse: %s"
                           (:mirror-coord analysis) (:mirror-verse analysis)))
          (println (format "    Mirror word (same positions, mirrored): %s"
                           (:mirror-word analysis)))
          (println (format "    Original GV: %d  Mirror GV: %d  Pair: %d (÷7=%s ÷13=%s)"
                           (:orig-gv analysis) (:mirror-gv analysis)
                           (:pair-gv analysis)
                           (:pair-div-7 analysis) (:pair-div-13 analysis)))

          ;; Check for ELS at mirror
          (let [els-hits (check-els-at-mirror s (:mirror-start analysis))]
            (when (seq els-hits)
              (println (format "    *** ELS at mirror: %s ***"
                               (pr-str els-hits)))))))
      (println))

    ;; Summary table
    (println "\n── SUMMARY ──\n")
    (println (format "  %-14s %-6s %6s  %-22s  ↔  %-22s  %6s  %-6s"
                     "Book" "Word" "Skip" "Verse" "Mirror Verse" "Pair" "÷?"))
    (println (apply str (repeat 110 "─")))

    (doseq [m (sort-by :global-start markers)]
      (let [a (analyze-mirror s m)
            divs (cond-> ""
                   (:pair-div-7 a)  (str "7 ")
                   (:pair-div-13 a) (str "13"))]
        (println (format "  %-14s %-6s %+6d  %-22s  ↔  %-22s  %6d  %-6s"
                         (:book m) (:word m) (:skip m)
                         (:orig-verse a) (:mirror-verse a)
                         (:pair-gv a) divs))))

    (println "\nDone.")))
