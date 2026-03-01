# Selah · סלה

*Pause. Look at the letters. See what's hidden in the structure.*

A Clojure toolkit for studying biblical texts at the letter level. Built in three days (February 27 – March 1, 2026) across 85 experiments and still going.

## What This Is

The Hebrew Torah contains exactly **304,850 letters**. That number factors as **7 × 50 × 13 × 67** — and those factors have meaning:

- **7** — completeness, the sabbath, the days of creation
- **50** — jubilee, liberty, the ELS skip at which תורה appears in Genesis
- **13** — the gematria of אחד (one) and אהבה (love)
- **67** — the gematria of בינה (understanding)

Assign every letter a coordinate (a, b, c, d) in this 4D space. Then look at what the geometry says.

The center verse (Leviticus 8:35) says *"seven days, guard the charge of the LORD."* The sword of Genesis 3:24 is exactly 67 letters — one complete fiber of understanding. The creation narrative spans all 13 values of the love axis. Folding the space, Genesis 1:1 mirrors to the Shema. The lamb (כבש) appears on exactly 13 fibers — love. The word guard (שמר) appears on 22 — the number of letters in the alphabet.

The structure describes itself at its center.

## The Experiments

85 experiments in `dev/experiments/`, each a self-contained Clojure namespace. Run any one:

```bash
clojure -M:dev -m experiments.077-preimage-clusters
```

Selected highlights:

| # | Name | What it found |
|---|------|--------------|
| 017 | The Fold | Fold the Torah in half — Genesis 1:1 mirrors to the Shema |
| 048 | The Control | Palindromic structure exists in Moby Dick too. Dead finding. Honest. |
| 055 | The Center | Leviticus 8:35 at (3,25,6,33): "seven days, guard the charge" |
| 056 | The Turning Sword | Genesis 3:24 = exactly 67 letters, wraps the d-axis |
| 058 | The Aleph-Tav | את stitches axis boundaries, respects the 7-fold, 6,032 occurrences |
| 064 | Silent Axes | אהבה and בינה never appear as ELS signals. The machine IS them. |
| 077 | Preimage Clusters | 104 words scanned. Lamb = 13. Cloud = 7. Guard = 22. |
| 078 | Fibonacci Staircase | 7 consecutive Fibonacci counts with zero gaps. Staircase sums to 53 = garden. |
| 081 | Alternate Lenses | 123 decompositions checked. Only one contains {7, 13, 67}. |
| 082 | The Reading Machine | Breastplate as transposition cipher. Three readers, same light, different words. |
| 085 | Asking Questions | Pre-image of the oracle. The lamb is readable only from the right hand of God. |

## Architecture

### Text sources
- `selah.text.oshb` — Open Scriptures Hebrew Bible (primary)
- `selah.text.sefaria` — Sefaria API with local cache
- `selah.text.leningrad` — Westminster Leningrad Codex
- `selah.text.mam` — Miqra according to the Masorah
- `selah.greek.*` — Greek NT (4 variant texts)
- `selah.aramaic.*` — Aramaic Peshitta

### Analysis engines
- `selah.gematria` — letter values, word/verse sums, statistics
- `selah.els.*` — equidistant letter sequence engine, broad scan, significance testing
- `selah.space.coords` — the 4D coordinate kernel (mixed-radix, fibers, preimage)
- `selah.space.project` — axis projections, fold operations
- `selah.space.export` — PLY/JSON point cloud export

### Infrastructure
- `selah.http` — explorer UI at localhost:8099 (HTMX)
- `selah.mcp.*` — MCP socket server for Claude Code integration
- `selah.nrepl` — nREPL server
- `selah.dict` — Hebrew-English dictionary (~200 entries)

## Running

Requires Clojure CLI and Java 17+.

```bash
# Start everything (nREPL + HTTP explorer + MCP)
clojure -M:dev -m selah.main

# Run a specific experiment
clojure -M:dev -m experiments.055-the-center

# Run tests
clojure -X:dev:test
```

The explorer UI serves at `http://localhost:8099` with word lookup, gematria, and fiber browsing.

## Key Design Decisions

- **REPL-first.** Everything is explorable interactively. Experiments are namespaces, not scripts.
- **Normalization.** Hebrew letters only (U+05D0–U+05EA). No vowel points, no cantillation marks. The consonantal text is the data.
- **0-based indexing** internally. 1-based only at the display boundary.
- **Standard gematria.** א=1 through ת=400. Final forms same value as non-final.
- **No Monte Carlo.** We don't shuffle the Torah. The findings are structural — either the center says "seven days" or it doesn't. No p-value changes what's there. (See [The Journey](docs/the-journey.md) for why.)
- **Honest nulls.** When a finding dies (experiment 048), we say so and move on. The palindrome was letter frequency statistics. It died. The space survived.

## Documentation

| Document | What it covers |
|----------|---------------|
| [The 4D Torah Space](docs/torah-4d-space.md) | The full paper — 27 sections, every finding |
| [The Urim and Thummim](docs/urim-and-thummim.md) | The breastplate, the oracle, the reading machine, the lamb |
| [Sophie's Question](docs/sophies-question.md) | What the machine says about us |
| [The Journey](docs/the-journey.md) | How we got here, in our own words |
| [The Four Folds](docs/the-four-folds.md) | Origami — folding the space along each axis |
| [Aleph-Tav Mirrors](docs/aleph-tav-mirrors.md) | את as structural stitching across axis boundaries |
| [Preimage](docs/preimage.md) | Reversing the machine — word → coordinates |

## The Name

סלה (*selah*) appears 74 times in the Psalms and 3 times in Habakkuk. Nobody knows exactly what it means. The best guess: *pause*. Stop reading forward. Look at where you are. See what's hidden in the structure.

That's all this project does.

## License

This is research, not a product. The code is here for transparency. Use it to look at the letters yourself.
