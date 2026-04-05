# Proof Page: Tabernacle Speaking Cluster

Type: `proof page`
State: `clean`

## Claim

In the tabernacle speaking station, the live checks reproduce a stable cluster: `קול` is fixed with Aaron dominant and God at zero, `שמע` is fixed with God dominant, `משכן` is fixed with God and Left dominant, and `פנים` is invisible.

## Status

- Classification: `standing`
- Experiment band: `Tabernacle walk`
- Proof owner: `codex`

## Source Surface

- Source text: tabernacle speaking / meeting station
- Text model / witness: current repo oracle / basin path over station words
- Relevant docs:
  - [`docs/codex/tabernacle-first-sluice.md`](/docs/codex/tabernacle-first-sluice.md)
  - [`docs/tabernacle/11-the-speaking.md`](/docs/tabernacle/11-the-speaking.md)
- Relevant code: [`dev/experiments/tabernacle/11_speaking.clj`](/dev/experiments/tabernacle/11_speaking.clj)

## Run Path

```bash
clojure -M:dev dev/experiments/tabernacle/11_speaking.clj
```

## Raw Result

The first sluice records:

- `קול`: fixed; Aaron dominates at `22`; God sees none
- `שמע`: fixed; God dominates at `12`
- `משכן`: fixed; God `20`, Left `24`, Right `0`
- `פנים`: invisible

The speaking doc states the same cluster in expanded prose.

## Why This Counts

This is a good standing cluster because it is:

- multi-word rather than anecdotal
- directly tied to live station-word checks
- partly positive (`fixed`, weighted) and partly negative (`invisible`)

That combination is harder to dismiss than a single highlighted reading.

## Falsification

This claim weakens or dies if:

- rerunning the speaking script no longer yields the reported pattern
- `קול` or `שמע` are no longer fixed under the same surface
- `פנים` no longer stays invisible at the station

## Does Not Prove

This does **not** prove:

- that every theological statement in the speaking doc is established
- that the face-invisibility theme is thereby universally proven across all layers
- that the station’s later synthesis about day/night/journey is equally strong

## Notes

- This is one of the better tabernacle claims because it includes both presence and absence in the same measured cluster.
