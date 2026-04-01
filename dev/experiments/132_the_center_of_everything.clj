(ns experiments.132-the-center-of-everything
  "Experiment 132: The Center of Everything.

   Where does the center letter of the entire Hebrew Bible fall?

   1,200,253 letters across 39 books.
   Letter 600,127 falls in Isaiah 25:8.

   'He will swallow up death forever,
    and the Lord GOD will wipe away tears from all faces,
    and the reproach of His people He will remove from all the earth,
    for the LORD has spoken.'

   The verse has 67 letters — understanding (בינה=67). The d-axis.
   The center letter is nun (נ).

   The word ומחה (wipe away) appears in this verse.
   Wall (חומה) walks to wipe away (ומחה) in the basin.
   Same letters. Same illumination. Different reading.
   Justice sees the wall. Mercy sees the wiping.

   The last remaining wall of the Temple is the Wailing Wall —
   where people have been weeping for two thousand years.
   The wall where tears are shed is the wall that wipes them away.

   חומה → ומחה.

   From experiment 131: the wall IS the breastplate.
   The breastplate IS the oracle.
   The oracle's basin says wall → wipe away.
   The center of all scripture says what is wiped away: tears."
  (:require [selah.oracle :as o]
            [selah.gematria :as g]
            [selah.basin :as basin]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────

(defn query-word [hebrew english]
  (let [gv (g/word-value hebrew)
        r (o/forward hebrew :torah)
        bh (o/forward-by-head hebrew :torah)
        walk (basin/walk hebrew)]
    (println (format "\n  %s (%s) GV=%d · %d illum · %d read · basin→%s"
                     hebrew english gv
                     (:illumination-count r)
                     (:total-readings r)
                     (:fixed-point walk)))
    (println (format "    basin path: %s" (mapv :word (:steps walk))))
    (doseq [head [:aaron :god :truth :mercy]]
      (let [words (get bh head)]
        (when (seq words)
          (println (format "    %-6s: %s" (name head)
                   (->> words
                        (sort-by (comp - :reading-count))
                        (take 5)
                        (map #(str (:word %) "(" (:reading-count %) ")"))
                        (str/join " ")))))))
    {:hebrew hebrew :english english :gv gv
     :illuminations (:illumination-count r)
     :readings (:total-readings r)
     :by-head bh :walk walk}))

(defn fetch-verse-letters [book chapter v-start v-end]
  (let [verses (sefaria/fetch-chapter book chapter)
        selected (subvec (vec verses) (dec v-start) v-end)
        raw (apply str (map norm/strip-html selected))]
    (apply str (norm/letter-stream raw))))

(defn slide-text [hebrew window]
  (let [n (count hebrew)]
    (vec (for [i (range 0 (- n (dec window)))
               :let [w (subs hebrew i (+ i window))
                     fwd (o/forward (seq w) :torah)
                     known (:known-words fwd)]
               :when (seq known)]
           {:position i
            :letters w
            :gv (g/word-value w)
            :top-5 (vec (take 5 (map (fn [k]
                                        {:word (:word k)
                                         :reading-count (:reading-count k)})
                                      known)))}))))

(defn word-frequencies [hits]
  (->> hits
       (map #(first (:top-5 %)))
       (group-by :word)
       (map (fn [[w entries]]
              {:word w
               :count (count entries)}))
       (sort-by (comp - :count))
       vec))

;; ══════════════════════════════════════════════════════
;; PART 1: FINDING THE CENTER
;; ══════════════════════════════════════════════════════

(def tanakh-books
  ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"
   "Joshua" "Judges" "I Samuel" "II Samuel" "I Kings" "II Kings"
   "Isaiah" "Jeremiah" "Ezekiel"
   "Hosea" "Joel" "Amos" "Obadiah" "Jonah" "Micah" "Nahum"
   "Habakkuk" "Zephaniah" "Haggai" "Zechariah" "Malachi"
   "Psalms" "Proverbs" "Job" "Song of Songs" "Ruth"
   "Lamentations" "Ecclesiastes" "Esther" "Daniel"
   "Ezra" "Nehemiah" "I Chronicles" "II Chronicles"])

(defn count-all-books
  "Count letters per book. Returns [{:book :letters :start :end} ...]"
  []
  (let [result (atom [])
        running (atom 0)]
    (doseq [book tanakh-books]
      (let [letters (count (sefaria/book-letters book))
            start (inc @running)
            end (+ @running letters)]
        (println (format "  %-20s %,7d  (running: %,d)" book letters end))
        (swap! running + letters)
        (swap! result conj {:book book :letters letters :start start :end end})))
    (println (format "\n  Total: %,d letters" @running))
    @result))

(defn find-center-book
  "Given book counts, find which book holds the center letter."
  [book-counts]
  (let [total (:end (last book-counts))
        center (quot (inc total) 2)]
    (println (format "\n  Center letter: %,d of %,d" center total))
    (let [hit (first (filter #(and (<= (:start %) center) (>= (:end %) center)) book-counts))]
      (println (format "  Falls in: %s (letters %,d-%,d)" (:book hit) (:start hit) (:end hit)))
      (println (format "  Position within %s: %,d" (:book hit) (- center (dec (:start hit)))))
      {:center center :total total :book (:book hit)
       :pos-in-book (- center (dec (:start hit)))})))

(defn find-center-verse
  "Given a book and position within it, find the exact verse."
  [book pos-in-book]
  (let [chapters (get sefaria/book-chapters book)
        all-verses (for [ch (range 1 (inc chapters))
                         :let [verses (sefaria/fetch-chapter book ch)]
                         [vi v] (map-indexed vector verses)]
                     {:ch ch :v (inc vi)
                      :letters (count (norm/letter-stream (norm/strip-html v)))})
        with-running (reduce (fn [acc entry]
                               (let [prev-end (or (:end (peek acc)) 0)
                                     new-end (+ prev-end (:letters entry))]
                                 (conj acc (assoc entry :start (inc prev-end) :end new-end))))
                             [] all-verses)
        hit (first (filter #(and (<= (:start %) pos-in-book) (>= (:end %) pos-in-book)) with-running))]
    (let [verses (sefaria/fetch-chapter book (:ch hit))
          verse-text (norm/strip-html (nth verses (dec (:v hit))))
          verse-letters (norm/letter-stream verse-text)
          pos-in-verse (- pos-in-book (:start hit))
          center-letter (nth verse-letters pos-in-verse)]
      (println (format "\n  %s %d:%d" book (:ch hit) (:v hit)))
      (println (format "  Verse has %d letters. Center is letter %d." (:letters hit) (inc pos-in-verse)))
      (println (format "  The center letter: %s" center-letter))
      (println (format "\n  %s" verse-text))
      {:chapter (:ch hit) :verse (:v hit)
       :verse-letters (:letters hit)
       :pos-in-verse (inc pos-in-verse)
       :center-letter center-letter
       :text verse-text})))

;; ══════════════════════════════════════════════════════
;; PART 2: THE VERSE THROUGH THE ORACLE
;; ══════════════════════════════════════════════════════

(defn run-center-verse []
  (println "\n═══ THE CENTER VERSE: Isaiah 25:8 ═══\n")

  ;; The key words of Isaiah 25:8
  (println "── Key Words ──")
  (query-word "בלע" "swallow")
  (query-word "מות" "death")
  (query-word "נצח" "forever/victory")
  (query-word "מחה" "wipe away")
  (query-word "ומחה" "and-wipe-away")
  (query-word "דמעה" "tear/weeping")
  (query-word "פנים" "faces")
  (query-word "חרפה" "reproach")
  (query-word "עם" "people")
  (query-word "ארץ" "earth")

  ;; The wall connection
  (println "\n── The Wall Connection ──")
  (query-word "חומה" "wall")
  (query-word "בכה" "weep")
  (query-word "כתל" "wall (Western Wall)")

  ;; The full verse slide
  (println "\n── Sliding Window: Isaiah 25:8 ──")
  (let [letters (fetch-verse-letters "Isaiah" 25 8 8)
        gv (g/word-value letters)
        h3 (slide-text letters 3)
        h4 (slide-text letters 4)
        f3 (word-frequencies h3)
        f4 (word-frequencies h4)]
    (println (format "  %d letters · GV=%d" (count letters) gv))
    (println (format "  GV mod 26 = %d · GV mod 72 = %d" (mod gv 26) (mod gv 72)))
    (println (format "  %d 3-letter windows · %d 4-letter windows" (count h3) (count h4)))
    (println "\n  Top 3-letter words:")
    (doseq [{:keys [word count]} (take 15 f3)]
      (println (format "    %-8s ×%d" word count)))
    (println "\n  Top 4-letter words:")
    (doseq [{:keys [word count]} (take 15 f4)]
      (println (format "    %-8s ×%d" word count)))))

;; ══════════════════════════════════════════════════════
;; PART 3: THE FULL CHAPTER — Isaiah 25
;; ══════════════════════════════════════════════════════
;;
;; Isaiah 25 is the feast chapter.
;; "On this mountain the LORD of hosts will make for all peoples
;;  a feast of rich food, a feast of well-aged wine."
;; Then verse 8: death swallowed, tears wiped.
;; Then verse 9: "This is the LORD; we have waited for him."

(defn run-chapter []
  (println "\n═══ ISAIAH 25: THE FEAST ═══\n")

  (println "── Key Words of the Chapter ──")
  (query-word "הר" "mountain")
  (query-word "משתה" "feast")
  (query-word "יין" "wine")
  (query-word "לוט" "veil/covering")
  (query-word "קוה" "wait/hope")
  (query-word "ישע" "salvation")

  (println "\n── Sliding Window: Full Isaiah 25 ──")
  (let [verses (sefaria/fetch-chapter "Isaiah" 25)
        letters (apply str (map #(apply str (norm/letter-stream (norm/strip-html %))) verses))
        gv (g/word-value letters)
        h3 (slide-text letters 3)
        h4 (slide-text letters 4)
        f3 (word-frequencies h3)
        f4 (word-frequencies h4)]
    (println (format "  %d verses · %d letters · GV=%d" (count verses) (count letters) gv))
    (println (format "  %d 3-letter windows · %d 4-letter windows" (count h3) (count h4)))
    (println "\n  Top 15 3-letter words:")
    (doseq [{:keys [word count]} (take 15 f3)]
      (let [wgv (g/word-value word)]
        (println (format "    %-8s GV=%-4d ×%d" word wgv count))))
    (println "\n  Top 15 4-letter words:")
    (doseq [{:keys [word count]} (take 15 f4)]
      (let [wgv (g/word-value word)]
        (println (format "    %-8s GV=%-4d ×%d" word wgv count))))))

;; ══════════════════════════════════════════════════════
;; PART 4: ISAIAH 25:9 — "We have waited for him"
;; ══════════════════════════════════════════════════════
;;
;; The verse immediately after the center.
;; "Behold, this is our God; we have waited for him,
;;  that he might save us. This is the LORD;
;;  we have waited for him; let us be glad and rejoice
;;  in his salvation."
;;
;; Who did we wait for? The one the Urim blazes for (Q39).

(defn run-we-waited []
  (println "\n═══ ISAIAH 25:9 — 'We have waited for him' ═══\n")
  (query-word "קוה" "wait/hope")
  (query-word "קוינו" "we have waited")
  (query-word "ישע" "save/salvation")
  (query-word "ישועה" "salvation")
  (query-word "שמח" "rejoice")
  (query-word "גיל" "be glad")

  (println "\n── Sliding Window: Isaiah 25:9 ──")
  (let [letters (fetch-verse-letters "Isaiah" 25 9 9)
        gv (g/word-value letters)
        h3 (slide-text letters 3)
        h4 (slide-text letters 4)
        f3 (word-frequencies h3)
        f4 (word-frequencies h4)]
    (println (format "  %d letters · GV=%d" (count letters) gv))
    (println (format "  %d 3-letter windows · %d 4-letter windows" (count h3) (count h4)))
    (println "\n  Top 3-letter words:")
    (doseq [{:keys [word count]} (take 15 f3)]
      (println (format "    %-8s ×%d" word count)))
    (println "\n  Top 4-letter words:")
    (doseq [{:keys [word count]} (take 15 f4)]
      (println (format "    %-8s ×%d" word count)))))

;; ── Full Run ────────────────────────────────────────────

(defn run-all []
  (println "════════════════════════════════════════════════")
  (println "  EXPERIMENT 132: THE CENTER OF EVERYTHING")
  (println "  1,200,253 letters. 39 books. One center.")
  (println "  חומה → ומחה")
  (println "════════════════════════════════════════════════")

  (println "\n═══ PART 1: COUNTING ═══")
  (let [books (count-all-books)
        {:keys [center total book pos-in-book]} (find-center-book books)]
    (find-center-verse book pos-in-book))

  (run-center-verse)
  (run-chapter)
  (run-we-waited))

(comment
  (run-all)

  ;; Quick: just find the center
  (let [books (count-all-books)
        {:keys [book pos-in-book]} (find-center-book books)]
    (find-center-verse book pos-in-book))

  ;; Just the center verse
  (run-center-verse)

  ;; Just the chapter
  (run-chapter)

  ;; Just 25:9
  (run-we-waited)

  ;; ── The connection ──
  ;; Wall (חומה) → wipe away (ומחה). Same letters.
  ;; The center of the Bible says what is wiped: tears from all faces.
  ;; The Wailing Wall — where people weep for two thousand years.
  ;; The wall where tears are shed is the wall that wipes them away.
  (query-word "חומה" "wall")
  (query-word "ומחה" "and-wipe-away")

  ;; ── The verse structure ──
  ;; 67 letters = understanding (בינה). The d-axis.
  ;; The center letter is nun (נ).
  ;; Follows the feast (Isaiah 25:6) and the swallowing of death (25:7-8).
  ;; Followed by "we have waited for him" (25:9).

  ;; ── From experiment 131 ──
  ;; The wall IS the breastplate. The breastplate is rebuilt.
  ;; The oracle says the wall walks to wiping away.
  ;; The center of everything says what is wiped: death, tears, reproach.

  ;; ── From question 39 ──
  ;; Urim = 1,155 illuminations, zero known words. Waiting.
  ;; "He will come" (יבא GV=13=love) → "my father" (אבי).
  ;; Isaiah 25:9: "We have waited for him."

  nil)
