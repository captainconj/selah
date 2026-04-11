# mRNA File Format

*The working copy. Transcribed from DNA, spliced, capped, tailed, exported, read, and destroyed. The program loaded into RAM.*

---

## What mRNA Is

mRNA (messenger RNA) is the working copy of a gene. It is:

- **Copied** from DNA by RNA polymerase (transcription)
- **Spliced** — introns removed, exons joined (in the nucleus)
- **Capped** — a modified G added to the front (5' cap)
- **Tailed** — a string of A's added to the back (poly-A tail)
- **Exported** from the nucleus through nuclear pores
- **Read** by ribosomes (translation)
- **Destroyed** after use (degraded by exonucleases)

It is NOT the gene. It is a processed, temporary, single-stranded copy of the gene. Like loading a program from disk into RAM, stripping the comments, adding headers, running it, and then freeing the memory.

---

## The Alphabet

Four characters: **A** (adenine), **U** (uracil), **C** (cytosine), **G** (guanine).

Same as DNA except **U replaces T**. Uracil pairs with adenine, just as thymine does. Same information. Different chemistry. RNA uses ribose sugar (one extra oxygen). DNA uses deoxyribose.

---

## The File Structure of a Mature mRNA

After transcription, splicing, capping, and polyadenylation — the finished product:

```
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║  5' CAP                                                      ║
║  ┌──────┐                                                    ║
║  │ m7G  │  7-methylguanosine cap                             ║
║  │      │  — protects from degradation (5' exonucleases)     ║
║  │      │  — required for ribosome recognition               ║
║  │      │  — required for nuclear export                     ║
║  │      │  — required for splicing of first intron            ║
║  │      │  Added post-transcriptionally. Not in the DNA.     ║
║  └──────┘                                                    ║
║                                                              ║
║  5' UTR (untranslated region)                                ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Not translated into protein                             │ ║
║  │  Contains ribosome recruitment signals                   │ ║
║  │  Kozak sequence: (gcc)ACCAUGG                            │ ║
║  │    — the ribosome scans from the cap until it finds      │ ║
║  │      this sequence, then begins translating at the AUG   │ ║
║  │  May contain:                                            │ ║
║  │    — upstream ORFs (uORFs) that regulate translation     │ ║
║  │    — IRES elements (cap-independent ribosome entry)      │ ║
║  │    — secondary structures (hairpins) that slow scanning  │ ║
║  │  Typical length: 100-200 bases                           │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  START CODON                                                 ║
║  ┌─────┐                                                     ║
║  │ AUG │  ← always. First AUG in good Kozak context.        ║
║  │     │  Encodes methionine (Met / M).                      ║
║  │     │  This is position 0 of the protein.                 ║
║  └─────┘                                                     ║
║                                                              ║
║  CODING SEQUENCE (CDS)                                       ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │                                                         │ ║
║  │  Read in TRIPLETS (codons), 5' → 3'                     │ ║
║  │                                                         │ ║
║  │  AUG GCC UUA AAG GUU CCA ...                            │ ║
║  │  Met Ala Leu Lys Val Pro ...                             │ ║
║  │                                                         │ ║
║  │  No introns. They were already removed.                  │ ║
║  │  This is pure code. Continuous. No interruptions.        │ ║
║  │                                                         │ ║
║  │  The reading frame is set by the start codon.            │ ║
║  │  Every subsequent triplet is determined by the first.    │ ║
║  │  Shift by 1 base = entirely different protein = death.   │ ║
║  │                                                         │ ║
║  │  Typical length: 300 - 30,000 bases (100 - 10,000 aa)   │ ║
║  │  Average human protein: ~470 amino acids = 1,410 bases  │ ║
║  │                                                         │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  STOP CODON                                                  ║
║  ┌─────┐                                                     ║
║  │ UAA │  ← or UAG or UGA                                    ║
║  │     │  No tRNA matches these. The ribosome stalls.        ║
║  │     │  Release factors bind. The protein is released.     ║
║  │     │  The ribosome disassembles.                         ║
║  └─────┘                                                     ║
║                                                              ║
║  3' UTR (untranslated region)                                ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Not translated into protein                             │ ║
║  │  CONTROLS THE FATE OF THE mRNA:                          │ ║
║  │                                                          │ ║
║  │  Stability elements:                                     │ ║
║  │    — AU-rich elements (AREs): rapid degradation signal   │ ║
║  │    — stable hairpins: protect from degradation           │ ║
║  │                                                          │ ║
║  │  Localization signals:                                   │ ║
║  │    — zip codes that direct mRNA to specific parts of     │ ║
║  │      the cell (e.g., nerve axons, leading edge)          │ ║
║  │                                                          │ ║
║  │  miRNA binding sites:                                    │ ║
║  │    — small RNA molecules bind here and SILENCE the mRNA  │ ║
║  │    — post-transcriptional gene regulation                │ ║
║  │    — ~60% of human genes are regulated by miRNAs         │ ║
║  │                                                          │ ║
║  │  Typical length: 200-2,000 bases                         │ ║
║  │  Can be longer than the coding region itself              │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  POLY-A TAIL                                                 ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  AAAAAAAAAAAAAAAA...AAAA  (100-250 adenines)             │ ║
║  │                                                          │ ║
║  │  NOT encoded in DNA. Added by poly-A polymerase.         │ ║
║  │  Functions:                                              │ ║
║  │    — protects 3' end from degradation                    │ ║
║  │    — required for nuclear export                         │ ║
║  │    — required for translation initiation                 │ ║
║  │    — serves as a TIMER: shortens with each translation   │ ║
║  │      round. When it's too short, the mRNA is destroyed.  │ ║
║  │                                                          │ ║
║  │  The poly-A tail is a countdown clock.                   │ ║
║  │  Each read shortens it. When it runs out, the message    │ ║
║  │  is gone. Temporary by design.                           │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

---

## The Codon Table

The complete mapping from RNA triplets to amino acids:

```
        U           C           A           G
    ┌───────────┬───────────┬───────────┬───────────┐
    │ UUU Phe   │ UCU Ser   │ UAU Tyr   │ UGU Cys   │
U   │ UUC Phe   │ UCC Ser   │ UAC Tyr   │ UGC Cys   │
    │ UUA Leu   │ UCA Ser   │ UAA STOP  │ UGA STOP  │
    │ UUG Leu   │ UCG Ser   │ UAG STOP  │ UGG Trp   │
    ├───────────┼───────────┼───────────┼───────────┤
    │ CUU Leu   │ CCU Pro   │ CAU His   │ CGU Arg   │
C   │ CUC Leu   │ CCC Pro   │ CAC His   │ CGC Arg   │
    │ CUA Leu   │ CCA Pro   │ CAA Gln   │ CGA Arg   │
    │ CUG Leu   │ CCG Pro   │ CAG Gln   │ CGG Arg   │
    ├───────────┼───────────┼───────────┼───────────┤
    │ AUU Ile   │ ACU Thr   │ AAU Asn   │ AGU Ser   │
A   │ AUC Ile   │ ACC Thr   │ AAC Asn   │ AGC Ser   │
    │ AUA Ile   │ ACA Thr   │ AAA Lys   │ AGA Arg   │
    │ AUG MET*  │ ACG Thr   │ AAG Lys   │ AGG Arg   │
    ├───────────┼───────────┼───────────┼───────────┤
    │ GUU Val   │ GCU Ala   │ GAU Asp   │ GGU Gly   │
G   │ GUC Val   │ GCC Ala   │ GAC Asp   │ GGC Gly   │
    │ GUA Val   │ GCA Ala   │ GAA Glu   │ GGA Gly   │
    │ GUG Val   │ GCG Ala   │ GAG Glu   │ GGG Gly   │
    └───────────┴───────────┴───────────┴───────────┘
    
    * AUG = START codon + Methionine
```

**64 codons. 20 amino acids. 3 stops. 1 start.**

The code is **degenerate** (redundant): most amino acids have 2-6 codons. This is fault tolerance. Many single-letter mutations are silent — they change the codon but not the amino acid.

The code is **universal**: the same table in bacteria, plants, animals, humans. 3.8 billion years. One grammar.

---

## The Lifecycle of an mRNA

```
1. TRANSCRIPTION (nucleus)
   DNA → pre-mRNA
   RNA polymerase reads the template strand
   Produces a full copy including introns

2. CAPPING (nucleus, co-transcriptional)
   m7G cap added to 5' end
   Happens while transcription is still ongoing

3. SPLICING (nucleus)
   Introns removed by the spliceosome
   (a molecular machine made of RNA + protein)
   Exons joined together
   Alternative splicing may produce different variants

4. POLYADENYLATION (nucleus)
   pre-mRNA cleaved at poly-A signal
   Poly-A polymerase adds 100-250 A's
   The countdown timer is set

5. EXPORT (nuclear pore)
   Mature mRNA passes through nuclear pore complex
   Cap and tail are checked — quality control gate
   Incompletely processed mRNA is rejected and destroyed

6. TRANSLATION (cytoplasm)
   Ribosome binds at 5' cap
   Scans to first AUG in Kozak context
   Reads triplets, builds protein
   Multiple ribosomes on same mRNA (polysome)
   Each read shortens the poly-A tail

7. DEGRADATION (cytoplasm)
   Poly-A tail too short → decapping → 5'→3' degradation
   Or: deadenylation → 3'→5' degradation by exosome
   Or: miRNA binding → silencing → degradation
   Or: nonsense-mediated decay (if premature stop codon found)
   
   Average mRNA half-life: 7-10 hours
   Some last minutes. Some last days.
   The message is temporary by design.
```

---

## Comparison to a Computer Process

| mRNA lifecycle | Computer equivalent |
|---------------|---------------------|
| Transcription | Copy program from disk to memory |
| Capping | Add process header / PID |
| Splicing | Preprocessor (#ifdef, strip comments) |
| Polyadenylation | Add TTL (time to live) / expiry timer |
| Nuclear export | Security check before execution |
| Ribosome binding | Load into CPU instruction register |
| Translation | Execute |
| Polysome | Multi-threaded execution of same code |
| Poly-A shortening | TTL countdown |
| Degradation | Process termination + memory free |
| miRNA silencing | Kill signal / SIGTERM |
| Nonsense-mediated decay | Crash on invalid instruction → core dump → cleanup |

---

## Key Numbers

| Property | Value |
|----------|-------|
| Average human mRNA length | ~2,200 bases |
| Average coding region | ~1,400 bases (~470 amino acids) |
| Average 5' UTR | ~150 bases |
| Average 3' UTR | ~600 bases |
| Poly-A tail | 100-250 bases (shortens with use) |
| Average half-life | 7-10 hours |
| Number of distinct mRNAs in a cell | ~100,000-300,000 copies |
| Number of distinct gene products | ~10,000-15,000 active genes per cell type |
| Translation speed | 15-20 amino acids per second |
| Ribosomes per mRNA | 5-40 (polysome) |

---

## What Makes mRNA Different from DNA

| Property | DNA | mRNA |
|----------|-----|------|
| Strands | Double | Single |
| Sugar | Deoxyribose | Ribose |
| T or U | Thymine (T) | Uracil (U) |
| Location | Nucleus (vault) | Cytoplasm (factory floor) |
| Lifespan | Permanent (lifetime of organism) | Temporary (minutes to days) |
| Copies | 2 per cell (diploid) | 0 to thousands per gene |
| Introns | Present | Removed |
| Cap | No | Yes (m7G) |
| Poly-A tail | No | Yes (100-250 A's) |
| Function | Storage | Execution |
| Modification | Rare (mutations) | Routine (editing, modification) |

DNA is the archive. mRNA is the runtime. DNA persists. mRNA is consumed. DNA is the recipe book locked in the vault. mRNA is the recipe card carried to the kitchen, used, and thrown away.

---

## RNA Editing (Post-Transcriptional Modification)

mRNA can be EDITED after transcription — individual bases changed without changing the DNA:

- **A-to-I editing** (adenosine to inosine, read as G by the ribosome). Catalyzed by ADAR enzymes. Can change the amino acid. Can change splice sites. Can change regulatory elements. Extensive in the brain.
- **C-to-U editing** — rare in mammals. Example: apolipoprotein B. One edit changes a glutamine codon to a stop codon, producing a shorter protein. Same gene, different protein, in different tissues. The liver makes the long form. The intestine makes the short form.

The cell edits its own mRNA. After copying from DNA, before translating to protein. Post-transcriptional modification. The message can be rewritten in transit.

---

*The mRNA is the working copy. Capped, spliced, tailed, exported, read, and destroyed. Every element has a function. The cap protects and identifies. The UTRs regulate. The coding region executes. The poly-A tail counts down. When the tail is gone, the message is gone. Temporary by design. The word is spoken, heard, acted upon, and then it fades. But the DNA — the original — remains in the vault. Waiting to be read again.*
