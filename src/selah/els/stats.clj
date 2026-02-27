(ns selah.els.stats
  "Statistical significance analysis for ELS patterns."
  (:require [selah.els.engine :as els]))

(defn letter-prob
  "Probability of a specific letter in a stream, based on frequency."
  [stream ch]
  (/ (double (count (filter #(= % ch) stream))) (count stream)))

(defn word-prob
  "Probability of a word occurring at any single (start, skip) position,
   assuming letter independence."
  [stream word]
  (reduce * (map #(letter-prob stream %) word)))

(defn expected-hits
  "Expected number of ELS hits for a word at a given skip,
   based on letter frequencies and stream length."
  [stream word skip]
  (let [k (count word)
        n (count stream)
        valid-starts (- n (* (dec k) (Math/abs (int skip))))]
    (* valid-starts (word-prob stream word))))

(defn p-at-least-one
  "Poisson P(>=1 hit) given expected count lambda."
  [lambda]
  (- 1.0 (Math/exp (- (double lambda)))))

(defn p-first-hit-in-first-n
  "P(at least one hit in first n starting positions), assuming independence."
  [stream word n]
  (let [p (word-prob stream word)]
    (- 1.0 (Math/pow (- 1.0 p) n))))

(defn shuffle-stream
  "Randomly shuffle a letter stream, preserving letter frequencies."
  [stream]
  (vec (shuffle stream)))

(defn shuffled-trial
  "Run one shuffled trial. Returns map of results."
  [stream word skip]
  (let [shuffled (shuffle-stream stream)
        hits     (els/search shuffled word skip)]
    {:count       (count hits)
     :first-start (when (seq hits) (:start (first hits)))}))

(defn shuffled-controls
  "Run n shuffled trials, return summary statistics."
  [stream word skip n]
  (let [results    (repeatedly n #(shuffled-trial stream word skip))
        counts     (map :count results)
        actual     (count (els/search stream word skip))
        avg        (/ (double (reduce + counts)) n)
        gte-actual (count (filter #(>= % actual) counts))
        first-in-10 (count (filter #(and (:first-start %)
                                          (< (:first-start %) 10))
                                   results))]
    {:actual          actual
     :shuffled-avg    avg
     :shuffled-gte    gte-actual
     :p-gte           (/ gte-actual (double n))
     :first-in-10     first-in-10
     :p-first-in-10   (/ first-in-10 (double n))
     :trials          n}))

(defn mirror-trial
  "One trial of the mirror test: shuffle 4 outer books independently,
   check if תורה appears forward in first two and reversed in last two."
  [genesis exodus numbers deuteronomy]
  (let [g (shuffle-stream genesis)
        e (shuffle-stream exodus)
        n (shuffle-stream numbers)
        d (shuffle-stream deuteronomy)]
    (and (seq (els/search g "תורה" 50))
         (seq (els/search e "תורה" 50))
         (seq (els/search n "תורה" -50))
         (seq (els/search d "תורה" -50)))))

(defn chiastic-trial
  "One trial of the full chiastic test: all 5 books shuffled independently."
  [genesis exodus leviticus numbers deuteronomy]
  (let [g (shuffle-stream genesis)
        e (shuffle-stream exodus)
        l (shuffle-stream leviticus)
        n (shuffle-stream numbers)
        d (shuffle-stream deuteronomy)]
    (and (seq (els/search g "תורה" 50))
         (seq (els/search e "תורה" 50))
         (seq (els/search l "יהוה" 7))
         (seq (els/search n "תורה" -50))
         (seq (els/search d "תורה" -50)))))

(defn compound-probability
  "Analytical compound probability of the full pattern with positional constraints."
  [genesis exodus leviticus numbers deuteronomy]
  (let [p-gen-pos  (p-first-hit-in-first-n genesis "תורה" 10)
        p-exo-pos  (p-first-hit-in-first-n exodus "תורה" 10)
        p-lev      (p-at-least-one (expected-hits leviticus "יהוה" 7))
        p-num      (p-at-least-one (expected-hits numbers "תורה" -50))
        p-deu      (p-at-least-one (expected-hits deuteronomy "תורה" -50))
        p-compound (* p-gen-pos p-exo-pos p-lev p-num p-deu)]
    {:p-genesis-positional   p-gen-pos
     :p-exodus-positional    p-exo-pos
     :p-leviticus-any        p-lev
     :p-numbers-any          p-num
     :p-deuteronomy-any      p-deu
     :p-compound             p-compound
     :one-in                 (long (/ 1.0 p-compound))}))
