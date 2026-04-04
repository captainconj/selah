# Proof Page: Ark Foundation Floor

Type: `proof page`
State: `clean`

## Claim

On the Ark foundation layer (`a=0, b=8, c=7`), the door word at `d=14` is `עשה`, the atonement root appears twice (`וכפרת`, `בכפר`), and the last triplet is `זהא` with `GV=13`.

## Status

- Classification: `standing`
- Experiment band: `Ark walk`
- Proof owner: `codex`

## Source Surface

- Source text: Ark walk foundation layer
- Text model / witness: current repo `4D` Torah space
- Relevant doc: [`docs/codex/ark-first-sluice.md`](/docs/codex/ark-first-sluice.md)
- Relevant code: [`dev/experiments/ark/01_foundation.clj`](/dev/experiments/ark/01_foundation.clj)

## Run Path

```bash
clojure -M:dev dev/experiments/ark/01_foundation.clj
```

## Raw Result

The first sluice and script agree on these foundation facts:

- door word at `d=14`: `עשה`
- double atonement root:
  - `וכפרת`
  - `בכפר`
- last triplet: `זהא`
- `GV(זהא) = 13`

## Why This Counts

This page is about the floor printing at all.

It matters because the claim is not “the theology is beautiful.”
It is that a specific layer, with specific positions, yields a stable and rerunnable word table containing the reported root repetitions and door position.

## Falsification

This claim weakens or dies if:

- rerunning the foundation script no longer yields the reported layer table
- the layer indexing is shown to be wrong
- the identified word boundaries on the layer are shown to be unreliable

## Does Not Prove

This does **not** prove:

- that every interpretation attached to the foundation layer is established
- that destruction/anointing symbolism is thereby settled
- that the whole Ark walk is equally strong at every station

## Notes

- This is one of the safer Ark proof pages because the first sluice already treated it as a direct reproduction, not as notebook theater.
