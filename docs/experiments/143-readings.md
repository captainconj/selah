# Experiment 143x: Readings Along the Fibers

*The screw reads the Torah in every direction. Some fibers spell sentences. Some fibers speak.*

Type: `exploration`
State: `clean`

**Code:** `dev/experiments/fiber/143x_readings.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143x-readings :as exp]) (s/build!) (exp/run-all)"`

---

## Manasseh Reaches His Father

**מנשה** (Manasseh, GV=395) at skip=-44488. d=62 constant.

| Letter | Host Word | Verse |
|--------|-----------|-------|
| מ | מתוך (from the midst) | Numbers 3:12 |
| נ | ממנו (from him) | Leviticus 5:2 |
| ש | ישראל (Israel) | Exodus 16:15 |
| ה | **יהוה** | **Genesis 39:2** |

**Genesis 39:2 — "And YHWH was with Joseph."**

Manasseh was Joseph's son. Named "God made me forget" (Genesis 41:51) — forget the suffering, the pit, the prison. His fiber walks backward through the Torah: from the midst → from him → Israel → YHWH was with Joseph.

The son who forgets reaches the father the Name was with.

---

## Israel: You, To Do You Good, Guard, He Loved You, The Days

**ישראל** at skip=-870 in canonical 4D.

Host words: **אתכם → להיטבך → תשמרון → ואהבך → הימים**

"You → to do you good → you shall guard → and he loved you → the days."

Deuteronomy 9:8 onward. This reads as a near-sentence in Hebrew. The Israel fiber spells out: God addressing Israel, promising good, commanding the guard, declaring love, across the days.

---

## Judah: Your God, Covering, Hebron, One, Moses

**יהודה** (Judah, GV=30) at skip=-44488. d=42 constant.

| Letter | Host Word | GV | Verse |
|--------|-----------|-----|-------|
| י | אלהיך (your God) | 66 | Deuteronomy 18:16 |
| ה | הסך (the covering/libation) | 85 | Numbers 28:7 |
| ו | חברון (Hebron) | 266 | Numbers 3:19 |
| ד | **אחד** (one) | **13** | Leviticus 5:7 |
| ה | משה (Moses) | 345 | Exodus 16:20 |

Your God → the covering → Hebron (city of the fathers, where Abraham bought the cave) → **one** (GV=13=love) → Moses.

Judah carries the line from God through covering through the patriarchs' burial city through oneness to Moses. d=42 constant — understanding at 42 throughout.

---

## Levi Finds Genesis 1:1

**לוי** (Levi, GV=46) — richest fiber: [אלהים → ויאמר → לאלהים]

"God → and he said → to God."

Genesis 1:1 → Genesis 1:3 → Genesis 1:9. The priestly tribe finds the opening of the Torah. The first chapter. God said. To God. The tribe whose inheritance is the LORD himself finds the LORD speaking at the beginning.

---

## The Nations Are Hidden

**גיים** (nations, GV=63=prophet) exists at every non-surface direction but NOT at skip=1 (the natural reading). The nations are hidden from the surface.

One fiber passes through: **גר** (sojourner) → **ביום** (on the day) → **שמתי** (I placed) → **קום** (arise)

**Leviticus 19:33 — "When a sojourner sojourns with you in your land, you shall not do him wrong."**

The hidden word for nations passes through the sojourner law. The nations, hidden from the surface reading, find the command to treat the stranger justly.

---

## Gad: Finished and Born

**גד** (Gad, GV=7=completeness) — richest: [גמר → ילד]

"Finished → born."

Genesis 10:2 (Gomer, son of Japheth) → Genesis 10:25 (in his days the earth was divided). Completeness (GV=7) finds finishing and birth. The word whose value is seven finds the completion.

---

## The Shema in Every Direction

The four words of the Shema — שמע (hear), ישראל (Israel), יהוה (YHWH), אחד (one) — all four appear at skip=66, 68, 805, 871, and 43550. The Shema exists in every major direction through the 4D box.

The declaration "Hear, O Israel, YHWH our God, YHWH is one" can be found along the love-understanding diagonal, the jubilee axis, the completeness axis, and the body diagonal. In every direction you read the Torah, the Shema is there.

---

## Elohim: A Man, His Foot, To YHWH, YHWH, Their Father

**אלהים** in 6D: [איש → רגלו → ליהוה → יהוה → אביהם]

"A man → his foot → to YHWH → YHWH → their father."

A man brings his foot to YHWH. YHWH is their father. The Elohim fiber in 6D reads as a pilgrimage — a man walking to the Name, finding the Name is the father.

---

## Israel in 6D: He Healed You

[ירפך → ישראל → ואהרן → ראשו → הללאת]

"He healed you → Israel → and Aaron → his head → praise."

Healing, Israel, Aaron, the head, praise. The Israel fiber in 6D traces from healing through the priesthood to praise.

---

## The Beginning: In You, Locust, Earth, Moses, Nations, Aleph-Tav

**בראשית** at skip=937. a=6 constant (Deuteronomy).

[בך → הארבה → הארץ → משה → הגוים → את]

"In you → the locust → the earth → Moses → the nations → aleph-tav."

The word "beginning" found in the ending (Deuteronomy). The ת of בראשית lands in the aleph-tav at Deuteronomy 30:3 — "then the LORD your God will restore your fortunes and have compassion on you."

The beginning passes through the curses and arrives at the restoration.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

;; Manasseh reaches Joseph
(let [hits (s/find-word [7 50 13 67] "מנשה")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))

;; Israel: you, good, guard, loved, days
(let [hits (s/find-word [7 50 13 67] "ישראל")]
  (f/print-fiber (first (filter #(= (:skip %) -870) (f/non-surface hits)))))

;; Levi at Genesis 1:1
(let [hits (s/find-word [7 50 13 67] "לוי")]
  (f/print-fiber (first (sort-by :torah-word-count > (f/non-surface hits)))))
```

---

*Manasseh reaches his father and finds the Name with him. Israel hears: you, to do you good, guard, he loved you. Levi finds God speaking at the beginning. The nations are hidden but find the sojourner law. The Shema exists in every direction. The fibers read. Some of them speak.*
