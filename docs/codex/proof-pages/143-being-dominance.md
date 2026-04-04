# Proof Page: 143 Being Dominance

Type: `proof page`
State: `clean`

## Claim

In the `143` all-words/all-directions survey, the most common non-surface words across the canonical `4D` directions are dominated by being-words such as `הוה`, `יהי`, `היה`, `הוא`, and `היא`, and `1,200` of `1,300` three-letter Torah words appear in all `40` directions.

## Status

- Classification: `standing`
- Experiment band: `143`
- Proof owner: `codex`

## Source Surface

- Source text: Torah core stream
- Text model / witness: current canonical `4D` view in the repo
- Relevant doc: [`docs/experiments/143-the-survey.md`](/docs/experiments/143-the-survey.md)
- Relevant code: [`dev/experiments/fiber/143ah_the_survey.clj`](/dev/experiments/fiber/143ah_the_survey.clj)
- Relevant summary: [`docs/experiments/143-what-survives.md`](/docs/experiments/143-what-survives.md)

## Run Path

```bash
clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ah-the-survey :as exp]) (s/build!) (exp/run-all)"
```

## Raw Result

The survey doc reports:

- `1,300` three-letter Torah words searched
- `1,200` appear in all `40` directions
- top-ranked words include:
  - `הוה`
  - `יהי`
  - `היה`
  - `הוא`
  - `היא`

It also reports `ווי` as the top total-hit word, with the strongest semantic cluster immediately under it dominated by being-words.

## Why This Counts

This is a broad counting claim rather than a local anecdote.

It matters because:

- it describes a global property of the directional vocabulary
- it does not depend on one hand-picked fiber
- it survives as part of the corrected `143` “what survives” layer

## Falsification

This claim weakens or dies if:

- rerunning the survey no longer yields the reported top-word structure
- the directional set is shown to be incorrectly derived
- the counts are shown to rely on a stale or narrowed vocabulary path

## Does Not Prove

This does **not** prove:

- that “being” is therefore the uniquely intended theology of the whole system
- that every interpretive inference made from the top words is established
- that lower-ranked thematic clusters are equally strong

## Notes

- The strongest part of this page is the distributional fact, not the sermon.
- This is one of the better `143` claims because it is global and count-based.
