(ns scratch.093i-rambans-answer
  "Experiment 093i — The Ramban's answer: phrase parsing.

   Judges 1:1-2: 'Who shall go up first?'
   Answer: יהודה יעלה (Judah shall go up).

   The same 9 letters {י×2, ה×3, ו×1, ד×1, ע×1, ל×1} yield
   12 phrase parsings — all permutations of {יהוה/והיה, יד, עלה}.
   All 12 have GV = 145.

   Reproduces findings from docs/093i-rambans-answer.md"
  (:require [selah.oracle :as oracle]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]))

(defn run []
  (println "═══════════════════════════════════════════")
  (println " 093i — The Ramban's Answer")
  (println "═══════════════════════════════════════════")

  ;; The Ramban's example: יהודה יעלה (Judah shall go up)
  ;; Combined letters: י ה ו ד ה י ע ל ה
  (let [judah   "יהודה"
        go-up   "יעלה"
        letters (str judah go-up)]

    (println (str "\nQuestion: Who shall go up first? (Judges 1:1-2)"))
    (println (str "Answer recorded: " judah " " go-up " (Judah shall go up)"))
    (println (str "Combined letters: " letters))
    (println (str "Letter frequencies: " (frequencies (seq letters))))
    (println (str "GV of combined letters: " (g/word-value letters)))

    ;; ── Parse with :dict vocabulary ─────────────────────────────
    (println "\n── :dict vocabulary (239 curated words) ──\n")

    (let [dict-results (oracle/parse-letters letters
                                              {:vocab :dict
                                               :max-words 4
                                               :min-letters 2})
          gv-set       (set (map :gv dict-results))]

      (println (str (count dict-results) " phrase parsings found.\n"))

      (doseq [{:keys [text meanings gv]} dict-results]
        (println (format "  %-20s  (%s)  GV=%d"
                         text
                         (str/join ", " meanings)
                         gv)))

      (println (str "\nDistinct GV values: " (sort gv-set)))
      (if (= gv-set #{145})
        (println "ALL parsings have GV = 145.")
        (println (str "WARNING: not all GV=145. Found: " gv-set)))

      ;; Check for the key phrase
      (println "\n── Looking for: יהוה יד עלה ──")
      (let [target (filter #(= (:text %) "יד יהוה עלה") dict-results)
            target2 (filter #(= (:text %) "יהוה יד עלה") dict-results)]
        (if (or (seq target) (seq target2))
          (println "  FOUND: the hand of YHWH is the offering.")
          (println "  (Check orderings — the words {יהוה, יד, עלה} should appear in some order.)"))
        (let [has-yhwh (filter #(some #{"יהוה"} (:phrase %)) dict-results)
              has-yad  (filter #(some #{"יד"} (:phrase %)) dict-results)
              has-olah (filter #(some #{"עלה"} (:phrase %)) dict-results)]
          (println (str "  Phrases containing יהוה: " (count has-yhwh)))
          (println (str "  Phrases containing יד:   " (count has-yad)))
          (println (str "  Phrases containing עלה:  " (count has-olah)))))

      ;; The three component words
      (println "\n── The three words ──")
      (println (str "  יהוה (YHWH) — the Name, GV=" (g/word-value "יהוה")))
      (println (str "  יד   (hand) — GV=" (g/word-value "יד") " = GV(דוד/David)=14"))
      (println (str "  עלה  (go up / burnt offering) — GV=" (g/word-value "עלה")))
      (println (str "  Sum: " (+ (g/word-value "יהוה") (g/word-value "יד") (g/word-value "עלה"))))

      ;; GV = 145 analysis
      (println "\n── 145 ──")
      (println "  145 = 5 × 29 (hand × prime)")
      (println "  1² + 4² + 5² = 1 + 16 + 25 = 42 (narcissistic)")
      (println "  42 = generations Abraham→Christ (Matt 1:17)"))

    ;; ── Parse with :torah vocabulary ────────────────────────────
    (println "\n\n── :torah vocabulary (full Torah lexicon) ──\n")

    (let [torah-results (oracle/parse-letters letters
                                               {:vocab :torah
                                                :max-words 4
                                                :min-letters 2})
          gv-set        (set (map :gv torah-results))]

      (println (str (count torah-results) " phrase parsings found.\n"))

      ;; Show first 30, note if there are more
      (doseq [{:keys [text meanings gv]} (take 30 torah-results)]
        (println (format "  %-25s  (%s)  GV=%d"
                         text
                         (str/join ", " (map #(or % "?") meanings))
                         gv)))
      (when (> (count torah-results) 30)
        (println (str "  ... and " (- (count torah-results) 30) " more.")))

      (println (str "\nDistinct GV values: " (sort gv-set)))
      (if (= gv-set #{145})
        (println "ALL torah parsings also have GV = 145. (Invariant holds.)")
        (println (str "Torah parsings GV range: " (apply min gv-set) " to " (apply max gv-set)))))

    ;; ── Note about Level 1 vs Level 2 ──────────────────────────
    (println "\n═══════════════════════════════════════════")
    (println " The Ramban identified two levels:")
    (println "   Level 1: traversal order (mechanical)")
    (println "   Level 2: cognitive assembly (the priest)")
    (println " No mechanical traversal produces 'Judah shall go up.'")
    (println " The priest must choose. The Thummim provides the menu.")
    (println " The menu says: the hand of YHWH is the offering.")
    (println "═══════════════════════════════════════════")))
