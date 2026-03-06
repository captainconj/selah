;; Ark Walk 10 — The Garden
;; מביא (bringing, GV=53=garden) and את (sign) are the two unanimous fixed points
;; on the door floor. The tree of life (c=12) grows vertically through the
;; covenant (c=10) down to the garden (c=9).

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [c d] (sc/letter-at s (sc/coord->idx 0 8 c d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; The critical junction: d=53-66 across three layers
      read-range (fn [c d-start d-end]
                   (apply str (for [d (range d-start (inc d-end))] (at c d))))]

  ;; ═══ THE TWO UNANIMOUS FIXED POINTS ═══
  (println "=== THE TWO UNANIMOUS FIXED POINTS (c=9) ===")
  (doseq [w ["מביא" "את"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (doseq [rw (take 2 words)]
            (println (format "    %-6s: %-8s count=%-3d"
                             (name reader) (:word rw) (:reading-count rw))))))))
  (println)

  ;; ═══ 53 = GARDEN ═══
  (println "=== 53 = GARDEN ===")
  (println (format "  מביא (bringing) GV = %d" (g/word-value "מביא")))
  (println (format "  גן (garden) GV = %d" (g/word-value "גן")))
  (println "  53 = 9th Fibonacci number")
  (println "  53 = garden sum from Experiment 078 (7 consecutive Fibonacci)")
  (println "  53 Torah portions")
  (println)

  ;; ═══ VERTICAL GEOMETRY: TREE → COVENANT → GARDEN ═══
  (println "=== VERTICAL GEOMETRY (d=53-66) ===")
  (println)
  (println "  Layer      d=53-58          d=59-61        d=62-66")
  (println "  -----      -------          -------        -------")
  (doseq [c [12 11 10 9 8 7]]
    (let [seg1 (read-range c 53 58)
          seg2 (read-range c 59 61)
          seg3 (read-range c 62 66)]
      (println (format "  c=%-2d       %-10s       %-8s       %-8s"
                       c seg1 seg2 seg3))))
  (println)

  ;; ═══ TREE OVER COVENANT OVER GARDEN ═══
  (println "=== THE TREE OVER THE COVENANT OVER THE GARDEN ===")
  (println "  c=12 d=53-58: למינהו (after its kind) — the third branch")
  (let [c12 (read-range 12 53 58)]
    (println (format "    %s  GV=%d" c12 (g/word-value c12))))
  (println)
  (println "  c=10 d=52-57: והקמתי (and I establish) — the covenant verb")
  (let [c10 (read-range 10 52 57)]
    (println (format "    %s  GV=%d" c10 (g/word-value c10))))
  (println)
  (println "  c=9  d=56-59: מביא (bringing) — the garden (GV=53)")
  (let [c9 (read-range 9 56 59)]
    (println (format "    %s  GV=%d" c9 (g/word-value c9))))
  (println)

  ;; ═══ PAIRS OVER COVENANT ═══
  (println "=== PAIRS OVER COVENANT ===")
  (println "  c=12 d=59-62: שנים (pairs) GV=400")
  (println "  c=10 d=58-64: את בריתי (sign + my covenant)")
  (let [sign-cov (read-range 10 58 64)]
    (println (format "    %s  GV=%d" sign-cov (g/word-value sign-cov))))
  (println)

  ;; ═══ THE ALEPH JUNCTION ═══
  (println "=== THE ALEPH JUNCTION (d=59-60) ===")
  (println "  Two alephs touch at the garden-sign boundary:")
  (doseq [c (range 7 13)]
    (let [l59 (at c 59) l60 (at c 60)]
      (println (format "    c=%-2d: %s %s" c l59 l60))))
  (println)

  ;; ═══ FULL JUNCTION TABLE ═══
  (println "=== FULL JUNCTION: d=48-66 ===")
  (doseq [d (range 48 67)]
    (let [letters (for [c [12 11 10 9]] (at c d))
          col-str (str/join " " (map str letters))]
      (println (format "  d=%-2d: %s" d col-str)))))
