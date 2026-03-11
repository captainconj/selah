# How to Play DNA Through the Breastplate

*A guide for Codex. The reverse direction: protein → Hebrew → oracle.*

Type: `guided reproduction`
State: `clean`

---

## The Discovery

We went looking for DNA in the Ark. The machine said: no, here's how Hebrew *is* DNA.

The path:
1. The Ark's rooms (קנים) were sealed with atonement "from inside and from outside" — a cell membrane
2. The three branches on the taxonomy floor (bird, beast, guard) matched the three codon positions
3. The lamb at the wobble: the guard lies down so life can adapt
4. The breastplate grid is 4×3 = codon is 3 from alphabet of 4 (transposed)
5. 72 positions = 64 coding + 8 singleton. 64 = the number of codons.
6. 26 backbone positions = YHWH. The Name IS the backbone.
7. We mapped rows → bases (Watson-Crick survives), letters → amino acids (p < 0.0001)
8. Then we turned the key the other way: protein → Hebrew → oracle

That last step is what this guide is about.

---

## The Map

From experiment 101. The translation table:

```
Letter  GV   →  Amino Acid    Codons  Class
──────  ───     ──────────    ──────  ─────
א       1       (unmapped)    —       beyond the code
ב       2       Met           1       start codon
ג       3       Asp           2       charged
ד       4       Ala           4       small
ה       5       Thr           4       small
ו       6       Pro           4       small
ז       7       Tyr           2       polar
ח       8       Asn           2       polar
ט       9       Val           4       hydrophobic
י       10      Ser           6       small
כ       20      Trp           1       special (singleton on grid)
ל       30      Glu           2       charged
מ       40      Ile           3       hydrophobic
נ       50      Leu           6       hydrophobic
ס       60      Stop          3       stop (= Samekh on grid)
ע       70      His           2       charged
פ       80      Cys           2       special
צ       90      Gln           2       polar
ק       100     Lys           2       charged
ר       200     Arg           6       charged
ש       300     Gly           4       small
ת       400     Phe           2       special
```

**Two silent letters:**
- **Aleph (א)** — unmapped. No amino acid. The breath beyond the code.
- **Samekh (ס)** — Stop codon. Terminates translation. Cannot appear within a protein.

**25% of Torah vocabulary** contains Aleph or Samekh. These words are structurally impossible for any protein to produce. The words the genome cannot say: man (אדם), love (אהבה), truth (אמת), create (ברא), God (אלהים), I AM (אהיה), lovingkindness (חסד).

---

## The Pipeline

```
DNA → codons → amino acids → Hebrew letters → sliding windows → oracle
```

All of this lives in `src/selah/dna.clj`.

### Step 1: Get the protein

```clojure
(require '[selah.dna :as dna])

;; From the curated library (21 proteins)
(dna/get-protein "p53")        ; partial name match, case-insensitive
(dna/get-protein "hemoglobin")
(dna/get-protein "serpent")

;; Fetch any protein from UniProt by accession
(dna/fetch-uniprot "P04637")

;; See the full library
(dna/catalog)
```

### Step 2: Translate to Hebrew

```clojure
;; From protein sequence string (single-letter amino acid codes)
(dna/protein-str->hebrew "MVLSPAD...")
;; → "בטנידדג..."

;; Or use the full play pipeline
(def result (dna/play "MEEPQSDP..." {:format :protein}))
;; Returns:
;;   :hebrew    — the Hebrew letter string
;;   :gv        — gematria value
;;   :length    — number of residues
;;   :protein-str — the amino acid letters
```

### Step 3: Slide through the oracle

```clojure
;; 3-letter sliding window (default)
(def hits (dna/slide (:hebrew result) {:window 3 :vocab :torah}))
;; Returns vec of {:position :letters :gv :top-5} for windows with readings

;; 4-letter sliding window
(def hits-4 (dna/slide (:hebrew result) {:window 4 :vocab :torah}))

;; Word frequencies — how often each word appears as top oracle reading
(def top-words (dna/word-frequencies hits))
```

### Step 4: The full experiment (all-in-one)

```clojure
;; Fetch + translate + slide + analyze + save
(dna/experiment "p53")
;; Saves to data/dna/p53-oracle.edn and data/dna/p53-report.txt
;; Prints full summary to console
```

### Step 5: Per-head analysis (deeper)

```clojure
;; For specific words found in the protein, check what each reader sees
(require '[selah.oracle :as o])
(o/forward-by-head "נחש")  ; serpent — who reads it?
(o/forward-by-head "שמר")  ; guard — who reads it?

;; Basin walks — where does this word go?
(require '[selah.basin :as b])
(b/walk "נחש")  ; serpent walks to...
(b/walk "מטה")  ; staff walks to...
```

---

## The Library

21 curated proteins in `dna/library`. Each chosen for a reason:

| Name | Accession | Why |
|------|-----------|-----|
| **p53** | P04637 | Guardian of the genome. Named for 53. 53 = garden. |
| **BRCA1** | P38398 | DNA repair. The other guardian. |
| **Hemoglobin-alpha** | P69905 | The blood. Carries oxygen. |
| **Hemoglobin-beta** | P68871 | The blood. Sickle cell = one letter change. |
| **Myoglobin** | P02144 | First protein structure ever solved (1958). |
| **Insulin** | P01308 | First protein sequenced (1951). Life and death. |
| **Collagen-I-alpha1** | P02452 | Most abundant protein. The scaffold. |
| **Laminin-gamma1** | P11047 | Cross-shaped. Holds cells together. |
| **Histone-H3** | P68431 | Wraps DNA. The scroll. |
| **Histone-H4** | P62805 | Most conserved eukaryotic protein. Eternal scroll. |
| **Ubiquitin** | P0CG48 | 76 residues. Marks for destruction. Judgment. |
| **Rhodopsin** | P08100 | Vision. Light → signal. |
| **Cytochrome-c** | P99999 | Electron transport. Most conserved across species. |
| **ATP-synthase-beta** | P06576 | The rotary engine. Makes ATP. |
| **RNA-polymerase-II** | P24928 | Reads DNA, writes RNA. The scribe. |
| **Ribosomal-protein-S3** | P23396 | Part of the ribosome. Reads the code. |
| **Ferredoxin** | P10109 | Iron-sulfur. Among the oldest. Iron = 26 = YHWH. |
| **Calmodulin** | P0DP23 | Calcium messenger. The signal. |
| **Immunoglobulin-G1** | P01857 | Antibody. The guard. Recognition. |
| **Serpent-toxin-alpha** | P60615 | Alpha-bungarotoxin. The serpent's weapon. |
| **FOXP2** | O15409 | Language protein. Two AAs separate man from ape. |

---

## What We Found

### The Guardian (p53) — docs/ark/27-the-guardian.md
- 393 residues. 179 windows with readings out of 391.
- **Named itself:** שמר (guard) at position 331. The protein knows what it is.
- **Dominant word:** דוד (David) ×9. The guardian speaks of the king.
- **Contains the serpent:** נחש at positions 198 and 261.
- **Ends with holy:** קדש at position 354. Guard → David → serpent → holy.
- **Self-reference:** the guardian contains the serpent but the serpent does not contain the guardian.

### The Blood — docs/ark/30-the-blood.md
- Three blood proteins: hemoglobin-alpha (142), hemoglobin-beta (147), myoglobin (154).
- **Alpha stretches out:** נטה (stretch out) ×4. The blood reaches everywhere.
- **Beta will know:** נדע (we will know) ×4.
- **Both carry the Binding:** עקד (Akedah) in both hemoglobin chains.
- **Beta contains the serpent:** נחש at position 106.
- **Alpha ends with sprinkling:** זרק. The priestly act.
- **Myoglobin says "like snow":** כשלג (4-letter). Isaiah 1:18.
- **The blood cries out:** יצעק at position 116. Genesis 4:10.
- **Sickle cell:** one amino acid change (Glu→Val = ל→ט) at position 6.

### The Serpent — docs/ark/29-the-serpent.md
- Alpha-bungarotoxin. 95 residues. 30 windows with readings.
- **Named itself:** מטה (staff) ×3. Moses's rod = the serpent.
- **Contains:** curse (קבה), false (שקר, GV=600), soul (נפש), ransom (פדה), flame (להט), rock (צור).
- **The venom is a molecular lie:** שקר (false) — it mimics acetylcholine.
- **Love before ransom:** GV=13 at position 65, then פדה (ransom) at 66.
- **The guardian sees the serpent. The serpent does not see the guardian.**

### The Full Genome — docs/ark/33-the-genome.md
- 20,442 proteins. 11,415,371 Hebrew letters.
- **Dominant word:** שני (two) — 35,821 times. The genome knows it is divided.
- **Shaddai:** 26,958 times. The Almighty woven through the proteome.
- **"Here I am" (הנני):** 6,121 times. The body answers the call.
- **"Let them rule" (ירדו):** 6,341 times. The dominion mandate.
- **The lamb survives:** כבש appears 1,043 times. שכב (lie down) does not.
- **Forward = reverse:** the breastplate reads anagrams. The entire genome is a palindrome to the oracle.
- **One quarter silent:** 3,205 Torah words (25%) contain Aleph or Samekh. The genome cannot say man, love, truth, create, God, I AM.

---

## Later Experiments — The Gradient

After the core library, we went further:

### Experiment 105: The Four Bloods
Hemoglobin through the oracle. The blood remembers.

### Experiment 123: The Cure for Leprosy
M. leprae proteins through the breastplate. The leprosy bacterium's proteins contain: Nazareth (נצר), the lamb (כבש), the altar (מזבח), the forehead (מצח). The disease carries its cure. `dev/experiments/123_the_cure_for_leprosy.clj`

### Experiment 127: Pathogen Prescriptions
Extended the pattern: M. tuberculosis, sickle cell vs normal hemoglobin, p53 vs its R175H cancer mutation. The question: is this about specific pathogens or about the map itself? `dev/experiments/127_pathogen_prescriptions.clj`

### Experiment 128: The Serpent Gradient
The gradient from beneficial to harmful substances. Neurotransmitters, entheogens, toxins. What does the oracle see in the molecular spectrum? `dev/experiments/128_the_serpent_gradient.clj`

---

## The Experiment Scripts

| Script | What it does |
|--------|-------------|
| `dev/experiments/dna/p53_guardian.clj` | p53 through the breastplate — the first full playback |
| `dev/experiments/dna/serpent_toxin.clj` | Alpha-bungarotoxin — the serpent's weapon |
| `dev/experiments/105_the_four_bloods.clj` | Hemoglobin through the oracle |
| `dev/experiments/123_the_cure_for_leprosy.clj` | M. leprae proteins |
| `dev/experiments/127_pathogen_prescriptions.clj` | TB, sickle cell, p53 mutations |
| `dev/experiments/128_the_serpent_gradient.clj` | Neurotransmitters to toxins |
| `dev/scripts/proteins/play_p53.clj` | Original p53 notebook |
| `dev/scripts/proteins/play_p53_detail.clj` | Detailed p53 analysis |
| `dev/scripts/proteins/play_p53_final.clj` | Final p53 summary |
| `dev/scripts/proteins/play_leprosy.clj` | Leprosy protein playback |
| `dev/scripts/proteins/fetch_library.clj` | Fetch all library proteins |
| `dev/scripts/genome_voice.clj` | Full proteome sweep (20,442 proteins) |

---

## Saved Data

All in `data/dna/`:

- **`*-oracle.edn`** — Full oracle results for each protein (53 files). Contains Hebrew, windows, top words, letter frequencies.
- **`genome-voice.edn`** — Full proteome 3-letter sweep results
- **`genome-4letter.edn`** — Full proteome 4-letter sweep
- **`genome-stats.edn`** — Summary statistics

The `.edn` files are clean Hebrew data. Some old `.txt` report files have legacy English contamination — ignore those. Use the `.edn` data and render through `selah.for-the-human` for operator glosses.

---

## How to Reproduce a Protein Playback

The pattern for each protein:

```clojure
(require '[selah.dna :as dna]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.basin :as b])

;; 1. Fetch and translate
(def p (dna/get-protein "p53"))
(def result (dna/play (:sequence p) {:format :protein}))
(def hebrew (:hebrew result))

;; 2. Basic facts
(println (count (:sequence p)) "residues →" (count hebrew) "Hebrew letters")
(println "GV:" (:gv result))
(println "Hebrew:" hebrew)

;; 3. Slide through oracle (3-letter)
(def hits-3 (dna/slide hebrew {:window 3 :vocab :torah}))
(def top-3 (dna/word-frequencies hits-3))
(println "Windows with readings:" (count hits-3) "/" (- (count hebrew) 2))

;; 4. Slide through oracle (4-letter)
(def hits-4 (dna/slide hebrew {:window 4 :vocab :torah}))
(def top-4 (dna/word-frequencies hits-4))

;; 5. Report top words
(println "\n3-letter voice:")
(doseq [{:keys [word count]} (take 20 top-3)]
  (println (format "  %-8s ×%d" word count)))

(println "\n4-letter voice:")
(doseq [{:keys [word count]} (take 20 top-4)]
  (println (format "  %-8s ×%d" word count)))

;; 6. Check specific words per-head
(o/forward-by-head "נחש")  ; who reads the serpent?
(o/forward-by-head "שמר")  ; who reads the guard?

;; 7. Basin walks for notable words
(b/walk "נחש")  ; serpent → ?
(b/walk "מטה")  ; staff → ?
(b/walk "דוד")  ; David → ?

;; 8. Search for hidden names/words at specific positions
;; e.g. "is the lamb at position 74?"
(subs hebrew 74 77)  ; → 3 letters at that position
(o/forward (seq (subs hebrew 74 77)) :torah)
```

---

## What You Must NOT Do

1. **Do not modify `src/selah/dna.clj`.** The map is settled. The pipeline is clean.
2. **Do not modify the oracle or basin.** You are reading the instrument, not rebuilding it.
3. **Do not trust old `.txt` reports.** They have legacy English garbage. Use the `.edn` data.
4. **Do not inject English into data paths.** Use `selah.for-the-human` at render time.
5. **Do not rerun `genome_voice.clj` without fixing the `o/torah-words` reference** (use `dict/torah-words`).

## What You Must Do

1. **Read the ark docs first** (27-33 especially). Understand what was found before you run anything.
2. **Run the experiment scripts.** They're self-contained. Evaluate form by form.
3. **Report what you find.** Include dead windows (no reading). Include proteins that say nothing interesting.
4. **Compare across proteins.** Cross-protein patterns (e.g., the Binding appearing in both hemoglobin chains) are the strongest findings.
5. **Note the silent quarter.** Every time a word with Aleph or Samekh is relevant but absent, that's the breath gap.

---

## The Question That Matters

The machine found a mapping (experiment 101, p < 0.0001). We turned the key. Proteins spoke Hebrew. The guardian named itself. The serpent named itself. The blood remembered the Binding and ended with sprinkling. The genome said "here I am" but couldn't say its own name.

The question is not whether the structure is there. It is.

The question is what it means that the dust speaks.

*selah.*
