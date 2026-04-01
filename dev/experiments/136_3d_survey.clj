(ns experiments.136-3d-survey
  "Experiment 136 (3D): Survey of all 3D decompositions of 304,850.

   58 non-trivial decompositions. The richest count.
   Notable: the near-cube 65×67×70, the canonical shadow 7×50×871,
   and jubilee×understanding×angel 50×67×91.

   For each space:
   1. Center verse
   2. Walk smoothness (Manhattan distance between consecutive letters)
   3. Fold point coordinates
   4. Torah/YHWH ELS at stride values
   5. Aleph-tav fold-pair coordinate sharing"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [clojure.string :as str]))

(def all-3d (u/decompositions-k u/N 3))

;; ── 1. Centers ──────────────────────────────────────────────

(defn center-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-3d]
      (let [cc (u/center-coord dims)
            cp (u/center-pos dims)
            v  (u/verse-str s cp)]
        {:dims dims
         :center-coord cc
         :center-pos cp
         :verse v}))))

;; ── 2. Walk smoothness ─────────────────────────────────────

(defn smoothness-survey []
  (for [dims all-3d]
    (assoc (u/walk-smoothness dims 50000)
           :dims dims)))

;; ── 3. Fold coordinates ────────────────────────────────────

(defn fold-survey []
  (for [dims all-3d]
    {:dims dims
     :fold-coord (u/fold-coord dims)
     :fold-partner-of-0 (u/idx->coord dims (u/fold-pair 0))
     :shared-axes-0 (u/axes-shared
                     (u/idx->coord dims 0)
                     (u/idx->coord dims (u/fold-pair 0)))}))

;; ── 4. ELS at stride values ────────────────────────────────
;; 58 spaces × multiple strides — expensive. Sample wisely.

(defn els-survey
  "ELS survey. Only check dim values and strides under 10,000
   to keep computation tractable."
  []
  (let [letters (u/torah-letters-vec)]
    (for [dims all-3d]
      (let [strides (u/compute-strides dims)
            ;; Only check stride values under a threshold
            all-skips (distinct (concat dims strides))
            reasonable (filter #(and (> % 1) (< % 10000)) all-skips)]
        {:dims dims
         :strides strides
         :els (into (sorted-map)
                    (for [skip reasonable]
                      [skip {:torah (u/els-at-stride letters "תורה" skip)
                             :yhwh  (u/els-at-stride letters "יהוה" skip)}]))}))))

;; ── 5. Aleph-tav fold analysis ─────────────────────────────

(defn at-fold-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-3d]
      (assoc (u/aleph-tav-fold-analysis dims s)
             :dims dims))))

;; ── Run all ─────────────────────────────────────────────────

(defn run-all []
  (let [s (u/ensure-space!)

        centers    (vec (center-survey))
        smoothness (vec (smoothness-survey))
        folds      (vec (fold-survey))
        at-folds   (vec (at-fold-survey))
        els-data   (vec (els-survey))

        ;; Find which spaces center on similar verses
        center-books (group-by #(first (str/split (:verse %) #" "))
                               centers)

        ;; Find smoothest walks
        ranked (sort-by :mean smoothness)

        output (with-out-str
                 (println "=== Experiment 136 (3D): Decomposition Survey ===")
                 (println (str (count all-3d) " non-trivial 3D decompositions"))
                 (println)

                 (println "── Centers (grouped by book) ──")
                 (doseq [[book entries] (sort-by key center-books)]
                   (printf "  %s: %d spaces\n" book (count entries))
                   (doseq [{:keys [dims center-coord verse]} entries]
                     (printf "    %s  center=%s  %s\n"
                             (u/format-dims dims)
                             (pr-str center-coord) verse)))
                 (println)

                 (println "── Walk Smoothness (top 10 smoothest) ──")
                 (doseq [{:keys [dims mean max median step1-pct]} (take 10 ranked)]
                   (printf "  %s  mean=%.1f  max=%d  median=%d  step1=%.1f%%\n"
                           (u/format-dims dims)
                           (double mean) (long max) (long median)
                           (* 100.0 step1-pct)))
                 (println "  ...")
                 (println "── Walk Smoothness (bottom 5 roughest) ──")
                 (doseq [{:keys [dims mean max median step1-pct]} (take-last 5 ranked)]
                   (printf "  %s  mean=%.1f  max=%d  median=%d  step1=%.1f%%\n"
                           (u/format-dims dims)
                           (double mean) (long max) (long median)
                           (* 100.0 step1-pct)))
                 (println)

                 (println "── Fold Coordinates ──")
                 (doseq [{:keys [dims fold-coord]} folds]
                   (printf "  %s  fold=%s\n"
                           (u/format-dims dims)
                           (pr-str fold-coord)))
                 (println)

                 (println "── Aleph-Tav Fold Pairs (sorted by sharing) ──")
                 (let [sorted-at (sort-by (fn [{:keys [axis-pcts]}]
                                           (- (apply max axis-pcts)))
                                         at-folds)]
                   (doseq [{:keys [dims total-ats axis-shares axis-pcts]} sorted-at]
                     (printf "  %s  %d ats  shares=%s  pcts=%s\n"
                             (u/format-dims dims)
                             total-ats
                             (pr-str axis-shares)
                             (pr-str (mapv #(format "%.1f%%" (* 100.0 %)) axis-pcts)))))
                 (println)

                 (println "── ELS Hits (non-zero only) ──")
                 (doseq [{:keys [dims els]} els-data]
                   (let [hits (filter (fn [[_ v]]
                                       (or (pos? (get-in v [:torah :total] 0))
                                           (pos? (get-in v [:yhwh :total] 0))))
                                     els)]
                     (when (seq hits)
                       (printf "  %s:\n" (u/format-dims dims))
                       (doseq [[skip {:keys [torah yhwh]}] hits]
                         (printf "    skip=%-6d  torah=%-3d  yhwh=%-3d\n"
                                 skip (:total torah) (:total yhwh)))))))]

    (u/save-results! "136-3d-survey"
                     {:experiment "136-3d"
                      :dimension 3
                      :decomposition-count (count all-3d)
                      :decompositions (vec all-3d)
                      :centers centers
                      :smoothness smoothness
                      :folds folds
                      :at-folds at-folds
                      :els els-data}
                     output)
    (print output)
    :done))

;; ── REPL ────────────────────────────────────────────────────

(comment
  (count all-3d)  ;; 58
  (center-survey)
  (first (smoothness-survey))
  (first (fold-survey))
  (run-all)
  )
