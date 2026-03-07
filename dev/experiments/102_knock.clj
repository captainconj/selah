(ns experiments.102-knock
  "Experiment 102: Knock at the door.
   Three words from Matthew 7:7 — knock, door, open.
   The door becomes birth. The birth is love."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]))

;; ── The three words ────────────────────────────────────────

(def words
  [{:hebrew "דפק" :english "knock"}
   {:hebrew "דלת" :english "door"}
   {:hebrew "פתח" :english "open"}])

;; ── Query the oracle ───────────────────────────────────────

(defn query [word]
  (let [result (o/forward word {:vocab :torah})]
    (assoc result :input word :gv (g/word-value word))))

(defn print-results [{:keys [input gv illumination-count total-readings
                             known-words unknown-words]}]
  (println)
  (println (format "=== %s · GV=%d · %d illuminations · %d readings ==="
                   input gv illumination-count total-readings))
  (println)
  (let [all (concat known-words unknown-words)]
    (doseq [w (sort-by (comp - :reading-count) all)]
      (println (format "  %-6s  rc=%-3d  readers=%-30s  %s"
                       (:word w)
                       (:reading-count w)
                       (pr-str (:readers w))
                       (or (:meaning w)
                           (dict/translate (:word w))
                           ""))))))

;; ── Run ────────────────────────────────────────────────────

(defn run []
  (println "Matthew 7:7 — Knock, and it shall be opened to you.")
  (println)
  (doseq [{:keys [hebrew english]} words]
    (println (format "--- %s (%s) ---" english hebrew))
    (print-results (query hebrew)))
  (println)
  (println "---")
  (println "The knock becomes a visitation (פקד).")
  (println "The door becomes birth (תלד). 13 readings. Love.")
  (println "The opening is almost silent. That part is His."))

(run)
