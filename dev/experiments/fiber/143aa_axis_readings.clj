(ns experiments.fiber.143aa-axis-readings
  "Experiment 143aa-ab: Axis readings and direction density.

   Each axis reads as a text. Completeness: בתוך (in the midst).
   Earlier drafts overstated skip=805 by mixing 2-5 letter counts,
   unequal fiber lengths, and different start positions.
   The corrected result: skip=805 is a rich direction, but not denser
   than the surface reading under fair comparison.
   Love and Abel remain adjacent at the sea crossing."
  (:require [selah.search :as s]
            [selah.fiber :as f]
            [selah.gematria :as g]
            [selah.dict :as d]
            [clojure.string :as str]))

;; (s/build!)

(defn read-axis
  "Read every skip-th letter from position 0. Return string."
  [skip count]
  (let [{:keys [letters]} (s/index)]
    (apply str (mapv #(nth letters (* skip %)) (range count)))))

(defn find-words-in-text
  "Find all Torah words (length 2-5) in a text string."
  [text]
  (let [torah-set (set (filter #(<= 2 (count %) 5) (d/torah-words)))
        found (atom [])
        seen (atom #{})]
    (doseq [wlen (range 2 6)]
      (doseq [start (range (max 0 (- (count text) wlen)))]
        (let [sub (subs text start (+ start wlen))]
          (when (and (torah-set sub) (not (@seen sub)))
            (swap! seen conj sub)
            (swap! found conj {:word sub :pos start :gv (g/word-value sub)})))))
    (sort-by :pos @found)))

(defn read-all-axes []
  (println "═══ AXIS READINGS ═══\n")
  (let [{:keys [n]} (s/index)]
    (doseq [[skip label ct] [[43550 "completeness(7)" 7]
                              [871 "jubilee(50)" 50]
                              [67 "love(13)" 13]
                              [1 "understanding(67)" 67]]]
      (let [text (read-axis skip ct)
            words (find-words-in-text text)]
        (println (format "%s: %s  GV=%d  (%d words)" label text (g/word-value text) (count words)))
        (doseq [{:keys [word pos gv]} words]
          (println (format "  pos=%d  %s  GV=%d" pos word gv)))
        (println)))))

(defn completeness-axis-context []
  (println "═══ COMPLETENESS AXIS: 7 letters with verse context ═══\n")
  (let [{:keys [letters]} (s/index)
        idx (s/index)]
    (doseq [i (range 7)]
      (let [pos (* 43550 i)
            letter (nth letters pos)
            tw ((:word-at idx) pos)
            v ((:verse-at idx) pos)]
        (println (format "  a=%d pos=%-7d %s in %-10s %s %d:%d"
                         i pos letter (if tw (:word tw) "?") (:book v) (:ch v) (:vs v)))))))

(defn direction-density-ranking []
  (println "═══ DIRECTION DENSITY RANKING ═══\n")
  (let [{:keys [letters n]} (s/index)
        view (s/make-view [7 50 13 67])
        dirs (s/direction-vectors 4)
        strides (:strides view)
        torah-set (set (filter #(<= 2 (count %) 5) (d/torah-words)))]
    (let [results (for [d dirs]
                    (let [skip (Math/abs (long (s/direction->skip strides d)))
                          flen (min 500 (quot n skip))
                          text (read-axis skip flen)
                          words (count (find-words-in-text text))]
                      {:dir d :skip skip :letters flen :words words
                       :density (double (/ words flen))}))]
      (doseq [{:keys [dir skip letters words density]} (sort-by :density > results)]
        (println (format "  dir=%-14s skip=%-7d %4d letters %3d words density=%.3f"
                         (str dir) skip letters words density))))))

(defn densest-fiber-reading []
  (println "═══ DENSEST FIBER (skip=805) ═══\n")
  (let [{:keys [n]} (s/index)
        text (read-axis 805 (quot n 805))
        words (find-words-in-text text)]
    (println (format "  %d letters, %d unique words\n" (count text) (count words)))
    (let [{:keys [letters]} (s/index)
          idx (s/index)]
      (doseq [{:keys [word pos gv]} (take 30 words)]
        (let [torah-pos (* 805 pos)
              v ((:verse-at idx) torah-pos)]
          (println (format "  pos=%-3d  %-8s GV=%-4d  %s %d:%d"
                           pos word gv (:book v) (:ch v) (:vs v))))))))

(defn special-skip-readings []
  (doseq [[skip label] [[13 "love-number"] [26 "YHWH-number"] [137 "axis-sum"]]]
    (println (format "\n═══ SKIP=%d (%s) ═══\n" skip label))
    (let [text (read-axis skip (min 50 (quot (:n (s/index)) skip)))
          words (find-words-in-text text)]
      (println (format "  %s\n" text))
      (doseq [{:keys [word pos gv]} words]
        (println (format "  pos=%d  %s  GV=%d" pos word gv))))))

(defn run-all []
  (read-all-axes)
  (completeness-axis-context)
  (println)
  (direction-density-ranking))

(comment
  (s/build!)
  (run-all)
  (densest-fiber-reading)
  (special-skip-readings))
