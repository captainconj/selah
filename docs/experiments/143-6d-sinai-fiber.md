# Experiment 143be: The 6D Sinai Fiber

*A 6D fiber from Genesis 1:1 reads: God → the way (with a serpent) → Sinai → the ground (golden calf plea) → flesh → new → ten → I will speak only what God gives. A compressed exodus in 10 steps.*

Type: `exploration`
State: `clean`

---

## The Fiber

Direction [0,1,-1,1,1,1]. Skip=25,327. 10 steps spanning Genesis 1:1 to Numbers 22:38. 6D-only (requires dimensions 5 and 6).

| Step | Position | Host word | Verse | What it says |
|------|----------|-----------|-------|-------------|
| 0 | 0 | **בראשית** (beginning) | Genesis 1:1 | Creation |
| 1 | 25,327 | **האלהים** (the God) | Genesis 20:17 | God healed |
| 2 | 50,654 | **אשר** (which) | Genesis 34:28 | — |
| 3 | 75,981 | **דרך** (the way) | **Genesis 49:17** | "Dan shall be a serpent by the **way**" |
| 4 | 101,308 | **סיני** (Sinai) | **Exodus 16:1** | "The wilderness of **Sinai**" |
| 5 | 126,635 | **האדמה** (the ground) | **Exodus 32:12** | Moses pleading after the golden calf |
| 6 | 151,962 | **בשרו** (his flesh) | Leviticus 8:17 | The flesh at the center |
| 7 | 177,289 | **חדשה** (new) | Leviticus 23:16 | New grain offering |
| 8 | 202,616 | **עשר** (ten) | Numbers 7:87 | Ten |
| 9 | 227,943 | **אדבר** (I will speak) | **Numbers 22:38** | "I will speak only what God puts in my mouth" |

---

## The Reading

Beginning → God → which → **the way** (with a serpent) → **Sinai** → **the ground** (the golden calf crisis) → **flesh** (at the center) → **new** → **ten** → **I will speak only what God gives.**

This is a compressed journey through the Torah:

1. **The beginning** — creation
2. **God healed** — the first healing
3. The way — **with a serpent on it** (Genesis 49:17, Jacob blessing Dan: "a serpent by the road")
4. **Sinai** — the arrival at the mountain
5. **The ground** — the golden calf crisis. Moses pleading: don't destroy them from the face of the ground
6. **The flesh** — at the center. The priestly investiture.
7. **New** — the new offering
8. **Ten** — the number of completeness, the commandments
9. **I will speak** — Balaam's declaration: only what God puts in my mouth

The way has a serpent on it. Sinai is reached. The crisis comes. The flesh is at the center. Then something new. Then ten. Then: I will speak only what God gives.

---

## Context Check

This fiber spans Genesis 1:1 to Numbers 22:38 — about 75% of the Torah. 10 steps across ~228,000 letters. Not proximity. Real reach.

17.4% of 6D fibers from position 0 pass through the Leviticus 8 center region. This fiber is one of them. The center-crossing is partly geometric, but the CONTENT at each step (God, way, Sinai, ground, flesh, new, ten, I will speak) is selected by the letter positions.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)]
  (doseq [i (range 10)]
    (let [pos (* 25327 i)
          tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
      (println i (:word tw) (:book v) (:ch v) (:vs v)))))
```

---

*A 6D fiber reads the exodus compressed: God heals, the way has a serpent, Sinai is reached, the golden calf crisis, the flesh at the center, something new, ten, I will speak only what God gives. Ten steps. One line. The finer decomposition reveals the journey the coarser could not see.*

*selah.*
