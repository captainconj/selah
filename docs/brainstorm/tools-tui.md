# Selah TUI — Bible Explorer

*A terminal-based Bible reader built for structural analysis.*

---

## Why

I can code but can't read Hebrew, Greek, or Aramaic. I need a tool that:
- Shows me the original text with transliteration and translation inline
- Lets me navigate fast with keyboard shortcuts
- Shows gematria, stream position, and structural annotations
- Compares manuscript variants side by side
- Highlights ELS hits, את markers, and other patterns we discover
- Is REPL-connected so I can run analysis from inside the reader

---

## Stack

| Layer | Choice | Why |
|-------|--------|-----|
| TUI framework | **Lanterna** (Java) | Pure Java, clean Clojure interop, Unicode support, mature |
| Data | Sefaria API + cached | Hebrew text, already built |
| Greek NT | Tischendorf + SBLGNT + WH + TR | Already parsed in `selah.greek.parse` |
| Translation | ESV / KJV / interlinear | Need to acquire, align verse-by-verse |
| Transliteration | Computed from Hebrew/Greek | Algorithmic, no external dependency |
| 3D visualization | **LWJGL / OpenGL** | Separate tool, shared data layer, for cylinder/grid work |

---

## Layout

### Main view — verse reader

```
┌──────────────────────────────────────────────────────────────┐
│ Genesis 1:1                                     [MT] [SP] [+]│
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  בְּרֵאשִׁית בָּרָא אֱלֹהִים אֵת הַשָּׁמַיִם וְאֵת הָאָרֶץ      │
│  bereshit   bara  elohim   et  hashamayim ve'et  ha'aretz    │
│  In-beginning created God  ··  the-heavens and-·· the-earth  │
│                                                              │
│  ── annotations ──────────────────────────────────────────── │
│  את markers: 2 (positions 10, 22)                            │
│  gematria: 913+203+86+401+395+401+296 = 2701 = 37 × 73      │
│  words: 7  │  letters: 28 = 4×7  │  stream pos: 0–27        │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│ [j/k] verse  [h/l] chapter  [g] goto  [/] search  [?] help  │
│ [v] variants  [e] ELS  [a] aleph-tav  [m] gematria  Gen 1:1 │
└──────────────────────────────────────────────────────────────┘
```

### Variant comparison view ([v])

```
┌──────────────────────────────────────────────────────────────┐
│ Genesis 1:1 — Variant Comparison                             │
├──────────────────────┬───────────────────────────────────────┤
│ Masoretic (MT)       │ Samaritan (SP)                        │
│                      │                                       │
│ בראשית ברא אלהים     │ בראשית ברא אלהים                      │
│ את השמים ואת הארץ     │ את השמים ואת הארץ                      │
│                      │                                       │
│ ✓ identical          │ ✓ identical                            │
├──────────────────────┴───────────────────────────────────────┤
│ Differences in this chapter: 0                               │
│ [←/→] navigate diffs  [q] back                               │
└──────────────────────────────────────────────────────────────┘
```

### ELS overlay view ([e])

```
┌──────────────────────────────────────────────────────────────┐
│ ELS Overlay — Genesis, skip +50, word: תורה                  │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ...בראשי[ת]ברא אלהים את השמים ואת הארץ והארץ היתה ת...     │
│         ↑                                                    │
│         ת (pos 5) ─── 50 letters ──→ ו (pos 55) ─── 50 ──→  │
│                                      ר (pos 105) ─── 50 ──→ │
│                                      ה (pos 155)            │
│         = תורה (Torah, forward, skip +50)                    │
│                                                              │
│  Hit 1 of 3 │ Chapter: Genesis 1 │ Context: Creation         │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│ [n/p] next/prev hit  [s] change skip  [w] change word       │
│ [g] grid view  [q] back                              Gen 1:1│
└──────────────────────────────────────────────────────────────┘
```

### Grid view ([g] from ELS overlay)

```
┌──────────────────────────────────────────────────────────────┐
│ Grid View — Genesis, width 50                                │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  row 0:  ב ר א ש י [ת] ב ר א  א ל ה י ם  א ת  ה ש מ ...  │
│  row 1:  ה א ר ץ  ו ה א ר ץ  ה י ת ה  ת ה [ו]  ו ב ה ...  │
│  row 2:  ... ל ה ט  א ל ה י ם  ר ו ח  א ל ה י ם  מ [ר] ... │
│  row 3:  ... ה מ י ם  א ש ר  מ ע ל  ל ר ק י ע  ו י [ה] ... │
│          ↓         ↓              ↓              ↓           │
│          ת         ו              ר              ה           │
│          = תורה reading DOWN the column (vertical = skip 50) │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│ [↑↓←→] scroll  [w] change width  [d] diagonals  [q] back   │
│ [c] color mode  [h] highlight word                   Gen 1  │
└──────────────────────────────────────────────────────────────┘
```

---

## Keyboard Navigation

### Global
| Key | Action |
|-----|--------|
| `j` / `k` | Next / previous verse |
| `h` / `l` | Previous / next chapter |
| `g` | Goto (book chapter:verse) |
| `/` | Search (Hebrew, English, or transliteration) |
| `?` | Help overlay |
| `q` | Back / quit |
| `:` | REPL command (execute Clojure expression) |
| `1`-`5` | Jump to book (Gen, Exod, Lev, Num, Deut) |

### View toggles
| Key | Action |
|-----|--------|
| `v` | Variant comparison |
| `e` | ELS overlay |
| `a` | Highlight את markers |
| `m` | Gematria annotations |
| `t` | Transliteration on/off |
| `n` | Notes / annotations panel |

### ELS mode
| Key | Action |
|-----|--------|
| `s` | Set skip value |
| `w` | Set word to search |
| `n` / `p` | Next / previous hit |
| `g` | Switch to grid view |
| `r` | Reverse direction |

### Grid mode
| Key | Action |
|-----|--------|
| `↑↓←→` | Scroll |
| `w` | Change grid width |
| `d` | Toggle diagonal highlighting |
| `c` | Color mode (gematria heatmap, ELS highlights, etc.) |
| `h` | Highlight occurrences of a word |

---

## Data Requirements

### What we have
- Hebrew Pentateuch letter streams (Sefaria, cached)
- Hebrew verse text with nikkud (Sefaria `he` field, cached)
- Greek NT parsed text (4 variants, with morphology and lemmas)
- Chapter/verse location mapping
- ELS search engine

### What we need to acquire

| Data | Source | Format | Notes |
|------|--------|--------|-------|
| English translation | ESV API or public domain KJV | Verse-aligned JSON | KJV is public domain, easiest |
| Transliteration mapping | Algorithmic | Code | Hebrew letter → romanization table |
| Samaritan Pentateuch | Academic sources / digitized | Text | For variant comparison |
| Strong's concordance | Public domain | JSON/XML | Word-level Hebrew → English mapping |
| Word boundaries | Sefaria verse text | Parse from spaced Hebrew | We have letters; need word segmentation |

### Priority
1. **KJV English text** — public domain, verse-aligned, widely available
2. **Transliteration** — trivial to implement algorithmically
3. **Word segmentation** — parse from Sefaria's spaced Hebrew text
4. **Strong's concordance** — word-level translation, root meanings

---

## Architecture

```
selah.tui.core          — main entry point, screen setup, event loop
selah.tui.views         — view rendering (verse, variant, ELS, grid)
selah.tui.nav           — navigation state machine
selah.tui.keys          — keymap, key event handling
selah.tui.data          — data access layer (wraps existing text/ELS tools)
selah.tui.render        — Lanterna rendering helpers (text, boxes, colors)
selah.tui.transliterate — Hebrew/Greek → romanization
selah.tui.english       — English translation access
```

### REPL integration
The TUI should be launchable from the REPL:
```clojure
(require '[selah.tui.core :as tui])
(tui/start)          ;; opens TUI in terminal
(tui/goto "Gen" 1 1) ;; navigate programmatically
(tui/stop)           ;; close TUI, back to REPL
```

And from the TUI, `:` opens a REPL prompt where you can evaluate Clojure expressions against the current context:
```
: (selah.gematria/verse-value current-verse)
=> 2701
```

---

## 3D Cylinder Viewer (Separate Tool)

For the cylinder/scroll visualization, a separate OpenGL application:

| Feature | Description |
|---------|-------------|
| Text on cylinder | Torah text wrapped on a 3D cylinder, scrollable |
| Adjustable parameters | Cylinder radius, pitch (letters per turn), rotation |
| Helix highlighting | Show ELS patterns as colored helices on the surface |
| Layer view | Toggle "rolled up" view showing radially adjacent letters |
| Split view | Two cylinders (two rollers) with Leviticus at the reading point |

Stack: **LWJGL** (OpenGL) + Clojure. Existing LWJGL + Clojure work in `~/Projects/loom` — reuse rendering pipeline, interop patterns, and input handling from the Loom spatial OS project. Could also consider a web-based **Three.js** viewer for easier sharing.

This is a Phase 2 tool. The TUI comes first.

---

## Build Order

1. **`selah.gematria`** — letter values, sums, statistics (unlocks Tier 1 hypotheses)
2. **`selah.tui.core`** — basic Lanterna screen, verse display, navigation
3. **`selah.tui.transliterate`** — Hebrew romanization
4. **English translation data** — acquire and align KJV
5. **`selah.tui.views`** — verse view with three layers (Hebrew, translit, English)
6. **`selah.tui.keys`** — vim-like navigation
7. **Annotations** — gematria, stream position, את markers
8. **ELS overlay** — highlight hits in context
9. **Grid view** — 2D text wrapping with vertical/diagonal scanning
10. **Variant comparison** — side-by-side manuscript diff
11. **3D cylinder** — OpenGL viewer (Phase 2)
