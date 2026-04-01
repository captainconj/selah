(ns experiments.092b-ramban-permutation
  "Ramban pair permutation test — does the real breastplate produce
   unusual Ramban pairs compared to random grids?

   A Ramban pair: same illumination pattern, different readers produce
   different dictionary words. Named after Nachmanides (13th century)
   who described exactly this mechanism:

     'The letters would light up to his eyes. However, he still did not
      know their correct order, for from the letters which can be ordered
      Yehuda ya'aleh (Judah shall go up) it is possible to make of them
      hey al Yehuda (woe unto Judah).'

   The real grid produces 12 such pairs across 340 illumination patterns.
   The question: would random grids produce the same, more, or fewer?"
  (:require [experiments.092-grid-permutation :as base]
            [selah.oracle :as oracle]
            [selah.dict :as dict]
            [clojure.set :as cset]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════════════
;; Ramban Pair Finder
;; ══════════════════════════════════════════════════════════════

(defn find-ramban-cases
  "For a given oracle context, find all illumination patterns where
   different readers produce different dictionary words.

   Returns {:pairs #{[word-a word-b]},
            :pair-count int,
            :ramban-illuminations int,
            :by-pair {[w1 w2] count},
            :total-illuminations int}"
  [orc dict-words]
  (let [dict-set (set dict-words)
        illumination-sets-fn (:illumination-sets orc)
        read-positions-fn (:read-positions orc)
        readers [:aaron :god :truth :mercy]
        ;; Deduplicate illumination sets across words
        ;; (anagram inputs produce the same position sets)
        seen (atom #{})
        all-pairs (atom {})
        ramban-count (atom 0)
        total-illum (atom 0)]
    (doseq [word dict-words]
      (doseq [pset (illumination-sets-fn word)]
        (when-not (@seen pset)
          (swap! seen conj pset)
          (swap! total-illum inc)
          ;; Read from all 4 readers
          (let [outputs (into {} (map (fn [r] [r (read-positions-fn r pset)]) readers))
                ;; Which outputs are dictionary words?
                dict-outputs (set (filter dict-set (vals outputs)))
                ;; Unique dict words
                n-unique (count dict-outputs)]
            (when (> n-unique 1)
              (swap! ramban-count inc)
              ;; Record each pair
              (let [sorted-words (sort dict-outputs)]
                (doseq [i (range (count sorted-words))
                        j (range (inc i) (count sorted-words))]
                  (let [pair [(nth sorted-words i) (nth sorted-words j)]]
                    (swap! all-pairs update pair (fnil inc 0))))))))))
    (let [pairs @all-pairs]
      {:pairs (set (keys pairs))
       :pair-count (count pairs)
       :by-pair pairs
       :ramban-illuminations @ramban-count
       :total-illuminations @total-illum})))

(defn find-ramban-detailed
  "Like find-ramban-cases but returns full reader→word mappings.
   Use for the real grid analysis, not for permutation test."
  [orc dict-words]
  (let [dict-set (set dict-words)
        illumination-sets-fn (:illumination-sets orc)
        read-positions-fn (:read-positions orc)
        readers [:aaron :god :truth :mercy]
        seen (atom #{})
        cases (atom [])]
    (doseq [word dict-words]
      (doseq [pset (illumination-sets-fn word)]
        (when-not (@seen pset)
          (swap! seen conj pset)
          (let [outputs (into {} (map (fn [r] [r (read-positions-fn r pset)]) readers))
                dict-outputs (set (filter dict-set (vals outputs)))]
            (when (> (count dict-outputs) 1)
              (swap! cases conj
                     {:illumination pset
                      :readings outputs
                      :dict-readings (into {} (filter (fn [[_ w]] (dict-set w)) outputs))
                      :triggering-word word}))))))
    @cases))

;; ══════════════════════════════════════════════════════════════
;; Baseline — Real Grid Analysis
;; ══════════════════════════════════════════════════════════════

(defn run-baseline []
  (println "\n═══════════════════════════════════════════════════")
  (println "  RAMBAN PAIR ANALYSIS — Real Grid Baseline")
  (println "═══════════════════════════════════════════════════")

  (let [dict-ws (vec (sort (dict/words)))
        orc (base/make-oracle oracle/stone-data)
        t0 (System/currentTimeMillis)
        result (find-ramban-cases orc dict-ws)
        detailed (find-ramban-detailed orc dict-ws)
        dt (- (System/currentTimeMillis) t0)]

    (println (str "\n  Dictionary words: " (count dict-ws)))
    (println (str "  Total illuminations checked: " (:total-illuminations result)))
    (println (str "  Ramban illuminations: " (:ramban-illuminations result)))
    (println (str "  Unique Ramban pairs: " (:pair-count result)))
    (println (str "  Time: " dt "ms"))

    (println "\n── Ramban Pairs (sorted by frequency) ──")
    (doseq [[[w1 w2] cnt] (sort-by val > (:by-pair result))]
      (let [t1 (dict/translate w1)
            t2 (dict/translate w2)]
        (println (format "  %-6s / %-6s  %3d×   %s / %s" w1 w2 cnt
                         (or t1 "?") (or t2 "?")))))

    ;; Detailed reader splits for each pair
    (println "\n── Reader Splits ──")
    (doseq [[[w1 w2] _] (sort-by val > (:by-pair result))]
      (let [relevant (filter (fn [{:keys [dict-readings]}]
                               (let [dw (set (vals dict-readings))]
                                 (and (dw w1) (dw w2))))
                             detailed)
            reader-map (atom {})]
        ;; For each relevant illumination, which reader sees which word?
        (doseq [{:keys [dict-readings]} relevant]
          (doseq [[reader word] dict-readings]
            (swap! reader-map update reader (fnil (fn [m] (update m word (fnil inc 0))) {}))))
        (println (format "\n  %s / %s  (%d illuminations):" w1 w2 (count relevant)))
        (doseq [r [:aaron :god :truth :mercy]]
          (let [counts (get @reader-map r {})]
            (when (seq counts)
              (println (format "    %-8s  %s"
                               (name r)
                               (str/join ", " (map (fn [[w c]] (str w "=" c "×")) (sort-by val > counts))))))))))

    (assoc result :detailed detailed :elapsed-ms dt)))

;; ══════════════════════════════════════════════════════════════
;; Permutation Test
;; ══════════════════════════════════════════════════════════════

(defn run-permutation-ramban
  "Run Ramban pair analysis on one shuffled grid."
  [rng dict-ws]
  (let [sd (base/shuffle-grid rng)
        orc (base/make-oracle sd)]
    (find-ramban-cases orc dict-ws)))

(defn run-permutation-test
  "Full permutation test for Ramban pairs.
   n: number of random grids (default 500).
   No rotational equivalence — readers are named and fixed."
  ([] (run-permutation-test 500))
  ([n]
   (println "\n═══════════════════════════════════════════════════")
   (println "  EXPERIMENT 092b — RAMBAN PAIR PERMUTATION TEST")
   (println (str "  " n " random grids. Same 72 letters. Named readers."))
   (println "  No rotational equivalence. The right hand is the right hand.")
   (println "═══════════════════════════════════════════════════")

   ;; Baseline
   (let [dict-ws (vec (sort (dict/words)))
         real-orc (base/make-oracle oracle/stone-data)
         t0b (System/currentTimeMillis)
         baseline (find-ramban-cases real-orc dict-ws)
         dtb (- (System/currentTimeMillis) t0b)
         _ (println (str "\n  Baseline: " (:pair-count baseline) " pairs, "
                         (:ramban-illuminations baseline) " illuminations"
                         " (" dtb "ms)"))

         ;; The 12 real pairs — track which survive in null grids
         real-pairs (:pairs baseline)
         _ (println (str "  Real pairs: " (count real-pairs)))

         ;; Permutations
         _ (println (str "\n── Running " n " permutations ──"))
         t0 (System/currentTimeMillis)
         progress (atom 0)
         results
         (vec (pmap (fn [seed]
                      (let [p (swap! progress inc)]
                        (when (zero? (mod p 50))
                          (let [elapsed (- (System/currentTimeMillis) t0)
                                rate (/ (double elapsed) p)]
                            (println (format "  %d/%d (%.0fms each, ~%.0fs remaining)"
                                             p n rate
                                             (/ (* rate (- n p)) 1000.0))))))
                      (let [rng (java.util.Random. seed)
                            r (run-permutation-ramban rng dict-ws)]
                        ;; Also check which real pairs appear
                        (assoc r :real-pair-matches
                               (count (cset/intersection real-pairs (:pairs r))))))
                    (range n)))
         dt (- (System/currentTimeMillis) t0)]

     (println (str "\n  Done. " n " permutations in " (format "%.1f" (/ dt 1000.0)) "s"
                    " (" (format "%.0f" (/ (double dt) n)) "ms each)"))

     ;; ── Analysis ──
     (println "\n═══════════════════════════════════════════════════")
     (println "  RESULTS — Real Grid vs Null Distribution")
     (println "═══════════════════════════════════════════════════")

     (let [stat (fn [k] (map k results))
           mean-fn (fn [vals] (/ (reduce + (map double vals)) (double (count vals))))
           std-fn (fn [vals]
                    (let [m (mean-fn vals)]
                      (Math/sqrt (/ (reduce + (map #(let [d (- (double %) m)] (* d d)) vals))
                                    (double (count vals))))))
           pctl-fn (fn [real-val vals]
                     (* 100.0 (/ (double (count (filter #(< % real-val) vals))) (count vals))))
           p-fn (fn [real-val vals higher?]
                  (let [exceeding (if higher?
                                    (count (filter #(>= % real-val) vals))
                                    (count (filter #(<= % real-val) vals)))]
                    (/ (double (inc exceeding)) (inc (count vals)))))

           ;; Pair count
           null-pairs (stat :pair-count)
           real-pc (:pair-count baseline)
           mean-pc (mean-fn null-pairs)
           std-pc (std-fn null-pairs)
           z-pc (if (pos? std-pc) (/ (- real-pc mean-pc) std-pc) 0.0)

           ;; Ramban illuminations
           null-ri (stat :ramban-illuminations)
           real-ri (:ramban-illuminations baseline)
           mean-ri (mean-fn null-ri)
           std-ri (std-fn null-ri)
           z-ri (if (pos? std-ri) (/ (- real-ri mean-ri) std-ri) 0.0)

           ;; Real pair matches
           null-rpm (stat :real-pair-matches)
           mean-rpm (mean-fn null-rpm)
           std-rpm (std-fn null-rpm)]

       ;; Unique pair count
       (println "\n── Unique Ramban Pairs ──")
       (println (format "  Real:   %d pairs" real-pc))
       (println (format "  Null:   %.1f ± %.1f (mean ± std)" mean-pc std-pc))
       (println (format "  z = %+.2f,  p = %.4f,  percentile = %.1f%%"
                        z-pc (p-fn real-pc null-pairs true) (pctl-fn real-pc null-pairs)))
       (println (format "  Null range: [%d, %d]"
                        (apply min null-pairs) (apply max null-pairs)))
       ;; Distribution
       (let [freqs (frequencies null-pairs)]
         (println "  Distribution:")
         (doseq [k (sort (keys freqs))]
           (println (format "    %2d pairs: %3d grids%s" k (freqs k)
                            (if (= k real-pc) " ← REAL" "")))))

       ;; Ramban illumination count
       (println "\n── Ramban Illumination Count ──")
       (println (format "  Real:   %d illuminations" real-ri))
       (println (format "  Null:   %.1f ± %.1f" mean-ri std-ri))
       (println (format "  z = %+.2f,  p = %.4f,  percentile = %.1f%%"
                        z-ri (p-fn real-ri null-ri true) (pctl-fn real-ri null-ri)))

       ;; Real pair survival
       (println "\n── Real Pair Survival ──")
       (println (format "  How many of the %d real pairs also appear in each null grid?" real-pc))
       (println (format "  Null:   %.1f ± %.1f" mean-rpm std-rpm))
       (println (format "  Null range: [%d, %d]"
                        (apply min null-rpm) (apply max null-rpm)))

       ;; Per-pair survival rate
       ;; Pairs are stored as sorted vectors [w1 w2] where w1 < w2
       (println "\n── Per-Pair Survival in Null Grids ──")
       (doseq [[[w1 w2] cnt] (sort-by val > (:by-pair baseline))]
         (let [;; Check both orderings since sort order may differ
               survives (count (filter (fn [r]
                                         (or (get (:by-pair r) [w1 w2])
                                             (get (:by-pair r) [w2 w1])))
                                       results))
               ;; Also count mean occurrences when present
               occur (keep (fn [r]
                             (or (get (:by-pair r) [w1 w2])
                                 (get (:by-pair r) [w2 w1])))
                           results)
               mean-occur (if (seq occur)
                            (/ (reduce + (map double occur)) (double (count occur)))
                            0.0)
               t1 (dict/translate w1)
               t2 (dict/translate w2)]
           (println (format "  %-6s/%-6s  real=%3d×  survives=%3d/%d (%5.1f%%)  mean-when-present=%.1f  %s / %s"
                            w1 w2 cnt survives n
                            (* 100.0 (/ (double survives) n))
                            mean-occur
                            (or t1 "?") (or t2 "?"))))))

     ;; Save
     (let [dir "data/experiments/092b"]
       (.mkdirs (java.io.File. dir))
       (spit (str dir "/baseline.edn") (pr-str (dissoc baseline :detailed)))
       (spit (str dir "/null-distribution.edn")
             (pr-str (mapv #(select-keys % [:pair-count :ramban-illuminations
                                            :real-pair-matches :pairs :by-pair])
                           results)))
       (println (str "\n  Saved to " dir "/")))

     {:baseline baseline :null results :n n})))

;; ══════════════════════════════════════════════════════════════
;; Quick check — find which specific pairs exist on a grid
;; ══════════════════════════════════════════════════════════════

(defn check-specific-pairs
  "Check if specific word pairs exist as Ramban pairs on a given oracle."
  [orc dict-ws pairs-to-check]
  (let [result (find-ramban-cases orc dict-ws)
        found-pairs (set (map (fn [[w1 w2]] #{w1 w2}) (keys (:by-pair result))))]
    (into {}
          (map (fn [[w1 w2]]
                 [[w1 w2]
                  {:found? (contains? found-pairs #{w1 w2})
                   :count (or (get (:by-pair result) [w1 w2])
                              (get (:by-pair result) [w2 w1])
                              0)}])
               pairs-to-check))))

;; ══════════════════════════════════════════════════════════════
;; Run
;; ══════════════════════════════════════════════════════════════

(defn run
  "Run the full Ramban pair permutation test."
  ([] (run 500))
  ([n]
   (let [baseline-detail (run-baseline)
         perm-results (run-permutation-test n)]
     {:baseline baseline-detail
      :permutation perm-results})))

(comment
  ;; Quick baseline only
  (def bl (run-baseline))

  ;; Smoke test — 1 permutation
  (def test-r (run-permutation-test 1))

  ;; Full test — 500 permutations
  (def results (run-permutation-test 500))

  ;; Just check specific pairs on one random grid
  (let [sd (base/shuffle-grid (java.util.Random. 42))
        orc (base/make-oracle sd)
        dict-ws (vec (sort (dict/words)))]
    (check-specific-pairs orc dict-ws
                          [["כבש" "שכב"] ["אל" "לא"] ["והיה" "יהוה"]]))

  :end)
