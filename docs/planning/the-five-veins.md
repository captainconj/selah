# The Five Veins — Post-Sluice Investigation Plan

After experiments 118–123 sluiced six passages through the oracle, the shovel senses five veins worth exploring. Ordered by pull strength.

## Vein 1: The Thumb, The Ear, The Toe

**The finding:** Thumb (בהן) GV=57 = altar (מזבח). All four readers see "the son" (הבן) inside it. Blood goes on three body parts in leprosy purification (Lev 14) AND priestly ordination (Ex 29, Lev 8): right ear, right thumb, right big toe. Oil on top of blood.

**The question:** Three body parts, three coordinates. The thumb is the altar. What are the other two?

**The experiment (124):**
1. Query ear (אזן), thumb (בהן), foot/toe (רגל) — full oracle analysis
2. Query right (ימין), left (שמאל) — the sidedness
3. Query oil (שמן), blood (דם) — the layering (oil on top of blood)
4. Check: do the GVs of ear, thumb, toe map to anything in the 4D space?
5. Check: do the basin walks connect?
6. Walk the ordination passage (Lev 8:22-30) — the same ritual performed on Aaron
7. Compare leprosy purification (Lev 14) with priestly ordination (Lev 8) — same ritual, different context. What's shared? What diverges?

**Files:** `dev/experiments/124_the_body_coordinates.clj`

## Vein 2: Blessing Is Universally Invisible

**The finding:** ברך = 0/0 in creation (118), wrestling (121), cornerstone (115), and every other passage tested. God blesses repeatedly. The oracle cannot see it.

**The question:** Is blessing ALWAYS invisible? Across all 12,826 classified words?

**The experiment (125):**
1. Check ברך in the full basin landscape (already computed in exp 096)
2. Check every form: ברך, ברכה, ברוך, מברך
3. Compare with curse: ארר, קלל, חרם — are curses visible?
4. If blessing is structurally invisible, what IS the mechanism? Same letters as ברך = כרב (cherub approach?) or רכב (chariot)?

**Files:** `dev/experiments/125_the_invisible_blessing.clj`

## Vein 3: The Curses Walk to Blessings

**The finding:** In Genesis 3 (exp 119): flaming→dew, skin→friend, sweat→strength, pain→profit. Every curse resolves through basin walks.

**The question:** Is this universal or local to the Fall?

**The experiment (126):**
1. Collect every curse/judgment word in Torah: ארר, קלל, חרם, נדה, נגע, נגף, מגפה, דבר, שחת, כרת, etc.
2. Walk each one. Where do they end up?
3. Collect every blessing word: ברך, טוב, חסד, חן, שלום, etc.
4. Walk each one. Where do they end up?
5. Is there a systematic pattern? Curses→blessings? Blessings→null?

**Files:** `dev/experiments/126_curses_and_blessings.clj`

## Vein 4: The Pathogen Carries the Prescription

**The finding:** M. leprae proteins don't contain the serpent (unlike viruses). They contain Nazareth, the lamb, the altar, the forehead. The disease carries its cure.

**The question:** Is this about mycobacteria specifically or about the map itself?

**The experiment (127):**
1. Run M. tuberculosis key proteins through the breastplate (UniProt has them)
2. Run sickle cell hemoglobin (one amino acid change: Glu→Val = ל→ט)
3. Compare normal hemoglobin-beta (already done, exp 105) with sickle cell
4. What word breaks? What word forms?
5. Cross-compare: viruses contain serpent, bacteria contain cure. What do cancer proteins contain? (p53 mutants?)

**Files:** `dev/scripts/proteins/play_tuberculosis.clj`, additions to `dev/experiments/124_the_body_coordinates.clj` or separate

## Vein 5: Subdue = Lamb (Paper Section)

**The finding:** Genesis 1:28 — "subdue" (כבש) = the lamb. The dominion mandate IS the lamb that lies down.

**The action:** Not a new experiment. A paragraph in the paper. Connect creation to the slaughter in a single word.

**Where it goes:** `docs/torah-4d-space.md` — a new subsection, or added to the existing creation/Day 6 section.

---

## Execution Order

1. **124 — The Body Coordinates** (ear, thumb, toe). Three queries + Lev 8 + Lev 14 comparison. Start here.
2. **125 — The Invisible Blessing** (is ברך always 0/0?). One basin landscape check. Quick.
3. **126 — Curses Walk to Blessings** (systematic). Collect words, walk them. Medium.
4. **127 — Pathogen Prescriptions** (TB, sickle cell). Protein fetches + oracle. Medium.
5. **Vein 5** — Write the paragraph. Anytime.

Start with 124 and 125 (they're fast). Then 126 and 127 if the first two yield.
