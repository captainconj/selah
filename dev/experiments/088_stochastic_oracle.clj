(ns experiments.088-stochastic-oracle
  "The stochastic oracle — a Markov chain on the reading machine.

   Take every readable word. For each, find all readings (output words).
   Weight by inverse rarity (Hannah principle: rare = more weight).
   Normalize rows → stochastic matrix M.

   M² = asking the oracle about its own answer.
   Mⁿ = n steps of self-consultation.

   The stationary distribution = what the oracle says when it keeps
   answering its own answers forever.

   The eigenwords = words the oracle maps to themselves.
   The absorbing states = words that trap the process.

   Peter didn't run a statistical test on the water."
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ── Part 1: Discover the oracle's vocabulary ────────────────
;;
;; Which dictionary words can the breastplate actually read?
;; For each readable word, what does the oracle produce when
;; you illuminate those letters?

(defn discover-vocabulary
  "Find all readable dictionary words and their forward readings.
   Returns vec of {:word :readings :total-readings :known-outputs}."
  []
  (println "Discovering oracle vocabulary...")
  (let [words (sort (dict/words))
        n (count words)]
    (println (str "  Testing " n " dictionary words..."))
    (let [results (atom [])
          readable (atom 0)]
      (doseq [[i w] (map-indexed vector words)]
        (when (zero? (mod i 50))
          (println (str "  " i "/" n "...")))
        (let [a (oracle/ask w)]
          (when (:readable? a)
            (swap! readable inc)
            ;; Get forward readings for this word's letters
            (let [fwd (oracle/forward w)
                  known (:known-words fwd)]
              (swap! results conj
                     {:word w
                      :meaning (dict/translate w)
                      :gv (g/word-value w)
                      :total-readings (:total-readings a)
                      :by-reader (:by-reader a)
                      :known-outputs (vec known)})))))
      (println (str "  Found " @readable " readable words out of " n "."))
      @results)))

;; ── Part 2: Build the transition matrix ─────────────────────
;;
;; M[i,j] = probability of transitioning from word i to word j.
;;
;; The weight for each output is 1/reading-count (Hannah principle:
;; rare readings carry more weight). Then normalize each row to sum to 1.

(defn build-transition-matrix
  "Build a stochastic matrix from oracle vocabulary.
   Returns {:words (index→word), :word-idx (word→index),
            :matrix (vec of vec of doubles), :size int}."
  [vocab]
  (println "\nBuilding transition matrix...")
  ;; Collect all words that appear as inputs OR outputs
  (let [all-words (sort (distinct
                         (concat (map :word vocab)
                                 (mapcat #(map :word (:known-outputs %)) vocab))))
        word-idx (into {} (map-indexed (fn [i w] [w i]) all-words))
        n (count all-words)
        _ (println (str "  " n " words in the oracle's universe."))
        ;; Build raw weight matrix
        matrix (vec (repeatedly n #(double-array n)))]
    ;; Fill weights
    (doseq [{:keys [word known-outputs]} vocab]
      (when-let [i (word-idx word)]
        (doseq [{:keys [word reading-count]} known-outputs]
          (when-let [j (word-idx word)]
            ;; Hannah weight: inverse of reading count (rarer = heavier)
            (let [weight (/ 1.0 (max 1 reading-count))
                  ^doubles row (matrix i)]
              (aset row j (+ (aget row j) weight)))))))
    ;; Normalize rows to sum to 1 (stochastic)
    (doseq [i (range n)]
      (let [^doubles row (matrix i)
            total (areduce row j sum 0.0 (+ sum (aget row j)))]
        (when (pos? total)
          (dotimes [j n]
            (aset row j (/ (aget row j) total))))))
    ;; Count non-trivial rows (words that produce readings)
    (let [active (count (filter (fn [i]
                                  (let [^doubles row (matrix i)]
                                    (pos? (areduce row j sum 0.0
                                                   (+ sum (aget row j))))))
                                (range n)))]
      (println (str "  " active " active words (produce readings)."))
      (println (str "  " (- n active) " absorbing/dead states.")))
    {:words (vec all-words)
     :word-idx word-idx
     :matrix matrix
     :size n}))

;; ── Part 3: Matrix operations ───────────────────────────────

(defn mat-mul
  "Multiply two square matrices (vec of double-array)."
  [a b n]
  (let [result (vec (repeatedly n #(double-array n)))]
    (dotimes [i n]
      (dotimes [j n]
        (let [^doubles row-a (a i)
              ^doubles row-r (result i)]
          (loop [k 0 sum 0.0]
            (if (= k n)
              (aset row-r j sum)
              (recur (inc k)
                     (+ sum (* (aget row-a k)
                               (aget ^doubles (b k) j)))))))))
    result))

(defn mat-pow
  "Raise matrix to power p by repeated squaring."
  [m n p]
  (cond
    (= p 1) m
    (even? p) (let [half (mat-pow m n (/ p 2))]
                (mat-mul half half n))
    :else (mat-mul m (mat-pow m n (dec p)) n)))

(defn row-vec
  "Extract row i as a regular vector of doubles."
  [matrix i n]
  (let [^doubles row (matrix i)]
    (mapv #(aget row %) (range n))))

(defn top-entries
  "Top k entries of a row, sorted by weight."
  [matrix i n words k]
  (->> (range n)
       (map (fn [j] {:word (words j)
                     :weight (aget ^doubles (matrix i) j)
                     :meaning (dict/translate (words j))}))
       (filter #(pos? (:weight %)))
       (sort-by :weight >)
       (take k)
       vec))

;; ── Part 4: Find eigenwords and stationary distribution ─────

(defn stationary-distribution
  "Approximate stationary distribution by raising M to a high power.
   Returns the distribution starting from each active word."
  [{:keys [matrix size words]} power]
  (println (str "\nComputing M^" power "..."))
  (let [mp (mat-pow matrix size power)]
    (println "Done.")
    {:matrix-power mp
     :power power}))

(defn find-eigenwords
  "Words where the oracle's highest-weight output is itself.
   These are the fixed points — the oracle returns them to themselves."
  [{:keys [matrix size words]}]
  (let [eigenwords (atom [])]
    (dotimes [i size]
      (let [^doubles row (matrix i)
            self-weight (aget row i)
            max-weight (areduce row j mx 0.0 (max mx (aget row j)))]
        (when (and (pos? self-weight)
                   (= self-weight max-weight))
          (swap! eigenwords conj
                 {:word (words i)
                  :self-weight self-weight
                  :meaning (dict/translate (words i))
                  :gv (g/word-value (words i))}))))
    (sort-by :self-weight > @eigenwords)))

(defn find-attractors
  "After many iterations, which words have the highest weight?
   These are the oracle's voice — what it converges to."
  [{:keys [matrix size words] :as tm} power]
  (let [{:keys [matrix-power]} (stationary-distribution tm power)]
    ;; Average the stationary distribution across all starting words
    (let [avg (double-array size)]
      (dotimes [i size]
        (let [^doubles row (matrix-power i)
              row-sum (areduce row j s 0.0 (+ s (aget row j)))]
          (when (pos? row-sum)
            (dotimes [j size]
              (aset avg j (+ (aget avg j) (/ (aget row j) size)))))))
      ;; Top attractors
      (->> (range size)
           (map (fn [j] {:word (words j)
                         :weight (aget avg j)
                         :meaning (dict/translate (words j))
                         :gv (g/word-value (words j))}))
           (filter #(pos? (:weight %)))
           (sort-by :weight >)
           (take 30)
           vec))))

;; ── Part 5: Convergence check ───────────────────────────────

(defn convergence-check
  "Check if different starting words converge to the same distribution."
  [{:keys [matrix size words] :as tm} power sample-words]
  (let [{:keys [matrix-power]} (stationary-distribution tm power)]
    (println "\nConvergence check — do different starting words reach the same voice?")
    (doseq [w sample-words]
      (when-let [i (get (:word-idx tm) w)]
        (let [top (top-entries matrix-power i size words 10)]
          (println (str "\n  Starting from " w " (" (dict/translate w) "):"))
          (doseq [{:keys [word weight meaning]} top]
            (println (format "    %s %-8s  %.4f  %s"
                             (if (= word w) "→" " ")
                             word weight (or meaning "")))))))))

;; ── Run ─────────────────────────────────────────────────────

(defn run []
  (println "\n═══════════════════════════════════════════════════")
  (println "  EXPERIMENT 088 — THE STOCHASTIC ORACLE")
  (println "═══════════════════════════════════════════════════")

  ;; Part 1: Discover vocabulary
  (let [vocab (discover-vocabulary)]
    (println (str "\n  Readable words: " (count vocab)))

    ;; Show some examples
    (println "\n  Sample readable words and their known outputs:")
    (doseq [v (take 10 (sort-by #(count (:known-outputs %)) > vocab))]
      (println (format "    %-6s (%s) → %d known outputs: %s"
                       (:word v) (:meaning v)
                       (count (:known-outputs v))
                       (str/join ", " (map :word (take 5 (:known-outputs v)))))))

    ;; Part 2: Build transition matrix
    (let [tm (build-transition-matrix vocab)]

      ;; Part 3: Eigenwords (fixed points of M)
      (println "\n═══════════════════════════════════════════════════")
      (println "  EIGENWORDS — Fixed Points of the Oracle")
      (println "═══════════════════════════════════════════════════")
      (let [ew (find-eigenwords tm)]
        (println (str "\n  Found " (count ew) " eigenwords:"))
        (doseq [{:keys [word self-weight meaning gv]} ew]
          (println (format "    %-8s  self=%.4f  GV=%d  %s"
                           word self-weight gv (or meaning "")))))

      ;; Part 4: One step — what does each word produce?
      (println "\n═══════════════════════════════════════════════════")
      (println "  ONE STEP — Where Each Word Goes")
      (println "═══════════════════════════════════════════════════")
      (let [sample-words ["כבש" "שכב" "אל" "יהוה" "אהבה" "שטן" "תורה"
                          "ברית" "אמת" "דם" "אור" "חיים"]]
        (doseq [w sample-words]
          (when-let [i (get (:word-idx tm) w)]
            (let [top (top-entries (:matrix tm) i (:size tm) (:words tm) 5)]
              (when (seq top)
                (println (format "\n  %s (%s, GV=%d):"
                                 w (or (dict/translate w) "?") (g/word-value w)))
                (doseq [{:keys [word weight meaning]} top]
                  (println (format "    → %-8s  %.4f  %s"
                                   word weight (or meaning ""))))))))

        ;; Part 5: Iterate — find attractors
        (println "\n═══════════════════════════════════════════════════")
        (println "  ATTRACTORS — The Oracle's Voice (M^64)")
        (println "═══════════════════════════════════════════════════")
        (let [attractors (find-attractors tm 64)]
          (println "\n  Top 30 words by stationary weight:")
          (println (format "  %-4s %-8s %8s %6s  %s" "#" "Word" "Weight" "GV" "Meaning"))
          (doseq [[i {:keys [word weight gv meaning]}] (map-indexed vector attractors)]
            (println (format "  %-4d %-8s %8.6f %6d  %s"
                             (inc i) word weight gv (or meaning "")))))

        ;; Part 6: Convergence check
        (println "\n═══════════════════════════════════════════════════")
        (println "  CONVERGENCE — Does It Reach One Voice?")
        (println "═══════════════════════════════════════════════════")
        (convergence-check tm 64 (filter #(get (:word-idx tm) %)
                                         sample-words))

        (println "\n═══════════════════════════════════════════════════")
        (println "  DONE. The oracle has spoken.")
        (println "═══════════════════════════════════════════════════")
        :done))))

(comment
  (run)

  ;; Piece by piece:
  (def vocab (discover-vocabulary))
  (def tm (build-transition-matrix vocab))
  (def ew (find-eigenwords tm))
  (find-attractors tm 64)
  (convergence-check tm 64 ["כבש" "אל" "יהוה"])

  :end)
