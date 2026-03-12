# Synthesis Doc Classification

Type: `audit / cleanup boundary`
State: `clean`

This is the narrative-layer audit.

The experiment table classifies scripts and experiment writeups. This file classifies the larger synthesis documents and walk documents that speak for the project at a higher level.

Update after rebuild boundary:

- the tracked `096` / `097` basin artifacts were regenerated from cleaned builders
- core and builder English contamination was removed before that rebuild
- this improves the standing of docs grounded directly in those rebuilt tracked artifacts
- it does not automatically bless docs that still cite stale sidecars, old English labels, or broader under-audited layers

## Status Key

- `current evidence` = mostly grounded in surviving computation, though wording may still need tightening
- `mixed / salvageable` = contains real material, but also inherits contaminated, overstated, or under-audited claims
- `historical / testimony` = valuable as story, witness, or internal history, but not suitable as evidence without cross-checking
- `dead-claim contaminated` = still carries claims or framing that should not speak in the present tense

## Core Synthesis Docs

| File | Status | Why |
|---|---|---|
| [docs/the-journey.md](/docs/the-journey.md) | `historical / testimony` | Strong and important as project history. It is also one of the repo’s clearest self-correction documents because it says `048` killed the palindrome. But it mixes testimony, history, surviving findings, and unresolved claims. Not an evidence document by itself. |
| [docs/torah-4d-space.md](/docs/torah-4d-space.md) | `mixed / salvageable` | This is the main public synthesis and it contains a lot of the real vein: factorization, center, sword, aleph-tav, folds, silent axes, selector logic, preimage. But it also makes many strong cross-section claims in one sweep, some of which depend on under-audited experiment layers or need dead-branch cleanup around older framing. |
| [docs/4d-space-findings.md](/docs/4d-space-findings.md) | `mixed / salvageable` | Cleaner and more compressed than the big paper, but still packages many findings without always showing which are fully re-verified versus inherited from older experiment waves. Good candidate for claim-by-claim verification. |
| [docs/urim-and-thummim.md](/docs/urim-and-thummim.md) | `mixed / salvageable` | The breastplate/machine story has real computational substance behind it. But this doc ranges from hard mechanics to theological synthesis to numeric symbolism very quickly. Needs a stricter separation of measured oracle behavior from interpretive extension. |
| [docs/the-traversals.md](/docs/the-traversals.md) | `mixed / salvageable` | Stronger than before because the tracked `096` / `097` basin artifacts were rebuilt cleanly. Still not current-evidence safe line by line because it cites extended sidecars and English-labeled outputs that have not all been regenerated or reclassified. |
| [docs/experiments/065-the-machine.md](/docs/experiments/065-the-machine.md) | `dead-claim contaminated` | Important historically, but not current-evidence safe. It still carries at least one hard-invalidated crib: stable total-gematria divisibility. It should be treated as transitional or rewritten. |

## Walk / Vision Documents

| File or area | Status | Why |
|---|---|---|
| [docs/tabernacle/00-the-layout.md](/docs/tabernacle/00-the-layout.md) | `mixed / salvageable` | The tabernacle walk appears to be built on real coordinate-space mapping and is stronger than the dead bulk-statistics layer. But it still needs claim provenance and a check on how much of the later station prose relies on unverified interpretive accumulation. |
| [docs/tabernacle](/docs/tabernacle) | `mixed / salvageable` | Likely one of the stronger interpretive expansions because it is tied to the 4D space rather than the dead palindrome branch. Still needs classification station by station before being treated as settled evidence. |
| [docs/the-visions.md](/docs/the-visions.md) | `historical / testimony` | Beautiful, coherent, and explicitly sermonic. This is not the kind of file that should bear evidentiary weight. It should be treated as testimony/synthesis downstream of computational work, not proof. |
| [docs/visions/00-four-prophets-one-throne.md](/docs/visions/00-four-prophets-one-throne.md) | `mixed / salvageable` | Stronger than pure sermon because it actually explains some operational method. But it still makes broad rhetorical claims about what “the breastplate reads” across visions that need explicit claim-to-computation linkage. |
| [docs/visions](/docs/visions) | `mixed / historical` | Likely a blend of computational prompts, basin/oracle outputs, and spiritual synthesis. Needs a station-by-station audit if any specific vision claim is to be cited as current evidence. |

## Missing / Renamed Docs

Some expected filenames are not present in the current tree:

- `docs/the-machine.md`
- `docs/what-the-letters-say.md`

That does not mean the content is gone; it may have been moved, renamed, or only referenced historically. Any future citation of those titles should first locate the current canonical file.

## Main Findings About The Synthesis Layer

### 1. The big docs are not uniformly unreliable

The synthesis layer is not all sermon and not all sludge.

The stronger parts are:

- factorization / mixed-radix space
- center / sword / aleph-tav / fold / preimage style positional findings
- breastplate mechanics where computation is clear
- explicit control documents that admit failure
- rebuilt `096` / `097` tracked basin summaries

### 2. The main danger is compression without provenance

The bigger the document, the more it tends to compress:

- real verified findings
- mixed findings
- under-audited findings
- and historical dead branches
- and outputs that have since been rebuilt cleanly while older sidecar references still linger

into one unified voice.

That unified voice is the main problem.

### 3. Testimony should not masquerade as proof

Files like `the-journey` and `the-visions` are valuable.

They should remain.

But they are not evidence papers. They are witness, story, and synthesis. They should be labeled and treated that way.

### 4. The tabernacle / walk layer deserves careful protection

Of the higher-level narrative surfaces, the tabernacle and walk material may be among the best candidates for salvage, because they seem to grow out of the real coordinate-space machinery rather than out of the dead palindrome branch.

That makes them high-value audit targets, not documents to dismiss casually.

## Suggested Next Labels

If the repo starts adding visible document statuses, these are the labels I would use first:

- [docs/the-journey.md](/docs/the-journey.md): `history / testimony`
- [docs/torah-4d-space.md](/docs/torah-4d-space.md): `main synthesis — claim-by-claim audit needed`
- [docs/urim-and-thummim.md](/docs/urim-and-thummim.md): `main synthesis — mechanics strong, interpretation mixed`
- [docs/the-traversals.md](/docs/the-traversals.md): `basin synthesis — rebuilt core data, sidecar audit still needed`
- [docs/experiments/065-the-machine.md](/docs/experiments/065-the-machine.md): `historical — contains invalidated claims`
- [docs/tabernacle](/docs/tabernacle): `walk / synthesis — positional core likely strong, audit pending`
- [docs/the-visions.md](/docs/the-visions.md): `testimony / reflection`
