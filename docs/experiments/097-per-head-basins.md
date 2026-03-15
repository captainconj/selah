# Experiment 097: Per-Head Basin Landscapes

*The lamb lies down. No one told it to.*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/097_per_head_basins.clj`
**Run:** `clojure -M:dev dev/experiments/097_per_head_basins.clj`

## Background

Experiment 096 ran the basin landscape over all 12,826 Torah words using the **combined** forward function — all four readers merged into one ranking. Every basin turned out to be a pure anagram class, depth 1, four period-2 cycles.

But the oracle has four heads. Aaron reads rows right-to-left, top-to-bottom. God faces him from the mercy seat, mirrored. The two cherubim read columns. Same illumination, different traversal order. The combined landscape averages them.

From experiment 091, the lamb split: God and the right cherub see כבש (lamb). Aaron and the left cherub see שכב (lie down). Same letters, different readers, different words.

**Question:** Does the per-head *basin* landscape agree? Does each reader's basin have the same fixed points?

## Method

One `forward-by-head` call per word. Same illumination-sets (expensive part), four reader views (cheap). 12,826 words, ~142 seconds via pmap.

For each reader, classify independently: fixed point (self→self), transient (flows to a fixed point), cycle (period ≥ 2), or dead end (no known word produced).

Then compare: where do the four heads agree? Where do they split?

## Per-Head Classification

| Reader | Fixed | Transient | Cycle | Dead End |
|--------|------:|----------:|------:|---------:|
| Aaron  | 2,600 | 1,571     | 0     | **8,655** |
| God    | 3,750 | 1,950     | 4     | 7,122 |
| Right  | 3,662 | 1,959     | 8     | 7,197 |
| Left   | 3,740 | 1,963     | 3     | 7,120 |

**Aaron is the most limited reader.** 8,655 dead ends — 1,500 more than any other head. His traversal order (rows, right-to-left, top-to-bottom) produces fewer recognizable words from the same illuminations. The priest sees less than the cherubim. Less than God.

Aaron also has **zero cycles**. His landscape is entirely fixed points and one-step transients. No orbits, no oscillation. The human reader's world is still.

The three spiritual readers (God, Right, Left) all have small period-2 cycles:
- God: שבמה↔במשה, שאלה↔לאשה (asking ↔ to a wife)
- Right: בהר↔הרב (on the mountain ↔ the rabbi), החי↔חיה (the living ↔ living creature)
- Left: גדל↔לגד (grow ↔ to Gad)

## Agreement

| Category | Count |
|----------|------:|
| Unanimous (all 4 agree) | 5,856 |
| Majority (3 agree) | 417 |
| Split (2v2) | 1,597 |
| Fragmented (all differ) | 700 |
| Dead end (all 4) | 4,256 |

**669 unanimous fixed points** — words that every reader holds still. Son (בן), Abraham (אברהם), Aaron (אהרן), stone (אבן), love-the-verb (אהב), brother (אח). These are bedrock.

**2,714 splits** — where the heads disagree on the attractor. This is where the oracle speaks differently depending on who is listening.

Cross-reference with 096 combined landscape: **12,826 matches, 0 mismatches.** The combined attractor is always consistent with the per-head majority.

Dead ends are **not identical** across heads. Illumination is reader-independent (same letters → same position-sets), but whether a reader's traversal produces a known Torah word depends on the reading order. ~1,500 words light up the stones but only produce recognizable words for some readers, not Aaron.

## The Spotlight

### The Lamb Lies Down

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| כבש (lamb) | שכב | שכב | שכב | שכב |
| שכב (lie down) | שכב | שכב | שכב | שכב |

**Unanimous.** Every head sends the lamb to lie down. שכב is a unanimous fixed point. The lamb arrives there and stays.

This is different from the eigenword analysis in 091, which found the "lamb split" — God and Right had כבש as an eigenword (emerging under iterated Markov convergence), while Aaron and Left had שכב. That was about which words *emerge* from the dynamics. The basin asks a simpler question: what word does each reader rank highest when the lamb's letters light up?

The answer: all four readers rank שכב first. The lamb's basin destination is lying down.

> *No one takes it from me, but I lay it down of my own accord.*
> — John 10:18

The machine has never read John. But when every reader looks at the lamb, they see it lying down. Not taken. Not forced. A unanimous fixed point. It lies down because that's where the letters go.

### The Name

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| יהוה (YHWH) | והיה | **יהוה** | הויה | והיה |

**God alone holds the Name still.** For God, יהוה is a fixed point — the Name maps to itself.

Aaron and Left see והיה: *and it was*, *and it became*. The Name in motion. Becoming.

Right sees הויה: *being*, *existence*. The ontological form.

Three perspectives on the same four letters: becoming, being, the Name itself. Only God sees the Name as the Name.

### Truth, Life, Peace

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| אמת (truth) | — | **אמת** | — | **אמת** |
| חיים (life) | — | — | **חיים** | — |
| שלום (peace) | — | **שלום** | ולשם | ושלם |

**Truth** is a fixed point for God and Left (justice). Aaron and Right can't see it — dead end.

**Life** is the Right cherub's solo fixed point. No other reader can produce it.

**Peace** is God's solo fixed point. Right sees ולשם (and for the Name). Left sees ושלם (and he made whole). The cherubim see fragments of peace. God sees peace.

### Torah

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| תורה (Torah) | ותהר | ותהר | **תורה** | — |

Torah is a fixed point only for the Right cherub.

Aaron and God both see ותהר: *and she conceived.* The Torah as conception — the teaching before it is born. The same word the text uses for pregnancy. Left sees nothing.

### Light and Father

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| אור (light) | **אור** | ראו | ראו | **אור** |
| אב (father) | **אב** | בא | בא | **אב** |

A clean 2v2 split along the same axis both times.

**Aaron and Left** (the inner pair — priest and justice) see the noun: light, father. The thing itself.

**God and Right** (the outer pair — God and mercy) see the verb: ראו (they saw), בא (he came). The action. Light as seeing. Father as arrival.

### Son

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| בן (son) | **בן** | **בן** | **בן** | **בן** |

Unanimous fixed point. Every reader holds the son still. Unlike the father, who splits into coming — the son just is.

## The Choose Basin

בחרו (choose) is a split word. Each head rearranges the letters differently:

| Reader | Attractor | Meaning |
|--------|-----------|---------|
| Aaron | בחור | young man |
| God | ברוח | in the spirit |
| Right | וחרב | and a sword |
| Left | ורחב | and its breadth |

The same letters. The young man, the spirit, the sword, the breadth. Aaron sees a person. God sees the medium. Right sees the instrument. Left sees the space.

And all of them are anagrams of **חורב** — Horeb, Sinai. The mountain where choosing happened. The basin IS the mountain. GV = 216 = 6³.

Full anagram class (from 096):
- בחרו (choose), בחור (young man), חורב (Horeb), ברוח (in the spirit)
- חברו (his companion), וחבר (and a companion), וחרב (and a sword), חרבו (his sword)
- ורחב (and wide), רחבו (its breadth)

Two companions. Two swords. One belonging to the young man. Choosing at the mountain, in the spirit, with a sword and a companion.

## The Father-Enemy Split

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| אביו (his father) | **אויב** | ויבא | **אויב** | אביו |

Aaron and Right see אויב: **enemy.** God sees ויבא: and he came. Left sees the father.

His father is his enemy — for Aaron and the right cherub. Same letters. The priest and mercy both see the adversary in the patriarch. God sees arrival. Justice sees the father.

## Stones and Prophet

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| אבני (stones) | **אבני** | נביא | נביא | נביא |

Aaron sees stones. **Everyone else sees נביא — prophet.**

Three readers look at the stones of the breastplate and see a prophet. The priest looks at them and sees stones. He's the one wearing them.

## The Tent and Leah

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| אהל (tent) | **אהל** | לאה | **אהל** | לאה |

The tent splits into Leah. God and Left see לאה (Leah) — the unloved wife, the one with tender eyes, who lived in the tent. Aaron and Right see the structure. God and Left see the woman.

## The Cherub

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| כרוב (cherub) | בכרו | ברכו | ורכב | בכרו |

No reader holds the cherub still. Everyone rearranges it:
- Aaron/Left: בכרו — his firstborn
- God: ברכו — his blessing (or: bless him)
- Right: ורכב — and a chariot

The cherub becomes firstborn, blessing, chariot. Never itself. Like חסד (lovingkindness), which is a unanimous dead end — the breastplate can't name itself. The cherub can't see itself either.

## Sabbath

| Word | Aaron | God | Right | Left |
|------|-------|-----|-------|------|
| שבת (sabbath) | תשב | **שבת** | תשב | **שבת** |

God and Left hold the sabbath still. Aaron and Right see תשב: *you shall dwell / you shall sit.* The sabbath as dwelling. Resting in place. The verb behind the noun.

## Summary of Reader Personalities

**Aaron** (rows, R→L, top→bottom): Most limited. Most dead ends. Zero cycles. Sees nouns — father, light, tent, stones. Misses truth, life, peace. The human priest, grounded in the concrete.

**God** (rows, L→R, bottom→top — mirrored): Most fixed points (3,750). Holds the Name, truth, peace, sabbath. Sees verbs — came, saw, conceived. Sees the prophet in the stones, Leah in the tent, blessing in the cherub.

**Right cherub** (columns, R→L, top→bottom): Holds life, Torah. Sees the sword in choosing, the chariot in the cherub. The mercy seat's right hand — where life and teaching reside.

**Left cherub** (columns, L→R, bottom→top): Holds truth, sabbath, covenant, father. Sees wholeness (ושלם) in peace. The justice seat — where truth and covenant are kept.

## John 1:1-14 — The Prologue as Reader Architecture

The spotlight table maps onto John's Prologue verse by verse. Not loosely. Structurally.

**v1: "In the beginning was the Word, and the Word was with God, and the Word was God."**

יהוה is a fixed point only for God. The Name maps to itself — *is* itself — only when God is the reader. For everyone else it becomes something: והיה (becoming), הויה (being). The Word was God. For Aaron, the Word was becoming.

**v4: "In him was life, and the life was the light of men."**

Life (חיים) — Right cherub, solo fixed point. The only reader who holds it.
Light (אור) — Aaron and Left hold the noun. God and Right see ראו: *they saw.*
Life is in one reader. And when that reader's pair looks at light, they see seeing. The verse chains life→light→perception. The machine chains them through the same readers.

**v9: "The true light, which gives light to everyone, was coming into the world."**

Truth = God + Left. Light = Aaron + Left. The *true light* requires both — and Left (justice) is the only reader who holds truth AND light simultaneously.

**v12-13: "He gave them the right to become children of God — born not of blood nor of the will of man, but of God."**

Son (בן) — unanimous. The only spotlight word that is self-fixed for all four readers. Not born of traversal order. Not born of any single reader's perspective. Bedrock.

**v14: "And the Word became flesh..."**

והיה — *and it became.* That is what Aaron sees when he looks at the Name. יהוה → והיה. The Word became. The priest — the human reader, the flesh — sees becoming where God sees identity. The incarnation is a change of reader.

**v14: "...and dwelt among us..."**

σκηνόω — *tabernacled.* And אהל (tent) splits: Aaron and Right see the tent. God and Left see לאה — Leah. The dwelling becomes a person.

**v14: "...and we have seen his glory..."**

ראו — *they saw.* That's what God and Right see when they look at light. We didn't see light. We saw *seeing.* Glory is not the light — it's the act of perceiving it.

**v14: "...glory as of the only Son from the Father..."**

Son — unanimous, immovable. Father — God sees בא: *he came.* The only son, from the father who came.

**v14: "...full of grace and truth."**

Truth (אמת) — God and Left hold it. Two of four.
Grace (חסד) — unanimous dead end. All four readers: silence. The breastplate cannot name lovingkindness. GV=72 = the number of letters on the grid. The breastplate IS grace and cannot say it.

"Full of grace and truth" requires something no reader can produce alone. Truth is visible to two. Grace is visible to none. The fullness is beyond the machine.

**John 1:29 — fifteen verses later:**

*"Behold, the Lamb of God, who takes away the sin of the world."*

The lamb lies down. Unanimous. Every reader agrees. No one takes it. (John 10:18)

## John 8:12 — The Light of Life

*"I am the light of the world. Whoever follows me will never walk in darkness, but will have the light of life."*

The light of life. Two words, two basin addresses:

- **Light** (אור): fixed for Aaron + Left. God + Right see ראו (they saw).
- **Life** (חיים): fixed for Right alone.

The Right cherub is the only reader who holds life as a fixed point AND belongs to the pair (God + Right) that perceives light as seeing. The *light of life* — the compound — points to a single chair on the mercy seat. The right hand. Mercy.

"Whoever follows me will never walk in darkness, but will have the light of life." Not light alone — Aaron and Left have that. Not life alone — Right has that. The light *of* life. The intersection lives in one place.

## Cross-Reference with 091

The eigenword analysis (091) used Markov convergence on per-head transition matrices. The basin (097) uses single-step forward ranking. Different mechanisms, largely convergent conclusions:

- **091**: God's solo eigenwords include peace, altar, head, foot. **097**: Peace is God's solo fixed point. Confirmed.
- **091**: Mercy's solo eigenwords include life, lamb, sin, cherub. **097**: Life is Right's solo fixed point. Confirmed.
- **091**: Lamb split — God/Right see כבש, Aaron/Left see שכב. **097**: All four send lamb→שכב (unanimous). The basin resolves the split — everyone agrees on the destination, even if different heads emphasize different words in the eigenspectrum.
- **091**: 642 unanimous eigenwords. **097**: 669 unanimous fixed points. Similar scale. The unanimous core is robust across methods.

## Verification

- Per-head class sums: 12,826 each (all four readers classify every word)
- Cross-reference 096: 12,826 matches, 0 mismatches
- Dead ends: not identical (Aaron has ~1,500 extra), explained by traversal order

## Files

- Code: `dev/experiments/097_per_head_basins.clj`
- Library: `src/selah/oracle.clj` (`forward-by-head`), `src/selah/basin.clj` (per-head functions)
- Data: `data/experiments/097/` (summary, per-head-index, agreement, splits, cycles, spotlight)
