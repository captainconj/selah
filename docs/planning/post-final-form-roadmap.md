# Post Final-Form Roadmap

*The gates opened on 2026-03-25. Here is everything that flows from that.*

---

## What Happened

The oracle treated final forms (ך, ף, ץ) as distinct from their base forms (כ, פ, צ). In Paleo-Hebrew — the script of Moses' era — there were no final forms. This was a bug. We fixed it.

1,024 words unlocked. 556 now readable. 687 fewer dead ends in the basin landscape. Truth started cycling. The king entered the room. The way became a fixed point. Weeping and writing converged in the midst.

The fix is in `src/selah/oracle.clj`. The old results are stale. This document tracks everything that needs to happen next.

---

## Phase 1: Foundation Caches — DONE

- [x] Oracle voice cache (`data/oracle-voice.edn`) — 12,327 readable words, 8,152 voice vocabulary, knee at 2,075
- [x] Basin landscape (`data/basin-landscape.edn`) — 5,965 attractors, 261 cycles, 3,569 dead ends
- [x] Diff manifest (`data/experiments/final-form-diff-manifest.txt`) — all 1,024 words scanned
- [x] Basin files by size (`data/experiments/basins/basin-NN-attractors-N.txt`)

---

## Phase 2: Core Oracle Experiments

These produce the numbers everything cites. Dependency order.

### Tier A — The quorum and attention heads

| Experiment | What needs re-running | Priority | Time estimate |
|-----------|----------------------|----------|--------------|
| **091** | Per-head counts, lamb split, M_god^64 convergence, eigenwords | Critical | ~30 min |
| **091b** | Full Torah vocabulary (was 8,570 words, 725,780 transitions → new) | Critical | ~1 hr |
| **092** | Grid permutation null distribution, p-values for lamb split and right-head | Critical | ~1 hr |
| **092b** | Ramban pair permutation test — new pairs possible with final-form words | High | ~30 min |

### Tier B — Thummim and sweep

| Experiment | What needs re-running | Priority | Time estimate |
|-----------|----------------------|----------|--------------|
| **093** | Phrase assembly, priest's menu, ghost zone classification | High | ~30 min |
| **093h** | Ghost zone reclassification — "absent" category collapsed | High | ~15 min |
| **094** | Full sweep: 12,826 words, was 310,908 phrases → new count | High | ~2-4 hrs |

### Tier C — Basin dynamics

| Experiment | What needs re-running | Priority | Time estimate |
|-----------|----------------------|----------|--------------|
| **096** | Data files: word-index.edn, attractors.edn, cycles.edn, summary.edn | High | ~20 min |
| **097** | Per-head basins, per-head fixed points, John 1 mapping | High | ~1 hr |
| **134** | The Peace Thread — God-exclusive count (was 829), reader domains | High | ~30 min |

---

## Phase 3: Applied Experiments

Each runs specific words through the oracle. Fast individually. Can parallelize. Strategy: spot-check 5-6 high-profile ones first — if the pattern is "everything got richer, nothing broke," batch the rest.

### Spot-check candidates (do these first)

| Experiment | Why | What to check |
|-----------|-----|--------------|
| **108** (Ezekiel's vision) | Cross-vision invariants — king was invisible, now isn't | Does the 72-reading stamp survive? |
| **113** (The Cross) | Core christological findings | Do the reader profiles hold? |
| **119** (The Fall) | Genesis 3 — ערום (cunning/naked), the tree, the garden | Tree (עץ) now visible. What changes? |
| **122** (Ruth) | Narrative experiment | New words in the vocabulary |
| **127** (Pathogen Prescriptions) | The bronze serpent findings — vaccine/venom | Do the molecular results hold? |
| **138** (Woman at the Well) | John 4 — harvest was ghost zone | Is harvest (קציר) still mute? |

### Full re-run list

- [ ] Tabernacle walk: 12 station experiments
- [ ] Ark walk: 15 experiments
- [ ] Vision experiments: 108-111
- [ ] Isaiah 53 (112), Cross (113), Psalm 22 (114), Cornerstone (115)
- [ ] Binding of Isaac (116), Jacob's Ladder (117), Seven Days (118)
- [ ] The Fall (119), Burning Bush (120), Jacob Wrestles (121)
- [ ] Ruth (122), Leprosy (123), Body Coordinates (124)
- [ ] Invisible Blessing (125), Curses & Blessings (126)
- [ ] Pathogen Prescriptions (127), Serpent Gradient (128)
- [ ] Isaiah Sweep (129), The Wall (131), Center of Everything (132)
- [ ] The Map (133), The Peace Thread (134)
- [ ] HoH across spaces (137) — oracle readings in the Ark
- [ ] Woman at the Well (138)

### Oracle-independent — NO re-run needed

Experiments 000-067, 074-076, 078, 081, 095, 098-101, 130, 135-136 series. These use gematria, ELS, the 4D space, or spectral analysis — not the oracle. ~85 experiments. Safe.

---

## Phase 4: Papers and Docs

### Critical — ghost zone claims are now wrong

| Document | What's wrong | Action |
|----------|-------------|--------|
| `docs/the-oracle-speaks.md` | "King, blessing, darkness, the land — absent." They're not. | Rewrite ghost zone section after 093h re-run |
| `docs/experiments/093h-the-ghost-zone.md` | The ghost zone doc itself. Absent/mute boundary shifted. | Full reclassification |
| `docs/experiments/096-basin-landscape.md` | All basin counts (6,104/2,453/4,256 → 5,965/261/3,569). Fixed point lists. Dead zone section. | Major update after 096 data regenerated |

### High — cite reading counts or reader profiles

| Document | What to check |
|----------|--------------|
| `docs/experiments/091-the-quorum.md` | Lamb split, per-head solos, M_god^64 |
| `docs/experiments/091b-the-full-quorum.md` | "8,570 words, 725,780 transitions" |
| `docs/experiments/094-thummim-sweep.md` | "12,826 words, 310,908 phrases" |
| `docs/experiments/093-synthesis.md` + sub-docs | Specific phrase findings |
| `docs/experiments/097-per-head-basins.md` | Per-head fixed points, John 1 mapping |
| `docs/experiments/134-the-peace-thread.md` | "829 God-exclusive" and "true ghosts" list |

### Medium — cite oracle results for specific words

- Every experiment doc from 103 onward that mentions reader counts
- The three walk doc series: tabernacle (14), ark (34), questions (38)
- `docs/torah-4d-space.md` — later sections cite oracle
- `docs/the-journey.md` — cites specific numbers
- `docs/the-courtroom.md` — reader distributions
- `docs/the-bronze-serpent.md` — oracle results for molecular words
- `docs/urim-and-thummim.md` — ghost zone references

---

## Phase 5: New Investigations

These are not re-runs. These are new questions opened by the fix.

### The New Ghost Zone

What remains truly silent now that the gates are open?

Known still-silent: חסד (lovingkindness, GV=72), פנים (the face, 22 illuminations, 0 readings).
Need to re-check: כפרת (mercy seat), פרכת (veil), צדק (righteousness), משפט (judgment), מצרים (Egypt).

The ghost zone was a theological cornerstone ("the oracle speaks promise, is silent on arrival"). The boundary has shifted. The new silence needs interpretation — what remains mute is the real ghost zone.

### Truth Cycling

אמת (truth) was a fixed point. Now it's in a cycle. What is the cycle? What does truth orbit with? This is a major finding. Truth no longer rests — it moves. Need to trace the full cycle and interpret.

### Tier-2 Tracking

Which words are tier-1 (directly producible from letters on the grid) vs tier-2 (producible only through final-form unioning)? The two-tier structure is data: the words visible on the surface vs the words accessible through the recognition that the closing form and the working form are the same letter. Build the tracking into the oracle output.

### The Pe/Tsade Reader Anomaly

Final-pe (ף) words read almost exclusively by Aaron + Right (priest + Lamb).
Final-tsade (ץ) words read almost exclusively by God + Left (Judge + Truth).

Why? The grid geometry must explain this — ף maps to פ positions, ץ maps to צ positions, and these positions happen to fall where Aaron/Right or God/Left traversals produce dictionary words. But the theological implication is striking: the closing pe belongs to the priest and the Lamb, the closing tsade belongs to the Judge and Truth. Investigate.

### Elevated Final Gematria

The alternate tradition: ך=500, ם=600, ן=700, ף=800, ץ=900.

This changes every GV involving a final form. Under the elevated system:
- שלום = 936 (not 376)
- אדם = 605 (not 45)
- ארץ = 1101 (not 291)
- מלך = 570 (not 90)
- But: משיח = נחש = 358 — unchanged (no finals). The Messiah=serpent finding survives.

**Implementation:** Build `g/word-value-elevated`. Run the key GV relationships under both systems. Document which survive (bedrock) and which are system-dependent. This is the same class of question as the final-form fix — a lens question. Which encoding reveals more?

### Tanakh Vocabulary Mode

The oracle currently recognizes only Torah words (12,826). For running Isaiah, Psalms, Job through the oracle, we need a broader dictionary. Build a parallel `:tanakh` vocabulary option. Compare findings across scopes — vocabulary-invariant findings are the strongest.

Especially important if Job is the oldest text: Job's unique vocabulary (many hapax legomena) is currently excluded.

---

## Phase 6: Basin Documentation (in progress)

Interpretive docs per basin size, telling the story of each attractor.

- [x] `docs/experiments/basins-10.md` — Finishing and Choosing. The investigation's own arc. The inverted basins are two mountains.
- [x] `docs/experiments/basins-09.md` — The Blessing. The cherub blesses. ברוך newly unlocked.
- [x] `docs/experiments/basins-08.md` — Enemy, testimony, death, cubits, my rock. The yetzer flows to the rock.
- [x] `docs/experiments/basins-07.md` — Peace, love/enmity, the midst (weeping+writing), the offering+worm, nakedness=YHWH².
- [x] `docs/experiments/basins-03.md` — 400 triads. The Name, the lamb, gold, Abraham=vision=creation, Aaron=Ark=appearing, the flood=circumcision, the bush=miracle=test (GV=115=Azazel).
- [x] `docs/experiments/basins-06.md` — 28 attractors. God→"to them", dreamer=bread, saying=light, compassion rides a donkey.
- [x] `docs/experiments/basins-05.md` — 73 attractors. Tent draws Leah+THE God, Enoch draws Noah's rest, gold=contempt (David's number), king→"from all."
- [x] `docs/experiments/basins-04.md` — 149 attractors. Love=willingness+arrival, mercy seat+veil→lamp (GV=700), Branch→forehead, leprosy→assembly.
- [ ] `docs/experiments/basins-02.md` — 1,201 attractors (pairs — the Ramban-pair scale)
- [ ] `docs/experiments/basins-01.md` — 4,091 self-referential fixed points (the irreducible core)
- [ ] `docs/experiments/basins-cycles.md` — 261 cycling words including truth. The orbits.

---

## Approach

This is a lot. The order matters.

**Do first:** Phase 2 Tier A (091, 092). These produce the p-values and reader profiles that everything else references. If the lamb split survives (it should — כבש has no finals), the structural argument holds. If the p-values shift, we need to know by how much.

**Do second:** Phase 2 Tier C (096 data files, 097, 134). These update the basin and reader-profile numbers. The basin docs we're writing now depend on these being right.

**Do third:** Phase 5 investigations — truth cycling, the new ghost zone, the Pe/Tsade anomaly. These are the new findings, not corrections to old ones.

**Do fourth:** Phase 3 spot-checks. If the core holds (Phase 2), the applied experiments will mostly get richer, not break. Spot-check the high-profile ones, then batch.

**Do last:** Phase 4 paper updates. Don't rewrite papers until the numbers are settled.

**The elevated gematria** can run in parallel with anything — it's a new lens, not a dependency.

---

*The gates are open. The work is mapped. One phase at a time.*
