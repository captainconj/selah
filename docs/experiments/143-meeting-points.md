# Experiment 143d: Meeting Points

*Where do fibers from different search words converge on the same Torah word? The geometry has gathering places.*

Type: `exploration`
State: `mixed`

**Code:** `dev/experiments/fiber/143d_meeting_points.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143d-meeting-points :as exp]) (s/build!) (exp/run-all)"`

---

## Method

Search 7 words (תורה כבש שלום אמת חיים חסד ברית) in the canonical 4D. For each non-surface fiber, record which Torah word each letter lands in — by *position* (start index of the host word). Find positions where 3 or more different search words share a host.

---

## Results

8 positions where 3+ search words meet on the same Torah word.

### 1. והקריב — "and he shall bring near"
**Leviticus 16:11** — Yom Kippur. Aaron's sin offering.

Hosts: **ברית** (covenant), **חיים** (life), **שלום** (peace)

Covenant, life, and peace converge at the Day of Atonement.

### 2. שדה — "field"
**Numbers 23:14** — The field of Zophim, top of Pisgah. Balaam's second oracle.

Hosts: **כבש** (lamb), **שלום** (peace), **תורה** (Torah)

Lamb, peace, and Torah meet where the prophet who came to curse speaks blessing instead.

### 3. ובמנרה — "and in the menorah"
**Exodus 37:20** — Bezalel making the lampstand.

Hosts: **אמת** (truth), **כבש** (lamb), **שלום** (peace)

Truth, lamb, and peace meet at the light.

### 4. מצרים — "Egypt"
**Exodus 9:25** — The plague of hail.

Hosts: **אמת** (truth), **ברית** (covenant), **תורה** (Torah)

Truth, covenant, and Torah meet in the plague that struck Egypt.

### 5. והתנחלתם — "and you shall inherit"
**Leviticus 25:46** — The jubilee legislation.

Hosts: **אמת** (truth), **חיים** (life), **תורה** (Torah)

Truth, life, and Torah meet at the inheritance — the jubilee, the return.

### 6. הושבתי — "I made to dwell"
**Leviticus 23:43** — The feast of booths.

Hosts: **אמת** (truth), **ברית** (covenant), **חיים** (life)

Truth, covenant, and life meet at Sukkot — when Israel dwells in booths to remember the wilderness.

### 7. תביאנה — "they shall bring"
**Leviticus 12:4** — After childbirth.

Hosts: **אמת** (truth), **ברית** (covenant), **תורה** (Torah)

Truth, covenant, and Torah meet at the bringing after birth.

### 8. התועבת — "the abomination"
**Leviticus 18:27** — The warning against the practices of the nations.

Hosts: **אמת** (truth), **ברית** (covenant), **תורה** (Torah)

Truth, covenant, and Torah converge at the abomination — the thing that must not be.

---

## The Pattern

The geometry's gathering places:

- **Yom Kippur** gathers covenant + life + peace
- **The menorah** gathers truth + lamb + peace
- **The jubilee** gathers truth + life + Torah
- **Sukkot** gathers truth + covenant + life
- **Balaam's field** gathers lamb + peace + Torah

The festivals and the furniture. Atonement, light, inheritance, dwelling, blessing. These are where the fibers cross.

No meeting point hosts all 7 words. The closest clusters are 3. The space keeps the words distinct — they meet in triads, not in masses.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

(let [dims [7 50 13 67]
      words ["תורה" "כבש" "שלום" "אמת" "חיים" "חסד" "ברית"]
      hosts (atom {})]
  (doseq [w words]
    (let [hits (s/find-word dims w {:max-results 500})
          ns (f/non-surface hits)]
      (doseq [h ns, tw (:torah-words h)]
        (when tw (swap! hosts update (:start tw) (fnil conj #{}) w)))))
  ;; Positions with 3+ words
  (doseq [[pos ws] (sort-by (comp count val) > @hosts)]
    (when (>= (count ws) 3)
      (let [tw ((:word-at (s/index)) pos)
            v  ((:verse-at (s/index)) pos)]
        (println (:word tw) (:book v) (:ch v) (:vs v) (sort ws))))))
```

---

*The menorah holds truth, lamb, and peace. Yom Kippur holds covenant, life, and peace. The jubilee holds truth, life, and Torah. The fibers converge at the furniture and the festivals. The geometry gathers what the surface separates.*
