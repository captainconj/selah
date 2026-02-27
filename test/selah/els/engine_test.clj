(ns selah.els.engine-test
  (:require [clojure.test :refer [deftest testing is are]]
            [selah.els.engine :as els]
            [selah.text.normalize :as norm]
            [selah.text.sefaria :as sefaria]))

;; ── Unit tests (no network) ───────────────────────────────────────

(deftest normalize-test
  (testing "strips niqqud, cantillation, spaces — keeps only letters"
    (is (= "בראשיתבראאלהים"
           (norm/normalize "בְּרֵאשִׁ֖ית בָּרָ֣א אֱלֹהִ֑ים"))))

  (testing "preserves final forms"
    (is (= "אםץ"
           (norm/normalize "אם ץ"))))

  (testing "empty input"
    (is (= "" (norm/normalize ""))))

  (testing "no Hebrew letters"
    (is (= "" (norm/normalize "hello world 123")))))

(deftest extract-test
  (let [stream (norm/letter-stream "אבגדהוזחטיכלמנסעפצקרשת")]
    (testing "forward extraction"
      (is (= "אגה" (els/extract stream 0 2 3))))

    (testing "reverse extraction"
      (is (= "תשר" (els/extract stream 21 -1 3))))

    (testing "skip of 1 is consecutive letters"
      (is (= "אבגד" (els/extract stream 0 1 4))))

    (testing "out of bounds returns nil"
      (is (nil? (els/extract stream 20 5 3))))))

(deftest search-test
  (let [stream (norm/letter-stream "תורהאבגדתורה")]
    (testing "finds word at start and end"
      (let [hits (els/search stream "תורה" 1)]
        (is (= 2 (count hits)))
        (is (= 0 (:start (first hits))))
        (is (= 8 (:start (second hits))))))))

(deftest verify-positions-test
  (let [stream (norm/letter-stream "אבגדהוזחטיכלמנסעפצקרשת")]
    (testing "1-based position extraction"
      (let [result (els/verify-positions stream [1 3 5])]
        (is (= "אגה" (:word result)))
        (is (true? (:constant-step? result)))
        (is (= 2 (:step result)))))))

;; ── Torah Codes integration tests (require cached Sefaria data) ──

(def ^:private torah-books
  (delay
    {:genesis     (sefaria/book-letters "Genesis")
     :exodus      (sefaria/book-letters "Exodus")
     :leviticus   (sefaria/book-letters "Leviticus")
     :numbers     (sefaria/book-letters "Numbers")
     :deuteronomy (sefaria/book-letters "Deuteronomy")}))

(defn- torah [book] (get @torah-books book))

;; ── The chiastic pattern ──────────────────────────────────────────
;;
;;   Genesis      →  תורה  forward, skip 50
;;   Exodus       →  תורה  forward, skip 50
;;   Leviticus    →  יהוה  forward, skip 7    (center)
;;   Numbers      →  תורה  reverse, skip -50
;;   Deuteronomy  →  תורה  reverse, skip -50

(deftest genesis-torah-forward
  (testing "Genesis: תורה appears at skip +50, first hit near the beginning"
    (let [hits (els/search (torah :genesis) "תורה" 50)]
      (is (pos? (count hits))
          "תורה should appear at least once at skip 50")
      (is (= 5 (:start (first hits)))
          "First occurrence starts at 0-based index 5 (position 6)"))))

(deftest genesis-published-positions
  (testing "Genesis: published 1-based positions [6 56 106 156] spell תורה"
    (let [result (els/verify-positions (torah :genesis) [6 56 106 156])]
      (is (= "תורה" (:word result)))
      (is (true? (:constant-step? result)))
      (is (= 50 (:step result))))))

(deftest exodus-torah-forward
  (testing "Exodus: תורה appears at skip +50"
    (let [hits (els/search (torah :exodus) "תורה" 50)]
      (is (pos? (count hits))
          "תורה should appear at least once at skip 50"))))

(deftest leviticus-yhwh-center
  (testing "Leviticus (center book): יהוה appears at skip +7"
    (let [hits (els/search (torah :leviticus) "יהוה" 7)]
      (is (pos? (count hits))
          "יהוה should appear at least once at skip 7 in the center book"))))

(deftest numbers-torah-reverse
  (testing "Numbers: תורה appears at skip -50 (reversed)"
    (let [hits (els/search (torah :numbers) "תורה" -50)]
      (is (pos? (count hits))
          "תורה should appear at least once at skip -50"))))

(deftest deuteronomy-torah-reverse
  (testing "Deuteronomy: תורה appears at skip -50 (reversed)"
    (let [hits (els/search (torah :deuteronomy) "תורה" -50)]
      (is (pos? (count hits))
          "תורה should appear at least once at skip -50"))))

(deftest chiastic-structure
  (testing "The full chiastic pattern: Torah forward outside, YHWH at center, Torah reversed inside"
    (let [gen-fwd  (els/search (torah :genesis) "תורה" 50)
          exo-fwd  (els/search (torah :exodus) "תורה" 50)
          lev-yhwh (els/search (torah :leviticus) "יהוה" 7)
          num-rev  (els/search (torah :numbers) "תורה" -50)
          deu-rev  (els/search (torah :deuteronomy) "תורה" -50)]

      (are [book hits] (pos? (count hits))
        "Genesis"     gen-fwd
        "Exodus"      exo-fwd
        "Leviticus"   lev-yhwh
        "Numbers"     num-rev
        "Deuteronomy" deu-rev)

      (testing "outer books point forward, inner books point backward — toward the center"
        (is (every? #(= 50 (:skip %)) gen-fwd))
        (is (every? #(= 50 (:skip %)) exo-fwd))
        (is (every? #(= -50 (:skip %)) num-rev))
        (is (every? #(= -50 (:skip %)) deu-rev))))))
