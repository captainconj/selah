(ns selah.els.scan-test
  (:require [clojure.test :refer [deftest testing is]]
            [selah.els.scan :as scan]
            [selah.els.stats :as stats]))

;; ── Load books once ─────────────────────────────────────────────────

(def ^:private books (delay (scan/load-books)))

;; ── Full scan ───────────────────────────────────────────────────────

(def ^:private all-results (delay (scan/scan-all @books)))

(deftest scan-produces-results
  (testing "full scan returns results for meaningful words at symbolic skips"
    (is (pos? (count @all-results))
        "Should find at least some hits")))

(deftest torah-at-50-present
  (testing "תורה at skip 50 appears in scan results"
    (let [torah-50 (first (filter #(and (= "תורה" (:word %))
                                         (= 50 (:skip %)))
                                  @all-results))]
      (is (some? torah-50))
      (is (pos? (:total torah-50))))))

(deftest yhwh-at-7-present
  (testing "יהוה at skip 7 appears in scan results"
    (let [yhwh-7 (first (filter #(and (= "יהוה" (:word %))
                                       (= 7 (:skip %)))
                                @all-results))]
      (is (some? yhwh-7))
      (is (pos? (:total yhwh-7))))))

(deftest yhwh-at-26-gematria
  (testing "יהוה at skip 26 (its own gematria) appears across books"
    (let [entry (first (filter #(and (= "יהוה" (:word %))
                                      (= 26 (:skip %)))
                               @all-results))
          positions (scan/first-hit-position entry)
          books-with-hits (count (filter some? (vals positions)))]
      (is (= 5 books-with-hits)
          "יהוה at skip 26 should appear in all five books"))))

;; ── Notable patterns ────────────────────────────────────────────────

(def ^:private notable (delay (scan/notable-patterns @books @all-results)))

(deftest notable-patterns-exist
  (testing "notable patterns are identified"
    (is (pos? (count @notable))
        "Should find at least some notable patterns")))

(deftest moses-is-everywhere
  (testing "משה appears at every symbolic skip in every book"
    (doseq [skip (keys scan/symbolic-skips)]
      (let [entry (first (filter #(and (= "משה" (:word %))
                                        (= skip (:skip %)))
                                 @all-results))
            positions (scan/first-hit-position entry)
            books-with-hits (count (filter some? (vals positions)))]
        (is (= 5 books-with-hits)
            (format "משה at skip %d should appear in all 5 books" skip))))))

;; ── Symbolic skip self-reference ────────────────────────────────────

(deftest sabbath-at-7
  (testing "שבת (Sabbath) at skip 7 (the day of rest) appears in all books"
    (let [entry (first (filter #(and (= "שבת" (:word %))
                                      (= 7 (:skip %)))
                               @all-results))
          positions (scan/first-hit-position entry)
          books-with-hits (count (filter some? (vals positions)))]
      (is (= 5 books-with-hits)
          "Sabbath at skip 7 should appear in all five books"))))

(deftest covenant-at-12
  (testing "ברית (Covenant) at skip 12 (tribes of Israel)"
    (let [entry (first (filter #(and (= "ברית" (:word %))
                                      (= 12 (:skip %)))
                               @all-results))]
      (is (some? entry))
      (is (pos? (:total entry))))))

;; ── Cross-book presence ─────────────────────────────────────────────

(deftest words-present-in-all-five-books
  (testing "which words appear in all 5 books at each symbolic skip"
    (doseq [{:keys [word english skip]} @all-results
            :let [positions (scan/first-hit-position
                              (first (filter #(and (= word (:word %))
                                                    (= skip (:skip %)))
                                            @all-results)))
                  in-all? (= 5 (count (filter some? (vals positions))))]]
      ;; Just verify the data structure is correct
      (is (map? positions)))))

;; ── Statistical comparison ──────────────────────────────────────────

(deftest exodus-torah-skip-50-anomaly
  (testing "Exodus תורה at skip 50 remains a statistical anomaly in broader scan"
    (let [entry (first (filter #(and (= "תורה" (:word %))
                                      (= 50 (:skip %)))
                               @all-results))
          exodus-total (:total (get (:results entry) "Exodus"))
          expected (stats/expected-hits (get @books "Exodus") "תורה" 50)]
      (is (> exodus-total (* 2.5 expected))
          "Exodus should have 2.5x+ expected hits"))))
