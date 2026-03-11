# Findings Codex Surfaced First

Type: `reference`
State: `clean`

*Concrete issues or distinctions that I called out before they were cleaned up or widely integrated into the repo state.*

## Experiment Plumbing

- The repeated `{:vocab :torah}` misuse in experiment scripts.
  - This was causing stale or inconsistent vocabulary handling across many experiments.
  - Claude later measured the exact blast radius and fixed the affected scripts and fallbacks.

- The repeated `(:path walk)` lookup against basin results.
  - `selah.basin/walk` returns `:steps`, not `:path`.
  - This explained the widespread `basin path: []` garbage in saved experiment outputs.

## Oracle Contract Drift

- The `map? vocab` branch in `resolve-index`.
  - It was conceptually wrong and effectively garbage.
  - Claude later replaced the silent wrongness with warning-and-`:torah` fallback.

- The fallback mismatch where `forward` and `forward-by-head` did not agree on how to treat unrecognized vocab.
  - This was part of the real interface drift, even though the actual blast radius to findings turned out to be narrower than worst-case.

## Vocabulary Confusion

- The repo’s blur between:
  - full Torah lexicon `12,826`
  - oracle-closed vocabulary `~7,300`
- This was not a true conceptual contradiction once Scott clarified the intended model.
- It was still a real terminology failure across code, docs, and experiment writeups.

## English Contamination

- The explicit call that `torah-english.edn` was garbage for evidence and experiments.
- The trace showing that English fallback had leaked into:
  - oracle-facing surfaces
  - basin summaries
  - sweep surfaces
  - DNA reporting
- The recommendation to move English help out of core data into a dedicated operator layer.
  - This helped shape `selah.for-the-human` and later the layered lexicon work.

## Artifact Hygiene

- The distinction between:
  - live clean code path
  - stale contaminated saved artifacts
- This mattered first in `096/097`, then again in the DNA layer.
- The repo repeatedly had “clean water, dirty jars” problems.

## Ark / DNA Reproduction Notes

- `00_the_column.clj` was broken by a script-level anagram helper bug.
  - The Ark structure still printed before the crash.
  - Claude later fixed the script rot.

- `genome_voice.clj` had a stale `o/torah-words` call.
  - The silent-quarter claim still held when recomputed directly from `dict/torah-words`.
  - Claude later fixed the helper.

- `100_the_code.clj` and `101_the_map.clj` are notebook-style files, not turnkey experiments.
  - Their claims may still hold.
  - But their reproducibility boundary is weaker than a normal script until better artifact outputs exist.

- The DNA artifact layer initially looked dirtier than the live DNA path.
  - That turned out to be mostly stale saved artifacts, not a still-live contamination bug.
  - One real live bug did remain: `word-frequencies` dropping position context.
  - I fixed that in [dna.clj](/home/scott/Projects/selah/src/selah/dna.clj), then regenerated the tracked library.

## Synthesis Discipline

- The distinction between:
  - structural gold
  - narrative overreach
- I kept pushing this line:
  - the machinery may be real
  - the readings may be profound
  - but some docs still speak as if the whole tower is equally load-bearing

That distinction still matters.

## What This Note Is Not

- This is not a claim that Claude “missed everything.”
- In several cases Claude quickly validated, fixed, or deepened the finding once I surfaced it.
- The point is simply to preserve where the audit pressure first landed and what kinds of failures it exposed.
