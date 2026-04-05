# Proof Page: Tabernacle Altar God Reading

Type: `proof page`
State: `clean`

## Claim

In the tabernacle walk, `מזבח` is a fixed point and only God reads it as `מזבח`; the other readers do not produce the altar word.

## Status

- Classification: `standing`
- Experiment band: `Tabernacle walk`
- Proof owner: `codex`

## Source Surface

- Source text: tabernacle altar station
- Text model / witness: current repo oracle / basin path over the tabernacle station word
- Relevant docs:
  - [`docs/codex/tabernacle-first-sluice.md`](/docs/codex/tabernacle-first-sluice.md)
  - [`docs/tabernacle/03-the-altar.md`](/docs/tabernacle/03-the-altar.md)
- Relevant code: [`dev/experiments/tabernacle/03_altar.clj`](/dev/experiments/tabernacle/03_altar.clj)

## Run Path

```bash
clojure -M:dev dev/experiments/tabernacle/03_altar.clj
```

## Raw Result

The first sluice records:

- `מזבח` basin: fixed
- only God reads `מזבח`
- Aaron / Right / Left do not produce readable `מזבח`

The station doc states the same split.

## Why This Counts

This is a narrow station-word claim.

It matters because:

- it cashes out under a live per-head check
- it is one of the strongest station asymmetries in the tabernacle layer
- it does not depend on long numerological chaining

## Falsification

This claim weakens or dies if:

- rerunning the altar script no longer shows `מזבח` as fixed
- non-God readers start producing `מזבח` under the same surface
- the per-head readout is shown to depend on stale helper behavior

## Does Not Prove

This does **not** prove:

- that every altar interpretation in the doc is established
- that the dimensions-love arithmetic in the altar doc is thereby standing
- that all altar-adjacent implement claims are equally strong

## Notes

- This is one of the best tabernacle pages because it is almost entirely a direct per-head readout claim.
