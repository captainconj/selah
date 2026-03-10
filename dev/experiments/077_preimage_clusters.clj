(ns experiments.077-preimage-clusters
  "Experiment 077: Preimage Clustering — Word Fingerprints in 4D Space

   The preimage reverses the machine: given a Hebrew word, find all
   a-fibers (7 letters at skip 43,550) that contain it.

   Early results show structured counts:
     שמר×22 (=letters), יהוה×12 (=tribes), תורה×8 (=new beginning),
     אהבה×5 (=books), ברית×4 (=axes)

   These counts reach outside the factorization to grab other structural
   constants. This experiment scans 104 words, then clusters by coordinate
   overlap to find geometric word families.

   PARTS:
   1. Full scan — preimage for every word, sorted table
   2. Count groups — words sharing the same hit count, axis divisibility
   3. Co-fiber analysis — which (b,c,d) addresses hold multiple words?
   4. Axis projections — word distributions on c-axis (13) and d-axis (67)
   5. Semantic clusters — co-located words, meaningful associations?
   6. Preimage fingerprints — feature vectors, pairwise similarity"
  (:require [selah.space.coords :as coords]
            [selah.gematria :as gem]
            [clojure.string :as str]))

;; ── Vocabulary ─────────────────────────────────────────────
;;
;; ~104 Hebrew words: 2-letter through 5-letter.
;; Merged from experiment 073 word lists + high-energy additions.

(def vocab
  "word -> English meaning"
  (array-map
   ;; ── 2-letter (24) ─────────────────────────────
   "את" "aleph-tav"
   "אל" "God/to"
   "אב" "father"
   "בן" "son"
   "לב" "heart"
   "דם" "blood"
   "שם" "name"
   "יד" "hand"
   "חי" "living"
   "רע" "evil/friend"
   "אש" "fire"
   "עז" "strong"
   "כל" "all"
   "אם" "mother/if"
   "עם" "people"
   "בר" "grain/son"
   "חן" "grace"
   "רב" "many"
   "גר" "sojourner"
   "פר" "bull"
   "עד" "witness"
   "עץ" "tree"
   "גן" "garden"
   "נח" "Noah"
   ;; ── 3-letter (59) ─────────────────────────────
   "אור" "light"
   "שמר" "guard"
   "אמת" "truth"
   "קדש" "holy"
   "יום" "day"
   "שבע" "seven"
   "טוב" "good"
   "אדם" "man"
   "חוה" "Eve"
   "מים" "water"
   "ארץ" "land"
   "שלם" "peace"
   "משה" "Moses"
   "דבר" "word/thing"
   "ברך" "bless"
   "חכם" "wise"
   "זרע" "seed"
   "נפש" "soul"
   "רוח" "spirit"
   "עין" "eye"
   "אחד" "one"
   "כהן" "priest"
   "מלך" "king"
   "גאל" "redeem"
   "רחם" "mercy"
   "שבת" "sabbath"
   "חסד" "lovingkindness"
   "עבד" "servant"
   "כפר" "atone"
   "נתן" "give"
   "שמע" "hear"
   "ידע" "know"
   "ראה" "see"
   "הלך" "walk"
   "ישב" "dwell"
   "עמד" "stand"
   "נשא" "lift/carry"
   "שלח" "send"
   "קרא" "call"
   "מות" "death"
   "חטא" "sin"
   "ברא" "create"
   "צדק" "righteous"
   "דרך" "way/path"
   "שמן" "oil"
   "יין" "wine"
   "לחם" "bread"
   "אבן" "stone"
   "כבד" "glory/heavy"
   "ענן" "cloud"
   "עלה" "ascend/offering"
   "טהר" "pure"
   "אמן" "amen/faithful"
   "צור" "rock"
   "חלק" "portion"
   "כרת" "cut(covenant)"
   "נדר" "vow"
   "עשה" "make/do"
   "בוא" "come/enter"
   "כבש" "lamb"
   "זבח" "sacrifice"
   "נחש" "serpent"
   "קשת" "bow/rainbow"
   "דעת" "knowledge"
   "ענה" "afflict"
   "פרי" "fruit"
   "חשך" "darkness"
   "שפך" "pour-out"
   "נקם" "avenge"
   "סלח" "forgive"
   "פדה" "ransom"
   "ירש" "inherit"
   "חרב" "sword"
   "מטה" "staff/tribe"
   "באר" "well"
   ;; ── 4-letter (16) ─────────────────────────────
   "תורה" "Torah"
   "יהוה" "YHWH"
   "אהבה" "love"
   "בינה" "understanding"
   "אהרן" "Aaron"
   "שמים" "heaven"
   "ברית" "covenant"
   "צדקה" "righteousness"
   "חיים" "life"
   "אלהם" "Elohim(def)"
   "עולם" "eternal"
   "מועד" "appointed-time"
   "משכן" "tabernacle"
   "מזבח" "altar"
   "אדני" "Lord"
   "כרוב" "cherub"
   ;; ── 5-letter (5) ──────────────────────────────
   "ישראל" "Israel"
   "אלהים" "Elohim"
   "חכמה" "wisdom"
   "משפט" "judgment"
   "קרבן" "offering"))

(def words (keys vocab))

;; ── Utilities ──────────────────────────────────────────────

(defn- bar
  "Simple horizontal bar for terminal display."
  [n max-n width]
  (let [filled (if (pos? max-n)
                 (int (* width (/ (double n) max-n)))
                 0)]
    (apply str (repeat filled "█"))))

(defn- divides? [n d]
  (and (pos? d) (zero? (mod n d))))

(defn- divisibility-tag [n]
  (str/join "," (filter some?
                        [(when (divides? n 7) "÷7")
                         (when (divides? n 13) "÷13")
                         (when (divides? n 50) "÷50")
                         (when (divides? n 67) "÷67")])))

;; ── PART 1: Full Scan ─────────────────────────────────────

(defn part-1-full-scan
  "Compute preimage for every word. Return {word -> hits-vec}."
  []
  (println "================================================================")
  (println "  PART 1: FULL PREIMAGE SCAN — ALL WORDS")
  (println "  Scanning 43,550 a-fibers for 104 Hebrew words...")
  (println "================================================================")
  (println)

  (let [t0 (System/currentTimeMillis)
        results (coords/preimage-all words)
        elapsed (- (System/currentTimeMillis) t0)]

    (println (format "  Scan complete in %.1f seconds." (/ elapsed 1000.0)))
    (println)

    ;; Sort by count descending
    (let [rows (sort-by (comp - count val) results)
          max-count (count (val (first rows)))]

      (println (format "  %-8s %-18s %5s  %5s  %s"
                       "Word" "Meaning" "Count" "GV" ""))
      (println (str "  " (apply str (repeat 65 "─"))))

      (doseq [[word hits] rows]
        (let [cnt (count hits)
              wgv (gem/word-value word)
              tag (divisibility-tag cnt)]
          (println (format "  %-8s %-18s %5d  %5d  %s  %s"
                           word (get vocab word "")
                           cnt wgv
                           (bar cnt max-count 30)
                           (if (seq tag) (str "[" tag "]") "")))))
      (println)

      ;; Summary stats
      (let [counts (map (comp count val) rows)
            total-hits (reduce + counts)]
        (println (format "  Total words scanned: %d" (count words)))
        (println (format "  Total hits across all words: %,d" total-hits))
        (println (format "  Words with 0 hits: %d"
                         (count (filter zero? counts))))
        (println))

      results)))

;; ── PART 2: Count Groups ─────────────────────────────────

(defn part-2-count-groups
  "Group words by hit count. Check if counts have axis divisibility."
  [results]
  (println "================================================================")
  (println "  PART 2: COUNT GROUPS — WORDS SHARING THE SAME HIT COUNT")
  (println "================================================================")
  (println)

  (let [by-count (->> results
                      (map (fn [[w hits]] {:word w :count (count hits)}))
                      (group-by :count)
                      (sort-by key >))]

    (doseq [[cnt group] by-count]
      (let [tag (divisibility-tag cnt)
            word-list (str/join ", " (map :word group))]
        (println (format "  Count %3d %s: %s"
                         cnt
                         (if (seq tag) (format " [%s]" tag) "      ")
                         word-list))))
    (println)

    ;; Which counts divide by axis numbers?
    (println "  ── Axis-divisible counts ──")
    (let [all-counts (sort > (keys by-count))]
      (doseq [d [7 13 50 67]]
        (let [matching (filter #(divides? % d) all-counts)]
          (when (seq matching)
            (println (format "    ÷%d: counts %s" d (str/join ", " matching))))))
      (println))))

;; ── PART 3: Co-fiber Analysis ─────────────────────────────

(defn part-3-co-fibers
  "Invert: (b,c,d) → set of words. Report multi-word fibers."
  [results]
  (println "================================================================")
  (println "  PART 3: CO-FIBER ANALYSIS — MULTI-WORD ADDRESSES")
  (println "  Which a-fibers carry 2+ different words?")
  (println "================================================================")
  (println)

  (let [;; Build inverted index: [b c d] -> set of words
        fiber-words (atom (transient {}))
        _ (doseq [[word hits] results
                  {:keys [b c d]} hits]
            (let [k [b c d]]
              (swap! fiber-words
                     (fn [m]
                       (let [cur (get m k #{})]
                         (assoc! m k (conj cur word)))))))
        fiber-map (persistent! @fiber-words)

        ;; Filter to multi-word fibers
        multi (sort-by (comp - count val)
                       (filter #(> (count (val %)) 1) fiber-map))]

    (println (format "  Total distinct (b,c,d) addresses with any word: %,d"
                     (count fiber-map)))
    (println (format "  Addresses with 2+ words: %,d" (count multi)))
    (println)

    ;; Distribution of word counts per fiber
    (let [dist (frequencies (map (comp count val) fiber-map))]
      (println "  Words per fiber distribution:")
      (doseq [n (sort (keys dist))]
        (println (format "    %d word(s): %,d fibers" n (get dist n))))
      (println))

    ;; Top 30 most word-rich fibers
    (println "  ── TOP 30 WORD-RICHEST FIBERS ──")
    (println)
    (let [s (coords/space)]
      (doseq [[[b c d] word-set] (take 30 multi)]
        (let [fib (coords/fiber :a {:b b :c c :d d})
              text (apply str (map #(coords/letter-at s %) (seq fib)))
              gv (reduce + (map #(coords/gv-at s %) (seq fib)))
              v (coords/verse-at s (aget fib 0))
              meanings (str/join ", " (map #(format "%s(%s)" % (get vocab % ""))
                                           (sort word-set)))]
          (println (format "    (*,%2d,%2d,%2d)  %d words  gv=%d  near %s %d:%d"
                           b c d (count word-set) gv
                           (:book v) (:ch v) (:vs v)))
          (println (format "      fiber: %s" text))
          (println (format "      words: %s" meanings))
          (println))))

    ;; Return for downstream use
    {:fiber-map fiber-map :multi multi}))

;; ── PART 4: Axis Projections ──────────────────────────────

(defn part-4-axis-projections
  "Project each word's preimage onto c-axis (13 bins) and d-axis (67 bins).
   Find which words cluster on specific axis values."
  [results]
  (println "================================================================")
  (println "  PART 4: AXIS PROJECTIONS — WHERE DO WORDS CLUSTER?")
  (println "================================================================")
  (println)

  ;; c-axis projection: for each word, how many hits at each c value?
  (println "  ── c-AXIS PROJECTION (13 bins: love/unity) ──")
  (println)

  (let [key-words ["תורה" "יהוה" "שמר" "אהבה" "ברית" "משה" "אור" "קדש" "שלם"
                    "אחד" "כהן" "מלך" "את" "שם" "אל" "חסד" "שבת" "חיים"
                    "עולם" "מזבח" "ברא" "שמע" "צדק"]
        present-keys (filter #(pos? (count (get results % []))) key-words)]

    ;; Header
    (print (format "  %-8s" "c="))
    (doseq [c (range 13)] (print (format " %3d" c)))
    (println "   total")
    (println (str "  " (apply str (repeat 65 "─"))))

    (doseq [w present-keys]
      (let [hits (get results w [])
            c-dist (frequencies (map :c hits))
            total (count hits)]
        (print (format "  %-8s" w))
        (doseq [c (range 13)]
          (print (format " %3d" (get c-dist c 0))))
        (println (format "   %3d" total))))
    (println)

    ;; Aggregate c-axis heat: sum all words' hits per c value
    (println "  ── AGGREGATE c-AXIS HEAT (all words combined) ──")
    (let [all-hits (mapcat val results)
          c-heat (frequencies (map :c all-hits))
          expected (/ (count all-hits) 13.0)
          max-heat (apply max (vals c-heat))]
      (doseq [c (range 13)]
        (let [h (get c-heat c 0)]
          (println (format "    c=%2d: %5d  (%.2f×)  %s"
                           c h (/ h expected)
                           (bar h max-heat 30)))))
      (println)))

  ;; d-axis projection
  (println "  ── d-AXIS PROJECTION (67 bins: understanding) ──")
  (println)

  (let [all-hits (mapcat val results)
        d-heat (frequencies (map :d all-hits))
        expected (/ (count all-hits) 67.0)
        max-heat (apply max (vals d-heat))
        ;; Top 15 and bottom 5
        sorted-d (sort-by val > d-heat)]

    (println "  Top 15 hottest d-values:")
    (doseq [[d h] (take 15 sorted-d)]
      (println (format "    d=%2d: %5d  (%.2f×)  %s"
                       d h (/ h expected)
                       (bar h max-heat 25))))
    (println)

    (println "  Bottom 5 coldest d-values:")
    (doseq [[d h] (take 5 (reverse sorted-d))]
      (println (format "    d=%2d: %5d  (%.2f×)" d h (/ h expected))))
    (println)

    ;; d=33 (center) and d=0 (origin) specifically
    (println "  Special d-values:")
    (println (format "    d= 0 (origin):     %d hits (%.2f×)"
                     (get d-heat 0 0)
                     (/ (get d-heat 0 0) expected)))
    (println (format "    d=33 (center):     %d hits (%.2f×)"
                     (get d-heat 33 0)
                     (/ (get d-heat 33 0) expected)))
    (println (format "    d=66 (boundary):   %d hits (%.2f×)"
                     (get d-heat 66 0)
                     (/ (get d-heat 66 0) expected)))
    (println)))

;; ── PART 5: Semantic Clusters ─────────────────────────────

(defn part-5-semantic-clusters
  "Group co-located words and check for semantic coherence."
  [co-fiber-data results]
  (println "================================================================")
  (println "  PART 5: SEMANTIC CLUSTERS — MEANINGFUL NEIGHBORHOODS?")
  (println "  Words at the same (b,c,d) share a 7-letter window.")
  (println "================================================================")
  (println)

  (let [{:keys [fiber-map multi]} co-fiber-data

        ;; Semantic categories
        categories {"divine"     #{"יהוה" "אלהים" "אלהם" "אל" "קדש" "אדני" "כרוב"}
                    "covenant"   #{"ברית" "תורה" "שמר" "אמת" "עד" "כרת" "נדר" "אמן" "קשת"}
                    "people"     #{"ישראל" "עם" "כהן" "מלך" "משה" "אהרן" "אדם" "גר" "עבד" "נח"}
                    "life"       #{"חי" "חיים" "נפש" "רוח" "דם" "זרע" "מות" "עץ" "פרי"}
                    "creation"   #{"אור" "שמים" "ארץ" "מים" "יום" "ברא" "עולם" "ענן" "אבן" "צור"
                                   "גן" "חשך" "באר"}
                    "relation"   #{"אב" "אם" "בן" "אהבה" "חן" "רחם" "חסד" "סלח"}
                    "moral"      #{"טוב" "רע" "צדקה" "צדק" "חכם" "חכמה" "שלם" "חטא" "טהר" "משפט"
                                   "דעת" "ענה" "נחש"}
                    "cult"       #{"מזבח" "משכן" "מועד" "קרבן" "כפר" "עלה" "כבש" "פר" "שמן" "יין"
                                   "לחם" "שבת" "כבד" "זבח" "מטה"}
                    "action"     #{"דבר" "ברך" "גאל" "שמע" "ידע" "ראה" "הלך" "ישב" "עמד"
                                   "נשא" "שלח" "קרא" "נתן" "עשה" "בוא" "דרך" "שפך" "נקם"
                                   "פדה" "ירש" "חרב"}}

        categorize (fn [word]
                     (first (for [[cat ws] categories
                                  :when (ws word)]
                              cat)))

        ;; Find multi-word fibers where all words share a category
        coherent (atom [])
        mixed (atom [])]

    (doseq [[[b c d] word-set] multi]
      (let [cats (set (keep categorize word-set))]
        (if (= 1 (count cats))
          (swap! coherent conj {:b b :c c :d d :words word-set
                                :category (first cats)})
          (swap! mixed conj {:b b :c c :d d :words word-set
                             :categories cats}))))

    (println (format "  Multi-word fibers: %d total" (count multi)))
    (println (format "    Same category: %d (coherent)" (count @coherent)))
    (println (format "    Mixed categories: %d" (count @mixed)))
    (println)

    ;; Show coherent clusters by category
    (println "  ── COHERENT CLUSTERS (same semantic category) ──")
    (let [by-cat (group-by :category @coherent)]
      (doseq [[cat fibers] (sort-by (comp - count val) by-cat)]
        (println (format "    %s: %d fibers" cat (count fibers)))
        (doseq [{:keys [b c d words]} (take 5 fibers)]
          (println (format "      (*,%2d,%2d,%2d): %s"
                           b c d
                           (str/join ", " (sort words)))))))
    (println)

    ;; Word co-occurrence: which word pairs co-locate most often?
    (println "  ── WORD PAIR CO-OCCURRENCE ──")
    (let [pair-counts (atom (transient {}))
          _ (doseq [[_ word-set] multi]
              (let [ws (sort (seq word-set))]
                (doseq [i (range (count ws))
                        j (range (inc i) (count ws))]
                  (let [pair [(nth ws i) (nth ws j)]]
                    (swap! pair-counts
                           (fn [m] (assoc! m pair (inc (get m pair 0)))))))))
          pairs (sort-by val > (persistent! @pair-counts))]

      (println (format "  Total word pairs that co-locate: %d" (count pairs)))
      (println)
      (println "  Top 25 co-occurring pairs:")
      (println (format "    %-8s %-8s %5s  %s"
                       "Word 1" "Word 2" "Count" "Meanings"))
      (println (str "    " (apply str (repeat 55 "─"))))

      (doseq [[[w1 w2] cnt] (take 25 pairs)]
        (println (format "    %-8s %-8s %5d  %s + %s"
                         w1 w2 cnt
                         (get vocab w1 "") (get vocab w2 ""))))
      (println))))

;; ── PART 6: Preimage Fingerprints ─────────────────────────

(defn- cosine-sim
  "Cosine similarity between two double arrays."
  [^doubles a ^doubles b]
  (let [n (alength a)]
    (loop [i 0, dot 0.0, na 0.0, nb 0.0]
      (if (= i n)
        (let [denom (* (Math/sqrt na) (Math/sqrt nb))]
          (if (pos? denom) (/ dot denom) 0.0))
        (let [ai (aget a i)
              bi (aget b i)]
          (recur (inc i)
                 (+ dot (* ai bi))
                 (+ na (* ai ai))
                 (+ nb (* bi bi))))))))

(defn part-6-fingerprints
  "Represent each word's preimage as a feature vector:
   [count, c-distribution(13), d-distribution(67), mean-gv].
   Compute pairwise similarity."
  [results]
  (println "================================================================")
  (println "  PART 6: PREIMAGE FINGERPRINTS — WORD SIMILARITY")
  (println "  Feature vector: [count, c-dist(13), d-dist(67), mean-gv]")
  (println "================================================================")
  (println)

  (let [;; Build feature vectors (only for words with hits)
        active (filter #(pos? (count (val %))) results)

        fingerprints
        (into {}
              (for [[word hits] active]
                (let [cnt (count hits)
                      c-dist (let [d (frequencies (map :c hits))
                                   total (double cnt)]
                               (double-array (for [c (range 13)]
                                               (/ (get d c 0) total))))
                      d-dist (let [d (frequencies (map :d hits))
                                   total (double cnt)]
                               (double-array (for [dd (range 67)]
                                               (/ (get d dd 0) total))))
                      mean-gv (/ (double (reduce + (map :gv hits))) cnt)
                      ;; Concatenate into single feature vector
                      ;; [normalized-count, c-dist(13), d-dist(67), normalized-gv]
                      fv (double-array 82)  ; 1 + 13 + 67 + 1
                      max-possible 43550.0]
                  (aset fv 0 (/ cnt max-possible))
                  (dotimes [i 13] (aset fv (+ 1 i) (aget c-dist i)))
                  (dotimes [i 67] (aset fv (+ 14 i) (aget d-dist i)))
                  (aset fv 81 (/ mean-gv 2000.0))  ;; normalize
                  [word {:fv fv :count cnt :mean-gv mean-gv
                         :c-dist (vec c-dist) :d-dist (vec d-dist)}])))

        ;; Pairwise similarity
        word-list (sort (keys fingerprints))
        pairs (atom (transient []))]

    ;; Compute pairwise cosine similarity
    (doseq [i (range (count word-list))
            j (range (inc i) (count word-list))]
      (let [w1 (nth word-list i)
            w2 (nth word-list j)
            sim (cosine-sim (:fv (get fingerprints w1))
                            (:fv (get fingerprints w2)))]
        (swap! pairs conj! {:w1 w1 :w2 w2 :sim sim})))

    (let [all-pairs (sort-by :sim > (persistent! @pairs))]

      ;; Most similar pairs
      (println "  ── TOP 25 MOST SIMILAR WORD PAIRS ──")
      (println (format "    %-8s %-8s %8s  %s"
                       "Word 1" "Word 2" "Cosine" "Meanings"))
      (println (str "    " (apply str (repeat 55 "─"))))

      (doseq [{:keys [w1 w2 sim]} (take 25 all-pairs)]
        (println (format "    %-8s %-8s %8.4f  %s + %s"
                         w1 w2 sim
                         (get vocab w1 "") (get vocab w2 ""))))
      (println)

      ;; Most isolated words (lowest average similarity)
      (println "  ── MOST ISOLATED WORDS (lowest avg similarity) ──")
      (let [avg-sims (for [w word-list]
                       (let [relevant (filter #(or (= w (:w1 %)) (= w (:w2 %)))
                                              all-pairs)
                             avg (if (seq relevant)
                                   (/ (reduce + (map :sim relevant))
                                      (double (count relevant)))
                                   0.0)]
                         {:word w :avg-sim avg :count (:count (get fingerprints w))}))]
        (doseq [{:keys [word avg-sim count]} (take 10 (sort-by :avg-sim avg-sims))]
          (println (format "    %-8s  avg-sim=%.4f  count=%d  %s"
                           word avg-sim count (get vocab word ""))))
        (println))

      ;; Most connected words
      (println "  ── MOST CONNECTED WORDS (highest avg similarity) ──")
      (let [avg-sims (for [w word-list]
                       (let [relevant (filter #(or (= w (:w1 %)) (= w (:w2 %)))
                                              all-pairs)
                             avg (/ (reduce + (map :sim relevant))
                                    (double (count relevant)))]
                         {:word w :avg-sim avg :count (:count (get fingerprints w))}))]
        (doseq [{:keys [word avg-sim count]} (take 10 (sort-by (comp - :avg-sim) avg-sims))]
          (println (format "    %-8s  avg-sim=%.4f  count=%d  %s"
                           word avg-sim count (get vocab word ""))))
        (println))

      ;; c-distribution entropy per word
      (println "  ── c-AXIS ENTROPY (lower = more clustered) ──")
      (let [entropy (fn [dist]
                      (- (reduce + (map (fn [p]
                                          (if (pos? p)
                                            (* p (Math/log p))
                                            0.0))
                                        dist))))
            word-entropy (sort-by :entropy
                                  (for [[w fp] fingerprints]
                                    {:word w
                                     :entropy (entropy (:c-dist fp))
                                     :count (:count fp)}))]
        (println "  Most clustered on c-axis (low entropy):")
        (doseq [{:keys [word entropy count]} (take 10 word-entropy)]
          (println (format "    %-8s  H=%.3f  n=%d  %s"
                           word entropy count (get vocab word ""))))
        (println)
        (println "  Most uniform on c-axis (high entropy):")
        (doseq [{:keys [word entropy count]} (take 10 (reverse word-entropy))]
          (println (format "    %-8s  H=%.3f  n=%d  %s"
                           word entropy count (get vocab word ""))))
        (println)))))

;; ── Verification ──────────────────────────────────────────

(defn verify-against-preimage
  "Check that preimage-all matches individual preimage calls for key words."
  [results]
  (println "================================================================")
  (println "  VERIFICATION: preimage-all vs individual preimage")
  (println "================================================================")
  (println)

  (let [check-words ["תורה" "יהוה" "שמר" "אהבה" "ברית"]
        known-counts {"תורה" 8 "יהוה" 12 "שמר" 22 "אהבה" 5 "ברית" 4}]
    (doseq [w check-words]
      (let [batch-count (count (get results w []))
            individual (coords/preimage w)
            ind-count (count individual)
            known (get known-counts w)
            match? (= batch-count ind-count)
            known-match? (= batch-count known)]
        (println (format "  %-6s  batch=%d  individual=%d  known=%d  %s"
                         w batch-count ind-count known
                         (cond
                           (and match? known-match?) "✓ ALL MATCH"
                           match? "✓ batch=individual (known differs)"
                           :else "✗ MISMATCH")))))
    (println)))

;; ── Main ──────────────────────────────────────────────────

(defn -main []
  (println)
  (println "╔══════════════════════════════════════════════════════════════╗")
  (println "║  EXPERIMENT 077: PREIMAGE CLUSTERING                       ║")
  (println "║  Word Fingerprints in the 4D Space                         ║")
  (println "║                                                            ║")
  (println "║  104 Hebrew words × 43,550 a-fibers                       ║")
  (println "║  Single-pass scan via preimage-all                         ║")
  (println "╚══════════════════════════════════════════════════════════════╝")
  (println)

  ;; Run all parts
  (let [results (part-1-full-scan)]

    (verify-against-preimage results)

    (part-2-count-groups results)

    (let [co-fiber-data (part-3-co-fibers results)]

      (part-4-axis-projections results)

      (part-5-semantic-clusters co-fiber-data results))

    (part-6-fingerprints results))

  (println)
  (println "Done."))
