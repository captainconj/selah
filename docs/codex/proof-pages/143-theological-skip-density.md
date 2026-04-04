# Proof Page: 143 Theological Skip Density

Type: `proof page`
State: `clean`

## Claim

Under the corrected `143` density boundary, meaningful non-surface skips `7` and `26` remain genuinely rich, while skip `72` remains genuinely sparse.

## Status

- Classification: `standing`
- Experiment band: `143`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: current canonical `4D` view in the repo
- Relevant docs:
  - [`docs/experiments/143-density-truth.md`](/docs/experiments/143-density-truth.md)
  - [`docs/experiments/143-theological-skips.md`](/docs/experiments/143-theological-skips.md)
- Relevant code:
  - [`dev/experiments/fiber/143ad_density_truth.clj`](/dev/experiments/fiber/143ad_density_truth.clj)
  - [`dev/experiments/fiber/143ae_theological_skips.clj`](/dev/experiments/fiber/143ae_theological_skips.clj)

## Run Path

```bash
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ad-density-truth :as exp]) (s/build!) (exp/run-all)"
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ae-theological-skips :as exp]) (s/build!) (exp/run-all)"
```

## Raw Result

The corrected density doc reports:

- skip `7`: `0.257`
- skip `26`: `0.243`
- skip `72`: `0.140`

It also explicitly states:

- surface is densest
- `7` is second densest among theologically meaningful skips
- `26` is third
- `72` is sparsest among the tested meaningful skips

## Why This Counts

This page matters because it preserves a surviving signal after a real retraction.

The inflated `805 > surface` claim died.
What remained was narrower and stronger:

- `7` is still rich
- `26` is still rich
- `72` is still sparse

That is exactly the kind of surviving core worth keeping.

## Falsification

This claim weakens or dies if:

- rerunning the corrected density code no longer yields the reported ordering
- the fair-comparison method is shown to be inconsistent
- the reported skip meanings are being smuggled in from a different metric than the one cited

## Does Not Prove

This does **not** prove:

- that those skips are therefore uniquely intended theological channels
- that every specific word found on those skips is equally weighty
- that the older `805` density narrative survives

## Notes

- This page is intentionally downstream of the correction, not of the older density branch.
- It is a “survived retraction” proof page, which makes it stronger, not weaker.
