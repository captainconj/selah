(ns selah.main
  (:require [selah.nrepl :as nrepl]
            [selah.mcp.socket :as mcp-socket]
            [selah.http :as http]
            [selah.translate :as translate]
            [clojure.java.io :as io]))

(def ^:private pid-file
  (str (System/getProperty "user.home") "/.selah/engine.pid"))

(defn- write-pid! []
  (let [dir (io/file (str (System/getProperty "user.home") "/.selah"))]
    (when-not (.exists dir) (.mkdirs dir))
    (let [pid (.pid (java.lang.ProcessHandle/current))]
      (spit pid-file (str pid))
      (println (str "[selah] PID " pid " written to " pid-file)))))

(defn- clean-pid! []
  (let [f (io/file pid-file)]
    (when (.exists f) (.delete f))))

(defn- shutdown-hook! []
  (.addShutdownHook
   (Runtime/getRuntime)
   (Thread. (fn []
              (println "\n[selah] Shutting down...")
              (mcp-socket/stop!)
              (http/stop!)
              (nrepl/stop!)
              (clean-pid!)))))

(defn -main [& _args]
  (println "[selah] Starting...")
  (write-pid!)

  ;; Services
  (nrepl/start!)
  (http/start!)
  (mcp-socket/start!)

  ;; Translation models (optional, heavy)
  (when (.exists (java.io.File. "models/opus-mt-en-he/encoder_model.onnx"))
    (translate/load!))
  (when (.exists (java.io.File. "models/opus-mt-he-en/encoder_model.onnx"))
    (translate/load-reverse!))

  (println)
  (println "[selah] Ready. \u05E1\u05DC\u05D4")
  (println (str "[selah]   HTTP    http://localhost:" (:port (http/state))))
  (println (str "[selah]   nREPL   port " (or (:port (nrepl/state)) "?")))
  (println "[selah]   MCP     port 7889")
  (when (translate/loaded?)
    (println "[selah]   en→he translation model loaded"))
  (when (translate/reverse-loaded?)
    (println "[selah]   he→en translation model loaded"))

  (shutdown-hook!)
  (.join (Thread/currentThread)))
