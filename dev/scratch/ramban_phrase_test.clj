(ns scratch.ramban-phrase-test
  "Test the Ramban's specific example: same illumination,
   one reader sees 'Judah shall go up', another sees 'woe unto Judah'."
  (:require [selah.oracle :as oracle]))

(defn run []
  (let [li oracle/letter-index
        he-positions (get li \ה)
        yod-positions (vec (get li \י))
        vav-positions (vec (get li \ו))
        dalet-positions (vec (get li \ד))
        ayin-positions (vec (get li \ע))
        lamed-positions (vec (get li \ל))
        hes (set he-positions)
        illuminations (atom [])
        seen (atom #{})]

    ;; Generate all valid 9-position illumination sets
    ;; Need: י×2, ה×3 (forced — only 3 on grid), ו×1, ד×1, ע×1, ל×1
    (doseq [i (range (count yod-positions))
            j (range (inc i) (count yod-positions))]
      (let [y1 (yod-positions i) y2 (yod-positions j)]
        (when-not (or (hes y1) (hes y2))
          (doseq [v vav-positions]
            (when-not (or (hes v) (= v y1) (= v y2))
              (doseq [d dalet-positions]
                (when-not (or (hes d) (= d y1) (= d y2) (= d v))
                  (doseq [a ayin-positions]
                    (when-not (or (hes a) (= a y1) (= a y2) (= a v) (= a d))
                      (doseq [l lamed-positions]
                        (when-not (or (hes l) (= l y1) (= l y2) (= l v) (= l d) (= l a))
                          (let [pset (set (concat he-positions [y1 y2 v d a l]))]
                            (when (and (= 9 (count pset)) (not (@seen pset)))
                              (swap! seen conj pset)
                              (swap! illuminations conj pset))))))))))))))

    (println (str (count @illuminations) " unique 9-position illumination sets"))

    (let [target-fwd "יהודהיעלה"
          target-rev "היעליהודה"
          fwd-counts (atom {:aaron 0 :god 0 :right 0 :left 0})
          rev-counts (atom {:aaron 0 :god 0 :right 0 :left 0})
          splits (atom 0)
          split-examples (atom [])]

      (doseq [pset @illuminations]
        (let [readings (into {} (map (fn [r] [r (oracle/read-positions r pset)])
                                     [:aaron :god :right :left]))
              fwd-readers (keep (fn [[r v]] (when (= v target-fwd) r)) readings)
              rev-readers (keep (fn [[r v]] (when (= v target-rev) r)) readings)]
          (doseq [r fwd-readers]
            (swap! fwd-counts update r inc))
          (doseq [r rev-readers]
            (swap! rev-counts update r inc))
          (when (and (seq fwd-readers) (seq rev-readers))
            (swap! splits inc)
            (when (<= @splits 10)
              (swap! split-examples conj readings)))))

      (println (str "\n\"יהודהיעלה\" (Judah shall go up) produced by:"))
      (doseq [r [:aaron :god :right :left]]
        (println (format "  %-8s: %d times" (name r) (get @fwd-counts r))))

      (println (str "\n\"היעליהודה\" (woe unto Judah) produced by:"))
      (doseq [r [:aaron :god :right :left]]
        (println (format "  %-8s: %d times" (name r) (get @rev-counts r))))

      (println (str "\n═══════════════════════════════"))
      (println (str "Ramban splits: " @splits " / " (count @illuminations)))
      (println "═══════════════════════════════")

      (doseq [[i readings] (map-indexed vector @split-examples)]
        (println (format "\nSplit #%d:" (inc i)))
        (doseq [r [:aaron :god :right :left]]
          (let [v (readings r)]
            (println (format "  %-8s: %s %s" (name r) v
                             (cond (= v target-fwd) "<-- Judah shall go up"
                                   (= v target-rev) "<-- woe unto Judah"
                                   :else ""))))))

      ;; Also: what other 9-letter outputs appear? Any other meaningful parsings?
      (println "\n── All unique 9-letter outputs (sample) ──")
      (let [all-outputs (atom {})]
        (doseq [pset (take 500 @illuminations)]
          (doseq [r [:aaron :god :right :left]]
            (let [v (oracle/read-positions r pset)]
              (swap! all-outputs update v (fnil inc 0)))))
        (println (str "Unique outputs from first 500 illuminations: " (count @all-outputs)))
        (println "Top 20 by frequency:")
        (doseq [[v cnt] (take 20 (sort-by val > @all-outputs))]
          (println (format "  %s  %dx" v cnt)))))))
