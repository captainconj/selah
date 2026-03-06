(require '[selah.text.sefaria :as s]
         '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[clojure.string :as str])

(defn verse-letters-mam [verse-str]
  (-> verse-str norm/strip-html norm/strip-section-markers norm/letter-stream))

(defn oshb-verse-letters [word-maps]
  (norm/letter-stream (str/join " " (map :text word-maps))))

(defn describe-diff
  "Compare two letter vectors verse-by-verse. Returns list of diff maps."
  [mam-l wlc-l]
  (let [ml (count mam-l) wl (count wlc-l)
        min-len (min ml wl)
        diffs (atom [])
        ;; Walk both sequences with alignment
        mi (atom 0) wi (atom 0)]
    ;; Simple approach: find positions where they diverge
    (loop [mi 0 wi 0]
      (cond
        (and (>= mi ml) (>= wi wl)) nil  ; done
        (>= mi ml)  ; WLC has extra at end
        (do (swap! diffs conj {:type :wlc-extra :letter (nth wlc-l wi)
                               :wlc-pos wi})
            (recur mi (inc wi)))
        (>= wi wl)  ; MAM has extra at end
        (do (swap! diffs conj {:type :mam-extra :letter (nth mam-l mi)
                               :mam-pos mi})
            (recur (inc mi) wi))
        (= (nth mam-l mi) (nth wlc-l wi))  ; match
        (recur (inc mi) (inc wi))
        ;; Mismatch — determine if insertion in MAM or WLC
        :else
        (let [;; Look ahead: does skipping one MAM letter realign?
              mam-skip (and (< (inc mi) ml)
                           (< wi wl)
                           (= (nth mam-l (inc mi)) (nth wlc-l wi)))
              ;; Does skipping one WLC letter realign?
              wlc-skip (and (< mi ml)
                           (< (inc wi) wl)
                           (= (nth mam-l mi) (nth wlc-l (inc wi))))]
          (cond
            mam-skip
            (do (swap! diffs conj {:type :mam-extra :letter (nth mam-l mi)
                                   :mam-pos mi :wlc-pos wi})
                (recur (inc mi) wi))
            wlc-skip
            (do (swap! diffs conj {:type :wlc-extra :letter (nth wlc-l wi)
                                   :mam-pos mi :wlc-pos wi})
                (recur mi (inc wi)))
            :else  ; substitution
            (do (swap! diffs conj {:type :sub
                                   :mam-letter (nth mam-l mi)
                                   :wlc-letter (nth wlc-l wi)
                                   :mam-pos mi :wlc-pos wi})
                (recur (inc mi) (inc wi)))))))
    @diffs))

(defn context-str [letters pos radius]
  (let [lo (max 0 (- pos radius))
        hi (min (count letters) (+ pos radius 1))]
    (str (apply str (subvec letters lo pos))
         "**" (nth letters pos) "**"
         (apply str (subvec letters (inc pos) hi)))))

(let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
      book-ch {"Genesis" 50 "Exodus" 40 "Leviticus" 27
               "Numbers" 36 "Deuteronomy" 34}
      sb (StringBuilder.)
      totals (atom {:mam+ 0 :wlc+ 0 :sub 0 :verses 0})]

  (.append sb "# Torah Variant Diff Report: MAM vs WLC\n\n")
  (.append sb "**MAM** = Miqra according to the Masorah (Sefaria, footnotes+markers stripped)\n")
  (.append sb "**WLC** = Westminster Leningrad Codex (OSHB 4.20, kethiv preferred)\n\n")
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
                (let [diffs (describe-diff mam-l wlc-l)]
                  (swap! totals update :verses inc)
                  (doseq [d diffs]
                    (swap! book-variants conj
                           (assoc d :ref (str book " " ch ":" v)
                                    :mam-len (count mam-l)
                                    :wlc-len (count wlc-l)
                                    :mam-letters mam-l
                                    :wlc-letters wlc-l)))))))))

      (let [variants @book-variants
            mam+ (count (filter #(= :mam-extra (:type %)) variants))
            wlc+ (count (filter #(= :wlc-extra (:type %)) variants))
            subs (count (filter #(= :sub (:type %)) variants))]
        (swap! totals update :mam+ + mam+)
        (swap! totals update :wlc+ + wlc+)
        (swap! totals update :sub + subs)
        (.append sb (format "### %s (%d variants in %d verses)\n\n"
                            book (count variants)
                            (count (distinct (map :ref variants)))))
        (when (seq variants)
          (.append sb "| Reference | Type | Letter | MAM Context | WLC Context |\n")
          (.append sb "|-----------|------|--------|-------------|-------------|\n")
          (doseq [v variants]
            (let [type-str (case (:type v)
                             :mam-extra "MAM+"
                             :wlc-extra "WLC+"
                             :sub "SUB")
                  letter-str (case (:type v)
                               :mam-extra (str (:letter v))
                               :wlc-extra (str (:letter v))
                               :sub (str (:mam-letter v) "→" (:wlc-letter v)))
                  mam-ctx (when-let [p (:mam-pos v)]
                            (when (< p (count (:mam-letters v)))
                              (context-str (:mam-letters v) p 4)))
                  wlc-ctx (when-let [p (:wlc-pos v)]
                            (when (< p (count (:wlc-letters v)))
                              (context-str (:wlc-letters v) p 4)))]
              (.append sb (format "| %s | %s | %s | %s | %s |\n"
                                  (:ref v) type-str letter-str
                                  (or mam-ctx "-") (or wlc-ctx "-"))))))
        (.append sb "\n"))))

  ;; Print summary
  (let [{:keys [mam+ wlc+ sub verses]} @totals]
    (.append sb (format "\n---\n\n## Summary\n\n- **%d** total variants in **%d** verses\n- **%d** MAM extra letters\n- **%d** WLC extra letters\n- **%d** substitutions\n- Net: MAM has %d fewer letters than WLC\n"
                        (+ mam+ wlc+ sub) verses mam+ wlc+ sub (- wlc+ mam+)))
    (println (format "Total: %d variants (%d MAM+, %d WLC+, %d sub) in %d verses"
                     (+ mam+ wlc+ sub) mam+ wlc+ sub verses)))

  (spit "docs/variant-diff-report.md" (.toString sb))
  (println "Saved to docs/variant-diff-report.md"))
