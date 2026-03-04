(ns experiments.097-per-head-basins
  "Experiment 097 — Per-Head Basin Landscapes.

   The oracle has four heads (Aaron, God, Right, Left).
   Experiment 096 ran the combined landscape — all readers merged.
   Now we split: does each head agree on the fixed points?

   From 091: the lamb split — God and Right see כבש, Aaron and Left see שכב.
   Does the per-head *basin* landscape agree?"
  (:require [selah.basin :as basin]
            [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

;; ── Helpers ─────────────────────────────────────────────────

(defn- save-edn [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(def data-dir "data/experiments/097/")

;; ── Phase 1: Per-head sweep ─────────────────────────────────

(defn run-sweep
  "Run all Torah words through forward-by-head. One call per word."
  []
  (let [torah (vec (sort (dict/torah-words)))
        _ (println (format "Running per-head sweep on %d words..." (count torah)))
        t0 (System/currentTimeMillis)
        step-map (basin/landscape-by-head torah)
        elapsed (/ (- (System/currentTimeMillis) t0) 1000.0)]
    (println (format "  Sweep complete: %.1fs" elapsed))
    step-map))

;; ── Phase 2: Per-head classification ────────────────────────

(defn classify-all-heads
  "Classify every word for each of the four readers."
  [step-map]
  (println "Classifying per head...")
  (into {}
    (for [reader [:aaron :god :right :left]]
      (let [classified (basin/classify-head step-map reader)
            counts (frequencies (map :class (vals classified)))]
        (println (format "  %s: %s" (name reader) (pr-str counts)))
        [reader classified]))))

;; ── Phase 3: Agreement analysis ─────────────────────────────

(defn agreement-analysis
  "For each word, how do the four heads agree?"
  [per-head-classified]
  (let [words (keys (per-head-classified :aaron))
        readers [:aaron :god :right :left]]
    (into {}
      (for [w words]
        (let [per-reader (into {}
                           (for [r readers]
                             [r (get-in per-head-classified [r w])]))
              attractors (into {}
                           (for [r readers
                                 :let [info (per-reader r)]
                                 :when (:attractor info)]
                             [r (:attractor info)]))
              classes (into {} (for [r readers] [r (:class (per-reader r))]))
              unique-attractors (set (vals attractors))
              n-agree (count unique-attractors)]
          [w {:per-reader per-reader
              :attractors attractors
              :classes classes
              :agreement (cond
                           (zero? n-agree) :dead-end
                           (= 1 n-agree)   :unanimous
                           (= 2 n-agree)   (if (>= (apply max (vals (frequencies (vals attractors)))) 3)
                                              :majority
                                              :split)
                           :else            :fragmented)}])))))

;; ── Phase 4: Extract structures ─────────────────────────────

(defn extract-structures
  "Pull out the interesting structures from the agreement analysis."
  [agreement per-head-classified step-map]
  (let [readers [:aaron :god :right :left]

        ;; Unanimous fixed points — fixed for ALL 4 heads
        unanimous-fps (vec (sort (for [[w info] agreement
                                       :when (and (= :unanimous (:agreement info))
                                                  (every? #(= :fixed-point %) (vals (:classes info))))]
                                   w)))

        ;; Unanimous attractors — all heads agree on attractor (may be transient)
        unanimous-attractors (vec (sort-by first
                                   (for [[w info] agreement
                                         :when (= :unanimous (:agreement info))
                                         :let [attr (first (vals (:attractors info)))]
                                         :when attr]
                                     [w attr])))

        ;; Split words — different attractors per head
        splits (vec (sort-by first
                      (for [[w info] agreement
                            :when (#{:split :majority :fragmented} (:agreement info))]
                        {:word w
                         :meaning (or (dict/translate w) (dict/translate-english w))
                         :gv (g/word-value w)
                         :attractors (:attractors info)
                         :classes (:classes info)})))

        ;; Per-head cycles
        per-head-cycles
        (into {}
          (for [reader readers]
            (let [classified (per-head-classified reader)
                  cycle-words (set (keep (fn [[w info]]
                                          (when (= :cycle (:class info)) w))
                                        classified))
                  ;; Resolve orbits using the precomputed step-map
                  orbits (when (seq cycle-words)
                           (loop [remaining cycle-words
                                  orbits []]
                             (if (empty? remaining)
                               orbits
                               (let [start (first remaining)
                                     orbit (loop [w start path [] guard 0]
                                             (cond
                                               (> guard 50) path
                                               (and (seq path) (= w start)) path
                                               :else
                                               (let [nxt (get-in step-map [w reader :next])]
                                                 (if (nil? nxt)
                                                   (conj path w)
                                                   (recur nxt (conj path w) (inc guard))))))
                                     orbit-set (set orbit)]
                                 (recur (set/difference remaining orbit-set)
                                        (conj orbits {:orbit orbit :period (count orbit)}))))))]
              [reader {:cycle-count (count cycle-words)
                       :orbits (or orbits [])}])))

        ;; Dead ends per head
        dead-ends-per-head
        (into {}
          (for [reader readers]
            [reader (set (keep (fn [[w info]]
                                 (when (= :dead-end (:class info)) w))
                               (per-head-classified reader)))]))

        ;; Dead ends should be identical across heads (illumination is reader-independent)
        dead-ends-identical? (apply = (vals dead-ends-per-head))

        ;; Agreement distribution
        agreement-dist (frequencies (map (comp :agreement val) agreement))]

    {:unanimous-fixed-points unanimous-fps
     :unanimous-fp-count (count unanimous-fps)
     :unanimous-attractor-count (count (filter (fn [[w a]] (not= w a)) unanimous-attractors))
     :split-count (count splits)
     :splits splits
     :per-head-cycles per-head-cycles
     :dead-ends-per-head (into {} (for [[r s] dead-ends-per-head] [r (count s)]))
     :dead-ends-identical? dead-ends-identical?
     :agreement-distribution agreement-dist}))

;; ── Phase 5: Spotlight words ────────────────────────────────

(defn spotlight
  "Detailed per-head breakdown for key words."
  [step-map per-head-classified]
  (let [readers [:aaron :god :right :left]
        words ["כבש" "שכב" "יהוה" "אלהים" "אהבה" "אמת" "חיים"
               "תורה" "שלום" "ברית" "משה" "דם" "אור" "חסד"
               "בינה" "כהן" "שבת" "ארון" "בן" "אב"]]
    (vec
      (for [w words
            :when (contains? step-map w)]
        (let [heads (step-map w)
              classified (into {} (for [r readers]
                                   [r (get-in per-head-classified [r w])]))]
          {:word w
           :meaning (or (dict/translate w) (dict/translate-english w))
           :gv (g/word-value w)
           :per-head (into {}
                       (for [r readers]
                         [r {:next (get-in heads [r :next])
                             :next-meaning (when-let [n (get-in heads [r :next])]
                                             (or (dict/translate n) (dict/translate-english n)))
                             :weight (get-in heads [r :weight])
                             :class (:class (classified r))
                             :attractor (:attractor (classified r))}]))})))))

;; ── Phase 6: Build per-head index ───────────────────────────

(defn build-per-head-index
  "Compact index: {word → {:aaron {:class :attractor} :god {...} ...}}"
  [per-head-classified]
  (let [readers [:aaron :god :right :left]
        words (keys (per-head-classified :aaron))]
    (into {}
      (for [w words]
        [w (into {}
             (for [r readers]
               (let [info (get-in per-head-classified [r w])]
                 [r {:class (:class info)
                     :attractor (:attractor info)
                     :steps (:steps info)}])))]))))

;; ── Phase 7: Cross-reference with 096 combined ─────────────

(defn cross-reference-096
  "Compare per-head attractors with the combined (096) attractor."
  [per-head-classified]
  (let [readers [:aaron :god :right :left]
        words (keys (per-head-classified :aaron))
        combined-idx (when (.exists (io/file "data/experiments/096/word-index.edn"))
                       (edn/read-string (slurp "data/experiments/096/word-index.edn")))]
    (when combined-idx
      (let [;; Words where per-head unanimous attractor differs from combined
            mismatches
            (vec (sort-by first
                   (for [w words
                         :let [combined-attr (:attractor (combined-idx w))
                               per-head-attrs (into {} (for [r readers]
                                                         [r (:attractor (get-in per-head-classified [r w]))]))
                               unanimous? (= 1 (count (set (vals per-head-attrs))))
                               per-head-attr (when unanimous? (first (vals per-head-attrs)))]
                         :when (and combined-attr per-head-attr
                                    (not= combined-attr per-head-attr))]
                     {:word w
                      :combined-attractor combined-attr
                      :per-head-attractor per-head-attr})))]
        {:combined-words (count combined-idx)
         :unanimous-match-count (- (count words) (count mismatches))
         :mismatch-count (count mismatches)
         :mismatches (take 50 mismatches)}))))

;; ── Run ─────────────────────────────────────────────────────

(defn run-experiment! []
  (println "=== Experiment 097: Per-Head Basin Landscapes ===")
  (println)

  ;; 1. Sweep
  (let [step-map (run-sweep)
        _ (println)

        ;; 2. Classify
        per-head (classify-all-heads step-map)
        _ (println)

        ;; 3. Agreement
        _ (println "Analyzing agreement...")
        agreement (agreement-analysis per-head)
        structures (extract-structures agreement per-head step-map)
        _ (println (format "  Agreement: %s" (pr-str (:agreement-distribution structures))))
        _ (println (format "  Unanimous fixed points: %d" (:unanimous-fp-count structures)))
        _ (println (format "  Splits: %d" (:split-count structures)))
        _ (println (format "  Dead ends identical? %s" (:dead-ends-identical? structures)))
        _ (println)

        ;; 4. Spotlight
        _ (println "Spotlight words:")
        spot (spotlight step-map per-head)
        _ (doseq [{:keys [word meaning per-head]} spot]
            (let [nexts (into {} (for [[r info] per-head] [r (:next info)]))]
              (println (format "  %s (%s): A→%s G→%s R→%s L→%s %s"
                               word (or meaning "?")
                               (:aaron nexts) (:god nexts) (:right nexts) (:left nexts)
                               (if (= 1 (count (set (vals nexts)))) "UNANIMOUS" "SPLIT")))))
        _ (println)

        ;; 5. Per-head index
        per-head-index (build-per-head-index per-head)

        ;; 6. Cross-reference 096
        _ (println "Cross-referencing with 096 combined landscape...")
        xref (cross-reference-096 per-head)
        _ (when xref
            (println (format "  Matches: %d, Mismatches: %d"
                             (:unanimous-match-count xref) (:mismatch-count xref))))
        _ (println)

        ;; 7. Build summary
        summary {:total-words (count step-map)
                 :per-head-class-counts
                 (into {} (for [r [:aaron :god :right :left]]
                            [r (frequencies (map :class (vals (per-head r))))]))
                 :agreement-distribution (:agreement-distribution structures)
                 :unanimous-fixed-points (:unanimous-fp-count structures)
                 :split-count (:split-count structures)
                 :dead-ends (:dead-ends-per-head structures)
                 :dead-ends-identical? (:dead-ends-identical? structures)
                 :per-head-cycles (into {} (for [[r info] (:per-head-cycles structures)]
                                             [r {:count (:cycle-count info)
                                                 :orbits (mapv #(select-keys % [:orbit :period])
                                                               (:orbits info))}]))
                 :cross-reference-096 (when xref
                                        (select-keys xref [:unanimous-match-count :mismatch-count]))}]

    ;; 8. Save
    (println "Saving...")
    (save-edn (str data-dir "summary.edn") summary)
    (save-edn (str data-dir "per-head-index.edn") per-head-index)
    (save-edn (str data-dir "agreement.edn")
              (into {} (for [[w info] agreement]
                         [w (select-keys info [:agreement :attractors :classes])])))
    (save-edn (str data-dir "splits.edn") (:splits structures))
    (save-edn (str data-dir "per-head-cycles.edn") (:per-head-cycles structures))
    (save-edn (str data-dir "spotlight.edn") spot)
    (when xref
      (save-edn (str data-dir "cross-reference-096.edn") xref))

    ;; 9. Verification
    (println)
    (println "Verification:")
    (doseq [r [:aaron :god :right :left]]
      (let [n (count (per-head r))]
        (println (format "  %s: %d words (expected %d) %s"
                         (name r) n (count step-map)
                         (if (= n (count step-map)) "OK" "MISMATCH")))))
    (println (format "  Dead ends identical across heads: %s" (:dead-ends-identical? structures)))

    ;; 10. Top splits
    (when (seq (:splits structures))
      (println)
      (println (format "Top splits (%d total):" (count (:splits structures))))
      (doseq [{:keys [word meaning attractors]} (take 20 (:splits structures))]
        (println (format "  %s (%s): %s"
                         word (or meaning "?")
                         (str/join ", " (for [[r a] attractors] (str (name r) "→" a)))))))

    (println)
    (println "Done.")))

(comment
  (run-experiment!)

  ;; Quick test: single word
  (basin/step-all-heads "כבש")
  (oracle/forward-by-head "כבש")

  ;; Per-head walk for lamb
  (basin/follow-chain
    (fn [w] (:next ((:aaron (basin/step-all-heads w)))))
    "כבש" 10)
  )
