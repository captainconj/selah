(ns experiments.136-5d-survey
  "Experiment 136 (5D): Survey of all 5D decompositions of 304,850.

   11 non-trivial decompositions. The space is tightening.
   The canonical shadow: 5×7×10×13×67 (jubilee split into He × Yod).

   For each space:
   1. Center verse
   2. Walk smoothness
   3. Fold point coordinates
   4. Torah/YHWH ELS at stride values
   5. Aleph-tav fold-pair coordinate sharing"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [clojure.string :as str]))

(def all-5d (u/decompositions-k u/N 5))

;; The canonical 5D: jubilee split into its factors
(def canonical-5d [5 7 10 13 67])

;; ── 1. Centers ──────────────────────────────────────────────

(defn center-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-5d]
      (let [cc (u/center-coord dims)
            cp (u/center-pos dims)
            v  (u/verse-str s cp)]
        {:dims dims
         :center-coord cc
         :center-pos cp
         :verse v
         :axis-sum (apply + dims)}))))

;; ── 2. Walk smoothness ─────────────────────────────────────

(defn smoothness-survey []
  (for [dims all-5d]
    (assoc (u/walk-smoothness dims 50000)
           :dims dims)))

;; ── 3. Fold coordinates ────────────────────────────────────

(defn fold-survey []
  (for [dims all-5d]
    {:dims dims
     :fold-coord (u/fold-coord dims)
     :center-coord (u/center-coord dims)}))

;; ── 4. ELS at stride values ────────────────────────────────

(defn els-survey []
  (let [letters (u/torah-letters-vec)]
    (for [dims all-5d]
      (let [strides (u/compute-strides dims)
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
    (for [dims all-5d]
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

        ranked (sort-by :mean smoothness)

        output (with-out-str
                 (println "=== Experiment 136 (5D): Decomposition Survey ===")
                 (println (str (count all-5d) " non-trivial 5D decompositions"))
                 (println)

                 (println "── Centers ──")
                 (doseq [{:keys [dims center-coord center-pos verse axis-sum]} centers]
                   (printf "  %-28s  center=%-20s  pos=%-7d  sum=%-4d  %s\n"
                           (u/format-dims dims)
                           (pr-str center-coord)
                           center-pos axis-sum verse))
                 (println)

                 (println "── Walk Smoothness (ranked) ──")
                 (doseq [{:keys [dims mean max median step1-pct]} ranked]
                   (printf "  %-28s  mean=%-7.1f  max=%-5d  median=%-4d  step1=%.1f%%\n"
                           (u/format-dims dims)
                           (double mean) (long max) (long median)
                           (* 100.0 step1-pct)))
                 (println)

                 (println "── Fold Coordinates ──")
                 (doseq [{:keys [dims fold-coord]} folds]
                   (printf "  %-28s  fold=%s\n"
                           (u/format-dims dims)
                           (pr-str fold-coord)))
                 (println)

                 (println "── Aleph-Tav Fold Pairs ──")
                 (let [sorted-at (sort-by (fn [{:keys [axis-pcts]}]
                                           (- (apply max axis-pcts)))
                                         at-folds)]
                   (doseq [{:keys [dims total-ats axis-shares axis-pcts]} sorted-at]
                     (printf "  %-28s  %d ats  shares=%s\n"
                             (u/format-dims dims)
                             total-ats
                             (pr-str (mapv #(format "%.1f%%" (* 100.0 %)) axis-pcts)))))
                 (println)

                 (println "── ELS Hits ──")
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

    (u/save-results! "136-5d-survey"
                     {:experiment "136-5d"
                      :dimension 5
                      :decomposition-count (count all-5d)
                      :decompositions (vec all-5d)
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
  (count all-5d)  ;; 11
  (center-survey)
  (run-all)
  )
