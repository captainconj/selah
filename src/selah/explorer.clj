(ns selah.explorer
  "The Explorer — interactive navigation of the 4D Torah space.

   Precomputes the full word census (frequency, gematria, preimage)
   and serves it as a web UI where you click through connections.

   Enter a word. See where it lives. Follow the threads."
  (:require [selah.text.oshb :as oshb]
            [selah.text.normalize :as norm]
            [selah.gematria :as gem]
            [selah.space.coords :as coords]
            [clojure.string :as str]
            [clojure.data.json :as json]))

;; ── State ────────────────────────────────────────────────────

(defonce ^:dynamic *state* (atom nil))

(defn state [] @*state*)

;; ── Number theory ────────────────────────────────────────────

(def fibs-seq (map first (iterate (fn [[a b]] [b (+ a b)]) [1 1])))
(def fib-set (set (take 30 fibs-seq)))
(defn fib? [n] (contains? fib-set n))
(defn fib-index [n]
  (first (keep-indexed (fn [i f] (when (= f n) (inc i))) (take 30 fibs-seq))))

(defn triangular-root [n]
  (when (pos? n)
    (let [disc (inc (* 8 n))]
      (when (pos? disc)
        (let [root (long (Math/sqrt disc))]
          (when (and (= disc (* root root)) (odd? root))
            (/ (dec root) 2)))))))

(defn prime? [n]
  (and (> n 1)
       (not-any? #(zero? (mod n %)) (range 2 (inc (long (Math/sqrt n)))))))

(defn factorize [n]
  (loop [n (long n) d 2 factors []]
    (cond
      (< n 2) factors
      (zero? (mod n d)) (recur (/ n d) d (conj factors d))
      :else (recur n (inc d) factors))))

(defn number-properties [n]
  (cond-> {}
    (fib? n)            (assoc :fibonacci (fib-index n))
    (triangular-root n) (assoc :triangular (triangular-root n))
    (let [r (long (Math/sqrt n))] (= n (* r r))) (assoc :square (long (Math/sqrt n)))
    (prime? n)          (assoc :prime true)
    (zero? (mod n 7))   (assoc :div-7 (/ n 7))
    (zero? (mod n 13))  (assoc :div-13 (/ n 13))
    (zero? (mod n 50))  (assoc :div-50 (/ n 50))
    (zero? (mod n 67))  (assoc :div-67 (/ n 67))
    (zero? (mod n 26))  (assoc :div-26 (/ n 26))))

;; ── Build the index ──────────────────────────────────────────

(defn build!
  "Precompute the entire word census. Takes ~6 seconds."
  []
  (println "[explorer] Building word index...")
  (let [t0 (System/currentTimeMillis)
        ;; Extract all words
        all-words (mapcat oshb/book-words
                          ["Genesis" "Exodus" "Leviticus" "Numbers" "Deuteronomy"])
        normalized (map (fn [w] (apply str (norm/letter-stream (:text w)))) all-words)
        freqs (frequencies normalized)
        ;; Build word table
        word-table (->> freqs
                        (mapv (fn [[w f]]
                                {:word w :freq f
                                 :gv (gem/word-value w)
                                 :len (count w)})))
        ;; Preimage for words 2-7 letters
        eligible (filter #(<= 2 (:len %) 7) word-table)
        preimage-counts (coords/preimage-indexed (mapv :word eligible))
        ;; Merge preimage into word table
        enriched (mapv (fn [w]
                         (assoc w :preimage (get preimage-counts (:word w) 0)))
                       word-table)
        ;; Build indices
        by-word (into {} (map (fn [w] [(:word w) w]) enriched))
        by-gv (group-by :gv enriched)
        by-preimage (group-by :preimage (filter #(pos? (:preimage %)) enriched))
        by-freq-rank (->> enriched (sort-by :freq >) (map-indexed (fn [i w] [(:word w) (inc i)])) (into {}))
        ;; Cross-reference index: for each preimage count, which GV words match?
        ;; preimage(A) = n, GV(B) = n
        xref-index (into {}
                         (for [[n words-at-n] by-preimage
                               :let [gv-targets (get by-gv n)]
                               :when (seq gv-targets)]
                           [n {:counted (mapv :word words-at-n)
                               :named (mapv :word gv-targets)}]))
        elapsed (- (System/currentTimeMillis) t0)]
    (reset! *state*
            {:words enriched
             :by-word by-word
             :by-gv by-gv
             :by-preimage by-preimage
             :by-freq-rank by-freq-rank
             :xref-index xref-index
             :total-words (count enriched)
             :total-tokens (reduce + (map :freq enriched))})
    (println (format "[explorer] Indexed %,d words in %.1fs" (count enriched) (/ elapsed 1000.0)))
    @*state*))

(defn index
  "Get the index, building if needed."
  []
  (or @*state* (build!)))

;; ── Query functions ──────────────────────────────────────────

(defn lookup-word
  "Full profile for a single word."
  [word]
  (let [idx (index)
        w (get (:by-word idx) word)]
    (when w
      (let [pre (:preimage w 0)
            gv (:gv w)
            ;; Cross-references: other words whose GV = this word's preimage count
            named-by (when (pos? pre)
                       (mapv :word (remove #(= (:word %) word) (get (:by-gv idx) pre))))
            ;; Reverse: words whose preimage count = this word's GV
            counts-to (mapv :word (remove #(= (:word %) word) (get (:by-preimage idx) gv)))
            ;; Same preimage level
            same-level (when (pos? pre)
                         (->> (get (:by-preimage idx) pre)
                              (remove #(= (:word %) word))
                              (sort-by :freq >)
                              (take 20)
                              (mapv #(select-keys % [:word :freq :gv]))))
            ;; Same GV
            same-gv (->> (get (:by-gv idx) gv)
                         (remove #(= (:word %) word))
                         (sort-by :freq >)
                         (take 20)
                         (mapv #(select-keys % [:word :freq :preimage])))]
        (merge w
               {:freq-rank (get (:by-freq-rank idx) word)
                :preimage-props (when (pos? pre) (number-properties pre))
                :gv-props (number-properties gv)
                :named-by named-by      ;; words whose GV = my preimage count
                :counts-to counts-to    ;; words whose preimage = my GV
                :same-level same-level
                :same-gv same-gv})))))

(defn search-words
  "Search for words matching a prefix or substring."
  [q]
  (let [idx (index)
        matches (->> (:words idx)
                     (filter #(str/includes? (:word %) q))
                     (sort-by :freq >)
                     (take 50)
                     (mapv #(select-keys % [:word :freq :gv :preimage :len])))]
    matches))

(defn level-words
  "All words at a given preimage count."
  [n]
  (let [idx (index)]
    (->> (get (:by-preimage idx) n)
         (sort-by :freq >)
         (mapv #(select-keys % [:word :freq :gv :len])))))

(defn gv-words
  "All words with a given gematria value."
  [n]
  (let [idx (index)]
    (->> (get (:by-gv idx) n)
         (sort-by :freq >)
         (mapv #(select-keys % [:word :freq :preimage :len])))))

(defn fiber-words
  "Get the a-fiber text and all words found at coordinates (b, c, d)."
  [b c d]
  (let [s (coords/space)
        fib (coords/fiber :a {:b b :c c :d d})
        text (apply str (map #(coords/letter-at s %) (seq fib)))
        gv (reduce + (map #(coords/gv-at s %) (seq fib)))
        v (coords/verse-at s (aget fib 0))
        ;; Find which indexed words appear in this fiber
        idx (index)
        found (->> (:words idx)
                   (filter #(and (<= 2 (:len %) 7)
                                 (str/includes? text (:word %))))
                   (sort-by :freq >)
                   (mapv #(select-keys % [:word :freq :gv :preimage])))]
    {:b b :c c :d d
     :text text :gv gv
     :verse (str (:book v) " " (:ch v) ":" (:vs v))
     :words found}))

;; ── HTTP routes ──────────────────────────────────────────────

(defn json-response [data]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"
             "Access-Control-Allow-Origin" "*"}
   :body (json/write-str data)})

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body body})

(defn parse-int [s]
  (try (Integer/parseInt s) (catch Exception _ nil)))

(defn api-handler
  "Handle explorer API requests."
  [req]
  (let [uri (:uri req)
        params (:query-string req)
        parse-params (fn [qs]
                       (when qs
                         (into {} (map #(let [[k v] (str/split % #"=" 2)]
                                          [(keyword k) (java.net.URLDecoder/decode (or v "") "UTF-8")])
                                       (str/split qs #"&")))))]
    (cond
      ;; Word lookup
      (str/starts-with? uri "/api/word/")
      (let [word (java.net.URLDecoder/decode (subs uri 10) "UTF-8")
            result (lookup-word word)]
        (if result
          (json-response result)
          (json-response {:error "not found" :word word})))

      ;; Search
      (= uri "/api/search")
      (let [p (parse-params params)
            q (:q p "")]
        (json-response (search-words q)))

      ;; Level (preimage count)
      (str/starts-with? uri "/api/level/")
      (let [n (parse-int (subs uri 11))]
        (json-response {:level n
                        :props (number-properties n)
                        :words (level-words n)}))

      ;; GV lookup
      (str/starts-with? uri "/api/gv/")
      (let [n (parse-int (subs uri 8))]
        (json-response {:gv n
                        :props (number-properties n)
                        :words (gv-words n)}))

      ;; Fiber lookup
      (str/starts-with? uri "/api/fiber/")
      (let [parts (str/split (subs uri 11) #"/")
            [b c d] (map parse-int parts)]
        (json-response (fiber-words b c d)))

      ;; Stats
      (= uri "/api/stats")
      (let [idx (index)]
        (json-response {:total-words (:total-words idx)
                        :total-tokens (:total-tokens idx)
                        :words-with-preimage (count (:by-preimage idx))
                        :distinct-preimage-counts (count (keys (:by-preimage idx)))
                        :cross-references (count (:xref-index idx))}))

      :else nil)))
