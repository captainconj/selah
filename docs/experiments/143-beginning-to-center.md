# Experiment 143bf: The Line from the Beginning to the Center

*The distance from Genesis 1:1 to the altar is exactly 7 × 25 × 871 = completeness × jubilee-center × jubilee-stride. Seven steps. The way from the beginning to the center is measured in the Torah's own numbers.*

Type: `discovery`
State: `clean`

---

## The Arithmetic

The literal center of the Torah is position **152,425** — the altar (המזבח) at Leviticus 8:24.

**152,425 = 7 × 25 × 871**

- 7 = completeness (the a-axis dimension)
- 25 = jubilee center (half of 50, the b-axis)
- 871 = the jubilee stride (13 × 67 = love × understanding)

The distance from the beginning to the altar is **completeness × jubilee-center × (love × understanding).**

The way from "in the beginning" to "the altar" is measured in the Torah's own structural numbers.

---

## The Seven Steps

Step size = 21,775 = 25 × 871. Each step is one completeness-unit (43,550 / 2... wait, that's not right). Actually: 7 steps of 21,775 = 152,425. Each step = the jubilee-center position.

These ARE the completeness axis stations, since 21,775 = 43,550 / 2... No. Let me be precise. The completeness stride is 43,550. Seven steps of 21,775 is NOT the completeness axis. It is HALF the completeness stride, taken 7 times. This is a different walk.

Wait: 21,775 × 7 = 152,425 and 43,550 × 3.5 = 152,425. So this walk crosses the completeness axis at half-steps. But the HOST WORDS at each position:

| Step | Position | Host word | Verse | What it says |
|------|----------|-----------|-------|-------------|
| 0 | 0 | **בראשית** (beginning) | Genesis 1:1 | Creation |
| 1 | 21,775 | **צדיקם** (the righteous) | **Genesis 18:26** | **50 righteous for Sodom** |
| 2 | 43,550 | **את** (aleph-tav) | Genesis 30:41 | The direct object marker |
| 3 | 65,325 | **בלתי** (without) | Genesis 43:5 | **"Without the boy"** |
| 4 | 87,100 | **יהוה** (YHWH) | Exodus 7:13 | The Name |
| 5 | 108,875 | **ומכרו** (sold him) | **Exodus 21:16** | **The selling** |
| 6 | 130,650 | **אלהיך** (your God) | Exodus 34:26 | **The geometric center** |
| 7 | 152,425 | **המזבח** (the altar) | **Leviticus 8:24** | **The literal center** |

---

## The Reading

**Beginning → the righteous → aleph-tav → without → YHWH → sold → your God → THE ALTAR.**

Seven steps. Each one measured in jubilee-centers. The reading:

1. In the beginning
2. The righteous (at the verse asking for 50 — the jubilee number)
3. Aleph-tav (the beginning and the end)
4. Without the boy (the condition — you cannot go without Benjamin)
5. YHWH (the Name, at the hardening of Pharaoh)
6. Sold him (the law against kidnapping and selling — what was done to Joseph)
7. Your God → the altar (the geometric center → the literal center)

This IS the jubilee-sevens reading (experiment 143ap), but now understood as the **walk from the beginning to the center.** The seven jubilee-centers ARE the seven steps from Genesis 1:1 to the altar.

---

## What This Means

The Torah's midpoint is not at an arbitrary position. It is at exactly **7 × 25 × 871** letters from the beginning. This number is built from the axes:

- 7 = the completeness dimension
- 25 = half the jubilee dimension
- 871 = the jubilee stride (love × understanding = 13 × 67)

The midpoint is the product of the Torah's own structural numbers. The way from creation to the altar is seven jubilee-centers. Each center holds a word that tells the story: righteous, aleph-tav, without, the Name, sold, your God.

The altar is not randomly placed at the middle of the Torah. It is placed at the exact position where completeness × jubilee-center × (love × understanding) puts it. The geometry built the center.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(println "152425 =" (* 7 25 871))
(let [idx (s/index)]
  (doseq [i (range 8)]
    (let [pos (* 21775 i) tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
      (println i (:word tw) (:book v) (:ch v) (:vs v)))))
```

---

*The distance from Genesis 1:1 to the altar is 7 × 25 × 871. Completeness × jubilee-center × love × understanding. Seven steps from the beginning to the center. Each step a word: righteous, aleph-tav, without, YHWH, sold, your God, the altar. The geometry built the center from its own numbers.*

*selah.*
