(ns experiments.130-tanakh-sweep
  "Experiment 130: Tanakh Sweep.

   All 39 books of the Hebrew Bible. Per-book profile:
   total letters, GV, factors, serpent/lamb census,
   curse/blessing/servant word density, key divisors.

   Then: centers, partitions, mirror pairs, meta-factors.

   The question: does the Bible have the same structure as Isaiah?

   כי נא — because, please."
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Book order (Tanakh canonical) ──────────────────

(def tanakh-order
  [;; Torah (5)
   "Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"
   ;; Former Prophets (4 books, 6 entries)
   "Joshua" "Judges" "I Samuel" "II Samuel" "I Kings" "II Kings"
   ;; Latter Prophets (4 books, but Twelve = 1 book traditionally)
   "Isaiah" "Jeremiah" "Ezekiel"
   ;; The Twelve
   "Hosea" "Joel" "Amos" "Obadiah" "Jonah" "Micah"
   "Nahum" "Habakkuk" "Zephaniah" "Haggai" "Zechariah" "Malachi"
   ;; Writings
   "Psalms" "Proverbs" "Job"
   ;; Five Megillot
   "Song of Songs" "Ruth" "Lamentations" "Ecclesiastes" "Esther"
   ;; Late Writings
   "Daniel" "Ezra" "Nehemiah" "I Chronicles" "II Chronicles"])

(def ot-39
  "All 39 books in Protestant OT order (matches the 66-book hypothesis)."
  tanakh-order)

;; ── Text fetching ──────────────────────────────────

(defn fetch-book-letters
  "Fetch all chapters of a book, return normalized Hebrew letter string."
  [book]
  (sefaria/book-string book))

;; ── Serpent & lamb search ──────────────────────────

(defn find-trigram-set
  [hebrew target-set]
  (vec (for [i (range (- (count hebrew) 2))
             :let [w (subs hebrew i (+ i 3))]
             :when (= (set w) target-set)]
         {:pos i :letters w})))

(def serpent-chars #{\נ \ח \ש})
(def lamb-chars #{\כ \ב \ש})

;; ── Key word search ──────────────────────────────────

(def curse-words ["ארר" "קלל" "חרם" "שחת" "הרג" "מות" "רשע" "נגף" "נגע"
                  "חטא" "עון" "פשע" "טמא" "נחש" "שאול"])

(def blessing-words ["ברך" "טוב" "חסד" "אהב" "חיה" "שלם" "אור" "רחם"
                      "גאל" "כפר" "קדש" "אמת" "חכמ" "רוח" "נחם"])

(def servant-words ["עבד" "כבש" "שכב" "צדק" "משח" "ישע" "פדה" "בשר"])

(defn count-word-in [hebrew tw]
  (count (for [i (range (- (count hebrew) (dec (count tw))))
               :when (= (subs hebrew i (+ i (count tw))) tw)]
           i)))

(defn word-census [hebrew word-list]
  (reduce (fn [acc w]
            (let [c (count-word-in hebrew w)]
              (if (pos? c) (assoc acc w c) acc)))
          {} word-list))

;; ── GV analysis ──────────────────────────────────────

(def key-divisors [7 13 26 50 53 67 72 137])

(defn gv-analysis [gv]
  (let [divs (filterv #(zero? (mod gv %)) key-divisors)]
    {:gv gv :divisors divs}))

(defn factorize [n]
  (loop [n (long n), d (long 2), factors []]
    (cond
      (> (* d d) n) (if (> n 1) (conj factors n) factors)
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

;; ── Book scanner ──────────────────────────────────

(defn scan-book [book]
  (print (format "  Scanning %-20s" book))
  (flush)
  (let [letters (fetch-book-letters book)
        n (count letters)
        gv (g/word-value letters)
        gva (gv-analysis gv)
        factors (factorize gv)

        ;; Serpent & lamb
        serpents (find-trigram-set letters serpent-chars)
        lambs (find-trigram-set letters lamb-chars)

        ;; Key word census
        curses (word-census letters curse-words)
        blessings (word-census letters blessing-words)
        servants (word-census letters servant-words)

        result {:book book
                :chapters (get sefaria/book-chapters book)
                :letters n
                :gv gv
                :factors factors
                :divisors (:divisors gva)
                :serpent-total (count serpents)
                :lamb-total (count lambs)
                :curses curses
                :curse-total (reduce + 0 (vals curses))
                :blessings blessings
                :blessing-total (reduce + 0 (vals blessings))
                :servants servants
                :servant-total (reduce + 0 (vals servants))}]

    (println (format " %,7d letters  GV=%,15d  serp=%3d  lamb=%3d  curse=%4d  bless=%4d  ÷%s"
                     n gv (count serpents) (count lambs)
                     (:curse-total result) (:blessing-total result)
                     (str/join "," (map str (:divisors gva)))))
    result))

;; ── Structural analysis ──────────────────────────

(defn find-center-book
  "Given results, find the book at the center by cumulative letter count."
  [results]
  (let [total (reduce + (map :letters results))
        mid (/ total 2)]
    (loop [cum 0, rs results]
      (let [r (first rs)
            next-cum (+ cum (:letters r))]
        (if (>= next-cum mid)
          {:center-book (:book r)
           :position-in-book (- mid cum)
           :total-letters total
           :midpoint mid}
          (recur next-cum (rest rs)))))))

(defn mirror-pairs
  "Fold the book list: book N ↔ book (total-N-1). Check GV sums for key divisors."
  [results]
  (let [n (count results)
        pairs (for [i (range (/ n 2))
                    :let [a (nth results i)
                          b (nth results (- n 1 i))
                          gv-sum (+ (:gv a) (:gv b))
                          divs (filterv #(zero? (mod gv-sum %)) key-divisors)]]
                {:pair [(str "(" (inc i) ") " (:book a))
                        (str "(" (- n i) ") " (:book b))]
                 :gv-sum gv-sum
                 :divisors divs})]
    (filterv #(seq (:divisors %)) pairs)))

;; ── Summary ──────────────────────────────────────────

(defn print-summary [results]
  (println "\n\n════════════════════════════════════════════════════════════════")
  (println "  TANAKH SWEEP — ALL 39 BOOKS")
  (println "════════════════════════════════════════════════════════════════")

  ;; Book table
  (println "\n  ── Book Profiles ──")
  (println "  #   Book                 Ch   Letters        GV              Div       Serp  Lamb  Curse Bless Serv")
  (println "  ──  ──────────────────  ───  ───────  ──────────────  ─────────  ────  ────  ───── ───── ────")
  (doseq [[i r] (map-indexed vector results)]
    (println (format "  %2d  %-20s %3d  %,7d  %,15d  %-9s  %4d  %4d  %5d %5d %4d"
                     (inc i) (:book r) (:chapters r) (:letters r) (:gv r)
                     (str/join "," (map str (:divisors r)))
                     (:serpent-total r) (:lamb-total r)
                     (:curse-total r) (:blessing-total r)
                     (:servant-total r))))

  ;; Totals
  (let [total-letters (reduce + (map :letters results))
        total-gv (reduce + (map :gv results))
        total-factors (factorize total-gv)]
    (println (format "\n  TOTAL: %,d letters, GV = %,d" total-letters total-gv))
    (println (format "  Factors: %s" (str/join " × " (map str total-factors))))
    (println (format "  Key divisors: %s"
                     (str/join ", " (map #(str "÷" %) (filter #(zero? (mod total-gv %)) key-divisors))))))

  ;; Torah vs rest
  (println "\n  ── Torah (5) vs Prophets+Writings (34) ──")
  (let [torah (take 5 results)
        rest-books (drop 5 results)]
    (doseq [[label books] [["Torah (1-5)" torah] ["Prophets+Writings (6-39)" rest-books]]]
      (let [n (count books)
            total-letters (reduce + (map :letters books))
            total-gv (reduce + (map :gv books))
            total-serpent (reduce + (map :serpent-total books))
            total-lamb (reduce + (map :lamb-total books))
            total-curse (reduce + (map :curse-total books))
            total-bless (reduce + (map :blessing-total books))
            factors (factorize total-gv)]
        (println (format "\n  %s (%d books, %,d letters):" label n total-letters))
        (println (format "    GV = %,d" total-gv))
        (println (format "    Factors: %s" (str/join " × " (map str factors))))
        (println (format "    Key: %s" (str/join ", " (map #(str "÷" %) (filter #(zero? (mod total-gv %)) key-divisors)))))
        (println (format "    Serpent: %d (%.2f/1000)  Lamb: %d (%.2f/1000)"
                         total-serpent (* 1000.0 (/ total-serpent (double total-letters)))
                         total-lamb (* 1000.0 (/ total-lamb (double total-letters)))))
        (println (format "    Curse: %d (%.2f/1000)  Blessing: %d (%.2f/1000)  Servant: %d (%.2f/1000)"
                         total-curse (* 1000.0 (/ total-curse (double total-letters)))
                         total-bless (* 1000.0 (/ total-bless (double total-letters)))
                         (reduce + (map :servant-total books))
                         (* 1000.0 (/ (reduce + (map :servant-total books)) (double total-letters))))))))

  ;; Partitions — first half vs second half
  (println "\n  ── First Half (1-19) vs Second Half (20-39) ──")
  (let [first-half (take 19 results)
        second-half (drop 19 results)]
    (doseq [[label books] [["First half (1-19)" first-half] ["Second half (20-39)" second-half]]]
      (let [total-gv (reduce + (map :gv books))
            factors (factorize total-gv)]
        (println (format "\n  %s: GV = %,d" label total-gv))
        (println (format "    Factors: %s" (str/join " × " (map str factors))))
        (println (format "    Key: %s" (str/join ", " (map #(str "÷" %) (filter #(zero? (mod total-gv %)) key-divisors))))))))

  ;; Center book
  (println "\n  ── Center Book (by letter count) ──")
  (let [center (find-center-book results)]
    (println (format "  Total letters: %,d  Midpoint: %,d  → %s"
                     (:total-letters center) (long (:midpoint center)) (:center-book center))))

  ;; Center book by count
  (println "\n  ── Center Book (by book count) ──")
  (let [mid (nth results 19)]  ;; 0-indexed: book 20 = center of 39
    (println (format "  Book 20 of 39: %s (%,d letters, GV=%,d)"
                     (:book mid) (:letters mid) (:gv mid)))
    (println (format "  Factors: %s" (str/join " × " (map str (:factors mid)))))
    (println (format "  Key: %s" (str/join ", " (map #(str "÷" %) (:divisors mid))))))

  ;; Mirror pairs
  (println "\n  ── Mirror Pairs with Key Divisors ──")
  (let [pairs (mirror-pairs results)]
    (if (seq pairs)
      (doseq [p pairs]
        (println (format "  %s ↔ %s  sum=%,d  ÷%s"
                         (first (:pair p)) (second (:pair p))
                         (:gv-sum p)
                         (str/join "," (map str (:divisors p))))))
      (println "  (none found)")))

  ;; Top serpent books
  (println "\n  ── Most Serpent-Dense Books ──")
  (doseq [r (take 10 (sort-by #(- (/ (:serpent-total %) (double (:letters %)))) results))]
    (when (pos? (:serpent-total r))
      (println (format "    %-20s %3d serpent in %,6d letters (%.3f per 1000)"
                       (:book r) (:serpent-total r) (:letters r)
                       (* 1000.0 (/ (:serpent-total r) (double (:letters r))))))))

  ;; Top lamb books
  (println "\n  ── Most Lamb-Dense Books ──")
  (doseq [r (take 10 (sort-by #(- (/ (:lamb-total %) (double (:letters %)))) results))]
    (when (pos? (:lamb-total r))
      (println (format "    %-20s %3d lamb in %,6d letters (%.3f per 1000)"
                       (:book r) (:lamb-total r) (:letters r)
                       (* 1000.0 (/ (:lamb-total r) (double (:letters r))))))))

  results)

;; ── Run ──────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════════════════════")
  (println "  EXPERIMENT 130: TANAKH SWEEP")
  (println "  39 books. The whole Hebrew Bible. Does the structure scale?")
  (println "════════════════════════════════════════════════════════════════\n")

  (let [results (mapv scan-book ot-39)]
    (print-summary results)

    ;; Save data
    (spit "data/experiments/130-tanakh-sweep.edn"
          (pr-str results))
    (println "\n  Saved: data/experiments/130-tanakh-sweep.edn")

    results))

(comment
  (run-all)

  ;; Quick single book test
  (scan-book "Ruth")
  (scan-book "Genesis")

  nil)
