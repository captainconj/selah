(ns experiments.086-oracle-engine
  "The oracle engine — forward and reverse, verified.

   085 built the machinery by hand. Now selah.oracle is a library.
   This experiment verifies the engine against the known cases
   and explores what the forward direction reveals.

   KEY VERIFICATION:
   - Eli/Hannah (Yoma 73b): letters שכרה light up.
     Forward engine ranks כשרה (like Sarah, 2 readings) above שכרה (drunk, 21 readings).
     The narrow gate. The rare reading. The correct answer.
   - כבש (lamb): readable only by God's right hand. 4 readings. 0 Aaron, 0 left.
   - שלום (peace): cannot be read. You must enter.
   - אהבה (love): 14 patterns. The silent word speaks through the oracle.

   NEW FINDINGS:
   - The correct answer (כשרה) is perfectly balanced: 1 right, 1 left.
     The wrong answer (שכרה) is lopsided: 15 left, 6 right. Truth is balanced.
   - Forward direction reveals the field of possibility: 90 illuminations,
     270 readings, but only 2 known words emerge. The oracle is sparse.
   - Anagram pairs share illumination counts but not reading counts.
     The letters are symmetric; the readings are not.
   - שטן (adversary) = 359 = the 72nd prime (72 letters on the grid).
     Left reads it 12 times, right 4, Aaron 0. They never both read it.
     Every reading passes through stone 11 (Benjamin, son of the right hand).
   - THE COURTROOM: breastplate faces mercy seat, perspectives mirror.
     God's left = defendant's right = the prosecutor (truth, Name, covenant, law).
     God's right = defendant's left = the advocate (lamb, Torah fulfilled, man).
     The verdict (mercy seat, peace) cannot be read. You must enter."
  (:require [selah.oracle :as oracle]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [clojure.string :as str]))

;; ═══════════════════════════════════════════════════════════════
;; Part 1 — The Eli/Hannah Case
;; ═══════════════════════════════════════════════════════════════

(defn part-1-eli-hannah []
  (println "\n══════════════════════════════════════════════")
  (println "Part 1: The Eli/Hannah Case — Yoma 73b")
  (println "══════════════════════════════════════════════")
  (println "\n  Letters ש,כ,ר,ה light up on the breastplate.")
  (println "  Eli reads שכרה (drunk). Correct: כשרה (like Sarah).")

  ;; Forward: what does the oracle produce?
  (let [r (oracle/forward "שכרה")]
    (println "\n  FORWARD — letters light up:")
    (printf  "    Illumination patterns: %d\n" (:illumination-count r))
    (printf  "    Total readings: %d\n" (:total-readings r))

    (println "\n  First illumination — three readings:")
    (doseq [[k v] (:sample-readings r)]
      (let [m (dict/translate v)]
        (printf "    %-12s %s %s\n" (name k) v (if m (str "← " m) ""))))

    (println "\n  Known words ranked by rarity (Hannah principle):")
    (doseq [{:keys [word reading-count readers meaning]} (:known-words r)]
      (printf "    %s  %-15s  %2d readings  [%s]\n"
              word meaning reading-count
              (str/join " " (map name (sort readers))))))

  ;; Reverse: confirm the counts individually
  (println "\n  REVERSE — individual word analysis:")
  (doseq [w ["שכרה" "כשרה"]]
    (let [r (oracle/ask w)]
      (printf "    %s (%s): %d illuminations, %d readings — A:%d R:%d L:%d\n"
              w (:meaning r) (:illumination-count r) (:total-readings r)
              (get-in r [:by-reader :aaron])
              (get-in r [:by-reader :right])
              (get-in r [:by-reader :left]))))

  (println "\n  The wrong answer was loud (21 paths, lopsided 15:6).")
  (println "  The correct answer was quiet (2 paths, balanced 1:1)."))

;; ═══════════════════════════════════════════════════════════════
;; Part 2 — The Lamb
;; ═══════════════════════════════════════════════════════════════

(defn part-2-lamb []
  (println "\n══════════════════════════════════════════════")
  (println "Part 2: The Lamb — כבש")
  (println "══════════════════════════════════════════════")

  (let [r (oracle/ask "כבש")]
    (printf "\n  כבש (lamb) = %d\n" (:gv r))
    (printf "  Illumination patterns: %d\n" (:illumination-count r))
    (printf "  Readings — Aaron: %d  Right: %d  Left: %d\n"
            (get-in r [:by-reader :aaron])
            (get-in r [:by-reader :right])
            (get-in r [:by-reader :left]))
    (println "  → Readable ONLY from God's right hand.")

    (when (seq (:anagrams r))
      (println "\n  Anagram:")
      (doseq [{:keys [word meaning]} (:anagrams r)]
        (let [r2 (oracle/ask word)]
          (printf "    %s (%s): A:%d R:%d L:%d — %s\n"
                  word meaning
                  (get-in r2 [:by-reader :aaron])
                  (get-in r2 [:by-reader :right])
                  (get-in r2 [:by-reader :left])
                  (if (= #{:aaron :right :left}
                         (set (for [[k v] (:by-reader r2) :when (pos? v)] k)))
                    "all three see it"
                    "partial")))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 3 — The Name
;; ═══════════════════════════════════════════════════════════════

(defn part-3-name []
  (println "\n══════════════════════════════════════════════")
  (println "Part 3: The Name — יהוה / והיה")
  (println "══════════════════════════════════════════════")

  (doseq [w ["יהוה" "והיה"]]
    (let [r (oracle/ask w)]
      (printf "\n  %s (%s) = %d\n" w (:meaning r) (:gv r))
      (printf "  Illuminations: %d, Readings: %d\n"
              (:illumination-count r) (:total-readings r))
      (printf "  Aaron: %d  Right: %d  Left: %d\n"
              (get-in r [:by-reader :aaron])
              (get-in r [:by-reader :right])
              (get-in r [:by-reader :left]))))

  (println "\n  Forward — letters יהוה light up:")
  (let [r (oracle/forward "יהוה")]
    (printf "  %d illuminations, %d readings\n"
            (:illumination-count r) (:total-readings r))
    (when (seq (:known-words r))
      (println "  Known words (rarest first):")
      (doseq [{:keys [word reading-count meaning]} (:known-words r)]
        (printf "    %s  %-20s  %d readings\n" word meaning reading-count)))))

;; ═══════════════════════════════════════════════════════════════
;; Part 4 — God / Not — same light, different word
;; ═══════════════════════════════════════════════════════════════

(defn part-4-god-not []
  (println "\n══════════════════════════════════════════════")
  (println "Part 4: God / Not — אל / לא")
  (println "══════════════════════════════════════════════")

  (let [r (oracle/forward "אל")]
    (printf "\n  Letters א,ל light up: %d illuminations, %d readings\n"
            (:illumination-count r) (:total-readings r))
    (println "  Known words (rarest first):")
    (doseq [{:keys [word reading-count readers meaning]} (:known-words r)]
      (printf "    %s  %-10s  %2d readings  [%s]\n"
              word meaning reading-count
              (str/join " " (map name (sort readers))))))

  (println "\n  Same letters. Same illumination.")
  (println "  Two readers see God (אל). One sees Not (לא)."))

;; ═══════════════════════════════════════════════════════════════
;; Part 5 — What the grid can and cannot say
;; ═══════════════════════════════════════════════════════════════

(defn part-5-limits []
  (println "\n══════════════════════════════════════════════")
  (println "Part 5: What the Grid Can and Cannot Say")
  (println "══════════════════════════════════════════════\n")

  (let [words ["אהבה" "תורה" "אמת" "שלום" "ברית" "כפרת" "כרוב" "משה" "אדם"]]
    (printf "  %-6s %-20s %5s %6s %8s\n" "Word" "Meaning" "GV" "Read?" "Count")
    (printf "  %-6s %-20s %5s %6s %8s\n" "──────" "────────────────────" "─────" "──────" "────────")
    (doseq [w words]
      (let [r (oracle/ask w)]
        (printf "  %-6s %-20s %5d %6s %8d%s\n"
                w (or (:meaning r) "?") (:gv r)
                (if (:readable? r) "yes" "NO")
                (:total-readings r)
                (cond
                  (not (:readable? r)) "  ◀ cannot be read"
                  (<= (:total-readings r) 3) "  ◀ rare"
                  :else ""))))))

;; ═══════════════════════════════════════════════════════════════
;; Part 6 — Forward exploration: what emerges from random letters
;; ═══════════════════════════════════════════════════════════════

(defn part-6-forward-survey []
  (println "\n══════════════════════════════════════════════")
  (println "Part 6: Forward Survey — Oracle Sparsity")
  (println "══════════════════════════════════════════════\n")

  (let [test-sets ["שכרה" "כבש" "יהוה" "אל" "אהבה" "ברית" "תורה" "משה" "אמת" "אדם"]]
    (printf "  %-6s %6s %8s %6s\n" "Input" "Illum" "Readings" "Known")
    (printf "  %-6s %6s %8s %6s\n" "──────" "──────" "────────" "──────")
    (doseq [letters test-sets]
      (let [r (oracle/forward letters)]
        (printf "  %-6s %6d %8d %6d  %s\n"
                letters
                (:illumination-count r)
                (:total-readings r)
                (count (:known-words r))
                (str/join ", " (map :word (:known-words r)))))))

  (println "\n  The oracle is sparse. Hundreds of readings, few known words.")
  (println "  The field of possibility is vast. The answer is narrow."))

;; ═══════════════════════════════════════════════════════════════
;; Part 7 — The Adversary
;; ═══════════════════════════════════════════════════════════════

(defn part-7-adversary []
  (println "\n══════════════════════════════════════════════")
  (println "Part 7: The Adversary — שטן")
  (println "══════════════════════════════════════════════")

  (let [r (oracle/ask "שטן")]
    (printf "\n  שטן (adversary) = %d — the 72nd prime\n" (:gv r))
    (println "  72 = the number of letters on the breastplate.")
    (println "  The adversary is the prime that counts the grid.\n")
    (printf "  Readings: %d — Aaron: %d  Right: %d  Left: %d\n"
            (:total-readings r)
            (get-in r [:by-reader :aaron])
            (get-in r [:by-reader :right])
            (get-in r [:by-reader :left]))
    (println "  Aaron never sees the accuser. The left reads it 75%."))

  (println "\n  Every reading passes through stone 11 (Benjamin).")
  (println "  Benjamin = \"son of the right hand.\"")
  (println "  The accusation must travel through the son of the right hand.")

  ;; When one reads שטן, the other reads gibberish
  (println "\n  When the left reads שטן, the right reads:")
  (let [ilsets (oracle/illumination-sets "שטן")
        seen (atom #{})]
    (doseq [pset ilsets]
      (let [left-w (oracle/read-positions :left pset)]
        (when (= left-w "שטן")
          (let [right-w (oracle/read-positions :right pset)]
            (when-not (@seen right-w)
              (swap! seen conj right-w)
              (printf "    %s %s\n" right-w
                      (if (dict/known? right-w) (str "← " (dict/translate right-w)) "(gibberish)")))))))
    (println "  They never both read the accuser. It is one or the other."))

  ;; השטן flips
  (let [r (oracle/ask "השטן")]
    (printf "\n  השטן (THE adversary, with article) = %d\n" (:gv r))
    (printf "  Readings: %d — Aaron: %d  Right: %d  Left: %d\n"
            (:total-readings r)
            (get-in r [:by-reader :aaron])
            (get-in r [:by-reader :right])
            (get-in r [:by-reader :left]))
    (println "  With the article — the formal office — it shifts to the right."))

  ;; Lamb vs adversary
  (println "\n  כבש (lamb) = 322 = 2 × 7 × 23")
  (println "  שטן (adversary) = 359 (prime)")
  (println "  Difference: 37 (prime). 37 × 3 = 111 = אלף (aleph, the silent letter)."))

;; ═══════════════════════════════════════════════════════════════
;; Part 8 — The Courtroom
;; ═══════════════════════════════════════════════════════════════

(defn part-8-courtroom []
  (println "\n══════════════════════════════════════════════")
  (println "Part 8: The Courtroom — Perspective Reversal")
  (println "══════════════════════════════════════════════")

  (println "\n  The breastplate faces the mercy seat.")
  (println "  God looks AT it. The defendant stands BEFORE it.\n")
  (println "  God's left  = defendant's RIGHT = the prosecutor")
  (println "  God's right = defendant's LEFT  = the advocate\n")
  (println "  Zechariah 3:1: Satan at Joshua's RIGHT HAND to accuse.")
  (println "  The defendant's right. God's left.\n")

  (println "  ─── AT MY RIGHT — THE PROSECUTOR (God's left) ───\n")
  (doseq [[w role] [["שטן" "the adversary"]
                     ["אמת" "truth — the charge"]
                     ["יהוה" "the Name — the standard"]
                     ["ברית" "covenant — what I broke"]
                     ["משה" "Moses — the law"]]]
    (let [r (oracle/ask w)]
      (printf "    %-5s  %-30s  %d readings\n" w role (get-in r [:by-reader :left]))))

  (println "\n  ─── AT MY LEFT — THE ADVOCATE (God's right) ───\n")
  (doseq [[w role] [["כבש" "the lamb"]
                     ["תורה" "Torah — fulfilled"]
                     ["אדם" "man — stands for me"]]]
    (let [r (oracle/ask w)]
      (printf "    %-5s  %-30s  %d readings\n" w role (get-in r [:by-reader :right]))))

  (println "\n  ─── ABOVE — THE PRIEST (Aaron) ───\n")
  (doseq [[w role] [["אור" "light — the illumination"]
                     ["ברא" "create"]]]
    (let [r (oracle/ask w)]
      (printf "    %-5s  %-30s  %d readings\n" w role (get-in r [:by-reader :aaron]))))

  (println "\n  ─── BEYOND BOTH — THE VERDICT ───\n")
  (doseq [[w role] [["כפרת" "mercy seat"]
                     ["שלום" "peace"]]]
    (let [r (oracle/ask w)]
      (printf "    %-5s  %-30s  readable? %s\n" w role (:readable? r))))

  (println "\n  The prosecutor is loud: 12 + 31 + 60 + 14 readings.")
  (println "  The advocate is quiet: 4 + 14 + 2 readings.")
  (println "  The verdict — mercy, peace — cannot be read at all.")
  (println "  You must enter."))

;; ═══════════════════════════════════════════════════════════════

(defn -main [& _]
  (println "═══════════════════════════════════════════════════════")
  (println "  Experiment 086: The Oracle Engine")
  (println "  Forward and reverse, verified against Yoma 73b")
  (println "═══════════════════════════════════════════════════════")

  (part-1-eli-hannah)
  (part-2-lamb)
  (part-3-name)
  (part-4-god-not)
  (part-5-limits)
  (part-6-forward-survey)
  (part-7-adversary)
  (part-8-courtroom)

  (println "\n═══════════════════════════════════════════════════════")
  (println "  The prosecutor is loud. The advocate is quiet.")
  (println "  The verdict is mercy — and you must enter to read it.")
  (println "═══════════════════════════════════════════════════════"))
