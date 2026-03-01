(ns selah.explorer.ui
  "Hiccup views for the Torah Explorer.

   Server-rendered HTML with HTMX for navigation.
   Click a word → see its profile. Click a connection → follow it.
   The structure reveals itself as you move through it."
  (:require [hiccup2.core :as h]
            [hiccup.util :as hu]
            [selah.explorer :as exp]
            [selah.dict :as dict]
            [clojure.string :as str]))

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
   (let [meaning (dict/translate word)]
     [:a {:href (str "/word/" (hu/url-encode word))
          :hx-get (str "/fragment/word/" (hu/url-encode word))
          :hx-target "#main"
          :hx-push-url (str "/word/" (hu/url-encode word))
          :class (str "word-link" (when extra-class (str " " extra-class)))
          :title (or meaning "")}
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
                named-by counts-to same-level same-gv]} word-data]
    [:div#word-profile
     [:div.word-header
      [:h1.hebrew word]
      (when-let [meaning (dict/translate word)]
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
           [:td.meaning-col (dict/translate word)]
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
           [:td.meaning-col (dict/translate word)]
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
        [:td.meaning-col (dict/translate word)]
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
           [:td.meaning-col (dict/translate word)]
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
      [:script {:src "https://unpkg.com/htmx.org@2.0.4"}]]
     [:body
      [:nav
       [:a {:href "/" :hx-get "/fragment/home" :hx-target "#main" :hx-push-url "/"} "סלה"]
       " "]
      [:div#main content]]])))

(defn fragment
  "Render just the content fragment (for HTMX swaps)."
  [content]
  (str (h/html content)))
