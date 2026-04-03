# Experiment 143aj: Walking the Jubilee Axis

*The jubilee axis reads a story. Beginning → Sabbath → accusation → covering → the righteous → your brother → I served. Freedom begins at rest and ends at the one who served.*

Type: `exploration`
State: `clean`

---

## The Walk

The jubilee axis has 50 stations — one every 871 letters (the b-stride). Each station is one letter in the Torah. The Torah word containing that letter tells us what the jubilee holds at that point.

| Station | Position | Inside | Verse | What it says |
|---------|----------|--------|-------|-------------|
| b=0 | 0 | **בראשית** (in the beginning) | Genesis 1:1 | Creation |
| b=2 | 1,742 | **השביעי** (the seventh) | **Genesis 2:2** | **The Sabbath** |
| b=3 | 2,613 | **הוא** (he) | Genesis 2:19 | Adam naming |
| b=4 | 3,484 | **אכלת** (you ate) | **Genesis 3:11** | **The accusation** |
| b=13 | 11,323 | **וישימו** (they placed) | **Genesis 9:23** | **Covering Noah** |
| b=25 | 21,775 | **צדיקם** (the righteous) | **Genesis 18:26** | **50 righteous** |
| b=42 | 36,582 | **אחיך** (your brother) | Genesis 27:6 | The stolen blessing |
| b=49 | 42,679 | **עבדתי** (I served) | **Genesis 30:26** | **Jacob's service** |

---

## The Story

**b=0: In the beginning.** The jubilee starts where everything starts.

**b=2: The seventh.** Station 2 holds the Sabbath — the rest at the end of creation. The jubilee (the 50th) finds the seventh (the rest) almost immediately. Freedom's first landmark is rest.

**b=4: You ate.** The accusation. "Have you eaten from the tree I commanded you not to eat from?" The jubilee passes through the moment of disobedience. The freedom axis holds the memory of the loss of freedom.

**b=13: They placed.** Station 13 — love's number. Genesis 9:23 — Shem and Japheth walking backward to cover their father's nakedness. The covering at love's station. Love covers.

**b=25: The righteous.** The CENTER. Genesis 18:26 — "If I find **fifty** righteous in Sodom..." The jubilee axis has 50 stations. Its center verse asks for 50 righteous. The axis and the verse carry the same number.

**b=42: Your brother.** Rebekah overhearing Isaac speak to Esau. The stolen blessing. Brotherhood and deception.

**b=49: I served.** The last station. Genesis 30:26 — Jacob to Laban: "Give me my wives and my children, for whom I have **served** you, and let me go." The jubilee — the axis of freedom — ends with the man who served 14 years for the women he loved and asks to be released.

---

## The Arc

Beginning → rest → naming → accusation → covering → righteousness → brotherhood → service.

The jubilee axis traces the journey from creation to earned freedom. It passes through every major turn: the Sabbath (the prototype of freedom), the fall (the loss of freedom), the covering (love's response to shame), the bargaining for the righteous (the price of mercy), and the service (the work that precedes release).

Freedom begins at rest (b=2) and ends at service (b=49). The Sabbath and the labor. The jubilee contains both — because jubilee is not escape from work but the completion of it. "I have served you" is how freedom is spoken.

---

## The Numbers

Station 13 (love) holds the covering. The letter at station 13 is ו (vav, GV=6), inside the word וישימו (they placed). The covering is at love, and the letter is the nail.

Station 25 (center) holds the righteous. The letter is צ (tsade, GV=90), inside צדיקם. Tsade is the letter of righteousness — the righteous one (צדיק) begins with tsade. The center letter of the jubilee IS the letter of the righteous.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters]} (s/index) idx (s/index)]
  (doseq [b (range 50)]
    (let [pos (* 871 b)
          v ((:verse-at idx) pos)
          tw ((:word-at idx) pos)]
      (println (format "b=%d pos=%d %s in %s %s %d:%d"
                       b pos (nth letters pos) (:word tw) (:book v) (:ch v) (:vs v))))))
```

---

*The jubilee axis walks from beginning to service. The Sabbath is its second station. The accusation is its fourth. The covering is at love's number. The righteous are at the center — and the center asks for fifty, the axis's own count. Freedom begins at rest and ends at the one who served.*

*selah.*
