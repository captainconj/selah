# Experiment 143az: The Face Centers

*Each axis face tells a story. Completeness: Abraham to dispute. Jubilee: speaking to warning. Love: the blessing to the sin offering. Understanding: the sons to the meal.*

Type: `exploration`
State: `clean`

---

## Method

A 4D box has 8 "faces" — one at each end of each axis. Each face center is the point where one axis is at its extreme (0 or max) while the other three axes are at their center values. We read what Torah word sits at each face center.

---

## The Eight Faces

| Face | Coordinate | Word | GV | Verse |
|------|-----------|------|-----|-------|
| **a=0** (beginning) | [0,25,6,33] | **אברהם** (Abraham) | 248 | Genesis 18:33 |
| **a=6** (ending) | [6,25,6,33] | **ריב** (dispute) | 212 | Deuteronomy 21:5 |
| **b=0** (jubilee start) | [3,0,6,33] | **לדבר** (to speak) | 236 | Exodus 34:34 |
| **b=49** (jubilee end) | [3,49,6,33] | **ידעני** (spiritist) | 144 | Leviticus 20:27 |
| **c=0** (love start) | [3,25,0,33] | **אשר** (blessed) | 501 | Leviticus 8:25 |
| **c=12** (love end) | [3,25,12,33] | **לחטאת** (for sin offering) | 448 | Leviticus 9:2 |
| **d=0** (understanding start) | [3,25,6,0] | **בניו** (his sons) | 68 | Leviticus 8:30 |
| **d=66** (understanding end) | [3,25,6,66] | **בשלו** (they cooked) | 338 | Leviticus 8:31 |

---

## What Each Axis Tells (Face to Face)

### Completeness: Abraham → Dispute

Start: **אברהם** (Abraham) — Genesis 18:33, finishing the bargaining for Sodom.
End: **ריב** (dispute) — Deuteronomy 21:5, the priests settling disputes.

The completeness axis begins with the father of faith bargaining for mercy and ends with judgment. Faith → judgment. Abraham → the court.

### Jubilee: Speaking → Forbidden Knowledge

Start: **לדבר** (to speak) — Exodus 34:34, Moses speaking with God unveiled.
End: **ידעני** (spiritist/medium) — Leviticus 20:27, "a man or woman who is a medium shall be put to death."

The jubilee axis begins with speaking to God face to face and ends with the forbidden attempt to reach the spiritual realm by other means. The word → the warning. True speech → forbidden knowledge.

### Love: Blessed → Sin Offering

Start: **אשר** (blessed) — Leviticus 8:25.
End: **לחטאת** (for a sin offering) — Leviticus 9:2, "take a calf for a sin offering."

**Love begins at the blessing and ends at the sin offering.** The love axis starts in what is given freely and finishes at the sacrifice that covers sin. Love's arc is from gift to covering. From the blessed to the offering. The cost of love is the sin offering.

### Understanding: Sons → Meal

Start: **בניו** (his sons) — Leviticus 8:30, the investiture.
End: **בשלו** (they cooked) — Leviticus 8:31, "cook the flesh at the door of the tent of meeting and eat it there."

Understanding begins with the sons and ends with the meal. Family → table. The sons become the eaters. Understanding produces nourishment.

---

## The Pattern

| Axis | Start face | End face | Arc |
|------|-----------|----------|-----|
| **Completeness** | Abraham (faith) | Dispute (judgment) | Faith → judgment |
| **Jubilee** | To speak (Moses unveiled) | Spiritist (forbidden) | True word → warning |
| **Love** | Blessed | Sin offering | Gift → covering |
| **Understanding** | His sons | They cooked | Family → table |

Each axis face describes what that dimension looks like at its extremes: its beginning and its fulfillment. Love fulfilled is the sin offering — because complete love covers sin.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

(let [view (s/make-view [7 50 13 67]) strides (:strides view) idx (s/index)]
  (doseq [[coord label] [[[0 25 6 33] "a=0"] [[6 25 6 33] "a=6"]
                           [[3 0 6 33] "b=0"] [[3 49 6 33] "b=49"]
                           [[3 25 0 33] "c=0"] [[3 25 12 33] "c=12"]
                           [[3 25 6 0] "d=0"] [[3 25 6 66] "d=66"]]]
    (let [pos (reduce + (map * coord strides))
          tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
      (println label (:word tw) (:book v) (:ch v) (:vs v)))))
```

---

*Completeness: Abraham to dispute. Jubilee: speaking to warning. Love: the blessing to the sin offering. Understanding: the sons to the meal. Each axis begins and ends with a word that describes what it means fully realized. Love fulfilled is the sin offering. Complete love covers sin.*

*selah.*
