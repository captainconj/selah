(ns selah.text.sefaria
  "Sefaria API client — fetch Hebrew text (MAM), cache locally."
  (:require [clj-http.lite.client :as http]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def api-base "https://www.sefaria.org/api/texts/")

(def book-chapters
  {;; Torah
   "Genesis"       50
   "Exodus"        40
   "Leviticus"     27
   "Numbers"       36
   "Deuteronomy"   34
   ;; Former Prophets
   "Joshua"        24
   "Judges"        21
   "I Samuel"      31
   "II Samuel"     24
   "I Kings"       22
   "II Kings"      25
   ;; Latter Prophets
   "Isaiah"        66
   "Jeremiah"      52
   "Ezekiel"       48
   "Hosea"         14
   "Joel"           4
   "Amos"           9
   "Obadiah"        1
   "Jonah"          4
   "Micah"          7
   "Nahum"          3
   "Habakkuk"       3
   "Zephaniah"      3
   "Haggai"         2
   "Zechariah"     14
   "Malachi"        3
   ;; Writings
   "Psalms"       150
   "Proverbs"      31
   "Job"           42
   "Song of Songs"  8
   "Ruth"           4
   "Lamentations"   5
   "Ecclesiastes"  12
   "Esther"        10
   "Daniel"        12
   "Ezra"          10
   "Nehemiah"      13
   "I Chronicles"  29
   "II Chronicles" 36})

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
        (let [url  (str api-base (str/replace ref " " "%20") "?lang=he")
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
