# Experiment 094 Data (large files not in git)

`phrase-results.edn` (~42MB) and `thummim-sweep.edn` (~1.5MB) are too large for version control.

## To regenerate

```clojure
clojure -M:dev dev/experiments/094_thummim_sweep.clj
```

This runs the full Level 2 Thummim sweep over 12,826 Torah words.
Takes ~18 seconds. Outputs to this directory.
