(ns scratch.093g-ghost-zone
  "Experiment 093g/093h — Ghost zone analysis.

   Two kinds of silence:
   1. Absent — words with letters NOT on the breastplate (ך and ץ)
   2. Mute   — words whose letters illuminate but no reader can parse

   Reproduces findings from docs/093h-the-ghost-zone.md"
  (:require [selah.oracle :as oracle]
            [selah.gematria :as g]
            [selah.dict :as dict]))

(defn run []
  (println "═══════════════════════════════════════════")
  (println " 093h — The Ghost Zone")
  (println "═══════════════════════════════════════════")

  ;; Letters present on the breastplate
  (let [grid-letters (set (keys oracle/letter-index))
        all-words    (dict/words)]

    ;; ── 1. Absent words: letters missing from the grid ──────────
    (println "\n── ABSENT: letters not on the breastplate ──\n")
    (println "Letters on grid:" (sort grid-letters))
    (println "Missing letters:" (sort (remove grid-letters
                                               (map char (range 0x05D0 0x05EB)))))

    (let [absent (->> all-words
                      (filter (fn [w]
                                (some #(not (grid-letters %)) (seq w))))
                      sort
                      (mapv (fn [w]
                              {:word    w
                               :meaning (dict/translate w)
                               :gv      (g/word-value w)
                               :missing (vec (distinct
                                               (filter #(not (grid-letters %))
                                                       (seq w))))})))]
      (println (str "\n" (count absent) " dict words with absent letters:\n"))
      (doseq [{:keys [word meaning gv missing]} absent]
        (println (format "  %-6s  %-25s  GV=%3d  missing: %s"
                         word meaning gv (apply str missing))))

      (println "\nThe king is not on the breastplate.")
      (println "Darkness is absent from the light.")
      (println "The land and the tree cannot be spoken.")
      (println "The way is absent — but truth and life can speak."))

    ;; ── 2. Mute words: illuminate but 0 phrase readings ─────────
    (println "\n\n── MUTE: letters glow, no reader speaks ──\n")

    (let [target-words ["חסד" "צדק" "צדקה" "משפט" "פנים" "מצרים" "כפרת" "פרכת"]
          mute-results
          (mapv (fn [w]
                  (let [ilsets  (oracle/illumination-sets w)
                        menu    (oracle/thummim-menu w {:vocab :dict})
                        phrases (when menu (:phrases menu))]
                    {:word          w
                     :meaning       (dict/translate w)
                     :gv            (g/word-value w)
                     :illuminations (count ilsets)
                     :readings      (count (or phrases []))}))
                target-words)]

      (println (format "  %-6s  %-20s  %4s  %12s  %s"
                       "Word" "Meaning" "GV" "Illuminations" "Readings"))
      (println "  ──────────────────────────────────────────────────────────")
      (doseq [{:keys [word meaning gv illuminations readings]} mute-results]
        (println (format "  %-6s  %-20s  %4d  %12d  %d"
                         word meaning gv illuminations readings)))

      (println "\n── Notable observations ──\n")
      (println "  חסד (lovingkindness) GV = 72 = number of letters on the breastplate.")
      (println "  The breastplate IS lovingkindness. It cannot name itself.\n")
      (println "  פנים (face) has 22 illumination patterns = size of Hebrew alphabet.")
      (println "  The face of God lights up fully and cannot be seen by any reader.\n")
      (println "  מצרים (Egypt) — 110 illumination patterns = years of Joseph's life.")
      (println "  The place of bondage screams in silence.\n")
      (println "  כפרת/פרכת (mercy seat/veil) — the Holy of Holies is beyond the oracle's speech."))

    ;; ── 3. Full mute scan of dictionary ─────────────────────────
    (println "\n\n── Full mute scan (all dict words) ──\n")

    (let [speakable-words (->> all-words
                               (remove (fn [w]
                                         (some #(not (grid-letters %)) (seq w))))
                               sort)
          mute-all
          (->> speakable-words
               (keep (fn [w]
                       (let [ilsets (oracle/illumination-sets w)]
                         (when (pos? (count ilsets))
                           (let [menu    (oracle/thummim-menu w {:vocab :dict})
                                 phrases (when menu (:phrases menu))]
                             (when (zero? (count (or phrases [])))
                               {:word          w
                                :meaning       (dict/translate w)
                                :gv            (g/word-value w)
                                :illuminations (count ilsets)}))))))
               vec)]

      (println (str (count mute-all) " mute words (illuminate but no :dict readings):\n"))
      (doseq [{:keys [word meaning gv illuminations]} mute-all]
        (println (format "  %-8s  %-25s  GV=%3d  illuminations=%d"
                         word meaning gv illuminations))))

    ;; ── Summary ─────────────────────────────────────────────────
    (println "\n═══════════════════════════════════════════")
    (println " The oracle speaks: peace, life, love, truth, the Name, the lamb.")
    (println " The oracle is silent on: king, darkness, land, mercy seat, face.")
    (println " The silence is as structured as the speech.")
    (println "═══════════════════════════════════════════")))
