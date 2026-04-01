;; Ark Walk 09 — Inside the Rooms
;; Three-floor oracle analysis: run every word on each floor through forward-by-head
;; Foundation affirms (55% fixed), Specification transforms (47%), Door knows (75% fixed)

(require '[selah.space.coords :as sc]
         '[selah.oracle :as o]
         '[selah.gematria :as g]
         '[selah.dict :as dict]
         '[selah.basin :as basin]
         '[clojure.string :as str])

(sc/build!)

(let [s (sc/space)

      ;; Words per floor
      foundation-words  ;; c=7
      ["משחיתם" "את" "הארץ" "עשה" "לך" "תבת" "עצי" "גפר"
       "קנים" "תעשה" "התבה" "וכפרת" "אתה" "מבית" "ומחוץ" "בכפר" "וזה"]

      specification-words  ;; c=8
      ["שר" "תעשה" "אתה" "שלש" "מאות" "אמה" "ארך" "התבה"
       "חמשים" "רחבה" "ושלשים" "קומתה" "צהר" "לתבה" "ואל"]

      door-words  ;; c=9
      ["מה" "תכלנה" "מלמעלה" "ופתח" "התבה" "בצדה" "תשים"
       "תחתים" "שנים" "ושלשים" "תעשה" "ואני" "הנני" "מביא" "את" "המבול"]

      classify (fn [w]
                 (try
                   (let [fwd (o/forward (seq w) :torah)
                         known (:known-words fwd)
                         illum (count (or (:illumination-sets fwd) []))]
                     (cond
                       (zero? illum) :invisible
                       (empty? known) :ghost
                       (= w (:word (first known))) :fixed
                       :else :transform))
                   (catch Exception _ :error)))

      floor-stats (fn [name words c]
                    (println (format "\n=== FLOOR: %s (c=%d) ===" name c))
                    (let [results (for [w (distinct words)]
                                   (let [cls (classify w)
                                         gv (g/word-value w)]
                                     {:word w :gv gv :class cls}))
                          by-class (group-by :class results)
                          total (count results)]
                      ;; Per-word detail
                      (doseq [entry (sort-by :gv results)]
                        (let [top-word (when (#{:fixed :transform} (:class entry))
                                         (try (:word (first (:known-words (o/forward (seq (:word entry)) :torah))))
                                              (catch Exception _ nil)))
                              arrow (if (and top-word (not= (:word entry) (str top-word)))
                                      (str " → " top-word)
                                      "")]
                          (println (format "  %-10s gv=%-4d  %-10s %s%s"
                                           (:word entry) (:gv entry) (name (:class entry))
                                           (or (dict/translate (:word entry)) "") arrow))))
                      ;; Summary
                      (println)
                      (doseq [cls [:fixed :transform :ghost :invisible]]
                        (let [n (count (get by-class cls []))]
                          (when (pos? n)
                            (println (format "  %-10s: %d/%d (%.0f%%)"
                                             (clojure.core/name cls) n total
                                             (* 100.0 (/ n total)))))))
                      results))]

  ;; Run all three floors
  (let [f7 (floor-stats "Foundation" foundation-words 7)
        f8 (floor-stats "Specification" specification-words 8)
        f9 (floor-stats "Door" door-words 9)]

    ;; ═══ CROSS-FLOOR COMPARISON ═══
    (println "\n=== CROSS-FLOOR COMPARISON ===")
    (println "  Floor          Fixed%  Transform%  Ghost%  Invisible%")
    (doseq [[name results] [["Foundation (c=7)" f7]
                             ["Specification (c=8)" f8]
                             ["Door (c=9)" f9]]]
      (let [by-class (group-by :class results)
            total (count results)]
        (println (format "  %-20s  %5.0f%%  %10.0f%%  %5.0f%%  %9.0f%%"
                         name
                         (* 100.0 (/ (count (get by-class :fixed [])) total))
                         (* 100.0 (/ (count (get by-class :transform [])) total))
                         (* 100.0 (/ (count (get by-class :ghost [])) total))
                         (* 100.0 (/ (count (get by-class :invisible [])) total)))))))
  (println)

  ;; ═══ KEY TRANSFORMS PER-HEAD ═══
  (println "=== KEY TRANSFORMS: PER-HEAD DETAIL ===")
  (doseq [w ["קנים" "וכפרת" "אמה" "רחבה" "קומתה" "צהר"
             "שנים" "תכלנה" "חמשים"]]
    (println (format "\n  %s (gv=%d) %s:" w (g/word-value w) (or (dict/translate w) "")))
    (let [by-head (o/forward-by-head w)]
      (doseq [reader [:aaron :god :truth :mercy]]
        (let [words (get by-head reader)]
          (doseq [rw (take 2 words)]
            (println (format "    %-6s: %-10s count=%-3d  %s"
                             (clojure.core/name reader) (:word rw) (:reading-count rw)
                             (or (dict/translate (:word rw)) ""))))))))

  ;; ═══ GHOST ZONE DETAIL ═══
  (println "\n=== GHOST ZONE DETAIL ===")
  (doseq [w ["גפר" "קנים" "חמשים" "תבה" "משחיתם"]]
    (try
      (let [fwd (o/forward (seq w) :torah)
            illum (count (or (:illumination-sets fwd) []))
            readings (count (or (:readings fwd) []))]
        (println (format "  %-10s  illuminations=%-4d  readings=%-4d  known=%-2d"
                         w illum readings (count (:known-words fwd)))))
      (catch Exception e
        (println (format "  %-10s  error" w))))))
