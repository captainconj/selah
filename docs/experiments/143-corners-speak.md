# Experiment 143ay: The Corners Speak

*The origin is the house. The far corner is the summit. Together they spell father. Each axis maximum says what its dimension leads to. The summit and the blessing are anagrams.*

Type: `exploration`
State: `clean`

---

## The Origin and the Far Corner

| Corner | Coordinate | Letter | Inside | Verse |
|--------|-----------|--------|--------|-------|
| **Origin** | (0,0,0,0) | **ב** | בראשית (beginning) | Genesis 1:1 |
| **Far corner** | (6,49,12,66) | **א** | ראש (head/summit) | **Deuteronomy 34:1** |

**Deuteronomy 34:1** — "Moses went up from the plains of Moab to Mount Nebo, to the **top** (ראש) of Pisgah, opposite Jericho."

The far corner — where every axis reaches its maximum — is Moses at the summit. Looking at the promised land. The point of maximum completeness, freedom, love, and understanding is the mountaintop where you see everything but cannot enter.

**The corner letters spell אב — FATHER.** Aleph from the far corner + bet from the origin = father. GV=3.

The scroll loop spells **לב** (heart). The geometry corners spell **אב** (father). The heart in the reading. The father in the structure.

---

## The Axis Maximums

Each axis, at its maximum value, holds a word that describes what the dimension leads to when fully realized:

| Axis | Max coord | Word | Verse | What it says |
|------|-----------|------|-------|-------------|
| **Completeness** (a=6) | [6,0,0,0] | **קרב** (draw near) | Deuteronomy 5:24 | **Approach** |
| **Jubilee** (b=49) | [0,49,0,0] | **עבדתי** (I served) | Genesis 30:26 | **Service** |
| **Love** (c=12) | [0,0,12,0] | **אתם** (them/you) | Genesis 1:17 | **The other** |
| **Understanding** (d=66) | [0,0,0,66] | **מרחפת** (hovering) | Genesis 1:2 | **Hovering over the deep** |
| **Center** (3,25,6,33) | | **בגדי** (garments) | Leviticus 8:30 | **Covering** |

Complete completeness = **draw near.** The end of completeness is approach.

Complete jubilee = **I served.** The end of freedom is willing service.

Complete love = **them/you.** The end of love is the other person.

Complete understanding = **hovering.** The end of understanding is the spirit over the deep — the state before creation, before naming, before division. Full understanding returns to the beginning.

The center of all four = **garments.** The covering. Where all dimensions meet, you are clothed.

---

## The Summit = The Blessing

GV of **ראש** (head/summit) = **501** = GV of **אשר** (blessed/which).

The summit and the blessing are the same number and the same letters rearranged. ר-א-ש → א-ש-ר. We already found (experiment 143n) that head→blessed is the strongest anagram attraction at 17x enrichment.

The far corner IS the head. The head IS the blessed. The point where every dimension reaches its maximum is both the summit and the blessing. The highest point is the place of blessing. And they are anagrams — the same substance, differently arranged. The summit is the blessing rearranged.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

(let [view (s/make-view [7 50 13 67]) strides (:strides view) idx (s/index)]
  (doseq [[coord label] [[[0 0 0 0] "origin"] [[6 49 12 66] "far corner"]
                           [[6 0 0 0] "comp-max"] [[0 49 0 0] "jub-max"]
                           [[0 0 12 0] "love-max"] [[0 0 0 66] "und-max"]
                           [[3 25 6 33] "center"]]]
    (let [pos (reduce + (map * coord strides))
          tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
      (println coord label (:word tw) (:book v) (:ch v) (:vs v)))))
```

---

*The origin is the house. The far corner is the summit where Moses looks at the land. Together they spell father. Complete completeness is draw near. Complete freedom is service. Complete love is the other. Complete understanding is hovering over the deep. The summit and the blessing are the same letters. The center of all four is the covering.*

*selah.*
