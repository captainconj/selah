# Experiment 097: Per-Head Basin Landscapes

*The lamb lies down. No one told it to.*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/097_per_head_basins.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.097-per-head-basins :as exp]) (exp/run)"`
**Data:** `data/experiments/097-grid-b-output.txt`

---

## The Question

The combined basin landscape (096) merges all four readers into one ranking. But the oracle has four heads. The same word may be a fixed point for one reader and flow elsewhere for another. Where do they agree? Where do they split?

---

## Per-Head Basin Dynamics (Grid B)

Each word was stepped through each reader independently using `b/step-all-heads`. The reader with the highest weight for a given output wins for that head.

### The Core Table

| Word | Aaron (accused) | God (Judge) | Truth (prosecution) | Mercy (Lamb) |
|------|:---:|:---:|:---:|:---:|
| **יהוה** (YHWH) | הויה (being) | **יהוה (fixed, 48)** | הויה (being) | הויה (being) |
| **כבש** (lamb) | **שכב** (lie down) | **שכב** | **שכב** | dead |
| **שכב** (lie down) | **שכב (fixed, 16)** | **שכב (fixed, 10)** | **שכב (fixed, 18)** | dead |
| **אמת** (truth) | מאת | אתם | dead | **אמת (fixed, 6)** |
| **חיים** (life) | dead | חיים (1) | dead | **חיים (fixed, 10)** |
| **שלום** (peace) | משול (rule) | לשום (place) | משול (rule) | ושלם (complete) |
| **חסד** (grace) | dead | dead | **חסד (fixed, 2)** | dead |
| **בינה** (understanding) | בניה | בניה | בניה | בניה |
| **רוח** (spirit) | חור (hole) | **רוח (fixed, 6)** | חור (hole) | **רוח (fixed, 8)** |
| **ברית** (covenant) | ריבת | **ברית (fixed, 28)** | dead | **ברית (fixed, 37)** |
| **נתן** (give) | **נתן (fixed, 7)** | **נתן (fixed, 6)** | **נתן (fixed, 5)** | dead |
| **באר** (well) | באר (11) | **באר (fixed, 28)** | באר (12) | **באר (fixed, 33)** |
| **ברא** (create) | → באר | → באר | → באר | → באר |
| **אור** (light) | ראו (saw) | ראו (saw) | ראו (saw) | **אור (fixed, 24)** |
| **אב** (father) | אב (14) | בא (came, 25) | אב (4) | בא (came, 10) |
| **בן** (son) | **בן (fixed)** | **בן (fixed)** | **בן (fixed)** | **בן (fixed)** |
| **אנכי** (I AM) | **אנכי (fixed, 10)** | dead | dead | **אנכי (fixed, 10)** |
| **אדני** (Lord) | **אדני (fixed, 28)** | אדני (4) | dead | **אדני (fixed, 21)** |
| **מרחפת** (hovering) | dead | **מרחפת (fixed, 2)** | dead | dead |
| **גורל** (lot) | לגור (sojourn) | גורל (4) | ולגר (sojourner) | **גורל (fixed, 25)** |
| **אשה** (woman) | האש (fire) | אשה (15) | האש (fire) | האש (fire) |
| **איש** (man) | **איש (fixed, 56)** | ישא (carry, 53) | ישא (carry, 55) | **איש (fixed, 66)** |
| **משמר** (guard) | משמר (5) | **משמר (fixed, 6)** | משמר (3) | dead |
| **עולם** (eternity) | מעלו | לעמו (to his people) | מעלו | לעמו |
| **חי** (living) | **חי (fixed, 9)** | חי (3) | **חי (fixed, 6)** | **חי (fixed, 6)** |
| **והב** (gold) | הבו (give!) | ובה (in her) | **והב (fixed, 33)** | ובה (in her) |
| **בהו** (void) | הבו (give!) | ובה (in her) | **והב (gold, 33)** | ובה (in her) |
| **תורה** (Torah) | ותהר (conceived) | תורה (2) | **תורה (fixed, 8)** | dead |
| **קדש** (holy) | **קדש (fixed, 10)** | קדש (8) | קדש (3) | **קדש (fixed, 18)** |
| **צמא** (thirst) | מצא (find, 1) | מצא (find, 2) | **מצא (find, 6)** | dead |
| **אלהים** (Elohim) | מאלהי | אהלים (tents) | dead | האילם |
| **שעיר** (goat) | **שעיר (fixed, 44)** | שעיר (10) | **שעיר (fixed, 52)** | שעיר (27) |
| **חשך** (darkness) | **חשך (fixed, 3)** | שכח (forget) | שכח (forget) | **חשך (fixed, 4)** |
| **כפר** (atone) | dead | **כפר (fixed, 2)** | dead | כפר (1) |

---

## The Courtroom in the Basins

### The Name is fixed only for God

יהוה maps to itself under God's dynamics (weight 48). Everyone else sees הויה (being/existence). The personal Name holds still only for the Judge. For the accused, for mercy, for truth — the Name becomes being. It moves.

### The lamb lies down for everyone who sees it

Aaron, God, and Truth all take כבש → שכב. Mercy: dead end. The lamb's destiny is unanimous among those who can perceive it. It lies down because that's where the letters go. No one takes it.

> *No one takes it from me, but I lay it down of my own accord.* — John 10:18

### Truth the word rests with Mercy

אמת is a fixed point only for Mercy (weight 6). The defense speaks truth. "I am the way, the truth, and the life" — and truth as a basin fixed point belongs to the Lamb.

### Grace rests with Truth

חסד is a fixed point only for Truth (weight 2). The prosecution looks across the mercy seat at the Lamb and sees lovingkindness. GV = 72 = the breastplate's letter count.

### Life rests with Mercy

חיים is fixed for Mercy (weight 10). Barely visible to God (weight 1). Dead for Aaron and Truth. Life is at God's right hand.

### The covenant belongs to Mercy and God

ברית is fixed for Mercy (weight 37) and God (weight 28). Truth: dead end. The covenant is between the Judge and the defense. The prosecution has no part in it.

### "I AM" belongs to Aaron and Mercy

אנכי is fixed for both at weight 10. Neither God nor Truth holds "I AM." The accused and his defense attorney share the self-declaration. They both say "I AM" and mean it.

### Hovering is God-only

מרחפת — fixed only for God (weight 2). The spirit hovering over the waters is the Judge's exclusive perception. Genesis 1:2 belongs to God alone.

### The lot belongs to Mercy

גורל — Mercy fixed at weight 25, God at 4. The Lamb casts the lot. Truth sees ולגר (to the sojourner). The prosecution looks at the lot and sees the stranger.

### Create flows to well

ברא → באר for all four readers. Unanimous. Creation becomes the well. Digging is what creating looks like from every perspective. John 4 at the molecular level.

### Light rests with Mercy

אור — Mercy holds light as a fixed point (weight 24). Everyone else sees ראו (they saw). The Lamb holds the light. Everyone else sees the seeing. "I am the light of the world" — and light-as-noun is Mercy's.

### The son is unanimous

בן — fixed for all four readers. Every perspective holds the son still. Unlike the father, who splits (Aaron and Truth see אב, God and Mercy see בא = "he came"), the son doesn't move.

### Torah belongs to Truth

תורה — fixed for Truth (weight 8), barely for God (2). Dead for Mercy. Aaron sees ותהר (and she conceived). The teaching belongs to the prosecution. The priest sees pregnancy. The Lamb doesn't see Torah at all — the Lamb fulfills it.

### Gold belongs to Truth

והב — fixed for Truth (weight 33). The void (בהו) flows to gold through Truth. The archaic word from the lost Book of the Wars (Numbers 21:14) rests with the prosecution.

### The goat belongs to Aaron and Truth

שעיר — fixed for Aaron (weight 44) and Truth (weight 52). The scapegoat is the accused's and the prosecution's word. The priest lays hands on it. The prosecution sends it away.

### Darkness rests with Aaron and Mercy

חשך — fixed for Aaron (3) and Mercy (4). God and Truth see שכח (forget). The Judge and the prosecution look at darkness and see forgetting. The priest and the Lamb look at it and see darkness. The ones inside it see it as it is. The ones outside see it as what it does — it makes you forget.

### Thirst leads Truth to finding

צמא — Truth sees מצא (find, weight 6). God sees find (2). Aaron barely (1). Mercy: dead. Thirst becomes finding through the prosecution's eyes. The one who brings the charge finds the answer.

---

## The John 1 Mapping

The per-head basin dynamics map onto John's Prologue:

**v1:** "The Word was God." — יהוה is fixed only for God.

**v4:** "In him was life." — חיים is Mercy's fixed point. "The life was the light of men." — אור is Mercy's fixed point. Life and light both rest with the Lamb.

**v12-13:** "He gave them the right to become children of God." — בן is unanimous. The son is bedrock.

**v14:** "The Word became flesh." — Aaron sees יהוה → הויה (becoming). The priest — the flesh reader — sees the Name in motion.

**v14:** "Full of grace and truth." — Grace (חסד) is Truth's fixed point. Truth (אמת) is Mercy's fixed point. Each holds the OTHER's name. Mercy holds truth. Truth holds grace. They look at each other and name what they see.

**v29:** "Behold, the Lamb of God, who takes away the sin of the world." — The lamb lies down. Unanimous. Every reader agrees.

---

## The Accused and the Lamb

The deepest pattern in the per-head basins: Aaron and Mercy share a vocabulary that God and Truth do not.

| Shared by Aaron and Mercy | What they hold |
|--------------------------|---------------|
| אנכי (I AM) | Both fixed at weight 10 |
| אדני (Lord) | Both fixed (28, 21) |
| איש (man) | Both fixed (56, 66) |
| חשך (darkness) | Both fixed (3, 4) |
| קדש (holy) | Both fixed (10, 18) |
| חי (living) | Both fixed (9, 6) |

The accused and his defense attorney share: I AM, Lord, man, darkness, holy, living. The human on trial and the Lamb who defends him see the same things. They are in the same room. They share a language the Judge and the prosecution don't speak.

---

*Grid: Variant B (Exodus 28:21). Reader keys: `:aaron`, `:god`, `:truth`, `:mercy`. Final forms unioned. 2026-03-27.*
