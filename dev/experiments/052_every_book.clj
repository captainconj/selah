(ns experiments.052-every-book
  "Can we ask the palindrome question of any other book?

   We showed (048) that the palindrome is FREE for stable-frequency sequences.
   But we only tested shuffled Torah, Markov, and random controls.

   Now: test REAL books. Real literature. Real sacred texts.
   Do they all score 0.999+ on the 7-fold palindrome?
   What about the divisibility properties?
   What about the anti-palindrome (z-score vs shuffled)?

   Texts:
   1. Torah (Hebrew) — our baseline
   2. Prophets (Hebrew) — Nevi'im from Sefaria
   3. Moby Dick (English) — Melville
   4. War and Peace (English) — Tolstoy
   5. The Iliad (English) — Homer
   6. King James Bible (English) — translation of same content
   7. E. coli genome (DNA) — from experiment 043

   Run: clojure -M:dev -m experiments.052-every-book"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def hebrew-alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת"))
(def english-alphabet (vec "abcdefghijklmnopqrstuvwxyz"))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn char-profile [letters alphabet]
  (let [n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn palindrome-score [values k]
  (let [n (count values)
        seg (quot n k)]
    (when (>= seg 1)
      (let [sk (mapv (fn [i]
                       (let [start (* i seg)
                             end (if (= i (dec k)) n (* (inc i) seg))]
                         (double (reduce + (subvec values start end)))))
                     (range k))
            half (quot k 2)
            first-h (subvec sk 0 half)
            last-h (vec (reverse (subvec sk (inc half) k)))]
        (if (and (seq first-h) (seq last-h) (= (count first-h) (count last-h)))
          (cosine-sim first-h last-h)
          0.0)))))

(defn thread-palindrome [values k]
  (let [n (count values)
        tk (mapv (fn [off]
                   (double (reduce + (map #(long (nth values %)) (range off n k)))))
                 (range k))
        half (quot k 2)
        first-h (subvec tk 0 half)
        last-h (vec (reverse (subvec tk (inc half) k)))]
    (if (and (seq first-h) (seq last-h) (= (count first-h) (count last-h)))
      (cosine-sim first-h last-h)
      0.0)))

(defn holographic-score [letters alphabet n-samples]
  (let [n (count letters)
        window (quot n 100)
        whole-prof (char-profile letters alphabet)]
    (when (and whole-prof (> n window) (> window 10))
      (let [scores (mapv (fn [_]
                           (let [start (rand-int (- n window))
                                 slice (subvec letters start (+ start window))
                                 sp (char-profile slice alphabet)]
                             (if sp (cosine-sim sp whole-prof) 0.0)))
                         (range n-samples))]
        (/ (reduce + scores) (double n-samples))))))

;; ── Value functions ──────────────────────────────────────────────

(defn hebrew-values [letters]
  (mapv #(long (g/letter-value %)) letters))

(defn english-values
  "Map English letters to ordinal values: a=1, b=2, ..., z=26"
  [letters]
  (mapv #(inc (.indexOf english-alphabet %)) letters))

(defn dna-values [bases]
  (mapv {\A 1 \T 2 \G 3 \C 4} bases))

;; ── Text loading ─────────────────────────────────────────────────

(defn load-english-text
  "Load a text file, extract lowercase letters only."
  [path max-chars]
  (let [raw (slurp path)
        ;; Strip Gutenberg header/footer
        text (let [start-marker "*** START OF"
                   end-marker "*** END OF"
                   start-idx (or (str/index-of raw start-marker) 0)
                   ;; Find end of the start marker line
                   start-line-end (or (str/index-of raw "\n" start-idx) start-idx)
                   end-idx (or (str/index-of raw end-marker) (count raw))]
               (subs raw (min (inc start-line-end) (count raw)) end-idx))
        ;; Extract only lowercase letters
        letters (filterv #(Character/isLetter ^char %) (vec (str/lower-case text)))]
    (vec (take max-chars letters))))

(defn load-fasta [path max-bases]
  (let [raw (slurp path)
        lines (str/split-lines raw)
        seq-lines (filter #(not (str/starts-with? % ">")) lines)
        all-str (str/upper-case (str/join "" seq-lines))
        clean (filterv #(contains? #{\A \T \G \C} %) (vec all-str))]
    (vec (take max-bases clean))))

;; ── The battery ──────────────────────────────────────────────────

(defn run-battery [letters values-fn alphabet label]
  (let [n (count letters)
        vals (vec (values-fn letters))
        total (long (reduce + vals))
        p7 (palindrome-score vals 7)
        p49 (palindrome-score vals 49)
        t7 (thread-palindrome vals 7)
        holo (holographic-score letters alphabet 50)
        ;; Fractal
        sub7 (vec (map #(nth letters %) (range 0 n 7)))
        sub-vals (vec (values-fn sub7))
        frac (palindrome-score sub-vals 7)
        ;; Shuffled comparison (10 trials for z-score)
        n-shuf 20
        shuf-scores (mapv (fn [_]
                            (let [sh (vec (shuffle letters))
                                  sv (vec (values-fn sh))]
                              (palindrome-score sv 7)))
                          (range n-shuf))
        mean-shuf (/ (reduce + shuf-scores) (double n-shuf))
        std-shuf (Math/sqrt (/ (reduce + (map #(Math/pow (- % mean-shuf) 2) shuf-scores))
                               (double n-shuf)))
        z-score (if (pos? std-shuf) (/ (- p7 mean-shuf) std-shuf) 0.0)]
    {:label label :n n :total total
     :p7 p7 :p49 p49 :t7 t7
     :holo holo :fractal frac
     :mod7 (mod total 7)
     :mod37 (mod total 37)
     :mean-shuf mean-shuf :z-score z-score}))

(defn -main []
  (println "=== EVERY BOOK ===")
  (println "  The palindrome question, asked of every text we can find.\n")

  ;; ── Load all texts ─────────────────────────────────────────────
  (println "Loading texts...")

  ;; 1. Torah (Hebrew)
  (let [torah (vec (mapcat sefaria/book-letters
                           ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        _ (println (format "  Torah: %,d Hebrew letters" (count torah)))

        ;; 2. Individual Torah books for comparison
        genesis (vec (sefaria/book-letters "Genesis"))
        _ (println (format "  Genesis alone: %,d Hebrew letters" (count genesis)))

        ;; 3. Moby Dick
        moby (load-english-text "data/texts/moby_dick.txt" 500000)
        _ (println (format "  Moby Dick: %,d English letters" (count moby)))

        ;; 4. War and Peace
        war (load-english-text "data/texts/war_and_peace.txt" 500000)
        _ (println (format "  War and Peace: %,d English letters" (count war)))

        ;; 5. The Iliad
        iliad (load-english-text "data/texts/iliad.txt" 500000)
        _ (println (format "  Iliad: %,d English letters" (count iliad)))

        ;; 6. KJV Bible
        kjv (load-english-text "data/texts/kjv_bible.txt" 500000)
        _ (println (format "  KJV Bible: %,d English letters" (count kjv)))

        ;; 7. E. coli
        ecoli (load-fasta "data/genomes/ecoli_306k.fasta" 306269)
        _ (println (format "  E. coli: %,d bases" (count ecoli)))]

    ;; ── Run the battery ──────────────────────────────────────────
    (println "\n── Running Structural Battery ──")
    (println "  (Each text also compared to 20 shuffled versions for z-score)\n")

    (let [results [(do (print "  Torah...") (flush)
                       (let [r (run-battery torah hebrew-values hebrew-alphabet "Torah")]
                         (println " done") r))
                   (do (print "  Genesis...") (flush)
                       (let [r (run-battery genesis hebrew-values hebrew-alphabet "Genesis")]
                         (println " done") r))
                   (do (print "  Moby Dick...") (flush)
                       (let [r (run-battery moby english-values english-alphabet "Moby Dick")]
                         (println " done") r))
                   (do (print "  War & Peace...") (flush)
                       (let [r (run-battery war english-values english-alphabet "War&Peace")]
                         (println " done") r))
                   (do (print "  Iliad...") (flush)
                       (let [r (run-battery iliad english-values english-alphabet "Iliad")]
                         (println " done") r))
                   (do (print "  KJV Bible...") (flush)
                       (let [r (run-battery kjv english-values english-alphabet "KJV Bible")]
                         (println " done") r))
                   (do (print "  E. coli...") (flush)
                       (let [r (run-battery ecoli dna-values [\A \T \G \C] "E. coli")]
                         (println " done") r))]]

      ;; ── Print comparison ────────────────────────────────────────
      (println "\n── Results ──\n")

      (let [header (format "  %-22s" "Test")
            labels (map :label results)]
        (println (apply str header (map #(format " %-11s" %) labels)))
        (println (apply str (repeat (+ 22 (* 12 (count results))) "─")))

        (doseq [[test-name getter fmt]
                [["Letters/bases"     :n        "%,d"]
                 ["7-fold palindrome" :p7       "%.6f"]
                 ["49-fold palindrome" :p49     "%.6f"]
                 ["7-thread"          :t7       "%.6f"]
                 ["Holographic (1%)"  :holo     "%.6f"]
                 ["Fractal (7th)"     :fractal  "%.6f"]
                 ["Shuffled mean"     :mean-shuf "%.6f"]
                 ["z-score"           :z-score  "%.2f"]
                 ["Σ mod 7"           :mod7     "%d"]
                 ["Σ mod 37"          :mod37    "%d"]]]
          (let [row (format "  %-22s" test-name)
                vals (map #(let [v (getter %)]
                             (if v (format (str " %-11s") (format fmt v))
                                   " -          ")) results)]
            (println (apply str row vals)))))

      ;; ── Analysis ────────────────────────────────────────────────
      (println "\n── Analysis ──\n")

      ;; Do all texts show palindrome?
      (let [all-p7 (map #(vector (:label %) (:p7 %)) results)
            all-z (map #(vector (:label %) (:z-score %)) results)]

        (println "  Palindrome scores (7-fold):")
        (doseq [[label score] (sort-by second all-p7)]
          (println (format "    %-15s %.6f" label score)))

        (println "\n  z-scores (vs shuffled self):")
        (doseq [[label z] (sort-by second all-z)]
          (println (format "    %-15s %+.2f" label z)))

        (println)
        (let [high-p7 (filter #(> (second %) 0.999) all-p7)
              low-p7 (filter #(<= (second %) 0.999) all-p7)]
          (println (format "  Texts with palindrome > 0.999: %d of %d"
                           (count high-p7) (count all-p7)))
          (when (seq low-p7)
            (println (format "  Texts below 0.999: %s"
                             (str/join ", " (map #(format "%s(%.4f)" (first %) (second %)) low-p7)))))))

      ;; ── What varies ─────────────────────────────────────────────
      (println "\n── What Varies Between Texts ──\n")

      (println "  If ALL texts score ~0.999+, then the palindrome is truly free.")
      (println "  If some score lower, frequency stability matters.")
      (println)

      ;; Compare frequency stability
      (println "  Frequency stability (first half vs second half cosine):")
      (let [stability (fn [letters alphabet]
                        (let [half (quot (count letters) 2)
                              p1 (char-profile (subvec letters 0 half) alphabet)
                              p2 (char-profile (subvec letters half) alphabet)]
                          (if (and p1 p2) (cosine-sim p1 p2) 0.0)))]
        (doseq [[label letters alpha]
                [["Torah" torah hebrew-alphabet]
                 ["Genesis" genesis hebrew-alphabet]
                 ["Moby Dick" moby english-alphabet]
                 ["War&Peace" war english-alphabet]
                 ["Iliad" iliad english-alphabet]
                 ["KJV Bible" kjv english-alphabet]
                 ["E. coli" ecoli [\A \T \G \C]]]]
          (println (format "    %-15s %.6f" label (stability letters alpha)))))

      ;; ── The verdict ─────────────────────────────────────────────
      (println "\n── The Verdict ──\n")

      (println "  The question was: is the palindrome special to the Torah?")
      (println)
      (println "  Answer: look at the table above.")
      (println "  If Moby Dick, War and Peace, and the Iliad all score 0.999+,")
      (println "  then the palindrome is a property of LANGUAGE, not of the Torah.")
      (println "  If DNA scores 0.999+, it's a property of STABLE SEQUENCES.")
      (println)
      (println "  What MIGHT be special:")
      (println "    - The z-score (how the Torah compares to its own shuffled version)")
      (println "    - The divisibility (mod 7, mod 37)")
      (println "    - Whether Hebrew has unique frequency properties")))

  (println "\nDone. Every book answers."))
