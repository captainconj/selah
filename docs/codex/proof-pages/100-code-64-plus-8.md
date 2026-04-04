# Proof Page: 100 Code Structure (64 + 8)

Type: `proof page`
State: `clean`

## Claim

When final letter forms are merged in experiment `100`, the breastplate grid divides into `64` non-singleton coding positions plus `8` singleton positions.

## Status

- Classification: `standing`
- Experiment band: `100`
- Proof owner: `codex`

## Source Surface

- Source text: breastplate grid
- Text model / witness: current repo breastplate data with final-form merging
- Relevant doc: [`docs/ark/20-experiment-100.md`](/docs/ark/20-experiment-100.md)
- Relevant code: [`dev/experiments/100_the_code.clj`](/dev/experiments/100_the_code.clj)
- Relevant boundary: [`docs/codex/code-map-reproducibility.md`](/docs/codex/code-map-reproducibility.md)
- Relevant artifacts:
  - `data/experiments/100/summary.edn`
  - `data/experiments/100/structure.edn`
  - `data/experiments/100/output.txt`

## Run Path

```bash
clojure -M:dev -e "(require '[experiments.e100-the-code :as exp]) (exp/run-experiment!)"
```

## Raw Result

The rebuilt `100` layer still reports:

- `14` non-singleton letters occupying `64` positions
- `8` singleton letters occupying `8` positions

The same layer also still reports:

- universal backbone letters: `נ ו י`
- backbone positions total: `26`

## Why This Counts

This claim is structural and countable.

It does not depend on later symbolic interpretation.
It is one of the cleanest pieces of the code layer because the result is a direct consequence of the grid census after explicit final-form merging.

## Falsification

This claim weakens or dies if:

- rerunning `100` no longer yields the `64 + 8` split
- the final-form merging rule is shown to be incorrectly implemented
- the grid source data changes materially

## Does Not Prove

This does **not** prove:

- that the breastplate is therefore a codon table in the full downstream sense
- that every map or DNA conclusion built on the code layer is established
- that the symbolic reading of the `8` singletons is settled

## Notes

- This is a count fact first.
- It is one of the safest proof pages to show a skeptical reader because it avoids most of the sermon layer.
