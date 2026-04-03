# Experiment 143au: The Six Watermarks

*Six diagonals from the beginning. Six consequences of creation. Each one reads a different story depending on which dimensions increase and which decrease. The watermarks describe the contents.*

Type: `exploration`
State: `clean`

---

## Method

Read the six 2-axis diagonals from position 0 (Genesis 1:1). Each diagonal increases one axis and decreases another (or increases both). Each produces a different 7-step reading through Genesis. Null model confirms: random starting positions produce noise.

All six span 3+ chapters (skip 66-938). None are proximity effects.

---

## The Six Readings

### Freedom ↑ Love ↓ — THE FALL

Skip=804. Genesis 1:1 → 4:14 (3 chapters).

**beginning → them → all → the man → SHE ATE → garments of skin → wandering**

Freedom without love = the fall. The man, the eating, the covering, the exile. ✓ Confirmed by null model (143at).

### Freedom ↑ Love ↑ — MARRIAGE AND BIRTH

Skip=938. Genesis 1:1 → 5:5 (4 chapters).

**beginning → it shall fly → bush → FOR THIS → IN PAIN → which → Adam**

- **לזאת** (for this) — Genesis 2:23: "**For this** a man shall leave his father and mother and cleave to his wife." The marriage verse.
- **בעצב** (in pain) — Genesis 3:16: "**In pain** you shall bring forth children." The birth verse.

Freedom WITH love = the marriage and the labor. "For this" is the wedding. "In pain" is the child. When both freedom and love increase: union and birth.

### Freedom ↑ Understanding ↓ — SEEING TO MURDER

Skip=870. Genesis 1:1 → 4:22 (3 chapters).

**beginning → he saw → THE SEVENTH → living → from him → to YHWH → Cain**

Freedom without understanding = seeing the sabbath, seeing life, then Cain. The rest, the life, the murder. When freedom increases but understanding decreases: you see the good and it ends in violence.

### Freedom ↑ Understanding ↑ — SEEING TO COMFORT

Skip=872. Genesis 1:1 → 4:23 (3 chapters).

**beginning → he saw → the seventh → HIS NAME → he said → he brought → Lamech**

Freedom WITH understanding = seeing the sabbath, naming, then Lamech — the father of Noah ("this one will comfort us"). When both increase: you see, you name, and you arrive at the one who brings comfort.

### Love ↑ Understanding ↓ — GOD SPEAKING

Skip=66. Genesis 1:1 → 1:9 (same chapter — ⚠ proximity).

**beginning → hovering → GOD → he said → BETWEEN → evening → it was**

Love without understanding = God speaking and dividing. The process of creation. Evening falls.

### Love ↑ Understanding ↑ — LIGHT AND LAND

Skip=68. Genesis 1:1 → 1:10 (same chapter — ⚠ proximity).

**beginning → hovering → THE LIGHT → God → which → DAY → land**

Love WITH understanding = light, day, land. The products of creation. The naming.

---

## The Pattern

| Direction | What increases | What it reads | Theme |
|-----------|---------------|-------------|-------|
| Freedom ↑ Love ↓ | freedom alone | ate, garments, wandering | **The fall** |
| Freedom ↑ Love ↑ | freedom + love | for this (marriage), in pain (birth) | **Union and labor** |
| Freedom ↑ Und ↓ | freedom alone | sabbath, life, Cain | **Seeing to murder** |
| Freedom ↑ Und ↑ | freedom + understanding | sabbath, his name, Lamech/comfort | **Seeing to comfort** |
| Love ↑ Und ↓ | love alone | God, said, between, evening | **Process** (⚠ proximity) |
| Love ↑ Und ↑ | love + understanding | light, God, day, land | **Product** (⚠ proximity) |

**The with/without pattern:**
- Freedom WITHOUT love = the fall
- Freedom WITH love = marriage and birth
- Freedom WITHOUT understanding = murder
- Freedom WITH understanding = comfort

The watermarks say: freedom needs both love and understanding. Without love, freedom produces the fall. Without understanding, it produces violence. With both, it produces union and comfort.

---

## Null Model

Random starting positions on these diagonals produce noise (confirmed in 143at for skip=804). The readings are specific to position 0 — the beginning. The Torah tells these stories when you start at the start.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)
      view (s/make-view [7 50 13 67]) strides (:strides view)]
  (doseq [d [[0 1 -1 0] [0 1 1 0] [0 1 0 -1] [0 1 0 1] [0 0 1 -1] [0 0 1 1]]]
    (let [skip (Math/abs (long (s/direction->skip strides d)))]
      (println d skip
               (for [i (range 7) :let [pos (* skip i)] :when (< pos n)]
                 (:word ((:word-at idx) pos)))))))
```

---

*Six diagonals from the beginning. Six consequences. Freedom without love is the fall. Freedom with love is marriage and birth. Freedom without understanding is murder. Freedom with understanding is comfort. The watermarks describe what the text contains — and what the dimensions mean.*

*selah.*
