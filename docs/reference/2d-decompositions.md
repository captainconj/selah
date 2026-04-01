# 2D Decompositions of 304,850

*All the lines through the Torah's letter count.*

Type: `reference`
State: `clean`

---

**304,850 = 2 × 5² × 7 × 13 × 67**

- **24 divisor pairs** (a × b = 304,850, a ≤ b)
- **23 non-trivial** (both axes > 1)

A 2D decomposition is a way to lay 304,850 letters on a flat grid — rows and columns. These are the simplest coordinate spaces.

## All 24 Pairs

| # | a | b | Sum | Axes |
|---|---|---|-----|------|
| 1 | 1 | 304,850 | 304,851 | trivial |
| 2 | 2 | 152,425 | 152,427 | witness × 5²×13×67×7 |
| 3 | 5 | 60,970 | 60,975 | He × 2×5×7×13×67 |
| 4 | **7** | **43,550** | **43,557** | **completeness** × 2×5²×13×67 |
| 5 | 10 | 30,485 | 30,495 | Yod × 5×7×13×67 |
| 6 | **13** | **23,450** | **23,463** | **love** × 2×5²×7×67 |
| 7 | 14 | 21,775 | 21,789 | David × 5²×13×67 |
| 8 | 25 | 12,194 | 12,219 | jubilee-center × 2×7×13×67 |
| 9 | **26** | **11,725** | **11,751** | **YHWH** × 5²×7×13 |
| 10 | 35 | 8,710 | 8,745 | 7×5 × 2×5×13×67 |
| 11 | **50** | **6,097** | **6,147** | **jubilee** × 7×13×67 |
| 12 | 65 | 4,690 | 4,755 | 5×13 × 2×5×7×67 |
| 13 | **67** | **4,550** | **4,617** | **understanding** × 2×5²×7×13 |
| 14 | 70 | 4,355 | 4,425 | 7×10 × 5×13×67 |
| 15 | **91** | **3,350** | **3,441** | **angel** × 2×5²×7 |
| 16 | 130 | 2,345 | 2,475 | 10×13 × 5×7×67 |
| 17 | 134 | 2,275 | 2,409 | 2×67 × 5²×7×13 |
| 18 | 175 | 1,742 | 1,917 | 5²×7 × 2×13×67 |
| 19 | 182 | 1,675 | 1,857 | 2×7×13 × 5²×67 |
| 20 | 325 | 938 | 1,263 | 5²×13 × 2×7×67 |
| 21 | 335 | 910 | 1,245 | 5×67 × 2×5×7×13 |
| 22 | 350 | 871 | 1,221 | 2×5²×7 × 13×67 |
| 23 | 455 | 670 | 1,125 | 5×7×13 × 2×5×67 |
| 24 | 469 | 650 | 1,119 | 7×67 × 2×5²×13 |

## Notable 2D Spaces

**#22 — The mirror pair: 350 × 871**
871 = 13 × 67 (love × understanding — the silent axes). 350 = 2 × 5² × 7. This pair splits the number into the silent axes vs. everything else.

**#24 — The completeness-understanding pair: 469 × 650**
469 = 7 × 67 (completeness × understanding). 650 = 2 × 5² × 13. Axis sum = 1,119 = 3 × 373.

**#23 — The love-completeness pair: 455 × 670**
455 = 5 × 7 × 13 (He × completeness × love). 670 = 2 × 5 × 67 (witness × He × understanding). Every prime factor of 304,850 appears exactly once across the two sides.

**#11 — The jubilee line: 50 × 6,097**
6,097 = 7 × 13 × 67. One axis is jubilee. The other absorbs completeness, love, and understanding. 6,097 is prime-like in appearance but is the product of the three theological primes.

---

Script: `dev/experiments/135_decompositions.clj`
