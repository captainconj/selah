(ns experiments.136-6d-survey
  "Experiment 136 (6D): Survey of the single 6D decomposition of 304,850.

   Only one non-trivial 6D: 2×5×5×7×13×67 — the prime factorization.
   This is the floor. Every axis is irreducible.

   The two 5s are the two He's in YHWH.
   Jubilee (50=2×5²) was atomized into witness and two breaths.

   Tests:
   1. Center verse
   2. Walk smoothness
   3. Fold point coordinates
   4. Torah/YHWH ELS at stride values (all prime strides)
   5. Aleph-tav fold-pair coordinate sharing across 6 axes"
  (:require [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [clojure.string :as str]))

(def the-6d [2 5 5 7 13 67])

;; ── Analysis ────────────────────────────────────────────────

(defn run-all []
  (let [s (u/ensure-space!)
        dims the-6d
        letters (u/torah-letters-vec)

        ;; Center
        cc (u/center-coord dims)
        cp (u/center-pos dims)
        cv (u/verse-str s cp)

        ;; Walk smoothness
        smooth (u/walk-smoothness dims 50000)

        ;; Fold
        fc (u/fold-coord dims)
        fold-0 (u/idx->coord dims 0)
        fold-last (u/idx->coord dims (u/fold-pair 0))

        ;; ELS at all prime strides
        strides (u/compute-strides dims)
        all-skips (distinct (concat dims strides))
        reasonable (filter #(and (> % 1) (< % 50000)) all-skips)
        els-data (into (sorted-map)
                       (for [skip reasonable]
                         [skip {:torah (u/els-at-stride letters "תורה" skip)
                                :yhwh  (u/els-at-stride letters "יהוה" skip)}]))

        ;; Aleph-tav fold analysis
        at-data (u/aleph-tav-fold-analysis dims s)

        ;; Axis labels
        axis-labels ["witness(2)" "He₁(5)" "He₂(5)" "completeness(7)" "love(13)" "understanding(67)"]

        output (with-out-str
                 (println "=== Experiment 136 (6D): The Floor ===")
                 (println (str "Single decomposition: " (u/format-dims dims)))
                 (println (str "Sum = " (apply + dims) " = 99"))
                 (println)

                 (println "── Axes ──")
                 (doseq [[i label dim] (map vector (range) axis-labels dims)]
                   (printf "  axis %d: %s  (size %d)\n" i label dim))
                 (println)

                 (println "── Center ──")
                 (printf "  center-coord = %s\n" (pr-str cc))
                 (printf "  center-pos   = %d\n" cp)
                 (printf "  verse        = %s\n" cv)
                 (println)

                 (println "── Walk Smoothness ──")
                 (printf "  mean=%.1f  max=%d  median=%d  step1=%.1f%%\n"
                         (double (:mean smooth))
                         (long (:max smooth))
                         (long (:median smooth))
                         (* 100.0 (:step1-pct smooth)))
                 (println)

                 (println "── Fold ──")
                 (printf "  fold-point coord = %s\n" (pr-str fc))
                 (printf "  first letter     = %s\n" (pr-str fold-0))
                 (printf "  last letter      = %s\n" (pr-str fold-last))
                 (println)

                 (println "── Strides ──")
                 (printf "  strides = %s\n" (pr-str strides))
                 (println "  stride meanings:")
                 (doseq [[i s] (map-indexed vector strides)]
                   (printf "    stride[%d] = %d  (skip along axis %d: %s)\n"
                           i s i (nth axis-labels i)))
                 (println)

                 (println "── ELS at Stride/Axis Values ──")
                 (doseq [[skip {:keys [torah yhwh]}] els-data]
                   (printf "  skip=%-7d  torah=%-3d  yhwh=%-3d%s\n"
                           skip (:total torah) (:total yhwh)
                           (cond
                             (some #{skip} dims) (str "  ← axis value")
                             (some #{skip} strides) (str "  ← stride")
                             :else "")))
                 (println)

                 (println "── Aleph-Tav Fold Pairs ──")
                 (printf "  total aleph-tavs: %d\n" (:total-ats at-data))
                 (println "  axis sharing:")
                 (doseq [[i label shares pct] (map vector
                                                   (range)
                                                   axis-labels
                                                   (:axis-shares at-data)
                                                   (:axis-pcts at-data))]
                   (printf "    %s: %d pairs share (%.1f%%)\n"
                           label shares (* 100.0 pct))))]

    (u/save-results! "136-6d-survey"
                     {:experiment "136-6d"
                      :dimension 6
                      :decomposition the-6d
                      :center {:coord cc :pos cp :verse cv}
                      :smoothness smooth
                      :fold {:coord fc :first fold-0 :last fold-last}
                      :strides strides
                      :els els-data
                      :at-folds at-data}
                     output)
    (print output)
    :done))

;; ── REPL ────────────────────────────────────────────────────

(comment
  (u/center-coord the-6d)
  (u/compute-strides the-6d)
  (run-all)
  )
