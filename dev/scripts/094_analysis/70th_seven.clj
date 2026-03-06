(require '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[selah.gematria :as g]
         '[clojure.string :as str])

;; Get the full Torah letter stream
(def books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

(def torah-letters
  (vec (mapcat (fn [book]
                 (mapcat (fn [{:keys [text]}]
                           (let [n (norm/normalize text)]
                             (filter norm/hebrew-letter? n)))
                         (oshb/book-words book)))
               books)))

(println (str "Total letters: " (count torah-letters)))

;; Find all zayins (ז = 7)
(def zayin-positions
  (vec (keep-indexed (fn [i ch] (when (= ch \ז) i))
                     torah-letters)))

(println (str "Total zayins: " (count zayin-positions)))

;; Take every 70th zayin
(def every-70th
  (vec (take-nth 70 zayin-positions)))

(println (str "Every 70th zayin: " (count every-70th) " positions"))
(println)

;; What letter is at each of those positions?
;; More interesting: what word contains each position?
;; For now, let's look at the letters around each position
(println "=== Every 70th zayin — surrounding context ===")
(doseq [[idx pos] (map-indexed vector every-70th)]
  (let [start (max 0 (- pos 3))
        end (min (count torah-letters) (+ pos 4))
        context (apply str (subvec torah-letters start end))
        ;; Position in 4D space: pos = a*43550 + b*871 + c*67 + d
        a (quot pos 43550)
        rem1 (mod pos 43550)
        b (quot rem1 871)
        rem2 (mod rem1 871)
        c (quot rem2 67)
        d (mod rem2 67)]
    (when (< idx 30)
      (println (format "  #%2d  pos=%6d  a=%d b=%2d c=%2d d=%2d  ...%s..."
                       (inc idx) pos a b c d context)))))

;; Also: what are the letters AT every 70th position starting from position 7?
(println)
(println "=== Every 70th letter starting from position 6 (7th letter, 0-indexed) ===")
(let [positions (range 6 (count torah-letters) 70)
      letters (map #(nth torah-letters %) positions)
      first-50 (take 50 letters)]
  (println (str "Count: " (count positions) " letters"))
  (println (str "First 50: " (apply str first-50)))
  ;; What are the letter frequencies?
  (let [freqs (frequencies letters)]
    (println)
    (println "Letter frequencies:")
    (doseq [[ch cnt] (sort-by val > freqs)]
      (println (format "  %s (%d) = %d (%.1f%%)" ch (g/letter-value ch) cnt
                       (* 100.0 (/ cnt (count positions))))))))

;; And: ELS at skip 490 — what words appear?
(println)
(println "=== ELS skip=490 from position 0, first 20 letters ===")
(let [positions (range 0 (count torah-letters) 490)
      letters (map #(nth torah-letters %) positions)]
  (println (str "Count: " (count positions) " letters"))
  (println (str "First 20: " (apply str (take 20 letters))))
  (println (str "First 50: " (apply str (take 50 letters)))))

;; Skip 490 from every starting position 0-489, look for תורה or יהוה
(println)
(println "=== Searching for words at skip 490 ===")
(let [target-words {"תורה" "Torah" "יהוה" "YHWH" "כבש" "lamb" "אהבה" "love" 
                    "תמים" "Thummim" "סלח" "forgive" "כפר" "atone" "שלום" "peace"}
      max-start 490]
  (doseq [[word meaning] target-words]
    (let [wlen (count word)
          hits (for [start (range max-start)
                     :let [positions (map #(+ start (* % 490)) (range wlen))]
                     :when (every? #(< % (count torah-letters)) positions)
                     :let [spelled (apply str (map #(nth torah-letters %) positions))]
                     :when (= spelled word)]
                 start)]
      (when (seq hits)
        (println (format "  %s (%s) found at skip 490 from positions: %s"
                         word meaning (str/join ", " (take 10 hits))))))))
