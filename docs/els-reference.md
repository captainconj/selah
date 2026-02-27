# ELS (Equidistant Letter Sequence) — Algorithm Reference

## Core Concept

Treat a Hebrew passage as a single continuous stream of letters (no spaces,
no vowels, no cantillation). Then sample every Nth letter starting from some
position. If the sampled letters spell a meaningful word, that's an ELS.

---

## Normalization

The single most important step. Get this wrong and nothing downstream works.

### Keep
- Hebrew letters: א ב ג ד ה ו ז ח ט י כ ל מ נ ס ע פ צ ק ר ש ת
- Final forms: ך ם ן ף ץ (these ARE distinct letters in the stream)

### Drop
- Niqqud (vowel points): U+05B0–U+05BD, U+05BF, U+05C1–U+05C2, U+05C4–U+05C5, U+05C7
- Cantillation marks: U+0591–U+05AF
- Maqaf: U+05BE
- Sof pasuq: U+05C3
- All whitespace, punctuation, Latin characters, digits
- Any other non-letter Unicode in the Hebrew block

### Final Letters

| Final | Regular | Keep both? |
|-------|---------|------------|
| ך | כ | Yes — do NOT collapse |
| ם | מ | Yes |
| ן | נ | Yes |
| ף | פ | Yes |
| ץ | צ | Yes |

Standard practice: preserve final forms as-is. They appear in the Masoretic
text and affect letter counts.

---

## Algorithms

### Extract

Given stream S, starting index (0-based), skip distance, and word length k:

```
extract(S, start, skip, k):
  result = ""
  pos = start
  for i in 0..(k-1):
    if pos < 0 or pos >= len(S): return nil
    result += S[pos]
    pos += skip
  return result
```

- Positive skip = forward
- Negative skip = reverse reading

### Search (find all occurrences at a given skip)

```
search(S, target, skip):
  k = len(target)
  hits = []
  for start in 0..(len(S)-1):
    word = extract(S, start, skip, k)
    if word == target:
      hits.append({start: start, skip: skip})
  return hits
```

### Scan (search across a range of skips)

```
scan(S, target, min-skip, max-skip):
  all-hits = []
  for skip in min-skip..max-skip:
    all-hits += search(S, target, skip)
    all-hits += search(S, target, -skip)  // reverse
  return all-hits
```

---

## 1-Based vs 0-Based Index Convention

Published claims (papers, PDFs) almost always use **1-based** positions.
Internally we use **0-based**. Convert: `internal = published - 1`.

### Verification of published positions

```
verify(S, positions-1based):
  word = ""
  for p in positions-1based:
    word += S[p - 1]
  return word
```

Also check constant spacing:

```
constant-step?(positions):
  if len(positions) < 2: return true
  step = positions[1] - positions[0]
  for i in 2..(len(positions)-1):
    if positions[i] - positions[i-1] != step: return false
  return true
```

---

## Published Claims (Torah Codes 1996)

Source: [Torah Codes 1996 PDF](https://sites.pitt.edu/~tpiccone/Bible/Torah%20Codes%201996.pdf)

### The Five Books Pattern

| Book | Target | Skip | Direction | Notes |
|------|--------|------|-----------|-------|
| Genesis | תורה | +50 | Forward | Starts near beginning |
| Exodus | תורה | +50 | Forward | Starts near beginning |
| Leviticus | יהוה | +7 | Forward | God's name, central book |
| Numbers | תורה | -50 | Reverse | Mirror of Gen/Exod |
| Deuteronomy | תורה | -50 | Reverse | Mirror of Gen/Exod |

### Genesis Specific Claim

1-based letter positions: 6, 56, 106, 156
Skip: 50
Word: תורה (Tav-Vav-Resh-Heh = TORH = Torah)

---

## Debugging Mismatches

When your results don't match published claims:

### Common causes (in order of likelihood)

1. **Different normalization** — did you strip the same characters?
2. **Different passage scope** — passage-limited vs book-wide stream
3. **Different text source** — Sefaria vs WLC vs Mechon Mamre may differ
4. **Plene/defective spelling** — some words have variant spellings across editions
5. **Off-by-one** — 1-based vs 0-based confusion

### Debug window

Print letters around the claimed position to see what's actually there:

```
debug-window(S, pos-1based, radius=10):
  i = pos-1based - 1
  lo = max(0, i - radius)
  hi = min(len(S), i + radius + 1)
  snippet = S[lo..hi]
  pointer = i - lo
  return {snippet, pointer, "letter at pos": S[i]}
```

### Stream comparison

If you have two differently-normalized streams, diff them:

```
compare(A, B):
  diffs = []
  for i in 0..min(len(A), len(B))-1:
    if A[i] != B[i]:
      diffs.append({index: i, a: A[i], b: B[i]})
  if len(A) != len(B):
    diffs.append({length-a: len(A), length-b: len(B)})
  return diffs
```
