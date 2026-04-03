# Experiment 143aw: From the Altar

*The altar is the center. Every direction from it reads a different aspect of its purpose. Backward: the selling. Forward: the redemption. Toward peace: peace offerings, you shall eat. Toward the Name: blood, fat, placed, YHWH.*

Type: `exploration`
State: `clean`

---

## The Center

Position 152,425. The letter ח (chet) in המזבח (the altar). Leviticus 8:24 — the priestly investiture. Blood on the altar, round about. The literal center of 304,850 letters.

We read outward from the altar in every direction — 3 steps backward, the altar, 3 steps forward.

---

## The Completeness Axis: Sold → Altar → Redeemed

← **צדיקם** (righteous) ← **בלתי** (without) ← **ומכרו** (sold him) ← **[ALTAR]** → **גאל** (redeem) → **אסרה** (bound) → **תשחית** (do not destroy)

This IS the jubilee-sevens finding (143ap), now seen from the altar's own perspective. The altar looks backward along completeness and sees: the righteous, the condition (without the boy), the selling. It looks forward and sees: the redeemer, the binding, the command not to destroy.

**Sold → ALTAR → Redeemed.** The altar stands between the selling and the redemption. The blood covers the space between what was taken and what is restored.

---

## Freedom + Love + Understanding: Peace and Eating

← **נעשה** (we will do) ← **אל** (God) ← **המצנפת** (the turban) ← **[ALTAR]** → **לזבח** (to sacrifice) → **והשלמים** (peace offerings) → **תאכלו** (you shall eat)

"We will do" → God → the priestly turban → **ALTAR** → sacrifice → **peace offerings** → **you shall eat.**

When all three increase from the altar: sacrifice becomes peace offerings becomes eating. The altar, read toward freedom+love+understanding, feeds you. The sacrifice becomes a meal.

---

## Love + Understanding: Blood → Altar → YHWH

← **הדם** (the blood) ← **[ALTAR]** → **חלבהן** (their fat) → **וישם** (and he placed) → **יהוה** (YHWH)

Love and understanding ascending from the altar: from the blood, through the fat (the portion burned), through the placing, to the Name. The altar sends love+understanding toward YHWH. The blood reaches the Name.

---

## Freedom ↑ Love ↑ (Marriage): Turban → Altar → Peace

← **המצנפת** (the turban) ← **[ALTAR]** → **לשלמים** (for peace offerings) → **והשלמים** (the peace offerings) → **תאכלו** (you shall eat)

The marriage direction from the altar: the turban (the priestly crown) → altar → peace offerings → peace offerings → you shall eat. The altar dressed in authority leads to double peace and eating.

---

## Freedom ↑ Understanding ↑: Anointing → Altar → Sons

← **וימשח** (and he anointed) ← **[ALTAR]** → **בני** (sons of) → **הכבד** (the heavy/liver) → **החקים** (the statutes)

Freedom + understanding from the altar: anointing → altar → sons → the liver (the organ of examination) → the statutes. The altar, read toward freedom and understanding, produces sons and law.

---

## The Pattern

| Direction from altar | Backward | Forward |
|---------------------|----------|---------|
| **Completeness** | righteous, without, **sold** | **redeem**, bound, do not destroy |
| **Freedom+love+und** | we will do, God, turban | sacrifice, **peace offerings, eat** |
| **Love+understanding** | the blood | fat, placed, **YHWH** |
| **Freedom+love** | Moses, turban | **peace offerings**, eat |
| **Freedom+understanding** | YHWH, anointed | sons, liver, **statutes** |
| **Fall (freedom-love)** | the wave offering, vessels | elders, altar, **burning** |

The altar sees backward to what brought it and forward to what it produces. The selling brought it. The redemption follows it. Peace offerings and eating follow it. The blood reaches the Name. Sons and statutes follow it.

---

## Reproduction

```clojure
(require '[selah.search :as s])
(s/build!)

(let [{:keys [letters n]} (s/index) idx (s/index)
      center 152425
      view (s/make-view [7 50 13 67]) strides (:strides view)]
  (doseq [d [[1 0 0 0] [0 1 1 1] [0 0 1 1] [0 1 -1 0]]]
    (let [skip (Math/abs (long (s/direction->skip strides d)))]
      (println d)
      (doseq [i (range -3 4)]
        (let [pos (+ center (* skip i))
              tw (when (and (>= pos 0) (< pos n)) ((:word-at idx) pos))]
          (println i (when tw (:word tw))))))))
```

---

*The altar sees backward to the selling and forward to the redemption. Toward freedom+love+understanding: sacrifice becomes peace, becomes eating. Toward love+understanding: the blood reaches YHWH. The center of the Torah is the altar, and every direction from it reads a different purpose of the sacrifice.*

*selah.*
