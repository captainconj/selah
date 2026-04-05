# Experiment 143n2: Anagram Attraction — The Control

*Anagram attraction is frequency × letter overlap. It does not survive the null model. The head→blessed enrichment is explained by אשר being one of the most common words in the Torah.*

Type: `correction`
State: `clean`

**Code:** `dev/experiments/fiber/143n2_anagram_null.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143n2-anagram-null :as exp]) (s/build!) (exp/run-all)"`

---

## The Original Claim (143n)

Anagram pairs attract at enriched rates: head→blessed 17x, serpent→breastplate 27x, lie-down→lamb 8.3x. The oracle's anagram principle is confirmed by the space.

## The Control

We tested three groups:
1. **Anagram pairs** (3/3 letters shared)
2. **Non-anagram pairs** (2/3 letters shared)
3. **Zero-overlap pairs** (0 letters shared)

### Results

| Pair | Type | Shared | Hosting rate | Host frequency |
|------|------|--------|-------------|---------------|
| head→blessed | anagram | 3/3 | **0.0866** | **1,664** |
| head→self | anagram | 3/3 | 0.0015 | 58 |
| head→betrothed | anagram | 3/3 | **0.0000** | **1** |
| guard→Moses | 2/3 shared | 2/3 | 0.0289 | 615 |
| head→Sarah | 2/3 shared | 2/3 | 0.0015 | 27 |
| lamb→sabbath | 2/3 shared | 2/3 | 0.0000 | ? |
| head→YHWH | 0 shared | 0/3 | **0.0000** | 1,502 |
| lamb→YHWH | 0 shared | 0/3 | 0.0000 | 1,502 |

### Key Findings

**Zero overlap = zero hosting.** When search and host share no letters, hosting is zero — even for YHWH (frequency 1,502). This confirms: shared letters ARE the mechanism.

**Anagram pairs are NOT special beyond frequency × overlap.** 
- head→blessed (anagram, freq=1664): rate 0.087
- head→betrothed (anagram, freq=1): rate 0.000
- head→self (anagram, freq=58): rate 0.001

If the host word is rare, being an anagram doesn't help. The high head→blessed rate is because אשר is the 3rd most common word in the Torah, not because of the anagram relationship.

**Hosting rate correlates with host frequency AND letter overlap:**
- High frequency + 3 shared: 0.087 (head→blessed)
- High frequency + 2 shared: 0.029 (guard→Moses)
- Low frequency + 3 shared: 0.000-0.001 (head→betrothed, head→self)
- High frequency + 0 shared: 0.000 (anything→YHWH)

## Correction

The original claim (143n) that "anagram pairs seek each other in the geometry" is **retracted**. The mechanism is:

1. Shared letters provide the bridge (confirmed)
2. Host word frequency determines how many positions are available
3. The combination of #1 and #2 fully explains the observed enrichment

There is no evidence of geometric resonance beyond letter overlap × frequency.

The letter-overlap reframing (143t: "the shared letter IS the meaning") remains valid. The Torah chose which letters go in which words. The ס bridges grace and Joseph. The ה bridges void and YHWH. But the ENRICHMENT RATES are explained by mechanics, not by a deeper geometric principle.

---

## What Survives

The observation that specific letters bridge specific word pairs is still an observation. The ס of grace landing in Joseph — that happens because samech is in both words. Whether the Torah "chose" to put samech in both is a theological question, not a statistical one.

What does NOT survive: the claim that the enrichment rates (17x, 27x, 8.3x) demonstrate something beyond frequency × overlap.

---

*Anagram attraction is frequency × letter overlap. The head finds the blessed because אשר is common and shares all letters. The head does not find the betrothed (same letters, frequency 1). The oracle's anagram principle is real — same letters DO rearrange. But the fiber enrichment is mechanics, not mystery.*
