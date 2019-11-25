varying vec2 texcoord;

uniform sampler2D bgl_RenderedTexture;

uniform int time;
uniform int screenWidth;
uniform int screenHeight;
uniform mat4 modelview;
uniform mat4 projection;
uniform float screenX;
uniform float screenY;

void main() {
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}