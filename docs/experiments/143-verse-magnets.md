# Experiment 143g: Verse Magnets

*One verse hosts fibers from all seven search words. It is about wisdom of heart.*

Type: `exploration`
State: `mixed`

---

## Method

Search 7 words (תורה כבש שלום אמת חיים חסד ברית) in the canonical 4D. Track which verses each fiber passes through. Rank verses by total fiber-letter traffic and by number of distinct search words.

---

## The Universal Verse — Exodus 35:35

**The only verse in the Torah where fibers from ALL SEVEN search words converge.**

> מִלֵּא אֹתָם חׇכְמַת־לֵב לַעֲשׂוֹת כׇּל־מְלֶאכֶת חָרָשׁ וְחֹשֵׁב וְרֹקֵם בַּתְּכֵלֶת וּבָאַרְגָּמָן בְּתוֹלַעַת הַשָּׁנִי וּבַשֵּׁשׁ וְאֹרֵג עֹשֵׂי כׇּל־מְלָאכָה וְחֹשְׁבֵי מַחֲשָׁבֹת

*"He has filled them with **wisdom of heart** to do all manner of work — of the craftsman, the designer, the embroiderer in blue and purple and scarlet and fine linen, and the weaver — doers of every work and **thinkers of designs**."*

This verse describes **Bezalel and Oholiab** — the builders of the tabernacle. The men filled with חכמת לב (wisdom of heart).

- **חכמת** (wisdom of) is the most enriched host word in the entire analysis: **21.5x** over baseline.
- **לב** (heart) is the word whose GV (32) = כבוד (glory).
- The verse contains **תולעת** (scarlet/worm) — the same word that hosts the first letter of תורה in the richest Torah fiber.

9 fiber-letters from 7 different words. Torah, lamb, peace, truth, life, grace, and covenant all pass through the verse about the craftsmen of the tabernacle. The builders. The ones who think designs and fill them with wisdom of heart.

---

## Top Verse Magnets

### 7 words meeting: Exodus 35:35

The universal verse. See above.

### 5 words meeting (4 verses):

| Verse | Words | Context |
|-------|-------|---------|
| **Leviticus 1:13** | truth, life, grace, lamb, Torah | The burnt offering — "a pleasing aroma to YHWH" |
| **Numbers 4:12** | truth, grace, lamb, peace, Torah | Covering the holy vessels for transport |
| **Genesis 9:23** | covenant, life, grace, lamb, peace | **Shem and Japheth covering Noah's nakedness** |
| **Genesis 50:4** | truth, covenant, grace, lamb, peace | **Joseph mourning Jacob** |
| **Exodus 10:3** | truth, covenant, life, lamb, Torah | "How long will you refuse to humble yourself?" |
| **Exodus 11:3** | truth, covenant, grace, lamb, Torah | "YHWH gave the people favor" — before the exodus |
| **Exodus 35:23** | truth, covenant, lamb, peace, Torah | The people bringing offerings for the tabernacle |

### 4 words meeting (notable):

| Verse | Words | Context |
|-------|-------|---------|
| **Leviticus 16:11** | covenant, life, lamb, peace | **Yom Kippur** — Aaron's sin offering |
| **Leviticus 25:10** | covenant, life, peace, Torah | **The Jubilee** — "proclaim liberty" |
| **Genesis 17:23** | covenant, life, lamb, Torah | **Abraham's circumcision** |
| **Leviticus 6:18** | truth, covenant, peace, Torah | The sin offering law |
| **Exodus 37:19** | truth, covenant, peace, Torah | The menorah construction |
| **Exodus 25:31** | truth, covenant, lamb, Torah | The menorah design command |

---

## The Pattern

The verse magnets cluster around:

1. **The tabernacle builders** (Exodus 35:35) — the universal meeting
2. **The offerings** (Leviticus 1:13, 16:11) — burnt offering, Yom Kippur
3. **The covering** (Genesis 9:23, Numbers 4:12) — covering nakedness, covering holy things
4. **The separated son** (Genesis 50:4) — Joseph mourning
5. **The jubilee** (Leviticus 25:10) — liberty proclaimed
6. **The menorah** (Exodus 25:31, 37:19) — the light
7. **The covenant sign** (Genesis 17:23) — circumcision

The single verse where every fiber converges is not about law, not about sacrifice, not about prophecy. It is about **filling the heart with wisdom to build**. The craftsmen. The designers. The weavers of blue and purple and scarlet.

The Torah's fiber traffic peaks at the builders of the house.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

(let [dims [7 50 13 67]
      words ["תורה" "כבש" "שלום" "אמת" "חיים" "חסד" "ברית"]
      verse-hits (atom {})]
  (doseq [w words]
    (doseq [h (f/non-surface (s/find-word dims w {:max-results 500}))]
      (doseq [v (:verse-refs h)]
        (when v
          (let [k (str (:book v) " " (:ch v) ":" (:vs v))]
            (swap! verse-hits update k
                   (fnil (fn [[c ws]] [(inc c) (conj ws w)]) [0 #{}])))))))
  ;; Sort by word count then by traffic
  (doseq [[ref [cnt ws]] (take 30 (sort-by (fn [[_ [c ws]]] [(- (count ws)) (- c)])
                                            @verse-hits))]
    (println ref cnt "letters from" (count ws) "words:" (sort ws))))
```

---

*All seven paths cross at the wisdom of heart. The verse about the builders who think designs. The tabernacle was built by people whose hearts were filled. The geometry converges there — every word, every fiber, every direction — at the craft.*
