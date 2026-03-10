# Library Doc Tightening

Date: 2026-03-10

This note records what still needs tightening in `docs/ark/31-the-library-so-far.md` after checking it against rebuilt DNA artifacts.

## Main Finding

The document contains a lot of real signal, but the summary table is still smoothing the artifact layer.

The problem is not that the whole document is false.
The problem is that the table sometimes presents a curated or interpretive summary where the rebuilt artifact now gives a more exact statement.

## Confirmed Table Drift

From direct checks against the rebuilt artifact files:

- `p53`
  - residues `393`
  - windows `179`
  - top word `דדו ×9`
  - table is aligned here

- `Serpent-toxin-alpha`
  - residues `95`
  - windows `30`
  - top word `מטה ×3`
  - table is aligned here

- `Hemoglobin-alpha`
  - residues `142`
  - windows `50`
  - top word `נטה ×4`
  - table is aligned here

- `Hemoglobin-beta`
  - residues `147`
  - windows `54`
  - top word `נדע ×4`
  - table is aligned here

- `Myoglobin`
  - residues `154`
  - windows `59`
  - actual top word `שוע ×3`
  - current table says `— (dispersed)`

- `Human-Insulin`
  - residues `110`
  - windows `34`
  - actual top word `שני ×2`
  - current table says `— (dispersed)`

- `Cytochrome-c`
  - residues `105`
  - windows `43`
  - top word `קשה ×3`
  - table is aligned here

- `Ferredoxin`
  - residues `184`
  - windows `71`
  - top word `ירד ×4`
  - table is aligned here

- `Ubiquitin`
  - residues `685`
  - windows `260`
  - actual top word `קשה ×18`
  - current table says `(×9 repeats)`

- `Histone-H4`
  - residues `103`
  - windows `36`
  - top word `שרש ×2`
  - table is aligned here

- `Rhodopsin`
  - residues `348`
  - windows `123`
  - actual top word `זנה ×4`
  - current table says `— (diverse)`

- `Calmodulin`
  - residues `149`
  - windows `50`
  - top word `תלד ×3`
  - table is aligned here

- `ATP-synthase-beta`
  - residues `529`
  - actual readable windows `184`
  - current table says `185`
  - top word `נטה ×5`

- `Histone-H3`
  - residues `136`
  - actual readable windows `51`
  - current table says `52`
  - actual top word `נרד ×2`
  - current table says `רדה ×2`

- `Ribosomal-protein-S3`
  - residues `243`
  - actual readable windows `78`
  - current table says `80`
  - top word `הוה ×3`

The remaining rows should also be checked against the exact artifact filenames used by the library.
One direct pass failed on `immunoglobulin-gamma-oracle.edn` because the filename in the check script did not match the current artifact name.

## Stronger Than The Table

Some specific claims in the body still look strong under direct spot checks:

- `p53`
  - `דדו ×9`
  - `נחש ×2`

- `Serpent toxin`
  - `מטה ×3`
  - `שקר ×1`
  - `נפש ×1`
  - `פדה ×1`

- `Hemoglobin-alpha`
  - `נטה ×4`
  - `שדי ×1`
  - `זרק ×1`
  - `עקד ×1`

- `Hemoglobin-beta`
  - `נדע ×4`
  - `נחש ×1`
  - `עקד ×2`

- `FOXP2`
  - `מלל ×2`
  - `הוה ×4`
  - `צוה ×4`
  - `שני ×5`

So the problem is not emptiness.
It is compression and drift.

## What Should Change

### Summary table

- Make the dominant-word column match the artifact exactly.
- Make the windows column match the artifact exactly.
- If a row is meant to summarize a repeated-pattern story instead of the literal top word, say that in a separate column or note, not in the dominant-word field.

### Self-reference column

- Keep it, but make it explicit that this is a highlighted motif, not necessarily the top artifact word.

### Key words column

- Treat these as highlighted motifs with operator gloss, not as if they were raw artifact fields.

### Body sections

- Keep the stronger prose sections, but make sure each one follows a visible pattern:
  1. measured claim
  2. artifact anchor
  3. interpretation

## Bottom Line

`31-the-library-so-far.md` still has real gold in it.

But unlike `33-the-genome.md`, its summary table has not yet been tightened enough to the rebuilt artifact layer.

That is the next obvious cleanup if this doc is going to stand as a trustworthy bridge between data and witness.

## Update

The summary table in `docs/ark/31-the-library-so-far.md` has now been tightened to the rebuilt artifact layer:

- dominant-word entries now reflect the literal top artifact hit
- windows counts now match `:windows-with-readings`
- the table explicitly distinguishes raw artifact fields from highlighted motifs

The body sections still carry interpretation, but the table is no longer smoothing over the rebuilt data.
