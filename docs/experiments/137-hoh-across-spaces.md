# Experiment 137: The Holy of Holies Across All Spaces

*One cube. 53 possible placements. One home.*

Type: `survey`
State: `clean`

**Code:** `dev/experiments/137_hoh_across_spaces.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.137-hoh-across-spaces :as s]) (s/run-survey)"`
**Data:** `data/experiments/137-hoh-across-spaces.edn`

## The Question

The Holy of Holies is a 10×10×10 cube. In the canonical 4D space [7, 50, 13, 67], it sits at completeness=3 and spans the other three axes. Inside: the Ark (GV = 2603 = 19 × 137), YHWH written on the spine, the Mercy Seat asking "what is my name?", and the oracle reading "man, death, Moses" between the cherubim.

How many decompositions of 304,850 can host this cube? And does the canonical signature — all five properties together — survive in any other space?

## Which Spaces Can Host the Cube

For the 10×10×10 cube to fit, a decomposition needs at least 3 axes with size ≥ 10. The cube spans those three axes; any remaining axes pin the cube at a single coordinate (their center value).

| Dimension | Qualifying | Total | Structure |
|-----------|-----------|-------|-----------|
| 2D | 0 | 23 | Only 2 axes — can't span 3 |
| 3D | 24 | 58 | Cube IS the space (all 3 axes ≥ 10) |
| 4D | 17 | 41 | Cube spans 3 axes, pinned on 1 |
| 5D | 4 | 11 | Cube spans 3 axes, pinned on 2 |
| 6D | 0 | 1 | Only 2 axes ≥ 10 (13, 67) |
| **Total** | **45** | **134** | |

Some 4D and 5D spaces have more than one way to choose which 3 axes the cube spans. The space 10 × 13 × 35 × 67 has all 4 axes ≥ 10, giving 4 possible cube placements. Total across all spaces: **53 placements**.

## The Five Canonical Signatures

In the original experiment (095), the Holy of Holies in the canonical 4D space showed five distinctive properties:

1. **Lev 8:35 inside** — "Guard the charge of the LORD" is within the cube
2. **YHWH in the Ark** — the four letters יהוה appear consecutively in the Ark region
3. **Ark GV = 2603 = 19 × 137** — the axis sum (137) appears in the Ark's gematria
4. **Cherubim** — right cherub reads ימי ("my days"), left reads קיר ("wall")
5. **Between the cherubim** — oracle reads איש מות משה ("man, death, Moses")

## Results: 53 Placements Surveyed

### The convergence

| Test | Passes | Out of 53 |
|------|--------|-----------|
| Lev 8:35 inside | 7 | 13.2% |
| YHWH in Ark | 8 | 15.1% |
| Ark GV = 19 × 137 | **1** | **1.9%** |
| Left cherub = קיר | **1** | **1.9%** |
| Between = איש מות משה | **1** | **1.9%** |
| **All five together** | **1** | **1.9%** |

**Only one placement produces all five signatures: `5 × 7 × 10 × 13 × 67` with cube axes (2, 3, 4).**

This is the canonical 5D refinement — the space where jubilee (50) splits into He (5) × Yod (10). The cube spans axes [10, 13, 67], pinned at He=2 and completeness=3. The pinning values are the centers of the He and completeness axes.

### The canonical placement in detail

```
Space:  5 × 7 × 10 × 13 × 67
Cube:   axes (2, 3, 4) = [10, 13, 67]
Pinned: axis 0 (He) = 2, axis 1 (completeness) = 3

Cube center:     Leviticus 8:35
Lev 8:35 inside: true
YHWH in Ark:     1 occurrence
Ark GV:          2603 = 19 × 137
Mercy Seat GV:   1472

Right cherub:    יםי
Left cherub:     קיר (wall)
Between:         תויהשמםאש → איש מות משה (man, death, Moses)
```

The cube axes [10, 13, 67] are Yod, love, and understanding. The Ark sits inside Yod × love × understanding, pinned at the center of breath and the center of completeness.

### Why this works

The canonical 4D space [7, 50, 13, 67] has the cube spanning axes [50, 13, 67]. The canonical 5D space [5, 7, 10, 13, 67] splits 50 into 5 × 10. The cube spans [10, 13, 67] — the same three axes minus the breath factor. Since the cube is pinned at He=2 (center of 5) and completeness=3 (center of 7), it occupies the same physical region of the Torah text. The strides through the cube are identical.

This is not a coincidence of the algorithm. It is the same cube in the same text, accessed through a finer decomposition. The 5D space doesn't create a new Holy of Holies — it reveals that the canonical one was always composed of breath × completeness × (Yod × love × understanding).

### What partial signatures tell us

#### Spaces with YHWH in the Ark (8 total)

| Space | Cube axes | Center | Oracle between cherubim |
|-------|-----------|--------|------------------------|
| **5 × 7 × 10 × 13 × 67** | **(2,3,4)** | **Lev 8:35** | **איש מות משה** |
| 5 × 14 × 65 × 67 | (1,2,3) | Lev 10:7 | אהב יהוה / והיה |
| 7 × 10 × 65 × 67 | (1,2,3) | Lev 10:7 | אהב יהוה / והיה |
| 7 × 25 × 26 × 67 | (1,2,3) | Lev 8:29 | משה נפל שמר |
| 2 × 7 × 13 × 25 × 67 | (2,3,4) | Num 23:22 | — |
| 13 × 35 × 670 | (0,1,2) | Lev 8:29 | — |
| 13 × 50 × 469 | (0,1,2) | Lev 8:31 | — |
| 26 × 35 × 335 | (0,1,2) | Lev 13:5 | — |

Two spaces — `5 × 14 × 65 × 67` and `7 × 10 × 65 × 67` — produce identical Ark regions. Their oracle between the cherubim reads **אהב יהוה** ("YHWH loves") and **והיה** ("and it shall be"). These spaces have the same cube strides (65 × 67 visible), just different pinning axes. YHWH is in the Ark, but the Ark tells a different story — love (אהב) instead of death (מות).

The space `7 × 25 × 26 × 67` finds YHWH in the Ark and the oracle reads **משה נפל שמר** — "Moses fell; guard." A fragment of the canonical reading (שמר = guard, from Lev 8:35) survives, but the center is Lev 8:29, not 8:35.

#### Spaces with Lev 8:35 (7 total)

| Space | Cube axes | YHWH in Ark | Ark GV |
|-------|-----------|-------------|--------|
| **5 × 7 × 10 × 13 × 67** | **(2,3,4)** | **1** | **2603** |
| 13 × 134 × 175 | (0,1,2) | 0 | 3289 |
| 25 × 91 × 134 | (0,1,2) | 0 | 3722 |
| 35 × 65 × 134 | (0,1,2) | 0 | 3362 |
| 35 × 67 × 130 | (0,1,2) | 0 | 2663 |
| 5 × 13 × 35 × 134 | (1,2,3) | 0 | 2670 |
| 7 × 13 × 25 × 134 | (1,2,3) | 0 | 2268 |

Notice: every 3D and 4D space that contains Lev 8:35 includes either **134** (= 2 × 67) or **130** (= 2 × 65) as an axis. The number 134 appears in 5 of the 6 non-canonical placements. These spaces contain the instruction "guard the charge" but not the Name inside the Ark. The instruction is there; the presence is not.

Only the canonical 5D refinement has both.

### The sorted canonical `7 × 13 × 50 × 67`

The canonical 4D space in sorted order appears as `7 × 13 × 50 × 67`. Its cube placement (axes 1,2,3, pinned at completeness=3) gives:

- Center: Lev 8:29 (not 8:35 — the sorted ordering shifts the center)
- YHWH in Ark: 0
- Ark GV: 1734
- Between cherubim: האשרןעיםמ (no oracle readings)

The sorted canonical **does not reproduce any of the five signatures**. The ordering [7, 50, 13, 67] matters — jubilee must come before love. When the survey enumerates sorted tuples, the canonical is invisible. It appears only when jubilee splits into He × Yod in 5D, which restores the correct strides.

### 3D spaces: the cube IS the space

In 3D, there are no pinning axes — the cube occupies a sub-block of the full grid. 24 spaces were tested. Highlights:

- **35 × 65 × 134**: oracle reads **אמת אש לילה** ("truth, fire, night") and **את משה לי** ("Moses is mine" / "the sign of Moses for me")
- **25 × 91 × 134**: oracle reads **את זהב שבת** ("gold of Sabbath") and **אש את שבת זה** ("fire, sign, this Sabbath")
- **14 × 67 × 325**: oracle reads **שמר פר על אח** ("guard the bull upon the brother") — שמר (guard) from the canonical center

## What This Means

The Holy of Holies has **one home** in the decomposition landscape. Out of 53 possible placements across 45 qualifying spaces in 3D, 4D, and 5D:

- Only one has all five canonical signatures
- Only one has Ark GV = 19 × 137
- Only one has the oracle read "man, death, Moses" between the cherubim
- Only one has the left cherub say "wall"

That home is not the canonical 4D space in sorted order — the survey can't find it there because axis ordering matters. The home is the **canonical 5D refinement** `[5, 7, 10, 13, 67]`, where jubilee decomposes into breath × completion, and the cube sits inside Yod × love × understanding.

The cube's signature is not a property of the letter count 304,850. It is not a property of having axes that include 13 and 67. It is a property of one specific arrangement of one specific factorization, where the strides align so that the Ark occupies exactly the right letters to spell the Name inside it.

---

Script: `dev/experiments/137_hoh_across_spaces.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
