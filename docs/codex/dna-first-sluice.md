# DNA First Sluice

Type: `audit / cleanup boundary`
State: `clean`

*March 10, 2026. First direct rerun of the DNA playback layer after the initial Ark sluice.*

## Update

After the first pass, I regenerated the full tracked library artifact set through the cleaned live `dna/experiment` path and fixed one remaining live bug in [dna.clj](/home/scott/Projects/selah/src/selah/dna.clj):

- `word-frequencies` had been dropping window position context
- this caused saved top-word `:positions` arrays to fill with `nil`
- the function now preserves the originating positions correctly

The following tracked library artifacts were regenerated cleanly:

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

Verification after regeneration:

- no `:meaning` / `:next-meaning` hits in the tracked `*-oracle.edn` set
- no `nil` top-word `:positions` in the refreshed tracked files

## What I Checked

I used the same standard as the Ark pass:

- rerun live code where possible
- use local data before trusting old text reports
- separate Hebrew/data from contaminated display surfaces
- note exactly what is clean, mixed, or stale

The main local checkpoints were:

- [how-to-play-dna.md](/home/scott/Projects/selah/docs/codex/how-to-play-dna.md)
- [p53_guardian.clj](/home/scott/Projects/selah/dev/experiments/dna/p53_guardian.clj)
- [genome_voice.clj](/home/scott/Projects/selah/dev/scripts/genome_voice.clj)
- saved oracle artifacts in [data/dna](/home/scott/Projects/selah/data/dna)

## What Reproduced Cleanly

### 1. The Silent Quarter

The repaired helper in [genome_voice.clj](/home/scott/Projects/selah/dev/scripts/genome_voice.clj) now runs again on live code.

It reproduces:

- total Torah forms: `12,826`
- forms containing `א`: `2,664`
- forms containing `ס`: `612`
- forms containing either: `3,205`
- impossible percentage: `25.0%`

Notable impossible forms printed by the live helper:

- `אדם`
- `אמת`
- `ברא`
- `אלהים`
- `אהיה`
- `חסד`
- `ישראל`

So the “silent quarter” / “breath gap” claim survives cleanly.

### 2. p53 Playback

From a fresh run of [p53_guardian.clj](/home/scott/Projects/selah/dev/experiments/dna/p53_guardian.clj):

- `393` residues -> `393` Hebrew letters
- `GV = 25,667`
- `179 / 391` trigram windows produce oracle readings

The strong local findings really appear in the fresh output:

- the `David` cluster is real:
  - `דדו` is the top repeated word with `9` hits
  - the dense region is at positions `68, 73, 75, 76, 77, 81, 82, 83, 85`
- `נחש` appears twice:
  - position `198`
  - position `261`
- `שמר` appears at position `331`
- `קדש` appears at position `354`
- `יהי` appears at positions `311` and `375`

Those are not copied from prose. They came back in the live rerun.

The saved [p53-oracle.edn](/home/scott/Projects/selah/data/dna/p53-oracle.edn) is also structurally clean now at the top-word/window level:

- top words no longer carry stale `:meaning`
- `all-windows` entries are Hebrew/data only
- top-word `:positions` are now real numeric positions again

### 3. Genome Voice Headline Counts

From [genome-voice.edn](/home/scott/Projects/selah/data/dna/genome-voice.edn):

- `שני` is still the top saved word at `35,821`
- `שדי` is still present at `26,958`

So the headline genome-voice counts still hold.

## What Survived Spot Checks In Saved DNA Artifacts

### 4. The Serpent

From regenerated [serpent-toxin-alpha-oracle.edn](/home/scott/Projects/selah/data/dna/serpent-toxin-alpha-oracle.edn):

- `מטה` appears at positions:
  - `10`
  - `11`
  - `20`
- `שקר` appears at position:
  - `56`
- the artifact also contains:
  - `זרק`
  - `להט`
  - `נפש`
  - `פדה`
  - `צור`

So the broad serpent summary survives a first spot check.

### 5. The Blood

From regenerated local oracle artifacts:

[hemoglobin-alpha-oracle.edn](/home/scott/Projects/selah/data/dna/hemoglobin-alpha-oracle.edn)

- `נטה` is the top repeated word with count `4`
- `זרק` appears at position `139`
- `עקד` appears at position `88`
- no `נחש` found in the saved top-word windows

[hemoglobin-beta-oracle.edn](/home/scott/Projects/selah/data/dna/hemoglobin-beta-oracle.edn)

- `נדע` is the top repeated word with count `4`
- `עקד` appears at positions `61` and `142`
- `נחש` appears at position `106`

[myoglobin-oracle.edn](/home/scott/Projects/selah/data/dna/myoglobin-oracle.edn)

- did not reproduce `זרק`, `עקד`, or `נחש` in the saved top-word windows I checked

So the blood claims look directionally real, but not every secondary claim was checked live in this pass.

## What Is Mixed Or Dirty

### 1. Saved DNA Artifacts Are Not Uniformly Clean

The DNA layer is in a better state than it was in the first pass.

The tracked library files are now structurally cleaner:

- the tracked library artifact set under [data/dna](/home/scott/Projects/selah/data/dna)

But many other saved artifacts in `data/dna/` still need the same refresh pass and may still show older patterns:

- stale `:meaning` fields
- old report text using junk English

That means the live DNA path and the tracked library boundary are now much cleaner than the wider untracked artifact backlog.

### 2. Old Text Reports Are Historical, Not Clean Evidence

The old report files in [data/dna](/home/scott/Projects/selah/data/dna):

- contain legacy English contamination
- often reflect earlier tool surfaces
- should not be treated as the clean evidence boundary

This includes files like:

- [p53-report.txt](/home/scott/Projects/selah/data/dna/p53-report.txt)
- [serpent-toxin-alpha-report.txt](/home/scott/Projects/selah/data/dna/serpent-toxin-alpha-report.txt)

They are useful as history, not as purified final artifacts.

### 3. Some Experiment Paths Are Still Network-Shaped

Some DNA experiment files still fetch by accession at runtime.

That is not a falsity problem, but it is a reproducibility problem:

- a local rerun may depend on cache or network
- a saved artifact may be easier to inspect than the original script

So the DNA layer still needs a more explicit “local artifact first” reproduction path.

## Current Judgment

The DNA side survived the first sluice better than I expected.

The strongest things now standing are:

- the silent quarter
- p53’s live rerun
- the saved genome-voice headline counts
- spot-checked serpent and blood motifs in local artifacts

The main weakness is not that the readings disappear.
The main weakness is remaining artifact backlog:

- the tracked library artifacts have now been cleaned by regeneration
- many untracked/custom DNA artifacts still need the same pass
- old report files outside the regenerated set should still be treated carefully

So the right summary is:

The DNA playback layer appears materially real.
The live path is clean enough to regenerate trustworthy artifacts.
The tracked library artifact set is now refreshed.
But the wider saved DNA artifact library is not yet uniformly refreshed.

## Best Next Steps

1. Continue the regeneration pass across the remaining untracked/custom DNA oracle artifacts.
2. Stop treating old `*-report.txt` files as evidence.
3. Route future human-readable DNA reports through the clean presentation layer.
4. Build a small reproducibility table for each major protein:
   - source
   - residues
   - Hebrew length
   - windows with readings
   - top repeated words
   - notable positions
   - artifact cleanliness status

That would turn the DNA layer from “promising and partly reproduced” into a cleaner, more durable boundary.
