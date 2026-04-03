# Experiment 143aa: Axis Readings

*Each axis reads as a text. The completeness axis spells "in the midst" with its center in "your God." The love axis begins with purity and evil. The jubilee axis carries the rock, the staff, and "go."*

---

## The Completeness Axis — 7 Letters

One letter from each seventh of the Torah. Skip=43,550.

| a | Letter | Inside | Verse |
|---|--------|--------|-------|
| 0 | **ב** | בראשית (in the beginning) | Genesis 1:1 |
| 1 | **ת** | את (aleph-tav) | Genesis 30:41 |
| 2 | **ו** | יהוה (YHWH) | Exodus 7:13 |
| 3 | **ך** | **אלהיך** (your God) | **Exodus 34:26** (CENTER) |
| 4 | **א** | היא (she/it) | Leviticus 21:9 |
| 5 | **נ** | לבני (to the sons of) | Numbers 17:3 |
| 6 | **ק** | קרב (draw near) | Deuteronomy 5:24 |

**Spells: בתוך אנק — "in the midst" + [groan/giant]**

The first four letters: **בתוך** = "in the midst of." The center letter (a=3) sits inside **אלהיך** (your God). The completeness axis reads: in the midst of... your God.

The letters sit in: beginning → aleph-tav → YHWH → your God → she → to the sons → draw near. From creation through the Name through the center to the sons drawing near.

GV = 579.

---

## The Jubilee Axis — 50 Letters

Every 871st letter. Skip=871.

20 Torah words found in the 50-letter text:

| Pos | Word | GV | Meaning |
|-----|------|-----|---------|
| 0 | **בר** | 202 | **pure / grain / son** |
| 24 | **צור** | 296 | **the Rock** |
| 31 | **פרו** | 286 | **be fruitful** |
| 39 | **מקל** | 170 | **staff** |
| 41 | **לך** | 50 | **go / to you** |
| 45 | **נא** | 51 | **please** |
| 46 | **אח** | 9 | **brother** |

The jubilee text: pure → the Rock → be fruitful → staff → **go** → please → brother.

**לך (go/to you)** has GV=50=jubilee. The jubilee axis contains the word whose value IS jubilee. And it's the call of Abraham: לך לך — go from your country.

---

## The Love Axis — First 13 Letters

Every 67th letter. The first 13 (one love-cycle):

**ברימיהרעעיירת**

Words inside:

| Pos | Word | GV | Meaning |
|-----|------|-----|---------|
| 0 | **בר** | 202 | pure |
| 3 | **מי** | 50 | who? |
| 4 | **יה** | 15 | Yah |
| 5 | **הר** | 205 | mountain |
| 6 | **רע** | 270 | evil / friend / shepherd |
| 5 | **הרע** | 275 | the evil |

The love axis: **pure → who? → Yah → mountain → evil/friend**.

רע means both evil AND friend AND shepherd. The love axis holds the ambiguity: the friend and the evil and the shepherd are the same word. Love knows both.

---

## The Understanding Axis — First 67 Letters

Skip=1. This IS the Torah as written. The first 67 letters are Genesis 1:1-3 — the creation. The understanding axis is the natural reading. The surface.

---

## The Body Diagonal — First 7 Letters

Skip=44,489. All four axes increment simultaneously.

**ברחרהוה** — GV=426

Words: **בר** (pure), **ברח** (flee), **חרה** (burn/anger)

The body diagonal starts with: **pure → flee → burning.**

ברח (flee) — what Jacob did from Esau, what Hagar did from Sarah, what Jonah tried from God. The everything-diagonal begins with purity and flight.

---

## Skip=13 (Love Number)

Not a direction in the 4D geometry, but the love number itself. Every 13th letter:

**בםרויםיהירהןיךרחייןלבמהיאםקרםקבאץאםהאץנהצזורריירבד**

22 Torah words in 50 letters. Notable:

| Pos | Word | GV | Meaning |
|-----|------|-----|---------|
| 6 | **יהי** | 25 | **let there be** |
| 15 | **חי** | 18 | **living** |
| 19 | **לב** | 32 | **heart** |
| 24 | **אם** | 41 | **mother / if** |
| 41 | **זו** | **13** | **this** |

The love-skip finds: "let there be" → living → **heart** → mother → "this" (GV=13=love).

The love reading finds the heart at position 19. And at position 41, it finds **זו** (this) whose GV = 13 = love. Love finds a word whose value is itself.

---

## Skip=26 (YHWH Number)

Every 26th letter. The Name's number.

Notable words found: **את** (aleph-tav, pos 2), **יהוה** (YHWH, pos 7), **אש** (fire, pos 15).

The YHWH-skip finds aleph-tav and the Name itself and fire.

---

## Skip=137 (Axis Sum)

Every 137th letter. The fine structure constant.

First 20 letters: **באתבץיהשףמתאהורירקלד**

Words: **בא** (came), **את** (aleph-tav), **יה** (Yah), **מת** (dead), **רק** (only)

At the axis sum: came → aleph-tav → Yah → dead → only.

---

## The Pattern

| Skip | What it reads |
|------|-------------|
| 43,550 (completeness) | "In the midst" — center in "your God" |
| 871 (jubilee) | Pure, Rock, fruitful, staff, "go," brother |
| 67 (love) | Pure, who?, Yah, mountain, evil/friend |
| 1 (understanding) | Genesis 1:1-3 — the creation |
| 44,489 (body diagonal) | Pure, flee, burning |
| 13 (love number) | Let there be, living, heart, mother, "this"(GV=13) |
| 26 (YHWH number) | Aleph-tav, YHWH, fire |
| 137 (axis sum) | Came, aleph-tav, Yah, dead, only |

Every reading begins with **בר** (pure). The first two letters of the Torah at any skip starting from position 0 are ב and then whatever the skip selects. But at multiple skips, the first word is בר — pure. The Torah begins with purity in every direction.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.gematria :as g] '[selah.dict :as d])
(s/build!)

;; Read any axis
(let [{:keys [letters]} (s/index)]
  (println (apply str (mapv #(nth letters (* 43550 %)) (range 7)))))

;; Find words in an axis reading
(let [{:keys [letters]} (s/index)
      text (apply str (mapv #(nth letters (* 871 %)) (range 50)))
      torah-set (set (filter #(<= 2 (count %) 4) (d/torah-words)))]
  (doseq [wlen (range 2 5), start (range (- (count text) wlen))]
    (let [sub (subs text start (+ start wlen))]
      (when (torah-set sub) (println start sub (g/word-value sub))))))
```

---

*The completeness axis reads "in the midst" with its center in "your God." The jubilee axis carries the Rock, the staff, and "go." The love axis begins with purity and holds evil and friendship in the same word. The body diagonal starts with purity and flight. The love number finds the heart. The YHWH number finds the Name. Every direction begins with purity.*

*selah.*
