# Proof Page: Ark Directory / Garden Junction

Type: `proof page`
State: `clean`

## Claim

On the Ark directory layer, `מביא` and `את` reproduce as unanimous four-reader results, and the vertical stack `למינהו / והקמתי / מביא` is actually present at the reported junction.

## Status

- Classification: `standing`
- Experiment band: `Ark walk`
- Proof owner: `codex`

## Source Surface

- Source text: Ark directory/garden layer
- Text model / witness: current repo `4D` Torah space
- Relevant docs:
  - [`docs/codex/ark-first-sluice.md`](/docs/codex/ark-first-sluice.md)
  - [`docs/ark/17-the-garden.md`](/docs/ark/17-the-garden.md)
- Relevant code:
  - [`dev/experiments/ark/03_directory.clj`](/dev/experiments/ark/03_directory.clj)
  - [`dev/experiments/ark/10_the_garden.clj`](/dev/experiments/ark/10_the_garden.clj)

## Run Path

```bash
clojure -M:dev dev/experiments/ark/03_directory.clj
clojure -M:dev dev/experiments/ark/10_the_garden.clj
```

## Raw Result

The first sluice records:

- `מביא` is unanimous across all four readers
- `את` is unanimous across all four readers
- `GV(מביא) = 53`
- the vertical stack `למינהו / והקמתי / מביא` is present at the reported positions

## Why This Counts

This is one of the strongest Ark reruns because it combines:

- positional geometry
- per-reader agreement
- a specific vertical junction claim

That is a tighter chain than a purely thematic reading.

## Falsification

This claim weakens or dies if:

- rerunning the directory/garden scripts no longer yields four-reader unanimity for `מביא` and `את`
- the vertical stack cannot be reproduced at the claimed positions
- the Garden-layer output is shown to depend on stale helper behavior

## Does Not Prove

This does **not** prove:

- that every later garden interpretation is established
- that `GV=53` therefore carries a settled symbolic meaning
- that the whole Ark/genome bridge is already proven end to end

## Notes

- The first sluice explicitly called this one of the strongest direct reruns in the whole Ark layer.
