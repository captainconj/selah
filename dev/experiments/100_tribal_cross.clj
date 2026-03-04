(ns experiments.100-tribal-cross
  "Experiment 100 — The Tribal Cross as Coordinates.

   Numbers 2: 12 tribes in a cross formation, precise census counts.
   East:South = 16:13 (the love axis in the ratio).
   N+S ÷ 7. Every camp ÷ 50. Census delta = 7×13×20.

   Question: Do the tribal camp totals, treated as coordinates
   (mod each axis), self-refer to their own census text?
   Does the cross geometry map onto the 4D space?"
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── Helpers ─────────────────────────────────────────────────

(defn- save-edn [path data]
  (io/make-parents path)
  (spit path (pr-str data))
  (println (str "  Saved: " path)))

(def data-dir "data/experiments/100/")

(defn- describe-pos [pos]
  (let [s (c/space)
        coord (vec (c/idx->coord pos))
        v (c/verse-at s pos)]
    {:position pos
     :coord    coord
     :letter   (str (c/letter-at s pos))
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :book     (:book v)
     :chapter  (:ch v)
     :vs       (:vs v)}))

;; ── The Tribal Cross ────────────────────────────────────────

(def census-chapters
  "Chapters containing census/tribal material."
  #{["Numbers" 1] ["Numbers" 2] ["Numbers" 3] ["Numbers" 4]
    ["Numbers" 26] ["Genesis" 49] ["Deuteronomy" 33]})

(defn- census-verse? [desc]
  (contains? census-chapters [(:book desc) (:chapter desc)]))

(def first-census
  "Numbers 1-2: tribal counts and camp assignments."
  {:east  {:name "Judah's Camp"
           :tribes [{:name "Judah"    :hebrew "יהודה"  :count 74600}
                    {:name "Issachar" :hebrew "יששכר"  :count 54400}
                    {:name "Zebulun"  :hebrew "זבולן"  :count 57400}]
           :total 186400}
   :south {:name "Reuben's Camp"
           :tribes [{:name "Reuben"  :hebrew "ראובן"  :count 46500}
                    {:name "Simeon"  :hebrew "שמעון"  :count 59300}
                    {:name "Gad"     :hebrew "גד"     :count 45650}]
           :total 151450}
   :west  {:name "Ephraim's Camp"
           :tribes [{:name "Ephraim"   :hebrew "אפרים"  :count 40500}
                    {:name "Manasseh"  :hebrew "מנשה"   :count 32200}
                    {:name "Benjamin"  :hebrew "בנימין"  :count 35400}]
           :total 108100}
   :north {:name "Dan's Camp"
           :tribes [{:name "Dan"      :hebrew "דן"     :count 62700}
                    {:name "Asher"    :hebrew "אשר"    :count 41500}
                    {:name "Naphtali" :hebrew "נפתלי"  :count 53400}]
           :total 157600}
   :center {:name "Levites"
            :tribes [{:name "Gershon" :hebrew "גרשון" :count 7500}
                     {:name "Kohath"  :hebrew "קהת"   :count 8600}
                     {:name "Merari"  :hebrew "מררי"  :count 6200}]
            :total 22000}})

(def second-census
  "Numbers 26: second census totals by tribe."
  {:east  {:total 186200
           :tribes [{:name "Judah" :count 76500}
                    {:name "Issachar" :count 64300}
                    {:name "Zebulun" :count 60500}]}
   :south {:total 149450
           :tribes [{:name "Reuben" :count 43730}
                    {:name "Simeon" :count 22200}
                    {:name "Gad" :count 40500}]}
   :west  {:total 120100
           :tribes [{:name "Ephraim" :count 32500}
                    {:name "Manasseh" :count 52700}
                    {:name "Benjamin" :count 45600}]}
   :north {:total 145400
           :tribes [{:name "Dan" :count 64400}
                    {:name "Asher" :count 53400}
                    {:name "Naphtali" :count 45400}]}})

;; ══════════════════════════════════════════════════════════════
;; Phase 1: Camp Totals Mod Analysis
;; ══════════════════════════════════════════════════════════════

(defn- factorize [n]
  (loop [n n d 2 factors []]
    (cond
      (< n 2) factors
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

(defn- mod-analysis [n]
  {:mod-7  (mod n 7)  :mod-50 (mod n 50)
   :mod-13 (mod n 13) :mod-67 (mod n 67)
   :div-7  (zero? (mod n 7))  :div-13 (zero? (mod n 13))
   :div-50 (zero? (mod n 50)) :div-91 (zero? (mod n 91))})

(defn phase-1-camp-analysis
  "Factor and mod-analyze every tribal count and camp total."
  []
  (println "\n══ Phase 1: Camp Totals — Factorization & Mod Analysis ══")

  (doseq [dir [:east :south :west :north :center]]
    (let [camp (get first-census dir)]
      (println (str "\n  " (:name camp) " (" (name dir) "):"))
      (doseq [t (:tribes camp)]
        (let [n (:count t)
              factors (factorize n)
              mods (mod-analysis n)]
          (println (str "    " (:name t) " (" (:hebrew t) "): " n
                        " = " (str/join "×" factors)
                        " mod(7,50,13,67)=("
                        (:mod-7 mods) "," (:mod-50 mods) ","
                        (:mod-13 mods) "," (:mod-67 mods) ")"
                        (when (:div-7 mods) " ÷7")
                        (when (:div-13 mods) " ÷13")))))
      (let [total (:total camp)
            mods (mod-analysis total)]
        (println (str "    TOTAL: " total
                      " mod(7,50,13,67)=("
                      (:mod-7 mods) "," (:mod-50 mods) ","
                      (:mod-13 mods) "," (:mod-67 mods) ")"
                      (when (:div-7 mods) " ÷7")
                      (when (:div-13 mods) " ÷13")
                      (when (:div-50 mods) " ÷50"))))))

  ;; Cross-camp analysis
  (println (str "\n── Cross-Camp ──"))
  (let [e (:total (:east first-census))
        s (:total (:south first-census))
        w (:total (:west first-census))
        n (:total (:north first-census))
        ns-sum (+ n s)
        ew-sum (+ e w)
        grand (+ e s w n)]
    (println (str "  E+W = " ew-sum " " (mod-analysis ew-sum)))
    (println (str "  N+S = " ns-sum " " (mod-analysis ns-sum)))
    (println (str "  Grand total = " grand " " (mod-analysis grand)))
    (println (str "  E/S ratio: " e "/" s " = " (/ e (double s))
                  " ≈ " (/ e (/ s 100.0)) "/100"))
    ;; GCD to find exact ratio
    (let [g (loop [a e b s] (if (zero? b) a (recur b (mod a b))))]
      (println (str "  E/S = " (/ e g) "/" (/ s g)
                    " (GCD=" g ")")))
    (println (str "  N/S ratio: " (/ n (double s))))))

;; ══════════════════════════════════════════════════════════════
;; Phase 2: Camp Totals as 4D Coordinates
;; ══════════════════════════════════════════════════════════════

(defn phase-2-camp-coordinates
  "Treat camp totals mod (7,50,13,67) as 4D coordinates.
   Check if they hit census/tribal chapters."
  []
  (println "\n══ Phase 2: Camp Totals as 4D Coordinates ══")
  (let [results
        (vec
         (for [dir [:east :south :west :north :center]
               :let [camp (get first-census dir)
                     total (:total camp)
                     a (mod total 7)
                     b (mod total 50)
                     c (mod total 13)
                     d (mod total 67)
                     pos (c/coord->idx a b c d)
                     desc (describe-pos pos)
                     hit? (census-verse? desc)]]
           (do
             (println (str "\n  " (:name camp) ": " total
                           " → (" a "," b "," c "," d ")"
                           " → " (:verse desc)
                           (when hit? " ★ CENSUS")))
             {:camp (name dir) :total total
              :coord [a b c d] :verse (:verse desc) :census-hit? hit?})))]

    ;; Also try individual tribal counts
    (println (str "\n── Individual Tribal Counts ──"))
    (doseq [dir [:east :south :west :north]
            t (:tribes (get first-census dir))
            :let [n (:count t)
                  a (mod n 7) b (mod n 50) c (mod n 13) d (mod n 67)
                  pos (c/coord->idx a b c d)
                  desc (describe-pos pos)
                  hit? (census-verse? desc)]]
      (println (str "  " (:name t) ": " n
                    " → (" a "," b "," c "," d ")"
                    " → " (:verse desc)
                    (when hit? " ★"))))

    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 3: The Census Delta
;; ══════════════════════════════════════════════════════════════

(defn phase-3-census-delta
  "Second census - first census = 1,820 = 7 × 13 × 20.
   Analyze the per-camp deltas and their coordinate implications."
  []
  (println "\n══ Phase 3: Census Delta ══")
  (let [grand-1 (reduce + (map #(:total (val %))
                                (dissoc first-census :center)))
        grand-2 (reduce + (map #(:total (val %))
                                (dissoc second-census)))  ;; no center in 2nd
        delta (- grand-2 grand-1)]
    (println (str "  First census total: " grand-1))
    (println (str "  Second census total: " grand-2))
    (println (str "  Delta: " delta " = " (str/join "×" (factorize (Math/abs delta)))))

    ;; Per-camp deltas
    (println (str "\n── Per-Camp Deltas ──"))
    (doseq [dir [:east :south :west :north]
            :let [d1 (:total (get first-census dir))
                  d2 (:total (get second-census dir))
                  dd (- d2 d1)]]
      (println (str "  " (name dir) ": " d1 " → " d2
                    " (Δ=" (when (pos? dd) "+") dd
                    ", factors=" (str/join "×" (factorize (Math/abs dd))) ")")))

    ;; Delta as coordinate
    (let [delta-abs (Math/abs delta)
          a (mod delta-abs 7) b (mod delta-abs 50)
          c (mod delta-abs 13) d (mod delta-abs 67)
          pos (c/coord->idx a b c d)
          desc (describe-pos pos)]
      (println (str "\n  |Delta|=" delta-abs
                    " → (" a "," b "," c "," d ")"
                    " → " (:verse desc)
                    (when (census-verse? desc) " ★ CENSUS"))))

    ;; Second census ratios
    (println (str "\n── Second Census Ratios ──"))
    (let [e2 (:total (:east second-census))
          s2 (:total (:south second-census))
          w2 (:total (:west second-census))
          n2 (:total (:north second-census))]
      (println (str "  E2/W2 = " (/ e2 (double w2))
                    " (first was " (/ (:total (:east first-census))
                                      (double (:total (:west first-census)))) ")"))
      (println (str "  N2/S2 = " (/ n2 (double s2))
                    " (first was " (/ (:total (:north first-census))
                                      (double (:total (:south first-census)))) ")"))
      (println (str "  20/13 = " (/ 20.0 13)))
      (println (str "  E2/W2 vs 20/13: error = "
                    (format "%.4f" (Math/abs (- (/ e2 (double w2)) (/ 20.0 13)))))))))

;; ══════════════════════════════════════════════════════════════
;; Phase 4: Tribal Name GV Analysis
;; ══════════════════════════════════════════════════════════════

(defn phase-4-tribal-names
  "Analyze tribal name gematria — both Jacob's 12 and Ishmael's 12."
  []
  (println "\n══ Phase 4: Tribal Name GV Analysis ══")

  ;; Jacob's 12
  (println (str "\n── Jacob's 12 Tribes ──"))
  (let [tribes (for [dir [:east :south :west :north]
                     t (:tribes (get first-census dir))]
                 t)
        gv-sum (reduce + (map #(g/word-value (:hebrew %)) tribes))]
    (doseq [t tribes
            :let [gv (g/word-value (:hebrew t))]]
      (println (str "  " (:name t) " (" (:hebrew t) ")"
                    " GV=" gv
                    " mod(7,13,67)=(" (mod gv 7) "," (mod gv 13) "," (mod gv 67) ")"
                    (when (zero? (mod gv 7)) " ÷7")
                    (when (zero? (mod gv 13)) " ÷13"))))
    (println (str "  SUM = " gv-sum
                  " = " (str/join "×" (factorize gv-sum))
                  " mod(7,13,67)=(" (mod gv-sum 7) "," (mod gv-sum 13) "," (mod gv-sum 67) ")")))

  ;; Ishmael's 12 princes (Genesis 25:13-16)
  (println (str "\n── Ishmael's 12 Princes ──"))
  (let [princes [{:name "Nebaioth"  :hebrew "נביות"}
                 {:name "Kedar"     :hebrew "קדר"}
                 {:name "Adbeel"    :hebrew "אדבאל"}
                 {:name "Mibsam"    :hebrew "מבשם"}
                 {:name "Mishma"    :hebrew "משמע"}
                 {:name "Dumah"     :hebrew "דומה"}
                 {:name "Massa"     :hebrew "משא"}
                 {:name "Hadad"     :hebrew "חדד"}
                 {:name "Tema"      :hebrew "תימא"}
                 {:name "Jetur"     :hebrew "יטור"}
                 {:name "Naphish"   :hebrew "נפיש"}
                 {:name "Kedemah"   :hebrew "קדמה"}]
        gv-sum (reduce + (map #(g/word-value (:hebrew %)) princes))]
    (doseq [p princes
            :let [gv (g/word-value (:hebrew p))]]
      (println (str "  " (:name p) " (" (:hebrew p) ")"
                    " GV=" gv
                    " mod(7,13,67)=(" (mod gv 7) "," (mod gv 13) "," (mod gv 67) ")"
                    (when (zero? (mod gv 7)) " ÷7")
                    (when (zero? (mod gv 13)) " ÷13"))))
    (println (str "  SUM = " gv-sum
                  " = " (str/join "×" (factorize gv-sum))
                  " mod(7,13,67)=(" (mod gv-sum 7) "," (mod gv-sum 13) "," (mod gv-sum 67) ")")))

  ;; Comparison
  (println (str "\n── Ishmael 137 = 7+50+13+67 (axis sum) ──"))
  (let [ishmael-gv (g/word-value "ישמעאל")]
    (println (str "  ישמעאל GV=" ishmael-gv
                  " mod 67=" (mod ishmael-gv 67)
                  " (=7²=49)"))))

;; ══════════════════════════════════════════════════════════════
;; Run All
;; ══════════════════════════════════════════════════════════════

(defn run-all []
  (let [t0 (System/currentTimeMillis)]
    (println "═══════════════════════════════════════════════")
    (println " Experiment 100: The Tribal Cross")
    (println "═══════════════════════════════════════════════")
    (c/space)

    (let [p1 (phase-1-camp-analysis)
          p2 (phase-2-camp-coordinates)
          _ (save-edn (str data-dir "phase2-camp-coords.edn") p2)
          p3 (phase-3-census-delta)
          _ (save-edn (str data-dir "phase3-delta.edn") p3)
          p4 (phase-4-tribal-names)
          _ (save-edn (str data-dir "phase4-names.edn") p4)
          elapsed (quot (- (System/currentTimeMillis) t0) 1000)]

      (println (str "\n═══ Complete in " elapsed "s ═══"))
      {:phase2 p2 :phase3 p3 :phase4 p4})))

(comment
  (def results (run-all))
  )
