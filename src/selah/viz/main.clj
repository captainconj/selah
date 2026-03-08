(ns selah.viz.main
  "Standalone visualizer entry point — no HTTP, nREPL, or MCP."
  (:require [selah.viz.gl :as gl]))

(defn -main [& _args]
  (println "[selah] Starting visualizer (standalone)...")
  (gl/start!)
  (.join (Thread/currentThread)))
