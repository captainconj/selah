(ns selah.aramaic.parse
  "Parser for Peshitta Syriac NT text (ETCBC/syrnt plain format)."
  (:require [clojure.string :as str]
            [selah.aramaic.normalize :as an]))

;; ── ETCBC plain text format ───────────────────────────────────────
;;
;; Format:
;;   Matthew (Matt)
;;   <blank>
;;   Matt 1
;;   <blank>
;;   1 ܟܬܒܐ ܕܝܠܝܕܘܬܗ ܕܝܫܘܥ ܡܫܝܚܐ ...
;;   2 ܐܒܪܗܡ ܐܘܠܕ ܠܐܝܣܚܩ ...
;;   ...
;;   Matt 2
;;   ...

(defn parse-peshitta
  "Parse a Peshitta plain text file. Returns seq of word maps:
   {:book \"Matthew\" :chapter N :verse N :word-num N :text \"ܟܬܒܐ\"}"
  [path]
  (let [lines (str/split-lines (slurp path))]
    (loop [lines lines, chapter 0, result []]
      (if (empty? lines)
        result
        (let [line (first lines)]
          (cond
            ;; Chapter header: "Matt 1" or "Matt 28"
            (re-find #"^[A-Za-z]+ (\d+)$" line)
            (recur (rest lines)
                   (parse-long (second (re-find #"^[A-Za-z]+ (\d+)$" line)))
                   result)

            ;; Verse line: "1 ܟܬܒܐ ..."
            (re-matches #"(\d+)\s+(.*)" line)
            (let [[_ vs-str text] (re-matches #"(\d+)\s+(.*)" line)
                  verse (parse-long vs-str)
                  words (remove str/blank? (str/split (str/trim text) #"\s+"))
                  entries (vec (map-indexed
                                 (fn [i w]
                                   {:book     "Matthew"
                                    :chapter  chapter
                                    :verse    verse
                                    :word-num (inc i)
                                    :text     w})
                                 words))]
              (recur (rest lines) chapter (into result entries)))

            :else
            (recur (rest lines) chapter result)))))))

(def peshitta-path
  "/home/scott/Projects/peshitta-syrnt/plain/0.1/Matthew.txt")

(defn load-matthew
  "Load Peshitta Matthew. Returns seq of word maps."
  ([] (load-matthew peshitta-path))
  ([path] (parse-peshitta path)))

(defn verses
  "Filter word maps to a specific verse range."
  [words from-ch from-vs to-ch to-vs]
  (filter (fn [{:keys [chapter verse]}]
            (let [pos  (+ (* chapter 1000) verse)
                  from (+ (* from-ch 1000) from-vs)
                  to   (+ (* to-ch 1000) to-vs)]
              (<= from pos to)))
          words))

(defn verse-text
  "Reconstruct text from word maps."
  [words]
  (str/join " " (map :text words)))

(comment
  (def matt (load-matthew))
  (count matt)

  ;; Matt 1:1
  (verse-text (verses matt 1 1 1 1))
  ;=> "ܟܬܒܐ ܕܝܠܝܕܘܬܗ ܕܝܫܘܥ ܡܫܝܚܐ ܒܪܗ ܕܕܘܝܕ ܒܪܗ ܕܐܒܪܗܡ"

  ;; Matt 1:1-17 (genealogy)
  (count (verses matt 1 1 1 17))
  )
