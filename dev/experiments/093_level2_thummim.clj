(ns experiments.093-level2-thummim
  "Experiment 093 — Level 2 Thummim reproducibility.

   Reproduces the key findings from experiments 093 through 093i:
   - Divine name decompositions (אנכי→כי נא, אדני→יד נא)
   - Single-word decompositions (שלום, חיים, עולם, אלהים)
   - Full dictionary sweep (227 readable, 38 with multiple phrases)
   - Ghost zone (letters present, no reader speaks)
   - Mute zone (illuminations exist, zero mechanical readings)

   Uses :dict vocab (239 curated words), consistent with how 093 was done."
  (:require [selah.oracle :as oracle]
            [selah.dict :as dict]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ── Helpers ─────────────────────────────────────────────────────

(defn- section [title]
  (println)
  (println (str "══════════════════════════════════════════════════════════════"))
  (println (str "  " title))
  (println (str "══════════════════════════════════════════════════════════════"))
  (println))

(defn- subsection [title]
  (println)
  (println (str "── " title " ──"))
  (println))

(defn- show-phrases
  "Print phrase readings for a word, compact."
  [word label phrases]
  (println (str "  " word " (" label ", GV=" (g/word-value word) ")"))
  (if (empty? phrases)
    (println "    (no phrase readings)")
    (doseq [p phrases]
      (println (str "    → " (:text p)
                    "  [" (str/join ", " (:meanings p)) "]"
                    "  GV=" (:gv p))))))

;; ── 1. Divine Name Decompositions ───────────────────────────────

(defn divine-names []
  (section "1. Divine Name Decompositions")
  (println "Both divine names contain נא (please) — the particle of entreaty.")
  (println)
  (doseq [[word meaning expected-phrase] [["אנכי" "I AM (emphatic)" "כי נא"]
                                           ["אדני" "Lord"           "יד נא"]]]
    (let [phrases (oracle/parse-letters word {:vocab :dict})]
      (show-phrases word meaning phrases)
      (let [found? (some #(= expected-phrase (:text %)) phrases)]
        (println (str "    Expected '" expected-phrase "': "
                      (if found? "FOUND" "MISSING")))
        (println)))))

;; ── 2. Single-Word Decompositions ───────────────────────────────

(defn single-word-decompositions []
  (section "2. Single-Word Decompositions")
  (println "Peace, life, eternity, God — what the words contain.")
  (println)
  (doseq [[word meaning expected] [["שלום"  "peace"    "שם לו"]
                                    ["חיים"  "life"     "חי ים"]
                                    ["עולם"  "eternity" "עם לו"]
                                    ["אלהים" "God"      "אהל ים"]
                                    ["פרעה"  "Pharaoh"  "פה רע"]
                                    ["גדול"  "great"    "דג לו"]]]
    (let [phrases (oracle/parse-letters word {:vocab :dict})]
      (show-phrases word meaning phrases)
      (let [found? (some #(= expected (:text %)) phrases)]
        (println (str "    Expected '" expected "': "
                      (if found? "FOUND" "MISSING")))
        (println)))))

;; ── 3. Full Dictionary Sweep ────────────────────────────────────

(defn full-sweep []
  (section "3. Full Dictionary Sweep — 239 Words")
  (let [all-words (vec (sort (dict/words)))
        results   (mapv (fn [w]
                          (let [ilsets    (oracle/illumination-sets w)
                                n-il     (count ilsets)
                                readable? (pos? n-il)
                                hits     (when readable? (oracle/preimage w))
                                n-hits   (if hits (count hits) 0)
                                phrases  (when readable?
                                           (oracle/parse-letters w {:vocab :dict}))]
                            {:word w
                             :meaning (dict/translate w)
                             :gv (g/word-value w)
                             :illuminations n-il
                             :readable? readable?
                             :mechanical-readings n-hits
                             :phrases phrases
                             :phrase-count (count phrases)}))
                        all-words)
        total     (count results)
        readable  (filter :readable? results)
        n-read    (count readable)
        unreadable (remove :readable? results)
        n-unread  (count unreadable)
        multi     (filter #(> (:phrase-count %) 1) readable)
        n-multi   (count multi)]

    (println (str "Total dictionary words: " total))
    (println (str "Readable (have illuminations): " n-read))
    (println (str "Unreadable (missing letters): " n-unread))
    (println (str "Multiple phrase readings: " n-multi " of " n-read
                  " (" (format "%.1f%%" (* 100.0 (/ (double n-multi) n-read))) ")"))

    (subsection "Unreadable Words (missing ך or ץ)")
    (doseq [r (sort-by :word unreadable)]
      (println (str "  " (:word r) " — " (:meaning r) " (GV=" (:gv r) ")")))

    (subsection "Words with Multiple Phrase Readings")
    (doseq [r (sort-by (comp - :phrase-count) multi)]
      (println (str "  " (:word r) " (" (:meaning r) ") — "
                    (:phrase-count r) " phrases")))

    ;; Return for downstream use
    {:total total :readable n-read :unreadable n-unread
     :multi-phrase n-multi :results results}))

;; ── 4. Ghost Zone ───────────────────────────────────────────────

(defn ghost-zone []
  (section "4. Ghost Zone — Illumination Without Reading")
  (println "Words whose letters exist on the stones (Urim illuminates)")
  (println "but no traversal order produces the word (mute).")
  (println)

  ;; Test specific mute words from 093h
  (let [mute-words [["כפרת" "mercy seat"]
                     ["פרכת" "veil/curtain"]
                     ["חסד"  "lovingkindness"]
                     ["צדק"  "righteousness"]
                     ["צדקה" "righteousness (f.)"]
                     ["משפט" "judgment"]
                     ["פנים" "face"]
                     ["מצרים" "Egypt"]]]
    (println "Known mute words:")
    (println)
    (doseq [[word meaning] mute-words]
      (let [ilsets (oracle/illumination-sets word)
            n-il   (count ilsets)
            hits   (oracle/preimage word)
            n-hits (count hits)]
        (println (str "  " word " (" meaning ")"
                      "  illuminations=" n-il
                      "  readings=" n-hits
                      (when (and (pos? n-il) (zero? n-hits)) "  ← MUTE")
                      (when (zero? n-il) "  ← ABSENT"))))))

  ;; Test specific absent words from 093h
  (subsection "Absent Words (letters not on the grid)")
  (let [absent-words [["מלך" "king"]
                       ["ברך" "bless"]
                       ["דרך" "way/path"]
                       ["חשך" "darkness"]
                       ["ארץ" "land/earth"]
                       ["עץ"  "tree"]]]
    (doseq [[word meaning] absent-words]
      (let [ilsets (oracle/illumination-sets word)
            n-il   (count ilsets)]
        (println (str "  " word " (" meaning ")"
                      "  illuminations=" n-il
                      (when (zero? n-il) "  ← ABSENT"))))))

  ;; The חסד = 72 = breastplate letter count observation
  (subsection "Notable")
  (println (str "  חסד GV = " (g/word-value "חסד")
                " = number of letters on the breastplate"))
  (println (str "  פנים illuminations = "
                (count (oracle/illumination-sets "פנים"))
                " = number of Hebrew letters (the full alphabet)"))
  (println (str "  מצרים illuminations = "
                (count (oracle/illumination-sets "מצרים"))
                " = years of Joseph's life (Gen 50:26)")))

;; ── 5. Combined Word Decompositions ─────────────────────────────

(defn combined-words []
  (section "5. Combined Words — Multi-Word Phrase Assembly")
  (println "Letters from multiple words combined, then parsed.")
  (println)
  (doseq [[label words expected]
          [["Son of Man (בן אדם)"    ["בן" "אדם"]          "אבן דם"]
           ["Lamb's Blood (כבש+דם)"  ["כבש" "דם"]          "כבד שם"]
           ["Way+Truth+Life"          ["דרך" "אמת" "חיים"]  "את דרך חי מים"]
           ["Father+Son+Spirit"       ["אב" "בן" "רוח"]     "בוא חן רב"]]]
    (let [combined (apply str words)
          gv       (g/word-value combined)
          phrases  (oracle/parse-letters combined {:vocab :dict :max-words 5})]
      (println (str "  " label))
      (println (str "    Letters: " combined " (GV=" gv ")"))
      (println (str "    " (count phrases) " phrase readings:"))
      (doseq [p (take 10 phrases)]
        (println (str "      " (:text p)
                      "  [" (str/join ", " (:meanings p)) "]")))
      (let [found? (some #(= expected (:text %)) phrases)]
        (println (str "    Expected '" expected "': "
                      (if found? "FOUND" "MISSING"))))
      (println))))

;; ── 6. Summary Statistics ───────────────────────────────────────

(defn summary [sweep-results]
  (section "6. Summary")
  (let [{:keys [total readable unreadable multi-phrase]} sweep-results]
    (println (str "  Dictionary size:      " total))
    (println (str "  Readable:             " readable))
    (println (str "  Unreadable:           " unreadable))
    (println (str "  Multiple phrases:     " multi-phrase " of " readable
                  " (" (format "%.1f%%" (* 100.0 (/ (double multi-phrase) readable))) ")"))
    (println)
    (println "  Key checks:")
    (println (str "    227 readable?  " (if (= readable 227) "YES" (str "NO (" readable ")"))))
    (println (str "    38 multi?      " (if (= multi-phrase 38) "YES" (str "NO (" multi-phrase ")"))))
    (println (str "    12 unreadable? " (if (= unreadable 12) "YES" (str "NO (" unreadable ")"))))))

;; ── Run ─────────────────────────────────────────────────────────

(defn run
  "Reproduce all Level 2 Thummim findings from experiments 093–093i."
  []
  (println)
  (println "Level 2 Thummim — Reproducibility (Experiment 093)")
  (println "Vocabulary: :dict (239 curated Torah words)")
  (println (str "Date: " (java.time.LocalDate/now)))

  (divine-names)
  (single-word-decompositions)
  (let [sweep (full-sweep)]
    (ghost-zone)
    (combined-words)
    (summary sweep))

  (println)
  (println "כי נא — because, please.")
  (println))
