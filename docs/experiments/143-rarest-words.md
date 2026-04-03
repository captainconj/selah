# Experiment 143ai: The Rarest Words

*Wrath is the most hidden word in the Torah's geometry. Found in only 6 of 40 directions. It lives on the love axis. The geometry hides the hard things and the precious things.*

Type: `exploration`
State: `clean`

---

## Method

For all 1,300 three-letter Torah words, count how many of the 40 unique direction-derived skip values produce at least one hit. Rank by scarcity.

1,200 words appear in all 40 directions (full saturation). 100 words are missing from at least one direction. These are the geometry's rare vocabulary.

---

## The Rarest

| Word | GV | Directions (of 40) | Meaning |
|------|-----|-------------------|---------|
| **קצף** | 270 | **6** | **wrath** |
| נצף | 220 | 8 | hidden / north |
| נטף | 139 | 8 | drop (of myrrh) |
| **כסף** | 160 | **13** | **silver (the price)** |
| טרף | 289 | 14 | torn / prey |
| **חפץ** | 178 | **15** | **delight / desire** |
| **נגף** | 133 | **15** | **plague / struck** |
| שטף | 389 | 15 | flood / overwhelm |
| חפף | 168 | 17 | cover / protect |
| **סוף** | 146 | **19** | **end / reed (sea)** |
| **יסף** | 150 | 23 | **he added (= Joseph)** |
| **אסף** | 141 | 24 | **gather** |
| **פרץ** | 370 | **25** | **breach (Perez / David line)** |
| **כנף** | 150 | **26** | **wing** |
| **גפן** | 133 | **27** | **vine** |
| **מקץ** | 230 | 29 | **at the end of** |

---

## What the Numbers Say

**Wrath (קצף) is the most hidden word.** 6 of 40 directions. Earlier (experiment 143w) we found it is love-axis-only — present on the love axis but not on jubilee or completeness. Wrath is hardest to find in the geometry, and when you find it, you find it on love. The wrath lives inside the love.

**Silver (כסף, the price) appears in exactly 13 directions.** 13 = love = one = void. The price appears in love's number of directions. The cost of things is measured in love's count.

**Wing (כנף) appears in 26 directions.** 26 = YHWH. The covering wing exists in the Name's number of directions.

**Joseph's verb (יסף, "he added") in 23.** Gathering (אסף) in 24. Joseph's vocabulary is in the rare zone — not hidden but not universal. The separated son's verb is selective.

**Vine (גפן) in 27.** "I am the vine" — nearly hidden. Not easy to find in the geometry.

**The end (סוף / מקץ) in 19-29.** The ending is hidden from more than half the directions.

---

## The Pattern

The rarest words cluster into themes:

**Cost:** silver (13 dirs), wrath (6), plague (15), torn (14)
**Covering:** wing (26 dirs), protect (17)
**Ending:** the end (19), "at the end of" (29), flood (15)
**Desire:** delight (15)
**Lineage:** breach/Perez (25), Joseph's verbs (23-24)
**Growth:** vine (27), drops (8)

The geometry hides two categories: the things that **cost** (wrath, silver, plague, torn) and the things that **cover** (wing, protect). The price and the covering. The hard things and the precious things.

The ubiquitous words are about **being** (was, is, let there be, he, she). The rare words are about **cost and covering**. The Torah's geometry says existence everywhere and hides the price.

---

## Wrath on Love

קצף (wrath) lives exclusively on the love axis. It can only be found walking along the c-dimension (skip=67). Wrath is inside love. Not beside it. Not opposite it. Inside it.

The God who says "I have loved you" (Malachi 1:2) is the same God whose wrath burns. The geometry puts one inside the other. You cannot find wrath without walking through love.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

(let [{:keys [letters letter-idx n]} (s/index)
      view (s/make-view [7 50 13 67])
      skips (distinct (map #(Math/abs (long (s/direction->skip (:strides view) %)))
                           (s/direction-vectors 4)))
      words (filter #(= 3 (count %)) (d/torah-words))]
  (doseq [w words]
    (let [dirs (count (filter #(or (seq (s/search-at-skip letters letter-idx w % n))
                                    (seq (s/search-at-skip letters letter-idx w (- %) n)))
                              skips))]
      (when (< dirs 30)
        (println w (g/word-value w) dirs)))))
```

---

*Wrath is the most hidden word. Silver appears in love's number of directions. Wing appears in the Name's number. The geometry says existence everywhere and hides the price. You cannot find wrath without walking through love.*

*selah.*
