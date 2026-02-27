(ns selah.greek.normalize-test
  (:require [clojure.test :refer [deftest testing is are]]
            [selah.greek.normalize :as gn]))

(deftest normalize-test
  (testing "strips accents, breathing marks, and punctuation"
    (is (= "βιβλοςγενεσεωςιησουχριστου"
           (gn/normalize "Βίβλος γενέσεως Ἰησοῦ Χριστοῦ"))))

  (testing "empty input"
    (is (= "" (gn/normalize ""))))

  (testing "no Greek letters"
    (is (= "" (gn/normalize "hello world 123")))))

(deftest greek-letter-test
  (testing "identifies Greek letters"
    (is (gn/greek-letter? \α))
    (is (gn/greek-letter? \Ω))
    (is (gn/greek-letter? \ς))  ; final sigma
    (is (not (gn/greek-letter? \a)))
    (is (not (gn/greek-letter? \space)))))

(deftest vowel-consonant-test
  (testing "vowel classification"
    (are [ch] (gn/vowel? ch)
      \α \ε \η \ι \ο \υ \ω))

  (testing "consonant classification"
    (are [ch] (gn/consonant? ch)
      \β \γ \δ \ζ \θ \κ \λ \μ \ν \ξ \π \ρ \σ \ς \τ \φ \χ \ψ))

  (testing "counts"
    (is (= 4 (gn/count-vowels "Ἰησοῦς")))     ; ι η ο υ
    (is (= 2 (gn/count-consonants "Ἰησοῦς"))))) ; σ ς → both σ after normalize... wait

(deftest isopsephy-test
  (testing "famous values"
    (is (= 888 (gn/isopsephy "Ἰησοῦς")))
    (is (= 1 (gn/isopsephy "α")))
    (is (= 800 (gn/isopsephy "ω"))))

  (testing "word isopsephy returns pairs"
    (let [pairs (gn/word-isopsephy "Βίβλος γενέσεως")]
      (is (= 2 (count pairs)))
      (is (= "Βιβλος" (first (first pairs)))))))

(deftest robinson-test
  (testing "converts transliterated Greek to Unicode"
    (is (= "ιησους" (gn/robinson->unicode "ihsouv")))
    (is (= "βιβλος" (gn/robinson->unicode "biblov")))
    (is (= "χριστου" (gn/robinson->unicode "cristou")))))
