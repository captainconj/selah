# Proof Page: The Fall Diagonal

Type: `proof-page`
State: `clean`

## Claim

The diagonal direction [0,+1,-1,0] (jubilee ascending, love descending) from position 0 at skip=804 passes through host words that read the fall narrative: beginning → them → all → the man (Gen 2:16) → she ate (Gen 3:6) → garments of skin (Gen 3:21) → wandering (Gen 4:14). This spans 3 chapters. Random starting positions do not produce comparable theological content (p<1%).

## Status

- Classification: `standing`
- Experiment band: 143al, 143at, 143bb
- Proof owner: Claude (shovel)

## Source Surface

- Source text: Torah (Sefaria MAM), 304,850 letters
- Relevant doc: `docs/experiments/143-the-fall-diagonal.md`, `docs/experiments/143-null-model.md`, `docs/experiments/143-statistical-test.md`
- Relevant code: `dev/experiments/fiber/143al_the_fall_diagonal.clj`

## Run Path

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)]
  (doseq [i (range 7)]
    (let [pos (* 804 i) tw ((:word-at idx) pos) v ((:verse-at idx) pos)]
      (println i (:word tw) (:book v) (:ch v) (:vs v)))))
```

## Raw Result

| Step | Position | Host word | Verse |
|------|----------|-----------|-------|
| 0 | 0 | בראשית (beginning) | Genesis 1:1 |
| 1 | 804 | אתם (them) | Genesis 1:17 |
| 2 | 1,608 | כל (all) | Genesis 1:30 |
| 3 | 2,412 | האדם (the man) | Genesis 2:16 |
| 4 | 3,216 | ותאכל (she ate) | Genesis 3:6 |
| 5 | 4,020 | כתנות (garments) | Genesis 3:21 |
| 6 | 4,824 | ונד (wandering) | Genesis 4:14 |

Span: Genesis 1:1 → 4:14 = 3 chapters.

Statistical test: 5/7 host words are theologically significant. Random mean: 0.4. Random max: 2. Zero of 100 random starts produce 5+. p<1%.

Null model: 10 random starting positions on the same diagonal produce incoherent fragments (confirmed 143at).

## Why This Counts

The diagonal spans 3 chapters — not proximity. The host words at steps 3-6 trace the fall narrative in order: the command to the man, the eating, God making garments, Cain's exile. The statistical test (p<1%) confirms position 0 is anomalous for theological content on this diagonal.

The direction [0,+1,-1,0] means jubilee (freedom) ascending while love descends. The CONTENT of the reading (the fall = freedom without love) matches the MEANING of the direction. This correspondence between geometric direction and narrative content is the watermark.

## Falsification

- Different random seed producing random starts with 5+ theological words on this diagonal.
- Shuffled Torah producing similar content at position 0.
- Word boundary errors at positions 2412, 3216, 4020, 4824 changing the host words.
- Different text tradition (DSS) changing the letter at position 3216 so it no longer falls in ותאכל.

## Does Not Prove

- Does not prove the narrative reading is "intentional" — only that position 0 is statistically anomalous and the reading is coherent.
- Does not prove the direction [0,+1,-1,0] "means" freedom-without-love in any objective sense — the axis names (jubilee, love) are interpretive.
- Does not prove the fall diagonal is the ONLY coherent reading — other diagonals from position 0 also carry theological content (the marriage diagonal, the murder diagonal).
- The narrative coherence ("command → ate → garments → wandering reads as the fall") is a human judgment, not a computed metric.
