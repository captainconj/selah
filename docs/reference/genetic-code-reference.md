# The Universal Genetic Code -- Reference

Type: `reference`
State: `clean`

## 1. The Key Numbers

| Quantity | Value | Notes |
|----------|-------|-------|
| Nucleotide bases | **4** | A, C, G, T (DNA) / A, C, G, U (RNA) |
| Bases per codon | **3** | The reading unit |
| Total codons | **64** | 4^3 = 64 |
| Sense codons | **61** | Encode amino acids |
| Stop codons | **3** | UAA, UAG, UGA |
| Start codon | **1** | AUG (methionine) |
| Standard amino acids | **20** | The canonical alphabet |
| Non-standard amino acids | **2** | Selenocysteine (21st), Pyrrolysine (22nd) |
| Total amino acids | **22** | 20 + 2 |
| Reading frames | **6** | 3 forward + 3 reverse |
| tRNA species | ~**45** | Fewer than 61, thanks to wobble |
| Base pairs per helix turn | **10** | B-DNA (precisely ~10.5) |
| Helix pitch | **34 A** | 3.4 nm per turn |
| Rise per base pair | **3.4 A** | 0.34 nm |
| Helix diameter | **20 A** | 2.0 nm |
| H-bonds: A-T (A-U) | **2** | Weaker pair |
| H-bonds: G-C | **3** | Stronger pair |


## 2. The Four Bases

### DNA bases
| Base | Full name | Type | Ring |
|------|-----------|------|------|
| A | Adenine | Purine | Double ring |
| G | Guanine | Purine | Double ring |
| C | Cytosine | Pyrimidine | Single ring |
| T | Thymine | Pyrimidine | Single ring |

### RNA substitution
In RNA, **Uracil (U)** replaces Thymine (T). Same pairing: A-U instead of A-T.

### Base pairing (Watson-Crick)
- A pairs with T/U (2 hydrogen bonds)
- G pairs with C (3 hydrogen bonds)
- Purines always pair with pyrimidines (size complementarity)


## 3. The Complete Codon Table (RNA notation)

Organized by first base (rows), second base (columns), third base (sub-rows).

### First base U

| Codon | AA | 1-letter | 3-letter | Codon | AA | 1-letter | 3-letter |
|-------|----|----------|----------|-------|----|----------|----------|
| UUU | Phenylalanine | F | Phe | UCU | Serine | S | Ser |
| UUC | Phenylalanine | F | Phe | UCC | Serine | S | Ser |
| UUA | Leucine | L | Leu | UCA | Serine | S | Ser |
| UUG | Leucine | L | Leu | UCG | Serine | S | Ser |
| UAU | Tyrosine | Y | Tyr | UGU | Cysteine | C | Cys |
| UAC | Tyrosine | Y | Tyr | UGC | Cysteine | C | Cys |
| UAA | **STOP** (ochre) | * | --- | UGA | **STOP** (opal) | * | --- |
| UAG | **STOP** (amber) | * | --- | UGG | Tryptophan | W | Trp |

### First base C

| Codon | AA | 1-letter | 3-letter | Codon | AA | 1-letter | 3-letter |
|-------|----|----------|----------|-------|----|----------|----------|
| CUU | Leucine | L | Leu | CCU | Proline | P | Pro |
| CUC | Leucine | L | Leu | CCC | Proline | P | Pro |
| CUA | Leucine | L | Leu | CCA | Proline | P | Pro |
| CUG | Leucine | L | Leu | CCG | Proline | P | Pro |
| CAU | Histidine | H | His | CGU | Arginine | R | Arg |
| CAC | Histidine | H | His | CGC | Arginine | R | Arg |
| CAA | Glutamine | Q | Gln | CGA | Arginine | R | Arg |
| CAG | Glutamine | Q | Gln | CGG | Arginine | R | Arg |

### First base A

| Codon | AA | 1-letter | 3-letter | Codon | AA | 1-letter | 3-letter |
|-------|----|----------|----------|-------|----|----------|----------|
| AUU | Isoleucine | I | Ile | ACU | Threonine | T | Thr |
| AUC | Isoleucine | I | Ile | ACC | Threonine | T | Thr |
| AUA | Isoleucine | I | Ile | ACA | Threonine | T | Thr |
| AUG | **Methionine (START)** | M | Met | ACG | Threonine | T | Thr |
| AAU | Asparagine | N | Asn | AGU | Serine | S | Ser |
| AAC | Asparagine | N | Asn | AGC | Serine | S | Ser |
| AAA | Lysine | K | Lys | AGA | Arginine | R | Arg |
| AAG | Lysine | K | Lys | AGG | Arginine | R | Arg |

### First base G

| Codon | AA | 1-letter | 3-letter | Codon | AA | 1-letter | 3-letter |
|-------|----|----------|----------|-------|----|----------|----------|
| GUU | Valine | V | Val | GCU | Alanine | A | Ala |
| GUC | Valine | V | Val | GCC | Alanine | A | Ala |
| GUA | Valine | V | Val | GCA | Alanine | A | Ala |
| GUG | Valine | V | Val | GCG | Alanine | A | Ala |
| GAU | Aspartic acid | D | Asp | GGU | Glycine | G | Gly |
| GAC | Aspartic acid | D | Asp | GGC | Glycine | G | Gly |
| GAA | Glutamic acid | E | Glu | GGA | Glycine | G | Gly |
| GAG | Glutamic acid | E | Glu | GGG | Glycine | G | Gly |


## 4. The 22 Amino Acids

### The 20 standard amino acids

| # | Name | 1-letter | 3-letter | Codons | Count | Property |
|---|------|----------|----------|--------|-------|----------|
| 1 | Glycine | G | Gly | GGU, GGC, GGA, GGG | 4 | Nonpolar, smallest |
| 2 | Alanine | A | Ala | GCU, GCC, GCA, GCG | 4 | Nonpolar, hydrophobic |
| 3 | Valine | V | Val | GUU, GUC, GUA, GUG | 4 | Nonpolar, hydrophobic |
| 4 | Leucine | L | Leu | UUA, UUG, CUU, CUC, CUA, CUG | 6 | Nonpolar, hydrophobic |
| 5 | Isoleucine | I | Ile | AUU, AUC, AUA | 3 | Nonpolar, hydrophobic |
| 6 | Proline | P | Pro | CCU, CCC, CCA, CCG | 4 | Nonpolar, cyclic (imino acid) |
| 7 | Phenylalanine | F | Phe | UUU, UUC | 2 | Nonpolar, aromatic |
| 8 | Tryptophan | W | Trp | UGG | 1 | Nonpolar, aromatic, largest |
| 9 | Methionine | M | Met | AUG | 1 | Nonpolar, sulfur-containing, START |
| 10 | Serine | S | Ser | UCU, UCC, UCA, UCG, AGU, AGC | 6 | Polar, uncharged, hydroxyl |
| 11 | Threonine | T | Thr | ACU, ACC, ACA, ACG | 4 | Polar, uncharged, hydroxyl |
| 12 | Cysteine | C | Cys | UGU, UGC | 2 | Polar, sulfhydryl, disulfide bonds |
| 13 | Tyrosine | Y | Tyr | UAU, UAC | 2 | Polar, aromatic, hydroxyl |
| 14 | Asparagine | N | Asn | AAU, AAC | 2 | Polar, uncharged, amide |
| 15 | Glutamine | Q | Gln | CAA, CAG | 2 | Polar, uncharged, amide |
| 16 | Aspartic acid | D | Asp | GAU, GAC | 2 | Negative charge (acidic) |
| 17 | Glutamic acid | E | Glu | GAA, GAG | 2 | Negative charge (acidic) |
| 18 | Lysine | K | Lys | AAA, AAG | 2 | Positive charge (basic) |
| 19 | Arginine | R | Arg | CGU, CGC, CGA, CGG, AGA, AGG | 6 | Positive charge (basic) |
| 20 | Histidine | H | His | CAU, CAC | 2 | Positive charge (basic, imidazole) |

### Degeneracy summary
| Codon count | Amino acids | Which ones |
|-------------|-------------|------------|
| 6 codons | 3 | Leu, Ser, Arg |
| 4 codons | 5 | Gly, Ala, Val, Pro, Thr |
| 3 codons | 1 | Ile |
| 2 codons | 9 | Phe, Tyr, Cys, Asn, Asp, Glu, Gln, Lys, His |
| 1 codon | 2 | Met (AUG), Trp (UGG) |
| **Total** | **20** | **61 sense codons** |

### The 21st amino acid: Selenocysteine (Sec, U)
- Single-letter code: **U**
- Three-letter code: **Sec**
- Encoded by: **UGA** (normally a stop codon)
- Mechanism: Requires a SECIS element (selenocysteine insertion sequence) in the mRNA, a stem-loop structure downstream of the UGA codon
- Property: Contains selenium instead of sulfur (cf. cysteine)
- Found in: All three domains of life (bacteria, archaea, eukarya)
- Function: Present in selenoproteins (glutathione peroxidases, thioredoxin reductases)

### The 22nd amino acid: Pyrrolysine (Pyl, O)
- Single-letter code: **O**
- Three-letter code: **Pyl**
- Encoded by: **UAG** (normally a stop codon, "amber")
- Mechanism: Requires a PYLIS element (pyrrolysine insertion sequence) in the mRNA; pyrrolysine is loaded directly onto tRNA^Pyl by a dedicated synthetase (PylRS)
- Property: Lysine derivative with a pyrroline ring
- Found in: Some methanogenic archaea, some bacteria
- Function: Found in methyltransferases involved in methanogenesis

### The complete alphabet: 22 letters
```
A  Ala   Alanine          M  Met   Methionine
C  Cys   Cysteine         N  Asn   Asparagine
D  Asp   Aspartic acid    O  Pyl   Pyrrolysine (22nd)
E  Glu   Glutamic acid    P  Pro   Proline
F  Phe   Phenylalanine    Q  Gln   Glutamine
G  Gly   Glycine          R  Arg   Arginine
H  His   Histidine        S  Ser   Serine
I  Ile   Isoleucine       T  Thr   Threonine
K  Lys   Lysine           U  Sec   Selenocysteine (21st)
L  Leu   Leucine          V  Val   Valine
                           W  Trp   Tryptophan
                           Y  Tyr   Tyrosine
```
22 amino acids. 22 letters. (Hebrew alphabet: 22 letters.)


## 5. Start and Stop Codons

### Start codon: AUG
- Encodes methionine (Met, M)
- Universal start signal for translation
- In bacteria: formyl-methionine (fMet) at initiation
- Rare alternative starts: GUG (Val), UUG (Leu) in some bacteria -- but the ribosome reads them as Met at the start position
- **Every protein begins with methionine** (often cleaved post-translationally)

### Stop codons
| Codon | Name | Discovery |
|-------|------|-----------|
| UAA | Ochre | First discovered (1965) |
| UAG | Amber | Named for discoverer Bernstein (German: amber) |
| UGA | Opal (Umber) | Third discovered |

- No tRNA recognizes stop codons in normal translation
- Release factors (RF1, RF2 in bacteria; eRF1 in eukaryotes) bind instead
- UAA is the most common stop codon across all domains of life
- UGA is recoded to selenocysteine (with SECIS element)
- UAG is recoded to pyrrolysine (with PYLIS element)


## 6. The Wobble Hypothesis (Crick, 1966)

### The problem
- 61 sense codons but only ~45 tRNAs in most organisms
- How can fewer tRNAs decode all 61 codons?

### The insight
The first two codon positions pair strictly (Watson-Crick). The **third position** (3' end of codon, 5' end of anticodon) has relaxed geometry, allowing non-standard pairing.

### Wobble pairing rules
| 5' anticodon base | Can pair with 3' codon base |
|--------------------|-----------------------------|
| A | U only |
| C | G only |
| G | C or U |
| U | A or G |
| I (inosine) | A, C, or U |

### Consequences
1. **Pyrimidine equivalence**: When the third position is a pyrimidine (U or C), the amino acid is often the same. A single tRNA with G at the wobble position reads both.
2. **Purine equivalence**: When the third position is a purine (A or G), the amino acid is often the same. A single tRNA with U at the wobble position reads both.
3. **Inosine**: Modified base inosine in the anticodon can pair with U, C, or A -- one tRNA reads three codons.
4. **Fourfold degeneracy**: When all four third-position variants encode the same amino acid (e.g., GCN = Ala), a single tRNA with inosine suffices.

### The structural pattern
- 8 amino acids have **fourfold degeneracy** at position 3 (any base works): Ala, Gly, Pro, Thr, Val, Ser(UC_), Leu(CU_), Arg(CG_)
- 9 amino acids have **twofold degeneracy** (pyrimidine or purine): Phe, Tyr, His, Asn, Asp, Cys, Gln, Glu, Lys
- 2 amino acids have **no degeneracy** (single codon): Met, Trp
- 1 amino acid has **threefold degeneracy**: Ile (AUU, AUC, AUA)
- 3 amino acids are **split** across codon families: Leu (6), Ser (6), Arg (6)


## 7. Reading Frames

### The concept
A nucleotide sequence can be partitioned into codons starting at any of three offsets:

```
Sequence:  A U G C C A G U U ...
Frame +1:  AUG CCA GUU ...    (starts at position 1)
Frame +2:   UGC CAG UU. ..    (starts at position 2)
Frame +3:    GCC AGU U.. ...   (starts at position 3)
```

### Six total frames
- **3 forward frames** (+1, +2, +3) on the sense strand
- **3 reverse frames** (-1, -2, -3) on the antisense (complementary) strand
- The start codon AUG determines which frame the ribosome uses
- An open reading frame (ORF) runs from a start codon to a stop codon in the same frame

### Frame sensitivity
- A single insertion or deletion (indel) shifts the reading frame
- **Frameshift mutations** are almost always catastrophic -- every downstream codon is misread
- The genetic code's structure minimizes damage from point mutations (substitutions) but NOT from frameshifts
- Some organisms use programmed frameshifts for gene regulation


## 8. The Tree of Life

### Three domains (Woese, 1990)
```
                    LUCA
                   /    \
                  /      \
            Bacteria    Archaea ---- Eukarya
                            \       /
                             \     /
                        (closer relatives)
```

- **Bacteria** (Eubacteria): E. coli, cyanobacteria, most pathogens
- **Archaea**: methanogens, extremophiles, halophiles -- originally mistaken for bacteria
- **Eukarya**: protists, fungi, plants, animals -- all with membrane-bound nucleus

Modern phylogenomics places Eukarya as arising from within Archaea (the Asgard archaea), making a two-domain model (Bacteria + Archaea/Eukarya) increasingly supported.

### LUCA (Last Universal Common Ancestor)
- Estimated: **~4.2 billion years ago** (2024 study, CI: 4.33-4.09 Gya)
- Only 100-200 million years after Earth became habitable
- **Had**: DNA or RNA genome, ribosomes, the genetic code, lipid membranes, ~2,600 proteins
- **Used**: Chemiosmotic energy production, ion pumps (Na+ exclusion, K+ concentration)
- **Evidence**: All three domains share the same genetic code, same ribosome structure, same chirality (L-amino acids, D-sugars), same ATP as energy currency

### Is the code truly universal?
**Nearly universal, with known exceptions:**

| Organism/lineage | Codon change | Standard meaning | Reassigned to |
|------------------|-------------|------------------|---------------|
| Vertebrate mitochondria | AGA, AGG | Arg | Stop |
| Vertebrate mitochondria | AUA | Ile | Met |
| Vertebrate mitochondria | UGA | Stop | Trp |
| Yeast mitochondria | CUN | Leu | Thr |
| Invertebrate mitochondria | AGA, AGG | Arg | Ser |
| Ciliate nuclei | UAA, UAG | Stop | Gln |
| Euplotid nuclei | UGA | Stop | Cys |
| Mycoplasma | UGA | Stop | Trp |
| Candida (some species) | CUG | Leu | Ser |

NCBI catalogs **33 variant genetic codes** total. All are minor modifications of the standard code -- no organism uses a fundamentally different mapping. The core structure (which amino acid class maps to which codon family) is conserved across all life.


## 9. Structure of the Codon Table

### Organization
The 4x4 grid is organized by:
- **Rows**: First base (5' position) -- U, C, A, G
- **Columns**: Second base -- U, C, A, G
- **Sub-rows**: Third base (3' position, wobble) -- U, C, A, G

This creates 16 "boxes" of 4 codons each (16 x 4 = 64).

### Degeneracy patterns within boxes

**8 boxes are "family boxes"** (all 4 codons = same amino acid):
- GCN = Ala, GGN = Gly, GUN = Val, CCN = Pro
- ACN = Thr, UCN = Ser, CUN = Leu, CGN = Arg

**5 boxes are "split boxes"** (2+2: pyrimidine pair + purine pair):
- UUN = Phe(U,C) + Leu(A,G)
- AUN = Ile(U,C,A) + Met(G) -- unusual 3+1 split
- UAN = Tyr(U,C) + Stop(A,G)
- AAN = Asn(U,C) + Lys(A,G)
- GAN = Asp(U,C) + Glu(A,G)

**3 more split boxes**:
- CAN = His(U,C) + Gln(A,G)
- AGN = Ser(U,C) + Arg(A,G)
- UGN = Cys(U,C) + Stop/Trp -- the most irregular box (UGA=Stop, UGG=Trp)

### The second base determines chemistry
This is one of the most striking patterns:

| Second base | Amino acid property |
|-------------|-------------------|
| **U** | Hydrophobic (Phe, Leu, Ile, Met, Val) |
| **C** | Small/structural (Ser, Pro, Thr, Ala) |
| **A** | Charged/polar (Tyr, His, Gln, Asn, Lys, Asp, Glu) + Stop |
| **G** | Mixed/special (Cys, Trp, Arg, Gly) + Stop |

The second base position carries the most chemical information about the amino acid's properties.


## 10. Mathematical and Numerical Patterns

### Rumer's bisection (1966)
Yuri Rumer, a Russian theoretical physicist, discovered that the 64 codons split into two classes of exactly **32** each:

- **Class I (32 codons)**: "Family boxes" -- the third base is irrelevant. All 4 codons in the box encode the same amino acid. These have **degeneracy = 4** at the wobble position.
- **Class II (32 codons)**: "Split boxes" -- the third base matters. The box encodes 2 different amino acids (or amino acid + stop). These have **degeneracy = 1 or 2** at the wobble position.

**The Rumer transformation**: Swapping A<->C and G<->U (equivalently: swapping purines and pyrimidines within each pair) at all three positions converts every Class I codon into a Class II codon and vice versa. The code has a hidden symmetry under this operation.

### Error minimization (Freeland & Hurst, 1998)
- Tested 1 million randomly generated alternative genetic codes
- With realistic biological weighting (transitions more likely than transversions, position-dependent error rates):
  **Only 1 in 1,000,000 random codes performed better** than the actual genetic code at minimizing translation error impact
- The standard code is in the **99.9999th percentile** for error robustness
- Similar amino acids (by hydrophobicity, size, charge) tend to have similar codons
- Single-base mutations at the third position are almost always synonymous (silent)
- Single-base mutations at the first position usually produce chemically similar amino acids

### Symmetry properties
- **Complementary codons** (read 3'->5' on opposite strand) tend to encode amino acids with opposite properties
- **XYU/XYC always synonymous**: If the third base is a pyrimidine, the two codons ALWAYS encode the same amino acid. No exceptions in the standard code.
- **XYA/XYG usually synonymous**: If the third base is a purine, the two codons encode the same amino acid in 13 of 16 boxes. Exceptions: AUA(Ile) vs AUG(Met), UGA(Stop) vs UGG(Trp), UAA(Stop) vs UAG(Stop -- but different release factors).

### Numerical regularities
- 64 = 4^3 = 2^6. The codon space is a 6-dimensional binary hypercube (each base = 2 bits).
- 61 sense codons / 20 amino acids = 3.05 codons per amino acid (average)
- The degeneracy distribution is far from uniform: {1,1,2,2,2,2,2,2,2,2,3,4,4,4,4,4,6,6,6} + 3 stops
- Sum of all codon counts: 1+1+2+2+2+2+2+2+2+2+3+4+4+4+4+4+6+6+6 = 61 (check: correct)
- The number of amino acids (20) falls between the minimum needed for protein function (~13-15 estimated) and the maximum the triplet code could support without synonymy (61)
- With 22 amino acids total (including Sec and Pyl), the biological alphabet exactly matches the Hebrew alphabet count

### The Gray code connection
- Adjacent codons in the table (differing by one base) tend to encode the same or similar amino acids
- The codon table can be mapped onto a 6-dimensional hypercube where Hamming distance correlates with amino acid dissimilarity
- Point mutations (Hamming distance 1) are maximally buffered

### Information content
- 4 bases encode log2(4) = 2 bits per position
- A codon carries 3 x 2 = 6 bits
- 6 bits can distinguish 64 symbols
- 20 amino acids require log2(20) = 4.32 bits
- The code is therefore **redundant by 6 - 4.32 = 1.68 bits per codon**
- This redundancy is not wasted -- it provides error correction at the wobble position


## 11. The Codon Space as a Mapping

### Compact notation (RNA)

```
         U           C           A           G
    +-----------+-----------+-----------+-----------+
    | UUU  F    | UCU  S    | UAU  Y    | UGU  C    |
U   | UUC  F    | UCC  S    | UAC  Y    | UGC  C    |
    | UUA  L    | UCA  S    | UAA  *    | UGA  *    |
    | UUG  L    | UCG  S    | UAG  *    | UGG  W    |
    +-----------+-----------+-----------+-----------+
    | CUU  L    | CCU  P    | CAU  H    | CGU  R    |
C   | CUC  L    | CCC  P    | CAC  H    | CGC  R    |
    | CUA  L    | CCA  P    | CAA  Q    | CGA  R    |
    | CUG  L    | CCG  P    | CAG  Q    | CGG  R    |
    +-----------+-----------+-----------+-----------+
    | AUU  I    | ACU  T    | AAU  N    | AGU  S    |
A   | AUC  I    | ACC  T    | AAC  N    | AGC  S    |
    | AUA  I    | ACA  T    | AAA  K    | AGA  R    |
    | AUG  M    | ACG  T    | AAG  K    | AGG  R    |
    +-----------+-----------+-----------+-----------+
    | GUU  V    | GCU  A    | GAU  D    | GGU  G    |
G   | GUC  V    | GCC  A    | GAC  D    | GGC  G    |
    | GUA  V    | GCA  A    | GAA  E    | GGA  G    |
    | GUG  V    | GCG  A    | GAG  E    | GGG  G    |
    +-----------+-----------+-----------+-----------+

* = Stop codon
```

### DNA notation (for reference)

Replace U with T throughout:
- AUG -> ATG (start)
- UAA -> TAA, UAG -> TAG, UGA -> TGA (stops)
- UUU -> TTT (Phe), etc.


## 12. Amino Acid Properties -- Grouped

### Nonpolar / Hydrophobic (9)
G (Gly), A (Ala), V (Val), L (Leu), I (Ile), P (Pro), F (Phe), W (Trp), M (Met)

### Polar / Uncharged (6)
S (Ser), T (Thr), C (Cys), Y (Tyr), N (Asn), Q (Gln)

### Positively charged / Basic (3)
K (Lys), R (Arg), H (His)

### Negatively charged / Acidic (2)
D (Asp), E (Glu)

### Special roles
- **G (Gly)**: Smallest, most flexible, fits anywhere
- **P (Pro)**: Rigid, creates kinks in protein chains
- **C (Cys)**: Forms disulfide bonds (S-S), stabilizes 3D structure
- **M (Met)**: Start signal, sulfur-containing
- **W (Trp)**: Largest, rarest, single codon
- **H (His)**: pKa near 6, acts as proton shuttle at physiological pH


## 13. Summary of Counts

```
4   bases
3   per codon
64  codons total
61  sense (amino-acid-encoding)
3   stop (UAA, UAG, UGA)
1   start (AUG)
20  standard amino acids
22  total amino acids (+ Sec, Pyl)
6   reading frames per sequence
32  Rumer Class I codons (family boxes)
32  Rumer Class II codons (split boxes)
16  codon boxes (4 codons each)
8   family boxes (4-fold degenerate)
8   split boxes (2-fold or mixed)
33  known variant genetic codes (NCBI)
~45 tRNA species (in most organisms)
10  base pairs per helix turn
34  angstroms per helix turn
20  angstroms helix diameter
2   H-bonds in A-T pair
3   H-bonds in G-C pair
3   domains of life (Bacteria, Archaea, Eukarya)
1   LUCA (last universal common ancestor, ~4.2 Gya)
```
