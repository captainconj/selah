(ns selah.http
  "HTTP server kernel. Start/stop. Delegates routing to explorer.routes."
  (:require [org.httpkit.server :as hk]
            [selah.explorer.routes :as routes]))

(defonce ^:dynamic *state* (atom {:server nil :port 8099}))

(defn state [] @*state*)

(defn start!
  [& {:keys [port] :or {port 8099}}]
  (let [server (hk/run-server #'routes/handler {:port port})]
    (swap! *state* assoc :server server :port port)
    (println (str "[http] Listening on port " port))
    server))

(defn stop! []
  (when-let [server (:server @*state*)]
    (server)
    (swap! *state* assoc :server nil)
    (println "[http] Stopped.")))
