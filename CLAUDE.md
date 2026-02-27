# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Is

Selah (סלה) — Clojure toolkit for studying biblical texts at the letter level. Hebrew Pentateuch text acquisition, normalization, and ELS (Equidistant Letter Sequence) analysis.

## Commands

```bash
# Start everything (nREPL + HTTP:8099 + MCP socket:7889)
clojure -M:dev -m selah.main

# Run all tests
clojure -X:dev:test

# Run a single test namespace
clojure -X:dev:test :nses '[selah.els-test]'

# REPL (dev profile loads dev/user.clj and test/)
clojure -M:dev
```

MCP bridge for Claude Code integration: `scripts/mcp-bridge.sh` (nc to localhost:7889).

## Architecture

- `src/selah/text/` — fetch, normalize, cache Hebrew text
- `src/selah/els/` — ELS engine, verification, debug tools
- `src/selah/stats/` — statistical significance analysis
- `src/selah/variants/` — cross-variant alignment and diff
- `src/selah/mcp/` — MCP socket server for Claude Code integration
- `docs/` — plan, source notes, algorithm reference

### Data Flow

```
Sefaria API → raw JSON (cached to data/cache/sefaria/) → strip HTML → normalize (Hebrew letters only) → letter stream (vec of chars) → ELS engine
```

### Text Layer

- **`selah.text.sefaria`** — Fetches Hebrew text chapter-by-chapter from Sefaria API, caches raw JSON to disk. Entry point: `(book-letters "Genesis")` returns a vector of chars.
- **`selah.text.normalize`** — Strips everything except Hebrew letters (U+05D0–U+05EA). Preserves final forms (ך ם ן ף ץ). `letter-stream` returns a vec, `normalize` returns a string.

### ELS Layer

- **`selah.els`** — The ELS engine. `extract` pulls letters at equidistant spacing, `search` finds all occurrences at a given skip, `scan` searches across a range of skips. `verify-positions` takes 1-based published positions and checks them.

### Statistical Layer (planned)

- **`selah.stats`** — Expected occurrence rates, shuffled-stream controls, skip distribution analysis. See `docs/plan.md` Phase 4.

### Variant Layer (planned)

- **`selah.variants`** — Cross-variant alignment and letter-level diff. Masoretic, Samaritan Pentateuch, Dead Sea Scrolls fragments. ELS sensitivity across variants. See `docs/plan.md` Phase 5.

### Infrastructure

- **`selah.main`** — Entry point. Starts nREPL, HTTP, and MCP socket server with shutdown hooks.
- **`selah.http`** — http-kit server on port 8099.
- **`selah.nrepl`** — nREPL server on random port, writes `.nrepl-port`.
- **`selah.mcp.socket`** — TCP socket MCP server on port 7889, JSON-RPC over newline-delimited JSON.
- **`selah.mcp.server`** — JSON-RPC dispatch for MCP protocol (initialize, tools/list, tools/call).
- **`selah.mcp.tools`** — Tool registry using `deftool` macro. Currently exposes `eval`, `inspect`, and `status` tools.

### Patterns

Every stateful namespace uses:
```clojure
(defonce ^:dynamic *state* (atom {...}))
(defn state [] @*state*)
```

New MCP tools are registered via `deftool` macro which registers the JSON schema and creates a multimethod dispatch:
```clojure
(deftool tool-name "description" {:type "object" :properties {...}} [args] body)
```

## Key Decisions

- **0-based indexing** internally; published claims use 1-based. `verify-positions` handles conversion.
- **Final forms preserved** — do NOT collapse ך→כ, ם→מ, etc.
- **One text source per analysis** — never blend streams from different sources.
- **Sefaria API** is the primary source (no auth required). Cache raw responses so we don't re-fetch.
- **REPL-first workflow** — rich comment blocks in source files for interactive exploration.

## Tests

The test file `test/selah/els_test.clj` has two layers:
- **Unit tests** (no network): normalization, extract, search, verify — always runnable
- **Integration tests** (need cached Sefaria data): verify the chiastic Torah Codes pattern across all five books of the Pentateuch (Genesis/Exodus forward skip +50 for תורה, Leviticus skip +7 for יהוה, Numbers/Deuteronomy reverse skip -50 for תורה)

## Roadmap

See `docs/plan.md` for the full phased plan. Text acquisition and ELS engine are built. Next: statistical significance (Phase 4), then variant text comparison (Phase 5).
