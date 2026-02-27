# Findings — First ELS Analysis Run

*February 27, 2026. Text source: Sefaria API (cached). Normalization: Hebrew letters only (U+05D0–U+05EA), final forms preserved.*

---

## Letter Stream Lengths

| Book        | Letters |
|-------------|---------|
| Genesis     | 78,364  |
| Exodus      | 63,857  |
| Leviticus   | 44,980  |
| Numbers     | 63,846  |
| Deuteronomy | 55,222  |
| **Total**   | **306,269** |

---

## The Chiastic Pattern — Confirmed

The published Torah Codes claim holds with our Sefaria-derived text. The five books of the Pentateuch encode a chiastic (mirror) structure:

```
  Genesis      →  תורה  forward   skip +50
  Exodus       →  תורה  forward   skip +50
  Leviticus    →  יהוה  forward   skip  +7    (center)
  Numbers      →  תורה  reverse   skip -50
  Deuteronomy  →  תורה  reverse   skip -50
```

The outer books spell "Torah" pointing inward; the center book spells God's name.

The skip values themselves carry meaning. **50** is the number of days from the Exodus to the giving of the Torah at Sinai — Shavuot, the counting of the Omer (7 weeks + 1 day). **7** is the number of creation, the Sabbath, the foundational rhythm of the text. The encoding isn't just *what* is spelled but *at what interval* — Torah at the interval of its own giving, YHWH at the interval of His rest.

### Genesis — תורה at skip +50

**3 hits.** The first begins at the 6th letter of the book.

| Hit | Start (0-based) | Position in book |
|-----|-----------------|------------------|
| 1   | 5               | 0.01% (near the very beginning) |
| 2   | 18,854          | 24.1% |
| 3   | 76,617          | 97.8% |

Published claim verified:

```
1-based positions: [6, 56, 106, 156]
Word: תורה
Constant step: true (50)
```

The letters land in:
- Position 6: **ת** — the ת of בראשי**ת** ("In the beginning")
- Position 56: **ו** — deep in the first creation account
- Position 106: **ר** — within the light narrative
- Position 156: **ה** — still in Day 1

### Exodus — תורה at skip +50

**8 hits** — notably more than any other book at this skip. The first begins at position 8.

Published claim verified:

```
1-based positions: [8, 58, 108, 158]
Word: תורה
Constant step: true (50)
```

Both Genesis and Exodus begin their first תורה within the first 10 letters of the book.

### Leviticus — יהוה at skip +7

**7 hits** of God's name at skip 7 in the center book.

For context, יהוה appears **317 times** at skip 1 (surface text) in Leviticus — it is saturated with the divine name. At skip 7, the 7 occurrences stand out against surrounding skips:

| Skip | Forward hits | Reverse hits |
|------|-------------|-------------|
| +5   | 6           | 6           |
| +6   | 4           | 10          |
| **+7** | **7**     | **2**       |
| +8   | 5           | 3           |
| +9   | 3           | 1           |

Skip 7 is not the highest count in the neighborhood, but 7 is a symbolically significant number in the text itself.

### Numbers — תורה at skip -50

**2 hits.** The reverse reading.

| Hit | Start (0-based) | % into book |
|-----|-----------------|-------------|
| 1   | 163             | 0.26%       |
| 2   | 60,606          | 94.9%       |

### Deuteronomy — תורה at skip -50

**3 hits.**

| Hit | Start (0-based) | % into book |
|-----|-----------------|-------------|
| 1   | 20,603          | 37.3%       |
| 2   | 31,222          | 56.5%       |
| 3   | 35,481          | 64.3%       |

---

## Observations

### Genesis and Exodus are remarkably similar

Both books begin their first תורה at skip +50 within the opening words — position 6 and position 8 respectively. The probability of both first hits landing in the first 10 letters (out of ~78k and ~64k letter streams) is striking.

### Skip 50 in context

Is skip 50 special, or do most skips produce similar hit counts? For Genesis:

| Skip | Forward | Reverse |
|------|---------|---------|
| 47   | 0       | 4       |
| 48   | 1       | 1       |
| 49   | 3       | 1       |
| **50** | **3** | **3**   |
| 51   | 1       | 2       |
| 52   | 4       | 2       |
| 53   | 4       | 3       |

Skip 50 is not an outlier in raw count — neighboring skips produce comparable numbers. What is notable is the *positional* clustering at the start of the book.

For Exodus, skip +50 produces **8 hits** — the highest in its neighborhood (next highest is 4). This is more suggestive.

### The word תורה doesn't appear in Genesis surface text

At skip 1, Genesis has **0** occurrences of תורה. The word "Torah" does not appear in the plain text of Genesis at all. It only appears in the equidistant structure.

| Book        | תורה at skip 1 (surface) |
|-------------|--------------------------|
| Genesis     | 0                        |
| Exodus      | 2                        |
| Leviticus   | 3                        |
| Numbers     | 8                        |
| Deuteronomy | 21                       |

The ascending count across the five books is itself interesting — the word becomes more frequent as the Torah discusses itself more explicitly.

### Letter frequency

The Hebrew letter distribution is similar across all five books:

| Letter | Genesis | Exodus | Leviticus | Numbers | Deuteronomy |
|--------|---------|--------|-----------|---------|-------------|
| י      | 11.6%   | 10.3%  | 9.0%      | 9.9%    | 10.2%       |
| ו      | 10.8%   | 9.7%   | 10.0%     | 9.4%    | 9.8%        |
| א      | 9.8%    | 8.2%   | 9.0%      | 8.2%    | 8.9%        |
| ה      | 8.0%    | 9.6%   | 10.4%     | 8.9%    | 9.6%        |
| ל      | 6.7%    | 6.7%   | 6.9%      | 7.6%    | 7.4%        |

The letters that compose תורה (ת, ו, ר, ה) are all common — each appears 4–11% of the time. This means random ELS hits are expected. The question is whether the *positional* patterns (beginning of book, specific skip) exceed chance.

### Distinct words at skip 50

In Genesis, there are 78,214 possible 4-letter extractions at skip 50. These produce **50,832 distinct words** — meaning most combinations are unique. Against this backdrop, finding a meaningful word at a specific position is less surprising statistically but more interesting structurally.

---

## Open Questions for Phase 4

1. **Expected occurrence rate:** Given the letter frequencies, how many times *should* תורה appear at any given skip in a book of this length? A simple model: P(תורה) ≈ P(ת) × P(ו) × P(ר) × P(ה) per starting position.

2. **Positional significance:** The raw hit count at skip 50 is not exceptional. But the clustering at the *beginning* of Genesis and Exodus is. Can we quantify the probability of first-hit-in-first-10-letters?

3. **Shuffled controls:** If we shuffle the Genesis letter stream (preserving letter frequencies), how often does the first תורה at skip 50 land in the first 10 letters?

4. **Cross-skip analysis:** Are there other meaningful Hebrew words that show similar patterns at skip 50?

---

## How to Reproduce

All commands assume you're in the project root with cached Sefaria data in `data/cache/sefaria/`.

### Run the tests (confirms the pattern)

```bash
clojure -X:dev:test
```

### REPL exploration

```bash
clojure -M:dev
```

```clojure
(require '[selah.text.sefaria :as sefaria]
         '[selah.text.normalize :as norm]
         '[selah.els :as els])

;; Load a book's letter stream
(def genesis (sefaria/book-letters "Genesis"))
(count genesis)  ;=> 78364

;; Verify the published Genesis claim
(els/verify-positions genesis [6 56 106 156])
;=> {:word "תורה", :positions [6 56 106 156], :constant-step? true, :step 50}

;; Search for תורה at skip 50
(els/search genesis "תורה" 50)
;=> ({:start 5, :skip 50, :word "תורה"} ...)

;; Scan across a range of skips
(els/scan genesis "תורה" 2 100)

;; Debug window around a position (1-based)
(els/debug-window genesis 6 10)
;=> {:snippet "בראשיתבראאלהיםאתהשמי", :pointer 5, :letter \ת, ...}

;; Surface occurrences (skip 1)
(count (els/search genesis "תורה" 1))  ;=> 0
```

### First-time setup (if no cache exists)

The first call to `(sefaria/book-letters "Genesis")` will fetch all 50 chapters from the Sefaria API and cache them to `data/cache/sefaria/`. This takes a minute or two. Subsequent calls use the cache.
