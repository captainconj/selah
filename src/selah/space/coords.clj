(ns selah.space.coords
  "4D coordinate space over the Torah letter stream.

   304,850 = 7 × 50 × 13 × 67

   Every letter has a unique address (a, b, c, d):
     a ∈ {0..6}   — the seven days / completeness
     b ∈ {0..49}  — jubilee / ELS skip for תורה
     c ∈ {0..12}  — אחד (one) / אהבה (love)
     d ∈ {0..66}  — בינה (understanding)

   position = a×43,550 + b×871 + c×67 + d

   The factorization is a lens — the data is invariant.
   Use (with-dims [7 50 13 67] ...) or (set-dims! [13 67 7 50])
   to explore alternate arrangements of the same stream."
  (:require [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Constants ──────────────────────────────────────────────

(def ^:const total-letters 304850)

;; ── Lens: the factorization ───────────────────────────────
;;
;; The default lens: [7 50 13 67].
;; This is the ONLY 4D factorization of 304,850 that contains
;; all three of 7 (completeness), 13 (one/love), and 67 (understanding).
;; The 50 (jubilee) is forced.

(defonce ^:dynamic *dims* (atom [7 50 13 67]))

(defn- compute-strides [dims]
  (vec (for [i (range (count dims))]
         (reduce * (subvec dims (inc i))))))

(defn dims    [] @*dims*)
(defn strides [] (compute-strides @*dims*))

(defn set-dims!
  "Change the factorization lens. dims must multiply to 304,850."
  [dims]
  (assert (= (reduce * dims) total-letters)
          (str "Dimensions must multiply to " total-letters))
  (reset! *dims* (vec dims)))

(defmacro with-dims
  "Temporarily use a different factorization."
  [dims & body]
  `(let [old# @*dims*]
     (try
       (set-dims! ~dims)
       ~@body
       (finally
         (reset! *dims* old#)))))

;; Convenience accessors for the current lens
(defn dim-a ^long [] (nth @*dims* 0))
(defn dim-b ^long [] (nth @*dims* 1))
(defn dim-c ^long [] (nth @*dims* 2))
(defn dim-d ^long [] (nth @*dims* 3))

(defn stride-a ^long [] (nth (strides) 0))
(defn stride-b ^long [] (nth (strides) 1))
(defn stride-c ^long [] (nth (strides) 2))
(defn stride-d ^long [] 1)

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
  (let [sa (stride-a) sb (stride-b) sc (stride-c)
        a (quot i sa)
        r (rem  i sa)
        b (quot r sb)
        r (rem  r sb)
        c (quot r sc)
        d (rem  r sc)]
    (long-array [a b c d])))

(defn coord->idx
  "Coordinate [a b c d] → position."
  ^long [^long a ^long b ^long c ^long d]
  (+ (* a (stride-a)) (* b (stride-b)) (* c (stride-c)) d))

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

(defn- axis-info []
  {:a {:dim (dim-a) :stride (stride-a)}
   :b {:dim (dim-b) :stride (stride-b)}
   :c {:dim (dim-c) :stride (stride-c)}
   :d {:dim (dim-d) :stride (stride-d)}})

(def ^:private axes [:a :b :c :d])

(defn hyperplane
  "All positions where axis = value.
   Returns int[] of (total / dim) positions."
  [axis ^long value]
  (let [{:keys [dim]} (get (axis-info) axis)
        da (dim-a) db (dim-b) dc (dim-c) dd (dim-d)
        size (/ total-letters dim)
        result (int-array size)]
    (assert (< value dim) (str axis " must be < " dim))
    (let [idx (atom 0)]
      (case axis
        :a (dotimes [b db]
             (dotimes [c dc]
               (dotimes [d dd]
                 (aset result (int @idx) (int (coord->idx value b c d)))
                 (swap! idx inc))))
        :b (dotimes [a da]
             (dotimes [c dc]
               (dotimes [d dd]
                 (aset result (int @idx) (int (coord->idx a value c d)))
                 (swap! idx inc))))
        :c (dotimes [a da]
             (dotimes [b db]
               (dotimes [d dd]
                 (aset result (int @idx) (int (coord->idx a b value d)))
                 (swap! idx inc))))
        :d (dotimes [a da]
             (dotimes [b db]
               (dotimes [c dc]
                 (aset result (int @idx) (int (coord->idx a b c value)))
                 (swap! idx inc))))))
    result))

(defn fiber
  "1D line: fix 3 axes, vary the free one.
   free-axis is the axis that varies; fixed is a map of the other 3.
   Returns int[] of dim(free-axis) positions."
  [free-axis fixed]
  (let [{:keys [dim]} (get (axis-info) free-axis)
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
  (let [ai (axis-info)
        free (remove (set (keys fixed)) axes)
        size (reduce * (map #(:dim (get ai %)) free))
        result (int-array size)
        a-range (if (contains? fixed :a) [(get fixed :a)] (range (dim-a)))
        b-range (if (contains? fixed :b) [(get fixed :b)] (range (dim-b)))
        c-range (if (contains? fixed :c) [(get fixed :c)] (range (dim-c)))
        d-range (if (contains? fixed :d) [(get fixed :d)] (range (dim-d)))
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
  (let [{:keys [stride dim]} (get (axis-info) axis)
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

;; ── Preimage ──────────────────────────────────────────────
;;
;; The machine reads forward: (b, c) → slab → text.
;; The preimage reads backward: word → which (b, c, d) contain it?

(defn preimage
  "Find all a-fibers containing a given Hebrew word.
   Returns vec of {:b :c :d :text :gv :verse} maps.
   word is a Hebrew string (e.g. \"תורה\")."
  [word]
  (let [s (space)
        results (atom (transient []))]
    (doseq [b (range (dim-b))
            c (range (dim-c))
            d (range (dim-d))]
      (let [fib (fiber :a {:b b :c c :d d})
            text (apply str (map #(letter-at s %) (seq fib)))]
        (when (str/includes? text word)
          (let [gv (reduce + (map #(gv-at s %) (seq fib)))
                v (verse-at s (aget fib 0))]
            (swap! results conj!
                   {:b b :c c :d d
                    :text text :gv gv
                    :verse (str (:book v) " " (:ch v) ":" (:vs v))})))))
    (persistent! @results)))

(defn preimage-on
  "Find all fibers on a given axis containing a word.
   axis is :a, :b, :c, or :d. Returns vec of maps."
  [axis word]
  (let [s (space)
        ai (axis-info)
        free-axis axis
        fixed-axes (remove #{axis} axes)
        dims (mapv #(:dim (get ai %)) fixed-axes)
        ranges (mapv #(range %) dims)
        results (atom (transient []))]
    (doseq [i0 (nth ranges 0)
            i1 (nth ranges 1)
            i2 (nth ranges 2)]
      (let [fixed (zipmap fixed-axes [i0 i1 i2])
            fib (fiber free-axis fixed)
            text (apply str (map #(letter-at s %) (seq fib)))]
        (when (str/includes? text word)
          (let [gv (reduce + (map #(gv-at s %) (seq fib)))]
            (swap! results conj!
                   (merge fixed
                          {:text text :gv gv :axis axis}))))))
    (persistent! @results)))

(defn preimage-all
  "Scan all a-fibers once, checking for all words in parallel.
   words is a seq of Hebrew strings.
   Returns {word -> vec of {:b :c :d :text :gv :verse}}."
  [words]
  (let [s (space)
        results (atom (into {} (map #(vector % (transient [])) words)))]
    (doseq [b (range (dim-b))
            c (range (dim-c))
            d (range (dim-d))]
      (let [fib (fiber :a {:b b :c c :d d})
            text (apply str (map #(letter-at s %) (seq fib)))
            gv (delay (reduce + (map #(gv-at s %) (seq fib))))
            v  (delay (let [vr (verse-at s (aget fib 0))]
                        (str (:book vr) " " (:ch vr) ":" (:vs vr))))]
        (doseq [w words]
          (when (str/includes? text w)
            (swap! results update w conj!
                   {:b b :c c :d d
                    :text text :gv @gv :verse @v})))))
    (into {} (map (fn [[w hits]] [w (persistent! hits)])) @results)))

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
