# Cleanup Boundary: 143 Overlap And Anagram Controls

Type: `reference / assay`
State: `clean`

This note marks the current honesty boundary for the `143e` / `143n` branch.

It does **not** reject the findings.
It says what the current runners actually establish, and what they do not yet establish.

## Current State

The active runners are:

- [`dev/experiments/fiber/143e_enrichment.clj`](/dev/experiments/fiber/143e_enrichment.clj)
- [`dev/experiments/fiber/143n_anagram_attraction.clj`](/dev/experiments/fiber/143n_anagram_attraction.clj)
- [`dev/experiments/fiber/143t_letter_overlap.clj`](/dev/experiments/fiber/143t_letter_overlap.clj)

The key docs are:

- [`docs/experiments/143-enrichment.md`](/docs/experiments/143-enrichment.md)
- [`docs/experiments/143-anagram-attraction.md`](/docs/experiments/143-anagram-attraction.md)
- [`docs/experiments/143-letter-overlap.md`](/docs/experiments/143-letter-overlap.md)
- [`docs/experiments/143-what-survives.md`](/docs/experiments/143-what-survives.md)

## What Is Established

- `143e` measures host over-representation against a naive baseline of raw letter coverage.
- `143n` measures search-word to host-word attraction against the same kind of raw coverage baseline.
- `143t` identifies the mechanism behind many of the strongest hits: shared letters provide the bridge.
- The repo already states the correct open question: does observed enrichment exceed what shared-letter overlap alone predicts?

## What Is Not Yet Established

- `143e` does **not** yet control for shared-letter overlap.
- `143n` does **not** yet compare anagram pairs to non-anagram controls with the same overlap profile.
- The headline ratios in those docs are therefore not first-canon standing claims.

More concretely:

- if a search word and host word share letters, naive coverage expectation is too weak a null
- if a pair is an anagram, overlap is maximal by definition, so naive enrichment is partly tautological
- the current runners cannot distinguish "true geometric preference" from "expected bridge access through shared letters"

## Minimum Control Needed

### For enrichment (`143e`)

For each search-word to host-word claim:

- compute expected hosting from matched shared-letter overlap alone
- compare observed hosting against hosts with similar overlap profile
- ask whether the target host still exceeds matched-overlap expectation

Until then, phrases like "pillar is a magnet" or "guard attracts fibers" are at best suggestive.

### For anagram attraction (`143n`)

For each anagram pair:

- build a null set of non-anagram host words with the same length
- match as closely as possible on shared-letter count / position profile
- compare observed attraction against that matched set

Until then, phrases like "anagram pairs attract in the geometry" outrun the current control surface.

## What Can Still Be Kept

- the overlap mechanism itself is real and worth preserving
- individual narrative fibers may still be interesting as observations
- the branch remains worth testing because the mechanism has been named honestly

## Promotion Rule

This branch stays `suggestive` until it can support at least one proof page with:

- a matched overlap control for enrichment, or
- a matched non-anagram null model for anagram attraction

Without that, the current ratios are useful prompts for assay, not standing evidence.
