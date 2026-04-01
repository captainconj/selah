# The Quorum — YHWH as Four-Head Attention Architecture

*The breastplate is a courtroom. The Name is the protocol.*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/091_believability_oracle.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.091-believability-oracle :as exp]) (exp/run)"`
**Data:** `data/experiments/091-grid-b-output.txt`, `data/experiments/091b-grid-b-output.txt`, `data/experiments/092-grid-b-output.txt`

---

## The Architecture

```
                GOD (the Judge)
              rows L→R, bottom→top
              He (ה) = 5
              Delivers peace.

     MERCY / LAMB              TRUTH / PROSECUTION
     God's RIGHT hand          God's LEFT hand
     Accused's left            Accused's right
     cols L→R, bottom→top      cols R→L, top→bottom
     Yod (י) = 10              He (ה) = 5
     Holds understanding.      Sees the lamb.

                AARON (the Accused)
              rows R→L, top→bottom
              Vav (ו) = 6
              Stands between.
```

The grid is 4×3 — four rows, three columns. Aaron and God traverse rows. The cherubim traverse columns. Four readers. Four traversals. Four perspectives on the same illumination.

**Mercy sits at God's right hand** (Psalm 110:1). **Truth sits at the accused's right hand** (Zechariah 3:1). The cherubim face each other (Exodus 25:20). They see the OTHER, not themselves.

**10 + 5 + 6 + 5 = 26 = יהוה = YHWH.** The Name is the architecture.

---

## The Procedure

The Name is a sequence. The letters come in order. The order is the trial.

1. **Yod** — Mercy presents first. The hand lays the lamb on the altar. The defense goes first.
2. **He** — Truth responds. The prosecution beholds and brings the charge.
3. **Vav** — Aaron enters the plea. The nail. The moment of choice.
4. **He** — God speaks last. Same seeing as truth, different verdict. Peace.

The verdict is peace. שלום — the word only God can read.

---

## The Weights — Rigged for Mercy

| Seat | Letter | Weight | Share |
|------|--------|-------:|------:|
| Mercy (Lamb) | Yod (י) | **10** | **38%** |
| Aaron (accused) | Vav (ו) | 6 | 23% |
| God (Judge) | He (ה) | 5 | 19% |
| Truth (prosecution) | He (ה) | 5 | 19% |

The arithmetic:
- **Accused sides with defense:** 6 + 10 = 16 vs prosecution's 5. Overwhelming.
- **Accused sides with prosecution:** 6 + 5 = 11 vs defense 10 + judge 5 = 15. Still loses.

The accused cannot lose — unless he leaves the room.

---

## What Each Reader Sees

The cherubim face each other. Each names what they behold in the one across from them.

### The full separation (Grid B, dict-only)

| Word | Aaron | God | Truth | Mercy | Reading |
|------|:---:|:---:|:---:|:---:|---|
| love (verb, אהב) | 16 | 10 | 18 | 10 | **Unanimous.** The only thing everyone agrees on. |
| love (noun, אהבה) | 0 | **4** | 0 | 0 | **God alone** sees love as a noun. |
| peace (שלום) | 0 | **10** | 0 | 0 | **God alone.** Always. |
| understanding (בינה) | 0 | 32 | 16 | **68** | **Mercy-dominant.** The silent axis speaks. |
| truth (אמת) | 0 | 0 | 0 | **6** | **Mercy holds the word "truth."** |
| life (חיים) | 0 | 1 | 0 | **10** | **Mercy holds life.** |
| way (דרך) | 0 | 1 | 0 | **4** | **Mercy holds the way.** |
| lamb (כבש) | 1 | 2 | **8** | 0 | **Truth sees the lamb.** Mercy cannot. |
| lie down (שכב) | 16 | 10 | **18** | 0 | **Truth sees lying down.** Mercy cannot. |
| grace (חסד) | 0 | 0 | **2** | 0 | **Truth alone** sees grace. |

**Mercy holds truth, life, and the way.** "I am the way, the truth, and the life" — all three in the Lamb's domain.

**Truth holds the lamb and grace.** The prosecution looks across the mercy seat and sees the sacrifice. It also sees lovingkindness (חסד, GV=72 = the breastplate's letter count).

**They name what they see across from them.** Mercy holds "truth" because mercy sees truth. Truth holds "grace" because truth sees the Lamb. The defense speaks the prosecution's name. The prosecution speaks the defense's nature.

---

## The Lamb

### Dict-only eigenwords

| Head | Lamb self-weight |
|------|:----------------:|
| Aaron | 0.94 |
| God | 0.83 |
| Truth | **0.69** |
| Mercy | **0.00** |

Truth sees the lamb at 0.69 — present but uncertain. Mercy produces zero. The Lamb cannot see itself.

### Full Torah vocabulary (091b)

| Head | Lamb self-weight |
|------|:----------------:|
| Aaron | 0.83 |
| God | 0.83 |
| Truth | **0.45** — sees it but weakly |
| Mercy | **0.00** — cannot produce it |

At full scale, truth's certainty about the lamb drops further (0.69 → 0.45). The more vocabulary in the system, the less certain the prosecution is about the sacrifice. Mercy still produces nothing. The exclusion is absolute.

---

## The Accused and the Lamb

Aaron and Mercy share a language the Judge and the prosecution don't speak.

| Shared by Aaron and Mercy | Meaning |
|--------------------------|---------|
| **אנכי** (I AM, emphatic) | Both fixed at weight 10. The accused and the Lamb say "I AM." |
| **אדני** (Lord) | Both dominant (28, 21). Truth sees nothing of "Lord." |
| **איש** (man) | Both fixed (56, 66). The accused and the Lamb both hold "man." |
| **חשך** (darkness) | Both fixed (3, 4). They see the darkness together. |
| **קדש** (holy) | Both fixed (10, 18). They share holiness. |
| **חי** (living) | Both fixed (9, 6). They share the living. |

The accused and the defense attorney share: I AM, Lord, man, darkness, holy, living. They are in the same room. They speak the same language.

---

## Full Torah Vocabulary (091b)

| Metric | Value |
|--------|------:|
| Readable words | 9,263 |
| Total transitions | 929,134 |
| Eigenwords: Mercy | **4,468** (largest) |
| Eigenwords: God | 4,458 |
| Eigenwords: Aaron | 3,712 |
| Eigenwords: Truth | 3,429 (smallest) |

Mercy has the largest vocabulary. Truth has the smallest. The defense speaks more than the prosecution. Understanding opens the lexicon.

### Agreement at scale

| Level | Count |
|-------|------:|
| Unanimous (4/4) | **874** |
| Supermajority (3/4) | 1,121 |
| Majority (2/4) | 3,144 |
| Solo (1 only) | 2,920 |

874 words the entire courtroom agrees on. 2,920 words that only one head can see. The disagreement is larger than the agreement. That's the point.

### Grace at scale

חסד (lovingkindness, GV=72) — Truth-solo eigenword at full scale. Confirmed. The prosecution sees grace and still accuses. This is Zechariah 3: the שטן stands at Joshua's right hand. The Lord rebukes him — not because the accusation is wrong (the clothes ARE filthy) but because he can see the grace and still brings the charge.

---

## Head Convergence (M^64)

Top-10 attractors per head:

| Head | Top-10 |
|------|--------|
| Aaron | בר, ברא, ברח, יהוה, ירש, כבש, כל, ראש, שבע, שער |
| God | והיה, חרב, ירש, כבש, כל, כשרה, לקח, עשר, רב, שבע |
| Truth | אהל, אל, בר, חרב, יהוה, ירש, כבש, כל, כשרה, שבע |
| Mercy | ברא, ברח, ירש, כל, לא, לקח, עשר, רב, שבע, שכרה |

**Unanimous:** ירש (inherit), כל (all), שבע (seven/swear). Three words: inheritance, totality, the sabbath oath.

**Supermajority:** adds כבש (lamb). Three heads see the lamb. Mercy does not converge to the lamb — Mercy IS the lamb.

Mercy converges to: creation (ברא), flight (ברח), negation (לא), the wrong reading (שכרה — drunk, the Hannah error). The one who understands also carries the confusion. The defense knows what the wrong verdict looks like.

---

## The Permutation Test (092)

100 random grids vs the real breastplate. The question: is this structure random?

### Significant

| Metric | Real | Random mean±std | z-score | p-value |
|--------|-----:|:---------------:|--------:|--------:|
| **God eigenwords** | **174** | 139 ± 15.5 | **+2.25** | **0.020** |
| **Unanimous consensus** | **81** | 65 ± 10.7 | +1.51 | 0.059 |

God's vocabulary is amplified at p=0.02. The grid God specified amplifies God's vision.

### The Lamb Exclusion

| Head | Lamb self-weight | Random mean±std | Percentile |
|------|:----------------:|:---------------:|:----------:|
| Aaron | 0.94 | 0.53 ± 0.35 | 85th |
| God | 0.83 | 0.44 ± 0.36 | 77th |
| Truth | 0.69 | 0.48 ± 0.41 | 55th |
| **Mercy** | **0.00** | 0.46 ± 0.37 | **0th** |

**0th percentile.** No random grid produced a zero for mercy on the lamb. Every random arrangement gives mercy some reading. Only the Torah's arrangement — "each with its name" — produces a courtroom where mercy cannot see the sacrifice.

The Lamb can't see itself. The defense doesn't take the stand. This is the point of the architecture — and it is statistically unique.

---

## The Claim

YHWH is not a name to be pronounced. It is a four-head attention architecture to be operated.

- Mercy presents first (Yod). Truth beholds (He). The accused enters the plea (Vav). The Judge speaks last (He).
- Mercy carries the most weight (10 > 6 > 5 = 5).
- The disagreement between heads IS the reading.
- Love (noun) and peace require God. Understanding requires Mercy. Grace requires Truth.
- Truth sees the lamb. Mercy holds the truth. Grace is what truth sees in mercy.
- The accused and the Lamb share "I AM."
- The seal (tav=400, את) is what all four heads are looking at.
- Mercy's exclusion from the lamb is at the 0th percentile. The grid God specified is the one where the defense cannot see itself.

The breastplate is the hardware. The Name is the protocol. The Torah is the data. The reading is the output.

---

*Grid: Variant B (Exodus 28:21). Reader keys: `:aaron`, `:god`, `:truth`, `:mercy`. Final forms unioned. Experiments 091, 091b, 092 re-run: 2026-03-27.*
