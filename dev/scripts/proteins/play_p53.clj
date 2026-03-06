(require '[selah.dna :as dna] :reload)
(require '[selah.oracle :as o])
(require '[selah.gematria :as g])
(require '[selah.dict :as dict])

;; Fetch p53
(def p53 (dna/get-protein "p53"))
(println "=== p53 — Guardian of the Genome ===")
(println (:description p53))
(println (str "Length: " (count (:sequence p53)) " residues"))
(println)

;; Play it through the breastplate
(def result (dna/play (:sequence p53) {:format :protein}))
(println (str "Hebrew (" (count (:hebrew result)) " letters):"))
(println (:hebrew result))
(println)
(println (str "GV: " (:gv result)))
(println)

;; What are the letter frequencies?
(println "Letter frequencies:")
(let [freq (sort-by val > (frequencies (:hebrew result)))]
  (doseq [[ch cnt] freq]
    (println (format "  %s (%s) = %d" ch (name (get dna/letter->aa ch :?)) cnt))))
(println)

;; The protein string for reference
(println (str "Protein: " (:protein-str result)))
