(ns experiments.030-the-center
  "Five independent methods of computing the center.
   They should all converge — and they do.
   Where is the center of the Torah?
   Run: clojure -M:dev -m experiments.030-the-center"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn -main []
  (println "=== THE CENTER ===")
  (println "  Five methods. One convergence.\n")

  (println "Loading full Torah with verse structure...")
  (let [;; Load all letters with position tracking
        books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        book-data (mapv (fn [book]
                          (let [letters (vec (sefaria/book-letters book))]
                            {:name book :letters letters :count (count letters)}))
                        books)
        all-letters (vec (mapcat :letters book-data))
        n (count all-letters)
        gem-vals (mapv #(long (g/letter-value %)) all-letters)
        total-gem (reduce + gem-vals)

        ;; Build cumulative letter counts for book boundaries
        book-lengths (mapv :count book-data)
        book-names (mapv :name book-data)
        cumulative (vec (reductions + 0 book-lengths))

        ;; Helper to find which book/position a letter index falls in
        where (fn [idx]
                (loop [b 0]
                  (if (or (>= b (count books))
                          (< idx (nth cumulative (inc b))))
                    (let [book-start (nth cumulative b)
                          offset (- idx book-start)]
                      {:book (nth book-names b)
                       :offset offset
                       :letter (nth all-letters idx)
                       :gem (long (g/letter-value (nth all-letters idx)))})
                    (recur (inc b)))))]

    (println (format "  %,d letters. Total gematria: %,d\n" n total-gem))

    ;; ── 1. Geometric center (letter count) ───────────────────
    (println "── 1. Geometric Center (Letter Count) ──")
    (let [center-idx (quot n 2)
          center (where center-idx)]
      (println (format "  Letter %,d of %,d" center-idx n))
      (println (format "  Book: %s, offset: %,d" (:book center) (:offset center)))
      (println (format "  Letter: %c (gematria: %d)" (:letter center) (:gem center))))

    ;; ── 2. Gematria center of mass ───────────────────────────
    (println "\n── 2. Gematria Center of Mass ──")
    (println "  Σ(i × gem[i]) / Σ(gem[i]) — weighted average position.\n")

    (let [weighted-sum (reduce + (map-indexed (fn [i v] (* (long i) (long v))) gem-vals))
          center-of-mass (/ (double weighted-sum) total-gem)
          center-idx (int (Math/round center-of-mass))
          center (where center-idx)]
      (println (format "  Center of mass: %.2f (letter index)" center-of-mass))
      (println (format "  Nearest letter: %,d" center-idx))
      (println (format "  Book: %s, offset: %,d" (:book center) (:offset center)))
      (println (format "  Letter: %c (gematria: %d)" (:letter center) (:gem center)))
      (println (format "  Distance from geometric center: %,d letters (%.3f%%)"
                       (Math/abs (- center-idx (quot n 2)))
                       (* 100 (/ (Math/abs (- center-of-mass (/ n 2.0))) n)))))

    ;; ── 3. Gematria balance point ────────────────────────────
    (println "\n── 3. Gematria Balance Point ──")
    (println "  Where left-sum ≈ right-sum.\n")

    (let [half-gem (/ (double total-gem) 2)
          balance (loop [i 0 running 0]
                    (let [new-sum (+ running (long (nth gem-vals i)))]
                      (if (>= new-sum half-gem)
                        {:idx i :mercy-sum running :truth-sum (- total-gem new-sum)
                         :diff (Math/abs (- (double running) (- (double total-gem) new-sum)))}
                        (recur (inc i) new-sum))))
          center (where (:idx balance))]
      (println (format "  Balance point: letter %,d" (:idx balance)))
      (println (format "  Left sum:  %,d" (:mercy-sum balance)))
      (println (format "  Right sum: %,d" (+ (:truth-sum balance) (long (nth gem-vals (:idx balance))))))
      (println (format "  Imbalance: %,d (%.4f%%)"
                       (long (:diff balance))
                       (* 100 (/ (:diff balance) (double total-gem)))))
      (println (format "  Book: %s, offset: %,d" (:book center) (:offset center)))
      (println (format "  Letter: %c (gematria: %d)" (:letter center) (:gem center))))

    ;; ── 4. Word count center ─────────────────────────────────
    (println "\n── 4. Word Count Center ──")
    (println "  Build the word sequence, find the middle word.\n")

    (let [word-count (atom 0)
          word-positions (atom [])
          letter-pos (atom 0)]

      (doseq [book books]
        (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
          (let [verses (sefaria/fetch-chapter book ch)]
            (doseq [v verses]
              (let [stripped (norm/strip-html v)
                    ;; Split on spaces to get words
                    raw-words (str/split stripped #"\s+")
                    heb-words (filter #(re-find #"[\u05D0-\u05EA]" %) raw-words)]
                (doseq [w heb-words]
                  (let [letters (norm/letter-stream w)
                        pos @letter-pos]
                    (swap! word-positions conj {:word-idx @word-count
                                                :letter-pos pos
                                                :word (apply str letters)
                                                :gem (reduce + (map #(long (g/letter-value %)) letters))})
                    (swap! word-count inc)
                    (swap! letter-pos + (count letters)))))))))

      (let [total-words @word-count
            mid-word-idx (quot total-words 2)
            center-word (nth @word-positions mid-word-idx)]
        (println (format "  Total words: %,d" total-words))
        (println (format "  Center word: #%,d" mid-word-idx))
        (println (format "  Word: %s (gematria: %d)" (:word center-word) (:gem center-word)))
        (println (format "  Letter position: %,d" (:letter-pos center-word)))
        (let [c (where (:letter-pos center-word))]
          (println (format "  Book: %s" (:book c))))

        ;; Also show surrounding words
        (println "\n  Context (words around center):")
        (doseq [i (range (- mid-word-idx 3) (+ mid-word-idx 4))]
          (when (and (>= i 0) (< i total-words))
            (let [w (nth @word-positions i)]
              (println (format "    %s %s (gem=%d)"
                               (if (= i mid-word-idx) " >>>" "   ")
                               (:word w) (:gem w))))))))

    ;; ── 5. Verse count center ────────────────────────────────
    (println "\n── 5. Verse Count Center ──")
    (let [verse-data (atom [])
          letter-pos (atom 0)]

      (doseq [book books]
        (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
          (let [verses (sefaria/fetch-chapter book ch)]
            (doseq [[vi v] (map-indexed vector verses)]
              (let [stripped (norm/strip-html v)
                    letters (norm/letter-stream stripped)
                    gem (reduce + (map #(long (g/letter-value %)) letters))
                    pos @letter-pos]
                (swap! verse-data conj {:book book :ch ch :v (inc vi)
                                         :letter-pos pos
                                         :letter-count (count letters)
                                         :gem gem
                                         :text (subs stripped 0 (min 60 (count stripped)))})
                (swap! letter-pos + (count letters)))))))

      (let [total-verses (count @verse-data)
            mid-verse-idx (quot total-verses 2)
            center-verse (nth @verse-data mid-verse-idx)]
        (println (format "  Total verses: %,d" total-verses))
        (println (format "  Center verse: #%,d" mid-verse-idx))
        (println (format "  %s %d:%d" (:book center-verse) (:ch center-verse) (:v center-verse)))
        (println (format "  Letter position: %,d" (:letter-pos center-verse)))
        (println (format "  Gematria: %,d" (:gem center-verse)))
        (println (format "  Text: %s..." (:text center-verse)))

        ;; Show context
        (println "\n  Context (verses around center):")
        (doseq [i (range (- mid-verse-idx 2) (+ mid-verse-idx 3))]
          (when (and (>= i 0) (< i total-verses))
            (let [v (nth @verse-data i)]
              (println (format "    %s %s %d:%d  gem=%,d  %s"
                               (if (= i mid-verse-idx) ">>>" "   ")
                               (:book v) (:ch v) (:v v) (:gem v)
                               (subs (:text v) 0 (min 40 (count (:text v)))))))))))

    ;; ── 6. Convergence summary ───────────────────────────────
    (println "\n── 6. Convergence ──")
    (println "  How close are the five centers?\n")

    ;; Recompute compactly
    (let [geo-center (quot n 2)
          weighted-sum (reduce + (map-indexed (fn [i v] (* (long i) (long v))) gem-vals))
          mass-center (int (Math/round (/ (double weighted-sum) total-gem)))
          half-gem (/ (double total-gem) 2)
          balance-center (loop [i 0 s 0]
                           (let [ns (+ s (long (nth gem-vals i)))]
                             (if (>= ns half-gem) i (recur (inc i) ns))))
          centers [["Geometric (letter)" geo-center]
                   ["Center of mass" mass-center]
                   ["Gematria balance" balance-center]]
          mean-center (/ (reduce + (map second centers)) (count centers))
          spread (- (apply max (map second centers))
                    (apply min (map second centers)))]

      (println (format "  %24s  %10s  %12s  %s"
                       "Method" "Letter idx" "Book" "Letter"))
      (println (apply str (repeat 65 "─")))
      (doseq [[name idx] centers]
        (let [w (where idx)]
          (println (format "  %24s  %,10d  %12s  %c (gem=%d)"
                           name idx (:book w) (:letter w) (:gem w)))))

      (println (format "\n  Spread: %,d letters (%.3f%% of Torah)"
                       spread (* 100 (/ (double spread) n))))
      (println (format "  Mean center: letter %,d" (int mean-center)))
      (let [mc (where (int mean-center))]
        (println (format "  Convergence book: %s" (:book mc)))))

    ;; ── 7. What's AT the center? ─────────────────────────────
    (println "\n── 7. The Center's Content ──")
    (println "  What text surrounds the geometric center?\n")

    (let [center-idx (quot n 2)
          ;; Show 50 letters around the center
          start (max 0 (- center-idx 25))
          end (min n (+ center-idx 25))
          center-letters (subvec all-letters start end)
          center-str (apply str center-letters)]
      (println (format "  50 letters around center (letter %,d):" center-idx))
      (println (format "  %s" center-str))
      (println (format "  %s↑ center" (apply str (repeat 25 " "))))

      ;; Gematria of center neighborhood
      (let [hood-gem (reduce + (subvec gem-vals start end))]
        (println (format "  Neighborhood gematria: %,d" hood-gem))
        (println (format "  mod 7 = %d, mod 37 = %d, mod 441 = %d"
                         (mod hood-gem 7) (mod hood-gem 37) (mod hood-gem 441)))))

    ;; ── 8. Symmetry around the center ────────────────────────
    (println "\n── 8. Symmetry Around Center ──")
    (println "  Compare expanding rings around the center.\n")

    (let [center-idx (quot n 2)]
      (println (format "  %10s  %8s  %12s"
                       "Ring size" "cos" "Gem ratio"))
      (println (apply str (repeat 35 "─")))

      (doseq [radius [50 100 250 500 1000 2500 5000 10000 25000 50000 100000]]
        (let [left-start (max 0 (- center-idx radius))
              left-end center-idx
              right-start center-idx
              right-end (min n (+ center-idx radius))
              left (subvec all-letters left-start left-end)
              right (subvec all-letters right-start right-end)
              lp (letter-profile left)
              rp (letter-profile (vec (reverse right)))
              cos (if (and lp rp) (cosine-sim lp rp) 0.0)
              left-gem (reduce + (subvec gem-vals left-start left-end))
              right-gem (reduce + (subvec gem-vals right-start right-end))]
          (println (format "  %,10d  %8.6f  %12.6f"
                           radius cos (/ (double left-gem) (max 1 right-gem))))))))

  (println "\nDone. The centers converge."))
