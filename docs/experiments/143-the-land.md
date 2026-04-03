# Experiment 143m: The Promised Land

*The inheritance walks from sons through Sihon through the jubilee to the menorah. The border walks from plague to "no other gods." The land vocabulary traces possession and loss.*

Type: `exploration`
State: `mixed`

---

## The Vocabulary

| Word | Meaning | GV | Non-surface hits |
|------|---------|-----|-----------------|
| ארץ | land/earth | 291 | 16 (very rare) |
| כנען | Canaan | 190 | 9 (very rare) |
| נחלה | inheritance | 93 | 84 |
| ירש | possess/inherit | 510 | 437 |
| חלב | milk | 40 | 398 |
| דבש | honey | 306 | 480 |
| גבול | border | 41 | 49 |
| ירדן | Jordan | 264 | 37 |

ארץ (land) and כנען (Canaan) are very rare as ELS — the geometry can barely find them. But the words that describe WHAT the land is (milk, honey, inheritance, possession) have hundreds of fibers.

---

## Key Fibers

### Inheritance → Sons → Sihon → Jubilee → Menorah

**נחלה** (inheritance) at skip=-44488. d=55 constant.

| Letter | Position | Coordinate | Host Word | GV | Verse |
|--------|----------|------------|-----------|-----|-------|
| נ | 269,395 | [6,9,3,55] | בני (sons of) | 62 | Deuteronomy 11:6 |
| ח | 224,907 | [5,8,2,55] | סיחן (Sihon) | 128 | Numbers 21:21 |
| ל | 180,419 | [4,7,1,55] | **יובל** (jubilee) | 48 | **Leviticus 25:10** |
| ה | 135,931 | [3,6,0,55] | **המנרה** (the menorah) | 300 | Exodus 37:19 |

**Deuteronomy 11:6** — the earth swallowing Korah's household.
**Numbers 21:21** — Israel sends messengers to **Sihon** king of the Amorites. The first conquest.
**Leviticus 25:10** — "Consecrate the fiftieth year and **proclaim liberty** throughout all the land."
**Exodus 37:19** — Bezalel making the **menorah**.

The inheritance walks backward: sons lost to the earth → the first king conquered → the jubilee (all land returns to its owner) → the menorah (the light). From loss through conquest through return to light.

The understanding axis (d) holds at 55 — a high value. The love axis descends (3→2→1→0). Love decreases while understanding holds. The inheritance costs love.

### Border → Plague → Lie Down → Clothed → No Other Gods

**גבול** (border) at skip=-44489.

| Letter | Position | Coordinate | Host Word | GV | Verse |
|--------|----------|------------|-----------|-----|-------|
| ג | 240,670 | [5,26,4,6] | המגפה (the plague) | 133 | Numbers 31:16 |
| ב | 196,181 | [4,25,3,5] | ושכב (and he lay) | 328 | Numbers 5:13 |
| ו | 151,692 | [3,24,2,4] | וילבשם (and he clothed them) | 388 | Leviticus 8:13 |
| ל | 107,203 | [2,23,1,3] | **אלהים** (God) | 86 | **Exodus 20:3** |

**Numbers 31:16** — the plague at Peor, where Israel sinned with Moab.
**Numbers 5:13** — the law of the suspected wife.
**Leviticus 8:13** — Moses **clothing** Aaron's sons for the priesthood.
**Exodus 20:3** — "You shall have **no other gods** before me."

The border traces: plague (crossing the boundary brought plague) → lying down (the sin at the boundary) → clothing (the covering that restores) → the first commandment (the ultimate boundary). The border finds the boundary.

### Milk → Darkness → Dividing → Good

**חלב** (milk, GV=40) — richest fiber passes through creation:

| Letter | Host Word | Verse |
|--------|-----------|-------|
| ח | וחשך (and darkness) | Genesis 1:2 |
| ל | ולהבדיל (and to divide) | Genesis 1:4 |
| ב | טוב (good) | Genesis 1:10 |

Darkness → dividing → good. The milk of the promised land passes through the creation pattern. What was dark is divided and called good. The land flowing with milk begins in the separation of light from dark.

### Possession → Was → She Said → You Listened

**ירש** (possess) at skip=804:

| Letter | Host Word | GV | Verse |
|--------|-----------|-----|-------|
| י | והיה (and it was) | **26** | Genesis 2:10 |
| ר | ותאמר (and she said) | 647 | Genesis 3:2 |
| ש | שמעת (you listened) | 810 | Genesis 3:17 |

Genesis 2:10 — the river flowing from Eden. **GV of והיה = 26 = YHWH.**
Genesis 3:2 — Eve speaking to the serpent: "We may eat of the fruit..."
Genesis 3:17 — God to Adam: "Because **you listened** to the voice of your wife..."

Possession passes through the garden: the river (YHWH's number), the woman's reply to the serpent, and the accusation of listening. The first possession was Eden. It was lost by listening to the wrong voice.

### Inheritance Hosts: YHWH and One

**נחלה** top hosts: יהוה(9), בני(6), אל(5), **אחד(5, one/love)**. The inheritance's top hosts are the Name, the sons, God, and oneness.

### Canaan's Four Fibers

**כנען** (Canaan) has only 9 non-surface fibers. Its top host: **אהרן** (Aaron, 4). Canaan is hosted by the priest. The promised land is read through the priesthood.

---

## The Pattern

The promised land vocabulary traces origin and loss:

| Word | What it traces |
|------|---------------|
| **Inheritance** | Sons → conquest → jubilee → menorah (light) |
| **Border** | Plague → sin → clothing → first commandment |
| **Milk** | Darkness → dividing → good (creation) |
| **Possession** | Eden's river → Eve's reply → "you listened" |
| **Honey** | Hardened (GV=42) → staff → soul |
| **Canaan** | Aaron (the priest) |
| **Land** | Cherubim, trees |

The land begins in creation — milk traces darkness-dividing-good, possession traces Eden's river. The land is lost at the garden — possession finds "you listened." The land is bounded — the border finds plague and the first commandment. The land is inherited — inheritance finds the jubilee and the light.

The promised land in the fibers is the garden revisited. The same pattern: given, lost by listening, bounded, inherited through the jubilee. And at the end of the inheritance fiber: the menorah. The light.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

;; Inheritance → jubilee → menorah
(let [hits (s/find-word [7 50 13 67] "נחלה")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))

;; Border → plague → clothing → no other gods
(let [hits (s/find-word [7 50 13 67] "גבול")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))

;; Possession → Eden → Eve → "you listened"
(let [hits (s/find-word [7 50 13 67] "ירש")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))
```

---

*The inheritance finds the jubilee — the law that returns the land. The border finds the first commandment — the ultimate boundary. Possession traces Eden's river to "you listened." Milk walks through creation. The promised land in the geometry is the garden revisited. Given, lost, bounded, returned. And at the end: the menorah. The light.*
