(ns selah.aramaic.normalize-test
  (:require [clojure.test :refer [deftest testing is]]
            [selah.aramaic.normalize :as an]))

(deftest normalize-test
  (testing "strips non-Syriac characters"
    (is (= "ܟܬܒܐܕܝܠܝܕܘܬܗ"
           (an/normalize "ܟܬܒܐ ܕܝܠܝܕܘܬܗ"))))

  (testing "empty input"
    (is (= "" (an/normalize ""))))

  (testing "no Syriac letters"
    (is (= "" (an/normalize "hello 123")))))

(deftest syriac-letter-test
  (testing "identifies Syriac letters"
    (is (an/syriac-letter? \ܐ))  ; Alaph
    (is (an/syriac-letter? \ܬ))  ; Taw
    (is (not (an/syriac-letter? \a)))
    (is (not (an/syriac-letter? \space)))))

(deftest gematria-test
  (testing "Yeshua = 386"
    ;; ܝ=10 + ܫ=300 + ܘ=6 + ܥ=70 = 386
    (is (= 386 (an/gematria "ܝܫܘܥ"))))

  (testing "Meshikha = 359"
    ;; ܡ=40 + ܫ=300 + ܝ=10 + ܚ=8 + ܐ=1 = 359
    (is (= 359 (an/gematria "ܡܫܝܚܐ"))))

  (testing "word gematria returns pairs"
    (let [pairs (an/word-gematria "ܝܫܘܥ ܡܫܝܚܐ")]
      (is (= 2 (count pairs)))
      (is (= 386 (second (first pairs))))
      (is (= 359 (second (second pairs)))))))

(deftest matres-test
  (testing "matres lectionis counting"
    ;; ܐܒܪܗܡ has one Alaph (mater)
    (is (= 1 (an/count-matres "ܐܒܪܗܡ")))
    (is (= 4 (an/count-pure-consonants "ܐܒܪܗܡ")))))
