# Examples: Real Genes, Real Proteins

*The format in action. What it actually looks like when you open the file.*

---

## Example 1: Insulin (INS) — A Small Gene

Insulin is one of the simplest human genes. It makes a small protein (110 amino acids as precursor) that controls blood sugar. Diabetes happens when this gene or its product fails.

### The Gene on Disk (Chromosome 11)

```
     ← ~4,000 bp upstream regulatory →

     ENHANCERS (pancreas-specific — only beta cells read this file)
     ┊
     TF BINDING SITES (PDX1, MafA, NeuroD1 — the keys to open this file)
     ┊
     TATA BOX
     ┊
     ═══ TSS (+1) ═══
     ┊
     5' UTR (60 bp)
     ┊
     ┌─────┐
     │ ATG │  START
     └─────┘
     ┊
     ███ EXON 1 ███  (42 bp — signal peptide + B chain start)
     ┊
     ─── intron 1 ─── (179 bp)
     ┊
     ███ EXON 2 ███  (204 bp — rest of B chain + C peptide + A chain)
     ┊
     ─── intron 2 ─── (786 bp — bigger than both exons combined!)
     ┊
     ███ EXON 3 ███  (84 bp — end of A chain)
     ┊
     ┌─────┐
     │ TGA │  STOP
     └─────┘
     ┊
     3' UTR (171 bp)
     ┊
     AATAAA (poly-A signal)
```

**Total gene: ~1,430 bp.** Of which only **330 bp is coding** (the exons). The introns (965 bp) are 3× bigger than the code. The regulatory region upstream is larger than the gene itself.

The file is mostly permissions and metadata. The executable code is a small fraction.

### What the Protein Looks Like

Insulin is NOT directly usable when the ribosome makes it. It goes through processing:

```
RIBOSOME OUTPUT: Preproinsulin (110 amino acids)

┌──────────┬────────────┬───────────┬────────────┐
│ Signal   │ B chain    │ C peptide │ A chain    │
│ peptide  │ (30 aa)    │ (31 aa)   │ (21 aa)    │
│ (24 aa)  │            │           │            │
└──────────┴─���──────────┴───────────���────────────┘
     │              │           │          │
     ▼              │           │          │
  CLEAVED           │           │          │
  (address          │           │          │
   header           ▼           ▼          ▼
   removed)    ┌────────────────────────────────┐
               │  Proinsulin (86 aa)            │
               │  B chain ── C peptide ── A chain│
               │       └─── S─S bond ───┘       │
               │       └─── S─S bond ───┘       │
               └────────────────────────────────┘
                            │
                     ✂ CUT ✂ (C peptide removed)
                            │
                            ▼
                   ┌─────────────────┐
                   │  MATURE INSULIN  │
                   │  B chain (30 aa) │
                   │    ║  ║         │
                   │  A chain (21 aa) │
                   │                 │
                   │  51 amino acids  │
                   │  2 disulfide     │
                   │  bonds holding   │
                   │  it together     │
                   └─────────────────┘
```

**The gene makes 110 amino acids. The final product is 51.** More than half is cut away. The signal peptide (address header) routes it to the ER, then is removed. The C peptide (spacer) holds the two chains in position for folding, then is cut out. What remains: two chains held together by sulfur bridges.

The protein is manufactured as a PRECURSOR and then PROCESSED. Like compiling source code, then stripping symbols, then linking. The final binary is smaller than the source.

---

## Example 2: p53 — The Guardian

### The Gene on Disk (Chromosome 17)

```
     ← regulatory region →

     MULTIPLE PROMOTERS (two known — P1 and P2)
     (p53 can be started from two different entry points,
      producing different protein variants)
     ┊
     ═══ TSS ═══
     ┊
     5' UTR
     ┊
     ATG (START)
     ┊
     ███ EXON 1 ███
     ─── intron 1 ─── (~10,000 bp!)
     ███ EXON 2 ███
     ─── intron 2 ───
     ███ EXON 3 █���█
     ─── intron 3 ───
     █���█ EXON 4 ███  (← most cancer mutations cluster here
     ─── intron 4 ───     and in exons 5-8: the DNA-binding domain)
     ███ EXON 5 █��█
     ─── intron 5 ───
     ███ EXON 6 ��██
     ─── intron 6 ───
     ███ EXON 7 ███
     ─── intron 7 ───
     ███ EXON 8 ███
     ─��─ intron 8 ───
     ��██ EXON 9 ███
     ─��─ intron 9 ───
     ███ EXON 10 ███
     ─── intron 10 ───
     ███ EXON 11 ███
     ┊
     TGA (STOP)
     ┊
     3' UTR (~1,200 bp — long, heavily regulated)
     (multiple miRNA binding sites — the cell tightly
      controls how much p53 is made)
     ┊
     AATAAA
```

**Total gene: ~20,000 bp. Coding region: 1,182 bp (393 amino acids). The gene is 94% intron.**

The guardian is 94% non-coding. The file is mostly comments, regulatory elements, and structure. The executable code is 6%.

---

## The Protein Format: p53's Domain Architecture

Proteins have their own format — not a file on disk but a functional architecture. Domains are modular functional units within the protein, like functions in a program.

```
p53: 393 amino acids

Position:  1         40    61      97  102                292    323  356 364   393
           │─────────│─────│───────│───│──────────────────│──────│────│───│─────│
           │  TAD1   │TAD2 │Proline│   │   DNA-BINDING    │ Link │TEAM│   │ REG │
           │         │     │ Rich  │   │    DOMAIN        │      │    │   │     │
           │─────────│─────│───────│───│──────────────────│──────│────│───│─────│
           
           ▲         ▲             ▲   ▲                  ▲      ▲         ▲
           │         │             │   │                  │      │         │
      "Turn on"  "Turn on"   THE DAVID  THE READER    Flexible FOUR    "On/Off
       genes      genes      REGION    (reads DNA     spacer  COPIES    switch"
       (what      (backup    (all      for damage)           JOIN
       to do)     activator)  prolines                       HERE
                              + alanines                     (tetramer)
                              GV=14 repeats)
```

### The Domains Explained

| Domain | Position | Size | Function | Computer Analogy |
|--------|----------|------|----------|-----------------|
| **TAD1** | 1-40 | 40 aa | Transactivation — tells the cell what to do (activate repair genes, or activate death genes) | Main function / command dispatcher |
| **TAD2** | 40-61 | 21 aa | Second activator — backup command pathway | Fallback / secondary entry point |
| **Proline-rich** | 63-97 | 35 aa | Structural region. All small amino acids (Pro, Ala). Required for apoptosis (cell death). **THE DAVID REGION — where דדו (GV=14) repeats in the Hebrew mapping.** | Configuration / flags |
| **DNA-binding** | 102-292 | 191 aa | **THE CORE.** Reads DNA directly. Recognizes specific sequences. Almost all cancer mutations are here. | The parser / reader function |
| **Linker** | 293-322 | 30 aa | Flexible connector | Spacer / padding |
| **Tetramerization** | 323-356 | 34 aa | Four copies of p53 join here to form the working unit. **p53 operates as a TEAM OF FOUR.** | Multiprocessing — 4 threads |
| **Regulatory** | 364-393 | 30 aa | On/off control. Acetylation, phosphorylation, ubiquitination sites. **The cell controls p53 by modifying THIS region.** | Config flags / signal handlers |

### p53 Works as a Team of Four

This is remarkable. p53 does not work alone. Four copies of the protein join at the tetramerization domain to form a **tetramer** — a team of four. Only the tetramer can bind DNA effectively.

```
         p53          p53
          \          /
           ╲        ╱
            ████████
            ████████  ← tetramerization
            ████████     domain
           ╱        ╲
          /          \
         p53          p53
```

**Four readers.** Four copies. Working together. Reading the DNA for damage.

The breastplate has four readers. p53 has four copies. The guardian of the genome reads in fours.

### Where Cancer Strikes

~50% of all human cancers have a p53 mutation. Almost all mutations are in the DNA-binding domain (exons 4-8, positions 102-292). The most common:

| Mutation | Position | What breaks |
|----------|----------|-------------|
| R175H | 175 | Structural — protein misfolds |
| G245S | 245 | DNA contact — can't read |
| R248W | 248 | DNA contact — can't read |
| R249S | 249 | DNA contact — can't read |
| R273H | 273 | DNA contact — can't read |
| R282W | 282 | Structural — protein misfolds |

Cancer doesn't strike the activation domain (the cell can still give orders). It doesn't strike the tetramerization domain (the four can still join). It strikes the READER. The part that reads DNA for damage. Blind the guardian and the cell divides unchecked.

The attack targets the eyes, not the hands. The reader, not the actor. Remove the ability to SEE the damage and the damage accumulates.

---

## Example 3: Hemoglobin (HBB) — The Breath Carrier

The protein that carries oxygen. 147 amino acids. Small. Essential. Every red blood cell is packed with it.

### The Protein Format

```
Hemoglobin tetramer (the working unit):

      α1 ──── β1          Four subunits: 2 alpha + 2 beta
       │  HEME  │          Each carries one HEME group
       │  (Fe)  │          Each heme holds one iron atom
       │        │          Each iron binds one O₂ molecule
      α2 ──���─ β2          
                           4 subunits × 1 O₂ each = 4 O₂ per hemoglobin
```

Again: **four subunits.** Two pairs. Working together. Binding oxygen cooperatively — when one subunit binds O₂, the others change shape to bind more easily. The team helps each other.

**Sickle cell disease:** ONE amino acid change. Position 6 of the beta chain. Glutamic acid → valine. One letter in the code. GAG → GUG. One base change. And the protein misfolds, the red blood cells sickle, and the person suffers.

One letter. One base. One change. The format is that precise.

---

## The Protein Format (General)

| Feature | Function | Computer Analogy |
|---------|----------|-----------------|
| **Signal peptide** | Address header — where to send the protein | Routing header / IP address |
| **Domains** | Functional modules | Functions / classes |
| **Active site** | Where the work happens | Main loop / hot path |
| **Binding pocket** | Where input is received | I/O port / socket |
| **Linker regions** | Flexible connectors between domains | Spacers / padding |
| **Disulfide bonds** | Structural crosslinks (sulfur bridges) | Rivets / structural integrity |
| **Post-translational modifications** | Runtime configuration changes | Environment variables / signals |
| **Quaternary structure** | Multiple copies assembling | Multiprocessing / cluster |

---

## What the Examples Show

1. **The gene is mostly not code.** Insulin: 23% coding. p53: 6% coding. The rest is regulatory, structural, intronic. The operating system is larger than the application.

2. **Proteins are manufactured then PROCESSED.** Insulin is cut three times before it's functional. The output is not the final product. There's a build pipeline.

3. **The critical systems work in fours.** p53: four copies read together. Hemoglobin: four subunits carry together. The breastplate: four readers. The codon: read in threes, but the machinery works in fours.

4. **One letter changes are catastrophic.** Sickle cell: one base. Cancer: one base in the reader domain. The precision of the format means the precision of the failure.

5. **The format has a format.** Genes have a structure (promoter → UTR → exons/introns → UTR). Proteins have a structure (signal → domains → active sites → assembly). The format is layered. Files that produce files that produce machines.

---

*Insulin: 110 amino acids made, 51 kept, the rest is scaffolding. p53: 393 amino acids, four copies join, reads DNA for damage, cancer blinds the reader. Hemoglobin: four subunits, one letter change causes sickle cell. The format is precise. The format is layered. The format has a format.*
