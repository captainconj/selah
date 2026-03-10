(ns selah.for-the-human
  "English glosses for the operator.

   Not evidence. Not interpretation. Not the text.

   Glosses come from selah.lexicon, which merges curated, model, and future
   reviewed/LLM sources with provenance. Use for orientation, not claims."
  (:require [clojure.string :as str]
            [selah.basin :as basin]
            [selah.gematria :as g]
            [selah.lexicon :as lexicon]
            [selah.oracle :as oracle]))

(def ^:private missing-gloss "???")
(def ^:private default-locale :en)

(def supported-locales
  "Locales the presentation layer can serve today.
   Only English is implemented now, but the surface is locale-aware so more
   languages can be added without changing the core data path."
  (lexicon/supported-locales))

(defn normalize-locale
  "Coerce locale input to a supported keyword, falling back to English."
  [locale]
  (lexicon/normalize-locale locale))

(defn gloss-info
  "Return an operator-facing gloss and its provenance for a Hebrew word."
  ([word] (gloss-info word default-locale))
  ([word locale]
   (when-let [{:keys [text source locale quality model notes confidence]} (:best (lexicon/entry word locale))]
     (cond-> {:gloss text
              :gloss-source source
              :gloss-locale locale}
       quality (assoc :gloss-quality quality)
       model (assoc :gloss-model model)
       notes (assoc :gloss-notes notes)
       confidence (assoc :gloss-confidence confidence)))))

(defn gloss-candidates
  "All operator-facing gloss candidates for a word."
  ([word] (gloss-candidates word default-locale))
  ([word locale]
   (:candidates (lexicon/entry word locale))))

(defn gloss
  "Return the best available English gloss for a Hebrew word, or nil."
  ([word] (gloss word default-locale))
  ([word locale]
   (:gloss (gloss-info word locale))))

(defn gloss!
  "Return a gloss when available, otherwise an explicit unknown marker."
  ([word] (gloss! word default-locale))
  ([word locale]
   (or (gloss word locale) missing-gloss)))

(defn source-label
  "Operator-facing label for a gloss source."
  [source]
  (case source
    :curated "curated"
    :model-reviewed "model-reviewed"
    :llm-reviewed "llm-reviewed"
    :llm "llm"
    :machine "machine"
    :model "model"
    "unknown"))

(defn label
  "Render a word with an optional operator-facing gloss."
  ([word] (label word default-locale))
  ([word locale]
   (if-let [g (gloss word locale)]
    (str word " (" g ")")
    word)))

(defn label!
  "Render a word with an explicit gloss marker even when none is known."
  ([word] (label! word default-locale))
  ([word locale]
   (str word " (" (gloss! word locale) ")")))

(defn word-info
  "Operator-facing view of a Hebrew word."
  ([word] (word-info word default-locale))
  ([word locale]
   (let [locale (normalize-locale locale)]
     (cond-> {:word word
              :gv (when (string? word) (g/word-value word))
              :locale locale
              :label (label word locale)
              :gloss-candidates (gloss-candidates word locale)}
       (gloss-info word locale) (merge (gloss-info word locale))))))

(defn phrase-info
  "Operator-facing view of a Hebrew phrase vector."
  ([phrase] (phrase-info phrase default-locale))
  ([phrase locale]
   (let [locale (normalize-locale locale)
         words (mapv #(word-info % locale) phrase)
         glosses (keep :gloss words)]
     {:phrase (vec phrase)
      :locale locale
      :text (str/join " " phrase)
      :labels (mapv :label words)
      :words words
      :candidate-count (reduce + (map #(count (:gloss-candidates %)) words))
      :gloss-text (when (seq glosses)
                    (str/join " + " glosses))})))

(defn title-text
  "Short provenance-aware tooltip text for a word."
  ([word] (title-text word default-locale))
  ([word locale]
   (when-let [{:keys [gloss gloss-source]} (gloss-info word locale)]
     (str gloss " [" (source-label gloss-source) "]"))))

(defn word-line
  "Compact operator-facing line for a single word."
  ([word] (word-line word default-locale))
  ([word locale]
   (let [{:keys [gv label gloss-source]} (word-info word locale)]
     (cond-> label
       gv (str " = " gv)
       gloss-source (str " [" (source-label gloss-source) "]")))))

(defn phrase-line
  "Compact operator-facing line for a phrase."
  ([phrase] (phrase-line phrase default-locale))
  ([phrase locale]
   (let [{:keys [text gloss-text]} (phrase-info phrase locale)]
     (if gloss-text
       (str text " -> " gloss-text)
       text))))

(defn reader-lines
  "Compact operator-facing lines for per-reader words."
  ([reader->word] (reader-lines reader->word default-locale))
  ([reader->word locale]
   (into {}
         (for [[reader word] reader->word]
           [reader (when word
                     (word-line word locale))]))))

(defn- annotate-word-field
  [m source-key gloss-key gloss-source-key gloss-locale-key locale]
  (if-let [word (get m source-key)]
    (if-let [{:keys [gloss gloss-source gloss-locale]} (gloss-info word locale)]
      (cond-> m
        (not (contains? m gloss-key)) (assoc gloss-key gloss)
        (not (contains? m gloss-source-key)) (assoc gloss-source-key gloss-source)
        (not (contains? m gloss-locale-key)) (assoc gloss-locale-key gloss-locale))
      m)
    m))

(defn annotate
  "Recursively annotate display-facing result data with operator glosses.

   Adds :gloss to maps with :word and field-specific gloss keys for common
   result shapes such as :fixed-point, :dead-end, and :next-word."
  ([x] (annotate x default-locale))
  ([x locale]
   (let [locale (normalize-locale locale)]
     (cond
       (map? x)
       (-> x
           (update-vals #(annotate % locale))
           (annotate-word-field :word :gloss :gloss-source :gloss-locale locale)
           (annotate-word-field :next-word :next-gloss :next-gloss-source :next-gloss-locale locale)
           (annotate-word-field :fixed-point :fixed-point-gloss :fixed-point-gloss-source :fixed-point-gloss-locale locale)
           (annotate-word-field :cycle-word :cycle-gloss :cycle-gloss-source :cycle-gloss-locale locale)
           (annotate-word-field :dead-end :dead-end-gloss :dead-end-gloss-source :dead-end-gloss-locale locale))

       (vector? x)
       (mapv #(annotate % locale) x)

       (seq? x)
       (doall (map #(annotate % locale) x))

       (set? x)
       (set (map #(annotate % locale) x))

       :else
       x))))

(defn- annotate-reader-sample
  [sample locale]
  (when sample
    (into {}
          (for [[reader word] sample]
            [reader (word-info word locale)]))))

(defn- annotate-word-list
  [xs locale]
  (mapv #(annotate % locale) xs))

(defn explain-ask
  ([result] (explain-ask result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)]
     (assoc annotated
            :input (word-info (:word result) locale)
            :summary (if (:readable? result)
                       (format "%s lights %d illumination%s and yields %d total reading%s."
                               (label (:word result) locale)
                               (:illumination-count result)
                               (if (= 1 (:illumination-count result)) "" "s")
                               (:total-readings result)
                               (if (= 1 (:total-readings result)) "" "s"))
                       (format "%s is not readable from the breastplate."
                               (label (:word result) locale)))))))

(defn explain-forward
  ([result] (explain-forward result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)
         top-known (first (:known-words annotated))]
     (assoc annotated
            :sample-readings (annotate-reader-sample (:sample-readings result) locale)
            :top-known top-known
            :summary (format "%s lights %d illumination%s and yields %d reading%s. Top known output: %s."
                             (:letters result)
                             (:illumination-count result)
                             (if (= 1 (:illumination-count result)) "" "s")
                             (:total-readings result)
                             (if (= 1 (:total-readings result)) "" "s")
                             (if top-known
                               (label (:word top-known) locale)
                               "none"))))))

(defn explain-forward-by-head
  ([result] (explain-forward-by-head result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)
         leaders (into {}
                       (for [[reader xs] annotated]
                         [reader (first xs)]))]
     {:by-reader annotated
      :leaders leaders
      :summary (into {}
                     (for [[reader leader] leaders]
                       [reader (when leader
                                 (str (name reader) ": " (label (:word leader) locale)))]))})))

(defn explain-question
  ([result] (explain-question result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)]
     (assoc annotated
            :summary (format "%d input word%s, %d total reading%s, %d coincidence%s."
                             (count (:words result))
                             (if (= 1 (count (:words result))) "" "s")
                             (count (:all-readings result))
                             (if (= 1 (count (:all-readings result))) "" "s")
                             (count (:coincidences result))
                             (if (= 1 (count (:coincidences result))) "" "s"))))))

(defn explain-thummim
  ([result] (explain-thummim result default-locale))
  ([result locale]
   (when result
     (let [annotated (annotate result locale)]
       (assoc annotated
              :phrases (annotate-word-list (:phrases annotated) locale)
              :summary (format "%s yields %d unique phrase reading%s across %d illumination%s."
                               (label (:word result) locale)
                               (count (:phrases result))
                               (if (= 1 (count (:phrases result))) "" "s")
                               (:illumination-count result)
                               (if (= 1 (:illumination-count result)) "" "s")))))))

(defn explain-parse
  ([phrases] (explain-parse phrases default-locale))
  ([phrases locale]
   {:phrases (annotate-word-list phrases locale)
    :summary (format "%d phrase reading%s."
                     (count phrases)
                     (if (= 1 (count phrases)) "" "s"))}))

(defn explain-step
  ([step] (explain-step step default-locale))
  ([step locale]
   (let [annotated (annotate step locale)]
     (assoc annotated
            :from (word-info (:word step) locale)
            :to (word-info (:next step) locale)
            :summary (format "%s -> %s [%d/%d]"
                             (label (:word step) locale)
                             (label (:next step) locale)
                             (:weight step)
                             (:total-readings step))))))

(defn explain-walk
  ([result] (explain-walk result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)
         steps (mapv #(explain-step % locale) (:steps result))
         path (mapv :word steps)
         terminal (cond
                    (:fixed-point result) (:fixed-point result)
                    (:cycle-word result)  (:cycle-word result)
                    (:dead-end result)    (:dead-end result))
         path* (cond-> path terminal (conj terminal))
         outcome (cond
                   (:converged? result) (str "Fixed point at " (label (:fixed-point result) locale))
                   (:cycle? result)     (str "Cycle through " (label (:cycle-word result) locale))
                   (:dead-end result)   (str "Dead end at " (label (:dead-end result) locale))
                   :else                "Did not settle")]
     (assoc annotated
            :steps steps
            :path path*
            :path-labels (mapv #(label % locale) path*)
            :summary (format "%s in %d step%s."
                             outcome
                             (count (:steps result))
                             (if (= 1 (count (:steps result))) "" "s"))))))

(defn explain-landscape
  ([result] (explain-landscape result default-locale))
  ([result locale]
   (let [annotated (annotate result locale)]
     (assoc annotated
            :by-attractor (mapv #(annotate % locale) (:by-attractor result))
            :cycles (mapv #(annotate % locale) (:cycles result))
            :dead-ends (mapv #(annotate % locale) (:dead-ends result))
            :summary (format "%d total words, %d attractors, %d cycles, %d dead ends."
                             (:total result)
                             (count (:by-attractor result))
                             (count (:cycles result))
                             (count (:dead-ends result)))))))

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

(defn present
  "Explain a result in the requested locale."
  ([result] (present result default-locale))
  ([result locale]
   ((explain result) result (normalize-locale locale))))

(defn lines
  "Return a small vector of operator-facing summary lines."
  ([result] (lines result default-locale))
  ([result locale]
   (let [explained (present result locale)
         path-line (when-let [path-labels (:path-labels explained)]
                     (str "Path: " (str/join " -> " path-labels)))
         top-line (when-let [top-known (:top-known explained)]
                    (str "Top known: " (label (:word top-known) locale)))
         phrase-line (when (seq (:phrases explained))
                       (str "First phrase: " (:text (first (:phrases explained)))))]
     (vec
      (remove nil?
              [(:summary explained)
               path-line
               top-line
               phrase-line])))))

(defn ask
  ([word] (ask word default-locale))
  ([word locale]
   (explain-ask (oracle/ask word) locale)))

(defn forward
  ([letters] (forward letters :torah default-locale))
  ([letters vocab] (forward letters vocab default-locale))
  ([letters vocab locale]
   (explain-forward (oracle/forward letters vocab) locale)))

(defn forward-by-head
  ([letters] (forward-by-head letters :torah default-locale))
  ([letters vocab] (forward-by-head letters vocab default-locale))
  ([letters vocab locale]
   (explain-forward-by-head (oracle/forward-by-head letters vocab) locale)))

(defn question
  ([words] (question words default-locale))
  ([words locale]
   (explain-question (oracle/question words) locale)))

(defn thummim-menu
  ([word] (thummim-menu word {} default-locale))
  ([word opts] (thummim-menu word opts default-locale))
  ([word opts locale]
   (explain-thummim (oracle/thummim-menu word opts) locale)))

(defn parse-letters
  ([letters] (parse-letters letters {} default-locale))
  ([letters opts] (parse-letters letters opts default-locale))
  ([letters opts locale]
   (explain-parse (oracle/parse-letters letters opts) locale)))

(defn step
  ([word] (step word false default-locale))
  ([word skip-self?] (step word skip-self? default-locale))
  ([word skip-self? locale]
   (when-let [result (basin/step word skip-self?)]
     (explain-step result locale))))

(defn walk
  ([word] (walk word 30 {} default-locale))
  ([word max-steps] (walk word max-steps {} default-locale))
  ([word max-steps opts] (walk word max-steps opts default-locale))
  ([word max-steps opts locale]
   (explain-walk (basin/walk word max-steps opts) locale)))

(defn trace
  ([word] (trace word 30 {} default-locale))
  ([word max-steps] (trace word max-steps {} default-locale))
  ([word max-steps opts] (trace word max-steps opts default-locale))
  ([word max-steps opts locale]
   (mapv #(label % locale) (basin/trace word max-steps opts))))

(defn landscape
  ([words] (landscape words default-locale))
  ([words locale]
   (explain-landscape (basin/landscape words) locale)))
