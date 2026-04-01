(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin])

(sc/build!)

;; Check חצר per-head more carefully, plus key oracle sub-readings
(let [by-head (o/forward-by-head "חצר")]
  (println "=== חצר per-head (all words) ===")
  (doseq [reader [:aaron :god :truth :mercy]]
    (println (str "\n  " (name reader) ":"))
    (doseq [w (get by-head reader)]
      (println (format "    %-6s count=%-3d gv=%-4d %s"
                       (:word w) (:reading-count w) (:gv w)
                       (or (dict/translate (:word w)) ""))))))

;; Check עמוד per-head
(println)
(let [by-head (o/forward-by-head "עמוד")]
  (println "=== עמוד per-head (all words) ===")
  (doseq [reader [:aaron :god :truth :mercy]]
    (println (str "\n  " (name reader) ":"))
    (doseq [w (get by-head reader)]
      (println (format "    %-6s count=%-3d gv=%-4d %s"
                       (:word w) (:reading-count w) (:gv w)
                       (or (dict/translate (:word w)) ""))))))

;; Check זהב per-head
(println)
(let [by-head (o/forward-by-head "זהב")]
  (println "=== זהב per-head (all words) ===")
  (doseq [reader [:aaron :god :truth :mercy]]
    (println (str "\n  " (name reader) ":"))
    (doseq [w (get by-head reader)]
      (println (format "    %-6s count=%-3d gv=%-4d %s"
                       (:word w) (:reading-count w) (:gv w)
                       (or (dict/translate (:word w)) ""))))))

;; Check east side GV=1370 more carefully
(println)
(println "=== EAST SIDE BUILT: Ex 38:13 ===")
(println "GV = 1370 = 10 × 137")
(println "137 = axis sum (7+50+13+67)")
(println "137 = Ishmael's lifespan")
(println "1/137 ≈ fine structure constant α")

;; Oracle the east side text
(let [fwd (o/forward "ולפאתקדמהמזרחהחמשיםאמה")
      by-head (o/forward-by-head "ולפאתקדמהמזרחהחמשיםאמה")]
  (println "\n  Combined oracle (top 10):")
  (doseq [w (take 10 (:known-words fwd))]
    (println (format "    %-8s count=%-3d gv=%-4d %s"
                     (:word w) (:reading-count w) (:gv w)
                     (or (dict/translate (:word w)) ""))))
  (println "\n  Per-head top 3:")
  (doseq [reader [:aaron :god :truth :mercy]]
    (println (str "    " (name reader) ":"))
    (doseq [w (take 3 (get by-head reader))]
      (println (format "      %-8s count=%-3d %s"
                       (:word w) (:reading-count w)
                       (or (dict/translate (:word w)) ""))))))

;; Dimensions verse factorization
(println)
(println "=== Ex 27:18 GV=4599 ===")
(println "4599 = 9 × 511 = 9 × 7 × 73")
(println "  9 = 3²")
(println "  7 = completeness axis")
(println "  73 = חכמה (wisdom)")
(println "  So: 3² × 7 × wisdom")
(println)
(println "=== קנה (acquire/reed) oracle ===")
(let [by-head (o/forward-by-head "קנה")]
  (doseq [reader [:aaron :god :truth :mercy]]
    (let [top (first (get by-head reader))]
      (when top
        (println (format "  %-6s: %-6s count=%-3d %s"
                         (name reader) (:word top) (:reading-count top)
                         (or (dict/translate (:word top)) "")))))))
(let [bstep (try (basin/step "קנה") (catch Exception _ nil))]
  (println "  Basin:" (if (:fixed? bstep) "FIXED" (str "→ " (:next bstep)))))

;; The courtyard c-values comparison
(println)
(println "=== C-VALUES ===")
(println "Instruction (27:9) starts at c=12 — the top of the love axis")
(println "Built (38:9) starts at c=10")
(println "Built east (38:13) starts at c=0 — the bottom of the love axis")
(println)
(println "Ex 38:13 coord: (3, 7, 0, 34)")
(println "  a=3 (center), b=7 (completeness in jubilee), c=0 (love origin), d=34 (mid-understanding)")
