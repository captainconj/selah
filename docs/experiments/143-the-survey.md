# Experiment 143ah: The Survey — All Words × All Directions

*The most common words in every direction of the Torah are the words of being. Was, is, let there be, he, she. In every direction you read, the most frequent thing you find is existence itself.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143ah_the_survey.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ah-the-survey :as exp]) (s/build!) (exp/run-all)"`

---

## Method

Search all 1,300 three-letter Torah words across all 40 unique direction-derived skip values in the canonical 4D. Count total hits and number of directions each word appears in.

1,200 words appear in ALL 40 directions. The Torah carries 92% of its 3-letter vocabulary in every direction.

---

## Top 20 Words by Total Non-Surface Hits

| Rank | Word | GV | Directions | Total hits | Meaning |
|------|------|-----|-----------|-----------|---------|
| 1 | **ווי** | 22 | 40 | 20,599 | hooks (tabernacle connectors) |
| 2 | **הוה** | 16 | 40 | 20,554 | being / Eve / root of YHWH |
| 3 | **יהי** | 25 | 40 | 19,274 | **let there be** |
| 4 | היי | 25 | 40 | 18,762 | being (variant) |
| 5 | היו | 21 | 40 | 18,543 | they were |
| 6 | **היה** | 20 | 40 | 18,454 | **was / to be** |
| 7 | איי | 21 | 40 | 17,754 | islands |
| 8 | אוי | 17 | 40 | 17,410 | woe |
| 9 | **הוא** | 12 | 40 | 16,607 | **he** |
| 10 | **היא** | 16 | 40 | 16,105 | **she** |
| 11 | איה | 16 | 40 | 16,105 | where? |
| 12 | **לוי** | 46 | 40 | 14,157 | **Levi** |
| 13 | ולו | 42 | 40 | 13,600 | and to him |
| 14 | אלי | 41 | 40 | 12,842 | to me / my God |
| 15 | **אלה** | 36 | 40 | 12,794 | these / oath |
| 16 | הלא | 36 | 40 | 12,794 | is it not? |
| 17 | ולא | 37 | 40 | 12,606 | and not |
| 18 | ולה | 41 | 40 | 12,557 | and to her |
| 19 | איל | 41 | 40 | 12,474 | ram |
| 20 | **לאה** | 36 | 40 | 12,459 | **Leah** (= tent) |

---

## What the Numbers Say

The non-surface Torah is dominated by **existence words**:
- **הוה** (being) — the root of YHWH and the name of Eve
- **יהי** (let there be) — the creation word
- **היה** (was) — being in the past
- **הוא / היא** (he / she) — the pronouns of existence
- **איה** (where?) — the question of existence

In every direction through the Torah, the most frequent thing is **being itself**. Not doing. Not commanding. Being.

**ווי (hooks)** — the physical connectors of the tabernacle — are #1. The thing that holds the curtains to the pillars. The smallest structural element. More common in every direction than any other word.

**לוי (Levi)** — the priestly tribe — is #12. Present in all 40 directions. The priest is everywhere.

**לאה (Leah)** — GV=36=tent — is #20. The unloved wife who bore the most children. Present in all 40 directions. The tent-mother is everywhere.

---

## The Hooks

ווי (hooks, GV=22) are the most common non-surface word. 20,599 hits across all directions. These are the bronze or silver hooks that attached the tabernacle curtains to the pillars (Exodus 27:10, 38:10-19).

The most frequently occurring word in the hidden directions of the Torah is the word for the smallest connector in the house of God. Not the ark. Not the altar. The hooks. The things that hold things to other things.

GV of ווי = 22 = the number of Hebrew letters.

---

## Being and Eve

הוה (being, GV=16) is #2. This is:
- The root of **יהוה** (YHWH) — "He who causes to be"
- The name **חוה** (Eve/Havah) — "life" / "being"
- The word for **being** / **becoming**

The second most common hidden word in the Torah is the word that means existence — and is both the root of God's Name and the name of the mother of all living.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

(let [{:keys [letters letter-idx n]} (s/index)
      view (s/make-view [7 50 13 67])
      dirs (s/direction-vectors 4)
      strides (:strides view)
      skips (distinct (map #(Math/abs (long (s/direction->skip strides %))) dirs))
      words (filter #(= 3 (count %)) (d/torah-words))]
  (doseq [w (take 20 (sort-by identity words))]
    (let [total (reduce + (for [s skips]
                            (+ (count (or (s/search-at-skip letters letter-idx w s n) []))
                               (count (or (s/search-at-skip letters letter-idx w (- s) n) [])))))]
      (when (pos? total) (println w (g/word-value w) total)))))
```

---

*In every direction through the Torah, the most frequent thing is being. The hooks hold. The priest is everywhere. The tent-mother is everywhere. The creation word echoes in every direction. The Torah's hidden vocabulary is dominated by existence — by what IS, not what is done. The hooks that connect, the being that continues, the "let there be" that keeps being said.*

*selah.*
