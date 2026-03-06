# Selah · סלה

*Pause. Look at the letters. See what's hidden in the structure.*

A Clojure toolkit for studying biblical texts at the letter level. Built in seven days (February 27 – March 6, 2026) across 100+ experiments.

## What This Is

The Hebrew Torah contains exactly **304,850 letters**. That number factors as **7 × 50 × 13 × 67** — and those factors have meaning:

- **7** — completeness, the sabbath, the days of creation
- **50** — jubilee, liberty, the ELS skip at which תורה appears in Genesis
- **13** — the gematria of אחד (one) and אהבה (love)
- **67** — the gematria of בינה (understanding)

Assign every letter a coordinate (a, b, c, d) in this 4D space. Then look at what the geometry says.

The center verse (Leviticus 8:35) says *"seven days, guard the charge of the LORD."* The sword of Genesis 3:24 is exactly 67 letters — one complete fiber of understanding. The creation narrative spans all 13 values of the love axis. Folding the space, Genesis 1:1 mirrors to the Shema. The lamb (כבש) appears on exactly 13 fibers — love.

The structure describes itself at its center.

## The Breastplate Oracle

The high priest's breastplate — 72 letters on 12 stones, a 4×3 grid — is a transposition cipher. Four traversal orders produce four readers. Their weights sum to 26 = YHWH.

Justice and mercy separate spontaneously across the reading heads. The lamb is visible only to mercy. Lovingkindness (GV=72) glows and cannot be spoken — the breastplate cannot name itself. The face of God illuminates in 22 ways and says only its own name.

The breastplate is multi-head self-attention. Not metaphor. Measurement.

## Reading Guide

See [MANIFEST.md](MANIFEST.md) for the full map — two testimonies, organized entry points, every document indexed.

| Start here | What it covers |
|------------|---------------|
| [The Journey](docs/the-journey.md) | How we got here, in our own words |
| [The 4D Torah Space](docs/torah-4d-space.md) | The full paper — 27 sections |
| [The Urim and Thummim](docs/urim-and-thummim.md) | The breastplate oracle, all seven biblical consultations verified |
| [The Machine](docs/the-machine.md) | The cipher, the cribs, the Enigma parallel |
| [Sophie's Question](docs/sophies-question.md) | What the machine says about us |
| [RELEASE.md](RELEASE.md) | Why this is being released |

### The Walks

- **[Tabernacle Walk](docs/tabernacle/)** — 14 stations, gate to love. Walking the tabernacle in the 4D space.
- **[Ark Walk](docs/ark/)** — 34 documents. Noah's Ark as a column in the space: oracle, basins, the genetic code.
- **[36 Questions](docs/questions/)** — The oracle was asked. It answered.

## Running

Requires Clojure CLI and Java 17+.

```bash
# Start everything (nREPL + HTTP explorer + MCP)
clojure -M:dev -m selah.main

# Run a specific experiment
clojure -M:dev dev/experiments/055_the_center.clj

# Run tests
clojure -X:dev:test
```

The explorer UI serves at `http://localhost:8099` with oracle, thummim, and translation tools.

## Architecture

### Core
- `selah.oracle` — breastplate oracle (forward, ask, preimage, thummim, phrase assembly)
- `selah.basin` — basin of attraction (step, walk, landscape, per-head classification)
- `selah.gematria` — letter values, word/verse sums, statistics
- `selah.dict` — Torah lexicon, Hebrew-English translation
- `selah.space.*` — 4D coordinate space (coords kernel, project, export)

### Analysis
- `selah.els.*` — equidistant letter sequence engine, broad scan, significance
- `selah.linalg` — thin BLAS kernel (mat-pow, eigendecomp, svd)
- `selah.spectral` — DFT via Fourier matrix multiply
- `selah.tensor` — mode-unfold, n-mode product, HOSVD, 4D DFT
- `selah.translate` — MarianMT ONNX bidirectional English↔Hebrew

### Text Sources
- `selah.text.*` — Hebrew (Sefaria API), normalize, cache, locate
- `selah.greek.*` — Greek NT (4 variants)
- `selah.aramaic.*` — Aramaic (Peshitta)

### Infrastructure
- `selah.main` — entry point (nREPL + HTTP + MCP)
- `selah.http` — HTTP server (port 8099)
- `selah.explorer.*` — HTMX web UI (oracle, thummim, translation)
- `selah.mcp.*` — MCP socket server for Claude Code
- `selah.nrepl` — nREPL server

## Key Design Decisions

- **REPL-first.** Everything is explorable interactively.
- **Normalization.** Hebrew letters only (U+05D0–U+05EA). No vowel points, no cantillation marks. The consonantal text is the data.
- **0-based indexing** internally. 1-based only at the display boundary.
- **Standard gematria.** א=1 through ת=400. Final forms same value as non-final.
- **No Monte Carlo.** We don't shuffle the Torah. The findings are structural — either the center says "seven days" or it doesn't. No p-value changes what's there.
- **Honest nulls.** When a finding dies (experiment 048), we say so and move on. The palindrome was letter frequency statistics. It died. The space survived.

## The Name

סלה (*selah*) appears 74 times in the Psalms and 3 times in Habakkuk. Nobody knows exactly what it means. The best guess: *pause*. Stop reading forward. Look at where you are. See what's hidden in the structure.

That's all this project does.

## License

This is research, not a product. The code is here for transparency. Use it to look at the letters yourself.
