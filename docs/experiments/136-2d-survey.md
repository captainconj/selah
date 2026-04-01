# Experiment 136 (2D): Flat Grids

*Every way to lay the Torah flat on a table.*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/136_2d_survey.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.136-2d-survey :as s]) (s/run-all)"`
**Data:** `data/experiments/136-2d-survey.edn`

## What is a 2D decomposition?

Take all 304,850 letters and arrange them in a grid — rows and columns. A 2D decomposition is a pair of numbers (a × b) where a × b = 304,850 and both a and b are greater than 1. These are the divisor pairs.

There are **23 non-trivial 2D decompositions**, from 2 × 152,425 (two very long rows) to 469 × 650 (a nearly square grid).

## What we measured

For each of the 23 grids, we computed:

1. **Center verse** — the letter at the geometric center of the grid, and what verse it falls in
2. **Walk smoothness** — when you read the Torah in order, how far does each consecutive letter jump in the grid? Lower mean = the text flows more naturally through that layout
3. **Fold coordinates** — if you fold the Torah in half (letter 152,425), where does that fold land in the grid?
4. **Torah/YHWH ELS** — equidistant letter sequences for תורה and יהוה at each axis value and stride value
5. **Aleph-tav fold pairs** — the 6,032 aleph-tav (את) markers: when you fold the text, do their partners share a row or column?

## Centers

The center of each grid is computed as (a÷2, b÷2) using integer division.

| Grid | Center | Verse |
|------|--------|-------|
| 2 × 152,425 | [1, 76212] | Numbers 23:22 |
| 5 × 60,970 | [2, 30485] | Leviticus 8:29 |
| 7 × 43,550 | [3, 21775] | Leviticus 8:29 |
| 10 × 30,485 | [5, 15242] | Leviticus 16:29 |
| 13 × 23,450 | [6, 11725] | Leviticus 8:29 |
| 14 × 21,775 | [7, 10887] | Leviticus 14:37 |
| 25 × 12,194 | [12, 6097] | Leviticus 8:29 |
| 26 × 11,725 | [13, 5862] | Leviticus 13:5 |
| 35 × 8,710 | [17, 4355] | Leviticus 8:29 |
| 50 × 6,097 | [25, 3048] | Leviticus 11:2 |
| 65 × 4,690 | [32, 2345] | Leviticus 8:29 |
| 67 × 4,550 | [33, 2275] | Leviticus 8:29 |
| 70 × 4,355 | [35, 2177] | Leviticus 10:7 |
| 91 × 3,350 | [45, 1675] | Leviticus 8:29 |
| 130 × 2,345 | [65, 1172] | Leviticus 9:13 |
| 134 × 2,275 | [67, 1137] | Leviticus 9:12 |
| 175 × 1,742 | [87, 871] | Leviticus 8:29 |
| 182 × 1,675 | [91, 837] | Leviticus 9:7 |
| 325 × 938 | [162, 469] | Leviticus 8:29 |
| 335 × 910 | [167, 455] | Leviticus 8:29 |
| 350 × 871 | [175, 435] | **Leviticus 8:35** |
| 455 × 670 | [227, 335] | Leviticus 8:29 |
| 469 × 650 | [234, 325] | Leviticus 8:29 |

### What we see

The centers split into two clean groups based on whether the first axis is **odd or even**.

**Odd first axis → Leviticus 8:29.** Every grid with an odd first axis (5, 7, 13, 25, 35, 65, 67, 91, 175, 325, 335, 455, 469) centers on position 152,425 — the exact halfway point of the Torah. This position falls at **Leviticus 8:29**, the ordination of Aaron. **13 of 23 grids** center here. The math is clean: when the row count a is odd, the center row is (a−1)/2 and the center column is b/2, which always gives position N/2 = 152,425. The ordination is forced by the half-fold.

**Even first axis → the center shifts.** When the first axis is even, the center row is a/2, which pushes the center position to N/2 + b/2 — past the midpoint by half a column width. The further the center shifts, the further from the ordination it lands:

| Grid | Shift | Verse |
|------|-------|-------|
| 2 × 152,425 | +76,212 | Numbers 23:22 — Balaam's oracle |
| 10 × 30,485 | +15,242 | Leviticus 16:29 — Day of Atonement |
| 14 × 21,775 | +10,887 | Leviticus 14:37 — examining leprosy |
| 26 × 11,725 | +5,862 | Leviticus 13:5 — the priest examines |
| 50 × 6,097 | +3,048 | Leviticus 11:2 — clean and unclean |
| 70 × 4,355 | +2,177 | Leviticus 10:7 — do not leave the tent |
| 182 × 1,675 | +837 | Leviticus 9:7 — make atonement |
| 130 × 2,345 | +1,172 | Leviticus 9:13 — the burnt offering |
| 134 × 2,275 | +1,137 | Leviticus 9:12 — the blood |

The even-axis centers trace a path *outward* from the ordination — through offerings, examination, purity law, and finally to Balaam's oracle in Numbers. The larger the even axis, the smaller the shift and the closer the center stays to Lev 8.

**The exception: 350 × 871.** This grid has an even first axis (350) but its center is at position 152,860 → **Leviticus 8:35** — "Guard the charge of the LORD." This is the canonical center verse, appearing in 2D because 350 = 7 × 50 and 871 = 13 × 67. The shift of +435 lands exactly at the canonical center.

**Why Leviticus 8?** Because position 152,425 is the physical center of the Torah's 304,850 letters. It falls in the middle of the ordination narrative. Every odd-row grid hits it exactly. Even-row grids shift away from it, but most stay in Leviticus.

## Walk Smoothness

When you read the Torah letter by letter, each consecutive letter is position i and i+1 in the linear stream. In a 2D grid, those two positions might be next to each other (Manhattan distance 1) or they might jump to a new row (Manhattan distance = row width). We measured the average and maximum jump.

**Result: every 2D grid has median step = 1 and step1 = 100%.** In a 2D layout, consecutive letters are always column-adjacent except at row boundaries. The mean distance is dominated by how often you hit a row boundary and how far the jump is.

| Grid | Mean | Max jump |
|------|------|----------|
| 5 × 60,970 | **1.0** | 1 |
| 7 × 43,550 | **1.0** | 1 |
| 13 × 23,450 | **1.0** | 1 |
| 469 × 650 | **1.0** | 1 |
| ... (13 grids total at 1.0) | | |
| 10 × 30,485 | 1.6 | 30,485 |
| 2 × 152,425 | **4.0** | 152,425 |

Grids where the column count (second axis) divides 304,850 cleanly with the row count as a factor of the column count achieve perfectly smooth walks — every row wraps into the next without a jump. The roughest grid is 2 × 152,425: the text fills one row completely, then jumps 152,425 positions to start the second row.

## Fold Coordinates

The fold point (letter 152,425) lands at these grid coordinates:

- Grids starting with **2**: fold = [1, 0] — the fold is at the start of the second row. Clean.
- Grids starting with **even** axis: fold always has second coordinate = 0 (beginning of a row).
- Grids starting with **odd** axis: fold lands mid-row. For example, 7 × 43,550 → [3, 21775] (middle row, middle column).

Notable fold coordinates:
- **175 × 1,742** → fold = [87, **871**]. The fold's column coordinate is 871 = 13 × 67 (love × understanding).
- **325 × 938** → fold = [162, **469**]. Column = 469 = 7 × 67 (completeness × understanding).
- **335 × 910** → fold = [167, **455**]. Column = 455 = 5 × 7 × 13 (He × completeness × love).
- **455 × 670** → fold = [227, **335**]. Column = 335 = 5 × 67 (He × understanding).
- **469 × 650** → fold = [234, **325**]. Column = 325 = 5² × 13 (He² × love).

The fold coordinate is always N/2 decomposed via the grid dimensions. When those dimensions are theologically composite, the fold coordinate inherits their meaning.

## Aleph-Tav Fold Pairs

There are 6,032 aleph-tav (את) markers in the Torah. For each one at position p, its fold partner sits at position 304,849 - p. We ask: does the aleph-tav and its fold partner share a row (same first coordinate)?

| Grid | Row sharing % | Column sharing % |
|------|--------------|-----------------|
| 5 × 60,970 | **26.2%** | 0.0% |
| 7 × 43,550 | **20.3%** | 0.0% |
| 13 × 23,450 | **10.4%** | 0.0% |
| 25 × 12,194 | **5.8%** | 0.0% |
| 67 × 4,550 | **2.9%** | 0.0% |
| 2 × 152,425 | 0.0% | 0.0% |
| 10 × 30,485 | 0.0% | 0.0% |

**The pattern is clear and precise.** Aleph-tav fold pairs share the row coordinate at a rate that depends on the axis value. Smaller axes = higher sharing. The sharing rate for an axis of size k is approximately 1/k (expected if positions were uniformly distributed), but the exact counts suggest the aleph-tavs are not uniformly placed — they cluster.

Column sharing is essentially zero — the second axis is always too large for random coincidence.

## ELS at Stride Values

For each grid, we check how many times תורה (Torah) and יהוה (YHWH) appear as equidistant letter sequences at the skip distances defined by the grid's axes.

**Key findings across all 2D grids:**

| Skip | Torah hits | YHWH hits | Notes |
|------|-----------|-----------|-------|
| 2 | 46 | 20 | witness |
| 5 | 18 | 52 | He (breath) |
| 7 | 20 | 51 | completeness |
| 13 | 17 | 53 | love |
| **50** | **34** | **50** | **jubilee — Torah peaks here** |
| **67** | 14 | **60** | **understanding — YHWH peaks here** |
| **130** | **29** | **66** | **10×13 — YHWH's strongest** |

Skip-50 is the strongest Torah ELS among all axis values (34 occurrences). Skip-67 gives the highest YHWH count among prime-axis values (60). Skip-130 (= 10 × 13, Yod × love) is the overall YHWH peak at 66 occurrences.

These counts are the same regardless of which 2D grid you're in — ELS at a given skip is a property of the linear text, not the grid. The grid tells you which skips are "natural" for that layout.

---

Script: `dev/experiments/136_2d_survey.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
