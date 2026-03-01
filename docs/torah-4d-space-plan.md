# Torah 4D Space — Navigation, Projection, and Point Cloud Export

## Context

304,850 = 7 × 50 × 13 × 67. Every Torah letter has a unique 4-tuple address `(a, b, c, d)` via mixed-radix decomposition. We want to navigate this as a database, project into 2D/3D, animate through time, and export point clouds for GPU shaders.

## Core Insight: Letters Are Numbers

There are only 27 distinct Hebrew letters (22 + 5 final forms). Map each to an index 0–26. The Torah becomes a single `byte[]` of 304,850 values. Everything else is a lookup table over those 27 values:

```clojure
;; 22 letters + 5 final forms = 27 symbols, fits in 5 bits
;; Store as bytes for simplicity (300KB), but could bitpack to ~190KB

;; Two index modes:
(def alphabet-27 [א ב ג ד ה ו ז ח ט י כ ל מ נ ס ע פ צ ק ר ש ת ך ם ן ף ץ])
;;                 0 1 2 3 4 5 6 7 8 9 ...                                  26

(def alphabet-22 [א ב ג ד ה ו ז ח ט י כ ל מ נ ס ע פ צ ק ר ש ת])
;;                 0 1 2 3 4 5 6 7 8 9 ...                          21
;; finals collapse: ך→כ(10) ם→מ(12) ן→נ(13) ף→פ(16) ץ→צ(17)

;; Lookup tables (27 entries for symbol mode, 22 for collapsed mode)
idx->char       ;; 0 → \א, 1 → \ב, ...
idx->gematria   ;; 0 → 1, 1 → 2, ..., 22 → 20 (ך=כ)
idx->ordinal    ;; 27-symbol → 22-letter (collapses finals)
```

The Torah itself: one `byte[]` — 304,850 bytes, ~300KB (or bitpacked at 5 bits/letter = ~190KB). Coordinates are pure arithmetic on position. No per-point maps. No objects. Just arrays and lookup tables.

## Data Structures

```clojure
;; The space — fits in a cache line's worth of pointers
{:stream    byte[]          ;; 304,850 letter indices (0-26)
 :values    int[]           ;; 304,850 gematria values (precomputed from stream)
 :running   long[]          ;; 304,850 cumulative gematria sums
 :verses    int[]           ;; 304,850 verse IDs (which verse each letter belongs to)
 :verse-ref vec             ;; verse-id → {:book :ch :vs} (small — ~5,800 entries)
}
```

Total memory: ~2.5MB of primitives. Everything else computed on the fly.

## Namespace Structure

```
src/selah/space/
  coords.clj    — address arithmetic + space construction
  project.clj   — projection + color + normalization
  export.clj    — JSON, binary float32, PLY output
```

## Step 1: `selah.space.coords` — The Kernel

**State**: `(defonce ^:dynamic *state* (atom nil))` — the space map above, built on first access.

**Lookup tables** (def'd constants, 27 entries):
- `letter-chars` — byte index → Hebrew char
- `char->idx` — Hebrew char → byte index
- `letter-gv` — byte index → gematria value

**Address arithmetic** (pure functions, no state):
- `idx->coord [i]` → `[a b c d]` via divmod chain
- `coord->idx [a b c d]` → `(+ (* a 43550) (* b 871) (* c 67) d)`
- Constants: `day-stride` = 43550, `jubilee-stride` = 871, `one-stride` = 67

**Space construction**:
- `build!` — loads Torah, builds all arrays, caches in `*state*`
- `space` — returns cached space, builds if needed

**Accessors** (work on the space):
- `letter-at [s i]` → char (via `letter-chars` lookup on `stream[i]`)
- `gv-at [s i]` → int (via `values[i]`)
- `verse-at [s i]` → `{:book :ch :vs}` (via `verses[i]` → `verse-ref`)
- `describe [s i]` → full map for REPL inspection (the only place we build a map)

**Slicing** (returns int arrays of positions, not point objects):
- `slice [spec]` → `int[]` of matching indices
    - `{:a 3 :c 7}` iterates free axes, computes indices directly
- `hyperplane [axis value]` → `int[]`
- `fiber [free-axis fixed]` → `int[]`
- `slab [fixed]` → `int[]`
- `walk [start axis n]` → `int[]` (ELS generalized — walk along any axis)

All query results are position arrays. Apply lookup functions to get letters, values, etc.

```clojure
;; REPL usage
(def s (space))
(def center (hyperplane :a 3))           ;; int[] of 43,550 positions
(map #(letter-at s %) (take 10 center))  ;; first 10 letters of center seventh
(map #(gv-at s %) (fiber :d {:a 0 :b 0 :c 0}))  ;; gematria along one understanding line
```

## Step 2: `selah.space.project` — Projection + Color

Works on position arrays from `coords`. Produces float arrays for export.

**Projection** — maps position → spatial coords:
- `project-3d [positions [ax ay az]]` → `float[]` of `[x y z x y z ...]` (interleaved, 3 floats per point)
- `project-2d [positions [ax ay]]` → `float[]` of `[x y x y ...]`
- Coordinates normalized to `[0..1]` by dividing by axis max

**Temporal** — splits positions into frames:
- `frames [positions spatial temporal]` → vec of `{:t n :positions int[]}`
    - `:a` as time = 7 frames, `:b` = 50, `:c` = 13, `:d` = 67

**Color** — maps position → color via lookup tables:
- `color-by-gematria [space positions]` → `float[]` of `[r g b r g b ...]`
    - log scale, 27 precomputed colors for the 27 letter-values
- `color-by-letter [space positions]` → `float[]` — 27 hues
- `color-by-book [space positions]` → `float[]` — 5 colors
- `color-by-day [positions]` → `float[]` — 7 colors from coordinate :a

All color functions produce packed `float[]` arrays. The precomputed color palettes are just 27-entry or 5-entry lookup tables.

## Step 3: `selah.space.export` — Point Cloud Output

Takes float arrays from `project`, writes files.

- `write-json! [path pos-floats color-floats]` — JSON array of `[x,y,z,r,g,b]` tuples. For Three.js.
- `write-binary! [path pos-floats color-floats]` — raw float32 with header. For WebGL `BufferGeometry`.
- `write-ply! [path pos-floats color-floats]` — ASCII PLY. For MeshLab/Blender.
- `write-frames! [dir frames fmt]` — numbered files from temporal projection.

Binary layout: `[uint32 count] [uint32 floats-per-vertex] [float32 × count × fpv]`
Full Torah at 7 floats/vertex = ~8.5MB.

## Step 4: MCP tools

Add to `selah.mcp.tools`:
- `space-describe` — look up a position or coord, get full info
- `space-slice` — query by spec, return summary + first N results
- `space-export` — project + color + write file, return path

## Verification
1. Round-trip: `(= i (apply coord->idx (idx->coord i)))` for all i
2. `(alength (:stream (space)))` = 304,850
3. `(count (hyperplane :a 0))` = 43,550
4. `(count (fiber :d {:a 0 :b 0 :c 0}))` = 67
5. Letter recovery: `(letter-at s 0)` = \ב (first letter of Torah)
6. ELS: `(map #(letter-at s %) (walk 5 :d 1 4))` recovers תורה
7. Export hyperplane as PLY, verify in viewer
