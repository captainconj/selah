# Fiber Experiments

Experiments exploring fibers — straight lines through decomposition spaces.

Each experiment searches for Hebrew words along every direction in a decomposition,
then observes what Torah words the fibers pass through (host words).

## Dependencies

```clojure
(require '[selah.search :as s]
         '[selah.fiber :as f]
         '[selah.gematria :as g])
(s/build!)  ;; 304,850 letters, 80,474 words, 5,846 verses
```

## Naming

`NNN_snake_title.clj` — standard experiment naming.
Namespace: `experiments.fiber.NNN-title`

## The discipline

Observe and report. No curated lists. No comparison against prior expectations.
Let the geometry speak. Write down what rises.

## Experiments

- **143** — Host frequency: which Torah words host the most fiber-letters?
- **144** — Host by axis: do different axes route through different words?
- **145** — Names in names: when you search for David, where does the geometry put him?
- **146** — Meeting points: do courtroom words share hosts?
- **147** — Phrase fibers: do host word sequences read as sentences?
- **148** — Bounded vs wrapping: do box-bounded fibers speak differently?
- **149** — 6D host analysis: does the finer decomposition change what rises?
- **150** — Cross-decomposition: which space tells the richest story?
