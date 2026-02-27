(ns selah.mcp.tools
  (:require [clojure.pprint :as pp]))

;; ── Tool registry ──────────────────────────────────────────────────

(defonce schemas (atom {}))
(defmulti handle-tool (fn [name _args] name))

(defmacro deftool
  "Define an MCP tool. Registers schema and creates dispatch method."
  [tool-name description input-schema [args-binding] & body]
  (let [name-str (name tool-name)]
    `(do
       (swap! schemas assoc ~name-str
              {:name        ~name-str
               :description ~description
               :inputSchema ~input-schema})
       (defmethod handle-tool ~name-str [_# ~args-binding]
         (try
           (let [result# (do ~@body)]
             {:content [{:type "text" :text (str result#)}]})
           (catch Exception e#
             {:content [{:type "text" :text (str "Error: " (.getMessage e#))}]
              :isError true}))))))

(defn tool-list []
  (vec (vals @schemas)))

;; ── Tools ──────────────────────────────────────────────────────────

(deftool eval
  "Evaluate a Clojure expression in the Selah runtime."
  {:type "object"
   :properties {:code {:type "string" :description "Clojure expression to evaluate"}}
   :required ["code"]}
  [args]
  (let [code   (:code args)
        result (load-string code)]
    (with-out-str (pp/pprint result))))

(deftool inspect
  "Inspect a namespace or var in the running system."
  {:type "object"
   :properties {:target {:type "string" :description "Namespace or var to inspect (e.g. 'selah.http' or 'selah.http/state')"}}
   :required ["target"]}
  [args]
  (let [target (:target args)
        sym    (symbol target)]
    (if (namespace sym)
      (let [v (requiring-resolve sym)]
        (with-out-str
          (println "Var:" sym)
          (println "Value:")
          (pp/pprint (deref v))))
      (let [ns-sym sym
            _      (require ns-sym)
            ns-obj (the-ns ns-sym)
            publics (ns-publics ns-obj)]
        (with-out-str
          (println "Namespace:" ns-sym)
          (println "Public vars:")
          (doseq [[k _] (sort publics)]
            (println " " k)))))))

(deftool status
  "Show the current state of the Selah system."
  {:type "object" :properties {}}
  [_args]
  (with-out-str
    (println "=== Selah · סלה ===")
    (println)
    (let [nrepl-state @(requiring-resolve 'selah.nrepl/*state*)
          http-state  @(requiring-resolve 'selah.http/*state*)
          mcp-state   @(requiring-resolve 'selah.mcp.socket/*state*)]
      (println "nREPL:  port" (:port nrepl-state)
               (if (:server nrepl-state) "(running)" "(stopped)"))
      (println "HTTP:   port" (:port http-state)
               (if (:server http-state) "(running)" "(stopped)"))
      (println "MCP:    port" (:port mcp-state)
               (if (:running? mcp-state) "(running)" "(stopped)")))))
