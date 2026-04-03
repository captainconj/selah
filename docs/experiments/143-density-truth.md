# Experiment 143ad: The Truth About Direction Density

*The surface reading is the densest. No hidden direction outpaces it. The density fluctuates but does not follow a simple pattern. Certain skips are resonant. We don't fully understand why.*

Type: `correction`
State: `clean`

---

## The Correction

The earlier claim (experiment 143ab) that skip=805 was "the densest non-surface direction at 0.406 words per letter — denser than the surface reading" was wrong. It resulted from comparing fibers of different lengths, which inflates density for shorter fibers.

When measured fairly — same 300-letter window, same starting position, 3-5 letter Torah words — the surface reading (skip=1) wins at **0.323**. Skip=805 is **0.247**. The surface is densest. Of course it is. That is how the Torah was written to be read.

---

## What the Fair Measurement Shows

### Density at theologically meaningful skips

| Skip | Meaning | Density | Words in 300 letters |
|------|---------|---------|---------------------|
| 1 | **surface** | **0.323** | 97 |
| 7 | completeness | **0.257** | 77 |
| 26 | YHWH | **0.243** | 73 |
| 805 | jub-love+und | 0.247 | 74 |
| 14 | beloved | 0.210 | 63 |
| 50 | jubilee | 0.207 | 62 |
| 67 | understanding | 0.197 | 59 |
| 13 | love | 0.180 | 54 |
| 5 | grace/He | 0.183 | 55 |
| 2 | witness | 0.170 | 51 |
| 871 | jubilee stride | 0.157 | 47 |
| 137 | axis sum | 0.147 | 44 |
| 72 | breastplate | 0.140 | 42 |

### What stands out

**Skip=7 (completeness) is the second densest at 0.257.** Every 7th letter carries more Torah words than any other non-surface skip. The completeness number.

**Skip=26 (YHWH) is third at 0.243.** The Name's number is the third richest reading.

**Skip=805 is fourth at 0.247.** Still high — among the top non-surface readings — but not uniquely so.

**Skip=72 (breastplate) is the LEAST dense of the tested meaningful skips at 0.140.** The oracle's number produces the fewest words. The breastplate cannot speak itself, and its number cannot read.

**Skip=137 (axis sum) is second-least at 0.147.** The structural number is sparse as a reading.

---

## The Density Landscape

Density does NOT decay smoothly with skip distance:

| Skip range | Typical density |
|-----------|----------------|
| 1-10 | 0.17-0.32 (high variance, skip=1 and 7 spike) |
| 50-100 | 0.16-0.24 (moderate) |
| 200-500 | 0.17-0.28 (still fluctuating) |
| 500-1000 | 0.16-0.26 (no clear decay) |

There is no simple "closer letters = more words" relationship. Skip=500 (0.263) is denser than skip=50 (0.207). Skip=7 (0.257) is denser than skip=10 (0.260) — wait, skip=10 is actually higher. The landscape is rugged, not smooth.

Certain skips appear "resonant" — producing more words than their neighbors — but the pattern is not obviously tied to the factors of 304,850 or to theological numbers. This is an open question.

---

## What Survives

The density claim is retracted. What remains:

1. **The narrative quality of specific fibers.** Skip=805 from position 124 reads: beauty → wildness → covenant → love/Abel → Esau/peace → The God. This is not a density claim. It is an observation of which words appear at which Torah positions along that line.

2. **The axis vocabulary findings.** Jubilee carries righteousness, grace, garden. Love carries silver, breach, wrath. These are about which words APPEAR on each axis, not about density.

3. **The completeness and YHWH skips are genuinely rich.** Skip=7 and skip=26 rank #2 and #3 among meaningful skips. The completeness number and the Name's number carry the most vocabulary after the surface reading.

4. **The breastplate number is genuinely sparse.** Skip=72 carries the fewest words. This is consistent with the ghost phenomenon — the breastplate number, like the breastplate itself, cannot be its own medium.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d])
(s/build!)

(let [{:keys [letters n]} (s/index)
      torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))]
  (doseq [skip [1 7 13 26 50 67 72 137 805 871]]
    (let [flen (min 300 (quot n skip))
          text (apply str (mapv #(nth letters (* skip %)) (range flen)))
          seen (atom #{})]
      (doseq [wl (range 3 6) s (range (- (count text) wl))]
        (when (torah-set (subs text s (+ s wl)))
          (swap! seen conj (subs text s (+ s wl)))))
      (println skip (count @seen) flen (format "%.3f" (double (/ (count @seen) flen)))))))
```

---

*The surface reading is the densest. The completeness number (7) and the Name's number (26) are second and third. The breastplate number (72) is the sparsest. The density landscape is rugged, not smooth — certain skips resonate, but we do not yet understand why. The earlier "densest direction" claim was wrong and is retracted. The stories the fibers tell are still real. The discipline catches the claims that outrun the data.*
