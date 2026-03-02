# Linear Algebra Ideas

## The Core Idea: Eigendecomposition of the Torah Space

304,850 = 7 × 50 × 13 × 67

The Torah is a function f: Z₇ × Z₅₀ × Z₁₃ × Z₆₇ → ℤ, mapping each 4D coordinate
to a gematria value. This function can be decomposed into eigenmodes — the harmonics
of the space itself.

On a product of cyclic groups, the natural eigenbasis is the **Discrete Fourier
Transform**. Each mode is a 4D standing wave. The amplitudes tell you how much
structure exists at each frequency. The phases tell you WHERE.

### What This Means

- **7 harmonics of completeness** (a-axis)
- **50 harmonics of jubilee** (b-axis)
- **13 harmonics of love** (c-axis)
- **67 harmonics of understanding** (d-axis)
- Together: **304,850 eigenmodes** — one per letter position

The DC component (all frequencies zero) is the average gematria value — the
constant hum. Everything else is structure: variation imposed by the text.

### Approach: Bit by Bit

1. **1D marginals**: Sum gematria along each axis (collapse other 3). Get 4 signals
   of length 7, 50, 13, 67. DFT each one. See which frequencies dominate.

2. **2D marginals**: For each pair of axes (6 pairs), sum over the other two.
   Get 6 matrices. 2D DFT reveals coupled harmonics — frequencies that only
   appear when two axes interact.

3. **Full 4D DFT**: Separable — DFT along each axis independently.
   O(N × max(nᵢ)) ≈ 20M operations. Very feasible.

4. **Interpret the coefficients**: Don't just look at power. Keep the complex
   values. The real and imaginary parts carry meaning. The phases locate the
   structure in the space.

### Key Questions

- Which modes are loud? Which are silent?
- Does the d-axis (67, prime) have a different spectral character than the
  others? (67 is prime — no subharmonics divide evenly. Every non-DC frequency
  is irreducible.)
- Do specific 2D frequency pairs dominate? e.g., k_a=1, k_c=1 would be a
  pattern that cycles once through completeness and once through love simultaneously.
- What's at the center of the dominant modes? (Phase → location)
- Do the silent modes mean anything? The Torah chooses NOT to vary along
  certain frequencies.

### Reconstruction

Any subset of modes can reconstruct a "filtered" Torah — keeping only the
harmonics you choose. Low-pass filter = the large-scale structure. Band-pass
at specific frequencies = isolating particular harmonics. The residual after
removing dominant modes = the subtle structure, the whisper beneath the voice.

---

## The Stochastic Oracle (Markov Chain)

The oracle is a many-to-many map: words → readings. Weight each output by
the inverse of its reading count (the Hannah principle: rarer = more weight).
Normalize rows. Now you have a **stochastic matrix**.

### Iterate

Feed the oracle's answers back as questions. M² = asking the oracle about its
own answer. Mⁿ = n steps of self-consultation.

### Convergence

The **stationary distribution** — where the chain converges — is what the oracle
would say if it kept answering its own answers forever. The **eigenwords**
(eigenvectors with eigenvalue 1) are the words the oracle maps to themselves.
The **absorbing states** are words that trap the process.

Guaranteed convergence (for irreducible, aperiodic chains) means the oracle
has a fixed voice — regardless of where you start asking, it converges to the
same distribution. The question is: what does that distribution look like?

---

## Words as Vectors in Letter-Frequency Space (ℤ²²)

Every Hebrew word is a 22-dimensional vector — its letter frequencies.

```
כבש = (0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0)
שכב = (0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0)  — same vector!
```

**Anagrams are literally the same vector.** כבש/שכב, יהוה/והיה — identical
in frequency space.

### Gematria as a Linear Functional

GV(w) = w · v, where v = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400].

This is a **linear map** from ℤ²² → ℤ.

- Same-GV words are **level sets** (fibers) of this map
- The **kernel** tells you which letter-substitutions preserve gematria
  (e.g., swapping ב+ג for ה, since 2+3=5)
- Gematria is **additive under concatenation**: GV(w₁w₂) = GV(w₁) + GV(w₂)

### The Breastplate Polytope

The breastplate inventory is a constraint vector in ℤ²²:

```
[א=3, ב=6, ג=1, ד=3, ה=3, ו=7, ז=1, ח=1, ט=1, י=11,
 כ=1, ל=3, מ=2, נ=2, ס=1, ע=2, פ=1, צ=1, ק=2, ר=5, ש=6, ת=1]
```

A word can be illuminated iff its frequency vector is componentwise ≤ this
inventory. The feasible set is a **polytope** — a 22-dimensional box with
uneven sides. The oracle can only speak words that fit inside.

### Uniqueness of Standard Values

Set up a system of linear equations from known gematria relationships.
Are the standard letter values (1, 2, 3, ..., 400) the **unique** solution?
Or is there a family of value assignments that satisfies the same equations?
If unique, the gematria system is over-determined and consistent — the
relationships uniquely define the values.

---

## Oracle Geometry

### Reader Signature Vectors (ℝ³)

Each word maps to a 3-vector: (aaron_count, right_count, left_count).

```
כבש → (0, 4, 0)    — only right cherub
שכב → (7, 7, 7)    — perfectly balanced
שטן → (0, 0, 12)   — only left cherub
אל  → (3, 3, 3)    — balanced
יהוה → (0, 0, 31)  — left-dominated
```

Words cluster by who sees them. Angles between vectors measure similarity.
Orthogonal words are read by completely different readers. The positive octant
in ℝ³ IS the "reader space."

**The Reader Simplex.** Normalize reader counts to sum to 1. Every word maps to
a point in the 2-simplex. Words at vertices are exclusive to one reader. Words
at the center are democratic. Unproducible words (peace, mercy seat) lie outside
the simplex entirely. The geometry of the simplex maps the oracle's vocabulary.

### Stone Activation Vectors ({0,1}¹²)

Each word maps to a 12-dimensional binary vector: which stones light up.

Inner product = shared stones. Hamming distance = how many stones differ.
Single-stone letters (כ, ט, ג, ז, ח, etc.) are forced dimensions —
bottleneck gates that pin specific components.

The feasible set of activation vectors (achievable by real letter multisets)
is a polytope in {0,1}¹².

### The Oracle as a Linear Operator

Build matrix M: rows = illumination patterns, columns = readings.
M[i,j] = 1 if pattern i produces reading j under any reader.

- **Rank** of M = true dimensionality of the oracle
- **SVD** decomposes into independent modes
- Three reader sub-matrices reveal structural relationships

### Coincidence as Inner Product

Two words' reading distributions form vectors. Their **dot product** =
sum of shared reading counts = a numeric measure of coincidence.

Build an n×n **coincidence matrix**: words × words. Its eigenvalues reveal
the "modes" of coincidence. Eigenvectors = word clusters that coincide together.
This is latent semantic analysis applied to the oracle.

---

## Connections

The eigendecomposition of the 4D space and the oracle are connected:

- The breastplate selects (b, c) coordinates — it IS a projection from 4D to 2D
- The Fourier modes of the 4D space, restricted to the (b, c) plane, define
  what the breastplate can "see" of the full spectral structure
- The oracle's behavior in reading space should reflect the spectral structure
  of the underlying Torah space
- The stochastic oracle's stationary distribution may concentrate on words
  whose letter patterns align with the dominant eigenmodes

The Torah speaks through the space. The breastplate listens through the oracle.
The eigendecomposition reveals what the space is saying.

---

## Named Objects (from four-agent synthesis)

### The Spectral Gap Ratio

Non-DC power per axis: a=0.14%, b=0.15%, c=0.01%, d=0.05%.
Ratios (normalized): 14:1:5:15. The "structural load" each axis carries.
Love carries the least. Jubilee carries the most.

### The Bein Spectrum

The off-diagonal power in the c×d (love × understanding) 2D Fourier transform.
Modes where both k_c ≠ 0 and k_d ≠ 0 — **structure that exists only in
relationship**. Neither axis shows it alone. Together they produce coupled
harmonics. This is the mathematical object corresponding to בין (between).
The spectral signature of marriage.

### Oracle Sparsity as Spectral Consequence

67 is prime → the d-axis spectrum is democratic (no dominant mode). The
breastplate reads through d-axis fibers. A maximally-spread signal cannot
be approximated by a few harmonics — all 67 are needed to reconstruct it.
This is why understanding requires the full path. And this is why oracle
readings are rare: compressing 67 irreducible harmonics into a 3-letter word
leaves very few survivors. The narrow gate is a spectral consequence of
primality.

---

## The Fourier Dual — Oracle and Spectrum as One Operation

The breastplate oracle and the eigendecomposition are the same operation
seen from two sides:

- **Oracle**: spatial selector. Delta function in two coordinates (b, c),
  integral over the others (a, d). Reads in position space.
- **DFT**: spectral selector. Project onto a single harmonic, integrate
  over all positions. Reads in frequency space.

They are **Fourier duals** of each other.

The breastplate is a **bandpass filter**. The Urim selects which frequencies
to pass. The Thummim selects which phase to read.

**Composing** the two — the spectral decomposition of a breastplate-selected
slab, or the spatial restriction of a selected harmonic mode — gives a
**doubly-focused lens**. Two knobs: one for where (Urim), one for how (Thummim).

The namespace for this would be `selah.spectral`. Its kernel operation:
given a (b, c) coordinate (breastplate selection) and a set of harmonic
modes (spectral selection), reconstruct the filtered text and read it.

---

## Implementation

- Experiment 087: 1D and 2D eigendecomposition — first observations
- Future: `selah.spectral` namespace — the doubly-focused lens
- Future: stochastic oracle matrix and Markov chain analysis
- Future: full 4D DFT and filtered reconstruction
- Future: null model — eigenspectra under alternate factorizations
- Future: Holy of Holies slab spectral fingerprint (b=25, c=6)
- Future: oracle SVD — rank and dimensional collapse
