(ns selah.main
  (:require [selah.nrepl :as nrepl]
            [selah.mcp.socket :as mcp-socket]
            [selah.http :as http]))

(defn -main [& _args]
  (println "[selah] Starting...")
  (nrepl/start!)
  (http/start!)
  (mcp-socket/start!)
  (println "[selah] Ready. סלה")
  (.addShutdownHook
   (Runtime/getRuntime)
   (Thread. (fn []
              (println "\n[selah] Shutting down...")
              (mcp-socket/stop!)
              (http/stop!)
              (nrepl/stop!))))
  (.join (Thread/currentThread)))
