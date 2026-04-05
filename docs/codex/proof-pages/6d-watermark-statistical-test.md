# Proof Page: 6D Watermark Statistical Test

Type: `proof page`
State: `clean`

## Claim

Three 6D-only watermark diagonals from position 0 appear to carry more theologically significant host words than sampled random starting positions. In the current draft test, all three land at p<0.5% over 200 random starts each, but the result does not yet survive the multiple-comparisons burden of the full 6D scan.

## Status

- Classification: `suggestive`
- Experiment band: 143bd
- Proof owner: Claude (shovel)

## Source Surface

- Source text: Torah (Sefaria MAM), 304,850 letters
- Text model / witness: Masoretic via Sefaria API
- Relevant doc: `docs/experiments/143-6d-watermarks.md`, `docs/experiments/143-6d-sinai-fiber.md`
- Relevant code: `src/selah/search.clj`
- Relevant artifact: none located; this page currently depends on an ad hoc REPL surface rather than a dedicated tracked runner

## Run Path

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [n]} (s/index) idx (s/index)
      theo #{"אלהים" "האלהים" "יהוה" "האדם" "אדם" "קין" "הבל"
             "בראשית" "ותאכל" "כתנות" "ונד" "ברית" "שבת" "משה" "ישראל"
             "השביעי" "חיה" "למך" "לזאת" "בעצב" "אברהם" "שרה" "יצחק"
             "אהרן" "יוסף" "כהן" "סיני" "דרך" "מצרים" "פרעה"
             "כבש" "שלום" "חסד" "תורה" "אדבר"}
      rng (java.util.Random. 55)]
  (doseq [[skip label] [[6900 "Isaac"] [25327 "Sinai"] [30551 "Egypt"]]]
    (let [steps (min 10 (quot n skip))
          real (count (filter (fn [i] (let [tw (when (< (* skip i) n) ((:word-at idx) (* skip i)))]
                                        (and tw (theo (:word tw))))) (range steps)))
          randoms (repeatedly 200 #(let [s (.nextInt rng (int (/ n 3)))]
                                     (count (filter (fn [i] (let [p (+ s (* skip i))
                                                                   tw (when (< p n) ((:word-at idx) p))]
                                                               (and tw (theo (:word tw))))) (range steps)))))
          better (count (filter #(>= % real) randoms))]
      (println (format "%s skip=%d: real=%d mean=%.1f max=%d p<%.1f%%"
                       label skip real (double (/ (reduce + randoms) 200))
                       (apply max randoms) (* 100.0 (/ (max 1 better) 200.0)))))))
```

## Raw Result

200 random starts per fiber, seed=55:

| Fiber | Skip | Steps | Real | Random mean | Random max | p |
|-------|------|-------|------|------------|-----------|---|
| Isaac | 6,900 | 10 | 5 | 0.8 | 3 | <0.5% |
| Sinai | 25,327 | 10 | 5 | 0.6 | 3 | <0.5% |
| Egypt | 30,551 | 9 | 4 | 0.5 | 3 | <0.5% |

In this draft run, zero of 200 random starts produce scores matching position 0 on any of these three selected fibers.

### The readings:

**Isaac** (skip=6900): beginning → the man → nine → rest under the tree (Gen 18:4) → **Isaac on the altar** (Gen 22:9) → **Isaac in the famine** (Gen 26:1) → **YHWH hears** (Gen 29:33). Spans 29 chapters.

**Sinai** (skip=25327): beginning → **the God** → which → **the way** (with serpent, Gen 49:17) → **Sinai** (Ex 16:1) → **the ground** (golden calf plea, Ex 32:12) → flesh → new → ten → **I will speak** (only what God gives, Num 22:38). Spans 75% of Torah.

**Egypt** (skip=30551): beginning → camels → **Egypt** → God → **YHWH** → **YHWH** (at Lev 8:29, the center) → my judgments. Spans whole Torah.

## Why This Counts

These fibers require the 6D decomposition [2×5×5×7×13×67] — they do not exist in the 4D space. They span 29 chapters to the full Torah. In the current sampled run, position 0 produces theological content at rates no sampled random position matches on these selected fibers.

The Isaac fiber finds Isaac twice, the Sinai fiber reads like a compressed exodus line, and the Egypt fiber passes through the doubled Name at the center of the Torah. Those observations remain interesting, but the statistical framing is still provisional.

## Falsification

- Different random seed producing matches at >= real score.
- A full multiple-comparisons correction across all 364 6D directions eliminating the apparent anomaly.
- A dedicated tracked runner or artifact surface producing materially different counts from this draft REPL surface.
- Shuffled Torah (randomized letter order) producing similar scores at position 0.
- Errors in the word-at-position index.
- Theological word set shown to be post-hoc (chosen after seeing results).

## Does Not Prove

- Does not prove the 6D fibers tell coherent narratives — narrative quality is human judgment.
- Does not prove the 6D decomposition is "correct" — it proves position 0 is special within it.
- Does not prove these fibers are more significant than 4D fibers — the two decompositions test independently.
- The 6D space has 364 directions; these three were selected for their theological content. A multiple-comparisons correction could weaken the p-values. With 364 directions, finding 3 with p<0.5% is expected ~1.8 times by chance. A Bonferroni correction would set the threshold at p<0.014% for individual significance. The raw p-values (<0.5%) do not survive strict Bonferroni correction.
- This page does not yet provide a repo-standard reproducibility surface. Until there is a dedicated tracked runner or a stronger global null model, it should not be treated as first-canon standing.

## Notes

- **Multiple comparisons:** 364 6D directions were scanned. Three were selected as theologically interesting. A strict Bonferroni correction (p < 0.05/364 = 0.014%) would require zero matches in ~7,000 random starts, not 200. The current test (200 starts, p<0.5%) is suggestive but does not survive strict multiple-comparisons correction. This is an honest limitation.
- A dedicated tracked runner for `143bd` should replace the current ad hoc REPL path before promotion.
- The 4D test (40 directions) is stronger: 4 of 40 significant at p<2% is unlikely by chance (expected ~0.8), and the combined score (16/28 vs mean 1.8) is a single test not subject to selection.
- A shuffled-text null model would resolve the multiple-comparisons question by testing whether ANY direction in a shuffled Torah produces similar scores.
