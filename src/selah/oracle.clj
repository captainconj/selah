(ns selah.oracle
  "The breastplate oracle — forward and reverse query engine.

   Given a word (reverse): which stones light up? Which reader sees it?
   Given lit letters (forward): what can each reader see?

   Yoma 73b: letters light up, the priest arranges them.
   Eli read שכרה (drunk). The correct reading was כשרה (like Sarah).
   Same letters, different arrangement. The oracle ranks the rare reading higher."
  (:require [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

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

;; ── Four readers — four traversal orders ─────────────────────
;;
;; Aaron:  looks down at his chest, rows R->L, top->bottom
;; God:    faces Aaron from mercy seat (mirrored), rows L->R, bottom->top
;; Right cherub: columns R->L (nearest first), top->bottom
;; Left cherub:  columns L->R (nearest first), bottom->top
;;
;; Perspective: breastplate faces God on the mercy seat.
;; Col 3 = God's right hand. Col 1 = God's left.
;; God sees the grid mirrored: Aaron's left = God's right.
;;
;; The four readers = four orientations of the 4x3 grid.
;; YHWH: Yod(10)=right, He(5)=left, Vav(6)=Aaron, He(5)=God.

(defn read-key
  "Sort key for a position under a given reader's traversal."
  [reader [s i]]
  (let [r (stone-row s) c (stone-col s)]
    (case reader
      :aaron [r (- c) i]
      :god   [(- r) c i]
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
                    (doseq [reader [:aaron :god :right :left]]
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
                 :god   (by-reader :god)
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
   :god   (read-positions :god positions)
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
                               (for [reader [:aaron :god :right :left]]
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

;; ── Level 2 Thummim: phrase assembly ─────────────────────────
;;
;; Given illuminated letters, find all ways to parse them into
;; dictionary words. This is what the priest does cognitively.
;; The tool provides the menu. The priest chooses.

(defn- multiset-subtract
  "Subtract freq-map b from a. Returns nil if b is not a sub-multiset of a."
  [a b]
  (reduce-kv (fn [acc ch cnt]
               (if (nil? acc) nil
                   (let [have (get acc ch 0)]
                     (cond
                       (< have cnt) nil
                       (= have cnt) (dissoc acc ch)
                       :else (assoc acc ch (- have cnt))))))
             a b))

(defn build-anagram-index
  "Build letter-frequency -> [words] index for a vocabulary set.
   Used by parse-letters for phrase assembly."
  [vocab]
  (reduce (fn [m w]
            (update m (frequencies (seq w)) (fnil conj []) w))
          {} vocab))

(def ^:private dict-anagram-index
  "Curated dictionary (239 words). Precomputed for phrase parsing."
  (delay (build-anagram-index (dict/words))))

(def ^:private torah-anagram-index
  "Full Torah vocabulary (~7,300 words). Lazy — first call builds the index."
  (delay (build-anagram-index (dict/torah-words))))

(declare voice-vocab)

(def ^:private voice-anagram-index
  "Oracle's natural voice (~2,050 words at the knee). Lazy — triggers voice computation."
  (delay (build-anagram-index (voice-vocab))))

(defn- resolve-index
  "Resolve a vocabulary spec to a cached anagram index.
   :dict = 239 curated (optimal), :voice = ~2,050 knee, :torah = ~7,300 full."
  [vocab]
  (cond
    (= vocab :torah) @torah-anagram-index
    (= vocab :voice) @voice-anagram-index
    (set? vocab)     (build-anagram-index vocab)
    (map? vocab)     (build-anagram-index (voice-vocab))
    :else            @dict-anagram-index))

(defn parse-illumination
  "Given a set of illuminated positions, find all ways to partition the
   letters into dictionary words. Returns a seq of phrase vectors.

   This is the Level 2 Thummim — cognitive assembly.
   The Ramban's 'correct order' problem, solved by enumeration.

   Options:
     :max-words   — maximum words in a phrase (default 4)
     :min-letters — minimum letters per word (default 2)
     :vocab       — :dict (239 curated, default), :torah (~7,300), or a set"
  ([positions] (parse-illumination positions {}))
  ([positions {:keys [max-words min-letters vocab] :or {max-words 4 min-letters 2 vocab :torah}}]
   (let [letters (mapv letter-at (sort positions))
         remaining (frequencies letters)
         total (count letters)
         index (resolve-index vocab)
         ;; Pre-filter: words whose letters are a sub-multiset
         candidates (->> index
                         (keep (fn [[freq ws]]
                                 (when (and (>= (reduce + (vals freq)) min-letters)
                                            (multiset-subtract remaining freq))
                                   [freq ws])))
                         vec)
         results (atom [])
         ;; Recursive backtracking
         search (fn search [rem depth path]
                  (when (empty? rem)
                    ;; All letters used — valid phrase
                    (swap! results conj (vec path)))
                  (when (and (seq rem) (< depth max-words))
                    (doseq [[freq ws] candidates]
                      (when-let [rem2 (multiset-subtract rem freq)]
                        (doseq [w ws]
                          (search rem2 (inc depth) (conj path w)))))))]
     (search remaining 0 [])
     ;; Sort: fewer words first, then alphabetically
     (->> @results
          distinct
          (map (fn [phrase]
                 {:phrase phrase
                  :words (count phrase)
                  :text (str/join " " phrase)
                  :meanings (mapv dict/translate phrase)
                  :gv (reduce + (map g/word-value phrase))
                  :letters total}))
          (sort-by (juxt :words :text))
          vec))))

(defn parse-letters
  "Given a string of Hebrew letters, find all ways to partition them into
   dictionary words. Works on raw letters — no grid positions needed.

   This is the pure cognitive Thummim: what phrases can be assembled
   from these letters? The tool provides the menu. The priest chooses.

   Options:
     :max-words   — maximum words in a phrase (default 4)
     :min-letters — minimum letters per word (default 2)
     :vocab       — :dict (239 curated, default), :torah (~7,300), or a set"
  ([letters] (parse-letters letters {}))
  ([letters {:keys [max-words min-letters vocab] :or {max-words 4 min-letters 2 vocab :torah}}]
   (let [remaining (frequencies (seq letters))
         total (count letters)
         index (resolve-index vocab)
         candidates (->> index
                         (keep (fn [[freq ws]]
                                 (when (and (>= (reduce + (vals freq)) min-letters)
                                            (multiset-subtract remaining freq))
                                   [freq ws])))
                         vec)
         results (atom [])
         search (fn search [rem depth path]
                  (when (empty? rem)
                    (swap! results conj (vec path)))
                  (when (and (seq rem) (< depth max-words))
                    (doseq [[freq ws] candidates]
                      (when-let [rem2 (multiset-subtract rem freq)]
                        (doseq [w ws]
                          (search rem2 (inc depth) (conj path w)))))))]
     (search remaining 0 [])
     (->> @results
          distinct
          (map (fn [phrase]
                 {:phrase phrase
                  :words (count phrase)
                  :text (str/join " " phrase)
                  :meanings (mapv dict/translate phrase)
                  :gv (reduce + (map g/word-value phrase))
                  :letters total}))
          (sort-by (juxt :words :text))
          vec))))

(defn thummim
  "The full Level 2 oracle: illuminate a word, then parse all possible
   phrase readings from each illumination pattern.

   Options:
     :max-illuminations — sample size (default 50)
     :max-words         — max words per phrase (default 4)
     :min-letters       — min letters per word (default 2)
     :vocab             — :dict (default), :torah, or a set

   Returns the menu. The priest chooses."
  ([word] (thummim word {}))
  ([word opts]
   (let [ilsets (illumination-sets word)
         n (count ilsets)]
     (when (pos? n)
       (let [sample-size (min n (get opts :max-illuminations 50))
             sampled (if (<= n sample-size) ilsets (take sample-size ilsets))
             all-phrases (atom #{})
             per-illumination
             (vec (map-indexed
                    (fn [i pset]
                      (let [letters (mapv letter-at (sort pset))
                            phrases (parse-illumination pset opts)
                            mech (readings pset)]
                        (doseq [p phrases]
                          (swap! all-phrases conj (:text p)))
                        {:illumination-index i
                         :positions pset
                         :letters (apply str letters)
                         :mechanical mech
                         :phrases phrases}))
                    sampled))]
         {:word word
          :meaning (dict/translate word)
          :gv (g/word-value word)
          :illumination-count n
          :sampled sample-size
          :unique-phrases (count @all-phrases)
          :illuminations per-illumination})))))

(defn thummim-menu
  "The priest's menu: all unique phrase readings for a word, ranked.
   Deduplicates across illumination patterns — just the readings.
   Sorts by word count (fewer = rarer = more signal), then alphabetically.

   Options: same as thummim — :vocab :dict|:torah, :max-words, etc."
  ([word] (thummim-menu word {}))
  ([word opts]
   (when-let [r (thummim word opts)]
     (let [all-phrases (->> (:illuminations r)
                            (mapcat :phrases)
                            (group-by :text)
                            (map (fn [[text ps]]
                                   (let [p (first ps)]
                                     (assoc p :occurrences (count ps)))))
                            (sort-by (juxt :words :text))
                            vec)]
       {:word word
        :meaning (:meaning r)
        :gv (:gv r)
        :illumination-count (:illumination-count r)
        :vocab (get opts :vocab :dict)
        :phrases all-phrases}))))

;; ── Oracle voice — stationary vocabulary ────────────────────────
;;
;; The oracle has a natural voice: the distribution of words it
;; produces across all inputs × illuminations × readers.
;; Words with high output frequency are the oracle's core vocabulary.
;; The "knee" of the cumulative distribution gives the optimal
;; vocabulary size — fidelity=0.

(def ^:private voice-cache-path "data/oracle-voice.edn")

(defn compute-oracle-voice
  "Scan all Torah words through the oracle. Count output frequencies
   across all illumination×reader combinations. Returns the oracle's
   natural vocabulary ranked by frequency.

   Expensive (~2-4 min). Result is cached to disk."
  []
  (println "[oracle] Computing oracle voice (full Torah vocabulary)...")
  (let [torah (vec (dict/torah-words))
        torah-set (set torah)
        n (count torah)
        progress (atom 0)
        ;; Count output frequencies across all inputs × illuminations × readers
        output-counts (atom {})
        _ (->> torah
               (pmap (fn [w]
                       (let [p (swap! progress inc)]
                         (when (zero? (mod p 1000))
                           (println (str "  " p "/" n "..."))))
                       (let [ilsets (illumination-sets w)]
                         (doseq [pset ilsets
                                 reader [:aaron :god :right :left]]
                           (let [out (read-positions reader pset)]
                             (when (torah-set out)
                               (swap! output-counts update out (fnil inc 0))))))))
               dorun)
        ;; Sort by frequency descending
        sorted (->> @output-counts
                    (sort-by val >)
                    vec)
        total (reduce + (map second sorted))
        ;; Build ranked vocabulary with cumulative mass
        ranked (loop [items sorted, cum 0.0, result []]
                 (if (empty? items)
                   result
                   (let [[w freq] (first items)
                         prob (/ (double freq) total)
                         cum' (+ cum prob)]
                     (recur (rest items)
                            cum'
                            (conj result {:word w :freq freq
                                          :prob (Double/parseDouble (format "%.6f" prob))
                                          :cumulative (Double/parseDouble (format "%.6f" cum'))})))))
        ;; Find the knee — where marginal gain drops below 0.01% per word
        knee-idx (or (first (keep-indexed
                              (fn [i {:keys [prob]}]
                                (when (< prob 0.0001) i))
                              ranked))
                     (count ranked))
        voice {:computed (str (java.time.LocalDate/now))
               :total-words n
               :readable-words (count (filter #(seq (illumination-sets %)) torah))
               :total-transitions total
               :vocabulary-size (count ranked)
               :knee {:index knee-idx
                      :cumulative-mass (:cumulative (nth ranked (dec knee-idx) {:cumulative 1.0}))
                      :words-above-knee knee-idx}
               :vocabulary ranked}]
    ;; Cache to disk
    (io/make-parents voice-cache-path)
    (spit voice-cache-path (pr-str voice))
    (println (str "[oracle] Voice computed: " (count ranked) " words, knee at "
                  knee-idx " (" (format "%.1f%%" (* 100.0 (:cumulative-mass (:knee voice)))) " mass)"))
    voice))

(def ^:private oracle-voice*
  "The oracle's voice — cached. Loads from disk or computes on first access."
  (delay
    (if (.exists (io/file voice-cache-path))
      (do (println "[oracle] Loading cached voice...")
          (edn/read-string (slurp voice-cache-path)))
      (compute-oracle-voice))))

(defn oracle-voice
  "The oracle's natural vocabulary, ranked by output frequency.
   Returns {:vocabulary [...] :knee {...} :total-transitions ...}"
  []
  @oracle-voice*)

(defn voice-vocab
  "The oracle's natural vocabulary — words at or above the knee of its
   output distribution (~2,050 words, 83% of probability mass).
   Returns dict ∪ voice-knee as a set of Hebrew words."
  []
  (let [voice (oracle-voice)
        ranked (:vocabulary voice)
        knee (:index (:knee voice))
        voice-words (set (map :word (take knee ranked)))]
    (set/union (dict/words) voice-words)))
