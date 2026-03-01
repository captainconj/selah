(ns selah.text.oshb
  "Parser for OpenScriptures Hebrew Bible (OSHB/WLC) XML files.
   Provides the Westminster Leningrad Codex text — the gold standard."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [selah.text.normalize :as norm])
  (:import [javax.xml.parsers SAXParserFactory]
           [org.xml.sax.helpers DefaultHandler]
           [org.xml.sax Attributes]))

(def oshb-dir "data/sources/oshb/wlc")

(def book-files
  {"Genesis"     "Gen.xml"
   "Exodus"      "Exod.xml"
   "Leviticus"   "Lev.xml"
   "Numbers"     "Num.xml"
   "Deuteronomy" "Deut.xml"})

(defn- parse-oshb-xml
  "Parse an OSHB XML file, extracting word text from <w> elements.
   Returns a seq of {:chapter :verse :text} maps."
  [file-path]
  (let [words (atom [])
        current-chapter (atom nil)
        current-verse (atom nil)
        in-word? (atom false)
        in-qere? (atom false)
        word-buf (StringBuilder.)
        handler (proxy [DefaultHandler] []
                  (startElement [uri local qname ^Attributes attrs]
                    (cond
                      (= qname "chapter")
                      (let [osis-id (.getValue attrs "osisID")]
                        (reset! current-chapter
                                (Integer/parseInt (last (str/split osis-id #"\.")))))

                      (= qname "verse")
                      (let [osis-id (.getValue attrs "osisID")
                            parts (str/split osis-id #"\.")]
                        (reset! current-verse (Integer/parseInt (last parts))))

                      ;; Track when we're inside a qere variant
                      (= qname "rdg")
                      (when (= (.getValue attrs "type") "x-qere")
                        (reset! in-qere? true))

                      (= qname "w")
                      (when-not @in-qere?  ; skip qere words — use kethiv (what's written)
                        (reset! in-word? true)
                        (.setLength word-buf 0))))

                  (characters [ch start length]
                    (when @in-word?
                      (.append word-buf ch start length)))

                  (endElement [uri local qname]
                    (cond
                      (= qname "rdg")
                      (reset! in-qere? false)

                      (= qname "w")
                      (when @in-word?
                        (reset! in-word? false)
                        (let [text (.toString word-buf)]
                          (swap! words conj
                                 {:chapter @current-chapter
                                  :verse   @current-verse
                                  :text    text}))))))]
    (let [factory (SAXParserFactory/newInstance)]
      (.setNamespaceAware factory false)
      (let [parser (.newSAXParser factory)]
        (.parse parser (io/file file-path) handler)))
    @words))

(defn book-words
  "Get all words from a book as a seq of {:chapter :verse :text} maps."
  [book]
  (let [filename (get book-files book)]
    (when filename
      (parse-oshb-xml (str oshb-dir "/" filename)))))

(defn book-text
  "Get the full Hebrew text of a book (with niqqud) as a single string.
   Words are space-separated."
  [book]
  (->> (book-words book)
       (map :text)
       (str/join " ")))

(defn book-letters
  "Get the normalized letter stream (consonants only) for a book."
  [book]
  (norm/letter-stream (book-text book)))

(defn book-string
  "Get the normalized letter string (consonants only) for a book."
  [book]
  (apply str (book-letters book)))

(defn torah-letters
  "Get the complete Torah letter stream from OSHB/WLC."
  []
  (vec (mapcat book-letters
               ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])))

(defn torah-string
  "Get the complete Torah as a single normalized string."
  []
  (apply str (torah-letters)))

(comment
  ;; Quick test
  (let [words (book-words "Genesis")]
    [(count words) (:text (first words))])

  (let [s (book-letters "Genesis")]
    [(count s) (apply str (take 20 s))])
  )
