;; Library 4-Letter Voice — All 21 proteins with window=4
;;
;; Runs each protein through the breastplate with 4-letter sliding windows.
;; Adds deeper readings to the library: בפיו (in his mouth), קולו (his voice),
;; הנני (here I am), מצוה (commandment), etc.
;;
;; Key findings per protein documented in docs/ark/ files.

(ns scripts.library-4letter
  (:require [selah.dna :as dna]
            [clojure.string :as str]
            [clojure.set]
            [clojure.java.io :as io]))

(defn run-library-4letter
  "Run all library proteins with window=4, save results."
  []
  (let [results (for [{:keys [name accession]} dna/library]
                  (do
                    (println (str "\n=== " name " (window=4) ==="))
                    (let [r (dna/experiment name {:window 4 :save? false})]
                      {:name name
                       :accession accession
                       :residues (:residues r)
                       :window-size 4
                       :windows-with-readings (:windows-with-readings r)
                       :total-windows (:total-windows r)
                       :top-words (vec (take 30 (:top-words r)))})))
        data (vec (doall results))]

    ;; Summary
    (println "\n\n=== LIBRARY SUMMARY (window=4) ===\n")
    (doseq [{:keys [name windows-with-readings total-windows top-words]} data]
      (println (format "%-25s  %3d/%3d windows  top: %s"
                       name
                       windows-with-readings total-windows
                       (str/join ", "
                            (map #(str (:word %) " ×" (:count %))
                                 (take 3 top-words))))))

    ;; Save
    (let [path "data/dna/library-4letter.edn"]
      (io/make-parents path)
      (spit path (pr-str data))
      (println (str "\nSaved: " path)))

    data))

(defn compare-windows
  "Compare 3-letter vs 4-letter results for a protein."
  [name-pattern]
  (let [r3 (dna/experiment name-pattern {:window 3 :save? false})
        r4 (dna/experiment name-pattern {:window 4 :save? false})
        words3 (set (map :word (:top-words r3)))
        words4 (set (map :word (:top-words r4)))
        new-at-4 (clojure.set/difference words4 words3)]
    (println (str "\n" (:name r3) ":"))
    (println (str "  3-letter: " (:windows-with-readings r3) "/" (:total-windows r3)
                  " windows, " (count (:top-words r3)) " words"))
    (println (str "  4-letter: " (:windows-with-readings r4) "/" (:total-windows r4)
                  " windows, " (count (:top-words r4)) " words"))
    (println (str "  New at 4-letter: " (count new-at-4)))
    (doseq [w (sort new-at-4)]
      (let [entry (first (filter #(= w (:word %)) (:top-words r4)))]
        (println (str "    " w " — " (:meaning entry) " ×" (:count entry)))))
    {:name (:name r3)
     :w3 r3 :w4 r4
     :new-at-4 new-at-4}))

(comment
  ;; Run all 21 proteins with window=4
  (run-library-4letter)

  ;; Compare 3 vs 4 for a specific protein
  (compare-windows "foxp2")
  (compare-windows "collagen")
  (compare-windows "p53")
  )
