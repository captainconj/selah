# Corner Survey Plan

*Every decomposition of 304,850. Every corner. What does the Torah say at the edges of each box?*

---

## The Question

304,850 = 2 × 5² × 7 × 13 × 67. This number factors into 111 decompositions across 3-6 dimensions. Each decomposition defines a different box. Each box has corners. Each corner lands on a specific verse.

We know one decomposition intimately: 7 × 50 × 13 × 67. Its center is Leviticus 8:35 ("guard the charge"). Its geometry produced the 4D space, the breastplate, the courtroom, 141 experiments.

What about the other 110? Do their corners land on significant verses? Do multiple decompositions share corner verses? Is there a verse that appears at a corner in MANY decompositions?

## The Scope

| Dim | Decompositions | Corners each | Total |
|:---:|:--------------:|:------------:|------:|
| 3D | 58 | 8 | 464 |
| 4D | 41 | 16 | 656 |
| 5D | 11 | 32 | 352 |
| 6D | 1 | 64 | 64 |
| **Total** | **111** | | **1,536** |

1,536 corner positions to look up. Many will be duplicates (different decompositions share the positions 0 and 304,849). The unique set will be smaller.

## The Approach

### Phase 1: Build the survey engine

A single function that takes a decomposition (e.g., [7, 50, 13, 67]) and returns:
- All 2^k corners with their verse references
- The center with its verse reference
- The axis sums, products, and notable GV matches

### Phase 2: Run all 111 decompositions

For each: corners, center. Store as data.

### Phase 3: Analyze

- **Corner frequency:** Which verses appear most often as corners across all decompositions?
- **Center convergence:** How many decompositions share the same center verse? (Many will — the linear midpoint is always ~152,425 = Leviticus 8:8, the breastplate installation)
- **Thematic clusters:** Do theologically significant verses (Gen 1:1, Lev 8:35, Deut 6:4, etc.) appear at corners?
- **The 6D corners:** 64 corners of the unique 6D decomposition. This is the finest granularity. What does the Torah say at every vertex of its most detailed box?

### Phase 4: Notable decompositions

Some axis sums are interesting:

| Decomposition | Sum | Notable? |
|--------------|----:|----------|
| 7 × 50 × 13 × 67 | **137** | 1/α = fine structure constant. Our canonical. |
| 2 × 35 × 65 × 67 | **169** | 13² = love squared |
| 5 × 7 × 13 × 670 | 695 | — |
| 13 × 67 × 350 | **430** | = נפש (soul) |
| 7 × 13 × 50 × 67 | **137** | Same as canonical, different ordering |
| 2 × 5 × 5 × 7 × 13 × 67 | **99** | The only 6D. 99 = ? |
| 50 × 67 × 91 | **208** | = יצחק (Isaac) |
| 7 × 67 × 650 | **724** | — |

### Phase 5: Write up

- Per-dimension survey docs (like we did for Isaiah)
- Corner frequency analysis
- The 6D document — the 64 vertices of the most granular Torah box

## Estimated Work

- Engine: 1 experiment script (~100 lines)
- Survey: automated, ~5 min runtime
- Analysis: 1-2 hours
- Write-up: per-dimension docs

## Priority

1. **6D first** — only one decomposition, 64 corners. The unique view.
2. **4D canonical** — we know the center, never mapped all 16 corners
3. **3D/4D interesting sums** — 137, 169, 208, 430
4. **Full survey** — all 111

---

*304,850 = 2 × 5² × 7 × 13 × 67. 111 boxes. 1,536 corners. What does the Torah say at the edges?*
