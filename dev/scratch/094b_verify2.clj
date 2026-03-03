(require '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[selah.gematria :as g]
         '[selah.space.coords :as coords]
         '[clojure.string :as str])

;; What verse is at the center of Leviticus?
(println "=== VERSE AT LEVITICUS CENTER (position 22397 within Lev) ===")
(let [words (oshb/book-words "Leviticus")
      ;; accumulate letter positions to find which verse contains pos 22397
      target 22397]
  (loop [ws words
         pos 0
         last-ref nil]
    (if (empty? ws)
      (println "  Not found")
      (let [{:keys [text ref]} (first ws)
            letters (filter norm/hebrew-letter? (norm/normalize text))
            n (count letters)
            new-pos (+ pos n)]
        (if (> new-pos target)
          (do
            (println (format "  Position %d falls in: %s" target ref))
            (println (format "  Word: %s" text))
            (println (format "  Letter range: %d-%d" pos (dec new-pos))))
          (recur (rest ws) new-pos ref))))))

;; What is at Torah position 163997 in 4D coords?
(println)
(println "=== TORAH POSITION 163997 (Lev center) in 4D ===")
(let [pos 163997
      a (quot pos 43550)
      rem1 (mod pos 43550)
      b (quot rem1 871)
      rem2 (mod rem1 871)
      c (quot rem2 67)
      d (mod rem2 67)]
  (println (format "  (a=%d, b=%d, c=%d, d=%d)" a b c d)))

;; Verify Lev 25:8 coordinates
(println)
(println "=== LEV 25:8 — COORDINATES ===")
(let [gen-len 78069
      exo-len 63531
      lev-start (+ gen-len exo-len)
      words (oshb/book-words "Leviticus")
      ;; Find words in Lev 25:8
      lev25-words (filter #(and (:ref %) (str/starts-with? (:ref %) "Leviticus 25:8"))
                          words)]
  ;; Accumulate to find positions
  (loop [ws words
         pos 0]
    (if (empty? ws)
      nil
      (let [{:keys [text ref]} (first ws)
            letters (vec (filter norm/hebrew-letter? (norm/normalize text)))
            n (count letters)
            torah-pos (+ lev-start pos)]
        (when (and ref (str/includes? (str ref) "25:8"))
          (let [word-text (apply str letters)]
            (when (> n 0)
              (let [a (quot torah-pos 43550)
                    rem1 (mod torah-pos 43550)
                    b (quot rem1 871)
                    rem2 (mod rem1 871)
                    c (quot rem2 67)
                    d (mod rem2 67)]
                (println (format "  %s  pos=%d  (%d,%d,%d,%d)  %s"
                                 ref torah-pos a b c d word-text))
                ;; Check if this is שבע
                (when (= "שבע" word-text)
                  (println "    ^^^ שבע (seven) ^^^"))))))
        (recur (rest ws) (+ pos n))))))

;; Total Torah gematria
(println)
(println "=== TOTAL TORAH GEMATRIA ===")
(let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
      total (reduce + (for [book books
                            {:keys [text]} (oshb/book-words book)
                            ch (norm/normalize text)
                            :when (norm/hebrew-letter? ch)]
                        (g/letter-value ch)))]
  (println (format "  Total gematria: %d" total))
  (println (format "  mod 441 (truth²): %d" (mod total 441)))
  (println (format "  mod 490 (Thummim): %d" (mod total 490)))
  (println (format "  mod 70: %d" (mod total 70))))
