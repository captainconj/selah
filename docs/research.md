# Research Directions

*What we've established, and where to look next.*

---

## What We Know

The chiastic Torah Codes pattern is confirmed and statistically characterized:

- תורה at skip +50 in Genesis (3 hits, first at letter 6) and Exodus (8 hits, first at letter 8)
- יהוה at skip +7 in Leviticus (7 hits, landing at Yom Kippur, "I am YHWH," and the covenant)
- תורה at skip -50 (reversed) in Numbers (2 hits) and Deuteronomy (3 hits)
- Compound probability: 1 in 14.4 million (conservative, assumes independence)
- 0/10,000 shuffled trials reproduce even the basic mirror pattern
- Skip values (50, 7) are not free parameters — they come from the text's own theology
- The hits are not randomly placed — they land at narratively significant locations
- Broad scan reveals additional self-referential patterns (Messiah at 40, Covenant at 7 and 50, Eve at 26)

The signal is not in the hit counts. It's in the structure, the placement, and the self-reference.

---

## 1. Variant Text Sensitivity

**Question:** Does the pattern survive across different manuscript traditions?

The Masoretic Text (MT) is our current source. But the Hebrew Bible exists in multiple ancient witnesses:

| Variant | Divergences from MT | Availability |
|---------|-------------------|--------------|
| Samaritan Pentateuch | ~6,000 differences | Digitized, accessible |
| Dead Sea Scrolls (DSS) | Fragmentary, varies by book | Partial, some books well-preserved |
| Septuagint back-translation | Greek → Hebrew reconstruction | Scholarly reconstructions exist |

**What we'd learn:**
- If a single letter difference in the Samaritan text breaks the pattern → the encoding is precise and fragile, implying exact authorial control at the letter level
- If the pattern survives across variants → it's a deeper structural property that transcends orthographic variation
- If it partially survives → we learn which parts of the pattern are robust and which are delicate

**Approach:**
1. Obtain Samaritan Pentateuch text (most complete alternative)
2. Normalize identically to our MT pipeline
3. Run the same chiastic pattern test
4. Diff the two letter streams — where do they diverge?
5. For each divergence, check: does this break any ELS hit?

**Implementation:** `selah.variants` namespace — alignment, diff, comparative ELS.

---

## 2. Non-Torah Controls

**Question:** Is this a property of the Torah specifically, or of Hebrew text in general, or of any sufficiently long text?

We've shown the pattern doesn't appear in shuffled Torah. But shuffling destroys all structure — linguistic, narrative, everything. A fairer control is a *real text* of comparable length and language.

**Control texts to test:**

| Text | Language | Length (est.) | Why this control |
|------|----------|---------------|------------------|
| Modern Hebrew novel | Hebrew | ~300k letters | Same alphabet, same letter frequencies, no sacred claim |
| Book of Isaiah | Hebrew | ~66k letters | Sacred text, but not Pentateuch — does the pattern extend? |
| Quran (Arabic) | Arabic | ~300k letters | Sacred text of a different tradition, different alphabet |
| Homer's Iliad (Greek) | Greek | ~300k letters | Ancient literary text, no sacred claim |

**What we'd learn:**
- If a modern Hebrew novel produces comparable compound patterns → it's a language property, not a Torah property
- If Isaiah shows similar patterns → the encoding extends beyond the Five Books
- If no control text matches → the Torah is structurally unique among texts of its kind

**Approach:**
1. Start with a modern Hebrew novel (easiest to obtain, same alphabet)
2. Normalize to Hebrew letters, build letter stream
3. Pick a "meaningful" 4-letter word relevant to that text
4. Run the same compound test: does the word appear at any skip in the opening of multiple sections, with mirrored direction?
5. Run shuffled controls on the control text for comparison

---

## 3. Proximity Mapping

**Question:** Do meaningful words cluster spatially when they share a skip?

We know where individual words land. We haven't asked: when תורה, יהוה, משיח, and ברית all appear at the same skip in the same book — are they near each other? Do they form spatial clusters?

**What we'd learn:**
- Random hits would scatter uniformly across the stream
- Clustered hits suggest coordinated placement — the words aren't just present, they're *together*
- Cross-word proximity at the same skip is a much harder pattern to produce by chance

**Approach:**
1. For each symbolic skip, find all meaningful-word hits in each book
2. Map their starting positions
3. Look for clusters: regions where multiple different words appear within a small window
4. Calculate expected vs observed cluster density
5. Identify the most dense regions — what is the text talking about there?

**Implementation:** `selah.proximity` namespace — spatial analysis, cluster detection.

---

## 4. Visualization

**Question:** Can we make the pattern visible?

The chiastic structure should be *seeable* — five horizontal bars (the books), with arrows showing where each word surfaces and in which direction. Torah arrows pointing inward from the outer books, YHWH at the center.

**What to build:**
- SVG or HTML rendering of all five books as horizontal streams
- Color-coded markers for each word at each skip
- Direction arrows (forward vs reverse)
- The chiastic symmetry should be immediately apparent
- Zoomable: click a hit to see its chapter and narrative context

**Why it matters:**
Numbers convince the mind. Images convince the heart. The chiastic structure is elegant — it deserves to be seen, not just tabulated.

**Implementation:** `selah.viz` namespace or standalone HTML output.

---

## Priority Order

1. **Variant sensitivity** — the most scientifically rigorous next step. Answers "how precise is this?"
2. **Non-Torah controls** — the strongest skeptical challenge. Answers "is this unique?"
3. **Proximity mapping** — deepens what we already have. Answers "is there more structure?"
4. **Visualization** — communicates what we've found. Answers "can others see it?"

---

## Principles

Carried forward from `docs/plan.md`:

- **Reproducibility over mystery.** Every result traceable to a specific source, normalization, and parameter set.
- **REPL-first.** This is exploration, not a product.
- **One source of truth at a time.** Don't blend texts. Keep streams separate.
- **Show your work.** The code is the proof.
