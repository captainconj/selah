(require '[selah.oracle :as o]
         '[selah.dict :as dict]
         '[selah.gematria :as g])

;; Categorize all words
(def categories
  {"names" #{"משה" "אהרן" "יעקב" "יצחק" "אברהם" "יוסף" "נח" "חוה" "אדם" "שרה" "רחל" "לאה" "ישראל" "פרעה" "קהת"}
   "divine" #{"יהוה" "אלהים" "אדני" "כרוב" "משכן" "מזבח" "ארון" "שבת" "קרבן" "כהן" "משיח" "אהל" "פרכת" "כפרת" "שמן" "יין" "לחם" "זהב" "כסף" "נחשת"}
   "theological" #{"צדק" "צדקה" "חסד" "חכמה" "דעת" "כבוד" "רחמים" "שכינה" "תורה" "אהבה" "בינה" "אמת" "שלום" "ברית" "חיים" "עולם" "משפט" "מועד" "חן" "קדוש" "תמים"}
   "already-deep" #{"כבש" "שכב" "את" "אל" "לא" "שכרה" "כשרה"}})

(def already-deep (get categories "already-deep"))

(defn categorize [w]
  (cond
    (contains? (get categories "names") w) :names
    (contains? (get categories "divine") w) :divine
    (contains? (get categories "theological") w) :theological
    :else :other))

(println "=== LEVEL 2 THUMMIM SCAN — ALL DICTIONARY WORDS ===")
(println "Total words:" (count (dict/words)))
(println)

(def results (atom []))

(doseq [w (sort (dict/words))]
  (let [cat (categorize w)
        r (try (o/thummim-menu w {:max-illuminations 20 :max-words 3 :min-letters 2})
               (catch Exception e {:error (.getMessage e)}))]
    (swap! results conj
           {:word w
            :meaning (dict/translate w)
            :gv (g/word-value w)
            :category cat
            :readable? (some? r)
            :illumination-count (when r (:illumination-count r))
            :phrase-count (when r (count (:phrases r)))
            :phrases (when r (:phrases r))})))

;; Report: UNREADABLE words
(println "═══════════════════════════════════════════════════")
(println "UNREADABLE ON THE BREASTPLATE (no illumination patterns)")
(println "═══════════════════════════════════════════════════")
(doseq [{:keys [word meaning gv category]} 
        (filter (complement :readable?) @results)]
  (println (str "  " word " = " meaning " (GV=" gv ", " (name category) ")")))
(println)

;; Report: Multi-phrase words by category
(doseq [cat [:names :divine :theological :other]]
  (println "═══════════════════════════════════════════════════")
  (println (str "CATEGORY: " (name cat)))
  (println "═══════════════════════════════════════════════════")
  (let [cat-results (filter #(and (= cat (:category %)) (:readable? %)) @results)]
    (doseq [{:keys [word meaning gv illumination-count phrase-count phrases]} 
            (sort-by #(- (or (:phrase-count %) 0)) cat-results)]
      (println (str "\n● " word " = " meaning " (GV=" gv 
                    ", illum=" illumination-count 
                    ", phrases=" phrase-count ")"))
      (when (and phrases (pos? (count phrases)))
        (doseq [p (take 15 phrases)]
          (println (str "    " (:text p) " — " (pr-str (:meanings p)))))
        (when (> (count phrases) 15)
          (println (str "    ... and " (- (count phrases) 15) " more")))))))

;; Summary statistics
(println "\n═══════════════════════════════════════════════════")
(println "SUMMARY")
(println "═══════════════════════════════════════════════════")
(let [readable (filter :readable? @results)
      unreadable (filter (complement :readable?) @results)
      multi (filter #(and (:phrase-count %) (> (:phrase-count %) 1)) @results)
      single (filter #(and (:phrase-count %) (= (:phrase-count %) 1)) @results)
      zero-phrase (filter #(and (:readable? %) (:phrase-count %) (zero? (:phrase-count %))) @results)]
  (println "Readable:" (count readable))
  (println "Unreadable:" (count unreadable))
  (println "Multi-phrase (>1):" (count multi))
  (println "Single phrase:" (count single))
  (println "Readable but 0 phrases (letters don't form dict words):" (count zero-phrase)))

(println "\nDone.")
(System/exit 0)
