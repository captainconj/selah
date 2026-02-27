(ns selah.text.locate
  "Map letter indices back to chapter locations in the text."
  (:require [selah.text.sefaria :as sefaria]
            [selah.text.normalize :as norm]))

(def book-chapters
  {"Genesis"     50
   "Exodus"      40
   "Leviticus"   27
   "Numbers"     36
   "Deuteronomy" 34})

(defn chapter-map
  "Build a chapter index for a book. Returns a sorted vector of
   {:chapter n :start-idx i :end-idx j :letters count}.
   Use with `idx->chapter` to locate any letter index."
  [book-name]
  (let [chapters (get book-chapters book-name)]
    (loop [ch     1
           offset 0
           result []]
      (if (> ch chapters)
        result
        (let [verses  (sefaria/fetch-chapter book-name ch)
              raw     (apply str (map norm/strip-html verses))
              letters (count (norm/letter-stream raw))]
          (recur (inc ch)
                 (+ offset letters)
                 (conj result {:chapter   ch
                               :start-idx offset
                               :end-idx   (+ offset letters -1)
                               :letters   letters})))))))

(defn idx->chapter
  "Given a chapter map and a 0-based letter index, return the chapter entry."
  [ch-map idx]
  (last (filter #(<= (:start-idx %) idx) ch-map)))

(defn locate
  "Locate a 0-based letter index in a book. Returns {:book :chapter :local-pos}."
  [book-name ch-map idx]
  (let [ch (idx->chapter ch-map idx)]
    {:book      book-name
     :chapter   (:chapter ch)
     :local-pos (- idx (:start-idx ch))}))

(defn locate-hits
  "Given ELS search hits and a chapter map, annotate each with its location."
  [book-name ch-map hits]
  (mapv (fn [{:keys [start] :as hit}]
          (merge hit (locate book-name ch-map start)))
        hits))

(comment
  (def gen-map (chapter-map "Genesis"))
  (idx->chapter gen-map 5)
  ;; => {:chapter 1, :start-idx 0, :end-idx 4117, :letters 4118}

  (locate "Genesis" gen-map 18854)
  ;; => {:book "Genesis", :chapter 16, :local-pos ...}
  )
