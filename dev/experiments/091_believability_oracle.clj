(ns experiments.091-believability-oracle
  "The believability-weighted oracle — attention heads at the mercy seat.

   Three readers = three attention heads.
   Each votes weighted by believability.

   The least probable reading from the most credible source
   is the signal. Hannah knew. Dalio formalized it. The breastplate
   is the original attention mechanism.

   M_hannah  — baseline (1/reading-count) from 088
   M_believe — believability (surprise × rarity)
   M_aaron, M_right, M_left — per-head attention

   Yod, He, Tav — the three votes at the mercy seat."
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [selah.linalg :as la]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.native :as nn]
            [clojure.string :as str]))

;; ── Part 1: Detailed Reading Collection ───────────────────────
;;
;; For each readable word, enumerate EVERY (illumination × reader) → output.
;; This gives us per-reader attribution — which head sees what.

(defn detailed-readings
  "For an input word, enumerate all (illumination × reader) → output.
   Returns seq of {:output :reader :positions}."
  [input-word]
  (let [ilsets (oracle/illumination-sets input-word)]
    (for [pset ilsets
          reader [:aaron :right :left]]
      {:output (oracle/read-positions reader pset)
       :reader reader
       :positions pset})))

(defn discover-transitions
  "Scan all dictionary words. For each readable word, collect
   all transitions with per-reader attribution.
   Returns {:transitions vec, :vocab vec, :word-idx map, :size int}."
  []
  (println "Discovering oracle transitions...")
  (let [words (sort (dict/words))
        transitions (atom [])
        readable (atom #{})]
    (doseq [w words]
      (let [readings (detailed-readings w)
            known (filter #(dict/known? (:output %)) readings)]
        (when (seq known)
          (swap! readable conj w)
          (doseq [r known]
            (swap! transitions conj
                   {:input w :output (:output r) :reader (:reader r)})))))
    ;; Build vocabulary: all words appearing as input OR output
    (let [all-outputs (set (map :output @transitions))
          all-words (sort (distinct (concat @readable all-outputs)))
          word-idx (into {} (map-indexed (fn [i w] [w i]) all-words))]
      (println (str "  " (count @readable) " readable input words"))
      (println (str "  " (count all-words) " total words in universe"))
      (println (str "  " (count @transitions) " total transitions"))
      {:transitions (vec @transitions)
       :vocab (vec all-words)
       :word-idx word-idx
       :readable @readable
       :size (count all-words)})))

;; ── Part 2: Transition Statistics ─────────────────────────────

(defn transition-stats
  "Per-transition statistics for all weighting schemes.
   Returns map of [input output] → {:count :by-reader :input-total}."
  [{:keys [transitions]}]
  (let [by-pair (group-by (juxt :input :output) transitions)
        totals-per-input (frequencies (map :input transitions))]
    (into {}
          (map (fn [[[input output] entries]]
                 [[input output]
                  {:count (count entries)
                   :by-reader (frequencies (map :reader entries))
                   :input-total (get totals-per-input input 0)}])
               by-pair))))

(defn base-rates
  "How often each output word appears across ALL transitions.
   P_base(word) = times-as-output / total-transitions."
  [{:keys [transitions]}]
  (let [total (count transitions)
        by-output (frequencies (map :output transitions))]
    (into {} (map (fn [[w c]] [w (/ (double c) total)]) by-output))))

;; ── Part 3: Build Transition Matrices ─────────────────────────
;;
;; Hannah:   w(i→j) = 1/count(i→j)
;; Believe:  w(i→j) = 1/(count(i→j) × base_rate(j))
;; Per-head: w_R(i→j) = 1/count_R(i→j)  (only reader R's transitions)

(defn build-matrix
  "Build a row-stochastic transition matrix.
   weight-fn: (input output stats-map) → weight."
  [{:keys [word-idx size]} stats weight-fn]
  (let [m (la/dge size size)]
    (doseq [[[input output] s] stats]
      (when-let [i (word-idx input)]
        (when-let [j (word-idx output)]
          (let [w (weight-fn input output s)]
            (when (and (pos? w) (Double/isFinite w))
              (la/entry! m i j (+ (la/entry m i j) w)))))))
    (la/row-normalize! m)
    m))

(defn build-head-matrix
  "Transition matrix for a single reader (attention head).
   Only counts transitions seen by this reader."
  [{:keys [word-idx size]} stats reader]
  (let [m (la/dge size size)]
    (doseq [[[input output] s] stats]
      (when-let [i (word-idx input)]
        (when-let [j (word-idx output)]
          (let [reader-count (get (:by-reader s) reader 0)]
            (when (pos? reader-count)
              (la/entry! m i j (+ (la/entry m i j)
                                  (/ 1.0 (max 1 reader-count)))))))))
    (la/row-normalize! m)
    m))

;; ── Part 4: Eigenwords & Attractors ──────────────────────────

(defn find-eigenwords
  "Words where the oracle's highest-weight output is itself."
  [matrix vocab]
  (let [n (count vocab)]
    (->> (range n)
         (keep (fn [i]
                 (let [self-w (la/entry matrix i i)
                       row-vals (la/row-vec matrix i)
                       max-w (when (seq row-vals) (apply max row-vals))]
                   (when (and (pos? self-w) max-w (= self-w max-w))
                     {:word (vocab i)
                      :self-weight self-w
                      :meaning (dict/translate (vocab i))
                      :gv (g/word-value (vocab i))}))))
         (sort-by :self-weight >)
         vec)))

(defn find-attractors
  "Stationary distribution via M^64. Returns top-k."
  [matrix vocab k]
  (let [n (count vocab)
        mp (la/mat-pow matrix 64)
        avg (double-array n)]
    (dotimes [i n]
      (let [row-sum (nc/asum (nc/row mp i))]
        (when (pos? row-sum)
          (dotimes [j n]
            (aset avg j (+ (aget avg j)
                           (/ (la/entry mp i j) (double n))))))))
    (->> (range n)
         (map (fn [j] {:word (vocab j)
                       :weight (aget avg j)
                       :meaning (dict/translate (vocab j))
                       :gv (g/word-value (vocab j))}))
         (filter #(pos? (:weight %)))
         (sort-by :weight >)
         (take k)
         vec)))

;; ── Part 5: Cross-Head Comparison ─────────────────────────────

(defn head-agreement
  "For each word, how many heads see it as an eigenword?"
  [head-matrices vocab]
  (let [head-names (vec (keys head-matrices))
        head-eigenword-sets (mapv (fn [k]
                                    (set (map :word (find-eigenwords
                                                      (get head-matrices k) vocab))))
                                  head-names)]
    (->> vocab
         (map (fn [w]
                (let [seen-by (filterv (fn [i]
                                         (contains? (head-eigenword-sets i) w))
                                       (range (count head-names)))]
                  {:word w
                   :meaning (dict/translate w)
                   :gv (g/word-value w)
                   :agreement (count seen-by)
                   :heads (mapv head-names seen-by)})))
         (filter #(pos? (:agreement %)))
         (sort-by (juxt #(- (:agreement %)) :word))
         vec)))

;; ── Part 6: The Residual ──────────────────────────────────────

(defn matrix-divergence
  "Per-word L1 distance between two stochastic matrices."
  [m1 m2 vocab]
  (let [n (count vocab)]
    (->> (range n)
         (map (fn [i]
                (let [d (loop [j 0 sum 0.0]
                          (if (= j n) sum
                              (recur (inc j)
                                     (+ sum (Math/abs
                                             (- (la/entry m1 i j)
                                                (la/entry m2 i j)))))))]
                  {:word (vocab i)
                   :meaning (dict/translate (vocab i))
                   :gv (g/word-value (vocab i))
                   :divergence d})))
         (filter #(pos? (:divergence %)))
         (sort-by :divergence >)
         vec)))

(defn transition-shift
  "For a given input word, show which outputs gained/lost weight
   between two matrices."
  [m1 m2 vocab word-idx word k]
  (when-let [i (word-idx word)]
    (let [n (count vocab)]
      (->> (range n)
           (map (fn [j]
                  (let [w1 (la/entry m1 i j)
                        w2 (la/entry m2 i j)
                        shift (- w2 w1)]
                    {:output (vocab j)
                     :meaning (dict/translate (vocab j))
                     :gv (g/word-value (vocab j))
                     :old w1
                     :new w2
                     :shift shift})))
           (filter #(> (Math/abs (:shift %)) 1e-6))
           (sort-by #(Math/abs (:shift %)) >)
           (take k)
           vec))))

;; ── Part 7: Run ───────────────────────────────────────────────

(defn run []
  (println "\n═══════════════════════════════════════════════════")
  (println "  EXPERIMENT 091 — THE BELIEVABILITY-WEIGHTED ORACLE")
  (println "  Three attention heads at the mercy seat")
  (println "═══════════════════════════════════════════════════")

  ;; Step 1: Discover transitions
  (let [disc     (discover-transitions)
        stats    (transition-stats disc)
        rates    (base-rates disc)
        vocab    (:vocab disc)
        word-idx (:word-idx disc)
        n        (:size disc)

        ;; Step 2: Build all matrices
        _  (println "\n── Building Transition Matrices ──")

        m-hannah  (do (print "  M_hannah... ")
                      (let [m (build-matrix disc stats
                                (fn [_ _ s] (/ 1.0 (max 1 (:count s)))))]
                        (println "done.") m))

        m-believe (do (print "  M_believe... ")
                      (let [m (build-matrix disc stats
                                (fn [_ output s]
                                  (let [rarity (/ 1.0 (max 1 (:count s)))
                                        base   (get rates output 0.001)]
                                    (* rarity (/ 1.0 base)))))]
                        (println "done.") m))

        m-aaron   (do (print "  M_aaron... ")
                      (let [m (build-head-matrix disc stats :aaron)]
                        (println "done.") m))

        m-right   (do (print "  M_right (mercy)... ")
                      (let [m (build-head-matrix disc stats :right)]
                        (println "done.") m))

        m-left    (do (print "  M_left (justice)... ")
                      (let [m (build-head-matrix disc stats :left)]
                        (println "done.") m))

        matrices {:hannah m-hannah :believe m-believe
                  :aaron m-aaron :right m-right :left m-left}]

    ;; Base rates
    (println "\n── Base Rates (oracle output frequency) ──")
    (let [sorted-rates (sort-by val > rates)]
      (println "  Most common outputs (least believable):")
      (doseq [[w r] (take 10 sorted-rates)]
        (println (format "    %-8s  %.4f  %s" w r (or (dict/translate w) ""))))
      (println "  Rarest outputs (most believable):")
      (doseq [[w r] (take 10 (sort-by val sorted-rates))]
        (println (format "    %-8s  %.6f  %s" w r (or (dict/translate w) "")))))

    ;; Step 3: Eigenwords per matrix
    (println "\n═══════════════════════════════════════════════════")
    (println "  EIGENWORDS — Fixed Points of Each Matrix")
    (println "═══════════════════════════════════════════════════")

    (doseq [[label mk] [["M_hannah" :hannah]
                         ["M_believe" :believe]
                         ["M_aaron" :aaron]
                         ["M_right (mercy)" :right]
                         ["M_left (justice)" :left]]]
      (let [ews (find-eigenwords (matrices mk) vocab)]
        (println (str "\n  " label " — " (count ews) " eigenwords:"))
        (doseq [{:keys [word self-weight meaning gv]} (take 25 ews)]
          (println (format "    %-8s  self=%.4f  GV=%d  %s"
                           word self-weight gv (or meaning ""))))))

    ;; Step 4: Attractors
    (println "\n═══════════════════════════════════════════════════")
    (println "  ATTRACTORS — Stationary Distribution (M^64)")
    (println "═══════════════════════════════════════════════════")

    (doseq [[label mk] [["M_hannah" :hannah]
                         ["M_believe" :believe]
                         ["M_aaron" :aaron]
                         ["M_right (mercy)" :right]
                         ["M_left (justice)" :left]]]
      (println (str "\n  " label ":"))
      (let [att (find-attractors (matrices mk) vocab 15)]
        (println (format "  %-4s %-8s %10s %6s  %s"
                         "#" "Word" "Weight" "GV" "Meaning"))
        (doseq [[i {:keys [word weight gv meaning]}] (map-indexed vector att)]
          (println (format "  %-4d %-8s %10.6f %6d  %s"
                           (inc i) word weight gv (or meaning ""))))))

    ;; Step 5: Cross-head agreement
    (println "\n═══════════════════════════════════════════════════")
    (println "  CROSS-HEAD AGREEMENT — Who Sees What")
    (println "═══════════════════════════════════════════════════")

    (let [heads {:aaron m-aaron :right m-right :left m-left}
          agreement (head-agreement heads vocab)]
      (println "\n  Consensus eigenwords (2+ heads agree):")
      (doseq [{:keys [word meaning gv agreement heads]}
              (filter #(>= (:agreement %) 2) agreement)]
        (println (format "    %-8s  %d/3 heads  %-24s  GV=%d  %s"
                         word agreement (str heads) gv (or meaning ""))))
      (println "\n  Solo eigenwords (one head only):")
      (doseq [{:keys [word meaning gv heads]}
              (filter #(= 1 (:agreement %)) agreement)]
        (println (format "    %-8s  %-10s  GV=%d  %s"
                         word (name (first heads)) gv (or meaning "")))))

    ;; Step 6: The residual — Hannah vs Believe
    (println "\n═══════════════════════════════════════════════════")
    (println "  THE RESIDUAL — Where Believability Changes Signal")
    (println "═══════════════════════════════════════════════════")

    (let [diff (matrix-divergence m-hannah m-believe vocab)]
      (println "\n  Top 20 words by divergence (row L1 distance):")
      (println (format "  %-8s %10s %6s  %s" "Word" "L1 dist" "GV" "Meaning"))
      (doseq [{:keys [word divergence gv meaning]} (take 20 diff)]
        (println (format "  %-8s %10.4f %6d  %s"
                         word divergence gv (or meaning ""))))

      ;; Show what changed for the most-shifted words
      (println "\n  Transition shifts for top 5 most-changed words:")
      (doseq [{:keys [word]} (take 5 diff)]
        (println (str "\n    " word " (" (or (dict/translate word) "?") "):"))
        (let [shifts (transition-shift m-hannah m-believe vocab word-idx word 5)]
          (doseq [{:keys [output meaning old new shift]} shifts]
            (println (format "      → %-8s  H=%.4f  B=%.4f  Δ=%+.4f  %s"
                             output old new shift (or meaning "")))))))

    ;; Step 7: Synthesis — gained/lost eigenwords
    (println "\n═══════════════════════════════════════════════════")
    (println "  SYNTHESIS — What Believability Reveals")
    (println "═══════════════════════════════════════════════════")

    (let [ew-h (set (map :word (find-eigenwords m-hannah vocab)))
          ew-b (set (map :word (find-eigenwords m-believe vocab)))
          gained (sort (clojure.set/difference ew-b ew-h))
          lost   (sort (clojure.set/difference ew-h ew-b))]
      (println (str "\n  Hannah eigenwords: " (count ew-h)))
      (println (str "  Believe eigenwords: " (count ew-b)))
      (println (str "  Gained: " (count gained) "  Lost: " (count lost)))
      (when (seq gained)
        (println "\n  + Eigenwords GAINED by believability:")
        (doseq [w gained]
          (println (format "    + %-8s  GV=%d  %s"
                           w (g/word-value w) (or (dict/translate w) "")))))
      (when (seq lost)
        (println "\n  - Eigenwords LOST by believability:")
        (doseq [w lost]
          (println (format "    - %-8s  GV=%d  %s"
                           w (g/word-value w) (or (dict/translate w) ""))))))

    ;; Lamb test
    (println "\n── The Lamb Test ──")
    (doseq [[label mk] [["M_hannah" :hannah] ["M_believe" :believe]
                          ["M_aaron" :aaron] ["M_right (mercy)" :right]
                          ["M_left (justice)" :left]]]
      (when-let [i (word-idx "כבש")]
        (let [m (matrices mk)
              self-w (la/entry m i i)
              top (take 3 (sort-by :weight >
                           (for [j (range n)]
                             {:word (vocab j)
                              :weight (la/entry m i j)})))]
          (println (format "    %-20s  self=%.4f  top=%s"
                           label self-w
                           (str/join ", "
                             (map #(format "%s(%.3f)" (:word %) (:weight %))
                                  top)))))))

    ;; Per-head attractor comparison
    (println "\n── Do the Heads Converge to the Same Voice? ──")
    (let [att-a (set (map :word (find-attractors m-aaron vocab 10)))
          att-r (set (map :word (find-attractors m-right vocab 10)))
          att-l (set (map :word (find-attractors m-left vocab 10)))
          all-three (clojure.set/intersection att-a att-r att-l)
          any-two (clojure.set/union
                    (clojure.set/intersection att-a att-r)
                    (clojure.set/intersection att-a att-l)
                    (clojure.set/intersection att-r att-l))]
      (println (str "  Aaron top-10: " (str/join ", " (sort att-a))))
      (println (str "  Right top-10: " (str/join ", " (sort att-r))))
      (println (str "  Left  top-10: " (str/join ", " (sort att-l))))
      (println (str "  All three agree: " (str/join ", " (sort all-three))))
      (println (str "  Any two agree:   " (str/join ", " (sort any-two)))))

    (println "\n═══════════════════════════════════════════════════")
    (println "  DONE. The three heads have spoken.")
    (println "═══════════════════════════════════════════════════")

    ;; Return for REPL exploration
    {:disc disc :stats stats :rates rates :matrices matrices
     :vocab vocab :word-idx word-idx}))

(comment
  (run)

  ;; Piece by piece:
  (def disc (discover-transitions))
  (def stats (transition-stats disc))
  (def rates (base-rates disc))

  ;; Explore a specific word's transitions
  (get stats ["כבש" "שכב"])
  (get stats ["אל" "לא"])

  ;; Explore base rates
  (sort-by val > rates)

  :end)
