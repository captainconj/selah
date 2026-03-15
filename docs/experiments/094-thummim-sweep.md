# Experiment 094 — The Thummim Sweep

*Every word in the Torah, through the oracle. Look at the distribution.*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/094_thummim_sweep.clj`
**Run:** `clojure -M:dev dev/experiments/094_thummim_sweep.clj`

## Setup

The Level 2 Thummim (`parse-letters`) takes a Hebrew word, treats its letters as a multiset, and finds all ways to partition that multiset into Torah vocabulary words. This is what the priest does cognitively — the breastplate lights up letters, and the priest assembles them into phrases.

We ran all 12,826 unique Torah word forms through this process. For each word: how many illumination patterns? How many phrase readings? What phrases?

Then: spectral analysis. Build the word similarity matrix G = W·W^T where W is the binary word-by-phrase incidence matrix (11,552 words × 310,908 phrases). G is too sparse for the full W (28 GB), so we construct it via chunked BLAS GEMM — column blocks of W accumulated into G. Then eigendecompose G to get the singular value spectrum of the oracle's phrase landscape.

**Sweep time**: 18 seconds (pmap). **G construction**: 97 seconds (MKL GEMM). **Eigendecomposition**: 389 seconds (LAPACK).

## The Numbers

| Quantity | Value |
|----------|-------|
| Total Torah words | 12,826 |
| Illuminable (letters exist on breastplate) | 11,553 (90.1%) |
| With at least one phrase reading | 11,552 |
| Not illuminable (missing letters) | 1,273 |
| Unique phrases produced | 310,908 |
| Total phrase instances | 350,334 |
| Forced readings (exactly 1 phrase) | 2,031 |
| Hapax phrases (produced by exactly 1 word) | 282,447 (90.8%) |
| Oracle synonym pairs | 4,140 |
| Singular values (non-zero) | 8,857 of 11,552 |
| Effective rank (90% energy) | 2,648 |
| Effective rank (95% energy) | 3,892 |
| Effective rank (99% energy) | 6,334 |

## Finding 1: The Odd-Even Oscillation

The phrase-count distribution is not a power law. It oscillates.

Words with **odd** phrase counts outnumber words with **even** phrase counts by **2.41×** across the entire range:

```
ODD    1: 2031 ████████████████████████████████████████████
even   2:  966 ████████████████████
ODD    3: 1288 ████████████████████████████
even   4:  518 ███████████
ODD    5:  780 █████████████████
even   6:  369 ████████
ODD    7:  611 █████████████
even   8:  206 ████
ODD    9:  452 ██████████
even  10:  174 ███
ODD   11:  285 ██████
even  12:  115 ██
ODD   13:  312 ███████
even  14:   73 █
 ...pattern continues to count=50 and beyond
```

This is structural, not random. The mechanism: `parse-letters` with `min-letters=2` and `max-words=4` partitions a letter multiset. Every word trivially produces itself as a 1-word phrase (count includes the identity). The remaining decompositions come in pairs — if letters {A,B,C,D} can split into {AB,CD}, they can also split into {CD,AB}. But phrase ordering is lexicographic, so these count as one. The single-word identity reading makes the total odd when the number of multi-word decompositions is even.

The pattern strengthens with word length:

| Length | Words | Odd | Even | Odd/Even |
|--------|-------|-----|------|----------|
| 2 | 126 | 100 | 26 | 3.85 |
| 3 | 1,215 | 771 | 444 | 1.74 |
| 4 | 3,759 | 2,413 | 1,346 | 1.79 |
| 5 | 3,683 | 2,723 | 960 | 2.84 |
| 6 | 2,109 | 1,742 | 367 | 4.75 |
| 7 | 569 | 525 | 44 | 11.93 |
| 8 | 82 | 75 | 7 | 10.71 |
| 9 | 9 | 9 | 0 | ∞ |

At 9 letters, ALL words have odd phrase counts. The identity reading dominates the parity.

## Finding 2: The Forced Readings

2,031 words (17.6% of illuminable) have exactly **one** phrase reading — the word itself. The oracle has zero ambiguity. These are the words the breastplate pronounces unequivocally.

Among the 92 forced readings with known dictionary translations:

**Structure words**: את (aleph-tav), כי (because/when), כל (all), על (upon), אם (if/mother), או (or), בו (in him), לו (to him), לי (to me), גם (also), נא (please), מן (from), כן (thus)

**People**: אדם (man), בן (son), בני (sons of), בת (daughter), חוה (Eve), נח (Noah), שרה (Sarah), רחל (Rachel), יצחק (Isaac), קהת (Kohath)

**Body**: לב (heart), פה (mouth), פני (face of), פנים (face), אזן (ear)

**Sacred objects**: מזבח (altar), משכן (tabernacle), כסף (silver)

**Nature**: דם (blood), מים (water), ים (sea), ענן (cloud), נהר (river), גן (garden), אבן (stone), הר (mountain), פר (bull), צאן (flock), עוף (bird), נחש (serpent)

**Actions**: אמר (say), נתן (give), שמע (hear), קרא (call), שלח (send), כפר (atone), גאל (redeem), סלח (forgive), פדה (ransom), כרת (cut covenant), זבח (sacrifice), נקם (avenge)

**Qualities**: חי (living), חזק (strong), כבד (glory), חסד (lovingkindness), צדק (righteousness), חן (grace), קדש (holy)

Note what is forced: **blood, altar, atone, forgive, ransom, redeem, sacrifice, cut-covenant** — the entire sacrificial vocabulary has exactly one reading. The priest cannot misread the blood. The atonement is unambiguous. The mercy seat names itself.

And note what is NOT forced: אהבה (love) has 3 phrases. יהוה (YHWH) has 3. שלום (peace) has 6. ברית (covenant) has 4. אמת (truth) has 2. The words that require interpretation are exactly the ones theology argues about.

## Finding 3: All 4,140 Synonyms Are Anagrams

Every synonym pair (words producing identical phrase sets) shares the same letter multiset. This is provably necessary: `parse-letters` operates on letter frequencies, not letter order. Same letters → same partitions → same phrases.

The synonyms form **63 groups**: 49 pairs, 11 triples, and 3 quadruples.

**The quadruples** (4 words, same letter bag, identical phrase sets):

- **אלהיכם = האכלים = כאהלים = כאלהים** (GV=106): "your God" = "the eaters" = "like tents" = "like God". Four words, same letters, same 110 phrase readings. The oracle cannot distinguish your-God from like-God from like-tents from the-eaters. The breastplate sees one light pattern; the priest sees four possible sources.

- **בעריהם = העברים = הערבים = עבריהם** (GV=327): "in their cities" = "the Hebrews" = "the Arabs/evenings" = "their crossings". Same letters. Geography, ethnicity, time, and transit — one illumination.

- **ויתעבר = ועברתי = ורביעת = תעבירו** (GV=688): "and he was angry" = "and I crossed over" = "and the fourth" = "you shall cause to cross". Anger, crossing, the fourth, and transmission — same letters.

**Notable triples**:

- **אהליהם = אלהיהם = האלהים** (GV=91=7×13): "their tents" = "their God" = "the God (Elohim)". The oracle sees the same light for Elohim and for tents. 91 = love × completeness.

- **להושיע = ליהושע = לישועה** (GV=421): "to save" = "to Joshua" = "to salvation". The name Joshua IS salvation — the oracle confirms the etymology by producing identical phrase sets.

- **השארנו = והנשאר = ראשונה** (GV=562): "we remained" = "and the remnant" = "the first". What remains IS what was first.

## Finding 4: The Spectral Landscape

### Total energy is tautological

Total energy = trace(G) = Σ G[i,i] = Σ phrase_count[i] = 350,334 = total phrase instances. This is the definition of trace, not a finding. The energy distributes across 8,857 non-zero modes.

### Three spectral gaps

The singular value spectrum has three clear gaps in its first 10 modes:

| Gap | Between | Ratio | Interpretation |
|-----|---------|-------|----------------|
| 1 | σ₂→σ₃ | 1.39 | 102.1 → 73.4 |
| 2 | σ₅→σ₆ | 1.16 | 69.2 → 59.9 |
| 3 | σ₉→σ₁₀ | 1.19 | 52.0 → 43.6 |

After σ₁₀, the spectrum becomes a slow, featureless decay. The gaps mark natural boundaries: **2 dominant modes, then 5, then 9**.

The two dominant modes together capture 6.3% of energy. The top 9 capture 14.1%. This is a high-dimensional system — 50 modes capture only 27.6%. There is no low-rank approximation. The oracle has **many independent degrees of freedom**.

### The effective rank

- **2,648 modes for 90%** — nearly a quarter of the 11,552 words are needed
- **3,892 modes for 95%**
- **6,334 modes for 99%** — more than half the space

Compare with the AI model eigendecomposition (experiment 087-088) which found ~21 effective modes. The phrase landscape has **100× more degrees of freedom** than the breastplate's mechanical reading landscape. This makes sense: the breastplate has 72 letters on a fixed grid, constraining the mechanical readings. But phrase assembly is combinatorial — it operates in a much larger space.

### The null space

11,552 - 8,857 = **2,695 words in the null space** — words whose phrase production pattern is a linear combination of other words. These are the words the oracle doesn't "need." The 8,857 non-null modes define the oracle's true vocabulary.

### Phrase landscape is NOT low-rank

The cumulative energy curve is nearly linear after the first few modes. Each additional mode adds ~0.25% of energy. This means the phrase landscape has no hidden low-dimensional structure — it is genuinely high-dimensional. The oracle's phrase production is not governed by a small number of latent factors. Each word's phrase set is mostly its own thing.

## Finding 5: The Distribution Is Not a Power Law

Power law fit: α = -0.067, R² = -0.28 (negative R² means the model is worse than the mean).

The phrase-count distribution is **flat with oscillation**, not heavy-tailed. Most words have between 1 and 50 phrases. The few extreme words (11,699 max) are all 8-9 letter words — length is the primary driver (correlation r=0.28).

| Feature pair | Correlation |
|-------------|-------------|
| Length ↔ Phrase count | 0.282 |
| Length ↔ Illumination count | 0.349 |
| Illumination count ↔ Phrase count | 0.247 |
| GV ↔ Phrase count | 0.088 |

Gematria value is nearly uncorrelated with phrase production. The oracle's combinatorial richness comes from letter variety and word length, not from numerical value.

## Finding 6: The Extreme Words

The word with the most phrase readings: **למשפחתיהם** ("to their families", GV=913, 9 letters) — 11,699 phrases. All top-20 words are 8-9 letters long. The phrase count grows roughly as length^5 for long words (mean: 4,742 for 9-letter words vs 734 for 8-letter vs 172 for 7-letter vs 43 for 6-letter).

The single illuminable word with 0 phrases: **ה** (heh, GV=5). One letter. `min-letters=2` excludes it. The letter that means "the" — the definite article — produces no phrase. The grammar that points at things cannot name itself.

## What This Means

The Thummim sweep reveals an oracle with several structural properties:

1. **Forced vocabulary**: 2,031 words (including the entire sacrificial/atonement system) have no interpretive freedom. The oracle speaks these clearly.

2. **Interpretive vocabulary**: Love, the Name, peace, truth, covenant — these have multiple readings. The oracle provides options; the priest must choose.

3. **Anagram identity**: The oracle cannot distinguish words that share the same letter bag. Elohim = tents. Joshua = salvation. This is a feature, not a bug — the oracle speaks at the level of letter substance, not letter order.

4. **High dimensionality**: 8,857 independent modes with no low-rank structure. The oracle is not a simple system with a few hidden variables. It has genuine combinatorial complexity.

5. **The odd-even signature**: The identity reading (each word is itself) creates a persistent parity bias. This is the one structural invariant — the word always names itself first.

## Files

```
data/experiments/094/
  thummim-sweep.edn      — 12,826 word results (no phrases, 1.4 MB)
  phrase-results.edn      — 11,552 words with full phrase data (40 MB)
  distributions.edn       — all distribution tables
  long-tail.edn           — forced readings, hapax, rare words
  spectral.edn            — singular values, effective rank
  power-law.edn           — fit parameters, data points
  synonyms.edn            — 4,140 synonym pairs, similarity stats
  feature-correlation.edn — correlation matrix

dev/experiments/094_thummim_sweep.clj — full experiment code
```
