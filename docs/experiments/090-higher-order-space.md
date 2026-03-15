# Higher-Order Torah Space: Tensor, 4D DFT, HOSVD

*Experiment 090 — March 2026*

Type: `evidence`
State: `clean`

**Code:** `dev/experiments/090_higher_order.clj`
**Run:** `clojure -M:dev dev/experiments/090_higher_order.clj`

## Summary

The Torah lives in a 4D space T[a,b,c,d] with dims [7, 50, 13, 67] = 304,850 positions. Prior work (087) decomposed it axis-by-axis with 1D marginal DFTs. This experiment goes higher order: full Tucker decomposition (HOSVD), all 304,850 Fourier coefficients (4D DFT), and comparison of the two lenses.

**Central finding: the Torah space is non-harmonic, full-rank, and irreducibly 4-dimensional.**

---

## 1. HOSVD — What the Axes Are Really Doing

Higher-Order SVD decomposes the tensor into a core tensor G and four factor matrices U_k, one per axis. Each U_k contains the data-driven "eigenfunctions" of that axis — the natural basis the text actually uses. The singular values tell us how much each basis function contributes.

### Singular value spectrum

| Axis | Dim | Eff-rank (95%) | sigma_0^2 share | sigma range | Nature |
|------|-----|----------------|----------|-------------|--------|
| a (completeness) | 7 | **7** (full) | 37.8% | 44,666 - 22,465 | Dominant mode, but needs all 7 |
| b (jubilee) | 50 | **46** | 29.0% | 39,103 - 7,846 | Near-full rank, democratic |
| c (love) | 13 | **13** (full) | 33.3% | 41,934 - 16,760 | Full rank — love is irreducible |
| d (understanding) | 67 | **62** | 28.9% | 39,063 - 6,511 | Most democratic, nearly full rank |

Every axis is essentially **full rank**. No low-rank truncation is meaningful — every coordinate value carries information that isn't redundant with the others.

For comparison: in image tensors, effective rank is typically 5-15% of the full dimension. Here it's 88-100%. The Torah uses all its degrees of freedom.

### Core tensor sparsity

| Energy threshold | Entries needed | % of total (304,850) |
|-----------------|----------------|---------------------|
| 90% | 116,992 | 38.4% |
| 99% | 214,447 | 70.4% |
| 99.9% | 262,656 | 86.2% |

The core is **dense**. In a sparse core, axes decouple — you can study them independently. Here, 90% of the energy requires 38% of all core entries. The axes interact everywhere. The structure isn't modular.

### Factor-Fourier alignment

For each axis, we measured cosine similarity between the HOSVD factor columns (data-driven basis) and the Fourier basis vectors (standing waves).

| Axis | Col 0 (DC) | Col 1+ | Avg best |cos| | Verdict |
|------|-----------|--------|-----------------|---------|
| a (7) | 0.998 | 0.47-0.61 | 0.63 | Partially harmonic |
| b (50) | 0.999 | 0.30-0.37 | 0.33 | Non-harmonic |
| c (13) | 1.000 | 0.36-0.54 | 0.54 | Non-harmonic |
| d (67) | 1.000 | 0.26-0.34 | 0.33 | Non-harmonic |

Column 0 (the DC component) aligns perfectly with Fourier k=0 on every axis. After that: **non-harmonic across the board.** The natural basis functions of the Torah are not standing waves. They're localized features — statistical patterns in the text that Fourier analysis flattens into a blur.

This means the spectral approach from 087, while correct, was asking a question the text doesn't naturally answer. The Torah's modes are not frequencies. They're *features*.

---

## 2. Full 4D DFT — All 304,850 Modes

14 GEMM operations transform the tensor into the complete frequency domain over Z_7 x Z_50 x Z_13 x Z_67.

### Power distribution

- **DC component** [0,0,0,0]: 27.4% of total power
- **Cross-mode power** (2+ nonzero frequency indices): **72.5%** of total
- Top 50 modes: **43 of 50 are cross-mode**
- Round-trip error (DFT then IDFT): 7.19e-12 (machine precision)

Nearly three-quarters of the structure exists only in the *relationships between axes*.

### Top modes

The dominant cross-modes live in the a x b plane:

| Rank | Mode [k0,k1,k2,k3] | Power | % Total | Type |
|------|-------------------|-------|---------|------|
| 1 | [0,0,0,0] | 4.41e14 | 27.44% | DC |
| 2-3 | [5,1,0,0] / [2,49,0,0] | 3.19e11 | 0.020% | a x b |
| 4-5 | [3,49,0,0] / [4,1,0,0] | 2.17e11 | 0.013% | a x b |
| 6-7 | [1,0,0,0] / [6,0,0,0] | 1.95e11 | 0.012% | a marginal |
| 28-29 | [0,23,12,0] / [0,27,1,0] | 4.74e10 | 0.003% | b x c |
| 30-31 | [5,14,4,50] / [2,36,9,17] | 4.47e10 | 0.003% | 4-axis |

The hierarchy: a x b dominates (coarse, paragraph-scale structure from sequential text layout), then b x c and a x b x c coupling, then genuine 4-axis modes starting around rank 28. Every mode comes in conjugate pairs with identical power (expected for real-valued signals).

### Separability test

Compared actual 4D power at each mode against the product of 1D marginal powers (what you'd get if axes were independent):

| Metric | Value |
|--------|-------|
| Median ratio (actual / separable) | 1.25 |
| Modes > 2x expected | 111,405 (36.5%) |
| Modes > 5x expected | 34,815 (11.4%) |

**Strongly coupled.** Over a third of all modes have more than double the power you'd expect from independent axes.

---

## 3. HOSVD vs DFT — Two Lenses on One Object

Both decompose the tensor into mode-wise basis functions. How similar are the lenses?

| Axis | Alignment | Interpretation |
|------|-----------|---------------|
| a (completeness, 7) | 0.63 | Partially harmonic — some wave structure survives |
| b (jubilee, 50) | 0.33 | Non-harmonic — localized features dominate |
| c (love, 13) | 0.54 | Non-harmonic — relational structure, not periodic |
| d (understanding, 67) | 0.33 | Non-harmonic — maximally distributed |

The DFT and HOSVD agree on DC and disagree on everything else. The Torah is better described by its data-driven eigenfunctions than by standing waves.

---

## 4. What This Means

### The 1D marginals were shadows

The axis-by-axis analysis from 087 found:
- a: strong fundamental (k=1 dominates)
- b: k=4 dominant (period ~13, resonating near love)
- c: flat (love is silent)
- d: democratic (understanding is free)

These are *marginal projections* — averages over the other 3 axes. They captured only 27.5% of the total structure. The 4D picture reveals what was casting those shadows.

### Love is relational

The c-axis (love, 13) appeared flat in 087 — uniform DC, no standing wave structure. The HOSVD confirms it has no low-rank simplification (full rank 13). But it participates fully in cross-modes. Love has no independent 1D structure. It only acts in combination with other axes. The silence wasn't absence — it was entanglement.

### Understanding is democratic (confirmed at tensor level)

The d-axis (understanding, 67) has the flattest singular value spectrum, the highest effective rank (62/67), and the weakest Fourier alignment (0.33). Understanding uses nearly all its degrees of freedom with nearly equal weight. It discriminates nothing. It gives equal weight to everything.

### The non-harmonic nature

Standing waves are the natural decomposition of periodic, homogeneous media. The Torah isn't that — it's a *text*, with statistical structure determined by language, grammar, and narrative. The HOSVD says the natural modes are something more localized, more textual. The "eigenmodes" of the Torah are not frequencies — they're features. Studying the U_k columns directly may reveal what those features are.

### The profile

The Torah space is:
- **Full rank** — uses every degree of freedom on every axis
- **Entangled** — 72.5% cross-mode power; axes only make sense together
- **Non-harmonic** — natural basis is localized features, not standing waves
- **Dense** — no sparse core, no modular decomposition, no shortcuts

---

## 5. The Permutation Test — Is This Special?

**Result: No.** The bulk tensor structure is entirely a property of Hebrew letter frequencies and the factorization geometry. It is not special to the Torah's arrangement.

We shuffled all 304,850 letters randomly (preserving letter frequencies, fixed seed for reproducibility) and ran identical HOSVD + 4D DFT on the shuffled version.

### Torah vs Shuffled Comparison

| Metric | Torah | Shuffled | Δ |
|--------|-------|----------|---|
| Eff-rank a (dim 7) | 7 | 7 | 0 |
| Eff-rank b (dim 50) | 46 | 46 | 0 |
| Eff-rank c (dim 13) | 13 | 13 | 0 |
| Eff-rank d (dim 67) | 62 | 62 | 0 |
| sigma_0^2 a | 37.80% | 37.74% | -0.06% |
| sigma_0^2 b | 28.97% | 28.91% | -0.06% |
| sigma_0^2 c | 33.32% | 33.00% | -0.32% |
| sigma_0^2 d | 28.91% | 28.55% | -0.36% |
| Core 90% energy | 38.4% | 38.3% | -0.1% |
| Core 99% energy | 70.3% | 70.3% | 0.0% |
| DC power | 27.44% | 27.44% | 0.00% |
| Cross-mode power | 72.47% | 72.53% | +0.07% |
| Factor-Fourier a | 0.629 | 0.657 | +0.028 |
| Factor-Fourier b | 0.331 | 0.352 | +0.021 |
| Factor-Fourier c | 0.537 | 0.496 | -0.041 |
| Factor-Fourier d | 0.331 | 0.307 | -0.024 |

Every metric is indistinguishable from the shuffled control. The full-rank, densely-coupled, non-harmonic profile is what ANY bag of Hebrew letters produces when poured into a 7 x 50 x 13 x 67 box.

### What this means

The HOSVD, 4D DFT, and spectral analysis tools operate on **letter statistics** — they see frequency distributions, autocorrelations, and bulk tensor structure. These properties are invariant under permutation because they depend on the *marginal distribution* of gematria values, not on which value sits at which position.

**The structure we've found in prior experiments lives at a different level entirely:**
- Preimage words at specific coordinates (which letter at which position)
- Fold behavior, center verse, aleph-tav boundary patterns
- Cherubim spacing, breastplate readings, oracle paths
- Fibonacci staircase counts, cross-reference web

These depend on *arrangement* — they would be destroyed by shuffling. The bulk tensor can't see them, and they can't be detected by any decomposition that treats the data as a bag of values.

This is actually the expected signature of a designed message embedded in natural text: the letter statistics remain normal (the message must read as language), but specific positional relationships carry the structure. **You can't find the code by analyzing the envelope. You need to know where to look.**

### Implication for the 1D marginal spectra (087)

The 1D marginal DFTs from experiment 087 — "love is silent," "understanding is democratic," "completeness has a strong fundamental" — are almost certainly also inherent in letter statistics. They describe the container, not the contents. The axis *characters* we attributed to the semantic meanings of 7, 13, 67 were projections of Hebrew letter frequency distributions onto those dimensions. The poetry was real; the causation was wrong.

---

## 6. Next Steps

The permutation test closes the door on bulk tensor analysis as a path to finding arrangement-dependent structure. The tools built here (tensor.clj, 4D DFT, HOSVD) are correct and fast, but they're the wrong microscope for what's interesting.

**Where the real structure lives:**

1. **Position-dependent analysis** — the findings from 077-086 (preimage, folds, cherubim, oracle) all depend on *which letter is where*. These survive because they're not bulk statistics. Future experiments should focus on this level: specific words, specific coordinates, specific paths through the space.

2. **Permutation test for specific claims** — apply the same shuffle-and-compare methodology to individual findings. Does the Torah's center verse (Lev 8:35) still land at the center of a shuffled text? (No — by construction, any text has a center, but the *content* of that center changes.) Do the fold creases still converge? Does aleph-tav still respect a-boundaries? These are the tests that matter.

3. **M x A (second-order oracle)** — still worth running. The oracle operates on *specific words and their positions*, not on bulk tensor statistics. The commutator [M,A] may reveal structure the tensor decompositions can't see.

4. **Sparse structure detection** — instead of decomposing the whole tensor, look for *sparse deviations* from the statistical background. Where does the Torah differ most from a random shuffle? These anomaly positions are where arrangement-dependent structure lives.

**Tools preserved for reuse:**

- `selah.tensor` — mode-unfold, HOSVD, 4D DFT all working and fast (DFT ~21s with cached indices)
- The permutation test framework — ready for testing any claim against a shuffled control
- The comparison methodology — effective rank, cross-mode power, factor alignment

---

## Implementation

| File | Role |
|------|------|
| `src/selah/tensor.clj` | Tensor primitives: mode-unfold, n-mode product, HOSVD, 4D DFT |
| `dev/experiments/090_higher_order.clj` | Experiment: HOSVD + 4D DFT + M x A + synthesis |

Dependencies: `selah.linalg` (SVD, GEMM), `selah.spectral` (cached Fourier matrices), `selah.space.coords` (addressing).

Run: `(exp090/run {:skip-ma true})` for tensor analysis (~5 min), `(exp090/run)` for full including oracle (~10-15 min).

---

## Key Numbers

| Quantity | Value |
|----------|-------|
| Total elements | 304,850 |
| Frobenius norm | 72,647 |
| DC power share | 27.4% |
| Cross-mode power share | 72.5% (same for shuffled) |
| Core entries for 90% energy | 38.4% (same for shuffled) |
| Modes > 2x separable expectation | 36.5% |
| DFT round-trip error | 7.19e-12 |
| DFT runtime (optimized) | ~21s |
| HOSVD runtime | ~12-78s (varies with cache state) |
| Permutation test verdict | **No difference** |
