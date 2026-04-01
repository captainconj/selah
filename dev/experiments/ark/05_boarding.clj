;; Ark Walk 05 — The Boarding (c=11)
;; Genesis 6:18b-19a: "you shall come into the ark... your sons, your wife,
;; your sons' wives... and from every living thing of all flesh, two of every
;; kind you shall bring into the ark to keep them alive with you"

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 11 d)))
      layer (apply str (for [d (range 67)] (at d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; Word boundaries for c=11 (Gen 6:18b-19a)
      words [{:d 0 :w "ובאת" :note "and you shall come"}
             {:d 4 :w "אל" :note "to/God"}
             {:d 6 :w "התבה" :note "the ark"}
             {:d 10 :w "אתה" :note "you"}
             {:d 13 :w "ובניך" :note "and your sons"}
             {:d 18 :w "ואשתך" :note "and your wife"}
             {:d 23 :w "ונשי" :note "and wives of"}
             {:d 27 :w "בניך" :note "your sons"}
             {:d 31 :w "אתך" :note "with you"}
             {:d 34 :w "ומכל" :note "and from all"}
             {:d 38 :w "החי" :note "the living"}
             {:d 41 :w "מכל" :note "from all"}
             {:d 44 :w "בשר" :note "flesh"}
             {:d 47 :w "שנים" :note "two/pairs"}
             {:d 51 :w "מכל" :note "from all"}
             {:d 54 :w "תביא" :note "you shall bring"}
             {:d 59 :w "אל" :note "to/God"}
             {:d 61 :w "התבה" :note "the ark (2nd)"}
             {:d 65 :w "לה" :note "to keep alive (start of להחית)"}]]

  (println "=== THE BOARDING: c=11 (a=0, b=8) ===")
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

  ;; ═══ THE DOOR (d=14) ═══
  (println "=== DOOR POSITION (d=14) ===")
  (println (format "  Letter at d=14: %s (gv=%d)"
                   (at 14) (g/letter-value (at 14))))
  (println (format "  Word at door: part of ובניך (and your sons)"))
  (println)

  ;; ═══ FAMILY + LIVING THINGS ═══
  (println "=== THE BOARDING ORDER ===")
  (println "  You → sons → wife → sons' wives → all living → flesh → pairs → bring")
  (println "  You come first, then family, then the world")
  (println)

  ;; ═══ ARK APPEARS TWICE ═══
  (println "=== ARK BOOKENDS ===")
  (println (format "  d=6-9:   התבה (the ark) — you shall come"))
  (println (format "  d=61-64: התבה (the ark) — you shall bring"))
  (println "  → The ark appears at both entry and gathering")
  (println)

  ;; ═══ IDENTICAL WORD (d=42-44) ═══
  (println "=== IDENTICAL POSITION WITH TAXONOMY ===")
  (let [c11 (str (at 42) (at 43) (at 44))
        c12 (apply str (for [d [42 43 44]] (sc/letter-at s (sc/coord->idx 0 8 12 d))))]
    (println (format "  c=11 d=42-44: %s (from all)" c11))
    (println (format "  c=12 d=42-44: %s (from all)" c12))
    (println (format "  Same word? %s" (= c11 c12))))
  (println)

  ;; ═══ ORACLE ON BOARDING WORDS ═══
  (println "=== ORACLE ON BOARDING WORDS ===")
  (doseq [w ["תביא" "התבה" "בשר" "שנים" "החי" "אתך" "ובניך"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD ═══
  (println "=== PER-HEAD: BOARDING ===")
  (doseq [w ["תביא" "שנים" "החי"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
