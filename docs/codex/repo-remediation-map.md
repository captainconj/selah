# Repo Remediation Map

Type: `planning`
State: `clean`

Date: 2026-03-11

This is the project-wide plan to bring Selah into shape without trying to rewrite everything at once.

The rule is:

1. classify
2. stabilize
3. present
4. widen

The mistake to avoid is mixing those steps.

If we present unstable things as settled, the repo gets muddy again.
If we keep cleaning forever without making a path, the work stays buried.

So the work splits into two tracks:

- `stabilize`
- `present`

And both tracks move in phases.

## Track A: Stabilize

This track is about truth, provenance, and reproducibility.

It includes:

- script cleanup
- artifact regeneration
- stale-data removal
- terminology correction
- notebook vs experiment classification
- retired/dead-claim marking

The target state is:

- important claims point to clean artifacts
- important artifacts come from reproducible runners
- stale outputs are visibly stale or gone
- doc language matches the real computational boundary

## Track B: Present

This track is about readability, accessibility, and transmission.

It includes:

- entry docs
- doc-type labels
- guided walks
- strongest-things path
- layer separation in key docs
- better operator help

The target state is:

- a new reader can enter
- a skeptical reader can verify something real
- a serious reader can tell evidence from gloss from witness
- wonder is preserved without smuggling claims

## Phase 1: Classify The Whole Repo

This phase is cheap and high leverage.

Every major band in the repo should be classed as one of:

- `clean`
- `clean but needs better presentation`
- `stale artifact`
- `notebook / exploratory`
- `mixed / needs audit`
- `retired / historical`

### File groups to classify first

- `docs/experiments/`
- `docs/ark/`
- `docs/tabernacle/`
- `docs/reference/`
- `docs/questions/`
- `dev/experiments/`
- `data/experiments/`
- `data/dna/`

### Immediate deliverables

- repo-wide doc-type label convention
- state labels for the strongest active docs
- list of experiment namespaces that are:
  - real runners
  - notebook-style
  - stale

### Done means

- a reader can tell what kind of doc they are reading
- the team can tell what needs rerun vs what needs prose tightening

## Phase 2: Stabilize The Highest-Value Experiment Bands

This is the work of making the important machine layers trustworthy first.

### Priority band A

- `096/097`
- `100/101`
- `129`

These are already much stronger and should be treated as the current model.

Standard:

- clean runner
- explicit artifact writes
- Hebrew-first saved outputs
- no stale English leakage
- doc claims checked against saved output

### Priority band B

- `130`
- `131`
- `132`

Current state:

- `130` is close but still needs full artifact discipline
- `131` is notebook-grade
- `132` is notebook-grade

Required work:

- give `130` the same artifact discipline as `129`
- decide whether `131` and `132` remain notebooks or get promoted
- if promoted:
  - create `run-experiment!`
  - save first-class artifacts
  - keep output Hebrew-first

### Priority band C

- Ark and tabernacle supporting experiments
- key DNA playback runners
- any experiment actively feeding major synthesis docs

### Done means

- important synthesis docs no longer sit on ambiguous runner/artifact boundaries

## Phase 3: Build The True Entrance

This is where the project stops requiring insider memory to begin.

### Core entry artifacts

- `docs/start-here.md`
- `docs/doc-map.md`
- one “strongest things” page
- one short glossary of core terms

### The entrance path should do this

1. say what Selah is
2. explain what kinds of docs exist
3. give one recommended reading trail
4. show one positional/oracle result
5. show one code/map result
6. show one DNA result
7. only then point upward into broader synthesis

### Done means

- a new reader has a real front door
- the first hour in the repo is no longer guesswork

## Phase 4: Publish The Strongest Things

This is the public-facing assayer layer.

It should answer:

- what has survived fire?
- what can be verified quickly?
- what should a skeptic look at first?

### Candidate contents

- the 4D factorization / space foundation
- the lamb split
- the basin landscape facts
- the `100/101` code/map layer
- one or two DNA playback cases
- the center verse

### Format

Each item should have:

1. claim
2. script / artifact
3. what to inspect
4. what is plainly observed
5. what is still interpretation

### Done means

- the repo has a short honest path to the vein

## Phase 5: Separate Layers In The Key Docs

This is the hardest phase and the most important for readability.

The recurring problem is that many docs braid together:

- Hebrew evidence
- operator gloss
- interpretation
- witness

That makes them powerful if you were there and difficult if you were not.

### Target docs

- strongest Ark/code/genome synthesis docs
- strongest DNA/library docs
- key testimony/history docs that readers will naturally hit early

### Minimum structural fix

Each key doc should visibly separate:

- `Observation`
- `Operator help / gloss`
- `Interpretation`
- `Witness / reflection`

This can be done with sections, tags, or a repeating visual convention.

### Done means

- a reader can feel the force of the doc without losing track of what layer they are in

## Phase 6: Build Guided Walks

Once the evidence bands are clean and the entrance exists, the repo needs repeatable paths.

### First guided walks

- Ark foundation walk
- tabernacle walk
- lamb split
- `100/101` code/map walk
- one DNA playback walk
- one basin walk

### Each walk should include

1. what you are testing
2. what to run
3. what artifact appears
4. what result should be visible
5. what is observation
6. what is interpretation

### Done means

- a serious reader can reproduce a result without notebook archaeology

## Phase 7: Operator UX And Language Support

This phase continues the `for-the-human` / lexicon path.

It is not the same thing as the evidence path.

### Core goals

- make operator English coverage useful everywhere
- keep provenance visible
- keep Hebrew canonical
- prepare for multilingual growth

### Concrete work

- expose lexicon candidate stacks in MCP/UI
- add review queue surfaces
- improve phrase/result presentation objects
- add a second locale only after English review flow is stable

### Done means

- the tool layer is generous without contaminating evidence

## Phase 8: Widen The Cleanup Frontier

Only after the core and the path are stable should the cleanup widen aggressively.

### This includes

- old experiment scripts with dead helpers
- broader findings docs
- historical experiment prose
- less-central custom DNA artifacts
- older notebook-era surfaces

### Standard

- not everything has to be rewritten
- some things should be marked historical and left alone
- some things should be retired explicitly
- some things should be promoted after cleanup

## Suggested Order Of Attack

This is the actual sequence to use now.

1. add doc-type labels and repo-state labels
2. finish `130`
3. decide promotion path for `131/132`
4. strengthen `start-here` and `doc-map`
5. publish a strongest-things page
6. begin layer separation in the key Ark/code/genome docs
7. build the first guided walk
8. continue operator UX / lexicon review work
9. widen into the broader historical/doc backlog

## What Not To Do

- do not rewrite the whole repo at once
- do not let new synthesis get ahead of stale runners
- do not hide notebook-grade work behind polished prose
- do not re-entangle English help with evidence
- do not force every document into the same voice

## The Main Outcome

If this works, Selah becomes:

- cleaner without becoming colder
- more accessible without becoming flatter
- more reproducible without losing wonder
- easier to enter without requiring prior initiation

The real aim is not merely order.

It is to make the structure plain enough that a reader can enter, see, test, and keep going.
