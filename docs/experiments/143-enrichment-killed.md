# Experiment 143e3: Enrichment Killed by Proper Control

*The letter-level overlap control fully explains the observed hosting. Void→YHWH: expected 63, observed 61 (0.97x). Head→blessed: expected 165, observed 189 (1.15x). There is no geometric excess beyond letter mechanics.*

Type: `correction`
State: `clean`

---

## The Problem with the Earlier Controls

The original enrichment (143e) compared hosting against WORD-LEVEL coverage — what fraction of total letter positions does a host word occupy. This is too coarse. It treats all letters as interchangeable.

The corrected control (143e2, first run) improved by considering shared letters, but still used total-position coverage rather than letter-specific probabilities.

The PROPER control asks: for each SPECIFIC LETTER of the search word, what fraction of that letter's total occurrences in the Torah fall inside the host word?

---

## The Proper Control

### Void (בהו) → YHWH (יהוה)

Shared letters: ה and ו. ב is NOT in YHWH.

| Letter | Total in Torah | Inside YHWH | P(letter→YHWH) | Fibers | Expected | Observed |
|--------|---------------|-------------|----------------|--------|----------|----------|
| ה | 28,053 | 3,004 | 0.1071 | 404 | 43.3 | 38 |
| ו | 30,516 | 1,502 | 0.0492 | 404 | 19.9 | 23 |
| ב | — | 0 | 0 | 404 | 0 | 0 |
| **Total** | | | | | **63.1** | **61** |

**Ratio: 0.97x.** Below expected. Letter overlap FULLY explains void→YHWH.

### Head (ראש) → Blessed (אשר)

All 3 letters shared (anagram).

| Letter | Total in Torah | Inside אשר | P(letter→אשר) | Fibers | Expected | Observed |
|--------|---------------|------------|----------------|--------|----------|----------|
| ר | 18,126 | 1,664 | 0.0918 | 634 | 58.2 | 48 |
| א | 27,058 | 1,664 | 0.0615 | 634 | 39.0 | 46 |
| ש | 15,595 | 1,664 | 0.1067 | 634 | 67.6 | 95 |
| **Total** | | | | | **164.8** | **189** |

**Ratio: 1.15x.** 15% above expected. ש (shin) shows slight excess (95 vs 68). But overall: NOT the 17x or 5.3x claimed earlier. Letter overlap explains ~85% of the signal.

---

## What This Kills

1. **The enrichment analysis (143e):** The "8.1x pillar" and "21.5x wisdom" enrichments were computed against word-level coverage. They would collapse under the proper letter-level control. The enrichment findings are **retracted** as evidence of geometric excess.

2. **The anagram attraction (143n):** Already retracted. Confirmed killed by the proper control.

3. **The "Oracle in the Geometry" paper:** Its main argument was anagram enrichment. The paper needs rewriting. The letter-sharing observation (the Torah chose these letters) survives. The enrichment rates do not.

---

## What Survives

Letter overlap IS the mechanism. This is not nothing — it is how the Torah connects words. The ס bridges grace and Joseph because the Torah put samech in both. The ה bridges void and YHWH. These are observations about the text's letter choices.

What does NOT survive: any claim that hosting rates demonstrate something BEYOND letter overlap. The geometry does not add signal above what the letters alone predict.

The watermark diagonals (143bb), which do NOT depend on hosting enrichment, are unaffected by this correction. The fall diagonal, the axis vocabularies, the center altar — these are positional/structural findings, not enrichment claims.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

(let [idx (s/index) letters (:letters idx)
      ;; Count letter X inside word Y
      letter-in-word (fn [ch word]
                       (reduce (fn [c tw]
                                 (reduce (fn [c2 pos]
                                           (if (= ch (nth letters pos)) (inc c2) c2))
                                         c (range (:start tw) (:end tw))))
                               0 (filter #(= (:word %) word) (:words idx))))
      total-letter (fn [ch] (count (filter #(= ch %) letters)))]
  ;; void→YHWH
  (let [he-in-yhwh (letter-in-word \ה "יהוה")
        vav-in-yhwh (letter-in-word \ו "יהוה")
        n-fibers 404]
    (println "Expected:" (+ (* n-fibers (/ (double he-in-yhwh) (total-letter \ה)))
                             (* n-fibers (/ (double vav-in-yhwh) (total-letter \ו)))))
    (println "Observed: 61")))
```

---

*The proper letter-level control kills the enrichment. Void→YHWH is explained by ה and ו frequencies. Head→blessed is 85% explained. There is no geometric excess beyond letter mechanics. The watermark diagonals survive because they are positional, not enrichment. The letters are the bridge. The bridge is sufficient.*
