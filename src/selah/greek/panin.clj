(ns selah.greek.panin
  "Ivan Panin's 24 heptadic claims for Matthew 1:1-17, tested programmatically.

   Key findings from investigation:
   - Panin counted vocabulary by LEMMA (dictionary form), not surface form
   - Claims 1-15 use the 49-lemma vocabulary of section 1 (Matt 1:1-11)
   - Claims 16-24 concern proper names in section 1 (Matt 1:1-11)
   - 'Proper name' excludes Χριστός (a title) but includes all personal/place names
   - 'Male genealogical names' excludes Zarah (sibling), Uriah (incidental),
     and Jesus (the subject) — these are non-genealogical male names
   - Female names = the 3 named women (Tamar, Rahab, Ruth) — not Mary
   - Babylon letter count uses the nominative/lemma form (7 letters)"
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [selah.greek.parse :as gp]
            [selah.greek.normalize :as gn])
  (:import [java.text Normalizer Normalizer$Form]))

;; ── Unicode helpers ──────────────────────────────────────────────

(defn- nfc
  "Unicode NFC normalization — needed because Tischendorf uses
   polytonic Greek (U+1F71 oxia) while literal strings use tonos (U+03AC)."
  [s]
  (Normalizer/normalize s Normalizer$Form/NFC))

;; ── Predicates ─────────────────────────────────────────────────────

(defn- noun? [w]
  (and (:morph w) (str/starts-with? (:morph w) "N")))

(defn- article? [w]
  (and (:morph w) (str/starts-with? (:morph w) "T-")))

;; ── Common noun lemmas (everything else that's a noun = proper name) ──

(def ^:private common-noun-lemmas
  "Common nouns in the Matthew 1 genealogy.
   Everything that is a noun and NOT in this set is a proper name."
  (set (map nfc ["βίβλος" "γένεσις" "υἱός" "γενεά"
                  "μετοικεσία" "ἀδελφός" "ἀνήρ" "βασιλεύς"])))

(def ^:private title-lemmas
  "Titles — proper-noun-like but not personal/place names.
   Panin excluded Χριστός from his proper name count."
  (set (map nfc ["Χριστός"])))

(defn- proper-name?
  "A word is a proper name if it's a noun that's not a common noun
   and not a title. This captures both PRI-tagged indeclinable names
   and declinable proper nouns (Ἰησοῦς, Σολομών, etc.)."
  [w]
  (and (noun? w)
       (:lemma w)
       (not (contains? common-noun-lemmas (nfc (:lemma w))))
       (not (contains? title-lemmas (nfc (:lemma w))))))

;; ── Vocabulary helpers ─────────────────────────────────────────────

(defn vocabulary
  "Distinct lemmas in a word sequence. Returns set of lemma strings."
  [words]
  (set (remove nil? (map :lemma words))))

(defn vocab-lemma-forms
  "Normalized lemma forms (one per distinct lemma). Returns seq of strings.
   Uses the lemma/dictionary form, not the first surface occurrence."
  [words]
  (map (fn [[lem _]] (gn/normalize lem))
       (group-by :lemma (filter :lemma words))))

(defn vocab-nouns
  "Distinct noun lemmas."
  [words]
  (set (map :lemma (filter noun? words))))

(defn vocab-non-nouns
  "Distinct non-noun lemmas."
  [words]
  (set/difference (vocabulary words) (vocab-nouns words)))

;; ── Frequency helpers ──────────────────────────────────────────────

(defn lemma-frequencies
  "Map of lemma -> occurrence count."
  [words]
  (frequencies (remove nil? (map :lemma words))))

(defn lemma-form-counts
  "Map of lemma -> number of distinct surface forms."
  [words]
  (into {} (map (fn [[lem ws]]
                  [lem (count (distinct (map #(gn/normalize (:text %)) ws)))])
                (group-by :lemma (filter :lemma words)))))

;; ── Name helpers ───────────────────────────────────────────────────

(def ^:private female-name-lemmas
  "The 3 named women in the Matthew 1 genealogy.
   Mary appears later (1:16) but is not counted among the genealogy women.
   Bathsheba is not named — the text says 'the [wife] of Uriah'."
  (set (map nfc ["Θαμάρ" "Ῥαχάβ" "Ῥούθ"])))

(def ^:private non-person-name-lemmas
  "Non-person proper nouns (place names)."
  (set (map nfc ["Βαβυλών"])))

(def ^:private non-genealogical-male-lemmas
  "Male names that are NOT in the genealogical 'begat' chain.
   Panin classified these as 'non-male' for his 28/7 split:
   - Ζάρα (Zarah): twin of Pharez, mentioned alongside but not in the line
   - Οὐρίας (Uriah): mentioned only to identify Bathsheba
   - Ἰησοῦς (Jesus): the subject/endpoint, not a link in the chain"
  (set (map nfc ["Ζάρα" "Οὐρίας" "Ἰησοῦς"])))

(defn- male-genealogical-lemmas
  "Male names IN the genealogical line (the 'begat' chain).
   Excludes female, non-person, and non-genealogical male names."
  [words]
  (let [all-proper (set (map #(nfc (:lemma %)) (filter proper-name? words)))]
    (set/difference all-proper
                    female-name-lemmas
                    non-person-name-lemmas
                    non-genealogical-male-lemmas)))

;; ── The 24 claims ─────────────────────────────────────────────────

(defn evaluate-claims
  "Evaluate all 24 of Panin's heptadic claims against a parsed Matthew.
   Returns a vector of {:n :claim :panin :actual}.

   All claims concern section 1 (Matt 1:1-11) except claims 1-3 which
   use the full genealogy (Matt 1:1-17)."
  [variant]
  (let [all-words (gp/load-matthew variant)
        gen17 (vec (gp/verses all-words 1 1 1 17))
        sec1  (vec (gp/verses all-words 1 1 1 11))

        ;; ── Section 1 vocabulary (1:1-11, by lemma) ──
        vocab-11  (vocabulary sec1)

        ;; Vocabulary lemma forms (dictionary form, not surface form)
        lem-forms (vocab-lemma-forms sec1)
        lem-str   (apply str lem-forms)

        ;; Noun/non-noun split of the vocabulary
        noun-lem-11  (vocab-nouns sec1)
        non-noun-11  (vocab-non-nouns sec1)

        ;; Frequency and form counts — scoped to 1:1-11 (the 49 vocab words)
        lem-freq   (lemma-frequencies sec1)
        multi-use  (count (filter #(> (val %) 1) lem-freq))
        single-use (count (filter #(= (val %) 1) lem-freq))

        form-cts   (lemma-form-counts sec1)
        single-frm (count (filter #(= 1 (val %)) form-cts))
        multi-frm  (count (filter #(> (val %) 1) form-cts))

        ;; ── Full genealogy (1:1-17) — used for claims 1-3 ──

        ;; Noun lemmas across full passage
        noun-lem-17 (vocab-nouns gen17)

        ;; Articles (full passage)
        arts      (filter article? gen17)
        art-forms (distinct (map #(gn/normalize (:text %)) arts))

        ;; ── Proper names — scoped to 1:1-11 (like all vocabulary claims) ──

        propers     (filter proper-name? sec1)
        proper-lems (set (map #(nfc (:lemma %)) propers))
        male-lems   (male-genealogical-lemmas sec1)
        non-male-lems (set/difference proper-lems male-lems)

        ;; Male genealogical name occurrences
        male-occ (count (filter #(contains? male-lems (nfc (:lemma %))) propers))

        ;; Female name letters — use LEMMA form (dictionary form)
        female-letter-count (reduce + (map #(count (gn/normalize %))
                                           female-name-lemmas))

        ;; Babylon — use LEMMA form (nominative: Βαβυλών = 7 letters)
        babylon-letters (if (contains? proper-lems (nfc "Βαβυλών"))
                          (count (gn/normalize "Βαβυλών"))
                          0)]

    [{:n 1  :claim "Nouns (unique lemmas in 1:1-17)"
      :panin 56 :actual (count noun-lem-17)}
     {:n 2  :claim "Article 'the' occurrences"
      :panin 56 :actual (count arts)}
     {:n 3  :claim "Article distinct forms"
      :panin 7  :actual (count art-forms)}
     {:n 4  :claim "Vocabulary words in 1:1-11 (by lemma)"
      :panin 49 :actual (count vocab-11)}
     {:n 5  :claim "Vocab words beginning with vowel"
      :panin 28 :actual (count (filter #(and (seq %) (gn/vowel? (first %))) lem-forms))}
     {:n 6  :claim "Vocab words beginning with consonant"
      :panin 21 :actual (count (filter #(and (seq %) (gn/consonant? (first %))) lem-forms))}
     {:n 7  :claim "Letters in the 49 vocab words"
      :panin 266 :actual (count lem-str)}
     {:n 8  :claim "Vowels in the 49 vocab words"
      :panin 140 :actual (count (filter gn/vowel? lem-str))}
     {:n 9  :claim "Consonants in the 49 vocab words"
      :panin 126 :actual (count (filter gn/consonant? lem-str))}
     {:n 10 :claim "Lemmas occurring more than once (in 1:1-11)"
      :panin 35 :actual multi-use}
     {:n 11 :claim "Lemmas occurring only once (in 1:1-11)"
      :panin 14 :actual single-use}
     {:n 12 :claim "Lemmas appearing in single form only (in 1:1-11)"
      :panin 42 :actual single-frm}
     {:n 13 :claim "Lemmas appearing in multiple forms (in 1:1-11)"
      :panin 7  :actual multi-frm}
     {:n 14 :claim "Noun vocab in 1:1-11 (of the 49)"
      :panin 42 :actual (count noun-lem-11)}
     {:n 15 :claim "Non-noun vocab in 1:1-11 (of the 49)"
      :panin 7  :actual (count non-noun-11)}
     {:n 16 :claim "Proper name lemmas (excl. Χριστός)"
      :panin 35 :actual (count proper-lems)}
     {:n 17 :claim "Proper name total uses"
      :panin 63 :actual (count propers)}
     {:n 18 :claim "Male genealogical name lemmas"
      :panin 28 :actual (count male-lems)}
     {:n 19 :claim "Male genealogical name occurrences"
      :panin 56 :actual male-occ}
     {:n 20 :claim "Non-male name lemmas (female+place+other)"
      :panin 7  :actual (count non-male-lems)}
     {:n 21 :claim "Letters in women's names (Tamar+Rahab+Ruth)"
      :panin 14 :actual female-letter-count}
     {:n 22 :claim "Compound nouns"
      :panin 7  :actual nil}  ; ambiguous definition
     {:n 23 :claim "Letters in compound nouns"
      :panin 49 :actual nil}  ; ambiguous definition
     {:n 24 :claim "Letters in 'Babylon' (lemma form)"
      :panin 7  :actual babylon-letters}]))

(defn print-claims
  "Print a formatted report of all 24 claims."
  [claims variant-name]
  (println)
  (println (format "=== Panin's 24 Claims — %s ===" variant-name))
  (println (format "%-4s %-50s %7s %7s %s"
                   "#" "Claim" "Panin" "Actual" ""))
  (println (apply str (repeat 80 "-")))
  (let [testable (filter #(some? (:actual %)) claims)
        matched  (count (filter #(= (:panin %) (:actual %)) testable))]
    (doseq [{:keys [n claim panin actual]} claims]
      (if (nil? actual)
        (println (format "%-4d %-50s %7d %7s   —"
                         n claim panin "?"))
        (println (format "%-4d %-50s %7d %7d   %s"
                         n claim panin actual
                         (cond
                           (= panin actual) "✓"
                           (zero? (mod actual 7)) (format "✗ (≠%d but still ÷7)" panin)
                           :else (format "✗ (off by %+d)" (- actual panin)))))))
    (println (apply str (repeat 80 "-")))
    (println (format "Confirmed: %d / %d testable claims" matched (count testable)))
    (println (format "Also ÷7:   %d / %d testable claims"
                     (count (filter #(and (some? (:actual %))
                                          (zero? (mod (:actual %) 7))) claims))
                     (count testable)))))

(defn compare-variants
  "Run all 24 claims across multiple variants."
  []
  (doseq [v [:tischendorf :sblgnt :westcott-hort :textus-receptus]]
    (print-claims (evaluate-claims v) (name v))))

(comment
  (compare-variants)

  ;; Single variant
  (print-claims (evaluate-claims :tischendorf) "Tischendorf")
  )
