;; Ark Walk 03 — The Directory (c=9)
;; Genesis 6:16b-17: the door floor. Three stories, "and I" (GV=67), the flood.
;; d=14 = ופתח (and-a-door). ואני GV=67 = the d-axis itself.

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [d] (sc/letter-at s (sc/coord->idx 0 8 9 d)))
      layer (apply str (for [d (range 67)] (at d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      words [{:d 0 :w "מה" :note "what? (from מלמעלה) GV=45=Adam"}
             {:d 2 :w "תכלנה" :note "finish"}
             {:d 7 :w "מלמעלה" :note "from above"}
             {:d 13 :w "ופתח" :note "and-a-door (GV=488=8×61)"}
             {:d 17 :w "התבה" :note "the ark"}
             {:d 21 :w "בצדה" :note "in its side"}
             {:d 25 :w "תשים" :note "you shall place"}
             {:d 29 :w "תחתים" :note "lower (rooms)"}
             {:d 34 :w "שנים" :note "second/pairs (GV=400)"}
             {:d 38 :w "ושלשים" :note "and third"}
             {:d 44 :w "תעשה" :note "you shall make"}
             {:d 48 :w "ואני" :note "and I (GV=67=understanding=d-axis!)"}
             {:d 52 :w "הנני" :note "behold I am"}
             {:d 56 :w "מביא" :note "bringing (GV=53=garden!)"}
             {:d 60 :w "את" :note "aleph-tav (sign)"}
             {:d 62 :w "המבול" :note "the flood"}]]

  (println "=== THE DIRECTORY: c=9 (a=0, b=8) ===")
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

  ;; ═══ THE DOOR ═══
  (println "=== THE DOOR (d=13-16) ===")
  (println (format "  ופתח (and a door) GV = %d = 8 × 61" (g/word-value "ופתח")))
  (println (format "  פתח (door root) GV = %d" (g/word-value "פתח")))
  (println)

  ;; ═══ THE SPEAKER ═══
  (println "=== AND I (d=48-51) ===")
  (println (format "  ואני GV = %d = understanding = d-axis dimension" (g/word-value "ואני")))
  (println "  The speaker's value IS the axis of understanding")
  (println)

  ;; ═══ THE GARDEN ═══
  (println "=== BRINGING = GARDEN (d=56-59) ===")
  (println (format "  מביא (bringing) GV = %d" (g/word-value "מביא")))
  (println (format "  גן (garden) GV = %d" (g/word-value "גן")))
  (println "  → Same value. The bringing IS the garden.")
  (println)

  ;; ═══ SIGN + FLOOD ═══
  (println "=== SIGN + FLOOD (d=60-66) ===")
  (println (format "  את (aleph-tav, sign) GV = %d" (g/word-value "את")))
  (println (format "  המבול (the flood) GV = %d" (g/word-value "המבול")))
  (println)

  ;; ═══ ORACLE: DOOR FLOOR WORDS ═══
  (println "=== ORACLE ON DOOR FLOOR WORDS ===")
  (doseq [w ["ופתח" "מביא" "את" "ואני" "הנני" "המבול" "תחתים" "שנים" "תכלנה"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          class (cond
                  (nil? bstep) "invisible"
                  (:fixed? bstep) "FIXED"
                  :else (str "→ " (:next bstep)))]
      (println (format "  %-10s gv=%-4d  %s  %s"
                       w (g/word-value w) class (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD: KEY WORDS ═══
  (println "=== PER-HEAD: DOOR FLOOR ===")
  (doseq [w ["מביא" "את" "המבול" "שנים" "תכלנה"]]
    (println (format "\n  %s (gv=%d):" w (g/word-value w)))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (doseq [rw (take 3 words)]
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) "")))))))))
