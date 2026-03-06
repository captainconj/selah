# Experiment 101: The Map

*Which row → which base? Which letter → which amino acid? The breastplate as translation table.*

---

## The Method

**Step 1: Row → Base.** All 24 permutations of {A, C, G, U} → {Row 1, Row 2, Row 3, Row 4} were scored by Pearson correlation between grid coding positions and genetic code codon positions.

**Step 2: Watson-Crick constraint.** The antiparallel pairs from experiment 100 (Row 1+4 and Row 2+3, each 32 coding positions) must be Watson-Crick complementary base pairs.

**Step 3: Letter → Amino Acid.** For each letter, compute cosine similarity between its grid cell profile and each amino acid's codon cell profile. Greedy assignment: best-matching pair first, no repeats.

**Step 4: Verification.** Permutation test. Position-2 chemical class check. Start/stop codon analysis.

---

## Finding 1: The Row → Base Mapping

Two mappings tied for best correlation (r = 0.397):

| Mapping | Row 1 | Row 2 | Row 3 | Row 4 | r |
|---------|-------|-------|-------|-------|---|
| **1** | **A** | **C** | **G** | **U** | **0.397** |
| 2 | G | C | A | U | 0.397 |

The Watson-Crick constraint breaks the tie:

| | Mapping 1 | Mapping 2 |
|---|-----------|-----------|
| Outer pair (Row 1+4) | A + U ✓ | G + U ✗ (wobble) |
| Inner pair (Row 2+3) | C + G ✓ | C + A ✗ |

**Only mapping 1 produces Watson-Crick antiparallel pairs.**

The map:

```
         Col 1 (pos 1)     Col 2 (pos 2)      Col 3 (pos 3/wobble)
Row 1=A: Stone 1 (Abraham)  Stone 2 (Isaac)★★   Stone 3 (Reuben)
Row 2=C: Stone 4 (Simeon)   Stone 5 (Judah)     Stone 6 (Dan)★
Row 3=G: Stone 7 (Gad)★     Stone 8 (Issachar)★ Stone 9 (Zebulun)★
Row 4=U: Stone 10 (Joseph)★ Stone 11 (Benjamin)★ Stone 12 (Yeshurun)

★ = singleton present. ★★ = two singletons.
```

The antiparallel pairs are the double helix:
- **A + U** (outer): purines + pyrimidines, 2 hydrogen bonds
- **C + G** (inner): pyrimidines + purines, 3 hydrogen bonds

---

## Finding 2: The Letter → Amino Acid Assignment

Greedy profile matching (p < 0.0001, 0/10,000 random permutations matched):

| Letter | GV | Freq | → | Amino Acid | Codons | Class |
|--------|-----|------|---|------------|--------|-------|
| **א** | **1** | **3** | → | **(unmapped)** | **—** | **beyond** |
| ב | 2 | 6 | → | Met | 1 | special |
| ג | 3 | 1 | → | Asp | 2 | charged |
| ד | 4 | 3 | → | Ala | 4 | small |
| ה | 5 | 3 | → | Thr | 4 | small |
| ו | 6 | 7 | → | Pro | 4 | small |
| ז | 7 | 1 | → | Tyr | 2 | polar |
| ח | 8 | 1 | → | Asn | 2 | polar |
| ט | 9 | 1 | → | Val | 4 | hydrophobic |
| י | 10 | 11 | → | Ser | 6 | small |
| כ | 20 | 1 | → | Trp | 1 | special |
| ל | 30 | 3 | → | Glu | 2 | charged |
| מ | 40 | 3 | → | Ile | 3 | hydrophobic |
| נ | 50 | 8 | → | Leu | 6 | hydrophobic |
| ס | 60 | 1 | → | Stop | 3 | stop |
| ע | 70 | 2 | → | His | 2 | charged |
| פ | 80 | 2 | → | Cys | 2 | special |
| צ | 90 | 1 | → | Gln | 2 | polar |
| ק | 100 | 2 | → | Lys | 2 | charged |
| ר | 200 | 5 | → | Arg | 6 | charged |
| ש | 300 | 6 | → | Gly | 4 | small |
| ת | 400 | 1 | → | Phe | 2 | hydrophobic |

**22 letters. 21 coded. Aleph is unmapped — the only letter that doesn't fit any amino acid.**

---

## Finding 3: Aleph Is Beyond the Code

There are 20 standard amino acids + Stop = 21 coded entities. There are 22 Hebrew letters. One letter must be left over. The greedy algorithm — matching by structural similarity — leaves **א (aleph)** unmapped. Not by arbitrary choice, but because aleph has the **worst similarity score** of any letter (best match = 0.548, the lowest).

Aleph has no sound of its own. It takes whatever vowel is given to it. GV = 1.

It begins: **אלהים** (God), **אחד** (one), **אני** (I), **אמת** (truth), **אהבה** (love).

It is not coded. It IS.

**את (aleph-tav, the sign):** The first letter (unmapped, beyond) + the last letter (Phe, aromatic, grounded). The sign spans the entire code — from what is beyond to what is embodied. And את illuminates columns 1 and 3 only, **skipping the discriminator column**. The boundary marker is not content. It frames the code.

---

## Finding 4: Position-2 Verification

The second codon position determines amino acid chemical class. Each row's column-2 letters should map to the correct class:

| Row | Base at pos 2 | Expected AAs | Assigned AAs | Score |
|-----|---------------|-------------|-------------|-------|
| 1 | A | Asn,Asp,Gln,Glu,His,Lys,Stop,Tyr | **Asn,Gln,His,Lys** + Ser | **4/5** |
| 2 | C | Ala,Pro,Ser,Thr | **Ala,Pro,Ser,Thr** | **4/4** ★ |
| 3 | G | Arg,Cys,Gly,Ser,Stop,Trp | **Arg,Gly,Ser,Trp** | **4/4** ★ |
| 4 | U | Ile,Leu,Met,Phe,Val | **Ile,Leu,Met,Val** + Gly,Ser | **4/6** |

**Rows 2 and 3 are perfect.** Every miss comes from backbone letters (י→Ser, ש→Gly) that span multiple rows — structural letters that cross boundaries, like the backbone of DNA.

The one amino acid that crosses pos-2 boundaries is **Ser** (codons in both C-class and G-class). It maps to **Yod (י)** — the backbone letter that appears in every row. The bridge amino acid maps to the bridge letter.

---

## Finding 5: The Start Codon

**AUG (Met)** — the universal start codon — selects cells [A,1], [U,2], [G,3] = stones **1 (Abraham), 11 (Benjamin), 9 (Zebulun)**.

**Bet (ב)** maps to Met. ב is the FIRST letter of the Torah (בראשית = "in the beginning"). Met is the first amino acid of every protein. **The letter that begins → the amino acid that begins.**

ב is the only non-backbone letter that covers **all three AUG cells**:
- [1,1] = Stone 1 (Abraham) ✓
- [4,2] = Stone 11 (Benjamin) ✓
- [3,3] = Stone 9 (Zebulun) ✓

The start codon touches the first stone (Abraham = the first patriarch) and Benjamin (= "son of my right hand").

GV of ב = 2. The house. The dwelling. "In the beginning" starts at the house.

---

## Finding 6: The Stop at Joseph

**Samekh (ס, GV=60)** maps to **Stop**. It is the sole singleton on stone 10 (Joseph), row 4, column 1.

Joseph who was "dead" to his brothers and then alive. The stop codon marks the end of translation — but the end is necessary for the protein to fold, to become functional, to live.

Every stop codon touches Joseph's stone:
- **UAA** → stones [10, 2, 3] = Joseph, Isaac, Reuben
- **UAG** → stones [10, 2, 9] = Joseph, Isaac, Zebulun
- **UGA** → stones [10, 8, 3] = Joseph, Issachar, Reuben

The stop signal always passes through the one who was cast into the pit and raised out.

---

## Finding 7: The Backbone Is the Name

Three letters appear in all four rows: **ו** (nail), **י** (hand), **נ** (nun/fish). They occupy **26 positions** = YHWH.

| Backbone | GV | Freq | → | AA | Codons | Role |
|----------|-----|------|---|-----|--------|------|
| ו (nail) | 6 | 7 | → | Pro | 4 | Bends the chain (proline introduces kinks) |
| י (hand) | 10 | 11 | → | Ser | 6 | Bridges worlds (only AA in 2 pos-2 classes) |
| נ (fish) | 50 | 8 | → | Leu | 6 | Core structure (most hydrophobic backbone) |

The nail bends the chain. The hand bridges the divide. The fish holds the hydrophobic core.

Pro + Ser + Leu = the three most common amino acids in structural proteins. They ARE the backbone of life. And they are spelled by the Name.

---

## Finding 8: The Guard Row

Row 3 (G = guanine): Gad, Issachar, Zebulun. The only row with a singleton in every column.

| Stone | Tribe | Singleton | → | AA | Note |
|-------|-------|-----------|---|-----|------|
| 7 | Gad | **ג** (3) | → | Asp | Charged. The watcher. |
| 8 | Issachar | **כ** (20) | → | **Trp** | Rarest AA (1 codon). "There is reward." |
| 9 | Zebulun | **ז** (7) | → | Tyr | Polar. GV=7 at pos 3 (wobble). |

**Trp (tryptophan)** — the rarest amino acid, coded by a single codon (UGG) — sits at the **guard row center** (the most regulated position). The most unique amino acid at the most guarded position. Issachar = "there is reward." The reward is rarity itself.

---

## Finding 9: The Divides

The biblical pairs from the divides (doc 21) encode the base pairs:

**The Antiparallel = the Double Helix:**
- Row 1 (A) + Row 4 (U) = **outer pair** = the loved: Abraham/Isaac/Reuben + Joseph/Benjamin/Yeshurun
- Row 2 (C) + Row 3 (G) = **inner pair** = the structure: Simeon/Judah/Dan + Gad/Issachar/Zebulun

**The Matriarchal Divide:**
- Leah's sons fill rows 1-3 (A, C, G) — the flow fills the structure
- Rachel's sons fill row 4 (U, the simplest) — the fixed point at the boundary

**Isaac at the Discriminator:**
- Stone 2 at [A, pos 2] = adenine at the position that determines chemical class
- Two singletons (ח→Asn, צ→Gln): the sacrifice carries the most information
- The bound one sits where the code is most discriminating

**Joseph at the Stop:**
- Stone 10 at [U, pos 1] = the beginning of every stop codon
- The one who "died" carries the end signal
- Every stop codon passes through his stone

**Bet = the Beginning:**
- ב → Met. The first letter of Torah → the first amino acid
- The house is where translation begins

**Aleph = Beyond:**
- א → (nothing). The silent letter is not coded
- It begins God, One, Truth, Love — words that are not products of the code but its source

---

## The Scores

| Metric | Value |
|--------|-------|
| Profile correlation (row→base) | 0.397 |
| Assignment score (total cosine similarity) | 13.888 |
| Permutation test (letter→AA) | p < 0.0001 |
| Frequency correlation (letter freq vs codon count) | r = 0.685 |
| Position-2 verification (rows 2+3) | 8/8 perfect |
| Watson-Crick antiparallel | ✓ (unique) |
| Start codon (ב covers all AUG cells) | ✓ |
| Unmapped letter = silent letter | ✓ (א) |

---

## The Map

```
         Col 1 (pos 1)     Col 2 (pos 2)      Col 3 (wobble)
         ─────────────     ─────────────      ──────────────
Row 1=A  Abraham            Isaac/Jacob         Reuben
         א ב ר ה ם י        צ ח ק י ע ק        ב ר א ו ב ן
         · M R T · S        Q N K S H K        M R · P M L

Row 2=C  Simeon              Judah               Dan
         ש מ ע ו ן ל        ו י י ה ו ד        ה ד ן נ פ ת
         G I H P L E        P S S T P A        T A L L C F

Row 3=G  Gad                 Issachar            Zebulun
         ל י ג ד א ש        ר י ש ש כ ר        ז ב ו ל ן י
         E S D A · G        R S G G W R        Y M P E L S

Row 4=U  Joseph              Benjamin            Yeshurun
         ו ס ף ב נ י        מ י ן ש ב ט        י י ש ר ו ן
         P * C M L S        I S L G M V        S S G R P L

· = Aleph (unmapped)    * = Stop
```

Where each letter becomes its one-letter amino acid code:
- M=Met, R=Arg, T=Thr, S=Ser, Q=Gln, N=Asn, K=Lys, H=His
- G=Gly, I=Ile, P=Pro, L=Leu, E=Glu, A=Ala, D=Asp, C=Cys
- F=Phe, W=Trp, Y=Tyr, V=Val, *=Stop, ·=beyond

The 72 letters of the breastplate. Translated.

---

## Finding 10: The Protein

Read continuously from stone 1 to stone 12, the breastplate encodes a protein:

```
·MRTISQNKSHKMR·PMLGIHPLEPSSTPATALLCFESDA·GRSGGWRYMPELSP*CMLSISLGMVSSGRPL
```

### The Reading Frame

| Region | Positions | Content |
|--------|-----------|---------|
| **· (silence)** | 1 | Aleph on Abraham. Before the beginning. |
| **THE PROTEIN** | 2–54 | **53 amino acids** (51 residues + 2 aleph gaps) |
| **\* (stop)** | 55 | Samekh on Joseph. The end. |
| **3' UTR** | 56–72 | 17 residues. Untranslated. Regulatory. |

**The open reading frame is 53 amino acids long.**

- 53 weekly Torah portions (parashot)
- 53 = the garden sum (experiment 078, Fibonacci staircase)
- 53 = F(9), the 9th Fibonacci number
- 53-EDO tunes the garden (spy report: music)
- 53 is prime

The Torah is read in 53 portions. The protein is 53 amino acids. The garden is tuned to 53. The code, the reading, and the tuning share the same number.

### The First Two Positions

Position 1: **·** (aleph, silence, God)
Position 2: **M** (bet, Met, beginning)

**In the beginning, God.** Genesis 1:1. The aleph before the bet. The silence before the word.

### The Three Alephs

Three gaps in the protein — positions where aleph (the uncoded) interrupts the sequence:

| Position | Stone | Tribe | Note |
|----------|-------|-------|------|
| 1 | 1 | Abraham | Before the start. The silence before the word. |
| 15 | 3 | Reuben | The firstborn who lost his place. |
| 41 | 7 | Gad | The guard. 41 = Judah's stone GV. |

The gaps between alephs: **14** and **26**. David and YHWH.

14 = דוד (David) = ואהב ("and he loved"). 26 = יהוה (YHWH). The silence in the protein is spaced by the beloved and the Name.

### The Protein by Stone

| Stone | Tribe | Sequence | Amino Acids | Character |
|-------|-------|----------|-------------|-----------|
| 1 | Abraham | ·MRTIS | ·-Met-Arg-Thr-Ile-Ser | Origin. Start after silence. |
| 2 | Isaac/Jacob | QNKSHK | Gln-Asn-Lys-Ser-His-Lys | Charged, polar. The sacrifice. |
| 3 | Reuben | MR·PML | Met-Arg-·-Pro-Met-Leu | Restart after gap. Lost firstborn. |
| 4 | Simeon | GIHPLE | Gly-Ile-His-Pro-Leu-Glu | Mixed. Flexible. |
| 5 | **Judah** | **PSSTPA** | **Pro-Ser-Ser-Thr-Pro-Ala** | **ALL small. The king is the hinge.** |
| 6 | Dan | TALLCF | Thr-Ala-Leu-Leu-Cys-Phe | Hydrophobic core. Judgment buried deep. |
| 7 | Gad | ESDA·G | Glu-Ser-Asp-Ala-·-Gly | Charged, interrupted by silence. The guard. |
| 8 | Issachar | RSGGWR | Arg-Ser-Gly-Gly-Trp-Arg | Contains Trp — the rarest. Reward. |
| 9 | Zebulun | YMPELS | Tyr-Met-Pro-Glu-Leu-Ser | Diverse. The dwelling. |
| 10 | Joseph | P\* | Pro-Stop | A bend, then silence. Cast into the pit. |
| 11 | Benjamin | ISLGMV | Ile-Ser-Leu-Gly-Met-Val | Untranslated. Right-hand son. |
| 12 | Yeshurun | SSGRPL | Ser-Ser-Gly-Arg-Pro-Leu | Untranslated. The upright. |

**Judah (PSSTPA)** — every amino acid is small. Pro, Ser, Ser, Thr, Pro, Ala. Stone 5 had the lowest GV (41), built entirely from backbone letters (ו, י, ה, ד). Now translated: pure flexibility. The king is not heavy — the king is the joint, the hinge, the place where the chain can turn. The structure bends at Judah.

**Joseph (P\*)** — only two positions before the stop: a bend (Pro), then silence. The son who was sold contributes one turn to the protein and then ends translation. But the end is necessary — without the stop, the protein never folds, never functions, never lives. Joseph's death completes the chain.

### After the Stop

Positions 56–72: **CMLSISLGMVSSGRPL**

| Stone | Tribe | Sequence |
|-------|-------|----------|
| 10 (rest) | Joseph | CMLS |
| 11 | Benjamin | ISLGMV |
| 12 | Yeshurun | SSGRPL |

Rachel's sons + the closing. In biology, the 3' UTR (untranslated region) after the stop codon is **regulatory** — it controls stability, localization, and translation efficiency. It shapes the protein's fate without being part of the protein itself.

Rachel was the beloved wife. Her sons are beyond the reading frame. Like grace — present, shaping, but never itself translated. The breastplate cannot name grace (חסד = unanimous dead end). And Rachel's children are not part of the protein. They regulate from beyond.

*selah.*
