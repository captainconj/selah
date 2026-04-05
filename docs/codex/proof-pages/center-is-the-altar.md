# Proof Page: The Center Is the Altar

Type: `proof-page`
State: `clean`

## Claim

The literal midpoint of the Torah (position 152,425 of 304,850 letters) falls inside the word המזבח (the altar) at Leviticus 8:24. The geometric center of the canonical 4D space (coordinate [3,25,6,33], position 152,860) falls inside בגדי (garments) at Leviticus 8:30. Both are in the same ceremony — the priestly investiture. Between them: the anointing oil (המשחה, GV=358=messiah).

## Status

- Classification: `standing`
- Experiment band: 143an, 143ax
- Proof owner: Claude (shovel)

## Source Surface

- Source text: Torah (Sefaria MAM), 304,850 letters
- Relevant doc: `docs/experiments/143-the-center-altar.md`, `docs/experiments/143-two-centers.md`
- Relevant code: `src/selah/search.clj`, `dev/experiments/fiber/143an_center_altar.clj`

## Run Path

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

;; Literal midpoint
(let [pos 152425 idx (s/index)
      tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
  (println "Literal center:" pos (:word tw) (g/word-value (:word tw)) (:book v) (:ch v) (:vs v)))

;; Geometric center
(let [view (s/make-view [7 50 13 67])
      pos (reduce + (map * [3 25 6 33] (:strides view)))
      idx (s/index)
      tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
  (println "Geometric center:" pos (:word tw) (g/word-value (:word tw)) (:book v) (:ch v) (:vs v)))
```

## Raw Result

- Literal midpoint (152,425): letter ח in **המזבח** (the altar, GV=62) at **Leviticus 8:24**
- Geometric center (152,860): letter ג in **בגדי** (garments, GV=19) at **Leviticus 8:30**
- Distance: 435 letters, 6 verses, same ceremony
- Between them at Leviticus 8:30: **המשחה** (the anointing oil, GV=358 = משיח = נחש)
- GV of המזבח = 62 = בני (sons of)
- GV of בגדי = 19 = חוה (Eve)
- 152,425 = 5² × 7 × 13 × 67 = 175 × 871 = Abraham's years × jubilee stride

## Why This Counts

The midpoint is a positional fact — letter 152,425 of 304,850 IS inside המזבח. The geometric center is arithmetic — coordinate [3,25,6,33] at strides [43550,871,67,1] = position 152,860 IS inside בגדי. The anointing oil (GV=358) between them is a reading of the text at those positions.

The factorization 152,425 = 5² × 7 × 13 × 67 is number theory. That 25 × 7 = 175 = Abraham's years (Genesis 25:7) is an observation about the Torah's numbers meeting its narrative.

## Falsification

- If the Sefaria MAM text produces a different letter count than 304,850, the midpoint shifts.
- If the word boundaries are computed differently (different HTML stripping, different niqqud handling), the host word at 152,425 might change.
- If a different manuscript tradition (DSS, Samaritan) has a different letter count, the center would be at a different position and might not be in the altar.

## Does Not Prove

- Does not prove the Torah was designed to place the altar at the center — it could be coincidence that Leviticus 8 (a long ceremony passage) happens to fall at the midpoint.
- Does not prove the gematria relationships (62=sons, 19=Eve, 358=messiah) are intentional.
- Does not prove the factorization (175 × 871) is meaningful beyond arithmetic.
- The center being in Leviticus is partly expected — Leviticus is the middle book.
