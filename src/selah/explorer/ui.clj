(ns selah.explorer.ui
  "Hiccup views for the Torah Explorer.

   Server-rendered HTML with HTMX for navigation.
   Click a word → see its profile. Click a connection → follow it.
   The structure reveals itself as you move through it."
  (:require [hiccup2.core :as h]
            [hiccup.util :as hu]
            [selah.explorer :as exp]
            [selah.for-the-human :as human]
            [selah.oracle :as oracle]
            [selah.translate :as translate]
            [selah.explorer.sweep-ui :as sweep-ui]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.json :as json]))

;; ── Helpers ──────────────────────────────────────────────────

(defn props-badges
  "Render number property badges."
  [props]
  (when (seq props)
    [:span.badges
     (when-let [f (:fibonacci props)]
       [:span.badge.fib (str "F(" f ")")])
     (when-let [t (:triangular props)]
       [:span.badge.tri (str "T(" t ")")])
     (when-let [s (:square props)]
       [:span.badge.sq (str s "²")])
     (when (:prime props)
       [:span.badge.prime "prime"])
     (when-let [d (:div-7 props)]
       [:span.badge.axis (str "÷7=" d)])
     (when-let [d (:div-13 props)]
       [:span.badge.axis (str "÷13=" d)])
     (when-let [d (:div-50 props)]
       [:span.badge.axis (str "÷50=" d)])
     (when-let [d (:div-67 props)]
       [:span.badge.axis (str "÷67=" d)])
     (when-let [d (:div-26 props)]
       [:span.badge.axis (str "÷26=" d)])]))

(defn word-link
  "Clickable word that navigates via HTMX. Shows translation on hover and inline."
  ([word] (word-link word nil))
  ([word extra-class]
   (let [meaning (human/gloss word)]
     [:a {:href (str "/word/" (hu/url-encode word))
          :hx-get (str "/fragment/word/" (hu/url-encode word))
          :hx-target "#main"
          :hx-push-url (str "/word/" (hu/url-encode word))
          :class (str "word-link" (when extra-class (str " " extra-class)))
          :title (or (human/title-text word) "")}
      word
      (when meaning [:span.meaning meaning])])))

(defn num-link
  "Clickable number that navigates to a level or GV view."
  [n route label]
  [:a {:href (str "/" route "/" n)
       :hx-get (str "/fragment/" route "/" n)
       :hx-target "#main"
       :hx-push-url (str "/" route "/" n)
       :class "num-link"}
   (str label)])

;; ── Word Profile View ────────────────────────────────────────

(defn word-profile [word-data]
  (let [{:keys [word freq gv preimage len freq-rank
                preimage-props gv-props
                named-by counts-to same-level same-gv]} word-data
        meaning (human/gloss word)]
    [:div#word-profile
     [:div.word-header
      [:h1.hebrew word]
      (when meaning
        [:p.translation meaning])
      [:div.word-meta
       [:span.stat "Freq: " [:strong freq] " (#" freq-rank ")"]
       [:span.stat "GV: " (num-link gv "gv" gv) " " (props-badges gv-props)]
       [:span.stat "Preimage: " (if (pos? (or preimage 0))
                                  (list (num-link preimage "level" preimage)
                                        " " (props-badges preimage-props))
                                  [:em "—"])]
       [:span.stat "Length: " len]]]

     ;; Cross-references: the machine NAMES what it counts
     (when (seq named-by)
       [:div.section
        [:h2 "The machine names what it counts"]
        [:p.explain "preimage(" word ") = " preimage " = GV of these words:"]
        [:div.word-grid
         (for [w named-by]
           (word-link w))]])

     ;; Reverse cross-ref: words whose preimage = my GV
     (when (seq counts-to)
       [:div.section
        [:h2 "Words counted by my value"]
        [:p.explain "These words have preimage count = " gv " = GV(" word "):"]
        [:div.word-grid
         (for [w counts-to]
           (word-link w))]])

     ;; Same preimage level
     (when (seq same-level)
       [:div.section
        [:h2 "Same preimage level (" preimage ")"]
        [:div.word-grid
         (for [{w :word f :freq g :gv} same-level]
           [:span (word-link w) [:small " " g]])]])

     ;; Same GV
     (when (seq same-gv)
       [:div.section
        [:h2 "Same gematria (" gv ")"]
        [:div.word-grid
         (for [{w :word f :freq p :preimage} same-gv]
           [:span (word-link w) [:small " pre=" (or p "—")]])]])

     ;; Factors
     (when (and preimage (> preimage 1))
       (let [factors (exp/factorize preimage)]
         (when (> (count factors) 1)
           [:div.section
            [:h2 "Factorization"]
            [:p preimage " = " (str/join " × " factors)]])))]))

;; ── Level View ───────────────────────────────────────────────

(defn level-view [n words]
  (let [props (exp/number-properties n)
        gv-sum (reduce + (map :gv words))]
    [:div#level-view
     [:h1 "Preimage Level " n " " (props-badges props)]
     [:p (count words) " words. GV sum = " gv-sum
      " " (props-badges (exp/number-properties gv-sum))]
     ;; Cross-reference: what words have GV = n?
     (let [gv-matches (exp/gv-words n)]
       (when (seq gv-matches)
         [:div.section
          [:h2 "GV = " n " (the machine names this level)"]
          [:div.word-grid
           (for [{w :word} gv-matches]
             (word-link w))]]))
     [:div.section
      [:h2 "Words at this level"]
      [:table.word-table
       [:thead [:tr [:th "Word"] [:th ""] [:th "Freq"] [:th "GV"] [:th "Len"]]]
       [:tbody
        (for [{:keys [word freq gv len]} words]
          [:tr
           [:td (word-link word)]
           [:td.meaning-col (human/gloss word)]
           [:td freq]
           [:td (num-link gv "gv" gv)]
           [:td len]])]]]]))

;; ── GV View ──────────────────────────────────────────────────

(defn gv-view [n words]
  (let [props (exp/number-properties n)]
    [:div#gv-view
     [:h1 "Gematria " n " " (props-badges props)]
     [:p (count words) " words share this value."]
     ;; What preimage levels does this number appear as?
     (let [level-words (exp/level-words n)]
       (when (seq level-words)
         [:div.section
          [:h2 "Also a preimage level (" (count level-words) " words)"]
          [:div.word-grid
           (for [{w :word} (take 20 level-words)]
             (word-link w))]]))
     [:div.section
      [:h2 "Words with GV = " n]
      [:table.word-table
       [:thead [:tr [:th "Word"] [:th ""] [:th "Freq"] [:th "Preimage"] [:th "Len"]]]
       [:tbody
        (for [{:keys [word freq preimage len]} words]
          [:tr
           [:td (word-link word)]
           [:td.meaning-col (human/gloss word)]
           [:td freq]
           [:td (if (and preimage (pos? preimage))
                  (num-link preimage "level" preimage)
                  "—")]
           [:td len]])]]]]))

;; ── Search Results ───────────────────────────────────────────

(defn search-results [q results]
  [:div#search-results
   [:h2 (count results) " results for \"" q "\""]
   [:table.word-table
    [:thead [:tr [:th "Word"] [:th ""] [:th "Freq"] [:th "GV"] [:th "Preimage"]]]
    [:tbody
     (for [{:keys [word freq gv preimage]} results]
       [:tr
        [:td (word-link word)]
        [:td.meaning-col (human/gloss word)]
        [:td freq]
        [:td (num-link gv "gv" gv)]
        [:td (if (and preimage (pos? preimage))
               (num-link preimage "level" preimage)
               "—")]])]]])

;; ── Coincidence View ─────────────────────────────────────────

(defn coincidence-view [words]
  "Show where multiple words coincide — same preimage count, same GV, same fibers."
  (let [idx (exp/index)
        word-data (keep #(get (:by-word idx) %) words)
        preimages (map :preimage word-data)
        gvs (map :gv word-data)
        gv-sum (reduce + gvs)
        gv-product (reduce * gvs)
        pre-sum (reduce + (filter pos? preimages))]
    [:div#coincidence-view
     [:h1 "Coincidence: " (str/join " + " words)]
     [:div.word-header
      (for [w words]
        [:span.combo-word (word-link w "combo")])]

     [:div.section
      [:h2 "Individual"]
      [:table.word-table
       [:thead [:tr [:th "Word"] [:th ""] [:th "Freq"] [:th "GV"] [:th "Preimage"]]]
       [:tbody
        (for [{:keys [word freq gv preimage]} word-data]
          [:tr
           [:td (word-link word)]
           [:td.meaning-col (human/gloss word)]
           [:td freq]
           [:td (num-link gv "gv" gv) " " (props-badges (exp/number-properties gv))]
           [:td (when (pos? (or preimage 0))
                  (list (num-link preimage "level" preimage)
                        " " (props-badges (exp/number-properties preimage))))]])]]]

     [:div.section
      [:h2 "Combined"]
      [:p "GV sum: " gv-sum " " (props-badges (exp/number-properties gv-sum))]
      [:p "GV product: " gv-product " " (props-badges (exp/number-properties gv-product))]
      (when (pos? pre-sum)
        [:p "Preimage sum: " pre-sum " " (props-badges (exp/number-properties pre-sum))])]

     ;; Shared preimage level?
     (let [shared-pre (when (apply = (filter pos? preimages))
                        (first (filter pos? preimages)))]
       (when shared-pre
         [:div.section
          [:h2 "Same preimage level: " shared-pre " "
           (props-badges (exp/number-properties shared-pre))]]))

     ;; Cross-connections between words
     [:div.section
      [:h2 "Cross-connections"]
      (for [a word-data
            b word-data
            :when (not= (:word a) (:word b))
            :let [matches
                  (cond-> []
                    (and (pos? (or (:preimage a) 0))
                         (= (:preimage a) (:gv b)))
                    (conj (str "preimage(" (:word a) ") = " (:preimage a) " = GV(" (:word b) ")"))

                    (and (pos? (or (:preimage b) 0))
                         (= (:preimage b) (:gv a)))
                    (conj (str "preimage(" (:word b) ") = " (:preimage b) " = GV(" (:word a) ")"))

                    (= (:preimage a) (:preimage b))
                    (conj (str "same preimage: " (:preimage a))))]
            :when (seq matches)]
        [:div.xref
         (for [m matches]
           [:p.connection m])])]]))

;; ── Home ─────────────────────────────────────────────────────

(defn home-content []
  (let [idx (exp/index)]
    [:div#home
     [:div.search-box
      [:h1 "סלה"]
      [:p "Pause. Look. See what's hidden."]
      [:form {:hx-get "/fragment/search"
              :hx-target "#main"
              :hx-push-url "true"}
       [:input#search {:type "text" :name "q"
                       :placeholder "Enter a Hebrew word..."
                       :autofocus true
                       :dir "rtl"
                       :autocomplete "off"}]
       [:button {:type "submit"} "Search"]]
      [:form {:hx-get "/fragment/coincidence"
              :hx-target "#main"
              :hx-push-url "true"
              :class "combo-form"}
       [:input {:type "text" :name "words"
                :placeholder "Multiple words (space-separated)..."
                :dir "rtl"}]
       [:button {:type "submit"} "Coincidence"]]]

     [:div.section
      [:h2 "Quick navigation"]
      [:div.quick-links
       [:div
        [:h3 "Fibonacci levels"]
        (for [f [1 2 3 5 8 13 21 34 55 89 144]]
          (num-link f "level" (str f " ")))]
       [:div
        [:h3 "Axis numbers"]
        (for [n [7 13 26 50 67 91 130 350 469 611 620]]
          (num-link n "gv" (str n " ")))]
       [:div
        [:h3 "Key words"]
        (for [w ["את" "יהוה" "אהבה" "תורה" "כבש" "כרת" "שמר"
                 "אור" "דם" "בן" "משה" "ברא" "אמת"]]
          (word-link w))]]]

     [:div.stats
      [:p (:total-words idx) " words indexed. "
       (:total-tokens idx) " tokens. "
       (reduce + (map count (vals (:by-preimage idx)))) " with preimage hits. "
       (count (:by-preimage idx)) " distinct levels."]]]))

;; ── Oracle Views ────────────────────────────────────────────

(defn breastplate-grid
  "Render the 4x3 breastplate grid. lit-positions highlights specific [stone idx] pairs."
  ([] (breastplate-grid nil))
  ([lit-positions]
   (let [lit (set (or lit-positions []))]
     [:div.breastplate
      [:table.bp-grid
       (for [row [1 2 3 4]]
         [:tr
          (for [col [1 2 3]]
            (let [[s letters _ _] (first (filter (fn [[_ _ r c]] (and (= r row) (= c col)))
                                                 oracle/stone-data))
                  tribe (oracle/stone-tribe s)]
              [:td.stone
               [:div.stone-num (str "S" s)]
               [:div.stone-letters
                (for [[i ch] (map-indexed vector (vec letters))]
                  (let [pos [s i]
                        cls (cond
                              (empty? lit)       "idle"
                              (contains? lit pos) "lit"
                              :else               "dim")]
                    [:span {:class cls} (str ch)]))]
               [:div.stone-tribe tribe]]))])]])))

(defn reader-card
  "Single reader card showing name, word read, known status, and reading count."
  [reader-key word count-n]
  (let [names {:aaron "Aaron (accused)" :truth "Truth (prosecution)" :mercy "Mercy (defense)"}
        meaning (human/gloss word)]
    [:div.reader-card
     [:div.reader-name (get names reader-key (name reader-key))]
     [:div.reader-word {:class (if meaning "known" "unknown")} word]
     (when meaning [:div.reader-meaning meaning])
     [:div.reader-count (str count-n " reading" (when (not= count-n 1) "s"))]]))

(defn oracle-content
  "The oracle page — header, two input modes (Ask/Illuminate), grid, results."
  []
  [:div#oracle
   [:div.oracle-header
    [:h1 "אורים ותומים"]
    [:p "Letters light up. The reader arranges them."]]

   ;; Tabs
   [:div.oracle-tabs
    [:button.oracle-tab.active {:data-tab "english" :onclick "switchTab('english')"} "English"]
    [:button.oracle-tab {:data-tab "ask" :onclick "switchTab('ask')"} "Ask (reverse)"]
    [:button.oracle-tab {:data-tab "illuminate" :onclick "switchTab('illuminate')"} "Illuminate (forward)"]
    [:button.oracle-tab {:data-tab "question" :onclick "switchTab('question')"} "Question"]
    [:button.oracle-tab {:data-tab "thummim" :onclick "switchTab('thummim')"} "Thummim"]]

   ;; English mode: type English, auto-translate to Hebrew, run oracle
   [:div#mode-english.oracle-mode
    [:form.oracle-form {:hx-get "/fragment/oracle/translate"
                        :hx-target "#oracle-result"
                        :hx-indicator "#oracle-result"}
     [:input {:type "text" :name "q"
              :placeholder "Ask in English..."
              :dir "ltr" :autocomplete "off" :autofocus true}]
     [:button {:type "submit"} "Ask"]]
    (when-not (translate/loaded?)
      [:p.oracle-warning "Translation model not loaded."])]

   ;; Ask mode (reverse): enter a word, see which stones light
   [:div#mode-ask.oracle-mode {:style "display:none"}
    [:form.oracle-form {:hx-get "/fragment/oracle/ask"
                        :hx-target "#oracle-result"
                        :hx-indicator "#oracle-result"}
     [:input {:type "text" :name "word"
              :placeholder "Enter a word..."
              :dir "auto" :autocomplete "off" :autofocus true}]
     [:button {:type "submit"} "Ask"]]]

   ;; Illuminate mode (forward): enter letters, see what readers produce
   [:div#mode-illuminate.oracle-mode {:style "display:none"}
    [:form.oracle-form {:hx-get "/fragment/oracle/illuminate"
                        :hx-target "#oracle-result"
                        :hx-indicator "#oracle-result"}
     [:input {:type "text" :name "letters"
              :placeholder "Enter lit letters..."
              :dir "auto" :autocomplete "off"}]
     [:button {:type "submit"} "Illuminate"]]]

   ;; Question mode: multi-word, coincidence in the output
   [:div#mode-question.oracle-mode {:style "display:none"}
    [:form.oracle-form {:hx-get "/fragment/oracle/question"
                        :hx-target "#oracle-result"
                        :hx-indicator "#oracle-result"}
     [:input {:type "text" :name "q"
              :placeholder "Ask a question (multiple words)..."
              :dir "auto" :autocomplete "off"}]
     [:button {:type "submit"} "Question"]]]

   ;; Thummim mode: phrase assembly from illumination
   [:div#mode-thummim.oracle-mode {:style "display:none"}
    [:form.oracle-form {:hx-get "/fragment/oracle/thummim"
                        :hx-target "#oracle-result"
                        :hx-indicator "#oracle-result"}
     [:input {:type "text" :name "word"
              :placeholder "Enter a word to parse..."
              :dir "auto" :autocomplete "off"}]
     [:button {:type "submit"} "Parse"]]]

   ;; Breastplate grid (idle state) — #breastplate allows OOB swap from English tab
   [:div#breastplate (breastplate-grid)]

   ;; Result area
   [:div#oracle-result]

   ;; Tab switch script
   [:script (h/raw "function switchTab(tab) {
      document.querySelectorAll('.oracle-tab').forEach(function(t){t.classList.remove('active')});
      document.querySelectorAll('.oracle-mode').forEach(function(f){f.style.display='none'});
      document.querySelector('[data-tab=\"'+tab+'\"]').classList.add('active');
      document.getElementById('mode-'+tab).style.display='block';
    }")]])

(defn oracle-ask-result
  "Reverse result: the word's oracle pre-image."
  [result]
  (let [{:keys [word meaning gv illumination-count by-reader
                total-readings first-illumination anagrams readable?]} result]
    [:div
     ;; Grid with first illumination highlighted
     (breastplate-grid first-illumination)

     [:div.section
      [:h2 word (when meaning (str " — " meaning)) " = " gv]

      (if readable?
        [:div
         [:p (str illumination-count " illumination pattern"
              (when (not= illumination-count 1) "s")
              ", " total-readings " total reading"
              (when (not= total-readings 1) "s"))]

         ;; Reader cards
         [:div.reader-cards
          (reader-card :aaron
                       (when first-illumination
                         (oracle/read-positions :aaron first-illumination))
                       (:aaron by-reader))
          (reader-card :truth
                       (when first-illumination
                         (oracle/read-positions :truth first-illumination))
                       (:truth by-reader))
          (reader-card :mercy
                       (when first-illumination
                         (oracle/read-positions :mercy first-illumination))
                       (:mercy by-reader))]

         ;; Stones involved
         (when first-illumination
           (let [stones (sort (set (map first first-illumination)))]
             [:p "Stones: " (str/join ", " (map #(str (oracle/stone-tribe %)) stones))]))]

        ;; Not readable
        [:div.not-readable
         [:div.big word]
         [:p "Cannot be read from the breastplate."]
         [:p "This word is beyond the grid — you must enter."]])]

     ;; Anagrams
     (when (seq anagrams)
       [:div.section
        [:h2 "Same letters, other words"]
        [:div.word-grid
         (for [{:keys [word meaning]} anagrams]
           [:span
            (word-link word)
            (when meaning [:span.meaning meaning])])]])]))

(defn oracle-illuminate-result
  "Forward result: what the readers see from lit letters."
  [result]
  (let [{:keys [letters illumination-count total-readings
                known-words unknown-words anagrams sample-readings]} result
        max-count (if (seq known-words)
                    (apply max (map :reading-count known-words))
                    1)]
    [:div
     ;; Grid — highlight all positions from first illumination
     (let [ilsets (oracle/illumination-sets letters)]
       (breastplate-grid (first ilsets)))

     [:div.section
      [:h2 (str illumination-count " illumination"
            (when (not= illumination-count 1) "s")
            ", " total-readings " readings")]

      ;; Sample: first illumination three readings
      (when sample-readings
        [:div
         [:h2 "First illumination — three readings"]
         [:div.reader-cards
          (for [[reader-key word] sample-readings]
            (let [meaning (human/gloss word)]
              [:div.reader-card
               [:div.reader-name (name reader-key)]
               [:div.reader-word {:class (if meaning "known" "unknown")} word]
               (when meaning [:div.reader-meaning meaning])]))]])]

     ;; Known words ranked by rarity
     (when (seq known-words)
       [:div.section
        [:h2 "Known words — rarest first (Hannah principle)"]
        (for [{:keys [word reading-count readers meaning gv]} known-words]
          (let [pct (min 100 (* 100 (/ reading-count max-count)))]
            [:div.rarity-item
             [:div.rarity-word.known word]
             [:div.rarity-meaning (or meaning "")]
             [:div.rarity-bar
              [:div.rarity-fill {:style (str "width:" pct "%")}]]
             [:div.rarity-count
              (str reading-count " reading" (when (not= reading-count 1) "s"))]
             [:div.rarity-readers
              (str/join " " (map name (sort readers)))]]))])

     ;; Anagrams from dictionary
     (when (seq anagrams)
       [:div.section
        [:h2 "Dictionary anagrams"]
        [:div.word-grid
         (for [{:keys [word meaning]} anagrams]
           [:span (word-link word)])]])]))

(defn- stone-label [s]
  (str "S" s " (" (oracle/stone-tribe s) ")"))

(defn oracle-question-result
  "Question result: union / intersection / difference across words."
  [result]
  (let [{:keys [words per-word coincidences all-readings
                unique-per-word stones unreadable]} result
        {:keys [union intersection unique]} stones]
    [:div
     ;; Per-word summary
     [:div.section
      [:h2 "Each word independently"]
      [:table.word-table
       [:thead [:tr [:th "Word"] [:th ""] [:th "GV"] [:th "Readable?"]
               [:th "Readings"] [:th "A"] [:th "R"] [:th "L"] [:th "Stones"]]]
       [:tbody
        (for [{:keys [input meaning gv readable? total-readings
                      by-reader]} (sort-by (comp - :total-readings) per-word)]
          [:tr
           [:td [:span.oracle-word {:class (when readable? "known")} input]]
           [:td.meaning-col (or meaning "")]
           [:td gv]
           [:td (if readable? "yes" [:strong "NO"])]
           [:td total-readings]
           [:td (get by-reader :aaron 0)]
           [:td (get by-reader :truth 0)]
           [:td (get by-reader :mercy 0)]
           [:td (str/join "," (sort (:stones (first (filter #(= input (:input %)) per-word)))))]
           ])]]]

     ;; Unreadable words
     (when (seq unreadable)
       [:div.section
        [:h2 "Beyond the grid"]
        [:p "These words cannot be read from the breastplate:"]
        [:div.word-grid
         (for [{:keys [input meaning]} unreadable]
           [:span.oracle-word input
            (when meaning [:span.meaning meaning])])]])

     ;; ── Stones: union / intersection / difference ──

     [:div.section
      [:h2 "Stones"]

      (when (seq intersection)
        [:div
         [:h3 "Intersection — lit by every word"]
         [:p (str/join ", " (map stone-label (sort intersection)))]])

      (when (some seq (vals unique))
        [:div
         [:h3 "Difference — unique to one word"]
         (for [w words
               :let [u (get unique w)]
               :when (seq u)]
           [:p [:span.oracle-word.known w] " only: "
            (str/join ", " (map stone-label (sort u)))])])

      (when union
        [:p {:style "color:#52525b;font-size:0.75rem;margin-top:0.5rem"}
         "Union: " (count union) " of 12 stones"])]

     ;; ── Readings: intersection (coincidences) ──

     (if (seq coincidences)
       [:div.section
        [:h2 "Intersection — same reading from different inputs"]
        (for [{:keys [word meaning gv sources source-count total-readings]} coincidences]
          [:div.rarity-item
           [:div.rarity-word.known word]
           [:div.rarity-meaning (or meaning "")]
           [:div {:style "flex:1"}
            [:div {:style "font-size:0.75rem;color:#a1a1aa"}
             (str source-count "/" (count words) " words: " (str/join ", " sources))]]
           [:div.rarity-count (str total-readings " readings")]])]

       [:div.section
        [:h2 "Intersection"]
        [:p "No shared readings. Each word speaks to a different part of the grid."]])

     ;; ── Readings: difference (unique to each word) ──

     (when (some seq (vals unique-per-word))
       [:div.section
        [:h2 "Difference — readings unique to each word"]
        (for [w words
              :let [unique-readings (get unique-per-word w)]
              :when (seq unique-readings)]
          [:div
           [:h3 [:span.oracle-word.known w]
            (when-let [m (human/gloss w)] (str " — " m))]
           (for [{:keys [word meaning total-readings]} (take 10 unique-readings)]
             [:div.rarity-item
              [:div.rarity-word {:class (if meaning "known" "unknown-word")} word]
              [:div.rarity-meaning (or meaning "")]
              [:div.rarity-count (str total-readings " readings")]])])])

     ;; ── Union: full reading table ──

     (when (seq all-readings)
       [:div.section
        [:h2 "Union — all " (count all-readings) " readings"]
        [:table.word-table
         [:thead [:tr [:th "Reading"] [:th ""] [:th "GV"] [:th "Sources"] [:th "Readings"]]]
         [:tbody
          (for [{:keys [word meaning gv sources total-readings]}
                (take 40 all-readings)]
            [:tr {:class (when (> (count sources) 1) "coincidence-row")}
             [:td [:span.oracle-word {:class (when (human/gloss word) "known")} word]]
             [:td.meaning-col (or meaning "")]
             [:td gv]
             [:td (str/join ", " sources)]
             [:td total-readings]])]]])]))

(defn oracle-thummim-result
  "Thummim result: all phrase readings from a word's illumination."
  [result]
  (if (nil? result)
    [:div.section [:p "Word has no illumination patterns on the breastplate."]]
    (let [{:keys [word meaning gv illumination-count phrases]} result]
      [:div
       ;; Header
       [:div.section
        [:h2 word (when meaning (str " — " meaning)) " = " gv]
        [:p (str illumination-count " illumination pattern"
             (when (not= illumination-count 1) "s")
             ". " (count phrases) " unique phrase reading"
             (when (not= (count phrases) 1) "s") ".")]
        [:p {:style "color:#a1a1aa;font-size:0.75rem"}
         "The Thummim provides the menu. The priest chooses."]]

       ;; Phrases grouped by word count
       (let [by-count (group-by :words phrases)]
         (for [[n ps] (sort-by key by-count)]
           [:div.section
            [:h2 (if (= n 1)
                   "Single words"
                   (str n "-word phrases"))]
            (for [{:keys [text phrase meanings gv occurrences]} ps]
              [:div.rarity-item
               [:div.rarity-word.known
                [:span {:dir "rtl"} text]]
               [:div.rarity-meaning
                (str/join " + " (remove nil? meanings))]
               [:div.rarity-count (str "GV=" gv)]])]))])))

(defn- tier-badge
  "Render a tier badge with appropriate styling."
  [tier tier-name]
  [:span.badge {:class (str "tier-" tier)} tier-name])

(def ^:private reader-labels
  {:god "God" :truth "Truth" :mercy "Mercy" :aaron "Aaron"})

(def ^:private reader-css
  {:god "reader-god" :truth "reader-truth" :mercy "reader-mercy" :aaron "reader-aaron"})

(defn- reader-badges
  "Show which readers can see a phrase (or per-word readers)."
  [readers per-word-readers]
  (when (or (seq readers) (some seq per-word-readers))
    [:span.reader-badges
     (if (seq readers)
       ;; All readers see every word — show as solid badges
       (for [r (sort-by {:god 0 :truth 1 :mercy 2 :aaron 3} readers)]
         [:span.badge {:class (reader-css r)} (reader-labels r)])
       ;; Show per-word reader info as dim badges for partial coverage
       (let [any (apply set/union #{} per-word-readers)]
         (for [r (sort-by {:god 0 :truth 1 :mercy 2 :aaron 3} any)]
           [:span.badge.reader-partial {:class (reader-css r)} (reader-labels r)])))]))

(defn- phrase-card
  "Render a single ranked phrase reading."
  [{:keys [text english gv gv-props tier tier-name self? phrase
           readers per-word-readers]}]
  [:div.rarity-item
   (tier-badge tier tier-name)
   [:div.rarity-word {:class (if (< tier 4) "known" "unknown-word")}
    [:span {:dir "rtl"} text]]
   [:div.rarity-meaning
    (let [meanings (remove nil? english)]
      (if (seq meanings)
        (str/join " + " meanings)
        (str/join " + " (map human/gloss! phrase))))]
   (reader-badges readers per-word-readers)
   [:div.rarity-count
    (str "GV=" gv)
    (when (seq gv-props)
      (list " " (props-badges gv-props)))]])

(defn oracle-translate-result
  "Translation result: English → Hebrew → ranked oracle readings."
  [{:keys [english hebrew hebrew-letters gv readings synthesis lit-positions per-word-lit]}]
  (list
   ;; Out-of-band swap: show all lit positions by default
   [:div#breastplate {:hx-swap-oob "true"}
    (breastplate-grid lit-positions)]

   ;; Hover illumination script
   (when (seq per-word-lit)
     [:script (h/raw
       (str "
(function(){
  var allPos = " (json/write-str (mapv (fn [[s i]] [s i]) (vec (or lit-positions #{})))) ";
  var grid = document.querySelector('#breastplate .bp-grid');
  if (!grid) return;
  function illuminate(positions) {
    var cells = grid.querySelectorAll('td.stone');
    cells.forEach(function(td) {
      var num = td.querySelector('.stone-num');
      if (!num) return;
      var s = parseInt(num.textContent.replace('S',''));
      var spans = td.querySelector('.stone-letters').children;
      for (var i = 0; i < spans.length; i++) { spans[i].className = 'dim'; }
      positions.forEach(function(p) {
        if (p[0] === s && p[1] < spans.length) spans[p[1]].className = 'lit';
      });
    });
  }
  document.querySelectorAll('.hebrew-word').forEach(function(el) {
    el.addEventListener('mouseenter', function() {
      illuminate(JSON.parse(el.dataset.pos));
    });
    el.addEventListener('mouseleave', function() {
      illuminate(allPos);
    });
  });
})();
"))])

   [:div
   ;; Translation header — Hebrew words are hoverable
   [:div.section
    [:h2 "Translation"]
    [:p {:style "font-size:0.85rem;color:#a1a1aa"} english]
    [:div {:style "display:flex;align-items:baseline;gap:1rem;margin:0.5rem 0"}
     [:span.hebrew {:style "font-size:2rem;direction:rtl"}
      (if (seq per-word-lit)
        (interpose " "
          (for [{:keys [word positions]} per-word-lit]
            [:span.hebrew-word
             {:data-pos (json/write-str (mapv (fn [[s i]] [s i]) positions))
              :style "cursor:pointer;transition:color 0.2s"}
             word]))
        hebrew)]
     [:span {:style "color:#71717a;font-size:0.85rem"} (str "letters: " hebrew-letters)]
     [:span {:style "color:#71717a;font-size:0.85rem"} (str "GV=" gv)
      (when-let [props (oracle/gv-properties gv)]
        (list " " (props-badges props)))]]]

   (if (seq readings)
     (let [self-reading (:self-reading synthesis)
           tier-counts (or (:tier-counts synthesis)
                           (frequencies (map :tier readings)))
           by-tier (group-by :tier readings)
           ;; Best non-self readings for the "oracle speaks" header
           top (or (seq (remove :self? (take 3 (or (:top synthesis)
                                                    (take 4 readings)))))
                   (take 3 readings))]
       [:div
        ;; Oracle speaks — synthesis header
        [:div.section.oracle-speaks
         [:h2 "The oracle speaks"]
         (when self-reading
           (phrase-card self-reading))
         (for [r top]
           (phrase-card r))]

        ;; Tier section helper
        (letfn [(tier-section [tier label desc cap & {:keys [collapsed?]}]
                  (when-let [items (seq (get by-tier tier))]
                    (let [total (get tier-counts tier (count items))
                          shown (take cap items)
                          hidden (- total (count shown))
                          content (list
                                   (when desc
                                     [:p {:style "color:#a1a1aa;font-size:0.75rem"} desc])
                                   (for [r shown] (phrase-card r))
                                   (when (pos? hidden)
                                     [:p {:style "color:#52525b;font-size:0.75rem;margin-top:0.5rem"}
                                      (str hidden " more not shown.")]))]
                      (if collapsed?
                        [:details.section
                         [:summary [:h2 {:style "display:inline"}
                                    (str label " (" total ")")]]
                         content]
                        [:div.section
                         [:h2 (str label " (" total ")")]
                         content]))))]

          (list
           ;; Tier 1: Unanimous — all 4 readers agree
           (tier-section 1 "Unanimous" "All four readers see every word." 20)

           ;; Tier 2: Majority — 3 readers
           (tier-section 2 "Majority" "Three readers agree." 20)

           ;; Tier 3: Split — 2 readers
           (tier-section 3 "Split" nil 15 :collapsed? true)

           ;; Tier 4: Solo — 1 reader only
           (tier-section 4 "Solo" nil 10 :collapsed? true)

           ;; Tier 5: Silent — no reader can produce it
           (tier-section 5 "Silent" nil 10 :collapsed? true)))])

     ;; No readings
     [:div.section
      [:p "No oracle readings found for these letters."]])]))

;; ── Basin of Attraction ──────────────────────────────────────

(defn basin-content
  "The basin page — enter a word, watch it flow to a fixed point."
  []
  [:div#basin
   [:div.basin-header
    [:h1 "אגן משיכה"]
    [:p "Feed a word to the oracle. Feed the answer back in. Watch it converge."]]

   ;; The three system attractors
   [:div.basin-stars
    [:div.basin-star
     [:div.star-word "אהבה"]
     [:div.star-meaning "love"]
     [:div.star-gv "13"]]
    [:div.basin-star
     [:div.star-word "אמת"]
     [:div.star-meaning "truth"]
     [:div.star-gv "441"]]
    [:div.basin-star
     [:div.star-word "חיים"]
     [:div.star-meaning "life"]
     [:div.star-gv "68"]]]

   ;; Mode tabs
   [:div.basin-tabs
    [:button.basin-tab.active {:data-mode "echo" :onclick "switchBasinMode('echo')"} "Echo"]
    [:button.basin-tab {:data-mode "flow" :onclick "switchBasinMode('flow')"} "Flow"]]

   [:div.basin-form
    [:form#basin-form {:hx-get "/fragment/basin/walk"
                       :hx-target "#basin-result"
                       :hx-indicator "#basin-result"}
     [:input {:type "text" :name "word" :id "basin-word"
              :placeholder "Enter a word..."
              :dir "auto" :autocomplete "off" :autofocus true}]
     [:input {:type "hidden" :name "mode" :id "basin-mode" :value "echo"}]
     [:button {:type "submit"} "Walk"]]]

   ;; Suggested words
   [:div.basin-suggest
    [:span.basin-suggest-label "Try: "]
    (for [w ["כבש" "ברא" "שבע" "אהבה" "אמת" "חיים" "תורה" "שלום" "דם" "יין"]]
      [:a.basin-try {:href "#"
                     :onclick (str "document.getElementById('basin-word').value='"
                                   w "';document.getElementById('basin-form').requestSubmit();return false;")}
       w])]

   [:div#basin-result]

   ;; Mode switch script
   [:script (h/raw "function switchBasinMode(mode) {
      document.querySelectorAll('.basin-tab').forEach(function(t){t.classList.remove('active')});
      document.querySelector('[data-mode=\"'+mode+'\"]').classList.add('active');
      document.getElementById('basin-mode').value = mode;
      var word = document.getElementById('basin-word').value;
      if (word) document.getElementById('basin-form').requestSubmit();
    }")]])

(defn basin-walk-result
  "The animated walk from word to fixed point (or cycle/dead-end)."
  [result]
  (let [{:keys [steps converged? fixed-point cycle? cycle-word dead-end]} result
        n (count steps)]
    [:div
     [:div.basin-path
      (for [[i s] (map-indexed vector steps)]
        (list
         [:div.basin-step
          {:class (when (and (= i (dec n)) converged?) "fixed-point")
           :style (str "animation-delay:" (* i 0.12) "s")}
          [:span.step-num (str (inc i))]
          [:span.step-word
           [:a {:href (str "/word/" (hu/url-encode (:word s)))
                :hx-get (str "/fragment/word/" (hu/url-encode (:word s)))
                :hx-target "#main"
                :hx-push-url (str "/word/" (hu/url-encode (:word s)))
                :style "color: inherit; text-decoration: none;"}
            (:word s)]]
          [:span.step-meaning (or (:gloss s) (human/gloss (:word s)) "")]
          [:span.step-meta
           [:span.step-gv (str (:gv s))]
           (str " \u00b7 " (:weight s) "/" (:total-readings s))]]
         (when (< i (dec n))
           [:div.basin-arrow
            {:class (when (and converged? (= i (- n 2))) "converging")}
            "\u2193"])))]

     [:div.basin-summary
      (cond
        converged?
        [:div
         [:div.fixed (str fixed-point)]
         [:div.label
          (let [last-step (peek steps)]
            (str (or (:fixed-point-gloss result) (human/gloss fixed-point) "") " = " (or (:gv last-step) "")))
          [:br]
          (str "Converged in " n " step" (when (not= n 1) "s"))]]

        cycle?
        [:div
         [:div {:style "color: #fbbf24; font-size: 1.2rem;"} "\u21bb Cycle"]
         [:div.label (str "Orbiting through " cycle-word " \u2014 no fixed point reached")]]

        dead-end
        [:div
         [:div {:style "color: #71717a; font-size: 1.2rem;"}
          (if (= dead-end (:start result))
            (str dead-end " speaks only itself")
            (str dead-end " \u2014 no further transitions"))]
         [:div.label
          (when-let [m (or (:dead-end-gloss result) (human/gloss dead-end))]
            (str m " \u2014 "))
          "switch to Echo mode to see the self-loop"]]

        :else
        [:div
         [:div {:style "color: #71717a;"} "Did not converge"]
         [:div.label (str "Reached " n " steps without settling")]])]]))

;; ── Layout ───────────────────────────────────────────────────

(def css
  "
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body {
    font-family: 'IBM Plex Mono', 'Menlo', monospace;
    background: #0a0a0f;
    color: #d4d4d8;
    max-width: 960px;
    margin: 0 auto;
    padding: 2rem 1rem;
    line-height: 1.6;
  }
  h1 { color: #e4e4e7; margin-bottom: 0.5rem; }
  h2 { color: #a1a1aa; margin: 1.5rem 0 0.5rem; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.1em; }
  h3 { color: #71717a; font-size: 0.8rem; margin-bottom: 0.3rem; }
  a { color: #60a5fa; text-decoration: none; }
  a:hover { text-decoration: underline; color: #93bbfc; }

  .hebrew { font-size: 2.5rem; direction: rtl; font-family: 'SBL Hebrew', 'David', serif; }
  .hebrew-word { cursor: pointer; transition: color 0.2s; }
  .hebrew-word:hover { color: #fbbf24; }
  .word-header { margin-bottom: 1.5rem; padding-bottom: 1rem; border-bottom: 1px solid #27272a; }
  .word-meta { display: flex; flex-wrap: wrap; gap: 1rem; margin-top: 0.5rem; }
  .stat { font-size: 0.85rem; color: #a1a1aa; }
  .stat strong { color: #e4e4e7; }

  .section { margin: 1.5rem 0; padding: 1rem; background: #111118; border-radius: 8px; border: 1px solid #1e1e2a; }
  .explain { font-size: 0.8rem; color: #71717a; margin-bottom: 0.5rem; direction: rtl; }

  .word-grid { display: flex; flex-wrap: wrap; gap: 0.5rem; direction: rtl; }
  .word-link { display: inline-block; padding: 0.2rem 0.6rem; background: #1a1a2e; border: 1px solid #2a2a3e;
               border-radius: 4px; font-family: 'SBL Hebrew', 'David', serif; font-size: 1.1rem;
               direction: rtl; transition: all 0.15s; }
  .word-link:hover { background: #252540; border-color: #60a5fa; text-decoration: none; }
  .word-link.combo { background: #1a2e1a; border-color: #2a4e2a; }

  .num-link { display: inline-block; padding: 0.1rem 0.4rem; background: #1e1a2e; border: 1px solid #2e2a3e;
              border-radius: 3px; font-size: 0.85rem; transition: all 0.15s; }
  .num-link:hover { background: #2a2540; border-color: #a78bfa; text-decoration: none; }

  .badges { display: inline; margin-left: 0.3rem; }
  .badge { display: inline-block; padding: 0.1rem 0.4rem; border-radius: 3px; font-size: 0.7rem; margin: 0 0.1rem; }
  .badge.fib { background: #422006; color: #fbbf24; border: 1px solid #854d0e; }
  .badge.tri { background: #1e3a5f; color: #7dd3fc; border: 1px solid #0369a1; }
  .badge.sq { background: #3b1f5e; color: #c4b5fd; border: 1px solid #6d28d9; }
  .badge.prime { background: #1a3320; color: #86efac; border: 1px solid #166534; }
  .badge.axis { background: #2d1b1b; color: #fca5a5; border: 1px solid #7f1d1d; }

  .word-table { width: 100%; border-collapse: collapse; margin-top: 0.5rem; }
  .word-table th { text-align: left; font-size: 0.75rem; color: #71717a; padding: 0.3rem 0.5rem;
                   border-bottom: 1px solid #27272a; }
  .word-table td { padding: 0.3rem 0.5rem; border-bottom: 1px solid #18181b; font-size: 0.85rem; }
  .word-table tr:hover { background: #111118; }

  .search-box { text-align: center; margin: 2rem 0; }
  .search-box h1 { font-size: 4rem; font-family: 'SBL Hebrew', 'David', serif; color: #60a5fa; }
  .search-box p { color: #71717a; margin-bottom: 1.5rem; font-style: italic; }
  .search-box form { display: flex; gap: 0.5rem; justify-content: center; margin-bottom: 0.5rem; }
  .search-box input { background: #111118; border: 1px solid #27272a; color: #e4e4e7; padding: 0.5rem 1rem;
                       border-radius: 6px; font-size: 1.1rem; width: 300px;
                       font-family: 'SBL Hebrew', 'David', serif; }
  .search-box input:focus { outline: none; border-color: #60a5fa; }
  .search-box button { background: #1e293b; color: #60a5fa; border: 1px solid #334155; padding: 0.5rem 1rem;
                        border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
  .search-box button:hover { background: #334155; }
  .combo-form { margin-top: 0.5rem; }

  .quick-links { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem; }
  .quick-links div { display: flex; flex-wrap: wrap; gap: 0.3rem; align-items: flex-start; }

  .stats { text-align: center; margin-top: 2rem; color: #52525b; font-size: 0.8rem; }

  .meaning { display: block; font-size: 0.65rem; color: #71717a; font-family: 'IBM Plex Mono', 'Menlo', monospace; direction: ltr; }
  .meaning-col { font-size: 0.8rem; color: #71717a; white-space: nowrap; }
  .translation { color: #a1a1aa; font-size: 1rem; margin-top: 0.25rem; font-style: italic; }

  .connection { font-size: 0.85rem; color: #a78bfa; padding: 0.2rem 0; direction: rtl; }
  .xref { margin-bottom: 0.3rem; }

  nav { margin-bottom: 1rem; }
  nav a { margin-right: 1rem; font-size: 0.85rem; color: #71717a; }
  nav a:hover { color: #d4d4d8; }
  .nav-label { font-size: 0.7rem; color: #52525b; font-family: 'IBM Plex Mono', monospace; }

  /* Breastplate grid */
  .breastplate { margin: 1.5rem auto; max-width: 480px; }
  .bp-grid { border-collapse: separate; border-spacing: 4px; margin: 0 auto; direction: ltr; width: 100%; }
  .stone { background: #1a1a2e; border: 1px solid #2a2a3e; border-radius: 6px; padding: 0.5rem 0.3rem;
           text-align: center; vertical-align: top; }
  .stone-num { font-size: 0.6rem; color: #52525b; margin-bottom: 0.2rem; }
  .stone-tribe { font-size: 0.55rem; color: #3f3f46; }
  .stone-letters { font-family: 'SBL Hebrew', 'David', serif; font-size: 1.3rem; direction: rtl;
                   letter-spacing: 0.15em; }
  .stone-letters .lit { color: #fbbf24; text-shadow: 0 0 10px rgba(251,191,36,0.5); font-weight: bold; }
  .stone-letters .dim { color: #3f3f46; }
  .stone-letters .idle { color: #a1a1aa; }

  /* Oracle page */
  .oracle-header { text-align: center; margin-bottom: 1.5rem; }
  .oracle-header h1 { font-family: 'SBL Hebrew', 'David', serif; font-size: 2.5rem; color: #fbbf24; }
  .oracle-header p { color: #71717a; font-style: italic; font-size: 0.85rem; }
  .oracle-tabs { display: flex; gap: 0.5rem; justify-content: center; margin-bottom: 1rem; }
  .oracle-tab { padding: 0.4rem 1rem; border: 1px solid #27272a; border-radius: 6px; background: #111118;
                color: #71717a; cursor: pointer; font-size: 0.85rem; transition: all 0.15s; }
  .oracle-tab.active { background: #1e293b; color: #fbbf24; border-color: #854d0e; }
  .oracle-mode { margin-bottom: 1rem; }
  .oracle-form { display: flex; gap: 0.5rem; justify-content: center; }
  .oracle-form input { background: #111118; border: 1px solid #27272a; color: #e4e4e7; padding: 0.5rem 1rem;
                       border-radius: 6px; font-size: 1.1rem; width: 250px;
                       font-family: 'SBL Hebrew', 'David', serif; direction: rtl; }
  .oracle-form input:focus { outline: none; border-color: #fbbf24; }
  .oracle-form button { background: #1e293b; color: #fbbf24; border: 1px solid #854d0e; padding: 0.5rem 1rem;
                        border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
  .oracle-form button:hover { background: #2d3748; }

  /* Reader cards */
  .reader-cards { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem; margin: 1rem 0; }
  .reader-card { background: #111118; border: 1px solid #1e1e2a; border-radius: 8px; padding: 1rem;
                 text-align: center; }
  .reader-name { font-size: 0.75rem; color: #71717a; text-transform: uppercase; letter-spacing: 0.1em;
                 margin-bottom: 0.5rem; }
  .reader-word { font-family: 'SBL Hebrew', 'David', serif; font-size: 1.8rem; direction: rtl; }
  .reader-word.known { color: #fbbf24; }
  .reader-word.unknown { color: #52525b; }
  .reader-meaning { font-size: 0.8rem; color: #a1a1aa; margin-top: 0.3rem; font-style: italic; }
  .reader-count { font-size: 0.7rem; color: #52525b; margin-top: 0.25rem; }

  /* Rarity ranking */
  .rarity-item { display: flex; align-items: center; gap: 0.75rem; margin: 0.4rem 0; padding: 0.4rem 0.6rem;
                 background: #111118; border-radius: 6px; border: 1px solid #1e1e2a; }
  .rarity-word { font-family: 'SBL Hebrew', 'David', serif; font-size: 1.2rem; direction: rtl; min-width: 60px;
                 text-align: center; }
  .rarity-word.known { color: #fbbf24; }
  .rarity-word.unknown-word { color: #52525b; }
  .rarity-meaning { font-size: 0.8rem; color: #a1a1aa; font-style: italic; flex: 1; }
  .rarity-bar { flex: 0 0 120px; height: 4px; background: #1e1e2a; border-radius: 2px; overflow: hidden; }
  .rarity-fill { height: 100%; background: #fbbf24; border-radius: 2px; }
  .rarity-count { font-size: 0.7rem; color: #71717a; min-width: 50px; text-align: right; }
  .rarity-readers { font-size: 0.65rem; color: #52525b; }
  .not-readable { text-align: center; padding: 2rem; color: #71717a; }
  .not-readable .big { font-size: 2rem; margin-bottom: 0.5rem; }
  .coincidence-row { background: #1a1a0a !important; }
  .coincidence-row td { color: #fbbf24; }

  /* English input (LTR in an RTL page) */
  .oracle-form input[dir=ltr] { direction: ltr; text-align: left;
    font-family: 'IBM Plex Mono', 'Menlo', monospace; font-size: 1rem; }
  .oracle-warning { color: #f87171; font-size: 0.8rem; text-align: center; margin-top: 0.5rem; }

  /* Tier badges — reader agreement */
  .badge.tier-0 { background: #422006; color: #fbbf24; border: 1px solid #854d0e; }
  .badge.tier-1 { background: #1a3320; color: #86efac; border: 1px solid #166534; }
  .badge.tier-2 { background: #1e3a5f; color: #7dd3fc; border: 1px solid #0369a1; }
  .badge.tier-3 { background: #2d2006; color: #fcd34d; border: 1px solid #854d0e; }
  .badge.tier-4 { background: #2d1b1b; color: #fca5a5; border: 1px solid #7f1d1d; }
  .badge.tier-5 { background: #1e1e2a; color: #52525b; border: 1px solid #27272a; }
  .oracle-speaks { border-color: #854d0e; }
  details.section > summary { cursor: pointer; }
  details.section > summary h2 { display: inline; }

  /* Reader badges */
  .reader-badges { display: inline-flex; gap: 0.15rem; margin: 0 0.3rem; }
  .badge.reader-god { background: #1a1a3e; color: #a78bfa; border: 1px solid #6d28d9; }
  .badge.reader-right { background: #1a2e1a; color: #86efac; border: 1px solid #166534; }
  .badge.reader-left { background: #2d1b1b; color: #fca5a5; border: 1px solid #7f1d1d; }
  .badge.reader-aaron { background: #2d2006; color: #fbbf24; border: 1px solid #854d0e; }
  .badge.reader-partial { opacity: 0.5; }

  /* Basin of attraction */
  .basin-header { text-align: center; margin-bottom: 1.5rem; }
  .basin-header h1 { font-family: 'SBL Hebrew', 'David', serif; font-size: 2.5rem; color: #86efac; }
  .basin-header p { color: #71717a; font-style: italic; font-size: 0.85rem; }
  .basin-form { display: flex; gap: 0.5rem; justify-content: center; margin-bottom: 1.5rem; }
  .basin-form input { background: #111118; border: 1px solid #27272a; color: #e4e4e7; padding: 0.5rem 1rem;
                      border-radius: 6px; font-size: 1.3rem; width: 250px;
                      font-family: 'SBL Hebrew', 'David', serif; direction: rtl; }
  .basin-form input:focus { outline: none; border-color: #86efac; }
  .basin-form button { background: #1a3320; color: #86efac; border: 1px solid #166534; padding: 0.5rem 1rem;
                       border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
  .basin-form button:hover { background: #254a30; }

  .basin-path { display: flex; flex-direction: column; align-items: center; gap: 0; margin: 1rem 0; }
  .basin-step { display: flex; align-items: center; gap: 1rem; padding: 0.6rem 1.2rem;
                background: #111118; border: 1px solid #1e1e2a; border-radius: 8px;
                width: 100%; max-width: 600px; transition: all 0.3s;
                animation: basin-fade-in 0.4s ease-out both; }
  .basin-step:hover { border-color: #3f3f46; background: #16161f; }
  .basin-step.fixed-point { border-color: #166534; background: #0f1f15; }
  .basin-step.fixed-point .step-word { color: #86efac; text-shadow: 0 0 12px rgba(134,239,172,0.3); }
  .basin-arrow { color: #3f3f46; font-size: 1.2rem; padding: 0.2rem 0; }
  .basin-arrow.converging { color: #86efac; }
  .step-num { font-size: 0.65rem; color: #52525b; min-width: 20px; }
  .step-word { font-family: 'SBL Hebrew', 'David', serif; font-size: 1.5rem; direction: rtl; color: #e4e4e7;
               min-width: 80px; text-align: center; }
  .step-meaning { font-size: 0.8rem; color: #71717a; font-style: italic; flex: 1; }
  .step-meta { font-size: 0.65rem; color: #52525b; text-align: right; white-space: nowrap; }
  .step-gv { color: #a78bfa; }

  .basin-summary { text-align: center; margin-top: 1.5rem; padding: 1rem; background: #111118;
                   border-radius: 8px; border: 1px solid #1e1e2a; }
  .basin-summary .fixed { color: #86efac; font-family: 'SBL Hebrew', 'David', serif; font-size: 1.8rem; }
  .basin-summary .label { color: #71717a; font-size: 0.85rem; margin-top: 0.3rem; }

  .basin-stars { display: flex; justify-content: center; gap: 2rem; margin: 1.5rem 0; }
  .basin-star { text-align: center; padding: 1rem; background: #111118; border: 1px solid #1e1e2a;
                border-radius: 8px; min-width: 100px; }
  .basin-star .star-word { font-family: 'SBL Hebrew', 'David', serif; font-size: 1.8rem; color: #86efac;
                           text-shadow: 0 0 12px rgba(134,239,172,0.3); }
  .basin-star .star-meaning { font-size: 0.75rem; color: #71717a; margin-top: 0.2rem; }
  .basin-star .star-gv { font-size: 0.65rem; color: #a78bfa; }

  .basin-tabs { display: flex; gap: 0.5rem; justify-content: center; margin-bottom: 1rem; }
  .basin-tab { padding: 0.4rem 1rem; border: 1px solid #27272a; border-radius: 6px; background: #111118;
               color: #71717a; cursor: pointer; font-size: 0.85rem; transition: all 0.15s; }
  .basin-tab.active { background: #1a3320; color: #86efac; border-color: #166534; }
  .basin-suggest { text-align: center; margin: 0.75rem 0 1rem; }
  .basin-suggest-label { font-size: 0.75rem; color: #52525b; }
  .basin-try { display: inline-block; padding: 0.15rem 0.5rem; margin: 0.1rem; background: #1a1a2e;
               border: 1px solid #2a2a3e; border-radius: 4px; font-family: 'SBL Hebrew', 'David', serif;
               font-size: 1rem; color: #a1a1aa; cursor: pointer; transition: all 0.15s; text-decoration: none; }
  .basin-try:hover { background: #252540; border-color: #86efac; color: #86efac; text-decoration: none; }

  @keyframes basin-fade-in {
    from { opacity: 0; transform: translateY(-8px); }
    to { opacity: 1; transform: translateY(0); }
  }
  .basin-step:nth-child(1) { animation-delay: 0s; }
  .basin-step:nth-child(2) { animation-delay: 0.1s; }
  .basin-step:nth-child(3) { animation-delay: 0.2s; }
  .basin-step:nth-child(4) { animation-delay: 0.3s; }
  .basin-step:nth-child(5) { animation-delay: 0.4s; }
  .basin-step:nth-child(6) { animation-delay: 0.5s; }
  .basin-step:nth-child(7) { animation-delay: 0.6s; }
  .basin-step:nth-child(8) { animation-delay: 0.7s; }
  .basin-step:nth-child(9) { animation-delay: 0.8s; }
  .basin-step:nth-child(10) { animation-delay: 0.9s; }
  ")

(defn layout [content]
  (str
   (h/html
    [:html {:lang "he" :dir "rtl"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title "Selah Explorer"]
      [:style (h/raw css)]
      [:style (h/raw sweep-ui/sweep-css)]
      [:script {:src "https://unpkg.com/htmx.org@2.0.4"}]]
     [:body
      [:nav
       [:a {:href "/" :hx-get "/fragment/home" :hx-target "#main" :hx-push-url "/"}
        "סלה " [:span.nav-label "Home"]]
       " "
       [:a {:href "/oracle" :hx-get "/fragment/oracle" :hx-target "#main" :hx-push-url "/oracle"}
        "אורים " [:span.nav-label "Oracle"]]
       " "
       [:a {:href "/sweep" :hx-get "/fragment/sweep" :hx-target "#main" :hx-push-url "/sweep"}
        "תמים " [:span.nav-label "Sweep"]]
       " "
       [:a {:href "/basin" :hx-get "/fragment/basin" :hx-target "#main" :hx-push-url "/basin"}
        "אגן " [:span.nav-label "Basin"]]
       " "]
      [:div#main content]]])))

(defn fragment
  "Render just the content fragment (for HTMX swaps)."
  [content]
  (str (h/html content)))
