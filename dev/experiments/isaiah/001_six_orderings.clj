(ns experiments.isaiah.001-six-orderings
  "Isaiah 3D space — count, factor, build the box, read the corners, find the center.

   67,115 = 5 × 31 × 433. Three primes: breath × God × the irreducible.
   Six axis orderings. Same center. Different corners.

   The center: Isaiah 35:8 — The Way of Holiness.
   The corners: a messianic outline.

   Run: clojure -M:dev -e \"(require '[experiments.isaiah.001-six-orderings :as exp]) (exp/run)\"
   "
  (:require [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [selah.gematria :as g]
            [selah.oracle :as o]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════
;; BUILD THE TEXT
;; ══════════════════════════════════════════════════════

(defn build-isaiah
  "Load Isaiah, build letter stream and verse index.
   Returns {:letters [...] :verses [...] :n int}."
  []
  (println "Loading Isaiah...")
  (let [letters (vec (sefaria/book-letters "Isaiah"))
        n (count letters)
        pos (atom 0)
        verses (atom [])]
    (doseq [ch (range 1 67)]
      (let [vs (sefaria/fetch-chapter "Isaiah" ch)]
        (doseq [[v-idx verse] (map-indexed vector vs)]
          (let [clean (apply str (filter norm/hebrew-letter? (norm/strip-html verse)))
                len (count clean)
                start @pos]
            (swap! verses conj {:ch ch :vs (inc v-idx)
                                :start start :end (+ start len)
                                :text verse})
            (swap! pos + len)))))
    (println (format "  %,d letters. %d verses. %d chapters." n (count @verses) 66))
    {:letters letters :verses @verses :n n}))

(defn idx->verse
  "Find the verse containing a given letter index."
  [{:keys [verses]} idx]
  (first (filter #(and (<= (:start %) idx) (< idx (:end %))) verses)))

(defn verse-summary
  "Short readable summary of a verse."
  [vref]
  (when vref
    (let [raw (:text vref "")
          clean (norm/strip-html raw)
          trimmed (subs clean 0 (min 100 (count clean)))]
      (format "Isaiah %d:%d — %s" (:ch vref) (:vs vref) trimmed))))

;; ══════════════════════════════════════════════════════
;; FACTORIZATION
;; ══════════════════════════════════════════════════════

(defn factorize [n]
  (loop [n n d 2 factors []]
    (cond
      (< n 2) factors
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %)) (range 2 (inc (long (Math/sqrt n)))))))

;; ══════════════════════════════════════════════════════
;; THE BOX — corners, edges, faces, center
;; ══════════════════════════════════════════════════════

(defn coord->idx
  "3D coordinate to linear index."
  [[d1 d2 d3] a b c]
  (+ (* a d2 d3) (* b d3) c))

(defn map-box
  "Map corners, face centers, edge midpoints, and center for one ordering."
  [isaiah dims]
  (let [[d1 d2 d3] dims
        n (:n isaiah)
        lookup (fn [a b c]
                 (let [idx (coord->idx dims a b c)]
                   (when (< idx n)
                     (let [vref (idx->verse isaiah idx)
                           letter (nth (:letters isaiah) idx)]
                       (merge vref {:a a :b b :c c :idx idx
                                    :letter (str letter)
                                    :gv (g/letter-value letter)})))))

        ac (quot (dec d1) 2)
        bc (quot (dec d2) 2)
        cc (quot (dec d3) 2)

        corners [["Origin"      0 0 0]
                 ["Far c"       0 0 (dec d3)]
                 ["Far b"       0 (dec d2) 0]
                 ["Far b+c"     0 (dec d2) (dec d3)]
                 ["Far a"       (dec d1) 0 0]
                 ["Far a+c"     (dec d1) 0 (dec d3)]
                 ["Far a+b"     (dec d1) (dec d2) 0]
                 ["Far corner"  (dec d1) (dec d2) (dec d3)]]

        faces [["a=0 face"   0 bc cc]
               ["a=max face" (dec d1) bc cc]
               ["b=0 face"   ac 0 cc]
               ["b=max face" ac (dec d2) cc]
               ["c=0 face"   ac bc 0]
               ["c=max face" ac bc (dec d3)]]

        center ["Center" ac bc cc]]

    {:dims dims
     :center (let [[tag a b c] center] (assoc (lookup a b c) :tag tag))
     :corners (vec (for [[tag a b c] corners]
                     (assoc (lookup a b c) :tag tag)))
     :faces (vec (for [[tag a b c] faces]
                   (assoc (lookup a b c) :tag tag)))}))

;; ══════════════════════════════════════════════════════
;; BREASTPLATE READINGS
;; ══════════════════════════════════════════════════════

(defn read-verse-words
  "Run key Hebrew words from a verse through the breastplate."
  [words]
  (mapv (fn [[label heb]]
          (let [r (o/ask heb)
                br (:by-reader r)]
            {:label label :word heb :gv (g/word-value heb)
             :aaron (:aaron br) :god (:god br)
             :truth (:truth br) :mercy (:mercy br)
             :total (:total-readings r)}))
        words))

;; ══════════════════════════════════════════════════════
;; RUN
;; ══════════════════════════════════════════════════════

(defn run []
  (println "═══════════════════════════════════════════════════")
  (println "  ISAIAH — THE BOX")
  (println "  Count. Factor. Build. Read.")
  (println "═══════════════════════════════════════════════════\n")

  (let [isaiah (build-isaiah)
        n (:n isaiah)
        factors (factorize n)]

    ;; ── Step 1: The Count ──
    (println (format "\n── THE COUNT ──\n"))
    (println (format "  Letters: %,d" n))
    (println (format "  Factors: %s" (str/join " × " factors)))
    (println (format "  All prime? %s" (every? prime? factors)))
    (println (format "  Sum: %d + %d + %d = %d" 5 31 433 (+ 5 31 433)))
    (println (format "  Digit sum: 4 + 6 + 9 = %d (prime: %s)" 19 (prime? 19)))
    (println (format "  Chapters: %d" 66))

    ;; ── Step 2: The Center ──
    (println (format "\n── THE CENTER ──\n"))
    (let [mid (quot n 2)
          vref (idx->verse isaiah mid)
          letter (nth (:letters isaiah) mid)]
      (println (format "  Center position: %,d" mid))
      (println (format "  Letter: %s (GV=%d)" letter (g/letter-value letter)))
      (println (format "  %s" (verse-summary vref)))
      (println)

      ;; Center in each ordering
      (println "  Center coordinate in each ordering:")
      (doseq [[d1 d2 d3] [[5 31 433] [5 433 31] [31 5 433]
                           [31 433 5] [433 5 31] [433 31 5]]]
        (let [ac (quot (dec d1) 2)
              bc (quot (dec d2) 2)
              cc (quot (dec d3) 2)]
          (println (format "    %d×%d×%d → (%d, %d, %d)" d1 d2 d3 ac bc cc)))))

    ;; ── Step 3: The Box — All 6 Orderings ──
    (println (format "\n── THE BOX — 6 ORDERINGS ──"))

    (let [orderings [[5 31 433]
                     [5 433 31]
                     [31 5 433]
                     [31 433 5]
                     [433 5 31]
                     [433 31 5]]]
      (doseq [dims orderings]
        (let [{:keys [center corners faces]} (map-box isaiah dims)
              [d1 d2 d3] dims]
          (println (format "\n  ═══ %d × %d × %d ═══" d1 d2 d3))
          (println (format "  Center: %s\n" (verse-summary center)))

          (println "  Corners:")
          (doseq [c corners]
            (println (format "    %-12s (%d,%d,%d) %s"
                             (:tag c) (:a c) (:b c) (:c c)
                             (verse-summary c))))
          (println "\n  Face centers:")
          (doseq [f faces]
            (println (format "    %-12s (%d,%d,%d) %s"
                             (:tag f) (:a f) (:b f) (:c f)
                             (verse-summary f)))))))

    ;; ── Step 4: The Center Verse Through the Breastplate ──
    (println (format "\n── ISAIAH 35:8 THROUGH THE BREASTPLATE ──\n"))

    (let [words [["and-it-shall-be" "והיה"]
                 ["there/name" "שם"]
                 ["highway" "מסלול"]
                 ["and-a-way" "ודרך"]
                 ["way" "דרך"]
                 ["the-holiness" "הקדש"]
                 ["called" "יקרא"]
                 ["not" "לא"]
                 ["unclean" "טמא"]
                 ["walk" "הלך"]
                 ["and-fools" "ואוילים"]
                 ["go-astray" "יתעו"]]
          results (read-verse-words words)]
      (println (format "  %-16s %-6s %-4s  %4s %4s %4s %4s  %s"
                       "Word" "Heb" "GV" "A" "G" "T" "M" "Note"))
      (println (str "  " (apply str (repeat 70 "─"))))
      (doseq [{:keys [label word gv aaron god truth mercy total]} results]
        (let [note (cond
                     (zero? total) "GHOST"
                     (and (pos? mercy) (zero? aaron) (zero? god) (zero? truth)) "Mercy-only"
                     (and (pos? god) (zero? aaron) (zero? truth) (zero? mercy)) "God-only"
                     (and (pos? truth) (zero? aaron) (zero? god) (zero? mercy)) "Truth-only"
                     (> mercy (max aaron god truth)) "Mercy-dominant"
                     (> god (max aaron truth mercy)) "God-dominant"
                     :else "")]
          (println (format "  %-16s %-6s %-4d  %4d %4d %4d %4d  %s"
                           label word gv aaron god truth mercy note)))))

    ;; ── Step 5: Key Isaiah Words ──
    (println (format "\n── KEY ISAIAH WORDS ──\n"))

    (let [words [["comfort" "נחמו"]
                 ["servant" "עבד"]
                 ["suffering" "סבל"]
                 ["heal" "רפא"]
                 ["wounds/stripes" "חבורה"]
                 ["lamb" "כבש"]
                 ["slaughter" "טבח"]
                 ["sin" "חטא"]
                 ["bear/carry" "נשא"]
                 ["peace" "שלום"]
                 ["righteousness" "צדק"]
                 ["salvation" "ישועה"]
                 ["Immanuel" "עמנואל"]
                 ["virgin" "עלמה"]
                 ["sign" "אות"]
                 ["remnant" "שאר"]
                 ["plowshare" "את"]
                 ["new" "חדש"]
                 ["sing" "שיר"]
                 ["redeem" "גאל"]]
          results (read-verse-words words)]
      (println (format "  %-16s %-8s %-4s  %4s %4s %4s %4s  %s"
                       "Word" "Heb" "GV" "A" "G" "T" "M" "Note"))
      (println (str "  " (apply str (repeat 72 "─"))))
      (doseq [{:keys [label word gv aaron god truth mercy total]} results]
        (let [note (cond
                     (zero? total) "GHOST"
                     (and (pos? mercy) (zero? aaron) (zero? god) (zero? truth)) "Mercy-only"
                     (and (pos? god) (zero? aaron) (zero? truth) (zero? mercy)) "God-only"
                     (and (pos? truth) (zero? aaron) (zero? god) (zero? mercy)) "Truth-only"
                     (> mercy (max aaron god truth)) "Mercy-dominant"
                     (> god (max aaron truth mercy)) "God-dominant"
                     :else "")]
          (println (format "  %-16s %-8s %-4d  %4d %4d %4d %4d  %s"
                           label word gv aaron god truth mercy note)))))

    (println "\n═══════════════════════════════════════════════════")
    (println "  The center says: there is a way.")
    (println "  The corners say: someone paid.")
    (println "  Even fools won't get lost.")
    (println "═══════════════════════════════════════════════════")
    (println "\nDone.")))

;; ── Run ──
;; (run)
