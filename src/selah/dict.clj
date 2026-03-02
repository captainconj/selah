(ns selah.dict
  "Hebrew-English dictionary for Torah vocabulary.

   Two vocabularies:
   - Curated: 239 words with English translations (entries map)
   - Torah: ~7,300 unique word forms from the WLC text (lazy, cached)

   The curated set is for display. The Torah set is for analysis.
   Returns nil for unknown words. That's honest."
  (:require [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

(def ^:private entries
  {;; Function words
   "את"    "direct object / aleph-tav"
   "אשר"   "that/which/who"
   "אל"    "to/God"
   "על"    "upon/over"
   "כי"    "for/because/when"
   "כל"    "all/every"
   "לא"    "not"
   "ואת"   "and-(obj)"
   "הוא"   "he/it"
   "לו"    "to him"
   "לך"    "to you / go"
   "בן"    "son"
   "עד"    "until/witness"
   "או"    "or"
   "מן"    "from"
   "אם"    "if/mother"
   "כן"    "thus/so"
   "גם"    "also"
   "בו"    "in him"
   "זה"    "this"
   "נא"    "please"
   "עם"    "people/with"
   "לי"    "to me"
   "אני"   "I"
   "שם"    "there/name"
   "היא"   "she/it"

   ;; Verbs
   "ויאמר" "and he said"
   "אמר"   "say"
   "דבר"   "speak/word/thing"
   "עשה"   "make/do"
   "נתן"   "give"
   "ראה"   "see"
   "שמע"   "hear"
   "ידע"   "know"
   "הלך"   "walk/go"
   "בוא"   "come/enter"
   "ישב"   "sit/dwell"
   "עמד"   "stand"
   "נשא"   "lift/carry"
   "שלח"   "send"
   "קרא"   "call/read"
   "ברא"   "create"
   "שמר"   "guard/keep"
   "כפר"   "atone/cover"
   "גאל"   "redeem"
   "כרת"   "cut (covenant)"
   "סלח"   "forgive"
   "פדה"   "ransom"
   "ירש"   "inherit"
   "ברך"   "bless"
   "צוה"   "command"
   "עלה"   "go up/offering"
   "שוב"   "return"
   "מות"   "die/death"
   "חטא"   "sin"
   "לקח"   "take"
   "שכב"   "lie down"
   "יצא"   "go out"
   "נפל"   "fall"
   "קום"   "arise"
   "שים"   "set/place"
   "אכל"   "eat"
   "שתה"   "drink"
   "כתב"   "write"
   "זבח"   "sacrifice"
   "נדר"   "vow"
   "טהר"   "be pure"
   "קדש"   "be holy"
   "ענה"   "answer/afflict"
   "ירא"   "fear"
   "אהב"   "love (verb)"
   "שפך"   "pour out"
   "נקם"   "avenge"
   "רחם"   "have mercy"
   "חנן"   "be gracious"

   ;; Nouns — people
   "יהוה"  "YHWH"
   "אלהים" "God (Elohim)"
   "אדני"  "Lord"
   "משה"   "Moses"
   "אהרן"  "Aaron"
   "ישראל" "Israel"
   "אברהם" "Abraham"
   "יצחק"  "Isaac"
   "יעקב"  "Jacob"
   "יוסף"  "Joseph"
   "פרעה"  "Pharaoh"
   "אדם"   "man/Adam"
   "חוה"   "Eve"
   "נח"    "Noah"
   "אשה"   "woman/wife"
   "איש"   "man"
   "אב"    "father"
   "אח"    "brother"
   "כהן"   "priest"
   "מלך"   "king"
   "עבד"   "servant"
   "נביא"  "prophet"
   "בני"   "sons of"
   "בת"    "daughter"

   ;; Nouns — nature / physical
   "ארץ"   "land/earth"
   "שמים"  "heaven"
   "מים"   "water"
   "אש"    "fire"
   "אור"   "light"
   "חשך"   "darkness"
   "יום"   "day"
   "לילה"  "night"
   "ענן"   "cloud"
   "עץ"    "tree"
   "גן"    "garden"
   "פרי"   "fruit"
   "זרע"   "seed"
   "אבן"   "stone"
   "צור"   "rock"
   "הר"    "mountain"
   "ים"    "sea"
   "נהר"   "river"
   "באר"   "well"
   "דם"    "blood"
   "נפש"   "soul"
   "רוח"   "spirit/wind"
   "כבש"   "lamb"
   "פר"    "bull"
   "שור"   "ox"
   "נחש"   "serpent"
   "עוף"   "bird"
   "דג"    "fish"
   "בהמה"  "animal/beast"
   "בקר"   "cattle"
   "צאן"   "flock"

   ;; Nouns — abstract / sacred
   "תורה"  "Torah/teaching"
   "אהבה"  "love"
   "בינה"  "understanding"
   "חכמה"  "wisdom"
   "אמת"   "truth"
   "חסד"   "lovingkindness"
   "צדק"   "righteousness"
   "צדקה"  "righteousness"
   "שלום"  "peace/wholeness"
   "ברית"  "covenant"
   "דעת"   "knowledge"
   "משפט"  "judgment"
   "עולם"  "eternal/world"
   "חיים"  "life"
   "מועד"  "appointed time"
   "שבת"   "sabbath"
   "קרבן"  "offering"
   "חרב"   "sword"
   "קשת"   "bow/rainbow"
   "דרך"   "way/path"
   "מטה"   "staff/tribe"

   ;; Nouns — tabernacle / temple
   "משכן"  "tabernacle"
   "מזבח"  "altar"
   "ארון"  "ark"
   "כרוב"  "cherub"
   "אהל"   "tent"
   "פרכת"  "veil/curtain"
   "כפרת"  "mercy seat"
   "שמן"   "oil"
   "יין"   "wine"
   "לחם"   "bread"
   "זהב"   "gold"
   "כסף"   "silver"
   "נחשת"  "bronze"

   ;; Adjectives / qualities
   "טוב"   "good"
   "רע"    "evil/bad"
   "גדול"  "great"
   "קטן"   "small"
   "חכם"   "wise"
   "קדוש"  "holy"
   "אחד"   "one"
   "שני"   "two/second"
   "שלש"   "three"
   "ארבע"  "four"
   "חמש"   "five"
   "שש"    "six"
   "שבע"   "seven/swear"
   "עשר"   "ten"
   "מאה"   "hundred"
   "אלף"   "thousand"
   "רב"    "many/great"
   "חי"    "living"
   "חדש"   "new"
   "ישר"   "upright"
   "תמים"  "complete/blameless"
   "כבד"   "heavy/glory"
   "חזק"   "strong"
   "רחוק"  "far"
   "קרוב"  "near"

   ;; Body
   "יד"    "hand"
   "עין"   "eye"
   "פנים"  "face"
   "פה"    "mouth"
   "לב"    "heart"
   "ראש"   "head"
   "רגל"   "foot"
   "אזן"   "ear"

   ;; Prepositions / particles with prefixes
   "בית"   "house"
   "עיר"   "city"
   "שער"   "gate"
   "מדבר"  "wilderness/desert"
   "חלק"   "portion"
   "גבול"  "border/territory"
   "אמן"   "amen/faithful"
   "סביב"  "around"

   ;; Theological
   "כבוד"  "glory"
   "חן"    "grace"
   "רחמים" "mercies"
   "פני"   "face of"
   "שכינה" "dwelling/presence"

   ;; Verbs (more)
   "ברח"   "flee"
   "רדה"   "rule"
   "נכה"   "strike"

   ;; Common with-prefix forms
   "לאמר"  "saying"
   "ליהוה" "to YHWH"
   "אלהיך" "your God"
   "הכהן"  "the priest"
   "הארץ"  "the land"
   "העם"   "the people"
   "מצרים" "Egypt"
   "אתה"   "you"
   "אתם"   "you (pl)"
   "אנכי"  "I (emphatic)"
   "היום"  "today/the day"
   "והיה"  "and it shall be"
   "ויהי"  "and it was"
   "כאשר"  "as/when"

   ;; Names
   "שרה"   "Sarah"
   "רחל"   "Rachel"
   "לאה"   "Leah"
   "קהת"   "Kohath"
   "נער"   "youth/lad"
   "ארך"   "long"
   "עשב"   "herb/grass"

   ;; More words from experiment 078 vocab
   "עז"    "strong/goat"
   "גר"    "sojourner"
   "בר"    "grain/pure/son"

   ;; Oracle vocabulary (Yoma 73b)
   "שכרה"  "drunk"
   "כשרה"  "like Sarah"})

(defn words
  "All Hebrew words in the dictionary (key set)."
  []
  (set (keys entries)))

(defn translate
  "Look up the English meaning of a Hebrew word. Returns nil if unknown."
  [word]
  (get entries word))

(defn known?
  "Is this word in the dictionary?"
  [word]
  (contains? entries word))

;; ── Full Torah vocabulary ─────────────────────────────────────

(def ^:private torah-vocab
  "All unique Hebrew word forms in the Torah. Lazy — first call extracts from OSHB."
  (delay
    (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
          all-words (transient #{})]
      (doseq [book books]
        (doseq [{:keys [text]} (oshb/book-words book)]
          (doseq [w (str/split (norm/normalize text) #"\s+")]
            (when (and (seq w) (every? norm/hebrew-letter? w))
              (conj! all-words w)))))
      (persistent! all-words))))

(defn torah-words
  "All unique Hebrew word forms in the Torah (~7,300 words).
   Includes every form that appears in the text — no curation.
   Lazy — first call triggers extraction from OSHB data."
  []
  @torah-vocab)
