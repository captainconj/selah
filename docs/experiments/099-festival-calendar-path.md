# Experiment 099: Festival Calendar Path

*Does the liturgical calendar trace a path through the 4D Torah space?*

Type: `evidence`
State: `mixed`

**Code:** `dev/experiments/099_festival_calendar_path.clj`
**Run:** `clojure -M:dev dev/experiments/099_festival_calendar_path.clj`

## Setup

The Torah prescribes a liturgical calendar with specific numbers: Passover day 14, Unleavened Bread 7 days, Omer count 50 days, Trumpets day 1 month 7, Atonement day 10 month 7, Sukkot 7 days with 13→7 bulls. These numbers are axis values. Do they navigate the space?

## Phase 1: Festival Numbers as Coordinates

Every festival number was placed on each axis it fits (a∈0..6, b∈0..49, c∈0..12, d∈0..66). 47 total lookups.

The striking pattern: every festival number placed on the d-axis at center (3,25,6,d) lands on **Lev 8:35** — the guard verse. This is true for d=1, d=7, d=8, d=10, d=14, d=15, d=50. The entire festival calendar, read as d-coordinates, sits inside the guard command.

No direct hits on festival-prescribing chapters (Lev 23, Num 28-29) from Phase 1 — but Phase 4 reveals something better.

## Phase 2: The Sukkot Bull Descent

Sukkot prescribes 13,12,11,10,9,8,7 bulls over seven days (Numbers 29). These are c-axis values minus one (c∈0..12). Walking c=12 down to c=6 at center (a=3, b=25, d=33):

| Day | Bulls | c | Verse | Letter |
|-----|-------|---|-------|--------|
| 1 | 13 | 12 | Lev 9:7 | א |
| 2 | 12 | 11 | Lev 9:6 | ה |
| 3 | 11 | 10 | Lev 9:4 | י |
| 4 | 10 | 9 | Lev 9:3 | נ |
| 5 | 9 | 8 | Lev 9:2 | ל |
| 6 | 8 | 7 | Lev 9:1 | ש |
| 7 | 7 | 6 | **Lev 8:35** | ו |

**The descent walks backward through the consecration narrative** — from Aaron's first offering (Lev 9:7) through the preparations (9:6→9:1) and arrives at **Lev 8:35** on the seventh day.

Lev 9:7 says: "Go to the altar and offer your sin offering." The bulls walk backward from the first sacrifice to the guard command.

The bull sum is 70. Position 70 in the Torah = **Genesis 1:2** — "the earth was formless and void."

### Genesis Projection

The same c-walk at a=0 (Genesis) traces Genesis 19:8→19:1 — Lot's story in Sodom, the angels arriving to rescue. Seven days, backward, ending at the angels' arrival.

## Phase 3: The Calendar as a Coordinate Path

Mapping month→a (mod 7) and day→d:

| Event | Month | Day | (a,25,6,d) | Verse |
|-------|-------|-----|------------|-------|
| Passover | 1 | 14 | (1,25,6,14) | Gen 43:13 |
| Unleavened begins | 1 | 15 | (1,25,6,15) | Gen 43:13 |
| Unleavened ends | 1 | 21 | (1,25,6,21) | Gen 43:14 |
| **Shavuot** | **3** | **6** | **(3,25,6,6)** | **Lev 8:35** |
| Trumpets | 7→0 | 1 | (0,25,6,1) | Gen 18:33 |
| Atonement | 7→0 | 10 | (0,25,6,10) | Gen 19:1 |
| Sukkot begins | 7→0 | 15 | (0,25,6,15) | Gen 19:1 |
| Sukkot ends | 7→0 | 21 | (0,25,6,21) | Gen 19:1 |
| Shemini Atzeret | 7→0 | 22 | (0,25,6,22) | Gen 19:1 |

**Shavuot (Pentecost)** — month 3, day 6 → (3,25,6,6) — is the festival closest to dead center (3,25,6,33). It differs only on d. The festival of revelation (giving of Torah at Sinai) sits at the center slab of the Torah space.

The 7th-month festivals (Trumpets through Shemini Atzeret) all map to a=0 (since 7 mod 7 = 0), landing in Genesis. Trumpets → Gen 18:33 ("the LORD went His way"), Atonement through Shemini → Gen 19:1 (angels arriving at Sodom). The autumn festivals project to the judgment narratives.

## Phase 4: Festival Verse Self-Reference

The most striking finding. We located each festival prescription verse in Leviticus 23 and read its 4D coordinate:

| Verse | Prescribes | Position | Coord | Self-Reference |
|-------|-----------|----------|-------|---------------|
| Lev 23:5 | Day 14 | 176349 | (4,2,6,**5**) | d=5 (verse number) |
| Lev 23:6 | 7 days | 176389 | (4,2,6,**45**) | — |
| **Lev 23:15** | **50 days** | 176863 | (4,3,0,**50**) | **d = 50** |
| Lev 23:16 | 50 days | 176921 | (4,3,1,41) | — |
| Lev 23:24 | Day 1 | 177406 | (4,3,8,57) | — |
| **Lev 23:27** | **Day 10** | 177520 | (4,3,**10**,37) | **c = 10** |
| Lev 23:34 | Day 15 | 177868 | (4,4,2,**50**) | d=50 again |
| Lev 23:36 | Day 8 | 177963 | (4,4,4,11) | — |

**Two exact self-references:**

1. **Lev 23:15** prescribes counting **50** days (the Omer count to Pentecost). Its d-coordinate IS **50**. The verse that commands "count fifty days" sits at position d=50 in the understanding axis.

2. **Lev 23:27** prescribes the **10th** day of the 7th month (Yom Kippur). Its c-coordinate IS **10**. The verse that says "on the tenth day" sits at c=10 on the love axis.

And **Lev 23:34** (Sukkot) also has d=**50** — the jubilee number again, at the festival that comes after the Day of Atonement. Freedom follows forgiveness.

All Leviticus 23 verses share a=**4** (Numbers). The festival chapter sits in the 4th a-slab, not the 3rd (Leviticus). This is because Leviticus 23 falls past the midpoint of the full text, pushing it into the Numbers region of the a-axis.

## Connections

1. **The Sukkot descent is the consecration in reverse**: 13 bulls (love) → 7 bulls (completeness), walking from altar to guard verse
2. **Shavuot = near-center**: The Torah-giving festival at (3,25,6,6) — center on every axis but d
3. **Two self-referencing verses**: Lev 23:15 (count 50) at d=50, Lev 23:27 (10th day) at c=10
4. **Autumn festivals → Genesis judgment**: Month 7 mod 7 = 0 → a=0 → Sodom narrative
5. **Bull sum = 70 → Gen 1:2**: "formless and void" — before creation, before the count
6. **Every festival d-value → Lev 8:35**: The guard verse contains the entire calendar
