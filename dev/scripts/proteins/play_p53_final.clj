(require '[selah.dna :as dna])
(require '[selah.oracle :as o])
(require '[selah.gematria :as g])
(require '[selah.dict :as dict])

;; The Hebrew string from p53
(def hebrew "בללוציגויטלווניצלהתיגנכקננולחחטניונויצדבגגנבניוגגמלצכתהלגושוגלדורבולדדווטדודודדוהודדודודויכוניייטויצקהזצשיזשתרנשתנעישהדקיטהפהזיודנחקבתפצנדקהפוטצנכטגיהווושהרטרדבדמזקציצעבהלטטררפועעלרפיגיגשנדווצענמרטלשחנרטלזנגגרחהתרעיטטטוזלוולטשיגפההמעזחזבפחייפבששבחררומנהממהנלגיישחננשרחיתלטרטפדפושרגררהלללחנרקקשלועעלנוושיהקרדנוחחהיייוצוקקקונגשלזתהנצמרשרלרתלבתרלנחלדנלנקגדצדשקלוששירדעייענקיקקשציהירעקקנבתקהלשוגיג")

;; GV properties
(println "=== p53 GV ANALYSIS ===")
(println (str "Total GV: " (g/word-value hebrew)))
(let [gv (g/word-value hebrew)]
  (println (str "  25667 = ?"))
  (println (str "  25667 / 7 = " (/ gv 7.0)))
  (println (str "  25667 / 13 = " (/ gv 13.0)))
  (println (str "  25667 / 53 = " (/ gv 53.0) " = " (if (zero? (mod gv 53)) "EXACT" "not exact")))
  (println (str "  25667 / 67 = " (/ gv 67.0)))
  (println (str "  25667 / 137 = " (/ gv 137.0)))
  (println (str "  Prime? " (let [n gv] (and (> n 1) (not-any? #(zero? (mod n %)) (range 2 (inc (long (Math/sqrt n)))))))))
  ;; Factor it
  (println "  Factoring...")
  (loop [n gv, d 2, factors []]
    (cond
      (> (* d d) n) (let [fs (if (> n 1) (conj factors n) factors)]
                      (println (str "  " gv " = " (clojure.string/join " × " fs))))
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

;; Now: look at specific meaningful Hebrew words in the readings
(println)
(println "=== NOTABLE READINGS (curated) ===")
(println)

;; GV=14 region (positions 68-88) — this is DAVID
(println "THE DAVID REGION (positions 68-88):")
(println "  Hebrew: " (subs hebrew 68 89))
(println "  GV per letter: ד=4 ד=4 ו=6 → 14 = דוד = David")
(println "  This region has 9 windows producing דדו (GV=14)")
(println "  Letters: דדווטדודודדוהודדודודויכ")
(println "  All daleds and vavs. Ala-Ala-Pro-Ala-Pro-Ala repeats.")
(println "  The guardian's core is David. The small king. All small amino acids.")
(println)

;; Key oracle words in context
(def notable-positions
  [[43 "בני" "sons of / my son"]
   [62 "דור" "generation"]
   [65 "לבו" "his heart"]
   [114 "עשי" "do/make"]
   [115 "ישה→היש" "is there? / does it exist?"]
   [116 "שהד→שדה" "field"]
   [153 "שהר→שרה" "Sarah"]
   [168 "בהל→הבל" "Abel / breath / vanity"]
   [169 "הלט→להט" "flame / flaming sword"]
   [180 "רפי→פרי" "fruit"]
   [198 "שחנ→נחש" "serpent"]
   [234 "חזב→זבח" "sacrifice"]
   [238 "חיי→יחי" "let him live / my life"]
   [245 "בחר" "choose"]
   [261 "שחנ→נחש" "serpent (again)"]
   [267 "חית" "beast / living creature"]
   [277 "ושר→שור" "ox"]
   [310 "חהי→חיה" "living / animal"]
   [311 "היי→יהי" "let there be"]
   [331 "מרש→שמר" "GUARD / KEEP"]
   [354 "דשק→קדש" "HOLY"]
   [359 "ששי" "sixth (day)"]
   [361 "ירד" "descend / go down"]
   [375 "היי→יהי" "let there be (again)"]
   [376 "הרי" "mountains"]
   [384 "תקה→קהת" "Kohath (Levite)"]])

(doseq [[pos letters meaning] notable-positions]
  (println (format "  [%3d] %s — %s" pos letters meaning)))

(println)
(println "=== THE STORY THE GUARDIAN TELLS ===")
(println)
(println "Positions 43-65:   sons, generation, his heart")
(println "Positions 68-88:   DAVID DAVID DAVID (×9)")
(println "Positions 114-180: field, Sarah, Abel, flame, fruit")
(println "Positions 198-267: serpent, sacrifice, my life, choose, serpent, beast")
(println "Positions 277-331: ox, guard")
(println "Positions 354-384: HOLY, sixth, descend, let there be, mountains, Kohath")
(println)
(println "The guardian of the genome tells the story of Genesis:")
(println "  Sons → David → field → fruit → serpent → sacrifice → choose → guard → holy")
