(ns experiments.134-the-peace-thread
  "Experiment 134: The Peace Thread — God's exclusive vocabulary.

   The fourth reader (God, from the mercy seat) sees words no other reader can.
   Peace (שלום) was invisible in the three-reader era. It has 35 readings,
   all from God. Not a ghost — just only visible from the right chair.

   Findings:
   - 829 words are God-exclusive out of 12,826 Torah forms
   - 250 of those are short (3-4 letter) root words
   - Peace is a fixed point: it flows to itself under basin dynamics
   - Most God-exclusive words are fixed points (stability under recursion)
   - Key God-exclusives: peace, dove, rest, breath, Sinai, guard, altar, nations, language
   - Love, understanding, face, king, grace remain true ghosts (no reader sees them)
   - Torah, life: Right cherub exclusive. Holy: Aaron dominant."
  (:require [selah.oracle :as o]
            [selah.basin :as b]
            [selah.dict :as d]
            [selah.gematria :as g]
            [clojure.java.io :as io]))

;; ── The finding ──────────────────────────────────────────────
;;
;; In the three-reader era, peace (שלום) had 0 readings.
;; The commentary said: "Peace cannot be read at all. You must enter."
;;
;; With the fourth reader (God, from the mercy seat), peace has 35 readings.
;; All 35 from God. Zero from Aaron, zero from Right, zero from Left.
;;
;; Peace was never in the ghost zone.
;; We just weren't sitting in the right chair.

;; ── Peace: the anchor ──────────────────────────────────────────

(def peace (o/ask "שלום"))
;; {:word "שלום" :gv 376 :illumination-count 126 :total-readings 35
;;  :by-reader {:aaron 0 :god 35 :truth 0 :mercy 0} :readable? true}

(def peace-basin (b/walk "שלום"))
;; {:start "שלום" :fixed-point "שלום" :converged? true :cycle? false}
;; Peace is a fixed point. It stays peace.

(def peace-forward (o/forward-by-head "שלום"))
;; God reads: שלום (35), ושלם (21), לשום (18), ולשם (11)
;; Right: ולשם (8), לשום (3) — rearrangements, never peace itself
;; Left: ושלם (2), לשום (2), ולשם (2) — rearrangements
;; Aaron: nothing
;;
;; Same letters. Same illumination. Only God reads "peace."
;; The others see: "and he paid" (ושלם), "to place" (לשום), "and for the name" (ולשם)

;; ── Full sweep: every Torah word through o/ask ────────────────

(defn sweep-all
  "Ask every Torah word. Returns per-word reader profile."
  []
  (let [words (vec (d/torah-words))
        n (count words)]
    (println (str "[134] Sweeping " n " words..."))
    (vec (pmap (fn [w]
                 (let [a (o/ask w)]
                   {:word w
                    :gv (:gv a)
                    :illuminations (:illumination-count a)
                    :total (:total-readings a)
                    :aaron (get-in a [:by-reader :aaron])
                    :god (get-in a [:by-reader :god])
                    :truth (get-in a [:by-reader :truth])
                    :mercy (get-in a [:by-reader :mercy])}))
               words))))

(defn classify
  "Classify each word's reader profile."
  [results]
  (mapv (fn [{:keys [aaron god right left total] :as d}]
          (let [readers (cond-> []
                          (pos? aaron) (conj :aaron)
                          (pos? god) (conj :god)
                          (pos? right) (conj :truth)
                          (pos? left) (conj :mercy))
                label (cond
                        (zero? total) :ghost
                        (= [:god] readers) :god-only
                        (= [:aaron] readers) :aaron-only
                        (= [:truth] readers) :truth-only
                        (= [:mercy] readers) :mercy-only
                        :else :shared)]
            (assoc d :readers (set readers) :reader-count (count readers) :label label)))
        results))

;; ── Run and save ──────────────────────────────────────────────

(defn run-all []
  (let [results (sweep-all)
        classified (classify results)

        ;; Partitions
        ghosts (filter #(= :ghost (:label %)) classified)
        god-only (sort-by (comp - :total) (filter #(= :god-only (:label %)) classified))
        aaron-only (sort-by (comp - :total) (filter #(= :aaron-only (:label %)) classified))
        right-only (sort-by (comp - :total) (filter #(= :truth-only (:label %)) classified))
        left-only (sort-by (comp - :total) (filter #(= :mercy-only (:label %)) classified))
        shared (filter #(= :shared (:label %)) classified)

        ;; Short roots (3-4 letters)
        short (fn [xs] (filter #(<= 3 (count (:word %)) 4) xs))

        ;; Basin walks on God-exclusive short words (top 20)
        god-short-top (take 20 (short god-only))
        with-basins (mapv (fn [d]
                            (let [bw (b/walk (:word d))]
                              (assoc d
                                :fixed-point (:fixed-point bw)
                                :converged? (:converged? bw))))
                          god-short-top)

        summary {:total (count classified)
                 :ghosts (count ghosts)
                 :god-only (count god-only)
                 :aaron-only (count aaron-only)
                 :truth-only (count right-only)
                 :mercy-only (count left-only)
                 :shared (count shared)
                 :god-only-short (count (short god-only))
                 :aaron-only-short (count (short aaron-only))
                 :truth-only-short (count (short right-only))
                 :mercy-only-short (count (short left-only))}

        ;; Theological spotlight
        theological-words ["שלום" "חסד" "אהבה" "בינה" "פנים" "מלך" "צדק" "משפט"
                           "כבש" "תורה" "חיים" "אמת" "רוח" "כבוד" "קדוש"
                           "כפרת" "יהוה" "נפש" "שבת" "ברכה" "גאל" "סלח"]
        lookup (into {} (map (fn [d] [(:word d) d]) classified))
        theological (vec (keep (fn [w] (when-let [d (lookup w)] d)) theological-words))

        artifact {:experiment 134
                  :title "The Peace Thread"
                  :summary summary
                  :theological theological
                  :god-exclusive-top-20-with-basins with-basins
                  :god-exclusive-short (vec (short god-only))
                  :aaron-exclusive-short (vec (short aaron-only))
                  :truth-exclusive-short (vec (short right-only))
                  :mercy-exclusive-short (vec (short left-only))}

        output (with-out-str
                 (println "=== Experiment 134: The Peace Thread ===")
                 (println)
                 (println "Summary:")
                 (doseq [[k v] (sort-by first summary)]
                   (printf "  %-20s %d\n" (name k) v))
                 (println)
                 (println "=== THEOLOGICAL WORDS — READER PROFILE ===")
                 (doseq [d theological]
                   (printf "%-6s GV=%-5d  A=%-3d G=%-3d R=%-3d L=%-3d  total=%-4d %s\n"
                           (:word d) (:gv d)
                           (:aaron d) (:god d) (:truth d) (:mercy d)
                           (:total d) (name (:label d))))
                 (println)
                 (println "=== GOD-EXCLUSIVE SHORT WORDS (top 20) + BASIN ===")
                 (doseq [d with-basins]
                   (printf "%-8s GV=%-5d readings=%-3d fixed=%-8s %s  %s\n"
                           (:word d) (:gv d) (:total d)
                           (or (:fixed-point d) "nil")
                           (if (:converged? d) "converged" "diverged")
                           (or (:meaning d) "")))
                 (println)
                 (println "=== PER-READER EXCLUSIVE COUNTS (short roots) ===")
                 (printf "God:   %d words\n" (count (short god-only)))
                 (printf "Aaron: %d words\n" (count (short aaron-only)))
                 (printf "Right: %d words\n" (count (short right-only)))
                 (printf "Left:  %d words\n" (count (short left-only))))]

    ;; Write artifacts
    (io/make-parents "data/experiments/134-peace-thread.edn")
    (spit "data/experiments/134-peace-thread.edn" (pr-str artifact))
    (spit "data/experiments/134-peace-thread-output.txt" output)
    (print output)
    artifact))

;; ── REPL ────────────────────────────────────────────────────

(comment
  ;; Quick check
  (o/ask "שלום")
  (b/walk "שלום")
  (o/forward-by-head "שלום")

  ;; Run the full experiment
  (run-all)

  ;; Check a specific word
  (let [a (o/ask "מזבח")]
    [(:by-reader a) (:total-readings a)])
  )
