# Experiment 143an: The Center Is the Altar

*The center letter of the Torah is the last letter of "the altar." Every diagonal passes through it. The altar is where every direction crosses.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143an_center_altar.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143an-center-altar :as exp]) (s/build!) (exp/run-all)"`

---

## The Center

Position 152,425 — the literal midpoint of 304,850 letters. The letter is **ח** (chet), the last letter of **המזבח** (the altar), at **Leviticus 8:24**:

> "Moses brought Aaron's sons forward and put some of the blood on the lobes of their right ears, on the thumbs of their right hands, and on the big toes of their right feet. Then Moses sprinkled the blood on **the altar round about**."

The center of the Torah is the altar at the priestly investiture. The blood on the altar. Round about.

The next word after the altar: **סביב** — "round about."

---

## Every Direction Crosses the Altar

We tested seven diagonals from the center position. Every one has המזבח (the altar) as its center host word:

| Direction | What varies | Center host |
|-----------|-----------|-------------|
| [0,+1,-1,0] | freedom↑ love↓ | **המזבח** |
| [0,0,+1,+1] | love+understanding↑ | **המזבח** |
| [1,1,1,1] | all axes↑ | **המזבח** |
| [1,0,0,-1] | completeness↑ understanding↓ | **המזבח** |
| [0,+1,+1,0] | freedom+love↑ | **המזבח** |
| [0,+1,-1,+1] | freedom+understanding↑ love↓ | **המזבח** |
| [1,1,1,-1] | 3 axes↑ understanding↓ | **המזבח** |

This is structural, not coincidental — the center position IS inside the altar word, so any direction from that position passes through it. But the observation stands: the Torah placed the altar at its center. Every path through the space crosses the altar.

---

## What Surrounds the Altar

The words immediately surrounding position 152,425:

| Position | Letter | Word | Verse |
|----------|--------|------|-------|
| 152,420 | ל | **על** (upon) | Leviticus 8:24 |
| 152,421-425 | ה מ ז ב ח | **המזבח** (the altar) | Leviticus 8:24 |
| 152,426-429 | ס ב י ב | **סביב** (round about) | Leviticus 8:24 |
| 152,430 | ו | **ויקח** (and he took) | Leviticus 8:25 |

Upon → the altar → round about → and he took.

The center of the Torah reads: upon the altar, round about, and he took. The blood is on the altar, surrounding it, and then the taking — of the fat, of the offering, of what is given.

---

## The Number

GV of המזבח = 62 = **בני** (sons of).

The altar's gematria is the same as "sons of." The altar and the sons share a number. The place of sacrifice and the children of the covenant.

---

## The Context

Leviticus 8:24 is the investiture of the priests — Aaron and his sons being consecrated. Blood on their ears (to hear), thumbs (to work), toes (to walk). Then blood on the altar, round about.

This is not a random moment in Leviticus. It is the moment the priesthood begins. The moment the system of mediation between God and Israel is inaugurated. The center of the Torah is the inauguration of the priesthood at the altar.

We already knew the center of the 4D space (position 130,650 at Leviticus 8:35) says "guard the charge of the LORD." That is 10 verses later in the same ceremony. The altar (152,425) and the charge (130,650) are both in Leviticus 8 — the same investiture.

The literal center and the geometric center are in the same chapter. The altar and the charge. The place and the instruction.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

;; The center letter
(let [idx (s/index) pos 152425]
  (println (nth (:letters idx) pos)
           (:word ((:word-at idx) pos))
           (let [v ((:verse-at idx) pos)] [(:book v) (:ch v) (:vs v)])))

;; All directions from center
(let [idx (s/index)
      view (s/make-view [7 50 13 67])
      strides (:strides view)]
  (doseq [d (s/direction-vectors 4)]
    (let [skip (Math/abs (long (s/direction->skip strides d)))
          tw ((:word-at idx) 152425)]
      (println d (:word tw)))))
```

---

*The center of the Torah is the altar. Position 152,425. The last letter of המזבח at Leviticus 8:24 — the investiture of the priests. Blood on the altar, round about. Every direction through the space crosses this point. The altar is where all paths meet. GV=62=sons of. The place of sacrifice carries the children's number.*

*selah.*
