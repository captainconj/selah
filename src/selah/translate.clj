(ns selah.translate
  "Bidirectional translation via MarianMT (ONNX Runtime).

   Forward:  English → Hebrew  (Helsinki-NLP/opus-mt-en-he, 77M params)
   Reverse:  Hebrew → English  (Helsinki-NLP/opus-mt-tc-big-he-en, 200M params)

   Both models share the same ONNX inference kernel, parameterized by
   a model-state map with per-model constants from config.json.

   Usage:
     (load!)                              ;; load en→he model
     (load-reverse!)                      ;; load he→en model (optional)
     (translate \"Who are you?\")         ;; → Hebrew string
     (translate-reverse \"שלום\")          ;; → English string
     (ask \"Who are you?\")               ;; → ranked oracle readings"
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [selah.oracle :as oracle]
            [selah.gematria :as g]
            [selah.dict :as dict]
            [selah.text.normalize :as norm])
  (:import [ai.onnxruntime OrtEnvironment OrtSession OnnxTensor]
           [ai.djl.sentencepiece SpTokenizer SpProcessor]
           [java.nio LongBuffer]
           [java.nio.file Paths]))

;; ── State ───────────────────────────────────────────────────────

(defonce ^:dynamic *state*
  (atom {:loaded? false}))

(defonce ^:dynamic *reverse-state*
  (atom {:loaded? false}))

(defn state [] @*state*)

;; ── Constants ───────────────────────────────────────────────────

(def ^:private unk-token-id 1)        ;; <unk> — same across MarianMT
(def ^:private max-length 64)         ;; max output tokens

;; ── Model loading ──────────────────────────────────────────────

(defn- load-vocab
  "Load vocab.json → {piece-string → token-id}."
  [model-dir]
  (with-open [r (io/reader (io/file model-dir "vocab.json"))]
    (json/read r :key-fn identity)))

(defn- load-config
  "Parse config.json for per-model constants."
  [model-dir]
  (with-open [r (io/reader (io/file model-dir "config.json"))]
    (let [cfg (json/read r :key-fn keyword)]
      {:eos-token-id   (long (or (:eos_token_id cfg) 0))
       :pad-token-id   (long (or (:decoder_start_token_id cfg) (:pad_token_id cfg) 0))
       :vocab-size     (long (or (:decoder_vocab_size cfg) (:vocab_size cfg) 65536))})))

(defn- load-model!
  "Load a MarianMT ONNX model into a state map. Returns the map."
  [model-dir label]
  (println (str "[translate] Loading " label " from " model-dir " ..."))
  (let [env    (OrtEnvironment/getEnvironment)
        enc    (.createSession env (str model-dir "/encoder_model.onnx"))
        dec    (.createSession env (str model-dir "/decoder_model.onnx"))
        sp-path (Paths/get (str model-dir "/source.spm") (into-array String []))
        tok    (SpTokenizer. sp-path)
        spm    (.getProcessor tok)
        vocab  (load-vocab model-dir)
        id->p  (into {} (map (fn [[k v]] [(long v) k]) vocab))
        config (load-config model-dir)]
    (println (str "[translate] " label " loaded. Vocab: " (count vocab)
                  " pieces. EOS=" (:eos-token-id config)
                  " PAD=" (:pad-token-id config)
                  " vocab-size=" (:vocab-size config)))
    (merge config
           {:env env
            :encoder enc
            :decoder dec
            :tokenizer tok
            :spm spm
            :piece->id vocab
            :id->piece id->p
            :loaded? true})))

;; ── Parameterized inference kernel ─────────────────────────────
;;
;; Every function takes `ms` (model-state map) instead of reading
;; a global atom. This lets forward and reverse models share code.

(defn- long-tensor
  "Create an OnnxTensor [1, n] of int64 from a sequence of longs."
  [^OrtEnvironment env ids]
  (let [n (count ids)
        buf (LongBuffer/allocate n)]
    (doseq [id ids]
      (.put buf (long id)))
    (.flip buf)
    (OnnxTensor/createTensor env buf (long-array [1 n]))))

(defn- model-tokenize
  "Text → vector of token IDs (including EOS)."
  [ms text]
  (let [{:keys [^SpProcessor spm piece->id eos-token-id]} ms
        pieces (.tokenize spm ^String text)]
    (conj (mapv (fn [^String piece]
                  (long (get piece->id piece unk-token-id)))
                pieces)
          eos-token-id)))

(defn- model-detokenize
  "Vector of token IDs → text string."
  [ms token-ids]
  (let [{:keys [id->piece eos-token-id pad-token-id]} ms
        pieces (keep (fn [id]
                       (when-not (or (= id eos-token-id) (= id pad-token-id))
                         (get id->piece id)))
                     token-ids)]
    (when (seq pieces)
      (-> (apply str pieces)
          (str/replace #"▁" " ")
          str/trim))))

(defn- model-run-encoder
  "Encoder pass: token IDs → {:result :mask}. Caller must close :result."
  [ms input-ids]
  (let [{:keys [^OrtEnvironment env ^OrtSession encoder]} ms
        n (count input-ids)
        mask (vec (repeat n 1))
        in-ids (long-tensor env input-ids)
        in-mask (long-tensor env mask)]
    (try
      (let [result (.run encoder {"input_ids" in-ids "attention_mask" in-mask})]
        {:result result :mask mask})
      (finally
        (.close in-ids)
        (.close in-mask)))))

(defn- argmax
  "Index of maximum value in float array starting at offset, scanning stride elements."
  ^long [^floats arr ^long offset ^long stride]
  (loop [i (long 1) best (long 0) best-val (aget arr offset)]
    (if (>= i stride)
      best
      (let [v (aget arr (+ offset i))]
        (if (> v best-val)
          (recur (inc i) i v)
          (recur (inc i) best best-val))))))

(defn- model-decoder-step
  "Single decoder step: tokens so far → next token ID."
  [^OrtEnvironment env ^OrtSession decoder vocab-size encoder-hidden mask tokens]
  (let [dec-ids (long-tensor env tokens)
        enc-mask (long-tensor env mask)]
    (try
      (let [inputs {"input_ids" dec-ids
                    "encoder_attention_mask" enc-mask
                    "encoder_hidden_states" encoder-hidden}
            dec-result (.run decoder inputs)
            logits-tensor ^OnnxTensor (-> (.get dec-result "logits") (.get))
            logits-buf (.getFloatBuffer logits-tensor)
            logits-arr (float-array (.remaining logits-buf))
            _ (.get logits-buf logits-arr)
            last-offset (* (long (dec (count tokens))) (long vocab-size))
            next-token (argmax logits-arr last-offset vocab-size)]
        (.close dec-result)
        next-token)
      (finally
        (.close dec-ids)
        (.close enc-mask)))))

(defn- model-decode-greedy
  "Autoregressive greedy decoding. O(n²) but fast for short outputs."
  [ms encoder-result]
  (let [{:keys [^OrtEnvironment env ^OrtSession decoder
                eos-token-id pad-token-id vocab-size]} ms
        {:keys [result mask]} encoder-result
        encoder-hidden (-> (.get ^ai.onnxruntime.OrtSession$Result result "last_hidden_state")
                           (.get))]
    (try
      (loop [tokens [pad-token-id]
             step 0]
        (if (or (and (> step 0) (= (peek tokens) eos-token-id))
                (>= step max-length))
          (let [raw (subvec tokens 1)]
            (if (and (seq raw) (= (peek raw) eos-token-id))
              (subvec raw 0 (dec (count raw)))
              raw))
          (let [next-token (model-decoder-step env decoder vocab-size
                                               encoder-hidden mask tokens)]
            (recur (conj tokens (long next-token)) (inc step)))))
      (finally
        (.close ^ai.onnxruntime.OrtSession$Result result)))))

(defn- model-translate
  "Full translation pipeline for a model-state map."
  [ms text]
  (let [input-ids (model-tokenize ms text)
        enc-result (model-run-encoder ms input-ids)
        output-ids (model-decode-greedy ms enc-result)]
    (model-detokenize ms output-ids)))

;; ── Public API — Forward (en→he) ──────────────────────────────

(defn load!
  "Load English→Hebrew MarianMT ONNX model. Call once at startup."
  ([] (load! "models/opus-mt-en-he"))
  ([model-dir]
   (reset! *state* (load-model! model-dir "en→he"))
   :ok))

(defn loaded? [] (:loaded? @*state*))

(defn translate
  "English → Hebrew. Returns the Hebrew string."
  [text]
  (when-not (loaded?)
    (throw (ex-info "Translation model not loaded. Call (translate/load!) first." {})))
  (model-translate @*state* text))

(defn translate-batch
  "Translate multiple English texts to Hebrew."
  [texts]
  (mapv translate texts))

;; ── Public API — Reverse (he→en) ──────────────────────────────

(defn load-reverse!
  "Load Hebrew→English MarianMT ONNX model. Optional — enriches oracle output."
  ([] (load-reverse! "models/opus-mt-he-en"))
  ([model-dir]
   (reset! *reverse-state* (load-model! model-dir "he→en"))
   :ok))

(defn reverse-loaded? [] (:loaded? @*reverse-state*))

(defn translate-reverse
  "Hebrew → English. Returns the English string, or nil if model not loaded."
  [text]
  (when (reverse-loaded?)
    (model-translate @*reverse-state* text)))

(defn batch-translate-reverse
  "Translate multiple Hebrew words to English. Deduplicates.
   Sequential — OrtSession is not thread-safe.
   Returns {hebrew-word → english-string}."
  [hebrew-words]
  (if-not (reverse-loaded?)
    {}
    (let [unique (vec (distinct hebrew-words))
          ms     @*reverse-state*]
      (into {} (mapv (fn [w] [w (model-translate ms w)]) unique)))))

;; ── Oracle integration ─────────────────────────────────────────

(defn- hebrew-letters
  "Strip everything except Hebrew consonants from a string."
  [^String s]
  (apply str (norm/letter-stream s)))

(defn ask
  "English → Hebrew → Oracle readings, ranked and enriched with English.
   Uses static Torah-English cache for back-translation (no ONNX at query time).
   Returns {:english :hebrew :hebrew-letters :gv :readings :synthesis}"
  [english-text & {:keys [vocab max-words min-letters]
                   :or {vocab :torah max-words 4 min-letters 2}}]
  (let [hebrew  (translate english-text)
        letters (when hebrew (hebrew-letters hebrew))
        raw     (when (and letters (pos? (count letters)))
                  (oracle/parse-letters letters {:vocab vocab
                                                 :max-words max-words
                                                 :min-letters min-letters}))
        ranked  (when (seq raw)
                  (oracle/rank-phrases raw letters))
        synthesis (when (seq ranked)
                    {:top (vec (take 3 ranked))
                     :tier-counts (or (:tier-counts (meta ranked))
                                      (frequencies (map :tier ranked)))
                     :self-reading (first (filter :self? ranked))})]
    (let [hebrew-words (when hebrew
                        (remove empty?
                                (str/split hebrew #"[^\u05D0-\u05EA]+")))
          per-word-lit (when (seq hebrew-words)
                         (mapv (fn [w]
                                 {:word w
                                  :positions (vec (set (mapcat #(get oracle/letter-index %)
                                                               (seq w))))})
                               hebrew-words))]
      {:english english-text
       :hebrew (or hebrew "")
       :hebrew-letters (or letters "")
       :gv (if letters (g/word-value letters) 0)
       :readings (or ranked [])
       :synthesis synthesis
       :per-word-lit (or per-word-lit [])
       :lit-positions (when letters
                        (set (mapcat #(get oracle/letter-index %)
                                     (distinct (seq letters)))))})))
