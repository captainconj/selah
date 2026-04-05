(ns experiments.fiber.143n2-anagram-null
  "Experiment 143n2: Anagram attraction null model.

   The original test (143n) showed anagram pairs attract at 5-27x.
   But anagram pairs share ALL letters — so they SHOULD show elevated
   hosting from letter overlap alone.

   This null model compares:
   1. Anagram pairs (all letters shared)
   2. Non-anagram pairs with SIMILAR letter overlap (2 of 3 shared)
   3. Non-anagram pairs with ZERO letter overlap

   If anagram pairs attract MORE than non-anagram pairs with similar
   overlap, the geometry is doing something beyond letter mechanics."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [selah.dict :as d]))

;; (s/build!)

(defn hosting-rate
  "For search-word, what fraction of fiber-letters land in host-word?"
  [dims search-word host-word]
  (let [hits (s/find-word dims search-word {:max-results 500})
        ns (f/non-surface hits)
        total-letters (* (count (seq search-word)) (count ns))
        hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
        obs (get hosts host-word 0)]
    (when (pos? total-letters)
      {:search search-word :host host-word
       :observed obs :total-letters total-letters
       :rate (double (/ obs total-letters))
       :shared (count (clojure.set/intersection (set (seq search-word)) (set (seq host-word))))})))

(defn run-all []
  (let [dims [7 50 13 67]]
    (println "═══ ANAGRAM NULL MODEL ═══\n")
    (println "  ANAGRAM PAIRS (all 3 letters shared):\n")
    (doseq [[sw hw label] [["ראש" "אשר" "head→blessed"]
                             ["שכב" "כבש" "lie-down→lamb"]
                             ["בשר" "שבר" "flesh→break"]
                             ["שבר" "בשר" "break→flesh"]
                             ["רמש" "שמר" "creeping→guard"]]]
      (let [r (hosting-rate dims sw hw)]
        (when r (println (format "    %s: obs=%d shared=%d rate=%.5f"
                                  label (:observed r) (:shared r) (:rate r))))))

    (println "\n  NON-ANAGRAM PAIRS WITH 2 SHARED LETTERS:\n")
    ;; Find pairs that share exactly 2 of 3 letters but are NOT anagrams
    (doseq [[sw hw label] [["ראש" "שרה" "head→Sarah (share ר,ש)"]
                             ["כבש" "שבת" "lamb→sabbath (share ב,ש)"]
                             ["בשר" "שרה" "flesh→Sarah (share ש,ר)"]
                             ["שמר" "משה" "guard→Moses (share מ,ש)"]
                             ["נחש" "חשב" "serpent→think (share ח,ש)"]]]
      (let [r (hosting-rate dims sw hw)]
        (when r (println (format "    %s: obs=%d shared=%d rate=%.5f"
                                  label (:observed r) (:shared r) (:rate r))))))

    (println "\n  NON-ANAGRAM PAIRS WITH 0-1 SHARED LETTERS:\n")
    (doseq [[sw hw label] [["ראש" "יהוה" "head→YHWH (share 0)"]
                             ["כבש" "יהוה" "lamb→YHWH (share 0)"]
                             ["שמר" "יהוה" "guard→YHWH (share 0)"]
                             ["בשר" "מים" "flesh→water (share מ only)"]]]
      (let [r (hosting-rate dims sw hw)]
        (when r (println (format "    %s: obs=%d shared=%d rate=%.5f"
                                  label (:observed r) (:shared r) (:rate r))))))))

(comment
  (s/build!)
  (run-all))
