# Bible Study Tools — Project Plan

## Vision

Build a Clojure toolkit for studying biblical texts at the letter level.
Start with the Hebrew Pentateuch and ELS (Equidistant Letter Sequence) analysis,
then grow into a broader platform for textual study, variant comparison, and discovery.

---

## Phase 1: Text Acquisition & Normalization

The foundation. Everything else depends on having clean, reliable letter streams.

### 1.1 Hebrew Text Sources

We need at least one primary source, ideally multiple for cross-referencing.

| Source | Format | Notes |
|--------|--------|-------|
| **Sefaria API** | JSON via REST | No auth. `GET /api/texts/Genesis.1.1-5?lang=he`. Best starting point. |
| **Tanach.us** | HTML / downloadable | WLC (Westminster Leningrad Codex) derived. Good for Masoretic verification. |
| **Mechon Mamre** | HTML | Stable, simple. Requires scraping. |
| **OSIS / OSHB XML** | XML files | Open Scriptures Hebrew Bible — downloadable, parseable, well-structured. |

**Decision:** Start with Sefaria API for convenience. Later, pull in OSHB XML
for offline / reproducible analysis. Store raw fetched text locally so we're
not hitting APIs repeatedly.

### 1.2 Normalization Pipeline

This is the make-or-break step. ELS results are meaningless if normalization is wrong.

**The rule:** Keep only Hebrew letters א‎–ת‎ (U+05D0–U+05EA). Drop everything else.

Strip:
- Niqqud (vowel points, U+05B0–U+05BD, U+05BF, U+05C1–U+05C2, U+05C4–U+05C5, U+05C7)
- Cantillation / trop marks (U+0591–U+05AF)
- Spaces, newlines, punctuation
- Maqaf (U+05BE), sof pasuq (U+05C3), paseq, quotes, section marks

**Do NOT** collapse final letters (ך→כ, ם→מ, etc.) unless explicitly chosen.
Final forms are distinct letters in the stream.

### 1.3 Data Model

```clojure
;; A text source
{:source   :sefaria        ;; or :tanach-us, :oshb, :mechon-mamre
 :ref      "Genesis.1"     ;; book.chapter or book.chapter.verse-verse
 :raw      "בְּרֵאשִׁ֖ית..."  ;; as-fetched, unmodified
 :letters  "בראשית..."     ;; normalized letter stream (string)
 :metadata {:fetched-at ... :version ...}}
```

### 1.4 Scope Options

Two modes that produce very different index spaces:

- **Passage-limited** — Only the verses in question (e.g., Gen 1:1–5).
  Indices relative to passage start. Good for verifying specific claims.
- **Book-wide** — Entire book as one continuous stream.
  Required for the classic Torah/YHWH ELS claims across full books.

We support both. The user picks the scope; the system builds the stream accordingly.

---

## Phase 2: ELS Engine

### 2.1 Core Functions

```
els-extract  : stream × start × skip × length → word | nil
els-search   : stream × target × skip → [{:start :skip}]
els-scan     : stream × target × skip-range → [{:start :skip}]  ;; search across many skips
```

- `start` is 0-based internally
- Support both positive (forward) and negative (reverse) skip
- Bounds-check: return nil if any index falls outside the stream

### 2.2 Verification Tools

For reproducing published claims (like the Torah Codes 1996 paper):

```
verify-positions : stream × [1-based-positions] → word
constant-step?   : [positions] → boolean
```

Convert 1-based published indices to 0-based internally.

### 2.3 Debug / Inspection

When results don't match published claims (they won't on the first try):

```
debug-window : stream × position × radius → {:snippet :pointer :context}
compare-streams : stream-a × stream-b → [{:index :letter-a :letter-b}]  ;; diff two normalizations
```

99% of mismatches come from normalization differences or passage boundaries.
Having good debug tools saves hours.

---

## Phase 3: The Classic Claims to Reproduce

The well-known pattern set from the Torah Codes literature:

| Book | Word | Skip | Direction |
|------|------|------|-----------|
| Genesis | תורה (TORH) | +50 | Forward |
| Exodus | תורה (TORH) | +50 | Forward |
| Leviticus | יהוה (YHWH) | +7 | Forward |
| Numbers | הרות (HROT = TORH reversed) | -50 | Reverse |
| Deuteronomy | הרות (HROT = TORH reversed) | -50 | Reverse |

Reference: [Torah Codes 1996 PDF](https://sites.pitt.edu/~tpiccone/Bible/Torah%20Codes%201996.pdf)

Specific published claim for Genesis: letters at 1-based positions 6, 56, 106, 156 spell תורה.

**Goal:** Reproduce these exactly, or document precisely why they differ
(which text source, which normalization choices).

---

## Phase 4: Statistical Context (are the codes meaningful?)

Once we can find ELS sequences, the natural question: is this significant
or would random text produce similar results?

### 4.1 Expected Occurrence Rate

- Given a stream of length N with alphabet size ~22
- Probability of a k-letter word at any given (start, skip): ~(1/22)^k
- Number of possible (start, skip) pairs: ~N × N
- Expected hits for a 4-letter word in a stream of ~300k letters: not trivial

### 4.2 Control Experiments

- Shuffle the letter stream, run the same search, compare hit counts
- Use non-Torah Hebrew texts of similar length as controls
- Compare skip distributions (are certain skips over-represented?)

This is where the project gets genuinely interesting from a study perspective.

---

## Phase 5: Variant Texts & Comparison

### 5.1 Text Variants to Collect

- Masoretic Text (MT) — the standard
- Samaritan Pentateuch — diverges in interesting places
- Dead Sea Scrolls fragments — oldest witnesses
- Septuagint (LXX) back-translations — Greek, but reveals source differences

### 5.2 Variant Analysis Tools

- Alignment: map corresponding passages across variants
- Diff: where do variants disagree at the letter level?
- ELS sensitivity: do ELS results change across variants?
  (If a code depends on a single letter that differs between MT and DSS, that's informative.)

---

## Project Structure

```
bible-study/
├── deps.edn
├── docs/
│   ├── plan.md              ← you are here
│   ├── sources.md           ← detailed notes on text sources
│   └── els-reference.md     ← ELS algorithm details & published claims
├── src/bible_study/
│   ├── core.clj             ← entry point, REPL convenience
│   ├── text/
│   │   ├── fetch.clj        ← API clients (Sefaria, etc.)
│   │   ├── normalize.clj    ← letter stream normalization
│   │   └── cache.clj        ← local storage of fetched texts
│   ├── els/
│   │   ├── engine.clj       ← extract, search, scan
│   │   ├── verify.clj       ← reproduce published claims
│   │   └── debug.clj        ← inspection & diff tools
│   ├── stats/
│   │   └── significance.clj ← statistical analysis, controls
│   └── variants/
│       ├── alignment.clj    ← cross-variant alignment
│       └── diff.clj         ← letter-level comparison
├── data/
│   └── cache/               ← cached Hebrew text (gitignored)
├── test/bible_study/
│   ├── normalize_test.clj
│   └── els_test.clj
└── .gitignore
```

---

## Implementation Order

1. **`deps.edn`** — minimal: clj-http, cheshire (JSON), clojure.test
2. **`text/normalize.clj`** — the Hebrew letter filter. Test it thoroughly.
3. **`text/fetch.clj`** — Sefaria client. Fetch Genesis, cache locally.
4. **`els/engine.clj`** — extract and search functions.
5. **`els/verify.clj`** — reproduce the Genesis תורה claim at skip 50.
6. **`els/debug.clj`** — debug window, stream comparison.
7. **Reproduce all five Pentateuch claims** — the real validation.
8. **`stats/significance.clj`** — shuffled controls, expected rates.
9. **Variant collection** — pull in additional text sources.
10. **Cross-variant ELS comparison** — the deep study.

---

## Open Questions

- Do we want a UI eventually? (Rich comment blocks + REPL may be enough for now.)
- Should we store results in Conceptual for cross-session memory?
- What other ELS claims beyond Torah/YHWH do we want to investigate?
- Interest in Greek NT letter analysis as well, or Hebrew only for now?

---

## Principles

- **Reproducibility over mystery.** Every result should be traceable to
  a specific text source, normalization, and parameter set.
- **REPL-first.** This is exploration tooling, not a product.
- **One source of truth at a time.** Don't blend texts. Keep streams separate.
- **Show your work.** Debug tools are not optional—they're essential.
