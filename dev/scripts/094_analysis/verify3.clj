(require '[selah.gematria :as g])

;; סוד = secret = 70?
(println (format "סוד (secret) = %d" (g/word-value "סוד")))
(println (format "  ס=%d ו=%d ד=%d sum=%d" 
                 (g/letter-value \ס) (g/letter-value \ו) (g/letter-value \ד)
                 (+ (g/letter-value \ס) (g/letter-value \ו) (g/letter-value \ד))))

;; Check 091b data for תמים as mercy solo eigenword
(println)
(let [solo-file "data/experiments/091b/solo-by-head.edn"]
  (println (str "Checking " solo-file "..."))
  (let [data (clojure.edn/read-string (slurp solo-file))]
    (println (str "Keys: " (keys data)))
    ;; Look for תמים in mercy solos
    (doseq [[head words] data]
      (let [has-tamim (some #(= "תמים" (:word %)) words)]
        (when has-tamim
          (println (format "  תמים found as solo eigenword of: %s" (name head))))))))
