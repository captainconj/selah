(ns experiments.014-verse-fractal
  "Push the fractal palindrome down one level: verse granularity.
   Does the chiastic symmetry hold at the verse level within books
   and between paired books?
   Run: clojure -M:dev -m experiments.014-verse-fractal"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(defn verse-stats
  "Get stats for every verse in a book. Returns vec of maps."
  [book-name]
  (let [chapters (get sefaria/book-chapters book-name)
        results  (atom [])]
    (doseq [ch (range 1 (inc chapters))]
      (let [verses (sefaria/fetch-chapter book-name ch)]
        (doseq [[v-idx verse-text] (map-indexed vector verses)]
          (let [stripped (norm/strip-html verse-text)
                letters  (norm/letter-stream stripped)
                n        (count letters)]
            (when (pos? n)
              (swap! results conj
                     {:book    book-name
                      :chapter ch
                      :verse   (inc v-idx)
                      :letters n
                      :gematria (g/total letters)
                      :mean     (/ (double (g/total letters)) n)}))))))
    @results))

(defn cosine-sim [a b]
  (let [dot (reduce + (map * a b))
        ma  (Math/sqrt (reduce + (map #(* % %) a)))
        mb  (Math/sqrt (reduce + (map #(* % %) b)))]
    (if (or (zero? ma) (zero? mb))
      0.0
      (/ dot (* ma mb)))))

(defn resample
  "Resample a vector to n evenly-spaced bins by averaging."
  [v target-n]
  (let [src-n  (count v)
        bin-sz (/ (double src-n) target-n)]
    (mapv (fn [i]
            (let [start (int (* i bin-sz))
                  end   (min src-n (int (* (inc i) bin-sz)))
                  chunk (subvec v start end)]
              (if (empty? chunk) 0.0
                  (/ (reduce + (map double chunk)) (count chunk)))))
          (range target-n))))

(defn palindrome-score
  "Split a vector in half, reverse the second half, compute cosine similarity."
  [v]
  (let [n    (count v)
        half (quot n 2)
        a    (subvec v 0 half)
        b    (vec (reverse (subvec v half (+ half half))))]
    (cosine-sim (mapv double a) (mapv double b))))

(defn -main []
  (println "=== Verse-Level Fractal Palindrome ===")
  (println "  Pushing the fractal down one more level.\n")

  ;; Load all verse data
  (println "Loading verse data for all 5 books...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        all-verses (into {} (map (fn [b]
                                   (print (format "  %s..." b)) (flush)
                                   (let [vs (verse-stats b)]
                                     (println (format " %d verses" (count vs)))
                                     [b vs]))
                                 books))]

    ;; ── 1. Internal palindrome at verse level ──────────────
    (println "\n── 1. Internal Palindrome (Verse Level) ──")
    (println "  First half vs reversed second half, verse by verse.\n")
    (println (format "  %-14s  %5s  %10s  %10s  %10s" "Book" "Verses"
                     "Length pal" "Gematria pal" "Mean pal"))
    (println (apply str (repeat 60 "─")))

    (doseq [book books]
      (let [vs     (get all-verses book)
            n      (count vs)
            lens   (mapv :letters vs)
            gems   (mapv :gematria vs)
            means  (mapv :mean vs)
            len-pal  (palindrome-score lens)
            gem-pal  (palindrome-score gems)
            mean-pal (palindrome-score means)]
        (println (format "  %-14s  %5d  %10.4f  %10.4f  %10.4f"
                         book n len-pal gem-pal mean-pal))))

    ;; ── 2. Compare with chapter level ──────────────────────
    (println "\n  (For reference, chapter-level palindrome scores from experiment 012:")
    (println "   Genesis: len=0.918, mean=0.996")
    (println "   Exodus:  len=0.939, mean=0.990")
    (println "   Numbers: len=0.860, mean=0.989")
    (println "   Deut:    len=0.875, mean=0.998)")

    ;; ── 3. Chiastic pairing at verse level ─────────────────
    (println "\n── 2. Chiastic Pairing at Verse Level ──")
    (println "  Forward vs reversed verse profiles between paired books.")
    (println "  Resampled to 100 bins for equal comparison.\n")

    (let [n-bins 100]
      (doseq [[book-a book-b label] [["Genesis" "Deuteronomy" "A / A'"]
                                       ["Exodus" "Numbers" "B / B'"]]]
        (let [vs-a (get all-verses book-a)
              vs-b (get all-verses book-b)
              ;; Verse length profiles
              lens-a (mapv :letters vs-a)
              lens-b (mapv :letters vs-b)
              ;; Gematria mean profiles
              means-a (mapv :mean vs-a)
              means-b (mapv :mean vs-b)
              ;; Resample to same number of bins
              ra-lens (resample lens-a n-bins)
              rb-lens (resample lens-b n-bins)
              ra-means (resample means-a n-bins)
              rb-means (resample means-b n-bins)
              ;; Forward and reversed cosine similarity
              fwd-lens  (cosine-sim ra-lens rb-lens)
              rev-lens  (cosine-sim ra-lens (vec (reverse rb-lens)))
              fwd-means (cosine-sim ra-means rb-means)
              rev-means (cosine-sim ra-means (vec (reverse rb-means)))]

          (println (format "  %s (%s ↔ %s):" label book-a book-b))
          (println (format "    Verse lengths:   forward=%.4f  chiastic=%.4f  %s"
                           fwd-lens rev-lens
                           (if (> rev-lens fwd-lens) "← CHIASTIC WINS" "  forward wins")))
          (println (format "    Gematria means:  forward=%.4f  chiastic=%.4f  %s"
                           fwd-means rev-means
                           (if (> rev-means fwd-means) "← CHIASTIC WINS" "  forward wins")))
          (println))))

    ;; ── 4. Finer binning sweep ─────────────────────────────
    (println "── 3. Binning Sweep ──")
    (println "  Testing chiastic advantage at different resolutions.\n")

    (doseq [[book-a book-b label] [["Genesis" "Deuteronomy" "Gen/Deut"]
                                     ["Exodus" "Numbers" "Exod/Num"]]]
      (let [vs-a (get all-verses book-a)
            vs-b (get all-verses book-b)
            lens-a (mapv :letters vs-a)
            lens-b (mapv :letters vs-b)]
        (println (format "  %s verse-length chiastic advantage by bin count:" label))
        (println (format "  %6s  %8s  %8s  %8s" "Bins" "Forward" "Chiastic" "Δ"))
        (println (apply str (repeat 40 "─")))
        (doseq [nb [10 20 50 100 200 500]]
          (let [ra (resample lens-a nb)
                rb (resample lens-b nb)
                fwd (cosine-sim ra rb)
                rev (cosine-sim ra (vec (reverse rb)))]
            (println (format "  %6d  %8.4f  %8.4f  %+8.4f" nb fwd rev (- rev fwd)))))
        (println)))

    ;; ── 5. Verse-level cumulative shape ────────────────────
    (println "── 4. Cumulative Shape ──")
    (println "  Normalized cumulative letter count — does one book's start")
    (println "  match the other's end?\n")

    (doseq [[book-a book-b label] [["Genesis" "Deuteronomy" "Gen/Deut"]
                                     ["Exodus" "Numbers" "Exod/Num"]]]
      (let [vs-a  (get all-verses book-a)
            vs-b  (get all-verses book-b)
            lens-a (mapv :letters vs-a)
            lens-b (mapv :letters vs-b)
            cum-a  (vec (reductions + lens-a))
            cum-b  (vec (reductions + lens-b))
            tot-a  (double (last cum-a))
            tot-b  (double (last cum-b))
            ;; Sample at 10% intervals
            pcts   [0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9]]
        (println (format "  %s:" label))
        (println (format "  %6s  %10s  %10s  %10s" "Frac" (str book-a " %") (str book-b " fwd%") (str book-b " rev%")))
        (println (apply str (repeat 46 "─")))
        (doseq [p pcts]
          (let [idx-a (min (dec (count cum-a)) (int (* p (count cum-a))))
                idx-b (min (dec (count cum-b)) (int (* p (count cum-b))))
                idx-br (min (dec (count cum-b)) (int (* (- 1.0 p) (count cum-b))))
                a-pct (/ (double (nth cum-a idx-a)) tot-a)
                b-pct (/ (double (nth cum-b idx-b)) tot-b)
                br-pct (- 1.0 (/ (double (nth cum-b idx-br)) tot-b))]
            (println (format "  %6.1f  %10.4f  %10.4f  %10.4f" (* 100 p) a-pct b-pct br-pct))))
        (println)))

    ;; ── 6. Sliding window palindrome ───────────────────────
    (println "── 5. Sliding Window Palindrome ──")
    (println "  Internal palindrome score at different window sizes.\n")

    (doseq [book books]
      (let [vs    (get all-verses book)
            lens  (mapv :letters vs)
            n     (count vs)
            ;; Test palindrome at different fractions of the book
            scores (for [frac [0.25 0.5 0.75 1.0]]
                     (let [window (int (* frac n))
                           window (if (odd? window) (dec window) window)
                           start  (int (/ (- n window) 2))
                           chunk  (subvec lens start (+ start window))]
                       {:frac frac :window window
                        :score (palindrome-score chunk)}))]
        (println (format "  %s (%d verses):" book n))
        (doseq [{:keys [frac window score]} scores]
          (println (format "    %3.0f%% center (%d verses): palindrome = %.4f"
                           (* 100 frac) window score)))))

    (println "\n── Summary ──")
    (println "  If the fractal palindrome holds at verse level:")
    (println "  • Internal palindrome scores should be positive (> random ~0.5)")
    (println "  • Chiastic pairing should beat forward pairing")
    (println "  • The signal may be weaker than chapter level (more noise)")
    (println "  • But if present at ALL, it extends the fractal to another scale."))

  (println "\nDone."))
