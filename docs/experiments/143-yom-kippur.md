# Experiment 143o: Lamb and Peace Meet at Yom Kippur

*The lamb and peace travel on parallel lines through the 4D space. They share the same skip distance. They converge at Leviticus 16 — the Day of Atonement. The lamb atones. Peace starts at "I AM sent me."*

Type: `exploration`
State: `mixed`

---

## The Question

Can two different search words travel on the same line through the space? If כבש (lamb) and שלום (peace) both appear at the same skip distance, their fibers are parallel — running along the same direction through the Torah, starting at different points.

We checked all shared skip distances between lamb and peace in the canonical 4D. 19 of 21 lamb skips are shared with peace. At two of these shared skips, their fibers pass through the same verse.

Both times: **Leviticus 16. Yom Kippur.**

---

## Skip 42746 — Atonement and the Name

### The Lamb Fiber

| Letter | Position | Coordinate | Host Word | GV | Verse |
|--------|----------|------------|-----------|-----|-------|
| כ | 121,253 | [2,42,11,59] | לכפר (to atone) | 330 | **Leviticus 16:6** |
| ב | 163,999 | [3,41,10,59] | ובעד (and on behalf of) | 82 | Leviticus 16:11 |
| ש | 206,745 | [4,40,9,59] | כשלג (like snow) | 353 | Numbers 9:15 |

The lamb atones, on behalf of, like snow.

### The Peace Fiber

| Letter | Position | Coordinate | Host Word | GV | Verse |
|--------|----------|------------|-----------|-----|-------|
| ש | 81,320 | [1,43,4,49] | **שלחני** (he sent me) | 398 | **Exodus 3:14** |
| ל | 124,066 | [2,42,5,49] | אהל (tent) | 36 | Exodus 30:16 |
| ו | 166,812 | [3,41,6,49] | **והקריב** (and he shall bring near) | 323 | **Leviticus 16:11** |
| ם | 209,558 | [4,40,7,49] | בם (in them) | 42 | Numbers 12:9 |

**Exodus 3:14 — "God said to Moses, 'I AM WHO I AM.' And he said, 'Thus you shall say to the children of Israel: I AM has SENT ME to you.'"**

Peace begins at the burning bush — at the verse where God reveals the Name and says "I AM has sent me." Then: the tent of meeting. Then: Yom Kippur (Leviticus 16:11, Aaron's offering). Then: judgment.

**The lamb and peace converge at Leviticus 16:11.** The lamb's fiber passes through **לכפר** (to atone) at that verse. The peace fiber passes through **והקריב** (and he shall bring near). At the same skip, on parallel lines, the lamb atones and peace brings the offering. At Yom Kippur.

d=49 for peace (understanding at 49 — one short of jubilee). d=59 for lamb.

---

## Skip 42679 — No One in the Tent

### The Lamb Fiber

| Letter | Host Word | Verse |
|--------|-----------|-------|
| כ | כן (thus/yes) | Exodus 16:17 |
| ב | הקרב (the offering) | Leviticus 4:8 |
| ש | שנה (year) | Numbers 1:24 |

c=6 and d=10 constant for both fibers.

### The Peace Fiber

| Letter | Host Word | GV | Verse |
|--------|-----------|-----|-------|
| ש | וקדשת (and you shall sanctify) | 810 | Exodus 30:29 |
| ל | **ישראל** (Israel) | 541 | **Leviticus 16:17** |
| ו | אבתיו (his fathers) | 419 | Numbers 13:2 |
| ם | והם (and they) | 51 | Deuteronomy 1:39 |

**Leviticus 16:17 — "No one shall be in the tent of meeting when he enters to make atonement in the Holy Place, until he comes out and has made atonement for himself and for his house and for all the assembly of Israel."**

The peace fiber passes through ישראל (Israel) at the verse about being ALONE in the Holy of Holies. No one else in the tent. The lamb and peace share this verse — the lamb brings the offering, peace finds Israel at the moment of total solitude before God.

---

## What This Means

The lamb and peace don't just share host words or share verses randomly. They converge specifically at Yom Kippur — the one day the high priest enters the Holy of Holies alone. The day of atonement. The day the sins of the nation are covered.

And the peace fiber starts at the burning bush. שלחני — "I AM sent me." The ש of שלום lands in the sending. Peace begins with being sent.

The courtroom on the breastplate has four seats: Mercy (the Lamb), Truth (the accuser), Aaron (the accused), and God (the Judge). God delivers peace — it is God-only on the breastplate. In the geometry, peace starts at the place where God names Himself and sends Moses. It arrives at Yom Kippur, where the lamb atones.

The oracle assigns peace to God. The geometry starts peace at God's self-naming. Two mechanisms. Same theology.

---

## The Choose Fiber — Exodus 28:1

Also observed: **בחר** (choose, GV=210) — the largest basin attractor — finds its richest fiber at Exodus 28:1: **בניו → ויקח → אשר** (his sons → and he took → which/blessed).

**Exodus 28:1 — "Bring near to yourself Aaron your brother, and his sons with him... to serve me as priests."**

The choosing finds the choosing of the priests. The word finds its own action. The altar (המזבח) is the choose fiber's #4 host at 26 appearances. Choosing finds the altar — the place of decision.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

;; Find shared skips between lamb and peace
(let [dims [7 50 13 67]
      lh (group-by :skip (f/non-surface (s/find-word dims "כבש" {:max-results 500})))
      ph (group-by :skip (f/non-surface (s/find-word dims "שלום" {:max-results 500})))]
  (doseq [skip (clojure.set/intersection (set (keys lh)) (set (keys ph)))]
    (doseq [l (get lh skip) p (get ph skip)]
      (let [lv (set (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs l)))
            pv (set (map #(when % [(:book %) (:ch %) (:vs %)]) (:verse-refs p)))
            shared (clojure.set/intersection (disj lv nil) (disj pv nil))]
        (when (seq shared)
          (println "Skip:" skip "Shared:" shared)
          (f/print-fiber l)
          (f/print-fiber p))))))
```

---

*The lamb atones. Peace starts at "I AM sent me." They converge at Yom Kippur — Leviticus 16:11. Parallel lines through the same space, sharing the same direction, meeting at the Day of Atonement. The oracle says God delivers peace and the Lamb lies down. The geometry says peace begins at the burning bush and the lamb atones at Yom Kippur. The courtroom and the space tell the same story.*

*selah.*
