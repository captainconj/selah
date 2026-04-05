# Proof Page: 143 Beginning To Center Arithmetic

Type: `proof page`
State: `clean`

## Claim

The midpoint position `152,425` factors exactly as `7 × 25 × 871`, and because `871 = 13 × 67`, the midpoint also factors as `5^2 × 7 × 13 × 67`.

## Status

- Classification: `standing`
- Experiment band: `143bf`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: `304,850`-letter WLC/Leningrad model used by the repo
- Relevant doc: [`docs/experiments/143-beginning-to-center.md`](/docs/experiments/143-beginning-to-center.md)
- Relevant code: [`src/selah/space/coords.clj`](/src/selah/space/coords.clj)
- Relevant supporting code: [`dev/experiments/fiber/143an_center_altar.clj`](/dev/experiments/fiber/143an_center_altar.clj), [`dev/experiments/fiber/143ap_jubilee_sevens.clj`](/dev/experiments/fiber/143ap_jubilee_sevens.clj)
- Relevant artifact: none; this is arithmetic over fixed repo constants plus midpoint lookup

## Run Path

```bash
clojure -M:dev -e "(println {:midpoint (/ 304850 2) :jubilee-stride (* 13 67) :factorization (= 152425 (* 7 25 871)) :prime-factorization (= 152425 (* 25 7 13 67))})"
```

## Raw Result

The arithmetic yields:

- `304,850 / 2 = 152,425`
- `13 × 67 = 871`
- `7 × 25 × 871 = 152,425`
- `25 × 7 × 13 × 67 = 152,425`

Under the repo's default `4D` lens:

- `7` is the `a` dimension
- `50` is the `b` dimension, so `25` is its midpoint
- `871` is the `b`-axis stride because `50` is followed by dimensions `13 × 67`

## Why This Counts

This is a pure arithmetic claim over fixed repo constants.

If the Torah stream length is `304,850` and the default lens is `[7 50 13 67]`, then the midpoint and the stride values are deterministic.
The factorization is not interpretive; it is exact multiplication.

## Falsification

This claim weakens or dies if:

- the repo's core Torah stream length is not actually `304,850`
- the default `4D` lens or stride computation changes
- the midpoint used elsewhere in the repo is shown to be off-by-one

## Does Not Prove

This does **not** prove:

- that the factorization itself encodes theology
- that `25 × 7 = 175` meaningfully invokes Abraham rather than being an arithmetic coincidence
- that the midpoint was designed for this factorization rather than simply admitting it under the chosen lens
- that the seven-step reading in [`docs/experiments/143-beginning-to-center.md`](/docs/experiments/143-beginning-to-center.md) is established as a narrative claim

## Notes

- This page intentionally promotes only the arithmetic surface.
- The Abraham-years observation may still be worth preserving, but it belongs in interpretation, not in the standing claim itself.
