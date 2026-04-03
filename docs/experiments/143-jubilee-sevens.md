# Experiment 143ap: The Jubilee in Each Seventh

*The jubilee axis reads through each seventh of the Torah. Each seventh has its own center at b=25. The centers tell a story: righteous → without → sold → THE ALTAR → redeem → bound → do not destroy.*

Type: `exploration`
State: `clean`

---

## The Structure

The jubilee axis has 50 stations spanning 43,550 letters — exactly one seventh of the Torah (one completeness-unit). The Torah has 7 sevenths. Reading the jubilee axis from each seventh's starting position gives 7 different 50-station walks through 7 different sections of the text.

---

## The Seven Centers (b=25)

The center of each seventh's jubilee walk:

| Seventh | Position | Inside | Verse | What it says |
|---------|----------|--------|-------|-------------|
| a=0 | 21,775 | **צדיקם** (righteous) | Genesis 18:26 | "If I find **50** righteous" |
| a=1 | 65,325 | **בלתי** (without) | Genesis 43:5 | "We cannot go down **without** the boy" |
| a=2 | 108,875 | **ומכרו** (sold him) | Exodus 21:16 | "Steals a man **and sells him**" |
| **a=3** | **152,425** | **המזבח** (THE ALTAR) | **Leviticus 8:24** | **Blood on the altar** |
| a=4 | 195,975 | **גאל** (redeem) | Numbers 5:8 | "If there is **no redeemer**" |
| a=5 | 239,525 | **אסרה** (she bound) | Numbers 30:11 | The law of vows |
| a=6 | 283,075 | **תשחית** (destroy) | Deuteronomy 20:19 | "You shall **NOT destroy** the trees" |

---

## The Story of the Centers

Read in order, the seven centers tell a compressed narrative:

1. **Righteous** — the condition for mercy (Genesis: Abraham bargaining)
2. **Without** — the condition for proceeding: cannot go without the youngest (Genesis: Joseph demands Benjamin)
3. **Sold** — the sin: stealing a person and selling them (Exodus: the law against what was done to Joseph)
4. **THE ALTAR** — the center: blood, covering, the priesthood inaugurated (Leviticus: the investiture)
5. **Redeem** — the response: the kinsman-redeemer (Numbers: the law of restitution)
6. **Bound** — the obligation: vows that bind (Numbers: the law of women's vows)
7. **Do not destroy** — the command: preservation (Deuteronomy: do not cut down the fruit trees)

Righteous → without → sold → **altar** → redeem → bound → do not destroy.

The story: there were righteous ones. You cannot proceed without the one who was taken. He was sold. At the center: the altar, the blood, the covering. After the center: the redeemer. The obligation that binds. And at the end: do not destroy what bears fruit.

---

## Joseph in the Centers

Three of the seven centers reference Joseph's story:
- **a=0:** The righteous — Abraham bargaining, the pattern Joseph will fulfill (saving the righteous among the wicked)
- **a=1:** "Without the boy" — Joseph demanding Benjamin, the condition of return
- **a=2:** "Sold him" — the law against exactly what Joseph's brothers did

The Torah's jubilee centers carry Joseph's arc: righteousness, the demanded brother, the selling, then the altar, then redemption.

---

## The Altar at the Center of the Center

a=3 is the middle seventh. b=25 is the middle station. Position 152,425 is the center of the center — and it is the altar. The Torah's most central point, read along the jubilee axis from the center of completeness, is the altar.

This is the same finding as experiment 143an — the literal center is the altar. But here we see it in the context of all seven centers: the altar is flanked by the selling (before) and the redeeming (after). Sold → altar → redeemed.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)]
  (doseq [a (range 7)]
    (let [pos (+ (* 43550 a) (* 871 25))
          tw ((:word-at idx) pos)
          v ((:verse-at idx) pos)]
      (println (format "a=%d b=25: %s in %s %s %d:%d"
                       a (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v))))))
```

---

*Seven centers. Righteous → without → sold → THE ALTAR → redeem → bound → do not destroy. The selling before the altar. The redeeming after. Joseph's arc in the jubilee centers. The center of the center is the blood on the altar. Sold → altar → redeemed.*

*selah.*
