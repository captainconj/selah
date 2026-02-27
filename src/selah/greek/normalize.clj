(ns selah.greek.normalize
  "Greek letter normalization, classification, and isopsephy (gematria).")

;; ── Greek letter ranges ────────────────────────────────────────────

(defn greek-letter?
  "True if ch is a Greek letter (α-ω, Α-Ω), including final sigma ς."
  [ch]
  (let [cp (int ch)]
    (or (<= 0x0391 cp 0x03A9)   ; Α-Ω (uppercase)
        (<= 0x03B1 cp 0x03C9)   ; α-ω (lowercase)
        (= cp 0x03C2))))        ; ς (final sigma)

(defn strip-accents
  "Remove combining diacriticals (accents, breathing marks, iota subscript)
   from a Greek string using Unicode canonical decomposition."
  [s]
  (let [nfd (java.text.Normalizer/normalize s java.text.Normalizer$Form/NFD)]
    (apply str (remove #(<= 0x0300 (int %) 0x036F) nfd))))

(defn normalize
  "Strip accents, punctuation, spaces — keep only Greek letters.
   Returns lowercase string."
  [s]
  (let [stripped (strip-accents s)]
    (apply str (filter greek-letter? (.toLowerCase stripped)))))

(defn letter-stream
  "Normalized Greek text as a vector of chars."
  [s]
  (vec (normalize s)))

;; ── Vowels and consonants ──────────────────────────────────────────

(def vowels #{\α \ε \η \ι \ο \υ \ω})
(def consonants #{\β \γ \δ \ζ \θ \κ \λ \μ \ν \ξ \π \ρ \σ \ς \τ \φ \χ \ψ})

(defn vowel? [ch] (contains? vowels ch))
(defn consonant? [ch] (contains? consonants ch))

(defn count-vowels [s] (count (filter vowel? (normalize s))))
(defn count-consonants [s] (count (filter consonant? (normalize s))))

;; ── Isopsephy (Greek gematria) ─────────────────────────────────────
;;
;; Standard Milesian system:
;;   α=1 β=2 γ=3 δ=4 ε=5 ϛ=6 ζ=7 η=8 θ=9
;;   ι=10 κ=20 λ=30 μ=40 ν=50 ξ=60 ο=70 π=80 (ϟ=90)
;;   ρ=100 σ/ς=200 τ=300 υ=400 φ=500 χ=600 ψ=700 ω=800
;;
;; ϛ (stigma/digamma, 6) and ϟ (koppa, 90) are archaic.
;; Modern texts don't use them, so we skip 6 and 90 in the mapping.

(def isopsephy-values
  {\α 1, \β 2, \γ 3, \δ 4, \ε 5,
   \ζ 7, \η 8, \θ 9,
   \ι 10, \κ 20, \λ 30, \μ 40, \ν 50, \ξ 60, \ο 70, \π 80,
   \ρ 100, \σ 200, \ς 200, \τ 300, \υ 400, \φ 500, \χ 600, \ψ 700, \ω 800})

(defn isopsephy
  "Calculate the isopsephy (Greek gematria) value of a word or string.
   Strips accents and lowercases first."
  [s]
  (reduce + 0 (map #(get isopsephy-values % 0) (normalize s))))

(defn word-isopsephy
  "Isopsephy of each word in a string. Returns seq of [word value] pairs."
  [s]
  (for [w (re-seq #"\p{IsGreek}+" (strip-accents s))]
    [w (isopsephy w)]))

;; ── Robinson transliteration → Unicode Greek ───────────────────────
;;
;; Used by Westcott-Hort, Textus Receptus, Scrivener parsed files.
;; Final sigma: 'v' at word end. Otherwise v doesn't appear.

(def ^:private robinson->greek
  {\a \α, \b \β, \g \γ, \d \δ, \e \ε, \z \ζ, \h \η, \q \θ,
   \i \ι, \k \κ, \l \λ, \m \μ, \n \ν, \x \ξ, \o \ο, \p \π,
   \r \ρ, \s \σ, \t \τ, \u \υ, \f \φ, \c \χ, \y \ψ, \w \ω,
   \v \ς})

(defn robinson->unicode
  "Convert Robinson/CCAT transliterated Greek to Unicode Greek.
   Handles final sigma (v → ς)."
  [s]
  (apply str (map #(get robinson->greek % %) (.toLowerCase s))))

(comment
  (normalize "Βίβλος γενέσεως Ἰησοῦ Χριστοῦ")
  ;=> "βιβλοσγενεσεωσιησουχριστου"

  (isopsephy "Ἰησοῦς")
  ;=> 888

  (robinson->unicode "ihsouv")
  ;=> "ιησους"

  (word-isopsephy "Βίβλος γενέσεως")
  ;=> (["βιβλος" 314] ["γενεσεως" 1263])
  )
