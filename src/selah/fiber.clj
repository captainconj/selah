(ns selah.fiber
  "Fiber interpretation — read the Torah along non-surface paths.

   A fiber is a straight line through a decomposition space.
   Each fiber passes through Torah words and verses.
   This namespace reads those passages as narrative.

   (require '[selah.search :as s] '[selah.fiber :as f])
   (s/build!)
   (def hits (s/find-word [7 50 13 67] \"תורה\"))
   (f/read-fiber (first hits))
   (f/print-fiber (first hits))
   (f/richest hits)"
  (:require [selah.search :as search]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ══════════════════════════════════════════════════════
;; FIBER READING
;; ══════════════════════════════════════════════════════

(defn read-fiber
  "Read a fiber: for each letter in the hit, what Torah word
   does it land in, what verse, what coordinate?
   Returns a seq of letter-contexts."
  [hit]
  (let [{:keys [positions coords verse-refs torah-words]} hit]
    (mapv (fn [pos coord verse tw]
            {:position pos
             :coord coord
             :verse verse
             :torah-word (:word tw)
             :torah-word-gv (when tw (g/word-value (:word tw)))})
          positions coords verse-refs torah-words)))

(defn fiber-message
  "The message of a fiber: the sequence of Torah words it passes through,
   read in order. This is what the geometry selected."
  [hit]
  (:unique-torah-words hit))

(defn fiber-verses
  "The unique verses a fiber passes through, in order."
  [hit]
  (let [seen (atom #{})]
    (reduce (fn [acc v]
              (let [k [(:book v) (:ch v) (:vs v)]]
                (if (or (nil? v) (@seen k))
                  acc
                  (do (swap! seen conj k)
                      (conj acc v)))))
            [] (:verse-refs hit))))

(defn fiber-span
  "The narrative span: from which verse to which verse,
   how many chapters crossed, how many unique verses touched."
  [hit]
  (let [{:keys [first-verse last-verse unique-verses letter-span]} hit
        ch-span (when (and first-verse last-verse)
                  (if (= (:book first-verse) (:book last-verse))
                    (- (:ch last-verse) (:ch first-verse))
                    :cross-book))]
    {:from (when first-verse
             (str (:book first-verse) " " (:ch first-verse) ":" (:vs first-verse)))
     :to (when last-verse
           (str (:book last-verse) " " (:ch last-verse) ":" (:vs last-verse)))
     :chapter-span ch-span
     :verse-count (count unique-verses)
     :letter-span letter-span}))

;; ══════════════════════════════════════════════════════
;; RANKING — which fibers carry the most?
;; ══════════════════════════════════════════════════════

(defn richest
  "Sort hits by torah-word-count descending.
   Fibers that pass through more distinct Hebrew words
   carry richer narrative."
  ([hits] (richest hits 20))
  ([hits n]
   (->> hits
        (sort-by :torah-word-count >)
        (take n)
        vec)))

(defn by-verse-count
  "Sort hits by number of distinct verses touched."
  ([hits] (by-verse-count hits 20))
  ([hits n]
   (->> hits
        (sort-by #(count (:unique-verses %)) >)
        (take n)
        vec)))

(defn by-span
  "Sort hits by letter-span descending — widest reach through the text."
  ([hits] (by-span hits 20))
  ([hits n]
   (->> hits
        (sort-by :letter-span >)
        (take n)
        vec)))

(defn non-surface
  "Filter to only non-surface fibers (skip != 1 and skip != -1)."
  [hits]
  (filterv #(not= 1 (Math/abs (long (:skip %)))) hits))

;; ══════════════════════════════════════════════════════
;; DISPLAY
;; ══════════════════════════════════════════════════════

(defn- verse-ref [v]
  (when v
    (str (:book v) " " (:ch v) ":" (:vs v))))

(defn print-fiber
  "Print a single fiber's full reading."
  [hit]
  (let [ctx (read-fiber hit)
        span (fiber-span hit)
        msg (fiber-message hit)]
    (println (format "\n═══ %s  skip=%d  %d letters ═══"
                     (:word hit) (:skip hit) (count ctx)))
    (println (format "  span: %s → %s (%d verses, %,d letters apart)"
                     (:from span) (:to span)
                     (:verse-count span) (:letter-span span)))
    (println (format "  axes: const=%s vary=%s"
                     (:constant-axes hit) (:varying-axes hit)))
    (println (format "  word GV: %d  letter GVs: %s"
                     (:word-gv hit) (:position-letter-gvs hit)))
    (println (format "  passes through %d Torah words: %s"
                     (count msg) (str/join " " msg)))
    (println)
    (doseq [{:keys [position coord verse torah-word torah-word-gv]} ctx]
      (println (format "  [%,d] %s  in %s  verse: %s"
                       position
                       (or torah-word "?")
                       (str coord)
                       (verse-ref verse)))
      (when torah-word-gv
        (println (format "         GV=%d" torah-word-gv))))))

(defn print-richest
  "Find and print the richest non-surface fibers for a word."
  ([dims word] (print-richest dims word 10))
  ([dims word n]
   (let [hits (search/find-word dims word)
         ns-hits (non-surface hits)
         rich (richest ns-hits n)]
     (println (format "\n%s in %s: %d total hits, %d non-surface"
                      word (str dims) (count hits) (count ns-hits)))
     (println (format "Top %d by Torah word count:\n" (count rich)))
     (doseq [h rich]
       (println (format "  skip=%-6d  words=%-2d  span=%-6d  %s → %s  [%s]"
                        (:skip h) (:torah-word-count h) (:letter-span h)
                        (verse-ref (:first-verse h))
                        (verse-ref (:last-verse h))
                        (str/join " " (:unique-torah-words h))))))))
