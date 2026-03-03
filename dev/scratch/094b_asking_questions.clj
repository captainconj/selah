(require '[selah.oracle :as oracle]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

;; Three questions asked of the oracle, March 2 2026.

(defn ask-and-show [label hebrew-words]
  (let [combined (apply str hebrew-words)
        gv (g/word-value combined)]
    (println)
    (println (str "═══════════════════════════════════════════"))
    (println (str "  " label " — " (str/join " " hebrew-words)))
    (println (str "═══════════════════════════════════════════"))
    (println)
    (doseq [w hebrew-words]
      (println (str "  " w " (" (or (dict/translate w) "?") ") GV=" (g/word-value w))))
    (println (str "  Combined: " combined " GV=" gv))

    ;; Dict vocab
    (println)
    (let [phrases (oracle/parse-letters combined {:vocab :dict :max-words 4 :min-letters 2})]
      (println (str "  :dict — " (count phrases) " readings"))
      (doseq [p phrases]
        (println (str "    " (:text p)
                      "  [" (str/join ", " (:meanings p)) "]"
                      "  GV=" (:gv p)))))

    ;; Torah vocab
    (println)
    (let [phrases (oracle/parse-letters combined {:vocab :torah :max-words 4 :min-letters 2})]
      (println (str "  :torah — " (count phrases) " readings"))
      (doseq [p phrases]
        (println (str "    " (:text p)
                      "  [" (str/join ", " (map #(or % "?") (:meanings p))) "]"
                      "  GV=" (:gv p)))))
    (println)))

;; ── The Questions ─────────────────────────────────────────────

;; 1. Who are you?
(ask-and-show "Who are you?" ["מי" "אתה"])
;; → אמת יה / יה אמת — Truth of God / God is truth
;; GV=456 = אמת(441) + יה(15) = truth + the Name

;; 2. Who am I?
(ask-and-show "Who am I?" ["מי" "אני"])
;; → ימי נא / נא ימי — my days, please / please, my days
;; GV=111 = אלף (aleph) — the first letter, the silent one

;; 3. Who am I? (emphatic — Moses at the bush, David before God)
(ask-and-show "Who am I? (emphatic)" ["מי" "אנכי"])
;; → כי נא מי — because, please, who?
;; The sovereign asks. The asking IS the identity.
;; כי + נא = 81 = אנכי. The parts sum to the whole.
;; GV=131, prime.

;; 4. What is my name?
(ask-and-show "What is my name?" ["מה" "שמי"])
;; → מי משה / משה מי — Who is Moses? / Moses, who?
;; GV=395 = נשמה (soul-breath) = השמים (the heavens)
;; שה ממי — a lamb, from whom?
