(ns selah.nrepl
  (:require [nrepl.server :as nrepl-server]))

(defonce ^:dynamic *state* (atom {:server nil :port nil}))

(defn state [] @*state*)

(defn start!
  "Start nREPL server. Writes port to .nrepl-port for editor integration."
  [& {:keys [port] :or {port 0}}]
  (let [server (nrepl-server/start-server :port port)
        port   (:port server)]
    (spit ".nrepl-port" (str port))
    (swap! *state* assoc :server server :port port)
    (println (str "[nrepl] Listening on port " port))
    server))

(defn stop! []
  (when-let [server (:server @*state*)]
    (nrepl-server/stop-server server)
    (swap! *state* assoc :server nil :port nil)
    (println "[nrepl] Stopped.")))
