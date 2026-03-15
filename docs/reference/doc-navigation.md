# Document Navigation

Type: `reference`
State: `clean`

This repo uses two simple navigation conventions for long doc sequences.

## Walk Docs

Walk docs should feel walkable.

For sequence-based directories like `/docs/ark/` and `/docs/tabernacle/`, each doc should include:

- a link back to the local index page
- a `Previous` link when there is a previous station
- a `Next` link when there is a next station

Use a short rail near the top of the file:

```md
**Walk:** [Index](index.md) · [Previous](03-the-foundation.md) · [Next](05-the-rooms.md)
```

For tabernacle docs, use the layout page as the anchor:

```md
**Walk:** [Index](index.md) · [Previous](03-the-altar.md) · [Next](05-the-curtains.md)
```

## Experiment Docs

Experiment docs should always tell the reader how to get back to the code.

Near the top of the doc, include:

- the script path
- a direct shell command to rerun it

Use this form:

```md
**Code:** `dev/experiments/118_the_seven_days.clj`
**Run:** `clojure -M:dev dev/experiments/118_the_seven_days.clj`
```

If the experiment is driven by a namespace function instead of a top-level script, use the actual reproducible command for that namespace.

## Principle

The reader should always be able to answer:

- where am I
- what came before
- what comes next
- what code produced this
- how do I rerun it
