# Experiment 143al: The Fall on the Freedom-Love Diagonal

*The direction that trades love for freedom reads the fall. Command → she ate → garments of skin → wandering. The cost of freedom without love is exile.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143al_the_fall_diagonal.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143al-the-fall-diagonal :as exp]) (s/build!) (exp/run-all)"`

---

## The Direction

[0, +1, -1, 0] — jubilee ascending, love descending. Completeness and understanding hold still. Skip = 804.

This diagonal walks toward freedom (jubilee) while releasing love. It is one of the 40 directions in the canonical 4D. It reads every 804th letter from position 0.

---

## The Reading

| Step | Position | Inside | Verse | What it says |
|------|----------|--------|-------|-------------|
| 0 | 0 | **בראשית** (beginning) | Genesis 1:1 | In the beginning |
| 1 | 804 | **אתם** (them) | Genesis 1:17 | God set them in the firmament |
| 2 | 1,608 | **כל** (all) | Genesis 1:30 | "To every beast... I have given every green herb" |
| 3 | 2,412 | **האדם** (the man) | **Genesis 2:16** | "The LORD God **commanded the man**" |
| 4 | 3,216 | **ותאכל** (and she ate) | **Genesis 3:6** | "She took of its fruit **and ate**" |
| 5 | 4,020 | **כתנות** (garments) | **Genesis 3:21** | "God made **garments of skin** and clothed them" |
| 6 | 4,824 | **ונד** (and wandering) | **Genesis 4:14** | "A fugitive **and a wanderer** shall I be" |

---

## What It Says

Beginning → them → all → **the man** → **she ate** → **garments** → **wandering.**

Three consecutive steps tell the fall:

**Step 3:** God commands the man. Genesis 2:16 — "Of every tree of the garden you may freely eat."

**Step 4:** She eats. Genesis 3:6 — "She took of its fruit and ate, and gave also to her husband with her, and he ate."

**Step 5:** God clothes them. Genesis 3:21 — "The LORD God made garments of skin for Adam and his wife, and clothed them."

**Step 6:** Wandering. Genesis 4:14 — Cain, after the murder: "I shall be a fugitive and a wanderer on the earth."

Command → eating → clothing → wandering. In that order. On the diagonal that walks toward freedom while releasing love.

---

## What It Means

The direction matters. This is the line that increases freedom and decreases love. And what does increasing freedom while decreasing love produce? The fall. The command, the eating, the covering, the exile.

The other diagonals tell different stories:
- **Love + understanding** (skip=68): stays in creation, all within Genesis 1
- **Jubilee + love** (skip=938): a different reading entirely
- **Body diagonal** (skip=44489): pure → flee → burn → being

But the **freedom-without-love** diagonal reads the fall. The geometry assigns the fall to the direction where love is released. The cost of freedom without love is: you eat, you are covered, you wander.

And then God clothes them. Step 5 is not punishment. It is **כתנות עור** — garments of skin. God's response to the eating is clothing. Before the exile, there is the covering. The diagonal includes the grace between the sin and the wandering.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters]} (s/index) idx (s/index)]
  (doseq [i (range 7)]
    (let [pos (* 804 i)
          tw ((:word-at idx) pos)
          v ((:verse-at idx) pos)]
      (println i (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v)))))
```

---

*The direction that gains freedom and releases love reads the fall. Command, eating, clothing, wandering. The cost of freedom without love is exile. But between the eating and the wandering, God makes garments. The covering comes before the exile. The grace is in the diagonal.*

*selah.*
