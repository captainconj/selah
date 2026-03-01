(ns selah.mcp.tools
  (:require [clojure.pprint :as pp]
            [selah.space.coords :as coords]
            [selah.space.project :as project]
            [selah.space.export :as export]))

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

;; ── Space tools ──────────────────────────────────────────────────

(deftool space-describe
  "Describe a Torah letter by position or 4D coordinate. Returns letter, gematria, verse reference, and coordinate."
  {:type "object"
   :properties {:position {:type "integer" :description "Letter position (0-based, 0–304849)"}
                :coord {:type "array" :items {:type "integer"}
                        :description "4D coordinate [a b c d] — alternative to position"}}
   :required []}
  [args]
  (let [pos (or (:position args)
                (when-let [[a b c d] (seq (:coord args))]
                  (coords/coord->idx a b c d)))]
    (when (nil? pos)
      (throw (ex-info "Provide either position or coord" {})))
    (with-out-str (pp/pprint (coords/describe pos)))))

(deftool space-slice
  "Query the Torah 4D space. Fix axes to slice, returns summary and first N results."
  {:type "object"
   :properties {:a {:type "integer" :description "Fix a-axis (0-6, the seven days)"}
                :b {:type "integer" :description "Fix b-axis (0-49, jubilee)"}
                :c {:type "integer" :description "Fix c-axis (0-12, one/love)"}
                :d {:type "integer" :description "Fix d-axis (0-66, understanding)"}
                :limit {:type "integer" :description "Max results to return (default 20)"}}
   :required []}
  [args]
  (let [spec (into {} (filter (fn [[k _]] (#{:a :b :c :d} k)) args))
        limit (get args :limit 20)
        positions (coords/slice spec)
        n (alength ^ints positions)
        s (coords/space)]
    (with-out-str
      (println (format "Slice %s — %,d positions" (pr-str spec) n))
      (println)
      (doseq [i (range (min limit n))]
        (let [pos (aget ^ints positions i)]
          (pp/pprint (coords/describe pos)))
        (println)))))

(deftool space-export
  "Export a slice of the Torah space as a point cloud file."
  {:type "object"
   :properties {:format {:type "string" :enum ["ply" "json" "binary"]
                         :description "Output format (default ply)"}
                :spatial {:type "array" :items {:type "string"}
                          :description "Spatial axes for projection, e.g. [\"b\" \"c\" \"d\"] (default [\"b\" \"c\" \"d\"])"}
                :color {:type "string" :enum ["letter" "gematria" "book" "day"]
                        :description "Color scheme (default letter)"}
                :a {:type "integer" :description "Fix a-axis (0-6)"}
                :b {:type "integer" :description "Fix b-axis (0-49)"}
                :c {:type "integer" :description "Fix c-axis (0-12)"}
                :d {:type "integer" :description "Fix d-axis (0-66)"}
                :path {:type "string" :description "Output path (auto-generated if omitted)"}}
   :required []}
  [args]
  (let [spec (into {} (filter (fn [[k _]] (#{:a :b :c :d} k)) args))
        fmt (keyword (get args :format "ply"))
        spatial-strs (get args :spatial ["b" "c" "d"])
        spatial (mapv keyword spatial-strs)
        color-mode (keyword (get args :color "letter"))
        s (coords/space)
        positions (if (empty? spec)
                    (int-array (range coords/total-letters))
                    (coords/slice spec))
        pts (if (= 3 (count spatial))
              (project/project-3d positions spatial)
              (project/project-2d positions spatial))
        cols (case color-mode
               :letter   (project/color-by-letter s positions)
               :gematria (project/color-by-gematria s positions)
               :book     (project/color-by-book s positions)
               :day      (project/color-by-day positions))
        ext (name fmt)
        path (or (:path args)
                 (format "data/export/torah-%s-%s.%s"
                         (if (empty? spec) "full" (pr-str spec))
                         (name color-mode) ext))
        writer (case fmt
                 :ply    export/write-ply!
                 :json   export/write-json!
                 :binary export/write-binary!)]
    (with-out-str
      (pp/pprint (writer path pts cols)))))
