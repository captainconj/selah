;; Ark Walk 04 — The Covenant (c=10)
;; Genesis 6:17-18: "all flesh shall die... but I establish my covenant with you"
;; Above the flood boundary. The first covenant.

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 10 d)))
      layer (apply str (for [d (range 67)] (at d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; Word boundaries for c=10 (Gen 6:17-18)
      words [{:d 0 :w "מים" :note "water"}
             {:d 3 :w "על" :note "upon"}
             {:d 5 :w "הארץ" :note "the earth"}
             {:d 9 :w "לשחת" :note "to destroy"}
             {:d 13 :w "כל" :note "all (GV=50 at d=13!)"}
             {:d 15 :w "בשר" :note "flesh"}
             {:d 18 :w "אשר" :note "which/blessed"}
             {:d 21 :w "בו" :note "in it"}
             {:d 23 :w "רוח" :note "spirit/wind"}
             {:d 26 :w "חיים" :note "LIFE"}
             {:d 30 :w "מתחת" :note "from under"}
             {:d 34 :w "השמים" :note "the heavens"}
             {:d 39 :w "כל" :note "all"}
             {:d 41 :w "אשר" :note "which/blessed"}
             {:d 44 :w "בארץ" :note "in the earth"}
             {:d 48 :w "יגוע" :note "shall perish"}
             {:d 52 :w "והקמתי" :note "and I establish"}
             {:d 58 :w "את" :note "sign"}
             {:d 60 :w "בריתי" :note "my covenant"}
             {:d 65 :w "את" :note "sign (bridge)"}]]

  (println "=== THE COVENANT: c=10 (a=0, b=8) ===")
  (println (format "  Full layer: %s" layer))
  (println (format "  Layer GV = %d  factors = %s"
                   (g/word-value layer) (factorize (g/word-value layer))))
  (println)

  ;; ═══ WORD TABLE ═══
  (println "=== WORD TABLE ===")
  (doseq [{:keys [d w note]} words]
    (println (format "  d=%-2d  %-8s  gv=%-4d  %s  %s"
                     d w (g/word-value w) (or (dict/translate w) "") note)))
  (println)

  ;; ═══ SPIRIT + LIFE ═══
  (println "=== SPIRIT AND LIFE (d=23-29) ===")
  (println (format "  רוח (spirit) GV=%d  d=23-25" (g/word-value "רוח")))
  (println (format "  חיים (life) GV=%d  d=26-29" (g/word-value "חיים")))
  (println (format "  רוח חיים = %d" (+ (g/word-value "רוח") (g/word-value "חיים"))))
  (println)

  ;; ═══ THE COVENANT (d=52-64) ═══
  (println "=== I ESTABLISH MY COVENANT (d=52-64) ===")
  (println (format "  והקמתי (I establish) GV=%d  d=52-57" (g/word-value "והקמתי")))
  (println (format "  את (sign) GV=%d  d=58-59" (g/word-value "את")))
  (println (format "  בריתי (my covenant) GV=%d  d=60-64" (g/word-value "בריתי")))
  (println (format "  Combined: %d  factors=%s"
                   (+ (g/word-value "והקמתי") (g/word-value "את") (g/word-value "בריתי"))
                   (factorize (+ (g/word-value "והקמתי") (g/word-value "את") (g/word-value "בריתי")))))
  (println)

  ;; ═══ VERTICAL: GARDEN BELOW COVENANT ═══
  (println "=== VERTICAL ALIGNMENT: COVENANT OVER GARDEN ===")
  (println "  c=10 d=52-58: והקמתי את (I establish + sign)")
  (println "  c=9  d=56-61: מביא את (bringing + sign)")
  (println "  → Covenant covers the garden from above")
  (println)

  ;; ═══ ORACLE ON COVENANT WORDS ═══
  (println "=== ORACLE ON COVENANT WORDS ===")
  (doseq [w ["חיים" "רוח" "בריתי" "והקמתי" "בשר" "מים" "כל" "אשר"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD: LIFE ═══
  (println "=== PER-HEAD: LIFE AND SPIRIT ===")
  (doseq [w ["חיים" "רוח" "בשר"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
