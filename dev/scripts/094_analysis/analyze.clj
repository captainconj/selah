(require '[clojure.edn :as edn] '[selah.gematria :as g])
(def syn (edn/read-string (slurp "data/experiments/094/synonyms.edn")))
(let [pairs (:synonyms syn)
      adj (reduce (fn [m {:keys [word-a word-b]}]
                    (-> m (update word-a (fnil conj #{}) word-b)
                          (update word-b (fnil conj #{}) word-a)))
                  {} pairs)
      seen (volatile! #{})
      groups (volatile! [])]
  (doseq [w (keys adj)]
    (when-not (contains? @seen w)
      (loop [q [w] g #{w}]
        (if (empty? q)
          (do (vswap! seen into g) (vswap! groups conj g))
          (let [nbrs (remove g (get adj (first q) []))]
            (recur (into (subvec (vec q) 1) nbrs) (into g nbrs)))))))
  (let [sg (sort-by (comp - count) @groups)]
    (println "Groups:" (count sg) "| Sizes:" (frequencies (map count sg)))
    (doseq [g (take 15 (filter #(>= (count %) 3) sg))]
      (println (format "%d-way: %s  GV=%d" (count g)
                       (clojure.string/join " = " (sort g))
                       (g/word-value (first g)))))))

(println)
(def tail (edn/read-string (slurp "data/experiments/094/long-tail.edn")))
(println "Forced readings with meaning:")
(doseq [{:keys [word gv meaning phrases]} (:forced tail) :when meaning]
  (println (format "  %s (%s, GV=%d)" word meaning gv)))
