# Hypotheses — Testable Claims

*Extracted from encoding-ideas.md. Each hypothesis is something we can compute, falsify, or measure. Ranked by ease of implementation and expected signal.*

---

## Tier 1 — Quick Computations (hours, not days)

These require tools we mostly already have.

### H1. Gematria Statistics
**Claim:** The Torah's letter-value stream has non-random statistical structure.
**Test:** Compute mean, median, harmonic mean, variance per book. Compare distributions. Check for power law / 1/f signature.
**Null:** Letter values are randomly distributed consistent with Hebrew letter frequencies.
**Tools needed:** `selah.gematria` — letter-to-value mapping, basic statistics.

### H2. Golden Section Location
**Claim:** The golden section of the Torah (position 306,269 / φ ≈ 189,244) lands at a significant verse.
**Test:** One computation. Find the verse. Read it.
**Null:** It lands at an unremarkable location.
**Tools needed:** Existing `selah.text.locate`.

### H3. Golden Section of Leviticus
**Claim:** The golden section of Leviticus (44,980 / φ ≈ 27,800) lands at a significant verse.
**Test:** Same as H2.
**Tools needed:** Existing tools.

### H4. Fibonacci Position Extraction
**Claim:** Letters at Fibonacci positions (1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144...) in the Torah spell something meaningful.
**Test:** Extract letters at all Fibonacci positions up to 306,269. Look for patterns.
**Null:** Random-looking sequence.
**Tools needed:** Trivial — just index into the letter stream.

### H5. Pi Digits as Skip Values
**Claim:** Using digits of π as skip sequences produces meaningful words.
**Test:** Extract letters at positions defined by cumulative π digits. Try various constructions.
**Null:** No recognizable words.
**Tools needed:** Trivial.

### H6. Position 31,415 in the Torah
**Claim:** The Torah position corresponding to π × 10,000 lands at a significant verse.
**Test:** Look up position 31,415. Also try 3,141 and 314,159 mod 306,269.
**Null:** Unremarkable locations.
**Tools needed:** Existing tools.

### H7. את Count and Distribution
**Claim:** The את (aleph-tav) markers in the Torah number something significant and/or are non-randomly distributed.
**Test:** Count all את occurrences per book. Map their positions. Check for periodicity.
**Null:** Count and positions consistent with Hebrew grammar expectations.
**Tools needed:** `selah.text.aleph-tav` — word-level search in the Hebrew text (need word boundaries, not just letter stream).

### H8. Running Sum Mod 7
**Claim:** The cumulative gematria sum mod 7 hits zero at significant verses.
**Test:** Compute running sum, take mod 7, find all zero-crossings. Map to verses.
**Null:** Zero-crossings are uniformly distributed.
**Tools needed:** `selah.gematria` + existing locate tools.

### H9. Book Length Ratios
**Claim:** Ratios between book lengths approximate π, φ, or other mathematical constants.
**Test:** Compute all pairwise ratios. Compare to known constants.
**Null:** Ratios are unremarkable.
**Tools needed:** Already have letter counts.

### H10. Eigenspectrum Shape
**Claim:** The gematria-weighted letter frequency distribution follows a power law (like natural signals) rather than being flat.
**Test:** Plot frequency × gematria value for all 22 letters. Fit power law. Compare per book.
**Null:** Flat or inconsistent with natural signal distributions.
**Tools needed:** `selah.gematria` + basic plotting.

---

## Tier 2 — Medium Computations (days)

These require new tools or data sources.

### H11. Grid Wrapping at Width 50
**Claim:** Wrapping the Torah at width 50 reveals meaningful vertical and diagonal words.
**Test:** Build a 2D grid. Scan columns (vertical) and diagonals for Hebrew words.
**Null:** No more meaningful words than expected by chance.
**Tools needed:** `selah.grid` — 2D text wrapping, vertical/diagonal word extraction, dictionary lookup.

### H12. Grid Wrapping at Tabernacle Dimensions
**Claim:** Wrapping at widths derived from Tabernacle/Temple measurements (42, 50, 100, 10, 7) produces more meaningful patterns than arbitrary widths.
**Test:** Compare meaningful word density at Tabernacle widths vs control widths (43, 51, 99, 11, 6).
**Null:** No significant difference.
**Tools needed:** Same grid tools as H11.

### H13. FFT of Letter-Value Stream
**Claim:** The Torah's gematria signal has dominant frequencies. Skip-50 and/or skip-7 correspond to spectral peaks.
**Test:** Run FFT on the 306,269-integer sequence. Look for peaks. Compare to shuffled controls.
**Null:** Flat spectrum (white noise).
**Tools needed:** `selah.signal` — FFT (can use Apache Commons Math or JTransforms via Java interop).

### H14. Autocorrelation at Symbolic Lags
**Claim:** The letter-value stream shows significant autocorrelation at lags 7, 26, 50 (symbolic numbers) compared to nearby lags.
**Test:** Compute autocorrelation function. Check values at symbolic lags vs neighbors.
**Null:** Autocorrelation consistent with linguistic patterns, no special significance at symbolic lags.
**Tools needed:** `selah.signal` — autocorrelation.

### H15. Entropy Per Chapter
**Claim:** Some chapters are more information-dense than others. Leviticus or the Tabernacle chapters have distinct entropy signatures.
**Test:** Compute Shannon entropy of letter distribution per chapter. Rank. Look for patterns.
**Null:** Entropy is roughly uniform across chapters.
**Tools needed:** `selah.stats` + chapter-level text extraction.

### H16. Conserved Core Across Variants
**Claim:** The ELS chiastic pattern lives in the letters that are invariant across manuscript traditions.
**Test:** Align Masoretic and Samaritan Pentateuch. Tag each letter as conserved or variable. Check if ELS hit positions fall in conserved regions.
**Null:** ELS positions are equally likely in conserved and variable regions.
**Tools needed:** `selah.variants` — Samaritan text acquisition, alignment, diff. (Already planned in research.md.)

### H17. Tribal Census Numbers as Skip Values
**Claim:** Using tribal census counts from Numbers 1-2 as ELS skip values produces meaningful patterns.
**Test:** Run ELS searches at skips 74600, 54400, 57400, 46500, 59300, 45650, etc. (and their factors).
**Null:** No more meaningful than random skip values of similar magnitude.
**Tools needed:** Existing ELS engine (may need large-skip support).

### H18. Isaiah Chapter-to-Book Thematic Mapping
**Claim:** Isaiah chapter N thematically parallels Bible book N, especially at the 39/27 division.
**Test:** Extract key themes/words from each Isaiah chapter. Compare to corresponding Bible book. Score thematic overlap.
**Null:** No more thematic overlap than random chapter-to-book pairings.
**Tools needed:** Isaiah text acquisition, thematic keyword extraction. May need Greek NT tools for NT books.

---

## Tier 3 — Deep Investigations (weeks)

### H19. Torah as Cylinder — Scroll Dimensions Define Grid
**Claim:** Traditional scribal specifications (letters per line, lines per column) define the ELS grid parameters.
**Test:** Research exact Talmudic scroll dimensions. Compute implied skip values. Test ELS at those skips.
**Null:** Scribal dimensions don't produce better ELS results than nearby dimensions.
**Tools needed:** Research (Talmudic sources), `selah.grid`, `selah.scroll` — scroll geometry.

### H20. Radial Adjacency — Letters Aligned When Rolled
**Claim:** Letters that become physically adjacent when the scroll is rolled up share meaningful relationships.
**Test:** Given scroll circumference, compute which letters from distant positions overlay each other. Check for gematria relationships, complementarity, or word formation.
**Null:** No more structure than expected by chance.
**Tools needed:** `selah.scroll` — physical scroll modeling, layer computation.

### H21. Breastplate as Query Interface
**Claim:** The 4×3 grid of tribal names, with gematria values, functions as an encoding/decoding matrix.
**Test:** Build the grid. Compute gematria per cell, per row, per column, per diagonal. Look for patterns. Try matrix operations.
**Null:** No mathematical structure beyond the names themselves.
**Tools needed:** Research (stone order, tribal name assignment), matrix operations.

### H22. DNA Structural Mapping
**Claim:** The Torah's double-helix structure (antiparallel strands, triplet reading, complementary pairing) has a formal mathematical correspondence to DNA's structure.
**Test:** Define a mapping between Hebrew letters and nucleotides (or codons and roots). Test for consistency. Check if reading-frame shifts in the Torah produce effects analogous to codon frame shifts.
**Null:** The parallel is purely analogical, with no formal mathematical correspondence.
**Tools needed:** Deep research, mathematical formalization.

### H23. Tenen's Fold — Does Genesis 1:1 Fold Into a 3D Form?
**Claim:** The letter sequence of Genesis 1:1, when paired and folded, produces a geometric form that generates the Hebrew alphabet.
**Test:** Reproduce Tenen's construction computationally. Verify or refute.
**Null:** The folding is arbitrary or doesn't produce recognizable letter forms.
**Tools needed:** 3D geometry, visualization.

### H24. Neural Network Representations
**Claim:** A model trained on the Torah's letter stream learns internal representations that correspond to known theological or structural features.
**Test:** Train a small character-level model on the Torah. Extract hidden representations. Apply PCA/t-SNE. Look for structure.
**Null:** Representations reflect Hebrew linguistic patterns only, nothing deeper.
**Tools needed:** ML framework, embedding analysis.

---

## Tools We Need to Build

### Already have
- `selah.text.sefaria` — Hebrew text acquisition and caching
- `selah.text.normalize` — letter stream normalization
- `selah.text.locate` — position-to-verse mapping
- `selah.els.engine` — ELS search, verification, debug
- `selah.els.scan` — broad scan across words and skips
- `selah.els.stats` — statistical significance (shuffled controls, Poisson model)
- `selah.greek.parse` — Greek NT parsing (4 variants)
- `selah.greek.normalize` — Greek letter normalization
- `selah.greek.panin` — Panin's heptadic claims evaluation

### Need to build

| Tool | Namespace | What it does | Needed for |
|------|-----------|-------------|------------|
| **Gematria engine** | `selah.gematria` | Letter → value mapping, word/verse/passage sums, running totals | H1, H8, H10, H21 |
| **Grid wrapper** | `selah.grid` | Wrap text at width N, extract verticals/diagonals, scan for words | H11, H12, H19 |
| **Signal tools** | `selah.signal` | FFT, autocorrelation, power spectrum, windowed analysis | H13, H14 |
| **Word finder** | `selah.text.words` | Word-level search in Hebrew text (not just letters) — find את, specific words | H7 |
| **Scroll geometry** | `selah.scroll` | Physical scroll modeling — circumference, layers, radial adjacency | H19, H20 |
| **Isaiah tools** | `selah.isaiah` | Isaiah text acquisition, chapter extraction, thematic analysis | H18 |
| **Dimensions data** | `selah.dimensions` | Tabernacle/Temple measurements, tribal census, as structured data | H12, H17, H21 |
| **Variant alignment** | `selah.variants` | Cross-manuscript alignment, diff, conserved-core extraction | H16 |
| **Visualization** | `selah.viz` | SVG/HTML output for grids, spectra, chiastic diagrams | All |

### Priority: the gematria engine

The single most impactful tool is `selah.gematria`. It unlocks H1, H2, H3, H6, H8, H9, H10 — nearly all of Tier 1. Build this first.

It needs:
- A map of letter → value (22 entries + 5 final forms)
- `(letter-value ch)` — single letter
- `(word-value word)` — sum of letter values
- `(stream-values letters)` — convert letter stream to integer stream
- `(running-sum letters)` — cumulative gematria
- `(stats letters)` — mean, median, harmonic mean, variance
- `(distribution letters)` — frequency × value histogram

That's maybe 40 lines of Clojure. Then Tier 1 opens up.

---

## What I (Scott) Need

I can code. I can't read Hebrew or Greek or Aramaic. So the tools need to:

1. **Translate positions to readable context** — every position should resolve to book, chapter, verse, and an English gloss
2. **Show Hebrew with transliteration** — when displaying hits, show the Hebrew, a romanization, and a translation
3. **Explain significance** — when a verse is found at a special position, include enough context to understand why it might matter
4. **Be REPL-friendly** — everything callable from the REPL, composable, inspectable
5. **Produce shareable output** — formatted reports, tables, eventually visualizations

The interface to the text matters as much as the computation. The tool should make the text *legible* to someone who can't read the original languages.
