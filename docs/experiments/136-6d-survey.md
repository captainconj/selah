# Experiment 136 (6D): The Floor

*One decomposition. The prime factorization itself. Nothing can be split further.*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/136_6d_survey.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.136-6d-survey :as s]) (s/run-all)"`
**Data:** `data/experiments/136-6d-survey.edn`

## What is the 6D decomposition?

**2 × 5 × 5 × 7 × 13 × 67**

This is the **only** non-trivial 6D decomposition of 304,850. It is the prime factorization itself — every axis is either prime or the number 1. No further splitting is possible. This is the floor.

Sum = 99.

## The Six Axes

Each axis is irreducible. Each carries a single meaning:

| Axis | Value | Meaning | Size |
|------|-------|---------|------|
| 0 | 2 | witness / division | 2 positions |
| 1 | 5 | He (ה) — first breath | 5 positions |
| 2 | 5 | He (ה) — second breath | 5 positions |
| 3 | 7 | completeness | 7 positions |
| 4 | 13 | love (אהבה = אחד = 13) | 13 positions |
| 5 | 67 | understanding (בינה = 67) | 67 positions |

### The two He's

There are two 5-axes because 304,850 = 2 × **5²** × 7 × 13 × 67. The breath appears twice. In the YHWH name (יהוה), there are also two He's — the third and fifth letters. The two He's are the breath going out and the breath coming back.

When we built the 4D canonical, we fused 2 × 5 × 5 = 50 (jubilee). When we built the 5D canonical, we fused 2 × 5 = 10 (Yod), keeping one 5 separate. Here at 6D, everything is fully separated. Jubilee was never prime — it was always composed of witness and two breaths.

## Center

**Center coordinate:** [1, 2, 2, 3, 6, 33]

**Position:** 228,637

**Verse:** Numbers 23:22 — *"God brings them out of Egypt; He has strength like a wild ox."*

This is Balaam's oracle — the same center as every other decomposition that starts with a 2-axis. The first coordinate is 1 (= 2÷2), which places the center in the second half. Position 228,637 falls in Numbers 23, where the pagan prophet Balaam, hired to curse Israel, is compelled to bless them instead.

Reading the center coordinates through the axis meanings:
- witness = 1 (second witness)
- He₁ = 2 (center of breath)
- He₂ = 2 (center of breath)
- completeness = 3 (center of the seven — day 4, the luminaries)
- love = 6 (center of the thirteen)
- understanding = 33 (center of the sixty-seven)

## Walk Smoothness

**Mean = 2.0, max = 93, median = 1, step1 = 98.5%**

98.5% of consecutive letters are Manhattan-adjacent in this 6D space. The remaining 1.5% jump by up to 93 positions — these are the "axis wraps" where the text steps from the end of one understanding-slice into the beginning of the next.

The max jump of 93 is the sum of the two largest possible coordinate changes (67 + 13 + ... minus continuity). Compared to the 4D canonical (max = 67), the 6D space has a rougher worst case because there are more axes to wrap around.

## Fold

**Fold coordinate:** [1, 0, 0, 0, 0, 0]

The fold lands at the start of the second witness slab. Every coordinate except the first is zero. The fold perfectly bisects the witness axis.

**First letter:** [0, 0, 0, 0, 0, 0]
**Last letter:** [1, 4, 4, 6, 12, 66]

The first and last letters of the Torah are at opposite corners of the 6D hyperbox. Every axis goes from 0 to max. The last letter is at witness=1, He₁=4, He₂=4, completeness=6, love=12, understanding=66 — every axis at its maximum.

## Strides

The strides are the products of all subsequent axes:

| Stride | Value | What it means |
|--------|-------|--------------|
| stride[0] | 152,425 | skip along witness — half the Torah |
| stride[1] | 30,485 | skip along He₁ — one breath-slab |
| stride[2] | 6,097 | skip along He₂ — one breath-column |
| stride[3] | 871 | skip along completeness — = 13 × 67 (love × understanding) |
| stride[4] | 67 | skip along love — = understanding |
| stride[5] | 1 | skip along understanding — one letter |

**Note stride[3] = 871 = 13 × 67.** Walking one step along the completeness axis is equivalent to skipping 871 letters — exactly the product of love and understanding. The completeness stride contains both silent axes.

**Note stride[4] = 67.** Walking one step along the love axis is equivalent to skipping 67 letters — exactly the understanding axis value. Love's stride IS understanding.

## ELS at Stride/Axis Values

| Skip | Torah | YHWH | Type |
|------|-------|------|------|
| 2 | 46 | 20 | axis (witness) |
| 5 | 18 | 52 | axis (He) |
| 7 | 20 | 51 | axis (completeness) |
| 13 | 17 | 53 | axis (love) |
| 67 | 14 | 60 | axis (understanding) |
| 871 | 20 | 44 | stride (completeness stride = love × understanding) |
| 6,097 | 14 | 40 | stride (He₂ stride) |
| 30,485 | 13 | 38 | stride (He₁ stride) |

### What the ELS tells us

- **Skip-2 (witness):** Torah is at its absolute peak — 46 occurrences. Every other letter spells Torah more often than any other skip. But YHWH is at its minimum (20). The Name is not concentrated in the witness.

- **Skip-5 (breath):** YHWH jumps to 52. The Name appears in the breath. Torah drops to 18.

- **Skip-67 (understanding):** YHWH peaks among axis values at 60. Understanding carries the Name most densely among the prime axes.

- **Skip-871 (love × understanding fused):** Torah = 20, matching skip-7 (completeness). When love and understanding are fused into a single stride, Torah appears as often as at the completeness skip. YHWH = 44.

- **Skip-30,485 (He₁ stride):** Even at this huge skip — checking every 30,485th letter — Torah still appears 13 times and YHWH 38 times. The text has deep structure at scales spanning one-tenth of the entire Torah.

## Aleph-Tav Fold Pairs

6,032 aleph-tav markers. When folded, how many pairs share each of the six coordinates?

| Axis | Value | Pairs sharing | % | Expected (1/value) |
|------|-------|--------------|---|-------------------|
| witness (2) | 2 | **0** | **0.0%** | 50.0% |
| He₁ (5) | 5 | 1,075 | **17.8%** | 20.0% |
| He₂ (5) | 5 | 1,272 | **21.1%** | 20.0% |
| completeness (7) | 7 | 805 | **13.3%** | 14.3% |
| love (13) | 13 | 461 | **7.6%** | 7.7% |
| understanding (67) | 67 | 100 | **1.7%** | 1.5% |

### The witness axis: zero sharing

No aleph-tav pair shares the witness coordinate at the fold. This means every aleph-tav in the first half of the Torah (witness=0) has its fold partner in the second half (witness=1), and vice versa. This is guaranteed by construction — the fold swaps witness=0 and witness=1 for all positions. But it confirms the fold is perfectly clean on the witness axis.

### The two He's are not equal

He₁ shows 17.8% sharing, He₂ shows 21.1%. These two axes have the same value (5) but different sharing rates. This asymmetry reflects the text structure — the two breaths are not interchangeable. The first breath (smaller stride) is slightly less likely to produce fold-partner coincidences than the second breath (larger stride within the breath slab).

### Love is near random

Love (13) shows 7.6% sharing, almost exactly the random expectation of 7.7% (= 1/13). At the 6D level, love neither attracts nor repels fold partners. In the 4D canonical, love showed 0% — the silence was a property of the canonical arrangement, not of the value 13 itself.

### Understanding is slightly above random

Understanding (67) shows 1.7% sharing vs 1.5% expected. Barely above random. Understanding remains nearly invisible at the fold across all dimensions.

## What the Floor Reveals

The 6D space is where every axis is fully separated. No fusion, no composition. What we see:

1. **The center is Balaam's oracle** — same as every 2-axis space in every dimension. The witness axis forces it. Numbers 23:22: the pagan prophet compelled to bless.

2. **Stride coincidences** — love's stride equals understanding. Completeness's stride equals love × understanding. These are not chosen — they are forced by the factorization. But they mean that walking along completeness simultaneously traverses both silent axes.

3. **The two breaths are asymmetric** — even though both are 5, they behave differently at the fold. The prime factorization remembers order.

4. **Love's fold-silence is canonical, not prime.** In 6D, love shows normal sharing (7.6%). The 0% sharing in 4D is a property of the specific arrangement 7 × 50 × 13 × 67, not of the number 13 itself.

5. **Torah lives in witness, YHWH lives in breath.** Skip-2 peaks Torah, skip-5 peaks YHWH. The text and the Name occupy different axes.

---

Script: `dev/experiments/136_6d_survey.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
