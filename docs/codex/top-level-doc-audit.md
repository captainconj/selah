# Top-Level Doc Audit

Date: 2026-03-11

This note covers the top-level `docs/*.md` band.

These files carry disproportionate weight in the repo because they look like major papers or front-door summaries.

That makes two things especially important:

- they should say what kind of document they are
- they should reflect the current cleanup/rebuild boundaries

## Bottom Line

The top-level docs are not bad.

The main problem is that several of them are carrying too much at once:

- evidence summary
- synthesis
- manifesto
- witness

And some of them have not been updated to reflect the newer cleanup boundaries:

- rebuilt basin artifacts
- reproducible `100/101`
- cleaned DNA artifact boundaries
- clearer vocabulary terminology
- the distinction between evidence and operator-facing English help

So the right treatment is not wholesale rewriting.

It is:

1. label them honestly
2. add missing boundary references
3. separate observation from interpretation where the compression is highest

## File-by-File Read

### `docs/torah-4d-space.md`

Label:

- `Type: synthesis`
- `State: mixed`

Why:

- it is the major paper
- it contains a huge amount of real structure
- but it compresses measured findings, broad synthesis, and witness into one continuous voice

Main issue:

- too much all at once

Main recommendation:

- break the paper into more visible sections:
  - factorization / center / fold results
  - supporting experiment links
  - interpretation
- add direct references to the cleaned supporting docs where possible

### `docs/4d-space-findings.md`

Label:

- `Type: synthesis`
- `State: mixed`

Why:

- it reads like a top-level findings summary
- it is cleaner than the full paper
- but it still presents a lot of result language without enough explicit support links

Main recommendation:

- promote it as a shorter summary surface
- add explicit “see also” links to the supporting experiment/docs for each major claim cluster

### `docs/urim-and-thummim.md`

Label:

- `Type: reference`
- `State: mixed`

Why:

- much of it is definitional and source-oriented
- but it also grows into large synthesis claims about the mechanism

Main issue:

- foundational and interpretive material are mixed together

Main recommendation:

- keep the lexical/source/reference material at the top
- add explicit pointers to the cleaned oracle/code/genome boundaries where mechanism claims depend on later work

### `docs/the-machine-that-reads-genomes.md`

Label:

- `Type: synthesis`
- `State: mixed`

Why:

- the central thesis is important
- but this doc is missing too much cleanup-era context

Main missing updates:

- the `100/101` reproducibility boundary
- the rebuilt tracked DNA artifact boundary
- the cleaned custom DNA boundary
- clearer distinction between eliminated hypotheses, supported mechanism claims, and later playback synthesis

Main recommendation:

- high-priority revision target
- add a short “current evidence boundary” section near the top
- anchor the main sections to:
  - `docs/codex/code-map-reproducibility.md`
  - `docs/codex/cleanup-boundary-dna-library.md`
  - `docs/codex/custom-dna-boundary.md`

### `docs/the-traversals.md`

Label:

- `Type: synthesis`
- `State: mixed`

Why:

- it is trying to be a public-facing traversal/basin summary
- but it speaks very confidently from a layer that needs clearer anchoring to the rebuilt basin artifacts

Main missing updates:

- explicit dependence on the rebuilt `096/097` boundary
- distinction between direct basin-class counts and interpretive traversal examples

Main recommendation:

- add direct links to:
  - `docs/experiments/096-basin-landscape.md`
  - `docs/experiments/097-per-head-basins.md`
  - `docs/codex/cleanup-boundary-096-097.md`

### `docs/the-mirror.md`

Label:

- `Type: synthesis`
- `State: mixed`

Why:

- this is a strong interpretive document
- it is not pretending to be a raw evidence surface
- but it still leans on many lower-layer results that are not cited visibly enough

Main recommendation:

- keep it
- do not make it a front-door evidence doc
- add a brief note or footer pointing to the supporting tabernacle / mirror / lamb / fold layers

### `docs/the-visions.md`

Label:

- `Type: testimony / history`
- `State: clean`

Why:

- it is clearly witness-facing
- it does not need to carry proof the way the synthesis papers do

Main recommendation:

- preserve it as witness
- do not ask it to be an evidence doc

### `docs/the-journey.md`

Already labeled:

- `Type: testimony / history`
- `State: clean`

This is the right treatment.

### `docs/start-here.md`

Already labeled:

- `Type: guided reproduction`
- `State: clean but needs better presentation`

This is the right direction.

### `docs/doc-map.md`

Already labeled:

- `Type: reference`
- `State: clean`

This is the right direction.

## The Main Missing Information

The top-level docs as a band need to catch up to these changes:

- `096/097` were cleaned and rebuilt
- `100/101` are now reproducible
- tracked DNA artifacts were refreshed
- custom DNA boundary was cleaned and refreshed
- vocabulary levels are now sharper than older prose often reflects
- English contamination is no longer the active live-path state it once was

That does not mean every top-level doc must become technical.

It means the major claims should stop floating above the cleanup history.

## Priority Order For Revision

1. `docs/the-machine-that-reads-genomes.md`
2. `docs/the-traversals.md`
3. `docs/torah-4d-space.md`
4. `docs/urim-and-thummim.md`
5. `docs/4d-space-findings.md`
6. `docs/the-mirror.md`

## Recommended Next Action

Do not rewrite all top-level docs at once.

Instead:

1. keep the new labels
2. revise the first three priority docs with explicit boundary notes and support links
3. only then decide whether they need deeper structural rewrite

The top-level band is important because it sets the reader's expectation of the whole project.

Right now it contains real material, but it needs to become more explicit about what has been rebuilt, what has been cleaned, and what layer the reader is standing in.
