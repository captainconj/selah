(ns selah.search
  "N-dimensional word search across Torah decompositions.

   Given a Hebrew word and a decomposition of 304,850:
   find every occurrence along any straight line —
   axes, diagonals, any direction vector.

   (require '[selah.search :as s])
   (s/build!)
   (s/find-word [7 50 13 67] \"תורה\")
   (s/find-word-all \"תורה\")
   (s/connections [7 50 13 67] [\"תורה\" \"יהוה\" \"שלום\"])"
  (:require [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════
;; LAYER 1: THE TORAH STREAM (build once)
;; ══════════════════════════════════════════════════════

(defonce ^:dynamic *index* (atom nil))

(defn build!
  "Build the Torah index. Call once.
   Indexes letters, verses (with text), and word boundaries."
  []
  (println "[search] Building Torah index...")
  (let [letters (vec (mapcat sefaria/book-letters
                             ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
        n (count letters)
        ;; Build verse + word index in a single pass
        pos (atom 0)
        verses (atom [])
        words (atom [])
        _ (doseq [book ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]]
            (doseq [ch (range 1 (inc (get sefaria/book-chapters book)))]
              (let [vs (sefaria/fetch-chapter book ch)]
                (doseq [[v-idx verse] (map-indexed vector vs)]
                  (let [stripped (norm/strip-html verse)
                        clean (apply str (filter norm/hebrew-letter? stripped))
                        len (count clean)
                        v-start @pos
                        ;; Extract words: split on spaces/maqaf/sof-pasuq, track letter positions
                        ;; Niqqud and cantillation are WITHIN words, not boundaries.
                        _ (let [word-break? (fn [c] (or (= c \space)
                                                        (= c \־)     ;; maqaf U+05BE
                                                        (= c \׃)))]  ;; sof-pasuq U+05C3
                            (loop [chars (seq stripped) w-chars [] w-start @pos]
                              (if-not chars
                                ;; Flush last word
                                (when (seq w-chars)
                                  (swap! words conj {:word (apply str w-chars)
                                                     :start w-start
                                                     :end @pos
                                                     :verse-idx (count @verses)}))
                                (let [c (first chars)]
                                  (cond
                                    ;; Hebrew letter — part of current word
                                    (norm/hebrew-letter? c)
                                    (do (swap! pos inc)
                                        (recur (next chars) (conj w-chars c) w-start))

                                    ;; Word boundary — flush current word
                                    (word-break? c)
                                    (do (when (seq w-chars)
                                          (swap! words conj {:word (apply str w-chars)
                                                             :start w-start
                                                             :end @pos
                                                             :verse-idx (count @verses)}))
                                        (recur (next chars) [] @pos))

                                    ;; Niqqud/cantillation — skip, stay in current word
                                    :else
                                    (recur (next chars) w-chars w-start))))))]
                    (swap! verses conj {:book book :ch ch :vs (inc v-idx)
                                        :start v-start :end (+ v-start len)
                                        :text clean}))))))
        vi (vec @verses)
        wi (vec @words)
        ;; Letter inverted index: char → sorted int array of positions
        letter-idx (reduce (fn [m i]
                             (update m (nth letters i) (fnil conj []) i))
                           {} (range n))
        ;; Binary search helper
        bsearch (fn [index idx key-start key-end]
                  (loop [lo 0 hi (dec (count index))]
                    (if (> lo hi) nil
                      (let [mid (quot (+ lo hi) 2)
                            entry (nth index mid)]
                        (cond
                          (< idx (key-start entry)) (recur lo (dec mid))
                          (>= idx (key-end entry)) (recur (inc mid) hi)
                          :else entry)))))
        verse-at (fn [idx] (bsearch vi idx :start :end))
        word-at  (fn [idx] (bsearch wi idx :start :end))]
    (reset! *index*
            {:letters letters
             :n n
             :letter-idx letter-idx
             :verses vi
             :words wi
             :verse-at verse-at
             :word-at word-at})
    (println (format "[search] Indexed %,d letters, %,d verses, %,d words, %d letter types."
                     n (count vi) (count wi) (count letter-idx)))))

(defn index [] @*index*)

;; ══════════════════════════════════════════════════════
;; LAYER 2: DECOMPOSITION VIEW
;; ══════════════════════════════════════════════════════

(defn make-view
  "Create a decomposition view with precomputed strides."
  [dims]
  (let [k (count dims)
        strides (vec (loop [i (dec k) acc (list 1) s 1]
                       (if (< i 1)
                         (vec acc)
                         (let [s' (* s (nth dims i))]
                           (recur (dec i) (cons s' acc) s')))))
        coord->idx (fn [coord]
                     (reduce + (map * coord strides)))
        idx->coord (fn [idx]
                     (loop [i 0 rem idx acc []]
                       (if (= i k) acc
                         (recur (inc i)
                                (mod rem (nth strides i))
                                (conj acc (quot rem (nth strides i)))))))]
    ;; Fix: idx->coord needs division by strides properly
    {:dims (vec dims)
     :k k
     :strides strides
     :coord->idx coord->idx
     :idx->coord (fn [idx]
                   (loop [i 0 rem idx acc []]
                     (if (= i k) acc
                       (let [s (nth strides i)]
                         (recur (inc i) (mod rem s) (conj acc (quot rem s)))))))}))

;; ══════════════════════════════════════════════════════
;; LAYER 3: DIRECTION VECTORS
;; ══════════════════════════════════════════════════════

(defn direction-vectors
  "All unique direction vectors for k dimensions.
   Each component ∈ {-1, 0, +1}. Exclude zero vector.
   Only keep one of each pair (v, -v) — the 'positive' one."
  [k]
  (let [all (for [bits (range 1 (long (Math/pow 3 k)))]
              (loop [i 0 b bits acc []]
                (if (= i k) acc
                  (recur (inc i) (quot b 3)
                         (conj acc (dec (mod b 3)))))))
        ;; Keep only the first non-zero component positive
        canonical (fn [v]
                    (let [first-nz (first (filter #(not= 0 %) v))]
                      (cond
                        (nil? first-nz) nil
                        (pos? first-nz) v
                        :else (mapv - v))))]
    (vec (distinct (remove nil? (map canonical all))))))

(defn direction->skip
  "Convert a direction vector to a linear skip distance."
  [strides direction]
  (reduce + (map * direction strides)))

;; ══════════════════════════════════════════════════════
;; LAYER 4: THE SEARCH
;; ══════════════════════════════════════════════════════

(defn search-at-skip
  "Find all occurrences of word at a given skip distance.
   Returns vec of start positions."
  [letters letter-idx word skip n]
  (when (not= 0 skip)
    (let [chars (vec word)
          wlen (count chars)
          first-char (first chars)
          first-positions (get letter-idx first-char [])]
      (persistent!
        (reduce (fn [acc start]
                  (let [end (+ start (* skip (dec wlen)))]
                    (if (or (neg? end) (>= end n)
                            (neg? start))
                      acc
                      (if (loop [i 1]
                            (if (= i wlen) true
                              (let [pos (+ start (* skip i))]
                                (if (or (neg? pos) (>= pos n))
                                  false
                                  (if (= (nth chars i) (nth letters pos))
                                    (recur (inc i))
                                    false)))))
                        (conj! acc start)
                        acc))))
                (transient [])
                first-positions)))))

(defn enrich-hit
  "Enrich a search hit with coordinates, verses, Torah words, and analysis."
  [{:keys [letters verse-at word-at]} view word skip start]
  (let [n (count letters)
        wlen (count (vec word))
        positions (mapv #(+ start (* skip %)) (range wlen))
        coords (mapv (:idx->coord view) positions)
        verse-refs (mapv verse-at positions)
        ;; Torah words at each letter position
        torah-words (mapv word-at positions)
        unique-torah-words (vec (distinct (remove nil? (map :word torah-words))))
        ;; Which axes are constant across all positions?
        constant-axes (for [i (range (:k view))
                           :let [vals (map #(nth % i) coords)]
                           :when (apply = vals)]
                       i)
        ;; Which axes vary?
        varying-axes (for [i (range (:k view))
                          :let [vals (map #(nth % i) coords)]
                          :when (not (apply = vals))]
                      i)
        ;; Verse span
        first-verse (first verse-refs)
        last-verse (last verse-refs)
        unique-verses (vec (distinct (remove nil? verse-refs)))
        ;; Gematria at each position
        letter-gvs (mapv #(g/letter-value (nth letters %)) positions)
        torah-word-gvs (mapv #(when % (g/word-value (:word %))) torah-words)]
    {:word word
     :skip skip
     :start start
     :positions positions
     :coords coords
     :verse-refs verse-refs
     :first-verse first-verse
     :last-verse last-verse
     :unique-verses unique-verses
     :letter-span (- (last positions) (first positions))
     :constant-axes (vec constant-axes)
     :varying-axes (vec varying-axes)
     :word-gv (g/word-value word)
     :position-letter-gvs letter-gvs
     :position-letter-gv-sum (reduce + letter-gvs)
     ;; Fiber context — the Torah words this fiber passes through
     :torah-words torah-words
     :unique-torah-words unique-torah-words
     :torah-word-count (count unique-torah-words)
     :torah-word-gvs torah-word-gvs}))

;; ══════════════════════════════════════════════════════
;; LAYER 5: PUBLIC API
;; ══════════════════════════════════════════════════════

(defn find-word
  "Find all occurrences of a Hebrew word along any straight line
   in a given decomposition.
   Returns a vec of enriched hits, sorted by skip."
  ([dims word] (find-word dims word {}))
  ([dims word {:keys [max-results] :or {max-results 1000}}]
   (let [{:keys [letters letter-idx n] :as idx} (index)
         view (make-view dims)
         dirs (direction-vectors (:k view))
         ;; Compute all unique skip distances
         skip-to-dirs (group-by #(direction->skip (:strides view) %)
                                dirs)
         unique-skips (keys skip-to-dirs)
         ;; Search at each unique skip
         raw-hits (mapcat (fn [skip]
                            (when-let [starts (search-at-skip letters letter-idx word skip n)]
                              (map (fn [start]
                                     {:skip skip
                                      :start start
                                      :directions (get skip-to-dirs skip)})
                                   starts)))
                          unique-skips)
         ;; Also search negative skips (reverse direction)
         neg-hits (mapcat (fn [skip]
                            (when-let [starts (search-at-skip letters letter-idx word (- skip) n)]
                              (map (fn [start]
                                     {:skip (- skip)
                                      :start start
                                      :directions (mapv #(mapv - %) (get skip-to-dirs skip))})
                                   starts)))
                          (filter pos? unique-skips))
         all-hits (take max-results (concat raw-hits neg-hits))
         ;; Enrich
         enriched (mapv #(merge (enrich-hit idx view word (:skip %) (:start %))
                                (select-keys % [:directions]))
                        all-hits)]
     (sort-by :skip enriched))))

(declare all-decompositions)

(defn find-word-all
  "Find a word across all non-trivial decompositions of 304,850.
   Returns map of dims → hits."
  ([word] (find-word-all word {}))
  ([word opts]
   (let [decomps (all-decompositions)]
     (into {}
           (for [dims decomps
                 :let [hits (find-word dims word opts)]
                 :when (seq hits)]
             [dims hits])))))

(defn connections
  "Find spatial connections between multiple words in a decomposition.
   Returns co-linear pairs, shared axes, distances."
  [dims words]
  (let [view (make-view dims)
        results (into {} (for [w words]
                           [w (find-word dims w {:max-results 100})]))
        ;; Find pairs of words that share an axis value
        shared-axes (for [w1 words
                          w2 words
                          :when (pos? (compare w1 w2))
                          h1 (get results w1)
                          h2 (get results w2)
                          :let [c1 (first (:coords h1))
                                c2 (first (:coords h2))
                                shared (for [i (range (count c1))
                                            :when (= (nth c1 i) (nth c2 i))]
                                        i)]
                          :when (seq shared)]
                      {:words [w1 w2]
                       :shared-axes shared
                       :coords [c1 c2]
                       :skips [(:skip h1) (:skip h2)]})]
    {:per-word results
     :shared-axes (vec (take 50 shared-axes))}))

;; ══════════════════════════════════════════════════════
;; DECOMPOSITIONS
;; ══════════════════════════════════════════════════════

(defn factorizations
  "All ordered factorizations of n into exactly k parts >= 2."
  [n k]
  (if (= k 1)
    (if (>= n 2) [[n]] [])
    (for [f (range 2 (inc (int (Math/sqrt n))))
          :when (zero? (mod n f))
          rest (factorizations (/ n f) (dec k))
          :when (or (empty? rest) (<= f (first rest)))]
      (into [f] rest))))

(defn all-decompositions
  "All non-trivial decompositions of 304,850 from 3D to 6D."
  []
  (vec (for [k [3 4 5 6]
             f (factorizations 304850 k)]
         f)))

;; ══════════════════════════════════════════════════════
;; DISPLAY
;; ══════════════════════════════════════════════════════

(defn print-hits
  "Pretty-print search results."
  [hits & {:keys [limit] :or {limit 20}}]
  (println (format "  %d hits\n" (count hits)))
  (doseq [{:keys [word skip start first-verse last-verse
                   letter-span constant-axes varying-axes
                   unique-torah-words torah-word-count]} (take limit hits)]
    (println (format "  skip=%-6d  start=%-6d  span=%-6d  words=%-2d  %s %d:%d → %s %d:%d  const=%s vary=%s"
                     skip start letter-span torah-word-count
                     (:book first-verse) (:ch first-verse) (:vs first-verse)
                     (:book last-verse) (:ch last-verse) (:vs last-verse)
                     constant-axes varying-axes))
    (when (seq unique-torah-words)
      (println (format "    passes through: %s"
                       (str/join " " unique-torah-words))))))
