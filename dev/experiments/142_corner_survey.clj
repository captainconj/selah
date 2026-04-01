(ns experiments.142-corner-survey
  "Experiment 142: Corner Survey — every decomposition of 304,850.

   What does the Torah say at the edges of each box?
   Corners, centers, face centers for all 111 decompositions
   across 3D-6D.

   Run: clojure -M:dev -e \"(require '[experiments.142-corner-survey :as exp]) (exp/run-canonical)\"
        clojure -M:dev -e \"(require '[experiments.142-corner-survey :as exp]) (exp/run-6d)\"
        clojure -M:dev -e \"(require '[experiments.142-corner-survey :as exp]) (exp/run-all)\""
  (:require [selah.space.coords :as sc]
            [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════
;; SURVEY ENGINE
;; ══════════════════════════════════════════════════════

(defn ensure-space! []
  (when-not (sc/space)
    (sc/build!))
  (sc/space))

(defn coord->idx
  "N-dimensional coordinate to linear index."
  [dims coord]
  (reduce (fn [acc [dim val]]
            (+ (* acc dim) val))
          0
          (map vector dims coord)))

(defn all-corners
  "Generate all 2^k corners of a k-dimensional box."
  [dims]
  (let [k (count dims)]
    (for [bits (range (long (Math/pow 2 k)))]
      (vec (for [i (range k)]
             (if (bit-test bits i) (dec (nth dims i)) 0))))))

(defn center-coord
  "Center coordinate of the box."
  [dims]
  (mapv #(quot (dec %) 2) dims))

(defn face-centers
  "Center of each face (2k faces for k dimensions)."
  [dims]
  (let [c (center-coord dims)
        k (count dims)]
    (for [i (range k)
          v [0 (dec (nth dims i))]]
      {:axis i :value v
       :coord (assoc c i v)})))

(defn lookup-position
  "Look up a linear position in the Torah. Returns verse info."
  [s idx]
  (when (and (>= idx 0) (< idx 304850))
    (let [letter (sc/letter-at s idx)
          vref (sc/verse-at s idx)]
      (merge vref
             {:idx idx
              :letter (str letter)
              :letter-gv (g/letter-value letter)}))))

(defn survey-decomposition
  "Survey one decomposition: corners, center, face centers."
  [s dims]
  (let [corners (all-corners dims)
        center (center-coord dims)
        faces (face-centers dims)]
    {:dims (vec dims)
     :ndim (count dims)
     :sum (reduce + dims)
     :center (let [idx (coord->idx dims center)]
               (assoc (lookup-position s idx) :coord center))
     :corners (vec (for [c corners]
                     (let [idx (coord->idx dims c)]
                       (assoc (lookup-position s idx) :coord c))))
     :faces (vec (for [{:keys [axis value coord]} faces]
                   (let [idx (coord->idx dims coord)]
                     (assoc (lookup-position s idx)
                            :coord coord :axis axis :value value))))}))

(defn print-survey
  "Pretty-print a survey result."
  [{:keys [dims ndim sum center corners faces]}]
  (println (format "\n═══ %s ═══" (str/join " × " dims)))
  (println (format "  %dD, axis sum = %d\n" ndim sum))

  (println (format "  CENTER: (%s) → %s %d:%d  letter=%s"
                   (str/join "," (:coord center))
                   (:book center) (:ch center) (:vs center)
                   (:letter center)))

  (println "\n  CORNERS:")
  (doseq [c (sort-by :idx corners)]
    (println (format "    (%s) idx=%-6d → %s %d:%d  %s"
                     (str/join "," (:coord c))
                     (:idx c)
                     (:book c) (:ch c) (:vs c)
                     (:letter c))))

  (when (seq faces)
    (println "\n  FACE CENTERS:")
    (doseq [f (sort-by :idx faces)]
      (println (format "    axis=%d val=%-5d (%s) → %s %d:%d  %s"
                       (:axis f) (:value f)
                       (str/join "," (:coord f))
                       (:book f) (:ch f) (:vs f)
                       (:letter f))))))

;; ══════════════════════════════════════════════════════
;; ALL DECOMPOSITIONS
;; ══════════════════════════════════════════════════════

(defn factorizations
  "All ordered factorizations of n into exactly k parts >= 2."
  [n k]
  (if (= k 1)
    (if (>= n 2) [[n]] [])
    (for [f (range 2 (inc n))
          :when (zero? (mod n f))
          rest (factorizations (/ n f) (dec k))
          :when (or (empty? rest) (<= f (first rest)))]
      (into [f] rest))))

(def all-decompositions
  "All decompositions of 304,850 into 3-6 factors."
  (delay
    (let [n 304850]
      (into []
            (for [k [3 4 5 6]
                  f (factorizations n k)]
              f)))))

;; ══════════════════════════════════════════════════════
;; RUN
;; ══════════════════════════════════════════════════════

(defn run-canonical []
  (println "═══════════════════════════════════════════════════")
  (println "  THE CANONICAL 4D: 7 × 50 × 13 × 67")
  (println "═══════════════════════════════════════════════════")
  (let [s (ensure-space!)]
    (print-survey (survey-decomposition s [7 50 13 67]))))

(defn run-6d []
  (println "═══════════════════════════════════════════════════")
  (println "  THE 6D: 2 × 5 × 5 × 7 × 13 × 67")
  (println "  64 corners. The full prime factorization.")
  (println "═══════════════════════════════════════════════════")
  (let [s (ensure-space!)]
    (print-survey (survey-decomposition s [2 5 5 7 13 67]))))

(defn run-dimension
  "Run all decompositions of a given dimension."
  [k]
  (let [s (ensure-space!)
        facts (factorizations 304850 k)]
    (println (format "═══ %dD DECOMPOSITIONS: %d total ═══\n" k (count facts)))
    (doseq [f (sort facts)]
      (print-survey (survey-decomposition s f)))))

(defn run-all []
  (println "═══════════════════════════════════════════════════")
  (println "  CORNER SURVEY — ALL 111 DECOMPOSITIONS")
  (println "  304,850 = 2 × 5² × 7 × 13 × 67")
  (println "═══════════════════════════════════════════════════")
  (let [s (ensure-space!)]
    (doseq [k [3 4 5 6]]
      (let [facts (factorizations 304850 k)]
        (println (format "\n\n╔═══ %dD: %d decompositions ═══╗\n" k (count facts)))
        (doseq [f (sort facts)]
          (print-survey (survey-decomposition s f)))))

    ;; Corner frequency analysis
    (println "\n\n═══ CORNER FREQUENCY ANALYSIS ═══\n")
    (let [all-facts @all-decompositions
          all-surveys (mapv #(survey-decomposition s %) all-facts)
          all-corner-verses (mapcat (fn [survey]
                                      (map (fn [c]
                                             {:book (:book c) :ch (:ch c) :vs (:vs c)
                                              :dims (:dims survey)})
                                           (:corners survey)))
                                    all-surveys)
          by-verse (group-by (fn [v] (format "%s %d:%d" (:book v) (:ch v) (:vs v)))
                             all-corner-verses)
          sorted (sort-by (comp - count second) by-verse)]
      (println "Most frequent corner verses (across all decompositions):\n")
      (doseq [[verse entries] (take 30 sorted)]
        (println (format "  %3d× — %s" (count entries) verse))))))

;; (run-canonical)
;; (run-6d)
;; (run-all)
