#!/usr/bin/env python3
"""
Spy4: Codon Alignment Analysis
Hypothesis: The Torah read in triplets encodes a codon-like structure.
"""

import json
import os
import re
import sys
from collections import Counter, defaultdict
from pathlib import Path
import math

ROOT = Path("/home/scott/Projects/selah")
CACHE = ROOT / "data" / "cache" / "sefaria"
OUT = ROOT / "data" / "experiments" / "097"

# Hebrew letter range
ALEPH = 0x05D0  # א
TAV = 0x05EA    # ת
# Final forms
FINAL_KAF = 0x05DA
FINAL_MEM = 0x05DC  # actually 05DD
FINAL_NUN = 0x05DF
FINAL_PE = 0x05E3
FINAL_TSADI = 0x05E5

# Standard gematria values
GEMATRIA = {
    'א': 1, 'ב': 2, 'ג': 3, 'ד': 4, 'ה': 5, 'ו': 6, 'ז': 7, 'ח': 8, 'ט': 9,
    'י': 10, 'כ': 20, 'ל': 30, 'מ': 40, 'נ': 50, 'ס': 60, 'ע': 70, 'פ': 80, 'צ': 90,
    'ק': 100, 'ר': 200, 'ש': 300, 'ת': 400,
    # Final forms — same value as non-final
    'ך': 20, 'ם': 40, 'ן': 50, 'ף': 80, 'ץ': 90
}

BOOKS = [
    ("genesis", 50),
    ("exodus", 40),
    ("leviticus", 27),
    ("numbers", 36),
    ("deuteronomy", 34),
]

def is_hebrew_letter(ch):
    cp = ord(ch)
    return ALEPH <= cp <= TAV

def extract_letters(text):
    """Extract only Hebrew letters from text, stripping HTML, niqqud, cantillation."""
    # Remove HTML tags
    text = re.sub(r'<[^>]+>', '', text)
    return [ch for ch in text if is_hebrew_letter(ch)]

def load_torah():
    """Load entire Torah as a list of Hebrew letters."""
    letters = []
    for book, chapters in BOOKS:
        for ch in range(1, chapters + 1):
            fpath = CACHE / f"{book}-{ch}.json"
            with open(fpath) as f:
                data = json.load(f)
            for verse in data['he']:
                if isinstance(verse, str):
                    letters.extend(extract_letters(verse))
                elif isinstance(verse, list):
                    for v in verse:
                        letters.extend(extract_letters(v))
    return letters

def triplet_gv(triplet):
    """Compute gematria value of a 3-letter triplet."""
    return sum(GEMATRIA.get(ch, 0) for ch in triplet)

def make_triplets(letters, offset=0):
    """Convert letter list to triplets starting at given offset."""
    n = (len(letters) - offset) // 3
    return [tuple(letters[offset + i*3 : offset + i*3 + 3]) for i in range(n)]

# ============================================================
# PART 1: Load Torah
# ============================================================
print("=" * 60)
print("PART 1: Loading Torah text")
print("=" * 60)
letters = load_torah()
print(f"Total Hebrew letters: {len(letters)}")
print(f"Expected: 304,805 (some variance from normalization)")
print(f"First 20 letters: {''.join(letters[:20])}")

# ============================================================
# PART 2: Triplet vocabulary analysis
# ============================================================
print("\n" + "=" * 60)
print("PART 2: Triplet vocabulary analysis")
print("=" * 60)

triplets_f0 = make_triplets(letters, 0)
triplets_f1 = make_triplets(letters, 1)
triplets_f2 = make_triplets(letters, 2)

print(f"Frame 0: {len(triplets_f0)} triplets")
print(f"Frame 1: {len(triplets_f1)} triplets")
print(f"Frame 2: {len(triplets_f2)} triplets")

# Count distinct triplets in each frame
for frame_idx, triplets in enumerate([triplets_f0, triplets_f1, triplets_f2]):
    counts = Counter(triplets)
    print(f"\nFrame {frame_idx}:")
    print(f"  Distinct triplets: {len(counts)}")
    print(f"  Possible (22^3): {22**3}")
    print(f"  Coverage: {len(counts)/22**3*100:.1f}%")

    # Distribution analysis
    freq_values = sorted(counts.values(), reverse=True)
    print(f"  Max frequency: {freq_values[0]} ({''.join(counts.most_common(1)[0][0])})")
    print(f"  Min frequency: {freq_values[-1]}")
    print(f"  Median frequency: {freq_values[len(freq_values)//2]}")

    # Top 20
    print(f"  Top 20 triplets:")
    for trip, cnt in counts.most_common(20):
        gv = triplet_gv(trip)
        print(f"    {''.join(trip)} (GV={gv}): {cnt}")

# Use frame 0 for main analysis
counts_f0 = Counter(triplets_f0)

# ============================================================
# PART 3: Natural break points — is there a 64-codon structure?
# ============================================================
print("\n" + "=" * 60)
print("PART 3: Natural break points in triplet distribution")
print("=" * 60)

freq_sorted = sorted(counts_f0.values(), reverse=True)

# Look at frequency ratios between consecutive ranks
print("\nFrequency at key ranks:")
for rank in [1, 10, 20, 30, 40, 50, 60, 61, 64, 70, 80, 100, 150, 200, 500, 1000, 2000, 5000, len(freq_sorted)]:
    if rank <= len(freq_sorted):
        print(f"  Rank {rank:5d}: freq = {freq_sorted[rank-1]:5d}")

# Look for the biggest drop between consecutive ranks
print("\nLargest frequency drops (possible natural breakpoints):")
drops = []
for i in range(1, min(len(freq_sorted), 500)):
    drop = freq_sorted[i-1] - freq_sorted[i]
    ratio = freq_sorted[i-1] / max(freq_sorted[i], 1)
    drops.append((i+1, drop, ratio, freq_sorted[i-1], freq_sorted[i]))

# Sort by drop size
drops_by_size = sorted(drops, key=lambda x: x[1], reverse=True)[:20]
print("  By absolute drop:")
for rank, drop, ratio, prev, curr in drops_by_size:
    print(f"    Between rank {rank-1} and {rank}: {prev} -> {curr} (drop={drop}, ratio={ratio:.2f})")

# Sort by ratio
drops_by_ratio = sorted(drops, key=lambda x: x[2], reverse=True)[:20]
print("  By ratio:")
for rank, drop, ratio, prev, curr in drops_by_ratio:
    print(f"    Between rank {rank-1} and {rank}: {prev} -> {curr} (drop={drop}, ratio={ratio:.2f})")

# Zipf's law check
print("\nZipf's law check (log-log linearity):")
import math
for i in [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096]:
    if i <= len(freq_sorted):
        print(f"  log(rank={i}) = {math.log(i):.2f}, log(freq={freq_sorted[i-1]}) = {math.log(max(freq_sorted[i-1],1)):.2f}")

# ============================================================
# PART 3b: GV-based grouping
# ============================================================
print("\n" + "=" * 60)
print("PART 3b: GV-based grouping of triplets")
print("=" * 60)

# Group triplets by their GV
gv_groups = defaultdict(list)
for trip, cnt in counts_f0.items():
    gv = triplet_gv(trip)
    gv_groups[gv].append((trip, cnt))

print(f"Distinct GV values from all triplets: {len(gv_groups)}")

# The maximum GV for a triplet: 400+400+400=1200
# The minimum: 1+1+1=3
gv_values = sorted(gv_groups.keys())
print(f"GV range: {gv_values[0]} to {gv_values[-1]}")

# How many GVs have exactly 1, 2, 3, ... triplets?
degeneracy = Counter(len(v) for v in gv_groups.values())
print(f"\nDegeneracy distribution (how many GV values have N distinct triplets):")
for n_trips, count in sorted(degeneracy.items()):
    print(f"  {n_trips} triplets: {count} GV values")

# Total frequency by GV
gv_total_freq = {gv: sum(c for _, c in trips) for gv, trips in gv_groups.items()}
gv_freq_sorted = sorted(gv_total_freq.items(), key=lambda x: x[1], reverse=True)

print(f"\nTop 30 GVs by total frequency:")
for gv, freq in gv_freq_sorted[:30]:
    n_trips = len(gv_groups[gv])
    print(f"  GV={gv:4d}: total_freq={freq:6d}, {n_trips} distinct triplets")

# ============================================================
# PART 4: Cross-beam analysis
# ============================================================
print("\n" + "=" * 60)
print("PART 4: Cross-beam (67-letter d-axis at center)")
print("=" * 60)

# The center is at position (3,25,6,33) in the 7x50x13x67 space
# position = a*43550 + b*871 + c*67 + d
# Cross-beam = all d values at (3,25,6,*) = positions 3*43550 + 25*871 + 6*67 + d for d in 0..66
center_a, center_b, center_c = 3, 25, 6
crossbeam_positions = [center_a * 43550 + center_b * 871 + center_c * 67 + d for d in range(67)]

print(f"Cross-beam starts at position {crossbeam_positions[0]}, ends at {crossbeam_positions[-1]}")

# Verify these are within range
print(f"Torah length: {len(letters)}")
if crossbeam_positions[-1] < len(letters):
    crossbeam_letters = [letters[p] for p in crossbeam_positions]
    print(f"Cross-beam letters (67): {''.join(crossbeam_letters)}")

    # Make 22 triplets + 1 remainder
    crossbeam_triplets = [tuple(crossbeam_letters[i*3:i*3+3]) for i in range(22)]
    remainder = crossbeam_letters[66] if len(crossbeam_letters) > 66 else None

    print(f"\n22 cross-beam triplets:")
    crossbeam_gvs = []
    for i, trip in enumerate(crossbeam_triplets):
        gv = triplet_gv(trip)
        crossbeam_gvs.append(gv)
        in_torah = counts_f0.get(trip, 0)
        print(f"  Codon {i+1:2d}: {''.join(trip)} GV={gv:4d}  freq_in_torah={in_torah}")

    if remainder:
        print(f"  Remainder: {remainder} (GV={GEMATRIA.get(remainder, 0)})")

    print(f"\nCross-beam GV sequence: {crossbeam_gvs}")
    print(f"Cross-beam GV sorted: {sorted(crossbeam_gvs)}")

    # Check: how many unique GVs?
    unique_gvs = set(crossbeam_gvs)
    print(f"Unique GV values in cross-beam: {len(unique_gvs)}")
    gv_counts = Counter(crossbeam_gvs)
    for gv, cnt in gv_counts.most_common():
        if cnt > 1:
            print(f"  GV={gv} appears {cnt} times")

    # All 22 cross-beam triplets present in Torah body?
    all_present = all(counts_f0.get(trip, 0) > 0 for trip in crossbeam_triplets)
    print(f"\nAll 22 cross-beam triplets found in Torah (frame 0): {all_present}")
    missing = [trip for trip in crossbeam_triplets if counts_f0.get(trip, 0) == 0]
    if missing:
        print(f"Missing triplets: {[''.join(t) for t in missing]}")
else:
    print("ERROR: Cross-beam positions exceed Torah length!")
    crossbeam_gvs = []
    crossbeam_triplets = []

# ============================================================
# PART 5: Local alignment — does the cross-beam sequence recur?
# ============================================================
print("\n" + "=" * 60)
print("PART 5: Cross-beam sequence alignment in Torah")
print("=" * 60)

if crossbeam_triplets:
    # Search for consecutive matching triplets from the cross-beam sequence
    # in the frame-0 triplet stream

    # Build a lookup: for each triplet, positions where it occurs
    trip_positions = defaultdict(list)
    for pos, trip in enumerate(triplets_f0):
        trip_positions[trip].append(pos)

    # For each starting triplet of cross-beam, check how far the match extends
    best_matches = []
    cb_set = set(range(len(crossbeam_triplets)))

    for start_cb in range(len(crossbeam_triplets)):
        first_trip = crossbeam_triplets[start_cb]
        for pos in trip_positions.get(first_trip, []):
            # Skip if this IS the cross-beam itself
            cb_start_pos = crossbeam_positions[0] // 3  # approximate
            if abs(pos - cb_start_pos) < 30:
                continue

            # How many consecutive cross-beam triplets match?
            match_len = 0
            for j in range(start_cb, len(crossbeam_triplets)):
                if pos + (j - start_cb) < len(triplets_f0) and triplets_f0[pos + (j - start_cb)] == crossbeam_triplets[j]:
                    match_len += 1
                else:
                    break

            if match_len >= 3:
                torah_pos = pos * 3
                best_matches.append((match_len, start_cb, pos, torah_pos))

    best_matches.sort(reverse=True)
    print(f"Matches of >=3 consecutive cross-beam triplets: {len(best_matches)}")
    for match_len, cb_start, trip_pos, torah_pos in best_matches[:20]:
        matched = [''.join(crossbeam_triplets[cb_start + k]) for k in range(match_len)]
        print(f"  {match_len} triplets starting at cb-codon {cb_start+1}, torah pos {torah_pos}: {' '.join(matched)}")

# ============================================================
# PART 6: Genome connection
# ============================================================
print("\n" + "=" * 60)
print("PART 6: Genome connection")
print("=" * 60)

# Load mitochondrial genome
mito_path = ROOT / "data" / "genomes" / "human_mitochondria.fasta"
with open(mito_path) as f:
    lines = f.readlines()
mito_seq = ''.join(line.strip() for line in lines if not line.startswith('>'))
print(f"Mitochondrial genome: {len(mito_seq)} bases")
print(f"= {len(mito_seq)//3} codons")

# The 22 amino acids by molecular weight (including selenocysteine and pyrrolysine)
# Standard 20 + U (Sec, 168.064) + O (Pyr, 255.31)
# Sorted by molecular weight:
AA_BY_MW = [
    ('G', 'Gly', 57.05),
    ('A', 'Ala', 71.08),
    ('S', 'Ser', 87.08),
    ('P', 'Pro', 97.12),
    ('V', 'Val', 99.13),
    ('T', 'Thr', 101.10),
    ('C', 'Cys', 103.14),
    ('I', 'Ile', 113.16),
    ('L', 'Leu', 113.16),  # same MW as Ile, conventionally L after I
    ('N', 'Asn', 114.10),
    ('D', 'Asp', 115.09),
    ('Q', 'Gln', 128.13),
    ('K', 'Lys', 128.17),  # same MW range as Gln
    ('E', 'Glu', 129.12),
    ('M', 'Met', 131.20),
    ('H', 'His', 137.14),
    ('F', 'Phe', 147.18),
    ('R', 'Arg', 156.19),
    ('Y', 'Tyr', 163.18),
    ('W', 'Trp', 186.21),
    ('U', 'Sec', 168.06),  # selenocysteine
    ('O', 'Pyr', 255.31),  # pyrrolysine
]

print(f"\n22 amino acids by MW:")
for i, (aa, name, mw) in enumerate(AA_BY_MW):
    print(f"  Rank {i+1:2d}: {aa} ({name}, MW={mw})")

# Map cross-beam GVs to amino acids
if crossbeam_gvs:
    gv_sorted_unique = sorted(set(crossbeam_gvs))
    print(f"\nSorted unique cross-beam GVs ({len(gv_sorted_unique)}): {gv_sorted_unique}")

    # The given sorted GVs (from the problem statement)
    given_gv_sorted = [17, 17, 22, 26, 36, 58, 70, 86, 90, 160, 255, 307, 346, 371, 415, 416, 431, 441, 446, 480, 540, 640]

    # Build rank mapping: GV -> rank (0-indexed)
    # For duplicates (17 appears twice), they map to ranks 0 and 1
    gv_rank = {}
    all_gvs_sorted = sorted(crossbeam_gvs)
    for rank, gv in enumerate(all_gvs_sorted):
        if gv not in gv_rank:
            gv_rank[gv] = rank  # first occurrence gets lower rank

    # But we need to handle the case where GV=17 appears twice
    # Let's map each position individually
    # Sort the 22 GVs, then each GV in the original sequence maps to the rank of its sorted position
    gv_positions_sorted = sorted(range(22), key=lambda i: crossbeam_gvs[i])
    rank_of_position = [0] * 22
    for rank, pos in enumerate(gv_positions_sorted):
        rank_of_position[pos] = rank

    print(f"\nCross-beam protein translation:")
    protein_seq = []
    for i in range(22):
        gv = crossbeam_gvs[i]
        rank = rank_of_position[i]
        aa = AA_BY_MW[rank][0]
        aa_name = AA_BY_MW[rank][1]
        trip_str = ''.join(crossbeam_triplets[i])
        protein_seq.append(aa)
        print(f"  Codon {i+1:2d}: {trip_str} GV={gv:4d} -> rank {rank+1:2d} -> {aa} ({aa_name})")

    protein = ''.join(protein_seq)
    print(f"\nProtein sequence: {protein}")
    print(f"Length: {len(protein)} residues")

    # ============================================================
    # Search in mitochondrial translated frames
    # ============================================================
    print("\n--- Searching translated mitochondrial reading frames ---")

    # Standard genetic code
    CODON_TABLE = {
        'TTT': 'F', 'TTC': 'F', 'TTA': 'L', 'TTG': 'L',
        'CTT': 'L', 'CTC': 'L', 'CTA': 'L', 'CTG': 'L',
        'ATT': 'I', 'ATC': 'I', 'ATA': 'I', 'ATG': 'M',
        'GTT': 'V', 'GTC': 'V', 'GTA': 'V', 'GTG': 'V',
        'TCT': 'S', 'TCC': 'S', 'TCA': 'S', 'TCG': 'S',
        'CCT': 'P', 'CCC': 'P', 'CCA': 'P', 'CCG': 'P',
        'ACT': 'T', 'ACC': 'T', 'ACA': 'T', 'ACG': 'T',
        'GCT': 'A', 'GCC': 'A', 'GCA': 'A', 'GCG': 'A',
        'TAT': 'Y', 'TAC': 'Y', 'TAA': '*', 'TAG': '*',
        'CAT': 'H', 'CAC': 'H', 'CAA': 'Q', 'CAG': 'Q',
        'AAT': 'N', 'AAC': 'N', 'AAA': 'K', 'AAG': 'K',
        'GAT': 'D', 'GAC': 'D', 'GAA': 'E', 'GAG': 'E',
        'TGT': 'C', 'TGC': 'C', 'TGA': '*', 'TGG': 'W',
        'CGT': 'R', 'CGC': 'R', 'CGA': 'R', 'CGG': 'R',
        'AGT': 'S', 'AGC': 'S', 'AGA': 'R', 'AGG': 'R',
        'GGT': 'G', 'GGC': 'G', 'GGA': 'G', 'GGG': 'G',
    }

    def translate_dna(seq):
        protein = []
        for i in range(0, len(seq) - 2, 3):
            codon = seq[i:i+3].upper()
            aa = CODON_TABLE.get(codon, 'X')
            protein.append(aa)
        return ''.join(protein)

    def reverse_complement(seq):
        comp = {'A': 'T', 'T': 'A', 'G': 'C', 'C': 'G', 'N': 'N'}
        return ''.join(comp.get(b, 'N') for b in reversed(seq.upper()))

    # Translate all 6 reading frames
    mito_rc = reverse_complement(mito_seq)

    all_proteins = {}
    for frame in range(3):
        fwd = translate_dna(mito_seq[frame:])
        rev = translate_dna(mito_rc[frame:])
        all_proteins[f"+{frame}"] = fwd
        all_proteins[f"-{frame}"] = rev

    # Search for the cross-beam protein or subsequences
    search_seq = protein.replace('[', '').replace(']', '')  # clean
    # Remove non-standard AAs for search
    search_standard = ''.join(c for c in search_seq if c in 'ACDEFGHIKLMNPQRSTVWY')

    print(f"Searching for: {search_standard}")

    # Search for substrings of decreasing length
    for min_len in range(len(search_standard), 3, -1):
        found = False
        for start in range(len(search_standard) - min_len + 1):
            subseq = search_standard[start:start+min_len]
            for frame_name, frame_protein in all_proteins.items():
                pos = frame_protein.find(subseq)
                if pos >= 0:
                    print(f"  MATCH: {subseq} (len={min_len}) in frame {frame_name} at position {pos}")
                    found = True
        if found:
            break
    else:
        print("  No matches of length >= 4 found in mitochondrial reading frames")

    # Try individual amino acid pairs (dipeptides)
    print("\n  Dipeptide matches in mitochondrial proteome:")
    for i in range(len(search_standard) - 1):
        dipeptide = search_standard[i:i+2]
        total = sum(p.count(dipeptide) for p in all_proteins.values())
        if total > 0:
            print(f"    {dipeptide}: {total} occurrences")

# ============================================================
# PART 6b: Search genome files
# ============================================================
print("\n--- Other genome files ---")
for gfile in sorted((ROOT / "data" / "genomes").glob("*.fasta")):
    with open(gfile) as f:
        lines = f.readlines()
    seq = ''.join(line.strip() for line in lines if not line.startswith('>'))
    print(f"\n{gfile.name}: {len(seq)} bases = {len(seq)//3} codons")

    # Translate frame 0 and search
    prot = translate_dna(seq)
    for min_len in [8, 7, 6, 5, 4]:
        matches = []
        for start in range(len(search_standard) - min_len + 1):
            subseq = search_standard[start:start+min_len]
            pos = prot.find(subseq)
            if pos >= 0:
                matches.append((subseq, pos))
        if matches:
            print(f"  Matches of length {min_len}:")
            for subseq, pos in matches:
                print(f"    {subseq} at position {pos}")
            break

# ============================================================
# PART 7: Statistical analysis of triplet structure
# ============================================================
print("\n" + "=" * 60)
print("PART 7: Statistical structure of Torah triplets")
print("=" * 60)

# Entropy analysis
total_f0 = sum(counts_f0.values())
probs = [c / total_f0 for c in counts_f0.values()]
entropy = -sum(p * math.log2(p) for p in probs if p > 0)
max_entropy = math.log2(len(counts_f0))
print(f"Shannon entropy of triplet distribution: {entropy:.4f} bits")
print(f"Max possible entropy ({len(counts_f0)} types): {max_entropy:.4f} bits")
print(f"Efficiency: {entropy/max_entropy*100:.1f}%")
print(f"Equivalent uniform types: {2**entropy:.1f}")

# Compare to single-letter entropy
letter_counts = Counter(letters)
total_letters = len(letters)
letter_probs = [c / total_letters for c in letter_counts.values()]
letter_entropy = -sum(p * math.log2(p) for p in letter_probs if p > 0)
print(f"\nSingle-letter entropy: {letter_entropy:.4f} bits")
print(f"Expected triplet entropy (3 * single): {3*letter_entropy:.4f} bits")
print(f"Actual triplet entropy: {entropy:.4f} bits")
print(f"Mutual information (deviation from independence): {3*letter_entropy - entropy:.4f} bits")

# This tells us how much structure exists in the triplet distribution
# beyond what's expected from letter frequencies alone

# Expected triplet frequencies under independence
print("\nTop triplets vs independence model:")
expected_f0 = {}
for trip, cnt in counts_f0.items():
    expected = total_f0
    for ch in trip:
        expected *= letter_counts[ch] / total_letters
    expected_f0[trip] = expected

# Most over-represented triplets
enrichment = {trip: counts_f0[trip] / max(expected_f0[trip], 0.001) for trip in counts_f0}
enriched = sorted(enrichment.items(), key=lambda x: x[1], reverse=True)
print("\nMost enriched triplets (observed/expected):")
for trip, ratio in enriched[:20]:
    obs = counts_f0[trip]
    exp = expected_f0[trip]
    gv = triplet_gv(trip)
    print(f"  {''.join(trip)} GV={gv:4d}: obs={obs:5d}, exp={exp:.1f}, ratio={ratio:.2f}x")

depleted = sorted(enrichment.items(), key=lambda x: x[1])
print("\nMost depleted triplets (observed/expected):")
for trip, ratio in depleted[:20]:
    obs = counts_f0[trip]
    exp = expected_f0[trip]
    gv = triplet_gv(trip)
    print(f"  {''.join(trip)} GV={gv:4d}: obs={obs:5d}, exp={exp:.1f}, ratio={ratio:.2f}x")

# ============================================================
# PART 8: Codon-like partition test
# ============================================================
print("\n" + "=" * 60)
print("PART 8: Codon-like partition — can we find 64 groups?")
print("=" * 60)

# The genetic code maps 64 codons -> 20 amino acids (+ stop)
# The degeneracy is: some AAs have 1 codon, some have 2, 4, or 6
# Total: 61 sense + 3 stop = 64
#
# If the Torah has a similar structure, we'd expect:
# - A partition of the ~8000+ observed triplets into ~22 groups
# - Where each group has a "meaning" (like amino acid)
# - And the grouping has biological degeneracy pattern
#
# Test: GV as the "amino acid" — each GV value is an "amino acid"
# How many triplets map to each GV? This IS the degeneracy.

print(f"Total distinct GV values (= 'amino acids'): {len(gv_groups)}")

# How does the degeneracy compare to genetic code?
# Genetic code: 1,1,2,2,2,2,2,2,3,4,4,4,4,4,4,4,6,6,6 (for 20 AAs + stop)
# Most common degeneracies: 4 (most AAs), 2, 6, 1, 3

print(f"\nDegeneracy distribution of GV groups:")
for deg, count in sorted(degeneracy.items()):
    if count >= 3 or deg <= 10:
        print(f"  Degeneracy {deg:3d}: {count} GV values")

# What if we use a different grouping?
# Group by sorted letters (anagram class)
anagram_groups = defaultdict(list)
for trip, cnt in counts_f0.items():
    key = tuple(sorted(trip))
    anagram_groups[key].append((trip, cnt))

print(f"\nAnagram classes (sorted-letter groups): {len(anagram_groups)}")
anagram_deg = Counter(len(v) for v in anagram_groups.values())
print("Degeneracy of anagram classes:")
for deg, count in sorted(anagram_deg.items()):
    print(f"  {deg} permutations: {count} classes")

# ============================================================
# SAVE RESULTS
# ============================================================
print("\n" + "=" * 60)
print("Saving results...")
print("=" * 60)

results = {
    "torah_letter_count": len(letters),
    "frame_0_triplet_count": len(triplets_f0),
    "distinct_triplets_f0": len(counts_f0),
    "possible_triplets": 22**3,
    "coverage_pct": round(len(counts_f0) / 22**3 * 100, 1),
    "shannon_entropy_bits": round(entropy, 4),
    "max_entropy_bits": round(max_entropy, 4),
    "single_letter_entropy": round(letter_entropy, 4),
    "mutual_information": round(3 * letter_entropy - entropy, 4),
    "equivalent_uniform_types": round(2**entropy, 1),
    "distinct_gv_values": len(gv_groups),
    "anagram_classes": len(anagram_groups),
    "crossbeam_gvs": crossbeam_gvs if crossbeam_gvs else [],
    "crossbeam_triplets": [''.join(t) for t in crossbeam_triplets] if crossbeam_triplets else [],
    "crossbeam_protein": protein if crossbeam_triplets else "",
    "top_20_triplets": [{'triplet': ''.join(t), 'gv': triplet_gv(t), 'count': c} for t, c in counts_f0.most_common(20)],
    "freq_at_ranks": {str(r): freq_sorted[r-1] for r in [1, 10, 20, 50, 64, 100, 200, 500, 1000] if r <= len(freq_sorted)},
}

# Save as EDN-ish format (Python dict -> pretty printed)
import pprint
with open(OUT / "spy4-codon-alignment.edn", 'w') as f:
    f.write(";; Spy4: Codon Alignment Analysis\n")
    f.write(";; Generated by spy4_codon_alignment.py\n\n")
    f.write(pprint.pformat(results))

print(f"Saved to {OUT / 'spy4-codon-alignment.edn'}")

# Also save the full triplet frequency table
with open(OUT / "triplet-frequencies.tsv", 'w') as f:
    f.write("triplet\tgv\tcount\texpected\tratio\n")
    for trip, cnt in sorted(counts_f0.items(), key=lambda x: x[1], reverse=True):
        gv = triplet_gv(trip)
        exp = expected_f0.get(trip, 0)
        ratio = cnt / max(exp, 0.001)
        f.write(f"{''.join(trip)}\t{gv}\t{cnt}\t{exp:.1f}\t{ratio:.3f}\n")

print(f"Saved triplet frequencies to {OUT / 'triplet-frequencies.tsv'}")

print("\nDone.")
