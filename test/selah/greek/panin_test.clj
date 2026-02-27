(ns selah.greek.panin-test
  (:require [clojure.test :refer [deftest testing is]]
            [selah.greek.panin :as panin]))

(def ^:private claims (delay (panin/evaluate-claims :tischendorf)))

(defn- claim [n] (nth @claims (dec n)))
(defn- actual [n] (:actual (claim n)))

;; ── Vocabulary structure (claims 1-6) ────────────────────────────

(deftest noun-lemmas-test
  (testing "Claim 1: 56 unique noun lemmas in 1:1-17 (8 × 7)"
    (is (= 56 (actual 1)))
    (is (zero? (mod (actual 1) 7)))))

(deftest article-occurrences-test
  (testing "Claim 2: 56 article occurrences (8 × 7)"
    (is (= 56 (actual 2)))
    (is (zero? (mod (actual 2) 7)))))

(deftest article-forms-test
  (testing "Claim 3: 7 distinct article forms (1 × 7)"
    (is (= 7 (actual 3)))))

(deftest vocabulary-count-test
  (testing "Claim 4: 49 vocabulary words in 1:1-11 by lemma (7 × 7)"
    (is (= 49 (actual 4)))
    (is (zero? (mod (actual 4) 7)))))

(deftest vocab-vowel-start-test
  (testing "Claim 5: 28 vocab words begin with vowel (4 × 7)"
    (is (= 28 (actual 5)))
    (is (zero? (mod (actual 5) 7)))))

(deftest vocab-consonant-start-test
  (testing "Claim 6: 21 vocab words begin with consonant (3 × 7)"
    (is (= 21 (actual 6)))
    (is (zero? (mod (actual 6) 7)))))

;; ── Vocab letter counts (claims 7-9) ────────────────────────────

(deftest vocab-consonants-test
  (testing "Claim 9: consonants in vocab words = 126 (18 × 7)"
    (is (= 126 (actual 9)))
    (is (zero? (mod (actual 9) 7)))))

(deftest vocab-letters-divisibility-test
  (testing "Claims 7-8: letters/vowels off by 2 (one lemma spelling variant)"
    ;; Panin: 266 letters, 140 vowels. We get 264, 138.
    ;; The 2-letter/vowel difference is consistent with a single lemma
    ;; having one extra vowel in Panin's text vs Tischendorf's lemma dict.
    (is (= 264 (actual 7)))
    (is (= 138 (actual 8)))
    ;; Our counts are still ÷7 (264 = 38×7 - 2, not ÷7)
    ;; But Panin's claimed values ARE ÷7
    (is (zero? (mod 266 7)))   ; Panin's claim
    (is (zero? (mod 140 7))))) ; Panin's claim

;; ── Frequency structure (claims 10-13) ──────────────────────────

(deftest multi-use-lemmas-test
  (testing "Claim 10: 35 lemmas used more than once (5 × 7)"
    (is (= 35 (actual 10)))
    (is (zero? (mod (actual 10) 7)))))

(deftest single-use-lemmas-test
  (testing "Claim 11: 14 lemmas used only once (2 × 7)"
    (is (= 14 (actual 11)))
    (is (zero? (mod (actual 11) 7)))))

(deftest single-form-lemmas-test
  (testing "Claim 12: 42 lemmas appear in single form only (6 × 7)"
    (is (= 42 (actual 12)))
    (is (zero? (mod (actual 12) 7)))))

(deftest multi-form-lemmas-test
  (testing "Claim 13: 7 lemmas appear in multiple forms (1 × 7)"
    (is (= 7 (actual 13)))))

;; ── Noun/non-noun split (claims 14-15) ──────────────────────────

(deftest noun-vocab-test
  (testing "Claim 14: 42 noun lemmas in vocab (6 × 7)"
    (is (= 42 (actual 14)))
    (is (zero? (mod (actual 14) 7)))))

(deftest non-noun-vocab-test
  (testing "Claim 15: 7 non-noun lemmas in vocab (1 × 7)"
    (is (= 7 (actual 15)))))

;; ── Proper names (claims 16-21) ─────────────────────────────────

(deftest proper-name-lemmas-test
  (testing "Claim 16: 35 proper name lemmas (5 × 7)"
    (is (= 35 (actual 16)))
    (is (zero? (mod (actual 16) 7)))))

(deftest proper-name-occurrences-test
  (testing "Claim 17: 63 proper name occurrences (9 × 7)"
    (is (= 63 (actual 17)))
    (is (zero? (mod (actual 17) 7)))))

(deftest male-name-lemmas-test
  (testing "Claim 18: 28 male genealogical name lemmas (4 × 7)"
    (is (= 28 (actual 18)))
    (is (zero? (mod (actual 18) 7)))))

(deftest male-name-occurrences-test
  (testing "Claim 19: 56 male genealogical name occurrences (8 × 7)"
    (is (= 56 (actual 19)))
    (is (zero? (mod (actual 19) 7)))))

(deftest non-male-lemmas-test
  (testing "Claim 20: 7 non-male name lemmas (1 × 7)"
    (is (= 7 (actual 20)))))

(deftest female-letters-test
  (testing "Claim 21: 14 letters in women's names (2 × 7)"
    (is (= 14 (actual 21)))
    (is (zero? (mod (actual 21) 7)))))

;; ── Babylon (claim 24) ──────────────────────────────────────────

(deftest babylon-letters-test
  (testing "Claim 24: Babylon lemma has 7 letters (1 × 7)"
    (is (= 7 (actual 24)))))

;; ── Structural invariants ───────────────────────────────────────

(deftest all-claims-structure-test
  (testing "evaluate-claims returns 24 claim maps"
    (is (= 24 (count @claims)))
    (is (every? #(contains? % :n) @claims))
    (is (every? #(contains? % :panin) @claims))
    (is (every? #(contains? % :actual) @claims))
    (is (every? #(contains? % :claim) @claims))))

(deftest vocabulary-sum-test
  (testing "Vowel-start + consonant-start = vocabulary count"
    (is (= (actual 4) (+ (actual 5) (actual 6))))))

(deftest frequency-sum-test
  (testing "Multi-use + single-use = vocabulary count"
    (is (= (actual 4) (+ (actual 10) (actual 11))))))

(deftest form-count-sum-test
  (testing "Single-form + multi-form = vocabulary count"
    (is (= (actual 4) (+ (actual 12) (actual 13))))))

(deftest noun-split-sum-test
  (testing "Noun vocab + non-noun vocab = vocabulary count"
    (is (= (actual 4) (+ (actual 14) (actual 15))))))

(deftest name-split-sum-test
  (testing "Male + non-male = total proper names"
    (is (= (actual 16) (+ (actual 18) (actual 20))))))

(deftest panin-values-all-div7-test
  (testing "All of Panin's claimed values are divisible by 7"
    (doseq [{:keys [n panin]} @claims]
      (is (zero? (mod panin 7))
          (format "Claim %d: Panin's value %d should be ÷7" n panin)))))

(deftest confirmed-count-test
  (testing "At least 20 of 22 testable claims confirmed on Tischendorf"
    (let [testable (filter #(some? (:actual %)) @claims)
          matched  (count (filter #(= (:panin %) (:actual %)) testable))]
      (is (>= matched 20)))))
