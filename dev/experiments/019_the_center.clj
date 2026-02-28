(ns experiments.019-the-center
  "The center of the Torah under every measure.
   Where do different definitions of 'center' converge?
   And: narrative chiasm within individual books.
   Run: clojure -M:dev -m experiments.019-the-center"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(defn verse-data
  "Get all verse data for a book. Returns vec of {:book :chapter :verse :text :letters :gematria}"
  [book]
  (let [results (atom [])]
    (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
      (let [verses (sefaria/fetch-chapter book ch)]
        (doseq [[v-idx verse-text] (map-indexed vector verses)]
          (let [stripped (norm/strip-html verse-text)
                letters  (norm/letter-stream stripped)
                n        (count letters)]
            (when (pos? n)
              (swap! results conj
                     {:book book :chapter ch :verse (inc v-idx)
                      :letters n
                      :gematria (g/total letters)
                      :mean (/ (double (g/total letters)) n)}))))))
    @results))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn -main []
  (println "=== The Center ===")
  (println "  Where every definition of center converges.\n")

  ;; Load everything
  (println "Loading all data...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        all-verse-data (into {} (map (fn [b]
                                       (print (format "  %s..." b)) (flush)
                                       (let [vd (verse-data b)]
                                         (println (format " %d verses" (count vd)))
                                         [b vd]))
                                     books))
        all-letters (vec (mapcat sefaria/book-letters books))
        n (count all-letters)

        ;; All verses in order
        all-verses (vec (mapcat #(get all-verse-data %) books))
        n-verses (count all-verses)

        ;; All words in order
        all-words (atom [])
        _ (doseq [book books]
            (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
              (let [verses (sefaria/fetch-chapter book ch)]
                (doseq [[v-idx verse-text] (map-indexed vector verses)]
                  (let [stripped (norm/strip-html verse-text)
                        raw-words (str/split stripped #"[\s\u05BE]+")
                        words (->> raw-words
                                   (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                                   (filter #(pos? (count %))))]
                    (doseq [w words]
                      (swap! all-words conj
                             {:word w :book book :chapter ch :verse (inc v-idx)
                              :gematria (g/word-value w)})))))))
        all-words-v (vec @all-words)
        n-words (count all-words-v)]

    (println (format "\n  Total: %,d letters, %,d verses, %,d words.\n" n n-verses n-words))

    ;; ── 1. Six definitions of center ───────────────────────
    (println "── 1. Six Definitions of Center ──\n")

    ;; (a) Letter midpoint
    (let [letter-mid (quot n 2)
          letter-at (nth all-letters letter-mid)
          ;; Find which verse contains this letter
          cum-letters (loop [vs all-verses pos 0 result []]
                        (if (empty? vs) result
                            (let [v (first vs)
                                  next-pos (+ pos (:letters v))]
                              (recur (rest vs) next-pos
                                     (conj result {:verse v :cum-start pos :cum-end next-pos})))))]
      (let [center-verse (first (filter #(and (>= letter-mid (:cum-start %))
                                               (< letter-mid (:cum-end %)))
                                         cum-letters))]
        (println (format "  (a) Letter midpoint: position %,d of %,d" letter-mid n))
        (println (format "      Letter: %c (value %d)" letter-at (g/letter-value letter-at)))
        (when center-verse
          (println (format "      In: %s %d:%d"
                           (get-in center-verse [:verse :book])
                           (get-in center-verse [:verse :chapter])
                           (get-in center-verse [:verse :verse]))))))

    ;; (b) Gematria midpoint — where cumulative gematria = half total
    (let [total-gem (reduce + (map #(long (g/letter-value %)) all-letters))
          half-gem (quot total-gem 2)]
      (println (format "\n  (b) Gematria midpoint: cumulative sum reaches %,d of %,d" half-gem total-gem))
      (let [result (loop [i 0 sum 0]
                     (if (>= sum half-gem)
                       i
                       (recur (inc i) (+ sum (long (g/letter-value (nth all-letters i)))))))]
        (println (format "      At letter position: %,d" result))
        (println (format "      Letter: %c (value %d)"
                         (nth all-letters result) (g/letter-value (nth all-letters result))))
        ;; Find verse
        (let [cum-letters (loop [vs all-verses pos 0 result-v []]
                            (if (empty? vs) result-v
                                (let [v (first vs)
                                      next-pos (+ pos (:letters v))]
                                  (recur (rest vs) next-pos
                                         (conj result-v {:verse v :cum-start pos :cum-end next-pos})))))
              center-verse (first (filter #(and (>= result (:cum-start %))
                                                 (< result (:cum-end %)))
                                           cum-letters))]
          (when center-verse
            (println (format "      In: %s %d:%d"
                             (get-in center-verse [:verse :book])
                             (get-in center-verse [:verse :chapter])
                             (get-in center-verse [:verse :verse])))))))

    ;; (c) Verse midpoint
    (let [verse-mid (quot n-verses 2)
          mid-verse (nth all-verses verse-mid)]
      (println (format "\n  (c) Verse midpoint: verse %,d of %,d" verse-mid n-verses))
      (println (format "      %s %d:%d (%d letters)"
                       (:book mid-verse) (:chapter mid-verse) (:verse mid-verse)
                       (:letters mid-verse))))

    ;; (d) Word midpoint
    (let [word-mid (quot n-words 2)
          mid-word (nth all-words-v word-mid)]
      (println (format "\n  (d) Word midpoint: word %,d of %,d" word-mid n-words))
      (println (format "      \"%s\" (gematria %d)"
                       (:word mid-word) (:gematria mid-word)))
      (println (format "      In: %s %d:%d"
                       (:book mid-word) (:chapter mid-word) (:verse mid-word))))

    ;; (e) Center of mass (position-weighted by gematria)
    (let [total-gem (reduce + (map #(long (g/letter-value %)) all-letters))
          ;; Center of mass = Σ(i × g_i) / Σ(g_i)
          ;; But this is expensive. Use chapter-level approximation.
          chapter-data (for [book books
                             ch (range 1 (inc (get sefaria/book-chapters book)))]
                         (let [vd (filter #(and (= (:book %) book)
                                                 (= (:chapter %) ch))
                                           all-verses)]
                           {:book book :chapter ch
                            :letters (reduce + (map :letters vd))
                            :gematria (reduce + (map :gematria vd))}))
          ;; Cumulative letter position at chapter start
          cum-positions (loop [chs chapter-data pos 0 result []]
                          (if (empty? chs) result
                              (let [c (first chs)]
                                (recur (rest chs)
                                       (+ pos (:letters c))
                                       (conj result (assoc c :start-pos pos))))))
          ;; Center of mass
          total-weight (reduce + (map :gematria cum-positions))
          com (/ (reduce + (map (fn [c]
                                   (* (+ (:start-pos c) (/ (:letters c) 2.0))
                                      (:gematria c)))
                                 cum-positions))
                 (double total-weight))
          ;; Find which chapter contains the center of mass
          com-chapter (first (filter #(and (>= com (:start-pos %))
                                            (< com (+ (:start-pos %) (:letters %))))
                                      cum-positions))]
      (println (format "\n  (e) Center of mass (gematria-weighted): position %.0f" com))
      (when com-chapter
        (println (format "      In: %s %d" (:book com-chapter) (:chapter com-chapter)))))

    ;; (f) Chapter midpoint (187 chapters total, middle chapter)
    (let [chapter-list (for [book books
                              ch (range 1 (inc (get sefaria/book-chapters book)))]
                         [book ch])
          n-ch (count chapter-list)
          mid-ch (quot n-ch 2)
          [mid-book mid-chapter] (nth (vec chapter-list) mid-ch)]
      (println (format "\n  (f) Chapter midpoint: chapter %d of %d" (inc mid-ch) n-ch))
      (println (format "      %s %d" mid-book mid-chapter)))

    ;; ── 2. The center passage ──────────────────────────────
    (println "\n── 2. The Center Passage ──")
    (println "  What are the verses around the center?\n")

    (let [mid-idx (quot n-verses 2)]
      (doseq [offset (range -5 6)]
        (let [v (nth all-verses (+ mid-idx offset))]
          (println (format "  %s %s %d:%d  (%d letters, gem %,d, mean %.1f)"
                           (if (zero? offset) ">>>" "   ")
                           (:book v) (:chapter v) (:verse v)
                           (:letters v) (:gematria v) (:mean v))))))

    ;; ── 3. Narrative chiasm within Genesis ─────────────────
    (println "\n── 3. Narrative Chiasm Within Genesis ──")
    (println "  Using scholarly divisions:\n")
    (println "  A  = Chapters 1-11   (Primeval: creation, flood, Babel)")
    (println "  B  = Chapters 12-25  (Abraham cycle)")
    (println "  B' = Chapters 26-36  (Jacob cycle)")
    (println "  A' = Chapters 37-50  (Joseph novella)\n")

    (let [gen-verses (get all-verse-data "Genesis")
          section-of (fn [ch]
                       (cond (<= ch 11) :A
                             (<= ch 25) :B
                             (<= ch 36) :B2
                             :else :A2))
          sections (group-by #(section-of (:chapter %)) gen-verses)
          counts (into {} (map (fn [[k vs]]
                                  [k {:letters (reduce + (map :letters vs))
                                      :gematria (reduce + (map :gematria vs))
                                      :verses (count vs)}])
                                sections))]
      (println (format "  Section  Chapters  Verses  Letters    Gematria"))
      (println (apply str (repeat 55 "─")))
      (doseq [[k label] [[:A "A  (1-11)"] [:B "B  (12-25)"]
                           [:B2 "B' (26-36)"] [:A2 "A' (37-50)"]]]
        (let [c (get counts k)]
          (println (format "  %-12s       %5d  %,7d  %,10d" label (:verses c) (:letters c) (:gematria c)))))

      ;; Ratios
      (let [a  (:letters (get counts :A))
            b  (:letters (get counts :B))
            b2 (:letters (get counts :B2))
            a2 (:letters (get counts :A2))]
        (println (format "\n  A/A'   = %d/%d = %.4f" a a2 (/ (double a) a2)))
        (println (format "  B/B'   = %d/%d = %.4f" b b2 (/ (double b) b2)))
        (println (format "  |B-B'| = %,d letters" (Math/abs (- b b2))))
        (println (format "  (A+B)/(B'+A') = %.4f  (cf π/2 = 1.5708)"
                         (/ (double (+ a b)) (+ b2 a2))))))

    ;; ── 4. Narrative chiasm within Deuteronomy ─────────────
    (println "\n── 4. Narrative Chiasm Within Deuteronomy ──")
    (println "  A  = Chapters 1-4    (First retrospective speech)")
    (println "  B  = Chapters 5-11   (Covenant and commandments)")
    (println "  C  = Chapters 12-26  (Legal code)")
    (println "  B' = Chapters 27-30  (Blessings, curses, renewal)")
    (println "  A' = Chapters 31-34  (Final words and death of Moses)\n")

    (let [deut-verses (get all-verse-data "Deuteronomy")
          section-of (fn [ch]
                       (cond (<= ch 4) :A
                             (<= ch 11) :B
                             (<= ch 26) :C
                             (<= ch 30) :B2
                             :else :A2))
          sections (group-by #(section-of (:chapter %)) deut-verses)
          counts (into {} (map (fn [[k vs]]
                                  [k {:letters (reduce + (map :letters vs))
                                      :gematria (reduce + (map :gematria vs))
                                      :verses (count vs)}])
                                sections))]
      (println (format "  Section   Chapters  Verses  Letters    Gematria"))
      (println (apply str (repeat 58 "─")))
      (doseq [[k label] [[:A "A  (1-4)"] [:B "B  (5-11)"]
                           [:C "C  (12-26)"] [:B2 "B' (27-30)"] [:A2 "A' (31-34)"]]]
        (let [c (get counts k)]
          (println (format "  %-14s     %5d  %,7d  %,10d" label (:verses c) (:letters c) (:gematria c)))))

      (let [a  (:letters (get counts :A))
            b  (:letters (get counts :B))
            c  (:letters (get counts :C))
            b2 (:letters (get counts :B2))
            a2 (:letters (get counts :A2))]
        (println (format "\n  A/A'   = %d/%d = %.4f" a a2 (/ (double a) a2)))
        (println (format "  B/B'   = %d/%d = %.4f" b b2 (/ (double b) b2)))
        (println (format "  A+B+C  = %,d  B'+A' = %,d" (+ a b c) (+ b2 a2)))
        (println (format "  (A+B+C)/(B'+A') = %.4f" (/ (double (+ a b c)) (+ b2 a2))))))

    ;; ── 5. Narrative chiasm within Exodus ──────────────────
    (println "\n── 5. Narrative Chiasm Within Exodus ──")
    (println "  A  = Chapters 1-6    (Oppression and call of Moses)")
    (println "  B  = Chapters 7-15   (Plagues and liberation)")
    (println "  C  = Chapters 16-24  (Wilderness, Sinai, covenant)")
    (println "  B' = Chapters 25-31  (Tabernacle instructions)")
    (println "  A' = Chapters 32-40  (Golden calf, renewal, construction)\n")

    (let [exod-verses (get all-verse-data "Exodus")
          section-of (fn [ch]
                       (cond (<= ch 6) :A
                             (<= ch 15) :B
                             (<= ch 24) :C
                             (<= ch 31) :B2
                             :else :A2))
          sections (group-by #(section-of (:chapter %)) exod-verses)
          counts (into {} (map (fn [[k vs]]
                                  [k {:letters (reduce + (map :letters vs))
                                      :gematria (reduce + (map :gematria vs))
                                      :verses (count vs)}])
                                sections))]
      (println (format "  Section   Chapters  Verses  Letters    Gematria"))
      (println (apply str (repeat 58 "─")))
      (doseq [[k label] [[:A "A  (1-6)"] [:B "B  (7-15)"]
                           [:C "C  (16-24)"] [:B2 "B' (25-31)"] [:A2 "A' (32-40)"]]]
        (let [c (get counts k)]
          (println (format "  %-14s     %5d  %,7d  %,10d" label (:verses c) (:letters c) (:gematria c)))))

      (let [a  (:letters (get counts :A))
            b  (:letters (get counts :B))
            c  (:letters (get counts :C))
            b2 (:letters (get counts :B2))
            a2 (:letters (get counts :A2))]
        (println (format "\n  A/A'   = %d/%d = %.4f" a a2 (/ (double a) a2)))
        (println (format "  B/B'   = %d/%d = %.4f" b b2 (/ (double b) b2)))
        (println (format "  (A+B+C)/(B'+A') = %.4f" (/ (double (+ a b c)) (+ b2 a2))))))

    ;; ── 6. The convergence ─────────────────────────────────
    (println "\n── 6. Convergence of Centers ──")
    (println "  How close are the different center definitions?\n")

    ;; Compute letter positions for all center definitions
    (let [letter-mid (quot n 2)
          ;; Word midpoint → letter position
          word-mid-idx (quot n-words 2)
          ;; Sum letters of all words before the center word
          word-letter-pos (loop [i 0 pos 0]
                            (if (= i word-mid-idx) pos
                                (recur (inc i)
                                       (+ pos (count (:word (nth all-words-v i)))))))
          ;; Verse midpoint → letter position
          verse-mid-idx (quot n-verses 2)
          verse-letter-pos (reduce + (map :letters (subvec all-verses 0 verse-mid-idx)))
          ;; Chapter midpoint
          ch-mid-pos (let [chapter-list (for [book books
                                               ch (range 1 (inc (get sefaria/book-chapters book)))]
                                          {:book book :chapter ch})
                           mid (quot (count chapter-list) 2)
                           pre-chapters (take mid chapter-list)]
                       (reduce + (map (fn [{:keys [book chapter]}]
                                         (reduce + (map :letters
                                                        (filter #(and (= (:book %) book)
                                                                       (= (:chapter %) chapter))
                                                                 all-verses))))
                                       pre-chapters)))]

      (println (format "  Definition           Letter position  Offset from letter mid"))
      (println (apply str (repeat 60 "─")))
      (println (format "  Letter midpoint:     %,12d  %+,8d" letter-mid 0))
      (println (format "  Word midpoint:       %,12d  %+,8d" word-letter-pos (- word-letter-pos letter-mid)))
      (println (format "  Verse midpoint:      %,12d  %+,8d" verse-letter-pos (- verse-letter-pos letter-mid)))
      (println (format "  Chapter midpoint:    %,12d  %+,8d" ch-mid-pos (- ch-mid-pos letter-mid)))
      (println (format "\n  Max spread: %,d letters (%.2f%% of total)"
                       (- (apply max [letter-mid word-letter-pos verse-letter-pos ch-mid-pos])
                          (apply min [letter-mid word-letter-pos verse-letter-pos ch-mid-pos]))
                       (* 100.0 (/ (double (- (apply max [letter-mid word-letter-pos verse-letter-pos ch-mid-pos])
                                               (apply min [letter-mid word-letter-pos verse-letter-pos ch-mid-pos])))
                                   n))))))

  (println "\nDone."))
