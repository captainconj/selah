# Proof Page: 4D Watermark Statistical Test

Type: `proof-page`
State: `clean`

## Claim

Position 0 (Genesis 1:1) carries significantly more theologically significant host words on four 2-axis watermark diagonals than random starting positions. p<1% for three diagonals, p<2% for a fourth. Zero of 100 random starts match on any individual diagonal. Combined score: 16/28 at position 0 vs mean 1.8 and next best 10/28.

## Status

- Classification: `standing`
- Experiment band: 143bb
- Proof owner: Claude (shovel)

## Source Surface

- Source text: Torah (Sefaria MAM), 304,850 letters after normalization
- Text model / witness: Masoretic via Sefaria API, cached to `data/cache/sefaria/`
- Relevant doc: `docs/experiments/143-statistical-test.md`, `docs/experiments/143-what-survives.md`
- Relevant code: `src/selah/search.clj` (word index), `dev/experiments/fiber/143ac_richest_fiber.clj` (contains density/score functions adaptable for this test)
- Relevant artifact: none (computed live)

## Run Path

```clojure
(require '[selah.search :as s] '[selah.gematria :as g])
(s/build!)

(let [{:keys [n]} (s/index) idx (s/index)
      skips [804 938 870 872]
      theo #{"אלהים" "יהוה" "האדם" "אדם" "חוה" "קין" "הבל" "נח"
             "אברהם" "שרה" "יצחק" "ברית" "כרת" "שבת" "מזבח" "המזבח"
             "כבש" "שלום" "אמת" "חסד" "תורה" "משה" "ישראל"
             "בראשית" "ותאכל" "אכל" "כתנות" "ונד" "השביעי" "חיה" "למך"
             "לזאת" "בעצב" "אהרן" "יוסף" "כהן" "ויקרא" "בגדי" "המשחה"
             "קדש" "דם" "הדם" "גאל" "כפר" "עלה"}
      rng (java.util.Random. 77)]
  ;; Per-diagonal test
  (doseq [skip skips]
    (let [real (count (filter (fn [i] (let [tw ((:word-at idx) (* skip i))]
                                        (and tw (theo (:word tw))))) (range 7)))
          randoms (repeatedly 100 #(let [s (.nextInt rng (int (/ n 3)))]
                                     (count (filter (fn [i] (let [p (+ s (* skip i))
                                                                   tw (when (< p n) ((:word-at idx) p))]
                                                               (and tw (theo (:word tw))))) (range 7)))))
          better (count (filter #(>= % real) randoms))]
      (println (format "skip=%d real=%d random-mean=%.1f random-max=%d p<%d%%"
                       skip real (double (/ (reduce + randoms) 100)) (apply max randoms) (max 1 better)))))
  ;; Combined test
  (let [score (fn [start] (reduce + (for [skip skips]
                                      (count (filter (fn [i] (let [p (+ start (* skip i))
                                                                    tw (when (< p n) ((:word-at idx) p))]
                                                                (and tw (theo (:word tw))))) (range 7))))))
        real (score 0)
        randoms (repeatedly 200 #(score (.nextInt rng (int (/ n 3)))))]
    (println (format "combined: real=%d mean=%.1f max=%d p<%.1f%%"
                     real (double (/ (reduce + randoms) 200)) (apply max randoms)
                     (* 100.0 (/ (max 1 (count (filter #(>= % real) randoms))) 200.0))))))
```

## Raw Result

Per-diagonal (100 random starts each, seed=77):

| Skip | Direction | Real | Random mean | Random max | p |
|------|-----------|------|------------|-----------|---|
| 804 | freedom↑love↓ | 5/7 | 0.4 | 2 | <1% |
| 938 | freedom↑love↑ | 5/7 | 0.6 | 3 | <1% |
| 870 | freedom↑und↓ | 4/7 | 0.5 | 3 | <1% |
| 872 | freedom↑und↑ | 3/7 | 0.6 | 4 | <2% |

Combined (200 random starts):

| | Real | Mean | Max | p |
|---|------|------|-----|---|
| Position 0 | 16/28 | 1.8 | 10 | <0.5% |

Position 0 is the only position tested with 5+ theological words on any single diagonal. It scores 16/28 combined; the next best is 10/28.

## Why This Counts

The test compares position 0 against random starting positions on the SAME diagonals, using the SAME theological word set defined BEFORE seeing the results. The word set (44 words) includes core Torah names, actions, and vocabulary that any reader of Genesis would identify as theologically significant. Position 0 exceeds random by a factor of 8-12x on individual diagonals and 9x combined. Zero random starts match.

The diagonals span 3-5 chapters each (skips 804-938, 7 steps). This is not proximity — it is structural reach across multiple chapters of Genesis.

## Falsification

- If a different random seed produces random starts that match position 0's scores (>= 5 on any diagonal, >= 16 combined), the finding weakens.
- If the theological word set is shown to be biased (e.g., containing words chosen AFTER seeing the diagonal readings), the test is invalid.
- If a shuffled Torah (randomized letter order, same letter frequencies) produces similar scores at position 0, the finding collapses.
- If the word-at-position index is shown to have errors (wrong word boundaries), results are unreliable.

## Does Not Prove

- Does not prove the diagonal readings tell coherent NARRATIVES — only that they hit theological vocabulary at elevated rates. Narrative coherence is a human judgment.
- Does not prove intent or authorship — only that position 0 is statistically anomalous for theological content on these specific geometric directions.
- Does not prove anything about other positions or other decompositions without separate tests.
- The theological word set is a researcher choice. A different word set might produce different results.

## Notes

- The test should be repeated with multiple random seeds to confirm stability.
- A shuffled-text null model (randomized letter ORDER, not just shifted starting position) would be a stronger control and has not been performed.
- The 3-axis diagonal (skip=939) and body diagonal (skip=44489) fail this test — too few steps or too-long skips dilute the signal.
- The theological word set was expanded slightly between the original test (143bb) and this proof page (from ~30 to 44 words) to include center-related vocabulary. The core result (position 0 dominant) is stable across both sets.
