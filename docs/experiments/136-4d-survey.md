
# Experiment 136 (4D): The Canonical Dimension

*41 hyperboxes. One of them is the canonical space. How does it compare?*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/136_4d_survey.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.136-4d-survey :as s]) (s/run-all)"`
**Data:** `data/experiments/136-4d-survey.edn`

## What is a 4D decomposition?

Arrange the 304,850 letters in a four-dimensional hyperbox: a × b × c × d. Each letter gets a four-coordinate address. There are **41 non-trivial 4D decompositions**.

The canonical space **7 × 50 × 13 × 67** lives here. It is the only 4D decomposition whose axes include all three of {7, 13, 67}. Its axis sum is **137 = 1/α** (the fine structure constant), unique among all 134 decompositions across all dimensions.

This survey measures every 4D space on the same battery of tests to see what makes the canonical special — and what doesn't.

## Centers

### The two main attractors

The 41 centers split primarily by the first axis value:

| First axis | # spaces | Center range |
|-----------|----------|-------------|
| **2** | **16** | **Numbers 23:22** — Balaam's oracle |
| 5 | 18 | **Leviticus 8:29–11:2** |
| 7 | 5 | **Leviticus 8:29–10:7** |
| 10 | 1 | **Leviticus 16:29** — Day of Atonement |
| 13 | 1 | **Leviticus 9:7** |

**16 of 41** spaces center on Numbers 23:22, all with first axis = 2. These all share center position 228,637 because the witness axis center is 1 (= 2÷2), placing the center in the second half.

**12 of 41** spaces center on Leviticus 8:29 specifically — the largest cluster. These are the spaces where **all axes are odd**, so the center is exactly N/2 = 152,425. When any axis is even (10, 14, 26, 50...), the center shifts outward from the ordination.

### Center convergence

| Verse | # spaces |
|-------|---------|
| **Numbers 23:22** | **16** |
| **Leviticus 8:29** | **12** |
| Leviticus 10:7 | 3 |
| Leviticus 8:30 | 2 |
| Leviticus 8:35 | 2 |
| Leviticus 11:2 | 2 |
| Leviticus 8:31, 9:7, 9:13, 16:29 | 1 each |

### The canonical center

**7 × 50 × 13 × 67** (the canonical ordering) centers at **(3, 25, 6, 33)** → position 152,860 → **Leviticus 8:35** — "Guard the charge of the LORD, that you die not."

This is the center we have always reported. But there is a subtlety: the survey lists decompositions in **sorted order** (a ≤ b ≤ c ≤ d). In sorted order, the canonical axes become **7 × 13 × 50 × 67**, which gives center **(3, 6, 25, 33)** → position 152,424 → **Leviticus 8:29**.

**The canonical center Lev 8:35 is axis-ORDER dependent, not just axis-VALUE dependent.** The ordering [7, 50, 13, 67] was chosen deliberately — it places jubilee (50) as the second axis and love (13) as the third, which shifts the center by exactly one love×understanding stride (436 positions). The sorted version [7, 13, 50, 67] gives Lev 8:29 — still the ordination, but not the climactic command.

### Other notable 4D centers

- **5 × 5 × 14 × 871** and **5 × 7 × 10 × 871** → **Leviticus 8:35** — the canonical center appears even without the canonical axis set. What these spaces share: 871 = 13 × 67 as the last axis. The even interior axis (14 or 10) shifts the center from 8:29 to 8:35.
- **7 × 10 × 13 × 335** and **7 × 10 × 65 × 67** → **Leviticus 10:7** — "Do not leave the tent of meeting." The 10-axis inside a 7-prefix pushes here.
- **10 × 13 × 35 × 67** → **Leviticus 16:29** — Day of Atonement. The 10-axis as first axis targets Yom Kippur.
- **13 × 14 × 25 × 67** → **Leviticus 9:7** — "Make atonement for yourself and for the people." The only 13-first-axis space; the even 14-axis shifts it from 8:29.

## Walk Smoothness

### The ranking

We measured mean Manhattan distance between consecutive letters for all 41 spaces. Lower = smoother.

**10 spaces achieve perfectly smooth walks** (mean = 1.0, max = 1):

| Space | Named axes |
|-------|-----------|
| 5 × 5 × 7 × 1742 | He², completeness |
| 5 × 5 × 13 × 938 | He², love |
| 5 × 5 × 67 × 182 | He², understanding |
| 5 × 5 × 91 × 134 | He², angel |
| 5 × 7 × 13 × 670 | He, completeness, love |
| 5 × 7 × 65 × 134 | He, completeness |
| 5 × 7 × 67 × 130 | He, completeness, understanding |
| 5 × 13 × 35 × 134 | He, love |
| 5 × 13 × 67 × 70 | He, love, understanding |
| 7 × 13 × 25 × 134 | completeness, love |

### Where is the canonical?

**7 × 50 × 13 × 67 has mean = 2.0, median = 1, max = 67, step1 = 98.5%.**

It ranks **13th of 41** on smoothness. 98.5% of consecutive letter pairs are Manhattan-adjacent (distance 1). The remaining 1.5% jump by at most 67 positions — they step along the understanding axis when wrapping from one love-slice to the next.

The canonical is not the smoothest walk, but it is among the smoothest of the spaces that keep all four theological axes separate. The spaces that beat it on smoothness tend to have fewer meaningful axes (many have 134 or 670 as an axis — composite numbers without clean theological readings).

### What determines smoothness?

A space has mean = 1.0 when the product of all axes except the last divides evenly into the stride pattern. The key: the last axis must be large enough to absorb the grid structure. The canonical's last axis (67) is moderate — large enough for good flow, small enough for meaningful structure.

## Fold Analysis

The fold point (letter 152,425) in each 4D space:

### Spaces with first axis = 2

All 16 fold to **[1, 0, 0, 0]**. The fold cleanly separates the two halves of the witness axis.

### The canonical fold

**7 × 50 × 13 × 67** → fold = **[3, 25, 0, 0]**

The fold of letter 152,425 decomposes via the canonical strides:
- 152,425 ÷ 43,550 (stride-a) = **3** remainder 21,775
- 21,775 ÷ 871 (stride-b) = **25** remainder 0
- 0 ÷ 67 (stride-c) = **0** remainder 0
- remainder = **0**

Reading the fold coordinates through the axes:
- **a = 3**: the center of completeness (day 4 of 7)
- **b = 25**: the center of jubilee (position 25 of 0–49)
- **c = 0**: the start of love
- **d = 0**: the start of understanding

The fold lands at the center of the two "loud" axes (completeness and jubilee) and at zero on both "silent" axes (love and understanding). **The silent axes are at their origin at the fold point.**

### Notable 4D fold coordinates

| Space | Fold | Observation |
|-------|------|------------|
| 5 × 5 × 91 × 134 | [2, 2, 45, **67**] | Last coordinate = understanding |
| 5 × 7 × 65 × 134 | [2, 3, 32, **67**] | Last coordinate = understanding |
| 5 × 13 × 35 × 134 | [2, 6, 17, **67**] | Last coordinate = understanding |
| 7 × 13 × 25 × 134 | [3, 6, 12, **67**] | Last coordinate = understanding |
| 5 × 13 × 67 × 70 | [2, 6, 33, **35**] | Last = 7 × 5 |
| 5 × 7 × 67 × 130 | [2, 3, 33, **65**] | Last = 5 × 13 |

All spaces with last axis = 134 (= 2 × 67) have their fold's last coordinate equal to exactly **67**. The fold always bisects the last axis when its value is exactly 2× the coordinate, which means 134-axis spaces have understanding at their fold.

## Aleph-Tav Fold Pairs

6,032 aleph-tav markers, each paired with its fold partner. For each pair, we count how many share each coordinate.

### The canonical space

**7 × 50 × 13 × 67:**
- Axis a (completeness, 7): **1,222 pairs share** = 20.3%
- Axis b (jubilee, 50): **510 pairs share** = 8.5%
- Axis c (love, 13): **0 pairs share** = 0.0%
- Axis d (understanding, 67): **100 pairs share** = 1.7%

**Love shows zero sharing in the canonical.** When the Torah is folded in half, no aleph-tav and its partner share the same love coordinate. This is striking — the love axis perfectly separates fold partners.

### The sharing hierarchy

Across all 41 spaces, a consistent pattern emerges. The sharing percentage for any axis of value k:

| Axis value | Sharing % | Random expectation |
|-----------|-----------|-------------------|
| 5 | ~26% | 20% |
| 7 | ~20% | 14% |
| 13 | ~10% | 8% |
| 25 | ~4% | 4% |
| 50 | 0% | 2% |
| 67 | ~1.7% | 1.5% |

The theological primes (5, 7, 13) consistently exceed random expectation. The jubilee axis (50) shows **zero sharing** whenever it appears — no aleph-tav pair shares a jubilee coordinate at the fold. Understanding (67) hovers near random.

### The zero-sharing axes

In the canonical, love (c=13) shows 0% sharing. But looking across all spaces:
- **Jubilee (50)** shows 0% sharing in every space where it appears as an axis
- **Love (13)** shows 0% sharing only in the canonical (in other spaces with a 13-axis, it shows ~7–10%)

The canonical's love axis is uniquely silent at the fold. In other 4D spaces that contain a 13-axis, aleph-tav pairs do share the love coordinate — just not in the one space that also has 7, 50, and 67.

## ELS at Stride Values

### The canonical's ELS

For **7 × 50 × 13 × 67**, the natural skips are the axis values (7, 50, 13, 67) and the strides (43,550; 871; 67; 1):

| Skip | Torah | YHWH | What the skip means |
|------|-------|------|-------------------|
| 7 | 20 | 51 | walk along completeness |
| 13 | 17 | 53 | walk along love |
| **50** | **34** | **50** | **walk along jubilee — Torah peak** |
| **67** | 14 | **60** | **walk along understanding — high YHWH** |
| **871** | **20** | **44** | **walk one love×understanding plane** |
| 3,350 | 19 | 58 | stride from 7×13×25×134 space |

### Comparing skip-50 across spaces

Skip-50 (jubilee) produces 34 Torah ELS — the highest count for any theological axis value. This is consistent across all spaces that have 50 as a natural skip. But **only spaces with 50 as an axis** "explain" this skip as a coordinate walk. In the canonical, skip-50 literally means "walk one step along the jubilee axis."

### The 8,710 anomaly

Several spaces that include a 5 × 7 prefix have stride 8,710 (= 2 × 5 × 13 × 67). At this skip: torah=**23**, yhwh=**65**. This is the highest YHWH count we see at any stride value, and it appears in the 5D canonical refinement of the 4D canonical.

## Summary: What makes 7 × 50 × 13 × 67 special?

1. **Unique axis set**: the only 4D space with {7, 13, 67}
2. **Axis sum = 137**: unique across all 134 decompositions in all dimensions
3. **Center**: Leviticus 8:35 — "Guard the charge of the LORD" — but only in the canonical ordering [7, 50, 13, 67]. The sorted order [7, 13, 50, 67] gives Lev 8:29. The center is order-dependent.
4. **Love is fold-silent**: zero aleph-tav pairs share the love coordinate at the fold — unique to the canonical
5. **Smooth enough**: rank 13/41, 98.5% of steps are Manhattan-adjacent
6. **Rich ELS**: skip-50 = Torah peak, skip-67 = YHWH concentration

The canonical is not the smoothest walk or the most symmetric grid. What it is: the only space where every axis carries irreducible theological meaning AND the mathematics (sum=137, fold silence, unique axis set) aligns.

---

Script: `dev/experiments/136_4d_survey.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
