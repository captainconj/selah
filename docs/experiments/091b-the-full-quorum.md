# The Full Quorum — Every Word in the Torah Through Four Heads

*Experiment 091b — March 2, 2026*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/091_believability_oracle.clj`
**Run:** `clojure -M:dev -e "(require '[experiments.091-believability-oracle :as exp091]) (exp091/run-full)"`

---

## What We Did

Experiment 091 tested the four-head oracle on ~215 curated dictionary words. Experiment 091b removed the curation. Every word in the Torah — all 12,826 unique word forms extracted from the Westminster Leningrad Codex — was passed through the breastplate and read by all four heads.

The question: does the theological separation hold when the vocabulary expands 60×?

---

## The Numbers

| Measure | Dictionary (091) | Full Torah (091b) | Factor |
|---------|------------------|-------------------|--------|
| Input vocabulary | ~215 | 12,826 | 60× |
| Readable words | ~210 | 8,570 | 41× |
| Total transitions | ~2,000 | 725,780 | 363× |
| Matrix size | 210×210 | 8,570×8,570 | 1,666× |

4,256 words (33%) are unreadable — the oracle cannot produce them from any input. These are mostly 5+ letter words whose letter combinations don't align with any breastplate illumination pattern. The readable 8,570 form the oracle's working vocabulary.

Four transition matrices, one per head. Each 8,570×8,570. Dense. MKL backend. ~587MB each.

---

## Eigenwords Per Head

An eigenword is a fixed point: the word the oracle returns to itself with highest weight. The word that, when asked, answers itself.

| Head | Letter | Eigenwords | % of vocabulary |
|------|--------|------------|-----------------|
| Aaron (nail) | ו vav=6 | 2,649 | 30.9% |
| God (regard) | ה he=5 | 3,849 | 44.9% |
| Right cherub (hand) | י yod=10 | 3,754 | 43.8% |
| Left cherub (regard) | ה he=5 | 3,832 | 44.7% |

Aaron sees the least. God sees the most. The three who behold (both He's and Yod) see nearly the same proportion. The connector sees a third.

---

## Cross-Head Agreement

| Agreement | Count | % of eigenwords |
|-----------|-------|-----------------|
| **Unanimous** (4/4) | 642 | 8.6% |
| **Supermajority** (3/4) | 991 | 13.2% |
| **Majority** (2/4) | 2,670 | 35.6% |
| **Solo** (1/4) | 3,203 | 42.7% |
| **Total** | **7,506** | 100% |

87.6% of the readable vocabulary (7,506 of 8,570) is an eigenword in at least one head. This is high — the oracle is self-referential almost everywhere.

But unanimity is rare: only 642 words (8.6%) are fixed points in all four heads. The quorum's consensus is narrow. Most of the oracle's voice lives in disagreement — in the 3,203 solos where only one head sees a word as self-referential. **The disagreement IS the reading.**

---

## The Unanimous Core (642 words)

The words all four heads agree on. Selected translations from the 642:

**Nouns**: stone (אבן), brother (אח), ear (אזן), mother (אם), son (בן), daughter (בת), mountain (הר), wine (יין), sea (ים), heart (לב), river (נהר), bull (פר), flock (צאן), offering (קרבן), oil (שמן)

**Verbs**: eat (אכל), say (אמר), give (נתן), call/read (קרא), hear (שמע)

**Particles**: or (או), all/every (כל), for/when (כי), thus (כן), from (מן), upon (על), with/people (עם), please (נא), to him (לו), to me (לי), in him (בו), he/it (הוא)

**Numbers**: six (שש), one (אחד), many/great (רב)

**Names**: Abraham (אברהם), Lord (אדני)

**The seal**: את (aleph-tav) — the direct object marker, the first and last letters. Unanimous at dictionary scale. Unanimous at full scale.

**Notable**: living (חי), grace (חן), sons of (בני)

The unanimous vocabulary is infrastructure — the words that hold the sentence together. The nouns of kinship (brother, mother, son, daughter), the nouns of sacrifice (bull, flock, offering), the connective tissue (say, give, hear, call). This is not theology. This is grammar. The quorum agrees on how the Torah speaks, not what it says.

---

## The Solo Eigenwords — What Only One Head Sees

This is where the theology lives. Words that are fixed points in exactly one head and no others. 3,203 total.

### Mercy sees alone (yod=10, the hand):

| Word | GV | Meaning |
|------|-----|---------|
| חיים | 68 | life |
| חטא | 18 | sin |
| פדה | 89 | ransom |
| אלהים | 86 | God (Elohim) |
| חכמה | 73 | wisdom |
| חכם | 68 | wise |
| לחם | 78 | bread |
| זהב | 14 | gold |
| היום | 61 | today/the day |
| העם | 115 | the people |
| ברא | 203 | create |
| ברח | 210 | flee |

The hand sees **life, sin, ransom, God-as-Elohim, wisdom, bread, gold, the people, today**. The vocabulary of redemption — the same separation found at dictionary scale, now confirmed across the full Torah. Create and flee are Aaron solos in the dictionary model but belong to mercy at full scale — the hand that creates is the hand that extends mercy.

### Justice sees alone (he=5, left cherub):

| Word | GV | Meaning |
|------|-----|---------|
| בוא | 9 | come/enter |
| גזל | 40 | rob/seize |
| עזה | 82 | Gaza/fierce |
| נחלה | 93 | inheritance |
| מצוה | 141 | commandment |
| מצבה | 137 | pillar/monument |
| דקה | 109 | fine/thin |
| מדה | 49 | measure |

Justice sees **coming/entering, seizure, fierceness, inheritance, commandment, pillar, measure**. Instruments of judgment. Entry and assessment.

### God sees alone (he=5, the second regard):

| Word | GV | Meaning |
|------|-----|---------|
| מזבח | 57 | altar |
| גבול | 41 | border/territory |
| גדול | 43 | great |
| עלה | 105 | go up / burnt offering |
| אמן | 91 | amen/faithful |
| טמאה | 55 | unclean |
| נביאים | 113 | prophets |
| כפי | 110 | my palm |
| פיו | 96 | his mouth |
| כליל | 90 | complete/wholly |

God sees **altar, border, greatness, burnt offering, amen, uncleanness, prophets, his mouth**. The vocabulary of worship and boundary — what is offered, what is whole, what is separated, who speaks. God names the architecture of the sacred.

### Aaron sees alone (vav=6, the nail):

| Word | GV | Meaning |
|------|-----|---------|
| דגן | 57 | grain |
| חיל | 48 | strength/army |
| מלא | 71 | full/fill |
| קהל | 135 | assembly |
| קנה | 155 | acquire/reed |
| רעה | 275 | shepherd/tend |
| חנה | 63 | camp/encamp |
| ארגמן | 294 | purple (dye) |

Aaron sees **grain, strength, assembly, fullness, acquisition, shepherding, encampment, purple**. The priest sees the physical infrastructure of worship — the materials, the gatherings, the provisions. The nail connects heaven and earth through stuff.

---

## The Lamb Holds

| Head | Lamb self-weight |
|------|-----------------|
| God (he=5) | **0.7229** |
| Right/mercy (yod=10) | 0.4455 |
| Aaron (vav=6) | 0.0000 |
| Left/justice (he=5) | 0.0000 |

At dictionary scale (091): God=0.952, mercy=0.692.
At full scale (091b): God=0.723, mercy=0.446.

The absolute weights drop — more words competing for attention in a 40× larger vocabulary. But the **pattern is identical**: God sees the lamb most clearly. Mercy sees the lamb. Aaron and justice see nothing. The two He's — same letter, same value — see opposite things. One beholds the lamb at 72%. The other: zero.

---

## What Changed at Scale

### Confirmed
- **Head specialization is robust.** The four traversal directions naturally separate into distinct vocabularies even with 8,570 words. Nobody taught the geometry which head should see what.
- **The lamb asymmetry is structural.** Not an artifact of the curated dictionary.
- **את (aleph-tav) remains unanimous.** The seal holds.
- **Mercy sees the vocabulary of redemption.** Life, sin, ransom, God-as-Elohim.
- **God sees the vocabulary of worship.** Altar, offering, border, amen.
- **Justice sees instruments and measures.** Commandment, inheritance, entry.
- **Aaron sees physical infrastructure.** Grain, strength, assembly, purple.

### New at full scale
- **Create (ברא) moved to mercy.** At dictionary scale, it was Aaron's solo. At full scale, the hand claims creation. The hand that creates is the hand that redeems.
- **The people (העם) are mercy's solo.** Nobody else sees the people as a fixed point. Mercy's hand is on the people.
- **Amen (אמן, GV=91=7×13) is God's solo.** The faithful response — visible only from across the mercy seat.
- **Prophets (נביאים) are God's solo.** God sees his own messengers.
- **Assembly (קהל) is Aaron's solo.** The priest gathers. Nobody else sees the gathering as self-referential.
- **Commandment (מצוה) is justice's solo.** The left cherub — the beholder of judgment — alone holds the commandment as its fixed point.

### The shape of the quorum
- 8.6% unanimous — the grammar, the infrastructure
- 13.2% supermajority — strong consensus
- 35.6% majority — moderate agreement
- 42.7% solo — the theology, the perspective-dependent meaning

Almost half the oracle's eigenwords are seen by only one head. The quorum is mostly disagreement. This is the design. Four perspectives that see different things from the same illumination. The reading lives in the tension between them.

---

## The Architecture at Scale

The breastplate operates on the full Torah vocabulary the same way it operates on the curated dictionary. The structure doesn't collapse. It deepens.

At 210 words, you can ask: is this an artifact of our vocabulary? We chose these words. We chose theological words. Of course they separate into theological vocabularies.

At 8,570 words, that objection dies. These are ALL the words. Every conjugation, every prefix, every proper name, every particle. The oracle wasn't shown theology. It was shown Hebrew. And it separated the Hebrew into mercy, justice, priesthood, and divinity — the same separation described in Exodus.

The Tetragrammaton is a voting system. 10 + 5 + 6 + 5 = 26. Four heads, four weights, four vocabularies. The Name is the architecture of the quorum.

*selah.*
