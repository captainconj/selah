(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.text.oshb :as oshb]
         '[selah.text.normalize :as norm]
         '[selah.oracle :as oracle]
         '[clojure.string :as str])

;; 1. Urim - Thummim = 233 = F(13)?
(println "=== 1. URIM - THUMMIM ===")
(let [urim (g/word-value "אורים")
      thummim (g/word-value "תמים")
      diff (- thummim urim)]
  (println (format "  אורים (Urim) = %d" urim))
  (println (format "  תמים (Thummim) = %d" thummim))
  (println (format "  Difference = %d" diff))
  ;; Fibonacci sequence
  (let [fibs (take 20 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))]
    (println (format "  Fibonacci: %s" (str/join ", " (take 15 fibs))))
    (println (format "  F(13) = %d" (nth fibs 12)))
    (println (format "  233 = F(13)? %s" (= 233 (nth fibs 12))))))

;; 2. Center letter of Leviticus
(println)
(println "=== 2. CENTER LETTER OF LEVITICUS ===")
(let [lev-letters (vec (mapcat (fn [{:keys [text]}]
                                 (let [n (norm/normalize text)]
                                   (filter norm/hebrew-letter? n)))
                               (oshb/book-words "Leviticus")))
      n (count lev-letters)
      mid (quot n 2)
      center-letter (nth lev-letters mid)]
  (println (format "  Leviticus letters: %d" n))
  (println (format "  Center position: %d" mid))
  (println (format "  Center letter: %s (value=%d)" center-letter (g/letter-value center-letter)))
  (println (format "  Is ayin (ע)? %s" (= center-letter \ע))))

;; 3. All five book centers
(println)
(println "=== 3. FIVE BOOK CENTERS ===")
(let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
  (doseq [book books]
    (let [letters (vec (mapcat (fn [{:keys [text]}]
                                 (let [n (norm/normalize text)]
                                   (filter norm/hebrew-letter? n)))
                               (oshb/book-words book)))
          n (count letters)
          mid (quot n 2)
          ch (nth letters mid)]
      (println (format "  %s: %d letters, center=%s (%d)"
                       book n ch (g/letter-value ch)))))
  (println (format "  Sum: %d" (+ 1 50 70 5 6))))

;; 4. Leviticus center position in Torah stream
(println)
(println "=== 4. LEVITICUS CENTER IN TORAH STREAM ===")
(let [gen-len (count (mapcat (fn [{:keys [text]}]
                               (filter norm/hebrew-letter? (norm/normalize text)))
                             (oshb/book-words "Genesis")))
      exo-len (count (mapcat (fn [{:keys [text]}]
                               (filter norm/hebrew-letter? (norm/normalize text)))
                             (oshb/book-words "Exodus")))
      lev-letters (vec (mapcat (fn [{:keys [text]}]
                                 (filter norm/hebrew-letter? (norm/normalize text)))
                               (oshb/book-words "Leviticus")))
      lev-len (count lev-letters)
      lev-start (+ gen-len exo-len)
      lev-center-torah (+ lev-start (quot lev-len 2))]
  (println (format "  Genesis: %d letters" gen-len))
  (println (format "  Exodus: %d letters" exo-len))
  (println (format "  Leviticus: %d letters (start=%d)" lev-len lev-start))
  (println (format "  Leviticus center in Torah: position %d" lev-center-torah))
  (println (format "  Position within Leviticus: %d" (quot lev-len 2)))
  (println (format "  Contains 490? %s" (str/includes? (str lev-center-torah) "490"))))

;; 5. Gematria verifications
(println)
(println "=== 5. GEMATRIA CHECKS ===")
(println (format "  קרן (shining) = %d" (g/word-value "קרן")))
(println (format "  ספיר (sapphire) = %d" (g/word-value "ספיר")))
(println (format "  350 = 5 × 70? %s" (= 350 (* 5 70))))
(println (format "  304850 / 350 = %s" (if (zero? (mod 304850 350)) 
                                          (str (/ 304850 350)) "not clean")))
(println (format "  871 = 13 × 67? %s" (= 871 (* 13 67))))
(println (format "  אמת (truth) = %d" (g/word-value "אמת")))
(println (format "  אור (light) = %d" (g/word-value "אור")))
(println (format "  אורים (Urim) = %d" (g/word-value "אורים")))

;; 6. Stone 7 + tribe Gad
(println)
(println "=== 6. STONE 7 + GAD ===")
(println (format "  לשם (Leshem/stone 7) = %d" (g/word-value "לשם")))
(println (format "  גד (Gad) = %d" (g/word-value "גד")))
(println (format "  Sum = %d" (+ (g/word-value "לשם") (g/word-value "גד"))))
(let [fibs (take 20 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))]
  (println (format "  F(14) = %d" (nth fibs 13)))
  (println (format "  377 = F(14)? %s" (= 377 (nth fibs 13)))))
