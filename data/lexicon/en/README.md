# English Lexicon Layer

This directory is for operator-facing English gloss candidates.

Hebrew remains canonical.
These files are presentation help, not evidence.

Layer order:

1. `curated.edn`
2. `model-reviewed.edn`
3. `llm-reviewed.edn`
4. `model.edn`
5. `llm.edn`

Expected shape:

```clojure
{"כבש" [{:text "lamb"
         :quality :high
         :notes "reviewed lemma gloss"}]

 "דם" [{:text "blood"
        :quality :high}
       {:text "bloodshed"
        :quality :medium
        :notes "broader semantic option"}]}
```

Supported keys per candidate:

- `:text`
- `:quality` `:high | :medium | :low`
- `:model`
- `:notes`
- `:confidence`
- optional `:source` override

Single-string values are also accepted:

```clojure
{"כבש" "lamb"}
```

Use:

- `curated.edn` for trusted hand-maintained glosses
- `model-reviewed.edn` for cleaned local-model outputs
- `llm-reviewed.edn` for reviewed LLM refinements
- `model.edn` for raw bulk model fills
- `llm.edn` for raw bulk LLM fills
