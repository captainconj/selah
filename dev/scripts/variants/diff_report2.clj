(require '[selah.text.sefaria :as s]
         '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[clojure.string :as str])

(defn verse-letters-mam [verse-str]
  (-> verse-str norm/strip-html norm/strip-section-markers norm/letter-stream))

(defn oshb-verse-letters [word-maps]
  (norm/letter-stream (str/join " " (map :text word-maps))))

(defn edit-ops
  "Compute minimal edit operations between two letter vectors using DP.
   Returns seq of {:type :match|:sub|:ins|:del, :a-letter :b-letter, :a-pos :b-pos}"
  [a b]
  (let [m (count a) n (count b)
        ;; Build DP matrix
        dp (make-array Long/TYPE (inc m) (inc n))]
    ;; Initialize
    (doseq [i (range (inc m))] (aset dp i 0 (long i)))
    (doseq [j (range (inc n))] (aset dp 0 j (long j)))
    ;; Fill
    (doseq [i (range 1 (inc m))
            j (range 1 (inc n))]
      (let [cost (if (= (nth a (dec i)) (nth b (dec j))) 0 1)]
        (aset dp i j (long (min (inc (aget dp (dec i) j))      ; delete from a
                                (inc (aget dp i (dec j)))      ; insert into a
                                (+ cost (aget dp (dec i) (dec j))))))))  ; match/sub
    ;; Backtrace
    (loop [i m j n ops []]
      (cond
        (and (zero? i) (zero? j)) (vec (reverse ops))
        (zero? i) (recur 0 (dec j)
                         (conj ops {:type :ins :b-letter (nth b (dec j)) :b-pos (dec j)}))
        (zero? j) (recur (dec i) 0
                         (conj ops {:type :del :a-letter (nth a (dec i)) :a-pos (dec i)}))
        :else
        (let [diag (aget dp (dec i) (dec j))
              up   (aget dp (dec i) j)
              left (aget dp i (dec j))
              curr (aget dp i j)]
          (cond
            ;; Match or substitution
            (and (= curr (+ diag (if (= (nth a (dec i)) (nth b (dec j))) 0 1))))
            (recur (dec i) (dec j)
                   (if (= (nth a (dec i)) (nth b (dec j)))
                     ops  ; match — skip
                     (conj ops {:type :sub
                                :a-letter (nth a (dec i)) :b-letter (nth b (dec j))
                                :a-pos (dec i) :b-pos (dec j)})))
            ;; Deletion (from a)
            (= curr (inc up))
            (recur (dec i) j
                   (conj ops {:type :del :a-letter (nth a (dec i)) :a-pos (dec i)}))
            ;; Insertion (from b)
            :else
            (recur i (dec j)
                   (conj ops {:type :ins :b-letter (nth b (dec j)) :b-pos (dec j)}))))))))

(defn context-str [letters pos radius]
  (when (and letters (< pos (count letters)))
    (let [lo (max 0 (- pos radius))
          hi (min (count letters) (+ pos radius 1))]
      (str (apply str (subvec letters lo pos))
           "**" (nth letters pos) "**"
           (apply str (subvec letters (inc pos) hi))))))

(let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
      book-ch {"Genesis" 50 "Exodus" 40 "Leviticus" 27
               "Numbers" 36 "Deuteronomy" 34}
      sb (StringBuilder.)
      totals (atom {:ins 0 :del 0 :sub 0 :verses 0})]

  (.append sb "# Torah Variant Diff Report: MAM vs WLC\n\n")
  (.append sb "**MAM** = Miqra according to the Masorah (Sefaria, footnotes stripped, kethiv stripped, qere kept)\n")
  (.append sb "**WLC** = Westminster Leningrad Codex (OSHB 4.20, kethiv kept, qere skipped)\n\n")
  (.append sb "- **DEL** = letter in WLC but not MAM\n")
  (.append sb "- **INS** = letter in MAM but not WLC\n")
  (.append sb "- **SUB** = different letter at same position\n\n")
  (.append sb (format "Generated: %s\n\n---\n\n" (str (java.time.LocalDateTime/now))))

  (doseq [book books]
    (let [chapters (get book-ch book)
          oshb-words (oshb/book-words book)
          oshb-by-cv (group-by (juxt :chapter :verse) oshb-words)
          book-variants (atom [])]

      (doseq [ch (range 1 (inc chapters))]
        (let [mam-verses (s/fetch-chapter book ch)]
          (doseq [v (range 1 (inc (count mam-verses)))]
            (let [mam-v (nth mam-verses (dec v) nil)
                  wlc-words (get oshb-by-cv [ch v])
                  mam-l (when mam-v (verse-letters-mam mam-v))
                  wlc-l (when wlc-words (oshb-verse-letters wlc-words))]
              (when (and mam-l wlc-l (not= mam-l wlc-l))
                (let [ops (edit-ops wlc-l mam-l)  ; a=WLC, b=MAM
                      edits (remove #(= :match (:type %)) ops)]
                  (swap! totals update :verses inc)
                  (doseq [op edits]
                    (swap! book-variants conj
                           (assoc op :ref (str book " " ch ":" v)
                                     :wlc-letters wlc-l
                                     :mam-letters mam-l)))))))))

      (let [variants @book-variants
            ins (count (filter #(= :ins (:type %)) variants))
            del (count (filter #(= :del (:type %)) variants))
            subs (count (filter #(= :sub (:type %)) variants))]
        (swap! totals update :ins + ins)
        (swap! totals update :del + del)
        (swap! totals update :sub + subs)
        (.append sb (format "### %s (%d edits in %d verses)\n\n"
                            book (+ ins del subs)
                            (count (distinct (map :ref variants)))))
        (when (seq variants)
          (.append sb "| Reference | Type | Detail | WLC Context | MAM Context |\n")
          (.append sb "|-----------|------|--------|-------------|-------------|\n")
          (doseq [v variants]
            (let [type-str (name (:type v))
                  detail (case (:type v)
                           :del (str "WLC has " (:a-letter v))
                           :ins (str "MAM has " (:b-letter v))
                           :sub (str (:a-letter v) "→" (:b-letter v)))
                  wlc-ctx (when (:a-pos v) (context-str (:wlc-letters v) (:a-pos v) 4))
                  mam-ctx (when (:b-pos v) (context-str (:mam-letters v) (:b-pos v) 4))]
              (.append sb (format "| %s | %s | %s | %s | %s |\n"
                                  (:ref v) type-str detail
                                  (or wlc-ctx "-") (or mam-ctx "-"))))))
        (.append sb "\n"))))

  (let [{:keys [ins del sub verses]} @totals]
    (.append sb (format "\n---\n\n## Summary\n\n- **%d** total edits in **%d** verses\n- **%d** insertions (MAM has extra letter)\n- **%d** deletions (WLC has extra letter)\n- **%d** substitutions\n- Net letter difference: %d (WLC longer)\n"
                        (+ ins del sub) verses ins del sub (- del ins)))
    (println (format "Total: %d edits (%d ins, %d del, %d sub) in %d verses"
                     (+ ins del sub) ins del sub verses)))

  (spit "docs/variant-diff-report.md" (.toString sb))
  (println "Saved to docs/variant-diff-report.md"))
