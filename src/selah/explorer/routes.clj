(ns selah.explorer.routes
  "All HTTP route definitions. Pure routing: params → domain → response."
  (:require [selah.explorer :as exp]
            [selah.explorer.ui :as ui]
            [selah.explorer.sweep-ui :as sweep-ui]
            [selah.oracle :as oracle]
            [selah.basin :as basin]
            [selah.sweep :as sweep]
            [selah.translate :as translate]
            [clojure.data.json :as json]
            [clojure.string :as str]))

;; ── Response helpers ──────────────────────────────────────────

(defn- html [content]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (ui/layout content)})

(defn- fragment [content]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (ui/fragment content)})

(defn- json-ok [data]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"
             "Access-Control-Allow-Origin" "*"}
   :body (json/write-str data)})

(defn- json-err [status data]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/write-str data)})

;; ── URL helpers ──────────────────────────────────────────────

(defn- decode [s]
  (java.net.URLDecoder/decode (or s "") "UTF-8"))

(defn- parse-query [qs]
  (when qs
    (into {} (keep (fn [pair]
                     (let [[k v] (str/split pair #"=" 2)]
                       (when k [(keyword k) (decode (or v ""))])))
                   (str/split qs #"&")))))

(defn- parse-int [s default]
  (try (Integer/parseInt s) (catch Exception _ default)))

(defn- parse-vocab [params]
  (case (:vocab params "torah") "dict" :dict "voice" :voice "torah" :torah :torah))

;; ── Handler ──────────────────────────────────────────────────

(defn handler [req]
  (let [uri (:uri req)
        params (parse-query (:query-string req))]
    (cond
      ;; ── Explorer ──

      (= uri "/")
      (html (ui/home-content))

      (= uri "/fragment/home")
      (fragment (ui/home-content))

      (str/starts-with? uri "/word/")
      (let [w (decode (subs uri 6))
            d (exp/lookup-word w)]
        (if d
          (html (ui/word-profile d))
          {:status 404
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body (ui/layout [:div [:h1 "Not found: " w]])}))

      (str/starts-with? uri "/fragment/word/")
      (let [w (decode (subs uri 15))
            d (exp/lookup-word w)]
        (fragment (if d (ui/word-profile d)
                       [:div [:h1 "Not found: " w]])))

      (str/starts-with? uri "/level/")
      (let [n (parse-int (subs uri 7) 0)]
        (html (ui/level-view n (exp/level-words n))))

      (str/starts-with? uri "/fragment/level/")
      (let [n (parse-int (subs uri 16) 0)]
        (fragment (ui/level-view n (exp/level-words n))))

      (str/starts-with? uri "/gv/")
      (let [n (parse-int (subs uri 4) 0)]
        (html (ui/gv-view n (exp/gv-words n))))

      (str/starts-with? uri "/fragment/gv/")
      (let [n (parse-int (subs uri 13) 0)]
        (fragment (ui/gv-view n (exp/gv-words n))))

      (= uri "/fragment/search")
      (let [q (:q params "")]
        (fragment (ui/search-results q (exp/search-words q))))

      (= uri "/fragment/coincidence")
      (let [words (str/split (str/trim (:words params "")) #"\s+")]
        (fragment (ui/coincidence-view words)))

      (= uri "/search")
      (let [q (:q params "")]
        (html (ui/search-results q (exp/search-words q))))

      (= uri "/coincidence")
      (let [words (str/split (str/trim (:words params "")) #"\s+")]
        (html (ui/coincidence-view words)))

      ;; ── Oracle ──

      (= uri "/oracle")
      (html (ui/oracle-content))

      (= uri "/fragment/oracle")
      (fragment (ui/oracle-content))

      (= uri "/fragment/oracle/ask")
      (fragment (ui/oracle-ask-result (oracle/ask (:word params ""))))

      (= uri "/fragment/oracle/illuminate")
      (fragment (ui/oracle-illuminate-result (oracle/forward (:letters params ""))))

      (= uri "/fragment/oracle/question")
      (let [words (vec (remove empty? (str/split (str/trim (:q params "")) #"\s+")))]
        (fragment (ui/oracle-question-result (oracle/question words))))

      (= uri "/fragment/oracle/thummim")
      (fragment (ui/oracle-thummim-result
                 (oracle/thummim-menu (:word params "")
                                      {:max-illuminations 20
                                       :max-words 3
                                       :min-letters 2})))

      ;; ── Sweep ──

      (= uri "/sweep")
      (html (sweep-ui/staircase-overview))

      (= uri "/fragment/sweep")
      (fragment (sweep-ui/staircase-overview))

      (str/starts-with? uri "/sweep/step/")
      (html (sweep-ui/step-detail (parse-int (subs uri 12) 0)))

      (str/starts-with? uri "/fragment/sweep/step/")
      (fragment (sweep-ui/step-detail (parse-int (subs uri 21) 0)))

      (= uri "/sweep/forced")
      (html (sweep-ui/forced-view))

      (= uri "/fragment/sweep/forced")
      (fragment (sweep-ui/forced-view))

      (= uri "/sweep/extremes")
      (html (sweep-ui/extremes-view))

      (= uri "/fragment/sweep/extremes")
      (fragment (sweep-ui/extremes-view))

      (= uri "/sweep/distribution")
      (html (sweep-ui/distribution-view))

      (= uri "/fragment/sweep/distribution")
      (fragment (sweep-ui/distribution-view))

      ;; ── Basin ──

      (= uri "/basin")
      (html (ui/basin-content))

      (= uri "/fragment/basin")
      (fragment (ui/basin-content))

      (= uri "/fragment/basin/walk")
      (let [word (str/trim (:word params ""))
            max-steps (parse-int (:steps params "30") 30)
            flow? (= (:mode params) "flow")]
        (fragment (ui/basin-walk-result (basin/walk word max-steps {:skip-self? flow?}))))

      ;; ── Translation ──

      (= uri "/fragment/oracle/translate")
      (let [q (:q params "")]
        (if (translate/loaded?)
          (fragment (ui/oracle-translate-result (translate/ask q)))
          (fragment [:div.section [:p "Translation model not loaded. Run (translate/load!) at the REPL."]])))

      ;; ── JSON API ──

      (= uri "/api/oracle/translate")
      (let [q (:q params "")
            vocab (parse-vocab params)]
        (if (translate/loaded?)
          (json-ok (translate/ask q :vocab vocab))
          (json-err 503 {:error "Translation model not loaded"})))

      (= uri "/api/translate")
      (let [q (:q params "")]
        (if (translate/loaded?)
          (json-ok {:english q :hebrew (translate/translate q)})
          (json-err 503 {:error "Translation model not loaded"})))

      (= uri "/api/translate/reverse")
      (let [q (:q params "")]
        (if (translate/reverse-loaded?)
          (json-ok {:hebrew q :english (translate/translate-reverse q)})
          (json-err 503 {:error "Reverse translation model not loaded"})))

      (= uri "/api/oracle/ask")
      (let [result (oracle/ask (:word params ""))]
        (json-ok (update result :first-illumination #(mapv vec %))))

      (= uri "/api/oracle/illuminate")
      (let [result (oracle/forward (:letters params ""))]
        (json-ok (-> result
                     (dissoc :sample-readings)
                     (update :known-words
                             (fn [ws] (mapv #(update % :readers (comp vec sort)) ws))))))

      (= uri "/api/oracle/thummim")
      (let [word (:word params "")
            vocab (parse-vocab params)
            max-words (parse-int (:max_words params "3") 3)
            result (oracle/thummim-menu word {:vocab vocab
                                              :max-illuminations 50
                                              :max-words max-words
                                              :min-letters 2})]
        (json-ok (or result {:word word :error "no illuminations"})))

      (= uri "/api/oracle/parse-letters")
      (let [letters (:letters params "")
            vocab (parse-vocab params)
            max-words (parse-int (:max_words params "3") 3)
            result (oracle/parse-letters letters {:vocab vocab
                                                   :max-words max-words
                                                   :min-letters 2})]
        (json-ok {:letters letters
                  :vocab (if (keyword? vocab) (name vocab) "torah")
                  :phrase-count (count result)
                  :phrases result}))

      (= uri "/api/oracle/voice")
      (let [voice (oracle/oracle-voice)
            top-50 (take 50 (:vocabulary voice))]
        (json-ok {:knee (:knee voice)
                  :total-words (:total-words voice)
                  :vocabulary-size (:vocabulary-size voice)
                  :total-transitions (:total-transitions voice)
                  :top-50 top-50}))

      (= uri "/api/basin/walk")
      (let [word (str/trim (:word params ""))
            max-steps (parse-int (:steps params "30") 30)]
        (json-ok (basin/walk word max-steps)))

      (= uri "/api/basin/trace")
      (let [word (str/trim (:word params ""))
            max-steps (parse-int (:steps params "30") 30)]
        (json-ok {:word word :path (basin/trace word max-steps)}))

      (= uri "/api/basin/landscape")
      (let [words (vec (remove empty? (str/split (str/trim (:words params "")) #",")))]
        (json-ok (basin/landscape words)))

      (= uri "/api/sweep/fibonacci")
      (json-ok (or (sweep/fibonacci-analysis) {:error "sweep data not loaded"}))

      (= uri "/api/sweep/staircase")
      (let [analysis (sweep/fibonacci-analysis)]
        (json-ok (if analysis
                   {:staircase (:staircase analysis)
                    :gaps (:gaps analysis)
                    :max-fib-phrase (:max-fib-phrase analysis)}
                   {:error "sweep data not loaded"})))

      (str/starts-with? uri "/api/")
      (or (exp/api-handler req)
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/write-str {:error "not found"})})

      ;; ── Fallback ──

      :else
      {:status 404
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (ui/layout [:div [:h1 "404"] [:p "Not found."]])})))
