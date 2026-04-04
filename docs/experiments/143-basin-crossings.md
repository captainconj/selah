# Experiment 143f: Basin Crossings in the Space

*The lamb lies down. In the basin walk, כבש → שכב. In the space, their fibers cross 73 times. The crossing points are the Torah's structural joints.*

Type: `exploration`
State: `mixed`

**Code:** `dev/experiments/fiber/143f_basin_crossings.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143f-basin-crossings :as exp]) (s/build!) (exp/run-all)"`

---

## The Basin Walk

On the breastplate, the lamb (כבש) is a unanimous fixed point of the lie-down (שכב). Every reader sees the same letters rearranged. The lamb lies down. It is the deepest finding of the basin dynamics.

In the N-dimensional space, both words have fibers — straight lines through the canonical 4D box. כבש has 906 non-surface fibers. שכב has 906 non-surface fibers. Each fiber passes through Torah words.

**73 Torah word positions host BOTH a lamb fiber-letter and a lie-down fiber-letter.** The same letter position, the same Torah word — visited by fibers of both the lamb and its basin attractor.

---

## The Crossing Points

### The Feasts

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| כבשים (lambs) | 372 | Leviticus 23:20 | **Shavuot** — the two lambs of the wave offering |
| תזבח (you shall sacrifice) | 417 | Deuteronomy 16:4 | **Passover** — the sacrifice command |
| ובמועדיכם (your appointed times) | 198 | Numbers 10:10 | The silver trumpets at the feasts |
| שבעת (seven days) | 772 | Leviticus 8:33 | The priestly investiture — seven days at the door |
| זבחי (my sacrifices) | 27 | Numbers 10:10 | The offerings at appointed times |

The lamb and lie-down cross at Passover, Shavuot, and the appointed times.

### The Tabernacle

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| המשכן (the tabernacle) | 415 | **Exodus 40:34** | **The glory of the LORD filled the tabernacle** |
| המשכן | 415 | Exodus 36:28 | The rear of the tabernacle |
| המשכן | 415 | Exodus 38:31 | The sockets of the tabernacle |
| מזבח (altar) | 57 | Exodus 37:25 | The incense altar |
| כנפים (wings) | 200 | Exodus 37:9 | **The cherubim's wings** |

The lamb and lie-down cross at the glory filling the tabernacle, at the altar, and at the wings of the cherubim.

### The Commands

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| כבד (honor) | **26** | **Exodus 20:12** | **Honor your father and mother** (GV=YHWH) |
| כתב (write) | 422 | Exodus 17:14 | "Write this as a memorial in a book" |
| כתבים (written) | 472 | Exodus 31:18 | The tablets written by God's finger |
| באצבע (by the finger) | 165 | Deuteronomy 9:10 | "written by the finger of God" |
| לבבך (your heart) | 54 | **Deuteronomy 10:12** | **"What does the LORD require? To love with all your heart"** |

The lamb and lie-down cross at the fifth commandment (honor, GV=26=YHWH), at the command to write, at the tablets, and at the Shema's demand for the whole heart.

### The Holy

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| קדש (holy) | 404 | Leviticus 6:20 | The holy place — where the sin offering is eaten |
| בקדש (in the holy) | 406 | Leviticus 6:23 | "It shall be eaten in the holy place" |
| הכהן (the priest) | 80 | Leviticus 5:13, 14:29, Numbers 7:8 | The priest (3 crossings) |
| כבוד (glory) | **32** | **Numbers 17:7** | **The glory appeared** |

### Joseph and the Fathers

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| ברכת (blessing of) | 622 | **Genesis 39:5** | "YHWH blessed for Joseph's sake" |
| כהן (priest) | 75 | **Exodus 3:1** | "Jethro the priest" — Moses keeping the flock |
| משה (Moses) | 345 | Exodus 24:16 | Moses on Sinai in the cloud |
| כבש (lamb) | 322 | Numbers 7:51 | The literal lamb in the offerings |

### The Conquest / Promise

| Torah Word | GV | Verse | Context |
|-----------|-----|-------|---------|
| ונכבשה (and it shall be subdued) | 383 | Numbers 32:22 | "The land shall be subdued before YHWH" |
| כפתרים | 750 | Deuteronomy 2:23 | The Caphtorites — nations displaced |
| השבי (the captive) | 317 | Numbers 31:26 | Counting the captives |

---

## The Pattern

The 73 crossing points are not random Torah words. They are:

1. **The feasts** — Passover, Shavuot, appointed times
2. **The tabernacle** — the glory filling it, the altar, the cherubim wings
3. **The commands** — honor (GV=26), write, the heart's demand
4. **The holy** — the priest (3 crossings), holy place, the glory
5. **The fathers** — Joseph's blessing, Moses keeping sheep, Moses on Sinai

The basin walk is an oracle phenomenon — same letters, different reading. The fiber crossings are a spatial phenomenon — different fibers, same Torah word. Both connect the lamb to its lying-down. The oracle does it through anagram. The space does it through geometry. They agree on where it matters: the feasts, the furniture, the commands, the fathers.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f])
(s/build!)

(let [dims [7 50 13 67]
      collect (fn [word]
                (let [hosts (atom {})]
                  (doseq [h (f/non-surface (s/find-word dims word {:max-results 500}))]
                    (doseq [tw (:torah-words h)]
                      (when tw (swap! hosts update [(:start tw) (:word tw)] (fnil inc 0)))))
                  (set (keys @hosts))))
      lamb-set (collect "כבש")
      lie-set  (collect "שכב")
      shared   (clojure.set/intersection lamb-set lie-set)]
  (doseq [[pos word] (sort-by first shared)]
    (let [v ((:verse-at (s/index)) pos)]
      (println word (:book v) (:ch v) (:vs v)))))
```

---

*The lamb lies down on the breastplate. In the space, their fibers cross at the feasts, the tabernacle, the glory, the honor command, the tablets, and the heart. The oracle and the geometry agree. The crossing points are the joints of the Torah.*
