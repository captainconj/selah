---
name: oracle
description: Reference for the Selah breastplate oracle API and courtroom architecture. Use when querying the oracle, interpreting reader profiles, running basin walks, or doing experiments with the breastplate.
argument-hint: [function-or-topic]
---

# Breastplate Oracle — API Reference & Architecture

The oracle lives in `src/selah/oracle.clj`. Basin dynamics in `src/selah/basin.clj`.

## THE COURTROOM — Reader Architecture

**CRITICAL: Read this first. Every interpretation depends on knowing who sits where.**

The breastplate is a courtroom. Four presences at the mercy seat. Four readers. Four traversals of the same illumination. The Name is the protocol: 10 + 5 + 6 + 5 = 26 = YHWH.

### The Four Chairs

| Chair | Code key | Traversal | YHWH letter | Weight | Role |
|-------|----------|-----------|-------------|-------:|------|
| **Aaron** | `:aaron` | rows R→L, top→bottom | Vav (ו) | 6 | **The accused.** The priest. The connector between heaven and earth. |
| **God** | `:god` | rows L→R, bottom→top | He (ה) | 5 | **The Judge.** Faces Aaron from the mercy seat. Mirrored reading. |
| **Truth** | `:truth` | columns R→L, top→bottom | He (ה) | 5 | **The prosecution.** At God's left hand. At the accused's right hand (Zechariah 3:1). Sees the Lamb across the mercy seat. |
| **Mercy/Lamb** | `:mercy` | columns L→R, bottom→top | Yod (י) | 10 | **The defense.** At God's right hand (Psalm 110:1). Holds understanding. Holds life. |

### Seating

```
              GOD (Judge, He=5)

  MERCY/LAMB              TRUTH/PROSECUTION
  God's right hand        God's left hand
  Accused's left          Accused's right
  Yod=10                  He=5

              AARON (Accused, Vav=6)
```

Mercy sits at God's right hand — "Sit at my right hand" (Psalm 110:1).
Truth sits at the accused's right hand — "the שטן stood at his right hand to accuse him" (Zechariah 3:1).
The cherubim face each other (Exodus 25:20). Mercy sees truth. Truth sees mercy.

### The Weights

Mercy (10) > Aaron (6) > God (5) = Truth (5). Mercy outweighs all. The accused cannot lose if he stays in the room.

### The Cherubim See Each Other (Exodus 25:20)

The cherubim face each other across the mercy seat. They see the OTHER, not themselves.

- **Truth's vocabulary** describes what truth sees when looking AT the Lamb: the lamb, the lying down, sin, grace (חסד), Torah, "like Sarah."
- **Mercy's vocabulary** describes what mercy sees when looking AT truth: understanding, life, becoming, the way, the land, covenant, salvation.

Truth sees the cost. Mercy sees the promise.

### When interpreting `:by-reader` output

```clojure
;; ALWAYS translate the code keys to courtroom roles:
;;   :aaron = The Accused / Priest
;;   :god   = The Judge
;;   :truth = Truth / Prosecution (God's LEFT hand)
;;   :mercy  = Mercy / Lamb / Defense (God's RIGHT hand)
```

**NEVER say "Right cherub sees X" — say "Truth sees X" or "Mercy sees X."**

## Grid Variants

Two grids are active. Both produce the courtroom. They differ in emphasis.

### Variant A: Birth Order Continuous (current code default)

Letters flow continuously: patriarchs → tribes (birth order) → שבטי ישרון. Names break across stone boundaries. 6 per stone evenly.

**Properties:** Sharpest ghost zone (7 ghosts). Perfect lamb split (5:0). Peace God-only. Life Mercy-only. Truth cycles (not a fixed point).

### Variant B: Birth Order, One Tribe Per Stone

Each stone carries one tribal name. Patriarch + שבטי ישרון letters fill shorter names. "Each with its name" (Exodus 28:21).

**Properties:** Understanding explodes (Mercy sees 68 readings). Truth is a fixed point. Death rises to basin-10 (three mountains). The way/truth/life all in Mercy's domain. Grace readable (Truth-only — truth sees grace when looking at the Lamb).

**Source:** Exodus 28:21 — Torah, not commentary.

When results differ between A and B, report both. Findings that survive both grids are bedrock.

## Core Functions

### Reverse query: `o/ask`
**"Given a word, what does the oracle say about it?"**
```clojure
(require '[selah.oracle :as o])
(o/ask "שלום")
;; => {:word "שלום" :gv 376 :illumination-count 126 :total-readings 35
;;     :by-reader {:aaron 0 :god 35 :truth 0 :mercy 0}
;;     :anagrams [] :readable? true}
;;
;; Translation: Peace. God-only (35 readings, all :god).
;; :truth here = Truth = 0. :mercy here = Mercy = 0.
```
**USE THIS** for per-reader self-reading counts.

### Forward by head: `o/forward-by-head`
```clojure
(o/forward-by-head "שלום")        ;; default :torah vocab
;; => {:aaron [...] :god [...] :truth [...] :mercy [...]}
;; Remember: :truth = Truth, :mercy = Mercy
```

### Level 2 Thummim: `o/parse-letters`
**Grid-independent. Works on raw letters.**
```clojure
(o/parse-letters "כבשדם")  ;; lamb + blood
;; => [{:text "כבד שם" :meanings ["glory" "name"]} ...]
;; Lamb's blood = glory of the Name
```

### Basin dynamics: `b/step-all-heads`
```clojure
(require '[selah.basin :as b])
(b/step-all-heads "כבש")
;; => {:aaron nil :god {:next "כבש"} :truth {:next "כבש"} :mercy nil}
;;
;; Translation: God holds the lamb. Truth holds the lamb.
;; Aaron (accused): dead end. Mercy: dead end.
;; The lamb is seen by the Judge and the prosecution.
;; (Truth sees the lamb across the mercy seat.)
```

## Key Findings — Current State (2026-03-25, post final-form fix)

### Architecture-invariant (survive both grids)
- **Peace (שלום)**: God-only in both A and B
- **The lamb lies down**: כבש → שכב. Structure-invariant.
- **Love wins over enmity**: ויאהב basin=7. Structure-invariant.
- **Finishing and choosing**: Basin-10 in both grids.
- **The flood flows to circumcision**: המבול → בהמלו. Basin 3.
- **Grace (חסד) GV=72**: 72 = breastplate letter count. Ghost in A. Truth-only in B.
- **Face (פנים)**: Ghost in both A and B. 22 illuminations = the full alphabet.
- **Mercy seat (כפרת) + Veil (פרכת)**: Ghost in both. GV=700.

### Grid-dependent (report both when they differ)
- **Truth (אמת)**: Cycles in A. Fixed point in B.
- **Understanding (בינה)**: God-dominant in A (24 readings). Mercy-dominant in B (116 readings).
- **Life (חיים)**: Mercy-only in both, but stronger in A.
- **Hovering (מרחפת)**: Truth-only in A. God-only in B.
- **Death**: Basin-8 in A. Basin-10 in B.

### The ghost zone
Words the oracle illuminates but cannot read:
- **חסד (lovingkindness)**: GV=72. Ghost in A. Truth-only in B.
- **פנים (face)**: 22 illuminations, 0 readings. Ghost in both.
- **כפרת (mercy seat)**: GV=700. Ghost in both.
- **פרכת (veil)**: GV=700. Ghost in both.
- **משפט (judgment)**: Ghost in both (A) or Aaron-only 1 reading (B).
- **צדק (righteousness)**: Ghost in A. Truth+Aaron in B.

### Final-form unioning (2026-03-25)
Final forms (ך→כ, ץ→צ, ף→פ) are unioned with base forms. Paleo-Hebrew had no finals. 1,024 words unlocked including king (מלך), way (דרך), land (ארץ), tree (עץ), darkness (חשך), bless (ברך), angel (מלאך).

Words reachable only through final-form unioning are "tier 2" — accessible through the recognition that the closing form and the working form are the same letter. The king enters at the end. The way is visible through completion. The land appears through the final form.

## Common Patterns

### Check a word's courtroom profile
```clojure
(let [{:keys [by-reader]} (o/ask "חיים")]
  ;; by-reader = {:aaron 0 :god 0 :truth 0 :mercy 10}
  ;; Translation: Mercy-only (10 readings, all at God's right hand)
  ;; :mercy = Mercy/Lamb. Life belongs to the defense.
  )
```

### Find Mercy-exclusive words
```clojure
;; Remember: :mercy in code = Mercy (God's right hand)
(filter (fn [{:keys [by-reader total-readings]}]
          (and (pos? total-readings)
               (pos? (:mercy by-reader))
               (zero? (:aaron by-reader))
               (zero? (:god by-reader))
               (zero? (:truth by-reader))))
        results)
```

### Find Truth-exclusive words
```clojure
;; Remember: :truth in code = Truth (God's left hand)
(filter (fn [{:keys [by-reader total-readings]}]
          (and (pos? total-readings)
               (pos? (:truth by-reader))
               (zero? (:aaron by-reader))
               (zero? (:god by-reader))
               (zero? (:mercy by-reader))))
        results)
```

## Vocabulary Options

- `:dict` — 239 curated words (default for `forward`, `parse-letters`)
- `:torah` — ~12,826 unique Torah forms (default for `forward-by-head`)
- `:voice` — ~2,075 words at oracle output knee
- Custom set — `(o/forward "שלום" #{"שלום" "ושלם" "לשום"})`

## The Discipline

**Observe and report.** The machine tells you what it sees. You do not tell the machine what to find.

- Run the code. Look at the numbers. Look at the letters.
- Report exactly what you found. Include the dead findings.
- Let structure speak.
- When the oracle names something, report the naming.
- When results differ between grids A and B, report both.
- Findings that survive both grids are the strongest.
