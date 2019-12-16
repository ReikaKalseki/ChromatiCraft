varying vec2 vLightMapCoords;
varying vec4 vLightMapColor;

uniform sampler2D bgl_LightMapTexture;

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
	//vert.y += 2.5*sin(time*0.02+getX(vert)*0.175);
	gl_Position = gl_ModelViewProjectionMatrix * vert;
    texcoord = vec2(gl_MultiTexCoord0);
    vec2 lightMapCoords = vec2(gl_MultiTexCoord1);
    vLightMapColor = texture2D(bgl_LightMapTexture, vec2(0.5+lightMapCoords.x, 0.5+lightMapCoords.y));
	gl_FrontColor = gl_Color;
}