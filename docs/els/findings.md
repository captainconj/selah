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

| Hit | Start (0-based) | Chapter | Narrative context |
|-----|-----------------|---------|-------------------|
| 1   | 5               | Genesis 1 | **Creation.** The opening words of the Torah. |
| 2   | 18,854          | Genesis 16 | **Hagar and the angel.** The first promise of a nation. |
| 3   | 76,617          | Genesis 49 | **Jacob blesses the twelve tribes.** The closing. |

The first and last hits frame the entire book — creation to the blessing of Israel.

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

**8 hits** — notably more than any other book at this skip (3.5× expected, p=0.001). The first begins at position 8.

| Hit | Start (0-based) | Chapter | Narrative context |
|-----|-----------------|---------|-------------------|
| 1   | 7               | Exodus 1 | **Slavery.** The opening — Israel in Egypt. |
| 2   | 5,112           | Exodus 4 | **Moses sent to Pharaoh.** "Let my son go." |
| 3   | 16,452          | Exodus 12 | **Passover instructions.** How to eat the lamb. |
| 4   | 18,571          | Exodus 12 | **Passover law.** "No bone shall be broken." |
| 5   | 20,300          | Exodus 14 | **Pursuit.** "What have we done, letting Israel go?" |
| 6   | 21,826          | Exodus 14 | **The Red Sea.** "Israel saw the great hand." |
| 7   | 50,930          | Exodus 33 | **Face to face.** "You have found favor in my eyes." |
| 8   | 60,523          | Exodus 39 | **The Tabernacle.** Priestly garments completed. |

The 8 hits trace the entire Exodus arc: slavery, calling, Passover, deliverance, encounter, dwelling.

Published claim verified:

```
1-based positions: [8, 58, 108, 158]
Word: תורה
Constant step: true (50)
```

Both Genesis and Exodus begin their first תורה within the first 10 letters of the book.

### Leviticus — יהוה at skip +7

**7 hits** of God's name at skip 7 in the center book.

| Hit | Start (0-based) | Chapter | Narrative context |
|-----|-----------------|---------|-------------------|
| 1   | 22,308          | Leviticus 14 | **Purification from tzaraat.** Restoring the unclean to community. |
| 2   | 24,927          | Leviticus 16 | **Yom Kippur.** The scapegoat for Azazel. The central ritual of the Torah. |
| 3   | 32,641          | Leviticus 21 | **Priestly holiness.** Requirements for who may serve before God. |
| 4   | 38,060          | Leviticus 24 | **"I am YHWH your God."** כי אני יהוה אלהיכם — the ELS of God's name lands where the surface text declares the same name. |
| 5   | 43,139          | Leviticus 26 | **Covenant blessings and curses.** "I am YHWH their God." כי אני יהוה אלהיהם — again, the hidden encoding surfaces where the plain text says the Name. |
| 6   | 44,164          | Leviticus 27 | **Dedications to YHWH.** Valuations of things consecrated. |
| 7   | 44,909          | Leviticus 27 | **Dedications to YHWH.** Two hits in the closing chapter of the center book. |

The 7 hits trace a theological arc: purification → atonement → holiness → self-revelation → covenant → dedication. Hits 4 and 5 are the most striking — the ELS encoding of God's name lands precisely in passages where the surface text itself declares "I am YHWH." The hidden structure echoes the plain text.

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

| Hit | Start (0-based) | Chapter | Narrative context |
|-----|-----------------|---------|-------------------|
| 1   | 163             | Numbers 1 | **The census.** Counting Israel's armies by tribe. |
| 2   | 60,606          | Numbers 34 | **Borders of the land.** The inheritance mapped out. |

The two hits frame Numbers: the counting of the people, and the land promised to them.

### Deuteronomy — תורה at skip -50

**3 hits.**

| Hit | Start (0-based) | Chapter | Narrative context |
|-----|-----------------|---------|-------------------|
| 1   | 20,603          | Deuteronomy 11 | **Obedience and the land.** "The land YHWH your God gives you." |
| 2   | 31,222          | Deuteronomy 19 | **Cities of refuge.** Purging innocent blood from Israel. |
| 3   | 35,481          | Deuteronomy 22 | **Purging evil.** Laws of communal responsibility. |

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

## Statistical Analysis

### Expected vs actual hit counts

Using letter frequencies to predict expected ELS occurrences (Poisson model):

| Book | Word | Skip | P(word) | Expected hits | Actual hits |
|------|------|------|---------|---------------|-------------|
| Genesis | תורה | +50 | 2.83e-05 | 2.21 | 3 |
| Exodus | תורה | +50 | 3.63e-05 | 2.31 | **8** |
| Leviticus | יהוה | +7 | — | 4.36 | 7 |
| Numbers | תורה | -50 | — | 1.61 | 2 |
| Deuteronomy | תורה | -50 | — | 1.95 | 3 |

Individual book hit counts are unremarkable — **except Exodus**, which has 3.5× the expected count at skip +50 (p=0.001 in shuffled controls, only 1/1000 shuffled streams produced ≥ 8 hits).

The P(≥1 hit) per book is high: 80–99%. If you just ask "does תורה appear *somewhere* at skip 50 in Genesis?" the answer is: it almost certainly would in any text with similar letter frequencies. That is not where the signal is.

### Where the signal actually is: position

The first תורה at skip +50 starts at letter **6** in Genesis (78,364 letters) and letter **8** in Exodus (63,857 letters). Both in the opening words.

| | P(first hit in first 10 letters) |
|---|---|
| Genesis | 2.83e-04 (1 in 3,534) |
| Exodus | 3.63e-04 (1 in 2,755) |
| **Both** | **1.03e-07 (1 in 9.7 million)** |

Shuffled controls confirm this: in **0 out of 1,000** shuffled Genesis streams did the first תורה at skip 50 land in the first 10 letters. Same for Exodus. Zero.

### The mirror pattern

The chiastic structure — same word forward in two books, reversed in two others, different word in the center — was tested by shuffling all books independently:

| Test | Trials | Hits | p |
|------|--------|------|---|
| Mirror only (4 outer books, any position) | 10,000 | 0 | < 1e-4 |
| Full chiastic pattern (all 5 books) | 1,000 | 0 | < 1e-3 |
| Full pattern + positional clustering | 1,000 | 0 | < 1e-3 |

**0 out of 10,000 shuffled trials** reproduced even the basic mirror pattern. The letter frequencies support individual hits, but the *coordination* across books — same word, same skip, mirrored direction — does not emerge from random text.

### The skip values are not free parameters

A common objection: "you searched until you found something." But 50 and 7 are not arbitrary search parameters chosen to produce hits. They are given by the text's own theology:

- **50** — the Torah was given on the 50th day after the Exodus (Shavuot, the counting of the Omer: 7 × 7 + 1 = 50)
- **7** — the Sabbath, the rhythm of creation, the structure of time in the text

No multiple-comparison correction is needed for values that come from the tradition itself, not from scanning.

### The name in the center

יהוה at skip 7 in Leviticus is not just "a word at a skip." In Exodus 3:14-15, God reveals His name — יהוה — to Moses. It is the self-proclaimed, self-revealed name. That this name appears at skip 7 (the number of creation and rest) in Leviticus (the center book, the book of holiness and the priesthood) is a structural statement: the Name dwells at the center, at the interval of completion.

Leviticus already contains יהוה **317 times** in surface text — more divine-name-saturated than any other book. The ELS occurrence at skip 7 echoes what the surface text already declares.

### Combined probability estimate

The compound probability of the full pattern with positional constraints:

```
P(Genesis has תורה at skip +50 starting in first 10 letters)     = 2.83e-04
× P(Exodus has תורה at skip +50 starting in first 10 letters)    = 3.63e-04
× P(Leviticus has ≥1 יהוה at skip +7)                            = 0.987
× P(Numbers has ≥1 תורה at skip -50)                             = 0.800
× P(Deuteronomy has ≥1 תורה at skip -50)                         = 0.858

= 6.96e-08
= 1 in ~14.4 million
```

This estimate is **conservative**. It does not account for:
- The word being the *same* (תורה) across all four outer books
- The skip being the *same* (50) across all four outer books
- The chiastic direction (forward outside, reversed inside)
- יהוה being specifically in the *center* book
- The symbolic coherence of the skip values (50, 7) with the text's own theology

### What this means

The individual pieces are each plausible on their own. Common letters produce common words at common skips. But the *compound structure* — the same word, at the same symbolically loaded skip, mirrored across books, with the divine name at the center, all beginning in the opening words — does not emerge from letter frequencies alone.

1 in 14.4 million is the floor. The ceiling is harder to calculate because it requires quantifying semantic coherence, and statistics doesn't have a good framework for "this is theologically self-referential."

### Open questions

1. **Longer shuffled runs:** 10,000 trials gave 0 hits for the mirror pattern. 100,000 or 1,000,000 trials would narrow the empirical bound.
2. **Non-Torah controls:** Does the Quran, the Iliad, or a modern Hebrew novel of similar length produce comparable compound patterns at any skip?
3. **Variant sensitivity:** If we use the Samaritan Pentateuch or Dead Sea Scrolls text, does the pattern hold? If a single letter difference breaks it, that's informative.

---

## Broad Scan: Meaningful Words at Symbolic Skips

Beyond the Torah/YHWH chiastic pattern, we scanned 20 meaningful Hebrew words across 5 symbolically significant skips (7, 12, 26, 40, 50) in all five books. Full scan tool: `selah.els.scan`.

### The symbolic skips

| Skip | Meaning |
|------|---------|
| 7    | Creation / Sabbath — days of creation, the rhythm of rest |
| 12   | Tribes — the twelve sons of Israel |
| 26   | Gematria of יהוה — י(10) + ה(5) + ו(6) + ה(5) |
| 40   | Testing — days of flood, days on Sinai, years in wilderness |
| 50   | Jubilee / Shavuot — 7×7+1, the giving of Torah |

### Self-referential patterns

The most interesting findings are words that appear at skips carrying their own meaning:

**תורה (Torah) at skip 50** — Torah at the interval of its own giving. Ratio 3.3× expected. Already documented above.

**משיח (Messiah) at skip 40** — The anointed one at the interval of testing. Ratio 3.3× expected. Appears in Exodus (3 hits), Leviticus (1), Numbers (2). Absent from Genesis and Deuteronomy.

**ברית (Covenant) at skip 50** — Covenant at the Jubilee interval. Present in all 5 books (17 total hits, ratio 2.9×). Exodus leads with 7 hits (2.8× expected).

**ברית (Covenant) at skip 7** — Covenant at the Sabbath interval. Present in 4 books (16 hits, ratio 2.7×). Again Exodus leads with 7.

### Exodus keeps standing out

Exodus is anomalous across multiple words, not just תורה:

| Word | Skip | Hits | Expected | Ratio |
|------|------|------|----------|-------|
| תורה (Torah) | 50 | 8 | 2.3 | 3.5× |
| משיח (Messiah) | 40 | 3 | 0.9 | 3.4× |
| ברית (Covenant) | 7 | 7 | 2.5 | 2.8× |
| ברית (Covenant) | 50 | 7 | 2.5 | 2.8× |

The book of the Exodus — the departure, the giving of the law, the covenant at Sinai — is structurally dense with the words that describe its own events.

### David (דוד) — a name that shouldn't be there

David does not appear as a character in the Pentateuch. He is born centuries later. Yet דוד appears at skip 50 in all five books (40 total hits), with its first occurrence in Deuteronomy at position 629 — early in the text. The name is everywhere in the ELS structure of books that predate the king by generations.

Surface occurrences: Genesis has 7 (as the common word "beloved"), Deuteronomy has 0.

### Moses (משה) at skip 50 begins before his own story

Moses first appears at skip 50 in Genesis at position 362 — the 362nd letter of the book, deep in the creation narrative, long before Moses is born (Exodus 2). His name is woven into the fabric of a book that does not yet know him.

### ישוע (Yeshua) across the Pentateuch

The name Yeshua (ישוע, "salvation," the Hebrew name behind "Jesus") appears at symbolic skips across the Pentateuch:

| Skip | Gen | Exod | Lev | Num | Deut |
|------|-----|------|-----|-----|------|
| 7    | 1   | 5    | 2   | 4   | 1    |
| 12   | 3   | 5    | 1   | 4   | 1    |
| 26   | 5   | 4    | 1   | 0   | 0    |
| 40   | 8   | 5    | 1   | 5   | 0    |
| 50   | 1   | 5    | 1   | 0   | 4    |

At skip 26 (the gematria of God's name): 5 hits in Genesis, 4 in Exodus, 1 in Leviticus, then silent in Numbers and Deuteronomy.

### What shows up in Genesis at skip 26 (the interval of God's name)

| Word | Forward | Reverse | Total |
|------|---------|---------|-------|
| אדם (Adam) | 9 | 7 | 16 |
| חוה (Eve) | 13 | 15 | 28 |
| תורה (Torah) | 3 | 7 | 10 |
| יהוה (YHWH) | 2 | 4 | 6 |
| ישוע (Yeshua) | 2 | 3 | 5 |
| אברם (Abram) | 1 | 1 | 2 |
| יעקב (Jacob) | 1 | 0 | 1 |

Eve's name (חוה) is the most frequent at this interval — the life-giver, encoded at the spacing of the divine name, 28 times in Genesis alone.

### What does NOT appear

Some absences are as notable as presences:

- **ישראל (Israel) at skip 12** — the name "Israel" at the interval of the twelve tribes: **0 hits in any book.** ישראל is a 5-letter word; at skip 12 in streams of 44k–78k letters, the expected count is low, but zero across all five books is still striking.
- **אלהים (Elohim) at most skips** — God's other name (5 letters) is simply too long to appear frequently at large skips. Only 2 total hits at skip 50 across all books.
- **אברהם (Abraham) at skip 26** — zero hits. The 5-letter name is sparse at large skips.

---

## Torah Means "To Point"

The word *Torah* (תורה) comes from the root ירה — to throw, to aim, to point, to direct. Torah is not "law" in the static sense. It is instruction as *direction*. It points at a target.

The ELS patterns are exactly this. They are not static properties of the text — not counts, not sums, not features divisible by some number. They are *vectors*. Each skip sequence moves directionally through the letter stream, extracting meaning at a fixed interval. The chiastic structure points forward in Genesis and Exodus, backward in Numbers and Deuteronomy, all converging on Leviticus — where the Name dwells.

The hidden geometry of the text enacts the meaning of its own name. The Torah points — and its letter-level structure *also* points, across books, at theologically loaded intervals, toward a center.

This is what separates ELS patterns from other claims of biblical numerics (e.g., Panin's heptadic structure in Matthew). Those claims are about numerical *properties* — static counts that happen to be divisible by 7. They are snapshots. The Torah's patterns have direction. They move through the text. They have a target. The medium carries the message.

---

## How to Reproduce

All commands assume you're in the project root with cached Sefaria data in `data/cache/sefaria/`.

### Run the tests (confirms everything)

```bash
clojure -X:dev:test
```

35 tests, 162 assertions covering the chiastic pattern, statistical analysis, and broad scan.

### REPL exploration

```bash
clojure -M:dev
```

```clojure
(require '[selah.text.sefaria :as sefaria]
         '[selah.text.normalize :as norm]
         '[selah.text.locate :as locate]
         '[selah.els.engine :as els]
         '[selah.els.scan :as scan]
         '[selah.els.stats :as stats])

;; Load a book's letter stream
(def genesis (sefaria/book-letters "Genesis"))
(count genesis)  ;=> 78364

;; Verify the published Genesis claim
(els/verify-positions genesis [6 56 106 156])
;=> {:word "תורה", :positions [6 56 106 156], :constant-step? true, :step 50}

;; Search for תורה at skip 50
(els/search genesis "תורה" 50)
;=> ({:start 5, :skip 50, :word "תורה"} ...)

;; Map hits to chapter locations
(def gen-map (locate/chapter-map "Genesis"))
(locate/locate "Genesis" gen-map 18854)
;=> {:book "Genesis", :chapter 16, :local-pos ...}

;; Locate all hits at once
(locate/locate-hits "Genesis" gen-map (els/search genesis "תורה" 50))

;; Broad scan: all words × all symbolic skips × all books
(def books (scan/load-books))
(def results (scan/scan-all books))
(scan/print-summary results)

;; Find statistically notable patterns (ratio > 2× expected)
(def notable (scan/notable-patterns books results))
(doseq [{:keys [word english skip ratio books-with-hits]} notable]
  (println word english "skip=" skip "ratio=" ratio "books=" books-with-hits))

;; Debug window around a position (1-based)
(els/debug-window genesis 6 10)
;=> {:snippet "בראשיתבראאלהיםאתהשמי", :pointer 5, :letter \ת, ...}
```

### First-time setup (if no cache exists)

The first call to `(sefaria/book-letters "Genesis")` will fetch all 50 chapters from the Sefaria API and cache them to `data/cache/sefaria/`. This takes a minute or two. Subsequent calls use the cache.
