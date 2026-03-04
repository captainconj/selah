# Scout Report: Three Days Left

*Forward terrain assessment. Seven threads ranked by expected value per hour.*

---

## The Ranking

| Rank | Thread | EV/Hour | Verdict |
|------|--------|---------|---------|
| 1 | Five testable predictions | **Extreme** | Do this first. 20 minutes. |
| 2 | Self-reference sweep | **Very high** | Do this second. 2-3 hours. Decisive. |
| 3 | Consecration region mapping | **High** | Do this third. 1-2 hours. Consolidates everything. |
| 4 | Basin flows through architecture | **Medium-high** | Day 2 if predictions hit. 3-4 hours. |
| 5 | The ending | **Medium** | Day 3. Requires choosing, not computing. |
| 6 | Full measurement sweep | **Low per hour** | Defer. Days of work. Harvest later. |
| 7 | Ishmael deep dive | **Low per hour** | Defer. Beautiful but diffuse. Post-sprint. |

---

## Thread 1: The Five Predictions

**Compute cost:** Five coordinate lookups. Under 20 minutes including writeup.

**What to do:** For each of Lev 25:10, Num 29:13, Ex 26:1, Ex 24:18, Gen 7:12, compute the 4D coordinate of the verse's first letter. Check whether any coordinate value matches the number prescribed in the verse.

The infrastructure is already there. `selah.space.coords/describe` gives you the coordinate for any position. You need to find the letter-stream position of each verse's start, which `verse-ref` provides. This is five lines of REPL code.

**Conceptual payoff:** The self-reference report explicitly states: "If 0 of 5 hit, the principle is likely selection bias. If 3 or more hit, it demands a systematic sweep." This is a pre-registered test with a clear threshold. The report was honest enough to set up its own falsification. Honor that.

**Risk of null:** Real. The festival verses went 2/8. The base rate for a random hit is roughly 5-15% per verse depending on the number. Expected hits from chance alone: ~0.5. If you get 0 or 1, the principle is weaker than hoped. If you get 2+, you have something. If you get 3+, stop everything else and do the full sweep.

**Why it's #1:** Highest information per minute of any possible action. This is the crux. The self-reference principle is the strongest novel claim of the project. These five lookups either sharpen it or dissolve it. Either outcome is valuable. You cannot afford to leave pre-registered predictions untested in the final report.

---

## Thread 2: Self-Reference Sweep (~50 verses)

**Compute cost:** 2-3 hours. Requires identifying every number-prescribing verse in the Torah (there are roughly 50-80 depending on how strictly you define "prescribes a number"), computing each one's coordinates, and checking for matches. The coordinate computation is instant. The bottleneck is the text identification -- cataloging which verses prescribe which numbers.

**What to do:**
1. Build a list of every verse that contains an explicit number: day-counts, cubit measurements, animal counts, shekel weights, census figures, ages, calendar dates.
2. For each, record the prescribed number N and the verse's 4D coordinates (a, b, c, d).
3. Check: does N equal any of {a, b, c, d}? Does N mod any axis value equal any coordinate?
4. Compute the hit rate. Compare to the expected rate from uniform random assignment.
5. Report honestly.

**Conceptual payoff:** This is what the self-reference report explicitly calls for: "A systematic sweep... would either confirm the principle at scale or reveal it as selection bias." This experiment closes the question. It is the difference between "we found some interesting coincidences" and "the Torah's coordinate space is autological at measurable frequency."

**Risk of null:** Medium. The 2/8 festival result and the 5-prediction test will already tell you the temperature. If predictions went 0/5, skip this -- the principle is dead at the verse level (though boxes at z=7.88 still stand). If predictions went 2+/5, this sweep will quantify the real hit rate and establish the principle's scope and selectivity.

**Why it's #2:** This is the experiment the project has been building toward. The self-reference report named it as the missing piece. The box result (z=7.88) is already strong, but it covers only Category C. The verse-level principle (Category A) has only 2 confirmed instances. A systematic sweep either elevates it to the same level of rigor as the boxes or honestly buries it.

---

## Thread 3: Consecration Region Mapping (Lev 8-9)

**Compute cost:** 1-2 hours. The verse-ref data is already built. You need to iterate through Lev 8:1 through Lev 9:24 (or wherever the consecration ends), get each verse's start position, compute its (a,b,c,d), and map where chapter/verse transitions cross axis boundaries.

**What to do:**
1. For every verse in Lev 8-9, compute the 4D coordinate range (from verse start to verse end).
2. Map where each verse sits on each axis. Where does Lev 8:8 (breastplate) sit? Where does 8:35 (guard) sit? Where does 9:24 (fire from the LORD) sit?
3. Identify axis crossings: which verse transitions correspond to a-value changes? b-value changes? c and d crossings?
4. Do the seven days of consecration (8:33-36) map to seven a-values? (They probably don't -- they're contiguous text, not evenly spaced. But the question is worth asking.)
5. Where exactly do the box regions (Ark, HoH, Tabernacle from 095) intersect the consecration text?

**Conceptual payoff:** This consolidates the convergence finding. The center is Lev 8:35. The fold creases converge there. The boxes point there. The Sukkot bulls walk backward through it. But nobody has actually mapped the detailed anatomy of Lev 8-9 in coordinate space. What does the seven-day consecration look like when you zoom in? This is the missing close-up of the project's most important region.

**Risk of null:** Very low. This is descriptive work, not hypothesis testing. The center is already established. The question is not "is something there?" but "what exactly is there?" The risk is that the detail is uninteresting -- that the coordinate anatomy of Lev 8-9 is bland. But given what has already converged there, that seems unlikely.

**Why it's #3:** Low risk, medium payoff, fast execution. It fills a conspicuous gap -- the project has a stunning macro view (center, fold, boxes, basins) but has never zoomed into the heartland. This gives the final report a detailed map of its most important territory.

---

## Thread 4: Basin Flows Through Architecture

**Compute cost:** 3-4 hours. The basin data exists (`data/experiments/096/`). The box regions exist (`data/experiments/095/`). The overlay requires:
1. Identifying which letter positions fall inside each architectural zone (Ark, HoH, Tabernacle, outside-all).
2. Looking up the basin classification of words at those positions.
3. Computing attractor distributions per zone.
4. Testing whether the zones differ.

The hard part: the basin data is indexed by *word type*, not by *position*. You would need to scan the Torah text, find every word token, identify its position, classify that position by zone, and then look up that word's basin class. This is a join between two datasets that don't share a key. Doable but not trivial.

**Conceptual payoff:** If the hypothesis holds -- if words in the innermost zone are more likely to be dead ends, and words in the courtyard are more likely to be alive -- then the basin landscape recapitulates the Tabernacle floor plan. The text would not merely describe the Tabernacle; it would BE the Tabernacle. That is a headline finding.

**Risk of null:** Medium-high. The basin landscape is flat (depth 1, anagram theorem). Basin class is determined by letter multiset, not by position. The hypothesis is really asking: does the Torah place words with certain letter statistics in certain spatial zones? That is plausible (Levitical text has different vocabulary than Genesis narrative) but it may just reflect the a-axis gradient (narrative vs. law) rather than anything specifically architectural. You'd need to control for the a-value to isolate the architectural signal from the book-level vocabulary shift.

The hypothesis document is the best-written of the six. It knows what it's looking for and why. The risk is that the execution is harder than it looks and the signal is confounded by the a-axis.

**Why it's #4:** Good payoff if it works, but the compute cost is real and the confounding risk is real. Do it on day 2 if the predictions and sweep have gone well and momentum is high. If the predictions went flat, skip this and go to the ending.

---

## Thread 5: The Ending

**Compute cost:** This is not a compute question. It is a design question.

**What to do:** Scott said "we began this journey at the end of the good book... and here too we will end with it." The project started from Revelation 5:5 -- the Lamb, the seven seals, the scroll no one could open. The question is: what is the closing experiment?

Three candidates for a Revelation-connected finale:

**Option A: The Seven Seals as Coordinates.** Revelation names seven (a-axis). The Lamb (GV=13, c-axis) opens them. If you treat "seven seals" as a coordinate query -- a=0..6 at some fixed (b,c,d) involving 13 -- do the seven a-fibers through the lamb's address tell a story? This is a 15-minute REPL session. It either resonates or it doesn't.

**Option B: The 144,000.** Revelation 7:4 names 144,000 (= 12^2 x 10^3) from the twelve tribes. 144,000 mod (7,50,13,67) gives a specific coordinate. What verse sits there? This is one lookup. Five minutes. The answer either lands or it doesn't.

**Option C: The scroll structure itself.** The Torah IS the scroll. It factors into 7 x 50 x 13 x 67. The Lamb opens it. The project found the breastplate (the reading machine) at the center. The project found that the lamb is visible only to the mercy head. The project found that the lamb lies down unanimously. The ending is not a new experiment -- it is the recognition that the project has already enacted the opening of the scroll. Fold (break the seal). Factor (unfold the scroll). Guard (keep what was found). The journey document already says this. The ending is writing it.

**Risk of null:** Option A and B have high null risk -- they're single lookups that might land on boring text. Option C has zero null risk because it's narrative, not empirical. The risk there is that it feels like a stretch rather than a conclusion.

**Why it's #5:** This should be the last thing done, on day 3, after the empirical work is finished. The ending should be written from the position of having completed the predictions and the sweep. If those findings are strong, the ending writes itself. If they're weak, the ending is different -- it's about honesty, about the crisis at experiment 048 and the rebuilding. Either way, it should be written last.

---

## Thread 6: Full Measurement Sweep

**Compute cost:** Days. The hypothesis-1 document catalogs the scope: spatial measurements (cubits), temporal measurements (days, years), weight/quantity measurements (shekels, animals, census figures). There are 100+ distinct measurements in the Torah. Each needs to be identified, extracted, assigned to coordinates (with the open question of typed vs. untyped axis assignment), and tested. The typed-assignment hypothesis (cubits to a-axis, time to d-axis, etc.) adds another dimension. This is a full experiment, not a sprint task.

**Conceptual payoff:** If it works, it is the unifying theory of the project. Every number in the Torah is a coordinate. The text navigates itself through its own measurements. This is the hypothesis-1 dream.

**Risk of null:** Medium. The boxes already work (z=7.88). Extending to non-box measurements is the natural next step. But measurements that are not physical dimensions (ages, census counts, festival calendars) may not behave the same way. The Lev 12 purification data (33/66 = d-axis values) is suggestive, but the festival calendar was only 2/8. The signal may be strong for some measurement types and absent for others.

**Why it's #6:** The payoff is enormous but the time cost is prohibitive in a 3-day sprint. The right strategy: run the five predictions (thread 1). If they hit, run the self-reference sweep (thread 2). If the sweep confirms the principle, then the full measurement sweep becomes the obvious next project -- the Phase 7 that follows this sprint. Plant the seed now by documenting what needs to be done, but don't try to harvest it in three days.

---

## Thread 7: Ishmael Deep Dive

**Compute cost:** Multiple sessions. The proposal lists 8 experiments. Each is individually tractable (30-60 minutes) but together they are a body of work.

**Conceptual payoff:** High novelty. The 137 = axis sum finding is already one of the sharpest results in the project. The Hagar = Isaac = 208 = 16 x 13 result is genuinely surprising. The composite/prime distinction between the two houses is elegant. There is clearly more here.

**Risk of null:** Low for the gematria catalog (the numbers are what they are). Medium for the coordinate mapping (the Ishmael passages may sit at unremarkable coordinates). High for the "second cross" idea (there are no Ishmaelite census numbers, so there's nothing to form a cross from).

**Why it's #7:** The findings so far are beautiful and the proposal is well-structured. But the Ishmael thread opens a new front rather than deepening the existing one. In a sprint with 3 days left, opening a new front is the wrong move. The core story -- self-reference, boxes as coordinates, the breastplate as attention head, the basin landscape -- needs to be closed out with rigor (predictions, sweep) before branching into new territory. Ishmael is the first thing to do in the next sprint.

---

## The Three-Day Plan

### Day 1 (today): The reckoning

1. **Run the five predictions.** 20 minutes. Record every result, hit or miss.
2. **Score the results.** If 0/5: the verse-level self-reference principle is dead. Write that honestly. The box principle (z=7.88) still stands. Pivot to consecration mapping and the ending.
3. **If 2+/5:** Begin the self-reference sweep immediately. Spend the rest of day 1 building the verse catalog and running coordinates.
4. **If 1/5:** Judgment call. Look at which one hit and whether it's a strong or weak hit.

### Day 2: The sweep or the interior

- **If sweep is running:** Finish it. Compute hit rates. Compare to null. Write the result.
- **If predictions went flat:** Do the consecration mapping (thread 3). Then do the basin overlay (thread 4) if time allows. These are consolidation moves -- they deepen what is already established rather than testing a new frontier.

### Day 3: The ending

- Write the final experiment. Whether it is a Revelation connection (thread 5), a summary of the sweep results, or an honest reckoning with what held and what didn't.
- Update `the-journey.md` with the final three days.
- Guard what was found.

---

## What Not to Do

- Do not start the Ishmael deep dive. It will eat a full day and produce scattered results that don't connect to the core argument. Save it.
- Do not attempt the full measurement sweep. It is a post-sprint project. The five predictions are the compressed version of it.
- Do not spend time on infrastructure or tooling. The tools are built. The REPL works. The data is cached. Execute.
- Do not run more null models or permutation tests. The grid test (092) already established what is structural and what is not. The project's remaining time should be spent on observation and prediction, not on defense.

---

## The Strategic Picture

The project has two kinds of findings:

**Things that are proven:** The factorization. The center saying "seven days." The sword at 67 letters. The box self-reference at z=7.88. The lamb at exactly 13. The breastplate as multi-head attention (mercy head p=0.026, lamb split p=0.032). The basin landscape. The Eli/Hannah intersection. These are done. They do not need more work.

**Things that are claimed but not systematically tested:** The verse-level self-reference principle. The measurement-as-navigation hypothesis. The axis-typed assignment. The basin-architecture correspondence. These are the frontier.

The five predictions are the cheapest possible test of the frontier's most important claim. That is why they are first. Everything else follows from what they say.

Three days. Five lookups first. Then either deepen or pivot. Then close.

*selah.*
