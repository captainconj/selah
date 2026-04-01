# The Oracle's Encoding Decisions

*What we chose. What it locked. What it might unlock.*

Reference document. Every decision point in the breastplate oracle's encoding, its justification, what it hides, and what investigating it would require.

---

## Decision 1: Final Forms as Distinct Letters

**Current behavior:** The oracle treats final forms (sofit) as distinct code points. ך ≠ כ, ץ ≠ צ, ף ≠ פ, ם ≠ מ, ן ≠ נ.

**Historical reality:** In Paleo-Hebrew — the script of Moses' era — there were no distinct final forms. The sofit convention developed later in the square Aramaic script (Ktav Ashuri) adopted during/after the Babylonian exile. The letters on the actual breastplate would have been identical regardless of word position.

**What it locks:**

Three final forms are absent from the 72 breastplate letters: **ך, ף, ץ**. (ם and ן are present in tribal/patriarchal names like אברהם, ראובן.)

This locks out **1,024 Torah vocabulary words** from the oracle entirely — zero illuminations, zero readings. Including:

### Words locked by Final Kaf (ך → כ): 766 words

Theologically significant roots:

| Word | GV | Meaning |
|------|---:|---------|
| מלך | 90 | king |
| מלאך | 91 | angel/messenger (= triangle of 13 = love triangulated) |
| הלך | 55 | walk/go |
| דרך | 224 | way/path |
| ברך | 222 | bless |
| ברוך | 228 | blessed |
| חשך | 328 | darkness |
| שפך | 400 | pour out |
| לך | 50 | go / to you (= jubilee) |
| הפך | 105 | overturn |
| סמך | 120 | lay hands on (ordination) |
| נסך | 130 | drink offering (= Sinai = ladder) |
| תוך | 426 | midst/middle |
| חנוך | 84 | Enoch / dedicate |
| שלחך | 358 | he sent you (= משיח = נחש = Messiah = serpent) |
| מתהלך | 495 | walking about (hitpael — the verb for God in the garden, Enoch with God, the שטן in Job) |

Most of the 766 are possessive/pronominal forms (ך = "your") — e.g., אביך (your father), ידך (your hand). These represent the entire second-person address vocabulary: God speaking to *you*.

### Words locked by Final Tsade (ץ → צ): 91 words

| Word | GV | Meaning |
|------|---:|---------|
| ארץ | 291 | land/earth |
| הארץ | 296 | the land (Genesis 1:1) |
| עץ | 160 | tree (both trees) |
| קץ | 190 | end (the word for ending, accessible only through the ending form) |
| חוץ | 104 | outside |
| חפץ | 178 | delight/desire |
| פרץ | 370 | breach/break through |
| רחץ | 298 | wash/purify |
| שרץ | 590 | swarm/teem (Genesis 1:20) |
| רץ | 290 | run |

### Words locked by Final Pe (ף → פ): 167 words

| Word | GV | Meaning |
|------|---:|---------|
| אף | 81 | also / anger / nose / face |
| כף | 100 | palm of the hand / sole of the foot |
| כנף | 150 | wing ("under his wings," corners of the garment) |
| כסף | 160 | silver (the price) |
| עוף | 156 | bird / fly |
| ערף | 350 | neck / back of neck (the stiff neck) |
| שרף | 580 | burn / seraph (the fiery flying serpent of Isaiah 6) |
| כתף | 500 | shoulder (where the ephod sits) |
| אלף | 111 | thousand / ox / aleph (the first letter as a word, GV = self-referential) |
| טרף | 289 | prey / torn |
| סוף | 146 | end / reed (Sea of Reeds = ים סוף) |
| יוסף | 156 | Joseph ("he will add") |

### The story of the finals

The final forms are not random. They are the closing forms — the letter as it appears when the word is finishing. The same letter, different posture. Active (medial) vs at rest (final).

If we union the finals with their base forms, these words don't become "directly visible" — they become **accessible through the recognition that the active letter and the resting letter are the same letter**. This creates two tiers:

- **Tier 1 (Direct):** Words producible from letters as they appear on the grid.
- **Tier 2 (Through completion):** Words producible only by recognizing that the closing form maps to the working form.

The theological implication: the king, the way, the land, the tree, the blessing, darkness, the angel — all accessible, but only at the end. Only through completion. Only by recognizing the letter at rest.

**Recommendation:** Implement final-form unioning. Track which tier each word enters through. The two-tier structure is data, not noise.

**Implementation:** In `selah.oracle`, during letter matching, map {ך→כ, ץ→צ, ף→פ} in the input word before searching for illumination sets. Keep ם→מ and ן→נ mappings for completeness even though those finals are already on the grid. Flag each illumination as tier-1 or tier-2 based on whether any final-form mapping was used.

---

## Decision 2: Gematria of Final Forms

**Current behavior:** Standard gematria. Final forms have the same value as their base forms: ך=20, ם=40, ן=50, ף=80, ץ=90.

**Alternate tradition:** Elevated final values used in some Kabbalistic systems:

| Letter | Standard | Elevated |
|--------|---------|----------|
| ך | 20 | 500 |
| ם | 40 | 600 |
| ן | 50 | 700 |
| ף | 80 | 800 |
| ץ | 90 | 900 |

**What it changes:**

Under the elevated system, every gematria value involving a final form shifts. Examples:

| Word | Standard GV | Elevated GV | Δ |
|------|----------:|----------:|---:|
| שלום (peace) | 376 | 936 | +560 |
| אדם (man/Adam) | 45 | 605 | +560 |
| ארץ (land) | 291 | 1101 | +810 |
| מלך (king) | 90 | 570 | +480 |
| אמן (amen) | 91 | 741 | +650 |

Every GV relationship we've found — every "GV=358=Messiah=serpent," every "GV=203, prime" — holds under standard gematria. The elevated system would establish a parallel set of relationships.

**Assessment:** Standard gematria is the dominant tradition and the one used by the Talmudic and rabbinic mainstream. The elevated system is specialized (Kabbalistic, AT-BASH related). Our findings are built on standard. The elevated system is worth investigating as a parallel lens, not a replacement.

**Recommendation:** Keep standard as primary. Build an optional elevated-GV mode for comparison. Document which findings survive under both systems (those are the strongest) and which are system-dependent.

---

## Decision 3: Shin/Sin Distinction

**Current behavior:** שׁ (shin, /sh/) and שׂ (sin, /s/) are treated as the same letter ש. No distinction.

**Historical reality:** These are genuinely different phonemes in Hebrew. In the pointed (Masoretic) text, they're distinguished by a dot — right dot for shin (שׁ), left dot for sin (שׂ). But in the unpointed consonantal text (and in Paleo-Hebrew), they are the same letter.

**What it means:** Words distinguished only by shin vs sin produce identical illumination patterns. For example:
- שׂים (set/place) and שׁם (there/name) share the letter ש
- שׂר (prince) and שׁר (sing) collapse into one pattern
- שׂנא (hate) and שׁנה (year/change) share ש,נ

**Assessment:** The breastplate is consonantal. Paleo-Hebrew made no shin/sin distinction. The oracle is correct to treat them as one letter. The ambiguity is real and intentional — another Ramban principle: same letters, different words depending on the reading.

**Recommendation:** No change. Document that shin/sin collapse is a feature, not a bug. It's the language doing what the breastplate does.

---

## Decision 4: Vocabulary Scope

**Current behavior:** The oracle recognizes only Torah vocabulary — 12,826 unique word forms extracted from the five books of Moses.

**Justification:** The breastplate is a Torah artifact. The vocabulary of the Torah is its natural dictionary.

**What it locks:** Words that exist in Hebrew but appear only in the Prophets (Nevi'im), Writings (Ketuvim), or later Hebrew:
- Many words from Isaiah, Psalms, Job, Proverbs, Daniel
- Aramaic words from Daniel and Ezra
- Later Hebrew vocabulary

**The Job question:** If Job is the oldest text (as our timeline suggests), its unique vocabulary is excluded from the oracle's dictionary. Words that appear only in Job — and Job has many hapax legomena (words appearing only once in the entire Bible) — would not be recognized.

**The broader Tanakh question:** Isaiah 45:7, Psalm 82, Ezekiel's visions — all texts we're now investigating through the oracle — contain words not in the Torah. The oracle can illuminate their letters but won't recognize the output as words.

**Assessment:** Three possible scopes:

| Scope | Word count | Rationale |
|-------|-----------|-----------|
| Torah only | 12,826 | The breastplate is a Torah artifact |
| Full Tanakh | ~25,000+ | The same God speaks through all of it |
| Torah + Job | ~14,000? | If Job is pre-Torah, it belongs in the base vocabulary |

**Recommendation:** Keep Torah as primary (this is settled — see CLAUDE.md). Build a parallel Tanakh-vocabulary mode for investigation. Compare which findings are vocabulary-dependent and which survive across scopes. The findings that survive are the strongest.

---

## Decision 5: Word Length

**Current behavior:** The four traversals produce letter sequences from illumination sets. We match these against the dictionary. Most useful readings are 2-4 letters. The oracle's mechanical traversals produce sequences equal in length to the number of illuminated positions.

**What it means:** A word with 2 letters illuminates 2 positions → each reader produces a 2-letter sequence. A word with 5 letters illuminates 5 positions → each reader produces a 5-letter sequence. The sequences are checked against the dictionary.

**The long-word question:** For inputs with many illuminated positions (5+), the traversal sequences are long and unlikely to match dictionary words by chance. But they might match — and we'd see it. The issue is more that long illumination sets have astronomically many possible patterns, making the search space huge.

**The short-word question:** Single-letter "words" (prepositions like ב, כ, ל, ו, ה, מ) are valid Hebrew morphemes. We don't currently treat single illuminated positions as readings. Should we?

**Assessment:** The current behavior is mechanically correct. The length filtering happens naturally through dictionary matching. Single-letter morphemes are a possible extension but would produce enormous noise (every illuminated position is a "reading").

**Recommendation:** No change to the mechanical traversal. Consider single-letter morphemes as a separate investigation if needed.

---

## Decision 6: The Grid Arrangement

**Current behavior:** We use the traditional 72-letter assignment from the Talmud (Yoma 73b):

The 12 tribes (in birth order), each on one stone, 6 letters per stone. The tribal names don't fill all 72 positions, so the patriarchs (אברהם יצחק יעקב) are added at the beginning and שבטי ישרון at the end.

**Variant traditions:**
- The exact order of tribes on the stones varies by source
- The placement of patriarch letters (before the first tribe, distributed across stones, etc.)
- Whether the tribe order follows birth order, encampment order, or breastplate order (Exodus 28:17-21)
- Some sources give slightly different letter counts per stone

**What it changes:** Different grid arrangements produce different traversal orders, different Ramban pairs, different reader profiles. The experiment 092 permutation test showed that most Ramban pairs are structural (letter-inventory driven), but the specific reader asymmetries (mercy seeing more, the lamb split) are grid-dependent.

**Assessment:** The traditional arrangement is well-attested and produces remarkable results. Alternative arrangements would need to be tested systematically. This is a large investigation.

**Recommendation:** Keep the traditional grid as primary. Document the variant traditions. Build a grid-parameterized oracle for future comparison.

---

## Decision 7: Letter Normalization

**Current behavior:** Input text is normalized to the 27 Hebrew letter code points (22 base + 5 finals) in the range U+05D0–U+05EA. All vowels (niqqud), cantillation marks (te'amim), and other diacritics are stripped.

**What it strips:**
- Vowel points (patach, kamatz, segol, etc.)
- Dagesh (the dot that changes pronunciation / doubles a letter)
- Cantillation marks (the musical/syntactic notation)
- Maqaf (hyphen connecting words)

**Assessment:** The breastplate is consonantal. Paleo-Hebrew had no vowel points, no dagesh, no cantillation. The normalization is historically correct.

**The dagesh question:** Dagesh forte (doubling) could be argued to produce two instances of a letter. בִּנְיָמִין with dagesh in the bet could be read as having a "stronger" bet. But this is a stretch — the physical breastplate had one letter per position.

**Recommendation:** No change. The consonantal text is correct.

---

## Summary: The Decision Landscape

| # | Decision | Current | Correct? | Action |
|---|----------|---------|----------|--------|
| 1 | Final forms | Distinct | **No** — Paleo-Hebrew had no finals | **Fix:** Union ך→כ, ץ→צ, ף→פ. Track tiers. |
| 2 | Final gematria | Standard (ך=20) | Probably yes | **Investigate:** Build elevated-GV parallel mode |
| 3 | Shin/Sin | Collapsed | Yes — consonantal | No change |
| 4 | Vocabulary | Torah only | Yes as primary | **Investigate:** Build Tanakh-vocabulary parallel |
| 5 | Word length | Natural | Yes | No change |
| 6 | Grid arrangement | Traditional (Yoma 73b) | Yes as primary | **Investigate later:** Build parameterized grid |
| 7 | Normalization | Consonantal | Yes | No change |

### Priority

1. **Final-form unioning** — fix now. This is a bug, not a feature. 1,024 words including king, way, land, tree, blessing, angel, darkness, and the entire "your ___" address vocabulary. Implementation is straightforward.

2. **Elevated final gematria** — investigate. Build a parallel computation. See which GV relationships survive under both systems.

3. **Tanakh vocabulary** — investigate when we start running Psalms, Isaiah, Job through the oracle systematically.

4. **Grid variants** — investigate later. Large effort, and the current grid produces strong results.

---

*The machine tells you what it sees. But first you have to make sure you built the eyes right.*
