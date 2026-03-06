;; Ark Walk 01 — The Foundation (c=7)
;; Genesis 6:13-14 on the foundation floor
;; Standing at d=14 (the door), looking right and left

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 7 d)))
      layer (apply str (for [d (range 67)] (at d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; Manually identified word boundaries from Gen 6:13-14
      ;; (these are the words that fall on c=7, d=0-66)
      words [{:d 0 :w "ני" :note "end of והנני"}
             {:d 2 :w "משחיתם" :note "destroying them (root=משח anoint)"}
             {:d 8 :w "את" :note "aleph-tav"}
             {:d 10 :w "הארץ" :note "the earth"}
             {:d 14 :w "עשה" :note "make — the DOOR word"}
             {:d 17 :w "לך" :note "for you (GV=50 jubilee)"}
             {:d 19 :w "תבת" :note "ark of"}
             {:d 22 :w "עצי" :note "wood of"}
             {:d 25 :w "גפר" :note "gopher"}
             {:d 28 :w "קנים" :note "rooms/nests (GV=200=resh)"}
             {:d 32 :w "תעשה" :note "you shall make"}
             {:d 36 :w "את" :note "aleph-tav"}
             {:d 38 :w "התבה" :note "the ark"}
             {:d 42 :w "וכפרת" :note "and you shall cover/atone (verb)"}
             {:d 47 :w "אתה" :note "it/you"}
             {:d 50 :w "מבית" :note "from inside"}
             {:d 54 :w "ומחוץ" :note "and from outside"}
             {:d 59 :w "בכפר" :note "with pitch/atonement (noun)"}
             {:d 63 :w "וזה" :note "and this"}
             {:d 66 :w "א" :note "bridge aleph"}]]

  (println "=== THE FOUNDATION: c=7 (a=0, b=8) ===")
  (println (format "  Full layer: %s" layer))
  (println (format "  Layer GV = %d  factors = %s"
                   (g/word-value layer) (factorize (g/word-value layer))))
  (println)

  ;; ═══ WORD TABLE ═══
  (println "=== WORD TABLE ===")
  (doseq [{:keys [d w note]} words]
    (let [gv (g/word-value w)]
      (println (format "  d=%-2d  %-8s  gv=%-4d  %s  %s"
                       d w gv (or (dict/translate w) "") note))))
  (println)

  ;; ═══ DOOR POSITION (d=14) ═══
  (println "=== STANDING AT THE DOOR (d=14) ===")
  (println (format "  Door word: עשה (make) GV=%d" (g/word-value "עשה")))
  (println (format "  Door letter: %s (gv=%d)" (at 14) (g/letter-value (at 14))))
  (println)

  ;; ═══ ATONEMENT SYMMETRY ═══
  (println "=== ATONEMENT APPEARS TWICE ===")
  (println (format "  d=42-46: וכפרת (verb, you shall cover) GV=%d" (g/word-value "וכפרת")))
  (println (format "  d=59-62: בכפר (noun, with atonement) GV=%d" (g/word-value "בכפר")))
  (println "  → Inside and outside sealed with the same root")
  (println)

  ;; ═══ LAST TRIPLET ═══
  (let [last3 (str (at 64) (at 65) (at 66))]
    (println "=== LAST TRIPLET ===")
    (println (format "  d=64-66: %s  GV=%d" last3 (g/word-value last3)))
    (when (= 13 (g/word-value last3))
      (println "  → GV = 13 = LOVE (אהבה = אחד = 13)")))
  (println)

  ;; ═══ DESTRUCTION = ANOINTING ═══
  (println "=== DESTRUCTION ROOT ===")
  (println (format "  משחיתם (destroying them) d=2-7, GV=%d" (g/word-value "משחיתם")))
  (println (format "  Root: משח (anoint) GV=%d" (g/word-value "משח")))
  (println "  → The word for destruction contains the root for anointing")
  (println)

  ;; ═══ ORACLE ON FOUNDATION WORDS ═══
  (println "=== ORACLE ON FOUNDATION WORDS ===")
  (doseq [w ["עשה" "גפר" "קנים" "תבה" "כפר" "וכפרת" "את" "עצי" "משחיתם"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD ON KEY WORDS ═══
  (println "=== PER-HEAD READINGS ===")
  (doseq [w ["קנים" "וכפרת" "משחיתם"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
