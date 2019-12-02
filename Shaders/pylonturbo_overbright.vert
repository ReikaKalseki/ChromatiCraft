varying vec2 texcoord;

uniform int time;
uniform int screenWidth;
uniform int screenHeight;
uniform mat4 modelview;
uniform mat4 projection;
uniform vec3 focus;

uniform float intensity;

void main() {
    vec4 vert = gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * vert;
    texcoord = vec2(gl_MultiTexCoord0);
}