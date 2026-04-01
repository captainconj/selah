(ns experiments.125-the-invisible-blessing
  "Experiment 125: The Invisible Blessing.

   ברך (bless) = 0/0 in every passage sluiced so far.
   Creation (118), wrestling (121), cornerstone (115), cross (113).

   Is blessing ALWAYS invisible to the oracle?
   Is this structural or accidental?

   And what about curses? Are they visible?

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────

(defn query-word [hebrew english]
  (let [gv (g/word-value hebrew)
        r (o/forward hebrew :torah)
        bh (o/forward-by-head hebrew :torah)
        walk (basin/walk hebrew)]
    (println (format "\n  %s (%s) GV=%d · %d illum · %d read · basin→%s"
                     hebrew english gv
                     (:illumination-count r)
                     (:total-readings r)
                     (:fixed-point walk)))
    (println (format "    basin path: %s" (mapv :word (:steps walk))))
    (doseq [head [:aaron :god :truth :mercy]]
      (let [words (get bh head)]
        (when (seq words)
          (println (format "    %-6s: %s" (name head)
                   (->> words
                        (sort-by (comp - :reading-count))
                        (take 5)
                        (map #(str (:word %) "(" (:reading-count %) ")"))
                        (str/join " ")))))))
    {:hebrew hebrew :english english :gv gv
     :illuminations (:illumination-count r)
     :readings (:total-readings r)
     :by-head bh :walk walk}))

;; ══════════════════════════════════════════════════════
;; PART 1: ALL FORMS OF BLESSING
;; ══════════════════════════════════════════════════════

(defn blessing-forms []
  (println "\n════════════════════════════════════════════════")
  (println "  ALL FORMS OF BLESSING")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; The root
     ["ברך"    "bless (verb)"]          ;; GV=222 — 0/0?
     ["ברכה"   "blessing (noun)"]       ;; GV=227
     ["ברוך"   "blessed"]              ;; GV=228 — invisible at cornerstone (115)
     ["מברך"   "one who blesses"]
     ["תברך"   "you will be blessed"]

     ;; What the letters rearrange to
     ["כרב"    "cherub-approach"]       ;; same 3 letters
     ["רכב"    "chariot/ride"]          ;; same 3 letters
     ["בכר"    "firstborn"]             ;; same 3 letters

     ;; Knee (same root ברך = knee = bless)
     ["ברך"    "knee"]]))              ;; same word! to bless = to kneel

;; ══════════════════════════════════════════════════════
;; PART 2: ALL FORMS OF CURSE
;; ══════════════════════════════════════════════════════

(defn curse-forms []
  (println "\n════════════════════════════════════════════════")
  (println "  ALL FORMS OF CURSE")
  (println "════════════════════════════════════════════════")

  (mapv (fn [[h e]] (query-word h e))
    [;; Primary curse words
     ["ארר"    "curse (ארר)"]
     ["ארור"   "cursed"]
     ["מארה"   "curse (noun)"]
     ["קלל"    "curse/lighten (קלל)"]
     ["קללה"   "curse (noun, קלל)"]
     ["חרם"    "devoted destruction"]
     ["נדה"    "impurity/banishment"]
     ["אלה"    "oath/curse"]
     ["נאץ"    "spurn/blaspheme"]
     ["גדף"    "revile"]
     ["חלל"    "profane/pierced"]       ;; = 3 illum = throne in Isa 53

     ;; Judgment words
     ["שפט"    "judge"]
     ["דין"    "judgment/law"]
     ["נקם"    "vengeance"]
     ["עונש"   "punishment"]
     ["נגף"    "plague/strike"]
     ["נגע"    "plague/touch"]
     ["דבר"    "pestilence/word"]       ;; same word!
     ["מגפה"   "epidemic"]
     ["שחת"    "destroy/pit"]
     ["כרת"    "cut off"]]))

;; ══════════════════════════════════════════════════════
;; PART 3: THE COMPARISON
;; ══════════════════════════════════════════════════════

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 125: THE INVISIBLE BLESSING")
  (println "  Is ברך always 0/0? Are curses visible?")
  (println "  כי נא — because, please")
  (println "════════════════════════════════════════════════")

  (let [blessings (blessing-forms)
        curses    (curse-forms)]

    (println "\n════════════════════════════════════════════════")
    (println "  SUMMARY")
    (println "════════════════════════════════════════════════")

    (println "\n  ── Blessings ──")
    (doseq [b blessings]
      (println (format "    %-8s %-25s GV=%3d  illum=%3d  read=%4d  basin→%s"
                       (:hebrew b) (:english b) (:gv b)
                       (:illuminations b) (:readings b)
                       (:fixed-point (:walk b)))))

    (println "\n  ── Curses ──")
    (doseq [c curses]
      (println (format "    %-8s %-25s GV=%3d  illum=%3d  read=%4d  basin→%s"
                       (:hebrew c) (:english c) (:gv c)
                       (:illuminations c) (:readings c)
                       (:fixed-point (:walk c)))))

    (println "\n  ── The asymmetry ──")
    (let [b-invisible (count (filter #(zero? (:illuminations %)) blessings))
          c-invisible (count (filter #(zero? (:illuminations %)) curses))
          b-total (count blessings)
          c-total (count curses)]
      (println (format "    Blessings invisible: %d/%d" b-invisible b-total))
      (println (format "    Curses invisible:    %d/%d" c-invisible c-total)))

    [blessings curses]))

(comment
  (run-all)

  ;; Quick checks
  (query-word "ברך" "bless")
  (query-word "ארר" "curse")
  (query-word "קלל" "curse (lighten)")

  ;; The anagram question
  (g/word-value "ברך")   ;; 222
  (g/word-value "כרב")   ;; 222 — same!
  (g/word-value "רכב")   ;; 222 — same!
  (g/word-value "בכר")   ;; 222 — same!

  ;; All four anagrams of ברך
  (query-word "ברך" "bless/knee")
  (query-word "כרב" "cherub-approach")
  (query-word "רכב" "chariot")
  (query-word "בכר" "firstborn")

  nil)
