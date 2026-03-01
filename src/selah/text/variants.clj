(ns selah.text.variants
  "Cross-variant text comparison.
   Aligns letter streams from different manuscript traditions using edit distance."
  (:require [selah.text.leningrad :as leningrad]
            [selah.text.mam :as mam]
            [selah.text.sefaria :as sefaria]
            [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def sources
  {:leningrad {:name "Westminster Leningrad Codex"
               :date "~1008 CE"
               :book-letters leningrad/book-letters
               :torah-letters leningrad/torah-letters}
   :mam       {:name "Miqra according to the Masorah"
               :date "Modern reconstruction"
               :book-letters mam/book-letters
               :torah-letters mam/torah-letters}})

(def book-chapters
  {"Genesis" 50 "Exodus" 40 "Leviticus" 27 "Numbers" 36 "Deuteronomy" 34})

;;; ---- Edit distance with backtrace ----

(defn edit-ops
  "Compute minimal edits between vectors a and b using Needleman-Wunsch.
   Returns seq of {:type :ins|:del|:sub, :letter or :from/:to, :a-pos :b-pos}
   where a=reference, b=target. Matches are omitted."
  [a b]
  (let [m (count a) n (count b)
        dp (make-array Long/TYPE (inc m) (inc n))]
    (doseq [i (range (inc m))] (aset dp i 0 (long i)))
    (doseq [j (range (inc n))] (aset dp 0 j (long j)))
    (doseq [i (range 1 (inc m))
            j (range 1 (inc n))]
      (let [cost (if (= (nth a (dec i)) (nth b (dec j))) 0 1)]
        (aset dp i j (long (min (inc (aget dp (dec i) j))
                                (inc (aget dp i (dec j)))
                                (+ cost (aget dp (dec i) (dec j))))))))
    ;; Backtrace — prefer match/sub over ins/del
    (loop [i m j n ops (transient [])]
      (cond
        (and (zero? i) (zero? j))
        (persistent! ops)

        (zero? i)
        (recur 0 (dec j)
               (conj! ops {:type :ins :letter (nth b (dec j)) :b-pos (dec j)}))

        (zero? j)
        (recur (dec i) 0
               (conj! ops {:type :del :letter (nth a (dec i)) :a-pos (dec i)}))

        :else
        (let [ai (nth a (dec i))
              bj (nth b (dec j))
              diag (aget dp (dec i) (dec j))
              up   (aget dp (dec i) j)
              left (aget dp i (dec j))
              curr (aget dp i j)
              match? (= ai bj)]
          (cond
            ;; Prefer diagonal (match or sub)
            (= curr (+ (if match? 0 1) diag))
            (recur (dec i) (dec j)
                   (if match?
                     ops
                     (conj! ops {:type :sub :from ai :to bj
                                 :a-pos (dec i) :b-pos (dec j)})))
            ;; Delete from a (a has extra)
            (= curr (inc up))
            (recur (dec i) j
                   (conj! ops {:type :del :letter ai :a-pos (dec i)}))
            ;; Insert from b (b has extra)
            :else
            (recur i (dec j)
                   (conj! ops {:type :ins :letter bj :b-pos (dec j)}))))))))

;;; ---- Verse-level access ----

(defn mam-verse-letters
  "Get normalized letter vector for a MAM verse string."
  [verse-str]
  (-> verse-str norm/strip-html norm/strip-section-markers norm/letter-stream))

(defn wlc-verse-letters
  "Get normalized letter vector for OSHB word maps from one verse."
  [word-maps]
  (norm/letter-stream (str/join " " (map :text word-maps))))

;;; ---- Diff engine ----

(defn diff-verse
  "Compare a single verse between MAM and WLC. Returns edit ops or nil if identical."
  [mam-letters wlc-letters]
  (when (and (seq mam-letters) (seq wlc-letters) (not= mam-letters wlc-letters))
    (edit-ops wlc-letters mam-letters)))

(defn- diff-chapter-by-verse
  "Diff a chapter verse-by-verse. Returns seq of diff maps."
  [book ch mam-verses oshb-by-cv]
  (for [v (range 1 (inc (count mam-verses)))
        :let [mam-l (mam-verse-letters (nth mam-verses (dec v)))
              wlc-words (get oshb-by-cv [ch v])
              wlc-l (when wlc-words (wlc-verse-letters wlc-words))
              ops (when wlc-l (diff-verse mam-l wlc-l))]
        :when ops]
    {:ref (str book " " ch ":" v)
     :edits ops
     :mam-len (count mam-l)
     :wlc-len (count wlc-l)}))

(defn- diff-chapter-as-block
  "Diff a chapter as a single block when verse counts disagree.
   Concatenates all verse letters and runs edit-distance on the whole chapter."
  [book ch mam-verses oshb-by-cv]
  (let [mam-l (vec (mapcat mam-verse-letters mam-verses))
        wlc-max-v (apply max (map second (filter #(= ch (first %)) (keys oshb-by-cv))))
        wlc-l (vec (mapcat #(wlc-verse-letters (get oshb-by-cv [ch %]))
                           (range 1 (inc wlc-max-v))))
        ops (diff-verse mam-l wlc-l)]
    (when ops
      [{:ref (str book " " ch ":* (block)")
        :edits ops
        :mam-len (count mam-l)
        :wlc-len (count wlc-l)}])))

(defn diff-book
  "Compare all verses in a book. Returns seq of {:ref :edits :mam-len :wlc-len}.
   Falls back to chapter-level comparison when verse counts disagree."
  [book]
  (let [chapters (get book-chapters book)
        oshb-words (oshb/book-words book)
        oshb-by-cv (group-by (juxt :chapter :verse) oshb-words)]
    (mapcat
     (fn [ch]
       (let [mam-verses (sefaria/fetch-chapter book ch)
             wlc-verse-count (count (filter #(= ch (first %)) (keys oshb-by-cv)))]
         (if (= (count mam-verses) wlc-verse-count)
           (diff-chapter-by-verse book ch mam-verses oshb-by-cv)
           (diff-chapter-as-block book ch mam-verses oshb-by-cv))))
     (range 1 (inc chapters)))))

(defn diff-torah
  "Compare all five books. Returns seq of per-verse diff maps."
  []
  (mapcat diff-book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))

;;; ---- Reporting ----

(defn- context-str [letters pos]
  (when (and letters (>= pos 0) (< pos (count letters)))
    (let [lo (max 0 (- pos 4))
          hi (min (count letters) (+ pos 5))]
      (str (apply str (subvec letters lo pos))
           "[" (nth letters pos) "]"
           (apply str (subvec letters (inc pos) hi))))))

(defn summarize
  "Summarize diff results. Returns {:total :ins :del :sub :verses :by-book}."
  [diffs]
  (let [all-edits (mapcat :edits diffs)
        by-type (frequencies (map :type all-edits))
        by-book (group-by #(first (str/split (:ref %) #" ")) diffs)]
    {:total (count all-edits)
     :ins (get by-type :ins 0)
     :del (get by-type :del 0)
     :sub (get by-type :sub 0)
     :verses (count diffs)
     :by-book (into {} (for [[book verses] by-book]
                         [book (let [edits (mapcat :edits verses)]
                                 {:verses (count verses)
                                  :edits (count edits)
                                  :ins (count (filter #(= :ins (:type %)) edits))
                                  :del (count (filter #(= :del (:type %)) edits))
                                  :sub (count (filter #(= :sub (:type %)) edits))})]))}))

(defn report
  "Generate markdown diff report. Returns string."
  [diffs]
  (let [summary (summarize diffs)
        sb (StringBuilder.)]
    (.append sb "# Torah Variant Diff Report: MAM vs WLC\n\n")
    (.append sb "**MAM** = Miqra according to the Masorah (Sefaria, qere preferred)\n")
    (.append sb "**WLC** = Westminster Leningrad Codex (OSHB 4.20, kethiv preferred)\n\n")
    (.append sb "- **del** = letter in WLC not in MAM (WLC extra)\n")
    (.append sb "- **ins** = letter in MAM not in WLC (MAM extra)\n")
    (.append sb "- **sub** = different letter at aligned position\n\n")

    ;; Summary table
    (.append sb "## Summary\n\n")
    (.append sb (format "| | Edits | DEL | INS | SUB | Verses |\n"))
    (.append sb "|--|-------|-----|-----|-----|--------|\n")
    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (when-let [s (get (:by-book summary) book)]
        (.append sb (format "| %s | %d | %d | %d | %d | %d |\n"
                            book (:edits s) (:del s) (:ins s) (:sub s) (:verses s)))))
    (.append sb (format "| **Total** | **%d** | **%d** | **%d** | **%d** | **%d** |\n\n"
                        (:total summary) (:del summary) (:ins summary)
                        (:sub summary) (:verses summary)))

    ;; Detail per book
    (.append sb "---\n\n## Detail\n\n")
    (let [by-book (group-by #(first (str/split (:ref %) #" ")) diffs)]
      (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
        (when-let [verses (get by-book book)]
          (.append sb (format "### %s\n\n" book))
          (.append sb "| Reference | Type | Detail |\n")
          (.append sb "|-----------|------|--------|\n")
          (doseq [{:keys [ref edits]} (sort-by :ref verses)]
            (doseq [e edits]
              (let [detail (case (:type e)
                             :del (str "WLC+" (:letter e))
                             :ins (str "MAM+" (:letter e))
                             :sub (str (:from e) "→" (:to e)))]
                (.append sb (format "| %s | %s | %s |\n"
                                    ref (name (:type e)) detail)))))
          (.append sb "\n"))))

    (.toString sb)))

(defn save-report!
  "Generate and save the diff report."
  ([] (save-report! "docs/variant-diff-report.md"))
  ([path]
   (let [diffs (diff-torah)
         md (report diffs)]
     (spit path md)
     (summarize diffs))))

;;; ---- Letter counts ----

(defn book-counts
  "Letter counts per book for all sources."
  []
  (for [b ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
    {:book b
     :wlc (count (leningrad/book-letters b))
     :mam (count (mam/book-letters b))
     :diff (- (count (mam/book-letters b))
              (count (leningrad/book-letters b)))}))

(comment
  ;; Quick diff
  (def d (diff-torah))
  (summarize d)

  ;; Save report
  (save-report!)

  ;; Just Genesis
  (def gd (diff-book "Genesis"))
  (summarize gd)
  )
