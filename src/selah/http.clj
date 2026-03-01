(ns selah.http
  (:require [org.httpkit.server :as hk]
            [clojure.data.json :as json]
            [selah.explorer :as exp]
            [selah.explorer.ui :as ui]
            [clojure.string :as str]))

(defonce ^:dynamic *state* (atom {:server nil :port 8099}))

(defn state [] @*state*)

(defn- decode [s]
  (java.net.URLDecoder/decode (or s "") "UTF-8"))

(defn- parse-query [qs]
  (when qs
    (into {} (keep (fn [pair]
                     (let [[k v] (str/split pair #"=" 2)]
                       (when k [(keyword k) (decode (or v ""))])))
                   (str/split qs #"&")))))

(defn handler [req]
  (let [uri (:uri req)
        params (parse-query (:query-string req))]
    (cond
      ;; ── Explorer UI ──

      ;; Home page
      (= uri "/")
      {:status 200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (ui/layout (ui/home-content))}

      ;; Fragment: home (HTMX)
      (= uri "/fragment/home")
      {:status 200
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (ui/fragment (ui/home-content))}

      ;; Word page (full page)
      (str/starts-with? uri "/word/")
      (let [word (decode (subs uri 6))
            data (exp/lookup-word word)]
        (if data
          {:status 200
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body (ui/layout (ui/word-profile data))}
          {:status 404
           :headers {"Content-Type" "text/html; charset=utf-8"}
           :body (ui/layout [:div [:h1 "Not found: " word]])}))

      ;; Fragment: word (HTMX)
      (str/starts-with? uri "/fragment/word/")
      (let [word (decode (subs uri 15))
            data (exp/lookup-word word)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (if data
                 (ui/fragment (ui/word-profile data))
                 (ui/fragment [:div [:h1 "Not found: " word]]))})

      ;; Level page
      (str/starts-with? uri "/level/")
      (let [n (try (Integer/parseInt (subs uri 7)) (catch Exception _ 0))
            words (exp/level-words n)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/layout (ui/level-view n words))})

      ;; Fragment: level
      (str/starts-with? uri "/fragment/level/")
      (let [n (try (Integer/parseInt (subs uri 16)) (catch Exception _ 0))
            words (exp/level-words n)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/fragment (ui/level-view n words))})

      ;; GV page
      (str/starts-with? uri "/gv/")
      (let [n (try (Integer/parseInt (subs uri 4)) (catch Exception _ 0))
            words (exp/gv-words n)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/layout (ui/gv-view n words))})

      ;; Fragment: gv
      (str/starts-with? uri "/fragment/gv/")
      (let [n (try (Integer/parseInt (subs uri 13)) (catch Exception _ 0))
            words (exp/gv-words n)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/fragment (ui/gv-view n words))})

      ;; Search
      (= uri "/fragment/search")
      (let [q (:q params "")
            results (exp/search-words q)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/fragment (ui/search-results q results))})

      ;; Coincidence — multiple words
      (= uri "/fragment/coincidence")
      (let [raw (:words params "")
            words (str/split (str/trim raw) #"\s+")]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/fragment (ui/coincidence-view words))})

      ;; Full page search
      (= uri "/search")
      (let [q (:q params "")
            results (exp/search-words q)]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/layout (ui/search-results q results))})

      ;; Full page coincidence
      (= uri "/coincidence")
      (let [raw (:words params "")
            words (str/split (str/trim raw) #"\s+")]
        {:status 200
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (ui/layout (ui/coincidence-view words))})

      ;; ── JSON API ──

      (str/starts-with? uri "/api/")
      (or (exp/api-handler req)
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/write-str {:error "not found"})})

      ;; Fallback
      :else
      {:status 404
       :headers {"Content-Type" "text/html; charset=utf-8"}
       :body (ui/layout [:div [:h1 "404"] [:p "Not found."]])})))

(defn start!
  [& {:keys [port] :or {port 8099}}]
  (let [server (hk/run-server handler {:port port})]
    (swap! *state* assoc :server server :port port)
    (println (str "[http] Listening on port " port))
    server))

(defn stop! []
  (when-let [server (:server @*state*)]
    (server)
    (swap! *state* assoc :server nil)
    (println "[http] Stopped.")))
