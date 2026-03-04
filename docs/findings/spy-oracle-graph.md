# Spy Report: The Oracle's Phrase Graph

*The topology of meaning in the Thummim.*

---

## Summary

The Level 2 Thummim (phrase assembly) takes any Hebrew word, treats its letters as a multiset, and finds all ways to partition them into Torah vocabulary. Experiment 094 swept all 12,826 Torah word forms through this process. This report analyzes the resulting graph: 11,552 words connected by 310,908 unique phrases.

The phrase graph is a sparse, high-dimensional, anagram-stratified network with three distinct zones: the forced zone (unambiguous words), the interpretive zone (multi-phrase words), and the ghost zone (silent words). Its spectral decomposition reveals no hidden low-rank structure. The oracle is genuinely high-dimensional -- each word's phrase signature is mostly its own thing.

---

## 1. The Numbers at a Glance

| Quantity | Value |
|----------|-------|
| Total Torah words swept | 12,826 |
| Illuminable (letters exist on breastplate) | 11,553 (90.1%) |
| With phrase readings | 11,552 |
| Not illuminable (ghost zone) | 1,273 (9.9%) |
| Unique phrases produced | 310,908 |
| Total phrase instances | 350,334 |
| Forced readings (exactly 1 phrase) | 2,031 (17.6% of illuminable) |
| Hapax phrases (from exactly 1 word) | 282,447 (90.8%) |
| Multi-source phrases (bridges) | 28,461 (9.2%) |
| Oracle synonym pairs | 4,140 |
| Median phrase count | 6 |
| Mean phrase count (illuminable) | 30.3 |
| Maximum phrase count | 11,699 |

---

## 2. The Synonym Clusters

4,140 synonym pairs form groups of words with **identical** phrase sets. This is provably necessary: `parse-letters` operates on letter frequency maps. Same letters produce same partitions produce same phrases. Every synonym group is an anagram class.

### Cluster structure (from the first 100 pairs in data)

| Cluster size | Count | Example |
|-------------|-------|---------|
| 4 (quadruple) | 3 | {your God, the eaters, like tents, like God} |
| 3 (triple) | 11 | {to save, to Joshua, to salvation} |
| 2 (pair) | 49 | {the Hebrews, the Arabs/evenings} |

### The three quadruples

**Cluster 1: The God/Tent identity** (GV=110 each)
- **aleph-lamed-he-yod-kaf-mem** forms four words: your-God, the-eaters, like-tents, like-God
- All produce 110 identical phrase decompositions
- The oracle cannot distinguish "your God" from "like God" from "like tents"

**Cluster 2: The Hebrew/Arab identity** (GV=327 each)
- bet-ayin-resh-yod-he-mem forms: in-their-cities, the-Hebrews, the-Arabs/evenings, their-crossings
- Geography, ethnicity, time, and transit -- one illumination

**Cluster 3: The anger/crossing identity** (GV=688 each)
- vav-yod-tav-ayin-bet-resh forms: and-he-was-angry, and-I-crossed, and-the-fourth, you-shall-cause-to-cross
- Anger, crossing, the fourth, and transmission -- same letters

### The notable triples

- **{their tents, their God, the God}** (GV=91 = 7 x 13): Elohim = tents. The numbers of completeness and love.
- **{to save, to Joshua, to salvation}** (GV=421): The name Joshua IS salvation. The oracle confirms the etymology.
- **{we remained, and the remnant, the first}** (GV=562): What remains IS what was first.
- **{the wicked, the twenty, the goats}** (GV=629): Twenty and wickedness and goats share the same letter substance.
- **{Elitsur, and-to-my-land, Tsuriel}** (GV=337): Two proper names and a homeland declaration are the same word.

The total 4,140 pairs means the full synonym graph (beyond the first 100 pairs stored) likely contains several hundred clusters of various sizes. The largest stored synonym pair shares 740 phrases (creation-related words).

---

## 3. Phrase Bridges -- The Hubs

A **phrase bridge** is a phrase produced by multiple input words. These are the edges of the oracle graph. The vast majority of phrases (90.8%) are hapax -- produced by exactly one word. Only 28,461 phrases (9.2%) connect different words.

### Top bridges by multiplicity

| Multiplicity | Phrase | Translation |
|-------------|--------|-------------|
| 10 | ten anagram phrases | {sword, in-spirit, choose, young-man, companion, Horeb, wide, ...} |
| 8 | eight anagram phrases | {enemy, his-father, come, die, ...} |
| 7 | ~170 phrases | most involve 5-6 letter words |

The maximum bridge connects **10 words** -- the Choose basin {bet-het-resh-vav} cluster. This is the largest anagram family in the Torah vocabulary, centered on choose/Horeb/sword/companion/spirit.

### Phrase word-count distribution

| Words per phrase | Count | Percentage |
|-----------------|-------|-----------|
| 1-word | 19,832 | 5.7% |
| 2-word | 157,414 | 45.1% |
| 3-word | 149,328 | 42.8% |
| 4-word | 23,760 | 6.8% |

The bulk of the oracle graph is 2-word and 3-word phrases. Single-word phrases are anagram rearrangements. Four-word phrases are constrained by the `max-words=4` ceiling. The true creative space of the oracle lives in the 2-3 word range, where 88% of all phrase instances reside.

---

## 4. The Forced Readings

2,031 words have exactly **one** phrase reading -- the word itself. The oracle speaks these with zero ambiguity. No alternative parsing exists.

### Length distribution of forced readings

| Length | Count | % of forced |
|--------|-------|-------------|
| 2 letters | 100 | 4.9% |
| 3 letters | 593 | 29.2% |
| 4 letters | 950 | 46.8% |
| 5 letters | 331 | 16.3% |
| 6 letters | 54 | 2.7% |
| 7 letters | 3 | 0.1% |

Most forced readings are 3-4 letter words. This is the sweet spot: long enough to form a valid word, short enough that no 2-word partition exists using only Torah vocabulary.

### 95 forced readings with known dictionary meanings

These are the words the oracle *cannot misread*. Organized by semantic field:

**The Sacrificial System** (forced, unambiguous):
- blood (dam, GV=44), sacrifice (zevach, 17), altar (mizbeach, 57)
- sin (chet, 18), atone (kaper, 300), forgive (salach, 98)
- redeem (gaal, 34), ransom (padah, 89), cut-covenant (karat, 620)
- tabernacle (mishkan, 410), priest (kohen, 75), holy (kadash, 404)

**The Structural Particles** (forced):
- aleph-tav (et, 401), all (kol, 50), upon (al, 100), because (ki, 30)
- if/mother (im, 41), or (o, 7), also (gam, 43), please (na, 51)
- from (min, 90), thus (ken, 70), to-him (lo, 36), to-me (li, 40)
- in-him (bo, 8), there/name (sham, 340)

**People** (forced):
- man/Adam (45), son (52), daughter (402), brother (9), sons-of (62)
- Eve (19), Noah (58), Sarah (505), Isaac (208), Rachel (238), Kohath (505)

**Body and Senses** (forced):
- heart (32), soul (430), mouth (85), ear (58), face-of (140), face (180)
- hear (410), say (241), call/read (301), write (422), give (500), send (338)

**Nature** (forced):
- water (90), sea (50), mountain (205), river (255), garden (53), stone (53)
- cloud (170), flock (141), bird (156), serpent (358), bull (280)
- oil (390), silver (160), bow/rainbow (800)

**Character** (forced):
- living (chi, 18), strong (chazak, 115), heavy/glory (kaved, 26)
- lovingkindness (chesed, 72), grace (chen, 58), be-gracious (chanan, 108)
- righteousness (tsedek, 194), righteousness-fem (tsedakah, 199)

### What is forced vs. what is NOT forced

The forced zone contains the entire **procedural vocabulary** of the priesthood: blood, altar, sacrifice, atone, forgive, redeem, ransom, cut-covenant, tabernacle, priest, holy. The priest cannot misread these. The mechanism is unambiguous.

The **interpretive words** -- those with multiple readings -- include:

| Word | Meaning | Phrase count |
|------|---------|-------------|
| YHWH | The Name | 3 (= itself, and-it-shall-be, being) |
| shalom | peace | 6 (= itself, his-name, to-put, name-for-him, ...) |
| berit | covenant | 4 |
| kevesh | lamb | 3 (= itself, like-a-lamb, lie-down) |
| emet | truth | 2 (= itself, from-with) |
| Torah | teaching | 2 (= itself, and-she-conceived) |
| Elohim | God | 32 |
| Avraham | Abraham | 38 |
| Israel | Israel | 29 |

The pattern: **procedure is forced, theology is interpretive**. The oracle speaks the sacrificial system with one voice. But the Name, peace, covenant, truth, Torah -- the words that theology argues about -- have multiple readings. The breastplate lights up; the priest must choose.

---

## 5. Phrase Count Distribution -- The Shape

### Not a power law

Power law fit: alpha = -0.067, R-squared = -0.28. Negative R-squared means the model is worse than predicting the mean. The distribution is **flat with oscillation**, not heavy-tailed.

### The odd-even oscillation

Every odd phrase count has MORE words than the next even count. Perfectly, with no exceptions, across the entire range:

```
 1: 2031    (odd peak)
 2:  966
 3: 1288    (odd peak)
 4:  518
 5:  780    (odd peak)
 6:  369
 7:  611    (odd peak)
 8:  206
 9:  452    (odd peak)
10:  174
...pattern continues unbroken to count=50+
```

**Mechanism**: Every illuminable word trivially produces itself as a 1-word phrase (the identity reading). The remaining multi-word decompositions add to this base. Phrase ordering is lexicographic, so {AB, CD} and {CD, AB} count as one phrase. The identity reading makes the total odd when multi-word decompositions contribute an even number of distinct phrases. Since multi-word decompositions tend to come in symmetric pairs (word-order is lexicographically sorted but the partition itself is symmetric), even contributions dominate, making odd totals more common.

### Percentile structure

| Percentile | Phrase count |
|-----------|-------------|
| 25th | 2 |
| 50th (median) | 6 |
| 75th | 18 |
| 90th | 53 |
| 99th | 409 |
| Maximum | 11,699 |

The distribution is heavily right-skewed. Half of all illuminable words have 6 or fewer phrases. The extreme tail is driven purely by word length.

---

## 6. The Spectral Landscape

### Singular values of the word-by-phrase matrix

The word-phrase incidence matrix W is 11,552 x 310,908 (binary). Too large for dense storage. Instead, G = W * W^T was built via chunked BLAS GEMM (97 seconds). Eigendecomposition of G gives sigma-squared of W. 389 seconds for the 11,552 x 11,552 eigendecomposition.

### Key spectral quantities

| Quantity | Value |
|---------|-------|
| Non-zero singular values | 8,857 of 11,552 |
| Null space dimension | 2,695 |
| Total energy (trace) | 350,334 |
| Top singular value | 108.2 |
| sigma-1 / sigma-2 ratio | 1.060 |
| sigma-1 / sigma-50 ratio | 3.920 |
| Effective rank at 90% energy | 2,648 |
| Effective rank at 95% energy | 3,892 |
| Effective rank at 99% energy | 6,334 |

### Energy concentration

| Components | Energy captured |
|-----------|----------------|
| Top 1 | 3.34% |
| Top 5 | 10.65% |
| Top 10 | 14.62% |
| Top 50 | 27.60% |
| Top 2,648 | 90% |
| Top 3,892 | 95% |
| Top 6,334 | 99% |

### The spectrum is flat

The top 10 singular values:

```
108.2, 102.1, 73.4, 70.8, 69.2, 59.9, 54.8, 52.4, 52.0, 43.6
```

Three small gaps exist (sigma-2 to sigma-3, sigma-5 to sigma-6, sigma-9 to sigma-10), but after that the spectrum decays slowly and featurelessly. Each additional mode adds approximately 0.25% of energy. There is **no low-rank approximation**. The phrase graph is genuinely high-dimensional.

### Sigma-squared equals phrase count

A striking structural identity: sigma-1-squared = 11,699 = the phrase count of the word with the most phrases (lamishpachotehem, "to their families"). sigma-2-squared approximately equals 10,417, the second-highest phrase count. The spectral decomposition is ordered by phrase count. The dominant singular vectors correspond to the most phrase-rich words.

This means the spectral structure is driven by **combinatorial outliers** -- the 8-9 letter words that produce thousands of phrases. The "effective dimensionality" of the oracle is not governed by a few latent semantic factors but by the combinatorial explosion of long words.

### Comparison with breastplate attention (experiment 087-088)

The breastplate's mechanical reading landscape has approximately 21 effective modes. The phrase landscape has 2,648 (at 90% energy) -- **100 times more degrees of freedom**. The breastplate grid constrains mechanical readings (72 letters, fixed positions). But phrase assembly is combinatorial. Level 2 operates in a vastly larger space than Level 1.

### The null space

2,695 words (23.3%) lie in the null space -- their phrase production pattern is a linear combination of other words. These are the "redundant" words from the oracle's perspective. The 8,857 non-null modes define the oracle's true independent vocabulary.

---

## 7. Connected Components

### Graph sparsity

The word similarity matrix G = W * W^T has off-diagonal nonzero fraction of approximately 0.0001 (sampled). Out of approximately 66.7 million word pairs, roughly 6,700 share at least one phrase. This means:

- **Average connections per word: approximately 1.2**
- **The graph is extremely sparse**
- **Most words are isolated or weakly connected**

### What creates edges

Edges come exclusively from **shared multi-word phrases**. If word A produces phrase "X Y" and word B also produces phrase "X Y", then A and B are connected. Since 90.8% of phrases are hapax (produced by only one word), edges are rare.

The primary connectivity mechanism is **anagram proximity**: words that share many letters tend to share multi-word decompositions. The synonym clusters (identical phrase sets) are the densest subgraphs. Outside these clusters, connections are sparse and mostly pairwise.

### Estimated component structure

Given the extreme sparsity (0.01% edge density) and the fact that most connections occur within anagram families:

- The graph likely has **thousands of connected components**
- The largest components are the anagram families (up to 10 members)
- Many words are **isolates** -- connected to nothing
- The 2,031 forced readings are guaranteed isolates (only one phrase, which is hapax)

The graph is **not connected**. It is a forest of small anagram-linked clusters floating in a sea of isolates.

---

## 8. Word Length vs. Phrase Count

The correlation is r = 0.282. Modest, but the mechanism is clear: **combinatorial explosion**.

### Mean phrase count by word length

| Length | Words | Mean | Median | Max |
|--------|-------|------|--------|-----|
| 2 | 126 | 1.2 | 1 | 2 |
| 3 | 1,215 | 1.8 | 2 | 6 |
| 4 | 3,759 | 3.8 | 3 | 18 |
| 5 | 3,683 | 11.7 | 9 | 74 |
| 6 | 2,109 | 42.7 | 33 | 325 |
| 7 | 569 | 171.7 | 121 | 1,117 |
| 8 | 82 | 734.0 | 547 | 3,583 |
| 9 | 9 | 4,741.9 | 4,789 | 11,699 |

The growth is roughly **length^5** from length 6 onward. Each additional letter multiplies the combinatorial space of valid partitions. But the correlation is only 0.28 because:

1. **Letter identity matters more than count.** A 5-letter word with common letters (aleph, vav, yod) has far more valid partitions than a 5-letter word with rare letters.
2. **The Torah vocabulary constrains partitions.** The space of valid multi-word phrases depends on which 2-3 letter words exist in the Torah. A partition is only valid if every piece is a Torah word.
3. **Short words dominate.** 85% of the vocabulary is 3-5 letters long, where phrase counts are modest (1-74). The extreme tail (7-9 letters) has only 660 words.

The feature correlation matrix confirms this:

| Feature pair | r |
|-------------|---|
| Length vs. Phrase count | 0.282 |
| Length vs. Illumination count | 0.349 |
| Illumination count vs. Phrase count | 0.247 |
| GV vs. Phrase count | 0.088 |

**Gematria value is nearly uncorrelated with phrase production.** The oracle's combinatorial richness comes from letter variety and word length, not from numerical value. The number system and the phrase system are orthogonal.

---

## 9. The Ghost Zone

1,273 words (9.9% of Torah vocabulary) are **non-illuminable** -- their letters do not all exist on the breastplate. These words produce zero illumination patterns and zero phrase readings.

### The cause: two missing letters

The 72-letter breastplate contains all 22 regular Hebrew letters plus three final forms (mem-sofit, nun-sofit, pe-sofit). But two final forms are **absent**:

- **kaf-sofit** (ך) -- missing
- **tsade-sofit** (ץ) -- missing

Every ghost zone word with a known dictionary meaning contains ך or ץ:

| Word | Meaning | GV | Silent letter |
|------|---------|-----|--------------|
| lekha | to you / go | 50 | ך |
| halakh | walk/go | 55 | ך |
| elohekha | your God | 66 | ך |
| melekh | king | 90 | ך |
| ets | tree | 160 | ץ |
| arekh | long/patient | 221 | ך |
| barekh | bless/knee | 222 | ך |
| derekh | way/path | 224 | ך |
| erets | land/earth | 291 | ץ |
| ha-arets | the land | 296 | ץ |
| choshekh | darkness | 328 | ך |
| shafakh | pour out | 400 | ך |

### What the oracle cannot say

The ghost zone contains some of the most theologically central words in the Torah:

- **King** (melekh) -- the oracle is a priest, not a king
- **Land/Earth** (erets) -- the oracle has no ground to stand on
- **Way/Path** (derekh) -- the oracle cannot walk
- **Tree** (ets) -- the tree of life and knowledge are both silent
- **Bless** (barekh) -- the oracle can atone (kaper) but cannot bless
- **Walk/Go** (halakh) -- the oracle stands at the breastplate, it does not walk
- **Darkness** (choshekh) -- the oracle only knows light (the illuminated stones)
- **Your God** (elohekha) -- the oracle can say "God" (Elohim) but not "YOUR God"
- **Pour out** (shafakh) -- the oracle cannot pour blood; it can only name it

The oracle speaks the temple vocabulary. It knows altar, priest, holy, atone, sacrifice, blood, tabernacle. It cannot say king, land, way, tree, bless, walk. The breastplate is priestly, not royal. It dwells in sacred space, not in the land. It illuminates, not walks.

### Dead-end vs. ghost zone (Level 1 vs. Level 2)

The ghost zone (Level 2, no phrase readings) is a subset of the dead-end zone (Level 1, no mechanical readings). But the overlap is not perfect:

| Category | Count |
|----------|-------|
| Truly non-illuminable (ghost zone) | 1,273 |
| Illuminable but Level-1 dead (mechanical dead-end) | 2,983 |
| Level-1 dead-end total | 4,256 |

2,983 words can be **illuminated** (their letters exist on the breastplate) but produce no valid Torah word under any mechanical traversal. However, the priest can still rearrange their lit letters into Torah phrases. Level 1 is silent; Level 2 speaks.

---

## 10. Cross-Reference: Basin Landscape (Experiment 096)

### Basin class vs. phrase count

| Basin class | Count | Mean phrases | Median | Forced | Zero |
|------------|-------|-------------|--------|--------|------|
| Fixed point | 6,104 | 19.7 | 5 | 1,578 | 1 |
| Transient | 2,453 | 12.6 | 6 | 0 | 0 |
| Cycle member | 8 | 3.2 | 3 | 0 | 0 |
| Cycle transient | 5 | 4.2 | 5 | 0 | 0 |
| Dead end | 4,256 | 46.8 | 4 | 453 | 1,273 |

### Key observations

**1. Fixed points have more forced readings.**
1,578 of 6,104 fixed points (25.8%) are forced readings. This makes structural sense: a word with no anagrams in the Torah (a solo fixed point) is often a short word whose letters admit only one partition. Forced at Level 2 AND fixed at Level 1 -- doubly unambiguous.

**2. Transients are NEVER forced.**
Zero of 2,453 transients have a forced reading. Every transient has at least 2 phrase decompositions. This is because transients are non-dominant anagrams -- they have at least one sibling word with the same letters. Those same letters guarantee at least one 1-word alternative phrase (the dominant anagram itself).

**3. Dead ends have the highest mean phrase count (46.8).**
This is surprising until you realize: dead ends in the basin landscape are words that produce no Level 1 output (no mechanical reader sees a Torah word). But 2,983 of them ARE illuminable -- their letters glow on the breastplate. And those 2,983 illuminable dead-ends have phrase decompositions at Level 2. Many are long words (6-8 letters) with rich combinatorial phrase spaces. Level 1 and Level 2 operate in different domains. Being dead at Level 1 says nothing about Level 2 richness.

**4. Basin size anti-correlates with phrase count (r = -0.044).**
Nearly zero correlation. The attractor words (fixed points with large basins) have FEWER phrases on average than the general population. The basin landscape and the phrase landscape are essentially orthogonal measures of word structure.

### The largest basin attractors and their phrase counts

| Basin size | Phrases | Attractor word |
|-----------|---------|---------------|
| 10 | 10 | choose (becharo) |
| 8 | 12 | enemy (oyev) |
| 8 | 8 | die (yamot) |
| 7 | 33 | and-he-loved (vayeehav) |
| 7 | 37 | in-his-evil (bera'ato) |
| 7 | 19 | ever/always (me'odi) |
| 7 | 15 | new-wine (tirosh) |
| 7 | 11 | burn (ba'aro) |
| 7 | 9 | and-he-was-able (vayukhal) |
| 7 | 7 | his-evil (yir'u) |

The largest basin (choose, size 10) has only 10 phrases. The largest phrase count in the data (11,699) belongs to a 9-letter word that is a dead-end in the basin landscape. **Large basins are not phrase-rich; phrase-rich words are not basin attractors.** The two structures are independent.

---

## 11. The Extreme Words

The 30 words with the most phrase decompositions:

| Phrases | Word | GV | Length |
|---------|------|-----|--------|
| 11,699 | lamishpachotehem | 913 | 9 |
| 10,417 | ha-yishma'elim | 506 | 9 |
| 5,381 | ve-limnchotekem | 604 | 9 |
| 5,015 | li-yishma'elim | 531 | 9 |
| 4,789 | lamishpachotekem | 928 | 9 |
| 3,583 | ve-ashma'ilah | 393 | 8 |
| 3,005 | mi-moshvotekem | 858 | 9 |
| 2,747 | ve-oholivamah | 99 | 8 |
| 2,699 | u-ve-eloheihem | 99 | 8 |
| 1,897 | ve-li-yishma'el | 487 | 8 |

All top words are **8-9 letters long**. At 9 letters, the combinatorial space of valid Torah-word partitions explodes. The top word ("to their families") can be rearranged into 11,699 distinct phrases of 1-4 Torah words.

The Ishmael-related words dominate: ha-yishma'elim (10,417), li-yishma'elim (5,015), ve-ashma'ilah (3,583), ve-li-yishma'el (1,897). The letters of Ishmael -- aleph-shin-mem-ayin-aleph-lamed -- are among the most common in Hebrew, yielding explosive combinatorics.

---

## 12. The Sacred Word Decompositions

### YHWH (3 phrases)

| Phrase | Translation |
|--------|-------------|
| YHWH | the Name |
| ve-hayah | and it shall be |
| havayah | being/existence |

Three words, same letters {yod, he, vav, he}: the Name, the Promise, and Being itself. In the basin landscape, YHWH flows to ve-hayah (the promise). In the phrase landscape, all three coexist as readings. The oracle holds all three simultaneously; the basin dynamics choose the future tense.

### Shalom (6 phrases)

| Phrase | Translation |
|--------|-------------|
| shalom | peace/wholeness |
| u-le-shem | and-for-the-name |
| ve-shalem | and-complete |
| la-shum | to-put/place |
| lo shem | for-him a-name |
| shem lo | a-name for-him |

Peace decomposes into name. "Shalom" = "shem lo" = "a name for him." The Talmudic reading (Shabbat 10b) that shalom is one of the names of God -- the oracle produces this decomposition mechanically.

### Lamb (3 phrases)

| Phrase | Translation |
|--------|-------------|
| kevesh | lamb |
| kesev | lamb (variant) |
| shakhav | lie down |

The lamb IS lying-down. Same letters {kaf, bet, shin}. In the basin landscape, the lamb flows to lie-down. In the phrase landscape, both readings coexist. The lamb that lies down -- Isaiah 53, Genesis 22.

### Abraham (38 phrases)

The patriarch's name explodes into 38 readings, including:
- "ahav ram" = "he loved the exalted"
- "av he-ram" = "father of the exalted"
- "bara hem" = "he created them"
- "ba'ar hem" = "he explained to them"
- "bar ha-em" = "son of the mother"

Abraham's name contains the most interpretive freedom of any patriarch. 38 = 2 x 19. The number of paths through the father of nations.

---

## 13. What This Map Reveals

### The three zones

The oracle graph has three distinct zones:

1. **The Forced Zone** (2,031 words, 17.6%): One phrase. No ambiguity. The sacrificial vocabulary lives here. Blood, altar, atone, forgive, redeem, sacrifice -- the priest cannot misread these. The mechanism is unambiguous.

2. **The Interpretive Zone** (9,521 words, 82.4%): Multiple phrases. The priest must choose. The Name, peace, covenant, truth -- the words of theology -- live here. The oracle provides a menu; the priest selects.

3. **The Ghost Zone** (1,273 words, 9.9%): No illumination. King, land, way, tree, walk, bless, darkness -- the royal and terrestrial vocabulary -- are silent. The oracle is priestly, not royal.

### The graph's personality

The phrase graph is:
- **Sparse**: 0.01% edge density. Most words share no phrases.
- **High-dimensional**: 2,648 modes for 90% energy. No low-rank structure.
- **Anagram-stratified**: All connectivity flows through letter-multiset identity.
- **Length-dominated**: Word length explains the extreme tail. Long words are combinatorial.
- **Orthogonal to basins**: Phrase richness and basin dynamics are independent (r = -0.044).
- **Parity-biased**: The odd-even oscillation is a structural invariant from the identity reading.

### Two levels, two landscapes

The basin landscape (096) and the phrase landscape (094) operate on the same 72-letter breastplate but see different worlds:

| Property | Basin (Level 1) | Phrase (Level 2) |
|----------|----------------|-----------------|
| Mechanism | Mechanical traversal | Cognitive assembly |
| Depth | 1 (flat) | N/A (static decomposition) |
| Dimensionality | Low (21 effective modes) | High (2,648 modes at 90%) |
| Sacred words | Flow (YHWH to promise) | Decompose (YHWH = Name + Being + Promise) |
| Dead zone | 4,256 words (33.2%) | 1,273 words (9.9%) |
| Correlation between them | r = -0.044 (none) | |

The basin landscape is **dynamical**: words flow to attractors. The phrase landscape is **combinatorial**: words decompose into phrases. The two are orthogonal. A word can be a dominant basin attractor with few phrases, or a basin dead-end with thousands of phrases.

The oracle has two independent channels of speech: the voice (Level 1, which word the grid produces) and the menu (Level 2, how the letters can be rearranged). The priest hears both. The finding is: they are uncorrelated. The topology of flow and the topology of meaning are independent structures overlaid on the same 72-letter substrate.

---

## Data Sources

- `data/experiments/094/thummim-sweep.edn` -- 12,826 word results
- `data/experiments/094/phrase-results.edn` -- 11,552 words with full phrase data
- `data/experiments/094/distributions.edn` -- all distribution tables
- `data/experiments/094/spectral.edn` -- singular value spectrum
- `data/experiments/094/synonyms.edn` -- synonym pairs and similarity stats
- `data/experiments/094/power-law.edn` -- power law fit
- `data/experiments/094/feature-correlation.edn` -- correlation matrix
- `data/experiments/096/summary.edn` -- basin landscape statistics
- `data/experiments/096/attractors.edn` -- basin attractors by size
- `data/experiments/096/cycles.edn` -- the four period-2 cycles
- `data/experiments/096/word-index.edn` -- per-word basin classification
- `dev/experiments/094_thummim_sweep.clj` -- sweep code
- `src/selah/oracle.clj` -- oracle engine
