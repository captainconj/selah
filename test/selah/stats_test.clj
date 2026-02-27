(ns selah.stats-test
  (:require [clojure.test :refer [deftest testing is are]]
            [selah.els :as els]
            [selah.stats :as stats]
            [selah.text.sefaria :as sefaria]))

;; ── Load all five books (cached) ────────────────────────────────────

(def ^:private books
  (delay
    {:genesis     (sefaria/book-letters "Genesis")
     :exodus      (sefaria/book-letters "Exodus")
     :leviticus   (sefaria/book-letters "Leviticus")
     :numbers     (sefaria/book-letters "Numbers")
     :deuteronomy (sefaria/book-letters "Deuteronomy")}))

(defn- book [k] (get @books k))

;; ── Expected vs actual hit counts ───────────────────────────────────

(deftest expected-hits-test
  (testing "expected hits are in the right ballpark (1-5 range)"
    (are [b word skip lo hi]
      (let [expected (stats/expected-hits (book b) word skip)]
        (and (>= expected lo) (<= expected hi)))
      :genesis     "תורה"  50   1.0 5.0
      :exodus      "תורה"  50   1.0 5.0
      :leviticus   "יהוה"   7   2.0 8.0
      :numbers     "תורה" -50   0.5 4.0
      :deuteronomy "תורה" -50   0.5 4.0)))

(deftest exodus-skip-50-anomaly
  (testing "Exodus has significantly more hits at skip +50 than expected"
    (let [expected (stats/expected-hits (book :exodus) "תורה" 50)
          actual   (count (els/search (book :exodus) "תורה" 50))]
      (is (>= actual (* 3 expected))
          "Exodus actual hits should be 3x+ the expected count"))))

;; ── Positional significance ─────────────────────────────────────────

(deftest genesis-positional-test
  (testing "P(first hit in first 10 letters) is very small for Genesis"
    (let [p (stats/p-first-hit-in-first-n (book :genesis) "תורה" 10)]
      (is (< p 1e-3)
          "Should be less than 1 in 1000"))))

(deftest exodus-positional-test
  (testing "P(first hit in first 10 letters) is very small for Exodus"
    (let [p (stats/p-first-hit-in-first-n (book :exodus) "תורה" 10)]
      (is (< p 1e-3)
          "Should be less than 1 in 1000"))))

(deftest combined-positional-test
  (testing "P(both Gen and Exod first hit in first 10) < 1 in a million"
    (let [p-gen (stats/p-first-hit-in-first-n (book :genesis) "תורה" 10)
          p-exo (stats/p-first-hit-in-first-n (book :exodus) "תורה" 10)
          p-both (* p-gen p-exo)]
      (is (< p-both 1e-6)
          "Combined positional probability should be < 1 in a million"))))

;; ── Shuffled controls ───────────────────────────────────────────────

(deftest genesis-shuffled-position-test
  (testing "shuffled Genesis very rarely produces first hit in first 10 letters (100 trials)"
    (let [result (stats/shuffled-controls (book :genesis) "תורה" 50 100)]
      (is (<= (:first-in-10 result) 3)
          "At most a handful of shuffled trials should have first hit in first 10 letters"))))

(deftest exodus-shuffled-count-test
  (testing "shuffled Exodus rarely produces >= 8 hits at skip +50 (100 trials)"
    (let [result (stats/shuffled-controls (book :exodus) "תורה" 50 100)]
      (is (< (:p-gte result) 0.05)
          "p < 0.05 for shuffled streams matching Exodus hit count"))))

;; ── Mirror pattern ──────────────────────────────────────────────────

(deftest mirror-pattern-test
  (testing "mirror pattern does not appear in shuffled books (100 trials)"
    (let [trials  100
          hits    (count (filter true?
                   (repeatedly trials
                     #(stats/mirror-trial
                        (book :genesis)
                        (book :exodus)
                        (book :numbers)
                        (book :deuteronomy)))))]
      (is (zero? hits)
          "Mirror pattern should not appear in any shuffled trial"))))

;; ── Full chiastic pattern ───────────────────────────────────────────

(deftest chiastic-pattern-test
  (testing "full chiastic pattern does not appear in shuffled books (100 trials)"
    (let [trials  100
          hits    (count (filter true?
                   (repeatedly trials
                     #(stats/chiastic-trial
                        (book :genesis)
                        (book :exodus)
                        (book :leviticus)
                        (book :numbers)
                        (book :deuteronomy)))))]
      (is (zero? hits)
          "Full chiastic pattern should not appear in any shuffled trial"))))

;; ── Compound probability ────────────────────────────────────────────

(deftest compound-probability-test
  (testing "compound probability is less than 1 in 10 million"
    (let [result (stats/compound-probability
                   (book :genesis) (book :exodus) (book :leviticus)
                   (book :numbers) (book :deuteronomy))]
      (is (< (:p-compound result) 1e-7)
          "Compound probability should be < 1e-7")
      (is (> (:one-in result) 10000000)
          "Should be 1 in > 10 million"))))

;; ── The skip values carry meaning ───────────────────────────────────

(deftest skip-50-is-shavuot
  (testing "skip 50 = days from Exodus to Sinai (7 × 7 + 1)"
    (is (= 50 (+ (* 7 7) 1))
        "The Omer: 7 weeks of 7 days, plus 1")))

(deftest skip-7-is-creation
  (testing "skip 7 = days of creation / Sabbath cycle"
    (is (= 7 7)
        "The rhythm of the text's own theology")))

;; ── Surface text: תורה frequency across books ──────────────────────

(deftest torah-surface-frequency
  (testing "תורה does not appear in Genesis surface text at all"
    (is (zero? (count (els/search (book :genesis) "תורה" 1)))
        "The word Torah is absent from the plain text of Genesis"))

  (testing "תורה surface frequency increases across the Pentateuch"
    (let [counts (mapv #(count (els/search (book %) "תורה" 1))
                       [:genesis :exodus :leviticus :numbers :deuteronomy])]
      (is (= counts (sort counts))
          "Surface occurrences should increase: Gen < Exod < Lev < Num < Deut"))))

;; ── Divine name saturation in Leviticus ─────────────────────────────

(deftest leviticus-yhwh-saturation
  (testing "Leviticus contains hundreds of surface יהוה occurrences"
    (let [surface-count (count (els/search (book :leviticus) "יהוה" 1))]
      (is (> surface-count 300)
          "יהוה should appear 300+ times in Leviticus surface text")))

  (testing "יהוה at skip 7 in Leviticus exceeds expected count"
    (let [expected (stats/expected-hits (book :leviticus) "יהוה" 7)
          actual   (count (els/search (book :leviticus) "יהוה" 7))]
      (is (> actual expected)
          "Actual should exceed expected"))))
