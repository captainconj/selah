(ns selah.text.mam
  "Miqra according to the Masorah (MAM) — Rabbi Dr. Avi Kadish's reconstruction.
   Based on the Aleppo Codex tradition. For Torah (where Aleppo is mostly lost),
   uses Leningrad + related manuscripts with editorial judgment.
   Source: Sefaria default Hebrew text."
  (:require [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(defn book-letters
  "Normalized letter stream for a book. Strips section markers ({פ}/{ס})."
  [book]
  (let [verses (sefaria/fetch-book book)
        raw    (apply str (map (comp norm/strip-section-markers norm/strip-html) verses))]
    (norm/letter-stream raw)))

(defn book-string [book]
  (apply str (book-letters book)))

(defn torah-letters []
  (vec (mapcat book-letters
               ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])))

(defn torah-string []
  (apply str (torah-letters)))

(comment
  (let [s (book-letters "Genesis")]
    [(count s) (apply str (take 20 s))])
  ;; => [78273 "בראשיתבראאלהיםאתהשמי"]
  )
