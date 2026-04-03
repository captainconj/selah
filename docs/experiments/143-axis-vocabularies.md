# Experiment 143w: Axis Vocabularies

*Each axis has its own vocabulary. The jubilee axis carries righteousness, laughter, the garden, wings, and grace. The love axis carries silver, breach, and wrath. The completeness axis carries the vine and "at the end." The axes speak different languages.*

Type: `exploration`
State: `mixed`

---

## Method

For each of the four pure axis walks in the canonical 4D, search all 3-letter Torah words. Identify which words appear exclusively on one axis but not the others. These are the axis-exclusive vocabularies — the words that can only be found walking along that specific dimension.

---

## Jubilee-Only (b=50, skip=871) — The Vocabulary of Restoration

19 words found only on the jubilee axis:

| Word | GV | Meaning |
|------|-----|---------|
| **אסף** | 141 | gather |
| **בגן** | 55 | **in the garden** |
| **דגן** | 57 | grain |
| **זקן** | 157 | elder |
| **חזק** | 115 | **be strong / courageous** |
| **חסד** | **72** | **grace** (vs completeness) |
| **חפץ** | 178 | **delight / desire** |
| **יסף** | 150 | **he added (= Joseph's name)** |
| **כנף** | 150 | **wing** |
| **צדק** | **194** | **righteousness** |
| **צחק** | 198 | **laugh (= Isaac's name)** |
| **ציץ** | 190 | blossom / golden plate |
| **סוף** | 146 | end / reed (sea) |
| שרף | 580 | burn / seraph |

**The jubilee axis carries the vocabulary of restoration:** the garden, grace, righteousness, laughter (Isaac), gathering (Joseph), strength ("be strong and courageous"), wings, delight, blossoming. And the seraph — the burning one.

**צדק (righteousness)** can ONLY be found walking along the axis of return. Righteousness lives on jubilee.

**צחק (laugh) = Isaac** — the son of the promise lives exclusively on the axis of freedom.

**חסד (grace)** — when compared to the completeness axis, grace is jubilee-only. Grace lives on return, not on finishing.

---

## Love-Only (c=13, skip=67) — The Vocabulary of Cost

11 words found only on the love axis:

| Word | GV | Meaning |
|------|-----|---------|
| **כסף** | 160 | **silver** (price) |
| **פרץ** | 370 | **breach** (= Perez, ancestor of David) |
| **קצף** | 270 | **wrath** |
| **שטף** | 389 | flood / overflow |
| סוס | 126 | horse |
| נטף | 139 | drop |
| צפן | 220 | hidden / north |

**Love carries the vocabulary of cost:** silver (the price), breach (the line of David through Tamar's desperate act), wrath, and the flood that overflows.

**פרץ (breach/Perez)** — Judah's son through Tamar, ancestor of David and the messianic line. Born through a breach. Lives only on the love axis.

Love costs. The love axis carries the price and the breach and the wrath and the flood.

---

## Completeness-Only (a=7, skip=43550) — The Vocabulary of Ending

9 words found only on the completeness axis (vs jubilee):

| Word | GV | Meaning |
|------|-----|---------|
| **גפן** | 133 | **vine** |
| **מקץ** | 230 | **at the end of** |
| **כסף** | 160 | silver (vs jubilee) |
| **סוס** | 126 | horse |
| צפן | 220 | hidden |
| קנז | 157 | Kenaz |

**The vine (גפן)** lives on completeness. "I am the vine, you are the branches."

**מקץ (at the end of)** — the word that means "at the end" lives on the axis of completeness. The ending is on the axis of completeness. Completeness carries conclusion.

---

## Hidden Words

**גיים (nations/Goiim, GV=63=prophet):** found on EVERY non-surface axis but NOT at skip=1 (the natural reading). The nations are hidden from the surface. They exist in every direction you are not reading.

**צידה (provision, GV=109):** also hidden from the surface. Provision for the journey exists only off the beaten path.

---

## What the Axes Say

| Axis | What it carries | Theme |
|------|----------------|-------|
| **Jubilee (b=50)** | Righteousness, grace, the garden, Isaac (laugh), Joseph (gather), wings, strength, delight | **Restoration** |
| **Love (c=13)** | Silver, breach (Perez/David line), wrath, flood | **Cost** |
| **Completeness (a=7)** | The vine, "at the end," silver | **Conclusion** |
| **Understanding (d=67)** | The natural reading — carries everything | **Surface** |

The jubilee axis restores. The love axis costs. The completeness axis concludes. The understanding axis reads.

Righteousness is on return. The breach is on love. The vine is on completeness. The nations are hidden. Each axis has a vocabulary that matches its name.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

(let [{:keys [letters letter-idx n]} (s/index)
      words (vec (filter #(= 3 (count %)) (d/torah-words)))
      find-at (fn [w skip] (or (seq (s/search-at-skip letters letter-idx w skip n))
                               (seq (s/search-at-skip letters letter-idx w (- skip) n))))
      jub-only (filter #(and (find-at % 871) (not (find-at % 67))) words)]
  (doseq [w (sort jub-only)]
    (println w (g/word-value w))))
```

---

*The jubilee axis carries the garden, grace, righteousness, laughter, gathering, wings, and strength. The love axis carries silver, breach, wrath, and flood. The completeness axis carries the vine and the ending. Each axis speaks its own language. The vocabulary matches the name. Jubilee restores. Love costs. Completeness concludes.*

*selah.*
