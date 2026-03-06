;; Ark Walk 06 — The Taxonomy (c=12, top layer)
;; Genesis 6:19-20: male and female, bird/beast/creeping after its kind
;; The tree of life: three branches from d=14 (let them live)

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 12 d)))
      layer (apply str (for [d (range 67)] (at d)))

      ;; Vertical helper
      at-layer (fn [c d] (sc/letter-at s (sc/coord->idx 0 8 c d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      words [{:d 0 :w "חית" :note "to keep alive (end of להחית)"}
             {:d 3 :w "אתך" :note "with you"}
             {:d 6 :w "זכר" :note "male (GV=227)"}
             {:d 9 :w "ונקבה" :note "and female (GV=163)"}
             {:d 14 :w "יהיו" :note "let them LIVE (GV=31)"}
             {:d 18 :w "מה" :note "from the / what"}
             {:d 20 :w "עוף" :note "BIRD (branch 1)"}
             {:d 23 :w "למינהו" :note "after its kind"}
             {:d 29 :w "ומן" :note "and from"}
             {:d 32 :w "הבהמה" :note "the BEAST (branch 2)"}
             {:d 37 :w "למינה" :note "after its kind"}
             {:d 42 :w "מכל" :note "from all"}
             {:d 45 :w "רמש" :note "CREEPING thing (branch 3, =guard שמר)"}
             {:d 48 :w "האדמה" :note "the ground (GV=55=T(10))"}
             {:d 53 :w "למינהו" :note "after its kind"}
             {:d 59 :w "שנים" :note "pairs (GV=400)"}
             {:d 63 :w "מכל" :note "from all"}
             {:d 66 :w "י" :note "yod (hand, GV=10)"}]]

  (println "=== THE TAXONOMY: c=12 (a=0, b=8) ===")
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

  ;; ═══ LET THEM LIVE (d=14 — the door position) ═══
  (println "=== THE DOOR SAYS 'LET THEM LIVE' ===")
  (println (format "  d=14: יהיו (let them live) GV=%d" (g/word-value "יהיו")))
  (println "  Reading upward from d=14:")
  (doseq [c [9 10 11 12]]
    (let [letter (at-layer c 14)]
      (println (format "    c=%-2d: %s (gv=%d)" c letter (g/letter-value letter)))))
  (println)

  ;; ═══ MALE + FEMALE = OIL ═══
  (println "=== MALE + FEMALE = ANOINTING OIL ===")
  (println (format "  זכר (male) GV = %d" (g/word-value "זכר")))
  (println (format "  ונקבה (and female) GV = %d" (g/word-value "ונקבה")))
  (println (format "  Sum = %d" (+ (g/word-value "זכר") (g/word-value "ונקבה"))))
  (println (format "  שמן (oil/anointing) GV = %d" (g/word-value "שמן")))
  (println)

  ;; ═══ THE THREE BRANCHES ═══
  (println "=== THE TREE OF LIFE: THREE BRANCHES ===")
  (println "  From d=14 (let them live):")
  (println)
  (println "  Branch 1: עוף (bird, d=20-22)     — flies above")
  (println "    למינהו (after its kind, d=23-28)")
  (println)
  (println "  Branch 2: הבהמה (beast, d=32-36)   — walks on ground")
  (println "    למינה (after its kind, d=37-41)")
  (println)
  (println "  Branch 3: רמש (creeping, d=45-47)  — lowest, the guard")
  (println "    האדמה (ground, d=48-52)")
  (println "    למינהו (after its kind, d=53-58)")
  (println)

  ;; ═══ THE GUARD ═══
  (println "=== THE GUARD: CREEPING THING ===")
  (println (format "  רמש (creeping) GV = %d" (g/word-value "רמש")))
  (println (format "  שמר (guard) GV = %d" (g/word-value "שמר")))
  (println "  Same letters rearranged. The creeping thing IS the guard.")
  (println)

  ;; ═══ THE GROUND ═══
  (println "=== THE GROUND: T(10) ===")
  (println (format "  האדמה (the ground) GV = %d" (g/word-value "האדמה")))
  (println "  55 = T(10) = 1+2+3+...+10 (triangular number)")
  (println)

  ;; ═══ VERTICAL: TREE OVER COVENANT OVER GARDEN ═══
  (println "=== VERTICAL ALIGNMENT: TREE → COVENANT → GARDEN ===")
  (println "  d=53-58 (third branch):")
  (doseq [c [12 10 9]]
    (let [word-str (apply str (for [d (range 53 59)] (at-layer c d)))]
      (println (format "    c=%-2d: %s  gv=%d"
                       c word-str (g/word-value word-str)))))
  (println)

  (println "  d=59-64 (pairs + covenant):")
  (doseq [c [12 10 9]]
    (let [word-str (apply str (for [d (range 59 65)] (at-layer c d)))]
      (println (format "    c=%-2d: %s  gv=%d"
                       c word-str (g/word-value word-str)))))
  (println)

  ;; ═══ THE HAND AT TOP (d=66) ═══
  (println "=== THE HAND AT THE TOP (d=66) ===")
  (doseq [c (range 7 13)]
    (let [letter (at-layer c 66)]
      (println (format "  c=%-2d: %s  gv=%d" c letter (g/letter-value letter)))))
  (let [col66 (apply str (for [c (range 7 13)] (at-layer c 66)))]
    (println (format "  Full column: %s  sum=%d" col66 (g/word-value col66))))
  (println)

  ;; ═══ ORACLE ═══
  (println "=== ORACLE ON TAXONOMY WORDS ===")
  (doseq [w ["יהיו" "עוף" "הבהמה" "רמש" "האדמה" "שנים" "זכר" "למינהו"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD: GUARD/CREEPING ═══
  (println "=== PER-HEAD: THE GUARD ===")
  (doseq [w ["רמש" "עוף" "הבהמה"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
