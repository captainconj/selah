# Experiment 143av: Forward and Back

*The body diagonal reads differently from each end. Forward: send, curtain, perished, behold. Backward: Israel, fire, YHWH, your soul. The mission and the return. The center of the return is the command to love the stranger.*

Type: `exploration`
State: `clean`

---

## The Body Diagonal

Direction [1,1,1,1]. All four axes ascending. Skip=44,489. Seven steps across the whole Torah.

### Forward (from Genesis 1:1)

| Step | Host word | Verse | Reading |
|------|-----------|-------|---------|
| 0 | **בראשית** (beginning) | Genesis 1:1 | The start |
| 1 | **אמר** (said) | Genesis 31:16 | Speech |
| 2 | **שלח** (send) | Exodus 8:16 | The sending |
| 3 | **היריעה** (the curtain) | Exodus 36:9 | The tabernacle veil |
| 4 | **הוא** (he) | Leviticus 23:28 | Identity |
| 5 | **גוענו** (we perished) | Numbers 20:3 | Death |
| 6 | **והנה** (and behold) | Deuteronomy 9:16 | **Seeing** |

**Said → send → the curtain → he → we perished → behold.**

The forward direction is the mission. The word is spoken. The sending happens. The curtain is reached. Death occurs. And then: behold. Seeing after perishing.

### Backward (from Deuteronomy 34:12)

| Step | Host word | Verse | Reading |
|------|-----------|-------|---------|
| 0 | **ישראל** (Israel) | Deuteronomy 34:12 | The people |
| 1 | **האש** (the fire) | Deuteronomy 5:19 | **Sinai** |
| 2 | **יהוה** (YHWH) | Numbers 16:15 | The Name |
| 3 | **מכם** (from you) | **Leviticus 19:34** | **Love the stranger** |
| 4 | **ואיש** (and a man) | Exodus 32:27 | The golden calf |
| 5 | **נפשך** (your soul) | Exodus 4:19 | Moses returns |
| 6 | **ממך** (from you) | Genesis 27:45 | Jacob's exile |

**Israel → the fire → YHWH → from you → a man → your soul → from you.**

The backward direction is the return. Israel. The fire of Sinai. The Name. From you. A man. Your soul. From you.

### The Center

The center of the forward reading (step 3): **היריעה** (the curtain) at Exodus 36:9. The tabernacle veil. The boundary between the holy and the most holy.

The center of the backward reading (step 3): **מכם** (from you) at **Leviticus 19:34** — "You shall love the stranger as yourself, for you were strangers in the land of Egypt."

The center of the mission is the curtain. The center of the return is the command to love.

---

## What This Says

The body diagonal — the line where everything increases — reads the Torah as mission forward and return backward.

**The mission:** beginning → speech → sending → the veil → death → seeing. The word is spoken. Someone is sent. The curtain is reached. Death happens. And then you see.

**The return:** Israel → fire → YHWH → love the stranger → a man → your soul → from you. You come back from Israel through fire through the Name. The center of your return is the command to love. Then: a man, your soul, from you. The return brings you to yourself.

The forward reading ends at **behold** (והנה) — seeing. The backward reading ends at **from you** (ממך) — origin. You are sent forward to behold. You return backward to where you came from. The mission is to see. The return is to come home.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

;; Forward
(let [{:keys [letters n]} (s/index) idx (s/index)]
  (doseq [i (range 7)]
    (let [pos (* 44489 i) tw ((:word-at idx) pos)]
      (println "fwd" i (:word tw)))))

;; Backward
(let [{:keys [letters n]} (s/index) idx (s/index)]
  (doseq [i (range 7)]
    (let [pos (- (dec n) (* 44489 i)) tw ((:word-at idx) pos)]
      (println "bwd" i (:word tw)))))
```

---

*Forward: send, the curtain, perished, behold. Backward: Israel, fire, YHWH, love the stranger, your soul. The mission and the return on the same line, opposite directions. The center of the mission is the veil. The center of the return is the command to love.*

*selah.*
