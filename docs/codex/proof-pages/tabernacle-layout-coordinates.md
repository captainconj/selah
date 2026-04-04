# Proof Page: Tabernacle Layout Coordinates

Type: `proof page`
State: `clean`

## Claim

The main tabernacle walk coordinate map reproduces a stable outside-to-inside sequence across the two tellings, including the specific station coordinates recorded in the first sluice.

## Status

- Classification: `standing`
- Experiment band: `Tabernacle walk`
- Proof owner: `codex`

## Source Surface

- Source text: tabernacle walk station verses
- Text model / witness: current repo `4D` Torah space
- Relevant doc: [`docs/codex/tabernacle-first-sluice.md`](/docs/codex/tabernacle-first-sluice.md)
- Relevant code: [`dev/experiments/tabernacle/walk.clj`](/dev/experiments/tabernacle/walk.clj)
- Relevant artifact path: `/tmp/tab-stations.edn` written by the walk script

## Run Path

```bash
clojure -M:dev dev/experiments/tabernacle/walk.clj
```

## Raw Result

The first sluice records these reproduced coordinates:

- Gate instruction: `(2,35,4,63)`
- Gate built: `(3,7,3,53)`
- Altar instruction: `(2,34,7,5)`
- Altar built: `(3,6,4,31)`
- Ark instruction: `(2,30,7,40)`
- Ark built: `(3,4,9,19)`
- Mercy seat instruction: `(2,30,11,63)`
- Mercy seat built: `(3,4,12,41)`
- “I will meet with you”: `(2,31,3,0)`
- Guard the charge (center): `(3,25,5,53)`

## Why This Counts

This page is about the walk existing as a real coordinate traversal.

It matters because:

- the progression is not just rhetorical prose
- the same walk links the instruction and construction tellings
- later station claims have a real coordinate spine under them

## Falsification

This claim weakens or dies if:

- rerunning the walk script no longer yields the recorded coordinates
- the verse-to-coordinate extraction is shown to be incorrect
- the walk sequence turns out to depend on stale text indexing

## Does Not Prove

This does **not** prove:

- that every station interpretation in the tabernacle docs is equally strong
- that the later synthesis docs are established by the layout alone
- that the raw walk script is already an ideal reproducibility surface

## Notes

- The tabernacle sluice explicitly notes that the walk script is still a raw top-level script rather than a polished namespace.
- That is a presentation weakness, not yet a reason to discard the coordinate map itself.
