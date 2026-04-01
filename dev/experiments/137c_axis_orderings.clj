(ns experiments.137c-axis-orderings
  "Experiment 137c: All 120 Axis Orderings of the 5D Space.

   The canonical 5D decomposition is [5, 7, 10, 13, 67].
   The product is 304,850 regardless of axis order.
   But the MEANING of each axis — which dimension of the text
   it controls — changes with every permutation.

   For all 120 orderings:
   - The cube axes are always {10, 13, 67} (the three axes >= 10)
   - The pinning axes are always {5, 7}
   - But their POSITIONS in the coordinate system differ

   For each permutation we ask:
   1. Is Lev 8:35 inside the cube?
   2. Does YHWH appear in the Ark?
   3. What is the Ark GV?
   4. What does the oracle read between the cherubim?"
  (:require [experiments.137-hoh-across-spaces :as hoh]
            [experiments.136-survey-utils :as u]
            [selah.space.coords :as c]
            [selah.oracle :as o]
            [clojure.string :as str]))

;; ── Permutations ────────────────────────────────────────

(defn permutations
  "All permutations of a collection of distinct elements."
  [coll]
  (if (empty? coll)
    [[]]
    (for [x coll
          p (permutations (remove #{x} coll))]
      (vec (cons x p)))))

;; ── Analysis ────────────────────────────────────────────

(defn analyze-ordering
  "Run the HoH analysis for a single axis ordering.
   Returns the analysis map augmented with the dims."
  [s dims]
  (let [results (hoh/analyze-space s dims)]
    ;; For 5D with exactly 3 axes >= 10, there's exactly one
    ;; cube-axis assignment. Return the first (and only) result.
    (first results)))

(defn signature-flags
  "Extract the key signature properties from an analysis result."
  [result]
  (when result
    {:has-lev835    (boolean (:has-lev835 result))
     :has-yhwh      (pos? (:yhwh-in-ark result))
     :ark-gv-2603   (= 2603 (:ark-gv result))
     :oracle-moses  (some #(= "איש מות משה" (:text %))
                          (:between-oracle result))}))

;; ── Main run ────────────────────────────────────────────

(defn run
  "Test all 120 permutations of [5 7 10 13 67]."
  []
  (let [axes [5 7 10 13 67]
        perms (permutations axes)
        s (c/space)
        _ (println (str "=== Experiment 137c: All " (count perms)
                        " Axis Orderings of " (pr-str axes) " ===\n"))
        output (StringBuilder.)
        results (atom [])]

    (.append output (str "=== Experiment 137c: All 120 Axis Orderings ===\n"))
    (.append output (str "Base axes: " (pr-str axes) "\n"))
    (.append output (str "Cube axes are always the three >= 10: {10, 13, 67}\n"))
    (.append output (str "Pinning axes are always {5, 7}\n\n"))

    ;; Analyze each permutation
    (doseq [[idx perm] (map-indexed vector perms)]
      (let [dims (vec perm)
            result (analyze-ordering s dims)
            flags (signature-flags result)]
        (swap! results conj (merge {:ordering dims} result flags))
        (println (str "  [" (inc idx) "/120] " (u/format-dims dims)
                      (when (:has-lev835 flags) " ★LEV")
                      (when (:has-yhwh flags) " ★YHWH")
                      (when (:ark-gv-2603 flags) " ★GV2603")
                      (when (:oracle-moses flags) " ★MOSES")))

        (.append output (str "── " (u/format-dims dims) " ──\n"))
        (.append output (str "  Cube axes (indices): " (pr-str (:cube-axes result)) "\n"))
        (.append output (str "  Pin axes: " (pr-str (zipmap (:pin-axes result) (:pin-values result))) "\n"))
        (.append output (str "  Cube center: " (:cube-center result) "\n"))
        (.append output (str "  Lev 8:35? " (:has-lev835 result) "\n"))
        (.append output (str "  YHWH in Ark: " (:yhwh-in-ark result) "\n"))
        (.append output (str "  Ark GV: " (:ark-gv result) "\n"))
        (.append output (str "  Between text: " (:between-text result) "\n"))
        (when (seq (:between-oracle result))
          (.append output (str "  Oracle: "
                               (str/join ", " (map :text (:between-oracle result)))
                               "\n")))
        (.append output "\n")))

    ;; ── Grouping ──────────────────────────────────────────
    (let [all @results

          ;; Group by signature
          grouped (group-by (fn [r]
                              [(boolean (:has-lev835 r))
                               (boolean (:has-yhwh r))
                               (boolean (:ark-gv-2603 r))
                               (boolean (:oracle-moses r))])
                            all)]

      (.append output "\n═══════════════════════════════════════════════\n")
      (.append output "GROUPING BY SIGNATURE\n")
      (.append output "═══════════════════════════════════════════════\n\n")

      (doseq [[[lev yhwh gv moses] members] (sort-by (fn [[k _]] (- (count (filter true? k)))) grouped)]
        (.append output (str "── "
                             (str/join " + " (remove nil?
                                               [(when lev "LEV-8:35")
                                                (when yhwh "YHWH")
                                                (when gv "GV=2603")
                                                (when moses "ORACLE=MOSES")]))
                             (when (not-any? true? [lev yhwh gv moses]) "NONE")
                             " (" (count members) " orderings) ──\n"))
        (doseq [m members]
          (.append output (str "  " (u/format-dims (:ordering m))
                               " | center=" (:cube-center m)
                               " | Ark GV=" (:ark-gv m)
                               " | YHWH=" (:yhwh-in-ark m)
                               (when (seq (:between-oracle m))
                                 (str " | oracle=" (str/join "," (map :text (:between-oracle m)))))
                               "\n")))
        (.append output "\n"))

      ;; ── Summary stats ──────────────────────────────────
      (.append output "\n═══ SUMMARY ═══\n")
      (.append output (str "Total orderings: " (count all) "\n"))
      (.append output (str "With Lev 8:35: " (count (filter :has-lev835 all)) "\n"))
      (.append output (str "With YHWH in Ark: " (count (filter :has-yhwh all)) "\n"))
      (.append output (str "With Ark GV=2603: " (count (filter :ark-gv-2603 all)) "\n"))
      (.append output (str "With oracle=Moses: " (count (filter :oracle-moses all)) "\n"))
      (.append output (str "With ALL FOUR: "
                           (count (filter #(and (:has-lev835 %)
                                                (:has-yhwh %)
                                                (:ark-gv-2603 %)
                                                (:oracle-moses %))
                                          all))
                           "\n"))

      ;; Distinct Ark GVs
      (let [ark-gvs (sort (distinct (map :ark-gv all)))]
        (.append output (str "\nDistinct Ark GVs: " (count ark-gvs) "\n"))
        (doseq [gv ark-gvs]
          (let [with-gv (filter #(= gv (:ark-gv %)) all)]
            (.append output (str "  GV=" gv " (" (count with-gv) " orderings)\n")))))

      ;; Distinct oracle readings
      (let [oracle-texts (sort (distinct (map (fn [r]
                                                 (str/join "," (map :text (:between-oracle r))))
                                               all)))]
        (.append output (str "\nDistinct oracle readings: " (count oracle-texts) "\n"))
        (doseq [t oracle-texts]
          (let [with-t (filter #(= t (str/join "," (map :text (:between-oracle %)))) all)]
            (.append output (str "  \"" t "\" (" (count with-t) " orderings)\n"))))))

    (u/save-results! "137c-axis-orderings" @results (str output))
    (println (str "\nDone. Results: data/experiments/137c-axis-orderings-output.txt"))
    (println)
    (print (str output))
    @results))
