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

(defn -main [& _args]
  (println "[selah] Starting...")
  (write-pid!)
  (nrepl/start!)
  (http/start!)
  (mcp-socket/start!)
  (when (.exists (java.io.File. "models/opus-mt-en-he/encoder_model.onnx"))
    (translate/load!))
  (println "[selah] Ready. סלה")
  (.addShutdownHook
   (Runtime/getRuntime)
   (Thread. (fn []
              (println "\n[selah] Shutting down...")
              (mcp-socket/stop!)
              (http/stop!)
              (nrepl/stop!)
              (clean-pid!))))
  (.join (Thread/currentThread)))
