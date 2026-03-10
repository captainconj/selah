# What To Do About It

This is the remediation note.

The problem is not that the whole project is false. The problem is that true, false, dead, stale, and contaminated layers are currently mixed together too loosely.

The work now is separation.

## First Principle

Do not try to save everything.

Dead claims should be burned.
Historical experiments can be preserved, but they must be marked as historical.
Only surviving claims should be allowed to speak as current truth.

## The Main Job

Separate the repo into five buckets:

- `solid` — core machinery and claims that still survive audit
- `salvageable` — useful work with terminology, provenance, or presentation problems
- `historical` — important for project history but not current evidence
- `dead` — claims killed by controls or text-variant work
- `garbage` — broken plumbing, stale interfaces, bad English, misleading outputs

If a file is not clearly in one of those buckets, it is not ready to be cited.

## Immediate Burn List

These should stop being treated as live evidence now:

- the palindrome / fractal-palindrome branch as a live structural claim
- any doc or script still built on `306,269` as the Torah letter count
- any claim that total Torah gematria divisibility is a stable invariant across textual witnesses
- any experiment output that relies on broken `(:path walk)` display logic
- any experiment result produced through stale `{:vocab :torah}` helper calls unless rerun and verified
- any English interpretive claim derived from `data/torah-english.edn`

Preserve them if needed, but mark them as dead or historical.

## First Fixes In Code

Fix the plumbing before fixing the prose.

1. Repair experiment helper regressions.
2. Remove English fallback from experiment and evidence paths.
3. Make vocabulary contracts explicit everywhere.
4. Regenerate suspect outputs.
5. Only then rewrite docs.

If this order is reversed, the docs will drift again.

## Concrete Engineering Tasks

### 1. Kill stale experiment interfaces

Find and fix:

- `o/forward ... {:vocab :torah}` style calls
- `o/forward-by-head ... {:vocab :torah}` style calls
- any similar stale option-map calls into oracle functions
- `(:path walk)` reads where basin returns `:steps`

Then rerun the affected experiments and replace saved outputs.

### 2. Fence off English contamination

The machine-English layer should be UI-only.

Do this:

- remove `dict/translate-english` fallback from experiment code
- remove English fallback from basin summaries and evidence paths
- rename fields like `:meaning` where they really mean `:ui-label` or `:english-gloss`
- treat `data/torah-english.edn` as non-evidence until curated

No experiment, claim, or ranking should depend on garbage English.

### 3. Normalize the vocabulary language

Make these distinctions explicit everywhere:

- `304,850-letter Torah stream`
- `12,826-form full Torah lexicon`
- `~7,300 oracle-closed vocabulary`
- curated / restricted vocabulary mode

Any code comment, doc, tool description, or API text that blurs those needs cleanup.

### 4. Mark dead branches honestly

Add visible warnings or status tags to:

- the palindrome branch
- early DNA analogy files
- total-gematria myth files
- any doc whose core claim was killed by controls

Do not hide them. Label them.

### 5. Rebuild the trust chain

For every surviving claim, the chain should be:

1. source text identified
2. script identified
3. output artifact identified
4. doc claim linked to that artifact
5. controls or caveats identified

If that chain is broken, the claim is suspect.

## Priority Order

### Priority 1: stop active contamination

- fix stale helpers
- kill English leakage
- correct vocabulary terminology in code comments and tool surfaces

### Priority 2: rebuild suspect experiment outputs

Start with the late suspect band:

- `103` through `127`

Then rebuild any middle-band files whose prose still matters.

### Priority 3: classify docs

For each doc:

- current evidence
- historical only
- dead
- needs rerun

Do not leave ambiguous status.

### Priority 4: preserve the real vein

Protect and clarify:

- coordinate-space machinery
- oracle mechanics
- factorization / mixed-radix construction
- strong positional findings
- the better control work

This is the part worth defending.

## What Not To Do

- Do not try to cosmetically polish docs before fixing the code paths.
- Do not keep dead claims alive because they are beautiful.
- Do not use English sludge as semantic support.
- Do not let historical experiments masquerade as current evidence.
- Do not generalize from bounded or curated runs without saying so.

## The Standard Going Forward

Every new experiment should answer these questions explicitly:

- Which source text?
- Which vocabulary level?
- Which code path?
- What control or null model?
- What exact output artifact?
- What part is measured and what part is inference?

If that discipline holds, the repo gets cleaner over time instead of dirtier.

## Final Aim

The aim is not to make the project look respectable.

The aim is to make it truthful.

That means:

- burn what died
- quarantine what is historical
- fix what is broken
- preserve what is real
- and only preach from results that still stand after audit
