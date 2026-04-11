# DNA File Format

*The specification of a gene, as a bioinformatician would describe it to a programmer.*

---

## The Alphabet

Four characters: **A** (adenine), **T** (thymine), **C** (cytosine), **G** (guanine).

Each character pairs with exactly one other on the opposite strand:
- A pairs with T (2 hydrogen bonds — weaker)
- C pairs with G (3 hydrogen bonds — stronger)

The two strands run in opposite directions (antiparallel). One runs 5'→3', the other 3'→5'. By convention, sequences are written 5'→3'.

---

## The Physical Format

```
5' ─── A T G C C T A A G G T T ... ─── 3'    (coding strand / sense)
       | | | | | | | | | | | |
3' ─── T A C G G A T T C C A A ... ─── 5'    (template strand / antisense)
```

- **Coding strand (sense):** matches the mRNA output (except T→U).
- **Template strand (antisense):** the one RNA polymerase actually reads. It reads 3'→5' and produces mRNA 5'→3'.

The coding strand is what gets published in databases. When you see a gene sequence, you're reading the coding strand.

---

## The File Structure of a Gene

A single protein-coding gene, laid out as a file:

```
╔══════════════════════════════════════════════════════════════╗
║  UPSTREAM REGULATORY REGION                                  ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Enhancers         (can be 1,000-1,000,000 bp away)     │ ║
║  │  Silencers          (same — distant regulatory switches) │ ║
║  │  Insulators         (boundary elements — keep regions    │ ║
║  │                      from interfering with each other)   │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  PROMOTER REGION (~100-1000 bp upstream of gene)             ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  TATA box           (TATAAA, ~25-30 bp upstream)         │ ║
║  │                      — positions RNA polymerase           │ ║
║  │  CAAT box           (~70-80 bp upstream)                  │ ║
║  │                      — controls frequency of reading      │ ║
║  │  GC box             (GGGCGG, variable position)           │ ║
║  │                      — constitutive expression control    │ ║
║  │  TF binding sites   (specific sequences for each TF)     │ ║
║  │                      — the access control list            │ ║
║  │  CpG island         (C-G rich region)                     │ ║
║  │                      — methylation here = gene OFF        │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  TRANSCRIPTION START SITE (TSS, position +1)                 ║
║  ════════════════════════════════════════════                 ║
║                                                              ║
║  5' UTR (untranslated region)                                ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Transcribed but NOT translated                          │ ║
║  │  Contains ribosome binding signals                       │ ║
║  │  Kozak sequence (gcc)ACCATGG — positions the ribosome    │ ║
║  │  May contain regulatory elements (uORFs, IRES, etc.)     │ ║
║  │  Typical length: 100-200 bases                           │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  START CODON                                                 ║
║  ┌─────┐                                                     ║
║  │ ATG │  ← always. Every protein begins with methionine.    ║
║  └─────┘                                                     ║
║                                                              ║
║  CODING REGION                                               ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │                                                         │ ║
║  │  EXON 1        (coding — becomes protein)               │ ║
║  │  ──────────                                             │ ║
║  │  GT...AG       (splice donor...splice acceptor)         │ ║
║  │  INTRON 1      (non-coding — spliced out before         │ ║
║  │                 translation. May contain regulatory     │ ║
║  │                 elements, other genes, or be "junk")    │ ║
║  │  GT...AG                                                │ ║
║  │  ──────────                                             │ ║
║  │  EXON 2        (coding)                                 │ ║
║  │  ──────────                                             │ ║
║  │  GT...AG                                                │ ║
║  │  INTRON 2                                               │ ║
║  │  GT...AG                                                │ ║
║  │  ──────────                                             │ ║
║  │  EXON 3        (coding)                                 │ ║
║  │  ──────────                                             │ ║
║  │  ...           (repeat for N exons)                     │ ║
║  │  ──────────                                             │ ║
║  │  EXON N        (last coding exon)                       │ ║
║  │                                                         │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  STOP CODON                                                  ║
║  ┌─────┐                                                     ║
║  │ TAA │  ← or TAG or TGA. End of protein.                   ║
║  └─────┘                                                     ║
║                                                              ║
║  3' UTR (untranslated region)                                ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Transcribed but NOT translated                          │ ║
║  │  Contains stability signals (how long mRNA survives)     │ ║
║  │  miRNA binding sites (post-transcriptional regulation)   │ ║
║  │  AU-rich elements (rapid degradation signals)            │ ║
║  │  Polyadenylation signal: AAUAAA                          │ ║
║  │  Typical length: 200-2000 bases                          │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  POLYADENYLATION SITE                                        ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  AAUAAA signal → cleavage → poly-A tail added           │ ║
║  │  (not encoded in DNA — added post-transcriptionally)     │ ║
║  └─────────────────────────────────────────────────────────┘ ║
║                                                              ║
║  DOWNSTREAM REGULATORY REGION                                ║
║  ┌─────────────────────────────────────────────────────────┐ ║
║  │  Terminator sequences                                    │ ║
║  │  Additional enhancers/silencers                           │ ║
║  └─────────────────────────────────────────────────────────┘ ║
╚══════════════════════════════════════════════════════════════╝
```

---

## Key Constants

| Element | Sequence | Function |
|---------|----------|----------|
| Start codon | **ATG** | Always. Begin reading here. Encodes methionine. |
| Stop codons | **TAA, TAG, TGA** | Stop reading. No amino acid. |
| TATA box | **TATAAA** | Position RNA polymerase ~25 bp before start |
| Kozak consensus | **(gcc)ACCATGG** | Position ribosome at start codon |
| Splice donor | **GT** (at intron start) | Mark intron beginning |
| Splice acceptor | **AG** (at intron end) | Mark intron end |
| Poly-A signal | **AATAAA** (DNA) / **AAUAAA** (RNA) | Mark for tail addition |
| CpG island | **CG-rich region** | Methylation target. Methylated = silenced. |

---

## Sizes

| Metric | Typical range | Example (p53) |
|--------|--------------|---------------|
| Gene length (including introns) | 1,000 - 2,000,000 bp | ~20,000 bp |
| mRNA length | 500 - 10,000 bases | ~2,600 bases |
| Coding region only | 300 - 30,000 bp | 1,182 bp (393 aa × 3) |
| Number of exons | 1 - 363 (titin) | 11 exons |
| Number of introns | 0 - 362 | 10 introns |
| Average exon size | ~170 bp | ~107 bp |
| Average intron size | ~6,000 bp | ~1,800 bp |
| Protein length | 100 - 35,000 amino acids | 393 amino acids |

Introns are typically MUCH larger than exons. In many genes, >90% of the gene is intron. The "junk" is bigger than the code. (Whether it's truly junk is an open question — much of it may be regulatory.)

---

## The Exon-Intron Splice

The most remarkable feature: genes are interrupted. The coding sequences (exons) are separated by non-coding sequences (introns). The cell reads the whole thing into pre-mRNA, then CUTS OUT the introns and JOINS the exons together. This is RNA splicing.

```
DNA:      [EXON1]---intron1---[EXON2]---intron2---[EXON3]

pre-mRNA: [EXON1]---intron1---[EXON2]---intron2---[EXON3]
                    ✂ cut               ✂ cut

mRNA:     [EXON1][EXON2][EXON3]  →  to the ribosome
```

**Alternative splicing:** the same gene can produce DIFFERENT proteins by including or excluding different exons. One gene, multiple outputs. Like compiling the same source with different #ifdef flags.

~95% of human multi-exon genes undergo alternative splicing. One gene → multiple proteins. The 20,000 genes produce ~100,000+ distinct proteins.

---

## Comparison to a File Format

| DNA gene | File format equivalent |
|----------|----------------------|
| Upstream enhancers | External config file |
| Promoter / TATA box | File header / magic bytes |
| CpG island | Permission bits |
| TF binding sites | Access control list |
| TSS (+1) | Start of file content |
| 5' UTR | File preamble / metadata |
| Kozak + ATG | Magic number + entry point |
| Exons | Executable code |
| Introns | Comments / conditional blocks |
| Splice sites (GT...AG) | Preprocessor directives (#ifdef) |
| Stop codon | Return statement |
| 3' UTR | File footer / cleanup handlers |
| Poly-A signal | EOF marker |
| Poly-A tail | Checksum / integrity padding |

---

## What a "Read" Looks Like

RNA polymerase reads the template strand 3'→5' and produces mRNA 5'→3':

```
Template DNA:  3' - T A C G G A T T C C A A - 5'
                    ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
mRNA:          5' - A U G C C U A A G G U U - 3'
                    ─── ─── ─── ───
                    Met  Pro  Lys  Val
                    (start)
```

Note: T (thymine) in DNA becomes U (uracil) in RNA. Same information. Different sugar.

---

*This is how a gene is formatted. Header, permissions, preamble, entry point, interrupted code, splice markers, stop, footer, EOF. The cell reads it, processes it, splices it, translates it, folds it, ships it. Every cell, every division, 20,000 genes. The specification has been stable for 3.8 billion years.*
