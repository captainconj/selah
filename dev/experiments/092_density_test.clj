(ns experiments.092-density-test
  "Theological density test — does the breastplate separate vocabularies?

   091b found solo eigenwords: words only ONE head sees as a fixed point.
   The skeptics say: 'cherry-picked 12 words from 990 solos.'

   This experiment answers: is the DENSITY of mercy words in the mercy
   head's solos higher than in the other heads? Cross-tabulate all four
   theological categories × four heads.

   The test is honest. If the diagonal doesn't dominate, we say so.
   If uncomfortable words appear in the wrong list, we name them.

   Head mapping (from 091b):
     :right = yod=10 (hand)  — 'mercy' hypothesis
     :left  = he=5  (regard) — 'justice' hypothesis
     :aaron = vav=6 (nail)   — 'priestly' hypothesis
     :god   = he=5  (regard) — 'divine' hypothesis"
  (:require [selah.dict :as dict]
            [selah.gematria :as g]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.set :as set]))

;; ─── Phase 1: Theological Categories ─────────────────────────────
;;
;; Every word in dict.clj gets assigned to zero or more categories.
;; This is the ground truth. We are honest about ambiguity.
;;
;; Categories:
;;   :mercy    — compassion, life, redemption, forgiveness, healing, grace
;;   :justice  — law, judgment, measure, commandment, punishment, war
;;   :priestly — ritual, materials, worship infrastructure, sacrifice
;;   :divine   — God's nature, presence, revelation, holiness, covenant
;;   :neutral  — doesn't clearly belong, or is purely grammatical/structural

(def categories
  {;; ── Function words → neutral ──
   "את"    #{:neutral}       ; direct object marker
   "אשר"   #{:neutral}       ; that/which/who
   "אל"    #{:divine}        ; to/God — dual meaning, but divine sense primary
   "על"    #{:neutral}       ; upon/over
   "כי"    #{:neutral}       ; for/because/when
   "כל"    #{:neutral}       ; all/every
   "לא"    #{:neutral}       ; not
   "ואת"   #{:neutral}       ; and-(obj)
   "הוא"   #{:neutral}       ; he/it
   "לו"    #{:neutral}       ; to him
   "לך"    #{:neutral}       ; to you / go
   "בן"    #{:neutral}       ; son
   "עד"    #{:justice}       ; until/witness — witness is a justice term
   "או"    #{:neutral}       ; or
   "מן"    #{:neutral}       ; from
   "אם"    #{:neutral}       ; if/mother
   "כן"    #{:neutral}       ; thus/so
   "גם"    #{:neutral}       ; also
   "בו"    #{:neutral}       ; in him
   "זה"    #{:neutral}       ; this
   "נא"    #{:neutral}       ; please
   "עם"    #{:neutral}       ; people/with
   "לי"    #{:neutral}       ; to me
   "אני"   #{:neutral}       ; I
   "שם"    #{:neutral}       ; there/name
   "היא"   #{:neutral}       ; she/it

   ;; ── Verbs ──
   "ויאמר" #{:neutral}       ; and he said
   "אמר"   #{:neutral}       ; say
   "דבר"   #{:neutral}       ; speak/word/thing
   "עשה"   #{:neutral}       ; make/do
   "נתן"   #{:neutral}       ; give
   "ראה"   #{:neutral}       ; see
   "שמע"   #{:neutral}       ; hear
   "ידע"   #{:neutral}       ; know
   "הלך"   #{:neutral}       ; walk/go
   "בוא"   #{:neutral}       ; come/enter
   "ישב"   #{:neutral}       ; sit/dwell
   "עמד"   #{:neutral}       ; stand
   "נשא"   #{:neutral}       ; lift/carry
   "שלח"   #{:neutral}       ; send
   "קרא"   #{:neutral}       ; call/read
   "ברא"   #{:neutral}       ; create — could be divine, but it's a general verb
   "שמר"   #{:priestly :justice} ; guard/keep — priestly duty, also keep the law
   "כפר"   #{:mercy :priestly}   ; atone/cover — mercy act, priestly ritual
   "גאל"   #{:mercy}         ; redeem
   "כרת"   #{:justice}        ; cut (covenant) — covenantal/legal act
   "סלח"   #{:mercy}         ; forgive
   "פדה"   #{:mercy}         ; ransom
   "ירש"   #{:neutral}       ; inherit
   "ברך"   #{:divine :mercy}  ; bless — divine act, merciful in nature
   "צוה"   #{:justice}        ; command
   "עלה"   #{:priestly}       ; go up / offering
   "שוב"   #{:mercy}         ; return (repentance)
   "מות"   #{:neutral}       ; die/death — universal, not categorizable
   "חטא"   #{:mercy :justice} ; sin — addressed by mercy, defined by justice
   "לקח"   #{:neutral}       ; take
   "שכב"   #{:neutral}       ; lie down
   "יצא"   #{:neutral}       ; go out
   "נפל"   #{:neutral}       ; fall
   "קום"   #{:neutral}       ; arise
   "שים"   #{:neutral}       ; set/place
   "אכל"   #{:neutral}       ; eat
   "שתה"   #{:neutral}       ; drink
   "כתב"   #{:justice}        ; write — writing law/decrees
   "זבח"   #{:priestly}       ; sacrifice
   "נדר"   #{:priestly}       ; vow
   "טהר"   #{:priestly}       ; be pure
   "קדש"   #{:divine :priestly} ; be holy — divine nature, priestly duty
   "ענה"   #{:neutral}       ; answer/afflict
   "ירא"   #{:divine}        ; fear (of God)
   "אהב"   #{:mercy}         ; love (verb)
   "שפך"   #{:justice}        ; pour out (blood/judgment)
   "נקם"   #{:justice}        ; avenge
   "רחם"   #{:mercy}         ; have mercy
   "חנן"   #{:mercy}         ; be gracious

   ;; ── People ──
   "יהוה"  #{:divine}        ; YHWH
   "אלהים" #{:divine}        ; God (Elohim)
   "אדני"  #{:divine}        ; Lord
   "משה"   #{:neutral}       ; Moses — person, not a category
   "אהרן"  #{:priestly}       ; Aaron — THE priest
   "ישראל" #{:neutral}       ; Israel
   "אברהם" #{:neutral}       ; Abraham
   "יצחק"  #{:neutral}       ; Isaac
   "יעקב"  #{:neutral}       ; Jacob
   "יוסף"  #{:neutral}       ; Joseph
   "פרעה"  #{:neutral}       ; Pharaoh
   "אדם"   #{:neutral}       ; man/Adam
   "חוה"   #{:neutral}       ; Eve
   "נח"    #{:mercy}         ; Noah — rest/grace reversed
   "אשה"   #{:neutral}       ; woman/wife
   "איש"   #{:neutral}       ; man
   "אב"    #{:neutral}       ; father
   "אח"    #{:neutral}       ; brother
   "כהן"   #{:priestly}       ; priest
   "מלך"   #{:justice}        ; king — ruler, authority
   "עבד"   #{:neutral}       ; servant
   "נביא"  #{:divine}        ; prophet — divine communication
   "בני"   #{:neutral}       ; sons of
   "בת"    #{:neutral}       ; daughter

   ;; ── Nature / physical ──
   "ארץ"   #{:neutral}       ; land/earth
   "שמים"  #{:divine}        ; heaven — God's domain
   "מים"   #{:neutral}       ; water
   "אש"    #{:divine :justice} ; fire — divine presence + judgment
   "אור"   #{:divine}        ; light
   "חשך"   #{:neutral}       ; darkness
   "יום"   #{:neutral}       ; day
   "לילה"  #{:neutral}       ; night
   "ענן"   #{:divine}        ; cloud — divine presence
   "עץ"    #{:neutral}       ; tree
   "גן"    #{:neutral}       ; garden
   "פרי"   #{:neutral}       ; fruit
   "זרע"   #{:neutral}       ; seed
   "אבן"   #{:neutral}       ; stone
   "צור"   #{:neutral}       ; rock
   "הר"    #{:neutral}       ; mountain
   "ים"    #{:neutral}       ; sea
   "נהר"   #{:neutral}       ; river
   "באר"   #{:neutral}       ; well
   "דם"    #{:priestly :justice} ; blood — sacrificial + judicial
   "נפש"   #{:neutral}       ; soul
   "רוח"   #{:divine}        ; spirit/wind
   "כבש"   #{:priestly :mercy}  ; lamb — sacrifice + mercy
   "פר"    #{:priestly}       ; bull — sacrifice
   "שור"   #{:neutral}       ; ox
   "נחש"   #{:neutral}       ; serpent
   "עוף"   #{:neutral}       ; bird
   "דג"    #{:neutral}       ; fish
   "בהמה"  #{:neutral}       ; animal/beast
   "בקר"   #{:neutral}       ; cattle
   "צאן"   #{:neutral}       ; flock

   ;; ── Abstract / sacred ──
   "תורה"  #{:divine :justice}  ; Torah/teaching — divine revelation + law
   "אהבה"  #{:mercy}         ; love
   "בינה"  #{:divine}        ; understanding
   "חכמה"  #{:divine}        ; wisdom
   "אמת"   #{:divine :justice}  ; truth — divine nature + legal standard
   "חסד"   #{:mercy}         ; lovingkindness
   "צדק"   #{:justice}        ; righteousness
   "צדקה"  #{:justice}        ; righteousness
   "שלום"  #{:mercy :divine}    ; peace — divine attribute + mercy
   "ברית"  #{:divine :justice}  ; covenant — divine + legal
   "דעת"   #{:divine}        ; knowledge
   "משפט"  #{:justice}        ; judgment
   "עולם"  #{:divine}        ; eternal/world
   "חיים"  #{:mercy}         ; life
   "מועד"  #{:priestly}       ; appointed time — ritual calendar
   "שבת"   #{:priestly :divine} ; sabbath
   "קרבן"  #{:priestly}       ; offering
   "חרב"   #{:justice}        ; sword
   "קשת"   #{:justice}        ; bow/rainbow — weapon + covenant sign
   "דרך"   #{:neutral}       ; way/path
   "מטה"   #{:neutral}       ; staff/tribe

   ;; ── Tabernacle / temple ──
   "משכן"  #{:priestly}       ; tabernacle
   "מזבח"  #{:priestly}       ; altar
   "ארון"  #{:divine :priestly} ; ark — divine presence housed in priestly care
   "כרוב"  #{:divine :priestly} ; cherub
   "אהל"   #{:priestly}       ; tent
   "פרכת"  #{:priestly}       ; veil/curtain
   "כפרת"  #{:mercy :priestly}  ; mercy seat — THE mercy object
   "שמן"   #{:priestly}       ; oil
   "יין"   #{:priestly}       ; wine — libation
   "לחם"   #{:priestly}       ; bread — showbread
   "זהב"   #{:priestly}       ; gold — tabernacle material
   "כסף"   #{:priestly}       ; silver — tabernacle material
   "נחשת"  #{:priestly}       ; bronze — tabernacle material

   ;; ── Adjectives / qualities ──
   "טוב"   #{:neutral}       ; good
   "רע"    #{:neutral}       ; evil/bad
   "גדול"  #{:neutral}       ; great
   "קטן"   #{:neutral}       ; small
   "חכם"   #{:divine}        ; wise — wisdom is divine
   "קדוש"  #{:divine}        ; holy
   "אחד"   #{:divine}        ; one — divine unity
   "שני"   #{:neutral}       ; two/second
   "שלש"   #{:neutral}       ; three
   "ארבע"  #{:neutral}       ; four
   "חמש"   #{:neutral}       ; five
   "שש"    #{:neutral}       ; six
   "שבע"   #{:divine :priestly} ; seven/swear — completeness
   "עשר"   #{:justice}        ; ten — commandments
   "מאה"   #{:neutral}       ; hundred
   "אלף"   #{:neutral}       ; thousand
   "רב"    #{:neutral}       ; many/great
   "חי"    #{:mercy}         ; living
   "חדש"   #{:neutral}       ; new
   "ישר"   #{:justice}        ; upright — moral rectitude
   "תמים"  #{:justice :priestly} ; complete/blameless — unblemished offering, moral
   "כבד"   #{:divine}        ; heavy/glory — divine glory
   "חזק"   #{:neutral}       ; strong
   "רחוק"  #{:neutral}       ; far
   "קרוב"  #{:neutral}       ; near

   ;; ── Body ──
   "יד"    #{:neutral}       ; hand
   "עין"   #{:neutral}       ; eye
   "פנים"  #{:divine}        ; face — face of God
   "פה"    #{:neutral}       ; mouth
   "לב"    #{:neutral}       ; heart
   "ראש"   #{:neutral}       ; head
   "רגל"   #{:neutral}       ; foot
   "אזן"   #{:neutral}       ; ear

   ;; ── Places / prepositions ──
   "בית"   #{:neutral}       ; house
   "עיר"   #{:neutral}       ; city
   "שער"   #{:neutral}       ; gate
   "מדבר"  #{:neutral}       ; wilderness
   "חלק"   #{:neutral}       ; portion
   "גבול"  #{:neutral}       ; border/territory
   "אמן"   #{:divine}        ; amen/faithful
   "סביב"  #{:neutral}       ; around

   ;; ── Theological ──
   "כבוד"  #{:divine}        ; glory
   "חן"    #{:mercy}         ; grace
   "רחמים" #{:mercy}         ; mercies
   "פני"   #{:divine}        ; face of (God)
   "שכינה" #{:divine}        ; dwelling/presence

   ;; ── More verbs ──
   "ברח"   #{:neutral}       ; flee
   "רדה"   #{:justice}        ; rule
   "נכה"   #{:justice}        ; strike

   ;; ── Common with-prefix forms ──
   "לאמר"  #{:neutral}       ; saying
   "ליהוה" #{:divine}        ; to YHWH
   "אלהיך" #{:divine}        ; your God
   "הכהן"  #{:priestly}       ; the priest
   "הארץ"  #{:neutral}       ; the land
   "העם"   #{:neutral}       ; the people
   "מצרים" #{:neutral}       ; Egypt
   "אתה"   #{:neutral}       ; you
   "אתם"   #{:neutral}       ; you (pl)
   "אנכי"  #{:neutral}       ; I (emphatic)
   "היום"  #{:neutral}       ; today/the day
   "והיה"  #{:neutral}       ; and it shall be
   "ויהי"  #{:neutral}       ; and it was
   "כאשר"  #{:neutral}       ; as/when

   ;; ── Names ──
   "שרה"   #{:neutral}       ; Sarah
   "רחל"   #{:neutral}       ; Rachel
   "לאה"   #{:neutral}       ; Leah
   "קהת"   #{:priestly}       ; Kohath — Levitical clan
   "נער"   #{:neutral}       ; youth/lad
   "ארך"   #{:neutral}       ; long
   "עשב"   #{:neutral}       ; herb/grass

   ;; ── More words ──
   "עז"    #{:neutral}       ; strong/goat
   "גר"    #{:neutral}       ; sojourner
   "בר"    #{:neutral}       ; grain/pure/son

   ;; ── Oracle vocabulary ──
   "שכרה"  #{:neutral}       ; drunk
   "כשרה"  #{:neutral}       ; like Sarah
   })

;; Sanity check: every dict word is categorized
(defn verify-categories []
  (let [dict-words (dict/words)
        cat-words  (set (keys categories))
        missing    (set/difference dict-words cat-words)
        extra      (set/difference cat-words dict-words)]
    (when (seq missing)
      (println "WARNING: dict words missing from categories:" (sort missing)))
    (when (seq extra)
      (println "WARNING: extra words in categories not in dict:" (sort extra)))
    (println (str "  " (count cat-words) " categorized, "
                  (count dict-words) " in dict, "
                  (count missing) " missing, "
                  (count extra) " extra"))
    {:missing missing :extra extra}))

;; Category → set of Hebrew words
(defn words-in-category [cat]
  (set (keep (fn [[word cats]] (when (contains? cats cat) word)) categories)))

;; Word → set of categories
(defn word-categories [word]
  (get categories word #{:uncategorized}))


;; ─── Phase 2: Parse Solo Eigenwords from 091b ─────────────────────
;;
;; We read agreement.edn which has structured solo data.

(defn load-agreement []
  (edn/read-string (slurp "data/experiments/091b/agreement.edn")))

(defn solo-words-by-head
  "Returns {:right #{words...} :left #{...} :aaron #{...} :god #{...}}.
   agreement.edn is a flat vector of {:word :gv :meaning :agreement :heads}."
  [agreement]
  (let [solos (filter #(= 1 (:agreement %)) agreement)]
    (reduce (fn [acc {:keys [word heads]}]
              (let [head (first heads)]
                (update acc head (fnil conj #{}) word)))
            {}
            solos)))


;; ─── Phase 3: Density Measurement ────────────────────────────────

(defn dict-solos
  "Solo eigenwords that have dictionary entries, grouped by head."
  [solos-by-head]
  (into {} (map (fn [[head words]]
                  [head (set (filter dict/known? words))])
                solos-by-head)))

(defn category-counts
  "For a set of dict-known words, count how many belong to each category."
  [words]
  (let [cats [:mercy :justice :priestly :divine :neutral]]
    (into {}
          (map (fn [cat]
                 (let [cat-words (words-in-category cat)
                       matches (set/intersection words cat-words)]
                   [cat {:count (count matches)
                         :words (sort matches)}]))
               cats))))

(defn density-table
  "Build the 4×4 cross-tabulation table.
   Rows = categories, Columns = heads.
   Each cell = count of words in that category that are solos for that head."
  [dict-by-head]
  (let [cats [:mercy :justice :priestly :divine]
        heads [:right :left :aaron :god]]
    {:cats cats
     :heads heads
     :cells
     (into {}
           (for [cat cats
                 head heads]
             (let [cat-words (words-in-category cat)
                   head-words (get dict-by-head head #{})
                   matches (set/intersection cat-words head-words)]
               [[cat head] {:count (count matches)
                            :words (sort matches)}])))
     :row-totals
     (into {} (for [cat cats]
                [cat (reduce + (for [head heads]
                                 (:count (get (into {} (for [h heads]
                                                         [h (let [cw (words-in-category cat)
                                                                  hw (get dict-by-head h #{})]
                                                              {:count (count (set/intersection cw hw))})]))
                                                       head))))]))
     :col-totals
     (into {} (for [head heads]
                [head (count (get dict-by-head head #{}))]))}))


;; ─── Phase 4: Statistical Tests ──────────────────────────────────

(defn chi-squared-4x4
  "Chi-squared test on the 4×4 contingency table.
   Returns {:chi2 :df :cells-detail}."
  [table]
  (let [{:keys [cats heads cells]} table
        ;; Grand total
        grand-total (reduce + (map (fn [[_ v]] (:count v)) cells))
        ;; Row totals
        row-totals (into {} (for [cat cats]
                              [cat (reduce + (for [head heads]
                                               (:count (get cells [cat head]))))]))
        ;; Column totals
        col-totals (into {} (for [head heads]
                              [head (reduce + (for [cat cats]
                                                (:count (get cells [cat head]))))]))
        ;; Chi-squared statistic
        detail
        (for [cat cats
              head heads]
          (let [observed (:count (get cells [cat head]))
                expected (if (pos? grand-total)
                           (/ (* (double (get row-totals cat 0))
                                 (double (get col-totals head 0)))
                              (double grand-total))
                           0.0)
                contrib (if (pos? expected)
                          (/ (Math/pow (- observed expected) 2) expected)
                          0.0)]
            {:cat cat :head head
             :observed observed :expected expected
             :contribution contrib}))
        chi2 (reduce + (map :contribution detail))
        df (* (dec (count cats)) (dec (count heads)))]
    {:chi2 chi2
     :df df
     :grand-total grand-total
     :row-totals row-totals
     :col-totals col-totals
     :detail (vec detail)}))

(defn enrichment-ratio
  "For a given category, what's the ratio of observed/expected
   for the 'correct' head vs the average of the other three?"
  [chi-detail cat correct-head]
  (let [cells (filter #(= (:cat %) cat) chi-detail)
        correct (first (filter #(= (:head %) correct-head) cells))
        others  (filter #(not= (:head %) correct-head) cells)
        correct-ratio (if (pos? (:expected correct))
                        (/ (:observed correct) (:expected correct))
                        0.0)
        other-ratios (map (fn [c] (if (pos? (:expected c))
                                    (/ (:observed c) (:expected c))
                                    0.0))
                          others)
        avg-other (if (seq other-ratios)
                    (/ (reduce + other-ratios) (count other-ratios))
                    0.0)]
    {:category cat
     :correct-head correct-head
     :observed (:observed correct)
     :expected (:expected correct)
     :ratio correct-ratio
     :avg-other-ratio avg-other
     :enrichment (if (pos? avg-other) (/ correct-ratio avg-other) ##Inf)}))

(defn fisher-exact-2x2
  "Fisher's exact test (one-tailed) for a 2×2 contingency table.
   Tests whether cell [0,0] is enriched.
   [a b]
   [c d]
   Returns p-value (upper tail: probability of seeing a or more extreme)."
  [a b c d]
  (let [n (+ a b c d)
        ;; log-factorial via Stirling or direct computation
        log-fact (fn [k] (reduce + 0.0 (map #(Math/log (double %)) (range 1 (inc k)))))
        ;; Hypergeometric probability for specific a given margins
        hyper-p (fn [a* b* c* d*]
                  (Math/exp (- (+ (log-fact (+ a* b*))
                                  (log-fact (+ c* d*))
                                  (log-fact (+ a* c*))
                                  (log-fact (+ b* d*)))
                               (+ (log-fact a*)
                                  (log-fact b*)
                                  (log-fact c*)
                                  (log-fact d*)
                                  (log-fact n)))))
        ;; Sum probabilities for a* >= a (enrichment direction)
        max-a (min (+ a b) (+ a c))
        p-value (reduce + (for [a* (range a (inc max-a))]
                            (let [b* (- (+ a b) a*)
                                  c* (- (+ a c) a*)
                                  d* (- n a* b* c*)]
                              (if (and (>= b* 0) (>= c* 0) (>= d* 0))
                                (hyper-p a* b* c* d*)
                                0.0))))]
    p-value))

(defn per-category-fisher
  "For each category, run Fisher's exact test:
   Is the 'correct' head significantly enriched vs the other three combined?"
  [table correct-mapping]
  (let [{:keys [cats heads cells]} table]
    (for [[cat correct-head] correct-mapping]
      (let [a  (:count (get cells [cat correct-head]))
            ;; b = other heads, same category
            b  (reduce + (for [h heads :when (not= h correct-head)]
                           (:count (get cells [cat h]))))
            ;; c = correct head, other categories
            c  (reduce + (for [c* cats :when (not= c* cat)]
                           (:count (get cells [c* correct-head]))))
            ;; d = other heads, other categories
            d  (reduce + (for [c* cats :when (not= c* cat)
                               h heads :when (not= h correct-head)]
                           (:count (get cells [c* h]))))
            p  (fisher-exact-2x2 a b c d)]
        {:category cat
         :head correct-head
         :a a :b b :c c :d d
         :p-value p}))))


;; ─── Phase 5: Root Collapse ──────────────────────────────────────
;;
;; Many solo eigenwords are inflected forms of the same root.
;; e.g., חטא, חטאה, חטאו, בחטאה, ויחטא — all from root חטא (sin).
;; Count distinct CONCEPTS, not surface forms.

(defn root-match?
  "Does the solo word contain the dict word as a substring root?
   We check both containment and that the dict word is >= 2 letters."
  [solo-word dict-word]
  (and (>= (count dict-word) 2)
       (str/includes? solo-word dict-word)))

(defn find-root
  "For a solo eigenword, find the best matching dict entry.
   Returns [dict-word meaning] or nil.
   Prefers exact match, then longest substring match."
  [word dict-words]
  ;; Exact match first
  (if-let [m (dict/translate word)]
    [word m]
    ;; Substring match: longest dict word that's a substring
    (let [matches (->> dict-words
                       (filter #(root-match? word %))
                       (sort-by count >))]
      (when (seq matches)
        [(first matches) (dict/translate (first matches))]))))

(defn root-analysis
  "For each head's solos, collapse to distinct roots.
   Returns per-head: {:total-solos :dict-exact :dict-root :distinct-roots :root-detail}."
  [solos-by-head]
  (let [dw (vec (sort (dict/words)))]
    (into {}
          (map (fn [[head words]]
                 (let [sorted-words (sort words)
                       root-map (into {} (keep (fn [w]
                                                 (when-let [r (find-root w dw)]
                                                   [w r]))
                                               sorted-words))
                       ;; Group by root
                       by-root (group-by (fn [[_ [root _]]] root)
                                         root-map)
                       exact-matches (count (filter dict/known? sorted-words))]
                   [head {:total-solos (count words)
                          :dict-exact exact-matches
                          :dict-root (count root-map)
                          :distinct-roots (count by-root)
                          :root-detail
                          (->> by-root
                               (map (fn [[root entries]]
                                      {:root root
                                       :meaning (dict/translate root)
                                       :forms (sort (map first entries))
                                       :count (count entries)}))
                               (sort-by :count >)
                               vec)}]))
               solos-by-head))))


;; ─── Phase 6: Uncomfortable Words ───────────────────────────────
;;
;; For each head, which dict-translatable words DON'T fit the
;; theological label? Name them. Don't hide them.

(def head-hypothesis
  {:right  :mercy
   :left   :justice
   :aaron  :priestly
   :god    :divine})

(defn uncomfortable-words
  "Words in each head's solos that have dict translations
   but belong to a DIFFERENT theological category (not neutral)."
  [dict-by-head]
  (into {}
        (map (fn [[head words]]
               (let [expected-cat (get head-hypothesis head)
                     uncomfortable
                     (->> words
                          (keep (fn [w]
                                  (let [cats (word-categories w)]
                                    (when (and (not (contains? cats expected-cat))
                                              (not (= cats #{:neutral})))
                                      {:word w
                                       :meaning (dict/translate w)
                                       :categories cats
                                       :expected expected-cat}))))
                          (sort-by :word)
                          vec)]
                 [head uncomfortable]))
             dict-by-head)))


;; ─── Phase 7: Run Everything ─────────────────────────────────────

(defn format-table
  "Pretty-print the 4×4 cross-tabulation."
  [table chi-result]
  (let [{:keys [cats heads cells]} table
        {:keys [col-totals row-totals detail]} chi-result
        head-labels {:right "mercy/yod" :left "justice/he"
                     :aaron "aaron/vav" :god "god/he"}]
    (println "\n  ┌─────────────┬───────────┬───────────┬───────────┬───────────┬───────┐")
    (println (format "  │ %-11s │ %-9s │ %-9s │ %-9s │ %-9s │ TOTAL │"
                     "" "mercy/yod" "just./he" "aaron/vav" "god/he"))
    (println "  ├─────────────┼───────────┼───────────┼───────────┼───────────┼───────┤")
    (doseq [cat cats]
      (let [counts (mapv (fn [h] (:count (get cells [cat h]))) heads)
            total (get row-totals cat 0)
            ;; Find expected values
            exps (mapv (fn [h]
                         (:expected (first (filter #(and (= (:cat %) cat)
                                                         (= (:head %) h))
                                                   detail))))
                       heads)]
        (println (format "  │ %-11s │ %3d (%4.1f) │ %3d (%4.1f) │ %3d (%4.1f) │ %3d (%4.1f) │ %5d │"
                         (name cat)
                         (nth counts 0) (nth exps 0)
                         (nth counts 1) (nth exps 1)
                         (nth counts 2) (nth exps 2)
                         (nth counts 3) (nth exps 3)
                         total))))
    (println "  ├─────────────┼───────────┼───────────┼───────────┼───────────┼───────┤")
    (let [totals (mapv #(get col-totals % 0) heads)]
      (println (format "  │ TOTAL       │ %3d       │ %3d       │ %3d       │ %3d       │ %5d │"
                       (nth totals 0) (nth totals 1) (nth totals 2) (nth totals 3)
                       (:grand-total chi-result))))
    (println "  └─────────────┴───────────┴───────────┴───────────┴───────────┴───────┘")
    (println (format "  (observed (expected))  χ²=%.3f  df=%d"
                     (:chi2 chi-result) (:df chi-result)))))

(defn run []
  (println "\n═══════════════════════════════════════════════════════════")
  (println "  EXPERIMENT 092 — THEOLOGICAL DENSITY TEST")
  (println "  Is the separation real, or cherry-picked?")
  (println "  Let the numbers speak.")
  (println "═══════════════════════════════════════════════════════════")

  ;; Phase 0: Verify categories
  (println "\n── Phase 0: Category Verification ──")
  (let [{:keys [missing extra]} (verify-categories)]
    (when (seq missing)
      (println "  STOP: fix categorization before continuing.")))

  ;; Phase 1: Category statistics
  (println "\n── Phase 1: Theological Categories ──")
  (let [cat-counts (into {} (for [c [:mercy :justice :priestly :divine :neutral]]
                              [c (count (words-in-category c))]))]
    (doseq [[c n] (sort-by val > cat-counts)]
      (println (format "  %-10s %3d words" (name c) n)))
    (println (format "  %-10s %3d total (some words in multiple categories)"
                     "sum" (reduce + (vals cat-counts)))))

  ;; Phase 2: Load solo eigenwords
  (println "\n── Phase 2: Solo Eigenwords from 091b ──")
  (let [agreement (load-agreement)
        solos-by-head (solo-words-by-head agreement)
        dict-by-head (dict-solos solos-by-head)]

    (println "  Solos per head (all / dict-known):")
    (doseq [h [:right :left :aaron :god]]
      (let [label (case h :right "yod=10 (hand/mercy)"
                          :left  "he=5  (justice)"
                          :aaron "vav=6 (nail/priestly)"
                          :god   "he=5  (God/divine)")]
        (println (format "    %-24s  %4d solos  %3d in dict"
                         label
                         (count (get solos-by-head h #{}))
                         (count (get dict-by-head h #{}))))))

    ;; Phase 2b: List every dict word found per head
    (println "\n  Dict words found in each head's solos:")
    (doseq [h [:right :left :aaron :god]]
      (let [words (sort (get dict-by-head h #{}))]
        (println (str "\n    " (name h) " (" (count words) "):"))
        (doseq [w words]
          (let [cats (word-categories w)]
            (println (format "      %-8s  %-30s  %s"
                             w (or (dict/translate w) "?")
                             (str/join "," (map name cats))))))))

    ;; Phase 3: Cross-tabulation
    (println "\n\n── Phase 3: Cross-Tabulation (4×4) ──")
    (let [table (density-table dict-by-head)
          chi (chi-squared-4x4 table)]
      (format-table table chi)

      ;; Phase 3b: Enrichment ratios
      (println "\n── Phase 3b: Enrichment Ratios ──")
      (println "  (observed/expected for the 'correct' head vs average of other three)")
      (let [mapping {:mercy :right :justice :left :priestly :aaron :divine :god}]
        (doseq [[cat head] mapping]
          (let [er (enrichment-ratio (:detail chi) cat head)]
            (println (format "  %-10s in %-10s:  O/E=%.2f  avg-other=%.2f  enrichment=%.2fx"
                             (name cat) (name head)
                             (:ratio er) (:avg-other-ratio er) (:enrichment er))))))

      ;; Phase 4: Fisher's exact test
      (println "\n── Phase 4: Fisher's Exact Test ──")
      (println "  (Is the 'correct' head significantly enriched for its category?)")
      (let [mapping [[:mercy :right] [:justice :left] [:priestly :aaron] [:divine :god]]
            results (per-category-fisher table mapping)]
        (doseq [{:keys [category head a b c d p-value]} results]
          (println (format "  %-10s × %-10s:  a=%2d b=%2d c=%2d d=%2d  p=%.4f  %s"
                           (name category) (name head)
                           a b c d p-value
                           (cond (< p-value 0.01)  "***"
                                 (< p-value 0.05)  "**"
                                 (< p-value 0.10)  "*"
                                 :else             "n.s."))))

        ;; Chi-squared significance
        (println (format "\n  χ² = %.3f, df = %d" (:chi2 chi) (:df chi)))
        (println "  Critical values: df=9: p<0.05 → χ²>16.92, p<0.01 → χ²>21.67")
        (let [chi2 (:chi2 chi)]
          (println (format "  Result: %s"
                           (cond (> chi2 21.67) "SIGNIFICANT at p < 0.01"
                                 (> chi2 16.92) "SIGNIFICANT at p < 0.05"
                                 :else          "NOT SIGNIFICANT")))))

      ;; Phase 5: Root analysis
      (println "\n\n── Phase 5: Root Analysis ──")
      (println "  (How many DISTINCT concepts vs inflected forms?)")
      (let [roots (root-analysis solos-by-head)]
        (doseq [h [:right :left :aaron :god]]
          (let [{:keys [total-solos dict-exact dict-root distinct-roots root-detail]}
                (get roots h)]
            (println (format "\n  %s: %d solos → %d exact dict matches, %d root matches → %d distinct roots"
                             (name h) total-solos dict-exact dict-root distinct-roots))
            ;; Show top roots by inflection count
            (println "    Top roots (by inflected form count):")
            (doseq [{:keys [root meaning count forms]} (take 10 root-detail)]
              (println (format "      %-8s (%s): %d forms — %s"
                               root (or meaning "?") count
                               (str/join ", " (take 5 forms))))))))

      ;; Phase 6: Uncomfortable words
      (println "\n\n── Phase 6: Uncomfortable Words ──")
      (println "  (Dict words in the 'wrong' head — not neutral, not matching hypothesis)")
      (let [uw (uncomfortable-words dict-by-head)]
        (doseq [h [:right :left :aaron :god]]
          (let [label (case h :right "mercy" :left "justice" :aaron "priestly" :god "divine")
                words (get uw h [])]
            (println (format "\n  %s head (expected: %s) — %d uncomfortable words:"
                             (name h) label (count words)))
            (if (seq words)
              (doseq [{:keys [word meaning categories expected]} words]
                (println (format "    %-8s  %-25s  has: %-20s  expected: %s"
                                 word meaning
                                 (str/join "," (map name categories))
                                 (name expected))))
              (println "    (none)")))))

      ;; Phase 7: Density comparison
      (println "\n\n── Phase 7: Density Comparison ──")
      (println "  (What fraction of each head's dict words belong to each category?)")
      (doseq [h [:right :left :aaron :god]]
        (let [words (get dict-by-head h #{})
              total (count words)
              label (case h :right "mercy/yod" :left "justice/he"
                            :aaron "aaron/vav" :god "god/he")]
          (println (format "\n  %s (%d dict words):" label total))
          (doseq [cat [:mercy :justice :priestly :divine :neutral]]
            (let [cat-words (words-in-category cat)
                  n (count (set/intersection words cat-words))
                  pct (if (pos? total) (* 100.0 (/ n (double total))) 0.0)]
              (println (format "    %-10s  %3d  (%5.1f%%)" (name cat) n pct))))))

      ;; Verdict
      (println "\n\n═══════════════════════════════════════════════════════════")
      (println "  VERDICT")
      (println "═══════════════════════════════════════════════════════════")
      (let [chi2 (:chi2 chi)
            mapping [[:mercy :right] [:justice :left] [:priestly :aaron] [:divine :god]]
            fishers (per-category-fisher table mapping)
            sig-count (count (filter #(< (:p-value %) 0.05) fishers))]
        (println (format "  χ² = %.3f (df=9). %s."
                         chi2
                         (cond (> chi2 21.67) "Significant at p < 0.01"
                               (> chi2 16.92) "Significant at p < 0.05"
                               :else          "Not significant")))
        (println (format "  Fisher's exact: %d/4 categories significantly enriched (p<0.05)."
                         sig-count))
        (println)
        (if (> chi2 16.92)
          (println "  The diagonal dominates. The separation is real.")
          (println "  The diagonal does NOT dominate. The skeptics may be right.")))

      (println "\n═══════════════════════════════════════════════════════════")
      (println "  DONE. The numbers have spoken.")
      (println "═══════════════════════════════════════════════════════════")

      ;; Return data for REPL exploration
      {:table table :chi chi
       :dict-by-head dict-by-head
       :solos-by-head solos-by-head})))


(comment
  ;; Run the full analysis
  (run)

  ;; Explore individual pieces
  (verify-categories)

  ;; Check what mercy words exist
  (words-in-category :mercy)

  ;; Load and inspect solos
  (def agreement (load-agreement))
  (def solos (solo-words-by-head agreement))
  (def dsolos (dict-solos solos))

  ;; Look at the cross-tab
  (density-table dsolos)

  :end)
