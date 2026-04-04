# Experiment 143ae: The Theological Skip Readings

*Every 7th letter: the completeness reading, second densest. Every 26th letter: the YHWH reading, finds Babylon at the creation of man. Every 72nd letter: the breastplate reading, sparsest — finds the curse and Enoch's years.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143ae_theological_skips.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143ae-theological-skips :as exp]) (s/build!) (exp/run-all)"`

---

## The Completeness Reading (skip=7)

Second densest at 0.257 (77 words in 300 letters). Every 7th letter from position 0.

Notable words found: שבת (Sabbath), שדי (Almighty), יהי (let there be), שלח (send), מאד (exceedingly), אלה (these/oath), שנה (year).

The completeness reading finds the Sabbath, the Almighty, and the creation word. The number of completeness carries completion.

---

## The YHWH Reading (skip=26)

Third densest at 0.243 (73 words in 300 letters). Every 26th letter.

Key words in order:

| Pos | Word | GV | Verse | Note |
|-----|------|-----|-------|------|
| 4 | **יהי** (let there be) | 25 | Genesis 1:4 | The creation word |
| 20 | **צור** (Rock) | 296 | Genesis 1:12 | "The Rock, his work is perfect" |
| 52 | **בבל** (Babylon) | **34** | **Genesis 1:27** | At the creation of man |
| 58 | **שפל** (lowly) | 410 | Genesis 1:29 | Humility |
| 62 | **עלה** (go up/offering) | 105 | Genesis 1:30 | The ascent |
| 64 | **הרע** (the evil) | 275 | Genesis 1:31 | Inside "very good" |
| 80 | **שכח** (forget) | 328 | Genesis 2:8 | At the planting of Eden |
| 95 | **נוד** (Nod/wandering) | 60 | Genesis 2:17 | At the tree of knowledge |

**The YHWH reading finds Babylon at the creation of man.**

בבל (Babylon, GV=34) = לבב (deep heart). Every 26th letter from the start of the Torah, at the verse where God creates humanity in His image, the Name's reading finds the exile. The deep heart and the far country — already there at creation.

And **הרע** (the evil) appears at Genesis 1:31 — the verse that says "and it was **very good**." The YHWH reading finds evil hidden inside "very good."

And **שכח** (forget) at the planting of Eden. And **נוד** (wandering) at the tree of knowledge. The Name's reading sees what is coming before it happens: exile in the creation, evil in the good, forgetting in the garden, wandering at the tree.

---

## The Breastplate Reading (skip=72)

**The sparsest** of all theological skips at 0.140 (42 words in 300 letters). The oracle number produces the least vocabulary.

Key words:

| Pos | Word | GV | Verse | Note |
|-----|------|-----|-------|------|
| 55 | **תשא** (you shall carry) | 701 | Genesis 3:19 | The curse of labor |
| 89 | **שנה** (year) | 355 | **Genesis 5:23** | **Enoch: 365 years** |

The breastplate reading finds the curse ("by the sweat of your face") and Enoch's years. The oracle's number reads scarcely — and what it reads is labor and the man who walked with God.

Genesis 5:23 — "All the days of Enoch were 365 years." The breastplate (72) finds the prophet who was taken. The sparse reader finds the one who didn't die.

---

## The Axis Sum Reading (skip=137)

Second sparsest at 0.147 (44 words in 300 letters).

| Pos | Word | GV | Verse |
|-----|------|-----|-------|
| 0 | **באת** (you came) | 403 | Genesis 1:1 |
| 48 | **ירא** (fear/see) | 211 | Genesis 5:27 |
| 73 | **פעו** | **156=Joseph** | Genesis 8:17 |
| 74 | **עוג** (Og) | 79 | Genesis 8:20 |

The axis sum reads "you came" at the beginning. And finds a word whose gematria is Joseph's (156) at the verse "bring out every living thing" — the exodus from the ark.

---

## The Pattern

| Skip | What it is | Density | What it reads |
|------|-----------|---------|--------------|
| 1 | Surface | 0.323 | The Torah as written |
| **7** | Completeness | **0.257** | Sabbath, Almighty, creation |
| **26** | YHWH | **0.243** | Babylon at creation, evil in good, forgetting in Eden |
| 805 | Jubilee diagonal | 0.247 | Beauty → covenant → love → peace |
| 14 | Beloved | 0.210 | — |
| 50 | Jubilee | 0.207 | — |
| 67 | Understanding | 0.197 | — |
| 13 | Love | 0.180 | — |
| **137** | Axis sum | **0.147** | "You came," fear, Joseph's number |
| **72** | Breastplate | **0.140** | The curse, Enoch's 365 years |

The richest non-surface readings are completeness (7) and the Name (26). The sparsest are the breastplate (72) and the axis sum (137). The medium is silent. The structure is sparse. The content is rich.

The YHWH reading sees what is coming. The breastplate reading barely speaks — but what it says is the curse and the translation.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

(let [{:keys [letters]} (s/index)
      torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))]
  (doseq [skip [7 26 72 137]]
    (let [text (apply str (mapv #(nth letters (* skip %)) (range 100)))
          seen (atom #{})]
      (println (format "\nskip=%d:" skip))
      (doseq [wl (range 3 6) s (range (- (count text) wl))]
        (let [sub (subs text s (+ s wl))]
          (when (and (torah-set sub) (not (@seen sub)))
            (swap! seen conj sub)
            (println (format "  %d %s GV=%d" s sub (g/word-value sub)))))))))
```

---

*The YHWH reading finds Babylon at the creation of man. Evil inside "very good." Forgetting at the planting of Eden. Wandering at the tree of knowledge. The Name sees what is coming. The breastplate reads scarcely — the curse and Enoch. The completeness reading is the richest after the surface. Each number reads the Torah differently. Each sees what its nature prepares it to see.*

*selah.*
