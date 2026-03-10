(ns selah.for-the-human
  "English glosses for the operator.

   Not evidence. Not interpretation. Not the text.

   Curated translations come from selah.dict. Machine fallback comes from
   torah-english.edn. Both are approximate. Use for orientation, not claims."
  (:require [clojure.string :as str]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [selah.oracle :as oracle]))

(def ^:private missing-gloss "???")

(defn gloss-info
  "Return an operator-facing gloss and its provenance for a Hebrew word."
  [word]
  (when (string? word)
    (if-let [curated (dict/translate word)]
      {:gloss curated
       :gloss-source :curated}
      (when-let [machine (dict/translate-english word)]
        {:gloss machine
         :gloss-source :machine}))))

(defn gloss
  "Return the best available English gloss for a Hebrew word, or nil."
  [word]
  (:gloss (gloss-info word)))

(defn gloss!
  "Return a gloss when available, otherwise an explicit unknown marker."
  [word]
  (or (gloss word) missing-gloss))

(defn label
  "Render a word with an optional operator-facing gloss."
  [word]
  (if-let [g (gloss word)]
    (str word " (" g ")")
    word))

(defn label!
  "Render a word with an explicit gloss marker even when none is known."
  [word]
  (str word " (" (gloss! word) ")"))

(defn word-info
  "Operator-facing view of a Hebrew word."
  [word]
  (cond-> {:word word
           :gv (when (string? word) (g/word-value word))
           :label (label word)}
    (gloss-info word) (merge (gloss-info word))))

(defn- annotate-word-field
  [m source-key gloss-key gloss-source-key]
  (if-let [word (get m source-key)]
    (if-let [{:keys [gloss gloss-source]} (gloss-info word)]
      (cond-> m
        (not (contains? m gloss-key)) (assoc gloss-key gloss)
        (not (contains? m gloss-source-key)) (assoc gloss-source-key gloss-source))
      m)
    m))

(defn annotate
  "Recursively annotate display-facing result data with operator glosses.

   Adds :gloss to maps with :word and field-specific gloss keys for common
   result shapes such as :fixed-point, :dead-end, and :next-word."
  [x]
  (cond
    (map? x)
    (-> x
        (update-vals annotate)
        (annotate-word-field :word :gloss :gloss-source)
        (annotate-word-field :next-word :next-gloss :next-gloss-source)
        (annotate-word-field :fixed-point :fixed-point-gloss :fixed-point-gloss-source)
        (annotate-word-field :cycle-word :cycle-gloss :cycle-gloss-source)
        (annotate-word-field :dead-end :dead-end-gloss :dead-end-gloss-source))

    (vector? x)
    (mapv annotate x)

    (seq? x)
    (doall (map annotate x))

    (set? x)
    (set (map annotate x))

    :else
    x))

(defn- annotate-reader-sample
  [sample]
  (when sample
    (into {}
          (for [[reader word] sample]
            [reader (word-info word)]))))

(defn- annotate-word-list
  [xs]
  (mapv annotate xs))

(defn explain-ask
  [result]
  (let [annotated (annotate result)]
    (assoc annotated
           :input (word-info (:word result))
           :summary (cond
                      (:readable? result)
                      (format "%s lights %d illumination%s and yields %d total reading%s."
                              (label (:word result))
                              (:illumination-count result)
                              (if (= 1 (:illumination-count result)) "" "s")
                              (:total-readings result)
                              (if (= 1 (:total-readings result)) "" "s"))

                      :else
                      (format "%s is not readable from the breastplate."
                              (label (:word result)))))))

(defn explain-forward
  [result]
  (let [annotated (annotate result)
        top-known (first (:known-words annotated))]
    (assoc annotated
           :sample-readings (annotate-reader-sample (:sample-readings result))
           :top-known top-known
           :summary (format "%s lights %d illumination%s and yields %d reading%s. Top known output: %s."
                            (:letters result)
                            (:illumination-count result)
                            (if (= 1 (:illumination-count result)) "" "s")
                            (:total-readings result)
                            (if (= 1 (:total-readings result)) "" "s")
                            (if top-known
                              (label (:word top-known))
                              "none")))))

(defn explain-forward-by-head
  [result]
  (let [annotated (annotate result)
        leaders (into {}
                      (for [[reader xs] annotated]
                        [reader (first xs)]))]
    {:by-reader annotated
     :leaders leaders
     :summary (into {}
                    (for [[reader leader] leaders]
                      [reader (when leader
                                (str (name reader) ": " (label (:word leader))))]))}))

(defn explain-question
  [result]
  (let [annotated (annotate result)]
    (assoc annotated
           :summary (format "%d input word%s, %d total reading%s, %d coincidence%s."
                            (count (:words result))
                            (if (= 1 (count (:words result))) "" "s")
                            (count (:all-readings result))
                            (if (= 1 (count (:all-readings result))) "" "s")
                            (count (:coincidences result))
                            (if (= 1 (count (:coincidences result))) "" "s")))))

(defn explain-thummim
  [result]
  (when result
    (let [annotated (annotate result)]
      (assoc annotated
             :phrases (annotate-word-list (:phrases annotated))
             :summary (format "%s yields %d unique phrase reading%s across %d illumination%s."
                              (label (:word result))
                              (count (:phrases result))
                              (if (= 1 (count (:phrases result))) "" "s")
                              (:illumination-count result)
                              (if (= 1 (:illumination-count result)) "" "s"))))))

(defn explain-parse
  [phrases]
  {:phrases (annotate-word-list phrases)
   :summary (format "%d phrase reading%s."
                    (count phrases)
                    (if (= 1 (count phrases)) "" "s"))})

(defn explain-step
  [step]
  (let [annotated (annotate step)]
    (assoc annotated
           :from (word-info (:word step))
           :to (word-info (:next step))
           :summary (format "%s -> %s [%d/%d]"
                            (label (:word step))
                            (label (:next step))
                            (:weight step)
                            (:total-readings step)))))

(defn explain-walk
  [result]
  (let [annotated (annotate result)
        steps (mapv explain-step (:steps result))
        path (mapv :word steps)
        terminal (cond
                   (:fixed-point result) (:fixed-point result)
                   (:cycle-word result)  (:cycle-word result)
                   (:dead-end result)    (:dead-end result))
        path* (cond-> path terminal (conj terminal))
        outcome (cond
                  (:converged? result) (str "Fixed point at " (label (:fixed-point result)))
                  (:cycle? result)     (str "Cycle through " (label (:cycle-word result)))
                  (:dead-end result)   (str "Dead end at " (label (:dead-end result)))
                  :else                "Did not settle")]
    (assoc annotated
           :steps steps
           :path path*
           :path-labels (mapv label path*)
           :summary (format "%s in %d step%s."
                            outcome
                            (count (:steps result))
                            (if (= 1 (count (:steps result))) "" "s")))))

(defn explain-landscape
  [result]
  (let [annotated (annotate result)]
    (assoc annotated
           :by-attractor (mapv annotate (:by-attractor result))
           :cycles (mapv annotate (:cycles result))
           :dead-ends (mapv annotate (:dead-ends result))
           :summary (format "%d total words, %d attractors, %d cycles, %d dead ends."
                            (:total result)
                            (count (:by-attractor result))
                            (count (:cycles result))
                            (count (:dead-ends result))))))

(defn explain
  "Dispatch operator-facing explanation for a common Selah result shape."
  [result]
  (cond
    (and (map? result) (contains? result :readable?)) explain-ask
    (and (map? result) (contains? result :known-words)) explain-forward
    (and (map? result) (contains? result :coincidences)) explain-question
    (and (map? result) (contains? result :steps)) explain-walk
    (and (map? result) (contains? result :by-attractor)) explain-landscape
    (and (map? result) (contains? result :phrases)) explain-thummim
    (vector? result) explain-parse
    :else annotate))

(defn lines
  "Return a small vector of operator-facing summary lines."
  [result]
  (let [explained ((explain result) result)]
    (cond-> []
      (:summary explained) (conj (:summary explained))
      (:path-labels explained) (conj (str "Path: " (str/join " -> " (:path-labels explained))))
      (:top-known explained) (conj (str "Top known: " (label (:word (:top-known explained)))))
      (and (:phrases explained) (seq (:phrases explained)))
      (conj (str "First phrase: " (:text (first (:phrases explained))))))))

(defn ask
  [word]
  (explain-ask (oracle/ask word)))

(defn forward
  ([letters] (forward letters :torah))
  ([letters vocab]
   (explain-forward (oracle/forward letters vocab))))

(defn forward-by-head
  ([letters] (forward-by-head letters :torah))
  ([letters vocab]
   (explain-forward-by-head (oracle/forward-by-head letters vocab))))

(defn question
  [words]
  (explain-question (oracle/question words)))

(defn thummim-menu
  ([word] (thummim-menu word {}))
  ([word opts]
   (explain-thummim (oracle/thummim-menu word opts))))

(defn parse-letters
  ([letters] (parse-letters letters {}))
  ([letters opts]
   (explain-parse (oracle/parse-letters letters opts))))

(defn step
  ([word] (step word false))
  ([word skip-self?]
   (when-let [result (basin/step word skip-self?)]
     (explain-step result))))

(defn walk
  ([word] (walk word 30 {}))
  ([word max-steps] (walk word max-steps {}))
  ([word max-steps opts]
   (explain-walk (basin/walk word max-steps opts))))

(defn trace
  ([word] (trace word 30 {}))
  ([word max-steps] (trace word max-steps {}))
  ([word max-steps opts]
   (mapv label (basin/trace word max-steps opts))))

(defn landscape
  [words]
  (explain-landscape (basin/landscape words)))
