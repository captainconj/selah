# What Codex Is Seeing

This is a working note after reading the project, not a verdict.

## Ground Truth From Scott

These clarifications supersede earlier ambiguity in the repo and should be treated as the intended model during audit:

- The 304,850-letter core should be grounded in the Leningrad/WLC stream.
- The full Torah lexicon is 12,826 unique word forms.
- The oracle is closed over a smaller vocabulary of roughly 7,300.
- The curated vocabulary is an older or explicitly restricted mode; it is acceptable only when chosen on purpose, not by accident or regression.
- The machine-generated English layer is for operator convenience, not for evidentiary or interpretive claims.

With that frame, some earlier “contradictions” are really documentation failures, while others are still genuine regressions or nonsense.

## The Project Shape

Selah is a Clojure research codebase built around a few central layers:

- `selah.text.*` ingests and normalizes source texts.
- `selah.space.coords` builds the 304,850-letter 4D coordinate system and the low-level access patterns over it.
- `selah.oracle` models the breastplate grid, illumination sets, reader traversals, and phrase-generation machinery.
- `selah.basin` feeds oracle outputs back into themselves to classify fixed points, transients, cycles, and attractors.
- `selah.explorer`, `selah.explorer.routes`, and `selah.explorer.ui` expose the system through an HTTP explorer.
- `selah.mcp.*` exposes a smaller tool surface over a socket protocol.
- `dev/experiments/*` contains passage-specific or hypothesis-specific scripts.
- `docs/experiments/*` and other docs turn those scripts and outputs into narrative claims.

The code is not just documentation support. There is a real computational substrate in `src/selah/space/coords.clj` and `src/selah/oracle.clj`. That is the spine of the project.

## The Research Workflow

The repo appears to operate in a repeated loop:

1. Identify a passage or hypothesis in `docs/planning/*`.
2. Write or adapt an experiment script in `dev/experiments/*`.
3. Run it and save generated artifacts in `data/experiments/*`.
4. Promote the result into a polished writeup in `docs/experiments/*` or a larger synthesis doc.

The planning docs make this explicit. `docs/planning/the-next-sluice.md` and `docs/planning/the-five-veins.md` are not archival notes; they read like active marching orders for the next wave of experiments.

## The Text Story

There are multiple text-source paths in the repo:

- `selah.text.oshb` parses local OSHB/WLC XML and appears to be the foundation for the core Torah coordinate model.
- `selah.text.sefaria` fetches and caches Sefaria text, apparently to support broader passage experiments outside the five-book core workflow.
- Additional namespaces exist for MAM, Leningrad, and variants.

This means the project has both a canonical core text path and a broader comparative text layer. That is powerful, but it also creates a burden: every major claim needs a clear statement about which text source it depends on.

## The Runtime Story

The runtime is lightweight:

- `selah.main` starts nREPL, HTTP, and MCP socket services.
- Translation models are optional and load only if ONNX files exist locally.
- The explorer is server-rendered HTML with HTMX-style navigation.

This is not overbuilt infrastructure. Most complexity is in the analytical model, not the application shell.

## What Looks Strong

Several parts of the repo feel technically coherent:

- The 4D coordinate engine is concrete and inspectable.
- The oracle model has clear data structures: grid, positions, traversals, illuminations, readings.
- The HTTP and MCP layers are thin wrappers over domain functions rather than separate systems.
- The repo preserves dead findings instead of only keeping winners, which is a good sign intellectually.

## What Looks Loose

The experiment layer is much looser than the core engine.

Many `dev/experiments/*` files appear to be handcrafted narrative-analysis scripts with repeated helper patterns copied across files. That gives speed, but it also increases the chance that a subtle API mismatch propagates across many experiments without being noticed.

The polished docs often speak with more confidence and thematic unity than the script layer warrants on first inspection. That does not mean the claims are false. It means the chain from computation to narrative should be checked carefully.

## Specific Mismatches I Found

### 1. Oracle vocabulary argument mismatch

In many experiment files, calls look like this:

- `o/forward hebrew {:vocab :torah}`
- `o/forward-by-head hebrew {:vocab :torah}`

But the current function signatures in `src/selah/oracle.clj` expect the second argument to be a direct vocabulary selector like `:torah`, `:dict`, or a set, not an options map.

So these experiment scripts are very likely not using the vocabulary they think they are using.

This pattern appears in a large number of experiment files, including several recent ones:

- `dev/experiments/118_the_seven_days.clj`
- `dev/experiments/119_the_fall.clj`
- `dev/experiments/120_the_burning_bush.clj`
- `dev/experiments/121_jacob_wrestles.clj`
- `dev/experiments/122_ruth.clj`
- `dev/experiments/123_the_cure_for_leprosy.clj`
- `dev/experiments/124_the_body_coordinates.clj`
- `dev/experiments/125_the_invisible_blessing.clj`
- `dev/experiments/126_curses_and_blessings.clj`
- `dev/experiments/127_pathogen_prescriptions.clj`

And it also appears in older experiment files.

Implication: some generated outputs may have been produced with a narrower or unintended vocabulary path.

### 2. Basin path key mismatch

Many experiment scripts print a basin path using `(:path walk)`.

But `selah.basin/walk` returns `:steps`, not `:path`.

So those printed “basin path” lines are not aligned with the current return shape of the function.

Implication: the visible output of those experiments may silently omit or misstate basin progression.

### 3. Saved outputs already show the breakage

This is not hypothetical.

The saved output files under `data/experiments/` already show repeated lines like:

- `basin path: []`

even when the surrounding output claims a non-null basin destination or clearly intends to show a walk.

That is not meaningful output. It is a symptom of reading the wrong key from the basin result map.

So at least some recent experiment output files are carrying broken presentation logic forward into the saved artifacts.

### 4. There is at least one branch in core oracle code that looks like garbage

In `src/selah/oracle.clj`, `resolve-index` contains this branch:

- `(map? vocab) (build-anagram-index (voice-vocab))`

That does not make conceptual sense.

If `vocab` is a map, the function ignores the provided map and builds an index from `voice-vocab` instead. That is not a normal API interpretation, and it does not match the surrounding contract for vocabulary selection.

This looks like either:

- leftover debugging code,
- a bad refactor,
- or an accidental branch that survived.

I would call that garbage until proven otherwise.

### 5. The codebase blurs the full lexicon and the oracle-closed vocabulary

With Scott's clarification, the underlying model is not self-contradictory. The repo language is.

Current source comments and API docs repeatedly describe the full Torah vocabulary as `~7,300`:

- `src/selah/dict.clj`
- `src/selah/oracle.clj`
- `src/selah/mcp/tools.clj`
- several narrative docs describing vocabulary levels

But the project's own saved data and later experiment docs repeatedly use `12,826`:

- `data/experiments/096/summary.edn`
- `data/oracle-voice.edn`
- `docs/experiments/094-thummim-sweep.md`
- `docs/experiments/096-basin-landscape.md`
- `docs/experiments/091b-the-full-quorum.md`
- `docs/the-journey.md`

That means many docs and code comments are collapsing two different things:

- the full lexicon of 12,826 forms,
- and the oracle-closed vocabulary of ~7,300.

That is not fatal, but it is sloppy enough to contaminate technical claims unless the terms are cleaned up everywhere.

### 6. The machine-translated English layer contains obvious junk

`data/torah-english.edn` is used as a fallback English layer for the full Torah vocabulary.

A lot of it is not reliable English. It includes things like:

- transliterated noise,
- partial garbage,
- `<unk>` artifacts,
- words mapped to names or nonsense strings,
- clearly wrong ordinary glosses.

Examples visible directly in the file include entries like:

- `"חרה" -> "Shit."`
- `"ה" -> "The"`
- `"לצות" -> "<unk>"`
- `"אהלו" -> "Ahl<unk>"`
- many forms translated as person names or malformed fragments

This matters because those glosses are then reused in outputs, basin summaries, and interpretive displays.

So when a result is explained in English outside the curated dictionary, the English layer may be adding noise or false precision.

### 7. English leakage is wider than it should be

The junk English layer is not isolated in one convenience script. It leaks into core-facing surfaces:

- `selah.oracle/forward`
- `selah.oracle/rank-phrases`
- `selah.basin`
- `selah.sweep`
- some DNA/protein scripts

This means garbage English can shape:

- basin summaries,
- phrase rankings,
- sweep displays,
- category labels in the UI,
- and narrative descriptions produced from those outputs.

If English is only for Scott, then the current code violates that boundary.

## Garbage and Nonsense Callouts

These are the places where I think the repo crosses from “rough research code” into “this should be called out plainly.”

### Garbage: copied experiment helpers with stale interfaces

The repeated `{:vocab :torah}` and `(:path walk)` mistakes across many experiment files are not isolated typos. They indicate a copy-forward helper pattern that drifted away from the live API and kept getting reused.

That is garbage in the strict engineering sense: duplicated glue code that is no longer truthful about what it is doing.

### Garbage: empty basin path output presented as if it meant something

The output files visibly print `basin path: []` over and over. That is not interpretation. That is broken plumbing.

If later docs treat those outputs as if the walk trace had been shown, that is not acceptable.

### Garbage: the code treats fallback English as if it were semantic truth

The fallback translations are rough machine output, but the code often stores them under fields like `:meaning` and passes them into UI grouping logic and analysis displays.

That naming encourages misuse. In practice it lets translation sludge masquerade as interpretation.

### Possible nonsense: strong narrative confidence built on lightly checked script layers

The core coordinate and oracle code looks real.

But the passage-experiment layer often reads like a sermon generator wrapped around repeated script templates. That becomes nonsense when:

- the template is calling the wrong function shape,
- the saved output is visibly broken,
- and the resulting prose still speaks with strong certainty.

That does not invalidate the whole project. It does invalidate confidence in any result that depends on those scripts without re-checking the computation.

### Garbage: the fallback English layer is not trustworthy enough for interpretation

It may be acceptable as a rough operator convenience.

## Arc Synthesis

The project did not start as garbage.

- It begins with a real anchor experiment and some honest exploratory measurement.
- It then drifts into palindrome / fractal mythology and loses methodological discipline.
- Experiment `048` is the necessary kill shot. The repo's own retrospective says the palindrome story died there.
- The factorization of `304,850` and the construction of the 4D mixed-radix space look like the true restart.
- The machine / oracle layer is where the repo becomes mechanically interesting again.
- The ark walk and tabernacle walk material look stronger than the dead bulk-statistical layer because they are tied to a real constructed space rather than haze about global symmetry.
- Some genome work looks stronger than the earlier DNA analogy band because it becomes more explicit about controls, null results, and where echo fails.
- Later, the laziness comes back in a different form: stale helper calls, broken experiment plumbing, English sludge, terminology drift, and docs that do not burn dead claims cleanly enough.

So the failure pattern is not "the whole project is nonsense." It is more specific:

- bad null models
- symbolic target fishing
- dead claims not being burned everywhere
- interface drift
- lazy output handling
- contamination from machine-generated English

That means the core cleanup question is not whether there is gold here. It is how to separate the vein from the tailings without sentimentality.

It is not acceptable as evidentiary language for claims about meaning unless each gloss has been checked. Right now it leaks too much junk into the interpretive surface.

### Possible nonsense: vocabulary-level arguments built on ambiguous counts

If docs do not consistently distinguish the 12,826-form lexicon from the ~7,300 oracle-closed vocabulary, then claims like “the findings survive at all three vocabulary levels” are not yet technically clean.

That may turn out to be fixable. But until it is stated precisely, the current wording is mushy.

## Problem Types Emerging

The audit is starting to sort into recurring categories:

### Type A: stale interface drift

- Experiment scripts calling current functions with old shapes
- Output code expecting keys that no longer exist

### Type B: vocabulary confusion

- Full lexicon vs oracle-closed vocabulary blurred together
- Curated mode appearing by accident instead of deliberate choice

### Type C: English contamination

- Machine translations leaking into analysis and UI as if they were meanings
- Garbage glosses becoming labels, summaries, and category inputs

### Type D: narrative overreach

- Strong prose built on script layers that have not been freshly verified
- Results phrased as settled when the computational path is partially stale

### Type E: documentation drift

- Old counts and contracts preserved in comments, docs, and tool descriptions
- Technical language no longer matching current intent

## What This Probably Means

The core engine may still be fine while the experiment layer has drifted from it.

That is a normal failure mode in research code:

- the central abstractions evolve,
- helper scripts freeze older calling conventions,
- narrative documents continue to be written from outputs that look plausible,
- and nobody notices the interface drift because the scripts still run.

This is the single most important technical risk I have seen so far.

## What I Would Check Next

- Audit the experiment scripts that call `oracle/forward` and `oracle/forward-by-head`.
- Audit the experiment scripts that expect basin walk results under `:path`.
- Remove or justify the `map? vocab` branch in `selah.oracle/resolve-index`.
- Reconcile terminology so `12,826` full lexicon and `~7,300` oracle-closed vocabulary are never conflated.
- Treat `data/torah-english.edn` as untrusted until filtered or replaced.
- Remove English leakage from any path that presents evidence rather than operator convenience.
- Re-run a small sample of recent experiments after fixing those calls.
- Compare regenerated outputs with the saved `data/experiments/*-output.txt` files.
- Identify which published docs depend on those outputs.

## Bottom Line

The project has a real computational core and a clear internal worldview.

But the highest-risk area is not the coordinate engine or the HTTP app. It is the passage-experiment layer where research claims are turned into concrete outputs and then into prose. That layer shows signs of interface drift, and that drift needs to be measured before stronger conclusions are drawn from recent experiment output.
