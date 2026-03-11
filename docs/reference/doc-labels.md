# Document Labels

Use these labels near the top of major documents.

They are not decoration.
They tell the reader what kind of claim they are reading.

## Format

Use a short block near the top of the document:

```md
Type: `evidence`
State: `clean`
```

Optional third line when useful:

```md
Depends on:
- `experiment 129`
- rebuilt artifact
```

Keep it short.

## Type Labels

Use one primary type per document.

- `evidence`
  Closest to computation or rebuilt artifact.

- `guided reproduction`
  Tells the reader how to rerun or walk something.

- `audit / cleanup boundary`
  Records what was stale, what was rebuilt, and what now stands.

- `synthesis`
  Interprets across multiple evidence layers.

- `testimony / history`
  Tells the discovery story or human meaning of the work.

- `planning`
  Says what should happen next.

- `reference`
  Defines terms, catalogs sources, or maps structures.

- `retired / historical`
  Preserved for honesty or memory, not a live evidence surface.

## State Labels

Use one current-state label per document.

- `clean`
  The document matches the current evidence boundary well enough to trust as written.

- `clean but needs better presentation`
  The substance is sound, but the structure or readability still needs work.

- `mixed`
  Contains real material but still blends layers or depends on weaker surfaces.

- `stale`
  No longer matches the current experiment/artifact boundary.

- `notebook`
  Exploratory or live-run oriented, not yet a durable artifact surface.

- `retired`
  Historically important, but not to be treated as a current evidence surface.

## Usage Rules

- Label the strongest active docs first.
- Do not mark a doc `clean` just because it is compelling.
- `synthesis` docs can be `clean` if they speak faithfully from clean sources.
- If a doc depends on stale artifacts, mark it `stale` or `mixed`.
- If a doc has not been rechecked after a cleanup boundary changed, do not guess.

## First Pass Priority

Apply labels first to:

- `docs/start-here.md`
- `docs/doc-map.md`
- strongest active experiment docs
- strongest Ark/code/genome docs
- major reference docs

This is enough to teach the reader the system before the whole repo is labeled.
