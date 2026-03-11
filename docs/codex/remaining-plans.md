# Remaining Plans

Type: `planning`
State: `clean`

This is the codex-side forward plan from the current cleanup boundary.

The main contamination work is no longer the center of gravity.
The code path is cleaner.
The basin artifacts are rebuilt.
The tracked and custom DNA boundaries are much cleaner.
The `100/101` code/map layer is now reproducible from the experiment namespaces.
The vocabulary language is cleaner in the main active surfaces.
The operator gloss layer now has a real lexicon foundation.

So the next work is not rescue. It is orderly building.

## 1. Presentation Layer

The new `selah.lexicon` and `selah.for-the-human` foundation exists, but it is still early.

What is already true:

- Hebrew remains canonical.
- English help is presentation-only.
- Glosses now carry source/provenance.
- The lexicon can serve layered candidates.
- The English model layer has been materialized into `data/lexicon/en/model.edn`.

What still needs doing:

- expose alternative gloss candidates in UI and MCP, not just the best gloss
- add one operator review surface for bad model entries
- add richer result presentation objects for:
  - oracle ask
  - oracle question
  - thummim phrases
  - basin walks
- stop hand-formatting the same operator strings in multiple places

Goal:

- one presentation API
- many tool surfaces
- no new direct translation logic outside the presentation layer

## 2. English Review Workflow

The English layer now has:

- `model.edn`
- `model-reviewed.edn`
- `llm-reviewed.edn`
- `curated.edn`

The next practical task is to use that structure.

Immediate review path:

1. generate `review-queue` slices from suspicious model outputs
2. review and promote the worst entries into `model-reviewed`
3. promote stable, trusted entries into `curated`
4. use LLM refinement only on the ugly/ambiguous tail

Important principle:

- do not try to perfect all `12,826` forms by hand first
- clean the worst machine sludge first
- promote trust upward in layers

## 3. Multilingual Foundation

The architecture now supports more than English in principle, but only `:en` is real.

What should happen next:

- keep locale as a first-class parameter in presentation functions
- add per-locale file layout under `data/lexicon/<locale>/`
- keep gloss provenance attached regardless of locale
- separate lexicon gloss from contextual translation

Likely future locale layout:

- `data/lexicon/es/`
- `data/lexicon/fr/`
- `data/lexicon/de/`
- etc.

The sequence should be:

1. stabilize English pipeline
2. prove review/promotion workflow
3. add a second locale with the same layered structure

## 4. Operator Tooling

The repo still needs better operator ergonomics.

Good next additions:

- MCP/admin tool to show gloss candidates for a word
- MCP/admin tool to show review queue entries
- MCP/admin tool to promote or replace a candidate
- UI fragment to inspect one word’s candidate stack with provenance

That would let the user do real lexicon review without editing EDN files blindly.

## 5. Remaining Doc Work

The biggest code-path contamination issues are much improved.
The remaining debt is increasingly in documents.

Still needed:

- continue broader vocabulary-term cleanup in findings/docs outside the core active surfaces
- re-audit synthesis docs as new clean experiments land
- keep distinguishing:
  - 12,826-form full lexicon
  - ~7,300 oracle-closed vocabulary
  - ~2,050 voice vocabulary
  - curated display vocabulary

One rule should stay firm:

- docs should cite rebuilt Hebrew/data artifacts
- English should help the human understand, not silently stand in for evidence

## 6. Ark / Genome Narrative Pass

This is the next thing pulling hardest now that the lower layers are stronger.

The docs most likely to need a fresh codex pass are:

- `docs/ark/20-experiment-100.md`
- `docs/ark/22-the-map.md`
- `docs/ark/26-hit-play.md`
- `docs/ark/31-the-library-so-far.md`
- `docs/ark/33-the-genome.md`

Why these:

- `100/101` now stand on a cleaner experimental boundary
- the custom DNA layer is less contaminated than before
- some older wording may now be either too weak, too broad, or too notebook-era

The job is not to flatten them.
The job is to make sure the speaking layer now matches the strengthened evidence layer.

## 7. Experiment Hygiene

Even after the contamination cleanup, old script habits can return.

Watch for:

- stale helper inheritance
- copied formatting that assumes missing fields
- English creeping back into saved artifacts
- vocabulary language drifting again
- polished prose getting ahead of regenerated outputs

The safe pattern remains:

1. measure
2. save structured Hebrew/data
3. annotate for the human later
4. only then narrate

## 8. Suggested Next Order

If work resumes on this track, the next order should be:

1. audit and tighten the Ark/code/genome narrative docs against the stronger `100/101` and DNA boundaries
2. expose the lexicon review queue in a tool surface
3. start cleaning the worst English model entries
4. add candidate/alternative display in one UI or MCP view
5. stabilize the English review workflow
6. only then add the second locale

## 9. The Main Constraint

Do not collapse these layers again:

- text
- computation
- artifact
- presentation
- interpretation

If those stay separate, Selah can keep growing without getting muddy again.
