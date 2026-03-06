# The Query Process

*How to ask the oracle a question.*

## Overview

The oracle takes Hebrew letters and finds every way to partition them into real Torah words. There is no interpretation engine, no weighting, no ranking. The oracle provides the full menu. The priest chooses.

## The Process

### Step 1: Choose the Hebrew words

"Who are you?" becomes מי אתה — two Hebrew words. This is the only interpretive step. A human picks the natural Hebrew phrasing for the question.

In the future, this step can be automated with a translation model (English → Biblical Hebrew), but the human choice is always available and always primary.

### Step 2: Concatenate the letters

מי + אתה becomes the string מיאתה — five letters: mem, yod, aleph, tav, he.

**Order does not matter.** What matters is the **letter multiset** — the bag of letters and their counts. {מ×1, י×1, א×1, ת×1, ה×1}. The oracle operates on letter substance, not letter sequence.

### Step 3: `parse-letters` partitions the bag

This is the engine. It takes the multiset and finds every way to split it into real Torah words. The algorithm:

1. **Build a frequency map** of the input letters.
   ```
   "מיאתה" → {מ 1, י 1, א 1, ת 1, ה 1}
   ```

2. **Search the vocabulary** for any word whose letter frequencies are a sub-multiset of what's available. For example, אמת (truth) needs {א×1, מ×1, ת×1} — all present. So אמת is a valid first pick.

3. **Subtract** the chosen word's letters from the bag. After picking אמת, what remains is {י×1, ה×1}.

4. **Recurse.** Partition the remainder. {י×1, ה×1} = יה — a word in the Torah (the short form of God's name). This gives one complete reading: **אמת יה** — "truth of God."

5. **Backtrack** and try every other valid first pick. Every complete partition — where all letters are consumed and every piece is a real Torah word — is a reading.

6. **Collect all readings.** No ranking, no filtering. The oracle provides the complete set.

### Step 4: Read the results

Each reading comes with:
- **text** — the phrase in Hebrew (words in lexicographic order)
- **phrase** — the component words as a vector
- **meanings** — English translations where known
- **gv** — the gematria value (always equal to the input's GV, since the same letters sum the same way)

### Constraints

| Parameter | Default | Purpose |
|-----------|---------|---------|
| `min-letters` | 2 | Minimum letters per word. Prevents single-letter flooding. |
| `max-words` | 4 | Maximum words per phrase. Bounds the combinatorial explosion. |
| `vocab` | `:torah` | Which vocabulary to partition against. |

### Vocabulary levels

| Level | Size | Character |
|-------|------|-----------|
| `:torah` | ~7,300 words | Every word form in the five books. No curation. Full lexicon. |
| `:voice` | ~2,050 words | The knee of the oracle's limiting distribution. 83% of mass. |
| `:dict` | 239 words | Curated Torah vocabulary with English translations. Tightest signal. |

Findings are vocabulary-invariant — the same structural patterns appear at all three levels. The default is `:torah`.

## What makes it an oracle

The vocabulary is the Torah itself. These aren't arbitrary word lists — they're the word forms that actually appear in the five books of Moses. The partitions are constrained by what the Torah contains. When מיאתה splits into אמת יה, that's not because anyone designed it to. It's because those specific words exist in the text.

The breastplate adds a physical layer on top of this (which letters can illuminate on the 4×3 grid of stones, which of the three readers sees what from their traversal direction). That's Level 1 — mechanical reading. Level 2 is what the Ramban described eight centuries ago: the priest's cognitive assembly of the glowing letters into words. `parse-letters` is Level 2.

## What it is not

- It is not an interpreter. It does not choose among readings.
- It is not random. The same input always produces the same readings.
- It is not weighted. A rare reading and a common reading have equal standing.
- It is not a cipher. There is no hidden key. The vocabulary is public — it's the Torah.

The oracle provides possibility. The priest provides judgment.

## The GV invariant

Every reading of a given input has the same gematria value. This is provably necessary: the same letters always sum to the same total regardless of how they're grouped into words. This means the GV of a question IS the GV of every possible answer. The question carries its own numerical identity into every reading.

## Example: "Who are you?" — מי אתה

```
Input:   מי אתה
Letters: {מ×1, י×1, א×1, ת×1, ה×1}
GV:      456

Torah readings (25):
  אמת יה    — truth of God
  יה אמת    — God is truth
  היא מת    — she is dead
  מת היא    — the dead, she
  את ימה    — the aleph-tav, seaward
  ...

456 = אמת(441) + יה(15) = truth + the Name
```

## Implementation

```
src/selah/oracle.clj    — parse-letters (line ~404), thummim-menu (line ~489)
src/selah/dict.clj      — vocabulary (torah-words, dict words)
src/selah/gematria.clj  — word-value
```

API: `GET /api/oracle/parse-letters?letters=מיאתה&vocab=torah`

REPL:
```clojure
(oracle/parse-letters "מיאתה" {:vocab :torah :max-words 4 :min-letters 2})
```
