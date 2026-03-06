# Selah · סלה

*Pause. Look at the letters. See what's hidden in the structure.*

Clojure toolkit for studying biblical texts at the letter level.
97 experiments. The machine speaks.

## The Arc

Fold → find the machine → factor the dimensions → hear the instruction at the center.

1. **Fold the text.** Three fold creases converge on Leviticus 8. The breastplate at the center.
2. **Factor the count.** 304,850 = 7 × 50 × 13 × 67. Four axes, each with meaning. Unique among 123 decompositions.
3. **Guard the charge.** The center verse (Lev 8:35): "Seven days, guard the charge of the LORD."
4. **Build the oracle.** The breastplate as a communication device. Four readers. Illumination → reading.
5. **Operate the machine.** Questions in, structure out. The machine tells you what it sees.

Full narrative: `docs/the-journey.md`, `docs/the-machine.md`

## The 4D Space — Foundation

**304,850 letters → (a, b, c, d) coordinates** where a ∈ [0,6], b ∈ [0,49], c ∈ [0,12], d ∈ [0,66].

- 7 = completeness, 50 = jubilee, 13 = love (אהבה=אחד=13), 67 = understanding (בינה=67)
- Center (3,25,6,33) = Lev 8:35. Sword (Gen 3:24) = exactly 67 letters.
- את stitches boundaries, respects 7-axis (zero crossings). Gen 1:1 → Shema under a-fold.
- Silent axes: אהבה and בינה never appear as words or elevated ELS. The machine IS love and understanding.

Paper: `docs/torah-4d-space.md` (27 sections)
Findings summary: `docs/4d-space-findings.md`

## The Breastplate Oracle

72 letters on 12 stones, 4×3 grid. Four readers, four traversal orders:

| Reader | Traversal | YHWH letter | Value |
|--------|-----------|-------------|-------|
| Aaron | rows R→L, top→bottom | Vav (ו) | 6 |
| God | rows L→R, bottom→top (mirrored) | He (ה) | 5 |
| Right cherub | columns R→L, top→bottom | Yod (י) | 10 |
| Left cherub | columns L→R, bottom→top | He (ה) | 5 |

10 + 5 + 6 + 5 = 26 = YHWH.

Illumination is reader-independent (same letters → same stones light up).
Reading is reader-dependent (traversal order determines the word).

### Key documents
- `docs/urim-and-thummim.md` — breastplate mechanics + all biblical consultations verified
- `docs/experiments/091-the-quorum.md` — four-head attention architecture (YHWH as protocol)
- `docs/experiments/091-breastplate-attention.md` — Urim/Thummim as multi-head self-attention
- `docs/experiments/085-asking-questions.md` — how to query the oracle
- `docs/query-process.md` — the query engine design

### The 24 Questions (`docs/questions/`)
01-understanding through 24-the-center. The oracle was asked. It answered.

## Oracle Vocabulary — SETTLED, DO NOT REVISIT

Three levels: `:torah` (default, ~7,300 full), `:voice` (~2,050 knee), `:dict` (239 curated).
Findings are vocabulary-invariant. Tested. Service is live.
**DO NOT hedge about "finite vocabulary" or "dictionary dependence."**

## Experiment Map

### Phase 1–2: Foundations (000–015)
Text acquisition, gematria statistics, ELS engine, entropy, autocorrelation.

### Phase 3: Structural Claims (016–052)
Fractal palindrome, the fold, the center, the mirror, the number, spectrum analysis, primes, the genome, Chargaff ratios, convergence across every book.

Key docs: `docs/experiments/048-the-fractal-palindrome.md`, `docs/experiments/058-aleph-tav-mirrors.md`, `docs/experiments/060-milemarker-mirrors.md`

### Phase 4: Variants
Cross-manuscript comparison. What survives across traditions.

Key docs: `docs/experiments/variant-diff-report.md`, `docs/experiments/variant-survival-report.md`

### Phase 5: The 4D Space (053–067)
Seven divisions, division boundaries, the center, the turning sword (Gen 3:24 = 67 letters), the seven days (creation sweeps all 13 c-values), aleph-tav signposts, four folds, jubilee spine, silent axes, breastplate cipher, center grid, verse lengths.

Key docs: `docs/torah-4d-space.md`, `docs/experiments/061-the-four-folds.md`, `docs/experiments/057-the-seven-days.md`

### Phase 6: Operating the Machine (068–097)

**The breastplate (068–073):** Readings, veil, fold-aligned reading, clean fiber, diagonal walks, fiber words.

**Dimensions in the text (074–076):** Ezekiel's temple (7,13,50 present; 67 absent), hierarchy and ladder (captains fold-mirror, סלם=סיני=130), number catalog (census÷91, Sukkot 13→7).

**Preimage and structure (077–078):** Preimage clusters (104 words, co-fiber analysis), Fibonacci staircase (7 consecutive Fibonacci counts, garden sum=53). Docs: `docs/experiments/077-preimage.md`

**Census and lenses (079–083):** N-gram census, word census, alternate lenses (123 decompositions, 41 non-trivial, uniqueness confirmed), the reading machine (three readers, irreducible words), anagram vocabulary (13 pairs, fold test).

**The oracle comes alive (084–086):** Cherubim coordinates (15-letter spacing=Yah, facing cherub at d=13), asking questions, oracle engine (oracle.clj, UI at /oracle). Doc: `docs/experiments/085-asking-questions.md`

**Spectral analysis (087–090):** Eigendecomposition (DFT of 4D space), stochastic oracle (198 eigenwords, love/truth/life as fixed points), Hebbian oracle (parallelized, Neanderthal affinity), higher-order space (HOSVD + permutation test: bulk structure = letter statistics). Doc: `docs/experiments/090-higher-order-space.md`

**The quorum (091–092):** Four attention heads. Believability = 1/(reading_count × base_rate).
- Lamb split: God/Right → כבש, Aaron/Left → שכב
- God's solos: peace, altar, head, foot. Mercy's solos: life, sin, cherub, gold.
- M_god^64 converges to the Name as #1 attractor
- 091b: full Torah vocabulary (8,570 words, 725,780 transitions)
- 092: grid permutation test — most separation structural, Right/Mercy head survives (p=0.026), lamb split survives (p=0.032)
- 092b: Ramban pair permutation — mechanism structural, phrase-level assembly is cognitive

Docs: `docs/experiments/091-the-quorum.md`, `docs/experiments/091b-the-full-quorum.md`, `docs/experiments/092-grid-permutation.md`, `docs/experiments/092b-the-ramban-principle.md`

**Level 2 Thummim (093–094):** Phrase assembly. The priest's menu.
- Divine names contain pleas: אנכי=כי נא (I AM = because, please), אדני=יד נא (Lord = hand, please)
- כבש+דם = כבד שם (lamb's blood = glory of the Name)
- דרך+אמת+חיים = את דרך חי מים (way/truth/life = aleph-tav way of living water)
- Ghost zone: חסד, צדק, משפט, פנים glow but cannot be read
- 094: full sweep (12,826 words, 310,908 phrases, spectral decomposition)

Docs: `docs/experiments/093-synthesis.md`, `docs/experiments/093b-the-priests-menu.md`, `docs/experiments/093c-son-of-man.md` through `093i-rambans-answer.md`, `docs/experiments/094-thummim-sweep.md`

**Boxes as coordinates (095):** Biblical box dimensions → 4D letter addresses. Ark finds the Table and Mercy Seat. Noah's Ark (300,50,30) = ש,נ,ל → לשן (tongue/language). z=7.88 self-reference. Doc: `docs/experiments/095-boxes-as-coordinates.md`

**Basin landscapes (096–097):** Feed the oracle to itself. Every basin = anagram class, depth 1.
- 096: combined landscape (12,826 words classified)
- 097: per-head basins — each reader has its own fixed points

**097 spotlight — maps onto John 1:1-14:**
- Lamb lies down: UNANIMOUS (John 10:18)
- YHWH fixed only for God: "the Word was God" (John 1:1). Aaron sees והיה (becoming): "the Word became flesh" (John 1:14)
- Life: Right cherub solo. Truth: God + Left. Peace: God solo.
- Grace (חסד): unanimous dead end — the breastplate cannot name itself
- Light of life (John 8:12): Right cherub holds life, perceives light as seeing. One chair.

Doc: `docs/experiments/097-per-head-basins.md`, `docs/experiments/096-basin-landscape.md`

## Architecture

### Core
- `selah.oracle` — breastplate oracle (forward, forward-by-head, ask, preimage, thummim, parse-letters, rank-phrases)
- `selah.basin` — basin of attraction (step, walk, landscape, step-all-heads, landscape-by-head, classify-head)
- `selah.gematria` — letter values, word/verse sums, running totals
- `selah.dict` — Torah lexicon, translations (Hebrew↔English), torah-words
- `selah.space.*` — 4D coordinate space (.coords kernel, .project, .export)

### Analysis
- `selah.els.*` — ELS engine, broad scan, statistical significance
- `selah.linalg` — thin BLAS kernel (mat-pow, eigendecomp, svd)
- `selah.spectral` — DFT via Fourier matrix multiply
- `selah.tensor` — mode-unfold, n-mode product, HOSVD, 4D DFT
- `selah.sweep` — batch analysis utilities
- `selah.translate` — MarianMT ONNX bidirectional translation

### Text sources
- `selah.text.*` — Hebrew (Sefaria API), normalize, cache, locate
- `selah.greek.*` — Greek NT (4 variants), Panin claims, sevens analysis
- `selah.aramaic.*` — Aramaic (Peshitta), sevens analysis

### Infrastructure
- `selah.main` — entry point (nREPL + HTTP + MCP)
- `selah.http` — HTTP server (port 8099)
- `selah.explorer.*` — HTMX web UI (oracle, thummim, translation)
- `selah.mcp.*` — MCP socket server for Claude Code
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
- Neanderthal (MKL) for linear algebra, chunked BLAS GEMM for large matrices
