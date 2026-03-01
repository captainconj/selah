(ns selah.space.coords
  "4D coordinate space over the Torah letter stream.

   304,850 = 7 × 50 × 13 × 67

   Every letter has a unique address (a, b, c, d):
     a ∈ {0..6}   — the seven days / completeness
     b ∈ {0..49}  — jubilee / ELS skip for תורה
     c ∈ {0..12}  — אחד (one) / אהבה (love)
     d ∈ {0..66}  — בינה (understanding)

   position = a×43,550 + b×871 + c×67 + d"
  (:require [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Constants ──────────────────────────────────────────────

(def ^:const total-letters 304850)
(def ^:const dim-a 7)
(def ^:const dim-b 50)
(def ^:const dim-c 13)
(def ^:const dim-d 67)

(def ^:const stride-a 43550)   ;; 50 × 13 × 67
(def ^:const stride-b 871)     ;; 13 × 67
(def ^:const stride-c 67)      ;; 67
(def ^:const stride-d 1)

;; ── Lookup tables ──────────────────────────────────────────
;;
;; 27 symbols: 22 letters + 5 final forms.
;; Index order: standard alphabetical, then finals.

(def letter-chars
  "byte index → Hebrew char (27 entries)"
  [\א \ב \ג \ד \ה \ו \ז \ח \ט \י
   \כ \ל \מ \נ \ס \ע \פ \צ \ק \ר
   \ש \ת \ך \ם \ן \ף \ץ])

(def char->idx
  "Hebrew char → byte index"
  (into {} (map-indexed (fn [i c] [c (byte i)]) letter-chars)))

(def letter-gv
  "byte index → gematria value (27 entries)"
  (let [gv {\א 1   \ב 2   \ג 3   \ד 4   \ה 5   \ו 6   \ז 7   \ח 8   \ט 9
             \י 10  \כ 20  \ל 30  \מ 40  \נ 50  \ס 60  \ע 70  \פ 80  \צ 90
             \ק 100 \ר 200 \ש 300 \ת 400
             \ך 20  \ם 40  \ן 50  \ף 80  \ץ 90}]
    (mapv #(get gv % 0) letter-chars)))

(def ^:private final->base
  "byte index of final form → byte index of base letter"
  {22 10   ;; ך → כ
   23 12   ;; ם → מ
   24 13   ;; ן → נ
   25 16   ;; ף → פ
   26 17}) ;; ץ → צ

(def idx-22
  "27-symbol index → 22-letter index (collapses finals)"
  (mapv (fn [i] (get final->base i i)) (range 27)))

(def book-names ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])

;; ── Address arithmetic ─────────────────────────────────────

(defn idx->coord
  "Position → [a b c d] via mixed-radix decomposition."
  ^longs [^long i]
  (let [a (quot i stride-a)
        r (rem  i stride-a)
        b (quot r stride-b)
        r (rem  r stride-b)
        c (quot r stride-c)
        d (rem  r stride-c)]
    (long-array [a b c d])))

(defn coord->idx
  "Coordinate [a b c d] → position."
  ^long [^long a ^long b ^long c ^long d]
  (+ (* a stride-a) (* b stride-b) (* c stride-c) d))

;; ── State ──────────────────────────────────────────────────

(defonce ^:dynamic *state* (atom nil))

(defn- build-verse-index
  "Build parallel arrays: verses (int[], position → verse-id)
   and verse-ref (vec of {:book :ch :vs :start :end})."
  []
  (let [verses-arr (int-array total-letters)
        refs (atom (transient []))
        pos (atom 0)
        vid (atom 0)]
    (doseq [book book-names]
      (let [words (oshb/book-words book)
            by-cv (group-by (juxt :chapter :verse) words)]
        (doseq [ch (sort (distinct (map :chapter words)))]
          (doseq [v (sort (distinct (map :verse (filter #(= ch (:chapter %)) words))))]
            (let [text (str/join " " (map :text (get by-cv [ch v])))
                  letters (norm/letter-stream text)
                  n (count letters)
                  start @pos
                  id @vid]
              ;; Fill verse ID for each letter position
              (dotimes [j n]
                (aset verses-arr (+ start j) id))
              (swap! refs conj! {:book book :ch ch :vs v
                                 :start start :end (+ start n)})
              (swap! pos + n)
              (swap! vid inc))))))
    {:verses   verses-arr
     :verse-ref (persistent! @refs)}))

(defn build!
  "Load Torah, build all arrays, cache in *state*."
  []
  (let [chars (oshb/torah-letters)
        n (count chars)
        _ (assert (= n total-letters)
                  (str "Expected " total-letters " letters, got " n))
        ;; Build byte stream
        stream (byte-array n)
        _ (dotimes [i n]
            (aset stream i (byte (get char->idx (nth chars i) 0))))
        ;; Build gematria values
        values (int-array n)
        _ (dotimes [i n]
            (aset values i (int (nth letter-gv (aget stream i)))))
        ;; Build running sum
        running (long-array n)
        _ (loop [i 0 sum 0]
            (when (< i n)
              (let [s (+ sum (aget values i))]
                (aset running i s)
                (recur (inc i) s))))
        ;; Build verse index
        {:keys [verses verse-ref]} (build-verse-index)]
    (reset! *state*
            {:stream    stream
             :values    values
             :running   running
             :verses    verses
             :verse-ref verse-ref
             :n         n})))

(defn space
  "Return the cached space, building if needed."
  []
  (or @*state* (build!)))

;; ── Accessors ──────────────────────────────────────────────

(defn letter-at
  "Letter at position i."
  [s ^long i]
  (nth letter-chars (aget ^bytes (:stream s) i)))

(defn gv-at
  "Gematria value at position i."
  ^long [s ^long i]
  (aget ^ints (:values s) i))

(defn running-at
  "Cumulative gematria sum through position i."
  ^long [s ^long i]
  (aget ^longs (:running s) i))

(defn verse-at
  "Verse reference for position i. Returns {:book :ch :vs}."
  [s ^long i]
  (let [vid (aget ^ints (:verses s) i)]
    (nth (:verse-ref s) vid)))

(defn describe
  "Full description of a position — for REPL inspection."
  [i]
  (let [s (space)
        coord (idx->coord i)
        v (verse-at s i)]
    {:position i
     :coord    (vec coord)
     :letter   (letter-at s i)
     :gematria (gv-at s i)
     :running  (running-at s i)
     :verse    (str (:book v) " " (:ch v) ":" (:vs v))
     :book     (:book v)
     :chapter  (:ch v)
     :vs       (:vs v)}))

;; ── Slicing ────────────────────────────────────────────────
;;
;; All query results are int[] of positions.
;; No objects, no maps — just position arrays.

(def ^:private axis-info
  {:a {:dim dim-a :stride stride-a}
   :b {:dim dim-b :stride stride-b}
   :c {:dim dim-c :stride stride-c}
   :d {:dim dim-d :stride stride-d}})

(def ^:private axes [:a :b :c :d])

(defn hyperplane
  "All positions where axis = value.
   Returns int[] of (total / dim) positions."
  [axis ^long value]
  (let [{:keys [dim stride]} (get axis-info axis)
        size (/ total-letters dim)
        result (int-array size)
        ;; Which axes are free?
        other-axes (remove #{axis} axes)]
    (assert (< value dim) (str axis " must be < " dim))
    (let [idx (atom 0)]
      (case axis
        :a (dotimes [b dim-b]
             (dotimes [c dim-c]
               (dotimes [d dim-d]
                 (aset result (int @idx) (int (coord->idx value b c d)))
                 (swap! idx inc))))
        :b (dotimes [a dim-a]
             (dotimes [c dim-c]
               (dotimes [d dim-d]
                 (aset result (int @idx) (int (coord->idx a value c d)))
                 (swap! idx inc))))
        :c (dotimes [a dim-a]
             (dotimes [b dim-b]
               (dotimes [d dim-d]
                 (aset result (int @idx) (int (coord->idx a b value d)))
                 (swap! idx inc))))
        :d (dotimes [a dim-a]
             (dotimes [b dim-b]
               (dotimes [c dim-c]
                 (aset result (int @idx) (int (coord->idx a b c value)))
                 (swap! idx inc))))))
    result))

(defn fiber
  "1D line: fix 3 axes, vary the free one.
   free-axis is the axis that varies; fixed is a map of the other 3.
   Returns int[] of dim(free-axis) positions."
  [free-axis fixed]
  (let [{:keys [dim]} (get axis-info free-axis)
        result (int-array dim)
        a (get fixed :a 0)
        b (get fixed :b 0)
        c (get fixed :c 0)
        d (get fixed :d 0)]
    (dotimes [i dim]
      (aset result i
            (int (case free-axis
                   :a (coord->idx i b c d)
                   :b (coord->idx a i c d)
                   :c (coord->idx a b i d)
                   :d (coord->idx a b c i)))))
    result))

(defn slab
  "2D slice: fix some axes, free the rest. Returns int[].
   fixed is a map of axis → value for axes to fix."
  [fixed]
  (let [free (remove (set (keys fixed)) axes)
        ;; Compute total size
        size (reduce * (map #(:dim (get axis-info %)) free))
        result (int-array size)
        a-range (if (contains? fixed :a) [(get fixed :a)] (range dim-a))
        b-range (if (contains? fixed :b) [(get fixed :b)] (range dim-b))
        c-range (if (contains? fixed :c) [(get fixed :c)] (range dim-c))
        d-range (if (contains? fixed :d) [(get fixed :d)] (range dim-d))
        idx (atom 0)]
    (doseq [a a-range b b-range c c-range d d-range]
      (aset result (int @idx) (int (coord->idx a b c d)))
      (swap! idx inc))
    result))

(defn slice
  "General query: fix some axes, free the rest.
   spec is a map of axis → value. Returns int[]."
  [spec]
  (slab spec))

(defn walk
  "Walk along an axis from a starting position. Generalized ELS.
   Returns int[] of n positions."
  [^long start axis ^long step ^long n]
  (let [{:keys [stride dim]} (get axis-info axis)
        coord (idx->coord start)
        result (int-array n)]
    (aset result 0 (int start))
    (loop [i 1
           pos start]
      (when (< i n)
        (let [next-pos (+ pos (* step stride))]
          (when (and (>= next-pos 0) (< next-pos total-letters))
            (aset result i (int next-pos))
            (recur (inc i) next-pos)))))
    result))

;; ── REPL ───────────────────────────────────────────────────

(comment
  ;; Build the space
  (def s (space))

  ;; Round-trip test
  (every? #(= % (apply coord->idx (vec (idx->coord %))))
          (range total-letters))

  ;; First letter = ב
  (letter-at s 0) ;=> \ב

  ;; Center seventh
  (def center (hyperplane :a 3))
  (alength center) ;=> 43550
  (map #(letter-at s %) (take 10 center))

  ;; One understanding line
  (map #(gv-at s %) (fiber :d {:a 0 :b 0 :c 0}))

  ;; Describe position 0
  (describe 0)

  ;; Walk along d-axis (ELS with skip 67)
  (map #(letter-at s %) (walk 0 :d 1 10))
  )
