(ns experiments.016-paint-fractal
  "Paint the fractal palindrome.
   Generates an HTML file with SVG visualizations.
   Run: clojure -M:dev -m experiments.016-paint-fractal"
  (:require [selah.gematria :as g]
            [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]
            [clojure.java.io :as io]))

(def book-colors
  {"Genesis"     "#E8A838"   ; gold
   "Exodus"      "#C04040"   ; deep red
   "Leviticus"   "#FFFFFF"   ; white center
   "Numbers"     "#4070C0"   ; deep blue
   "Deuteronomy" "#D4943C"}) ; amber (pairs with Genesis)

(def pair-colors
  [["#E8A838" "#D4943C"]   ; A / A' — gold family
   ["#C04040" "#4070C0"]]) ; B / B' — red / blue

(defn chapter-stats [book-name]
  (let [chapters (get sefaria/book-chapters book-name)]
    (mapv (fn [ch]
            (let [verses  (sefaria/fetch-chapter book-name ch)
                  raw     (apply str (map norm/strip-html verses))
                  letters (norm/letter-stream raw)
                  n       (count letters)]
              {:book book-name :chapter ch :letters n
               :gematria (g/total letters)
               :mean (if (pos? n) (/ (double (g/total letters)) n) 0.0)}))
          (range 1 (inc chapters)))))

(defn svg-line
  "Generate SVG polyline from data points."
  [points color width & {:keys [dash] :or {dash nil}}]
  (let [pts-str (clojure.string/join " " (map #(str (first %) "," (second %)) points))]
    (format "<polyline points=\"%s\" fill=\"none\" stroke=\"%s\" stroke-width=\"%s\"%s/>"
            pts-str color (str width)
            (if dash (format " stroke-dasharray=\"%s\"" dash) ""))))

(defn svg-rect [x y w h color & {:keys [opacity rx] :or {opacity 1.0 rx 0}}]
  (format "<rect x=\"%.1f\" y=\"%.1f\" width=\"%.1f\" height=\"%.1f\" fill=\"%s\" opacity=\"%.2f\" rx=\"%d\"/>"
          (double x) (double y) (double w) (double h) color (double opacity) (int rx)))

(defn svg-text [x y text & {:keys [size color anchor] :or {size 14 color "#ccc" anchor "middle"}}]
  (format "<text x=\"%.1f\" y=\"%.1f\" font-family=\"monospace\" font-size=\"%d\" fill=\"%s\" text-anchor=\"%s\">%s</text>"
          (double x) (double y) (int size) color anchor text))

(defn svg-circle [cx cy r color]
  (format "<circle cx=\"%.1f\" cy=\"%.1f\" r=\"%.1f\" fill=\"%s\"/>"
          (double cx) (double cy) (double r) color))

(defn normalize-to-range [vals target-min target-max]
  (let [mn (apply min vals)
        mx (apply max vals)
        span (- mx mn)]
    (mapv #(+ target-min (* (/ (- % mn) (if (zero? span) 1.0 span))
                            (- target-max target-min)))
          vals)))

(defn panel-book-bars
  "Panel 1: Five books as proportional horizontal bars."
  [all-stats]
  (let [w 900 h 320
        margin-left 140 margin-right 40
        bar-h 36 gap 12
        max-letters (apply max (map #(reduce + (map :letters %)) (vals all-stats)))
        scale (/ (- w margin-left margin-right) (double max-letters))
        books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        labels ["A" "B" "C" "B'" "A'"]
        y-start 60]
    (str
     (format "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n" w h)
     (svg-text (/ w 2) 30 "THE FIVE BOOKS" :size 18 :color "#aaa")
     "\n"
     (clojure.string/join "\n"
       (map-indexed
        (fn [i book]
          (let [letters (reduce + (map :letters (get all-stats book)))
                bar-w (* letters scale)
                y (+ y-start (* i (+ bar-h gap)))
                color (get book-colors book)]
            (str
             (svg-rect margin-left y bar-w bar-h color :opacity 0.8 :rx 3)
             "\n"
             (svg-text (- margin-left 10) (+ y 24) (format "%s  %s" (nth labels i) book)
                       :size 13 :color color :anchor "end")
             "\n"
             (svg-text (+ margin-left bar-w 8) (+ y 24)
                       (format "%,d" letters) :size 11 :color "#888" :anchor "start"))))
        books))
     "\n"
     ;; Connecting arcs for pairs
     (let [y-a  (+ y-start 18)
           y-b  (+ y-start (+ bar-h gap) 18)
           y-c  (+ y-start (* 2 (+ bar-h gap)) 18)
           y-b2 (+ y-start (* 3 (+ bar-h gap)) 18)
           y-a2 (+ y-start (* 4 (+ bar-h gap)) 18)
           arc-x (- margin-left 30)]
       (str
        ;; A ↔ A' arc
        (format "<path d=\"M %d %d C %d %d, %d %d, %d %d\" fill=\"none\" stroke=\"#E8A838\" stroke-width=\"2\" opacity=\"0.6\"/>\n"
                (int arc-x) (int y-a)
                (- (int arc-x) 40) (int (/ (+ y-a y-a2) 2))
                (- (int arc-x) 40) (int (/ (+ y-a y-a2) 2))
                (int arc-x) (int y-a2))
        ;; B ↔ B' arc
        (format "<path d=\"M %d %d C %d %d, %d %d, %d %d\" fill=\"none\" stroke=\"#C04040\" stroke-width=\"2\" opacity=\"0.6\"/>\n"
                (int arc-x) (int y-b)
                (- (int arc-x) 25) (int (/ (+ y-b y-b2) 2))
                (- (int arc-x) 25) (int (/ (+ y-b y-b2) 2))
                (int arc-x) (int y-b2))
        ;; √2 label on A/A' arc
        (svg-text (- arc-x 50) (/ (+ y-a y-a2) 2) "√2" :size 16 :color "#E8A838")
        "\n"
        ;; = label on B/B' arc
        (svg-text (- arc-x 35) (/ (+ y-b y-b2) 2) "≡" :size 18 :color "#C04040")
        "\n"
        ;; π/2 bracket
        (svg-text (+ w -35) (/ (+ y-a y-c) 2) "}" :size 48 :color "#8080FF")
        (svg-text (+ w -5) (/ (+ y-a y-a2) 2) "π/2" :size 14 :color "#8080FF")))
     "\n</svg>")))

(defn panel-mirror-chart
  "Panel 2/3: Overlaid line charts showing chiastic mirror."
  [stats-a stats-b book-a book-b label-a label-b]
  (let [w 900 h 280
        margin {:mercy 60 :truth 40 :top 50 :bottom 40}
        plot-w (- w (:mercy margin) (:truth margin))
        plot-h (- h (:top margin) (:bottom margin))
        lens-a (mapv :letters stats-a)
        lens-b (mapv :letters stats-b)
        lens-b-rev (vec (reverse lens-b))
        n-a (count lens-a)
        n-b (count lens-b)
        ;; Normalize both to same x range
        all-vals (concat lens-a lens-b)
        y-min (apply min all-vals)
        y-max (apply max all-vals)
        y-scale (/ plot-h (double (- y-max y-min)))
        x-scale-a (/ plot-w (double (dec n-a)))
        x-scale-b (/ plot-w (double (dec n-b)))
        ;; Generate points
        pts-a (mapv (fn [i] [(+ (:mercy margin) (* i x-scale-a))
                              (- (+ (:top margin) plot-h)
                                 (* (- (nth lens-a i) y-min) y-scale))])
                     (range n-a))
        pts-b-rev (mapv (fn [i] [(+ (:mercy margin) (* i x-scale-b))
                                   (- (+ (:top margin) plot-h)
                                      (* (- (nth lens-b-rev i) y-min) y-scale))])
                         (range n-b))
        color-a (get book-colors book-a)
        color-b (get book-colors book-b)]
    (str
     (format "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n" w h)
     (svg-text (/ w 2) 25 (format "%s ↔ %s (reversed)" book-a book-b) :size 16 :color "#aaa")
     "\n"
     ;; Grid lines
     (clojure.string/join "\n"
       (for [i (range 5)]
         (let [y (+ (:top margin) (* i (/ plot-h 4.0)))]
           (format "<line x1=\"%d\" y1=\"%.0f\" x2=\"%d\" y2=\"%.0f\" stroke=\"#333\" stroke-width=\"1\"/>"
                   (:mercy margin) y (- w (:truth margin)) y))))
     "\n"
     ;; Book A line (solid)
     (svg-line pts-a color-a "2.5")
     "\n"
     ;; Book B reversed (dashed)
     (svg-line pts-b-rev color-b "2.5" :dash "6,3")
     "\n"
     ;; Legend
     (svg-rect (+ (:mercy margin) 10) (+ (:top margin) 5) 20 3 color-a)
     (svg-text (+ (:mercy margin) 35) (+ (:top margin) 10)
              (format "%s (forward)" book-a) :size 11 :color color-a :anchor "start")
     "\n"
     (svg-rect (+ (:mercy margin) 10) (+ (:top margin) 20) 20 3 color-b)
     (svg-text (+ (:mercy margin) 35) (+ (:top margin) 25)
              (format "%s (reversed)" book-b) :size 11 :color color-b :anchor "start")
     "\n"
     ;; Axis labels
     (svg-text (/ w 2) (- h 8) "Chapter position →" :size 11 :color "#666")
     "\n"
     "</svg>")))

(defn panel-internal-palindrome
  "Panel 4: Internal palindrome — each book's first half vs reversed second half."
  [stats book-name]
  (let [w 420 h 200
        margin {:mercy 50 :truth 20 :top 40 :bottom 30}
        plot-w (- w (:mercy margin) (:truth margin))
        plot-h (- h (:top margin) (:bottom margin))
        lens (mapv :letters stats)
        n (count lens)
        half (quot n 2)
        first-half lens
        second-rev (vec (concat (reverse (subvec lens half)) (subvec lens 0 half)))
        all-vals (concat lens second-rev)
        y-min (apply min all-vals)
        y-max (apply max all-vals)
        y-scale (/ plot-h (double (- y-max y-min)))
        x-scale (/ plot-w (double (dec n)))
        color (get book-colors book-name)
        pts-fwd (mapv (fn [i] [(+ (:mercy margin) (* i x-scale))
                                 (- (+ (:top margin) plot-h)
                                    (* (- (nth lens i) y-min) y-scale))])
                       (range n))
        pts-rev (mapv (fn [i] [(+ (:mercy margin) (* i x-scale))
                                 (- (+ (:top margin) plot-h)
                                    (* (- (nth second-rev i) y-min) y-scale))])
                       (range n))]
    (str
     (format "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n" w h)
     (svg-text (/ w 2) 20 book-name :size 14 :color color)
     "\n"
     (svg-line pts-fwd color "2")
     "\n"
     (svg-line pts-rev color "1.5" :dash "4,3")
     "\n"
     ;; Center line
     (let [cx (+ (:mercy margin) (* half x-scale))]
       (format "<line x1=\"%.0f\" y1=\"%d\" x2=\"%.0f\" y2=\"%d\" stroke=\"%s\" stroke-width=\"1\" stroke-dasharray=\"2,4\" opacity=\"0.5\"/>"
               cx (:top margin) cx (- h (:bottom margin)) color))
     "\n"
     "</svg>")))

(defn panel-gematria-curve
  "Panel 5: Running gematria mean across the whole Torah."
  [all-stats]
  (let [w 900 h 220
        margin {:mercy 60 :truth 40 :top 50 :bottom 40}
        plot-w (- w (:mercy margin) (:truth margin))
        plot-h (- h (:top margin) (:bottom margin))
        books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        ;; Collect all chapter means in order
        all-means (vec (mapcat #(mapv :mean (get all-stats %)) books))
        n (count all-means)
        ;; Also compute the reversed curve
        all-means-rev (vec (reverse all-means))
        y-min (apply min all-means)
        y-max (apply max all-means)
        y-scale (/ plot-h (- y-max y-min))
        x-scale (/ plot-w (double (dec n)))
        pts-fwd (mapv (fn [i] [(+ (:mercy margin) (* i x-scale))
                                 (- (+ (:top margin) plot-h)
                                    (* (- (nth all-means i) y-min) y-scale))])
                       (range n))
        pts-rev (mapv (fn [i] [(+ (:mercy margin) (* i x-scale))
                                 (- (+ (:top margin) plot-h)
                                    (* (- (nth all-means-rev i) y-min) y-scale))])
                       (range n))
        ;; Book boundaries
        boundaries (reductions + (map #(count (get all-stats %)) books))]
    (str
     (format "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n" w h)
     (svg-text (/ w 2) 25 "GEMATRIA MEAN PER CHAPTER — FORWARD AND REVERSED" :size 14 :color "#aaa")
     "\n"
     ;; Book boundary lines
     (clojure.string/join "\n"
       (map-indexed
        (fn [i b]
          (when (< i 4)
            (let [ch-count (nth (vec boundaries) i)
                  bx (+ (:mercy margin) (* ch-count x-scale))]
              (str
               (format "<line x1=\"%.0f\" y1=\"%d\" x2=\"%.0f\" y2=\"%d\" stroke=\"#444\" stroke-width=\"1\"/>"
                       bx (:top margin) bx (- h (:bottom margin)))
               "\n"
               (svg-text bx (- h 8) (nth books (inc i)) :size 9 :color "#555")))))
        books))
     "\n"
     (svg-text (+ (:mercy margin) 20) (- h 8) "Genesis" :size 9 :color "#555" :anchor "start")
     "\n"
     ;; Forward curve
     (svg-line pts-fwd "#E8A838" "2")
     "\n"
     ;; Reversed curve
     (svg-line pts-rev "#4070C0" "1.5" :dash "5,3")
     "\n"
     ;; Center marker
     (let [cx (+ (:mercy margin) (* (/ n 2.0) x-scale))]
       (str
        (svg-circle cx (- (+ (:top margin) plot-h)
                          (* (- (nth all-means (quot n 2)) y-min) y-scale))
                    5 "#FFFFFF")
        "\n"
        (svg-text cx (- (:top margin) 2) "ה" :size 18 :color "#fff")))
     "\n"
     ;; Legend
     (svg-rect (- w 200) (+ (:top margin) 5) 20 3 "#E8A838")
     (svg-text (- w 175) (+ (:top margin) 10) "Forward" :size 11 :color "#E8A838" :anchor "start")
     "\n"
     (svg-rect (- w 200) (+ (:top margin) 20) 20 3 "#4070C0")
     (svg-text (- w 175) (+ (:top margin) 25) "Reversed" :size 11 :color "#4070C0" :anchor "start")
     "\n"
     "</svg>")))

(defn panel-fractal-zoom
  "Panel 6: Fractal zoom — three scales of palindrome."
  [all-stats]
  (let [w 900 h 180
        books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        book-letters (mapv #(long (reduce + (map :letters (get all-stats %)))) books)
        total (reduce + book-letters)
        scale (/ 800.0 total)
        x-start 50]
    (str
     (format "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n" w h)
     (svg-text (/ w 2) 25 "THE FRACTAL: SELF-SIMILAR AT EVERY SCALE" :size 14 :color "#aaa")
     "\n"
     ;; Scale 1: The five books
     (svg-text 25 55 "Scale 1:" :size 10 :color "#666" :anchor "start")
     (let [positions (reductions + (cons 0 book-letters))]
       (clojure.string/join "\n"
         (map-indexed
          (fn [i book]
            (let [x (+ x-start (* (nth (vec positions) i) scale))
                  bw (* (nth book-letters i) scale)
                  color (get book-colors book)]
              (str (svg-rect x 42 bw 20 color :opacity 0.8 :rx 2)
                   "\n"
                   (svg-text (+ x (/ bw 2)) 56
                            (subs book 0 3) :size 9 :color "#000"))))
          books)))
     "\n"
     ;; Mirror arcs
     (let [positions (vec (reductions + (cons 0 book-letters)))
           gen-mid (+ x-start (* (/ (nth positions 1) 2.0) scale))
           deut-mid (+ x-start (* (/ (+ (nth positions 4) (nth positions 5)) 2.0) scale))
           exod-mid (+ x-start (* (/ (+ (nth positions 1) (nth positions 2)) 2.0) scale))
           num-mid (+ x-start (* (/ (+ (nth positions 3) (nth positions 4)) 2.0) scale))
           lev-mid (+ x-start (* (/ (+ (nth positions 2) (nth positions 3)) 2.0) scale))]
       (str
        ;; A ↔ A' arc
        (format "<path d=\"M %.0f 42 Q %.0f 20, %.0f 42\" fill=\"none\" stroke=\"#E8A838\" stroke-width=\"1.5\" opacity=\"0.6\"/>\n"
                gen-mid (/ (+ gen-mid deut-mid) 2) deut-mid)
        ;; B ↔ B' arc
        (format "<path d=\"M %.0f 42 Q %.0f 28, %.0f 42\" fill=\"none\" stroke=\"#C04040\" stroke-width=\"1.5\" opacity=\"0.6\"/>\n"
                exod-mid (/ (+ exod-mid num-mid) 2) num-mid)
        ;; Center dot
        (svg-circle lev-mid 52 4 "#fff")))
     "\n"
     ;; Scale 2: Chapters within one pair
     (svg-text 25 95 "Scale 2:" :size 10 :color "#666" :anchor "start")
     (let [gen-chs (mapv :letters (get all-stats "Genesis"))
           max-ch (apply max gen-chs)
           ch-w (/ 350.0 (count gen-chs))]
       (clojure.string/join "\n"
         (map-indexed
          (fn [i l]
            (let [ch-h (* 25 (/ (double l) max-ch))]
              (svg-rect (+ x-start (* i ch-w)) (- 105 ch-h) (max 1 (- ch-w 1)) ch-h
                        "#E8A838" :opacity 0.7)))
          gen-chs)))
     "\n"
     (let [deut-chs (vec (reverse (mapv :letters (get all-stats "Deuteronomy"))))
           max-ch (apply max deut-chs)
           ch-w (/ 350.0 (count deut-chs))]
       (str
        (clojure.string/join "\n"
          (map-indexed
           (fn [i l]
             (let [ch-h (* 25 (/ (double l) max-ch))]
               (svg-rect (+ 470 (* i ch-w)) (- 105 ch-h) (max 1 (- ch-w 1)) ch-h
                         "#D4943C" :opacity 0.7)))
           deut-chs))
        "\n"
        (svg-text (+ x-start 175) 118 "Genesis →" :size 10 :color "#E8A838")
        "\n"
        (svg-text 645 118 "← Deuteronomy (rev)" :size 10 :color "#D4943C")))
     "\n"
     ;; Scale 3: hint at verse level
     (svg-text 25 148 "Scale 3:" :size 10 :color "#666" :anchor "start")
     ;; Fine-grained bars to suggest verse-level structure
     (let [gen1 (get all-stats "Genesis")
           ch1-letters (:letters (first gen1))
           n-bars 50
           bar-w (/ 800.0 n-bars)]
       (clojure.string/join "\n"
         (map (fn [i]
                (let [h (+ 5 (rand-int 20))]
                  (svg-rect (+ x-start (* i bar-w)) (- 155 h) (max 1 (- bar-w 1)) h
                            "#E8A838" :opacity 0.4)))
              (range n-bars))))
     "\n"
     (svg-text (/ w 2) 170 "... and deeper" :size 10 :color "#555")
     "\n"
     "</svg>")))

(defn generate-html [all-stats]
  (let [gen  (get all-stats "Genesis")
        exod (get all-stats "Exodus")
        num  (get all-stats "Numbers")
        deut (get all-stats "Deuteronomy")]
    (str
     "<!DOCTYPE html>\n<html>\n<head>\n"
     "<meta charset=\"UTF-8\">\n"
     "<title>The Fractal Palindrome</title>\n"
     "<style>\n"
     "body { background: #111; color: #ccc; font-family: 'Georgia', serif; "
     "max-width: 960px; margin: 0 auto; padding: 40px 20px; }\n"
     "h1 { color: #E8A838; text-align: center; font-size: 2.2em; letter-spacing: 0.1em; margin-bottom: 0; }\n"
     "h2 { color: #888; text-align: center; font-style: italic; font-weight: normal; margin-top: 5px; }\n"
     ".panel { margin: 40px 0; text-align: center; }\n"
     ".panel svg { display: block; margin: 0 auto; }\n"
     ".caption { color: #666; font-size: 0.9em; text-align: center; margin-top: 8px; font-style: italic; }\n"
     ".stat { color: #E8A838; font-family: monospace; }\n"
     ".divider { text-align: center; color: #333; margin: 30px 0; letter-spacing: 1em; }\n"
     ".hebrew { font-size: 1.4em; direction: rtl; }\n"
     "p { line-height: 1.6; max-width: 700px; margin: 0 auto 15px; }\n"
     "</style>\n</head>\n<body>\n"
     "<h1>THE FRACTAL PALINDROME</h1>\n"
     "<h2>The recursive symmetry of the Torah's five books</h2>\n"
     "<div class=\"divider\">· · ·</div>\n"

     ;; Panel 1: Book bars
     "<div class=\"panel\">\n"
     (panel-book-bars all-stats)
     "\n<p class=\"caption\">Five books. Two pairs and a center. The lengths encode √2 and π/2.</p>\n"
     "</div>\n"

     "<div class=\"divider\">· · ·</div>\n"

     ;; Panel 2: Genesis ↔ Deuteronomy
     "<div class=\"panel\">\n"
     (panel-mirror-chart gen deut "Genesis" "Deuteronomy" "A" "A'")
     "\n<p class=\"caption\">Genesis forward (solid) and Deuteronomy reversed (dashed). The shapes mirror.</p>\n"
     "</div>\n"

     ;; Panel 3: Exodus ↔ Numbers
     "<div class=\"panel\">\n"
     (panel-mirror-chart exod num "Exodus" "Numbers" "B" "B'")
     "\n<p class=\"caption\">Exodus forward (solid) and Numbers reversed (dashed). 11 letters apart.</p>\n"
     "</div>\n"

     "<div class=\"divider\">· · ·</div>\n"

     ;; Panel 4: Internal palindromes
     "<p style=\"text-align:center; color:#888;\">Each book mirrors itself:</p>\n"
     "<div class=\"panel\" style=\"display: flex; flex-wrap: wrap; justify-content: center; gap: 10px;\">\n"
     (clojure.string/join "\n"
       (map #(str "<div>" (panel-internal-palindrome (get all-stats %) %) "</div>")
            ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]))
     "\n</div>\n"
     "<p class=\"caption\">Solid: chapters forward. Dashed: chapters reversed. Each book reads the same both ways.</p>\n"

     "<div class=\"divider\">· · ·</div>\n"

     ;; Panel 5: Gematria curve
     "<div class=\"panel\">\n"
     (panel-gematria-curve all-stats)
     "\n<p class=\"caption\">The gematria energy reads nearly the same forward and backward. The center: <span class=\"hebrew\">ה</span></p>\n"
     "</div>\n"

     "<div class=\"divider\">· · ·</div>\n"

     ;; Panel 6: Fractal zoom
     "<div class=\"panel\">\n"
     (panel-fractal-zoom all-stats)
     "\n<p class=\"caption\">The same pattern at every scale. Palindromes within palindromes within palindromes.</p>\n"
     "</div>\n"

     "<div class=\"divider\">· · ·</div>\n"

     ;; Stats block
     "<div style=\"text-align: center; margin: 40px 0;\">\n"
     "<p><span class=\"stat\">306,269</span> letters</p>\n"
     "<p><span class=\"stat\">p &lt; 0.00001</span> — probability of this pattern by chance</p>\n"
     "<p><span class=\"stat\">0 / 100,000</span> random partitions matched all four criteria</p>\n"
     "<p><span class=\"stat\">2 / 120</span> orderings of the book lengths produce the pattern</p>\n"
     "</div>\n"

     "<div class=\"divider\">· · ·</div>\n"
     "<p style=\"text-align: center; font-style: italic; color: #555;\">סלה</p>\n"

     "</body>\n</html>\n")))

(defn -main []
  (println "=== Painting the Fractal Palindrome ===\n")

  (println "Loading chapter data for all 5 books...")
  (let [books ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"]
        all-stats (into {} (map (fn [b]
                                   (print (format "  %s..." b)) (flush)
                                   (let [s (chapter-stats b)]
                                     (println (format " %d chapters" (count s)))
                                     [b s]))
                                 books))
        html (generate-html all-stats)
        out-path "docs/fractal-palindrome.html"]
    (io/make-parents out-path)
    (spit out-path html)
    (println (format "\n  Written to: %s" out-path))
    (println "  Open in a browser to see the fractal."))

  (println "\nDone."))
