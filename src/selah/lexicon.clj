(ns selah.lexicon
  "Operator-facing multilingual lexicon candidates.

   Hebrew remains canonical. This namespace serves presentation-layer gloss
   candidates with provenance so UIs and tools can help the operator without
   contaminating evidence paths.

   Current sources:
   - curated dictionary entries from selah.dict
   - machine English from data/torah-english.edn
   - optional locale files under data/lexicon/<locale>/

   Future locale/model/LLM upgrades should be added here, not to core data."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [selah.dict :as dict]))

(def ^:private default-locale :en)
(def ^:private lexicon-root "data/lexicon")
(def ^:private torah-english-path "data/torah-english.edn")

(def ^:private source-priority
  {:curated 0
   :model-reviewed 1
   :llm-reviewed 2
   :llm 3
   :model 4
   :unknown 99})

(def ^:private quality-priority
  {:high 0
   :medium 1
   :low 2
   nil 99})

(defn- existing-subdirs [root]
  (let [dir (io/file root)]
    (if (.exists dir)
      (->> (.listFiles dir)
           (filter #(.isDirectory ^java.io.File %))
           (map #(.getName ^java.io.File %))
           (map keyword)
           set)
      #{})))

(defn supported-locales
  "Locales the presentation lexicon can serve today."
  []
  (set/union #{:en} (existing-subdirs lexicon-root)))

(defn normalize-locale
  "Coerce locale input to a supported keyword, falling back to English."
  [locale]
  (let [loc (cond
              (keyword? locale) locale
              (string? locale)  (keyword (str/lower-case locale))
              :else             default-locale)]
    (if ((supported-locales) loc) loc default-locale)))

(defn- normalize-candidate
  [locale source word candidate]
  (cond
    (string? candidate)
    {:text candidate
     :source source
     :locale locale}

    (map? candidate)
    (cond-> {:text (:text candidate)
             :source (or (:source candidate) source)
             :locale (or (:locale candidate) locale)}
      (:quality candidate) (assoc :quality (:quality candidate))
      (:model candidate) (assoc :model (:model candidate))
      (:notes candidate) (assoc :notes (:notes candidate))
      (:confidence candidate) (assoc :confidence (:confidence candidate))
      word (assoc :word word))

    :else
    nil))

(defn- normalize-entry
  [locale source word entry]
  (cond
    (string? entry)
    [(normalize-candidate locale source word entry)]

    (map? entry)
    (if (:text entry)
      [(normalize-candidate locale source word entry)]
      (->> entry
           (mapcat (fn [[_ v]]
                     (normalize-entry locale source word v)))
           vec))

    (sequential? entry)
    (->> entry
         (keep #(normalize-candidate locale source word %))
         vec)

    :else
    []))

(defn- dedupe-candidates
  [candidates]
  (->> candidates
       (reduce (fn [acc candidate]
                 (let [k [(:text candidate) (:source candidate) (:model candidate)]]
                   (if (or (nil? (:text candidate))
                           (contains? acc k))
                     acc
                     (assoc acc k candidate))))
               {})
       vals))

(defn- sort-candidates
  [candidates]
  (->> candidates
       (sort-by (fn [candidate]
                  [(get source-priority (:source candidate) 99)
                   (get quality-priority (:quality candidate) 99)
                   (or (:text candidate) "")]))
       vec))

(def ^:private machine-english*
  (delay
    (let [f (io/file torah-english-path)]
      (if (.exists f)
        (edn/read-string (slurp f))
        {}))))

(defn- builtin-curated-candidates
  [word locale]
  (if-let [text (dict/translate word)]
    [(normalize-candidate locale :curated word {:text text :quality :high})]
    []))

(defn- builtin-model-candidates
  [word locale]
  (if-let [text (get @machine-english* word)]
    [(normalize-candidate locale :model word {:text text
                                              :quality :medium
                                              :model "torah-english.edn"})]
    []))

(def ^:private file-specs
  [{:file "curated.edn" :source :curated}
   {:file "model-reviewed.edn" :source :model-reviewed}
   {:file "llm-reviewed.edn" :source :llm-reviewed}
   {:file "model.edn" :source :model}
   {:file "llm.edn" :source :llm}])

(def ^:private file-cache (atom {}))

(defn source-files
  "Expected locale file locations for layered gloss sources."
  ([]
   (source-files default-locale))
  ([locale]
   (let [locale (normalize-locale locale)]
     (into {}
           (map (fn [{:keys [file source]}]
                  [source (str lexicon-root "/" (name locale) "/" file)]))
           file-specs))))

(defn refresh!
  "Clear cached lexicon file data. Useful after editing locale files."
  []
  (reset! file-cache {})
  :ok)

(defn- read-file-map
  [path]
  (let [f (io/file path)]
    (when (.exists f)
      (edn/read-string (slurp f)))))

(defn- write-file-map!
  [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  path)

(defn- locale-file-data
  [locale]
  (or (get @file-cache locale)
      (let [data (into {}
                       (keep (fn [{:keys [file source]}]
                               (when-let [m (read-file-map (str lexicon-root "/" (name locale) "/" file))]
                                 [source m])))
                       file-specs)]
        (swap! file-cache assoc locale data)
        data)))

(defn- file-candidates
  [word locale]
  (let [locale (normalize-locale locale)
        file-data (locale-file-data locale)]
    (->> file-data
         (mapcat (fn [[source entries]]
                   (normalize-entry locale source word (get entries word))))
         vec)))

(defn gloss-candidates
  "All gloss candidates for a Hebrew word in the given locale."
  ([word] (gloss-candidates word default-locale))
  ([word locale]
   (let [locale (normalize-locale locale)]
     (->> (concat
           (builtin-curated-candidates word locale)
           (file-candidates word locale)
           (when (= locale :en)
             (builtin-model-candidates word locale)))
          dedupe-candidates
          sort-candidates))))

(defn best-candidate
  "Best available gloss candidate for a word and locale."
  ([word] (best-candidate word default-locale))
  ([word locale]
   (first (gloss-candidates word locale))))

(defn entry
  "Presentation lexicon entry for a Hebrew word."
  ([word] (entry word default-locale))
  ([word locale]
   (let [locale (normalize-locale locale)
         candidates (gloss-candidates word locale)]
     {:word word
      :locale locale
      :best (first candidates)
      :candidates candidates
      :sources (source-files locale)})))

(defn source-map
  "Read a locale/source map from disk. Returns {} when absent."
  ([source]
   (source-map source default-locale))
  ([source locale]
   (or (read-file-map (get (source-files locale) source))
       {})))

(defn write-source-map!
  "Write a locale/source map to disk and refresh caches."
  ([source data]
   (write-source-map! source data default-locale))
  ([source data locale]
   (let [path (get (source-files locale) source)]
     (write-file-map! path data)
     (refresh!)
     path)))

(defn materialize-model-file!
  "Populate data/lexicon/<locale>/model.edn from the legacy machine-English
   cache. For now only English has a source corpus."
  ([] (materialize-model-file! default-locale))
  ([locale]
   (let [locale (normalize-locale locale)]
     (when-not (= locale :en)
       (throw (ex-info "Legacy model materialization is only available for English" {:locale locale})))
     (let [payload (into {}
                         (map (fn [[word text]]
                                [word [{:text text
                                        :quality :medium
                                        :model "torah-english.edn"
                                        :notes "Imported from legacy machine-English cache"}]]))
                         @machine-english*)]
       (write-source-map! :model payload locale)))))

(defn upsert-candidate!
  "Add or replace a candidate in a locale/source file by :text."
  ([source word candidate]
   (upsert-candidate! source word candidate default-locale))
  ([source word candidate locale]
   (let [locale (normalize-locale locale)
         current (source-map source locale)
         existing (vec (normalize-entry locale source word (get current word)))
         candidate* (normalize-candidate locale source word candidate)
         merged (->> (conj (remove #(= (:text %) (:text candidate*)) existing) candidate*)
                     vec)]
     (write-source-map! source (assoc current word merged) locale))))

(defn remove-candidate!
  "Remove a candidate text from a locale/source file."
  ([source word text]
   (remove-candidate! source word text default-locale))
  ([source word text locale]
   (let [locale (normalize-locale locale)
         current (source-map source locale)
         remaining (vec (remove #(= (:text %) text)
                                (normalize-entry locale source word (get current word))))
         next-map (if (seq remaining)
                    (assoc current word remaining)
                    (dissoc current word))]
     (write-source-map! source next-map locale))))

(defn promote-candidate!
  "Copy a candidate text from one source layer to another, optionally removing
   it from the source layer afterward."
  ([from-source to-source word text]
   (promote-candidate! from-source to-source word text default-locale {}))
  ([from-source to-source word text locale]
   (promote-candidate! from-source to-source word text locale {}))
  ([from-source to-source word text locale {:keys [remove-from-source?]
                                            :or {remove-from-source? false}}]
   (let [locale (normalize-locale locale)
         candidate (first (filter #(= (:text %) text)
                                  (normalize-entry locale from-source word
                                                   (get (source-map from-source locale) word))))]
     (when-not candidate
       (throw (ex-info "Candidate text not found in source layer"
                       {:from-source from-source :to-source to-source :word word :text text :locale locale})))
     (upsert-candidate! to-source word (assoc candidate :source to-source) locale)
     (when remove-from-source?
       (remove-candidate! from-source word text locale))
     :ok)))

(defn- suspicious-text?
  [text]
  (boolean
   (or (str/includes? text "<unk>")
       (re-find #"[A-Z]{2,}" text)
       (re-find #"[.!?]{2,}|[.][A-Za-z]" text)
       (re-find #"[A-Za-z]{5,}[aeiouy][A-Za-z]{5,}" text)
       (re-find #"\b(Vi|Wi|Ye|Mc|Oog|Ina|Hela|Madwa)\b" text))))

(defn review-queue
  "Return suspicious model candidates for human/LLM review.

   Heuristics are intentionally simple: unknown tokens, odd capitalization,
   transliteration-like strings, and other obvious machine debris."
  ([] (review-queue default-locale 100))
  ([locale] (review-queue locale 100))
  ([locale limit]
   (let [locale (normalize-locale locale)
         model-map (source-map :model locale)]
     (->> model-map
          (keep (fn [[word candidates]]
                  (let [candidate (first (normalize-entry locale :model word candidates))]
                    (when (and candidate
                               (suspicious-text? (:text candidate)))
                      {:word word
                       :text (:text candidate)
                       :quality (:quality candidate)
                       :model (:model candidate)
                       :notes (:notes candidate)}))))
          (sort-by (juxt :text :word))
          (take limit)
          vec))))
