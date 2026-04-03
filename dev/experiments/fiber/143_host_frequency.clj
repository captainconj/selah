(ns experiments.fiber.143-host-frequency
  "Experiment 143: Host Word Frequency

   Question: When we search for Hebrew words along every direction in the
   canonical 4D space, which Torah words host the most fiber-letters?

   A 'host word' is the Torah word that a search-letter lands inside.
   When תורה is found at skip=-44489, the ת lands inside התולעת (the scarlet worm)
   in Leviticus 14:6. התולעת is the host.

   Method: Search key words in [7 50 13 67]. For each non-surface hit,
   record which Torah words host each letter. Count. Rank.
   No curated list. Let the geometry speak.

   ═══════════════════════════════════════════════════════════
   FINDINGS
   ═══════════════════════════════════════════════════════════

   COMBINED HOSTS (6 search words: תורה כבש שלום יהוה אמת חיים)
   ───────────────────────────────────────────────────────────
   3,555 unique host words across 2,753 non-surface hits.

   Top 10:
   1. את       (aleph-tav)     GV=401   282 fiber-letters
   2. אשר      (that/which)    GV=501   216
   3. יהוה     (YHWH)          GV=26    127
   4. כל       (all)           GV=50    115
   5. ואת      (and-aleph-tav) GV=407   112
   6. משה      (Moses)         GV=345   106
   7. ישראל    (Israel)        GV=541   89
   8. כי       (because/when)  GV=30    88
   9. ויאמר    (and he said)   GV=257   87
   10. אל      (to/God)        GV=31    63

   Notable deeper:
   #15 הכהן (the priest) 41
   #20 המשכן (the tabernacle) 35
   #27 אלהים (Elohim) 27
   #34 אהרן (Aaron) 23
   #36 המזבח (the altar) 22
   #45 אברהם (Abraham) 19
   #46 מועד (appointed time) 19

   NOTE: Frequency bias exists — longer, more frequent words host more.
   But the geometry still selects the priest, tabernacle, altar, appointed time.

   PER-WORD HOSTS
   ───────────────────────────────────────────────────────────

   תורה (GV=611) — 504 non-surface
     #1 יהוה(73) — Torah fibers pass through the Name more than anything.
     #2 את(64)  #3 אשר(48)  #4 ואת(35)  #5 ישראל(24)
     #8 אהרן(16)  #13 הכהן(11)

   כבש (GV=322) — 888 non-surface
     #1 כל(108) — Lamb fibers pass through 'all' (GV=50=jubilee).
     #5 משה(32)  #6 הכהן(29)  #7 ישראל(28)  #8 המשכן(25)
     The lamb is in the furniture. Priest, tabernacle, Israel.

   שלום (GV=376) — 225 non-surface
     #1 אשר(25)  #2 לו(13, 'to him')  #3 ישראל(13)  #4 יהוה(12)
     #5 אל(12)  #6 לא(10, 'not')
     Peace: to him, Israel, YHWH, not.

   יהוה (GV=26) — 58 non-surface (fewest — the Name is hard to find as ELS)
     #1 יהוה(26) — THE NAME LANDS IN ITSELF 26 TIMES. GV=26=YHWH.
     26 self-hostings. The Name's number.
     #2 ויעש(3)  #3 ויהי(3)  #4 משה(3)  #5 ויאמר(3)
     Then: 'and he made', 'and it was', Moses, 'and he said'.

   אמת (GV=441) — 937 non-surface (most)
     #1 את(218) — Truth lives inside aleph-tav. The beginning and the end.
     #2 ואת(70)  #3 ויאמר(59, 'and he said')  #4 משה(55)
     Truth is in the saying and in Moses.

   חיים (GV=68) — 141 non-surface
     #1 יהוה(16) — Life fibers pass through YHWH first.
     #3 המזבח(6, the altar)
     Life is at the Name and the altar.

   AXIS ANALYSIS — תורה
   ───────────────────────────────────────────────────────────
   Pure axis walks (one axis varies):

   Axis a (completeness=7): 12 hits
     בהר(on the mountain), יהוה, הבית(the house), מועד(appointed time),
     רחם(womb), ותרדמה(deep sleep)

   Axis b (jubilee=50): 16 hits
     יהוה(4), פרעה(Pharaoh), גבול(border), האדמה(the ground)

   Axis c (love=13): 14 hits
     את(4), שנה(year), ואת(2), שרוג(intertwined), ויברך(and he blessed), זהב(gold)

   Diagonal walks (2+ axes): 462 of 504 — the bulk.
     יהוה(66) dominates the diagonals.

   NAMES LANDING IN NAMES
   ───────────────────────────────────────────────────────────
   David(14) lands in: Adam(11), Joseph(5)
   Moses(345) lands in: Moses(15 self), Israel(11), Aaron(1)
   Aaron(256) lands in: Israel(7), Aaron(6 self), Moses(3)
   Adam(45) lands in: Adam(6 self), Aaron(3), Israel(2), Abraham(1)
   Jacob(182) lands in: Jacob(3 self), Abraham(1), Israel(1)
   Joseph(156) lands in: Joseph(4 self)
   Noah(58) lands in: Noah(3 self)

   David lands in Adam and Joseph — never in himself (דוד does not appear
   as a Torah word, David is not named in the Torah). The geometry routes
   the beloved (דוד=beloved, GV=14=2×7) through the first man and the
   separated son.

   THE TRUTH FIBER — GARDEN TO EXPULSION
   ───────────────────────────────────────────────────────────
   אמת at skip=805 = direction [0, +1, -1, +1]
   (jubilee ascending, love descending, understanding ascending, completeness constant)

   Coordinates: [0,2,12,56] → [0,3,11,57] → [0,4,10,58]

   Host words:
   1. א in האדם (the man, GV=50) — Genesis 2:19 (Adam naming the animals)
   2. מ in שמעתי (I heard) — Genesis 3:10 ('I heard your voice and was afraid')
   3. ת in את (aleph-tav) — Genesis 3:24 (the cherubim and the flaming sword)

   Truth walks from naming to fear to exile.
   Love decreases (12→11→10). Jubilee increases (2→3→4).
   Understanding increases. The truth fiber moves toward understanding
   and away from love. The cost of knowing.

   THE COVENANT FIBER — OATH TO THE ANOINTED
   ───────────────────────────────────────────────────────────
   ברית at skip=-44489, all 4 axes varying.

   Host words:
   1. ב in שבע (seven/oath, GV=372) — Numbers 26:51
   2. ר in מזרחה (eastward, GV=260) — Numbers 2:3 (camp arrangement)
   3. י in המשיח (THE ANOINTED ONE, GV=363) — Leviticus 4:5 (sin offering)
   4. ת in fire at the sea — Exodus 15:7

   The covenant passes through: oath, eastward, the anointed one, consuming fire.
   The yod (hand) of covenant lands in the Messiah.

   HEART PAIRS
   ───────────────────────────────────────────────────────────
   לב is 2 letters. Every fiber is a pair of host words.
   984 unique pairs from 1,000 fibers. Nearly all unique.

   Notable pairs:
   - אל / בשר (God / flesh) — 3 fibers. The heart connects God and flesh.
   - עולם / בן (eternity / son) — 2 fibers.
     Genesis 9:16 (everlasting covenant) → Genesis 36:32 (son, king of Edom)
     Exodus 12:14 (eternal statute) → Exodus 38:22 (son, Bezalel the builder)
   - מתושלח / אבא (Methuselah / father) — 1 fiber.
     Genesis 5:22 → Genesis 33:14. Methuselah (his death sends it) → Abba.
   - לבן / שבעים (Laban / seventy)
   - אלהים / ובעל (God / and husband)

   The heart of eternity is the son. The heart of Methuselah is Abba.

   6D COMPARISON [2 5 5 7 13 67]
   ───────────────────────────────────────────────────────────
   Same 3 words (תורה, כבש, יהוה), finer decomposition.
   More directions (364 vs 40) = more fibers.

   תורה: 1,782 non-surface (vs 504 in 4D)
   כבש: 1,899 non-surface (vs 888)
   יהוה: 293 non-surface (vs 58)

   Top hosts (4,455 unique):
   1. אשר(379)  2. יהוה(376)  3. את(250)  4. כל(235)  5. כי(204)
   6. ישראל(164)  7. ואת(151)  8. משה(141)  9. הכהן(102)  10. בני(93)

   The priest moves up to #9. The same vocabulary rises.
   YHWH moves from #3 to #2. More fibers, more Name."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ── Setup ────────────────────────────────────────────
;; (s/build!)

;; ── Utilities ────────────────────────────────────────

(defn host-frequency
  "For a set of search words, count host words across all non-surface fibers."
  [dims search-words]
  (let [hosts (atom {})]
    (doseq [w search-words]
      (let [hits (s/find-word dims w)
            ns-hits (f/non-surface hits)]
        (println (format "  %s: %d hits, %d non-surface" w (count hits) (count ns-hits)))
        (doseq [h ns-hits]
          (doseq [tw (:torah-words h)]
            (when tw
              (swap! hosts update (:word tw) (fnil inc 0)))))))
    @hosts))

(defn print-top-hosts [hosts n]
  (println (format "\n  %d unique hosts\n" (count hosts)))
  (doseq [[i [word cnt]] (map-indexed vector (take n (sort-by val > hosts)))]
    (println (format "  %2d. %-10s GV=%-4d  %d fiber-letters"
                     (inc i) word (g/word-value word) cnt))))

(defn per-word-hosts
  "Host frequency for a single word."
  [dims word]
  (let [hits (s/find-word dims word)
        ns-hits (f/non-surface hits)
        hosts (atom {})]
    (doseq [h ns-hits]
      (doseq [tw (:torah-words h)]
        (when tw (swap! hosts update (:word tw) (fnil inc 0)))))
    {:word word :hits (count hits) :non-surface (count ns-hits) :hosts @hosts}))

(defn fiber-pairs
  "For a 2-letter word, extract host-word pairs from each fiber."
  [dims word]
  (let [hits (s/find-word dims word)
        ns-hits (f/non-surface hits)]
    (frequencies
      (for [h ns-hits
            :let [tw (:torah-words h)
                  w1 (when (first tw) (:word (first tw)))
                  w2 (when (second tw) (:word (second tw)))]
            :when (and w1 w2)]
        [w1 w2]))))

;; ── Run ──────────────────────────────────────────────

(comment
  (s/build!)

  ;; Combined hosts
  (let [hosts (host-frequency [7 50 13 67]
                              ["תורה" "כבש" "שלום" "יהוה" "אמת" "חיים"])]
    (print-top-hosts hosts 50))

  ;; Per-word
  (doseq [w ["תורה" "כבש" "שלום" "יהוה" "אמת" "חיים"]]
    (let [{:keys [hosts non-surface]} (per-word-hosts [7 50 13 67] w)]
      (println (format "\n═══ %s — %d non-surface ═══" w non-surface))
      (print-top-hosts hosts 15)))

  ;; Heart pairs
  (let [pairs (fiber-pairs [7 50 13 67] "לב")]
    (println (format "%d unique pairs" (count pairs)))
    (doseq [[[w1 w2] cnt] (take 30 (sort-by val > pairs))]
      (println (format "  %s / %s  (GV %d/%d)  %d" w1 w2 (g/word-value w1) (g/word-value w2) cnt))))

  ;; Names
  (doseq [w ["דוד" "משה" "אברהם" "יוסף" "אהרן" "אדם" "נח" "יעקב" "ישראל"]]
    (let [{:keys [hosts non-surface]} (per-word-hosts [7 50 13 67] w)
          name-set #{"יוסף" "משה" "אברהם" "אדם" "אהרן" "ישראל" "יעקב" "נח" "דוד"}
          in-names (select-keys hosts name-set)]
      (println (format "  %s (%d ns): lands in %s" w non-surface
                       (str/join ", " (map (fn [[k v]] (str k "(" v ")"))
                                           (sort-by val > in-names)))))))

  ;; Truth fiber: garden to expulsion
  (let [hits (s/find-word [7 50 13 67] "אמת")
        target (first (filter #(and (= (:skip %) 805)
                                    (some (fn [tw] (and tw (= (:word tw) "האדם")))
                                          (:torah-words %)))
                              hits))]
    (f/print-fiber target))

  ;; Covenant fiber: oath to anointed
  (let [hits (s/find-word [7 50 13 67] "ברית")
        target (first (filter #(some (fn [tw] (and tw (= (:word tw) "המשיח")))
                                      (:torah-words %))
                              (f/non-surface hits)))]
    (f/print-fiber target))
  )
