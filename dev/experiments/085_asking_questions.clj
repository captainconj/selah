(ns experiments.085-asking-questions
  "The reading machine — asking questions of the breastplate.

   Given a word (the answer), find its pre-image: which stones light up,
   which reader produces it, what else the same letters could spell.

   Yoma 73b: letters light up, the priest arranges them.
   Eli read שכרה (drunk). Correct: כשרה (like Sarah).
   Same letters, different arrangement.

   FINDINGS:
   - כבש (lamb): 4 readings, ALL from one cherub only (col 3→1). 0 Aaron, 0 other.
     When breastplate faces God on mercy seat, col 3 = God's right hand.
     The lamb is readable only from the right hand of God.
   - שכב (lie down): 30 readings from all three. Everyone sees the lamb lie down.
   - אל/לא: same illumination → Aaron and God's-right read אל (God),
     God's-left reads לא (not). Same light, different word.
   - יהוה: 231 illumination patterns. God's-left reads it 31 times (= אל = לא).
   - שכרה (drunk) has 21 readings (easy). כשרה (like Sarah) has 2 (rare).
     Eli's error was the easy reading. Correct answer was the rare one.
   - שלום (peace) and כפרת (mercy seat) CANNOT be read at all.
     Peace is unproducible. The mercy seat is beyond the breastplate.
   - אהבה (love) CAN be read: 14 patterns. The silent word speaks through the oracle.
   - אמת (truth): only 3 patterns. אדם (man): only 2. Truth and man are rare.
   - ברית (covenant): 65 patterns. Covenant is everywhere.
   - כ only on stone 8 (Issachar = 'there is reward'). Every word with כ passes through reward."
  (:require [selah.gematria :as g]
            [clojure.string :as str]))

;; ═══════════════════════════════════════════════════════════════
;; The 72-letter breastplate grid
;; ═══════════════════════════════════════════════════════════════

(def stone-data
  "12 stones: [stone-num letters row col]"
  [[1  "אברהםי"  1 1]   [2  "צחקיעק"  1 2]   [3  "בראובן"  1 3]
   [4  "שמעוןל"  2 1]   [5  "וייהוד"  2 2]   [6  "הדןנפת"  2 3]
   [7  "ליגדאש"  3 1]   [8  "ריששכר"  3 2]   [9  "זבולןי"  3 3]
   [10 "וסףבני"  4 1]   [11 "מיןשבט"  4 2]   [12 "יישרון"  4 3]])

(def stone-letters (into {} (map (fn [[s l _ _]] [s (vec l)]) stone-data)))
(def stone-row     (into {} (map (fn [[s _ r _]] [s r]) stone-data)))
(def stone-col     (into {} (map (fn [[s _ _ c]] [s c]) stone-data)))

(defn letter-at [[s i]] ((stone-letters s) i))

;; Letter → all [stone pos] where it appears
(def letter-index
  (reduce (fn [m [s letters _ _]]
            (reduce-kv (fn [m2 i ch] (update m2 ch (fnil conj []) [s i]))
                       m (vec letters)))
          {} stone-data))

;; ═══════════════════════════════════════════════════════════════
;; Three readers — three traversal orders
;; ═══════════════════════════════════════════════════════════════
;;
;; Aaron:  looks down, Hebrew R→L within rows, top→bottom
;; Right cherub: columns R→L (nearest first), top→bottom
;; Left cherub:  columns L→R (nearest first), bottom→top

(defn read-key [reader [s i]]
  (let [r (stone-row s) c (stone-col s)]
    (case reader
      :aaron  [r (- c) i]
      :right  [(- c) r i]
      :left   [c (- r) i])))

(defn read-positions [reader positions]
  (->> positions (sort-by #(read-key reader %)) (map letter-at) (apply str)))

;; ═══════════════════════════════════════════════════════════════
;; Known words for anagram checking
;; ═══════════════════════════════════════════════════════════════

(def known-words
  {"את" "aleph-tav" "אל" "God" "לא" "not" "כל" "all" "על" "upon"
   "בן" "son" "אב" "father" "אם" "mother" "דם" "blood" "חי" "living"
   "יד" "hand" "לב" "heart" "עם" "people" "שם" "name" "רע" "evil"
   "אש" "fire" "גם" "also" "גר" "sojourner" "בר" "grain/son"
   "רב" "many" "כן" "thus" "חן" "grace" "עז" "strong" "אח" "brother"
   "אור" "light" "יום" "day" "שמר" "guard" "אמת" "truth" "קדש" "holy"
   "אדם" "man" "כהן" "priest" "מלך" "king" "דבר" "word" "ברך" "bless"
   "ברא" "create" "באר" "well" "כבש" "lamb" "שכב" "lie down"
   "חרב" "sword" "ברח" "flee" "שבע" "seven" "אהל" "tent" "לקח" "take"
   "חלק" "portion" "ירש" "inherit" "ישר" "upright" "שער" "gate"
   "קרא" "call" "ראה" "see" "ירא" "fear" "אהב" "love"
   "יהוה" "YHWH" "והיה" "and-it-shall-be" "ויהי" "and-it-was"
   "תורה" "Torah" "משה" "Moses" "אהבה" "love(n)" "בינה" "understanding"
   "ברית" "covenant" "שלום" "peace" "כפרת" "mercy-seat" "פרכת" "veil"
   "כרוב" "cherub" "שכרה" "drunk" "כשרה" "like-Sarah"
   "שרה" "Sarah" "רחל" "Rachel" "רחם" "mercy"})

;; ═══════════════════════════════════════════════════════════════
;; Pre-image: answer → illumination patterns
;; ═══════════════════════════════════════════════════════════════

(defn preimage
  "Given a word, find all (reader, position-set) pairs that produce it.
   Returns seq of {:reader :positions :stones}."
  [word]
  (let [chars (vec word)
        n     (count chars)
        cands (mapv #(letter-index %) chars)
        seen  (atom #{})
        hits  (atom [])]
    (when (every? seq cands)
      (letfn [(go [i chosen used]
                (if (= i n)
                  (let [pset (set chosen)]
                    (doseq [reader [:aaron :right :left]]
                      (let [k [reader pset]]
                        (when (and (not (@seen k))
                                   (= word (read-positions reader pset)))
                          (swap! seen conj k)
                          (swap! hits conj {:reader reader :positions pset
                                            :stones (mapv first (sort-by #(read-key reader %) pset))})))))
                  (doseq [pos (cands i)]
                    (when-not (used pos)
                      (go (inc i) (conj chosen pos) (conj used pos))))))]
        (go 0 [] #{})))
    @hits))

(defn illumination-sets
  "All distinct position-sets whose letters are the multiset of word."
  [word]
  (let [chars (vec word)
        n     (count chars)
        cands (mapv #(letter-index %) chars)
        seen  (atom #{})
        result (atom [])]
    (when (every? seq cands)
      (letfn [(go [i chosen used]
                (if (= i n)
                  (let [pset (set chosen)]
                    (when-not (@seen pset)
                      (swap! seen conj pset)
                      (swap! result conj pset)))
                  (doseq [pos (cands i)]
                    (when-not (used pos)
                      (go (inc i) (conj chosen pos) (conj used pos))))))]
        (go 0 [] #{})))
    @result))

;; ═══════════════════════════════════════════════════════════════
;; Anagram engine — what else can the same letters spell?
;; ═══════════════════════════════════════════════════════════════

(defn permutations [v]
  (if (<= (count v) 1) [v]
    (distinct
      (mapcat (fn [i]
                (map #(into [(v i)] %)
                     (permutations (into (subvec v 0 i) (subvec v (inc i))))))
              (range (count v))))))

(defn anagram-words
  "All known words that can be spelled from the given letters."
  [letters]
  (let [perms (permutations (vec letters))]
    (for [p perms
          :let [w (apply str p)]
          :when (known-words w)]
      {:word w})))

;; ═══════════════════════════════════════════════════════════════
;; The ask function — the query interface
;; ═══════════════════════════════════════════════════════════════

(defn ask
  "Ask the breastplate a question. The answer is the word.
   Shows: which letters light, which stones, what each reader sees,
   and what else the same letters could spell."
  [word]
  (let [chars  (vec word)
        gv     (g/word-value word)
        ilsets (illumination-sets word)
        hits   (preimage word)
        alts   (distinct (anagram-words chars))]

    (printf "\n  ┌─ ASK: %s (%s) = %d\n" word (or (known-words word) "?") gv)
    (println "  │")

    ;; Which letters, how many positions each
    (printf "  │  Letters needed:")
    (doseq [ch (distinct chars)]
      (printf " %s×%d" ch (count (filter #{ch} chars))))
    (println)

    (printf "  │  Grid positions: ")
    (doseq [ch (distinct chars)]
      (let [positions (letter-index ch)]
        (printf " %s→%d stones" ch (count (set (map first positions))))))
    (println)
    (println "  │")

    ;; Illumination count
    (printf "  │  Illumination patterns: %d\n" (count ilsets))
    (printf "  │  Readable by Aaron: %d  Right: %d  Left: %d\n"
            (count (filter #(= :aaron (:reader %)) hits))
            (count (filter #(= :right (:reader %)) hits))
            (count (filter #(= :left (:reader %)) hits)))
    (println "  │")

    ;; Show a few representative patterns
    (when (seq hits)
      (println "  │  Representative readings:")
      (doseq [{:keys [reader positions stones]} (take 5 hits)]
        (printf "  │    %s → stones %s → %s\n"
                (name reader)
                (str/join "," stones)
                (str/join " " (map (fn [p] (format "%s(%d:%d)" (letter-at p) (first p) (second p)))
                                   (sort-by #(read-key reader %) positions))))))

    ;; Show what each reader sees from the first illumination
    (when (seq ilsets)
      (let [il (first ilsets)]
        (println "  │")
        (println "  │  First illumination — three readings:")
        (doseq [reader [:aaron :right :left]]
          (let [w (read-positions reader il)]
            (printf "  │    %-8s  %s  %s\n"
                    (name reader) w
                    (if-let [m (known-words w)] (str "← " m) ""))))))

    ;; Anagram words
    (when (seq alts)
      (println "  │")
      (println "  │  Same letters, other words:")
      (doseq [{:keys [word meaning]} alts]
        (printf "  │    %s = %s\n" word meaning)))

    (println "  └─")))

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — Grid inventory
;; ═══════════════════════════════════════════════════════════════

(defn part-1-grid []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: The Breastplate Grid — 72 Letters")
  (println "══════════════════════════════════════════════\n")

  (println "       Col 1          Col 2          Col 3")
  (println "       ─────          ─────          ─────")
  (doseq [row [1 2 3 4]]
    (printf "  R%d " row)
    (doseq [col [1 2 3]]
      (let [s (first (filter #(and (= row (stone-row (first %)))
                                   (= col (stone-col (first %))))
                             stone-letters))]
        (printf "  S%-2d %s  " (key s) (apply str (val s)))))
    (println))

  (println "\n  Letter inventory:")
  (let [all-letters (mapcat (fn [[_ ls]] ls) (sort stone-letters))
        freqs (->> all-letters frequencies (sort-by val >))]
    (doseq [[ch cnt] freqs]
      (let [stones (set (for [[s letters _ _] stone-data
                              [i c] (map-indexed vector (vec letters))
                              :when (= c ch)] s))]
        (printf "    %s (%2d): %d positions on stones %s\n"
                ch (g/letter-value ch) cnt (str/join "," (sort stones)))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — The Eli/Hannah case
;; ═══════════════════════════════════════════════════════════════

(defn part-2-eli-hannah []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: The Eli/Hannah Case — שכרה vs כשרה")
  (println "══════════════════════════════════════════════")

  (ask "שכרה")
  (ask "כשרה")

  ;; Key observation
  (println "\n  Note: כ appears ONLY on stone 8 (Issachar).")
  (println "  Every question containing כ requires Issachar's stone to light up.")
  (println "  Issachar = יששכר = 'there is reward' = 830 = 10 × 83."))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — The Name on the grid
;; ═══════════════════════════════════════════════════════════════

(defn part-3-name []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: The Name — יהוה")
  (println "══════════════════════════════════════════════")

  (ask "יהוה")
  (ask "והיה"))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — Anagram pairs from the grid
;; ═══════════════════════════════════════════════════════════════

(defn part-4-pairs []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: Anagram Pairs — the Grid's Vocabulary")
  (println "══════════════════════════════════════════════")

  (ask "כבש")
  (ask "שכב")
  (ask "ברא")
  (ask "באר")
  (ask "אל")
  (ask "לא"))

;; ═══════════════════════════════════════════════════════════════
;; Part 5 — What the grid can and cannot say
;; ═══════════════════════════════════════════════════════════════

(defn part-5-limits []
  (println "\n══════════════════════════════════════════════")
  (println "Part 5: What the Grid Can and Cannot Say")
  (println "══════════════════════════════════════════════\n")

  (let [test-words ["אהבה" "תורה" "אמת" "שלום" "ברית" "כפרת" "כרוב" "משה" "אדם"]
        results (for [w test-words]
                  (let [hits (preimage w)]
                    {:word w :gv (g/word-value w)
                     :hits (count hits)
                     :can? (pos? (count hits))}))]
    (printf "  %-6s %5s %6s\n" "Word" "GV" "Found?")
    (printf "  %-6s %5s %6s\n" "──────" "─────" "──────")
    (doseq [{:keys [word gv hits can?]} results]
      (printf "  %-6s %5d %4d %s\n"
              word gv hits (if can? "" "  ◀ cannot be read")))))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 085: Asking Questions")
  (println "  The pre-image of the reading machine")
  (println "═══════════════════════════════════════════════════════")

  (part-1-grid)
  (part-2-eli-hannah)
  (part-3-name)
  (part-4-pairs)
  (part-5-limits)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  The answer determines which letters light up.")
  (println "  The reader determines which word you see.")
  (println "═══════════════════════════════════════════════════════"))
