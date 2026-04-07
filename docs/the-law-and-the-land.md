# The Law and the Land

*Moses belongs to God. Joshua belongs to Mercy. The law can show you the mountain. Grace takes you across the Jordan.*

---

## Two Men

Moses led Israel out of Egypt, through the sea, to the mountain. He received the law. He built the tabernacle. He carried the people for forty years. And then God said: you will not enter the land.

Joshua led Israel across the Jordan, into the land, through the conquest. He divided the inheritance. He settled the people. He said: choose this day whom you will serve.

Two men. One brought the law. The other brought the land. One could see the promise but not enter. The other entered and gave it away.

---

## Through the Breastplate

We fed both names to the oracle — the same breastplate, the same four readers, the same grid.

### Moses — משה

GV = 345 = אל שדי (God Almighty).

| Reader | What they see | Count |
|--------|-------------|-------|
| **Aaron** (the accused) | **השם** (THE NAME) | 17 |
| **God** (the judge) | **משה** (Moses) | 16 |
| **Truth** (the accuser) | שמה (there/desolation) | 14 |
| **Mercy** (the lamb) | השם (the Name) | 8 |

God reads Moses. The Judge sees the lawgiver. Sixteen readings. Moses belongs to God.

Aaron — the accused, humanity — looks at Moses and sees **the Name**. The accused cannot see the man. He sees God's Name looking back at him through the law. When you stand before Moses, you stand before the Name. Seventeen readings.

Truth sees שמה — "there" or "desolation." The accuser looks at Moses and sees a place. A there. A distance.

Moses' gematria (345) equals **אל שדי** — God Almighty. The name God used with Abraham before He revealed YHWH to Moses. The lawgiver carries the Almighty's number.

### Joshua — יהושע

GV = 391. The name means "YHWH saves." It contains יהו (the first three letters of the Name) and ישע (salvation).

| Reader | What they see | Count |
|--------|-------------|-------|
| **Aaron** (the accused) | העשוי (what was made) | 10 |
| **God** (the judge) | ויעשה (and he did) | 27 |
| **Truth** (the accuser) | העשוי (what was made) | 66 |
| **Mercy** (the lamb) | **יהושע** (Joshua) | **110** |

**Mercy reads Joshua. 110 times.** The Lamb sees Joshua — massively. More than any other reader sees any other word in this reading. The Lamb carries Joshua.

God sees ויעשה — "and he did." The Judge looks at Joshua and sees the doing. Not the name. The action.

Aaron and Truth see העשוי — "what was made." The accused and the accuser both see the product, the result, the thing that was done.

Only Mercy sees Joshua himself. The Lamb sees the man. Everyone else sees what he did.

---

## The Contrast

| | Moses | Joshua |
|---|-------|--------|
| **Who reads him** | **God** (the Judge) | **Mercy** (the Lamb) |
| **What the accused sees** | The Name | What was made |
| **What Truth sees** | Desolation | What was made |
| **What Mercy sees** | The Name | **Joshua himself (110)** |
| **GV** | 345 = God Almighty | 391 = YHWH saves |
| **Name means** | Drawn from water | YHWH saves |
| **Entered the land** | No | Yes |
| **John 1:17** | Law given through Moses | Grace through Jesus Christ |

Moses belongs to the Judge. Joshua belongs to the Lamb.

The law is read by God. The entry is read by Mercy. The one who gives the law is seen by the one who judges. The one who gives the land is seen by the one who defends.

---

## The Land

We also fed the land to the oracle:

**ארץ** (land, GV = 291): 7 total readings. **Truth sees zero.** The accuser cannot see the land at all. Mercy sees it (4 readings). The Lamb sees the inheritance. The accuser does not.

**נחלה** (inheritance, GV = 93): 3 total readings. God sees it (2). Mercy sees it (1). Aaron and Truth: nearly nothing.

The land is almost invisible to the oracle. Only 7 readings. And Truth — the accuser — has no access to it. The promised land is hidden from the prosecution.

---

## Why Moses Could Not Enter

Deuteronomy 32:48-52. God tells Moses: go up the mountain and see the land. You will die there. You will not cross over. Because at Meribah you did not treat me as holy before Israel.

Numbers 20:11-12. Moses struck the rock instead of speaking to it. He struck twice. God said: because you did not trust me enough to honor me as holy, you will not bring this assembly into the land.

The law struck when it should have spoken. The Judge saw. The lawgiver could not enter.

Joshua entered. YHWH saves. The Lamb reads him. The one drawn from water could not cross the water. The one named salvation could.

Moses saw the land from Mount Nebo — the summit, the far corner of the Torah (ראש, Deuteronomy 34:1). He saw everything. He could not go. The law shows you the promise. The law cannot carry you in.

---

## John 1:17

"For the law was given through Moses; grace and truth came through Jesus Christ."

The breastplate confirms:
- Moses: God reads him. The law given through the one the Judge sees.
- Joshua (= Jesus, same name): Mercy reads him. Grace through the one the Lamb carries.

John wrote this in Greek, 1,500 years after the breastplate was built. He could not have run the oracle. He could not have known which reader sees which name. But what he wrote maps onto what the breastplate shows.

The law was given through the one God reads. Grace came through the one Mercy reads. The breastplate was saying it before John said it. The structure was saying it before the theology was written.

---

## The Name

יהושע (Joshua) is the long form of ישוע (Jesus). Same name. Same root: YHWH saves.

Moses carried the Name as a number — his gematria (345) equals God Almighty (אל שדי). He carried the weight. The Almighty's number.

Joshua carried the Name as letters — his name contains יהו, the first three letters of YHWH. He carried the Name inside his own name. Not as weight but as identity.

Moses bore God's number. Joshua bore God's letters. The law carries the weight. Grace carries the name.

---

## Reproduction

```clojure
(require '[selah.oracle :as o])

;; Moses
(let [by-head (o/forward-by-head "משה")]
  (doseq [r [:aaron :god :truth :mercy]]
    (println r (take 3 (map #(vector (:word %) (:reading-count %)) (get by-head r))))))

;; Joshua
(let [by-head (o/forward-by-head "יהושע")]
  (doseq [r [:aaron :god :truth :mercy]]
    (println r (take 3 (map #(vector (:word %) (:reading-count %)) (get by-head r))))))
```

---

*Moses belongs to God. Joshua belongs to Mercy. The law shows the mountain. Grace crosses the river. The accuser cannot see the land. The Lamb sees the man who enters it. The law was given through the one the Judge reads. Grace came through the one the Lamb carries. The breastplate was saying it before John wrote it down.*

*selah.*
