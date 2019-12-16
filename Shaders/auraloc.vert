void main() {
    vec4 vert = gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * vert;
    vec2 texcoord = vec2(gl_MultiTexCoord0);
}