(ns selah.gematria
  "Hebrew letter values and numerical analysis of the letter stream.

   Standard gematria: א=1 through ת=400.
   Final forms carry the same value as their non-final counterparts."
  (:require [selah.text.normalize :as norm]))

;; ── Letter values ────────────────────────────────────────────

(def ^:private values
  {\א 1   \ב 2   \ג 3   \ד 4   \ה 5   \ו 6   \ז 7   \ח 8   \ט 9
   \י 10  \כ 20  \ל 30  \מ 40  \נ 50  \ס 60  \ע 70  \פ 80  \צ 90
   \ק 100 \ר 200 \ש 300 \ת 400
   ;; Final forms — same values as non-final
   \ך 20  \ם 40  \ן 50  \ף 80  \ץ 90})

(defn letter-value
  "Gematria value of a single Hebrew letter. Returns 0 for non-Hebrew."
  [ch]
  (get values ch 0))

(defn word-value
  "Sum of gematria values for a string (letters only)."
  [^String s]
  (transduce (map letter-value) + (norm/letter-stream s)))

(defn stream-values
  "Convert a letter stream (vec of chars) to a vec of integer values."
  [letters]
  (mapv letter-value letters))

;; ── Aggregates ───────────────────────────────────────────────

(defn total
  "Total gematria sum of a letter stream."
  [letters]
  (transduce (map letter-value) + letters))

(defn running-sum
  "Cumulative gematria sum. Returns a vec the same length as input."
  [letters]
  (reductions + (map letter-value letters)))

(defn running-sum-mod
  "Cumulative gematria sum mod n. Returns a lazy seq."
  [letters n]
  (map #(mod % n) (running-sum letters)))

;; ── Statistics ───────────────────────────────────────────────

(defn stats
  "Basic statistics of a letter stream's gematria values.
   Returns {:total :count :mean :median :harmonic-mean :variance}."
  [letters]
  (let [vals   (stream-values letters)
        n      (count vals)
        sum    (reduce + vals)
        mean   (/ (double sum) n)
        sorted (sort vals)
        median (if (odd? n)
                 (nth sorted (quot n 2))
                 (/ (+ (nth sorted (dec (quot n 2)))
                       (nth sorted (quot n 2)))
                    2.0))
        hm     (/ (double n)
                  (reduce + (map #(/ 1.0 %) (remove zero? vals))))
        var    (/ (reduce + (map #(Math/pow (- % mean) 2) vals))
                  (double n))]
    {:total  sum
     :count  n
     :mean   mean
     :median median
     :harmonic-mean hm
     :variance var
     :std-dev (Math/sqrt var)}))

;; ── Distribution ─────────────────────────────────────────────

(defn letter-spectrum
  "Frequency × value for each Hebrew letter. Returns sorted vec of
   {:letter :value :freq :count :energy} maps."
  [letters]
  (let [freqs (frequencies letters)
        n     (count letters)]
    (->> values
         (map (fn [[ch v]]
                (let [cnt (get freqs ch 0)]
                  {:letter ch
                   :value  v
                   :count  cnt
                   :freq   (/ (double cnt) n)
                   :energy (* v cnt)})))
         (filter #(pos? (:count %)))
         (sort-by :value)
         vec)))

;; ── Zero crossings ──────────────────────────────────────────

(defn zero-crossings-mod
  "Positions where running sum mod n equals zero.
   Returns a lazy seq of 0-based indices."
  [letters n]
  (keep-indexed
   (fn [i v] (when (zero? v) i))
   (running-sum-mod letters n)))

;; ── Printing ─────────────────────────────────────────────────

(defn print-stats
  "Print formatted statistics for a letter stream."
  [label letters]
  (let [s (stats letters)]
    (println)
    (println (format "=== %s ===" label))
    (println (format "  Letters:       %,d" (:count s)))
    (println (format "  Total value:   %,d" (:total s)))
    (println (format "  Mean:          %.2f" (:mean s)))
    (println (format "  Median:        %.1f" (double (:median s))))
    (println (format "  Harmonic mean: %.2f" (:harmonic-mean s)))
    (println (format "  Std dev:       %.2f" (:std-dev s)))
    s))

(defn print-spectrum
  "Print the letter spectrum (eigenspectrum of the basis)."
  [letters]
  (let [spec (letter-spectrum letters)]
    (println)
    (println (format "%-6s %5s %7s %8s %10s" "Letter" "Value" "Count" "Freq" "Energy"))
    (println (apply str (repeat 42 "-")))
    (doseq [{:keys [letter value count freq energy]} spec]
      (println (format "  %s    %4d  %6d   %5.3f  %,9d" letter value count freq energy)))
    (println (apply str (repeat 42 "-")))
    (println (format "  Total energy: %,d" (reduce + (map :energy spec))))))

(comment
  (require '[selah.text.sefaria :as sefaria])

  ;; Genesis 1:1 — the famous first verse
  (word-value "בראשית")   ;=> 913
  (word-value "ברא")      ;=> 203
  (word-value "אלהים")    ;=> 86
  (word-value "את")       ;=> 401
  (word-value "השמים")    ;=> 395
  (word-value "ואת")      ;=> 407
  (word-value "הארץ")     ;=> 296
  ;; Total: 2701 = 37 × 73

  ;; Per-book statistics
  (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
    (print-stats book (sefaria/book-letters book)))

  ;; Eigenspectrum
  (print-spectrum (sefaria/book-letters "Genesis"))

  ;; Golden section of the Torah
  (def torah (vec (mapcat sefaria/book-letters
                    ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])))
  (def phi-pos (long (/ (count torah) 1.6180339887)))
  (nth torah phi-pos)  ;; what letter?

  ;; Running sum mod 7 — zero crossings
  (take 20 (zero-crossings-mod (sefaria/book-letters "Genesis") 7))
  )
