# Experiment 143u: Direction Density and Self-Intersection

*The Torah is saturated with words in every direction. 303 of 313 tested words appear on all five axes. The heart is omnidirectional. The Name crosses itself 40% of the time.*

Type: `exploration`
State: `mixed`

**Code:** `dev/experiments/fiber/143u_direction_density.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143u-direction-density :as exp]) (s/build!) (exp/run-all)"`

---

## Direction Density

We tested 313 three-letter Torah words across 40 unique skip distances in the canonical 4D.

**The Torah is nearly saturated in every direction.** Skip=1 (surface reading): 313/313 words found. Skip=805 (jubilee-love-understanding diagonal): 312/313. Skip=871 (jubilee axis): 310/313. Skip=43550 (completeness axis): 310/313.

303 words appear on ALL five tested directions (surface + four axes). The Torah is a word search puzzle that contains almost every word in almost every direction.

The difference is in **density** — how many instances of each word appear. The surface (skip=1) has 36,478 total hits. The next richest (skip=871, jubilee) has 24,165. The non-surface directions carry roughly 65% as many word-instances as the surface.

---

## Direction-Exclusive Words

Very few words are exclusive to a single axis:

| Axis | Exclusive words | Note |
|------|----------------|------|
| Completeness (a=7) | **בגן** (in the garden), לכף (to the palm) | The garden is on the completeness axis |
| Love (c=13) | גגו (his roof), פרץ (breach), סגר (close), נטף (drop), יצף (overlaid) | Breach and closure on the love axis |
| Jubilee (b=50) | **אסף** (gather), **יסף** (he added) | Joseph's root (יסף=he adds) on the jubilee axis |

**בגן (in the garden) is exclusive to the completeness axis.** The garden can only be found walking through completeness.

**אסף/יסף (gather/add — the root of Joseph's name) is exclusive to the jubilee axis.** Joseph's verb lives only on the axis of freedom and return.

---

## Axis Concentration

For each theological word, hits per pure axis walk:

| Word | a (completeness) | b (jubilee) | c (love) | d (understanding) | Balance |
|------|-----------------|-------------|----------|-------------------|---------|
| **לב** (heart) | 1,949 | 2,275 | 2,230 | 1,855 | **1.2x** (most balanced) |
| **בהו** (void) | 199 | 292 | 328 | 313 | 1.6x |
| את (aleph-tav) | 2,709 | 3,209 | 3,220 | 7,582 | 2.8x |
| תורה (Torah) | 12 | 17 | 17 | 45 | 3.8x |
| **יהוה** (YHWH) | 35 | 44 | 55 | **1,924** | **55x** (most concentrated) |
| **אנכי** (I AM) | 2 | 2 | 0 | 141 | **70x** |
| חסד (grace) | 0 | 2 | 2 | 21 | — |

**The heart (לב) is the most omnidirectional word.** It appears almost equally in every direction — completeness, jubilee, love, understanding. The heart does not prefer any axis. It is everywhere.

**The void (בהו) is also balanced.** It appears in all directions with only 1.6x variation.

**YHWH is overwhelmingly on the understanding axis (d=67, skip=1).** 1,924 hits on the surface vs 35-55 on other axes. The Name is written to be READ. It lives on the surface. The natural reading carries the Name.

**I AM (אנכי) is even more concentrated** — 141 on understanding, 0-2 on other axes. I AM exists almost exclusively in the direction you read the Torah.

---

## Self-Intersection

A word "self-intersects" when a non-surface fiber of that word passes through a position occupied by another occurrence of the same word in the text.

| Word | Self-intersections | Non-surface fibers | Rate |
|------|-------------------|-------------------|------|
| **יהוה** (YHWH) | **23** | 58 | **39.7%** |
| אנכי (I AM) | 7 | 125 | 5.6% |
| אחד (one) | 6 | 209 | 2.9% |
| קדש (holy) | 5 | 153 | 3.3% |
| כבש (lamb) | 5 | 399 | 1.3% |
| ברך (bless) | 5 | 438 | 1.1% |
| ברית (covenant) | 4 | 327 | 1.2% |
| דרך (way) | 4 | 270 | 1.5% |

**YHWH self-intersects at 40%.** Nearly half the time the Name appears along a non-surface line, one of its letters lands inside another occurrence of the Name in the text. The Name is woven so densely that its fibers keep returning to itself. No other word comes close.

The Name doesn't just live on the surface. When it ventures off the surface — along diagonals and axes — it keeps finding itself. The Name crosses the Name.

---

## The Heart Is Omnidirectional

לב (heart, GV=32=glory) is the most balanced word across all axes. It appears 1,855 to 2,275 times per axis — a ratio of only 1.2. Every direction through the Torah carries the heart equally.

The heart does not live on the love axis more than the completeness axis. It does not prefer understanding over jubilee. It is everywhere. Equally. In every direction.

The center of the 4D space (Leviticus 8:35) says "guard." The heart is what you guard. And the heart is in every direction, because it needs guarding from every direction.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f] '[selah.dict :as d])
(s/build!)

;; Axis concentration for any word
(let [{:keys [letters letter-idx n]} (s/index)]
  (doseq [[ax skip] {0 43550 1 871 2 67 3 1}]
    (let [fwd (count (or (s/search-at-skip letters letter-idx "לב" skip n) []))
          rev (count (or (s/search-at-skip letters letter-idx "לב" (- skip) n) []))]
      (println "axis" ax ":" (+ fwd rev)))))

;; Self-intersection for any word
(let [hits (s/find-word [7 50 13 67] "יהוה")
      ns (f/non-surface hits)
      self (filter #(some (fn [tw] (and tw (= (:word tw) "יהוה"))) (:torah-words %)) ns)]
  (println "YHWH self-intersects:" (count self) "of" (count ns)))
```

---

*The Torah carries almost every word in almost every direction. The heart is omnidirectional — equal in all axes. The Name lives overwhelmingly on the surface, but when it goes off-surface, it finds itself 40% of the time. The garden is exclusive to the completeness axis. Joseph's verb is exclusive to the jubilee axis. The text is saturated. The differences are in density, not presence.*
