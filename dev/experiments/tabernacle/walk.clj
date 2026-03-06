(require '[selah.space.coords :as sc])

(let [s (sc/space)
      vrefs (:verse-ref s)

      find-verse (fn [ch vs]
                   (first (filter #(and (= "Exodus" (:book %))
                                        (= ch (:ch %)) (= vs (:vs %)))
                                  vrefs)))

      ;; Every station of the tabernacle walk, outside → inside
      stations [;; The Gate
               {:name "Gate (instruction)" :ch 27 :vs 16}
               {:name "Gate (built)" :ch 38 :vs 18}
               ;; The Courtyard
               {:name "Courtyard (instruction)" :ch 27 :vs 9}
               {:name "Courtyard (built)" :ch 38 :vs 9}
               ;; Altar of Burnt Offering
               {:name "Altar of Burnt Offering (instruction)" :ch 27 :vs 1}
               {:name "Altar of Burnt Offering (built)" :ch 38 :vs 1}
               ;; The Laver
               {:name "Laver (instruction)" :ch 30 :vs 18}
               {:name "Laver (built)" :ch 38 :vs 8}
               ;; The Tent structure
               {:name "Tabernacle curtains (instruction)" :ch 26 :vs 1}
               {:name "Tabernacle curtains (built)" :ch 36 :vs 8}
               ;; Table of Showbread
               {:name "Table of Showbread (instruction)" :ch 25 :vs 23}
               {:name "Table of Showbread (built)" :ch 37 :vs 10}
               ;; The Menorah
               {:name "Menorah (instruction)" :ch 25 :vs 31}
               {:name "Menorah (built)" :ch 37 :vs 17}
               ;; Altar of Incense
               {:name "Altar of Incense (instruction)" :ch 30 :vs 1}
               {:name "Altar of Incense (built)" :ch 37 :vs 25}
               ;; The Veil
               {:name "Veil (instruction)" :ch 26 :vs 31}
               {:name "Veil (built)" :ch 36 :vs 35}
               ;; The Ark
               {:name "Ark (instruction)" :ch 25 :vs 10}
               {:name "Ark (built)" :ch 37 :vs 1}
               ;; Mercy Seat
               {:name "Mercy Seat (instruction)" :ch 25 :vs 17}
               {:name "Mercy Seat (built)" :ch 37 :vs 6}
               ;; The Meeting
               {:name "I will meet with you" :ch 25 :vs 22}
               ;; Glory fills
               {:name "Glory fills the tabernacle" :ch 40 :vs 34}
               ;; Erection command
               {:name "Set up the tabernacle" :ch 40 :vs 1}
               {:name "Place the Ark" :ch 40 :vs 3}
               ;; Center verse
               {:name "Guard the charge (center)" :ch 8 :vs 35 :book "Leviticus"}]

      results (vec (for [{:keys [name ch vs book] :or {book "Exodus"}} stations]
                     (let [v (first (filter #(and (= book (:book %))
                                                  (= ch (:ch %)) (= vs (:vs %)))
                                            vrefs))
                           coord (when v (vec (sc/idx->coord (:start v))))
                           letters (when v
                                     (apply str (for [i (range (:start v) (min (:end v) (+ (:start v) 20)))]
                                                  (sc/letter-at s i))))]
                       {:name name :ref (str (if (= book "Exodus") "Ex" "Lev") " " ch ":" vs)
                        :start (:start v) :coord coord :letters letters})))]

  (spit "/tmp/tab-stations.edn" (pr-str results))
  (doseq [{:keys [name ref coord letters]} results]
    (println (format "%-42s %-10s (%d,%2d,%2d,%2d)  %s"
                     name ref
                     (nth coord 0) (nth coord 1) (nth coord 2) (nth coord 3)
                     (or letters "")))))
