# Experiment 136 (3D): All the Boxes

*58 ways to put the Torah in a box.*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/136_3d_survey.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.136-3d-survey :as s]) (s/run-all)"`
**Data:** `data/experiments/136-3d-survey.edn`

## What is a 3D decomposition?

Arrange the 304,850 letters in a rectangular box: length × width × height. Each letter gets a three-coordinate address (x, y, z). There are **58 non-trivial 3D decompositions** — the most of any dimension. This is where the decomposition space peaks.

Notable boxes:
- **7 × 50 × 871** — the canonical 4D space with love×understanding fused into one axis
- **13 × 67 × 350** — the two silent axes get their own dimensions
- **50 × 67 × 91** — jubilee × understanding × angel
- **65 × 67 × 70** — the near-cube (all axes within 8% of ∛304,850 ≈ 67.3)

## Centers

### Grouped by first axis

| First axis | # of spaces | Center range |
|-----------|-------------|-------------|
| 2 | 11 | **Numbers 23:22** — Balaam's oracle |
| 5 | 14 | **Leviticus 8:29–11:2** — ordination and beyond |
| 7 | 9 | **Leviticus 8:29–10:7** |
| 10 | 5 | **Leviticus 16:29** — Day of Atonement |
| 13 | 7 | **Leviticus 8:29–9:7** |
| 14 | 3 | **Leviticus 14:37** — examining leprosy |
| 25–65+ | 9 | **Leviticus 8:29–11:2** |

### The rule: all-odd axes → Lev 8:29

**When every axis in the box is odd, the center lands exactly at position 152,425 → Leviticus 8:29.** This is because odd axes yield center coordinates that sum to exactly N/2.

**When any axis is even, the center shifts** — the even axis contributes an extra half-stride that pushes the center past the midpoint. Larger even axes produce smaller shifts. The 11 spaces with first axis = 2 shift the most (to Numbers 23:22), while spaces with even interior axes shift less (staying in Leviticus but drifting from 8:29 to 8:30, 8:31, 9:7, 9:13, 10:7, 11:2, or 13:5).

The five spaces with first axis = 10 all center on **Leviticus 16:29** — the Day of Atonement. The three spaces with first axis = 14 all center on **Leviticus 14:37** — examining leprosy.

### Verse convergence

| Verse | # of spaces |
|-------|------------|
| **Leviticus 8:29** | **19** |
| **Numbers 23:22** | **11** |
| **Leviticus 16:29** | **5** |
| Leviticus 8:30 | 5 |
| Leviticus 8:31 | 4 |
| Leviticus 14:37 | 3 |
| Leviticus 8:35 | 2 |
| Leviticus 9:7 | 2 |
| Leviticus 10:7 | 2 |
| Leviticus 11:2 | 2 |
| Leviticus 13:5 | 2 |
| Leviticus 9:13 | 1 |

Leviticus 8:29 is still the dominant attractor — 19 of 58 spaces converge there. But the picture is richer than "everything centers on the ordination." The even-axis spaces fan out through Leviticus, tracing a path from the ordination through offerings (9:7, 9:13), purity (10:7, 11:2, 13:5), and the leprosy examination (14:37).

### Notable 3D centers

- **7 × 50 × 871** → [3, 25, 435] → **Leviticus 8:35** — "Guard the charge of the LORD." The canonical shadow in 3D. Same center verse as the canonical 4D. The last axis is 871 = 13 × 67, and center coordinate 435 = 871÷2.
- **5 × 70 × 871** → [2, 35, 435] → **Leviticus 8:35** — same canonical center, different prefix.
- **10 × 13 × 2345** and other 10-axis boxes → **Leviticus 16:29** — Day of Atonement.
- **14 × 25 × 871** → **Leviticus 14:37** — "He shall look at the plague in the walls of the house." The 14-axis consistently targets the leprosy examination.

## Walk Smoothness

How smoothly does the Torah flow through each box?

### Perfectly smooth boxes (mean = 1.0)

These boxes have the property that consecutive letters are always Manhattan-adjacent — no jumps at all:

| Box | Properties |
|-----|-----------|
| 5 × 5 × 12,194 | two He axes |
| 5 × 7 × 8,710 | He × completeness |
| 5 × 13 × 4,690 | He × love |
| 5 × 67 × 910 | He × understanding |
| 7 × 13 × 3,350 | completeness × love |
| 7 × 67 × 650 | completeness × understanding |
| 13 × 67 × 350 | love × understanding |

There are 19 perfectly smooth 3D boxes total. The pattern: when the first two axes multiply to a factor of the third axis's stride, the walk never jumps.

### Roughest boxes

| Box | Mean | Max jump |
|-----|------|----------|
| 2 × 7 × 21,775 | 2.3 | 21,781 |
| 2 × 13 × 11,725 | 2.2 | 11,737 |
| 2 × 25 × 6,097 | 2.1 | 6,121 |

Boxes with a 2-axis are always rougher — you jump across a plane boundary every other letter.

## Fold Coordinates

The fold point (letter 152,425) in each box:

### Boxes with first axis = 2
All fold to **[1, 0, 0]** — the fold happens at the start of the second "slab." The entire first half of the Torah fills one slab, the second half fills the other.

### Boxes with first axis = 5
Fold to **[2, ?, ?]** — the fold happens in the middle slab (slab 2 of 0–4).

### Boxes with first axis = 7
Fold to **[3, ?, ?]** — the center slab. Examples:
- 7 × 50 × 871 → fold = [3, **25**, 0] — fold at jubilee center, understanding = 0
- 7 × 67 × 650 → fold = [3, **33**, 325] — fold at understanding center, column 325 = 5² × 13

### Notable fold coordinates

| Box | Fold | What the coordinates say |
|-----|------|------------------------|
| 7 × 50 × 871 | [3, 25, 0] | center of completeness, center of jubilee, beginning of love×understanding |
| 13 × 67 × 350 | [6, 33, **175**] | center of love, center of understanding, column = Abraham's lifespan |
| 25 × 67 × 182 | [12, 33, **91**] | center of jubilee², center of understanding, column = angel (מלאך) |
| 25 × 91 × 134 | [12, 45, **67**] | center of jubilee², middle of angel, column = understanding |
| 35 × 65 × 134 | [17, 32, **67**] | 7×5 center, 5×13 center, column = understanding |
| 35 × 67 × 130 | [17, 33, **65**] | 7×5 center, understanding center, column = 5 × 13 |
| 65 × 67 × 70 | [32, 33, **35**] | near-cube: each coordinate is the center of its axis, last = 7 × 5 |

The fold coordinate of the last axis in these boxes is consistently a theologically meaningful number — it inherits meaning from the factorization.

## Aleph-Tav Fold Pairs

6,032 aleph-tavs. For each, we check if the fold partner shares any of the three coordinates.

### Sharing by axis value

The sharing percentage for the first axis follows a clear pattern:

| Axis value | Sharing % | Expected (1/value) |
|-----------|-----------|-------------------|
| 5 | **26.2%** | 20.0% |
| 7 | **20.3%** | 14.3% |
| 13 | **10.4%** | 7.7% |
| 25 | **5.8%** | 4.0% |
| 35 | **4.6%** | 2.9% |
| 65 | **2.9%** | 1.5% |
| 67 | **2.9%** | 1.5% |

**The sharing is always higher than random expectation** (1/axis-value). The aleph-tavs are not uniformly distributed — they are clustered so that fold partners preferentially share coordinates. The excess over random is strongest for the theological axes (5, 7, 13).

### Second axis sharing

The second axis shows sharing only when both it and the first axis are small enough. For 5 × 7 × 8,710, the second axis (7) shows 15.4% sharing. For 5 × 5 × 12,194, it shows 20.7%.

The third axis (always the largest) shows near-zero sharing — too many possible values for coincidence.

## ELS at Stride Values

Each box has two stride values (products of subsequent axes). For a box a × b × c, stride₁ = b × c and stride₂ = c.

### Consistent ELS counts (dimension-independent)

These counts appear everywhere because they depend only on the skip value, not the grid:

| Skip | Torah | YHWH | Meaning |
|------|-------|------|---------|
| 5 | 18 | 52 | breath |
| 7 | 20 | 51 | completeness |
| 13 | 17 | 53 | love |
| **50** | **34** | **50** | **jubilee — Torah peak** |
| **67** | 14 | **60** | **understanding — high YHWH** |
| 91 | 21 | 50 | angel |
| **130** | **29** | **66** | **YHWH's absolute peak** |
| **134** | **27** | 56 | 2 × 67 |
| 182 | 23 | 58 | 2 × 7 × 13 |

### Notable stride-value ELS

- **8,710** (stride in 5 × 7 × 8,710 and 35 × 65 × 134): torah=**23**, yhwh=**65** — unusually high for a large skip
- **4,355** (stride in 7 × 10 × 4,355): torah=14, yhwh=**61** — YHWH concentrated at the completeness×yod stride

---

Script: `dev/experiments/136_3d_survey.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
