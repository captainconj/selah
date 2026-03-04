(ns experiments.096-basin-classification
  "Experiment 096 — Basin Landscape Classification.

   Reads the raw landscape (basin/landscape over all 12,826 Torah words),
   classifies every word, resolves cycle orbits, computes statistics,
   and saves structured EDN for basin.clj to load lazily."
  (:require [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; ── Helpers ─────────────────────────────────────────────────

(defn- save-edn
  "Save data to an EDN file."
  [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(def data-dir "data/experiments/096/")

;; ── Load raw landscape ──────────────────────────────────────

(defn load-landscape []
  (println "Loading raw landscape...")
  (let [ls (edn/read-string (slurp "data/basin-landscape.edn"))]
    (println (format "  %d attractors, %d cycle entries, %d dead ends, %d total"
                     (count (:by-attractor ls))
                     (count (:cycles ls))
                     (count (:dead-ends ls))
                     (:total ls)))
    ls))

;; ── Resolve cycles ──────────────────────────────────────────

(defn resolve-orbit
  "Given a cycle-word (a word known to be in a cycle), re-walk
   step by step to extract the full orbit. Returns the orbit
   as a vector of words in traversal order."
  [start-word]
  (loop [current start-word
         path []]
    (if (and (seq path) (= current start-word))
      path
      (let [result (basin/step current)]
        (if (nil? result)
          path ;; shouldn't happen for known cycle members
          (recur (:next result) (conj path current)))))))

(defn resolve-cycles
  "Given the raw cycle entries from landscape, resolve into distinct orbits.
   Returns [{:orbit [w1 w2 ...] :period N :transients [words feeding in]}]."
  [cycle-entries]
  (println "Resolving cycles...")
  (let [;; Group by cycle-word to find distinct orbits
        groups (group-by :cycle-word cycle-entries)
        cycle-words (set (keys groups))

        ;; For each unique cycle-word, walk to find the orbit
        orbits (into {}
                     (for [cw cycle-words]
                       (let [orbit (resolve-orbit cw)]
                         (println (format "  Orbit from %s: %s (period %d)"
                                          cw (pr-str orbit) (count orbit)))
                         [cw {:orbit orbit :period (count orbit)}])))

        ;; Now merge orbits that share members (e.g. if A→B→A, both A and B are orbit members)
        orbit-member-set (set (mapcat :orbit (vals orbits)))

        ;; Transients: cycle entries whose :word is NOT in any orbit
        transients (remove #(contains? orbit-member-set (:word %)) cycle-entries)

        ;; Deduplicate orbits — two cycle-words might resolve to the same orbit
        unique-orbits (->> (vals orbits)
                           (map (fn [{:keys [orbit period]}]
                                  {:orbit orbit
                                   :period period
                                   :canonical (set orbit)}))
                           (group-by :canonical)
                           vals
                           (map first)
                           (map #(dissoc % :canonical)))]

    (println (format "  %d unique orbits, %d orbit members, %d cycle-transients"
                     (count unique-orbits)
                     (count orbit-member-set)
                     (count transients)))

    ;; Attach transients to their target orbit
    (mapv (fn [{:keys [orbit period]}]
            (let [orbit-set (set orbit)
                  feeding (->> transients
                               (filter #(contains? orbit-set (:cycle-word %)))
                               (mapv :word))]
              {:orbit orbit
               :period period
               :transients feeding}))
          unique-orbits)))

;; ── Classification ──────────────────────────────────────────

(defn classify-all
  "Classify every word into exactly one class.
   Returns {:word-index {word → info}, :attractors [...], :cycles [...], :dead-ends #{...}}."
  [landscape resolved-cycles]
  (println "Classifying all words...")
  (let [;; Build orbit sets
        orbit-members (set (mapcat :orbit resolved-cycles))
        cycle-transient-set (set (mapcat :transients resolved-cycles))

        ;; Map cycle transients to their target orbit's canonical member
        cycle-transient->orbit
        (into {}
              (for [cyc resolved-cycles
                    t (:transients cyc)]
                [t (first (:orbit cyc))]))

        ;; Dead ends
        dead-end-set (set (map :word (:dead-ends landscape)))

        ;; Build attractor index: word → attractor info
        attractor-basins (:by-attractor landscape)

        ;; Map from attractor word → basin data
        attractor->basin (into {} (for [b attractor-basins] [(:fixed-point b) b]))

        ;; Map from basin word → attractor
        word->attractor (into {}
                              (for [b attractor-basins
                                    w (:words b)]
                                [w (:fixed-point b)]))

        ;; Fixed points: attractors where the word is its own attractor
        ;; (every word in by-attractor has a fixed-point, but basin members flow TO it)
        ;; A fixed point is a word that maps to itself under step
        fixed-point-set (set (map :fixed-point attractor-basins))

        ;; Now classify each word and compute steps
        all-words (concat
                   (mapcat :words attractor-basins)
                   (map :word (:cycles landscape))
                   (map :word (:dead-ends landscape)))

        word-index
        (into {}
              (for [w all-words]
                (let [info (cond
                             ;; Dead end
                             (contains? dead-end-set w)
                             {:class :dead-end :attractor nil :steps nil
                              :gv (g/word-value w)
                              :meaning (or (dict/translate w) (dict/translate-english w))}

                             ;; Cycle member
                             (contains? orbit-members w)
                             {:class :cycle-member
                              :attractor nil
                              :orbit (first (keep (fn [cyc]
                                                    (when (some #{w} (:orbit cyc))
                                                      (first (:orbit cyc))))
                                                  resolved-cycles))
                              :steps nil
                              :gv (g/word-value w)
                              :meaning (or (dict/translate w) (dict/translate-english w))}

                             ;; Cycle transient
                             (contains? cycle-transient-set w)
                             {:class :cycle-transient
                              :attractor nil
                              :orbit (cycle-transient->orbit w)
                              :steps 1 ;; feeds directly into orbit
                              :gv (g/word-value w)
                              :meaning (or (dict/translate w) (dict/translate-english w))}

                             ;; Fixed point
                             (and (contains? fixed-point-set w)
                                  (= w (word->attractor w)))
                             {:class :fixed-point
                              :attractor w
                              :steps 0
                              :gv (g/word-value w)
                              :meaning (or (dict/translate w) (dict/translate-english w))}

                             ;; Transient (flows to a fixed point in ≥1 steps)
                             (contains? (set (keys word->attractor)) w)
                             (let [attr (word->attractor w)
                                   ;; Count steps by tracing
                                   steps (loop [cur w n 0]
                                           (if (= cur attr)
                                             n
                                             (let [r (basin/step cur)]
                                               (if (or (nil? r) (> n 30))
                                                 n
                                                 (recur (:next r) (inc n))))))]
                               {:class :transient
                                :attractor attr
                                :steps steps
                                :gv (g/word-value w)
                                :meaning (or (dict/translate w) (dict/translate-english w))})

                             :else
                             {:class :unknown :gv (g/word-value w)})]
                  [w info])))]

    (let [classes (frequencies (map :class (vals word-index)))]
      (println "  Classification counts:" classes)
      (println "  Total classified:" (count word-index)))

    {:word-index word-index
     :attractor-basins attractor-basins
     :resolved-cycles resolved-cycles
     :dead-ends dead-end-set}))

;; ── Statistics ──────────────────────────────────────────────

(defn compute-stats
  "Compute summary statistics from the classification."
  [{:keys [word-index attractor-basins resolved-cycles dead-ends]}]
  (println "Computing statistics...")
  (let [idx (vals word-index)
        total (count idx)
        class-counts (frequencies (map :class idx))

        ;; Basin size distribution
        basin-sizes (mapv :basin-size attractor-basins)
        basin-hist (sort-by first (frequencies basin-sizes))

        ;; Transient lengths
        transients (filter #(= :transient (:class %)) idx)
        steps (keep :steps transients)
        step-vals (sort steps)

        ;; Cycle info
        cycle-periods (mapv :period resolved-cycles)

        stats {:total total
               :class-counts class-counts
               :class-pct (into {} (map (fn [[k v]] [k (format "%.1f%%" (* 100.0 (/ v total)))])
                                        class-counts))
               :basins {:count (count attractor-basins)
                        :size-distribution (into (sorted-map) basin-hist)
                        :total-words (reduce + basin-sizes)
                        :mean-size (when (seq basin-sizes)
                                     (double (/ (reduce + basin-sizes) (count basin-sizes))))
                        :max-size (when (seq basin-sizes) (apply max basin-sizes))
                        :median-size (when (seq basin-sizes)
                                       (nth (sort basin-sizes) (/ (count basin-sizes) 2)))}
               :cycles {:count (count resolved-cycles)
                        :periods cycle-periods
                        :total-members (count (mapcat :orbit resolved-cycles))
                        :total-transients (count (mapcat :transients resolved-cycles))}
               :transients {:count (count transients)
                            :mean-steps (when (seq steps) (double (/ (reduce + steps) (count steps))))
                            :median-steps (when (seq steps) (nth step-vals (/ (count step-vals) 2)))
                            :max-steps (when (seq steps) (apply max steps))
                            :step-distribution (into (sorted-map) (frequencies steps))}
               :dead-ends {:count (count dead-ends)}}]

    (println (format "  Classes: %s" (pr-str class-counts)))
    (println (format "  Basins: %d attractors, max size %d, mean %.1f"
                     (count attractor-basins)
                     (or (:max-size (:basins stats)) 0)
                     (or (:mean-size (:basins stats)) 0.0)))
    (println (format "  Cycles: %d orbits, periods %s"
                     (count resolved-cycles) (pr-str cycle-periods)))
    (when (seq steps)
      (println (format "  Transient steps: mean %.1f, median %d, max %d"
                       (:mean-steps (:transients stats))
                       (:median-steps (:transients stats))
                       (:max-steps (:transients stats)))))
    stats))

;; ── Build output ────────────────────────────────────────────

(defn build-attractors
  "Build sorted attractor list with meanings."
  [attractor-basins]
  (->> attractor-basins
       (mapv (fn [{:keys [fixed-point meaning gv basin-size words]}]
               {:word fixed-point
                :meaning (or meaning (dict/translate fixed-point) (dict/translate-english fixed-point))
                :gv gv
                :basin-size basin-size
                :basin words}))
       (sort-by (comp - :basin-size))
       vec))

(defn build-cycles
  "Build cycle data for output."
  [resolved-cycles]
  (mapv (fn [{:keys [orbit period transients]}]
          {:orbit (mapv (fn [w]
                          {:word w
                           :meaning (or (dict/translate w) (dict/translate-english w))
                           :gv (g/word-value w)})
                        orbit)
           :period period
           :transients (mapv (fn [w]
                               {:word w
                                :meaning (or (dict/translate w) (dict/translate-english w))
                                :gv (g/word-value w)})
                             transients)})
        resolved-cycles))

;; ── Run ─────────────────────────────────────────────────────

(defn run-experiment! []
  (println "=== Experiment 096: Basin Classification ===")
  (println)

  ;; 1. Load
  (let [ls (load-landscape)

        ;; 2. Resolve cycles
        resolved (resolve-cycles (:cycles ls))

        ;; 3. Classify
        classified (classify-all ls resolved)

        ;; 4. Statistics
        stats (compute-stats classified)

        ;; 5. Build outputs
        attractors (build-attractors (:attractor-basins classified))
        cycles (build-cycles resolved)]

    ;; 6. Save
    (println)
    (println "Saving files...")
    (save-edn (str data-dir "summary.edn") stats)
    (save-edn (str data-dir "word-index.edn") (:word-index classified))
    (save-edn (str data-dir "attractors.edn") attractors)
    (save-edn (str data-dir "cycles.edn") cycles)

    ;; 7. Print top 20 attractors
    (println)
    (println "Top 20 attractors by basin size:")
    (doseq [{:keys [word meaning gv basin-size]} (take 20 attractors)]
      (println (format "  %s (%s, GV=%d) — basin=%d"
                       word (or meaning "?") gv basin-size)))

    ;; 8. Print cycles
    (println)
    (println "Cycles:")
    (doseq [{:keys [orbit period transients]} cycles]
      (let [orbit-str (->> orbit (map :word) (clojure.string/join " → "))]
        (println (format "  Period %d: %s → %s"
                         period orbit-str (-> orbit first :word)))
        (when (seq transients)
          (println (format "    ← transients: %s"
                           (->> transients (map :word) (clojure.string/join ", ")))))))

    ;; 9. Verify
    (println)
    (println "Verification:")
    (let [idx (:word-index classified)
          total (count idx)]
      (println (format "  Total words classified: %d (expected 12,826)" total))
      (println (format "  Sum matches: %s" (= total 12826))))

    (println)
    (println "Done.")))

(comment
  (run-experiment!)

  ;; Quick checks
  (def ls (load-landscape))
  (resolve-orbit "רחב")
  (resolve-orbit "בחר")
  )
