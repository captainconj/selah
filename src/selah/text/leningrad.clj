(ns selah.text.leningrad
  "Westminster Leningrad Codex — the oldest complete Masoretic manuscript (~1008 CE).
   Primary source: OSHB (OpenScriptures Hebrew Bible, WLC 4.20).
   Fallback: Sefaria 'Tanach with Text Only' (same underlying data)."
  (:require [selah.text.oshb :as oshb]))

;; OSHB is our gold source — it's offline and matches Sefaria's Leningrad text exactly.

(def book-letters oshb/book-letters)
(def book-string oshb/book-string)
(def torah-letters oshb/torah-letters)
(def torah-string oshb/torah-string)

(comment
  (let [s (book-letters "Genesis")]
    [(count s) (apply str (take 20 s))])
  ;; => [78143 "בראשיתבראאלהיםאתהשמי"]
  )
