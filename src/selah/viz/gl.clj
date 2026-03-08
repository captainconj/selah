(ns selah.viz.gl
  "LWJGL3 OpenGL renderer for the Torah 4D space.

   Runs on a dedicated thread — the REPL stays responsive.
   Watches the scene atom for dirty state, rebuilds VBOs as needed."
  (:require [selah.viz.scene :as scene]
            [selah.viz.controls :as controls]
            [selah.space.coords :as c])
  (:import [org.lwjgl.glfw GLFW GLFWErrorCallback GLFWKeyCallbackI
            GLFWMouseButtonCallbackI GLFWCursorPosCallbackI
            GLFWScrollCallbackI GLFWFramebufferSizeCallbackI]
           [org.lwjgl.opengl GL GL11 GL13 GL15 GL20 GL30 GL32]
           [org.lwjgl.system MemoryUtil]
           [org.joml Matrix4f Vector3f]
           [java.nio FloatBuffer ByteBuffer]
           [java.awt Font Graphics2D RenderingHints Color]
           [java.awt.image BufferedImage]
           [java.awt.font FontRenderContext TextLayout]))

;; ── Shaders (embedded) ────────────────────────────────────

(def ^:private vertex-shader-src
  "#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in float aHighlight;
layout (location = 3) in float aLetterIdx;

uniform mat4 uMVP;
uniform float uTime;
uniform float uPointSize;
uniform int uOrtho;

out vec3 vColor;
out float vHighlight;
out float vLetterIdx;

void main() {
    gl_Position = uMVP * vec4(aPos, 1.0);

    if (uOrtho == 1) {
        // Ortho: constant size for all points
        gl_PointSize = uPointSize;
    } else {
        // Perspective: size decreases with distance
        float dist = length(gl_Position.xyz);
        gl_PointSize = uPointSize / max(dist, 0.1);
    }

    vColor = aColor;
    vHighlight = aHighlight;
    vLetterIdx = aLetterIdx;
}")

(def ^:private fragment-shader-src
  "#version 330 core
in vec3 vColor;
in float vHighlight;
in float vLetterIdx;

uniform float uTime;
uniform sampler2D uAtlas;
uniform float uAtlasCols;  // 8.0
uniform float uAtlasRows;  // 4.0

out vec4 FragColor;

void main() {
    // Look up letter in texture atlas (8x4 grid, 27 letters)
    int idx = int(vLetterIdx + 0.5);
    int col = idx % int(uAtlasCols);
    int row = idx / int(uAtlasCols);

    // Map gl_PointCoord into the right atlas cell
    vec2 cellSize = vec2(1.0 / uAtlasCols, 1.0 / uAtlasRows);
    vec2 cellOrigin = vec2(float(col), float(row)) * cellSize;
    vec2 uv = cellOrigin + gl_PointCoord * cellSize;

    float letterAlpha = texture(uAtlas, uv).r;

    // Discard empty pixels
    if (letterAlpha < 0.1) discard;

    float alpha = letterAlpha * 0.85;
    vec3 color = vColor;

    if (vHighlight > 0.5) {
        float pulse = 0.7 + 0.3 * sin(uTime * 4.0);
        color = mix(color, vec3(1.0), 0.5 * pulse);
        alpha = letterAlpha * (0.9 + 0.1 * pulse);
    }

    FragColor = vec4(color, alpha);
}")

;; ── Box wireframe shaders ─────────────────────────────────

(def ^:private box-vertex-src
  "#version 330 core
layout (location = 0) in vec3 aPos;
uniform mat4 uMVP;
void main() {
    gl_Position = uMVP * vec4(aPos, 1.0);
}")

(def ^:private box-fragment-src
  "#version 330 core
uniform vec3 uBoxColor;
out vec4 FragColor;
void main() {
    FragColor = vec4(uBoxColor, 0.8);
}")

;; ── GL helpers ─────────────────────────────────────────────

(defn- compile-shader [^String src ^long type]
  (let [shader (GL20/glCreateShader type)]
    (GL20/glShaderSource shader src)
    (GL20/glCompileShader shader)
    (when (zero? (GL20/glGetShaderi shader GL20/GL_COMPILE_STATUS))
      (let [log (GL20/glGetShaderInfoLog shader)]
        (GL20/glDeleteShader shader)
        (throw (RuntimeException. (str "Shader compile error: " log)))))
    shader))

(defn- link-program [vs fs]
  (let [program (GL20/glCreateProgram)]
    (GL20/glAttachShader program vs)
    (GL20/glAttachShader program fs)
    (GL20/glLinkProgram program)
    (when (zero? (GL20/glGetProgrami program GL20/GL_LINK_STATUS))
      (let [log (GL20/glGetProgramInfoLog program)]
        (GL20/glDeleteProgram program)
        (throw (RuntimeException. (str "Program link error: " log)))))
    (GL20/glDeleteShader vs)
    (GL20/glDeleteShader fs)
    program))

(defn- create-shader-program [vert-src frag-src]
  (let [vs (compile-shader vert-src GL20/GL_VERTEX_SHADER)
        fs (compile-shader frag-src GL20/GL_FRAGMENT_SHADER)]
    (link-program vs fs)))

(defn- upload-float-buffer
  "Create or update a VBO with float data. Returns buffer id."
  (^long [^floats data]
   (let [buf-id (GL15/glGenBuffers)
         fb (MemoryUtil/memAllocFloat (alength data))]
     (try
       (.put fb data)
       (.flip fb)
       (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER buf-id)
       (GL15/glBufferData GL15/GL_ARRAY_BUFFER ^FloatBuffer fb GL15/GL_STATIC_DRAW)
       (finally
         (MemoryUtil/memFree fb)))
     buf-id))
  (^long [^long buf-id ^floats data]
   (let [fb (MemoryUtil/memAllocFloat (alength data))]
     (try
       (.put fb data)
       (.flip fb)
       (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER buf-id)
       (GL15/glBufferData GL15/GL_ARRAY_BUFFER ^FloatBuffer fb GL15/GL_STATIC_DRAW)
       (finally
         (MemoryUtil/memFree fb)))
     buf-id)))

;; ── Hebrew letter texture atlas ────────────────────────────
;;
;; 8×4 grid (32 cells, 27 used). Each cell 64×64.
;; Generated at runtime via Java2D — no external font files needed.

(def ^:const atlas-cols 8)
(def ^:const atlas-rows 4)
(def ^:const atlas-cell 64)

(defn- generate-atlas-image
  "Render 27 Hebrew letters into a BufferedImage atlas.
   Returns a BufferedImage (512×256, TYPE_BYTE_GRAY)."
  ^BufferedImage []
  (let [w (* atlas-cols atlas-cell)
        h (* atlas-rows atlas-cell)
        img (BufferedImage. w h BufferedImage/TYPE_BYTE_GRAY)
        ^Graphics2D g (.createGraphics img)
        ;; Try common Hebrew fonts, fall back to any available
        font-names ["Noto Sans Hebrew" "David" "Arial" "DejaVu Sans"
                    "Liberation Sans" "SansSerif"]
        font (loop [[name & more] font-names]
               (if name
                 (let [f (Font. name Font/BOLD 44)]
                   (if (.canDisplay f (int 0x05D0))
                     f
                     (recur more)))
                 (Font. "SansSerif" Font/BOLD 44)))]
    (.setRenderingHint g RenderingHints/KEY_ANTIALIASING
                       RenderingHints/VALUE_ANTIALIAS_ON)
    (.setRenderingHint g RenderingHints/KEY_TEXT_ANTIALIASING
                       RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
    (.setFont g font)
    (.setColor g Color/BLACK)
    (.fillRect g 0 0 w h)
    (.setColor g Color/WHITE)
    (let [frc (.getFontRenderContext g)]
      (dotimes [i 27]
        (let [ch (nth c/letter-chars i)
              s (str ch)
              layout (TextLayout. s font frc)
              bounds (.getBounds layout)
              col (mod i atlas-cols)
              row (quot i atlas-cols)
              ;; Center letter in cell
              cx (+ (* col atlas-cell) (/ atlas-cell 2))
              cy (+ (* row atlas-cell) (/ atlas-cell 2))
              tx (- cx (/ (.getWidth bounds) 2))
              ty (+ cy (/ (.getHeight bounds) 2))]
          (.drawString g s (float tx) (float ty)))))
    (.dispose g)
    img))

(defn- upload-atlas-texture
  "Upload the atlas BufferedImage as a GL texture. Returns texture id."
  ^long [^BufferedImage img]
  (let [w (.getWidth img)
        h (.getHeight img)
        raster (.getRaster img)
        pixels (byte-array (* w h))
        _ (.getDataElements raster 0 0 w h pixels)
        buf (MemoryUtil/memAlloc (* w h))]
    (try
      (.put buf pixels)
      (.flip buf)
      (let [tex (GL11/glGenTextures)]
        (GL11/glBindTexture GL11/GL_TEXTURE_2D tex)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_LINEAR)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_LINEAR)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_S GL11/GL_CLAMP)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_T GL11/GL_CLAMP)
        (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RED
                           w h 0 GL11/GL_RED GL11/GL_UNSIGNED_BYTE ^ByteBuffer buf)
        (GL11/glBindTexture GL11/GL_TEXTURE_2D 0)
        tex)
      (finally
        (MemoryUtil/memFree buf)))))

;; ── MVP matrix ─────────────────────────────────────────────

(defn- compute-mvp
  "Build Model-View-Projection matrix from camera state.
   In ortho mode, auto-fits the [0,1]×[0,1] data to the viewport
   with pan and zoom applied."
  ^floats [{:keys [eye target up ortho? fov pan zoom]
            :or {pan [0 0] zoom 1.0}} ^long width ^long height]
  (let [aspect (/ (float width) (float (max height 1)))
        [ex ey ez] eye
        [tx ty tz] target
        [ux uy uz] up
        proj (Matrix4f.)
        view (Matrix4f.)
        mvp  (Matrix4f.)
        fb   (float-array 16)]
    (if ortho?
      ;; Auto-fit: lookAt already centers [0,1] data at origin in view space.
      ;; Data lives in [-0.5, 0.5] after view transform.
      ;; Fit to viewport: shrink the visible range so data fills the width.
      (let [[px py] pan
            half-h (/ 0.55 zoom)
            half-w (* half-h aspect)]
        (.ortho proj
                (float (- px half-w)) (float (+ px half-w))
                (float (- py half-h)) (float (+ py half-h))
                (float -10.0) (float 10.0)))
      (.perspective proj (float (Math/toRadians (double fov)))
                    aspect (float 0.01) (float 100.0)))
    (.lookAt view
             (Vector3f. (float ex) (float ey) (float ez))
             (Vector3f. (float tx) (float ty) (float tz))
             (Vector3f. (float ux) (float uy) (float uz)))
    (.mul proj view mvp)
    (.get mvp ^floats fb)
    fb))

;; ── GL state ───────────────────────────────────────────────

(defonce ^:private gl-state
  (atom {:program    nil
         :box-program nil
         :vao        nil
         :pos-vbo    nil
         :col-vbo    nil
         :hi-vbo     nil
         :let-vbo    nil   ;; letter index VBO
         :atlas-tex  nil   ;; Hebrew letter texture
         :point-count 0
         :width      1280
         :height     800
         :start-time 0}))

;; ── Build VBOs ─────────────────────────────────────────────

(defn- build-highlight-buffer
  "Create float[] marking highlighted positions (1.0 = highlighted, 0.0 = not)."
  ^floats [^ints indices highlight-set]
  (let [n (alength indices)
        out (float-array n)]
    (when (seq highlight-set)
      (dotimes [i n]
        (when (contains? highlight-set (aget indices i))
          (aset out i (float 1.0)))))
    out))

(defn- rebuild-vbos!
  "Recompute positions, colors, highlights, letters and upload to GPU.
   Reuses existing VBOs when possible to reduce allocations."
  []
  (let [{:keys [positions colors count indices letters]} (scene/position-data)
        highlight (:highlight (scene/state))
        hi-data (build-highlight-buffer indices highlight)
        old-pos (:pos-vbo @gl-state)
        old-col (:col-vbo @gl-state)
        old-hi  (:hi-vbo @gl-state)
        old-let (:let-vbo @gl-state)
        ;; Reuse existing VBOs or create new ones
        pos-vbo (if old-pos
                  (upload-float-buffer old-pos positions)
                  (upload-float-buffer positions))
        col-vbo (if old-col
                  (upload-float-buffer old-col colors)
                  (upload-float-buffer colors))
        hi-vbo  (if old-hi
                  (upload-float-buffer old-hi hi-data)
                  (upload-float-buffer hi-data))
        let-vbo (if old-let
                  (upload-float-buffer old-let letters)
                  (upload-float-buffer letters))
        vao (or (:vao @gl-state)
                (GL30/glGenVertexArrays))]
    (GL30/glBindVertexArray vao)
    ;; Position attribute (location 0)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER pos-vbo)
    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)
    (GL20/glEnableVertexAttribArray 0)
    ;; Color attribute (location 1)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER col-vbo)
    (GL20/glVertexAttribPointer 1 3 GL11/GL_FLOAT false 0 0)
    (GL20/glEnableVertexAttribArray 1)
    ;; Highlight attribute (location 2)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER hi-vbo)
    (GL20/glVertexAttribPointer 2 1 GL11/GL_FLOAT false 0 0)
    (GL20/glEnableVertexAttribArray 2)
    ;; Letter index attribute (location 3)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER let-vbo)
    (GL20/glVertexAttribPointer 3 1 GL11/GL_FLOAT false 0 0)
    (GL20/glEnableVertexAttribArray 3)
    (GL30/glBindVertexArray 0)
    (swap! gl-state assoc
           :vao vao :pos-vbo pos-vbo :col-vbo col-vbo
           :hi-vbo hi-vbo :let-vbo let-vbo
           :point-count count)
    (scene/mark-clean)))

;; ── Box rendering ──────────────────────────────────────────

(def ^:private box-edges
  "12 edges of a box as index pairs into 8 corners."
  [[0 1] [1 2] [2 3] [3 0]   ;; bottom
   [4 5] [5 6] [6 7] [7 4]   ;; top
   [0 4] [1 5] [2 6] [3 7]]) ;; verticals

(defn- box-corners
  "8 corners of an axis-aligned box."
  [[x0 y0 z0] [x1 y1 z1]]
  [[x0 y0 z0] [x1 y0 z0] [x1 y1 z0] [x0 y1 z0]
   [x0 y0 z1] [x1 y0 z1] [x1 y1 z1] [x0 y1 z1]])

(defn- render-box!
  "Draw a single wireframe box."
  [box-program mvp-fb {:keys [min max color]}]
  (let [corners (box-corners min max)
        verts (float-array (mapcat (fn [[i j]]
                                     (concat (nth corners i) (nth corners j)))
                                   box-edges))
        vbo (upload-float-buffer verts)
        vao (GL30/glGenVertexArrays)]
    (GL30/glBindVertexArray vao)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo)
    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)
    (GL20/glEnableVertexAttribArray 0)
    (GL20/glUseProgram box-program)
    (GL20/glUniformMatrix4fv
     (GL20/glGetUniformLocation box-program "uMVP") false mvp-fb)
    (let [[r g b] color]
      (GL20/glUniform3f
       (GL20/glGetUniformLocation box-program "uBoxColor")
       (float r) (float g) (float b)))
    (GL11/glDrawArrays GL11/GL_LINES 0 (* 12 2))
    (GL30/glBindVertexArray 0)
    (GL30/glDeleteVertexArrays vao)
    (GL15/glDeleteBuffers (int vbo))))

;; ── Camera animation ───────────────────────────────────────

(defn- update-camera-animation!
  "Smoothly lerp camera when flying."
  []
  (let [cam (:camera (scene/state))]
    (when-let [fly-start (:fly-start cam)]
      (let [elapsed (/ (double (- (System/nanoTime) fly-start)) 1e9)
            t (min 1.0 (* elapsed 2.0)) ;; 0.5s flight
            t-smooth (* t t (- 3.0 (* 2.0 t))) ;; smoothstep
            [fx fy fz] (:fly-from cam)
            [tx ty tz] (:fly-to cam)]
        (when (and fx tx)
          (let [ex (+ fx (* t-smooth (- tx fx)))
                ey (+ fy (* t-smooth (- ty fy)))
                ez (+ fz (* t-smooth (- tz fz)))]
            (swap! scene/*state* assoc-in [:camera :eye]
                   [ex ey ez])))
        (when (>= t 1.0)
          (swap! scene/*state* update :camera dissoc
                 :fly-start :fly-from :fly-to))))))

;; ── HUD overlay ───────────────────────────────────────────

(def ^:private hud-vertex-src
  "#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aUV;
out vec2 vUV;
void main() {
    gl_Position = vec4(aPos, 0.0, 1.0);
    vUV = aUV;
}")

(def ^:private hud-fragment-src
  "#version 330 core
in vec2 vUV;
uniform sampler2D uHudTex;
out vec4 FragColor;
void main() {
    vec4 c = texture(uHudTex, vUV);
    if (c.a < 0.01) discard;
    FragColor = c;
}")

(defonce ^:private hud-state
  (atom {:tex nil :vao nil :vbo nil :last-info nil}))

(def ^:const sidebar-width 180)  ;; pixels

(defn- generate-hud-image
  "Render state info as a vertical side panel texture.
   Returns BufferedImage (ARGB, sidebar-width × 800)."
  ^BufferedImage [lines]
  (let [w sidebar-width h 800
        img (BufferedImage. w h BufferedImage/TYPE_4BYTE_ABGR)
        ^Graphics2D g (.createGraphics img)]
    (.setRenderingHint g RenderingHints/KEY_TEXT_ANTIALIASING
                       RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
    ;; Transparent background
    (.setComposite g (java.awt.AlphaComposite/getInstance
                      java.awt.AlphaComposite/CLEAR))
    (.fillRect g 0 0 w h)
    (.setComposite g (java.awt.AlphaComposite/getInstance
                      java.awt.AlphaComposite/SRC_OVER))
    ;; Semi-transparent dark panel
    (.setColor g (Color. 0 0 0 180))
    (.fillRect g 0 0 w h)
    ;; Info lines
    (.setFont g (Font. "SansSerif" Font/PLAIN 16))
    (doseq [[i line] (map-indexed vector lines)]
      (let [label? (or (.endsWith (str line) ":") (empty? (str line)))
            dim?   (.startsWith (str line) "  ")]
        (.setColor g (cond
                       (zero? i) (Color. 200 180 255)   ;; mode line — purple
                       label?    (Color. 140 140 140)    ;; labels — dim
                       dim?      (Color. 220 220 220)    ;; values — bright
                       :else     (Color. 180 180 180)))
        (.drawString g (str line) 12 (+ 28 (* i 22)))))
    ;; Controls at bottom
    (.setColor g (Color. 110 110 110))
    (.setFont g (Font. "SansSerif" Font/PLAIN 12))
    (let [controls ["Tab: view" "1-4: axis" "+/-: step"
                    "\u2191\u2193\u2190\u2192: slice" "O: 3D" "P: palette"
                    "R: reset" "Scroll: zoom"]]
      (doseq [[i c] (map-indexed vector controls)]
        (.drawString g c 12 (- h (* (- (count controls) i) 17) 6))))
    (.dispose g)
    img))

(defn- upload-hud-texture! [^BufferedImage img]
  (let [w (.getWidth img) h (.getHeight img)
        raster (.getRaster img)
        pixels (byte-array (* w h 4))
        _ (.getDataElements raster 0 0 w h pixels)
        ;; Convert ABGR → RGBA
        rgba (byte-array (* w h 4))]
    (dotimes [i (* w h)]
      (let [j (* i 4)]
        (aset rgba (+ j 0) (aget pixels (+ j 3)))  ;; R ← A... wait, TYPE_4BYTE_ABGR is A,B,G,R
        (aset rgba (+ j 1) (aget pixels (+ j 2)))  ;; G
        (aset rgba (+ j 2) (aget pixels (+ j 1)))  ;; B
        (aset rgba (+ j 3) (aget pixels (+ j 0))))) ;; A
    (let [buf (MemoryUtil/memAlloc (alength rgba))
          tex (or (:tex @hud-state) (GL11/glGenTextures))]
      (try
        (.put buf rgba)
        (.flip buf)
        (GL11/glBindTexture GL11/GL_TEXTURE_2D tex)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_LINEAR)
        (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_LINEAR)
        (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGBA
                           w h 0 GL11/GL_RGBA GL11/GL_UNSIGNED_BYTE ^ByteBuffer buf)
        (GL11/glBindTexture GL11/GL_TEXTURE_2D 0)
        (swap! hud-state assoc :tex tex)
        (finally
          (MemoryUtil/memFree buf))))))

(defn- ensure-hud-vao!
  "Create/update the HUD quad as a left-side vertical strip."
  [^long width ^long height]
  ;; Recompute every time — width may change on resize
  (let [;; Left edge of screen, full height, sidebar-width pixels wide
        ;; Convert pixel width to clip-space [-1, 1]
        right-clip (- (* 2.0 (/ (double sidebar-width) (double (max width 1)))) 1.0)
        verts (float-array [-1.0 -1.0        0.0 1.0   ;; bottom-left
                             (float right-clip) -1.0  1.0 1.0   ;; bottom-right
                            -1.0  1.0        0.0 0.0   ;; top-left
                             (float right-clip)  1.0  1.0 0.0]) ;; top-right
        vbo (or (:vbo @hud-state) (upload-float-buffer verts))
        vao (or (:vao @hud-state) (GL30/glGenVertexArrays))]
    ;; Always re-upload verts (cheap)
    (upload-float-buffer vbo verts)
    (GL30/glBindVertexArray vao)
    (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo)
    (GL20/glVertexAttribPointer 0 2 GL11/GL_FLOAT false (* 4 4) 0)
    (GL20/glEnableVertexAttribArray 0)
    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false (* 4 4) (* 2 4))
    (GL20/glEnableVertexAttribArray 1)
    (GL30/glBindVertexArray 0)
    (swap! hud-state assoc :vao vao :vbo vbo)))

(defn- render-hud! [^long width ^long height]
  (let [lines (scene/info-lines)
        info-key (vec lines)]
    ;; Only regenerate texture when info changes
    (when (not= info-key (:last-info @hud-state))
      (upload-hud-texture! (generate-hud-image lines))
      (swap! hud-state assoc :last-info info-key))
    (ensure-hud-vao! width height)
    (when-let [program (:hud-program @gl-state)]
      (when-let [tex (:tex @hud-state)]
        (when-let [vao (:vao @hud-state)]
          ;; Full viewport for HUD (it's in clip space)
          (GL11/glViewport 0 0 width height)
          (GL11/glDisable GL11/GL_DEPTH_TEST)
          (GL20/glUseProgram program)
          (GL13/glActiveTexture GL13/GL_TEXTURE0)
          (GL11/glBindTexture GL11/GL_TEXTURE_2D tex)
          (GL20/glUniform1i (GL20/glGetUniformLocation program "uHudTex") 0)
          (GL30/glBindVertexArray vao)
          (GL11/glDrawArrays GL11/GL_TRIANGLE_STRIP 0 4)
          (GL30/glBindVertexArray 0)
          (GL11/glEnable GL11/GL_DEPTH_TEST))))))

;; ── Init / Render / Shutdown ──────────────────────────────

(defn- init-gl!
  "Create window, shaders, initial VBOs."
  []
  (.set (GLFWErrorCallback/createPrint System/err))
  (when-not (GLFW/glfwInit)
    (throw (IllegalStateException. "Failed to init GLFW")))
  ;; Window hints
  (GLFW/glfwDefaultWindowHints)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_PROFILE GLFW/GLFW_OPENGL_CORE_PROFILE)
  (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT GL11/GL_TRUE)
  ;; Create window
  (let [window (GLFW/glfwCreateWindow 1280 800
                                       "Selah \u00B7 Torah 4D Space" 0 0)]
    (when (zero? window)
      (GLFW/glfwTerminate)
      (throw (RuntimeException. "Failed to create GLFW window")))
    (GLFW/glfwMakeContextCurrent window)
    (GLFW/glfwSwapInterval 1)  ;; vsync
    (GL/createCapabilities)
    ;; GL state
    (GL11/glEnable GL11/GL_DEPTH_TEST)
    (GL11/glEnable GL32/GL_PROGRAM_POINT_SIZE)
    (GL11/glEnable GL11/GL_BLEND)
    (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
    (GL11/glClearColor 0.05 0.05 0.08 1.0)
    ;; Shaders + texture atlas
    (let [program (create-shader-program vertex-shader-src fragment-shader-src)
          box-program (create-shader-program box-vertex-src box-fragment-src)
          hud-program (create-shader-program hud-vertex-src hud-fragment-src)
          atlas-img (generate-atlas-image)
          atlas-tex (upload-atlas-texture atlas-img)]
      (swap! gl-state assoc
             :program program
             :box-program box-program
             :hud-program hud-program
             :atlas-tex atlas-tex
             :start-time (System/nanoTime)))
    ;; Register input callbacks
    (controls/register-callbacks! window)
    ;; Framebuffer resize callback
    (GLFW/glfwSetFramebufferSizeCallback
     window
     (reify GLFWFramebufferSizeCallbackI
       (invoke [_ _win w h]
         (swap! gl-state assoc :width w :height h))))
    ;; Get actual framebuffer size (HiDPI)
    (let [pw (int-array 1) ph (int-array 1)]
      (GLFW/glfwGetFramebufferSize window pw ph)
      (swap! gl-state assoc :width (aget pw 0) :height (aget ph 0)))
    ;; Store window handle, build initial VBOs
    (swap! scene/*state* assoc :window window :running? true)
    (println "[viz] Loading Torah data and building VBOs...")
    (rebuild-vbos!)
    (println (str "[viz] " (:point-count @gl-state) " points loaded"))
    ;; Show
    (GLFW/glfwShowWindow window)
    window))

(defn- render-frame! []
  (let [{:keys [program box-program vao point-count
                width height start-time atlas-tex hud-program]} @gl-state
        cam (:camera (scene/state))
        ortho? (:ortho? cam)
        zoom (get cam :zoom 1.0)
        ;; Data viewport: offset right of sidebar, with top/bottom margins
        margin-y 30
        data-x sidebar-width
        data-w (- width sidebar-width)
        data-h (- height (* 2 margin-y))
        time-s (/ (double (- (System/nanoTime) start-time)) 1e9)
        mvp-fb (compute-mvp cam data-w data-h)
        ;; Point size: in ortho, constant pixels that scale with zoom
        base-size (if ortho? (max 4.0 (min 80.0 (* 18.0 zoom))) 8.0)]
    ;; Clear (full viewport)
    (GL11/glViewport 0 0 width height)
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
    ;; Set data viewport (right of sidebar, with margins)
    (GL11/glViewport data-x margin-y data-w data-h)
    ;; Point cloud
    (when (and program vao (pos? point-count))
      (GL20/glUseProgram program)
      (GL20/glUniformMatrix4fv
       (GL20/glGetUniformLocation program "uMVP") false mvp-fb)
      (GL20/glUniform1f
       (GL20/glGetUniformLocation program "uTime") (float time-s))
      (GL20/glUniform1f
       (GL20/glGetUniformLocation program "uPointSize") (float base-size))
      (GL20/glUniform1i
       (GL20/glGetUniformLocation program "uOrtho") (if ortho? 1 0))
      ;; Bind letter atlas texture
      (when atlas-tex
        (GL13/glActiveTexture GL13/GL_TEXTURE0)
        (GL11/glBindTexture GL11/GL_TEXTURE_2D atlas-tex)
        (GL20/glUniform1i
         (GL20/glGetUniformLocation program "uAtlas") 0)
        (GL20/glUniform1f
         (GL20/glGetUniformLocation program "uAtlasCols") (float atlas-cols))
        (GL20/glUniform1f
         (GL20/glGetUniformLocation program "uAtlasRows") (float atlas-rows)))
      (GL30/glBindVertexArray vao)
      (GL11/glDrawArrays GL11/GL_POINTS 0 point-count)
      (GL30/glBindVertexArray 0))
    ;; Boxes
    (let [boxes (:boxes (scene/state))]
      (when (seq boxes)
        (doseq [box boxes]
          (render-box! box-program mvp-fb box))))
    ;; HUD overlay
    (render-hud! width height)
    ;; Swap
    (GLFW/glfwSwapBuffers (:window (scene/state)))))

(defn- render-loop!
  "Main render loop. Runs until window close or running? false."
  []
  (let [window (init-gl!)]
    (try
      (while (and (:running? (scene/state))
                  (not (GLFW/glfwWindowShouldClose window)))
        (GLFW/glfwPollEvents)
        (controls/process-held-keys! window)
        (update-camera-animation!)
        ;; Rebuild VBOs if state changed
        (when (scene/dirty?)
          (rebuild-vbos!))
        ;; Also rebuild if highlights changed (separate dirty check)
        (render-frame!))
      (catch Exception e
        (println "[viz] Render error:" (.getMessage e))
        (.printStackTrace e))
      (finally
        (println "[viz] Shutting down GL...")
        ;; Cleanup
        (when-let [vao (:vao @gl-state)]
          (GL30/glDeleteVertexArrays vao))
        (when-let [p (:program @gl-state)]
          (GL20/glDeleteProgram p))
        (when-let [p (:box-program @gl-state)]
          (GL20/glDeleteProgram p))
        (doseq [k [:pos-vbo :col-vbo :hi-vbo :let-vbo]]
          (when-let [b (get @gl-state k)]
            (GL15/glDeleteBuffers (int b))))
        (when-let [t (:atlas-tex @gl-state)]
          (GL11/glDeleteTextures (int t)))
        (GLFW/glfwDestroyWindow window)
        (GLFW/glfwTerminate)
        (reset! gl-state {:program nil :box-program nil :vao nil
                          :pos-vbo nil :col-vbo nil :hi-vbo nil :let-vbo nil
                          :atlas-tex nil
                          :point-count 0 :width 1280 :height 800
                          :start-time 0})
        (swap! scene/*state* assoc :running? false :window nil)
        (println "[viz] Done.")))))

;; ── Public API ─────────────────────────────────────────────

(defn start!
  "Launch the visualizer on a dedicated thread."
  []
  (if (:running? (scene/state))
    (println "[viz] Already running")
    (let [t (Thread. ^Runnable render-loop! "selah-viz")]
      (.start t)
      (println "[viz] Starting on thread" (.getName t)))))

(defn stop!
  "Signal the render loop to stop."
  []
  (swap! scene/*state* assoc :running? false))

(defn request-rebuild!
  "Force a VBO rebuild on the next frame."
  []
  (swap! scene/*state* assoc :dirty? true))
