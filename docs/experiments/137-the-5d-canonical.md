# Experiment 137 (continued): The 5D Canonical Space

*Five axes. One cube. The door of the Ark.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/137b_passing_through.clj`, `dev/experiments/137c_axis_orderings.clj`, `dev/experiments/137d_yhwh_loves.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.137b-passing-through :as s]) (s/run-all)"`
**Data:** `data/experiments/137-hoh-across-spaces.edn`, `data/experiments/137c-axis-orderings-output.txt`, `data/experiments/137d-yhwh-loves-output.txt`
**Prior:** `docs/experiments/137-hoh-across-spaces.md` (the survey that found one home)

## The Space

**5 × 7 × 10 × 13 × 67** — the canonical 5D refinement.

This is the canonical 4D space [7, 50, 13, 67] with jubilee split: 50 = 5 × 10 (He × Yod). The five axes:

| Axis | Value | Meaning |
|------|-------|---------|
| 0 | 5 | He (ה) — breath |
| 1 | 7 | completeness — the seven days |
| 2 | 10 | Yod (י) — hand, completion |
| 3 | 13 | love (אהבה = אחד = 13) |
| 4 | 67 | understanding (בינה = 67) |

Axis sum = 102. Center coordinate = [2, 3, 5, 6, 33]. Center position = 152,860 → **Leviticus 8:35** — the same center as the canonical 4D.

The Holy of Holies (10×10×10 cube) spans axes [10, 13, 67] and is pinned at He=2 (center of breath) and completeness=3 (center of the seven days).

## 1. Passing Through the Door

The cube sits at He=2, completeness=3. What happens when we walk along the two pinning axes — passing through the five breaths and the seven days?

### The Breath Walk (He = 0..4, day fixed at 3)

Each He-slab places the Ark's 45 letters in a different book of the Torah:

| He | Spine | Book | Verse | Ark GV |
|----|-------|------|-------|--------|
| 0 | להגמל | Genesis | 24:30 — *"when he saw the ring"* (Rebekah at the well) | 2526 |
| 1 | עבדיך | Exodus | 10:6 — *"your servants"* (plague of locusts) | 2848 |
| **2** | **יהוהו** | **Leviticus** | **8:35 — "guard the charge of YHWH"** | **2603 = 19 × 137** |
| 3 | וככהת | Numbers | 15:12 — *"according to the number"* | 2348 |
| 4 | יךכלה | Deuteronomy | 14:23 — *"all your tithe"* | 3175 |

The five breaths walk through the five books. The Ark GV = 19 × 137 appears **only** at He=2. The spine spells YHWH **only** at He=2.

The oracle between the cherubim also changes with each breath:

| He | Between cherubim | Oracle |
|----|-----------------|--------|
| 0 | ךאשאהאקדו | — |
| 1 | קראויצצרי | — |
| **2** | **תויהשמםאש** | **איש מות משה — "man, death, Moses"** |
| 3 | בלוםגרפשה | גבול משה פר — "Moses' border, bull" |
| 4 | תועםשמווק | — |

At He=3 (one breath past the door), the oracle retains Moses and the bull (פר) — a shadow of the canonical reading, but missing the death (מות) and the man (איש).

### The Completeness Walk (day = 0..6, He fixed at 2)

Seven days. The Ark's spine at each day:

| Day | Spine | Verse | Oracle between cherubim | Ark GV |
|-----|-------|-------|------------------------|--------|
| 0 | שהקול | Exodus 32:17 — *"the sound"* (golden calf) | או אמת כי פר — "or truth, because bull" | 3307 |
| 1 | אתהכל | Exodus 37:16 — *"all its vessels"* (building the table) | — | 3633 |
| 2 | כליתו | Leviticus 3:15 — *"his kidneys"* (peace offering) | **אהל אמת זהב — "tent of truth, gold"** | 3365 |
| **3** | **יהוהו** | **Leviticus 8:35 — "guard the charge of YHWH"** | **איש מות משה — "man, death, Moses"** | **2603 = 19 × 137** |
| 4 | ושלחא | Leviticus 14:7 — *"and shall let go"* (cleansing leper) | אש בוא גבול — "fire comes to the border" | 3588 |
| 5 | יישרא | Leviticus 19:2 — *"Israel"* (be holy) | — | 4265 |
| 6 | ןאישמ | Leviticus 24:10 — *"a man"* (the blasphemer) | — | 3435 |

Day 2 — the step before you enter the Ark — the oracle reads **"tent of truth, gold"** (אהל אמת זהב). The tent of meeting, built of truth and gold, stands at the threshold.

Day 4 — the step after — reads **"fire comes to the border"** (אש בוא גבול). You step through the door; fire meets you at the boundary.

### The 5×7 Grid

Every combination of He (0..4) × day (0..6) = 35 possible pin positions. Ark GV = 2603 = 19 × 137 appears at **exactly one** cell: **[2, 3]**. No other (He, day) combination produces it.

Other readings from the grid:

| Cell | Oracle | Note |
|------|--------|------|
| [0,5] | והיה לו מות | "and there shall be death for him" |
| [1,2] | אכל מות משה | "Moses ate death" |
| [2,3] | **איש מות משה** | **"man, death, Moses"** (canonical) |
| [3,3] | גבול משה פר | "Moses' border, bull" |
| [4,4] | אנכי בו כרת | "I AM in it, cut off" (covenant) |

At [3,2] the spine reads **ייהוה** — Yod + YHWH. One breath past the canonical, one day earlier, the Name gains a prefix. At [1,2] the spine reads **ויהיב** — ויהי ("and it was"), the creation formula.

## 2. Axis Orderings

There are 120 permutations of [5, 7, 10, 13, 67]. In every ordering, the cube axes are always {10, 13, 67} (the only three ≥ 10) and the pinning axes are always {5, 7}. The question: does the ordering of these axes within the 5-tuple change the result?

### Full results across 120 orderings

| Property | # orderings |
|----------|------------|
| Lev 8:35 inside cube | 22 |
| YHWH in Ark | 11 |
| Ark GV = 2603 = 19 × 137 | **2** |
| Oracle = "man, death, Moses" | **2** |
| **All four together** | **2** |

### The two orderings with the full signature

- **[5, 7, 10, 13, 67]**
- **[7, 5, 10, 13, 67]**

These differ only in swapping the two pinning axes. Since both are pinned at their center value (He=2, completeness=3), the swap changes nothing — the cube occupies exactly the same 1,000 letters. **There is exactly one physical cube placement across all 120 orderings.**

### Why other orderings fail

When the cube axes are reordered (e.g., [5, 7, 13, 10, 67] puts love before Yod), the strides change, and the cube addresses different letters entirely. 94 of the 120 orderings produce a unique Ark GV — almost every arrangement sees different text.

### Notable readings in other orderings

| Ordering | Properties | Oracle |
|----------|-----------|--------|
| [7, 10, 5, 13, 67] | YHWH only | אהב בר יהוה — "YHWH loves the pure" |
| [7, 67, 13, 5, 10] | LEV only | את יהוה ראה — "see YHWH / the sign" |
| [7, 13, 10, 5, 67] | none | אחד אמת אשה — "one truth, woman" |

The ordering [7, 10, 5, 13, 67] is the same axis values as the canonical but with He and Yod swapped in the cube. It finds YHWH in the Ark and reads "YHWH loves the pure" — but misses Lev 8:35 and the 137 signature.

## 3. The Pinning Coordinates

Covered in section 1. The 5×7 grid of all 35 possible (He, day) pin positions shows that:

- **[2, 3] is the unique cell** with Ark GV = 19 × 137
- **[2, 3] is the unique cell** with YHWH on the spine
- **[2, 3] is the unique cell** with "man, death, Moses" between the cherubim
- No other pin position produces even two of these three signatures simultaneously

The canonical pin is not just preferred — it is the only pin that works.

## 4. What 5D Reveals That 4D Hides

The 136 survey measured this space alongside all 11 non-trivial 5D decompositions. Here is what is specific to the canonical 5D.

### The fold

**Fold coordinate: [2, 3, 5, 0, 0]**

The fold of the Torah (letter 152,425) in this space lands at He=2, completeness=3, Yod=5, love=0, understanding=0. The first three coordinates are at their axis centers. Love and understanding are at zero — **the silent axes start at their origin at the fold point**, exactly as in the 4D canonical where the fold was [3, 25, 0, 0].

The breath coordinate at the fold is He = 2, which equals the He-value itself. The fold happens *at* the breath.

### Aleph-tav fold pairs

| Axis | Value | Sharing | Expected |
|------|-------|---------|----------|
| He (5) | 5 | **26.2%** | 20.0% |
| completeness (7) | 7 | **15.4%** | 14.3% |
| Yod (10) | 10 | **0.0%** | 10.0% |
| love (13) | 13 | 7.6% | 7.7% |
| understanding (67) | 67 | 1.7% | 1.5% |

**Yod goes silent at the fold.** Zero aleph-tav pairs share the Yod coordinate when the Torah is folded in half. In the 4D canonical, love was fold-silent (0%). When jubilee splits into He × Yod, it is **Yod that inherits the silence**, not He. Love returns to near-random (7.6%).

The silence transfers: jubilee (50) was silent in 4D → Yod (10) is silent in 5D. The breath survives; the completion goes quiet.

### Walk smoothness

Mean = 2.0, max = 79, step1 = 98.5%. Smooth enough — 98.5% of consecutive letters are Manhattan-adjacent. The max jump of 79 comes from axis wraps along the understanding axis (67) plus edge effects.

### ELS at natural strides

| Skip | Torah | YHWH | What it means |
|------|-------|------|---------------|
| 5 | 18 | 52 | axis: He (breath) |
| 7 | 20 | 51 | axis: completeness |
| 10 | 16 | 50 | axis: Yod |
| 13 | 17 | 53 | axis: love |
| 67 | 14 | 60 | axis: understanding |
| 871 | 20 | 44 | stride: love × understanding |
| **8,710** | **23** | **65** | **stride: He × love × understanding — YHWH peak** |

The stride 8,710 = 5 × 13 × 67 = He × love × understanding. At this skip — checking every 8,710th letter — YHWH appears **65 times**. This is the highest YHWH count at any stride value in the entire 5D survey. Torah appears 23 times, also strong.

This stride is unique to the canonical 5D. It corresponds to walking through one complete breath × love × understanding slab. The text has more YHWH at this macro-scale skip than at most single-axis skips.

## 5. The "YHWH Loves" Spaces

Two spaces from the 137 survey — `5 × 14 × 65 × 67` and `7 × 10 × 65 × 67` — produced identical Ark regions where the oracle reads **אהב יהוה** ("YHWH loves").

### Why they are identical

Both spaces have cube axes (1, 2, 3) with axis sizes [14/10, 65, 67]. The strides for the cube axes work out so that the pinned offset plus the cube-axis-1 offset produces the same linear positions:

- Space 1: pin = `2 × 60,970 = 121,940`, axis-1 center at `6 × 4,355 = 26,130`. Total = 148,070.
- Space 2: pin = `3 × 43,550 = 130,650`, axis-1 center at `4 × 4,355 = 17,420`. Total = 148,070.

Same base position, same strides on axes 2 and 3 (both 67 and 1). The 1,000 letters of the HoH, the 45 letters of the Ark, and the 15 letters of the Mercy Seat are all **position-identical**.

### What the YHWH-loves Ark contains

**Cube center:** Leviticus 10:7 — the death of Nadab and Abihu. *"Do not leave the tent of meeting, lest you die; for the anointing oil of the LORD is upon you."*

**Between the cherubim:** 9 letters → יהובבארהה. Oracle reads 24 phrases, all permutations of:
- **אהב** (love)
- **יהוה** or **והיה** (the Name / "and it shall be")
- **בר** or **רב** (pure/son or great/many)

Every reading contains love and the Name. Representative readings:
- אהב יהוה בר — "YHWH loves the pure"
- יהוה אהב רב — "YHWH's love is great"
- והיה אהב בר — "and there shall be love for the pure"
- בר אהב יהוה — "the pure one loves YHWH"

**The Mercy Seat is silent.** No dictionary readings from the 15 mercy-seat letters. In the canonical space, the Mercy Seat asks "what is my name?" Here it says nothing.

**The Ark spine (stride 1):** Letters spell **שמן משחת** — "anointing oil." The anointing runs straight through the center of the Ark, from the verse about Nadab and Abihu dying because the anointing oil of the LORD was upon them.

**Ark GV = 3067.** Not a multiple of 137.

### The contrast with the canonical

| Property | Canonical (5×7×10×13×67) | YHWH-loves (5×14×65×67) |
|----------|-------------------------|------------------------|
| Center | Lev 8:35 — "guard the charge" | Lev 10:7 — "lest you die" |
| Spine | YHWH (יהוהו) | anointing oil (שמן משחת) |
| Between cherubim | man, death, Moses | YHWH loves the pure |
| Mercy Seat | "what is my name?" | silent |
| Ark GV | 2603 = 19 × 137 | 3067 |
| YHWH in Ark | yes | yes |

Both Arks contain the Name. But the canonical Ark centers on the instruction and the question. The YHWH-loves Ark centers on the anointing and the warning. One asks "what is my name?" — the other cannot ask at all.

The YHWH-loves space is not the canonical. But it is not empty. It is the space where the Name appears in the context of love rather than death — where the story is not "man dies" but "YHWH loves the pure." It is a different face of the same structure.

---

Scripts: `dev/experiments/137b_passing_through.clj`, `137c_axis_orderings.clj`, `137d_yhwh_loves.clj`
Shared utilities: `dev/experiments/136_survey_utils.clj`
