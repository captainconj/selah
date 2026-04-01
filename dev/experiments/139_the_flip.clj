(ns experiments.139-the-flip
  "Experiment 139: The Flip — Config A vs Config B.

   The seating arrangement question: which cherub is which?

   Config A (current): Right cherub = God's right = Yod = mercy
   Config B (the flip): Right cherub = priest's right = God's left

   Under the flip, the Right and Left traversal keys swap.
   Everything mercy currently sees, truth would see, and vice versa.

   The test: which config produces the courtroom architecture?
   - The lamb split (כבש by one pair, שכב by the other)
   - Mercy seeing more dictionary words (currently p=0.026)
   - Peace as a reader-exclusive
   - Life as a reader-exclusive"
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ── The Flip ──────────────────────────────────────────
;;
;; Config A (current read-key):
;;   :truth [(- c) r i]    — columns R→L, top→bottom
;;   :mercy  [c (- r) i]    — columns L→R, bottom→top
;;
;; Config B (flipped):
;;   :truth [c (- r) i]    — columns L→R, bottom→top (was :mercy)
;;   :mercy  [(- c) r i]    — columns R→L, top→bottom (was :truth)
;;
;; Aaron and God stay the same. Only the cherubim swap.

(def stone-row @#'o/stone-row)
(def stone-col @#'o/stone-col)

(defn read-key-flipped
  "Sort key for Config B — cherubim swapped."
  [reader [s i]]
  (let [r (stone-row s) c (stone-col s)]
    (case reader
      :aaron [r (- c) i]           ;; same
      :god   [(- r) c i]           ;; same
      :truth [c (- r) i]           ;; was :mercy
      :mercy  [(- c) r i])))        ;; was :truth

(defn read-positions-flipped
  "Read positions under Config B."
  [reader positions]
  (->> positions
       (sort-by #(read-key-flipped reader %))
       (map o/letter-at)
       (apply str)))

;; ── Run both configs on a word ────────────────────────

(defn ask-both
  "Run o/ask-equivalent under both configs for a word.
   Returns {:word :config-a {:by-reader ...} :config-b {:by-reader ...}}"
  [word]
  (let [ilsets (o/illumination-sets word)
        ;; Config A (current)
        hits-a (for [pset ilsets
                     reader [:aaron :god :truth :mercy]
                     :when (= (o/normalize-finals word)
                              (o/normalize-finals (o/read-positions reader pset)))]
                 reader)
        by-a (frequencies hits-a)
        ;; Config B (flipped)
        hits-b (for [pset ilsets
                     reader [:aaron :god :truth :mercy]
                     :when (= (o/normalize-finals word)
                              (o/normalize-finals (read-positions-flipped reader pset)))]
                 reader)
        by-b (frequencies hits-b)]
    {:word word
     :gv (g/word-value word)
     :illuminations (count ilsets)
     :config-a {:aaron (get by-a :aaron 0)
                :god   (get by-a :god 0)
                :truth (get by-a :truth 0)
                :mercy  (get by-a :mercy 0)
                :total (count hits-a)}
     :config-b {:aaron (get by-b :aaron 0)
                :god   (get by-b :god 0)
                :truth (get by-b :truth 0)
                :mercy  (get by-b :mercy 0)
                :total (count hits-b)}}))

;; ── The Test Words ────────────────────────────────────

(def test-words
  "Theologically significant words for the flip test."
  [;; The courtroom signatures
   "שלום"    ;; peace — currently God-exclusive
   "חיים"    ;; life — currently Right-exclusive
   "אמת"    ;; truth — currently God+Left
   "כבש"    ;; lamb
   "שכב"    ;; lie down
   "אהבה"   ;; love
   "אהב"    ;; love (verb)
   ;; The Name
   "יהוה"   ;; YHWH
   "והיה"   ;; becoming
   ;; Key oracle words
   "מים"    ;; water
   "רוח"    ;; spirit
   "אור"    ;; light
   "חטא"    ;; sin
   "דם"     ;; blood
   ;; Newly unlocked
   "מלך"    ;; king
   "דרך"    ;; way
   "ארץ"    ;; land
   "עץ"     ;; tree
   "חשך"    ;; darkness
   "ברך"    ;; bless
   "מלאך"   ;; angel
   ;; Readings from John 4
   "שתה"    ;; drink
   "נתן"    ;; give
   "באר"    ;; well
   "ברא"    ;; create
   "ישע"    ;; save
   ;; The woman at the well
   "אשה"    ;; woman
   "איש"    ;; man
   ;; The courtroom
   "שעיר"   ;; goat
   "גורל"   ;; lot
   "כפר"    ;; atonement
   "שלח"    ;; send
   "נשא"    ;; carry
   ])

;; ── Run the Experiment ────────────────────────────────

(defn run-test-words []
  (println "╔═══════════════════════════════════════════════════╗")
  (println "║  Experiment 139: The Flip — A vs B                ║")
  (println "║  Which cherub is which?                           ║")
  (println "╚═══════════════════════════════════════════════════╝")
  (println)
  (println "Config A: Right cherub = God's right = mercy (current)")
  (println "Config B: Right cherub = priest's right = God's left (flipped)")
  (println)
  (println (format "%-8s %-4s  %-25s %-25s  %s"
                   "Word" "GV" "Config A (A G R L)" "Config B (A G R L)" "Observation"))
  (println (apply str (repeat 100 "─")))

  (let [results (mapv ask-both test-words)]
    (doseq [{:keys [word gv config-a config-b]} results]
      (let [a config-a
            b config-b
            obs (cond
                  ;; Check for exclusivity flips
                  (and (pos? (:truth a)) (zero? (:mercy a))
                       (zero? (:truth b)) (pos? (:mercy b)))
                  "RIGHT→LEFT flip"

                  (and (pos? (:mercy a)) (zero? (:truth a))
                       (zero? (:mercy b)) (pos? (:truth b)))
                  "LEFT→RIGHT flip"

                  (and (= (:truth a) (:mercy b))
                       (= (:mercy a) (:truth b)))
                  "perfect swap"

                  (= a b) "IDENTICAL"

                  :else "")]
        (println (format "%-8s %-4d  A=%-3d G=%-3d R=%-3d L=%-3d    A=%-3d G=%-3d R=%-3d L=%-3d  %s"
                         word gv
                         (:aaron a) (:god a) (:truth a) (:mercy a)
                         (:aaron b) (:god b) (:truth b) (:mercy b)
                         obs))))

    ;; Summary statistics
    (println)
    (println "━━━ SUMMARY ━━━")
    (let [a-rights (reduce + (map #(get-in % [:config-a :truth]) results))
          a-lefts  (reduce + (map #(get-in % [:config-a :mercy]) results))
          b-rights (reduce + (map #(get-in % [:config-b :truth]) results))
          b-lefts  (reduce + (map #(get-in % [:config-b :mercy]) results))
          a-gods   (reduce + (map #(get-in % [:config-a :god]) results))
          b-gods   (reduce + (map #(get-in % [:config-b :god]) results))
          a-aarons (reduce + (map #(get-in % [:config-a :aaron]) results))
          b-aarons (reduce + (map #(get-in % [:config-b :aaron]) results))]
      (println (format "  Config A totals: A=%d G=%d R=%d L=%d" a-aarons a-gods a-rights a-lefts))
      (println (format "  Config B totals: A=%d G=%d R=%d L=%d" b-aarons b-gods b-rights b-lefts))
      (println)
      (println "  Aaron and God should be IDENTICAL across configs (they don't flip).")
      (println "  Right and Left should SWAP across configs.")
      (println (format "  Aaron match: %s" (if (= a-aarons b-aarons) "YES" "NO")))
      (println (format "  God match:   %s" (if (= a-gods b-gods) "YES" "NO")))
      (println (format "  R/L swap:    %s" (if (and (= a-rights b-lefts) (= a-lefts b-rights)) "YES — perfect swap" "NO — something else is happening"))))

    ;; The lamb split test
    (println)
    (println "━━━ THE LAMB SPLIT ━━━")
    (let [lamb (first (filter #(= "כבש" (:word %)) results))
          lie  (first (filter #(= "שכב" (:word %)) results))]
      (println (format "  Config A: lamb=A%d/G%d/R%d/L%d  lie-down=A%d/G%d/R%d/L%d"
                       (get-in lamb [:config-a :aaron]) (get-in lamb [:config-a :god])
                       (get-in lamb [:config-a :truth]) (get-in lamb [:config-a :mercy])
                       (get-in lie [:config-a :aaron]) (get-in lie [:config-a :god])
                       (get-in lie [:config-a :truth]) (get-in lie [:config-a :mercy])))
      (println (format "  Config B: lamb=A%d/G%d/R%d/L%d  lie-down=A%d/G%d/R%d/L%d"
                       (get-in lamb [:config-b :aaron]) (get-in lamb [:config-b :god])
                       (get-in lamb [:config-b :truth]) (get-in lamb [:config-b :mercy])
                       (get-in lie [:config-b :aaron]) (get-in lie [:config-b :god])
                       (get-in lie [:config-b :truth]) (get-in lie [:config-b :mercy]))))

    ;; The exclusivity test
    (println)
    (println "━━━ EXCLUSIVITY TEST ━━━")
    (doseq [{:keys [word config-a config-b]} results]
      (let [a-excl (cond
                     (and (pos? (:god config-a)) (zero? (:aaron config-a))
                          (zero? (:truth config-a)) (zero? (:mercy config-a))) "God-only"
                     (and (pos? (:truth config-a)) (zero? (:aaron config-a))
                          (zero? (:god config-a)) (zero? (:mercy config-a))) "Right-only"
                     (and (pos? (:mercy config-a)) (zero? (:aaron config-a))
                          (zero? (:god config-a)) (zero? (:truth config-a))) "Left-only"
                     (and (pos? (:aaron config-a)) (zero? (:god config-a))
                          (zero? (:truth config-a)) (zero? (:mercy config-a))) "Aaron-only"
                     :else nil)
            b-excl (cond
                     (and (pos? (:god config-b)) (zero? (:aaron config-b))
                          (zero? (:truth config-b)) (zero? (:mercy config-b))) "God-only"
                     (and (pos? (:truth config-b)) (zero? (:aaron config-b))
                          (zero? (:god config-b)) (zero? (:mercy config-b))) "Right-only"
                     (and (pos? (:mercy config-b)) (zero? (:aaron config-b))
                          (zero? (:god config-b)) (zero? (:truth config-b))) "Left-only"
                     (and (pos? (:aaron config-b)) (zero? (:god config-b))
                          (zero? (:truth config-b)) (zero? (:mercy config-b))) "Aaron-only"
                     :else nil)]
        (when (or a-excl b-excl)
          (println (format "  %-8s  A: %-12s  B: %-12s  %s"
                           word (or a-excl "—") (or b-excl "—")
                           (if (and a-excl b-excl (not= a-excl b-excl))
                             "← FLIPPED"
                             ""))))))

    results))

;; ── Full vocabulary scan ──────────────────────────────

(defn run-full-scan []
  (println "╔═══════════════════════════════════════════════════╗")
  (println "║  Full vocabulary scan — A vs B                    ║")
  (println "╚═══════════════════════════════════════════════════╝")
  (println)
  (let [words (vec (sort (dict/torah-words)))
        n (count words)
        a-totals (atom {:aaron 0 :god 0 :truth 0 :mercy 0})
        b-totals (atom {:aaron 0 :god 0 :truth 0 :mercy 0})]
    (println (str "Scanning " n " words under both configs..."))
    (doseq [[idx w] (map-indexed vector words)]
      (when (zero? (mod (inc idx) 2000))
        (println (str "  " (inc idx) "/" n "...")))
      (let [r (ask-both w)
            a (:config-a r)
            b (:config-b r)]
        (swap! a-totals update :aaron + (:aaron a))
        (swap! a-totals update :god + (:god a))
        (swap! a-totals update :truth + (:truth a))
        (swap! a-totals update :mercy + (:mercy a))
        (swap! b-totals update :aaron + (:aaron b))
        (swap! b-totals update :god + (:god b))
        (swap! b-totals update :truth + (:truth b))
        (swap! b-totals update :mercy + (:mercy b))))
    (println)
    (println "━━━ FULL VOCABULARY TOTALS ━━━")
    (println (format "  Config A: Aaron=%d  God=%d  Right=%d  Left=%d"
                     (:aaron @a-totals) (:god @a-totals)
                     (:truth @a-totals) (:mercy @a-totals)))
    (println (format "  Config B: Aaron=%d  God=%d  Right=%d  Left=%d"
                     (:aaron @b-totals) (:god @b-totals)
                     (:truth @b-totals) (:mercy @b-totals)))
    (println)
    (println "  If Right and Left perfectly swap, the architecture is symmetric")
    (println "  and the seating question is purely theological, not statistical.")
    (println "  If they DON'T perfectly swap, the grid geometry breaks the symmetry")
    (println "  and one config is objectively more productive than the other.")
    {:config-a @a-totals :config-b @b-totals}))

;; ── Run ───────────────────────────────────────────────
;; (run-test-words)
;; (run-full-scan)
