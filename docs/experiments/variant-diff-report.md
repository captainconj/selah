# Torah Variant Diff Report: MAM vs WLC

Type: `reference`
State: `clean`

**Code:** `src/selah/text/variants.clj`, `dev/scripts/variants/diff_report2.clj`
**Run:** `clojure -M:dev dev/scripts/variants/diff_report2.clj`

**MAM** = Miqra according to the Masorah (Sefaria, qere preferred)
**WLC** = Westminster Leningrad Codex (OSHB 4.20, kethiv preferred)

- **del** = letter in WLC not in MAM (WLC extra)
- **ins** = letter in MAM not in WLC (MAM extra)
- **sub** = different letter at aligned position

## Summary

| | Edits | DEL | INS | SUB | Verses |
|--|-------|-----|-----|-----|--------|
| Genesis | 33 | 11 | 7 | 15 | 30 |
| Exodus | 35 | 13 | 10 | 12 | 31 |
| Leviticus | 17 | 8 | 3 | 6 | 17 |
| Numbers | 65 | 36 | 21 | 8 | 40 |
| Deuteronomy | 49 | 29 | 10 | 10 | 43 |
| **Total** | **199** | **97** | **51** | **51** | **161** |

---

## Detail

### Genesis

| Reference | Type | Detail |
|-----------|------|--------|
| Genesis 13:8 | del | WLC+י |
| Genesis 14:2 | sub | י→ו |
| Genesis 14:22 | del | WLC+י |
| Genesis 14:8 | sub | י→ו |
| Genesis 19:16 | ins | MAM+י |
| Genesis 19:20 | sub | י→ו |
| Genesis 24:33 | sub | י→ו |
| Genesis 25:23 | sub | י→ו |
| Genesis 25:3 | del | WLC+י |
| Genesis 26:7 | sub | י→ו |
| Genesis 27:29 | ins | MAM+ו |
| Genesis 27:3 | del | WLC+ה |
| Genesis 27:31 | del | WLC+ו |
| Genesis 30:11 | ins | MAM+א |
| Genesis 35:23 | sub | ל→ו |
| Genesis 35:23 | sub | ו→ל |
| Genesis 35:5 | ins | MAM+ו |
| Genesis 36:14 | sub | י→ו |
| Genesis 36:5 | sub | י→ו |
| Genesis 39:20 | sub | ו→י |
| Genesis 40:10 | sub | י→ו |
| Genesis 41:35 | ins | MAM+ו |
| Genesis 43:28 | ins | MAM+ו |
| Genesis 45:15 | del | WLC+י |
| Genesis 46:12 | del | WLC+ו |
| Genesis 46:13 | del | WLC+ו |
| Genesis 46:14 | sub | ל→ו |
| Genesis 46:14 | sub | ו→ל |
| Genesis 46:9 | del | WLC+ו |
| Genesis 49:13 | del | WLC+ו |
| Genesis 8:17 | sub | ו→י |
| Genesis 8:20 | ins | MAM+ו |
| Genesis 8:20 | del | WLC+ו |

### Exodus

| Reference | Type | Detail |
|-----------|------|--------|
| Exodus 10:25 | del | WLC+ו |
| Exodus 12:4 | ins | MAM+ו |
| Exodus 14:13 | del | WLC+י |
| Exodus 14:14 | del | WLC+י |
| Exodus 14:22 | ins | MAM+ו |
| Exodus 16:2 | sub | י→ו |
| Exodus 16:7 | sub | ו→י |
| Exodus 19:11 | del | WLC+י |
| Exodus 19:19 | del | WLC+ו |
| Exodus 1:16 | sub | י→ו |
| Exodus 21:8 | sub | א→ו |
| Exodus 23:22 | ins | MAM+ו |
| Exodus 25:22 | ins | MAM+ו |
| Exodus 26:24 | del | WLC+י |
| Exodus 28:28 | ins | MAM+ו |
| Exodus 29:22 | ins | MAM+י |
| Exodus 29:40 | sub | י→ע |
| Exodus 29:40 | sub | ע→י |
| Exodus 32:34 | del | WLC+י |
| Exodus 34:24 | del | WLC+ו |
| Exodus 36:13 | ins | MAM+י |
| Exodus 36:19 | sub | י→ל |
| Exodus 36:19 | sub | ל→י |
| Exodus 37:3 | del | WLC+ו |
| Exodus 37:8 | sub | ת→י |
| Exodus 37:8 | sub | ו→ת |
| Exodus 38:10 | ins | MAM+ו |
| Exodus 39:13 | del | WLC+ו |
| Exodus 39:35 | ins | MAM+ו |
| Exodus 39:4 | sub | ת→י |
| Exodus 39:4 | sub | ו→ת |
| Exodus 4:2 | ins | MAM+ה |
| Exodus 4:3 | del | WLC+י |
| Exodus 6:14 | del | WLC+ו |
| Exodus 8:15 | del | WLC+י |

### Leviticus

| Reference | Type | Detail |
|-----------|------|--------|
| Leviticus 10:1 | ins | MAM+י |
| Leviticus 10:13 | ins | MAM+ו |
| Leviticus 11:21 | sub | א→ו |
| Leviticus 11:4 | del | WLC+י |
| Leviticus 13:6 | sub | י→ו |
| Leviticus 14:10 | del | WLC+י |
| Leviticus 16:8 | del | WLC+ו |
| Leviticus 18:29 | del | WLC+ו |
| Leviticus 19:4 | del | WLC+י |
| Leviticus 20:18 | sub | י→ו |
| Leviticus 20:6 | del | WLC+ו |
| Leviticus 21:5 | sub | ה→ו |
| Leviticus 23:20 | del | WLC+ו |
| Leviticus 23:38 | del | WLC+ו |
| Leviticus 25:30 | sub | א→ו |
| Leviticus 26:45 | ins | MAM+ו |
| Leviticus 5:11 | sub | י→ו |

### Numbers

| Reference | Type | Detail |
|-----------|------|--------|
| Numbers 10:16 | del | WLC+ו |
| Numbers 10:9 | del | WLC+ו |
| Numbers 11:26 | del | WLC+י |
| Numbers 13:26 | del | WLC+ו |
| Numbers 13:29 | ins | MAM+ו |
| Numbers 13:32 | del | WLC+ו |
| Numbers 14:36 | sub | ו→י |
| Numbers 15:39 | ins | MAM+ו |
| Numbers 16:11 | sub | ו→י |
| Numbers 19:7 | del | WLC+ו |
| Numbers 1:16 | sub | י→ו |
| Numbers 1:17 | del | WLC+ו |
| Numbers 20:17 | del | WLC+ו |
| Numbers 21:13 | del | WLC+ו |
| Numbers 21:30 | del | WLC+ו |
| Numbers 21:32 | sub | י→ו |
| Numbers 22:38 | del | WLC+ו |
| Numbers 22:5 | del | WLC+ו |
| Numbers 23:13 | sub | ך→ה |
| Numbers 23:13 | ins | MAM+כ |
| Numbers 23:29 | del | WLC+י |
| Numbers 25:* (block) | del | WLC+ה |
| Numbers 25:* (block) | del | WLC+פ |
| Numbers 25:* (block) | del | WLC+ג |
| Numbers 25:* (block) | del | WLC+מ |
| Numbers 25:* (block) | del | WLC+ה |
| Numbers 25:* (block) | del | WLC+י |
| Numbers 25:* (block) | del | WLC+ח |
| Numbers 25:* (block) | del | WLC+א |
| Numbers 25:* (block) | del | WLC+י |
| Numbers 25:* (block) | del | WLC+ה |
| Numbers 25:* (block) | del | WLC+י |
| Numbers 25:* (block) | del | WLC+ר |
| Numbers 25:* (block) | del | WLC+ו |
| Numbers 26:1 | ins | MAM+ה |
| Numbers 26:1 | ins | MAM+פ |
| Numbers 26:1 | ins | MAM+ג |
| Numbers 26:1 | ins | MAM+מ |
| Numbers 26:1 | ins | MAM+ה |
| Numbers 26:1 | ins | MAM+י |
| Numbers 26:1 | ins | MAM+ר |
| Numbers 26:1 | ins | MAM+ח |
| Numbers 26:1 | ins | MAM+א |
| Numbers 26:1 | ins | MAM+י |
| Numbers 26:1 | ins | MAM+ה |
| Numbers 26:1 | ins | MAM+י |
| Numbers 26:1 | ins | MAM+ו |
| Numbers 26:24 | del | WLC+ו |
| Numbers 26:9 | sub | ו→י |
| Numbers 32:22 | del | WLC+י |
| Numbers 32:7 | sub | ו→י |
| Numbers 33:35 | del | WLC+ו |
| Numbers 33:36 | del | WLC+ו |
| Numbers 33:52 | ins | MAM+ו |
| Numbers 34:11 | del | WLC+ו |
| Numbers 34:4 | sub | ה→ו |
| Numbers 35:19 | del | WLC+י |
| Numbers 3:2 | del | WLC+ו |
| Numbers 3:42 | ins | MAM+ו |
| Numbers 3:43 | del | WLC+ו |
| Numbers 7:23 | del | WLC+ו |
| Numbers 7:7 | ins | MAM+ו |
| Numbers 9:17 | ins | MAM+ו |
| Numbers 9:3 | del | WLC+ו |
| Numbers 9:7 | ins | MAM+י |

### Deuteronomy

| Reference | Type | Detail |
|-----------|------|--------|
| Deuteronomy 10:11 | ins | MAM+י |
| Deuteronomy 12:20 | del | WLC+ו |
| Deuteronomy 18:22 | del | WLC+ו |
| Deuteronomy 1:15 | ins | MAM+ו |
| Deuteronomy 20:1 | del | WLC+י |
| Deuteronomy 21:15 | del | WLC+ו |
| Deuteronomy 21:7 | sub | ה→ו |
| Deuteronomy 22:14 | del | WLC+י |
| Deuteronomy 24:13 | ins | MAM+ו |
| Deuteronomy 25:7 | del | WLC+י |
| Deuteronomy 28:18 | del | WLC+ו |
| Deuteronomy 28:27 | sub | ל→ר |
| Deuteronomy 28:27 | sub | פ→ח |
| Deuteronomy 28:27 | sub | ע→ט |
| Deuteronomy 28:30 | sub | ל→ב |
| Deuteronomy 28:30 | sub | ג→כ |
| Deuteronomy 28:49 | del | WLC+ו |
| Deuteronomy 28:52 | del | WLC+ו |
| Deuteronomy 28:58 | del | WLC+ו |
| Deuteronomy 28:59 | del | WLC+ו |
| Deuteronomy 29:22 | sub | י→ו |
| Deuteronomy 2:23 | del | WLC+ו |
| Deuteronomy 30:18 | ins | MAM+ו |
| Deuteronomy 30:19 | del | WLC+י |
| Deuteronomy 30:9 | del | WLC+ו |
| Deuteronomy 32:13 | del | WLC+ו |
| Deuteronomy 32:24 | del | WLC+ו |
| Deuteronomy 32:27 | del | WLC+י |
| Deuteronomy 32:34 | ins | MAM+ו |
| Deuteronomy 32:7 | del | WLC+ו |
| Deuteronomy 32:7 | del | WLC+ו |
| Deuteronomy 33:12 | del | WLC+י |
| Deuteronomy 33:19 | del | WLC+ו |
| Deuteronomy 33:25 | del | WLC+י |
| Deuteronomy 34:11 | del | WLC+ו |
| Deuteronomy 3:25 | del | WLC+ו |
| Deuteronomy 3:5 | del | WLC+ו |
| Deuteronomy 4:3 | ins | MAM+ו |
| Deuteronomy 4:42 | del | WLC+ו |
| Deuteronomy 4:42 | del | WLC+ו |
| Deuteronomy 5:* (block) | sub | ו→י |
| Deuteronomy 6:21 | del | WLC+ו |
| Deuteronomy 6:9 | sub | ז→ו |
| Deuteronomy 6:9 | sub | ו→ז |
| Deuteronomy 7:16 | ins | MAM+ו |
| Deuteronomy 8:12 | del | WLC+ו |
| Deuteronomy 8:2 | ins | MAM+ו |
| Deuteronomy 8:3 | ins | MAM+י |
| Deuteronomy 9:15 | ins | MAM+ו |
