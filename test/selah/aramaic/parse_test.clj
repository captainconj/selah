(ns selah.aramaic.parse-test
  (:require [clojure.test :refer [deftest testing is]]
            [selah.aramaic.parse :as ap]
            [selah.aramaic.normalize :as an]))

(def ^:private matt (delay (ap/load-matthew)))

(deftest load-test
  (testing "loads all 28 chapters"
    (is (= (set (range 1 29))
           (set (distinct (map :chapter @matt)))))))

(deftest matt-1-1-test
  (testing "Matt 1:1 has 8 words"
    (let [v1 (ap/verses @matt 1 1 1 1)]
      (is (= 8 (count v1)))))

  (testing "Matt 1:1 starts with ktaba (book)"
    (let [v1 (ap/verses @matt 1 1 1 1)]
      (is (= "ܟܬܒܐ" (:text (first v1)))))))

(deftest genealogy-test
  (testing "Matt 1:1-17 has ~175 words"
    (let [gen (ap/verses @matt 1 1 1 17)]
      (is (<= 170 (count gen) 180)))))

(deftest verse-filter-test
  (testing "verse range filtering works"
    (let [ch1 (ap/verses @matt 1 1 1 25)]
      (is (every? #(= 1 (:chapter %)) ch1))
      (is (= 1 (:verse (first ch1)))))))
