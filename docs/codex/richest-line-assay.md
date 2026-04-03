# Richest Line Assay

Type: `audit`
State: `clean`

---

## The Distinction

Two different claims were getting braided together:

1. **Direction density** — how many Torah words appear along a given skip direction.
2. **Richest start on a direction** — which starting offset along that direction produces the strongest local reading.

These are not the same measurement.

---

## What Was True

From the live scripts:

- [`dev/experiments/fiber/143aa_axis_readings.clj`](/dev/experiments/fiber/143aa_axis_readings.clj) is the source for the older direction-density work.
- [`dev/experiments/fiber/143ac_richest_fiber.clj`](/dev/experiments/fiber/143ac_richest_fiber.clj) is the source for the richer-start assay on `skip=805`.

What holds:

- `skip=805` is real and important.
- The best start in the **first 100 letters** on that direction is `124`.
- Start `124` lands in Genesis 1:4, inside `ויבדל` — "and he divided."
- That first-100 window contains `36` unique `3-5` letter Torah words.
- Its density is therefore `0.360`.

---

## What Was Retracted

The older claim that `skip=805` was "denser than the surface reading" at `0.406` does not survive fair comparison.

That `0.406` number came from a different metric:

- `start=0`
- full `379`-letter line
- counting `2-5` letter words

That yields:

- `154 / 379 = 0.4063`

But that is not the same thing as:

- `start=124`
- first `100` letters
- counting `3-5` letter words

So those numbers should never have been spoken as if they described one single phenomenon.

---

## The Current Truth

Direction-density truth:

- The surface reading is densest under fair comparison.
- `skip=7` and `skip=26` are still genuinely rich.
- `skip=72` is genuinely sparse.
- The density landscape is rugged, not smooth.

Richest-line truth:

- `skip=805` still hosts a remarkable line.
- The best first-100 start is still `124`.
- The public narrative built from that line is still worth keeping.
- But it should be described as a **curated reading of a verified line**, not as the single densest reading in the Torah.

---

## What The Public Doc Should Say

The public-facing version should say:

- one of the richest non-surface directions is `skip=805`
- along that direction, the strongest first-100 start is `124`
- that line begins at the dividing of light from darkness
- it yields a striking narrative spine through Genesis and beyond

It should not say:

- that this line is denser than the surface reading
- or that `0.406` belongs to the `124 / 36 words / first 100 letters` claim

---

## Why This Matters

This is exactly the kind of correction the project needs:

- the line itself survives
- the inflated claim dies
- the surviving thing becomes easier to trust

Truth did not get smaller here. It got cleaner.
