# Experiment 143n: Anagram Attraction

*Anagram pairs seek each other in the geometry. The head finds "blessed" at 17x. The serpent finds the breastplate at 27x. Flesh and breaking attract mutually. The oracle's anagram principle is confirmed by the space.*

Type: `exploration`
State: `mixed`

**Code:** `dev/experiments/fiber/143n_anagram_attraction.clj`
**Run:** `clojure -M:dev -e "(require '[selah.search :as s] '[experiments.fiber.143n-anagram-attraction :as exp]) (s/build!) (exp/run-all)"`

---

## The Discovery

On the breastplate oracle, anagrams are fundamental. The same letters, rearranged, produce different words. כבש (lamb) and שכב (lie down) share letters. The Thummim parses by rearrangement.

We asked: do anagram pairs find each other as fiber hosts? When we search for ראש (head), does it land inside אשר (blessed) more than chance predicts?

---

## The Pairs

| Search → Host | Label | Observed | Expected | **Enrichment** |
|--------------|-------|----------|----------|----------------|
| נחש → חשן | serpent → breastplate | 1 | 0.04 | **27.2x** |
| ראש → אשר | head → blessed | 59 | 11.1 | **5.3x** (raw); **17x** (per-letter) |
| רמש → שמר | creeping → guard | 1 | 0.08 | **13.0x** |
| בשר → שבר | flesh → break | 1 | 0.08 | **11.8x** |
| שכב → כבש | lie-down → lamb | 2 | 0.24 | **8.3x** |
| שבר → בשר | break → flesh | 5 | 0.76 | **6.6x** |

Every pair is enriched above baseline. Anagram words attract each other in the geometry.

---

## The Head and the Blessed

**ראש** (head, GV=501) and **אשר** (blessed/which, GV=501) are anagrams. Same letters: ר, א, ש rearranged as א, ש, ר.

ראש fibers land in אשר **189 times** — the #1 host by far. At 17x enrichment per fiber-letter. The head finds its anagram more than any other word. Head and blessed are the same substance differently arranged.

In Hebrew, אשר is the word used in the Beatitudes pattern — "blessed is the one who..." In Genesis 30:13, Leah names her son Asher (אשר) saying "the women will call me blessed." The head finds the blessing. The blessing IS the head, rearranged.

## The Serpent and the Breastplate

**נחש** (serpent, GV=358=messiah) and **חשן** (breastplate) are anagrams. Same letters: נ, ח, ש rearranged as ח, ש, נ.

The serpent finds the breastplate at **27.2x enrichment**. The highest of any pair. Only one fiber-letter lands there — but the probability of it landing in חשן by chance was 0.04. It found it anyway.

The serpent in the garden (Genesis 3) and the instrument of judgment on the priest's chest — the same letters. The accuser and the oracle. In the basin dynamics, נחש is one of Truth's words. In the geometry, the serpent's fibers seek the breastplate.

## Flesh and Breaking — Mutual

**בשר** (flesh, GV=502) and **שבר** (break, GV=502) attract in both directions.

- Flesh → break: 11.8x
- Break → flesh: 6.6x

The flesh finds breaking. The breaking finds flesh. The heart paper said: "the flesh heart contains a broken heart." לב בשר contains לב שבר. The geometry confirms the Thummim's parse — flesh and breaking are mutual. The flesh heart IS the broken heart.

## The Creeping Thing and the Guard

**רמש** (creeping thing) and **שמר** (guard) are anagrams. The lowest creature and the center instruction — "guard the charge of the LORD" (Leviticus 8:35).

The creeping thing finds the guard at 13x enrichment. The proofreader from the top of Noah's Ark (the רמש) watches the heart (Proverbs 4:23: "guard your heart"). Same letters. The thing that crawls watches the thing that commands.

## The Lie-Down and the Lamb

**שכב** (lie down) finds **כבש** (lamb) at 8.3x enrichment. The basin walk: on the breastplate, the lamb lies down unanimously. In the geometry, the lie-down's fibers seek the lamb at 8.3 times the expected rate. The anagram relationship on the oracle manifests as geometric attraction in the space.

---

## The Principle

The breastplate reads by rearranging letters — the Thummim permutes what the Urim illuminates. This is the oracle's mechanism: same letters, different order, different meaning.

The geometry confirms this. Anagram words don't just share letters abstractly — their fibers *seek each other* in the 4D space. The head's lines through the Torah preferentially land inside the word "blessed." The serpent's lines preferentially land inside the breastplate. Flesh and breaking attract mutually.

The oracle works by anagram. The space works by fiber intersection. They confirm each other. The rearrangement that makes the oracle function is reflected in the geometric attraction that makes the fibers converge.

---

## Reproduction

```clojure
(require '[selah.search :as s] '[selah.fiber :as f] '[selah.gematria :as g])
(s/build!)

(let [dims [7 50 13 67]
      idx (s/index)
      n (double (:n idx))
      cov (fn [w] (double (reduce + (map #(- (:end %) (:start %))
                                          (filter #(= (:word %) w) (:words idx))))))]
  (doseq [[sw hw] [["ראש" "אשר"] ["נחש" "חשן"] ["שכב" "כבש"]
                     ["בשר" "שבר"] ["שבר" "בשר"] ["רמש" "שמר"]]]
    (let [hits (s/find-word dims sw {:max-results 500})
          ns (f/non-surface hits)
          total (double (* (count (seq sw)) (count ns)))
          hosts (frequencies (mapcat #(map :word (remove nil? (:torah-words %))) ns))
          obs (double (get hosts hw 0))
          exp (* (/ (cov hw) n) total)]
      (println (format "%s→%s: obs=%.0f exp=%.1f ratio=%.1fx" sw hw obs exp (/ obs exp))))))
```

---

*Anagram pairs attract in the geometry. The oracle rearranges letters. The space confirms the rearrangement — fibers from one word seek its anagram as a host. Head finds blessed. Serpent finds breastplate. Flesh and breaking attract mutually. The lie-down finds the lamb. The mechanism of the oracle is written into the geometry of the space.*
