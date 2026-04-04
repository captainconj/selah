# Proof Page: 143 Center Altar

Type: `proof page`
State: `clean`

## Claim

The literal midpoint of the `304,850`-letter Torah stream is position `152,425`, and that position lies in `המזבח` at Leviticus `8:24`.

## Status

- Classification: `standing`
- Experiment band: `143`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: `304,850`-letter WLC/Leningrad model used by the repo
- Relevant doc: [`docs/experiments/143-the-center-altar.md`](/docs/experiments/143-the-center-altar.md)
- Relevant code: [`dev/experiments/fiber/143an_center_altar.clj`](/dev/experiments/fiber/143an_center_altar.clj)
- Relevant summary: [`docs/experiments/143-what-survives.md`](/docs/experiments/143-what-survives.md)

## Run Path

```bash
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143an-center-altar :as exp]) (s/build!) (exp/run-all)"
```

## Raw Result

The experiment reports:

- center position: `152425`
- center host word: `המזבח`
- verse: `Leviticus 8:24`

The surrounding positions also show:

- `על`
- `המזבח`
- `סביב`

## Why This Counts

This is a positional claim, not a symbolic one.

Given the repo’s fixed Torah stream length, the midpoint is deterministic.
The question is simply which word occupies that position.
That result is direct and rerunnable.

## Falsification

This claim weakens or dies if:

- the core Torah stream length or witness changes
- rerun indexing places position `152,425` in a different word
- the stream model is shown to be internally inconsistent

## Does Not Prove

This does **not** prove:

- that every altar-centered theological inference built on top of it is established
- that the positional fact alone demonstrates divine authorship
- that all center-related claims in the repo are equally strong

## Notes

- This is one of the cleaner `143` claims because it is mostly arithmetic plus direct lookup.
- The interpretive layer should stay downstream of the positional fact.
