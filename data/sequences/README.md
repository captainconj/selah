# Sequence Data (not in git)

The human proteome FASTA file is too large for version control.

## To regenerate

Download the reviewed human proteome from UniProt:

```
https://rest.uniprot.org/uniprotkb/stream?format=fasta&query=%28%28organism_id%3A9606%29+AND+%28reviewed%3Atrue%29%29
```

Save as `human-proteome-reviewed.fasta` in this directory.

~20,442 proteins, ~14MB.
