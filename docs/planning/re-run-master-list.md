# Re-Run Master List

*The oracle changed. Everything downstream needs to update.*

Last updated: 2026-03-26

---

## What Changed

Three changes to the oracle, applied 2026-03-25/26:

1. **Grid: Variant A → Variant B.** Continuous flow → one tribe per stone. Source: Exodus 28:21 ("each with its name"). Selected via experiment 140.
2. **Final forms unioned.** ך→כ, ץ→צ, ף→פ. Paleo-Hebrew had no finals. 1,024 words unlocked.
3. **Reader keys renamed.** `:right`→`:truth`, `:left`→`:mercy`. Courtroom labels, not geometric labels. Mercy at God's right hand (Psalm 110:1). Truth at the accused's right hand (Zechariah 3:1).

All prior experiment data, basin caches, reader profiles, and documentation reference the old oracle.

---

## Phase 1: Regenerate Caches

Everything depends on these. Do first.

- [ ] Delete `data/oracle-voice.edn` → run `(o/compute-oracle-voice)` (~3 min)
- [ ] Delete `data/basin-landscape.edn` → run basin landscape on new grid (~3 min with native oracle)
- [ ] Regenerate `data/experiments/096/` — word-index.edn, attractors.edn, cycles.edn, summary.edn

---

## Phase 2: Re-Run Core Experiments

These produce the numbers everything cites.

| # | Experiment | What it produces | Est. time |
|---|-----------|-----------------|-----------|
| [x] | **091** | Per-head counts, lamb split, eigenwords | Done 2026-03-27. Doc needs full rewrite. |
| [x] | **091b** | Full Torah vocabulary quorum (9,263 readable, 929,134 transitions) | Done 2026-03-27. In 091 doc, needs rewrite. |
| [x] | **092** | Grid permutation (p=0.020 God, 0th pctl mercy exclusion) | Done 2026-03-27. Doc needs full rewrite. |
| [x] | **092b** | Ramban pair permutation (11 pairs, structural) | Done 2026-03-27. Doc needs full rewrite. |
| [x] | **093** | Level 2 Thummim synthesis | Done 2026-03-27. Doc needs full rewrite. |
| [x] | **093h** | Ghost zone (12 ghosts, 0 absent, אלהים now ghost) | Done 2026-03-27. Doc REWRITTEN. |
| [x] | **094** | Full sweep (475,483 phrases, 81.5% multi-reading) | Done 2026-03-27. Doc REWRITTEN. |
| [x] | **097** | Per-head basins | Done 2026-03-27. Doc REWRITTEN. |
| [x] | **134** | Peace Thread (Mercy=965, God=551, Truth=386, Aaron=533) | Done 2026-03-27. Doc REWRITTEN. |

---

## Phase 3: Re-Run Applied Experiments

~50 experiments that call the oracle. Each needs:
1. Rename `:right`→`:truth`, `:left`→`:mercy` in the script
2. Re-run and capture new output
3. Update the corresponding doc if one exists

### Tabernacle walk (12 scripts)

| [ ] | Script | Doc |
|-----|--------|-----|
| [ ] | `dev/experiments/tabernacle/01_gate.clj` | `docs/tabernacle/01-the-gate.md` |
| [ ] | `dev/experiments/tabernacle/02_courtyard.clj` | `docs/tabernacle/02-the-courtyard.md` |
| [ ] | `dev/experiments/tabernacle/03_altar.clj` | `docs/tabernacle/03-the-altar.md` |
| [ ] | `dev/experiments/tabernacle/04_laver.clj` | `docs/tabernacle/04-the-laver.md` |
| [ ] | `dev/experiments/tabernacle/05_curtains.clj` | `docs/tabernacle/05-the-curtains.md` |
| [ ] | `dev/experiments/tabernacle/06_table.clj` | `docs/tabernacle/06-the-table.md` |
| [ ] | `dev/experiments/tabernacle/07_menorah.clj` | `docs/tabernacle/07-the-menorah.md` |
| [ ] | `dev/experiments/tabernacle/08_incense_altar.clj` | `docs/tabernacle/08-the-incense-altar.md` |
| [ ] | `dev/experiments/tabernacle/09_veil.clj` | `docs/tabernacle/09-the-veil.md` |
| [ ] | `dev/experiments/tabernacle/10_ark.clj` | `docs/tabernacle/10-the-ark.md` |
| [ ] | `dev/experiments/tabernacle/11_speaking.clj` | `docs/tabernacle/11-the-speaking.md` |
| [ ] | `dev/experiments/tabernacle/12_meeting.clj` | `docs/tabernacle/12-the-meeting.md` |

### Ark walk (15 scripts)

| [ ] | Script | Doc |
|-----|--------|-----|
| [ ] | `dev/experiments/ark/01_foundation.clj` | `docs/ark/` (various) |
| [ ] | through `dev/experiments/ark/15_the_king.clj` | |

### Vision experiments (4)

| [ ] | Script | Doc |
|-----|--------|-----|
| [ ] | `dev/experiments/108_ezekiels_chariot.clj` | `docs/experiments/108-ezekiels-chariot.md` |
| [ ] | `dev/experiments/109_isaiahs_temple.clj` | `docs/experiments/109-isaiahs-temple.md` |
| [ ] | `dev/experiments/110_revelation_throne.clj` | `docs/experiments/110-revelation-throne.md` |
| [ ] | `dev/experiments/111_daniels_thrones.clj` | `docs/experiments/111-daniels-thrones.md` |

### Text studies (~20)

| [ ] | Script | Doc |
|-----|--------|-----|
| [ ] | `103_throne_room.clj` | `103-the-throne-room.md` |
| [ ] | `104_the_spies.clj` | |
| [ ] | `105_the_four_bloods.clj` | |
| [ ] | `106_has_woman_been_bit.clj` | `106-has-woman-been-bit.md` |
| [ ] | `107_the_bronze_serpent.clj` | `107-the-bronze-serpent.md` |
| [ ] | `112_the_suffering_servant.clj` | `112-the-suffering-servant.md` |
| [ ] | `113_the_cross.clj` | `113-the-cross.md` |
| [ ] | `114_psalm_22.clj` | `114-psalm-22.md` |
| [ ] | `115_the_cornerstone.clj` | `115-the-cornerstone.md` |
| [ ] | `116_the_binding_of_isaac.clj` | |
| [ ] | `117_jacobs_ladder.clj` | |
| [ ] | `118_the_seven_days.clj` | |
| [ ] | `119_the_fall.clj` | |
| [ ] | `120_the_burning_bush.clj` | |
| [ ] | `121_jacob_wrestles.clj` | |
| [ ] | `122_ruth.clj` | `122-ruth.md` |
| [ ] | `123_the_cure_for_leprosy.clj` | |
| [ ] | `124_the_body_coordinates.clj` | `124-the-body-coordinates.md` |
| [ ] | `125_the_invisible_blessing.clj` | |
| [ ] | `131_the_wall.clj` | `131-the-wall.md` |
| [ ] | `132_the_center_of_everything.clj` | `132-the-center-of-everything.md` |

### Other oracle-dependent scripts

| [ ] | Script |
|-----|--------|
| [ ] | `085_asking_questions.clj` |
| [ ] | `086_oracle_engine.clj` |
| [ ] | `089_hebbian_oracle.clj` |
| [ ] | `137_hoh_across_spaces.clj` |
| [ ] | `137d_yhwh_loves.clj` |
| [ ] | `138_the_woman_at_the_well.clj` |
| [ ] | `dev/scripts/the_cross.clj` |
| [ ] | `dev/scripts/hannah/*.clj` (3 files) |
| [ ] | `dev/scripts/thummim/*.clj` (2 files) |
| [ ] | `dev/scripts/proteins/play_leprosy.clj` |

---

## Phase 4: Update Documentation

### Synthesis papers (6 files)

Every one references old reader names or ghost zone.

| [ ] | File | Key issues |
|-----|------|-----------|
| [x] | `docs/the-courtroom.md` | **REWRITTEN** 2026-03-27. |
| [x] | `docs/the-bronze-serpent.md` | **UPDATED** 2026-03-27. Bronze→Mercy-only. |
| [x] | `docs/the-oracle-speaks.md` | **REWRITTEN** 2026-03-27. |
| [x] | `docs/the-decomposition.md` | **FIXED** 2026-03-27. 1 line. |
| [x] | `docs/the-departure.md` | **UPDATED** 2026-03-27. Evidence table + findings. |
| [ ] | `docs/the-mirror.md` | Traversal and reader pair discussion. |

### Main reference docs (4 files)

| [ ] | File | Key issues |
|-----|------|-----------|
| [x] | `MANIFEST.md` | **UPDATED** 2026-03-27. |
| [ ] | `docs/urim-and-thummim.md` | **28 old refs. Full rewrite needed. The big one.** |
| [ ] | `docs/the-journey.md` | Cites specific numbers. |
| [ ] | `docs/skills/oracle.md` | Old reader keys in examples and tables. |

### Experiment docs (~35 files)

| [ ] | File | Key issues |
|-----|------|-----------|
| [ ] | `091-the-quorum.md` | Reader table, courtroom section, per-head solos. |
| [ ] | `091-breastplate-attention.md` | Architecture table maps old reader names. |
| [ ] | `091b-the-full-quorum.md` | Stale basin stats: "6,104 fixed / 2,453 transient / 4,256 dead". |
| [ ] | `092b-the-ramban-principle.md` | Reader labels. |
| [ ] | `093-synthesis.md` | "4 traversal directions (Aaron, God, right cherub, left cherub)". |
| [ ] | `093b-the-priests-menu.md` | Reader distribution tables. |
| [ ] | `093h-the-ghost-zone.md` | Ghost zone lists — king, darkness, land, tree now readable. |
| [ ] | `096-basin-landscape.md` | Stale basin counts throughout. Dead zone section. Fixed point lists. |
| [ ] | `097-per-head-basins.md` | Per-head fixed points, John 1 mapping, reader domains. |
| [ ] | `103-the-throne-room.md` | Reader labels. |
| [ ] | `106-has-woman-been-bit.md` | "Left cherub sees אביה most clearly". |
| [ ] | `107-the-bronze-serpent.md` | 7 references with old labels. |
| [ ] | `108-ezekiels-chariot.md` | 19+ references with old labels. |
| [ ] | `109-isaiahs-temple.md` | Reader labels. |
| [ ] | `110-revelation-throne.md` | Reader labels. |
| [ ] | `111-daniels-thrones.md` | Reader labels. |
| [ ] | `112-the-suffering-servant.md` | Reader labels. |
| [ ] | `113-the-cross.md` | Reader labels. |
| [ ] | `114-psalm-22.md` | Reader labels. |
| [ ] | `115-the-cornerstone.md` | Table: "Right cherub (justice)" / "Left cherub (mercy)" — BACKWARDS. |
| [ ] | `122-ruth.md` | Reader labels. |
| [ ] | `124-the-body-coordinates.md` | Reader labels. |
| [ ] | `131-the-wall.md` | Reader labels. |
| [ ] | `132-the-center-of-everything.md` | Reader labels. |
| [ ] | `134-the-peace-thread.md` | `:by-reader` table uses old keys. "829 God-exclusive" stale. |
| [ ] | `137-hoh-across-spaces.md` | Reader labels. |
| [ ] | `140-grid-variants.md` | References "Variant A" as current. Now B is current. |

### Walk docs (~26 files)

| [ ] | File | Key issues |
|-----|------|-----------|
| [ ] | `docs/tabernacle/01-13` (13 files) | Reader labels, reader-specific findings. |
| [ ] | `docs/ark/15, 16, 18, 21, 23, 28, 34` (7 files) | Reader labels, ghost zone, basin numbers (23 cites "6,104"). |

### Spy reports (10 files)

| [ ] | File | Key issues |
|-----|------|-----------|
| [ ] | `docs/findings/spy-basin-attractors.md` | Stale counts: "6,104 fixed, 2,453 transient, 4,256 dead". |
| [ ] | `docs/findings/spy-number-catalog.md` | Basin numbers. |
| [ ] | `docs/findings/spy-oracle-graph.md` | Basin attractors. |
| [ ] | `docs/findings/spy-cross-reference-web.md` | Basin numbers. |
| [ ] | `docs/findings/spy-code-documentation.md` | `:right`/`:left` references. |
| [ ] | `docs/findings/spy-journal-update.md` | Basin attractors. |
| [ ] | `docs/findings/spy-angels-and-throne.md` | Reader labels. |
| [ ] | `docs/findings/spy-breastplate-genome.md` | Reader labels. |
| [ ] | `docs/findings/spy-dna-reading-frame.md` | Reader labels. |
| [ ] | `docs/findings/spy-paper-outline.md` | Reader labels. |

### Questions (8 files)

| [ ] | File |
|-----|------|
| [ ] | `docs/questions/11-veil.md` |
| [ ] | `docs/questions/12-face.md` |
| [ ] | `docs/questions/24-the-center.md` |
| [ ] | `docs/questions/30-inside-the-holy-of-holies.md` |
| [ ] | `docs/questions/32-the-narrow-gate.md` |
| [ ] | `docs/questions/33-the-tree-of-life.md` |
| [ ] | `docs/questions/35-the-genome-in-the-ark.md` |
| [ ] | `docs/questions/37-39` (3 files) |

### Vision docs (6 files)

| [ ] | File |
|-----|------|
| [ ] | `docs/visions/00-four-prophets-one-throne.md` |
| [ ] | `docs/visions/01-the-throne-room.md` |
| [ ] | `docs/visions/02-what-god-does-not-see.md` |
| [ ] | `docs/visions/05-the-hidden-words.md` |
| [ ] | `docs/visions/06-four-angles.md` |
| [ ] | `docs/visions/07-one-throne.md` |

### Planning docs (4 files)

| [ ] | File |
|-----|------|
| [ ] | `docs/planning/architecture-confidence.md` |
| [ ] | `docs/planning/post-final-form-roadmap.md` |
| [ ] | `docs/planning/the-big-picture.md` |
| [ ] | `docs/planning/vision-walks.md` |

---

## Phase 5: Data Files

| [ ] | File/Directory | Action |
|-----|---------------|--------|
| [ ] | `data/oracle-voice.edn` | Regenerate (Phase 1) |
| [ ] | `data/basin-landscape.edn` | Regenerate (Phase 1) |
| [ ] | `data/experiments/096/` (4 files) | Regenerate (Phase 1) |
| [ ] | `data/experiments/092/` | Regenerate after experiment 092 re-run |
| [ ] | `data/experiments/092b/` | Regenerate after 092b re-run |
| [ ] | `data/experiments/097/` | Regenerate after 097 re-run |
| [ ] | `data/experiments/*-output.txt` | Regenerate from experiment re-runs |
| [ ] | `data/dna/*-oracle.edn` (~25 files) | Check for old reader keys, regenerate if needed |

---

## What NOT to re-run

~85 experiments (000-067, 074-076, 078, 081, 095, 098-101, 130, 135-136 series) do not use the oracle. They use gematria, ELS, the 4D space, or spectral analysis. They are unaffected.

---

## Terminology Guide

When updating docs, use this consistent language:

| Old | New |
|-----|-----|
| Right cherub | Mercy / the Lamb / the Defense |
| Left cherub | Truth / the Prosecution |
| God's right hand | Mercy's seat |
| God's left hand | Truth's seat |
| `:right` | `:truth` |
| `:left` | `:mercy` |
| "Right-exclusive" | "Mercy-exclusive" |
| "Left-exclusive" | "Truth-exclusive" |
| "the right/mercy head" | "Mercy" |
| "the left/justice head" | "Truth" |

In diagrams:
```
            GOD (Judge)

  MERCY/LAMB          TRUTH/PROSECUTION
  God's right         God's left
  Accused's left      Accused's right

            AARON (Accused)
```

---

## Codex Verification Checklist

After all re-runs are complete, Codex should verify:

- [ ] No remaining `:right` or `:left` in `src/selah/`
- [ ] No remaining "Right cherub" or "Left cherub" in `docs/` (except historical explanation of the change)
- [ ] No remaining "6,104" or "4,256" or "2,453" basin counts in `docs/`
- [ ] Basin counts in docs match `data/basin-landscape.edn`
- [ ] Ghost zone list in `093h` matches current oracle output
- [ ] All `data/experiments/` files dated after the re-run
- [ ] `MANIFEST.md` lines 15-17 updated
- [ ] `docs/urim-and-thummim.md` reader table updated
- [ ] Every experiment doc that cites reader counts has been verified against the new oracle

---

## Scope

| Category | Files | Action |
|----------|------:|--------|
| Caches | 3 | Regenerate |
| Core experiments | 9 | Re-run |
| Applied experiments | ~50 | Key rename + re-run |
| Documentation | ~75 | Terminology + numbers |
| Data files | ~30 | Regenerate |
| **Total** | **~167** | |

---

*One foundation. One re-run. No wasted work.*
