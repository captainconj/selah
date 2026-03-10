(ns selah.basin
  "Basin of attraction — feed the oracle to itself.

   Give it a word. It gives back another word. Feed that back in.
   Over and over, like water circling in a basin until it goes still.

   Three words never move: love (אהבה), truth (אמת), life (חיים).
   Everything else orbits, shifts, drifts — but those three stand fixed.

   The machine has never read John 14:6."
  (:require [selah.oracle :as oracle]
            [selah.gematria :as g]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; ── One step ────────────────────────────────────────────────

(defn step
  "One oracle step: word → forward query → highest-weight Torah output.
   Uses the oracle-closed vocabulary (~7,300 words).
   When skip-self? true, excludes the input word from outputs (reveals flow)."
  ([word] (step word false))
  ([word skip-self?]
   (let [fwd (oracle/forward word :torah)
         known (:known-words fwd)]
     (when (seq known)
       (let [candidates (if skip-self?
                          (remove #(= (:word %) word) known)
                          known)
             sorted (sort-by (comp - :reading-count) candidates)
             top (first sorted)]
         (when top
           (let [next-word (:word top)]
             {:word word
              :gv (g/word-value word)
              :next next-word
              :next-gv (g/word-value next-word)
              :weight (:reading-count top)
              :total-readings (:total-readings fwd)
              :illuminations (:illumination-count fwd)
              :alternatives (count candidates)
              :self-weight (when skip-self?
                             (:reading-count (first (filter #(= (:word %) word) known))))
              :fixed? (= word next-word)})))))))

;; ── Walk the basin ──────────────────────────────────────────

(defn walk
  "Walk from a starting word until convergence or max steps.
   Returns {:start :steps [{:word :next :weight ...}] :converged? :fixed-point :cycle?}.
   Options:
     :skip-self?  Exclude self-loops (default false)."
  ([word] (walk word 30))
  ([word max-steps] (walk word max-steps {}))
  ([word max-steps {:keys [skip-self?] :or {skip-self? false}}]
   (loop [current word
          steps []
          seen #{}
          n 0]
     (cond
       ;; Max steps reached
       (>= n max-steps)
       {:start word :steps steps :converged? false :fixed-point nil :cycle? false}

       ;; Already seen this word — cycle or fixed point
       (contains? seen current)
       (let [last-step (peek steps)]
         (if (and last-step (= current (:word last-step)) (= current (:next last-step)))
           ;; Fixed point — word maps to itself
           {:start word :steps steps :converged? true
            :fixed-point current :cycle? false}
           ;; Cycle — orbiting
           {:start word :steps steps :converged? false
            :fixed-point nil :cycle? true
            :cycle-word current}))

       :else
       (let [result (step current skip-self?)]
         (if (nil? result)
           ;; Dead end — no illuminations (or only self-loops)
           {:start word :steps steps :converged? false
            :fixed-point nil :dead-end current}
           (if (:fixed? result)
             ;; Fixed point found
             {:start word :steps (conj steps result) :converged? true
              :fixed-point current :cycle? false}
             ;; Keep going
             (recur (:next result)
                    (conj steps result)
                    (conj seen current)
                    (inc n)))))))))

;; ── Trace with full path ────────────────────────────────────

(defn trace
  "Like walk, but returns a simple path: [word1 word2 word3 ... fixed-point].
   Easier to display."
  ([word] (trace word 30))
  ([word max-steps]
   (let [result (walk word max-steps)]
     (into (mapv :word (:steps result))
           (when (:fixed-point result)
             [(:fixed-point result)]))))
  ([word max-steps opts]
   (let [result (walk word max-steps opts)]
     (into (mapv :word (:steps result))
           (when (:fixed-point result)
             [(:fixed-point result)])))))

;; ── Batch: many starting words ──────────────────────────────

(defn landscape
  "Run many words through the basin. Returns map of fixed-point → [words that flow to it].
   This is the attractor landscape."
  [words]
  (let [results (pmap (fn [w] {:word w :walk (walk w 30)}) words)]
    {:by-attractor
     (->> results
          (filter #(:fixed-point (:walk %)))
          (group-by #(:fixed-point (:walk %)))
          (map (fn [[fp entries]]
                 {:fixed-point fp
                  :gv (g/word-value fp)
                  :basin-size (count entries)
                  :words (mapv :word entries)}))
          (sort-by (comp - :basin-size))
          vec)
     :cycles
     (->> results
          (filter #(:cycle? (:walk %)))
          (mapv (fn [{:keys [word walk]}]
                  {:word word :cycle-word (:cycle-word walk)})))
     :dead-ends
     (->> results
          (filter #(:dead-end (:walk %)))
          (mapv (fn [{:keys [word walk]}]
                  {:word word :dead-end (:dead-end walk)})))
     :total (count words)}))

;; ── Per-head basin — four readers, four landscapes ─────────

(defn step-all-heads
  "One oracle step for all four heads at once.
   Returns {:aaron {:next w :weight n} :god {...} ...}.
   nil entry means dead end for that reader."
  [word]
  (let [per-head (oracle/forward-by-head word :torah)]
    (into {}
      (for [reader [:aaron :god :right :left]]
        (let [top (first (get per-head reader))]
          [reader (when top
                    {:next (:word top)
                     :weight (:reading-count top)})])))))

(defn- follow-chain
  "Follow word → step-fn(word) → ... until fixed point, cycle, or max steps."
  [step-fn word max-steps]
  (loop [current word
         seen []
         n 0]
    (let [nxt (step-fn current)]
      (cond
        (nil? nxt)
        {:class :dead-end :steps n}

        (= nxt current)
        {:class (if (zero? n) :fixed-point :transient)
         :attractor current :steps n}

        (>= n max-steps)
        {:class :unknown :steps n}

        (some #{nxt} seen)
        {:class :cycle :steps n :cycle-word nxt}

        :else
        (recur nxt (conj seen current) (inc n))))))

(defn classify-head
  "Classify all words for one reader from a precomputed step map.
   step-map: {word → {:aaron {:next w} :god {:next w} ...}}.
   Returns {word → {:class :attractor :steps}}."
  [step-map reader]
  (let [step-fn (fn [w] (get-in step-map [w reader :next]))]
    (into {}
      (for [w (keys step-map)]
        [w (follow-chain step-fn w 30)]))))

(defn landscape-by-head
  "Run all Torah words through the basin with per-head resolution.
   One forward-by-head call per word → four step results.
   Returns {word → {:aaron {:next w :weight n} ...}}."
  [words]
  (let [n (count words)
        progress (atom 0)]
    (->> words
         (pmap (fn [w]
                 (let [p (swap! progress inc)]
                   (when (zero? (mod p 500))
                     (println (str "  " p "/" n "..."))))
                 [w (step-all-heads w)]))
         doall
         (into {}))))

;; ── Lazy data (from experiment 096) ─────────────────────────

(defn- load-edn [path]
  (let [f (io/file path)]
    (when (.exists f)
      (edn/read-string (slurp f)))))

(def ^:private word-idx
  "Word → {:class :attractor :steps :gv :meaning}. Lazy."
  (delay (load-edn "data/experiments/096/word-index.edn")))

(def ^:private attr-data
  "Attractors sorted by basin size desc. Lazy."
  (delay (load-edn "data/experiments/096/attractors.edn")))

(def ^:private cycle-data
  "Cycle orbits. Lazy."
  (delay (load-edn "data/experiments/096/cycles.edn")))

(def ^:private summary-data
  "Summary statistics. Lazy."
  (delay (load-edn "data/experiments/096/summary.edn")))

;; ── Predicates (O(1) via word-index) ───────────────────────

(defn word-class
  "Classification of w: :fixed-point | :transient | :cycle-member | :cycle-transient | :dead-end | nil."
  [w]
  (:class (get @word-idx w)))

(defn fixed-point?
  "Does w map to itself under one oracle step?"
  [w]
  (= :fixed-point (word-class w)))

(defn transient?
  "Does w flow to a fixed point in ≥1 steps?"
  [w]
  (= :transient (word-class w)))

(defn cycle-member?
  "Is w part of a true orbit (period ≥ 2)?"
  [w]
  (= :cycle-member (word-class w)))

(defn dead-end?
  "Does w produce no illumination output?"
  [w]
  (= :dead-end (word-class w)))

(defn converges?
  "Does w reach a fixed point (either immediately or after transient steps)?"
  [w]
  (boolean (#{:fixed-point :transient} (word-class w))))

;; ── Queries ────────────────────────────────────────────────

(defn attractor-for
  "The fixed point that w flows to, or nil."
  [w]
  (:attractor (get @word-idx w)))

(defn basin-of
  "Basin data for attractor w: {:word :meaning :gv :basin-size :basin [...]}.
   Also works for any word — looks up its attractor first."
  [w]
  (let [attr (or (when (fixed-point? w) w)
                 (attractor-for w))]
    (when attr
      (first (filter #(= attr (:word %)) @attr-data)))))

(defn steps-to
  "Number of steps from w to its attractor. 0 for fixed points, nil for non-converging."
  [w]
  (:steps (get @word-idx w)))

(defn attractors
  "All attractors sorted by basin size descending."
  []
  @attr-data)

(defn largest-basins
  "Top n attractors by basin size."
  [n]
  (take n @attr-data))

(defn cycles
  "All cycle orbits: [{:orbit [...] :period N :transients [...]}]."
  []
  @cycle-data)

(defn dead-ends
  "Set of all dead-end words."
  []
  (set (keep (fn [[w info]] (when (= :dead-end (:class info)) w)) @word-idx)))

(defn stats
  "Summary statistics map."
  []
  @summary-data)

;; ── REPL ────────────────────────────────────────────────────

(comment
  ;; Single step
  (step "כבש")   ;; lamb → ?
  (step "אהבה")  ;; love → ?

  ;; Walk from lamb
  (walk "כבש")

  ;; Simple path
  (trace "כבש")

  ;; Flow mode — skip self-loops
  (trace "כבש" 30 {:skip-self? true})

  ;; Walk from several words
  (landscape ["כבש" "שלום" "תורה" "משה" "ברית" "דם" "אור" "חשך"])

  ;; Predicates (from 096 classification)
  (fixed-point? "אמת")     ;; → true
  (word-class "כבש")       ;; → :transient
  (attractor-for "כבש")    ;; → "שכב"
  (cycle-member? "בחר")    ;; → true
  (largest-basins 5)
  (stats)
  )
