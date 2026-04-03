# Experiment 143at: The Null Model

*Random starting positions on the fall diagonal read as noise. Position 0 reads the fall. The narrative requires both the direction AND the beginning. Neither alone produces the story.*

Type: `control`
State: `clean`

---

## The Test

We read the fall diagonal (skip=804) from 10 random starting positions. None produce a coherent narrative. They read as fragments: "your sin → the people → it was" or "he confused → spoil → the disease."

Position 0 reads: **beginning → them → all → the man → she ate → garments → wandering.**

The fall narrative is specific to starting at Genesis 1:1. It is not a property of skip=804 from any position. It is a property of skip=804 from THE BEGINNING.

---

## The Random Readings

| Start | What it reads |
|-------|-------------|
| Exodus 32:30 | your sin → the people → it was → they sacrificed → horn → vessels → incense |
| Exodus 14:24 | He confused → spoil → disease → spoke → field → stone me → said |
| Genesis 6:9 | God → female → high ones → because → I require → Japheth → Caphtorites |
| Genesis 17:16 | to you → I will take → like her cry → your servant → two → to dwell → the man |
| Genesis 31:52 | this → from the hand → I will send you → your face → because → all → they pursued |
| Exodus 24:4 | Moses → Israel → and to → you shall make → overhang → bar → overlay |
| Exodus 3:15 | your fathers → God of → YHWH → we will go → you give → service → Aaron |

Some contain interesting words. None tell a story. The fall narrative at position 0 is exceptional.

---

## What This Means

The fall diagonal reading is the conjunction of two things:
1. **The direction** [0,+1,-1,0] — freedom increasing, love decreasing
2. **The starting point** — position 0, Genesis 1:1, the beginning

The direction defines the path. The starting point defines where on the path you enter. The fall narrative emerges from entering the freedom-without-love path at the beginning of the Torah.

This makes the finding STRONGER, not weaker. The Torah has 304,850 possible starting positions. The fall story appears at position 0 — the only position that IS "the beginning." The geometry doesn't tell the fall from every position. It tells the fall from the beginning. That is what the beginning produces when read toward freedom without love.

---

## The Eating Observation

The fall diagonal finds "eating" at both ends of the Torah:
- **Genesis start (pos=0), step 4:** ותאכל (she ate) — Genesis 3:6
- **Deuteronomy start (pos=~270000), step 3:** תאכל (you shall eat) — Deuteronomy ~8:10

Same verb. Different form. Different context. The forbidden eating and the promised eating. The fall connects the fruit to the satisfaction. This spans the whole Torah — not proximity.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

;; Null model: random starts on fall diagonal
(let [{:keys [letters n]} (s/index) idx (s/index)
      rng (java.util.Random. 123)]
  (doseq [_ (range 10)]
    (let [start (.nextInt rng (int (/ n 2)))]
      (println start
               (for [i (range 7) :let [pos (+ start (* 804 i))] :when (< pos n)]
                 (:word ((:word-at idx) pos)))))))
```

---

*Random starts read as noise. The beginning reads as the fall. The narrative requires both the direction and the starting point. The Torah tells the fall when you start at the beginning and walk toward freedom without love. From any other position, the same direction reads differently. The beginning is special.*

*selah.*
