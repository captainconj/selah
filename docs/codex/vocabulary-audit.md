# Vocabulary Audit

Type: `audit / cleanup boundary`
State: `clean`

Working ledger for places that blur:

- `12,826-form full Torah lexicon`
- `~7,300 oracle-closed vocabulary`
- `2,050 voice vocabulary`
- curated / restricted dictionary mode

This is not the fix. It is the map of what needs fixing.

## Terms To Use

- `full Torah lexicon` = `12,826` unique word forms in the five-book WLC/Leningrad stream
- `oracle-closed vocabulary` = `~7,300` words the oracle can actually operate over as its closed working set
- `voice vocabulary` = `~2,050` words at the limiting-distribution knee
- `dict vocabulary` = curated restricted set (`239`, or `~210-215` in older experiment framing depending on filtering)

## Classification Key

- `correct` = wording is aligned with the clarified model
- `blurred` = mixes terms or leaves them underspecified
- `wrong` = states the wrong size or wrong identity outright

## Code And API Surfaces

| File | Current wording / issue | Status | What it should say |
|---|---|---|---|
| [src/selah/dict.clj](/home/scott/Projects/selah/src/selah/dict.clj) | Repeatedly describes Torah vocab as `~7,300 unique word forms` and `full ~7,300` | `wrong` | Distinguish full lexicon `12,826` from oracle-closed `~7,300`; do not call `~7,300` the full Torah lexicon |
| [src/selah/mcp/tools.clj](/home/scott/Projects/selah/src/selah/mcp/tools.clj) | `torah` described as `full Torah lexicon (~7,300 unique word forms)` | `wrong` | Either rename this mode to reflect oracle-closed usage or describe it as oracle-closed `~7,300`, not full lexicon |
| [docs/reference/query-process.md](/home/scott/Projects/selah/docs/reference/query-process.md) | `:torah` listed as `~7,300 words` and called `Full lexicon` | `wrong` | `:torah` should not be described as the full lexicon if it is `~7,300`; define all four levels cleanly |
| [src/selah/dna.clj](/home/scott/Projects/selah/src/selah/dna.clj) | Default `:vocab :torah` appears without vocabulary-level explanation nearby | `blurred` | Add explicit terminology if surfaced to users; `:torah` must not silently imply full `12,826` without saying what it actually means |

## Strong / Mostly Correct Docs

| File | Current wording / issue | Status | Note |
|---|---|---|---|
| [docs/experiments/091b-the-full-quorum.md](/home/scott/Projects/selah/docs/experiments/091b-the-full-quorum.md) | Uses `12,826` clearly for full input vocabulary and `8,570` readable subset | `correct` | Good model for full-lexicon wording |
| [docs/experiments/091b-review.md](/home/scott/Projects/selah/docs/experiments/091b-review.md) | Uses `12,826` clearly and distinguishes dictionary-scale from full-form scale | `correct` | Good review doc; terminology mostly clean |
| [docs/experiments/096-basin-landscape.md](/home/scott/Projects/selah/docs/experiments/096-basin-landscape.md) | Uses `12,826` for full walk set | `correct` | Still needs English contamination audit, but vocabulary count is right |
| [docs/the-journey.md](/home/scott/Projects/selah/docs/the-journey.md#L314) | States `The full Torah — 12,826 unique word forms` | `correct` | This section is a good reference point |

## Blurred Or Wrong Synthesis Docs

| File | Current wording / issue | Status | What it should say |
|---|---|---|---|
| [docs/the-journey.md](/home/scott/Projects/selah/docs/the-journey.md#L280) | Says findings were tested at `three vocabulary levels` and later says `voice`, `dict`, `torah` are `settled`, but does not state clearly that `torah` is not the same thing as full `12,826` | `blurred` | Keep the three service levels, but explicitly separate them from the full lexicon used in `091b`, `094`, `096` |
| [docs/experiments/093-because-please.md](/home/scott/Projects/selah/docs/experiments/093-because-please.md#L168) | Calls `~7,300 unique word forms` the `full Torah lexicon` | `wrong` | Replace with either `oracle-closed vocabulary (~7,300)` or `full Torah lexicon (12,826)` depending on what was actually tested |
| [docs/experiments/093-because-please.md](/home/scott/Projects/selah/docs/experiments/093-because-please.md#L149) | Says `Torah's vocabulary` generically without level disambiguation | `blurred` | Name the exact level: curated, voice, oracle-closed, or full lexicon |
| [docs/experiments/093i-rambans-answer.md](/home/scott/Projects/selah/docs/experiments/093i-rambans-answer.md) | Says `Torah's vocabulary` without clarifying level | `blurred` | Needs explicit level wording |
| [docs/experiments/085-asking-questions.md](/home/scott/Projects/selah/docs/experiments/085-asking-questions.md) | Contrasts `dict vocabulary` with `full Torah lexicon` but may be referring to service-level `:torah` behavior | `blurred` | Verify actual run level and rename accordingly |

## Experiment Docs With Local Vocabulary Language

| File | Current wording / issue | Status | What it should say |
|---|---|---|---|
| [docs/experiments/057-the-seven-days.md](/home/scott/Projects/selah/docs/experiments/057-the-seven-days.md) | Says `Full Torah vocabulary` in summary table | `blurred` | Verify whether this means service-level `:torah` or actual full `12,826` |
| [docs/experiments/091-the-quorum.md](/home/scott/Projects/selah/docs/experiments/091-the-quorum.md) | Uses vocabulary/theological separation language without always naming the scale | `blurred` | Need explicit scale labels: curated dictionary vs larger runs |
| [docs/experiments/108-ezekiels-chariot.md](/home/scott/Projects/selah/docs/experiments/108-ezekiels-chariot.md) | Says something is present in the oracle's vocabulary without clear distinction between readable vocabulary and full lexicon | `blurred` | Clarify whether this means readable/oracle-closed/voice/full |

## README / Public-Facing Risk

| File | Current wording / issue | Status | What it should say |
|---|---|---|---|
| [README.md](/home/scott/Projects/selah/README.md) | Public architecture section mentions `selah.dict — Torah lexicon, Hebrew-English translation` but does not define the lexicon layers | `blurred` | Add one short terminology note somewhere public; this is a likely future confusion source |

## Notes

- The repo currently uses `:torah` as a service/API mode name in places where docs also talk about the `full Torah lexicon`. That is the central ambiguity.
- The cleanest repair may require either:
  - renaming the service mode, or
  - keeping the mode name but documenting that `:torah` means the oracle's Torah-derived working vocabulary, not the full `12,826`-form lexicon.
- `091b`, `094`, and `096` should be treated as terminology anchors because they already speak clearly in terms of `12,826`.
