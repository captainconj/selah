# Proof Page: 101 Row/Base Map

Type: `proof page`
State: `clean`

## Claim

The rebuilt `101` experiment still lands on row/base mapping `1=A, 2=C, 3=G, 4=U`, with Watson-Crick pairing breaking the top raw-correlation tie.

## Status

- Classification: `standing`
- Experiment band: `100 / 101`
- Proof owner: `codex`

## Source Surface

- Source text: breastplate grid / codon model
- Text model / witness: current repo breastplate data
- Relevant doc: [`docs/ark/22-the-map.md`](/docs/ark/22-the-map.md)
- Relevant code: [`dev/experiments/101_the_map.clj`](/dev/experiments/101_the_map.clj)
- Relevant boundary: [`docs/codex/code-map-reproducibility.md`](/docs/codex/code-map-reproducibility.md)
- Relevant artifact: `data/experiments/101/output.txt`

## Run Path

```bash
clojure -M:dev -e "(require '[experiments.101-the-map :as exp]) (exp/run-experiment!)"
```

## Raw Result

The rebuilt namespace reproduces:

- top row/base mapping: `{1 A, 2 C, 3 G, 4 U}`
- top raw correlation: `0.3967800427596649`
- second-best tied mapping still exists
- Watson-Crick pairing breaks the tie in favor of `A/C/G/U`
- permutation test: `0/10000` exceedances, `p = 9.999000099990002E-5`

## Why This Counts

This claim is narrow and mechanical:

- the experiment is now rerunnable from the namespace itself
- the tie and tie-break rule are both explicit
- the permutation line is produced by the experiment, not by detached script theater

## Falsification

This claim weakens or dies if:

- rerunning `101` no longer produces the reported top mapping
- the tie-break rule is shown to be incorrectly implemented
- the permutation output is not actually tied to the namespace computation

## Does Not Prove

This does **not** prove:

- that every later DNA or theology synthesis built on the map is true
- that the row/base map is unique in every possible scoring system
- that the unmapped `א` interpretation is established as theology rather than post-map interpretation

## Notes

- This proof page is about the map result only.
- Downstream symbolic meaning belongs on separate proof pages, if it survives assay.
