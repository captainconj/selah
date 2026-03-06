;; Ark Walk 13 — Choose Life
;; Basin landscape applied to the Ark. Fixed points, dead ends, cycles.
;; The largest basin = CHOOSE (216=6³). Love basin (7 members).
;; Ghost zone: grace, righteousness, judgment, face, king.

(require '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(let [factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))]

  ;; ═══ IRREDUCIBLE FIXED POINTS ═══
  (println "=== IRREDUCIBLE FIXED POINTS ===")
  (println "  Words that know their own name:")
  (doseq [w ["אמת" "חיים" "שלום" "ברית" "דם" "אור" "שמים" "מים"
             "רוח" "נפש" "לב" "בן" "אב" "אם" "יד" "פה"
             "כהן" "חטא" "קדש" "טוב" "יום" "לילה" "אשה" "אות"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          fixed? (and bstep (:fixed? bstep))]
      (println (format "  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w)
                       (if fixed? "FIXED" "not fixed")
                       (or (dict/translate w) "?")))))
  (println)

  ;; ═══ NOTABLE FLOWS ═══
  (println "=== NOTABLE FLOWS ===")
  (doseq [[w label] [["יהוה" "YHWH → becoming"]
                      ["אלהים" "God → to them"]
                      ["אל" "God → not"]
                      ["כבש" "lamb → lie down"]
                      ["כרוב" "cherub → blessed"]
                      ["רבקה" "Rebecca → bring near"]
                      ["לאה" "Leah → tent"]
                      ["יוסף" "Joseph → and he added"]
                      ["ראש" "head → blessed"]
                      ["כפרת" "mercy seat → menorah knob"]
                      ["פרכת" "veil → menorah knob"]]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          result (cond
                   (nil? bstep) "invisible"
                   (:fixed? bstep) "FIXED"
                   :else (str "→ " (:next bstep)))]
      (println (format "  %-8s gv=%-4d  %-15s  %s"
                       w (g/word-value w) result label))))
  (println)

  ;; ═══ THE FOUR CYCLES ═══
  (println "=== THE FOUR CYCLES (period-2) ===")
  (doseq [[a b meaning gv] [["רחב" "בחר" "wide ⟷ choose" 210]
                              ["רפא" "אפר" "heal ⟷ ash" 281]
                              ["לחה" "החל" "moist ⟷ begin" 43]
                              ["רכב" "בכר" "chariot ⟷ firstborn" 222]]]
    (let [step-a (try (:next (basin/step a)) (catch Exception _ "?"))
          step-b (try (:next (basin/step b)) (catch Exception _ "?"))]
      (println (format "  %s ⟷ %s  GV=%d  factors=%s  %s"
                       a b gv (factorize gv) meaning))
      (println (format "    %s→%s  %s→%s" a step-a b step-b))))
  (println)

  ;; ═══ CHOOSE BASIN (LARGEST) ═══
  (println "=== CHOOSE BASIN (largest, GV=216=6³) ===")
  (let [choose-words ["בחרו" "בחור" "חורב" "ברוח" "חברו" "וחבר" "וחרב" "חרבו" "ורחב" "רחבו"]]
    (doseq [w choose-words]
      (let [bstep (try (basin/step w) (catch Exception _ nil))
            result (cond
                     (nil? bstep) "invisible"
                     (:fixed? bstep) "FIXED"
                     :else (str "→ " (:next bstep)))]
        (println (format "  %-8s gv=%-4d  %s  %s"
                         w (g/word-value w) result (or (dict/translate w) "?"))))))
  (println)

  ;; ═══ LOVE BASIN ═══
  (println "=== LOVE BASIN (GV=24, 7 members) ===")
  (let [love-words ["ויאהב" "ואיבה" "ואביה" "והביא" "הביאו" "יבאהו" "ויבאה"]]
    (doseq [w love-words]
      (let [bstep (try (basin/step w) (catch Exception _ nil))
            result (cond
                     (nil? bstep) "invisible"
                     (:fixed? bstep) "FIXED"
                     :else (str "→ " (:next bstep)))]
        (println (format "  %-8s gv=%-4d  %s  %s"
                         w (g/word-value w) result (or (dict/translate w) "?"))))))
  (println "  Note: ויאהב (and he loved) ← ואיבה (and enmity, Gen 3:15)")
  (println)

  ;; ═══ GHOST ZONE ═══
  (println "=== GHOST ZONE (cannot speak) ===")
  (doseq [w ["חסד" "צדק" "משפט" "פנים" "עץ" "מלך" "ארץ" "דרך" "הלך" "ברך" "חשך"]]
    (let [bstep (try (basin/step w) (catch Exception _ nil))
          result (cond
                   (nil? bstep) "dead end"
                   (:fixed? bstep) "FIXED"
                   :else (str "→ " (:next bstep)))]
      (println (format "  %-8s gv=%-4d  %s  %s"
                       w (g/word-value w) result (or (dict/translate w) "?")))))
  (println)

  ;; ═══ PER-HEAD SPOTLIGHT ═══
  (println "=== PER-HEAD SPOTLIGHT ===")
  (doseq [w ["כבש" "יהוה" "חיים" "אמת" "שלום" "בן" "חסד" "אהל"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :right :left]]
        (let [words (get by-head reader)]
          (if (seq words)
            (println (format "    %-6s: %-8s count=%-3d  %s"
                             (name reader) (:word (first words)) (:reading-count (first words))
                             (or (dict/translate (:word (first words))) "")))
            (println (format "    %-6s: (silent)" (name reader)))))))))
