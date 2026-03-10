(ns experiments.082-the-reading-machine
  "The physical reading machine — three readers, one grid.

   The breastplate (4 rows × 3 columns) is placed face-down on the mercy seat
   between the two cherubim. Three readers observe the same 12 stones:

   - Aaron (from above): reads ROWS → 3-letter words (4 readings)
   - Right cherub (facing left): reads COLUMNS top→bottom → 4-letter words (3 readings)
   - Left cherub (facing right): reads COLUMNS bottom→top → 4-letter words reversed (3 readings)

   The Eli/Hannah story (Yoma 73b) is a 4-letter reading direction error:
   שכרה ('drunk') vs כשרה ('like Sarah') — same letters, opposite column direction.
   Eli read the right cherub's direction when the answer was the left cherub's.

   This experiment verifies:
   1. The irreducible words — which Torah vocabulary has prime gematria
   2. The anagram pairs — words that are the same letters rearranged (same value)
   3. The veil/mercy seat identity — פרכת and כפרת
   4. The three-reader geometry — what each observer can read
   5. The Eli/Hannah error as a column-direction error

   FINDINGS:
   - Urim (257) is prime/irreducible. Thummim (490) = 7²×10, structured by completeness.
   - אל (God) = לא (Not) = 31 (prime). Same letters reversed. The fold interconverts them.
   - פרכת (veil) = כפרת (mercy seat) = 700 = 7×100. Same letters rearranged.
   - אבן (stone) = גן (garden) = 53 (prime). Both = 3 + 50 (father-stuff + jubilee).
   - שכרה = כשרה = 525 = 3×5²×7. The misreading and correct reading share 7.
   - 30 of ~190 dictionary words are irreducible (prime gematria).
   - The axes themselves are irreducible: 7, 13, 67 are all prime."
  (:require [selah.gematria :as g]
            [clojure.string :as str]))

;; ═══════════════════════════════════════════════════════════════
;; Helpers
;; ═══════════════════════════════════════════════════════════════

(defn prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else (not (some #(zero? (mod n %))
                     (range 3 (inc (int (Math/sqrt n))) 2)))))

(defn factorize [n]
  (loop [n n, d 2, fs []]
    (cond
      (= n 1) fs
      (zero? (mod n d)) (recur (/ n d) d (conj fs d))
      :else (recur n (inc d) fs))))

(defn letters
  "Extract individual Hebrew letters from a word."
  [word]
  (vec word))

(defn anagram?
  "Are two words composed of the same letters (possibly rearranged)?"
  [a b]
  (= (sort (letters a)) (sort (letters b))))

;; ═══════════════════════════════════════════════════════════════
;; Vocabulary — curated Torah words
;; ═══════════════════════════════════════════════════════════════

(def vocabulary
  {"את" "aleph-tav" "אל" "to/God" "על" "upon/over"
   "כי" "for/because" "כל" "all/every" "לא" "not"
   "הוא" "he/it" "בן" "son" "עד" "until/witness"
   "או" "or" "מן" "from" "אם" "mother"
   "כן" "thus/so" "גם" "also" "זה" "this"
   "נא" "please" "עם" "people/with" "אני" "I"
   "שם" "there/name"
   "אמר" "say" "דבר" "speak/word/thing" "עשה" "make/do"
   "נתן" "give" "ראה" "see" "שמע" "hear" "ידע" "know"
   "הלך" "walk/go" "בוא" "come/enter" "ישב" "sit/dwell"
   "עמד" "stand" "נשא" "lift/carry" "שלח" "send"
   "קרא" "call/read" "ברא" "create" "שמר" "guard/keep"
   "כפר" "atone/cover" "גאל" "redeem" "כרת" "cut (covenant)"
   "סלח" "forgive" "פדה" "ransom" "ירש" "inherit"
   "ברך" "bless" "צוה" "command" "עלה" "go up/offering"
   "שוב" "return" "מות" "die/death" "חטא" "sin"
   "לקח" "take" "שכב" "lie down" "יצא" "go out"
   "נפל" "fall" "קום" "arise" "שים" "set/place"
   "אכל" "eat" "שתה" "drink" "כתב" "write"
   "זבח" "sacrifice" "נדר" "vow" "טהר" "be pure"
   "קדש" "be holy" "ענה" "answer/afflict" "ירא" "fear"
   "אהב" "love (verb)" "שפך" "pour out" "נקם" "avenge"
   "רחם" "have mercy" "חנן" "be gracious"
   "יהוה" "YHWH" "אלהים" "God (Elohim)" "אדני" "Lord"
   "משה" "Moses" "אהרן" "Aaron" "ישראל" "Israel"
   "אברהם" "Abraham" "יצחק" "Isaac" "יעקב" "Jacob"
   "יוסף" "Joseph" "פרעה" "Pharaoh" "אדם" "man/Adam"
   "חוה" "Eve" "נח" "Noah" "אשה" "woman/wife"
   "איש" "man" "אב" "father" "אח" "brother"
   "כהן" "priest" "מלך" "king" "עבד" "servant"
   "נביא" "prophet" "בת" "daughter"
   "ארץ" "land/earth" "שמים" "heaven" "מים" "water"
   "אש" "fire" "אור" "light" "חשך" "darkness"
   "יום" "day" "לילה" "night" "ענן" "cloud"
   "עץ" "tree" "גן" "garden" "פרי" "fruit"
   "זרע" "seed" "אבן" "stone" "צור" "rock"
   "הר" "mountain" "ים" "sea" "נהר" "river"
   "באר" "well" "דם" "blood" "נפש" "soul"
   "רוח" "spirit/wind" "כבש" "lamb" "פר" "bull"
   "שור" "ox" "נחש" "serpent" "עוף" "bird"
   "דג" "fish" "בהמה" "animal/beast" "בקר" "cattle" "צאן" "flock"
   "תורה" "Torah/teaching" "אהבה" "love" "בינה" "understanding"
   "חכמה" "wisdom" "אמת" "truth" "חסד" "lovingkindness"
   "צדק" "righteousness" "שלום" "peace/wholeness"
   "ברית" "covenant" "דעת" "knowledge" "משפט" "judgment"
   "עולם" "eternal/world" "חיים" "life"
   "שבת" "sabbath" "קרבן" "offering" "חרב" "sword"
   "קשת" "bow/rainbow" "דרך" "way/path"
   "משכן" "tabernacle" "מזבח" "altar" "ארון" "ark"
   "כרוב" "cherub" "אהל" "tent" "פרכת" "veil/curtain"
   "שמן" "oil" "יין" "wine" "לחם" "bread"
   "זהב" "gold" "כסף" "silver" "נחשת" "bronze"
   "טוב" "good" "רע" "evil/bad" "גדול" "great"
   "קטן" "small" "חכם" "wise" "קדוש" "holy"
   "אחד" "one" "שבע" "seven/swear" "עשר" "ten"
   "מאה" "hundred" "אלף" "thousand" "רב" "many/great"
   "חי" "living" "חדש" "new" "ישר" "upright"
   "תמים" "complete/blameless" "כבד" "heavy/glory" "חזק" "strong"
   "יד" "hand" "עין" "eye" "פנים" "face" "פה" "mouth"
   "לב" "heart" "ראש" "head" "רגל" "foot" "אזן" "ear"
   "בית" "house" "עיר" "city" "שער" "gate"
   "מדבר" "wilderness" "אמן" "amen/faithful"
   "כבוד" "glory" "חן" "grace" "רחמים" "mercies"
   "שכינה" "dwelling/presence"
   "רחל" "Rachel" "לאה" "Leah" "נער" "youth/lad"
   "סלה" "selah"
   ;; Machine components
   "אורים" "Urim" "כפרת" "mercy seat"})

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — The Irreducible Words
;; ═══════════════════════════════════════════════════════════════

(defn part-1-irreducible []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: The Irreducible Words (prime gematria)")
  (println "══════════════════════════════════════════════\n")

  (let [all-words (->> vocabulary
                       (map (fn [[w _]] {:word w :gv (g/word-value w)}))
                       (sort-by :gv))
        primes    (filter #(prime? (:gv %)) all-words)]

    (doseq [{:keys [word gv]} primes]
      (printf "  %4d  %s\n" gv word))

    (println (format "\n  %d irreducible words out of %d total"
                     (count primes) (count all-words)))

    ;; Check the axes
    (println "\n  The axes:")
    (doseq [n [7 50 13 67]]
      (printf "    %2d — %s\n" n (if (prime? n) "PRIME (irreducible)" "composite")))

    ;; The Urim/Thummim pair
    (println "\n  The reading pair:")
    (let [urim 257 thummim 490]
      (printf "    Urim   = %d — %s, %s\n" urim
              (if (prime? urim) "PRIME" "composite") (str (factorize urim)))
      (printf "    Thummim = %d — %s, %s\n" thummim
              (if (prime? thummim) "PRIME" "composite") (str (factorize thummim)))
      (printf "    Light cannot be factored. Perfection = 7² × 10.\n"))))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — The Anagram Pairs (same letters, same value, different reading)
;; ═══════════════════════════════════════════════════════════════

(defn part-2-anagram-pairs []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: The Anagram Pairs — same letters, different arrangement")
  (println "══════════════════════════════════════════════\n")

  (let [pairs [["אל"  "God/to"     "לא"  "not"]
               ["פרכת" "veil"       "כפרת" "mercy seat"]
               ["שכרה" "drunk"      "כשרה" "like Sarah"]
               ["אבן"  "stone"      "גן"   "garden (same value, different letters)"]]]

    (doseq [[w1 m1 w2 m2] pairs]
      (let [gv1 (g/word-value w1)
            gv2 (g/word-value w2)
            anag (anagram? w1 w2)]
        (println (format "  %s (%s) = %d    ↔    %s (%s) = %d"
                         w1 m1 gv1 w2 m2 gv2))
        (println (format "    Same value: %s | Anagram: %s | Prime: %s | Factors: %s"
                         (= gv1 gv2) anag (prime? gv1) (str (factorize gv1))))
        (when (= gv1 gv2)
          (println (format "    ÷7: %s | ÷13: %s | ÷67: %s"
                           (zero? (mod gv1 7))
                           (zero? (mod gv1 13))
                           (zero? (mod gv1 67)))))
        (println)))))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — The Veil and the Mercy Seat
;; ═══════════════════════════════════════════════════════════════

(defn part-3-veil-mercy-seat []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: The Veil and the Mercy Seat")
  (println "══════════════════════════════════════════════\n")

  (let [veil   "פרכת"
        mercy  "כפרת"
        gv     (g/word-value veil)]
    (println "  The veil (פרכת):")
    (println (format "    Letters: %s" (str/join " " (map str (letters veil)))))
    (println (format "    Gematria: %d = %s" gv (str (factorize gv))))
    (println)
    (println "  The mercy seat (כפרת):")
    (println (format "    Letters: %s" (str/join " " (map str (letters mercy)))))
    (println (format "    Gematria: %d = %s" (g/word-value mercy) (str (factorize (g/word-value mercy)))))
    (println)
    (println (format "  Same letters: %s" (= (sort (letters veil)) (sort (letters mercy)))))
    (println (format "  Same value: %s" (= gv (g/word-value mercy))))
    (println)
    (println "  Root כפר (atone/cover):")
    (println (format "    כפר = %d = %s" (g/word-value "כפר") (str (factorize (g/word-value "כפר")))))
    (println)
    (println "  You pass through the veil (פרכת) and find the mercy seat (כפרת).")
    (println "  Same letters. Same value. The barrier and the destination are the same material.")
    (println "  What changes is the arrangement — the reading order — the Thummim.")))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — The Three Readers
;; ═══════════════════════════════════════════════════════════════

(defn part-4-three-readers []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: The Three Readers — Aaron and the Cherubim")
  (println "══════════════════════════════════════════════\n")

  (println "  The breastplate: 4 rows × 3 columns = 12 stones\n")
  (println "           Col1    Col2    Col3")
  (println "  Row 1:   [1]     [2]     [3]     ← Aaron's near side")
  (println "  Row 2:   [4]     [5]     [6]")
  (println "  Row 3:   [7]     [8]     [9]")
  (println "  Row 4:  [10]    [11]    [12]     ← Aaron's far side")
  (println "            ↑               ↑")
  (println "        Right cherub    Left cherub")
  (println "        reads ↓ (T→B)   reads ↑ (B→T)")
  (println)
  (println "  Three readers, three orientations:")
  (println)
  (println "  AARON (from above):")
  (println "    Reads ROWS (left→right or right→left)")
  (println "    3 stones per row → 3-letter words")
  (println "    4 rows = 4 possible readings")
  (println)
  (println "  RIGHT CHERUB (facing left):")
  (println "    Reads COLUMNS, Aaron's top → bottom")
  (println "    4 stones per column → 4-letter words")
  (println "    3 columns = 3 possible readings")
  (println)
  (println "  LEFT CHERUB (facing right):")
  (println "    Reads COLUMNS, Aaron's bottom → top")
  (println "    4 stones per column → 4-letter words REVERSED")
  (println "    3 columns = 3 possible readings")
  (println)
  (println "  The cherubim read the SAME columns in OPPOSITE directions.")
  (println "  Like אל and לא — same letters, reversed by which side you're on."))

;; ═══════════════════════════════════════════════════════════════
;; Part 5 — The Eli/Hannah Error
;; ═══════════════════════════════════════════════════════════════

(defn part-5-eli-hannah []
  (println "\n══════════════════════════════════════════════")
  (println "Part 5: The Eli/Hannah Error — Yoma 73b")
  (println "══════════════════════════════════════════════\n")

  (let [drunk  "שכרה"
        sarah  "כשרה"
        gv     (g/word-value drunk)]
    (println "  Four letters light up: ש כ ר ה")
    (println)
    (println (format "  Eli reads (right cherub, top→bottom): %s = \"%s\" = %d"
                     drunk "she is drunk" gv))
    (println (format "  Correct   (left cherub, bottom→top):  %s = \"%s\" = %d"
                     sarah "like Sarah" (g/word-value sarah)))
    (println)
    (println (format "  Same value: %s" (= gv (g/word-value sarah))))
    (println (format "  Anagram: %s" (anagram? drunk sarah)))
    (println (format "  Value: %d = %s" gv (str (factorize gv))))
    (println (format "  ÷7: %s (completeness)" (zero? (mod gv 7))))
    (println)
    (println "  This is a 4-LETTER word — the cherubim's reading, not Aaron's.")
    (println "  Eli read from the right cherub's direction (top→bottom).")
    (println "  The correct reading was the left cherub's direction (bottom→top).")
    (println "  Same column. Same stones lit. Wrong reading direction.")
    (println)
    (println "  The Talmud is telling us:")
    (println "    - The breastplate has TWO valid 4-letter reading directions")
    (println "    - Wisdom (the Thummim) is knowing WHICH direction")
    (println "    - The letters don't change — only the arrangement")
    (println "    - 525 ÷ 7 = 75 — both readings are structured by completeness")
    (println "    - But only one arrangement is true")))

;; ═══════════════════════════════════════════════════════════════
;; Part 6 — The Name Between the Cherubim
;; ═══════════════════════════════════════════════════════════════

(defn part-6-the-name []
  (println "\n══════════════════════════════════════════════")
  (println "Part 6: The Name Between the Cherubim")
  (println "══════════════════════════════════════════════\n")

  (let [yhwh (g/word-value "יהוה")]
    (println (format "  יהוה = %d = %s" yhwh (str (factorize yhwh))))
    (println (format "  26 = 2 × 13. Two loves. Two thirteens."))
    (println)
    (println "  The Name's structure: י - ה - ו - ה")
    (println "    Two ה's face each other across the ו (vav, the connector)")
    (println "    Like two cherubim facing each other across the mercy seat")
    (println "    The ו holds the space between — value 6, the center letter of the Torah")
    (println)
    (println "  Each cherub carries 13 (love).")
    (println "  Together they see 26 (the Name).")
    (println "  The Name exists BETWEEN them — where both readings converge.")
    (println)
    (println "  God speaks from above the mercy seat, from BETWEEN the two cherubim.")
    (println "  (Exodus 25:22)")
    (println)
    (println "  The Name is not on either side. It is what happens when love faces love.")))

;; ═══════════════════════════════════════════════════════════════
;; Part 7 — Summary of the physical model
;; ═══════════════════════════════════════════════════════════════

(defn part-7-summary []
  (println "\n══════════════════════════════════════════════")
  (println "Part 7: The Physical Model")
  (println "══════════════════════════════════════════════\n")

  (println "  Aaron enters the Holy of Holies. The layers, top to bottom:")
  (println)
  (println "    1. God's voice (from above, between the cherubim)")
  (println "    2. Aaron's heart")
  (println "    3. Inner breastplate layer")
  (println "    4. Urim (אורים = 257, prime) + Thummim (תמים = 490 = 7²×10)")
  (println "    5. Outer breastplate layer")
  (println "    6. 12 stones with all 22 letters (face down)")
  (println "    7. Torah scroll / parchment")
  (println "    8. Mercy seat — pure gold (mirror)")
  (println "    9. Ark (ארון = 257 = אורים — same value as Urim)")
  (println "   10. Stone tablets inside (אבן = 53 = גן = garden)")
  (println)
  (println "  The scroll is between the cherubim on the mercy seat.")
  (println "  The gold surface is a mirror.")
  (println "  The Urim (light) selects. The gold reflects (reverses).")
  (println "  The Thummim (perfection, 7²×10) reorders what the mirror reversed.")
  (println)
  (println "  He passes through the veil (פרכת = 700) to find the mercy seat (כפרת = 700).")
  (println "  Same letters rearranged. He opens the ark (= Urim) and finds stone (= garden)."))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 082: The Reading Machine")
  (println "  Three readers, one grid, same letters, different truths")
  (println "═══════════════════════════════════════════════════════")

  (part-1-irreducible)
  (part-2-anagram-pairs)
  (part-3-veil-mercy-seat)
  (part-4-three-readers)
  (part-5-eli-hannah)
  (part-6-the-name)
  (part-7-summary)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  Light selects. The mirror reverses. Perfection reorders.")
  (println "═══════════════════════════════════════════════════════"))
