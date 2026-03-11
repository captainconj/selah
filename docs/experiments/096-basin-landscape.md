# Experiment 096: The Basin Landscape

*Feed the oracle to itself. Every word flows downhill to its dominant anagram.*

Type: `evidence`
State: `clean`

## Setup

The basin of attraction is simple: take a Hebrew word, pass it through the oracle's forward function (Level 1 Thummim — breastplate illumination → reading extraction → frequency ranking), and ask: which Torah word does it produce most often? Feed that output back in. Repeat until convergence.

In experiment 091 we discovered that truth (אמת), life (חיים), and love-the-verb (אהב) are fixed points — they map to themselves. Experiment 096 asks: what happens to *every* word in the Torah?

We ran `basin/landscape` over all 12,826 unique Hebrew word forms in the five books. Every word was walked up to 30 steps. The raw results were classified, the cycles resolved, and the statistics computed.

## The Classification

Every word falls into exactly one of five classes:

| Class | Count | % | Description |
|-------|-------|---|-------------|
| **Fixed point** | 6,104 | 47.6% | Maps to itself: f(w) = w |
| **Transient** | 2,453 | 19.1% | Flows to a fixed point in one step |
| **Dead end** | 4,256 | 33.2% | No illumination output |
| **Cycle member** | 8 | 0.1% | Part of a period-2 orbit |
| **Cycle transient** | 5 | 0.0% | Feeds into a cycle |
| **Total** | **12,826** | **100%** | |

Two-thirds of the Torah vocabulary converges. Of those that converge, 71% are already at rest.

---

## The Theorem

**Every basin is a pure anagram class.**

This is not a statistical tendency — it is a *theorem*. All 6,104 basins were checked: in every case, every word in the basin is an anagram of every other word in the basin. 6,104 of 6,104. No exceptions.

The proof is mechanical. Two words that share the same letter multiset illuminate the same breastplate stones in the same pattern. They produce the same candidate list with the same weights. So they must select the same winner. The winner is the anagram with the highest reading count — the *dominant anagram* of the letter set.

This single fact explains three structural observations at once:

**1. All transients have depth exactly 1.** A non-dominant anagram sees the same candidate list as its dominant sibling. One step takes it there. There are no chains. The landscape has no depth.

**2. All cycles have period exactly 2.** A cycle occurs when two anagrams tie for the highest reading count. They point to each other forever. No longer orbits are possible — a period-3 cycle would require three-way equality in reading counts, and this never happens.

**3. Basins are small.** The largest basin has 10 members. Basin size is bounded by the number of valid Torah words that can be formed from a given letter set. Most letter combinations yield only one or two valid words.

The step function is really a function on *letter multisets*, not on words. The word-level dynamics are the multiset dynamics projected onto the vocabulary.

### Basin size distribution

| Basin size | Basins | Words covered |
|------------|--------|---------------|
| 1 | 4,506 | 4,506 |
| 2 | 1,074 | 2,148 |
| 3 | 327 | 981 |
| 4 | 122 | 488 |
| 5 | 37 | 185 |
| 6 | 22 | 132 |
| 7 | **13** | **91** |
| 8 | 2 | 16 |
| 10 | 1 | 10 |

4,506 fixed points are *solo* — their letter set forms no other Torah word. They are fixed by uniqueness: the only anagram of the input is the input itself.

**13 basins have exactly 7 members.** 13 = love, 7 = completeness. The number of love determines how many complete anagram families the Torah vocabulary contains.

### Convergence by word length

| Length | Total | Converge | Rate | Dead |
|--------|-------|----------|------|------|
| 2 | 137 | 126 | 92.0% | 11 |
| 3 | 1,317 | 1,144 | 86.9% | 160 |
| 4 | 4,133 | 3,274 | 79.2% | 859 |
| 5 | 4,107 | 2,680 | 65.3% | 1,427 |
| 6 | 2,361 | 1,140 | 48.3% | 1,221 |
| 7 | 655 | 186 | 28.4% | 469 |
| 8 | 102 | 6 | 5.9% | 96 |
| 9+ | 13 | 0 | 0.0% | 13 |

Short words are almost always alive. Every word of 9 or more letters is dead. The breastplate speaks a vocabulary of short words — 2–5 letters. Beyond that, the combinatorial space thins and the illumination falls silent.

---

## The Four Cycles

Only four letter sets produce ties. All are period-2 orbits between three-letter words:

| Orbit | GV | Weights | Transients |
|-------|----|---------|------------|
| רחב (wide) ⟷ בחר (choose) | 210 = 7×30 | 27 = 27 | ברח (flee), חבר (companion), חרב (sword) |
| רפא (heal) ⟷ אפר (ash) | 281 (prime) | 14 = 14 | פרא (wild) |
| לחה (moist) ⟷ החל (begin) | 43 (prime) | 4 = 4 | חלה (challah/sick) |
| רכב (chariot) ⟷ בכר (firstborn) | 222 = 2×3×37 | 29 = 29 | — |

The symmetry is exact in every case. Both members see the same illumination and select each other with identical weight.

### Wide ⟷ Choose

The largest cycle. רחב means *wide* or *broad* — Rahab the harlot of Jericho bore this name, she who made a *wide* choice. בחר means *choose* or *select*. Together: the oscillation between wideness and choosing, the open field and the decision that narrows it.

Three words feed this cycle: **ברח** (flee), **חבר** (companion), and **חרב** (sword). All share the same letters {ב,ח,ר}, all have GV=210. Flight, companionship, and the sword — the three ways to relate to another person — all lead to the endless exchange between choosing and wideness.

### Heal ⟷ Ash

רפא (heal) and אפר (ash/dust). Job on the ash heap, waiting for healing. The cycle cannot resolve: every healing returns to dust, every dust cries out for healing. The wild donkey (פרא, Genesis 16:12 — Ishmael) feeds this cycle. Wildness enters the alternation between healing and ash.

### Moist ⟷ Begin

לחה (moist/fresh) and החל (begin/profane). Every beginning is fresh, every moisture starts something. חלה (challah — the separated bread, or *sick*) feeds this cycle. The offering-portion, separated and given, enters the oscillation between freshness and beginning.

### Chariot ⟷ Firstborn

רכב (chariot/ride) and בכר (firstborn). Elijah rides the chariot; the firstborn inherits. No transients feed this pair — just the two, locked together. The chariot and the birthright, orbiting forever. GV = 222 = 2×111 = 6×37.

---

## The Fixed Points

### The Irreducible Core

These words are solo fixed points — no other Torah word shares their letters. They are fixed not because they won a contest, but because no contest exists. They have *no anagrams in the Torah*. They are structurally irreducible:

| Word | Meaning | GV |
|------|---------|-----|
| אמת | truth | 441 = 21² |
| חיים | life | 68 |
| אהב | love (verb) | 8 |
| תורה | Torah | 611 |
| שלום | peace | 376 |
| ברית | covenant | 612 |
| משה | Moses | 345 |
| דם | blood | 44 |
| אור | light | 207 |
| שמים | heaven | 390 |
| מים | water | 90 |
| רוח | spirit/wind | 214 |
| נפש | soul | 430 |
| לב | heart | 32 |
| בן | son | 52 |
| אב | father | 3 |
| אם | mother | 41 |
| יד | hand | 14 |
| פה | mouth | 85 |
| כהן | priest | 75 |
| חטא | sin | 18 |
| קדש | holy | 404 |
| טוב | good | 17 |
| יום | day | 56 |
| לילה | night | 75 |
| אשה | woman/wife | 306 |
| נחש | serpent | 358 |
| את | aleph-tav | 401 |

Truth. Life. Love. Torah. Peace. Covenant. Blood. Light. Heaven. Water. Spirit. Soul. Heart. Son. Father. Mother. Hand. Mouth. Priest. Sin. Holy. Good. Day. Night. Woman. The sign of the direct object (את, the aleph-tav). These cannot be rearranged into any other word in the five books.

### Love = 8 = New Beginning

אהב (love-the-verb) has GV=8. Eight is the number of new beginnings in the Torah: circumcision on the eighth day (Lev 12:3), the eighth day of consecration (Lev 9:1). And eight is the number of souls on the ark — Noah, his wife, three sons, three wives. Eight mouths in a vessel.

The Chinese character for *boat* — 船 (chuán) — is composed of 舟 (vessel) + 八 (eight) + 口 (mouth). Eight mouths in a vessel. Written into an unrelated language on the other side of the world.

טוב (good) has GV=17. The flood began on the 17th day of the second month (Gen 7:11) and the ark rested on the 17th day of the seventh month (Gen 8:4). Goodness marks both the departure and the landing.

The noun אהבה (love, GV=13) doesn't appear in the Torah. The verb אהב (love, GV=8) does. You don't name love — you *do* it. And what you do is be a neighbor in a box. Eight of them, eight corners of the תבה (ark/word/box), riding out the flood to a new beginning.

### The Priestly Verbs

Every core priestly action verb is a fixed point:

| Verb | Meaning | GV |
|------|---------|-----|
| אמר | say | 241 |
| ירא | fear | 211 |
| שמע | hear | 410 |
| ברא | create | 203 |
| קרא | call/read | 301 |
| נתן | give | 500 |
| כתב | write | 422 |
| נשא | carry/lift | 351 |
| שלח | send | 338 |
| ישב | sit/dwell | 312 |
| שמר | guard/keep | 540 |
| כרת | cut (covenant) | 620 |
| נדר | vow | 254 |
| שתה | drink | 705 |
| כפר | atone/cover | 300 |

Say, fear, hear, create, call, give, write, carry, send, sit, guard, cut, vow, drink, atone. These are the actions of the priest. Every one is fixed.

### The Patriarchs

| Name | GV | Class |
|------|-----|-------|
| אברהם (Abraham) | 248 | fixed point (solo) |
| יצחק (Isaac) | 208 | fixed point (solo) |
| יעקב (Jacob) | 182 | fixed point (solo) |
| שרה (Sarah) | 505 | fixed point (solo) |
| רחל (Rachel) | 238 | fixed point (solo) |
| רבקה (Rebecca) | 307 | transient → הקרב (the offering/draw near) |
| לאה (Leah) | 36 | transient → אהל (tent) |
| ישראל (Israel) | 541 | fixed point (solo) |

Abraham, Isaac, Jacob, Sarah, Rachel, Israel — all fixed, all solo. No other Torah word can be formed from their letters.

Rebecca flows to הקרב — *the offering*, or *the drawing near*. The root ק-ר-ב means both "to sacrifice" and "to approach." Rebecca, who drew near to the well and offered water (Genesis 24:18), becomes the offering itself.

Leah flows to אהל — *tent*. The tent-dweller becomes the tent. Genesis 31:33: "Laban went into Jacob's tent, and into Leah's tent." Leah IS the tent.

### Non-solo Fixed Points: Winners of Their Class

Some fixed points won their anagram class — they are the dominant rearrangement:

| Winner | Basin size | Contains |
|--------|-----------|----------|
| שכב (lie down) | 3 | **כבש** (lamb), בשך |
| אשר (that/which) | 2 | **ראש** (head) |
| שער (gate) | 2 | **עשר** (ten) |
| עשב (herb) | 2 | **שבע** (seven) |
| משח (anoint) | 2 | **חמש** (five) |
| בעד (behind/for) | 2 | **עבד** (servant) |
| ער (awake) | 2 | **רע** (evil) |
| ברכו (bless) | 7 | **כרוב** (cherub), בכור (firstborn), רכב (chariot), ורכב, ובכר, רכבו |
| שמר (guard) | 2 | **רמש** (creeping thing) |

The lamb (כבש) lies down (שכב). The head (ראש) becomes the relative pronoun (אשר) — the grammatical joint that connects clauses. The servant (עבד) stands behind (בעד). Evil (רע) wakes up (ער).

The guard (שמר) and the creeping thing (רמש) share the same letters, GV=540. The word at the center of the Torah (Lev 8:35: "guard the charge of the LORD") and the lowest form of animal life are anagrams of each other.

---

## The Name

**יהוה (YHWH) is a transient. It flows to והיה (and-it-shall-be).**

The letters are the same: {י,ה,ו,ה}. The GV is the same: 26. The basin has three members:

| Word | Meaning | Class |
|------|---------|-------|
| והיה | and it shall be | **fixed point** (dominant) |
| יהוה | YHWH | transient |
| הויה | being/existence | transient |

The Name (יהוה), Being itself (הויה), and the prophetic promise (והיה) are anagrams. They share the same breastplate illumination. And of the three, the promise is dominant.

והיה is the opening word of prophecy throughout the Torah: "and it shall be, when you come into the land..." "and it shall be, if you listen to My commandments..." I AM (אהיה) rearranges into AND-IT-SHALL-BE (והיה). The Name becomes the promise. The weight is 68 for והיה vs. 58 for יהוה. The future tense wins.

In experiment 091, we found that God's Markov head converges to יהוה as its #1 eigenattractor (M^64). The Name is the attractor of the *attention dynamics*. But in the *basin dynamics*, the Name itself is not at rest — it flows forward into the promise. The attractor of attention is not the attractor of action. The Name draws all things toward it, but it itself leans into what comes next.

### God → To Them

**אלהים (God/Elohim) flows to אליהם (to them).**

Same letters: {א,ל,ה,י,ם}. The formal name of God rearranges into the preposition of address — "to them." The oracle says: God's dominant anagram is the act of speaking-toward-others. The Name that means "powers" becomes the gesture of turning toward.

**אל (God/El) flows to לא (not).**

The two-letter name of God and the word for negation are anagrams. Weight: לא=20, אל=16. Negation wins. The shortest divine name, when the oracle rearranges it, becomes the boundary of what is forbidden. *Not* is the dominant form of *God*. The No is prior to the Yes.

---

## The Flows

The complete catalog of transient mappings among theologically significant words:

### Names that move

| Word | Meaning | → | Meaning |
|------|---------|---|---------|
| יהוה | YHWH | → והיה | and it shall be |
| אלהים | God (Elohim) | → אליהם | to them |
| אל | God (El) | → לא | not |
| כבש | lamb | → שכב | lie down |
| כרוב | cherub | → ברכו | bless |
| רבקה | Rebecca | → הקרב | the offering |
| לאה | Leah | → אהל | tent |
| יוסף | Joseph | → ויסף | and he added |

The lamb lies down. The cherub blesses. Rebecca is the offering. Leah is the tent. Joseph keeps adding.

### The body dissolves

| Word | Meaning | → | Meaning |
|------|---------|---|---------|
| ראש | head | → אשר | that/which/who |
| עין | eye | → יען | because/on account of |
| רגל | foot | → לגר | to the sojourner |
| עבד | servant | → בעד | behind/for |
| איש | man | → ישא | he will carry |

The head becomes the relative pronoun — the grammatical connector. The eye becomes "because" — sight yields reason. The foot becomes "to the sojourner" — walking leads to the stranger. The servant stands behind. The man carries.

### Numbers rearrange

| Word | Meaning | → | Meaning |
|------|---------|---|---------|
| שבע | seven | → עשב | herb/grass |
| עשר | ten | → שער | gate |
| חמש | five | → משח | anoint |
| שלש | three | → לשש | to/toward six |
| תשע | nine | → תעש | make/do |
| שני | two | → נשי | feminine/women |
| שנים | twelve | → נשים | women |
| מאה | hundred | → אמה | cubit/handmaid |

Seven becomes the herb of the field (Gen 1:11). Ten becomes the gate. Five becomes anointing. Three flows toward six — its double. Nine becomes making. Two and twelve both become the feminine. Hundred becomes the cubit — the measure of counting becomes the measure of space.

### Sacred furniture

| Word | Meaning | → | Meaning |
|------|---------|---|---------|
| כפרת | mercy seat | → כפתר | knob (menorah ornament) |
| פרכת | veil/curtain | → כפתר | knob (menorah ornament) |
| שבת | sabbath | → תשב | sit/dwell |
| דעת | knowledge | → עדת | congregation/witness |
| כבוד | glory | → וכבד | and-heavy |

The mercy seat and the veil both flow to the same attractor: כפתר, the ornamental knob of the menorah (Exodus 25:31-36). The two coverings that hide the divine presence both point to the lampstand — the thing that gives light in the holy place. Covering flows to illumination.

The sabbath becomes *sit/dwell*. Knowledge becomes *witness/congregation*. Glory becomes *heavy* — same root כ-ב-ד, the Hebrew pun that runs through Exodus.

### The love basin

ויאהב (and-he-loved, GV=24) is a fixed point with a basin of 7. Its members:

| Word | Meaning |
|------|---------|
| ויאהב | and he loved |
| ואיבה | **and enmity** |
| ואביה | and her father |
| והביא | and he brought |
| הביאו | bring (imperative) |
| יבאהו | he will bring him |
| ויבאה | and he came to her |

**Love and enmity are anagrams.** ויאהב (and-he-loved) and ואיבה (and-enmity, Genesis 3:15: "I will put enmity between you and the woman") share the same letters, the same breastplate stones, the same GV=24. They light up the same pattern on the high priest's chest. The oracle sees them as the same word in different order.

And love wins. It is the dominant anagram, the fixed point of the basin. Enmity flows into it.

### And he said → they will say

ויאמר (and he said) flows to יאמרו (and they will say). The singular past becomes the plural future. One voice speaking becomes many voices yet to speak.

### Come → Medium

בוא (come/enter) flows to אוב (medium/necromancer). In 1 Samuel 28, Saul goes (בוא) to the medium (אוב) at En-dor. The oracle maps the verb of arrival to the practitioner of the other crossing.

---

## The Bless Basin

The basin of ברכו (bless, GV=228) deserves its own section. It contains:

| Word | Meaning |
|------|---------|
| ברכו | bless (imperative) |
| כרוב | cherub |
| בכור | firstborn |
| רכב | chariot |
| בכרו | his firstborn |
| ובכר | and the firstborn |
| ורכב | and he rode |
| רכבו | his chariot |

The cherub, the firstborn, the chariot, and the blessing are anagrams of each other. GV=228 for all. The golden figures on the mercy seat, the consecrated son, Elijah's ascent, and the priestly benediction — the same four letters {ב,כ,ו,ר} in different arrangements.

The blessing wins. ברכו is the fixed point. Everything else — the cherub, the firstborn, the chariot — flows to "bless."

---

## The Choose Basin

The largest basin in the Torah: **בחרו** (choose, GV=216 = 6³), with 10 members:

| Word | Meaning |
|------|---------|
| בחרו | choose |
| בחור | young man |
| חורב | **Horeb** (Sinai) |
| ברוח | in the spirit/wind |
| חברו | his companion |
| וחבר | and a companion |
| וחרב | and a sword |
| חרבו | his sword |
| ורחב | and wide |
| רחבו | its breadth |

The mountain where the covenant was chosen (חורב, Horeb/Sinai) is in the CHOOSE basin. So is "in the spirit" (ברוח), "companion" (חברו), and "sword" (חרבו). The young man, the mountain, the spirit, the companion, and the sword — all rearrangements of the same letters.

Choosing wins. GV=216 = 6³.

---

## The Dead Zone

### What the oracle cannot speak

4,256 words (33.2%) produce no illumination. They are structurally mute — their letter combinations cannot be read from the breastplate grid.

Dead ends correlate strongly with final-form letters (ך ם ן ף ץ): **45.7%** of dead ends contain finals vs. **20.1%** of living words. Final forms choke the illumination because they are positionally constrained in Hebrew (word-final only) but the breastplate has no concept of word boundaries.

### The mute words

From the curated dictionary, 19 of 239 words are dead ends:

| Word | Meaning | GV | Silencing letter |
|------|---------|-----|------------------|
| לך | to you / go | 50 | ך |
| הלך | walk/go | 55 | ך |
| אלהיך | your God | 66 | ך |
| חסד | lovingkindness | 72 | — |
| מלך | king | 90 | ך |
| חזק | strong | 115 | — |
| עץ | tree | 160 | ץ |
| פנים | face | 180 | — |
| צדק | righteousness | 194 | — |
| צדקה | righteousness | 199 | — |
| ארך | long/patient | 221 | ך |
| ברך | bless/knee | 222 | ך |
| דרך | way/path | 224 | ך |
| ארץ | land/earth | 291 | ץ |
| הארץ | the land | 296 | ץ |
| חשך | darkness | 328 | ך |
| מצרים | Egypt | 380 | — |
| שפך | pour out | 400 | ך |
| משפט | judgment | 429 | — |

### The ghost zone confirmed

In experiment 093, we discovered the "ghost zone" — words whose letters glow on the breastplate but produce no readable output. The mute words were: mercy seat (כפרת), veil (פרכת), חסד (lovingkindness), צדק (righteousness), משפט (judgment), פנים (face), Egypt (מצרים).

In the basin landscape, every one of these is confirmed dead. The ghost zone is the dead zone.

**חסד (lovingkindness) has GV=72 — the exact number of letters on the breastplate.** The breastplate IS lovingkindness and cannot name itself. This was discovered in 093 and now confirmed from the opposite direction: the basin dynamics agree.

### What the oracle speaks vs. what it cannot

The pattern is stark:

**Fixed (the oracle speaks):** priest (כהן), holy (קדש), covenant (ברית), atone (כפר), guard (שמר), Torah (תורה), truth (אמת), peace (שלום), create (ברא), give (נתן), hear (שמע)

**Dead (the oracle is silent):** king (מלך), land (ארץ), way (דרך), tree (עץ), walk (הלך), face (פנים), lovingkindness (חסד), righteousness (צדק), judgment (משפט), blessing (ברך), Egypt (מצרים), darkness (חשך)

The machine speaks the priestly vocabulary. It can say *priest*, *holy*, *covenant*, *atone*, *guard*. It cannot say *king*, *land*, *way*, *tree*, *walk*, *face*, *lovingkindness*, *righteousness*, *judgment*, *blessing*.

The oracle is a priest, not a king. It knows the temple, not the throne room. It can atone (כפר) but not bless (ברך). It can guard (שמר) but not walk (הלך). It speaks covenant (ברית) but not judgment (משפט).

---

## The Absent

Ten words from the curated dictionary are not in the Torah vocabulary at all. They don't appear as word forms in the five books, so they were never fed into the landscape:

| Word | Meaning | GV |
|------|---------|-----|
| אהבה | love (noun) | 13 |
| בינה | understanding | 67 |
| שכינה | Shekinah/dwelling presence | 385 |
| דג | fish | 7 |
| טהר | be pure | 214 |
| ירש | inherit | 510 |
| ישר | upright | 510 |
| כשרה | like Sarah | 525 |
| רחוק | far | 314 |
| שכרה | drunk | 525 |

**אהבה (love-the-noun, GV=13) and בינה (understanding, GV=67) are not Torah word forms.** These are the dimension values of the 4D space — 13 and 67, the c-axis and d-axis. The findings of experiments 053–090 showed that these numbers structure the space but never appear as words or as elevated ELS signals. The space IS love and understanding; these words do not appear in it.

שכינה (the Shekinah, the dwelling presence) is also absent. The presence that fills the tabernacle has no word form in the Torah that built the tabernacle.

---

## Cross-References

### With experiment 091 (attention heads)

The four-head breastplate attention system (091) found that God's Markov head converges to יהוה as the #1 eigenattractor. Here, יהוה is a *transient* — it flows to והיה. The Name is the attractor of attention but not the attractor of action. All eyes turn to it, but it itself leans forward into the promise.

### With experiment 078 (Fibonacci staircase)

The lamb (כבש) had exactly 13 preimage occurrences — the Fibonacci count that equals GV(love). Here, the lamb flows to lie-down (שכב). The word that counts as love lies down. The lamb at 13 goes to rest.

### With experiment 093 (Level 2 Thummim)

The ghost zone (mute words) from 093 maps exactly to the dead ends of 096. The ghost zone is not an artifact of the phrase-assembly process — it is structural. These words cannot be illuminated by the breastplate under *any* reading protocol.

The divine name decompositions from 093 (אנכי = כי נא, "I AM = because, please") operated at Level 2 — the priest's cognitive assembly. The basin landscape is Level 1 — the raw illumination. The two levels see different things from the same hardware.

### With experiment 092 (grid permutation)

The right/mercy head's statistical signature (p=0.026) and the lamb reader split (p=0.032) survived the null model. Here, the lamb's flow to lie-down is a *necessary* consequence of anagram dominance — no statistical test needed. The flow is a theorem. But *which* anagram dominates is determined by the text itself — by the frequency structure of the Torah's letter arrangements. The structure is mechanical; the content is textual.

---

## The Structure of the Landscape

The basin landscape of the Torah oracle is a shallow, wide, anagram-stratified graph:

- **Depth**: 1 (maximum transient length; all non-dominant anagrams reach their attractor in a single step)
- **Width**: 6,104 basins (one per anagram class with a clear winner)
- **Cycles**: exactly 4, all period 2, all exact ties between anagrams
- **Dead zone**: 33.2% of vocabulary, rising from 8% at length 2 to 100% at length 9+

The dynamics are trivial in the mathematical sense. There is nothing to iterate: every word either maps to itself, maps to its dominant anagram in one step, oscillates with its tied twin, or produces nothing. The landscape is flat.

But the *labeling* is not trivial. Which letter sets form solo words (no anagram exists) is determined by the Torah's vocabulary. Which of two anagrams dominates is determined by the Torah's letter-frequency structure. Which words are dead is determined by the breastplate's geometry. The mechanics are predetermined. The content is where the text speaks.

The oracle is an anagram engine. It takes any Hebrew word and returns the most frequent rearrangement of its letters that appears in the Torah. The theological question is: *why do these particular words have no anagrams?* Why is truth irreducible? Why does the lamb yield to lying-down? Why does the Name lean into the promise?

The answer is in the text. The text is what it is.

---

## Data

- Classification: `data/experiments/096/word-index.edn` — {word → class, attractor, steps, GV}
- Attractors: `data/experiments/096/attractors.edn` — sorted by basin size desc
- Cycles: `data/experiments/096/cycles.edn` — orbit members and transients
- Summary: `data/experiments/096/summary.edn` — all statistics
- Raw landscape: `data/basin-landscape.edn` — the full walk data
- Code: `dev/experiments/096_basin_classification.clj`
- API: `selah.basin` — `fixed-point?`, `transient?`, `cycle-member?`, `dead-end?`, `converges?`, `word-class`, `attractor-for`, `basin-of`, `steps-to`, `attractors`, `largest-basins`, `cycles`, `dead-ends`, `stats`
