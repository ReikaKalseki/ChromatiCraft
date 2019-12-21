varying vec4 vLightMapColor;

uniform sampler2D bgl_LightMapTexture;

uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;

uniform float radius;
uniform float thickness;
uniform float amplitude;

float getX(vec4 vert) {
	return vert.x+float(chunkX);
}

float getY(vec4 vert) {
	return vert.y+float(chunkY);
}

float getZ(vec4 vert) {
	return vert.z+float(chunkZ);
}

float getDistance(vec4 vert) {
	vec3 real = vec3(getX(vert), getY(vert), getZ(vert));
	vec3 diff = abs(real-focus);
	return sqrt(diff.x*diff.x+diff.y*diff.y+diff.z*diff.z);
}

float getDistanceXZ(vec4 vert) {
	vec3 real = vec3(getX(vert), getY(vert), getZ(vert));
	vec3 diff = abs(real-focus);
	return sqrt(diff.x*diff.x+diff.z*diff.z);
}

void main() {
    vec4 vert = gl_Vertex;
	float distv = getDistanceXZ(vert);
	float rdist = abs(distv-radius);
	float fr = max(0.0, 1.0-rdist/thickness);
	float f = intensity*fr;
	vert.y += amplitude*f;
	
	//vert.y += 2.5*sin(time*0.02+getX(vert)*0.175);
	
	gl_Position = gl_ModelViewProjectionMatrix * vert;
	
    texcoord = vec2(gl_MultiTexCoord0);
    vec2 lightMapCoords = vec2(gl_MultiTexCoord1);
	//lightMapCoords *= 15.0/16.0;
	//lightMapCoords += 1.0/32.0;
	lightMapCoords /= 256.0;
    vLightMapColor = min(texture2D(bgl_LightMapTexture, lightMapCoords)*2.0, vec4(1.0));
	gl_FrontColor = gl_Color;
}