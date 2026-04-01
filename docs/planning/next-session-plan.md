# Next Session Plan

*Pick up here. The data is computed. The docs need finishing.*

---

## Immediate: Rewrite Core Docs (Phase 2 completion)

These experiments are re-run and data is captured. The docs were updated but not fully rewritten from scratch. They still have Grid A remnants, old reader labels in the body text, or mixed old/new content.

### Full rewrite needed:

1. **`docs/experiments/091-the-quorum.md`** — The courtroom section (lines 99-186) is clean. Everything before it (lines 1-96) was rewritten. The findings section (lines 188+) was updated with Grid B data but the old Grid A "What the Fourth Head Revealed" prose was replaced inline rather than rewritten holistically. The 091b and 092 sections were appended. Needs: one coherent document that tells the Grid B story from start to finish.

2. **`docs/experiments/092-grid-permutation.md`** — Was fully rewritten. Should be clean. Verify.

3. **`docs/experiments/092b-the-ramban-principle.md`** — Was fully rewritten. Should be clean. Verify.

4. **`docs/experiments/093-synthesis.md`** — Partially updated. Reader labels fixed, ghost zone line fixed, reader profile table added at the end. But the body text (sections 1-6) still references "4 traversal directions" generically and doesn't tell the Grid B courtroom story. Needs full rewrite.

### Already fully rewritten (no action needed):
- `docs/experiments/093h-the-ghost-zone.md` — DONE
- `docs/experiments/094-thummim-sweep.md` — DONE
- `docs/experiments/097-per-head-basins.md` — DONE
- `docs/experiments/134-the-peace-thread.md` — DONE

---

## Then: Phase 3 — Applied Experiments

~50 experiment scripts need `:right`→`:truth`, `:left`→`:mercy` key rename and re-run. Strategy: batch the key renames with sed, then spot-check 5-6 high-profile experiments, then batch the rest.

### Key rename (can be batched):
```bash
for f in dev/experiments/*.clj dev/experiments/tabernacle/*.clj dev/experiments/ark/*.clj dev/scripts/*.clj; do
  sed -i 's/:right/:truth/g; s/:left/:mercy/g' "$f"
done
```

### Spot-check candidates (run and verify output):
- 108 (Ezekiel's vision) — cross-vision invariants
- 113 (The Cross) — christological findings
- 119 (The Fall) — Genesis 3, tree now visible
- 127 (Pathogen Prescriptions) — bronze serpent molecular findings
- 138 (Woman at the Well) — John 4

### Each spot-check: re-run, capture output, rewrite the doc.

---

## Then: Phase 4 — Paper Updates

The synthesis papers need updating with new numbers and terminology:
- `docs/the-courtroom.md`
- `docs/the-bronze-serpent.md`
- `docs/the-oracle-speaks.md`
- `docs/the-decomposition.md`
- `docs/the-departure.md`
- `MANIFEST.md`
- `docs/urim-and-thummim.md`

---

## Key Numbers to Remember

| Metric | Value |
|--------|------:|
| Grid | **Variant B** (Exodus 28:21, one tribe per stone) |
| Reader keys | `:aaron`, `:god`, `:truth`, `:mercy` |
| Readable words | 9,263 (091b) / 9,209 (134) |
| Total transitions | 929,134 |
| Total phrases | 475,483 |
| Basin attractors | 6,517 |
| Basin dead ends | 3,125 |
| Basin cycles | 112 |
| Basin top 3 | Finishing (10), Choosing (10), Death (10) |
| Voice vocabulary | 8,732 (knee at 2,221) |
| God eigenwords | 174 (p=0.020, 99th pctl) |
| Mercy eigenwords | 4,468 (most) |
| Mercy exclusion from lamb | 0th percentile |
| Mercy-exclusive words | 965 (largest) |
| Truth-exclusive words | 386 (smallest, contains grace) |
| God-exclusive words | 551 (contains "I will be") |
| Ghost zone | 12 words (face, mercy seat, veil, judgment, Elohim, forgive, wisdom, altar, blameless, strong, portion, righteousness-f) |
| Peace | God-only. Always. |
