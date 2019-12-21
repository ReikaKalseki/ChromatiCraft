varying vec4 vLightMapColor;

uniform sampler2D bgl_LightMapTexture;

uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;

uniform float waveRadius;
uniform float waveThickness;
uniform float waveAmplitude;

uniform float curlMovementXZ;
uniform float curlMovementY;

uniform float stretchFactor;

float getX(vec4 vert) {
	return vert.x+float(chunkX);
}

float getY(vec4 vert) {
	return vert.y+float(chunkY);
}

float getZ(vec4 vert) {
	return vert.z+float(chunkZ);
}

vec3 getReal(vec4 vert) {
	return vec3(getX(vert), getY(vert), getZ(vert));
}

float getDistance(vec3 diff) {
	diff = abs(diff);
	return sqrt(diff.x*diff.x+diff.y*diff.y+diff.z*diff.z);
}

float getDistanceXZ(vec3 diff) {
	diff = abs(diff);
	return sqrt(diff.x*diff.x+diff.z*diff.z);
}

vec3 getRelativeCoord(vec3 real) {
	return real-vec3(float(chunkX), float(chunkY), float(chunkZ));
}

void main() {
    vec4 vert = gl_Vertex;
	
	//vert.y += 2.5*sin(time*0.02+getX(vert)*0.175);
	
	vec3 real = getReal(vert);
	vec3 diff = real-focus;
	float distxz = getDistanceXZ(diff);
	float rdist = abs(distxz-waveRadius);
	float fr = max(0.0, 1.0-rdist/waveThickness);
	float f = intensity*fr;
	real.y += waveAmplitude*f;
	real.y += curlMovementY*distxz*intensity;
	real.x = mix(real.x, focus.x, curlMovementXZ*intensity);
	real.z = mix(real.z, focus.z, curlMovementXZ*intensity);
	
	diff = real-focus;
	diff *= 1.0+max(0.0, stretchFactor-1.0);
	vec3 repl = focus+diff;
	real.x = repl.x;
	real.z = repl.z;
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(getRelativeCoord(real.xyz), vert.w);
	
    texcoord = vec2(gl_MultiTexCoord0);
    vec2 lightMapCoords = vec2(gl_MultiTexCoord1);
	//lightMapCoords *= 15.0/16.0;
	//lightMapCoords += 1.0/32.0;
	lightMapCoords /= 256.0;
    vLightMapColor = min(texture2D(bgl_LightMapTexture, lightMapCoords)*2.0, vec4(1.0));
	gl_FrontColor = gl_Color;
}