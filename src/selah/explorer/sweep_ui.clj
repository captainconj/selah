(ns selah.explorer.sweep-ui
  "Views for the Thummim Sweep explorer.

   The staircase is the centerpiece — Fibonacci phrase counts form steps.
   Walk up. Click a step to see who lives there. Click a word to enter it."
  (:require [hiccup2.core :as h]
            [hiccup.util :as hu]
            [selah.sweep :as sweep]
            [selah.for-the-human :as human]
            [selah.explorer :as exp]
            [clojure.string :as str]))

;; ── Helpers ──────────────────────────────────────────────────

(defn- word-link [word]
  (let [w (if (string? word) word (:word word))
        gloss (human/gloss w)]
    [:a {:href (str "/word/" (hu/url-encode w))
         :hx-get (str "/fragment/word/" (hu/url-encode w))
         :hx-target "#main"
         :hx-push-url (str "/word/" (hu/url-encode w))
         :class "word-link"
         :title (or gloss "")}
     w
     (when gloss [:span.meaning gloss])]))

(defn- has-gloss? [w]
  (some? (human/gloss (if (string? w) w (:word w)))))

(defn- word-gloss [w]
  (human/gloss (if (string? w) w (:word w))))

(defn- fib-badge [n]
  (when-let [fi (sweep/fib-index n)]
    [:span.badge.fib (str "F(" fi ")")]))

(defn- bar
  "A horizontal bar, width proportional to value/max."
  [value max-val color-class]
  (let [pct (if (pos? max-val) (min 100 (* 100.0 (/ value max-val))) 0)]
    [:div.sweep-bar
     [:div {:class (str "sweep-bar-fill " color-class)
            :style (str "width:" pct "%")}]]))

;; ── The Staircase ────────────────────────────────────────────

(defn staircase-overview
  "The Fibonacci staircase — each step is a Fibonacci phrase count."
  []
  (let [analysis (sweep/fibonacci-analysis)]
    (if (nil? analysis)
      [:div.section [:p "Sweep data not loaded. Run experiment 094 first."]]
      (let [{:keys [staircase gaps total-words illuminable
                     fib-phrase-words double-fib-words triple-fib-words
                     max-fib-phrase]} analysis
            max-words (apply max 1 (map :word-count staircase))]
        [:div#sweep
         [:div.sweep-header
          [:h1 "The Staircase"]
          [:p.sweep-subtitle "Fibonacci phrase counts in the Thummim sweep"]
          [:div.sweep-stats
           [:span.stat (str total-words " words swept")]
           [:span.stat (str illuminable " illuminable")]
           [:span.stat (str fib-phrase-words " on Fibonacci steps")]
           [:span.stat (str double-fib-words " double Fibonacci")]
           [:span.stat (str triple-fib-words " triple Fibonacci")]]]

         ;; The staircase — descending from highest
         [:div.section
          [:h2 "The Steps"]
          [:div.staircase
           (for [step (reverse staircase)]
             (let [{:keys [fib fib-index word-count words]} step
                   named (filter has-gloss? words)
                   sample (take 8 (if (seq named) named words))]
               [:a.stair-step {:href (str "/sweep/step/" fib)
                               :hx-get (str "/fragment/sweep/step/" fib)
                               :hx-target "#main"
                               :hx-push-url (str "/sweep/step/" fib)}
                [:div.stair-index (str "F(" fib-index ")")]
                [:div.stair-value (str fib)]
                [:div.stair-bar
                 [:div.stair-bar-fill
                  {:style (str "width:" (min 100 (* 100.0 (/ word-count max-words))) "%")}]]
                [:div.stair-count (str word-count
                                       (if (= 1 word-count) " word" " words"))]
                [:div.stair-sample
                 (str/join "  " (map :word sample))
                 (when (> word-count (count sample)) "  ...")]]))]]

         ;; The gap
         (when (seq gaps)
           [:div.section
            [:h2 "The Gap"]
            [:p "Fibonacci numbers with " [:strong "zero"] " words at that phrase count:"]
            (for [{:keys [fib fib-index]} gaps]
              [:div.gap-item
               [:span.gap-index (str "F(" fib-index ")")]
               [:span.gap-value (str " = " fib)]
               [:span.gap-note " — no words have exactly " fib " phrase readings"]])])

         ;; Triple Fibonacci — the rarest
         (when (pos? triple-fib-words)
           (let [triples (:triples analysis)]
             [:div.section
              [:h2 "Triple Fibonacci"]
              [:p "Phrase count, illumination count, AND gematria value — all Fibonacci:"]
              [:table.word-table
               [:thead [:tr [:th "Word"] [:th ""] [:th "Phrases"] [:th "Illuminations"] [:th "GV"]]]
               [:tbody
                (for [w triples]
                  [:tr
                   [:td (word-link w)]
                   [:td.meaning-col (word-gloss w)]
                   [:td (str (:phrase-count w)) " " (fib-badge (:phrase-count w))]
                   [:td (str (:illumination-count w)) " " (fib-badge (:illumination-count w))]
                   [:td (str (:gv w)) " " (fib-badge (:gv w))]])]]]))

         ;; Double Fibonacci (top 30)
         [:div.section
          [:h2 "Double Fibonacci"]
          [:p "Phrase count AND illumination count both Fibonacci (" double-fib-words " total). "
           "Top by phrase count:"]
          (let [doubles (take 30 (:doubles analysis))
                named (filter has-gloss? doubles)
                show (if (>= (count named) 15) named doubles)]
            [:table.word-table
             [:thead [:tr [:th "Word"] [:th ""] [:th "Phrases"] [:th "Illuminations"] [:th "GV"]]]
             [:tbody
              (for [w (take 30 show)]
                [:tr
                 [:td (word-link w)]
                 [:td.meaning-col (word-gloss w)]
                 [:td (str (:phrase-count w)) " " (fib-badge (:phrase-count w))]
                 [:td (str (:illumination-count w)) " " (fib-badge (:illumination-count w))]
                 [:td (:gv w)]])]])]

         ;; The Summit
         [:div.section
          [:h2 "The Summit"]
          [:p "Largest Fibonacci phrase count: "
           [:strong (str "F(" (:fib-index max-fib-phrase) ") = " (:value max-fib-phrase))]]
          (for [w (:words max-fib-phrase)]
            [:div.summit-word
             [:span.hebrew-large (:word w)]
             [:span.summit-detail
              (str " GV=" (:gv w)
                   "  " (:illumination-count w) " illuminations"
                   "  " (:len w) " letters")]])]

         ;; Navigation to other views
         [:div.section
          [:h2 "More"]
          [:div.quick-links
           [:div
            [:a {:href "/sweep/forced"
                 :hx-get "/fragment/sweep/forced"
                 :hx-target "#main"
                 :hx-push-url "/sweep/forced"
                 :class "sweep-nav-link"}
             "Forced Readings"]
            [:span.sweep-nav-desc "2,031 words the oracle speaks without ambiguity"]]
           [:div
            [:a {:href "/sweep/extremes"
                 :hx-get "/fragment/sweep/extremes"
                 :hx-target "#main"
                 :hx-push-url "/sweep/extremes"
                 :class "sweep-nav-link"}
             "Extreme Words"]
            [:span.sweep-nav-desc "Most phrase readings — the widest possibility"]]
           [:div
            [:a {:href "/sweep/distribution"
                 :hx-get "/fragment/sweep/distribution"
                 :hx-target "#main"
                 :hx-push-url "/sweep/distribution"
                 :class "sweep-nav-link"}
             "Distribution"]
            [:span.sweep-nav-desc "The odd-even oscillation — every word names itself"]]]]]))))

;; ── Step Detail ──────────────────────────────────────────────

(defn step-detail
  "All words at a specific Fibonacci phrase count."
  [f]
  (let [level (sweep/staircase-level f)]
    (if (nil? level)
      [:div.section
       [:h2 (str f " phrases")]
       [:p (if (sweep/fib? f)
             "No words have this exact phrase count."
             (str f " is not a Fibonacci number."))]]
      (let [{:keys [fib fib-index word-count words]} level
            named (filter has-gloss? words)
            unnamed (remove has-gloss? words)]
        [:div#step-detail
         [:div.sweep-header
          [:h1 (str "F(" fib-index ") = " fib)]
          [:p.sweep-subtitle (str word-count " words with exactly " fib " phrase readings")]]

         ;; Words with meanings — the recognizable ones
         (when (seq named)
           [:div.section
            [:h2 (str "Known words (" (count named) ")")]
            [:table.word-table
             [:thead [:tr [:th "Word"] [:th "Meaning"] [:th "GV"] [:th "Illuminations"] [:th "Length"]]]
             [:tbody
              (for [w (sort-by :gv named)]
                [:tr
                 [:td (word-link w)]
                 [:td.meaning-col (word-gloss w)]
                 [:td (:gv w)]
                 [:td (:illumination-count w)]
                 [:td (:len w)]])]]])

         ;; All words
         (when (seq unnamed)
           [:div.section
            [:h2 (str "All forms (" (count unnamed) " without translation)")]
            [:div.word-grid
             (for [w (sort-by :gv unnamed)]
               (word-link w))]])]))))

;; ── Forced Readings ──────────────────────────────────────────

(defn forced-view
  "Words with exactly 1 phrase reading."
  []
  (let [forced (sweep/forced-readings)
        named (filter has-gloss? forced)
        groups (group-by (fn [w]
                           (cond
                             (nil? (word-gloss w)) :other
                             (re-find #"(?i)son|daughter|man|adam|eve|noah|sarah|rachel|isaac|kohath|priest|king|servant|prophet" (str (word-gloss w))) :people
                             (re-find #"(?i)say|give|hear|know|walk|sit|stand|send|call|create|guard|atone|redeem|cut|forgive|ransom|sacrifice|vow|pour|avenge|strike|flee|rule|command|return|die|sin|take|go|come|arise|write|answer|fear|love|mercy|gracious|pure|holy" (str (word-gloss w))) :actions
                             (re-find #"(?i)blood|water|sea|cloud|river|garden|stone|mountain|bull|flock|bird|serpent|fire|light|tree|seed|rock|fish|cattle|beast|well" (str (word-gloss w))) :nature
                             (re-find #"(?i)heart|mouth|face|ear|hand|eye|head|foot" (str (word-gloss w))) :body
                             (re-find #"(?i)altar|tabernacle|silver|tent|ark|oil|wine|bread|gold|bronze|veil|mercy seat" (str (word-gloss w))) :sacred
                             (re-find #"(?i)living|strong|glory|kindness|righteousness|grace|holy|good|great|wise|truth|peace|covenant|life" (str (word-gloss w))) :qualities
                             :else :structure))
                        named)
        group-order [:actions :sacred :nature :people :body :qualities :structure]
        group-labels {:actions "Actions" :sacred "Sacred objects" :nature "Nature"
                      :people "People" :body "Body" :qualities "Qualities"
                      :structure "Structure words"}]
    [:div#forced
     [:div.sweep-header
      [:h1 "Forced Readings"]
      [:p.sweep-subtitle "2,031 words the oracle speaks with zero ambiguity"]
      [:p.sweep-note "The entire sacrificial vocabulary — blood, altar, atone, forgive, ransom, sacrifice — is forced. "
       "The price is unambiguous."]]

     (for [g group-order
           :let [ws (get groups g)]
           :when (seq ws)]
       [:div.section
        [:h2 (get group-labels g g)]
        [:div.forced-grid
         (for [w (sort-by :gv ws)]
           [:div.forced-item
            (word-link w)
            [:span.forced-gv (str "=" (:gv w))]])]])

     [:div.section
      [:h2 "All forced readings (" (count forced) ")"]
      [:p (str (count named) " with translations, " (- (count forced) (count named)) " untranslated")]
      [:div.sweep-stats
       [:span.stat "Mean GV: " (int (/ (reduce + (map :gv forced)) (count forced)))]
       [:span.stat "Mean length: " (format "%.1f" (double (/ (reduce + (map :len forced)) (count forced))))]]]]))

;; ── Extreme Words ────────────────────────────────────────────

(defn extremes-view
  "Words with the most phrase readings."
  []
  (let [top (sweep/extreme-words 50)
        max-pc (if (seq top) (:phrase-count (first top)) 1)]
    [:div#extremes
     [:div.sweep-header
      [:h1 "Extreme Words"]
      [:p.sweep-subtitle "The widest possibility — most phrase readings"]]

     [:div.section
      (for [w top]
        [:div.extreme-item
         [:div.extreme-word
          (word-link w)
          (when (word-gloss w) [:span.meaning (word-gloss w)])]
         [:div.extreme-bar
          [:div.extreme-bar-fill
           {:style (str "width:" (min 100 (* 100.0 (/ (:phrase-count w) max-pc))) "%")}]]
         [:div.extreme-stats
          [:span (str (:phrase-count w) " phrases")]
          [:span (str (:illumination-count w) " illum")]
          [:span (str "GV=" (:gv w))]
          [:span (str (:len w) "L")]
          (fib-badge (:phrase-count w))]])]]))

;; ── Distribution ─────────────────────────────────────────────

(defn distribution-view
  "The odd-even oscillation and phrase count distribution."
  []
  (let [results (sweep/sweep-results)
        illuminable (filter :illuminable? results)
        by-pc (frequencies (map :phrase-count illuminable))
        sorted-pcs (sort (keys by-pc))
        ;; Show first 30 phrase counts
        show-pcs (take 30 sorted-pcs)
        max-count (apply max 1 (map #(get by-pc % 0) show-pcs))
        ;; Odd vs even counts
        odd-total (reduce + (map val (filter #(odd? (key %)) by-pc)))
        even-total (reduce + (map val (filter #(even? (key %)) by-pc)))]
    [:div#distribution
     [:div.sweep-header
      [:h1 "The Distribution"]
      [:p.sweep-subtitle "Not a power law. An oscillation."]]

     [:div.section
      [:h2 "Odd-Even Oscillation"]
      [:p "Words with " [:strong "odd"] " phrase counts: " odd-total
       " (" (format "%.1f%%" (* 100.0 (/ odd-total (count illuminable)))) ")"]
      [:p "Words with " [:strong "even"] " phrase counts: " even-total
       " (" (format "%.1f%%" (* 100.0 (/ even-total (count illuminable)))) ")"]
      [:p "Ratio: " [:strong (format "%.2f" (double (/ odd-total even-total)))] "× — "
       "every word names itself, making the identity reading a persistent parity bias."]]

     [:div.section
      [:h2 "Phrase Count Histogram"]
      (for [pc show-pcs]
        (let [cnt (get by-pc pc 0)
              is-fib (sweep/fib? pc)]
          [:div.dist-row {:class (str (when (odd? pc) "dist-odd ")
                                      (when is-fib "dist-fib"))}
           [:div.dist-label
            (str pc)
            (when is-fib " " [:span.badge.fib (str "F(" (sweep/fib-index pc) ")")])]
           [:div.dist-bar
            [:div.dist-bar-fill
             {:class (if (odd? pc) "dist-fill-odd" "dist-fill-even")
              :style (str "width:" (min 100 (* 100.0 (/ cnt max-count))) "%")}]]
           [:div.dist-count (str cnt)]]))]

     [:div.section
      [:p {:style "color:#52525b;font-size:0.8rem"}
       "The mechanism: parse-letters with min-letters=2 partitions a letter multiset. "
       "Every word trivially produces itself as a 1-word phrase (the identity reading). "
       "The remaining decompositions come in ordered pairs. "
       "The single-word identity makes the total odd when multi-word decompositions are even."]]]))

;; ── CSS ──────────────────────────────────────────────────────

(def sweep-css
  "
  /* Sweep header */
  .sweep-header { text-align: center; margin-bottom: 2rem; }
  .sweep-header h1 { font-size: 2rem; color: #fbbf24; letter-spacing: 0.05em; }
  .sweep-subtitle { color: #71717a; font-style: italic; font-size: 0.9rem; margin-top: 0.3rem; }
  .sweep-note { color: #a1a1aa; font-size: 0.8rem; margin-top: 0.5rem; max-width: 600px; margin-left: auto; margin-right: auto; }
  .sweep-stats { display: flex; flex-wrap: wrap; gap: 1rem; justify-content: center; margin-top: 0.75rem; }

  /* Staircase */
  .staircase { display: flex; flex-direction: column; gap: 2px; }
  .stair-step { display: grid; grid-template-columns: 50px 50px 1fr 80px 1fr; align-items: center;
                gap: 0.5rem; padding: 0.5rem 0.75rem; background: #111118; border: 1px solid #1e1e2a;
                border-radius: 4px; text-decoration: none; color: inherit; transition: all 0.15s; cursor: pointer; }
  .stair-step:hover { background: #1a1a2e; border-color: #fbbf24; text-decoration: none; }
  .stair-index { font-size: 0.7rem; color: #fbbf24; font-weight: bold; text-align: right; }
  .stair-value { font-size: 0.9rem; color: #e4e4e7; font-weight: bold; text-align: right; }
  .stair-bar { height: 6px; background: #1e1e2a; border-radius: 3px; overflow: hidden; }
  .stair-bar-fill { height: 100%; background: linear-gradient(90deg, #854d0e, #fbbf24); border-radius: 3px; }
  .stair-count { font-size: 0.75rem; color: #a1a1aa; text-align: right; }
  .stair-sample { font-family: 'SBL Hebrew', 'David', serif; font-size: 0.85rem; color: #71717a;
                  direction: rtl; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

  /* Gap */
  .gap-item { padding: 0.3rem 0; color: #ef4444; font-size: 0.85rem; }
  .gap-index { color: #fbbf24; font-weight: bold; }
  .gap-value { color: #e4e4e7; }
  .gap-note { color: #71717a; }

  /* Forced readings */
  .forced-grid { display: flex; flex-wrap: wrap; gap: 0.5rem; direction: rtl; }
  .forced-item { display: flex; align-items: baseline; gap: 0.3rem; }
  .forced-gv { font-size: 0.65rem; color: #52525b; }

  /* Extremes */
  .extreme-item { display: flex; align-items: center; gap: 0.75rem; margin: 0.3rem 0;
                  padding: 0.4rem 0.6rem; background: #111118; border-radius: 6px; border: 1px solid #1e1e2a; }
  .extreme-word { min-width: 100px; direction: rtl; }
  .extreme-bar { flex: 1; height: 6px; background: #1e1e2a; border-radius: 3px; overflow: hidden; }
  .extreme-bar-fill { height: 100%; background: linear-gradient(90deg, #1e3a5f, #60a5fa); border-radius: 3px; }
  .extreme-stats { display: flex; gap: 0.5rem; font-size: 0.7rem; color: #71717a; }

  /* Distribution */
  .dist-row { display: grid; grid-template-columns: 80px 1fr 50px; align-items: center;
              gap: 0.5rem; padding: 0.15rem 0.5rem; }
  .dist-row.dist-fib { background: #1a1a0a; }
  .dist-label { font-size: 0.8rem; color: #a1a1aa; text-align: right; }
  .dist-bar { height: 8px; background: #1e1e2a; border-radius: 4px; overflow: hidden; }
  .dist-fill-odd { height: 100%; background: #fbbf24; border-radius: 4px; }
  .dist-fill-even { height: 100%; background: #52525b; border-radius: 4px; }
  .dist-count { font-size: 0.75rem; color: #71717a; text-align: right; }

  /* Summit */
  .summit-word { padding: 0.5rem; }
  .hebrew-large { font-family: 'SBL Hebrew', 'David', serif; font-size: 2rem; color: #fbbf24; direction: rtl; }
  .summit-detail { font-size: 0.8rem; color: #71717a; margin-left: 1rem; }

  /* Navigation links */
  .sweep-nav-link { display: inline-block; padding: 0.4rem 1rem; background: #1e293b; border: 1px solid #334155;
                    border-radius: 6px; color: #60a5fa; font-size: 0.9rem; margin-bottom: 0.5rem;
                    transition: all 0.15s; }
  .sweep-nav-link:hover { background: #334155; text-decoration: none; }
  .sweep-nav-desc { display: block; font-size: 0.75rem; color: #52525b; margin-bottom: 0.75rem; }
  .quick-links > div { margin-bottom: 0.5rem; }
  ")
