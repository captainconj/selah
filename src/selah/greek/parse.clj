(ns selah.greek.parse
  "Parsers for Greek NT text formats.
   Each parser returns a seq of word maps:
     {:book \"Matthew\" :chapter 1 :verse 1 :word-num 1
      :text \"Βίβλος\" :lemma \"βίβλος\" :pos \"N\" :morph \"NSF\"
      :strongs 976}"
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [selah.greek.normalize :as norm]))

;; ── Helpers ────────────────────────────────────────────────────────

(defn- clean-text
  "Strip punctuation and paragraph markers from a Greek word."
  [s]
  (str/replace s #"[¶·;,.\-·\[\](){}\"'«»]" ""))

(defn- parse-ref
  "Parse '1:2' or '1:2.3' into {:chapter 1 :verse 2} or {:chapter 1 :verse 2 :word-num 3}."
  [ref-str]
  (let [parts (str/split ref-str #"[:.]+")]
    (cond-> {:chapter (parse-long (nth parts 0))
             :verse   (parse-long (nth parts 1))}
      (>= (count parts) 3) (assoc :word-num (parse-long (nth parts 2))))))

;; ── Byzantine CSV (Unicode Greek, verse-level) ────────────────────

(defn parse-byzantine-csv
  "Parse a Byzantine CSV file (chapter,verse,text).
   Returns word maps with :text only (no morphology)."
  [path]
  (let [lines (str/split-lines (slurp path))]
    (for [line (rest lines)  ; skip header
          :let [[_ ch vs text] (re-matches #"(\d+),(\d+),\"?(.+?)\"?\s*$" line)]
          :when ch
          :let [chapter (parse-long ch)
                verse   (parse-long vs)
                words   (str/split (str/replace text #"[¶]" "") #"\s+")]
          [i w] (map-indexed vector words)
          :when (seq w)]
      {:book "Matthew" :chapter chapter :verse verse
       :word-num (inc i) :text (clean-text w)})))

;; ── KJTR verse-level (Unicode Greek, BBCCCVVV prefix) ─────────────

(defn parse-kjtr-txt
  "Parse KJTR.txt for a specific book (default Matthew = 40).
   Returns word maps with :text only."
  ([path] (parse-kjtr-txt path 40))
  ([path book-num]
   (let [prefix (str book-num)
         lines  (filter #(str/starts-with? % prefix)
                        (str/split-lines (slurp path)))]
     (for [line lines
           :let [[ref-str & rest-line] (str/split line #"\s+" 2)
                 text  (first rest-line)
                 bn    (parse-long (subs ref-str 0 2))
                 ch    (parse-long (subs ref-str 2 5))
                 vs    (parse-long (subs ref-str 5 8))
                 words (str/split (str/replace text #"[¶]" "") #"\s+")]
           :when (= bn book-num)
           [i w] (map-indexed vector words)
           :when (seq w)]
       {:book "Matthew" :chapter ch :verse vs
        :word-num (inc i) :text (clean-text w)}))))

;; ── KJTR TSV (Unicode Greek, word-per-line with morphology) ───────

(defn parse-kjtr-tsv
  "Parse KJTR.tsv for a specific book (default Matthew = 40).
   Returns word maps with full morphology."
  ([path] (parse-kjtr-tsv path 40))
  ([path book-num]
   (let [prefix (str book-num)
         lines  (str/split-lines (slurp path))]
     (loop [lines (rest lines)  ; skip header
            verse-word-counts {}
            result []]
       (if (empty? lines)
         result
         (let [line  (first lines)
               parts (str/split line #"\t")]
           (if (< (count parts) 7)
             (recur (rest lines) verse-word-counts result)
             (let [ref-str (nth parts 0)
                   bn      (parse-long (subs ref-str 0 2))
                   ch      (parse-long (subs ref-str 2 5))
                   vs      (parse-long (subs ref-str 5 8))
                   vkey    [ch vs]
                   wn      (inc (get verse-word-counts vkey 0))]
               (if (not= bn book-num)
                 (recur (rest lines) verse-word-counts result)
                 (recur (rest lines)
                        (assoc verse-word-counts vkey wn)
                        (conj result
                              {:book     "Matthew"
                               :chapter  ch
                               :verse    vs
                               :word-num wn
                               :text     (clean-text (nth parts 1))
                               :lemma    (nth parts 3)
                               :strongs  (parse-long (nth parts 4))
                               :pos      (nth parts 5)
                               :morph    (nth parts 6)})))))))))))

;; ── SBLGNT MorphGNT (Unicode Greek, word-per-line, 7 columns) ────

(defn parse-sblgnt
  "Parse a MorphGNT file (space-separated, 7 columns per word).
   Ref format: BBCCVV. Returns word maps with full morphology."
  [path]
  (let [lines (str/split-lines (slurp path))]
    (loop [lines lines
           verse-word-counts {}
           result []]
      (if (empty? lines)
        result
        (let [parts (str/split (str/trim (first lines)) #"\s+")]
          (if (< (count parts) 7)
            (recur (rest lines) verse-word-counts result)
            (let [ref-str (nth parts 0)
                  ch      (parse-long (subs ref-str 2 4))
                  vs      (parse-long (subs ref-str 4 6))
                  vkey    [ch vs]
                  wn      (inc (get verse-word-counts vkey 0))
                  pos     (nth parts 1)
                  morph   (nth parts 2)
                  text    (nth parts 3)  ; as printed
                  norm-w  (nth parts 4)  ; stripped
                  lemma   (nth parts 6)]
              (recur (rest lines)
                     (assoc verse-word-counts vkey wn)
                     (conj result
                           {:book     "Matthew"
                            :chapter  ch
                            :verse    vs
                            :word-num wn
                            :text     (clean-text text)
                            :normalized norm-w
                            :lemma    lemma
                            :pos      (str/trim pos)
                            :morph    (str/trim morph)})))))))))

;; ── Tischendorf Unicode (word-per-line, Strong's + dual lemma) ────

(defn parse-tischendorf-unicode
  "Parse Tischendorf Unicode TUP file (word-per-line).
   Format: BOOK C:V.W text morph strongs lemma1 ! lemma2"
  [path]
  (let [lines (str/split-lines (slurp path))]
    (for [line lines
          :let [parts (str/split (str/trim line) #"\s+")]
          :when (>= (count parts) 6)
          :let [ref      (nth parts 1)
                ref-map  (parse-ref ref)
                text     (nth parts 2)
                morph    (nth parts 3)
                strongs  (parse-long (nth parts 4))
                lemma    (nth parts 5)]]
      (merge {:book "Matthew" :text (clean-text text)
              :morph morph :strongs strongs :lemma lemma
              :pos (str (first morph))}
             ref-map))))

;; ── Robinson parsed (WH, TR — transliterated, verse-per-line) ─────

(defn- parse-robinson-words
  "Parse a Robinson-format text block into word maps.
   Each word is followed by Strong's number(s) and {MORPH}."
  [chapter verse text]
  (let [tokens (re-seq #"(\S+)\s+(\d+)(?:\s+\d+)?\s+\{([^}]+)\}" text)]
    (map-indexed
      (fn [i [_ word strongs morph]]
        {:book     "Matthew"
         :chapter  chapter
         :verse    verse
         :word-num (inc i)
         :text     (norm/robinson->unicode word)
         :strongs  (parse-long strongs)
         :pos      (str (first morph))
         :morph    morph})
      tokens)))

(defn parse-robinson-parsed
  "Parse a Robinson-format parsed file (WH or TR).
   Handles continuation lines (indented with space)."
  [path]
  (let [lines   (str/split-lines (slurp path))
        ;; Join continuation lines (start with space) onto previous line
        joined  (reduce (fn [acc line]
                          (if (and (seq acc) (str/starts-with? line " "))
                            (update acc (dec (count acc)) str " " (str/trim line))
                            (conj acc (str/trim line))))
                        [] lines)]
    (mapcat (fn [line]
              (when-let [[_ ch vs rest-text] (re-matches #"(\d+):(\d+)\s+(.*)" line)]
                (parse-robinson-words (parse-long ch) (parse-long vs) rest-text)))
            joined)))

;; ── Scrivener text-only (transliterated, verse-per-line) ──────────

(defn parse-scrivener-textonly
  "Parse Scrivener text-only file. No morphology.
   Handles title blocks and continuation lines."
  [path]
  (let [lines   (str/split-lines (slurp path))
        ;; Skip title/header lines, join continuations
        joined  (reduce (fn [acc line]
                          (cond
                            (re-matches #"\s*\[.*\]\s*" line) acc  ; skip title
                            (re-matches #"\s*$" line) acc          ; skip empty
                            (and (seq acc) (not (re-find #"^\s*\d+:" line)))
                            (update acc (dec (count acc)) str " " (str/trim line))
                            :else (conj acc (str/trim line))))
                        [] lines)]
    (mapcat (fn [line]
              (when-let [[_ ch vs text] (re-matches #"(\d+):(\d+)\s+(.*)" line)]
                (let [cleaned (str/replace text #"\[.*?\]" "")
                      words   (remove str/blank? (str/split (str/trim cleaned) #"\s+"))]
                  (map-indexed
                    (fn [i w]
                      {:book     "Matthew"
                       :chapter  (parse-long ch)
                       :verse    (parse-long vs)
                       :word-num (inc i)
                       :text     (norm/robinson->unicode w)})
                    words))))
            joined)))

;; ── Unified loading ────────────────────────────────────────────────

(def variant-paths
  "Default paths for each variant's Matthew data."
  {:byzantine       "/home/scott/Projects/greektext-byzantine/csv-unicode/ccat/no-variants/MAT.csv"
   :kjtr            "/home/scott/Projects/greektext-kjtr/KJTR.txt"
   :kjtr-morphology "/home/scott/Projects/greektext-kjtr/KJTR.tsv"
   :sblgnt          "/home/scott/Projects/greektext-sblgnt/61-Mt-morphgnt.txt"
   :tischendorf     "/home/scott/Projects/greektext-tischendorf-data/word-per-line/1.1/Unicode/MT.TUP"
   :westcott-hort   "/home/scott/Projects/greektext-westcott-hort/parsed/MT.UWH"
   :textus-receptus "/home/scott/Projects/greektext-textus-receptus/parsed/MT.UTR"
   :scrivener       "/home/scott/Projects/greektext-scrivener-1894/textonly/MT.SCV"})

(defn load-matthew
  "Load Matthew from a specific variant. Returns seq of word maps."
  [variant]
  (let [path (get variant-paths variant)]
    (when-not path (throw (ex-info (str "Unknown variant: " variant) {:variant variant})))
    (case variant
      :byzantine       (parse-byzantine-csv path)
      :kjtr            (parse-kjtr-txt path)
      :kjtr-morphology (parse-kjtr-tsv path)
      :sblgnt          (parse-sblgnt path)
      :tischendorf     (parse-tischendorf-unicode path)
      :westcott-hort   (parse-robinson-parsed path)
      :textus-receptus (parse-robinson-parsed path)
      :scrivener       (parse-scrivener-textonly path))))

(defn verses
  "Filter word maps to a specific verse range [from-ch from-vs] to [to-ch to-vs]."
  [words from-ch from-vs to-ch to-vs]
  (filter (fn [{:keys [chapter verse]}]
            (let [pos  (+ (* chapter 1000) verse)
                  from (+ (* from-ch 1000) from-vs)
                  to   (+ (* to-ch 1000) to-vs)]
              (<= from pos to)))
          words))

(defn verse-text
  "Reconstruct the text of a verse range from word maps."
  [words]
  (str/join " " (map :text words)))

(comment
  ;; Load Matthew from each variant
  (def byz (load-matthew :byzantine))
  (def sbl (load-matthew :sblgnt))
  (def tisch (load-matthew :tischendorf))
  (def wh (load-matthew :westcott-hort))
  (def tr (load-matthew :textus-receptus))

  ;; Get Matthew 1:1 from each
  (verse-text (verses byz 1 1 1 1))
  (verse-text (verses sbl 1 1 1 1))

  ;; Get the genealogy (Matt 1:1-17) from each
  (def gen-byz (verses byz 1 1 1 17))
  (count gen-byz) ; word count
  )
