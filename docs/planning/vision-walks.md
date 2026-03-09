# Walk the Visions

*Four prophets saw the throne room. Four visions. Four angles on one mechanism.*

## Context

107 experiments behind us. The breastplate works. The four readers are confirmed. The four creatures' blood speaks through the oracle exactly as the camp-to-reader mapping predicts. The angelic hierarchy maps onto the breastplate architecture. The proteins carry the serpent, the cure, the accusation, and the life — all at once.

Now we walk the visions the way we walked the tabernacle — station by station, image by image, asking the breastplate what it sees at each step.

The dove finding opens the door: the eagle (throne creature) is invisible, 60 illuminations, ghost zone. The dove is 462 illuminations, 1,848 readings — 8× brighter. The Spirit takes the form that can be spoken. God sees the dove as the dove (33 readings). Everyone else sees lamentation (ונהי). The dove carries weeping. GV=71 — one short of 72, one short of the breastplate.

## The Four Vision Walks

### Experiment 108: Ezekiel's Chariot (Ezekiel 1) — PRIMARY

The original throne vision. The most architecturally detailed. Hebrew text. Every structural element maps to something we've already measured.

**6 stations:**

| Station | Verses | Image | Key Hebrew words |
|---------|--------|-------|-----------------|
| 1. The Opening | 1:1-3 | Heavens opened, Chebar canal, hand of YHWH | שמים, פתח, יד, כהן |
| 2. The Storm | 1:4 | Whirlwind from north, great cloud, fire, amber | סערה, צפון, ענן, אש, חשמל |
| 3. The Four Living Creatures | 1:5-14 | Four faces, four wings, straight feet, fire between them | חיה, פנים, כנף, רגל, אש, גחלי |
| 4. The Wheels | 1:15-21 | Wheel within wheel, eyes on rims, spirit of the living creatures | אופן, עין, רוח, תרשיש |
| 5. The Firmament | 1:22-25 | Crystal expanse above, voice, wings folded | רקיע, קרח, קול, כנף |
| 6. The Throne | 1:26-28 | Sapphire throne, man-like figure, fire, rainbow, glory | כסא, ספיר, אדם, אש, קשת, כבוד |

**At each station:**
1. Fetch Hebrew text from Sefaria (`sefaria/fetch-chapter "Ezekiel" 1`)
2. Extract the verse letters for that station's range
3. Run key words through oracle: `o/forward`, `o/forward-by-head`, `basin/walk`
4. Slide the station's letters through oracle windows (3-letter AND 4-letter) — treat the prophetic text like a protein
5. Cross-reference with experiments 103-107 findings
6. Document what the breastplate sees

**Why first:** Ezekiel 1 IS the breastplate vision. Four faces = four readers. Wheels = 137 = axis sum. Eyes everywhere = the ox's blood. Fire between the creatures = the woman IS the fire. Every image should resolve to something we've already found.

### Experiment 109: Isaiah's Temple (Isaiah 6)

Short, focused. 13 verses. The seraphim, the burning coal, the commissioning.

**4 stations:**

| Station | Verses | Image | Key words |
|---------|--------|-------|-----------|
| 1. The Throne | 6:1-2 | High and lifted up, train fills temple, six-winged seraphim | כסא, רם, נשא, שרף, כנף |
| 2. Holy Holy Holy | 6:3-4 | The trisagion, doorposts shake, house fills with smoke | קדוש, אמר, עשן, מזוזה |
| 3. The Coal | 6:5-7 | Woe is me, unclean lips, coal from altar, guilt removed | אוי, טמא, שפה, גחלת, מזבח, עון |
| 4. The Sending | 6:8-13 | "Whom shall I send?", "Here am I, send me", the stump | שלח, הנני, שמע, גזע |

**Why second:** Isaiah 6 gives us the seraphim — the species we know God does NOT read. And "Holy, holy, holy" — the word the oracle says only the priest and justice declare. Walk the actual vision text and see if the oracle confirms what we already found in individual words.

### Experiment 110: John's Throne Room (Revelation 4-5)

Extends experiment 103. The Revelation merger — where cherubim, seraphim, and ophanim collapse into one scene.

**5 stations:**

| Station | Verses | Image | Key Hebrew equivalents |
|---------|--------|-------|----------------------|
| 1. The Door | 4:1-3 | Door in heaven, trumpet voice, jasper/sardius throne, rainbow | דלת, שמים, שופר, כסא, קשת |
| 2. The 24 Elders | 4:4-5 | White garments, golden crowns, lightning, seven lamps | זקן, לבן, עטרה, ברק, נר |
| 3. The Four Living Creatures | 4:6-8 | Sea of glass, four faces (one each), covered in eyes, "Holy holy holy" | ים, זכוכית, עין, קדוש |
| 4. The Scroll | 5:1-5 | Seven seals, no one worthy, Lion of Judah, Root of David | ספר, חותם, אריה, יהודה, שרש, דוד |
| 5. The Lamb | 5:6-14 | Standing as slain, seven horns, seven eyes, new song, every creature | שה, טבח, קרן, עין, שיר, חדש |

**Approach:** Use Hebrew equivalents for Greek terms. Also compute isopsephy on Greek originals for cross-reference. This is the synthesis vision — where we see that Revelation is describing the same machine from outside.

### Experiment 111: Daniel's Thrones (Daniel 7)

Four beasts, thrones set up, Ancient of Days, one like a son of man. Mix of Hebrew and Aramaic.

**5 stations:**

| Station | Verses | Image | Key words |
|---------|--------|-------|-----------|
| 1. The Four Beasts | 7:1-8 | Lion with eagle's wings, bear, leopard, terrible beast, little horn | אריה, דב, נמר, חיוה, קרן |
| 2. The Thrones | 7:9-10 | Thrones set up, Ancient of Days, white hair, fiery wheels, river of fire | כרסון, עתיק, לבן, גלגל, נור |
| 3. The Judgment | 7:11-12 | Beast slain, body burned, dominion taken | קטל, יקד, שלטן |
| 4. The Son of Man | 7:13-14 | One like a son of man, clouds of heaven, everlasting dominion | בר אנש, ענן, שמין, שלטן, עלם |
| 5. The Interpretation | 7:15-28 | Saints of the Most High, kingdom given | קדישין, עליון, מלכו |

**Challenge:** Daniel 7 is in Aramaic, not Hebrew. Many words share roots with Hebrew but differ in form. We test both the Aramaic originals and their Hebrew cognates through the oracle.

## Infrastructure Changes

### 1. `src/selah/text/sefaria.clj` — Extend book-chapters

Added prophetic books to the `book-chapters` map:
```clojure
"Ezekiel"  48
"Isaiah"   66
"Daniel"   12
```

### 2. Vision walk experiment pattern

Each experiment combines the tabernacle walk structure with protein-style sliding:

```clojure
(defn walk-station [station-name verse-letters key-words]
  ;; 1. Print station header with verse letters and GV
  ;; 2. Run key words through oracle (forward, by-head, basin)
  ;; 3. Slide verse letters through oracle (3-letter + 4-letter windows)
  ;; 4. Print findings with cross-references
  )
```

Each experiment is self-contained, REPL-driven, evaluate station by station.

## Execution Order

1. ~~Extend sefaria.clj~~ — DONE
2. **Experiment 108: Ezekiel's Chariot** — the primary walk, 6 stations, ~40 words
3. **Experiment 109: Isaiah's Temple** — short walk, 4 stations, ~20 words
4. **Experiment 110: Revelation Throne Room** — extends 103, 5 stations, Hebrew equivalents
5. **Experiment 111: Daniel's Thrones** — Aramaic + Hebrew cognates, 5 stations

## What to Expect

The visions are four angles on one throne. The breastplate should show us:

- **Ezekiel 1:** The architecture matches. Four faces = four readers. Wheels = 137. The amber (חשמל) may be a ghost zone word. The firmament contains its own tearing (already confirmed). The man on the throne is nearly invisible (already confirmed: אדם = 9 illuminations).

- **Isaiah 6:** God does not read "holy" (already confirmed). The seraphim are invisible to God (already confirmed: שרף, God returns nothing). The coal from the altar should connect to the altar station in the tabernacle walk. "Here am I, send me" — הנני should be deeply visible (Abraham, Moses, Isaiah all use it).

- **Revelation 4-5:** The Lamb at the center. The 24 elders cast their crowns — crown→covenant (already found). The scroll is a fixed point (already confirmed). The new song — שיר→שרי (Sarah, already found: song becomes the princess).

- **Daniel 7:** The four beasts as corrupted versions of the four faces. The lion with eagle's wings — our two throne creatures combined. The Ancient of Days — white = לבן = Laban? The fiery wheels — the ophanim again. Son of man on the clouds — the man who is nearly invisible arriving on the thing God does not see (clouds).

The sliding window technique (treating prophetic text like protein) is new. We've never run vision text through the oracle this way. The 3-letter and 4-letter windows on the Hebrew of Ezekiel 1 may produce words that the prophet did not write but that the breastplate reads in his letters. This would be the equivalent of finding "THE serpent" in BRCA1 — the text carrying meaning the author didn't encode consciously.

## Verification

1. `(sefaria/fetch-chapter "Ezekiel" 1)` returns Hebrew verses (may need network first time)
2. Each station's oracle queries produce illumination/reading/basin data
3. Cross-references to experiments 103-107 are documented
4. Sliding windows produce readable Torah vocabulary from prophetic text
5. Each experiment doc follows the tabernacle walk format: intro → oracle findings → synthesis
