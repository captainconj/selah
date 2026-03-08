(ns selah.viz.controls
  "Keyboard and mouse input for the Torah 4D space visualizer.

   Ortho mode: pan (right-drag), zoom (scroll), step slices (+/-/arrows).
   3D mode: WASD fly, right-drag look, Space up, Shift down."
  (:require [selah.viz.scene :as scene])
  (:import [org.lwjgl.glfw GLFW GLFWKeyCallbackI
            GLFWMouseButtonCallbackI GLFWCursorPosCallbackI
            GLFWScrollCallbackI]))

;; ── Mouse state ────────────────────────────────────────────

(defonce ^:private mouse-state
  (atom {:dragging?  false
         :last-x     0.0
         :last-y     0.0}))

;; ── Constants ──────────────────────────────────────────────

(def ^:private move-speed 0.02)
(def ^:private rotate-speed 0.002)
(def ^:private pan-speed 0.002)

;; ── Palette ────────────────────────────────────────────────

(def ^:private palettes [:letter :gematria :book :day])

(defn- cycle-palette []
  (let [current (:palette (scene/state))
        idx (.indexOf palettes current)
        next-idx (mod (inc idx) (count palettes))
        next-pal (nth palettes next-idx)]
    (scene/set-palette next-pal)
    (println (str "[viz] Palette: " (name next-pal)))))

;; ── Slice stepping ─────────────────────────────────────────

(defn- step-fixed
  "Step the first fixed axis by delta."
  [delta]
  (let [fx (scene/fixed-axes)
        axis (first (sort (keys fx)))]
    (when axis
      (scene/step-axis axis delta)
      (println (str "[viz] " (name axis) "=" (get-in (scene/state) [:slice axis]))))))

(defn- step-fixed-nth
  "Step the nth fixed axis by delta."
  [n delta]
  (let [fx (scene/fixed-axes)
        sorted-axes (sort (keys fx))]
    (when (< n (count sorted-axes))
      (let [axis (nth sorted-axes n)]
        (scene/step-axis axis delta)
        (println (str "[viz] " (name axis) "=" (get-in (scene/state) [:slice axis])))))))

;; ── Key callback ───────────────────────────────────────────

(defn- ortho? [] (get-in (scene/state) [:camera :ortho?]))

(defn- on-key [_window key _scancode action _mods]
  (when (= action GLFW/GLFW_PRESS)
    (condp = key
      ;; O: toggle ortho/3D
      GLFW/GLFW_KEY_O
      (do (scene/toggle-ortho)
          (println (str "[viz] " (if (ortho?) "Ortho" "3D"))))

      ;; P: cycle palette
      GLFW/GLFW_KEY_P (cycle-palette)

      ;; Tab: cycle 2D views (ortho only)
      GLFW/GLFW_KEY_TAB
      (when (ortho?)
        (scene/cycle-slice-view!))

      ;; 1-4: toggle axis fixed/free
      GLFW/GLFW_KEY_1
      (let [v (get-in (scene/state) [:slice :a])]
        (if v (scene/free-axis :a) (scene/fix-axis :a 3))
        (when (ortho?) (scene/reset-ortho-camera!))
        (println (str "[viz] a: " (if v "free" "=3"))))

      GLFW/GLFW_KEY_2
      (let [v (get-in (scene/state) [:slice :b])]
        (if v (scene/free-axis :b) (scene/fix-axis :b 0))
        (when (ortho?) (scene/reset-ortho-camera!))
        (println (str "[viz] b: " (if v "free" "=0"))))

      GLFW/GLFW_KEY_3
      (let [v (get-in (scene/state) [:slice :c])]
        (if v (scene/free-axis :c) (scene/fix-axis :c 6))
        (when (ortho?) (scene/reset-ortho-camera!))
        (println (str "[viz] c: " (if v "free" "=6"))))

      GLFW/GLFW_KEY_4
      (let [v (get-in (scene/state) [:slice :d])]
        (if v (scene/free-axis :d) (scene/fix-axis :d 33))
        (when (ortho?) (scene/reset-ortho-camera!))
        (println (str "[viz] d: " (if v "free" "=33"))))

      ;; +/- or ]/[: step through FIRST fixed axis
      GLFW/GLFW_KEY_EQUAL       (step-fixed 1)
      GLFW/GLFW_KEY_MINUS       (step-fixed -1)
      GLFW/GLFW_KEY_RIGHT_BRACKET (step-fixed 1)
      GLFW/GLFW_KEY_LEFT_BRACKET  (step-fixed -1)

      ;; Arrow keys: step fixed axes (up/down = first, left/right = second)
      GLFW/GLFW_KEY_UP    (step-fixed-nth 0 1)
      GLFW/GLFW_KEY_DOWN  (step-fixed-nth 0 -1)
      GLFW/GLFW_KEY_RIGHT (step-fixed-nth 1 1)
      GLFW/GLFW_KEY_LEFT  (step-fixed-nth 1 -1)

      ;; C: clear highlights
      GLFW/GLFW_KEY_C
      (do (scene/clear-highlights)
          (swap! scene/*state* assoc :dirty? true)
          (println "[viz] Highlights cleared"))

      ;; R: reset camera
      GLFW/GLFW_KEY_R
      (if (ortho?)
        (scene/reset-ortho-camera!)
        (scene/look-at [0.5 0.5 2.5] [0.5 0.5 0.0]))

      ;; Default
      nil)))

;; ── Held-key polling (3D flight only) ─────────────────────

(defn process-held-keys!
  "Poll held keys for continuous camera movement. 3D mode only.
   Minecraft creative: W/S forward/back, A/D strafe, Space up, Shift down."
  [window]
  (when-not (ortho?)
    (let [cam (:camera (scene/state))
          [ex ey ez] (:eye cam)
          [tx ty tz] (:target cam)
          dx (- tx ex) dy (- ty ey) dz (- tz ez)
          len (Math/sqrt (+ (* dx dx) (* dy dy) (* dz dz)))
          fdx (/ dx (max len 0.001))
          fdy (/ dy (max len 0.001))
          fdz (/ dz (max len 0.001))
          rx (- fdz) rz fdx
          rlen (Math/sqrt (+ (* rx rx) (* rz rz)))
          rx (/ rx (max rlen 0.001))
          rz (/ rz (max rlen 0.001))
          moved? (volatile! false)
          mx (volatile! 0.0) my (volatile! 0.0) mz (volatile! 0.0)]
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_W))
        (vreset! moved? true)
        (vswap! mx + (* fdx move-speed))
        (vswap! my + (* fdy move-speed))
        (vswap! mz + (* fdz move-speed)))
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_S))
        (vreset! moved? true)
        (vswap! mx - (* fdx move-speed))
        (vswap! my - (* fdy move-speed))
        (vswap! mz - (* fdz move-speed)))
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_A))
        (vreset! moved? true)
        (vswap! mx - (* rx move-speed))
        (vswap! mz - (* rz move-speed)))
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_D))
        (vreset! moved? true)
        (vswap! mx + (* rx move-speed))
        (vswap! mz + (* rz move-speed)))
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_SPACE))
        (vreset! moved? true)
        (vswap! my + move-speed))
      (when (= GLFW/GLFW_PRESS (GLFW/glfwGetKey window GLFW/GLFW_KEY_LEFT_SHIFT))
        (vreset! moved? true)
        (vswap! my - move-speed))
      (when @moved?
        (scene/look-at
         [(+ ex @mx) (+ ey @my) (+ ez @mz)]
         [(+ tx @mx) (+ ty @my) (+ tz @mz)])))))

;; ── Mouse callbacks ────────────────────────────────────────

(defn- on-mouse-button [_window button action _mods]
  (when (= button GLFW/GLFW_MOUSE_BUTTON_RIGHT)
    (cond
      (= action GLFW/GLFW_PRESS)
      (swap! mouse-state assoc :dragging? true)

      (= action GLFW/GLFW_RELEASE)
      (swap! mouse-state assoc :dragging? false))))

(defn- on-cursor-pos [_window x y]
  (let [{:keys [dragging? last-x last-y]} @mouse-state]
    (when (and dragging? (pos? last-x))
      (let [dx (- x last-x)
            dy (- y last-y)]
        (if (ortho?)
          ;; Ortho: pan
          (let [zoom (get-in (scene/state) [:camera :zoom])
                pdx (* dx pan-speed (/ 1.0 zoom))
                pdy (* dy pan-speed (/ -1.0 zoom))]
            (swap! scene/*state* update-in [:camera :pan]
                   (fn [[px py]] [(+ px pdx) (+ py pdy)])))
          ;; 3D: rotate view (FPS-style)
          (let [dx (* dx rotate-speed)
                dy (* dy rotate-speed -1.0)
                cam (:camera (scene/state))
                [ex ey ez] (:eye cam)
                [tx ty tz] (:target cam)
                fx (- tx ex) fy (- ty ey) fz (- tz ez)
                cos-dx (Math/cos dx) sin-dx (Math/sin dx)
                fx' (- (* fx cos-dx) (* fz sin-dx))
                fz' (+ (* fx sin-dx) (* fz cos-dx))
                len (Math/sqrt (+ (* fx' fx') (* fy fy) (* fz' fz')))
                fy' (+ fy (* dy len))
                fy' (max (min fy' (* 0.95 len)) (* -0.95 len))]
            (scene/look-at
             [ex ey ez]
             [(+ ex fx') (+ ey fy') (+ ez fz')]))))))
  (swap! mouse-state assoc :last-x x :last-y y))

(defn- on-scroll [_window _x-offset y-offset]
  (if (ortho?)
    ;; Ortho: zoom
    (let [zoom (get-in (scene/state) [:camera :zoom])
          factor (Math/pow 1.1 y-offset)
          new-zoom (max 0.1 (min 20.0 (* zoom factor)))]
      (swap! scene/*state* assoc-in [:camera :zoom] new-zoom))
    ;; 3D: move toward/away from target
    (let [cam (:camera (scene/state))
          [ex ey ez] (:eye cam)
          [tx ty tz] (:target cam)
          dx (- tx ex) dy (- ty ey) dz (- tz ez)
          len (Math/sqrt (+ (* dx dx) (* dy dy) (* dz dz)))
          factor (* 0.1 y-offset)
          new-len (max 0.05 (- len factor))]
      (when (pos? len)
        (let [scale (/ new-len len)]
          (scene/look-at
           [(- tx (* dx scale)) (- ty (* dy scale)) (- tz (* dz scale))]
           [tx ty tz]))))))

;; ── Registration ───────────────────────────────────────────

(defn register-callbacks!
  "Register all GLFW input callbacks on the window."
  [^long window]
  (GLFW/glfwSetKeyCallback
   window
   (reify GLFWKeyCallbackI
     (invoke [_ win key scancode action mods]
       (on-key win key scancode action mods))))
  (GLFW/glfwSetMouseButtonCallback
   window
   (reify GLFWMouseButtonCallbackI
     (invoke [_ win button action mods]
       (on-mouse-button win button action mods))))
  (GLFW/glfwSetCursorPosCallback
   window
   (reify GLFWCursorPosCallbackI
     (invoke [_ win x y]
       (on-cursor-pos win x y))))
  (GLFW/glfwSetScrollCallback
   window
   (reify GLFWScrollCallbackI
     (invoke [_ win x-offset y-offset]
       (on-scroll win x-offset y-offset)))))
