(ns selah.mcp.server
  (:require [selah.mcp.tools :as tools]))

(def server-info
  {:name    "selah"
   :version "0.1.0"})

(defn success [id result]
  {:jsonrpc "2.0" :id id :result result})

(defn error-response [id code message]
  {:jsonrpc "2.0" :id id
   :error {:code code :message message}})

(defn dispatch [{:keys [method params id]}]
  (case method
    "initialize"
    (success id {:protocolVersion "2024-11-05"
                 :serverInfo      server-info
                 :capabilities    {:tools {}}})

    "notifications/initialized" nil

    "tools/list"
    (success id {:tools (tools/tool-list)})

    "tools/call"
    (let [result (tools/handle-tool (:name params)
                                    (or (:arguments params) {}))]
      (success id result))

    "ping"
    (success id {})

    (error-response id -32601 (str "Method not found: " method))))
