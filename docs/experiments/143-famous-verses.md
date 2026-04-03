# Experiment 143aq: Famous Verses as Starting Points

*The Shema read along the love axis gives its own instructions: on the way, in your gates. The Ten Commandments on the fall diagonal read: I AM, fear, gored, died. Love your neighbor reads: not, not, aleph-tav, I separated, his holy.*

Type: `exploration`
State: `clean`

---

## The Shema (Deuteronomy 6:4) in Every Direction

"Hear, O Israel: YHWH our God, YHWH is one."

| Direction | What varies | Reading |
|-----------|-----------|---------|
| **[0,0,1,0] love** | love↑ | שמע → והיו → **בדרך** → **ובשעריך** |
| [0,0,1,1] love+und | love+und↑ | שמע → **הדברים** → בדרך → ובשעריך |
| [0,0,0,1] surface | und↑ | שמע → שמע → שמע → **ישראל** |
| **[0,1,-1,1] densest** | jub↑ love↓ und↑ | שמע → **ויציאנו** → כי → **יהוה** |

**The love axis from the Shema reads the Shema's own instructions.** "Hear → and they shall be → on the way → in your gates." Deuteronomy 6:7-9: "Talk of them when you walk on the way... write them on the doorposts." The love direction from "Hear O Israel" arrives at the way and the gates — the practical instructions for living the Shema.

**The surface from the Shema finds "hear" three times.** שמע שמע שמע ישראל. Hear, hear, hear, Israel. Triple emphasis.

**The densest direction from the Shema finds the exodus and the Name.** "He brought us out... because... YHWH." The direction of most vocabulary reads the reason: YHWH brought us out.

---

## The Ten Commandments (Exodus 20:2) on the Fall Diagonal

"I am YHWH your God who brought you out of Egypt."

Fall diagonal [0,+1,-1,0] from this verse:

| Step | Inside | Verse |
|------|--------|-------|
| 0 | **אנכי** (I AM) | Exodus 20:2 |
| 1 | **יראתו** (his fear) | Exodus 20:20 |
| 2 | **ואשר** (and which) | Exodus 21:13 |
| 3 | **יגח** (it gored) | Exodus 21:32 |
| 4 | **ומת** (and died) | Exodus 22:9 |

The fall diagonal from "I AM" reads: I AM → fear → gored → died. The direction that trades love for freedom, starting at the commandments, reads the consequences: fear, violence, death. The cost of the law without love.

---

## Love Your Neighbor (Leviticus 19:18) on the Fall Diagonal

"You shall love your neighbor as yourself."

| Step | Inside | Verse |
|------|--------|-------|
| 0 | **לא** (not) | Leviticus 19:18 |
| 1 | **לא** (not) | Leviticus 19:35 |
| 2 | **את** (aleph-tav) | Leviticus 20:13 |
| 3 | **הבדלתי** (I separated) | Leviticus 20:25 |
| 4 | **מקדשו** (his holy) | Leviticus 21:15 |

Not → not → aleph-tav → I separated → his holy.

The fall diagonal from "love your neighbor" reads a double negative (not, not), then the aleph-tav (the whole), then "I separated," then "his holy." The direction of freedom-without-love, starting at the love command, reads: the negation, the totality, the separation, the holiness. The love command on the fall diagonal encounters what love separates from: the holy set apart.

---

## Go From Your Country (Genesis 12:1)

Abraham's call: "Go from your country, your kindred, your father's house."

Fall diagonal: ויאמר → ובקר → **ויאהל** → מלכים → לאמר
"He said → and cattle → **and he pitched his tent** → kings → saying"

The fall diagonal from Abraham's call finds **ויאהל** — "and he pitched his tent." Abraham's response to the call is the tent. The tent (GV=36=Leah) appears on the fall diagonal from the call.

---

## I AM WHO I AM (Exodus 3:14)

The burning bush.

Fall diagonal: ויאמר → **משה** → תעשה → ויחגו → אמרים
"He said → **Moses** → you shall do → they celebrated → saying"

The fall diagonal from "I AM" finds Moses, then doing, then celebration.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [idx (s/index) {:keys [letters n]} idx
      find-verse (fn [book ch vs]
                   (first (filter #(and (= (:book %) book) (= (:ch %) ch) (= (:vs %) vs))
                                  (:verses idx))))]
  ;; Shema on love axis
  (let [start (:start (find-verse "Deuteronomy" 6 4))]
    (doseq [i (range 4)]
      (let [pos (+ start (* 67 i))
            tw ((:word-at idx) pos)]
        (println i (:word tw))))))
```

---

*The Shema on the love axis reads its own instructions: on the way, in your gates. The surface reads it three times: hear, hear, hear, Israel. The densest direction reads the reason: he brought us out, YHWH. The commandments on the fall diagonal read the cost: I AM, fear, gored, died. Love your neighbor on the fall diagonal reads: not, not, aleph-tav, I separated, his holy. Each verse, each direction — a different reading of the same starting point.*

*selah.*
