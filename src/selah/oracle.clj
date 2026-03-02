(ns selah.oracle
  "The breastplate oracle — forward and reverse query engine.

   Given a word (reverse): which stones light up? Which reader sees it?
   Given lit letters (forward): what can each reader see?

   Yoma 73b: letters light up, the priest arranges them.
   Eli read שכרה (drunk). The correct reading was כשרה (like Sarah).
   Same letters, different arrangement. The oracle ranks the rare reading higher."
  (:require [selah.gematria :as g]
            [selah.dict :as dict]))

;; ── The 72-letter breastplate grid ────────────────────────────

(def stone-data
  "12 stones, 72 letters, 4x3 grid: [stone-num letters row col].
   Letters flow continuously: patriarchs + 12 tribes + שבטי ישרון."
  [[1  "אברהםי"  1 1]   [2  "צחקיעק"  1 2]   [3  "בראובן"  1 3]
   [4  "שמעוןל"  2 1]   [5  "וייהוד"  2 2]   [6  "הדןנפת"  2 3]
   [7  "ליגדאש"  3 1]   [8  "ריששכר"  3 2]   [9  "זבולןי"  3 3]
   [10 "וסףבני"  4 1]   [11 "מיןשבט"  4 2]   [12 "יישרון"  4 3]])

(def stone-letters
  "Stone number -> vec of characters."
  (into {} (map (fn [[s l _ _]] [s (vec l)]) stone-data)))

(def stone-row (into {} (map (fn [[s _ r _]] [s r]) stone-data)))
(def stone-col (into {} (map (fn [[s _ _ c]] [s c]) stone-data)))

(def stone-tribe
  "Stone number -> primary name carried on the stone."
  {1 "Abraham" 2 "Isaac/Jacob" 3 "Reuben" 4 "Simeon"
   5 "Judah" 6 "Dan" 7 "Gad" 8 "Issachar"
   9 "Zebulun" 10 "Joseph" 11 "Benjamin" 12 "Yeshurun"})

(def letter-index
  "Letter -> all [stone pos] positions where it appears on the grid."
  (reduce (fn [m [s letters _ _]]
            (reduce-kv (fn [m2 i ch] (update m2 ch (fnil conj []) [s i]))
                       m (vec letters)))
          {} stone-data))

(defn letter-at
  "Character at position [stone index]."
  [[s i]]
  ((stone-letters s) i))

;; ── Three readers — three traversal orders ────────────────────
;;
;; Aaron:  looks down, Hebrew R->L within rows, top->bottom
;; Right cherub: columns R->L (nearest first), top->bottom
;; Left cherub:  columns L->R (nearest first), bottom->top
;;
;; Perspective: breastplate faces God on the mercy seat.
;; Col 3 = God's right hand. Col 1 = God's left.

(defn read-key
  "Sort key for a position under a given reader's traversal."
  [reader [s i]]
  (let [r (stone-row s) c (stone-col s)]
    (case reader
      :aaron [r (- c) i]
      :right [(- c) r i]
      :left  [c (- r) i])))

(defn read-positions
  "Read a set of positions in the order a given reader would see them."
  [reader positions]
  (->> positions (sort-by #(read-key reader %)) (map letter-at) (apply str)))

;; ── Reverse direction: answer -> illumination ─────────────────

(defn illumination-sets
  "All distinct position-sets whose letters are the multiset of the word.
   These are the ways the grid can light up to contain these letters."
  [word]
  (let [chars (vec word)
        n     (count chars)
        cands (mapv #(get letter-index %) chars)
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

(defn preimage
  "Given a word, find all (reader, position-set) pairs that produce it.
   Returns seq of {:reader :positions :stones}."
  [word]
  (let [chars (vec word)
        n     (count chars)
        cands (mapv #(get letter-index %) chars)
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

;; ── Anagrams — frequency-map comparison, no permutation blow-up ──

(defn anagrams
  "All known dictionary words that are anagrams of the given letters.
   O(|dict|) — compares frequency maps, no permutation explosion."
  [letters]
  (let [target (frequencies (seq letters))]
    (for [w (dict/words)
          :when (= target (frequencies (seq w)))]
      {:word w :meaning (dict/translate w)})))

;; ── Reverse query: ask ────────────────────────────────────────

(defn ask
  "Reverse query: given a word (the answer), find its oracle pre-image.
   Returns {:word :meaning :gv :illumination-count :by-reader :total-readings
            :first-illumination :anagrams :readable?}."
  [word]
  (let [ilsets (illumination-sets word)
        hits   (preimage word)
        alts   (distinct (anagrams word))
        by-reader (fn [r] (count (filter #(= r (:reader %)) hits)))]
    {:word word
     :meaning (dict/translate word)
     :gv (g/word-value word)
     :illumination-count (count ilsets)
     :first-illumination (first ilsets)
     :by-reader {:aaron (by-reader :aaron)
                 :right (by-reader :right)
                 :left  (by-reader :left)}
     :total-readings (count hits)
     :anagrams (vec (remove #(= (:word %) word) alts))
     :readable? (pos? (count hits))}))

;; ── Forward direction: lit letters -> readings ────────────────

(defn readings
  "Given a position-set, what does each reader see?"
  [positions]
  {:aaron (read-positions :aaron positions)
   :right (read-positions :right positions)
   :left  (read-positions :left positions)})

(defn forward
  "Forward query: given lit letters, what can each reader see?
   Returns all readings ranked by rarity (fewest readings first).
   The Hannah principle: the rare reading is the correct one."
  [letters]
  (let [ilsets (illumination-sets letters)
        ;; For each illumination, get what each reader sees
        all-readings (mapcat (fn [pset]
                               (for [reader [:aaron :right :left]]
                                 {:reader reader
                                  :word (read-positions reader pset)
                                  :positions pset}))
                             ilsets)
        ;; Group by word, count readings per word
        word-counts (->> all-readings
                         (group-by :word)
                         (map (fn [[w rs]]
                                {:word w
                                 :reading-count (count rs)
                                 :readers (set (map :reader rs))
                                 :meaning (dict/translate w)
                                 :known? (dict/known? w)
                                 :gv (g/word-value w)})))
        ;; Separate known from unknown, sort by rarity
        known (vec (sort-by :reading-count (filter :known? word-counts)))
        unknown (vec (take 20 (sort-by :reading-count (remove :known? word-counts))))
        ;; Dictionary anagrams
        anagram-hits (vec (distinct (anagrams letters)))
        ;; Sample: first illumination readings
        sample (when (seq ilsets)
                 (readings (first ilsets)))]
    {:letters letters
     :illumination-count (count ilsets)
     :total-readings (count all-readings)
     :known-words known
     :unknown-words unknown
     :anagrams anagram-hits
     :sample-readings sample}))

;; ── Question: many-to-many coincidence ────────────────────────

(defn- word-stones
  "Set of stones lit by any illumination of this word."
  [word]
  (let [ilsets (illumination-sets word)]
    (set (mapcat #(map first %) ilsets))))

(defn question
  "Ask a multi-word question. Each word is illuminated independently.
   Coincidences in the readings reveal the oracle's answer —
   same output from different inputs, like co-fibers in the preimage."
  [words]
  (let [;; Ask each word independently
        per-word (mapv (fn [w]
                         (let [a (ask w)
                               f (forward w)
                               stones (word-stones w)]
                           {:input w
                            :meaning (dict/translate w)
                            :gv (g/word-value w)
                            :readable? (:readable? a)
                            :illumination-count (:illumination-count a)
                            :total-readings (:total-readings a)
                            :by-reader (:by-reader a)
                            :stones stones
                            :known-words (:known-words f)}))
                       words)

        ;; Collect all known readings with their source word
        all-readings (mapcat (fn [{:keys [input known-words]}]
                               (map #(assoc % :source input) known-words))
                             per-word)

        ;; Group by output word — count how many input words produce each reading
        by-output (->> all-readings
                       (group-by :word)
                       (map (fn [[w entries]]
                              {:word w
                               :meaning (:meaning (first entries))
                               :gv (:gv (first entries))
                               :sources (vec (distinct (map :source entries)))
                               :source-count (count (distinct (map :source entries)))
                               :total-readings (reduce + (map :reading-count entries))}))
                       (sort-by (juxt #(- (:source-count %))
                                      :total-readings)))

        ;; Set operations on readings
        ;; Union: all distinct readings (already in by-output)
        ;; Intersection: readings produced by more than one input word
        coincidences (vec (filter #(> (:source-count %) 1) by-output))

        ;; Difference: readings unique to each input word
        unique-per-word (into {}
                          (map (fn [w]
                                 [w (vec (filter #(and (= 1 (:source-count %))
                                                       (= w (first (:sources %))))
                                                 by-output))])
                               words))

        ;; Stone set operations
        all-stone-sets (map :stones per-word)
        stone-union (when (seq all-stone-sets)
                      (apply clojure.set/union all-stone-sets))
        stone-intersection (when (and (seq all-stone-sets)
                                      (every? seq all-stone-sets))
                             (apply clojure.set/intersection all-stone-sets))
        stone-unique (into {}
                       (map (fn [{:keys [input stones]}]
                              [input (clojure.set/difference
                                       stones
                                       (apply clojure.set/union
                                              (map :stones (remove #(= input (:input %))
                                                                   per-word))))])
                            per-word))

        ;; Unreadable words in the question
        unreadable (vec (filter (complement :readable?) per-word))]

    {:words words
     :per-word per-word
     :all-readings (vec by-output)
     :coincidences coincidences
     :unique-per-word unique-per-word
     :stones {:union stone-union
              :intersection stone-intersection
              :unique stone-unique}
     :unreadable unreadable}))
