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
   :reviewed 1
   :model-reviewed 2
   :llm-reviewed 3
   :llm 4
   :model 5
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
  [{:file "curated.edn" :source :reviewed}
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
