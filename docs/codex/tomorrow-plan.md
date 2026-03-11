# Tomorrow Plan

Type: `planning`
State: `clean`

Date: 2026-03-10

This is the next-day plan after tonight's cleanup, rebuilding, and Ark/DNA/code-map tightening work.

It is not a generic backlog.
It is the next ordered pass from the current state of the repo.

## What Changed Tonight

Tonight materially improved the project boundary:

- core English contamination was removed from the main live paths
- `096/097` basin artifacts were rebuilt from cleaned builders
- the operator layer gained `selah.for-the-human` and `selah.lexicon`
- the English model lexicon was materialized into a reviewable layered store
- the tracked DNA library artifacts were refreshed
- the custom DNA boundary was cleaned and corrected, including bad leprosy/TB accessions
- `100/101` were turned into reproducible experiment runners
- the Ark/code/genome docs were re-audited and key summary drift was tightened

This means tomorrow does not need to begin with rescue.
It can begin with ordered refinement.

## Current Ground Truth

What now stands:

- Hebrew/data path is much cleaner than before
- English help is separated into the operator layer
- tracked library DNA artifacts are refreshed
- custom DNA artifacts are refreshed through the clean path
- the code/map layer is reproducible
- the Ark/genome narrative layer is being tightened against rebuilt artifacts

What is still open:

- broader doc tightening above the stronger evidence layer
- operator tooling for lexicon review
- practical cleanup of the English model tail
- multilingual foundation beyond English
- ongoing experiment hygiene as new passage work lands

## Tomorrow's Order

## 1. Finish the Ark / Genome Narrative Pass

Priority docs:

- `docs/ark/20-experiment-100.md`
- `docs/ark/22-the-map.md`
- `docs/ark/26-hit-play.md`
- `docs/ark/31-the-library-so-far.md` (now tightened at the table layer)
- `docs/ark/33-the-genome.md`

Goal:

- make sure the speaking layer matches the rebuilt code/map/genome artifact layer
- keep the fire, remove compression drift
- separate measured table facts from highlighted motifs and interpretation

Expected result:

- one clearer Ark/code/genome document band that can actually stand

## 2. Tighten the DNA Narrative Boundary

Use the cleaned tracked/custom artifact boundary to reassess the DNA-facing docs and reports.

Main jobs:

- note which DNA docs still quote stale report language
- regenerate or retire any remaining stale saved report text that still matters
- keep the distinction clear:
  - refreshed artifact
  - highlighted motif
  - interpretation

Expected result:

- no major DNA narrative doc leaning on stale pre-cleanup artifact language

## 3. Build Operator Lexicon Review Surfaces

The layered lexicon exists, but review still lives too close to the filesystem.

Tomorrow should add at least one real operator surface for review:

- MCP tool to inspect all gloss candidates for a word
- MCP tool to inspect the review queue
- MCP tool to promote/remove/edit a candidate

If time allows:

- add one small UI/admin view for a single word's candidate stack and provenance

Expected result:

- English cleanup becomes operational, not just structural

## 4. Start Cleaning the Worst English Tail

Use the review workflow already built.

Order:

1. inspect the current review queue
2. remove obvious garbage
3. promote obvious stable fixes into `model-reviewed`
4. reserve `curated` for truly trusted entries

Focus on:

- `<unk>`-style sludge
- malformed transliterations
- obviously wrong glosses
- words that appear often in operator workflows

Expected result:

- better English help where it matters most, without contaminating evidence paths

## 5. Strengthen Presentation Consistency

The `for-the-human` layer is real, but still early.

Tomorrow's target:

- stop duplicating operator-facing formatting across UI/MCP surfaces
- make more result shapes flow through shared presentation objects
- expose alternate gloss candidates where useful

Expected result:

- one presentation API feeding multiple user surfaces

## 6. Keep the Multilingual Foundation in View

Do not build a second locale yet unless English review surfaces are stable.

But do preserve the shape:

- locale-aware APIs stay first-class
- provenance stays attached
- lexicon gloss remains distinct from contextual translation

If there is spare time:

- add one placeholder second-locale directory structure and loader path, but no large content push yet

## 7. Maintain Experiment Hygiene

As Claude continues sluicing passage work, keep guarding against regression:

- no English in saved artifacts
- no stale helper inheritance
- no missing-field assumptions in output templates
- no vocabulary-level blur
- no polished prose outrunning rebuilt outputs

This remains a live process, not a one-time cleanup.

## Specific Suggested Sequence

If tomorrow starts fresh, the clean order is:

1. finish Ark/code/genome narrative tightening
2. check remaining DNA-facing docs for stale language
3. add MCP lexicon review surfaces
4. work the review queue
5. improve shared presentation objects
6. only then think about second-locale scaffolding

## What Not To Do Tomorrow

- do not collapse English help back into core artifacts
- do not mix regenerated data with unreviewed narrative edits in the same breath
- do not start multilingual expansion before the English review loop is usable
- do not let new experiments copy stale display patterns forward again

## The Main Standard

Tomorrow should keep this order intact:

1. observe
2. save structured Hebrew/data
3. annotate for the human
4. then speak

That is how the well stays clear.
