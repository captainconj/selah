(ns selah.aramaic.normalize
  "Syriac/Aramaic letter normalization, classification, and gematria.")

;; ── Syriac letter range (U+0710–U+072C) ───────────────────────────

(defn syriac-letter?
  "True if ch is a Syriac letter (ܐ-ܬ)."
  [ch]
  (let [cp (int ch)]
    ;; Base Syriac letters: U+0710 (Alaph) through U+072C (Taw)
    ;; Skip U+0711 (superscript alaph combining mark)
    (and (<= 0x0710 cp 0x072C)
         (not= cp 0x0711))))

(defn normalize
  "Strip everything except Syriac letters.
   Removes vowel points (U+0730–U+074A), punctuation, spaces."
  [s]
  (apply str (filter syriac-letter? s)))

(defn letter-stream
  "Normalized Syriac text as a vector of chars."
  [s]
  (vec (normalize s)))

;; ── Matres lectionis (quasi-vowels) ───────────────────────────────
;; Syriac is consonantal. These letters can serve as vowel indicators:
;;   ܐ (Alaph), ܘ (Waw), ܝ (Yodh)

(def matres-lectionis #{\ܐ \ܘ \ܝ})

(defn mater-lectionis? [ch] (contains? matres-lectionis ch))

(defn count-matres [s] (count (filter mater-lectionis? (normalize s))))
(defn count-pure-consonants [s] (count (remove mater-lectionis? (normalize s))))

;; ── Gematria (Syriac uses same values as Hebrew) ──────────────────
;;
;;   ܐ=1  ܒ=2  ܓ=3  ܕ=4  ܗ=5  ܘ=6  ܙ=7  ܚ=8  ܛ=9
;;   ܝ=10 ܟ=20 ܠ=30 ܡ=40 ܢ=50 ܣ=60 ܥ=70 ܦ=80 ܨ=90
;;   ܩ=100 ܪ=200 ܫ=300 ܬ=400

(def gematria-values
  {\ܐ 1,   \ܒ 2,   \ܓ 3,   \ܕ 4,   \ܗ 5,   \ܘ 6,   \ܙ 7,   \ܚ 8,   \ܛ 9,
   \ܝ 10,  \ܟ 20,  \ܠ 30,  \ܡ 40,  \ܢ 50,  \ܣ 60,  \ܥ 70,  \ܦ 80,  \ܨ 90,
   \ܩ 100, \ܪ 200, \ܫ 300, \ܬ 400})

(defn gematria
  "Calculate the gematria value of a Syriac string."
  [s]
  (reduce + 0 (map #(get gematria-values % 0) (normalize s))))

(defn word-gematria
  "Gematria of each word. Returns seq of [word value] pairs."
  [s]
  (for [w (re-seq #"[\u0710-\u072C]+" s)]
    [w (gematria w)]))

(comment
  (normalize "ܟܬܒܐ ܕܝܠܝܕܘܬܗ")
  ;=> "ܟܬܒܐܕܝܠܝܕܘܬܗ"

  (gematria "ܝܫܘܥ")
  ;=> 386 (Yeshua: yodh=10 + shin=300 + waw=6 + ayin=70)

  (gematria "ܡܫܝܚܐ")
  ;=> 359 (Meshikha: mim=40 + shin=300 + yodh=10 + heth=8 + alaph=1)
  )
