# Proof Page: 143 Jubilee Sevens Centers

Type: `proof page`
State: `clean`

## Claim

In the repo's default `4D` lens, the `b=25` jubilee-center position inside each of the seven `a`-sevenths lands on a fixed seven-word sequence, and the middle one (`a=3`) lands on `המזבח` at Leviticus `8:24`.

## Status

- Classification: `standing`
- Experiment band: `143ap`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: `304,850`-letter WLC/Leningrad model used by the repo
- Relevant doc: [`docs/experiments/143-jubilee-sevens.md`](/docs/experiments/143-jubilee-sevens.md)
- Relevant code: [`dev/experiments/fiber/143ap_jubilee_sevens.clj`](/dev/experiments/fiber/143ap_jubilee_sevens.clj)
- Relevant summary: [`docs/experiments/143-what-survives.md`](/docs/experiments/143-what-survives.md)

## Run Path

```bash
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ap-jubilee-sevens :as exp]) (s/build!) (exp/run-all)"
```

## Raw Result

The experiment reports the seven `b=25` centers at positions `a×43,550 + 25×871`:

- `a=0` -> position `21,775` -> `צדיקם` -> Genesis `18:26`
- `a=1` -> position `65,325` -> `בלתי` -> Genesis `43:5`
- `a=2` -> position `108,875` -> `ומכרו` -> Exodus `21:16`
- `a=3` -> position `152,425` -> `המזבח` -> Leviticus `8:24`
- `a=4` -> position `195,975` -> `גאל` -> Numbers `5:8`
- `a=5` -> position `239,525` -> `אסרה` -> Numbers `30:11`
- `a=6` -> position `283,075` -> `תשחית` -> Deuteronomy `20:19`

## Why This Counts

This is a direct positional lookup claim.

Given the repo's fixed lens and stride values, each seventh-center position is deterministic.
The proof burden is simply whether rerun indexing lands on the same host words and verses.

The middle seventh-center coincides with the already established midpoint position `152,425`.

## Falsification

This claim weakens or dies if:

- the default `4D` lens or stride values change
- rerun indexing places any listed position in a different host word or verse
- the underlying Torah stream length or witness changes
- the `b=25` lookup in the runner is shown to be off-by-one or otherwise inconsistent

## Does Not Prove

This does **not** prove:

- that the seven center words form a single intended narrative
- that Joseph-centered or redemption-centered readings built from this list are established
- that the sequence demonstrates authorship or theology by itself

## Notes

- This page proves the seven center lookups, not the strongest interpretation of them.
- It is one of the cleaner `143` claims because the runner is short and the lookup surface is explicit.
