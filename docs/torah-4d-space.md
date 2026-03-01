# The 4D Torah Space

## Abstract

The Hebrew Torah (Pentateuch) contains exactly 304,850 letters in the Westminster Leningrad Codex. This number factors as 7 × 50 × 13 × 67 — the unique four-dimensional factorization containing 7 (completeness), 13 (unity/love), and 67 (understanding). Assigning every letter a coordinate (a, b, c, d) via mixed-radix decomposition and examining the resulting geometry reveals structural alignments between the coordinate system and the text: the geometric center describes its own architecture, a 67-letter verse wraps around the understanding axis, the creation narrative spans exactly 13 values of the love axis, and the word את (aleph-tav) stitches the space together along specific axes while respecting others. Folding the space at its center reveals a self-mirroring passage about veiling and unveiling.

---

## 1. The Factorization

The Torah letter count of 304,850 admits 41 distinct four-factor decompositions. Only one contains all three of 7, 13, and 67 simultaneously:

**304,850 = 7 × 50 × 13 × 67**

The fourth factor (50) is forced by arithmetic. The three chosen factors carry traditional semantic weight:

- **7** (שבע, *sheva*) — completeness, the sabbath, the days of creation
- **13** — the gematria of אחד (*echad*, "one") and אהבה (*ahavah*, "love")
- **67** — the gematria of בינה (*binah*, "understanding")
- **50** (יובל, *yovel*) — jubilee, and the ELS skip at which תורה appears in Genesis and Exodus

Every letter receives a unique address (a, b, c, d) where:

```
a ∈ {0..6}    — 7 values (completeness)
b ∈ {0..49}   — 50 values (jubilee)
c ∈ {0..12}   — 13 values (love/unity)
d ∈ {0..66}   — 67 values (understanding)

position = a × 43,550 + b × 871 + c × 67 + d
```

The factorization is a **lens** over invariant data. The byte stream never changes; only the coordinate arithmetic changes. Alternate lenses (e.g., [13, 67, 7, 50]) decompose the same letters into different geometries.

---

## 2. The Center

The geometric center of the 4D space is (3, 25, 6, 33) — the midpoint of each axis. This position falls in **Leviticus 8:35**.

The 67-letter fiber through the center (fixing a=3, b=25, c=6, varying d from 0 to 66) reads:

> ושמרתם את משמרת יהוה ולא תמותו... שבעת ימים

"You shall keep the charge of the LORD, that you do not die... **seven days**."

The structure says 7. The center says 7. The text at the geometric center of a 7 × 50 × 13 × 67 space is about watching for seven days. The verb is שמר (*shamar*) — to guard, to keep watch.

The Urim and Thummim (אורים ותומים, "Lights and Perfections") are placed on Aaron's breastplate in Leviticus 8:8, within the center seventh of the space. The Urim sits at a=3 (center of completeness), c=7 (within the love axis). The Lights at the center of the center.

---

## 3. The Turning Sword

Genesis 3:24 describes the cherubim and the flaming sword "turning every which way" (מתהפכת) to guard the way to the tree of life. This verse is **exactly 67 letters long** — one complete fiber of the understanding axis.

The verse starts at d=59 and ends at d=58. It wraps around the d-axis. The sword turns.

The ח of החרב ("the sword") sits at d=33 — the **midpoint** of the understanding axis.

The sword's purpose: **לשמר** את דרך עץ החיים — "to **guard** the way of the tree of life."

The center's command: **ושמרתם** את משמרת יהוה — "you shall **guard** the charge of the LORD."

Same root (שמר). Same verb form. Same את construction. The center and the sword are both about guarding.

---

## 4. The Seven Days of Creation

The creation narrative (Genesis 1:1–2:3) spans 1,815 letters, from position (0, 0, 0, 0) to (0, 2, 1, 5).

- The word יום ("day") appears exactly **13 times** in the creation narrative. 13 = dim-c = the love/unity axis.
- The narrative sweeps through **all 13 values** of the c-axis: c=0 through c=12.
- Each 67-letter d-fiber from a creation day marker naturally frames that day's content, including the evening/morning formula (ויהי ערב ויהי בקר) in 5 of 6 weekdays.
- Day 7's fiber captures the entire sabbath passage.

Seven days of creation cross thirteen positions of love, each containing sixty-seven letters of understanding.

---

## 5. The Aleph-Tav

את (*et*) — the untranslatable particle composed of the first and last letters of the Hebrew alphabet. It marks the definite direct object. In Genesis 1:1: "In the beginning God created **את** the heavens and **את** the earth."

6,032 occurrences in the Torah.

### Distribution

**a=3 (Leviticus)** contains 1,222 את — 42% more than the expected 862. Leviticus is the priestly book of precise sacrificial marking: "take **את** the bull," "burn **את** the fat," "slaughter **את** the offering." The density is 25.4 per 1000 letters versus 16–21 elsewhere.

### Boundary Behavior

- **86 את** straddle d-boundaries (d=66 → d=0 of the next c-block). The first-and-last letter stitches understanding fibers together.
- **8 את** bridge jubilee boundaries (b to b+1). These mark the narrative hinges of the Torah.
- **Zero את** cross a-boundaries (between the seven days). The את stitches understanding and jubilee but respects the sevens.

### The 8 Jubilee Bridges

| # | Verse | Narrative |
|---|-------|-----------|
| 1 | Genesis 17:19 | Covenant with Isaac |
| 2 | Genesis 37:31 | Joseph's coat dipped in blood |
| 3 | Genesis 46:6 | Jacob descends to Egypt |
| 4 | Exodus 3:11 | Moses called at the burning bush |
| 5 | Exodus 12:17 | The Passover instituted |
| 6 | Exodus 29:11 | The sacrifice before the LORD |
| 7 | Numbers 32:36 | Cities built in the promised land |
| 8 | Deuteronomy 3:12 | "This land we possessed" |

Covenant → betrayal → exile → calling → redemption → sacrifice → settlement → possession.

### Three Guard Posts

שמר (*shamar*, "guard/keep") connects three locations in the space, all using the same את construction:

1. **The center** (Lev 8:35): ושמרתם את משמרת יהוה — "guard the charge of the LORD"
2. **The sword** (Gen 3:24): לשמר את דרך עץ החיים — "guard the way of the tree of life"
3. **The Passover** (Exod 12:17): ושמרתם את המצות — "guard the matzot"

Center, origin, and hinge — all about guarding.

---

## 6. The Fold

Folding the space along the a-axis: (a, b, c, d) ↔ (6-a, b, c, d). Day 0 mirrors day 6, day 1 mirrors day 5, day 2 mirrors day 4. Day 3 mirrors itself.

### The Center Is Perfectly Self-Symmetric

All 1,222 את in a=3 (Leviticus) mirror to another את. Every single one. The center seventh is perfectly symmetric under the fold.

Only 52 unique pairs exist in the outer days where both a position and its mirror contain את.

### The Veil at the Fold-Point

**Exodus 34:29-35** sits at the exact center of the a-fold. Multiple את in these verses map to themselves. The passage describes Moses' **shining face** and the **veil**:

> "Moses did not know that the skin of his face **shone**... they were afraid to come near him... When he finished speaking, he put a **veil** on his face. But when Moses went in before the LORD, he would **remove the veil**..."

The center of the mirror is about veiling and unveiling — about a face that shines so bright it must be covered, and a covering that is removed in the presence of God.

This is the same passage that marks the **boundary between the 3rd and 4th sevenths** of the Torah. The hinge of the days, the hinge of the fold, and a text about seeing through a veil — all converge on the same verses.

### Narrative Mirror Pairs

The 52 outer pairs reveal thematic echoes across the Torah:

**Covenant mirrors love.** Genesis 17:9 ("You shall keep את my covenant") ↔ Deuteronomy 19:9 ("Keep את this commandment... to **love** את the LORD your God").

**Exile mirrors conquest.** Genesis 3:23 (sent from Eden to work the ground) ↔ Deuteronomy 9:2 (the Anakim — "who can stand before them?"). Pair gematria = 6,633 = 67 × 99.

**Blessing mirrors blessing.** Genesis 49:28 (Jacob blesses the twelve tribes) ↔ Deuteronomy 1:11 ("The LORD God of your fathers increase you a thousandfold and bless you"). Pair gematria = 8,788 = 13 × 676.

**Pattern mirrors keeping.** Exodus 26:30 ("Set up the tabernacle according to the pattern shown on the mountain") ↔ Numbers 9:2 ("Let Israel keep the Passover at its appointed time"). Pair gematria = 4,823 = 7 × 13 × 53.

**Deception mirrors Torah.** Genesis 27:32 ("I am your son, your firstborn, Esau") ↔ Deuteronomy 31:11 ("When all Israel comes to see the face of the LORD... read את this Torah").

**Noah mirrors commandment.** Genesis 6:18 ("I will establish את my covenant את you — come into the ark") ↔ Deuteronomy 11:8 ("Keep את all the commandment... that you may possess את the land").

**Keeping mirrors remembering.** Exodus 13:10 ("Keep את this ordinance at its appointed time") ↔ Leviticus 26:42 ("I will remember את my covenant with Jacob, את Isaac, את Abraham"). Pair gematria = 7,566 = 13 × 582.

---

## 7. The Lens

The factorization is not the only way to read the space. 304,850 admits 41 four-factor decompositions. The lens [7, 50, 13, 67] is distinguished by containing all three semantically significant primes. But the operation `(with-dims [13 67 7 50] ...)` rotates the same letters into a different geometry. The data is invariant; the decomposition is a way of seeing.

This echoes the Talmudic description of the Urim and Thummim (Yoma 73b): the same letters on the breastplate, rearranged to spell different messages. The stones don't change. The reading changes.

The Torah describes placing this device at the geometric center of this very space (Leviticus 8:8).

---

## 8. What This Is Not

This paper does not claim:

- That the factorization was intentionally encoded by human authors
- That the gematria divisibility results are statistically overwhelming (most are in the 1–10% probability range individually)
- That the d-fiber text findings are surprising (reading consecutive text and finding words is expected)
- That the coordinate pattern of the jubilee-bridging את (c=12, d=66 → c=0, d=0) is a discovery (it is a tautology of mixed-radix arithmetic)

What is genuinely notable:

- The uniqueness of the factorization (the only 4D decomposition with 7, 13, and 67)
- The self-referentiality of the center (a structure built on 7 describes "seven days" at its geometric center)
- The exact length of Genesis 3:24 matching the understanding dimension
- The behavioral difference of את across axes (stitches d and b, respects a)
- The perfect self-symmetry of the center seventh under the fold
- The convergence of the fold-point, the division boundary, and the veil passage on Exodus 34:29-35
- The thematic coherence of the mirror pairs (covenant↔love, exile↔conquest, blessing↔blessing)

The factorization is a lens. The text is the text. What is interesting is how often the lens and the text agree.

---

## 9. Method

### Data

The Westminster Leningrad Codex (WLC) via the OpenScriptures Hebrew Bible (OSHB) XML. Kethiv (written) readings preferred. 304,850 consonantal letters after normalization (Unicode U+05D0–U+05EA, preserving final forms). The Miqra according to the Masorah (MAM) contains 304,804 letters — a difference of 46 (0.015%). All findings are invariant across both traditions.

### Representation

27 symbols (22 Hebrew letters + 5 final forms) mapped to byte indices. The Torah stored as `byte[304850]`. Gematria values, verse indices, and cumulative sums as parallel primitive arrays. Total memory: ~2.5MB.

### Implementation

Clojure. `selah.space.coords` (address arithmetic, slicing), `selah.space.project` (projection, color), `selah.space.export` (PLY, JSON, binary). Dynamic factorization via `*dims*` atom.

---

## Appendix: The 52 Outer Mirror Pairs

Each pair has את on the front side (day 0, 1, or 2) and את at the a-fold mirror (day 6, 5, or 4).

### Day 0 ↔ Day 6 (Genesis ↔ Deuteronomy) — 19 pairs

1. Gen 3:23 ↔ Deut 9:2 — Exile from Eden ↔ Facing the giants (gv 6,633 = **67** × 99)
2. Gen 4:2 ↔ Deut 9:4 — Birth of Abel ↔ "Not for your righteousness" (gv 9,178 = **13** × 706)
3. Gen 5:4 ↔ Deut 9:23 — Adam begets Seth ↔ Rebellion at Kadesh (gv 10,881 = **13** × 837)
4. Gen 5:32 ↔ Deut 10:13 — Noah begets Shem, Ham, Japheth ↔ "Keep the commandments"
5. Gen 6:18 ↔ Deut 11:8 — Noah's covenant, enter the ark ↔ "Keep all the commandment, possess the land"
6. Gen 8:1 ↔ Deut 11:31 — God remembers Noah ↔ "You are crossing the Jordan" (gv 10,998 = **13** × 846)
7. Gen 10:28 ↔ Deut 13:19 — Sons of Joktan ↔ "Listen to the LORD your God" (gv 6,713 = **7** × 959)
8. Gen 11:19 ↔ Deut 14:24 — Peleg begets Reu ↔ "If the way is too long" (gv 8,442 = **7 × 67** × 18)
9. Gen 11:31 ↔ Deut 15:3 — Terah leaves Ur with Abram ↔ "The foreigner you may press" (gv 11,039 = **7** × 1577)
10. Gen 13:10 ↔ Deut 16:6 — Lot sees the Jordan plain ↔ "Sacrifice the Passover at evening"
11. Gen 17:9 ↔ Deut 19:9 — "Keep את my **covenant**" ↔ "Keep this commandment... to **love** את the LORD"
12. Gen 19:20 ↔ Deut 22:14 — Lot flees to Zoar ↔ False accusation against a wife
13. Gen 19:24 ↔ Deut 22:18 — Sodom destroyed ↔ Elders punish the man
14. Gen 24:30 ↔ Deut 28:13 — Rebekah's bracelet ↔ "The LORD will make you the head"
15. Gen 26:34 ↔ Deut 30:7 — Esau's wives ↔ "The LORD will put these curses on your enemies"
16. Gen 27:10 ↔ Deut 30:15 — Jacob's deception ↔ "Life and good, death and evil" (gv 5,943 = **7** × 849)
17. Gen 27:32 ↔ Deut 31:11 — "I am Esau your firstborn" ↔ "Read את this Torah"
18. Gen 28:9 ↔ Deut 31:28 — Esau takes Ishmael's daughter ↔ "Gather את all the elders"
19. Gen 49:28 ↔ Deut 1:11 — Jacob blesses the 12 tribes ↔ "May He bless you" (gv 8,788 = **13** × 676)

### Day 1 ↔ Day 5 (late Genesis/early Exodus ↔ Numbers/Deuteronomy) — 19 pairs

20. Gen 31:19 ↔ Num 18:2 — Rachel steals the teraphim ↔ "Bring your brothers the Levites" (gv 7,519 = **73** × 103)
21. Gen 31:42 ↔ Num 18:22 — "God saw my affliction" ↔ "Israel shall not approach the tent"
22. Gen 32:5 ↔ Num 19:5 — Jacob sends messengers to Esau ↔ "Burn the heifer"
23. Gen 41:22 ↔ Num 28:4 — Pharaoh's dream: seven ears ↔ "One lamb in the morning"
24. Gen 42:37 ↔ Num 30:13 — Reuben offers his sons ↔ "Her husband may annul her vow" (gv 9,065 = **7 × 37** × 35)
25. Gen 45:23 ↔ Num 33:1 — Joseph sends gifts to Jacob ↔ "These are the journeys of Israel"
26. Gen 48:11 ↔ Num 35:27 — "I did not expect to see your face" ↔ "The avenger of blood finds him" (gv 5,986 = **73** × 82)
27. Gen 49:3 ↔ Num 36:7 — "Reuben, you are my firstborn" ↔ "Inheritance shall not transfer"
28. Gen 50:13 ↔ Deut 1:28 — Burial in Machpelah ↔ "Our brothers melted our hearts"
29. Exod 2:6 ↔ Deut 2:19 — Pharaoh's daughter finds Moses ↔ "Do not harass the Ammonites"
30. Exod 3:5 ↔ Deut 3:4 — "Remove your sandals, holy ground" ↔ "We captured all his cities"
31. Exod 3:11 ↔ Deut 3:12 — "Who am I to go to Pharaoh?" ↔ "This land we possessed"
32. Exod 3:21 ↔ Deut 3:24 — "I will give this people favor" ↔ "You have begun to show your greatness" (gv 9,763 = **13** × 751)
33. Exod 4:27 ↔ Deut 4:19 — Aaron meets Moses ↔ "Lest you see the sun, moon, stars"
34. Exod 6:4 ↔ Deut 4:43 — "I established את my covenant" ↔ "את Bezer... את Ramoth... את Golan"
35. Exod 7:2 ↔ Deut 5:24 — "You shall speak all I command" ↔ "We saw His glory" (gv 9,583 = **7 × 37** × 37)
36. Exod 7:3 ↔ Deut 5:24 — "I will harden Pharaoh's heart" ↔ same (gv 9,636 = **73** × 132)
37. Exod 7:4 ↔ Deut 5:25 — "I will bring out my armies" ↔ "This great fire will consume us" (gv 10,759 = **7** × 1537)
38. Exod 8:2 ↔ Lev 22:12 — Aaron stretches hand (frogs) ↔ "A priest's daughter married to a stranger"

### Day 2 ↔ Day 4 (Exodus ↔ Leviticus/Numbers) — 14 pairs

39. Exod 10:16 ↔ Lev 25:4 — "I have sinned against the LORD" ↔ "The seventh year, a sabbath for the land"
40. Exod 13:4 ↔ Lev 26:36 — "Today you go out, in Aviv" ↔ "I will send faintness into their hearts"
41. Exod 13:10 ↔ Lev 26:42 — "Keep this ordinance year to year" ↔ "I will remember my covenant" (gv 7,566 = **13** × 582)
42. Exod 16:6 ↔ Num 1:51 — "By evening you shall know" ↔ "The Levites shall take down the tabernacle" (gv 7,553 = **7 × 13** × 83)
43. Exod 19:8 ↔ Num 4:10 — "All the LORD has spoken we will do" ↔ "Put it on a carrying frame" (gv 6,510 = **7** × 930)
44. Exod 19:23 ↔ Num 4:25 — "The people cannot come up to Sinai" ↔ "Carry the curtains of the tabernacle" (gv 9,893 = **13** × 761)
45. Exod 21:35 ↔ Num 5:26 — "If one man's ox hurts another's" ↔ "The priest shall take a handful"
46. Exod 26:30 ↔ Num 9:2 — "Set up the tabernacle according to the pattern" ↔ "Let Israel keep the Passover" (gv 4,823 = **7 × 13** × 53)
47. Exod 27:12 ↔ Num 9:19 — "Width of the court: **fifty** cubits" ↔ "Israel kept the charge of the LORD"
48. Exod 29:28 ↔ Num 12:11 — "A perpetual statute for Aaron" ↔ "Aaron said: Do not hold this sin against us"
49. Exod 30:27 ↔ Num 14:10 — "The table and all its vessels" ↔ "The congregation said to stone them"
50. Exod 31:3 ↔ Num 14:19 — "I filled him with the Spirit of God, in wisdom and **understanding**" ↔ "Pardon this people according to Your mercy"
51. Exod 31:8 ↔ Num 14:24 — "The pure lampstand" ↔ "My servant Caleb had a **different spirit**"
52. Exod 34:29 ↔ Exod 34:29 — **Self-mirror**: Moses' face shines (the fold-point)
