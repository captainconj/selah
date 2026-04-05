# Proof Page: Tabernacle Ark Mercy Asymmetry

Type: `proof page`
State: `clean`

## Claim

In the tabernacle walk, `ארון` is fixed and all four readers see it, but the mercy-side reader dominates strongly (`114`) over the others (`48`, `28`, `12`); `כפרת` also reproduces as `כפתר` on God and Left while Aaron and Right see nothing.

## Status

- Classification: `standing`
- Experiment band: `Tabernacle walk`
- Proof owner: `codex`

## Source Surface

- Source text: tabernacle Ark / mercy-seat station
- Text model / witness: current repo oracle / basin path over the station words
- Relevant docs:
  - [`docs/codex/tabernacle-first-sluice.md`](/docs/codex/tabernacle-first-sluice.md)
  - [`docs/tabernacle/10-the-ark.md`](/docs/tabernacle/10-the-ark.md)
- Relevant code: [`dev/experiments/tabernacle/10_ark.clj`](/dev/experiments/tabernacle/10_ark.clj)

## Run Path

```bash
clojure -M:dev dev/experiments/tabernacle/10_ark.clj
```

## Raw Result

The first sluice records:

- `ארון` basin: fixed
- Aaron: `48`
- God: `28`
- Right: `12`
- Left: `114`

It also records for `כפרת`:

- not fixed
- basin walks to `כפתר`
- God sees `כפתר`
- Left sees `כפתר`
- Aaron / Right see nothing

## Why This Counts

This is a strong station page because it contains two concrete, rerunnable asymmetries:

- a large per-head weight imbalance on `ארון`
- a stable `כפרת -> כפתר` split surface

That is enough to treat the station as standing signal even if later symbolism remains arguable.

## Falsification

This claim weakens or dies if:

- rerunning the Ark station no longer yields the recorded counts
- `ארון` is no longer fixed
- `כפרת` no longer routes to `כפתר` for God / Left under the same surface

## Does Not Prove

This does **not** prove:

- that “the Ark belongs to mercy” is fully established as theology rather than interpretation of a strong asymmetry
- that every invisibility claim in the Ark doc is equally strong
- that all Holy-of-Holies synthesis in the tabernacle band is standing

## Notes

- This is a good example of a claim that is stronger than its sermon but not empty of sermon.
