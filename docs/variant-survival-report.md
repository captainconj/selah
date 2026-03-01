# Variant Survival Report: What Survives Textual Variation?

**Date:** 2026-02-28
**Texts compared:**
- **WLC** — Westminster Leningrad Codex (~1008 CE), OSHB 4.20, kethiv preferred
- **MAM** — Miqra according to the Masorah (Sefaria), section markers stripped, footnotes stripped

## Letter Counts

| Book | WLC | MAM | Diff |
|------|-----|-----|------|
| Genesis | 78,069 | 78,065 | -4 |
| Exodus | 63,531 | 63,528 | -3 |
| Leviticus | 44,795 | 44,790 | -5 |
| Numbers | 63,545 | 63,530 | -15 |
| Deuteronomy | 54,910 | 54,891 | -19 |
| **TOTAL** | **304,850** | **304,804** | **-46** |

Total difference: 46 letters (0.015%). WLC (kethiv) has slightly more letters than MAM (qere) — consistent with kethiv preserving more plene spellings.

## Structural Findings

### 1. ELS Chiastic Pattern — SURVIVES ✓

| Book | Pattern | WLC Position | MAM Position | Verdict |
|------|---------|-------------|-------------|---------|
| Genesis | תורה fwd skip 50 | start=5 | start=5 | **identical** |
| Exodus | תורה fwd skip 50 | start=7 | start=7 | **identical** |
| Deuteronomy | הרות fwd skip 49 | start=278 | start=278 | **identical** |

The anchoring positions are **invariant across manuscript traditions**. All variant letters occur downstream of the first few positions in each book — the ELS starting points are untouched.

### 2. Center Convergence — SURVIVES ✓

| Metric | WLC | MAM |
|--------|-----|-----|
| Center inside Leviticus? | Yes | Yes |
| Leviticus position range | 0.4645–0.6114 | 0.4644–0.6112 |
| Front/Back ratio | 1.573551 | 1.572187 |
| π/2 | 1.570796 | 1.570796 |
| Deviation from π/2 | 0.28% | 0.14% |

The center falls inside Leviticus in both traditions. MAM is actually *closer* to π/2 than WLC.

### 3. Genesis 1:1 Gematria — SURVIVES ✓

Genesis 1:1 = 2701 = 37 × 73 in both traditions (no consonantal variant in Gen 1:1).

### 4. Total Torah Gematria — DOES NOT SURVIVE ✗

| Property | WLC | MAM |
|----------|-----|-----|
| Total gematria | 21,010,192 | 21,030,176 |
| mod 7 | 0 ✓ | 6 ✗ |
| mod 37 | 1 | 5 |
| mod 73 | 62 | 44 |
| mod 441 | 70 | 209 |

WLC total is divisible by 7, but MAM is not. **No total-gematria divisibility property survives variant comparison.**

The earlier claim of "mod 441=0" was an artifact from a text that included Sefaria footnote content (variant reading annotations containing Hebrew letters).

## Variant Classification

199 raw edits across 161 verses (Needleman-Wunsch alignment, chapter-block fallback for verse numbering mismatches).

### 1. Verse boundary difference — 26 edits (not real variants)

Numbers 25:19 (WLC) = Numbers 26:1 (MAM). The phrase ויהי אחרי המגפה (13 letters) is assigned to different chapters. Shows as 13 DEL + 13 INS but the consonantal text is identical.

### 2. Plene/defective spelling — 122 edits (ins/del of matres lectionis)

The dominant category. Extra ו or י in one tradition but not the other.

| Letter | WLC extra (DEL) | MAM extra (INS) |
|--------|----------------|-----------------|
| ו      | 59             | 29              |
| י      | 24             | 6               |
| ה      | 4              | 4               |
| א      | 0              | 2               |

All non-matres DEL/INS (פ,ג,מ,ח,ר) belong to the Numbers 25/26 verse boundary above.

WLC (kethiv) tends toward more plene spellings — 87 extra matres vs 41 for MAM.

### 3. Matres swap (י↔ו) — 25 subs

Same position, different mater lectionis. Still a spelling variant, not a consonantal difference. Typically reflects different vowel traditions (e.g., cholem vs chirik).

- י→ו (WLC has yod, MAM has vav): 17 instances
- ו→י (WLC has vav, MAM has yod): 8 instances

### 4. Other matres swap (א↔ו, ה↔ו, ך↔ה) — 7 subs

- א→ו: Exodus 21:8, Leviticus 11:21, Leviticus 25:30 (kethiv לא vs qere לו, etc.)
- ה→ו: Numbers 34:4, Deuteronomy 21:7, Leviticus 21:5
- ך→ה: Numbers 23:13 (final kaf → he, with extra כ insertion)

### 5. Transpositions — 10 subs (5 pairs)

Letter-order differences within a word. The alignment algorithm reports each swap as two substitutions.

| Verse | Swap |
|-------|------|
| Genesis 35:23 | ל↔ו |
| Genesis 46:14 | ל↔ו |
| Exodus 29:40 | י↔ע |
| Exodus 36:19 | י↔ל |
| Deuteronomy 6:9 | ז↔ו |

### 6. Complex word-form differences — 4 subs (2 pairs)

Exodus 37:8 and 39:4 each show ת→י + ו→ת — likely different word forms (e.g., כפרת vs כפורת).

### 7. Genuine consonantal kethiv/qere — 5 subs

The only true consonantal differences. Both are famous tiqqunei soferim (scribal traditions):

| Verse | WLC (kethiv) | MAM (qere) |
|-------|-------------|------------|
| Deuteronomy 28:27 | ל | ר |
| Deuteronomy 28:27 | פ | ח |
| Deuteronomy 28:27 | ע | ט |
| Deuteronomy 28:30 | ל | ב |
| Deuteronomy 28:30 | ג | כ |

Deut 28:27: WLC בעפלים (with tumors) vs MAM בטחרים (with hemorrhoids).
Deut 28:30: WLC ישגלנה vs MAM ישכבנה (euphemistic substitution).

### 8. Remaining — 1 sub

Deuteronomy 5 block (ו→י) — from block-level alignment of the chapter with different verse divisions (30 vs 33 verses).

### Summary by category

| Category | Edits | Real variant? |
|----------|-------|---------------|
| Verse boundary | 26 | No — same text, different assignment |
| Plene/defective | 122 | Spelling only |
| Matres swap (י↔ו) | 25 | Spelling only |
| Other matres swap | 7 | Spelling only |
| Transpositions | 10 | Word-form |
| Complex word-form | 4 | Word-form |
| Genuine consonantal | 5 | Yes — different words |
| Other | 1 | Spelling only |
| **Total** | **199** | **5 consonantal** |

**Bottom line**: Of 304,850 consonants, exactly 5 (0.002%) differ between traditions — and all 5 are well-known kethiv/qere readings in Deuteronomy 28. Everything else is spelling variation (plene vs defective).

### Verse numbering differences

Three chapters have different verse divisions between MAM and WLC (same text, different verse boundaries):

| Chapter | MAM verses | WLC verses |
|---------|-----------|------------|
| Exodus 20 | 23 | 26 |
| Numbers 25 | 18 | 19 |
| Deuteronomy 5 | 30 | 33 |

These are handled by block-level alignment (diff the whole chapter as one stream).

See `docs/variant-diff-report.md` for the complete per-verse listing.

## Data Quality Notes

1. **Sefaria footnotes**: The MAM text from Sefaria includes `<i class="footnote">` tags with Hebrew letters from variant readings. These must be stripped (not just the tags — the content too). Without stripping, the MAM count inflates by ~500 letters.

2. **Section markers**: MAM text includes parasha markers ({פ}/{ס}) that are not text content. Stripped via `normalize/strip-section-markers`.

3. **Kethiv/qere**: OSHB encodes both written (kethiv) and reading (qere) forms. For manuscript fidelity, we use kethiv. The parser skips `<rdg type="x-qere">` elements.
