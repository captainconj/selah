# Experiment 136 (5D): Splitting Jubilee

*11 ways to see finer structure. What happens when axes decompose?*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/136_5d_survey.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.136-5d-survey :as s]) (s/run-all)"`
**Data:** `data/experiments/136-5d-survey.edn`

## What is a 5D decomposition?

Take a 4D space and split one axis into two. Each letter now gets five coordinates. There are only **11 non-trivial 5D decompositions** — the space is tightening because 304,850 has limited prime factors.

The canonical 5D shadow is **5 × 7 × 10 × 13 × 67** (sum = 102). This is the canonical 4D space with jubilee (50) split into He (5) × Yod (10). Jubilee is not prime — it is composed of breath and completion.

## The 11 Spaces

| Space | Sum | First axis | Theological reading |
|-------|-----|-----------|-------------------|
| 2 × 5 × 5 × 7 × 871 | 890 | 2 | witness + He² + completeness + love×understanding |
| 2 × 5 × 5 × 13 × 469 | 494 | 2 | witness + He² + love + completeness×understanding |
| 2 × 5 × 5 × 67 × 91 | 170 | 2 | witness + He² + understanding + angel |
| 2 × 5 × 7 × 13 × 335 | 362 | 2 | witness + He + completeness + love + He×understanding |
| 2 × 5 × 7 × 65 × 67 | 146 | 2 | witness + He + completeness + He×love + understanding |
| 2 × 5 × 13 × 35 × 67 | 122 | 2 | witness + He + love + completeness×He + understanding |
| 2 × 7 × 13 × 25 × 67 | 114 | 2 | witness + completeness + love + jubilee-center + understanding |
| 5 × 5 × 7 × 13 × 134 | 164 | 5 | He + He + completeness + love + witness×understanding |
| 5 × 5 × 7 × 26 × 67 | 110 | 5 | He + He + completeness + YHWH + understanding |
| 5 × 5 × 13 × 14 × 67 | 104 | 5 | He + He + love + David + understanding |
| **5 × 7 × 10 × 13 × 67** | **102** | **5** | **He + completeness + Yod + love + understanding** |

## Centers: The Clean Split

The most striking finding at 5D is the center convergence.

### Spaces starting with 2 (7 spaces)

**All seven center on Numbers 23:22** — Balaam's oracle. Every single one. Same position: 228,637.

These spaces all have the witness axis first. The center of the witness axis is 1 (= 2÷2), which places the center in the second half of the text. Position 228,637 falls in Numbers 23, Balaam's second oracle.

Numbers 23:22 reads: *"God brings them out of Egypt; He has strength like a wild ox."*

### Spaces starting with 5 (4 spaces)

These center in **Leviticus 8** — the ordination chapter. The specific verses:

| Space | Verse | Content |
|-------|-------|---------|
| 5 × 5 × 7 × 13 × 134 | **Lev 8:29** | Moses takes his portion of the ordination ram |
| 5 × 5 × 7 × 26 × 67 | **Lev 8:29** | Same |
| 5 × 5 × 13 × 14 × 67 | **Lev 8:29** | Same |
| 5 × 7 × 10 × 13 × 67 | **Lev 8:35** | **Guard the charge of the LORD** |

The canonical 5D (**5 × 7 × 10 × 13 × 67**) centers at **Lev 8:35** — "Guard the charge of the LORD, that you die not." This is the same center verse as the canonical 4D space (7 × 50 × 13 × 67). When jubilee splits into He × Yod, the center survives.

### The pattern across dimensions

| Dimension | First axis = 2 | All axes odd | Even interior axis |
|-----------|---------------|-------------|-------------------|
| 2D | Numbers 23:22 | Leviticus 8:29 | Leviticus 8:35–14:37 |
| 3D | Numbers 23:22 | Leviticus 8:29 | Leviticus 8:30–16:29 |
| 4D | Numbers 23:22 | Leviticus 8:29 | Leviticus 8:30–16:29 |
| 5D | Numbers 23:22 | Leviticus 8:29 | Leviticus 8:29–8:35 |
| 6D | Numbers 23:22 | — | — |

**The rule is sharper than "first axis."** When all axes are odd, the center is exactly N/2 = 152,425 → Lev 8:29. When any axis is even, the center shifts — the 2-axis shifts it to Numbers, while even interior axes shift it to nearby Leviticus verses. The farther the shift, the farther from the ordination.

## Walk Smoothness

| Space | Mean | Max | step1% |
|-------|------|-----|--------|
| 5 × 5 × 7 × 13 × 134 | **1.0** | **1** | **100%** |
| 5 × 5 × 7 × 26 × 67 | 2.0 | 67 | 98.5% |
| 5 × 5 × 13 × 14 × 67 | 2.0 | 67 | 98.5% |
| 5 × 7 × 10 × 13 × 67 | 2.0 | 79 | 98.5% |
| 2 × 5 × 7 × 13 × 335 | 2.0 | 357 | 99.7% |
| ... | | | |
| 2 × 5 × 5 × 7 × 871 | 2.0 | 885 | 99.9% |

Only one 5D space achieves a perfectly smooth walk: **5 × 5 × 7 × 13 × 134**. This is the space where 67 (understanding) is fused with 2 (witness) into 134. The walk is perfectly smooth because 5 × 5 × 7 × 13 = 2,275 divides evenly into 304,850.

The canonical 5D (5 × 7 × 10 × 13 × 67) has mean = 2.0 and max jump = 79 — smooth enough, with the occasional jump along the understanding axis.

## Fold Coordinates

### Spaces starting with 2

All seven fold to **[1, 0, 0, 0, 0]**. The fold cleanly separates the two witness slabs. Every subsequent coordinate is zero.

### Spaces starting with 5

| Space | Fold coordinate | Reading |
|-------|----------------|---------|
| 5 × 5 × 7 × 13 × 134 | [2, 2, 3, 6, **67**] | center, center, center, center, **understanding** |
| 5 × 5 × 7 × 26 × 67 | [2, 2, 3, **13**, 0] | center, center, center, **love**, zero |
| 5 × 5 × 13 × 14 × 67 | [2, 2, 6, **7**, 0] | center, center, center, **completeness**, zero |
| 5 × 7 × 10 × 13 × 67 | [2, 3, **5**, 0, 0] | center, center, **He**, zero, zero |

**The fold coordinates spell theological values.** In the canonical 5D, the fold sits at [2, 3, 5, 0, 0] — the third coordinate is 5 (He, breath). The fold happens at the breath.

In the YHWH space (5 × 5 × 7 × 26 × 67), the fold's fourth coordinate is 13 (love). In the David space (5 × 5 × 13 × 14 × 67), it's 7 (completeness).

## Aleph-Tav Fold Pairs

### The sharing hierarchy at 5D

The sharing percentages tell a consistent story. For spaces starting with 5:

| Axis position | Value | Sharing % |
|--------------|-------|-----------|
| 1st | 5 | **26.2%** |
| 2nd | 5 or 7 | **15–21%** |
| 3rd | 7–13 | **7–15%** |
| 4th | 13–26 | **0–8%** |
| 5th | 67–134 | **0–2%** |

**The sharing drops monotonically with axis value.** Smaller axes share more. This is expected geometrically (fewer bins = more collisions), but the rates consistently exceed random, especially for the prime theological axes.

### The canonical 5D

For **5 × 7 × 10 × 13 × 67**: sharing = [26.2%, 15.4%, 0.0%, 7.6%, 1.7%].

The Yod axis (10) shows **0% sharing** — no aleph-tav pair shares the Yod coordinate at the fold. This is the analog of love's silence in the 4D canonical: when you split jubilee into He × Yod, it's the Yod that goes silent at the fold.

## ELS at Stride Values

### The canonical 5D

**5 × 7 × 10 × 13 × 67** has 5 axis values and 5 strides:

| Skip | Torah | YHWH | Type |
|------|-------|------|------|
| 5 | 18 | 52 | axis (He) |
| 7 | 20 | 51 | axis (completeness) |
| 10 | 16 | 50 | axis (Yod) |
| 13 | 17 | 53 | axis (love) |
| 67 | 14 | 60 | axis (understanding) |
| 871 | 20 | 44 | stride (love × understanding) |
| **8,710** | **23** | **65** | **stride (He × love × understanding)** |

The stride 8,710 is remarkable: 23 Torah and 65 YHWH — the highest YHWH count at any stride value in the entire survey. This stride corresponds to walking through one complete He × love × understanding slab. The text has more YHWH at this large skip than at most small skips.

### Cross-space comparison

The space 2 × 5 × 7 × 13 × 335 has all four theological primes visible (2, 5, 7, 13) plus 335 = 5 × 67. At skip 335: torah=24, yhwh=35. The 5 × 67 composite axis produces moderate Torah counts but low YHWH.

Skip-469 (= 7 × 67, completeness × understanding) appears in three spaces: torah=17, yhwh=**61** — YHWH concentrated when completeness and understanding are fused.

## What 5D reveals

The move from 4D to 5D splits jubilee. What we learn:

1. **The breath holds the center.** The fold's coordinate in the canonical 5D is He (5). In 4D, the fold is at jubilee center (25). When jubilee splits, the breath survives as the fold marker.

2. **Yod goes silent at the fold.** Love was fold-silent in 4D. When jubilee splits into He × Yod, Yod inherits the silence.

3. **The center holds.** The canonical 5D centers on Lev 8:35 — the same verse as the canonical 4D. When jubilee splits into He × Yod, the center does not drift. "Guard the charge" persists.

4. **The 8,710 stride is YHWH's peak.** The largest natural stride in the canonical 5D gives the strongest YHWH signal in the entire survey.

---

Script: `dev/experiments/136_5d_survey.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
