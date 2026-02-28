(ns experiments.007-entropy
  "H15: Shannon entropy per chapter — the information landscape.
   Which chapters carry the most information?
   Run: clojure -M:dev -m experiments.007-entropy"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(defn shannon-entropy
  "Shannon entropy of a letter sequence (bits)."
  [letters]
  (let [n     (count letters)
        freqs (frequencies letters)]
    (if (zero? n)
      0.0
      (- (reduce + (map (fn [[_ cnt]]
                          (let [p (/ (double cnt) n)]
                            (* p (Math/log p))))
                        freqs))))))

(defn entropy-bits
  "Shannon entropy in bits (log base 2)."
  [letters]
  (/ (shannon-entropy letters) (Math/log 2)))

(defn chapter-letters
  "Get normalized letter stream for a single chapter."
  [book ch]
  (let [verses (sefaria/fetch-chapter book ch)
        raw    (apply str (map norm/strip-html verses))]
    (norm/letter-stream raw)))

;; ── Main ─────────────────────────────────────────────────────

(defn -main []
  (println "=== H15: Shannon Entropy Per Chapter ===\n")
  (println "Computing entropy for every chapter in the Torah...")

  (let [all-chapters (atom [])
        global-offset (atom 0)]

    ;; Compute entropy for each chapter
    (doseq [book book-names]
      (let [chapters (get sefaria/book-chapters book)]
        (doseq [ch (range 1 (inc chapters))]
          (let [letters (chapter-letters book ch)
                n       (count letters)
                h       (entropy-bits letters)
                ;; Max possible entropy for 22-letter alphabet
                h-max   (/ (Math/log 22) (Math/log 2))
                ;; Efficiency = actual / max
                eff     (/ h h-max)]
            (swap! all-chapters conj
                   {:book    book
                    :chapter ch
                    :letters n
                    :entropy h
                    :efficiency eff
                    :start   @global-offset})
            (swap! global-offset + n)))))

    ;; Sort by entropy
    (let [sorted-asc  (sort-by :entropy @all-chapters)
          sorted-desc (reverse sorted-asc)]

      ;; Top 15 highest entropy (most information-dense)
      (println "\n  Most information-dense chapters (highest entropy):\n")
      (println (format "  %-12s  %3s  %6s  %6s  %5s"
                       "Book" "Ch" "Ltrs" "H(bits)" "Eff"))
      (println (apply str (repeat 40 "-")))
      (doseq [{:keys [book chapter letters entropy efficiency]} (take 15 sorted-desc)]
        (println (format "  %-12s  %3d  %,6d  %6.4f  %5.3f"
                         book chapter letters entropy efficiency)))

      ;; Bottom 15 lowest entropy (most repetitive)
      (println "\n  Most repetitive chapters (lowest entropy):\n")
      (println (format "  %-12s  %3s  %6s  %6s  %5s"
                       "Book" "Ch" "Ltrs" "H(bits)" "Eff"))
      (println (apply str (repeat 40 "-")))
      (doseq [{:keys [book chapter letters entropy efficiency]} (take 15 sorted-asc)]
        (println (format "  %-12s  %3d  %,6d  %6.4f  %5.3f"
                         book chapter letters entropy efficiency)))

      ;; Per-book average entropy
      (println "\n=== Per-Book Average Entropy ===")
      (doseq [book book-names]
        (let [chapters (filter #(= (:book %) book) @all-chapters)
              mean-h  (/ (reduce + (map :entropy chapters)) (count chapters))
              mean-eff (/ (reduce + (map :efficiency chapters)) (count chapters))]
          (println (format "  %-12s: H = %.4f bits, efficiency = %.3f"
                           book mean-h mean-eff))))

      ;; Torah maximum entropy
      (let [h-max (/ (Math/log 22) (Math/log 2))]
        (println (format "\n  Max possible (22 equiprobable letters): %.4f bits" h-max))
        (println (format "  English text typically: ~4.0-4.5 bits")))

      ;; Entropy at center chapters
      (println "\n=== Entropy at Center Chapters ===")
      (doseq [target [["Leviticus" 8 "Center of Torah"]
                       ["Leviticus" 14 "Center of Leviticus"]
                       ["Leviticus" 18 "Golden section of Leviticus"]
                       ["Leviticus" 11 "Traditional center (vav in gachon)"]
                       ["Genesis" 1 "First chapter"]
                       ["Deuteronomy" 34 "Last chapter"]]]
        (let [[book ch label] target
              entry (first (filter #(and (= (:book %) book)
                                          (= (:chapter %) ch))
                                    @all-chapters))]
          (when entry
            (println (format "  %-30s %-12s %2d: H = %.4f (eff %.3f, %,d letters)"
                             label (:book entry) (:chapter entry)
                             (:entropy entry) (:efficiency entry) (:letters entry))))))

      ;; Entropy profile — scan across the Torah
      (println "\n=== Entropy Profile (every 10th chapter) ===")
      (let [indexed (map-indexed vector @all-chapters)]
        (println (format "  %4s  %-12s  %3s  %6s  %5s"
                         "Idx" "Book" "Ch" "H" "Eff"))
        (println (apply str (repeat 36 "-")))
        (doseq [[i entry] indexed]
          (when (zero? (mod i 10))
            (println (format "  %4d  %-12s  %3d  %6.4f  %5.3f"
                             i (:book entry) (:chapter entry)
                             (:entropy entry) (:efficiency entry))))))))

  (println "\nDone."))
