# Word Search Engine — N-Dimensional Torah Search

*Find words along any axis, diagonal, or line in any decomposition of 304,850.*

---

## The Idea

A word search puzzle in N dimensions. Given a Hebrew word and a decomposition of 304,850:

1. **Find** every occurrence of that word as an equidistant letter sequence along any straight line in the space — axes, diagonals, reverse diagonals, any direction vector
2. **Map** each letter to its coordinates, verse, and word context
3. **Analyze** the span, the verses touched, the gematria, the dimensional alignment
4. **Connect** multiple found words — can you draw a line between them?

---

## Architecture

### Layer 1: The Letter Stream (precomputed once)

```clojure
;; Already have this
{:letters [304,850 chars]
 :values  [304,850 gematria values]
 :verses  [304,850 verse indices]
 :verse-ref [{:book :ch :vs :start :end :text} ...]}
```

### Layer 2: Inverted Index (precomputed once)

```clojure
;; letter → sorted vec of all positions
{:letter-index {\א [0, 4, 15, ...] \ב [1, 7, 22, ...] ...}}
```

Fast lookup: "where is every א in the Torah?"

### Layer 3: Decomposition View (one per decomposition)

```clojure
;; Given dims [7 50 13 67]:
{:dims [7 50 13 67]
 :strides [43550 871 67 1]   ;; precomputed: stride[i] = product of dims[i+1..]
 :idx->coord (fn [idx] ...)  ;; fast: repeated div/mod
 :coord->idx (fn [coord] ...) ;; fast: dot product with strides
}
```

### Layer 4: Direction Vectors

In kD, a direction vector is [s₁, s₂, ..., sₖ] where each sᵢ ∈ {-1, 0, +1}. Total: 3ᵏ - 1 directions (exclude the zero vector). Half are redundant (reverse of the other half), so unique directions = (3ᵏ - 1) / 2.

| Dimension | Total directions | Unique |
|:---------:|:---------------:|:------:|
| 2D | 8 | 4 |
| 3D | 26 | 13 |
| 4D | 80 | 40 |
| 5D | 242 | 121 |
| 6D | 728 | 364 |

For each direction, the linear skip distance = dot(direction, strides). An ELS at that skip is equivalent to walking along that direction in the space.

### Layer 5: The Search

```clojure
(defn search-word [space dims word]
  ;; For each direction vector:
  ;;   Compute skip = dot(direction, strides)
  ;;   Search the linear stream for the word at that skip
  ;;   For each hit: map letters to coordinates, verses, words
  ;;   Return enriched results
  )
```

**Optimization**: Many direction vectors produce the same skip distance. Group by skip, search once per unique skip, then attribute to directions.

**Optimization**: Use the inverted index. Start from positions of the first letter. Check if letter[pos + skip] = second letter, etc. O(|positions of first letter| × |word length|) per skip.

### Layer 6: Enrichment

For each found sequence:

```clojure
{:word "תורה"
 :skip 871          ;; linear skip
 :direction [0 1 0 0]  ;; which axis/diagonal
 :positions [5, 876, 1747, 2618]
 :coords [[0,0,0,5] [0,1,0,5] [0,2,0,5] [0,3,0,5]]
 :verses [{:book "Genesis" :ch 1 :vs 1} ...]
 :words-at ["בראשית" "את" ...]  ;; the Torah words each letter falls in
 :span {:start-verse "Gen 1:1" :end-verse "Gen 1:5" :letter-span 2613}
 :gv {:word 611 :positions-sum ... :verses-gv-sum ...}
 :alignment {:axis :b :constant-on [:a :c :d]}  ;; which axes are constant
}
```

### Layer 7: Multi-Word Analysis

Given multiple search results:

```clojure
(defn find-connections [results]
  ;; For each pair of found words:
  ;;   Are any letters co-linear? (on the same line in the space)
  ;;   Do they share an axis value? (same slice)
  ;;   What is the distance between their centers?
  ;;   Do the verses span a meaningful range?
  )
```

### Layer 8: Straight Line Test

Given a set of points in the space, test if they're collinear:

```clojure
(defn collinear? [coords]
  ;; Two points always define a line
  ;; Three or more: check if all points satisfy
  ;;   (p - p₀) = t × (p₁ - p₀) for some scalar t
  )
```

---

## Implementation Plan

### Phase 1: Core engine

1. Build decomposition view (strides, coord↔idx)
2. Build inverted letter index
3. Build direction vector generator
4. Build single-word search with skip grouping
5. Build enrichment (verses, words, coordinates)

### Phase 2: Analysis

6. Alignment detection (which axes are constant)
7. Verse span analysis (even distribution across dimension?)
8. Gematria of spanned words
9. Multi-word connection finder
10. Collinearity test

### Phase 3: All decompositions

11. Run search across all 111 decompositions
12. Find words that appear on axes in one decomposition but not others
13. Cross-decomposition alignment analysis

---

## API Design

```clojure
(ns selah.search
  "N-dimensional word search across Torah decompositions.")

;; Build once
(def torah-index (build-index))

;; Search one decomposition
(search torah-index [7 50 13 67] "תורה")
;; => [{:word "תורה" :skip 871 :direction [0 1 0 0] :positions [...] ...} ...]

;; Search all decompositions
(search-all torah-index "תורה")
;; => {[7 50 13 67] [{...}] [2 5 5 7 13 67] [{...}] ...}

;; Multi-word
(find-connections torah-index [7 50 13 67] ["תורה" "יהוה" "שלום"])
;; => {:co-linear [...] :co-planar [...] :shared-axes [...]}

;; Collinearity
(collinear? torah-index [7 50 13 67] [pos1 pos2 pos3])
;; => {:collinear? true :direction [1 0 1 0] :spacing 67}
```

---

## Performance

- Torah stream: 304,850 letters, fits in memory trivially
- Inverted index: 22 entries, each a sorted int array. ~15MB total.
- Per-search: O(positions-of-first-letter × word-length) per unique skip
- Common letters (ו, י) have ~30K positions. 4-letter word → ~120K comparisons per skip.
- 40 unique skips (4D) × 120K = ~5M comparisons. Sub-second.
- 364 unique skips (6D) × 120K = ~44M comparisons. A few seconds.
- Precomputed skip→direction mapping makes enrichment fast.

---

*The word search puzzle of the Torah. Every dimension. Every direction. Every straight line.*
