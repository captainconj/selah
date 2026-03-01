(ns scripts.variant-diff-report
  "Generate a complete verse-by-verse diff report between MAM and WLC Torah texts.
   Run: clojure -M:dev -e '(load-file \"scripts/variant_diff_report.clj\") (scripts.variant-diff-report/-main)'"
  (:require [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [javax.xml.parsers SAXParserFactory]
           [org.xml.sax.helpers DefaultHandler]
           [org.xml.sax Attributes]))

;; ---- Config ----

(def books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(def book-chapters
  {"Genesis" 50, "Exodus" 40, "Leviticus" 27, "Numbers" 36, "Deuteronomy" 34})

(def oshb-dir "data/sources/oshb/wlc")
(def oshb-files
  {"Genesis" "Gen.xml", "Exodus" "Exod.xml", "Leviticus" "Lev.xml",
   "Numbers" "Num.xml", "Deuteronomy" "Deut.xml"})

;; ---- Sefaria MAM text cleanup ----
;; Sefaria encodes three types of annotations we must handle:
;; 1. Footnotes: <sup class="footnote-marker">*</sup><i class="footnote">...</i>
;;    These are editorial notes — remove entirely.
;; 2. Kethiv/qere (non-trivial): <span class="mam-kq">
;;      <span class="mam-kq-k">(kethiv)</span> <span class="mam-kq-q">[qere]</span>
;;    </span>
;;    Remove kethiv, keep qere (the reading tradition).
;; 3. Kethiv/qere (trivial): <span class="mam-kq-trivial">word</span>
;;    Same consonants, different vowels — strip_html handles these fine.

(defn strip-sefaria-annotations
  "Remove footnotes and resolve kethiv/qere to qere-only."
  [^String s]
  (if s
    (-> s
        ;; Remove footnote content elements
        (.replaceAll "(?s)<i[^>]*class=\"footnote\"[^>]*>.*?</i>" "")
        (.replaceAll "(?s)<sup[^>]*class=\"footnote-marker\"[^>]*>.*?</sup>" "")
        (.replaceAll "\\*" "")
        ;; Remove kethiv spans (keep qere)
        (.replaceAll "(?s)<span[^>]*class=\"mam-kq-k\"[^>]*>.*?</span>" "")
        ;; Strip the qere brackets [] but keep the content
        (.replaceAll "\\[" "")
        (.replaceAll "\\]" ""))
    ""))

(defn clean-mam-verse
  "Full cleaning pipeline for a Sefaria MAM verse string -> letter vector."
  [verse-str]
  (-> verse-str
      strip-sefaria-annotations
      norm/strip-html
      norm/strip-section-markers
      norm/letter-stream))

;; ---- OSHB parser with kethiv/qere awareness ----
;; The stock selah.text.oshb parser captures BOTH kethiv and qere <w> elements.
;; For comparison with MAM (which uses qere readings), we want qere when available.
;; Strategy: when we encounter a kethiv <w>, skip it; when we see the qere <w>
;; inside <note><rdg type="x-qere">, use that instead.

(defn parse-oshb-qere-preferred
  "Parse OSHB XML preferring qere readings over kethiv.
   - Normal <w> (not kethiv): captured normally.
   - <w type='x-ketiv'>: skipped (replaced by qere).
   - <w> inside <rdg type='x-qere'>: captured (the qere reading).
   Returns seq of {:chapter :verse :text}."
  [file-path]
  (let [words (atom [])
        current-chapter (atom nil)
        current-verse (atom nil)
        in-note? (atom false)
        in-qere? (atom false)
        skip-kethiv? (atom false)
        in-word? (atom false)
        word-buf (StringBuilder.)
        handler (proxy [DefaultHandler] []
                  (startElement [uri local qname ^Attributes attrs]
                    (cond
                      (= qname "note")
                      (reset! in-note? true)

                      (= qname "rdg")
                      (when (and @in-note?
                                 (= "x-qere" (.getValue attrs "type")))
                        (reset! in-qere? true))

                      (= qname "chapter")
                      (let [osis-id (.getValue attrs "osisID")]
                        (reset! current-chapter
                                (Integer/parseInt (last (str/split osis-id #"\.")))))

                      (= qname "verse")
                      (let [osis-id (.getValue attrs "osisID")
                            parts (str/split osis-id #"\.")]
                        (reset! current-verse (Integer/parseInt (last parts))))

                      (= qname "w")
                      (let [w-type (.getValue attrs "type")]
                        (cond
                          ;; Kethiv word — skip it
                          (= w-type "x-ketiv")
                          (reset! skip-kethiv? true)

                          ;; Qere word inside note — capture it
                          @in-qere?
                          (do (reset! in-word? true)
                              (.setLength word-buf 0))

                          ;; Normal word outside note — capture it
                          (not @in-note?)
                          (do (reset! in-word? true)
                              (.setLength word-buf 0))))))

                  (characters [ch start length]
                    (when @in-word?
                      (.append word-buf ch start length)))

                  (endElement [uri local qname]
                    (cond
                      (= qname "note")
                      (do (reset! in-note? false)
                          (reset! in-qere? false))

                      (= qname "rdg")
                      (reset! in-qere? false)

                      (= qname "w")
                      (cond
                        @skip-kethiv?
                        (reset! skip-kethiv? false)

                        @in-word?
                        (do (reset! in-word? false)
                            (swap! words conj
                                   {:chapter @current-chapter
                                    :verse   @current-verse
                                    :text    (.toString word-buf)}))))))]
    (let [factory (SAXParserFactory/newInstance)]
      (.setNamespaceAware factory false)
      (.parse (.newSAXParser factory) (io/file file-path) handler))
    @words))

(defn oshb-book-words
  "Get OSHB words for a book, qere-preferred (matching MAM qere tradition)."
  [book]
  (parse-oshb-qere-preferred (str oshb-dir "/" (get oshb-files book))))

;; ---- Verse-level text extraction ----

(defn mam-chapter-verses
  "MAM verses for one chapter -> vector of letter-vectors (0-indexed)."
  [book ch]
  (mapv clean-mam-verse (sefaria/fetch-chapter book ch)))

(defn wlc-chapter-verses
  "WLC verses for one chapter from pre-fetched words -> vector of letter-vectors."
  [ch words]
  (let [ch-words (filter #(= ch (:chapter %)) words)
        by-verse (group-by :verse ch-words)
        max-verse (if (seq by-verse) (apply max (keys by-verse)) 0)]
    (mapv (fn [v]
            (norm/letter-stream (str/join "" (map :text (get by-verse v [])))))
          (range 1 (inc max-verse)))))

;; ---- LCS-based diff ----

(defn lcs-table [a b]
  (let [m (count a), n (count b)
        t (make-array Long/TYPE (inc m) (inc n))]
    (doseq [i (range 1 (inc m)), j (range 1 (inc n))]
      (aset-long t i j
                 (if (= (nth a (dec i)) (nth b (dec j)))
                   (inc (aget t (dec i) (dec j)))
                   (max (aget t (dec i) j) (aget t i (dec j))))))
    t))

(defn backtrack-diff [t a b]
  (loop [i (count a), j (count b), ops '()]
    (cond
      (and (zero? i) (zero? j)) ops
      (zero? i) (recur i (dec j) (cons [:insert (nth b (dec j))] ops))
      (zero? j) (recur (dec i) j (cons [:delete (nth a (dec i))] ops))
      (= (nth a (dec i)) (nth b (dec j)))
      (recur (dec i) (dec j) (cons [:match (nth a (dec i))] ops))
      (>= (aget ^longs t (dec i) j) (aget ^longs t i (dec j)))
      (recur (dec i) j (cons [:delete (nth a (dec i))] ops))
      :else
      (recur i (dec j) (cons [:insert (nth b (dec j))] ops)))))

(defn diff-letters [mam wlc]
  (when (not= mam wlc)
    (backtrack-diff (lcs-table mam wlc) mam wlc)))

;; ---- Change extraction & classification ----

(defn extract-changes [ops]
  (loop [rem ops, pa 0, pb 0, out []]
    (if (empty? rem) out
      (let [[op letter] (first rem), r (rest rem)]
        (case op
          :match (recur r (inc pa) (inc pb) out)
          :delete
          (if (and (seq r) (= :insert (ffirst r)))
            (recur (rest r) (inc pa) (inc pb)
                   (conj out {:type :substitute
                              :mam-letter letter :wlc-letter (second (first r))
                              :pos-mam pa :pos-wlc pb}))
            (recur r (inc pa) pb
                   (conj out {:type :mam-extra :letter letter
                              :pos-mam pa :pos-wlc pb})))
          :insert
          (recur r pa (inc pb)
                 (conj out {:type :wlc-extra :letter letter
                            :pos-mam pa :pos-wlc pb})))))))

(def mater-lectionis #{\ו \י \א \ה})

(defn classify [{:keys [type letter mam-letter wlc-letter] :as c}]
  (assoc c :classification
         (case type
           :mam-extra (if (mater-lectionis letter) "plene in MAM" "extra consonant in MAM")
           :wlc-extra (if (mater-lectionis letter) "plene in WLC" "extra consonant in WLC")
           :substitute (str "substitution (" mam-letter " -> " wlc-letter ")"))))

;; ---- Context ----

(defn ctx [letters pos radius]
  (let [n (count letters)
        s (max 0 (- pos radius))
        e (min n (+ pos radius 1))]
    (str (apply str (subvec letters s (min pos n)))
         "[" (if (< pos n) (nth letters pos) "") "]"
         (apply str (subvec letters (min n (inc pos)) e)))))

;; ---- Diff engine ----

(defn diff-verse-pair [book ch v mam-v wlc-v]
  (when-let [ops (diff-letters mam-v wlc-v)]
    (mapv #(-> % classify
               (assoc :book book :chapter ch :verse v
                      :mam-context (ctx mam-v (:pos-mam %) 5)
                      :wlc-context (ctx wlc-v (:pos-wlc %) 5)))
          (extract-changes ops))))

(defn diff-chapter-block [book ch mam-vs wlc-vs]
  (let [ma (vec (mapcat identity mam-vs))
        wa (vec (mapcat identity wlc-vs))]
    (when-let [ops (diff-letters ma wa)]
      (mapv #(-> % classify
                 (assoc :book book :chapter ch :verse nil
                        :mam-context (ctx ma (:pos-mam %) 5)
                        :wlc-context (ctx wa (:pos-wlc %) 5)))
            (extract-changes ops)))))

(defn diff-book [book]
  (println (str "  Processing " book "...")) (flush)
  (let [words (oshb-book-words book)
        chs (get book-chapters book)]
    (vec
     (mapcat
      (fn [ch]
        (let [mam-vs (mam-chapter-verses book ch)
              wlc-vs (wlc-chapter-verses ch words)]
          (if (= (count mam-vs) (count wlc-vs))
            (mapcat #(diff-verse-pair book ch (inc %)
                                      (nth mam-vs %) (nth wlc-vs %))
                    (range (count mam-vs)))
            (do (println (str "    Ch " ch ": verse count mismatch (MAM="
                              (count mam-vs) " WLC=" (count wlc-vs)
                              "), diffing as chapter block"))
                (flush)
                (diff-chapter-block book ch mam-vs wlc-vs)))))
      (range 1 (inc chs))))))

;; ---- Report formatting ----

(defn type-label [v] (case (:type v) :mam-extra "MAM+" :wlc-extra "WLC+" :substitute "SUB"))

(defn verse-ref [{:keys [book chapter verse]}]
  (if verse (str book " " chapter ":" verse) (str book " " chapter ":*")))

(defn fmt-row [v]
  (let [change (case (:type v)
                 :mam-extra (str "MAM has extra **" (:letter v) "**")
                 :wlc-extra (str "WLC has extra **" (:letter v) "**")
                 :substitute (str "**" (:mam-letter v) "** -> **" (:wlc-letter v) "**"))]
    (str "| " (verse-ref v) " | " (type-label v) " | " change
         " | " (:mam-context v) " | " (:wlc-context v)
         " | " (:classification v) " |")))

(defn book-row [book vs]
  (let [m+ (count (filter #(= :mam-extra (:type %)) vs))
        w+ (count (filter #(= :wlc-extra (:type %)) vs))
        su (count (filter #(= :substitute (:type %)) vs))
        net (- m+ w+)]
    (str "| " book " | " (count vs) " | " m+ " | " w+ " | " su " | "
         (if (neg? net) (str net) (str "+" net)) " |")))

(defn generate-report [all]
  (let [by-book (group-by :book all)
        m+ (count (filter #(= :mam-extra (:type %)) all))
        w+ (count (filter #(= :wlc-extra (:type %)) all))
        su (count (filter #(= :substitute (:type %)) all))
        net (- m+ w+)
        vcount (count (distinct (map verse-ref all)))]
    (str
     "# Torah Variant Diff Report: MAM vs WLC\n\n"
     "**MAM** = Miqra according to the Masorah (Sefaria, footnotes stripped)  \n"
     "**WLC** = Westminster Leningrad Codex (OSHB 4.20, qere preferred over kethiv)  \n\n"
     "Generated: " (java.time.LocalDateTime/now) "\n\n"

     "> **Methodology notes:**\n"
     "> - Sefaria inline footnotes (variant reading annotations) are stripped before comparison.\n"
     "> - OSHB kethiv/qere: the qere (reading) form is preferred, matching MAM's tradition.\n"
     ">   Kethiv forms are skipped when a qere alternative exists.\n"
     "> - Chapters with different verse numbering between traditions (Exodus 20,\n"
     ">   Numbers 25, Deuteronomy 5) are compared as chapter blocks.\n"
     "> - Letters are normalized to consonants only (U+05D0-U+05EA), final forms preserved.\n\n"

     "---\n\n"
     "## Summary\n\n"
     "| Book | Total Variants | MAM+ | WLC+ | Substitutions | Net (MAM-WLC) |\n"
     "|------|---------------|------|------|---------------|---------------|\n"
     (str/join "\n" (for [b books] (book-row b (get by-book b []))))
     "\n"
     (let [ns (if (neg? net) (str net) (str "+" net))]
       (str "| **TOTAL** | **" (count all) "** | **" m+ "** | **" w+
            "** | **" su "** | **" ns "** |"))
     "\n\n"
     vcount " verses contain at least one variant.\n\n"

     "**MAM+** = MAM has an extra letter (typically plene/mater lectionis)  \n"
     "**WLC+** = WLC has an extra letter  \n"
     "**SUB** = Different letter at same position  \n"
     "**Net** = MAM extras minus WLC extras (negative = WLC is longer)\n\n"

     "### Classification Breakdown\n\n"
     "| Classification | Count |\n"
     "|---------------|-------|\n"
     (str/join "\n" (for [[c n] (sort-by (comp - val) (frequencies (map :classification all)))]
                      (str "| " c " | " n " |")))
     "\n"

     "\n---\n\n"
     "## Complete Variant Listing\n\n"
     "Context shows ~5 surrounding letters with the variant position in **[brackets]**.  \n"
     "Chapters with mismatched verse numbering show `:*` for the verse number.\n\n"
     (str/join "\n\n"
               (for [b books
                     :let [bv (get by-book b [])]
                     :when (seq bv)]
                 (str "### " b " (" (count bv) " variants)\n\n"
                      "| Reference | Type | Change | MAM Context | WLC Context | Classification |\n"
                      "|-----------|------|--------|-------------|-------------|----------------|\n"
                      (str/join "\n" (map fmt-row bv)))))
     "\n\n---\n\n"
     "*Report generated by `scripts/variant_diff_report.clj`*\n")))

(defn -main [& _]
  (println "Torah Variant Diff: MAM vs WLC")
  (println "==============================")
  (println "(Sefaria footnotes stripped, OSHB qere preferred)")
  (println)
  (let [all (vec (mapcat diff-book books))
        m+ (count (filter #(= :mam-extra (:type %)) all))
        w+ (count (filter #(= :wlc-extra (:type %)) all))
        su (count (filter #(= :substitute (:type %)) all))
        net (- m+ w+)]
    (println)
    (println (str "Total variants found: " (count all)))
    (println (str "  MAM+:           " m+))
    (println (str "  WLC+:           " w+))
    (println (str "  Substitutions:  " su))
    (println (str "  Net difference: " net
                  (if (neg? net) " (WLC longer)" " (MAM longer)")))
    (.mkdirs (io/file "docs"))
    (spit "docs/variant-diff-report.md" (generate-report all))
    (println)
    (println "Report written to docs/variant-diff-report.md")
    (println (str "Report size: " (.length (io/file "docs/variant-diff-report.md")) " bytes"))))
