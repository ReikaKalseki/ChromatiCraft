uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;

float getX(vec4 vert) {
	return vert.x+float(chunkX);
}

float getY(vec4 vert) {
	return vert.y+float(chunkY);
}

float getZ(vec4 vert) {
	return vert.z+float(chunkZ);
}

void main() {
    vec4 vert = gl_Vertex;
	vert.y += 2.5*sin(time*0.1+getX(vert)*0.175);
	gl_Position = gl_ModelViewProjectionMatrix * vert;
    texcoord = vec2(gl_MultiTexCoord0);
}