(ns user
  (:require [selah.main :as main]))

(comment
  ;; Start everything from REPL
  (main/-main)

  ;; Or start individually
  (require '[selah.nrepl :as nrepl])
  (require '[selah.http :as http])
  (require '[selah.mcp.socket :as mcp])

  (nrepl/start!)
  (http/start!)
  (mcp/start!)

  (nrepl/stop!)
  (http/stop!)
  (mcp/stop!)
  ;;
  )
