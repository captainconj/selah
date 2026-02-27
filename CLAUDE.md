# Selah · סלה

*Pause. Look at the letters. See what's hidden in the structure.*

Clojure toolkit for studying biblical texts at the letter level.

## Project Focus
- Hebrew Pentateuch text acquisition and normalization
- ELS (Equidistant Letter Sequence) analysis
- Variant text comparison

## Architecture
- `src/selah/text/` — fetch, normalize, cache Hebrew text
- `src/selah/els/` — ELS engine, verification, debug tools
- `src/selah/stats/` — statistical significance analysis
- `src/selah/variants/` — cross-variant alignment and diff
- `src/selah/mcp/` — MCP socket server for Claude Code integration
- `docs/` — plan, source notes, algorithm reference

## Running
- `clojure -M:dev -m selah.main` — starts nREPL + HTTP (8099) + MCP socket (7889)
- MCP bridge: `scripts/mcp-bridge.sh` (nc to localhost:7889)

## Key Decisions
- Primary text source: Sefaria API
- Normalization: Hebrew letters only (U+05D0–U+05EA), preserve final forms
- 0-based indexing internally, convert from 1-based for published claims
- REPL-first workflow
