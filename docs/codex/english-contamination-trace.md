# English Contamination Trace

Working ledger for where English glosses enter the system, especially where machine-generated English from `data/torah-english.edn` leaks into evidence paths.

## Rule

- Curated English can be tolerated as operator/UI aid.
- Machine English from `torah-english.edn` must not drive evidence, rankings, summaries, or interpretive claims.

## Source Of Contamination

Primary source:

- [src/selah/dict.clj](/home/scott/Projects/selah/src/selah/dict.clj)

Key function:

- `translate-english`

Behavior:

- tries curated `dict/translate` first
- falls back to machine-translated `data/torah-english.edn`

That means any call to `translate-english` is contaminated unless it is explicitly UI-only.

## Classification Key

- `ui-only` = display convenience, not evidence
- `mixed` = used in output structures that may be read as evidence
- `evidence-contaminating` = directly shapes analysis, summaries, rankings, or saved data likely to be cited

## Core Code Paths

| File | Line / area | Use | Status | Why |
|---|---|---|---|---|
| [src/selah/dict.clj](/home/scott/Projects/selah/src/selah/dict.clj) | `translate-english` | fallback gloss provider | `source` | This is the contamination source itself |
| [src/selah/oracle.clj](/home/scott/Projects/selah/src/selah/oracle.clj#L204) | `forward` sets `:meaning (or (dict/translate w) (dict/translate-english w))` | labels returned words | `evidence-contaminating` | `forward` output feeds experiments and higher-level summaries |
| [src/selah/oracle.clj](/home/scott/Projects/selah/src/selah/oracle.clj#L709) | `rank-phrases` builds `english-cache` from `translate-english` | phrase ranking / display context | `evidence-contaminating` | This is not just passive display; it affects ranked phrase presentation |
| [src/selah/basin.clj](/home/scott/Projects/selah/src/selah/basin.clj#L36) | `step` uses machine fallback for `:meaning` and `:next-meaning` | walk steps | `evidence-contaminating` | Basin step objects are analysis objects, not pure UI |
| [src/selah/basin.clj](/home/scott/Projects/selah/src/selah/basin.clj#L126) | `landscape` assigns attractor `:meaning` via machine fallback | attractor summaries | `evidence-contaminating` | Attractor summaries are saved and cited downstream |
| [src/selah/sweep.clj](/home/scott/Projects/selah/src/selah/sweep.clj#L80) | exposes `:meaning` from sweep artifacts | analysis surface | `mixed` | If source artifacts are contaminated, the sweep surface republishes them |
| [src/selah/explorer/ui.clj](/home/scott/Projects/selah/src/selah/explorer/ui.clj) | renders `:meanings` / English text | UI rendering | `ui-only` | Acceptable if clearly marked as glosses and not reused as evidence |
| [src/selah/explorer/sweep_ui.clj](/home/scott/Projects/selah/src/selah/explorer/sweep_ui.clj#L223) | regex-categorizes words by English `:meaning` | categorization in sweep UI | `mixed` | Probably UI, but category labels can easily be mistaken for analysis |
| [src/selah/dna.clj](/home/scott/Projects/selah/src/selah/dna.clj) | displays `:meaning` of top oracle hits | DNA experiment/reporting surface | `mixed` | Depends whether output is exploratory UI or cited result |

## Experiment Builders That Save Contaminated Meanings

| File | Line / area | Use | Status | Why |
|---|---|---|---|---|
| [dev/experiments/096_basin_classification.clj](/home/scott/Projects/selah/dev/experiments/096_basin_classification.clj#L154) | repeated `or (dict/translate w) (dict/translate-english w)` | saved attractor/word-index/cycle data | `evidence-contaminating` | This writes contaminated meanings into canonical experiment artifacts |
| [dev/experiments/097_per_head_basins.clj](/home/scott/Projects/selah/dev/experiments/097_per_head_basins.clj#L111) | repeated machine fallback for meanings | per-head basin artifacts | `evidence-contaminating` | Same issue as 096, but in per-head form |
| [dev/experiments/094_thummim_sweep.clj](/home/scott/Projects/selah/dev/experiments/094_thummim_sweep.clj) | uses curated `dict/translate` only | sweep artifact labels | `mixed but cleaner` | Less contaminated because it avoids machine fallback in the visible path |

## Passage Experiments / DNA Experiments Using `:meaning`

These are less foundational than 096/097, but they still carry risk because they present machine-English as if it were semantic output.

| File band | Use | Status | Why |
|---|---|---|---|
| [dev/experiments/105_the_four_bloods.clj](/home/scott/Projects/selah/dev/experiments/105_the_four_bloods.clj) through [dev/experiments/127_pathogen_prescriptions.clj](/home/scott/Projects/selah/dev/experiments/127_pathogen_prescriptions.clj) | top-word `:meaning` printed in reports | `mixed` | Mostly presentation, but the prose can inherit false semantic confidence |
| [dev/experiments/120_the_burning_bush.clj](/home/scott/Projects/selah/dev/experiments/120_the_burning_bush.clj) and [dev/experiments/124_the_body_coordinates.clj](/home/scott/Projects/selah/dev/experiments/124_the_body_coordinates.clj) | protein/body outputs with `:meaning` | `mixed` | Exposed in saved output and docs |
| [dev/scripts/proteins/play_leprosy.clj](/home/scott/Projects/selah/dev/scripts/proteins/play_leprosy.clj) and related protein scripts | operator analysis with `:meaning` | `mixed` | Fine for operator exploration, not fine as evidence without fencing |

## UI / Operator-Only Candidates

These are probably acceptable if clearly marked as glosses:

| File | Use | Status | Note |
|---|---|---|---|
| [src/selah/explorer/ui.clj](/home/scott/Projects/selah/src/selah/explorer/ui.clj) | phrase and word display | `ui-only` | Keep, but rename labels if needed |
| [src/selah/explorer/sweep_ui.clj](/home/scott/Projects/selah/src/selah/explorer/sweep_ui.clj) | sweep browser | `ui-only / mixed` | UI is fine; regex categorization by English should be treated cautiously |
| [src/selah/mcp/tools.clj](/home/scott/Projects/selah/src/selah/mcp/tools.clj) | prints meanings in MCP output | `mixed` | MCP output often becomes evidence in practice, so this may need stricter wording |

## Main Problems

### 1. `:meaning` is an overloaded field

The code often stores machine-English under `:meaning`.

That is a bad name for this layer.

It should be something like:

- `:gloss`
- `:english-gloss`
- `:ui-gloss`

Calling it `:meaning` invites interpretive misuse.

### 2. Canonical artifacts are already contaminated

The worst issue is not just UI.

The worst issue is that experiments `096` and `097` appear to save contaminated meanings into artifacts that later code and docs treat as authoritative.

### 3. Ranking and grouping can inherit the contamination

Once machine-English enters `forward`, `rank-phrases`, basin summaries, or sweep displays, it can influence:

- what humans notice first
- how results are grouped
- what gets quoted in docs
- which patterns feel coherent

That is enough to contaminate interpretation even if the underlying Hebrew computation is sound.

## Recommended Cleanup

1. Remove `translate-english` from experiment builders and saved analysis artifacts.
2. Reserve machine English for explicit UI/operator surfaces only.
3. Rename `:meaning` fields where they can contain machine glosses.
4. Mark any saved artifact built with machine fallback as contaminated until regenerated.
5. Audit docs that quote or summarize basin/sweep meanings as if they were semantic evidence.

## Highest Priority Targets

- [src/selah/oracle.clj](/home/scott/Projects/selah/src/selah/oracle.clj)
- [src/selah/basin.clj](/home/scott/Projects/selah/src/selah/basin.clj)
- [dev/experiments/096_basin_classification.clj](/home/scott/Projects/selah/dev/experiments/096_basin_classification.clj)
- [dev/experiments/097_per_head_basins.clj](/home/scott/Projects/selah/dev/experiments/097_per_head_basins.clj)
- [src/selah/explorer/sweep_ui.clj](/home/scott/Projects/selah/src/selah/explorer/sweep_ui.clj)

