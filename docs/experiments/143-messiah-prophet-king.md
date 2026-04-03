# Experiment 143q: Messiah, Prophet, King

*The messiah finds Yom Kippur and the lamb lying down. The king finds Melchizedek and Amalek. The prophet gives in the Name. The image finds Egypt and the command.*

Type: `exploration`
State: `mixed`

---

## Messiah — משיח (GV=358=serpent)

107 non-surface hits. GV=358 = נחש (serpent). The anointed and the serpent are the same number.

**Top hosts:** ישראל (8), אשר (7), משה (7), כי (5), יהוה (4), **המזבח (the altar, 4)**, **השביעי (the seventh, 4)**, **המשכן (the tabernacle, 3)**.

The messiah finds Israel, Moses, the Name, the altar, the seventh, and the tabernacle.

### Yom Kippur Fiber

**Skip:** -44489. All axes varying.

| Letter | Host Word | GV | Verse |
|--------|-----------|-----|-------|
| מ | **המזבח** (the altar) | 62 | **Leviticus 16:18** |
| ש | **קדשים** (holy things) | 454 | Leviticus 16:20 |
| י | **ובני** (and the sons of) | 68 | Numbers 1:22 |
| ח | לנכח (before/opposite) | 108 | Numbers 2:2 |

**Leviticus 16:18-20 — Yom Kippur.** "He shall go out to the **altar** that is before the LORD... and when he has finished atoning for the **holy place**."

The messiah's richest fiber starts at the altar on the Day of Atonement, passes through the holy things, and reaches the sons of Israel. The anointed finds Yom Kippur.

### The Lying-Down Fiber

**Skip:** -44420.

| Letter | Host Word | GV | Verse |
|--------|-----------|-----|-------|
| מ | למלחמה (for war) | 123 | Deuteronomy 3:1 |
| ש | שנים (two/years) | 400 | Numbers 25:14 |
| י | **ישכב** (he lies down) | 332 | Numbers 19:20 |
| ח | **המזבח** (the altar) | 62 | Exodus 29:21 |

For war → two → **he lies down** → the altar.

**ישכב — the basin attractor of the lamb.** On the breastplate, כבש (lamb) flows to שכב (lie down). The messiah's fiber passes through the word the lamb becomes. War, then duality, then lying down, then the altar.

The anointed goes to war, passes through twoness, lies down, and reaches the altar.

---

## King — מלך (GV=90)

74 non-surface hits.

**Top hosts:** **לך (to you / go, 7)**, אל (5), **אלהיך (your God, 5)**, **עמך (your people, 4)**.

The king finds "to you" and "your God" and "your people." The king belongs to the addressed — the second person. "Your" king.

### The Two Meetings

**Skip -44489:** [עמלק → לאלהים → ואתך]

| Letter | Host Word | Verse |
|--------|-----------|-------|
| מ | **עמלק** (Amalek) | **Exodus 17:8** |
| ל | לאלהים (to God) | Genesis 40:8 |
| ך | ואתך (and you) | Genesis 12:12 |

Amalek → to God → and you. The king finds the enemy first (Amalek, the perpetual adversary), then God, then "you" at Genesis 12:12 — Abram entering Egypt, afraid they will kill him for Sarah.

**Skip -44489 (different start):** [מלך → משלשת → בחיקך] at Genesis 14:17

Genesis 14:17 — the king of Sodom comes out to meet Abram. The very next verse (14:18) is **Melchizedek**. The king finds the meeting where two kings appear — Sodom and Salem, wickedness and righteousness, side by side.

Both holy (קדש) and king (מלך) find Genesis 14:17-18 — the Melchizedek passage. The priest-king of Salem. Holy finds the righteousness (צדק). King finds the encounter. Two words, same passage, different angles.

---

## Prophet — נביא (GV=63)

180 non-surface hits.

**Top hosts:** **בני (sons of, 29)**, ישראל (19), את (17), יהוה (11), אל (11).

The prophet finds the sons of Israel and the Name. The prophet speaks to the sons on behalf of the Name.

**Richest fiber:** [עינים → העבתת → הבתים → הוא]

| Letter | Host Word | Verse |
|--------|-----------|-------|
| נ | עינים (eyes) | Leviticus 26:16 |
| ב | העבתת (the braided chains) | Exodus 39:18 |
| י | **הבתים** (the houses) | **Exodus 12:7** |
| א | הוא (he) | Genesis 34:14 |

**Exodus 12:7** — "They shall take some of the blood and put it on the two doorposts and the lintel of the **houses** in which they eat it." The Passover.

The prophet's fiber: eyes → chains → the houses (where the blood goes on the doorposts) → he. The prophet sees, is bound, finds the Passover house, and arrives at "he."

---

## Image — צלם (GV=160)

196 non-surface hits.

**Top hosts:** **מצרים (Egypt, 23)**, **צוה (command, 12)**, על (12), לא (12).

The image finds Egypt — the land of images, the land of idols. And it finds the command. "You shall not make for yourself an image" (Exodus 20:4). The image and the prohibition of images. The word finds its own boundary.

**Richest fiber:** [יצאו → **שלם** → הגזרים] at Genesis 15:14

**שלם** — Salem, complete, whole. GV=370. The image passes through wholeness at the covenant of the pieces (Genesis 15). The image finds completeness.

---

## The Offices

| Office | GV | What it finds | Key passage |
|--------|-----|---------------|-------------|
| **Messiah** | 358 | Yom Kippur altar, the lamb lying down | Leviticus 16:18 |
| **King** | 90 | Amalek, Melchizedek, "to you" | Genesis 14:17, Exodus 17:8 |
| **Prophet** | 63 | Sons of Israel, the Passover houses | Exodus 12:7 |
| **Image** | 160 | Egypt, the command, wholeness | Genesis 15:14 |

The messiah goes to the altar on Yom Kippur and lies down. The king meets Amalek and Melchizedek — the enemy and the righteous priest. The prophet finds the Passover house where the blood goes on the doorposts. The image finds Egypt (the land of idols) and the command (the prohibition).

Each office finds its own domain. The anointed finds the atonement. The king finds the encounter. The prophet finds the house marked with blood. The image finds the boundary.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

;; Messiah → Yom Kippur
(let [hits (s/find-word [7 50 13 67] "משיח" {:max-results 200})]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))

;; Messiah → lies down
(let [hits (s/find-word [7 50 13 67] "משיח" {:max-results 200})]
  (f/print-fiber (first (filter #(some (fn [tw] (and tw (= (:word tw) "ישכב")))
                                        (:torah-words %))
                                (f/non-surface hits)))))

;; King → Melchizedek
(let [hits (s/find-word [7 50 13 67] "מלך")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))
```

---

*The messiah finds the altar on Yom Kippur and lies down like the lamb. The king meets Amalek and Melchizedek — the enemy and the righteous priest, at the same crossroads. The prophet finds the Passover house. Three offices, three passages, three dimensions of the same story. The anointed atones. The king encounters. The prophet marks.*

*selah.*
