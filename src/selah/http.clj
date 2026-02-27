(ns selah.http
  (:require [org.httpkit.server :as hk]
            [clojure.data.json :as json]))

(defonce ^:dynamic *state* (atom {:server nil :port 8099}))

(defn state [] @*state*)

(defn handler [req]
  (case (:uri req)
    "/" {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/write-str {:status "ok"
                                :project "selah"})}
    {:status 404
     :headers {"Content-Type" "application/json"}
     :body (json/write-str {:error "not found"})}))

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
