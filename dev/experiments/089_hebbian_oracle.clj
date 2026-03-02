(ns experiments.089-hebbian-oracle
  "The Hebbian oracle — words that fire together wire together.

   The breastplate gives each word a feature vector:
   - 12 stone activations ({0,1}¹²)
   - 3 reader counts (ℝ³)
   - 22 letter frequencies (ℤ²²)
   - 1 gematria value

   Affinity between words = how much their features overlap.
   Hebbian: shared activations strengthen connections.
   Hannah: rare features carry more weight.

   Gibbs sampling walks the chain. No matrix needed.
   The stationary distribution IS the oracle's voice."
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [selah.linalg :as la]
            [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.core :as nc]
            [clojure.string :as str]))

;; ── Part 1: Feature Extraction ────────────────────────────────

(def hebrew-letters
  "The 22 Hebrew letters in order."
  [\א \ב \ג \ד \ה \ו \ז \ח \ט \י \כ \ל \מ \נ \ס \ע \פ \צ \ק \ר \ש \ת])

(def letter-idx
  (into {} (map-indexed (fn [i c] [c i]) hebrew-letters)))

;; Map final forms to base
(def final->base {\ך \כ \ם \מ \ן \נ \ף \פ \ץ \צ})

(defn letter-freq-vec
  "22-dimensional letter frequency vector for a word."
  [word]
  (let [v (int-array 22)]
    (doseq [c word]
      (let [base (get final->base c c)]
        (when-let [i (letter-idx base)]
          (aset v i (inc (aget v i))))))
    v))

(defn stone-vec
  "12-dimensional stone activation vector.
   Marks which stones light up across ALL illumination patterns."
  [word]
  (let [v (int-array 12)
        illums (oracle/illumination-sets word)]
    (doseq [illum illums]
      (doseq [[stone _pos] illum]
        (aset v (dec (int stone)) 1)))
    v))

(defn reader-vec
  "3-dimensional reader count vector [aaron right left].
   by-reader maps :aaron/:right/:left → count (integer)."
  [ask-result]
  (let [by-r (:by-reader ask-result)]
    [(int (or (get by-r :aaron) 0))
     (int (or (get by-r :right) 0))
     (int (or (get by-r :left) 0))]))

(defn word-features
  "Complete feature set for a word."
  [word ask-result]
  {:word word
   :gv (g/word-value word)
   :letter-freq (letter-freq-vec word)
   :stones (stone-vec word)
   :readers (reader-vec ask-result)
   :total-readings (or (:total-readings ask-result) 0)
   :readable? (:readable? ask-result)})

;; ── Part 2: Affinity Functions ────────────────────────────────
;;
;; Hebbian: shared activations create connection.
;; Multiple channels of affinity, each weighted.

(defn stone-overlap
  "Number of shared active stones between two words."
  ^long [^ints s1 ^ints s2]
  (loop [i 0 sum 0]
    (if (= i 12) sum
        (recur (inc i)
               (if (and (pos? (aget s1 i)) (pos? (aget s2 i)))
                 (inc sum) sum)))))

(defn letter-overlap
  "Shared letter inventory (min of each frequency, summed)."
  ^long [^ints f1 ^ints f2]
  (loop [i 0 sum 0]
    (if (= i 22) sum
        (recur (inc i) (+ sum (min (aget f1 i) (aget f2 i)))))))

(defn is-anagram?
  "True if two words have exactly the same letter frequencies."
  [^ints f1 ^ints f2]
  (loop [i 0]
    (if (= i 22) true
        (if (= (aget f1 i) (aget f2 i))
          (recur (inc i))
          false))))

(defn reader-similarity
  "Cosine similarity of reader profiles."
  [[a1 r1 l1] [a2 r2 l2]]
  (let [dot (+ (* a1 a2) (* r1 r2) (* l1 l2))
        mag1 (Math/sqrt (+ (* a1 a1) (* r1 r1) (* l1 l1)))
        mag2 (Math/sqrt (+ (* a2 a2) (* r2 r2) (* l2 l2)))]
    (if (or (zero? mag1) (zero? mag2))
      0.0
      (/ (double dot) (* mag1 mag2)))))

(defn jaccard-letters
  "Jaccard similarity of letter sets (ignoring frequency).
   Ratio of shared distinct letters to total distinct letters."
  [^ints f1 ^ints f2]
  (let [shared (loop [i 0 s 0] (if (= i 22) s
                                    (recur (inc i)
                                           (if (and (pos? (aget f1 i))
                                                    (pos? (aget f2 i)))
                                             (inc s) s))))
        union (loop [i 0 s 0] (if (= i 22) s
                                   (recur (inc i)
                                          (if (or (pos? (aget f1 i))
                                                  (pos? (aget f2 i)))
                                            (inc s) s))))]
    (if (zero? union) 0.0 (/ (double shared) union))))

(defn affinity
  "Hebbian affinity between two word feature sets.
   Channels: stones, letters (Jaccard), reader profile, anagram, gematria.
   Hannah principle: rare features carry more weight.
   Normalized by word length to prevent long-word bias."
  [f1 f2 {:keys [stone-weight letter-weight reader-weight
                  anagram-weight gv-weight]
          :or {stone-weight 3.0
               letter-weight 2.0
               reader-weight 1.5
               anagram-weight 10.0
               gv-weight 1.0}}]
  (let [;; Stone overlap, normalized by max possible (12)
        stones (/ (double (stone-overlap (:stones f1) (:stones f2))) 12.0)
        ;; Letter Jaccard (0-1, length-independent)
        letters (jaccard-letters (:letter-freq f1) (:letter-freq f2))
        ;; Reader cosine similarity (0-1)
        readers (reader-similarity (:readers f1) (:readers f2))
        ;; Anagram bonus (same letter multiset)
        anagram (if (is-anagram? (:letter-freq f1) (:letter-freq f2)) 1.0 0.0)
        ;; Same gematria bonus
        same-gv (if (= (:gv f1) (:gv f2)) 1.0 0.0)]
    (+ (* stone-weight stones)
       (* letter-weight letters)
       (* reader-weight readers)
       (* anagram-weight anagram)
       (* gv-weight same-gv))))

;; ── Part 2b: Dense Affinity Matrix via Neanderthal ───────────
;;
;; Stone overlap = F_stones · F_stones^T (batch inner products)
;; Reader cosine = (F_readers · F_readers^T) / (norms ⊗ norms)
;; Jaccard + anagram + GV need per-element loops.

(defn build-affinity-matrix
  "Build the full dense affinity matrix using Neanderthal.
   Batch-computes stone overlap and reader similarity via mm.
   Returns Neanderthal dge of size n×n."
  [features weights]
  (let [n (count features)
        {:keys [stone-weight letter-weight reader-weight
                anagram-weight gv-weight]
         :or {stone-weight 2.0 letter-weight 1.0 reader-weight 1.0
              anagram-weight 5.0 gv-weight 0.5}} weights
        _ (println (str "Building dense affinity matrix (" n "×" n ")..."))

        ;; Pack stone vectors into n×12 matrix
        f-stones (nn/dge n 12)
        _ (dotimes [i n]
            (let [^ints sv (:stones (features i))]
              (dotimes [j 12]
                (nc/entry! f-stones i j (double (aget sv j))))))
        ;; Stone overlap = F · F^T (each entry = dot product of stone vectors)
        ;; Result (i,j) = number of shared active stones
        stone-overlap-mat (nc/mm f-stones (nc/trans f-stones))
        _ (println "  Stone overlap matrix computed.")

        ;; Pack reader vectors into n×3 matrix
        f-readers (nn/dge n 3)
        reader-norms (double-array n)
        _ (dotimes [i n]
            (let [[a r l] (:readers (features i))]
              (nc/entry! f-readers i 0 (double a))
              (nc/entry! f-readers i 1 (double r))
              (nc/entry! f-readers i 2 (double l))
              (aset reader-norms i
                    (Math/sqrt (+ (* (double a) (double a))
                                  (* (double r) (double r))
                                  (* (double l) (double l)))))))
        ;; Reader dot products = F · F^T
        reader-dots (nc/mm f-readers (nc/trans f-readers))
        _ (println "  Reader similarity matrix computed.")

        ;; Build full affinity matrix
        afm (nn/dge n n)

        ;; GV index for fast same-GV lookup
        gv-vec (int-array (map :gv features))

        ;; Pre-compute Jaccard denominators (number of distinct letters per word)
        distinct-counts (int-array n)]
    (dotimes [i n]
      (let [^ints freq (:letter-freq (features i))]
        (aset distinct-counts i
              (loop [j 0 cnt 0]
                (if (= j 22) cnt
                    (recur (inc j) (if (pos? (aget freq j)) (inc cnt) cnt)))))))

    ;; Fill affinity matrix
    ;; Stone and reader channels come from pre-computed matrices.
    ;; Jaccard and anagram channels need per-element computation.
    (println "  Computing per-element channels (Jaccard, anagram, GV)...")
    (let [chunks (partition-all (max 1 (/ n 16)) (range n))]
      (->> chunks
           (pmap (fn [rows]
                   (doseq [i rows]
                     (let [fi (features i)
                           ^ints freq-i (:letter-freq fi)
                           gv-i (aget gv-vec i)]
                       (dotimes [j n]
                         (when (not= i j)
                           (let [;; Stone: from pre-computed matrix
                                 stones (/ (nc/entry stone-overlap-mat i j) 12.0)
                                 ;; Reader cosine: dots / (norm_i * norm_j)
                                 ni (aget reader-norms i)
                                 nj (aget reader-norms j)
                                 readers (if (or (zero? ni) (zero? nj))
                                           0.0
                                           (/ (nc/entry reader-dots i j) (* ni nj)))
                                 ;; Jaccard: shared distinct letters / union distinct letters
                                 ^ints freq-j (:letter-freq (features j))
                                 shared (loop [k 0 s 0]
                                          (if (= k 22) s
                                              (recur (inc k)
                                                     (if (and (pos? (aget freq-i k))
                                                              (pos? (aget freq-j k)))
                                                       (inc s) s))))
                                 union (+ (aget distinct-counts i)
                                          (aget distinct-counts j)
                                          (- shared))
                                 letters (if (zero? union) 0.0
                                             (/ (double shared) union))
                                 ;; Anagram
                                 anagram (if (is-anagram? freq-i freq-j) 1.0 0.0)
                                 ;; Same GV
                                 same-gv (if (= gv-i (aget gv-vec j)) 1.0 0.0)
                                 ;; Total affinity
                                 aff (+ (* stone-weight stones)
                                        (* letter-weight letters)
                                        (* reader-weight readers)
                                        (* anagram-weight anagram)
                                        (* gv-weight same-gv))]
                             (nc/entry! afm i j aff))))))))
           doall))
    (println "  Done. Dense affinity matrix ready.")
    afm))

;; ── Part 3: Build Word Features (Full Vocabulary) ─────────────

(defn build-torah-vocab
  "Extract all unique word forms from the Torah."
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
    (let [v (sort @all-words)]
      (println (str "  " (count v) " unique word forms."))
      v)))

(defn build-features
  "Compute features for all readable words. Returns vec of feature maps.
   Uses pmap for parallel feature extraction (oracle/ask is pure)."
  [words]
  (println (str "Computing features for " (count words) " words..."))
  (let [n (count words)
        progress (atom 0)
        results (->> words
                     (pmap (fn [w]
                             (let [p (swap! progress inc)]
                               (when (zero? (mod p 2000))
                                 (println (str "  " p "/" n "..."))))
                             (let [a (oracle/ask w)]
                               (when (:readable? a)
                                 (word-features w a)))))
                     (filterv some?))]
    (println (str "  " (count results) " readable words with features."))
    results))

;; ── Part 4: Gibbs Sampler ─────────────────────────────────────
;;
;; State = current word index.
;; At each step, compute affinity to all other words.
;; Sample next word proportional to exp(affinity / temperature).
;; Collect histogram.

(defn build-neighbor-index
  "Pre-compute candidate neighbors for each word.
   Two words are candidates if they share ≥1 stone, same GV, or are anagrams.
   Returns vec of int-arrays (neighbor indices per word)."
  [features]
  (let [n (count features)
        ;; Index by stone
        by-stone (reduce (fn [acc i]
                           (let [^ints sv (:stones (features i))]
                             (reduce (fn [a s]
                                       (if (pos? (aget sv s))
                                         (update a s (fnil conj #{}) i)
                                         a))
                                     acc (range 12))))
                         {} (range n))
        ;; Index by GV
        by-gv (group-by #(:gv (features %)) (range n))
        ;; Index by anagram signature
        by-sig (group-by #(vec (sort (:word (features %)))) (range n))]
    (println (str "  Building neighbor index for " n " words..."))
    (mapv (fn [i]
            (let [^ints sv (:stones (features i))
                  ;; All words sharing ≥1 stone
                  stone-nbrs (reduce (fn [acc s]
                                       (if (pos? (aget sv s))
                                         (into acc (get by-stone s))
                                         acc))
                                     #{} (range 12))
                  ;; Same GV
                  gv-nbrs (set (get by-gv (:gv (features i))))
                  ;; Same anagram signature
                  sig-nbrs (set (get by-sig (vec (sort (:word (features i))))))
                  ;; Union, excluding self
                  all-nbrs (disj (into stone-nbrs (into gv-nbrs sig-nbrs)) i)]
              (int-array (sort all-nbrs))))
          (range n))))

(defn transition-probs
  "Compute transition probabilities from word i to neighbors only.
   Returns double-array of size n (sparse, most entries zero)."
  [features i temperature weights neighbor-index]
  (let [idx (long i)
        fi (features idx)
        n (count features)
        ^ints neighbors (neighbor-index idx)
        nn (alength neighbors)
        affinities (double-array n)]
    ;; Only compute affinity to neighbors
    (dotimes [k nn]
      (let [j (aget neighbors k)
            a (affinity fi (features j) weights)]
        (when (pos? a)
          (aset affinities j (Math/exp (/ (double a) (double temperature)))))))
    ;; Normalize
    (let [total (areduce affinities k sum 0.0 (+ sum (aget affinities k)))]
      (when (pos? total)
        (dotimes [j n]
          (aset affinities j (/ (aget affinities j) total)))))
    affinities))

(defn gibbs-step
  "One step of the Gibbs sampler. Returns next word index."
  ^long [^doubles probs]
  (let [r (Math/random)
        n (alength probs)]
    (loop [j 0 cumul 0.0]
      (if (>= j n) (dec n)
          (let [new-cumul (+ cumul (aget probs j))]
            (if (>= new-cumul r)
              j
              (recur (inc j) new-cumul)))))))

(defn gibbs-sample
  "Run Gibbs sampler for n-steps. Returns histogram of visits."
  [features n-steps temperature weights
   & {:keys [start burn-in report-every]
      :or {start 0 burn-in 1000 report-every 10000}}]
  (let [n (count features)
        histogram (long-array n)
        ;; Build neighbor index (sparse)
        nbr-idx (build-neighbor-index features)
        _ (let [avg-nbrs (/ (reduce + (map #(alength ^ints %) nbr-idx)) (double n))]
            (println (format "  Avg neighbors per word: %.1f" avg-nbrs)))
        ;; Pre-compute transition probabilities for each word
        _ (println (str "Pre-computing transition probabilities (" n " words)..."))
        trans (vec (pmap #(transition-probs features % temperature weights nbr-idx)
                         (range n)))
        _ (println "Done. Starting Gibbs sampler...")]
    (loop [step 0
           current (long start)]
      (when (and (pos? step) (zero? (mod step report-every)))
        (println (str "  Step " step "/" n-steps
                      " — current: " (:word (features current)))))
      (if (= step n-steps)
        ;; Return results
        (let [total-visits (- n-steps burn-in)
              results (->> (range n)
                           (map (fn [j] {:word (:word (features j))
                                         :gv (:gv (features j))
                                         :visits (aget histogram j)
                                         :freq (/ (double (aget histogram j))
                                                  (max 1 total-visits))}))
                           (filter #(pos? (:visits %)))
                           (sort-by :freq >)
                           vec)]
          {:histogram histogram
           :results results
           :n-steps n-steps
           :burn-in burn-in
           :temperature temperature
           :n-words n
           :total-visits total-visits})
        ;; Take a step
        (do
          (when (>= step burn-in)
            (aset histogram current (inc (aget histogram current))))
          (let [probs (trans current)
                next-word (gibbs-step probs)]
            (recur (inc step) next-word)))))))

;; ── Part 4b: Gibbs Sampler with Dense Affinity Matrix ─────────

(defn gibbs-sample-dense
  "Gibbs sampler using pre-computed dense affinity matrix.
   Faster startup (no per-word transition pre-computation).
   The dense matrix trades memory for speed."
  [features afm n-steps temperature
   & {:keys [start burn-in report-every]
      :or {start 0 burn-in 1000 report-every 10000}}]
  (let [n (count features)
        histogram (long-array n)
        ;; Pre-compute transition probabilities from affinity matrix
        ;; For each row i: p(j|i) ∝ exp(affinity(i,j) / T)
        _ (println (str "Pre-computing transition probabilities from dense matrix (" n " words)..."))
        trans (vec
               (pmap
                (fn [i]
                  (let [probs (double-array n)]
                    (dotimes [j n]
                      (let [a (nc/entry afm i j)]
                        (when (pos? a)
                          (aset probs j (Math/exp (/ a (double temperature)))))))
                    ;; Normalize
                    (let [total (areduce probs k sum 0.0 (+ sum (aget probs k)))]
                      (when (pos? total)
                        (dotimes [j n]
                          (aset probs j (/ (aget probs j) total)))))
                    probs))
                (range n)))
        _ (println "Done. Starting Gibbs sampler...")]
    (loop [step 0
           current (long start)]
      (when (and (pos? step) (zero? (mod step report-every)))
        (println (str "  Step " step "/" n-steps
                      " — current: " (:word (features current)))))
      (if (= step n-steps)
        (let [total-visits (- n-steps burn-in)
              results (->> (range n)
                           (map (fn [j] {:word (:word (features j))
                                         :gv (:gv (features j))
                                         :visits (aget histogram j)
                                         :freq (/ (double (aget histogram j))
                                                  (max 1 total-visits))}))
                           (filter #(pos? (:visits %)))
                           (sort-by :freq >)
                           vec)]
          {:histogram histogram
           :results results
           :n-steps n-steps
           :burn-in burn-in
           :temperature temperature
           :n-words n
           :total-visits total-visits})
        (do
          (when (>= step burn-in)
            (aset histogram current (inc (aget histogram current))))
          (let [probs (trans current)
                next-word (gibbs-step probs)]
            (recur (inc step) next-word)))))))

;; ── Part 5: Analysis ──────────────────────────────────────────

(defn connectivity-stats
  "How connected is each word? Average affinity to neighbors."
  [features weights nbr-idx]
  (let [n (count features)]
    (println (str "Computing connectivity for " n " words..."))
    (->> (range n)
         (pmap (fn [i]
                 (let [fi (features i)
                       ^ints nbrs (nbr-idx i)
                       nn (alength nbrs)
                       total-aff (loop [k 0 sum 0.0]
                                   (if (= k nn) sum
                                       (recur (inc k)
                                              (+ sum (affinity fi (features (aget nbrs k)) weights)))))
                       n-nonzero (loop [k 0 cnt 0]
                                   (if (= k nn) cnt
                                       (recur (inc k)
                                              (if (pos? (affinity fi (features (aget nbrs k)) weights))
                                                (inc cnt) cnt))))]
                   {:word (:word fi)
                    :gv (:gv fi)
                    :total-affinity total-aff
                    :mean-affinity (if (pos? n-nonzero)
                                    (/ total-aff n-nonzero) 0.0)
                    :n-neighbors n-nonzero
                    :readings (:total-readings fi)})))
         (sort-by :total-affinity >)
         vec)))

;; ── Part 6: Run ───────────────────────────────────────────────

(defn run
  "Run the Hebbian oracle on full Torah vocabulary."
  [& {:keys [temperature n-steps max-words weights]
      :or {temperature 1.0
           n-steps 100000
           max-words nil
           weights {:stone-weight 2.0
                    :letter-weight 1.0
                    :reader-weight 1.0
                    :anagram-weight 5.0
                    :gv-weight 0.5}}}]
  (println "\n═══════════════════════════════════════════════════")
  (println "  EXPERIMENT 089 — THE HEBBIAN ORACLE")
  (println (str "  T=" temperature "  steps=" n-steps))
  (println "═══════════════════════════════════════════════════")

  ;; Build vocabulary
  (let [torah-words (build-torah-vocab)
        words (if max-words
                ;; Uniform random sample, not alphabetical prefix
                (let [shuffled (shuffle (vec torah-words))]
                  (sort (take max-words shuffled)))
                torah-words)
        features (build-features (vec words))]

    (println (str "\n  Feature vectors: " (count features) " words"))

    ;; Build neighbor index once
    (let [nbr-idx (build-neighbor-index features)]

    ;; Connectivity stats
    (println "\n═══ TOP CONNECTED WORDS ═══")
    (let [conn (connectivity-stats features weights nbr-idx)]
      (println (format "  %-12s %6s %8s %8s %5s"
                       "Word" "GV" "TotAff" "MeanAff" "Nbrs"))
      (doseq [{:keys [word gv total-affinity mean-affinity n-neighbors]}
              (take 30 conn)]
        (println (format "  %-12s %6d %8.1f %8.3f %5d"
                         word gv total-affinity mean-affinity n-neighbors))))

    ;; Gibbs sampling
    (println "\n═══ GIBBS SAMPLING ═══")
    (let [result (gibbs-sample features n-steps temperature weights
                               :burn-in (min 5000 (/ n-steps 10))
                               :report-every (/ n-steps 10))]

      (println (str "\n  Visited " (count (:results result))
                    " / " (:n-words result) " words"))

      ;; Top attractors
      (println "\n═══ TOP ATTRACTORS (stationary distribution) ═══")
      (println (format "  %-4s %-12s %6s %10s %10s"
                       "#" "Word" "GV" "Visits" "Freq"))
      (doseq [[i {:keys [word gv visits freq]}]
              (map-indexed vector (take 50 (:results result)))]
        (println (format "  %-4d %-12s %6d %10d %10.6f"
                         (inc i) word gv visits freq)))

      ;; Temperature comparison
      (println "\n═══════════════════════════════════════════════════")
      (println "  DONE.")
      (println "═══════════════════════════════════════════════════")
      result))))

;; ── Part 7: Save Data ────────────────────────────────────────

(defn save-data
  "Save experiment data to data/experiments/089/."
  [features nbr-idx conn gibbs-result]
  (let [dir "data/experiments/089"]
    (.mkdirs (java.io.File. dir))

    ;; Features (word + GV + readable stones/readers + letter counts)
    (println "Saving features...")
    (spit (str dir "/features.edn")
          (pr-str (mapv (fn [f]
                          {:word (:word f)
                           :gv (:gv f)
                           :total-readings (:total-readings f)
                           :stones (vec (:stones f))
                           :readers (:readers f)
                           :letter-freq (vec (:letter-freq f))})
                        features)))

    ;; Anagram groups
    (println "Saving anagram groups...")
    (let [groups (->> features
                      (group-by #(vec (sort (:word %))))
                      (filter #(> (count (val %)) 1))
                      (map (fn [[sig members]]
                             {:signature (apply str (sort (first (map :word members))))
                              :gv (:gv (first members))
                              :members (mapv :word members)
                              :count (count members)}))
                      (sort-by :count >)
                      vec)]
      (spit (str dir "/anagram-groups.edn") (pr-str groups)))

    ;; Connectivity stats (top 200)
    (println "Saving connectivity stats...")
    (spit (str dir "/connectivity.edn")
          (pr-str (vec (take 200 conn))))

    ;; Gibbs results (top 500 + metadata)
    (println "Saving Gibbs results...")
    (spit (str dir "/gibbs-results.edn")
          (pr-str {:n-words (:n-words gibbs-result)
                   :n-steps (:n-steps gibbs-result)
                   :burn-in (:burn-in gibbs-result)
                   :temperature (:temperature gibbs-result)
                   :total-visits (:total-visits gibbs-result)
                   :top-500 (vec (take 500 (:results gibbs-result)))}))

    ;; Summary stats
    (println "Saving summary...")
    (let [n (count features)
          n-groups (->> features
                        (group-by #(vec (sort (:word %))))
                        (filter #(> (count (val %)) 1))
                        count)
          n-singletons (- n (* 2 n-groups)) ;; approximate
          avg-nbrs (/ (reduce + (map #(alength ^ints %) nbr-idx)) (double n))]
      (spit (str dir "/summary.edn")
            (pr-str {:total-torah-words "12,826"
                     :readable-words n
                     :anagram-groups n-groups
                     :avg-neighbors avg-nbrs
                     :top-attractor (:word (first (:results gibbs-result)))
                     :top-attractor-freq (:freq (first (:results gibbs-result)))
                     :unique-visited (count (:results gibbs-result))})))

    (println (str "Data saved to " dir "/"))))

(comment
  ;; Quick test on dict-only words (~210)
  (def r (run :max-words 300 :n-steps 50000 :temperature 1.0))

  ;; Full Torah vocabulary
  (def r (run :n-steps 200000 :temperature 1.0))

  ;; Cold — concentrated
  (def r (run :n-steps 100000 :temperature 0.1))

  ;; Hot — exploratory
  (def r (run :n-steps 100000 :temperature 5.0))

  ;; Vary weights
  (def r (run :n-steps 100000 :temperature 1.0
              :weights {:stone-weight 5.0    ; stone-dominant
                        :letter-weight 0.5
                        :reader-weight 0.5
                        :anagram-weight 2.0
                        :gv-weight 0.0}))

  ;; ── Dense affinity matrix workflow ────────────────────────────
  ;; (uses ~3.4GB for full 7323×7323 matrix — fine on 16-core machine)
  (do
    (def torah-words (build-torah-vocab))
    (def features (build-features (vec torah-words)))  ;; pmap — ~16× faster
    (def fv (vec features))
    (def weights {:stone-weight 2.0 :letter-weight 1.0 :reader-weight 1.0
                  :anagram-weight 5.0 :gv-weight 0.5})
    (def afm (build-affinity-matrix fv weights))       ;; Neanderthal mm
    (def gibbs (gibbs-sample-dense fv afm 500000 1.0
                                   :burn-in 5000 :report-every 50000)))

  ;; ── Sparse workflow (original, lower memory) ──────────────────
  (do
    (def torah-words (build-torah-vocab))
    (def features (build-features (vec torah-words)))
    (def fv (vec features))
    (def nbr-idx (build-neighbor-index fv))
    (def weights {:stone-weight 2.0 :letter-weight 1.0 :reader-weight 1.0
                  :anagram-weight 5.0 :gv-weight 0.5})
    (def conn (connectivity-stats fv weights nbr-idx))
    (def gibbs (gibbs-sample fv 500000 1.0 weights
                             :burn-in 5000 :report-every 50000))
    (save-data fv nbr-idx conn gibbs))

  :end)
