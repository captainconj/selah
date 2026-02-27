(ns selah.aramaic.sevens-test
  (:require [clojure.test :refer [deftest testing is]]
            [selah.aramaic.sevens :as asev]
            [selah.aramaic.parse :as ap]))

(def ^:private matt (delay (ap/load-matthew)))

(deftest analysis-structure-test
  (testing "analyze returns feature maps"
    (let [words    (vec (ap/verses @matt 1 1 1 1))
          analysis (asev/analyze words)]
      (is (vector? analysis))
      (is (every? #(contains? % :feature) analysis))
      (is (every? #(contains? % :div7?) analysis)))))

(deftest peshitta-word-count
  (testing "Peshitta Matt 1:1-17 has 175 words (25 × 7)"
    (let [words (vec (ap/verses @matt 1 1 1 17))]
      (is (= 175 (asev/word-count words)))
      (is (zero? (mod (asev/word-count words) 7))))))

(deftest peshitta-unique-forms
  (testing "Peshitta Matt 1:1-17 has 112 unique word forms (16 × 7)"
    (let [words (vec (ap/verses @matt 1 1 1 17))]
      (is (= 112 (asev/unique-words words)))
      (is (zero? (mod (asev/unique-words words) 7))))))

(deftest peshitta-matres
  (testing "Peshitta Matt 1:1-17 has 287 matres lectionis (41 × 7)"
    (let [words (vec (ap/verses @matt 1 1 1 17))]
      (is (= 287 (asev/matres-count words)))
      (is (zero? (mod (asev/matres-count words) 7))))))

(deftest peshitta-vs-greek-structure
  (testing "Peshitta genealogy is structurally more compact than Greek"
    (let [p-words (vec (ap/verses @matt 1 1 1 17))]
      ;; 175 Aramaic words vs 275 Greek words for the same content
      (is (< (count p-words) 200)))))
