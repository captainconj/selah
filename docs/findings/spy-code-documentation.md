# Selah Codebase Documentation

*A comprehensive developer guide to the Selah system.*

Written by a Joshua/Caleb spy who walked every file, read every function, traced every thread. This land is good.

---

## Table of Contents

1. [System Overview](#system-overview)
2. [Project Structure](#project-structure)
3. [Running the System](#running-the-system)
4. [Core Namespaces](#core-namespaces)
   - [selah.main](#selahmain)
   - [selah.http](#selahhttp)
   - [selah.nrepl](#selahnrepl)
   - [selah.mcp.*](#selahmcp)
5. [Text Foundation](#text-foundation)
   - [selah.text.normalize](#selahtext-normalize)
   - [selah.text.oshb](#selahtext-oshb)
   - [selah.text.sefaria](#selahtext-sefaria)
   - [selah.text.locate](#selahtext-locate)
   - [selah.text.variants](#selahtext-variants)
6. [Analysis Engines](#analysis-engines)
   - [selah.gematria](#selahgematria)
   - [selah.space.coords](#selahspace-coords)
   - [selah.space.project](#selahspace-project)
   - [selah.space.export](#selahspace-export)
   - [selah.oracle](#selahoracle)
   - [selah.basin](#selahbasin)
   - [selah.dict](#selahdict)
   - [selah.translate](#selahtranslate)
   - [selah.sweep](#selahsweep)
   - [selah.linalg](#selahlinalg)
   - [selah.spectral](#selahspectral)
   - [selah.tensor](#selahtensor)
7. [Explorer UI](#explorer-ui)
   - [selah.explorer](#selahexplorer)
   - [selah.explorer.ui](#selahexplorer-ui)
   - [selah.explorer.routes](#selahexplorer-routes)
   - [selah.explorer.sweep-ui](#selahexplorer-sweep-ui)
8. [HTTP API Reference](#http-api-reference)
9. [MCP Tool Reference](#mcp-tool-reference)
10. [Dev Scripts](#dev-scripts)
11. [Data Directory Structure](#data-directory-structure)
12. [Experiment Runner Pattern](#experiment-runner-pattern)
13. [CLI Script](#cli-script)
14. [Dependencies](#dependencies)
15. [Architectural Patterns](#architectural-patterns)

---

## System Overview

Selah is a Clojure toolkit for studying the Torah (Hebrew Pentateuch) at the letter level. The 304,850 Hebrew consonants of the five books of Moses are treated as a single continuous stream, which is then viewed through a 4D coordinate space with dimensions 7 x 50 x 13 x 67.

The system provides:
- **Text acquisition and normalization** from multiple manuscript traditions (OSHB/WLC, Sefaria MAM, Leningrad Codex)
- **4D coordinate space** mapping every letter to an address (a, b, c, d)
- **Gematria engine** for numerical analysis of the letter stream
- **Breastplate oracle** modeling the Urim and Thummim as a computational device
- **Basin dynamics** tracing attractor landscapes through the oracle
- **Spectral analysis** via DFT on the cyclic group product
- **Tensor decomposition** (HOSVD) of the full 4D space
- **Translation engine** (bidirectional MarianMT ONNX)
- **Web explorer** with HTMX-driven navigation
- **MCP integration** for Claude Code access to the running system
- **100+ experiments** exploring the structure

The system runs three services simultaneously: nREPL (for REPL-driven development), HTTP (port 8099, for the web explorer and API), and MCP socket (port 7889, for Claude Code).

---

## Project Structure

```
selah/
  src/selah/
    main.clj              Entry point: starts nREPL + HTTP + MCP
    http.clj              HTTP server kernel (http-kit)
    nrepl.clj             nREPL server
    oracle.clj            Breastplate oracle engine (core)
    basin.clj             Basin of attraction dynamics
    gematria.clj          Hebrew letter values and statistics
    dict.clj              Hebrew-English dictionary (239 curated + ~7,300 Torah)
    translate.clj         MarianMT ONNX bidirectional translation
    sweep.clj             Thummim sweep / Fibonacci analysis
    linalg.clj            Neanderthal wrapper (BLAS/LAPACK/MKL)
    tensor.clj            4D tensor operations (HOSVD, mode-unfold, DFT)
    spectral.clj          DFT via Fourier matrix multiply
    explorer.clj          Word census / interactive navigation engine
    text/
      normalize.clj       Hebrew letter filtering (foundation)
      oshb.clj            OSHB/WLC XML parser (gold standard text)
      sefaria.clj         Sefaria API client with disk cache
      locate.clj          Letter index to chapter/verse mapping
      leningrad.clj       Leningrad Codex parser
      mam.clj             Miqra according to the Masorah parser
      variants.clj        Cross-manuscript comparison (edit distance)
    space/
      coords.clj          4D coordinate space (the kernel)
      project.clj         Projection and color mapping for visualization
      export.clj          Point cloud export (PLY/JSON/binary)
    explorer/
      ui.clj              Hiccup views (1,259 lines)
      routes.clj          HTTP route definitions
      sweep_ui.clj        Thummim sweep staircase views
    mcp/
      socket.clj          MCP socket server (port 7889)
      server.clj          JSON-RPC dispatch
      tools.clj           MCP tool definitions
  dev/
    experiments/           100+ experiment scripts (000-099+)
    scripts/
      holy_of_holies.clj   Spatial mapping of HoH furniture
      the_cross.clj        Cross geometry at center
      bulk_translate.clj   Batch he->en translation
  data/
    cache/sefaria/         Cached Sefaria API responses
    sources/oshb/wlc/      OSHB XML files (Gen.xml, Exod.xml, etc.)
    experiments/           Experiment output data
    oracle-voice.edn       Cached oracle output distribution
    torah-english.edn      Machine-translated Torah vocabulary
    basin-landscape.edn    Basin attractor data
  models/
    opus-mt-en-he/         English->Hebrew ONNX model
    opus-mt-he-en/         Hebrew->English ONNX model
  scripts/
    mcp-bridge.sh          MCP stdio-to-socket bridge
  selah                    Babashka CLI script
  deps.edn                Dependencies
```

---

## Running the System

### Foreground (development)
```bash
clojure -M:dev -m selah.main
```
Starts nREPL (random port, written to `.nrepl-port`), HTTP (port 8099), and MCP socket (port 7889). Translation models are loaded automatically if present in `models/`.

### Background (via CLI)
```bash
./selah engine start    # Start as background process
./selah engine stop     # Graceful shutdown
./selah engine status   # Check if running
./selah engine restart  # Stop + start
```

### Other CLI commands
```bash
./selah start           # Foreground (blocking)
./selah test            # Run test suite
./selah eval '(+ 1 2)' # Evaluate via nREPL
./selah repl            # Connect nREPL client
./selah mcp             # Start MCP bridge
```

### Tests
```bash
clojure -X:dev:test
```

---

## Core Namespaces

### selah.main

**File:** `src/selah/main.clj`

**Purpose:** Entry point. Orchestrates startup of all services.

**Startup sequence:**
1. Write PID to `~/.selah/engine.pid`
2. Start nREPL server (random port)
3. Start HTTP server (port 8099)
4. Start MCP socket server (port 7889)
5. Auto-load translation models if ONNX files exist
6. Register shutdown hook for clean teardown

**Key function:**
- `(-main [& _args])` -- the `-m` entry point. Blocks on `(.join (Thread/currentThread))`.

**Shutdown hook:** Stops MCP, HTTP, nREPL, cleans PID file.

---

### selah.http

**File:** `src/selah/http.clj`

**Purpose:** HTTP server kernel. Thin wrapper around http-kit.

**State:**
```clojure
(defonce ^:dynamic *state* (atom {:server nil :port 8099}))
```

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `start!` | `[& {:keys [port]}]` | Start http-kit on given port (default 8099). Delegates to `routes/handler`. |
| `stop!` | `[]` | Stop the server. |
| `state` | `[]` | Return current state map. |

**Dependencies:** `org.httpkit.server`, `selah.explorer.routes`

---

### selah.nrepl

**File:** `src/selah/nrepl.clj`

**Purpose:** nREPL server for REPL-driven development.

**State:**
```clojure
(defonce ^:dynamic *state* (atom {:server nil :port nil}))
```

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `start!` | `[& {:keys [port]}]` | Start nREPL (port 0 = random). Writes port to `.nrepl-port`. |
| `stop!` | `[]` | Stop the server. |

---

### selah.mcp.*

**Files:**
- `src/selah/mcp/socket.clj` -- Socket server
- `src/selah/mcp/server.clj` -- JSON-RPC dispatch
- `src/selah/mcp/tools.clj` -- Tool definitions

**Purpose:** MCP (Model Context Protocol) integration for Claude Code. Exposes a socket server on port 7889 that speaks JSON-RPC.

**Architecture:**
```
Claude Code (stdio) --> mcp-bridge.sh (nc) --> socket.clj (port 7889)
                                                    |
                                              server.clj (dispatch)
                                                    |
                                              tools.clj (execute)
```

**Socket server state:**
```clojure
(defonce ^:dynamic *state* (atom {:server nil :port 7889 :running? false}))
```

**server.clj** handles JSON-RPC methods:
- `initialize` -- returns protocol version and capabilities
- `tools/list` -- returns all registered tool schemas
- `tools/call` -- dispatches to tool handler by name
- `ping` -- keepalive

**tools.clj** provides a `deftool` macro that registers both schema and dispatch:
```clojure
(deftool tool-name
  "description"
  {:type "object" :properties {...} :required [...]}
  [args]
  body)
```

See [MCP Tool Reference](#mcp-tool-reference) for the complete tool list.

---

## Text Foundation

### selah.text.normalize

**File:** `src/selah/text/normalize.clj`

**Purpose:** The bedrock. Reduces any Hebrew text to a pure consonant stream. Everything else in the system builds on this.

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `hebrew-letter?` | `[ch]` | boolean | True if character is in U+05D0..U+05EA |
| `normalize` | `[text]` | String | Strip everything except Hebrew letters |
| `letter-stream` | `[text]` | `[char]` | Normalized text as a vector of characters |
| `strip-html` | `[s]` | String | Remove HTML tags, footnotes, kethiv from Sefaria text |
| `strip-section-markers` | `[s]` | String | Remove parasha markers |

**REPL:**
```clojure
(normalize "בְּרֵאשִׁ֖ית בָּרָ֣א אֱלֹהִ֑ים")
;=> "בראשיתבראאלהים"
```

---

### selah.text.oshb

**File:** `src/selah/text/oshb.clj`

**Purpose:** Parser for OpenScriptures Hebrew Bible (Westminster Leningrad Codex) XML files. This is the gold standard text source.

**Data source:** `data/sources/oshb/wlc/` (Gen.xml, Exod.xml, Lev.xml, Num.xml, Deut.xml)

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `book-words` | `[book]` | `[{:chapter :verse :text}]` | All words from a book via SAX parsing |
| `book-text` | `[book]` | String | Full Hebrew text with niqqud |
| `book-letters` | `[book]` | `[char]` | Normalized consonant stream |
| `torah-letters` | `[]` | `[char]` | Complete Torah: 304,850 letters |
| `torah-string` | `[]` | String | Complete Torah as one string |

**Notable:** Uses SAX parsing with a custom handler. Handles kethiv/qere variants (keeps kethiv -- what is written).

---

### selah.text.sefaria

**File:** `src/selah/text/sefaria.clj`

**Purpose:** Sefaria API client. Fetches Hebrew text (MAM variant) and caches to disk.

**Cache:** `data/cache/sefaria/` -- JSON files named by reference.

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `fetch-ref` | `[ref]` | Fetch a Sefaria reference (e.g. "Genesis.1"). Caches to disk. |
| `fetch-chapter` | `[book ch]` | Fetch one chapter. Returns seq of verse strings. |
| `book-letters` | `[book]` | Normalized letter stream for a book. |

---

### selah.text.locate

**File:** `src/selah/text/locate.clj`

**Purpose:** Map letter indices back to chapter/verse locations.

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `chapter-map` | `[book]` | Build a sorted vector of `{:chapter :start-idx :end-idx :letters}` |
| `idx->chapter` | `[ch-map idx]` | Given index, return the chapter entry |
| `locate` | `[book idx]` | Returns `{:book :chapter :local-pos}` |

---

### selah.text.variants

**File:** `src/selah/text/variants.clj`

**Purpose:** Cross-manuscript comparison using edit distance (Needleman-Wunsch alignment).

**Registered sources:** `:leningrad` (WLC, ~1008 CE) and `:mam` (modern reconstruction).

**Key function:**
- `edit-ops [a b]` -- Compute minimal edits between two letter vectors. Returns seq of `{:type :ins|:del|:sub, ...}`.

---

## Analysis Engines

### selah.gematria

**File:** `src/selah/gematria.clj`

**Purpose:** Hebrew letter values and numerical analysis. Standard gematria: aleph=1 through tav=400. Final forms carry the same values as their non-final counterparts.

**State:** None. Pure functions.

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `letter-value` | `[ch]` | int | Gematria value of a single letter (0 for non-Hebrew) |
| `word-value` | `[s]` | int | Sum of gematria values for a string |
| `stream-values` | `[letters]` | `[int]` | Convert letter stream to value vector |
| `total` | `[letters]` | int | Total gematria sum |
| `running-sum` | `[letters]` | lazy seq | Cumulative sum |
| `running-sum-mod` | `[letters n]` | lazy seq | Cumulative sum mod n |
| `stats` | `[letters]` | map | `{:total :count :mean :median :harmonic-mean :variance :std-dev}` |
| `letter-spectrum` | `[letters]` | `[map]` | Frequency x value for each letter |
| `zero-crossings-mod` | `[letters n]` | lazy seq | Positions where running sum mod n = 0 |
| `print-stats` | `[label letters]` | stats | Print formatted statistics |
| `print-spectrum` | `[letters]` | nil | Print the letter spectrum table |

**REPL:**
```clojure
(word-value "בראשית")   ;=> 913
(word-value "אלהים")    ;=> 86
(word-value "יהוה")     ;=> 26
```

---

### selah.space.coords

**File:** `src/selah/space/coords.clj`

**Purpose:** The kernel of the entire system. Maps 304,850 Torah letters into a 4D coordinate space.

```
304,850 = 7 x 50 x 13 x 67

Every letter has address (a, b, c, d):
  a in {0..6}   -- the seven days / completeness
  b in {0..49}  -- jubilee / ELS skip for Torah
  c in {0..12}  -- love (13) / one (13)
  d in {0..66}  -- understanding (67)

position = a*43,550 + b*871 + c*67 + d
```

**State:**
```clojure
(defonce ^:dynamic *dims* (atom [7 50 13 67]))   ;; The lens (factorization)
(defonce ^:dynamic *state* (atom nil))            ;; Cached space data
```

The `*state*` atom, once built, holds:
```clojure
{:stream    byte-array     ;; letter index (27 symbols) per position
 :values    int-array      ;; gematria value per position
 :running   long-array     ;; cumulative gematria sum
 :verses    int-array      ;; verse ID per position
 :verse-ref [map]          ;; verse ID -> {:book :ch :vs :start :end}
 :n         304850}
```

**Key functions:**

| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `dims` | `[]` | `[int]` | Current factorization, e.g. `[7 50 13 67]` |
| `set-dims!` | `[dims]` | nil | Change factorization (must multiply to 304,850) |
| `with-dims` | `[dims & body]` | any | Macro: temporarily use a different factorization |
| `idx->coord` | `[i]` | `long-array` | Position to `[a b c d]` |
| `coord->idx` | `[a b c d]` | long | `[a b c d]` to position |
| `build!` | `[]` | state | Load Torah, build all arrays, cache in `*state*` |
| `space` | `[]` | state | Return cached space (builds if needed) |
| `letter-at` | `[s i]` | char | Letter at position i |
| `gv-at` | `[s i]` | long | Gematria value at position i |
| `running-at` | `[s i]` | long | Cumulative sum through position i |
| `verse-at` | `[s i]` | map | `{:book :ch :vs}` for position i |
| `describe` | `[i]` | map | Full description: position, coord, letter, gematria, verse |
| `hyperplane` | `[axis value]` | `int[]` | All positions where axis = value |
| `fiber` | `[free-axis fixed]` | `int[]` | 1D line: fix 3 axes, vary one |
| `slab` | `[fixed]` | `int[]` | 2D slice: fix some axes, free others |
| `slice` | `[spec]` | `int[]` | General query (alias for slab) |
| `walk` | `[start axis step n]` | `int[]` | Walk along an axis (generalized ELS) |
| `preimage` | `[word]` | `[map]` | Find all a-fibers containing a word |
| `preimage-on` | `[axis word]` | `[map]` | Find fibers on any axis containing a word |
| `preimage-all` | `[words]` | `{word -> [map]}` | Scan all a-fibers for multiple words at once |
| `preimage-indexed` | `[words]` | `{word -> count}` | Fast preimage for large vocabularies (hash lookup) |
| `preimage-full` | `[words]` | `{word -> [map]}` | Fast preimage with full hit data |

**Lookup tables:**
- `letter-chars` -- byte index to Hebrew char (27 entries: 22 letters + 5 finals)
- `char->idx` -- Hebrew char to byte index
- `letter-gv` -- byte index to gematria value
- `idx-22` -- 27-symbol index collapsed to 22-letter index (finals merged)

**REPL:**
```clojure
(def s (space))
(letter-at s 0)        ;=> \ב (first letter of Torah)
(describe 0)           ;=> {:position 0, :coord [0 0 0 0], :letter \ב, ...}
(describe 152425)      ;=> center of Torah

;; Round-trip test
(every? #(= % (apply coord->idx (vec (idx->coord %))))
        (range total-letters))  ;=> true

;; Alternate lens
(with-dims [10 13 35 67]
  (describe 0))
```

---

### selah.space.project

**File:** `src/selah/space/project.clj`

**Purpose:** Projection from 4D coordinates to 2D/3D for visualization. Also provides color palettes and temporal frame splitting.

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `project-3d` | `[positions axes]` | `float[]` | Project to 3D. axes = `[:b :c :d]`. Returns `[x y z ...]` normalized to [0..1]. |
| `project-2d` | `[positions axes]` | `float[]` | Project to 2D. |
| `frames` | `[positions spatial temporal]` | `[{:t :positions}]` | Split positions into temporal frames along an axis. |
| `color-by-letter` | `[s positions]` | `float[]` | 27 hues for 27 symbols |
| `color-by-gematria` | `[s positions]` | `float[]` | Warm-to-cool by gematria value |
| `color-by-book` | `[s positions]` | `float[]` | 5 colors for 5 books |
| `color-by-day` | `[positions]` | `float[]` | 7 rainbow colors for 7 a-values |

---

### selah.space.export

**File:** `src/selah/space/export.clj`

**Purpose:** Write point clouds to disk in various formats.

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `write-json!` | `[path pos-floats color-floats]` | JSON array of `[x,y,z,r,g,b]` tuples |
| `write-binary!` | `[path pos-floats color-floats]` | Raw float32 with header (for WebGL) |
| `write-ply!` | `[path pos-floats color-floats]` | ASCII PLY (MeshLab/Blender compatible) |
| `write-frames!` | `[dir frames writer-fn project-fn color-fn space axes]` | Write temporal frames as numbered files |

---

### selah.oracle

**File:** `src/selah/oracle.clj`

**Purpose:** The breastplate oracle engine. Models the High Priest's breastplate (Urim and Thummim) as a computational device. 12 stones, 72 letters, 4x3 grid, four readers (traversal orders).

This is the single most important analysis namespace. Two directions: reverse (word -> which stones light up?) and forward (lit letters -> what can each reader see?).

**State:** None (stateless). Uses `dict` and `gematria` for lookups.

**Core data:**
```clojure
stone-data     ;; 12 stones: [stone-num letters row col]
stone-letters  ;; stone -> vec of chars
letter-index   ;; letter -> all [stone pos] positions
```

**Four readers** (four traversal orders of the 4x3 grid):
- **Aaron** -- looks down at his chest: rows R->L, top->bottom
- **God** -- faces Aaron from mercy seat (mirrored): rows L->R, bottom->top
- **Right cherub** -- columns R->L (nearest first), top->bottom
- **Left cherub** -- columns L->R (nearest first), bottom->top

YHWH = Yod(10)=right, He(5)=left, Vav(6)=Aaron, He(5)=God. 10+5+6+5=26.

**Key functions:**

| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `illumination-sets` | `[word]` | `[#{positions}]` | All distinct position-sets whose letters match the word's multiset |
| `preimage` | `[word]` | `[{:reader :positions :stones}]` | All (reader, position-set) pairs that produce the word |
| `read-positions` | `[reader positions]` | String | Read a set of positions in a reader's traversal order |
| `readings` | `[positions]` | `{:aaron :god :right :left}` | What does each reader see for a position-set? |
| `anagrams` | `[letters]` | `[{:word :meaning}]` | All dictionary anagrams (O(\|dict\|) frequency-map comparison) |
| `ask` | `[word]` | map | Reverse query: word -> oracle pre-image with all metadata |
| `forward` | `[letters vocab?]` | map | Forward query: lit letters -> all readings ranked by rarity |
| `forward-by-head` | `[letters vocab?]` | map | Per-reader ranked word lists |
| `question` | `[words]` | map | Multi-word query: illuminate each word, find coincidences |
| `parse-illumination` | `[positions opts?]` | `[{:phrase :text :meanings :gv}]` | Level 2 Thummim: partition lit positions into dictionary phrases |
| `parse-letters` | `[letters opts?]` | `[{:phrase :text :meanings :gv}]` | Level 2 Thummim on raw letters (no grid positions) |
| `thummim` | `[word opts?]` | map | Full Level 2: illuminate + parse all illumination patterns |
| `thummim-menu` | `[word opts?]` | map | Deduplicated phrase menu, sorted by word count |
| `rank-phrases` | `[phrases input-letters]` | `[map]` | Rank into tiers by reader agreement (SELF/UNANIMOUS/MAJORITY/SPLIT/SOLO/SILENT) |
| `gv-properties` | `[n]` | map | Number properties: Fibonacci, prime, axis divisors |

**Vocabulary levels** (settled, do not revisit):
- `:torah` (default) -- full Torah lexicon (~7,300 words)
- `:voice` -- ~2,050 words at the knee of the oracle's output distribution
- `:dict` -- 239 curated Torah words (tightest signal/noise)

**Tier system** (reader agreement):
| Tier | Name | Description |
|------|------|-------------|
| 0 | SELF | Single word reproducing the input (echo) |
| 1 | UNANIMOUS | All 4 readers see every word |
| 2 | MAJORITY | 3 readers agree |
| 3 | SPLIT | 2 readers agree |
| 4 | SOLO | Only 1 reader sees it |
| 5 | SILENT | No reader can produce all words |

**Oracle voice computation:**
```clojure
(compute-oracle-voice)  ;; Expensive (~2-4 min), cached to data/oracle-voice.edn
(oracle-voice)          ;; Load cached voice
(voice-vocab)           ;; Words at or above the knee (~2,050 words)
```

**REPL:**
```clojure
(ask "כבש")                           ;; lamb -> oracle pre-image
(forward "כבש" :torah)                ;; forward: what can readers see?
(thummim-menu "שלום")                  ;; Level 2: phrase assembly for "peace"
(parse-letters "אבבנרוח")             ;; Parse letters of father+son+spirit
(question ["כבש" "דם" "אש"])          ;; Multi-word coincidence query
```

---

### selah.basin

**File:** `src/selah/basin.clj`

**Purpose:** Basin of attraction dynamics. Feed the oracle to itself: word -> forward query -> highest-weight output -> repeat. Reveals fixed points, cycles, and attractor landscapes.

Three words never move: love, truth, life. The machine has never read John 14:6.

**State:** Lazy-loaded from experiment 096 data.
```clojure
;; Lazy atoms loaded from data/experiments/096/
word-idx     ;; word -> {:class :attractor :steps :gv :meaning}
attr-data    ;; attractors sorted by basin size
cycle-data   ;; cycle orbits
summary-data ;; summary statistics
```

**Key functions:**

| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `step` | `[word skip-self?]` | map | One oracle step: word -> highest-weight Torah output |
| `walk` | `[word max-steps opts]` | map | Walk until convergence, cycle, or max steps |
| `trace` | `[word max-steps]` | `[word1 word2 ... fixed-point]` | Simple path display |
| `landscape` | `[words]` | map | Batch: attractor landscape for many words |
| `step-all-heads` | `[word]` | map | One step for all four readers simultaneously |
| `classify-head` | `[step-map reader]` | map | Classify all words for one reader |
| `landscape-by-head` | `[words]` | map | Per-head basin classification (parallelized) |

**O(1) predicates** (from precomputed 096 data):
| Function | Description |
|----------|-------------|
| `word-class` | Classification: `:fixed-point`, `:transient`, `:cycle-member`, `:dead-end` |
| `fixed-point?` | Word maps to itself |
| `transient?` | Word flows to a fixed point |
| `cycle-member?` | Word is part of an orbit (period >= 2) |
| `dead-end?` | No illumination output |
| `converges?` | Reaches a fixed point (immediately or after transient) |

**Queries:**
| Function | Description |
|----------|-------------|
| `attractor-for` | The fixed point a word flows to |
| `basin-of` | Basin data for an attractor |
| `steps-to` | Number of steps to attractor |
| `attractors` | All attractors sorted by basin size |
| `largest-basins` | Top n attractors |
| `cycles` | All cycle orbits |
| `dead-ends` | Set of all dead-end words |
| `stats` | Summary statistics |

**REPL:**
```clojure
(step "כבש")                  ;; lamb -> ?
(trace "כבש")                 ;; path to convergence
(fixed-point? "אמת")          ;=> true
(attractor-for "כבש")         ;=> "שכב"
(largest-basins 5)
```

---

### selah.dict

**File:** `src/selah/dict.clj`

**Purpose:** Hebrew-English dictionary. Two vocabularies: 239 curated words with English translations, and ~7,300 unique word forms from the WLC text.

**State:**
```clojure
;; Lazy atom for full Torah vocabulary
(def ^:private torah-vocab (delay ...))
;; Lazy atom for machine translations
(def ^:private torah-english (delay ...))
```

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `words` | `[]` | `#{string}` | All 239 curated Hebrew words |
| `translate` | `[word]` | String or nil | Curated dictionary lookup |
| `known?` | `[word vocab?]` | boolean | Is word in the specified vocabulary? |
| `torah-words` | `[]` | `#{string}` | All ~7,300 unique Torah word forms (lazy) |
| `translate-english` | `[word]` | String or nil | Curated first, then machine-translated cache |

**Machine translations:** Loaded from `data/torah-english.edn` (generated by `dev/scripts/bulk_translate.clj` using the Hebrew->English ONNX model).

**Curated entries** include:
- Function words (at, asher, el, al, ki, kol, lo, etc.)
- Verbs (say, speak, make, give, see, hear, know, walk, create, guard, atone, etc.)
- People (YHWH, Elohim, Moses, Aaron, Israel, Abraham, etc.)
- Nature (land, heaven, water, fire, light, cloud, tree, blood, soul, spirit, lamb, etc.)
- Abstract/sacred (Torah, love, understanding, wisdom, truth, lovingkindness, peace, covenant, etc.)
- Tabernacle (tabernacle, altar, ark, cherub, tent, veil, mercy seat, etc.)
- Oracle vocabulary (drunk/shikrah, like-Sarah/kesarah -- the Eli/Hannah case from Yoma 73b)

---

### selah.translate

**File:** `src/selah/translate.clj`

**Purpose:** Bidirectional translation via MarianMT ONNX models. Forward: English -> Hebrew (77M params). Reverse: Hebrew -> English (200M params).

**State:**
```clojure
(defonce ^:dynamic *state* (atom {:loaded? false}))          ;; en->he
(defonce ^:dynamic *reverse-state* (atom {:loaded? false}))   ;; he->en
```

When loaded, each state contains: `{:env :encoder :decoder :tokenizer :spm :piece->id :id->piece :eos-token-id :pad-token-id :vocab-size :loaded?}`.

**Architecture:** Both models share the same parameterized inference kernel:
1. `model-tokenize` -- text to token IDs via SentencePiece
2. `model-run-encoder` -- encoder pass (OnnxTensor)
3. `model-decode-greedy` -- autoregressive greedy decoding (O(n^2))
4. `model-detokenize` -- token IDs back to text

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `load!` | `[model-dir?]` | `:ok` | Load en->he model (default `models/opus-mt-en-he`) |
| `load-reverse!` | `[model-dir?]` | `:ok` | Load he->en model (default `models/opus-mt-he-en`) |
| `loaded?` | `[]` | boolean | Is en->he model loaded? |
| `reverse-loaded?` | `[]` | boolean | Is he->en model loaded? |
| `translate` | `[text]` | String | English -> Hebrew |
| `translate-reverse` | `[text]` | String or nil | Hebrew -> English |
| `translate-batch` | `[texts]` | `[String]` | Batch en->he |
| `batch-translate-reverse` | `[words]` | `{he -> en}` | Batch he->en (sequential, OrtSession not thread-safe) |
| `ask` | `[english-text & opts]` | map | English -> Hebrew -> Oracle readings, ranked with English back-translations |

**REPL:**
```clojure
(load!)
(translate "Who are you?")        ;=> Hebrew string
(load-reverse!)
(translate-reverse "שלום")         ;=> "Peace" or similar
(ask "Who are you?")              ;=> ranked oracle readings with English
```

---

### selah.sweep

**File:** `src/selah/sweep.clj`

**Purpose:** Thummim sweep analysis -- Fibonacci structure in phrase counts. Loads experiment 094 data lazily and computes Fibonacci analysis on first call.

**State:** Lazy-loaded from `data/experiments/094/thummim-sweep.edn`.

**Key functions:**
| Function | Signature | Returns | Description |
|----------|-----------|---------|-------------|
| `fib?` | `[n]` | boolean | Is n a Fibonacci number? |
| `fib-index` | `[n]` | int or nil | Which F(k) is n? (1-based) |
| `sweep-results` | `[]` | `[map]` | All 12,826 word results from the sweep |
| `fibonacci-analysis` | `[]` | map | Complete Fibonacci analysis (cached) |
| `staircase-level` | `[f]` | map | Words at a specific Fibonacci phrase count |
| `word-detail` | `[word]` | map | Full sweep data for a specific word |
| `forced-readings` | `[]` | `[map]` | Words with exactly 1 phrase reading (unambiguous) |
| `synonyms` | `[]` | map | Oracle synonym groups (identical phrase sets) |
| `extreme-words` | `[n]` | `[map]` | Top N words by phrase count |

**Fibonacci analysis result:**
```clojure
{:total-words :illuminable :fib-phrase-words :double-fib-words :triple-fib-words
 :max-fib-phrase :max-fib-illum :staircase :gaps :doubles :triples}
```

---

### selah.linalg

**File:** `src/selah/linalg.clj`

**Purpose:** Thin Neanderthal wrapper. Handles column-major conversion so callers think in row-major. Native BLAS/LAPACK via Intel MKL underneath.

**State:** None.

**Key functions:**

| Function | Signature | Description |
|----------|-----------|-------------|
| `matrix` | `[rows]` | Build dge from seq of row vectors |
| `from-double-arrays` | `[arrs n]` | Convert vec of double-arrays to dge |
| `vec->dv` | `[xs]` | Clojure seq to Neanderthal double vector |
| `dge` | `[m n]` | Create m x n zero matrix |
| `to-vecs` | `[m]` | Extract all rows as Clojure vectors |
| `to-double-arrays` | `[m]` | Convert back to vec of double-arrays |
| `mat-mul` | `[a b]` | Matrix multiply |
| `mat-pow` | `[m p]` | Matrix power by repeated squaring (BLAS GEMM each step) |
| `row-normalize!` | `[m]` | Normalize rows to sum to 1 (stochastic matrix, in-place) |
| `eigendecomp` | `[m]` | Eigendecomposition: `{:values :imag :vectors}` |
| `svd` | `[m]` | SVD: `{:u :sigma :vt}` |
| `stochastic-eigendecomp` | `[m]` | Eigendecomp for stochastic matrices: `{:stationary :eigenvalues :vectors}` |
| `entry` / `entry!` | `[m r c]` | Get/set matrix entry |
| `copy` / `scal!` / `axpy!` | | Matrix operations |

---

### selah.spectral

**File:** `src/selah/spectral.clj`

**Purpose:** Spectral analysis of the Torah 4D space. The Torah is a function f: Z_7 x Z_50 x Z_13 x Z_67 -> Z. On a product of cyclic groups, the natural eigenbasis is the Discrete Fourier Transform.

Metaphor: Urim = which positions (spatial filter). Thummim = which frequencies (spectral filter). Together: the doubly-focused lens.

**State:** Memoized Fourier matrices per dimension.

**Key functions:**

**DFT operations:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `dft` | `[signal]` | 1D DFT via matrix multiply. Returns `[{:k :re :im :magnitude :power :phase}]` |
| `idft` | `[coeffs]` | Inverse DFT. Returns double-array. |
| `dft-batch` | `[signals]` | Batch 1D DFT of M signals (one GEMM call). |
| `dft-2d` | `[{:data :rows :cols}]` | 2D DFT via four matrix products. |
| `idft-2d` | `[coeffs rows cols]` | Inverse 2D DFT. |

**Spatial selection (Urim):**
| Function | Signature | Description |
|----------|-----------|-------------|
| `marginal` | `[s axis]` | Sum of gematria per hyperplane along axis |
| `marginal-mean` | `[s axis]` | Mean gematria per hyperplane |
| `marginal-2d` | `[s axis1 axis2]` | 2D marginal |
| `fiber-signal` | `[s free-axis fixed]` | Gematria values along a single fiber |
| `slab-signal` | `[s fixed]` | Gematria values from a 2D slab |

**Spectral selection (Thummim):**
| Function | Signature | Description |
|----------|-----------|-------------|
| `spectrum` / `spectrum-2d` | | Convenience wrappers |
| `power-spectrum` | `[coeffs]` | Just power values as double-array |
| `dc-component` / `non-dc` / `dominant-mode` | | Component selectors |
| `spectral-character` | `[coeffs]` | Summary: DC fraction, dominant k, etc. |

**Doubly-focused lens (Urim x Thummim):**
| Function | Signature | Description |
|----------|-----------|-------------|
| `fiber-spectrum` | `[s free-axis fixed]` | DFT of a single fiber |
| `slab-spectrum` | `[s fixed]` | 2D DFT of a slab |
| `hyperplane-power` | `[s fixed-axis val free-axis]` | Average power spectrum across all fibers in a hyperplane (batch DFT) |

**Filtering:**
| Function | Description |
|----------|-------------|
| `filter-modes` | Zero out modes not in keep-set |
| `low-pass` / `band-pass` / `high-pass` | Frequency filters |
| `reconstruct` | Reconstruct from modified coefficients |
| `residual` | Signal remaining after removing modes |

**Analysis:**
| Function | Description |
|----------|-------------|
| `axis-spectra` | All four 1D marginal spectra |
| `compare-spectra` | Compare spectral characters of all axes |
| `slab-fingerprint` | 2D DFT power distribution of a slab |

---

### selah.tensor

**File:** `src/selah/tensor.clj`

**Purpose:** Tensor operations on the 4D Torah space. Real and complex tensors stored as flat arrays with row-major addressing. Neanderthal for all 2D operations.

**State:** Memoized unfold indices per (shape, mode).

**Tensor format:**
```clojure
;; Real:
{:data double-array, :shape [d0 d1 d2 d3]}

;; Complex:
{:re double-array, :im double-array, :shape [d0 d1 d2 d3]}
```

**Key functions:**

| Function | Signature | Description |
|----------|-----------|-------------|
| `from-space` | `[s]` | Build real tensor from Torah space (copies gematria values) |
| `zeros` / `zeros-complex` | `[shape]` | Zero tensors |
| `mode-unfold` | `[tensor mode]` | Unfold along mode k into Neanderthal dge. Pre-computed index maps. |
| `mode-refold` | `[matrix mode shape]` | Inverse of mode-unfold |
| `n-mode-product` | `[tensor mode m]` | Tensor x_k matrix M |
| `real->complex` | `[tensor]` | Promote to complex (zero imaginary) |
| `complex-magnitude-sq` | `[ct]` | Element-wise \|z\|^2 |
| `complex-mode-unfold` / `complex-mode-refold` | | Complex variants |
| `hosvd` | `[tensor]` | Higher-Order SVD: `{:core :factors [U0..U3] :singular-values [s0..s3]}` |
| `dft-4d` | `[tensor]` | Full 4D DFT: 14 GEMM calls. Real -> complex. |
| `idft-4d` | `[ct]` | Inverse 4D DFT: complex -> real. |
| `power-spectrum-4d` | `[ct]` | Power spectrum \|F-hat\|^2 |
| `top-modes` | `[ps k]` | Top-k modes by power |
| `frobenius-norm` / `frobenius-norm-sq` | `[tensor]` | Norms |
| `cosine-similarity` | `[m1 j1 m2 j2]` | Cosine similarity between matrix columns |
| `factor-fourier-alignment` | `[u-k dim-k]` | HOSVD factor vs Fourier basis alignment matrix |

**REPL:**
```clojure
(def T (from-space (coords/space)))
(:shape T)  ;=> [7 50 13 67]

;; HOSVD
(def decomp (hosvd T))

;; 4D DFT round-trip
(let [ct (dft-4d T)
      rec (idft-4d ct)]
  ;; max error should be < 1e-8
  )
```

---

## Explorer UI

### selah.explorer

**File:** `src/selah/explorer.clj`

**Purpose:** Precomputes the full word census and provides query functions for the web explorer. Builds an index of all Torah words with frequency, gematria, preimage counts, cross-references.

**State:**
```clojure
(defonce ^:dynamic *state* (atom nil))
```

Once built, contains:
```clojure
{:words [enriched-word-maps]
 :by-word {word -> map}
 :by-gv {gv -> [maps]}
 :by-preimage {count -> [maps]}
 :by-freq-rank {word -> rank}
 :xref-index {n -> {:counted [...] :named [...]}}
 :total-words N
 :total-tokens N}
```

**Key functions:**
| Function | Signature | Description |
|----------|-----------|-------------|
| `build!` | `[]` | Precompute entire word census (~6 seconds) |
| `index` | `[]` | Get index (builds if needed) |
| `lookup-word` | `[word]` | Full word profile with cross-references |
| `search-words` | `[q]` | Search by prefix/substring |
| `level-words` | `[n]` | All words at a given preimage count |
| `gv-words` | `[n]` | All words with a given gematria value |
| `fiber-words` | `[b c d]` | Words found at coordinates (b, c, d) |
| `api-handler` | `[req]` | Handle explorer JSON API requests |

**Number theory utilities:** `prime?`, `fib?`, `fib-index`, `triangular-root`, `factorize`, `number-properties`.

---

### selah.explorer.ui

**File:** `src/selah/explorer/ui.clj` (1,259 lines)

**Purpose:** Hiccup views for the Torah Explorer. Server-rendered HTML with HTMX for SPA-like navigation. This is the largest single source file.

**Key view functions:**
| Function | Description |
|----------|-------------|
| `layout` | Full HTML page wrapper with CSS, HTMX, fonts |
| `fragment` | Bare HTML fragment (for HTMX partial updates) |
| `home-content` | Landing page with search, dictionary overview |
| `word-profile` | Full word profile: gematria, preimage, cross-references |
| `level-view` | All words at a preimage count level |
| `gv-view` | All words with a given gematria value |
| `search-results` | Search result list |
| `coincidence-view` | Multi-word coincidence analysis |
| `oracle-content` | Oracle landing page with tabs |
| `oracle-ask-result` | Reverse oracle query results |
| `oracle-illuminate-result` | Forward illumination results |
| `oracle-question-result` | Multi-word question results |
| `oracle-thummim-result` | Level 2 Thummim phrase menu |
| `oracle-translate-result` | English -> Hebrew -> Oracle results |
| `basin-content` | Basin explorer landing page |
| `basin-walk-result` | Basin walk visualization |

**Helper functions:**
- `word-link` -- HTMX-navigable word with hover translation
- `num-link` -- Clickable number for level/gv navigation
- `props-badges` -- Number property badges (Fibonacci, prime, divisors)

---

### selah.explorer.routes

**File:** `src/selah/explorer/routes.clj`

**Purpose:** All HTTP route definitions. Pure routing: parse params -> call domain -> build response. Single `handler` function dispatched by URI matching.

**Response helpers:** `html`, `fragment`, `json-ok`, `json-err`.

**Route groups are documented in the [HTTP API Reference](#http-api-reference).**

---

### selah.explorer.sweep-ui

**File:** `src/selah/explorer/sweep_ui.clj`

**Purpose:** Views for the Thummim Sweep explorer. The Fibonacci staircase visualization.

**Key views:**
| Function | Description |
|----------|-------------|
| `staircase-overview` | Main view: Fibonacci phrase counts as steps with bar charts |
| `step-detail` | Detail view for a single staircase step |
| `forced-view` | Words with exactly 1 phrase reading |
| `extremes-view` | Words with highest phrase counts |
| `distribution-view` | Distribution statistics |

---

## HTTP API Reference

All routes are defined in `src/selah/explorer/routes.clj`. The server listens on port 8099.

### Explorer (HTML)

| Route | Method | Description |
|-------|--------|-------------|
| `/` | GET | Home page |
| `/word/{word}` | GET | Word profile page |
| `/level/{n}` | GET | All words at preimage count n |
| `/gv/{n}` | GET | All words with gematria value n |
| `/search?q=` | GET | Search results page |
| `/coincidence?words=` | GET | Multi-word coincidence page |

### Oracle (HTML)

| Route | Method | Description |
|-------|--------|-------------|
| `/oracle` | GET | Oracle landing page |
| `/basin` | GET | Basin explorer page |
| `/sweep` | GET | Thummim sweep staircase page |
| `/sweep/step/{n}` | GET | Staircase step detail |
| `/sweep/forced` | GET | Forced readings |
| `/sweep/extremes` | GET | Extreme phrase counts |
| `/sweep/distribution` | GET | Distribution view |

### HTMX Fragments

Every HTML route has a `/fragment/` counterpart that returns bare HTML (no layout wrapper) for HTMX partial updates. For example:
- `/fragment/word/{word}` -- word profile fragment
- `/fragment/oracle/ask?word=` -- reverse oracle result
- `/fragment/oracle/illuminate?letters=` -- forward illumination
- `/fragment/oracle/thummim?word=` -- Level 2 Thummim menu
- `/fragment/oracle/translate?q=` -- English -> Hebrew -> Oracle
- `/fragment/basin/walk?word=&steps=&mode=` -- basin walk result

### JSON API

| Route | Params | Description |
|-------|--------|-------------|
| `/api/oracle/ask?word=` | `word` | Reverse oracle query |
| `/api/oracle/illuminate?letters=` | `letters` | Forward illumination |
| `/api/oracle/thummim?word=&vocab=&max_words=` | `word`, `vocab` (dict\|voice\|torah), `max_words` | Level 2 Thummim menu |
| `/api/oracle/parse-letters?letters=&vocab=&max_words=` | `letters`, `vocab`, `max_words` | Raw letter partition |
| `/api/oracle/voice` | -- | Oracle voice: knee, top-50, statistics |
| `/api/oracle/translate?q=&vocab=` | `q`, `vocab` | English -> Hebrew -> Oracle (requires model) |
| `/api/translate?q=` | `q` | English -> Hebrew only |
| `/api/translate/reverse?q=` | `q` | Hebrew -> English only |
| `/api/basin/walk?word=&steps=` | `word`, `steps` | Basin walk |
| `/api/basin/trace?word=&steps=` | `word`, `steps` | Simple path trace |
| `/api/basin/landscape?words=` | `words` (comma-separated) | Batch basin landscape |
| `/api/sweep/fibonacci` | -- | Full Fibonacci analysis |
| `/api/sweep/staircase` | -- | Staircase + gaps |
| `/api/word/{word}` | -- | Word profile (JSON) |
| `/api/search?q=` | `q` | Word search |
| `/api/level/{n}` | -- | Words at preimage count n |
| `/api/gv/{n}` | -- | Words with gematria value n |
| `/api/fiber/{b}/{c}/{d}` | -- | Fiber words at coordinates |
| `/api/stats` | -- | System statistics |

---

## MCP Tool Reference

MCP tools are defined in `src/selah/mcp/tools.clj`. Access via the MCP socket on port 7889, or through the stdio bridge `scripts/mcp-bridge.sh`.

| Tool | Parameters | Description |
|------|-----------|-------------|
| `eval` | `code` (string) | Evaluate arbitrary Clojure in the Selah runtime |
| `inspect` | `target` (string) | Inspect a namespace or var (e.g. `selah.http` or `selah.http/state`) |
| `status` | -- | Show system status: nREPL, HTTP, MCP running/stopped |
| `space-describe` | `position` (int) or `coord` ([a,b,c,d]) | Describe a Torah letter by position or coordinate |
| `space-slice` | `a`, `b`, `c`, `d` (optional), `limit` | Query the 4D space by fixing axes |
| `space-export` | `format`, `spatial`, `color`, `a/b/c/d`, `path` | Export a slice as point cloud (PLY/JSON/binary) |
| `space-preimage` | `word` (required), `axis` (a\|b\|c\|d) | Find all fibers containing a Hebrew word |
| `oracle-thummim` | `word` (required), `vocab`, `max_words` | Level 2 Thummim phrase assembly |
| `oracle-parse-letters` | `letters` (required), `vocab`, `max_words` | Raw letter partition into phrases |

---

## Dev Scripts

### dev/scripts/holy_of_holies.clj

**File:** `dev/scripts/holy_of_holies.clj`

**Purpose:** Spatial mapping of the Holy of Holies and its furniture in the 4D Torah coordinate space.

**Regions defined:**
- Holy of Holies: 10x10x10 cube centered at (3,25,6,33) = Lev 8:35
  - a=3, b=20..29, c=1..10, d=28..37
- The Ark: 5x3x3 (half-cubits: 2.5x1.5x1.5)
  - b=24..26, c=5..7, d=31..35
- Mercy Seat: top face of Ark (b=24..26, c=7, d=31..35)
- Right Cherub: b=24..26, c=7, d=31
- Left Cherub: b=24..26, c=7, d=35
- Between the Cherubim: b=24..26, c=7, d=32..34
- Above the Ark: b=24..26, c=8..10, d=31..35
- In Front: b=20..23, c=5..7, d=31..35

**Functions:** `hoh-positions`, `ark-positions`, `mercy-seat-positions`, etc. plus `region-info`, `describe-region`, `oracle-region`.

---

### dev/scripts/the_cross.clj

**File:** `dev/scripts/the_cross.clj`

**Purpose:** The cross at the center of the 4D Torah space. Understanding (67) x Love (13), nail (vav) at the intersection.

**Two beams through center (3,25,6,33):**
- Horizontal: d-axis (understanding, 67 letters) -- a=3, b=25, c=6, d=0..66
- Vertical: c-axis (love, 13 letters) -- a=3, b=25, d=33, c=0..12
- Spine: a-axis (completeness, 7 letters) -- b=25, c=6, d=33, a=0..6

**Functions:** `horizontal-beam`, `vertical-beam`, `spine`, `full-cross`, `mercy-seat-line`, `beam-info`, `nail-count`, `arm-gvs`, `endpoints`, `mercy-seat-words`.

**Findings:**
- 79 unique positions on the cross (prime)
- 13 vavs (nails) on the cross = love = one
- Horizontal beam GV=6070, arms 33+1+33 (symmetric)
- Vertical beam GV=622 = 2 x 311 = 2 x GV(man)
- Spine GV=73 = GV(wisdom)
- Mercy seat centerline contains YHWH at d=14..17, "by the hand of" at d=18..20, Moses at d=21..23
- Hand-to-fire distance = 14 = GV(hand)

---

### dev/scripts/bulk_translate.clj

**File:** `dev/scripts/bulk_translate.clj`

**Purpose:** One-time script to translate all ~7,300 Torah words Hebrew -> English using the reverse ONNX model. Output: `data/torah-english.edn`.

**Usage:** Load via nREPL: `(load-file "dev/scripts/bulk_translate.clj")`

---

## Data Directory Structure

```
data/
  cache/
    sefaria/              Cached Sefaria API JSON responses
  sources/
    oshb/
      wlc/                Westminster Leningrad Codex XML files
        Gen.xml, Exod.xml, Lev.xml, Num.xml, Deut.xml
  experiments/
    088/                  Stochastic oracle data
    089/                  Hebbian oracle data
    091b/                 Full Torah vocabulary quorum data
    092/                  Grid permutation test results
    092b/                 Ramban pair permutation test
    094/                  Thummim sweep (12,826 words)
      thummim-sweep.edn   Full sweep results
      synonyms.edn        Oracle synonym groups
      distributions.edn   Distribution data
    094b/                 Sweep extensions
    095/                  Boxes as coordinates
    096/                  Basin classification
      word-index.edn      word -> {class, attractor, steps}
      attractors.edn      Attractors by basin size
      cycles.edn          Cycle orbits
      summary.edn         Summary statistics
    097/                  Per-head basins
    098/                  Patriarchal coordinates
    099/                  Festival calendar path
    100/                  (and beyond)
    benchmarks.edn        Performance benchmarks
  oracle-voice.edn        Oracle output distribution (cached, ~2-4 min to compute)
  torah-english.edn       Machine-translated Hebrew -> English for ~7,300 words
  basin-landscape.edn     Basin attractor landscape data
  genomes/                DNA/genome analysis data (from experiments 043-049)
  texts/                  Additional text files
  conversations/          Stored conversation data
```

---

## Experiment Runner Pattern

Experiments live in `dev/experiments/` as numbered Clojure files: `NNN_descriptive_name.clj`.

**Pattern:**
1. Each experiment is a standalone namespace (e.g. `experiments.095-boxes-as-coordinates`)
2. It requires the relevant selah namespaces
3. All computation happens in `(comment ...)` blocks (REPL-driven)
4. Results are written to `data/experiments/NNN/` as EDN files
5. A corresponding doc is written to `docs/NNN-descriptive-name.md`

**Example structure:**
```clojure
(ns experiments.095-boxes-as-coordinates
  (:require [selah.space.coords :as c]
            [selah.gematria :as g]
            [clojure.java.io :as io]))

;; ... definitions ...

(comment
  ;; Initialize
  (def s (c/space))

  ;; Run analysis
  (def results ...)

  ;; Save
  (spit "data/experiments/095/results.edn" (pr-str results))

  ;; Inspect
  (count results)
  )
```

**Numbering:**
- 000-052: Early exploration (gematria, entropy, fractal, fold, center, ELS, genome)
- 053-067: Structural analysis (seven divisions, aleph-tav, folds, jubilee spine, grids)
- 068-085: Breastplate/oracle discovery (readings, fibers, cherubim, asking questions)
- 086-094: Oracle engine + analysis (engine, eigendecomposition, stochastic, HOSVD, quorum, sweeps)
- 095+: Applied analysis (boxes, basin classification, per-head basins, patriarchs, festivals)

---

## CLI Script

**File:** `selah` (Babashka script)

**Purpose:** Command-line interface for managing the Selah engine. Written in Babashka (`#!/usr/local/bin/bb`).

**Config:**
- PID file: `~/.selah/engine.pid`
- Log file: `~/.selah/engine.log`
- nREPL port file: `.nrepl-port` (project root)

**Commands:**

| Command | Description |
|---------|-------------|
| `selah engine start` | Start engine as background process (15s startup wait) |
| `selah engine stop` | Graceful shutdown (SIGTERM, then SIGKILL after 4s) |
| `selah engine restart` | Stop + start |
| `selah engine status` | Show PID, nREPL port, HTTP status |
| `selah start` | Start in foreground (blocking) |
| `selah test` | Run test suite via `clojure -X:dev:test` |
| `selah eval '<expr>'` | Evaluate Clojure via nREPL |
| `selah repl` | Connect an nREPL client |
| `selah mcp` | Start MCP stdio bridge |
| `selah help` | Show help |

**nREPL eval mechanism:** Uses Babashka's nREPL client to connect to the running engine and evaluate expressions.

---

## Dependencies

From `deps.edn`:

| Dependency | Version | Purpose |
|------------|---------|---------|
| `org.clojure/clojure` | 1.12.4 | Language |
| `org.clojure/data.json` | 2.5.1 | JSON parsing/generation |
| `http-kit/http-kit` | 2.8.0 | HTTP server |
| `nrepl/nrepl` | 1.3.1 | nREPL server |
| `org.clj-commons/clj-http-lite` | 1.0.13 | HTTP client (Sefaria API) |
| `hiccup/hiccup` | 2.0.0-RC3 | HTML generation |
| `uncomplicate/neanderthal` | 0.61.0 | Linear algebra (native BLAS/LAPACK) |
| `org.bytedeco/mkl-platform-redist` | 2025.3-1.5.13 | Intel MKL native libraries |
| `com.microsoft.onnxruntime/onnxruntime` | 1.22.0 | ONNX model inference |
| `ai.djl.sentencepiece/sentencepiece` | 0.36.0 | SentencePiece tokenizer |

**Aliases:**
- `:dev` -- adds `dev/` and `test/` to classpath
- `:run` -- main opts for `selah.main`
- `:test` -- Cognitect test-runner

---

## Architectural Patterns

### 1. State Atom Convention
Every stateful namespace owns a single atom:
```clojure
(defonce ^:dynamic *state* (atom {...}))
(defn state [] @*state*)
```
The `^:dynamic` allows REPL rebinding. The `defonce` prevents reload clobbering.

### 2. Lazy Construction
Heavy data structures are built on first access and cached:
```clojure
(defn space []
  (or @*state* (build!)))
```
Delays are used for optional/expensive resources:
```clojure
(def ^:private torah-vocab (delay ...))
(def ^:private oracle-voice* (delay ...))
```

### 3. Position-Based Queries
All spatial queries return `int[]` of positions. No objects, no maps -- just position arrays. Callers turn positions into whatever they need via `letter-at`, `gv-at`, `verse-at`, `describe`.

### 4. REPL-First Ergonomics
Every namespace ends with a `(comment ...)` block containing example invocations. The system is designed to be explored interactively. The `build!` / `space` / `index` pattern means you can just call a function and the system bootstraps itself.

### 5. Vocabulary Parameterization
The oracle, parse-letters, and thummim functions all accept a `:vocab` parameter:
- `:torah` -- full Torah lexicon (~7,300 words), the default
- `:voice` -- ~2,050 words at the knee of the oracle's output distribution
- `:dict` -- 239 curated words
- A custom set -- any set of Hebrew strings

Findings are vocabulary-invariant. This has been tested. The question is settled.

### 6. HTMX Fragment Pattern
Every HTML page route has a `/fragment/` counterpart. The full page includes layout + HTMX headers. Fragments are bare HTML for partial page updates. This gives SPA-like navigation with server rendering.

### 7. Experiment-as-Script
Experiments are not tests or production code. They are REPL scripts in `dev/experiments/` that produce data in `data/experiments/` and documentation in `docs/`. The system's library code (`src/selah/`) provides the primitives; experiments compose them.

### 8. Neanderthal for All Linear Algebra
All matrix operations go through `selah.linalg` (thin Neanderthal wrapper) which handles row-major to column-major conversion. DFT is implemented as matrix multiply via pre-computed Fourier matrices, getting BLAS GEMM performance. The tensor namespace uses mode-unfolding with cached index maps for O(1) element lookup.

### 9. The Oracle Metaphor
The breastplate is not just a metaphor -- it is the computational model:
- **Urim** (lights) = which positions illuminate (spatial selection)
- **Thummim** (perfections/completions) = how illuminated letters are read (traversal + cognitive assembly)
- **Level 1** = mechanical traversal by four readers
- **Level 2** = phrase assembly by the priest (backtracking partitioning)
- **Basin dynamics** = what happens when you feed the oracle to itself

### 10. Gotcha: Neanderthal + System.gc()
Do not call `System.gc()` after large Neanderthal native allocations. This can cause SIGSEGV. Let the JVM manage garbage collection naturally.

---

*This documentation covers every source file, every function of significance, every API endpoint, every MCP tool, and every architectural pattern in the Selah codebase. The land is exceedingly good.*
