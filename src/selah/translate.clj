(ns selah.translate
  "English → Hebrew translation via MarianMT (ONNX Runtime).

   The model is Helsinki-NLP/opus-mt-en-he, a 77M-parameter
   encoder-decoder transformer. Exported to ONNX, loaded once,
   runs on CPU via Microsoft's ONNX Runtime.

   Tokenization: SentencePiece splits text into subword pieces,
   vocab.json maps pieces to token IDs. The two mappings are
   independent — SentencePiece's internal IDs ≠ model token IDs.

   Usage:
     (load!)                         ;; load model + tokenizer
     (translate \"Who are you?\")    ;; → Hebrew string
     (ask \"Who are you?\")          ;; → {:hebrew ... :readings ...}"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [selah.oracle :as oracle]
            [selah.gematria :as g]
            [selah.text.normalize :as norm])
  (:import [ai.onnxruntime OrtEnvironment OrtSession OnnxTensor]
           [ai.djl.sentencepiece SpTokenizer SpProcessor]
           [java.nio LongBuffer]
           [java.nio.file Paths]))

;; ── State ───────────────────────────────────────────────────────

(defonce ^:dynamic *state*
  (atom {:env nil
         :encoder nil
         :decoder nil
         :tokenizer nil       ;; SpTokenizer (owns native resources)
         :spm nil             ;; SpProcessor (from tokenizer)
         :piece->id nil       ;; vocab.json: piece string → token ID
         :id->piece nil       ;; reverse: token ID → piece string
         :loaded? false}))

(defn state [] @*state*)

;; ── Constants ───────────────────────────────────────────────────

(def ^:private eos-token-id 0)        ;; </s>
(def ^:private pad-token-id 65838)    ;; <pad> = decoder start
(def ^:private unk-token-id 1)        ;; <unk>
(def ^:private max-length 64)         ;; max output tokens
(def ^:private vocab-size 65839)

;; ── Tokenization ────────────────────────────────────────────────

(defn- load-vocab
  "Load vocab.json → {piece-string → token-id}."
  [model-dir]
  (with-open [r (io/reader (io/file model-dir "vocab.json"))]
    (json/read r :key-fn identity)))

(defn- tokenize
  "English text → vector of token IDs (including EOS).
   SentencePiece segments into pieces, vocab.json maps to model IDs."
  [text]
  (let [{:keys [^SpProcessor spm piece->id]} @*state*
        pieces (.tokenize spm ^String text)]
    (conj (mapv (fn [^String piece]
                  (long (get piece->id piece unk-token-id)))
                pieces)
          eos-token-id)))

(defn- detokenize
  "Vector of token IDs → Hebrew text.
   Reverse vocab.json to get pieces, join with SentencePiece conventions:
   ▁ prefix = word boundary (becomes space), first ▁ is stripped."
  [token-ids]
  (let [{:keys [id->piece]} @*state*
        pieces (keep (fn [id]
                       (when-not (or (= id eos-token-id) (= id pad-token-id))
                         (get id->piece id)))
                     token-ids)]
    (when (seq pieces)
      (-> (apply str pieces)
          (str/replace #"▁" " ")
          str/trim))))

;; ── ONNX Inference ──────────────────────────────────────────────

(defn- long-tensor
  "Create an OnnxTensor [1, n] of int64 from a sequence of longs."
  [^OrtEnvironment env ids]
  (let [n (count ids)
        buf (LongBuffer/allocate n)]
    (doseq [id ids]
      (.put buf (long id)))
    (.flip buf)
    (OnnxTensor/createTensor env buf (long-array [1 n]))))

(defn- run-encoder
  "Encoder pass: token IDs → {:result OrtSession.Result, :seq-len n, :mask [1 1 ...]}.
   Caller must close :result when done."
  [input-ids]
  (let [{:keys [^OrtEnvironment env ^OrtSession encoder]} @*state*
        n (count input-ids)
        mask (vec (repeat n 1))
        in-ids (long-tensor env input-ids)
        in-mask (long-tensor env mask)]
    (try
      (let [result (.run encoder {"input_ids" in-ids "attention_mask" in-mask})]
        {:result result :seq-len n :mask mask})
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

(defn- decoder-step
  "Single decoder step: tokens so far → next token ID.
   Creates and closes its own tensors."
  [^OrtEnvironment env ^OrtSession decoder encoder-hidden mask tokens]
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

(defn- decode-greedy
  "Autoregressive greedy decoding using decoder_model.onnx (no KV cache).
   Runs the full decoder sequence each step — O(n²) but fast for short outputs."
  [encoder-result]
  (let [{:keys [^OrtEnvironment env ^OrtSession decoder]} @*state*
        {:keys [result mask]} encoder-result
        encoder-hidden (-> (.get ^ai.onnxruntime.OrtSession$Result result "last_hidden_state")
                           (.get))]  ;; unwrap Optional
    (try
      (loop [tokens [pad-token-id]
             step 0]
        (if (or (and (> step 0) (= (peek tokens) eos-token-id))
                (>= step max-length))
          ;; Strip start token and trailing EOS
          (let [raw (subvec tokens 1)]
            (if (and (seq raw) (= (peek raw) eos-token-id))
              (subvec raw 0 (dec (count raw)))
              raw))
          ;; One decoder step
          (let [next-token (decoder-step env decoder encoder-hidden mask tokens)]
            (recur (conj tokens (long next-token)) (inc step)))))
      (finally
        (.close ^ai.onnxruntime.OrtSession$Result result)))))

;; ── Public API ──────────────────────────────────────────────────

(defn load!
  "Load MarianMT ONNX models and tokenizer. Call once at startup."
  ([] (load! "models/opus-mt-en-he"))
  ([model-dir]
   (println "[translate] Loading MarianMT from" model-dir "...")
   (let [env (OrtEnvironment/getEnvironment)
         encoder (.createSession env (str model-dir "/encoder_model.onnx"))
         decoder (.createSession env (str model-dir "/decoder_model.onnx"))
         sp-path (Paths/get (str model-dir "/source.spm") (into-array String []))
         tokenizer (SpTokenizer. sp-path)
         spm (.getProcessor tokenizer)
         vocab (load-vocab model-dir)
         id->piece (into {} (map (fn [[k v]] [(long v) k]) vocab))]
     (reset! *state*
             {:env env
              :encoder encoder
              :decoder decoder
              :tokenizer tokenizer
              :spm spm
              :piece->id vocab
              :id->piece id->piece
              :loaded? true})
     (println "[translate] Loaded. Vocab:" (count vocab) "pieces.")
     :ok)))

(defn loaded? [] (:loaded? @*state*))

(defn translate
  "English → Hebrew. Returns the Hebrew string."
  [text]
  (when-not (loaded?)
    (throw (ex-info "Translation model not loaded. Call (translate/load!) first." {})))
  (let [input-ids (tokenize text)
        encoder-result (run-encoder input-ids)
        output-ids (decode-greedy encoder-result)]
    (detokenize output-ids)))

(defn translate-batch
  "Translate multiple English texts to Hebrew. Returns a vector of Hebrew strings."
  [texts]
  (mapv translate texts))

(defn- hebrew-letters
  "Strip everything except Hebrew consonants from a string."
  [^String s]
  (apply str (norm/letter-stream s)))

(defn ask
  "English → Hebrew → Oracle readings.
   Returns {:english ... :hebrew ... :hebrew-letters ... :gv N :readings [...]}"
  [english-text & {:keys [vocab max-words min-letters]
                   :or {vocab :torah max-words 4 min-letters 2}}]
  (let [hebrew (translate english-text)
        letters (when hebrew (hebrew-letters hebrew))
        readings (when (and letters (pos? (count letters)))
                   (oracle/parse-letters letters {:vocab vocab
                                                  :max-words max-words
                                                  :min-letters min-letters}))]
    {:english english-text
     :hebrew (or hebrew "")
     :hebrew-letters (or letters "")
     :gv (if letters (g/word-value letters) 0)
     :readings (or readings [])}))
