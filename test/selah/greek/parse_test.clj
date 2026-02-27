(ns selah.greek.parse-test
  (:require [clojure.test :refer [deftest testing is are]]
            [selah.greek.parse :as gp]
            [selah.greek.normalize :as gn]))

;; ── Loader tests (each variant produces valid data) ───────────────

(def ^:private variants
  [:byzantine :sblgnt :tischendorf :westcott-hort
   :textus-receptus :scrivener :kjtr :kjtr-morphology])

(deftest all-variants-load
  (testing "every variant loads Matthew without error"
    (doseq [v variants]
      (let [words (gp/load-matthew v)]
        (is (pos? (count words))
            (str (name v) " should produce words"))))))

(deftest matt-1-1-word-count
  (testing "Matt 1:1 has exactly 8 words in all variants"
    (doseq [v variants]
      (let [words (gp/verses (gp/load-matthew v) 1 1 1 1)]
        (is (= 8 (count words))
            (str (name v) " Matt 1:1 should have 8 words"))))))

(deftest matt-1-1-content
  (testing "Matt 1:1 starts with 'book of genealogy of Jesus Christ' in all variants"
    (doseq [v variants]
      (let [words (gp/verses (gp/load-matthew v) 1 1 1 1)
            text  (gn/normalize (gp/verse-text words))]
        ;; All variants should normalize to the same opening
        (is (clojure.string/starts-with? text "βιβλοςγενεσεως")
            (str (name v) " should start with βιβλοςγενεσεως"))))))

;; ── Genealogy section ─────────────────────────────────────────────

(deftest genealogy-word-counts
  (testing "Matt 1:1-17 word counts are in expected range across variants"
    (doseq [v variants]
      (let [words (gp/verses (gp/load-matthew v) 1 1 1 17)]
        (is (<= 270 (count words) 280)
            (str (name v) " genealogy should have ~275 words"))))))

;; ── Morphological variants have POS data ──────────────────────────

(deftest parsed-variants-have-morphology
  (testing "parsed variants include part-of-speech"
    (doseq [v [:sblgnt :tischendorf :westcott-hort :textus-receptus :kjtr-morphology]]
      (let [first-word (first (gp/load-matthew v))]
        (is (some? (:pos first-word))
            (str (name v) " should have :pos"))
        (is (some? (:morph first-word))
            (str (name v) " should have :morph"))))))

;; ── Verse filtering ───────────────────────────────────────────────

(deftest verse-filter-test
  (testing "verse range filtering works correctly"
    (let [words (gp/load-matthew :byzantine)
          ch1   (gp/verses words 1 1 1 25)]
      (is (every? #(= 1 (:chapter %)) ch1))
      (is (= 1 (:verse (first ch1))))
      (is (= 25 (:verse (last ch1)))))))
