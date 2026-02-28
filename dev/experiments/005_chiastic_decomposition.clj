(ns experiments.005-chiastic-decomposition
  "The Torah as palindromic operator: A-B-C-B'-A' decomposition.
   Each book is a 22-dimensional vector (letter frequency distribution).
   Do the paired books reflect each other?
   Run: clojure -M:dev -m experiments.005-chiastic-decomposition"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(def alphabet "אבגדהוזחטיכלמנסעפצקרשת")

(defn letter-profile
  "22-dimensional vector: normalized frequency of each Hebrew letter."
  [letters]
  (let [n     (count letters)
        freqs (frequencies letters)]
    (mapv (fn [ch] (/ (double (get freqs ch 0)) n)) alphabet)))

(defn letter-energy-profile
  "22-dimensional vector: fraction of total gematria energy per letter."
  [letters]
  (let [total (double (g/total letters))
        freqs (frequencies letters)]
    (mapv (fn [ch]
            (let [cnt (get freqs ch 0)
                  val (g/letter-value ch)]
              (/ (* (double cnt) val) total)))
          alphabet)))

(defn dot-product [a b]
  (reduce + (map * a b)))

(defn magnitude [v]
  (Math/sqrt (dot-product v v)))

(defn cosine-similarity [a b]
  (/ (dot-product a b) (* (magnitude a) (magnitude b))))

(defn vec-sub [a b] (mapv - a b))
(defn vec-add [a b] (mapv + a b))
(defn vec-scale [s v] (mapv #(* s %) v))

(defn euclidean-distance [a b]
  (magnitude (vec-sub a b)))

;; ── Main analysis ────────────────────────────────────────────

(defn -main []
  (println "Loading books...")
  (let [books (mapv (fn [b]
                      (let [letters (sefaria/book-letters b)]
                        (println (format "  %s: %,d letters" b (count letters)))
                        {:name b :letters letters}))
                    book-names)

        ;; Letter frequency profiles (22-dim vectors)
        profiles (mapv #(letter-profile (:letters %)) books)
        ;; Energy profiles
        energy-profiles (mapv #(letter-energy-profile (:letters %)) books)]

    ;; ── Pairwise cosine similarity ──────────────────────────
    (println "\n=== Cosine Similarity Matrix (Letter Frequencies) ===")
    (println (format "  %-12s  %-8s  %-8s  %-8s  %-8s  %-8s"
                     "" "Gen" "Exod" "Lev" "Num" "Deut"))
    (println (apply str (repeat 60 "-")))
    (doseq [i (range 5)]
      (print (format "  %-12s" (nth book-names i)))
      (doseq [j (range 5)]
        (print (format "  %8.6f" (cosine-similarity (nth profiles i) (nth profiles j)))))
      (println))

    ;; ── Chiastic pairs ──────────────────────────────────────
    (println "\n=== Chiastic Pairing ===")
    (let [pairs [["A  (Genesis)" "A' (Deuteronomy)" 0 4]
                 ["B  (Exodus)"  "B' (Numbers)"     1 3]]]
      (doseq [[label-a label-b ia ib] pairs]
        (let [pa (nth profiles ia)
              pb (nth profiles ib)
              cos-freq (cosine-similarity pa pb)
              dist-freq (euclidean-distance pa pb)
              ea (nth energy-profiles ia)
              eb (nth energy-profiles ib)
              cos-energy (cosine-similarity ea eb)
              dist-energy (euclidean-distance ea eb)]
          (println (format "\n  %s ↔ %s" label-a label-b))
          (println (format "    Frequency cosine similarity: %.6f" cos-freq))
          (println (format "    Frequency Euclidean distance: %.6f" dist-freq))
          (println (format "    Energy cosine similarity:     %.6f" cos-energy))
          (println (format "    Energy Euclidean distance:    %.6f" dist-energy)))))

    ;; ── Symmetric / Antisymmetric decomposition ─────────────
    (println "\n=== Symmetric / Antisymmetric Decomposition ===")
    (println "  Symmetric  = (book_i + book_{5-i}) / 2  — what the pair shares")
    (println "  Antisymmetric = (book_i - book_{5-i}) / 2  — what differs\n")

    (doseq [[label ia ib] [["A/A' (Gen/Deut)" 0 4]
                            ["B/B' (Exod/Num)" 1 3]]]
      (let [pa (nth profiles ia)
            pb (nth profiles ib)
            sym  (vec-scale 0.5 (vec-add pa pb))
            anti (vec-scale 0.5 (vec-sub pa pb))
            sym-mag  (magnitude sym)
            anti-mag (magnitude anti)
            ratio    (/ anti-mag sym-mag)]
        (println (format "  %s:" label))
        (println (format "    Symmetric magnitude:     %.6f" sym-mag))
        (println (format "    Antisymmetric magnitude: %.6f" anti-mag))
        (println (format "    Anti/Sym ratio:          %.6f  (0 = perfect mirror)" ratio))
        ;; Which letters differ most?
        (let [diffs (map-indexed (fn [i v]
                                   {:letter (nth alphabet i)
                                    :value  (g/letter-value (nth alphabet i))
                                    :diff   v
                                    :abs-diff (Math/abs v)})
                                 anti)
              sorted (reverse (sort-by :abs-diff diffs))]
          (println "    Largest differences (antisymmetric component):")
          (doseq [{:keys [letter value diff]} (take 5 sorted)]
            (println (format "      %s (val %3d): %+.6f" letter value diff))))))

    ;; ── C (Leviticus) — distance from average of all ────────
    (println "\n=== Leviticus as Center ===")
    (let [mean-profile (vec-scale 0.2 (reduce vec-add profiles))
          lev-profile  (nth profiles 2)
          cos  (cosine-similarity lev-profile mean-profile)
          dist (euclidean-distance lev-profile mean-profile)]
      (println (format "  Cosine similarity (Lev vs mean of all): %.6f" cos))
      (println (format "  Euclidean distance (Lev vs mean of all): %.6f" dist)))

    ;; ── Cross-pair comparison ────────────────────────────────
    (println "\n=== Non-chiastic pairs (control) ===")
    (doseq [[a b] [[0 1] [0 2] [0 3] [1 2] [1 4] [2 3] [2 4] [3 4]]]
      (let [cos (cosine-similarity (nth profiles a) (nth profiles b))]
        (println (format "  %-12s ↔ %-12s : %.6f"
                         (nth book-names a) (nth book-names b) cos))))

    ;; ── Eigenspectrum: treat 5 books as 5×22 matrix ─────────
    (println "\n=== Book-Letter Matrix (5 × 22) — Row norms ===")
    (println "  Each book is a row, each letter is a column")
    (println "  Showing per-book letter distribution magnitudes:\n")
    (doseq [i (range 5)]
      (let [p (nth profiles i)
            m (magnitude p)]
        (println (format "  %-12s  magnitude = %.6f" (nth book-names i) m))))

    ;; ── Per-letter variance across books ─────────────────────
    (println "\n=== Per-Letter Variance Across Books ===")
    (println "  Which letters vary most between books?\n")
    (println (format "  %-4s  %5s  %8s  %8s  %8s"
                     "Ltr" "Value" "Mean" "StdDev" "CV"))
    (println (apply str (repeat 40 "-")))
    (doseq [col (range 22)]
      (let [ch   (nth alphabet col)
            vals (mapv #(nth % col) profiles)
            mean (/ (reduce + vals) 5.0)
            std  (Math/sqrt (/ (reduce + (map #(Math/pow (- % mean) 2) vals)) 5.0))
            cv   (if (pos? mean) (/ std mean) 0.0)]
        (println (format "  %s    %4d  %8.5f  %8.5f  %8.4f"
                         ch (g/letter-value ch) mean std cv)))))
  (println "\nDone."))
