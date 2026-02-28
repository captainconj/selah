(ns experiments.020-center-of-center
  "The center of the center.
   Leviticus's internal chiastic structure.
   The Day of Atonement at the heart of the heart.
   Run: clojure -M:dev -m experiments.020-center-of-center"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))

(defn chapter-stats [book ch]
  (let [verses  (sefaria/fetch-chapter book ch)
        raw     (apply str (map norm/strip-html verses))
        letters (norm/letter-stream raw)
        n       (count letters)
        freqs   (frequencies letters)]
    {:book book :chapter ch :letters n
     :gematria (g/total letters)
     :mean (if (pos? n) (/ (double (g/total letters)) n) 0.0)
     :profile (mapv (fn [c] (/ (double (get freqs c 0)) (max 1 n))) alphabet)}))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn -main []
  (println "=== The Center of the Center ===")
  (println "  Leviticus is the center book.")
  (println "  What is the center of Leviticus?\n")

  (let [lev-stats (mapv #(chapter-stats "Leviticus" %) (range 1 28))
        n-ch (count lev-stats)
        total-letters (reduce + (map :letters lev-stats))
        total-gem (reduce + (map :gematria lev-stats))]

    (println (format "  Leviticus: %d chapters, %,d letters, gematria %,d\n"
                     n-ch total-letters total-gem))

    ;; ── 1. Scholarly chiastic structure of Leviticus ───────
    (println "── 1. Leviticus Chiastic Structure ──")
    (println "  Scholarly consensus:")
    (println "  A   = 1-7    Sacrifice laws")
    (println "  B   = 8-10   Consecration of priests")
    (println "  C   = 11-15  Purity laws")
    (println "  D   = 16     Day of Atonement (center)")
    (println "  C'  = 17-20  Holiness code")
    (println "  B'  = 21-22  Priestly regulations")
    (println "  A'  = 23-27  Festivals and vows\n")

    (let [sections {:A  [1 7]   :B  [8 10]  :C  [11 15]
                    :D  [16 16] :C2 [17 20] :B2 [21 22] :A2 [23 27]}
          section-data (into {}
                         (map (fn [[k [start end]]]
                                (let [chs (filter #(and (>= (:chapter %) start)
                                                         (<= (:chapter %) end))
                                                   lev-stats)]
                                  [k {:chapters (- end start -1)
                                      :letters (reduce + (map :letters chs))
                                      :gematria (reduce + (map :gematria chs))
                                      :mean (/ (double (reduce + (map :gematria chs)))
                                               (reduce + (map :letters chs)))}]))
                              sections))]

      (println (format "  %5s  %5s  %7s  %10s  %6s"
                       "Sec" "Chs" "Letters" "Gematria" "Mean"))
      (println (apply str (repeat 42 "─")))
      (doseq [k [:A :B :C :D :C2 :B2 :A2]]
        (let [d (get section-data k)]
          (println (format "  %-5s  %5d  %,7d  %,10d  %6.1f"
                           (name k) (:chapters d) (:letters d) (:gematria d) (:mean d)))))

      ;; Ratios
      (let [a  (:letters (get section-data :A))
            b  (:letters (get section-data :B))
            c  (:letters (get section-data :C))
            d  (:letters (get section-data :D))
            c2 (:letters (get section-data :C2))
            b2 (:letters (get section-data :B2))
            a2 (:letters (get section-data :A2))]
        (println (format "\n  A/A'   = %,d/%,d = %.4f" a a2 (/ (double a) a2)))
        (println (format "  B/B'   = %,d/%,d = %.4f" b b2 (/ (double b) b2)))
        (println (format "  C/C'   = %,d/%,d = %.4f" c c2 (/ (double c) c2)))
        (println (format "  |B-B'| = %,d letters" (Math/abs (- b b2))))
        (println (format "  |C-C'| = %,d letters" (Math/abs (- c c2))))
        (println (format "  D (center) = %,d letters" d))
        (println (format "  First half (A+B+C+D) = %,d" (+ a b c d)))
        (println (format "  Second half (C'+B'+A') = %,d" (+ c2 b2 a2)))
        (println (format "  Ratio = %.4f" (/ (double (+ a b c d)) (+ c2 b2 a2))))

        ;; Profile comparison
        (println "\n  Profile cosine similarity between paired sections:")
        (let [prof (fn [k]
                     (let [[start end] (get sections k)
                           chs (filter #(and (>= (:chapter %) start)
                                              (<= (:chapter %) end))
                                        lev-stats)
                           all-letters (mapcat (fn [{:keys [book chapter]}]
                                                 (let [vs (sefaria/fetch-chapter book chapter)
                                                       raw (apply str (map norm/strip-html vs))]
                                                   (norm/letter-stream raw)))
                                               chs)
                           n (count all-letters)
                           freqs (frequencies all-letters)]
                       (mapv (fn [ch] (/ (double (get freqs ch 0)) (max 1 n))) alphabet)))]
          (println (format "    A ↔ A':  %.4f" (cosine-sim (prof :A) (prof :A2))))
          (println (format "    B ↔ B':  %.4f" (cosine-sim (prof :B) (prof :B2))))
          (println (format "    C ↔ C':  %.4f" (cosine-sim (prof :C) (prof :C2)))))))

    ;; ── 2. Chapter 16: Day of Atonement ───────────────────
    (println "\n── 2. Chapter 16 — The Day of Atonement ──")
    (println "  The center of the center book.\n")

    (let [ch16 (nth lev-stats 15)  ;; 0-indexed
          letters (sefaria/book-letters "Leviticus")
          ;; Position of ch16 within Leviticus
          cum (reductions + (map :letters lev-stats))
          ch16-start (if (> 15 0) (nth (vec cum) 14) 0)
          ch16-end (nth (vec cum) 15)
          ch16-letters (subvec (vec letters) ch16-start ch16-end)
          center-of-lev (quot total-letters 2)
          ;; Does ch16 contain the center of Leviticus?
          contains-center (and (>= center-of-lev ch16-start)
                               (< center-of-lev ch16-end))]
      (println (format "  Chapter 16: %,d letters, gematria %,d" (:letters ch16) (:gematria ch16)))
      (println (format "  Leviticus center: position %,d" center-of-lev))
      (println (format "  Chapter 16 range: %,d to %,d" ch16-start ch16-end))
      (println (format "  Contains Leviticus center? %s" (if contains-center "YES" "NO")))

      ;; Find what's at the center of Leviticus
      (let [center-letter (nth (vec letters) center-of-lev)]
        (println (format "  Center letter of Leviticus: %c (value %d)" center-letter (g/letter-value center-letter)))
        ;; What verse is the center in?
        (let [verses (sefaria/fetch-chapter "Leviticus" 16)
              verse-lens (mapv (fn [v]
                                 (count (norm/letter-stream (norm/strip-html v))))
                               verses)
              center-in-ch (- center-of-lev ch16-start)
              cum-v (vec (reductions + verse-lens))
              verse-idx (count (take-while #(<= % center-in-ch) cum-v))]
          (when (< verse-idx (count verses))
            (println (format "  Center verse: Leviticus 16:%d" (inc verse-idx))))))

      ;; The two-birds ritual: Leviticus 14
      (println "\n  The Leviticus 14 connection (two birds, purification):")
      (let [ch14 (nth lev-stats 13)
            ch14-start (if (> 13 0) (nth (vec cum) 12) 0)]
        (println (format "  Chapter 14: %,d letters, starts at position %,d" (:letters ch14) ch14-start))))

    ;; ── 3. Internal palindrome of Leviticus ────────────────
    (println "\n── 3. Internal Palindrome of Leviticus ──")
    (println "  Chapter-by-chapter mirror.\n")

    (let [lens (mapv :letters lev-stats)
          gems (mapv :gematria lev-stats)
          means (mapv :mean lev-stats)
          n-ch (count lev-stats)
          half (quot n-ch 2)]

      ;; Print chapters with their mirror partners
      (println (format "  %3s  %6s  %6s  %6s  %6s  %6s  %6s"
                       "" "Ch" "Letters" "Gem" "Mirror" "Letters" "Gem"))
      (println (apply str (repeat 50 "─")))
      (doseq [i (range half)]
        (let [j (- n-ch 1 i)
              a (nth lev-stats i)
              b (nth lev-stats j)]
          (println (format "  %3d  %3d    %,6d  %,6d    %3d    %,6d  %,6d"
                           (inc i) (:chapter a) (:letters a) (:gematria a)
                           (:chapter b) (:letters b) (:gematria b)))))
      (when (odd? n-ch)
        (let [mid (nth lev-stats half)]
          (println (format "       %3d    %,6d  %,6d  ← CENTER"
                           (:chapter mid) (:letters mid) (:gematria mid)))))

      ;; Palindrome scores
      (let [first-h (subvec lens 0 half)
            second-h (vec (reverse (subvec lens (- n-ch half))))
            len-pal (cosine-sim (mapv double first-h) (mapv double second-h))
            first-g (subvec gems 0 half)
            second-g (vec (reverse (subvec gems (- n-ch half))))
            gem-pal (cosine-sim (mapv double first-g) (mapv double second-g))
            first-m (subvec means 0 half)
            second-m (vec (reverse (subvec means (- n-ch half))))
            mean-pal (cosine-sim first-m second-m)]
        (println (format "\n  Palindrome scores:"))
        (println (format "    Chapter lengths: %.4f" len-pal))
        (println (format "    Chapter gematria: %.4f" gem-pal))
        (println (format "    Mean gematria: %.4f" mean-pal))))

    ;; ── 4. The center chapter of the center book ──────────
    (println "\n── 4. Leviticus 14 — The Center of Leviticus ──")
    (println "  (Position 22,490 — middle of the center book)\n")

    (let [letters (sefaria/book-letters "Leviticus")
          lev-center (quot (count letters) 2)
          cum (vec (reductions + (map :letters lev-stats)))
          ;; Which chapter contains position lev-center?
          ch-idx (count (take-while #(<= % lev-center) cum))
          ch-start (if (pos? ch-idx) (nth cum (dec ch-idx)) 0)]
      (println (format "  Leviticus has %,d letters. Center: position %,d" (count letters) lev-center))
      (println (format "  Falls in chapter %d (starts at %,d)" (inc ch-idx) ch-start))
      (let [center-letter (nth (vec letters) lev-center)]
        (println (format "  Center letter: %c (value %d)" center-letter (g/letter-value center-letter))))

      ;; What's in the immediate neighborhood?
      (let [neighborhood (subvec (vec letters) (- lev-center 15) (+ lev-center 16))]
        (println (format "\n  31 letters at the center of Leviticus:"))
        (println (format "    %s" (apply str neighborhood)))
        (let [gem-sum (reduce + (map #(long (g/letter-value %)) neighborhood))]
          (println (format "    Gematria sum: %d" gem-sum))
          (println (format "    Sum mod 7: %d" (mod gem-sum 7)))
          (println (format "    Sum mod 11: %d" (mod gem-sum 11)))
          (println (format "    Sum mod 22: %d" (mod gem-sum 22))))))

    ;; ── 5. The hierarchy of centers ────────────────────────
    (println "\n── 5. The Hierarchy of Centers ──\n")
    (println "  Torah center → Leviticus 8 (consecration)")
    (println "  Leviticus center → ? (let's see)")

    ;; Where is the center of Leviticus in terms of verse content?
    (let [lev-letters (sefaria/book-letters "Leviticus")
          lev-center (quot (count lev-letters) 2)
          ;; Center verse of Leviticus
          lev-verses (mapcat (fn [ch]
                                (let [vs (sefaria/fetch-chapter "Leviticus" ch)]
                                  (map-indexed (fn [i v]
                                                 {:chapter ch :verse (inc i)
                                                  :letters (count (norm/letter-stream (norm/strip-html v)))})
                                               vs)))
                              (range 1 28))
          lev-verse-vec (vec lev-verses)
          n-lv (count lev-verse-vec)
          mid-verse (quot n-lv 2)]
      (println (format "  Leviticus verse midpoint: verse %d of %d" (inc mid-verse) n-lv))
      (let [mv (nth lev-verse-vec mid-verse)]
        (println (format "    → Leviticus %d:%d (%d letters)" (:chapter mv) (:verse mv) (:letters mv))))

      ;; Center word of Leviticus
      (let [all-lev-words (atom [])
            _ (doseq [ch (range 1 28)]
                (let [verses (sefaria/fetch-chapter "Leviticus" ch)]
                  (doseq [verse-text verses]
                    (let [stripped (norm/strip-html verse-text)
                          words (->> (clojure.string/split stripped #"[\s\u05BE]+")
                                     (map (fn [w] (apply str (filter norm/hebrew-letter? w))))
                                     (filter #(pos? (count %))))]
                      (doseq [w words]
                        (swap! all-lev-words conj {:word w :gematria (g/word-value w)}))))))
            lev-words (vec @all-lev-words)
            n-lw (count lev-words)
            mid-word-idx (quot n-lw 2)
            mid-word (nth lev-words mid-word-idx)]
        (println (format "\n  Leviticus word midpoint: word %d of %d" (inc mid-word-idx) n-lw))
        (println (format "    → \"%s\" (gematria %d)" (:word mid-word) (:gematria mid-word)))))

    ;; ── 6. Numbers: 333, 7, 11 at the center ──────────────
    (println "\n── 6. Numerology of the Center ──\n")

    (println "  Center word: וישחט (vayishchat, 'and he slaughtered')")
    (println "  Gematria: 333")
    (println (format "    = 3 × 111"))
    (println (format "    = 3 × 3 × 37"))
    (println (format "    = 9 × 37"))
    (println (format "    333 mod 7 = %d" (mod 333 7)))
    (println (format "    333 mod 11 = %d" (mod 333 11)))
    (println (format "    333 mod 22 = %d" (mod 333 22)))

    (println "\n  Center letter: ה")
    (println "  Value: 5")
    (println "  5 = number of books in the Torah")
    (println "  5 = 306,269 mod 7")
    (println "  5 = 306,269 mod 12")

    (println "\n  Total Torah gematria: 21,113,757")
    (println (format "    = 3 × 7,037,919"))
    (println (format "    = 3 × 3 × 2,346,083 = 9 × 2,346,083"))
    (println (format "    = 49 × %d" (quot 21113757 49)))
    (println (format "    21,113,757 mod 333 = %d" (mod 21113757 333)))
    (println (format "    21,113,757 / 333 = %.2f" (/ 21113757.0 333))))

  (println "\nDone."))
