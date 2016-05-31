precision mediump float;
varying vec4 v_Color;

uniform sampler2D u_Texture;    // Texture
varying vec2 v_TexCoordinate;   // Coordinate (from Vertex Shader)

void main() {
    gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);
}
