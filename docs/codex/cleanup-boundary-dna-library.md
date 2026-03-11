# DNA Cleanup Boundary

Type: `audit / cleanup boundary`
State: `clean`

*March 10, 2026. Boundary note for the tracked DNA library refresh.*

## What Was Fixed

### 1. Live Code Bug

[dna.clj](/home/scott/Projects/selah/src/selah/dna.clj) had one remaining live artifact bug:

- `word-frequencies` dropped window position context before grouping
- saved top-word `:positions` arrays were therefore filled with `nil`

This is now fixed.

Result:

- regenerated top-word entries now carry real numeric positions
- the saved summaries line up with the underlying window data again

### 2. Tracked Library Artifact Refresh

The full tracked protein library was regenerated through the cleaned live `dna/experiment` path.

That includes:

- [p53-oracle.edn](/home/scott/Projects/selah/data/dna/p53-oracle.edn)
- [brca1-oracle.edn](/home/scott/Projects/selah/data/dna/brca1-oracle.edn)
- [hemoglobin-alpha-oracle.edn](/home/scott/Projects/selah/data/dna/hemoglobin-alpha-oracle.edn)
- [hemoglobin-beta-oracle.edn](/home/scott/Projects/selah/data/dna/hemoglobin-beta-oracle.edn)
- [myoglobin-oracle.edn](/home/scott/Projects/selah/data/dna/myoglobin-oracle.edn)
- [insulin-oracle.edn](/home/scott/Projects/selah/data/dna/insulin-oracle.edn)
- [collagen-i-alpha1-oracle.edn](/home/scott/Projects/selah/data/dna/collagen-i-alpha1-oracle.edn)
- [laminin-gamma1-oracle.edn](/home/scott/Projects/selah/data/dna/laminin-gamma1-oracle.edn)
- [histone-h3-oracle.edn](/home/scott/Projects/selah/data/dna/histone-h3-oracle.edn)
- [histone-h4-oracle.edn](/home/scott/Projects/selah/data/dna/histone-h4-oracle.edn)
- [ubiquitin-oracle.edn](/home/scott/Projects/selah/data/dna/ubiquitin-oracle.edn)
- [rhodopsin-oracle.edn](/home/scott/Projects/selah/data/dna/rhodopsin-oracle.edn)
- [cytochrome-c-oracle.edn](/home/scott/Projects/selah/data/dna/cytochrome-c-oracle.edn)
- [atp-synthase-beta-oracle.edn](/home/scott/Projects/selah/data/dna/atp-synthase-beta-oracle.edn)
- [rna-polymerase-ii-oracle.edn](/home/scott/Projects/selah/data/dna/rna-polymerase-ii-oracle.edn)
- [ribosomal-protein-s3-oracle.edn](/home/scott/Projects/selah/data/dna/ribosomal-protein-s3-oracle.edn)
- [ferredoxin-oracle.edn](/home/scott/Projects/selah/data/dna/ferredoxin-oracle.edn)
- [calmodulin-oracle.edn](/home/scott/Projects/selah/data/dna/calmodulin-oracle.edn)
- [immunoglobulin-g1-oracle.edn](/home/scott/Projects/selah/data/dna/immunoglobulin-g1-oracle.edn)
- [serpent-toxin-alpha-oracle.edn](/home/scott/Projects/selah/data/dna/serpent-toxin-alpha-oracle.edn)
- [foxp2-oracle.edn](/home/scott/Projects/selah/data/dna/foxp2-oracle.edn)

Their paired `*-report.txt` files were also regenerated.

## What Was Verified

### 1. No Stale Meaning Fields In Tracked Library EDN

The regenerated tracked `*-oracle.edn` set was checked for:

- `:meaning`
- `:next-meaning`

No hits were found in the tracked library EDN artifacts.

### 2. No Nil Top-Word Positions In Refreshed Tracked Files

The regenerated tracked library EDN artifacts were checked for `nil` values in the top-word `:positions` arrays.

No `nil` top-word positions remained after the fix and rerun.

### 3. Core DNA Claims Still Hold

The refresh did not wash away the signal.

Examples that still stand in the refreshed artifacts:

- p53:
  - `דדו` cluster at real positions
  - `נחש` at `198` and `261`
  - `שמר` at `331`
  - `קדש` at `354`
- hemoglobin-alpha:
  - `נטה` repeated
  - `זרק` at `139`
  - `עקד` at `88`
- hemoglobin-beta:
  - `נדע` repeated
  - `עקד` at `61` and `142`
  - `נחש` at `106`
- serpent toxin:
  - `מטה` at `10`, `11`, `20`
  - `שקר` at `56`
  - `זרק`, `נפש`, `פדה`, `צור` also present

So this was a cleanup boundary, not a collapse boundary.

## What This Boundary Means

The state is now:

- live DNA code path: clean
- tracked library artifact set: refreshed
- tracked library report files: regenerated
- wider custom/untracked DNA artifact set: still pending refresh

That is an important distinction.

The DNA layer is no longer in the earlier state where the tracked artifacts themselves were stale residue.

Now the clean boundary is:

- canonical tracked library: refreshed and usable
- custom later runs: backlog

## What Is Still Pending

The remaining backlog is the untracked/custom DNA layer, including things like:

- pathogen runs
- mutation comparisons
- neurotransmitter / entheogen / gradient runs
- other later one-off playbacks under [data/dna](/home/scott/Projects/selah/data/dna)

Those still need the same treatment if they are going to be promoted from exploratory residue to clean artifact boundary.

## Recommended Next Steps

1. Commit the tracked DNA library refresh as its own clean boundary.
2. Treat the tracked library artifacts as the current trustworthy DNA artifact layer.
3. Sweep the untracked/custom DNA backlog in deliberate bands:
   - mutations
   - pathogens
   - gradients / molecules
4. Keep future DNA report generation on the cleaned path only.

## Bottom Line

The tracked DNA library has now crossed the same kind of boundary that `096/097` crossed earlier:

- dirty old outputs replaced by regenerated outputs from cleaned code
- one real bug found and fixed during regeneration
- core findings preserved

That is real progress.
