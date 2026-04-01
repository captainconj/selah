(ns experiments.136-4d-survey
  "Experiment 136 (4D): Survey of all 4D decompositions of 304,850.

   41 non-trivial decompositions. The canonical space lives here:
   7×50×13×67 (sum=137, the ONLY decomposition with {7,13,67}).

   For each space:
   1. Center verse
   2. Walk smoothness (Manhattan distance between consecutive letters)
   3. Fold point coordinates
   4. Torah/YHWH ELS at stride values
   5. Aleph-tav fold-pair coordinate sharing

   This is the key dimension — every other survey is measured against it."
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [clojure.string :as str]))

(def all-4d (u/decompositions-k u/N 4))
(def canonical [7 50 13 67])

;; ── 1. Centers ──────────────────────────────────────────────

(defn center-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-4d]
      (let [cc (u/center-coord dims)
            cp (u/center-pos dims)
            v  (u/verse-str s cp)]
        {:dims dims
         :canonical? (= dims canonical)
         :center-coord cc
         :center-pos cp
         :verse v
         :axis-sum (apply + dims)}))))

;; ── 2. Walk smoothness ─────────────────────────────────────

(defn smoothness-survey []
  (for [dims all-4d]
    (assoc (u/walk-smoothness dims 50000)
           :dims dims
           :canonical? (= dims canonical))))

;; ── 3. Fold coordinates ────────────────────────────────────

(defn fold-survey []
  (for [dims all-4d]
    (let [fc (u/fold-coord dims)
          cc (u/center-coord dims)]
      {:dims dims
       :canonical? (= dims canonical)
       :fold-coord fc
       :center-coord cc
       ;; In any decomposition, fold flips through center
       ;; fold-coord should equal center-coord (by construction)
       :fold-at-center? (= fc cc)})))

;; ── 4. ELS at stride values ────────────────────────────────

(defn els-survey []
  (let [letters (u/torah-letters-vec)]
    (for [dims all-4d]
      (let [strides (u/compute-strides dims)
            all-skips (distinct (concat dims strides))
            reasonable (filter #(and (> % 1) (< % 10000)) all-skips)]
        {:dims dims
         :canonical? (= dims canonical)
         :strides strides
         :els (into (sorted-map)
                    (for [skip reasonable]
                      [skip {:torah (u/els-at-stride letters "תורה" skip)
                             :yhwh  (u/els-at-stride letters "יהוה" skip)}]))}))))

;; ── 5. Aleph-tav fold analysis ─────────────────────────────

(defn at-fold-survey []
  (let [s (u/ensure-space!)]
    (for [dims all-4d]
      (assoc (u/aleph-tav-fold-analysis dims s)
             :dims dims
             :canonical? (= dims canonical)))))

;; ── Run all ─────────────────────────────────────────────────

(defn run-all []
  (let [s (u/ensure-space!)

        centers    (vec (center-survey))
        smoothness (vec (smoothness-survey))
        folds      (vec (fold-survey))
        at-folds   (vec (at-fold-survey))
        els-data   (vec (els-survey))

        ;; Key comparisons: canonical vs the rest
        canon-smooth (first (filter :canonical? smoothness))
        ranked (sort-by :mean smoothness)
        canon-rank (inc (count (take-while #(not (:canonical? %)) ranked)))

        canon-at (first (filter :canonical? at-folds))

        output (with-out-str
                 (println "=== Experiment 136 (4D): Decomposition Survey ===")
                 (println (str (count all-4d) " non-trivial 4D decompositions"))
                 (println (str "Canonical: " (u/format-dims canonical) "  sum=137"))
                 (println)

                 (println "── Centers ──")
                 (doseq [{:keys [dims center-coord center-pos verse axis-sum canonical?]} centers]
                   (printf "  %s%-22s  center=%-16s  pos=%-7d  sum=%-4d  %s\n"
                           (if canonical? "★ " "  ")
                           (u/format-dims dims)
                           (pr-str center-coord)
                           center-pos
                           axis-sum
                           verse))
                 (println)

                 ;; Do multiple spaces land on the same verse?
                 (println "── Center Convergence ──")
                 (let [by-verse (group-by :verse centers)]
                   (doseq [[verse entries] (sort-by (comp - count val) by-verse)
                           :when (> (count entries) 1)]
                     (printf "  %s: %d spaces → %s\n"
                             verse (count entries)
                             (str/join ", " (map #(u/format-dims (:dims %)) entries)))))
                 (println)

                 (println "── Walk Smoothness (ranked) ──")
                 (printf "  Canonical rank: %d of %d\n" canon-rank (count ranked))
                 (println)
                 (doseq [{:keys [dims mean max median step1-pct canonical?]} ranked]
                   (printf "  %s%-22s  mean=%-7.1f  max=%-5d  median=%-4d  step1=%.1f%%\n"
                           (if canonical? "★ " "  ")
                           (u/format-dims dims)
                           (double mean) (long max) (long median)
                           (* 100.0 step1-pct)))
                 (println)

                 (println "── Fold Analysis ──")
                 (doseq [{:keys [dims fold-coord fold-at-center? canonical?]} folds]
                   (printf "  %s%-22s  fold=%s  at-center?=%s\n"
                           (if canonical? "★ " "  ")
                           (u/format-dims dims)
                           (pr-str fold-coord)
                           fold-at-center?))
                 (println)

                 (println "── Aleph-Tav Fold Pairs ──")
                 (let [sorted-at (sort-by (fn [{:keys [axis-pcts]}]
                                           (- (apply max axis-pcts)))
                                         at-folds)]
                   (doseq [{:keys [dims total-ats axis-shares axis-pcts canonical?]} sorted-at]
                     (printf "  %s%-22s  %d ats  shares=%s  pcts=%s\n"
                             (if canonical? "★ " "  ")
                             (u/format-dims dims)
                             total-ats
                             (pr-str axis-shares)
                             (pr-str (mapv #(format "%.1f%%" (* 100.0 %)) axis-pcts)))))
                 (println)

                 (println "── ELS Hits (non-zero only) ──")
                 (doseq [{:keys [dims els canonical?]} els-data]
                   (let [hits (filter (fn [[_ v]]
                                       (or (pos? (get-in v [:torah :total] 0))
                                           (pos? (get-in v [:yhwh :total] 0))))
                                     els)]
                     (when (seq hits)
                       (printf "  %s%s:\n"
                               (if canonical? "★ " "  ")
                               (u/format-dims dims))
                       (doseq [[skip {:keys [torah yhwh]}] hits]
                         (printf "      skip=%-6d  torah=%-3d  yhwh=%-3d\n"
                                 skip (:total torah) (:total yhwh)))))))]

    (u/save-results! "136-4d-survey"
                     {:experiment "136-4d"
                      :dimension 4
                      :decomposition-count (count all-4d)
                      :decompositions (vec all-4d)
                      :canonical canonical
                      :canonical-rank canon-rank
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
  (count all-4d)  ;; 41
  (center-survey)
  (first (smoothness-survey))

  ;; Quick canonical check
  (let [s (u/ensure-space!)]
    (u/verse-str s (u/center-pos canonical)))
  ;; should be Lev 8:35

  (run-all)
  )
