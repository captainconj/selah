# Experiment 143ao: Beginnings and Endings

*Every diagonal begins at the house (ב). The body diagonal ends at "in him." The scroll ends at Israel and loops to the beginning, spelling heart.*

Type: `exploration`
State: `clean`

---

## Every Direction Begins at the House

Every diagonal from position 0 starts with **ב** (bet) — the first letter of the Torah, in בראשית ("in the beginning"). Bet means house. Every direction through the Torah begins at the house.

The endings vary by direction:

| Pair | GV | Diagonals | Meaning |
|------|-----|-----------|---------|
| **בו** (in him) | 8 | 4 diagonals including body | New beginning |
| **בא** (came) | 3 | 3 diagonals | He came / arrival |
| **בר** (pure/son) | 202 | 3 diagonals | Purity / the son |
| **בך** (in you) | 22 | 2 diagonals | 22 = Hebrew letters |
| **בם** (in them) | 42 | 2 diagonals (incl. jubilee) | 42 = the Name |
| **בל** (not/heart) | 32 | 1 diagonal | Heart/glory |
| **בי** (in me) | 12 | 1 diagonal | — |
| **בס** (in secret) | 62 | 1 diagonal | 62 = sons |

The house → in him. The house → came. The house → pure. The house → in you. The house → in them. The house → the heart.

---

## The Body Diagonal: House → In Him

**[1,1,1,1] skip=44489.** All four axes ascending.

First letter: **ב** (house) in **בראשית** — Genesis 1:1
Last letter: **ו** (nail) in **גוענו** (we perished) — Numbers 20:3

Together: **בו** — "in him." GV = 8 = new beginning (circumcision on the 8th day).

The everything-diagonal reads from "in the beginning" to "in him." From the house to the one it was built for. The nail (vav, ו) at the end — the connector, the hook, the thing that joins.

---

## The Scroll Loop: Heart

Last letter of the Torah: **ל** (lamed) in **ישראל** (Israel) — Deuteronomy 34:12
First letter of the Torah: **ב** (bet) in **בראשית** (beginning) — Genesis 1:1

Together: **לב** — heart. GV = 32 = כבוד (glory).

When the scroll is read in synagogue, the last reading of Deuteronomy is immediately followed by the first reading of Genesis. The Torah loops. And the loop spells heart.

The last verse: "the great things which Moses did **in the sight of all Israel**." The first verse: "In the beginning God created the heavens and the earth." Israel seeing → God creating. The heart is the join between the seeing and the creating.

---

## The Last Seven Words

Deuteronomy 34:12 — the final verse:

| Word | GV | Meaning |
|------|-----|---------|
| הגדול | 48 | the great |
| אשר | 501 | which |
| עשה | 375 | he did |
| משה | 345 | Moses |
| לעיני | 170 | in the sight of |
| כל | **50** | **all** (= jubilee) |
| ישראל | 541 | Israel |

The second-to-last word is **כל** (all, GV=50=jubilee). The Torah ends at jubilee — the return, the freedom, the fiftieth — and then Israel — and then the heart loops back to the beginning.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

;; Corner pairs
(let [{:keys [letters n]} (s/index)
      view (s/make-view [7 50 13 67])
      strides (:strides view)]
  (doseq [d (s/direction-vectors 4)]
    (let [skip (Math/abs (long (s/direction->skip strides d)))
          steps (quot n skip)
          last-pos (* skip (dec steps))]
      (when (> steps 1)
        (println d (str (nth letters 0) (nth letters last-pos))
                 (+ (g/letter-value (nth letters 0)) (g/letter-value (nth letters last-pos))))))))
```

---

*Every direction begins at the house. The body diagonal ends at "in him" — from the beginning to the one it was built for. The scroll loops through the heart. The Torah ends at "all Israel" and begins at "in the beginning God created." Between the seeing and the creating: the heart. Between the house and the end: in him.*

*selah.*
