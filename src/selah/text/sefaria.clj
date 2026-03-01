(ns selah.text.sefaria
  "Sefaria API client — fetch Hebrew text (MAM), cache locally."
  (:require [clj-http.lite.client :as http]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [selah.text.normalize :as norm]))

(def api-base "https://www.sefaria.org/api/texts/")

(def book-chapters
  {"Genesis"     50
   "Exodus"      40
   "Leviticus"   27
   "Numbers"     36
   "Deuteronomy" 34})

(def cache-dir "data/cache/sefaria")

(defn- cache-path [ref]
  (let [safe-name (-> ref
                      (.replace " " "-")
                      (.replace "." "-")
                      (.toLowerCase))]
    (str cache-dir "/" safe-name ".json")))

(defn- ensure-cache-dir! []
  (.mkdirs (io/file cache-dir)))

(defn fetch-ref
  "Fetch a Sefaria reference (e.g. \"Genesis.1\"). Returns raw JSON map.
   Caches to disk."
  [ref]
  (let [path (cache-path ref)]
    (if (.exists (io/file path))
      (json/read-str (slurp path) :key-fn keyword)
      (do
        (ensure-cache-dir!)
        (let [url  (str api-base ref "?lang=he")
              resp (http/get url {:accept :json})
              data (json/read-str (:body resp) :key-fn keyword)]
          (spit path (json/write-str data))
          data)))))

(defn- extract-hebrew
  "Extract Hebrew text from a Sefaria response. Returns a flat seq of verse strings."
  [data]
  (let [he (:he data)]
    (if (string? he)
      [he]
      (flatten he))))

(defn fetch-chapter
  "Fetch a single chapter's Hebrew text as a seq of verse strings."
  [book chapter]
  (let [ref  (str book "." chapter)
        data (fetch-ref ref)]
    (extract-hebrew data)))

(defn fetch-book
  "Fetch an entire book's Hebrew text. Returns a seq of all verse strings in order."
  [book]
  (let [chapters (get book-chapters book)]
    (when chapters
      (mapcat #(fetch-chapter book %) (range 1 (inc chapters))))))

(defn book-letters
  "Fetch an entire book and return its normalized letter stream (vector of chars)."
  [book]
  (let [verses (fetch-book book)
        raw    (apply str (map norm/strip-html verses))]
    (norm/letter-stream raw)))

(defn book-string
  "Fetch an entire book and return its normalized letter string."
  [book]
  (apply str (book-letters book)))

(comment
  (fetch-chapter "Genesis" 1)
  (let [s (book-letters "Genesis")]
    [(count s) (apply str (take 20 s))])
  )
