#!/usr/bin/env python3
"""
Experiment 097-spy: Phonetic Mapping of Torah Letters to DNA Bases

Hypothesis: Map Hebrew letters to DNA bases by phonetic articulation point.
Traditional Hebrew grammar classifies 22 letters into 5 groups.
DNA has 4 bases. Try multiple 5→4 reductions and compare to real genomes.
"""

import json
import os
import re
import sys
from collections import Counter
from pathlib import Path
from math import sqrt

# ─── Paths ───────────────────────────────────────────────────────────────────

SELAH = Path("/home/scott/Projects/selah")
CACHE = SELAH / "data" / "cache" / "sefaria"
GENOMES = SELAH / "data" / "genomes"
OUTPUT = SELAH / "data" / "experiments" / "097"

# ─── Hebrew letter classification ────────────────────────────────────────────

# The 22 Hebrew consonants (no final forms listed separately -- they map to same phoneme)
# Traditional 5-group articulation classification (from Sefer Yetzirah and grammarians):

GUTTURALS  = set("אהחע")           # throat: aleph, he, chet, ayin
LABIALS    = set("בומפ")           # lips: bet, vav, mem, pe
DENTALS    = set("דטלנת")          # tongue/teeth: dalet, tet, lamed, nun, tav
SIBILANTS  = set("זסצשר")          # hissing: zayin, samekh, tsade, shin, resh
PALATALS   = set("גיכק")           # palate: gimel, yod, kaf, qof

# Final forms map to their non-final phoneme
FINAL_TO_NORMAL = {
    'ך': 'כ',  # final kaf → kaf
    'ם': 'מ',  # final mem → mem
    'ן': 'נ',  # final nun → nun
    'ף': 'פ',  # final pe → pe
    'ץ': 'צ',  # final tsade → tsade
}

def normalize_letter(c):
    """Map final forms to their normal form."""
    return FINAL_TO_NORMAL.get(c, c)

# ─── Mapping schemes: 5 articulation groups → 4 DNA bases ────────────────────

# Each scheme is a dict: Hebrew letter → DNA base
# We try multiple biologically-motivated reductions.

def make_mapping(group_to_base):
    """Given a dict mapping group-name to base, produce letter→base mapping."""
    mapping = {}
    groups = {
        'guttural': GUTTURALS,
        'labial': LABIALS,
        'dental': DENTALS,
        'sibilant': SIBILANTS,
        'palatal': PALATALS,
    }
    for gname, letters in groups.items():
        base = group_to_base[gname]
        for L in letters:
            mapping[L] = base
    return mapping

# Scheme 1: Merge gutturals + palatals (both "back of mouth") → A
# Rationale: gutturals (throat) and palatals (palate) are both posterior articulation
SCHEME_1 = make_mapping({
    'guttural': 'A', 'palatal': 'A',   # back-of-mouth → Adenine
    'labial': 'T',                       # lips → Thymine
    'dental': 'G',                       # tongue/teeth → Guanine
    'sibilant': 'C',                     # hissing → Cytosine
})
SCHEME_1_NAME = "guttural+palatal=A, labial=T, dental=G, sibilant=C"

# Scheme 2: Merge sibilants + dentals (both tongue-involved)
SCHEME_2 = make_mapping({
    'guttural': 'A',                     # throat → Adenine
    'labial': 'T',                       # lips → Thymine
    'dental': 'G', 'sibilant': 'G',     # tongue → Guanine
    'palatal': 'C',                      # palate → Cytosine
})
SCHEME_2_NAME = "guttural=A, labial=T, dental+sibilant=G, palatal=C"

# Scheme 3: Merge gutturals + palatals → T, optimize for E. coli balance
SCHEME_3 = make_mapping({
    'guttural': 'T', 'palatal': 'T',    # back → Thymine
    'labial': 'A',                       # lips → Adenine
    'dental': 'C',                       # tongue/teeth → Cytosine
    'sibilant': 'G',                     # hissing → Guanine
})
SCHEME_3_NAME = "guttural+palatal=T, labial=A, dental=C, sibilant=G"

# Scheme 4: Merge labials + palatals (both involve mouth closure)
SCHEME_4 = make_mapping({
    'guttural': 'A',                     # throat → Adenine
    'labial': 'T', 'palatal': 'T',      # closure → Thymine
    'dental': 'G',                       # tongue → Guanine
    'sibilant': 'C',                     # hissing → Cytosine
})
SCHEME_4_NAME = "guttural=A, labial+palatal=T, dental=G, sibilant=C"

# Scheme 5: Purine/pyrimidine logic — try to match Chargaff's rules
# In real DNA: A≈T and G≈C (Chargaff pairing)
# Hebrew groups by size: gutturals(4), labials(4), dentals(5), sibilants(5), palatals(4)
# To get A≈T: need ~equal-sized merged groups for A and T
# gutturals(4)=A, palatals(4)=T → each ~22% of distinct letters
# dentals(5)=G, sibilants(5)+labials(4) won't work...
# Try: gutturals(4)=A, labials(4)=T, then split the remaining 14 letters
# Actually let's just try swapping the base assignments for scheme 1
SCHEME_5 = make_mapping({
    'guttural': 'A', 'palatal': 'A',    # back → Adenine
    'labial': 'C',                       # lips → Cytosine
    'dental': 'T',                       # tongue → Thymine
    'sibilant': 'G',                     # hissing → Guanine
})
SCHEME_5_NAME = "guttural+palatal=A, labial=C, dental=T, sibilant=G"

# Scheme 6: Kabbalistic "mother letters" alignment
# Aleph(א)=air, Mem(מ)=water, Shin(ש)=fire — the 3 mothers
# Map mothers to specific bases, fill in rest
SCHEME_6 = make_mapping({
    'guttural': 'A',                     # throat (aleph=air) → Adenine
    'labial': 'C',                       # lips (mem=water) → Cytosine
    'dental': 'G',                       # tongue → Guanine
    'sibilant': 'T',                     # hissing (shin=fire) → Thymine
    'palatal': 'G',                      # palate → merge with dental
})
SCHEME_6_NAME = "guttural=A, labial=C, dental+palatal=G, sibilant=T"

# Scheme 7: Try to maximize balance (closest to 25% each)
# Group sizes by letter count in Torah (approximate):
# Gutturals: א(27060)+ה(28055)+ח(7407)+ע(11250) = 73,772 (24.2%)
# Labials: ב(16345)+ו(30533)+מ(14466+10624)+פ(4850+3280) = ~80,098 (26.3%)
# Dentals: ד(7032)+ט(2930)+ל(21570)+נ(9867+5765)+ת(17950) = ~65,114 (21.4%)
# Sibilants: ז(3837)+ס(2660)+צ(4285+2140)+ש(15595)+ר(18125) = ~46,642 (15.3%)
# Palatals: ג(2109)+י(31556)+כ(10043+6220)+ק(5765) = ~55,693 (18.3%)
# So gutturals are already close to 25%!
# Dental+sibilant would be ~36.7% — too much
# Palatal+sibilant = ~33.6% — still high
# Best balance: keep gutturals alone (24.2%), keep labials alone (26.3%)
#   dental(21.4%) and sibilant(15.3%) and palatal(18.3%)
#   dental alone = 21.4% → close to 25%
#   sibilant+palatal = 33.6% → too high
# Hmm. Let's try: guttural=A(24.2%), labial=T(26.3%), dental=G(21.4%), sib+pal=C(33.6%)
# Or: split the big merged group differently
# Best: guttural=A, sibilant+palatal=T, dental=G, labial=C
SCHEME_7 = make_mapping({
    'guttural': 'A',                     # 24.2%
    'sibilant': 'T', 'palatal': 'T',    # 15.3+18.3 = 33.6%
    'dental': 'G',                       # 21.4%
    'labial': 'C',                       # 26.3%
})
SCHEME_7_NAME = "guttural=A, sibilant+palatal=T, dental=G, labial=C"

# Scheme 8: Optimize for Chargaff pairing (A%≈T%, G%≈C%)
# We need two groups with similar total freq for A/T, two for G/C
# guttural(24.2%) ↔ dental(21.4%) → A≈T with 24.2:21.4
# labial(26.3%) ↔ sibilant+palatal(33.6%) → G≈C with 26.3:33.6  (not great)
# OR: guttural(24.2%) ↔ labial(26.3%) → A≈T (good!)
#     dental(21.4%) ↔ sibilant+palatal(33.6%) → nope
# Better: guttural(24.2%) ↔ dental(21.4%)
#         labial(26.3%) ↔ palatal(18.3%) — 26.3:18.3 nope
# Hmm. No perfect match because the 5 groups are unequal.
# The best Chargaff pairing is guttural≈labial (24.2:26.3)
SCHEME_8 = make_mapping({
    'guttural': 'A',                     # 24.2% → pair with T
    'labial': 'T',                       # 26.3% → pair with A
    'dental': 'G',                       # 21.4% → pair with C
    'sibilant': 'C', 'palatal': 'C',    # 15.3+18.3=33.6% → pair with G (poor)
})
SCHEME_8_NAME = "guttural=A, labial=T, dental=G, sibilant+palatal=C [Chargaff attempt]"

ALL_SCHEMES = [
    ("scheme-1", SCHEME_1, SCHEME_1_NAME),
    ("scheme-2", SCHEME_2, SCHEME_2_NAME),
    ("scheme-3", SCHEME_3, SCHEME_3_NAME),
    ("scheme-4", SCHEME_4, SCHEME_4_NAME),
    ("scheme-5", SCHEME_5, SCHEME_5_NAME),
    ("scheme-6", SCHEME_6, SCHEME_6_NAME),
    ("scheme-7", SCHEME_7, SCHEME_7_NAME),
    ("scheme-8", SCHEME_8, SCHEME_8_NAME),
]


# ─── Torah text extraction ───────────────────────────────────────────────────

BOOKS = [
    ("genesis", 50),
    ("exodus", 40),
    ("leviticus", 27),
    ("numbers", 36),
    ("deuteronomy", 34),
]

def extract_torah_letters():
    """Extract all Hebrew consonant letters from cached Sefaria Torah text."""
    letters = []
    for book, num_chapters in BOOKS:
        for ch in range(1, num_chapters + 1):
            path = CACHE / f"{book}-{ch}.json"
            with open(path) as f:
                data = json.load(f)
            he_verses = data.get("he", [])
            for verse in he_verses:
                if isinstance(verse, list):
                    # Some chapters have nested structure
                    for v in verse:
                        text = re.sub(r'<[^>]+>', '', str(v))
                        for c in text:
                            if '\u05D0' <= c <= '\u05EA':
                                letters.append(c)
                else:
                    text = re.sub(r'<[^>]+>', '', str(verse))
                    for c in text:
                        if '\u05D0' <= c <= '\u05EA':
                            letters.append(c)
    return letters


def read_fasta(path):
    """Read a FASTA file and return the sequence as a string."""
    seq = []
    with open(path) as f:
        for line in f:
            if line.startswith('>'):
                continue
            seq.append(line.strip().upper())
    return ''.join(seq)


def base_frequencies(seq):
    """Compute base frequencies for a DNA sequence."""
    counts = Counter(seq)
    total = len(seq)
    freqs = {}
    for base in 'ACGT':
        freqs[base] = counts.get(base, 0) / total if total > 0 else 0
    return freqs


def freq_distance(f1, f2):
    """Euclidean distance between two frequency dicts."""
    return sqrt(sum((f1.get(b, 0) - f2.get(b, 0))**2 for b in 'ACGT'))


def chargaff_deviation(freqs):
    """How far from Chargaff's rules: |A%-T%| + |G%-C%|."""
    return abs(freqs['A'] - freqs['T']) + abs(freqs['G'] - freqs['C'])


def apply_mapping(letters, scheme):
    """Convert Hebrew letters to DNA bases using a mapping scheme."""
    seq = []
    unmapped = 0
    for L in letters:
        norm = normalize_letter(L)
        base = scheme.get(norm)
        if base:
            seq.append(base)
        else:
            unmapped += 1
    if unmapped > 0:
        print(f"  WARNING: {unmapped} unmapped letters")
    return ''.join(seq)


def compute_kmers(seq, k):
    """Extract all k-mers from a sequence, returning a set."""
    kmers = set()
    for i in range(len(seq) - k + 1):
        kmers.add(seq[i:i+k])
    return kmers


def kmer_positions(seq, k):
    """Extract all k-mers with their positions."""
    positions = {}
    for i in range(len(seq) - k + 1):
        kmer = seq[i:i+k]
        if kmer not in positions:
            positions[kmer] = []
        positions[kmer].append(i)
    return positions


def find_shared_kmers(seq1, seq2, k):
    """Find k-mers shared between two sequences."""
    kmers1 = compute_kmers(seq1, k)
    kmers2 = compute_kmers(seq2, k)
    return kmers1 & kmers2


def longest_exact_match(seq1, seq2, min_k=10, max_k=30):
    """Find the longest exact match between two sequences by binary-ish search."""
    best_k = 0
    best_shared = set()

    for k in range(min_k, max_k + 1):
        shared = find_shared_kmers(seq1, seq2, k)
        if shared:
            best_k = k
            best_shared = shared
        else:
            break

    return best_k, best_shared


def expected_shared_kmers(len1, len2, k, alphabet_size=4):
    """Expected number of shared k-mers between two random sequences."""
    # Number of distinct k-mers possible
    total_possible = alphabet_size ** k
    # Probability that a specific k-mer appears in seq of length L
    # Approx: 1 - (1 - 1/total_possible)^(L-k+1)
    n1 = len1 - k + 1
    n2 = len2 - k + 1
    # Expected shared ≈ total_possible * P(kmer in seq1) * P(kmer in seq2)
    # For uniform random: P(kmer in seq) ≈ n/total_possible for n << total_possible
    if total_possible == 0:
        return 0
    p1 = min(1.0, n1 / total_possible)
    p2 = min(1.0, n2 / total_possible)
    return total_possible * p1 * p2


def dinucleotide_frequencies(seq):
    """Compute dinucleotide (2-mer) frequencies."""
    counts = Counter()
    for i in range(len(seq) - 1):
        di = seq[i:i+2]
        counts[di] += 1
    total = sum(counts.values())
    freqs = {}
    for di, count in counts.most_common():
        freqs[di] = count / total
    return freqs


def dinucleotide_bias(seq):
    """Compute dinucleotide bias: observed/expected ratio for each dinucleotide.
    Expected = freq(base1) * freq(base2). Bias > 1 means overrepresented."""
    bf = base_frequencies(seq)
    df = dinucleotide_frequencies(seq)
    bias = {}
    for b1 in 'ACGT':
        for b2 in 'ACGT':
            di = b1 + b2
            expected = bf[b1] * bf[b2]
            observed = df.get(di, 0)
            bias[di] = observed / expected if expected > 0 else 0
    return bias


def gc_content(seq):
    """GC content of a DNA sequence."""
    gc = sum(1 for b in seq if b in 'GC')
    return gc / len(seq) if seq else 0


# ─── Main analysis ───────────────────────────────────────────────────────────

def main():
    print("=" * 70)
    print("EXPERIMENT 097-SPY: PHONETIC MAPPING — TORAH LETTERS → DNA BASES")
    print("=" * 70)

    # ── Step 1: Extract Torah letters ──
    print("\n[1] Extracting Torah letters from cached Sefaria text...")
    torah_letters = extract_torah_letters()
    total_letters = len(torah_letters)
    print(f"    Total Hebrew consonants: {total_letters:,}")

    # Verify letter count
    if abs(total_letters - 304850) > 100:
        print(f"    WARNING: Expected ~304,850 letters, got {total_letters}")

    # Letter frequency
    letter_freq = Counter(torah_letters)
    print(f"    Distinct letters: {len(letter_freq)}")
    print("    Top 10 by frequency:")
    for letter, count in letter_freq.most_common(10):
        print(f"      {letter}: {count:,} ({100*count/total_letters:.2f}%)")

    # ── Step 2: Read real genomes ──
    print("\n[2] Reading real genome sequences...")
    genomes = {}
    for fname in ["ecoli_306k.fasta", "human_chr1_306k.fasta", "human_mitochondria.fasta"]:
        path = GENOMES / fname
        if path.exists():
            seq = read_fasta(path)
            name = fname.replace('.fasta', '')
            genomes[name] = seq
            bf = base_frequencies(seq)
            gc = gc_content(seq)
            print(f"    {name}: {len(seq):,} bases, GC={100*gc:.1f}%")
            print(f"      A={100*bf['A']:.1f}% C={100*bf['C']:.1f}% G={100*bf['G']:.1f}% T={100*bf['T']:.1f}%")
        else:
            print(f"    {fname}: NOT FOUND")

    # ── Step 3: Apply all mapping schemes ──
    print("\n[3] Applying phonetic mapping schemes...")
    print("-" * 70)

    results = {}
    for scheme_id, scheme, scheme_name in ALL_SCHEMES:
        print(f"\n  {scheme_id}: {scheme_name}")
        torah_dna = apply_mapping(torah_letters, scheme)
        tf = base_frequencies(torah_dna)
        gc = gc_content(torah_dna)
        cd = chargaff_deviation(tf)
        print(f"    Length: {len(torah_dna):,} bases")
        print(f"    A={100*tf['A']:.1f}% C={100*tf['C']:.1f}% G={100*tf['G']:.1f}% T={100*tf['T']:.1f}%")
        print(f"    GC={100*gc:.1f}%  Chargaff deviation={cd:.4f}")

        # Distance to each real genome
        distances = {}
        for gname, gseq in genomes.items():
            gf = base_frequencies(gseq)
            d = freq_distance(tf, gf)
            distances[gname] = d
            print(f"    Distance to {gname}: {d:.4f}")

        results[scheme_id] = {
            'name': scheme_name,
            'torah_dna': torah_dna,
            'base_freqs': tf,
            'gc_content': gc,
            'chargaff_deviation': cd,
            'distances': distances,
        }

    # ── Step 4: Find best matches ──
    print("\n" + "=" * 70)
    print("[4] RANKING — Best frequency matches to real genomes")
    print("=" * 70)

    for gname in genomes:
        print(f"\n  Closest to {gname}:")
        ranked = sorted(results.items(), key=lambda x: x[1]['distances'][gname])
        for i, (sid, r) in enumerate(ranked[:3]):
            print(f"    #{i+1} {sid} (d={r['distances'][gname]:.4f}): {r['name']}")

    # Best Chargaff
    print(f"\n  Best Chargaff pairing (|A-T|+|G-C| closest to 0):")
    ranked_chargaff = sorted(results.items(), key=lambda x: x[1]['chargaff_deviation'])
    for i, (sid, r) in enumerate(ranked_chargaff[:3]):
        print(f"    #{i+1} {sid} (dev={r['chargaff_deviation']:.4f}): {r['name']}")

    # Overall best: closest to E. coli (most balanced genome)
    best_ecoli_id = min(results, key=lambda x: results[x]['distances'].get('ecoli_306k', 999))
    best_ecoli = results[best_ecoli_id]
    print(f"\n  BEST OVERALL (closest to E.coli): {best_ecoli_id}")
    print(f"    {best_ecoli['name']}")

    # ── Step 5: K-mer analysis for top schemes ──
    print("\n" + "=" * 70)
    print("[5] K-MER ALIGNMENT ANALYSIS")
    print("=" * 70)

    # Analyze top 3 schemes (closest to E. coli)
    top_schemes = sorted(results.items(), key=lambda x: x[1]['distances'].get('ecoli_306k', 999))[:3]

    kmer_results = {}
    for scheme_id, scheme_data in top_schemes:
        torah_dna = scheme_data['torah_dna']
        print(f"\n  {scheme_id}: {scheme_data['name']}")
        print(f"  {'─' * 60}")

        scheme_kmer_results = {}
        for gname, gseq in genomes.items():
            print(f"\n    vs {gname}:")

            # Find longest exact match
            best_k, best_shared = longest_exact_match(torah_dna, gseq, min_k=6, max_k=25)
            expected = expected_shared_kmers(len(torah_dna), len(gseq), best_k) if best_k > 0 else 0

            if best_k > 0:
                print(f"      Longest exact match: {best_k}-mers")
                print(f"      Shared {best_k}-mers: {len(best_shared):,}")
                print(f"      Expected (random): {expected:.1f}")
                ratio = len(best_shared) / expected if expected > 0 else float('inf')
                print(f"      Observed/Expected: {ratio:.2f}x")

                # Show some examples
                examples = list(best_shared)[:5]
                print(f"      Examples: {', '.join(examples)}")
            else:
                print(f"      No matches found at k>=6")

            # Also check standard k values
            for k in [6, 8, 10, 12, 14, 16]:
                shared = find_shared_kmers(torah_dna, gseq, k)
                exp = expected_shared_kmers(len(torah_dna), len(gseq), k)
                if len(shared) > 0:
                    ratio = len(shared) / exp if exp > 0 else float('inf')
                    print(f"      k={k:2d}: {len(shared):>8,} shared (expected {exp:>10.1f}, ratio {ratio:.2f}x)")
                else:
                    print(f"      k={k:2d}: 0 shared")
                    break

            scheme_kmer_results[gname] = {
                'longest_match': best_k,
                'shared_at_longest': len(best_shared),
                'expected_at_longest': expected,
            }

        kmer_results[scheme_id] = scheme_kmer_results

    # ── Step 6: Dinucleotide analysis ──
    print("\n" + "=" * 70)
    print("[6] DINUCLEOTIDE BIAS ANALYSIS")
    print("=" * 70)

    # Compare dinucleotide biases between Torah-DNA and real genomes
    best_id = top_schemes[0][0]
    best_dna = results[best_id]['torah_dna']

    torah_bias = dinucleotide_bias(best_dna)
    print(f"\n  Using {best_id} Torah-DNA:")
    print(f"  {'Dinuc':>5}  {'Torah':>7}  ", end="")
    for gname in genomes:
        print(f"  {gname[:8]:>8}", end="")
    print()

    genome_biases = {}
    for gname, gseq in genomes.items():
        genome_biases[gname] = dinucleotide_bias(gseq)

    for b1 in 'ACGT':
        for b2 in 'ACGT':
            di = b1 + b2
            print(f"  {di:>5}  {torah_bias[di]:>7.3f}  ", end="")
            for gname in genomes:
                print(f"  {genome_biases[gname][di]:>8.3f}", end="")
            print()

    # Dinucleotide distance
    print("\n  Dinucleotide distance (Euclidean):")
    for gname in genomes:
        dist = sqrt(sum((torah_bias[di] - genome_biases[gname][di])**2
                       for b1 in 'ACGT' for b2 in 'ACGT' for di in [b1+b2]))
        print(f"    {best_id} vs {gname}: {dist:.4f}")

    # ── Step 7: Codon (triplet) analysis ──
    print("\n" + "=" * 70)
    print("[7] TRIPLET (CODON) FREQUENCY ANALYSIS")
    print("=" * 70)

    best_dna = results[best_id]['torah_dna']
    # Extract codons from Torah-DNA (reading frame 0)
    torah_codons = Counter()
    for i in range(0, len(best_dna) - 2, 3):
        codon = best_dna[i:i+3]
        torah_codons[codon] += 1

    # Same for E. coli
    ecoli_seq = genomes.get('ecoli_306k', '')
    ecoli_codons = Counter()
    for i in range(0, len(ecoli_seq) - 2, 3):
        codon = ecoli_seq[i:i+3]
        ecoli_codons[codon] += 1

    all_codons = sorted(set(list(torah_codons.keys()) + list(ecoli_codons.keys())))
    torah_total = sum(torah_codons.values())
    ecoli_total = sum(ecoli_codons.values())

    print(f"\n  Total codons: Torah={torah_total:,}, E.coli={ecoli_total:,}")
    print(f"\n  {'Codon':>5}  {'Torah%':>8}  {'Ecoli%':>8}  {'Ratio':>8}")
    for codon in sorted(all_codons, key=lambda c: torah_codons.get(c, 0), reverse=True)[:20]:
        tf = 100 * torah_codons.get(codon, 0) / torah_total
        ef = 100 * ecoli_codons.get(codon, 0) / ecoli_total
        ratio = tf / ef if ef > 0 else float('inf')
        print(f"  {codon:>5}  {tf:>8.2f}  {ef:>8.2f}  {ratio:>8.2f}")

    # Stop codons in Torah-DNA
    stop_codons = ['TAA', 'TAG', 'TGA']
    print(f"\n  Stop codon frequencies:")
    for sc in stop_codons:
        tf = 100 * torah_codons.get(sc, 0) / torah_total
        ef = 100 * ecoli_codons.get(sc, 0) / ecoli_total if ecoli_total > 0 else 0
        print(f"    {sc}: Torah={tf:.2f}%, E.coli={ef:.2f}%")

    # ── Step 8: Information entropy ──
    print("\n" + "=" * 70)
    print("[8] INFORMATION CONTENT")
    print("=" * 70)

    from math import log2

    def entropy(seq):
        """Shannon entropy in bits per base."""
        counts = Counter(seq)
        total = len(seq)
        h = 0
        for c, n in counts.items():
            p = n / total
            if p > 0:
                h -= p * log2(p)
        return h

    def entropy_windowed(seq, window=1000, step=500):
        """Compute entropy in sliding windows."""
        entropies = []
        for i in range(0, len(seq) - window, step):
            w = seq[i:i+window]
            entropies.append(entropy(w))
        return entropies

    torah_entropy = entropy(best_dna)
    print(f"\n  Shannon entropy (bits/base):")
    print(f"    Torah ({best_id}): {torah_entropy:.4f}")
    for gname, gseq in genomes.items():
        ge = entropy(gseq)
        print(f"    {gname}: {ge:.4f}")
    print(f"    Max possible (uniform): {log2(4):.4f}")

    # ── Step 9: Summary statistics ──
    print("\n" + "=" * 70)
    print("[9] SUMMARY")
    print("=" * 70)

    summary = {
        'total_letters': total_letters,
        'schemes': {},
        'best_ecoli_match': best_ecoli_id,
        'best_chargaff': ranked_chargaff[0][0],
    }

    for sid, r in results.items():
        summary['schemes'][sid] = {
            'name': r['name'],
            'base_freqs': {k: round(v, 6) for k, v in r['base_freqs'].items()},
            'gc_content': round(r['gc_content'], 6),
            'chargaff_deviation': round(r['chargaff_deviation'], 6),
            'distances': {k: round(v, 6) for k, v in r['distances'].items()},
        }

    summary['kmer_analysis'] = {}
    for sid, kr in kmer_results.items():
        summary['kmer_analysis'][sid] = {}
        for gname, data in kr.items():
            summary['kmer_analysis'][sid][gname] = data

    summary['entropy'] = {
        'torah': round(torah_entropy, 4),
    }
    for gname, gseq in genomes.items():
        summary['entropy'][gname] = round(entropy(gseq), 4)

    # ── Save results ──
    print("\n[10] Saving results...")

    # Format as EDN-like (Python dicts → EDN-ish)
    def to_edn(obj, indent=0):
        """Convert Python obj to EDN-like string."""
        pad = "  " * indent
        if isinstance(obj, dict):
            if not obj:
                return "{}"
            lines = ["{"]
            for k, v in obj.items():
                key_str = f':{k}' if isinstance(k, str) and k.isidentifier() else f'"{k}"'
                val_str = to_edn(v, indent + 1)
                lines.append(f"{pad}  {key_str} {val_str}")
            lines.append(f"{pad}}}")
            return "\n".join(lines)
        elif isinstance(obj, (list, tuple)):
            items = [to_edn(x, indent + 1) for x in obj]
            return "[" + " ".join(items) + "]"
        elif isinstance(obj, float):
            return f"{obj}"
        elif isinstance(obj, int):
            return str(obj)
        elif isinstance(obj, str):
            return f'"{obj}"'
        elif isinstance(obj, bool):
            return "true" if obj else "false"
        else:
            return str(obj)

    edn_path = OUTPUT / "spy1-phonetic-mapping.edn"
    with open(edn_path, 'w') as f:
        f.write(";; Experiment 097-spy: Phonetic Mapping of Torah Letters to DNA Bases\n")
        f.write(";; Generated by dev/scripts/spy_phonetic_genome.py\n\n")
        f.write(to_edn(summary))
        f.write("\n")
    print(f"    Saved: {edn_path}")

    # ── Honest assessment ──
    print("\n" + "=" * 70)
    print("HONEST ASSESSMENT")
    print("=" * 70)

    print("""
  The key question: does the Torah, read as DNA through phonetic mapping,
  show any structural similarity to real genomes beyond what we'd expect
  from frequency matching alone?

  What we CAN match: base frequencies.
    With 5 articulation groups and 4 bases, we have enough degrees of freedom
    to get reasonably close to real genome frequencies. This is not surprising
    — it's essentially a bin-packing exercise.

  What would be SURPRISING:
    - Shared k-mers beyond random expectation
    - Similar dinucleotide biases (indicating similar local structure)
    - Meaningful reading frames (ORFs, codon bias)
    - Alignment to known genes

  The 22→4 mapping is fundamentally lossy — it destroys most of the
  information in the Hebrew text. Multiple different Hebrew words map to
  the same DNA sequence. This means any "alignment" found could be
  coincidental rather than structural.

  The phonetic grouping itself is linguistically grounded (Sefer Yetzirah,
  medieval grammarians). But the assignment of groups to bases is arbitrary.
  With 8 schemes × 3 genomes = 24 comparisons, we have substantial
  multiple-testing burden.
""")

    # Print final verdict
    best = top_schemes[0]
    best_dist = best[1]['distances'].get('ecoli_306k', 999)
    print(f"  Best frequency match: {best[0]}")
    print(f"    Distance to E. coli: {best_dist:.4f}")
    print(f"    GC content: {100*best[1]['gc_content']:.1f}%")
    charg = best[1]['chargaff_deviation']
    print(f"    Chargaff deviation: {charg:.4f}")
    if charg > 0.10:
        print(f"    → FAILS Chargaff's rules (deviation > 10%)")
        print(f"      Real DNA has |A-T| + |G-C| ≈ 0 (complementary pairing)")
        print(f"      Torah-DNA has {charg:.4f} — no complementary strand structure")
    else:
        print(f"    → Moderately satisfies Chargaff's rules")

    return summary


if __name__ == "__main__":
    summary = main()
