(ns selah.viz
  "REPL API for the Torah 4D space visualizer.

   (viz/open!)                          ;; Launch window
   (viz/close!)                         ;; Close it
   (viz/fly-to [3 25 6 33])             ;; Fly to center verse
   (viz/highlight \"כבש\")                ;; Light up the lamb
   (viz/highlight-fiber :c 6)           ;; Light up love=6
   (viz/clear!)                         ;; Clear highlights
   (viz/show-box :ark [0.7 0 0] [0.7 0.05 0.02] [1 0.8 0.2])
   (viz/axes :b :c :d)                  ;; Map axes
   (viz/fix :a 3)                       ;; Slice
   (viz/free :a)                        ;; Release
   (viz/palette :book)                  ;; Switch colors
   (viz/ortho!)                         ;; Orthographic
   (viz/persp!)                         ;; Perspective
   (viz/describe-at [3 25 6 33])        ;; Print verse info"
  (:require [selah.viz.scene :as scene]
            [selah.viz.gl :as gl]
            [selah.space.coords :as c]))

;; ── Window ─────────────────────────────────────────────────

(defn open!
  "Launch the visualizer window."
  []
  (gl/start!))

(defn close!
  "Close the visualizer window."
  []
  (gl/stop!))

;; ── Camera ─────────────────────────────────────────────────

(defn fly-to
  "Animate camera to a 4D coordinate [a b c d]."
  [coord]
  (scene/fly-to coord))

(defn look-at
  "Set camera eye and target directly."
  [eye target]
  (scene/look-at eye target))

;; ── Axes ───────────────────────────────────────────────────

(defn axes
  "Map 4D axes to screen x, y, z."
  [x y z]
  (scene/set-axes x y z))

;; ── Slicing ────────────────────────────────────────────────

(defn fix
  "Fix an axis to a value."
  [axis val]
  (scene/fix-axis axis val))

(defn free
  "Release a fixed axis."
  [axis]
  (scene/free-axis axis))

;; ── Palette ────────────────────────────────────────────────

(defn palette
  "Switch color palette. :letter :gematria :book :day"
  [p]
  (scene/set-palette p))

;; ── Projection ─────────────────────────────────────────────

(defn ortho!  [] (scene/set-ortho true))
(defn persp!  [] (scene/set-ortho false))

;; ── Highlighting ───────────────────────────────────────────

(defn highlight
  "Highlight all positions where a Hebrew word appears in a-fibers.
   Returns the number of fiber hits."
  [word]
  (let [n (scene/highlight-word word)]
    (gl/request-rebuild!)
    (println (str "[viz] Highlighted \"" word "\" — " n " fiber hits"))
    n))

(defn highlight-fiber
  "Highlight all positions on a given hyperplane."
  [axis val]
  (let [positions (c/hyperplane axis val)]
    (scene/highlight-positions (seq positions))
    (gl/request-rebuild!)
    (println (str "[viz] Highlighted " (alength positions)
                  " positions on " (name axis) "=" val))))

(defn highlight-positions
  "Highlight specific position indices."
  [positions]
  (scene/highlight-positions positions)
  (gl/request-rebuild!))

(defn clear!
  "Clear all highlights and boxes."
  []
  (scene/clear-highlights)
  (scene/clear-boxes)
  (gl/request-rebuild!))

;; ── Boxes ──────────────────────────────────────────────────

(defn show-box
  "Add a wireframe box. min/max are [x y z], color is [r g b]."
  [label min-coord max-coord color]
  (scene/show-box label min-coord max-coord color))

(defn clear-boxes []
  (scene/clear-boxes))

;; ── Info ───────────────────────────────────────────────────

(defn describe-at
  "Describe the letter at a 4D coordinate."
  [[a b c d]]
  (let [idx (c/coord->idx a b c d)]
    (c/describe idx)))

(defn status
  "Print current visualizer state."
  []
  (let [s (scene/state)]
    {:running?  (:running? s)
     :palette   (:palette s)
     :axes      (:axes s)
     :slice     (:slice s)
     :highlights (count (:highlight s))
     :boxes     (count (:boxes s))
     :ortho?    (get-in s [:camera :ortho?])}))
