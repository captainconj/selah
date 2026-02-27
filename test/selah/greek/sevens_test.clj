(ns selah.greek.sevens-test
  (:require [clojure.test :refer [deftest testing is are]]
            [selah.greek.sevens :as sevens]
            [selah.greek.parse :as gp]
            [selah.greek.normalize :as gn]))

;; ── Feature extraction tests ──────────────────────────────────────

(def ^:private tisch-1-1
  (delay (vec (gp/verses (gp/load-matthew :tischendorf) 1 1 1 1))))

(def ^:private tisch-genealogy
  (delay (vec (gp/verses (gp/load-matthew :tischendorf) 1 1 1 17))))

(deftest matt-1-1-basics
  (testing "Matt 1:1 has 8 words"
    (is (= 8 (sevens/word-count @tisch-1-1))))

  (testing "letter count is consistent with manual check"
    ;; Βίβλος γενέσεως Ἰησοῦ Χριστοῦ υἱοῦ Δαυεὶδ υἱοῦ Ἀβραάμ
    (is (= 46 (sevens/letter-count @tisch-1-1))))

  (testing "vowels + consonants = letters"
    (is (= (sevens/letter-count @tisch-1-1)
           (+ (sevens/vowel-count @tisch-1-1)
              (sevens/consonant-count @tisch-1-1))))))

(deftest isopsephy-sanity
  (testing "total isopsephy is positive and large"
    (is (> (sevens/total-isopsephy @tisch-1-1) 1000))))

(deftest proper-name-detection
  (testing "Tischendorf 1:1-17 has about 73 proper names via PRI tag"
    (let [n (sevens/proper-name-count @tisch-genealogy)]
      (is (<= 70 n 80)
          (str "Expected ~73 proper names, got " n)))))

;; ── Analysis structure ────────────────────────────────────────────

(deftest analysis-structure
  (testing "analyze returns a vector of feature maps"
    (let [analysis (sevens/analyze @tisch-1-1)]
      (is (vector? analysis))
      (is (every? #(contains? % :feature) analysis))
      (is (every? #(contains? % :value) analysis))
      (is (every? #(contains? % :mod7) analysis))
      (is (every? #(contains? % :div7?) analysis)))))

;; ── Cross-variant consistency ─────────────────────────────────────

(deftest all-variants-same-word-count-1-1
  (testing "Matt 1:1 has 8 words in every variant"
    (doseq [v [:sblgnt :tischendorf :westcott-hort :textus-receptus
               :byzantine :kjtr :scrivener]]
      (let [words (gp/verses (gp/load-matthew v) 1 1 1 1)]
        (is (= 8 (count words))
            (str (name v) " should have 8 words"))))))

;; ── Statistical controls ──────────────────────────────────────────

(deftest expected-sevens-test
  (testing "expected-sevens returns proper structure"
    (let [analysis (sevens/analyze @tisch-1-1)
          stats    (sevens/expected-sevens analysis)]
      (is (= 14 (:features stats)))
      (is (number? (:expected stats)))
      (is (number? (:z-score stats))))))

;; ── The actual findings ───────────────────────────────────────────

(deftest variant-sensitivity
  (testing "different variants produce different seven-counts for Matt 1:1"
    (let [counts (for [v [:sblgnt :tischendorf :westcott-hort :textus-receptus]]
                   (let [words    (gp/verses (gp/load-matthew v) 1 1 1 1)
                         analysis (sevens/analyze words)]
                     (count (filter :div7? analysis))))]
      ;; Not all variants produce the same number of sevens
      ;; This is the key finding: the claim is variant-dependent
      (is (> (count (distinct counts)) 1)
          "Different variants should produce different seven-counts"))))

(deftest genealogy-not-exceptional
  (testing "Matt 1:1-17 seven-count is close to chance expectation (~2 of 14)"
    (let [words    (gp/verses (gp/load-matthew :tischendorf) 1 1 1 17)
          analysis (sevens/analyze words)
          div7     (count (filter :div7? analysis))]
      (is (<= div7 5)
          "Should not have an exceptionally high seven count"))))
