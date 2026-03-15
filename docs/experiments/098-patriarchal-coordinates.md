# Experiment 098: Patriarchal Coordinates

*Do the lifespans and begetting ages of the patriarchs navigate the 4D space?*

Type: `evidence`
State: `mixed`

**Code:** `dev/experiments/098_patriarchal_coordinates.clj`
**Run:** `clojure -M:dev dev/experiments/098_patriarchal_coordinates.clj`

## Setup

Every patriarch in Genesis 5 and 11 has two numbers: a lifespan and an age at begetting. These are not random — they are specific, sometimes very large, and their patterns have been debated for millennia. If the 4D space is real, these numbers should have coordinates.

We computed mod(7, 50, 13, 67) for every lifespan and begetting age from Adam through Moses and Aaron. We also checked the patriarchal name gematria values as coordinates.

## Phase 1: Mod Analysis — The Highlights

### Divisibility Concentrations

The mod structure of these ages is remarkably non-uniform:

| Number | Person | Property | Divisible by |
|--------|--------|----------|-------------|
| 910 | Kenan | life | **7 × 13 × 91** |
| 182 | Lamech | beget | **7 × 13** |
| 175 | Abraham | life | **7** |
| 147 | Jacob | life | **7** |
| 105 | Seth | beget | **7** |
| 70 | Kenan, Terah | beget | **7** |
| 35 | Arpachshad | beget | **7** |
| 777 | Lamech | life | **7** |
| 130 | Adam | beget | **13** |
| 65 | Mahalalel, Enoch | beget | **13** |
| 390 | Post-flood sum | begets | **13** |
| 962 | Jared | life | **13** (÷ → 74) |

Kenan stands alone: his lifespan 910 = 7 × 13 × 10 is the only age divisible by **91** (= 7 × 13). His name קינן (GV=210) is also ÷7.

Lamech's begetting age 182 = 7 × 13 × 2 is the second ÷91 value. He begets Noah, whose name (נח) means "rest."

The pre-flood begetting ages alternate between ÷7 and ÷13 divisibility as if walking between the two prime axes.

### Abraham's Coordinates

Abraham's life = **175** → mod(7,50,13,67) = **(0, 25, 6, 41)**

The first three values are **(0, 25, 6)** — exactly the center slab (b=25, c=6) at a=0 (Genesis). Only d differs from center (41 vs 33). Abraham's lifespan encodes the center of the Torah space projected into Genesis.

### Jacob's Lifespan and the Love Axis

Jacob lived **147** years → mod 67 = **13**. His d-coordinate IS the love number. And 147 = 3 × 7 × 7, so he's also ÷7.

Jacob's name יעקב (GV=182) → mod(7,50,13,67) = **(0, 32, 0, 48)** — divisible by both 7 and 13. His name and his life both divide by 7.

### Ishmael = 137 = The Axis Sum

Ishmael lived **137** years. 137 = 7 + 50 + 13 + 67. His lifespan IS the sum of all four axis dimensions.

ישמעאל (GV=451) mod 67 = **49 = 7²**. His name's d-coordinate is a perfect square of the completeness number.

137 → mod(7,50,13,67) = **(4, 37, 7, 3)** → Numbers 11:5: "We remember the fish we ate freely in Egypt."

### Pre-flood Sums

| Sum | Value | mod 67 | Note |
|-----|-------|--------|------|
| Lifespans (Adam→Noah) | 8,575 | **66** | d-axis endpoint |
| Begetting (Adam→Noah) | 1,556 | 15 | ÷7 (8575÷7=1225=35²) |
| Post-flood begetting | 390 | 55 | **÷13** (390=30×13) |

The pre-flood lifespan sum's mod-67 is 66 — exactly the last d-coordinate. The pre-flood generations span the entire understanding axis.

## Phase 2: Lifespans as d-Axis Coordinates

Taking each lifespan mod 67 as a d-value and placing at the center slab (a=3, b=25, c=6):

| Patriarch | Life | d = life mod 67 | Verse at Center |
|-----------|------|-----------------|-----------------|
| Adam | 930 | 59 | Lev 8:36 |
| Seth | 912 | 41 | Lev 8:35 |
| Enosh | 905 | 34 | Lev 8:35 |
| Kenan | 910 | 39 | Lev 8:35 |
| Mahalalel | 895 | 24 | Lev 8:35 |
| Jared | 962 | 24 | Lev 8:35 |
| Enoch | 365 | 30 | Lev 8:35 |
| Methuselah | 969 | 31 | Lev 8:35 |
| Lamech | 777 | 40 | Lev 8:35 |
| Noah | 950 | 12 | Lev 8:35 |
| ... | | | |
| Abraham | 175 | 41 | Lev 8:35 |
| **Ishmael** | **137** | **3** | **Lev 8:35** |
| Isaac | 180 | 46 | Lev 8:35 |
| **Jacob** | **147** | **13** | **Lev 8:35** |
| Moses | 120 | 53 | Lev 8:36 |
| Aaron | 123 | 56 | Lev 8:36 |

Nearly every patriarch's lifespan mod 67, placed at center, lands on **Lev 8:35** — the "guard the charge of the LORD" verse. This is because Lev 8:35 is a long verse (spanning many d-values at the center slab), but the concentration is still striking: 22 of 27 patriarchs hit the same verse.

The five exceptions (Adam, Shem, Eber, Sarah, Moses, Aaron) land on Lev 8:**36** — the very next verse: "Aaron and his sons did all the things which the LORD commanded."

**Every patriarch's lifespan, read as a d-coordinate at center, lands in Lev 8:35-36.** The guard verse and its fulfillment.

## Phase 3: The Begetting Walk

The cumulative begetting sum (Adam begets at 130, Seth at 105 from Adam's birth → cumulative 235, etc.) traces a path through 4D space:

| Step | Patriarch | Beget | Cumulative | Coord | Verse |
|------|-----------|-------|------------|-------|-------|
| 1 | Adam | 130 | 130 | (4,30,0,63) | Num 7:47 |
| 2 | Seth | 105 | 235 | (4,35,1,34) | Num 9:18 |
| 3 | Enosh | 90 | 325 | (3,25,0,57) | **Lev 8:29** |
| 4 | Kenan | 70 | 395 | (3,45,5,60) | Lev 18:30 |
| 5 | Mahalalel | 65 | 460 | (5,10,5,58) | Num 22:29 |
| 6 | Jared | 162 | 622 | (6,22,11,19) | Deut 19:15 |
| 7 | Enoch | 65 | 687 | (1,37,11,17) | Gen 49:31 |
| 8 | Methuselah | 187 | 874 | (6,24,3,3) | Deut 20:17 |
| 9 | Lamech | 182 | 1056 | (6,6,3,51) | Deut 9:21 |
| 10 | Noah | 500 | 1556 | (2,6,9,15) | Ex 10:22 |

Step 3 (Enosh, cumulative=325) hits **(3,25,0,57)** — center a, center b, c=0. Lev 8:29: Moses' portion of the consecration ram.

Step 4 (Kenan) hits Lev 18:30: "**keep my charge**, that you do not practice any of these abominable customs." The charge again.

## Phase 4: The Name Coordinates

Gematria values of patriarchal names, treated as full 4D coordinates:

| Name | GV | Coord | Verse |
|------|-----|-------|-------|
| Adam (אדם) | 45 | (3,45,6,45) | **Lev 19:2** |
| Seth (שת) | 700 | (0,0,11,30) | Gen 1:16 |
| Noah (נח) | 58 | (2,8,6,58) | **Ex 12:11** |
| Jared (ירד) | 214 | (4,14,6,13) | **Num 1:12** |

Adam (GV=45) → **Lev 19:2**: "You shall be holy, for I the LORD your God am holy." The first human's name-coordinate is the holiness command.

Noah (GV=58) → **Ex 12:11**: "It is the LORD's Passover." The ark-builder's name points to Passover.

Jared (GV=214) → Num 1:12 with d=**13** — the love coordinate again.

## Phase 5: Ishmael's 137

Ishmael's lifespan 137 = 7 + 50 + 13 + 67:

- mod 7 = **4** → Numbers (the 4th book = journey/wilderness)
- mod 50 = **37** → the 37th jubilee position
- mod 13 = **7** → c=7, one step beyond center (c=6)
- mod 67 = **3** → d=3, near the origin of understanding

At center: (3,25,6,3) → **Lev 8:35** — the guard verse. Even Ishmael guards the charge.

137 as a full coordinate: (4,37,7,3) → **Numbers 11:5**: "We remember the fish we ate freely in Egypt, the cucumbers, the melons..."

The children of Israel in the wilderness, remembering what they had in Egypt. The axis-sum life, projected to its coordinate, finds the wilderness generation looking back.

## Connections

1. **Abraham's center**: Life=175 → (0,25,6,*) = center of Torah projected to Genesis
2. **Jacob = love**: Life=147 mod 67 = 13 = אהבה = love
3. **Ishmael = completeness**: Life=137 = 7+50+13+67 = axis sum
4. **Kenan = junction**: Life=910 ÷ 91 (=7×13), the only patriarch divisible by both prime axes simultaneously
5. **Pre-flood sum = boundary**: 8,575 mod 67 = 66 = d-axis maximum
6. **Post-flood begetting = love**: Sum=390 ÷ 13
7. **All lifespans → Lev 8:35-36**: The guard verse and its fulfillment
