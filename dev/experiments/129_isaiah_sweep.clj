(ns experiments.129-isaiah-sweep
  "Experiment 129: Isaiah Sweep.

   All 66 chapters through the oracle. Chapter-by-chapter profile.
   Serpent census, lamb census, key word density, GV factors,
   basin walk character, top words, invisibles.

   The question: does the oracle see the seam at chapter 40?
   Judgment (1-39) vs comfort (40-66).

   כי נא — because, please."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.dict :as dict]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Text fetching ──────────────────────────────────────

(defn fetch-chapter-letters
  "Fetch all verses of a chapter, return normalized Hebrew string."
  [chapter]
  (let [verses (sefaria/fetch-chapter "Isaiah" chapter)
        raw (apply str (map norm/strip-html verses))]
    (apply str (norm/letter-stream raw))))

;; ── Sliding window (lightweight — no per-window printing) ──

(defn slide-words
  "Slide a window across hebrew, return vec of {:position :letters :word :meaning}."
  [hebrew window]
  (vec (for [i (range 0 (- (count hebrew) (dec window)))
             :let [w (subs hebrew i (+ i window))
                   fwd (o/forward (seq w) :torah)
                   known (:known-words fwd)]
             :when (seq known)]
         (let [top (first known)]
           {:position i
            :letters w
            :word (:word top)
            :meaning (:meaning top)
            :illuminations (:illumination-count fwd)
            :readings (:total-readings fwd)}))))

(defn word-freqs [hits]
  (->> hits
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :meaning (:meaning (first entries))
               :count (count entries)}))
       (sort-by (comp - :count))
       vec))

;; ── Serpent & lamb search ──────────────────────────────

(defn find-trigram-set
  "Find all positions where the 3-letter window contains exactly the target char set."
  [hebrew target-set]
  (vec (for [i (range (- (count hebrew) 2))
             :let [w (subs hebrew i (+ i 3))]
             :when (= (set w) target-set)]
         {:pos i :letters w :exact? (= w (apply str (sort target-set)))})))

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
  (loop [n n, d 2, factors []]
    (cond
      (> (* d d) n) (if (> n 1) (conj factors n) factors)
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

;; ── Chapter scanner ──────────────────────────────────

(defn scan-chapter [chapter]
  (print (format "  Scanning Isaiah %d..." chapter))
  (flush)
  (let [letters (fetch-chapter-letters chapter)
        n (count letters)
        gv (g/word-value letters)
        gva (gv-analysis gv)
        factors (factorize gv)

        ;; Serpent & lamb
        serpents (find-trigram-set letters serpent-chars)
        serpent-exact (count (filter #(= (:letters %) "נחש") serpents))
        serpent-anagram (- (count serpents) serpent-exact)
        lambs (find-trigram-set letters lamb-chars)
        lamb-exact (count (filter #(= (:letters %) "כבש") lambs))
        lamb-anagram (- (count lambs) lamb-exact)

        ;; Key word census
        curses (word-census letters curse-words)
        blessings (word-census letters blessing-words)
        servants (word-census letters servant-words)

        ;; Sliding windows (3-letter only for sweep speed)
        hits-3 (slide-words letters 3)
        top-3 (word-freqs hits-3)

        ;; Count invisibles (words that produce 0 illuminations)
        ;; Sample 50 random 3-letter windows for invisible rate
        invisible-sample (let [total (- n 2)]
                           (if (< total 50) total
                             (let [indices (take 50 (shuffle (range total)))]
                               (count (filter
                                       (fn [i]
                                         (let [w (subs letters i (+ i 3))
                                               fwd (o/forward (seq w) :torah)]
                                           (zero? (:illumination-count fwd))))
                                       indices)))))

        ;; 72-stamp count (words with exactly 72 readings)
        stamps-72 (count (filter #(= 72 (:readings %)) hits-3))

        ;; 3-illum (throne) count
        throne-count (count (filter #(= 3 (:illuminations %)) hits-3))

        ;; Basin walks of top 5 words
        top-basins (mapv (fn [{:keys [word meaning]}]
                           (let [walk (basin/walk word)]
                             {:word word :meaning meaning
                              :destination (:fixed-point walk)}))
                         (take 5 top-3))

        result {:chapter chapter
                :letters n
                :gv gv
                :factors factors
                :divisors (:divisors gva)
                :serpent-exact serpent-exact
                :serpent-anagram serpent-anagram
                :serpent-total (count serpents)
                :lamb-exact lamb-exact
                :lamb-anagram lamb-anagram
                :lamb-total (count lambs)
                :curses curses
                :curse-total (reduce + (vals curses))
                :blessings blessings
                :blessing-total (reduce + (vals blessings))
                :servants servants
                :servant-total (reduce + (vals servants))
                :top-3 (vec (take 10 top-3))
                :top-basins top-basins
                :stamps-72 stamps-72
                :throne-count throne-count
                :invisible-sample-pct (if (< n 52) 0
                                        (int (* 100.0 (/ invisible-sample 50.0))))
                :section (if (<= chapter 39) :judgment :comfort)}]

    (println (format " %d letters, GV=%,d, serpent=%d, lamb=%d, curse=%d, bless=%d"
                     n gv (count serpents) (count lambs)
                     (:curse-total result) (:blessing-total result)))

    result))

;; ── Summary ──────────────────────────────────────────

(defn print-summary [results]
  (println "\n\n════════════════════════════════════════════════════════════════")
  (println "  ISAIAH SWEEP — ALL 66 CHAPTERS")
  (println "════════════════════════════════════════════════════════════════")

  ;; Chapter table
  (println "\n  ── Chapter Profiles ──")
  (println "  Ch  Letters    GV        Div    Serp  Lamb  Curse Bless Serv  72s   3s    Top Word")
  (println "  ──  ───────  ────────  ─────  ────  ────  ───── ───── ────  ───  ───  ──────────")
  (doseq [r results]
    (println (format "  %2d  %5d  %,9d  %-5s  %3d   %3d   %4d  %4d  %3d   %2d   %2d   %s (%s)"
                     (:chapter r) (:letters r) (:gv r)
                     (str/join "," (map str (:divisors r)))
                     (:serpent-total r) (:lamb-total r)
                     (:curse-total r) (:blessing-total r)
                     (:servant-total r)
                     (:stamps-72 r) (:throne-count r)
                     (:word (first (:top-3 r)))
                     (or (:meaning (first (:top-3 r))) "?"))))

  ;; Judgment vs comfort aggregate
  (println "\n  ── Judgment (1-39) vs Comfort (40-66) ──")
  (let [judg (filter #(= :judgment (:section %)) results)
        comf (filter #(= :comfort (:section %)) results)]
    (doseq [[label chapters] [["Judgment (1-39)" judg] ["Comfort (40-66)" comf]]]
      (let [n (count chapters)
            total-letters (reduce + (map :letters chapters))
            total-serpent (reduce + (map :serpent-total chapters))
            total-lamb (reduce + (map :lamb-total chapters))
            total-curse (reduce + (map :curse-total chapters))
            total-bless (reduce + (map :blessing-total chapters))
            total-serv (reduce + (map :servant-total chapters))
            total-72 (reduce + (map :stamps-72 chapters))
            total-throne (reduce + (map :throne-count chapters))]
        (println (format "\n  %s (%d chapters, %,d letters):" label n total-letters))
        (println (format "    Serpent: %d total (%.2f per 1000 letters)"
                         total-serpent (* 1000.0 (/ total-serpent (double total-letters)))))
        (println (format "    Lamb:    %d total (%.2f per 1000 letters)"
                         total-lamb (* 1000.0 (/ total-lamb (double total-letters)))))
        (println (format "    Curse words:    %d total (%.2f per 1000 letters)"
                         total-curse (* 1000.0 (/ total-curse (double total-letters)))))
        (println (format "    Blessing words: %d total (%.2f per 1000 letters)"
                         total-bless (* 1000.0 (/ total-bless (double total-letters)))))
        (println (format "    Servant words:  %d total (%.2f per 1000 letters)"
                         total-serv (* 1000.0 (/ total-serv (double total-letters)))))
        (println (format "    72-stamps: %d   Throne(3): %d" total-72 total-throne)))))

  ;; Top serpent chapters
  (println "\n  ── Most Serpent-Dense Chapters ──")
  (doseq [r (take 10 (sort-by #(- (/ (:serpent-total %) (double (:letters %)))) results))]
    (when (pos? (:serpent-total r))
      (println (format "    Isaiah %2d: %d serpent in %d letters (%.2f per 1000)"
                       (:chapter r) (:serpent-total r) (:letters r)
                       (* 1000.0 (/ (:serpent-total r) (double (:letters r))))))))

  ;; Top lamb chapters
  (println "\n  ── Most Lamb-Dense Chapters ──")
  (doseq [r (take 10 (sort-by #(- (/ (:lamb-total %) (double (:letters %)))) results))]
    (when (pos? (:lamb-total r))
      (println (format "    Isaiah %2d: %d lamb in %d letters (%.2f per 1000)"
                       (:chapter r) (:lamb-total r) (:letters r)
                       (* 1000.0 (/ (:lamb-total r) (double (:letters r))))))))

  ;; Key divisor chapters
  (println "\n  ── Chapters with Key GV Divisors ──")
  (doseq [d key-divisors]
    (let [matching (filter #(some #{d} (:divisors %)) results)]
      (when (seq matching)
        (println (format "    ÷%d: %s"
                         d (str/join ", " (map #(str "ch" (:chapter %)) matching)))))))

  ;; Top basin walks across all chapters
  (println "\n  ── Top Words & Basin Destinations (all chapters) ──")
  (let [all-top-words (->> results
                           (mapcat :top-3)
                           (group-by :word)
                           (map (fn [[w entries]]
                                  {:word w
                                   :meaning (:meaning (first entries))
                                   :total (reduce + (map :count entries))
                                   :chapters (count entries)}))
                           (sort-by (comp - :total))
                           (take 20))]
    (doseq [{:keys [word meaning total chapters]} all-top-words]
      (let [walk (basin/walk word)]
        (println (format "    %-8s %-20s total=%-4d in %2d chapters → %s"
                         word (or meaning "?") total chapters
                         (or (:fixed-point walk) "null"))))))

  results)

;; ── Run ──────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════════════════════")
  (println "  EXPERIMENT 129: ISAIAH SWEEP")
  (println "  66 chapters. Judgment and comfort. Does the oracle see the seam?")
  (println "════════════════════════════════════════════════════════════════\n")

  (let [results (mapv scan-chapter (range 1 67))]
    (print-summary results)

    ;; Save data
    (spit "data/experiments/129-isaiah-sweep.edn"
          (pr-str results))
    (println "\n  Saved: data/experiments/129-isaiah-sweep.edn")

    results))

(comment
  (run-all)

  ;; Quick single chapter test
  (scan-chapter 53)
  (scan-chapter 6)
  (scan-chapter 40)

  nil)
