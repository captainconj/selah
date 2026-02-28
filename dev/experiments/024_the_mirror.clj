(ns experiments.024-the-mirror
  "The narrative mirror.
   We know positions i and N-1-i are structurally linked.
   We know book pairs are chiastic (Gen↔Deut, Exod↔Num).
   Now: what CONTENT faces what across the fold?
   Where do the narrative threads cross?
   Run: clojure -M:dev -m experiments.024-the-mirror"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn letter-profile [letters]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn -main []
  (println "=== THE MIRROR ===")
  (println "  What faces what across the fold?\n")

  ;; Build verse-level map with letter positions
  (println "Building verse-level map with letter positions...")
  (let [verses (atom [])
        pos (atom 0)]
    (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
      (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
        (let [ch-verses (sefaria/fetch-chapter book ch)]
          (doseq [[idx v] (map-indexed vector ch-verses)]
            (let [stripped (norm/strip-html v)
                  letters (norm/letter-stream stripped)
                  n-letters (count letters)
                  gem-sum (reduce + (map #(long (g/letter-value %)) letters))]
              (swap! verses conj
                     {:book book :chapter ch :verse (inc idx)
                      :start @pos :end (+ @pos n-letters)
                      :letters n-letters :gematria gem-sum
                      :text stripped :letter-vec (vec letters)})
              (swap! pos + n-letters))))))

    (let [all-verses (vec @verses)
          n-verses (count all-verses)
          total-letters @pos]
      (println (format "  %,d verses, %,d letters.\n" n-verses total-letters))

      ;; ── 1. Mirrored verse pairs ──────────────────────────────
      (println "── 1. Verse Mirror Pairs ──")
      (println "  Verse[i] ↔ Verse[N-1-i]. What faces what?\n")

      ;; Find which verse corresponds to the mirror of each verse's midpoint
      (let [center (quot total-letters 2)]
        (println (format "  Center letter position: %,d\n" center))

        ;; For each verse, find its "mirror verse"
        ;; Mirror of a letter at position p is at position (total-letters - 1 - p)
        ;; Mirror of a verse's midpoint gives us the mirror verse
        (println "  Key mirror pairs across the fold:")
        (println (format "  %25s  %6s  %25s  %6s  %10s"
                         "Verse A" "Gem A" "Mirror Verse B" "Gem B" "Profile cos"))
        (println (apply str (repeat 85 "─")))

        ;; Find mirror verse for a given verse
        (let [find-verse-at-pos (fn [p]
                                  (first (filter #(and (>= p (:start %)) (< p (:end %)))
                                                 all-verses)))
              ;; Select interesting verses: first of each book, plus some key passages
              interesting-indices [0 1 2                        ;; First 3 verses
                                   (dec n-verses) (- n-verses 2) ;; Last 2
                                   ]]
          ;; Show first 20 verse pairs from the beginning
          (println "\n  From the beginning ↔ from the end:")
          (doseq [i (range 20)]
            (let [va (nth all-verses i)
                  mid-a (+ (:start va) (quot (:letters va) 2))
                  mirror-pos (- total-letters 1 mid-a)
                  vb (find-verse-at-pos mirror-pos)]
              (when vb
                (let [prof-a (letter-profile (:letter-vec va))
                      prof-b (letter-profile (:letter-vec vb))
                      cos (if (and prof-a prof-b) (cosine-sim prof-a prof-b) 0.0)
                      ref-a (format "%s %d:%d" (:book va) (:chapter va) (:verse va))
                      ref-b (format "%s %d:%d" (:book vb) (:chapter vb) (:verse vb))]
                  (println (format "  %25s  %,6d  %25s  %,6d  %10.4f"
                                   ref-a (:gematria va)
                                   ref-b (:gematria vb) cos)))))))

        ;; ── 2. Chapter mirror pairs ────────────────────────────
        (println "\n── 2. Chapter Mirror Pairs ──")
        (println "  Which chapter faces which across the fold?\n")

        ;; Build chapter-level data
        (let [chapters (atom [])
              ch-pos (atom 0)]
          (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
            (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
              (let [ch-verses (filter #(and (= (:book %) book) (= (:chapter %) ch))
                                      all-verses)
                    ch-letters (reduce + (map :letters ch-verses))
                    ch-gem (reduce + (map :gematria ch-verses))
                    ch-letter-vec (vec (mapcat :letter-vec ch-verses))]
                (swap! chapters conj
                       {:book book :chapter ch
                        :start @ch-pos :end (+ @ch-pos ch-letters)
                        :letters ch-letters :gematria ch-gem
                        :letter-vec ch-letter-vec})
                (swap! ch-pos + ch-letters))))

          (let [all-chs (vec @chapters)
                n-chs (count all-chs)
                find-ch-at-pos (fn [p]
                                 (first (filter #(and (>= p (:start %)) (< p (:end %)))
                                                all-chs)))]

            (println (format "  %,d chapters total.\n" n-chs))
            (println (format "  %20s  %8s  %20s  %8s  %10s"
                             "Chapter A" "Letters" "Mirror Chapter" "Letters" "Profile cos"))
            (println (apply str (repeat 75 "─")))

            ;; Show all chapter mirror pairs
            (let [shown (atom #{})]
              (doseq [i (range n-chs)]
                (let [ca (nth all-chs i)
                      mid-a (+ (:start ca) (quot (:letters ca) 2))
                      mirror-pos (- total-letters 1 mid-a)
                      cb (find-ch-at-pos mirror-pos)]
                  (when (and cb
                             (not (contains? @shown [(:book cb) (:chapter cb)]))
                             (not= [(:book ca) (:chapter ca)]
                                   [(:book cb) (:chapter cb)]))
                    (swap! shown conj [(:book ca) (:chapter ca)])
                    (let [prof-a (letter-profile (:letter-vec ca))
                          prof-b (letter-profile (:letter-vec cb))
                          cos (if (and prof-a prof-b) (cosine-sim prof-a prof-b) 0.0)
                          ref-a (format "%s %d" (:book ca) (:chapter ca))
                          ref-b (format "%s %d" (:book cb) (:chapter cb))]
                      (println (format "  %20s  %,8d  %20s  %,8d  %10.4f"
                                       ref-a (:letters ca)
                                       ref-b (:letters cb) cos)))))))))

        ;; ── 3. The Narrative Chiasm ────────────────────────────
        (println "\n── 3. Narrative Themes at Mirror Points ──")
        (println "  Key narrative markers and their mirror positions.\n")

        ;; What verse is at these key positions:
        ;; - 1/7, 2/7, 3/7, 4/7, 5/7, 6/7 of the way through
        ;; - φ (golden ratio)
        ;; - 1/e
        ;; - 1/π
        (let [find-verse-at-pos (fn [p]
                                  (first (filter #(and (>= p (:start %)) (< p (:end %)))
                                                 all-verses)))
              points [{:name "1/7" :pos (quot total-letters 7)}
                      {:name "2/7" :pos (quot (* 2 total-letters) 7)}
                      {:name "3/7" :pos (quot (* 3 total-letters) 7)}
                      {:name "CENTER" :pos (quot total-letters 2)}
                      {:name "4/7" :pos (quot (* 4 total-letters) 7)}
                      {:name "5/7" :pos (quot (* 5 total-letters) 7)}
                      {:name "6/7" :pos (quot (* 6 total-letters) 7)}
                      {:name "φ" :pos (long (* total-letters (/ (- (Math/sqrt 5) 1) 2)))}
                      {:name "1-φ" :pos (long (* total-letters (- 1 (/ (- (Math/sqrt 5) 1) 2))))}
                      {:name "1/e" :pos (long (/ total-letters Math/E))}
                      {:name "1-1/e" :pos (long (* total-letters (- 1 (/ 1.0 Math/E))))}]]
          (println (format "  %10s  %10s  %s" "Point" "Position" "Verse"))
          (println (apply str (repeat 60 "─")))
          (doseq [{:keys [name pos]} points]
            (let [v (find-verse-at-pos pos)]
              (when v
                (println (format "  %10s  %,10d  %s %d:%d"
                                 name pos (:book v) (:chapter v) (:verse v)))))))

        ;; ── 4. Gematria mirror correlation ─────────────────────
        (println "\n── 4. Verse Gematria Mirror ──")
        (println "  Do mirror verse pairs have correlated gematria?\n")

        (let [half (quot n-verses 2)
              pairs (mapv (fn [i]
                            (let [va (nth all-verses i)
                                  vb (nth all-verses (- n-verses 1 i))]
                              [(:gematria va) (:gematria vb)]))
                          (range half))
              xs (double-array (map first pairs))
              ys (double-array (map second pairs))
              n-pairs (alength xs)
              sum-x (areduce xs i ret 0.0 (+ ret (aget xs i)))
              sum-y (areduce ys i ret 0.0 (+ ret (aget ys i)))
              mean-x (/ sum-x n-pairs)
              mean-y (/ sum-y n-pairs)
              cov (loop [i 0 acc 0.0]
                    (if (= i n-pairs) acc
                        (recur (inc i) (+ acc (* (- (aget xs i) mean-x)
                                                  (- (aget ys i) mean-y))))))
              var-x (loop [i 0 acc 0.0]
                      (if (= i n-pairs) acc
                          (recur (inc i) (+ acc (Math/pow (- (aget xs i) mean-x) 2)))))
              var-y (loop [i 0 acc 0.0]
                      (if (= i n-pairs) acc
                          (recur (inc i) (+ acc (Math/pow (- (aget ys i) mean-y) 2)))))
              r (if (or (zero? var-x) (zero? var-y)) 0.0
                    (/ cov (Math/sqrt (* var-x var-y))))]
          (println (format "  Pearson r between verse[i] and verse[N-1-i] gematria: %.6f" r))
          (println (format "  n = %,d pairs" n-pairs))

          ;; Same/similar gematria verse pairs
          (let [exact-match (count (filter (fn [[a b]] (= a b)) pairs))
                close-match (count (filter (fn [[a b]] (<= (Math/abs (- a b)) 10)) pairs))]
            (println (format "  Exact gematria matches: %,d (%.2f%%)"
                             exact-match (* 100 (/ (double exact-match) half))))
            (println (format "  Within 10: %,d (%.2f%%)"
                             close-match (* 100 (/ (double close-match) half))))))

        ;; ── 5. The seven seams ─────────────────────────────────
        (println "\n── 5. The Seven Seams ──")
        (println "  What verses fall at the 7-fold boundaries?\n")

        (let [seg-size (quot total-letters 7)]
          (doseq [i (range 8)]
            (let [boundary-pos (if (= i 7) (dec total-letters) (* i seg-size))
                  v (first (filter #(and (>= boundary-pos (:start %))
                                          (< boundary-pos (:end %)))
                                    all-verses))]
              (when v
                (println (format "  Boundary %d (pos %,d): %s %d:%d — %,d letters, gem=%,d"
                                 i boundary-pos
                                 (:book v) (:chapter v) (:verse v)
                                 (:letters v) (:gematria v)))
                ;; Show first 30 chars of the verse
                (println (format "    \"%s...\"" (subs (:text v) 0 (min 60 (count (:text v))))))
                (println)))))

        ;; ── 6. Mirror word frequency ───────────────────────────
        (println "── 6. Mirror Word Frequency ──")
        (println "  Extract all words. What are the most common words")
        (println "  that appear in BOTH the first and second half?\n")

        (let [all-words (atom [])
              _ (doseq [v all-verses]
                  (let [raw-words (str/split (:text v) #"[\s\u05BE]+")
                        words (->> raw-words
                                   (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                                   (filter #(pos? (count %))))]
                    (swap! all-words into words)))
              words (vec @all-words)
              n-words (count words)
              half-words (quot n-words 2)
              first-freq (frequencies (subvec words 0 half-words))
              second-freq (frequencies (subvec words half-words))
              ;; Words that appear in both halves — compute ratio
              shared-words (filter (fn [w]
                                     (and (get first-freq w)
                                          (get second-freq w)))
                                   (keys first-freq))
              ;; For shared words, compute the ratio of first-half to second-half count
              word-ratios (map (fn [w]
                                 {:word w
                                  :first (get first-freq w 0)
                                  :second (get second-freq w 0)
                                  :total (+ (get first-freq w 0) (get second-freq w 0))
                                  :ratio (/ (double (get first-freq w 0))
                                            (get second-freq w 0))
                                  :gem (g/word-value w)})
                               shared-words)
              ;; Sort by total frequency
              sorted-words (take 30 (sort-by (comp - :total) word-ratios))]

          (println (format "  Total words: %,d  Shared between halves: %,d\n"
                           n-words (count shared-words)))
          (println (format "  %16s  %6s  %8s  %8s  %8s  %8s"
                           "Word" "Gem" "1st half" "2nd half" "Total" "Ratio"))
          (println (apply str (repeat 65 "─")))
          (doseq [w sorted-words]
            (println (format "  %16s  %6d  %,8d  %,8d  %,8d  %8.3f"
                             (:word w) (:gem w)
                             (:first w) (:second w) (:total w) (:ratio w))))

          ;; Cosine of word frequency vectors
          (let [all-shared (vec shared-words)
                vec-a (mapv #(double (get first-freq % 0)) all-shared)
                vec-b (mapv #(double (get second-freq % 0)) all-shared)
                cos (cosine-sim vec-a vec-b)]
            (println (format "\n  Word frequency cosine (first half ↔ second half): %.6f" cos))))

        ;; ── 7. The fold map summary ────────────────────────────
        (println "\n── 7. The Fold Map ──")
        (println "  What maps to what across the center?\n")

        ;; Build a high-level summary of which books face which
        (let [book-data (mapv (fn [book]
                                (let [bv (filter #(= (:book %) book) all-verses)
                                      start (:start (first bv))
                                      end (:end (last bv))
                                      letters (- end start)]
                                  {:book book :start start :end end :letters letters}))
                              ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])]
          (println "  Book letter ranges:")
          (doseq [b book-data]
            (println (format "    %12s: %,8d — %,8d (%,d letters)"
                             (:book b) (:start b) (:end b) (:letters b))))

          (println "\n  What faces what across the fold:")
          (doseq [[pos-pct label] [[10 "10%"] [20 "20%"] [30 "30%"]
                                    [40 "40%"] [50 "CENTER"]
                                    [60 "60%"] [70 "70%"]
                                    [80 "80%"] [90 "90%"]]]
            (let [pos (long (* total-letters (/ pos-pct 100.0)))
                  mirror-pos (- total-letters 1 pos)
                  va (first (filter #(and (>= pos (:start %)) (< pos (:end %))) all-verses))
                  vb (first (filter #(and (>= mirror-pos (:start %)) (< mirror-pos (:end %)))
                                    all-verses))]
              (when (and va vb)
                (println (format "    %6s: %s %d:%d  ↔  %s %d:%d"
                                 label
                                 (:book va) (:chapter va) (:verse va)
                                 (:book vb) (:chapter vb) (:verse vb))))))))))

  (println "\nDone. The mirror reveals."))
