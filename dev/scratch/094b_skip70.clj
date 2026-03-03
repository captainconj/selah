(require '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(def torah-letters
  (vec (mapcat (fn [book]
                 (mapcat (fn [{:keys [text]}]
                           (let [n (norm/normalize text)]
                             (filter norm/hebrew-letter? n)))
                         (oshb/book-words book)))
               ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])))

(def N (count torah-letters))

;; Search for key words at skip 70
(println "=== WORDS AT SKIP 70 ===")
(let [targets {"תורה" "Torah" "יהוה" "YHWH" "כבש" "lamb" "אהבה" "love"
               "תמים" "Thummim" "שלום" "peace" "חסד" "lovingkindness"
               "דם" "blood" "כפר" "atone" "סלח" "forgive" "פדה" "ransom"
               "גאל" "redeem" "מזבח" "altar" "שמר" "guard" "ברית" "covenant"
               "אמת" "truth" "חיים" "life" "נח" "Noah/rest" "חן" "grace"
               "סלה" "selah" "שבע" "seven" "שבת" "sabbath" "גן" "garden"
               "את" "aleph-tav" "אל" "God" "בן" "son" "אב" "father"}]
  (doseq [[word meaning] (sort-by key targets)]
    (let [wlen (count word)
          hits (for [start (range 70)
                     :let [positions (map #(+ start (* % 70)) (range wlen))]
                     :when (every? #(< % N) positions)
                     :let [spelled (apply str (map #(nth torah-letters %) positions))]
                     :when (= spelled word)]
                 {:start start
                  :a (quot start 43550)
                  :positions positions})]
      (when (seq hits)
        (println (format "  %s (%s): %d hits at starts: %s"
                         word meaning (count hits)
                         (str/join ", " (map :start (take 15 hits)))))))))

;; Also skip 490
(println)
(println "=== WORDS AT SKIP 490 ===")
(let [targets {"תורה" "Torah" "יהוה" "YHWH" "כבש" "lamb" "אהבה" "love"
               "תמים" "Thummim" "שלום" "peace" "חסד" "lovingkindness"
               "דם" "blood" "כפר" "atone" "סלח" "forgive" "פדה" "ransom"
               "גאל" "redeem" "מזבח" "altar" "שמר" "guard" "ברית" "covenant"
               "אמת" "truth" "חיים" "life" "נח" "Noah/rest" "חן" "grace"
               "סלה" "selah" "שבע" "seven" "שבת" "sabbath" "גן" "garden"
               "את" "aleph-tav" "אל" "God" "בן" "son" "אב" "father"}]
  (doseq [[word meaning] (sort-by key targets)]
    (let [wlen (count word)
          hits (for [start (range 490)
                     :let [positions (map #(+ start (* % 490)) (range wlen))]
                     :when (every? #(< % N) positions)
                     :let [spelled (apply str (map #(nth torah-letters %) positions))]
                     :when (= spelled word)]
                 {:start start})]
      (when (seq hits)
        (println (format "  %s (%s): %d hits at starts: %s"
                         word meaning (count hits)
                         (str/join ", " (map :start (take 15 hits)))))))))

;; Now the zayin analysis — every 70th zayin coordinates
(println)
(println "=== EVERY 70th ZAYIN — c-axis distribution ===")
(let [zayin-pos (vec (keep-indexed (fn [i ch] (when (= ch \ז) i)) torah-letters))
      every-70th (vec (take-nth 70 zayin-pos))
      c-values (map (fn [pos] 
                      (let [rem1 (mod pos 43550)
                            rem2 (mod rem1 871)]
                        (quot rem2 67)))
                    every-70th)
      c-freq (frequencies c-values)]
  (println (str "Total: " (count every-70th) " positions"))
  (println "c-axis frequencies (expected ~" (format "%.1f" (/ (count every-70th) 13.0)) " per value):")
  (doseq [c (range 13)]
    (let [cnt (get c-freq c 0)
          expected (/ (count every-70th) 13.0)
          ratio (/ cnt expected)]
      (println (format "  c=%2d: %2d  (%.2fx)" c cnt ratio)))))

;; And the key thing: every 70th zayin, what WORDS do they fall in?
(println)
(println "=== EVERY 70th ZAYIN — containing words (from context) ===")
(let [zayin-pos (vec (keep-indexed (fn [i ch] (when (= ch \ז) i)) torah-letters))
      every-70th (vec (take-nth 70 zayin-pos))]
  (doseq [[idx pos] (map-indexed vector every-70th)]
    (let [;; extract ~10 letter context
          start (max 0 (- pos 5))
          end (min N (+ pos 6))
          context (apply str (subvec torah-letters start end))
          a (quot pos 43550)
          rem1 (mod pos 43550)
          b (quot rem1 871)
          rem2 (mod rem1 871)
          c (quot rem2 67)
          d (mod rem2 67)]
      (println (format "  #%2d  (%d,%2d,%2d,%2d)  ...%s..."
                       (inc idx) a b c d context)))))
