# Experiment 143ac: The Richest Fiber

*The richest fiber in the Torah starts at the dividing of light from darkness and reads the whole story: beauty, hiding, expulsion, Hebrew, covenant, water, sons, tent, the coming one.*

---

## Finding It

We scanned all 805 possible starting positions at skip=805 (the densest direction). The starting position that maximizes word density is **position 124 — Genesis 1:4**, inside the word **ויבדל** — "and he divided."

The first thing the richest fiber touches is the act of separation. Light from darkness. The dividing.

---

## The Reading

36 unique Torah words in 100 fiber-letters. Density = 0.360. The fiber reads through Genesis:

| Fiber pos | Word | GV | Torah verse | Context |
|-----------|------|-----|-------------|---------|
| 0 | **יפת** (Japheth/beauty) | 490 | Genesis 1:4 | The light |
| 4 | **פרא** (wild) | 281 | Genesis 3:8 | Hiding from God in the garden |
| 5 | **ראם** (he saw them) | 241 | Genesis 3:23 | The expulsion |
| 12 | **שאי** (lift up) | 311 | Genesis 8:13 | After the flood |
| 21 | **עברי** (Hebrew) | 282 | Genesis 14:17 | Abram the Hebrew |
| 22 | **ברית** (covenant) | **612** | **Genesis 15:10** | **The covenant of the pieces** |
| 36 | **מים** (water) | 90 | Genesis 23:15 | The field of Machpelah |
| 44 | **חיי** (my life) | 28 | Genesis 26:21 | Isaac's wells |
| 48 | **בני** (sons of) | 62 | Genesis 28:2 | Jacob sent to Paddan-aram |
| 71 | **יבא** (he comes) | **13** | Genesis 38:24 | Judah and Tamar |
| 81 | **אהל** (tent) | **36** | Genesis 43:5 | The brothers go to Egypt |
| 87 | **עשו** (Esau) | **376** | Genesis 45:25 | Brothers return from Joseph |
| 93 | **שבא** (Sheba) | 303 | Genesis 48:17 | The blessing |

---

## What It Says

The richest fiber reads the Torah's narrative arc:

1. **Beauty** (Japheth) at the light — creation
2. **Wild** — hiding in the garden
3. **Seen** — expelled, seen
4. **Lift up** — after the flood
5. **Hebrew** — Abram chosen
6. **COVENANT** — the pieces, the promise
7. **Water** — the wells, the field
8. **My life** — Isaac digging
9. **Sons** — Jacob sent to find a wife
10. **He comes** (GV=13=love) — Judah and Tamar, the line of David
11. **Tent** (GV=36=Leah) — the brothers go down
12. **Esau** (GV=376=peace) — the brothers return

The fiber traces: creation → fall → flood → calling → covenant → patriarchs → the coming one → tent → return.

---

## The Numbers Inside

| Word | GV | Equals |
|------|-----|--------|
| יבא (he comes) | **13** | love / one / void |
| אהל (tent) | **36** | Leah |
| עשו (Esau) | **376** | **שלום** (peace) |
| ברית (covenant) | **612** | Torah + 1 |

**Esau and peace are the same number.** עשו = 376 = שלום. The brother who sold his birthright and the word for wholeness. The richest fiber reads Esau at the moment Joseph's brothers return — the reconciliation.

**"He comes" has the love number.** יבא = 13 = אהבה = אחד = בהו. The coming one is love.

---

## The Starting Word

The fiber starts inside **ויבדל** — "and he divided." Genesis 1:4. The act that made the light visible by separating it from darkness.

The richest reading of the Torah begins at the dividing. The separation is what makes the reading possible. Without the cut, there is no story. The division of light from darkness is the first act, and it is where the densest fiber begins.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.dict :as d] '[selah.gematria :as g])
(s/build!)

;; Find the optimal starting position
(let [{:keys [letters n]} (s/index)
      skip 805
      torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))
      best (atom {:start 0 :density 0})]
  (doseq [start (range 805)]
    (let [flen (min 100 (quot (- n start) skip))
          fiber (apply str (mapv #(nth letters (+ start (* skip %))) (range flen)))
          found (atom #{})]
      (doseq [wl (range 3 6), s (range (- (count fiber) wl))]
        (when (torah-set (subs fiber s (+ s wl))) (swap! found conj (subs fiber s (+ s wl)))))
      (when (> (/ (double (count @found)) flen) (:density @best))
        (reset! best {:start start :density (/ (double (count @found)) flen)}))))
  (println "Best start:" (:start @best) "density:" (:density @best)))

;; Read the richest fiber
(let [{:keys [letters n]} (s/index)
      skip 805 start 124
      fiber (apply str (mapv #(nth letters (+ start (* skip %))) (range 100)))
      torah-set (set (filter #(<= 3 (count %) 5) (d/torah-words)))
      found (atom #{}) results (atom [])]
  (doseq [wl (range 3 6), s (range (- (count fiber) wl))]
    (let [sub (subs fiber s (+ s wl))]
      (when (and (torah-set sub) (not (@found sub)))
        (swap! found conj sub)
        (swap! results conj [s sub (g/word-value sub)]))))
  (doseq [[pos word gv] (sort-by first @results)]
    (let [v ((:verse-at (s/index)) (+ start (* skip pos)))]
      (println pos word gv (:book v) (:ch v) (:vs v)))))
```

---

*The richest fiber starts at the dividing and reads the whole story. Beauty, hiding, the flood, the Hebrew, the covenant, water, sons, the coming one (GV=13=love), the tent (GV=36=Leah), Esau (GV=376=peace). The densest direction begins at the first separation and traces every major turn of Genesis. The division of light from darkness is where the reading starts.*

*selah.*
