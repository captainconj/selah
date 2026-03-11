# Protein Catalog

*Every protein played through the breastplate. Provenance, accessions, cache locations, usage.*

Type: `reference`
State: `clean`

---

## The Core Library (21 proteins)

Defined in `src/selah/dna.clj`. Each has a curated oracle artifact in `data/dna/` and a cached sequence in `data/sequences/uniprot/`.

| # | Name | Accession | Organism | Residues | Why chosen | Experiments |
|---|------|-----------|----------|----------|-----------|-------------|
| 1 | p53 | P04637 | Human | 393 | Guardian of the genome. GV=53=garden. | 101, 105, 127 |
| 2 | BRCA1 | P38398 | Human | 1863 | DNA repair. The other guardian. | 101, 106 |
| 3 | Hemoglobin-alpha | P69905 | Human | 142 | The blood. Carries oxygen. | 101, 105 |
| 4 | Hemoglobin-beta | P68871 | Human | 147 | The blood. Sickle cell = one letter change. | 101, 105, 127 |
| 5 | Myoglobin | P02144 | Human | 154 | First protein structure ever solved (1958). | 101, 105 |
| 6 | Insulin | P01308 | Human | 110 | First protein sequenced (1951). Life and death. | 101, 128 |
| 7 | Collagen-I-alpha1 | P02452 | Human | 1464 | Most abundant protein. The scaffold. | 101 |
| 8 | Laminin-gamma1 | P11047 | Human | 1609 | Cross-shaped. Holds cells together. | 101 |
| 9 | Histone-H3 | P68431 | Human | 136 | Wraps DNA. The scroll. | 101 |
| 10 | Histone-H4 | P62805 | Human | 103 | Most conserved eukaryotic protein. Eternal scroll. | 101 |
| 11 | Ubiquitin | P0CG48 | Human | 76 | Marks for destruction. Judgment. | 101 |
| 12 | Rhodopsin | P08100 | Human | 348 | Vision. Light → signal. | 101 |
| 13 | Cytochrome-c | P99999 | Human | 105 | Electron transport. Most conserved across species. | 101 |
| 14 | ATP-synthase-beta | P06576 | Human | 529 | The rotary engine. Makes ATP. | 101 |
| 15 | RNA-polymerase-II | P24928 | Human | 1970 | Reads DNA, writes RNA. The scribe. | 101 |
| 16 | Ribosomal-protein-S3 | P23396 | Human | 243 | Part of the ribosome. Reads the code. | 101 |
| 17 | Ferredoxin | P10109 | Human | 184 | Iron-sulfur. Among the oldest. Iron=26=YHWH. | 101 |
| 18 | Calmodulin | P0DP23 | Human | 149 | Calcium messenger. The signal. | 101, 106 |
| 19 | Immunoglobulin-G1 | P01857 | Human | 330 | Antibody. The guard. Recognition. | 101, 107 |
| 20 | Serpent-toxin-alpha | P60615 | Bungarus multicinctus | 74 | Alpha-bungarotoxin. The serpent's weapon. | 101 |
| 21 | FOXP2 | O15409 | Human | 715 | Language protein. Two AAs separate man from ape. | 101 |

**Oracle artifacts:** `data/dna/{slug}-oracle.edn` + `data/dna/{slug}-report.txt`
**Cached sequences:** `data/sequences/uniprot/{ACCESSION}.fasta`
**Regeneration:** `(dna/experiment "name")` — fetches, translates, slides, saves.

---

## Leprosy Proteins (Experiment 123)

Source: `dev/experiments/123_the_cure_for_leprosy.clj`, `dev/scripts/proteins/play_leprosy.clj`

| Name | Accession | Status | Organism | Purpose | Oracle Artifact |
|------|-----------|--------|----------|---------|-----------------|
| Leprae-Ag85B | Q49899 | **WRONG** → P31951 | M. leprae | Mycolyltransferase. Builds waxy armor. | leprae-ag85b-oracle.edn |
| Leprae-SOD | P31952 | **WRONG** → P13367 | M. leprae | Superoxide dismutase [Mn]. Immune shield. | leprae-sod-oracle.edn |
| Leprae-MMP-I | P09655 | **WRONG** → P46841 | M. leprae | Major membrane protein I / encapsulin. | leprae-mmp-i-oracle.edn |
| Leprae-Hsp65 | P0A5B4 | **WRONG** → P09239 | M. leprae | Chaperonin GroEL 2. Major T-cell antigen. | leprae-hsp65-oracle.edn |
| Leprae-Bacterioferritin | P15917 | **WRONG** → P43315 | M. leprae | Bacterioferritin BFR. Iron storage. | leprae-bacterioferritin-oracle.edn |

**All 5 leprosy accessions need correction.** Existing oracle artifacts were computed from wrong proteins.

### Corrected Accessions

| Name | Wrong | Actually was | Correct | Real protein | Residues |
|------|-------|-------------|---------|-------------|----------|
| Leprae-Ag85B | Q49899 | Uncharacterized MLC1351.15c | **P31951** | Ag85B mycolyltransferase | 327 |
| Leprae-SOD | P31952 | Demerged/inactive entry | **P13367** | Superoxide dismutase [Mn] | 207 |
| Leprae-MMP-I | P09655 | Rat Spink1 protease inhibitor | **P46841** | Encapsulin / MMP-I | 307 |
| Leprae-Hsp65 | P0A5B4 | M. bovis biosynthesis protein | **P09239** | Chaperonin GroEL 2 | 541 |
| Leprae-Bacterioferritin | P15917 | Anthrax lethal factor | **P43315** | Bacterioferritin BFR | 159 |

---

## Tuberculosis Proteins (Experiment 127)

Source: `dev/experiments/127_pathogen_prescriptions.clj`

| Name | Accession | Status | Organism | Purpose | Oracle Artifact |
|------|-----------|--------|----------|---------|-----------------|
| TB-Ag85B | P9WQP1 | **OK** | M. tuberculosis | Antigen 85B. Key vaccine target. | tb-ag85b-oracle.edn |
| TB-ESAT-6 | P9WNK7 | **OK** | M. tuberculosis | Early secreted antigen. Virulence. | tb-esat-6-oracle.edn |
| TB-CFP-10 | P9WNK5 | **OK** | M. tuberculosis | Culture filtrate protein. Diagnostic. | tb-cfp-10-oracle.edn |
| TB-KatG | P9WIE5 | **OK** | M. tuberculosis | Catalase-peroxidase. Activates isoniazid. | tb-katg-oracle.edn |
| TB-Hsp65 | P9WMG9 | **WRONG** → P9WPE7 | M. tuberculosis | Chaperonin GroEL 2 (Hsp65). | tb-hsp65-oracle.edn |

### Corrected Accession

| Name | Wrong | Actually was | Correct | Real protein | Residues |
|------|-------|-------------|---------|-------------|----------|
| TB-Hsp65 | P9WMG9 | Unrelated HTH-type regulator Rv0043c | **P9WPE7** | Chaperonin GroEL 2 | 540 |

---

## Mutation Comparisons (Experiment 127)

Source: `dev/experiments/127_pathogen_prescriptions.clj`

| Name | Base Accession | Mutation | Purpose | Oracle Artifact |
|------|---------------|----------|---------|-----------------|
| p53-Normal | P04637 | — | Wild-type guardian. | p53-normal-oracle.edn |
| p53-R175H | P04637 | Arg→His at 175 | Most common cancer mutation. | p53-r175h-oracle.edn |
| HBB-Normal | P68871 | — | Wild-type hemoglobin beta. | hbb-normal-oracle.edn |
| HBB-Sickle | P68871 | Glu→Val at 6 (ל→ט) | Sickle cell disease. | hbb-sickle-oracle.edn |

Manual mutations applied in code. Shared base sequence.

---

## Serpent Gradient (Experiment 128)

Source: `dev/experiments/128_the_serpent_gradient.clj`

Gradient from pathogenic to beneficial. Molecules that affect the mind and body.

| Name | Accession | Organism | Category | Purpose | Oracle Artifact |
|------|-----------|----------|----------|---------|-----------------|
| Prion-PrP | P04156 | Human | Pathogen | Prion protein. Misfolded = disease. | prion-prp-oracle.edn |
| Malaria-CSP | P19597 | P. falciparum | Pathogen | Circumsporozoite protein. Parasite coat. | malaria-csp-oracle.edn |
| Toxoplasma-SAG1 | P13664 | T. gondii | Pathogen | Surface antigen 1. Mind-altering parasite. | toxoplasma-sag1-oracle.edn |
| Candida-Als3 | O74623 | C. albicans | Commensal | Agglutinin-like protein. Commensal→pathogen switch. | candida-als3-oracle.edn |
| Lactobacillus-SlpA | P35829 | L. acidophilus | Beneficial | S-layer protein. Probiotic armor. | lactobacillus-slpa-oracle.edn |
| Tardigrade-Dsup | P0DOW4 | R. varieornatus | Beneficial | Damage suppressor. Shields DNA from radiation. | tardigrade-dsup-oracle.edn |
| THC-THCAS | Q8GTB6 | Cannabis sativa | Entheogen | THCA synthase. Makes THC precursor. | thc-thcas-oracle.edn |
| Psilocybin-PsiM | A0A1Y0UNY6 | P. cubensis | Entheogen | Psilocybin methyltransferase. | psilocybin-psim-oracle.edn |
| Scopolamine-H6H | Q05920 | H. niger | Entheogen | Hyoscyamine 6β-hydroxylase. Tropane alkaloid. | scopolamine-h6h-oracle.edn |
| Mescaline-OMT1 | Q6QNK4 | L. williamsii | Entheogen | O-methyltransferase. Peyote biosynthesis. | mescaline-omt1-oracle.edn |
| Serotonin-TPH2 | Q8IWU9 | Human | Neurotransmitter | Tryptophan hydroxylase 2. Makes serotonin. | serotonin-tph2-oracle.edn |
| Human-INMT | P46597 | Human | Neurotransmitter | Indolethylamine N-methyltransferase. Makes DMT. | human-inmt-oracle.edn |
| Human-Insulin | P01308 | Human | Healing | Insulin precursor. Life and death. | human-insulin-oracle.edn |
| Human-Oxytocin | P01178 | Human | Healing | Oxytocin-neurophysin 1. The love hormone. | human-oxytocin-oracle.edn |

---

## The Four Bloods (Experiment 105)

Source: `dev/experiments/105_the_four_bloods.clj`

Hemoglobin from the four throne-room creatures.

| Name | Accession | Organism | Throne face | Tribe | Cached |
|------|-----------|----------|------------|-------|--------|
| Lion | P18975 | Panthera leo | אריה | Judah | ✓ P18975.fasta |
| Ox | P01966 | Bos taurus | שור | Ephraim | ✓ P01966.fasta |
| Eagle | P01993 | Aquila chrysaetos | נשר | Dan | ✓ P01993.fasta |
| Man | P69905 | Homo sapiens | אדם | Reuben | ✓ P69905.fasta |

---

## Woman & Serpent (Experiment 106)

Source: `dev/experiments/106_has_woman_been_bit_by_the_serpent.clj`

| Name | Accession | Organism | Purpose | Cached |
|------|-----------|----------|---------|--------|
| Estrogen receptor alpha | P03372 | Human | Receptor for female development. | ✓ P03372.fasta |
| hCG beta | P0DN86 | Human | Pregnancy signal. | ✓ P0DN86.fasta |
| Calmodulin | P0DP23 | Human | Contains Eve AND the serpent. | ✓ P0DP23.fasta |
| BRCA1 | P38398 | Human | Guardian. When she fails, cancer. | ✓ P38398.fasta |

---

## Bronze Serpent / Vaccines (Experiment 107)

Source: `dev/experiments/107_the_bronze_serpent.clj`

| Name | Accession | Organism | Purpose | Cached |
|------|-----------|----------|---------|--------|
| SARS-CoV-2 Spike | P0DTC2 | SARS-CoV-2 | COVID-19 spike. 1273 residues. | ✓ P0DTC2.fasta |
| Influenza H1 (1918) | Q9WFX3 | Influenza A | Hemagglutinin. Deadliest flu. | ✓ Q9WFX3.fasta |
| HIV-1 gp160 | P04578 | HIV-1 | Envelope glycoprotein. Hides from immune system. | ✓ P04578.fasta |
| Vaccinia D8 | P21057 | Vaccinia | First vaccine. Cowpox surface protein. | ✓ P21057.fasta |
| IgG1 Fc | P01857 | Human | Antibody. What immune system makes. | ✓ P01857.fasta |

---

## Full Proteome (Genome Voice)

Source: `dev/scripts/genome_voice.clj`

| Dataset | Source | File | Proteins | Letters |
|---------|--------|------|----------|---------|
| Human reference proteome | UniProt UP000005640 | `data/sequences/human-proteome-reviewed.fasta` | 20,442 | 11,415,371 |

Oracle output: `data/dna/genome-voice.edn` (3-letter), `data/dna/genome-4letter.edn` (4-letter)

---

## Cache Locations

| Type | Path | Pattern |
|------|------|---------|
| Sequences | `data/sequences/uniprot/` | `{ACCESSION}.fasta` |
| Full proteome | `data/sequences/` | `human-proteome-reviewed.fasta` |
| Oracle artifacts | `data/dna/` | `{slug}-oracle.edn` |
| Reports | `data/dna/` | `{slug}-report.txt` |
| Genome sweep | `data/dna/` | `genome-voice.edn`, `genome-4letter.edn` |

---

## Accession Correction Summary

6 accessions need fixing. All are in the leprosy/TB subset.

| Label | Wrong | Correct | What the wrong one actually was |
|-------|-------|---------|-------------------------------|
| Leprae-Ag85B | Q49899 | **P31951** | Uncharacterized protein |
| Leprae-SOD | P31952 | **P13367** | Demerged/inactive entry |
| Leprae-MMP-I | P09655 | **P46841** | Rat Spink1 |
| Leprae-Hsp65 | P0A5B4 | **P09239** | M. bovis biosynthesis protein |
| Leprae-Bacterioferritin | P15917 | **P43315** | Anthrax lethal factor |
| TB-Hsp65 | P9WMG9 | **P9WPE7** | Unrelated TB regulator |

**Files needing accession updates:**
- `dev/experiments/123_the_cure_for_leprosy.clj`
- `dev/experiments/127_pathogen_prescriptions.clj`
- `dev/scripts/proteins/play_leprosy.clj`
- `src/selah/dna.clj` (if entries added to library)

After accession correction, oracle artifacts for the 6 affected proteins must be regenerated.

---

## Experiment Index

| Exp | Title | Proteins used |
|-----|-------|--------------|
| 101 | The Map | All 21 core library |
| 105 | The Four Bloods | Hemoglobin: lion, ox, eagle, man |
| 106 | Has Woman Been Bit | Estrogen receptor, hCG, Calmodulin, BRCA1 |
| 107 | The Bronze Serpent | SARS-CoV-2 Spike, Influenza H1, HIV gp160, Vaccinia D8, IgG1 |
| 123 | The Cure for Leprosy | 5 M. leprae proteins (accessions need correction) |
| 127 | Pathogen Prescriptions | 5 TB proteins + p53 normal/R175H + HBB normal/sickle |
| 128 | The Serpent Gradient | 14 proteins: pathogens → entheogens → healing |
| Genome | The Genome Voice | 20,442 human reviewed proteome |
