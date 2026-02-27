(ns selah.text.normalize
  "Hebrew text normalization — the foundation.
   Reduces any Hebrew text to a pure letter stream (א–ת only).")

(defn hebrew-letter?
  "True if c is a Hebrew letter (א–ת, including final forms)."
  [^Character c]
  (let [cp (int c)]
    (<= 0x05D0 cp 0x05EA)))

(defn normalize
  "Strip everything except Hebrew letters. Returns a string of pure letters."
  [^String text]
  (->> text
       (filter hebrew-letter?)
       (apply str)))

(defn letter-stream
  "Normalize text and return as a vector of characters for indexed access."
  [^String text]
  (vec (filter hebrew-letter? text)))

(defn stream-length
  "Length of the normalized letter stream."
  [stream]
  (count stream))

(defn letter-at
  "Get the letter at index i (0-based). Returns nil if out of bounds."
  [stream i]
  (when (and (>= i 0) (< i (count stream)))
    (nth stream i)))

(defn strip-html
  "Remove HTML tags from a string (Sefaria responses contain markup)."
  [^String s]
  (if s
    (.replaceAll s "<[^>]*>" "")
    ""))

(comment
  ;; Quick test
  (normalize "בְּרֵאשִׁ֖ית בָּרָ֣א אֱלֹהִ֑ים")
  ;; => "בראשיתבראאלהים"

  (let [s (letter-stream "בְּרֵאשִׁ֖ית")]
    [(count s) s])
  ;; => [6 [\ב \ר \א \ש \י \ת]]
  )
