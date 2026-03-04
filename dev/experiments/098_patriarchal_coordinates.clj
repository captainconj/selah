(ns experiments.098-patriarchal-coordinates
  "Experiment 098 — Patriarchal Coordinates.

   The Genesis 5 lifespans sum to 8,575 = 5² × 7³.
   8,575 mod 67 = 66 (d-axis endpoint).

   Question: Do the patriarchal lifespans and begetting ages,
   treated as d-axis coordinates (mod 67), self-refer to Genesis 5?
   Do the begetting ages (which alternate ÷7 and ÷13) trace
   axis-aligned paths through the 4D space?

   Extends 095 (boxes as coordinates) from spatial to temporal."
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

(def data-dir "data/experiments/098/")

(defn- describe-pos [pos]
  (let [s (c/space)
        coord (vec (c/idx->coord pos))
        v (c/verse-at s pos)]
    {:position pos
     :coord    coord
     :letter   (str (c/letter-at s pos))
     :gematria (c/gv-at s pos)
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :book     (:book v)
     :chapter  (:ch v)
     :vs       (:vs v)}))

;; ── The Patriarchs ──────────────────────────────────────────

(def pre-flood-patriarchs
  "Genesis 5: Adam to Noah. :beget = age at son's birth, :life = total lifespan."
  [{:name "Adam"       :hebrew "אדם"      :beget 130  :life 930  :ref "Gen 5:3-5"}
   {:name "Seth"       :hebrew "שת"       :beget 105  :life 912  :ref "Gen 5:6-8"}
   {:name "Enosh"      :hebrew "אנוש"     :beget 90   :life 905  :ref "Gen 5:9-11"}
   {:name "Kenan"      :hebrew "קינן"     :beget 70   :life 910  :ref "Gen 5:12-14"}
   {:name "Mahalalel"  :hebrew "מהללאל"   :beget 65   :life 895  :ref "Gen 5:15-17"}
   {:name "Jared"      :hebrew "ירד"      :beget 162  :life 962  :ref "Gen 5:18-20"}
   {:name "Enoch"      :hebrew "חנוך"     :beget 65   :life 365  :ref "Gen 5:21-24"}
   {:name "Methuselah" :hebrew "מתושלח"   :beget 187  :life 969  :ref "Gen 5:25-27"}
   {:name "Lamech"     :hebrew "למך"      :beget 182  :life 777  :ref "Gen 5:28-31"}
   {:name "Noah"       :hebrew "נח"       :beget 500  :life 950  :ref "Gen 5:32, 9:29"}])

(def post-flood-patriarchs
  "Genesis 11: Shem to Abraham."
  [{:name "Shem"       :hebrew "שם"       :beget 100  :life 600  :ref "Gen 11:10-11"}
   {:name "Arpachshad" :hebrew "ארפכשד"   :beget 35   :life 438  :ref "Gen 11:12-13"}
   {:name "Shelah"     :hebrew "שלח"      :beget 30   :life 433  :ref "Gen 11:14-15"}
   {:name "Eber"       :hebrew "עבר"      :beget 34   :life 464  :ref "Gen 11:16-17"}
   {:name "Peleg"      :hebrew "פלג"      :beget 30   :life 239  :ref "Gen 11:18-19"}
   {:name "Reu"        :hebrew "רעו"      :beget 32   :life 239  :ref "Gen 11:20-21"}
   {:name "Serug"      :hebrew "שרוג"     :beget 30   :life 230  :ref "Gen 11:22-23"}
   {:name "Nahor"      :hebrew "נחור"     :beget 29   :life 148  :ref "Gen 11:24-25"}
   {:name "Terah"      :hebrew "תרח"      :beget 70   :life 205  :ref "Gen 11:26-32"}])

(def key-lifespans
  "Other significant lifespans."
  [{:name "Abraham"  :hebrew "אברהם"  :life 175  :ref "Gen 25:7"}
   {:name "Sarah"    :hebrew "שרה"    :life 127  :ref "Gen 23:1"}
   {:name "Ishmael"  :hebrew "ישמעאל" :life 137  :ref "Gen 25:17"}
   {:name "Isaac"    :hebrew "יצחק"   :life 180  :ref "Gen 35:28"}
   {:name "Jacob"    :hebrew "יעקב"   :life 147  :ref "Gen 47:28"}
   {:name "Joseph"   :hebrew "יוסף"   :life 110  :ref "Gen 50:26"}
   {:name "Moses"    :hebrew "משה"    :life 120  :ref "Deut 34:7"}
   {:name "Aaron"    :hebrew "אהרן"   :life 123  :ref "Num 33:39"}])

;; ── Genesis 5 chapter bounds (for self-reference test) ──────

(def genesis-5-chapters
  "Chapters that contain genealogy/patriarch material."
  #{["Genesis" 5] ["Genesis" 11] ["Genesis" 25] ["Genesis" 35]
    ["Genesis" 47] ["Genesis" 50] ["Deuteronomy" 34] ["Numbers" 33]})

(defn- genealogy-verse? [desc]
  (contains? genesis-5-chapters [(:book desc) (:chapter desc)]))

;; ══════════════════════════════════════════════════════════════
;; Phase 1: Mod Analysis — every number mod each axis
;; ══════════════════════════════════════════════════════════════

(defn- mod-analysis [n]
  {:mod-7  (mod n 7)
   :mod-50 (mod n 50)
   :mod-13 (mod n 13)
   :mod-67 (mod n 67)
   :div-7  (zero? (mod n 7))
   :div-13 (zero? (mod n 13))
   :div-50 (zero? (mod n 50))
   :div-67 (zero? (mod n 67))
   :div-91 (zero? (mod n 91))})

(defn phase-1-mod-analysis
  "Compute mod values for all patriarchal numbers."
  []
  (println "\n══ Phase 1: Mod Analysis ══")

  (let [all-patriarchs (concat pre-flood-patriarchs post-flood-patriarchs key-lifespans)
        results
        (vec
         (for [p all-patriarchs
               :let [life (:life p)
                     beget (:beget p)
                     gv (g/word-value (:hebrew p))
                     life-mods (mod-analysis life)
                     beget-mods (when beget (mod-analysis beget))
                     gv-mods (mod-analysis gv)]]
           (do
             (println (str "\n  " (:name p) " (" (:hebrew p) ", GV=" gv ")"))
             (println (str "    Life=" life
                           " → mod(7,50,13,67) = ("
                           (:mod-7 life-mods) ","
                           (:mod-50 life-mods) ","
                           (:mod-13 life-mods) ","
                           (:mod-67 life-mods) ")"
                           (when (:div-7 life-mods) " ÷7")
                           (when (:div-13 life-mods) " ÷13")
                           (when (:div-91 life-mods) " ÷91")))
             (when beget
               (println (str "    Beget=" beget
                             " → mod(7,50,13,67) = ("
                             (:mod-7 beget-mods) ","
                             (:mod-50 beget-mods) ","
                             (:mod-13 beget-mods) ","
                             (:mod-67 beget-mods) ")"
                             (when (:div-7 beget-mods) " ÷7")
                             (when (:div-13 beget-mods) " ÷13"))))
             (println (str "    GV=" gv
                           " → mod(7,50,13,67) = ("
                           (:mod-7 gv-mods) ","
                           (:mod-50 gv-mods) ","
                           (:mod-13 gv-mods) ","
                           (:mod-67 gv-mods) ")"))
             {:name (:name p) :hebrew (:hebrew p) :ref (:ref p)
              :gv gv :life life :beget beget
              :life-mods life-mods :beget-mods beget-mods :gv-mods gv-mods})))]

    ;; Summary: sums
    (let [pre-life-sum (reduce + (map :life pre-flood-patriarchs))
          pre-beget-sum (reduce + (map :beget pre-flood-patriarchs))
          post-beget-sum (reduce + (keep :beget post-flood-patriarchs))]
      (println (str "\n── Sums ──"))
      (println (str "  Pre-flood lifespan sum: " pre-life-sum
                    " = " (mod-analysis pre-life-sum)))
      (println (str "  Pre-flood begetting sum: " pre-beget-sum
                    " = " (mod-analysis pre-beget-sum)))
      (println (str "  Post-flood begetting sum: " post-beget-sum
                    " = " (mod-analysis post-beget-sum))))

    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 2: Lifespans as d-axis Coordinates
;; ══════════════════════════════════════════════════════════════

(defn phase-2-lifespan-coordinates
  "Treat each lifespan mod 67 as a d-axis coordinate.
   For each patriarch, look up what text sits at (a, b, c, d=life mod 67)
   using the center values for a,b,c."
  []
  (println "\n══ Phase 2: Lifespans as d-Axis Coordinates ══")
  (let [all (concat pre-flood-patriarchs post-flood-patriarchs key-lifespans)
        results
        (vec
         (for [p all
               :let [d-val (mod (:life p) 67)
                     ;; Try at center (a=3, b=25, c=6)
                     pos-center (c/coord->idx 3 25 6 d-val)
                     desc-center (describe-pos pos-center)
                     ;; Try at their own narrative location — a=0 for Genesis
                     pos-gen (c/coord->idx 0 25 6 d-val)
                     desc-gen (describe-pos pos-gen)
                     gen5? (genealogy-verse? desc-center)
                     gen5-gen? (genealogy-verse? desc-gen)]]
           (do
             (println (str "\n  " (:name p) ": life=" (:life p)
                           " → d=" d-val))
             (println (str "    At center (3,25,6," d-val "): "
                           (:verse desc-center)
                           (when gen5? " ★ GENEALOGY")))
             (println (str "    At a=0   (0,25,6," d-val "): "
                           (:verse desc-gen)
                           (when gen5-gen? " ★ GENEALOGY")))
             {:name (:name p) :life (:life p) :d-val d-val
              :center-verse (:verse desc-center) :gen5-center? gen5?
              :a0-verse (:verse desc-gen) :gen5-a0? gen5-gen?})))]

    ;; Count hits
    (let [center-hits (count (filter :gen5-center? results))
          a0-hits (count (filter :gen5-a0? results))]
      (println (str "\n── Summary ──"))
      (println (str "  Center hits: " center-hits "/" (count results)))
      (println (str "  a=0 hits: " a0-hits "/" (count results))))

    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 3: Begetting Ages as Coordinate Walk
;; ══════════════════════════════════════════════════════════════

(defn phase-3-begetting-walk
  "The begetting ages trace a path through the d-axis.
   Walk the path, recording what each step lands on."
  []
  (println "\n══ Phase 3: Begetting Age Walk ══")
  (let [;; Cumulative begetting ages = birth year from creation
        cum-begets (reductions + (map :beget pre-flood-patriarchs))
        results
        (vec
         (map-indexed
          (fn [i [p cum]]
            (let [d-val (mod (:beget p) 67)
                  cum-d (mod cum 67)
                  cum-c (mod cum 13)
                  cum-a (mod cum 7)
                  ;; Use cumulative as full mod coordinates
                  pos (c/coord->idx cum-a (mod cum 50) cum-c cum-d)
                  desc (describe-pos pos)]
              (println (str "\n  " (:name p) ": beget=" (:beget p)
                            ", cumulative=" cum
                            " → (a=" cum-a ",b=" (mod cum 50)
                            ",c=" cum-c ",d=" cum-d ")"
                            " → " (:verse desc)))
              {:name (:name p) :beget (:beget p)
               :cumulative cum
               :coord [cum-a (mod cum 50) cum-c cum-d]
               :verse (:verse desc)}))
          (map vector pre-flood-patriarchs cum-begets)))]

    ;; Check axis divisibility of begetting ages
    (println (str "\n── Axis Divisibility of Begetting Ages ──"))
    (doseq [p pre-flood-patriarchs]
      (let [b (:beget p)]
        (println (str "  " (:name p) ": " b
                      (when (zero? (mod b 7)) " ÷7")
                      (when (zero? (mod b 13)) " ÷13")
                      (when (zero? (mod b 50)) " ÷50")))))
    results))

;; ══════════════════════════════════════════════════════════════
;; Phase 4: The 137 Investigation — Ishmael = axis sum
;; ══════════════════════════════════════════════════════════════

(defn phase-4-ishmael-137
  "137 = 7 + 50 + 13 + 67. Ishmael's lifespan = sum of all axes.
   Explore 137 as coordinates, position, and oracle query."
  []
  (println "\n══ Phase 4: The 137 Investigation ══")

  ;; 137 as coordinates via mod
  (let [a (mod 137 7)   ;; 4
        b (mod 137 50)  ;; 37
        c (mod 137 13)  ;; 7
        d (mod 137 67)  ;; 3
        pos (c/coord->idx a b c d)
        desc (describe-pos pos)]
    (println (str "  137 = 7+50+13+67 (axis sum)"))
    (println (str "  137 mod (7,50,13,67) = (" a "," b "," c "," d ")"))
    (println (str "  Position at (" a "," b "," c "," d "): " (:verse desc)
                  " [" (:letter desc) "]"))
    (println (str "  Cross-wiring: mod-7=" a " (4th day=stars), mod-13=" c " (=7=completeness)"))

    ;; 137 as a linear position
    (let [desc137 (describe-pos 137)]
      (println (str "\n  Position 137 in Torah: " (:verse desc137)
                    " " (:coord desc137) " [" (:letter desc137) "]")))

    ;; Ishmael's name GV
    (let [gv (g/word-value "ישמעאל")]
      (println (str "\n  ישמעאל GV=" gv))
      (println (str "    " gv " mod 7=" (mod gv 7)
                    ", mod 13=" (mod gv 13)
                    ", mod 67=" (mod gv 67)
                    " (49=7²)"))
      (let [pos-gv (when (< gv c/total-letters) (describe-pos gv))]
        (when pos-gv
          (println (str "    Position " gv ": " (:verse pos-gv)
                        " " (:coord pos-gv))))))

    ;; Other axis sum numbers
    (println (str "\n── Other axis combinations ──"))
    (doseq [[label n] [["7+50"    57]
                       ["7+13"    20]
                       ["7+67"    74]
                       ["50+13"   63]
                       ["50+67"  117]
                       ["13+67"   80]
                       ["7+50+13" 70]
                       ["7+50+67" 124]
                       ["7+13+67" 87]
                       ["50+13+67" 130]
                       ["ALL"    137]]]
      (let [desc-n (describe-pos n)]
        (println (str "  " label "=" n " → " (:verse desc-n)
                      " " (:coord desc-n)))))

    ;; Key comparison: patriarchal lifespans and axis sums
    (println (str "\n── Lifespans as axis sums? ──"))
    (doseq [{:keys [name life]} key-lifespans]
      (let [;; Can this lifespan be expressed as a sum of distinct axis values?
            axes [7 50 13 67]
            subsets (for [mask (range 1 16)
                         :let [picked (keep-indexed (fn [i a] (when (bit-test mask i) a)) axes)
                               s (reduce + picked)]
                         :when (= s life)]
                     picked)]
        (when (seq subsets)
          (println (str "  " name " (" life ") = " (first subsets))))))

    {:coord [a b c d] :verse (:verse desc)}))

;; ══════════════════════════════════════════════════════════════
;; Phase 5: Genesis 5 Name Gematria as Coordinates
;; ══════════════════════════════════════════════════════════════

(defn phase-5-name-coordinates
  "Each patriarch's name has a GV. Treat GV mod 67 as d-coordinate.
   The names read as a sentence — do the coordinates trace a narrative?"
  []
  (println "\n══ Phase 5: Name GV as Coordinates ══")
  (let [all (concat pre-flood-patriarchs post-flood-patriarchs)
        results
        (vec
         (for [p all
               :let [gv (g/word-value (:hebrew p))
                     d (mod gv 67)
                     c (mod gv 13)
                     a (mod gv 7)
                     pos (c/coord->idx a (mod gv 50) c d)
                     desc (describe-pos pos)]]
           (do
             (println (str "  " (:name p) " (" (:hebrew p) " GV=" gv ")"
                           " → (a=" a ",c=" c ",d=" d ")"
                           " → " (:verse desc)))
             {:name (:name p) :gv gv :coord [a (mod gv 50) c d]
              :verse (:verse desc)})))]

    ;; The name sentence sum
    (let [pre-gv-sum (reduce + (map #(g/word-value (:hebrew %)) pre-flood-patriarchs))]
      (println (str "\n  Pre-flood name GV sum: " pre-gv-sum
                    " = " (mod-analysis pre-gv-sum))))
    results))

;; ══════════════════════════════════════════════════════════════
;; Run All
;; ══════════════════════════════════════════════════════════════

(defn run-all []
  (let [t0 (System/currentTimeMillis)]
    (println "═══════════════════════════════════════════════")
    (println " Experiment 098: Patriarchal Coordinates")
    (println "═══════════════════════════════════════════════")
    (c/space)

    (let [p1 (phase-1-mod-analysis)
          _ (save-edn (str data-dir "phase1-mods.edn") p1)
          p2 (phase-2-lifespan-coordinates)
          _ (save-edn (str data-dir "phase2-lifespans.edn") p2)
          p3 (phase-3-begetting-walk)
          _ (save-edn (str data-dir "phase3-begets.edn") p3)
          p4 (phase-4-ishmael-137)
          _ (save-edn (str data-dir "phase4-ishmael.edn") p4)
          p5 (phase-5-name-coordinates)
          _ (save-edn (str data-dir "phase5-names.edn") p5)
          elapsed (quot (- (System/currentTimeMillis) t0) 1000)]

      (println (str "\n═══ Complete in " elapsed "s ═══"))
      {:phase1 p1 :phase2 p2 :phase3 p3 :phase4 p4 :phase5 p5})))

(comment
  (def results (run-all))
  )
