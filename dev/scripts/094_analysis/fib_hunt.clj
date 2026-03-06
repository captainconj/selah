(require '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.edn :as edn])

;; Generate Fibonacci numbers up to reasonable range
(def fibs 
  (let [f (take 30 (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))]
    (set f)))

(def fib-list (sort (vec fibs)))
(println "Fibonacci numbers:" (vec fib-list))

;; Check every number we've encountered in 094/094b
(println)
(println "=== FIBONACCI HUNT ACROSS ALL 094 RESULTS ===")

(def checks
  [;; Core numbers
   ["Thummim GV" 490]
   ["Urim GV" 257]
   ["Urim-Thummim gap" 233]
   ["Leshem+Gad" 377]
   ["Total forced readings" 2031]
   ["Total illuminable words" 11552]
   ["Total unique phrases" 310908]
   ["Total phrase instances" 350334]
   ["Hapax phrases" 282447]
   ["Oracle synonyms" 4140]
   ["Synonym groups" 63]
   ["Non-zero singular values" 8857]
   ["Effective rank 90%" 2648]
   ["Effective rank 95%" 3892]
   ["Effective rank 99%" 6334]
   ["Total Torah words" 12826]
   ["Max phrase count" 11699]
   ["GV=490 word count" 25]
   ["GV=70 word count" 25]
   ["Seventy phrase count" 13]
   ["Seven phrase count" 2]
   ["Thummim phrase count" 6]
   ["Forced sum" 490]
   ["Breastplate letters" 72]
   ["Sukkot bulls" 70]
   ["Torah gematria" 21010192]
   ["Torah gematria mod 441" 70]
   ["Torah gematria mod 70" 42]
   ["Total zayins" 2198]
   ["Every-70th-zayin count" 32]
   ["Lev center position" 22397]
   ["Book center sum" 132]
   ["5040 = 7!" 5040]
   ["304850/70" 4355]
   ;; Singular values (rounded to integers)
   ["σ₁" 108]
   ["σ₂" 102]
   ["σ₃" 73]
   ;; Forced reading GVs
   ["כפר atone" 300]
   ["פדה ransom" 89]
   ["מזבח altar" 57]
   ["דם blood" 44]
   ["סלח forgive" 98]
   ["גאל redeem" 34]
   ["זבח sacrifice" 17]
   ["כרת cut covenant" 620]
   ["נקם avenge" 190]
   ["משכן tabernacle" 410]
   ;; Key GVs
   ["אהבה love" 13]
   ["אמת truth" 441]
   ["שלום peace" 376]
   ["ברית covenant" 612]
   ["יהוה YHWH" 26]
   ["כבש lamb" 322]
   ["אור light" 207]
   ["דרך way" 224]
   ["חיים life" 68]
   ;; Phrase counts of key words
   ["Love readings" 3]
   ["YHWH readings" 3]
   ["Peace readings" 6]
   ["Covenant readings" 4]
   ["Truth readings" 2]
   ;; Odd/even ratio
   ["Odd count words (approx)" 7611]
   ["Even count words (approx)" 3941]])

(println)
(println "=== EXACT FIBONACCI MATCHES ===")
(doseq [[label n] checks]
  (when (fibs n)
    (let [idx (.indexOf fib-list (int n))]
      (println (format "  F(%d) = %d ← %s" (inc idx) n label)))))

(println)
(println "=== NEAR FIBONACCI (within ±1) ===")
(doseq [[label n] checks]
  (when (not (fibs n))
    (let [nearest (apply min-key #(Math/abs (- (long %) (long n))) fib-list)
          delta (- n nearest)]
      (when (and (<= (Math/abs delta) 2) (> n 10))
        (println (format "  %d ← %s  (nearest F = %d, Δ = %+d)"
                         n label nearest delta))))))

;; Look for Fibonacci in the distribution itself
(println)
(println "=== FIBONACCI IN PHRASE-COUNT DISTRIBUTION ===")
(let [dists (edn/read-string (slurp "data/experiments/094/distributions.edn"))
      pd (:phrase-count-dist dists)]
  ;; For each Fibonacci number, how many words have that phrase count?
  (doseq [f (filter #(and (> % 0) (< % 200)) fib-list)]
    (let [w (get pd f 0)]
      (println (format "  phrase-count=%d (F): %d words" f w))))
  
  ;; And: which phrase counts have a Fibonacci number of words?
  (println)
  (println "  Phrase counts with Fibonacci-many words:")
  (doseq [[pc cnt] (sort-by key pd)]
    (when (and (fibs cnt) (> cnt 2))
      (println (format "    count=%d: %d words (= F)" pc cnt)))))
