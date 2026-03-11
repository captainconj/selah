# Ark / Code / Genome Doc Audit

Type: `audit / cleanup boundary`
State: `clean`

Date: 2026-03-10

This note audits the Ark/code/genome docs that lean most heavily on the strengthened lower layers:

- `docs/ark/20-experiment-100.md`
- `docs/ark/22-the-map.md`
- `docs/ark/26-hit-play.md`
- `docs/ark/31-the-library-so-far.md`
- `docs/ark/33-the-genome.md`

The standard here is simple:

- what is now fully earned by rebuilt artifacts
- what is still broadly right but needs tighter wording
- what is currently speaking ahead of the rebuilt evidence

## Summary

### Strongest

- `20-experiment-100.md`
- `22-the-map.md`

These are now materially stronger because the underlying `100/101` layer has been rerun through experiment namespaces, not just notebook fragments.

### Mixed but salvageable

- `26-hit-play.md`
- `31-the-library-so-far.md`
- `33-the-genome.md`

These are not dead. But they are more exposed to narrative compression, legacy English framing, and summary tables that are no longer tight enough to the rebuilt artifacts.

## File-by-File

## `docs/ark/20-experiment-100.md`

### What stands cleanly

- `64 + 8`
- backbone letters `ו י נ`
- backbone positions `26`
- `את` skipping column `2`
- `כבש/שכב` same-stone claim
- row/column singleton structure
- `ק` as row-1-exclusive non-singleton

### What needs tightening

- The phrase "The Name IS the backbone" is interpretive language laid directly on a structural observation.
  The underlying observation is strong.
  The sentence is witness-language, not technical language.
- The "double helix" phrasing is still a metaphor laid over row-pair structure.
  It is fair as interpretation, but it should not sound like the experiment directly proves molecular helicity.
- The singleton-word mini-vocabulary is interesting but still a higher-selection surface than the core split/backbone claims.

### Judgment

`Strong, mostly earned, only needs distinction between structure and interpretation.`

## `docs/ark/22-the-map.md`

### What stands cleanly

- top-scoring mapping `1=A, 2=C, 3=G, 4=U`
- second mapping tied on raw correlation
- Watson-Crick constraint breaking that tie
- assignment score `13.888299198575803`
- frequency correlation `0.6854196827438999`
- unmapped `א`
- start-codon stones `[1 11 9]`
- stop on `ס`
- translated breastplate sequence

### What is materially better now

- `p < 0.0001` is no longer just a printed line from a helper script.
  The experiment namespace now computes a real `10,000`-trial permutation result with `0` exceedances.

### What needs tightening

- The doc still reads with total confidence where the actual rebuilt state is slightly more nuanced:
  raw correlation tie first, Watson-Crick filter second.
- The phrase "the only letter that doesn't fit any amino acid" is directionally right, but the rebuilt artifact is more specific:
  `א` is the unmapped remainder under the greedy assignment and has the weakest top similarity profile.
- The long protein-reading section is more interpretive than the mapping and assignment sections.
  It should stay, but it should be visibly downstream from the map rather than blended with it.

### Judgment

`Strong and substantially earned. Mostly needs precision and ordering, not conceptual retreat.`

## `docs/ark/26-hit-play.md`

### What stands

- The bidirectional claim is fair:
  the map is being used both Hebrew→protein and protein→Hebrew.
- The high-level transition from map to protein playback is the real project move.

### What needs tightening

- "Twenty proteins from the book of life" is stale framing now that the library/corpus boundaries have moved and expanded.
- The protein list is a rhetorical library list, not a provenance-aware catalog.
  That role is now better served by `docs/reference/protein-catalog.md`.
- The calf/breastplate contrast is thematic framing, not reproducibility framing.
  Fine as a doorway, but not evidence.

### Judgment

`Good threshold document, but it should be reframed more clearly as transition/theology rather than evidence report.`

## `docs/ark/31-the-library-so-far.md`

### What stands broadly

- The 21-protein library boundary is real in `src/selah/dna.clj`.
- Many headline patterns are anchored in rebuilt library artifacts:
  - p53 `David`
  - serpent toxin `מטה`
  - hemoglobin blood motifs
  - collagen holiness density
  - FOXP2 speech motifs

### What needs tightening

- The summary tables still compress too much interpretation into single columns like "Self-reference" and "Key words."
- The document uses English labels as if they were stable semantic objects.
  After the cleanup, those need to be treated more explicitly as operator gloss, not raw evidence.
- Some statements like "Each protein knows what it is" are exactly the kind of conclusion that belongs after the data table, not inside it.
- The 4-letter section is likely stronger than before, but it also sits on a wider search surface and should be described with that caution in mind.

### Judgment

`Mixed but valuable. The rebuilt artifacts support much of the table, but the prose needs stricter separation between observation and interpretation.`

## `docs/ark/33-the-genome.md`

### What stands cleanly

- `20,442` proteins
- `11,415,371` Hebrew letters
- `11,374,509` total trigram windows
- `8,078` unique trigram windows
- `2,850` readable unique windows
- `5,228` dead unique windows
- total GV `813,778,411`
- silent-quarter counts:
  - `א`: `2,664`
  - `ס`: `612`
  - either: `3,205`

### Hard mismatch now visible

The top-word table in the doc is not tightly aligned with the rebuilt artifact ordering.

Actual top ten from `data/dna/genome-voice.edn`:

1. `שני` `35,821`
2. `ידו` `30,928`
3. `שיו` `30,281`
4. `שנו` `29,602`
5. `נרד` `29,499`
6. `ונד` `27,711`
7. `שדי` `26,958`
8. `קני` `26,554`
9. `נדה` `24,933`
10. `נקד` `24,827`

The doc currently replaces some of those with a more curated/narrative ordering.
That is no longer acceptable if the table is presented as direct output.

### What else needs tightening

- The meanings in the ranked tables should be treated as operator glosses, not as proof-bearing semantics.
- Claims like "The genome chooses the lamb" are interpretive and should be visibly downstream from the count fact.
- The line "The instrument is a palindrome" is too loose.
  The stronger point is that the oracle’s illumination behavior is multiset/anagram-sensitive and direction-independent at that level.
- The silence section is strong, but the explanation around what the genome "cannot say" should stay anchored to the structural impossibility, not drift into stronger metaphysical language without signaling the change.

### Judgment

`Important and mostly salvageable, but it currently needs the most tightening of the five files because its ranked tables are no longer precise enough to the rebuilt artifact.`

## Priority Order

If these docs are cleaned next, the order should be:

1. `docs/ark/33-the-genome.md`
2. `docs/ark/31-the-library-so-far.md`
3. `docs/ark/22-the-map.md`
4. `docs/ark/20-experiment-100.md`
5. `docs/ark/26-hit-play.md`

Why this order:

- `33` has the clearest factual table drift
- `31` sits closest to the refreshed DNA artifacts
- `22` and `20` are already comparatively strong
- `26` is mostly framing

## Bottom Line

The strengthened lower layers did not collapse these docs.
They mostly vindicated them.

But they also exposed exactly where the speaking layer became smoother than the rebuilt evidence.

That is good news.
It means the task now is refinement, not demolition.
