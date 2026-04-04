# Experiment 143af: Geography of the Torah

*Genesis 1 is one of the sparsest regions. The creation uses a small vocabulary precisely. Exodus and Leviticus attract the most fiber traffic — because they are in the center. Deuteronomy has the richest vocabulary per word. The Torah's density landscape tells its own story.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143af_geography.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143af-geography :as exp]) (s/build!) (exp/run-all)"`

---

## Vocabulary by Book

| Book | Total words | Unique words | Ratio (unique/total) |
|------|------------|-------------|---------------------|
| Deuteronomy | 14,382 | 4,091 | **0.284** (richest) |
| Exodus | 16,834 | 4,174 | 0.248 |
| Genesis | 20,674 | 5,024 | 0.243 |
| Numbers | 16,543 | 3,855 | 0.233 |
| Leviticus | 12,041 | 2,717 | **0.226** (most repetitive) |

**Deuteronomy has the richest vocabulary** — the most unique words per total word. Moses is not repeating formulas. He is speaking freely.

**Leviticus is the most repetitive** — the fewest unique words per total. The ritual instructions reuse the same vocabulary precisely. "The priest shall take... the priest shall sprinkle... the priest shall burn..."

**Genesis has the most words** but middling uniqueness. It tells long stories with recurring phrases.

---

## Surface Vocabulary Hot Spots

Scanning 300-letter windows across the Torah:

**Densest regions (most unique words per 300 letters):**

| Position | Words | Location | Context |
|----------|-------|----------|---------|
| 50,000 | 154 | Genesis 34 | **Shechem/Dinah** — conflict, dialogue, deception |
| 300,000 | 152 | **Deuteronomy 31** | **Moses handing off the Torah** |
| 230,000 | 150 | Numbers 24 | **Balaam's blessing** |
| 10,000 | 148 | Genesis 8 | **The flood receding** |
| 30,000 | 146 | Genesis 24 | **Rebekah at the well** |
| 70,000 | 145 | Genesis 45 | **Joseph revealing himself** |
| 100,000 | 141 | **Exodus 14** | **The sea crossing** |

**Sparsest regions:**

| Position | Words | Location | Context |
|----------|-------|----------|---------|
| 160,000 | **94** | **Leviticus 13** | **Skin disease laws** |
| 0 | **97** | **Genesis 1** | **Creation** |
| 250,000 | 99 | Numbers 36 | Inheritance laws |
| 190,000 | 102 | Numbers 2 | Census camp layout |

**Genesis 1 is one of the sparsest regions of the Torah.** The creation narrative uses a small vocabulary with extreme precision. "And God said. And it was so. And God saw that it was good. And there was evening, and there was morning." The repetition IS the structure. The sparse vocabulary IS the design.

The densest region — Genesis 34, the Dinah story — is the place of maximum human activity: negotiation, deception, revenge, circumcision weaponized. Where people scheme most, the vocabulary peaks.

Deuteronomy 31 — Moses giving the Torah away — is the second densest. The moment of handing over is rich with language.

---

## Fiber Traffic by Book

Non-surface fibers (7 search words, canonical 4D) land preferentially in certain books:

| Book | Fiber traffic | % of total | Book % of Torah | Enrichment |
|------|-------------|-----------|----------------|-----------|
| **Exodus** | 931 | 26.5% | 20.8% | **1.27x** |
| **Leviticus** | 653 | 18.6% | 14.7% | **1.27x** |
| Numbers | 780 | 22.2% | 20.8% | 1.07x |
| Genesis | 743 | 21.2% | 25.6% | 0.83x |
| **Deuteronomy** | 403 | 11.5% | 18.0% | **0.64x** |

**Exodus and Leviticus are enriched at 1.27x.** Fibers land in the tabernacle-and-sacrifice books 27% more than their size warrants.

**Deuteronomy is depleted at 0.64x.** Fibers avoid the speech book. Moses's addresses are under-represented by non-surface paths.

**This is partly geometric.** Exodus and Leviticus are in the CENTER of the Torah (letters ~65,000 to ~175,000 of 305,000). Non-surface fibers that span the whole text necessarily cross the center more than the edges. The enrichment is partly a centrality effect.

But it is worth noting: the center of the Torah is the law. The fibers converge where the instructions are.

---

## Factors Are Not Special

Factors of 304,850 (2, 5, 7, 13, 67, etc.) are NOT denser on average than non-factors:

- Factor skips: average density 0.190
- Non-factor skips: average density 0.204
- Ratio: 0.93

The factors of the Torah's letter count do not produce richer readings. This is a null result. The number theory of 304,850 does not directly determine which skips carry the most vocabulary.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d])
(s/build!)

;; Vocabulary hot spots
(let [{:keys [letters n]} (s/index)
      torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))]
  (doseq [start (range 0 (- n 300) 10000)]
    (let [text (subs (apply str letters) start (+ start 300))
          seen (atom #{})]
      (doseq [wl (range 3 6) s (range (- 300 wl))]
        (when (torah-set (subs text s (+ s wl)))
          (swap! seen conj (subs text s (+ s wl)))))
      (let [v ((:verse-at (s/index)) start)]
        (println start (count @seen) (:book v) (:ch v))))))
```

---

*Genesis 1 is sparse — the creation uses few words precisely. The Dinah story is densest — where people scheme most, vocabulary peaks. Fibers prefer the center (Exodus, Leviticus) and avoid the edges (Deuteronomy). Factors of 304,850 are not special for density. The Torah's geography tells its own story: the structured beginning, the rich middle, the speaking end.*
