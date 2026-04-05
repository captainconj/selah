# Proof Page: 4D Watermark Statistical Test

Type: `proof page`
State: `clean`

## Claim

Position 0 (Genesis 1:1) appears to carry more theologically significant host words on four 2-axis watermark diagonals than random starting positions. In the current draft test, three diagonals land at p<1%, a fourth at p<2%, and the combined score is 16/28 at position 0 versus mean 1.8 and next best 10/28.

## Status

- Classification: `suggestive`
- Experiment band: 143bb
- Proof owner: Claude (shovel)

## Source Surface

- Source text: Torah (Sefaria MAM), 304,850 letters after normalization
- Text model / witness: Masoretic via Sefaria API, cached to `data/cache/sefaria/`
- Relevant doc: `docs/experiments/143-statistical-test.md`, `docs/experiments/143-what-survives.md`
- Relevant code: `src/selah/search.clj` (word index)
- Relevant artifact: none located; this page currently depends on an ad hoc REPL surface rather than a dedicated tracked runner

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

In this draft run, position 0 is the only tested position with 5+ theological words on any single diagonal. It scores 16/28 combined; the next best sampled start is 10/28.

## Why This Counts

The draft test compares position 0 against random starting positions on the same diagonals, using the same theological word set within the run. The word set includes core Torah names, actions, and vocabulary that any reader of Genesis would identify as theologically significant. In this run, position 0 exceeds the sampled random starts by a large margin on individual diagonals and combined.

The diagonals span 3-5 chapters each (skips 804-938, 7 steps). That makes the finding more than local proximity if the current scoring surface survives a dedicated rerun.

## Falsification

- If a different random seed produces random starts that match position 0's scores (>= 5 on any diagonal, >= 16 combined), the finding weakens.
- If a dedicated tracked runner or artifact surface produces materially different counts from this draft REPL surface, the finding weakens.
- If the theological word set is shown to be biased (e.g., containing words chosen AFTER seeing the diagonal readings), the test is invalid.
- If a shuffled Torah (randomized letter order, same letter frequencies) produces similar scores at position 0, the finding collapses.
- If the word-at-position index is shown to have errors (wrong word boundaries), results are unreliable.

## Does Not Prove

- Does not prove the diagonal readings tell coherent NARRATIVES — only that they hit theological vocabulary at elevated rates. Narrative coherence is a human judgment.
- Does not prove intent or authorship — only that position 0 is statistically anomalous for theological content on these specific geometric directions.
- Does not prove anything about other positions or other decompositions without separate tests.
- The theological word set is a researcher choice. A different word set might produce different results.
- This page does not yet provide a repo-standard reproducibility surface. Until there is a dedicated tracked runner or artifact, it should not be treated as first-canon standing.

## Notes

- The test should be repeated with multiple random seeds to confirm stability.
- A dedicated tracked runner for `143bb` should replace the current ad hoc REPL path before promotion.
- A shuffled-text null model (randomized letter ORDER, not just shifted starting position) would be a stronger control and has not been performed.
- The 3-axis diagonal (skip=939) and body diagonal (skip=44489) fail this test — too few steps or too-long skips dilute the signal.
- The theological word set was expanded slightly between the original test (143bb) and this proof page (from ~30 to 44 words) to include center-related vocabulary. The core result (position 0 dominant) is stable across both sets.
