(ns experiments.fiber.143e2-overlap-control
  "Experiment 143e2: Letter-overlap controlled enrichment.

   The original enrichment (143e) compared host frequency against
   raw word coverage. This control computes expected enrichment
   from SHARED LETTERS alone, then tests whether observed enrichment
   exceeds the letter-overlap prediction.

   For each search-word → host-word pair:
   1. Count which letters of the search word appear in the host word
   2. Compute expected hosting from shared-letter positions only
   3. Compare observed hosting to this letter-specific expectation
   4. Report excess (if any) above the letter-overlap baseline"
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.java.io :as io]))

;; (s/build!)

(defn letter-coverage-in-word
  "For a given letter, how many positions in the Torah are inside
   occurrences of the target word AND contain that specific letter?"
  [letter target-word]
  (let [idx (s/index)
        words (:words idx)
        target-positions (filter #(= (:word %) target-word) words)]
    ;; Count positions within target word instances where the letter matches
    (reduce (fn [cnt tw]
              (let [letters (:letters idx)]
                (reduce (fn [c pos]
                          (if (= letter (nth letters pos)) (inc c) c))
                        cnt (range (:start tw) (:end tw)))))
            0 target-positions)))

(defn overlap-expected
  "Expected host count for search-word → host-word based on
   shared letters only. For each letter of search-word that also
   appears in host-word, compute the probability of landing in
   a host-word position, then sum across all fiber-letters."
  [search-word host-word n-fibers]
  (let [n (double (:n (s/index)))
        search-letters (vec (seq search-word))
        host-letters (set (seq host-word))
        ;; For each search letter position
        per-letter (for [[i ch] (map-indexed vector search-letters)]
                     (if (host-letters ch)
                       ;; This letter is shared. How many positions in the Torah
                       ;; contain this letter inside this host word?
                       (let [coverage (letter-coverage-in-word ch host-word)]
                         (/ coverage n))
                       ;; Not shared. Zero probability from this letter.
                       0.0))]
    ;; Expected = n-fibers × sum of per-letter probabilities
    (* n-fibers (reduce + per-letter))))

(defn overlap-controlled-enrichment
  "For a search word, compute hosting of key hosts with overlap control."
  [dims search-word hosts-to-test]
  (let [hits (s/find-word dims search-word {:max-results 500})
        ns (f/non-surface hits)
        n-fibers (count ns)
        observed-hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))]
    (for [host hosts-to-test
          :let [obs (double (get observed-hosts host 0))
                shared (count (clojure.set/intersection (set (seq search-word)) (set (seq host))))
                naive-expected (overlap-expected search-word host n-fibers)]
          :when (pos? obs)]
      {:search search-word :host host :observed (long obs)
       :shared-letters shared
       :overlap-expected naive-expected
       :excess (- obs naive-expected)
       :ratio (if (pos? naive-expected) (/ obs naive-expected) ##Inf)})))

(defn run-all []
  (let [dims [7 50 13 67]
        tests [["בהו" ["יהוה"]]
               ["אמת" ["את"]]
               ["חסד" ["יוסף"]]
               ["תורה" ["יהוה"]]
               ["כבש" ["כל"]]]]
    (println "═══ OVERLAP-CONTROLLED ENRICHMENT ═══\n")
    (doseq [[sw hosts] tests]
      (let [results (overlap-controlled-enrichment dims sw hosts)]
        (doseq [{:keys [search host observed shared-letters overlap-expected excess ratio]} results]
          (println (format "  %s → %s: obs=%d shared=%d overlap-exp=%.1f excess=%.1f ratio=%.1fx"
                           search host observed shared-letters overlap-expected excess ratio)))))))

(comment
  (s/build!)
  (run-all))
