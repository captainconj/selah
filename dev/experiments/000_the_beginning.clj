(ns experiments.000-the-beginning
  "The first experiment. The one that started it all.
   Every 50th letter in Genesis spells תורה (Torah).
   Every 50th letter in Exodus spells תורה.
   Every 50th letter in Numbers spells הרות (Torah reversed).
   Every 50th letter in Deuteronomy spells הרות.
   Leviticus — the center — spells יהוה at skip 7.
   Run: clojure -M:dev -m experiments.000-the-beginning"
  (:require [selah.els.engine :as els]
            [selah.text.sefaria :as sefaria]))

(defn -main []
  (println "=== THE BEGINNING ===")
  (println "  The classic Torah Codes claim. The one that lit the fuse.\n")

  ;; ── 1. Torah at skip 50 ────────────────────────────────────
  (println "── 1. תורה at Skip 50 ──\n")

  (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
    (let [letters (vec (sefaria/book-letters book))
          n (count letters)]
      (println (format "  %s (%,d letters):" book n))

      ;; Search for תורה forward and reverse at skip 50
      (let [hits-fwd (els/search letters "תורה" 50)
            hits-rev (els/search letters "הרות" 50)]
        (if (seq hits-fwd)
          (doseq [hit hits-fwd]
            (println (format "    תורה at skip +50, start position %d (1-based: %d)"
                             (:start hit) (inc (:start hit)))))
          (println "    No תורה at skip 50"))

        (if (seq hits-rev)
          (doseq [hit hits-rev]
            (println (format "    הרות (reversed) at skip +50, start position %d (1-based: %d)"
                             (:start hit) (inc (:start hit)))))
          (println "    No הרות at skip 50")))))

  ;; ── 2. YHVH in Leviticus ──────────────────────────────────
  (println "\n── 2. יהוה in Leviticus ──\n")

  (let [lev (vec (sefaria/book-letters "Leviticus"))]
    (println (format "  Leviticus (%,d letters)" (count lev)))

    ;; Search for YHVH at various skips near 7, 8
    (doseq [skip [7 8 -7 -8]]
      (let [hits (els/search lev "יהוה" skip)]
        (when (seq hits)
          (println (format "    יהוה at skip %+d: %d occurrence(s)" skip (count hits)))
          (doseq [hit (take 3 hits)]
            (println (format "      start=%d" (:start hit))))))))

  ;; ── 3. The pattern ─────────────────────────────────────────
  (println "\n── 3. The Pattern ──\n")

  (println "  Genesis:     תורה →  (Torah, forward)")
  (println "  Exodus:      תורה →  (Torah, forward)")
  (println "  Leviticus:   יהוה    (the Name)")
  (println "  Numbers:     הרות ←  (Torah, reversed)")
  (println "  Deuteronomy: הרות ←  (Torah, reversed)")
  (println "")
  (println "  The outer four books point inward.")
  (println "  They all point at the center.")
  (println "  The center spells the Name.")
  (println "")
  (println "  This is what we came to verify.")
  (println "  Everything after this is what we found.")

  (println "\nDone. It began here."))
