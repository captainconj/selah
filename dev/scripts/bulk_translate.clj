;; Bulk translate all Torah words Hebrew → English using the reverse model.
;; Run via nREPL: (load-file "dev/scripts/bulk_translate.clj")

(require '[selah.dict :as dict]
         '[selah.translate :as t]
         '[clojure.java.io :as io])

(def words (vec (dict/torah-words)))
(def n (count words))
(println "Translating" n "Torah words...")

(def ms @t/*reverse-state*)

(def results
  (into {}
    (map-indexed
      (fn [i w]
        (when (zero? (mod (inc i) 500))
          (println (str "  " (inc i) "/" n "...")))
        (try
          [w (t/translate-reverse w)]
          (catch Exception e
            [w nil])))
      words)))

(let [out-path "data/torah-english.edn"]
  (io/make-parents out-path)
  (spit out-path (pr-str results))
  (println "Saved" (count results) "translations to" out-path))
