# Experiment 143ak: Walking All Four Axes

*Each axis walks a different story. The love axis fits entirely within creation. The jubilee axis walks from beginning to service. The completeness axis reads "in the midst of your God." Each axis is a journey.*

Type: `exploration`
State: `clean`

---

## The Love Axis — 13 Stations

Every 67 letters. All 13 stations fall within Genesis 1 — the creation narrative. Love is contained in creation.

| Station | Inside | Verse | What it says |
|---------|--------|-------|-------------|
| c=0 | **בראשית** (in the beginning) | Genesis 1:1 | The start |
| c=1 | **ואת** (and aleph-tav) | Genesis 1:1 | Still the first verse |
| c=2 | **בין** (between) | Genesis 1:4 | Dividing light from dark |
| c=3 | **ויאמר** (and he said) | Genesis 1:6 | Speech |
| c=4 | **המים** (the waters) | Genesis 1:7 | Separation of waters |
| c=5 | **ויהי** (and it was) | Genesis 1:8 | Being |
| **c=6** | **ויקרא** (and he called) | **Genesis 1:10** | **THE CENTER: Naming** |
| c=7 | **עשב** (herb) | Genesis 1:11 | Life emerging |
| c=8 | **מזריע** (seeding) | Genesis 1:12 | Reproduction |
| c=9 | **שלישי** (third) | Genesis 1:13 | The third day |
| **c=10** | **ולמועדים** (for appointed times) | **Genesis 1:14** | **THE FESTIVALS** |
| c=11 | **המארת** (the luminaries) | Genesis 1:16 | The light-bearers |
| c=12 | **אתם** (them) | Genesis 1:17 | God set them |

**The center of love (c=6) is the naming.** "God called the dry land Earth." Love gives things their name.

**Station c=10 carries the appointed times (מועדים).** The festivals — Passover, Shavuot, Sukkot — are on the love axis. Genesis 1:14 is the verse that establishes the moedim. Love carries the calendar of meeting.

**Station c=2 carries "between" (בין).** The dividing. Love begins at the beginning, then immediately finds the between — the space between light and dark, the separation that makes relationship possible.

Love fits inside the first chapter. It does not need Exodus or the law or the prophets. Love is in the creation. All of it.

---

## The Jubilee Axis — 50 Stations

Every 871 letters. The walk reads through Genesis 1-30 — creation through Jacob's service.

| Station | Inside | Verse | What it says |
|---------|--------|-------|-------------|
| b=0 | **בראשית** (beginning) | Genesis 1:1 | Creation |
| b=2 | **השביעי** (the seventh) | **Genesis 2:2** | **The Sabbath** |
| b=4 | **אכלת** (you ate) | **Genesis 3:11** | **The accusation** |
| **b=13** | **וישימו** (they placed) | **Genesis 9:23** | **Covering Noah (at love's number)** |
| **b=25** | **צדיקם** (the righteous) | **Genesis 18:26** | **50 righteous (center asks for 50)** |
| b=42 | **אחיך** (your brother) | Genesis 27:6 | The stolen blessing |
| **b=49** | **עבדתי** (I served) | **Genesis 30:26** | **Jacob asks for release** |

Freedom begins at rest (b=2, the Sabbath) and ends at service (b=49, Jacob's plea). The covering is at love's station (b=13). The center asks for its own count (50 righteous at station 25 of 50).

---

## The Completeness Axis — 7 Stations

Every 43,550 letters. One station per book boundary.

| Station | Inside | Verse | What it says |
|---------|--------|-------|-------------|
| a=0 | **בראשית** (beginning) | Genesis 1:1 | The house |
| a=1 | **את** (aleph-tav) | Genesis 30:41 | The direct object |
| a=2 | **יהוה** (YHWH) | Exodus 7:13 | The Name |
| **a=3** | **אלהיך** (your God) | **Exodus 34:26** | **CENTER: Your God** |
| a=4 | **היא** (she/it) | Leviticus 21:9 | Being |
| a=5 | **לבני** (to the sons) | Numbers 17:3 | The sons |
| a=6 | **קרב** (draw near) | Deuteronomy 5:24 | Approach |

First four letters spell **בתוך** (in the midst). The center is in "your God." The axis reads: beginning → aleph-tav → YHWH → your God → she → sons → draw near.

From the house through the Name to "draw near."

---

## The Understanding Axis — 67 Stations

Every 1 letter. This IS the Torah's first 67 letters — Genesis 1:1-2. The surface reading of creation.

The center (d=33) is ה in **היתה** (it was) — Genesis 1:2, the void. Understanding centers on emptiness.

Station d=13 is ם in **אלהים** (God). Love's number falls inside God's name on the understanding axis.

---

## The Pattern

| Axis | Span | Center | Theme |
|------|------|--------|-------|
| **Love** (13) | Genesis 1:1–1:17 (creation only) | The naming | Love is in the creation |
| **Jubilee** (50) | Genesis 1:1–30:26 (creation to service) | 50 righteous | Freedom: rest to service |
| **Completeness** (7) | Genesis–Deuteronomy (whole Torah) | Your God | In the midst of God |
| **Understanding** (67) | Genesis 1:1–1:2 (first two verses) | The void | Understanding begins empty |

Each axis has a different scope:
- Love fits in one chapter
- Understanding fits in two verses
- Jubilee spans 30 chapters
- Completeness spans the whole Torah

The shortest axis (understanding, 67 letters) holds the deepest mystery — the void. The longest axis (completeness, 7 stations across 5 books) holds the simplest truth — your God. The in-between axes hold the stories — love in creation, freedom in Genesis.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

;; Walk any axis
(let [{:keys [letters]} (s/index) idx (s/index)]
  (doseq [[skip label dim] [[67 "love" 13] [871 "jubilee" 50]
                              [43550 "completeness" 7] [1 "understanding" 67]]]
    (println (format "\n%s axis:" label))
    (doseq [i (range dim)]
      (let [pos (* skip i)
            tw ((:word-at idx) pos)
            v ((:verse-at idx) pos)]
        (println (format "  %d: %s in %s (%s %d:%d)"
                         i (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v)))))))
```

---

*Love fits in creation. Jubilee walks from rest to service. Completeness spans the Torah and centers on "your God." Understanding begins at the void. The shortest axis holds the deepest mystery. The longest holds the simplest truth. The in-between axes hold the stories.*

*selah.*
