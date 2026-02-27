(ns selah.mcp.socket
  (:require [clojure.data.json :as json]
            [selah.mcp.server :as server])
  (:import [java.net ServerSocket]
           [java.io BufferedReader InputStreamReader PrintWriter]))

(defonce ^:dynamic *state* (atom {:server nil :port 7889 :running? false}))

(defn state [] @*state*)

(defn- read-message [^BufferedReader reader]
  (try
    (when-let [line (.readLine reader)]
      (json/read-str line :key-fn keyword))
    (catch Exception _ nil)))

(defn- write-message [^PrintWriter writer msg]
  (.println writer (json/write-str msg))
  (.flush writer))

(defn- handle-client [client]
  (try
    (let [reader (BufferedReader. (InputStreamReader. (.getInputStream client)))
          writer (PrintWriter. (.getOutputStream client) true)]
      (loop []
        (when-let [request (read-message reader)]
          (when-let [response (server/dispatch request)]
            (write-message writer response))
          (recur))))
    (catch Exception e
      (when (:running? @*state*)
        (println "[mcp-socket] Client error:" (.getMessage e))))
    (finally
      (.close client))))

(defn start!
  [& {:keys [port] :or {port 7889}}]
  (let [ss (ServerSocket. port)]
    (swap! *state* assoc :server ss :port port :running? true)
    (println (str "[mcp] Socket server on port " port))
    (future
      (while (:running? @*state*)
        (try
          (let [client (.accept ss)]
            (future (handle-client client)))
          (catch Exception e
            (when (:running? @*state*)
              (println "[mcp-socket] Accept error:" (.getMessage e)))))))))

(defn stop! []
  (swap! *state* assoc :running? false)
  (when-let [server (:server @*state*)]
    (try (.close server) (catch Exception _)))
  (swap! *state* assoc :server nil)
  (println "[mcp] Socket server stopped."))
