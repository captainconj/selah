---
name: oracle
description: Reference for the Selah breastplate oracle API. Use when querying the oracle, running basin walks, analyzing reader profiles, or doing experiments with the breastplate.
argument-hint: [function-or-topic]
---

# Breastplate Oracle — API Reference

The oracle lives in `src/selah/oracle.clj`. Basin dynamics in `src/selah/basin.clj`.

## Core Functions

### Reverse query: `o/ask`
**"Given a word (the answer), what does the oracle say about it?"**
```clojure
(require '[selah.oracle :as o])
(o/ask "שלום")
;; => {:word "שלום" :gv 376 :meaning "peace/wholeness"
;;     :illumination-count 126 :total-readings 35
;;     :by-reader {:aaron 0 :god 35 :right 0 :left 0}
;;     :anagrams [] :readable? true}
```
**USE THIS** for per-reader self-reading counts. Do NOT reconstruct this from `forward-by-head`.

### Forward query: `o/forward`
**"Given lit letters, what can the readers see?"**
```clojure
(o/forward "שלום")        ;; default :dict vocab (239 curated)
(o/forward "שלום" :torah) ;; full Torah vocab (~7,300)
;; => {:letters "שלום" :illumination-count 126 :total-readings 504
;;     :known-words [{:word ... :reading-count ... :known? true :gv ...}...]
;;     :unknown-words [...] :anagrams [...] :sample-readings {...}}
```
Returns ALL readings ranked by rarity. Known words sorted fewest-readings-first (Hannah principle).

### Forward by head: `o/forward-by-head`
**"Per-reader ranked word lists from these letters."**
```clojure
(o/forward-by-head "שלום")        ;; default :torah vocab
(o/forward-by-head "שלום" :dict)  ;; curated vocab
;; => {:aaron [...] :god [...] :right [...] :left [...]}
;; Each value is a vec of {:word :reading-count :known? :gv} sorted by reading-count desc
```
Shows what EACH reader sees from the same illumination. Different from `ask` — this shows all readings, not just the self-reading.

### Preimage: `o/preimage`
**"All (reader, position-set) pairs that produce this exact word."**
```clojure
(o/preimage "שלום")
;; => [{:reader :god :positions #{...} :stones [4 4 5 1]} ...]
```
Low-level. Returns the raw position-sets and stones per reader. Use `ask` instead unless you need spatial data.

### Illumination sets: `o/illumination-sets`
**"All ways the grid can light up to contain these letters."**
```clojure
(o/illumination-sets "שלום")
;; => [#{[s1 i1] [s2 i2] ...} ...]  ;; sets of [stone pos] pairs
```
Reader-independent. Same illumination, different readings per reader.

### Level 2 Thummim: `o/thummim` / `o/thummim-menu` / `o/parse-letters`
**"What phrases can be assembled from these illuminated letters?"**
```clojure
;; Full thummim (illumination-by-illumination)
(o/thummim "כבש" {:vocab :torah :max-words 3})

;; Just the deduplicated menu
(o/thummim-menu "כבש" {:vocab :torah})

;; Pure letter parsing (no grid positions needed)
(o/parse-letters "כבש" {:vocab :torah :max-words 3})
```

### Phrase ranking: `o/rank-phrases`
**"Rank phrases by reader agreement tier."**
```clojure
(o/rank-phrases (o/parse-letters "שלום") "שלום")
;; Tiers: 0=SELF, 1=UNANIMOUS, 2=MAJORITY, 3=SPLIT, 4=SOLO, 5=SILENT
```

## Basin Dynamics (`selah.basin`)

### Basin walk: `b/walk`
**"Feed oracle output back as input. Where does it converge?"**
```clojure
(require '[selah.basin :as b])
(b/walk "שלום")
;; => {:start "שלום" :steps [...] :converged? true :fixed-point "שלום" :cycle? false}
```
Empirical result: every basin has depth 1. Every basin = anagram class.

### Basin step: `b/step`
**"One step of the basin dynamics."**
```clojure
(b/step "שלום")
;; => "שלום"  ;; fixed point — maps to itself
```

### Full landscape: `b/landscape`
**"Classify every Torah word."**
```clojure
(b/landscape)  ;; expensive — runs full sweep
```

### Per-head basin: `b/step-all-heads` / `b/landscape-by-head`
**"Basin dynamics per reader — each reader has their own fixed points."**
```clojure
(b/step-all-heads "כבש")
;; => {:aaron "שכב" :god "כבש" :right "כבש" :left "שכב"}
;; Lamb: God sees lamb, Aaron sees "lie down" — same letters, different reader
```

## Four Readers

| Reader | Traversal | YHWH letter | Value |
|--------|-----------|-------------|-------|
| Aaron | rows R→L, top→bottom | Vav (ו) | 6 |
| God | rows L→R, bottom→top | He (ה) | 5 |
| Right cherub | columns R→L, top→bottom | Yod (י) | 10 |
| Left cherub | columns L→R, bottom→top | He (ה) | 5 |

10 + 5 + 6 + 5 = 26 = YHWH.

## Vocabulary Options

- `:dict` — 239 curated words (default for `forward`, `parse-letters`)
- `:torah` — ~7,300 oracle-closed Torah forms (default for `forward-by-head`)
- `:voice` — ~2,050 words at oracle output knee
- Custom set — `(o/forward "שלום" #{"שלום" "ושלם" "לשום"})`

## Common Patterns

### Check a word's reader profile
```clojure
(let [a (o/ask "שלום")]
  (:by-reader a))
;; => {:aaron 0 :god 35 :right 0 :left 0}
```

### Sweep all Torah words
```clojure
(let [words (vec (d/torah-words))
      results (pmap #(select-keys (o/ask %) [:word :gv :by-reader :total-readings]) words)]
  ;; filter, classify, etc.
  )
```

### Find God-exclusive words
```clojure
(filter (fn [{:keys [by-reader total-readings]}]
          (and (pos? total-readings)
               (pos? (:god by-reader))
               (zero? (:aaron by-reader))
               (zero? (:right by-reader))
               (zero? (:left by-reader))))
        results)
```

### Check what a word's anagram partners read as
```clojure
(o/forward-by-head "כבש")
;; Shows all readings from lamb's letters — including שכב (lie down) per reader
```

## Key Findings (settled)

- **Peace (שלום)**: 35 readings, ALL from God. Fixed point. GOD-ONLY.
- **Torah (תורה)**: 14 readings, all Right cherub. RIGHT-ONLY.
- **Life (חיים)**: 10 readings, all Right cherub. RIGHT-ONLY.
- **Holy (קדוש)**: 72 readings, 70 Aaron + 2 Right. AARON-dominant.
- **Lamb (כבש)**: God + Right see כבש. Aaron + Left see שכב (lie down).
- **YHWH (יהוה)**: All four readers. Fixed point only for God.
- **Grace (חסד)**: GHOST. GV=72 = breastplate letter count. Cannot name itself.
- **Love (אהבה)**: Not in Torah vocabulary. Silent axis.
- **Understanding (בינה)**: Not in Torah vocabulary. Silent axis.
- **Face (פנים)**: GHOST. 22 illuminations, zero readings.
- **Mercy seat (כפרת)**: GHOST. GV=700 = veil.
