(ns experiments.092-grid-permutation
  "Grid permutation test — does the breastplate's structure matter?

   The skeptic's challenge: 091b showed four heads produce theologically
   coherent solo eigenword vocabularies. Would ANY random arrangement
   of the same 72 letters produce similar separation?

   Method:
   1. Extract the 72-letter multiset from the real grid
   2. Shuffle letters, partition into 12×6, rebuild stone-data
   3. Run the full pipeline: illumination → transitions → head matrices → eigenwords
   4. Record: eigenword counts, agreement distribution, dict overlap, lamb self-weights
   5. Repeat 100+ times. Compare real grid to null distribution.

   Also: singleton-letter control — is the lamb's reader split typical
   of all words containing its unique letter כ?"
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [selah.linalg :as la]
            [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.native :as nn]
            [clojure.string :as str]
            [clojure.set :as cset]
            [clojure.edn :as edn]))

;; ══════════════════════════════════════════════════════════════
;; Grid Abstraction — make oracle functions work on any grid
;; ══════════════════════════════════════════════════════════════

(defn extract-letters
  "All 72 letters from the real breastplate grid, in order."
  []
  (vec (mapcat (fn [[_ letters _ _]] (vec letters)) oracle/stone-data)))

(defn make-grid
  "Build a stone-data structure from a flat vec of 72 characters.
   Preserves the 4×3 layout: 12 stones, 6 letters each,
   same [stone-num row col] structure as the real grid."
  [letters-72]
  (assert (= 72 (count letters-72))
          (str "Need exactly 72 letters, got " (count letters-72)))
  (let [groups (partition 6 letters-72)
        layout [[1 1 1] [2 1 2] [3 1 3]
                [4 2 1] [5 2 2] [6 2 3]
                [7 3 1] [8 3 2] [9 3 3]
                [10 4 1] [11 4 2] [12 4 3]]]
    (mapv (fn [[stone-num row col] letters]
            [stone-num (apply str letters) row col])
          layout groups)))

(defn shuffle-grid
  "Take the real 72 letters, shuffle them, return new stone-data."
  ([] (shuffle-grid (java.util.Random.)))
  ([^java.util.Random rng]
   (let [letters (extract-letters)
         shuffled (let [arr (object-array letters)]
                    ;; Fisher-Yates
                    (loop [i (dec (alength arr))]
                      (when (> i 0)
                        (let [j (.nextInt rng (inc i))
                              tmp (aget arr i)]
                          (aset arr i (aget arr j))
                          (aset arr j tmp)
                          (recur (dec i)))))
                    (vec arr))]
     (make-grid shuffled))))

;; ── Rebuild oracle internals from arbitrary stone-data ──────

(defn build-stone-letters [sd]
  (into {} (map (fn [[s l _ _]] [s (vec l)]) sd)))

(defn build-stone-row [sd]
  (into {} (map (fn [[s _ r _]] [s r]) sd)))

(defn build-stone-col [sd]
  (into {} (map (fn [[s _ _ c]] [s c]) sd)))

(defn build-letter-index [sd]
  (reduce (fn [m [s letters _ _]]
            (reduce-kv (fn [m2 i ch] (update m2 ch (fnil conj []) [s i]))
                       m (vec letters)))
          {} sd))

(defn make-oracle
  "Build a self-contained oracle context from arbitrary stone-data.
   Returns a map of functions that mirror selah.oracle's API
   but operate on the given grid."
  [sd]
  (let [sl  (build-stone-letters sd)
        sr  (build-stone-row sd)
        sc  (build-stone-col sd)
        li  (build-letter-index sd)

        letter-at (fn [[s i]] ((sl s) i))

        read-key (fn [reader [s i]]
                   (let [r (sr s) c (sc s)]
                     (case reader
                       :aaron [r (- c) i]
                       :god   [(- r) c i]
                       :right [(- c) r i]
                       :left  [c (- r) i])))

        read-positions (fn [reader positions]
                         (->> positions
                              (sort-by #(read-key reader %))
                              (map letter-at)
                              (apply str)))

        illumination-sets
        (fn [word]
          (let [chars (vec word)
                n     (count chars)
                cands (mapv #(get li %) chars)
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

        detailed-readings
        (fn [input-word]
          (let [ilsets (illumination-sets input-word)]
            (for [pset ilsets
                  reader [:aaron :god :right :left]]
              {:output (read-positions reader pset)
               :reader reader
               :positions pset})))]

    {:stone-data sd
     :letter-index li
     :stone-letters sl
     :stone-row sr
     :stone-col sc
     :letter-at letter-at
     :read-key read-key
     :read-positions read-positions
     :illumination-sets illumination-sets
     :detailed-readings detailed-readings}))

;; ══════════════════════════════════════════════════════════════
;; Vocabulary — build once, reuse for every permutation
;; ══════════════════════════════════════════════════════════════

(defn build-dict-vocab
  "Dictionary words only (~215). Fast test vocabulary."
  []
  (vec (sort (dict/words))))

(defn build-torah-vocab
  "All unique Torah word forms. Full vocabulary."
  []
  (println "Extracting Torah vocabulary...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        all-words (atom #{})]
    (doseq [book books]
      (doseq [{:keys [text]} (oshb/book-words book)]
        (let [ws (str/split (norm/normalize text) #"\s+")]
          (doseq [w ws]
            (when (and (seq w) (every? norm/hebrew-letter? w))
              (swap! all-words conj w))))))
    (let [v (vec (sort @all-words))]
      (println (str "  " (count v) " unique word forms."))
      v)))

;; ══════════════════════════════════════════════════════════════
;; Pipeline — run the full 091b analysis on a given oracle
;; ══════════════════════════════════════════════════════════════

(defn discover-transitions
  "Scan words through a given oracle context.
   Returns {:transitions vec, :vocab vec, :word-idx map, :size int}."
  [orc words-to-scan valid-set]
  (let [detailed-readings (:detailed-readings orc)
        transitions (atom [])
        readable (atom #{})]
    (doseq [w words-to-scan]
      (let [readings (detailed-readings w)
            known (filter #(valid-set (:output %)) readings)]
        (when (seq known)
          (swap! readable conj w)
          (doseq [r known]
            (swap! transitions conj
                   {:input w :output (:output r) :reader (:reader r)})))))
    (let [all-outputs (set (map :output @transitions))
          all-words (vec (sort (distinct (concat @readable all-outputs))))
          word-idx (into {} (map-indexed (fn [i w] [w i]) all-words))]
      {:transitions (vec @transitions)
       :vocab all-words
       :word-idx word-idx
       :readable @readable
       :size (count all-words)})))

(defn discover-transitions-parallel
  "Parallel version for larger vocabularies."
  [orc words-to-scan valid-set]
  (let [detailed-readings (:detailed-readings orc)
        n (count words-to-scan)
        raw-results
        (->> words-to-scan
             (pmap (fn [w]
                     (let [readings (detailed-readings w)
                           known (filter #(valid-set (:output %)) readings)]
                       (when (seq known)
                         {:word w
                          :transitions (mapv (fn [r]
                                              {:input w
                                               :output (:output r)
                                               :reader (:reader r)})
                                            known)}))))
             (filterv some?))]
    (let [all-trans (vec (mapcat :transitions raw-results))
          readable (set (map :word raw-results))
          all-outputs (set (map :output all-trans))
          all-words (vec (sort (distinct (concat readable all-outputs))))
          word-idx (into {} (map-indexed (fn [i w] [w i]) all-words))]
      {:transitions all-trans
       :vocab all-words
       :word-idx word-idx
       :readable readable
       :size (count all-words)})))

(defn transition-stats
  "Per-pair statistics: count, by-reader breakdown."
  [{:keys [transitions]}]
  (let [by-pair (group-by (juxt :input :output) transitions)
        totals-per-input (frequencies (map :input transitions))]
    (into {}
          (map (fn [[[input output] entries]]
                 [[input output]
                  {:count (count entries)
                   :by-reader (frequencies (map :reader entries))
                   :input-total (get totals-per-input input 0)}])
               by-pair))))

(defn build-head-matrix
  "Transition matrix for a single reader."
  [{:keys [word-idx size]} stats reader]
  (let [m (la/dge size size)]
    (doseq [[[input output] s] stats]
      (when-let [i (word-idx input)]
        (when-let [j (word-idx output)]
          (let [reader-count (get (:by-reader s) reader 0)]
            (when (pos? reader-count)
              (la/entry! m i j (+ (la/entry m i j)
                                  (/ 1.0 (max 1 reader-count)))))))))
    (la/row-normalize! m)
    m))

(defn find-eigenwords
  "Words whose highest self-transition weight equals row maximum."
  [matrix vocab]
  (let [n (count vocab)]
    (->> (range n)
         (keep (fn [i]
                 (let [self-w (la/entry matrix i i)
                       row-vals (la/row-vec matrix i)
                       max-w (when (seq row-vals) (apply max row-vals))]
                   (when (and (pos? self-w) max-w (= self-w max-w))
                     {:word (vocab i)
                      :self-weight self-w}))))
         vec)))

;; ══════════════════════════════════════════════════════════════
;; Metrics — what we measure for each grid
;; ══════════════════════════════════════════════════════════════

(defn compute-metrics
  "Run the full pipeline on a given oracle and vocabulary.
   Returns a flat metrics map suitable for statistical comparison.

   vocab-source: :dict or :torah
   words: pre-built vocabulary vector
   valid-set: set of valid output words"
  [orc words valid-set]
  (let [;; Discover transitions
        disc (discover-transitions orc words valid-set)
        stats (transition-stats disc)
        vocab (:vocab disc)
        word-idx (:word-idx disc)
        n (:size disc)

        ;; Build four head matrices
        m-aaron (build-head-matrix disc stats :aaron)
        m-god   (build-head-matrix disc stats :god)
        m-right (build-head-matrix disc stats :right)
        m-left  (build-head-matrix disc stats :left)

        ;; Eigenwords per head
        ew-aaron (find-eigenwords m-aaron vocab)
        ew-god   (find-eigenwords m-god vocab)
        ew-right (find-eigenwords m-right vocab)
        ew-left  (find-eigenwords m-left vocab)

        ew-sets {:aaron (set (map :word ew-aaron))
                 :god   (set (map :word ew-god))
                 :right (set (map :word ew-right))
                 :left  (set (map :word ew-left))}

        ;; Agreement distribution
        all-ew-words (apply cset/union (vals ew-sets))
        agreement-fn (fn [w]
                       (count (filter #(contains? (ew-sets %) w)
                                      [:aaron :god :right :left])))
        agreement-dist (frequencies (map agreement-fn all-ew-words))

        ;; Solo eigenwords per head
        solo-fn (fn [head]
                  (set (filter (fn [w]
                                 (and (contains? (ew-sets head) w)
                                      (= 1 (agreement-fn w))))
                               all-ew-words)))
        solos {:aaron (solo-fn :aaron) :god (solo-fn :god)
               :right (solo-fn :right) :left (solo-fn :left)}

        ;; Dict overlap: how many solo eigenwords are in the dictionary?
        dict-words (dict/words)
        dict-solos (into {}
                         (map (fn [[h ws]]
                                [h (count (cset/intersection ws dict-words))])
                              solos))
        dict-solos-total (reduce + (vals dict-solos))

        ;; Lamb test: self-weight of כבש in each head
        lamb-idx (word-idx "כבש")
        lamb-self (when lamb-idx
                    {:aaron (la/entry m-aaron lamb-idx lamb-idx)
                     :god   (la/entry m-god lamb-idx lamb-idx)
                     :right (la/entry m-right lamb-idx lamb-idx)
                     :left  (la/entry m-left lamb-idx lamb-idx)})

        ;; Head separation: how different are the solo sets?
        ;; Jaccard distance between all pairs of solo eigenword sets
        jaccard (fn [a b]
                  (let [u (cset/union a b)
                        i (cset/intersection a b)]
                    (if (empty? u) 0.0
                        (- 1.0 (/ (double (count i)) (count u))))))
        pair-jaccards (for [h1 [:aaron :god :right :left]
                            h2 [:aaron :god :right :left]
                            :when (pos? (compare (name h1) (name h2)))]
                        [h1 h2 (jaccard (solos h1) (solos h2))])
        mean-jaccard (if (seq pair-jaccards)
                       (/ (reduce + (map last pair-jaccards))
                          (double (count pair-jaccards)))
                       0.0)]

    {:universe-size n
     :readable-count (count (:readable disc))
     :transition-count (count (:transitions disc))
     ;; Eigenword counts per head
     :ew-aaron (count ew-aaron)
     :ew-god   (count ew-god)
     :ew-right (count ew-right)
     :ew-left  (count ew-left)
     :ew-total (count all-ew-words)
     ;; Agreement distribution
     :unanimous (get agreement-dist 4 0)
     :supermajority (get agreement-dist 3 0)
     :majority (get agreement-dist 2 0)
     :solo-count (get agreement-dist 1 0)
     ;; Dict overlap
     :dict-solos-aaron (:aaron dict-solos)
     :dict-solos-god   (:god dict-solos)
     :dict-solos-right (:right dict-solos)
     :dict-solos-left  (:left dict-solos)
     :dict-solos-total dict-solos-total
     ;; Head separation
     :mean-jaccard mean-jaccard
     ;; Lamb
     :lamb-self lamb-self
     :lamb-readable? (some? lamb-idx)}))

;; ══════════════════════════════════════════════════════════════
;; Phase 1: Baseline — the real grid
;; ══════════════════════════════════════════════════════════════

(defn run-baseline
  "Run metrics on the real breastplate grid.
   vocab-mode: :dict (fast, ~215 words) or :torah (full, ~12,826 words)."
  ([] (run-baseline :dict))
  ([vocab-mode]
   (println "\n── Phase 1: Baseline (real grid) ──")
   (println (str "  Vocabulary: " (name vocab-mode)))
   (let [orc (make-oracle oracle/stone-data)
         words (case vocab-mode
                 :dict  (build-dict-vocab)
                 :torah (build-torah-vocab))
         valid (set words)
         t0 (System/currentTimeMillis)
         metrics (compute-metrics orc words valid)
         dt (- (System/currentTimeMillis) t0)]
     (println (str "  Done in " dt "ms."))
     (println (str "  Universe: " (:universe-size metrics) " words"))
     (println (str "  Eigenwords: aaron=" (:ew-aaron metrics)
                    " god=" (:ew-god metrics)
                    " right=" (:ew-right metrics)
                    " left=" (:ew-left metrics)))
     (println (str "  Agreement: 4/4=" (:unanimous metrics)
                    " 3/4=" (:supermajority metrics)
                    " 2/4=" (:majority metrics)
                    " 1/4=" (:solo-count metrics)))
     (println (str "  Dict solos: " (:dict-solos-total metrics)
                    " (A=" (:dict-solos-aaron metrics)
                    " G=" (:dict-solos-god metrics)
                    " R=" (:dict-solos-right metrics)
                    " L=" (:dict-solos-left metrics) ")"))
     (println (str "  Mean Jaccard: " (format "%.4f" (:mean-jaccard metrics))))
     (when (:lamb-self metrics)
       (let [ls (:lamb-self metrics)]
         (println (str "  Lamb self: A=" (format "%.4f" (:aaron ls))
                        " G=" (format "%.4f" (:god ls))
                        " R=" (format "%.4f" (:right ls))
                        " L=" (format "%.4f" (:left ls))))))
     (assoc metrics :vocab-mode vocab-mode :grid :real :elapsed-ms dt))))

;; ══════════════════════════════════════════════════════════════
;; Phase 2-3: Permutation test
;; ══════════════════════════════════════════════════════════════

(defn run-permutation
  "Run metrics on one shuffled grid."
  [rng words valid]
  (let [sd (shuffle-grid rng)
        orc (make-oracle sd)]
    (compute-metrics orc words valid)))

(defn run-permutation-test
  "Full permutation test.
   n: number of permutations (default 100)
   vocab-mode: :dict or :torah"
  ([] (run-permutation-test 100 :dict))
  ([n] (run-permutation-test n :dict))
  ([n vocab-mode]
   (println "\n═══════════════════════════════════════════════════")
   (println "  EXPERIMENT 092 — GRID PERMUTATION TEST")
   (println (str "  " n " random grids. Same 72 letters. Different arrangement."))
   (println (str "  Vocabulary: " (name vocab-mode)))
   (println "═══════════════════════════════════════════════════")

   ;; Phase 1: baseline
   (let [baseline (run-baseline vocab-mode)
         words (case vocab-mode
                 :dict  (build-dict-vocab)
                 :torah (build-torah-vocab))
         valid (set words)

         ;; Phase 2-3: permutations
         _ (println (str "\n── Phase 2-3: Running " n " permutations ──"))
         t0 (System/currentTimeMillis)
         progress (atom 0)
         results
         (vec (pmap (fn [seed]
                      (let [p (swap! progress inc)]
                        (when (zero? (mod p 10))
                          (let [elapsed (- (System/currentTimeMillis) t0)
                                rate (/ (double elapsed) p)]
                            (println (format "  %d/%d (%.1fs each, ~%.0fs remaining)"
                                             p n (/ rate 1000.0)
                                             (/ (* rate (- n p)) 1000.0))))))
                      (let [rng (java.util.Random. seed)]
                        (run-permutation rng words valid)))
                    (range n)))
         dt (- (System/currentTimeMillis) t0)]

     (println (str "\n  Done. " n " permutations in " (format "%.1f" (/ dt 1000.0)) "s"
                    " (" (format "%.2f" (/ (double dt) n 1000.0)) "s each)"))

     ;; Phase 4: compare
     (println "\n═══════════════════════════════════════════════════")
     (println "  Phase 4: COMPARISON — Real vs Null Distribution")
     (println "═══════════════════════════════════════════════════")

     (let [p-value (fn [real-val null-vals higher-is-better?]
                     (let [nv (count null-vals)
                           exceeding (if higher-is-better?
                                       (count (filter #(>= % real-val) null-vals))
                                       (count (filter #(<= % real-val) null-vals)))]
                       (/ (double (inc exceeding)) (inc nv))))

           percentile (fn [real-val null-vals]
                        (let [nv (count null-vals)
                              below (count (filter #(< % real-val) null-vals))]
                          (* 100.0 (/ (double below) nv))))

           report (fn [label real-val null-vals higher-better?]
                    (let [sorted (sort null-vals)
                          nv (count sorted)
                          mean (/ (reduce + null-vals) (double nv))
                          std (Math/sqrt (/ (reduce + (map #(let [d (- % mean)] (* d d))
                                                           null-vals))
                                           (double nv)))
                          p (p-value real-val null-vals higher-better?)
                          pctl (percentile real-val null-vals)
                          z (if (pos? std) (/ (- real-val mean) std) 0.0)]
                      (println (format "  %-25s  real=%-8s  null=%.2f +/- %.2f  z=%+.2f  p=%.4f  pctl=%.1f%%"
                                       label (str real-val) mean std z p pctl))))]

       ;; Eigenword counts
       (println "\n── Eigenword Counts ──")
       (report "Aaron eigenwords"  (:ew-aaron baseline)  (map :ew-aaron results) true)
       (report "God eigenwords"    (:ew-god baseline)    (map :ew-god results) true)
       (report "Right eigenwords"  (:ew-right baseline)  (map :ew-right results) true)
       (report "Left eigenwords"   (:ew-left baseline)   (map :ew-left results) true)
       (report "Total eigenwords"  (:ew-total baseline)  (map :ew-total results) true)

       ;; Agreement distribution
       (println "\n── Agreement Distribution ──")
       (report "Unanimous (4/4)"    (:unanimous baseline)     (map :unanimous results) true)
       (report "Supermajority (3/4)" (:supermajority baseline) (map :supermajority results) true)
       (report "Majority (2/4)"     (:majority baseline)      (map :majority results) true)
       (report "Solo (1/4)"         (:solo-count baseline)    (map :solo-count results) false)

       ;; Dict overlap
       (println "\n── Dictionary Solo Overlap (theological coherence) ──")
       (report "Dict solos total" (:dict-solos-total baseline) (map :dict-solos-total results) true)
       (report "Dict solos Aaron" (:dict-solos-aaron baseline) (map :dict-solos-aaron results) true)
       (report "Dict solos God"   (:dict-solos-god baseline)   (map :dict-solos-god results) true)
       (report "Dict solos Right" (:dict-solos-right baseline) (map :dict-solos-right results) true)
       (report "Dict solos Left"  (:dict-solos-left baseline)  (map :dict-solos-left results) true)

       ;; Head separation
       (println "\n── Head Separation (mean Jaccard distance) ──")
       (report "Mean Jaccard" (double (:mean-jaccard baseline))
               (map :mean-jaccard results) true)

       ;; Lamb
       (println "\n── The Lamb Test ──")
       (let [real-ls (:lamb-self baseline)
             ;; How many permutations even have lamb readable?
             lamb-readable (count (filter :lamb-readable? results))
             lamb-results (filter :lamb-readable? results)]
         (println (str "  Lamb readable: real=yes  null=" lamb-readable "/" n
                        " (" (format "%.1f%%" (* 100.0 (/ (double lamb-readable) n))) ")"))
         (when real-ls
           (println "  Lamb self-weights (among readable grids):")
           (when (seq lamb-results)
             (report "  Lamb Aaron" (:aaron real-ls)
                     (keep #(get-in % [:lamb-self :aaron]) lamb-results) true)
             (report "  Lamb God" (:god real-ls)
                     (keep #(get-in % [:lamb-self :god]) lamb-results) true)
             (report "  Lamb Right" (:right real-ls)
                     (keep #(get-in % [:lamb-self :right]) lamb-results) true)
             (report "  Lamb Left" (:left real-ls)
                     (keep #(get-in % [:lamb-self :left]) lamb-results) true))
           ;; The split: does the real grid show a unique reader partition?
           (let [real-split (set (keys (filter #(pos? (val %)) real-ls)))
                 null-splits (map (fn [r]
                                    (when-let [ls (:lamb-self r)]
                                      (set (keys (filter #(pos? (val %)) ls)))))
                                  lamb-results)
                 matching (count (filter #(= real-split %) null-splits))]
             (println (str "  Real lamb reads from: " real-split))
             (println (str "  Same reader split in null: " matching "/"
                            (count lamb-results)
                            " (" (format "%.1f%%"
                                         (if (pos? (count lamb-results))
                                           (* 100.0 (/ (double matching) (count lamb-results)))
                                           0.0))
                            ")"))))))

     ;; Universe size sanity check
     (println "\n── Sanity Check ──")
     (println (str "  Real universe: " (:universe-size baseline)))
     (println (str "  Null universe: mean=" (format "%.1f"
                                                     (/ (reduce + (map :universe-size results))
                                                        (double n)))
                    " min=" (apply min (map :universe-size results))
                    " max=" (apply max (map :universe-size results))))
     (println (str "  Real transitions: " (:transition-count baseline)))
     (println (str "  Null transitions: mean=" (format "%.0f"
                                                        (/ (reduce + (map :transition-count results))
                                                           (double n)))))

     (println "\n═══════════════════════════════════════════════════")
     (println "  DONE. The grid has been tested.")
     (println "═══════════════════════════════════════════════════")

     {:baseline baseline :null results :n n :vocab-mode vocab-mode})))

;; ══════════════════════════════════════════════════════════════
;; Singleton-Letter Control
;; ══════════════════════════════════════════════════════════════
;;
;; For every letter that appears only once on the grid, check:
;; does the reader distribution for ALL words containing that letter
;; match the lamb's {God, Right} vs {Aaron, Left} split?

(defn letter-frequencies-on-grid
  "Count how many times each letter appears on the breastplate."
  []
  (frequencies (extract-letters)))

(defn singleton-letters
  "Letters that appear exactly once on the grid."
  []
  (set (map key (filter #(= 1 (val %)) (letter-frequencies-on-grid)))))

(defn word-reader-distribution
  "For a word, return the reader distribution using the REAL oracle.
   {:aaron n :god n :right n :left n :total n}"
  [word]
  (let [hits (oracle/preimage word)]
    (when (seq hits)
      (let [by-reader (frequencies (map :reader hits))]
        {:aaron (get by-reader :aaron 0)
         :god   (get by-reader :god 0)
         :right (get by-reader :right 0)
         :left  (get by-reader :left 0)
         :total (count hits)}))))

(defn reader-split
  "Classify a word's reader distribution as a set of active readers.
   'Active' means > 0 readings from that head."
  [dist]
  (when dist
    (set (keep (fn [k] (when (pos? (get dist k 0)) k))
               [:aaron :god :right :left]))))

(defn run-singleton-control
  "For every singleton letter, scan all dict words containing it.
   Compare their reader splits to the lamb's split."
  []
  (println "\n═══════════════════════════════════════════════════")
  (println "  SINGLETON-LETTER CONTROL")
  (println "  Is the lamb's reader split unique to כבש,")
  (println "  or shared by all words with singleton letters?")
  (println "═══════════════════════════════════════════════════")

  (let [singletons (singleton-letters)
        all-letters (extract-letters)
        letter-freq (letter-frequencies-on-grid)

        _ (println (str "\n  Grid letter frequencies:"))
        _ (doseq [[ch n] (sort-by val > letter-freq)]
            (println (format "    %s  ×%d%s" ch n
                             (if (= n 1) "  ← SINGLETON" ""))))

        _ (println (str "\n  Singletons: " (apply str (sort singletons))
                        " (" (count singletons) " letters)"))

        ;; For each singleton letter, find all dict words containing it
        dict-ws (dict/words)
        lamb-dist (word-reader-distribution "כבש")
        lamb-split (reader-split lamb-dist)

        _ (println (str "\n  Lamb (כבש) distribution: " lamb-dist))
        _ (println (str "  Lamb reader split: " lamb-split))]

    (println "\n── Per-Singleton Analysis ──")

    (let [per-singleton
          (vec (for [ch (sort singletons)]
                 (let [;; Find which stone has this letter
                       positions (get oracle/letter-index ch)
                       [stone-num pos-in-stone] (first positions)
                       ;; All dict words containing this letter
                       containing (filterv #(some #{ch} (seq %)) dict-ws)
                       ;; Get reader distribution for each
                       dists (vec (keep (fn [w]
                                          (when-let [d (word-reader-distribution w)]
                                            (assoc d :word w :meaning (dict/translate w))))
                                        containing))
                       splits (map reader-split dists)
                       split-freq (frequencies splits)
                       ;; How many match the lamb's split?
                       matching (count (filter #(= lamb-split %) splits))]
                   (println (str "\n  " ch " (stone " stone-num ", pos " pos-in-stone
                                  ", " (count containing) " dict words, "
                                  (count dists) " readable):"))
                   (doseq [[s cnt] (sort-by val > split-freq)]
                     (println (format "    %-35s  %d words  %s"
                                      (str s) cnt
                                      (if (= s lamb-split) " ← LAMB PATTERN" ""))))
                   (when (seq dists)
                     (println "  Notable words:")
                     (doseq [{:keys [word meaning] :as d}
                             (take 8 (sort-by :total > dists))]
                       (println (format "    %-8s  A=%d G=%d R=%d L=%d  %-5s  %s"
                                        word (:aaron d) (:god d) (:right d) (:left d)
                                        (str (reader-split d))
                                        (or meaning "")))))
                   {:letter ch
                    :stone stone-num
                    :words-containing (count containing)
                    :readable (count dists)
                    :split-freq split-freq
                    :lamb-pattern-count matching
                    :lamb-pattern-pct (when (pos? (count dists))
                                        (* 100.0 (/ (double matching) (count dists))))})))]

      ;; Summary
      (println "\n═══════════════════════════════════════════════════")
      (println "  SINGLETON CONTROL SUMMARY")
      (println "═══════════════════════════════════════════════════")
      (println (format "  %-6s  %-8s  %-10s  %-10s  %s"
                       "Letter" "Stone" "Readable" "Lamb-patt" "% match"))
      (doseq [{:keys [letter stone readable lamb-pattern-count lamb-pattern-pct]}
              per-singleton]
        (println (format "  %-6s  %-8d  %-10d  %-10d  %s"
                         (str letter) stone readable
                         (or lamb-pattern-count 0)
                         (if lamb-pattern-pct
                           (format "%.1f%%" lamb-pattern-pct)
                           "N/A"))))

      (let [;; Is כ special among singletons?
            kaf-data (first (filter #(= \כ (:letter %)) per-singleton))]
        (println (str "\n  כ (kaf) is on stone 8 (Issachar = 'there is reward')."))
        (println (str "  Lamb's split " lamb-split " appears in "
                       (:lamb-pattern-count kaf-data) "/"
                       (:readable kaf-data) " כ-words ("
                       (when (:lamb-pattern-pct kaf-data)
                         (format "%.1f%%" (:lamb-pattern-pct kaf-data)))
                       ")."))
        (println "  If the split is typical of ALL כ-words → letter position drives it.")
        (println "  If the split is specific to כבש → something deeper."))

      {:singletons per-singleton
       :lamb-split lamb-split
       :lamb-dist lamb-dist})))

;; ══════════════════════════════════════════════════════════════
;; Save / Load
;; ══════════════════════════════════════════════════════════════

(defn save-results
  "Save permutation test results to data/experiments/092/."
  [{:keys [baseline null n vocab-mode] :as results}]
  (let [dir "data/experiments/092"]
    (.mkdirs (java.io.File. dir))
    ;; Save baseline
    (spit (str dir "/baseline.edn") (pr-str (dissoc baseline :matrices)))
    ;; Save null distribution summary (not the full matrices!)
    (spit (str dir "/null-distribution.edn")
          (pr-str (mapv #(dissoc % :matrices) null)))
    ;; Save summary
    (let [summary {:n n
                   :vocab-mode vocab-mode
                   :baseline (select-keys baseline
                               [:ew-aaron :ew-god :ew-right :ew-left :ew-total
                                :unanimous :supermajority :majority :solo-count
                                :dict-solos-total :mean-jaccard :lamb-self
                                :universe-size :transition-count])
                   :null-means (into {}
                                 (map (fn [k]
                                        [k (/ (reduce + (map (comp double k) null))
                                              (double n))])
                                      [:ew-aaron :ew-god :ew-right :ew-left :ew-total
                                       :unanimous :supermajority :majority :solo-count
                                       :dict-solos-total :mean-jaccard
                                       :universe-size :transition-count]))}]
      (spit (str dir "/summary.edn") (pr-str summary)))
    (println (str "  Saved to " dir "/"))))

;; ══════════════════════════════════════════════════════════════
;; Run
;; ══════════════════════════════════════════════════════════════

(defn run
  "Run everything: permutation test + singleton control.
   Default: 100 permutations on dict vocabulary (fast)."
  ([] (run 100 :dict))
  ([n] (run n :dict))
  ([n vocab-mode]
   (let [perm-results (run-permutation-test n vocab-mode)
         singleton-results (run-singleton-control)]
     (save-results perm-results)
     {:permutation perm-results
      :singleton singleton-results})))

(comment
  ;; ── Quick test (1 permutation, dict vocab) ──
  ;; Use this to verify the pipeline works before the full run.
  (def test-result (run-permutation-test 1 :dict))

  ;; ── Dict vocabulary, 100 permutations (~10-20 min) ──
  (def results (run 100 :dict))

  ;; ── Dict vocabulary, 500 permutations ──
  (def results (run 500 :dict))

  ;; ── Torah vocabulary, 100 permutations (SLOW: hours) ──
  ;; Only do this if the dict test shows significance.
  (def results (run 100 :torah))

  ;; ── Just the baseline ──
  (def baseline (run-baseline :dict))
  (def baseline-full (run-baseline :torah))

  ;; ── Just the singleton control ──
  (def singleton (run-singleton-control))

  ;; ── Explore a specific permuted grid ──
  (let [sd (shuffle-grid (java.util.Random. 42))
        orc (make-oracle sd)]
    (println "Shuffled grid:")
    (doseq [[s letters r c] sd]
      (println (format "  Stone %2d [%d,%d]: %s" s r c letters)))
    ;; Check if a specific word is readable
    (let [ilsets ((:illumination-sets orc) "כבש")]
      (println (str "\n  Lamb illuminations: " (count ilsets)))))

  ;; ── Compare real vs one random grid ──
  (let [words (build-dict-vocab)
        valid (set words)
        real-orc (make-oracle oracle/stone-data)
        rand-orc (make-oracle (shuffle-grid (java.util.Random. 42)))
        m-real (compute-metrics real-orc words valid)
        m-rand (compute-metrics rand-orc words valid)]
    {:real (select-keys m-real [:ew-total :unanimous :dict-solos-total :mean-jaccard])
     :rand (select-keys m-rand [:ew-total :unanimous :dict-solos-total :mean-jaccard])})

  ;; ── Grid letter frequencies ──
  (letter-frequencies-on-grid)
  (singleton-letters)

  :end)
