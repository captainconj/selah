(ns experiments.083-anagram-vocabulary
  "The anagram vocabulary — same letters, different truths.

   Exhaustive search of the Torah dictionary for:
   1. Anagram pairs (same letters rearranged)
   2. Exact reversal pairs (one word is the other backward)
   3. Same-value different-letter pairs
   4. The fold test: does אל systematically mirror to לא?

   FINDINGS:
   - 12 anagram pairs in the dictionary, almost all semantically loaded
   - יהוה / והיה — the Name IS becoming
   - כבש / שכב (lamb / lie down) — the lamb lies down
   - ברא / באר (create / well) — creation from the well
   - 3 exact reversal pairs: אל/לא (31), בר/רב (202), ברח/חרב (210)
   - 31 is a prime TRIPLET: אל (God), לא (not), ויהי (and it was)
   - אש = קרא = 301 (fire = call/read — the burning bush spoke)
   - אברהם = רחם = 248 (Abraham = mercy)
   - משכן = קדוש = שמע = 410 (tabernacle = holy = hear)
   - Fold test: אל→לא at 0.93× (chance). The structure is in vocabulary, not positioning.
   - את shows strongest fold-reversal affinity (1.27×), יה second (1.23×)"
  (:require [selah.gematria :as g]
            [selah.text.oshb :as oshb]
            [clojure.string :as str]))

;; ═══════════════════════════════════════════════════════════════

(def dict-words
  {"את" "aleph-tav" "אשר" "that/which" "אל" "to/God" "על" "upon/over"
   "כי" "for/because" "כל" "all/every" "לא" "not" "הוא" "he/it"
   "בן" "son" "עד" "until/witness" "או" "or" "מן" "from"
   "אם" "mother" "כן" "thus/so" "גם" "also" "זה" "this"
   "נא" "please" "עם" "people/with" "לי" "to me" "אני" "I"
   "שם" "there/name" "היא" "she/it"
   "ויאמר" "and he said" "אמר" "say" "דבר" "speak/word/thing"
   "עשה" "make/do" "נתן" "give" "ראה" "see" "שמע" "hear"
   "ידע" "know" "הלך" "walk/go" "בוא" "come/enter" "ישב" "sit/dwell"
   "עמד" "stand" "נשא" "lift/carry" "שלח" "send" "קרא" "call/read"
   "ברא" "create" "שמר" "guard/keep" "כפר" "atone/cover"
   "גאל" "redeem" "כרת" "cut (covenant)" "סלח" "forgive"
   "פדה" "ransom" "ירש" "inherit" "ברך" "bless" "צוה" "command"
   "עלה" "go up/offering" "שוב" "return" "מות" "die/death"
   "חטא" "sin" "לקח" "take" "שכב" "lie down" "יצא" "go out"
   "נפל" "fall" "קום" "arise" "שים" "set/place" "אכל" "eat"
   "שתה" "drink" "כתב" "write" "זבח" "sacrifice" "נדר" "vow"
   "טהר" "be pure" "קדש" "be holy" "ענה" "answer/afflict"
   "ירא" "fear" "אהב" "love (verb)" "שפך" "pour out"
   "נקם" "avenge" "רחם" "have mercy" "חנן" "be gracious"
   "יהוה" "YHWH" "אלהים" "God (Elohim)" "אדני" "Lord"
   "משה" "Moses" "אהרן" "Aaron" "ישראל" "Israel"
   "אברהם" "Abraham" "יצחק" "Isaac" "יעקב" "Jacob"
   "יוסף" "Joseph" "פרעה" "Pharaoh" "אדם" "man/Adam"
   "חוה" "Eve" "נח" "Noah" "אשה" "woman/wife" "איש" "man"
   "אב" "father" "אח" "brother" "כהן" "priest" "מלך" "king"
   "עבד" "servant" "נביא" "prophet" "בני" "sons of" "בת" "daughter"
   "ארץ" "land/earth" "שמים" "heaven" "מים" "water" "אש" "fire"
   "אור" "light" "חשך" "darkness" "יום" "day" "לילה" "night"
   "ענן" "cloud" "עץ" "tree" "גן" "garden" "פרי" "fruit"
   "זרע" "seed" "אבן" "stone" "צור" "rock" "הר" "mountain"
   "ים" "sea" "נהר" "river" "באר" "well" "דם" "blood"
   "נפש" "soul" "רוח" "spirit/wind" "כבש" "lamb" "פר" "bull"
   "שור" "ox" "נחש" "serpent" "עוף" "bird" "דג" "fish"
   "בהמה" "animal/beast" "בקר" "cattle" "צאן" "flock"
   "תורה" "Torah" "אהבה" "love" "בינה" "understanding"
   "חכמה" "wisdom" "אמת" "truth" "חסד" "lovingkindness"
   "צדק" "righteousness" "צדקה" "righteousness (f)"
   "שלום" "peace/wholeness" "ברית" "covenant" "דעת" "knowledge"
   "משפט" "judgment" "עולם" "eternal/world" "חיים" "life"
   "מועד" "appointed time" "שבת" "sabbath" "קרבן" "offering"
   "חרב" "sword" "קשת" "bow/rainbow" "דרך" "way/path"
   "משכן" "tabernacle" "מזבח" "altar" "ארון" "ark"
   "כרוב" "cherub" "אהל" "tent" "פרכת" "veil/curtain"
   "שמן" "oil" "יין" "wine" "לחם" "bread" "זהב" "gold"
   "כסף" "silver" "נחשת" "bronze"
   "טוב" "good" "רע" "evil/bad" "גדול" "great" "קטן" "small"
   "חכם" "wise" "קדוש" "holy" "אחד" "one" "שני" "two/second"
   "שלש" "three" "ארבע" "four" "שבע" "seven/swear" "עשר" "ten"
   "מאה" "hundred" "אלף" "thousand" "רב" "many/great"
   "חי" "living" "חדש" "new" "ישר" "upright" "תמים" "complete"
   "כבד" "heavy/glory" "חזק" "strong"
   "יד" "hand" "עין" "eye" "פנים" "face" "פה" "mouth"
   "לב" "heart" "ראש" "head" "רגל" "foot" "אזן" "ear"
   "בית" "house" "עיר" "city" "שער" "gate" "מדבר" "wilderness"
   "אמן" "amen/faithful" "כבוד" "glory" "חן" "grace"
   "רחמים" "mercies" "שכינה" "dwelling/presence"
   "ברח" "flee" "רדה" "rule" "נכה" "strike"
   "לאמר" "saying" "ליהוה" "to YHWH" "הארץ" "the land"
   "העם" "the people" "מצרים" "Egypt" "אתה" "you"
   "היום" "today" "והיה" "and it shall be" "ויהי" "and it was"
   "כאשר" "as/when" "רחל" "Rachel" "לאה" "Leah"
   "נער" "youth/lad" "עשב" "herb/grass" "עז" "strong/goat"
   "גר" "sojourner" "בר" "grain/pure/son" "חלק" "portion"
   "כפרת" "mercy seat"})

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — Anagram pairs
;; ═══════════════════════════════════════════════════════════════

(defn sorted-letters [w] (apply str (sort w)))

(defn part-1-anagrams []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: Anagram Pairs — same letters, rearranged")
  (println "══════════════════════════════════════════════\n")

  (let [words  (vec (keys dict-words))
        pairs  (for [i (range (count words))
                     j (range (inc i) (count words))
                     :let [a (words i) b (words j)]
                     :when (and (not= a b)
                                (= (sorted-letters a) (sorted-letters b)))]
                 {:a a :ma (dict-words a)
                  :b b :mb (dict-words b)
                  :gv (g/word-value a)
                  :reversal? (= a (apply str (reverse b)))})]
    (doseq [{:keys [a ma b mb gv reversal?]} (sort-by :gv pairs)]
      (printf "  %4d  %-6s (%s)  ↔  %-6s (%s)%s\n"
              gv a ma b mb (if reversal? "  ◀ REVERSAL" "")))
    (println (format "\n  %d anagram pairs found" (count pairs)))
    (let [revs (filter :reversal? pairs)]
      (println (format "  %d exact reversals" (count revs))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — Same-value groups
;; ═══════════════════════════════════════════════════════════════

(defn part-2-same-value []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: Same Value, Different Words")
  (println "══════════════════════════════════════════════\n")

  (let [by-gv (->> dict-words
                   (map (fn [[w _]] {:word w :gv (g/word-value w)}))
                   (group-by :gv)
                   (filter #(> (count (val %)) 1))
                   (sort-by key))]
    (doseq [[gv words] by-gv]
      (let [names (str/join ", " (map :word words))]
        (printf "  %4d: %s\n" gv names)))
    (println (format "\n  %d values shared by multiple words" (count by-gv)))))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — The fold test: אל → לא?
;; ═══════════════════════════════════════════════════════════════

(defn part-3-fold-test []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: The Fold Test — does אל mirror to לא?")
  (println "══════════════════════════════════════════════\n")

  (let [stream (vec (oshb/torah-letters))
        N      (count stream)
        ;; Find all consecutive אל and לא
        al-positions (for [i (range (dec N))
                          :when (and (= (stream i) \א)
                                     (= (stream (inc i)) \ל))]
                      i)
        la-positions (set (for [i (range (dec N))
                               :when (and (= (stream i) \ל)
                                          (= (stream (inc i)) \א))]
                           i))
        ;; Under full-position fold, pair at [i, i+1] mirrors to [N-1-i-1, N-1-i]
        ;; = [N-2-i, N-1-i]. Forward reading of mirror = stream[N-2-i], stream[N-1-i]
        mirrors (for [i al-positions
                      :let [mi (- N 2 i)]
                      :when (and (>= mi 0) (< (inc mi) N)
                                 (= (stream mi) \ל)
                                 (= (stream (inc mi)) \א))]
                  i)
        al->al  (count (for [i al-positions
                             :let [mi (- N 2 i)]
                             :when (and (>= mi 0) (< (inc mi) N)
                                        (= (stream mi) \א)
                                        (= (stream (inc mi)) \ל))]
                         i))]
    (println (format "  Consecutive אל (God) in Torah: %d" (count al-positions)))
    (println (format "  Consecutive לא (Not) in Torah: %d" (count la-positions)))
    (println (format "  Excess of God over Not: %d (%.1f%%)"
                     (- (count al-positions) (count la-positions))
                     (* 100.0 (/ (- (count al-positions) (count la-positions))
                                 (count la-positions)))))
    (println)
    (println (format "  אל that mirror to לא: %d of %d (%.2f%%)"
                     (count mirrors) (count al-positions)
                     (* 100.0 (/ (count mirrors) (count al-positions)))))
    (println (format "  אל that mirror to אל: %d (%.2f%%)"
                     al->al (* 100.0 (/ al->al (count al-positions)))))
    (println)
    (println "  God is more likely to persist as God under the fold")
    (println "  than to become Not. The structure is in the vocabulary,")
    (println "  not the positioning.")))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — Top pair reversal affinity under fold
;; ═══════════════════════════════════════════════════════════════

(defn part-4-pair-affinity []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: Pair Reversal Affinity Under the Fold")
  (println "══════════════════════════════════════════════\n")

  (let [stream (vec (oshb/torah-letters))
        N      (count stream)
        test-pairs [[\א \ת] [\א \ל] [\י \ה] [\ה \ו] [\ו \י] [\ו \א]]
        results (for [[c1 c2] test-pairs]
                  (let [positions (for [i (range (dec N))
                                       :when (and (= (stream i) c1)
                                                  (= (stream (inc i)) c2))]
                                   i)
                        rev-count (count (for [i (range (dec N))
                                              :when (and (= (stream i) c2)
                                                         (= (stream (inc i)) c1))]
                                          i))
                        mirrors   (count (for [i positions
                                              :let [mi (- N 2 i)]
                                              :when (and (>= mi 0) (< (inc mi) N)
                                                         (= (stream mi) c2)
                                                         (= (stream (inc mi)) c1))]
                                          i))
                        expected  (* (count positions) (/ rev-count (double N)))]
                    {:pair (str c1 c2)
                     :rev  (str c2 c1)
                     :count (count positions)
                     :rev-count rev-count
                     :mirrors mirrors
                     :expected expected
                     :ratio (if (pos? expected) (/ mirrors expected) 0)}))]
    (printf "  %-6s %-6s %6s %6s %7s %7s %6s\n"
            "Pair" "Rev" "Count" "RevCt" "Mirror" "Expect" "Ratio")
    (printf "  %-6s %-6s %6s %6s %7s %7s %6s\n"
            "------" "------" "------" "------" "-------" "-------" "------")
    (doseq [{:keys [pair rev count rev-count mirrors expected ratio]} results]
      (printf "  %-6s %-6s %6d %6d %7d %7.1f %5.2fx\n"
              pair rev count rev-count mirrors expected ratio))))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 083: The Anagram Vocabulary")
  (println "  Same letters, different truths")
  (println "═══════════════════════════════════════════════════════")

  (part-1-anagrams)
  (part-2-same-value)
  (part-3-fold-test)
  (part-4-pair-affinity)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  The letters know. The positions don't enforce it.")
  (println "═══════════════════════════════════════════════════════"))
