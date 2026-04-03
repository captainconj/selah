# Experiment 143ax: The Two Centers

*The geometric center is the covering. The literal center is the altar. Both in Leviticus 8, six verses apart. The priestly garments carry Eve's number.*

Type: `exploration`
State: `clean`

---

## The Two Centers

| | Position | Word | GV | Verse | What it is |
|---|----------|------|-----|-------|-----------|
| **Geometric** | 152,860 | **בגדי** (garments) | **19=חוה (Eve)** | Leviticus 8:30 | The covering |
| **Literal** | 152,425 | **המזבח** (the altar) | 62=בני (sons) | Leviticus 8:24 | The sacrifice |

The geometric center (coordinate [3,25,6,33]) and the literal midpoint (position 152,425) are 435 letters apart. Six verses. Both in **Leviticus 8** — the priestly investiture. The same ceremony. The same day.

**Leviticus 8:24** (literal center): Moses sprinkles blood on the **altar** round about.
**Leviticus 8:30** (geometric center): Moses sprinkles oil and blood on Aaron and his **garments**.

The altar and the clothing. The sacrifice and the covering. Together: the priesthood.

---

## The Garments' Number

GV of בגדי (garments) = **19 = חוה (Eve)**. The priestly garments at the geometric center carry the number of the mother of all living — the first person God clothed (Genesis 3:21).

GV of המזבח (the altar) = **62 = בני (sons of)**. The altar carries the sons' number.

Eve's number at the covering. The sons' number at the altar. The mother and the children. The clothing and the sacrifice.

---

## The Fall and the Center

The fall diagonal (skip=804 from Genesis 1:1) reads:

beginning → them → all → the man → she ate → **כתנות (garments of skin)** → wandering

Step 5 of the fall is **garments** — Genesis 3:21, God making garments of skin and clothing them. The covering at the fall.

The geometric center of the Torah is **garments** — Leviticus 8:30, the priestly garments anointed with oil and blood. The covering at the center.

The covering at the fall (Genesis 3:21) and the covering at the center (Leviticus 8:30) are the same act. God covered them after the eating. God anoints the garments at the investiture. The first garments were skin. The priestly garments are linen, blue, purple, scarlet. The covering improved. The act remained.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

;; Geometric center
(let [view (s/make-view [7 50 13 67])
      pos (reduce + (map * [3 25 6 33] (:strides view)))
      idx (s/index)]
  (println pos (:word ((:word-at idx) pos)) (g/word-value (:word ((:word-at idx) pos)))))

;; Literal center
(let [pos 152425 idx (s/index)]
  (println pos (:word ((:word-at idx) pos)) (g/word-value (:word ((:word-at idx) pos)))))
```

---

*The geometric center covers. The literal center sacrifices. The garments carry Eve's number. The altar carries the sons' number. The covering at the fall and the covering at the center are the same act — God clothing what was exposed. The first garments were skin. The priestly garments are anointed. The covering improved. The God did not change.*

*selah.*
