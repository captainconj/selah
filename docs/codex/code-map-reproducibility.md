# Code / Map Reproducibility

Date: 2026-03-10

This note records the cleanup boundary around experiments `100` and `101`.

## What Was Wrong

The important code/map claims already had real logic behind them, but the experiment surface was weak:

- `dev/experiments/100_the_code.clj` was not a proper runnable experiment namespace.
- `dev/experiments/101_the_map.clj` depended on REPL-only `comment` forms.
- `dev/scripts/101_map/*.clj` were output fragments, not a complete experiment path.
- The key line `Permutation test: p < 0.0001 (0/10000)` was being printed from a script fragment, not computed by the namespace itself.

So the claims were partly real, but the reproducibility boundary was too soft.

## What Changed

- `100_the_code.clj` now has `run-experiment!`.
- `101_the_map.clj` now has `run-experiment!`.
- `101` now computes:
  - all 24 row→base scores
  - Watson-Crick tie-breaking
  - greedy letter→amino-acid assignment
  - a real 10,000-trial permutation test with fixed seed
  - translated breastplate output
- fresh artifact outputs now exist:
  - `data/experiments/100/output.txt`
  - `data/experiments/101/output.txt`
  - updated EDN artifacts under `data/experiments/100/`
  - updated EDN artifacts under `data/experiments/101/`

## What Reproduced

### Experiment 100

The structural spine still holds:

- coding positions: `64`
- singleton positions: `8`
- backbone letters: `ו י נ`
- backbone positions: `26`
- `את` still skips column `2`
- `כבש` and `שכב` still hit the same stones

### Experiment 101

The map still lands where the repo says it lands:

- top row/base mapping: `{1 A, 2 C, 3 G, 4 U}`
- top raw correlation: `0.3967800427596649`
- second-best tied mapping still exists
- Watson-Crick pairing still breaks the tie in favor of `A/C/G/U`
- assignment score: `13.888299198575803`
- frequency correlation: `0.6854196827438999`
- permutation test: `0/10000` exceedances, `p = 9.999000099990002E-5`
- unmapped letter: `א`
- start codon summary still lands on stones `[1 11 9]`
- stop letter still lands on `ס` on Joseph's stone

The translated 72-letter breastplate now comes straight from the experiment namespace, not from a detached helper script.

## What This Means

The `100/101` layer is now in a materially better state:

- the core claims are no longer just notebook claims
- the map can now be rerun from the namespace itself
- the permutation line is no longer theater

This does not prove every later synthesis built on top of the map. But it does mean the map/code boundary itself is substantially stronger than it was before.
