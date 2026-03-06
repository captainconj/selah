;; Ark Walk 02 — The Specification (c=8)
;; Genesis 6:15-16a: 300 cubits length, 50 cubits breadth, 30 cubits height
;; The measurement floor. Three repetitions of אמה (cubit).

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 8 d)))
      layer (apply str (for [d (range 67)] (at d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      words [{:d 0 :w "שר" :note "prince (from אשר bridge)"}
             {:d 2 :w "תעשה" :note "you shall make"}
             {:d 6 :w "אתה" :note "you/it"}
             {:d 9 :w "שלש" :note "three (300)"}
             {:d 12 :w "מאות" :note "hundreds"}
             {:d 16 :w "אמה" :note "cubit (1st, GV=46)"}
             {:d 19 :w "ארך" :note "length"}
             {:d 22 :w "התבה" :note "the ark"}
             {:d 26 :w "חמשים" :note "fifty (jubilee)"}
             {:d 31 :w "אמה" :note "cubit (2nd)"}
             {:d 34 :w "רחבה" :note "its breadth (anagram: חרבה sword)"}
             {:d 38 :w "ושלשים" :note "and thirty"}
             {:d 44 :w "אמה" :note "cubit (3rd)"}
             {:d 47 :w "קומתה" :note "its height"}
             {:d 52 :w "צהר" :note "window/light (GV=295)"}
             {:d 55 :w "תעשה" :note "you shall make"}
             {:d 59 :w "לתבה" :note "for the ark"}
             {:d 63 :w "ואל" :note "and to"}
             {:d 66 :w "א" :note "bridge aleph"}]]

  (println "=== THE SPECIFICATION: c=8 (a=0, b=8) ===")
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

  ;; ═══ THE THREE DIMENSIONS ═══
  (println "=== THE THREE DIMENSIONS ===")
  (println (format "  300 cubits length:  שלש מאות אמה ארך"))
  (println (format "  50 cubits breadth:  חמשים אמה רחבה"))
  (println (format "  30 cubits height:   ושלשים אמה קומתה"))
  (println)
  (println "  300 → ש (shin/fire)   50 → נ (nun/jubilee)   30 → ל (lamed/teach)")
  (println (format "  לשן (tongue/language) GV=%d" (g/word-value "לשן")))
  (println)

  ;; ═══ THREE CUBITS (אמה) ═══
  (println "=== THREE CUBITS ===")
  (println (format "  אמה (cubit) GV = %d" (g/word-value "אמה")))
  (println "  Appears at d=16, d=31, d=44 — three measurements")
  (println (format "  אמה → מאה (hundred) — unit becomes count"))
  (println)

  ;; ═══ WINDOW ═══
  (println "=== THE WINDOW (צהר) ===")
  (println (format "  d=52-54: צהר (window/light/shining) GV=%d" (g/word-value "צהר")))
  (println (format "  צהר → הצר (narrow/distress) — light becomes constriction"))
  (println)

  ;; ═══ BREADTH ANAGRAM ═══
  (println "=== BREADTH = SWORD ===")
  (println (format "  רחבה (breadth) GV=%d" (g/word-value "רחבה")))
  (println (format "  חרבה (sword/desolation) GV=%d" (g/word-value "חרבה")))
  (println "  Same letters. The width contains the blade.")
  (println)

  ;; ═══ ORACLE ON SPECIFICATION WORDS ═══
  (println "=== ORACLE ON SPECIFICATION WORDS ===")
  (doseq [w ["אמה" "שלש" "חמשים" "קומתה" "צהר" "רחבה" "ארך" "מאות"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD ON TRANSFORMS ═══
  (println "=== PER-HEAD: SPECIFICATION TRANSFORMS ===")
  (doseq [w ["רחבה" "קומתה" "צהר" "חמשים"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
