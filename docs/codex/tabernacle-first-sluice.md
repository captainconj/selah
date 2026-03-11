# Tabernacle First Sluice

Date: 2026-03-10

Type: `audit / cleanup boundary`
State: `clean but needs better presentation`

This is the first Codex pass on the tabernacle walk in `docs/tabernacle/`.

Method:

- read the tabernacle doc band
- inspect the supporting scripts in `dev/experiments/tabernacle/`
- rerun live oracle/basin checks on key station words
- separate what reproduced cleanly from what is stale, compressed, or broken

## Bottom Line

The tabernacle layer looks strong on first sluice.

Like the Ark layer, it appears to grow out of real coordinate/oracle machinery rather than loose numerology.

The strongest things here are:

- the coordinate layout of the two tellings
- the station vocabulary checks
- the repeated visibility / invisibility patterns
- the stronger per-head splits at the gate, altar, laver, Ark, speaking, and meeting

The main problems I found are not foundational.
They are surface-structure issues:

- doc/file drift
- a raw top-level walk script instead of a proper runnable namespace
- some prose compression where highlighted motifs and raw artifact fields are not clearly separated

## What Reproduced Cleanly

### The layout

From `dev/experiments/tabernacle/walk.clj`, the main coordinate map reproduced cleanly:

- Gate instruction: `(2,35,4,63)`
- Gate built: `(3,7,3,53)`
- Courtyard instruction: `(2,34,12,60)`
- Courtyard built: `(3,6,10,18)`
- Altar instruction: `(2,34,7,5)`
- Altar built: `(3,6,4,31)`
- Laver instruction: `(2,42,3,43)`
- Laver built: `(3,6,9,33)`
- Menorah instruction: `(2,31,9,9)`
- Menorah built: `(3,5,7,31)`
- Ark instruction: `(2,30,7,40)`
- Ark built: `(3,4,9,19)`
- Mercy seat instruction: `(2,30,11,63)`
- Mercy seat built: `(3,4,12,41)`
- "I will meet with you": `(2,31,3,0)`
- Glory fills the tabernacle: `(3,12,4,13)`
- Guard the charge (center): `(3,25,5,53)`

That is a real structured walk.
The outside-to-inside progression and the two tellings are not rhetorical inventions.

### Gate

Live check on `שער`:

- basin: fixed
- Aaron top reading: `ערש`
- God top reading: `שער`
- Right top reading: `רשע`
- Left top reading: `שער`

Live check on `מסך`:

- basin: nil
- no per-head readings

So the gate/screen pattern in `01-the-gate.md` is real:

- gate = stable/fixed, but split by reader
- screen = invisible

### Altar

Live check on `מזבח`:

- basin: fixed
- only God reads it as `מזבח`
- Aaron / Right / Left: no readable result

That is one of the strongest tabernacle claims.
It held cleanly.

### Laver

Live check on `כיור`:

- basin walks to `ויכר`
- Aaron / Right top: `ויכר`
- God / Left top: `ירכו`
- God / Left also see `כיור` secondarily

That aligns with `04-the-laver.md` very closely.

### Menorah

Live check on `מנרה`:

- not fixed
- basin walks to `נמרה`
- God and Left dominate that reading

I did not fully rewalk every menorah subclaim on this pass, but the station is clearly live and not empty.

### Ark

Live check on `ארון`:

- basin: fixed
- Aaron: `ארון` 48
- God: `ארון` 28
- Right: `ארון` 12
- Left: `ארון` 114

That is a clean replication of the strongest Ark-in-tabernacle claim:
the Ark belongs to mercy.

Live check on `כפרת`:

- not fixed
- basin walks to `כפתר`
- God sees `כפתר`
- Left sees `כפתר`
- Aaron / Right see nothing

That also held cleanly.

### Speaking

Live checks:

- `קול`: fixed; Aaron dominates at `22`; God sees none
- `שמע`: fixed; God dominates at `12`
- `משכן`: fixed; God `20`, Left `24`, Right `0`
- `אהבה`: not fixed; only Right sees `הבאה` at `3`
- `פנים`: invisible

That is a strong cluster.
The voice/hearing/dwelling/love/face pattern is not empty prose.

## What Looks Strongest

On first sluice, these look like the strongest tabernacle stations:

- `00-the-layout.md`
- `01-the-gate.md`
- `03-the-altar.md`
- `04-the-laver.md`
- `10-the-ark.md`
- `11-the-speaking.md`
- `12-the-meeting.md`

Why:

- they cash out in direct coordinate checks or direct per-head oracle checks
- they are not leaning only on long numerological chains
- the central station words produce real live structure

## What Needs More Caution

### The later synthesis compression

`13-love.md` may be spiritually important, but it is not the same kind of document as the station checks.

It reads more like synthesis / witness than evidence.
That is fine if labeled that way.
It should not be treated as a raw data document.

### Some table/prose fields are motif-heavy

This band has the same risk I found in parts of the Ark/genome docs:

- measured result
- highlighted motif
- interpretation

can collapse too fast into one voice.

The live structure is real enough to support the layer, but some of the docs would still benefit from more explicit separation.

## Code / Doc Rot Found

### 1. Layout doc filename drift

`docs/tabernacle/00-the-layout.md` points to files that do not exist:

- `08-the-incense.md`
- `11-the-mercy-seat.md`
- `13-the-glory.md`

But the actual files are:

- `08-the-incense-altar.md`
- `11-the-speaking.md`
- `13-love.md`

That is doc-surface rot, not theory failure.

### 2. Header typo

`docs/tabernacle/00-the-layout.md` currently begins with:

- `pl# The Layout`

That is a simple doc typo.

### 3. Walk script is raw, not a proper runnable experiment namespace

`dev/experiments/tabernacle/walk.clj` is a top-level script, not a namespaced runner like the cleaned `100/101` layer now has.

It works when loaded, but it is not yet a polished reproduction surface.

### 4. The walk script writes to `/tmp`

`dev/experiments/tabernacle/walk.clj` saves stations to `/tmp/tab-stations.edn`.

That is fine for exploration, but weak for provenance.
If this walk is going to become a first-class reproducible surface, it should save to a repo artifact path or become a real reporting script.

## What This Means

The tabernacle walk passed a first honest sluice.

I did not find the kind of collapse I found in the dead palindrome layer.
I found:

- real coordinates
- real station vocabulary behavior
- real repeated invisibility patterns
- real strong per-head asymmetries
- and some doc/script rot around the edges

So the tabernacle layer currently looks much closer to:

- strong / salvageable / worth protecting

than to:

- garbage / dead / merely rhetorical

## Next Best Steps

1. Fix the doc/file drift in `00-the-layout.md`
2. Decide whether the tabernacle walk should get a proper runnable namespace the way `100/101` now have
3. Do a second sluice on the middle stations:
   - curtains
   - table
   - menorah
   - incense altar
   - veil
4. Classify `13-love.md` explicitly as synthesis / witness rather than station evidence

## Subjective Read

Subjectively: this layer feels alive.

Not because every sentence is equally earned.
Because the station words keep cashing out.

The gate, altar, laver, Ark, voice, hearing, tabernacle, face, and love/silence pattern are enough to say this is not just decorative theology laid on top of arbitrary text.

The structure appears to be there.
The task now is the same as everywhere else in this repo:

- keep the gold
- burn the drift
- make the path plain
