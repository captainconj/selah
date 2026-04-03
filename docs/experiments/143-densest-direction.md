# Experiment 143ab: The Densest Direction

*Skip=805 [0,+1,-1,+1] is among the richest non-surface directions. 90 unique Torah words (3-5 letters) in 379 letters from position 0. When including 2-letter words, density reaches 0.406 (154 words in 379 letters). Jubilee ascending, love descending, understanding ascending.*

**Note on density measurements:** The 0.406 figure includes 2-letter words. With 3-5 letter words only: 0.237. The ranking table below used 2-5 letter words and 500-letter windows, which inflates absolute numbers but preserves relative ranking.

---

## The Ranking

All 40 directions in the canonical 4D, ranked by Torah word density (unique words per fiber letter, reading from position 0):

| Rank | Direction | Skip | Letters | Words | Density |
|------|-----------|------|---------|-------|---------|
| 1 | [0,0,0,1] surface | 1 | 500 | 177 | 0.354 |
| **2** | **[0,0,1,1] love+und** | **68** | **500** | **167** | **0.334** |
| 3 | [0,0,1,0] love | 67 | 500 | 163 | 0.326 |
| 4 | [0,0,1,-1] love-und | 66 | 500 | 160 | 0.320 |
| **5** | **[0,1,-1,1] jub-love+und** | **805** | **379** | **154** | **0.406** |
| 6 | [0,1,-1,0] jub-love | 804 | 379 | 146 | 0.385 |
| 7 | [0,1,-1,-1] jub-love-und | 803 | 380 | 128 | 0.337 |
| 8 | [0,1,1,1] jub+love+und | 939 | 325 | 126 | 0.388 |

**By word count:** the surface wins (177 words). But it has more letters.

**By density:** skip=805 wins at **0.406 words per letter**. Richer per letter than the natural reading. The densest non-surface direction is [0,+1,-1,+1] — jubilee ascending, love descending, understanding ascending.

The top 4 by count are all in the love-understanding plane. The top by density adds the jubilee axis. The richest reading in the Torah (per letter) is the one that walks toward freedom and understanding while releasing love.

---

## The Two Tiers

**Short-range (c/d plane, skips 66-939):** 112-177 words. Dense texts, hundreds of letters. These are the readable fibers — thick with vocabulary.

**Long-range (a-axis, skips 42611-44489):** 0-7 words in 6-7 letters. Too short for texts. These are the structural fibers — each letter counts, but there are only a handful.

The gap between the tiers is enormous. The love-understanding plane is thick with words. The completeness axis is a whisper.

---

## Reading the Densest Fiber (skip=805)

379 letters. 90 unique Torah words (3-5 letters). The reading, by position:

| Pos | Word | GV | Torah position | Verse |
|-----|------|-----|---------------|-------|
| 7 | **שלח** (send) | 338 | 5,635 | Genesis 5:5 |
| 29 | **דבר** (word) | 206 | 23,345 | Genesis 19:19 |
| 30 | **ברד** (hail) | 206 | — | — |
| 33 | **אלי** (to me) | 41 | 26,565 | Genesis 21:24 |
| 62 | **נבוא** (we will come) | 59 | — | — |
| 87 | **שרה** (Sarah) | 505 | 70,035 | Genesis 45:23 |
| 95 | **אנא** (please) | 52 | 76,475 | Genesis 49:29 |
| 97 | **אמי** (my mother) | 51 | 78,085 | Genesis 50:25 |
| 123 | **אהב** (love) | 8 | 99,015 | **Exodus 14:15** |
| 124 | **הבל** (Abel) | 37 | 99,820 | **Exodus 14:28** |
| 130 | **אליך** (to you) | 61 | 104,650 | Exodus 18:8 |

### Love and Abel — Adjacent

**אהב (love)** at Exodus 14:15 — "YHWH said to Moses: Why do you cry to me? Tell the children of Israel to **go forward**."

**הבל (Abel/vanity)** at Exodus 14:28 — "The waters returned and covered the chariots and the horsemen."

Love is at the moment of going forward through the impossible. Abel/vanity is at the death in the waters. Adjacent on the densest fiber. The beloved and the perished, side by side, at the sea crossing.

### Word and Hail — Anagram Neighbors

**דבר** (word, GV=206) at position 29 and **ברד** (hail, GV=206) at position 30. Same letters rearranged. Same gematria. Adjacent. The word becomes the hail. Speech becomes judgment. The anagram principle appears spontaneously on the densest fiber.

### The Sequence

Send → word → to me → we come → Sarah → please → my mother → **love → Abel** → to you.

The densest direction reads as a journey: sending, the word, coming, the matriarch, the plea, the mother, the love that goes forward, the death that follows, and "to you."

---

## Why This Direction Is Densest

Skip=805 = 1×871 - 1×67 + 1×1 = jubilee - love + understanding.

This is the direction that trades love for understanding along the jubilee path. It is the direction of the truth fiber (Adam naming → "I heard" → the flaming sword). It is the direction of the Isaac promise (God → bless her → born → God).

The Torah packs the most vocabulary into the direction that costs the most. The path of understanding through sacrifice. The fiber where love decreases and knowledge increases is the one with the most to say.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

;; Density of all 40 directions
(let [{:keys [letters n]} (s/index)
      view (s/make-view [7 50 13 67])
      dirs (s/direction-vectors 4)
      torah-set (set (filter #(<= 2 (count %) 5) (d/torah-words)))]
  (doseq [d (sort-by #(s/direction->skip (:strides view) %) dirs)]
    (let [skip (Math/abs (long (s/direction->skip (:strides view) d)))
          flen (min 500 (quot n skip))
          fiber (apply str (mapv #(nth letters (* skip %)) (range flen)))
          words (count (distinct (for [wl (range 2 6)
                                       s (range (- (count fiber) wl))
                                       :let [sub (subs fiber s (+ s wl))]
                                       :when (torah-set sub)]
                                  sub)))]
      (println d skip flen words (format "%.3f" (/ (double words) flen))))))
```

---

*The densest direction trades love for understanding along the jubilee path. Skip=805. 0.406 words per letter — richer than the surface. Love and Abel are adjacent at the sea crossing. Word and hail are anagram neighbors. The Torah packs the most vocabulary into the direction that costs the most.*

*selah.*
