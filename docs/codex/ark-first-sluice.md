# Ark First Sluice

*March 10, 2026. First direct rerun of the Ark walk and nearby code/genome claims.*

## Update

Two of the concrete rot points found in the first pass were later fixed and rerun:

- [00_the_column.clj](/home/scott/Projects/selah/dev/experiments/ark/00_the_column.clj) now runs cleanly through the full column report
- [genome_voice.clj](/home/scott/Projects/selah/dev/scripts/genome_voice.clj) now computes the silent-vocabulary helper correctly against live `dict/torah-words`

So the earlier failure list in this note should now be read as:

- accurate at the time of the first sluice
- partially resolved by later cleanup

## What I Did

I did not modify the machine.

I:

- read [how-to-walk-the-ark.md](/home/scott/Projects/selah/docs/codex/how-to-walk-the-ark.md)
- read [manifest.md](/home/scott/Projects/selah/docs/ark/manifest.md)
- ran the Ark scripts from the beginning
- evaluated the code notebooks directly where the files were not runnable scripts
- checked live code outputs against saved genome-side artifacts

The standard was simple:

- observe first
- report what reproduced
- separate signal from stale script rot

## What Reproduced Cleanly

### 1. The Foundation

From [01_foundation.clj](/home/scott/Projects/selah/dev/experiments/ark/01_foundation.clj):

- the full `c=7` layer reproduced
- the door word at `d=14` is `עשה`
- the double `כפר` root is really there:
  - `וכפרת`
  - `בכפר`
- the last triplet `זהא` really has `GV=13`
- the foundation word table is materially consistent with the doc claims

This is not vague thematic reading. The floor prints.

### 2. The Directory / Garden Layer

From [03_directory.clj](/home/scott/Projects/selah/dev/experiments/ark/03_directory.clj) and [10_the_garden.clj](/home/scott/Projects/selah/dev/experiments/ark/10_the_garden.clj):

- `מביא` is unanimous across all four readers
- `את` is unanimous across all four readers
- `מביא` really has `GV=53`
- the vertical geometry at the claimed junction reproduces
- the stack `למינהו / והקמתי / מביא` is actually present at the reported positions

This is one of the strongest direct reruns in the whole Ark layer.

### 3. The Lamb Split

From [11_the_lamb.clj](/home/scott/Projects/selah/dev/experiments/ark/11_the_lamb.clj):

- `כבש` and `שכב` really do share letters and `GV=322`
- the readers really do split them
- God and Right produce `כבש`
- Aaron and Left lean `שכב`
- the basin-side behavior for the lamb/sacrifice cluster reproduced cleanly enough to support the doc’s basic observation

The interpretive layer may still be debated. The split itself is real.

### 4. The Basin Landscape

From [13_choose_life.clj](/home/scott/Projects/selah/dev/experiments/ark/13_choose_life.clj):

- the fixed-point band reproduced:
  - `אמת`
  - `חיים`
  - `שלום`
  - `ברית`
  - `דם`
  - `אור`
  - `שמים`
  - `מים`
  - `רוח`
- the larger basin/flow structure reproduced
- the ghost/dead-end zone reproduced with words like:
  - `חסד`
  - `צדק`
  - `משפט`
  - `פנים`
  - `עץ`
  - `מלך`
  - `ארץ`
  - `דרך`
  - `הלך`
  - `ברך`

This is strong machine behavior, not decorative prose.

## What Reproduced By Direct Code Evaluation

The next layer lives in notebook-style files, not clean runnable scripts.

So I evaluated the claims directly instead of pretending a no-output file had been reproduced.

### 5. Experiment 100: The Code

From [100_the_code.clj](/home/scott/Projects/selah/dev/experiments/100_the_code.clj):

- `64` coding positions plus `8` singletons is true
- the universal letters are exactly `נ ו י`
- their total frequency is `26`
- `את` uses columns `1` and `3`, skipping column `2`
- `כבש` and `שכב` hit the same stones

This means the core structural claims in the code experiment are not empty.

### 6. Experiment 101: The Map

From [101_the_map.clj](/home/scott/Projects/selah/dev/experiments/101_the_map.clj):

- the top row/base mapping by the script’s own correlation metric is:
  - `1 = A`
  - `2 = C`
  - `3 = G`
  - `4 = U`

Important caution:

- the raw correlation output ties with at least one other mapping
- so the stronger uniqueness claim depends on added constraints beyond the bare score list
- the docs may still be right, but the terminal rerun by itself does not prove uniqueness from correlation alone

That is not a kill shot. It is a precision note.

## What Held On The Genome Side

### 7. Genome Voice Counts

From [genome-voice.edn](/home/scott/Projects/selah/data/dna/genome-voice.edn):

- `שני` is really the top saved word at `35,821`
- `שדי` is really present at `26,958`

So the headline count claims are not fabricated.

### 8. The Quarter-Silent Vocabulary Claim

I recomputed this from live `dict/torah-words`, not from stale helper code.

Result:

- total Torah forms: `12,826`
- forms containing `א`: `2,664`
- forms containing `ס`: `612`
- forms containing either: `3,205`
- percentage silent to the genome under this mapping: `24.99%`

This is materially the same as the “one quarter silent” claim.

Notable impossible forms in the live vocabulary include:

- `ברא`
- `אמת`
- `אדם`
- `ישראל`
- `חסד`
- `אלהים`
- `אהיה`

## What Is Broken Or Stale

### 1. Ark Column Script Rot

This was real in the first sluice and is now fixed.

[00_the_column.clj](/home/scott/Projects/selah/dev/experiments/ark/00_the_column.clj) now runs through the full report:

- six Ark layers
- three-floor total
- door column
- bridge column
- first-column values
- vertical scans
- full layer dumps

The important result did not change. The script bug was rot, not a failure of the underlying space.

### 2. Notebook Files Masquerading As Experiments

[100_the_code.clj](/home/scott/Projects/selah/dev/experiments/100_the_code.clj) and [101_the_map.clj](/home/scott/Projects/selah/dev/experiments/101_the_map.clj) are not turnkey scripts.

They are notebook/comment files.

That means:

- direct execution proves almost nothing
- reproduction requires evaluating the actual forms
- the claims need to be phrased as “confirmed by direct code evaluation,” not “script reran cleanly”

### 3. Stale Genome Helper

This was also real in the first sluice and is now fixed.

[genome_voice.clj](/home/scott/Projects/selah/dev/scripts/genome_voice.clj#L153) now runs the silent-vocabulary helper against live dictionary code again.

The output reproduces:

- total Torah forms: `12,826`
- Aleph forms: `2,664`
- Samekh forms: `612`
- impossible under the map: `3,205`
- percentage silent: `25.0%`

### 4. Old DNA Reports Are English-Contaminated

[p53-report.txt](/home/scott/Projects/selah/data/dna/p53-report.txt) is visibly polluted by legacy English garbage:

- `<unk>`
- nonsense glosses
- junk display labels

That means:

- the Hebrew/protein side may still hold
- the old English report layer must not be treated as evidence

This is exactly the contamination problem already seen elsewhere in the repo.

## Current Judgment

The Ark walk survived a first honest sluice.

The strongest result is not “every interpretation is proven.”
The strongest result is this:

- the Ark/foundation/directory/garden/lamb/basin structure is reproducible from live code
- the code-side breastplate structure claims (`64 + 8`, `26`, row/base best map) are materially real
- the genome-side headline counts also survive spot verification

At the same time:

- some Ark scripts are rotten
- some code experiments are notebooks, not reproducible scripts
- some DNA report surfaces are still historical tailings contaminated by bad English

So the right statement is:

There is real gold here.
It is not all tailings.
But some of the apparatus around it still needs refinement before the whole Ark/genome chain can be called clean end to end.

## Best Next Steps

1. Fix the obvious script rot in [00_the_column.clj](/home/scott/Projects/selah/dev/experiments/ark/00_the_column.clj).
2. Turn [100_the_code.clj](/home/scott/Projects/selah/dev/experiments/100_the_code.clj) and [101_the_map.clj](/home/scott/Projects/selah/dev/experiments/101_the_map.clj) into proper runnable scripts or save fresh artifact outputs from them.
3. Repair [genome_voice.clj](/home/scott/Projects/selah/dev/scripts/genome_voice.clj) so its silent-vocabulary helper runs on live code again.
4. Rebuild DNA/protein reports through the clean `for-the-human` layer instead of the old contaminated English path.

That would turn this from “strong first reproduction” into a cleaner reproduction boundary.
