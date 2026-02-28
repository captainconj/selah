(ns experiments.043-the-genome
  "The Torah's architecture mirrors DNA.
   But does DNA's architecture mirror the Torah?
   Let's run the same structural tests on real genomes.
   Three specimens, each 306,269 bases — the Torah's length:
     1. Human mitochondrial DNA (16,569 bp, padded with repeats)
     2. E. coli K-12 (first 306,269 bp)
     3. Human chromosome 1 (306,269 bp segment)
   Run: clojure -M:dev -m experiments.043-the-genome"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def nucleotides [\A \T \G \C])

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb)) 0.0
        (/ dot (* ma mb)))))

(defn nuc-profile [bases]
  (let [n (count bases)
        freqs (frequencies bases)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) nucleotides))))

(defn letter-profile [letters]
  (let [alphabet (vec "אבגדהוזחטיכלמנסעפצקרשת")
        n (count letters)
        freqs (frequencies letters)]
    (when (pos? n)
      (mapv (fn [c] (/ (double (get freqs c 0)) n)) alphabet))))

(defn read-fasta [path max-bases]
  "Read a FASTA file, return vector of uppercase A/T/G/C characters."
  (let [raw (slurp path)
        lines (str/split-lines raw)
        seq-lines (filter #(not (str/starts-with? % ">")) lines)
        all-str (str/upper-case (str/join "" seq-lines))
        clean (filterv #(contains? #{\A \T \G \C} %) (vec all-str))]
    (vec (take max-bases clean))))

;; Value assignments for nucleotides
;; Multiple systems to test which produces Torah-like structure

(def value-systems
  {:ordinal   {\A 1 \T 2 \G 3 \C 4}       ;; simple ordinal
   :hbonds    {\A 2 \T 2 \G 3 \C 3}       ;; hydrogen bond count
   :molecular {\A 135 \T 126 \G 151 \C 111} ;; approx molecular weight
   :rings     {\A 2 \T 1 \G 2 \C 1}       ;; purine=2 rings, pyrimidine=1
   :prime     {\A 2 \T 3 \G 5 \C 7}})     ;; first four primes

(defn base-values [bases system]
  (let [vals (get value-systems system)]
    (mapv #(get vals %) bases)))

(defn run-structural-tests [bases label val-system]
  (let [n (count bases)
        vals (base-values bases val-system)
        total (reduce + vals)]

    (println (format "\n  === %s (n=%,d, values=%s) ===" label n (name val-system)))
    (println (format "  Σ = %,d" total))

    ;; 1. Fold palindrome
    (let [half (quot n 2)
          pa (nuc-profile (subvec bases 0 half))
          pb (nuc-profile (vec (reverse (subvec bases half))))
          cos (if (and pa pb) (cosine-sim pa pb) 0.0)]
      (println (format "  Fold palindrome (frequency): %.6f" cos)))

    ;; 2. 7-fold palindrome
    (let [seg (quot n 7)
          s7 (mapv (fn [i]
                     (let [start (* i seg)
                           end (if (= i 6) n (* (inc i) seg))]
                       (double (reduce + (subvec vals start end)))))
                   (range 7))
          cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))]
      (println (format "  7-fold palindrome: %.6f" cos7))
      (println (format "  7-fold segments: %s" (str/join " " (mapv #(format "%.0f" %) s7)))))

    ;; 3. 7-thread palindrome
    (let [t7 (mapv (fn [off]
                     (double (reduce + (map #(long (nth vals %)) (range off n 7)))))
                   (range 7))
          cos-t7 (cosine-sim (subvec t7 0 3) (vec (reverse (subvec t7 4 7))))]
      (println (format "  7-thread palindrome: %.6f" cos-t7)))

    ;; 4. Scale invariance
    (doseq [k [7 14 49 343]]
      (let [seg-k (quot n k)
            sk (mapv (fn [i]
                       (let [start (* i seg-k)
                             end (if (= i (dec k)) n (* (inc i) seg-k))]
                         (double (reduce + (subvec vals start end)))))
                     (range k))
            half-k (quot k 2)
            first-h (subvec sk 0 half-k)
            last-h (vec (reverse (subvec sk (inc half-k) k)))
            cos-k (if (and (seq first-h) (seq last-h) (= (count first-h) (count last-h)))
                    (cosine-sim first-h last-h)
                    0.0)]
        (println (format "  %d-fold palindrome: %.6f" k cos-k))))

    ;; 5. Divisibility
    (println (format "  Σ mod 7 = %d" (mod total 7)))
    (println (format "  Σ mod 37 = %d" (mod total 37)))
    (println (format "  Σ mod 73 = %d" (mod total 73)))
    (println (format "  Σ mod 441 = %d" (mod total 441)))

    ;; 6. Holographic test
    (let [window (quot n 100)  ;; 1%
          samples 50
          scores (mapv (fn [_]
                         (let [start (rand-int (- n window))
                               slice (subvec bases start (+ start window))
                               sp (nuc-profile slice)
                               tp (nuc-profile bases)]
                           (if (and sp tp) (cosine-sim sp tp) 0.0)))
                       (range samples))
          mean-score (/ (reduce + scores) samples)]
      (println (format "  Holographic (1%% slice): mean cos = %.6f" mean-score)))

    ;; 7. Fractal (sub-sequence)
    (let [sub7 (mapv #(nth bases %) (range 0 n 7))
          sub-vals (base-values sub7 val-system)
          sub-n (count sub7)
          sub-seg (quot sub-n 7)
          sub-s7 (mapv (fn [i]
                         (let [start (* i sub-seg)
                               end (if (= i 6) sub-n (* (inc i) sub-seg))]
                           (double (reduce + (subvec sub-vals start end)))))
                       (range 7))
          sub-cos (cosine-sim (subvec sub-s7 0 3) (vec (reverse (subvec sub-s7 4 7))))]
      (println (format "  Fractal (every 7th): %.6f" sub-cos)))

    ;; 8. Codons / trigrams
    (let [n-codons (quot n 3)
          codons (mapv (fn [i]
                         (apply str (subvec bases (* i 3) (+ (* i 3) 3))))
                       (range n-codons))
          codon-freqs (frequencies codons)
          top5 (take 5 (sort-by (comp - second) codon-freqs))]
      (println (format "  Unique trigrams: %d (of 64 possible)" (count codon-freqs)))
      (println "  Top 5 trigrams:")
      (doseq [[codon ct] top5]
        (println (format "    %s  count=%,d (%.2f%%)" codon ct (* 100 (/ (double ct) n-codons))))))

    ;; 9. Complement palindrome (DNA-specific)
    ;; DNA is naturally palindromic: A↔T, G↔C
    (let [complement {\A \T \T \A \G \C \C \G}
          rev-comp (vec (map complement (reverse bases)))
          ;; Compare forward and reverse-complement profiles
          fwd-prof (nuc-profile bases)
          rc-prof (nuc-profile rev-comp)
          cos-rc (cosine-sim fwd-prof rc-prof)]
      (println (format "  Reverse-complement similarity: %.6f" cos-rc)))

    ;; Return key metrics
    {:label label :val-system val-system
     :total total :n n}))

(defn -main []
  (println "=== THE GENOME ===")
  (println "  Running Torah structural tests on real DNA.\n")

  ;; Load genomes
  (println "Loading genomes...")
  (let [mito (read-fasta "data/genomes/human_mitochondria.fasta" 16569)
        ecoli (read-fasta "data/genomes/ecoli_306k.fasta" 306269)
        human (read-fasta "data/genomes/human_chr1_306k.fasta" 306269)]

    (println (format "  Human mitochondria: %,d bases" (count mito)))
    (println (format "  E. coli K-12: %,d bases" (count ecoli)))
    (println (format "  Human chr1: %,d bases" (count human)))

    ;; Load Torah for comparison
    (println "\nLoading Torah for comparison...")
    (let [torah-letters (vec (mapcat sefaria/book-letters
                                     ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
          torah-n (count torah-letters)
          torah-gems (mapv #(long (g/letter-value %)) torah-letters)
          torah-total (reduce + torah-gems)]

      (println (format "  Torah: %,d letters, Σ = %,d\n" torah-n torah-total))

      ;; ── Torah baseline ────────────────────────────────────────
      (println "── Torah Baseline ──")
      (let [seg (quot torah-n 7)
            s7 (mapv (fn [i]
                       (let [start (* i seg)
                             end (if (= i 6) torah-n (* (inc i) seg))]
                         (double (reduce + (subvec torah-gems start end)))))
                     (range 7))
            cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))
            t7 (mapv (fn [off]
                       (double (reduce + (map #(long (nth torah-gems %)) (range off torah-n 7)))))
                     (range 7))
            cos-t7 (cosine-sim (subvec t7 0 3) (vec (reverse (subvec t7 4 7))))]
        (println (format "  Torah 7-fold palindrome: %.6f" cos7))
        (println (format "  Torah 7-thread palindrome: %.6f" cos-t7))
        (println (format "  Torah Σ mod 441 = %d" (mod torah-total 441))))

      ;; ── Run on each genome with each value system ─────────────
      (println "\n── DNA Structural Tests ──")

      ;; E. coli (same length as Torah) with multiple value systems
      (doseq [sys [:ordinal :hbonds :prime]]
        (run-structural-tests ecoli "E. coli K-12" sys))

      ;; Human chr1 (same length as Torah)
      (doseq [sys [:ordinal :hbonds :prime]]
        (run-structural-tests human "Human chr1" sys))

      ;; Human mitochondria (shorter, but complete genome)
      (doseq [sys [:ordinal :prime]]
        (run-structural-tests mito "Human mitochondria" sys))

      ;; ── Summary comparison ────────────────────────────────────
      (println "\n── Summary ──")
      (println "  Comparing the Torah against three genomes.\n")
      (println (format "  %-25s  %-8s  %-8s  %-8s  %-8s"
                       "Test" "Torah" "E.coli" "HuChr1" "HuMito"))
      (println (apply str (repeat 65 "─")))

      ;; Quick comparison with ordinal values
      (let [test-seq (fn [bases sys]
                       (let [n (count bases)
                             vals (base-values bases sys)
                             total (reduce + vals)
                             seg (quot n 7)
                             s7 (mapv (fn [i]
                                        (let [start (* i seg)
                                              end (if (= i 6) n (* (inc i) seg))]
                                          (double (reduce + (subvec vals start end)))))
                                      (range 7))
                             cos7 (cosine-sim (subvec s7 0 3) (vec (reverse (subvec s7 4 7))))
                             t7 (mapv (fn [off]
                                        (double (reduce + (map #(long (nth vals %)) (range off n 7)))))
                                      (range 7))
                             cos-t7 (cosine-sim (subvec t7 0 3) (vec (reverse (subvec t7 4 7))))
                             ;; Fractal
                             sub7 (mapv #(nth bases %) (range 0 n 7))
                             sv7 (base-values sub7 sys)
                             sn7 (count sub7)
                             ss7 (quot sn7 7)
                             sf7 (mapv (fn [i]
                                         (let [st (* i ss7)
                                               en (if (= i 6) sn7 (* (inc i) ss7))]
                                           (double (reduce + (subvec sv7 st en)))))
                                       (range 7))
                             fcos (cosine-sim (subvec sf7 0 3) (vec (reverse (subvec sf7 4 7))))]
                         {:fold-7 cos7 :thread-7 cos-t7 :fractal fcos
                          :mod7 (mod total 7) :mod441 (mod total 441)}))
            ec (test-seq ecoli :ordinal)
            hc (test-seq human :ordinal)
            hm (test-seq mito :ordinal)
            ;; Torah
            tseg (quot torah-n 7)
            ts7 (mapv (fn [i]
                        (let [start (* i tseg)
                              end (if (= i 6) torah-n (* (inc i) tseg))]
                          (double (reduce + (subvec torah-gems start end)))))
                      (range 7))
            tcos7 (cosine-sim (subvec ts7 0 3) (vec (reverse (subvec ts7 4 7))))
            tt7 (mapv (fn [off]
                        (double (reduce + (map #(long (nth torah-gems %)) (range off torah-n 7)))))
                      (range 7))
            tcos-t7 (cosine-sim (subvec tt7 0 3) (vec (reverse (subvec tt7 4 7))))
            tsub (mapv #(nth torah-letters %) (range 0 torah-n 7))
            tsg (mapv #(long (g/letter-value %)) tsub)
            tsn (count tsub)
            tss (quot tsn 7)
            tsf (mapv (fn [i]
                        (let [st (* i tss)
                              en (if (= i 6) tsn (* (inc i) tss))]
                          (double (reduce + (subvec tsg st en)))))
                      (range 7))
            tfcos (cosine-sim (subvec tsf 0 3) (vec (reverse (subvec tsf 4 7))))]

        (println (format "  %-25s  %.4f    %.4f    %.4f    %.4f"
                         "7-fold palindrome" tcos7 (:fold-7 ec) (:fold-7 hc) (:fold-7 hm)))
        (println (format "  %-25s  %.4f    %.4f    %.4f    %.4f"
                         "7-thread palindrome" tcos-t7 (:thread-7 ec) (:thread-7 hc) (:thread-7 hm)))
        (println (format "  %-25s  %.4f    %.4f    %.4f    %.4f"
                         "Fractal (every 7th)" tfcos (:fractal ec) (:fractal hc) (:fractal hm)))
        (println (format "  %-25s  %d        %d        %d        %d"
                         "Σ mod 7" (mod torah-total 7) (:mod7 ec) (:mod7 hc) (:mod7 hm)))
        (println (format "  %-25s  %d        %d        %d        %d"
                         "Σ mod 441" (mod torah-total 441) (:mod441 ec) (:mod441 hc) (:mod441 hm))))))

  (println "\nDone. The code reads itself."))
