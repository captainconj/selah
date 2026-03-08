(ns experiments.105-the-four-bloods
  "Experiment 105: The Blood of the Four Living Creatures.

   The throne room has four living creatures with four faces:
   lion, ox, man, eagle (Rev 4:7, Ezek 1:10). Each is a real
   creature with real blood. We play hemoglobin-alpha from each
   species through the breastplate and compare.

   RESULTS:
   - All four share: שדי (Almighty) at 51, זרק (sprinkle) at 139
   - Lion, Ox, Man share: הוה (HVH/Name) at 37. Eagle is SILENT.
   - Lion unique: פקד (captain) at 11. דגי (fish) at 4.
   - Ox unique: קדש (holy!) at 59. גוי (nation) at 114.
   - Eagle unique: דבש (honey!) at 76. קהת (Kohath) at 14. לעד (forever) at 20.
   - Man unique: חנה (Hannah) at 66. תלד (birth) at 115.
   - Binding (עקד): in ox (88), eagle (56), man (88). NOT in lion.
   - No creature's blood contains the serpent (נחש).
   - GV: Lion 9767, Eagle 9677, Ox 9627, Man 8991 (lowest)."
  (:require [selah.dna :as dna]
            [selah.oracle :as o]
            [selah.gematria :as g]
            [clojure.string :as str]))

;; ── The Four Creatures ──────────────────────────────────

(def creatures
  [{:name  "Lion"
    :face  "אריה"
    :tribe "Judah"
    :camp  "east"
    :accession "P18975"
    :organism "Panthera leo"
    :protein "Hemoglobin subunit alpha"}
   {:name  "Ox"
    :face  "שור"
    :tribe "Ephraim"
    :camp  "west"
    :accession "P01966"
    :organism "Bos taurus"
    :protein "Hemoglobin subunit alpha"}
   {:name  "Eagle"
    :face  "נשר"
    :tribe "Dan"
    :camp  "north"
    :accession "P01993"
    :organism "Aquila chrysaetos (Golden eagle)"
    :protein "Hemoglobin subunit alpha-A"}
   {:name  "Man"
    :face  "אדם"
    :tribe "Reuben"
    :camp  "south"
    :accession "P69905"
    :organism "Homo sapiens"
    :protein "Hemoglobin subunit alpha"}])

;; ── Helpers ──────────────────────────────────────────────

(defn get-hebrew [accession]
  (let [r (dna/fetch-uniprot accession)]
    {:hebrew (dna/protein-str->hebrew (:sequence r))
     :length (count (:sequence r))
     :desc (:description r)}))

(defn fmt-head [by-head head n]
  (let [words (get by-head head)]
    (if (seq words)
      (->> words
           (sort-by (comp - :reading-count))
           (take n)
           (map #(str (:word %) "(" (:reading-count %) ")"))
           (str/join " "))
      "—")))

(defn window-by-head
  "Get per-head readings for a 3-letter window at position pos."
  [hebrew pos]
  (let [w (subs hebrew pos (+ pos 3))]
    {:position pos
     :letters w
     :gv (g/word-value w)
     :by-head (o/forward-by-head (seq w) :torah)}))

(defn print-window [hebrew pos]
  (let [{:keys [letters by-head]} (window-by-head hebrew pos)]
    (println (format "  [%3d] %s:" pos letters))
    (doseq [head [:aaron :god :right :left]]
      (let [s (fmt-head by-head head 3)]
        (when (not= s "—")
          (println (format "    %-6s: %s" (name head) s)))))))

;; ── Full slide with oracle ──────────────────────────────

(defn run-creature [{:keys [name face tribe camp accession organism protein]}]
  (println (str "\n══════════════════════════════════════"))
  (println (str "  " name " (" face ") — " organism))
  (println (str "  " tribe " camp, " camp))
  (println (str "══════════════════════════════════════"))
  (let [{:keys [hebrew length desc]} (get-hebrew accession)
        gv (g/word-value hebrew)
        hits (dna/slide hebrew {:window 3 :vocab :torah})
        top-words (dna/word-frequencies hits)]
    (println (str desc))
    (println (str length " residues. GV=" gv))
    (println (str "Readings: " (count hits) "/" (- (count hebrew) 2)))
    (println (str "\nHebrew: " hebrew))

    (println "\nAll readings:")
    (doseq [{:keys [position letters top-5]} hits]
      (let [top (first top-5)]
        (println (format "  [%3d] %s → %s (%s)"
                         position letters
                         (:word top) (or (:meaning top) "?")))))

    (println "\nTop words:")
    (doseq [{:keys [word meaning count]} (take 15 top-words)]
      (println (format "  %-8s %-20s ×%d" word (or meaning "") count)))

    {:name name :face face :hebrew hebrew :gv gv
     :hits hits :top-words top-words}))

;; ── Comparison analysis ─────────────────────────────────

(defn compare-creatures [results]
  (println "\n══════════════════════════════════════")
  (println "  COMPARISON: Four Living Creatures")
  (println "══════════════════════════════════════")

  ;; Summary
  (doseq [{:keys [name gv hits top-words]} results]
    (println (format "%s: GV=%d, %d readings, %d unique words"
                     name gv (count hits) (count top-words))))

  ;; Check shared positions
  (println "\n── Shared vocabulary ──")
  (let [hebrews (mapv :hebrew results)]
    ;; Position 37: HVH
    (println "\nPosition 37 (HVH / root of YHWH):")
    (doseq [[r heb] (map vector results hebrews)]
      (let [w (subs heb 37 40)]
        (println (format "  %s: %s" (:name r) w))
        (print-window heb 37)))

    ;; Position 51: Shaddai
    (println "\nPosition 51 (Shaddai / the Almighty):")
    (doseq [[r heb] (map vector results hebrews)]
      (println (format "  %s:" (:name r)))
      (print-window heb 51))

    ;; Position 139: Sprinkle
    (println "\nPosition 139 (Sprinkle — last reading):")
    (doseq [[r heb] (map vector results hebrews)]
      (println (format "  %s:" (:name r)))
      (print-window heb 139))

    ;; Unique positions
    (println "\n── Unique readings ──")

    (println "\nLION unique:")
    (print-window (nth hebrews 0) 3)   ;; hands
    (print-window (nth hebrews 0) 4)   ;; fish
    (print-window (nth hebrews 0) 11)  ;; captain

    (println "\nOX unique:")
    (print-window (nth hebrews 1) 59)  ;; holy!
    (print-window (nth hebrews 1) 88)  ;; Binding
    (print-window (nth hebrews 1) 114) ;; nation

    (println "\nEAGLE unique:")
    (print-window (nth hebrews 2) 3)   ;; together
    (print-window (nth hebrews 2) 14)  ;; Kohath
    (print-window (nth hebrews 2) 20)  ;; forever
    (print-window (nth hebrews 2) 56)  ;; Binding
    (print-window (nth hebrews 2) 76)  ;; honey!

    (println "\nMAN unique:")
    (print-window (nth hebrews 3) 3)   ;; his hand
    (print-window (nth hebrews 3) 66)  ;; Hannah
    (print-window (nth hebrews 3) 88)  ;; Binding
    (print-window (nth hebrews 3) 115) ;; birth

    ;; Check for serpent
    (println "\n── Serpent (נחש) search ──")
    (doseq [[r heb] (map vector results hebrews)]
      (let [windows (for [i (range (- (count heb) 2))]
                      [(subs heb i (+ i 3)) i])
            matches (filter (fn [[w _]] (= (set w) #{\נ \ח \ש})) windows)]
        (if (seq matches)
          (doseq [[w i] matches]
            (println (str (:name r) ": serpent anagram " w " at position " i)))
          (println (str (:name r) ": no serpent")))))))

;; ── Run ─────────────────────────────────────────────────

(defn run-all []
  (let [results (mapv run-creature creatures)]
    (compare-creatures results)
    results))

(comment
  (run-all)

  ;; Run individual creatures
  (run-creature (nth creatures 0))  ;; Lion
  (run-creature (nth creatures 1))  ;; Ox
  (run-creature (nth creatures 2))  ;; Eagle
  (run-creature (nth creatures 3))  ;; Man

  ;; Also useful: bovine insulin (first protein ever sequenced)
  ;; (get-hebrew "P01317") — the ox gave us the first reading
  )
