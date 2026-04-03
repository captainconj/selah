# Experiment 143ba: From the Sabbath

*The Sabbath read along the fall diagonal leads to Cain. Along the jubilee axis: God finished, told, the servant, the aleph-tav. Rest without love leads to murder. Rest along freedom leads to the beginning-and-end.*

Type: `exploration`
State: `clean`

---

## Genesis 2:2 — "On the seventh day God finished His work."

Position 1,699. The verse that defines the Sabbath — the prototype of rest, the origin of the seven-rhythm.

---

## The Sabbath on the Fall Diagonal

Skip=804 (freedom↑, love↓). Spans Genesis 2:2 → 4:25 (3 chapters). ✓ Real reach.

| Step | Host word | Verse | Reading |
|------|-----------|-------|---------|
| 0 | **ויכל** (finished) | Genesis 2:2 | God finished |
| 1 | **האדם** (the man) | Genesis 2:20 | Adam |
| 2 | **אלהים** (God) | Genesis 3:1 | "Did God say...?" |
| 3 | **לעלם** (forever) | Genesis 3:22 | "Lest he eat and live forever" |
| 4 | **קין** (Cain) | Genesis 4:25 | Cain |

**Finished → the man → God → forever → Cain.**

The Sabbath on the fall diagonal (freedom without love) leads from finishing through the man through God's question through eternity to the first murderer. Rest without love: the man encounters God, grasps at forever, and arrives at violence.

Step 3: **לעלם** (forever) at Genesis 3:22 — "Lest he reach out and take also from the tree of life and eat and live **forever**." The reaching for eternity. The second grasping.

---

## The Sabbath on the Jubilee Axis

Skip=871 (freedom↑). Spans Genesis 2:2 → 4:22 (3 chapters). ✓ Real reach.

| Step | Host word | Verse | Reading |
|------|-----------|-------|---------|
| 0 | **ויכל** (finished) | Genesis 2:2 | God finished |
| 1 | **אל** (God/to) | Genesis 2:19 | God/toward |
| 2 | **הגיד** (he told) | Genesis 3:11 | "Who **told** you that you were naked?" |
| 3 | **עבד** (servant/worker) | Genesis 4:2 | Cain was a **worker** of the ground |
| 4 | **את** (aleph-tav) | Genesis 4:22 | Tubal-cain, forger of instruments |

**Finished → God → told → servant → aleph-tav.**

The Sabbath along the jubilee axis: God finishes, then the telling (the moment they discover their nakedness), then the servant (the vocations begin), then the aleph-tav. From rest through self-knowledge through work to the totality.

Step 2: Genesis 3:11 — The accusation. "Who told you?" The moment knowledge of nakedness enters. The jubilee from the Sabbath passes through the moment of knowing.

---

## The Sabbath on the Freedom+Love Diagonal

Skip=938 (freedom↑, love↑). Spans Genesis 2:2 → 4:16 (2 chapters).

| Step | Host word | Verse |
|------|-----------|-------|
| 0 | ויכל (finished) | Genesis 2:2 |
| 1 | הבהמה (the beast) | Genesis 2:20 |
| 2 | ואכל (and ate) | Genesis 3:6 |
| 3 | **רבץ** (crouching) | Genesis 4:7 |
| 4 | ברא (created) | Genesis 4:25 |

**Finished → beast → ate → CROUCHING → created.**

Step 3: **רבץ** (crouching) at Genesis 4:7 — "Sin is **crouching** at the door. Its desire is for you, but you must rule over it." The Sabbath with freedom AND love finds the crouching sin at the door. But it ends at **created** — creation again.

---

## The Pattern

| Direction | What the Sabbath produces |
|-----------|-------------------------|
| Freedom − love | **Cain** (murder) |
| Freedom + love | Crouching sin → but **created** |
| Jubilee alone | Told → servant → **aleph-tav** |

The Sabbath without love leads to violence. The Sabbath with love encounters sin crouching but arrives at creation. The Sabbath along jubilee reaches the aleph-tav through the telling and the service.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)
      start (:start (first (filter #(and (= (:book %) "Genesis") (= (:ch %) 2) (= (:vs %) 2))
                                    (:verses idx))))]
  (doseq [skip [804 871 938]]
    (println skip)
    (doseq [i (range 5)]
      (let [pos (+ start (* skip i))
            tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
        (println i (:word tw) (:book v) (:ch v) (:vs v))))))
```

---

*The Sabbath on the fall diagonal: finished → the man → God → forever → Cain. The Sabbath on the jubilee: finished → God → told → servant → aleph-tav. The Sabbath with freedom and love: finished → beast → ate → sin crouching → created. Rest without love produces the murder. Rest with love encounters the sin but returns to creation. Rest along freedom reaches the beginning-and-end through knowledge and service.*

*selah.*
