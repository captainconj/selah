# Proof Page: 143 Fall Diagonal

Type: `proof page`
State: `clean`

## Claim

From position `0`, the `143` direction `[0,+1,-1,0]` (`skip=804`) yields a 7-step host-word sequence whose later steps run through Genesis `2:16`, `3:6`, `3:21`, and `4:14` in the order command -> ate -> garments -> wandering.

## Status

- Classification: `standing`
- Experiment band: `143`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: current canonical `4D` view in the repo
- Relevant doc: [`docs/experiments/143-the-fall-diagonal.md`](/docs/experiments/143-the-fall-diagonal.md)
- Relevant code: [`dev/experiments/fiber/143al_the_fall_diagonal.clj`](/dev/experiments/fiber/143al_the_fall_diagonal.clj)
- Relevant boundary: [`docs/experiments/143-proximity-check.md`](/docs/experiments/143-proximity-check.md)

## Run Path

```bash
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143al-the-fall-diagonal :as exp]) (s/build!) (exp/run-all)"
```

## Raw Result

The doc and runner align on this 7-step sequence:

- `0`: `בראשית` — Genesis `1:1`
- `3`: `האדם` — Genesis `2:16`
- `4`: `ותאכל` — Genesis `3:6`
- `5`: `כתנות` — Genesis `3:21`
- `6`: `ונד` — Genesis `4:14`

The critical surviving sequence is:

- command
- ate
- garments
- wandering

## Why This Counts

This claim survives because it is a genuine reach claim, not a local-proximity trick.

The corrected `143` boundary explicitly distinguishes:

- short-range love-axis reflections
- long-range diagonals that span multiple chapters

This diagonal is in the second class.

## Falsification

This claim weakens or dies if:

- rerun host-word extraction no longer yields the reported sequence
- the skip/direction mapping is wrong
- a stronger null/control shows this exact class of narrative sequence is common under comparable random starts

## Does Not Prove

This does **not** prove:

- that every diagonal in `143` is equally meaningful
- that the directional interpretation “freedom without love” is itself a settled proof
- that one narrative sequence by itself establishes the full watermark synthesis

## Notes

- The positional sequence is stronger than the sermon built on it.
- The sermon may still be worth keeping, but it should remain downstream of the mechanical sequence.
