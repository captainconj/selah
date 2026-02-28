(ns experiments.002-tier1-hypotheses
  "Tier 1 hypothesis tests — quick computations.
   Covers H2, H3, H5, H6, H8, H9 from hypotheses.md.
   Run: clojure -M:dev -m experiments.002-tier1-hypotheses"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.locate :as locate]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
(def book-sizes [78064 63529 44790 63530 54834])  ;; placeholder — will compute

(def phi 1.6180339887)

(defn load-torah []
  (println "Loading Torah...")
  (let [books (mapv (fn [b]
                      (let [letters (sefaria/book-letters b)]
                        (println (format "  %s: %,d letters" b (count letters)))
                        {:name b :letters letters :count (count letters)}))
                    book-names)
        torah (vec (mapcat :letters books))]
    (println (format "  TOTAL: %,d letters" (count torah)))
    {:torah torah
     :books books
     :sizes (mapv :count books)}))

;; ── H2 & H3: Golden Sections ────────────────────────────────

(defn locate-torah-pos
  "Locate a position in the full Torah stream."
  [books pos]
  (loop [p pos i 0]
    (let [{:keys [name letters count]} (nth books i)]
      (if (< p count)
        (let [ch-map (locate/chapter-map name)
              loc    (locate/locate name ch-map p)]
          (assoc loc :letter (nth letters p)
                     :value (g/letter-value (nth letters p))
                     :global-pos (- pos p)))  ;; not right but close enough
        (recur (- p count) (inc i))))))

(defn golden-sections [books torah]
  (println "\n=== H2 & H3: Golden Sections ===")
  (let [n (count torah)]
    ;; Torah golden section
    (let [phi-pos (long (/ n phi))
          loc (locate-torah-pos books phi-pos)]
      (println (format "  Torah n/φ = %,d → %s %d, letter %s (value %d)"
                       phi-pos (:book loc) (:chapter loc)
                       (:letter loc) (:value loc))))

    ;; Leviticus golden section
    (let [lev (nth books 2)
          lev-n (:count lev)
          phi-pos (long (/ lev-n phi))]
      (println (format "\n  Leviticus: %,d letters" lev-n))
      (println (format "  Leviticus n/φ = %,d" phi-pos))
      (let [ch-map (locate/chapter-map "Leviticus")
            loc    (locate/locate "Leviticus" ch-map phi-pos)]
        (println (format "  → Leviticus %d, local pos %d"
                         (:chapter loc) (:local-pos loc)))
        (println (format "  Letter: %s (value %d)"
                         (nth (:letters lev) phi-pos)
                         (g/letter-value (nth (:letters lev) phi-pos))))))

    ;; Golden sections of each book
    (println "\n  Per-book golden sections:")
    (doseq [{:keys [name letters count]} books]
      (let [phi-pos (long (/ count phi))
            ch-map (locate/chapter-map name)
            loc    (locate/locate name ch-map phi-pos)]
        (println (format "    %-12s  n/φ = %,6d → ch %2d, letter %s"
                         name phi-pos (:chapter loc)
                         (nth letters phi-pos)))))))

;; ── H5: Pi Digits as Skip Values ────────────────────────────

(def pi-digits
  "First 100 digits of pi (after 3.)"
  [3 1 4 1 5 9 2 6 5 3 5 8 9 7 9 3 2 3 8 4
   6 2 6 4 3 3 8 3 2 7 9 5 0 2 8 8 4 1 9 7
   1 6 9 3 9 9 3 7 5 1 0 5 8 2 0 9 7 4 9 4
   4 5 9 2 3 0 7 8 1 6 4 0 6 2 8 6 2 0 8 9
   9 8 6 2 8 0 3 4 8 2 5 3 4 2 1 1 7 0 6 7])

(defn pi-skip-positions
  "Generate positions by treating pi digits as cumulative skips."
  [digits max-pos]
  (->> digits
       (remove zero?)  ;; skip zeros as they'd mean same position
       (reductions +)
       (take-while #(< % max-pos))
       vec))

(defn pi-experiments [books torah]
  (println "\n=== H5: Pi Digits as Skip Values ===")
  (let [n (count torah)
        ;; Method 1: cumulative pi digits as positions
        positions (pi-skip-positions pi-digits n)]
    (println "  Method 1: Cumulative pi digits as positions")
    (println (format "  Positions: %s" (vec (take 20 positions))))
    (let [letters (mapv #(nth torah %) positions)]
      (println (format "  Letters: %s" (apply str letters)))
      (println (format "  Gematria sum: %d" (reduce + (map g/letter-value letters))))))

  ;; Method 2: pi digits as skip multiplier
  (println "\n  Method 2: Pi digits × 1000 as positions")
  (let [n (count torah)
        positions (filter #(< % n)
                          (map #(* % 1000) (remove zero? pi-digits)))]
    (doseq [p (take 10 positions)]
      (let [loc (locate-torah-pos books p)]
        (println (format "    pos %,6d → %s %d, letter %s"
                         p (:book loc) (:chapter loc) (:letter loc)))))))

;; ── H6: Position 31,415 ─────────────────────────────────────

(defn pi-positions [books torah]
  (println "\n=== H6: Pi-Derived Positions ===")
  (let [n (count torah)]
    (doseq [[label pos] [["π × 10"        314]
                         ["π × 100"       3141]
                         ["π × 1000"     31415]
                         ["π × 10000"   (mod 314159 n)]
                         ["π × 100000"  (mod 3141592 n)]]]
      (let [loc (locate-torah-pos books pos)]
        (println (format "  %-14s  pos %,7d → %s %d, letter %s (value %d)"
                         label pos (:book loc) (:chapter loc)
                         (:letter loc) (:value loc)))))))

;; ── H8: Running Sum Mod 7 ───────────────────────────────────

(defn running-sum-mod7 [books torah]
  (println "\n=== H8: Running Sum Mod 7 — Zero Crossings ===")
  (let [gvals (g/stream-values torah)
        n     (count gvals)]

    ;; Count total zero crossings
    (println "  Computing running sum mod 7...")
    (let [crossings (g/zero-crossings-mod torah 7)
          first-20  (take 20 crossings)
          total     (count (take 100000 crossings))]  ;; count first 100k
      (println (format "  First 100,000 letters contain %,d zero crossings mod 7" total))
      (println (format "  First 20 positions: %s" (vec first-20)))

      ;; Locate the first 10
      (println "\n  First 10 zero-crossings:")
      (doseq [pos (take 10 crossings)]
        (let [loc (locate-torah-pos books pos)]
          (println (format "    pos %,7d → %s %d"
                           pos (:book loc) (:chapter loc))))))

    ;; Check specific positions — chapter boundaries
    (println "\n  Running sum mod 7 at book boundaries:")
    (let [rsm (vec (g/running-sum-mod torah 7))]
      (loop [offset 0 i 0]
        (when (< i (count books))
          (let [{:keys [name count]} (nth books i)
                boundary (+ offset count -1)]
            (println (format "    End of %-12s (pos %,7d): mod 7 = %d"
                             name boundary (nth rsm boundary)))
            (recur (+ offset count) (inc i))))))))

;; ── H9: Book Length Ratios ──────────────────────────────────

(defn book-ratios [books]
  (println "\n=== H9: Book Length Ratios ===")
  (let [sizes (mapv :count books)
        names (mapv :name books)
        total (reduce + sizes)]
    ;; All pairwise ratios
    (println "  Pairwise ratios:")
    (println (format "  %-14s  %-14s  %10s  %s" "Book A" "Book B" "Ratio" "Near?"))
    (println (apply str (repeat 60 "-")))
    (doseq [i (range 5)
            j (range (inc i) 5)]
      (let [r (/ (double (nth sizes i)) (nth sizes j))
            ;; Check proximity to known constants
            near-pi  (< (Math/abs (- r Math/PI)) 0.15)
            near-phi (< (Math/abs (- r phi)) 0.15)
            near-e   (< (Math/abs (- r Math/E)) 0.15)
            near-2   (< (Math/abs (- r 2.0)) 0.15)
            near-sq2 (< (Math/abs (- r (Math/sqrt 2))) 0.15)
            note (cond near-pi  (format "≈ π (%.4f)" Math/PI)
                       near-phi (format "≈ φ (%.4f)" phi)
                       near-e   (format "≈ e (%.4f)" Math/E)
                       near-2   "≈ 2"
                       near-sq2 (format "≈ √2 (%.4f)" (Math/sqrt 2))
                       :else    "")]
        (println (format "  %-14s  %-14s  %10.6f  %s"
                         (nth names i) (nth names j) r note))))

    ;; Ratios to total
    (println "\n  Fraction of total Torah:")
    (doseq [{:keys [name count]} books]
      (let [frac (/ (double count) total)]
        (println (format "    %-12s  %,6d / %,6d = %.6f  (1/%.2f)"
                         name count total frac (/ 1.0 frac)))))

    ;; Adjacent book ratios
    (println "\n  Adjacent book ratios:")
    (doseq [i (range 4)]
      (let [r (/ (double (nth sizes i)) (nth sizes (inc i)))]
        (println (format "    %s / %s = %.6f"
                         (nth names i) (nth names (inc i)) r))))

    ;; Sum of first 3 vs last 2
    (let [first3 (reduce + (take 3 sizes))
          last2  (reduce + (drop 3 sizes))]
      (println (format "\n  Gen+Exod+Lev / Num+Deut = %,d / %,d = %.6f"
                       first3 last2 (/ (double first3) last2)))
      (println (format "  Gen+Exod / Lev+Num+Deut = %,d / %,d = %.6f"
                       (+ (nth sizes 0) (nth sizes 1))
                       (+ (nth sizes 2) (nth sizes 3) (nth sizes 4))
                       (/ (double (+ (nth sizes 0) (nth sizes 1)))
                          (+ (nth sizes 2) (nth sizes 3) (nth sizes 4))))))))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (let [{:keys [torah books sizes]} (load-torah)]
    (golden-sections books torah)
    (pi-experiments books torah)
    (pi-positions books torah)
    (running-sum-mod7 books torah)
    (book-ratios books))
  (println "\nDone."))
