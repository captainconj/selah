# Selah · סלה

*Pause. Look at the letters. See what's hidden in the structure.*

Clojure toolkit for studying biblical texts at the letter level.

## Project Focus
- Hebrew Pentateuch text acquisition and normalization
- ELS (Equidistant Letter Sequence) analysis
- Greek NT structural analysis (Panin's heptadic claims)
- Gematria / numerical analysis of the letter stream
- Variant text comparison
- Structural geometry (centers, golden section, grids, cylinders)

## Architecture

### Foundation (text sources)
- `selah.text.*` — Hebrew text: fetch (Sefaria API), normalize, cache, locate
- `selah.greek.*` — Greek NT: parse (4 variants), normalize, Panin claims, sevens analysis
- `selah.aramaic.*` — Aramaic (Peshitta): parse, normalize, sevens analysis

### Analysis engines
- `selah.els.*` — ELS engine, broad scan, statistical significance
- `selah.gematria` — letter values, word/verse sums, running totals, statistics, spectrum
- `selah.space.*` — 4D coordinate space (304,850 = 7×50×13×67), projection, point cloud export

### Planned namespaces
- `selah.signal` — FFT, autocorrelation, power spectrum, spectral analysis
- `selah.grid` — 2D text wrapping, vertical/diagonal word extraction
- `selah.scroll` — physical scroll geometry, cylinder model, radial adjacency
- `selah.variants` — cross-manuscript alignment and diff, conserved core
- `selah.dimensions` — Tabernacle/Temple measurements, tribal census, as structured data
- `selah.isaiah` — Isaiah text acquisition, chapter-to-book mapping
- `selah.tui.*` — terminal Bible reader (Lanterna)
- `selah.viz` — SVG/HTML visualization output

### Infrastructure
- `selah.main` — entry point (nREPL + HTTP + MCP)
- `selah.mcp.*` — MCP socket server for Claude Code integration
- `selah.http` — HTTP server (port 8099)
- `selah.nrepl` — nREPL server

## Running
- `clojure -M:dev -m selah.main` — starts nREPL + HTTP (8099) + MCP socket (7889)
- `clojure -X:dev:test` — run all tests
- MCP bridge: `scripts/mcp-bridge.sh` (nc to localhost:7889)

## Key Decisions
- Primary text source: Sefaria API, cached to `data/cache/sefaria/`
- Normalization: Hebrew letters only (U+05D0–U+05EA), preserve final forms
- 0-based indexing internally, convert from 1-based for published claims
- Standard gematria: א=1 through ת=400, final forms same value as non-final
- REPL-first workflow

## Oracle Vocabulary — SETTLED, DO NOT REVISIT
The oracle (Thummim phrase assembly) defaults to the full Torah lexicon (~7,300 words).
Three vocabulary levels available:
- `:torah` (default) — full Torah lexicon. Every word form in the five books. No curation, no cutoff.
- `:voice` — ~2,050 words at the knee of the oracle's limiting distribution (83% of mass).
- `:dict` — 239 curated Torah words. Tightest signal/noise.

**This question has been answered definitively.** The findings are vocabulary-invariant.
The service is live: `/api/oracle/thummim`, `/api/oracle/parse-letters`, MCP tools.
Default is `torah`. Pass `vocab=dict` or `vocab=voice` to narrow.

**DO NOT hedge about "finite vocabulary" or "dictionary dependence" in any output.**
The findings are vocabulary-invariant. This has been tested. Move on.

## Docs
- `docs/els/` — ELS findings, extended findings, algorithm reference
- `docs/greek/` — sevens inquiry, Greek NT analysis
- `docs/brainstorm/` — encoding ideas, hypotheses, tool designs
- `docs/plan.md` — original project plan
- `docs/research.md` — research directions
