(ns selah.els.engine
  "Equidistant Letter Spacing — the core engine.
   Works on any letter stream (vector of chars).")

(defn extract
  "Extract letters at equidistant spacing from stream.
   stream - vector of chars
   start  - 0-based starting index
   skip   - step size (positive = forward, negative = reverse)
   k      - number of letters to extract
   Returns a string, or nil if any index is out of bounds."
  [stream start skip k]
  (let [n (count stream)]
    (loop [i     start
           remaining k
           acc   (transient [])]
      (if (zero? remaining)
        (apply str (persistent! acc))
        (when (and (>= i 0) (< i n))
          (recur (+ i skip)
                 (dec remaining)
                 (conj! acc (nth stream i))))))))

(defn search
  "Find all starting positions where target appears at the given skip.
   Returns a seq of {:start :skip :word}."
  [stream target skip]
  (let [k      (count target)
        n      (count stream)
        target (str target)]
    (for [start (range n)
          :let [word (extract stream start skip k)]
          :when (= word target)]
      {:start start :skip skip :word word})))

(defn scan
  "Search for target across a range of skips (both forward and reverse).
   Returns a seq of all hits."
  [stream target min-skip max-skip]
  (mapcat
   (fn [skip]
     (concat (search stream target skip)
             (search stream target (- skip))))
   (range min-skip (inc max-skip))))

(defn verify-positions
  "Given 1-based positions from published claims, extract the letters.
   Returns {:word :positions :constant-step? :step}."
  [stream positions-1based]
  (let [letters (map #(nth stream (dec %) nil) positions-1based)
        word    (apply str letters)
        steps   (map - (rest positions-1based) positions-1based)
        step    (first steps)]
    {:word           word
     :positions      positions-1based
     :constant-step? (every? #(= % step) steps)
     :step           step}))

(defn debug-window
  "Show letters around a position for debugging.
   pos is 1-based (matching published conventions).
   Returns {:snippet :pointer :letter}."
  [stream pos-1based radius]
  (let [i  (dec pos-1based)
        lo (max 0 (- i radius))
        hi (min (count stream) (+ i radius 1))]
    {:snippet (apply str (subvec stream lo hi))
     :pointer (- i lo)
     :letter  (nth stream i nil)
     :pos-0   i
     :pos-1   pos-1based}))

(comment
  ;; Usage pattern:
  ;; (require '[selah.text.fetch :as fetch])
  ;; (def genesis (fetch/book-letters "Genesis"))
  ;; (search genesis "תורה" 50)
  ;; (verify-positions genesis [6 56 106 156])
  ;; (debug-window genesis 6 10)
  )
