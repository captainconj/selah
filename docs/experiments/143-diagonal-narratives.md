# Experiment 143am: Diagonal Narratives

*Each diagonal tells a different story. The freedom-love diagonal reads the fall. Its reverse reads the return. The near-body diagonals reconstruct Genesis 1:1. One diagonal reads: YHWH said to you on the day.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143am_diagonal_narratives.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143am-diagonal-narratives :as exp]) (s/build!) (exp/run-all)"`

---

## The Freedom-Love Diagonal [0,+1,-1,0]

Skip=804. Jubilee ascending, love descending.

| Step | Inside | Verse |
|------|--------|-------|
| 0 | בראשית (beginning) | Genesis 1:1 |
| 1 | אתם (them) | Genesis 1:17 |
| 2 | כל (all) | Genesis 1:30 |
| 3 | **האדם** (the man) | **Genesis 2:16** — the command |
| 4 | **ותאכל** (she ate) | **Genesis 3:6** — the eating |
| 5 | **כתנות** (garments) | **Genesis 3:21** — God clothes them |
| 6 | **ונד** (wandering) | **Genesis 4:14** — Cain the fugitive |

**The fall in four steps.** The direction that gains freedom and releases love reads: command → eating → clothing → wandering. Between the sin and the exile, God makes garments.

**The reverse** = the return. Wandering → garments → the eating → the man → all → beginning. The direction that gains love and releases freedom reads the fall backward — the way home.

---

## The Near-Body Diagonals Reconstruct Genesis 1:1

**[1,0,0,-1] skip=43549:**

בראשית → **את** → **יהוה** → **אלהיך** → אביה → לאות → כמנו

Beginning → aleph-tav → YHWH → your God → her father → for a sign → like us.

The first three host words reconstruct the skeleton of Genesis 1:1: "In the beginning... aleph-tav... YHWH." The diagonal reassembles the opening.

---

## "YHWH Said to You on the Day"

**[1,1,1,-1] skip=44487:**

בראשית → **אמר** → **יהוה** → **אתם** → **יום** → ויאמרו

Beginning → **said** → **YHWH** → **you** → **day** → and they said.

Five host words that read as a near-sentence: YHWH said to you on the day. And then: "they said." The response.

---

## "He Ate, YHWH, the Sojourner, the Seventh"

**[1,1,0,1] skip=44422:**

בראשית → **ויאכל** (he ate) → ובבהמה (among beasts) → **יהוה** → **ולגר** (the sojourner) → **השביעי** (the seventh)

The eating → the Name → the stranger → the Sabbath. A compressed Torah along one diagonal: what was eaten, the Name that responds, the stranger who is welcomed, the rest at the end.

---

## "She Answered, Egypt, God"

**[1,1,-1,-1] skip=44353:**

בראשית → **ותען** (she answered) → **מצרים** (Egypt) → **אל** (God) → יהיו → מישראל

She answered → Egypt → God → they shall be → from Israel. A compressed exodus.

---

## The Body Diagonal

**[1,1,1,1] skip=44489:**

בראשית → **אמר** (said) → **שלח** (send) → היריעה (the curtain) → **הוא** (he) → גוענו (we perished) → והנה (behold)

Said → send → curtain → he → we perished → behold. Speech, sending, the tabernacle curtain, identity, death, and then: behold.

---

## The Pattern

Each diagonal direction tells a different compressed story:

| Direction | What varies | Story |
|-----------|------------|-------|
| [0,+1,-1,0] | freedom↑ love↓ | **The fall**: command → ate → clothed → wandering |
| [0,-1,+1,0] | freedom↓ love↑ | **The return**: wandering → clothed → the man → beginning |
| [1,0,0,-1] | comp↑ und↓ | **Genesis 1:1 reconstructed**: beginning → AT → YHWH → your God |
| [1,1,1,-1] | 3 axes↑ und↓ | **"YHWH said to you on the day"** |
| [1,1,0,1] | 3 axes↑ love↓ | **Compressed Torah**: ate → YHWH → sojourner → Sabbath |
| [1,1,-1,-1] | comp+jub↑ love+und↓ | **Compressed exodus**: she answered → Egypt → God |
| [1,1,1,1] | all↑ | **Said → send → curtain → he → perished → behold** |

The diagonals are compressed stories. Each direction compresses a different narrative. The fall lives on the freedom-without-love diagonal. The exodus lives on the diagonal that rises on completeness and jubilee while releasing love and understanding. Genesis 1:1 reassembles on the diagonal that rises on completeness while releasing understanding.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)
      view (s/make-view [7 50 13 67])
      strides (:strides view)]
  ;; Any diagonal
  (let [skip 804]
    (doseq [i (range 7)]
      (let [pos (* skip i)
            tw ((:word-at idx) pos)]
        (println i (:word tw) (:book ((:verse-at idx) pos)) (:ch ((:verse-at idx) pos)))))))
```

---

*Each diagonal is a compressed story. The fall lives on the line where freedom increases and love decreases. The return is the same line read backward. Genesis 1:1 reassembles on a diagonal. One diagonal reads: YHWH said to you on the day. The geometry assigns each narrative to the direction that produces it.*

*selah.*
