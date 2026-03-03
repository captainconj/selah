(ns experiments.094-thummim-sweep
  "Experiment 094 — The Thummim Sweep.

   Run every word in the Torah (~7,300 unique forms) through the
   Level 2 Thummim (phrase assembly). Look at the distribution.

   The signal lives in the long tail. Babe Ruths live in the long tail.
   כשרה was found in the long tail of שכרה's readings.

   Five phases:
     1. Full sweep — parse-letters for every Torah word
     2. Distribution analysis — phrase counts, illumination counts
     3. Long tail — rare/unique readings, hapax phrases, forced multi-word
     4. Neanderthal — SVD of word×phrase incidence, Gram matrices, power law
     5. Output — save everything to data/experiments/094/"
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [selah.linalg :as la]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.native :as nn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

;; ══════════════════════════════════════════════════════════════
;; Phase 1: The Sweep
;; ══════════════════════════════════════════════════════════════

(defn sweep-word
  "Sweep a single word through the Thummim.
   Uses parse-letters (letter multiset) — O(1) per word instead of
   O(illuminations) via thummim-menu. Same phrase decompositions."
  [word opts]
  (let [ilsets (oracle/illumination-sets word)
        n-il   (count ilsets)
        phrases (when (pos? n-il)
                  (oracle/parse-letters word opts))]
    {:word word
     :gv (g/word-value word)
     :meaning (dict/translate word)
     :illuminable? (pos? n-il)
     :illumination-count n-il
     :phrase-count (count (or phrases []))
     :phrases (vec (or phrases []))
     :len (count word)}))

(defn run-sweep
  "Sweep all Torah words through the Thummim.
   Returns vec of sweep results, sorted alphabetically."
  ([] (run-sweep {}))
  ([opts]
   (let [words (vec (sort (dict/torah-words)))
         opts  (merge {:max-words 4 :min-letters 2 :vocab :torah} opts)
         n     (count words)
         progress (atom 0)
         t0    (System/currentTimeMillis)]
     (println (str "Sweeping " n " words through Thummim (vocab=" (:vocab opts) ")..."))
     (let [results (->> words
                        (pmap (fn [w]
                                (let [p (swap! progress inc)]
                                  (when (zero? (mod p 500))
                                    (println (str "  " p "/" n " ("
                                                  (quot (- (System/currentTimeMillis) t0) 1000) "s)"))))
                                (sweep-word w opts)))
                        vec)
           elapsed (quot (- (System/currentTimeMillis) t0) 1000)]
       (println (str "Sweep complete: " n " words in " elapsed "s"))
       results))))

;; ══════════════════════════════════════════════════════════════
;; Phase 2: Distribution Analysis
;; ══════════════════════════════════════════════════════════════

(defn compute-distributions
  "Analyze the sweep results. Returns distribution maps."
  [results]
  (let [illuminable (filter :illuminable? results)
        with-phrases (filter #(pos? (:phrase-count %)) results)

        ;; Phrase count distribution
        phrase-count-dist (frequencies (map :phrase-count results))

        ;; Illumination count distribution
        illum-count-dist (frequencies (map :illumination-count results))

        ;; Words-per-phrase distribution (across all phrases of all words)
        all-phrases (mapcat :phrases with-phrases)
        words-per-phrase-dist (frequencies (map :words all-phrases))

        ;; Phrase text frequency: how often does each unique phrase appear
        ;; across different input words?
        phrase-freq (->> all-phrases
                         (map :text)
                         frequencies
                         (sort-by val >)
                         vec)

        ;; GV distribution across all phrases
        gv-dist (->> all-phrases
                     (map :gv)
                     frequencies
                     (sort-by key)
                     vec)

        ;; Length distribution of input words
        len-dist (frequencies (map :len results))]

    {:total-words (count results)
     :illuminable-count (count illuminable)
     :with-phrases-count (count with-phrases)
     :not-illuminable-count (count (remove :illuminable? results))
     :total-unique-phrases (count (distinct (map :text all-phrases)))
     :total-phrase-instances (count all-phrases)
     :phrase-count-dist (into (sorted-map) phrase-count-dist)
     :illumination-count-dist (into (sorted-map) illum-count-dist)
     :words-per-phrase-dist (into (sorted-map) words-per-phrase-dist)
     :phrase-frequency (vec (take 200 phrase-freq))
     :phrase-frequency-full-count (count phrase-freq)
     :gv-dist gv-dist
     :len-dist (into (sorted-map) len-dist)}))

(defn print-distributions
  "Print a summary of distributions to stdout."
  [dists]
  (println)
  (println "═══ DISTRIBUTION SUMMARY ═══")
  (println)
  (println (str "Total words:        " (:total-words dists)))
  (println (str "Illuminable:        " (:illuminable-count dists)
                " (" (format "%.1f%%" (* 100.0 (/ (:illuminable-count dists)
                                                   (double (:total-words dists))))) ")"))
  (println (str "With phrases:       " (:with-phrases-count dists)))
  (println (str "Not illuminable:    " (:not-illuminable-count dists)))
  (println (str "Unique phrases:     " (:total-unique-phrases dists)))
  (println (str "Total phrase inst:  " (:total-phrase-instances dists)))

  (println)
  (println "── Phrase Count Distribution (how many phrases per word) ──")
  (doseq [[cnt n] (take 30 (:phrase-count-dist dists))]
    (println (format "  %4d phrases: %4d words" cnt n)))
  (when (> (count (:phrase-count-dist dists)) 30)
    (println "  ..."))

  (println)
  (println "── Words per Phrase (1-word vs 2-word vs 3-word vs 4-word) ──")
  (doseq [[wc n] (:words-per-phrase-dist dists)]
    (println (format "  %d-word: %,d phrases" wc n)))

  (println)
  (println "── Top 30 Most Frequent Phrases (across all input words) ──")
  (doseq [[text freq] (take 30 (:phrase-frequency dists))]
    (println (format "  %4d× %s" freq text)))

  (println)
  (println "── Input Word Length Distribution ──")
  (doseq [[len n] (:len-dist dists)]
    (println (format "  %d letters: %4d words" len n))))

;; ══════════════════════════════════════════════════════════════
;; Phase 3: Long Tail
;; ══════════════════════════════════════════════════════════════

(defn analyze-long-tail
  "Find the signal in the long tail."
  [results]
  (let [illuminable (filter :illuminable? results)
        with-phrases (filter #(pos? (:phrase-count %)) results)

        ;; Rare words: exactly 1 or 2 phrase readings
        rare-words (->> with-phrases
                        (filter #(<= (:phrase-count %) 2))
                        (sort-by :phrase-count)
                        vec)

        ;; Forced single reading: phrase-count = 1
        forced (->> with-phrases
                    (filter #(= 1 (:phrase-count %)))
                    (sort-by :word)
                    vec)

        ;; Hapax phrases: phrases that appear from exactly ONE input word
        all-phrase-sources (->> with-phrases
                               (mapcat (fn [{:keys [word phrases]}]
                                         (map (fn [p] [(:text p) word]) phrases)))
                               (group-by first)
                               (map (fn [[text pairs]]
                                      {:phrase text
                                       :sources (vec (distinct (map second pairs)))
                                       :source-count (count (distinct (map second pairs)))})))
        hapax (->> all-phrase-sources
                   (filter #(= 1 (:source-count %)))
                   (sort-by :phrase)
                   vec)

        ;; Forced multi-word: all phrases have >1 word (no single-word reading exists)
        forced-multi (->> with-phrases
                          (filter (fn [{:keys [phrases]}]
                                    (every? #(> (:words %) 1) phrases)))
                          (sort-by :word)
                          vec)

        ;; GV clustering of rare readings
        rare-gvs (->> rare-words
                      (mapcat :phrases)
                      (map :gv)
                      frequencies
                      (sort-by val >)
                      (take 30)
                      vec)

        ;; Semantic groups among rare readings with translations
        rare-with-meaning (->> rare-words
                               (mapcat (fn [{:keys [word phrases]}]
                                         (for [p phrases
                                               :let [meanings (remove nil? (:meanings p))]
                                               :when (seq meanings)]
                                           {:input word
                                            :input-meaning (dict/translate word)
                                            :phrase (:text p)
                                            :meanings meanings
                                            :gv (:gv p)})))
                               vec)

        ;; Known words check
        known-words-check (for [w ["כבש" "אהבה" "אמת" "יהוה" "שלום" "ברית"]]
                            (let [r (first (filter #(= w (:word %)) results))]
                              (when r
                                (select-keys r [:word :meaning :illuminable?
                                                :illumination-count :phrase-count]))))]

    {:rare-words-count (count rare-words)
     :forced-count (count forced)
     :forced (mapv #(select-keys % [:word :gv :meaning :phrase-count :phrases]) forced)
     :hapax-count (count hapax)
     :hapax (vec (take 200 hapax))
     :forced-multi-count (count forced-multi)
     :forced-multi (mapv #(select-keys % [:word :gv :meaning :phrase-count :phrases])
                         (take 100 forced-multi))
     :rare-gv-clusters rare-gvs
     :rare-with-meaning (vec (take 100 rare-with-meaning))
     :known-words-check (vec (remove nil? known-words-check))}))

(defn print-long-tail
  "Print the long tail findings."
  [tail]
  (println)
  (println "═══ LONG TAIL ANALYSIS ═══")
  (println)
  (println (str "Rare words (1-2 phrases):  " (:rare-words-count tail)))
  (println (str "Forced reading (exactly 1): " (:forced-count tail)))
  (println (str "Hapax phrases (unique):    " (:hapax-count tail)))
  (println (str "Forced multi-word:         " (:forced-multi-count tail)))

  (println)
  (println "── Forced Readings (phrase-count = 1) ──")
  (doseq [{:keys [word gv meaning phrases]} (take 40 (:forced tail))]
    (let [p (first phrases)]
      (println (format "  %s (GV=%d, %s) → %s [%s]"
                       word gv (or meaning "?")
                       (:text p)
                       (str/join ", " (remove nil? (:meanings p)))))))

  (println)
  (println "── Forced Multi-word (no single-word reading exists) ──")
  (doseq [{:keys [word gv meaning phrase-count]} (take 30 (:forced-multi tail))]
    (println (format "  %s (GV=%d, %s) — %d phrases, all multi-word"
                     word gv (or meaning "?") phrase-count)))

  (println)
  (println "── Hapax Phrases (appear from exactly ONE input word) ──")
  (doseq [{:keys [phrase sources]} (take 40 (:hapax tail))]
    (println (format "  %s ← %s" phrase (first sources))))

  (println)
  (println "── GV Clusters in Rare Readings ──")
  (doseq [[gv cnt] (:rare-gv-clusters tail)]
    (println (format "  GV=%d: %d rare readings" gv cnt)))

  (println)
  (println "── Known Words Check ──")
  (doseq [w (:known-words-check tail)]
    (println (format "  %s (%s): illuminations=%d, phrases=%d"
                     (:word w) (or (:meaning w) "?")
                     (:illumination-count w) (:phrase-count w)))))

;; ══════════════════════════════════════════════════════════════
;; Phase 4: Neanderthal — Matrix Analysis
;; ══════════════════════════════════════════════════════════════
;;
;; W (word × phrase) is 11k × 310k — too large for dense storage.
;; Instead: build G = W·W^T directly via inverted index.
;; G is N×N (~11k²) ≈ 1 GB. Eigenvalues of G = σ² of W.
;; Phrase frequency statistics come from Phase 2.

(defn build-word-similarity
  "Build G = W·W^T (word similarity matrix) via chunked BLAS GEMM.
   G[i,j] = number of phrases shared between word i and word j.

   Strategy:
   1. Build inverted index: phrase → word-index-set
   2. Hapax phrases (90%+) → just increment diagonal (scalar)
   3. Multi-source phrases → chunk into column blocks of W (N × chunk-size)
   4. Accumulate G += W_chunk · W_chunk^T via nc/mm! (pure MKL GEMM)

   This never materializes the full 11k × 310k matrix."
  [results]
  (let [with-phrases (vec (filter #(pos? (:phrase-count %)) results))
        word-list (mapv :word with-phrases)
        word-idx (into {} (map-indexed (fn [i w] [w i]) word-list))
        N (count word-list)]
    (println (str "Building G (" N "×" N ") via chunked BLAS GEMM..."))
    (let [t0 (System/currentTimeMillis)
          ;; Inverted index: phrase-text → set of word indices
          inv-idx (reduce (fn [idx {:keys [word phrases]}]
                            (let [wi (word-idx word)]
                              (reduce (fn [idx2 p]
                                        (update idx2 (:text p) (fnil conj #{}) wi))
                                      idx phrases)))
                          {} with-phrases)
          M (count inv-idx)
          _ (println (str "  Inverted index: " M " unique phrases"))
          ;; Separate hapax (1 word) from multi-source (2+ words)
          hapax (filterv (fn [[_ wset]] (= 1 (count wset))) inv-idx)
          multi (filterv (fn [[_ wset]] (> (count wset) 1)) inv-idx)
          _ (println (str "  Hapax: " (count hapax) " | Multi-source: " (count multi)))
          ;; Initialize G
          G (nn/dge N N)
          ;; Step 1: Hapax → diagonal only (each adds 1 to G[i,i])
          _ (doseq [[_ wset] hapax]
              (let [i (first wset)]
                (nc/entry! G i i (+ (nc/entry G i i) 1.0))))
          _ (println "  Hapax diagonal done")
          ;; Step 2: Multi-source → chunk into W columns, GEMM accumulate
          chunk-size 5000
          chunks (partition-all chunk-size multi)]
      (doseq [[ci chunk] (map-indexed vector chunks)]
        (let [cs (count chunk)
              W-chunk (nn/dge N cs)]
          ;; Fill columns: column j = indicator vector for phrase j's word set
          (doseq [[j [_ wset]] (map-indexed vector chunk)]
            (doseq [wi wset]
              (nc/entry! W-chunk wi j 1.0)))
          ;; G += W-chunk · W-chunk^T  (BLAS GEMM in-place)
          (nc/mm! 1.0 W-chunk (nc/trans W-chunk) 1.0 G)
          (println (str "  Chunk " (inc ci) "/" (count chunks) " (" cs " phrases) — GEMM done"))))
      (let [elapsed (quot (- (System/currentTimeMillis) t0) 1000)]
        (println (str "  G built in " elapsed "s (pure BLAS)"))
        {:G G
         :word-idx word-idx
         :words word-list
         :N N
         :M M}))))

(defn spectral-analysis
  "Eigendecomposition of G = W·W^T.
   Eigenvalues of G = σ² of W → singular value spectrum.
   Returns effective rank, top singular values, etc."
  [{:keys [G N words]}]
  (println (str "Eigendecomposition of G (" N "×" N ")..."))
  (let [t0 (System/currentTimeMillis)
        {:keys [values vectors]} (la/eigendecomp G)
        elapsed (quot (- (System/currentTimeMillis) t0) 1000)
        _ (println (str "  Eigendecomp done in " elapsed "s"))
        ;; Sort eigenvalues descending (real parts)
        sorted-evals (->> values (map #(Math/abs %)) sort reverse vec)
        ;; Singular values = sqrt of eigenvalues of G
        sigma (mapv #(Math/sqrt (max 0.0 %)) sorted-evals)
        ;; Effective rank at various thresholds
        total-energy (reduce + sorted-evals)
        cumulative (reductions + sorted-evals)
        cumfrac (mapv #(/ % (max total-energy 1e-10)) cumulative)
        rank-90 (inc (count (take-while #(< % 0.90) cumfrac)))
        rank-95 (inc (count (take-while #(< % 0.95) cumfrac)))
        rank-99 (inc (count (take-while #(< % 0.99) cumfrac)))
        top-sigma (vec (take 50 sigma))]
    (println (str "  Effective rank: 90%=" rank-90 " 95%=" rank-95 " 99%=" rank-99))
    (println (str "  Top 5 σ: " (str/join ", " (map #(format "%.1f" %) (take 5 sigma)))))
    {:sigma top-sigma
     :sigma-count (count (filter #(> % 1e-6) sigma))
     :total-energy total-energy
     :rank-90 rank-90
     :rank-95 rank-95
     :rank-99 rank-99
     :cumulative-fraction (vec (take 50 cumfrac))
     :elapsed-s elapsed}))

(defn synonym-analysis
  "Find oracle synonyms from G: words with identical phrase sets.
   G[i,j] = G[i,i] = G[j,j] means i and j produce exactly the same phrases."
  [{:keys [G N words]}]
  (println "Finding oracle synonyms...")
  (let [synonym-pairs (atom [])
        ;; Check all pairs (this is O(N²) but N≈11k, takes ~2min)
        ;; Optimization: only check pairs where G[i,i] = G[j,j] (same phrase count)
        diags (mapv (fn [i] (long (nc/entry G i i))) (range N))
        ;; Group by diagonal value (phrase count)
        by-count (group-by second (map-indexed vector diags))]
    ;; Only check pairs within same phrase-count group
    (doseq [[cnt pairs] by-count
            :when (> (count pairs) 1)]
      (let [idxs (mapv first pairs)]
        (dotimes [a (count idxs)]
          (dotimes [b a]
            (let [i (idxs a) j (idxs b)
                  gij (long (nc/entry G i j))]
              (when (= gij cnt)
                (swap! synonym-pairs conj
                       {:word-a (words i)
                        :word-b (words j)
                        :shared-phrases gij})))))))
    (let [result (vec (sort-by :shared-phrases > @synonym-pairs))]
      (println (str "  Oracle synonyms: " (count result)))
      result)))

(defn word-similarity-stats
  "Extract summary statistics from G."
  [{:keys [G N words]}]
  (let [diags (mapv (fn [i] (long (nc/entry G i i))) (range N))
        ;; Off-diagonal: sample for statistics (full scan is N²)
        sample-size 10000
        rng (java.util.Random. 42)
        off-diag-sample (repeatedly sample-size
                                    #(let [i (.nextInt rng N)
                                           j (.nextInt rng N)]
                                       (when (not= i j)
                                         (long (nc/entry G i j)))))
        off-diag (remove nil? off-diag-sample)
        off-max (apply max off-diag)
        off-mean (/ (double (reduce + off-diag)) (count off-diag))
        off-nonzero (count (filter pos? off-diag))]
    {:diag-summary {:min (apply min diags)
                    :max (apply max diags)
                    :mean (/ (double (reduce + diags)) N)}
     :off-diag-sample {:max off-max
                        :mean (format "%.2f" off-mean)
                        :nonzero-frac (format "%.4f" (/ (double off-nonzero) (count (vec off-diag))))
                        :sample-size (count (vec off-diag))}}))

(defn power-law-fit
  "Fit a power law to phrase-count distribution.
   log(freq) = -alpha * log(rank) + C
   Least-squares via Neanderthal SVD."
  [results]
  (let [;; Phrase count frequency: how many words have each phrase count?
        freq-dist (->> results
                       (map :phrase-count)
                       frequencies
                       (sort-by key)
                       (remove #(zero? (first %)))  ;; exclude 0-phrase words
                       vec)
        n (count freq-dist)
        ;; Log-log regression
        ranks (mapv first freq-dist)
        freqs (mapv second freq-dist)
        log-ranks (mapv #(Math/log (double %)) ranks)
        log-freqs (mapv #(Math/log (double %)) freqs)
        ;; Design matrix A = [log(rank), 1]
        A (la/matrix (mapv (fn [lr] [lr 1.0]) log-ranks))
        b (la/vec->dv log-freqs)
        ;; Solve via SVD: x = V · Σ^{-1} · U^T · b
        {:keys [u sigma vt]} (la/svd A)
        ;; Manual pseudo-inverse solve
        ;; U^T · b
        utb (nc/mv (nc/trans u) b)
        ;; Σ^{-1} · (U^T b)
        sinv-utb (nn/dv (mapv (fn [i]
                                (let [s (sigma i)]
                                  (if (> (Math/abs s) 1e-10)
                                    (/ (nc/entry utb i) s)
                                    0.0)))
                              (range (count sigma))))
        ;; V · (Σ^{-1} U^T b) = x
        x (nc/mv (nc/trans vt) sinv-utb)
        alpha (- (nc/entry x 0))  ;; negative slope = positive alpha
        C (nc/entry x 1)
        ;; R-squared
        mean-lf (/ (reduce + log-freqs) (double n))
        ss-tot (reduce + (map #(Math/pow (- % mean-lf) 2) log-freqs))
        predicted (mapv #(+ (* (nc/entry x 0) %) C) log-ranks)
        ss-res (reduce + (map #(Math/pow (- %1 %2) 2) log-freqs predicted))
        r-sq (- 1.0 (/ ss-res ss-tot))
        ;; Cumulative distribution and knee
        sorted-counts (->> results
                           (map :phrase-count)
                           (remove zero?)
                           sort
                           vec)
        total (count sorted-counts)
        cum-dist (map-indexed (fn [i _] [(inc i) (nth sorted-counts i)]) sorted-counts)
        ;; Knee: where does the cumulative mass hit 50%?
        half-idx (quot total 2)
        knee-phrase-count (when (pos? total) (nth sorted-counts half-idx))]
    (println)
    (println (str "Power law fit: alpha=" (format "%.3f" alpha)
                  " R²=" (format "%.4f" r-sq)))
    (when knee-phrase-count
      (println (str "Median phrase count: " knee-phrase-count)))
    {:alpha alpha
     :C C
     :r-squared r-sq
     :n-points n
     :data-points (mapv (fn [[r f]] {:rank r :freq f}) freq-dist)
     :median-phrase-count knee-phrase-count
     :total-with-phrases (count sorted-counts)}))

(defn feature-correlation
  "Build feature matrix F (N × 4) and compute correlation matrix.
   Columns: word length, GV, illumination count, phrase count."
  [results]
  (let [with-phrases (filter #(pos? (:phrase-count %)) results)
        N (count with-phrases)
        features [:len :gv :illumination-count :phrase-count]
        ;; Build F
        F (la/matrix (mapv (fn [r] (mapv #(double (% r)) features)) with-phrases))
        ;; Correlation = (F^T · F) / N, then normalize by std devs
        ;; First: means
        means (mapv (fn [col]
                      (/ (reduce + (map #(double (% (nth features col))) with-phrases))
                         (double N)))
                    (range 4))
        ;; Std devs
        stds (mapv (fn [col]
                     (let [mu (means col)]
                       (Math/sqrt (/ (reduce + (map #(Math/pow (- (double (% (nth features col))) mu) 2)
                                                    with-phrases))
                                     (double N)))))
                   (range 4))
        ;; Pearson correlation via raw sums
        corr (vec (for [i (range 4)]
                    (vec (for [j (range 4)]
                           (let [mu-i (means i) mu-j (means j)
                                 s-i (stds i) s-j (stds j)]
                             (if (or (zero? s-i) (zero? s-j))
                               0.0
                               (/ (reduce + (map (fn [r]
                                                   (* (- (double ((nth features i) r)) mu-i)
                                                      (- (double ((nth features j) r)) mu-j)))
                                                 with-phrases))
                                  (* N s-i s-j))))))))]
    (println)
    (println "Feature correlation matrix [len, GV, illuminations, phrases]:")
    (doseq [row corr]
      (println (str "  " (str/join " " (map #(format "%7.3f" %) row)))))
    {:features (mapv name features)
     :means means
     :stds stds
     :correlation corr}))

(defn run-matrix-analysis
  "Run all Phase 4 Neanderthal analysis.
   Order matters: extract all G statistics BEFORE eigendecomp, because
   eigendecomp allocates large native buffers that can interfere with
   G's native memory after return."
  [results save-fn]
  (println)
  (println "═══ MATRIX ANALYSIS (Phase 4) ═══")
  (let [;; Step 1: Build G via GEMM
        ws (build-word-similarity results)

        ;; Step 2: Extract G stats and synonyms BEFORE eigendecomp
        ;; (eigendecomp's native allocations can corrupt G)
        sim-stats (word-similarity-stats ws)
        synonyms (synonym-analysis ws)
        _ (save-fn "synonyms.edn"
                   {:count (count synonyms)
                    :synonyms (vec (take 100 synonyms))
                    :similarity-stats sim-stats})

        ;; Step 3: Power law + feature correlation (small matrices, safe)
        pl (power-law-fit results)
        _ (save-fn "power-law.edn" pl)
        fc (feature-correlation results)
        _ (save-fn "feature-correlation.edn" fc)

        ;; Step 4: Eigendecomp (heavy — 11k×11k, ~5 min)
        ;; Do NOT call System/gc here — Neanderthal native buffers
        ;; interact badly with full GC (SIGSEGV in G1FullGCMarker).
        spectral (spectral-analysis ws)
        _ (save-fn "spectral.edn" spectral)]

    {:spectral spectral
     :synonyms (vec (take 100 synonyms))
     :synonym-count (count synonyms)
     :similarity-stats sim-stats
     :power-law pl
     :feature-correlation fc}))

;; ══════════════════════════════════════════════════════════════
;; Phase 5: Output & Printing
;; ══════════════════════════════════════════════════════════════

(defn- save-edn
  "Save data to an EDN file."
  [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(defn print-matrix-summary
  "Print Phase 4 results."
  [{:keys [spectral synonyms synonym-count similarity-stats power-law feature-correlation]}]
  (println)
  (println "═══ MATRIX SUMMARY ═══")
  (println)
  (println (str "Singular value spectrum (via G eigendecomp):"))
  (println (str "  Non-zero σ: " (:sigma-count spectral)))
  (println (str "  Effective rank: 90%=" (:rank-90 spectral)
                " 95%=" (:rank-95 spectral)
                " 99%=" (:rank-99 spectral)))
  (println (str "  Top 10 σ: " (str/join ", " (map #(format "%.1f" %) (take 10 (:sigma spectral))))))
  (println (str "  Computed in " (:elapsed-s spectral) "s"))

  (println)
  (println "Word similarity (G = W·W^T):")
  (println (str "  Phrase count range: " (get-in similarity-stats [:diag-summary :min])
                " to " (get-in similarity-stats [:diag-summary :max])
                " (mean " (format "%.1f" (get-in similarity-stats [:diag-summary :mean])) ")"))
  (let [od (:off-diag-sample similarity-stats)]
    (println (str "  Off-diagonal: max=" (:max od)
                  " mean=" (:mean od)
                  " nonzero=" (:nonzero-frac od))))

  (when (pos? synonym-count)
    (println)
    (println (str "Oracle synonyms (identical phrase sets): " synonym-count))
    (doseq [{:keys [word-a word-b shared-phrases]} (take 20 synonyms)]
      (println (format "  %s ≡ %s (%d shared phrases)" word-a word-b shared-phrases))))

  (println)
  (println (str "Power law: α=" (format "%.3f" (:alpha power-law))
                " R²=" (format "%.4f" (:r-squared power-law)))))

;; ══════════════════════════════════════════════════════════════
;; Main Entry Point
;; ══════════════════════════════════════════════════════════════

(defn run
  "Run the full experiment. All five phases.
   Saves results incrementally — no work lost if a later phase crashes."
  ([] (run {}))
  ([opts]
   (let [t0 (System/currentTimeMillis)
         dir "data/experiments/094/"
         save (fn [name data] (save-edn (str dir name) data))

         ;; Phase 1: Sweep
         results (run-sweep opts)

         ;; Phase 2: Distributions
         _ (println)
         dists (compute-distributions results)
         _ (print-distributions dists)

         ;; Phase 3: Long tail
         tail (analyze-long-tail results)
         _ (print-long-tail tail)

         ;; ── Save Phases 1-3 immediately ──
         _ (println)
         _ (println "═══ SAVING PHASES 1-3 ═══")
         slim-results (mapv #(dissoc % :phrases) results)
         phrase-results (->> results
                             (filter #(pos? (:phrase-count %)))
                             (mapv #(select-keys % [:word :gv :meaning :phrase-count
                                                    :phrases :illumination-count])))
         _ (save "thummim-sweep.edn"
                 {:computed (str (java.time.LocalDate/now))
                  :opts opts
                  :results slim-results})
         _ (save "phrase-results.edn"
                 {:computed (str (java.time.LocalDate/now))
                  :count (count phrase-results)
                  :results phrase-results})
         _ (save "distributions.edn" dists)
         _ (save "long-tail.edn" tail)

         ;; Phase 4: Matrix analysis (saves incrementally via save-fn)
         ;; Needs full results with phrases for incidence matrix
         matrix (run-matrix-analysis results save)
         _ (print-matrix-summary matrix)]

     (let [elapsed (quot (- (System/currentTimeMillis) t0) 1000)]
       (println)
       (println (str "═══ EXPERIMENT 094 COMPLETE (" elapsed "s) ═══"))
       (println)
       (println "Key numbers:")
       (println (str "  Words swept:         " (count results)))
       (println (str "  Illuminable:         " (:illuminable-count dists)))
       (println (str "  With phrases:        " (:with-phrases-count dists)))
       (println (str "  Unique phrases:      " (:total-unique-phrases dists)))
       (println (str "  Forced (1 phrase):   " (:forced-count tail)))
       (println (str "  Hapax phrases:       " (:hapax-count tail)))
       (println (str "  Forced multi-word:   " (:forced-multi-count tail)))
       (println (str "  Effective rank:      90%=" (get-in matrix [:spectral :rank-90])
                     " 95%=" (get-in matrix [:spectral :rank-95])))
       (println (str "  Power law α:         " (format "%.3f" (get-in matrix [:power-law :alpha]))))
       (println (str "  Power law R²:        " (format "%.4f" (get-in matrix [:power-law :r-squared]))))
       (when (pos? (:synonym-count matrix))
         (println (str "  Oracle synonyms:     " (:synonym-count matrix))))

       {:results results
        :distributions dists
        :long-tail tail
        :matrix matrix}))))

;; ══════════════════════════════════════════════════════════════
;; Verification helpers
;; ══════════════════════════════════════════════════════════════

(defn verify-optimization
  "Spot-check: parse-letters gives same phrase set as thummim-menu for 5 words.
   The optimization only skips per-illumination mechanical readings."
  []
  (let [test-words ["כבש" "אהבה" "שלום" "ברית" "אמת"]
        opts {:max-words 4 :min-letters 2 :vocab :torah}]
    (println "Verifying optimization (parse-letters ≡ thummim-menu phrases)...")
    (doseq [w test-words]
      (let [via-parse (set (map :text (oracle/parse-letters w opts)))
            via-thummim (when-let [tm (oracle/thummim-menu w opts)]
                          (set (map :text (:phrases tm))))
            match? (= via-parse via-thummim)]
        (println (format "  %s: parse=%d, thummim=%d — %s"
                         w
                         (count via-parse)
                         (count (or via-thummim #{}))
                         (if match? "MATCH ✓" "MISMATCH ✗")))))
    (println "Verification complete.")))

(defn run-eigendecomp-only
  "Run just the eigendecomp on saved sweep data.
   Use when Phase 1-3 + partial Phase 4 completed but eigendecomp crashed."
  ([] (run-eigendecomp-only {}))
  ([opts]
   (let [dir "data/experiments/094/"
         saved (edn/read-string (slurp (str dir "thummim-sweep.edn")))
         ;; Rebuild phrase data from phrase-results
         phrase-saved (edn/read-string (slurp (str dir "phrase-results.edn")))
         ;; Merge phrases back into results
         phrase-map (into {} (map (fn [r] [(:word r) r]) (:results phrase-saved)))
         results (mapv (fn [r]
                         (if-let [pr (phrase-map (:word r))]
                           (assoc r :phrases (:phrases pr))
                           (assoc r :phrases [])))
                       (:results saved))
         ;; Build G
         ws (build-word-similarity results)
         ;; Eigendecomp
         spectral (spectral-analysis ws)]
     (save-edn (str dir "spectral.edn") spectral)
     (println)
     (println (str "Effective rank: 90%=" (:rank-90 spectral)
                   " 95%=" (:rank-95 spectral)
                   " 99%=" (:rank-99 spectral)))
     spectral)))

(comment
  ;; ── Quick start ──
  ;; Verify the optimization first:
  (verify-optimization)

  ;; Run the full experiment:
  (def results (run))

  ;; Or run with dict vocab (faster, smaller):
  (def results (run {:vocab :dict}))

  ;; Phase by phase:
  (def sweep (run-sweep))
  (def dists (compute-distributions sweep))
  (print-distributions dists)
  (def tail (analyze-long-tail sweep))
  (print-long-tail tail)
  (def matrix (run-matrix-analysis sweep))
  (print-matrix-summary matrix)

  ;; Inspect specific words:
  (first (filter #(= "כבש" (:word %)) sweep))
  (first (filter #(= "אהבה" (:word %)) sweep))

  ;; Look at forced readings:
  (:forced (analyze-long-tail sweep))

  ;; Oracle synonyms:
  (get-in matrix [:gram :oracle-synonyms])
  )
