(ns experiments.fiber.143ac-richest-fiber
  "Experiment 143ac: The richest fiber.

   Skip=805 from position 124 (Genesis 1:4, 'and he divided').
   36 unique 3-5 letter words in the first 100 fiber-letters (density 0.360).
   Full fiber (379 letters): 85 words from start=124, 90 words from start=0.

   Reads: beauty → wildness → exile → Hebrew → COVENANT → sons →
   he comes (GV=13) → tent (GV=36=Leah) → Esau (GV=376=peace).
   Ends at האל (The God) at Deuteronomy 31:9 — Moses giving the Torah."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.string :as str]))

;; (s/build!)

(defn read-fiber-text
  "Read every skip-th letter from start. Return string."
  [skip start len]
  (let [{:keys [letters]} (s/index)]
    (apply str (mapv #(nth letters (+ start (* skip %))) (range len)))))

(defn count-words
  "Count unique Torah words (length min-len to max-len) in a text."
  [text & {:keys [min-len max-len] :or {min-len 3 max-len 5}}]
  (let [torah-set (set (filter #(<= min-len (count %) max-len) (d/torah-words)))
        seen (atom #{})]
    (doseq [wl (range min-len (inc max-len))]
      (doseq [s (range (max 0 (- (count text) wl)))]
        (let [sub (subs text s (+ s wl))]
          (when (torah-set sub) (swap! seen conj sub)))))
    @seen))

(defn find-words-with-context
  "Find Torah words in fiber text and annotate with Torah position and verse."
  [skip start text]
  (let [torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))
        idx (s/index)
        found (atom [])
        seen (atom #{})]
    (doseq [wl (range 3 6)]
      (doseq [s (range (max 0 (- (count text) wl)))]
        (let [sub (subs text s (+ s wl))]
          (when (and (torah-set sub) (not (@seen sub)))
            (swap! seen conj sub)
            (let [torah-pos (+ start (* skip s))
                  v ((:verse-at idx) torah-pos)]
              (swap! found conj {:word sub :fiber-pos s :torah-pos torah-pos
                                  :gv (g/word-value sub)
                                  :book (:book v) :ch (:ch v) :vs (:vs v)}))))))
    (sort-by :fiber-pos @found)))

(defn density-numbers
  "Print the four key density measurements."
  []
  (let [n (:n (s/index))]
    (println "═══ DENSITY MEASUREMENTS (3-5 letter words) ═══\n")
    (doseq [[start label] [[0 "start=0"] [124 "start=124"]]]
      (doseq [[len label2] [[(quot (- n start) 805) "full"] [100 "first 100"]]]
        (let [text (read-fiber-text 805 start len)
              words (count (count-words text))]
          (println (format "  skip=805, %s, %s: %d letters, %d words, density=%.4f"
                           label label2 (count text) words (double (/ words (count text))))))))))

(defn optimal-start
  "Find the starting position (0 to skip-1) that maximizes density in first 100 letters."
  []
  (println "\n═══ OPTIMAL START (first 100 letters, 3-5 letter words) ═══\n")
  (let [best (atom {:start 0 :density 0 :words 0})]
    (doseq [start (range 805)]
      (let [text (read-fiber-text 805 start 100)
            words (count (count-words text))
            density (double (/ words 100))]
        (when (> density (:density @best))
          (reset! best {:start start :density density :words words}))))
    (let [{:keys [start density words]} @best
          v ((:verse-at (s/index)) start)]
      (println (format "  Best: start=%d (%s %d:%d) %d words, density=%.4f"
                       start (:book v) (:ch v) (:vs v) words density)))))

(defn read-richest-fiber
  "Read and print the richest fiber with full context."
  []
  (let [text (read-fiber-text 805 124 (quot (- (:n (s/index)) 124) 805))
        words (find-words-with-context 805 124 text)]
    (println (format "\n═══ RICHEST FIBER: skip=805, start=124 ═══"))
    (println (format "  %d letters, %d unique words\n" (count text) (count words)))
    (doseq [{:keys [word fiber-pos torah-pos gv book ch vs]} words]
      (println (format "  fpos=%-3d tpos=%-7d %-8s GV=%-4d %s %d:%d"
                       fiber-pos torah-pos word gv book ch vs)))))

(defn run-all []
  (density-numbers)
  (optimal-start)
  (read-richest-fiber))

(comment
  (s/build!)
  (run-all)

  ;; Quick density check with 2-letter words included
  (let [text (read-fiber-text 805 0 379)
        words-2-5 (count (count-words text :min-len 2 :max-len 5))]
    (println "2-5 letter words:" words-2-5 "density:" (double (/ words-2-5 379)))))
