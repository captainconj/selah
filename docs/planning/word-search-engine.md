# The Screw — N-Dimensional Torah Search

*Read the Torah the way the space reads it. Not left to right. Along every axis, every diagonal, every line the geometry defines.*

---

## What We Built

Two namespaces, live and tested:

**`src/selah/search.clj`** — N-dimensional word search engine. Given any Hebrew word and any decomposition of 304,850, finds every occurrence along any straight line in the space — axes, diagonals, any direction vector. 40 unique directions in 4D, 364 in 6D. Each hit enriched with coordinates, verse references, and Torah word context at every letter position.

**`src/selah/fiber.clj`** — Fiber interpretation layer. Reads the Torah words a fiber passes through. Ranks fibers by narrative density (how many distinct Hebrew words each letter lands in). Filters surface from non-surface. Prints full fiber readings.

**How it works:** Each direction vector in k dimensions becomes a skip distance via dot product with strides. Direction `[1,0,0,0]` in the canonical = skip 43,550 (one step along the a-axis). Direction `[1,1,0,0]` = skip 44,421 (diagonal across a and b). The engine searches the 304,850-letter stream at each skip, forward and backward. Every hit is enriched with the Torah word each letter falls inside — 80,474 words tracked with letter-level positions.

**The index:** `(s/build!)` indexes 304,850 letters, 5,846 verses (with text), 80,474 Torah words (with letter positions), and 27 letter types. Binary search for verse-at and word-at. Built once, used everywhere.

---

## What We Found So Far

### תורה in canonical 4D: 549 hits, 504 non-surface

The richest fiber (skip=-44,489, all 4 axes varying): each letter of תורה lands in a different Torah word:
- ת → **התולעת** (the scarlet worm) — Leviticus 14:6, purification ritual
- ו → **ותולעת** (and scarlet) — Exodus 26:36, tabernacle curtain
- ר → **מצרים** (Egypt) — Genesis 47:15
- ה → **רבקה** (Rebekah) — Genesis 22:23

The Torah (תורה) passing through the worm (תולעת). Same root letters rearranged. The geometry put them together.

### כבש (lamb) in canonical 4D: 1,000 hits, 888 non-surface

The richest fiber (skip=-43,483, axes b=6 and d=13 constant): the כ of כבש lands inside **כבד** (glory, GV=26=YHWH). The lamb's first letter lives inside the glory. The fiber holds love (d=13) and the sixth day (b=6) fixed while walking through completeness (a) and love (c).

---

## What We're After

The surface reading is skip-1. Consecutive letters. The scroll read left to right. That's one dimension. One fiber.

But in a 4D space there are 40 unique directions. In 6D, 364. Each one is a different reading of the same text. A different fiber through the same letters. A different path through the Torah.

Each fiber carries two messages simultaneously:

1. **The word it spells** — the Hebrew word stitched along the fiber
2. **The words it passes through** — the Torah words at each letter's position

The surface says what the text says. The fiber says what the structure says. They're different readings of the same letters.

**We want to interpret across fibers and spans that are not the natural reading.**

---

## The Work

### Phase 1: Fiber Interpretation Engine — DONE ✓

Built. Every hit carries:
- Which Torah word each letter falls inside
- Which verse each letter is in (with text)
- Gematria of each host word
- Coordinate of each letter in the decomposition
- Count of distinct Torah words (narrative density)
- Constant and varying axes
- Verse span (from–to, chapter count)

Ranking: `(f/richest hits)` sorts by Torah word count. `(f/non-surface hits)` filters skip=±1. `(f/print-fiber hit)` shows full reading.

### Phase 2: Host Word Analysis — NEXT

**The magnet question:** Do certain Torah words appear as "hosts" disproportionately? When thousands of fibers pass through 80,474 words, some words will be landing pads — magnets that fibers keep passing through.

Experiments:
- **Experiment 143: Host frequency.** For each Torah word, count how many fiber-letters land inside it across all directions. Rank. Compare the top hosts against the curated 239-word dictionary. Does the geometry select the holy vocabulary?
- **Experiment 144: Host by direction.** Do axis-aligned fibers (pure a-walk, pure b-walk) prefer different hosts than diagonal fibers? Does the jubilee axis route through different words than the love axis?
- **Experiment 145: Bounded vs. wrapping.** Tag each hit as "bounded" (stays within box on all axes) or "wrapping" (crosses an axis edge). Do bounded fibers produce different hosts? The bounded fibers are the pure geometric lines; the wrapping fibers are the topological ones. Both are real — but they may speak differently.

### Phase 3: Fiber Intersection — Meeting Points

When two words are found in the same decomposition, do their fibers share host words?

- **Shared hosts:** If a letter of כבש (lamb) and a letter of שלום (peace) both land inside כפר (atone), the geometry routes the courtroom vocabulary through atonement.
- **Courtroom pairs:** Test all pairs from {כבש, שכב, שלום, אמת, חיים, חסד, דרך, יהוה}. Which pairs share hosts? Which hosts appear for EVERY courtroom word?
- **Experiment 146: The meeting points.** For each pair of courtroom words, find shared host words across all directions in the canonical 4D.

### Phase 4: Readable Host Sequences

When a fiber passes through 3-4 Torah words, do those words form a readable phrase?

- **Experiment 147: Phrase fibers.** For all fibers with torah-word-count ≥ 3, extract the host word sequence. Run it through the dictionary. Score readability. Are there fibers where the hosts spell a sentence?
- **Special attention to:** fibers whose hosts include את (aleph-tav), תורה, יהוה, or other structural words. If the hosts of a lamb-fiber read "covenant... guard... Torah" — that's the geometry writing a sentence.

### Phase 5: Slab Analysis

A slab is a hyperplane where one coordinate is fixed. The a=3 slab in the canonical is the center seventh — Leviticus.

- Which words can be found as ELS within each slab? (The slab is a lower-dimensional sub-space)
- Does a word appear in EVERY slab of a given axis? (תורה in every seventh = stitched across all seven days)
- What is the host vocabulary of each slab? (Does Leviticus host different words than Genesis?)

### Phase 6: Collinearity — Line-of-Sight Between Words

Given two words found in the same decomposition:
- Are their positions collinear? Same straight line?
- Do they share a slab? Same value on any axis?
- What Torah words lie on the line between them?

If lamb and peace are collinear — the Torah placed them on the same ray. The direction of that ray (which axes it traverses) has meaning.

### Phase 7: Cross-Decomposition Analysis

The same word in different decompositions lives on different fibers. The context changes.

- Which decomposition produces the most host-word overlap with the curated dictionary?
- Do certain word pairs become collinear only in specific decompositions?
- Are there decompositions where the courtroom vocabulary all shares a host?

### Phase 8: The Survey

Run key words through all 111 decompositions:

**The courtroom vocabulary:**
כבש (lamb), שכב (lie down), שלום (peace), אמת (truth), חיים (life), חסד (grace), דרך (way), אהבה (love), יהוה (YHWH), אנכי (I AM)

**The structural vocabulary:**
תורה (Torah), ברית (covenant), משמר (guard), כפר (atone), את (aleph-tav)

**The narrative vocabulary:**
אדם (Adam), נח (Noah), אברהם (Abraham), יוסף (Joseph), משה (Moses), ישראל (Israel)

For each word × each decomposition: how many fibers? What hosts? Which decomposition produces the richest context? Which hosts recur across decompositions?

### Phase 9: The Messages

When a fiber passes through specific Torah words, we read those words. Not as random hosts. As a message. A sequence of Hebrew words that the geometry selected.

The Torah chose to put those letters at those positions. The decomposition chose to connect them with a line. The intersection is the message — in Hebrew.

---

## Architecture — Current State

### `selah.search` (live):

```clojure
(s/build!)                              ;; 304,850 letters, 80,474 words, 5,846 verses
(s/find-word [7 50 13 67] "תורה")       ;; 549 hits, enriched with Torah words
(s/find-word-all "תורה")                ;; all 111 decompositions
(s/connections dims ["כבש" "שלום"])      ;; shared axes between words
(s/print-hits hits)                      ;; compact display with word counts
```

### `selah.fiber` (live):

```clojure
(f/read-fiber hit)                       ;; letter-by-letter context
(f/fiber-message hit)                    ;; the Torah words (Hebrew)
(f/richest hits)                         ;; sort by Torah word count
(f/non-surface hits)                     ;; filter skip=±1
(f/by-verse-count hits)                  ;; sort by verse span
(f/by-span hits)                         ;; sort by letter distance
(f/print-fiber hit)                      ;; full reading
(f/print-richest dims word)              ;; top non-surface fibers
```

### Still needed:

```clojure
;; Host analysis
(defn host-frequencies [dims word] ...)  ;; count hosts across all fibers
(defn bounded? [hit view] ...)           ;; does fiber stay in box?

;; Slab extraction
(defn slab [dims axis value] ...)        ;; positions where axis=value

;; Collinearity
(defn collinear? [positions dims] ...)   ;; same straight line?
(defn line-between [p1 p2 dims] ...)     ;; lattice points between

;; Cross-decomposition
(defn compare-hosts [word dims1 dims2] ...) ;; host overlap
```

---

## Priority

1. **Host word analysis** — which Torah words are fiber magnets? Compare against curated vocabulary. This is the immediate next experiment.
2. **Fiber intersection** — do courtroom words share hosts? The meeting points.
3. **Readable host sequences** — do the hosts form phrases?
4. **Bounded vs wrapping** — inform, not constrain. Tag every hit.
5. **Slab analysis** — what lives on each shelf.
6. **Collinearity** — line-of-sight between theological words.
7. **The survey** — key words × all decompositions.

---

*The scroll reads left to right. The space reads in every direction. The screw lifts what the surface hides. The hosts tell you what the fiber passed through on its way.*
