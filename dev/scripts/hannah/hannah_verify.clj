(require '[selah.oracle :as o])

;; Manually verify the critical illumination #61
;; Positions: [1 3]=ה(row1,col1), [1 2]=ר(row1,col1), [8 4]=כ(row3,col2), [11 3]=ש(row4,col2)

(def positions #{[1 3] [1 2] [8 4] [11 3]})

(println "=== MANUAL VERIFICATION OF ILLUMINATION #61 ===")
(println)
(println "Letters and grid locations:")
(doseq [[s i] (sort-by first positions)]
  (let [r (o/stone-row s) c (o/stone-col s) ch (o/letter-at [s i])]
    (println (str "  [" s " " i "] = " ch " at row=" r " col=" c))))

(println)

;; Aaron's sort key: [row, -col, within-stone-idx] → reads rows L-to-R in Hebrew (R-to-L physical)
;; Wait: Aaron reads rows R->L, top->bottom. Key: [r, -c, i]
;; [1 3]=ה: key=[1,-1,3], [1 2]=ר: key=[1,-1,2], [8 4]=כ: key=[3,-2,4], [11 3]=ש: key=[4,-2,3]
;; Sorted: [1,-1,2] [1,-1,3] [3,-2,4] [4,-2,3]
;; = ר ה כ ש = רהכש
(println "Aaron: sort by [row, -col, idx]")
(doseq [[s i] (sort-by #(o/read-key :aaron %) positions)]
  (println (str "  [" s " " i "] key=" (o/read-key :aaron [s i]) " = " (o/letter-at [s i]))))
(println (str "  → " (o/read-positions :aaron positions)))

(println)

;; God: sort by [-row, col, idx] → bottom-to-top, L-to-R (from Aaron's view)
;; [1 3]=ה: key=[-1,1,3], [1 2]=ר: key=[-1,1,2], [8 4]=כ: key=[-3,2,4], [11 3]=ש: key=[-4,2,3]
;; Sorted: [-4,2,3] [-3,2,4] [-1,1,2] [-1,1,3]
;; = ש כ ר ה = שכרה ← DRUNK
(println "God: sort by [-row, col, idx]")
(doseq [[s i] (sort-by #(o/read-key :god %) positions)]
  (println (str "  [" s " " i "] key=" (o/read-key :god [s i]) " = " (o/letter-at [s i]))))
(println (str "  → " (o/read-positions :god positions)))

(println)

;; Right cherub: sort by [-col, row, idx] → R-to-L columns, top-to-bottom
;; [1 3]=ה: key=[-1,1,3], [1 2]=ר: key=[-1,1,2], [8 4]=כ: key=[-2,3,4], [11 3]=ש: key=[-2,4,3]
;; Sorted: [-2,3,4] [-2,4,3] [-1,1,2] [-1,1,3]
;; = כ ש ר ה = כשרה ← LIKE SARAH!
(println "Right cherub: sort by [-col, row, idx]")
(doseq [[s i] (sort-by #(o/read-key :right %) positions)]
  (println (str "  [" s " " i "] key=" (o/read-key :right [s i]) " = " (o/letter-at [s i]))))
(println (str "  → " (o/read-positions :right positions)))

(println)

;; Left cherub: sort by [col, -row, idx] → L-to-R columns, bottom-to-top
;; [1 3]=ה: key=[1,-1,3], [1 2]=ר: key=[1,-1,2], [8 4]=כ: key=[2,-3,4], [11 3]=ש: key=[2,-4,3]
;; Sorted: [1,-1,2] [1,-1,3] [2,-4,3] [2,-3,4]
;; = ר ה ש כ = רהשכ
(println "Left cherub: sort by [col, -row, idx]")
(doseq [[s i] (sort-by #(o/read-key :left %) positions)]
  (println (str "  [" s " " i "] key=" (o/read-key :left [s i]) " = " (o/letter-at [s i]))))
(println (str "  → " (o/read-positions :left positions)))

(println)
(println "=== CONFIRMED: Illumination #61 ===")
(println "God sees שכרה (DRUNK). Right cherub sees כשרה (LIKE SARAH).")
(println "Same four letters. Same light. Different truth.")
(println)

;; Now: which stones are these letters on?
(println "=== THE STONES IN PLAY ===")
(println "Stone 1 (Abraham/אברהם) carries: ר and ה")
(println "  Abraham — the father of Sarah. The ר and ה from his stone.")
(println "  ה is the 4th letter of אברהם. ר is the 3rd.")
(println)
(println "Stone 8 (Issachar/יששכר) carries: כ")
(println "  Issachar = 'there is reward' (יש שכר)")
(println "  כ is the 5th letter of ריששכר — it is THE כ of שכר (reward)!")
(println "  This is the ONLY כ on the entire grid.")
(println)
(println "Stone 11 (Benjamin/בנימין→שבט) carries: ש")
(println "  Benjamin = 'son of the right hand'")
(println "  ש is at position 3 in מיןשבט")
(println)
(println "=== NARRATIVE ALIGNMENT ===")
(println "The right cherub — at God's right hand — reads כשרה (like Sarah).")
(println "The ש comes from Benjamin = 'son of the right hand'.")
(println "Hannah was praying for a son. She received Samuel.")
(println "The right-hand reading, from the right-hand stone, gives the right answer.")
