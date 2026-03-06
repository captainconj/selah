;; Ark Walk 12 — The Measure
;; Three-floor parallel read: c=9 over c=8 over c=7, every d-position
;; ושלשים appears identically on c=9 and c=8.
;; ואני (GV=67) = the d-axis. מביא (GV=53) = the garden.

(require '[selah.space.coords :as sc]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      at (fn [c d] (sc/letter-at s (sc/coord->idx 0 8 c d)))

      factorize (fn [n]
                  (loop [n n d 2 factors []]
                    (cond
                      (< n 2) factors
                      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
                      :else (recur n (inc d) factors))))

      ;; Read a range on a layer
      read-range (fn [c d-start d-end]
                   (apply str (for [d (range d-start (inc d-end))] (at c d))))]

  ;; ═══ THREE FLOORS PARALLEL ═══
  (println "=== THREE FLOORS PARALLEL: c=9 / c=8 / c=7 ===")
  (println)

  ;; Aligned word table
  (let [segments
        [{:d [0 1]    :c9 "מה (what,45)"        :c8 "שר (prince,500)"       :c7 "ני (witness)"}
         {:d [2 6]    :c9 "תכלנה (finish)"       :c8 "תעשה א (make)"        :c7 "משחית (destroy)"}
         {:d [7 12]   :c9 "מלמעלה (from above)"  :c8 "אתה שלש (you+three)"  :c7 "את הארץ (sign+earth)"}
         {:d [13 16]  :c9 "ופתח (door,488)"      :c8 "מאות א (hundreds)"    :c7 "ארץ עשה (earth+make)"}
         {:d [17 20]  :c9 "התבה (ark)"            :c8 "אמה ארך (cubit+len)"  :c7 "לך תב (for you+ark)"}
         {:d [21 24]  :c9 "בצדה (in its side)"   :c8 "התבה ח (ark)"         :c7 "עצי (wood)"}
         {:d [25 28]  :c9 "תשים (place)"         :c8 "חמש (fifty)"          :c7 "גפר קנים (gopher+rooms)"}
         {:d [29 33]  :c9 "תחתים (lower)"        :c8 "אמה רחב (cubit+brd)"  :c7 "..."}
         {:d [34 37]  :c9 "שנים (pairs,400)"     :c8 "רחבה (breadth)"       :c7 "את התבה (sign+ark)"}
         {:d [38 43]  :c9 "ושלשים (and-thirty)"  :c8 "ושלשים (SAME WORD!)"  :c7 "התבה וכפרת (ark+atone)"}
         {:d [44 47]  :c9 "תעשה (make)"          :c8 "אמה קומתה (cubit+h)"  :c7 "כפרת אתה (atonement)"}
         {:d [48 51]  :c9 "ואני (and-I,67!)"     :c8 "קומתה (height)"       :c7 "מבית (inside)"}
         {:d [52 55]  :c9 "הנני (behold I am)"   :c8 "צהר תעשה (window)"    :c7 "מבית ומחוץ (in+out)"}
         {:d [56 59]  :c9 "מביא (bringing,53!)"  :c8 "תעשה לתבה (make+ark)" :c7 "ומחוץ בכפר (out+atone)"}
         {:d [60 61]  :c9 "את (sign,401)"        :c8 "תב (ark-root)"        :c7 "כפ (atone-root)"}
         {:d [62 66]  :c9 "המבול (flood,83)"     :c8 "ואל א (and-to+bridge)":c7 "וזה א (life+bridge)"}]]

    (doseq [seg segments]
      (println (format "  d=%-5s  c=9: %-25s  c=8: %-25s  c=7: %-25s"
                       (str (first (:d seg)) "-" (second (:d seg)))
                       (:c9 seg) (:c8 seg) (:c7 seg)))))
  (println)

  ;; ═══ IDENTICAL WORD ═══
  (println "=== IDENTICAL WORD (d=38-43) ===")
  (let [c9-word (read-range 9 38 43)
        c8-word (read-range 8 38 43)]
    (println (format "  c=9: %s  GV=%d" c9-word (g/word-value c9-word)))
    (println (format "  c=8: %s  GV=%d" c8-word (g/word-value c8-word)))
    (println (format "  Identical? %s" (= c9-word c8-word))))
  (println)

  ;; ═══ THE SPEAKER ═══
  (println "=== THE SPEAKER (d=48-51) ===")
  (let [speaker (read-range 9 48 51)]
    (println (format "  ואני (and I) GV = %d = d-axis = understanding" (g/word-value speaker)))
    (println "  The speaker's value IS the axis of understanding"))
  (println)

  ;; ═══ THE BRINGER ═══
  (println "=== THE BRINGER (d=56-59) ===")
  (let [bringer (read-range 9 56 59)]
    (println (format "  מביא (bringing) GV = %d = garden" (g/word-value bringer))))
  (println)

  ;; ═══ FIRST COLUMN: OIL ═══
  (println "=== FIRST COLUMN (d=0): ANOINTING OIL ===")
  (let [d0 (apply str (for [c [9 8 7]] (at c 0)))]
    (println (format "  c=9,8,7 at d=0: %s  GV=%d" d0 (g/word-value d0)))
    (println "  Rearranged: שמן (oil/anointing)"))
  (println)

  ;; ═══ THREE-FLOOR TOTAL ═══
  (println "=== THREE-FLOOR TOTAL ===")
  (let [total (reduce + (for [c [7 8 9] d (range 67)]
                           (g/letter-value (at c d))))]
    (println (format "  GV = %d  factors = %s" total (factorize total)))))
