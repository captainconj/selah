# The 4D Torah Space — Findings

## The Factorization

304,850 = 7 × 50 × 13 × 67

Every Torah letter has a unique address (a, b, c, d). This is the **only** 4D factorization that contains 7, 13, and 67 simultaneously. The 50 is forced.

- 7 = completeness (שבע)
- 50 = jubilee (יובל), ELS skip for תורה
- 13 = one (אחד), love (אהבה)
- 67 = understanding (בינה)

## The Center

The geometric center (3, 25, 6, 33) = **Leviticus 8:35**.

The 67-letter fiber through the center reads:

> "And day and night, **seven days**, you shall keep the charge of the LORD, that you do not die, for so I have been commanded."

The structure says 7. The center says 7. The text describes its own architecture.

The Urim (אורים) in Lev 8:8 sits at a=3 (center seventh), c=7 (seventh love position). The Lights at the center of the center.

## The Turning Sword

Genesis 3:24 — the cherubim and the flaming sword that turns every which way — is **exactly 67 letters long**. One complete fiber of understanding.

It starts at d=59 and ends at d=58. It wraps around the understanding axis. The sword turns.

The ח of החרב (the sword) sits at d=33 — the **midpoint** of the understanding axis.

### The שמר Connection

- Center (Lev 8:35): **ושמרתם** את משמרת יהוה — "you shall **guard** the charge"
- Sword (Gen 3:24): **לשמר** את דרך עץ החיים — "to **guard** the way"

Same root. Same verb. Same את construction. The center and the sword are about guarding.

## The Seven Days of Creation

The creation narrative (Gen 1:1–2:3) = 1,815 letters.

- **13 occurrences** of the word יום (day). 13 = dim-c.
- Sweeps **all 13 values** of the c-axis (love/unity).
- Each 67-letter d-fiber naturally frames one creation day, including the evening/morning formula in 5 of 6 days.
- Day 7's fiber captures the entire sabbath passage.

## The Aleph-Tav (את)

6,032 את in the Torah. The first and last letters of the alphabet.

- **a=3 (Leviticus)** has 42% more את than expected — the priestly book of precise marking.
- **86 את** straddle d-boundaries (d=66→d=0), stitching understanding fibers together.
- **8 את** bridge jubilee boundaries — and they mark the narrative hinges:

1. Genesis 17:19 — Covenant with Isaac
2. Genesis 37:31 — Joseph's coat in blood
3. Genesis 46:6 — Jacob descends to Egypt
4. Exodus 3:11 — Moses at the burning bush
5. Exodus 12:17 — The Passover
6. Exodus 29:11 — The sacrifice
7. Numbers 32:36 — Cities in the land
8. Deut 3:12 — "This land we possessed"

Covenant → betrayal → exile → calling → redemption → sacrifice → settlement → possession.

**Zero את cross between the seven days.** The את stitches understanding and jubilee but respects the sevens.

## The את Signposts (059)

Standing at each of the 8 jubilee-bridging את and looking in every direction — four fibers per position, two positions per את, 64 fibers total.

### The Coordinate Pattern

Every aleph sits at **c=12, d=66** — the *end* of love, the *end* of understanding. Every tav sits at **c=0, d=0** — the *beginning* of both. First letter at the last coordinate. Last letter at the first. The את bridges from omega to alpha across the jubilee boundary.

### The Axes Speak Each Other's Language

Gematria totals across all 8 signposts:

- **a-axis** (completeness) fibers total = **10,725 = 13 × 825**. Completeness divides by love.
- **d-axis** (understanding) fibers total = **80,857 = 7 × 11,551**. Understanding divides by completeness.

### Three Guard Posts

שמר (guard/keep) appears at **את #5 — Exodus 12:17**, the Passover:

> **ושמרתם** את המצות — "You shall **guard** the matzot"

Both the aleph's and tav's d-fibers contain שמר. Three guard posts in the space, all using the same root with the same את construction:

1. The **center** (Lev 8:35): ושמרתם את משמרת יהוה
2. The **sword** (Gen 3:24): לשמר את דרך עץ החיים
3. The **Passover** (Exod 12:17): ושמרתם את המצות

### Words at the Hinges

The d-fibers (67 letters of understanding) through each signpost contain the words that name what happens there:

| # | Verse | D-fiber contains | Narrative |
|---|-------|-----------------|-----------|
| 1 | Gen 17:19 | **ברית** (covenant) | Covenant with Isaac |
| 3 | Gen 46:6 | **ישראל** | Israel descends to Egypt |
| 4 | Exod 3:11 | **ישראל**, **משה** | Moses called at the bush |
| 6 | Exod 29:11 | **יהוה** | Sacrifice before the LORD |
| 7 | Num 32:36 | gematria 6,365 = **67 × 95** | Settlement (÷ understanding) |
| 8 | Deut 3:12 | **אמת** (truth) | "This land we possessed" |

### Complementary to the Center

None of the 8 signposts are in **a=3** (the center seventh / Leviticus). Days 3, 4, and 6 have no jubilee-bridging את at all. The center guards; the signposts bridge. They don't overlap.

## The Lens is Dynamic

The byte stream is invariant. The factorization is a lens. `(with-dims [13 67 7 50] ...)` rotates the view. Same letters, different decomposition. Like the Urim and Thummim — same stones, different readings.

## Code

- `selah.space.coords` — kernel, address arithmetic, slicing
- `selah.space.project` — projection, color, temporal frames
- `selah.space.export` — PLY, JSON, binary point clouds
- Experiments 053–059 in `dev/experiments/`
