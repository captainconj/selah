(ns experiments.136-2d-survey
  "Experiment 136 (2D): Survey of all 2D decompositions of 304,850.

   23 non-trivial decompositions (divisor pairs with both > 1).
   A 2D decomposition arranges the Torah as a grid — rows × columns.

   For each space:
   1. Center verse
   2. Walk smoothness (Manhattan distance between consecutive letters)
   3. Fold point coordinates
   4. Torah/YHWH ELS at stride values
   5. Aleph-tav fold-pair coordinate sharing"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [clojure.string :as str]))

(def all-2d (u/decompositions-k u/N 2))

;; ── 1. Centers ──────────────────────────────────────────────

(defn center-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-2d]
      (let [cc (u/center-coord dims)
            cp (u/center-pos dims)
            v  (u/verse-str s cp)]
        {:dims dims
         :center-coord cc
         :center-pos cp
         :verse v}))))

;; ── 2. Walk smoothness ─────────────────────────────────────

(defn smoothness-survey []
  (for [dims all-2d]
    (assoc (u/walk-smoothness dims 50000)
           :dims dims)))

;; ── 3. Fold coordinates ────────────────────────────────────

(defn fold-survey []
  (for [dims all-2d]
    (let [fc (u/fold-coord dims)
          ;; In 2D, fold is a vertical line at the center column
          ;; Check: does fold-partner mirror across column center?
          test-pos 0
          partner (u/fold-pair test-pos)
          c1 (u/idx->coord dims test-pos)
          c2 (u/idx->coord dims partner)]
      {:dims dims
       :fold-coord fc
       :first-coord c1
       :last-coord c2
       :shared-axes (u/axes-shared c1 c2)})))

;; ── 4. ELS at stride values ────────────────────────────────

(defn els-survey []
  (let [letters (u/torah-letters-vec)]
    (for [dims all-2d]
      {:dims dims
       :strides (u/compute-strides dims)
       :els (u/stride-els-survey dims letters)})))

;; ── 5. Aleph-tav fold analysis ─────────────────────────────

(defn at-fold-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-2d]
      (assoc (u/aleph-tav-fold-analysis dims s)
             :dims dims))))

;; ── Run all ─────────────────────────────────────────────────

(defn run-all []
  (let [s (u/ensure-space!)
        letters (u/torah-letters-vec)

        centers    (vec (center-survey))
        smoothness (vec (smoothness-survey))
        folds      (vec (fold-survey))
        at-folds   (vec (at-fold-survey))

        ;; ELS is expensive — only check dim values and strides
        els-results (vec (for [dims all-2d]
                           {:dims dims
                            :strides (u/compute-strides dims)
                            :els (u/stride-els-survey dims letters)}))

        output (with-out-str
                 (println "=== Experiment 136 (2D): Decomposition Survey ===")
                 (println (str (count all-2d) " non-trivial 2D decompositions"))
                 (println)

                 (println "── Centers ──")
                 (doseq [{:keys [dims center-coord center-pos verse]} centers]
                   (printf "  %s  center=%s  pos=%d  %s\n"
                           (u/format-dims dims)
                           (pr-str center-coord)
                           center-pos verse))
                 (println)

                 (println "── Walk Smoothness ──")
                 (println "  (Lower mean = smoother walk through text)")
                 (doseq [{:keys [dims mean max median step1-pct]} (sort-by :mean smoothness)]
                   (printf "  %s  mean=%.1f  max=%d  median=%d  step1=%.1f%%\n"
                           (u/format-dims dims)
                           (double mean) (long max) (long median)
                           (* 100.0 step1-pct)))
                 (println)

                 (println "── Fold Coordinates ──")
                 (doseq [{:keys [dims fold-coord]} folds]
                   (printf "  %s  fold-point=%s\n"
                           (u/format-dims dims)
                           (pr-str fold-coord)))
                 (println)

                 (println "── Aleph-Tav Fold Pairs ──")
                 (doseq [{:keys [dims total-ats axis-shares axis-pcts]} at-folds]
                   (printf "  %s  %d ats  axis-sharing=%s  pcts=%s\n"
                           (u/format-dims dims)
                           total-ats
                           (pr-str axis-shares)
                           (pr-str (mapv #(format "%.1f%%" (* 100.0 %)) axis-pcts))))
                 (println)

                 (println "── ELS at Stride Values ──")
                 (doseq [{:keys [dims els]} els-results]
                   (printf "  %s:\n" (u/format-dims dims))
                   (doseq [[skip {:keys [torah yhwh]}] els]
                     (when (or (pos? (:total torah)) (pos? (:total yhwh)))
                       (printf "    skip=%d  torah=%d  yhwh=%d\n"
                               skip (:total torah) (:total yhwh))))))]

    (u/save-results! "136-2d-survey"
                     {:experiment "136-2d"
                      :dimension 2
                      :decomposition-count (count all-2d)
                      :decompositions (vec all-2d)
                      :centers centers
                      :smoothness smoothness
                      :folds folds
                      :at-folds at-folds
                      :els els-results}
                     output)
    (print output)
    :done))

;; ── REPL ────────────────────────────────────────────────────

(comment
  (center-survey)
  (smoothness-survey)
  (fold-survey)
  (at-fold-survey)
  (els-survey)
  (run-all)
  )
