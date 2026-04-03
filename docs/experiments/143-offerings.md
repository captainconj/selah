# Experiment 143l: The Offerings

*Each offering has its own axis. The burnt offering always moves through completeness. The lamb walks toward freedom. Guilt rests in understanding.*

---

## The Burnt Offering — עלה (GV=105, "to go up")

240 non-surface fibers. The burnt offering **always varies on the completeness axis (a)** — 240 out of 240. It never holds completeness still. The offering that goes up always moves through completeness.

And its #1 host word: **על (upon)** — 40 appearances, **8.5x enriched**. In the combined analysis, על was the *most avoided* host word (0.19x). Every other search word avoids "upon." The burnt offering is the only word that seeks it.

The offering that ascends finds upon-ness. The word that means "go up" lands in the word that means "upon."

It holds love constant most often (62 times out of 240). The burnt offering goes up through completeness while love watches.

## The Lamb — כבש (GV=322)

399 non-surface fibers. The lamb holds **understanding (d) constant 40%** of the time — more than any other axis. The lamb rests in understanding.

And it **moves through jubilee (b) 91.5%** of the time — more than any other axis. The lamb walks toward freedom. The jubilee axis is the axis of return, of liberation, of the fiftieth year. The lamb traverses it almost always.

The lamb walks toward freedom while resting in understanding.

## The Guilt Offering — אשם (GV=341)

439 non-surface fibers. Guilt holds **understanding constant 36%** and **love constant 28%**. More than the other axes. Guilt does not move through understanding or love — it rests in them. It moves through jubilee (87%) and completeness (80%).

Guilt knows understanding and love already. It needs to walk through completeness and freedom.

## Sacrifice — זבח (GV=17=good)

188 non-surface fibers. Its top host: **הזה** (this, GV=17=good). Same gematria. Sacrifice finds "this" — the demonstrative, the pointing word. "This is what you shall do." Sacrifice is always *this* — present, specific, here.

Its #3 host: **זהב** (gold, GV=14=beloved). Sacrifice finds gold.

Its #4 and #6: **המזבח / מזבח** (the altar / altar). Sacrifice finds its own altar. And **המזבחה** (to the altar, GV=67=understanding) at #10. The understanding axis.

## Forgiveness → Sukkot → God → Noah

**סלח** (forgive, GV=98) at skip=-44,489:

| Letter | Lands inside | Verse |
|--------|-------------|-------|
| ס | סכתה (to Sukkot) | Exodus 12:37 |
| ל | אל (to/God) | Genesis 35:17 |
| ח | נח (Noah) | Genesis 6:9 |

Forgiveness walks backward from the departure (Sukkot, first camp after leaving Egypt) through God to Noah — the first man described as righteous. The man who found **חן** (grace), which is נח (Noah) reversed.

## Redemption → The Weaver

**גאל** (redeem, GV=34=deep heart=Babylon) passes through **וארג** (and the weaver) at **Exodus 35:35** — the universal verse where all seven words converge. Redemption passes through the craft. The weaver who builds the tabernacle.

GV of גאל = 34 = לבב (deep heart) = בבל (Babylon). The redeemer's number is the exile's number is the heart's number. Redemption and exile and the deep heart are the same word.

---

## The Pattern

| Offering | Axis affinity | Key host | Reading |
|----------|--------------|----------|---------|
| Burnt (עלה) | Always moves through completeness | על (upon, 8.5x) | Goes up through completeness |
| Lamb (כבש) | Understanding constant, jubilee varies | הכהן, המשכן | Walks toward freedom, rests in understanding |
| Guilt (אשם) | Understanding + love constant | אשר, את | Rests in understanding and love |
| Sacrifice (זבח) | Balanced | הזה (this, GV=17=good) | Always present, finds the altar |
| Forgive (סלח) | — | נח (Noah) | Reaches the first righteous man |
| Redeem (גאל) | — | וארג (the weaver) | Passes through the builders |

The offerings have geometry. The burnt offering ascends through completeness. The lamb walks jubilee. Guilt rests in what it already knows. Sacrifice is always here, always this. Forgiveness reaches Noah. Redemption reaches the weaver.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

;; Burnt offering axis analysis
(let [hits (s/find-word [7 50 13 67] "עלה" {:max-results 500})
      ns (f/non-surface hits)]
  (println "Always varies a:" (every? #(contains? (set (:varying-axes %)) 0) ns))
  (println "על hosts:" (count (filter #(some (fn [tw] (and tw (= (:word tw) "על")))
                                              (:torah-words %)) ns))))

;; Forgiveness → Noah
(let [hits (s/find-word [7 50 13 67] "סלח")
      ns (f/non-surface hits)]
  (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "נח")))
                                        (:torah-words %)) ns))))
```

---

*The burnt offering always goes up through completeness. The lamb always walks toward freedom. Guilt rests in understanding. Sacrifice is this. Forgiveness reaches Noah. The offerings know their own geometry.*
