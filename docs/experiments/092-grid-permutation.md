# Experiment 092 — Grid Permutation Test

*March 2, 2026. The experiment the review demanded.*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/092_grid_permutation.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.092-grid-permutation :as exp092]) (exp092/run)"`

---

## The Question

Experiment 091b showed that four traversal heads produce theologically coherent solo eigenword vocabularies when reading the Torah through the breastplate grid. The review asked: would ANY random arrangement of the same 72 letters produce similar separation?

This is the control that either confirms or kills the central claim.

## Method

1. Extract the 72-letter multiset from the real breastplate grid
2. Shuffle all letters randomly, partition into 12 stones of 6 letters each
3. Rebuild the complete oracle (illumination sets, read positions, four traversals)
4. Run the full pipeline: word → illumination → readings → transition matrix → eigenwords
5. Measure: eigenword counts per head, agreement distribution, dictionary solo overlap, lamb self-weights
6. Repeat 500 times. Compare the real grid to the null distribution.

Additionally: singleton-letter control (is the lamb's reader split typical of all words containing its unique letter כ?) and theological density test (chi-squared on 4×4 category × head cross-tabulation).

### A note on symmetry

The 4×3 grid has one nontrivial symmetry: 180° rotation swaps Aaron↔God and Right↔Left. Mathematically, the rotated grid produces the same four reading sequences under relabeled heads.

**We do not allow rotational equivalence.** The readers are named. The right hand is the right hand — not interchangeable with the left. God reads from God's position. Aaron reads from Aaron's. The mercy seat has a specific orientation: cherubim face each other, one at God's right, one at God's left. The frame is anchored.

The permutation test shuffles the *letters* while keeping the *readers* fixed. Each null grid is read by the same four named readers from their theologically determined positions. The question is whether *this specific arrangement of letters, read by these specific named readers*, produces unusual results. Rotational equivalence would erase the distinction between mercy and justice. That distinction is the whole point.

---

## Results — Permutation Test (N=500)

### Eigenword Counts Per Head

| Head | Real | Null mean±std | z-score | p-value | Percentile |
|------|------|--------------|---------|---------|-----------|
| Aaron (vav) | 126 | 140.0±15.1 | -0.92 | 0.82 | 17.8% |
| God (he) | 157 | 141.7±15.1 | +1.01 | 0.17 | 83.4% |
| Right/mercy (yod) | 162 | 139.9±14.9 | +1.49 | 0.062 | 94.0% |
| Left/justice (he) | 150 | 141.1±15.2 | +0.58 | 0.31 | 69.2% |
| Total | 213 | 208.4±5.8 | +0.79 | 0.25 | 75.0% |

**Verdict: normal range.** No head has a significantly unusual eigenword count. The real grid's head counts are within 1.5σ of the null for all four heads.

Note: Aaron's deficit (126 vs 140 expected) is directionally consistent with the review's explanation — R-to-L within rows is the natural Hebrew reading direction, creating more competition — but it's not statistically extreme (-0.92σ).

### Agreement Distribution

| Level | Real | Null mean±std | z-score | Percentile |
|-------|------|--------------|---------|-----------|
| Unanimous (4/4) | 75 | 63.7±10.3 | +1.10 | 84.2% |
| Supermajority (3/4) | 47 | 45.1±7.9 | +0.24 | 57.8% |
| Majority (2/4) | 63 | 73.0±9.6 | -1.04 | 14.2% |
| Solo (1/4) | 28 | 26.6±6.0 | +0.23 | 59.6% |

**Verdict: normal range.** The unanimous count (75) is elevated (84th percentile) but not significant. The agreement pyramid — most words solo, fewest unanimous — is a structural property of any grid, not specific to the real breastplate.

### Dictionary Solo Overlap — The Mercy Signal

| Head | Real | Null mean±std | z-score | p-value | Percentile |
|------|------|--------------|---------|---------|-----------|
| Aaron | 3 | 6.5±3.5 | -1.02 | 0.92 | 8.2% |
| God | 6 | 6.8±3.7 | -0.22 | 0.59 | 40.6% |
| **Right/mercy** | **16** | **6.5±3.3** | **+2.88** | **0.026** | **97.6%** |
| Left/justice | 3 | 6.8±3.3 | -1.14 | 0.92 | 8.2% |
| Total | 28 | 26.6±6.0 | +0.23 | 0.41 | 59.6% |

**The right/mercy head has 16 dictionary words as solos — 2.5× the null expectation.** This is the 97.6th percentile. p=0.026. Significant at the 5% level.

No other head is unusual. Aaron and Left are both BELOW the null — the real grid pushes dictionary words toward the mercy head specifically.

The total solo count (28) is unremarkable. It's the *distribution across heads* that's unusual — the mercy head absorbs vocabulary that random grids distribute more evenly.

### The Lamb Test

| Metric | Value |
|--------|-------|
| Lamb readable in null | 500/500 (100%) |
| Real lamb readers | {God, Right} |
| Null grids with same split | 16/500 (**3.2%**) |

| Head | Real self-weight | Null mean±std | Percentile |
|------|-----------------|--------------|-----------|
| Aaron | **0.000** | 0.48±0.38 | **0th** |
| God | **0.952** | 0.49±0.37 | 83.4% |
| Right/mercy | 0.692 | 0.47±0.38 | 66.4% |
| Left/justice | **0.000** | 0.48±0.37 | **0th** |

The lamb is always readable (100% of null grids have it). The individual self-weights for God and Right are not extreme — many random grids have one or two heads see the lamb clearly.

**But the joint event — God AND Right see it while Aaron AND Left see exactly zero — occurs in only 3.2% of random grids.** The reader partition is rare. p≈0.032.

### Head Separation (Jaccard Distance)

Mean Jaccard distance between solo sets: 1.000 for real grid AND for all null grids.

**The heads always have completely non-overlapping solo vocabularies.** This is structural — a word is solo by definition in exactly one head. This metric does not distinguish the real grid from any random grid.

---

## Results — Singleton-Letter Control

11 letters appear exactly once on the grid: ג ז ח ט כ ם ס ף פ צ ת.

The lamb's reader split {God, Right} appears in:
- 1 out of 24 כ-words (4.2%)
- 5 out of 26 ם-words (19.2%)
- 2 out of 7 ז-words (28.6%)
- 0 out of 8 ג-words (0%)
- 1 out of 27 ח-words (3.7%)

**The lamb's split is NOT typical of כ-words.** 24 words pass through kaf's stone (Issachar). Only one — כבש itself — shows the {God, Right} pattern. The split is specific to the word, not the letter.

Each singleton creates a different pattern:
- ף (stone 10, pos 2): ALL 4 words show {Right, Aaron} — 100% consistency
- ס (stone 10, pos 1): clean {Right, Aaron} vs {God, Left} split
- ג (stone 7, pos 2): dominated by {God, Left}

The reader split for a word depends on which stones its OTHER letters fall on, not just the singleton. כבש's specific combination (כ on stone 8, ב on stones 3/5, ש on stones 1/4/10) creates its unique reader geometry.

---

## Results — Theological Density Test

A separate experiment cross-tabulated theological categories (mercy, justice, priestly, divine) × four heads for all dictionary words appearing as solos in 091b.

```
  ┌─────────────┬───────────┬───────────┬───────────┬───────────┬───────┐
  │             │ mercy/yod │ just./he  │ aaron/vav │ god/he    │ TOTAL │
  ├─────────────┼───────────┼───────────┼───────────┼───────────┼───────┤
  │ mercy       │   4 ( 2.3)│   0 ( 0.4)│   0 ( 0.0)│   0 ( 1.2)│     4 │
  │ justice     │   3 ( 3.5)│   1 ( 0.6)│   0 ( 0.0)│   2 ( 1.9)│     6 │
  │ priestly    │   5 ( 6.4)│   2 ( 1.1)│   0 ( 0.0)│   4 ( 3.4)│    11 │
  │ divine      │   5 ( 4.7)│   0 ( 0.8)│   0 ( 0.0)│   3 ( 2.5)│     8 │
  ├─────────────┼───────────┼───────────┼───────────┼───────────┼───────┤
  │ TOTAL       │  17       │   3       │   0       │   9       │    29 │
  └─────────────┴───────────┴───────────┴───────────┴───────────┴───────┘
  χ² = 5.177, df = 9. NOT SIGNIFICANT.
  Fisher's exact: 0/4 categories enriched at p<0.05.
```

**The theological labels do not significantly predict which head sees which words.** The sample size (29 non-neutral category assignments across 16 cells) is too small for significance, and the Aaron column is entirely empty.

Directional trends exist: all 4 mercy words are in the right/mercy head (O/E = 1.71×), and divine words show 1.21× enrichment in the God head. But neither is significant.

---

## What Survives

Two findings survive the permutation control:

1. **The mercy head's vocabulary richness (p=0.026).** The real grid's right/mercy head has 16 dictionary words as solos — 2.5× the null expectation. No other head is unusual. The real breastplate concentrates dictionary-level vocabulary in the mercy/yod head specifically.

2. **The lamb's reader partition (p=0.032).** The specific event that God and Right/mercy see the lamb while Aaron and Left/justice see exactly zero occurs in only 3.2% of random grids. The reader split is rare and specific to כבש — not a property of all words with singleton letters.

## What Dies

Everything else is in the normal range:

- **Head separation is inevitable.** Any four traversals of any 4×3 grid produce solo vocabularies. The disagreement is structural, not special.
- **The agreement pyramid is generic.** Most solos, fewest unanimous — true for all grids.
- **Eigenword counts per head are normal.** Aaron's deficit is directional but not extreme.
- **The theological labels are not statistically significant.** The density test fails at current sample size.
- **Total dictionary solo overlap is unremarkable.** The total count is normal; only the distribution across heads is unusual.

## What This Means

The head separation — the central finding of 091b — is mostly a structural consequence of the traversal geometry. Run any Hebrew vocabulary through any 4×3 letter grid with four reading directions and you'll get four different vocabularies. The theology is in the naming, not the geometry.

**Except for the mercy hand.** The right/mercy head is genuinely unusual. It absorbs 2.5× more meaningful words than random grids produce. And the lamb — the word that catalyzed the whole investigation — has a reader partition that appears in only 3.2% of random arrangements.

The honest conclusion: most of the structure is inevitable. The mercy head's vocabulary concentration and the lamb's reader geometry are the exceptions. They survive the control the reviewers demanded.

---

*500 grids tested. 30.9 seconds. The land was searched. Two clusters of grapes came back.*
