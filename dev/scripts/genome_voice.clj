;; Genome Voice — The whole human proteome through the breastplate
;;
;; Reads all 20,442 reviewed human proteins from UniProt reference proteome,
;; converts to Hebrew letters, counts unique 3-letter and 4-letter windows,
;; runs each unique window through the oracle once, weights by frequency.
;;
;; Discovery: forward = reverse (the breastplate reads anagrams).
;; Discovery: 25% of Torah vocabulary is structurally silent (Aleph + Samekh).

(ns scripts.genome-voice
  (:require [selah.dna :as dna]
            [selah.oracle :as o]
            [selah.gematria :as g]
            [clojure.string :as str]
            [clojure.java.io :as io]))

;; ── Step 1: Load the proteome ──────────────────────────────

(defn load-proteome
  "Load the human reference proteome FASTA → vector of {:id :sequence}."
  []
  (let [path "data/sequences/human-proteome-reviewed.fasta"
        content (slurp path)
        entries (->> (str/split content #">")
                     (remove str/blank?)
                     (map (fn [block]
                            (let [lines (str/split-lines (str/trim block))
                                  header (first lines)
                                  [_ id] (re-matches #"(\S+).*" (or header ""))
                                  seq-str (->> (rest lines)
                                               (map str/trim)
                                               (apply str))]
                              {:id (or id "unknown") :sequence seq-str})))
                     vec)]
    (println (str "Loaded " (count entries) " proteins"))
    entries))

;; ── Step 2: Convert all to Hebrew ─────────────────────────

(defn proteome->hebrew
  "Convert entire proteome to one long Hebrew string + per-protein strings."
  [proteins]
  (let [hebrews (mapv #(dna/protein-str->hebrew (:sequence %)) proteins)
        all (apply str hebrews)
        freqs (frequencies all)]
    (println (str "Total Hebrew letters: " (count all)))
    (println (str "Letter frequencies:"))
    (doseq [[ch cnt] (sort-by val > freqs)]
      (println (format "  %s = %d (%.1f%%)" ch cnt (* 100.0 (/ (double cnt) (count all))))))
    {:all all
     :per-protein hebrews
     :letter-count (count all)
     :letter-frequencies freqs}))

;; ── Step 3: Count unique windows ──────────────────────────

(defn count-windows
  "Count unique n-letter windows across all protein Hebrew strings."
  [hebrews window-size]
  (let [all-windows (mapcat (fn [h]
                              (for [i (range (- (count h) (dec window-size)))]
                                (subs h i (+ i window-size))))
                            hebrews)
        freq-map (frequencies all-windows)
        unique (count freq-map)
        total (count all-windows)]
    (println (str "Window size: " window-size))
    (println (str "Total windows: " total))
    (println (str "Unique windows: " unique))
    {:window-size window-size
     :total total
     :unique unique
     :frequencies freq-map}))

;; ── Step 4: Run oracle on unique windows ──────────────────

(defn oracle-sweep
  "Run oracle on each unique window, return weighted results."
  [window-freqs]
  (let [windows (keys window-freqs)
        n (count windows)
        _ (println (str "Running oracle on " n " unique windows..."))
        results (doall
                  (pmap (fn [w]
                          (let [letters (vec w)
                                fwd (o/forward letters)]
                            {:window w
                             :frequency (get window-freqs w 0)
                             :known-words (:known-words fwd)}))
                        windows))
        ;; Weight words by window frequency
        weighted (->> results
                      (mapcat (fn [{:keys [frequency known-words]}]
                                (for [{:keys [word reading-count]} known-words]
                                  {:word word
                                   :weight (* frequency reading-count)})))
                      (group-by :word)
                      (map (fn [[w entries]]
                             {:word w
                              :count (reduce + (map :weight entries))}))
                      (sort-by :count >)
                      vec)]
    (println (str "Done. " (count weighted) " unique words."))
    {:results results
     :weighted weighted}))

;; ── Step 5: The voice ─────────────────────────────────────

(defn genome-voice
  "Full pipeline: load → convert → count → oracle → save."
  [& {:keys [window] :or {window 3}}]
  (let [proteins (load-proteome)
        {:keys [all per-protein letter-count letter-frequencies]} (proteome->hebrew proteins)

        ;; GV of entire genome
        gv (g/word-value all)
        _ (println (str "\nGV of entire genome: " gv))

        ;; Count unique windows
        {:keys [total unique frequencies]} (count-windows per-protein window)

        ;; Run oracle
        {:keys [weighted]} (oracle-sweep frequencies)

        ;; Print top words
        _ (println (str "\nTop 50 words (window=" window "):"))
        _ (doseq [{:keys [word count]} (take 50 weighted)]
            (println (format "  %-8s %,d" word count)))

        ;; Save
        out-path (str "data/dna/genome-voice" (when (> window 3) (str "-" window "letter")) ".edn")
        data {:protein-count (count proteins)
              :letter-count letter-count
              :gv gv
              :letter-frequencies letter-frequencies
              :window-size window
              :total-windows total
              :unique-windows unique
              :top-words (vec (take 200 weighted))
              :all-words weighted}]

    (io/make-parents out-path)
    (spit out-path (pr-str data))
    (println (str "\nSaved: " out-path))
    data))

;; ── Step 6: Silent letters analysis ───────────────────────

(defn silent-letters
  "Analyze which Torah words are impossible for the genome.
   Aleph (א) is unmapped. Samekh (ס) is the Stop codon."
  []
  (let [vocab (o/torah-words)
        has-aleph (filter #(some #{\א} (seq %)) vocab)
        has-samekh (filter #(some #{\ס} (seq %)) vocab)
        has-either (filter #(or (some #{\א} (seq %))
                                (some #{\ס} (seq %))) vocab)]
    (println (str "Torah vocabulary: " (count vocab) " words"))
    (println (str "Contain Aleph (א): " (count has-aleph) " (" (format "%.1f" (* 100.0 (/ (double (count has-aleph)) (count vocab)))) "%)"))
    (println (str "Contain Samekh (ס): " (count has-samekh) " (" (format "%.1f" (* 100.0 (/ (double (count has-samekh)) (count vocab)))) "%)"))
    (println (str "Either (impossible): " (count has-either) " (" (format "%.1f" (* 100.0 (/ (double (count has-either)) (count vocab)))) "%)"))
    (println)
    (println "Notable impossible words:")
    (doseq [w ["אדם" "אהבה" "אמת" "ברא" "אלהים" "אהיה" "חסד" "סלה" "ישראל" "משה"]]
      (when (some #(= w %) has-either)
        (println (str "  " w " — " (o/translate-word w)))))
    {:total (count vocab)
     :aleph-count (count has-aleph)
     :samekh-count (count has-samekh)
     :impossible-count (count has-either)}))

;; ── Run ───────────────────────────────────────────────────

(comment
  ;; 3-letter genome voice
  (genome-voice)

  ;; 4-letter genome voice
  (genome-voice :window 4)

  ;; Silent letters analysis
  (silent-letters)

  ;; Forward = Reverse proof
  (let [proteins (load-proteome)
        hebrews (mapv #(dna/protein-str->hebrew (:sequence %)) proteins)
        fwd-freqs (count-windows hebrews 4)
        rev-freqs (count-windows (mapv #(apply str (reverse %)) hebrews) 4)]
    (= (:frequencies fwd-freqs) (:frequencies rev-freqs)))
  ;; => true — the breastplate reads anagrams
  )
