# Experiment 143ag: Axis Centers

*The center of completeness is "your God." The center of jubilee is "the righteous" — at the verse that asks for 50 righteous in Sodom. The center of love is the naming. The center of understanding is the void.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143ag_axis_centers.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ag-axis-centers :as exp]) (s/build!) (exp/run-all)"`

---

## Method

Each axis in the canonical 4D has a center — the midpoint of its dimension. The center of the completeness axis (a, dim=7) is a=3. The center of the jubilee axis (b, dim=50) is b=25. The center of the love axis (c, dim=13) is c=6. The center of the understanding axis (d, dim=67) is d=33.

Each center corresponds to a position in the Torah (center × stride). We asked: what Torah word does each center letter sit inside? What verse?

---

## The Four Centers

### Completeness (a=3): YOUR GOD

Position 130,650. The letter **ך** inside **אלהיך** — "your God."

**Exodus 34:26** — "The first of the firstfruits of your ground you shall bring to the house of the LORD **your God**. You shall not boil a young goat in its mother's milk."

The center of completeness is your God. And the verse is about firstfruits — the first of what the ground produces, brought to the house. The complete thing returned to its source.

### Jubilee (b=25): THE RIGHTEOUS

Position 21,775. The letter **צ** inside **צדיקם** — "the righteous."

**Genesis 18:26** — "And the LORD said, 'If I find **fifty** righteous in the city, I will spare the whole place for their sake.'"

The jubilee axis has dimension 50. Its center is at the verse that asks for **50** righteous. The number of the axis appears in the verse at its center. The jubilee centers on righteousness, and the count it demands is its own dimension.

### Love (c=6): AND HE CALLED

Position 402. The letter **ר** inside **ויקרא** — "and he called."

**Genesis 1:10** — "God **called** the dry land Earth, and the gathering of the waters he called Seas. And God saw that it was good."

The center of love is the naming. The act of calling something by its name. Love calls things by name. The center of the love axis is the first naming — God looking at what He separated and giving it a word.

### Understanding (d=33): IT WAS

Position 33. The letter **ה** inside **היתה** — "it was."

**Genesis 1:2** — "The earth **was** formless and void, and darkness was over the face of the deep."

The center of understanding is the void. Before creation took shape. Before the light was separated from darkness. Understanding begins at emptiness — at the place where everything is potential and nothing is yet named.

---

## The Pattern

| Axis | Center | Inside | Verse | What it means |
|------|--------|--------|-------|--------------|
| **Completeness** (a=7) | a=3 | **אלהיך** (your God) | Exodus 34:26 | Completeness centers on God |
| **Jubilee** (b=50) | b=25 | **צדיקם** (the righteous) | Genesis 18:26 | Jubilee centers on 50 righteous |
| **Love** (c=13) | c=6 | **ויקרא** (and he called) | Genesis 1:10 | Love centers on naming |
| **Understanding** (d=67) | d=33 | **היתה** (it was) | Genesis 1:2 | Understanding centers on the void |

Each axis centers on a word that speaks its nature:
- Completeness → your God (the source of completeness)
- Jubilee → the righteous (the condition of freedom, counted at 50)
- Love → the naming (love calls by name)
- Understanding → the void (understanding begins at emptiness)

---

## The Body Diagonal

Reading from corner (0,0,0,0) to corner (6,49,12,66) — the body diagonal at skip=44,489:

| Step | Position | Letter | Inside | Verse |
|------|----------|--------|--------|-------|
| 0 | 0 | **ב** | בראשית (in the beginning) | Genesis 1:1 |
| 1 | 44,489 | **ר** | אמר (said) | Genesis 31:16 |
| 2 | 88,978 | **ח** | שלח (send) | Exodus 8:16 |
| 3 | 133,467 | **ר** | היריעה (the curtain) | Exodus 36:9 |
| 4 | 177,956 | **ה** | הוא (he) | Leviticus 23:28 |
| 5 | 222,445 | **ו** | גוענו (we perished) | Numbers 20:3 |
| 6 | 266,934 | **ה** | והנה (and behold) | Deuteronomy 9:16 |

Letters: **ב ר ח ר ה ו ה**

Contains:
- **בר** (pure/son, GV=202) — positions 0-1
- **ברח** (flee, GV=210=choose) — positions 0-2
- **חר** (burn, GV=208=Isaac=Hagar) — positions 2-3
- **הוה** (Eve/being/becoming) — positions 4-6

The body diagonal reads: pure → flee → burn → being. Or: the son flees, burns, and becomes.

GV of חר = 208 = יצחק (Isaac) = הגר (Hagar). The burning at the center of the diagonal carries the number of both the son of promise and the servant sent away.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

;; Axis centers
(let [{:keys [letters]} (s/index) idx (s/index)]
  (doseq [[skip label center] [[43550 "completeness" 3] [871 "jubilee" 25]
                                 [67 "love" 6] [1 "understanding" 33]]]
    (let [pos (* skip center)
          v ((:verse-at idx) pos)
          tw ((:word-at idx) pos)]
      (println label "center:" (nth letters pos) "in" (:word tw) (:book v) (:ch v) (:vs v)))))

;; Body diagonal
(let [{:keys [letters]} (s/index) idx (s/index)]
  (doseq [i (range 7)]
    (let [pos (* 44489 i)]
      (println i (nth letters pos) (:word ((:word-at idx) pos))))))
```

---

*Completeness centers on your God. Jubilee centers on the righteous — at the verse that asks for 50, its own number. Love centers on the naming. Understanding centers on the void. The body diagonal reads: pure, flee, burn, being. Each axis knows what it holds.*

*selah.*
