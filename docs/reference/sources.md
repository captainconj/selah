# Hebrew Text Sources

Type: `reference`
State: `clean`

## Primary: Sefaria API

**Base URL:** `https://www.sefaria.org/api/texts/`

### Usage

```
GET https://www.sefaria.org/api/texts/Genesis.1.1-5?lang=he
```

Returns JSON with Hebrew text in the `he` field (may be array of strings, one per verse).

### Book References (Pentateuch)

| Book | Sefaria Ref | Hebrew |
|------|-------------|--------|
| Genesis | `Genesis` | בראשית |
| Exodus | `Exodus` | שמות |
| Leviticus | `Leviticus` | ויקרא |
| Numbers | `Numbers` | במדבר |
| Deuteronomy | `Deuteronomy` | דברים |

### Fetching Entire Books

For book-wide ELS, we need the complete text. Sefaria allows chapter-level requests:

```
GET https://www.sefaria.org/api/texts/Genesis.1?lang=he    → chapter 1
GET https://www.sefaria.org/api/texts/Genesis.50?lang=he   → chapter 50
```

Strategy: Fetch each chapter, concatenate verses in order, then normalize.

### Chapter Counts

| Book | Chapters |
|------|----------|
| Genesis | 50 |
| Exodus | 40 |
| Leviticus | 27 |
| Numbers | 36 |
| Deuteronomy | 34 |

### Response Shape

```json
{
  "he": ["verse1_hebrew", "verse2_hebrew", ...],
  "text": ["verse1_english", "verse2_english", ...],
  "ref": "Genesis 1",
  "heRef": "בראשית א",
  ...
}
```

The `he` field contains HTML-like markup in some cases (e.g., `<b>` tags around
divine names). Strip all HTML tags during normalization.

---

## Secondary: OSHB (Open Scriptures Hebrew Bible)

**Repository:** `https://github.com/openscriptures/morphhb`

XML files with full morphological markup of the Westminster Leningrad Codex.

### Advantages
- Downloadable — no API dependency
- Well-structured XML
- Includes morphology codes
- Academic standard

### Disadvantages
- Larger setup (clone repo, parse XML)
- More complex than needed for Phase 1

**Plan:** Use as a secondary source for verification and variant comparison.

---

## Tertiary Sources

### Tanach.us
- URL: `https://tanach.us/`
- WLC-derived, good for cross-checking
- Manual inspection / copy-paste workflow

### Mechon Mamre
- URL: `https://mechon-mamre.org/`
- Clean Hebrew/English Tanakh
- HTML scraping required
- Very stable source

---

## Key Concern: Orthographic Variation

Different sources may use different spellings for the same word:

- **Plene (מלא):** includes matres lectionis (ו for /o/, י for /i/)
- **Defective (חסר):** omits them

Example: דוד (David, plene) vs דוד (same in this case, but other words vary)

**This changes letter counts and ELS results.**

### Our approach:
1. Pick ONE primary source (Sefaria/WLC)
2. Document which source produced each result
3. When comparing claims from papers, note their stated source
4. Build tools to diff streams from different sources

---

## Caching Strategy

- Cache raw API responses in `data/cache/sefaria/`
- File naming: `genesis-01.json`, `genesis-02.json`, etc.
- Cache normalized letter streams in `data/cache/streams/`
- File naming: `genesis-letters.txt` (full book, letters only)
- `.gitignore` the cache directory (it's reproducible from sources)
