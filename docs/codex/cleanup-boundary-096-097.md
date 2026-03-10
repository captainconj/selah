# Cleanup Boundary: 096 / 097

This is the codex-side record of the cleanup and rebuild boundary around the basin artifacts.

## What Is Clean Now

There is now a real separation between core data and operator gloss:

- `4d3657f` added `selah.for-the-human` as an operator-facing gloss and explanation layer.
- `aca3e3c` cleaned the core pipes and builders so machine-English is no longer baked into `oracle`, `basin`, or the `096` / `097` artifact builders.
- `5a46605` regenerated the tracked `096` / `097` artifacts from the cleaned builders.

That means the main path is now:

- core code returns Hebrew/data
- operator gloss is added later, on purpose
- regenerated tracked basin artifacts are no longer carrying `:meaning` or `:next-meaning`

## What Was Regenerated

The dedicated rebuild run completed successfully for:

- `experiments.096-basin-classification/run-experiment!`
- `experiments.097-per-head-basins/run-experiment!`

Tracked files actually rewritten by the rebuild:

- `data/experiments/096/attractors.edn`
- `data/experiments/096/cycles.edn`
- `data/experiments/096/word-index.edn`
- `data/experiments/097/splits.edn`
- `data/experiments/097/spotlight.edn`
- `data/experiments/097/summary.edn`

These were committed in:

- `5a46605` `regenerate 096/097 artifacts from cleaned builders (aca3e3c)`

## What Was Verified

The rebuild produced sane results:

- `096` still classifies `12,826` total words.
- `097` still reports `12,826` cross-reference matches against `096`.
- `097` still reports `0` mismatches against `096`.
- The regenerated tracked files do not contain `:meaning` or `:next-meaning`.

So the computational shape survived the cleanup.

That matters.

The earlier concern was not that the whole basin layer would collapse. The concern was that garbage English had been mixed into saved evidence paths. The rebuild removed that contamination from the tracked outputs that were actually regenerated.

## What Was Dirty And Was Removed

At the time of the rebuild, there were still older untracked `096` sidecar files containing stale English fields and machine-generated gloss sludge.

Those files were not part of the tracked regeneration commit.

They have since been removed from the working tree.

So the current state is simpler:

- tracked rebuilt artifacts: clean
- stale `096` sidecar debris: removed

## What This Means For Docs

Docs that cite the rebuilt tracked `096` / `097` outputs are now on firmer ground than they were before this cleanup.

Docs that cite:

- old saved English meanings
- pre-cleanup `096` / `097` output renderings

still need audit.

The right rule now is simple:

- cite rebuilt Hebrew/data artifacts
- add English only through `selah.for-the-human` or other explicit operator-facing layers
- do not treat old machine-English fields as evidence

## What Still Needs Doing

The basin cleanup is real, but it is not the end of the audit.

Remaining work:

- audit docs that quote `096` / `097` meanings as semantic evidence
- continue the same cleanup pattern through `sweep`, `dna`, and later passage experiment layers
- keep vocabulary terminology explicit: `12,826` full lexicon vs `~7,300` oracle-closed vocabulary

## Codex Read

This phase went well.

The core findings survived honesty.

The plumbing needed correction.
The artifact path needed purification.
The rebuild confirmed that the gold was still there after the English sludge was removed.

That is the right kind of result.
