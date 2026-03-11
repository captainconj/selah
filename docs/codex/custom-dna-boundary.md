# Custom DNA Boundary

Type: `audit / cleanup boundary`
State: `clean`

Date: 2026-03-10

This note records the current state of the untracked/custom DNA layer after the clean `selah.dna` path was extended outward.

## What Changed

- `src/selah/dna.clj` now has a shared custom-entry path via `experiment-entry`.
- The shared path fetches a named sequence, optionally applies a mutation, runs the clean oracle pipeline, and saves EDN/report artifacts with provenance fields.
- The shared path now preserves real top-word positions and stores fetched sequence descriptions.

## Cleanly Refreshed Custom Artifacts

These custom artifacts were regenerated through the clean path and verified to have:

- no baked `:meaning` / `:next-meaning`
- real numeric top-word positions
- a saved `:description`

Refreshed set:

- `candida-als3`
- `hbb-normal`
- `hbb-sickle`
- `human-inmt`
- `human-insulin`
- `human-oxytocin`
- `lactobacillus-slpa`
- `malaria-csp`
- `mescaline-omt1`
- `p53-normal`
- `p53-r175h`
- `prion-prp`
- `psilocybin-psim`
- `scopolamine-h6h`
- `serotonin-tph2`
- `tardigrade-dsup`
- `thc-thcas`
- `toxoplasma-sag1`

## Provenance Corrections Applied

The custom runner was not the problem for the blocked disease subset. The target inventory was.

Corrected accession map:

- `Leprae-MMP-I` `P09655` → `P46841`
- `Leprae-Ag85B` `Q49854` → `P31951`
- `Leprae-Hsp65` `P0A5B4` → `P09239`
- `Leprae-SOD` `O33085` → `P13367`
- `Leprae-Bacterioferritin` `P15917` → `P43315`
- `TB-Hsp65` `P9WMG9` → `P9WPE7`

These corrections were regenerated and verified against the fetched sequence descriptions.

Verified corrected subset:

- `Leprae-MMP-I` `P46841`
  `Type 2A encapsulin shell protein`
- `Leprae-Ag85B` `P31951`
  `Ag85B mycolyltransferase`
- `Leprae-Hsp65` `P09239`
  `Chaperonin GroEL 2`
- `Leprae-SOD` `P13367`
  `Superoxide dismutase [Mn]`
- `Leprae-Bacterioferritin` `P43315`
  `Bacterioferritin`
- `TB-Hsp65` `P9WPE7`
  `Chaperonin GroEL 2`

## Script Rot Also Found

`dev/scripts/proteins/play_leprosy.clj` had stale oracle-helper drift:

- `{:vocab :torah}` passed where `:torah` was required
- `(:path walk)` instead of `(:steps walk)`

Those helper issues are now patched.

## Operational Boundary

Current boundary:

- shared custom DNA pipeline: stronger
- valid custom artifact set: refreshable and now materially cleaner
- corrected leprosy/TB custom subset: rerun and verified

## Next Steps

1. Decide which of these custom artifacts should become tracked canonical outputs versus remaining exploratory sidecars.
2. If experiment `123` or `127` output narratives are regenerated, make sure they inherit the corrected accessions.
