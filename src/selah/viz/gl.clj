(ns selah.viz.gl
  "LWJGL3 OpenGL renderer for the Torah 4D space.

   Runs on a dedicated thread — the REPL stays responsive.
   Watches the scene atom for dirty state, rebuilds VBOs as needed."
  (:require [selah.viz.scene :as scene]
            [selah.viz.controls :as controls]
            [selah.space.coords :as c]
            [selah.space.project :as proj])
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
      ;; Grid mode: height-constrained (uniform spacing, data centered)
      ;; Read mode: width-constrained (data fills viewport width)
      (let [[px py] pan
            display (get (scene/state) :display :grid)
            [half-w half-h]
            (if (= display :read)
              (let [hw (/ 0.70 zoom)] [hw (/ hw aspect)])
              (let [hh (/ 0.55 zoom)] [(* hh aspect) hh]))]
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
                    "\u2191\u2193\u2190\u2192: slice" "G: grid/read"
                    "O: 3D" "P: palette" "R: reset" "Scroll: zoom"]]
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

;; ── Splash screen ────────────────────────────────────────

(defn- generate-splash-image
  "Render a centered splash screen from load-progress state."
  ^BufferedImage [w h {:keys [phase detail pct]}]
  (let [w (int w) h (int h)
        pct (double (or pct 0.0))
        ^String phase (str (or phase ""))
        ^String detail (str (or detail ""))
        img (BufferedImage. w h BufferedImage/TYPE_4BYTE_ABGR)
        ^Graphics2D g (.createGraphics img)]
    (.setRenderingHint g RenderingHints/KEY_TEXT_ANTIALIASING
                       RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
    (.setRenderingHint g RenderingHints/KEY_ANTIALIASING
                       RenderingHints/VALUE_ANTIALIAS_ON)
    ;; Background
    (.setColor g (Color. (int 13) (int 13) (int 20)))
    (.fillRect g 0 0 w h)
    (let [cx (quot w 2)
          cy (quot h 2)]
      ;; Title
      (.setFont g (Font. "SansSerif" Font/BOLD (int 36)))
      (.setColor g (Color. (int 200) (int 180) (int 255)))
      (let [title "Selah \u00B7 \u05E1\u05DC\u05D4"
            fm (.getFontMetrics g)
            tw (.stringWidth fm title)]
        (.drawString g ^String title (int (- cx (quot tw 2))) (int (- cy 70))))
      ;; Subtitle
      (.setFont g (Font. "SansSerif" Font/PLAIN (int 18)))
      (.setColor g (Color. (int 140) (int 140) (int 160)))
      (let [sub "Torah 4D Space Visualizer"
            fm (.getFontMetrics g)
            tw (.stringWidth fm sub)]
        (.drawString g ^String sub (int (- cx (quot tw 2))) (int (- cy 38))))
      ;; Progress bar
      (let [bar-w (int 300) bar-h (int 6)
            bar-x (int (- cx (quot bar-w 2)))
            bar-y (int (+ cy 2))]
        ;; Track
        (.setColor g (Color. (int 40) (int 40) (int 55)))
        (.fillRoundRect g bar-x bar-y bar-w bar-h (int 3) (int 3))
        ;; Fill
        (.setColor g (Color. (int 160) (int 140) (int 220)))
        (.fillRoundRect g bar-x bar-y (int (* bar-w (min pct 1.0))) bar-h (int 3) (int 3)))
      ;; Phase name
      (.setFont g (Font. "SansSerif" Font/PLAIN (int 16)))
      (.setColor g (Color. (int 180) (int 180) (int 200)))
      (let [fm (.getFontMetrics g)
            tw (.stringWidth fm phase)]
        (.drawString g phase (int (- cx (quot tw 2))) (int (+ cy 30))))
      ;; Detail (counts)
      (when (seq detail)
        (.setFont g (Font. "SansSerif" Font/PLAIN (int 14)))
        (.setColor g (Color. (int 110) (int 110) (int 130)))
        (let [fm (.getFontMetrics g)
              tw (.stringWidth fm detail)]
          (.drawString g ^String detail (int (- cx (quot tw 2))) (int (+ cy 52)))))
      ;; Percentage
      (.setFont g (Font. "SansSerif" Font/PLAIN (int 13)))
      (.setColor g (Color. (int 80) (int 80) (int 100)))
      (let [pct-str (str (int (* pct 100)) "%")
            fm (.getFontMetrics g)
            tw (.stringWidth fm pct-str)]
        (.drawString g ^String pct-str (int (- cx (quot tw 2))) (int (+ cy 70)))))
    (.dispose g)
    img))

(defonce ^:private splash-state (atom {:tex nil :vao nil :vbo nil}))

;; ── Loading progress ─────────────────────────────────────
;;
;; Loading runs on a background thread so the GL thread stays
;; responsive (no "unresponsive" popup). Progress is posted to
;; an atom that the splash loop polls each frame.

(defonce ^:private load-progress
  (atom {:phase ""        ;; current phase name
         :detail ""       ;; e.g. "150,000 / 304,850"
         :pct 0.0         ;; 0.0 to 1.0
         :done? false
         :error nil}))

(defn- post-phase! [pct phase]
  (swap! load-progress assoc :phase phase :detail "" :pct (double pct))
  (println (str "[viz] " phase)))

(defn- post-detail! [detail]
  (swap! load-progress assoc :detail (str detail)))

(defn- fmt-count [n] (format "%,d" (int n)))

(defn- render-splash!
  "Render a splash frame from load-progress and swap buffers."
  [window]
  (let [{:keys [width height]} @gl-state
        progress @load-progress]
    ;; Generate and upload splash texture
    (let [img (generate-splash-image width height progress)
          w (.getWidth img) h (.getHeight img)
          raster (.getRaster img)
          pixels (byte-array (* w h 4))
          _ (.getDataElements raster 0 0 w h pixels)
          rgba (byte-array (* w h 4))]
      ;; Convert ABGR → RGBA
      (dotimes [i (* w h)]
        (let [j (* i 4)]
          (aset rgba (+ j 0) (aget pixels (+ j 3)))
          (aset rgba (+ j 1) (aget pixels (+ j 2)))
          (aset rgba (+ j 2) (aget pixels (+ j 1)))
          (aset rgba (+ j 3) (aget pixels (+ j 0)))))
      (let [buf (MemoryUtil/memAlloc (alength rgba))
            tex (or (:tex @splash-state) (GL11/glGenTextures))]
        (try
          (.put buf rgba) (.flip buf)
          (GL11/glBindTexture GL11/GL_TEXTURE_2D tex)
          (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_LINEAR)
          (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_LINEAR)
          (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGBA
                             w h 0 GL11/GL_RGBA GL11/GL_UNSIGNED_BYTE ^ByteBuffer buf)
          (GL11/glBindTexture GL11/GL_TEXTURE_2D 0)
          (swap! splash-state assoc :tex tex)
          (finally
            (MemoryUtil/memFree buf)))))
    ;; Ensure full-screen quad
    (let [verts (float-array [-1 -1  0 1
                               1 -1  1 1
                              -1  1  0 0
                               1  1  1 0])
          vbo (or (:vbo @splash-state) (upload-float-buffer verts))
          vao (or (:vao @splash-state) (GL30/glGenVertexArrays))]
      (upload-float-buffer vbo verts)
      (GL30/glBindVertexArray vao)
      (GL15/glBindBuffer GL15/GL_ARRAY_BUFFER vbo)
      (GL20/glVertexAttribPointer 0 2 GL11/GL_FLOAT false (* 4 4) 0)
      (GL20/glEnableVertexAttribArray 0)
      (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false (* 4 4) (* 2 4))
      (GL20/glEnableVertexAttribArray 1)
      (GL30/glBindVertexArray 0)
      (swap! splash-state assoc :vao vao :vbo vbo))
    ;; Render
    (GL11/glViewport 0 0 width height)
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
    (GL11/glDisable GL11/GL_DEPTH_TEST)
    (when-let [program (:hud-program @gl-state)]
      (when-let [tex (:tex @splash-state)]
        (when-let [vao (:vao @splash-state)]
          (GL20/glUseProgram program)
          (GL13/glActiveTexture GL13/GL_TEXTURE0)
          (GL11/glBindTexture GL11/GL_TEXTURE_2D tex)
          (GL20/glUniform1i (GL20/glGetUniformLocation program "uHudTex") 0)
          (GL30/glBindVertexArray vao)
          (GL11/glDrawArrays GL11/GL_TRIANGLE_STRIP 0 4)
          (GL30/glBindVertexArray 0))))
    (GL11/glEnable GL11/GL_DEPTH_TEST)
    (GLFW/glfwSwapBuffers window)
    (GLFW/glfwPollEvents)))

(defn- cleanup-splash! []
  (when-let [tex (:tex @splash-state)]
    (GL11/glDeleteTextures (int tex)))
  (when-let [vao (:vao @splash-state)]
    (GL30/glDeleteVertexArrays vao))
  (when-let [vbo (:vbo @splash-state)]
    (GL15/glDeleteBuffers (int vbo)))
  (reset! splash-state {:tex nil :vao nil :vbo nil}))

(defn- load-data!
  "Heavy loading work — runs on background thread.
   Posts fine-grained progress updates via load-progress atom."
  []
  (try
    ;; Phase 1: Torah text (0% → 15%)
    (post-phase! 0.0 "Loading Torah text")
    (c/space)
    (post-phase! 0.15 "Torah loaded")

    ;; Phase 2: Coordinates (15% → 50%)
    (post-phase! 0.15 "Computing coordinates")
    (let [s (c/space)
          n (int c/total-letters)
          coords (int-array (* n 4))
          report-interval (int 50000)]
      (dotimes [i n]
        (let [c4 (c/idx->coord i)
              j (* i 4)]
          (aset coords j       (int (aget c4 0)))
          (aset coords (+ j 1) (int (aget c4 1)))
          (aset coords (+ j 2) (int (aget c4 2)))
          (aset coords (+ j 3) (int (aget c4 3))))
        (when (zero? (rem i report-interval))
          (let [frac (/ (double i) (double n))]
            (swap! load-progress assoc
                   :detail (str (fmt-count i) " / " (fmt-count n))
                   :pct (+ 0.15 (* 0.35 frac))))))
      (swap! load-progress assoc :pct 0.50)

      ;; Phase 3: Palettes (50% → 85%)
      (let [all-pos (int-array n)
            _ (dotimes [i n] (aset all-pos i i))]

        (post-phase! 0.50 "Letter palette")
        (let [pal-letter (proj/color-by-letter s all-pos)]
          (swap! load-progress assoc :pct 0.58)

          (post-phase! 0.58 "Gematria palette")
          (let [pal-gematria (proj/color-by-gematria s all-pos)]
            (swap! load-progress assoc :pct 0.66)

            (post-phase! 0.66 "Book palette")
            (let [pal-book (proj/color-by-book s all-pos)]
              (swap! load-progress assoc :pct 0.74)

              (post-phase! 0.74 "Day palette")
              (let [pal-day (proj/color-by-day all-pos)]
                (swap! load-progress assoc :pct 0.82)

                ;; Phase 4: Letter indices + store (85% → 100%)
                (post-phase! 0.82 "Letter indices")
                (let [stream ^bytes (:stream s)
                      let-idx (float-array n)]
                  (dotimes [i n]
                    (aset let-idx i (float (aget stream i))))

                  (post-phase! 0.92 "Storing precomputed data")
                  (scene/set-precomputed!
                   {:coords   coords
                    :palettes {:letter   pal-letter
                               :gematria pal-gematria
                               :book     pal-book
                               :day      pal-day}
                    :letters  let-idx})

                  (post-phase! 1.0 "Ready")
                  (swap! load-progress assoc :done? true))))))))
    (catch Throwable e
      (println "[viz] Load error:" (.getMessage e))
      (.printStackTrace e)
      (swap! load-progress assoc :error e))))

;; ── Init / Render / Shutdown ──────────────────────────────

(defn- init-gl!
  "Create window and shaders (fast). Heavy loading runs on a
   background thread while the GL thread renders the splash."
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
    ;; Show window immediately
    (swap! scene/*state* assoc :window window :running? true)
    (GLFW/glfwShowWindow window)
    ;; Start loader on background thread
    (reset! load-progress {:phase "Initializing..." :detail "" :pct 0.0
                           :done? false :error nil})
    (let [loader (Thread. ^Runnable load-data! "selah-loader")]
      (.start loader)
      ;; Splash loop — render progress while loading, keep window responsive
      (loop []
        (GLFW/glfwPollEvents)
        (let [{:keys [done? error]} @load-progress]
          (render-splash! window)
          (when (and (not done?) (nil? error)
                     (not (GLFW/glfwWindowShouldClose window)))
            (recur))))
      ;; Wait for loader to finish
      (.join loader 1000))
    ;; Check for errors
    (when-let [e (:error @load-progress)]
      (throw (RuntimeException. "Loading failed" e)))
    ;; Build VBOs (must be on GL thread)
    (println "[viz] Building VBOs...")
    (rebuild-vbos!)
    (println (str "[viz] " (:point-count @gl-state) " points loaded"))
    (cleanup-splash!)
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
        display (get (scene/state) :display :grid)
        base-size (if ortho?
                    (if (= display :read)
                      (max 6.0 (min 100.0 (* 26.0 zoom)))   ;; read: bigger letters
                      (max 4.0 (min 80.0 (* 16.0 zoom))))   ;; grid: uniform dots
                    8.0)]
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

;; ── Mouse picking ─────────────────────────────────────────

(defn- process-pick!
  "Process a pending pick request. Projects all visible points to
   screen space via manual MVP multiply, finds nearest to click.
   Works in both ortho and perspective modes."
  []
  (when-let [[mx my] (:pick-request (scene/state))]
    (scene/clear-pick-request!)
    (let [{:keys [width height]} @gl-state
          cam (:camera (scene/state))
          ortho? (:ortho? cam)
          margin-y 30
          data-x sidebar-width
          data-w (- width sidebar-width)
          data-h (- height (* 2 margin-y))
          vx (- mx data-x)
          vy (- my margin-y)]
      (when (and (>= vx 0) (< vx data-w)
                 (>= vy 0) (< vy data-h))
        (let [m ^floats (compute-mvp cam data-w data-h)
              {:keys [positions indices count]} (scene/position-data)
              ;; Mouse in NDC (data viewport)
              ndc-x (double (- (* 2.0 (/ (double vx) (double (max data-w 1)))) 1.0))
              ndc-y (double (- 1.0 (* 2.0 (/ (double vy) (double (max data-h 1))))))
              ;; Pick radius in NDC² — generous enough for perspective
              ;; Ortho: tight (points are evenly spaced)
              ;; Perspective: looser (points vary in screen size with depth)
              max-dist (if ortho? 0.005 0.02)
              best-dist (volatile! Double/MAX_VALUE)
              best-idx (volatile! -1)]
          ;; Manual MVP multiply — no object allocation in hot loop
          ;; Column-major: m[col*4+row]
          (dotimes [i count]
            (let [j (* 3 i)
                  px (double (aget ^floats positions j))
                  py (double (aget ^floats positions (+ j 1)))
                  pz (double (aget ^floats positions (+ j 2)))
                  ;; clip = M * [px py pz 1]
                  cw (+ (* (aget m 3)  px) (* (aget m 7)  py)
                        (* (aget m 11) pz)    (aget m 15))]
              (when (> cw 0.001)
                (let [cx (+ (* (aget m 0)  px) (* (aget m 4)  py)
                            (* (aget m 8)  pz)    (aget m 12))
                      cy (+ (* (aget m 1)  px) (* (aget m 5)  py)
                            (* (aget m 9)  pz)    (aget m 13))
                      sx (/ cx cw)
                      sy (/ cy cw)
                      dx (- sx ndc-x)
                      dy (- sy ndc-y)
                      dist (+ (* dx dx) (* dy dy))]
                  (when (< dist @best-dist)
                    (vreset! best-dist dist)
                    (vreset! best-idx i))))))
          (when (and (>= @best-idx 0) (< @best-dist max-dist))
            (scene/select! (aget ^ints indices @best-idx))))))))

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
        ;; Process mouse pick requests
        (process-pick!)
        ;; Render
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
