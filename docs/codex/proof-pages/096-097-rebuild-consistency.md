# Proof Page: 096/097 Rebuild Consistency

Type: `proof page`
State: `clean`

## Claim

After the cleaned rebuild boundary, the tracked `096` and `097` basin artifacts still classify the full `12,826`-word Torah lexicon consistently, and `097` cross-references against `096` with `0` mismatches.

## Status

- Classification: `standing`
- Experiment band: `096 / 097`
- Proof owner: `codex`

## Source Surface

- Source text: full Torah lexicon
- Text model / witness: `12,826`-form Torah lexicon used by the rebuilt basin path
- Relevant docs:
  - [`docs/experiments/096-basin-landscape.md`](/docs/experiments/096-basin-landscape.md)
  - [`docs/experiments/097-per-head-basins.md`](/docs/experiments/097-per-head-basins.md)
- Relevant code:
  - [`dev/experiments/096_basin_classification.clj`](/dev/experiments/096_basin_classification.clj)
  - [`dev/experiments/097_per_head_basins.clj`](/dev/experiments/097_per_head_basins.clj)
- Relevant boundary: [`docs/codex/cleanup-boundary-096-097.md`](/docs/codex/cleanup-boundary-096-097.md)
- Relevant artifacts:
  - `data/experiments/096/summary.edn`
  - `data/experiments/097/summary.edn`
  - `data/experiments/097/cross-reference-096.edn`

## Run Path

```bash
clojure -M:dev -e "(require '[experiments.096-basin-classification :as exp96]) (exp96/run-experiment!)"
clojure -M:dev -e "(require '[experiments.097-per-head-basins :as exp97]) (exp97/run-experiment!)"
```

## Raw Result

The cleaned boundary records:

- `096` still classifies `12,826` total words
- `097` still reports `12,826` cross-reference matches against `096`
- `097` still reports `0` mismatches against `096`
- regenerated tracked artifacts no longer carry machine-English `:meaning` / `:next-meaning` fields

## Why This Counts

This is a trust-chain proof page, not a theology proof page.

It matters because:

- the basin layer survived the English-cleanup boundary
- the computational shape did not collapse when the sludge was removed
- the rebuilt tracked artifacts are internally consistent across the two experiment layers

## Falsification

This claim weakens or dies if:

- reruns no longer classify `12,826` words cleanly
- `097` no longer agrees with `096`
- the rebuilt artifacts are shown to depend on stale helper or contaminated output paths

## Does Not Prove

This does **not** prove:

- that every interpretation built on `096` / `097` is equally strong
- that basin dynamics are thereby theologically established
- that all older saved `096` / `097` prose or outputs are automatically clean

## Notes

- This page is about the rebuilt evidence path surviving cleanup.
- It is one of the stronger “the gold survived honesty” pages in the repo.
